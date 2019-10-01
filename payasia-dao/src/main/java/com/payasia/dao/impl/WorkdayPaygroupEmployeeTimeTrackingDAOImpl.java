package com.payasia.dao.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.persistence.Query;
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
import com.payasia.dao.WorkdayPaygroupEmployeeTimeTrackingDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.Employee_;
import com.payasia.dao.bean.WorkdayPaygroupEmployeeTimeTracking;
import com.payasia.dao.bean.WorkdayPaygroupEmployeeTimeTracking_;

@Repository
public class WorkdayPaygroupEmployeeTimeTrackingDAOImpl extends BaseDAO implements WorkdayPaygroupEmployeeTimeTrackingDAO {

	@Override
	protected Object getBaseEntity() {
		return new WorkdayPaygroupEmployeeTimeTracking();
	}
	
	
	@Override
	public void save(WorkdayPaygroupEmployeeTimeTracking workdayPaygroupEmployeeTimeTracking) {
		super.save(workdayPaygroupEmployeeTimeTracking);
	}

	@Override
	public void update(WorkdayPaygroupEmployeeTimeTracking workdayPaygroupEmployeeTimeTracking) {
		super.update(workdayPaygroupEmployeeTimeTracking);
	}

	@Override
	public WorkdayPaygroupEmployeeTimeTracking saveReturn(WorkdayPaygroupEmployeeTimeTracking workdayPaygroupEmployeeTimeTracking) {

		WorkdayPaygroupEmployeeTimeTracking persistObj = workdayPaygroupEmployeeTimeTracking;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (WorkdayPaygroupEmployeeTimeTracking) getBaseEntity();
			beanUtil.copyProperties(persistObj, workdayPaygroupEmployeeTimeTracking);
		} catch (IllegalAccessException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		} catch (InvocationTargetException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		}
		this.entityManagerFactory.persist(persistObj);
		return persistObj;
	}

	/*@Override
	public WorkdayPaygroupEmployeeTimeTracking findEmployeeTimeTrackingByDate(long employeeId, long companyId,
			Timestamp startDate, Timestamp endDate) {
		
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<WorkdayPaygroupEmployeeTimeTracking> criteriaQuery = cb.createQuery(WorkdayPaygroupEmployeeTimeTracking.class);
		Root<WorkdayPaygroupEmployeeTimeTracking> employeeTimeTrackingRoot = criteriaQuery.from(WorkdayPaygroupEmployeeTimeTracking.class);

		criteriaQuery.select(employeeTimeTrackingRoot);
		Join<WorkdayPaygroupEmployeeTimeTracking, Employee> employeeJoin = employeeTimeTrackingRoot.join(WorkdayPaygroupEmployeeTimeTracking_.employee);
		Join<WorkdayPaygroupEmployeeTimeTracking, Company> companyJoin = employeeTimeTrackingRoot.join(WorkdayPaygroupEmployeeTimeTracking_.company);
		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(employeeJoin.get(Employee_.employeeId), employeeId));
		restriction = cb.and(restriction, cb.equal(companyJoin.get(Company_.companyId), companyId));
		restriction = cb.and(restriction, cb.equal(employeeTimeTrackingRoot.get(WorkdayPaygroupEmployeeTimeTracking_.timeTrackingStartDate), startDate));
		restriction = cb.and(restriction, cb.equal(employeeTimeTrackingRoot.get(WorkdayPaygroupEmployeeTimeTracking_.timeTrackingEndDate), endDate));
		criteriaQuery.where(restriction);
		TypedQuery<WorkdayPaygroupEmployeeTimeTracking> typedQuery = entityManagerFactory.createQuery(criteriaQuery);
		List<WorkdayPaygroupEmployeeTimeTracking> list = typedQuery.getResultList();
		if(list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}*/
	
	@Override
	public WorkdayPaygroupEmployeeTimeTracking findEmployeeTimeTracking(long employeeId, long companyId, String code) {
		
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<WorkdayPaygroupEmployeeTimeTracking> criteriaQuery = cb.createQuery(WorkdayPaygroupEmployeeTimeTracking.class);
		Root<WorkdayPaygroupEmployeeTimeTracking> employeeTimeOffRoot = criteriaQuery.from(WorkdayPaygroupEmployeeTimeTracking.class);

		criteriaQuery.select(employeeTimeOffRoot);
		Join<WorkdayPaygroupEmployeeTimeTracking, Employee> employeeJoin = employeeTimeOffRoot.join(WorkdayPaygroupEmployeeTimeTracking_.employee);
		Join<WorkdayPaygroupEmployeeTimeTracking, Company> companyJoin = employeeTimeOffRoot.join(WorkdayPaygroupEmployeeTimeTracking_.company);
		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(employeeJoin.get(Employee_.employeeId), employeeId));
		restriction = cb.and(restriction, cb.equal(companyJoin.get(Company_.companyId), companyId));
		restriction = cb.and(restriction, cb.equal(employeeTimeOffRoot.get(WorkdayPaygroupEmployeeTimeTracking_.code), code));
		criteriaQuery.where(restriction);
		TypedQuery<WorkdayPaygroupEmployeeTimeTracking> typedQuery = entityManagerFactory.createQuery(criteriaQuery);
		List<WorkdayPaygroupEmployeeTimeTracking> list = typedQuery.getResultList();
		if(list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}
	
	@Override
	public void deleteEmployeeTimeTracking(long employeeId, long companyId, String code) {
		
		String queryString = "DELETE FROM WorkdayPaygroupEmployeeTimeTracking t WHERE t.company.companyId = :companyId AND t.employee.employeeId = :employeeId AND t.code = :code";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("companyId", companyId);
		q.setParameter("employeeId", employeeId);
		q.setParameter("code", code);

		q.executeUpdate();
	}
}
