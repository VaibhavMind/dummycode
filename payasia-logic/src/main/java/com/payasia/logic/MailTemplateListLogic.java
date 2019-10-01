package com.payasia.logic;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.payasia.common.dto.EmailAttachmentDTO;
import com.payasia.common.dto.EmailTemplateCategoryDTO;
import com.payasia.common.dto.EmailTemplateSubCategoryDTO;
import com.payasia.common.form.MailTemplateListForm;
import com.payasia.common.form.MailTemplateListResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;

/**
 * The Interface MailTemplateListLogic.
 */
/**
 * @author vivekjain
 * 
 */
@Transactional
public interface MailTemplateListLogic {

	/**
	 * Gets the mail template list.
	 * 
	 * @param subCategoryId2
	 * 
	 * @return the mail template list
	 */
	MailTemplateListResponse getMailTemplateList(Long companyId,
			String categoryId, String subCategoryId, PageRequest pageDTO,
			SortCondition sortDTO);

	/**
	 * Adds the mail template.
	 * 
	 * @param mailTemplateListForm
	 *            the mail template list form
	 * @return
	 */
	String addMailTemplate(MailTemplateListForm mailTemplateListForm,
			Long companyId);

	/**
	 * Update mail template.
	 * 
	 * @param mailTemplateListForm
	 *            the mail template list form
	 */
	void updateMailTemplate(MailTemplateListForm mailTemplateListForm,
			Long companyId);

	/**
	 * Delete mail template.
	 * 
	 * @param templateId
	 *            the template id
	 */
	void deleteMailTemplate(long templateId,Long companyId);

	

	/**
	 * Gets the category list.
	 * 
	 * @return the category list
	 */
	List<EmailTemplateCategoryDTO> getCategoryList();

	/**
	 * View attachment.
	 * 
	 * @param attachmentId
	 *            the attachment id
	 * @return
	 */
	EmailAttachmentDTO viewAttachment(long attachmentId,Long companyId);

	/**
	 * Delete attachment.
	 * 
	 * @param attachmentId
	 *            the attachment id
	 */
	void deleteAttachment(long attachmentId,Long companyId);

	/**
	 * get SubCategory List.
	 * 
	 * @param Long
	 *            the emailTemplateCategoryId
	 */
	List<EmailTemplateSubCategoryDTO> getSubCategoryList(
			Long emailTemplateCategoryId);

	/**
	 * get Mail Template Name.
	 * 
	 * @param Long
	 *            the companyId
	 * @param String
	 *            the templateName
	 * @return the mail template Name
	 */
	String getMailTemplateName(Long companyId, String templateName);

	/**
	 * check MailTemplate Name Is duplicate or not.
	 * 
	 * @param Long
	 *            the templateId
	 * @param String
	 *            the templateName
	 * @param Long
	 *            the companyId
	 * @return the mail template Name
	 */
	String checkMailTemplateName(Long templateId, String templateName,
			Long companyId);
	
	/**
	 * Gets the mail template data.
	 * 
	 * @param templateId
	 *            the template id
	 * @return the mail template data
	 */
	
	MailTemplateListForm getMailTemplateData(long templateId,Long companyId);
	
	

}
