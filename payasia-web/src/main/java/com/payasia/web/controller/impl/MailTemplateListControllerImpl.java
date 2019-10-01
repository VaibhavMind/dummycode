/**
 * @author abhisheksachdeva
 *
 */
package com.payasia.web.controller.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.EmailAttachmentDTO;
import com.payasia.common.dto.EmailTemplateSubCategoryDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.MailTemplateListForm;
import com.payasia.common.form.MailTemplateListResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.logic.MailTemplateListLogic;
import com.payasia.web.controller.MailTemplateListController;
import com.payasia.web.util.PayAsiaSessionAttributes;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * The Class MailTemplateListControllerImpl.
 */
/**
 * @author vivekjain
 * 
 */
@Controller
@RequestMapping(value = "/admin/mailTemplateList")
public class MailTemplateListControllerImpl implements
		MailTemplateListController {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger
			.getLogger(MailTemplateListControllerImpl.class);

	/** The mail template list logic. */
	@Resource
	MailTemplateListLogic mailTemplateListLogic;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mind.payasia.common.controller.MailTemplateListController#addMailTemplate
	 * (com.mind.payasia.common.form.MailTemplateListForm,
	 * org.springframework.validation.BindingResult,
	 * org.springframework.ui.ModelMap, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	@RequestMapping(value = "/addMailTemplate.html", method = RequestMethod.POST)
	public ModelAndView addMailTemplate(
			@ModelAttribute("mailTemplateListForm") MailTemplateListForm mailTemplateListForm,
			BindingResult result, ModelMap model, HttpServletRequest request) {
		
		boolean isValidName=isValidString(mailTemplateListForm.getName());
		boolean isValidSub=isValidString(mailTemplateListForm.getSubject());
	
		if(!isValidName || !isValidSub) {
            throw new PayAsiaSystemException("payasia.record.invalid.data");
		}
		
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		mailTemplateListLogic.addMailTemplate(mailTemplateListForm, companyId);
		return new ModelAndView("admin/mailTemplateList");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mind.payasia.common.controller.MailTemplateListController#
	 * deleteMailTemplate(java.lang.String)
	 */
	@Override
	@RequestMapping(value = "/deleteMailTemplate.html", method = RequestMethod.POST)
	public void deleteMailTemplate(
			@RequestParam(value = "tempId", required = true) long templateId) {
		/*
		 * ID DYCRYPT
		 * */
		templateId = FormatPreserveCryptoUtil.decrypt(templateId);
		
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		mailTemplateListLogic.deleteMailTemplate(templateId,companyId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mind.payasia.common.controller.MailTemplateListController#
	 * updateMailTemplate(com.mind.payasia.common.form.MailTemplateListForm,
	 * org.springframework.validation.BindingResult,
	 * org.springframework.ui.ModelMap, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	@RequestMapping(value = "/updateMailTemplate.html", method = RequestMethod.POST)
	public void updateMailTemplate(
			@ModelAttribute("mailTemplateListForm") MailTemplateListForm mailTemplateListForm,
			BindingResult result, ModelMap model, HttpServletRequest request) {
		
		boolean isValidName=isValidString(mailTemplateListForm.getName());
		boolean isValidSub=isValidString(mailTemplateListForm.getSubject());
	
		if(!isValidName || !isValidSub) {
            throw new PayAsiaSystemException("payasia.record.invalid.data");
		}
		
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		mailTemplateListLogic.updateMailTemplate(mailTemplateListForm,companyId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mind.payasia.common.controller.MailTemplateListController#
	 * getMailTemplateData(java.lang.String)
	 */
	@Override
	@RequestMapping(value = "/getMailTemplateData.html", method = RequestMethod.POST)
	@ResponseBody public String getMailTemplateData(long templateId,
			ModelMap model) {
		/*
		 * ID DYCRYPT
		 * */
		templateId = FormatPreserveCryptoUtil.decrypt(templateId);
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		MailTemplateListForm mailTemplateListForm = mailTemplateListLogic
				.getMailTemplateData(templateId,companyId);
		model.addAttribute("mailTemplateListForm", mailTemplateListForm);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(mailTemplateListForm,
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
	 * com.mind.payasia.common.controller.MailTemplateListController#accessControl
	 * ()
	 */
	@Override
	@RequestMapping(value = "/viewMailTemplateList.html", method = RequestMethod.POST)
	@ResponseBody public String viewMailTemplateList(
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "subCategoryId", required = true) String subCategoryId,
			@RequestParam(value = "categoryId", required = true) String categoryId,
			HttpServletRequest request, HttpServletResponse response) {

		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		LOGGER.info("viewMailTemplates Begin");
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		MailTemplateListResponse mailTemplateListResponse = mailTemplateListLogic
				.getMailTemplateList(companyId, categoryId, subCategoryId,
						pageDTO, sortDTO);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(mailTemplateListResponse,
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
	 * com.payasia.web.controller.MailTemplateListController#viewAttachment(
	 * long, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/viewAttachment.html", method = RequestMethod.POST)
	public @ResponseBody void viewAttachment(
			@RequestParam(value = "attachmentId", required = true) long attachmentId,
			HttpServletResponse response) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		ServletOutputStream outStream = null;
		try {
			EmailAttachmentDTO attachment = mailTemplateListLogic.viewAttachment(attachmentId,companyId);
			byte[] byteFile = attachment.getAttachmentBytes();
			response.reset();
			String mimeType = URLConnection.guessContentTypeFromName(attachment.getAttachmentName());
			response.setContentType("application/" + mimeType);
			response.setContentLength(byteFile.length);
			String filename = attachment.getAttachmentName();
			response.setHeader("Content-Disposition", "attachment;filename="+ filename);
			outStream = response.getOutputStream();
			outStream.write(byteFile);
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			try {
				if (outStream != null) {

					outStream.flush();
					outStream.close();
				}
			} catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.MailTemplateListController#deleteAttachment
	 * (long)
	 */
	@Override
	@RequestMapping(value = "/deleteAttachment.html", method = RequestMethod.POST)
	public void deleteAttachment(
			@RequestParam(value = "attachmentId", required = true) long attachmentId) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		mailTemplateListLogic.deleteAttachment(attachmentId,companyId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.MailTemplateListController#getSubCategoryList
	 * (java.lang.Long)
	 */
	@Override
	@RequestMapping(value = "/getSubCategoryList.html", method = RequestMethod.POST)
	@ResponseBody public String getSubCategoryList(
			@RequestParam(value = "categoryId", required = true) Long categoryId,
			Locale locale) {
		UserContext.setLocale(locale);
		List<EmailTemplateSubCategoryDTO> subCategoryList = mailTemplateListLogic
				.getSubCategoryList(categoryId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray
				.fromObject(subCategoryList, jsonConfig);

		return jsonObject.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.MailTemplateListController#getMailTemplateName
	 * (java.lang.String, javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/getMailTemplateName.html", method = RequestMethod.POST)
	@ResponseBody public String getMailTemplateName(
			@RequestParam(value = "templateName", required = true) String templateName,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		String templateStatus = mailTemplateListLogic.getMailTemplateName(
				companyId, templateName);
		return templateStatus;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.MailTemplateListController#checkMailTemplateName
	 * (java.lang.Long, java.lang.String, javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/checkMailTemplateName.html", method = RequestMethod.POST)
	@ResponseBody public String checkMailTemplateName(
			@RequestParam(value = "templateId", required = true) Long templateId,
			@RequestParam(value = "templateName", required = true) String templateName,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		/* ID DYCRYPT
		 * */
		templateId = FormatPreserveCryptoUtil.decrypt(templateId);
		String templateStatus = mailTemplateListLogic.checkMailTemplateName(
				templateId, templateName, companyId);
		return templateStatus;
	}
	
	
	private boolean isValidString(String str){
		
		if(str !=null && str.contains("<") ||  str.contains("%3C") || str.contains(">") || str.contains("%3E")){
		return  false;	
		}
		return true;
		
		
	}
}
