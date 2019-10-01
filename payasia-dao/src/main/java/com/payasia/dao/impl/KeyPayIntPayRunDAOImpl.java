package com.payasia.dao.impl;

import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.util.NullAwareBeanUtilsBean;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.KeyPayIntPayRunDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.KeyPayIntPayRun;
import com.payasia.dao.bean.KeyPayIntPayRun_;

@Repository
public class KeyPayIntPayRunDAOImpl extends BaseDAO implements
		KeyPayIntPayRunDAO {

	@Override
	protected Object getBaseEntity() {
		KeyPayIntPayRun KeyPayIntPayRun = new KeyPayIntPayRun();
		return KeyPayIntPayRun;
	}

	@Override
	public void save(KeyPayIntPayRun KeyPayIntPayRun) {
		super.save(KeyPayIntPayRun);
	}

	@Override
	public void delete(KeyPayIntPayRun KeyPayIntPayRun) {
		super.delete(KeyPayIntPayRun);
	}

	@Override
	public void update(KeyPayIntPayRun KeyPayIntPayRun) {
		super.update(KeyPayIntPayRun);
	}

	@Override
	public KeyPayIntPayRun findById(Long KeyPayIntPayRunId) {
		return super.findById(KeyPayIntPayRun.class, KeyPayIntPayRunId);
	}

	@Override
	public KeyPayIntPayRun saveReturn(KeyPayIntPayRun keyPayIntPayRun) {
		KeyPayIntPayRun persistObj = keyPayIntPayRun;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (KeyPayIntPayRun) getBaseEntity();
			beanUtil.copyProperties(persistObj, keyPayIntPayRun);
		} catch (IllegalAccessException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		} catch (InvocationTargetException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		}
		this.entityManagerFactory.persist(persistObj);
		return persistObj;
	}

	@Override
	public List<KeyPayIntPayRun> findAllPayRun(Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<KeyPayIntPayRun> criteriaQuery = cb
				.createQuery(KeyPayIntPayRun.class);
		Root<KeyPayIntPayRun> root = criteriaQuery.from(KeyPayIntPayRun.class);
		criteriaQuery.select(root);

		Join<KeyPayIntPayRun, Company> companyJoin = root
				.join(KeyPayIntPayRun_.company);
		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(companyJoin.get(Company_.companyId), companyId));

		criteriaQuery.where(restriction);

		TypedQuery<KeyPayIntPayRun> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		return typedQuery.getResultList();
	}

	@Override
	public KeyPayIntPayRun findByPayRunId(Long companyId, Long payRunId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<KeyPayIntPayRun> criteriaQuery = cb
				.createQuery(KeyPayIntPayRun.class);
		Root<KeyPayIntPayRun> root = criteriaQuery.from(KeyPayIntPayRun.class);
		criteriaQuery.select(root);
		Join<KeyPayIntPayRun, Company> companyJoin = root
				.join(KeyPayIntPayRun_.company);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction,
				cb.equal(root.get(KeyPayIntPayRun_.payRunId), payRunId));
		restriction = cb.and(restriction,
				cb.equal(companyJoin.get(Company_.companyId), companyId));
		criteriaQuery.where(restriction);

		TypedQuery<KeyPayIntPayRun> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		if (!typedQuery.getResultList().isEmpty()) {
			return typedQuery.getResultList().get(0);
		}

		return null;
	}

	@Override
	public List<KeyPayIntPayRun> findByPayRunParameterDate(Long companyId,
			Timestamp startCalDate, Timestamp endCalDate) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<KeyPayIntPayRun> criteriaQuery = cb
				.createQuery(KeyPayIntPayRun.class);
		Root<KeyPayIntPayRun> root = criteriaQuery.from(KeyPayIntPayRun.class);
		criteriaQuery.select(root);

		Join<KeyPayIntPayRun, Company> companyJoin = root
				.join(KeyPayIntPayRun_.company);
		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(companyJoin.get(Company_.companyId), companyId));

		restriction = cb.and(restriction, cb.greaterThanOrEqualTo(
				root.get(KeyPayIntPayRun_.payRunParameterDate), startCalDate));
		restriction = cb.and(restriction, cb.lessThanOrEqualTo(
				root.get(KeyPayIntPayRun_.payRunParameterDate), endCalDate));

		criteriaQuery.where(restriction);

		TypedQuery<KeyPayIntPayRun> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		return typedQuery.getResultList();
	}

}
