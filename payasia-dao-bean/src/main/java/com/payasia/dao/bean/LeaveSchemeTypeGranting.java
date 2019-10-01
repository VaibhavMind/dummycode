package com.payasia.dao.bean;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
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
 * The persistent class for the Leave_Scheme_Type_Granting database table.
 * 
 */
@Entity
@Table(name = "Leave_Scheme_Type_Granting")
public class LeaveSchemeTypeGranting extends CompanyBaseEntity implements
		Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Leave_Scheme_Type_Granting_ID")
	private long leaveSchemeTypeGrantingId;

	@ManyToOne
	@JoinColumn(name = "Leave_Calendar")
	private AppCodeMaster leaveCalendar;

	@ManyToOne
	@JoinColumn(name = "Distribution_Method")
	private AppCodeMaster distributionMethod;

	 
	@ManyToOne
	@JoinColumn(name = "Leave_Scheme_Type_ID")
	private LeaveSchemeType leaveSchemeType;

	 
	@OneToMany(mappedBy = "leaveSchemeTypeGranting", cascade = { CascadeType.REMOVE })
	private Set<LeaveSchemeTypeEntitlement> leaveSchemeTypeEntitlements;

	@Column(name = "Grant_Day")
	private Integer grantDay;

	@Column(name = "Expire_Entitlement")
	private Boolean expireEntitlement;

	public LeaveSchemeTypeGranting() {
	}

	public long getLeaveSchemeTypeGrantingId() {
		return leaveSchemeTypeGrantingId;
	}

	public void setLeaveSchemeTypeGrantingId(long leaveSchemeTypeGrantingId) {
		this.leaveSchemeTypeGrantingId = leaveSchemeTypeGrantingId;
	}

	public AppCodeMaster getLeaveCalendar() {
		return leaveCalendar;
	}

	public void setLeaveCalendar(AppCodeMaster leaveCalendar) {
		this.leaveCalendar = leaveCalendar;
	}

	public AppCodeMaster getDistributionMethod() {
		return distributionMethod;
	}

	public void setDistributionMethod(AppCodeMaster distributionMethod) {
		this.distributionMethod = distributionMethod;
	}

	public LeaveSchemeType getLeaveSchemeType() {
		return leaveSchemeType;
	}

	public void setLeaveSchemeType(LeaveSchemeType leaveSchemeType) {
		this.leaveSchemeType = leaveSchemeType;
	}

	public Set<LeaveSchemeTypeEntitlement> getLeaveSchemeTypeEntitlements() {
		return leaveSchemeTypeEntitlements;
	}

	public void setLeaveSchemeTypeEntitlements(
			Set<LeaveSchemeTypeEntitlement> leaveSchemeTypeEntitlements) {
		this.leaveSchemeTypeEntitlements = leaveSchemeTypeEntitlements;
	}

	public Integer getGrantDay() {
		return grantDay;
	}

	public void setGrantDay(Integer grantDay) {
		this.grantDay = grantDay;
	}

	public Boolean getExpireEntitlement() {
		return expireEntitlement;
	}

	public void setExpireEntitlement(Boolean expireEntitlement) {
		this.expireEntitlement = expireEntitlement;
	}

}