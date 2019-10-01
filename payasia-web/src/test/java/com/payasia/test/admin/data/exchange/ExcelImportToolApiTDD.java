package com.payasia.test.admin.data.exchange;

import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.RestController;

import com.payasia.api.admin.data.exchange.impl.ExcelImportToolApiImpl;
import com.payasia.api.hris.model.Filters;
import com.payasia.api.hris.model.SearchParam;
import com.payasia.api.utils.SearchSortUtils;
import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.DataImportHistoryDTO;
import com.payasia.common.dto.PayslipFrequencyDTO;
import com.payasia.common.dto.SearchSortDTO;
import com.payasia.common.dto.SectionInfoDTO;
import com.payasia.common.form.DBTableInformationForm;
import com.payasia.common.form.DataImportForm;
import com.payasia.common.form.EmployeeFilterListForm;
import com.payasia.common.form.ExcelImportToolForm;
import com.payasia.common.form.ExcelImportToolFormResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.logic.DataImportLogic;
import com.payasia.logic.ExcelImportToolLogic;
import com.payasia.test.root.TDDConfig;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

@RestController
public class ExcelImportToolApiTDD extends TDDConfig{

	@InjectMocks
	private ExcelImportToolApiImpl excelImportToolApiImpl;
	
	@Mock
	private ExcelImportToolLogic excelImportToolLogic;
	
	@Mock
	DataImportLogic dataImportLogic;
	
	@Mock
	private SearchSortUtils searchSortUtils;
	
	@Before
	public void setup() {
		mockMvc=MockMvcBuilders.standaloneSetup(excelImportToolApiImpl).build();
	}
	
	@Test
	public void test_search_import_template() {
		
		Long companyId = 267l;
		
		SearchParam searchParamObj = new SearchParam();
		searchParamObj.setPage(1);
		searchParamObj.setRows(2);
		searchParamObj.setSortField(null);
		searchParamObj.setSortOrder("asc");
		searchParamObj.setGlobalFilter(null);
		searchParamObj.setMultiSortMeta(null);
		
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(searchParamObj.getPage());
		pageDTO.setPageSize(searchParamObj.getRows());
		
		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(searchParamObj.getSortField());
		sortDTO.setOrderType(searchParamObj.getSortOrder());
		
		SearchSortDTO searchSortDTO = new SearchSortDTO();
		searchSortDTO.setPageRequest(pageDTO);
		searchSortDTO.setSortCondition(sortDTO);
		
			
		Filters[] filters = new Filters[0];
		searchParamObj.setFilters(filters);

		String searchCondition = "", searchValue = "" ;
		
		List<ExcelImportToolForm> excelImportToolFormList = new ArrayList<ExcelImportToolForm>();
		ExcelImportToolFormResponse excelImportToolFormResponse = new ExcelImportToolFormResponse();
		ExcelImportToolForm excelImportToolForm = new ExcelImportToolForm();
		excelImportToolForm.setTemplateName("11Payslip");
		excelImportToolForm.setCategory("Payslip Form");
		excelImportToolForm.setTemplateDesc("Insert New Only");
		excelImportToolFormList.add(excelImportToolForm);
		excelImportToolFormResponse.setExcelImportToolFormList(excelImportToolFormList);
		
		when(searchSortUtils.getSearchSortObject(searchParamObj)).thenReturn(searchSortDTO);
		
		when(excelImportToolLogic.getExistImportTempDef(pageDTO, sortDTO,
				companyId,searchCondition, searchValue)).thenReturn(excelImportToolFormResponse);
		
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(excelImportToolFormResponse, jsonConfig);
		
		ResponseEntity<?> res = excelImportToolApiImpl.findExcelImportTemplate(searchParamObj);
		Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
		Assert.assertEquals(jsonObject.toString(), res.getBody());
		
		
	}

	
	@Test
	public void test_get_data_template() throws Exception{

		Long templateId = 8727231171727550L;
		
		ExcelImportToolForm excelImportToolForm = new ExcelImportToolForm();
		excelImportToolForm.setFormId(42320L);
		when(excelImportToolLogic.getDataForTemplate(templateId)).thenReturn(excelImportToolForm);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(excelImportToolForm, jsonConfig);
		ResponseEntity<?> res = excelImportToolApiImpl.findDataForTemplate(templateId);
		Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
		Assert.assertEquals(jsonObject.toString(), res.getBody());
	}
	
	
	@Test
	public void test_get_existing_mapping() throws Exception{

		Long entityId = 20L;
		Long companyId = 267l;
		ExcelImportToolForm excelImportToolForm = new ExcelImportToolForm();
		DBTableInformationForm dbTableInformationForm = new DBTableInformationForm();
		List<DBTableInformationForm> dbTableInformationFormList = new ArrayList<DBTableInformationForm>();
		dbTableInformationForm.setDataDictionaryId(891);
		dbTableInformationFormList.add(dbTableInformationForm);
		excelImportToolForm.setDbTableInformationFormList(dbTableInformationFormList);
		
		when(excelImportToolLogic.getExistMapping(companyId,entityId, UserContext.getLanguageId())).thenReturn(excelImportToolForm);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(excelImportToolForm, jsonConfig);
		ResponseEntity<?> res = excelImportToolApiImpl.findExistingMapping(entityId);
		Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
		Assert.assertEquals(jsonObject.toString(), res.getBody());
	}
	
