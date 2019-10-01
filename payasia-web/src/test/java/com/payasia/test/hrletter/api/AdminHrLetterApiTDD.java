package com.payasia.test.hrletter.api;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

import com.payasia.api.client.admin.impl.AdminHrLetterApiImpl;
import com.payasia.api.hris.model.Filters;
import com.payasia.api.hris.model.MultiSortMeta;
import com.payasia.api.hris.model.SearchParam;
import com.payasia.api.utils.ApiMessageHandler;
import com.payasia.api.utils.ApiMessageHandler.Type;
import com.payasia.api.utils.SearchSortUtils;
import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.SearchSortDTO;
import com.payasia.common.form.AssignLeaveSchemeForm;
import com.payasia.common.form.EmployeeFilterListForm;
import com.payasia.common.form.HRLetterForm;
import com.payasia.common.form.HRLetterResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.logic.AssignLeaveSchemeLogic;
import com.payasia.logic.CompanyDocumentCenterLogic;
import com.payasia.logic.HRLetterLogic;
import com.payasia.test.root.TDDConfig;

import net.sf.json.JSONObject;

public class AdminHrLetterApiTDD extends TDDConfig {

	@InjectMocks
	private AdminHrLetterApiImpl adminHrLetterApiImpl;
	
	@Mock
	private HRLetterLogic hrLetterLogic;
	
	@Mock
	private SearchSortUtils searchSortUtils;
	
	@Mock
	private AssignLeaveSchemeLogic assignLeaveSchemeLogic;
	
	@Mock
	private CompanyDocumentCenterLogic companyDocumentCenterLogic;
	
	@Before
	public void setup() {
		mockMvc = MockMvcBuilders.standaloneSetup(adminHrLetterApiImpl).build();
	}
	
//	@Ignore
	@Test
	public void test_search_admin_hrLetters_400_bad_request() throws Exception{
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
		
		String searchCondition = "", searchValue = "";
		
		HRLetterResponse hrLetterResponse = new HRLetterResponse();
		List<HRLetterForm> hrLetterFormList = new ArrayList<>();
		HRLetterForm hrLetterForm = new HRLetterForm();
		hrLetterForm.setBody("abc");
		hrLetterForm.setLetterId(11);
		hrLetterFormList.add(hrLetterForm);

		hrLetterResponse.setPage(1);
		hrLetterResponse.setTotal(1);
		hrLetterResponse.setRecords(1);
		hrLetterResponse.setRows(hrLetterFormList);
		hrLetterResponse.setSearchEmployeeList(null);

		when(searchSortUtils.getSearchSortObject(searchParamObj)).thenReturn(searchSortDTO);
		
		when(hrLetterLogic.searchHRLetter(companyId, searchCondition, searchValue, pageDTO, sortDTO)).thenReturn(hrLetterResponse);
		
		String url = getAppUrl() + "admin/hris/hrletter/search";
		mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON_VALUE).content(searchParamObj.toString()))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}
	
//	@Ignore
	@Test
	public void test_search_admin_hrLetters() throws Exception{
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
		
		String searchCondition = "", searchValue = "";
		
		HRLetterResponse hrLetterResponse = new HRLetterResponse();
		List<HRLetterForm> hrLetterFormList = new ArrayList<>();
		HRLetterForm hrLetterForm = new HRLetterForm();
		hrLetterForm.setBody("abc");
		hrLetterForm.setLetterId(11);
		hrLetterFormList.add(hrLetterForm);

		hrLetterResponse.setPage(1);
		hrLetterResponse.setTotal(1);
		hrLetterResponse.setRecords(1);
		hrLetterResponse.setRows(hrLetterFormList);
		hrLetterResponse.setSearchEmployeeList(null);

		when(searchSortUtils.getSearchSortObject(searchParamObj)).thenReturn(searchSortDTO);
		
		when(hrLetterLogic.searchHRLetter(companyId, searchCondition, searchValue, pageDTO, sortDTO)).thenReturn(hrLetterResponse);
		
		ResponseEntity<?> res = adminHrLetterApiImpl.searchAdminHrLetters(searchParamObj);
		Assert.assertEquals(hrLetterResponse, res.getBody());
		Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
	}
	
