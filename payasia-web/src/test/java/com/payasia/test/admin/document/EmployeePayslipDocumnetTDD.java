package com.payasia.test.admin.document;

import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.xml.sax.SAXException;

import com.itextpdf.text.DocumentException;
import com.payasia.api.admin.impl.EmployeePayslipDocumentApiImpl;
import com.payasia.common.dto.MonthMasterDTO;
import com.payasia.common.dto.PartDTO;
import com.payasia.common.form.AdminPaySlipForm;
import com.payasia.common.form.PartsForm;
import com.payasia.logic.AWSS3Logic;
import com.payasia.logic.EmployeeDocumentLogic;
import com.payasia.logic.EmployeePaySlipLogic;
import com.payasia.logic.GeneralLogic;
import com.payasia.test.root.TDDConfig;

public class EmployeePayslipDocumnetTDD extends TDDConfig{
	
	@InjectMocks
	private EmployeePayslipDocumentApiImpl employeePayslipDocumentApiImpl;

	@Mock
	private EmployeeDocumentLogic employeeDocumentLogic;
	
	@Mock
	private EmployeePaySlipLogic employeePaySlipLogic;
	
	@Mock
	private GeneralLogic generalLogic;
	
	@Mock
	private AWSS3Logic awss3LogicImpl;
	
	@Before
	public void setup() {
		mockMvc = MockMvcBuilders.standaloneSetup(employeePayslipDocumentApiImpl).build();
	}
	@Test
	public void testGetEmployeeId() {
		Long companyId = 267l;
		Long employeeId = 12440l;
		String searchString = "demo";
		AdminPaySlipForm adminPaySlipFormForm1 = new AdminPaySlipForm();
		List<AdminPaySlipForm> adminPaySlipFormFormList = new ArrayList<>();
		adminPaySlipFormForm1.setEmployeeId(102313l);
		adminPaySlipFormForm1.setEmployeeNumber("demoemp10");
		adminPaySlipFormForm1.setEmployeeName("demo emp10");

		AdminPaySlipForm adminPaySlipFormForm2 = new AdminPaySlipForm();
		adminPaySlipFormForm2.setEmployeeId(102313l);
		adminPaySlipFormForm2.setEmployeeNumber("demoemp10");
		adminPaySlipFormForm2.setEmployeeName("demo emp10");
		adminPaySlipFormFormList.add(adminPaySlipFormForm1);
		adminPaySlipFormFormList.add(adminPaySlipFormForm2);

		when(employeeDocumentLogic.getEmployeeId(companyId, searchString, employeeId))
				.thenReturn(adminPaySlipFormFormList);
		ResponseEntity<?> res = employeePayslipDocumentApiImpl.getEmployeeId(searchString);
		Assert.assertEquals(adminPaySlipFormFormList,res.getBody());
		Assert.assertEquals(HttpStatus.OK, res.getStatusCode());

	}
	
	@Test
	public void testMonthList() {
		
		List<MonthMasterDTO> monthList = new ArrayList<>();
		MonthMasterDTO monthMasterDTO1 = new MonthMasterDTO();
		monthMasterDTO1.setMonthId(1);
		monthMasterDTO1.setMonthName("January");
		MonthMasterDTO monthMasterDTO2 = new MonthMasterDTO();
		monthMasterDTO2.setMonthId(2);
		monthMasterDTO2.setMonthName("Febuary");
		MonthMasterDTO monthMasterDTO3 = new MonthMasterDTO();
		monthMasterDTO3.setMonthId(3);
		monthMasterDTO3.setMonthName("March");
		MonthMasterDTO monthMasterDTO4 = new MonthMasterDTO();
		monthMasterDTO4.setMonthId(4);
		monthMasterDTO4.setMonthName("April");
		MonthMasterDTO monthMasterDTO5 = new MonthMasterDTO();
		monthMasterDTO5.setMonthId(5);
		monthMasterDTO5.setMonthName("May");
		MonthMasterDTO monthMasterDTO6 = new MonthMasterDTO();
		monthMasterDTO6.setMonthId(6);
		monthMasterDTO6.setMonthName("June");
		monthList.add(monthMasterDTO1);
		monthList.add(monthMasterDTO2);
		monthList.add(monthMasterDTO3);
		monthList.add(monthMasterDTO4);
		monthList.add(monthMasterDTO5);
		monthList.add(monthMasterDTO6);
		
		when(generalLogic.getMonthList()).thenReturn(monthList);
		ResponseEntity<?> res = employeePayslipDocumentApiImpl.viewMonthAndYearList();
		Assert.assertEquals(monthList,res.getBody());
		Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
		
	}
	
