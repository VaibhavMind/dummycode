package com.payasia.web.controller.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.MailTemplateDTO;
import com.payasia.common.dto.ResponseObjectDTO;
import com.payasia.common.form.LeaveReviewerResponseForm;
import com.payasia.common.form.LundinTimesheetEventReminderForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.logic.LundinTimesheetEventReminderLogic;
import com.payasia.web.controller.LundinTimesheetEventReminderController;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

@Controller

public class LundinTimesheetEventReminderControllerImpl implements
		LundinTimesheetEventReminderController {

	@Resource
	LundinTimesheetEventReminderLogic lundinTimesheetEventReminderLogic;

	private static final Logger LOGGER = Logger
			.getLogger(LundinTimesheetEventReminderControllerImpl.class);

	@Override
	@RequestMapping(value = "/admin/lundin/eventReminder/getTimesheetEventReminders.html", method = RequestMethod.POST)
	@ResponseBody
	public String getTimesheetEventReminders(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "searchType", required = false) String searchType,
			@RequestParam(value = "searchValue", required = false) Long searchValue,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			HttpServletRequest request, HttpServletResponse response) {

		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		LundinTimesheetEventReminderForm lundinTimesheetEventReminderForm = lundinTimesheetEventReminderLogic
				.getTimesheetEventReminders(companyId, pageDTO, searchType,
						searchValue);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(
				lundinTimesheetEventReminderForm, jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/admin/lundin/eventReminder/saveTimesheetEventReminder.html", method = RequestMethod.POST)
	@ResponseBody
	public String saveTimesheetEventReminder(
			@ModelAttribute(value = "lundinEventReminderForm") LundinTimesheetEventReminderForm timesheetEventReminderForm,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		try {
			ResponseObjectDTO responseDto = lundinTimesheetEventReminderLogic
					.saveTimesheetEventReminder(timesheetEventReminderForm,
							companyId);
			JsonConfig jsonConfig = new JsonConfig();
			JSONObject jsonObject = JSONObject.fromObject(responseDto,
					jsonConfig);
			return URLEncoder.encode(jsonObject.toString(), "UTF8");
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/admin/lundin/eventReminder/getTimesheetEventReminderData.html", method = RequestMethod.POST)
	@ResponseBody
	public String getTimesheetEventReminderData(
			@RequestParam(value = "eventReminderConfigId", required = false) Long eventReminderConfigId,
			HttpServletRequest request, HttpServletResponse response) {
		/*ID DECRYPT*/
		eventReminderConfigId = FormatPreserveCryptoUtil.decrypt(eventReminderConfigId);
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		try {
			LundinTimesheetEventReminderForm lundinTimesheetEventReminderForm = lundinTimesheetEventReminderLogic
					.getTimesheetEventReminderData(companyId,
							eventReminderConfigId);

			JsonConfig jsonConfig = new JsonConfig();
			JSONObject jsonObject = JSONObject.fromObject(
					lundinTimesheetEventReminderForm, jsonConfig);

			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/admin/lundin/eventReminder/updateTimesheetEventReminderData.html", method = RequestMethod.POST)
	@ResponseBody
	public String updateTimesheetEventReminderData(
			@ModelAttribute(value = "lundinEventReminderForm") LundinTimesheetEventReminderForm timesheetEventReminderForm,
			HttpServletRequest request, HttpServletResponse response) {

		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		try {
			ResponseObjectDTO responseDto = lundinTimesheetEventReminderLogic
					.updateTimesheetEventReminder(timesheetEventReminderForm,
							FormatPreserveCryptoUtil.decrypt(timesheetEventReminderForm
									.getReminderEventConfigId()), companyId);
			JsonConfig jsonConfig = new JsonConfig();
			JSONObject jsonObject = JSONObject.fromObject(responseDto,
					jsonConfig);
			return URLEncoder.encode(jsonObject.toString(), "UTF8");
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/admin/lundin/eventReminder/deleteTimesheetEventReminder.html", method = RequestMethod.POST)
	@ResponseBody
	public String deleteTimesheetEventReminder(
			@RequestParam(value = "eventReminderConfigId", required = false) Long eventReminderConfigId,
			HttpServletRequest request, HttpServletResponse response) {
		/*ID DECRYPT*/
		eventReminderConfigId = FormatPreserveCryptoUtil.decrypt(eventReminderConfigId);
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		try {
			ResponseObjectDTO responseDto = lundinTimesheetEventReminderLogic
					.deleteTimesheetEventReminder(eventReminderConfigId,
							companyId);
			JsonConfig jsonConfig = new JsonConfig();
			JSONObject jsonObject = JSONObject.fromObject(responseDto,
					jsonConfig);
			return URLEncoder.encode(jsonObject.toString(), "UTF8");
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/admin/lundin/eventReminder/searchEmployee.html", method = RequestMethod.POST)
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

		LeaveReviewerResponseForm leaveReviewerResponse = lundinTimesheetEventReminderLogic
				.searchEmployee(pageDTO, sortDTO, employeeId, empName,
						empNumber, companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(leaveReviewerResponse,
				jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/admin/lundin/eventReminder/getMailTemplateList.html", method = RequestMethod.POST)
	@ResponseBody
	public String getMailTemplateList(HttpServletRequest request) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		List<MailTemplateDTO> mailTemplateDtos = lundinTimesheetEventReminderLogic
				.getMailTemplates(companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(mailTemplateDtos,
				jsonConfig);
		return jsonObject.toString();
	}

}
