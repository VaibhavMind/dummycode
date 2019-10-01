package com.payasia.dao.impl;

import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.payasia.dao.BaseDAO;
import com.payasia.dao.EmployeeLoginDetailDAO;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeLoginDetail;
import com.payasia.dao.bean.EmployeeLoginDetail_;
import com.payasia.dao.bean.Employee_;

@Repository
public class EmployeeLoginDetailDAOImpl extends BaseDAO implements
		EmployeeLoginDetailDAO {

	@Override
	protected Object getBaseEntity() {
		EmployeeLoginDetail employeeLoginDetail = new EmployeeLoginDetail();
		return employeeLoginDetail;
	}

	@Override
	public void update(EmployeeLoginDetail employeeLoginDetail) {
		super.update(employeeLoginDetail);

	}

	@Override
	public void save(EmployeeLoginDetail employeeLoginDetail) {
		super.save(employeeLoginDetail);
	}

	@Override
	public void delete(EmployeeLoginDetail employeeLoginDetail) {
		super.delete(employeeLoginDetail);

	}

	@Override
	public EmployeeLoginDetail findByID(long employeeLoginDetailId) {
		return super.findById(EmployeeLoginDetail.class, employeeLoginDetailId);
	}

	@Override
	public EmployeeLoginDetail findByEmployeeId(long employeeId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeLoginDetail> criteriaQuery = cb
				.createQuery(EmployeeLoginDetail.class);
		Root<EmployeeLoginDetail> empLoginDetailRoot = criteriaQuery
				.from(EmployeeLoginDetail.class);
		criteriaQuery.select(empLoginDetailRoot);

		Join<EmployeeLoginDetail, Employee> empJoin = empLoginDetailRoot
				.join(EmployeeLoginDetail_.employee);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(empJoin.get(Employee_.employeeId), employeeId));

		criteriaQuery.where(restriction);

		TypedQuery<EmployeeLoginDetail> empLoginDetailTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<EmployeeLoginDetail> employeeLoginList = empLoginDetailTypedQuery
				.getResultList();
		if (employeeLoginList != null &&  !employeeLoginList.isEmpty()) {
			return employeeLoginList.get(0);
		}
		return null;

	}

}
