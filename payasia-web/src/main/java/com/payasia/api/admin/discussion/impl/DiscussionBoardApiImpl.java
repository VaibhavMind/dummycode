package com.payasia.api.admin.discussion.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.annotation.Resource;
import javax.servlet.ServletContext;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.payasia.api.admin.discussion.DiscussionBoardApi;
import com.payasia.api.hris.model.Filters;
import com.payasia.api.hris.model.MultiSortMeta;
import com.payasia.api.hris.model.SearchParam;
import com.payasia.api.utils.ApiMessageHandler;
import com.payasia.api.utils.ApiMessageHandler.Type;
import com.payasia.api.utils.ApiUtils;
import com.payasia.api.utils.SearchSortUtils;
import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.EmailAttachmentDTO;
import com.payasia.common.dto.SearchSortDTO;
import com.payasia.common.dto.TopicAttachmentDTO;
import com.payasia.common.form.DiscussionBoardForm;
import com.payasia.common.form.DiscussionBoardFormResponse;
import com.payasia.common.form.DiscussionTopicCommentForm;
import com.payasia.common.form.DiscussionTopicCommentResponseForm;
import com.payasia.common.util.FileUtils;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.dao.bean.ForumTopic;
import com.payasia.logic.AWSS3Logic;
import com.payasia.logic.DiscussionBoardLogic;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * @author gauravkumar
 * @param :
 *            This class used for Discussion Board API
 */
@RestController
@RequestMapping(value = ApiUtils.API_ROOT_PATH_FOR_ADMIN+"discussionboard/")
public class DiscussionBoardApiImpl implements DiscussionBoardApi{

	@Resource
	private DiscussionBoardLogic discussionBoardLogic;
	
	@Autowired
	private ServletContext servletContext;
	
	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	private SearchSortUtils searchSortUtils;
	
	@Autowired
	@Qualifier("AWSS3Logic")
	AWSS3Logic awss3LogicImpl;
	
	@Value("#{payasiaptProperties['payasia.application.deployment.location']}")
	private String appDeployLocation;
	
	@Override
	@PostMapping(value = "search-topics")
	public ResponseEntity<?> findDiscussionBoardTopics(@RequestBody SearchParam searchParamObj, @RequestParam(value = "year", required = true) int year) {
		final Long employeeId = Long.parseLong(UserContext.getUserId());
		final Long companyId = UserContext.getClientAdminId();
		SearchSortDTO searchSortDTO = searchSortUtils.getSearchSortObject(searchParamObj);
		final List<Filters> filterlist = Arrays.asList(searchParamObj.getFilters());
		
		String searchCondition = null, searchValue = null, fromDate = null, toDate = null;
		
		for (Filters filter : filterlist) {
			switch (filter.getField()) {
			case "topicName":
				searchCondition = "topicName";
				searchValue = filter.getValue();
				break;
			case "topicAuthor":
				searchCondition = "topicAuthor";
				searchValue = filter.getValue();
				break;
			case "topicStatus":
				searchCondition = "topicStatus";
				searchValue = filter.getValue();
				break;
			case "createdOn":
				searchCondition = "createdOn";
				searchValue = filter.getValue();
				break;
			case "fromDate":
				fromDate = filter.getValue();
				break;
			case "toDate":
				toDate = filter.getValue();
				break;
			}
		}

		DiscussionBoardFormResponse discussionBoardResponse = discussionBoardLogic.findDiscussionBoardTopics(employeeId, companyId, searchSortDTO.getPageRequest(), searchSortDTO.getSortCondition(),
				searchCondition, searchValue, fromDate, toDate, year);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(discussionBoardResponse, jsonConfig);
		
		return new ResponseEntity<>(jsonObject.toString(), HttpStatus.OK);
	}
	
