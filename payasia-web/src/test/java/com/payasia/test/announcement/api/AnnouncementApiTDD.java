package com.payasia.test.announcement.api;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payasia.api.common.hris.impl.CommonApiImpl;
import com.payasia.api.dashboard.impl.AnnouncementApiImpl;
import com.payasia.api.hris.model.Filters;
import com.payasia.api.hris.model.GlobalFilter;
import com.payasia.api.hris.model.MultiSortMeta;
import com.payasia.api.hris.model.SearchParam;
import com.payasia.api.utils.ApiMessageHandler;
import com.payasia.api.utils.ApiMessageHandler.Type;
import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.SearchSortDTO;
import com.payasia.common.form.AnouncementForm;
import com.payasia.common.form.AnouncementFormResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.logic.AnouncementLogic;
import com.payasia.test.root.TDDConfig;

@RestController
public class AnnouncementApiTDD extends TDDConfig {

	@InjectMocks
	AnnouncementApiImpl announcementApiImpl;

	@InjectMocks
	CommonApiImpl commonApiImpl;

	@Mock
	AnouncementLogic anouncementLogic;

	@Mock
	private ServletContext servletContext;

	@Before
	public void setup() {
		mockMvc = MockMvcBuilders.standaloneSetup(announcementApiImpl).build();
	}

	Long companyId = 267l;
	AnouncementForm announcementForm = new AnouncementForm();

	@Test
	public void testAddAnnouncement() {

		announcementForm.setAnnouncementId(1l);
		announcementForm.setTitle("Announcement 1");
		announcementForm.setDescription("Descriptioon");
		announcementForm.setPostDateTime("2019-07-19 02:00");
		announcementForm.setRemoveDateTime("2019-07-22 02:00");
		announcementForm.setScope("G");
		when(anouncementLogic.postAnouncement(announcementForm, companyId)).thenReturn(PayAsiaConstants.PAYASIA_SUCCESS);
		ResponseEntity<?> res = announcementApiImpl.addAnnouncements(announcementForm);
		Assert.assertEquals(
				new ApiMessageHandler(Type.SUCCESS, HttpStatus.OK.toString(), messageSource.getMessage(
						"payasia.announcement.added.successfully", new Object[] {}, UserContext.getLocale())),
				res.getBody());
		Assert.assertEquals(HttpStatus.OK, res.getStatusCode());

	}

