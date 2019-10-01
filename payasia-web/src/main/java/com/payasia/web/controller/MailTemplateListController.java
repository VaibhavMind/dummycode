/**
 * @author abhisheksachdeva
 *
 */
package com.payasia.web.controller;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

import com.payasia.common.form.MailTemplateListForm;

/**
 * The Interface MailTemplateListController.
 */
public interface MailTemplateListController {

	/**
	 * Adds the mail template.
	 * 
	 * @param mailTemplateListForm
	 *            the mail template list form
	 * @param result
	 *            the result
	 * @param model
	 *            the model
	 * @param request
	 *            the request
	 * @return the model and view
	 */
	ModelAndView addMailTemplate(MailTemplateListForm mailTemplateListForm,
			BindingResult result, ModelMap model, HttpServletRequest request);

	/**
	 * Update mail template.
	 * 
	 * @param mailTemplateListForm
	 *            the mail template list form
	 * @param result
	 *            the result
	 * @param model
	 *            the model
	 * @param request
	 *            the request
	 */
	void updateMailTemplate(MailTemplateListForm mailTemplateListForm,
			BindingResult result, ModelMap model, HttpServletRequest request);

	/**
	 * Gets the mail template data.
	 * 
	 * @param templateId
	 *            the template id
	 * @param model
	 *            the model
	 * @return the mail template data
	 */
	String getMailTemplateData(long templateId, ModelMap model);

	/**
	 * Delete mail template.
	 * 
	 * @param templateId
	 *            the template id
	 */
	void deleteMailTemplate(long templateId);

	/**
	 * View attachment.
	 * 
	 * @param attachmentId
	 *            the attachment id
	 * @param response
	 *            the response
	 */
	void viewAttachment(long attachmentId, HttpServletResponse response);

	/**
	 * Delete attachment.
	 * 
	 * @param attachmentId
	 *            the attachment id
	 */
	void deleteAttachment(long attachmentId);

	/**
	 * get Mail Template Name.
	 * 
	 * @param String
	 *            the templateName
	 * @param HttpServletRequest
	 *            the request
	 * @param HttpServletResponse
	 *            the response
	 * @return the mail template Name
	 */
	String getMailTemplateName(String templateName, HttpServletRequest request,
			HttpServletResponse response);

	/**
	 * check MailTemplate Name Is duplicate or not.
	 * 
	 * @param Long
	 *            the templateId
	 * @param String
	 *            the templateName
	 * @param HttpServletRequest
	 *            the request
	 * @param HttpServletResponse
	 *            the response
	 * @return the mail template Name
	 */
	String checkMailTemplateName(Long templateId, String templateName,
			HttpServletRequest request, HttpServletResponse response);

	String viewMailTemplateList(int page, int rows, String columnName,
			String sortingType, String subCategoryId, String categoryId,
			HttpServletRequest request, HttpServletResponse response);

	/**
	 * get SubCategory List.
	 * 
	 * @param Long
	 *            the categoryId
	 */
	String getSubCategoryList(Long categoryId, Locale locale);

}
