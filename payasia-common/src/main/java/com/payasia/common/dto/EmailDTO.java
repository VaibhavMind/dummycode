package com.payasia.common.dto;

import java.io.File;
import java.io.Serializable;
import java.util.Map;

import com.payasia.common.util.PayAsiaEmailTO;

public class EmailDTO implements Serializable {
	
	private File emailBody;
	private File emailSubject;
	private PayAsiaEmailTO payAsiaEmailTO;
	private Map<String, Object>  emailParamMap;
	
	private File attachment;
	public Map<String, Object> getEmailParamMap() {
		return emailParamMap;
	}
	public void setEmailParamMap(Map<String, Object> emailParamMap) {
		this.emailParamMap = emailParamMap;
	}
	public PayAsiaEmailTO getPayAsiaEmailTO() {
		return payAsiaEmailTO;
	}
	public void setPayAsiaEmailTO(PayAsiaEmailTO payAsiaEmailTO) {
		this.payAsiaEmailTO = payAsiaEmailTO;
	}
	public File getEmailBody() {
		return emailBody;
	}
	public void setEmailBody(File emailBody) {
		this.emailBody = emailBody;
	}
	public File getEmailSubject() {
		return emailSubject;
	}
	public void setEmailSubject(File emailSubject) {
		this.emailSubject = emailSubject;
	}
	public File getAttachment() {
		return attachment;
	}
	public void setAttachment(File attachment) {
		this.attachment = attachment;
	}
	
	
	
	
	

}
