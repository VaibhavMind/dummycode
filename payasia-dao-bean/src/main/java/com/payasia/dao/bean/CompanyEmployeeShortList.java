package com.payasia.dao.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * The persistent class for the Company_Employee_Short_List database table.
 * 
 */
@Entity
@Table(name = "Company_Employee_Short_List")
public class CompanyEmployeeShortList extends BaseEntity implements
		Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Short_List_ID")
	private long shortListId;

	@Column(name = "Close_Bracket")
	private String closeBracket;

	@Column(name = "Equality_Operator")
	private String equalityOperator;

	@Column(name = "Logical_Operator")
	private String logicalOperator;

	@Column(name = "Open_Bracket")
	private String openBracket;

	@Column(name = "Value")
	private String value;

	 
	@ManyToOne
	@JoinColumn(name = "Data_Dictionary_ID")
	private DataDictionary dataDictionary;

	 
	@ManyToOne
	@JoinColumns({
			@JoinColumn(name = "Company_ID", referencedColumnName = "Company_ID"),
			@JoinColumn(name = "Employee_ID", referencedColumnName = "Employee_ID"),
			@JoinColumn(name = "Role_ID", referencedColumnName = "Role_ID") })
	private EmployeeRoleMapping employeeRoleMapping;

	public CompanyEmployeeShortList() {
	}

	public long getShortListId() {
		return this.shortListId;
	}

	public void setShortListId(long shortListId) {
		this.shortListId = shortListId;
	}

	public String getCloseBracket() {
		return this.closeBracket;
	}

	public void setCloseBracket(String closeBracket) {
		this.closeBracket = closeBracket;
	}

	public String getEqualityOperator() {
		return this.equalityOperator;
	}

	public void setEqualityOperator(String equalityOperator) {
		this.equalityOperator = equalityOperator;
	}

	public String getLogicalOperator() {
		return this.logicalOperator;
	}

	public void setLogicalOperator(String logicalOperator) {
		this.logicalOperator = logicalOperator;
	}

	public String getOpenBracket() {
		return this.openBracket;
	}

	public void setOpenBracket(String openBracket) {
		this.openBracket = openBracket;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public DataDictionary getDataDictionary() {
		return this.dataDictionary;
	}

	public void setDataDictionary(DataDictionary dataDictionary) {
		this.dataDictionary = dataDictionary;
	}

	public EmployeeRoleMapping getEmployeeRoleMapping() {
		return this.employeeRoleMapping;
	}

	public void setEmployeeRoleMapping(EmployeeRoleMapping employeeRoleMapping) {
		this.employeeRoleMapping = employeeRoleMapping;
	}

}