	@Test
	public void test_get_existing_child_mapping() throws Exception{
		Long companyId = 267l;
		Long entityId = 20L;
		Long formId = 13l;
		int tablePosition =1;
		
		ExcelImportToolForm excelImportToolForm = new ExcelImportToolForm();
		Set<SectionInfoDTO> sectionList = new HashSet<SectionInfoDTO>();
		SectionInfoDTO basicSectionInfoDTO = new SectionInfoDTO();
		basicSectionInfoDTO.setSectionId(0l);
		basicSectionInfoDTO.setSectionName("Basic Information");
		sectionList.add(basicSectionInfoDTO);
		List<SectionInfoDTO> sectionInfoList = new ArrayList<>(sectionList);
		excelImportToolForm.setSectionList(sectionInfoList);
		
		when(excelImportToolLogic.getExistTableMapping(companyId,entityId,formId,tablePosition , UserContext.getLanguageId())).thenReturn(excelImportToolForm);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(excelImportToolForm, jsonConfig);
		ResponseEntity<?> res = excelImportToolApiImpl.findExistingChildTableMapping(entityId,formId,tablePosition);
		Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
		Assert.assertEquals(jsonObject.toString(), res.getBody());
	}
	
	@Test
	public void test_delete_template() throws Exception{
		
		Long templateId = 2121212121210L;
		String status="Success";
		when(excelImportToolLogic.deleteTemplate(templateId)).thenReturn(status);
		ResponseEntity<?> res = excelImportToolApiImpl.deleteImportTemplate(templateId);
		Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
		
	}
	
	@Test
	public void test_playslip_format() throws Exception{
		Long companyId = 267l;
		String playSlipFormat="T";
		Map<String, String> paySlipFormatMap = new HashMap<String, String>();
		paySlipFormatMap.put("paySlipFormat", playSlipFormat);
		when(dataImportLogic.getPayslipFormatforCompany(companyId)).thenReturn(playSlipFormat);
		ResponseEntity<?> res = excelImportToolApiImpl.getPlaySlipFormat();
		Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
		Assert.assertEquals(paySlipFormatMap, res.getBody());
		
	}
	
	@Test
	public void test_company_frequency() throws Exception{
		Long companyId = 267l;
		
		PayslipFrequencyDTO payslipFrequencyDTO = new PayslipFrequencyDTO();
		payslipFrequencyDTO.setPayslipFrequencyID(4l);
		payslipFrequencyDTO.setFrequency("Partly");
		
		List<PayslipFrequencyDTO> templateList =  new ArrayList<PayslipFrequencyDTO>();
		templateList.add(payslipFrequencyDTO);
		when(dataImportLogic.getPayslipFrequency(companyId)).thenReturn(templateList);
		ResponseEntity<?> res = excelImportToolApiImpl.getCompanyFrequency();
		Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
		
	}
		
