package com.payasia.dao.impl;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.payasia.dao.BaseDAO;
import com.payasia.dao.DynamicFormSeqDAO;
import com.payasia.dao.bean.DynamicFormSeq;

@Repository
public class DynamicFormSeqDAOImpl extends BaseDAO implements DynamicFormSeqDAO {

	@Override
	protected Object getBaseEntity() {
		DynamicFormSeq dynamicFormSeq = new DynamicFormSeq();
		return dynamicFormSeq;
	}

	@Override
	public void save(DynamicFormSeq dynamicFormSeq) {
		super.save(dynamicFormSeq);
	}

	@Override
	public void update(DynamicFormSeq dynamicFormSeq) {
		super.update(dynamicFormSeq);
	}

	@Override
	public void delete(DynamicFormSeq dynamicFormSeq) {
		super.delete(dynamicFormSeq);
	}

	@Override
	public Long getNextVal() {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<DynamicFormSeq> criteriaQuery = cb
				.createQuery(DynamicFormSeq.class);
		Root<DynamicFormSeq> dynamicFormSeqRoot = criteriaQuery
				.from(DynamicFormSeq.class);
		criteriaQuery.select(dynamicFormSeqRoot);

		TypedQuery<DynamicFormSeq> maxDynamicTableRecordSeqQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		DynamicFormSeq dynamicFormSeq = maxDynamicTableRecordSeqQuery
				.getSingleResult();
		long maxFormId = dynamicFormSeq.getNextVal();

		delete(dynamicFormSeq);
		dynamicFormSeq = new DynamicFormSeq();
		dynamicFormSeq.setNextVal(maxFormId + 1);
		save(dynamicFormSeq);

		return maxFormId;

	}
}
