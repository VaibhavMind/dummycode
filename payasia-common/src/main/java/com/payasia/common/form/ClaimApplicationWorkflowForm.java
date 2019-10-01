package com.payasia.common.form;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class ClaimApplicationWorkflowForm implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2045161183964305549L;
	
	private Long claimApplicationID;
	private Long claimStatusID;
	private BigDecimal totalAmount;
	private String forwardTo;
	private String emailCC;
	private String remarks;
	private long createdBy;
	private String createdDate;
	private Timestamp createDateM;
	private Integer srNo;
	private String workflowRule;
	private String empName;
	private String statusName;
	private String statusNameLocale;
	private byte[] empImage;
	
	public String getStatusNameLocale() {
		return statusNameLocale;
	}
	public void setStatusNameLocale(String statusNameLocal) {
		this.statusNameLocale = statusNameLocal;
	}
	public Integer getSrNo() {
		return srNo;
	}
	public void setSrNo(Integer srNo) {
		this.srNo = srNo;
	}
	public String getWorkflowRule() {
		return workflowRule;
	}
	public void setWorkflowRule(String workflowRule) {
		this.workflowRule = workflowRule;
	}
	public String getEmpName() {
		return empName;
	}
	public void setEmpName(String empName) {
		this.empName = empName;
	}
	public String getStatusName() {
		return statusName;
	}
	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}
	public Long getClaimApplicationID() {
		return claimApplicationID;
	}
	public void setClaimApplicationID(Long claimApplicationID) {
		this.claimApplicationID = claimApplicationID;
	}
	public Long getClaimStatusID() {
		return claimStatusID;
	}
	public void setClaimStatusID(Long claimStatusID) {
		this.claimStatusID = claimStatusID;
	}
	public BigDecimal getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}
	public String getForwardTo() {
		return forwardTo;
	}
	public void setForwardTo(String forwardTo) {
		this.forwardTo = forwardTo;
	}
	public String getEmailCC() {
		return emailCC;
	}
	public void setEmailCC(String emailCC) {
		this.emailCC = emailCC;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public long getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(long createdBy) {
		this.createdBy = createdBy;
	}
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	public Timestamp getCreateDateM() {
		return createDateM;
	}
	public void setCreateDateM(Timestamp createDateM) {
		this.createDateM = createDateM;
	}
	public byte[] getEmpImage() {
		return empImage;
	}
	public void setEmpImage(byte[] empImage) {
		this.empImage = empImage;
	}
	

}
