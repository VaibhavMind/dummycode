package com.payasia.api.hris;

import java.io.IOException;
import java.util.Locale;

import javax.xml.bind.JAXBException;

import org.springframework.http.ResponseEntity;
import org.xml.sax.SAXException;

import com.itextpdf.text.DocumentException;
import com.payasia.api.hris.model.SearchParam;
import com.payasia.api.swagger.SwaggerTags;
import com.payasia.common.form.EmployeePaySlipForm;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Api(tags="HRIS", description="HRIS related APIs")
public interface EmployeePayslipApi extends SwaggerTags{

	@ApiOperation(value = "Search Payslip data", notes = "Payslip data can be provided with sorting and pagination.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> getPaySlipList(@ApiParam(value = "Conditions for searching, sorting and pagination can be given in this object.", required = true) SearchParam searchParamObj);
	
	@ApiOperation(value = "View and Download Payslip", notes = "A particular Payslip can be previewed and downloaded here.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> dogetPaySlip(EmployeePaySlipForm employeePaySlipForm, @ApiParam(value = "Locale can be changed with this parameter.", required = false) Locale locale)
			throws DocumentException, IOException, JAXBException, SAXException;
	
	@ApiOperation(value = "Shows Monthlist", notes = "Month-name corresponding to month-id is displayed here.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> getMonthList();

}
