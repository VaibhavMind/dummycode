package com.payasia.dao.bean;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * The persistent class for the Work_Flow_Rule_Master database table.
 * 
 */
@Entity
@Table(name = "Work_Flow_Rule_Master")
public class WorkFlowRuleMaster implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Work_Flow_Rule_ID")
	private long workFlowRuleId;

	@Column(name = "Rule_Name")
	private String ruleName;

	@Column(name = "Rule_Value")
	private String ruleValue;

	@Column(name = "Label_Key")
	private String labelKey;

	@OneToMany(mappedBy = "workFlowRuleMaster")
	private Set<ClaimApplicationReviewer> claimApplicationReviewers;

	@OneToMany(mappedBy = "workFlowRuleMaster")
	private Set<ClaimTemplateWorkflow> claimTemplateWorkflows;

	@OneToMany(mappedBy = "workFlowRuleMaster")
	private Set<EmployeeClaimReviewer> employeeClaimReviewers;

	@OneToMany(mappedBy = "workFlowRuleMaster")
	private Set<EmployeeLeaveReviewer> employeeLeaveReviewers;

	@OneToMany(mappedBy = "workFlowRuleMaster")
	private Set<EmployeeOTReviewer> employeeOtReviewers;

	@OneToMany(mappedBy = "workFlowRuleMaster")
	private Set<LeaveApplicationReviewer> leaveApplicationReviewers;

	@OneToMany(mappedBy = "workFlowRuleMaster")
	private Set<LeaveSchemeWorkflow> leaveSchemeWorkflows;

	@OneToMany(mappedBy = "workFlowRuleMaster")
	private Set<LeaveSchemeTypeWorkflow> leaveSchemeTypeWorkflows;

	@OneToMany(mappedBy = "workFlowRuleMaster")
	private Set<OTTemplateWorkflow> otTemplateWorkflows;

	@OneToMany(mappedBy = "workFlowRuleMaster")
	private Set<OTApplicationReviewer> otApplicationReviewers;

	@OneToMany(mappedBy = "Level1")
	private Set<ClaimAmountReviewerTemplate> claimAmountLevel1Reviewers;

	@OneToMany(mappedBy = "Level2")
	private Set<ClaimAmountReviewerTemplate> claimAmountLevel2Reviewers;

	@OneToMany(mappedBy = "Level3")
	private Set<ClaimAmountReviewerTemplate> claimAmountLevel3Reviewers;

	public WorkFlowRuleMaster() {
	}

	public long getWorkFlowRuleId() {
		return this.workFlowRuleId;
	}

	public void setWorkFlowRuleId(long workFlowRuleId) {
		this.workFlowRuleId = workFlowRuleId;
	}

	public String getRuleName() {
		return this.ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	public String getRuleValue() {
		return this.ruleValue;
	}

	public void setRuleValue(String ruleValue) {
		this.ruleValue = ruleValue;
	}

	public Set<ClaimApplicationReviewer> getClaimApplicationReviewers() {
		return this.claimApplicationReviewers;
	}

	public void setClaimApplicationReviewers(
			Set<ClaimApplicationReviewer> claimApplicationReviewers) {
		this.claimApplicationReviewers = claimApplicationReviewers;
	}

	public Set<ClaimTemplateWorkflow> getClaimTemplateWorkflows() {
		return this.claimTemplateWorkflows;
	}

	public void setClaimTemplateWorkflows(
			Set<ClaimTemplateWorkflow> claimTemplateWorkflows) {
		this.claimTemplateWorkflows = claimTemplateWorkflows;
	}

	public Set<EmployeeClaimReviewer> getEmployeeClaimReviewers() {
		return this.employeeClaimReviewers;
	}

	public void setEmployeeClaimReviewers(
			Set<EmployeeClaimReviewer> employeeClaimReviewers) {
		this.employeeClaimReviewers = employeeClaimReviewers;
	}

	public Set<EmployeeLeaveReviewer> getEmployeeLeaveReviewers() {
		return this.employeeLeaveReviewers;
	}

	public void setEmployeeLeaveReviewers(
			Set<EmployeeLeaveReviewer> employeeLeaveReviewers) {
		this.employeeLeaveReviewers = employeeLeaveReviewers;
	}

	public Set<EmployeeOTReviewer> getEmployeeOtReviewers() {
		return this.employeeOtReviewers;
	}

	public void setEmployeeOtReviewers(
			Set<EmployeeOTReviewer> employeeOtReviewers) {
		this.employeeOtReviewers = employeeOtReviewers;
	}

	public Set<LeaveApplicationReviewer> getLeaveApplicationReviewers() {
		return this.leaveApplicationReviewers;
	}

	public void setLeaveApplicationReviewers(
			Set<LeaveApplicationReviewer> leaveApplicationReviewers) {
		this.leaveApplicationReviewers = leaveApplicationReviewers;
	}

	public Set<LeaveSchemeWorkflow> getLeaveSchemeWorkflows() {
		return this.leaveSchemeWorkflows;
	}

	public void setLeaveSchemeWorkflows(
			Set<LeaveSchemeWorkflow> leaveSchemeWorkflows) {
		this.leaveSchemeWorkflows = leaveSchemeWorkflows;
	}

	public Set<OTTemplateWorkflow> getOtTemplateWorkflows() {
		return this.otTemplateWorkflows;
	}

	public void setOtTemplateWorkflows(
			Set<OTTemplateWorkflow> otTemplateWorkflows) {
		this.otTemplateWorkflows = otTemplateWorkflows;
	}

	public Set<OTApplicationReviewer> getOtApplicationReviewers() {
		return this.otApplicationReviewers;
	}

	public void setOtApplicationReviewers(
			Set<OTApplicationReviewer> otApplicationReviewers) {
		this.otApplicationReviewers = otApplicationReviewers;
	}

	public Set<LeaveSchemeTypeWorkflow> getLeaveSchemeTypeWorkflows() {
		return leaveSchemeTypeWorkflows;
	}

	public void setLeaveSchemeTypeWorkflows(
			Set<LeaveSchemeTypeWorkflow> leaveSchemeTypeWorkflows) {
		this.leaveSchemeTypeWorkflows = leaveSchemeTypeWorkflows;
	}

	public Set<ClaimAmountReviewerTemplate> getClaimAmountLevel1Reviewers() {
		return claimAmountLevel1Reviewers;
	}

	public void setClaimAmountLevel1Reviewers(
			Set<ClaimAmountReviewerTemplate> claimAmountLevel1Reviewers) {
		this.claimAmountLevel1Reviewers = claimAmountLevel1Reviewers;
	}

	public Set<ClaimAmountReviewerTemplate> getClaimAmountLevel2Reviewers() {
		return claimAmountLevel2Reviewers;
	}

	public void setClaimAmountLevel2Reviewers(
			Set<ClaimAmountReviewerTemplate> claimAmountLevel2Reviewers) {
		this.claimAmountLevel2Reviewers = claimAmountLevel2Reviewers;
	}

	public Set<ClaimAmountReviewerTemplate> getClaimAmountLevel3Reviewers() {
		return claimAmountLevel3Reviewers;
	}

	public void setClaimAmountLevel3Reviewers(
			Set<ClaimAmountReviewerTemplate> claimAmountLevel3Reviewers) {
		this.claimAmountLevel3Reviewers = claimAmountLevel3Reviewers;
	}

	public String getLabelKey() {
		return labelKey;
	}

	public void setLabelKey(String labelKey) {
		this.labelKey = labelKey;
	}
}