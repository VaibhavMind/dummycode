package com.payasia.web.controller.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.payasia.common.form.CompanyForm;
import com.payasia.common.form.CompanyGroupForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.logic.CompanyGroupLogic;
import com.payasia.logic.CompanyInformationLogic;
import com.payasia.web.controller.CompanyGroupController;
import com.payasia.web.util.PayAsiaSessionAttributes;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

@Controller
@RequestMapping(value = "/admin/companyGroup")
public class CompanyGroupControllerImpl implements CompanyGroupController {
	private static final Logger LOGGER = Logger
			.getLogger(CompanyGroupControllerImpl.class);
	@Resource
	CompanyGroupLogic companyGroupLogic;

	@Resource
	CompanyInformationLogic companyInformationLogic;

	@Override
	@RequestMapping(value = "/viewGroups.html", method = RequestMethod.POST)
	@ResponseBody public String viewGroups(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			HttpServletRequest request) {
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		CompanyGroupForm response = companyGroupLogic.viewGroup(pageDTO,
				sortDTO, companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(response, jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/addCompanyGroup.html", method = RequestMethod.POST)
	@ResponseBody public String addCompanyGroup(
			@ModelAttribute("companyGroupForm") CompanyGroupForm companyGroupForm,
			HttpServletRequest request) {
		CompanyGroupForm response = companyGroupLogic
				.checkGroup(companyGroupForm);
		if ("notavailable".equalsIgnoreCase(response.getStatus())) {
			companyGroupLogic.addCompanyGroup(companyGroupForm);
		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(response, jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/getCompanyGroup.html", method = RequestMethod.POST)
	@ResponseBody public String getCompanyGroup(
			@RequestParam(value = "groupId", required = true) Long groupId) {

		CompanyGroupForm response = companyGroupLogic
				.getCompanyGroupById(groupId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(response, jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;

	}

	@Override
	@RequestMapping(value = "/updateCompanyGroup.html", method = RequestMethod.POST)
	@ResponseBody public String updateCompanyGroup(
			@ModelAttribute("companyGroupForm") CompanyGroupForm companyGroupForm,
			HttpServletRequest request) {

		CompanyGroupForm response = companyGroupLogic
				.checkGroupUpdate(companyGroupForm);
		if ("notavailable".equalsIgnoreCase(response.getStatus())) {
			companyGroupLogic.upadateCompanyGroup(companyGroupForm);
		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(response, jsonConfig);
		return jsonObject.toString();

	}

	@Override
	@RequestMapping(value = "/deleteCompanyGroup.html", method = RequestMethod.POST)
	@ResponseBody public String deleteCompanyGroup(
			@RequestParam(value = "groupId") Long groupId) {

		CompanyGroupForm response = new CompanyGroupForm();
		try {
			companyGroupLogic.deleteClaimCategory(groupId);
			response.setStatus("Company Group Successfully deleted");
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			response.setStatus("Company Group Cannot be deleted");
		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(response, jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/getCompanyGroupList.html", method = RequestMethod.POST)
	@ResponseBody public String getCompanyGroupList() {
		List<CompanyForm> companyGroupList = companyInformationLogic
				.getCompanyGroup();

		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(companyGroupList,
				jsonConfig);
		return jsonObject.toString();

	}

}
