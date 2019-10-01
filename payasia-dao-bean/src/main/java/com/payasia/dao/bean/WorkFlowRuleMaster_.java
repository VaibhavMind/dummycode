package com.payasia.dao.bean;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2017-09-25T18:40:06.273+0530")
@StaticMetamodel(WorkFlowRuleMaster.class)
public class WorkFlowRuleMaster_ {
	public static volatile SingularAttribute<WorkFlowRuleMaster, Long> workFlowRuleId;
	public static volatile SingularAttribute<WorkFlowRuleMaster, String> ruleName;
	public static volatile SingularAttribute<WorkFlowRuleMaster, String> ruleValue;
	public static volatile SingularAttribute<WorkFlowRuleMaster, String> labelKey;
	public static volatile SetAttribute<WorkFlowRuleMaster, ClaimApplicationReviewer> claimApplicationReviewers;
	public static volatile SetAttribute<WorkFlowRuleMaster, ClaimTemplateWorkflow> claimTemplateWorkflows;
	public static volatile SetAttribute<WorkFlowRuleMaster, EmployeeClaimReviewer> employeeClaimReviewers;
	public static volatile SetAttribute<WorkFlowRuleMaster, EmployeeLeaveReviewer> employeeLeaveReviewers;
	public static volatile SetAttribute<WorkFlowRuleMaster, EmployeeOTReviewer> employeeOtReviewers;
	public static volatile SetAttribute<WorkFlowRuleMaster, LeaveApplicationReviewer> leaveApplicationReviewers;
	public static volatile SetAttribute<WorkFlowRuleMaster, LeaveSchemeWorkflow> leaveSchemeWorkflows;
	public static volatile SetAttribute<WorkFlowRuleMaster, LeaveSchemeTypeWorkflow> leaveSchemeTypeWorkflows;
	public static volatile SetAttribute<WorkFlowRuleMaster, OTTemplateWorkflow> otTemplateWorkflows;
	public static volatile SetAttribute<WorkFlowRuleMaster, OTApplicationReviewer> otApplicationReviewers;
	public static volatile SetAttribute<WorkFlowRuleMaster, ClaimAmountReviewerTemplate> claimAmountLevel1Reviewers;
	public static volatile SetAttribute<WorkFlowRuleMaster, ClaimAmountReviewerTemplate> claimAmountLevel2Reviewers;
	public static volatile SetAttribute<WorkFlowRuleMaster, ClaimAmountReviewerTemplate> claimAmountLevel3Reviewers;
}
