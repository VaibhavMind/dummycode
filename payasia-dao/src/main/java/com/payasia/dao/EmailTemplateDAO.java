package com.payasia.dao;

import java.util.List;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.EmailTemplate;
import com.payasia.dao.bean.EmailTemplateSubCategoryMaster;

/**
 * The Interface EmailTemplateDAO.
 */
/**
 * @author vivekjain
 * 
 */
public interface EmailTemplateDAO {

	/**
	 * purpose : Save EmailTemplate Object.
	 * 
	 * @param emailTemplate
	 *            the email template
	 * @return the email template
	 */
	EmailTemplate save(EmailTemplate emailTemplate);

	/**
	 * purpose : Find EmailTemplate Object by templateId.
	 * 
	 * @param templateId
	 *            the template id
	 * @return the email template
	 */
	EmailTemplate findById(long templateId);

	/**
	 * purpose : Gets the sort path for email template.
	 * 
	 * @param sortDTO
	 *            the sort dto
	 * @param templateRoot
	 *            the template root
	 * @param templateCompanyRootJoin
	 *            the template company root join
	 * @param templateCategoryJoin
	 *            the template category join
	 * @return the sort path for email template
	 */
	Path<String> getSortPathForEmailTemplate(
			SortCondition sortDTO,
			Root<EmailTemplate> templateRoot,
			Join<EmailTemplate, Company> templateCompanyRootJoin,
			Join<EmailTemplate, EmailTemplateSubCategoryMaster> templateCategoryJoin);

	/**
	 * purpose : Delete EmailTemplate Object.
	 * 
	 * @param emailTemplate
	 *            the email template
	 */
	void delete(EmailTemplate emailTemplate);

	/**
	 * purpose : Update EmailTemplate Object.
	 * 
	 * @param emailTemplate
	 *            the email template
	 */
	void update(EmailTemplate emailTemplate);

	/**
	 * purpose : Find EmailTemplate Object by condition subCategoryId and
	 * companyId.
	 * 
	 * @param templateName
	 *            the template name
	 * @param subCategoryId
	 *            the sub category id
	 * @param companyId
	 *            the company id
	 * @return the email template
	 */
	EmailTemplate findByConditionSubCategoryAndCompId(String templateName,
			long subCategoryId, long companyId);

	/**
	 * purpose : Find all EmailTemplate Objects List.
	 * 
	 * @param companyId
	 *            the company id
	 * @return the list
	 */
	List<EmailTemplate> findAll(long companyId);

	/**
	 * purpose : Find EmailTemplate Objects List by condition companyId.
	 * 
	 * @param companyId
	 *            the company id
	 * @param pageDTO
	 *            the page dto
	 * @param sortDTO
	 *            the sort dto
	 * @return the list
	 */
	List<EmailTemplate> findByConditionCompany(long companyId,
			PageRequest pageDTO, SortCondition sortDTO);

	/**
	 * purpose : Gets the count for email template by companyId.
	 * 
	 * @param companyId
	 *            the company id
	 * @return the count for email template by company
	 */
	int getCountForEmailTemplateByCompany(long companyId);

	/**
	 * purpose : Find EmailTemplate Object by template name and companyId.
	 * 
	 * @param templateName
	 *            the template name
	 * @param companyId
	 *            the company id
	 * @return the email template
	 */
	EmailTemplate findBytemplateNameAndCompany(String templateName,
			long companyId);

	/**
	 * purpose : Find EmailTemplate Object by template name, mailTemplateId and
	 * companyId.
	 * 
	 * @param mailTemplateId
	 *            the mail template id
	 * @param templateName
	 *            the template name
	 * @param companyId
	 *            the company id
	 * @return the email template
	 */
	EmailTemplate findBytemplateNameAndCompany(Long mailTemplateId,
			String templateName, long companyId);

	List<EmailTemplate> findByConditionCompanyAndSubCategoryName(
			Long companyId, String leaveEventReminderSubCategoryName);

	/**
	 * purpose : Find EmailTemplate Object by condition subCategoryId and
	 * companyId.
	 * 
	 * @param subCategoryId
	 *            the sub category id
	 * @param companyId
	 *            the company id
	 * @param pageDTO
	 *            the page dto
	 * @param sortDTO
	 *            the sort dto
	 * @return the EmailTemplate list
	 */
	List<EmailTemplate> findByConditionCategory(long subCategoryId,
			long categoryId, long companyId, PageRequest pageDTO,
			SortCondition sortDTO);

	int getCountForEmailTemplate(long subCategoryID, long categoryId,
			long companyId);

	EmailTemplate findByConditionSubCategory(long subCategoryId, long companyId);
	
	EmailTemplate findById(long templateId,Long companyId);

}
