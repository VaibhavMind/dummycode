package com.payasia.dao.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * The persistent class for the OT_Application_Item_Detail_Workflow database
 * table.
 * 
 */
@Entity
@Table(name = "OT_Application_Item_Detail_Workflow")
public class OTApplicationItemDetailWorkflow implements Serializable {

	private static final long serialVersionUID = -8310934252083286539L;

	@Id
	@GeneratedValue
	@Column(name = "OT_Application_Item_Detail_Workflow_ID")
	private long otApplicationItemDetailWorkflowId;

	@Column(name = "OT_Template_Item_Value")
	private String otTemplateItemValue;

	 
	@ManyToOne
	@JoinColumn(name = "OT_Application_Item_Workflow_ID")
	private OTApplicationItemWorkflow otApplicationItemWorkflow;

	 
	@ManyToOne
	@JoinColumn(name = "OT_Application_Item_Detail_ID")
	private OTApplicationItemDetail otApplicationItemDetail;

	 
	@ManyToOne
	@JoinColumn(name = "OT_Template_Item_ID")
	private OTTemplateItem otTemplateItem;

	public long getOtApplicationItemDetailWorkflowId() {
		return otApplicationItemDetailWorkflowId;
	}

	public void setOtApplicationItemDetailWorkflowId(
			long otApplicationItemDetailWorkflowId) {
		this.otApplicationItemDetailWorkflowId = otApplicationItemDetailWorkflowId;
	}

	public String getOtTemplateItemValue() {
		return otTemplateItemValue;
	}

	public void setOtTemplateItemValue(String otTemplateItemValue) {
		this.otTemplateItemValue = otTemplateItemValue;
	}

	public OTApplicationItemWorkflow getOtApplicationItemWorkflow() {
		return otApplicationItemWorkflow;
	}

	public void setOtApplicationItemWorkflow(
			OTApplicationItemWorkflow otApplicationItemWorkflow) {
		this.otApplicationItemWorkflow = otApplicationItemWorkflow;
	}

	public OTApplicationItemDetail getOtApplicationItemDetail() {
		return otApplicationItemDetail;
	}

	public void setOtApplicationItemDetail(
			OTApplicationItemDetail otApplicationItemDetail) {
		this.otApplicationItemDetail = otApplicationItemDetail;
	}

	public OTTemplateItem getOtTemplateItem() {
		return otTemplateItem;
	}

	public void setOtTemplateItem(OTTemplateItem otTemplateItem) {
		this.otTemplateItem = otTemplateItem;
	}

}
