package com.payasia.test.documentcenter.api;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
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
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.payasia.api.client.admin.impl.DocumentCenterApiImpl;
import com.payasia.api.hris.model.Filters;
import com.payasia.api.hris.model.GlobalFilter;
import com.payasia.api.hris.model.MultiSortMeta;
import com.payasia.api.hris.model.SearchParam;
import com.payasia.api.utils.ApiMessageHandler;
import com.payasia.api.utils.ApiMessageHandler.Type;
import com.payasia.api.utils.SearchSortUtils;
import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.SearchSortDTO;
import com.payasia.common.form.CompanyDocumentCenterForm;
import com.payasia.common.form.CompanyDocumentCenterResponseForm;
import com.payasia.common.form.EmployeeFilterListForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.logic.AWSS3Logic;
import com.payasia.logic.CompanyDocumentCenterLogic;
import com.payasia.test.root.TDDConfig;

public class DocumentCenterApiTDD extends TDDConfig {
	
	@InjectMocks
	DocumentCenterApiImpl documentCenterApiImpl;
	
	@Mock
	CompanyDocumentCenterLogic companyDocumentCenterLogic;

	@Mock
	private ServletContext servletContext;
	
	@Mock
	private SearchSortUtils searchSortUtils;
	
	@Mock
	private AWSS3Logic awss3LogicImpl;
	
	Long companyId = 267l;
	
	@Before
	public void setup() {
		mockMvc = MockMvcBuilders.standaloneSetup(documentCenterApiImpl).build();
	}
	
	@Test
	public void testDeleteDocument() {

		Long docId = 1206772l;
		when(companyDocumentCenterLogic.deleteCompanyDocument(docId, companyId)).thenReturn(PayAsiaConstants.PAYASIA_SUCCESS);
		ResponseEntity<?> res = documentCenterApiImpl.deleteCompanyDocument(docId);
		Assert.assertEquals(
				new ApiMessageHandler(Type.SUCCESS, HttpStatus.OK.toString(), messageSource.getMessage(
						"payasia.documentcenter.deleted.successfully", new Object[] {}, UserContext.getLocale())),
				res.getBody());
		Assert.assertEquals(HttpStatus.OK, res.getStatusCode());

	}

