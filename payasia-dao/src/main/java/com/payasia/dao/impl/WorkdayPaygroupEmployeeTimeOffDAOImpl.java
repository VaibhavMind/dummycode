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
import com.payasia.dao.WorkdayPaygroupEmployeeTimeOffDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.Employee_;
import com.payasia.dao.bean.WorkdayPaygroupEmployeeTimeOff;
import com.payasia.dao.bean.WorkdayPaygroupEmployeeTimeOff_;

@Repository
public class WorkdayPaygroupEmployeeTimeOffDAOImpl extends BaseDAO implements WorkdayPaygroupEmployeeTimeOffDAO {

	@Override
	protected Object getBaseEntity() {
		return new WorkdayPaygroupEmployeeTimeOff();
	}
	
	
	@Override
	public void save(WorkdayPaygroupEmployeeTimeOff workdayPaygroupEmployeeTimeOff) {
		super.save(workdayPaygroupEmployeeTimeOff);
	}

	@Override
	public void update(WorkdayPaygroupEmployeeTimeOff workdayPaygroupEmployeeTimeOff) {
		super.update(workdayPaygroupEmployeeTimeOff);
	}

	@Override
	public WorkdayPaygroupEmployeeTimeOff saveReturn(WorkdayPaygroupEmployeeTimeOff workdayPaygroupEmployeeTimeOff) {

		WorkdayPaygroupEmployeeTimeOff persistObj = workdayPaygroupEmployeeTimeOff;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (WorkdayPaygroupEmployeeTimeOff) getBaseEntity();
			beanUtil.copyProperties(persistObj, workdayPaygroupEmployeeTimeOff);
		} catch (IllegalAccessException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		} catch (InvocationTargetException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		}
		this.entityManagerFactory.persist(persistObj);
		return persistObj;
	}

	/*@Override
	public WorkdayPaygroupEmployeeTimeOff findEmployeeTimeOffByDate(long employeeId, long companyId,
			Timestamp date) {
		
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<WorkdayPaygroupEmployeeTimeOff> criteriaQuery = cb.createQuery(WorkdayPaygroupEmployeeTimeOff.class);
		Root<WorkdayPaygroupEmployeeTimeOff> employeeTimeOffRoot = criteriaQuery.from(WorkdayPaygroupEmployeeTimeOff.class);

		criteriaQuery.select(employeeTimeOffRoot);
		Join<WorkdayPaygroupEmployeeTimeOff, Employee> employeeJoin = employeeTimeOffRoot.join(WorkdayPaygroupEmployeeTimeOff_.employee);
		Join<WorkdayPaygroupEmployeeTimeOff, Company> companyJoin = employeeTimeOffRoot.join(WorkdayPaygroupEmployeeTimeOff_.company);
		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(employeeJoin.get(Employee_.employeeId), employeeId));
		restriction = cb.and(restriction, cb.equal(companyJoin.get(Company_.companyId), companyId));
		restriction = cb.and(restriction, cb.equal(employeeTimeOffRoot.get(WorkdayPaygroupEmployeeTimeOff_.timeOffDate), date));
		criteriaQuery.where(restriction);
		TypedQuery<WorkdayPaygroupEmployeeTimeOff> typedQuery = entityManagerFactory.createQuery(criteriaQuery);
		List<WorkdayPaygroupEmployeeTimeOff> list = typedQuery.getResultList();
		if(list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}*/
	
	@Override
	public WorkdayPaygroupEmployeeTimeOff findEmployeeTimeOff(long employeeId, long companyId, String code) {
		
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<WorkdayPaygroupEmployeeTimeOff> criteriaQuery = cb.createQuery(WorkdayPaygroupEmployeeTimeOff.class);
		Root<WorkdayPaygroupEmployeeTimeOff> employeeTimeOffRoot = criteriaQuery.from(WorkdayPaygroupEmployeeTimeOff.class);

		criteriaQuery.select(employeeTimeOffRoot);
		Join<WorkdayPaygroupEmployeeTimeOff, Employee> employeeJoin = employeeTimeOffRoot.join(WorkdayPaygroupEmployeeTimeOff_.employee);
		Join<WorkdayPaygroupEmployeeTimeOff, Company> companyJoin = employeeTimeOffRoot.join(WorkdayPaygroupEmployeeTimeOff_.company);
		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(employeeJoin.get(Employee_.employeeId), employeeId));
		restriction = cb.and(restriction, cb.equal(companyJoin.get(Company_.companyId), companyId));
		restriction = cb.and(restriction, cb.equal(employeeTimeOffRoot.get(WorkdayPaygroupEmployeeTimeOff_.code), code));
		criteriaQuery.where(restriction);
		TypedQuery<WorkdayPaygroupEmployeeTimeOff> typedQuery = entityManagerFactory.createQuery(criteriaQuery);
		List<WorkdayPaygroupEmployeeTimeOff> list = typedQuery.getResultList();
		if(list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}
	
	@Override
	public void deleteEmployeeTimeOff(long employeeId, long companyId, String code) {
		
		String queryString = "DELETE FROM WorkdayPaygroupEmployeeTimeOff t WHERE t.company.companyId = :companyId AND t.employee.employeeId = :employeeId AND t.code = :code";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("companyId", companyId);
		q.setParameter("employeeId", employeeId);
		q.setParameter("code", code);

		q.executeUpdate();
	}
}
