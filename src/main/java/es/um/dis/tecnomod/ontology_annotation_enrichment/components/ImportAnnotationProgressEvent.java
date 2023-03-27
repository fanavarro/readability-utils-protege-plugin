package es.um.dis.tecnomod.ontology_annotation_enrichment.components;

import java.beans.PropertyChangeEvent;
import java.io.Serializable;

public class ImportAnnotationProgressEvent extends PropertyChangeEvent implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3043488184991421416L;
	public static final String PROPERTY_NAME = "";
	private ImportAnnotationProgressBean importAnnotationProgressBean;
	
	public ImportAnnotationProgressEvent(Object source, ImportAnnotationProgressBean oldValue, ImportAnnotationProgressBean newValue) {
		super(source, PROPERTY_NAME, oldValue, newValue);
		this.importAnnotationProgressBean = newValue;
	}
	public Integer getPercentageComplete() {
		return importAnnotationProgressBean.getPercentageComplete();
	}
	
	public String getMessage() {
		return importAnnotationProgressBean.getMessage();
	}
	public ImportAnnotationProgressBean getImportAnnotationProgressBean() {
		return importAnnotationProgressBean;
	}
	public void setImportAnnotationProgressBean(ImportAnnotationProgressBean importAnnotationProgressBean) {
		this.importAnnotationProgressBean = importAnnotationProgressBean;
	}
	
	
}
