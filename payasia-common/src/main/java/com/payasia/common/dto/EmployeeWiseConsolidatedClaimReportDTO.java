package com.payasia.common.dto;

import java.io.Serializable;
import java.util.List;

public class EmployeeWiseConsolidatedClaimReportDTO implements Serializable {
	
	private List<ClaimReportHeaderDTO> claimHeaderDTOs;
	private List<EmployeeWiseConsolidatedClaimReportDataDTO> empWiseclaimDataDTOs;
	private List<String> dataDictNameList ;
	private List<ClaimDetailsReportCustomDataDTO> claimDetailsCustomDataDTOs;
	
	public List<ClaimReportHeaderDTO> getClaimHeaderDTOs() {
		return claimHeaderDTOs;
	}
	public void setClaimHeaderDTOs(List<ClaimReportHeaderDTO> claimHeaderDTOs) {
		this.claimHeaderDTOs = claimHeaderDTOs;
	}
	public List<EmployeeWiseConsolidatedClaimReportDataDTO> getEmpWiseclaimDataDTOs() {
		return empWiseclaimDataDTOs;
	}
	public void setEmpWiseclaimDataDTOs(List<EmployeeWiseConsolidatedClaimReportDataDTO> empWiseclaimDataDTOs) {
		this.empWiseclaimDataDTOs = empWiseclaimDataDTOs;
	}
	public List<String> getDataDictNameList() {
		return dataDictNameList;
	}
	public void setDataDictNameList(List<String> dataDictNameList) {
		this.dataDictNameList = dataDictNameList;
	}
	public List<ClaimDetailsReportCustomDataDTO> getClaimDetailsCustomDataDTOs() {
		return claimDetailsCustomDataDTOs;
	}
	public void setClaimDetailsCustomDataDTOs(
			List<ClaimDetailsReportCustomDataDTO> claimDetailsCustomDataDTOs) {
		this.claimDetailsCustomDataDTOs = claimDetailsCustomDataDTOs;
	}
	
	
	
	

}
