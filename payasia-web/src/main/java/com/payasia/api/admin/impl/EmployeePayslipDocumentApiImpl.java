package com.payasia.api.admin.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.xml.bind.JAXBException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.xml.sax.SAXException;

import com.itextpdf.text.DocumentException;
import com.payasia.api.admin.EmployeePayslipDocumentApi;
import com.payasia.api.utils.ApiMessageHandler;
import com.payasia.api.utils.ApiMessageHandler.Type;
import com.payasia.api.utils.ApiUtils;
import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.MonthMasterDTO;
import com.payasia.common.form.AdminPaySlipForm;
import com.payasia.common.form.PartsForm;
import com.payasia.dao.bean.Payslip;
import com.payasia.logic.EmployeeDocumentLogic;
import com.payasia.logic.EmployeePaySlipLogic;
import com.payasia.logic.GeneralLogic;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * @author anujsaxena
 * 
 */
@Controller
@RequestMapping(value = ApiUtils.API_ROOT_PATH_FOR_ADMIN + "/payslip")
public class EmployeePayslipDocumentApiImpl implements EmployeePayslipDocumentApi {

	@Resource
	private EmployeePaySlipLogic employeePaySlipLogic;

	@Resource
	private EmployeeDocumentLogic employeeDocumentLogic;

	@Autowired
	private MessageSource messageSource;
	
	@Resource
	private GeneralLogic generalLogic;

