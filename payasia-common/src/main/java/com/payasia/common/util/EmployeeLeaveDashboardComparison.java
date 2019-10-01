package com.payasia.common.util;

import java.sql.Timestamp;
import java.util.Comparator;

import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.EmployeeLeaveSchemeTypeDTO;

public class EmployeeLeaveDashboardComparison implements Comparator<EmployeeLeaveSchemeTypeDTO> {

	public String sortParam;
	public String paramName;
	
	public EmployeeLeaveDashboardComparison() {

	}
	
	public EmployeeLeaveDashboardComparison(String sortParam, String paramName) {
		super();
		this.sortParam = sortParam;
		this.paramName = paramName!=null ? paramName :"";
	}
	
	@Override
	public int compare(EmployeeLeaveSchemeTypeDTO obj1, EmployeeLeaveSchemeTypeDTO obj2) {
		int var = 0;
		Timestamp t1;
		Timestamp t2;
		
		switch(this.paramName) {
		case "createDate":
			t1 = DateUtils.stringToTimestamp(obj1.getCreatedDate(), UserContext.getWorkingCompanyDateFormat());
			t2 = DateUtils.stringToTimestamp(obj2.getCreatedDate(), UserContext.getWorkingCompanyDateFormat());
			var = t1.compareTo(t2);
			break;
			
  		case "fromDate":
  			t1 = DateUtils.stringToTimestamp(obj1.getStartDate(), UserContext.getWorkingCompanyDateFormat());
			t2 = DateUtils.stringToTimestamp(obj2.getStartDate(), UserContext.getWorkingCompanyDateFormat());
			var = t1.compareTo(t2);
  			break;

  			
  		case "startDate":
  			t1 = DateUtils.stringToTimestamp(obj1.getStartDate(), UserContext.getWorkingCompanyDateFormat());
			t2 = DateUtils.stringToTimestamp(obj2.getStartDate(), UserContext.getWorkingCompanyDateFormat());
			var = t1.compareTo(t2);
  			break;

		case "toDate":
			t1 = DateUtils.stringToTimestamp(obj1.getEndDate(), UserContext.getWorkingCompanyDateFormat());
			t2 = DateUtils.stringToTimestamp(obj2.getEndDate(), UserContext.getWorkingCompanyDateFormat());
			var = t1.compareTo(t2);
			break;
			

		case "endDate":
			t1 = DateUtils.stringToTimestamp(obj1.getEndDate(), UserContext.getWorkingCompanyDateFormat());
			t2 = DateUtils.stringToTimestamp(obj2.getEndDate(), UserContext.getWorkingCompanyDateFormat());
			var = t1.compareTo(t2);
			break;
			
		case "leaveType":
			var = obj1.getLeaveType().compareToIgnoreCase(obj2.getLeaveType());
			break;
			
		case "leaveScheme":
			var = obj1.getLeaveSchemeName().compareToIgnoreCase(obj2.getLeaveSchemeName());
			break;
			
		case "fromSession":
			var = obj1.getStartSession().compareToIgnoreCase(obj2.getStartSession());
			break;
			
		case "toSession":
			var = obj1.getEndSession().compareToIgnoreCase(obj2.getEndSession());
			break;
			
		case "noOfDays":
			var = obj1.getNoOfDays().compareToIgnoreCase(obj2.getNoOfDays());
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

