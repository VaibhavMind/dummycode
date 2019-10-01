package com.payasia.web.controller.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

import com.payasia.common.form.ClaimCustomFieldForm;
import com.payasia.common.form.ClaimCustomFieldResponse;
import com.payasia.common.form.ConfigureClaimItemForm;
import com.payasia.common.form.ConfigureClaimItemResponse;
import com.payasia.common.form.OTTemplateForm;
import com.payasia.common.form.OTTemplateResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.logic.OTTemplateLogic;
import com.payasia.web.controller.OTTemplateController;
import com.payasia.web.util.PayAsiaSessionAttributes;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

@Controller
@RequestMapping(value = "/OTTemplate")
public class OTTemplateControllerImpl implements OTTemplateController {
	private static final Logger LOGGER = Logger
			.getLogger(OTTemplateControllerImpl.class);

	@Resource
	OTTemplateLogic otTemplateLogic;

	@RequestMapping(value = "/viewOTTemplate", method = RequestMethod.POST)
	@Override
	@ResponseBody public String accessControl(
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "searchCondition", required = true) String searchCondition,
			@RequestParam(value = "searchText", required = true) String searchText,
			HttpServletRequest request, HttpServletResponse response) {

		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		OTTemplateResponse otTemplateResponse = otTemplateLogic.accessControl(
				companyId, searchCondition, searchText, pageDTO, sortDTO);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(otTemplateResponse,
				jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/addOTTemplate", method = RequestMethod.POST)
	@ResponseBody public String addOTTemplate(
			@RequestParam(value = "otTemplateName", required = true) String otTemplateName,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		String status = otTemplateLogic
				.addOTTemplate(companyId, otTemplateName);
		return status;

	}

	@Override
	@RequestMapping(value = "/deleteOTTemplate", method = RequestMethod.POST)
	public @ResponseBody void deleteOTTemplate(
			@RequestParam(value = "otTemplateId", required = true) Long otTemplateId,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		otTemplateLogic.deleteOTTemplate(companyId, otTemplateId);
	}

	@Override
	@RequestMapping(value = "/getOTTemplate", method = RequestMethod.POST)
	@ResponseBody public String getOTTemplate(
			@RequestParam(value = "otTemplateId", required = true) Long otTemplateId,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		OTTemplateForm otTemplateForm = otTemplateLogic.getOTTemplate(
				companyId, otTemplateId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(otTemplateForm,
				jsonConfig);

		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/configureOTTemplate", method = RequestMethod.POST)
	@ResponseBody public String configureOTTemplate(
			@ModelAttribute(value = "otTemplateForm") OTTemplateForm otTemplateForm,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		String Status = otTemplateLogic.configureOTTemplate(otTemplateForm,
				companyId);

		return Status;
	}

	@Override
	@RequestMapping(value = "/getOTType", method = RequestMethod.POST)
	@ResponseBody public String getOTType(
			@RequestParam(value = "otTemplateId", required = true) Long otTemplateId,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		OTTemplateResponse otTemplateResponse = new OTTemplateResponse();

		Set<OTTemplateForm> otTypeList = otTemplateLogic.getOTTypeList(
				companyId, otTemplateId);
		otTemplateResponse.setOtTemplateSet(otTypeList);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(otTemplateResponse,
				jsonConfig);

		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/addOTType", method = RequestMethod.POST)
	public @ResponseBody void addOTType(
			@RequestParam(value = "otTypeId", required = true) String[] otTypeId,
			@RequestParam(value = "otTemplateId", required = true) Long otTemplateId) {

		otTemplateLogic.addOTType(otTypeId, otTemplateId);
	}

	@RequestMapping(value = "/viewOTType", method = RequestMethod.POST)
	@Override
	@ResponseBody public String viewOTType(
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "otTemplateId", required = true) Long otTemplateId,
			HttpServletRequest request, HttpServletResponse response) {

		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		OTTemplateResponse otTemplateResponse = otTemplateLogic.viewOTType(
				otTemplateId, companyId, pageDTO, sortDTO);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(otTemplateResponse,
				jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/editOTType", method = RequestMethod.POST)
	public @ResponseBody void editOTType(
			@RequestParam(value = "otTemplateTypeId", required = true) Long otTemplateTypeId,
			@ModelAttribute(value = "otTemplateForm") OTTemplateForm otTemplateForm) {

		otTemplateLogic.editOTType(otTemplateTypeId, otTemplateForm);
	}

	@Override
	@RequestMapping(value = "/getOTTypeForEdit", method = RequestMethod.POST)
	@ResponseBody public String getOTTypeForEdit(
			@RequestParam(value = "otTemplateTypeId", required = true) Long otTemplateTypeId) {

		OTTemplateForm otTemplateForm = otTemplateLogic
				.getOTTypeForEdit(otTemplateTypeId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(otTemplateForm,
				jsonConfig);

		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/deleteOTType", method = RequestMethod.POST)
	public @ResponseBody void deleteOTType(
			@RequestParam(value = "otTemplateTypeId", required = true) Long otTemplateTypeId,
			@RequestParam(value = "otTemplateId", required = true) Long otTemplateId) {

		otTemplateLogic.deleteOTType(otTemplateTypeId, otTemplateId);
	}

	@RequestMapping(value = "/configureOTItems", method = RequestMethod.GET)
	@ResponseBody public String configureClaimItem() {
		ConfigureClaimItemResponse response = new ConfigureClaimItemResponse();

		List<ConfigureClaimItemForm> list = new ArrayList<ConfigureClaimItemForm>();
		ConfigureClaimItemForm configureClaimItemForm1 = new ConfigureClaimItemForm();
		configureClaimItemForm1.setItem("Day Shift");
		configureClaimItemForm1.setShortlist("hr");
		configureClaimItemForm1.setVisible("Visible");

		list.add(configureClaimItemForm1);
		ConfigureClaimItemForm configureClaimItemForm2 = new ConfigureClaimItemForm();
		configureClaimItemForm2.setItem("Night Shift");
		configureClaimItemForm2.setShortlist("hr");
		configureClaimItemForm2.setVisible("Visible");

		list.add(configureClaimItemForm2);
		ConfigureClaimItemForm configureClaimItemForm3 = new ConfigureClaimItemForm();
		configureClaimItemForm3.setItem("On call");
		configureClaimItemForm3.setShortlist("hr");
		configureClaimItemForm3.setVisible("Visible");

		list.add(configureClaimItemForm3);
		ConfigureClaimItemForm configureClaimItemForm4 = new ConfigureClaimItemForm();
		configureClaimItemForm4.setItem("Stand By");
		configureClaimItemForm4.setShortlist("hr");
		configureClaimItemForm4.setVisible("Visible");

		list.add(configureClaimItemForm4);
		ConfigureClaimItemForm configureClaimItemForm5 = new ConfigureClaimItemForm();
		configureClaimItemForm5.setItem("Meals");
		configureClaimItemForm5.setShortlist("Amount");
		configureClaimItemForm5.setVisible("Visible");

		list.add(configureClaimItemForm5);
		ConfigureClaimItemForm configureClaimItemForm6 = new ConfigureClaimItemForm();
		configureClaimItemForm6.setItem("Local Travel");
		configureClaimItemForm6.setShortlist("Amount");
		configureClaimItemForm6.setVisible("Visible");

		list.add(configureClaimItemForm6);

		int totalRecords;
		totalRecords = list.size();
		response.setRows(list);
		int recordSize = totalRecords;
		int pageSize = 5;
		int totalPages = recordSize / pageSize;
		if (recordSize % pageSize != 0) {
			totalPages = totalPages + 1;
		}
		if (recordSize == 0) {
			totalPages = 0;
		}

		response.setRecords(String.valueOf(recordSize));
		response.setPage("1");
		response.setTotal(String.valueOf(totalPages));

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(response, jsonConfig);
		return jsonObject.toString();
	}

	@RequestMapping(value = "/addCustomField", method = RequestMethod.GET)
	@ResponseBody public String addCustomField() {
		ClaimCustomFieldResponse response = new ClaimCustomFieldResponse();

		List<ClaimCustomFieldForm> list = new ArrayList<ClaimCustomFieldForm>();
		ClaimCustomFieldForm claimCustomFieldForm1 = new ClaimCustomFieldForm();
		claimCustomFieldForm1.setId("1");
		claimCustomFieldForm1.setDescription("");
		claimCustomFieldForm1.setMandatory("Yes");

		list.add(claimCustomFieldForm1);

		int totalRecords;
		totalRecords = list.size();
		response.setRows(list);
		int recordSize = totalRecords;
		int pageSize = 5;
		int totalPages = recordSize / pageSize;
		if (recordSize % pageSize != 0) {
			totalPages = totalPages + 1;
		}
		if (recordSize == 0) {
			totalPages = 0;
		}

		response.setRecords(String.valueOf(recordSize));
		response.setPage("1");
		response.setTotal(String.valueOf(totalPages));

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(response, jsonConfig);
		return jsonObject.toString();
	}
}