	@Test
	public void test_templateList() throws Exception{
		Long companyId = 267l;
		Long entityId = 2l;
		ExcelImportToolForm excelImportToolForm = new ExcelImportToolForm();
		excelImportToolForm.setTemplateId(2440);
		excelImportToolForm.setTemplateName("New+Hires");
		List<ExcelImportToolForm> templateList = new ArrayList<ExcelImportToolForm>();
		templateList.add(excelImportToolForm);
		
//		JsonConfig jsonConfig = new JsonConfig();
//		JSONArray jsonObject = JSONArray.fromObject(templateList,
//				jsonConfig);
		
		when(dataImportLogic.getTemplateList(entityId, companyId)).thenReturn(templateList);
		ResponseEntity<?> res = excelImportToolApiImpl.getTemplateList(entityId);
		Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
		Assert.assertEquals(templateList, res.getBody());
	}
	
	@Test
	public void test_history() throws Exception{
		Long companyId = 267l;
		DataImportForm importHistory = new DataImportForm();
		List<DataImportHistoryDTO> dataImportHistoryList = new ArrayList<DataImportHistoryDTO>();
		DataImportHistoryDTO dataImportHistoryDTO = new DataImportHistoryDTO();
		dataImportHistoryDTO.setDataImportHistoryId(168l);
		dataImportHistoryDTO.setFileName("V_IMPORT.xlsx");
		dataImportHistoryDTO.setImportDate("15 Jul 2019");
		dataImportHistoryDTO.setStatus("Completed");
		dataImportHistoryList.add(dataImportHistoryDTO);
		importHistory.setDataImportHistoryList(dataImportHistoryList);
		
		
		when(dataImportLogic.getImportHistory(companyId)).thenReturn(importHistory);
		ResponseEntity<?> res = excelImportToolApiImpl.getHistory();
		Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
		Assert.assertEquals(importHistory.getDataImportHistoryList(), res.getBody());
		
	}
		
	
	@Test
	public void test_company_address_mapping() throws Exception{
		Long companyId = 267l;
		List<EmployeeFilterListForm> filterList = new ArrayList<EmployeeFilterListForm>();
		EmployeeFilterListForm employeeFilterListForm = new EmployeeFilterListForm();
		employeeFilterListForm.setDataDictionaryId(1396L);
		employeeFilterListForm.setFilterId(1021L);
		filterList.add(employeeFilterListForm);
		when(dataImportLogic.getCompanyAddressMappingList(companyId)).thenReturn(filterList);
		ResponseEntity<?> res = excelImportToolApiImpl.getCompanyAddressMapping();
		Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
	}
	
	
	@Test
	public void test_company_filter() throws Exception{
		
		Long companyId = 267l;
		Long employeeId = 12440l;
		List<EmployeeFilterListForm> filterList = new ArrayList<EmployeeFilterListForm>();
		EmployeeFilterListForm employeeFilterListForm = new EmployeeFilterListForm();
		employeeFilterListForm.setFieldName("Company Name");
		employeeFilterListForm.setDataDictionaryId(898L);
		employeeFilterListForm.setDataType("nvarchar");
		filterList.add(employeeFilterListForm);
		when(dataImportLogic.getCompanyFilterList(companyId, employeeId)).thenReturn(filterList);
		ResponseEntity<?> res = excelImportToolApiImpl.getCompanyFilterList();
		Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
	}
	
	
	@Test
	public void test_generate_excel() throws Exception{
		
		Long templateIdDecr = 12323232323232l;
		ExcelImportToolForm importToolForm = new ExcelImportToolForm();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		
		Workbook wb = new HSSFWorkbook();
		Sheet sheet = wb.createSheet("Sheet1");

		importToolForm.setBaseSectionId(22);
		
		importToolForm.setWorkbook(wb);
		
		when(excelImportToolLogic.generateExcel(templateIdDecr)).thenReturn(importToolForm);
		
		importToolForm.getWorkbook().write(bos);
		//byte[] bytes = 1234
		byte[] data = bos.toByteArray();
		
		Map<String, Object> generateDataExcelMap = new HashMap<>();
		generateDataExcelMap.put("generateExcelbody", data);
		generateDataExcelMap.put("excelName", importToolForm.getTemplateName() + ".xlsx");
		
		ResponseEntity<?> res = excelImportToolApiImpl.generateExcelImport(templateIdDecr);	
		Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
//		Assert.assertEquals(generateDataExcelMap, res.getBody());
	}
	
		
}
