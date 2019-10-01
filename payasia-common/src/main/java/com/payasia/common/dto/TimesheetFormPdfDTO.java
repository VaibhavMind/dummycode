/**
 * @author vivekjain
 *
 */
package com.payasia.common.dto;

import java.io.Serializable;


public class TimesheetFormPdfDTO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -221427464878694254L;
	private byte[] timesheetPdfByteFile;
	private String employeeNumber;
	private String timesheetBatchDesc;
	public byte[] getTimesheetPdfByteFile() {
		return timesheetPdfByteFile;
	}
	public void setTimesheetPdfByteFile(byte[] timesheetPdfByteFile) {
		this.timesheetPdfByteFile = timesheetPdfByteFile;
	}
	public String getEmployeeNumber() {
		return employeeNumber;
	}
	public void setEmployeeNumber(String employeeNumber) {
		this.employeeNumber = employeeNumber;
	}
	public String getTimesheetBatchDesc() {
		return timesheetBatchDesc;
	}
	public void setTimesheetBatchDesc(String timesheetBatchDesc) {
		this.timesheetBatchDesc = timesheetBatchDesc;
	}
	
	
}
