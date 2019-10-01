package com.payasia.dao.bean;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * The persistent class for the App_Code_Master database table.
 * 
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Table(name = "App_Code_Master")
public class AppCodeMaster implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "App_Code_ID")
	private long appCodeID;

	@Column(name = "Category")
	private String category;

	@Column(name = "Code_Desc")
	private String codeDesc;

	@Column(name = "Code_Value")
	private String codeValue;

	@Column(name = "Label_Key")
	private String labelKey;

	 
	@OneToMany(mappedBy = "appCodeMaster")
	private Set<CalendarCodeMaster> calendarCodeMasters;

	 
	@OneToMany(mappedBy = "leaveCalendar")
	private Set<LeaveSchemeTypeGranting> leaveSchemeTypeGrantings1;

	 
	@OneToMany(mappedBy = "distributionMethod")
	private Set<LeaveSchemeTypeGranting> leaveSchemeTypeGrantings2;

	 
	@OneToMany(mappedBy = "prorationBasedOn")
	private Set<LeaveSchemeTypeProration> leaveSchemeTypeProrations1;

	 
	@OneToMany(mappedBy = "prorationMethod")
	private Set<LeaveSchemeTypeProration> leaveSchemeTypeProrations2;

	@OneToMany(mappedBy = "prorationMethod")
	private Set<ClaimTemplate> claimTemplateProration;

	public Set<ClaimTemplate> getClaimTemplateProration() {
		return claimTemplateProration;
	}

	public void setClaimTemplateProration(
			Set<ClaimTemplate> claimTemplateProration) {
		this.claimTemplateProration = claimTemplateProration;
	}

	 
	@OneToMany(mappedBy = "roundingMethod")
	private Set<LeaveSchemeTypeProration> leaveSchemeTypeProrations3;

	 
	@OneToMany(mappedBy = "appCodeMaster")
	private Set<EmployeeLeaveSchemeTypeHistory> employeeLeaveSchemeTypeHistories;

	 
	@OneToMany(mappedBy = "frontEndViewMode")
	private Set<LeaveTypeMaster> frontEndViewModes;

	 
	@OneToMany(mappedBy = "backEndViewMode")
	private Set<LeaveTypeMaster> backEndViewModes;

	 
	@OneToMany(mappedBy = "frontEndViewMode")
	private Set<ClaimTemplate> frontEndViewMode;

	 
	@OneToMany(mappedBy = "backEndViewMode")
	private Set<ClaimTemplate> backEndViewMode;

	 
	@OneToMany(mappedBy = "recipientType")
	private Set<ReminderEventConfig> reminderEventConfigs;

	 
	@OneToMany(mappedBy = "grantCondition")
	private Set<LeaveSchemeTypeGrantCondition> leaveSchemeTypeGrantConditions;

	 
	@OneToMany(mappedBy = "leaveTransactionToShow")
	private Set<LeavePreference> leavePreferences;

	 
	@OneToMany(mappedBy = "claimType")
	private Set<ClaimTemplateItemClaimType> claimTemplateItemClaimTypes;

	 
	@OneToMany(mappedBy = "claimItemWorkflowAction")
	private Set<ClaimApplicationItemWorkflow> claimTemplateItemWorkflowActions;

	 
	@OneToMany(mappedBy = "claimItemWorkflowStatus")
	private Set<ClaimApplicationItemWorkflow> claimTemplateItemWorkflowStatuses;

	 
	@OneToMany(mappedBy = "allowedTimesField")
	private Set<ClaimTemplate> claimTemplates;

	 
	@OneToMany(mappedBy = "fieldType")
	private Set<ClaimTemplateItemCustomField> claimTemplateItemCustomFields;

	 
	@OneToMany(mappedBy = "applyAfterFrom")
	private Set<LeaveSchemeTypeAvailingLeave> leaveSchemeTypeAvailingLeaves;

	public AppCodeMaster() {
	}

	public long getAppCodeID() {
		return this.appCodeID;
	}

	public void setAppCodeID(long appCodeID) {
		this.appCodeID = appCodeID;
	}

	public String getCategory() {
		return this.category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getCodeDesc() {
		return this.codeDesc;
	}

	public void setCodeDesc(String codeDesc) {
		this.codeDesc = codeDesc;
	}

	public String getCodeValue() {
		return this.codeValue;
	}

	public void setCodeValue(String codeValue) {
		this.codeValue = codeValue;
	}

	public Set<EmployeeLeaveSchemeTypeHistory> getEmployeeLeaveSchemeTypeHistories() {
		return employeeLeaveSchemeTypeHistories;
	}

	public void setEmployeeLeaveSchemeTypeHistories(
			Set<EmployeeLeaveSchemeTypeHistory> employeeLeaveSchemeTypeHistories) {
		this.employeeLeaveSchemeTypeHistories = employeeLeaveSchemeTypeHistories;
	}

	public Set<LeaveSchemeTypeGranting> getLeaveSchemeTypeGrantings1() {
		return leaveSchemeTypeGrantings1;
	}

	public void setLeaveSchemeTypeGrantings1(
			Set<LeaveSchemeTypeGranting> leaveSchemeTypeGrantings1) {
		this.leaveSchemeTypeGrantings1 = leaveSchemeTypeGrantings1;
	}

	public Set<LeaveSchemeTypeGranting> getLeaveSchemeTypeGrantings2() {
		return leaveSchemeTypeGrantings2;
	}

	public void setLeaveSchemeTypeGrantings2(
			Set<LeaveSchemeTypeGranting> leaveSchemeTypeGrantings2) {
		this.leaveSchemeTypeGrantings2 = leaveSchemeTypeGrantings2;
	}

	public Set<LeaveSchemeTypeProration> getLeaveSchemeTypeProrations1() {
		return leaveSchemeTypeProrations1;
	}

	public void setLeaveSchemeTypeProrations1(
			Set<LeaveSchemeTypeProration> leaveSchemeTypeProrations1) {
		this.leaveSchemeTypeProrations1 = leaveSchemeTypeProrations1;
	}

	public Set<LeaveSchemeTypeProration> getLeaveSchemeTypeProrations2() {
		return leaveSchemeTypeProrations2;
	}

	public void setLeaveSchemeTypeProrations2(
			Set<LeaveSchemeTypeProration> leaveSchemeTypeProrations2) {
		this.leaveSchemeTypeProrations2 = leaveSchemeTypeProrations2;
	}

	public Set<LeaveSchemeTypeProration> getLeaveSchemeTypeProrations3() {
		return leaveSchemeTypeProrations3;
	}

	public void setLeaveSchemeTypeProrations3(
			Set<LeaveSchemeTypeProration> leaveSchemeTypeProrations3) {
		this.leaveSchemeTypeProrations3 = leaveSchemeTypeProrations3;
	}

	public Set<CalendarCodeMaster> getCalendarCodeMasters() {
		return calendarCodeMasters;
	}

	public void setCalendarCodeMasters(
			Set<CalendarCodeMaster> calendarCodeMasters) {
		this.calendarCodeMasters = calendarCodeMasters;
	}

	public Set<ReminderEventConfig> getReminderEventConfigs() {
		return reminderEventConfigs;
	}

	public void setReminderEventConfigs(
			Set<ReminderEventConfig> reminderEventConfigs) {
		this.reminderEventConfigs = reminderEventConfigs;
	}

	public Set<LeaveSchemeTypeGrantCondition> getLeaveSchemeTypeGrantConditions() {
		return leaveSchemeTypeGrantConditions;
	}

	public void setLeaveSchemeTypeGrantConditions(
			Set<LeaveSchemeTypeGrantCondition> leaveSchemeTypeGrantConditions) {
		this.leaveSchemeTypeGrantConditions = leaveSchemeTypeGrantConditions;
	}

	public Set<LeavePreference> getLeavePreferences() {
		return leavePreferences;
	}

	public void setLeavePreferences(Set<LeavePreference> leavePreferences) {
		this.leavePreferences = leavePreferences;
	}

	public Set<ClaimTemplateItemClaimType> getClaimTemplateItemClaimTypes() {
		return claimTemplateItemClaimTypes;
	}

	public void setClaimTemplateItemClaimTypes(
			Set<ClaimTemplateItemClaimType> claimTemplateItemClaimTypes) {
		this.claimTemplateItemClaimTypes = claimTemplateItemClaimTypes;
	}

	public Set<ClaimApplicationItemWorkflow> getClaimTemplateItemWorkflowActions() {
		return claimTemplateItemWorkflowActions;
	}

	public void setClaimTemplateItemWorkflowActions(
			Set<ClaimApplicationItemWorkflow> claimTemplateItemWorkflowActions) {
		this.claimTemplateItemWorkflowActions = claimTemplateItemWorkflowActions;
	}

	public Set<ClaimApplicationItemWorkflow> getClaimTemplateItemWorkflowStatuses() {
		return claimTemplateItemWorkflowStatuses;
	}

	public void setClaimTemplateItemWorkflowStatuses(
			Set<ClaimApplicationItemWorkflow> claimTemplateItemWorkflowStatuses) {
		this.claimTemplateItemWorkflowStatuses = claimTemplateItemWorkflowStatuses;
	}

	public Set<ClaimTemplate> getClaimTemplates() {
		return claimTemplates;
	}

	public void setClaimTemplates(Set<ClaimTemplate> claimTemplates) {
		this.claimTemplates = claimTemplates;
	}

	public Set<ClaimTemplateItemCustomField> getClaimTemplateItemCustomFields() {
		return claimTemplateItemCustomFields;
	}

	public void setClaimTemplateItemCustomFields(
			Set<ClaimTemplateItemCustomField> claimTemplateItemCustomFields) {
		this.claimTemplateItemCustomFields = claimTemplateItemCustomFields;
	}

	public Set<LeaveSchemeTypeAvailingLeave> getLeaveSchemeTypeAvailingLeaves() {
		return leaveSchemeTypeAvailingLeaves;
	}

	public void setLeaveSchemeTypeAvailingLeaves(
			Set<LeaveSchemeTypeAvailingLeave> leaveSchemeTypeAvailingLeaves) {
		this.leaveSchemeTypeAvailingLeaves = leaveSchemeTypeAvailingLeaves;
	}

	public Set<LeaveTypeMaster> getFrontEndViewModes() {
		return frontEndViewModes;
	}

	public void setFrontEndViewModes(Set<LeaveTypeMaster> frontEndViewModes) {
		this.frontEndViewModes = frontEndViewModes;
	}

	public Set<LeaveTypeMaster> getBackEndViewModes() {
		return backEndViewModes;
	}

	public void setBackEndViewModes(Set<LeaveTypeMaster> backEndViewModes) {
		this.backEndViewModes = backEndViewModes;
	}

	public String getLabelKey() {
		return labelKey;
	}

	public void setLabelKey(String labelKey) {
		this.labelKey = labelKey;
	}

	public Set<ClaimTemplate> getFrontEndViewMode() {
		return frontEndViewMode;
	}

	public void setFrontEndViewMode(Set<ClaimTemplate> frontEndViewMode) {
		this.frontEndViewMode = frontEndViewMode;
	}

	public Set<ClaimTemplate> getBackEndViewMode() {
		return backEndViewMode;
	}

	public void setBackEndViewMode(Set<ClaimTemplate> backEndViewMode) {
		this.backEndViewMode = backEndViewMode;
	}

}