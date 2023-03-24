package es.um.dis.tecnomod.ontology_annotation_enrichment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
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


public class AnnotationEnricher {
	private final static Logger LOGGER = Logger.getLogger(AnnotationEnricher.class.getName());
	
	private OWLOntologyManager ontologyManager;
	private OWLOntology ontology;

	public AnnotationEnricher(OWLOntology ontology) {
		this.ontology = ontology;
		this.ontologyManager = ontology.getOWLOntologyManager();
	}
	
	public AnnotationEnricher(InputStream inputStream) throws OWLOntologyCreationException {
		this.ontologyManager = OWLManager.createOWLOntologyManager();
		this.ontology = this.ontologyManager.loadOntologyFromOntologyDocument(inputStream);
	}
	
	public AnnotationEnricher(File ontologyFile) throws OWLOntologyCreationException, FileNotFoundException {
		this(new FileInputStream(ontologyFile));
	}
	
	public AnnotationEnricher(IRI ontologyIRI) throws OWLOntologyCreationException {
		this.ontologyManager = OWLManager.createOWLOntologyManager();
		this.ontology = this.ontologyManager.loadOntologyFromOntologyDocument(ontologyIRI);
	}
	
	public void enrichOntology() {
		LOGGER.log(Level.INFO, String.format("Enriching %s", this.ontology.getOntologyID().toString()));
		
		Set<OWLAxiom> classAxioms = this.getAnnotationAssertionAxiomsForOWLClasses();
		this.ontologyManager.addAxioms(this.ontology, classAxioms);
		
		Set<OWLAxiom> objectPropertyAxioms = this.getAnnotationAssertionAxiomsForOWLObjectProperties();
		this.ontologyManager.addAxioms(this.ontology, objectPropertyAxioms);
		
		Set<OWLAxiom> dataPropertyAxioms = this.getAnnotationAssertionAxiomsForOWLDataProperties();
		this.ontologyManager.addAxioms(this.ontology, dataPropertyAxioms);
		
		Set<OWLAxiom> individualAxioms = this.getAnnotationAssertionAxiomsForOWLIndividuals();
		this.ontologyManager.addAxioms(this.ontology, individualAxioms);
		
		Set<OWLAxiom> annotationPropertyAxioms = this.getAnnotationAssertionAxiomsForOWLAnnotationProperties();
		this.ontologyManager.addAxioms(this.ontology, annotationPropertyAxioms);
		
		int totalAxioms = classAxioms.size() + objectPropertyAxioms.size() + dataPropertyAxioms.size() + individualAxioms.size() + annotationPropertyAxioms.size();
		
		LOGGER.log(Level.INFO, String.format("%d new axioms added to the ontology", totalAxioms));
		LOGGER.log(Level.INFO, String.format("%d new class axioms added to the ontology", classAxioms.size()));
		LOGGER.log(Level.INFO, String.format("%d new object property axioms added to the ontology", objectPropertyAxioms.size()));
		LOGGER.log(Level.INFO, String.format("%d new data property axioms added to the ontology", dataPropertyAxioms.size()));
		LOGGER.log(Level.INFO, String.format("%d new annotation property axioms added to the ontology", annotationPropertyAxioms.size()));
		LOGGER.log(Level.INFO, String.format("%d new individual axioms added to the ontology", individualAxioms.size()));
	}
	
	public void enrichEntity(OWLEntity entity) {
		Set<OWLAxiom> annotationAssertionAxioms = this.getAnnotationAssertionAxiomsForOWLEntity(entity);
		this.ontologyManager.addAxioms(this.ontology, annotationAssertionAxioms);
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
		} catch(Exception e) {
			LOGGER.log(Level.SEVERE, "Not controlled exception", e);
		}
		return axiomsToAdd;
	}
	
	public Set<OWLAxiom> getAnnotationAssertionAxiomsForOWLEntities(Stream <? extends OWLEntity> entities) {
		Set<OWLAxiom> axiomsToAdd = ConcurrentHashMap.newKeySet();
		Set<OWLAnnotationProperty> visitedAnnotationProperties = ConcurrentHashMap.newKeySet();
		entities.forEach(entity -> {
			Set<OWLAxiom> currentEntityAxioms = this.getAnnotationAssertionAxiomsForOWLEntity(entity, visitedAnnotationProperties);
			if (currentEntityAxioms != null && !currentEntityAxioms.isEmpty()) {
				axiomsToAdd.addAll(currentEntityAxioms);
			}
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
