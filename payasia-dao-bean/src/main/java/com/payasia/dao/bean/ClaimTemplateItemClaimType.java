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
@Table(name = "Claim_Template_Item_Claim_Type")
public class ClaimTemplateItemClaimType extends CompanyBaseEntity implements
		Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "Claim_Type_ID")
	private long claimTypeId;

	 
	@ManyToOne
	@JoinColumn(name = "Claim_Template_Item_ID")
	private ClaimTemplateItem claimTemplateItem;

	 
	@ManyToOne
	@JoinColumn(name = "Claim_Type")
	private AppCodeMaster claimType;

	@Column(name = "Min_Limit")
	private Integer minLimit;

	@Column(name = "Max_Limit")
	private Integer maxLimit;

	@Column(name = "Default_Unit")
	private Integer defaultUnit;

	@Column(name = "Show_Default_Unit")
	private Boolean showDefaultUnit;

	@Column(name = "Default_Unit_Price")
	private BigDecimal defaultUnitPrice;

	@Column(name = "Allow_Change_Default_Price")
	private Boolean allowChangeDefaultPrice;

	@Column(name = "Receipt_Amt_Percent_Applicable")
	private Integer receiptAmtPercentApplicable;

	@Column(name = "Allow_Change_Forex_Rate")
	private Boolean allowChangeForexRate;

	@Column(name = "Allow_Override_Converted_Amt")
	private Boolean allowOverrideConvertedAmt;

	public ClaimTemplateItemClaimType() {
	}

	public long getClaimTypeId() {
		return claimTypeId;
	}

	public void setClaimTypeId(long claimTypeId) {
		this.claimTypeId = claimTypeId;
	}

	public ClaimTemplateItem getClaimTemplateItem() {
		return claimTemplateItem;
	}

	public void setClaimTemplateItem(ClaimTemplateItem claimTemplateItem) {
		this.claimTemplateItem = claimTemplateItem;
	}

	public AppCodeMaster getClaimType() {
		return claimType;
	}

	public void setClaimType(AppCodeMaster claimType) {
		this.claimType = claimType;
	}

	public Integer getMinLimit() {
		return minLimit;
	}

	public void setMinLimit(Integer minLimit) {
		this.minLimit = minLimit;
	}

	public Integer getMaxLimit() {
		return maxLimit;
	}

	public void setMaxLimit(Integer maxLimit) {
		this.maxLimit = maxLimit;
	}

	public Integer getDefaultUnit() {
		return defaultUnit;
	}

	public void setDefaultUnit(Integer defaultUnit) {
		this.defaultUnit = defaultUnit;
	}

	public Boolean getShowDefaultUnit() {
		return showDefaultUnit;
	}

	public void setShowDefaultUnit(Boolean showDefaultUnit) {
		this.showDefaultUnit = showDefaultUnit;
	}

	public BigDecimal getDefaultUnitPrice() {
		return defaultUnitPrice;
	}

	public void setDefaultUnitPrice(BigDecimal defaultUnitPrice) {
		this.defaultUnitPrice = defaultUnitPrice;
	}

	public Boolean getAllowChangeDefaultPrice() {
		return allowChangeDefaultPrice;
	}

	public void setAllowChangeDefaultPrice(Boolean allowChangeDefaultPrice) {
		this.allowChangeDefaultPrice = allowChangeDefaultPrice;
	}

	public Integer getReceiptAmtPercentApplicable() {
		return receiptAmtPercentApplicable;
	}

	public void setReceiptAmtPercentApplicable(
			Integer receiptAmtPercentApplicable) {
		this.receiptAmtPercentApplicable = receiptAmtPercentApplicable;
	}

	public Boolean getAllowChangeForexRate() {
		return allowChangeForexRate;
	}

	public void setAllowChangeForexRate(Boolean allowChangeForexRate) {
		this.allowChangeForexRate = allowChangeForexRate;
	}

	public Boolean getAllowOverrideConvertedAmt() {
		return allowOverrideConvertedAmt;
	}

	public void setAllowOverrideConvertedAmt(Boolean allowOverrideConvertedAmt) {
		this.allowOverrideConvertedAmt = allowOverrideConvertedAmt;
	}

}