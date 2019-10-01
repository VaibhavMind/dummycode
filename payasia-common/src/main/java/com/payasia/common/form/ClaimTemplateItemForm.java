package com.payasia.common.form;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;

import com.payasia.common.dto.CustomFieldsDTO;

public class ClaimTemplateItemForm implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8416612425927828157L;
	private Long claimType;
	private Long claimTemplateItemClaimTypeId;
	private Long claimTemplateId;
	private Long claimTemplateItemId;
	private Integer minLimit;
	private Integer maxLimit;
	private Integer defaultUnit;
	private Boolean showDefaultUnit;
	private BigDecimal defaultUnitPrice;
	private Boolean allowChangeDefaultPrice;
	private Integer receiptAmountPercentApplicable;
	private Boolean allowChangeForexRate;
	private Boolean allowOverrideConvertedAmt;
	public Boolean getOpenToDependents() {
		return openToDependents;
	}
	public void setOpenToDependents(Boolean openToDependents) {
		this.openToDependents = openToDependents;
	}
	private Boolean openToDependents;

	private Long generalID;
	private Boolean receiptNoMandatory;
	private Boolean claimDateMandatory;
	private Boolean remarksMandatory;
	private Boolean attachmentMandatory;
	private Boolean allowOverrideTaxAmt;
	private boolean overrideReceiptAmountForQuantityBased;
	private Boolean amountBeforeTaxVisible;
	private Integer taxPercentage;
	private Integer claimsAllowedPerMonth; 
	private Integer backdatedClaimDaysLimit;
	private BigDecimal entitlementPerDay;
	private BigDecimal entitlementPerYear;
	private BigDecimal entitlementPerMonth;
	private Boolean allowExceedEntitlement;
	private String remarks;
	private String helpText;
	
	private Long customFieldID;
	private String fieldName;
	private Boolean mandatory;
	
	private Boolean confAvailable;
	private Boolean proration;
	
	
	
	public Long getClaimTemplateItemClaimTypeId() {
		return claimTemplateItemClaimTypeId;
	}
	public void setClaimTemplateItemClaimTypeId(Long claimTemplateItemClaimTypeId) {
		this.claimTemplateItemClaimTypeId = claimTemplateItemClaimTypeId;
	}
	private List<CustomFieldsDTO> customFieldsDTOList = LazyList.decorate(
			new ArrayList<CustomFieldsDTO>(),
			FactoryUtils.instantiateFactory(CustomFieldsDTO.class));
	
	
	public Boolean getConfAvailable() {
		return confAvailable;
	}
	public void setConfAvailable(Boolean confAvailable) {
		this.confAvailable = confAvailable;
	}
	public List<CustomFieldsDTO> getCustomFieldsDTOList() {
		return customFieldsDTOList;
	}
	public void setCustomFieldsDTOList(List<CustomFieldsDTO> customFieldsDTOList) {
		this.customFieldsDTOList = customFieldsDTOList;
	}
	public BigDecimal getEntitlementPerMonth() {
		return entitlementPerMonth;
	}
	public void setEntitlementPerMonth(BigDecimal entitlementPerMonth) {
		this.entitlementPerMonth = entitlementPerMonth;
	}
	
	
	public Boolean getAllowExceedEntitlement() {
		return allowExceedEntitlement;
	}
	public void setAllowExceedEntitlement(Boolean allowExceedEntitlement) {
		this.allowExceedEntitlement = allowExceedEntitlement;
	}
	public Long getCustomFieldID() {
		return customFieldID;
	}
	public void setCustomFieldID(Long customFieldID) {
		this.customFieldID = customFieldID;
	}
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public Boolean getMandatory() {
		return mandatory;
	}
	public void setMandatory(Boolean mandatory) {
		this.mandatory = mandatory;
	}
	public Long getGeneralID() {
		return generalID;
	}
	public void setGeneralID(Long generalID) {
		this.generalID = generalID;
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
	public BigDecimal getEntitlementPerYear() {
		return entitlementPerYear;
	}
	public void setEntitlementPerYear(BigDecimal entitlementPerYear) {
		this.entitlementPerYear = entitlementPerYear;
	}
	
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public Long getClaimTemplateId() {
		return claimTemplateId;
	}
	public void setClaimTemplateId(Long claimTemplateId) {
		this.claimTemplateId = claimTemplateId;
	}
	public Long getClaimTemplateItemId() {
		return claimTemplateItemId;
	}
	public void setClaimTemplateItemId(Long claimTemplateItemId) {
		this.claimTemplateItemId = claimTemplateItemId;
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
	public Integer getReceiptAmountPercentApplicable() {
		return receiptAmountPercentApplicable;
	}
	public void setReceiptAmountPercentApplicable(
			Integer receiptAmountPercentApplicable) {
		this.receiptAmountPercentApplicable = receiptAmountPercentApplicable;
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
	public Long getClaimType() {
		return claimType;
	}
	public void setClaimType(Long claimType) {
		this.claimType = claimType;
	}
	public Boolean getShowDefaultUnit() {
		return showDefaultUnit;
	}
	public void setShowDefaultUnit(Boolean showDefaultUnit) {
		this.showDefaultUnit = showDefaultUnit;
	}
	public Boolean getAmountBeforeTaxVisible() {
		return amountBeforeTaxVisible;
	}
	public void setAmountBeforeTaxVisible(Boolean amountBeforeTaxVisible) {
		this.amountBeforeTaxVisible = amountBeforeTaxVisible;
	}
	public Boolean getProration() {
		return proration;
	}
	public void setProration(Boolean proration) {
		this.proration = proration;
	}
	public boolean isOverrideReceiptAmountForQuantityBased() {
		return overrideReceiptAmountForQuantityBased;
	}
	public void setOverrideReceiptAmountForQuantityBased(
			boolean overrideReceiptAmountForQuantityBased) {
		this.overrideReceiptAmountForQuantityBased = overrideReceiptAmountForQuantityBased;
	}
	public String getHelpText() {
		return helpText;
	}
	public void setHelpText(String helpText) {
		this.helpText = helpText;
	}

	
	

}
