package com.payasia.common.form;

import java.io.Serializable;

public class DiscussionBoardForm implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4131221104992120612L;
	private Integer companyID;
	private long topicId;
	private String topicName;
	private Long commentsCount;	
	private Long topicAuthorId;
	private String topicAuthorName;
	private String lastComment;
	private String topicStatus;
	private String createdOn;
	private String topicDescription;
	private String emailIds;
	private String emailCcIds;
	private String topicStatusRemarks;
	private String statusChangedBy;
	private String generatedBy;
	private String employeeIds;
	public Integer getCompanyID() {
		return companyID;
	}
	public void setCompanyID(Integer companyID) {
		this.companyID = companyID;
	}
	public long getTopicId() {
		return topicId;
	}
	public void setTopicId(long topicId) {
		this.topicId = topicId;
	}
	public String getTopicName() {
		return topicName;
	}
	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}
	public Long getCommentsCount() {
		return commentsCount;
	}
	public void setCommentsCount(Long commentsCount) {
		this.commentsCount = commentsCount;
	}
	public Long getTopicAuthorId() {
		return topicAuthorId;
	}
	public void setTopicAuthorId(Long topicAuthorId) {
		this.topicAuthorId = topicAuthorId;
	}
	public String getTopicAuthorName() {
		return topicAuthorName;
	}
	public void setTopicAuthorName(String topicAuthorName) {
		this.topicAuthorName = topicAuthorName;
	}
	public String getLastComment() {
		return lastComment;
	}
	public void setLastComment(String lastComment) {
		this.lastComment = lastComment;
	}
	public String getTopicStatus() {
		return topicStatus;
	}
	public void setTopicStatus(String topicStatus) {
		this.topicStatus = topicStatus;
	}
	public String getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}
	public String getTopicDescription() {
		return topicDescription;
	}
	public void setTopicDescription(String topicDescription) {
		this.topicDescription = topicDescription;
	}
	public String getEmailIds() {
		return emailIds;
	}
	public void setEmailIds(String emailIds) {
		this.emailIds = emailIds;
	}
	public String getTopicStatusRemarks() {
		return topicStatusRemarks;
	}
	public void setTopicStatusRemarks(String topicStatusRemarks) {
		this.topicStatusRemarks = topicStatusRemarks;
	}
	public String getStatusChangedBy() {
		return statusChangedBy;
	}
	public void setStatusChangedBy(String statusChangedBy) {
		this.statusChangedBy = statusChangedBy;
	}
	public String getGeneratedBy() {
		return generatedBy;
	}
	public void setGeneratedBy(String generatedBy) {
		this.generatedBy = generatedBy;
	}
	public String getEmailCcIds() {
		return emailCcIds;
	}
	public void setEmailCcIds(String emailCcIds) {
		this.emailCcIds = emailCcIds;
	}
	public String getEmployeeIds() {
		return employeeIds;
	}
	public void setEmployeeIds(String employeeIds) {
		this.employeeIds = employeeIds;
	}	


}
