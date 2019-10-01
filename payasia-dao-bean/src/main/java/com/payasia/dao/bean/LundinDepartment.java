package com.payasia.dao.bean;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Lundin_Department")
public class LundinDepartment extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "Department_ID")
	private long departmentId;

	@ManyToOne
	@JoinColumn(name = "Department_Ref_ID")
	private DynamicFormFieldRefValue dynamicFormFieldRefValue;

	@ManyToOne
	@JoinColumn(name = "Company_ID")
	private Company company;

	@Column(name = "Effective_Date")
	private Timestamp effectiveDate;

	@Column(name = "Status")
	private boolean status;

	@ManyToOne
	@JoinColumn(name = "Department_Type")
	private AppCodeMaster departmentType;

	@Column(name = "Display_Order")
	private int order;

	@ManyToOne
	@JoinColumn(name = "Default_Block")
	private LundinBlock defaultBlock;

	@ManyToOne
	@JoinColumn(name = "Default_AFE")
	private LundinAFE defaultAFE;

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public DynamicFormFieldRefValue getDynamicFormFieldRefValue() {
		return dynamicFormFieldRefValue;
	}

	public void setDynamicFormFieldRefValue(
			DynamicFormFieldRefValue dynamicFormFieldRefValue) {
		this.dynamicFormFieldRefValue = dynamicFormFieldRefValue;
	}

	public long getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(long departmentId) {
		this.departmentId = departmentId;
	}

	public Timestamp getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(Timestamp effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public AppCodeMaster getDepartmentType() {
		return departmentType;
	}

	public void setDepartmentType(AppCodeMaster departmentType) {
		this.departmentType = departmentType;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public LundinBlock getDefaultBlock() {
		return defaultBlock;
	}

	public void setDefaultBlock(LundinBlock defaultBlock) {
		this.defaultBlock = defaultBlock;
	}

	public LundinAFE getDefaultAFE() {
		return defaultAFE;
	}

	public void setDefaultAFE(LundinAFE defaultAFE) {
		this.defaultAFE = defaultAFE;
	}

}
