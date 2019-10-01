package com.payasia.common.util;

import java.sql.Timestamp;
import java.util.Comparator;

import com.payasia.common.bean.util.UserContext;
import com.payasia.common.form.AddLeaveForm;

public class EmployeeLeaveComparison implements Comparator<AddLeaveForm> {

	public String sortParam;
	public String paramName;
	
	public EmployeeLeaveComparison() {

	}
	
	public EmployeeLeaveComparison(String sortParam, String paramName) {
		super();
		this.sortParam = sortParam;
		this.paramName = paramName;
	}
	
	@Override
	public int compare(AddLeaveForm obj1, AddLeaveForm obj2) {
		int var = 0;
		Timestamp t1;
		Timestamp t2;
		
		switch(this.paramName) {
		case "createDate":
			t1 = DateUtils.stringToTimestamp(obj1.getCreateDate(), UserContext.getWorkingCompanyDateFormat());
			t2 = DateUtils.stringToTimestamp(obj2.getCreateDate(), UserContext.getWorkingCompanyDateFormat());
			var = t1.compareTo(t2);
			break;
			
		case "requestType":
			var = obj1.getRequestType().compareToIgnoreCase(obj2.getRequestType());
			break;
			
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
			
		case "leavereviewer1":
			var = obj1.getLeaveReviewer1().compareToIgnoreCase(obj2.getLeaveReviewer1());
			break;

		case "leavereviewer2":
			var = obj1.getLeaveReviewer2().compareToIgnoreCase(obj2.getLeaveReviewer2());
			break;
			
		case "leavereviewer3":
			var = obj1.getLeaveReviewer3().compareToIgnoreCase(obj2.getLeaveReviewer3());
			break;
			
		case "leaveType":
			var = obj1.getLeaveType().compareToIgnoreCase(obj2.getLeaveType());
			break;
			
		case "leaveScheme":
			var = obj1.getLeaveScheme().compareToIgnoreCase(obj2.getLeaveScheme());
			break;
			
		case "fromSession":
			var = obj1.getFromSession().compareToIgnoreCase(obj2.getFromSession());
			break;
			
		case "toSession":
			var = obj1.getToSession().compareToIgnoreCase(obj2.getToSession());
			break;
			
		case "noOfDays":
			var = obj1.getNoOfDays().compareTo(obj2.getNoOfDays());
			break;
		}
		
		if(this.sortParam.equalsIgnoreCase("DESC")){
			return (-1*var);
		}
		return var;
	}

}
