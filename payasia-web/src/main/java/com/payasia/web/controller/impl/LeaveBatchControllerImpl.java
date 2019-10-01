package com.payasia.web.controller.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.payasia.common.bean.util.UserContext;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.LeaveBatchForm;
import com.payasia.common.form.LeaveBatchResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.LeaveBatchMasterDAO;
import com.payasia.dao.bean.LeaveBatchMaster;
import com.payasia.logic.LeaveBatchLogic;
import com.payasia.web.controller.LeaveBatchController;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

@Controller
public class LeaveBatchControllerImpl implements LeaveBatchController {
	private static final Logger LOGGER = Logger
			.getLogger(LeaveBatchControllerImpl.class);

	@Resource
	LeaveBatchLogic leaveBatchLogic;
	
	@Resource
	LeaveBatchMasterDAO leaveBatchMasterDAO;

	@RequestMapping(value = "/admin/leaveBatch/addLeaveBatch.html", method = RequestMethod.POST)
	@ResponseBody public String addLeaveBatch(
			@ModelAttribute("leaveBatchForm") LeaveBatchForm leaveBatchForm) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		String responseStatus = leaveBatchLogic.addLeaveBatch(leaveBatchForm,
				companyId);

		return responseStatus;
	}

	@Override
	@RequestMapping(value = "/admin/leaveBatch/viewLeaveBatch.html", method = RequestMethod.POST)
	@ResponseBody public String getLeaveBatch(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			@RequestParam(value = "leaveBatchFilter", required = true) String leaveBatchFilter,
			@RequestParam(value = "filterText", required = true) String filterText) {

		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);
		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		LeaveBatchResponse response = leaveBatchLogic.viewLeaveBatch(pageDTO,
				sortDTO, leaveBatchFilter, filterText, companyId);
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
	@RequestMapping(value = "/admin/leaveBatch/getLeaveBatch.html", method = RequestMethod.POST)
	@ResponseBody public String getLeaveBatchData(
			@RequestParam(value = "leaveBatchID", required = true) Long leaveBatchId) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		/*LeaveBatchForm response = leaveBatchLogic
				.getLeaveBatchData(leaveBatchId);*/
		LeaveBatchForm response = leaveBatchLogic
				.getLeaveBatchDataByCompany(leaveBatchId, companyId);
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
	@RequestMapping(value = "/admin/leaveBatch/editLeaveBatch.html", method = RequestMethod.POST)
	@ResponseBody public String editLeaveBatch(
			@ModelAttribute("leaveBatchForm") LeaveBatchForm leaveBatchForm,
			@RequestParam(value = "leaveBatchID", required = true) Long leaveBatchId) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		String responseString = leaveBatchLogic.editLeaveBatch(leaveBatchForm,
				leaveBatchId, companyId);

		return responseString.toLowerCase();

	}

	@Override
	@RequestMapping(value = "/admin/leaveBatch/deleteLeaveBatch.html", method = RequestMethod.POST)
	public @ResponseBody void deleteLeaveBatch(
			@RequestParam(value = "leaveBatchID", required = true) Long leaveBatchId) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		LeaveBatchMaster leaveBatchMasterCheck = leaveBatchMasterDAO
				.findByCompID(leaveBatchId, companyId);
		if(leaveBatchMasterCheck == null)
		{
			throw new PayAsiaSystemException("Authentication Exception");
		}
		leaveBatchLogic.deleteLeaveBatch(leaveBatchId);

	}
}
