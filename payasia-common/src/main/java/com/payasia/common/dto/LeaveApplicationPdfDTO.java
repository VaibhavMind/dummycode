/**
 * @author vivekjain
 *
 */
package com.payasia.common.dto;

import java.io.Serializable;
import java.util.Arrays;


public class LeaveApplicationPdfDTO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -221427464878694254L;
	private byte[] leaveAppPdfByteFile;
	private String employeeNumber;
	private String leaveSchemeName;
	public byte[] getLeaveAppPdfByteFile() {
		return leaveAppPdfByteFile;
	}
	public void setLeaveAppPdfByteFile(byte[] leaveAppPdfByteFile) {
		if (leaveAppPdfByteFile != null) {
			this.leaveAppPdfByteFile = Arrays.copyOf(leaveAppPdfByteFile, leaveAppPdfByteFile.length);
		}
	}
	public String getEmployeeNumber() {
		return employeeNumber;
	}
	public void setEmployeeNumber(String employeeNumber) {
		this.employeeNumber = employeeNumber;
	}
	public String getLeaveSchemeName() {
		return leaveSchemeName;
	}
	public void setLeaveSchemeName(String leaveSchemeName) {
		this.leaveSchemeName = leaveSchemeName;
	}
	
}
