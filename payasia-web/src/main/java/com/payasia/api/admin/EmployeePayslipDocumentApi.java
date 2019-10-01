package com.payasia.api.admin;

import java.io.IOException;
import java.util.Locale;

import javax.xml.bind.JAXBException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.xml.sax.SAXException;

import com.itextpdf.text.DocumentException;
import com.payasia.api.hris.model.SearchParam;
import com.payasia.common.form.AdminPaySlipForm;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

public interface EmployeePayslipDocumentApi {
	
	@ApiOperation(value = "Generate Pay Slip", notes = "Employee can be  show payslip")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> generatePayslip(AdminPaySlipForm adminPaySlipForm) throws DocumentException, IOException, JAXBException, SAXException;
	
	
	@ApiOperation(value = "Show payslip part details", notes = "Employee can be show part of payslip")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> showPayslipPartDetailsForAdmin(String strpayslipDataStr);
	
	
	
	@ApiOperation(value = "download payslip", notes = "Employee can be download payslip")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> downloadPaySlip( AdminPaySlipForm adminPaySlipForm) throws DocumentException, IOException, JAXBException, SAXException;
	
	
	@ApiOperation(value = "print payslip", notes = "Employee can be print payslip")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> printPaySlip( AdminPaySlipForm adminPaySlipForm) throws DocumentException, IOException, JAXBException, SAXException;
	
   ResponseEntity<?> viewMonthAndYearList();
   
   @ApiOperation(value = "Show Employee Number", notes = "Employee number can be  searched employee number and name")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> getEmployeeId(String employeeNumber);

}
