package com.payasia.test.discussionboard.api;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
														  
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.RestController;

import com.payasia.api.admin.discussion.impl.DiscussionBoardApiImpl;
import com.payasia.api.hris.model.Filters;
import com.payasia.api.hris.model.MultiSortMeta;
import com.payasia.api.hris.model.SearchParam;
import com.payasia.api.utils.ApiMessageHandler;
import com.payasia.api.utils.ApiMessageHandler.Type;
import com.payasia.api.utils.SearchSortUtils;
import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.SearchSortDTO;
import com.payasia.common.form.DiscussionBoardForm;
import com.payasia.common.form.DiscussionBoardFormResponse;
import com.payasia.common.form.DiscussionTopicCommentForm;
import com.payasia.common.form.DiscussionTopicCommentResponseForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.logic.DiscussionBoardLogic;
import com.payasia.test.root.TDDConfig;
import com.payasia.test.util.TestUtil;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

@RestController
public class DiscussionBoardApiTDD extends TDDConfig{

	@InjectMocks
	private DiscussionBoardApiImpl discussionBoardApiImpl;
	
	@Mock
	private DiscussionBoardLogic discussionBoardLogic;
	
	@Mock
	private ServletContext servletContext;

	@Mock
	private SearchSortUtils searchSortUtils;
	
	@Before
	public void setup() {
		mockMvc=MockMvcBuilders.standaloneSetup(discussionBoardApiImpl).build();
	}
	
//	@Ignore
	@Test
	// Required parameter is not met or found causes Bad Request Error.
	public void test_400_bad_request() throws Exception {
		Long employeeId = 12440l;
		Long companyId = 267l;
		byte[] data = "Hello".getBytes();
		String type = "single";
		String topicId = "123";

		List<DiscussionTopicCommentResponseForm> topicCommentsForms = new ArrayList<>();
		DiscussionTopicCommentResponseForm discussionTopicCommentResponseForm = new DiscussionTopicCommentResponseForm();
		discussionTopicCommentResponseForm.setStatus(true);
		discussionTopicCommentResponseForm.setTopicName("ABCD");
		topicCommentsForms.add(discussionTopicCommentResponseForm);

		String templateName = "E:/WORKSPACE/PayAsiaClaimDev/.metadata/.plugins/org.eclipse.wst.server.core/tmp0/"
				+ "wtpwebapps/payasia-web/resources/HRISReportTemplate/DiscussionBoardTopicComments.xlsx";

		when(servletContext.getRealPath("/resources/HRISReportTemplate/DiscussionBoardTopicComments.xlsx")).thenReturn(templateName);

		when(discussionBoardLogic.downloadTopicCommentsCommon(topicId, companyId, employeeId, type)).thenReturn(topicCommentsForms);

		when(discussionBoardLogic.generateDiscussionTopicCommentsExcel(topicCommentsForms, templateName)).thenReturn(data);

		String fileName;

		if ((StringUtils.equalsIgnoreCase(type, "multiple"))) {
			fileName = PayAsiaConstants.PAYASIA_DISCUSSION_BOARD_MULTIPLE_TOPIC_EXCEL_FILE_NAME;
		} else {
			fileName = topicCommentsForms.get(0).getTopicName();
		}
		fileName = fileName + ".xlsx";

		String url = getAppUrl() + "admin/discussionboard/download-comments";

		mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).param("type", "single").param("abc", "123"))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());

											
	}
	
