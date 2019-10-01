package com.payasia.dao.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.NullAwareBeanUtilsBean;
import com.payasia.common.util.SortConstants;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.EmailTemplateDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.EmailTemplate;
import com.payasia.dao.bean.EmailTemplateCategoryMaster;
import com.payasia.dao.bean.EmailTemplateCategoryMaster_;
import com.payasia.dao.bean.EmailTemplateSubCategoryMaster;
import com.payasia.dao.bean.EmailTemplateSubCategoryMaster_;
import com.payasia.dao.bean.EmailTemplate_;

/**
 * The Class EmailTemplateDAOImpl.
 */
/**
 * @author vivekjain
 * 
 */
@Repository
public class EmailTemplateDAOImpl extends BaseDAO implements EmailTemplateDAO {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.EmailTemplateDAO#save(com.payasia.dao.bean.EmailTemplate)
	 */
	@Override
	public EmailTemplate save(EmailTemplate emailTemplate) {
		EmailTemplate persistObj = emailTemplate;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (EmailTemplate) getBaseEntity();
			beanUtil.copyProperties(persistObj, emailTemplate);
		} catch (IllegalAccessException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		} catch (InvocationTargetException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		}
		this.entityManagerFactory.persist(persistObj);

		return persistObj;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.BaseDAO#getBaseEntity()
	 */
	@Override
	protected Object getBaseEntity() {
		EmailTemplate emailTemplate = new EmailTemplate();
		return emailTemplate;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.EmailTemplateDAO#findById(long)
	 */
	@Override
	public EmailTemplate findById(long templateId) {
		EmailTemplate emailTemplate = super.findById(EmailTemplate.class,
				templateId);
		return emailTemplate;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.EmailTemplateDAO#findByConditionCategory(long, long,
	 * com.payasia.common.form.PageRequest,
	 * com.payasia.common.form.SortCondition)
	 */
	@Override
	public List<EmailTemplate> findByConditionCategory(long subCategoryId,
			long categoryId, long companyId, PageRequest pageDTO,
			SortCondition sortDTO) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmailTemplate> criteriaQuery = cb
				.createQuery(EmailTemplate.class);
		Root<EmailTemplate> templateRoot = criteriaQuery
				.from(EmailTemplate.class);
		criteriaQuery.select(templateRoot);

		Join<EmailTemplate, Company> templateCompanyRootJoin = templateRoot
				.join(EmailTemplate_.company);
		Join<EmailTemplate, EmailTemplateSubCategoryMaster> templateSubCategoryJoin = templateRoot
				.join(EmailTemplate_.emailTemplateSubCategoryMaster);

		Predicate restriction = cb.conjunction();

		Path<Long> companyID = templateCompanyRootJoin.get(Company_.companyId);
		restriction = cb.and(restriction, cb.equal(companyID, companyId));

		if (categoryId != 0) {
			Join<EmailTemplateSubCategoryMaster, EmailTemplateCategoryMaster> templateCategoryJoin = templateSubCategoryJoin
					.join(EmailTemplateSubCategoryMaster_.emailTemplateCategoryMaster);
			Path<Long> categoryID = templateCategoryJoin
					.get(EmailTemplateCategoryMaster_.emailTemplateCategoryId);
			restriction = cb.and(restriction, cb.equal(categoryID, categoryId));
		}
		if (subCategoryId != 0) {
			Path<Long> subCategoryID = templateSubCategoryJoin
					.get(EmailTemplateSubCategoryMaster_.emailTemplateSubCategoryId);
			restriction = cb.and(restriction,
					cb.equal(subCategoryID, subCategoryId));
		}

		criteriaQuery.where(restriction);
		if (sortDTO != null) {

			Path<String> sortPath = getSortPathForEmailTemplate(sortDTO,
					templateRoot, templateCompanyRootJoin,
					templateSubCategoryJoin);
			if (sortPath != null) {
				if (SortConstants.DB_ORDER_BY_ASC.equalsIgnoreCase(sortDTO
						.getOrderType())) {
					criteriaQuery.orderBy(cb.asc(sortPath));
				}
				if (SortConstants.DB_ORDER_BY_DESC.equalsIgnoreCase(sortDTO
						.getOrderType())) {
					criteriaQuery.orderBy(cb.desc(sortPath));
				}
			}

		}

		TypedQuery<EmailTemplate> emailTemplateQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			emailTemplateQuery.setFirstResult(getStartPosition(pageDTO));
			emailTemplateQuery.setMaxResults(pageDTO.getPageSize());
		}
		List<EmailTemplate> emailTemplates = emailTemplateQuery.getResultList();

