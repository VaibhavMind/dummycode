package com.payasia.web.controller.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.payasia.common.bean.util.UserContext;
import com.payasia.common.form.CompanyDocumentCenterForm;
import com.payasia.common.form.EmpDocumentCenterForm;
import com.payasia.common.form.EmpDocumentCentreResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.logic.AWSS3Logic;
import com.payasia.logic.EmpDocumentCenterLogic;
import com.payasia.web.controller.EmpDocumentCenterController;
import com.payasia.web.util.PayAsiaSessionAttributes;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * The Class EmpDocumentCenterControllerImpl.
 */
@Controller
@RequestMapping(value = "/employee/DocumentCenter")
public class EmpDocumentCenterControllerImpl implements
		EmpDocumentCenterController {
	private static final Logger LOGGER = Logger
			.getLogger(EmpDocumentCenterControllerImpl.class);
	/** The emp document center logic. */
	@Resource
	EmpDocumentCenterLogic empDocumentCenterLogic;

	@Resource
	AWSS3Logic awss3LogicImpl;

	/** The download path. */
	@Value("#{payasiaptProperties['payasia.home']}")
	private String downloadPath;

	@Value("#{payasiaptProperties['payasia.application.deployment.location']}")
	private String appDeployLocation;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.EmpDocumentCenterController#searchDocument
	 * (java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * int, int, javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, javax.servlet.http.HttpSession)
	 */
	@Override
	@RequestMapping(value = "/searchDocument.html", method = RequestMethod.POST)
	@ResponseBody
	public String searchDocument(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "searchCondition", required = true) String searchCondition,
			@RequestParam(value = "searchText", required = true) String searchText,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {

		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		EmpDocumentCentreResponse empDocumentCentreResponse = empDocumentCenterLogic
				.searchDocument(searchCondition, searchText, pageDTO, sortDTO,
						employeeId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(
				empDocumentCentreResponse, jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.web.controller.EmpDocumentCenterController#
	 * searchDocumentEmployeeDocumentCenter
	 * (javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, javax.servlet.http.HttpSession,
	 * java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.Long, int, int)
	 */
	@Override
	@RequestMapping(value = "/searchDocumentEmployeeDocumentCenter.html", method = RequestMethod.POST)
	@ResponseBody
	public String searchDocumentEmployeeDocumentCenter(
			HttpServletRequest request,
			HttpServletResponse response,
			HttpSession session,
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "searchCondition", required = true) String searchCondition,
			@RequestParam(value = "searchText", required = true) String searchText,
			@RequestParam(value = "categoryId", required = true) Long categoryId,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows) {

		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		CompanyDocumentCenterForm companyDocumentCenterFormResponse = null;
//		companyDocumentCenterFormResponse= empDocumentCenterLogic
//				.searchDocumentEmployeeDocumentCenter(searchCondition,
//						searchText, pageDTO, sortDTO, companyId, categoryId,
//						employeeId);
//		companyDocumentCenterFormResponse.setPage(1);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(
				companyDocumentCenterFormResponse, jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.EmpDocumentCenterController#uploadDocument
	 * (com.payasia.common.form.EmpDocumentCenterForm,
	 * org.springframework.validation.BindingResult,
	 * org.springframework.ui.ModelMap, javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, javax.servlet.http.HttpSession)
	 */
	@Override
	@RequestMapping(value = "/uploadDocument.html", method = RequestMethod.POST)
	public void uploadDocument(
			@ModelAttribute("employeeDocumentCenterForm") EmpDocumentCenterForm employeeDocumentCenterForm,
			BindingResult result, ModelMap model, HttpServletRequest request,
			HttpServletResponse response, HttpSession session) {

		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		empDocumentCenterLogic.uploadDocument(employeeDocumentCenterForm,
				companyId, employeeId);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.EmpDocumentCenterController#editUploadedData
	 * (long)
	 */
	@Override
	@RequestMapping(value = "/editUploadedData.html", method = RequestMethod.POST)
	@ResponseBody
	public String editUploadedData(long docId) {

		EmpDocumentCenterForm uploadedDoc =null; //empDocumentCenterLogic.getUploadedDoc(docId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(uploadedDoc, jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.web.controller.EmpDocumentCenterController#
	 * downloadEmployeeDocument(long, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/downloadEmployeeDocument", method = RequestMethod.GET)
	public @ResponseBody String downloadEmployeeDocument(
			@RequestParam(value = "docId", required = true) long docId,
			HttpServletResponse response) {
		Long companyID = Long.parseLong(UserContext.getWorkingCompanyId());
		Long employeeId =  Long.parseLong(UserContext.getUserId());
		String filePath = empDocumentCenterLogic.viewDocument(docId,companyID,employeeId);

		String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
		FileInputStream fis;
		String fileType = fileName.substring(fileName.lastIndexOf(".") + 1);
		String strFileExt = fileType;
		FileInputStream stream = null;
		BufferedInputStream is = null;
		try {

			File file = new File(filePath);

			if (file.exists()) {

				if (PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION
						.equalsIgnoreCase(appDeployLocation)) {
					String redirectUrl = awss3LogicImpl
							.generateSignedURL(filePath);
					return "redirect:" + redirectUrl;
				}
				response.setHeader("Pragma", "public");
				response.setHeader("Expires", "0");
				response.setHeader("Cache-Control",
						"must-revalidate, post-check=0, pre-check=0");

				if ("pdf".equalsIgnoreCase(strFileExt)) {
					response.setHeader("Content-Type", "application/pdf");
				} else if ("doc".equalsIgnoreCase(strFileExt)) {
					response.setHeader("Content-Type", "application/msword");
				} else if ("xls".equalsIgnoreCase(strFileExt)) {
					response.setHeader("Content-Type",
							"application/vnd.ms-excel");
				} else if ("ppt".equalsIgnoreCase(strFileExt)) {
					response.setHeader("Content-Type",
							"application/vnd.ms-powerpoint");
				} else if ("docx".equalsIgnoreCase(strFileExt)) {
					response.setHeader("Content-Type",
							"application/vnd.openxmlformats-officedocument.wordprocessingml.document");
				} else if ("pptx".equalsIgnoreCase(strFileExt)) {
					response.setHeader("Content-Type",
							"application/vnd.openxmlformats-officedocument.presentationml.presentation");
				} else if ("xlsx".equalsIgnoreCase(strFileExt)) {
					response.setHeader("Content-Type",
							"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
				} else {
					response.setHeader("Content-Type",
							"application/octet-stream");
				}
				response.setHeader("Content-Transfer-Encoding", "binary");
				response.setHeader("Content-Length",
						String.valueOf(file.length()));
				response.setHeader("Content-Disposition",
						"attachment;filename=" + fileName);

				fis = new FileInputStream(file);

				is = new BufferedInputStream(fis);
				ServletOutputStream out = response.getOutputStream();
				byte[] buf = new byte[50 * 50 * 1024];
				int bytesRead;
				while ((bytesRead = is.read(buf)) != -1) {
					out.write(buf, 0, bytesRead);
				}

			}
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
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

				} catch (IOException e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
		}
		return "";

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.EmpDocumentCenterController#updateDocument
	 * (com.payasia.common.form.EmpDocumentCenterForm,
	 * org.springframework.validation.BindingResult,
	 * org.springframework.ui.ModelMap, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	@RequestMapping(value = "/updateDocument.html", method = RequestMethod.POST)
	@ResponseBody
	public String updateDocument(
			@ModelAttribute("employeeDocumentCenterForm") EmpDocumentCenterForm employeeDocumentCenterForm,
			BindingResult result, ModelMap model, HttpServletRequest request) {

		String success = "true";
		try {
			Long employeeId = (Long) request.getSession().getAttribute(
					PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
			empDocumentCenterLogic.updateDocument(employeeDocumentCenterForm,
					employeeId);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			success = "false";

		}

		return success;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.EmpDocumentCenterController#deleteCompanyDocument
	 * (javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, javax.servlet.http.HttpSession,
	 * java.lang.Long)
	 */
	@Override
	@RequestMapping(value = "/deleteCompanyDocument.html", method = RequestMethod.POST)
	@ResponseBody
	public String deleteCompanyDocument(HttpServletRequest request,
			HttpServletResponse response, HttpSession session,
			@RequestParam(value = "docId", required = true) Long docId) {
		EmpDocumentCenterForm empDocumentCenterForm = new EmpDocumentCenterForm();

		empDocumentCenterLogic.deleteCompanyDocument(docId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(empDocumentCenterForm,
				jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;

	}

	@Override
	@RequestMapping(value = "/viewDocument", method = RequestMethod.GET)
	public @ResponseBody void viewDocument(
			@RequestParam(value = "docId", required = true) long docId,
			HttpServletResponse response, HttpServletRequest request) {
		byte[] buf = null;
		
		/*ID DECRYPT*/
		docId = FormatPreserveCryptoUtil.decrypt(docId); 

		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		String filePath = empDocumentCenterLogic.viewDocument(docId, companyId,
				employeeId);
				

		if (filePath != null) {
			String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
			FileInputStream fis;
			String fileType = fileName.substring(fileName.lastIndexOf(".") + 1);
			String strFileExt = fileType;
			FileInputStream stream = null;
			InputStream awsFileStream = null;
			BufferedInputStream is = null;
			try {
				File file = null;
				if (PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION
						.equalsIgnoreCase(appDeployLocation)) {

					try {
						// awsFileStream = awss3LogicImpl
						// .readS3ObjectAsStream(filePath);

						String redirectUrl = awss3LogicImpl
								.generateSignedURL(filePath);
						response.sendRedirect(redirectUrl);

					} catch (IOException iOException) {
						LOGGER.error(iOException.getMessage(), iOException);
					}

				} else {
					file = new File(filePath);
				}
				if (PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION
						.equalsIgnoreCase(appDeployLocation)
						|| (file != null && file.exists())) {

					response.setHeader("Pragma", "public");
					response.setHeader("Expires", "0");
					response.setHeader("Cache-Control",
							"must-revalidate, post-check=0, pre-check=0");

					switch (strFileExt.toLowerCase()) {
					case "pdf":
						response.setHeader("Content-Type", "application/pdf");
						break;
					case "doc":
						response.setHeader("Content-Type", "application/msword");
						break;
					case "xls":
						response.setHeader("Content-Type",
								"application/vnd.ms-excel");
						break;
					case "ppt":
						response.setHeader("Content-Type",
								"application/vnd.ms-powerpoint");
						break;
					case "docx":
						response.setHeader("Content-Type",
								"application/vnd.openxmlformats-officedocument.wordprocessingml.document");
						break;
					case "pptx":
						response.setHeader("Content-Type",
								"application/vnd.openxmlformats-officedocument.presentationml.presentation");
						break;
					case "xlsx":
						response.setHeader("Content-Type",
								"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
						break;
					default:
						response.setHeader("Content-Type",
								"application/octet-stream");
						break;
					}

					response.setHeader("Content-Transfer-Encoding", "binary");
					response.setHeader("Content-Disposition",
							"attachment;filename=" + fileName);
					/*if (PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION
							.equalsIgnoreCase(appDeployLocation)) {
						is = new BufferedInputStream(awsFileStream);
					} */if (!PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION
							.equalsIgnoreCase(appDeployLocation)) {
						response.setHeader("Content-Length",
								String.valueOf(file.length()));
						fis = new FileInputStream(file);
						is = new BufferedInputStream(fis);
						ServletOutputStream out = response.getOutputStream();
						buf = new byte[50 * 50 * 1024];
						int bytesRead;
						while ((bytesRead = is.read(buf)) != -1) {
							out.write(buf, 0, bytesRead);
						}
						is.close();
					}
					/*
					 * BufferedInputStream is1 = new
					 * BufferedInputStream(awsFileStream); BufferedInputStream
					 * is = new BufferedInputStream(fis);
					 */

				}
			} catch (IOException iOException) {
				LOGGER.error(iOException.getMessage(), iOException);
			} finally {
				if (stream != null) {
					try {
						stream.close();
					} catch (IOException iOException) {
						LOGGER.error(iOException.getMessage(), iOException);
					}
				}
			}
		}
	}

}
