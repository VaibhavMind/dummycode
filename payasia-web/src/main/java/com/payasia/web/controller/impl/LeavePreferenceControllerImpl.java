package com.payasia.web.controller.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.payasia.common.bean.util.UserContext;
import com.payasia.common.form.EmployeeFilterListForm;
import com.payasia.common.form.LeavePreferenceForm;
import com.payasia.logic.LeavePreferenceLogic;
import com.payasia.web.controller.LeavePreferenceController;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * The Class LeavePreferenceControllerImpl.
 */
@Controller
public class LeavePreferenceControllerImpl implements LeavePreferenceController {
	/** The email preference logic. */
	@Resource
	LeavePreferenceLogic leavePreferenceLogic;

	@Override
	@RequestMapping(value = "/admin/leavePreference/saveLeavePreferences.html", method = RequestMethod.POST)
	public @ResponseBody void saveLeavePreference(
			@ModelAttribute(value = "leavePreferenceForm") LeavePreferenceForm leavePreferenceForm) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		leavePreferenceLogic
				.saveLeavePreference(leavePreferenceForm, companyId);
	}

	@Override
	@RequestMapping(value = "/admin/leavePreference/leavePreferenceDataForEdit.html", method = RequestMethod.POST)
	public @ResponseBody String getLeavePreference() {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		LeavePreferenceForm leavePreferenceForm = leavePreferenceLogic
				.getLeavePreference(companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(leavePreferenceForm,
				jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/admin/leavePreference/getEmployeeFilterList.html", method = RequestMethod.POST)
	@ResponseBody
	public String getEmployeeFilterList() {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		List<EmployeeFilterListForm> filterList = leavePreferenceLogic
				.getEmployeeFilterList(companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(filterList, jsonConfig);
		return jsonObject.toString();
	}

}
