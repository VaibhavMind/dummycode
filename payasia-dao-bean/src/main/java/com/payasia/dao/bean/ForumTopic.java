package com.payasia.dao.bean;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * The persistent class for the Forum_Topic database table.
 * 
 */
@Entity
@Table(name = "Forum_Topic")
public class ForumTopic extends UpdatedBaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY )
	@Column(name = "Topic_ID")
	private long topicId;

	@ManyToOne
	@JoinColumn(name = "Company_ID")
	private Company company;

	@Column(name = "Topic_Name")
	private String topicName;

	@Column(name = "Topic_Desc")
	private String topicDesc;

	@Column(name = "Status")
	private boolean status;

	@Column(name = "Email_To")
	private String emailTo;

	@Column(name = "Email_CC")
	private String emailCC;

	@Column(name = "Topic_Employee_Id")
	private String topicEmployeeIds;

	@ManyToOne
	@JoinColumn(name = "Created_By")
	private Employee createdBy;

	@Column(name = "Created_Date")
	private Timestamp createdDate;

	@OneToMany(mappedBy = "forumTopic", cascade = { CascadeType.REMOVE })
	private Set<ForumTopicComment> forumTopicComments;

	@OneToMany(mappedBy = "forumTopic", cascade = { CascadeType.REMOVE })
	private Set<ForumTopicStatusChangeHistory> forumTopicStatusChangeHistorys;

	public ForumTopic() {
	}

	public long getTopicId() {
		return topicId;
	}

	public void setTopicId(long topicId) {
		this.topicId = topicId;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public String getTopicName() {
		return topicName;
	}

	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}

	public String getTopicDesc() {
		return topicDesc;
	}

	public void setTopicDesc(String topicDesc) {
		this.topicDesc = topicDesc;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public Set<ForumTopicComment> getForumTopicComments() {
		return forumTopicComments;
	}

	public void setForumTopicComments(Set<ForumTopicComment> forumTopicComments) {
		this.forumTopicComments = forumTopicComments;
	}

	public Employee getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Employee createdBy) {
		this.createdBy = createdBy;
	}

	public Timestamp getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Timestamp createdDate) {
		this.createdDate = createdDate;
	}

	public Set<ForumTopicStatusChangeHistory> getForumTopicStatusChangeHistorys() {
		return forumTopicStatusChangeHistorys;
	}

	public void setForumTopicStatusChangeHistorys(
			Set<ForumTopicStatusChangeHistory> forumTopicStatusChangeHistorys) {
		this.forumTopicStatusChangeHistorys = forumTopicStatusChangeHistorys;
	}

	public String getEmailTo() {
		return emailTo;
	}

	public void setEmailTo(String emailTo) {
		this.emailTo = emailTo;
	}

	public String getEmailCC() {
		return emailCC;
	}

	public void setEmailCC(String emailCC) {
		this.emailCC = emailCC;
	}

	public String getTopicEmployeeIds() {
		return topicEmployeeIds;
	}

	public void setTopicEmployeeIds(String topicEmployeeIds) {
		this.topicEmployeeIds = topicEmployeeIds;
	}

}