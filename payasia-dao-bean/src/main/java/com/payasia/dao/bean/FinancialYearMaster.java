package com.payasia.dao.bean;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * The persistent class for the Financial_Year_Master database table.
 * 
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Table(name = "Financial_Year_Master")
public class FinancialYearMaster implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "Fin_Year_ID")
	private long finYearId;

	 
	@OneToMany(mappedBy = "financialYearMaster")
	private Set<Company> companies;

	 
	@ManyToOne
	@JoinColumn(name = "Start_Month_ID")
	private MonthMaster monthMaster1;

	 
	@ManyToOne
	@JoinColumn(name = "End_Month_ID")
	private MonthMaster monthMaster2;

	public FinancialYearMaster() {
	}

	public long getFinYearId() {
		return this.finYearId;
	}

	public void setFinYearId(long finYearId) {
		this.finYearId = finYearId;
	}

	public Set<Company> getCompanies() {
		return this.companies;
	}

	public void setCompanies(Set<Company> companies) {
		this.companies = companies;
	}

	public MonthMaster getMonthMaster1() {
		return this.monthMaster1;
	}

	public void setMonthMaster1(MonthMaster monthMaster1) {
		this.monthMaster1 = monthMaster1;
	}

	public MonthMaster getMonthMaster2() {
		return this.monthMaster2;
	}

	public void setMonthMaster2(MonthMaster monthMaster2) {
		this.monthMaster2 = monthMaster2;
	}

}