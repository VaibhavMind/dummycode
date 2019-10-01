package com.payasia.common.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

public class ClaimTemplateItemDTO  implements Serializable{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long claimTemplateItemId;
	private String recieptNo;
	private String claimDate;
	private BigDecimal claimAmount;
	private BigDecimal amountBeforeTax;
	private BigDecimal taxAmount;
	private String remarks;
	private CommonsMultipartFile attachment;
	
	
	
	public CommonsMultipartFile getAttachment() {
		return attachment;
	}
	public void setAttachment(CommonsMultipartFile attachment) {
		this.attachment = attachment;
	}
	public Long getClaimTemplateItemId() {
		return claimTemplateItemId;
	}
	public void setClaimTemplateItemId(Long claimTemplateItemId) {
		this.claimTemplateItemId = claimTemplateItemId;
	}
	public String getRecieptNo() {
		return recieptNo;
	}
	public void setRecieptNo(String recieptNo) {
		this.recieptNo = recieptNo;
	}
	public String getClaimDate() {
		return claimDate;
	}
	public void setClaimDate(String claimDate) {
		this.claimDate = claimDate;
	}
	public BigDecimal getClaimAmount() {
		return claimAmount;
	}
	public void setClaimAmount(BigDecimal claimAmount) {
		this.claimAmount = claimAmount;
	}
	public BigDecimal getAmountBeforeTax() {
		return amountBeforeTax;
	}
	public void setAmountBeforeTax(BigDecimal amountBeforeTax) {
		this.amountBeforeTax = amountBeforeTax;
	}
	public BigDecimal getTaxAmount() {
		return taxAmount;
	}
	public void setTaxAmount(BigDecimal taxAmount) {
		this.taxAmount = taxAmount;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
	
	
	
	
	
	
	

}
