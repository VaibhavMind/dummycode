package com.payasia.common.form;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;

import com.payasia.common.dto.LeaveReviewFormDTO;

public class CoherentShiftDetailForm implements Serializable {
	private static final long serialVersionUID = -6352896795089247093L;
	private Long coherentShiftApplicationReviewerId;
	private Long workflowType;
	private String workflowTypeName;
	private String createdBy;
	private Long createdById;
	private String createdDate;
	private Timestamp createdDateM;
	private String status;
	private String forwardTo;
	private Long forwardToId;
	private String emailCC;
	private String remarks;
	private boolean canOverride;
	private boolean canReject;
	private boolean canApprove;
	private boolean canForward;
	private AddLeaveForm addLeaveForm;
	private Long coherentShiftApplicationId;
	
	private Integer totalShift;
	private String shiftType;
	public Integer getTotalShift() {
		return totalShift;
	}

	public void setTotalShift(Integer totalShift) {
		this.totalShift = totalShift;
	}

	public String getShiftType() {
		return shiftType;
	}

	public void setShiftType(String shiftType) {
		this.shiftType = shiftType;
	}

	public Long getCoherentShiftApplicationReviewerId() {
		return coherentShiftApplicationReviewerId;
	}

	public void setCoherentShiftApplicationReviewerId(
			Long coherentShiftApplicationReviewerId) {
		this.coherentShiftApplicationReviewerId = coherentShiftApplicationReviewerId;
	}

	public Long getWorkflowType() {
		return workflowType;
	}

	public void setWorkflowType(Long workflowType) {
		this.workflowType = workflowType;
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

	public Timestamp getCreatedDateM() {
		return createdDateM;
	}

	public void setCreatedDateM(Timestamp createdDateM) {
		this.createdDateM = createdDateM;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getForwardTo() {
		return forwardTo;
	}

	public void setForwardTo(String forwardTo) {
		this.forwardTo = forwardTo;
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

	public AddLeaveForm getAddLeaveForm() {
		return addLeaveForm;
	}

	public void setAddLeaveForm(AddLeaveForm addLeaveForm) {
		this.addLeaveForm = addLeaveForm;
	}

	public Long getCoherentShiftApplicationId() {
		return coherentShiftApplicationId;
	}

	public void setCoherentShiftApplicationId(Long coherentShiftApplicationId) {
		this.coherentShiftApplicationId = coherentShiftApplicationId;
	}

	public String getReviewer1() {
		return reviewer1;
	}

	public void setReviewer1(String reviewer1) {
		this.reviewer1 = reviewer1;
	}

	public String getReviewer2() {
		return reviewer2;
	}

	public void setReviewer2(String reviewer2) {
		this.reviewer2 = reviewer2;
	}

	public String getReviewer3() {
		return reviewer3;
	}

	public void setReviewer3(String reviewer3) {
		this.reviewer3 = reviewer3;
	}

	public String getEmployee() {
		return employee;
	}

	public void setEmployee(String employee) {
		this.employee = employee;
	}

	public String getSystemEmail() {
		return systemEmail;
	}

	public void setSystemEmail(String systemEmail) {
		this.systemEmail = systemEmail;
	}

	public String getClaimMonth() {
		return claimMonth;
	}

	public void setClaimMonth(String claimMonth) {
		this.claimMonth = claimMonth;
	}

	public String getCostCentre() {
		return costCentre;
	}

	public void setCostCentre(String costCentre) {
		this.costCentre = costCentre;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getTotalOTHours() {
		return totalOTHours;
	}

	public void setTotalOTHours(String totalOTHours) {
		this.totalOTHours = totalOTHours;
	}

	public String getTotalOT15Hours() {
		return totalOT15Hours;
	}

	public void setTotalOT15Hours(String totalOT15Hours) {
		this.totalOT15Hours = totalOT15Hours;
	}

	public String getTotalOT10Day() {
		return totalOT10Day;
	}

	public void setTotalOT10Day(String totalOT10Day) {
		this.totalOT10Day = totalOT10Day;
	}

	public String getTotalOT20Day() {
		return totalOT20Day;
	}

	public void setTotalOT20Day(String totalOT20Day) {
		this.totalOT20Day = totalOT20Day;
	}

	public Long getOtTimesheetReviewerId() {
		return otTimesheetReviewerId;
	}

	public void setOtTimesheetReviewerId(Long otTimesheetReviewerId) {
		this.otTimesheetReviewerId = otTimesheetReviewerId;
	}

	public Long getOtTimesheetId() {
		return otTimesheetId;
	}

	public void setOtTimesheetId(Long otTimesheetId) {
		this.otTimesheetId = otTimesheetId;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public List<LeaveReviewFormDTO> getCoherentReviewFormDTOList() {
		return coherentReviewFormDTOList;
	}

	private String reviewer1;
	private String reviewer2;
	private String reviewer3;
	private String employee;
	private String systemEmail;
	private String claimMonth;
	private String costCentre;
	private String department;
	private String totalOTHours;
	private String totalOT15Hours;
	private String totalOT10Day;
	private String totalOT20Day;
	private Long otTimesheetReviewerId;
	private Long otTimesheetId;
	
	private final List<LeaveReviewFormDTO> coherentReviewFormDTOList = LazyList
			.decorate(new ArrayList<LeaveReviewFormDTO>(),
					FactoryUtils.instantiateFactory(LeaveReviewFormDTO.class));
}