//	@Ignore
	@Test
	// NUMBER FORMAT EXCEPTION or INDEX OUT OF BOUNDS EXCEPTION causes Internal Server Error.
	public void test_500_internal_server_error() throws Exception {
		Long employeeId = 12440l;
		Long companyId = 267l;
		byte[] data = "Hello".getBytes();
		String type = "single";
		String topicId = "123lk";

		List<DiscussionTopicCommentResponseForm> topicCommentsForms = new ArrayList<>();
		DiscussionTopicCommentResponseForm discussionTopicCommentResponseForm = new DiscussionTopicCommentResponseForm();
		discussionTopicCommentResponseForm.setStatus(true);
		discussionTopicCommentResponseForm.setTopicName("ABCD");
		topicCommentsForms.add(discussionTopicCommentResponseForm);

		String templateName = "E:/WORKSPACE/PayAsiaClaimDev/.metadata/.plugins/org.eclipse.wst.server.core/tmp0/"
				+ "wtpwebapps/payasia-web/resources/HRISReportTemplate/DiscussionBoardTopicComments.xlsx";

		when(servletContext.getRealPath("/resources/HRISReportTemplate/DiscussionBoardTopicComments.xlsx")).thenReturn(templateName);

		when(discussionBoardLogic.downloadTopicCommentsCommon("123", companyId, employeeId, type)).thenReturn(topicCommentsForms);

		when(discussionBoardLogic.generateDiscussionTopicCommentsExcel(topicCommentsForms, templateName)).thenReturn(data);

		String fileName;

		if ((StringUtils.equalsIgnoreCase(type, "multiple"))) {
			fileName = PayAsiaConstants.PAYASIA_DISCUSSION_BOARD_MULTIPLE_TOPIC_EXCEL_FILE_NAME;
		} else {
			fileName = topicCommentsForms.get(0).getTopicName();
		}
		fileName = fileName + ".xlsx";
		ResponseEntity<?> res = null;
		try{
			res = discussionBoardApiImpl.downloadComments(type, topicId);
		}
		catch(IndexOutOfBoundsException ex){
			System.out.println("###### DONE ######");
			return ;
		}
		Assert.assertEquals("500", res.getStatusCode());
	}
		
//	@Ignore
	@Test
	public void test_find_discussion_board_topics() throws Exception{
		Long employeeId = 12440l;
		Long companyId = 267l;
		
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(1);
		pageDTO.setPageSize(2);
		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(null);
		sortDTO.setOrderType("asc");
		
		SearchSortDTO searchSortDTO = new SearchSortDTO();
		searchSortDTO.setPageRequest(pageDTO);
		searchSortDTO.setSortCondition(sortDTO);
		
		
		SearchParam searchParamObj = new SearchParam();
		searchParamObj.setPage(1);
		searchParamObj.setRows(2);
		searchParamObj.getSortField();
		searchParamObj.setSortOrder("1");
		Filters[] filters = new Filters[0];
		searchParamObj.setFilters(filters);
		MultiSortMeta[] multiSortMetas = new MultiSortMeta[0];
		searchParamObj.setMultiSortMeta(multiSortMetas);
		
		String searchCondition = null, searchValue = null, fromDate = null, toDate = null;
		int year = 2018;
		
		DiscussionBoardFormResponse discussionBoardFormResponse = new DiscussionBoardFormResponse();
		DiscussionBoardForm discussionBoardForm = new DiscussionBoardForm();
		discussionBoardForm.setTopicName("Discussion Topic Name");
		discussionBoardForm.setTopicDescription("Discussion Description");
		discussionBoardFormResponse.setDiscussionBoardForm(discussionBoardForm);
		discussionBoardFormResponse.setPage(1);

		when(searchSortUtils.getSearchSortObject(searchParamObj)).thenReturn(searchSortDTO);
		
		when(discussionBoardLogic.findDiscussionBoardTopics(employeeId, companyId, searchSortDTO.getPageRequest(), searchSortDTO.getSortCondition(), 
				searchCondition, searchValue, fromDate, toDate, year)).thenReturn(discussionBoardFormResponse);
		
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(discussionBoardFormResponse, jsonConfig);
		
		ResponseEntity<?> res = discussionBoardApiImpl.findDiscussionBoardTopics(searchParamObj, year);
		Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
		Assert.assertEquals(jsonObject.toString(), res.getBody());
		
	}
	