//	@Ignore
	@Test
	public void test_save_hrLetter() {
		Long companyId = 267l;
		
		HRLetterForm hrLetterForm = new HRLetterForm();
		hrLetterForm.setLetterName("abc");
		hrLetterForm.setLetterDescription("desc");
		hrLetterForm.setBody("body");
		hrLetterForm.setActive(true);
		
		when(hrLetterLogic.saveHRLetter(hrLetterForm, companyId)).thenReturn(PayAsiaConstants.NOTAVAILABLE);
		
		ResponseEntity<?> res = adminHrLetterApiImpl.saveHrLetter(hrLetterForm);
		Assert.assertEquals(new ApiMessageHandler(Type.SUCCESS, HttpStatus.OK.toString(), messageSource.getMessage("payasia.hr.letters.hr.letter.successfully.saved", new Object[]{}, UserContext.getLocale())), res.getBody());
		Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
	}
	
//	@Ignore
	@Test
	public void test_save_hrLetter_400_bad_request() throws Exception{
		Long companyId = 267l;
		
		HRLetterForm hrLetterForm = new HRLetterForm();
		hrLetterForm.setLetterName("abc");
		hrLetterForm.setLetterDescription("desc");
		hrLetterForm.setBody("body");
		hrLetterForm.setActive(true);
		
		when(hrLetterLogic.saveHRLetter(hrLetterForm, companyId)).thenReturn(PayAsiaConstants.NOTAVAILABLE);
		
		String url = getAppUrl() + "admin/hris/hrletter/add";
		mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON_VALUE).content(hrLetterForm.toString()))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}
	
//	@Ignore
	@Test
	public void test_get_hrLetter_details() {
		Long companyId = 267l;
		long letterId = 11;

		HRLetterForm hrLetterForm = new HRLetterForm();
		hrLetterForm.setLetterName("Test_Name");
		hrLetterForm.setLetterDescription("Letter_Description");
		hrLetterForm.setBody("Letter_Body");
		hrLetterForm.setActive(true);
		hrLetterForm.setLetterId(11);
		
		when(hrLetterLogic.getHRLetter(letterId, companyId)).thenReturn(hrLetterForm);
		
		ResponseEntity<?> res = adminHrLetterApiImpl.getHrLetterDetails(letterId);
		Assert.assertEquals(hrLetterForm, res.getBody());
		Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
	}
	
//	@Ignore
	@Test
	public void test_get_hrLetter_details_400_bad_request() throws Exception{
		Long companyId = 267l;
		long letterId = 11;

		HRLetterForm hrLetterForm = new HRLetterForm();
		hrLetterForm.setLetterName("Test_Name");
		hrLetterForm.setLetterDescription("Letter_Description");
		hrLetterForm.setBody("Letter_Body");
		hrLetterForm.setActive(true);
		hrLetterForm.setLetterId(11);
		
		when(hrLetterLogic.getHRLetter(letterId, companyId)).thenReturn(hrLetterForm);
		
		String url = getAppUrl() + "admin/hris/hrletter/details";
		mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON_VALUE).param("announcementForm", "11"))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}
	
//	@Ignore
	@Test
	public void test_update_hrLetter() {
		Long companyId = 267l;
		long letterId = 12;
		
		HRLetterForm hrLetterForm = new HRLetterForm();
		hrLetterForm.setLetterName("xyz");
		hrLetterForm.setLetterDescription("desc");
		hrLetterForm.setBody("body");
		hrLetterForm.setActive(true);
		hrLetterForm.setLetterId(letterId);
		
		when(hrLetterLogic.updateHRLetter(hrLetterForm, companyId)).thenReturn(PayAsiaConstants.NOTAVAILABLE);
		
		ResponseEntity<?> res = adminHrLetterApiImpl.updateHrLetter(hrLetterForm);
		Assert.assertEquals(new ApiMessageHandler(Type.SUCCESS, HttpStatus.OK.toString(), messageSource.getMessage("payasia.hr.letters.hr.letter.successfully.update", new Object[]{}, UserContext.getLocale())), res.getBody());
		Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
	}
	
