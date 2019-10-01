package com.payasia.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.payasia.dao.BaseDAO;
import com.payasia.dao.EmployeePasswordChangeHistoryDAO;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeePasswordChangeHistory;
import com.payasia.dao.bean.EmployeePasswordChangeHistory_;
import com.payasia.dao.bean.Employee_;

/**
 * The Class EmployeePasswordChangeHistoryDAOImpl.
 */
/**
 * @author vivekjain
 * 
 */
@Repository
public class EmployeePasswordChangeHistoryDAOImpl extends BaseDAO implements
		EmployeePasswordChangeHistoryDAO {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.BaseDAO#getBaseEntity()
	 */
	@Override
	protected Object getBaseEntity() {
		EmployeePasswordChangeHistory employeePasswordChangeHistory = new EmployeePasswordChangeHistory();
		return employeePasswordChangeHistory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.EmployeePasswordChangeHistoryDAO#save(com.payasia.dao
	 * .bean.EmployeePasswordChangeHistory)
	 */
	@Override
	public void save(EmployeePasswordChangeHistory employeePasswordChangeHistory) {
		super.save(employeePasswordChangeHistory);
	}

	@Override
	public List<EmployeePasswordChangeHistory> getPreviousPasswords(
			Long employeeId, int maxResults) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeePasswordChangeHistory> criteriaQuery = cb
				.createQuery(EmployeePasswordChangeHistory.class);
		Root<EmployeePasswordChangeHistory> passTypeRoot = criteriaQuery
				.from(EmployeePasswordChangeHistory.class);

		criteriaQuery.select(passTypeRoot);

		Join<EmployeePasswordChangeHistory, Employee> employeeJoin = passTypeRoot
				.join(EmployeePasswordChangeHistory_.employee);

		Path<Long> employeeID = employeeJoin.get(Employee_.employeeId);

		criteriaQuery.where(cb.equal(employeeID, employeeId));
		criteriaQuery.orderBy(cb.desc(passTypeRoot
				.get(EmployeePasswordChangeHistory_.empPwdChangeHistoryId)));
		TypedQuery<EmployeePasswordChangeHistory> passTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		passTypedQuery.setMaxResults(maxResults);
		List<EmployeePasswordChangeHistory> passwordList = new ArrayList<>();
		if (maxResults != 0) {
			passwordList = passTypedQuery.getResultList();
		}

		return passwordList;

	}

	@Override
	public EmployeePasswordChangeHistory getPreviousPasswords(Long employeeId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeePasswordChangeHistory> criteriaQuery = cb
				.createQuery(EmployeePasswordChangeHistory.class);
		Root<EmployeePasswordChangeHistory> passTypeRoot = criteriaQuery
				.from(EmployeePasswordChangeHistory.class);

		criteriaQuery.select(passTypeRoot);

		Join<EmployeePasswordChangeHistory, Employee> employeeJoin = passTypeRoot
				.join(EmployeePasswordChangeHistory_.employee);

		Path<Long> employeeID = employeeJoin.get(Employee_.employeeId);

		criteriaQuery.where(cb.equal(employeeID, employeeId));
		criteriaQuery.orderBy(cb.desc(passTypeRoot
				.get(EmployeePasswordChangeHistory_.empPwdChangeHistoryId)));
		TypedQuery<EmployeePasswordChangeHistory> passTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		List<EmployeePasswordChangeHistory> passwordList = passTypedQuery
				.getResultList();
		if (passwordList != null &&  !passwordList.isEmpty()) {
			return passwordList.get(0);
		}
		return null;

	}
}
