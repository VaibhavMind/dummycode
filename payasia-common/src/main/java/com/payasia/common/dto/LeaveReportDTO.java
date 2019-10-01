package com.payasia.common.dto;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class LeaveReportDTO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<LeaveReportHeaderDTO> leaveHeaderDTOs;
	private List<LeaveReportDataDTO> leaveDataDTOs;
	private List<LeaveTranReportDTO> leaveTranReportDTOs;
	private List<String> leaveTypeNames;
	private List<YearWiseSummarryDTO> summarryDTOs;
	private List<LeaveBalAsOnDayDTO> leaveBalAsOnDayDTOs;
	private List<LeaveReviewerReportDTO> leaveReviewerReportDTOs;
	private List<String> dataDictNameList ;
	private List<LeaveReportCustomDataDTO> leaveCustomDataDTOs;
	private byte[] leaveBalAsOnDayCustReportByteFile;
	private Map<String,List<DayWiseLeaveTranReportDTO>> dayWiseLeaveTranMap;
	private List<String> companyCodeList;
	private Map<String,List<EmployeeHeadCountReportDTO>> leaveHeadCountEmpDataListMap;
	private Map<String,List<EmployeeHeadCountReportDTO>> leaveHeadCountHeaderDTOMap;
	private boolean leavePreferencePreApproval;
	private boolean leaveExtensionPreference;
	
	
	
	public boolean isLeavePreferencePreApproval() {
		return leavePreferencePreApproval;
	}
	public void setLeavePreferencePreApproval(boolean leavePreferencePreApproval) {
		this.leavePreferencePreApproval = leavePreferencePreApproval;
	}
	public boolean isLeaveExtensionPreference() {
		return leaveExtensionPreference;
	}
	public void setLeaveExtensionPreference(boolean leaveExtensionPreference) {
		this.leaveExtensionPreference = leaveExtensionPreference;
	}
	public List<YearWiseSummarryDTO> getSummarryDTOs() {
		return summarryDTOs;
	}
	public void setSummarryDTOs(List<YearWiseSummarryDTO> summarryDTOs) {
		this.summarryDTOs = summarryDTOs;
	}
	public List<String> getLeaveTypeNames() {
		return leaveTypeNames;
	}
	public void setLeaveTypeNames(List<String> leaveTypeNames) {
		this.leaveTypeNames = leaveTypeNames;
	}
	public List<LeaveReportHeaderDTO> getLeaveHeaderDTOs() {
		return leaveHeaderDTOs;
	}
	public void setLeaveHeaderDTOs(List<LeaveReportHeaderDTO> leaveHeaderDTOs) {
		this.leaveHeaderDTOs = leaveHeaderDTOs;
	}
	public List<LeaveReportDataDTO> getLeaveDataDTOs() {
		return leaveDataDTOs;
	}
	public void setLeaveDataDTOs(List<LeaveReportDataDTO> leaveDataDTOs) {
		this.leaveDataDTOs = leaveDataDTOs;
	}
	public List<LeaveTranReportDTO> getLeaveTranReportDTOs() {
		return leaveTranReportDTOs;
	}
	public void setLeaveTranReportDTOs(List<LeaveTranReportDTO> leaveTranReportDTOs) {
		this.leaveTranReportDTOs = leaveTranReportDTOs;
	}
	public List<LeaveBalAsOnDayDTO> getLeaveBalAsOnDayDTOs() {
		return leaveBalAsOnDayDTOs;
	}
	public void setLeaveBalAsOnDayDTOs(List<LeaveBalAsOnDayDTO> leaveBalAsOnDayDTOs) {
		this.leaveBalAsOnDayDTOs = leaveBalAsOnDayDTOs;
	}
	public List<LeaveReviewerReportDTO> getLeaveReviewerReportDTOs() {
		return leaveReviewerReportDTOs;
	}
	public void setLeaveReviewerReportDTOs(
			List<LeaveReviewerReportDTO> leaveReviewerReportDTOs) {
		this.leaveReviewerReportDTOs = leaveReviewerReportDTOs;
	}
	public List<String> getDataDictNameList() {
		return dataDictNameList;
	}
	public void setDataDictNameList(List<String> dataDictNameList) {
		this.dataDictNameList = dataDictNameList;
	}
	public byte[] getLeaveBalAsOnDayCustReportByteFile() {
		return leaveBalAsOnDayCustReportByteFile;
	}
	public void setLeaveBalAsOnDayCustReportByteFile(
			byte[] leaveBalAsOnDayCustReportByteFile) {
		if (leaveBalAsOnDayCustReportByteFile != null) {
			this.leaveBalAsOnDayCustReportByteFile = Arrays.copyOf(leaveBalAsOnDayCustReportByteFile, leaveBalAsOnDayCustReportByteFile.length);
		}
	}
	public List<LeaveReportCustomDataDTO> getLeaveCustomDataDTOs() {
		return leaveCustomDataDTOs;
	}
	public void setLeaveCustomDataDTOs(
			List<LeaveReportCustomDataDTO> leaveCustomDataDTOs) {
		this.leaveCustomDataDTOs = leaveCustomDataDTOs;
	}
	public Map<String, List<DayWiseLeaveTranReportDTO>> getDayWiseLeaveTranMap() {
		return dayWiseLeaveTranMap;
	}
	public void setDayWiseLeaveTranMap(
			Map<String, List<DayWiseLeaveTranReportDTO>> dayWiseLeaveTranMap) {
		this.dayWiseLeaveTranMap = dayWiseLeaveTranMap;
	}
	public List<String> getCompanyCodeList() {
		return companyCodeList;
	}
	public void setCompanyCodeList(List<String> companyCodeList) {
		this.companyCodeList = companyCodeList;
	}
	public Map<String, List<EmployeeHeadCountReportDTO>> getLeaveHeadCountEmpDataListMap() {
		return leaveHeadCountEmpDataListMap;
	}
	public void setLeaveHeadCountEmpDataListMap(
			Map<String, List<EmployeeHeadCountReportDTO>> leaveHeadCountEmpDataListMap) {
		this.leaveHeadCountEmpDataListMap = leaveHeadCountEmpDataListMap;
	}
	public Map<String, List<EmployeeHeadCountReportDTO>> getLeaveHeadCountHeaderDTOMap() {
		return leaveHeadCountHeaderDTOMap;
	}
	public void setLeaveHeadCountHeaderDTOMap(
			Map<String, List<EmployeeHeadCountReportDTO>> leaveHeadCountHeaderDTOMap) {
		this.leaveHeadCountHeaderDTOMap = leaveHeadCountHeaderDTOMap;
	}
	
	
	
	
	
	
	

}