//    @Ignore
	@SuppressWarnings("unchecked")
	@Test
	public void test_download_comments() throws Exception {
		Long employeeId = 12440l;
		Long companyId = 267l;
		byte[] data = "Hello".getBytes();
		String type = "single";
		String topicId = "123";
		
		List<DiscussionTopicCommentResponseForm> topicCommentsForms = new ArrayList<>();
		DiscussionTopicCommentResponseForm discussionTopicCommentResponseForm = new DiscussionTopicCommentResponseForm();
		discussionTopicCommentResponseForm.setStatus(true);
		discussionTopicCommentResponseForm.setTopicName("ABCD");
		topicCommentsForms.add(discussionTopicCommentResponseForm);
		
		String templateName = "E:/WORKSPACE/PayAsiaClaimDev/.metadata/.plugins/org.eclipse.wst.server.core/tmp0/"
				+ "wtpwebapps/payasia-web/resources/HRISReportTemplate/DiscussionBoardTopicComments.xlsx";
		
		when(servletContext.getRealPath("/resources/HRISReportTemplate/DiscussionBoardTopicComments.xlsx")).thenReturn(templateName);
		
		when( discussionBoardLogic.downloadTopicCommentsCommon(topicId, companyId, employeeId, type)).thenReturn(topicCommentsForms);
		
		when( discussionBoardLogic.generateDiscussionTopicCommentsExcel(topicCommentsForms, templateName)).thenReturn(data);
		
		ResponseEntity<Map<String, Object>> res = (ResponseEntity<Map<String, Object>>) discussionBoardApiImpl.downloadComments(type, topicId);
		Assert.assertEquals("ABCD.xlsx", res.getBody().get("fileName"));
		Assert.assertTrue(res.hasBody());
		Assert.assertEquals(data, res.getBody().get("data"));
		Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
	}
	
//	@Ignore
	@SuppressWarnings("unchecked")
	@Test
	public void test_get_discussion_board_default_email() throws Exception{
		Long employeeId = 12440l;
		Long companyId = 267l;

		String emailIds = "abc@gmail.com";
				
		when(discussionBoardLogic.getDisscusBoardDefaultEmail(companyId, employeeId)).thenReturn(emailIds);
		
		ResponseEntity<Map<String, String>> res = (ResponseEntity<Map<String, String>>) discussionBoardApiImpl.getDiscussionBoardDefaultEmail();
		Assert.assertEquals("abc@gmail.com", res.getBody().get("defaultEmail"));
		Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
	}
	
//	@Ignore
	@SuppressWarnings("unchecked")
	@Test
	public void test_get_discussion_board_default_emailCC() throws Exception{
		Long employeeId = 12440l;
		Long companyId = 267l;

		String emailIds = "xyz@gmail.com";
				
		when(discussionBoardLogic.getDisscusBoardDefaultEmailCC(companyId, employeeId)).thenReturn(emailIds);
		
		ResponseEntity<Map<String, String>> res = (ResponseEntity<Map<String, String>>) discussionBoardApiImpl.getDiscussionBoardDefaultEmailCC();
		Assert.assertEquals("xyz@gmail.com", res.getBody().get("defaultEmailCC"));
		Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
	}
	
//	@Ignore
	@SuppressWarnings("unchecked")
	@Test
	public void test_get_enable_visibility() throws Exception{
		Long companyId = 267l;
		String enableVisibility = "true";
				
		when(discussionBoardLogic.getEnableVisibility(companyId)).thenReturn(enableVisibility);
		
		ResponseEntity<Map<String, String>> res = (ResponseEntity<Map<String, String>>) discussionBoardApiImpl.getEnableVisibility();
		Assert.assertEquals("true", res.getBody().get("enableVisibility"));
		Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
	}
	
	// Doubt and incomplete
