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
 * The persistent class for the Leave_Scheme_Type_Proration database table.
 * 
 */
@Entity
@Table(name = "Leave_Scheme_Type_Proration")
public class LeaveSchemeTypeProration extends CompanyBaseEntity implements
		Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Leave_Scheme_Type_Proration_ID")
	private long leaveSchemeTypeProrationId;

	@ManyToOne
	@JoinColumn(name = "Proration_Based_On")
	private AppCodeMaster prorationBasedOn;

	@ManyToOne
	@JoinColumn(name = "Proration_Method")
	private AppCodeMaster prorationMethod;

	@Column(name = "Proration_First_Year_Only")
	private boolean prorationFirstYearOnly;

	@ManyToOne
	@JoinColumn(name = "Rounding_Method")
	private AppCodeMaster roundingMethod;

	 
	@ManyToOne
	@JoinColumn(name = "Leave_Scheme_Type_ID")
	private LeaveSchemeType leaveSchemeType;

	@Column(name = "Cut_Off_Day")
	private Integer cutOffDay;

	@Column(name = "No_Proration")
	private Boolean noProration = false;

	 
	@OneToMany(mappedBy = "leaveSchemeTypeProration", cascade = { CascadeType.REMOVE })
	private Set<LeaveSchemeTypeCustomRounding> leaveSchemeTypeCustomRoundings;

	 
	@OneToMany(mappedBy = "leaveSchemeTypeProration", cascade = { CascadeType.REMOVE })
	private Set<LeaveSchemeTypeCustomProration> leaveSchemeTypeCustomProrations;

	public LeaveSchemeTypeProration() {
	}

	public long getLeaveSchemeTypeProrationId() {
		return leaveSchemeTypeProrationId;
	}

	public void setLeaveSchemeTypeProrationId(long leaveSchemeTypeProrationId) {
		this.leaveSchemeTypeProrationId = leaveSchemeTypeProrationId;
	}

	public AppCodeMaster getProrationBasedOn() {
		return prorationBasedOn;
	}

	public void setProrationBasedOn(AppCodeMaster prorationBasedOn) {
		this.prorationBasedOn = prorationBasedOn;
	}

	public AppCodeMaster getProrationMethod() {
		return prorationMethod;
	}

	public void setProrationMethod(AppCodeMaster prorationMethod) {
		this.prorationMethod = prorationMethod;
	}

	public boolean isProrationFirstYearOnly() {
		return prorationFirstYearOnly;
	}

	public void setProrationFirstYearOnly(boolean prorationFirstYearOnly) {
		this.prorationFirstYearOnly = prorationFirstYearOnly;
	}

	public LeaveSchemeType getLeaveSchemeType() {
		return leaveSchemeType;
	}

	public void setLeaveSchemeType(LeaveSchemeType leaveSchemeType) {
		this.leaveSchemeType = leaveSchemeType;
	}

	public Set<LeaveSchemeTypeCustomRounding> getLeaveSchemeTypeCustomRoundings() {
		return leaveSchemeTypeCustomRoundings;
	}

	public void setLeaveSchemeTypeCustomRoundings(
			Set<LeaveSchemeTypeCustomRounding> leaveSchemeTypeCustomRoundings) {
		this.leaveSchemeTypeCustomRoundings = leaveSchemeTypeCustomRoundings;
	}

	public AppCodeMaster getRoundingMethod() {
		return roundingMethod;
	}

	public void setRoundingMethod(AppCodeMaster roundingMethod) {
		this.roundingMethod = roundingMethod;
	}

	public Integer getCutOffDay() {
		return cutOffDay;
	}

	public void setCutOffDay(Integer cutOffDay) {
		this.cutOffDay = cutOffDay;
	}

	public Set<LeaveSchemeTypeCustomProration> getLeaveSchemeTypeCustomProrations() {
		return leaveSchemeTypeCustomProrations;
	}

	public void setLeaveSchemeTypeCustomProrations(
			Set<LeaveSchemeTypeCustomProration> leaveSchemeTypeCustomProrations) {
		this.leaveSchemeTypeCustomProrations = leaveSchemeTypeCustomProrations;
	}

	public Boolean getNoProration() {
		return noProration;
	}

	public void setNoProration(Boolean noProration) {
		this.noProration = noProration;
	}

}