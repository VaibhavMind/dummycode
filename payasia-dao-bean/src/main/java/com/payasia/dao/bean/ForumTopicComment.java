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
 * The persistent class for the Forum_Topic_Comment database table.
 * 
 */
@Entity
@Table(name = "Forum_Topic_Comment")
public class ForumTopicComment extends CompanyUpdatedBaseEntity implements
		Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy =GenerationType.IDENTITY)
	@Column(name = "Topic_Comment_ID")
	private long topicCommentId;

	@ManyToOne
	@JoinColumn(name = "Topic_ID")
	private ForumTopic forumTopic;

	@ManyToOne
	@JoinColumn(name = "Created_By")
	private Employee createdBy;

	@Column(name = "Published_Date")
	private Timestamp publishedDate;

	@ManyToOne
	@JoinColumn(name = "Published_Status")
	private AppCodeMaster publishedStatus;

	@Column(name = "Created_Date")
	private Timestamp createdDate;

	@Column(name = "Comment")
	private String comment;

	@Column(name = "Send_Mail")
	private boolean sendMail;

	@Column(name = "Email_To")
	private String emailTo;

	@Column(name = "Email_CC")
	private String emailCC;

	@Column(name = "Topic_Comment_Employee_Id")
	private String topicCommentEmployeeIds;

	@OneToMany(mappedBy = "forumTopicComment", cascade = { CascadeType.REMOVE })
	private Set<ForumTopicCommentAttachment> forumTopicAttachments;

	public ForumTopicComment() {
	}

	public long getTopicCommentId() {
		return topicCommentId;
	}

	public void setTopicCommentId(long topicCommentId) {
		this.topicCommentId = topicCommentId;
	}

	public ForumTopic getForumTopic() {
		return forumTopic;
	}

	public void setForumTopic(ForumTopic forumTopic) {
		this.forumTopic = forumTopic;
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

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public boolean isSendMail() {
		return sendMail;
	}

	public void setSendMail(boolean sendMail) {
		this.sendMail = sendMail;
	}

	public Set<ForumTopicCommentAttachment> getForumTopicAttachments() {
		return forumTopicAttachments;
	}

	public void setForumTopicAttachments(
			Set<ForumTopicCommentAttachment> forumTopicAttachments) {
		this.forumTopicAttachments = forumTopicAttachments;
	}

	public Timestamp getPublishedDate() {
		return publishedDate;
	}

	public void setPublishedDate(Timestamp publishedDate) {
		this.publishedDate = publishedDate;
	}

	public AppCodeMaster getPublishedStatus() {
		return publishedStatus;
	}

	public void setPublishedStatus(AppCodeMaster publishedStatus) {
		this.publishedStatus = publishedStatus;
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

	public String getTopicCommentEmployeeIds() {
		return topicCommentEmployeeIds;
	}

	public void setTopicCommentEmployeeIds(String topicCommentEmployeeIds) {
		this.topicCommentEmployeeIds = topicCommentEmployeeIds;
	}

}