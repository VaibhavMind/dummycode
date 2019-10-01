package com.payasia.dao.impl;

import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.payasia.dao.BaseDAO;
import com.payasia.dao.LanguageMasterDAO;
import com.payasia.dao.bean.LanguageMaster;
import com.payasia.dao.bean.LanguageMaster_;

/**
 * The Class LanguageMasterDAOImpl.
 */
/**
 * @author vivekjain
 * 
 */
@Repository
public class LanguageMasterDAOImpl extends BaseDAO implements LanguageMasterDAO {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.BaseDAO#getBaseEntity()
	 */
	@Override
	protected Object getBaseEntity() {
		LanguageMaster languageMaster = new LanguageMaster();
		return languageMaster;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.LanguageMasterDAO#findByLanguageCode(java.lang.String)
	 */
	@Override
	public LanguageMaster findByLanguageCode(String lanCode) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LanguageMaster> criteriaQuery = cb
				.createQuery(LanguageMaster.class);
		Root<LanguageMaster> languageMasterRoot = criteriaQuery
				.from(LanguageMaster.class);
		criteriaQuery.select(languageMasterRoot);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(
				languageMasterRoot.get(LanguageMaster_.languageCode), lanCode));

		criteriaQuery.where(restriction);

		TypedQuery<LanguageMaster> languageMasterQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<LanguageMaster> languageMasterList = languageMasterQuery
				.getResultList();
		if (languageMasterList != null &&  !languageMasterList.isEmpty()) {
			return languageMasterList.get(0);
		}
		return null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.LanguageMasterDAO#getLanguages()
	 */
	@Override
	public List<LanguageMaster> getLanguages() {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LanguageMaster> criteriaQuery = cb
				.createQuery(LanguageMaster.class);
		Root<LanguageMaster> languageMasterRoot = criteriaQuery
				.from(LanguageMaster.class);
		criteriaQuery.select(languageMasterRoot);
		
		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(languageMasterRoot.get(LanguageMaster_.languageActive), true));
		criteriaQuery.where(restriction);
		
		TypedQuery<LanguageMaster> languageMasterTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<LanguageMaster> languageMasterList = languageMasterTypedQuery
				.getResultList();

		return languageMasterList;
	}

	@Override
	public LanguageMaster getDefaultLanguage() {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LanguageMaster> criteriaQuery = cb
				.createQuery(LanguageMaster.class);
		Root<LanguageMaster> languageMasterRoot = criteriaQuery
				.from(LanguageMaster.class);
		criteriaQuery.select(languageMasterRoot);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(
				languageMasterRoot.get(LanguageMaster_.defaultLang), true));

		criteriaQuery.where(restriction);

		TypedQuery<LanguageMaster> languageMasterQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<LanguageMaster> languageMasterList = languageMasterQuery
				.getResultList();
		if (languageMasterList != null &&  !languageMasterList.isEmpty()) {
			return languageMasterList.get(0);
		}
		return null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.LanguageMasterDAO#findById(java.lang.Long)
	 */
	@Override
	public LanguageMaster findById(Long languageId) {
		return super.findById(LanguageMaster.class, languageId);
	}
}
