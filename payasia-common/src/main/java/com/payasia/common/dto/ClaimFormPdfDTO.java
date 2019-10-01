/**
 * @author vivekjain
 *
 */
package com.payasia.common.dto;

import java.io.Serializable;
import java.util.Arrays;


public class ClaimFormPdfDTO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -221427464878694254L;
	private byte[] claimFormPdfByteFile;
	private String employeeNumber;
	private String claimTemplateName;
	public byte[] getClaimFormPdfByteFile() {
		return claimFormPdfByteFile;
	}
	public void setClaimFormPdfByteFile(byte[] claimFormPdfByteFile) {
		if (claimFormPdfByteFile != null) {
			this.claimFormPdfByteFile = Arrays.copyOf(claimFormPdfByteFile, claimFormPdfByteFile.length);
		}
	}
	public String getEmployeeNumber() {
		return employeeNumber;
	}
	public void setEmployeeNumber(String employeeNumber) {
		this.employeeNumber = employeeNumber;
	}
	public String getClaimTemplateName() {
		return claimTemplateName;
	}
	public void setClaimTemplateName(String claimTemplateName) {
		this.claimTemplateName = claimTemplateName;
	}
	
}
