package com.payasia.common.dto;

import java.io.Serializable;
import java.util.List;

public class CoherentTimesheetReportDTO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<String> employeeNumbers;
	private String companyName;
	private List<String> dataDictNameList ;
	
	private List<LeaveReportHeaderDTO> timesheetHeaderDTOs;
	private List<CoherentOvertimeReportDataDTO> coherentOvertimeReportDTOs;
	private List<LeaveReportCustomDataDTO> leaveCustomDataDTOs;
	private int totalEmployeesCount;
	
	private String fromBatchDate;
	private String toBatchDate;
	
	private Long fromBatchId;
	private Long toBatchId;
	
	private String fileBatchName;
	private List<CoherentShiftReportDataDTO> coherentShjiftReportDTOs;
	public List<CoherentShiftReportDataDTO> getCoherentShjiftReportDTOs() {
		return coherentShjiftReportDTOs;
	}
	public void setCoherentShjiftReportDTOs(
			List<CoherentShiftReportDataDTO> coherentShjiftReportDTOs) {
		this.coherentShjiftReportDTOs = coherentShjiftReportDTOs;
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
	public List<LeaveReportHeaderDTO> getTimesheetHeaderDTOs() {
		return timesheetHeaderDTOs;
	}
	public void setTimesheetHeaderDTOs(
			List<LeaveReportHeaderDTO> timesheetHeaderDTOs) {
		this.timesheetHeaderDTOs = timesheetHeaderDTOs;
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
	public List<CoherentOvertimeReportDataDTO> getCoherentOvertimeReportDTOs() {
		return coherentOvertimeReportDTOs;
	}
	public void setCoherentOvertimeReportDTOs(
			List<CoherentOvertimeReportDataDTO> coherentOvertimeReportDTOs) {
		this.coherentOvertimeReportDTOs = coherentOvertimeReportDTOs;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
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
	public String getFileBatchName() {
		return fileBatchName;
	}
	public void setFileBatchName(String fileBatchName) {
		this.fileBatchName = fileBatchName;
	}
	public Long getFromBatchId() {
		return fromBatchId;
	}
	public void setFromBatchId(Long fromBatchId) {
		this.fromBatchId = fromBatchId;
	}
	public Long getToBatchId() {
		return toBatchId;
	}
	public void setToBatchId(Long toBatchId) {
		this.toBatchId = toBatchId;
	}
	
	
	
	

}
