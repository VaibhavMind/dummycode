package com.payasia.web.controller.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.xml.sax.SAXException;

import com.itextpdf.text.DocumentException;
import com.payasia.common.bean.util.UserContext;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.EmployeePaySlipForm;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.dao.bean.Payslip;
import com.payasia.logic.EmployeePaySlipLogic;
import com.payasia.logic.MultilingualLogic;
import com.payasia.web.controller.EmployeePaySlipController;
import com.payasia.web.util.PayAsiaSessionAttributes;

/**
 * The Class EmployeePaySlipControllerImpl.
 */
/**
 * @author vivekjain
 * 
 */
@Controller
@RequestMapping(value = "/employee/employeePayslip")
public class EmployeePaySlipControllerImpl implements EmployeePaySlipController {

	private static final Logger LOGGER = Logger
			.getLogger(EmployeePaySlipControllerImpl.class);

	/** The employee pay slip logic. */
	@Resource
	EmployeePaySlipLogic employeePaySlipLogic;

	/** The message source. */
	@Autowired
	private MessageSource messageSource;

	@Resource
	MultilingualLogic multilingualLogic;

	@Override
	@RequestMapping(value = "/getPaySlipStatus.html", method = RequestMethod.POST)
	@ResponseBody public String getPaySlipStatus(
			ModelMap model,
			@ModelAttribute(value = "employeePaySlipForm") EmployeePaySlipForm employeePaySlipForm,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) {
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		 
		boolean isPayslipReleased = employeePaySlipLogic
				.getPaySlipReleaseDetails(employeeId, companyId,
						employeePaySlipForm);

		if (!isPayslipReleased) {
			try {
				return URLEncoder.encode(messageSource.getMessage(
						"payasia.no.payslip.found.error", new Object[] {},
						locale), "UTF8");
			} catch (UnsupportedEncodingException | NoSuchMessageException exception) {
				LOGGER.error(exception.getMessage(), exception);
				throw new PayAsiaSystemException(exception.getMessage(),
						exception);
			}
		}
		 
		boolean isPayslipExistinCompanyDocFolder = employeePaySlipLogic
				.getPaySlipStatusFromCompanyDocument(employeeId, companyId,
						employeePaySlipForm);
		if (isPayslipExistinCompanyDocFolder) {
			return PayAsiaConstants.PAYASIA_SUCCESS;
		}

		Payslip payslip = employeePaySlipLogic.getPaySlipDetails(employeeId,
				employeePaySlipForm);
		if (payslip != null) {

			String effectiveDateCheck = employeePaySlipLogic
					.checkForEffectiveFrom(companyId, payslip);

			if (effectiveDateCheck != null) {
				try {
					return URLEncoder.encode(messageSource.getMessage(
							effectiveDateCheck, new Object[] {}, locale),
							"UTF8");
				} catch (UnsupportedEncodingException | NoSuchMessageException exception) {
					LOGGER.error(exception.getMessage(), exception);
					throw new PayAsiaSystemException(exception.getMessage(),
							exception);
				}
			}

			return PayAsiaConstants.PAYASIA_SUCCESS;

		} else {
			try {
				return URLEncoder.encode(messageSource.getMessage(
						"payasia.no.payslip.found.error", new Object[] {},
						locale), "UTF8");
			} catch (UnsupportedEncodingException | NoSuchMessageException exception) {
				LOGGER.error(exception.getMessage(), exception);
				throw new PayAsiaSystemException(exception.getMessage(),
						exception);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.web.controller.EmployeePaySlipController#getPaySlip(org.
	 * springframework.ui.ModelMap, com.payasia.common.form.EmployeePaySlipForm,
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/getPaySlip.pdf", method = RequestMethod.GET, produces = "application/pdf")
	public @ResponseBody byte[] getPaySlip(
			ModelMap model,
			@ModelAttribute(value = "employeePaySlipForm") EmployeePaySlipForm employeePaySlipForm,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) throws DocumentException, IOException,
			JAXBException, SAXException {

		 
		UserContext.setLocale(locale);
		Long languageId = multilingualLogic.getLanguageId(locale.toString());
		UserContext.setLanguageId(languageId);

		byte[] byteFile;
		Payslip payslip = null;
		String pdfName = "";
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		 
		boolean isPayslipExistinCompanyDocFolder = employeePaySlipLogic
				.getPaySlipStatusFromCompanyDocument(employeeId, companyId,
						employeePaySlipForm);
		if (isPayslipExistinCompanyDocFolder) {
			byteFile = employeePaySlipLogic
					.getPaySlipFromCompanyDocumentFolder(employeeId, companyId,
							employeePaySlipForm);
			String ext = "pdf";
			String fileName = employeeId + "_payslip_"
					+ employeePaySlipForm.getPayslipYear()
					+ employeePaySlipForm.getPayslipMonthId()
					+ employeePaySlipForm.getPayslipPart() + "." + ext;
			pdfName = fileName;
		} else {
			payslip = employeePaySlipLogic.getPaySlipDetails(employeeId,
					employeePaySlipForm);

			byteFile = employeePaySlipLogic.generatePdf(companyId, employeeId,
					payslip);
			pdfName = createPayslipFileName(payslip);
		}

		response.setContentLength(byteFile.length);

		response.setHeader("Content-Disposition", "inline;filename=" + pdfName);

		return byteFile;

	}

	@Override
	@RequestMapping(value = "/downloadPaySlip.pdf", method = RequestMethod.POST, produces = "application/pdf")
	public @ResponseBody byte[] downloadPaySlip(
			ModelMap model,
			@ModelAttribute(value = "employeePaySlipForm") EmployeePaySlipForm employeePaySlipForm,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) throws DocumentException, IOException,
			JAXBException, SAXException {
		 
		UserContext.setLocale(locale);
		Long languageId = multilingualLogic.getLanguageId(locale.toString());
		UserContext.setLanguageId(languageId);

		byte[] byteFile;
		Payslip payslip = null;
		String pdfName = "";
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		 
		boolean isPayslipExistinCompanyDocFolder = employeePaySlipLogic
				.getPaySlipStatusFromCompanyDocument(employeeId, companyId,
						employeePaySlipForm);
		if (isPayslipExistinCompanyDocFolder) {
			byteFile = employeePaySlipLogic
					.getPaySlipFromCompanyDocumentFolder(employeeId, companyId,
							employeePaySlipForm);
			String ext = "pdf";
			String fileName = employeeId + "_payslip_"
					+ employeePaySlipForm.getPayslipYear()
					+ employeePaySlipForm.getPayslipMonthId()
					+ employeePaySlipForm.getPayslipPart() + "." + ext;
			pdfName = fileName;
		} else {
			payslip = employeePaySlipLogic.getPaySlipDetails(employeeId,
					employeePaySlipForm);

			byteFile = employeePaySlipLogic.generatePdf(companyId, employeeId,
					payslip);
			pdfName = createPayslipFileName(payslip);
		}

		response.setContentLength(byteFile.length);

		response.setHeader("Content-Disposition", "attachment;filename="
				+ pdfName);

		return byteFile;

	}

	private String createPayslipFileName(Payslip payslip) {
		String payslipName = payslip.getEmployee().getEmployeeNumber()
				+ "_payslip_" + payslip.getYear();
		if (Long.toString(payslip.getMonthMaster().getMonthId()).length() == 1) {
			payslipName = payslipName + "0"
					+ payslip.getMonthMaster().getMonthId() + payslip.getPart()
					+ ".pdf";
		} else {
			payslipName = payslipName + payslip.getMonthMaster().getMonthId()
					+ payslip.getPart() + ".pdf";
		}
		return payslipName;
	}

}
