package es.um.dis.tecnomod.ontology_annotation_enrichment.components;

import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;

import org.semanticweb.owlapi.model.OWLOntology;

public class ImportAnnotationsBulkWindow extends ImportAnnotationWindowAbstract
                             implements ActionListener, PropertyChangeListener {

    /**
	 * 
	 */
	private static final long serialVersionUID = -2875550578135610005L;

    public ImportAnnotationsBulkWindow(OWLOntology ontology) {
    	super(ontology);
    }
    

}
