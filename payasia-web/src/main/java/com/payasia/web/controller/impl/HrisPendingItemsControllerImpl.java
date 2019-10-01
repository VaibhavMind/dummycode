package com.payasia.web.controller.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.payasia.common.form.HrisPendingItemWorkflowRes;
import com.payasia.common.form.HrisPendingItemsForm;
import com.payasia.common.form.HrisPendingItemsFormResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.logic.HrisPendingItemsLogic;
import com.payasia.logic.MultilingualLogic;
import com.payasia.web.controller.HrisPendingItemsController;
import com.payasia.web.util.PayAsiaSessionAttributes;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

@Controller
@RequestMapping(value = {"/employee/hrisPendingItems"})
public class HrisPendingItemsControllerImpl implements
		HrisPendingItemsController {

	/** The Constant logger. */
	private static final Logger LOGGER = Logger
			.getLogger(HrisPendingItemsControllerImpl.class);
	@Resource
	HrisPendingItemsLogic hrisPendingItemsLogic;
	/** The multilingual logic. */
	@Resource
	MultilingualLogic multilingualLogic;

	@Override
	@RequestMapping(value = "/getPendingItems.html", method = RequestMethod.POST)
	@ResponseBody public String getPendingItems(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "searchCondition", required = false) String searchCondition,
			@RequestParam(value = "searchText", required = false) String searchText,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session, Locale locale) {
		Long empId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long languageId = multilingualLogic.getLanguageId(locale.toString());
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		HrisPendingItemsFormResponse hrisPendingItemsFormResponse = null;
		hrisPendingItemsFormResponse = hrisPendingItemsLogic.getPendingItems(
				empId, companyId, pageDTO, sortDTO, searchCondition,
				searchText, languageId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(
				hrisPendingItemsFormResponse, jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/reviewHrisPendingItem.html", method = RequestMethod.POST)
	@ResponseBody public String reviewHrisPendingItem(
			@RequestParam(value = "reviewId") Long reviewId, Locale locale,
			HttpServletRequest request, HttpServletResponse response) {
		
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		Long languageId = multilingualLogic.getLanguageId(locale.toString());
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		/*ID DYCRYPT*/
		reviewId = FormatPreserveCryptoUtil.decrypt(reviewId);
		
		  /**
	     * This code & check comment as per discussion with Mr.Peeyush  
	     * */
		/*boolean isHrisReviewer = false;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		for (GrantedAuthority grantedAuthority : authentication.getAuthorities()) {
			if ("PRIV_HRIS_REVIEWER".equals(grantedAuthority.getAuthority())) {
				isHrisReviewer = true;
			}
		}
		if(!isHrisReviewer)
			throw new PayAsiaSystemException("Unauthorized_Access", "Unauthorized Access");*/
		
		HrisPendingItemsForm hrisPendingItemsForm = hrisPendingItemsLogic.reviewHrisPendingItem(reviewId, companyId, languageId,employeeId);
		
	   if(hrisPendingItemsForm!=null){
		
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(hrisPendingItemsForm,jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/forward.html", method = RequestMethod.POST)
	@ResponseBody public String forward(
			@ModelAttribute(value = "hrisPendingItemsForm") HrisPendingItemsForm hrisPendingItemsForm,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session, Locale locale) {
		
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		Long languageId = multilingualLogic.getLanguageId(locale.toString());
		/*ID DYCRYPT*/
		hrisPendingItemsForm.sethRISChangeRequestReviewerId(FormatPreserveCryptoUtil.decrypt(hrisPendingItemsForm.gethRISChangeRequestReviewerId()));
		HrisPendingItemWorkflowRes hrisPendingItemWorkflowRes = hrisPendingItemsLogic.forward(hrisPendingItemsForm, employeeId, languageId);
		if (hrisPendingItemWorkflowRes!=null)
		{
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(
				hrisPendingItemWorkflowRes, jsonConfig);

		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/accept.html", method = RequestMethod.POST)
	@ResponseBody public String accept(
			@ModelAttribute(value = "hrisPendingItemsForm") HrisPendingItemsForm hrisPendingItemsForm,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session, Locale locale) {
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		Long languageId = multilingualLogic.getLanguageId(locale.toString());
		/*ID DYCRYPT*/
		hrisPendingItemsForm.sethRISChangeRequestReviewerId(FormatPreserveCryptoUtil.decrypt(hrisPendingItemsForm.gethRISChangeRequestReviewerId()));
		HrisPendingItemWorkflowRes hrisPendingItemWorkflowRes = hrisPendingItemsLogic.accept(hrisPendingItemsForm, employeeId, languageId);
	   
		if(hrisPendingItemWorkflowRes!=null){
		
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(hrisPendingItemWorkflowRes, jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		 }
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/reject.html", method = RequestMethod.POST)
	@ResponseBody public String reject(
			@ModelAttribute(value = "hrisPendingItemsForm") HrisPendingItemsForm hrisPendingItemsForm,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session, Locale locale) {
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		Long languageId = multilingualLogic.getLanguageId(locale.toString());
		/*ID DYCRYPT*/
		hrisPendingItemsForm.sethRISChangeRequestReviewerId(FormatPreserveCryptoUtil.decrypt(hrisPendingItemsForm.gethRISChangeRequestReviewerId()));
		HrisPendingItemWorkflowRes hrisPendingItemWorkflowRes = hrisPendingItemsLogic.reject(hrisPendingItemsForm, employeeId, languageId);
		
		if (hrisPendingItemWorkflowRes!=null){
		  JsonConfig jsonConfig = new JsonConfig();
		  JSONObject jsonObject = JSONObject.fromObject(hrisPendingItemWorkflowRes, jsonConfig);

		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/searchEmployee.html", method = RequestMethod.POST)
	@ResponseBody public String searchEmployee(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			@RequestParam(value = "empName", required = true) String empName,
			@RequestParam(value = "empNumber", required = true) String empNumber,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		HrisPendingItemWorkflowRes hrisPendingItemWorkflowRes = hrisPendingItemsLogic
				.searchEmployee(pageDTO, sortDTO, employeeId, empName,
						empNumber, companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(
				hrisPendingItemWorkflowRes, jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return null;
	}

}
