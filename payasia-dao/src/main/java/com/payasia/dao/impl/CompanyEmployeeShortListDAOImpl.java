package com.payasia.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import org.springframework.stereotype.Repository;

import com.payasia.dao.BaseDAO;
import com.payasia.dao.CompanyEmployeeShortListDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.CompanyEmployeeShortList;
import com.payasia.dao.bean.CompanyEmployeeShortList_;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeRoleMapping;
import com.payasia.dao.bean.EmployeeRoleMapping_;
import com.payasia.dao.bean.Employee_;
import com.payasia.dao.bean.RoleMaster;
import com.payasia.dao.bean.RoleMaster_;

/**
 * The Class CompanyEmployeeShortListDAOImpl.
 */
/**
 * @author vivekjain
 * 
 */
@Repository
public class CompanyEmployeeShortListDAOImpl extends BaseDAO implements
		CompanyEmployeeShortListDAO {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.BaseDAO#getBaseEntity()
	 */
	@Override
	protected Object getBaseEntity() {
		CompanyEmployeeShortList companyEmployeeShortList = new CompanyEmployeeShortList();
		return companyEmployeeShortList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.CompanyEmployeeShortListDAO#findByCondition(java.lang
	 * .Long, java.lang.Long, java.lang.Long)
	 */
	@Override
	public List<CompanyEmployeeShortList> findByCondition(Long employeeId,
			Long roleId, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CompanyEmployeeShortList> criteriaQuery = cb
				.createQuery(CompanyEmployeeShortList.class);
		Root<CompanyEmployeeShortList> employeeRoot = criteriaQuery
				.from(CompanyEmployeeShortList.class);

		criteriaQuery.select(employeeRoot);
		Join<CompanyEmployeeShortList, EmployeeRoleMapping> empRoleMappingEmployeeJoin = employeeRoot
				.join(CompanyEmployeeShortList_.employeeRoleMapping);

		Join<EmployeeRoleMapping, RoleMaster> empRoleMasterJoin = empRoleMappingEmployeeJoin
				.join(EmployeeRoleMapping_.roleMaster);

		Join<EmployeeRoleMapping, Employee> empMasterEmployeeJoin = empRoleMappingEmployeeJoin
				.join(EmployeeRoleMapping_.employee);

		Join<EmployeeRoleMapping, Company> empCompanyJoin = empRoleMappingEmployeeJoin
				.join(EmployeeRoleMapping_.company);

		Predicate restriction = cb.conjunction();

		if (roleId != null) {
			restriction = cb
					.and(restriction, cb.equal(
							empRoleMasterJoin.get(RoleMaster_.roleId), roleId));
		}

		if (employeeId != null) {
			
			restriction = cb.and(restriction,
					cb.equal(empMasterEmployeeJoin.get(Employee_.employeeId),
							employeeId));
		}

		if (companyId != null) {
			restriction = cb
					.and(restriction, cb.equal(
							empCompanyJoin.get(Company_.companyId), companyId));
		}

		criteriaQuery.where(restriction);

		criteriaQuery.orderBy(cb.asc(employeeRoot
				.get(CompanyEmployeeShortList_.shortListId)));

		TypedQuery<CompanyEmployeeShortList> employeeTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		List<CompanyEmployeeShortList> companyEmployeeShortList = employeeTypedQuery
				.getResultList();
		return companyEmployeeShortList;

	}

	@Override
	public List<Tuple> getCompanyEmpShortlistCount(Long employeeId, Long roleId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Tuple> criteriaQuery = cb.createQuery(Tuple.class);
		Root<CompanyEmployeeShortList> employeeRoot = criteriaQuery
				.from(CompanyEmployeeShortList.class);

		Join<CompanyEmployeeShortList, EmployeeRoleMapping> empRoleMappingEmployeeJoin = employeeRoot
				.join(CompanyEmployeeShortList_.employeeRoleMapping);

		Join<EmployeeRoleMapping, RoleMaster> empRoleMasterJoin = empRoleMappingEmployeeJoin
				.join(EmployeeRoleMapping_.roleMaster);

		Join<EmployeeRoleMapping, Employee> empMasterEmployeeJoin = empRoleMappingEmployeeJoin
				.join(EmployeeRoleMapping_.employee);

		Join<EmployeeRoleMapping, Company> empCompanyJoin = empRoleMappingEmployeeJoin
				.join(EmployeeRoleMapping_.company);

		List<Selection<?>> selectionItems = new ArrayList<Selection<?>>();

		// selectionItems.add(empRoleMasterJoin.get(RoleMaster_.roleId).alias(
		// getAlias(RoleMaster_.roleId)));
		// selectionItems.add(empMasterEmployeeJoin.get(Employee_.employeeId)
		// .alias(getAlias(Employee_.employeeId)));
		selectionItems.add(empCompanyJoin.get(Company_.companyId).alias(
				getAlias(Company_.companyId)));
		selectionItems.add(cb.count(employeeRoot).as(Integer.class)
				.alias("shortlistCount"));
		criteriaQuery.multiselect(selectionItems).distinct(true);

		Predicate restriction = cb.conjunction();

		if (roleId != null) {
			restriction = cb
					.and(restriction, cb.equal(
							empRoleMasterJoin.get(RoleMaster_.roleId), roleId));
		}

		if (employeeId != null) {
			restriction = cb.and(restriction,
					cb.equal(empMasterEmployeeJoin.get(Employee_.employeeId),
							employeeId));
		}

		// if (companyId != null) {
		// restriction = cb
		// .and(restriction, cb.equal(
		// empCompanyJoin.get(Company_.companyId), companyId));
		// }

		criteriaQuery.where(restriction);

		criteriaQuery.groupBy(empCompanyJoin.get(Company_.companyId));

		TypedQuery<Tuple> employeeTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return employeeTypedQuery.getResultList();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.CompanyEmployeeShortListDAO#findById(long)
	 */
	@Override
	public CompanyEmployeeShortList findById(long companyEmployeeShortListId) {

		return super.findById(CompanyEmployeeShortList.class,
				companyEmployeeShortListId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.CompanyEmployeeShortListDAO#update(com.payasia.dao.bean
	 * .CompanyEmployeeShortList)
	 */
	@Override
	public void update(CompanyEmployeeShortList companyEmployeeShortListId) {

		super.update(companyEmployeeShortListId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.CompanyEmployeeShortListDAO#delete(com.payasia.dao.bean
	 * .CompanyEmployeeShortList)
	 */
	@Override
	public void delete(CompanyEmployeeShortList companyEmployeeShortList) {
		super.delete(companyEmployeeShortList);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.CompanyEmployeeShortListDAO#deleteByCondition(java.lang
	 * .Long, java.lang.Long, java.lang.Long)
	 */
	@Override
	public void deleteByCondition(Long employeeId, Long roleId, Long companyId) {

		String queryString = "DELETE FROM CompanyEmployeeShortList c WHERE c.employeeRoleMapping.id.companyId = :company AND c.employeeRoleMapping.id.roleId = :roleId AND c.employeeRoleMapping.id.employeeId = :employeeId";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("company", companyId);
		q.setParameter("roleId", roleId);
		q.setParameter("employeeId", employeeId);

		q.executeUpdate();

	}

	@Override
	public void deleteByEmployeeAndRole(Long employeeId, Long roleId) {

		String queryString = "DELETE FROM CompanyEmployeeShortList c WHERE  c.employeeRoleMapping.id.roleId = :roleId AND c.employeeRoleMapping.id.employeeId = :employeeId";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("roleId", roleId);
		q.setParameter("employeeId", employeeId);

		q.executeUpdate();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.CompanyEmployeeShortListDAO#save(com.payasia.dao.bean
	 * .CompanyEmployeeShortList)
	 */
	@Override
	public void save(CompanyEmployeeShortList companyEmployeeShortList) {
		super.save(companyEmployeeShortList);

	}

}
