package com.payasia.common.util;

import java.sql.Timestamp;
import java.util.Comparator;

import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.RecentActivityDTO;

public class RecentActivityComparison implements Comparator<RecentActivityDTO> {

	public String sortParam;
	public String paramName;
	
	public RecentActivityComparison() {

	}
	
	public RecentActivityComparison(String sortParam, String paramName) {
		super();
		this.sortParam = sortParam;
		this.paramName = paramName;
	}
	
	@Override
	public int compare(RecentActivityDTO obj1, RecentActivityDTO obj2) {
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
			var = obj1.getCreatedDate().compareTo(obj2.getCreatedDate());
			break;
		
		case "updatedDate":
			var = obj1.getUpdatedDate().compareTo(obj2.getUpdatedDate());
			break;
			
		case "createdBy":
			var = obj1.getCreatedBy().compareToIgnoreCase(obj2.getCreatedBy());
			break;
		
		case "activityType":
			var = obj1.getActivityType().compareToIgnoreCase(obj2.getActivityType());
			break;
	
		case "leaveType":
			var = obj1.getLeaveType().compareToIgnoreCase(obj2.getLeaveType());
			break;
		}
		
		if(this.sortParam.equalsIgnoreCase("DESC")){
			return (-1*var);
		}
		return var;
	}
}

