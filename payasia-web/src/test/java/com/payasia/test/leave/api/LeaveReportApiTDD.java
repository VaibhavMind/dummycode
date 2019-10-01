package com.payasia.test.leave.api;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.RestController;

import com.payasia.api.leave.impl.EmployeeLeaveReportsApiImpl;
import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.EmployeeLeaveSchemeTypeDTO;
import com.payasia.common.dto.LeaveReportDTO;
import com.payasia.common.dto.LeaveReportHeaderDTO;
import com.payasia.common.dto.LeaveTranReportDTO;
import com.payasia.common.dto.SelectOptionDTO;
import com.payasia.common.form.EmployeeFilterListForm;
import com.payasia.common.form.LeavePreferenceForm;
import com.payasia.common.form.LeaveReportsForm;
import com.payasia.logic.LeaveBalanceSummaryLogic;
import com.payasia.logic.LeaveReportsLogic;
import com.payasia.logic.ManageUserLogic;
import com.payasia.test.root.TDDConfig;
import com.payasia.test.util.TestUtil;

@RestController
public class LeaveReportApiTDD extends TDDConfig{
	
	@InjectMocks
	private EmployeeLeaveReportsApiImpl employeeLeaveReportsApiImpl;
	
	@Mock
	private LeaveReportsLogic leaveReportsLogic;
	
	@Mock
	private LeaveBalanceSummaryLogic leaveBalanceSummaryLogic;
	
	@Mock
	private ManageUserLogic manageUserLogic;
	
	@Mock
	private ServletContext servletContext;
	
	@Before
	public void setup() {
		mockMvc=MockMvcBuilders.standaloneSetup(employeeLeaveReportsApiImpl).build();
	}	

//	@Ignore
//	@Test(timeout=9000)
	@Test
	public void test_get_leave_type_list() throws Exception{
		Long companyId = 267l;
		
		List<LeaveReportsForm> leaveSchemeList = new ArrayList<>();
		LeaveReportsForm leaveReportsFormObj1 = new LeaveReportsForm();
		leaveReportsFormObj1.setFileType("Test-1");
		leaveReportsFormObj1.setLeaveType("Case-1");
		leaveReportsFormObj1.setLeaveTypeId(11l);
		
		LeaveReportsForm leaveReportsFormObj2 = new LeaveReportsForm();
		leaveReportsFormObj2.setFileType("Test-2");
		leaveReportsFormObj2.setLeaveType("Case-2");
		leaveReportsFormObj2.setLeaveTypeId(22l);
		
		leaveSchemeList.add(leaveReportsFormObj1);
		leaveSchemeList.add(leaveReportsFormObj2);
		
		when(leaveReportsLogic.getLeaveTypeList(companyId)).thenReturn(leaveSchemeList);

		String url = getAppUrl()+"employee/leave/leaveReports/leave-type-list";
		
		ResultActions result = mockMvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("[0].fileType").value("Test-1"))
				.andExpect(jsonPath("[0].leaveType").value("Case-1"))
				.andExpect(jsonPath("[0].leaveTypeId").value(11l))
				.andExpect(jsonPath("[1].fileType").value("Test-2"))
				.andExpect(jsonPath("[1].leaveType").value("Case-2"))
				.andExpect(jsonPath("[1].leaveTypeId").value(22l));
		
		System.out.println("RESULTS : " + result);
	}
	
//	@Ignore
	@Test
	public void test_get_leave_report_list() throws Exception{
		Long companyId = 267l;
		String companyCode = "minddemo";
		boolean isAdmin = false;
		
		List<SelectOptionDTO> leaveReportList = new ArrayList<>();
		
		SelectOptionDTO selectOptionDTOObj1 = new SelectOptionDTO();
		selectOptionDTOObj1.setStrKey("LTR");;
		selectOptionDTOObj1.setValue("Leave Transaction Report");
		
		SelectOptionDTO selectOptionDTOObj2 = new SelectOptionDTO();
		selectOptionDTOObj2.setStrKey("LBR");;
		selectOptionDTOObj2.setValue("Leave Balance Report");
		
		leaveReportList.add(selectOptionDTOObj1);
		leaveReportList.add(selectOptionDTOObj2);
		
		when(leaveReportsLogic.getLeaveReportList(companyId, companyCode, isAdmin)).thenReturn(leaveReportList);

		String url = getAppUrl()+"employee/leave/leaveReports/leave-report-list";
		
		ResultActions result = mockMvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("[0].strKey").value("LTR"))
				.andExpect(jsonPath("[0].value").value("Leave Transaction Report"))
				.andExpect(jsonPath("[1].strKey").value("LBR"))
				.andExpect(jsonPath("[1].value").value("Leave Balance Report"));
		
		System.out.println("RESULTS : " + result);
	}
	
