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
 * The persistent class for the OT_Item_Master database table.
 * 
 */
@Entity
@Table(name="OT_Item_Master")
public class OTItemMaster implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name="OT_Item_ID")
	private long otItemId;

	@Column(name="Code")
	private String code;

	@Column(name="OT_Item_Desc")
	private String otItemDesc;

	@Column(name="OT_Item_Name")
	private String otItemName;

	@Column(name="Visibility")
	private boolean visibility;

	 
    @ManyToOne
	@JoinColumn(name="Company_ID")
	private Company company;

	 
	@OneToMany(mappedBy="otItemMaster")
	private Set<OTTemplateItem> otTemplateItems;

    public OTItemMaster() {
    }

	public long getOtItemId() {
		return this.otItemId;
	}

	public void setOtItemId(long otItemId) {
		this.otItemId = otItemId;
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getOtItemDesc() {
		return this.otItemDesc;
	}

	public void setOtItemDesc(String otItemDesc) {
		this.otItemDesc = otItemDesc;
	}

	public String getOtItemName() {
		return this.otItemName;
	}

	public void setOtItemName(String otItemName) {
		this.otItemName = otItemName;
	}

	public boolean getVisibility() {
		return this.visibility;
	}

	public void setVisibility(boolean visibility) {
		this.visibility = visibility;
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
	
}