package com.payasia.dao.bean;

import java.io.Serializable;
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
 * The persistent class for the Leave_Scheme_Type_Custom_Field database table.
 * 
 */
@Entity
@Table(name = "Leave_Scheme_Type_Custom_Field")
public class LeaveSchemeTypeCustomField extends CompanyBaseEntity implements
		Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Custom_Field_ID")
	private long customFieldId;

	@Column(name = "Field_Name")
	private String fieldName;

	@Column(name = "Mandatory")
	private boolean mandatory;

	 
	@ManyToOne
	@JoinColumn(name = "Leave_Scheme_Type_Availing_Leave_ID")
	private LeaveSchemeTypeAvailingLeave leaveSchemeTypeAvailingLeave;

	 
	@OneToMany(mappedBy = "leaveSchemeTypeCustomField")
	private Set<LeaveApplicationCustomField> leaveApplicationCustomFields;

	public LeaveSchemeTypeCustomField() {
	}

	public long getCustomFieldId() {
		return this.customFieldId;
	}

	public void setCustomFieldId(long customFieldId) {
		this.customFieldId = customFieldId;
	}

	public String getFieldName() {
		return this.fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public boolean getMandatory() {
		return this.mandatory;
	}

	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}

	public LeaveSchemeTypeAvailingLeave getLeaveSchemeTypeAvailingLeave() {
		return leaveSchemeTypeAvailingLeave;
	}

	public void setLeaveSchemeTypeAvailingLeave(
			LeaveSchemeTypeAvailingLeave leaveSchemeTypeAvailingLeave) {
		this.leaveSchemeTypeAvailingLeave = leaveSchemeTypeAvailingLeave;
	}

	public Set<LeaveApplicationCustomField> getLeaveApplicationCustomFields() {
		return leaveApplicationCustomFields;
	}

	public void setLeaveApplicationCustomFields(
			Set<LeaveApplicationCustomField> leaveApplicationCustomFields) {
		this.leaveApplicationCustomFields = leaveApplicationCustomFields;
	}

}