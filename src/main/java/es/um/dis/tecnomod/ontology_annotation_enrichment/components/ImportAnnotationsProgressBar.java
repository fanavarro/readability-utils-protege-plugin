package es.um.dis.tecnomod.ontology_annotation_enrichment.components;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JProgressBar;

public class ImportAnnotationsProgressBar extends JProgressBar implements ImportAnnotationProgressListener {
	private final static Logger LOGGER = Logger.getLogger(ImportAnnotationsProgressBar.class.getName());

	/**
	 * 
	 */
	private static final long serialVersionUID = -8149558126066770174L;

	public ImportAnnotationsProgressBar(int i, int j) {
		super(i, j);
		this.setValue(0);
	}

	@Override
	public void onImportAnnotationProgress(ImportAnnotationProgressEvent event) {
		LOGGER.log(Level.WARNING, String.format("Receiving event %s", event.toString()));
		if (event.getPercentageComplete() != null){
			this.setValue(event.getPercentageComplete());
		}
	}

}
