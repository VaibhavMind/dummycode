package com.payasia.common.util;

import java.sql.Timestamp;
import java.util.Comparator;

import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.EmployeeClaimAdjustmentDTO;

public class EmployeeClaimAdjustmentComparison implements Comparator<EmployeeClaimAdjustmentDTO> {
	public String sortParam;
	public String paramName;
	
	public EmployeeClaimAdjustmentComparison() { }

	public EmployeeClaimAdjustmentComparison(String sortParam, String paramName) {
		super();
		this.sortParam = sortParam;
		this.paramName = paramName;
	}

	@Override
	public int compare(EmployeeClaimAdjustmentDTO obj1, EmployeeClaimAdjustmentDTO obj2) {
		
	       int var = 0;		
			
			switch(this.paramName) {
			case "effectiveDate":
				Timestamp t1 = DateUtils.stringToTimestamp(obj1.getEffectiveDate(), UserContext.getWorkingCompanyDateFormat());
				Timestamp t2 = DateUtils.stringToTimestamp(obj2.getEffectiveDate(), UserContext.getWorkingCompanyDateFormat());
				var = t1.compareTo(t2);
				break;
				
	  		case "remarks":
	  			var = obj1.getRemarks().compareToIgnoreCase(obj2.getRemarks());
	  			break;

			case "amount":
				var = obj1.getAmount().compareTo(obj2.getAmount());
				break;
			}
			
			if(this.sortParam.equalsIgnoreCase("DESC")){
				return (-1*var);
			}
			return var;
		}
}
