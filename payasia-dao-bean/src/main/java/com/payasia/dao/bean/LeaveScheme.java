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
 * The persistent class for the Leave_Scheme database table.
 * 
 */
@Entity
@Table(name = "Leave_Scheme")
public class LeaveScheme extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Leave_Scheme_ID")
	private long leaveSchemeId;

	@Column(name = "Scheme_Name")
	private String schemeName;

	@Column(name = "Visibility")
	private boolean visibility;

	 
	@ManyToOne
	@JoinColumn(name = "Company_ID")
	private Company company;

	 
	@OneToMany(mappedBy = "leaveScheme", cascade = { CascadeType.REMOVE })
	private Set<LeaveSchemeType> leaveSchemeTypes;

	 
	@OneToMany(mappedBy = "leaveScheme")
	private Set<LeaveSchemeWorkflow> leaveSchemeWorkflows;

	 
	@OneToMany(mappedBy = "leaveScheme")
	private Set<EmployeeLeaveScheme> employeeLeaveSchemes;

	 
	@OneToMany(mappedBy = "leaveScheme")
	private Set<ReminderEventConfig> reminderEventConfigs;

	public LeaveScheme() {
	}

	public long getLeaveSchemeId() {
		return this.leaveSchemeId;
	}

	public void setLeaveSchemeId(long leaveSchemeId) {
		this.leaveSchemeId = leaveSchemeId;
	}

	public String getSchemeName() {
		return this.schemeName;
	}

	public void setSchemeName(String schemeName) {
		this.schemeName = schemeName;
	}

	public boolean getVisibility() {
		return this.visibility;
	}

	public void setVisibility(boolean visibility) {
		this.visibility = visibility;
	}

	public Company getCompany() {
		return this.company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public Set<LeaveSchemeType> getLeaveSchemeTypes() {
		return this.leaveSchemeTypes;
	}

	public void setLeaveSchemeTypes(Set<LeaveSchemeType> leaveSchemeTypes) {
		this.leaveSchemeTypes = leaveSchemeTypes;
	}

	public Set<LeaveSchemeWorkflow> getLeaveSchemeWorkflows() {
		return this.leaveSchemeWorkflows;
	}

	public void setLeaveSchemeWorkflows(
			Set<LeaveSchemeWorkflow> leaveSchemeWorkflows) {
		this.leaveSchemeWorkflows = leaveSchemeWorkflows;
	}

	public Set<EmployeeLeaveScheme> getEmployeeLeaveSchemes() {
		return this.employeeLeaveSchemes;
	}

	public void setEmployeeLeaveSchemes(
			Set<EmployeeLeaveScheme> employeeLeaveSchemes) {
		this.employeeLeaveSchemes = employeeLeaveSchemes;
	}

	public Set<ReminderEventConfig> getReminderEventConfigs() {
		return reminderEventConfigs;
	}

	public void setReminderEventConfigs(
			Set<ReminderEventConfig> reminderEventConfigs) {
		this.reminderEventConfigs = reminderEventConfigs;
	}

}