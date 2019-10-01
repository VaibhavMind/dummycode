package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

import com.payasia.common.dto.TopicAttachmentDTO;

public class DiscussionTopicCommentForm implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3917783132582200283L;
	private Long topicId;
	private Long topicCommentId;
	private String comment;
	private boolean sendMail;
	private String email;
	private String emailCc;
	private Long companyId;
	private boolean isCommentEditable;
	private Long createdById;
	private String createdByName;
	private String createdByEmpNumber;
	private String createdDate;
	private String attachmentName;
	private Long attachmentId;
	private String commentStatus;
	private String encodedComment;
	private String topicEmployeeIds;

	private List<TopicAttachmentDTO> attachmentList;
	
	private int attachmentListSize;
	
	public Long getTopicId() {
		return topicId;
	}
	public void setTopicId(Long topicId) {
		this.topicId = topicId;
	}
	public Long getTopicCommentId() {
		return topicCommentId;
	}
	public void setTopicCommentId(Long topicCommentId) {
		this.topicCommentId = topicCommentId;
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
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Long getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
	public List<TopicAttachmentDTO> getAttachmentList() {
		return attachmentList;
	}
	public void setAttachmentList(List<TopicAttachmentDTO> attachmentList) {
		this.attachmentList = attachmentList;
	}
	public boolean isCommentEditable() {
		return isCommentEditable;
	}
	public void setCommentEditable(boolean isCommentEditable) {
		this.isCommentEditable = isCommentEditable;
	}
	public Long getCreatedById() {
		return createdById;
	}
	public void setCreatedById(Long createdById) {
		this.createdById = createdById;
	}
	public String getCreatedByName() {
		return createdByName;
	}
	public void setCreatedByName(String createdByName) {
		this.createdByName = createdByName;
	}
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	public String getAttachmentName() {
		return attachmentName;
	}
	public void setAttachmentName(String attachmentName) {
		this.attachmentName = attachmentName;
	}
	public Long getAttachmentId() {
		return attachmentId;
	}
	public void setAttachmentId(Long attachmentId) {
		this.attachmentId = attachmentId;
	}
	public String getCreatedByEmpNumber() {
		return createdByEmpNumber;
	}
	public void setCreatedByEmpNumber(String createdByEmpNumber) {
		this.createdByEmpNumber = createdByEmpNumber;
	}
	public String getCommentStatus() {
		return commentStatus;
	}
	public void setCommentStatus(String commentStatus) {
		this.commentStatus = commentStatus;
	}
	public String getEncodedComment() {
		return encodedComment;
	}
	public void setEncodedComment(String encodedComment) {
		this.encodedComment = encodedComment;
	}
	public String getEmailCc() {
		return emailCc;
	}
	public void setEmailCc(String emailCc) {
		this.emailCc = emailCc;
	}
	public String getTopicEmployeeIds() {
		return topicEmployeeIds;
	}
	public void setTopicEmployeeIds(String topicEmployeeIds) {
		this.topicEmployeeIds = topicEmployeeIds;
	}
	public int getAttachmentListSize() {
		return attachmentListSize;
	}
	public void setAttachmentListSize(int attachmentListSize) {
		this.attachmentListSize = attachmentListSize;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((attachmentId == null) ? 0 : attachmentId.hashCode());
		result = prime * result + ((attachmentList == null) ? 0 : attachmentList.hashCode());
		result = prime * result + attachmentListSize;
		result = prime * result + ((attachmentName == null) ? 0 : attachmentName.hashCode());
		result = prime * result + ((comment == null) ? 0 : comment.hashCode());
		result = prime * result + ((commentStatus == null) ? 0 : commentStatus.hashCode());
		result = prime * result + ((companyId == null) ? 0 : companyId.hashCode());
		result = prime * result + ((createdByEmpNumber == null) ? 0 : createdByEmpNumber.hashCode());
		result = prime * result + ((createdById == null) ? 0 : createdById.hashCode());
		result = prime * result + ((createdByName == null) ? 0 : createdByName.hashCode());
		result = prime * result + ((createdDate == null) ? 0 : createdDate.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((emailCc == null) ? 0 : emailCc.hashCode());
		result = prime * result + ((encodedComment == null) ? 0 : encodedComment.hashCode());
		result = prime * result + (isCommentEditable ? 1231 : 1237);
		result = prime * result + (sendMail ? 1231 : 1237);
		result = prime * result + ((topicCommentId == null) ? 0 : topicCommentId.hashCode());
		result = prime * result + ((topicEmployeeIds == null) ? 0 : topicEmployeeIds.hashCode());
		result = prime * result + ((topicId == null) ? 0 : topicId.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DiscussionTopicCommentForm other = (DiscussionTopicCommentForm) obj;
		if (attachmentId == null) {
			if (other.attachmentId != null)
				return false;
		} else if (!attachmentId.equals(other.attachmentId))
			return false;
		if (attachmentList == null) {
			if (other.attachmentList != null)
				return false;
		} else if (!attachmentList.equals(other.attachmentList))
			return false;
		if (attachmentListSize != other.attachmentListSize)
			return false;
		if (attachmentName == null) {
			if (other.attachmentName != null)
				return false;
		} else if (!attachmentName.equals(other.attachmentName))
			return false;
		if (comment == null) {
			if (other.comment != null)
				return false;
		} else if (!comment.equals(other.comment))
			return false;
		if (commentStatus == null) {
			if (other.commentStatus != null)
				return false;
		} else if (!commentStatus.equals(other.commentStatus))
			return false;
		if (companyId == null) {
			if (other.companyId != null)
				return false;
		} else if (!companyId.equals(other.companyId))
			return false;
		if (createdByEmpNumber == null) {
			if (other.createdByEmpNumber != null)
				return false;
		} else if (!createdByEmpNumber.equals(other.createdByEmpNumber))
			return false;
		if (createdById == null) {
			if (other.createdById != null)
				return false;
		} else if (!createdById.equals(other.createdById))
			return false;
		if (createdByName == null) {
			if (other.createdByName != null)
				return false;
		} else if (!createdByName.equals(other.createdByName))
			return false;
		if (createdDate == null) {
			if (other.createdDate != null)
				return false;
		} else if (!createdDate.equals(other.createdDate))
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (emailCc == null) {
			if (other.emailCc != null)
				return false;
		} else if (!emailCc.equals(other.emailCc))
			return false;
		if (encodedComment == null) {
			if (other.encodedComment != null)
				return false;
		} else if (!encodedComment.equals(other.encodedComment))
			return false;
		if (isCommentEditable != other.isCommentEditable)
			return false;
		if (sendMail != other.sendMail)
			return false;
		if (topicCommentId == null) {
			if (other.topicCommentId != null)
				return false;
		} else if (!topicCommentId.equals(other.topicCommentId))
			return false;
		if (topicEmployeeIds == null) {
			if (other.topicEmployeeIds != null)
				return false;
		} else if (!topicEmployeeIds.equals(other.topicEmployeeIds))
			return false;
		if (topicId == null) {
			if (other.topicId != null)
				return false;
		} else if (!topicId.equals(other.topicId))
			return false;
		return true;
	}
	
}
