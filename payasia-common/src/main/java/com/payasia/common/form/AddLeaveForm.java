package com.payasia.common.form;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.payasia.common.dto.EmployeeLeaveApplicationReviewerDTO;
import com.payasia.common.dto.LeaveApplicationAttachmentDTO;
import com.payasia.common.dto.LeaveApplicationReviewerDTO;
import com.payasia.common.dto.LeaveApplicationWorkflowDTO;
import com.payasia.common.dto.LeaveCustomFieldDTO;
import com.payasia.common.dto.LeaveDTO;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddLeaveForm implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6247902630649907645L;
	private Long leaveApplicationId;
	private Long leaveSchemeId;
	private Long employeeLeaveSchemeId;
	private String leaveScheme;
	private Long leaveTypeId;
	private String leaveType;
	private String createDate;
	private Timestamp createDateM;
	private String fromDate;
	private String toDate;
	private Long applyToId;
	private String applyTo;
	private String applyToEmail;
    private  Long leaveReviewer1Id;  
	private String leaveReviewer1;
	private String leaveReviewer2;
	private Long leaveReviewer2Id;
	private String leaveReviewer3;
	private Long leaveReviewer3Id;
	private int totalNoOfReviewers;
	private String emailCC;
	private String contactDetails;
	private List<LeaveTypeForm> leaveTypeFormList;
	private String reason;
	private String status;
	private Long statusId;
	private List<LeaveApplicationAttachmentDTO> attachmentList;
	private List<LeaveApplicationWorkflowDTO> workflowList;
	private Long fromSessionId;
	private Long toSessionId;
	private String fromSession;
	private String toSession;
	private String fromSessionLabelKey;
	private String toSessionLabelKey;
	private String leaveMode;
	private String remarks;
	private LeaveDTO leaveDTO;
	public Long getLeaveReviewer1Id() {
		return leaveReviewer1Id;
	}

	public void setLeaveReviewer1Id(Long leaveReviewer1Id) {
		this.leaveReviewer1Id = leaveReviewer1Id;
	}

	private BigDecimal leaveBalance;
	private BigDecimal noOfDays;
	private Long leaveApplicationReviewerId;
	private Boolean approvalNotRequired;
	private Integer ruleValue;
	private Boolean isWithdrawn;
	private Boolean preApprovalReq;
	private Boolean preApprovalReq1;
	private Boolean leavePreferencePreApproval;

	private String leaveAppEmp;
	private String leaveAppByEmp;
	private String leaveAppRemarks;
	private String leaveAppStatus;
	private String leaveAppCreated;
	private String leaveUnit;

	private Boolean action;
	private String reviewer1Status;
	private String reviewer2Status;
	private String reviewer3Status;
	
	private String requestType;
	
	private List<EmployeeLeaveApplicationReviewerDTO> employeeLeaveApplicationReviewerDToList;
	
	private List<LeaveApplicationReviewerDTO> leaveApplicationReviewerDToList;
	
	private String preApprovalReqRemark;
	
	private Boolean isExtend;

	public String getRequestType() {
		return requestType;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	public Boolean getPreApprovalReq1() {
		return preApprovalReq1;
	}

	public void setPreApprovalReq1(Boolean preApprovalReq1) {
		this.preApprovalReq1 = preApprovalReq1;
	}

	public Boolean getLeavePreferencePreApproval() {
		return leavePreferencePreApproval;
	}

	public void setLeavePreferencePreApproval(Boolean leavePreferencePreApproval) {
		this.leavePreferencePreApproval = leavePreferencePreApproval;
	}

	public Boolean getPreApprovalReq() {
		return preApprovalReq;
	}

	public void setPreApprovalReq(Boolean preApprovalReq) {
		this.preApprovalReq = preApprovalReq;
	}

	public String getLeaveAppEmp() {
		return leaveAppEmp;
	}

	public void setLeaveAppEmp(String leaveAppEmp) {
		this.leaveAppEmp = leaveAppEmp;
	}

	public String getLeaveAppRemarks() {
		return leaveAppRemarks;
	}

	public void setLeaveAppRemarks(String leaveAppRemarks) {
		this.leaveAppRemarks = leaveAppRemarks;
	}

	public String getLeaveAppStatus() {
		return leaveAppStatus;
	}

	public void setLeaveAppStatus(String leaveAppStatus) {
		this.leaveAppStatus = leaveAppStatus;
	}

	public String getLeaveAppCreated() {
		return leaveAppCreated;
	}

	public void setLeaveAppCreated(String leaveAppCreated) {
		this.leaveAppCreated = leaveAppCreated;
	}

	public Boolean getIsWithdrawn() {
		return isWithdrawn;
	}

	public void setIsWithdrawn(Boolean isWithdrawn) {
		this.isWithdrawn = isWithdrawn;
	}

	public Integer getRuleValue() {
		return ruleValue;
	}

	public void setRuleValue(Integer ruleValue) {
		this.ruleValue = ruleValue;
	}

	private List<LeaveApplicationReviewerDTO> leaveApplicationReviewers;
	private LeaveApplicationReviewerDTO leaveApplicationReviewerDTO;

	public LeaveApplicationReviewerDTO getLeaveApplicationReviewerDTO() {
		return leaveApplicationReviewerDTO;
	}

	public void setLeaveApplicationReviewerDTO(LeaveApplicationReviewerDTO leaveApplicationReviewerDTO) {
		this.leaveApplicationReviewerDTO = leaveApplicationReviewerDTO;
	}

	public List<LeaveApplicationReviewerDTO> getLeaveApplicationReviewers() {
		return leaveApplicationReviewers;
	}

	public void setLeaveApplicationReviewers(List<LeaveApplicationReviewerDTO> leaveApplicationReviewers) {
		this.leaveApplicationReviewers = leaveApplicationReviewers;
	}

	public Boolean getApprovalNotRequired() {
		return approvalNotRequired;
	}

	public void setApprovalNotRequired(Boolean approvalNotRequired) {
		this.approvalNotRequired = approvalNotRequired;
	}

	public BigDecimal getNoOfDays() {
		return noOfDays;
	}

	public void setNoOfDays(BigDecimal noOfDays) {
		this.noOfDays = noOfDays;
	}

	public String getApplyToEmail() {
		return applyToEmail;
	}

	public void setApplyToEmail(String applyToEmail) {
		this.applyToEmail = applyToEmail;
	}

	public Long getLeaveApplicationReviewerId() {
		return leaveApplicationReviewerId;
	}

	public void setLeaveApplicationReviewerId(Long leaveApplicationReviewerId) {
		this.leaveApplicationReviewerId = leaveApplicationReviewerId;
	}

	public BigDecimal getLeaveBalance() {
		return leaveBalance;
	}

	public void setLeaveBalance(BigDecimal leaveBalance) {
		this.leaveBalance = leaveBalance;
	}

	public LeaveDTO getLeaveDTO() {
		return leaveDTO;
	}

	public void setLeaveDTO(LeaveDTO leaveDTO) {
		this.leaveDTO = leaveDTO;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getLeaveMode() {
		return leaveMode;
	}

	public void setLeaveMode(String leaveMode) {
		this.leaveMode = leaveMode;
	}

	private List<LeaveCustomFieldDTO> leaveCustomFieldDTO;

	private List<LeaveCustomFieldDTO> customFieldDTOList = LazyList.decorate(new ArrayList<LeaveCustomFieldDTO>(),
			FactoryUtils.instantiateFactory(LeaveCustomFieldDTO.class));

	public List<LeaveCustomFieldDTO> getCustomFieldDTOList() {
		return customFieldDTOList;
	}

	public void setCustomFieldDTOList(List<LeaveCustomFieldDTO> customFieldDTOList) {
		this.customFieldDTOList = customFieldDTOList;
	}

	public Long getEmployeeLeaveSchemeId() {
		return employeeLeaveSchemeId;
	}

	public void setEmployeeLeaveSchemeId(Long employeeLeaveSchemeId) {
		this.employeeLeaveSchemeId = employeeLeaveSchemeId;
	}

	public List<LeaveCustomFieldDTO> getLeaveCustomFieldDTO() {
		return leaveCustomFieldDTO;
	}

	public void setLeaveCustomFieldDTO(List<LeaveCustomFieldDTO> leaveCustomFieldDTO) {
		this.leaveCustomFieldDTO = leaveCustomFieldDTO;
	}

	public Long getLeaveSchemeId() {
		return leaveSchemeId;
	}

	public void setLeaveSchemeId(Long leaveSchemeId) {
		this.leaveSchemeId = leaveSchemeId;
	}

	public Long getLeaveTypeId() {
		return leaveTypeId;
	}

	public void setLeaveTypeId(Long leaveTypeId) {
		this.leaveTypeId = leaveTypeId;
	}

	public String getFromDate() {
		return fromDate;
	}

	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}

	public String getToDate() {
		return toDate;
	}

	public void setToDate(String toDate) {
		this.toDate = toDate;
	}

	public Long getApplyToId() {
		return applyToId;
	}

	public void setApplyToId(Long applyToId) {
		this.applyToId = applyToId;
	}

	public String getApplyTo() {
		return applyTo;
	}

	public void setApplyTo(String applyTo) {
		this.applyTo = applyTo;
	}

	public int getTotalNoOfReviewers() {
		return totalNoOfReviewers;
	}

	public void setTotalNoOfReviewers(int totalNoOfReviewers) {
		this.totalNoOfReviewers = totalNoOfReviewers;
	}

	public List<LeaveTypeForm> getLeaveTypeFormList() {
		return leaveTypeFormList;
	}

	public void setLeaveTypeFormList(List<LeaveTypeForm> leaveTypeFormList) {
		this.leaveTypeFormList = leaveTypeFormList;
	}

	public String getEmailCC() {
		return emailCC;
	}

	public void setEmailCC(String emailCC) {
		this.emailCC = emailCC;
	}

	public String getContactDetails() {
		return contactDetails;
	}

	public void setContactDetails(String contactDetails) {
		this.contactDetails = contactDetails;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public List<LeaveApplicationAttachmentDTO> getAttachmentList() {
		return attachmentList;
	}

	public void setAttachmentList(List<LeaveApplicationAttachmentDTO> attachmentList) {
		this.attachmentList = attachmentList;
	}

	public String getLeaveReviewer2() {
		return leaveReviewer2;
	}

	public void setLeaveReviewer2(String leaveReviewer2) {
		this.leaveReviewer2 = leaveReviewer2;
	}

	public Long getLeaveReviewer2Id() {
		return leaveReviewer2Id;
	}

	public void setLeaveReviewer2Id(Long leaveReviewer2Id) {
		this.leaveReviewer2Id = leaveReviewer2Id;
	}

	public String getLeaveReviewer3() {
		return leaveReviewer3;
	}

	public void setLeaveReviewer3(String leaveReviewer3) {
		this.leaveReviewer3 = leaveReviewer3;
	}

	public Long getLeaveReviewer3Id() {
		return leaveReviewer3Id;
	}

	public void setLeaveReviewer3Id(Long leaveReviewer3Id) {
		this.leaveReviewer3Id = leaveReviewer3Id;
	}

	public String getLeaveReviewer1() {
		return leaveReviewer1;
	}

	public void setLeaveReviewer1(String leaveReviewer1) {
		this.leaveReviewer1 = leaveReviewer1;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getStatusId() {
		return statusId;
	}

	public void setStatusId(Long statusId) {
		this.statusId = statusId;
	}

	public String getLeaveScheme() {
		return leaveScheme;
	}

	public void setLeaveScheme(String leaveScheme) {
		this.leaveScheme = leaveScheme;
	}

	public String getLeaveType() {
		return leaveType;
	}

	public void setLeaveType(String leaveType) {
		this.leaveType = leaveType;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public Long getLeaveApplicationId() {
		return leaveApplicationId;
	}

	public void setLeaveApplicationId(Long leaveApplicationId) {
		this.leaveApplicationId = leaveApplicationId;
	}

	public List<LeaveApplicationWorkflowDTO> getWorkflowList() {
		return workflowList;
	}

	public void setWorkflowList(List<LeaveApplicationWorkflowDTO> workflowList) {
		this.workflowList = workflowList;
	}

	public Long getFromSessionId() {
		return fromSessionId;
	}

	public void setFromSessionId(Long fromSessionId) {
		this.fromSessionId = fromSessionId;
	}

	public Long getToSessionId() {
		return toSessionId;
	}

	public void setToSessionId(Long toSessionId) {
		this.toSessionId = toSessionId;
	}

	public String getFromSession() {
		return fromSession;
	}

	public void setFromSession(String fromSession) {
		this.fromSession = fromSession;
	}

	public String getToSession() {
		return toSession;
	}

	public void setToSession(String toSession) {
		this.toSession = toSession;
	}

	public String getFromSessionLabelKey() {
		return fromSessionLabelKey;
	}

	public void setFromSessionLabelKey(String fromSessionLabelKey) {
		this.fromSessionLabelKey = fromSessionLabelKey;
	}

	public String getToSessionLabelKey() {
		return toSessionLabelKey;
	}

	public void setToSessionLabelKey(String toSessionLabelKey) {
		this.toSessionLabelKey = toSessionLabelKey;
	}

	public Timestamp getCreateDateM() {
		return createDateM;
	}

	public void setCreateDateM(Timestamp createDateM) {
		this.createDateM = createDateM;
	}

	public String getLeaveUnit() {
		return leaveUnit;
	}

	public void setLeaveUnit(String leaveUnit) {
		this.leaveUnit = leaveUnit;
	}

	public String getLeaveAppByEmp() {
		return leaveAppByEmp;
	}

	public void setLeaveAppByEmp(String leaveAppByEmp) {
		this.leaveAppByEmp = leaveAppByEmp;
	}

	public Boolean getAction() {
		return action;
	}

	public void setAction(Boolean action) {
		this.action = action;
	}

	public String getReviewer1Status() {
		return reviewer1Status;
	}

	public void setReviewer1Status(String reviewer1Status) {
		this.reviewer1Status = reviewer1Status;
	}

	public String getReviewer2Status() {
		return reviewer2Status;
	}

	public void setReviewer2Status(String reviewer2Status) {
		this.reviewer2Status = reviewer2Status;
	}

	public String getReviewer3Status() {
		return reviewer3Status;
	}

	public void setReviewer3Status(String reviewer3Status) {
		this.reviewer3Status = reviewer3Status;
	}

	public List<EmployeeLeaveApplicationReviewerDTO> getEmployeeLeaveApplicationReviewerDToList() {
		return employeeLeaveApplicationReviewerDToList;
	}

	public void setEmployeeLeaveApplicationReviewerDToList(
			List<EmployeeLeaveApplicationReviewerDTO> employeeLeaveApplicationReviewerDToList) {
		this.employeeLeaveApplicationReviewerDToList = employeeLeaveApplicationReviewerDToList;
	}

	public List<LeaveApplicationReviewerDTO> getLeaveApplicationReviewerDToList() {
		return leaveApplicationReviewerDToList;
	}

	public void setLeaveApplicationReviewerDToList(List<LeaveApplicationReviewerDTO> leaveApplicationReviewerDToList) {
		this.leaveApplicationReviewerDToList = leaveApplicationReviewerDToList;
	}

	public String getPreApprovalReqRemark() {
		return preApprovalReqRemark;
	}

	public void setPreApprovalReqRemark(String preApprovalReqRemark) {
		this.preApprovalReqRemark = preApprovalReqRemark;
	}

	public Boolean getIsExtend() {
		return isExtend;
	}

	public void setIsExtend(Boolean isExtend) {
		this.isExtend = isExtend;
	}

	

}
