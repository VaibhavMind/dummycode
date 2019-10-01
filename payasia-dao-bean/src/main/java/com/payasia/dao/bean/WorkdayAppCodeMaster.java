package com.payasia.dao.bean;

import java.io.Serializable;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Entity implementation class for Entity: WorkdayAppCodeMaster
 *
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Table(name = "Workday_App_Code_Master")
public class WorkdayAppCodeMaster implements Serializable {

	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Workday_App_Code_ID")
	private long workdayAppCodeId;
	
	@Column(name = "Category")
	private String category;
	
	@Column(name = "Code_Desc")
	private String codeDesc;
	
	@Column(name = "Code_Value")
	private String codeValue;
	
	@Column(name = "Type")
	private String type;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "Country_ID")
	private CountryMaster country;

	public long getWorkdayAppCodeId() {
		return workdayAppCodeId;
	}

	public void setWorkdayAppCodeId(long workdayAppCodeId) {
		this.workdayAppCodeId = workdayAppCodeId;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getCodeDesc() {
		return codeDesc;
	}

	public void setCodeDesc(String codeDesc) {
		this.codeDesc = codeDesc;
	}

	public String getCodeValue() {
		return codeValue;
	}

	public void setCodeValue(String codeValue) {
		this.codeValue = codeValue;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public CountryMaster getCountry() {
		return country;
	}

	public void setCountry(CountryMaster country) {
		this.country = country;
	}
   
}
