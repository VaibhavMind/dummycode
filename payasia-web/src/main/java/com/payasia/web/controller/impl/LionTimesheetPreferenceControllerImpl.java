package com.payasia.web.controller.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.payasia.common.dto.ResponseObjectDTO;
import com.payasia.common.form.LionTimesheetPreferenceForm;
import com.payasia.common.form.LundinTimesheetPreferenceForm;
import com.payasia.dao.AppCodeMasterDAO;
import com.payasia.dao.bean.AppCodeMaster;
import com.payasia.logic.LionTimesheetPreferenceLogic;
import com.payasia.web.controller.LionTimesheetPreferenceController;
import com.payasia.web.util.PayAsiaSessionAttributes;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * The Class LundinTimesheetPreferenceControllerImpl.
 */
@Controller
@RequestMapping(value = "/admin/lionhkTimesheetPreference")
public class LionTimesheetPreferenceControllerImpl implements
		LionTimesheetPreferenceController {

	@Resource
	LionTimesheetPreferenceLogic lionTimesheetPreferenceLogic;

	@Resource
	AppCodeMasterDAO appCodeMasterDAO;

	/*
	 * @Resource LeavePreferenceLogic leavePreferenceLogic;
	 */

	@Override
	@RequestMapping(value = "/saveLionTimesheetPreference.html", method = RequestMethod.POST)
	public @ResponseBody String saveLionTimesheetPreference(
			LionTimesheetPreferenceForm lionTimesheetPreferenceForm,
			BindingResult result, ModelMap model, HttpServletRequest request) {
		ResponseObjectDTO respObject = new ResponseObjectDTO();
		try {
			Long companyId = (Long) request.getSession().getAttribute(
					PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
			lionTimesheetPreferenceLogic.saveLionTimesheetPreference(
					lionTimesheetPreferenceForm, companyId);
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
	@RequestMapping(value = "lionhkTimesheetPreferenceData.html", method = RequestMethod.POST)
	public @ResponseBody String getLundinTimesheetPreference(
			HttpServletRequest request, HttpServletResponse response) {

		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		LionTimesheetPreferenceForm lionPreference = lionTimesheetPreferenceLogic
				.getLionTimesheetPreference(companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(lionPreference,
				jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "lionTimesheetAppCodeMasterData.html", method = RequestMethod.POST)
	public @ResponseBody String lionTimesheetAppCodeMasterData(
			HttpServletRequest request, HttpServletResponse response) {

		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		List<AppCodeMaster> appCodeMasterList = appCodeMasterDAO
				.findByCondition("Timesheet Lion");
		Map<String, String> appCodeMasterMap = new HashMap<String, String>();
		for (AppCodeMaster appCodeMaster : appCodeMasterList) {
			appCodeMasterMap.put(String.valueOf(appCodeMaster.getAppCodeID()),
					appCodeMaster.getCodeValue());
		}

		JSONObject jsonObject = new JSONObject();
		jsonObject.putAll(appCodeMasterMap);
		return jsonObject.toString();
	}

	/*
	 * @Override
	 * 
	 * @RequestMapping(value = "/getEmployeeFilterListLion.html", method =
	 * RequestMethod.POST)
	 * 
	 * @ResponseBody public String getEmployeeFilterList(HttpServletRequest
	 * request, HttpServletResponse response, Locale locale) { Long companyId =
	 * (Long) request.getSession().getAttribute(
	 * PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
	 * 
	 * List<EmployeeFilterListForm> filterList = leavePreferenceLogic
	 * .getEmployeeFilterList(companyId); JsonConfig jsonConfig = new
	 * JsonConfig(); JSONArray jsonObject = JSONArray.fromObject(filterList,
	 * jsonConfig); return jsonObject.toString(); }
	 */

	@Override
	public String saveLionTimesheetPreference(
			LundinTimesheetPreferenceForm lundinTimesheetPreferenceForm,
			BindingResult result, ModelMap model, HttpServletRequest request) {
		// TODO Auto-generated method stub
		return null;
	}
}
