package com.payasia.common.form;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import com.payasia.common.dto.PayslipDTO;

public class PayslipResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5814260994078491275L;
	private List<PayslipDTO> payslips;
	private byte[] byteFile;
	private List<String> pages;
	private Integer noOfPages;
	
	public List<PayslipDTO> getPayslips() {
		return payslips;
	}
	public void setPayslips(List<PayslipDTO> payslips) {
		this.payslips = payslips;
	}
	public byte[] getByteFile() {
		return byteFile;
	}
	public void setByteFile(byte[] byteFile) {
		if (byteFile != null) {
			this.byteFile = Arrays.copyOf(byteFile, byteFile.length);
		}
	}
	
	public Integer getNoOfPages() {
		return noOfPages;
	}
	public void setNoOfPages(Integer noOfPages) {
		this.noOfPages = noOfPages;
	}
	public List<String> getPages() {
		return pages;
	}
	public void setPages(List<String> pages) {
		this.pages = pages;
	}
	
	
	
	
	

}