//	@Ignore
	@Test
	public void test_get_discussion_topic() throws Exception {
		DiscussionTopicCommentForm discussionTopicReplyForm1 = new DiscussionTopicCommentForm();
		DiscussionBoardForm discussionBoardForm = new DiscussionBoardForm();
		discussionBoardForm.setTopicStatus("Nice");
		discussionBoardForm.setTopicDescription("Wow");

		String url = getAppUrl() + "admin/discussionboard/discussion-topic";

		mockMvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.discussionTopicReplyForm.sendMail").value(discussionTopicReplyForm1.isSendMail()));

											
	}
	
//	@Ignore
	@Test
	public void test_add_discussion_board_topic() throws Exception{
		Long employeeId = 12440l;
		Long companyId = 267l;
		
		DiscussionBoardForm discussionBoardForm = new DiscussionBoardForm();
		discussionBoardForm.setTopicName("TopicName");
		discussionBoardForm.setTopicDescription("Description");
		discussionBoardForm.setEmailIds("abc@gmail.com");
		discussionBoardForm.setEmailCcIds("shailendra.tomar@mind-infotech.com");
		
		String employeeIdsList = "";
		String addTopicValue = "11";
		
		when(discussionBoardLogic.addDiscussionBoardTopic(discussionBoardForm, companyId, employeeId, employeeIdsList)).thenReturn(addTopicValue);

		ResponseEntity<?> res = discussionBoardApiImpl.addDiscussionBoardTopic(discussionBoardForm, employeeIdsList);
		Assert.assertEquals(new ApiMessageHandler(Type.SUCCESS, HttpStatus.OK.toString(), messageSource.getMessage("payasia.discussion.board.topic.added.successfully", new Object[]{}, UserContext.getLocale())), res.getBody());
		Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
		
	}
	
//	@Ignore
	@Test
	public void test_get_topic_comment_list() throws Exception{
		Long employeeId = 12440l;
		Long companyId = 267l;
		
		Long topicId = 12l;
		
		DiscussionTopicCommentResponseForm discussionTopicForm = new DiscussionTopicCommentResponseForm();
		discussionTopicForm.setTopicName("Test Name");
		discussionTopicForm.setStatus(true);
		
		when(discussionBoardLogic.getTopicCommentList(topicId, companyId, employeeId)).thenReturn(discussionTopicForm);
		
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(discussionTopicForm, jsonConfig);
		
		ResponseEntity<?> res = discussionBoardApiImpl.getTopicCommentList(topicId);
		Assert.assertEquals(jsonObject.toString(), res.getBody());
		Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
	}

//	@Ignore
	@Test
	public void test_get_forum_topic_data_by_id() throws Exception{
		Long companyId = 267l;
		Long topicId = 12l;

		DiscussionBoardFormResponse discussionBoardFormResponse = new DiscussionBoardFormResponse();
		DiscussionBoardForm discussionBoardForm = new DiscussionBoardForm();
		discussionBoardForm.setTopicDescription("Description");
		discussionBoardForm.setStatusChangedBy("Changed");
		discussionBoardFormResponse.setDiscussionBoardForm(discussionBoardForm);
		
		when(discussionBoardLogic.getForumTopicDataById(topicId, companyId)).thenReturn(discussionBoardFormResponse);
		
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(discussionBoardFormResponse.getDiscussionBoardForm(), jsonConfig);
		
		ResponseEntity<?> res = discussionBoardApiImpl.getForumTopicDataById(topicId);
		
		Assert.assertEquals(jsonObject.toString(), res.getBody());
		Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
	}
	
