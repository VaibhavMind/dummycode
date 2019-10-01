package com.payasia.dao.bean;

import java.io.Serializable;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * The persistent class for the Multi_Lingual_Data database table.
 * 
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "Multi_Lingual_Data")
public class MultiLingualData extends CompanyBaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private MultiLingualDataPK id;

	@Column(name = "Label")
	private String label;

	 
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "Data_Dictionary_ID", insertable = false, updatable = false)
	private DataDictionary dataDictionary;

	 
	@ManyToOne
	@JoinColumn(name = "Language_ID", insertable = false, updatable = false)
	private LanguageMaster languageMaster;

	public MultiLingualData() {
	}

	public MultiLingualDataPK getId() {
		return this.id;
	}

	public void setId(MultiLingualDataPK id) {
		this.id = id;
	}

	public String getLabel() {
		return this.label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public DataDictionary getDataDictionary() {
		return this.dataDictionary;
	}

	public void setDataDictionary(DataDictionary dataDictionary) {
		this.dataDictionary = dataDictionary;
	}

	public LanguageMaster getLanguageMaster() {
		return this.languageMaster;
	}

	public void setLanguageMaster(LanguageMaster languageMaster) {
		this.languageMaster = languageMaster;
	}

}