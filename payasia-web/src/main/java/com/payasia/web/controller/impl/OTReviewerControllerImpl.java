package com.payasia.web.controller.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

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

import com.payasia.common.form.OTReviewerForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.logic.OTReviewerLogic;
import com.payasia.web.controller.OTReviewerController;
import com.payasia.web.util.PayAsiaSessionAttributes;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

@Controller
@RequestMapping(value = "/otReview")
public class OTReviewerControllerImpl implements OTReviewerController {
	private static final Logger LOGGER = Logger
			.getLogger(OTReviewerControllerImpl.class);
	@Resource
	OTReviewerLogic oTReviewerLogic;

	@Override
	@RequestMapping(value = "/getOTReviewers", method = RequestMethod.POST)
	@ResponseBody public String getOTReviewers(
			@RequestParam(value = "otTemplateId", required = true) Long otTemplateId) {

		OTReviewerForm oTReviewerForm = oTReviewerLogic
				.getOTReviewers(otTemplateId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(oTReviewerForm,
				jsonConfig);

		try {

			return URLEncoder.encode(jsonObject.toString(), "UTF8");
		} catch (UnsupportedEncodingException e) {

			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/saveOTReviewer", method = RequestMethod.POST)
	@ResponseBody public String saveOTReviewer(
			@ModelAttribute("otReviewerForm") OTReviewerForm otReviewerForm,
			BindingResult result, ModelMap model, HttpServletRequest request) {

		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long employeeId = otReviewerForm.getEmployeeId();
		OTReviewerForm oTReviewerFormResponse = oTReviewerLogic
				.checkEmployeeReviewer(employeeId, companyId);

		if (oTReviewerFormResponse.getEmployeeStatus().equalsIgnoreCase(
				"notexists")) {

			try {

				oTReviewerLogic.saveOTReviewer(otReviewerForm, companyId);
				oTReviewerFormResponse.setStatus("success");

			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
				oTReviewerFormResponse.setStatus("failure");

			}
		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(oTReviewerFormResponse,
				jsonConfig);
		try {

			return URLEncoder.encode(jsonObject.toString(), "UTF8");
		} catch (UnsupportedEncodingException e) {

			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/viewOTReviewers", method = RequestMethod.GET)
	@ResponseBody public String viewClaimReviewers(
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

		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		OTReviewerForm oTReviewerFormResponse = oTReviewerLogic.getOTReviewers(
				searchCondition, searchText, pageDTO, sortDTO, companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(oTReviewerFormResponse,
				jsonConfig);
		try {

			return URLEncoder.encode(jsonObject.toString(), "UTF8");
		} catch (UnsupportedEncodingException e) {

			LOGGER.error(e.getMessage(), e);
		}
		return null;

	}

	@Override
	@RequestMapping(value = "/getOTReviewerData", method = RequestMethod.POST)
	@ResponseBody public String getOTReviewerData(
			@RequestParam(value = "employeeId", required = true) Long employeeId,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {

		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		OTReviewerForm otReviewerForm = oTReviewerLogic.getOTReviewerData(
				employeeId, companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(otReviewerForm,
				jsonConfig);
		try {

			return URLEncoder.encode(jsonObject.toString(), "UTF8");
		} catch (UnsupportedEncodingException e) {

			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/updateOTReviewer", method = RequestMethod.POST)
	@ResponseBody public String updateOTReviewer(
			@ModelAttribute("otReviewerForm") OTReviewerForm otReviewerForm,
			BindingResult result, ModelMap model, HttpServletRequest request) {

		OTReviewerForm oTReviewerFormResponse = new OTReviewerForm();
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		try {
			oTReviewerLogic.updateOTReviewer(otReviewerForm, companyId);
			oTReviewerFormResponse.setStatus("success");
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			oTReviewerFormResponse.setStatus("failure");

		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(oTReviewerFormResponse,
				jsonConfig);
		try {

			return URLEncoder.encode(jsonObject.toString(), "UTF8");
		} catch (UnsupportedEncodingException e) {

			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/deleteOTReviewer", method = RequestMethod.POST)
	@ResponseBody public String deleteOTReviewer(
			@ModelAttribute("employeeId") Long employeeId,
			BindingResult result, ModelMap model, HttpServletRequest request) {

		OTReviewerForm otReviewerFormResponse = new OTReviewerForm();
		try {

			oTReviewerLogic.deleteOTReviewer(employeeId);
			otReviewerFormResponse.setStatus("success");
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			otReviewerFormResponse.setStatus("failure");

		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(otReviewerFormResponse,
				jsonConfig);
		return jsonObject.toString();
	}

}
