package com.payasia.common.form;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.payasia.common.dto.ComboValueDTO;
import com.payasia.common.dto.CustomFieldsDTO;
import com.payasia.common.dto.CustomRoundingDTO;
import com.payasia.common.dto.FirstMonthCustomDTO;
import com.payasia.common.dto.LeaveEntitlementDTO;
import com.payasia.common.dto.LeaveSchemeAppCodeDTO;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LeaveSchemeForm implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2211735566627501609L;
	private String templateName;
	private Long leaveSchemeId;
	private String leaveSchemeName;
	private Long leaveSchemeTypeId;
	private String noOfItems;
	private String active;
	private Boolean visibility;
	private String configure;

	private String workFlowLevel;
	private Boolean allowOverrideL1;
	private Boolean allowOverrideL2;
	private Boolean allowOverrideL3;
	private Boolean allowRejectL1;
	private Boolean allowRejectL2;
	private Boolean allowRejectL3;
	private Boolean allowApprove1;
	private Boolean allowApprove2;
	private Boolean allowApprove3;
	private Boolean allowForward1;
	private Boolean allowForward2;
	private Boolean allowForward3;
	private String optionValue;
	private Long optionId;
	private String distMethName;
	private Boolean noProration;

	private Long leaveTypeId;
	private String leaveType;
	private String leaveTypeDesc;
	private Boolean leaveTypeVisibility;
	private String shortList;

	private Long leaveSchemeTypeGrantingId;
	private Long leaveSchemeTypeProrationId;
	private Long leaveSchemeTypeAvailingId;
	private Long leaveSchemeTypeYearEndId;
	private Long leaveCalendar;
	private Long distributionMethod;
	private String leaveGrantDay;
	private String leaveCutOffDay;
	private Long rounding;
	private Boolean holidays;
	private Boolean leaveExtension;
	private Boolean leaveExtensionPreference;
	private Boolean offInclusive;
	private BigDecimal minimumBlockLeave;
	private BigDecimal maximumBlockLeave;
	private BigDecimal maximumDays;
	private Integer maximumFrequency;
	private Boolean allowAdvanceLeavePosting;
	private Integer advLeaveApplyBeforeDays;
	private Integer advLeavePostBeforeDays;
	private Boolean allowBackDateLeavePosting;
	private Integer backDatePostingAfterDays;
	private Integer nextYearPostingBeforeDays;
	private Long considerLeaveBalFrom;
	private Long[] allowOnlyIfBalIsZero;
	private Long[] leaveCanNotCombined;
	private Boolean approvalNotRequired;
	private String defaultCCEmail;
	private Boolean sendMailToDefaultCC;
	private String remarks;
	private Integer leaveCanBeAppOnlyAfterDays;
	private Long applyAfterFromId;

	private Boolean noYearEndProcess;
	private Boolean allowCarryForward;
	private BigDecimal maximumNumberCarryForwarded;
	private BigDecimal annualCarryFwdLimit;
	private Integer expiryDate;
	private Long probationMethod;
	private Long basedOn;
	private Boolean prorationFirstYearOnly;

	private BigDecimal maxLeaves;
	private BigDecimal minLeaves;
	private BigDecimal days;

	private String employeeName;
	private String employeeNumber;

	private AddLeaveForm addLeaveForm;
	private List<LeaveEntitlementDTO> leaveEntitlementList;
	private List<CustomRoundingDTO> customRoundingList;
	private List<CustomFieldsDTO> customFieldsList;
	private List<ComboValueDTO> sessionList;
	private Long employeeLeaveSchemeId;
	private Boolean expireEntitlement;
	private String excessLeaveAllowAs;

	private boolean excessLeaveAllowFullEntitlement;
	private boolean excessLeaveAllowMaximumDays;
	
	private BigDecimal minDaysGapBetweenLeave;
	private BigDecimal maxDaysAllowPerYear;

	private List<LeaveEntitlementDTO> leaveEntitlementDTOList = LazyList
			.decorate(new ArrayList<LeaveEntitlementDTO>(),
					FactoryUtils.instantiateFactory(LeaveEntitlementDTO.class));
	
	private boolean autoApproveAfter;
	private Integer autoApproveAfterDays;
	private String leaveVisibilityStartDate;
	private String leaveVisibilityEndDate;
	
	public List<ComboValueDTO> getSessionList() {
		return sessionList;
	}

	public void setSessionList(List<ComboValueDTO> sessionList) {
		this.sessionList = sessionList;
	}

	public Boolean isLeaveExtensionPreference() {
		return leaveExtensionPreference;
	}
	
	public Boolean getLeaveExtensionPreference() {
		return leaveExtensionPreference;
	}

	public void setLeaveExtensionPreference(Boolean leaveExtensionPreference) {
		this.leaveExtensionPreference = leaveExtensionPreference;
	}

	public Boolean getLeaveExtension() {
		return leaveExtension;
	}
	
	public Boolean isLeaveExtension() {
		return leaveExtension;
	}

	public void setLeaveExtension(Boolean leaveExtension) {
		this.leaveExtension = leaveExtension;
	}
	public Long[] getAllowOnlyIfBalIsZero() {
		return allowOnlyIfBalIsZero;
	}

	public Long getEmployeeLeaveSchemeId() {
		return employeeLeaveSchemeId;
	}

	public void setEmployeeLeaveSchemeId(Long employeeLeaveSchemeId) {
		this.employeeLeaveSchemeId = employeeLeaveSchemeId;
	}

	public void setAllowOnlyIfBalIsZero(Long[] allowOnlyIfBalIsZero) {
		 if(allowOnlyIfBalIsZero !=null){
			   this.allowOnlyIfBalIsZero = Arrays.copyOf(allowOnlyIfBalIsZero, allowOnlyIfBalIsZero.length); 
		} 
		
	}

	public Long[] getLeaveCanNotCombined() {
		return leaveCanNotCombined;
	}

	public void setLeaveCanNotCombined(Long[] leaveCanNotCombined) {
		 if(leaveCanNotCombined !=null){
			   this.leaveCanNotCombined = Arrays.copyOf(leaveCanNotCombined, leaveCanNotCombined.length); 
		} 
	}

	private List<CustomRoundingDTO> customRoundingDTOList = LazyList.decorate(
			new ArrayList<CustomRoundingDTO>(),
			FactoryUtils.instantiateFactory(CustomRoundingDTO.class));

	private List<CustomFieldsDTO> customFieldsDTOList = LazyList.decorate(
			new ArrayList<CustomFieldsDTO>(),
			FactoryUtils.instantiateFactory(CustomFieldsDTO.class));

	private List<FirstMonthCustomDTO> firstMonthCustomList = LazyList.decorate(
			new ArrayList<CustomRoundingDTO>(),
			FactoryUtils.instantiateFactory(CustomRoundingDTO.class));

	private Boolean allowExcessLeave;

	public List<FirstMonthCustomDTO> getFirstMonthCustomList() {
		return firstMonthCustomList;
	}

	public void setFirstMonthCustomList(
			List<FirstMonthCustomDTO> firstMonthCustomList) {
		this.firstMonthCustomList = firstMonthCustomList;
	}

	public Boolean getAllowExcessLeave() {
		return allowExcessLeave;
	}

	public void setAllowExcessLeave(Boolean allowExcessLeave) {
		this.allowExcessLeave = allowExcessLeave;
	}

	public AddLeaveForm getAddLeaveForm() {
		return addLeaveForm;
	}

	public void setAddLeaveForm(AddLeaveForm addLeaveForm) {
		this.addLeaveForm = addLeaveForm;
	}

	public String getLeaveSchemeName() {
		return leaveSchemeName;
	}

	public void setLeaveSchemeName(String leaveSchemeName) {
		this.leaveSchemeName = leaveSchemeName;
	}

	public String getEmployeeNumber() {
		return employeeNumber;
	}

	public void setEmployeeNumber(String employeeNumber) {
		this.employeeNumber = employeeNumber;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public BigDecimal getDays() {
		return days;
	}

	public void setDays(BigDecimal days) {
		this.days = days;
	}

	public BigDecimal getMaxLeaves() {
		return maxLeaves;
	}

	public void setMaxLeaves(BigDecimal maxLeaves) {
		this.maxLeaves = maxLeaves;
	}

	public BigDecimal getMinLeaves() {
		return minLeaves;
	}

	public void setMinLeaves(BigDecimal minLeaves) {
		this.minLeaves = minLeaves;
	}

	public Boolean getProrationFirstYearOnly() {
		return prorationFirstYearOnly;
	}

	public void setProrationFirstYearOnly(Boolean prorationFirstYearOnly) {
		this.prorationFirstYearOnly = prorationFirstYearOnly;
	}

	public Long getBasedOn() {
		return basedOn;
	}

	public void setBasedOn(Long basedOn) {
		this.basedOn = basedOn;
	}

	public Long getProbationMethod() {
		return probationMethod;
	}

	public void setProbationMethod(Long probationMethod) {
		this.probationMethod = probationMethod;
	}

	public Long getLeaveSchemeTypeGrantingId() {
		return leaveSchemeTypeGrantingId;
	}

	public void setLeaveSchemeTypeGrantingId(Long leaveSchemeTypeGrantingId) {
		this.leaveSchemeTypeGrantingId = leaveSchemeTypeGrantingId;
	}

	public Long getLeaveSchemeTypeProrationId() {
		return leaveSchemeTypeProrationId;
	}

	public void setLeaveSchemeTypeProrationId(Long leaveSchemeTypeProrationId) {
		this.leaveSchemeTypeProrationId = leaveSchemeTypeProrationId;
	}

	public Long getLeaveSchemeTypeAvailingId() {
		return leaveSchemeTypeAvailingId;
	}

	public void setLeaveSchemeTypeAvailingId(Long leaveSchemeTypeAvailingId) {
		this.leaveSchemeTypeAvailingId = leaveSchemeTypeAvailingId;
	}

	public Long getLeaveSchemeTypeYearEndId() {
		return leaveSchemeTypeYearEndId;
	}

	public void setLeaveSchemeTypeYearEndId(Long leaveSchemeTypeYearEndId) {
		this.leaveSchemeTypeYearEndId = leaveSchemeTypeYearEndId;
	}

	public Boolean getOffInclusive() {
		return offInclusive;
	}

	public void setOffInclusive(Boolean offInclusive) {
		this.offInclusive = offInclusive;
	}

	public BigDecimal getMinimumBlockLeave() {
		return minimumBlockLeave;
	}

	public void setMinimumBlockLeave(BigDecimal minimumBlockLeave) {
		this.minimumBlockLeave = minimumBlockLeave;
	}

	public BigDecimal getMaximumBlockLeave() {
		return maximumBlockLeave;
	}

	public void setMaximumBlockLeave(BigDecimal maximumBlockLeave) {
		this.maximumBlockLeave = maximumBlockLeave;
	}

	public Integer getMaximumFrequency() {
		return maximumFrequency;
	}

	public void setMaximumFrequency(Integer maximumFrequency) {
		this.maximumFrequency = maximumFrequency;
	}

	public Integer getAdvLeaveApplyBeforeDays() {
		return advLeaveApplyBeforeDays;
	}

	public void setAdvLeaveApplyBeforeDays(Integer advLeaveApplyBeforeDays) {
		this.advLeaveApplyBeforeDays = advLeaveApplyBeforeDays;
	}

	public Integer getAdvLeavePostBeforeDays() {
		return advLeavePostBeforeDays;
	}

	public void setAdvLeavePostBeforeDays(Integer advLeavePostBeforeDays) {
		this.advLeavePostBeforeDays = advLeavePostBeforeDays;
	}

	public Boolean getAllowBackDateLeavePosting() {
		return allowBackDateLeavePosting;
	}

	public void setAllowBackDateLeavePosting(Boolean allowBackDateLeavePosting) {
		this.allowBackDateLeavePosting = allowBackDateLeavePosting;
	}

	public Integer getBackDatePostingAfterDays() {
		return backDatePostingAfterDays;
	}

	public void setBackDatePostingAfterDays(Integer backDatePostingAfterDays) {
		this.backDatePostingAfterDays = backDatePostingAfterDays;
	}

	public Integer getNextYearPostingBeforeDays() {
		return nextYearPostingBeforeDays;
	}

	public void setNextYearPostingBeforeDays(Integer nextYearPostingBeforeDays) {
		this.nextYearPostingBeforeDays = nextYearPostingBeforeDays;
	}

	public Long getConsiderLeaveBalFrom() {
		return considerLeaveBalFrom;
	}

	public void setConsiderLeaveBalFrom(Long considerLeaveBalFrom) {
		this.considerLeaveBalFrom = considerLeaveBalFrom;
	}

	public Boolean getApprovalNotRequired() {
		return approvalNotRequired;
	}

	public void setApprovalNotRequired(Boolean approvalNotRequired) {
		this.approvalNotRequired = approvalNotRequired;
	}

	public String getDefaultCCEmail() {
		return defaultCCEmail;
	}

	public void setDefaultCCEmail(String defaultCCEmail) {
		this.defaultCCEmail = defaultCCEmail;
	}

	public Boolean getSendMailToDefaultCC() {
		return sendMailToDefaultCC;
	}

	public void setSendMailToDefaultCC(Boolean sendMailToDefaultCC) {
		this.sendMailToDefaultCC = sendMailToDefaultCC;
	}

	public Boolean getNoYearEndProcess() {
		return noYearEndProcess;
	}

	public void setNoYearEndProcess(Boolean noYearEndProcess) {
		this.noYearEndProcess = noYearEndProcess;
	}

	public Boolean getAllowCarryForward() {
		return allowCarryForward;
	}

	public void setAllowCarryForward(Boolean allowCarryForward) {
		this.allowCarryForward = allowCarryForward;
	}

	public BigDecimal getMaximumNumberCarryForwarded() {
		return maximumNumberCarryForwarded;
	}

	public void setMaximumNumberCarryForwarded(
			BigDecimal maximumNumberCarryForwarded) {
		this.maximumNumberCarryForwarded = maximumNumberCarryForwarded;
	}

	public BigDecimal getAnnualCarryFwdLimit() {
		return annualCarryFwdLimit;
	}

	public void setAnnualCarryFwdLimit(BigDecimal annualCarryFwdLimit) {
		this.annualCarryFwdLimit = annualCarryFwdLimit;
	}

	public BigDecimal getNumOfDaysAttachmentBeExempted() {
		return numOfDaysAttachmentBeExempted;
	}

	public void setNumOfDaysAttachmentBeExempted(
			BigDecimal numOfDaysAttachmentBeExempted) {
		this.numOfDaysAttachmentBeExempted = numOfDaysAttachmentBeExempted;
	}

	private String probationMethodName;
	private Integer probationMethodValue;
	private String messageToBeDisplayed;
	private Boolean attachementMandatory;
	private BigDecimal numOfDaysAttachmentBeExempted;
	private Integer cutOffDay;

	private LeaveSchemeAppCodeDTO leaveSchemeAppCodeDTO;
	private Boolean leavesPostedAdvanced;
	private String configureLeaveType;
	private String workflowLeaveType;

	private String employeesAssigned;

	private String maxAvailed;
	private String minAvailed;

	public String getMaxAvailed() {
		return maxAvailed;
	}

	public void setMaxAvailed(String maxAvailed) {
		this.maxAvailed = maxAvailed;
	}

	public String getMinAvailed() {
		return minAvailed;
	}

	public void setMinAvailed(String minAvailed) {
		this.minAvailed = minAvailed;
	}

	public String getEmployeesAssigned() {
		return employeesAssigned;
	}

	public void setEmployeesAssigned(String employeesAssigned) {
		this.employeesAssigned = employeesAssigned;
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

	public String getConfigureLeaveType() {
		return configureLeaveType;
	}

	public void setConfigureLeaveType(String configureLeaveType) {
		this.configureLeaveType = configureLeaveType;
	}

	public String getWorkflowLeaveType() {
		return workflowLeaveType;
	}

	public void setWorkflowLeaveType(String workflowLeaveType) {
		this.workflowLeaveType = workflowLeaveType;
	}

	public Boolean getAllowAdvanceLeavePosting() {
		return allowAdvanceLeavePosting;
	}

	public void setAllowAdvanceLeavePosting(Boolean allowAdvanceLeavePosting) {
		this.allowAdvanceLeavePosting = allowAdvanceLeavePosting;
	}

	public Boolean getLeavesPostedAdvanced() {
		return leavesPostedAdvanced;
	}

	public void setLeavesPostedAdvanced(Boolean leavesPostedAdvanced) {
		this.leavesPostedAdvanced = leavesPostedAdvanced;
	}

	public List<CustomRoundingDTO> getCustomRoundingList() {
		return customRoundingList;
	}

	public void setCustomRoundingList(List<CustomRoundingDTO> customRoundingList) {
		this.customRoundingList = customRoundingList;
	}

	public List<CustomFieldsDTO> getCustomFieldsList() {
		return customFieldsList;
	}

	public void setCustomFieldsList(List<CustomFieldsDTO> customFieldsList) {
		this.customFieldsList = customFieldsList;
	}

	public Boolean getHolidays() {
		return holidays;
	}

	public Boolean getAttachementMandatory() {
		return attachementMandatory;
	}

	public LeaveSchemeAppCodeDTO getLeaveSchemeAppCodeDTO() {
		return leaveSchemeAppCodeDTO;
	}

	public void setLeaveSchemeAppCodeDTO(
			LeaveSchemeAppCodeDTO leaveSchemeAppCodeDTO) {
		this.leaveSchemeAppCodeDTO = leaveSchemeAppCodeDTO;
	}

	public Long getLeaveCalendar() {
		return leaveCalendar;
	}

	public void setLeaveCalendar(Long leaveCalendar) {
		this.leaveCalendar = leaveCalendar;
	}

	public Long getDistributionMethod() {
		return distributionMethod;
	}

	public void setDistributionMethod(Long distributionMethod) {
		this.distributionMethod = distributionMethod;
	}

	public Long getRounding() {
		return rounding;
	}

	public void setRounding(Long rounding) {
		this.rounding = rounding;
	}

	public String getMessageToBeDisplayed() {
		return messageToBeDisplayed;
	}

	public void setMessageToBeDisplayed(String messageToBeDisplayed) {
		this.messageToBeDisplayed = messageToBeDisplayed;
	}

	public List<LeaveEntitlementDTO> getLeaveEntitlementDTOList() {
		return leaveEntitlementDTOList;
	}

	public void setLeaveEntitlementDTOList(
			List<LeaveEntitlementDTO> leaveEntitlementDTOList) {
		this.leaveEntitlementDTOList = leaveEntitlementDTOList;
	}

	public List<CustomRoundingDTO> getCustomRoundingDTOList() {
		return customRoundingDTOList;
	}

	public void setCustomRoundingDTOList(
			List<CustomRoundingDTO> customRoundingDTOList) {
		this.customRoundingDTOList = customRoundingDTOList;
	}

	public List<CustomFieldsDTO> getCustomFieldsDTOList() {
		return customFieldsDTOList;
	}

	public void setCustomFieldsDTOList(List<CustomFieldsDTO> customFieldsDTOList) {
		this.customFieldsDTOList = customFieldsDTOList;
	}

	public String getWorkFlowLevel() {
		return workFlowLevel;
	}

	public void setWorkFlowLevel(String workFlowLevel) {
		this.workFlowLevel = workFlowLevel;
	}

	public Boolean getAllowOverrideL1() {
		return allowOverrideL1;
	}

	public void setAllowOverrideL1(Boolean allowOverrideL1) {
		this.allowOverrideL1 = allowOverrideL1;
	}

	public Boolean getAllowOverrideL2() {
		return allowOverrideL2;
	}

	public void setAllowOverrideL2(Boolean allowOverrideL2) {
		this.allowOverrideL2 = allowOverrideL2;
	}

	public Boolean getAllowOverrideL3() {
		return allowOverrideL3;
	}

	public void setAllowOverrideL3(Boolean allowOverrideL3) {
		this.allowOverrideL3 = allowOverrideL3;
	}

	public Boolean getAllowRejectL1() {
		return allowRejectL1;
	}

	public void setAllowRejectL1(Boolean allowRejectL1) {
		this.allowRejectL1 = allowRejectL1;
	}

	public Boolean getAllowRejectL2() {
		return allowRejectL2;
	}

	public void setAllowRejectL2(Boolean allowRejectL2) {
		this.allowRejectL2 = allowRejectL2;
	}

	public Boolean getAllowRejectL3() {
		return allowRejectL3;
	}

	public void setAllowRejectL3(Boolean allowRejectL3) {
		this.allowRejectL3 = allowRejectL3;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
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

	public String getConfigure() {
		return configure;
	}

	public void setConfigure(String configure) {
		this.configure = configure;
	}

	public Long getLeaveSchemeId() {
		return leaveSchemeId;
	}

	public void setLeaveSchemeId(Long leaveSchemeId) {
		this.leaveSchemeId = leaveSchemeId;
	}

	public Boolean getVisibility() {
		return visibility;
	}

	public void setVisibility(Boolean visibility) {
		this.visibility = visibility;
	}

	public Boolean getLeaveTypeVisibility() {
		return leaveTypeVisibility;
	}

	public void setLeaveTypeVisibility(Boolean leaveTypeVisibility) {
		this.leaveTypeVisibility = leaveTypeVisibility;
	}

	public String getLeaveType() {
		return leaveType;
	}

	public void setLeaveType(String leaveType) {
		this.leaveType = leaveType;
	}

	public Long getLeaveTypeId() {
		return leaveTypeId;
	}

	public void setLeaveTypeId(Long leaveTypeId) {
		this.leaveTypeId = leaveTypeId;
	}

	public String getLeaveTypeDesc() {
		return leaveTypeDesc;
	}

	public void setLeaveTypeDesc(String leaveTypeDesc) {
		this.leaveTypeDesc = leaveTypeDesc;
	}

	public String getShortList() {
		return shortList;
	}

	public void setShortList(String shortList) {
		this.shortList = shortList;
	}

	public Long getLeaveSchemeTypeId() {
		return leaveSchemeTypeId;
	}

	public void setLeaveSchemeTypeId(Long leaveSchemeTypeId) {
		this.leaveSchemeTypeId = leaveSchemeTypeId;
	}

	public Integer getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Integer expiryDate) {
		this.expiryDate = expiryDate;
	}

	public Boolean isHolidays() {
		return holidays;
	}

	public void setHolidays(Boolean holidays) {
		this.holidays = holidays;
	}

	public Boolean isAttachementMandatory() {
		return attachementMandatory;
	}

	public void setAttachementMandatory(Boolean attachementMandatory) {
		this.attachementMandatory = attachementMandatory;
	}

	public List<LeaveEntitlementDTO> getLeaveEntitlementList() {
		return leaveEntitlementList;
	}

	public void setLeaveEntitlementList(
			List<LeaveEntitlementDTO> leaveEntitlementList) {
		this.leaveEntitlementList = leaveEntitlementList;
	}

	public Integer getProbationMethodValue() {
		return probationMethodValue;
	}

	public void setProbationMethodValue(Integer probationMethodValue) {
		this.probationMethodValue = probationMethodValue;
	}

	public BigDecimal getMaximumDays() {
		return maximumDays;
	}

	public void setMaximumDays(BigDecimal maximumDays) {
		this.maximumDays = maximumDays;
	}

	public Integer getCutOffDay() {
		return cutOffDay;
	}

	public void setCutOffDay(Integer cutOffDay) {
		this.cutOffDay = cutOffDay;
	}

	public String getProbationMethodName() {
		return probationMethodName;
	}

	public void setProbationMethodName(String probationMethodName) {
		this.probationMethodName = probationMethodName;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getLeaveGrantDay() {
		return leaveGrantDay;
	}

	public void setLeaveGrantDay(String leaveGrantDay) {
		this.leaveGrantDay = leaveGrantDay;
	}

	public String getLeaveCutOffDay() {
		return leaveCutOffDay;
	}

	public void setLeaveCutOffDay(String leaveCutOffDay) {
		this.leaveCutOffDay = leaveCutOffDay;
	}

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

	public String getDistMethName() {
		return distMethName;
	}

	public void setDistMethName(String distMethName) {
		this.distMethName = distMethName;
	}

	public Boolean getNoProration() {
		return noProration;
	}

	public void setNoProration(Boolean noProration) {
		this.noProration = noProration;
	}

	public Integer getLeaveCanBeAppOnlyAfterDays() {
		return leaveCanBeAppOnlyAfterDays;
	}

	public void setLeaveCanBeAppOnlyAfterDays(Integer leaveCanBeAppOnlyAfterDays) {
		this.leaveCanBeAppOnlyAfterDays = leaveCanBeAppOnlyAfterDays;
	}

	public Long getApplyAfterFromId() {
		return applyAfterFromId;
	}

	public void setApplyAfterFromId(Long applyAfterFromId) {
		this.applyAfterFromId = applyAfterFromId;
	}

	public Boolean getExpireEntitlement() {
		return expireEntitlement;
	}

	public void setExpireEntitlement(Boolean expireEntitlement) {
		this.expireEntitlement = expireEntitlement;
	}

	public String getExcessLeaveAllowAs() {
		return excessLeaveAllowAs;
	}

	public void setExcessLeaveAllowAs(String excessLeaveAllowAs) {
		this.excessLeaveAllowAs = excessLeaveAllowAs;
	}

	public boolean isExcessLeaveAllowFullEntitlement() {
		return excessLeaveAllowFullEntitlement;
	}

	public void setExcessLeaveAllowFullEntitlement(
			boolean excessLeaveAllowFullEntitlement) {
		this.excessLeaveAllowFullEntitlement = excessLeaveAllowFullEntitlement;
	}

	public boolean isExcessLeaveAllowMaximumDays() {
		return excessLeaveAllowMaximumDays;
	}

	public void setExcessLeaveAllowMaximumDays(
			boolean excessLeaveAllowMaximumDays) {
		this.excessLeaveAllowMaximumDays = excessLeaveAllowMaximumDays;
	}

	public BigDecimal getMinDaysGapBetweenLeave() {
		return minDaysGapBetweenLeave;
	}

	public void setMinDaysGapBetweenLeave(BigDecimal minDaysGapBetweenLeave) {
		this.minDaysGapBetweenLeave = minDaysGapBetweenLeave;
	}

	public BigDecimal getMaxDaysAllowPerYear() {
		return maxDaysAllowPerYear;
	}

	public void setMaxDaysAllowPerYear(BigDecimal maxDaysAllowPerYear) {
		this.maxDaysAllowPerYear = maxDaysAllowPerYear;
	}

	public boolean isAutoApproveAfter() {
		return autoApproveAfter;
	}

	public void setAutoApproveAfter(boolean autoApproveAfter) {
		this.autoApproveAfter = autoApproveAfter;
	}

	public Integer getAutoApproveAfterDays() {
		return autoApproveAfterDays;
	}

	public void setAutoApproveAfterDays(Integer autoApproveAfterDays) {
		this.autoApproveAfterDays = autoApproveAfterDays;
	}

	public String getLeaveVisibilityStartDate() {
		return leaveVisibilityStartDate;
	}

	public void setLeaveVisibilityStartDate(String leaveVisibilityStartDate) {
		this.leaveVisibilityStartDate = leaveVisibilityStartDate;
	}

	public String getLeaveVisibilityEndDate() {
		return leaveVisibilityEndDate;
	}

	public void setLeaveVisibilityEndDate(String leaveVisibilityEndDate) {
		this.leaveVisibilityEndDate = leaveVisibilityEndDate;
	}

}
