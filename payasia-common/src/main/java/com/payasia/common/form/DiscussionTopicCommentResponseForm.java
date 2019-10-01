package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class DiscussionTopicCommentResponseForm implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3917783132582200283L;
	private Long topicId;
	private String topicName;
	private String topicDesc;
	private boolean isTopicEditable;
	private boolean isTopicVisibilityEditable;
	private List<DiscussionTopicCommentForm> discussionTopicCommentFormList;
	private List<String> topicNameList;
	private String generatedBy;
	private boolean status;
	private Map<String,List<DiscussionTopicCommentForm>> discussionTopicCommentMap;
	private Map<String,List<DiscussionBoardForm>> discussionTopicMap;
	
	
	public boolean isTopicVisibilityEditable() {
		return isTopicVisibilityEditable;
	}
	public void setTopicVisibilityEditable(boolean isTopicVisibilityEditable) {
		this.isTopicVisibilityEditable = isTopicVisibilityEditable;
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
	public List<DiscussionTopicCommentForm> getDiscussionTopicCommentFormList() {
		return discussionTopicCommentFormList;
	}
	public void setDiscussionTopicCommentFormList(
			List<DiscussionTopicCommentForm> discussionTopicCommentFormList) {
		this.discussionTopicCommentFormList = discussionTopicCommentFormList;
	}
	public boolean isTopicEditable() {
		return isTopicEditable;
	}
	public void setTopicEditable(boolean isTopicEditable) {
		this.isTopicEditable = isTopicEditable;
	}
	public String getTopicDesc() {
		return topicDesc;
	}
	public void setTopicDesc(String topicDesc) {
		this.topicDesc = topicDesc;
	}
	public String getGeneratedBy() {
		return generatedBy;
	}
	public void setGeneratedBy(String generatedBy) {
		this.generatedBy = generatedBy;
	}
	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
	public List<String> getTopicNameList() {
		return topicNameList;
	}
	public void setTopicNameList(List<String> topicNameList) {
		this.topicNameList = topicNameList;
	}
	public Map<String, List<DiscussionTopicCommentForm>> getDiscussionTopicCommentMap() {
		return discussionTopicCommentMap;
	}
	public void setDiscussionTopicCommentMap(
			Map<String, List<DiscussionTopicCommentForm>> discussionTopicCommentMap) {
		this.discussionTopicCommentMap = discussionTopicCommentMap;
	}
	public Map<String, List<DiscussionBoardForm>> getDiscussionTopicMap() {
		return discussionTopicMap;
	}
	public void setDiscussionTopicMap(
			Map<String, List<DiscussionBoardForm>> discussionTopicMap) {
		this.discussionTopicMap = discussionTopicMap;
	}
	
	
	

	
	
	

}
