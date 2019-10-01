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
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.RestController;
import org.xml.sax.SAXException;

import com.itextpdf.text.DocumentException;
import com.payasia.api.admin.impl.EmployeeDocumentApiImpl;
import com.payasia.common.dto.CompanyDocumentDetailDTO;
import com.payasia.common.form.AdminPaySlipForm;
import com.payasia.common.form.EmployeeTaxDocumentForm;
import com.payasia.logic.AWSS3Logic;
import com.payasia.logic.EmployeeDocumentLogic;
import com.payasia.test.root.TDDConfig;

@RestController
public class EmployeeTaxDocumentTDD extends TDDConfig {

	@InjectMocks
	private EmployeeDocumentApiImpl employeeDocumentApiImpl;

	@Mock
	private EmployeeDocumentLogic employeeDocumentLogic;

	@Mock
	private AWSS3Logic awss3LogicImpl;
	
	

	@Before
	public void setup() {
		mockMvc = MockMvcBuilders.standaloneSetup(employeeDocumentApiImpl).build();
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
		ResponseEntity<?> res = employeeDocumentApiImpl.getEmployeeId(searchString);
		Assert.assertEquals(adminPaySlipFormFormList,res.getBody());
		Assert.assertEquals(HttpStatus.OK, res.getStatusCode());

	}

	@Test
	public void testYearList() {
		
		List<Integer> yearList = new ArrayList<>();
		yearList.add(2016);
		yearList.add(2018);
		yearList.add(2019);
		Long companyId = 267l;
		when(employeeDocumentLogic.getYearList(companyId)).thenReturn(yearList);
		ResponseEntity<?> res = employeeDocumentApiImpl.showYearList();
		Assert.assertEquals(yearList,res.getBody());
		Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
		
	}
	
	@Test
	public void testGetTaxDocumentList() {
		
		String documnetDataStr ="{\r\n" + 
				"	\"employeeNumber\":\"demoemp\",\r\n" + 
				"	\"year\":2019\r\n" + 
				"}";
		Long companyId = 267l;
		String employeeNumber = "demoemp";
		Long employeeId = 12440l;
	    int	year =2019;
		EmployeeTaxDocumentForm employeeTaxDocumentForm = new EmployeeTaxDocumentForm();
		List<CompanyDocumentDetailDTO> CompanyDocumentDetailList = new ArrayList<CompanyDocumentDetailDTO>();
		CompanyDocumentDetailDTO companyDocumentDetailDTO1 = new CompanyDocumentDetailDTO();
		companyDocumentDetailDTO1.setDocumentId(1206492l);
		companyDocumentDetailDTO1.setDocumentName("121212.pdf");
		
		CompanyDocumentDetailDTO companyDocumentDetailDTO2 = new CompanyDocumentDetailDTO();
		companyDocumentDetailDTO1.setDocumentId(1206493l);
		companyDocumentDetailDTO1.setDocumentName("121212.pdf");
		CompanyDocumentDetailList.add(companyDocumentDetailDTO1);
		CompanyDocumentDetailList.add(companyDocumentDetailDTO2);
		employeeTaxDocumentForm.setTaxDocumentList(CompanyDocumentDetailList);
		when(employeeDocumentLogic.getTaxDocumentList(employeeNumber, year, companyId, employeeId)).thenReturn(employeeTaxDocumentForm);
		ResponseEntity<?> res = employeeDocumentApiImpl.getTaxDocumentList(documnetDataStr);
		Assert.assertEquals(employeeTaxDocumentForm,res.getBody());
		Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
		
	}
	@Ignore
	@Test
	public void testDownloadTaxDocuments() throws DocumentException, IOException, JAXBException, SAXException {
		
		 String filePath ="abc";
		InputStream anyInputStream = new ByteArrayInputStream("test data".getBytes());
		 byte[] byteFile = {37, 80, 68, 70, 45, 49, 46, 52};
		Long documentId = 1234l;
		when(employeeDocumentLogic.generateTaxDocument(documentId)).thenReturn(filePath);
		 when(awss3LogicImpl.readS3ObjectAsStream(filePath)).thenReturn(anyInputStream);
		ResponseEntity<?> res = employeeDocumentApiImpl.downloadTaxDocuments(documentId);
		Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
		
	}

}
