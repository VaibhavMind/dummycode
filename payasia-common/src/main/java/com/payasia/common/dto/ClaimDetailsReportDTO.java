package com.payasia.common.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class ClaimDetailsReportDTO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String companyName;
	private List<ClaimReportHeaderDTO> claimHeaderDTOs;
	private List<ClaimDetailsReportDataDTO> claimDetailsDataDTOs;
	private List<ClaimDetailsReportCustomDataDTO> claimDetailsCustomDataDTOs;
	private List<String> dataDictNameList ;
	private List<String> companyCodeList;
	private Map<String,List<EmployeeHeadCountReportDTO>> claimHeadCountEmpDataListMap;
	private Map<String,List<EmployeeHeadCountReportDTO>> claimHeadCountHeaderDTOMap;
	private Map<String,List<ClaimDetailsReportDataDTO>> claimDetailsTranMap;
	private boolean isSubordinateCompanyEmployee;
	
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public Map<String, List<ClaimDetailsReportDataDTO>> getClaimDetailsTranMap() {
		return claimDetailsTranMap;
	}
	public void setClaimDetailsTranMap(
			Map<String, List<ClaimDetailsReportDataDTO>> claimDetailsTranMap) {
		this.claimDetailsTranMap = claimDetailsTranMap;
	}
	public List<ClaimReportHeaderDTO> getClaimHeaderDTOs() {
		return claimHeaderDTOs;
	}
	public void setClaimHeaderDTOs(List<ClaimReportHeaderDTO> claimHeaderDTOs) {
		this.claimHeaderDTOs = claimHeaderDTOs;
	}
	public List<ClaimDetailsReportDataDTO> getClaimDetailsDataDTOs() {
		return claimDetailsDataDTOs;
	}
	public void setClaimDetailsDataDTOs(List<ClaimDetailsReportDataDTO> claimDetailsDataDTOs) {
		this.claimDetailsDataDTOs = claimDetailsDataDTOs;
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
	public List<String> getCompanyCodeList() {
		return companyCodeList;
	}
	public void setCompanyCodeList(List<String> companyCodeList) {
		this.companyCodeList = companyCodeList;
	}
	public Map<String, List<EmployeeHeadCountReportDTO>> getClaimHeadCountEmpDataListMap() {
		return claimHeadCountEmpDataListMap;
	}
	public void setClaimHeadCountEmpDataListMap(
			Map<String, List<EmployeeHeadCountReportDTO>> claimHeadCountEmpDataListMap) {
		this.claimHeadCountEmpDataListMap = claimHeadCountEmpDataListMap;
	}
	public Map<String, List<EmployeeHeadCountReportDTO>> getClaimHeadCountHeaderDTOMap() {
		return claimHeadCountHeaderDTOMap;
	}
	public void setClaimHeadCountHeaderDTOMap(
			Map<String, List<EmployeeHeadCountReportDTO>> claimHeadCountHeaderDTOMap) {
		this.claimHeadCountHeaderDTOMap = claimHeadCountHeaderDTOMap;
	}
	
	public boolean isSubordinateCompanyEmployee() {
		return isSubordinateCompanyEmployee;
	}
	public void setSubordinateCompanyEmployee(boolean isSubordinateCompanyEmployee) {
		this.isSubordinateCompanyEmployee = isSubordinateCompanyEmployee;
	}

	

}
