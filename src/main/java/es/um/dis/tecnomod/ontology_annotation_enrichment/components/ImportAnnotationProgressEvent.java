package es.um.dis.tecnomod.ontology_annotation_enrichment.components;

import java.io.Serializable;

public class ImportAnnotationProgressEvent implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3043488184991421416L;
	private Integer percentageComplete;
	private String message;
	public ImportAnnotationProgressEvent(Integer percentage, String message) {
		super();
		
		this.message = message;
		this.percentageComplete = percentage;
		
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
		final int prime = 31;
		int result = 1;
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		result = prime * result + ((percentageComplete == null) ? 0 : percentageComplete.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ImportAnnotationProgressEvent other = (ImportAnnotationProgressEvent) obj;
		if (message == null) {
			if (other.message != null)
				return false;
		} else if (!message.equals(other.message))
			return false;
		if (percentageComplete == null) {
			if (other.percentageComplete != null)
				return false;
		} else if (!percentageComplete.equals(other.percentageComplete))
			return false;
		return true;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ImportAnnotationProgressEvent [percentageComplete=");
		builder.append(percentageComplete);
		builder.append(", message=");
		builder.append(message);
		builder.append("]");
		return builder.toString();
	}
	
}
