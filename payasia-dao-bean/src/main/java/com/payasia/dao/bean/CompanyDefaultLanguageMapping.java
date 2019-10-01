package com.payasia.dao.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Company_Default_Language_Mapping")
public class CompanyDefaultLanguageMapping {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private int id;

	@Column(name = "Company_ID")
	private int companyId;

	@ManyToOne
	@JoinColumn(name = "Language_ID")
	private LanguageMaster languageMaster;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCompanyId() {
		return companyId;
	}

	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}

	public LanguageMaster getLanguageMaster() {
		return languageMaster;
	}

	public void setLanguageMaster(LanguageMaster languageMaster) {
		this.languageMaster = languageMaster;
	}

}