//	@Ignore
	@Test
	public void test_get_discussion_board_topic_status_history() throws Exception{
		Long employeeId = 12440l;
		Long companyId = 267l;
		
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(1);
		pageDTO.setPageSize(2);
		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(null);
		sortDTO.setOrderType("asc");
		
		SearchParam searchParamObj = new SearchParam();
		searchParamObj.setPage(1);
		searchParamObj.setRows(2);
		searchParamObj.getSortField();
		searchParamObj.setSortOrder("1");
		Filters[] filters = new Filters[0];
		searchParamObj.setFilters(filters);
		MultiSortMeta[] multiSortMetas = new MultiSortMeta[0];
		searchParamObj.setMultiSortMeta(multiSortMetas);
		
		Long topicId = 12l;

		DiscussionBoardFormResponse discussionBoardResponse = new DiscussionBoardFormResponse();
		DiscussionBoardForm discussionBoardForm = new DiscussionBoardForm();
		discussionBoardForm.setTopicDescription("Description status");
		discussionBoardForm.setStatusChangedBy("Changed status");
		discussionBoardResponse.setDiscussionBoardForm(discussionBoardForm);
		
		SearchSortDTO searchSortDTO = new SearchSortDTO();
		searchSortDTO.setPageRequest(pageDTO);
		searchSortDTO.setSortCondition(sortDTO);
		
		when(searchSortUtils.getSearchSortObject(searchParamObj)).thenReturn(searchSortDTO);
		
		when(discussionBoardLogic.getDiscussionBoardTopicStatusHistory(employeeId, companyId, pageDTO,
				sortDTO, topicId)).thenReturn(discussionBoardResponse);
		
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(discussionBoardResponse, jsonConfig);
		
		ResponseEntity<?> res = discussionBoardApiImpl.getDiscussionBoardTopicStatusHistory(searchParamObj, topicId);
		
		Assert.assertEquals(jsonObject.toString(), res.getBody());
		Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
	}

//	@Ignore
	@Test
	public void test_update_discussion_board_topic() throws Exception{
		Long employeeId = 12440l;
		Long companyId = 267l;
		
		String employeeIdsList = "";
		
		DiscussionBoardForm discussionBoardForm = new DiscussionBoardForm();
		discussionBoardForm.setTopicDescription("Description updated");
		discussionBoardForm.setStatusChangedBy("Change updated");
		
		String updatedStatus = "true";
		
		when(discussionBoardLogic.updateDiscussionBoardTopic(companyId, employeeId, discussionBoardForm, employeeIdsList)).thenReturn(updatedStatus);
		ResponseEntity<?> res = discussionBoardApiImpl.updateDiscussionBoardTopic(discussionBoardForm, employeeIdsList);
		
		Assert.assertEquals(new ApiMessageHandler(Type.SUCCESS, HttpStatus.OK.toString(), messageSource.getMessage("payasia.discussion.board.topic.updated.successfully", new Object[]{}, UserContext.getLocale())), res.getBody());
		// Assert.assertEquals(res.getBody(), apiMessageHandler);
		Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
	}
	
//	@Ignore
	@Test
	public void test_add_topic_comment() throws Exception{
		Long employeeId = 12440l;
		Long companyId = 267l;
		
		DiscussionTopicCommentForm discussionTopicReplyForm = new DiscussionTopicCommentForm();
		discussionTopicReplyForm.setTopicEmployeeIds("2");
		discussionTopicReplyForm.setTopicId(23l);
		discussionTopicReplyForm.setCommentStatus("comment status");
		discussionTopicReplyForm.setEncodedComment("comment status");
		discussionTopicReplyForm.setSendMail(true);
		discussionTopicReplyForm.setEmail("abc");
		discussionTopicReplyForm.setEmailCc("xyz");
		
		JSONObject jObject = new JSONObject();
		jObject.put("topicEmployeeIds", "2");
		jObject.put("topicId", 23l);
		jObject.put("commentStatus", "comment status");
		jObject.put("sendMail", true);
		jObject.put("email", "abc");
		jObject.put("emailCc", "xyz");
		jObject.put("encodedComment", "comment status");
		
	    String jsonText = jObject.toString();
	    
		String msg = "TopicCommentId#"+"23";
		
		when(discussionBoardLogic.addTopicComment(discussionTopicReplyForm, discussionTopicReplyForm.getTopicEmployeeIds(), companyId, employeeId)).thenReturn(msg);
		ResponseEntity<?> res = discussionBoardApiImpl.addTopicComment(jsonText, null);
		
										  
		Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
		Assert.assertEquals(new ApiMessageHandler(Type.SUCCESS, HttpStatus.OK.toString(), messageSource.getMessage("payasia.discussion.board.topic.comment.added.successfully", new Object[]{}, UserContext.getLocale())), res.getBody());
		
	}
	
