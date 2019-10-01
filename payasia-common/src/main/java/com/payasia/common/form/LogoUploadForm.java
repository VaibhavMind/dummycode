package com.payasia.common.form;

import java.io.Serializable;
import java.util.Arrays;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 * The Class LogoUploadForm.
 */
public class LogoUploadForm implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 641743337815676013L;

	/** The company id. */
	private Long companyId;
	
	/** The company name. */
	private String companyName;
	
	/** The logo image. */
	private CommonsMultipartFile logoImage;
	
	/** The logo image name. */
	private String logoImageName;
    
    /** The image. */
    private byte[] image;
    
    private String fileType;
    
    
    
	
	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	/**
	 * Gets the company id.
	 *
	 * @return the company id
	 */
	public Long getCompanyId() {
		return companyId;
	}

	/**
	 * Sets the company id.
	 *
	 * @param companyId the new company id
	 */
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	/**
	 * Gets the company name.
	 *
	 * @return the company name
	 */
	public String getCompanyName() {
		return companyName;
	}

	/**
	 * Sets the company name.
	 *
	 * @param companyName the new company name
	 */
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	/**
	 * Gets the logo image.
	 *
	 * @return the logo image
	 */
	public CommonsMultipartFile getLogoImage() {
		return logoImage;
	}

	/**
	 * Sets the logo image.
	 *
	 * @param logoImage the new logo image
	 */
	public void setLogoImage(CommonsMultipartFile logoImage) {
		this.logoImage = logoImage;
	}

	/**
	 * Gets the logo image name.
	 *
	 * @return the logo image name
	 */
	public String getLogoImageName() {
		return logoImageName;
	}

	/**
	 * Sets the logo image name.
	 *
	 * @param logoImageName the new logo image name
	 */
	public void setLogoImageName(String logoImageName) {
		this.logoImageName = logoImageName;
	}

	/**
	 * Gets the image.
	 *
	 * @return the image
	 */
	public byte[] getImage() {
		return image;
	}

	/**
	 * Sets the image.
	 *
	 * @param image the new image
	 */
	public void setImage(byte[] image) {
		if (image != null) {
			this.image = Arrays.copyOf(image, image.length);
		}
	}

	
	

}
