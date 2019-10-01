package com.payasia.dao.impl;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.payasia.dao.BaseDAO;
import com.payasia.dao.EmployeeDefaultEmailCCDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeDefaultEmailCC;
import com.payasia.dao.bean.EmployeeDefaultEmailCC_;
import com.payasia.dao.bean.Employee_;
import com.payasia.dao.bean.ModuleMaster;
import com.payasia.dao.bean.ModuleMaster_;

@Repository
public class EmployeeDefaultEmailCCDAOImpl extends BaseDAO implements
		EmployeeDefaultEmailCCDAO {

	private static final Logger LOGGER = Logger
			.getLogger(EmployeeDefaultEmailCCDAOImpl.class);

	@Override
	protected Object getBaseEntity() {
		EmployeeDefaultEmailCC employeeDefaultEmailCC = new EmployeeDefaultEmailCC();
		return employeeDefaultEmailCC;
	}

	@Override
	public void save(EmployeeDefaultEmailCC employeeDefaultEmailCC) {
		super.save(employeeDefaultEmailCC);
	}

	@Override
	public void delete(EmployeeDefaultEmailCC employeeDefaultEmailCC) {
		super.delete(employeeDefaultEmailCC);
	}

	@Override
	public void update(EmployeeDefaultEmailCC employeeDefaultEmailCC) {
		super.update(employeeDefaultEmailCC);

	}

	@Override
	public EmployeeDefaultEmailCC findByCondition(Long companyId,
			Long employeeId, Long moduleId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeDefaultEmailCC> criteriaQuery = cb
				.createQuery(EmployeeDefaultEmailCC.class);
		Root<EmployeeDefaultEmailCC> root = criteriaQuery
				.from(EmployeeDefaultEmailCC.class);
		criteriaQuery.select(root);
		Join<EmployeeDefaultEmailCC, Company> companyJoin = root
				.join(EmployeeDefaultEmailCC_.company);
		Join<EmployeeDefaultEmailCC, Employee> employeeJoin = root
				.join(EmployeeDefaultEmailCC_.employee);
		Join<EmployeeDefaultEmailCC, ModuleMaster> moduleJoin = root
				.join(EmployeeDefaultEmailCC_.moduleMaster);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction,
				cb.equal(companyJoin.get(Company_.companyId), companyId));
		restriction = cb.and(restriction,
				cb.equal(employeeJoin.get(Employee_.employeeId), employeeId));
		restriction = cb.and(restriction,
				cb.equal(moduleJoin.get(ModuleMaster_.moduleId), moduleId));

		criteriaQuery.where(restriction);
		TypedQuery<EmployeeDefaultEmailCC> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		try {
			return typedQuery.getSingleResult();
		} catch (Exception ex) {
			return null;
		}
	}

}
