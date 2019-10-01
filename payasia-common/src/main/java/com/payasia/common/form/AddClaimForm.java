package com.payasia.common.form;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.payasia.common.dto.ClaimApplicationItemDTO;
import com.payasia.common.dto.ClaimItemBalanceDTO;
import com.payasia.common.dto.ClaimTemplateItemDTO;
import com.payasia.common.dto.DataImportLogDTO;
import com.payasia.common.dto.ValidateClaimApplicationDTO;
import com.payasia.common.dto.ValidationClaimItemDTO;

@JsonInclude(Include.NON_NULL)
public class AddClaimForm implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7144561704211639497L;
	private Long employeeClaimTemplateItemId;
	private Long claimTemplateId;
	private String claimTemplateName;
	private Long claimTemplateItemId;
	private String claimTemplateItemName;
	private String claimItemName;
	private List<AddClaimForm> claimTemplateItemList;
	private String receiptNumber;
	private String claimDate;
	private BigDecimal claimAmount;
	private String totalItems;
	private BigDecimal amountBeforeTax;
	private BigDecimal taxAmount;
	private Long claimNumber;
	private String createdDate;
	private String remarks;
	private String helpText;
	
	private Integer totalClaimItems;
	private String claimReviewer1;
	private String claimReviewer2;
	private String claimReviewer3;
	private Long claimReviewer2Id;
	private Long claimReviewer3Id;
	private String addClaimStatus;
	private String applyTo;
	private String emailCC;
	private Long applyToId;
	private Integer totalNoOfReviewers;
	private Long claimApplicationId;
	private String createDate;
	private ClaimApplicationForm claimApplicationForm;
	private Boolean claimApplicationStatus;
	private HashMap<Long, ClaimApplicationItemDTO> claimApplicationItems;
	private List<ClaimTemplateForm> claimTemplates;
	private ClaimTempItemConfig claimTempItemConfig;
	private List<ClaimApplicationItemForm> claimItems;
	private ClaimApplicationItemForm claimApplicationItemForm;
	private String status;
	private String action;
	private BigDecimal forexRate;
	private Boolean isApproved;
	private List<ClaimApplicationWorkflowForm> claimWorkflows;
	private Long claimApplicationReviewerId;
	private ClaimItemBalanceDTO claimItemBalanceDTO;
	private ValidationClaimItemDTO validationClaimItemDTO;
	private ValidateClaimApplicationDTO validateClaimApplicationDTO;
	private List<DataImportLogDTO> dataImportLogDTOs;
	private Boolean dataValid;
	private Boolean claimReviewerNotDefined;
	private Boolean visibleToEmployee;
	private Long employeeClaimTemplateId;
	private long claimApplicationItemId;
	private boolean isLundinTimesheetModule;
	private BigDecimal defaultUnitPrice;
	
	private List<ClaimApplicationItemDTO> claimApplicationItemsList;
	
	private String messsage;
	
	private byte[] claimReviewer1Img;
	private byte[] claimReviewer2Img;
	private byte[] claimReviewer3Img;
	
	public BigDecimal getDefaultUnitPrice() {
		return defaultUnitPrice;
	}
	public void setDefaultUnitPrice(BigDecimal defaultUnitPrice) {
		this.defaultUnitPrice = defaultUnitPrice;
	}
	private String paidDate;
	
	
	
	
	public Boolean getClaimReviewerNotDefined() {
		return claimReviewerNotDefined;
	}
	public void setClaimReviewerNotDefined(Boolean claimReviewerNotDefined) {
		this.claimReviewerNotDefined = claimReviewerNotDefined;
	}
	public Boolean getDataValid() {
		return dataValid;
	}
	public void setDataValid(Boolean dataValid) {
		this.dataValid = dataValid;
	}
	public List<DataImportLogDTO> getDataImportLogDTOs() {
		return dataImportLogDTOs;
	}
	public void setDataImportLogDTOs(List<DataImportLogDTO> dataImportLogDTOs) {
		this.dataImportLogDTOs = dataImportLogDTOs;
	}
	public ValidateClaimApplicationDTO getValidateClaimApplicationDTO() {
		return validateClaimApplicationDTO;
	}
	public void setValidateClaimApplicationDTO(
			ValidateClaimApplicationDTO validateClaimApplicationDTO) {
		this.validateClaimApplicationDTO = validateClaimApplicationDTO;
	}
	public ValidationClaimItemDTO getValidationClaimItemDTO() {
		return validationClaimItemDTO;
	}
	public void setValidationClaimItemDTO(
			ValidationClaimItemDTO validationClaimItemDTO) {
		this.validationClaimItemDTO = validationClaimItemDTO;
	}
	public ClaimItemBalanceDTO getClaimItemBalanceDTO() {
		return claimItemBalanceDTO;
	}
	public void setClaimItemBalanceDTO(ClaimItemBalanceDTO claimItemBalanceDTO) {
		this.claimItemBalanceDTO = claimItemBalanceDTO;
	}
	public List<ClaimApplicationWorkflowForm> getClaimWorkflows() {
		return claimWorkflows;
	}
	public void setClaimWorkflows(List<ClaimApplicationWorkflowForm> claimWorkflows) {
		this.claimWorkflows = claimWorkflows;
	}
	public Boolean getIsApproved() {
		return isApproved;
	}
	public void setIsApproved(Boolean isApproved) {
		this.isApproved = isApproved;
	}
	public BigDecimal getForexRate() {
		return forexRate;
	}
	public void setForexRate(BigDecimal forexRate) {
		this.forexRate = forexRate;
	}
	public Long getClaimNumber() {
		return claimNumber;
	}
	public void setClaimNumber(Long claimNumber) {
		this.claimNumber = claimNumber;
	}
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	

	
	public String getClaimItemName() {
		return claimItemName;
	}
	public void setClaimItemName(String claimItemName) {
		this.claimItemName = claimItemName;
	}

	public String getTotalItems() {
		return totalItems;
	}
	public void setTotalItems(String totalItems) {
		this.totalItems = totalItems;
	}
	public ClaimApplicationItemForm getClaimApplicationItemForm() {
		return claimApplicationItemForm;
	}
	public void setClaimApplicationItemForm(
			ClaimApplicationItemForm claimApplicationItemForm) {
		this.claimApplicationItemForm = claimApplicationItemForm;
	}
	public List<ClaimApplicationItemForm> getClaimItems() {
		return claimItems;
	}
	public void setClaimItems(List<ClaimApplicationItemForm> claimItems) {
		this.claimItems = claimItems;
	}
	public ClaimTempItemConfig getClaimTempItemConfig() {
		return claimTempItemConfig;
	}
	public void setClaimTempItemConfig(ClaimTempItemConfig claimTempItemConfig) {
		this.claimTempItemConfig = claimTempItemConfig;
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
	public HashMap<Long, ClaimApplicationItemDTO> getClaimApplicationItems() {
		return claimApplicationItems;
	}
	public void setClaimApplicationItems(
			HashMap<Long, ClaimApplicationItemDTO> claimApplicationItems) {
		this.claimApplicationItems = claimApplicationItems;
	}
	public Boolean getClaimApplicationStatus() {
		return claimApplicationStatus;
	}
	public void setClaimApplicationStatus(Boolean claimApplicationStatus) {
		this.claimApplicationStatus = claimApplicationStatus;
	}
	private List<ClaimTemplateItemDTO> claimTemplateItemDTOList = LazyList.decorate(
			new ArrayList<ClaimTemplateItemDTO>(),
			FactoryUtils.instantiateFactory(ClaimTemplateItemDTO.class));
	
	
	
	public ClaimApplicationForm getClaimApplicationForm() {
		return claimApplicationForm;
	}
	public void setClaimApplicationForm(ClaimApplicationForm claimApplicationForm) {
		this.claimApplicationForm = claimApplicationForm;
	}
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	public Long getClaimApplicationId() {
		return claimApplicationId;
	}
	public void setClaimApplicationId(Long claimApplicationId) {
		this.claimApplicationId = claimApplicationId;
	}
	public Integer getTotalNoOfReviewers() {
		return totalNoOfReviewers;
	}
	public void setTotalNoOfReviewers(Integer totalNoOfReviewers) {
		this.totalNoOfReviewers = totalNoOfReviewers;
	}
	public Long getClaimReviewer2Id() {
		return claimReviewer2Id;
	}
	public void setClaimReviewer2Id(Long claimReviewer2Id) {
		this.claimReviewer2Id = claimReviewer2Id;
	}
	public Long getClaimReviewer3Id() {
		return claimReviewer3Id;
	}
	public void setClaimReviewer3Id(Long claimReviewer3Id) {
		this.claimReviewer3Id = claimReviewer3Id;
	}
	public Long getApplyToId() {
		return applyToId;
	}
	public void setApplyToId(Long applyToId) {
		this.applyToId = applyToId;
	}
	public String getEmailCC() {
		return emailCC;
	}
	public void setEmailCC(String emailCC) {
		this.emailCC = emailCC;
	}
	public String getApplyTo() {
		return applyTo;
	}
	public void setApplyTo(String applyTo) {
		this.applyTo = applyTo;
	}
	public String getAddClaimStatus() {
		return addClaimStatus;
	}
	public void setAddClaimStatus(String addClaimStatus) {
		this.addClaimStatus = addClaimStatus;
	}
	public List<ClaimTemplateItemDTO> getClaimTemplateItemDTOList() {
		return claimTemplateItemDTOList;
	}
	public void setClaimTemplateItemDTOList(
			List<ClaimTemplateItemDTO> claimTemplateItemDTOList) {
		this.claimTemplateItemDTOList = claimTemplateItemDTOList;
	}
	public Integer getTotalClaimItems() {
		return totalClaimItems;
	}
	public void setTotalClaimItems(Integer totalClaimItems) {
		this.totalClaimItems = totalClaimItems;
	}
	public String getClaimReviewer1() {
		return claimReviewer1;
	}
	public void setClaimReviewer1(String claimReviewer1) {
		this.claimReviewer1 = claimReviewer1;
	}
	public String getClaimReviewer2() {
		return claimReviewer2;
	}
	public void setClaimReviewer2(String claimReviewer2) {
		this.claimReviewer2 = claimReviewer2;
	}
	public String getClaimReviewer3() {
		return claimReviewer3;
	}
	public void setClaimReviewer3(String claimReviewer3) {
		this.claimReviewer3 = claimReviewer3;
	}
	public BigDecimal getClaimAmount() {
		return claimAmount;
	}
	public void setClaimAmount(BigDecimal claimAmount) {
		this.claimAmount = claimAmount;
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
	
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public List<AddClaimForm> getClaimTemplateItemList() {
		return claimTemplateItemList;
	}
	public void setClaimTemplateItemList(List<AddClaimForm> claimTemplateItemList) {
		this.claimTemplateItemList = claimTemplateItemList;
	}
	public Long getClaimTemplateItemId() {
		return claimTemplateItemId;
	}
	public void setClaimTemplateItemId(Long claimTemplateItemId) {
		this.claimTemplateItemId = claimTemplateItemId;
	}
	public String getClaimTemplateItemName() {
		return claimTemplateItemName;
	}
	public void setClaimTemplateItemName(String claimTemplateItemName) {
		this.claimTemplateItemName = claimTemplateItemName;
	}
	public Long getClaimTemplateId() {
		return claimTemplateId;
	}
	public void setClaimTemplateId(Long claimTemplateId) {
		this.claimTemplateId = claimTemplateId;
	}
	public String getClaimTemplateName() {
		return claimTemplateName;
	}
	public void setClaimTemplateName(String claimTemplateName) {
		this.claimTemplateName = claimTemplateName;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public Long getClaimApplicationReviewerId() {
		return claimApplicationReviewerId;
	}
	public void setClaimApplicationReviewerId(Long claimApplicationReviewerId) {
		this.claimApplicationReviewerId = claimApplicationReviewerId;
	}
	
	public Boolean getVisibleToEmployee() {
		return visibleToEmployee;
	}
	public void setVisibleToEmployee(Boolean visibleToEmployee) {
		this.visibleToEmployee = visibleToEmployee;
	}
	public Long getEmployeeClaimTemplateId() {
		return employeeClaimTemplateId;
	}
	public void setEmployeeClaimTemplateId(Long employeeClaimTemplateId) {
		this.employeeClaimTemplateId = employeeClaimTemplateId;
	}
	public long getClaimApplicationItemId() {
		return claimApplicationItemId;
	}
	public void setClaimApplicationItemId(long claimApplicationItemId) {
		this.claimApplicationItemId = claimApplicationItemId;
	}
	public boolean isLundinTimesheetModule() {
		return isLundinTimesheetModule;
	}
	public void setLundinTimesheetModule(boolean isLundinTimesheetModule) {
		this.isLundinTimesheetModule = isLundinTimesheetModule;
	}
	public String getPaidDate() {
		return paidDate;
	}
	public void setPaidDate(String paidDate) {
		this.paidDate = paidDate;
	}
	public String getHelpText() {
		return helpText;
	}
	public void setHelpText(String helpText) {
		this.helpText = helpText;
	}
	public List<ClaimApplicationItemDTO> getClaimApplicationItemsList() {
		return claimApplicationItemsList;
	}
	public void setClaimApplicationItemsList(List<ClaimApplicationItemDTO> claimApplicationItemsList) {
		this.claimApplicationItemsList = claimApplicationItemsList;
	}
	public String getMesssage() {
		return messsage;
	}
	public void setMesssage(String messsage) {
		this.messsage = messsage;
	}
	public byte[] getClaimReviewer1Img() {
		return claimReviewer1Img;
	}
	public void setClaimReviewer1Img(byte[] claimReviewer1Img) {
		this.claimReviewer1Img = claimReviewer1Img;
	}
	public byte[] getClaimReviewer2Img() {
		return claimReviewer2Img;
	}
	public void setClaimReviewer2Img(byte[] claimReviewer2Img) {
		this.claimReviewer2Img = claimReviewer2Img;
	}
	public byte[] getClaimReviewer3Img() {
		return claimReviewer3Img;
	}
	public void setClaimReviewer3Img(byte[] claimReviewer3Img) {
		this.claimReviewer3Img = claimReviewer3Img;
	}
	
}
