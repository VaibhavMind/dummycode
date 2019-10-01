package com.payasia.dao.bean;

import java.io.Serializable;
import java.util.Arrays;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * The persistent class for the Company_External_Link database table.
 * 
 */
@Entity
@Table(name = "Company_External_Link")
public class CompanyExternalLink extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "External_Link_ID")
	private long externalLinkId;

	@Lob()
	@Column(name = "Image")
	private byte[] image;

	@Column(name = "Link")
	private String link;

	@Column(name = "Image_Name")
	private String imageName;

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	@Column(name = "Message")
	private String message;

	 
	@ManyToOne
	@JoinColumn(name = "Company_ID")
	private Company company;

	public CompanyExternalLink() {
	}

	public long getExternalLinkId() {
		return this.externalLinkId;
	}

	public void setExternalLinkId(long externalLinkId) {
		this.externalLinkId = externalLinkId;
	}

	public byte[] getImage() {
		return this.image;
	}

	public void setImage(byte[] image) {
		if (image != null) {
			this.image = Arrays.copyOf(image, image.length);
		}
	}

	public String getLink() {
		return this.link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Company getCompany() {
		return this.company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

}