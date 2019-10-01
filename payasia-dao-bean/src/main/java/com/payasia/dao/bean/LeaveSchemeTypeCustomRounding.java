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
@Table(name = "Leave_Scheme_Type_Custom_Rounding")
public class LeaveSchemeTypeCustomRounding extends CompanyBaseEntity implements
		Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Custom_Rounding_ID")
	private long customRoundingId;

	@Column(name = "From_Range")
	private BigDecimal fromRange;

	@Column(name = "To_Range")
	private BigDecimal toRange;

	@Column(name = "Value")
	private BigDecimal value;

	 
	@ManyToOne
	@JoinColumn(name = "Leave_Scheme_Type_Proration_ID")
	private LeaveSchemeTypeProration leaveSchemeTypeProration;

	public LeaveSchemeTypeCustomRounding() {
	}

	public long getCustomRoundingId() {
		return customRoundingId;
	}

	public void setCustomRoundingId(long customRoundingId) {
		this.customRoundingId = customRoundingId;
	}

	public BigDecimal getFromRange() {
		return fromRange;
	}

	public void setFromRange(BigDecimal fromRange) {
		this.fromRange = fromRange;
	}

	public BigDecimal getToRange() {
		return toRange;
	}

	public void setToRange(BigDecimal toRange) {
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