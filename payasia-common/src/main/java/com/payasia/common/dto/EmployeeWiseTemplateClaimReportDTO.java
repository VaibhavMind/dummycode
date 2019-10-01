package com.payasia.common.dto;

import java.io.Serializable;
import java.util.List;

public class EmployeeWiseTemplateClaimReportDTO implements Serializable {
	
	private List<ClaimReportHeaderDTO> claimHeaderDTOs;
	private List<EmployeeWiseTemplateClaimReportDataDTO> empWiseTemplateclaimDataDTOs;
	private List<ClaimDetailsReportCustomDataDTO> claimDetailsCustomDataDTOs;
	private List<String> dataDictNameList ;
	
	public List<ClaimReportHeaderDTO> getClaimHeaderDTOs() {
		return claimHeaderDTOs;
	}
	public void setClaimHeaderDTOs(List<ClaimReportHeaderDTO> claimHeaderDTOs) {
		this.claimHeaderDTOs = claimHeaderDTOs;
	}
	public List<EmployeeWiseTemplateClaimReportDataDTO> getEmpWiseTemplateclaimDataDTOs() {
		return empWiseTemplateclaimDataDTOs;
	}
	public void setEmpWiseTemplateclaimDataDTOs(
			List<EmployeeWiseTemplateClaimReportDataDTO> empWiseTemplateclaimDataDTOs) {
		this.empWiseTemplateclaimDataDTOs = empWiseTemplateclaimDataDTOs;
	}
	public List<ClaimDetailsReportCustomDataDTO> getClaimDetailsCustomDataDTOs() {
		return claimDetailsCustomDataDTOs;
	}
	public void setClaimDetailsCustomDataDTOs(
			List<ClaimDetailsReportCustomDataDTO> claimDetailsCustomDataDTOs) {
		this.claimDetailsCustomDataDTOs = claimDetailsCustomDataDTOs;
	}
	public List<String> getDataDictNameList() {
		return dataDictNameList;
	}
	public void setDataDictNameList(List<String> dataDictNameList) {
		this.dataDictNameList = dataDictNameList;
	}
	
	
	
	
	

}
