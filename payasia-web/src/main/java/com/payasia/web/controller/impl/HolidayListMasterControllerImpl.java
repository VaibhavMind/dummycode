package com.payasia.web.controller.impl;

/**
 * @author vivekjain
 * 
 */
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.DataImportLogDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.HolidayListMasterForm;
import com.payasia.common.form.HolidayListMasterResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.FileUtils;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.logic.HolidayListMasterLogic;
import com.payasia.web.controller.HolidayListMasterController;
import com.payasia.web.util.PayAsiaSessionAttributes;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * The Class HolidayListMasterControllerImpl.
 */

@Controller
@RequestMapping(value = "/admin/holidayListMaster")
public class HolidayListMasterControllerImpl implements
		HolidayListMasterController {

	/** The Constant logger. */
	private static final Logger LOGGER = Logger
			.getLogger(HolidayListMasterControllerImpl.class);

	/** The holiday list master logic. */
	@Resource
	HolidayListMasterLogic holidayListMasterLogic;

	@Autowired
	private MessageSource messageSource;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.HolidayListMasterController#viewHolidayList
	 * (java.lang.String, java.lang.String, int, int, java.lang.Long,
	 * java.lang.Long, int, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	@RequestMapping(value = "/viewHolidayList.html", method = RequestMethod.POST)
	public @ResponseBody
	String viewHolidayList(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			@RequestParam(value = "countryId", required = false) String countryId,
			@RequestParam(value = "stateId", required = false) String stateId,
			@RequestParam(value = "year", required = false) String year,
			HttpServletRequest request) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		HolidayListMasterResponse response = holidayListMasterLogic
				.getHolidayList(pageDTO, sortDTO, countryId, stateId, year,
						companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(response, jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.HolidayListMasterController#getHolidayData
	 * (java.lang.Long, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	@RequestMapping(value = "/getHolidayData.html", method = RequestMethod.POST)
	public @ResponseBody
	String getHolidayData(
			@RequestParam(value = "holidayId", required = true) Long holidayId,
			HttpServletRequest request) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		// ID DYCRYPT
		holidayId = FormatPreserveCryptoUtil.decrypt(holidayId);
		HolidayListMasterForm response = holidayListMasterLogic.getHolidayData(
				holidayId, companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(response, jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.HolidayListMasterController#getStateList(java
	 * .lang.Long)
	 */
	@Override
	@RequestMapping(value = "/getStateList.html", method = RequestMethod.POST)
	public @ResponseBody
	String getStateList(
			@RequestParam(value = "countryId", required = true) Long countryId) {
		List<HolidayListMasterForm> stateList = holidayListMasterLogic
				.getStateList(countryId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(stateList, jsonConfig);
		return jsonObject.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.HolidayListMasterController#getCountryList()
	 */
	@Override
	@RequestMapping(value = "/getCountryList.html", method = RequestMethod.POST)
	public @ResponseBody
	String getCountryList() {
		List<HolidayListMasterForm> countryList = holidayListMasterLogic
				.getCountryList();
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(countryList, jsonConfig);
		return jsonObject.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.HolidayListMasterController#getHolidayListFromXL
	 * (com.payasia.common.form.HolidayListMasterForm,
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/getHolidayListFromXL.html", method = RequestMethod.POST)
	public @ResponseBody
	String getHolidayListFromXL(
			@ModelAttribute("holidayListMasterForm") HolidayListMasterForm holidayListMasterForm,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) throws Exception {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		HolidayListMasterForm holidayListMasterFrm = new HolidayListMasterForm();
		
		
		if(holidayListMasterForm.getFileUpload() != null){
			
			boolean isFileValid = FileUtils.isValidFile(holidayListMasterForm.getFileUpload(), holidayListMasterForm.getFileUpload().getOriginalFilename(), PayAsiaConstants.ALLOWED_STANDARD_UPLOAD_FILE_EXT, PayAsiaConstants.ALLOWED_STANDARD_UPLOAD_FILE_MINE_TYPE, PayAsiaConstants.FILE_SIZE);
		
			if(isFileValid){
				holidayListMasterFrm = holidayListMasterLogic
						.getHolidayListFromXL(holidayListMasterForm, companyId);
				if (holidayListMasterFrm.getDataImportLogDTOs() != null) {
					for (DataImportLogDTO dataImportLogDTO : holidayListMasterFrm
							.getDataImportLogDTOs()) {
						try {
							dataImportLogDTO.setRemarks(URLEncoder.encode(messageSource
									.getMessage(dataImportLogDTO.getRemarks(),
											new Object[] {}, locale), "UTF-8"));
						} catch (UnsupportedEncodingException | NoSuchMessageException exception) {
							LOGGER.error(exception.getMessage(), exception);
							throw new PayAsiaSystemException(exception.getMessage(),
									exception);
						}
					}
			}
		}
		
		}
		
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(holidayListMasterFrm,
				jsonConfig);
		return jsonObject.toString();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.HolidayListMasterController#addHolidayMaster
	 * (com.payasia.common.form.HolidayListMasterForm,
	 * javax.servlet.http.HttpServletRequest)
	 */
	@Override
	@RequestMapping(value = "/addHolidayMaster.html", method = RequestMethod.POST)
	public @ResponseBody
	String addHolidayMaster(
			@ModelAttribute("holidayListMasterForm") HolidayListMasterForm holidayListMasterForm,
			HttpServletRequest request) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		HolidayListMasterForm holidayStatus = holidayListMasterLogic
				.addHolidayMaster(holidayListMasterForm, companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject
				.fromObject(holidayStatus, jsonConfig);
		return jsonObject.toString();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.HolidayListMasterController#editHolidayMaster
	 * (com.payasia.common.form.HolidayListMasterForm, java.lang.Long,
	 * javax.servlet.http.HttpServletRequest)
	 */
	@Override
	@RequestMapping(value = "/editHolidayMaster.html", method = RequestMethod.POST)
	public @ResponseBody
	String editHolidayMaster(
			@ModelAttribute("holidayListMasterForm") HolidayListMasterForm holidayListMasterForm,
			@RequestParam(value = "holidayId", required = true) Long holidayId,
			HttpServletRequest request) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		holidayListMasterForm.setHolidayId(FormatPreserveCryptoUtil.decrypt(holidayListMasterForm.getHolidayId()));
		holidayId = FormatPreserveCryptoUtil.decrypt(holidayId);
		HolidayListMasterForm holidayStatus = holidayListMasterLogic
				.editHolidayMaster(holidayListMasterForm, holidayId, companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject
				.fromObject(holidayStatus, jsonConfig);
		return jsonObject.toString();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.HolidayListMasterController#deleteHolidayMaster
	 * (java.lang.Long)
	 */
	@Override
	@RequestMapping(value = "/deleteHolidayMaster.html", method = RequestMethod.POST)
	public @ResponseBody
	String deleteHolidayMaster(
			@RequestParam(value = "holidayId", required = true) Long holidayId) {
		
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		// ID DYCRYPT
		holidayId = FormatPreserveCryptoUtil.decrypt(holidayId);
		List<Integer> yearList = holidayListMasterLogic
				.deleteHolidayMaster(holidayId,companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(yearList, jsonConfig);
		return jsonObject.toString();

	}

	@Override
	@RequestMapping(value = "/getYearList.html", method = RequestMethod.POST)
	public @ResponseBody
	String getYearList(HttpServletRequest request, HttpServletResponse response) {
		HolidayListMasterForm holidayListMasterForm = new HolidayListMasterForm();
		holidayListMasterForm.setYearList(holidayListMasterLogic.getYearList());

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(holidayListMasterForm,
				jsonConfig);
		return jsonObject.toString();
	}
}