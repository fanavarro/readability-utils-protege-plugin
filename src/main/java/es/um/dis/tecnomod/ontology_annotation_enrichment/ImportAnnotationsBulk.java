package es.um.dis.tecnomod.ontology_annotation_enrichment;

import java.awt.event.ActionEvent;

import javax.swing.JComponent;
import javax.swing.JFrame;

import org.protege.editor.owl.ui.action.ProtegeOWLAction;
import org.semanticweb.owlapi.model.OWLOntology;

import es.um.dis.tecnomod.ontology_annotation_enrichment.components.ImportAnnotationsWindow;

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
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	createAndShowImportAnnotationsWindow(owlOntology);
            }
        });
		 
	}
	
	private void createAndShowImportAnnotationsWindow(OWLOntology owlOntology) {
        //Create and set up the window.
        JFrame frame = new JFrame("Import annotations (bulk)");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        //Create and set up the content pane.
        JComponent newContentPane = new ImportAnnotationsWindow(owlOntology);
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);

        //Display the window.
        frame.pack();
        frame.setSize(800,600);
        frame.setVisible(true);
    }

}
