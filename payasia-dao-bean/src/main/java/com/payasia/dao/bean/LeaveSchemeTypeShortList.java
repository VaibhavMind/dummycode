package com.payasia.dao.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * The persistent class for the Leave_Scheme_Type_Short_List database table.
 * 
 */
@Entity
@Table(name = "Leave_Scheme_Type_Short_List")
public class LeaveSchemeTypeShortList extends CompanyBaseEntity implements
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
	@JoinColumn(name = "Leave_Scheme_Type_ID")
	private LeaveSchemeType leaveSchemeType;

	public LeaveSchemeTypeShortList() {
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

	public LeaveSchemeType getLeaveSchemeType() {
		return this.leaveSchemeType;
	}

	public void setLeaveSchemeType(LeaveSchemeType leaveSchemeType) {
		this.leaveSchemeType = leaveSchemeType;
	}

}