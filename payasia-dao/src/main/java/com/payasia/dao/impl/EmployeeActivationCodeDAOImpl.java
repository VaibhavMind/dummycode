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
import com.payasia.dao.EmployeeActivationCodeDAO;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeActivationCode;
import com.payasia.dao.bean.EmployeeActivationCode_;
import com.payasia.dao.bean.EmployeeMobileDetails;
import com.payasia.dao.bean.EmployeeMobileDetails_;
import com.payasia.dao.bean.Employee_;

@Repository
public class EmployeeActivationCodeDAOImpl extends BaseDAO implements
		EmployeeActivationCodeDAO {

	@Override
	protected Object getBaseEntity() {
		EmployeeActivationCode employeeActivationCode = new EmployeeActivationCode();
		return employeeActivationCode;
	}

	@Override
	public void update(EmployeeActivationCode employeeActivationCode) {
		super.update(employeeActivationCode);

	}

	@Override
	public void save(EmployeeActivationCode employeeActivationCode) {
		super.save(employeeActivationCode);
	}

	@Override
	public void delete(EmployeeActivationCode employeeActivationCode) {
		super.delete(employeeActivationCode);

	}

	@Override
	public EmployeeActivationCode findByID(long employeeActivationCodeId) {
		return super.findById(EmployeeActivationCode.class,
				employeeActivationCodeId);
	}

	@Override
	public EmployeeActivationCode findByActivationCode(String activationCode,
			String userName) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeActivationCode> criteriaQuery = cb
				.createQuery(EmployeeActivationCode.class);
		Root<EmployeeActivationCode> employeeActivationCodeRoot = criteriaQuery
				.from(EmployeeActivationCode.class);
		Join<EmployeeActivationCode, Employee> employeeJoin = employeeActivationCodeRoot
				.join(EmployeeActivationCode_.employee);

		criteriaQuery.select(employeeActivationCodeRoot);
		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(employeeActivationCodeRoot
				.get(EmployeeActivationCode_.activationCode), activationCode));

		restriction = cb.and(restriction, cb.equal(
				employeeActivationCodeRoot.get(EmployeeActivationCode_.active),
				false));

		restriction = cb.and(
				restriction,
				cb.or(cb.equal(
						employeeJoin.get(Employee_.employeeNumber).as(
								String.class), userName),
						cb.equal(
								employeeJoin.get(Employee_.email).as(
										String.class), userName)));

		criteriaQuery.where(restriction);

		TypedQuery<EmployeeActivationCode> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		if (typedQuery.getResultList().size() > 0) {
			EmployeeActivationCode employeeActivationCode = typedQuery
					.getSingleResult();

			return employeeActivationCode;
		}
		return null;

	}

	@Override
	public List<EmployeeActivationCode> findByEmployee(Long employeeId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeActivationCode> criteriaQuery = cb
				.createQuery(EmployeeActivationCode.class);
		Root<EmployeeActivationCode> employeeActivationCodeRoot = criteriaQuery
				.from(EmployeeActivationCode.class);
		Join<EmployeeActivationCode, Employee> employeeJoin = employeeActivationCodeRoot
				.join(EmployeeActivationCode_.employee);

		Join<EmployeeActivationCode, EmployeeMobileDetails> employeeMobileDetailsJoin = employeeActivationCodeRoot
				.join(EmployeeActivationCode_.employeeMobileDetails);

		criteriaQuery.select(employeeActivationCodeRoot);
		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(
				employeeActivationCodeRoot.get(EmployeeActivationCode_.active),
				true));

		restriction = cb.and(restriction, cb.equal(
				employeeJoin.get(Employee_.employeeId).as(Long.class),
				employeeId));

		restriction = cb.and(
				restriction,
				cb.equal(
						employeeMobileDetailsJoin.get(
								EmployeeMobileDetails_.deviceOS).as(
								String.class), "iOS"));

		criteriaQuery.where(restriction);

		TypedQuery<EmployeeActivationCode> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return typedQuery.getResultList();

	}

}
