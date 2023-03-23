package es.um.dis.tecnomod.iri2label;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import org.protege.editor.owl.ui.action.ProtegeOWLAction;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;

public class CreateLabelFromIRI extends ProtegeOWLAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1339920281237310690L;
	
	private StringUtils stringUtils;
	private OntologyUtils ontologyUtils;

	public void initialise() throws Exception {
		stringUtils = new StringUtils();
		ontologyUtils = new OntologyUtils();
	}

	public void dispose() throws Exception {
	}

	public void actionPerformedBak(ActionEvent event) {
		OWLOntology owlOntology = this.getOWLWorkspace().getOWLModelManager().getActiveOntology();
		OWLEntity owlEntity = this.getOWLWorkspace().getOWLSelectionModel().getSelectedEntity();
		
		String label = stringUtils.iri2string(owlEntity.getIRI());
		String lang = null;
		
		
		
		String message = String.format("The entity %s is going to be labelled as %s.", owlEntity.getIRI().toQuotedString(), label);

		int result = JOptionPane.showConfirmDialog(getOWLWorkspace(), message, "Create label",
	               JOptionPane.YES_NO_OPTION,
	               JOptionPane.QUESTION_MESSAGE);
		if(result == JOptionPane.YES_OPTION){
			this.ontologyUtils.addRDFSLabel(owlOntology, owlEntity.getIRI(), label, lang);
         }
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		OWLOntology owlOntology = this.getOWLWorkspace().getOWLModelManager().getActiveOntology();
		OWLEntity owlEntity = this.getOWLWorkspace().getOWLSelectionModel().getSelectedEntity();
		
		String label = stringUtils.iri2string(owlEntity.getIRI());
		String lang = null;
		
		
		String message = String.format("The entity %s is going to be labelled as %s.", owlEntity.getIRI().toQuotedString(), label);
		//JOptionPane.showConfirmDialog(getOWLWorkspace(), message);
		int result = JOptionPane.showConfirmDialog(getOWLWorkspace(), message, "Create label",
	               JOptionPane.YES_NO_OPTION,
	               JOptionPane.QUESTION_MESSAGE);
		if(result == JOptionPane.YES_OPTION){
			this.ontologyUtils.addRDFSLabel(owlOntology, owlEntity.getIRI(), label, lang);
         }
	}
}
