package com.payasia.api.admin;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.springframework.http.ResponseEntity;
import org.xml.sax.SAXException;

import com.itextpdf.text.DocumentException;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

/**
 * @author anuj saxena
 * 
 */

public interface EmployeeDocumentApi {
	
	ResponseEntity<?>  showYearList();
	
	@ApiOperation(value = "Show Employee Number", notes = "Employee number can be  searched employee number and name")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> getEmployeeId(String searchString);
	
	@ApiOperation(value = "Show Tax Document List", notes = "Employee can be show tax dcoument list")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> getTaxDocumentList(String documentDataStr);
	
	@ApiOperation(value = "do download tax document", notes = "Employee can be download")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> downloadTaxDocuments(Long documentId) throws DocumentException, IOException, JAXBException, SAXException;

	@ApiOperation(value = "Show Document", notes = "Employee can be show tax dcoument")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> getTaxDocuments(Long documentId) throws DocumentException, IOException, JAXBException, SAXException;
	
	@ApiOperation(value = "Print Tax Document", notes = "Employee can be print tax document")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> printTaxDocuments(Long documentId) throws DocumentException, IOException, JAXBException, SAXException;
}