	@Override
	@PostMapping(value = "download-comments")
	public ResponseEntity<?> downloadComments(@RequestParam(value = "type", required = true) String type, @RequestParam(value = "topicId", required = true) String topicId){
		final Long employeeId = Long.parseLong(UserContext.getUserId());
		final Long companyId = UserContext.getClientAdminId();
		Map<String, Object> downloadCommentsMap = new HashMap<>();
		
		List<DiscussionTopicCommentResponseForm> topicCommentsForms = new ArrayList<>();
		
//		if(StringUtils.)
		topicCommentsForms = discussionBoardLogic.downloadTopicCommentsCommon(topicId, companyId, employeeId, type);
		
		String templateFileName = servletContext.getRealPath("/resources/HRISReportTemplate/DiscussionBoardTopicComments.xlsx");

		byte[] data = discussionBoardLogic.generateDiscussionTopicCommentsExcel(topicCommentsForms, templateFileName);
		
		String fileName = null;
		
		if((StringUtils.equalsIgnoreCase(type, "multiple"))){
			fileName = PayAsiaConstants.PAYASIA_DISCUSSION_BOARD_MULTIPLE_TOPIC_EXCEL_FILE_NAME;
		}
		else{
			if(topicCommentsForms!=null && topicCommentsForms.size()>0) {
				fileName = topicCommentsForms.get(0).getTopicName();
				fileName = fileName + ".xlsx";
			}
		}
		
		downloadCommentsMap.put("data", data);
		downloadCommentsMap.put("fileName", fileName);

		return new ResponseEntity<>(downloadCommentsMap, HttpStatus.OK);
	}
	
	// TODO: Below three APIs can be combined since these are called when "ADD TOPIC" is clicked
	@Override
	@GetMapping(value = "board-default-email")
	public ResponseEntity<?> getDiscussionBoardDefaultEmail() {
		final Long employeeId = Long.parseLong(UserContext.getUserId());
		final Long companyId = UserContext.getClientAdminId();

		String emailIds = discussionBoardLogic.getDisscusBoardDefaultEmail(companyId, employeeId);
		Map<String,String> defaultEmailMap = new HashMap<>();
		defaultEmailMap.put("defaultEmail", emailIds);
		return new ResponseEntity<>(defaultEmailMap, HttpStatus.OK);
	}
	
	@Override
	@GetMapping(value = "board-default-emailCC")
	public ResponseEntity<?>  getDiscussionBoardDefaultEmailCC() {
		final Long employeeId = Long.parseLong(UserContext.getUserId());
		final Long companyId = UserContext.getClientAdminId();

		String emailIds = discussionBoardLogic.getDisscusBoardDefaultEmailCC(companyId, employeeId);
		Map<String,String> defaultEmailCCMap = new HashMap<>();
		defaultEmailCCMap.put("defaultEmailCC", emailIds);
		return new ResponseEntity<>(defaultEmailCCMap, HttpStatus.OK);
	}
	
	@Override
	@PostMapping(value = "topic-default-email")
	public ResponseEntity<?> getTopicDefaultEmail(@RequestParam(value = "topicId", required = true) Long topicId) {
		final Long employeeId = Long.parseLong(UserContext.getUserId());
		final Long companyId = UserContext.getClientAdminId();
		topicId = FormatPreserveCryptoUtil.decrypt(topicId);

		String emailIds = discussionBoardLogic.getTopicDefaultEmail(companyId, topicId, employeeId);
		Map<String,String> defaultEmailMap = new HashMap<>();
		defaultEmailMap.put("defaultEmail", emailIds);
		return new ResponseEntity<>(defaultEmailMap, HttpStatus.OK);
	}

	@Override
	@PostMapping(value = "topic-default-emailCC")
	public ResponseEntity<?> getTopicDefaultEmailCC(@RequestParam(value = "topicId", required = true) Long topicId) {
		final Long employeeId = Long.parseLong(UserContext.getUserId());
		final Long companyId = UserContext.getClientAdminId();
		topicId = FormatPreserveCryptoUtil.decrypt(topicId);

		String emailIds = discussionBoardLogic.getTopicDefaultEmailCC(companyId, topicId, employeeId);
		Map<String,String> defaultEmailCCMap = new HashMap<>();
		defaultEmailCCMap.put("defaultEmailCC", emailIds);
		return new ResponseEntity<>(defaultEmailCCMap, HttpStatus.OK);
	}
	
