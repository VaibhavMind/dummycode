package com.payasia.common.form;

import java.io.Serializable;

public class GeneratePayslipForm implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String sourceFilePath;
	private String destinationFilePath;
	public String getSourceFilePath() {
		return sourceFilePath;
	}
	public void setSourceFilePath(String sourceFilePath) {
		this.sourceFilePath = sourceFilePath;
	}
	public String getDestinationFilePath() {
		return destinationFilePath;
	}
	public void setDestinationFilePath(String destinationFilePath) {
		this.destinationFilePath = destinationFilePath;
	}
	
	

}
