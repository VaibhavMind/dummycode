package com.payasia.dao.bean;

import java.io.Serializable;
import java.math.BigDecimal;

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
@Table(name = "Claim_Template_Item_General")
public class ClaimTemplateItemGeneral extends CompanyBaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "General_ID")
	private long generalId;
	@Column(name = "Open_To_Dependents")
	private Boolean openToDependents;

	@ManyToOne
	@JoinColumn(name = "Claim_Template_Item_ID")
	private ClaimTemplateItem claimTemplateItem;

	@Column(name = "Receipt_No_Mandatory")
	private Boolean receiptNoMandatory;

	@Column(name = "Claim_Date_Mandatory")
	private Boolean claimDateMandatory;

	@Column(name = "Remarks_Mandatory")
	private Boolean remarksMandatory;

	@Column(name = "Attachment_Mandatory")
	private Boolean attachmentMandatory;

	@Column(name = "Allow_Override_Tax_Amt")
	private Boolean allowOverrideTaxAmt;

	@Column(name = "Tax_Percentage")
	private Integer taxPercentage;

	@Column(name = "Claims_Allowed_Per_Month")
	private Integer claimsAllowedPerMonth;

	@Column(name = "Backdated_Claim_Days_Limit")
	private Integer backdatedClaimDaysLimit;

	@Column(name = "Entitlement_Per_Day")
	private BigDecimal entitlementPerDay = new BigDecimal(0);

	@Column(name = "Entitlement_Per_Month")
	private BigDecimal entitlementPerMonth = new BigDecimal(0);

	@Column(name = "Entitlement_Per_Year")
	private BigDecimal entitlementPerYear = new BigDecimal(0);

	@Column(name = "Allow_Exceed_Entitlement")
	private Boolean allowExceedEntitlement;

	@Column(name = "Remarks")
	private String remarks;

	@Column(name = "Help_Text")
	private String helpText;

	@Column(name = "Amount_Before_Tax_Visible")
	private Boolean amountBeforeTaxVisible;

	@Column(name = "Override_Receipt_Amount_For_Quantity_Based")
	private boolean overrideReceiptAmountForQuantityBased;

	public ClaimTemplateItemGeneral() {
	}

	public long getGeneralId() {
		return generalId;
	}

	public void setGeneralId(long generalId) {
		this.generalId = generalId;
	}

	public ClaimTemplateItem getClaimTemplateItem() {
		return claimTemplateItem;
	}

	public Boolean getOpenToDependents() {
		return openToDependents;
	}

	public void setOpenToDependents(Boolean openToDependents) {
		this.openToDependents = openToDependents;
	}

	public void setClaimTemplateItem(ClaimTemplateItem claimTemplateItem) {
		this.claimTemplateItem = claimTemplateItem;
	}

	public Boolean getReceiptNoMandatory() {
		return receiptNoMandatory;
	}

	public void setReceiptNoMandatory(Boolean receiptNoMandatory) {
		this.receiptNoMandatory = receiptNoMandatory;
	}

	public Boolean getClaimDateMandatory() {
		return claimDateMandatory;
	}

	public void setClaimDateMandatory(Boolean claimDateMandatory) {
		this.claimDateMandatory = claimDateMandatory;
	}

	public Boolean getRemarksMandatory() {
		return remarksMandatory;
	}

	public void setRemarksMandatory(Boolean remarksMandatory) {
		this.remarksMandatory = remarksMandatory;
	}

	public Boolean getAttachmentMandatory() {
		return attachmentMandatory;
	}

	public void setAttachmentMandatory(Boolean attachmentMandatory) {
		this.attachmentMandatory = attachmentMandatory;
	}

	public Boolean getAllowOverrideTaxAmt() {
		return allowOverrideTaxAmt;
	}

	public void setAllowOverrideTaxAmt(Boolean allowOverrideTaxAmt) {
		this.allowOverrideTaxAmt = allowOverrideTaxAmt;
	}

	public Integer getTaxPercentage() {
		return taxPercentage;
	}

	public void setTaxPercentage(Integer taxPercentage) {
		this.taxPercentage = taxPercentage;
	}

	public Integer getClaimsAllowedPerMonth() {
		return claimsAllowedPerMonth;
	}

	public void setClaimsAllowedPerMonth(Integer claimsAllowedPerMonth) {
		this.claimsAllowedPerMonth = claimsAllowedPerMonth;
	}

	public Integer getBackdatedClaimDaysLimit() {
		return backdatedClaimDaysLimit;
	}

	public void setBackdatedClaimDaysLimit(Integer backdatedClaimDaysLimit) {
		this.backdatedClaimDaysLimit = backdatedClaimDaysLimit;
	}

	public BigDecimal getEntitlementPerDay() {
		return entitlementPerDay;
	}

	public void setEntitlementPerDay(BigDecimal entitlementPerDay) {
		this.entitlementPerDay = entitlementPerDay;
	}

	public BigDecimal getEntitlementPerMonth() {
		return entitlementPerMonth;
	}

	public void setEntitlementPerMonth(BigDecimal entitlementPerMonth) {
		this.entitlementPerMonth = entitlementPerMonth;
	}

	public BigDecimal getEntitlementPerYear() {
		return entitlementPerYear;
	}

	public void setEntitlementPerYear(BigDecimal entitlementPerYear) {
		this.entitlementPerYear = entitlementPerYear;
	}

	public Boolean getAllowExceedEntitlement() {
		return allowExceedEntitlement;
	}

	public void setAllowExceedEntitlement(Boolean allowExceedEntitlement) {
		this.allowExceedEntitlement = allowExceedEntitlement;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Boolean getAmountBeforeTaxVisible() {
		return amountBeforeTaxVisible;
	}

	public void setAmountBeforeTaxVisible(Boolean amountBeforeTaxVisible) {
		this.amountBeforeTaxVisible = amountBeforeTaxVisible;
	}

	public boolean isOverrideReceiptAmountForQuantityBased() {
		return overrideReceiptAmountForQuantityBased;
	}

	public void setOverrideReceiptAmountForQuantityBased(boolean overrideReceiptAmountForQuantityBased) {
		this.overrideReceiptAmountForQuantityBased = overrideReceiptAmountForQuantityBased;
	}

	public String getHelpText() {
		return helpText;
	}

	public void setHelpText(String helpText) {
		this.helpText = helpText;
	}

}