package es.um.dis.tecnomod.ontology_annotation_enrichment;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import org.protege.editor.owl.ui.action.ProtegeOWLAction;
import org.semanticweb.owlapi.model.OWLOntology;

public class ImportAnnotationsBulk extends ProtegeOWLAction {

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
		String message = "All the entities in the ontology are going to be enriched by extracting the annotation assertion axioms from their IRI. This task is performed entity by entity and could be time-consuming. Continue?";
		int result = JOptionPane.showConfirmDialog(getOWLWorkspace(), message, "Import annotations",
	               JOptionPane.YES_NO_OPTION,
	               JOptionPane.QUESTION_MESSAGE);
		if(result == JOptionPane.YES_OPTION){
			AnnotationEnricher annotationEnricher = new AnnotationEnricher(owlOntology);
			annotationEnricher.enrichOntology();
        }
	}

}
