package es.um.dis.tecnomod.iri2label;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import org.protege.editor.owl.ui.action.ProtegeOWLAction;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;

public class CreateLabelFromIRI extends ProtegeOWLAction {

	private static final String CREATE_LABEL = "Create label";

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

	
	@Override
	public void actionPerformed(ActionEvent event) {
		OWLOntology owlOntology = this.getOWLWorkspace().getOWLModelManager().getActiveOntology();
		OWLEntity owlEntity = this.getOWLWorkspace().getOWLSelectionModel().getSelectedEntity();
		
		String label = stringUtils.iri2string(owlEntity.getIRI()).trim();
		String lang = null;
		String message;
		
		if (label == null || label.isEmpty()) {
			message = String.format("Label cannot be obtained because the entity IRI %s seems to be uncompliant with the NCName restrictions.", owlEntity.getIRI().toQuotedString());
			JOptionPane.showMessageDialog(getOWLWorkspace(), message, CREATE_LABEL, JOptionPane.WARNING_MESSAGE);
		}
		else {
			message = String.format("The entity %s is going to be labelled as %s.", owlEntity.getIRI().toQuotedString(), label);
			int result = JOptionPane.showConfirmDialog(getOWLWorkspace(), message, CREATE_LABEL,
		               JOptionPane.YES_NO_OPTION,
		               JOptionPane.QUESTION_MESSAGE);
			if(result == JOptionPane.YES_OPTION){
				this.ontologyUtils.addRDFSLabel(owlOntology, owlEntity.getIRI(), label, lang);
	        }
		}
	}
}
