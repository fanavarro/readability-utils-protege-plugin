package es.um.dis.tecnomod.ontology_annotation_enrichment;

import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import es.um.dis.tecnomod.ontology_annotation_enrichment.components.ImportAnnotationProgressBean;
import es.um.dis.tecnomod.ontology_annotation_enrichment.components.ImportAnnotationProgressEvent;


public class AnnotationEnricher {
	private final static Logger LOGGER = Logger.getLogger(AnnotationEnricher.class.getName());
	private static final float MAX_PROGRESS_WITHOUT_COMPLETE = 97;
	
	private List<EventListener> listeners;
	private float progress;
	private float progressPerEntity;
	private ImportAnnotationProgressBean lastProgressNotification;
	
	private OWLOntologyManager ontologyManager;
	private OWLOntology ontology;

	public AnnotationEnricher(OWLOntology ontology) {
		this.ontology = ontology;
		this.ontologyManager = ontology.getOWLOntologyManager();
		this.listeners = new ArrayList<EventListener>();
		this.progress = 0;
		this.progressPerEntity = this.getProgressPerEntity();
		this.lastProgressNotification = null;
	}
	
	public AnnotationEnricher(InputStream inputStream) throws OWLOntologyCreationException {
		this.ontologyManager = OWLManager.createOWLOntologyManager();
		this.ontology = this.ontologyManager.loadOntologyFromOntologyDocument(inputStream);
		this.listeners = new ArrayList<EventListener>();
		this.progress = 0;
		this.progressPerEntity = this.getProgressPerEntity();
		this.lastProgressNotification = null;
	}
	
	public AnnotationEnricher(File ontologyFile) throws OWLOntologyCreationException, FileNotFoundException {
		this(new FileInputStream(ontologyFile));
	}
	
	public AnnotationEnricher(IRI ontologyIRI) throws OWLOntologyCreationException {
		this.ontologyManager = OWLManager.createOWLOntologyManager();
		this.ontology = this.ontologyManager.loadOntologyFromOntologyDocument(ontologyIRI);
		this.listeners = new ArrayList<EventListener>();
		this.progress = 0;
		this.progressPerEntity = this.getProgressPerEntity();
		this.lastProgressNotification = null;
	}
	private float getProgressPerEntity() {
		int totalEntities = 0;
		totalEntities += ontology.getClassesInSignature().size();
		totalEntities += ontology.getObjectPropertiesInSignature().size();
		totalEntities += ontology.getDataPropertiesInSignature().size();
		totalEntities += ontology.getAnnotationPropertiesInSignature().size();
		totalEntities += ontology.getIndividualsInSignature().size();
		
		return (100.0f/(float)totalEntities);
	}
	
	public void addListener(EventListener listener){
		this.listeners.add(listener);
	}
	public void removeListener(EventListener listener){
		this.listeners.remove(listener);
	}
	public void cleanListeners(){
		this.listeners.clear();
	}
	
	public void notifyListeners(int progress, String message) {
		ImportAnnotationProgressBean eventContent = new ImportAnnotationProgressBean(progress, message);
		ImportAnnotationProgressEvent event = new ImportAnnotationProgressEvent(this, this.lastProgressNotification, eventContent);
		for (EventListener listener : this.listeners) {
			if (listener instanceof PropertyChangeListener) {
				((PropertyChangeListener)listener).propertyChange(event);
			}
		}
		this.lastProgressNotification = eventContent;
	}
	