//	@Ignore
	@Test
	public void test_update_hrLetter_400_bad_request() throws Exception{
		Long companyId = 267l;
		long letterId = 12;
		
		HRLetterForm hrLetterForm = new HRLetterForm();
		hrLetterForm.setLetterName("xyz");
		hrLetterForm.setLetterDescription("desc");
		hrLetterForm.setBody("body");
		hrLetterForm.setActive(true);
		hrLetterForm.setLetterId(letterId);
		
		when(hrLetterLogic.updateHRLetter(hrLetterForm, companyId)).thenReturn(PayAsiaConstants.NOTAVAILABLE);
		
		String url = getAppUrl() + "admin/hris/hrletter/edit";
		mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON_VALUE).content(hrLetterForm.toString()))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}
	
//	@Ignore
	@SuppressWarnings("unchecked")
	@Test
	public void test_get_employeeIds() {
		Long companyId = 267l;
		Long employeeId = 12440l;
		String searchString = "de";
		
		List<AssignLeaveSchemeForm> assignLeaveSchemeFormList = new ArrayList<>();
		AssignLeaveSchemeForm assignLeaveSchemeForm = new AssignLeaveSchemeForm();
		assignLeaveSchemeForm.setEmployeeId(1l);
		assignLeaveSchemeForm.setEmployeeName("abc");
		assignLeaveSchemeForm.setEmployeeNumber("demo");
		assignLeaveSchemeForm.setStatus("Completed");
		assignLeaveSchemeFormList.add(assignLeaveSchemeForm);
		
		when(assignLeaveSchemeLogic.getEmployeeId(companyId, searchString, employeeId)).thenReturn(assignLeaveSchemeFormList);
		
		ResponseEntity<Map<String, Object>> res = (ResponseEntity<Map<String, Object>>) adminHrLetterApiImpl.getEmployeeIds(searchString);
		Assert.assertEquals(assignLeaveSchemeFormList, res.getBody().get("employeeIdList"));
		Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
	}
	
//	@Ignore
	@Test
	public void test_get_employeeIds_400_bad_request() throws Exception{
		Long companyId = 267l;
		Long employeeId = 12440l;
		String searchString = "de";
		
		List<AssignLeaveSchemeForm> assignLeaveSchemeFormList = new ArrayList<>();
		AssignLeaveSchemeForm assignLeaveSchemeForm = new AssignLeaveSchemeForm();
		assignLeaveSchemeForm.setEmployeeId(1l);
		assignLeaveSchemeForm.setEmployeeName("abc");
		assignLeaveSchemeForm.setEmployeeNumber("demo");
		assignLeaveSchemeForm.setStatus("Completed");
		assignLeaveSchemeFormList.add(assignLeaveSchemeForm);
		
		when(assignLeaveSchemeLogic.getEmployeeId(companyId, searchString, employeeId)).thenReturn(assignLeaveSchemeFormList);
		
		String url = getAppUrl() + "admin/hris/hrletter/employees";
		mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON_VALUE).content(searchString))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}
	
//	@Ignore
	@Test
	public void test_preview_hrLetter() {
		Long companyId = 267l;
		long letterId = 12;
		long employeeId = 12441;
		Long loggedInEmployeeId = 12440l;
		boolean isAdmin = true;
		String convertedPreviewMessage = "Nicely completed";
		String isSaveHrLetterInDocumentCenter = "true";
		
		JSONObject json = new JSONObject();
		json.put("letterId", letterId);
		json.put("employeeId", employeeId);
		
		HRLetterForm hrLetterForm = new HRLetterForm();
		hrLetterForm.setLetterName("xyz");
		hrLetterForm.setLetterDescription("desc");
		hrLetterForm.setBody(convertedPreviewMessage);
		hrLetterForm.setActive(true);
		hrLetterForm.setLetterId(letterId);
		hrLetterForm.setSaveInDocumentCenter(true);
		
		when(hrLetterLogic.getHRLetter(letterId, companyId)).thenReturn(hrLetterForm);
		
		when(hrLetterLogic.previewHRLetterBodyText(companyId, letterId, employeeId, loggedInEmployeeId, null, null, isAdmin)).thenReturn(convertedPreviewMessage);
		
		when(hrLetterLogic.isSaveHrLetterInDocumentCenter(companyId)).thenReturn(isSaveHrLetterInDocumentCenter);
		
		ResponseEntity<?> res = adminHrLetterApiImpl.previewHrLetter(json.toString());
		Assert.assertEquals(hrLetterForm, res.getBody());
		Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
	}
	
