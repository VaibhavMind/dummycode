package com.payasia.dao.impl;

import java.lang.reflect.InvocationTargetException;
import java.sql.Date;
import java.util.List;
import java.util.Map;

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
import com.payasia.dao.WorkdayPaygroupBatchDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.WorkdayAppCodeMaster;
import com.payasia.dao.bean.WorkdayAppCodeMaster_;
import com.payasia.dao.bean.WorkdayPaygroupBatch;
import com.payasia.dao.bean.WorkdayPaygroupBatch_;

@Repository
public class WorkdayPaygroupBatchDAOImpl extends BaseDAO implements WorkdayPaygroupBatchDAO {

	@Override
	protected Object getBaseEntity() {
		return new WorkdayPaygroupBatch();
	}

	@Override
	public WorkdayPaygroupBatch findById(long workdayPayGroupBatchId) {
		return super.findById(WorkdayPaygroupBatch.class, workdayPayGroupBatchId);
	}

	@Override
	public WorkdayPaygroupBatch findByWorkdayPaygroupBatchId(Long workdayPayGroupBatchId, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<WorkdayPaygroupBatch> criteriaQuery = cb.createQuery(WorkdayPaygroupBatch.class);
		Root<WorkdayPaygroupBatch> paygroupBatchRoot = criteriaQuery.from(WorkdayPaygroupBatch.class);

		criteriaQuery.select(paygroupBatchRoot);
		Join<WorkdayPaygroupBatch, Company> companyJoin = paygroupBatchRoot.join(WorkdayPaygroupBatch_.company);
		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(companyJoin.get(Company_.companyId), companyId));
		restriction = cb.and(restriction, cb.greaterThanOrEqualTo(
				paygroupBatchRoot.get(WorkdayPaygroupBatch_.workdayPaygroupBatchId), workdayPayGroupBatchId));
		criteriaQuery.where(restriction);
		TypedQuery<WorkdayPaygroupBatch> typedQuery = entityManagerFactory.createQuery(criteriaQuery);

