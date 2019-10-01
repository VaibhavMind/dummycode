package com.payasia.dao.bean;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * The persistent class for the Payslip_Frequency database table.
 * 
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Table(name = "Payslip_Frequency")
public class PayslipFrequency implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "Payslip_Frequency_ID")
	private long payslipFrequencyID;

	@Column(name = "Frequency")
	private String frequency;

	@Column(name = "Frequency_Desc")
	private String frequency_Desc;

	 
	@OneToMany(mappedBy = "payslipFrequency")
	private Set<Company> companies;

	public PayslipFrequency() {
	}

	public long getPayslipFrequencyID() {
		return this.payslipFrequencyID;
	}

	public void setPayslipFrequencyID(long payslipFrequencyID) {
		this.payslipFrequencyID = payslipFrequencyID;
	}

	public String getFrequency() {
		return this.frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public String getFrequency_Desc() {
		return this.frequency_Desc;
	}

	public void setFrequency_Desc(String frequency_Desc) {
		this.frequency_Desc = frequency_Desc;
	}

	public Set<Company> getCompanies() {
		return this.companies;
	}

	public void setCompanies(Set<Company> companies) {
		this.companies = companies;
	}

}