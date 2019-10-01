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
 * The persistent class for the OT_Template_Item database table.
 * 
 */
@Entity
@Table(name="OT_Template_Item")
public class OTTemplateItem implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name="OT_Template_Item_ID")
	private long OTTemplateItemId;

	@Column(name="Visibility")
	private boolean visibility;

	 
    @ManyToOne
	@JoinColumn(name="OT_Item_ID")
	private OTItemMaster otItemMaster;

	 
    @ManyToOne
	@JoinColumn(name="OT_Template_ID")
	private OTTemplate otTemplate;

	 
	@OneToMany(mappedBy="otTemplateItem")
	private Set<OTApplicationItemDetail> otApplicationItemDetails;

    public OTTemplateItem() {
    }

	public long getOTTemplateItemId() {
		return this.OTTemplateItemId;
	}

	public void setOTTemplateItemId(long OTTemplateItemId) {
		this.OTTemplateItemId = OTTemplateItemId;
	}

	public boolean getVisibility() {
		return this.visibility;
	}

	public void setVisibility(boolean visibility) {
		this.visibility = visibility;
	}

	public OTItemMaster getOtItemMaster() {
		return this.otItemMaster;
	}

	public void setOtItemMaster(OTItemMaster otItemMaster) {
		this.otItemMaster = otItemMaster;
	}
	
	public OTTemplate getOtTemplate() {
		return this.otTemplate;
	}

	public void setOtTemplate(OTTemplate otTemplate) {
		this.otTemplate = otTemplate;
	}
	
	public Set<OTApplicationItemDetail> getOtApplicationItemDetails() {
		return this.otApplicationItemDetails;
	}

	public void setOtApplicationItemDetails(Set<OTApplicationItemDetail> otApplicationItemDetails) {
		this.otApplicationItemDetails = otApplicationItemDetails;
	}
	
}