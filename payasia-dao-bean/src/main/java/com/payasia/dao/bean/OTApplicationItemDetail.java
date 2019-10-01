package com.payasia.dao.bean;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


/**
 * The persistent class for the OT_Application_Item_Detail database table.
 * 
 */
@Entity
@Table(name="OT_Application_Item_Detail")
public class OTApplicationItemDetail implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="OT_Application_Item_Detail_ID")
	private long otApplicationItemDetailId;

	@Column(name="OT_Template_Item_Value")
	private BigDecimal otTemplateItemValue;

	 
    @ManyToOne
	@JoinColumn(name="OT_Application_Item_ID")
	private OTApplicationItem otApplicationItem;

	 
    @ManyToOne
	@JoinColumn(name="OT_Template_Item_ID")
	private OTTemplateItem otTemplateItem;

    public OTApplicationItemDetail() {
    }

	public long getOtApplicationItemDetailId() {
		return this.otApplicationItemDetailId;
	}

	public void setOtApplicationItemDetailId(long otApplicationItemDetailId) {
		this.otApplicationItemDetailId = otApplicationItemDetailId;
	}

	public BigDecimal getOtTemplateItemValue() {
		return this.otTemplateItemValue;
	}

	public void setOtTemplateItemValue(BigDecimal otTemplateItemValue) {
		this.otTemplateItemValue = otTemplateItemValue;
	}

	public OTApplicationItem getOtApplicationItem() {
		return this.otApplicationItem;
	}

	public void setOtApplicationItem(OTApplicationItem otApplicationItem) {
		this.otApplicationItem = otApplicationItem;
	}
	
	public OTTemplateItem getOtTemplateItem() {
		return this.otTemplateItem;
	}

	public void setOtTemplateItem(OTTemplateItem otTemplateItem) {
		this.otTemplateItem = otTemplateItem;
	}
	
}