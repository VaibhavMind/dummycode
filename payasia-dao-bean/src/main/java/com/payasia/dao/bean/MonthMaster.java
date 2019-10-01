package com.payasia.dao.bean;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * The persistent class for the Month_Master database table.
 * 
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Table(name = "Month_Master")
public class MonthMaster implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Month_ID")
	private long monthId;

	@Column(name = "Month_Abbr", insertable = false)
	private String monthAbbr;

	@Column(name = "Month_Name")
	private String monthName;

	@Column(name = "Label_Key")
	private String labelKey;

	@OneToMany(mappedBy = "monthMaster")
	private Set<DynamicForm> dynamicForms;

	@OneToMany(mappedBy = "monthMaster1")
	private Set<FinancialYearMaster> financialYearMasters1;

	@OneToMany(mappedBy = "monthMaster2")
	private Set<FinancialYearMaster> financialYearMasters2;

	@OneToMany(mappedBy = "monthMaster")
	private Set<Payslip> payslips;

	@OneToMany(mappedBy = "monthMaster")
	private Set<PayslipUploadHistory> payslipUploadHistories;

	@OneToMany(mappedBy = "monthMaster")
	private Set<CompanyPayslipRelease> companyPayslipReleases;

	public MonthMaster() {
	}

	public long getMonthId() {
		return this.monthId;
	}

	public void setMonthId(long monthId) {
		this.monthId = monthId;
	}

	public String getMonthAbbr() {
		return this.monthAbbr;
	}

	public void setMonthAbbr(String monthAbbr) {
		this.monthAbbr = monthAbbr;
	}

	public String getMonthName() {
		return this.monthName;
	}

	public void setMonthName(String monthName) {
		this.monthName = monthName;
	}

	public Set<DynamicForm> getDynamicForms() {
		return this.dynamicForms;
	}

	public void setDynamicForms(Set<DynamicForm> dynamicForms) {
		this.dynamicForms = dynamicForms;
	}

	public Set<FinancialYearMaster> getFinancialYearMasters1() {
		return this.financialYearMasters1;
	}

	public void setFinancialYearMasters1(
			Set<FinancialYearMaster> financialYearMasters1) {
		this.financialYearMasters1 = financialYearMasters1;
	}

	public Set<FinancialYearMaster> getFinancialYearMasters2() {
		return this.financialYearMasters2;
	}

	public void setFinancialYearMasters2(
			Set<FinancialYearMaster> financialYearMasters2) {
		this.financialYearMasters2 = financialYearMasters2;
	}

	public Set<Payslip> getPayslips() {
		return this.payslips;
	}

	public void setPayslips(Set<Payslip> payslips) {
		this.payslips = payslips;
	}

	public Set<PayslipUploadHistory> getPayslipUploadHistories() {
		return this.payslipUploadHistories;
	}

	public void setPayslipUploadHistories(
			Set<PayslipUploadHistory> payslipUploadHistories) {
		this.payslipUploadHistories = payslipUploadHistories;
	}

	public Set<CompanyPayslipRelease> getCompanyPayslipReleases() {
		return companyPayslipReleases;
	}

	public void setCompanyPayslipReleases(
			Set<CompanyPayslipRelease> companyPayslipReleases) {
		this.companyPayslipReleases = companyPayslipReleases;
	}

	public String getLabelKey() {
		return labelKey;
	}

	public void setLabelKey(String labelKey) {
		this.labelKey = labelKey;
	}

}