package com.payasia.dao.bean;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;


/**
 * The persistent class for the OT_Application database table.
 * 
 */
@Entity
@Table(name="OT_Application")
public class OTApplication implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="OT_Application_ID")
	private long otApplicationId;

	@Column(name="Created_Date")
	private Timestamp createdDate;

	@Column(name="Remarks")
	private String remarks;

	@Column(name="Updated_Date")
	private Timestamp updatedDate;

	 
    @ManyToOne
	@JoinColumn(name="Company_ID")
	private Company company;

	 
    @ManyToOne
	@JoinColumn(name="Employee_ID")
	private Employee employee;

	 
    @ManyToOne
	@JoinColumn(name="OT_Status_ID")
	private OTStatusMaster otStatusMaster;

	 
    @ManyToOne
	@JoinColumn(name="OT_Template_ID")
	private OTTemplate otTemplate;

	 
	@OneToMany(mappedBy="otApplication")
	private Set<OTApplicationItem> otApplicationItems;

	 
	@OneToMany(mappedBy="otApplication")
	private Set<OTApplicationReviewer> otApplicationReviewers;

	 
	@OneToMany(mappedBy="otApplication")
	private Set<OTApplicationWorkflow> otApplicationWorkflows;

    public OTApplication() {
    }

	public long getOtApplicationId() {
		return this.otApplicationId;
	}

	public void setOtApplicationId(long otApplicationId) {
		this.otApplicationId = otApplicationId;
	}

	public Timestamp getCreatedDate() {
		return this.createdDate;
	}

	public void setCreatedDate(Timestamp createdDate) {
		this.createdDate = createdDate;
	}

	public String getRemarks() {
		return this.remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Timestamp getUpdatedDate() {
		return this.updatedDate;
	}

	public void setUpdatedDate(Timestamp updatedDate) {
		this.updatedDate = updatedDate;
	}

	public Company getCompany() {
		return this.company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}
	
	public Employee getEmployee() {
		return this.employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}
	
	public OTStatusMaster getOtStatusMaster() {
		return this.otStatusMaster;
	}

	public void setOtStatusMaster(OTStatusMaster otStatusMaster) {
		this.otStatusMaster = otStatusMaster;
	}
	
	public OTTemplate getOtTemplate() {
		return this.otTemplate;
	}

	public void setOtTemplate(OTTemplate otTemplate) {
		this.otTemplate = otTemplate;
	}
	
	public Set<OTApplicationItem> getOtApplicationItems() {
		return this.otApplicationItems;
	}

	public void setOtApplicationItems(Set<OTApplicationItem> otApplicationItems) {
		this.otApplicationItems = otApplicationItems;
	}
	
	public Set<OTApplicationReviewer> getOtApplicationReviewers() {
		return this.otApplicationReviewers;
	}

	public void setOtApplicationReviewers(Set<OTApplicationReviewer> otApplicationReviewers) {
		this.otApplicationReviewers = otApplicationReviewers;
	}
	
	public Set<OTApplicationWorkflow> getOtApplicationWorkflows() {
		return this.otApplicationWorkflows;
	}

	public void setOtApplicationWorkflows(Set<OTApplicationWorkflow> otApplicationWorkflows) {
		this.otApplicationWorkflows = otApplicationWorkflows;
	}
	
}