	@Override
	@GetMapping(value = "enable-visibility")
	public ResponseEntity<?> getEnableVisibility() {
		final Long companyId = UserContext.getClientAdminId();

		String enableVisibility = discussionBoardLogic.getEnableVisibility(companyId);
		Map<String,String> enableVisibilityMap = new HashMap<>();
		enableVisibilityMap.put("enableVisibility", enableVisibility);
		return new ResponseEntity<>(enableVisibilityMap, HttpStatus.OK);
	}
	
	@Override
	@PostMapping(value = "add-topic")
	public ResponseEntity<?> addDiscussionBoardTopic(@RequestBody DiscussionBoardForm discussionBoardForm, @RequestParam(value = "employeeIdsList", required = true) String employeeIdsList) {
		final Long employeeId = Long.parseLong(UserContext.getUserId());
		final Long companyId = UserContext.getClientAdminId();

		String addTopicValue = discussionBoardLogic.addDiscussionBoardTopic(discussionBoardForm, companyId, employeeId, employeeIdsList);
		if(StringUtils.equalsIgnoreCase(addTopicValue, PayAsiaConstants.PAYASIA_ERROR)){
			return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.discussion.board.topic.added.error", new Object[]{}, UserContext.getLocale())), HttpStatus.NOT_FOUND);
		}
		else if(StringUtils.equalsIgnoreCase(addTopicValue, PayAsiaConstants.FALSE)) {
			return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.discussion.board.topic.name.already.exists", new Object[]{}, UserContext.getLocale())), HttpStatus.NOT_FOUND);
		}
		else {
			return new ResponseEntity<>(new ApiMessageHandler(Type.SUCCESS, HttpStatus.OK.toString(), messageSource.getMessage("payasia.discussion.board.topic.added.successfully", new Object[]{}, UserContext.getLocale())), HttpStatus.OK);
		}
	}
	
	@Override
	@GetMapping(value = "discussion-topic", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
	public ResponseEntity<?> getDiscussionTopic() {
		Map<String, Object> discussionTopicMap = new HashMap<>();
		
		DiscussionTopicCommentForm discussionTopicReplyForm = new DiscussionTopicCommentForm();
		discussionTopicMap.put("discussionTopicReplyForm", discussionTopicReplyForm);

		DiscussionBoardForm discussionBoardForm = new DiscussionBoardForm();
		discussionTopicMap.put("discussionBoardForm", discussionBoardForm);
		return new ResponseEntity<>(discussionTopicMap, HttpStatus.OK);
	}
	
	@Override
	@PostMapping(value = "topic-comment-list")
	public ResponseEntity<?> getTopicCommentList(@RequestParam (value = "topicId") Long topicId) {
		final Long employeeId = Long.parseLong(UserContext.getUserId());
		final Long companyId = UserContext.getClientAdminId();
		topicId = FormatPreserveCryptoUtil.decrypt(topicId);
		
		DiscussionTopicCommentResponseForm discussionTopicForm = discussionBoardLogic.getTopicCommentList(topicId, companyId, employeeId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(discussionTopicForm, jsonConfig);
		return new ResponseEntity<>(jsonObject.toString(), HttpStatus.OK);
	}
	
	@Override
	@PostMapping(value = "forum-topic-data")
	public ResponseEntity<?> getForumTopicDataById(@RequestParam(value = "topicId", required = true) Long topicId) {
		final Long companyId = UserContext.getClientAdminId();
		topicId=FormatPreserveCryptoUtil.decrypt(topicId);
		
		DiscussionBoardFormResponse discussionBoardFormResponse = discussionBoardLogic.getForumTopicDataById(topicId, companyId);
		if (discussionBoardFormResponse == null) {
			return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("data.info.nodata", new Object[]{}, UserContext.getLocale())), HttpStatus.NOT_FOUND);
		}
		else{
			JsonConfig jsonConfig = new JsonConfig();
			JSONObject jsonObject = JSONObject.fromObject(discussionBoardFormResponse.getDiscussionBoardForm(), jsonConfig);
			return new ResponseEntity<>(jsonObject.toString(), HttpStatus.OK);
		}
	}
	
