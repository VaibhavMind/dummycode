package com.payasia.dao.impl;

import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.payasia.dao.BaseDAO;
import com.payasia.dao.EmployeeTypeMasterDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.EmployeeTypeMaster;
import com.payasia.dao.bean.EmployeeTypeMaster_;

/**
 * The Class EmployeeTypeMasterDAOImpl.
 */
/**
 * @author vivekjain
 * 
 */
@Repository
public class EmployeeTypeMasterDAOImpl extends BaseDAO implements
		EmployeeTypeMasterDAO {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.EmployeeTypeMasterDAO#findByConditionCompany(long)
	 */
	@Override
	public List<EmployeeTypeMaster> findByConditionCompany(long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeTypeMaster> criteriaQuery = cb
				.createQuery(EmployeeTypeMaster.class);
		Root<EmployeeTypeMaster> empTypeRoot = criteriaQuery
				.from(EmployeeTypeMaster.class);

		criteriaQuery.select(empTypeRoot);

		Join<EmployeeTypeMaster, Company> empTypeRootJoin = empTypeRoot
				.join(EmployeeTypeMaster_.company);

		Path<Long> companyID = empTypeRootJoin.get(Company_.companyId);

		criteriaQuery.where(cb.equal(companyID, companyId));

		criteriaQuery.orderBy(cb.asc(empTypeRoot
				.get(EmployeeTypeMaster_.empTypeId)));

		TypedQuery<EmployeeTypeMaster> empTypeTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<EmployeeTypeMaster> empTypeMasterList = empTypeTypedQuery
				.getResultList();

		return empTypeMasterList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.BaseDAO#getBaseEntity()
	 */
	@Override
	protected Object getBaseEntity() {
		EmployeeTypeMaster employeeTypeMaster = new EmployeeTypeMaster();
		return employeeTypeMaster;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.EmployeeTypeMasterDAO#findById(long)
	 */
	@Override
	public EmployeeTypeMaster findById(long empTypeId) {

		EmployeeTypeMaster employeeTypeMaster = super.findById(
				EmployeeTypeMaster.class, empTypeId);

		return employeeTypeMaster;
	}

}
