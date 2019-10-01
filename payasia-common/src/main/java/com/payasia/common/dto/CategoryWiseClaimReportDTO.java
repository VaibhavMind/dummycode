package com.payasia.common.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class CategoryWiseClaimReportDTO implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Map<String,List<ClaimDetailsReportDataDTO>> claimDetailsTranMap;
	
	public Map<String, List<ClaimDetailsReportDataDTO>> getClaimDetailsTranMap() {
		return claimDetailsTranMap;
	}

	public void setClaimDetailsTranMap(
			Map<String, List<ClaimDetailsReportDataDTO>> claimDetailsTranMap) {
		this.claimDetailsTranMap = claimDetailsTranMap;
	}
     
		
}
