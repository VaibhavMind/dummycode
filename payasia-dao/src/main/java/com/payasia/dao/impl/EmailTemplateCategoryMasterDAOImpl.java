package com.payasia.dao.impl;

import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.payasia.dao.BaseDAO;
import com.payasia.dao.EmailTemplateCategoryMasterDAO;
import com.payasia.dao.bean.EmailTemplateCategoryMaster;
import com.payasia.dao.bean.EmailTemplateCategoryMaster_;

/**
 * The Class EmailTemplateCategoryMasterDAOImpl.
 */
/**
 * @author vivekjain
 * 
 */
@Repository
public class EmailTemplateCategoryMasterDAOImpl extends BaseDAO implements
		EmailTemplateCategoryMasterDAO {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.EmailTemplateCategoryMasterDAO#findAll()
	 */
	@Override
	public List<EmailTemplateCategoryMaster> findAll() {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmailTemplateCategoryMaster> criteriaQuery = cb
				.createQuery(EmailTemplateCategoryMaster.class);
		Root<EmailTemplateCategoryMaster> categoryRoot = criteriaQuery
				.from(EmailTemplateCategoryMaster.class);

		criteriaQuery.select(categoryRoot);
		criteriaQuery.orderBy(cb.asc(categoryRoot
				.get(EmailTemplateCategoryMaster_.emailTemplateCategoryId)));

		TypedQuery<EmailTemplateCategoryMaster> categoryQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<EmailTemplateCategoryMaster> categoryList = categoryQuery
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
	 * @see com.payasia.dao.EmailTemplateCategoryMasterDAO#findbyId(long)
	 */
	@Override
	public EmailTemplateCategoryMaster findbyId(long categoryId) {

		EmailTemplateCategoryMaster categoryMaster = super.findById(
				EmailTemplateCategoryMaster.class, categoryId);

		return categoryMaster;
	}

}
