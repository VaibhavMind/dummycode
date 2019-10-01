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
 * The persistent class for the Language_Master database table.
 * 
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Table(name = "Language_Master")
public class LanguageMaster implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "Language_ID")
	private long languageId;

	@Column(name = "Default_Lang")
	private Boolean defaultLang;

	@Column(name = "Language")
	private String language;

	@Column(name = "Language_Code")
	private String languageCode;

	@Column(name = "Language_Desc")
	private String languageDesc;

	@Column(name = "Active")
	private Boolean languageActive;
	 
	@OneToMany(mappedBy = "languageMaster")
	private Set<MultiLingualData> multiLingualData;

	public LanguageMaster() {
	}

	public long getLanguageId() {
		return this.languageId;
	}

	public void setLanguageId(long languageId) {
		this.languageId = languageId;
	}

	public Boolean getDefaultLang() {
		return this.defaultLang;
	}

	public void setDefaultLang(Boolean defaultLang) {
		this.defaultLang = defaultLang;
	}

	public String getLanguage() {
		return this.language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getLanguageCode() {
		return this.languageCode;
	}

	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

	public String getLanguageDesc() {
		return this.languageDesc;
	}

	public void setLanguageDesc(String languageDesc) {
		this.languageDesc = languageDesc;
	}

	public Set<MultiLingualData> getMultiLingualData() {
		return this.multiLingualData;
	}

	public void setMultiLingualData(Set<MultiLingualData> multiLingualData) {
		this.multiLingualData = multiLingualData;
	}

	public Boolean getLanguageActive() {
		return languageActive;
	}

	public void setLanguageActive(Boolean languageActive) {
		this.languageActive = languageActive;
	}

}