package com.smile.webscripts.document;

/**
 * Encapsulates data about a Document upload to the Alfresco repository.
 */
public class DocumentUploadInfo {
	
	String nodeRef;
	String error;
	boolean success;	
	
	public DocumentUploadInfo(String nodeRef, String error, boolean success) {
		this.nodeRef = nodeRef;
		this.error = error;
		this.success = success;
	}

	public String getNodeRef() {
		return nodeRef;
	}
	
	public void setNodeRef(String nodeRef) {
		this.nodeRef = nodeRef;
	}
		
	public String getError() {
		return error;
	}
	
	public void setError(String error) {
		this.error = error;
	}
	
	public boolean isSuccess() {
		return success;
	}
	
	public void setSuccess(boolean success) {
		this.success = success;
	}	
}