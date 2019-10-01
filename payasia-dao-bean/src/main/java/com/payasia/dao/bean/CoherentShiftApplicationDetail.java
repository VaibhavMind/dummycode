package com.payasia.dao.bean;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Coherent_Shift_Application_Detail")
public class CoherentShiftApplicationDetail extends CompanyBaseEntity implements
		Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "Shift_Application_Detail_ID")
	private long shiftApplicationDetailID;

	@ManyToOne
	@JoinColumn(name = "Shift_Application_ID")
	private CoherentShiftApplication coherentShiftApplication;

	@Column(name = "Shift_Date")
	private Timestamp shiftDate;

	@ManyToOne
	@JoinColumn(name = "Shift_Type")
	private AppCodeMaster shiftType;

	@Column(name = "Shift")
	private boolean shift;

	@Column(name = "Remarks")
	private String remarks;

	@Column(name = "Shift_Changed")
	private boolean shiftChanged;

	@Column(name = "Shift_Type_Changed")
	private boolean shiftTypeChanged;

	public boolean isShiftChanged() {
		return shiftChanged;
	}

	public void setShiftChanged(boolean shiftChanged) {
		this.shiftChanged = shiftChanged;
	}

	public long getShiftApplicationDetailID() {
		return shiftApplicationDetailID;
	}

	public void setShiftApplicationDetailID(long shiftApplicationDetailID) {
		this.shiftApplicationDetailID = shiftApplicationDetailID;
	}

	public CoherentShiftApplication getCoherentShiftApplication() {
		return coherentShiftApplication;
	}

	public void setCoherentShiftApplication(
			CoherentShiftApplication coherentShiftApplication) {
		this.coherentShiftApplication = coherentShiftApplication;
	}

	public Timestamp getShiftDate() {
		return shiftDate;
	}

	public void setShiftDate(Timestamp shiftDate) {
		this.shiftDate = shiftDate;
	}

	public boolean isShift() {
		return shift;
	}

	public void setShift(boolean shift) {
		this.shift = shift;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public AppCodeMaster getShiftType() {
		return shiftType;
	}

	public void setShiftType(AppCodeMaster shiftType) {
		this.shiftType = shiftType;
	}

	public boolean isShiftTypeChanged() {
		return shiftTypeChanged;
	}

	public void setShiftTypeChanged(boolean shiftTypeChanged) {
		this.shiftTypeChanged = shiftTypeChanged;
	}

}