	@Override
	@PostMapping("part")
	public ResponseEntity<?> showPayslipPartDetailsForAdmin(@RequestBody String strpayslipDataStr) {

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObj = JSONObject.fromObject(strpayslipDataStr, jsonConfig);
		String employeeNumber = jsonObj.getString("employeeNumber");
		int year = jsonObj.getInt("year");
		Long monthId = jsonObj.getLong("monthId");
		Long companyId = UserContext.getClientAdminId();
		Long employeeId = Long.parseLong(UserContext.getUserId());
		PartsForm partForm = employeePaySlipLogic.getPayslipPartDetailsForAdmin(employeeNumber, year, monthId,companyId, employeeId);
		if(partForm !=null && partForm.getParts().isEmpty()) {
			return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource
					.getMessage(partForm.getVerifyResponse(), new Object[] {}, UserContext.getLocale())),
					HttpStatus.NOT_FOUND);
		}
		
		
		return new ResponseEntity<>(partForm, HttpStatus.OK);
	}

	@Override
	@PostMapping("show")
	public ResponseEntity<?> generatePayslip(@RequestBody AdminPaySlipForm adminPaySlipForm)
			throws DocumentException, IOException, JAXBException, SAXException {

		byte[] byteFile = null;
		Payslip payslip = null;
		String pdfName = "";
		Long companyId = UserContext.getClientAdminId();
		Long employeeId = Long.parseLong(UserContext.getUserId());
		boolean isPayslipExistinCompanyDocFolder = employeePaySlipLogic
				.getPaySlipStatusFromCompanyDocumentForAdmin(employeeId, companyId, adminPaySlipForm);
		if (isPayslipExistinCompanyDocFolder) {
			byteFile = employeePaySlipLogic.getPaySlipFromCompanyDocumentFolderForAdmin(employeeId, companyId,
					adminPaySlipForm);
			String ext = "pdf";
			String fileName = adminPaySlipForm.getEmployeeNumber() + "_payslip_" + adminPaySlipForm.getPayslipYear()
					+ adminPaySlipForm.getPayslipMonthId() + adminPaySlipForm.getPayslipPart() + "." + ext;
			pdfName = fileName;
		} else {
			payslip = employeePaySlipLogic.getPaySlipDetails(adminPaySlipForm, companyId, employeeId);
			if (payslip != null) {
				byteFile = employeePaySlipLogic.generatePdf(companyId, payslip.getEmployee().getEmployeeId(), payslip);
				pdfName = createPayslipFileName(payslip);
			}

		}
		if (byteFile == null) {

			return new ResponseEntity<>(
					new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage(
							"payasia.employee.document.payslip.file", new Object[] {}, UserContext.getLocale())),
					HttpStatus.NOT_FOUND);
		}
		Map<String, Object> downloadMap = new HashMap<>();
		downloadMap.put("payslipbody", byteFile);
		downloadMap.put("PayslipFileName", pdfName);
		return new ResponseEntity<>(downloadMap, HttpStatus.OK);

	}

	private String createPayslipFileName(Payslip payslip) {
		String payslipName = payslip.getEmployee().getEmployeeNumber() + "_payslip_" + payslip.getYear();
		if (Long.toString(payslip.getMonthMaster().getMonthId()).length() == 1) {
			payslipName = payslipName + "0" + payslip.getMonthMaster().getMonthId() + payslip.getPart() + ".pdf";
		} else {
			payslipName = payslipName + payslip.getMonthMaster().getMonthId() + payslip.getPart() + ".pdf";
		}
		return payslipName;
	}


	@Override
	@PostMapping("download")
	public ResponseEntity<?> downloadPaySlip(@RequestBody AdminPaySlipForm adminPaySlipForm)
			throws DocumentException, IOException, JAXBException, SAXException {
		byte[] byteFile = null;
		Payslip payslip = null;
		String pdfName = "";
		Long companyId = UserContext.getClientAdminId();
		Long employeeId = Long.parseLong(UserContext.getUserId());
		boolean isPayslipExistinCompanyDocFolder = employeePaySlipLogic
				.getPaySlipStatusFromCompanyDocumentForAdmin(employeeId, companyId, adminPaySlipForm);
		if (isPayslipExistinCompanyDocFolder) {
			byteFile = employeePaySlipLogic.getPaySlipFromCompanyDocumentFolderForAdmin(employeeId, companyId,
					adminPaySlipForm);
			String ext = "pdf";
			String fileName = adminPaySlipForm.getEmployeeNumber() + "_payslip_" + adminPaySlipForm.getPayslipYear()
					+ adminPaySlipForm.getPayslipMonthId() + adminPaySlipForm.getPayslipPart() + "." + ext;
			pdfName = fileName;
		} else {
			payslip = employeePaySlipLogic.getPaySlipDetails(adminPaySlipForm, companyId, employeeId);
			if (payslip != null) {
				byteFile = employeePaySlipLogic.generatePdf(companyId, payslip.getEmployee().getEmployeeId(), payslip);
				pdfName = createPayslipFileName(payslip);
			}

		}
		if (byteFile == null) {

			return new ResponseEntity<>(
					new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage(
							"payasia.employee.document.payslip.file", new Object[] {}, UserContext.getLocale())),
					HttpStatus.NOT_FOUND);
		}
		Map<String, Object> downloadMap = new HashMap<>();
		downloadMap.put("payslipbody", byteFile);
		downloadMap.put("PayslipFileName", pdfName);
		return new ResponseEntity<>(downloadMap, HttpStatus.OK);
	}
	
	@Override
	@PostMapping("print")
	public ResponseEntity<?> printPaySlip(@RequestBody AdminPaySlipForm adminPaySlipForm)
			throws DocumentException, IOException, JAXBException, SAXException {
		byte[] byteFile = null;
		Payslip payslip = null;
		String pdfName = "";
		Long companyId = UserContext.getClientAdminId();
		Long employeeId = Long.parseLong(UserContext.getUserId());
		boolean isPayslipExistinCompanyDocFolder = employeePaySlipLogic
				.getPaySlipStatusFromCompanyDocumentForAdmin(employeeId, companyId, adminPaySlipForm);
		if (isPayslipExistinCompanyDocFolder) {
			byteFile = employeePaySlipLogic.getPaySlipFromCompanyDocumentFolderForAdmin(employeeId, companyId,
					adminPaySlipForm);
			String ext = "pdf";
			String fileName = adminPaySlipForm.getEmployeeNumber() + "_payslip_" + adminPaySlipForm.getPayslipYear()
					+ adminPaySlipForm.getPayslipMonthId() + adminPaySlipForm.getPayslipPart() + "." + ext;
			pdfName = fileName;
		} else {
			payslip = employeePaySlipLogic.getPaySlipDetails(adminPaySlipForm, companyId, employeeId);
			if (payslip != null) {
				byteFile = employeePaySlipLogic.generatePdf(companyId, payslip.getEmployee().getEmployeeId(), payslip);
				pdfName = createPayslipFileName(payslip);
			}

		}
		if (byteFile == null) {

			return new ResponseEntity<>(
					new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage(
							"payasia.employee.document.payslip.file", new Object[] {}, UserContext.getLocale())),
					HttpStatus.NOT_FOUND);
		}
		Map<String, Object> downloadMap = new HashMap<>();
		downloadMap.put("payslipbody", byteFile);
		downloadMap.put("PayslipFileName", pdfName);
		return new ResponseEntity<>(downloadMap, HttpStatus.OK);
	}

	@Override
	@PostMapping("months")
	public ResponseEntity<?> viewMonthAndYearList() {
	  		List<MonthMasterDTO> monthList = generalLogic.getMonthList();
	
		return new ResponseEntity(monthList,HttpStatus.OK);
	}

	@Override
	@PostMapping("employees")
	public ResponseEntity<?> getEmployeeId(@RequestParam(value = "empNumber", required = true) String empNumber) {
		Long companyId = UserContext.getClientAdminId();
		Long employeeId = Long.parseLong(UserContext.getUserId());
		List<AdminPaySlipForm> adminPaySlipFormFormList = employeeDocumentLogic.getEmployeeId(companyId, empNumber,
				employeeId);
		if (adminPaySlipFormFormList.isEmpty()) {
			return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource
					.getMessage("payasia.employee.document.tax", new Object[] {}, UserContext.getLocale())),
					HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(adminPaySlipFormFormList, HttpStatus.OK);
	}


}