	@Override
	@PostMapping(value = "status-history")
	public ResponseEntity<?> getDiscussionBoardTopicStatusHistory(@RequestBody SearchParam searchParamObj, @RequestParam(value = "topicId", required = true) Long topicId) {
		final Long employeeId = Long.parseLong(UserContext.getUserId());
		final Long companyId = UserContext.getClientAdminId();
		topicId = FormatPreserveCryptoUtil.decrypt(topicId);
		
		SearchSortDTO searchSortDTO = searchSortUtils.getSearchSortObject(searchParamObj);
		
		DiscussionBoardFormResponse discussionBoardResponse = null;
		discussionBoardResponse = discussionBoardLogic.getDiscussionBoardTopicStatusHistory(employeeId, companyId, searchSortDTO.getPageRequest(), 
				searchSortDTO.getSortCondition(), topicId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(discussionBoardResponse, jsonConfig);
		return new ResponseEntity<>(jsonObject.toString(), HttpStatus.OK);
	}
	
	@Override
	@PutMapping(value = "update-topic")
	public ResponseEntity<?> updateDiscussionBoardTopic(@RequestBody DiscussionBoardForm discussionBoardForm, @RequestParam(value = "employeeIdsList", required = false) String employeeIdsList) {
		final Long employeeId = Long.parseLong(UserContext.getUserId());
		final Long companyId = UserContext.getClientAdminId();
		discussionBoardForm.setTopicId(FormatPreserveCryptoUtil.decrypt(discussionBoardForm.getTopicId()));

		String updatedStatus = discussionBoardLogic.updateDiscussionBoardTopic(companyId, employeeId, discussionBoardForm, employeeIdsList);
		
		if(StringUtils.equalsIgnoreCase(updatedStatus, PayAsiaConstants.TRUE)){
			return new ResponseEntity<>(new ApiMessageHandler(Type.SUCCESS, HttpStatus.OK.toString(), messageSource.getMessage("payasia.discussion.board.topic.updated.successfully", new Object[]{}, UserContext.getLocale())), HttpStatus.OK);
		}
		return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.discussion.board.topic.name.already.exists", new Object[]{}, UserContext.getLocale())), HttpStatus.NOT_FOUND);
	}
	
	
	@Override
	@PostMapping(value = "add-topic-comment")
	public ResponseEntity<?> addTopicComment(@RequestParam("discussionTopicReplyForm") String jsonStr, @RequestParam(value = "files", required = false) CommonsMultipartFile[] files) {
		final Long employeeId = Long.parseLong(UserContext.getUserId());
		final Long companyId = UserContext.getClientAdminId();
		
		DiscussionTopicCommentForm discussionTopicReplyForm = new DiscussionTopicCommentForm();
		
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObj = JSONObject.fromObject(jsonStr, jsonConfig);
		discussionTopicReplyForm.setTopicId(jsonObj.getLong("topicId"));
		discussionTopicReplyForm.setSendMail(jsonObj.getBoolean("sendMail"));
		discussionTopicReplyForm.setEmail(jsonObj.getString("email"));
		discussionTopicReplyForm.setEmailCc(jsonObj.getString("emailCc"));
		discussionTopicReplyForm.setEncodedComment(jsonObj.getString("encodedComment"));
		discussionTopicReplyForm.setTopicEmployeeIds(jsonObj.getString("topicEmployeeIds"));
		discussionTopicReplyForm.setCommentStatus(jsonObj.getString("commentStatus"));

		List<TopicAttachmentDTO> attachmentList = new ArrayList<>();
		
		if(files!=null && files.length>0) {
			for(CommonsMultipartFile commonsMultipartFile : files) {
				TopicAttachmentDTO topicAttachmentDTO = new TopicAttachmentDTO();
				topicAttachmentDTO.setAttachment(commonsMultipartFile);
				attachmentList.add(topicAttachmentDTO);
			}
			discussionTopicReplyForm.setAttachmentList(attachmentList);
		}
		
		boolean isValid = checkValidFileType(discussionTopicReplyForm);
		
		if (isValid) {
			
			discussionTopicReplyForm.setTopicId(FormatPreserveCryptoUtil.decrypt(discussionTopicReplyForm.getTopicId()));
			String msg = discussionBoardLogic.addTopicComment(discussionTopicReplyForm, discussionTopicReplyForm.getTopicEmployeeIds(), companyId, employeeId);
			if(msg.startsWith("TopicCommentId#")){
				return new ResponseEntity<>(new ApiMessageHandler(Type.SUCCESS, HttpStatus.OK.toString(), messageSource.getMessage("payasia.discussion.board.topic.comment.added.successfully", new Object[]{}, UserContext.getLocale())), HttpStatus.OK);
			}
			return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage(msg, new Object[]{}, UserContext.getLocale())), HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.discussion.board.topic.invalid.file", new Object[]{}, UserContext.getLocale())), HttpStatus.NOT_FOUND);
	}
	
	@Override
	@PostMapping(value = "topic-comment-attachment-list")
	public ResponseEntity<?> getTopicCommentAttachmentList(@RequestParam(value = "topicCommentId", required = true) Long topicCommentId) {
		final Long employeeId = Long.parseLong(UserContext.getUserId());
		final Long companyId = UserContext.getClientAdminId();
		topicCommentId = FormatPreserveCryptoUtil.decrypt(topicCommentId);

		DiscussionTopicCommentResponseForm discussionTopicForm = discussionBoardLogic.getTopicCommentAttachmentList(topicCommentId, companyId, employeeId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(discussionTopicForm, jsonConfig);
		return new ResponseEntity<>(jsonObject.toString(), HttpStatus.OK);
	}
	
	@Override
	@PostMapping(value = "update-topic-comment")
	public ResponseEntity<?> updateTopicComment(@RequestParam("discussionTopicReplyForm") String jsonStr, @RequestParam(value = "files", required = false) CommonsMultipartFile[] files){
		final Long employeeId = Long.parseLong(UserContext.getUserId());
		final Long companyId = UserContext.getClientAdminId();
		
		DiscussionTopicCommentForm discussionTopicReplyForm = new DiscussionTopicCommentForm();
		
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObj = JSONObject.fromObject(jsonStr, jsonConfig);
		discussionTopicReplyForm.setTopicId(jsonObj.getLong("topicId"));
		discussionTopicReplyForm.setTopicCommentId(FormatPreserveCryptoUtil.decrypt(jsonObj.getLong("topicCommentId")));
		discussionTopicReplyForm.setSendMail(jsonObj.getBoolean("sendMail"));
		discussionTopicReplyForm.setEmail(jsonObj.getString("email"));
		discussionTopicReplyForm.setEmailCc(jsonObj.getString("emailCc"));
		discussionTopicReplyForm.setEncodedComment(jsonObj.getString("encodedComment"));
		discussionTopicReplyForm.setTopicEmployeeIds(jsonObj.getString("topicEmployeeIds"));
		discussionTopicReplyForm.setCommentStatus(jsonObj.getString("commentStatus"));
		
		List<TopicAttachmentDTO> attachmentList = new ArrayList<>();
		
		if(files!=null && files.length>0) {
			for(CommonsMultipartFile commonsMultipartFile : files) {
				TopicAttachmentDTO topicAttachmentDTO = new TopicAttachmentDTO();
				topicAttachmentDTO.setAttachment(commonsMultipartFile);
				attachmentList.add(topicAttachmentDTO);
			}
			discussionTopicReplyForm.setAttachmentList(attachmentList);
		}
		
		boolean isFileValid = checkValidFileType(discussionTopicReplyForm);
		
		if (isFileValid) {
			String msg = discussionBoardLogic.updateTopicComment(discussionTopicReplyForm, companyId, employeeId, discussionTopicReplyForm.getTopicEmployeeIds());
			if(StringUtils.equalsIgnoreCase(msg, PayAsiaConstants.PAYASIA_SUCCESS)){
				return new ResponseEntity<>(new ApiMessageHandler(Type.SUCCESS, HttpStatus.OK.toString(), messageSource.getMessage("payasia.discussion.board.topic.comment.updated.successfully", new Object[]{}, UserContext.getLocale())), HttpStatus.OK);
			}
			return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage(msg, new Object[]{}, UserContext.getLocale())), HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.discussion.board.topic.invalid.file", new Object[]{}, UserContext.getLocale())), HttpStatus.NOT_FOUND);
	}
	
	private boolean checkValidFileType(DiscussionTopicCommentForm discussionTopicReplyForm) {
		if (discussionTopicReplyForm.getAttachmentList() != null) {
			for (TopicAttachmentDTO topicAttachmentDTO : discussionTopicReplyForm.getAttachmentList()) {
				if ((topicAttachmentDTO.getAttachment() != null) && (topicAttachmentDTO.getAttachment().getSize() > 0)) {
					boolean isFileValid = FileUtils.isValidFile(topicAttachmentDTO.getAttachment(),
							topicAttachmentDTO.getAttachment().getOriginalFilename(),
							PayAsiaConstants.ALLOWED_GENERAL_UPLOAD_FILE_EXT,
							PayAsiaConstants.ALLOWED_GENERAL_UPLOAD_FILE_MINE_TYPE, PayAsiaConstants.FILE_SIZE);
					if (!isFileValid) {
						return false;
					}
				}
			}
		}
		return true;
	}
	
	@Override
	@PostMapping(value = "topic-comment-data")
	public ResponseEntity<?> getTopicCommentData(@RequestParam (value = "topicCommentId", required = true) Long topicCommentId){
		final Long companyId = UserContext.getClientAdminId();
		topicCommentId=FormatPreserveCryptoUtil.decrypt(topicCommentId);
		
		DiscussionTopicCommentForm discussionTopicReplyForm = discussionBoardLogic.getTopicCommentData(topicCommentId,companyId);
		
		if (discussionTopicReplyForm == null) {
			return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("data.info.nodata", new Object[]{}, UserContext.getLocale())), HttpStatus.NOT_FOUND);
		}
		else{
			JsonConfig jsonConfig = new JsonConfig();
			JSONObject jsonObject = JSONObject.fromObject(discussionTopicReplyForm, jsonConfig);
			return new ResponseEntity<>(jsonObject.toString(), HttpStatus.OK);
		}
	}
	