//	@Ignore
	@Test
	public void test_preview_hrLetter_400_bad_request() throws Exception{
		Long companyId = 267l;
		long letterId = 12;
		long employeeId = 12441;
		Long loggedInEmployeeId = 12440l;
		boolean isAdmin = true;
		String convertedPreviewMessage = "Nicely completed";
		String isSaveHrLetterInDocumentCenter = "true";
		
		JSONObject json = new JSONObject();
		json.put("letterId", letterId);
		json.put("employeeId", employeeId);
		
		HRLetterForm hrLetterForm = new HRLetterForm();
		hrLetterForm.setLetterName("xyz");
		hrLetterForm.setLetterDescription("desc");
		hrLetterForm.setBody(convertedPreviewMessage);
		hrLetterForm.setActive(true);
		hrLetterForm.setLetterId(letterId);
		hrLetterForm.setSaveInDocumentCenter(true);
		
		when(hrLetterLogic.getHRLetter(letterId, companyId)).thenReturn(hrLetterForm);
		
		when(hrLetterLogic.previewHRLetterBodyText(companyId, letterId, employeeId, loggedInEmployeeId, null, null, isAdmin)).thenReturn(convertedPreviewMessage);
		
		when(hrLetterLogic.isSaveHrLetterInDocumentCenter(companyId)).thenReturn(isSaveHrLetterInDocumentCenter);
		
		String url = getAppUrl() + "admin/hris/hrletter/preview";
		mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON_VALUE).content(""))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}
	
//	@Ignore
	@Test
	public void test_send_hrLetter() {
		Long companyId = 267l;
		long letterId = 12;
		long employeeId = 12441;
		Long loggedInEmployeeId = 12440l;
		String hrLetterBodyText = "Nicely done";
		String ccEmails = "email";
		boolean saveInDocCenterCheck = true;
		String status = "payasia.hr.letters.hrletter.send.successfully";
		
		JSONObject json = new JSONObject();
		json.put("letterId", letterId);
		json.put("employeeId", employeeId);
		json.put("hrLetterBodyText", hrLetterBodyText);
		json.put("ccEmails", ccEmails);
		json.put("saveInDocCenterCheck", saveInDocCenterCheck);
		
		when(hrLetterLogic.sendHrLetterWithPDF(companyId, letterId, employeeId, loggedInEmployeeId, hrLetterBodyText, ccEmails, saveInDocCenterCheck)).thenReturn(status);
		
		ResponseEntity<?> res = adminHrLetterApiImpl.sendHrLetter(json.toString());
		Assert.assertEquals(new ApiMessageHandler(Type.SUCCESS, HttpStatus.OK.toString(), messageSource.getMessage(status, new Object[] {}, UserContext.getLocale())), res.getBody());
		Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
	}
	
//	@Ignore
	@Test
	public void test_send_hrLetter_400_bad_request() throws Exception{
		Long companyId = 267l;
		long letterId = 12;
		long employeeId = 12441;
		Long loggedInEmployeeId = 12440l;
		String hrLetterBodyText = "Nicely done";
		String ccEmails = "email";
		boolean saveInDocCenterCheck = true;
		String status = "payasia.hr.letters.hrletter.send.successfully";
		
		JSONObject json = new JSONObject();
		json.put("letterId", "11");
		json.put("employeeId", employeeId);
		json.put("hrLetterBodyText", hrLetterBodyText);
		json.put("ccEmails", ccEmails);
		json.put("saveInDocCenterCheck", saveInDocCenterCheck);
		
		when(hrLetterLogic.sendHrLetterWithPDF(companyId, letterId, employeeId, loggedInEmployeeId, hrLetterBodyText, ccEmails, saveInDocCenterCheck)).thenReturn(status);
		
		String url = getAppUrl() + "admin/hris/hrletter/send";
		mockMvc.perform(post(url).contentType(MediaType.APPLICATION_ATOM_XML).content(""))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}
	
