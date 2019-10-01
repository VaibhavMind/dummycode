package com.payasia.dao.bean;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;


/**
 * The persistent class for the Day_Type_Master database table.
 * 
 */
@Entity
@Table(name="Day_Type_Master")
public class DayTypeMaster implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name="Day_Type_ID")
	private long dayTypeId;

	@Column(name="Day_Type")
	private String dayType;

	@Column(name="Day_Type_Desc")
	private String dayTypeDesc;

	 
	@OneToMany(mappedBy="dayTypeMaster")
	private Set<OTApplicationItem> otApplicationItems;

	 
	@OneToMany(mappedBy="dayTypeMaster")
	private Set<OTApplicationItemWorkflow> otApplicationItemWorkflows;

    public DayTypeMaster() {
    }

	public long getDayTypeId() {
		return this.dayTypeId;
	}

	public void setDayTypeId(long dayTypeId) {
		this.dayTypeId = dayTypeId;
	}

	public String getDayType() {
		return this.dayType;
	}

	public void setDayType(String dayType) {
		this.dayType = dayType;
	}

	public String getDayTypeDesc() {
		return this.dayTypeDesc;
	}

	public void setDayTypeDesc(String dayTypeDesc) {
		this.dayTypeDesc = dayTypeDesc;
	}

	public Set<OTApplicationItem> getOtApplicationItems() {
		return this.otApplicationItems;
	}

	public void setOtApplicationItems(Set<OTApplicationItem> otApplicationItems) {
		this.otApplicationItems = otApplicationItems;
	}
	
	public Set<OTApplicationItemWorkflow> getOtApplicationItemWorkflows() {
		return this.otApplicationItemWorkflows;
	}

	public void setOtApplicationItemWorkflows(Set<OTApplicationItemWorkflow> otApplicationItemWorkflows) {
		this.otApplicationItemWorkflows = otApplicationItemWorkflows;
	}
	
}