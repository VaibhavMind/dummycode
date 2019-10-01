package com.payasia.common.dto;


public class DiscussionBoardConditionDTO {
	
	private Long employeeId;
	private Long companyId;
	private Long topicId;
	private String topicName;	
	private String topicAuthor;
	private String topicStatus;
	private String createdOn;
	private String createdFrom;
	private String createdTo;
	private int year;
	
	public Long getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}
	public Long getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
	public Long getTopicId() {
		return topicId;
	}
	public void setTopicId(Long topicId) {
		this.topicId = topicId;
	}
	public String getTopicName() {
		return topicName;
	}
	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}
	public String getTopicAuthor() {
		return topicAuthor;
	}
	public void setTopicAuthor(String topicAuthor) {
		this.topicAuthor = topicAuthor;
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
	public String getCreatedFrom() {
		return createdFrom;
	}
	public void setCreatedFrom(String createdFrom) {
		this.createdFrom = createdFrom;
	}
	public String getCreatedTo() {
		return createdTo;
	}
	public void setCreatedTo(String createdTo) {
		this.createdTo = createdTo;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}


}
