package com.payasia.logic;

import com.payasia.common.form.EmployeeListFormPage;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.HRISChangeRequest;
import com.payasia.dao.bean.HRISChangeRequestWorkflow;

/**
 * @author vivekjain
 * 
 */
public interface HRISMailLogic {

	String sendEMailForHRISDataChange(Long companyId,
			HRISChangeRequest hrisChangeRequest,
			HRISChangeRequestWorkflow hrisChangeRequestWorkflow,
			String subCategoryName, Employee loggedInEmployee,
			Employee reviewer, EmployeeListFormPage employeeListFormPage);

	String sendWithdrawEmailForHRISDataChange(Long companyId,
			HRISChangeRequest hrisChangeRequest, String subCategoryName,
			Employee loggedInEmployee, EmployeeListFormPage employeeListFormPage);

	String sendAcceptRejectMailForHRISDataChange(Long companyId,
			HRISChangeRequestWorkflow hrisChangeRequestWorkflow,
			String subCategoryName, Employee loggedInEmployee,
			String reviewRemarks, EmployeeListFormPage employeeListFormPage);

	String sendPendingEmailForHRISDataChange(Long companyId,
			HRISChangeRequestWorkflow hrisChangeRequestWorkflow,
			String subCategoryName, Employee loggenInEmployee,
			String revRemarks, Employee reviewer,
			EmployeeListFormPage employeeListFormPage);

}
