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
 * The persistent class for the OT_Application_Item database table.
 * 
 */
@Entity
@Table(name="OT_Application_Item")
public class OTApplicationItem implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="OT_Application_Item_ID")
	private long otApplicationItemId;

	@Column(name="OT_Date")
	private Timestamp otDate;

	@Column(name="Remarks")
	private String remarks;

	 
    @ManyToOne
	@JoinColumn(name="Day_Type_ID")
	private DayTypeMaster dayTypeMaster;

	 
    @ManyToOne
	@JoinColumn(name="OT_Application_ID")
	private OTApplication otApplication;

	 
	@OneToMany(mappedBy="otApplicationItem")
	private Set<OTApplicationItemDetail> otApplicationItemDetails;

	 
	@OneToMany(mappedBy="otApplicationItem")
	private Set<OTApplicationItemWorkflow> otApplicationItemWorkflows;

    public OTApplicationItem() {
    }

	public long getOtApplicationItemId() {
		return this.otApplicationItemId;
	}

	public void setOtApplicationItemId(long otApplicationItemId) {
		this.otApplicationItemId = otApplicationItemId;
	}

	public Timestamp getOtDate() {
		return this.otDate;
	}

	public void setOtDate(Timestamp otDate) {
		this.otDate = otDate;
	}

	public String getRemarks() {
		return this.remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public DayTypeMaster getDayTypeMaster() {
		return this.dayTypeMaster;
	}

	public void setDayTypeMaster(DayTypeMaster dayTypeMaster) {
		this.dayTypeMaster = dayTypeMaster;
	}
	
	public OTApplication getOtApplication() {
		return this.otApplication;
	}

	public void setOtApplication(OTApplication otApplication) {
		this.otApplication = otApplication;
	}
	
	public Set<OTApplicationItemDetail> getOtApplicationItemDetails() {
		return this.otApplicationItemDetails;
	}

	public void setOtApplicationItemDetails(Set<OTApplicationItemDetail> otApplicationItemDetails) {
		this.otApplicationItemDetails = otApplicationItemDetails;
	}
	
	public Set<OTApplicationItemWorkflow> getOtApplicationItemWorkflows() {
		return this.otApplicationItemWorkflows;
	}

	public void setOtApplicationItemWorkflows(Set<OTApplicationItemWorkflow> otApplicationItemWorkflows) {
		this.otApplicationItemWorkflows = otApplicationItemWorkflows;
	}
	
}