		return emailTemplates;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.EmailTemplateDAO#findByConditionCompany(long,
	 * com.payasia.common.form.PageRequest,
	 * com.payasia.common.form.SortCondition)
	 */
	@Override
	public List<EmailTemplate> findByConditionCompany(long companyId,
			PageRequest pageDTO, SortCondition sortDTO) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmailTemplate> criteriaQuery = cb
				.createQuery(EmailTemplate.class);
		Root<EmailTemplate> templateRoot = criteriaQuery
				.from(EmailTemplate.class);
		criteriaQuery.select(templateRoot);

		Join<EmailTemplate, Company> templateCompanyRootJoin = templateRoot
				.join(EmailTemplate_.company);
		Join<EmailTemplate, EmailTemplateSubCategoryMaster> templateCategoryJoin = templateRoot
				.join(EmailTemplate_.emailTemplateSubCategoryMaster);

		Predicate restriction = cb.conjunction();

		Path<Long> companyID = templateCompanyRootJoin.get(Company_.companyId);
		restriction = cb.and(restriction, cb.equal(companyID, companyId));

		criteriaQuery.where(restriction);
		if (sortDTO != null) {

			Path<String> sortPath = getSortPathForEmailTemplate(sortDTO,
					templateRoot, templateCompanyRootJoin, templateCategoryJoin);
			if (sortPath != null) {
				if (SortConstants.DB_ORDER_BY_ASC.equalsIgnoreCase(sortDTO
						.getOrderType())) {
					criteriaQuery.orderBy(cb.asc(sortPath));
				}
				if (SortConstants.DB_ORDER_BY_DESC.equalsIgnoreCase(sortDTO
						.getOrderType())) {
					criteriaQuery.orderBy(cb.desc(sortPath));
				}
			}

		}

		TypedQuery<EmailTemplate> emailTemplateQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			emailTemplateQuery.setFirstResult(getStartPosition(pageDTO));
			emailTemplateQuery.setMaxResults(pageDTO.getPageSize());
		}
		List<EmailTemplate> emailTemplates = emailTemplateQuery.getResultList();

		return emailTemplates;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.EmailTemplateDAO#getSortPathForEmailTemplate(com.payasia
	 * .common.form.SortCondition, javax.persistence.criteria.Root,
	 * javax.persistence.criteria.Join, javax.persistence.criteria.Join)
	 */
	@Override
	public Path<String> getSortPathForEmailTemplate(
			SortCondition sortDTO,
			Root<EmailTemplate> templateRoot,
			Join<EmailTemplate, Company> templateCompanyRootJoin,
			Join<EmailTemplate, EmailTemplateSubCategoryMaster> templateCategoryJoin) {

		List<String> emailTemplateIsColList = new ArrayList<String>();
		emailTemplateIsColList.add(SortConstants.EMAIL_TEMPLATE_NAME);
		emailTemplateIsColList.add(SortConstants.EMAIL_TEMPLATE_SUBJECT);
		emailTemplateIsColList.add(SortConstants.EMAIL_TEMPLATE_CATEGORY);

		Path<String> sortPath = null;

		if (emailTemplateIsColList.contains(sortDTO.getColumnName())) {
			sortPath = templateRoot.get(colMap.get(EmailTemplate.class
					+ sortDTO.getColumnName()));
		}
		return sortPath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.EmailTemplateDAO#delete(com.payasia.dao.bean.EmailTemplate
	 * )
	 */
	@Override
	public void delete(EmailTemplate emailTemplate) {

		super.delete(emailTemplate);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.EmailTemplateDAO#update(com.payasia.dao.bean.EmailTemplate
	 * )
	 */
	@Override
	public void update(EmailTemplate emailTemplate) {
		super.update(emailTemplate);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.EmailTemplateDAO#getCountForEmailTemplate(long,
	 * long)
	 */
	@Override
	public int getCountForEmailTemplate(long subCategoryID, long categoryId,
			long companyId) {
		return findByConditionCategory(subCategoryID, categoryId, companyId,
				null, null).size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.EmailTemplateDAO#getCountForEmailTemplateByCompany(long)
	 */
	@Override
	public int getCountForEmailTemplateByCompany(long companyId) {
		return findByConditionCompany(companyId, null, null).size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.EmailTemplateDAO#findByConditionSubCategoryAndCompId(
	 * java.lang.String, long, long)
	 */
	@Override
	public EmailTemplate findByConditionSubCategoryAndCompId(
			String templateName, long subCategoryId, long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmailTemplate> criteriaQuery = cb
				.createQuery(EmailTemplate.class);
		Root<EmailTemplate> templateRoot = criteriaQuery
				.from(EmailTemplate.class);
		criteriaQuery.select(templateRoot);

		Join<EmailTemplate, Company> templateCompanyRootJoin = templateRoot
				.join(EmailTemplate_.company);
		Join<EmailTemplate, EmailTemplateSubCategoryMaster> templateCategoryJoin = templateRoot
				.join(EmailTemplate_.emailTemplateSubCategoryMaster);

		Path<Long> companyID = templateCompanyRootJoin.get(Company_.companyId);

		Path<Long> subCategoryID = templateCategoryJoin
				.get(EmailTemplateSubCategoryMaster_.emailTemplateSubCategoryId);

		Predicate restriction = cb.conjunction();

		if (StringUtils.isNotBlank(templateName)) {

			restriction = cb.and(restriction, cb.equal(
					cb.upper(templateRoot.get(EmailTemplate_.name)),
					templateName.toUpperCase()));
		}

		restriction = cb.and(restriction, cb.equal(companyID, companyId));

		restriction = cb.and(restriction,
				cb.equal(subCategoryID, subCategoryId));

		criteriaQuery.where(restriction);

		TypedQuery<EmailTemplate> emailTemplateQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<EmailTemplate> emailTemplateList = emailTemplateQuery
				.getResultList();
		if (emailTemplateList != null && !emailTemplateList.isEmpty()) {
			return emailTemplateList.get(0);
		}
		return null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.EmailTemplateDAO#findAll(long)
	 */
	@Override
	public List<EmailTemplate> findAll(long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmailTemplate> criteriaQuery = cb
				.createQuery(EmailTemplate.class);
		Root<EmailTemplate> templateRoot = criteriaQuery
				.from(EmailTemplate.class);
		criteriaQuery.select(templateRoot);

		Join<EmailTemplate, Company> templateCompanyRootJoin = templateRoot
				.join(EmailTemplate_.company);

		Path<Long> companyID = templateCompanyRootJoin.get(Company_.companyId);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(companyID, companyId));

		criteriaQuery.where(restriction);

		TypedQuery<EmailTemplate> emailTemplateQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<EmailTemplate> emailTemplatesList = emailTemplateQuery
				.getResultList();
		return emailTemplatesList;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.EmailTemplateDAO#findBytemplateNameAndCompany(java.lang
	 * .String, long)
	 */
	@Override
	public EmailTemplate findBytemplateNameAndCompany(String templateName,
			long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmailTemplate> criteriaQuery = cb
				.createQuery(EmailTemplate.class);
		Root<EmailTemplate> mailTemplateRoot = criteriaQuery
				.from(EmailTemplate.class);

		criteriaQuery.select(mailTemplateRoot);

		Join<EmailTemplate, Company> mailTemplateRootJoin = mailTemplateRoot
				.join(EmailTemplate_.company);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(
				mailTemplateRootJoin.get(Company_.companyId), companyId));

		restriction = cb.and(restriction, cb.equal(
				cb.upper(mailTemplateRoot.get(EmailTemplate_.name)),
				templateName.toUpperCase()));

		criteriaQuery.where(restriction);

		TypedQuery<EmailTemplate> mailTemplateTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<EmailTemplate> emailTemplateList = mailTemplateTypedQuery
				.getResultList();
		if (emailTemplateList != null && !emailTemplateList.isEmpty()) {
			return emailTemplateList.get(0);
		}
		return null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.EmailTemplateDAO#findBytemplateNameAndCompany(java.lang
	 * .Long, java.lang.String, long)
	 */
	@Override
	public EmailTemplate findBytemplateNameAndCompany(Long mailTemplateId,
			String templateName, long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmailTemplate> criteriaQuery = cb
				.createQuery(EmailTemplate.class);
		Root<EmailTemplate> mailTemplateRoot = criteriaQuery
				.from(EmailTemplate.class);

		criteriaQuery.select(mailTemplateRoot);

		Join<EmailTemplate, Company> mailTemplateRootJoin = mailTemplateRoot
				.join(EmailTemplate_.company);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(
				mailTemplateRootJoin.get(Company_.companyId), companyId));

		if (mailTemplateId != null) {
			restriction = cb.and(restriction, cb.notEqual(
					mailTemplateRoot.get(EmailTemplate_.emailTemplateId),
					mailTemplateId));
		}

		restriction = cb.and(restriction, cb.equal(
				cb.upper(mailTemplateRoot.get(EmailTemplate_.name)),
				templateName.toUpperCase()));

		criteriaQuery.where(restriction);

		TypedQuery<EmailTemplate> mailTemplateTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<EmailTemplate> emailTemplateList = mailTemplateTypedQuery
				.getResultList();
		if (emailTemplateList != null && !emailTemplateList.isEmpty()) {
			return emailTemplateList.get(0);
		}
		return null;

	}

	@Override
	public List<EmailTemplate> findByConditionCompanyAndSubCategoryName(
			Long companyId, String leaveEventReminderSubCategoryName) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmailTemplate> criteriaQuery = cb
				.createQuery(EmailTemplate.class);
		Root<EmailTemplate> templateRoot = criteriaQuery
				.from(EmailTemplate.class);
		criteriaQuery.select(templateRoot);

		Join<EmailTemplate, Company> templateCompanyRootJoin = templateRoot
				.join(EmailTemplate_.company);
		Join<EmailTemplate, EmailTemplateSubCategoryMaster> templateCategoryJoin = templateRoot
				.join(EmailTemplate_.emailTemplateSubCategoryMaster);

		Predicate restriction = cb.conjunction();

		Path<Long> companyID = templateCompanyRootJoin.get(Company_.companyId);
		restriction = cb.and(restriction, cb.equal(companyID, companyId));

		restriction = cb.and(restriction, cb.equal(templateCategoryJoin
				.get(EmailTemplateSubCategoryMaster_.subCategoryName),
				leaveEventReminderSubCategoryName));
		criteriaQuery.where(restriction);

		TypedQuery<EmailTemplate> emailTemplateQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<EmailTemplate> emailTemplates = emailTemplateQuery.getResultList();

		return emailTemplates;
	}

	@Override
	public EmailTemplate findByConditionSubCategory(long subCategoryId,
			long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmailTemplate> criteriaQuery = cb
				.createQuery(EmailTemplate.class);
		Root<EmailTemplate> templateRoot = criteriaQuery
				.from(EmailTemplate.class);
		criteriaQuery.select(templateRoot);

		Join<EmailTemplate, Company> templateCompanyRootJoin = templateRoot
				.join(EmailTemplate_.company);
		Join<EmailTemplate, EmailTemplateSubCategoryMaster> templateCategoryJoin = templateRoot
				.join(EmailTemplate_.emailTemplateSubCategoryMaster);

		Path<Long> companyID = templateCompanyRootJoin.get(Company_.companyId);

		Path<Long> subCategoryID = templateCategoryJoin
				.get(EmailTemplateSubCategoryMaster_.emailTemplateSubCategoryId);

		Path<String> subCategoryName = templateCategoryJoin
				.get(EmailTemplateSubCategoryMaster_.subCategoryName);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(companyID, companyId));

		restriction = cb.and(restriction,
				cb.equal(subCategoryID, subCategoryId));

		criteriaQuery.where(restriction);

		TypedQuery<EmailTemplate> emailTemplateQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<EmailTemplate> emailTemplateList = emailTemplateQuery
				.getResultList();
		if (emailTemplateList != null && !emailTemplateList.isEmpty()) {
			return emailTemplateList.get(0);
		}
		return null;

	}

	@Override
	public EmailTemplate findById(long templateId, Long companyId) {
		
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmailTemplate> criteriaQuery = cb.createQuery(EmailTemplate.class);
		Root<EmailTemplate> templateRoot = criteriaQuery.from(EmailTemplate.class);
		criteriaQuery.select(templateRoot);

		Join<EmailTemplate, Company> templateCompanyRootJoin = templateRoot.join(EmailTemplate_.company);

		Path<Long> companyID = templateCompanyRootJoin.get(Company_.companyId);

		Path<Long> templateID = templateRoot.get(EmailTemplate_.emailTemplateId);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(companyID, companyId));

		restriction = cb.and(restriction, cb.equal(templateID, templateId));

		criteriaQuery.where(restriction);

		TypedQuery<EmailTemplate> emailTemplateQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<EmailTemplate> emailTemplateList = emailTemplateQuery.getResultList();
		if (emailTemplateList != null && !emailTemplateList.isEmpty()) {
			return emailTemplateList.get(0);
		}
		return null;
	}
}
