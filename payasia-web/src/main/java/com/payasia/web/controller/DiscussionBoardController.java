package com.payasia.web.controller;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;

import com.payasia.common.form.DiscussionBoardForm;
import com.payasia.common.form.DiscussionTopicCommentForm;

public interface DiscussionBoardController {

	String findDiscussionBoardTopics(String columnName, String sortingType, String searchCondition, String searchText,
			String searchCreatedFromText, String searchCreatedToText, int page, int rows, int year,
			HttpServletRequest request);

	String getForumTopicDataById(String topicId, HttpServletRequest request);

	String addDiscussionBoardTopic(DiscussionBoardForm discussionBoardForm, String employeeIdsList,
			HttpServletRequest request);

	String updateDiscussionBoardTopic(DiscussionBoardForm discussionBoardForm, String employeeIdsList,
			HttpServletRequest request);

	String deleteDiscussionBoardTopic(String topicId, HttpServletRequest request);

	String getTopicCommentAttachmentList(Long topicCommentId, ModelMap model, HttpServletResponse response,
			HttpServletRequest request);

	String deleteAttachment(long attachmentId, HttpServletResponse response, HttpServletRequest request);

	void viewSeletecdAttachments(String attachmentIds, HttpServletResponse response, HttpServletRequest request);

	void viewAttachment(Long attachmentId, HttpServletResponse response, HttpServletRequest request);

	String getTopicCommentList(long topicId, ModelMap model, HttpServletResponse response, HttpServletRequest request);

	String getTopicCommentData(long topicCommentId, ModelMap model);

	String updateTopicComment(DiscussionTopicCommentForm discussionTopicReplyForm, String employeeIdsList,
			BindingResult result, ModelMap model, HttpServletRequest request);

	String deleteTopicComment(Long topicCommentId, HttpServletRequest request);

	String addTopicComment(DiscussionTopicCommentForm discussionTopicReplyForm, BindingResult result, ModelMap model,
			HttpServletRequest request);

	void downloadDiscussionBoardTopicComments(Long topicId, HttpServletRequest request, HttpServletResponse response,
			Locale locale);

	String getTopicDefaultEmail(Long topicId, HttpServletRequest request, HttpServletResponse response);

	String getDisscusBoardDefaultEmail(HttpServletRequest request, HttpServletResponse response);

	String getdDiscussionBoardTopicStatusHistory(String columnName, String sortingType, int page, int rows,
			Long topicId, HttpServletRequest request);

	void downloadMultipleTopicComments(String topicIds, HttpServletRequest request, HttpServletResponse response,
			Locale locale);

	String getYearList(HttpServletRequest request, Locale locale);

	String getTopicDefaultEmailCC(Long topicId, HttpServletRequest request, HttpServletResponse response);

	String getDisscusBoardDefaultEmailCC(HttpServletRequest request, HttpServletResponse response);

	String searchEmployee(String columnName, String sortingType, int page, int rows, String searchCondition,
			String searchText, String metaData, Boolean includeResignedEmployees, HttpServletRequest request,
			HttpServletResponse response);

	String searchTopicEmployees(String TopicId, String columnName, String sortingType, int page, int rows,
			String searchCondition, String searchText, String metaData, Boolean includeResignedEmployees,
			HttpServletRequest request, HttpServletResponse response);

	String getEnableVisibility(HttpServletRequest request, HttpServletResponse response);
}
