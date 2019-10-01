package com.payasia.web.controller.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.payasia.common.bean.util.UserContext;
import com.payasia.common.form.LeaveEventReminderForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.logic.LeaveEventReminderLogic;
import com.payasia.web.controller.LeaveEventReminderController;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

@Controller
public class LeaveEventReminderControllerImpl implements
		LeaveEventReminderController {
	private static final Logger LOGGER = Logger
			.getLogger(LeaveEventReminderControllerImpl.class);
	@Resource
	LeaveEventReminderLogic leaveEventReminderLogic;

	@Override
	@RequestMapping(value = "/admin/eventReminder/getLeaveTypes.html", method = RequestMethod.POST)
	@ResponseBody public String getLeaveTypes(
			@RequestParam(value = "leaveSchemeId", required = true) Long leaveSchemeId) {

		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		LeaveEventReminderForm eventReminderForm = new LeaveEventReminderForm();
		eventReminderForm.setLeaveTypeDTOs(leaveEventReminderLogic
				.getLeaveTypes(leaveSchemeId, companyId));

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(eventReminderForm,
				jsonConfig);
		return jsonObject.toString();

	}

	@Override
	@RequestMapping(value = "/admin/eventReminder/saveLeaveEventReminder.html", method = RequestMethod.POST)
	@ResponseBody public String saveLeaveEventReminder(
			@ModelAttribute(value = "leaveEventReminderForm") LeaveEventReminderForm leaveEventReminderForm) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		LeaveEventReminderForm eventReminderForm = new LeaveEventReminderForm();
		try {
			leaveEventReminderLogic.saveLeaveEventReminder(
					leaveEventReminderForm, companyId);
			eventReminderForm.setStatus(true);

		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			eventReminderForm.setStatus(false);
		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(eventReminderForm,
				jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/admin/eventReminder/getEventReminders.html", method = RequestMethod.POST)
	@ResponseBody public String getEventReminders(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "searchType", required = false) String searchType,
			@RequestParam(value = "searchValue", required = false) Long searchValue,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows) {

		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		LeaveEventReminderForm eventReminderForm = leaveEventReminderLogic
				.getEventReminders(companyId, pageDTO, searchType, searchValue);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(eventReminderForm,
				jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/admin/eventReminder/getEventReminderData.html", method = RequestMethod.POST)
	@ResponseBody public String getLeaveEventReminderData(
			@RequestParam(value = "eventReminderConfigId", required = false) Long eventReminderConfigId) {
		/* ID DECRYPT */
		eventReminderConfigId= FormatPreserveCryptoUtil.decrypt(eventReminderConfigId);
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		LeaveEventReminderForm eventReminderForm = leaveEventReminderLogic
				.getLeaveEventReminderData(companyId, eventReminderConfigId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(eventReminderForm,
				jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/admin/eventReminder/updateEventReminderData.html", method = RequestMethod.POST)
	@ResponseBody public String updateEventReminderData(
			@ModelAttribute(value = "leaveEventReminderForm") LeaveEventReminderForm leaveEventReminderForm,
			@RequestParam(value = "eventReminderConfigId", required = false) Long eventReminderConfigId) {
		/* ID DECRYPT */
		eventReminderConfigId = FormatPreserveCryptoUtil.decrypt(eventReminderConfigId);
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		LeaveEventReminderForm eventReminderForm = new LeaveEventReminderForm();

		try {
			leaveEventReminderLogic.updateLeaveEventReminder(
					leaveEventReminderForm, eventReminderConfigId, companyId);
			eventReminderForm.setStatus(true);

		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			eventReminderForm.setStatus(false);
		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(eventReminderForm,
				jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/admin/eventReminder/deleteLeaveEventReminder.html", method = RequestMethod.POST)
	@ResponseBody public String deleteLeaveEventReminder(
			@RequestParam(value = "eventReminderConfigId", required = false) Long eventReminderConfigId) {
		/* ID DECRYPT */
		eventReminderConfigId= FormatPreserveCryptoUtil.decrypt(eventReminderConfigId);
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		LeaveEventReminderForm eventReminderForm = new LeaveEventReminderForm();
		try {
			leaveEventReminderLogic.deleteLeaveEventReminder(
					eventReminderConfigId, companyId);
			eventReminderForm.setStatus(true);

		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			eventReminderForm.setStatus(false);
		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(eventReminderForm,
				jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/admin/eventReminder/scheDulerExample.html", method = RequestMethod.POST)
	@Scheduled(fixedRate = 2)
	public @ResponseBody void scheDulerExample(HttpServletRequest request,
			HttpServletResponse response) {

	}

}
