package com.payasia.dao.bean;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * The persistent class for the Employee database table.
 * 
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Announcement extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Announcement_ID")
	private long announcementId;

	@Column(name = "Title")
	private String title;

	@Column(name = "Description")
	private String description;

	@Column(name = "Post_Date_Time")
	private Timestamp postDateTime;

	@Column(name = "Remove_Date_Time")
	private Timestamp removeDateTime;

	@Column(name = "Archive")
	private boolean archive;

	@Column(name = "Scope")
	private String scope;

	 
	@ManyToOne
	@JoinColumn(name = "Company_ID")
	private Company company;

	 
	@ManyToOne
	@JoinColumn(name = "Group_ID")
	private CompanyGroup companyGroup;

	public long getAnnouncementId() {
		return announcementId;
	}

	public void setAnnouncementId(long announcementId) {
		this.announcementId = announcementId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Timestamp getPostDateTime() {
		return postDateTime;
	}

	public void setPostDateTime(Timestamp postDateTime) {
		this.postDateTime = postDateTime;
	}

	public Timestamp getRemoveDateTime() {
		return removeDateTime;
	}

	public void setRemoveDateTime(Timestamp removeDateTime) {
		this.removeDateTime = removeDateTime;
	}

	public boolean isArchive() {
		return archive;
	}

	public void setArchive(boolean archive) {
		this.archive = archive;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public CompanyGroup getCompanyGroup() {
		return companyGroup;
	}

	public void setCompanyGroup(CompanyGroup companyGroup) {
		this.companyGroup = companyGroup;
	}

}