package es.um.dis.tecnomod.iri2label;

import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JOptionPane;

import org.openrdf.model.vocabulary.RDFS;
import org.protege.editor.owl.ui.action.ProtegeOWLAction;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.search.EntitySearcher;

public class CreateLabelFromIRIBulk extends ProtegeOWLAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8920669375639346359L;

	private StringUtils stringUtils;
	private OntologyUtils ontologyUtils;
	private OWLAnnotationProperty rdfsLabel;

	public void initialise() throws Exception {
		stringUtils = new StringUtils();
		ontologyUtils = new OntologyUtils();
	}

	public void dispose() throws Exception {
	}

	public void actionPerformed(ActionEvent event) {
		OWLOntology owlOntology = this.getOWLWorkspace().getOWLModelManager().getActiveOntology();
		this.rdfsLabel = this.getOWLWorkspace().getOWLModelManager().getOWLDataFactory().getOWLAnnotationProperty(IRI.create(RDFS.LABEL.stringValue()));
		String message = "This action will include an rdfs:label to all ontology entities by processing the last fragment of their IRIs. Entities already labelled with rdfs:label will be skipped. Continue?";
		int result = JOptionPane.showConfirmDialog(getOWLWorkspace(), message, "Create label (bulk)",
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		if (result == JOptionPane.YES_OPTION) {
			this.addLabelsBulk(owlOntology);
		}
	}
	
	private void addLabelsBulk(OWLOntology owlOntology) {
		String lang = null;
		for(OWLEntity owlEntity : getEntitiesWithoutLabel(owlOntology)) {
			String label = this.stringUtils.iri2string(owlEntity.getIRI());
			this.ontologyUtils.addRDFSLabel(owlOntology, owlEntity.getIRI(), label, lang);
		}
	}

	private Set<OWLEntity> getEntitiesWithoutLabel(OWLOntology owlOntology) {
		Set<OWLEntity> entitiesWithoutLabels = new HashSet<>();
		/* Check classes */
		for (OWLEntity entity : owlOntology.getClassesInSignature()) {
			if (!this.hasLabel(entity, owlOntology)) {
				entitiesWithoutLabels.add(entity);
			}
		}
		
		/* Check object properties */
		for (OWLEntity entity : owlOntology.getObjectPropertiesInSignature()) {
			if (!this.hasLabel(entity, owlOntology)) {
				entitiesWithoutLabels.add(entity);
			}
		}
		
		/* Check data properties */
		for (OWLEntity entity : owlOntology.getDataPropertiesInSignature()) {
			if (!this.hasLabel(entity, owlOntology)) {
				entitiesWithoutLabels.add(entity);
			}
		}
		
		/* Check annotation properties */
		for (OWLEntity entity : owlOntology.getAnnotationPropertiesInSignature()) {
			if (!this.hasLabel(entity, owlOntology)) {
				entitiesWithoutLabels.add(entity);
			}
		}
		
		/* Check individuals */
		for (OWLEntity entity : owlOntology.getIndividualsInSignature()) {
			if (!this.hasLabel(entity, owlOntology)) {
				entitiesWithoutLabels.add(entity);
			}
		}
		return entitiesWithoutLabels;
	}
	private boolean hasLabel(OWLEntity entity, OWLOntology owlOntology) {
		Collection<OWLAnnotation> labels = EntitySearcher.getAnnotations(entity, owlOntology, this.rdfsLabel);
		return !labels.isEmpty();
	}
}
