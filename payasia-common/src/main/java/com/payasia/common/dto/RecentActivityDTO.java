
package com.payasia.common.dto;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author vivekjain
 *
 */
@JsonInclude(Include.NON_NULL)
public class RecentActivityDTO implements Serializable,Comparable<RecentActivityDTO>{

	
	private static final long serialVersionUID = -2698806457373642001L;
	 
	private String workflowTypeName;
	private String createdBy;
	private Long createdById;
	private String createdDate;
	private String fromDate;
	private String toDate;
	private Boolean isCancelApplication;
	private Long leaveApplicationReviewerId;

	private Long leaveApplicationId;
	private String leaveType;
	private Date updatedDate;

	
	 
	private Long claimApplicationId;
	private String claimTemplateName;
	private Long claimApplicationReviewerId;
	
	private String activityType;
	private Long lundinTimesheetId;
	private Long lundinTimesheetReviewerId;
     private Long employeeId;
	private Long hrisChangeRequestId;
	private Long hrisChangeRequestReviewerId;
	private String oldValue;
	private String newValue;
	private String field;
	private byte[] employeeImage;

	
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
	public Long getLundinTimesheetId() {
		return lundinTimesheetId;
	}
	public void setLundinTimesheetId(Long lundinTimesheetId) {
		this.lundinTimesheetId = lundinTimesheetId;
	}
	public Long getLundinTimesheetReviewerId() {
		return lundinTimesheetReviewerId;
	}
	public void setLundinTimesheetReviewerId(Long lundinTimesheetReviewerId) {
		this.lundinTimesheetReviewerId = lundinTimesheetReviewerId;
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
	public Boolean getIsCancelApplication() {
		return isCancelApplication;
	}
	public void setIsCancelApplication(Boolean isCancelApplication) {
		this.isCancelApplication = isCancelApplication;
	}
	public Long getLeaveApplicationReviewerId() {
		return leaveApplicationReviewerId;
	}
	public void setLeaveApplicationReviewerId(Long leaveApplicationReviewerId) {
		this.leaveApplicationReviewerId = leaveApplicationReviewerId;
	}
	public Long getLeaveApplicationId() {
		return leaveApplicationId;
	}
	public void setLeaveApplicationId(Long leaveApplicationId) {
		this.leaveApplicationId = leaveApplicationId;
	}
	public String getLeaveType() {
		return leaveType;
	}
	public void setLeaveType(String leaveType) {
		this.leaveType = leaveType;
	}
	public Long getClaimApplicationId() {
		return claimApplicationId;
	}
	public void setClaimApplicationId(Long claimApplicationId) {
		this.claimApplicationId = claimApplicationId;
	}
	public String getClaimTemplateName() {
		return claimTemplateName;
	}
	public void setClaimTemplateName(String claimTemplateName) {
		this.claimTemplateName = claimTemplateName;
	}
	public Long getClaimApplicationReviewerId() {
		return claimApplicationReviewerId;
	}
	public void setClaimApplicationReviewerId(Long claimApplicationReviewerId) {
		this.claimApplicationReviewerId = claimApplicationReviewerId;
	}
	public String getActivityType() {
		return activityType;
	}
	public void setActivityType(String activityType) {
		this.activityType = activityType;
	}
	public Date getUpdatedDate() {
		return updatedDate;
	}
	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}
	@Override
	public int compareTo(RecentActivityDTO o) {
		return this.updatedDate.compareTo(o.updatedDate);
		}
	public Long getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}
	public Long getHrisChangeRequestId() {
		return hrisChangeRequestId;
	}
	public void setHrisChangeRequestId(Long hrisChangeRequestId) {
		this.hrisChangeRequestId = hrisChangeRequestId;
	}
	public String getOldValue() {
		return oldValue;
	}
	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}
	public String getNewValue() {
		return newValue;
	}
	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public Long getHrisChangeRequestReviewerId() {
		return hrisChangeRequestReviewerId;
	}
	public void setHrisChangeRequestReviewerId(
			Long hrisChangeRequestReviewerId) {
		this.hrisChangeRequestReviewerId = hrisChangeRequestReviewerId;
	}
	public byte[] getEmployeeImage() {
		return employeeImage;
	}
	public void setEmployeeImage(byte[] employeeImage) {
		this.employeeImage = employeeImage;
	}
	
	
}
