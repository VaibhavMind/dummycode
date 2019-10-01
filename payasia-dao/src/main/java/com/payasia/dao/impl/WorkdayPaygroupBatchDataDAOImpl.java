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
import com.payasia.dao.WorkdayPaygroupBatchDataDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.Employee_;
import com.payasia.dao.bean.WorkdayPaygroupBatch;
import com.payasia.dao.bean.WorkdayPaygroupBatchData;
import com.payasia.dao.bean.WorkdayPaygroupBatchData_;
import com.payasia.dao.bean.WorkdayPaygroupBatch_;

@Repository
public class WorkdayPaygroupBatchDataDAOImpl extends BaseDAO implements WorkdayPaygroupBatchDataDAO {

	@Override
	protected Object getBaseEntity() {
		return new WorkdayPaygroupBatchData();
	}

	@Override
	public List<WorkdayPaygroupBatchData> findAllByPaygroupBatch(long paygroupBatchId, long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<WorkdayPaygroupBatchData> criteriaQuery = cb.createQuery(WorkdayPaygroupBatchData.class);
		Root<WorkdayPaygroupBatchData> paygroupBatchDataRoot = criteriaQuery.from(WorkdayPaygroupBatchData.class);

		criteriaQuery.select(paygroupBatchDataRoot);
		Join<WorkdayPaygroupBatchData, WorkdayPaygroupBatch> paygroupJoin = paygroupBatchDataRoot
				.join(WorkdayPaygroupBatchData_.workdayPaygroupBatch);
		Join<WorkdayPaygroupBatchData, Company> companyJoin = paygroupBatchDataRoot
				.join(WorkdayPaygroupBatchData_.company);
		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction,
				cb.equal(paygroupJoin.get(WorkdayPaygroupBatch_.workdayPaygroupBatchId), paygroupBatchId));
		restriction = cb.and(restriction, cb.equal(companyJoin.get(Company_.companyId), companyId));
		criteriaQuery.where(restriction);
		TypedQuery<WorkdayPaygroupBatchData> typedQuery = entityManagerFactory.createQuery(criteriaQuery);
		return typedQuery.getResultList();
	}

	@Override
	public void save(WorkdayPaygroupBatchData workdayPaygroupBatchData) {
		super.save(workdayPaygroupBatchData);
	}

	@Override
	public void update(WorkdayPaygroupBatchData workdayPaygroupBatchData) {
		super.update(workdayPaygroupBatchData);
	}

	@Override
	public WorkdayPaygroupBatchData saveReturn(WorkdayPaygroupBatchData workdayPaygroupBatchData) {

		WorkdayPaygroupBatchData persistObj = workdayPaygroupBatchData;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (WorkdayPaygroupBatchData) getBaseEntity();
			beanUtil.copyProperties(persistObj, workdayPaygroupBatchData);
		} catch (IllegalAccessException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		} catch (InvocationTargetException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		}
		this.entityManagerFactory.persist(persistObj);
		return persistObj;
	}

	@Override
	public List<WorkdayPaygroupBatchData> findEmployeeWorkdayPaygroupBatchData( long companyId, long batchPeriod) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<WorkdayPaygroupBatchData> criteriaQuery = cb.createQuery(WorkdayPaygroupBatchData.class);
		Root<WorkdayPaygroupBatchData> paygroupBatchDataRoot = criteriaQuery.from(WorkdayPaygroupBatchData.class);

		criteriaQuery.select(paygroupBatchDataRoot);
			Join<WorkdayPaygroupBatchData, Company> companyJoin = paygroupBatchDataRoot
				.join(WorkdayPaygroupBatchData_.company);
		Join<WorkdayPaygroupBatchData, WorkdayPaygroupBatch> workdayPaygroupBatchJoin = paygroupBatchDataRoot
				.join(WorkdayPaygroupBatchData_.workdayPaygroupBatch);
		
		Predicate restriction = cb.conjunction();
		
		restriction = cb.and(restriction, cb.equal(workdayPaygroupBatchJoin.get(WorkdayPaygroupBatch_.workdayPaygroupBatchId), batchPeriod));
		restriction = cb.and(restriction, cb.equal(companyJoin.get(Company_.companyId), companyId));
		criteriaQuery.where(restriction);
		TypedQuery<WorkdayPaygroupBatchData> typedQuery = entityManagerFactory.createQuery(criteriaQuery);
		return typedQuery.getResultList();
	}
	
	@Override
	public WorkdayPaygroupBatchData findEmployeePayrollBatchData(Long companyId,
			WorkdayPaygroupBatchData workdayPaygroupBatch) {
		
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<WorkdayPaygroupBatchData> criteriaQuery = cb.createQuery(WorkdayPaygroupBatchData.class);
		Root<WorkdayPaygroupBatchData> paygroupBatchDataRoot = criteriaQuery.from(WorkdayPaygroupBatchData.class);

		criteriaQuery.select(paygroupBatchDataRoot);
			Join<WorkdayPaygroupBatchData, Company> companyJoin = paygroupBatchDataRoot
				.join(WorkdayPaygroupBatchData_.company);
		Join<WorkdayPaygroupBatchData, WorkdayPaygroupBatch> workdayPaygroupBatchJoin = paygroupBatchDataRoot
				.join(WorkdayPaygroupBatchData_.workdayPaygroupBatch);
		Join<WorkdayPaygroupBatchData, Employee> employeeJoin = paygroupBatchDataRoot
				.join(WorkdayPaygroupBatchData_.employee);
		
		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(employeeJoin.get(Employee_.employeeId), workdayPaygroupBatch.getEmployee().getEmployeeId()));
		restriction = cb.and(restriction, cb.equal(workdayPaygroupBatchJoin.get(WorkdayPaygroupBatch_.isEmployeeData), false));
		restriction = cb.and(restriction, cb.equal(workdayPaygroupBatchJoin.get(WorkdayPaygroupBatch_.isLatest), true));
		restriction = cb.and(restriction, cb.equal(workdayPaygroupBatchJoin.get(WorkdayPaygroupBatch_.payPeriodStartDate), workdayPaygroupBatch.getWorkdayPaygroupBatch().getPayPeriodStartDate()));
		restriction = cb.and(restriction, cb.equal(workdayPaygroupBatchJoin.get(WorkdayPaygroupBatch_.payPeriodEndDate), workdayPaygroupBatch.getWorkdayPaygroupBatch().getPayPeriodEndDate()));
		restriction = cb.and(restriction, cb.equal(companyJoin.get(Company_.companyId), companyId));
		
		criteriaQuery.where(restriction);
		TypedQuery<WorkdayPaygroupBatchData> typedQuery = entityManagerFactory.createQuery(criteriaQuery);
		if(typedQuery.getResultList()==null && !typedQuery.getResultList().isEmpty()){
			return typedQuery.getResultList().get(0);
		}
		return null;
	}
}
