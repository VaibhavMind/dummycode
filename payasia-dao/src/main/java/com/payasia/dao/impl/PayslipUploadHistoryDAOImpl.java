package com.payasia.dao.impl;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.springframework.stereotype.Repository;

import com.payasia.dao.BaseDAO;
import com.payasia.dao.PayslipUploadHistoryDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.MonthMaster;
import com.payasia.dao.bean.MonthMaster_;
import com.payasia.dao.bean.PayslipUploadHistory;
import com.payasia.dao.bean.PayslipUploadHistory_;

 
/**
 * The Class PayslipUploadHistoryDAOImpl.
 */
@Repository
public class PayslipUploadHistoryDAOImpl extends BaseDAO implements
		PayslipUploadHistoryDAO {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.PayslipUploadHistoryDAO#save(com.payasia.dao.bean.
	 * PayslipUploadHistory)
	 */
	@Override
	public void save(PayslipUploadHistory payslipUploadHistory) {
		super.save(payslipUploadHistory);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.PayslipUploadHistoryDAO#findByCompany(java.lang.Long)
	 */
	@Override
	public PayslipUploadHistory findByCompany(Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<PayslipUploadHistory> criteriaQuery = cb
				.createQuery(PayslipUploadHistory.class);
		Root<PayslipUploadHistory> empRoot = criteriaQuery
				.from(PayslipUploadHistory.class);
		Join<PayslipUploadHistory, Company> empCompanyJoin = empRoot
				.join(PayslipUploadHistory_.company);
		criteriaQuery.select(empRoot);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction,
				cb.equal(empCompanyJoin.get(Company_.companyId), companyId));

		Subquery<Timestamp> subquery = criteriaQuery.subquery(Timestamp.class);
		Root<PayslipUploadHistory> fromDynamicForm = subquery
				.from(PayslipUploadHistory.class);
		subquery.select(cb.greatest(fromDynamicForm.get(
				PayslipUploadHistory_.payslip_Upload_Date).as(Timestamp.class)));

		Join<PayslipUploadHistory, Company> dynamicFormCompanySubJoin = fromDynamicForm
				.join(PayslipUploadHistory_.company);

		subquery.where(cb.equal(
				dynamicFormCompanySubJoin.get(Company_.companyId), companyId));
		restriction = cb.and(restriction, cb.equal(
				empRoot.get(PayslipUploadHistory_.payslip_Upload_Date),
				subquery));
		criteriaQuery.where(restriction);
		TypedQuery<PayslipUploadHistory> empTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		if (empTypedQuery.getResultList().size() > 0) {
			empTypedQuery.setMaxResults(1);
			return empTypedQuery.getSingleResult();
		}

		return null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.PayslipUploadHistoryDAO#newTranSave(com.payasia.dao.bean
	 * .PayslipUploadHistory)
	 */
	@Override
	public void newTranSave(PayslipUploadHistory payslipUploadHistory) {
		super.save(payslipUploadHistory);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.BaseDAO#getBaseEntity()
	 */
	@Override
	protected Object getBaseEntity() {
		PayslipUploadHistory payslipUploadHistory = new PayslipUploadHistory();
		return payslipUploadHistory;
	}

	@Override
	public PayslipUploadHistory findByCondition(Long companyId, Long monthId,
			Integer year) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<PayslipUploadHistory> criteriaQuery = cb
				.createQuery(PayslipUploadHistory.class);
		Root<PayslipUploadHistory> payRelRoot = criteriaQuery
				.from(PayslipUploadHistory.class);

		criteriaQuery.select(payRelRoot);

		Join<PayslipUploadHistory, Company> companyJoin = payRelRoot
				.join(PayslipUploadHistory_.company);
		Join<PayslipUploadHistory, MonthMaster> monthJoin = payRelRoot
				.join(PayslipUploadHistory_.monthMaster);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(companyJoin.get(Company_.companyId), companyId));
		restriction = cb.and(restriction, cb.lessThanOrEqualTo(
				payRelRoot.get(PayslipUploadHistory_.year), year));
		restriction = cb.and(restriction,
				cb.lessThan(monthJoin.get(MonthMaster_.monthId), monthId));

		criteriaQuery.where(restriction);
		criteriaQuery.orderBy(cb.desc(payRelRoot
				.get(PayslipUploadHistory_.year)));
		criteriaQuery.orderBy(cb.desc(monthJoin.get(MonthMaster_.monthId)));
		TypedQuery<PayslipUploadHistory> companyTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		List<PayslipUploadHistory> allPayRelList = companyTypedQuery
				.getResultList();
		if ( !allPayRelList.isEmpty()) {
			return allPayRelList.get(0);
		}
		return null;

	}
}
