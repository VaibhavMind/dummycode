/**
 * @author abhisheksachdeva
 *
 */
package com.payasia.logic.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.EmailAttachmentDTO;
import com.payasia.common.dto.EmailTemplateCategoryDTO;
import com.payasia.common.dto.EmailTemplateSubCategoryDTO;
import com.payasia.common.form.MailTemplateListForm;
import com.payasia.common.form.MailTemplateListResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.EmailTemplateAttachmentDAO;
import com.payasia.dao.EmailTemplateCategoryMasterDAO;
import com.payasia.dao.EmailTemplateDAO;
import com.payasia.dao.EmailTemplateSubCategoryMasterDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.EmailTemplate;
import com.payasia.dao.bean.EmailTemplateAttachment;
import com.payasia.dao.bean.EmailTemplateCategoryMaster;
import com.payasia.dao.bean.EmailTemplateSubCategoryMaster;
import com.payasia.logic.MailTemplateListLogic;

/**
 * The Class MailTemplateListLogicImpl.
 */
@Component
public class MailTemplateListLogicImpl implements MailTemplateListLogic {

	/** The Constant logger. */
	private static final Logger LOGGER = Logger
			.getLogger(MailTemplateListLogicImpl.class);

	/** The email template category master dao. */
	@Resource
	EmailTemplateCategoryMasterDAO emailTemplateCategoryMasterDAO;

	/** The email template sub category master dao. */
	@Resource
	EmailTemplateSubCategoryMasterDAO emailTemplateSubCategoryMasterDAO;

	/** The email template dao. */
	@Resource
	EmailTemplateDAO emailTemplateDAO;

	/** The company dao. */
	@Resource
	CompanyDAO companyDAO;