	@Override
	@DeleteMapping(value = "delete-topic-comment")
	public ResponseEntity<?> deleteTopicComment(@RequestParam(value = "topicCommentId", required = true) Long topicCommentId){
		final Long companyId = UserContext.getClientAdminId();
		topicCommentId=FormatPreserveCryptoUtil.decrypt(topicCommentId);
		
		String deleteStatus = discussionBoardLogic.deleteTopicComment(topicCommentId, companyId);
		if(StringUtils.equalsIgnoreCase(deleteStatus, PayAsiaConstants.TRUE)){
			return new ResponseEntity<>(new ApiMessageHandler(Type.SUCCESS, HttpStatus.OK.toString(), messageSource.getMessage("payasia.discussion.board.topic.comment.deleted.successfully", new Object[]{}, UserContext.getLocale())), HttpStatus.OK);
		}
		return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.discussion.board.topic.comment.deleted.error", new Object[]{}, UserContext.getLocale())), HttpStatus.NOT_FOUND);
	}
	
	@Override
	@DeleteMapping(value = "delete-discussion-board-topic")
	public ResponseEntity<?> deleteDiscussionBoardTopic(@RequestParam(value = "topicId", required = true) Long topicId) {
		final Long companyId = UserContext.getClientAdminId();
		topicId=FormatPreserveCryptoUtil.decrypt(topicId);
        
		String deleteStatus = discussionBoardLogic.deleteDiscussionBoardTopic(companyId, topicId);
		if(StringUtils.equalsIgnoreCase(deleteStatus, PayAsiaConstants.TRUE)){
			return new ResponseEntity<>(new ApiMessageHandler(Type.SUCCESS, HttpStatus.OK.toString(), messageSource.getMessage("payasia.discussion.board.topic.deleted.successfully", new Object[]{}, UserContext.getLocale())), HttpStatus.OK);
		}
		return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.discussion.board.topic.deleted.error", new Object[]{}, UserContext.getLocale())), HttpStatus.NOT_FOUND);
	}
	
