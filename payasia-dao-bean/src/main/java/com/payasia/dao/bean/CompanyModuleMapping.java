package com.payasia.dao.bean;

import java.io.Serializable;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * The persistent class for the Country_Master database table.
 * 
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "Company_Module_Mapping")
public class CompanyModuleMapping extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "Company_Module_Mapping_ID")
	private long companyModuleMappingId;

	@ManyToOne
	@JoinColumn(name = "Company_ID")
	private Company company;

	@ManyToOne
	@JoinColumn(name = "Module_ID")
	private ModuleMaster moduleMaster;

	public CompanyModuleMapping() {
		super();
	}

	public CompanyModuleMapping(Company company, ModuleMaster moduleMaster) {
		super();
		this.company = company;
		this.moduleMaster = moduleMaster;
	}

	public long getCompanyModuleMappingId() {
		return companyModuleMappingId;
	}

	public void setCompanyModuleMappingId(long companyModuleMappingId) {
		this.companyModuleMappingId = companyModuleMappingId;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public ModuleMaster getModuleMaster() {
		return moduleMaster;
	}

	public void setModuleMaster(ModuleMaster moduleMaster) {
		this.moduleMaster = moduleMaster;
	}

	@Override
	public String toString() {
		return "CompanyModuleMapping [companyModuleMappingId="
				+ companyModuleMappingId + ", company=" + company
				+ ", moduleMaster=" + moduleMaster + "]";
	}

}