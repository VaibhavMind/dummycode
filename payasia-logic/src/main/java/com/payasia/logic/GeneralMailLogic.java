package com.payasia.logic;

import java.math.BigDecimal;
import java.util.List;

import com.payasia.common.dto.LeaveDTO;
import com.payasia.common.dto.LeaveSessionDTO;
import com.payasia.common.dto.PreviousWorkflowDTO;
import com.payasia.common.form.AddLeaveForm;
import com.payasia.dao.bean.EmailTemplateSubCategoryMaster;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.LeaveApplication;
import com.payasia.dao.bean.LeaveApplicationWorkflow;
import com.payasia.dao.bean.WorkflowDelegate;

public interface GeneralMailLogic {

	AddLeaveForm getLeaveBalance(Long companyId, Long employeeId, Long employeeLeaveSchemeTypeId);

	AddLeaveForm getNoOfDays(Long companyId, Long employeeId, LeaveDTO leaveDTO);

	Long getSubCategoryId(String subCategoryName, List<EmailTemplateSubCategoryMaster> EmailTemplateSubCategoryMasterList);

	String sendAcceptRejectMailForLeave(Long companyId, LeaveApplicationWorkflow leaveApplicationWorkflow, String subCategoryName, BigDecimal days,
			BigDecimal balance, Employee loggedInEmployee, String reviewRemarks, LeaveSessionDTO sessionDTO, boolean isLeaveUnitDays, String... fromEmail);

	String sendEMailForLeave(Long companyId, LeaveApplication leaveApplication, String subCategoryName, BigDecimal days, BigDecimal balance,
			Employee loggedInEmployee, Employee reviewer, LeaveSessionDTO sessionDTO, boolean isLeaveUnitDays);

	String sendWithdrawEmailForLeave(Long companyId, LeaveApplicationWorkflow leaveApplicationWorkflow, String subCategoryName, BigDecimal days,
			BigDecimal balance, Employee loggedInEmployee, LeaveSessionDTO sessionDTO, boolean isLeaveUnitDays);

	String sendPendingEmailForLeave(Long companyId, LeaveApplicationWorkflow leaveApplicationWorkflow, String subCategoryName, BigDecimal days,
			BigDecimal balance, Employee loggenInEmployee, String revRemarks, Employee reviewer, LeaveSessionDTO sessionDTO, boolean isLeaveUnitDays);

	String sendMailToDelgate(Long companyId, WorkflowDelegate workflowDelegatem, PreviousWorkflowDTO previousWorkflowDTO, String subcategoryName,
			boolean isLeaveUnitDays);

	String getEmployeeName(Employee employee);

	String sendEMailLeaveByAdminForEmployee(Long companyId, LeaveApplication leaveApplication, String subCategoryName, BigDecimal days, BigDecimal balance,
			Employee loggedInEmployee, Employee reviewer, LeaveSessionDTO sessionDTO, boolean isLeaveUnitDays, String toEmailId);

	public String sendMailForPreApprovedLeave(Long companyId, LeaveApplicationWorkflow leaveApplicationWorkflow, String subCategoryName, BigDecimal days,
			BigDecimal balance, Employee loggedInEmployee, String reviewRemarks, LeaveSessionDTO sessionDTO, boolean isLeaveUnitDays, String toEmail,
			String... fromEmail);

	String sendMailForLeaveExtension(Long companyId, LeaveApplication leaveApplication, String subCategoryName,
			BigDecimal days, BigDecimal balance, Employee loggedInEmployee, String reviewRemarks,
			LeaveSessionDTO sessionDTO, boolean isLeaveUnitDays, String toEmail, String... fromEmail);

	String sendMailToDelgate(Long companyId, WorkflowDelegate workflowDelegatem,
			PreviousWorkflowDTO previousWorkflowDTO, String subcategoryName, boolean isLeaveUnitDays,
			boolean userCCmailCheck);

	String sendMailToEmployee(Long companyId, String subcategoryName, String email, String otp,String expire);

}