	@Test
	public void testDeleteDocument404NotFound() {

		Long docId = 2l;
		when(companyDocumentCenterLogic.deleteCompanyDocument(docId, companyId)).thenReturn(PayAsiaConstants.PAYASIA_ERROR);
		ResponseEntity<?> res = documentCenterApiImpl.deleteCompanyDocument(docId);
		Assert.assertEquals(
				new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource
						.getMessage("payasia.documentcenter.deleted.error", new Object[] {}, UserContext.getLocale())),
				res.getBody());
		Assert.assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());

	}

	@Test
	public void testDeleteDocument405MethodNotAllowed() throws Exception {
		String url = getAppUrl() + "/admin/documentcenter/delete-document";
		mockMvc.perform(put(url).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).param("docId", "1"))
				.andExpect(MockMvcResultMatchers.status().isMethodNotAllowed());
	}
	
	@Test
	public void testDeleteDocument400BadRequest() throws Exception {
		String url = getAppUrl() + "admin/documentcenter/delete-document";
		mockMvc.perform(delete(url).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());

	}
	
	@Test
	public void testSearchDocument(){
		Long categoryId=10l;
		String sortOrder="asc";
		int page=1;
		int rows=10;
		String sortField="template";
		Long employeeId=12440l;
		MultiSortMeta[] multiSortMeta = new MultiSortMeta[0];
		GlobalFilter globalFilter = new GlobalFilter();
		Filters[] filters = new Filters[1];
		Filters filter=new Filters();
		filter.setField("year");
		filter.setValue("2019");
		filters[0]=filter;
		SearchParam searchParam = new SearchParam();
		searchParam.setFilters(filters);
		searchParam.setGlobalFilter(globalFilter);
		searchParam.setMultiSortMeta(multiSortMeta);
		searchParam.setPage(page);
		searchParam.setRows(rows);
		searchParam.setSortField(sortField);
		searchParam.setSortOrder(sortOrder);

		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(searchParam.getPage());
		pageDTO.setPageSize(searchParam.getRows());

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(searchParam.getSortField());
		sortDTO.setOrderType(searchParam.getSortOrder());
		
		SearchSortDTO searchSortDTO = new SearchSortDTO();
		searchSortDTO.setPageRequest(pageDTO);
		searchSortDTO.setSortCondition(sortDTO);
		
		CompanyDocumentCenterResponseForm response = new CompanyDocumentCenterResponseForm();
		List<CompanyDocumentCenterForm> formList=new ArrayList<>();
		CompanyDocumentCenterForm centerForm=new CompanyDocumentCenterForm();
		centerForm.setDocId(1l);
		centerForm.setDocName("123.pdf");
		centerForm.setDescription("testing-1");
		centerForm.setUploadDate("2019-11-11 03:00");
		formList.add(centerForm);
		response.setRows(formList);
		
		when(searchSortUtils.getSearchSortObject(searchParam)).thenReturn(searchSortDTO);
		
		when(companyDocumentCenterLogic.searchDocument(searchParam.getFilters()[0].getField(), searchParam.getFilters()[0].getValue(), 
				searchSortDTO.getPageRequest(), searchSortDTO.getSortCondition(), companyId, categoryId, employeeId)).thenReturn(response);
		ResponseEntity<?> res = documentCenterApiImpl.searchDocument(searchParam, categoryId);
		Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
	}

	@Test
	public void testSearchDocument404NotFound(){
		Long categoryId=10l;
		String sortOrder="asc";
		int page=1;
		int rows=10;
		String sortField="template";
		Long employeeId=12440l;
		MultiSortMeta[] multiSortMeta = new MultiSortMeta[0];
		GlobalFilter globalFilter = new GlobalFilter();
		Filters[] filters = new Filters[1];
		Filters filter=new Filters();
		filter.setField("year");
		filter.setValue("2019");
		filters[0]=filter;
		SearchParam searchParam = new SearchParam();
		searchParam.setFilters(filters);
		searchParam.setGlobalFilter(globalFilter);
		searchParam.setMultiSortMeta(multiSortMeta);
		searchParam.setPage(page);
		searchParam.setRows(rows);
		searchParam.setSortField(sortField);
		searchParam.setSortOrder(sortOrder);

		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(searchParam.getPage());
		pageDTO.setPageSize(searchParam.getRows());

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(searchParam.getSortField());
		sortDTO.setOrderType(searchParam.getSortOrder());
		
		SearchSortDTO searchSortDTO = new SearchSortDTO();
		searchSortDTO.setPageRequest(pageDTO);
		searchSortDTO.setSortCondition(sortDTO);
		
		CompanyDocumentCenterResponseForm response = new CompanyDocumentCenterResponseForm();
		List<CompanyDocumentCenterForm> formList=new ArrayList<>();
		response.setRows(formList);
		
		when(searchSortUtils.getSearchSortObject(searchParam)).thenReturn(searchSortDTO);
		
		when(companyDocumentCenterLogic.searchDocument(searchParam.getFilters()[0].getField(), searchParam.getFilters()[0].getValue(), 
				searchSortDTO.getPageRequest(), searchSortDTO.getSortCondition(), companyId, categoryId, employeeId)).thenReturn(response);
		ResponseEntity<?> res = documentCenterApiImpl.searchDocument(searchParam, categoryId);
		Assert.assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
	}
	
	@Test
	public void testSearchDocument400BadRequest() throws Exception {
		String url = getAppUrl() + "/admin/documentcenter/search-document";
		mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());

	}
	
	@Test
	public void testSearchDocument405MethodNotAllowed() throws Exception {
		String url = getAppUrl() + "/admin/documentcenter/search-document";
		mockMvc.perform(put(url).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).param("docId", "1"))
				.andExpect(MockMvcResultMatchers.status().isMethodNotAllowed());
	}
	
	@Ignore
	@Test
	public void testUploadDocument() throws IOException{
		String jsonStr="{\"categoryId\":\"7\",\"description\":\"test\",\"year\":\"2010\"}";
		FileItem fileItem = new DiskFileItem("fileData", "application/pdf",true, "demoemp_201011.zip", 0, new java.io.File(System.getProperty("java.io.tmpdir")));              
		CommonsMultipartFile multipartFile = new CommonsMultipartFile(fileItem);
		CompanyDocumentCenterForm companyDocumentCenterForm=new CompanyDocumentCenterForm();
		companyDocumentCenterForm.setCategoryId(7l);
		companyDocumentCenterForm.setDescription("test");
		companyDocumentCenterForm.setYear("2010");
		companyDocumentCenterForm.setFileData(multipartFile);
		CompanyDocumentCenterForm companyDocumentResponse=new CompanyDocumentCenterForm();
		companyDocumentResponse.setCategoryId(7l);
		companyDocumentResponse.setDescription("test");
		companyDocumentResponse.setYear("2010");
		companyDocumentResponse.setFileData(multipartFile);
		when(companyDocumentCenterLogic.uploadDocument(companyDocumentCenterForm, companyId)).thenReturn(companyDocumentResponse);
		
		ResponseEntity<?> res = documentCenterApiImpl.uploadDocument(jsonStr, multipartFile);
		Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
	}
	
	@Test
	public void testUploadDocument400BadRequest() throws Exception {
		String url = getAppUrl() + "/admin/documentcenter/upload-document";
		mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());

	}
	
	@Test
	public void testEditUploadedData() {
		Long docId = 2l;
		CompanyDocumentCenterForm uploadedDoc=new CompanyDocumentCenterForm();
		when(companyDocumentCenterLogic.getUploadedDoc(docId)).thenReturn(uploadedDoc);
		ResponseEntity<?> res = documentCenterApiImpl.editUploadedData(docId);
		Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
	}
	
	@Test
	public void testEditUploadedData404NotFound() {
		Long docId = 2l;
		when(companyDocumentCenterLogic.getUploadedDoc(docId)).thenReturn(null);
		ResponseEntity<?> res = documentCenterApiImpl.editUploadedData(docId);
		Assert.assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
	}
	
	@Test
	public void testEditUploadedData400BadRequest() throws Exception {
		String url = getAppUrl() + "/admin/documentcenter/edit-uploaded-data";
		mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}
	
	@Test
	public void testEditUploadedData405MethodNotAllowed() throws Exception {
		String url = getAppUrl() + "/admin/documentcenter/edit-uploaded-data";
		mockMvc.perform(delete(url).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).param("docId","1"))
				.andExpect(MockMvcResultMatchers.status().isMethodNotAllowed());
	}
	
	@Test
	public void testDownloadDocuments() throws IOException {
		
		 Long documentID=1206446l;
		 Long employeeId=12440l;
		 InputStream anyInputStream = new ByteArrayInputStream("test data".getBytes());
		 String filePath ="company/267/tax/2018/demoemp/demoemp_SPSClaimTemplate1.pdf";
		 when(companyDocumentCenterLogic.viewDocument(documentID,companyId, employeeId)).thenReturn(filePath);
		 when(awss3LogicImpl.readS3ObjectAsStream(filePath)).thenReturn(anyInputStream);
		 ResponseEntity<?> res = documentCenterApiImpl.downloadDocuments(documentID);
		 Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
	}
	
	@Test
	public void testDownloadDocuments404NotFound() throws IOException {
		
		 Long documentID=1206446l;
		 Long employeeId=12440l;
		 InputStream anyInputStream = new ByteArrayInputStream("test data".getBytes());
		 when(companyDocumentCenterLogic.viewDocument(documentID,companyId, employeeId)).thenReturn(null);
		 when(awss3LogicImpl.readS3ObjectAsStream(null)).thenReturn(anyInputStream);
		 ResponseEntity<?> res = documentCenterApiImpl.downloadDocuments(documentID);
		 Assert.assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
	}
	
	@Test
	public void testDownloadDocuments400BadRequest() throws Exception {
		String url = getAppUrl() + "/admin/documentcenter/download-documents";
		mockMvc.perform(get(url).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}
	
	@Test
	public void testDownloadDocuments405MethodNotAllowed() throws Exception {
		String url = getAppUrl() + "/admin/documentcenter/download-documents";
		mockMvc.perform(delete(url).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(MockMvcResultMatchers.status().isMethodNotAllowed());
	}
	
	@Test
	public void testUpdateDocument() {

		CompanyDocumentCenterForm companyDocumentCenterForm=new CompanyDocumentCenterForm();
		companyDocumentCenterForm.setDocId(7l);
		companyDocumentCenterForm.setDescription("test");
		when(companyDocumentCenterLogic.updateDocument(companyDocumentCenterForm, companyId)).thenReturn(PayAsiaConstants.PAYASIA_SUCCESS);
		ResponseEntity<?> res = documentCenterApiImpl.updateDocument(companyDocumentCenterForm);
		Assert.assertEquals(
				new ApiMessageHandler(Type.SUCCESS, HttpStatus.OK.toString(), messageSource.getMessage(
						"payasia.documentcenter.updated.document.successfully", new Object[] {}, UserContext.getLocale())),
				res.getBody());
		Assert.assertEquals(HttpStatus.OK, res.getStatusCode());

	}
	
	@Test
	public void testUpdateDocument404NotFound() {

		CompanyDocumentCenterForm companyDocumentCenterForm=new CompanyDocumentCenterForm();
		companyDocumentCenterForm.setDocId(7l);
		companyDocumentCenterForm.setDescription("test");
		when(companyDocumentCenterLogic.updateDocument(companyDocumentCenterForm, companyId)).thenReturn("error");
		ResponseEntity<?> res = documentCenterApiImpl.updateDocument(companyDocumentCenterForm);
		Assert.assertEquals(
				new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage(
						"payasia.documentcenter.updated.document.error", new Object[] {}, UserContext.getLocale())),res.getBody());
		Assert.assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());

	}
	
	@Test
	public void testUpdateDocument405MethodNotAllowed() throws Exception {
		String url = getAppUrl() + "/admin/documentcenter/update-document";
		mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(MockMvcResultMatchers.status().isMethodNotAllowed());
	}
	
	@Test
	public void testUpdateDocument400BadRequest() throws Exception {
		String url = getAppUrl() + "/admin/documentcenter/update-document";
		mockMvc.perform(put(url).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());

	}
	
	@Test
	public void testDeleteTaxDocument() {

		Long[] docDetailsIds= {1l,2l};
		when(companyDocumentCenterLogic.deleteTaxDocuments(docDetailsIds, companyId)).thenReturn(PayAsiaConstants.PAYASIA_SUCCESS);
		ResponseEntity<?> res = documentCenterApiImpl.deleteTaxDocument(docDetailsIds);
		Assert.assertEquals(
				new ApiMessageHandler(Type.SUCCESS, HttpStatus.OK.toString(), messageSource.getMessage(
						"payasia.documentcenter.deleted.successfully", new Object[] {}, UserContext.getLocale())),
				res.getBody());
		Assert.assertEquals(HttpStatus.OK, res.getStatusCode());

	}

	@Test
	public void testDeleteTaxDocument404NotFound() {

		Long[] docDetailsIds= {1l,2l};
		when(companyDocumentCenterLogic.deleteTaxDocuments(docDetailsIds, companyId)).thenReturn(PayAsiaConstants.PAYASIA_ERROR);
		ResponseEntity<?> res = documentCenterApiImpl.deleteTaxDocument(docDetailsIds);
		Assert.assertEquals(
				new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource
						.getMessage("payasia.documentcenter.deleted.error", new Object[] {}, UserContext.getLocale())),
				res.getBody());
		Assert.assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());

	}

	@Test
	public void testDeleteTaxDocument405MethodNotAllowed() throws Exception {
		String url = getAppUrl() + "/admin/documentcenter/delete-tax-document";
		mockMvc.perform(put(url).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(MockMvcResultMatchers.status().isMethodNotAllowed());
	}
	
	@Test
	public void testDeleteTaxDocument400BadRequest() throws Exception {
		String url = getAppUrl() + "/admin/documentcenter/delete-tax-document";
		mockMvc.perform(delete(url).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}
	
	@Test
	public void testGetEditEmployeeFilterList() {
		Long documentId=20l;
		CompanyDocumentCenterForm companyDocumentCenterForm=new CompanyDocumentCenterForm();
		companyDocumentCenterForm.setDocId(7l);
		companyDocumentCenterForm.setDescription("test");
		List<EmployeeFilterListForm> filterList=new ArrayList<>();
		CompanyDocumentCenterForm companyDocumentCenterForm1=new CompanyDocumentCenterForm();
		companyDocumentCenterForm1.setDocId(17l);
		EmployeeFilterListForm e=new EmployeeFilterListForm();
		EmployeeFilterListForm e1=new EmployeeFilterListForm();
		filterList.add(e);
		filterList.add(e1);
		when(companyDocumentCenterLogic.getEditEmployeeFilterList(documentId, companyId)).thenReturn(filterList);
		ResponseEntity<?> res = documentCenterApiImpl.getEditEmployeeFilterList(companyDocumentCenterForm1);
		Assert.assertEquals(HttpStatus.OK, res.getStatusCode());

	}
	
	@Test
	public void testGetEditEmployeeFilterList404NotFound() {
		Long documentId=20l;
		CompanyDocumentCenterForm companyDocumentCenterForm1=new CompanyDocumentCenterForm();
		companyDocumentCenterForm1.setDocId(17l);
		when(companyDocumentCenterLogic.getEditEmployeeFilterList(documentId, companyId)).thenReturn(null);
		ResponseEntity<?> res = documentCenterApiImpl.getEditEmployeeFilterList(companyDocumentCenterForm1);
		Assert.assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());

	}
	
	@Test
	public void testGetEditEmployeeFilterList405MethodNotAllowed() throws Exception {
		String url = getAppUrl() + "/admin/documentcenter/edit-employee-filter-list";
		mockMvc.perform(put(url).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(MockMvcResultMatchers.status().isMethodNotAllowed());
	}
	
	@Test
	public void testGetEditEmployeeFilterList400BadRequest() throws Exception {
		String url = getAppUrl() + "/admin/documentcenter/edit-employee-filter-list";
		mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}
	
	@Test
	public void testGetEmployeeFilterList() {
		List<EmployeeFilterListForm> filterList=new ArrayList<>();
		EmployeeFilterListForm e=new EmployeeFilterListForm();
		filterList.add(e);
		when(companyDocumentCenterLogic.getEmployeeFilterList(companyId)).thenReturn(filterList);
		ResponseEntity<?> res = documentCenterApiImpl.getEmployeeFilterList();
		Assert.assertEquals(HttpStatus.OK, res.getStatusCode());

	}
	
	@Test
	public void testGetEmployeeFilterList404NotFound() {
		when(companyDocumentCenterLogic.getEmployeeFilterList(companyId)).thenReturn(null);
		ResponseEntity<?> res = documentCenterApiImpl.getEmployeeFilterList();
		Assert.assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());

	}
	
	@Test
	public void testGetEmployeeFilterList405MethodNotAllowed() throws Exception {
		String url = getAppUrl() + "/admin/documentcenter/employee-filter-list";
		mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(MockMvcResultMatchers.status().isMethodNotAllowed());
	}
	
	@Test
	public void testSearchDocumentEmployeeDocumentCenter(){
		Long categoryId=10l;
		String sortOrder="asc";
		int page=1;
		int rows=10;
		String sortField="template";
		Long employeeId=12440l;
		Filters[] filters = new Filters[1];
		Filters filter=new Filters();
		filter.setField("1");
		filter.setValue("1");
		filters[0]=filter;
		SearchParam searchParam = new SearchParam();
		searchParam.setFilters(filters);
		searchParam.setPage(page);
		searchParam.setRows(rows);
		searchParam.setSortField(sortField);
		searchParam.setSortOrder(sortOrder);

		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(searchParam.getPage());
		pageDTO.setPageSize(searchParam.getRows());

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(searchParam.getSortField());
		sortDTO.setOrderType(searchParam.getSortOrder());
		
		SearchSortDTO searchSortDTO = new SearchSortDTO();
		searchSortDTO.setPageRequest(pageDTO);
		searchSortDTO.setSortCondition(sortDTO);
		
		CompanyDocumentCenterResponseForm response = new CompanyDocumentCenterResponseForm();
		List<CompanyDocumentCenterForm> formList=new ArrayList<>();
		CompanyDocumentCenterForm centerForm=new CompanyDocumentCenterForm();
		
		formList.add(centerForm);
		response.setRows(formList);
		
		
		when(searchSortUtils.getSearchSortObject(searchParam)).thenReturn(searchSortDTO);
		
		when(companyDocumentCenterLogic.searchDocumentEmployeeDocumentCenter(searchParam.getFilters()[0].getField(), searchParam.getFilters()[0].getValue(), 
				searchSortDTO.getPageRequest(), searchSortDTO.getSortCondition(), companyId, categoryId, employeeId)).thenReturn(response);
		ResponseEntity<?> res = documentCenterApiImpl.searchDocumentEmployeeDocumentCenter(searchParam, categoryId);
		Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
	}
	
	@Test
	public void testSearchDocumentEmployeeDocumentCenter404NotFound(){
		Long categoryId=10l;
		String sortOrder="asc";
		int page=1;
		int rows=10;
		String sortField="template";
		Long employeeId=12440l;
		
		Filters[] filters = new Filters[1];
		Filters filter=new Filters();
		filter.setField("year");
		filter.setValue("2019");
		filters[0]=filter;
		SearchParam searchParam = new SearchParam();
		searchParam.setFilters(filters);
	
		searchParam.setPage(page);
		searchParam.setRows(rows);
		searchParam.setSortField(sortField);
		searchParam.setSortOrder(sortOrder);

		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(searchParam.getPage());
		pageDTO.setPageSize(searchParam.getRows());

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(searchParam.getSortField());
		sortDTO.setOrderType(searchParam.getSortOrder());
		
		SearchSortDTO searchSortDTO = new SearchSortDTO();
		searchSortDTO.setPageRequest(pageDTO);
		searchSortDTO.setSortCondition(sortDTO);
		
		CompanyDocumentCenterResponseForm response = new CompanyDocumentCenterResponseForm();
		when(searchSortUtils.getSearchSortObject(searchParam)).thenReturn(searchSortDTO);
		when(companyDocumentCenterLogic.searchDocumentEmployeeDocumentCenter(searchParam.getFilters()[0].getField(), searchParam.getFilters()[0].getValue(), 
				searchSortDTO.getPageRequest(), searchSortDTO.getSortCondition(), companyId, categoryId, employeeId)).thenReturn(response);
		ResponseEntity<?> res = documentCenterApiImpl.searchDocumentEmployeeDocumentCenter(searchParam, categoryId);
		Assert.assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
	}
	
	@Test
	public void testSearchDocumentEmployeeDocumentCenter400BadRequest() throws Exception {
		String url = getAppUrl() + "/admin/documentcenter/search-document-employee";
		mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());

	}
	
	@Test
	public void testSearchDocumentEmployeeDocumentCenter405MethodNotAllowed() throws Exception {
		String url = getAppUrl() + "/admin/documentcenter/search-document-employee";
		mockMvc.perform(put(url).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(MockMvcResultMatchers.status().isMethodNotAllowed());
	}
	
	@Test
	public void testDeleteFilter() {

		Long filterId = 1206772l;
		when(companyDocumentCenterLogic.deleteFilter(filterId)).thenReturn(PayAsiaConstants.PAYASIA_SUCCESS);
		ResponseEntity<?> res = documentCenterApiImpl.deleteFilter(filterId);
		Assert.assertEquals(
				new ApiMessageHandler(Type.SUCCESS, HttpStatus.OK.toString(), messageSource.getMessage(
						"payasia.documentcenter.filter.deleted.successfully", new Object[] {}, UserContext.getLocale())),
				res.getBody());
		Assert.assertEquals(HttpStatus.OK, res.getStatusCode());

	}

	@Test
	public void testDeleteFilter404NotFound() {

		Long filterId = 2l;
		when(companyDocumentCenterLogic.deleteFilter(filterId)).thenReturn(PayAsiaConstants.PAYASIA_ERROR);
		ResponseEntity<?> res = documentCenterApiImpl.deleteFilter(filterId);
		Assert.assertEquals(
				new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource
						.getMessage("payasia.documentcenter.filter.deleted.error", new Object[] {}, UserContext.getLocale())),
				res.getBody());
		Assert.assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());

	}

	@Test
	public void testDeleteFilter405MethodNotAllowed() throws Exception {
		String url = getAppUrl() + "/admin/documentcenter/delete-filter";
		mockMvc.perform(put(url).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(MockMvcResultMatchers.status().isMethodNotAllowed());
	}
	
	@Test
	public void testDeleteFilter400BadRequest() throws Exception {
		String url = getAppUrl() + "admin/documentcenter/delete-filter";
		mockMvc.perform(delete(url).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());

	}
	
	@Test
	public void testSaveEmployeeFilterList() throws UnsupportedEncodingException {

		Long documentId = 1206772l;
		String metaData="testMeta";
		CompanyDocumentCenterForm companyDocumentCenterForm=new CompanyDocumentCenterForm();
		companyDocumentCenterForm.setDocId(documentId);
		companyDocumentCenterForm.setMetaData(metaData);
		when(companyDocumentCenterLogic.saveEmployeeFilterList(metaData, documentId)).thenReturn("ShortList saved Successfully");
		ResponseEntity<?> res = documentCenterApiImpl.saveEmployeeFilterList(companyDocumentCenterForm);
		Assert.assertEquals(HttpStatus.OK, res.getStatusCode());

	}

	@Test
	public void testSaveEmployeeFilterList404NotFound() throws UnsupportedEncodingException {

		Long documentId = 1206772l;
		String metaData="testMeta";
		CompanyDocumentCenterForm companyDocumentCenterForm=new CompanyDocumentCenterForm();
		companyDocumentCenterForm.setDocId(documentId);
		companyDocumentCenterForm.setMetaData(metaData);
		when(companyDocumentCenterLogic.saveEmployeeFilterList(metaData, documentId)).thenReturn("0");
		ResponseEntity<?> res = documentCenterApiImpl.saveEmployeeFilterList(companyDocumentCenterForm);
		Assert.assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());


	}

	@Test
	public void testSaveEmployeeFilterList405MethodNotAllowed() throws Exception {
		String url = getAppUrl() + "/admin/documentcenter/save-employee-filterlist";
		mockMvc.perform(put(url).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(MockMvcResultMatchers.status().isMethodNotAllowed());
	}
	
	@Test
	public void testSaveEmployeeFilterList400BadRequest() throws Exception {
		String url = getAppUrl() + "admin/documentcenter/save-employee-filterlist";
		mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());

	}
	
}
