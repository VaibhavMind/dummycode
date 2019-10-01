package com.payasia.common.dto;

import java.io.Serializable;
import java.sql.Timestamp;

public class LundinConditionDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4345787790295874709L;
	private Long companyId;
	private Long statusId;
	private Timestamp fromDate;
	private Timestamp toDate;
	private String createdDate;
	private String blockCode;
	private String blockName;
	private String departmentCode;
	private String departmentName;
	private String transactionType;
	private String afeCode;
	private String afeName;
	private boolean status;
	
	public Long getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
	public Long getStatusId() {
		return statusId;
	}
	public void setStatusId(Long statusId) {
		this.statusId = statusId;
	}
	public Timestamp getFromDate() {
		return fromDate;
	}
	public void setFromDate(Timestamp fromDate) {
		this.fromDate = fromDate;
	}
	public Timestamp getToDate() {
		return toDate;
	}
	public void setToDate(Timestamp toDate) {
		this.toDate = toDate;
	}
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	public String getBlockCode() {
		return blockCode;
	}
	public void setBlockCode(String blockCode) {
		this.blockCode = blockCode;
	}
	public String getBlockName() {
		return blockName;
	}
	public void setBlockName(String blockName) {
		this.blockName = blockName;
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
	public String getTransactionType() {
		return transactionType;
	}
	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}
	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
	public String getAfeCode() {
		return afeCode;
	}
	public void setAfeCode(String afeCode) {
		this.afeCode = afeCode;
	}
	public String getAfeName() {
		return afeName;
	}
	public void setAfeName(String afeName) {
		this.afeName = afeName;
	}
}
