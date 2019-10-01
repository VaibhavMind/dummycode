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
 * The persistent class for the Leave_Scheme_Type_Custom_Rounding database
 * table.
 * 
 */
@Entity
@Table(name = "Leave_Scheme_Type_Custom_Proration")
public class LeaveSchemeTypeCustomProration extends CompanyBaseEntity implements
		Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Custom_Proration_ID")
	private long customProrationId;

	@Column(name = "From_Range")
	private int fromRange;

	@Column(name = "To_Range")
	private int toRange;

	@Column(name = "Value")
	private BigDecimal value;

	 
	@ManyToOne
	@JoinColumn(name = "Leave_Scheme_Type_Proration_ID")
	private LeaveSchemeTypeProration leaveSchemeTypeProration;

	public LeaveSchemeTypeCustomProration() {
	}

	public long getCustomProrationId() {
		return customProrationId;
	}

	public void setCustomProrationId(long customProrationId) {
		this.customProrationId = customProrationId;
	}

	public int getFromRange() {
		return fromRange;
	}

	public void setFromRange(int fromRange) {
		this.fromRange = fromRange;
	}

	public int getToRange() {
		return toRange;
	}

	public void setToRange(int toRange) {
		this.toRange = toRange;
	}

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

	public LeaveSchemeTypeProration getLeaveSchemeTypeProration() {
		return leaveSchemeTypeProration;
	}

	public void setLeaveSchemeTypeProration(
			LeaveSchemeTypeProration leaveSchemeTypeProration) {
		this.leaveSchemeTypeProration = leaveSchemeTypeProration;
	}

}