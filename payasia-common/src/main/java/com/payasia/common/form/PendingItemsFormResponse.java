package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

import com.payasia.common.dto.EmployeeLeaveDTO;

public class PendingItemsFormResponse extends PageResponse implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3351329795327700764L;
	private List<PendingItemsForm> rows;
	private List<EmployeeLeaveDTO> employeeLeaveDTOs;
	private int recordSize;
	private String leaveUnit;
	
	
	
	public List<EmployeeLeaveDTO> getEmployeeLeaveDTOs() {
		return employeeLeaveDTOs;
	}

	public void setEmployeeLeaveDTOs(List<EmployeeLeaveDTO> employeeLeaveDTOs) {
		this.employeeLeaveDTOs = employeeLeaveDTOs;
	}

	public int getRecordSize() {
		return recordSize;
	}

	public void setRecordSize(int recordSize) {
		this.recordSize = recordSize;
	}
/*
	public List<PendingItemsForm> getPendingItemsList() {
		return pendingItemsList;
	}

	public void setPendingItemsList(List<PendingItemsForm> pendingItemsList) {
		this.pendingItemsList = pendingItemsList;
	}*/

	public String getLeaveUnit() {
		return leaveUnit;
	}

	public List<PendingItemsForm> getRows() {
		return rows;
	}

	public void setRows(List<PendingItemsForm> rows) {
		this.rows = rows;
	}

	public void setLeaveUnit(String leaveUnit) {
		this.leaveUnit = leaveUnit;
	}
	
}
