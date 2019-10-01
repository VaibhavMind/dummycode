package com.payasia.dao.bean;

import java.io.Serializable;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * The persistent class for the Dynamic_Form database table.
 * 
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "Dynamic_Form")
public class DynamicForm extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private DynamicFormPK id;

	@Column(name = "Active")
	private boolean active;

	@Column(name = "Meta_Data")
	private String metaData;

	@Column(name = "Tab_Name")
	private String tabName;

	@Column(name = "Effective_Part")
	private Integer effectivePart;

	@Column(name = "Effective_Year")
	private Integer effectiveYear;

	 
	@ManyToOne
	@JoinColumn(name = "Effective_Month")
	private MonthMaster monthMaster;

	 
	@ManyToOne
	@JoinColumn(name = "Company_ID", insertable = false, updatable = false)
	private Company company;

	 
	@ManyToOne
	@JoinColumn(name = "Entity_ID", insertable = false, updatable = false)
	private EntityMaster entityMaster;

	public DynamicForm() {
	}

	public DynamicFormPK getId() {
		return this.id;
	}

	public void setId(DynamicFormPK id) {
		this.id = id;
	}

	public boolean getActive() {
		return this.active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getMetaData() {
		return this.metaData;
	}

	public void setMetaData(String metaData) {
		this.metaData = metaData;
	}

	public String getTabName() {
		return this.tabName;
	}

	public void setTabName(String tabName) {
		this.tabName = tabName;
	}

	public Company getCompany() {
		return this.company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public EntityMaster getEntityMaster() {
		return this.entityMaster;
	}

	public void setEntityMaster(EntityMaster entityMaster) {
		this.entityMaster = entityMaster;
	}

	public Integer getEffectivePart() {
		return effectivePart;
	}

	public void setEffectivePart(Integer effectivePart) {
		this.effectivePart = effectivePart;
	}

	public Integer getEffectiveYear() {
		return effectiveYear;
	}

	public void setEffectiveYear(Integer effectiveYear) {
		this.effectiveYear = effectiveYear;
	}

	public MonthMaster getMonthMaster() {
		return monthMaster;
	}

	public void setMonthMaster(MonthMaster monthMaster) {
		this.monthMaster = monthMaster;
	}

}