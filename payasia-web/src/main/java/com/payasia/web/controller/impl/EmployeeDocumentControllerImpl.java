/**
 * @author abhisheksachdeva
 * 
 */
package com.payasia.web.controller.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.bind.JAXBException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.xml.sax.SAXException;

import com.itextpdf.text.DocumentException;
import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.PayslipErrorLog;
import com.payasia.common.dto.PrintTokenDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.AdminPaySlipForm;
import com.payasia.common.form.EmployeeFilterListForm;
import com.payasia.common.form.EmployeeTaxDocumentForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.PartsForm;
import com.payasia.common.form.PayslipRes;
import com.payasia.common.form.SortCondition;
import com.payasia.common.form.WorkFlowDelegateResponse;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.dao.bean.Payslip;
import com.payasia.logic.AWSS3Logic;
import com.payasia.logic.EmployeeDetailLogic;
import com.payasia.logic.EmployeeDocumentLogic;
import com.payasia.logic.EmployeePaySlipLogic;
import com.payasia.logic.MultilingualLogic;
import com.payasia.web.controller.EmployeeDocumentController;
import com.payasia.web.util.PayAsiaSessionAttributes;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * The Class EmployeeDocumentControllerImpl.
 */
@Controller
@RequestMapping(value = { "/employee/empDocs", "/admin/empDocs" })
public class EmployeeDocumentControllerImpl implements
		EmployeeDocumentController {

	/** The download path. */
	@Value("#{payasiaptProperties['payasia.home']}")
	private String downloadPath;

	/** The employee document logic. */
	@Resource
	EmployeeDocumentLogic employeeDocumentLogic;

	/** The employee pay slip logic. */
	@Resource
	EmployeePaySlipLogic employeePaySlipLogic;

	/** The message source. */
	@Autowired
	private MessageSource messageSource;

	private static final Logger LOGGER = Logger
			.getLogger(EmployeeDocumentControllerImpl.class);

	@Resource
	EmployeeDetailLogic employeeDetailLogic;

	@Resource
	MultilingualLogic multilingualLogic;

	@Value("#{payasiaptProperties['payasia.application.deployment.location']}")
	private String appDeployLocation;

	@Resource
	AWSS3Logic awss3LogicImpl;
	
	@Autowired
	private ServletContext servletContext;

	@Override
	@RequestMapping(value = "/getPaySlipStatus.html", method = RequestMethod.POST)
	@ResponseBody public String getPaySlipStatus(
			@ModelAttribute("adminPaySlipForm") AdminPaySlipForm adminPaySlipForm,
			BindingResult result, ModelMap model, HttpServletRequest request,
			HttpServletResponse response, HttpSession session, Locale locale) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		Long companyID = Long.parseLong(UserContext.getWorkingCompanyId());
		 
		try {
			adminPaySlipForm.setEmployeeNumber(URLDecoder.decode(
					adminPaySlipForm.getEmployeeNumber(), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
         
		Long empId =    employeeDetailLogic.getEmpIdByEmpNoAndComId(adminPaySlipForm.getEmployeeNumber(),companyID);
		
		if(empId!=null)
		{
			
		if(employeeId.equals(empId))	
		{
		boolean isPayslipExistinCompanyDocFolder = employeePaySlipLogic
				.getPaySlipStatusFromCompanyDocumentForAdmin(employeeId,
						companyId, adminPaySlipForm);
		if (isPayslipExistinCompanyDocFolder) {
			return PayAsiaConstants.PAYASIA_SUCCESS;
		}

		Payslip payslip = employeePaySlipLogic.getPaySlipDetails(
				adminPaySlipForm, companyId, employeeId);

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
		else
		{
			Long userId =Long.parseLong(UserContext.getUserId());
		boolean	isAuthorized=	employeeDetailLogic.isAdminAuthorizedForEmployee(empId, companyID,userId);
		if(isAuthorized)
		{
			boolean isPayslipExistinCompanyDocFolder = employeePaySlipLogic
					.getPaySlipStatusFromCompanyDocumentForAdmin(employeeId,
							companyId, adminPaySlipForm);
			if (isPayslipExistinCompanyDocFolder) {
				return PayAsiaConstants.PAYASIA_SUCCESS;
			}

			Payslip payslip = employeePaySlipLogic.getPaySlipDetails(
					adminPaySlipForm, companyId, employeeId);

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
		else
		{
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
		}
		else
		{
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

	@Override
	@RequestMapping(value = "/getPaySlipStatusForPrint.html", method = RequestMethod.POST)
	@ResponseBody public String getPaySlipStatusForPrint(Long employeeId,
			Integer year, Long month, Integer part, Long companyId,
			Long sessionEmployeeId, HttpServletRequest request, Locale locale) {
		Payslip payslip = employeePaySlipLogic.getPaySlipDetailsForPrint(
				companyId, sessionEmployeeId, employeeId, year, month, part);

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
	 * @see
	 * com.payasia.web.controller.EmployeeDocumentController#getPaySlip(com.
	 * payasia.common.form.AdminPaySlipForm,
	 * org.springframework.validation.BindingResult,
	 * org.springframework.ui.ModelMap, javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, javax.servlet.http.HttpSession,
	 * java.util.Locale)
	 */
	@Override
	@RequestMapping(value = "/generatePayslip.pdf", method = RequestMethod.GET, produces = "application/pdf")
	public @ResponseBody byte[] getPaySlip(
			@ModelAttribute("adminPaySlipForm") AdminPaySlipForm adminPaySlipForm,
			BindingResult result, ModelMap model, HttpServletRequest request,
			HttpServletResponse response, HttpSession session, Locale locale)
			throws DocumentException, IOException, JAXBException, SAXException {

		 
		UserContext.setLocale(locale);
		Long languageId = multilingualLogic.getLanguageId(locale.toString());
		UserContext.setLanguageId(languageId);

		byte[] byteFile;
		Payslip payslip = null;
		String pdfName = "";
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		 
		try {
			adminPaySlipForm.setEmployeeNumber(URLDecoder.decode(
					adminPaySlipForm.getEmployeeNumber(), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}

		 
		boolean isPayslipExistinCompanyDocFolder = employeePaySlipLogic
				.getPaySlipStatusFromCompanyDocumentForAdmin(employeeId,
						companyId, adminPaySlipForm);
		if (isPayslipExistinCompanyDocFolder) {
			byteFile = employeePaySlipLogic
					.getPaySlipFromCompanyDocumentFolderForAdmin(employeeId,
							companyId, adminPaySlipForm);
			String ext = "pdf";
			String fileName = adminPaySlipForm.getEmployeeNumber()
					+ "_payslip_" + adminPaySlipForm.getPayslipYear()
					+ adminPaySlipForm.getPayslipMonthId()
					+ adminPaySlipForm.getPayslipPart() + "." + ext;
			pdfName = fileName;
		} else {
			payslip = employeePaySlipLogic.getPaySlipDetails(adminPaySlipForm,
					companyId, employeeId);

			byteFile = employeePaySlipLogic.generatePdf(companyId, payslip
					.getEmployee().getEmployeeId(), payslip);
			pdfName = createPayslipFileName(payslip);
		}

		response.setContentLength(byteFile.length);

		response.setHeader("Content-Disposition", "inline;filename=" + pdfName);

		return byteFile;

	}

	@Override
	@RequestMapping(value = "/generatePayslipForPrint.pdf", method = RequestMethod.GET, produces = "application/pdf")
	public @ResponseBody byte[] generatePayslipForPrint(Long employeeId,
			Integer year, Long month, Integer part, Long companyId,
			Long sessionEmployeeId, HttpServletRequest request,
			HttpServletResponse response, Locale locale)
			throws DocumentException, IOException, JAXBException, SAXException {
		 
		UserContext.setLocale(locale);
		Long languageId = multilingualLogic.getLanguageId(locale.toString());
		UserContext.setLanguageId(languageId);

		Payslip payslip = employeePaySlipLogic.getPaySlipDetailsForPrint(
				companyId, sessionEmployeeId, employeeId, year, month, part);

		byte[] byteFile = employeePaySlipLogic.generatePdf(companyId, payslip
				.getEmployee().getEmployeeId(), payslip);

		response.setContentLength(byteFile.length);
		String pdfName = createPayslipFileName(payslip);

		response.setHeader("Content-Disposition", "inline;filename=" + pdfName);

		return byteFile;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.EmployeeDocumentController#getTaxDocuments
	 * (java.lang.Long, javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, javax.servlet.http.HttpSession)
	 */
	@Override
	@RequestMapping(value = "/generateTaxDocuments", method = RequestMethod.GET)
	public void getTaxDocuments(
			@RequestParam(value = "documentId", required = true) Long documentId,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		String filePath = employeeDocumentLogic.generateTaxDocument(documentId);
		FileInputStream fis;
		FileInputStream stream = null;
		BufferedInputStream is = null;
		InputStream inputStream = null;
		try {
			File file = null;
			if (!PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION
					.equalsIgnoreCase(appDeployLocation)) {
				file = new File(filePath);
				fis = new FileInputStream(file);
				is = new BufferedInputStream(fis);
				response.setHeader("Content-Length",
						String.valueOf(file.length()));
			} else {
				try {
					inputStream = awss3LogicImpl.readS3ObjectAsStream(filePath);
					is = new BufferedInputStream(inputStream);
				} catch (IOException e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
			if (PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION
					.equalsIgnoreCase(appDeployLocation) || file.exists()) {

				response.setHeader("Pragma", "public");
				response.setHeader("Expires", "0");
				response.setHeader("Cache-Control",
						"must-revalidate, post-check=0, pre-check=0");

				response.setHeader("Content-Type", "application/pdf");
				response.setHeader("Content-Transfer-Encoding", "binary");

				ServletOutputStream out = response.getOutputStream();
				byte[] buf = new byte[50 * 50 * 1024];
				int bytesRead;
				while ((bytesRead = is.read(buf)) != -1) {
					out.write(buf, 0, bytesRead);
				}

			}
		} catch (IOException exception) {
			LOGGER.error(exception.getMessage(), exception);
			throw new PayAsiaSystemException(exception.getMessage(), exception);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException exception) {
					LOGGER.error(exception.getMessage(), exception);
					throw new PayAsiaSystemException(exception.getMessage(),
							exception);
				}
			}
		}
	}

	@Override
	@RequestMapping(value = "/downloadTaxDocuments", method = RequestMethod.GET, produces = "application/pdf")
	public @ResponseBody byte[] downloadTaxDocuments(
			@RequestParam(value = "documentId", required = true) Long documentId,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session) throws DocumentException, IOException,
			JAXBException, SAXException {

		String filePath = employeeDocumentLogic.generateTaxDocument(documentId);
		byte[] byteFile;
		if (PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION
				.equalsIgnoreCase(appDeployLocation)) {
			byteFile = IOUtils.toByteArray(awss3LogicImpl
					.readS3ObjectAsStream(filePath));
		} else {
			File pdfFile = new File(filePath);
			InputStream pdfIn = null;

			try {
				pdfIn = new FileInputStream(pdfFile);
			} catch (FileNotFoundException ex) {
				LOGGER.error(ex.getMessage(), ex);
				throw new PayAsiaSystemException(ex.getMessage(), ex);
			}
			byteFile = IOUtils.toByteArray(pdfIn);
		}
		String pdfName = filePath.substring(filePath.lastIndexOf("/") + 1,
				filePath.length()).toLowerCase();// pdfFile.getName() + ".pdf";
		response.setContentLength(byteFile.length);
		response.setHeader("Content-Disposition", "attachment;filename="
				+ pdfName);
		return byteFile;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.EmployeeDocumentController#getTaxDocumentList
	 * (java.lang.String, int, javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, javax.servlet.http.HttpSession)
	 */
	@Override
	@RequestMapping(value = "/getTaxDocumentList.html", method = RequestMethod.POST)
	@ResponseBody public String getTaxDocumentList(
			@RequestParam(value = "employeeNumber") String employeeNumber,
			@RequestParam(value = "year") int year, HttpServletRequest request,
			HttpServletResponse response, HttpSession session, Locale locale) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		EmployeeTaxDocumentForm employeeTaxDocumentForm = employeeDocumentLogic
				.getTaxDocumentList(employeeNumber, year, companyId, employeeId);
		try {
			if (employeeTaxDocumentForm.getVerifyResponse() != null) {
				if (employeeTaxDocumentForm.getMessageParam() != null) {

					employeeTaxDocumentForm
							.setVerifyResponse(URLEncoder.encode(
									messageSource.getMessage(
											employeeTaxDocumentForm
													.getVerifyResponse(),
											employeeTaxDocumentForm
													.getMessageParam(), locale),
									"UTF-8"));

				} else {
					employeeTaxDocumentForm
							.setVerifyResponse(URLEncoder.encode(messageSource
									.getMessage(employeeTaxDocumentForm
											.getVerifyResponse(),
											new Object[] {}, locale), "UTF-8"));
				}
			}
		} catch (UnsupportedEncodingException | NoSuchMessageException exception) {
			LOGGER.error(exception.getMessage(), exception);
			throw new PayAsiaSystemException(exception.getMessage(), exception);
		}
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(employeeTaxDocumentForm,
				jsonConfig);
		return jsonObject.toString();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.EmployeeDocumentController#getEmployeeId(java
	 * .lang.String, javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/getEmployeeId.html", method = RequestMethod.POST)
	@ResponseBody public String getEmployeeId(
			@RequestParam(value = "searchString", required = true) String searchString,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		List<AdminPaySlipForm> adminPaySlipFormFormList = employeeDocumentLogic
				.getEmployeeId(companyId, searchString, employeeId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(adminPaySlipFormFormList,
				jsonConfig);
		return jsonObject.toString();

	}

	@Override
	@RequestMapping(value = "/searchEmployee.html", method = RequestMethod.POST)
	@ResponseBody public String searchEmployee(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			@RequestParam(value = "empName", required = true) String empName,
			@RequestParam(value = "empNumber", required = true) String empNumber,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		WorkFlowDelegateResponse employeeResponse = employeeDocumentLogic
				.searchEmployee(pageDTO, sortDTO, empName, empNumber,
						companyId, employeeId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(employeeResponse,
				jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return null;
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


	@Override
	@RequestMapping(value = "/applet/generatePrintTokenPaySlips.html", method = RequestMethod.GET)
	public @ResponseBody byte[] generatePrintTokenPaySlips(
			@RequestParam(value = "employeeIndex", required = false) String employeeIndex,
			@RequestParam(value = "printTokenKey", required = false) String printTokenKey,
			@RequestParam(value = "loggedInCompanyId", required = false) String loggedInCompanyId,
			@RequestParam(value = "loggedInEmployeeId", required = false) String loggedInEmployeeId,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) throws NumberFormatException, IOException {
		 
		UserContext.setLocale((Locale) servletContext
				.getAttribute("PAYASIA_PAYSLIP_PRINT_LOCALE"));

		byte[] byteFile;
		Payslip payslip = null;
		String pdfName = "";
		Long lempId = Long.parseLong(loggedInEmployeeId);
		Long lcmpId = Long.parseLong(loggedInCompanyId);

		HashMap<String, PrintTokenDTO> printToken = (HashMap<String, PrintTokenDTO>) servletContext.getAttribute(
						PayAsiaConstants.PAYSLIP_TOKEN);
		PrintTokenDTO printTokenDTO = printToken.get(printTokenKey);

		Long payslipEmployeeId = printTokenDTO.getEmployeeIds().get(
				Integer.parseInt(employeeIndex));

		if (payslipEmployeeId == null) {
			return null;
		}

		 
		boolean isPayslipExistinCompanyDocFolder = employeePaySlipLogic
				.getPaySlipStatusFromCompanyDocumentForPrint(payslipEmployeeId,
						lcmpId, printTokenDTO.getYear(), printTokenDTO
								.getPart(), Long.parseLong(String
								.valueOf(printTokenDTO.getMonth())));
		if (isPayslipExistinCompanyDocFolder) {
			byteFile = employeePaySlipLogic
					.getPaySlipFromCompanyDocumentFolderForPrint(
							payslipEmployeeId, lcmpId, printTokenDTO.getYear(),
							printTokenDTO.getPart(), Long.parseLong(String
									.valueOf(printTokenDTO.getMonth())));
			String ext = "pdf";
			String fileName = payslipEmployeeId + "_payslip_"
					+ printTokenDTO.getYear()
					+ Long.parseLong(String.valueOf(printTokenDTO.getMonth()))
					+ printTokenDTO.getPart() + "." + ext;
			pdfName = fileName;
			response.setContentLength(byteFile.length);

			response.setHeader("Content-Disposition", "inline;filename="
					+ pdfName);
			return byteFile;
		} else {
			payslip = employeePaySlipLogic.getPaySlipDetailsForPrint(lcmpId,
					lempId, payslipEmployeeId, printTokenDTO.getYear(),
					Long.parseLong(String.valueOf(printTokenDTO.getMonth())),
					printTokenDTO.getPart());

			Boolean paySlipExists = false;

			if (payslip != null) {

				String effectiveDateCheck = employeePaySlipLogic
						.checkForEffectiveFrom(lcmpId, payslip);

				if (effectiveDateCheck != null) {
					paySlipExists = false;
				} else {
					paySlipExists = true;
				}

			}

			if (paySlipExists) {
				byteFile = null;
				try {
					byteFile = employeePaySlipLogic.generatePdf(lcmpId, payslip
							.getEmployee().getEmployeeId(), payslip);
				} catch (DocumentException e) {
					LOGGER.error(e.getMessage(), e);
				} catch (IOException e) {
					LOGGER.error(e.getMessage(), e);
				} catch (JAXBException e) {
					LOGGER.error(e.getMessage(), e);
				} catch (SAXException e) {
					LOGGER.error(e.getMessage(), e);
				}

				response.setContentLength(byteFile.length);
				pdfName = createPayslipFileName(payslip);

				response.setHeader("Content-Disposition", "inline;filename="
						+ pdfName);

				return byteFile;
			}
		}

		return null;

	}

	@Override
	@RequestMapping(value = "/getEmployeeFilterList.html", method = RequestMethod.POST)
	@ResponseBody public String getEmployeeFilterList(
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		List<EmployeeFilterListForm> filterList = employeePaySlipLogic
				.getEmployeeFilterList(companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(filterList, jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/searchEmployeeListForPrint.html", method = RequestMethod.POST)
	@ResponseBody public String searchEmployeeListForPrint(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			@RequestParam(value = "empName", required = true) String empName,
			@RequestParam(value = "empNumber", required = true) String empNumber,
			@RequestParam(value = "isShortList", required = true) boolean isShortList,
			@RequestParam(value = "metaData", required = false) String metaData,
			HttpServletRequest request, HttpServletResponse response) {
		try {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long sessionEmployeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		//String decodedmetadata=URLDecoder.decode(metaData,"UTF-8");
		
		WorkFlowDelegateResponse workFlowResponse = employeePaySlipLogic
				.searchEmployeeListForPrint(pageDTO, sortDTO,
						sessionEmployeeId, empName, empNumber, companyId,
						isShortList, metaData);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(workFlowResponse,
				jsonConfig);

			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/parts.html", method = RequestMethod.POST)
	@ResponseBody public String generateParts(
			@RequestParam(value = "year", required = true) Integer year,
			@RequestParam(value = "month", required = true) Long month,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		PartsForm partForm = employeePaySlipLogic.getParts(year, month,
				companyId, employeeId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(partForm, jsonConfig);
		return jsonObject.toString();

	}

	@Override
	@RequestMapping(value = "/getPayslipPartDetailsForAdmin.html", method = RequestMethod.POST)
	@ResponseBody public String getPayslipPartDetailsForAdmin(
			@RequestParam(value = "employeeNumber", required = true) String employeeNumber,
			@RequestParam(value = "year", required = true) Integer year,
			@RequestParam(value = "monthId", required = true) Long monthId,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long loogedInEmployeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		PartsForm partForm = employeePaySlipLogic
				.getPayslipPartDetailsForAdmin(employeeNumber, year, monthId,
						companyId, loogedInEmployeeId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(partForm, jsonConfig);
		return jsonObject.toString();

	}

	@Override
	@RequestMapping(value = "/sendPayslips.html", method = RequestMethod.POST)
	@ResponseBody public String sendPayslips(
			@RequestParam(value = "employeeList", required = true) String[] employeeList,
			@RequestParam(value = "year", required = true) Integer year,
			@RequestParam(value = "month", required = true) Integer month,
			@RequestParam(value = "part", required = true) Integer part,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) {
		 
		UserContext.setLocale(locale);
		Long languageId = multilingualLogic.getLanguageId(locale.toString());
		UserContext.setLanguageId(languageId);

		Long lEmployeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		PayslipRes payslipRes = new PayslipRes();
		List<PayslipErrorLog> errors = new ArrayList<>();
		String returnStr = "";
		Integer errorCount = 1;
		String empName = "";
		Long userId =Long.parseLong(UserContext.getUserId());
		Long companyID = Long.parseLong(UserContext.getWorkingCompanyId());
List<String> employeeNoList = employeeDetailLogic.getAuthorizedEmployeeList(Arrays.asList(employeeList), companyID, userId);
if(employeeNoList!=null && !employeeNoList.isEmpty())
{
	
		for (String employeeId : employeeNoList) {
			try {
				String pdfName = "";
				Long empId = Long.parseLong(employeeId);
				empName = employeeDetailLogic.getEmployeeNameWithNumber(empId);
				Long companyId = (Long) request.getSession().getAttribute(
						PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
				Payslip payslip = null;
				byte[] byteFile = null;
				 
				boolean isPayslipExistinCompanyDocFolder = employeePaySlipLogic
						.getPaySlipStatusFromCompanyDocumentForPrint(empId,
								companyId, year, part,
								Long.parseLong(String.valueOf(month)));
				if (isPayslipExistinCompanyDocFolder) {
					byteFile = employeePaySlipLogic
							.getPaySlipFromCompanyDocumentFolderForPrint(empId,
									companyId, year, part,
									Long.parseLong(String.valueOf(month)));
					String ext = "pdf";
					String fileName = employeeId + "_payslip_" + year
							+ Long.parseLong(String.valueOf(month)) + part
							+ "." + ext;
					pdfName = fileName;
					response.setContentLength(byteFile.length);

					response.setHeader("Content-Disposition",
							"inline;filename=" + pdfName);

				} else {
					payslip = employeePaySlipLogic.getPaySlipDetailsForPrint(
							companyId, lEmployeeId, empId, year,
							Long.parseLong(String.valueOf(month)), part);

					Boolean paySlipExists = false;

					if (payslip != null) {

						String effectiveDateCheck = employeePaySlipLogic
								.checkForEffectiveFrom(companyId, payslip);

						if (effectiveDateCheck != null) {
							paySlipExists = false;
						} else {
							paySlipExists = true;
						}

					}

					if (paySlipExists) {
						byteFile = null;
						try {
							byteFile = employeePaySlipLogic.generatePdf(
									companyId, payslip.getEmployee()
											.getEmployeeId(), payslip);
						} catch (DocumentException | IOException
								| JAXBException | SAXException exception) {
							LOGGER.error(exception.getMessage(), exception);
							throw new PayAsiaSystemException(
									exception.getMessage(), exception);

						}

						response.setContentLength(byteFile.length);
						pdfName = createPayslipFileName(payslip);

						response.setHeader("Content-Disposition",
								"inline;filename=" + pdfName);

					}
				}

				if (byteFile != null) {
					returnStr = employeePaySlipLogic.savePayslipEmail(byteFile,
							empId, companyId, month, year, part);
				} else {
					PayslipErrorLog error = new PayslipErrorLog();
					error.setId(errorCount);
					error.setMessage(messageSource.getMessage(
							"payasia.payslip.not.found", new Object[] {},
							locale)
							+ empName);
					errorCount++;
					errors.add(error);
				}

				if (StringUtils.isNotBlank(returnStr)) {
					PayslipErrorLog error = new PayslipErrorLog();
					error.setId(errorCount);
					error.setMessage(messageSource.getMessage(returnStr,
							new Object[] {}, locale));
					errorCount++;
					errors.add(error);

				}

			} catch (Exception exception) {
				LOGGER.error(exception.getMessage(), exception);
				PayslipErrorLog error = new PayslipErrorLog();
				error.setId(errorCount);
				error.setMessage(messageSource.getMessage(
						"payasia.payslip.not.found", new Object[] {}, locale)
						+ empName);
				errorCount++;
				errors.add(error);
			}

		}
}
		payslipRes.setErrors(errors);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(payslipRes, jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/downloadPaySlip.pdf", method = RequestMethod.POST, produces = "application/pdf")
	public @ResponseBody byte[] downloadPaySlip(
			ModelMap model,
			@ModelAttribute("adminPaySlipForm") AdminPaySlipForm adminPaySlipForm,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) throws DocumentException, IOException,
			JAXBException, SAXException {
		 
		UserContext.setLocale(locale);
		Long languageId = multilingualLogic.getLanguageId(locale.toString());
		UserContext.setLanguageId(languageId);

		 
		try {
			adminPaySlipForm.setEmployeeNumber(URLDecoder.decode(
					adminPaySlipForm.getEmployeeNumber(), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}

		byte[] byteFile;
		Payslip payslip = null;
		String pdfName = "";
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		 
		boolean isPayslipExistinCompanyDocFolder = employeePaySlipLogic
				.getPaySlipStatusFromCompanyDocumentForAdmin(employeeId,
						companyId, adminPaySlipForm);

		if (isPayslipExistinCompanyDocFolder) {
			byteFile = employeePaySlipLogic
					.getPaySlipFromCompanyDocumentFolderForAdmin(employeeId,
							companyId, adminPaySlipForm);
			String ext = "pdf";
			String fileName = adminPaySlipForm.getEmployeeNumber()
					+ "_payslip_" + adminPaySlipForm.getPayslipYear()
					+ adminPaySlipForm.getPayslipMonthId()
					+ adminPaySlipForm.getPayslipPart() + "." + ext;
			pdfName = fileName;
		} else {
			payslip = employeePaySlipLogic.getPaySlipDetails(adminPaySlipForm,
					companyId, employeeId);

			byteFile = employeePaySlipLogic.generatePdf(companyId, payslip
					.getEmployee().getEmployeeId(), payslip);
			pdfName = createPayslipFileName(payslip);
		}

		response.setContentLength(byteFile.length);

		response.setHeader("Content-Disposition", "attachment;filename="
				+ pdfName);

		return byteFile;

	}

}
