package com.payasia.dao.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the Dynamic_Form database table.
 * 
 */
@Embeddable
public class DynamicFormPK implements Serializable {
	 
	private static final long serialVersionUID = 1L;

	@Column(name="Company_ID")
	private long company_ID;

	@Column(name="Entity_ID")
	private long entity_ID;

	@Column(name="Form_ID")
	private long formId;

	@Column(name="Version")
	private int version;

    public DynamicFormPK() {
    }
	public long getCompany_ID() {
		return this.company_ID;
	}
	public void setCompany_ID(long company_ID) {
		this.company_ID = company_ID;
	}
	public long getEntity_ID() {
		return this.entity_ID;
	}
	public void setEntity_ID(long entity_ID) {
		this.entity_ID = entity_ID;
	}
	public long getFormId() {
		return this.formId;
	}
	public void setFormId(long formId) {
		this.formId = formId;
	}
	public int getVersion() {
		return this.version;
	}
	public void setVersion(int version) {
		this.version = version;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof DynamicFormPK)) {
			return false;
		}
		DynamicFormPK castOther = (DynamicFormPK)other;
		return 
			(this.company_ID == castOther.company_ID)
			&& (this.entity_ID == castOther.entity_ID)
			&& (this.formId == castOther.formId)
			&& (this.version == castOther.version);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + ((int) (this.company_ID ^ (this.company_ID >>> 32)));
		hash = hash * prime + ((int) (this.entity_ID ^ (this.entity_ID >>> 32)));
		hash = hash * prime + ((int) (this.formId ^ (this.formId >>> 32)));
		hash = hash * prime + this.version;
		
		return hash;
    }
}