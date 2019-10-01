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

import com.payasia.common.form.OTBatchForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.logic.OTBatchDefinitionLogic;
import com.payasia.web.controller.OTBatchController;
import com.payasia.web.util.PayAsiaSessionAttributes;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

@Controller
@RequestMapping(value = "/oTBatch")
public class OTBatchControllerImpl implements OTBatchController {
	private static final Logger LOGGER = Logger
			.getLogger(OTBatchControllerImpl.class);
	@Resource
	OTBatchDefinitionLogic oTBatchDefinitionLogic;

	@Override
	@RequestMapping(value = "/saveOTBatch", method = RequestMethod.POST)
	@ResponseBody public String saveOTBatch(
			@ModelAttribute("oTBatchForm") OTBatchForm oTBatchForm,
			BindingResult result, ModelMap model, HttpServletRequest request) {

		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		OTBatchForm oTBatchResponse = oTBatchDefinitionLogic.checkOTItem(
				companyId, oTBatchForm, null);
		if ("success".equalsIgnoreCase(oTBatchResponse.getStatus())) {
			oTBatchDefinitionLogic.saveOTItem(oTBatchForm, companyId);
		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(oTBatchResponse,
				jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/updateOTBatch", method = RequestMethod.POST)
	@ResponseBody public String updateOTBatch(
			@ModelAttribute("oTBatchForm") OTBatchForm oTBatchForm,
			BindingResult result, ModelMap model, HttpServletRequest request) {

		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		OTBatchForm oTBatchResponse = oTBatchDefinitionLogic.checkOTItem(
				companyId, oTBatchForm, oTBatchForm.getOtBatchId());
		if ("success".equalsIgnoreCase(oTBatchResponse.getStatus())) {
			oTBatchDefinitionLogic.updateOTItem(oTBatchForm, companyId);
		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(oTBatchResponse,
				jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/viewOTBatch", method = RequestMethod.GET)
	@ResponseBody public String viewOTBatch(
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

		OTBatchForm oTBatchFormResponse = oTBatchDefinitionLogic
				.getOTBatchList(searchCriteria, keyword, pageDTO, sortDTO,
						companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(oTBatchFormResponse,
				jsonConfig);
		return jsonObject.toString();

	}

	@Override
	@RequestMapping(value = "/deleteOTBatch", method = RequestMethod.POST)
	@ResponseBody public String deleteOTBatch(
			@RequestParam(value = "otBatchId", required = true) Long otBatchId) {

		OTBatchForm oTBatchForm = new OTBatchForm();

		try {
			oTBatchDefinitionLogic.deleteOTBatch(otBatchId);
			oTBatchForm.setStatus("success");

		} catch (Exception e) {
			oTBatchForm.setStatus("failure");
			LOGGER.error(e.getMessage(), e);

		}
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(oTBatchForm, jsonConfig);
		return jsonObject.toString();
	}

}