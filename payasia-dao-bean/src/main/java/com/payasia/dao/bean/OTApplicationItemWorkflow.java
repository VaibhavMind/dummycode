package com.payasia.dao.bean;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


/**
 * The persistent class for the OT_Application_Item_Workflow database table.
 * 
 */
@Entity
@Table(name="OT_Application_Item_Workflow")
public class OTApplicationItemWorkflow implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="OT_Application_Item_Workflow_ID")
	private long otApplicationItemWorkflowId;

	@Column(name="OT_Date")
	private Timestamp otDate;

	@Column(name="Remarks")
	private String remarks;

	 
    @ManyToOne
	@JoinColumn(name="Day_Type_ID")
	private DayTypeMaster dayTypeMaster;

	 
    @ManyToOne
	@JoinColumn(name="OT_Application_Item_ID")
	private OTApplicationItem otApplicationItem;

	 
    @ManyToOne
	@JoinColumn(name="OT_Application_Workflow_ID")
	private OTApplicationWorkflow otApplicationWorkflow;

    public OTApplicationItemWorkflow() {
    }

	public long getOtApplicationItemWorkflowId() {
		return this.otApplicationItemWorkflowId;
	}

	public void setOtApplicationItemWorkflowId(long otApplicationItemWorkflowId) {
		this.otApplicationItemWorkflowId = otApplicationItemWorkflowId;
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
	
	public OTApplicationItem getOtApplicationItem() {
		return this.otApplicationItem;
	}

	public void setOtApplicationItem(OTApplicationItem otApplicationItem) {
		this.otApplicationItem = otApplicationItem;
	}
	
	public OTApplicationWorkflow getOtApplicationWorkflow() {
		return this.otApplicationWorkflow;
	}

	public void setOtApplicationWorkflow(OTApplicationWorkflow otApplicationWorkflow) {
		this.otApplicationWorkflow = otApplicationWorkflow;
	}
	
}