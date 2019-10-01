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
 * The persistent class for the Claim_Template_Item database table.
 * 
 */
@Entity
@Table(name = "HR_Letter_Shortlist")
public class HRLetterShortlist extends CompanyBaseEntity implements
		Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "Short_List_ID")
	private long short_List_ID;

	 
	@ManyToOne
	@JoinColumn(name = "HR_Letter_ID")
	private HRLetter hRLetter;

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

	public HRLetterShortlist() {
	}

	public long getShort_List_ID() {
		return short_List_ID;
	}

	public void setShort_List_ID(long short_List_ID) {
		this.short_List_ID = short_List_ID;
	}

	public String getCloseBracket() {
		return closeBracket;
	}

	public void setCloseBracket(String closeBracket) {
		this.closeBracket = closeBracket;
	}

	public String getEqualityOperator() {
		return equalityOperator;
	}

	public void setEqualityOperator(String equalityOperator) {
		this.equalityOperator = equalityOperator;
	}

	public String getLogicalOperator() {
		return logicalOperator;
	}

	public void setLogicalOperator(String logicalOperator) {
		this.logicalOperator = logicalOperator;
	}

	public String getOpenBracket() {
		return openBracket;
	}

	public void setOpenBracket(String openBracket) {
		this.openBracket = openBracket;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public DataDictionary getDataDictionary() {
		return dataDictionary;
	}

	public void setDataDictionary(DataDictionary dataDictionary) {
		this.dataDictionary = dataDictionary;
	}

	public HRLetter gethRLetter() {
		return hRLetter;
	}

	public void sethRLetter(HRLetter hRLetter) {
		this.hRLetter = hRLetter;
	}

}