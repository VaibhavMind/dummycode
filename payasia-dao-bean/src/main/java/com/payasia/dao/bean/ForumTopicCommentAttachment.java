package com.payasia.dao.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * The persistent class for the Forum_Topic_Comment_Attachment database table.
 * 
 */
@Entity
@Table(name = "Forum_Topic_Comment_Attachment")
public class ForumTopicCommentAttachment extends CompanyBaseEntity implements
		Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy =GenerationType.IDENTITY)
	@Column(name = "Topic_Attachment_ID")
	private long topicAttachmentId;

	@ManyToOne
	@JoinColumn(name = "Topic_Comment_ID")
	private ForumTopicComment forumTopicComment;

	@Column(name = "File_Name")
	private String fileName;

	@Column(name = "File_Type")
	private String fileType;

	public ForumTopicCommentAttachment() {
	}

	public long getTopicAttachmentId() {
		return topicAttachmentId;
	}

	public void setTopicAttachmentId(long topicAttachmentId) {
		this.topicAttachmentId = topicAttachmentId;
	}

	public ForumTopicComment getForumTopicComment() {
		return forumTopicComment;
	}

	public void setForumTopicComment(ForumTopicComment forumTopicComment) {
		this.forumTopicComment = forumTopicComment;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

}