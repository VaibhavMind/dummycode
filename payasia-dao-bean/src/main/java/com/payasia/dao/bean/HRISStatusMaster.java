package com.payasia.dao.bean;

import java.io.Serializable;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * The persistent class for the HRIS_Status_Master database table.
 * 
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Table(name = "HRIS_Status_Master")
public class HRISStatusMaster implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "HRIS_Status_ID ")
	private long hrisStatusId;

	@Column(name = "HRIS_Status_Name ")
	private String hrisStatusName;

	@Column(name = "HRIS_Status_Desc ")
	private String hrisStatusDesc;

	public HRISStatusMaster() {
	}

	public long getHrisStatusId() {
		return hrisStatusId;
	}

	public void setHrisStatusId(long hrisStatusId) {
		this.hrisStatusId = hrisStatusId;
	}

	public String getHrisStatusName() {
		return hrisStatusName;
	}

	public void setHrisStatusName(String hrisStatusName) {
		this.hrisStatusName = hrisStatusName;
	}

	public String getHrisStatusDesc() {
		return hrisStatusDesc;
	}

	public void setHrisStatusDesc(String hrisStatusDesc) {
		this.hrisStatusDesc = hrisStatusDesc;
	}

}