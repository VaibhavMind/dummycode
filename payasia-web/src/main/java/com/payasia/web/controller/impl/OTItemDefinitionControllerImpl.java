package com.payasia.web.controller.impl;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.payasia.common.form.OTItemDefinitionForm;
import com.payasia.common.form.OTItemDefinitionResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.logic.OTItemDefinitionLogic;
import com.payasia.web.controller.OTItemDefinitionController;
import com.payasia.web.util.PayAsiaSessionAttributes;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

@Controller
@RequestMapping(value = "/otItem")
public class OTItemDefinitionControllerImpl implements
		OTItemDefinitionController {
	@Resource
	OTItemDefinitionLogic otItemDefinitionLogic;
	private static final Logger LOGGER = Logger
			.getLogger(OTItemDefinitionControllerImpl.class);

	@Override
	@RequestMapping(value = "/viewOTItemDefinition", method = RequestMethod.GET)
	@ResponseBody public String viewClaimItems(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "searchCondition", required = true) String searchCriteria,
			@RequestParam(value = "searchText", required = true) String keyword,
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

		OTItemDefinitionResponse otItemDefinitionResponse = otItemDefinitionLogic
				.getOTItemList(searchCriteria, keyword, pageDTO, sortDTO,
						companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(otItemDefinitionResponse,
				jsonConfig);
		return jsonObject.toString();

	}

	@Override
	@RequestMapping(value = "/updateOTItem", method = RequestMethod.POST)
	@ResponseBody public String updateOTItem(
			@ModelAttribute("otItemDefinitionForm") OTItemDefinitionForm otItemDefinitionForm,
			BindingResult result, ModelMap model, HttpServletRequest request,
			HttpSession session, HttpServletResponse response, Long otItemId) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		OTItemDefinitionForm otItemDefinitionResponse = otItemDefinitionLogic
				.checkOTItem(companyId, otItemDefinitionForm, otItemId);
		if ("success".equalsIgnoreCase(otItemDefinitionResponse.getStatus())) {
			otItemDefinitionLogic.updateOTItem(otItemDefinitionForm, otItemId);
		}
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(otItemDefinitionResponse,
				jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/saveOTItem", method = RequestMethod.POST)
	@ResponseBody public String saveOTItem(
			@ModelAttribute("leaveTypeForm") OTItemDefinitionForm otItemDefinitionForm,
			BindingResult result, ModelMap model, HttpServletRequest request) {

		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		OTItemDefinitionForm otItemDefinitionResponse = otItemDefinitionLogic
				.checkOTItem(companyId, otItemDefinitionForm, null);
		if ("success".equalsIgnoreCase(otItemDefinitionResponse.getStatus())) {
			otItemDefinitionLogic.saveOTItem(otItemDefinitionForm, companyId);
		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(otItemDefinitionResponse,
				jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/deleteOTItem", method = RequestMethod.POST)
	public String deleteOTItem(
			@RequestParam(value = "otItemId", required = true) Long otItemId) {
		OTItemDefinitionForm otItemDefinitionForm = new OTItemDefinitionForm();
		try {
			otItemDefinitionLogic.deleteOTItem(otItemId);
			otItemDefinitionForm.setDeletedMsg("OT Item Deleted Successfully.");
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			otItemDefinitionForm.setDeletedMsg("OT Item Couldn't Be Deleted.");
		}
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(otItemDefinitionForm,
				jsonConfig);
		return jsonObject.toString();
	}
}
