package com.payasia.dao.bean;

import java.io.Serializable;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Table(name = "Workday_Paygroup")
public class WorkdayPaygroup implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Workday_Paygroup_ID")
	private long workdayPaygroupId;
	
	@Column(name = "Paygroup_ID")
	private String paygroupId;
	
	@Column(name = "Paygroup_Name")
	private String paygroupName;
	
	@ManyToOne
	@JoinColumn(name = "Company_ID")
	private Company company;

	public long getWorkdayPaygroupId() {
		return workdayPaygroupId;
	}

	public void setWorkdayPaygroupId(long workdayPaygroupId) {
		this.workdayPaygroupId = workdayPaygroupId;
	}

	public String getPaygroupId() {
		return paygroupId;
	}

	public void setPaygroupId(String paygroupId) {
		this.paygroupId = paygroupId;
	}

	public String getPaygroupName() {
		return paygroupName;
	}

	public void setPaygroupName(String paygroupName) {
		this.paygroupName = paygroupName;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}
}
