package com.payasia.dao.bean;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * The persistent class for the Leave_Type_Master database table.
 * 
 */
@Entity
@Table(name = "Leave_Type_Master")
public class LeaveTypeMaster extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Leave_Type_ID")
	private long leaveTypeId;

	@Column(name = "Account_Code")
	private String accountCode;

	@Column(name = "Code")
	private String code;

	@Column(name = "Leave_Type_Desc")
	private String leaveTypeDesc;

	@Column(name = "Sort_Order")
	private int sortOrder;

	@ManyToOne
	@JoinColumn(name = "Front_End_View_Mode")
	private AppCodeMaster frontEndViewMode;

	@Column(name = "Front_End_Application_Mode")
	private boolean frontEndApplicationMode;

	@ManyToOne
	@JoinColumn(name = "Back_End_View_Mode")
	private AppCodeMaster backEndViewMode;

	@Column(name = "Back_End_Application_Mode")
	private boolean backEndApplicationMode;

	@Column(name = "Leave_Type_Name")
	private String leaveTypeName;

	@Column(name = "Visibility")
	private boolean visibility;

	 
	@OneToMany(mappedBy = "leaveTypeMaster")
	private Set<LeaveSchemeType> leaveSchemeTypes;

	 
	@ManyToOne
	@JoinColumn(name = "Company_ID")
	private Company company;

	 
	@OneToMany(mappedBy = "leaveTypeMaster")
	private Set<ReminderEventConfig> reminderEventConfigs;

	@ManyToOne
	@JoinColumn(name = "Leave_Type")
	private AppCodeMaster leaveType;

	public LeaveTypeMaster() {
	}

	public long getLeaveTypeId() {
		return this.leaveTypeId;
	}

	public void setLeaveTypeId(long leaveTypeId) {
		this.leaveTypeId = leaveTypeId;
	}

	public String getAccountCode() {
		return this.accountCode;
	}

	public void setAccountCode(String accountCode) {
		this.accountCode = accountCode;
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getLeaveTypeDesc() {
		return this.leaveTypeDesc;
	}

	public void setLeaveTypeDesc(String leaveTypeDesc) {
		this.leaveTypeDesc = leaveTypeDesc;
	}

	public String getLeaveTypeName() {
		return this.leaveTypeName;
	}

	public void setLeaveTypeName(String leaveTypeName) {
		this.leaveTypeName = leaveTypeName;
	}

	public boolean getVisibility() {
		return this.visibility;
	}

	public void setVisibility(boolean visibility) {
		this.visibility = visibility;
	}

	public Set<LeaveSchemeType> getLeaveSchemeTypes() {
		return this.leaveSchemeTypes;
	}

	public void setLeaveSchemeTypes(Set<LeaveSchemeType> leaveSchemeTypes) {
		this.leaveSchemeTypes = leaveSchemeTypes;
	}

	public Company getCompany() {
		return this.company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public int getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}

	public AppCodeMaster getFrontEndViewMode() {
		return frontEndViewMode;
	}

	public void setFrontEndViewMode(AppCodeMaster frontEndViewMode) {
		this.frontEndViewMode = frontEndViewMode;
	}

	public boolean isFrontEndApplicationMode() {
		return frontEndApplicationMode;
	}

	public void setFrontEndApplicationMode(boolean frontEndApplicationMode) {
		this.frontEndApplicationMode = frontEndApplicationMode;
	}

	public AppCodeMaster getBackEndViewMode() {
		return backEndViewMode;
	}

	public void setBackEndViewMode(AppCodeMaster backEndViewMode) {
		this.backEndViewMode = backEndViewMode;
	}

	public boolean isBackEndApplicationMode() {
		return backEndApplicationMode;
	}

	public void setBackEndApplicationMode(boolean backEndApplicationMode) {
		this.backEndApplicationMode = backEndApplicationMode;
	}

	public Set<ReminderEventConfig> getReminderEventConfigs() {
		return reminderEventConfigs;
	}

	public void setReminderEventConfigs(
			Set<ReminderEventConfig> reminderEventConfigs) {
		this.reminderEventConfigs = reminderEventConfigs;
	}

	public AppCodeMaster getLeaveType() {
		return leaveType;
	}

	public void setLeaveType(AppCodeMaster leaveType) {
		this.leaveType = leaveType;
	}

}