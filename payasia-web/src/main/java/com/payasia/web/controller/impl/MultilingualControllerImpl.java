package com.payasia.web.controller.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.payasia.common.bean.util.UserContext;
import com.payasia.common.form.MultilingualResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.logic.MultilingualLogic;
import com.payasia.web.controller.MultilingualController;
import com.payasia.web.util.PayAsiaSessionAttributes;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

@Controller
@RequestMapping(value = "/admin/multilingual")
public class MultilingualControllerImpl implements MultilingualController {
	private static final Logger LOGGER = Logger
			.getLogger(MultilingualControllerImpl.class);
	@Resource
	MultilingualLogic multilingualLogic;

	@RequestMapping(value = "/updateMultilingualLabel.html", method = RequestMethod.POST)
	public void updateMultilingualLabel(
			@RequestParam(value = "dataDictionaryId", required = true) Long dataDictionaryId,
			@RequestParam(value = "languageId", required = true) Long languageId,
			@RequestParam(value = "labelValue", required = true) String labelValue,
			HttpServletRequest request) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		/*
		 * ID DYCRYPT
		 * */
		dataDictionaryId = FormatPreserveCryptoUtil.decrypt(dataDictionaryId);
		multilingualLogic.updateMultilingualLabel(dataDictionaryId, languageId,
				labelValue, companyId);

	}

	@RequestMapping(value = "/deleteMultilingualRecord.html", method = RequestMethod.POST)
	public void deleteMultilingualRecord(
			@RequestParam(value = "dataDictionaryId", required = true) Long dataDictionaryId,
			@RequestParam(value = "languageId", required = true) Long languageId) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		
		/*
		 * ID DYCRYPT
		 * */
		dataDictionaryId = FormatPreserveCryptoUtil.decrypt(dataDictionaryId);
		multilingualLogic.deleteMultilingualRecord(dataDictionaryId, languageId,companyId);
	}

	@RequestMapping(value = "/getMultilingualLabels.html", method = RequestMethod.POST)
	public @ResponseBody @Override String getMultilingualLabels(
			@RequestParam(value = "entityId", required = false) Long entityId,
			@RequestParam(value = "languageId", required = false) Long languageId,
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
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

		MultilingualResponse multilingualResponse = multilingualLogic
				.getMultilingualLabelsList(entityId, languageId, pageDTO,
						sortDTO, companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(multilingualResponse,
				jsonConfig);
		 
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@RequestMapping(value = "/getTest.html", method = RequestMethod.POST)
	public @ResponseBody @Override String getMultilingualLabels(
			@RequestParam(value = "entityId", required = false) Long entityId,
			@RequestParam(value = "languageId", required = false) Long languageId,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {

		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		response.setContentType("text/html; charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		MultilingualResponse multilingualResponse = multilingualLogic
				.getMultilingualLabelsList(1L, 1L, null, null, companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(multilingualResponse,
				jsonConfig);
		return jsonObject.toString();

	}
}
