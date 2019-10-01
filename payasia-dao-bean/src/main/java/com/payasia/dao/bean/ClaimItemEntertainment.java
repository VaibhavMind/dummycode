package com.payasia.dao.bean;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Claim_Item_Entertainment")
public class ClaimItemEntertainment extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "Claim_Item_Entertainment_ID")
	private long ClaimItemEntertainmentId;

	@ManyToOne
	@JoinColumn(name = "Company_ID")
	private Company company;

	@ManyToOne
	@JoinColumn(name = "Employee_ID")
	private Employee employee;

	@ManyToOne
	@JoinColumn(name = "Claim_Item_ID")
	private ClaimTemplateItem claimTemplateItem;

	@Column(name = "From_Date")
	private Timestamp startDate;

	@Column(name = "To_Date")
	private Timestamp endDate;

	@Column(name = "Amount")
	private String Amount;

	@Column(name = "Reason")
	private String Reason;

	@Column(name = "forfeitAtEndDate")
	private boolean forfeitAtEndDate;

	public boolean isForfeitAtEndDate() {
		return forfeitAtEndDate;
	}

	public void setForfeitAtEndDate(boolean forfeitAtEndDate) {
		this.forfeitAtEndDate = forfeitAtEndDate;
	}

	public long getClaimItemEntertainmentId() {
		return ClaimItemEntertainmentId;
	}

	public void setClaimItemEntertainmentId(long claimItemEntertainmentId) {
		ClaimItemEntertainmentId = claimItemEntertainmentId;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public ClaimTemplateItem getClaimTemplateItem() {
		return claimTemplateItem;
	}

	public void setClaimTemplateItem(ClaimTemplateItem claimTemplateItem) {
		this.claimTemplateItem = claimTemplateItem;
	}

	public Timestamp getStartDate() {
		return startDate;
	}

	public void setStartDate(Timestamp startDate) {
		this.startDate = startDate;
	}

	public Timestamp getEndDate() {
		return endDate;
	}

	public void setEndDate(Timestamp endDate) {
		this.endDate = endDate;
	}

	public String getAmount() {
		return Amount;
	}

	public void setAmount(String amount) {
		Amount = amount;
	}

	public String getReason() {
		return Reason;
	}

	public void setReason(String reason) {
		Reason = reason;
	}

}
