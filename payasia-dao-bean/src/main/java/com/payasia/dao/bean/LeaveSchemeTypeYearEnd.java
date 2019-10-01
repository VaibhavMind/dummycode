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
 * The persistent class for the Leave_Scheme_Type_Year_End database table.
 * 
 */
@Entity
@Table(name = "Leave_Scheme_Type_Year_End")
public class LeaveSchemeTypeYearEnd extends CompanyBaseEntity implements
		Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Leave_Scheme_Type_Year_End_ID")
	private long leaveSchemeTypeYearEndID;

	@Column(name = "Allow_Carry_Forward")
	private Boolean allowCarryForward;

	@Column(name = "Max_Carry_Forward_Limit")
	private BigDecimal maxCarryForwardLimit;

	@Column(name = "Annual_Carry_Forward_Percentage")
	private BigDecimal annualCarryForwardPercentage;

	@Column(name = "Leave_Expiry_Days")
	private Integer leaveExpiryDays;

	 
	@ManyToOne
	@JoinColumn(name = "Leave_Scheme_Type_ID")
	private LeaveSchemeType leaveSchemeType;

	public LeaveSchemeTypeYearEnd() {
	}

	public long getLeaveSchemeTypeYearEndID() {
		return leaveSchemeTypeYearEndID;
	}

	public void setLeaveSchemeTypeYearEndID(long leaveSchemeTypeYearEndID) {
		this.leaveSchemeTypeYearEndID = leaveSchemeTypeYearEndID;
	}

	public Boolean getAllowCarryForward() {
		return allowCarryForward;
	}

	public void setAllowCarryForward(Boolean allowCarryForward) {
		this.allowCarryForward = allowCarryForward;
	}

	public BigDecimal getMaxCarryForwardLimit() {
		return maxCarryForwardLimit;
	}

	public void setMaxCarryForwardLimit(BigDecimal maxCarryForwardLimit) {
		this.maxCarryForwardLimit = maxCarryForwardLimit;
	}

	public BigDecimal getAnnualCarryForwardPercentage() {
		return annualCarryForwardPercentage;
	}

	public void setAnnualCarryForwardPercentage(
			BigDecimal annualCarryForwardPercentage) {
		this.annualCarryForwardPercentage = annualCarryForwardPercentage;
	}

	public Integer getLeaveExpiryDays() {
		return leaveExpiryDays;
	}

	public void setLeaveExpiryDays(Integer leaveExpiryDays) {
		this.leaveExpiryDays = leaveExpiryDays;
	}

	public LeaveSchemeType getLeaveSchemeType() {
		return leaveSchemeType;
	}

	public void setLeaveSchemeType(LeaveSchemeType leaveSchemeType) {
		this.leaveSchemeType = leaveSchemeType;
	}

}