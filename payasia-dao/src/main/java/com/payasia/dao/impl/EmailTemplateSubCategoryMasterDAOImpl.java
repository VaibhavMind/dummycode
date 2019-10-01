package com.payasia.dao.impl;

import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.payasia.dao.BaseDAO;
import com.payasia.dao.EmailTemplateSubCategoryMasterDAO;
import com.payasia.dao.bean.EmailTemplateCategoryMaster;
import com.payasia.dao.bean.EmailTemplateCategoryMaster_;
import com.payasia.dao.bean.EmailTemplateSubCategoryMaster;
import com.payasia.dao.bean.EmailTemplateSubCategoryMaster_;

/**
 * The Class EmailTemplateSubCategoryMasterDAOImpl.
 */
/**
 * @author vivekjain
 * 
 */
@Repository
public class EmailTemplateSubCategoryMasterDAOImpl extends BaseDAO implements
		EmailTemplateSubCategoryMasterDAO {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.EmailTemplateSubCategoryMasterDAO#findAll()
	 */
	@Override
	public List<EmailTemplateSubCategoryMaster> findAll() {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmailTemplateSubCategoryMaster> criteriaQuery = cb
				.createQuery(EmailTemplateSubCategoryMaster.class);
		Root<EmailTemplateSubCategoryMaster> categoryRoot = criteriaQuery
				.from(EmailTemplateSubCategoryMaster.class);

		criteriaQuery.select(categoryRoot);
		criteriaQuery
				.orderBy(cb.asc(categoryRoot
						.get(EmailTemplateSubCategoryMaster_.emailTemplateSubCategoryId)));

		TypedQuery<EmailTemplateSubCategoryMaster> categoryQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<EmailTemplateSubCategoryMaster> categoryList = categoryQuery
				.getResultList();

		return categoryList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.BaseDAO#getBaseEntity()
	 */
	@Override
	protected Object getBaseEntity() {
		EmailTemplateCategoryMaster emailTemplateCategoryMaster = new EmailTemplateCategoryMaster();
		return emailTemplateCategoryMaster;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.EmailTemplateSubCategoryMasterDAO#findbyId(long)
	 */
	@Override
	public EmailTemplateSubCategoryMaster findbyId(long categoryId) {

		EmailTemplateSubCategoryMaster categoryMaster = super.findById(
				EmailTemplateSubCategoryMaster.class, categoryId);

		return categoryMaster;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.EmailTemplateSubCategoryMasterDAO#getSubCategoryList(
	 * java.lang.Long)
	 */
	@Override
	public List<EmailTemplateSubCategoryMaster> getSubCategoryList(
			Long emailTemplateCategoryId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmailTemplateSubCategoryMaster> criteriaQuery = cb
				.createQuery(EmailTemplateSubCategoryMaster.class);
		Root<EmailTemplateSubCategoryMaster> categoryRoot = criteriaQuery
				.from(EmailTemplateSubCategoryMaster.class);

		criteriaQuery.select(categoryRoot);

		Join<EmailTemplateSubCategoryMaster, EmailTemplateCategoryMaster> categorySubCatRootJoin = categoryRoot
				.join(EmailTemplateSubCategoryMaster_.emailTemplateCategoryMaster);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(categorySubCatRootJoin
				.get(EmailTemplateCategoryMaster_.emailTemplateCategoryId),
				emailTemplateCategoryId));
		criteriaQuery.where(restriction);

		TypedQuery<EmailTemplateSubCategoryMaster> categoryQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<EmailTemplateSubCategoryMaster> categoryList = categoryQuery
				.getResultList();

		return categoryList;

	}

	
}
