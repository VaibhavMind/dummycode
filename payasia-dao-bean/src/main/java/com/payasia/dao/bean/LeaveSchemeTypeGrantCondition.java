package com.payasia.dao.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * The persistent class for the Leave_Scheme_Type_Custom_Field database table.
 * 
 */
@Entity
@Table(name = "Leave_Scheme_Type_Grant_Condition")
public class LeaveSchemeTypeGrantCondition extends CompanyBaseEntity implements
		Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Leave_Scheme_Type_Grant_Condition_ID")
	private long leaveSchemeTypeGrantConditionId;

	 
	@ManyToOne
	@JoinColumn(name = "Leave_Scheme_Type_Availing_Leave_ID")
	private LeaveSchemeTypeAvailingLeave leaveSchemeTypeAvailingLeave;

	 
	@ManyToOne
	@JoinColumn(name = "Grant_Condition")
	private AppCodeMaster grantCondition;

	 
	@ManyToOne
	@JoinColumn(name = "Grant_Condition_Value")
	private LeaveSchemeType grantConditionValue;

	public LeaveSchemeTypeGrantCondition() {
	}

	public long getLeaveSchemeTypeGrantConditionId() {
		return leaveSchemeTypeGrantConditionId;
	}

	public void setLeaveSchemeTypeGrantConditionId(
			long leaveSchemeTypeGrantConditionId) {
		this.leaveSchemeTypeGrantConditionId = leaveSchemeTypeGrantConditionId;
	}

	public LeaveSchemeTypeAvailingLeave getLeaveSchemeTypeAvailingLeave() {
		return leaveSchemeTypeAvailingLeave;
	}

	public void setLeaveSchemeTypeAvailingLeave(
			LeaveSchemeTypeAvailingLeave leaveSchemeTypeAvailingLeave) {
		this.leaveSchemeTypeAvailingLeave = leaveSchemeTypeAvailingLeave;
	}

	public AppCodeMaster getGrantCondition() {
		return grantCondition;
	}

	public void setGrantCondition(AppCodeMaster grantCondition) {
		this.grantCondition = grantCondition;
	}

	public LeaveSchemeType getGrantConditionValue() {
		return grantConditionValue;
	}

	public void setGrantConditionValue(LeaveSchemeType grantConditionValue) {
		this.grantConditionValue = grantConditionValue;
	}

}