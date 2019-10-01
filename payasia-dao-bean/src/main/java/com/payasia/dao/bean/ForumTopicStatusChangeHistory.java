package com.payasia.dao.bean;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * The persistent class for the Forum Topic Status Change History database
 * table.
 * 
 */
@Entity
@Table(name = "Forum_Topic_Status_Change_History")
public class ForumTopicStatusChangeHistory extends CompanyUpdatedBaseEntity
		implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "Topic_Status_Change_History_ID")
	private long topicStatusChangeHistoryId;

	@ManyToOne
	@JoinColumn(name = "Topic_ID")
	private ForumTopic forumTopic;

	@Column(name = "Remarks")
	private String remarks;

	@Column(name = "Changed_Status")
	private boolean changedStatus;

	@ManyToOne
	@JoinColumn(name = "Created_By")
	private Employee createdBy;

	@Column(name = "Created_Date")
	private Timestamp createdDate;

	public ForumTopicStatusChangeHistory() {
	}

	public long getTopicStatusChangeHistoryId() {
		return topicStatusChangeHistoryId;
	}

	public void setTopicStatusChangeHistoryId(long topicStatusChangeHistoryId) {
		this.topicStatusChangeHistoryId = topicStatusChangeHistoryId;
	}

	public ForumTopic getForumTopic() {
		return forumTopic;
	}

	public void setForumTopic(ForumTopic forumTopic) {
		this.forumTopic = forumTopic;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
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

	public boolean isChangedStatus() {
		return changedStatus;
	}

	public void setChangedStatus(boolean changedStatus) {
		this.changedStatus = changedStatus;
	}

}