//	@Ignore
	@Test
	public void test_get_leave_preference_detail() throws Exception{
		Long companyId = 267l;
		String companyCode = "minddemo";
		boolean isAdmin = false;
		
		LeavePreferenceForm leavePreferenceForm = new LeavePreferenceForm();
		leavePreferenceForm.setShowLeaveBalanceCustomReport(true);
		leavePreferenceForm.setLeaveUnit("Days");
		
		when(leaveReportsLogic.getLeavePreferenceDetail(companyId, companyCode, isAdmin)).thenReturn(leavePreferenceForm);

		String url = getAppUrl()+"employee/leave/leaveReports/leave-preference-detail";
		
		ResultActions result = mockMvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.showLeaveBalanceCustomReport").value(true))
				.andExpect(jsonPath("$.leaveUnit").value("Days"));
		
		System.out.println("RESULTS : " + result);
	}
	
//	@Ignore
	@Test
	public void test_get_leave_transaction_list() throws Exception{
		Long companyId = 267l;
		
		List<LeaveReportsForm> leaveSchemeList = new ArrayList<>();
		LeaveReportsForm leaveReportsFormObj1 = new LeaveReportsForm();
		leaveReportsFormObj1.setLeaveTransactionName("payasia.approved");
		leaveReportsFormObj1.setLeaveTransactionId(40l);
		
		LeaveReportsForm leaveReportsFormObj2 = new LeaveReportsForm();
		leaveReportsFormObj2.setLeaveTransactionName("payasia.credited");
		leaveReportsFormObj2.setLeaveTransactionId(37l);
		
		leaveSchemeList.add(leaveReportsFormObj1);
		leaveSchemeList.add(leaveReportsFormObj2);
		
		when(leaveReportsLogic.getLeaveTransactionList(companyId)).thenReturn(leaveSchemeList);
		
		when(messageSource.getMessage(leaveReportsFormObj1.getLeaveTransactionName(), new Object[] {}, UserContext.getLocale())).thenReturn("Approved");
		when(messageSource.getMessage(leaveReportsFormObj2.getLeaveTransactionName(), new Object[] {}, UserContext.getLocale())).thenReturn("Credited");
		
		String url = getAppUrl()+"employee/leave/leaveReports/leave-transaction-list";
		
		ResultActions result = mockMvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("[0].leaveTransactionName").value("Approved"))
				.andExpect(jsonPath("[0].leaveTransactionId").value(40l))
				.andExpect(jsonPath("[1].leaveTransactionName").value("Credited"))
				.andExpect(jsonPath("[1].leaveTransactionId").value(37l));
		
		System.out.println("RESULTS : " + result);
	}
	
	// TODO : Check with jsonObject and jsonObject.toString() return type
//	@Ignore
	@Test
	public void test_get_year_list() throws Exception{
		Long companyId = 267l;
		boolean isManager = true;
		
		List<EmployeeLeaveSchemeTypeDTO> yearList = new ArrayList<>();
		EmployeeLeaveSchemeTypeDTO employeeLeaveSchemeTypeDTOObj1 = new EmployeeLeaveSchemeTypeDTO();
		employeeLeaveSchemeTypeDTOObj1.setYearKey("Jan 2019 - Dec 2019");
		employeeLeaveSchemeTypeDTOObj1.setYearValue(2019);
		employeeLeaveSchemeTypeDTOObj1.setCurrentDateInLeaveCal(false);
		
		EmployeeLeaveSchemeTypeDTO employeeLeaveSchemeTypeDTOObj2 = new EmployeeLeaveSchemeTypeDTO();
		employeeLeaveSchemeTypeDTOObj2.setYearKey("Jan 2020 - Dec 2020");
		employeeLeaveSchemeTypeDTOObj2.setYearValue(2020);
		employeeLeaveSchemeTypeDTOObj2.setCurrentDateInLeaveCal(true);
		
		yearList.add(employeeLeaveSchemeTypeDTOObj1);
		yearList.add(employeeLeaveSchemeTypeDTOObj2);
		
		when(leaveReportsLogic.getDistinctYears(companyId, isManager)).thenReturn(yearList);

		String url = getAppUrl()+"employee/leave/leaveReports/year-list";
		
		ResultActions result = mockMvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("[0].yearKey").value("Jan 2019 - Dec 2019"))
				.andExpect(jsonPath("[0].yearValue").value(2019))
				.andExpect(jsonPath("[0].currentDateInLeaveCal").value(false))
				.andExpect(jsonPath("[1].yearKey").value("Jan 2020 - Dec 2020"))
				.andExpect(jsonPath("[1].yearValue").value(2020))
				.andExpect(jsonPath("[1].currentDateInLeaveCal").value(true));
		
		System.out.println("RESULTS : " + result);
	}
	
