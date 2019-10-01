package com.payasia.common.form;

import java.io.Serializable;
import java.util.Arrays;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

public class ExternalLinkForm implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6621559676880680574L;
	private Long companyId;
    private String companyName;
	
	private String message;
	private String extUrl;
	private Long compExtLinkId;
	private CommonsMultipartFile extImage;
	private String responseString;
	
	
	
	public String getResponseString() {
		return responseString;
	}

	public void setResponseString(String responseString) {
		this.responseString = responseString;
	}

	private String extImageName;
    
    private byte[] image;
    
	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getExtUrl() {
		return extUrl;
	}

	public void setExtUrl(String extUrl) {
		this.extUrl = extUrl;
	}

	public Long getCompExtLinkId() {
		return compExtLinkId;
	}

	public void setCompExtLinkId(Long compExtLinkId) {
		this.compExtLinkId = compExtLinkId;
	}

	public CommonsMultipartFile getExtImage() {
		return extImage;
	}

	public void setExtImage(CommonsMultipartFile extImage) {
		this.extImage = extImage;
	}

	public String getExtImageName() {
		return extImageName;
	}

	public void setExtImageName(String extImageName) {
		this.extImageName = extImageName;
	}

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		if (image != null) {
			this.image = Arrays.copyOf(image, image.length);
		}
	}

	
	
}
