package com.payasia.web.controller.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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

import com.payasia.common.dto.DataImportLogDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.EmployeeListForm;
import com.payasia.common.form.HRISReviewerForm;
import com.payasia.common.form.LionTimesheetReviewerForm;
import com.payasia.common.form.LundinReviewerResponseForm;
import com.payasia.common.form.LundinTimesheetReviewerForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.FileUtils;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.logic.LionTimesheetReviewerLogic;
import com.payasia.web.util.PayAsiaSessionAttributes;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

@Controller
/*@RequestMapping(value = { "/employee/lionTimesheetReviewer",
		"/admin/lionTimesheetReviewer" })*/
public class LionTimesheetReviewerControllerImpl implements
		LionTimesheetReviewerController {

	/** The Constant logger. */
	private static final Logger LOGGER = Logger
			.getLogger(LionTimesheetReviewerControllerImpl.class);

	@Resource
	LionTimesheetReviewerLogic lionTimesheetReviewerLogic;

	@Autowired
	private MessageSource messageSource;

	@Override
	@RequestMapping(value = "/admin/lionTimesheetReviewer/viewLionReviewers.html", method = RequestMethod.GET)
	@ResponseBody
	public String viewLionReviewers(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "searchCondition", required = true) String searchCondition,
			@RequestParam(value = "searchText", required = true) String searchText,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		LundinReviewerResponseForm lundinReviewerFormResponse = lionTimesheetReviewerLogic
				.getLionReviewers(searchCondition, searchText, pageDTO,
						sortDTO, companyId);
		// CipherUtility.setIndirectReferenceForList(
		// lundinReviewerFormResponse.getRows(), "employeeId");
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(
				lundinReviewerFormResponse, jsonConfig);

		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return null;

	}

	@Override
	@RequestMapping(value = "/admin/lionTimesheetReviewer/getLionTypeWorkFlow.html", method = RequestMethod.POST)
	@ResponseBody
	public String getLionTypeWorkFlow(HttpServletRequest request,
			HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		LundinTimesheetReviewerForm lundinReviewerForm = lionTimesheetReviewerLogic
				.getLionWorkFlow(companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(lundinReviewerForm,
				jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/admin/lionTimesheetReviewer/getLionReviewerData.html", method = RequestMethod.POST)
	@ResponseBody
	public String getLionReviewerData(
			@RequestParam(value = "employeeId", required = true) Long employeeId,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		/*ID DECRYPT*/
		employeeId = FormatPreserveCryptoUtil.decrypt(employeeId);
		// employeeId = (long) CipherUtility.getDecryptedValue(employeeId);
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		LundinReviewerResponseForm lundinReviewerForm = lionTimesheetReviewerLogic
				.getLionReviewerData(employeeId, companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(lundinReviewerForm,
				jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/admin/lionTimesheetReviewer/saveLionReviewer.html", method = RequestMethod.POST)
	@ResponseBody
	public String saveLundinReviewer(
			@ModelAttribute("lundinTimesheetReviewerForm") LionTimesheetReviewerForm lionTimesheetReviewerForm,
			ModelMap model, HttpServletRequest request) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
       
		/*ID DECRYPT*/
    	lionTimesheetReviewerForm.setEmployeeId(FormatPreserveCryptoUtil.decrypt(lionTimesheetReviewerForm.getEmployeeId()));
    	lionTimesheetReviewerForm.setLundinReviewerId1(FormatPreserveCryptoUtil.decrypt(lionTimesheetReviewerForm.getLundinReviewerId1()));
		Long employeeId = lionTimesheetReviewerForm.getEmployeeId();
		LionTimesheetReviewerForm hrisReviewerFormResponse = lionTimesheetReviewerLogic
				.checkLionReviewer(employeeId, companyId);
		if (hrisReviewerFormResponse.getEmployeeStatus().equalsIgnoreCase(
				"notexists")) {

			try {
                boolean flag=reviwerIdCheck(employeeId,lionTimesheetReviewerForm.getLundinReviewerId1());
                if(flag) {
                	
				lionTimesheetReviewerLogic.saveLionReviewer(
						lionTimesheetReviewerForm, companyId);
				hrisReviewerFormResponse.setStatus("success");
                }
                else {
                	hrisReviewerFormResponse.setStatus("failure");
                }

			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
				hrisReviewerFormResponse.setStatus("failure");
			}
		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(hrisReviewerFormResponse,
				jsonConfig);
		return jsonObject.toString();
	}



	@Override
	@RequestMapping(value = "/admin/lionTimesheetReviewer/updateLionReviewer.html", method = RequestMethod.POST)
	@ResponseBody
	public String updateLundinReviewer(
			@ModelAttribute("lundinTimesheetReviewerForm") LundinTimesheetReviewerForm lundinTimesheetReviewerForm,
			BindingResult result, ModelMap model, HttpServletRequest request) {

		HRISReviewerForm hrisReviewerFormResponse = new HRISReviewerForm();
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		try {
			lundinTimesheetReviewerForm.setEmployeeName(FormatPreserveCryptoUtil.decrypt(Long.valueOf(lundinTimesheetReviewerForm.getEmployeeName())).toString());
			lionTimesheetReviewerLogic.updateLionReviewer(
					lundinTimesheetReviewerForm, companyId);
			hrisReviewerFormResponse.setStatus("success");
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			hrisReviewerFormResponse.setStatus("failure");

		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(hrisReviewerFormResponse,
				jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/admin/lionTimesheetReviewer/deleteLionReviewer.html", method = RequestMethod.POST)
	@ResponseBody
	public String deleteLundinReviewer(
			@RequestParam(value = "employeeId", required = true) Long employeeId,
			ModelMap model, HttpServletRequest request) {
		/*ID DECRYPT*/
		employeeId = FormatPreserveCryptoUtil.decrypt(employeeId);
		// employeeId = (long) CipherUtility.getDecryptedValue(employeeId);
		HRISReviewerForm hrisReviewerFormResponse = new HRISReviewerForm();
		try {

			lionTimesheetReviewerLogic.deleteLionReviewer(employeeId);
			hrisReviewerFormResponse.setStatus("success");
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			hrisReviewerFormResponse.setStatus("failure");

		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(hrisReviewerFormResponse,
				jsonConfig);
		return jsonObject.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.WorkFlowDelegateController#searchEmployee(
	 * java.lang.String, java.lang.String, int, int, java.lang.String,
	 * java.lang.Sring, tjavax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = { "/employee/lionTimesheetReviewer/searchEmployee.html","/admin/lionTimesheetReviewer/searchEmployee.html"}, method = RequestMethod.POST)
	@ResponseBody
	public String searchEmployee(
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

		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		LundinReviewerResponseForm hrisReviewerResponse = lionTimesheetReviewerLogic
				.searchEmployee(pageDTO, sortDTO, empName, empNumber,
						companyId, employeeId);
		// CipherUtility.setIndirectReferenceForList(
		// hrisReviewerResponse.getSearchEmployeeList(), "employeeID");
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(hrisReviewerResponse,
				jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return null;
	}

	//TODO remove
	@Override
	/*@RequestMapping(value = "/admin/lionTimesheetReviewer/searchEmployeeBySessionCompany.html", method = RequestMethod.POST)*/
	@ResponseBody
	public String searchEmployeeBySessionCompany(
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

		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		LundinReviewerResponseForm hrisReviewerResponse = lionTimesheetReviewerLogic
				.searchEmployeeBySessionCompany(pageDTO, sortDTO, empName,
						empNumber, companyId, employeeId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(hrisReviewerResponse,
				jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return null;
	}

	@Override
	@RequestMapping(value = { "/employee/lionTimesheetReviewer/getCompanyGroupEmployeeId.html"}, method = RequestMethod.POST)
	@ResponseBody
	public String getCompanyGroupEmployeeId(
			@RequestParam(value = "searchString", required = true) String searchString,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		List<EmployeeListForm> employeeListFormList = lionTimesheetReviewerLogic
				.getCompanyGroupEmployeeId(companyId, searchString, employeeId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(employeeListFormList,
				jsonConfig);
		return jsonObject.toString();

	}
//TODO remove
	@Override
/*	@RequestMapping(value = { "/employee/lionTimesheetReviewer/getEmployeeId.html","/admin/lionTimesheetReviewer/getEmployeeId.html"} , method = RequestMethod.POST)*/
	@ResponseBody
	public String getEmployeeId(
			@RequestParam(value = "searchString", required = true) String searchString,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		List<EmployeeListForm> adminPaySlipFormFormList = lionTimesheetReviewerLogic
				.getEmployeeId(companyId, searchString, employeeId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(adminPaySlipFormFormList,
				jsonConfig);
		return jsonObject.toString();

	}

	@Override
	@RequestMapping(value = "/admin/lionTimesheetReviewer/configureLionRevWorkflow.html", method = RequestMethod.POST)
	@ResponseBody
	public String configureLionRevWorkflow(
			@ModelAttribute(value = "lundinTimesheetReviewerForm") LundinTimesheetReviewerForm lundinTimesheetReviewerForm,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		String Status = lionTimesheetReviewerLogic.configureLionRevWorkflow(
				companyId, lundinTimesheetReviewerForm);

		return Status;
	}

	@Override
	@RequestMapping(value = "/admin/lionTimesheetReviewer/getLionWorkFlowLevel.html", method = RequestMethod.POST)
	@ResponseBody
	public String getLionWorkFlowLevel(HttpServletRequest request,
			HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		LundinTimesheetReviewerForm lundinReviewerForm = lionTimesheetReviewerLogic
				.getLionWorkFlowLevel(companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(lundinReviewerForm,
				jsonConfig);

		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}
//TODO
	@Override
	@RequestMapping(value = "/admin/lionTimesheetReviewer/importLionReviewer.html", method = RequestMethod.POST)
	@ResponseBody
	public String importLionReviewer(
			@ModelAttribute(value = "lundinTimesheetReviewerForm") LundinTimesheetReviewerForm lundinTimesheetReviewerForm,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) throws Exception {
		boolean isValidImg = false;

		LundinTimesheetReviewerForm lundinReviewerfrm = null ;
		if(lundinTimesheetReviewerForm.getFileUpload()!=null){
		  isValidImg = FileUtils.isValidFile(lundinTimesheetReviewerForm.getFileUpload(), lundinTimesheetReviewerForm.getFileUpload().getOriginalFilename(),PayAsiaConstants.ALLOWED_ONLY_UPLOAD_EXCLE_FILE_EXT, PayAsiaConstants.ALLOWED_ONLY_UPLOAD_EXCLE_MINE_TYPE, PayAsiaConstants.FILE_SIZE);
		}
		
		if(isValidImg) {
		
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		lundinReviewerfrm = lionTimesheetReviewerLogic
				.importLionReviewer(lundinTimesheetReviewerForm, companyId);
		if (lundinReviewerfrm.getDataImportLogDTOs() != null) {
			for (DataImportLogDTO dataImportLogDTO : lundinReviewerfrm
					.getDataImportLogDTOs()) {
				try {
					dataImportLogDTO.setRemarks(URLEncoder.encode(messageSource
							.getMessage(dataImportLogDTO.getRemarks(),
									new Object[] {}, locale), "UTF-8"));
					
				} catch (UnsupportedEncodingException | NoSuchMessageException exception) {
					LOGGER.error(exception.getMessage(), exception);
					throw new PayAsiaSystemException(exception.getMessage(),
							exception);
				}
			}

		}
		}
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(lundinReviewerfrm,
				jsonConfig);
		return jsonObject.toString();
	}
	
	/**
	 * @param : Long workflowID, Long delegateID
	 * @param : This method used to check reviwer are same .
	 * */
	  private boolean reviwerIdCheck(Long employeeId, Long lundinReviewerId1) {
	          if(employeeId.equals(lundinReviewerId1)) {
	        	  return false;
	          }
			return true;
		}
}