//	@Ignore
	@Test
	public void test_get_employee_filter_list() throws Exception{
		Long companyId = 267l;
		
		List<EmployeeFilterListForm> empFilterList = new ArrayList<>();
		EmployeeFilterListForm employeeFilterListFormObj1 = new EmployeeFilterListForm();
		employeeFilterListFormObj1.setFieldName("Test-1");;
		employeeFilterListFormObj1.setDataDictionaryId(66l);
		employeeFilterListFormObj1.setDataType("nvarchar");
		
		EmployeeFilterListForm employeeFilterListFormObj2 = new EmployeeFilterListForm();
		employeeFilterListFormObj2.setFieldName("Test-2");;
		employeeFilterListFormObj2.setDataDictionaryId(77l);
		employeeFilterListFormObj2.setDataType("bit");
		
		empFilterList.add(employeeFilterListFormObj1);
		empFilterList.add(employeeFilterListFormObj2);
		
		when(manageUserLogic.getEmployeeFilterList(companyId)).thenReturn(empFilterList);

		String url = getAppUrl()+"employee/leave/leaveReports/employee-filter-list";
		
		// Without JSONObject.tostring() return type
		ResultActions result = mockMvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("[0].fieldName").value("Test-1"))
				.andExpect(jsonPath("[0].dataDictionaryId").value(66l))
				.andExpect(jsonPath("[0].dataType").value("nvarchar"))
				.andExpect(jsonPath("[1].fieldName").value("Test-2"))
				.andExpect(jsonPath("[1].dataDictionaryId").value(77l))
				.andExpect(jsonPath("[1].dataType").value("bit"));
		
//		// With JSONObject.tostring() return type
//		ResultActions result = mockMvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
//				.andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
//				.andExpect(jsonPath("empFilterList.[0].fieldName").value("Test-1"))
//				.andExpect(jsonPath("empFilterList.[0].dataDictionaryId").value(66l))
//				.andExpect(jsonPath("empFilterList.[0].dataType").value("nvarchar"))
//				.andExpect(jsonPath("empFilterList.[1].fieldName").value("Test-2"))
//				.andExpect(jsonPath("empFilterList.[1].dataDictionaryId").value(77l))
//				.andExpect(jsonPath("empFilterList.[1].dataType").value("bit"));
		
		
		System.out.println("RESULTS : " + result);
	}
	
//	@Ignore
	@Test
	public void test_show_report_data() throws Exception{
		Long companyId = 267l;
		Long employeeId =12440l;
		boolean isManager = true;
		
		LeaveReportsForm leaveReportsForm = new LeaveReportsForm();
		leaveReportsForm.setMultipleLeaveTypeId(new Long[]{});
		leaveReportsForm.setMultipleLeaveTransactionName(new String[]{"Submitted","Withdrawn","Rejected","Approved"});
		leaveReportsForm.setStartDate("2017-11-11");
		leaveReportsForm.setEndDate("2018-11-11");
		leaveReportsForm.setIncludeApprovalCancel(false);;
		leaveReportsForm.setMultipleRecord(false);
		leaveReportsForm.setIncludeResignedEmployees(false);
		leaveReportsForm.setDataDictionaryIds(new String[]{});
		
		LeaveReportDTO leaveDataDTO = new LeaveReportDTO();
		List<LeaveTranReportDTO> leaveTranReportDTOs = new ArrayList<>();;
		LeaveTranReportDTO leaveTranReportDTO = new LeaveTranReportDTO();
		leaveTranReportDTO.setEmployeeName("Test");
		leaveTranReportDTO.setRemarks("Nice");
		leaveTranReportDTO.setLeaveTypeName("Care");
		leaveTranReportDTOs.add(leaveTranReportDTO);
		
		List<LeaveReportHeaderDTO> leaveHeaderDTOs = new ArrayList<>();
		LeaveReportHeaderDTO leaveReportHeaderDTO = new LeaveReportHeaderDTO();
		leaveReportHeaderDTO.setmDataProp("Emp1");
		leaveReportHeaderDTO.setsTitle("TestCase");
		leaveReportHeaderDTO.setsClass("");
		leaveHeaderDTOs.add(leaveReportHeaderDTO);
		
		leaveDataDTO.setLeaveTranReportDTOs(leaveTranReportDTOs);
		leaveDataDTO.setLeaveHeaderDTOs(leaveHeaderDTOs);
		
		when(leaveReportsLogic.showLeaveTranReport(companyId, employeeId, leaveReportsForm, isManager, leaveReportsForm.getDataDictionaryIds())).thenReturn(leaveDataDTO);

		String url = getAppUrl()+"employee/leave/leaveReports/show-report-data";
		
		ResultActions result = mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).param("requestReportType", "ltr").content(TestUtil.asJsonString(leaveReportsForm)))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("aaData.[0].employeeName").value("Test"))
				.andExpect(jsonPath("aaData.[0].remarks").value("Nice"))
				.andExpect(jsonPath("aaData.[0].leaveTypeName").value("Care"))
				.andExpect(jsonPath("aoColumns.[0].mDataProp").value("Emp1"))
				.andExpect(jsonPath("aoColumns.[0].sTitle").value("TestCase"))
				.andExpect(jsonPath("aoColumns.[0].sClass").value(""));
		System.out.println("RESULTS : " + result);
	}
		
