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

/**
 * The persistent class for the Lundin_Timesheet_Detail database table.
 * 
 */
@Entity
@Table(name = "Lundin_Timesheet_Detail")
public class LundinTimesheetDetail extends CompanyBaseEntity implements
		Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "Timesheet_Detail_ID")
	private long timesheetDetailID;

	@ManyToOne
	@JoinColumn(name = "Timesheet_ID")
	private EmployeeTimesheetApplication employeeTimesheetApplication;

	@Column(name = "Timesheet_Date")
	private Timestamp timesheetDate;

	@ManyToOne
	@JoinColumn(name = "Block_ID")
	private LundinBlock lundinBlock;

	@ManyToOne
	@JoinColumn(name = "AFE_ID")
	private LundinAFE lundinAFE;

	@ManyToOne
	@JoinColumn(name = "Value")
	private AppCodeMaster value;

	@Column(name = "Block_Code")
	private String blockCode;

	@Column(name = "Block_Name")
	private String blockName;

	@Column(name = "AFE_Code")
	private String afeCode;

	@Column(name = "AFE_Name")
	private String afeName;

	public LundinTimesheetDetail() {
	}

	public long getTimesheetDetailID() {
		return timesheetDetailID;
	}

	public void setTimesheetDetailID(long timesheetDetailID) {
		this.timesheetDetailID = timesheetDetailID;
	}

	public EmployeeTimesheetApplication getEmployeeTimesheetApplication() {
		return employeeTimesheetApplication;
	}

	public void setEmployeeTimesheetApplication(
			EmployeeTimesheetApplication employeeTimesheetApplication) {
		this.employeeTimesheetApplication = employeeTimesheetApplication;
	}

	public Timestamp getTimesheetDate() {
		return timesheetDate;
	}

	public void setTimesheetDate(Timestamp timesheetDate) {
		this.timesheetDate = timesheetDate;
	}

	public LundinBlock getLundinBlock() {
		return lundinBlock;
	}

	public void setLundinBlock(LundinBlock lundinBlock) {
		this.lundinBlock = lundinBlock;
	}

	public LundinAFE getLundinAFE() {
		return lundinAFE;
	}

	public void setLundinAFE(LundinAFE lundinAFE) {
		this.lundinAFE = lundinAFE;
	}

	public AppCodeMaster getValue() {
		return value;
	}

	public void setValue(AppCodeMaster value) {
		this.value = value;
	}

	public String getBlockCode() {
		return blockCode;
	}

	public void setBlockCode(String blockCode) {
		this.blockCode = blockCode;
	}

	public String getBlockName() {
		return blockName;
	}

	public void setBlockName(String blockName) {
		this.blockName = blockName;
	}

	public String getAfeCode() {
		return afeCode;
	}

	public void setAfeCode(String afeCode) {
		this.afeCode = afeCode;
	}

	public String getAfeName() {
		return afeName;
	}

	public void setAfeName(String afeName) {
		this.afeName = afeName;
	}

}