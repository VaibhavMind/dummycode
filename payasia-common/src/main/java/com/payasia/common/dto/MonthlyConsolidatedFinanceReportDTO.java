package com.payasia.common.dto;

import java.io.Serializable;
import java.util.List;

public class MonthlyConsolidatedFinanceReportDTO implements Serializable {
	
	private List<ClaimReportHeaderDTO> claimHeaderDTOs;
	private List<MonthlyConsolidatedFinanceReportDataDTO> montlyConsFinDataDTOs;
	private List<MonthlyConsFinReportDTO> montlyConsFinDataNewDTOs;
	private List<ClaimDetailsReportCustomDataDTO> claimDetailsCustomDataDTOs;
	private List<String> dataDictNameList ;
	private boolean showMonthlyConsFinReportGroupingByEmp;
	
	public List<ClaimReportHeaderDTO> getClaimHeaderDTOs() {
		return claimHeaderDTOs;
	}
	public void setClaimHeaderDTOs(List<ClaimReportHeaderDTO> claimHeaderDTOs) {
		this.claimHeaderDTOs = claimHeaderDTOs;
	}
	public List<MonthlyConsolidatedFinanceReportDataDTO> getMontlyConsFinDataDTOs() {
		return montlyConsFinDataDTOs;
	}
	public void setMontlyConsFinDataDTOs(List<MonthlyConsolidatedFinanceReportDataDTO> montlyConsFinDataDTOs) {
		this.montlyConsFinDataDTOs = montlyConsFinDataDTOs;
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
	public List<MonthlyConsFinReportDTO> getMontlyConsFinDataNewDTOs() {
		return montlyConsFinDataNewDTOs;
	}
	public void setMontlyConsFinDataNewDTOs(
			List<MonthlyConsFinReportDTO> montlyConsFinDataNewDTOs) {
		this.montlyConsFinDataNewDTOs = montlyConsFinDataNewDTOs;
	}
	public boolean isShowMonthlyConsFinReportGroupingByEmp() {
		return showMonthlyConsFinReportGroupingByEmp;
	}
	public void setShowMonthlyConsFinReportGroupingByEmp(
			boolean showMonthlyConsFinReportGroupingByEmp) {
		this.showMonthlyConsFinReportGroupingByEmp = showMonthlyConsFinReportGroupingByEmp;
	}
	
	
	
	
	

}