	@Override
	@PostMapping(value = "search-employee-with-image")
	public ResponseEntity<?> searchEmployee(@RequestBody SearchParam searchParamObj, @RequestParam(value = "topicId", required = false) String topicId, 
			@RequestParam(value = "metaData", required = false) String metaData,
			@RequestParam(value = "includeResignedEmployees", required = false) Boolean includeResignedEmployees) {
		
		final Long companyId = Long.valueOf(UserContext.getWorkingCompanyId());
		final Long employeeId = Long.valueOf(UserContext.getUserId());
		final List<MultiSortMeta> multisortlist = Arrays.asList(searchParamObj.getMultiSortMeta());
		final List<Filters> filterlist = Arrays.asList(searchParamObj.getFilters());
		String searchFor = "SearchEmployee";
		SearchSortDTO searchSortDTO = searchSortUtils.getSearchSortObject(searchParamObj);

		if (multisortlist != null && !multisortlist.isEmpty()) {
			searchSortDTO.getSortCondition().setColumnName(multisortlist.get(0).getField());
			searchSortDTO.getSortCondition().setOrderType(multisortlist.get(0).getOrder().equals("1") ? PayAsiaConstants.ASC : PayAsiaConstants.DESC);
		}

		String searchCondition = null;
		String searchValue = null;
		
		for (Filters filter : filterlist) {
			switch (filter.getField()) {
			case "employeeNumber":
				searchCondition = "empno";
				searchValue = filter.getValue();
				break;
			case "firstName":
				searchCondition = "firstName";
				searchValue = filter.getValue();
				break;
			case "lastName":
				searchCondition = "lastName";
				searchValue = filter.getValue();
				break;
			}
		}
		
		ForumTopic topicData = null;
		if(StringUtils.isNotBlank(topicId)) {
			topicData = discussionBoardLogic.getTopicData(companyId, Long.parseLong(topicId));
			searchFor = "SearchTopicEmployee";
		}
		String topicEmployeeList = (topicData!=null)?topicData.getTopicEmployeeIds():"";
		
		DiscussionBoardFormResponse discussionBoardFormResponse = discussionBoardLogic.searchEmployee(topicData, searchSortDTO.getPageRequest(),
				searchSortDTO.getSortCondition(), searchCondition, searchValue, companyId, employeeId, metaData, includeResignedEmployees, searchFor, topicEmployeeList);

		return new ResponseEntity<>(discussionBoardFormResponse, HttpStatus.OK);
	}

