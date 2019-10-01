package com.payasia.dao.impl;

import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.payasia.dao.BaseDAO;
import com.payasia.dao.PayslipFrequencyDAO;
import com.payasia.dao.bean.PayslipFrequency;
import com.payasia.dao.bean.PayslipFrequency_;

/**
 * The Class PayslipFrequencyDAOImpl.
 */
@Repository
public class PayslipFrequencyDAOImpl extends BaseDAO implements
		PayslipFrequencyDAO {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.BaseDAO#getBaseEntity()
	 */
	@Override
	protected Object getBaseEntity() {
		 
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.PayslipFrequencyDAO#findAll()
	 */
	@Override
	public List<PayslipFrequency> findAll() {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<PayslipFrequency> criteriaQuery = cb
				.createQuery(PayslipFrequency.class);
		Root<PayslipFrequency> payslipFrequencyRoot = criteriaQuery
				.from(PayslipFrequency.class);

		criteriaQuery.select(payslipFrequencyRoot);

		criteriaQuery.orderBy(cb.asc(payslipFrequencyRoot
				.get(PayslipFrequency_.payslipFrequencyID)));

		TypedQuery<PayslipFrequency> payslipFrequencyRootQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<PayslipFrequency> allPayslipFrequencyList = payslipFrequencyRootQuery
				.getResultList();
		return allPayslipFrequencyList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.PayslipFrequencyDAO#findByFrequency(java.lang.String)
	 */
	@Override
	public PayslipFrequency findByFrequency(String frequency) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<PayslipFrequency> criteriaQuery = cb
				.createQuery(PayslipFrequency.class);
		Root<PayslipFrequency> payslipFrequencyRoot = criteriaQuery
				.from(PayslipFrequency.class);

		criteriaQuery.select(payslipFrequencyRoot);
		criteriaQuery
				.where(cb.equal(cb.upper(payslipFrequencyRoot
						.get(PayslipFrequency_.frequency)), frequency
						.toUpperCase()));
		TypedQuery<PayslipFrequency> payslipFrequencyRootQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<PayslipFrequency> payslipFreqList = payslipFrequencyRootQuery
				.getResultList();
		if (payslipFreqList != null &&  !payslipFreqList.isEmpty()) {
			return payslipFreqList.get(0);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.PayslipFrequencyDAO#findById(long)
	 */
	@Override
	public PayslipFrequency findById(long paySlipfreId) {

		PayslipFrequency payslipFrequency = super.findById(
				PayslipFrequency.class, paySlipfreId);
		return payslipFrequency;
	}

}
