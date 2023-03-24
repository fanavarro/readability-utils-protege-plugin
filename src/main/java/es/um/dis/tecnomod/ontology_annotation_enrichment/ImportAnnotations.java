package es.um.dis.tecnomod.ontology_annotation_enrichment;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import org.protege.editor.owl.ui.action.ProtegeOWLAction;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;

public class ImportAnnotations extends ProtegeOWLAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7958599797242235008L;

	@Override
	public void initialise() throws Exception {
		
	}

	@Override
	public void dispose() throws Exception {
		
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		OWLOntology owlOntology = this.getOWLWorkspace().getOWLModelManager().getActiveOntology();
		OWLEntity owlEntity = this.getOWLWorkspace().getOWLSelectionModel().getSelectedEntity();		
		String message = String.format("The entity %s is going to be enriched by extracting the annotation assertion axioms from its IRI. Continue?", owlEntity.getIRI().toQuotedString());
		int result = JOptionPane.showConfirmDialog(getOWLWorkspace(), message, "Import annotations",
	               JOptionPane.YES_NO_OPTION,
	               JOptionPane.QUESTION_MESSAGE);
		if(result == JOptionPane.YES_OPTION){
			AnnotationEnricher annotationEnricher = new AnnotationEnricher(owlOntology);
			annotationEnricher.enrichEntity(owlEntity);
        }
	}

}