//	@Ignore
	@Test
	public void get_topic_comment_attachment_list() throws Exception {
		Long employeeId = 12440l;
		Long companyId = 267l;
		Long topicCommentId = 123l;
		
		DiscussionTopicCommentResponseForm discussionTopicCommentResponseForm = new DiscussionTopicCommentResponseForm();
		discussionTopicCommentResponseForm.setStatus(true);
		discussionTopicCommentResponseForm.setTopicName("ABCD");
		
		when(discussionBoardLogic.getTopicCommentAttachmentList(topicCommentId, companyId, employeeId)).thenReturn(discussionTopicCommentResponseForm);
		
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(discussionTopicCommentResponseForm, jsonConfig);
		
		ResponseEntity<?> res = discussionBoardApiImpl.getTopicCommentAttachmentList(topicCommentId);
		
		Assert.assertEquals(jsonObject.toString(), res.getBody());
		Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
	}
	
//	@Ignore
	@Test
	public void test_update_topic_comment() throws Exception{
		Long employeeId = 12440l;
		Long companyId = 267l;
		
		DiscussionTopicCommentForm discussionTopicReplyForm = new DiscussionTopicCommentForm();
		discussionTopicReplyForm.setTopicEmployeeIds("2");
		discussionTopicReplyForm.setTopicId(44l);
		discussionTopicReplyForm.setCommentStatus("comment status updated");
		discussionTopicReplyForm.setTopicCommentId(55l);
		discussionTopicReplyForm.setSendMail(true);
		discussionTopicReplyForm.setEmail("abc");
		discussionTopicReplyForm.setEmailCc("xyz");
		discussionTopicReplyForm.setEncodedComment("comment status");
		discussionTopicReplyForm.setTopicEmployeeIds("2");
		discussionTopicReplyForm.setCommentStatus("comment status updated");
		
		JSONObject jObject = new JSONObject();
		jObject.put("topicId", 44l);
		jObject.put("topicCommentId", 55l);
		jObject.put("sendMail", true);
		jObject.put("email", "abc");
		jObject.put("emailCc", "xyz");
		jObject.put("encodedComment", "comment status");
		jObject.put("topicEmployeeIds", "2");
		jObject.put("commentStatus", "comment status updated");
		
	    String jsonText = jObject.toString();
		
		String msg = "SUCCESS";
		
		when(discussionBoardLogic.updateTopicComment(discussionTopicReplyForm, companyId, employeeId, discussionTopicReplyForm.getTopicEmployeeIds())).thenReturn(msg);
		ResponseEntity<?> res = discussionBoardApiImpl.updateTopicComment(jsonText, null);
		
		Assert.assertEquals(new ApiMessageHandler(Type.SUCCESS, HttpStatus.OK.toString(), messageSource.getMessage("payasia.discussion.board.topic.comment.updated.successfully", new Object[]{}, UserContext.getLocale())), res.getBody());
		Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
	}
	
//	@Ignore
	@Test
	public void test_get_topic_comment_data() throws Exception{
		Long companyId = 267l;
		Long topicCommentId = 55l;
		
		DiscussionTopicCommentForm discussionTopicReplyForm = new DiscussionTopicCommentForm();
		discussionTopicReplyForm.setTopicId(Long.parseLong("56"));
		discussionTopicReplyForm.setCommentStatus("topic comment status");
		
		when(discussionBoardLogic.getTopicCommentData(topicCommentId,companyId)).thenReturn(discussionTopicReplyForm);
		
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(discussionTopicReplyForm, jsonConfig);

		ResponseEntity<?> res = discussionBoardApiImpl.getTopicCommentData(topicCommentId);
		
		Assert.assertEquals(jsonObject.toString(), res.getBody());
		Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
	}
	