	@Test
	public void testShowPayslipPartDetailsForAdmin() {
		
		String documnetDataStr ="{\r\n" + 
				"	\"employeeNumber\":\"demoemp\",\r\n" + 
				"	\"monthId\":9,\r\n" + 
				"	\"year\":2019\r\n" + 
				"}";
		Long companyId = 267l;
		String employeeNumber = "demoemp";
		Long employeeId = 12440l;
	    int	year =2019;
	    Long monthId =9l;
	    PartsForm partsForm = new PartsForm();
		List<PartDTO> partsList = new ArrayList<>();
		PartDTO partDTO1 = new PartDTO();
		partDTO1.setPart(1);
		PartDTO partDTO2 = new PartDTO();
		partDTO2.setPart(2);
		partsList.add(partDTO1);
		partsList.add(partDTO2);
		partsForm.setParts(partsList);
		when(employeePaySlipLogic.getPayslipPartDetailsForAdmin(employeeNumber,year,monthId,companyId,employeeId)).thenReturn(partsForm);
		ResponseEntity<?> res = employeePayslipDocumentApiImpl.showPayslipPartDetailsForAdmin(documnetDataStr);
		Assert.assertEquals(partsForm,res.getBody());
		Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
		
	}
	
	@Test
	public void testDownloadDocuments() throws IOException, DocumentException, JAXBException, SAXException {
		
		
		 Long employeeId=12440l;
		 Long companyId =267l;
		 AdminPaySlipForm adminPaySlipForm = new AdminPaySlipForm();
		 adminPaySlipForm.setEmployeeNumber("demoemp");
		 adminPaySlipForm.setPayslipMonthId(9l);
		 adminPaySlipForm.setPayslipYear(2019);
		 adminPaySlipForm.setPayslipPart(2);
		 boolean exitsFile =true;
		 InputStream anyInputStream = new ByteArrayInputStream("test data".getBytes());
		 byte[] byteFile = {37, 80, 68, 70, 45, 49, 46, 52};
		 String filePath ="company/306/tax/2011/PMY0120/PMY0120_payslip_2011121.pdf";
		 when(employeePaySlipLogic.getPaySlipStatusFromCompanyDocumentForAdmin(employeeId,companyId,adminPaySlipForm)).thenReturn(exitsFile);
		 when(employeePaySlipLogic.getPaySlipFromCompanyDocumentFolderForAdmin(employeeId,companyId,adminPaySlipForm)).thenReturn(byteFile);
		 when(awss3LogicImpl.readS3ObjectAsStream(filePath)).thenReturn(anyInputStream);
		 ResponseEntity<?> res = employeePayslipDocumentApiImpl.downloadPaySlip(adminPaySlipForm);
		 Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
	}
	
	@Test
	public void testPrintPaySlip() throws IOException, DocumentException, JAXBException, SAXException {
		
		
		 Long employeeId=12440l;
		 Long companyId =267l;
		 AdminPaySlipForm adminPaySlipForm = new AdminPaySlipForm();
		 adminPaySlipForm.setEmployeeNumber("demoemp");
		 adminPaySlipForm.setPayslipMonthId(9l);
		 adminPaySlipForm.setPayslipYear(2019);
		 adminPaySlipForm.setPayslipPart(2);
		 boolean exitsFile =true;
		 InputStream anyInputStream = new ByteArrayInputStream("test data".getBytes());
		 byte[] byteFile = {37, 80, 68, 70, 45, 49, 46, 52};
		 String filePath ="company/306/tax/2011/PMY0120/PMY0120_payslip_2011121.pdf";
		 when(employeePaySlipLogic.getPaySlipStatusFromCompanyDocumentForAdmin(employeeId,companyId,adminPaySlipForm)).thenReturn(exitsFile);
		 when(employeePaySlipLogic.getPaySlipFromCompanyDocumentFolderForAdmin(employeeId,companyId,adminPaySlipForm)).thenReturn(byteFile);
		 when(awss3LogicImpl.readS3ObjectAsStream(filePath)).thenReturn(anyInputStream);
		 ResponseEntity<?> res = employeePayslipDocumentApiImpl.downloadPaySlip(adminPaySlipForm);
		 Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
	}

	
	@Test
	public void testGeneratePayslip() throws IOException, DocumentException, JAXBException, SAXException {
		
		
		 Long employeeId=12440l;
		 Long companyId =267l;
		 AdminPaySlipForm adminPaySlipForm = new AdminPaySlipForm();
		 adminPaySlipForm.setEmployeeNumber("demoemp");
		 adminPaySlipForm.setPayslipMonthId(9l);
		 adminPaySlipForm.setPayslipYear(2019);
		 adminPaySlipForm.setPayslipPart(2);
		 boolean exitsFile =true;
		 InputStream anyInputStream = new ByteArrayInputStream("test data".getBytes());
		 byte[] byteFile = {37, 80, 68, 70, 45, 49, 46, 52};
		 String filePath ="company/306/tax/2011/PMY0120/PMY0120_payslip_2011121.pdf";
		 when(employeePaySlipLogic.getPaySlipStatusFromCompanyDocumentForAdmin(employeeId,companyId,adminPaySlipForm)).thenReturn(exitsFile);
		 when(employeePaySlipLogic.getPaySlipFromCompanyDocumentFolderForAdmin(employeeId,companyId,adminPaySlipForm)).thenReturn(byteFile);
		 when(awss3LogicImpl.readS3ObjectAsStream(filePath)).thenReturn(anyInputStream);
		 ResponseEntity<?> res = employeePayslipDocumentApiImpl.downloadPaySlip(adminPaySlipForm);
		 Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
	}
}
