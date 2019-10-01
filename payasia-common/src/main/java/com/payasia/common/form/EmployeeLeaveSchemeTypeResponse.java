package com.payasia.common.form;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.payasia.common.dto.LeaveCustomFieldDTO;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeLeaveSchemeTypeResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String applyTo;
	private Long leaveReviewerId1;
	private String leaveReviewer1Name;
	private Long leaveReviewerId2;
	private String leaveReviewer2Name;
	private Long leaveReviewerId3;
	private String leaveReviewer3Name;
	private BigDecimal leaveBalance;
	private List<LeaveCustomFieldDTO> leaveCustomFieldDTO;
	private Integer noOfCustomFields;
	private String instruction;
	private Map<Long,String> employeeLeaveReviewerMap;
	public Map<Long, String> getEmployeeLeaveReviewerMap() {
		return employeeLeaveReviewerMap;
	}

	public void setEmployeeLeaveReviewerMap(Map<Long, String> employeeLeaveReviewerMap) {
		this.employeeLeaveReviewerMap = employeeLeaveReviewerMap;
	}

	public String getApplyTo() {
		return applyTo;
	}

	public void setApplyTo(String applyTo) {
		this.applyTo = applyTo;
	}

	public Long getLeaveReviewerId1() {
		return leaveReviewerId1;
	}

	public void setLeaveReviewerId1(Long leaveReviewerId1) {
		this.leaveReviewerId1 = leaveReviewerId1;
	}

	public String getLeaveReviewer1Name() {
		return leaveReviewer1Name;
	}

	public void setLeaveReviewer1Name(String leaveReviewer1Name) {
		this.leaveReviewer1Name = leaveReviewer1Name;
	}

	public Long getLeaveReviewerId2() {
		return leaveReviewerId2;
	}

	public void setLeaveReviewerId2(Long leaveReviewerId2) {
		this.leaveReviewerId2 = leaveReviewerId2;
	}

	public String getLeaveReviewer2Name() {
		return leaveReviewer2Name;
	}

	public void setLeaveReviewer2Name(String leaveReviewer2Name) {
		this.leaveReviewer2Name = leaveReviewer2Name;
	}

	public Long getLeaveReviewerId3() {
		return leaveReviewerId3;
	}

	public void setLeaveReviewerId3(Long leaveReviewerId3) {
		this.leaveReviewerId3 = leaveReviewerId3;
	}

	public String getLeaveReviewer3Name() {
		return leaveReviewer3Name;
	}

	public void setLeaveReviewer3Name(String leaveReviewer3Name) {
		this.leaveReviewer3Name = leaveReviewer3Name;
	}

	public BigDecimal getLeaveBalance() {
		return leaveBalance;
	}

	public void setLeaveBalance(BigDecimal leaveBalance) {
		this.leaveBalance = leaveBalance;
	}

	public List<LeaveCustomFieldDTO> getLeaveCustomFieldDTO() {
		return leaveCustomFieldDTO;
	}

	public void setLeaveCustomFieldDTO(
			List<LeaveCustomFieldDTO> leaveCustomFieldDTO) {
		this.leaveCustomFieldDTO = leaveCustomFieldDTO;
	}

	public Integer getNoOfCustomFields() {
		return noOfCustomFields;
	}

	public void setNoOfCustomFields(Integer noOfCustomFields) {
		this.noOfCustomFields = noOfCustomFields;
	}

	public String getInstruction() {
		return instruction;
	}

	public void setInstruction(String instruction) {
		this.instruction = instruction;
	}

}
