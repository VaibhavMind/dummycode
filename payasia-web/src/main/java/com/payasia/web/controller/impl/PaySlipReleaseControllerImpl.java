package com.payasia.web.controller.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

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

import com.payasia.common.form.PageRequest;
import com.payasia.common.form.PaySlipReleaseForm;
import com.payasia.common.form.PaySlipReleaseFormResponse;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.dao.CompanyDAO;
import com.payasia.logic.PaySlipReleaseLogic;
import com.payasia.web.controller.PaySlipReleaseController;
import com.payasia.web.util.PayAsiaSessionAttributes;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * The Class PaySlipReleaseControllerImpl.
 * 
 * @author vivek jain
 */
@Controller
@RequestMapping(value = "/admin/payslipRelease")
public class PaySlipReleaseControllerImpl implements PaySlipReleaseController {
	/** The excel export tool logic. */
	@Resource
	PaySlipReleaseLogic paySlipReleaseLogic;

	@Resource
	CompanyDAO companyDAO;

	private static final Logger LOGGER = Logger
			.getLogger(PaySlipReleaseControllerImpl.class);

	@RequestMapping(value = "/viewPayslipReleaseList", method = RequestMethod.POST)
	public @ResponseBody @Override String viewPayslipReleaseList(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "searchCondition", required = false) String searchCondition,
			@RequestParam(value = "searchText", required = false) String searchText,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session

	) {

		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		PaySlipReleaseFormResponse response1 = paySlipReleaseLogic
				.viewPayslipReleaseList(searchCondition, searchText, pageDTO,
						sortDTO, companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(response1, jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/savePaySlipRelease", method = RequestMethod.POST)
	@ResponseBody
	public String savePaySlipRelease(
			@ModelAttribute(value = "paySlipReleaseForm") PaySlipReleaseForm paySlipReleaseForm,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		String status = paySlipReleaseLogic.savePaySlipRelease(
				paySlipReleaseForm, companyId);

		return status;
	}

	@Override
	@RequestMapping(value = "/deletePaySlipRelease", method = RequestMethod.POST)
	@ResponseBody
	public String deletePaySlipRelease(
			@RequestParam(value = "companyPayslipReleaseId", required = true) Long companyPayslipReleaseId,
			HttpServletRequest request, HttpServletResponse response) {
		/*ID DECRYPT*/
		companyPayslipReleaseId = FormatPreserveCryptoUtil.decrypt(companyPayslipReleaseId);
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID); 
		String status = paySlipReleaseLogic.deletePaySlipRelease(companyPayslipReleaseId,companyId);
		return status;
	}

	@Override
	@RequestMapping(value = "/getPaySlipReleaseDetails", method = RequestMethod.POST)
	@ResponseBody
	public String getPaySlipReleaseDetails(
			@RequestParam(value = "companyPayslipReleaseId", required = true) long companyPayslipReleaseId,
			HttpServletRequest request, HttpServletResponse response) {
		/*ID DECRYPT*/
		companyPayslipReleaseId = FormatPreserveCryptoUtil.decrypt(companyPayslipReleaseId);
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID); 
		PaySlipReleaseForm paySlipReleaseForm = paySlipReleaseLogic.getPaySlipReleaseDetails(companyPayslipReleaseId,companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(paySlipReleaseForm,jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return null;

	}

	@Override
	@RequestMapping(value = "/editPaySlipRelease", method = RequestMethod.POST)
	@ResponseBody
	public String editPaySlipRelease(
			@ModelAttribute(value = "paySlipReleaseForm") PaySlipReleaseForm paySlipReleaseForm,
			@RequestParam(value = "companyPayslipReleaseId", required = true) long companyPayslipReleaseId,
			HttpServletRequest request, HttpServletResponse response) {
		/*ID DECRYPT*/
		companyPayslipReleaseId = FormatPreserveCryptoUtil.decrypt(companyPayslipReleaseId);
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		String status = paySlipReleaseLogic.editPaySlipRelease(
				paySlipReleaseForm, companyId, companyPayslipReleaseId);

		return status;
	}

	@Override
	@RequestMapping(value = "/getPaySlipPart", method = RequestMethod.POST)
	@ResponseBody
	public String getPaySlipPart(HttpServletRequest request,
			HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Integer part = paySlipReleaseLogic.getPaySlipPart(companyId);
		return part.toString();

	}

	@Override
	@RequestMapping(value = "/getPayslipSendMailDetails.html", method = RequestMethod.POST)
	@ResponseBody
	public String getPayslipSendMailDetails(HttpServletRequest request,
			HttpServletResponse response) {
		long companyId = ((Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID));
		String status = paySlipReleaseLogic
				.getPayslipSendMailDetails(companyId);
		return status;

	}
}
