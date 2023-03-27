package es.um.dis.tecnomod.ontology_annotation_enrichment.components;

import java.util.logging.Logger;

import javax.swing.JTextArea;

public class ImportAnnotationsProgressLog extends JTextArea implements ImportAnnotationProgressListener {
	private final static Logger LOGGER = Logger.getLogger(ImportAnnotationsProgressLog.class.getName());
	/**
	 * 
	 */
	private static final long serialVersionUID = 6693842385078303010L;

	public ImportAnnotationsProgressLog(int rows, int columns) {
		super(rows, columns);
	}

	@Override
	public void onImportAnnotationProgress(ImportAnnotationProgressEvent event) {
		if (event.getMessage() != null && !event.getMessage().isEmpty()) {
			this.append(event.getMessage());
			this.append("\n");
			this.setCaretPosition(this.getDocument().getLength());
		}
	}

}
