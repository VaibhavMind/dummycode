package com.payasia.dao.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the Dynamic_Form_Table_Record database table.
 * 
 */
@Embeddable
public class DynamicFormTableRecordPK implements Serializable {
	 
	private static final long serialVersionUID = 1L;

	@Column(name="Dynamic_Form_Table_Record_ID")
	private long dynamicFormTableRecordId;

	@Column(name="Sequence")
	private int sequence;

    public DynamicFormTableRecordPK() {
    }
	public long getDynamicFormTableRecordId() {
		return this.dynamicFormTableRecordId;
	}
	public void setDynamicFormTableRecordId(long dynamicFormTableRecordId) {
		this.dynamicFormTableRecordId = dynamicFormTableRecordId;
	}
	public int getSequence() {
		return this.sequence;
	}
	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof DynamicFormTableRecordPK)) {
			return false;
		}
		DynamicFormTableRecordPK castOther = (DynamicFormTableRecordPK)other;
		return 
			(this.dynamicFormTableRecordId == castOther.dynamicFormTableRecordId)
			&& (this.sequence == castOther.sequence);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + ((int) (this.dynamicFormTableRecordId ^ (this.dynamicFormTableRecordId >>> 32)));
		hash = hash * prime + this.sequence;
		
		return hash;
    }
}