package com.payasia.dao.bean;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * The persistent class for the Company_Group database table.
 * 
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "Company_Group")
public class CompanyGroup extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Group_ID")
	private long groupId;

	@Column(name = "Group_Code")
	private String groupCode;

	@Column(name = "Group_Desc")
	private String groupDesc;

	@Column(name = "Group_Name")
	private String groupName;

	 
	@OneToMany(mappedBy = "companyGroup")
	private Set<Company> companies;

	 
	@OneToMany(mappedBy = "companyGroup")
	private Set<EmpDataExportTemplate> empDataExportTemplates;

	 
	@OneToMany(mappedBy = "companyGroup")
	private Set<Announcement> announcements;

	public CompanyGroup() {
	}

	public long getGroupId() {
		return this.groupId;
	}

	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}

	public String getGroupCode() {
		return this.groupCode;
	}

	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}

	public String getGroupDesc() {
		return this.groupDesc;
	}

	public void setGroupDesc(String groupDesc) {
		this.groupDesc = groupDesc;
	}

	public String getGroupName() {
		return this.groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public Set<Company> getCompanies() {
		return this.companies;
	}

	public void setCompanies(Set<Company> companies) {
		this.companies = companies;
	}

	public Set<EmpDataExportTemplate> getEmpDataExportTemplates() {
		return empDataExportTemplates;
	}

	public void setEmpDataExportTemplates(
			Set<EmpDataExportTemplate> empDataExportTemplates) {
		this.empDataExportTemplates = empDataExportTemplates;
	}

	public Set<Announcement> getAnnouncements() {
		return announcements;
	}

	public void setAnnouncements(Set<Announcement> announcements) {
		this.announcements = announcements;
	}

}