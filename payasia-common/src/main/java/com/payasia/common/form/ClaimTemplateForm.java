package com.payasia.common.form;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;

import com.payasia.common.dto.AppCodeDTO;
import com.payasia.common.dto.ClaimAmtReviewersTemplateDTO;
import com.payasia.common.dto.ClaimTemplateConditionDTO;

public class ClaimTemplateForm implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8027370500903953684L;
	private String templateName;
	private Long claimTemplateId;
	private Long claimTemplateItemId;
	private String claimTemplateName;
	private Long employeeClaimTemplateId;
	private String noOfItems;
	private String active;
	private Boolean visibility;
	private String configure;
	private String optionValue;
	private Long optionId;
	private String workFlowLevel;
	private String workFlowLevelCT;
	private Boolean allowOverrideCTL1;
	private Boolean allowOverrideCTL2;
	private Boolean allowOverrideCTL3;
	private Boolean allowRejectCTL1;
	private Boolean allowRejectCTL2;
	private Boolean allowRejectCTL3;
	private Boolean allowApprove1;
	private Boolean allowApprove2;
	private Boolean allowApprove3;
	private Boolean allowForward1;
	private Boolean allowForward2;
	private Boolean allowForward3;

	private String claimItem;
	private String claimItemDesc;
	private Boolean claimItemVisibility;
	private Long claimItemId;
	private String claimTemplateItemWorkflow;
	private String claimTemplateItemShortList;
	private String claimItemCategory;

	private BigDecimal perClaim;
	private BigDecimal perYear;
	private BigDecimal perMonth;

	private String claimTypeConfigure;
	private Long allowNoOfTimesAppCodeId;
	private String allowNoOfTimesAppCodeVal;
	private String allowNoOfTimesVal;

	private Long currencyId;
	List<AppCodeDTO> customFieldList;

	private String defaultCC;
	private Boolean sendMailToDefaultCC;
	
	
	private Long considerAdditionalBalanceFrom;
	private Long considerAdditionalBalanceFromEdit;

	private Boolean allowIfAtLeastOneAttacment;
	private Boolean proration;
	private Long prorationBasedOnAppCodeId;
	private String prorationBasedOnAppCodeVal;

	private Boolean frontEndAppModeClaim;
	private Long frontEndViewModeIdClaim;
	private Boolean backEndAppModeClaim;
	private Long backEndViewModeIdClaim;
	private Boolean frontEndAppModeEditClaim;
	private Long frontEndViewModeIdEditClaim;
	private Boolean backEndAppModeEditClaim;
	private Long backEndViewModeIdEditClaim;
	private Integer cutOffValue;
	private Long prorationMethod;
	private Long claimType;
	
	private String currencyCode;
	
	public Integer getCutOffValue() {
		return cutOffValue;
	}
	public void setCutOffValue(Integer cutOffValue) {
		this.cutOffValue = cutOffValue;
	}
	private Boolean showDefaultUnit;

	private ClaimTemplateItemForm claimTemplateItemForm;
	private List<ClaimItemForm> claimItemCategories;

	private String shortList;

	private ClaimTemplateConditionDTO claimTemplateDTO;
	private String codeDescLocale;
	public String getOptionValue() {
		return optionValue;
	}

	public void setOptionValue(String optionValue) {
		this.optionValue = optionValue;
	}

	public Long getOptionId() {
		return optionId;
	}

	public void setOptionId(Long optionId) {
		this.optionId = optionId;
	}
	
	private Boolean claimReviewersBasedOnClaimAmt;
	private List<ClaimAmtReviewersTemplateDTO> claimAmtRevTemplateDTOList;
	private final List<ClaimAmtReviewersTemplateDTO> claimAmtRevTempDTOList = LazyList
			.decorate(
					new ArrayList<ClaimAmtReviewersTemplateDTO>(),
					FactoryUtils
							.instantiateFactory(ClaimAmtReviewersTemplateDTO.class));

	public Boolean getProration() {
		return proration;
	}

	public void setProration(Boolean proration) {
		this.proration = proration;
	}

	public Boolean getAllowIfAtLeastOneAttacment() {
		return allowIfAtLeastOneAttacment;
	}

	public void setAllowIfAtLeastOneAttacment(Boolean allowIfAtLeastOneAttacment) {
		this.allowIfAtLeastOneAttacment = allowIfAtLeastOneAttacment;
	}

	public List<AppCodeDTO> getCustomFieldList() {
		return customFieldList;
	}

	public void setCustomFieldList(List<AppCodeDTO> customFieldList) {
		this.customFieldList = customFieldList;
	}

	public Long getCurrencyId() {
		return currencyId;
	}

	public void setCurrencyId(Long currencyId) {
		this.currencyId = currencyId;
	}

	public String getClaimTemplateName() {
		return claimTemplateName;
	}

	public void setClaimTemplateName(String claimTemplateName) {
		this.claimTemplateName = claimTemplateName;
	}

	public Long getEmployeeClaimTemplateId() {
		return employeeClaimTemplateId;
	}

	public void setEmployeeClaimTemplateId(Long employeeClaimTemplateId) {
		this.employeeClaimTemplateId = employeeClaimTemplateId;
	}

	public String getClaimItemCategory() {
		return claimItemCategory;
	}

	public void setClaimItemCategory(String claimItemCategory) {
		this.claimItemCategory = claimItemCategory;
	}

	public List<ClaimItemForm> getClaimItemCategories() {
		return claimItemCategories;
	}

	public void setClaimItemCategories(List<ClaimItemForm> claimItemCategories) {
		this.claimItemCategories = claimItemCategories;
	}

	public ClaimTemplateItemForm getClaimTemplateItemForm() {
		return claimTemplateItemForm;
	}

	public void setClaimTemplateItemForm(
			ClaimTemplateItemForm claimTemplateItemForm) {
		this.claimTemplateItemForm = claimTemplateItemForm;
	}

	public Boolean getShowDefaultUnit() {
		return showDefaultUnit;
	}

	public void setShowDefaultUnit(Boolean showDefaultUnit) {
		this.showDefaultUnit = showDefaultUnit;
	}

	public Long getClaimType() {
		return claimType;
	}

	public void setClaimType(Long claimType) {
		this.claimType = claimType;
	}

	public String getClaimTypeConfigure() {
		return claimTypeConfigure;
	}

	public void setClaimTypeConfigure(String claimTypeConfigure) {
		this.claimTypeConfigure = claimTypeConfigure;
	}

	public BigDecimal getPerClaim() {
		return perClaim;
	}

	public void setPerClaim(BigDecimal perClaim) {
		this.perClaim = perClaim;
	}

	public BigDecimal getPerYear() {
		return perYear;
	}

	public void setPerYear(BigDecimal perYear) {
		this.perYear = perYear;
	}

	public BigDecimal getPerMonth() {
		return perMonth;
	}

	public void setPerMonth(BigDecimal perMonth) {
		this.perMonth = perMonth;
	}

	public String getClaimTemplateItemShortList() {
		return claimTemplateItemShortList;
	}

	public void setClaimTemplateItemShortList(String claimTemplateItemShortList) {
		this.claimTemplateItemShortList = claimTemplateItemShortList;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getClaimTemplateItemWorkflow() {
		return claimTemplateItemWorkflow;
	}

	public void setClaimTemplateItemWorkflow(String claimTemplateItemWorkflow) {
		this.claimTemplateItemWorkflow = claimTemplateItemWorkflow;
	}

	public Boolean getAllowApprove1() {
		return allowApprove1;
	}

	public void setAllowApprove1(Boolean allowApprove1) {
		this.allowApprove1 = allowApprove1;
	}

	public Boolean getAllowApprove2() {
		return allowApprove2;
	}

	public void setAllowApprove2(Boolean allowApprove2) {
		this.allowApprove2 = allowApprove2;
	}

	public Boolean getAllowApprove3() {
		return allowApprove3;
	}

	public void setAllowApprove3(Boolean allowApprove3) {
		this.allowApprove3 = allowApprove3;
	}

	public Boolean getAllowForward1() {
		return allowForward1;
	}

	public void setAllowForward1(Boolean allowForward1) {
		this.allowForward1 = allowForward1;
	}

	public Boolean getAllowForward2() {
		return allowForward2;
	}

	public void setAllowForward2(Boolean allowForward2) {
		this.allowForward2 = allowForward2;
	}

	public Boolean getAllowForward3() {
		return allowForward3;
	}

	public void setAllowForward3(Boolean allowForward3) {
		this.allowForward3 = allowForward3;
	}

	public String getWorkFlowLevel() {
		return workFlowLevel;
	}

	public void setWorkFlowLevel(String workFlowLevel) {
		this.workFlowLevel = workFlowLevel;
	}

	public Long getClaimItemId() {
		return claimItemId;
	}

	public void setClaimItemId(Long claimItemId) {
		this.claimItemId = claimItemId;
	}

	public String getClaimItem() {
		return claimItem;
	}

	public void setClaimItem(String claimItem) {
		this.claimItem = claimItem;
	}

	public String getClaimItemDesc() {
		return claimItemDesc;
	}

	public void setClaimItemDesc(String claimItemDesc) {
		this.claimItemDesc = claimItemDesc;
	}

	public Boolean getClaimItemVisibility() {
		return claimItemVisibility;
	}

	public void setClaimItemVisibility(Boolean claimItemVisibility) {
		this.claimItemVisibility = claimItemVisibility;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public Long getClaimTemplateId() {
		return claimTemplateId;
	}

	public void setClaimTemplateId(Long claimTemplateId) {
		this.claimTemplateId = claimTemplateId;
	}

	public String getNoOfItems() {
		return noOfItems;
	}

	public void setNoOfItems(String noOfItems) {
		this.noOfItems = noOfItems;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public Boolean getVisibility() {
		return visibility;
	}

	public void setVisibility(Boolean visibility) {
		this.visibility = visibility;
	}

	public String getConfigure() {
		return configure;
	}

	public void setConfigure(String configure) {
		this.configure = configure;
	}

	public String getWorkFlowLevelCT() {
		return workFlowLevelCT;
	}

	public void setWorkFlowLevelCT(String workFlowLevelCT) {
		this.workFlowLevelCT = workFlowLevelCT;
	}

	public Boolean getAllowOverrideCTL1() {
		return allowOverrideCTL1;
	}

	public void setAllowOverrideCTL1(Boolean allowOverrideCTL1) {
		this.allowOverrideCTL1 = allowOverrideCTL1;
	}

	public Boolean getAllowOverrideCTL2() {
		return allowOverrideCTL2;
	}

	public void setAllowOverrideCTL2(Boolean allowOverrideCTL2) {
		this.allowOverrideCTL2 = allowOverrideCTL2;
	}

	public Boolean getAllowOverrideCTL3() {
		return allowOverrideCTL3;
	}

	public void setAllowOverrideCTL3(Boolean allowOverrideCTL3) {
		this.allowOverrideCTL3 = allowOverrideCTL3;
	}

	public Boolean getAllowRejectCTL1() {
		return allowRejectCTL1;
	}

	public void setAllowRejectCTL1(Boolean allowRejectCTL1) {
		this.allowRejectCTL1 = allowRejectCTL1;
	}

	public Boolean getAllowRejectCTL2() {
		return allowRejectCTL2;
	}

	public void setAllowRejectCTL2(Boolean allowRejectCTL2) {
		this.allowRejectCTL2 = allowRejectCTL2;
	}

	public Boolean getAllowRejectCTL3() {
		return allowRejectCTL3;
	}

	public void setAllowRejectCTL3(Boolean allowRejectCTL3) {
		this.allowRejectCTL3 = allowRejectCTL3;
	}

	public String getShortList() {
		return shortList;
	}

	public void setShortList(String shortList) {
		this.shortList = shortList;
	}

	public Long getClaimTemplateItemId() {
		return claimTemplateItemId;
	}

	public void setClaimTemplateItemId(Long claimTemplateItemId) {
		this.claimTemplateItemId = claimTemplateItemId;
	}

	public Long getAllowNoOfTimesAppCodeId() {
		return allowNoOfTimesAppCodeId;
	}

	public void setAllowNoOfTimesAppCodeId(Long allowNoOfTimesAppCodeId) {
		this.allowNoOfTimesAppCodeId = allowNoOfTimesAppCodeId;
	}

	public String getAllowNoOfTimesVal() {
		return allowNoOfTimesVal;
	}

	public void setAllowNoOfTimesVal(String allowNoOfTimesVal) {
		this.allowNoOfTimesVal = allowNoOfTimesVal;
	}

	public String getAllowNoOfTimesAppCodeVal() {
		return allowNoOfTimesAppCodeVal;
	}

	public void setAllowNoOfTimesAppCodeVal(String allowNoOfTimesAppCodeVal) {
		this.allowNoOfTimesAppCodeVal = allowNoOfTimesAppCodeVal;
	}

	public Boolean getSendMailToDefaultCC() {
		return sendMailToDefaultCC;
	}

	public void setSendMailToDefaultCC(Boolean sendMailToDefaultCC) {
		this.sendMailToDefaultCC = sendMailToDefaultCC;
	}

	public String getDefaultCC() {
		return defaultCC;
	}

	public void setDefaultCC(String defaultCC) {
		this.defaultCC = defaultCC;
	}

	public Long getProrationBasedOnAppCodeId() {
		return prorationBasedOnAppCodeId;
	}

	public void setProrationBasedOnAppCodeId(Long prorationBasedOnAppCodeId) {
		this.prorationBasedOnAppCodeId = prorationBasedOnAppCodeId;
	}

	public String getProrationBasedOnAppCodeVal() {
		return prorationBasedOnAppCodeVal;
	}

	public void setProrationBasedOnAppCodeVal(String prorationBasedOnAppCodeVal) {
		this.prorationBasedOnAppCodeVal = prorationBasedOnAppCodeVal;
	}

	public Boolean getFrontEndAppModeClaim() {
		return frontEndAppModeClaim;
	}

	public void setFrontEndAppModeClaim(Boolean frontEndAppModeClaim) {
		this.frontEndAppModeClaim = frontEndAppModeClaim;
	}

	public Long getFrontEndViewModeIdClaim() {
		return frontEndViewModeIdClaim;
	}

	public void setFrontEndViewModeIdClaim(Long frontEndViewModeIdClaim) {
		this.frontEndViewModeIdClaim = frontEndViewModeIdClaim;
	}

	public Boolean getBackEndAppModeClaim() {
		return backEndAppModeClaim;
	}

	public void setBackEndAppModeClaim(Boolean backEndAppModeClaim) {
		this.backEndAppModeClaim = backEndAppModeClaim;
	}

	public Long getBackEndViewModeIdClaim() {
		return backEndViewModeIdClaim;
	}

	public void setBackEndViewModeIdClaim(Long backEndViewModeIdClaim) {
		this.backEndViewModeIdClaim = backEndViewModeIdClaim;
	}

	public Boolean getFrontEndAppModeEditClaim() {
		return frontEndAppModeEditClaim;
	}

	public void setFrontEndAppModeEditClaim(Boolean frontEndAppModeEditClaim) {
		this.frontEndAppModeEditClaim = frontEndAppModeEditClaim;
	}

	public Long getFrontEndViewModeIdEditClaim() {
		return frontEndViewModeIdEditClaim;
	}

	public void setFrontEndViewModeIdEditClaim(Long frontEndViewModeIdEditClaim) {
		this.frontEndViewModeIdEditClaim = frontEndViewModeIdEditClaim;
	}

	public Boolean getBackEndAppModeEditClaim() {
		return backEndAppModeEditClaim;
	}

	public void setBackEndAppModeEditClaim(Boolean backEndAppModeEditClaim) {
		this.backEndAppModeEditClaim = backEndAppModeEditClaim;
	}

	public Long getBackEndViewModeIdEditClaim() {
		return backEndViewModeIdEditClaim;
	}

	public void setBackEndViewModeIdEditClaim(Long backEndViewModeIdEditClaim) {
		this.backEndViewModeIdEditClaim = backEndViewModeIdEditClaim;
	}

	public ClaimTemplateConditionDTO getClaimTemplateDTO() {
		return claimTemplateDTO;
	}

	public void setClaimTemplateDTO(ClaimTemplateConditionDTO claimTemplateDTO) {
		this.claimTemplateDTO = claimTemplateDTO;
	}

	public Boolean getClaimReviewersBasedOnClaimAmt() {
		return claimReviewersBasedOnClaimAmt;
	}

	public void setClaimReviewersBasedOnClaimAmt(
			Boolean claimReviewersBasedOnClaimAmt) {
		this.claimReviewersBasedOnClaimAmt = claimReviewersBasedOnClaimAmt;
	}

	public List<ClaimAmtReviewersTemplateDTO> getClaimAmtRevTemplateDTOList() {
		return claimAmtRevTemplateDTOList;
	}

	public void setClaimAmtRevTemplateDTOList(
			List<ClaimAmtReviewersTemplateDTO> claimAmtRevTemplateDTOList) {
		this.claimAmtRevTemplateDTOList = claimAmtRevTemplateDTOList;
	}

	public List<ClaimAmtReviewersTemplateDTO> getClaimAmtRevTempDTOList() {
		return claimAmtRevTempDTOList;
	}
	public Long getProrationMethod() {
		return prorationMethod;
	}
	public void setProrationMethod(Long prorationMethod) {
		this.prorationMethod = prorationMethod;
	}
	public String getCodeDescLocale() {
		return codeDescLocale;
	}
	public void setCodeDescLocale(String codeDescLocale) {
		this.codeDescLocale = codeDescLocale;
	}
	
	public Long getConsiderAdditionalBalanceFrom() {
		return considerAdditionalBalanceFrom;
	}
	public void setConsiderAdditionalBalanceFrom(Long considerAdditionalBalanceFrom) {
		this.considerAdditionalBalanceFrom = considerAdditionalBalanceFrom;
	}
	
	public Long getConsiderAdditionalBalanceFromEdit() {
		return considerAdditionalBalanceFromEdit;
	}
	public void setConsiderAdditionalBalanceFromEdit(Long considerAdditionalBalanceFromEdit) {
		this.considerAdditionalBalanceFromEdit = considerAdditionalBalanceFromEdit;
	}
	public String getCurrencyCode() {
		return currencyCode;
	}
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}
	
}
