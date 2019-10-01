package com.payasia.dao.impl;

import java.lang.reflect.InvocationTargetException;
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
import com.payasia.dao.KeyPayIntPayRunDetailDAO;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.Employee_;
import com.payasia.dao.bean.KeyPayIntPayRun;
import com.payasia.dao.bean.KeyPayIntPayRunDetail;
import com.payasia.dao.bean.KeyPayIntPayRunDetail_;
import com.payasia.dao.bean.KeyPayIntPayRun_;

@Repository
public class KeyPayIntPayRunDetailDAOImpl extends BaseDAO implements
		KeyPayIntPayRunDetailDAO {

	@Override
	protected Object getBaseEntity() {
		KeyPayIntPayRunDetail keyPayIntPayRunDetail = new KeyPayIntPayRunDetail();
		return keyPayIntPayRunDetail;
	}

	@Override
	public void save(KeyPayIntPayRunDetail keyPayIntPayRunDetail) {
		super.save(keyPayIntPayRunDetail);
	}

	@Override
	public void delete(KeyPayIntPayRunDetail keyPayIntPayRunDetail) {
		super.delete(keyPayIntPayRunDetail);
	}

	@Override
	public KeyPayIntPayRunDetail saveReturn(
			KeyPayIntPayRunDetail keyPayIntPayRunDetail) {
		KeyPayIntPayRunDetail persistObj = keyPayIntPayRunDetail;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (KeyPayIntPayRunDetail) getBaseEntity();
			beanUtil.copyProperties(persistObj, keyPayIntPayRunDetail);
		} catch (IllegalAccessException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		} catch (InvocationTargetException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		}
		this.entityManagerFactory.persist(persistObj);
		return persistObj;
	}

	@Override
	public void update(KeyPayIntPayRunDetail keyPayIntPayRunDetail) {
		super.update(keyPayIntPayRunDetail);
	}

	@Override
	public List<KeyPayIntPayRunDetail> findByKeyPayIntPayRunId(
			Long keyPayIntPayRunId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<KeyPayIntPayRunDetail> criteriaQuery = cb
				.createQuery(KeyPayIntPayRunDetail.class);
		Root<KeyPayIntPayRunDetail> root = criteriaQuery
				.from(KeyPayIntPayRunDetail.class);
		criteriaQuery.select(root);

		Join<KeyPayIntPayRunDetail, KeyPayIntPayRun> keyPayIntPayRunJoin = root
				.join(KeyPayIntPayRunDetail_.keyPayIntPayRun);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(
				keyPayIntPayRunJoin.get(KeyPayIntPayRun_.keyPayIntPayRunId),
				keyPayIntPayRunId));

		criteriaQuery.where(restriction);

		TypedQuery<KeyPayIntPayRunDetail> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		return typedQuery.getResultList();
	}

	@Override
	public List<Long> findKeyPayRunDetailId(Long keyPayIntPayRunId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
		Root<KeyPayIntPayRunDetail> root = criteriaQuery
				.from(KeyPayIntPayRunDetail.class);
		criteriaQuery.select(root.get(KeyPayIntPayRunDetail_.detailId));

		Join<KeyPayIntPayRunDetail, KeyPayIntPayRun> keyPayIntPayRunJoin = root
				.join(KeyPayIntPayRunDetail_.keyPayIntPayRun);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(
				keyPayIntPayRunJoin.get(KeyPayIntPayRun_.keyPayIntPayRunId),
				keyPayIntPayRunId));

		criteriaQuery.where(restriction);

		TypedQuery<Long> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		return typedQuery.getResultList();
	}

	@Override
	public KeyPayIntPayRunDetail findByCondition(Long keyPayIntPayRunId,
			Long leaveCategoryId, String externalId, Long employeeId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<KeyPayIntPayRunDetail> criteriaQuery = cb
				.createQuery(KeyPayIntPayRunDetail.class);
		Root<KeyPayIntPayRunDetail> root = criteriaQuery
				.from(KeyPayIntPayRunDetail.class);
		criteriaQuery.select(root);

		Join<KeyPayIntPayRunDetail, KeyPayIntPayRun> keyPayIntPayRunJoin = root
				.join(KeyPayIntPayRunDetail_.keyPayIntPayRun);
		Join<KeyPayIntPayRunDetail, Employee> employeeJoin = root
				.join(KeyPayIntPayRunDetail_.employee);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(
				root.get(KeyPayIntPayRunDetail_.externalId), externalId));
		restriction = cb.and(restriction, cb.equal(
				root.get(KeyPayIntPayRunDetail_.leaveCategoryId),
				leaveCategoryId));
		restriction = cb.and(restriction,
				cb.equal(employeeJoin.get(Employee_.employeeId), employeeId));
		restriction = cb.and(restriction, cb.equal(
				keyPayIntPayRunJoin.get(KeyPayIntPayRun_.keyPayIntPayRunId),
				keyPayIntPayRunId));

		criteriaQuery.where(restriction);

		TypedQuery<KeyPayIntPayRunDetail> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		List<KeyPayIntPayRunDetail> recordList = typedQuery.getResultList();
		if (recordList != null && !recordList.isEmpty()) {
			return recordList.get(0);
		}
		return null;
	}

}
