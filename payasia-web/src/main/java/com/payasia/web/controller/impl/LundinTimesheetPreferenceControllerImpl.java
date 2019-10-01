package com.payasia.web.controller.impl;

import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.ResponseObjectDTO;
import com.payasia.common.form.EmployeeFilterListForm;
import com.payasia.common.form.LundinTimesheetPreferenceForm;
import com.payasia.logic.LeavePreferenceLogic;
import com.payasia.logic.LundinTimesheetPreferenceLogic;
import com.payasia.web.controller.LundinTimesheetPreferenceController;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * The Class LundinTimesheetPreferenceControllerImpl.
 */
@Controller
public class LundinTimesheetPreferenceControllerImpl implements
		LundinTimesheetPreferenceController {

	@Resource
	LundinTimesheetPreferenceLogic lundinTimesheetPreferenceLogic;
	@Resource
	LeavePreferenceLogic leavePreferenceLogic;

	@Override
	@RequestMapping(value = "/admin/lundinTimesheetPreference/saveLundinTimesheetPreference.html", method = RequestMethod.POST)
	public @ResponseBody String saveLundinTimesheetPreference(
			LundinTimesheetPreferenceForm lundinTimesheetPreferenceForm,
			BindingResult result, ModelMap model, HttpServletRequest request) {
		ResponseObjectDTO respObject = new ResponseObjectDTO();
		try {
			Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
			lundinTimesheetPreferenceLogic.saveLundinTimesheetPreference(
					lundinTimesheetPreferenceForm, companyId);
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
	@RequestMapping(value = "/admin/lundinTimesheetPreference/lundinTimesheetPreferenceData.html", method = RequestMethod.POST)
	public @ResponseBody String getLundinTimesheetPreference(
			HttpServletRequest request, HttpServletResponse response) {

		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		LundinTimesheetPreferenceForm lundinPreference = lundinTimesheetPreferenceLogic
				.getLundinTimesheetPreference(companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(lundinPreference,
				jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/admin/lundinTimesheetPreference/getEmployeeFilterList.html", method = RequestMethod.POST)
	@ResponseBody
	public String getEmployeeFilterList(HttpServletRequest request,
			HttpServletResponse response, Locale locale) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		List<EmployeeFilterListForm> filterList = leavePreferenceLogic
				.getEmployeeFilterList(companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(filterList, jsonConfig);
		return jsonObject.toString();
	}
}
