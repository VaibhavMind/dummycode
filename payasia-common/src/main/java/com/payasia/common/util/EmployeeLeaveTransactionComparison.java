package com.payasia.common.util;

import java.sql.Timestamp;
import java.util.Comparator;

import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.EmployeeLeaveDTO;

public class EmployeeLeaveTransactionComparison implements Comparator<EmployeeLeaveDTO> {

	public String sortParam;
	public String paramName;
	
	public EmployeeLeaveTransactionComparison() {

	}
	
	public EmployeeLeaveTransactionComparison(String sortParam, String paramName) {
		super();
		this.sortParam = sortParam;
		this.paramName = paramName;
	}
	
	@Override
	public int compare(EmployeeLeaveDTO obj1, EmployeeLeaveDTO obj2) {
		int var = 0;
		Timestamp t1;
		Timestamp t2;
		
		switch(this.paramName) {
  		case "fromDate":
  			t1 = DateUtils.stringToTimestamp(obj1.getFromDate(), UserContext.getWorkingCompanyDateFormat());
			t2 = DateUtils.stringToTimestamp(obj2.getFromDate(), UserContext.getWorkingCompanyDateFormat());
			var = t1.compareTo(t2);
  			break;

		case "toDate":
			t1 = DateUtils.stringToTimestamp(obj1.getToDate(), UserContext.getWorkingCompanyDateFormat());
			t2 = DateUtils.stringToTimestamp(obj2.getToDate(), UserContext.getWorkingCompanyDateFormat());
			var = t1.compareTo(t2);
			break;
			
		case "leaveTypeName":
			var = obj1.getLeaveTypeName().compareToIgnoreCase(obj2.getLeaveTypeName());
			break;
			
		case "leaveStatus":
			var = obj1.getLeaveStatus().compareToIgnoreCase(obj2.getLeaveStatus());
			break;
			
		case "noOfDays":
			var = obj1.getNoOfDays().compareTo(obj2.getNoOfDays());
			break;
		
		case "employeeName":
			var = obj1.getEmployeeName().compareToIgnoreCase(obj2.getEmployeeName());
			break;
			
		case "employeeNumber":
			var = obj1.getEmployeeNumber().compareToIgnoreCase(obj2.getEmployeeNumber());
			break;
		}
		
		if(this.sortParam.equalsIgnoreCase("DESC")){
			return (-1*var);
		}
		return var;
	}

}


