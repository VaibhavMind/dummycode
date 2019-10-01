package com.payasia.common.form;

import java.io.Serializable;

public class ChangeEmployeeNameListForm implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private long empNoHistoryID;
	private String oldEmployeeNumber;
	private String changeTo;
	private String changeOn;
	private String changedBy;
	private String changeReason;
	private long employeeID;
	private long chgByEmpID;
	private String changeEmployeeNumber;
	private String empNumberSeries;
	private Long empNumberSeriesId;

	public long getChgByEmpID() {
		return chgByEmpID;
	}

	public void setChgByEmpID(long chgByEmpID) {
		this.chgByEmpID = chgByEmpID;
	}

	public long getEmployeeID() {
		return employeeID;
	}

	public void setEmployeeID(long employeeID) {
		this.employeeID = employeeID;
	}

	public long getEmpNoHistoryID() {
		return empNoHistoryID;
	}

	public void setEmpNoHistoryID(long empNoHistoryID) {
		this.empNoHistoryID = empNoHistoryID;
	}

	public String getOldEmployeeNumber() {
		return oldEmployeeNumber;
	}

	public void setOldEmployeeNumber(String oldEmployeeNumber) {
		this.oldEmployeeNumber = oldEmployeeNumber;
	}

	public String getChangeTo() {
		return changeTo;
	}

	public void setChangeTo(String changeTo) {
		this.changeTo = changeTo;
	}

	public String getChangeOn() {
		return changeOn;
	}

	public void setChangeOn(String changeOn) {
		this.changeOn = changeOn;
	}

	public String getChangedBy() {
		return changedBy;
	}

	public void setChangedBy(String changedBy) {
		this.changedBy = changedBy;
	}

	public String getChangeReason() {
		return changeReason;
	}

	public void setChangeReason(String changeReason) {
		this.changeReason = changeReason;
	}

	public String getChangeEmployeeNumber() {
		return changeEmployeeNumber;
	}

	public void setChangeEmployeeNumber(String changeEmployeeNumber) {
		this.changeEmployeeNumber = changeEmployeeNumber;
	}

	public Long getEmpNumberSeriesId() {
		return empNumberSeriesId;
	}

	public void setEmpNumberSeriesId(Long empNumberSeriesId) {
		this.empNumberSeriesId = empNumberSeriesId;
	}

	public String getEmpNumberSeries() {
		return empNumberSeries;
	}

	public void setEmpNumberSeries(String empNumberSeries) {
		this.empNumberSeries = empNumberSeries;
	}

}
