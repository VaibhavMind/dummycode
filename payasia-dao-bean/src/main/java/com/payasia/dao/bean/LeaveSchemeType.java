package com.payasia.dao.bean;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
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
 * The persistent class for the Leave_Scheme_Type database table.
 * 
 */
@Entity
@Table(name = "Leave_Scheme_Type")
public class LeaveSchemeType extends CompanyBaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Leave_Scheme_Type_ID")
	private long leaveSchemeTypeId;

	@Column(name = "Visibility")
	private boolean visibility;

	@Column(name = "Workflow_Changed")
	private boolean workflowChanged;

	 
	@ManyToOne
	@JoinColumn(name = "Leave_Scheme_ID")
	private LeaveScheme leaveScheme;

	 
	@ManyToOne
	@JoinColumn(name = "Leave_Type_ID")
	private LeaveTypeMaster leaveTypeMaster;

	 
	@OneToMany(mappedBy = "leaveSchemeType", cascade = { CascadeType.REMOVE })
	private Set<LeaveSchemeTypeGranting> leaveSchemeTypeGrantings;

	 
	@OneToMany(mappedBy = "leaveSchemeType", cascade = { CascadeType.REMOVE })
	private Set<LeaveSchemeTypeProration> leaveSchemeTypeProrations;

	 
	@OneToMany(mappedBy = "leaveSchemeType", cascade = { CascadeType.REMOVE })
	private Set<LeaveSchemeTypeAvailingLeave> LeaveSchemeTypeAvailingLeaves;

	 
	@OneToMany(mappedBy = "considerLeaveBalanceFrom")
	private Set<LeaveSchemeTypeAvailingLeave> leaveSchemeTypeAvailingLeaves1;

	 
	@OneToMany(mappedBy = "leaveSchemeType", cascade = { CascadeType.REMOVE })
	private Set<LeaveSchemeTypeYearEnd> leaveSchemeTypeYearEnds;

	 
	@OneToMany(mappedBy = "leaveSchemeType", cascade = { CascadeType.REMOVE })
	private Set<LeaveSchemeTypeShortList> leaveSchemeTypeShortLists;

	 
	@OneToMany(mappedBy = "leaveSchemeType", cascade = { CascadeType.REMOVE })
	private Set<LeaveSchemeTypeWorkflow> leaveSchemeTypeWorkflows;

	 
	@OneToMany(mappedBy = "leaveSchemeType")
	private Set<EmployeeLeaveSchemeType> employeeLeaveSchemeTypes;

	 
	@OneToMany(mappedBy = "leaveSchemeType")
	private Set<LeaveGrantBatchDetail> leaveGrantBatchDetails;

	 
	@OneToMany(mappedBy = "grantConditionValue")
	private Set<LeaveSchemeTypeGrantCondition> leaveSchemeTypeGrantConditions;

	 
	@OneToMany(mappedBy = "leaveSchemeType")
	private Set<LeaveYearEndBatch> leaveYearEndBatchs;

	public LeaveSchemeType() {
	}

	public long getLeaveSchemeTypeId() {
		return this.leaveSchemeTypeId;
	}

	public void setLeaveSchemeTypeId(long leaveSchemeTypeId) {
		this.leaveSchemeTypeId = leaveSchemeTypeId;
	}

	public boolean getVisibility() {
		return this.visibility;
	}

	public void setVisibility(boolean visibility) {
		this.visibility = visibility;
	}

	public LeaveScheme getLeaveScheme() {
		return this.leaveScheme;
	}

	public void setLeaveScheme(LeaveScheme leaveScheme) {
		this.leaveScheme = leaveScheme;
	}

	public LeaveTypeMaster getLeaveTypeMaster() {
		return this.leaveTypeMaster;
	}

	public void setLeaveTypeMaster(LeaveTypeMaster leaveTypeMaster) {
		this.leaveTypeMaster = leaveTypeMaster;
	}

	public Set<LeaveSchemeTypeGranting> getLeaveSchemeTypeGrantings() {
		return leaveSchemeTypeGrantings;
	}

	public void setLeaveSchemeTypeGrantings(
			Set<LeaveSchemeTypeGranting> leaveSchemeTypeGrantings) {
		this.leaveSchemeTypeGrantings = leaveSchemeTypeGrantings;
	}

	public Set<LeaveSchemeTypeShortList> getLeaveSchemeTypeShortLists() {
		return this.leaveSchemeTypeShortLists;
	}

	public void setLeaveSchemeTypeShortLists(
			Set<LeaveSchemeTypeShortList> leaveSchemeTypeShortLists) {
		this.leaveSchemeTypeShortLists = leaveSchemeTypeShortLists;
	}

	public Set<LeaveSchemeTypeProration> getLeaveSchemeTypeProrations() {
		return leaveSchemeTypeProrations;
	}

	public void setLeaveSchemeTypeProrations(
			Set<LeaveSchemeTypeProration> leaveSchemeTypeProrations) {
		this.leaveSchemeTypeProrations = leaveSchemeTypeProrations;
	}

	public Set<LeaveSchemeTypeAvailingLeave> getLeaveSchemeTypeAvailingLeaves1() {
		return leaveSchemeTypeAvailingLeaves1;
	}

	public void setLeaveSchemeTypeAvailingLeaves1(
			Set<LeaveSchemeTypeAvailingLeave> leaveSchemeTypeAvailingLeaves1) {
		this.leaveSchemeTypeAvailingLeaves1 = leaveSchemeTypeAvailingLeaves1;
	}

	public Set<LeaveSchemeTypeAvailingLeave> getLeaveSchemeTypeAvailingLeaves() {
		return LeaveSchemeTypeAvailingLeaves;
	}

	public void setLeaveSchemeTypeAvailingLeaves(
			Set<LeaveSchemeTypeAvailingLeave> leaveSchemeTypeAvailingLeaves) {
		LeaveSchemeTypeAvailingLeaves = leaveSchemeTypeAvailingLeaves;
	}

	public Set<LeaveSchemeTypeYearEnd> getLeaveSchemeTypeYearEnds() {
		return leaveSchemeTypeYearEnds;
	}

	public void setLeaveSchemeTypeYearEnds(
			Set<LeaveSchemeTypeYearEnd> leaveSchemeTypeYearEnds) {
		this.leaveSchemeTypeYearEnds = leaveSchemeTypeYearEnds;
	}

	public Set<LeaveSchemeTypeWorkflow> getLeaveSchemeTypeWorkflows() {
		return leaveSchemeTypeWorkflows;
	}

	public void setLeaveSchemeTypeWorkflows(
			Set<LeaveSchemeTypeWorkflow> leaveSchemeTypeWorkflows) {
		this.leaveSchemeTypeWorkflows = leaveSchemeTypeWorkflows;
	}

	public boolean isWorkflowChanged() {
		return workflowChanged;
	}

	public void setWorkflowChanged(boolean workflowChanged) {
		this.workflowChanged = workflowChanged;
	}

	public Set<EmployeeLeaveSchemeType> getEmployeeLeaveSchemeTypes() {
		return employeeLeaveSchemeTypes;
	}

	public void setEmployeeLeaveSchemeTypes(
			Set<EmployeeLeaveSchemeType> employeeLeaveSchemeTypes) {
		this.employeeLeaveSchemeTypes = employeeLeaveSchemeTypes;
	}

	public Set<LeaveGrantBatchDetail> getLeaveGrantBatchDetails() {
		return leaveGrantBatchDetails;
	}

	public void setLeaveGrantBatchDetails(
			Set<LeaveGrantBatchDetail> leaveGrantBatchDetails) {
		this.leaveGrantBatchDetails = leaveGrantBatchDetails;
	}

	public Set<LeaveSchemeTypeGrantCondition> getLeaveSchemeTypeGrantConditions() {
		return leaveSchemeTypeGrantConditions;
	}

	public void setLeaveSchemeTypeGrantConditions(
			Set<LeaveSchemeTypeGrantCondition> leaveSchemeTypeGrantConditions) {
		this.leaveSchemeTypeGrantConditions = leaveSchemeTypeGrantConditions;
	}

	public Set<LeaveYearEndBatch> getLeaveYearEndBatchs() {
		return leaveYearEndBatchs;
	}

	public void setLeaveYearEndBatchs(Set<LeaveYearEndBatch> leaveYearEndBatchs) {
		this.leaveYearEndBatchs = leaveYearEndBatchs;
	}

}
