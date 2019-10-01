package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

import com.payasia.common.dto.CompanyDTO;
import com.payasia.common.dto.LeaveSchemeDTO;
import com.payasia.common.dto.LeaveTypeDTO;

public class YearEndProcessFilterForm implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5570091904826484734L;
	
	private List<LeaveSchemeDTO> leaveSchemeDTOs;
	private List<LeaveTypeDTO> leaveTypeDTOs;
	private List<CompanyGroupForm> companyGroupList;
	private List<CompanyDTO> companyList;
	public List<LeaveSchemeDTO> getLeaveSchemeDTOs() {
		return leaveSchemeDTOs;
	}
	public void setLeaveSchemeDTOs(List<LeaveSchemeDTO> leaveSchemeDTOs) {
		this.leaveSchemeDTOs = leaveSchemeDTOs;
	}
	public List<LeaveTypeDTO> getLeaveTypeDTOs() {
		return leaveTypeDTOs;
	}
	public void setLeaveTypeDTOs(List<LeaveTypeDTO> leaveTypeDTOs) {
		this.leaveTypeDTOs = leaveTypeDTOs;
	}
	public List<CompanyGroupForm> getCompanyGroupList() {
		return companyGroupList;
	}
	public void setCompanyGroupList(List<CompanyGroupForm> companyGroupList) {
		this.companyGroupList = companyGroupList;
	}
	public List<CompanyDTO> getCompanyList() {
		return companyList;
	}
	public void setCompanyList(List<CompanyDTO> companyList) {
		this.companyList = companyList;
	}
	
	

}