	private void addAxiomsThreadSafe(OWLOntologyManager manager, OWLOntology ontology, Set<OWLAxiom> axioms) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				manager.addAxioms(ontology, axioms);
			}
		});
	}
	public void enrichOntology() {
		
		this.notifyListeners(Math.round(this.progress), "Enriching classes");
		Set<OWLAxiom> classAxioms = this.getAnnotationAssertionAxiomsForOWLClasses();
		this.addAxiomsThreadSafe(ontologyManager, ontology, classAxioms);
		
		this.notifyListeners(Math.round(this.progress), "Enriching object properties");
		Set<OWLAxiom> objectPropertyAxioms = this.getAnnotationAssertionAxiomsForOWLObjectProperties();
		this.addAxiomsThreadSafe(ontologyManager, ontology, objectPropertyAxioms);
		
		this.notifyListeners(Math.round(this.progress), "Enriching data properties");
		Set<OWLAxiom> dataPropertyAxioms = this.getAnnotationAssertionAxiomsForOWLDataProperties();
		this.addAxiomsThreadSafe(ontologyManager, ontology, dataPropertyAxioms);
		
		this.notifyListeners(Math.round(this.progress), "Enriching individuals");
		Set<OWLAxiom> individualAxioms = this.getAnnotationAssertionAxiomsForOWLIndividuals();
		this.addAxiomsThreadSafe(ontologyManager, ontology, individualAxioms);
		
		this.notifyListeners(Math.round(this.progress), "Enriching annotation properties");
		Set<OWLAxiom> annotationPropertyAxioms = this.getAnnotationAssertionAxiomsForOWLAnnotationProperties();
		this.addAxiomsThreadSafe(ontologyManager, ontology, annotationPropertyAxioms);
		
		int totalAxioms = classAxioms.size() + objectPropertyAxioms.size() + dataPropertyAxioms.size() + individualAxioms.size() + annotationPropertyAxioms.size();
		this.progress = 100;
		this.notifyListeners(Math.round(this.progress), "Done");
		this.notifyListeners(Math.round(this.progress), String.format("%d new axioms added to the ontology", totalAxioms));
		this.notifyListeners(Math.round(this.progress), String.format("%d new class axioms added to the ontology", classAxioms.size()));
		this.notifyListeners(Math.round(this.progress), String.format("%d new object property axioms added to the ontology", objectPropertyAxioms.size()));
		this.notifyListeners(Math.round(this.progress), String.format("%d new data property axioms added to the ontology", dataPropertyAxioms.size()));
		this.notifyListeners(Math.round(this.progress), String.format("%d new annotation property axioms added to the ontology", annotationPropertyAxioms.size()));
		this.notifyListeners(Math.round(this.progress), String.format("%d new individual axioms added to the ontology", individualAxioms.size()));
	}
	
	public void enrichEntity(OWLEntity entity) {
		Set<OWLAxiom> annotationAssertionAxioms = this.getAnnotationAssertionAxiomsForOWLEntity(entity);
		this.addAxiomsThreadSafe(ontologyManager, ontology, annotationAssertionAxioms);
	}
	
	public Set<OWLAxiom> getAnnotationAssertionAxiomsForOWLEntity(OWLEntity entity) {
		return this.getAnnotationAssertionAxiomsForOWLEntity(entity, ConcurrentHashMap.newKeySet());
	}
	
	private Set<OWLAxiom> getAnnotationAssertionAxiomsForOWLEntity(OWLEntity entity, Set<OWLAnnotationProperty> visitedAnnotationProperties) {
		
		
		Set<OWLAxiom> axiomsToAdd = ConcurrentHashMap.newKeySet();
		OWLOntologyManager externalOntologyManager = OWLManager.createOWLOntologyManager();
		try {
			OWLOntology externalOntologyPortion = externalOntologyManager.loadOntology(entity.getIRI());
			externalOntologyPortion.getAnnotationAssertionAxioms(entity.getIRI()).stream().forEach(annotationAssertionAxiom -> {
				if (!this.ontology.containsAxiom(annotationAssertionAxiom)) {
					axiomsToAdd.add(annotationAssertionAxiom);
					/* If the annotation assertion axiom is using a new annotation property that do not exist in the ontology previously,
					 * include its annotations as well.*/
					OWLAnnotationProperty annotationPropertyUsed = annotationAssertionAxiom.getProperty();
					if (!visitedAnnotationProperties.contains(annotationPropertyUsed) && !this.ontology.containsAnnotationPropertyInSignature(annotationPropertyUsed.getIRI())) {
						visitedAnnotationProperties.add(annotationPropertyUsed);
						axiomsToAdd.addAll(this.getAnnotationAssertionAxiomsForOWLEntity(annotationPropertyUsed, visitedAnnotationProperties));
					}
				}
			});
			
			
		} catch (OWLOntologyCreationException ontologyCreationException) {
			LOGGER.log(Level.WARNING, String.format("Cannot obtain data for %s", entity.getIRI().toQuotedString()));
			this.notifyListeners(Math.round(this.progress), String.format("Cannot obtain data for %s", entity.getIRI().toQuotedString()));
		} catch(Exception e) {
			LOGGER.log(Level.SEVERE, "Not controlled exception", e);
			this.notifyListeners(Math.round(this.progress), String.format("Cannot obtain data for %s", entity.getIRI().toQuotedString()));
		}
		
		return axiomsToAdd;
	}
	
	public Set<OWLAxiom> getAnnotationAssertionAxiomsForOWLEntities(Stream <? extends OWLEntity> entities) {
		Set<OWLAxiom> axiomsToAdd = ConcurrentHashMap.newKeySet();
		Set<OWLAnnotationProperty> visitedAnnotationProperties = ConcurrentHashMap.newKeySet();
		entities.forEach(entity -> {
			this.notifyListeners(Math.round(this.progress), String.format("Enriching entity %s", entity.getIRI().toQuotedString()));
			
			Set<OWLAxiom> currentEntityAxioms = this.getAnnotationAssertionAxiomsForOWLEntity(entity, visitedAnnotationProperties);
			if (currentEntityAxioms != null && !currentEntityAxioms.isEmpty()) {
				axiomsToAdd.addAll(currentEntityAxioms);
			}
			Math.min(this.progress + this.progressPerEntity, MAX_PROGRESS_WITHOUT_COMPLETE);
			this.progress = Math.min(this.progress + this.progressPerEntity, MAX_PROGRESS_WITHOUT_COMPLETE);
			this.notifyListeners(Math.round(this.progress), null);
		});
		return axiomsToAdd;
	}
	
	public Set<OWLAxiom> getAnnotationAssertionAxiomsForOWLClasses(){
		return this.getAnnotationAssertionAxiomsForOWLEntities(this.ontology.getClassesInSignature().stream());
	}
	public Set<OWLAxiom> getAnnotationAssertionAxiomsForOWLObjectProperties(){
		return this.getAnnotationAssertionAxiomsForOWLEntities(this.ontology.getObjectPropertiesInSignature().stream());
	}
	
	public Set<OWLAxiom> getAnnotationAssertionAxiomsForOWLDataProperties(){
		return this.getAnnotationAssertionAxiomsForOWLEntities(this.ontology.getDataPropertiesInSignature().stream());
	}
	
	public Set<OWLAxiom> getAnnotationAssertionAxiomsForOWLAnnotationProperties(){
		return this.getAnnotationAssertionAxiomsForOWLEntities(this.ontology.getAnnotationPropertiesInSignature().stream());
	}
	
	public Set<OWLAxiom> getAnnotationAssertionAxiomsForOWLIndividuals(){
		return this.getAnnotationAssertionAxiomsForOWLEntities(this.ontology.getIndividualsInSignature().stream());
	}

	public OWLOntology getOntology() {
		return ontology;
	}

	public void setOntology(OWLOntology ontology) {
		this.ontology = ontology;
	}
}