	@Override
	@GetMapping(value = "view-attachments")
	public ResponseEntity<?> viewSeletecdAttachments(@RequestParam(value = "attachmentIds", required = true) String attachmentIds) throws IOException {
		final Long employeeId = Long.parseLong(UserContext.getUserId());
		final Long companyId = UserContext.getClientAdminId();
		
		Map<String, Object> zipMap = new HashMap<>();
		
		DiscussionTopicCommentForm discussionTopicCommentForm = discussionBoardLogic.viewSeletecdAttachments(attachmentIds, companyId, employeeId);
		List<TopicAttachmentDTO> attachmentDTOList = discussionTopicCommentForm.getAttachmentList();
		
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(byteArrayOutputStream);
        ZipOutputStream zipOutputStream = new ZipOutputStream(bufferedOutputStream);

        BufferedInputStream fif = null;
		FileInputStream fis = null;
		
        for (TopicAttachmentDTO emailAttachmentDTO : attachmentDTOList) {
        	zipOutputStream.putNextEntry(new ZipEntry(emailAttachmentDTO.getAttachmentName()));
			String filePath = emailAttachmentDTO.getAttachmentPath();

			if (PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION.equalsIgnoreCase(appDeployLocation)) {
				InputStream fins = awss3LogicImpl.readS3ObjectAsStream(filePath);
				fif = new BufferedInputStream(fins);
			} else {
				File newfile = new File(filePath);
				fis = new FileInputStream(newfile);
				fif = new BufferedInputStream(fis);
			}
		}
		IOUtils.toByteArray(fif);
		zipOutputStream.closeEntry();

        if (zipOutputStream != null) {
            zipOutputStream.finish();
            zipOutputStream.flush();
            IOUtils.closeQuietly(zipOutputStream);
        }
        IOUtils.closeQuietly(bufferedOutputStream);
        IOUtils.closeQuietly(byteArrayOutputStream);
        zipMap.put("zipFile", byteArrayOutputStream.toByteArray());
        zipMap.put("zipFileName", discussionTopicCommentForm.getAttachmentName());
        zipMap.put("zipFileExtension", "zip");
        return new ResponseEntity<>(zipMap, HttpStatus.OK);
    }
	
