package com.payasia.logic;

import java.util.List;
import java.util.Set;

import com.payasia.common.dto.EmailAttachmentDTO;
import com.payasia.common.form.DiscussionBoardForm;
import com.payasia.common.form.DiscussionBoardFormResponse;
import com.payasia.common.form.DiscussionTopicCommentForm;
import com.payasia.common.form.DiscussionTopicCommentResponseForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.ForumTopic;

public interface DiscussionBoardLogic {

	DiscussionBoardFormResponse findDiscussionBoardTopics(Long employeeId, Long companyId, PageRequest pageDTO,
			SortCondition sortDTO, String searchCondition, String searchText, String searchCreatedFromText,
			String searchCreatedToText, int year);

	DiscussionBoardFormResponse getForumTopicDataById(Long topicId, Long companyId);

	String addDiscussionBoardTopic(DiscussionBoardForm discussionBoardForm, Long companyId, Long empId,
			String employeeIdsList);

	String updateDiscussionBoardTopic(Long companyId, Long employeeId, DiscussionBoardForm discussionBoardForm,
			String employeeIdsList);

	String deleteDiscussionBoardTopic(Long companyId, Long topicId);

	DiscussionTopicCommentResponseForm getTopicCommentAttachmentList(Long topicCommentId, Long companyId,
			Long employeeId);

	String deleteAttachment(long attachmentId, Long companyId);

	DiscussionTopicCommentForm viewSeletecdAttachments(String attachmentIds, Long companyId, Long employeeId);

	EmailAttachmentDTO viewAttachment(Long attachmentId, Long companyId, Long employeeId);

	DiscussionTopicCommentForm getTopicCommentData(long topicCommentId);

	String updateTopicComment(DiscussionTopicCommentForm discussionTopicReplyForm, Long companyId, Long employeeId,
			String employeeIdsList);

	String deleteTopicComment(Long topicCommentId, Long companyId);

	String addTopicComment(DiscussionTopicCommentForm discussionTopicReplyForm, String employeeIdsList, Long companyId,
			Long employeeId);

	String getTopicDefaultEmail(Long companyId, Long topicId, Long employeeId);

	String getDisscusBoardDefaultEmail(Long companyId, Long employeeId);

	DiscussionTopicCommentResponseForm getTopicCommentList(long topicId, Long companyId, Long employeeId);

	DiscussionBoardFormResponse getDiscussionBoardTopicStatusHistory(Long employeeId, Long companyId,
			PageRequest pageDTO, SortCondition sortDTO, Long topicId);

	DiscussionTopicCommentResponseForm downloadDiscussionBoardTopicComments(long topicId, Long companyId,
			Long employeeId);

	List<DiscussionTopicCommentResponseForm> downloadMultipleTopicComments(String topicIds, Long companyId,
			Long employeeId);

	/**
	 * The method to generate excel file for discussion topics
	 * 
	 * @param topicCommentForms
	 * @return Excel file in bytes
	 */
	public byte[] generateDiscussionTopicCommentsExcel(List<DiscussionTopicCommentResponseForm> topicCommentForms,
			String templateFilePath);

	Set<Integer> getDistinctYears(Long companyId);

	String getTopicDefaultEmailCC(Long companyId, Long topicId, Long employeeId);

	String getDisscusBoardDefaultEmailCC(Long companyId, Long employeeId);

	DiscussionBoardFormResponse searchEmployee(ForumTopic topicData, PageRequest pageDTO, SortCondition sortDTO,
			String searchCondition, String searchText, Long companyId, Long employeeId, String metaData,
			Boolean includeResignedEmployees, String sourceFor, String topicEmployeeList);

	ForumTopic getTopicData(Long companyId, Long topicId);

	String getEnableVisibility(Long companyId);
	
	DiscussionTopicCommentForm getTopicCommentData(long topicCommentId,Long companyId);

	List<DiscussionTopicCommentResponseForm> downloadTopicCommentsCommon(String string, Long companyId, Long employeeId,
			String string2);
}
