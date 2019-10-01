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
import com.payasia.common.form.LundinReviewerResponseForm;
import com.payasia.common.form.LundinTimesheetReviewerForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.dao.CompanyGroupDAO;
import com.payasia.dao.bean.Company;
import com.payasia.logic.LundinTimesheetReviewerLogic;
import com.payasia.web.controller.LundinTimesheetReviewerController;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

@Controller
public class LundinTimesheetReviewerControllerImpl implements
		LundinTimesheetReviewerController {

	/** The Constant logger. */
	private static final Logger LOGGER = Logger
			.getLogger(LundinTimesheetReviewerControllerImpl.class);

	@Resource
	LundinTimesheetReviewerLogic lundinTimesheetReviewerLogic;
	
	@Resource
	CompanyGroupDAO  companyGroupDAO;

	@Autowired
	private MessageSource messageSource;

	@Override
	@RequestMapping(value = "/admin/lundinTimesheetReviewer/viewLundinReviewers.html", method = RequestMethod.GET)
	@ResponseBody
	public String viewLundinReviewers(
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
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		LundinReviewerResponseForm lundinReviewerFormResponse = lundinTimesheetReviewerLogic
				.getLundinReviewers(searchCondition, searchText, pageDTO,
						sortDTO, companyId);
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
	@RequestMapping(value = "/admin/lundinTimesheetReviewer/getLundinTypeWorkFlow.html", method = RequestMethod.POST)
	@ResponseBody
	public String getLundinTypeWorkFlow(HttpServletRequest request,
			HttpServletResponse response) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		LundinTimesheetReviewerForm lundinReviewerForm = lundinTimesheetReviewerLogic
				.getLundinWorkFlow(companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(lundinReviewerForm,
				jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/admin/lundinTimesheetReviewer/getLundinReviewerData.html", method = RequestMethod.POST)
	@ResponseBody
	public String getLundinReviewerData(
			@RequestParam(value = "employeeId", required = true) Long employeeId,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		
		/*ID DECRYPT*/
		employeeId = FormatPreserveCryptoUtil.decrypt(employeeId);
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		LundinReviewerResponseForm lundinReviewerForm = null;
		Company companyGroup = companyGroupDAO.getEmployeeByCompany(employeeId,companyId);
		if(companyGroup != null)
		{
			lundinReviewerForm = lundinTimesheetReviewerLogic
					.getLundinReviewerData(employeeId,companyId);
			JsonConfig jsonConfig = new JsonConfig();
			JSONObject jsonObject = JSONObject.fromObject(lundinReviewerForm,
					jsonConfig);
			try {
				return URLEncoder.encode(jsonObject.toString(), "UTF-8");
			} catch (UnsupportedEncodingException ex) {
				LOGGER.error(ex.getMessage(), ex);
			}
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/admin/lundinTimesheetReviewer/saveLundinReviewer.html", method = RequestMethod.POST)
	@ResponseBody
	public String saveLundinReviewer(
			@ModelAttribute("lundinTimesheetReviewerForm") LundinTimesheetReviewerForm lundinTimesheetReviewerForm,
			ModelMap model, HttpServletRequest request) {
		/*ID DECRYPT*/
		lundinTimesheetReviewerForm.setEmployeeId(FormatPreserveCryptoUtil.decrypt(lundinTimesheetReviewerForm.getEmployeeId()));
		lundinTimesheetReviewerForm.setLundinReviewerId1(FormatPreserveCryptoUtil.decrypt(lundinTimesheetReviewerForm.getLundinReviewerId1()));
		lundinTimesheetReviewerForm.setLundinReviewerId2(FormatPreserveCryptoUtil.decrypt(lundinTimesheetReviewerForm.getLundinReviewerId2()));
		lundinTimesheetReviewerForm.setLundinReviewerId3(FormatPreserveCryptoUtil.decrypt(lundinTimesheetReviewerForm.getLundinReviewerId3()));
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		Long employeeId = lundinTimesheetReviewerForm.getEmployeeId();
		
		
		LundinTimesheetReviewerForm hrisReviewerFormResponse = lundinTimesheetReviewerLogic
				.checkLundinReviewer(employeeId, companyId);
		if (hrisReviewerFormResponse.getEmployeeStatus().equalsIgnoreCase(
				"notexists")) {

			try {
				boolean flag = reviwerIdCheck(employeeId,lundinTimesheetReviewerForm.getLundinReviewerId1(),lundinTimesheetReviewerForm.getLundinReviewerId2(),lundinTimesheetReviewerForm.getLundinReviewerId3());
				
			    if(flag) {
				lundinTimesheetReviewerLogic.saveLundinReviewer(
						lundinTimesheetReviewerForm, companyId);
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
	@RequestMapping(value = "/admin/lundinTimesheetReviewer/updateLundinReviewer.html", method = RequestMethod.POST)
	@ResponseBody
	public String updateLundinReviewer(
			@ModelAttribute("lundinTimesheetReviewerForm") LundinTimesheetReviewerForm lundinTimesheetReviewerForm,
			BindingResult result, ModelMap model, HttpServletRequest request) {
		
		/*ID DECRYPT*/
		lundinTimesheetReviewerForm.setEmployeeName(FormatPreserveCryptoUtil.decrypt(Long.parseLong(lundinTimesheetReviewerForm.getEmployeeName())).toString());
		lundinTimesheetReviewerForm.setLundinReviewerId1(FormatPreserveCryptoUtil.decrypt(lundinTimesheetReviewerForm.getLundinReviewerId1()));
		lundinTimesheetReviewerForm.setLundinReviewerId2(FormatPreserveCryptoUtil.decrypt(lundinTimesheetReviewerForm.getLundinReviewerId2()));
		lundinTimesheetReviewerForm.setLundinReviewerId3(FormatPreserveCryptoUtil.decrypt(lundinTimesheetReviewerForm.getLundinReviewerId3()));
		
		HRISReviewerForm hrisReviewerFormResponse = new HRISReviewerForm();
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		try {
			if(reviwerIdCheck(Long.parseLong(lundinTimesheetReviewerForm.getEmployeeName()), lundinTimesheetReviewerForm.getLundinReviewerId1(), lundinTimesheetReviewerForm.getLundinReviewerId2(), lundinTimesheetReviewerForm.getLundinReviewerId3())){
			lundinTimesheetReviewerLogic.updateLundinReviewer(
					lundinTimesheetReviewerForm, companyId);
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
	@RequestMapping(value = "/admin/lundinTimesheetReviewer/deleteLundinReviewer.html", method = RequestMethod.POST)
	@ResponseBody
	public String deleteLundinReviewer(
			@RequestParam(value = "employeeId", required = true) Long employeeId,
			ModelMap model, HttpServletRequest request) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		/*ID DECRYPT*/
		employeeId = FormatPreserveCryptoUtil.decrypt(employeeId);
		Company companyGroup = companyGroupDAO.getEmployeeByCompany(employeeId,companyId);
		if(companyGroup!=null)
		{
		HRISReviewerForm hrisReviewerFormResponse = new HRISReviewerForm();
		try {

			lundinTimesheetReviewerLogic.deleteLundinReviewer(employeeId);
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
		return "";
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
	@RequestMapping(value = "/admin/lundinTimesheetReviewer/searchEmployee.html", method = RequestMethod.POST)
	@ResponseBody
	public String searchEmployee(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			@RequestParam(value = "empName", required = true) String empName,
			@RequestParam(value = "empNumber", required = true) String empNumber,
			HttpServletRequest request, HttpServletResponse response) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		LundinReviewerResponseForm hrisReviewerResponse = lundinTimesheetReviewerLogic
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

	@Override
	@RequestMapping(value = "/admin/lundinTimesheetReviewer/searchEmployeeBySessionCompany.html", method = RequestMethod.POST)
	@ResponseBody
	public String searchEmployeeBySessionCompany(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			@RequestParam(value = "empName", required = true) String empName,
			@RequestParam(value = "empNumber", required = true) String empNumber,
			HttpServletRequest request, HttpServletResponse response) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		LundinReviewerResponseForm hrisReviewerResponse = lundinTimesheetReviewerLogic
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
	@RequestMapping(value = "/admin/lundinTimesheetReviewer/getCompanyGroupEmployeeId.html", method = RequestMethod.POST)
	@ResponseBody
	public String getCompanyGroupEmployeeId(
			@RequestParam(value = "searchString", required = true) String searchString,
			HttpServletRequest request, HttpServletResponse response) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		List<EmployeeListForm> employeeListFormList = lundinTimesheetReviewerLogic
				.getCompanyGroupEmployeeId(companyId, searchString, employeeId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(employeeListFormList,
				jsonConfig);
		return jsonObject.toString();

	}

	@Override
	@RequestMapping(value = "/admin/lundinTimesheetReviewer/getEmployeeId.html", method = RequestMethod.POST)
	@ResponseBody
	public String getEmployeeId(
			@RequestParam(value = "searchString", required = true) String searchString,
			HttpServletRequest request, HttpServletResponse response) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		List<EmployeeListForm> adminPaySlipFormFormList = lundinTimesheetReviewerLogic
				.getEmployeeId(companyId, searchString, employeeId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(adminPaySlipFormFormList,
				jsonConfig);
		return jsonObject.toString();

	}

	@Override
	@RequestMapping(value = "/admin/lundinTimesheetReviewer/configureLundinRevWorkflow.html", method = RequestMethod.POST)
	@ResponseBody
	public String configureLundinRevWorkflow(
			@ModelAttribute(value = "lundinTimesheetReviewerForm") LundinTimesheetReviewerForm lundinTimesheetReviewerForm,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		String Status = lundinTimesheetReviewerLogic
				.configureLundinRevWorkflow(companyId,
						lundinTimesheetReviewerForm);

		return Status;
	}

	@Override
	@RequestMapping(value = "/admin/lundinTimesheetReviewer/getLundinWorkFlowLevel.html", method = RequestMethod.POST)
	@ResponseBody
	public String getLundinWorkFlowLevel(HttpServletRequest request,
			HttpServletResponse response) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		LundinTimesheetReviewerForm lundinReviewerForm = lundinTimesheetReviewerLogic
				.getLundinWorkFlowLevel(companyId);
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

	@Override
	@RequestMapping(value = "/admin/lundinTimesheetReviewer/importLundinReviewer.html", method = RequestMethod.POST)
	@ResponseBody
	public String importLundinReviewer(
			@ModelAttribute(value = "lundinTimesheetReviewerForm") LundinTimesheetReviewerForm lundinTimesheetReviewerForm,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) throws Exception {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		LundinTimesheetReviewerForm lundinReviewerfrm = lundinTimesheetReviewerLogic
				.importLundinReviewer(lundinTimesheetReviewerForm, companyId);
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

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(lundinReviewerfrm,
				jsonConfig);
		return jsonObject.toString();
	}
	
	/**
	 * @param : Long hrisreviewerid1, Long hrisreviewerid2, Long hrisreviewerid3
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
