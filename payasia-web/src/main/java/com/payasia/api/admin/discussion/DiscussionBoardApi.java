package com.payasia.api.admin.discussion;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.payasia.api.hris.model.SearchParam;
import com.payasia.common.form.DiscussionBoardForm;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

@Api(tags="DISCUSSION BOARD", description="Discussion Board related APIs")
public interface DiscussionBoardApi {

	@ApiOperation(value = "Discussion Board Topics", notes = "Discussion Board Topics can be searched and displayed.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> findDiscussionBoardTopics(SearchParam searchParamObj, int year);

	@ApiOperation(value = "Download Discussion Board Topics", notes = "Discussion Board Topics can be downloaded.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> downloadComments(String type, String topicId);

	@ApiOperation(value = "Discussion Board default email", notes = "Default email can be fetched using this API.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> getDiscussionBoardDefaultEmail();

	@ApiOperation(value = "Discussion Board default emailcc", notes = "Default emailcc can be fetched using this API.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> getDiscussionBoardDefaultEmailCC();

	@ApiOperation(value = "Topic default email", notes = "Default email can be fetched using this API.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> getTopicDefaultEmail(Long topicId);

	@ApiOperation(value = "Topic default emailcc", notes = "Default emailcc can be fetched using this API.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> getTopicDefaultEmailCC(Long topicId);
	
	@ApiOperation(value = "Enable Visibility", notes = "Check the visibility status.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> getEnableVisibility();

	@ApiOperation(value = "Add Discussion Board topic", notes = "Discussion Board Topics can be added.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> addDiscussionBoardTopic(DiscussionBoardForm discussionBoardForm, String employeeIdsList);

	@ApiOperation(value = "Discussion Board topic forms", notes = "Show Discussion Board Topics.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> getDiscussionTopic();

	@ApiOperation(value = "Discussion Board topic comment list", notes = "Show Discussion Board Topic Comment List.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> getTopicCommentList(Long topicId);

	@ApiOperation(value = "To fetch topic data", notes = "Topic data is provided for editing.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> getForumTopicDataById(Long topicId);

	@ApiOperation(value = "Display Discussion Board Topic Status History", notes = "Discussion Board Topic Status History can be displayed on the basis of topicId.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> getDiscussionBoardTopicStatusHistory(SearchParam searchParamObj, Long topicId);

	@ApiOperation(value = "Update Discussion Board Topic", notes = "Discussion Board Topic can be updated.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> updateDiscussionBoardTopic(DiscussionBoardForm discussionBoardForm, String employeeIdsList);

	@ApiOperation(value = "Insert Discussion Board Topic Comment", notes = "Discussion Board Topic comment can be added.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> addTopicComment(String jsonStr, CommonsMultipartFile[] files);

	@ApiOperation(value = "View topic attachment list", notes = "Topic attachment list can be viewed.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> getTopicCommentAttachmentList(Long topicCommentId);

	@ApiOperation(value = "Update Discussion Board Topic comment", notes = "Discussion Board Topic comment can be updated.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> updateTopicComment(String jsonStr, CommonsMultipartFile[] files);
	
	@ApiOperation(value = "Get Discussion Board Topic comment data", notes = "Discussion Board Topic comment data can be fetched.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> getTopicCommentData(Long topicCommentId);

	@ApiOperation(value = "Delete Discussion Board Topic comment", notes = "Discussion Board Topic comment can be deleted.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> deleteTopicComment(Long topicCommentId);

	@ApiOperation(value = "Delete Discussion Board Topic", notes = "Discussion Board Topic can be deleted.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> deleteDiscussionBoardTopic(Long topicId);

	@ApiOperation(value = "Search Employee with image", notes = "Employee image can be searched with image.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> searchEmployee(SearchParam searchParamObj, String topicId, String metaData, Boolean includeResignedEmployees);
	
	@ApiOperation(value = "View attachments", notes = "Attached files can be viewed.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> viewSeletecdAttachments(String attachmentIds) throws IOException;

	@ApiOperation(value = "View attachment", notes = "Attached file can be viewed.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> viewAttachment(Long attachmentId);

}
