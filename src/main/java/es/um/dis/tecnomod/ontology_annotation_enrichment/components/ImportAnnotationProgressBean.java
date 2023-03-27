package es.um.dis.tecnomod.ontology_annotation_enrichment.components;

import java.io.Serializable;
import java.util.Objects;

public class ImportAnnotationProgressBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3690548414089379193L;
	private Integer percentageComplete;
	private String message;
	
	public ImportAnnotationProgressBean(Integer percentageComplete, String message) {
		super();
		this.percentageComplete = percentageComplete;
		this.message = message;
	}
	public Integer getPercentageComplete() {
		return percentageComplete;
	}
	public void setPercentageComplete(Integer percentageComplete) {
		this.percentageComplete = percentageComplete;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	@Override
	public int hashCode() {
		return Objects.hash(message, percentageComplete);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ImportAnnotationProgressBean other = (ImportAnnotationProgressBean) obj;
		return Objects.equals(message, other.message) && Objects.equals(percentageComplete, other.percentageComplete);
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ImportAnnotationProgressBean [percentageComplete=");
		builder.append(percentageComplete);
		builder.append(", message=");
		builder.append(message);
		builder.append("]");
		return builder.toString();
	}
	
}
