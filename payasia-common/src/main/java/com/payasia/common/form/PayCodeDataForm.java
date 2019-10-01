package com.payasia.common.form;

import java.io.Serializable;
import java.math.BigDecimal;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

public class PayCodeDataForm implements Serializable{

	private static final long serialVersionUID = 1L;

	private Long empid;

	private Long payCodeId;

	private String payCode;

	private BigDecimal amount;

	private String date;
	private CommonsMultipartFile fileUpload;

	public Long getPayCodeId() {
		return payCodeId;
	}

	public void setPayCodeId(Long payCodeId) {
		this.payCodeId = payCodeId;
	}

	public String getPayCode() {
		return payCode;
	}

	public void setPayCode(String payCode) {
		this.payCode = payCode;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Long getEmpid() {
		return empid;
	}

	public void setEmpid(Long empid) {
		this.empid = empid;
	}

	public CommonsMultipartFile getFileUpload() {
		return fileUpload;
	}

	public void setFileUpload(CommonsMultipartFile fileUpload) {
		this.fileUpload = fileUpload;
	}

}
