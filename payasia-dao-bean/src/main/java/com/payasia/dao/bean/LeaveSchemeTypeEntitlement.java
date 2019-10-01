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
 * The persistent class for the Leave_Scheme_Type_Entitlement database table.
 * 
 */
@Entity
@Table(name = "Leave_Scheme_Type_Entitlement")
public class LeaveSchemeTypeEntitlement extends CompanyBaseEntity implements
		Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Entitlement_ID")
	private long entitlementId;

	@Column(name = "Value")
	private BigDecimal value;

	@Column(name = "Year")
	private int year;

	 
	@ManyToOne
	@JoinColumn(name = "Leave_Scheme_Type_Granting_ID")
	private LeaveSchemeTypeGranting leaveSchemeTypeGranting;

	public LeaveSchemeTypeEntitlement() {
	}

	public long getEntitlementId() {
		return this.entitlementId;
	}

	public void setEntitlementId(long entitlementId) {
		this.entitlementId = entitlementId;
	}

	public int getYear() {
		return this.year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public LeaveSchemeTypeGranting getLeaveSchemeTypeGranting() {
		return leaveSchemeTypeGranting;
	}

	public void setLeaveSchemeTypeGranting(
			LeaveSchemeTypeGranting leaveSchemeTypeGranting) {
		this.leaveSchemeTypeGranting = leaveSchemeTypeGranting;
	}

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

}