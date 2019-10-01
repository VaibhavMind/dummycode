package com.payasia.test.admin.document;

import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.payasia.api.admin.impl.CurrencyDefinitionApiImpl;
import com.payasia.api.hris.model.SearchParam;
import com.payasia.common.form.CurrencyDefinitionForm;
import com.payasia.common.form.CurrencyDefinitionResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.logic.CurrencyDefinitionLogic;
import com.payasia.logic.EmployeeDocumentLogic;
import com.payasia.test.root.TDDConfig;

public class CurrencyDefinitionApiTDD extends TDDConfig{
	
	
	@InjectMocks
	private CurrencyDefinitionApiImpl currencyDefinitionApiImpl;
	
	@Mock
	private CurrencyDefinitionLogic currencyDefinitionLogic;
	
	@Before
	public void setup() {
		mockMvc = MockMvcBuilders.standaloneSetup(currencyDefinitionApiImpl).build();
	}
	

	@Test
	public void testViewCurrencyDefinition() {

		Long companyId = 267l;
		Long employeeId = 12440l;
		String currencyDate = "";
		Long currencyId = 582l;
		SearchParam searchParam = new SearchParam();
		searchParam.setPage(1);
		searchParam.setRows(10);
		searchParam.setSortField("");
		searchParam.setSortOrder("1");
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(searchParam.getPage());
		pageDTO.setPageSize(searchParam.getRows());
		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(searchParam.getSortField());
		sortDTO.setOrderType(searchParam.getSortOrder());

		CurrencyDefinitionForm currencyDefinitionForm1 = new CurrencyDefinitionForm();
		currencyDefinitionForm1.setCurrency("INR | India Rupee");
		currencyDefinitionForm1.setEndDate("01 Aug 2019");
		currencyDefinitionForm1.setCompanyExchangeRateId(2979l);

		CurrencyDefinitionForm currencyDefinitionForm2 = new CurrencyDefinitionForm();
		currencyDefinitionForm2.setCurrency("INR | India Rupee");
		currencyDefinitionForm2.setEndDate("01 Aug 2019");
		currencyDefinitionForm2.setCompanyExchangeRateId(2979l);

		List<CurrencyDefinitionForm> currencyDefinitionFormList = new ArrayList<>();
		currencyDefinitionFormList.add(currencyDefinitionForm1);
		currencyDefinitionFormList.add(currencyDefinitionForm2);
		CurrencyDefinitionResponse response = new CurrencyDefinitionResponse();
		response.setRows(currencyDefinitionFormList);

		when(currencyDefinitionLogic.viewCurrencyDefinition(companyId, currencyId, currencyDate, pageDTO, sortDTO))
				.thenReturn(response);
		ResponseEntity<?> res = currencyDefinitionApiImpl.viewCurrencyDefinition(searchParam, currencyId, currencyDate);
		Assert.assertEquals(response, res.getBody());
		Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
	}
	
	@Test
	public void testGetBaseCurrency() {
		Long companyId = 267l;
		String str = "abc";
		when(currencyDefinitionLogic.getBaseCurrency(companyId)).thenReturn(str);
		ResponseEntity<?> res = currencyDefinitionApiImpl.showBaseCurrency();
		Assert.assertEquals(str, res.getBody());
		Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
	}
	
	@Test
	public void testAddCurrencyDefinition() {

		String currencyDefinitionStatus = "success";
		Long companyId = 267l;
		CurrencyDefinitionForm currencyDefinitionForm = new CurrencyDefinitionForm();
		currencyDefinitionForm.setBaseCurrency("INR | India Rupee");
		currencyDefinitionForm.setCurrencyId(582l);
		currencyDefinitionForm.setStartDate("17 Sep 2019");
		currencyDefinitionForm.setEndDate("20 Sep 2019");
		currencyDefinitionForm.setExchangeRate(new BigDecimal(600));
		currencyDefinitionForm.setCompanyExchangeRateId(2989l);
		when(currencyDefinitionLogic.addCurrencyDefinition(currencyDefinitionForm, companyId)).thenReturn(currencyDefinitionStatus);
		
		ResponseEntity<?> res = currencyDefinitionApiImpl.addCurrencyDefinition(currencyDefinitionForm);
		Assert.assertEquals(currencyDefinitionStatus, res.getBody());
		Assert.assertEquals(HttpStatus.OK, res.getStatusCode());

	}
	
	
	@Test
	public void testEditCurrencyDefinition() {

		String currencyDefinitionStatus = "success";
		Long companyId = 267l;
		CurrencyDefinitionForm currencyDefinitionForm = new CurrencyDefinitionForm();
		currencyDefinitionForm.setBaseCurrency("INR | India Rupee");
		currencyDefinitionForm.setCurrencyId(582l);
		currencyDefinitionForm.setStartDate("17 Sep 2019");
		currencyDefinitionForm.setEndDate("20 Sep 2019");
		currencyDefinitionForm.setExchangeRate(new BigDecimal(600));
		currencyDefinitionForm.setCompanyExchangeRateId(2989l);
		when(currencyDefinitionLogic.updateCurrencyDefinition(currencyDefinitionForm, companyId))
				.thenReturn(currencyDefinitionStatus);
		ResponseEntity<?> res = currencyDefinitionApiImpl.editCurrencyDefinition(currencyDefinitionForm);
		Assert.assertEquals(currencyDefinitionStatus, res.getBody());
		Assert.assertEquals(HttpStatus.OK, res.getStatusCode());

	}
	
	@Test
	public void testRemoveCurrencyDefinition() {
		
		String currencyDefinitionStatus ="success";
		Long companyExchangeRateId = 2987l;
		Long companyId =267l;
		when(currencyDefinitionLogic.deleteCurrencyDefinition(companyId, companyExchangeRateId)).thenReturn(currencyDefinitionStatus);
		ResponseEntity<?> res = currencyDefinitionApiImpl.removeCurrencyDefinition(companyExchangeRateId);
		Assert.assertEquals(currencyDefinitionStatus, res.getBody());
		Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
		
	}
	
	@Test
	public void testImportCompanyExchangeRate() throws Exception {
		
		CurrencyDefinitionForm currencyDefinitionForm = new CurrencyDefinitionForm();
		Long companyId = 267l;
		File file = new File("E://ImportCompanyExchangeRate.xlsx");
		FileInputStream input = new FileInputStream(file);
		DiskFileItem fileItem = new DiskFileItem("file", "text/plain", false, file.getName(), (int) file.length(),
				file.getParentFile());
		fileItem.getOutputStream();
		CommonsMultipartFile commonsMultipartFile = new CommonsMultipartFile(fileItem);
		currencyDefinitionForm.setFileUpload(commonsMultipartFile);
		when(currencyDefinitionLogic.importCompanyExchangeRate(currencyDefinitionForm, companyId))
				.thenReturn(currencyDefinitionForm);
		ResponseEntity<?> res = currencyDefinitionApiImpl.importCompanyExchangeRate(commonsMultipartFile);
		Assert.assertEquals(currencyDefinitionForm, res.getBody());
		Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
		
	}
}