	@Test
	public void testAddAnnouncement404NotFound() {

		announcementForm.setAnnouncementId(1l);
		announcementForm.setDescription("Description");
		announcementForm.setScope("G");
		when(anouncementLogic.postAnouncement(announcementForm, companyId)).thenReturn(PayAsiaConstants.PAYASIA_ERROR);
		ResponseEntity<?> res = announcementApiImpl.addAnnouncements(announcementForm);
		Assert.assertEquals(
				new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource
						.getMessage("payasia.announcement.added.error", new Object[] {}, UserContext.getLocale())),
				res.getBody());
		Assert.assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());

	}

	@Test
	public void testAddAnnouncement400BadRequest() throws Exception {
		String url = getAppUrl() + "admin/announcement/add-announcement";
		mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).param("announcementForm", "1"))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());

	}

	@Test
	public void testAddAnnouncement405MethodNotAllowed() throws Exception {

		String url = getAppUrl() + "admin/announcement/add-announcement";
		announcementForm.setAnnouncementId(1l);
		announcementForm.setTitle("Announcement 1");
		announcementForm.setDescription("Descriptioon");
		announcementForm.setPostDateTime("2019-07-19 02:00");
		announcementForm.setRemoveDateTime("2019-07-22 02:00");
		announcementForm.setScope("G");
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		mapper.writeValueAsBytes(announcementForm);
		mockMvc.perform(put(url).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.content(mapper.writeValueAsBytes(announcementForm)))
				.andExpect(MockMvcResultMatchers.status().isMethodNotAllowed());

	}

	@Test
	public void testDeleteAnnouncement() {

		Long announcementId = 20l;
		when(anouncementLogic.deleteAnouncement(announcementId, companyId)).thenReturn(PayAsiaConstants.PAYASIA_SUCCESS);
		ResponseEntity<?> res = announcementApiImpl.deleteAnnouncement(announcementId);
		Assert.assertEquals(
				new ApiMessageHandler(Type.SUCCESS, HttpStatus.OK.toString(), messageSource.getMessage(
						"payasia.announcement.deleted.successfully", new Object[] {}, UserContext.getLocale())),
				res.getBody());
		Assert.assertEquals(HttpStatus.OK, res.getStatusCode());

	}

	@Test
	public void testDeleteAnnouncement404NotFound() {

		Long announcementId = 2l;
		when(anouncementLogic.deleteAnouncement(announcementId, companyId)).thenReturn(PayAsiaConstants.PAYASIA_ERROR);
		ResponseEntity<?> res = announcementApiImpl.deleteAnnouncement(announcementId);
		Assert.assertEquals(
				new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource
						.getMessage("payasia.announcement.deleted.error", new Object[] {}, UserContext.getLocale())),
				res.getBody());
		Assert.assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());

	}

	@Test
	public void testDeleteAnnouncement400BadRequest() throws Exception {
		String url = getAppUrl() + "/admin/announcement/delete-announcement";
		mockMvc.perform(delete(url).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).param("announcementID", "1"))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());

	}

	@Test
	public void testUpdateAnnouncement() {

		announcementForm.setAnnouncementId(1l);
		announcementForm.setTitle("Announcement 1");
		announcementForm.setDescription("Descriptioon");
		announcementForm.setPostDateTime("2019-07-19 02:00");
		announcementForm.setRemoveDateTime("2019-07-22 02:00");
		announcementForm.setScope("G");
		when(anouncementLogic.updateAnouncement(announcementForm, companyId)).thenReturn(PayAsiaConstants.PAYASIA_SUCCESS);
		ResponseEntity<?> res = announcementApiImpl.updateAnnouncement(announcementForm);
		Assert.assertEquals(
				new ApiMessageHandler(Type.SUCCESS, HttpStatus.OK.toString(), messageSource.getMessage(
						"payasia.announcement.updated.successfully", new Object[] {}, UserContext.getLocale())),
				res.getBody());
		Assert.assertEquals(HttpStatus.OK, res.getStatusCode());

	}

	@Test
	public void testUpdateAnnouncement404NotFound() {

		announcementForm.setAnnouncementId(1l);
		announcementForm.setTitle("Announcement 1");
		announcementForm.setDescription("Descriptioon");
		announcementForm.setPostDateTime("2019-07-19 02:00");
		announcementForm.setRemoveDateTime("2019-07-22 02:00");
		announcementForm.setScope("G");
		when(anouncementLogic.updateAnouncement(announcementForm, companyId)).thenReturn(PayAsiaConstants.PAYASIA_ERROR);
		ResponseEntity<?> res = announcementApiImpl.updateAnnouncement(announcementForm);
		Assert.assertEquals(
				new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource
						.getMessage("payasia.announcement.updated.error", new Object[] {}, UserContext.getLocale())),
				res.getBody());
		Assert.assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());

	}

	@Test
	public void testUpdateAnnouncement400BadRequest() throws Exception {
		String url = getAppUrl() + "admin/announcement/update-announcement";
		mockMvc.perform(put(url).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).param("announcementForm", "1"))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());

	}

	@Test
	public void testUpdateAnnouncement405MethodNotAllowed() throws Exception {

		String url = getAppUrl() + "admin/announcement/update-announcement";
		announcementForm.setAnnouncementId(1l);
		announcementForm.setTitle("Announcement 1");
		announcementForm.setDescription("Descriptioon");
		announcementForm.setPostDateTime("2019-07-19 02:00");
		announcementForm.setRemoveDateTime("2019-07-22 02:00");
		announcementForm.setScope("G");
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		mapper.writeValueAsBytes(announcementForm);
		mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.content(mapper.writeValueAsBytes(announcementForm)))
				.andExpect(MockMvcResultMatchers.status().isMethodNotAllowed());

	}

	@Test
	public void testgetAnouncementForEdit() {

		Long announcementId = 20l;
		announcementForm.setAnnouncementId(1l);
		announcementForm.setTitle("Announcement 1");
		announcementForm.setDescription("Descriptioon");
		announcementForm.setPostDateTime("2019-07-19 02:00");
		announcementForm.setRemoveDateTime("2019-07-22 02:00");
		announcementForm.setScope("G");
		when(anouncementLogic.getAnouncementForEdit(companyId, announcementId)).thenReturn(announcementForm);
		ResponseEntity<?> res = announcementApiImpl.getAnouncement(announcementId);
		Assert.assertEquals(new ResponseEntity<>(announcementForm, HttpStatus.OK), res);
		Assert.assertEquals(HttpStatus.OK, res.getStatusCode());

	}

	@Test
	public void testgetAnouncementForEdit404NotFound() {
		Long announcementId = 20l;
		announcementForm.setAnnouncementId(1l);
		announcementForm.setTitle("Announcement 1");
		announcementForm.setDescription("Descriptioon");
		announcementForm.setPostDateTime("2019-07-19 02:00");
		announcementForm.setRemoveDateTime("2019-07-22 02:00");
		announcementForm.setScope("G");
		when(anouncementLogic.getAnouncementForEdit(companyId, announcementId)).thenReturn(null);
		ResponseEntity<?> res = announcementApiImpl.getAnouncement(announcementId);
		Assert.assertEquals(
				new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage(
						"payasia.announcement.get.data.for.update.error", new Object[] {}, UserContext.getLocale())),
				res.getBody());
		Assert.assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());

	}

	@Test
	public void testgetAnouncementForEdit400BadRequest() throws Exception {

		String url = getAppUrl() + "admin/announcement/get-announcement";
		mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).param("announcementId", "20l"))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());

	}

	@Test
	@Ignore
	public void testgetAnouncementList() {

		Long announcementId = 1l;
		SearchParam searchParam = new SearchParam();
		MultiSortMeta[] multiSortMeta = new MultiSortMeta[7];
		MultiSortMeta msm = new MultiSortMeta();
		msm.setField("");
		msm.setOrder("");
		multiSortMeta[0] = msm;
		GlobalFilter globalFilter = new GlobalFilter();
		globalFilter.setField("");
		globalFilter.setValue("");
		Filters[] filters = new Filters[7];
		Filters fl = new Filters();
		fl.setField("");
		fl.setValue("");
		filters[0] = fl;
		searchParam.setFilters(filters);
		searchParam.setGlobalFilter(globalFilter);
		searchParam.setMultiSortMeta(multiSortMeta);
		searchParam.setPage(1);
		searchParam.setRows(1);
		searchParam.setSortField("");
		searchParam.setSortOrder("");
		SearchSortDTO searchSortDTO = new SearchSortDTO();
		PageRequest pr = new PageRequest();
		pr.setPageNumber(1);
		pr.setPageSize(1);
		SortCondition sc = new SortCondition();
		sc.setColumnName("");
		sc.setOrderType("1");
		searchSortDTO.setPageRequest(pr);
		searchSortDTO.setSortCondition(sc);
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(searchParam.getPage());
		pageDTO.setPageSize(searchParam.getRows());
		AnouncementFormResponse announcementFormResponse = new AnouncementFormResponse();
		announcementFormResponse.setPage(1);
		announcementFormResponse.setRecords(1);
		List<AnouncementForm> announcementFormList = new ArrayList<AnouncementForm>();
		announcementForm.setAnnouncementId(announcementId);
		announcementForm.setTitle("Announcement 1");
		announcementForm.setDescription("Descriptioon");
		announcementForm.setPostDateTime("2019-07-19 02:00");
		announcementForm.setRemoveDateTime("2019-07-22 02:00");
		announcementForm.setScope("G");
		announcementFormList.add(announcementForm);
		announcementFormResponse.setRows(announcementFormList);
		announcementFormResponse.setTotal(10);
		when(anouncementLogic.getAnnouncementList(companyId, searchSortDTO.getPageRequest(), searchSortDTO.getSortCondition(),null))
				.thenReturn(announcementFormResponse);
		ResponseEntity<?> res = commonApiImpl.getAnouncementList(searchParam);
		Assert.assertEquals(new ResponseEntity<>(announcementFormResponse, HttpStatus.OK), res.getBody());
		Assert.assertEquals(HttpStatus.OK, res.getStatusCode());

	}

}
