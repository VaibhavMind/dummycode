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

import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.DataImportLogDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.EmployeeListForm;
import com.payasia.common.form.HRISReviewerForm;
import com.payasia.common.form.HRISReviewerResponseForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.FileUtils;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.logic.HRISReviewerLogic;
import com.payasia.web.controller.HRISReviewerController;
import com.payasia.web.util.PayAsiaSessionAttributes;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

@Controller
@RequestMapping(value = { "/employee/hrisReviewer", "/admin/hrisReviewer" })
public class HRISReviewerControllerImpl implements HRISReviewerController {

	/** The Constant logger. */
	private static final Logger LOGGER = Logger
			.getLogger(HRISReviewerControllerImpl.class);

	@Resource
	HRISReviewerLogic hrisReviewerLogic;

	@Autowired
	private MessageSource messageSource;

	@Override
	@RequestMapping(value = "/viewHRISReviewers.html", method = RequestMethod.GET)
	@ResponseBody public String viewHRISReviewers(
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

		HRISReviewerResponseForm hrisReviewerFormResponse = hrisReviewerLogic
				.getHRISReviewers(searchCondition, searchText, pageDTO,
						sortDTO, companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(hrisReviewerFormResponse,
				jsonConfig);

		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return null;

	}

	@Override
	@RequestMapping(value = "/getHRISTypeWorkFlow.html", method = RequestMethod.POST)
	@ResponseBody public String getHRISTypeWorkFlow(HttpServletRequest request,
			HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		HRISReviewerForm hrisReviewerForm = hrisReviewerLogic
				.getHRISWorkFlow(companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(hrisReviewerForm,
				jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/getHRISReviewerData.html", method = RequestMethod.POST)
	@ResponseBody public String getHRISReviewerData(
			@RequestParam(value = "employeeId", required = true) Long employeeId,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		/*ID DECRYPT*/
		employeeId = FormatPreserveCryptoUtil.decrypt(employeeId);
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		HRISReviewerResponseForm hrisReviewerForm = hrisReviewerLogic
				.getHRISReviewerData(employeeId, companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(hrisReviewerForm,
				jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/saveHRISReviewer.html", method = RequestMethod.POST)
	@ResponseBody public String saveHRISReviewer(
			@ModelAttribute("hrisReviewerForm") HRISReviewerForm hrisReviewerForm,
			ModelMap model, HttpServletRequest request) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long employeeId = hrisReviewerForm.getEmployeeId();

		HRISReviewerForm hrisReviewerFormResponse = hrisReviewerLogic
				.checkHRISReviewer(employeeId, companyId);
		if (hrisReviewerFormResponse.getEmployeeStatus().equalsIgnoreCase("notexists")) {

			try {
			    
				boolean flag = reviwerIdCheck(employeeId,hrisReviewerForm.getHrisReviewerId1(),hrisReviewerForm.getHrisReviewerId2(),hrisReviewerForm.getHrisReviewerId3());
				
			    if(flag) {
            		hrisReviewerLogic.saveHRISReviewer(hrisReviewerForm, companyId);
            		hrisReviewerFormResponse.setStatus("success");
                 }else{
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
	@RequestMapping(value = "/updateHRISReviewer.html", method = RequestMethod.POST)
	@ResponseBody public String updateHRISReviewer(
			@ModelAttribute("hrisReviewerForm") HRISReviewerForm hrisReviewerForm,
			BindingResult result, ModelMap model, HttpServletRequest request) {

		HRISReviewerForm hrisReviewerFormResponse = null;
		
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		try {
			hrisReviewerForm.setEmployeeName(FormatPreserveCryptoUtil.decrypt(Long.valueOf(hrisReviewerForm.getEmployeeName())).toString());
			hrisReviewerFormResponse = hrisReviewerLogic.checkHRISReviewer(Long.valueOf(hrisReviewerForm.getEmployeeName()), companyId);
			if(hrisReviewerFormResponse.getEmployeeStatus().equalsIgnoreCase("exists") && reviwerIdCheck(Long.parseLong(hrisReviewerForm.getEmployeeName()), hrisReviewerForm.getHrisReviewerId1(), hrisReviewerForm.getHrisReviewerId2(), hrisReviewerForm.getHrisReviewerId3())){
			  hrisReviewerLogic.updateHRISReviewer(hrisReviewerForm, companyId);
			  hrisReviewerFormResponse.setStatus("success");
			}else{
				hrisReviewerFormResponse.setStatus("failure");
			}
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
	@RequestMapping(value = "/deleteHRISReviewer.html", method = RequestMethod.POST)
	@ResponseBody public String deleteHRISReviewer(
			@RequestParam(value = "employeeId", required = true) Long employeeId,
			ModelMap model, HttpServletRequest request) {
		/*ID DECRYPT*/
		employeeId = FormatPreserveCryptoUtil.decrypt(employeeId);
		HRISReviewerForm hrisReviewerFormResponse = new HRISReviewerForm();
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		try {

			hrisReviewerLogic.deleteHRISReviewer(employeeId,companyId);
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

		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		HRISReviewerResponseForm hrisReviewerResponse = hrisReviewerLogic
				.searchEmployee(pageDTO, sortDTO, empName, empNumber,
						companyId, employeeId);

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
	@RequestMapping(value = "/searchEmployeeBySessionCompany.html", method = RequestMethod.POST)
	@ResponseBody public String searchEmployeeBySessionCompany(
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

		HRISReviewerResponseForm hrisReviewerResponse = hrisReviewerLogic
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
	@RequestMapping(value = "/getCompanyGroupEmployeeId.html", method = RequestMethod.POST)
	@ResponseBody public String getCompanyGroupEmployeeId(
			@RequestParam(value = "searchString", required = true) String searchString,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		List<EmployeeListForm> employeeListFormList = hrisReviewerLogic
				.getCompanyGroupEmployeeId(companyId, searchString, employeeId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(employeeListFormList,
				jsonConfig);
		return jsonObject.toString();

	}

	@Override
	@RequestMapping(value = "/getEmployeeId.html", method = RequestMethod.POST)
	@ResponseBody public String getEmployeeId(
			@RequestParam(value = "searchString", required = true) String searchString,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		List<EmployeeListForm> adminPaySlipFormFormList = hrisReviewerLogic
				.getEmployeeId(companyId, searchString, employeeId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(adminPaySlipFormFormList,
				jsonConfig);
		return jsonObject.toString();

	}

	@Override
	@RequestMapping(value = "/configureHRISRevWorkflow.html", method = RequestMethod.POST)
	@ResponseBody public String configureHRISRevWorkflow(
			@ModelAttribute(value = "hrisReviewerForm") HRISReviewerForm hrisReviewerForm,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		String Status = hrisReviewerLogic.configureHRISRevWorkflow(companyId,
				hrisReviewerForm);

		return Status;
	}

	@Override
	@RequestMapping(value = "/getHRISWorkFlowLevel.html", method = RequestMethod.POST)
	@ResponseBody public String getHRISWorkFlowLevel(
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		HRISReviewerForm hrisReviewerForm = hrisReviewerLogic
				.getHRISWorkFlowLevel(companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(hrisReviewerForm,
				jsonConfig);

		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/importHRISReviewer.html", method = RequestMethod.POST)
	@ResponseBody public String importHRISReviewer(
			@ModelAttribute(value = "hrisReviewerForm") HRISReviewerForm hrisReviewerForm,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) throws Exception {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		
		HRISReviewerForm hrisReviewerfrm = new HRISReviewerForm();
		
		try{
			boolean isFileValid = false;
			if(hrisReviewerForm.getFileUpload() != null){
				isFileValid = FileUtils.isValidFile(hrisReviewerForm.getFileUpload(), hrisReviewerForm.getFileUpload().getOriginalFilename(), PayAsiaConstants.ALLOWED_ONLY_UPLOAD_EXCLE_FILE_EXT, PayAsiaConstants.ALLOWED_ONLY_UPLOAD_EXCLE_MINE_TYPE,PayAsiaConstants.FILE_SIZE);
			}
			if(isFileValid){
				
				hrisReviewerfrm = hrisReviewerLogic
						.importHRISReviewer(hrisReviewerForm, companyId);
				if (hrisReviewerfrm.getDataImportLogDTOs() != null) {
					for (DataImportLogDTO dataImportLogDTO : hrisReviewerfrm
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
		
		}catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(hrisReviewerfrm,
				jsonConfig);
		return jsonObject.toString();
	}
	
	/**
	 * @param : Long workflowID, Long delegateID
	 * @param : This method used to check reviewer are same .
	 * */
	private boolean reviwerIdCheck(Long employeeId, Long hrisreviewerid1, Long hrisreviewerid2, Long hrisreviewerid3) {

		hrisreviewerid1 = hrisreviewerid1 != null ? hrisreviewerid1 : 0l;
		hrisreviewerid2 = hrisreviewerid2 != null ? hrisreviewerid2 : 0l;
		
		if(employeeId.equals(hrisreviewerid1) || employeeId.equals(hrisreviewerid2)|| employeeId.equals(hrisreviewerid3)) {
			return false;
		} else if (hrisreviewerid1.equals(hrisreviewerid2) || hrisreviewerid1.equals(hrisreviewerid3) || hrisreviewerid2.equals(hrisreviewerid3)) {
			return false;
		}
		return true;
	}
}
