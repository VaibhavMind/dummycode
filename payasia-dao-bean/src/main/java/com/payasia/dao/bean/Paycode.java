package com.payasia.dao.bean;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 * The persistent class for the Paycode database table.
 * 
 */
@Entity
public class Paycode extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "Paycode_ID")
	private long paycodeID;

	@Column(name = "Paycode")
	private String paycode;

	 
	@OneToMany(mappedBy = "paycode")
	private Set<PayDataCollection> payDataCollections;

	 
	@ManyToOne
	@JoinColumn(name = "Company_ID")
	private Company company;

	public Paycode() {
	}

	public long getPaycodeID() {
		return this.paycodeID;
	}

	public void setPaycodeID(long paycodeID) {
		this.paycodeID = paycodeID;
	}

	public String getPaycode() {
		return this.paycode;
	}

	public void setPaycode(String paycode) {
		this.paycode = paycode;
	}

	public Set<PayDataCollection> getPayDataCollections() {
		return this.payDataCollections;
	}

	public void setPayDataCollections(Set<PayDataCollection> payDataCollections) {
		this.payDataCollections = payDataCollections;
	}

	public Company getCompany() {
		return this.company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

}