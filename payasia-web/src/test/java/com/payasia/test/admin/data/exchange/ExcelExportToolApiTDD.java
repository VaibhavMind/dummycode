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

import com.payasia.api.admin.data.exchange.impl.ExcelExportToolApiImpl;
import com.payasia.api.hris.model.Filters;
import com.payasia.api.hris.model.SearchParam;
import com.payasia.api.utils.SearchSortUtils;
import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.SearchSortDTO;
import com.payasia.common.dto.SectionInfoDTO;
import com.payasia.common.form.DBTableInformationForm;
import com.payasia.common.form.DataExportForm;
import com.payasia.common.form.ExcelExportFiltersForm;
import com.payasia.common.form.ExcelExportToolForm;
import com.payasia.common.form.ExcelExportToolFormResponse;
import com.payasia.common.form.ExcelImportToolForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.logic.DataExportLogic;
import com.payasia.logic.ExcelExportToolLogic;
import com.payasia.test.root.TDDConfig;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

@RestController
public class ExcelExportToolApiTDD extends TDDConfig{

	@InjectMocks
	private ExcelExportToolApiImpl excelExportToolApiImpl;
	
	@Mock
	private ExcelExportToolLogic excelExportToolLogic;
	
	@Mock
	DataExportLogic dataExportLogic;
	
	@Mock
	private SearchSortUtils searchSortUtils;
	
	@Before
	public void setup() {
		mockMvc=MockMvcBuilders.standaloneSetup(excelExportToolApiImpl).build();
	}
	
	@Test
	public void test_search_import_template() {
		
		Long companyId = 267l;
		Long entityId = 0L;
		String categoryId ="0";
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
		
		List<ExcelExportToolForm> excelExportToolFormList = new ArrayList<ExcelExportToolForm>();
		ExcelExportToolFormResponse excelExportToolFormResponse = new ExcelExportToolFormResponse();
		ExcelExportToolForm excelExportToolForm = new ExcelExportToolForm();
		excelExportToolForm.setTemplateName("CompanyTemp1");
		excelExportToolForm.setCategory("Company");
		
		excelExportToolFormList.add(excelExportToolForm);
		excelExportToolFormResponse.setExcelExportToolFormList(excelExportToolFormList);
		
		when(searchSortUtils.getSearchSortObject(searchParamObj)).thenReturn(searchSortDTO);
		
		when(excelExportToolLogic.getExistImportTempDef(pageDTO, sortDTO,
				companyId,searchCondition, searchValue,entityId,categoryId)).thenReturn(excelExportToolFormResponse);
		
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(excelExportToolFormResponse, jsonConfig);
		
		ResponseEntity<?> res = excelExportToolApiImpl.findExcelExportTemplate(searchParamObj);
		Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
		Assert.assertEquals(jsonObject.toString(), res.getBody());
		
	}

	
	@Test
	public void test_get_existing_mapping() throws Exception{

		Long entityId = 20L;
		Long companyId = 267l;
		ExcelExportToolForm excelExportToolForm = new ExcelExportToolForm();
		DBTableInformationForm dbTableInformationForm = new DBTableInformationForm();
		List<DBTableInformationForm> dbTableInformationFormList = new ArrayList<DBTableInformationForm>();
		dbTableInformationForm.setDataDictionaryId(891);
		dbTableInformationFormList.add(dbTableInformationForm);
		excelExportToolForm.setDbTableInformationFormList(dbTableInformationFormList);
		when(excelExportToolLogic.getExistMapping(companyId,entityId, UserContext.getLanguageId())).thenReturn(excelExportToolForm);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(excelExportToolForm, jsonConfig);
		ResponseEntity<?> res = excelExportToolApiImpl.findExistingMapping(entityId);
		Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
		Assert.assertEquals(jsonObject.toString(), res.getBody());
	}
	
	@Test
	public void test_get_existing_child_mapping() throws Exception{
		Long companyId = 267l;
		Long entityId = 20L;
		Long formId = 13l;
		int tablePosition =1;
		
		ExcelExportToolForm excelExportToolForm = new ExcelExportToolForm();
		Set<SectionInfoDTO> sectionList = new HashSet<SectionInfoDTO>();
		SectionInfoDTO basicSectionInfoDTO = new SectionInfoDTO();
		basicSectionInfoDTO.setSectionId(0l);
		basicSectionInfoDTO.setSectionName("Basic Information");
		sectionList.add(basicSectionInfoDTO);
		List<SectionInfoDTO> sectionInfoList = new ArrayList<>(sectionList);
		excelExportToolForm.setSectionList(sectionInfoList);
		
		when(excelExportToolLogic.getExistTableMapping(companyId,entityId,formId,tablePosition , UserContext.getLanguageId())).thenReturn(excelExportToolForm);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(excelExportToolForm, jsonConfig);
		ResponseEntity<?> res = excelExportToolApiImpl.findExistingChildTableMapping(entityId,formId,tablePosition);
		Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
		Assert.assertEquals(jsonObject.toString(), res.getBody());
	}
	
	@Test
	public void test_get_templates() throws Exception{
		
		Long templateId = 1212121212121212120L;
		ExcelExportToolForm excelExportToolForm = new ExcelExportToolForm();
		excelExportToolForm.setTemplateName("CompanyTemp1");
		when(excelExportToolLogic.getDataForTemplate(templateId)).thenReturn(excelExportToolForm);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(excelExportToolForm, jsonConfig);
		ResponseEntity<?> res = excelExportToolApiImpl.getTemplates(templateId);
		Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
		Assert.assertEquals(jsonObject.toString(), res.getBody());
	}
	
	@Test
	public void test_delete_templates() throws Exception{
		
		Long companyId = 267l;
		Long templateId = 2121212121210L;
		boolean status = true;
		when(excelExportToolLogic.isAdminAuthorizedForComTemplate(templateId, companyId)).thenReturn(status);
		ResponseEntity<?> res = excelExportToolApiImpl.deleteTemplates(templateId);
		Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
	}
	
	@Test
	public void test_generate_excel() throws Exception{
		
		Long companyId = 1L;
		List<ExcelExportFiltersForm> excelExportFilterList = new ArrayList<ExcelExportFiltersForm>();
		DataExportForm dataExportForm = new DataExportForm();
		dataExportForm.setTemplateId(50);
		dataExportForm.setScope("group");
		dataExportForm.setExcelExportFilterList(null);
		String[] arry = new String[1];
		arry[0] = "1";
		dataExportForm.setSelectedComapnyIds(arry);
		Workbook wb = new HSSFWorkbook();
		dataExportForm.setWorkbook(wb);
		
		
		
		when(dataExportLogic.generateExcelGroup(dataExportForm.getTemplateId(), 267L, 12440L,
				dataExportForm.getExcelExportFilterList(), UserContext.getLanguageId(),
				dataExportForm.getSelectedComapnyIds())).thenReturn(dataExportForm);
		
		Workbook excelFile = dataExportForm.getWorkbook();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		excelFile.write(bos);
		byte[] data = bos.toByteArray();
		Map<String, Object> generateDataExcelMap = new HashMap<>();
		generateDataExcelMap.put("generateExcelbody", data);
		generateDataExcelMap.put("excelName", dataExportForm.getTemplateName() + ".xlsx");
		
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(dataExportForm, jsonConfig);
		ResponseEntity<?> res = excelExportToolApiImpl.generateExcelExport(jsonObject.toString());	
		Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
//		Assert.assertEquals(generateDataExcelMap, res.getBody());
	}
	
}