//	@Ignore
	@Test
	public void test_delete_hrLetter() {
		Long companyId = 267l;
		long letterId = 11;

		String status = "success";
		
		when(hrLetterLogic.deleteHRLetter(letterId, companyId)).thenReturn(status);
		
		ResponseEntity<?> res = adminHrLetterApiImpl.deleteHrLetter(letterId);
		Assert.assertEquals(new ApiMessageHandler(Type.SUCCESS, HttpStatus.OK.toString(), messageSource.getMessage("payasia.hr.letters.hr.letter.successfully.deleted", new Object[] {}, UserContext.getLocale())), res.getBody());
		Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
	}

//	@Ignore
	@Test
	public void test_delete_hrLetter_400_bad_request() throws Exception{
		Long companyId = 267l;
		long letterId = 11;

		String status = "success";
		
		when(hrLetterLogic.deleteHRLetter(letterId, companyId)).thenReturn(status);
		
		String url = getAppUrl() + "admin/hris/hrletter/delete";
		mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON_VALUE).param("letterId","11f"))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}
	
//	@Ignore
	@SuppressWarnings("unchecked")
	@Test
	public void test_get_edit_employee_filterList() {
		Long companyId = 267l;
		long letterId = 11;

		List<EmployeeFilterListForm> filterList = new ArrayList<>();
		EmployeeFilterListForm employeeFilterListForm = new EmployeeFilterListForm();
		employeeFilterListForm.setDataDictionaryId(12l);
		employeeFilterListForm.setDataDictionaryName("Data Test");
		filterList.add(employeeFilterListForm);
		
		when(hrLetterLogic.getEditEmployeeFilterList(letterId, companyId)).thenReturn(filterList);
		
		ResponseEntity<Map<String, Object>> res = (ResponseEntity<Map<String, Object>>) adminHrLetterApiImpl.getEditEmployeeFilterList(letterId);
		Assert.assertEquals(filterList, res.getBody().get("editEmployeeFilterList"));
		Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
	}
	
//	@Ignore
	@Test
	public void test_get_edit_employee_filterList_400_bad_request() throws Exception{
		Long companyId = 267l;
		long letterId = 11;

		List<EmployeeFilterListForm> filterList = new ArrayList<>();
		EmployeeFilterListForm employeeFilterListForm = new EmployeeFilterListForm();
		employeeFilterListForm.setDataDictionaryId(12l);
		employeeFilterListForm.setDataDictionaryName("Data Test");
		filterList.add(employeeFilterListForm);
		
		when(hrLetterLogic.getEditEmployeeFilterList(letterId, companyId)).thenReturn(filterList);
		
		String url = getAppUrl() + "admin/hris/hrletter/edit-employee-filter-list";
		mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON_VALUE).param("letterId","11f"))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}
	
//	@Ignore
	@SuppressWarnings("unchecked")
	@Test
	public void test_get_employee_filterList() {
		Long companyId = 267l;

		List<EmployeeFilterListForm> filterList = new ArrayList<>();
		EmployeeFilterListForm employeeFilterListForm = new EmployeeFilterListForm();
		employeeFilterListForm.setDataDictionaryId(13l);
		employeeFilterListForm.setDataDictionaryName("Data Testing");
		filterList.add(employeeFilterListForm);
		
		when(companyDocumentCenterLogic.getEmployeeFilterList(companyId)).thenReturn(filterList);
		
		ResponseEntity<Map<String, Object>> res = (ResponseEntity<Map<String, Object>>) adminHrLetterApiImpl.getEmployeeFilterList();
		Assert.assertEquals(filterList, res.getBody().get("employeeFilterList"));
		Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
	}
	
	// TODO: Complete this Test Case
//	@Ignore
//	@Test
	public void test_get_employee_filterList_400_bad_request() throws Exception{
		Long companyId = 267l;

		List<EmployeeFilterListForm> filterList = new ArrayList<>();
		EmployeeFilterListForm employeeFilterListForm = new EmployeeFilterListForm();
		employeeFilterListForm.setDataDictionaryId(13l);
		employeeFilterListForm.setDataDictionaryName("Data Testing");
		filterList.add(employeeFilterListForm);
		
		when(companyDocumentCenterLogic.getEmployeeFilterList(companyId)).thenReturn(filterList);
		
		String url = getAppUrl() + "admin/hris/employee-filter-list";
		mockMvc.perform(post(url))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}
	
}
