package com.payasia.dao.bean;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Arrays;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * The persistent class for the Company_Logo database table.
 * 
 */
@Entity
@Table(name = "Company_Logo")
public class CompanyLogo extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "Company_Logo_ID")
	private long companyLogoId;

	@Lob()
	@Column(name = "Image")
	private byte[] image;

	@Column(name = "Image_Name")
	private String imageName;

	@Column(name = "Logo_Desc")
	private String logoDesc;

	@Column(name = "Uploaded_Date")
	private Timestamp uploadedDate;

	 
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "Company_ID")
	private Company company;

	public CompanyLogo() {
	}

	public long getCompanyLogoId() {
		return this.companyLogoId;
	}

	public void setCompanyLogoId(long companyLogoId) {
		this.companyLogoId = companyLogoId;
	}

	public byte[] getImage() {
		return this.image;
	}

	public void setImage(byte[] image) {
		if (image != null) {
			this.image = Arrays.copyOf(image, image.length);
		}
	}

	public String getImageName() {
		return this.imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public String getLogoDesc() {
		return this.logoDesc;
	}

	public void setLogoDesc(String logoDesc) {
		this.logoDesc = logoDesc;
	}

	public Timestamp getUploadedDate() {
		return this.uploadedDate;
	}

	public void setUploadedDate(Timestamp uploadedDate) {
		this.uploadedDate = uploadedDate;
	}

	public Company getCompany() {
		return this.company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

}