	/** The email template attachment dao. */
	@Resource
	EmailTemplateAttachmentDAO emailTemplateAttachmentDAO;
	@Resource
	MessageSource messageSource;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mind.payasia.common.logic.MailTemplateListLogic#getMailTemplateList()
	 */
	@Override
	public MailTemplateListResponse getMailTemplateList(Long companyId,
			String categoryId, String subCategoryId, PageRequest pageDTO,
			SortCondition sortDTO) {
		List<EmailTemplate> emailTemplateList = null;
		long categoryID = 0;
		long subCategoryID = 0;
		if (StringUtils.isNotBlank(subCategoryId)) {
			subCategoryID = Long.parseLong(subCategoryId);
		}
		if (StringUtils.isNotBlank(categoryId)) {
			categoryID = Long.parseLong(categoryId);
		}

		emailTemplateList = emailTemplateDAO.findByConditionCategory(
				subCategoryID, categoryID, companyId, pageDTO, sortDTO);

		List<MailTemplateListForm> mailTemplateList = new ArrayList<MailTemplateListForm>();

		for (EmailTemplate emailTemplate : emailTemplateList) {
			MailTemplateListForm mailTemplateListForm = new MailTemplateListForm();
			mailTemplateListForm.setCategoryId(emailTemplate
					.getEmailTemplateSubCategoryMaster()
					.getEmailTemplateSubCategoryId());
			mailTemplateListForm.setCategory(emailTemplate
					.getEmailTemplateSubCategoryMaster().getSubCategoryName());
			mailTemplateListForm.setEmailBody(emailTemplate.getBody());
			mailTemplateListForm.setName(emailTemplate.getName());
			mailTemplateListForm.setSubject(emailTemplate.getSubject());
			/*ID ENCRYPT*/
			mailTemplateListForm.setTemplateId(FormatPreserveCryptoUtil.encrypt(emailTemplate
					.getEmailTemplateId()));
			mailTemplateList.add(mailTemplateListForm);
		}
		MailTemplateListResponse response = new MailTemplateListResponse();
		int recordSize = 0;

		recordSize = emailTemplateDAO.getCountForEmailTemplate(subCategoryID,
				categoryID, companyId);

		if (pageDTO != null) {

			int pageSize = pageDTO.getPageSize();
			int totalPages = recordSize / pageSize;

			if (recordSize % pageSize != 0) {
				totalPages = totalPages + 1;
			}
			if (recordSize == 0) {
				pageDTO.setPageNumber(0);
			}

			response.setPage(pageDTO.getPageNumber());
			response.setTotal(totalPages);

			response.setRecords(recordSize);
		}
		response.setRows(mailTemplateList);
		return response;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mind.payasia.common.logic.MailTemplateListLogic#addMailTemplate(com
	 * .mind.payasia.common.form.MailTemplateListForm)
	 */
	@Override
	public String addMailTemplate(MailTemplateListForm mailTemplateListForm,
			Long companyId) {
		boolean status = true;
		EmailTemplate emailTemplate = new EmailTemplate();

		Company company = companyDAO.findById(companyId);

		EmailTemplateSubCategoryMaster subcategoryMaster = emailTemplateSubCategoryMasterDAO
				.findbyId(mailTemplateListForm.getSubCategoryId());

		emailTemplate.setBody(mailTemplateListForm.getEmailBody());
		emailTemplate.setCompany(company);
		emailTemplate.setEmailTemplateSubCategoryMaster(subcategoryMaster);
		emailTemplate.setName(mailTemplateListForm.getName());
		emailTemplate.setSubject(mailTemplateListForm.getSubject());

		List<EmailTemplate> emailTemplateVOList = emailTemplateDAO
				.findAll(companyId);
		if (emailTemplateVOList != null) {
			for (EmailTemplate emailTemplateVO : emailTemplateVOList) {
				if (mailTemplateListForm.getName().toUpperCase()
						.equals(emailTemplateVO.getName().toUpperCase())) {
					status = false;
				}
			}
		}
		if (status) {
			EmailTemplate template = emailTemplateDAO.save(emailTemplate);

			if (mailTemplateListForm.getAttachmentList() != null) {
				for (EmailAttachmentDTO emailAttachmentDTO : mailTemplateListForm
						.getAttachmentList()) {
					if (emailAttachmentDTO.getAttachment().getSize() > 0) {
						EmailTemplateAttachment attachment = new EmailTemplateAttachment();
						byte[] fileBytes = emailAttachmentDTO.getAttachment()
								.getBytes();
						attachment.setAttachment(fileBytes);
						attachment.setFileName(emailAttachmentDTO
								.getAttachment().getOriginalFilename());
						attachment.setEmailTemplate(template);
						emailTemplateAttachmentDAO.save(attachment);
					}
				}
			}

			return "new.email.template.saved.successfully";
		} else {
			return "duplicate.email.template.name.please.add.another.email.template.name";
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mind.payasia.common.logic.MailTemplateListLogic#deleteMailTemplate
	 * (java.lang.String)
	 */
	@Override
	public void deleteMailTemplate(long templateId,Long companyId) {
		EmailTemplate emailTemplate = emailTemplateDAO.findById(templateId,companyId);
		if(emailTemplate!=null){
	      emailTemplateDAO.delete(emailTemplate);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.MailTemplateListLogic#updateMailTemplate(com.payasia
	 * .common.form.MailTemplateListForm, java.lang.Long)
	 */
	@Override
	public void updateMailTemplate(MailTemplateListForm mailTemplateListForm,
			Long companyId) {

		EmailTemplate emailTemplate = new EmailTemplate();

		Company company = companyDAO.findById(companyId);

		EmailTemplateSubCategoryMaster subcategoryMaster = emailTemplateSubCategoryMasterDAO
				.findbyId(mailTemplateListForm.getSubCategoryId());

		emailTemplate.setBody(mailTemplateListForm.getEmailBody());
		emailTemplate.setCompany(company);
		emailTemplate.setEmailTemplateSubCategoryMaster(subcategoryMaster);
		emailTemplate.setName(mailTemplateListForm.getName());
		emailTemplate.setSubject(mailTemplateListForm.getSubject());
		emailTemplate.setEmailTemplateId(mailTemplateListForm.getTemplateId());

		emailTemplateDAO.update(emailTemplate);

		if (mailTemplateListForm.getAttachmentList() != null) {
			for (EmailAttachmentDTO emailAttachmentDTO : mailTemplateListForm
					.getAttachmentList()) {

				EmailTemplateAttachment attachment = new EmailTemplateAttachment();
				if (emailAttachmentDTO.getEmailAttachmentId() > 0) {
					EmailTemplateAttachment oldAttachment = emailTemplateAttachmentDAO
							.findById(emailAttachmentDTO.getEmailAttachmentId());
					byte[] fileBytes = oldAttachment.getAttachment();
					attachment.setAttachment(fileBytes);
					attachment.setEmailAttachmentId(oldAttachment
							.getEmailAttachmentId());
					attachment.setEmailTemplate(emailTemplate);
					attachment.setFileName(oldAttachment.getFileName());
					emailTemplateAttachmentDAO.update(attachment);

				} else {
					if (emailAttachmentDTO.getAttachment() != null) {
							if (emailAttachmentDTO.getAttachment().getSize() > 0) {
							byte[] fileBytes = emailAttachmentDTO
									.getAttachment().getBytes();
							attachment.setAttachment(fileBytes);
							attachment.setFileName(emailAttachmentDTO
									.getAttachment().getOriginalFilename());
							attachment.setEmailTemplate(emailTemplate);
							emailTemplateAttachmentDAO.save(attachment);
						}
					}
				}
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.MailTemplateListLogic#getMailTemplateData(long)
	 */
	@Override
	public MailTemplateListForm getMailTemplateData(long templateId,Long companyId) {

		EmailTemplate emailTemplate = emailTemplateDAO.findById(templateId,companyId);

		MailTemplateListForm mailTemplateListForm = new MailTemplateListForm();
		mailTemplateListForm.setCategoryId(emailTemplate
				.getEmailTemplateSubCategoryMaster()
				.getEmailTemplateCategoryMaster().getEmailTemplateCategoryId());
		mailTemplateListForm.setCategory(emailTemplate
				.getEmailTemplateSubCategoryMaster()
				.getEmailTemplateCategoryMaster().getCategoryName());
		mailTemplateListForm.setSubCategoryId(emailTemplate
				.getEmailTemplateSubCategoryMaster()
				.getEmailTemplateSubCategoryId());
		mailTemplateListForm.setSubCategoryName(emailTemplate
				.getEmailTemplateSubCategoryMaster().getSubCategoryName());
		mailTemplateListForm.setEmailBody(emailTemplate.getBody());
		mailTemplateListForm.setName(emailTemplate.getName());
		mailTemplateListForm.setSubject(emailTemplate.getSubject());
		mailTemplateListForm.setTemplateId(emailTemplate.getEmailTemplateId());

		List<EmailAttachmentDTO> attachmentList = new ArrayList<EmailAttachmentDTO>();

		for (EmailTemplateAttachment attachment : emailTemplate
				.getEmailTemplateAttachments()) {
			EmailAttachmentDTO attachmentDTO = new EmailAttachmentDTO();
			attachmentDTO.setEmailAttachmentId(attachment
					.getEmailAttachmentId());
			attachmentDTO.setAttachmentName(attachment.getFileName());
			attachmentList.add(attachmentDTO);

		}
		mailTemplateListForm.setAttachmentList(attachmentList);
		return mailTemplateListForm;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.MailTemplateListLogic#getCategoryList()
	 */
	@Override
	public List<EmailTemplateCategoryDTO> getCategoryList() {
		Locale locale = UserContext.getLocale();
		List<EmailTemplateCategoryMaster> categoryList = emailTemplateCategoryMasterDAO
				.findAll();

		List<EmailTemplateCategoryDTO> emailTemplateCategoryList = new ArrayList<EmailTemplateCategoryDTO>();

		for (EmailTemplateCategoryMaster categoryMaster : categoryList) {
			EmailTemplateCategoryDTO emailTemplateCategoryDTO = new EmailTemplateCategoryDTO();
			emailTemplateCategoryDTO.setCategoryDesc(categoryMaster
					.getCategoryDesc());
			if (StringUtils.isNotBlank(categoryMaster.getLabelKey())) {
				String labelMsg = messageSource.getMessage(
						categoryMaster.getLabelKey(), new Object[] {}, locale);
				emailTemplateCategoryDTO.setCategoryName(labelMsg);
			} else {
				emailTemplateCategoryDTO.setCategoryName(categoryMaster
						.getCategoryName());
			}

			emailTemplateCategoryDTO.setEmailTemplateCategoryId(categoryMaster
					.getEmailTemplateCategoryId());
			emailTemplateCategoryList.add(emailTemplateCategoryDTO);
		}

		return emailTemplateCategoryList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.MailTemplateListLogic#getSubCategoryList(java.lang.
	 * Long)
	 */
	@Override
	public List<EmailTemplateSubCategoryDTO> getSubCategoryList(
			Long emailTemplateCategoryId) {
		Locale locale = UserContext.getLocale();
		List<EmailTemplateSubCategoryMaster> categoryList = emailTemplateSubCategoryMasterDAO
				.getSubCategoryList(emailTemplateCategoryId);

		List<EmailTemplateSubCategoryDTO> emailTemplateSubCategoryList = new ArrayList<EmailTemplateSubCategoryDTO>();

		for (EmailTemplateSubCategoryMaster categoryMaster : categoryList) {
			EmailTemplateSubCategoryDTO emailTemplateSubCategoryDTO = new EmailTemplateSubCategoryDTO();
			emailTemplateSubCategoryDTO.setSubCategoryDesc(categoryMaster
					.getSubCategoryDesc());
			String labelMsg;
			if (StringUtils.isNotBlank(categoryMaster.getLabelKey())) {
				labelMsg = messageSource.getMessage(
						categoryMaster.getLabelKey(), new Object[] {}, locale);
			} else {
				labelMsg = categoryMaster.getSubCategoryName();
			}

			try {
				emailTemplateSubCategoryDTO.setSubCategoryName(URLEncoder
						.encode(labelMsg, "UTF-8"));
			} catch (UnsupportedEncodingException ex) {
				LOGGER.error(ex.getMessage(), ex);
			}
			emailTemplateSubCategoryDTO
					.setEmailTemplateSubCategoryId(categoryMaster
							.getEmailTemplateSubCategoryId());
			emailTemplateSubCategoryList.add(emailTemplateSubCategoryDTO);
		}

		return emailTemplateSubCategoryList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.MailTemplateListLogic#viewAttachment(long)
	 */
	@Override
	public EmailAttachmentDTO viewAttachment(long attachmentId,Long companyId) {
		EmailAttachmentDTO attachmentDTO = new EmailAttachmentDTO();
		EmailTemplateAttachment attachment = emailTemplateAttachmentDAO.findById(attachmentId,companyId);
		if(attachment!=null){
	      attachmentDTO.setAttachmentBytes(attachment.getAttachment());
		  attachmentDTO.setAttachmentName(attachment.getFileName());
		}
		return attachmentDTO;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.MailTemplateListLogic#deleteAttachment(long)
	 */
	@Override
	public void deleteAttachment(long attachmentId,Long companyId) {
		EmailTemplateAttachment attachment = emailTemplateAttachmentDAO.findById(attachmentId,companyId);
		if(attachment!=null){
		  emailTemplateAttachmentDAO.delete(attachment);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.MailTemplateListLogic#getMailTemplateName(java.lang
	 * .Long, java.lang.String)
	 */
	@Override
	public String getMailTemplateName(Long companyId, String templateName) {
		EmailTemplate emailTemplate = emailTemplateDAO
				.findBytemplateNameAndCompany(templateName, companyId);
		if (emailTemplate == null) {
			return PayAsiaConstants.TRUE;
		} else {
			return PayAsiaConstants.FALSE;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.MailTemplateListLogic#checkMailTemplateName(java.lang
	 * .Long, java.lang.String, java.lang.Long)
	 */
	@Override
	public String checkMailTemplateName(Long templateId, String templateName,
			Long companyId) {
		EmailTemplate emailTemplate = emailTemplateDAO
				.findBytemplateNameAndCompany(templateId, templateName,
						companyId);
		if (emailTemplate == null) {
			return PayAsiaConstants.TRUE;
		} else {
			return PayAsiaConstants.FALSE;
		}
	}
}
