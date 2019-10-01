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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SendPasswordResponse;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.logic.SendPasswordLogic;
import com.payasia.web.controller.SendPasswordController;
import com.payasia.web.util.PayAsiaSessionAttributes;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * The Class SendPasswordControllerImpl.
 */
/**
 * @author vivekjain
 * 
 */
@Controller
@RequestMapping(value = "/admin/sendPasswordPage")
public class SendPasswordControllerImpl implements SendPasswordController {

	/** The Constant logger. */
	private static final Logger LOGGER = Logger
			.getLogger(SendPasswordControllerImpl.class);

	/** The send password logic. */
	@Resource
	SendPasswordLogic sendPasswordLogic;

	/** The message source. */
	@Autowired
	private MessageSource messageSource;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.SendPasswordController#filterEmployeeList(
	 * java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * int, int, javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/searchSendPwdEmployee.html", method = RequestMethod.POST)
	public @ResponseBody
	String filterEmployeeList(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "searchCondition", required = true) String searchCondition,
			@RequestParam(value = "searchText", required = true) String searchText,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			HttpServletRequest request, HttpServletResponse response) {

		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		SendPasswordResponse sendPasswordResponse = sendPasswordLogic
				.getEmployeeList(searchCondition, searchText, pageDTO, sortDTO,
						companyId, employeeId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(sendPasswordResponse,
				jsonConfig);
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
	 * com.payasia.web.controller.SendPasswordController#sendPwdEmail(java.lang
	 * .String[], javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, java.util.Locale)
	 */
	@Override
	@RequestMapping(value = "/sendPwdEmail.html", method = RequestMethod.POST)
	public @ResponseBody
	String sendPwdEmail(
			@RequestParam(value = "employeeId", required = true) String[] employeeId,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) {

		/*ID DECRYPT*/
		List<String> list = new ArrayList<String>();
		for(String emplo : employeeId) {
			list.add(String.valueOf(FormatPreserveCryptoUtil.decrypt(Long.parseLong(emplo))));
		}
		
		String [] tempEmployeeId = list.toArray(new String[list.size()]);

		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		String sendPwdStatus = sendPasswordLogic.sendPwdEmail(companyId,
				tempEmployeeId);
		String statusEmpIds = "";
		if (sendPwdStatus.indexOf('/') > 0) {
			String tempStatus[] = sendPwdStatus.split("/");

			for (int count = 0; count < tempStatus.length; count++) {
				sendPwdStatus = tempStatus[0];
				statusEmpIds = tempStatus[1];
			}
		}
		try {

			sendPwdStatus = URLEncoder.encode(messageSource.getMessage(
					sendPwdStatus, new Object[] {}, locale), "UTF-8");
			if (statusEmpIds != "") {
				sendPwdStatus += " " + statusEmpIds;
			}
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		} catch (NoSuchMessageException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return sendPwdStatus;
	}
}
