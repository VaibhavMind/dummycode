package com.payasia.dao.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * The persistent class for the Emp_No_Series_Master database table.
 * 
 */
@Entity
@Table(name = "Emp_No_Series_Master")
public class EmpNoSeriesMaster extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "Emp_No_Series_ID")
	private long empNoSeriesId;

	@Column(name = "Active")
	private boolean active;

	@Column(name = "Prefix")
	private String prefix;

	@Column(name = "Series_Desc")
	private String seriesDesc;

	@Column(name = "Suffix")
	private String suffix;

	 
	@ManyToOne
	@JoinColumn(name = "Company_ID")
	private Company company;

	public EmpNoSeriesMaster() {
	}

	public long getEmpNoSeriesId() {
		return this.empNoSeriesId;
	}

	public void setEmpNoSeriesId(long empNoSeriesId) {
		this.empNoSeriesId = empNoSeriesId;
	}

	public boolean getActive() {
		return this.active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getPrefix() {
		return this.prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getSeriesDesc() {
		return this.seriesDesc;
	}

	public void setSeriesDesc(String seriesDesc) {
		this.seriesDesc = seriesDesc;
	}

	public String getSuffix() {
		return this.suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public Company getCompany() {
		return this.company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

}