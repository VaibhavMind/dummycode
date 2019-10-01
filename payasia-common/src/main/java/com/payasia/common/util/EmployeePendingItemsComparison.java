package com.payasia.common.util;

import java.sql.Timestamp;
import java.util.Comparator;

import com.payasia.common.bean.util.UserContext;
import com.payasia.common.form.PendingItemsForm;

public class EmployeePendingItemsComparison implements Comparator<PendingItemsForm> {

	public String sortParam;
	public String paramName;
	
	public EmployeePendingItemsComparison() {

	}
	
	public EmployeePendingItemsComparison(String sortParam, String paramName) {
		super();
		this.sortParam = sortParam;
		this.paramName = paramName;
	}
	
	@Override
	public int compare(PendingItemsForm obj1, PendingItemsForm obj2) {
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
			
		case "createdDate":
			t1 = DateUtils.stringToTimestamp(obj1.getCreatedDate(), UserContext.getWorkingCompanyDateFormat());
			t2 = DateUtils.stringToTimestamp(obj2.getCreatedDate(), UserContext.getWorkingCompanyDateFormat());
			var = t1.compareTo(t2);
			break;
			
		case "createdBy":
			var = obj1.getCreatedBy().compareToIgnoreCase(obj2.getCreatedBy());
			break;
		}
		
		if(this.sortParam.equalsIgnoreCase("DESC")){
			return (-1*var);
		}
		return var;
	}
}

