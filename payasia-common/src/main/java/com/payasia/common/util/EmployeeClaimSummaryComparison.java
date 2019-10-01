package com.payasia.common.util;

import java.util.Comparator;

import com.payasia.common.dto.EmployeeClaimSummaryDTO;

public class EmployeeClaimSummaryComparison implements Comparator<EmployeeClaimSummaryDTO> {
	public String sortParam;
	public String paramName;
	
	public EmployeeClaimSummaryComparison() { }

	public EmployeeClaimSummaryComparison(String sortParam, String paramName) {
		super();
		this.sortParam = sortParam;
		this.paramName = paramName;
	}

	@Override
	public int compare(EmployeeClaimSummaryDTO obj1, EmployeeClaimSummaryDTO obj2) {
		
	       int var = 0;		
			
			switch(this.paramName) {
			case "claimTemplateName":
				var = obj1.getClaimTemplateName().compareToIgnoreCase(obj2.getClaimTemplateName());
				break;
				
	  		case "entitlementDetails":
	  			var = obj1.getEntitlementDetails().compareToIgnoreCase(obj2.getEntitlementDetails());
	  			break;

			case "claimedAmount":
				var = obj1.getClaimedAmount().compareTo(obj2.getClaimedAmount());
				break;

			case "adjustments":
				var = obj1.getAdjustments().compareToIgnoreCase(obj2.getAdjustments());
				break;

			case "balance":
				var = obj1.getBalance().compareTo(obj2.getBalance());
				break;
				
			case "claimItemName":
				var = obj1.getClaimItemName().compareToIgnoreCase(obj2.getClaimItemName());
				break;
				
			case "entitlement":
				var = obj1.getEntitlement().compareTo(obj2.getEntitlement());
				break;
			}
			
			if(this.sortParam.equalsIgnoreCase("DESC")){
				return (-1*var);
			}
			return var;
		}
}
