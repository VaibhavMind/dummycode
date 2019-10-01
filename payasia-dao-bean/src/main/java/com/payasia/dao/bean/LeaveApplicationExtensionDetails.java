package com.payasia.dao.bean;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Leave_Application_Extension_Details")
public class LeaveApplicationExtensionDetails extends CompanyBaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy =GenerationType.IDENTITY)
	@Column(name = "Leave_Application_Extension_Details_ID")
	private long leaveApplicationExtensionDetailsId;

	@Column(name = "From_Date")
	private Timestamp fromDate;

	@Column(name = "To_Date")
	private Timestamp toDate;

	@ManyToOne
	@JoinColumn(name = "Start_Session")
	private LeaveSessionMaster leaveSessionMaster1;

	@ManyToOne
	@JoinColumn(name = "End_Session")
	private LeaveSessionMaster leaveSessionMaster2;

	@Column(name = "Remarks")
	private String remarks;

	@ManyToOne
	@JoinColumn(name = "Leave_Application_ID")
	private LeaveApplication leaveApplication;

	public LeaveApplicationExtensionDetails() {
	}

	public long getLeaveApplicationExtensionDetailsId() {
		return leaveApplicationExtensionDetailsId;
	}

	public void setLeaveApplicationExtensionDetailsId(long leaveApplicationExtensionDetailsId) {
		this.leaveApplicationExtensionDetailsId = leaveApplicationExtensionDetailsId;
	}

	public Timestamp getToDate() {
		return toDate;
	}

	public void setToDate(Timestamp toDate) {
		this.toDate = toDate;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public LeaveApplication getLeaveApplication() {
		return leaveApplication;
	}

	public void setLeaveApplication(LeaveApplication leaveApplication) {
		this.leaveApplication = leaveApplication;
	}

	public Timestamp getFromDate() {
		return fromDate;
	}

	public void setFromDate(Timestamp fromDate) {
		this.fromDate = fromDate;
	}

	public LeaveSessionMaster getLeaveSessionMaster1() {
		return leaveSessionMaster1;
	}

	public void setLeaveSessionMaster1(LeaveSessionMaster leaveSessionMaster1) {
		this.leaveSessionMaster1 = leaveSessionMaster1;
	}

	public LeaveSessionMaster getLeaveSessionMaster2() {
		return leaveSessionMaster2;
	}

	public void setLeaveSessionMaster2(LeaveSessionMaster leaveSessionMaster2) {
		this.leaveSessionMaster2 = leaveSessionMaster2;
	}

}
