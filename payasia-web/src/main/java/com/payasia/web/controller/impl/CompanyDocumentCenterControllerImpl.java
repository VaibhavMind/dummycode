package com.payasia.web.controller.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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

import com.payasia.common.dto.CompanyDocumentLogDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.CompanyDocumentCenterForm;
import com.payasia.common.form.EmployeeFilterListForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.FileUtils;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.logic.AWSS3Logic;
import com.payasia.logic.CompanyDocumentCenterLogic;
import com.payasia.logic.GeneralLogic;
import com.payasia.web.controller.CompanyDocumentCenterController;
import com.payasia.web.security.XSSRequestWrapper;
import com.payasia.web.util.PayAsiaSessionAttributes;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * The Class CompanyDocumentCenterControllerImpl.
 */
/**
 * @author ragulapraveen
 * 
 */
@Controller
@RequestMapping(value = "/admin/companyDocument")
public class CompanyDocumentCenterControllerImpl implements
		CompanyDocumentCenterController {

	/** The Constant logger. */
	private static final Logger LOGGER = Logger
			.getLogger(CompanyDocumentCenterControllerImpl.class);

	/** The company document center logic. */
	@Resource
	CompanyDocumentCenterLogic companyDocumentCenterLogic;

	@Resource
	GeneralLogic generalLogic;
	/** The message source. */
	@Autowired
	private MessageSource messageSource;

	/** The download path. */
	@Value("#{payasiaptProperties['payasia.home']}")
	private String downloadPath;

	@Value("#{payasiaptProperties['payasia.application.deployment.location']}")
	private String appDeployLocation;

	@Resource
	AWSS3Logic awss3LogicImpl;

	/**
	 * 
	 * Gets the upload form.
	 * 
	 * @return the upload form
	 */
	@RequestMapping(value = "/upload.html", method = RequestMethod.GET)
	@ResponseBody
	public String getUploadForm() {

		CompanyDocumentCenterForm companyDocumentCenterForm = new CompanyDocumentCenterForm();
		String fileName = companyDocumentCenterForm.getFileData()
				.getOriginalFilename();
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(fileName, jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException unsupportedEncodingException) {
			LOGGER.error(unsupportedEncodingException.getMessage(),
					unsupportedEncodingException);
		} finally {
			
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.CompanyDocumentCenterController#uploadDocument
	 * (com.payasia.common.form.CompanyDocumentCenterForm,
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, javax.servlet.http.HttpSession)
	 */
	@Override
	@RequestMapping(value = "/uploadDocument.html", method = RequestMethod.POST, produces = "text/html; charset=utf-8")
	@ResponseBody
	public String uploadDocument(
			@ModelAttribute("companyDocumentCenterForm") CompanyDocumentCenterForm companyDocumentCenterForm,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		try {
		CompanyDocumentCenterForm companyDocumentResponse = new CompanyDocumentCenterForm();

		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		String categoryName = companyDocumentCenterLogic.getDocumentCategoryName(companyDocumentCenterForm.getCategoryId());
		String fileName = companyDocumentCenterForm.getFileData().getOriginalFilename();

		String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1,fileName.length()).toLowerCase();
		Boolean isFileValid = false;
		isFileValid=XSSRequestWrapper.isValidString(companyDocumentCenterForm.getDescription());
	
		if(!isFileValid) {
            throw new PayAsiaSystemException("payasia.record.invalid.data");
		}
		
		
		List<CompanyDocumentLogDTO> companyDocumentLogs = new ArrayList<>();
		if (categoryName.equalsIgnoreCase(PayAsiaConstants.DOCUMENT_CATEGORY_GENERAL)) {
			isFileValid = FileUtils.isValidFile(companyDocumentCenterForm.getFileData(), fileName, PayAsiaConstants.ALLOWED_GENERAL_UPLOAD_FILE_EXT, PayAsiaConstants.ALLOWED_GENERAL_UPLOAD_FILE_MINE_TYPE, PayAsiaConstants.FILE_SIZE);
		}else{
			if (fileExt.equalsIgnoreCase(PayAsiaConstants.FILE_TYPE_ZIP)) {
			   isFileValid = FileUtils.isValidFile(companyDocumentCenterForm.getFileData(), fileName, PayAsiaConstants.ALLOWED_STANDARD_UPLOAD_FILE_EXT,PayAsiaConstants.ALLOWED_STANDARD_UPLOAD_FILE_MINE_TYPE,PayAsiaConstants.FILE_SIZE);;
			}
		}
		if (isFileValid) {
			companyDocumentResponse = companyDocumentCenterLogic
					.uploadDocument(companyDocumentCenterForm, companyId);

			if (companyDocumentResponse.isZipEntriesInvalid()) {
				JsonConfig jsonConfig = new JsonConfig();
				JSONObject jsonObject = JSONObject.fromObject(
						companyDocumentResponse, jsonConfig);
				return jsonObject.toString();
			}
		} else {
			CompanyDocumentLogDTO companyDocumentLogDTO = new CompanyDocumentLogDTO();
			companyDocumentLogDTO.setName(fileName);
			companyDocumentLogDTO.setMessage(PayAsiaConstants.INVALID_FILE);
			companyDocumentLogs.add(companyDocumentLogDTO);
			companyDocumentResponse.setCompanyDocumentLogs(companyDocumentLogs);
		}
		
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(companyDocumentResponse,
				jsonConfig);
		
		return jsonObject.toString();
		}catch (Exception exception) {
			LOGGER.error(exception.getMessage(), exception);
			String ex=exception.getMessage();
			return ex;
		}
	
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.web.controller.CompanyDocumentCenterController#
	 * deleteCompanyDocument(javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, javax.servlet.http.HttpSession,
	 * java.lang.Long)
	 */
	@Override
	@RequestMapping(value = "/deleteCompanyDocument.html", method = RequestMethod.POST)
	@ResponseBody
	public String deleteCompanyDocument(HttpServletRequest request,
			HttpServletResponse response, HttpSession session,
			@RequestParam(value = "docId", required = true) Long docId) {
		CompanyDocumentCenterForm companyDocumentCenterFormResponse = new CompanyDocumentCenterForm();

		/*ID DECRYPT*/
		docId = FormatPreserveCryptoUtil.decrypt(docId);
		
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		companyDocumentCenterLogic.deleteCompanyDocument(docId, companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(
				companyDocumentCenterFormResponse, jsonConfig);
		return jsonObject.toString();

	}

	@Override
	@RequestMapping(value = "/deleteTaxDocuments.html", method = RequestMethod.POST)
	@ResponseBody
	public String deleteTaxDocuments(HttpServletRequest request,
			HttpServletResponse response, HttpSession session,
			@RequestParam(value = "docIds", required = true) Long[] docIds) {

		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		companyDocumentCenterLogic.deleteTaxDocuments(docIds, companyId);
		return "Documents deleted successfully";

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.CompanyDocumentCenterController#searchDocument
	 * (javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, javax.servlet.http.HttpSession,
	 * java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.Long, int, int)
	 */
	@Override
	@RequestMapping(value = "/searchDocument.html", method = RequestMethod.POST)
	@ResponseBody
	public String searchDocument(
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

	//	CompanyDocumentCenterForm companyDocumentCenterFormResponse = companyDocumentCenterLogic
	//			.searchDocument(searchCondition, searchText, pageDTO, sortDTO,
	//					companyId, categoryId, employeeId);
//		companyDocumentCenterFormResponse.setPage(1);
		JsonConfig jsonConfig = new JsonConfig();
		//JSONObject jsonObject = JSONObject.fromObject(
		//		companyDocumentCenterFormResponse, jsonConfig);
	/*	try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException unsupportedEncodingException) {
			LOGGER.error(unsupportedEncodingException.getMessage(),
					unsupportedEncodingException);

		}*/
		return null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.web.controller.CompanyDocumentCenterController#
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

		/*CompanyDocumentCenterForm companyDocumentCenterFormResponse = companyDocumentCenterLogic
				.searchDocumentEmployeeDocumentCenter(searchCondition,
						searchText, pageDTO, sortDTO, companyId, categoryId,
						employeeId);*/
//		companyDocumentCenterFormResponse.setPage(1);
	/*	JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(
				companyDocumentCenterFormResponse, jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException unsupportedEncodingException) {
			LOGGER.error(unsupportedEncodingException.getMessage(),
					unsupportedEncodingException);
		}*/
		return null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.CompanyDocumentCenterController#editUploadedData
	 * (long)
	 */
	@Override
	@RequestMapping(value = "/editUploadedData.html", method = RequestMethod.POST)
	@ResponseBody
	public String editUploadedData(long docId) {

		/*ID DECRYPT*/
		docId = FormatPreserveCryptoUtil.decrypt(docId);
		
		CompanyDocumentCenterForm uploadedDoc = companyDocumentCenterLogic
				.getUploadedDoc(docId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(uploadedDoc, jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException unsupportedEncodingException) {
			LOGGER.error(unsupportedEncodingException.getMessage(),
					unsupportedEncodingException);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.CompanyDocumentCenterController#viewDocument
	 * (long, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/viewDocument", method = RequestMethod.GET)
	public @ResponseBody void viewDocument(
			@RequestParam(value = "docId", required = true) long docId,
			HttpServletResponse response, HttpServletRequest request) {
		
		/*ID DECRYPT*/
		docId = FormatPreserveCryptoUtil.decrypt(docId);
		
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		String filePath = companyDocumentCenterLogic.viewDocument(docId,
				companyId, employeeId);
		BufferedInputStream is;
		if (filePath != null) {
			String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
			FileInputStream fis;
			String fileType = fileName.substring(fileName.lastIndexOf(".") + 1);
			String strFileExt = fileType;
			FileInputStream stream = null;
			try {
				File file = null;
				if (!PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION
						.equalsIgnoreCase(appDeployLocation)) {
					file = new File(filePath);
				} else {
					try {
						String redirectUrl = awss3LogicImpl
								.generateSignedURL(filePath);
						response.sendRedirect(redirectUrl);
					} catch (IOException e) {
						LOGGER.error(e.getMessage(), e);
					}
				}

				if (PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION
						.equalsIgnoreCase(appDeployLocation)
						|| (file != null && file.exists() && !PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION
								.equalsIgnoreCase(appDeployLocation))) {

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
					if (!PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION
							.equalsIgnoreCase(appDeployLocation)) {
						response.setHeader("Content-Length",
								String.valueOf(file.length()));
						fis = new FileInputStream(file);
						is = new BufferedInputStream(fis);

						ServletOutputStream out = response.getOutputStream();
						byte[] buf = new byte[50 * 50 * 1024];
						int bytesRead;
						while ((bytesRead = is.read(buf)) != -1) {
							out.write(buf, 0, bytesRead);
						}
						is.close();
					}
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.CompanyDocumentCenterController#updateDocument
	 * (com.payasia.common.form.CompanyDocumentCenterForm,
	 * org.springframework.validation.BindingResult,
	 * org.springframework.ui.ModelMap, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	@RequestMapping(value = "/updateDocument.html", method = RequestMethod.POST)
	@ResponseBody
	public String updateDocument(
			@ModelAttribute("companyDocumentCenterForm") CompanyDocumentCenterForm companyDocumentCenterForm,
			BindingResult result, ModelMap model, HttpServletRequest request) {

		String success = "true";
		
		try {
			Long companyId = (Long) request.getSession().getAttribute(
					PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

			boolean isFileValid = false;
			
			isFileValid=XSSRequestWrapper.isValidString(companyDocumentCenterForm.getDescription());
			
			if(!isFileValid) {
                throw new PayAsiaSystemException("payasia.record.invalid.data");
			}
			
			if (companyDocumentCenterForm.getFileData()!=null && companyDocumentCenterForm.getFileData().getBytes().length>0) {
				isFileValid = FileUtils.isValidFile(companyDocumentCenterForm.getFileData(), companyDocumentCenterForm.getFileData().getOriginalFilename(), PayAsiaConstants.ALLOWED_GENERAL_UPLOAD_FILE_EXT, PayAsiaConstants.ALLOWED_GENERAL_UPLOAD_FILE_MINE_TYPE, PayAsiaConstants.FILE_SIZE);
			}else{
				
				isFileValid = true;
			}

			if(isFileValid){
				
				/*ID DECRYPT*/
			   companyDocumentCenterForm.setDocId(FormatPreserveCryptoUtil.decrypt(companyDocumentCenterForm.getDocId()));
			   companyDocumentCenterLogic.updateDocument(companyDocumentCenterForm, companyId);
			   success = PayAsiaConstants.TRUE;
			}else{
				success = PayAsiaConstants.FALSE;
			}
		} catch (Exception exception) {
			LOGGER.error(exception.getMessage(), exception);
			success = PayAsiaConstants.FALSE;
		}
		return success;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.web.controller.CompanyDocumentCenterController#
	 * checkCompanyDocument(java.lang.Long, java.lang.String,
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, javax.servlet.http.HttpSession)
	 */
	@Override
	@RequestMapping(value = "/checkCompanyDocument.html", method = RequestMethod.POST)
	@ResponseBody
	public String checkCompanyDocument(
			@RequestParam(value = "categoryId", required = true) Long catergoryId,
			@RequestParam(value = "documentName", required = true) String documentName,
			HttpServletRequest request, HttpServletResponse res,
			HttpSession session) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		CompanyDocumentCenterForm response = companyDocumentCenterLogic
				.checkCompanyDocument(catergoryId, documentName, companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(response, jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException unsupportedEncodingException) {
			LOGGER.error(unsupportedEncodingException.getMessage(),
					unsupportedEncodingException);
		}
		return null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.web.controller.CompanyDocumentCenterController#
	 * getEmployeeFilterList(javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, java.util.Locale)
	 */
	@Override
	@RequestMapping(value = "/employeeFilterList.html", method = RequestMethod.POST)
	@ResponseBody
	public String getEmployeeFilterList(HttpServletRequest request,
			HttpServletResponse response, Locale locale) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		List<EmployeeFilterListForm> filterList = companyDocumentCenterLogic
				.getEmployeeFilterList(companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(filterList, jsonConfig);
		return jsonObject.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.web.controller.CompanyDocumentCenterController#
	 * getEditEmployeeFilterList(java.lang.Long,
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, java.util.Locale)
	 */
	@Override
	@RequestMapping(value = "/editEmployeeFilterList.html", method = RequestMethod.POST)
	@ResponseBody
	public String getEditEmployeeFilterList(
			@RequestParam(value = "documentId", required = true) Long documentId,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) {
		
		/*ID DECRYPT*/
		documentId = FormatPreserveCryptoUtil.decrypt(documentId);
		
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		List<EmployeeFilterListForm> filterList = companyDocumentCenterLogic
				.getEditEmployeeFilterList(documentId, companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(filterList, jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException unsupportedEncodingException) {
			LOGGER.error(unsupportedEncodingException.getMessage(),
					unsupportedEncodingException);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.CompanyDocumentCenterController#deleteFilter
	 * (java.lang.Long)
	 */
	@Override
	@RequestMapping(value = "/deleteFilter.html", method = RequestMethod.POST)
	public void deleteFilter(
			@RequestParam(value = "filterId", required = true) Long filterId) {
		companyDocumentCenterLogic.deleteFilter(filterId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.web.controller.CompanyDocumentCenterController#
	 * saveEmployeeFilterList(java.lang.String, java.lang.Long,
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, java.util.Locale)
	 */
	@Override
	@RequestMapping(value = "/saveEmployeeFilterList.html", method = RequestMethod.POST)
	@ResponseBody
	public String saveEmployeeFilterList(
			@RequestParam(value = "metaData", required = true) String metaData,
			@RequestParam(value = "documentId", required = true) Long documentId,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) {

		String filterStatus= null;

		try {
			
		   final String decodedMetaData = URLDecoder.decode(metaData, "UTF-8"); 
		  
		   /*ID DECRYPT*/
		   documentId = FormatPreserveCryptoUtil.decrypt(documentId);
		   
		   filterStatus = companyDocumentCenterLogic.saveEmployeeFilterList(decodedMetaData, documentId);
		   filterStatus = URLEncoder.encode(messageSource.getMessage(filterStatus, new Object[] {}, locale), "UTF-8");
		} catch (UnsupportedEncodingException unsupportedEncodingException) {
			LOGGER.error(unsupportedEncodingException.getMessage(),
					unsupportedEncodingException);
		} catch (NoSuchMessageException noSuchMessageException) {
			LOGGER.error(noSuchMessageException.getMessage(),
					noSuchMessageException);
		}
		return filterStatus;

	}

}
