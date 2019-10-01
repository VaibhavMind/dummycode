package com.payasia.common.form;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.payasia.common.dto.EmployeeLeaveSchemeTypeDTO;
import com.payasia.common.dto.EmployeeLeaveSchemeTypeHistoryDTO;
import com.payasia.common.dto.LeaveApplicationAttachmentDTO;
import com.payasia.common.dto.LeaveConditionDTO;
import com.payasia.common.dto.LeaveDTO;
import com.payasia.common.dto.PostLeavetransactionDTO;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LeaveBalanceSummaryResponse extends PageResponse {

	private List<EmployeeLeaveSchemeTypeDTO> rows;
	private List<EmployeeLeaveSchemeTypeHistoryDTO> empLeaveSchemeTypeHistoryList;
	private List<LeaveBalanceSummaryForm> leaveBalanceSummaryFormList;
	private LeaveConditionDTO leaveConditionDTO;
	private LeaveDTO leaveDTO;
	private String responseString;
	private PostLeavetransactionDTO postLeavetransactionDTO;
	private EmployeeLeaveSchemeTypeHistoryDTO employeeLeaveSchemeTypeHistoryDTO;
	private List<LeaveApplicationAttachmentDTO> attachmentList;
	private Boolean preApprovalReq;
	private Boolean leaveExtension;
	private Boolean leavePreferencePreApproval;
	private Boolean leaveExtensionPreference;
	private Boolean preApprovalReqFirst;
		
	public Boolean getPreApprovalReqFirst() {
		return preApprovalReqFirst;
	}

	public void setPreApprovalReqFirst(Boolean preApprovalReqFirst) {
		this.preApprovalReqFirst = preApprovalReqFirst;
	}

	public Boolean isPreApprovalReq() {
		return preApprovalReq;
	}

	public Boolean getPreApprovalReq() {
		return preApprovalReq;
	}

	public void setPreApprovalReq(Boolean preApprovalReq) {
		this.preApprovalReq = preApprovalReq;
	}
	
	public Boolean isLeaveExtension() {
		return leaveExtension;
	}

	public Boolean getLeaveExtension() {
		return leaveExtension;
	}

	public void setLeaveExtension(Boolean leaveExtension) {
		this.leaveExtension = leaveExtension;
	}
	
	public Boolean isLeavePreferencePreApproval() {
		return leavePreferencePreApproval;
	}

	public Boolean getLeavePreferencePreApproval() {
		return leavePreferencePreApproval;
	}

	public void setLeavePreferencePreApproval(Boolean leavePreferencePreApproval) {
		this.leavePreferencePreApproval = leavePreferencePreApproval;
	}
	
	public Boolean isLeaveExtensionPreference() {
		return leaveExtensionPreference;
	}

	public Boolean getLeaveExtensionPreference() {
		return leaveExtensionPreference;
	}

	public void setLeaveExtensionPreference(Boolean leaveExtensionPreference) {
		this.leaveExtensionPreference = leaveExtensionPreference;
	}

	public PostLeavetransactionDTO getPostLeavetransactionDTO() {
		return postLeavetransactionDTO;
	}

	public void setPostLeavetransactionDTO(
			PostLeavetransactionDTO postLeavetransactionDTO) {
		this.postLeavetransactionDTO = postLeavetransactionDTO;
	}

	public LeaveDTO getLeaveDTO() {
		return leaveDTO;
	}

	public void setLeaveDTO(LeaveDTO leaveDTO) {
		this.leaveDTO = leaveDTO;
	}

	public LeaveConditionDTO getLeaveConditionDTO() {
		return leaveConditionDTO;
	}

	public void setLeaveConditionDTO(LeaveConditionDTO leaveConditionDTO) {
		this.leaveConditionDTO = leaveConditionDTO;
	}

	/*public List<EmployeeLeaveSchemeTypeDTO> getEmpLeaveSchemeTypeList() {
		return empLeaveSchemeTypeList;
	}

	public void setEmpLeaveSchemeTypeList(
			List<EmployeeLeaveSchemeTypeDTO> empLeaveSchemeTypeList) {
		this.empLeaveSchemeTypeList = empLeaveSchemeTypeList;
	}
*/
	public List<EmployeeLeaveSchemeTypeHistoryDTO> getEmpLeaveSchemeTypeHistoryList() {
		return empLeaveSchemeTypeHistoryList;
	}

	public void setEmpLeaveSchemeTypeHistoryList(
			List<EmployeeLeaveSchemeTypeHistoryDTO> empLeaveSchemeTypeHistoryList) {
		this.empLeaveSchemeTypeHistoryList = empLeaveSchemeTypeHistoryList;
	}


	public String getResponseString() {
		return responseString;
	}

	public void setResponseString(String responseString) {
		this.responseString = responseString;
	}

	public List<LeaveBalanceSummaryForm> getLeaveBalanceSummaryFormList() {
		return leaveBalanceSummaryFormList;
	}

	public void setLeaveBalanceSummaryFormList(
			List<LeaveBalanceSummaryForm> leaveBalanceSummaryFormList) {
		this.leaveBalanceSummaryFormList = leaveBalanceSummaryFormList;
	}

	public EmployeeLeaveSchemeTypeHistoryDTO getEmployeeLeaveSchemeTypeHistoryDTO() {
		return employeeLeaveSchemeTypeHistoryDTO;
	}

	public void setEmployeeLeaveSchemeTypeHistoryDTO(
			EmployeeLeaveSchemeTypeHistoryDTO employeeLeaveSchemeTypeHistoryDTO) {
		this.employeeLeaveSchemeTypeHistoryDTO = employeeLeaveSchemeTypeHistoryDTO;
	}

	public List<LeaveApplicationAttachmentDTO> getAttachmentList() {
		return attachmentList;
	}

	public void setAttachmentList(List<LeaveApplicationAttachmentDTO> attachmentList) {
		this.attachmentList = attachmentList;
	}

	public List<EmployeeLeaveSchemeTypeDTO> getRows() {
		return rows;
	}

	public void setRows(List<EmployeeLeaveSchemeTypeDTO> rows) {
		this.rows = rows;
	}

}
