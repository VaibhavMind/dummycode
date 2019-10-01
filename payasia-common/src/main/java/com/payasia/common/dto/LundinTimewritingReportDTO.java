package com.payasia.common.dto;

import java.io.Serializable;
import java.util.List;

public class LundinTimewritingReportDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4217120984052116720L;
	
	private Long departmentId;
	private String departmentCode;
	private String departmentName;
	private int displayOrder;
	private String departmentType;
	private List<LundinTimewritingDeptReportDTO> timewritingDeptReportDTOList;
	private List<String> employeeNumbers;
	
	private String fileBatchName;
	private String fromBatchDate;
	private String toBatchDate;
	private String defaultBlockCode;
	private String defaultBlockName;
	private boolean isDefaultBlockEffectiveAllocation;
	private Long defaultBlockId;
	private Long defaultAFEId;
	private String defaultAFEName;
	private boolean isAutoTimewrite;
	private String employeeNumber;
	private String firstName;
	private String lastName;
	private boolean dailyPaidEmployee;
	
	public Long getDepartmentId() {
		return departmentId;
	}
	public void setDepartmentId(Long departmentId) {
		this.departmentId = departmentId;
	}
	public String getDepartmentCode() {
		return departmentCode;
	}
	public void setDepartmentCode(String departmentCode) {
		this.departmentCode = departmentCode;
	}
	public String getDepartmentName() {
		return departmentName;
	}
	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}
	public String getDepartmentType() {
		return departmentType;
	}
	public void setDepartmentType(String departmentType) {
		this.departmentType = departmentType;
	}
	public int getDisplayOrder() {
		return displayOrder;
	}
	public void setDisplayOrder(int displayOrder) {
		this.displayOrder = displayOrder;
	}
	public List<LundinTimewritingDeptReportDTO> getTimewritingDeptReportDTOList() {
		return timewritingDeptReportDTOList;
	}
	public void setTimewritingDeptReportDTOList(
			List<LundinTimewritingDeptReportDTO> timewritingDeptReportDTOList) {
		this.timewritingDeptReportDTOList = timewritingDeptReportDTOList;
	}
	public List<String> getEmployeeNumbers() {
		return employeeNumbers;
	}
	public void setEmployeeNumbers(List<String> employeeNumbers) {
		this.employeeNumbers = employeeNumbers;
	}
	public String getFileBatchName() {
		return fileBatchName;
	}
	public void setFileBatchName(String fileBatchName) {
		this.fileBatchName = fileBatchName;
	}
	public String getFromBatchDate() {
		return fromBatchDate;
	}
	public void setFromBatchDate(String fromBatchDate) {
		this.fromBatchDate = fromBatchDate;
	}
	public String getToBatchDate() {
		return toBatchDate;
	}
	public void setToBatchDate(String toBatchDate) {
		this.toBatchDate = toBatchDate;
	}
	public String getDefaultBlockCode() {
		return defaultBlockCode;
	}
	public void setDefaultBlockCode(String defaultBlockCode) {
		this.defaultBlockCode = defaultBlockCode;
	}
	public String getDefaultBlockName() {
		return defaultBlockName;
	}
	public void setDefaultBlockName(String defaultBlockName) {
		this.defaultBlockName = defaultBlockName;
	}
	public Long getDefaultBlockId() {
		return defaultBlockId;
	}
	public void setDefaultBlockId(Long defaultBlockId) {
		this.defaultBlockId = defaultBlockId;
	}
	public Long getDefaultAFEId() {
		return defaultAFEId;
	}
	public void setDefaultAFEId(Long defaultAFEId) {
		this.defaultAFEId = defaultAFEId;
	}
	public String getDefaultAFEName() {
		return defaultAFEName;
	}
	public void setDefaultAFEName(String defaultAFEName) {
		this.defaultAFEName = defaultAFEName;
	}
	public boolean isDefaultBlockEffectiveAllocation() {
		return isDefaultBlockEffectiveAllocation;
	}
	public void setDefaultBlockEffectiveAllocation(
			boolean isDefaultBlockEffectiveAllocation) {
		this.isDefaultBlockEffectiveAllocation = isDefaultBlockEffectiveAllocation;
	}
	public boolean isAutoTimewrite() {
		return isAutoTimewrite;
	}
	public void setAutoTimewrite(boolean isAutoTimewrite) {
		this.isAutoTimewrite = isAutoTimewrite;
	}
	public String getEmployeeNumber() {
		return employeeNumber;
	}
	public void setEmployeeNumber(String employeeNumber) {
		this.employeeNumber = employeeNumber;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public boolean isDailyPaidEmployee() {
		return dailyPaidEmployee;
	}
	public void setDailyPaidEmployee(boolean dailyPaidEmployee) {
		this.dailyPaidEmployee = dailyPaidEmployee;
	}
	
	
	
	

	
}
