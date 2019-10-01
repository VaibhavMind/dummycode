package com.payasia.test.client.admin;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.util.ArrayList;
import java.util.List;

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

import com.payasia.api.client.admin.impl.SwitchCompanyApiImpl;
import com.payasia.api.hris.model.Filters;
import com.payasia.api.hris.model.MultiSortMeta;
import com.payasia.api.hris.model.SearchParam;
import com.payasia.api.utils.ApiMessageHandler;
import com.payasia.api.utils.SearchSortUtils;
import com.payasia.api.utils.ApiMessageHandler.Type;
import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.SearchSortDTO;
import com.payasia.common.form.CompanyForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.form.SwitchCompanyForm;
import com.payasia.common.form.SwitchCompanyResponse;
import com.payasia.logic.SwitchCompanyLogic;
import com.payasia.test.root.TDDConfig;

public class SwitchCompanyApiTDD extends TDDConfig {

	@InjectMocks
	private SwitchCompanyApiImpl switchCompanyApiImpl;
	
	@Mock
	private SwitchCompanyLogic switchCompanyLogic;
	
	@Mock
	private SearchSortUtils searchSortUtils;
	
	@Before
	public void setup() {
		mockMvc = MockMvcBuilders.standaloneSetup(switchCompanyApiImpl).build();
	}
	
//	@Ignore
	@Test
	public void test_get_switch_company_list_400_bad_request() throws Exception{
		Long employeeId = 12440l;
		
		Boolean includeInactiveCompany = true;
		
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
		
		String searchCondition = "";
		String searchValue = "";
		
		SwitchCompanyResponse switchCompanyResponse = new SwitchCompanyResponse();
		List<SwitchCompanyForm> switchCompanyFormList = new ArrayList<>();
		SwitchCompanyForm switchCompanyForm = new SwitchCompanyForm();
		switchCompanyForm.setCompanyCode("abc");
		switchCompanyForm.setCompanyId(22l);
		switchCompanyFormList.add(switchCompanyForm);

		switchCompanyResponse.setPage(1);
		switchCompanyResponse.setTotal(1);
		switchCompanyResponse.setRecords(1);
		switchCompanyResponse.setSwitchCompanyFormList(switchCompanyFormList);

		when(searchSortUtils.getSearchSortObject(searchParamObj)).thenReturn(searchSortDTO);
		
		when(switchCompanyLogic.getSwitchCompanyList(pageDTO, sortDTO, employeeId, searchCondition, searchValue, includeInactiveCompany)).thenReturn(switchCompanyResponse);
		
		String url = getAppUrl() + "admin/switch/company/company-list";
		mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON_VALUE).content(searchParamObj.toString()).param("includeInactiveCompany", "1"))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}
	
//	@Ignore
	@Test
	public void test_get_switch_company_list_405_method_not_allowed() throws Exception{
		Long employeeId = 12440l;
		Boolean includeInactiveCompany = true;
		
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
		
		String searchCondition = "";
		String searchValue = "";
		
		SwitchCompanyResponse switchCompanyResponse = null;
		
		when(searchSortUtils.getSearchSortObject(searchParamObj)).thenReturn(searchSortDTO);
		
		when(switchCompanyLogic.getSwitchCompanyList(pageDTO, sortDTO, employeeId, searchCondition, searchValue, includeInactiveCompany)).thenReturn(switchCompanyResponse);
		
		ResponseEntity<?> res = switchCompanyApiImpl.getSwitchCompanyList(searchParamObj, includeInactiveCompany);
		Assert.assertEquals(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), 
				messageSource.getMessage("data.info.nodata", new Object[] {}, UserContext.getLocale())), res.getBody());
		Assert.assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
	}
	
//	@Ignore
	@Test
	public void test_get_switch_company_list() throws Exception{
		Long employeeId = 12440l;
		Boolean includeInactiveCompany = true;
		
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
		
		String searchCondition = "";
		String searchValue = "";
		
		SwitchCompanyResponse switchCompanyResponse = new SwitchCompanyResponse();
		List<SwitchCompanyForm> switchCompanyFormList = new ArrayList<>();
		SwitchCompanyForm switchCompanyForm = new SwitchCompanyForm();
		switchCompanyForm.setCompanyCode("abc");;
		switchCompanyForm.setCompanyId(22l);;
		switchCompanyFormList.add(switchCompanyForm);

		switchCompanyResponse.setPage(1);
		switchCompanyResponse.setTotal(1);
		switchCompanyResponse.setRecords(1);
		switchCompanyResponse.setSwitchCompanyFormList(switchCompanyFormList);

		when(searchSortUtils.getSearchSortObject(searchParamObj)).thenReturn(searchSortDTO);
		
		when(switchCompanyLogic.getSwitchCompanyList(pageDTO, sortDTO, employeeId, searchCondition, searchValue, includeInactiveCompany)).thenReturn(switchCompanyResponse);
		
		ResponseEntity<?> res = switchCompanyApiImpl.getSwitchCompanyList(searchParamObj, includeInactiveCompany);
		Assert.assertEquals(switchCompanyResponse, res.getBody());
		Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
	}
	
//	@Ignore
	@Test
	public void test_switch_company_400_bad_request() throws Exception{
		
		String url = getAppUrl() + "admin/switch/company/company-switch";
		mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON_VALUE).param("check", "1"))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}
	
//	@Ignore
	@Test
	public void test_switch_company_405_method_not_allowed() throws Exception{

		Long companyId = 267l;
		String companyCode = "";
		CompanyForm companyForm = null;
		
		when(switchCompanyLogic.getCompany(companyId)).thenReturn(companyForm);
		
		ResponseEntity<?> res = switchCompanyApiImpl.switchCompany(companyId, companyCode);
		Assert.assertEquals(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), 
				messageSource.getMessage("payasia.switch.company.not.assigned", new Object[] {}, UserContext.getLocale())), res.getBody());
		Assert.assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
	}
	
}
