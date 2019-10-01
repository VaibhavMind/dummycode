package com.payasia.logic;

import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeTimesheetApplication;
import com.payasia.dao.bean.LionTimesheetApplicationReviewer;

public interface LionTimesheetMailLogic {

	String sendEMailForTimesheet(Long companyId,
			EmployeeTimesheetApplication empTimesheetApplication,
			LionTimesheetApplicationReviewer requestWorkflow,
			String subCategoryName, Employee loggedInEmployee, Employee reviewer);

	String sendAcceptRejectMailForTimesheet(Long companyId,
			EmployeeTimesheetApplication employeeTimesheetApp,
			LionTimesheetApplicationReviewer requestWorkflow,
			String subCategoryName, Employee loggedInEmployee,
			String reviewRemarks);

	String submitTimesheetEmailByRevForEmp(Long companyId,
			EmployeeTimesheetApplication empTimesheetApplication,
			LionTimesheetApplicationReviewer requestWorkflow,
			String subCategoryName, Employee loggedInEmployee, Employee reviewer);

}