	@Override
	@GetMapping(value = "view-attachment")
	public ResponseEntity<?> viewAttachment(@RequestParam(value = "attachmentId", required = true) Long attachmentId) {
		final Long employeeId = Long.parseLong(UserContext.getUserId());
		final Long companyId = UserContext.getClientAdminId();
		
		byte[] bytes = null;
		Map<String, Object> attMap = new HashMap<>();
		EmailAttachmentDTO attachmentDTO = discussionBoardLogic.viewAttachment(attachmentId, companyId, employeeId);
		if(attachmentDTO == null) {
			return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("data.info.nodata", new Object[]{}, UserContext.getLocale())), HttpStatus.NOT_FOUND);
		}
		String filePath = attachmentDTO.getAttachmentPath();
		BufferedInputStream is;

		if (!StringUtils.isEmpty(filePath)) {
			FileInputStream fis;
			try {
				File file = new File(filePath);

				if (PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION.equalsIgnoreCase(appDeployLocation) || 
						(file.exists() && !PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION.equalsIgnoreCase(appDeployLocation))) {

					if (!PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION.equalsIgnoreCase(appDeployLocation)) {
						fis = new FileInputStream(file);
						is = new BufferedInputStream(fis);
					} 
					else {
						InputStream fins = awss3LogicImpl.readS3ObjectAsStream(filePath);
						is = new BufferedInputStream(fins);
					}
					bytes = IOUtils.toByteArray(is);
					
					attMap.put("fullFileName", attachmentDTO.getAttachmentName());
					attMap.put("fileName", StringUtils.substringBefore(attachmentDTO.getAttachmentName(), "."));
					attMap.put("fileNameExtension", StringUtils.substringAfter(attachmentDTO.getAttachmentName(), "."));
					attMap.put("data", bytes);

					is.close();
				}
			} 
			catch (IOException iOException) {
				
			} 
		}
		return new ResponseEntity<>(attMap, HttpStatus.OK);
	}
	
}

