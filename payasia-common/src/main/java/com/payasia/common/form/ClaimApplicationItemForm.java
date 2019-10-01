package com.payasia.common.form;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.payasia.common.dto.ClaimCustomFieldDTO;
import com.payasia.common.dto.LeaveCustomFieldDTO;
import com.payasia.common.dto.ValidationClaimItemDTO;

@JsonInclude(Include.NON_NULL)
public class ClaimApplicationItemForm implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -206005874904336191L;

	private Long employeeClaimTemplateItemId;
	private Long claimApplicationItemID;
	private String claimApplicationClaimItemName;
	private Long claimApplicationID;
	private String receiptNumber;
	private String claimDate;
	private BigDecimal claimAmount;
	private BigDecimal amountBeforeTax;
	private BigDecimal taxAmount;
	private Integer taxAmountPer;
	private String remarks;
	private Long employeeClaimTemplateItemID;
	private Float quantity;
	private BigDecimal unitPrice;
	private List<ClaimTemplateForm> claimTemplates;
	private BigDecimal applicableClaimAmount;
	private Integer applicableClaimAmountPer;
	private Boolean isQuantityBased;
	private Boolean isAmountBased;
	private Boolean isForexBased;
	private String category;
	private List<ClaimCustomFieldDTO> customFields;
	private List<ClaimApplicationItemAttach> attachements;
	private List<ClaimApplicationItemWorkflowForm> claimApplicationItemWorkflowForms;
	private Boolean rejectStatus;
	private Boolean active;
	private Boolean rejected;
	private Long currencyId;
	private BigDecimal forexRate;
	private String claimType;
	private BigDecimal receiptAmount;
	private Integer currencyYear;
	private Integer currencyMonth;
	private String currencyName;
	private String guideLines;
	private String claimItemName;
	private String claimItemStatus;
	private Boolean isAdmin;
	private BigDecimal forexAmount;
	private boolean allowOverrideTaxAmt;
	private String claimantName;
	private boolean openToDependents;
	private String currencyCode;
	private Long blockId;
	private String blockName;
	private Long afeId;
	private String afeName;
	private boolean isLundinTimesheetModule;
	private String accountCodeStartWith;
	private boolean requestFromWebService;
	private ValidationClaimItemDTO validationClaimItemDTO;
    private BigDecimal defaultUnitPrice;
    
    private String claimCreatedDate;
	private String lastUpdatedDate;
	
	public BigDecimal getDefaultUnitPrice() {
		return defaultUnitPrice;
	}
	public void setDefaultUnitPrice(BigDecimal defaultUnitPrice) {
		this.defaultUnitPrice = defaultUnitPrice;
	}
	public String getGuideLines() {
		return guideLines;
	}

	public void setGuideLines(String guideLines) {
		this.guideLines = guideLines;
	}

	public Integer getCurrencyYear() {
		return currencyYear;
	}

	public void setCurrencyYear(Integer currencyYear) {
		this.currencyYear = currencyYear;
	}

	public Integer getCurrencyMonth() {
		return currencyMonth;
	}

	public void setCurrencyMonth(Integer currencyMonth) {
		this.currencyMonth = currencyMonth;
	}

	public BigDecimal getReceiptAmount() {
		return receiptAmount;
	}

	public void setReceiptAmount(BigDecimal receiptAmount) {
		this.receiptAmount = receiptAmount;
	}

	public String getClaimType() {
		return claimType;
	}

	public void setClaimType(String claimType) {
		this.claimType = claimType;
	}

	public Long getCurrencyId() {
		return currencyId;
	}

	public void setCurrencyId(Long currencyId) {
		this.currencyId = currencyId;
	}

	public Boolean getRejected() {
		return rejected;
	}

	public void setRejected(Boolean rejected) {
		this.rejected = rejected;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public Boolean getRejectStatus() {
		return rejectStatus;
	}

	public void setRejectStatus(Boolean rejectStatus) {
		this.rejectStatus = rejectStatus;
	}

	public List<ClaimApplicationItemWorkflowForm> getClaimApplicationItemWorkflowForms() {
		return claimApplicationItemWorkflowForms;
	}

	public void setClaimApplicationItemWorkflowForms(
			List<ClaimApplicationItemWorkflowForm> claimApplicationItemWorkflowForms) {
		this.claimApplicationItemWorkflowForms = claimApplicationItemWorkflowForms;
	}

	public Integer getTaxAmountPer() {
		return taxAmountPer;
	}

	public void setTaxAmountPer(Integer taxAmountPer) {
		this.taxAmountPer = taxAmountPer;
	}

	public Integer getApplicableClaimAmountPer() {
		return applicableClaimAmountPer;
	}

	public void setApplicableClaimAmountPer(Integer applicableClaimAmountPer) {
		this.applicableClaimAmountPer = applicableClaimAmountPer;
	}

	public List<ClaimApplicationItemAttach> getAttachements() {
		return attachements;
	}

	public void setAttachements(List<ClaimApplicationItemAttach> attachements) {
		this.attachements = attachements;
	}

	public String getClaimApplicationClaimItemName() {
		return claimApplicationClaimItemName;
	}

	public void setClaimApplicationClaimItemName(
			String claimApplicationClaimItemName) {
		this.claimApplicationClaimItemName = claimApplicationClaimItemName;
	}

	public List<ClaimCustomFieldDTO> getCustomFields() {
		return customFields;
	}

	public void setCustomFields(List<ClaimCustomFieldDTO> customFields) {
		this.customFields = customFields;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public Boolean getIsQuantityBased() {
		return isQuantityBased;
	}

	public void setIsQuantityBased(Boolean isQuantityBased) {
		this.isQuantityBased = isQuantityBased;
	}

	public Boolean getIsAmountBased() {
		return isAmountBased;
	}

	public void setIsAmountBased(Boolean isAmountBased) {
		this.isAmountBased = isAmountBased;
	}

	public Boolean getIsForexBased() {
		return isForexBased;
	}

	public void setIsForexBased(Boolean isForexBased) {
		this.isForexBased = isForexBased;
	}

	public BigDecimal getApplicableClaimAmount() {
		return applicableClaimAmount;
	}

	public void setApplicableClaimAmount(BigDecimal applicableClaimAmount) {
		this.applicableClaimAmount = applicableClaimAmount;
	}

	private List<ClaimCustomFieldDTO> customFieldDTOList = LazyList.decorate(
			new ArrayList<LeaveCustomFieldDTO>(),
			FactoryUtils.instantiateFactory(ClaimCustomFieldDTO.class));

	public List<ClaimCustomFieldDTO> getCustomFieldDTOList() {
		return customFieldDTOList;
	}

	public void setCustomFieldDTOList(
			List<ClaimCustomFieldDTO> customFieldDTOList) {
		this.customFieldDTOList = customFieldDTOList;
	}

	public BigDecimal getClaimAmount() {
		return claimAmount;
	}

	public void setClaimAmount(BigDecimal claimAmount) {
		this.claimAmount = claimAmount;
	}

	public Long getEmployeeClaimTemplateItemId() {
		return employeeClaimTemplateItemId;
	}

	public void setEmployeeClaimTemplateItemId(Long employeeClaimTemplateItemId) {
		this.employeeClaimTemplateItemId = employeeClaimTemplateItemId;
	}

	public List<ClaimTemplateForm> getClaimTemplates() {
		return claimTemplates;
	}

	public void setClaimTemplates(List<ClaimTemplateForm> claimTemplates) {
		this.claimTemplates = claimTemplates;
	}

	public Long getClaimApplicationItemID() {
		return claimApplicationItemID;
	}

	public void setClaimApplicationItemID(Long claimApplicationItemID) {
		this.claimApplicationItemID = claimApplicationItemID;
	}

	public Long getClaimApplicationID() {
		return claimApplicationID;
	}

	public void setClaimApplicationID(Long claimApplicationID) {
		this.claimApplicationID = claimApplicationID;
	}

	public String getReceiptNumber() {
		return receiptNumber;
	}

	public void setReceiptNumber(String receiptNumber) {
		this.receiptNumber = receiptNumber;
	}

	public String getClaimDate() {
		return claimDate;
	}

	public void setClaimDate(String claimDate) {
		this.claimDate = claimDate;
	}

	public BigDecimal getAmountBeforeTax() {
		return amountBeforeTax;
	}

	public void setAmountBeforeTax(BigDecimal amountBeforeTax) {
		this.amountBeforeTax = amountBeforeTax;
	}

	public BigDecimal getTaxAmount() {
		return taxAmount;
	}

	public void setTaxAmount(BigDecimal taxAmount) {
		this.taxAmount = taxAmount;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Long getEmployeeClaimTemplateItemID() {
		return employeeClaimTemplateItemID;
	}

	public void setEmployeeClaimTemplateItemID(Long employeeClaimTemplateItemID) {
		this.employeeClaimTemplateItemID = employeeClaimTemplateItemID;
	}

	public Float getQuantity() {
		return quantity;
	}

	public void setQuantity(Float quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}

	public BigDecimal getForexRate() {
		return forexRate;
	}

	public void setForexRate(BigDecimal forexRate) {
		this.forexRate = forexRate;
	}

	public String getClaimItemStatus() {
		return claimItemStatus;
	}

	public void setClaimItemStatus(String claimItemStatus) {
		this.claimItemStatus = claimItemStatus;
	}

	public String getClaimItemName() {
		return claimItemName;
	}

	public void setClaimItemName(String claimItemName) {
		this.claimItemName = claimItemName;
	}

	public Boolean getIsAdmin() {
		return isAdmin;
	}

	public void setIsAdmin(Boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

	public BigDecimal getForexAmount() {
		return forexAmount;
	}

	public void setForexAmount(BigDecimal forexAmount) {
		this.forexAmount = forexAmount;
	}

	public String getCurrencyName() {
		return currencyName;
	}

	public void setCurrencyName(String currencyName) {
		this.currencyName = currencyName;
	}

	public boolean isAllowOverrideTaxAmt() {
		return allowOverrideTaxAmt;
	}

	public void setAllowOverrideTaxAmt(boolean allowOverrideTaxAmt) {
		this.allowOverrideTaxAmt = allowOverrideTaxAmt;
	}

	public String getClaimantName() {
		return claimantName;
	}

	public void setClaimantName(String claimantName) {
		this.claimantName = claimantName;
	}

	public boolean isOpenToDependents() {
		return openToDependents;
	}

	public void setOpenToDependents(boolean openToDependents) {
		this.openToDependents = openToDependents;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public Long getBlockId() {
		return blockId;
	}

	public void setBlockId(Long blockId) {
		this.blockId = blockId;
	}

	public Long getAfeId() {
		return afeId;
	}

	public void setAfeId(Long afeId) {
		this.afeId = afeId;
	}


	public boolean isLundinTimesheetModule() {
		return isLundinTimesheetModule;
	}

	public void setLundinTimesheetModule(boolean isLundinTimesheetModule) {
		this.isLundinTimesheetModule = isLundinTimesheetModule;
	}

	public String getBlockName() {
		return blockName;
	}

	public void setBlockName(String blockName) {
		this.blockName = blockName;
	}

	public String getAfeName() {
		return afeName;
	}

	public void setAfeName(String afeName) {
		this.afeName = afeName;
	}


	public String getAccountCodeStartWith() {
		return accountCodeStartWith;
	}

	public void setAccountCodeStartWith(String accountCodeStartWith) {
		this.accountCodeStartWith = accountCodeStartWith;
	}

	public boolean isRequestFromWebService() {
		return requestFromWebService;
	}

	public void setRequestFromWebService(boolean requestFromWebService) {
		this.requestFromWebService = requestFromWebService;
	}

	public ValidationClaimItemDTO getValidationClaimItemDTO() {
		return validationClaimItemDTO;
	}

	public void setValidationClaimItemDTO(ValidationClaimItemDTO validationClaimItemDTO) {
		this.validationClaimItemDTO = validationClaimItemDTO;
	}
	
	public String getClaimCreatedDate() {
		return claimCreatedDate;
	}
	public void setClaimCreatedDate(String claimCreatedDate) {
		this.claimCreatedDate = claimCreatedDate;
	}
	public String getLastUpdatedDate() {
		return lastUpdatedDate;
	}
	public void setLastUpdatedDate(String lastUpdatedDate) {
		this.lastUpdatedDate = lastUpdatedDate;
	}

}