//	@Ignore
	@Test
	public void test_generate_report_data_pdf_or_excel() throws Exception{
		Long companyId = 267l;
		Long employeeId =12440l;
		boolean isManager = true;
		
		String fileName= "Leave Transaction Report";
		fileName = fileName + ".xlsx";
		
		String templateName = "E:/WORKSPACE/PayAsiaClaimDev/.metadata/.plugins/org.eclipse.wst.server.core/tmp0/"
				+ "wtpwebapps/payasia-web/resources/LeaveReportTemplate/LeaveTranReportWithAppDateWithoutCustomFields.xlsx";
		
		LeaveReportsForm leaveReportsForm = new LeaveReportsForm();
		leaveReportsForm.setMultipleLeaveTypeId(new Long[]{});
		leaveReportsForm.setMultipleLeaveTransactionName(new String[]{"Submitted","Withdrawn","Rejected","Approved"});
		leaveReportsForm.setStartDate("2017-11-11");
		leaveReportsForm.setEndDate("2018-11-11");
		leaveReportsForm.setIncludeApprovalCancel(false);;
		leaveReportsForm.setMultipleRecord(false);
		leaveReportsForm.setIncludeResignedEmployees(false);
		leaveReportsForm.setDataDictionaryIds(new String[]{});
		leaveReportsForm.setFileType("excel");
		
		LeaveReportDTO leaveDataDTO = new LeaveReportDTO();
		
		leaveDataDTO.setDataDictNameList(new ArrayList<String>());
		
		List<LeaveTranReportDTO> leaveTranReportDTOs = new ArrayList<>();
		LeaveTranReportDTO leaveTranReportDTO = new LeaveTranReportDTO();
		leaveTranReportDTO.setEmployeeName("Test");
		leaveTranReportDTO.setRemarks("Nice");
		leaveTranReportDTO.setLeaveTypeName("Care");
		leaveTranReportDTOs.add(leaveTranReportDTO);
		
		List<LeaveReportHeaderDTO> leaveHeaderDTOs = new ArrayList<>();
		LeaveReportHeaderDTO leaveReportHeaderDTO = new LeaveReportHeaderDTO();
		leaveReportHeaderDTO.setmDataProp("Emp1");
		leaveReportHeaderDTO.setsTitle("TestCase");
		leaveReportHeaderDTO.setsClass("");
		leaveHeaderDTOs.add(leaveReportHeaderDTO);
		
		leaveDataDTO.setLeaveTranReportDTOs(leaveTranReportDTOs);
		leaveDataDTO.setLeaveHeaderDTOs(leaveHeaderDTOs);
		
		when(leaveReportsLogic.showLeaveTranReport(companyId, employeeId, leaveReportsForm, isManager, leaveReportsForm.getDataDictionaryIds())).thenReturn(leaveDataDTO);
		when(servletContext.getRealPath("/resources/LeaveReportTemplate/LeaveTranReportWithAppDateNHoursaWOCustomFields.xlsx")).thenReturn(templateName);
		
		String url = getAppUrl()+"employee/leave/leaveReports/generate-report-data";
		
		ResultActions result = mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).param("requestReportType", "ltr").content(TestUtil.asJsonString(leaveReportsForm)))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.fileName").value(fileName));
		
		System.out.println("RESULTS : " + result);
	}
	
}
