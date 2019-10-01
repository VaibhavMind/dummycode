package com.payasia.dao.bean;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;


/**
 * The persistent class for the OT_Template database table.
 * 
 */
@Entity
@Table(name="OT_Template")
public class OTTemplate implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name="OT_Template_ID")
	private long otTemplateId;

	@Column(name="Account_Code")
	private String accountCode;

	@Column(name="Template_Name")
	private String templateName;

	@Column(name="Visibility")
	private boolean visibility;

	 
	@OneToMany(mappedBy="otTemplate")
	private Set<EmployeeOTReviewer> employeeOtReviewers;

	 
    @ManyToOne
	@JoinColumn(name="Company_ID")
	private Company company;

	 
	@OneToMany(mappedBy="otTemplate")
	private Set<OTTemplateItem> otTemplateItems;

	 
	@OneToMany(mappedBy="otTemplate")
	private Set<OTTemplateWorkflow> otTemplateWorkflows;

	 
	@OneToMany(mappedBy="otTemplate")
	private Set<OTApplication> otApplications;

    public OTTemplate() {
    }

	public long getOtTemplateId() {
		return this.otTemplateId;
	}

	public void setOtTemplateId(long otTemplateId) {
		this.otTemplateId = otTemplateId;
	}

	public String getAccountCode() {
		return this.accountCode;
	}

	public void setAccountCode(String accountCode) {
		this.accountCode = accountCode;
	}

	public String getTemplateName() {
		return this.templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public boolean getVisibility() {
		return this.visibility;
	}

	public void setVisibility(boolean visibility) {
		this.visibility = visibility;
	}

	public Set<EmployeeOTReviewer> getEmployeeOtReviewers() {
		return this.employeeOtReviewers;
	}

	public void setEmployeeOtReviewers(Set<EmployeeOTReviewer> employeeOtReviewers) {
		this.employeeOtReviewers = employeeOtReviewers;
	}
	
	public Company getCompany() {
		return this.company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}
	
	public Set<OTTemplateItem> getOtTemplateItems() {
		return this.otTemplateItems;
	}

	public void setOtTemplateItems(Set<OTTemplateItem> otTemplateItems) {
		this.otTemplateItems = otTemplateItems;
	}
	
	public Set<OTTemplateWorkflow> getOtTemplateWorkflows() {
		return this.otTemplateWorkflows;
	}

	public void setOtTemplateWorkflows(Set<OTTemplateWorkflow> otTemplateWorkflows) {
		this.otTemplateWorkflows = otTemplateWorkflows;
	}
	
	public Set<OTApplication> getOtApplications() {
		return this.otApplications;
	}

	public void setOtApplications(Set<OTApplication> otApplications) {
		this.otApplications = otApplications;
	}
	
}