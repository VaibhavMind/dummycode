package com.payasia.dao.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * The persistent class for the Company_Address_Mapping database table.
 * 
 */
@Entity
@Table(name = "Company_Address_Mapping")
public class CompanyAddressMapping implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Company_Address_ID")
	private long companyAddressId;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "Company_ID")
	private Company company;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "Data_Dictionary_ID")
	private DataDictionary dataDictionary;

	public CompanyAddressMapping() {
	}

	public long getCompanyAddressId() {
		return companyAddressId;
	}

	public void setCompanyAddressId(long companyAddressId) {
		this.companyAddressId = companyAddressId;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public DataDictionary getDataDictionary() {
		return dataDictionary;
	}

	public void setDataDictionary(DataDictionary dataDictionary) {
		this.dataDictionary = dataDictionary;
	}

}