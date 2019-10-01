/**
 * @author vivekjain
 *
 */
package com.payasia.web.controller.impl;

import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.payasia.common.form.AnouncementForm;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.logic.AnouncementLogic;
import com.payasia.web.controller.AnouncementController;
import com.payasia.web.util.PayAsiaSessionAttributes;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * The Class AnouncementControllerImpl.
 * 
 */

@Controller
public class AnouncementControllerImpl implements AnouncementController {
	/** The Constant logger. */
	private static final Logger LOGGER = Logger
			.getLogger(AnouncementControllerImpl.class);
	@Resource
	AnouncementLogic anouncementLogic;

	@Override
	@RequestMapping(value = "/admin/anouncement/postAnouncement.html", method = RequestMethod.POST)
	public @ResponseBody
	void postAnouncement(
			@ModelAttribute("anouncementForm") AnouncementForm anouncementForm,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		anouncementLogic.postAnouncement(anouncementForm, companyId);

	}

	@Override
	@RequestMapping(value = "/employee/anouncement/getAnouncement.html", method = RequestMethod.POST)
	public @ResponseBody
	String getAnouncement(HttpServletRequest request,
			HttpServletResponse response, Locale locale) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		List<AnouncementForm> anouncementFormList = anouncementLogic
				.getAnouncement(companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(anouncementFormList,
				jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/admin/anouncement/getAnouncementForEdit.html", method = RequestMethod.POST)
	public @ResponseBody
	String getAnouncementForEdit(
			@RequestParam(value = "announcementId", required = true) Long announcementId,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) {
		
		/*ID DECRYPT*/
		announcementId = FormatPreserveCryptoUtil.decrypt(announcementId);
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		AnouncementForm anouncementForm = anouncementLogic
				.getAnouncementForEdit(companyId, announcementId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(anouncementForm,
				jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/admin/anouncement/updateAnouncement.html", method = RequestMethod.POST)
	public @ResponseBody
	void updateAnouncement(
			@ModelAttribute("anouncementForm") AnouncementForm anouncementForm,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) {
		
		/*ID DECRYPT*/
		anouncementForm.setAnnouncementId(FormatPreserveCryptoUtil.decrypt(anouncementForm.getAnnouncementId()));
		
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		anouncementLogic.updateAnouncement(anouncementForm, companyId);

	}

	@Override
	@RequestMapping(value = "/admin/anouncement/deleteAnouncement.html", method = RequestMethod.POST)
	public @ResponseBody
	void deleteAnouncement(
			@RequestParam(value = "announcementId", required = true) Long announcementId,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) {
		
		/*ID DECRYPT*/
		announcementId = FormatPreserveCryptoUtil.decrypt(announcementId);
		
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		anouncementLogic.deleteAnouncement(announcementId, companyId);

	}

}
