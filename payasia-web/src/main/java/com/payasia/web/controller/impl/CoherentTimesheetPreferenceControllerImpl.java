package com.payasia.web.controller.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.ResponseObjectDTO;
import com.payasia.common.form.ClaimReviewerForm;
import com.payasia.common.form.CoherentOTEmployeeListForm;
import com.payasia.common.form.CoherentTimesheetPreferenceForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.logic.CoherentTimesheetPreferenceLogic;
import com.payasia.logic.LundinTimesheetPreferenceLogic;
import com.payasia.web.controller.CoherentTimesheetPreferenceController;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

@Controller
public class CoherentTimesheetPreferenceControllerImpl implements
		CoherentTimesheetPreferenceController {

	@Resource
	LundinTimesheetPreferenceLogic lundinTimesheetPreferenceLogic;

	@Resource
	CoherentTimesheetPreferenceLogic coherentTimesheetPreferenceLogic;

	@Override
	@RequestMapping(value = "/admin/coherentTimesheetPreference/coherentTimesheetPreferenceData.html", method = RequestMethod.POST)
	public @ResponseBody String getCoherentTimesheetPreference(
			HttpServletRequest request, HttpServletResponse response) {

		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		CoherentTimesheetPreferenceForm coherentTimesheetPreferenceForm = coherentTimesheetPreferenceLogic
				.getCoherentTimesheetPreference(companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(
				coherentTimesheetPreferenceForm, jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/admin/coherentTimesheetPreference/searchEmployee.html", method = RequestMethod.POST)
	@ResponseBody
	public String searchEmployee(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			@RequestParam(value = "empName", required = true) String empName,
			@RequestParam(value = "empNumber", required = true) String empNumber,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
        Long employeeId = Long.parseLong(UserContext.getUserId());

		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		ClaimReviewerForm claimReviewerResponse = coherentTimesheetPreferenceLogic
				.searchEmployee(pageDTO, sortDTO, empName, empNumber,
						companyId, employeeId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(claimReviewerResponse,
				jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			// LOGGER.error(ex.getMessage(), ex);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/admin/coherentTimesheetPreference/saveCoherentTimesheetPreference.html", method = RequestMethod.POST)
	public @ResponseBody String saveCoherentTimesheetPreference(
			CoherentTimesheetPreferenceForm coherentTimesheetPreferenceForm,
			BindingResult result, ModelMap model, HttpServletRequest request) {
		ResponseObjectDTO respObject = new ResponseObjectDTO();
		try {
			Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
			coherentTimesheetPreferenceLogic.saveCoherentTimesheetPreference(
					coherentTimesheetPreferenceForm, companyId);
			respObject.setSuccess(true);
			JsonConfig jsonConfig = new JsonConfig();
			JSONObject jsonObject = JSONObject.fromObject(respObject,
					jsonConfig);
			return jsonObject.toString();
		} catch (Exception e) {
			respObject.setSuccess(false);
			JsonConfig jsonConfig = new JsonConfig();
			JSONObject jsonObject = JSONObject.fromObject(respObject,
					jsonConfig);
			return jsonObject.toString();
		}
	}

	@Override
	@RequestMapping(value = "/admin/coherentTimesheetPreference/saveCoherentOTEmployee.html", method = RequestMethod.POST)
	@ResponseBody
	public String saveCoherentOTEmployee(
			@RequestParam(value = "employeeId", required = true) Long otEmployeeId,
			HttpServletRequest request) {
		/*ID DECRYPT*/
		otEmployeeId =   FormatPreserveCryptoUtil.decrypt(otEmployeeId);


		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		coherentTimesheetPreferenceLogic.saveCoherentOTEmployee(otEmployeeId,
				employeeId, companyId);

		// ClaimReviewerForm claimReviewerForm = claimReviewerLogic
		// .getClaimReviewers(claimTemplateId);
		//
		// JsonConfig jsonConfig = new JsonConfig();
		// JSONObject jsonObject = JSONObject.fromObject(claimReviewerForm,
		// jsonConfig);
		//
		// try {
		//
		// return URLEncoder.encode(jsonObject.toString(), "UTF8");
		// } catch (UnsupportedEncodingException e) {
		//
		// LOGGER.error(e.getMessage(), e);
		// }
		return null;
	}

	@Override
	@RequestMapping(value = "/admin/coherentTimesheetPreference/searchCoherentSelectOTEmployees.html", method = RequestMethod.GET)
	@ResponseBody
	public String searchCoherentSelectOTEmployees(
			@RequestParam(value = "searchCondition") String searchCondition,
			@RequestParam(value = "searchText") String searchText,
			HttpServletRequest request) {

		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		
		CoherentOTEmployeeListForm CoherentOTEmployeeListForm = coherentTimesheetPreferenceLogic
				.searchCoherentSelectOTEmployees(companyId, searchCondition,
						searchText);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(
				CoherentOTEmployeeListForm, jsonConfig);

		try {

			return URLEncoder.encode(jsonObject.toString(), "UTF8");
		} catch (UnsupportedEncodingException e) {

		}
		return null;
	}

	@Override
	@RequestMapping(value = "/admin/coherentTimesheetPreference/deleteCoherentOTEmployee.html", method = RequestMethod.POST)
	@ResponseBody
	public String deleteCoherentOTEmployee(
			@RequestParam(value = "employeeId") String employeeId,
			HttpServletRequest request) {
		coherentTimesheetPreferenceLogic.deleteCoherentOTEmployee(employeeId);
		return null;
	}

}
