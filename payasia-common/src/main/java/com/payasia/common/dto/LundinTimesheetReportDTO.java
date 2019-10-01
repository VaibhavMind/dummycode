package com.payasia.common.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LundinTimesheetReportDTO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<LundinTimewritingReportDTO> lundinTimewritingReportDTOList;
	private List<String> employeeNumbers;
	private List<String> dataDictNameList ;
	private HashMap<String, LundinTimewritingReportDTO> deptCodeTechnicalTimewritingDTOMap;
	private HashMap<String, LundinTimewritingReportDTO> deptCodeNonTechnicalTimewritingDTOMap;
	private HashMap<String, List<String>> empListDepartmentWiseMap;
	private HashMap<String, Double> empTimesheetKeyValueMap;
	private List<LundinTimewritingDeptReportDTO> totalTechnicalManHoursDTOList;
	private List<LundinTimewritingDeptReportDTO> totalTechnicalManHoursWithEffecAllocDTOList;
	private HashMap<String, Double> totalTechnicalManHoursValMap;
	private HashMap<String, Double> totalTechnicalManHoursWithEffecAllocMap;
	private Map<String, String> nonTechEffecAllocByDeptMap ;
	private Map<String, String> nonTechFinalEffecAllocByDeptMap ;
	private List<String> employeeColCountList ;
	private Map<String, String> nonTechTotalFinalEffecAllocMap;
	private Map<String, Double> malyasianOpernTechMap;
	private List<LundinTimewritingDeptReportDTO> blockAfeNTOFCDTOList;
	private Map<String, Double> ntofcTotalDaysValMap;
	private Map<String, String> ntofcEffecAllocMap ;
	private Map<String, String> ntofcFinalEffecAllocMap ;
	private String ntofcFinalEffecAllocTotal;
	private String ntofcDepartmentName;
	private String ntofcDepartmentCode;
	
	private List<LeaveReportHeaderDTO> lundinHeaderDTOs;
	private List<LundinDailyPaidTimesheetDTO> lundinDailyPaidTimesheetDTOs;
	private List<LundinTimesheetStatusReportDTO> lundinTimesheetStatusReportDTOs;
	private List<LeaveReportCustomDataDTO> leaveCustomDataDTOs;
	private int totalEmployeesCount;
	private int totalEmployeesApprovedTimesheetCount;
	private int totalEmployeesPendingTimesheetCount;
	
	public List<LundinTimewritingReportDTO> getLundinTimewritingReportDTOList() {
		return lundinTimewritingReportDTOList;
	}
	public void setLundinTimewritingReportDTOList(
			List<LundinTimewritingReportDTO> lundinTimewritingReportDTOList) {
		this.lundinTimewritingReportDTOList = lundinTimewritingReportDTOList;
	}
	public List<String> getEmployeeNumbers() {
		return employeeNumbers;
	}
	public void setEmployeeNumbers(List<String> employeeNumbers) {
		this.employeeNumbers = employeeNumbers;
	}
	public List<String> getDataDictNameList() {
		return dataDictNameList;
	}
	public void setDataDictNameList(List<String> dataDictNameList) {
		this.dataDictNameList = dataDictNameList;
	}
	public HashMap<String, Double> getEmpTimesheetKeyValueMap() {
		return empTimesheetKeyValueMap;
	}
	public void setEmpTimesheetKeyValueMap(
			HashMap<String, Double> empTimesheetKeyValueMap) {
		this.empTimesheetKeyValueMap = empTimesheetKeyValueMap;
	}
	public HashMap<String, Double> getTotalTechnicalManHoursValMap() {
		return totalTechnicalManHoursValMap;
	}
	public void setTotalTechnicalManHoursValMap(
			HashMap<String, Double> totalTechnicalManHoursValMap) {
		this.totalTechnicalManHoursValMap = totalTechnicalManHoursValMap;
	}
	public List<LundinTimewritingDeptReportDTO> getTotalTechnicalManHoursDTOList() {
		return totalTechnicalManHoursDTOList;
	}
	public void setTotalTechnicalManHoursDTOList(
			List<LundinTimewritingDeptReportDTO> totalTechnicalManHoursDTOList) {
		this.totalTechnicalManHoursDTOList = totalTechnicalManHoursDTOList;
	}
	public HashMap<String, Double> getTotalTechnicalManHoursWithEffecAllocMap() {
		return totalTechnicalManHoursWithEffecAllocMap;
	}
	public void setTotalTechnicalManHoursWithEffecAllocMap(
			HashMap<String, Double> totalTechnicalManHoursWithEffecAllocMap) {
		this.totalTechnicalManHoursWithEffecAllocMap = totalTechnicalManHoursWithEffecAllocMap;
	}
	public List<LundinTimewritingDeptReportDTO> getTotalTechnicalManHoursWithEffecAllocDTOList() {
		return totalTechnicalManHoursWithEffecAllocDTOList;
	}
	public void setTotalTechnicalManHoursWithEffecAllocDTOList(
			List<LundinTimewritingDeptReportDTO> totalTechnicalManHoursWithEffecAllocDTOList) {
		this.totalTechnicalManHoursWithEffecAllocDTOList = totalTechnicalManHoursWithEffecAllocDTOList;
	}
	public HashMap<String, LundinTimewritingReportDTO> getDeptCodeTechnicalTimewritingDTOMap() {
		return deptCodeTechnicalTimewritingDTOMap;
	}
	public void setDeptCodeTechnicalTimewritingDTOMap(
			HashMap<String, LundinTimewritingReportDTO> deptCodeTechnicalTimewritingDTOMap) {
		this.deptCodeTechnicalTimewritingDTOMap = deptCodeTechnicalTimewritingDTOMap;
	}
	public HashMap<String, LundinTimewritingReportDTO> getDeptCodeNonTechnicalTimewritingDTOMap() {
		return deptCodeNonTechnicalTimewritingDTOMap;
	}
	public void setDeptCodeNonTechnicalTimewritingDTOMap(
			HashMap<String, LundinTimewritingReportDTO> deptCodeNonTechnicalTimewritingDTOMap) {
		this.deptCodeNonTechnicalTimewritingDTOMap = deptCodeNonTechnicalTimewritingDTOMap;
	}
	public Map<String, String> getNonTechEffecAllocByDeptMap() {
		return nonTechEffecAllocByDeptMap;
	}
	public void setNonTechEffecAllocByDeptMap(
			Map<String, String> nonTechEffecAllocByDeptMap) {
		this.nonTechEffecAllocByDeptMap = nonTechEffecAllocByDeptMap;
	}
	public Map<String, String> getNonTechFinalEffecAllocByDeptMap() {
		return nonTechFinalEffecAllocByDeptMap;
	}
	public void setNonTechFinalEffecAllocByDeptMap(
			Map<String, String> nonTechFinalEffecAllocByDeptMap) {
		this.nonTechFinalEffecAllocByDeptMap = nonTechFinalEffecAllocByDeptMap;
	}
	public Map<String, String> getNonTechTotalFinalEffecAllocMap() {
		return nonTechTotalFinalEffecAllocMap;
	}
	public void setNonTechTotalFinalEffecAllocMap(
			Map<String, String> nonTechTotalFinalEffecAllocMap) {
		this.nonTechTotalFinalEffecAllocMap = nonTechTotalFinalEffecAllocMap;
	}
	public Map<String, Double> getMalyasianOpernTechMap() {
		return malyasianOpernTechMap;
	}
	public void setMalyasianOpernTechMap(Map<String, Double> malyasianOpernTechMap) {
		this.malyasianOpernTechMap = malyasianOpernTechMap;
	}
	public List<LundinTimewritingDeptReportDTO> getBlockAfeNTOFCDTOList() {
		return blockAfeNTOFCDTOList;
	}
	public void setBlockAfeNTOFCDTOList(
			List<LundinTimewritingDeptReportDTO> blockAfeNTOFCDTOList) {
		this.blockAfeNTOFCDTOList = blockAfeNTOFCDTOList;
	}
	public Map<String, Double> getNtofcTotalDaysValMap() {
		return ntofcTotalDaysValMap;
	}
	public void setNtofcTotalDaysValMap(Map<String, Double> ntofcTotalDaysValMap) {
		this.ntofcTotalDaysValMap = ntofcTotalDaysValMap;
	}
	public Map<String, String> getNtofcEffecAllocMap() {
		return ntofcEffecAllocMap;
	}
	public void setNtofcEffecAllocMap(Map<String, String> ntofcEffecAllocMap) {
		this.ntofcEffecAllocMap = ntofcEffecAllocMap;
	}
	public Map<String, String> getNtofcFinalEffecAllocMap() {
		return ntofcFinalEffecAllocMap;
	}
	public void setNtofcFinalEffecAllocMap(
			Map<String, String> ntofcFinalEffecAllocMap) {
		this.ntofcFinalEffecAllocMap = ntofcFinalEffecAllocMap;
	}
	public String getNtofcFinalEffecAllocTotal() {
		return ntofcFinalEffecAllocTotal;
	}
	public void setNtofcFinalEffecAllocTotal(String ntofcFinalEffecAllocTotal) {
		this.ntofcFinalEffecAllocTotal = ntofcFinalEffecAllocTotal;
	}
	public List<String> getEmployeeColCountList() {
		return employeeColCountList;
	}
	public void setEmployeeColCountList(List<String> employeeColCountList) {
		this.employeeColCountList = employeeColCountList;
	}
	public String getNtofcDepartmentName() {
		return ntofcDepartmentName;
	}
	public void setNtofcDepartmentName(String ntofcDepartmentName) {
		this.ntofcDepartmentName = ntofcDepartmentName;
	}
	public String getNtofcDepartmentCode() {
		return ntofcDepartmentCode;
	}
	public void setNtofcDepartmentCode(String ntofcDepartmentCode) {
		this.ntofcDepartmentCode = ntofcDepartmentCode;
	}
	public HashMap<String, List<String>> getEmpListDepartmentWiseMap() {
		return empListDepartmentWiseMap;
	}
	public void setEmpListDepartmentWiseMap(
			HashMap<String, List<String>> empListDepartmentWiseMap) {
		this.empListDepartmentWiseMap = empListDepartmentWiseMap;
	}
	public List<LundinDailyPaidTimesheetDTO> getLundinDailyPaidTimesheetDTOs() {
		return lundinDailyPaidTimesheetDTOs;
	}
	public void setLundinDailyPaidTimesheetDTOs(
			List<LundinDailyPaidTimesheetDTO> lundinDailyPaidTimesheetDTOs) {
		this.lundinDailyPaidTimesheetDTOs = lundinDailyPaidTimesheetDTOs;
	}
	public List<LeaveReportHeaderDTO> getLundinHeaderDTOs() {
		return lundinHeaderDTOs;
	}
	public void setLundinHeaderDTOs(List<LeaveReportHeaderDTO> lundinHeaderDTOs) {
		this.lundinHeaderDTOs = lundinHeaderDTOs;
	}
	public List<LeaveReportCustomDataDTO> getLeaveCustomDataDTOs() {
		return leaveCustomDataDTOs;
	}
	public void setLeaveCustomDataDTOs(
			List<LeaveReportCustomDataDTO> leaveCustomDataDTOs) {
		this.leaveCustomDataDTOs = leaveCustomDataDTOs;
	}
	public int getTotalEmployeesCount() {
		return totalEmployeesCount;
	}
	public void setTotalEmployeesCount(int totalEmployeesCount) {
		this.totalEmployeesCount = totalEmployeesCount;
	}
	public List<LundinTimesheetStatusReportDTO> getLundinTimesheetStatusReportDTOs() {
		return lundinTimesheetStatusReportDTOs;
	}
	public void setLundinTimesheetStatusReportDTOs(
			List<LundinTimesheetStatusReportDTO> lundinTimesheetStatusReportDTOs) {
		this.lundinTimesheetStatusReportDTOs = lundinTimesheetStatusReportDTOs;
	}
	public int getTotalEmployeesApprovedTimesheetCount() {
		return totalEmployeesApprovedTimesheetCount;
	}
	public void setTotalEmployeesApprovedTimesheetCount(
			int totalEmployeesApprovedTimesheetCount) {
		this.totalEmployeesApprovedTimesheetCount = totalEmployeesApprovedTimesheetCount;
	}
	public int getTotalEmployeesPendingTimesheetCount() {
		return totalEmployeesPendingTimesheetCount;
	}
	public void setTotalEmployeesPendingTimesheetCount(
			int totalEmployeesPendingTimesheetCount) {
		this.totalEmployeesPendingTimesheetCount = totalEmployeesPendingTimesheetCount;
	}
	

}
