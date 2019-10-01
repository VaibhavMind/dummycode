package com.payasia.web.controller.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
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

import com.payasia.common.dto.DataImportLogDTO;
import com.payasia.common.exception.PayAsiaRollBackDataException;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.CurrencyDefinitionForm;
import com.payasia.common.form.CurrencyDefinitionResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.FileUtils;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.logic.CurrencyDefinitionLogic;
import com.payasia.web.controller.CurrencyDefinitionController;
import com.payasia.web.util.PayAsiaSessionAttributes;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * The Class CurrencyDefinitionControllerImpl.
 */
/**
 * @author vivekjain
 * 
 */
@Controller
@RequestMapping(value = "/admin/currencyDefinition")
public class CurrencyDefinitionControllerImpl implements
		CurrencyDefinitionController {

	/** The Constant logger. */
	private static final Logger LOGGER = Logger
			.getLogger(CurrencyDefinitionControllerImpl.class);

	/** The currency definition logic. */
	@Resource
	CurrencyDefinitionLogic currencyDefinitionLogic;

	/** The message source. */
	@Autowired
	private MessageSource messageSource;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.web.controller.CurrencyDefinitionController#
	 * viewCurrencyDefinition(int, int, java.lang.String, java.lang.String,
	 * java.lang.Integer, javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/viewCurrencyDefinition.html", method = RequestMethod.POST)
	@ResponseBody public String viewCurrencyDefinition(
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "currencyId", required = true) Long currencyId,
			@RequestParam(value = "currencyDate", required = true) String currencyDate,
			HttpServletRequest request, HttpServletResponse response) {

		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		CurrencyDefinitionResponse currencyDefinitionResponse = currencyDefinitionLogic
				.viewCurrencyDefinition(companyId, currencyId, currencyDate,
						pageDTO, sortDTO);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(
				currencyDefinitionResponse, jsonConfig);
		return jsonObject.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.CurrencyDefinitionController#addCurrencyDefinition
	 * (com.payasia.common.form.CurrencyDefinitionForm,
	 * org.springframework.validation.BindingResult,
	 * org.springframework.ui.ModelMap, javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, java.util.Locale)
	 */
	@Override
	@RequestMapping(value = "/addCurrencyDefinition.html", method = RequestMethod.POST)
	@ResponseBody public String addCurrencyDefinition(
			@ModelAttribute("currencyDefinitionForm") CurrencyDefinitionForm currencyDefinitionForm,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		String currencyDefinitionStatus = currencyDefinitionLogic
				.addCurrencyDefinition(currencyDefinitionForm, companyId);

		try {
			currencyDefinitionStatus = URLEncoder.encode(messageSource
					.getMessage(currencyDefinitionStatus, new Object[] {},
							locale), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		} catch (NoSuchMessageException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}

		return currencyDefinitionStatus;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.web.controller.CurrencyDefinitionController#
	 * updateCurrencyDefinition(java.lang.Long, java.lang.Integer,
	 * com.payasia.common.form.CurrencyDefinitionForm,
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/editCurrencyDefinition.html", method = RequestMethod.POST)
	@ResponseBody public String updateCurrencyDefinition(
			@ModelAttribute("currencyDefinitionForm") CurrencyDefinitionForm currencyDefinitionForm,
			HttpServletRequest request, HttpServletResponse response) {
		
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		String status = currencyDefinitionLogic.updateCurrencyDefinition(
				currencyDefinitionForm, companyId);
		return status;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.web.controller.CurrencyDefinitionController#
	 * getDataForCurrencyDefinition(java.lang.Long, java.lang.Integer,
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/currencyDefinitionDataForEdit.html", method = RequestMethod.POST)
	@ResponseBody public String getDataForCurrencyDefinition(
			@RequestParam(value = "companyExchangeRateId", required = true) Long companyExchangeRateId,
			HttpServletRequest request, HttpServletResponse response) {
		/*ID DECRYPT*/
		companyExchangeRateId=FormatPreserveCryptoUtil.decrypt(companyExchangeRateId);
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		CurrencyDefinitionForm currencyDefinitionList = currencyDefinitionLogic
				.getDataForCurrencyDefinition(companyId, companyExchangeRateId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(currencyDefinitionList,
				jsonConfig);
		return jsonObject.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.web.controller.CurrencyDefinitionController#
	 * deleteCurrencyDefinition(java.lang.Long, java.lang.Integer,
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/deleteCurrencyDefinition.html", method = RequestMethod.POST)
	public void deleteCurrencyDefinition(
			@RequestParam(value = "companyExchangeRateId", required = true) Long companyExchangeRateId,
			HttpServletRequest request, HttpServletResponse response) {
		/*ID DECRYPT*/
		companyExchangeRateId=FormatPreserveCryptoUtil.decrypt(companyExchangeRateId);
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		currencyDefinitionLogic.deleteCurrencyDefinition(companyId,
				companyExchangeRateId);
	}

	@Override
	@RequestMapping(value = "/getBaseCurrency.html", method = RequestMethod.POST)
	@ResponseBody public String getBaseCurrency(HttpServletRequest request,
			HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		String baseCurrency = currencyDefinitionLogic
				.getBaseCurrency(companyId);

		return baseCurrency;
	}

	@Override
	@RequestMapping(value = "/importCompanyExchangeRate.html", method = RequestMethod.POST)
	@ResponseBody
	public String importCompanyExchangeRate(
			@ModelAttribute(value = "currencyDefinitionForm") CurrencyDefinitionForm currencyDefinitionForm,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) throws Exception {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		CurrencyDefinitionForm currencyDefinitionFrm = new CurrencyDefinitionForm();
		try {
			
			boolean isVaildFile = false;
			
			if(currencyDefinitionForm.getFileUpload()!=null){
		       isVaildFile = FileUtils.isValidFile(currencyDefinitionForm.getFileUpload(), currencyDefinitionForm.getFileUpload().getOriginalFilename(), PayAsiaConstants.ALLOWED_ONLY_UPLOAD_EXCLE_FILE_EXT, PayAsiaConstants.ALLOWED_ONLY_UPLOAD_EXCLE_MINE_TYPE, PayAsiaConstants.FILE_SIZE);
			}
			
			if(isVaildFile){
				currencyDefinitionFrm = currencyDefinitionLogic.importCompanyExchangeRate(currencyDefinitionForm,companyId);
			}else{
				List<DataImportLogDTO> errors = new ArrayList<DataImportLogDTO>();
				DataImportLogDTO error = new DataImportLogDTO();
				error.setFailureType("payasia.record.error");
				error.setRemarks("payasia.record.error");
				error.setFromMessageSource(false);
				errors.add(error);
				currencyDefinitionFrm.setDataValid(false);
				currencyDefinitionFrm.setDataImportLogDTOs(errors);
			}
			
		} catch (PayAsiaRollBackDataException ex) {
			LOGGER.error(ex.getMessage(), ex);
			currencyDefinitionFrm.setDataValid(false);
			currencyDefinitionFrm.setDataImportLogDTOs(ex.getErrors());
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			List<DataImportLogDTO> errors = new ArrayList<DataImportLogDTO>();
			DataImportLogDTO error = new DataImportLogDTO();

			error.setFailureType("payasia.record.error");
			error.setRemarks("payasia.record.error");
			error.setFromMessageSource(false);

			errors.add(error);
			currencyDefinitionFrm.setDataValid(false);
			currencyDefinitionFrm.setDataImportLogDTOs(errors);
		}

		if (currencyDefinitionFrm.getDataImportLogDTOs() != null) {
			for (DataImportLogDTO dataImportLogDTO : currencyDefinitionFrm
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

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(currencyDefinitionFrm,
				jsonConfig);
		return jsonObject.toString();
	}

}