		if (typedQuery.getResultList() != null && !typedQuery.getResultList().isEmpty()) {
			return typedQuery.getResultList().get(0);
		}
		return null;
	}

	@Override
	public List<WorkdayPaygroupBatch> findByCompanyId(Long companyId, Map<String, Object> dataRecord) {

		java.util.Date batchFromDate = (java.util.Date) dataRecord.get("fromDate");
		java.util.Date batchToDate = (java.util.Date) dataRecord.get("toDate");
		String isEmpData = (String) dataRecord.get("isEmpData");
		Long batchTypeVal = dataRecord.get("batchTypeVal") == null ? 0L : (Long) dataRecord.get("batchTypeVal");
		Boolean isEmp = false;
		if (isEmpData.contains("E")) {
			isEmp = true;
		}

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<WorkdayPaygroupBatch> criteriaQuery = cb.createQuery(WorkdayPaygroupBatch.class);
		Root<WorkdayPaygroupBatch> paygroupBatchRoot = criteriaQuery.from(WorkdayPaygroupBatch.class);

		criteriaQuery.select(paygroupBatchRoot);
		Join<WorkdayPaygroupBatch, Company> companyJoin = paygroupBatchRoot.join(WorkdayPaygroupBatch_.company);
		Join<WorkdayPaygroupBatch, WorkdayAppCodeMaster> workdayAppCodeMasterJoin = paygroupBatchRoot
				.join(WorkdayPaygroupBatch_.batchTypeAppCode);
		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(companyJoin.get(Company_.companyId), companyId));
		restriction = cb.and(restriction, cb
				.greaterThanOrEqualTo(paygroupBatchRoot.get(WorkdayPaygroupBatch_.payPeriodStartDate), batchFromDate));
		restriction = cb.and(restriction,
				cb.lessThanOrEqualTo(paygroupBatchRoot.get(WorkdayPaygroupBatch_.payPeriodStartDate), batchToDate));
		restriction = cb.and(restriction, cb.equal(paygroupBatchRoot.get(WorkdayPaygroupBatch_.isLatest), true));
		restriction = cb.and(restriction, cb.equal(paygroupBatchRoot.get(WorkdayPaygroupBatch_.isEmployeeData), isEmp));
		restriction = cb.and(restriction,
				cb.equal(workdayAppCodeMasterJoin.get(WorkdayAppCodeMaster_.workdayAppCodeId), batchTypeVal));
		criteriaQuery.where(restriction);
		TypedQuery<WorkdayPaygroupBatch> typedQuery = entityManagerFactory.createQuery(criteriaQuery);
		return typedQuery.getResultList();
	}

	@Override
	public List<WorkdayPaygroupBatch> findByCompanyId(Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<WorkdayPaygroupBatch> criteriaQuery = cb.createQuery(WorkdayPaygroupBatch.class);
		Root<WorkdayPaygroupBatch> paygroupBatchRoot = criteriaQuery.from(WorkdayPaygroupBatch.class);

		criteriaQuery.select(paygroupBatchRoot);
		Join<WorkdayPaygroupBatch, Company> companyJoin = paygroupBatchRoot.join(WorkdayPaygroupBatch_.company);
		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(companyJoin.get(Company_.companyId), companyId));
		restriction = cb.and(restriction, cb.equal(paygroupBatchRoot.get(WorkdayPaygroupBatch_.isLatest), true));
		criteriaQuery.where(restriction);
		TypedQuery<WorkdayPaygroupBatch> typedQuery = entityManagerFactory.createQuery(criteriaQuery);
		return typedQuery.getResultList();
	}

	@Override
	public void save(WorkdayPaygroupBatch workdayPaygroupBatch) {
		super.save(workdayPaygroupBatch);
	}

	@Override
	public void update(WorkdayPaygroupBatch workdayPaygroupBatch) {
		super.update(workdayPaygroupBatch);
	}

	@Override
	public WorkdayPaygroupBatch saveReturn(WorkdayPaygroupBatch workdayPaygroupBatch) {

		WorkdayPaygroupBatch persistObj = workdayPaygroupBatch;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (WorkdayPaygroupBatch) getBaseEntity();
			beanUtil.copyProperties(persistObj, workdayPaygroupBatch);
		} catch (IllegalAccessException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		} catch (InvocationTargetException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		}
		this.entityManagerFactory.persist(persistObj);
		return persistObj;
	}

	@Override
	public WorkdayPaygroupBatch findByPayPeriodDates(long companyId, Date payPeriodStartDate, Date payPeriodEndDate,
			boolean isEmployeeData) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<WorkdayPaygroupBatch> criteriaQuery = cb.createQuery(WorkdayPaygroupBatch.class);
		Root<WorkdayPaygroupBatch> paygroupBatchRoot = criteriaQuery.from(WorkdayPaygroupBatch.class);

		criteriaQuery.select(paygroupBatchRoot);
		Join<WorkdayPaygroupBatch, Company> companyJoin = paygroupBatchRoot.join(WorkdayPaygroupBatch_.company);
		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(companyJoin.get(Company_.companyId), companyId));
		restriction = cb.and(restriction,
				cb.equal(paygroupBatchRoot.get(WorkdayPaygroupBatch_.payPeriodStartDate), payPeriodStartDate));
		restriction = cb.and(restriction,
				cb.equal(paygroupBatchRoot.get(WorkdayPaygroupBatch_.payPeriodEndDate), payPeriodEndDate));
		restriction = cb.and(restriction, cb.equal(paygroupBatchRoot.get(WorkdayPaygroupBatch_.isLatest), true));
		restriction = cb.and(restriction,
				cb.equal(paygroupBatchRoot.get(WorkdayPaygroupBatch_.isEmployeeData), isEmployeeData));
		criteriaQuery.where(restriction);
		TypedQuery<WorkdayPaygroupBatch> typedQuery = entityManagerFactory.createQuery(criteriaQuery);
		List<WorkdayPaygroupBatch> list = typedQuery.getResultList();
		if (list != null && !list.isEmpty())
			return list.get(0);
		return null;
	}

}
