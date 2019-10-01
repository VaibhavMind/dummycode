package com.payasia.common.form;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import com.payasia.common.dto.ClaimApplicationItemDTO;
import com.payasia.common.dto.CustomFieldsDTO;

public class ClaimTempItemConfig implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6066977185278090599L;
	private Boolean isQuantityBasedItem;
	private Integer defaultUnit;
	private BigDecimal defaultUnitPrice;
	private List<CustomFieldsDTO> customFieldDTO;
	private Boolean isAmountBased;
	private Boolean isClaimAmountApplicable;
	private Boolean isForexBased;
	private Integer taxAmountPercentage;
	private Integer amountApplicable;
	private Boolean allowChangeDefaultPrice;
	private Boolean amountBeforeTaxVisible;
	private String guideLines;
	private Boolean allowChangeForexRate;
	private Boolean allowOverrideConvertedAmt;
	private Boolean showDefaultUnit;
	private Boolean receiptNoMandatory;
	private Boolean claimDateMandatory;
	private Boolean remarksMandatory;
	private boolean allowOverrideTaxAmt;
	private boolean openToDependent;
	private List<String> claimantNameList;
	private String currencyCode;
	private Long currencyId;
	private List<ClaimApplicationItemDTO> claimantDTOList;
	private String accountCodeStartWith;
	private boolean overrideReceiptAmountForQuantityBased;
	private String remarks;
	
	
	
	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Long getCurrencyId() {
		return currencyId;
	}

	public void setCurrencyId(Long currencyId) {
		this.currencyId = currencyId;
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

	public String getGuideLines() {
		return guideLines;
	}

	public void setGuideLines(String guideLines) {
		this.guideLines = guideLines;
	}

	public Boolean getIsForexBased() {
		return isForexBased;
	}

	public void setIsForexBased(Boolean isForexBased) {
		this.isForexBased = isForexBased;
	}

	public Boolean getAmountBeforeTaxVisible() {
		return amountBeforeTaxVisible;
	}

	public void setAmountBeforeTaxVisible(Boolean amountBeforeTaxVisible) {
		this.amountBeforeTaxVisible = amountBeforeTaxVisible;
	}

	public Boolean getAllowChangeDefaultPrice() {
		return allowChangeDefaultPrice;
	}

	public void setAllowChangeDefaultPrice(Boolean allowChangeDefaultPrice) {
		this.allowChangeDefaultPrice = allowChangeDefaultPrice;
	}

	public BigDecimal getDefaultUnitPrice() {
		return defaultUnitPrice;
	}

	public void setDefaultUnitPrice(BigDecimal defaultUnitPrice) {
		this.defaultUnitPrice = defaultUnitPrice;
	}

	public Integer getAmountApplicable() {
		return amountApplicable;
	}

	public void setAmountApplicable(Integer amountApplicable) {
		this.amountApplicable = amountApplicable;
	}

	public Integer getTaxAmountPercentage() {
		return taxAmountPercentage;
	}

	public void setTaxAmountPercentage(Integer taxAmountPercentage) {
		this.taxAmountPercentage = taxAmountPercentage;
	}

	public Boolean getIsClaimAmountApplicable() {
		return isClaimAmountApplicable;
	}

	public void setIsClaimAmountApplicable(Boolean isClaimAmountApplicable) {
		this.isClaimAmountApplicable = isClaimAmountApplicable;
	}

	public Boolean getIsAmountBased() {
		return isAmountBased;
	}

	public void setIsAmountBased(Boolean isAmountBased) {
		this.isAmountBased = isAmountBased;
	}

	public List<CustomFieldsDTO> getCustomFieldDTO() {
		return customFieldDTO;
	}

	public void setCustomFieldDTO(List<CustomFieldsDTO> customFieldDTO) {
		this.customFieldDTO = customFieldDTO;
	}

	public Integer getDefaultUnit() {
		return defaultUnit;
	}

	public void setDefaultUnit(Integer defaultUnit) {
		this.defaultUnit = defaultUnit;
	}

	public Boolean getIsQuantityBasedItem() {
		return isQuantityBasedItem;
	}

	public void setIsQuantityBasedItem(Boolean isQuantityBasedItem) {
		this.isQuantityBasedItem = isQuantityBasedItem;
	}

	public Boolean getShowDefaultUnit() {
		return showDefaultUnit;
	}

	public void setShowDefaultUnit(Boolean showDefaultUnit) {
		this.showDefaultUnit = showDefaultUnit;
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

	public boolean isAllowOverrideTaxAmt() {
		return allowOverrideTaxAmt;
	}

	public void setAllowOverrideTaxAmt(boolean allowOverrideTaxAmt) {
		this.allowOverrideTaxAmt = allowOverrideTaxAmt;
	}

	public boolean isOpenToDependent() {
		return openToDependent;
	}

	public void setOpenToDependent(boolean openToDependent) {
		this.openToDependent = openToDependent;
	}

	public List<String> getClaimantNameList() {
		return claimantNameList;
	}

	public void setClaimantNameList(List<String> claimantNameList) {
		this.claimantNameList = claimantNameList;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public List<ClaimApplicationItemDTO> getClaimantDTOList() {
		return claimantDTOList;
	}

	public void setClaimantDTOList(List<ClaimApplicationItemDTO> claimantDTOList) {
		this.claimantDTOList = claimantDTOList;
	}



	public String getAccountCodeStartWith() {
		return accountCodeStartWith;
	}

	public void setAccountCodeStartWith(String accountCodeStartWith) {
		this.accountCodeStartWith = accountCodeStartWith;
	}

	public boolean isOverrideReceiptAmountForQuantityBased() {
		return overrideReceiptAmountForQuantityBased;
	}

	public void setOverrideReceiptAmountForQuantityBased(
			boolean overrideReceiptAmountForQuantityBased) {
		this.overrideReceiptAmountForQuantityBased = overrideReceiptAmountForQuantityBased;
	}

}
