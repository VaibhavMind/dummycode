package com.payasia.common.form;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.payasia.common.dto.LeaveReviewFormDTO;

/**
 * @author abhisheksachdeva
 *
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PendingItemsForm implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6352896795089247093L;
	private Long leaveApplicationReviewerId;
	private Long workflowType;
	private String workflowTypeName;
	private String createdBy;
	private Long createdById;
	private String createdDate;
	private Timestamp createdDateM;
	private String status;
	private String fromDate;
	private String toDate;
	private String forwardTo;
	private Long forwardToId;
	private String emailCC;
	private String remarks;
	private boolean canOverride;
	private boolean canReject;
	private boolean canApprove;
	private boolean canForward;
	private AddLeaveForm addLeaveForm;
	private Long fromSessionId;
	private Long toSessionId;
	private String fromSession;
	private String toSession;
	private Long leaveApplicationId;
	private String typeOfApplication;
	private BigDecimal days;
	private String leaveAppReviewer1;
	private String leaveAppReviewer2;
	private String leaveAppReviewer3;
	private long leaveAppReviewer1Id;
	private long leaveAppReviewer2Id;
	private long leaveAppReviewer3Id;
	private Boolean isCancelApplication;
	private String reason;
	private String leaveTypeName;
	private String employee;
	private String systemEmail;
	private Boolean action;
	private Boolean multiAction;
	
/*	private final List<LeaveReviewFormDTO> leaveReviewFormDTOList = LazyList
			.decorate(new ArrayList<LeaveReviewFormDTO>(),
					FactoryUtils.instantiateFactory(LeaveReviewFormDTO.class));*/
	private  List<LeaveReviewFormDTO> leaveReviewFormDTOList;
	

	
	public long getLeaveAppReviewer1Id() {
		return leaveAppReviewer1Id;
	}

	public void setLeaveAppReviewer1Id(long leaveAppReviewer1Id) {
		this.leaveAppReviewer1Id = leaveAppReviewer1Id;
	}

	public long getLeaveAppReviewer2Id() {
		return leaveAppReviewer2Id;
	}

	public void setLeaveAppReviewer2Id(long leaveAppReviewer2Id) {
		this.leaveAppReviewer2Id = leaveAppReviewer2Id;
	}

	public long getLeaveAppReviewer3Id() {
		return leaveAppReviewer3Id;
	}

	public void setLeaveAppReviewer3Id(long leaveAppReviewer3Id) {
		this.leaveAppReviewer3Id = leaveAppReviewer3Id;
	}

	public void setLeaveReviewFormDTOList(List<LeaveReviewFormDTO> leaveReviewFormDTOList) {
		this.leaveReviewFormDTOList = leaveReviewFormDTOList;
	}

	public Timestamp getCreatedDateM() {
		return createdDateM;
	}

	public void setCreatedDateM(Timestamp createdDateM) {
		this.createdDateM = createdDateM;
	}

	public Boolean getIsCancelApplication() {
		return isCancelApplication;
	}

	public void setIsCancelApplication(Boolean isCancelApplication) {
		this.isCancelApplication = isCancelApplication;
	}

	public String getLeaveAppReviewer1() {
		return leaveAppReviewer1;
	}

	public void setLeaveAppReviewer1(String leaveAppReviewer1) {
		this.leaveAppReviewer1 = leaveAppReviewer1;
	}

	public String getLeaveAppReviewer2() {
		return leaveAppReviewer2;
	}

	public void setLeaveAppReviewer2(String leaveAppReviewer2) {
		this.leaveAppReviewer2 = leaveAppReviewer2;
	}

	public String getLeaveAppReviewer3() {
		return leaveAppReviewer3;
	}

	public void setLeaveAppReviewer3(String leaveAppReviewer3) {
		this.leaveAppReviewer3 = leaveAppReviewer3;
	}

	public String getTypeOfApplication() {
		return typeOfApplication;
	}

	public void setTypeOfApplication(String typeOfApplication) {
		this.typeOfApplication = typeOfApplication;
	}

	public Long getLeaveApplicationId() {
		return leaveApplicationId;
	}

	public void setLeaveApplicationId(Long leaveApplicationId) {
		this.leaveApplicationId = leaveApplicationId;
	}

	public boolean isCanApprove() {
		return canApprove;
	}

	public void setCanApprove(boolean canApprove) {
		this.canApprove = canApprove;
	}

	public boolean isCanForward() {
		return canForward;
	}

	public void setCanForward(boolean canForward) {
		this.canForward = canForward;
	}

	public Long getWorkflowType() {
		return workflowType;
	}

	public void setWorkflowType(Long workflowType) {
		this.workflowType = workflowType;
	}

	public Long getLeaveApplicationReviewerId() {
		return leaveApplicationReviewerId;
	}

	public void setLeaveApplicationReviewerId(Long leaveApplicationReviewerId) {
		this.leaveApplicationReviewerId = leaveApplicationReviewerId;
	}

	public String getWorkflowTypeName() {
		return workflowTypeName;
	}

	public void setWorkflowTypeName(String workflowTypeName) {
		this.workflowTypeName = workflowTypeName;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Long getCreatedById() {
		return createdById;
	}

	public void setCreatedById(Long createdById) {
		this.createdById = createdById;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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

	public AddLeaveForm getAddLeaveForm() {
		return addLeaveForm;
	}

	public void setAddLeaveForm(AddLeaveForm addLeaveForm) {
		this.addLeaveForm = addLeaveForm;
	}

	public String getForwardTo() {
		return forwardTo;
	}

	public void setForwardTo(String forwardTo) {
		this.forwardTo = forwardTo;
	}

	public boolean isCanOverride() {
		return canOverride;
	}

	public void setCanOverride(boolean canOverride) {
		this.canOverride = canOverride;
	}

	public boolean isCanReject() {
		return canReject;
	}

	public void setCanReject(boolean canReject) {
		this.canReject = canReject;
	}

	public Long getForwardToId() {
		return forwardToId;
	}

	public void setForwardToId(Long forwardToId) {
		this.forwardToId = forwardToId;
	}

	public String getEmailCC() {
		return emailCC;
	}

	public void setEmailCC(String emailCC) {
		this.emailCC = emailCC;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
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


	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getLeaveTypeName() {
		return leaveTypeName;
	}

	public void setLeaveTypeName(String leaveTypeName) {
		this.leaveTypeName = leaveTypeName;
	}

	public String getEmployee() {
		return employee;
	}

	public void setEmployee(String employee) {
		this.employee = employee;
	}

	public List<LeaveReviewFormDTO> getLeaveReviewFormDTOList() {
		return leaveReviewFormDTOList;
	}

	public BigDecimal getDays() {
		return days;
	}

	public void setDays(BigDecimal days) {
		this.days = days;
	}

	public String getSystemEmail() {
		return systemEmail;
	}

	public void setSystemEmail(String systemEmail) {
		this.systemEmail = systemEmail;
	}

	public Boolean getAction() {
		return action;
	}

	public void setAction(Boolean action) {
		this.action = action;
	}

	public Boolean getMultiAction() {
		return multiAction;
	}

	public void setMultiAction(Boolean multiAction) {
		this.multiAction = multiAction;
	}
    

}
