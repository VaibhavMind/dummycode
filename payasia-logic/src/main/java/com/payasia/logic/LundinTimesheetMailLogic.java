package com.payasia.logic;

import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeTimesheetApplication;
import com.payasia.dao.bean.TimesheetApplicationWorkflow;

public interface LundinTimesheetMailLogic {

	String sendEMailForTimesheet(Long companyId,
			EmployeeTimesheetApplication lundinTimesheet,
			TimesheetApplicationWorkflow requestWorkflow, String subCategoryName,
			Employee loggedInEmployee, Employee reviewer);

	String sendWithdrawEmailForTimesheet(Long companyId,
			EmployeeTimesheetApplication lundinTimesheet, String subCategoryName,
			Employee loggedInEmployee, TimesheetApplicationWorkflow requestWorkflow);

	String sendAcceptRejectMailForTimesheet(Long companyId,
			TimesheetApplicationWorkflow requestWorkflow, String subCategoryName,
			Employee loggedInEmployee, String reviewRemarks);

	String sendPendingEmailForTimesheet(Long companyId,
			TimesheetApplicationWorkflow requestWorkflow, String subCategoryName,
			Employee loggenInEmployee, String revRemarks, Employee reviewer);

}
