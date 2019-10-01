package com.payasia.common.util;

import java.util.Comparator;

import com.payasia.common.dto.PayslipDTO;

public class PayslipComparison implements Comparator<PayslipDTO> {
	public String sortParam;
	public String paramName;
	public PayslipComparison() {}
	
	public PayslipComparison(String sortParam,String paramName) {
		this.sortParam=sortParam;
		this.paramName=paramName;
	}

	@Override
	public int compare(PayslipDTO obj1, PayslipDTO obj2) {
	
       int var = 0;		
		
		switch(this.paramName) {
		case "monthId":
			var = obj1.getMonthId().compareTo(obj2.getMonthId());
			break;
  		case "monthName":
  			var = obj1.getMonthId().compareTo(obj2.getMonthId());
  			break;

		case "part":
			var = obj1.getPart().compareTo(obj2.getPart());
			break;
		
		}
		
		if(this.sortParam.equalsIgnoreCase("DESC")){
			return (-1*var);
		}
		return var;
	}
}
