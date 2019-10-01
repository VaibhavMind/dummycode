package com.payasia.common.dto;

import java.io.Serializable;
import java.util.List;

public class HRISReportDTO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<String> companyCodeList;
	private List<PayAsiaCompanyStatisticReportDTO> hrisHeadCountEmpDataList;
	private String createdBy;
	private String createdOn;
	
	
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}
	public List<String> getCompanyCodeList() {
		return companyCodeList;
	}
	public void setCompanyCodeList(List<String> companyCodeList) {
		this.companyCodeList = companyCodeList;
	}
	public List<PayAsiaCompanyStatisticReportDTO> getHrisHeadCountEmpDataList() {
		return hrisHeadCountEmpDataList;
	}
	public void setHrisHeadCountEmpDataList(
			List<PayAsiaCompanyStatisticReportDTO> hrisHeadCountEmpDataList) {
		this.hrisHeadCountEmpDataList = hrisHeadCountEmpDataList;
	}
	
	

}