//	@Ignore
	@Test
	public void test_delete_topic_comment() throws Exception {
		Long companyId = 267l;
		Long topicCommentId = 123l;
		
		String deleteStatus = "true";
		
		when(discussionBoardLogic.deleteTopicComment(topicCommentId, companyId)).thenReturn(deleteStatus);
		
		ResponseEntity<?> res = discussionBoardApiImpl.deleteTopicComment(topicCommentId);
		
		Assert.assertEquals(new ApiMessageHandler(Type.SUCCESS, HttpStatus.OK.toString(), messageSource.getMessage("payasia.discussion.board.topic.comment.deleted.successfully", new Object[]{}, UserContext.getLocale())), res.getBody());
		Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
	}

//	@Ignore
	@Test
	public void test_delete_discussion_board_topic() throws Exception {
		Long companyId = 267l;
		Long topicId = 123l;
		
		String deleteStatus = "true";
		
		when(discussionBoardLogic.deleteDiscussionBoardTopic(companyId, topicId)).thenReturn(deleteStatus);
		
		ResponseEntity<?> res = discussionBoardApiImpl.deleteDiscussionBoardTopic(topicId);
		
		Assert.assertEquals(new ApiMessageHandler(Type.SUCCESS, HttpStatus.OK.toString(), messageSource.getMessage("payasia.discussion.board.topic.deleted.successfully", new Object[]{}, UserContext.getLocale())), res.getBody());
		Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
	}
	
//	@Ignore
	@Test
	public void test_search_employee() throws Exception{
		Long employeeId = 12440l;
		Long companyId = 267l;
		
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(1);
		pageDTO.setPageSize(2);
		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(null);
		sortDTO.setOrderType("asc");
		
		SearchSortDTO searchSortDTO = new SearchSortDTO();
		searchSortDTO.setPageRequest(pageDTO);
		searchSortDTO.setSortCondition(sortDTO);
		
		
		SearchParam searchParamObj = new SearchParam();
		searchParamObj.setPage(1);
		searchParamObj.setRows(2);
		searchParamObj.getSortField();
		searchParamObj.setSortOrder("1");
		Filters[] filters = new Filters[0];
		searchParamObj.setFilters(filters);
		MultiSortMeta[] multiSortMetas = new MultiSortMeta[0];
		searchParamObj.setMultiSortMeta(multiSortMetas);
		
		String searchCondition = null, searchValue = null;
		
		DiscussionBoardFormResponse discussionBoardFormResponse = new DiscussionBoardFormResponse();
		DiscussionBoardForm discussionBoardForm = new DiscussionBoardForm();
		discussionBoardForm.setTopicName("Discussion Topic Name");
		discussionBoardForm.setTopicDescription("Discussion Description");
		discussionBoardFormResponse.setDiscussionBoardForm(discussionBoardForm);
		discussionBoardFormResponse.setPage(1);

		String metaData = "";
		Boolean includeResignedEmployees = false;
		
		when(searchSortUtils.getSearchSortObject(searchParamObj)).thenReturn(searchSortDTO);
		
		when(discussionBoardLogic.searchEmployee(null, searchSortDTO.getPageRequest(),
				searchSortDTO.getSortCondition(), searchCondition, searchValue, companyId, employeeId, metaData, includeResignedEmployees, "SearchEmployee", "")).
		thenReturn(discussionBoardFormResponse);
		
		ResponseEntity<?> res = discussionBoardApiImpl.searchEmployee(searchParamObj, null ,metaData, includeResignedEmployees);
		Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
		Assert.assertEquals(discussionBoardFormResponse, res.getBody());
  
	}
	
}
