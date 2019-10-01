package com.payasia.common.form;

import java.util.List;

import com.payasia.common.dto.EmployeeLeaveSchemeTypeDTO;
import com.payasia.common.dto.EmployeeLeaveSchemeTypeHistoryDTO;

public class AssignClaimSchemeResponse extends PageResponse {

	private List<AssignLeaveSchemeForm> assignLeaveSchemeList;

	private List<EmployeeListForm> searchEmployeeList;

	private List<EmployeeLeaveDistributionForm> empLeaveDistriList;

	private List<EmployeeLeaveSchemeTypeDTO> empLeaveSchemeTypeList;
	private List<EmployeeLeaveSchemeTypeHistoryDTO> empLeaveSchemeTypeHistoryList;

	public List<AssignLeaveSchemeForm> getAssignLeaveSchemeList() {
		return assignLeaveSchemeList;
	}

	public void setAssignLeaveSchemeList(
			List<AssignLeaveSchemeForm> assignLeaveSchemeList) {
		this.assignLeaveSchemeList = assignLeaveSchemeList;
	}

	public List<EmployeeListForm> getSearchEmployeeList() {
		return searchEmployeeList;
	}

	public void setSearchEmployeeList(List<EmployeeListForm> searchEmployeeList) {
		this.searchEmployeeList = searchEmployeeList;
	}

	public List<EmployeeLeaveDistributionForm> getEmpLeaveDistriList() {
		return empLeaveDistriList;
	}

	public void setEmpLeaveDistriList(
			List<EmployeeLeaveDistributionForm> empLeaveDistriList) {
		this.empLeaveDistriList = empLeaveDistriList;
	}

	public List<EmployeeLeaveSchemeTypeDTO> getEmpLeaveSchemeTypeList() {
		return empLeaveSchemeTypeList;
	}

	public void setEmpLeaveSchemeTypeList(
			List<EmployeeLeaveSchemeTypeDTO> empLeaveSchemeTypeList) {
		this.empLeaveSchemeTypeList = empLeaveSchemeTypeList;
	}

	public List<EmployeeLeaveSchemeTypeHistoryDTO> getEmpLeaveSchemeTypeHistoryList() {
		return empLeaveSchemeTypeHistoryList;
	}

	public void setEmpLeaveSchemeTypeHistoryList(
			List<EmployeeLeaveSchemeTypeHistoryDTO> empLeaveSchemeTypeHistoryList) {
		this.empLeaveSchemeTypeHistoryList = empLeaveSchemeTypeHistoryList;
	}

}
