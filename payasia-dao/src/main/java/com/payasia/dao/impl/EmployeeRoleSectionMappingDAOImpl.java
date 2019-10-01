package com.payasia.dao.impl;

import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.payasia.dao.BaseDAO;
import com.payasia.dao.EmployeeRoleSectionMappingDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeRoleSectionMapping;
import com.payasia.dao.bean.EmployeeRoleSectionMapping_;
import com.payasia.dao.bean.Employee_;
import com.payasia.dao.bean.RoleMaster;
import com.payasia.dao.bean.RoleMaster_;

@Repository
public class EmployeeRoleSectionMappingDAOImpl extends BaseDAO implements
		EmployeeRoleSectionMappingDAO {

	@Override
	protected Object getBaseEntity() {
		EmployeeRoleSectionMapping employeeRoleSectionMapping = new EmployeeRoleSectionMapping();
		return employeeRoleSectionMapping;
	}

	@Override
	public void update(EmployeeRoleSectionMapping employeeRoleSectionMapping) {
		this.entityManagerFactory.merge(employeeRoleSectionMapping);
	}

	@Override
	public void delete(EmployeeRoleSectionMapping employeeRoleSectionMapping) {
		this.entityManagerFactory.remove(employeeRoleSectionMapping);
	}

	@Override
	public void save(EmployeeRoleSectionMapping employeeRoleSectionMapping) {
		super.save(employeeRoleSectionMapping);
	}

	@Override
	public EmployeeRoleSectionMapping findById(Long employeeRoleSectionMappingId) {
		return super.findById(EmployeeRoleSectionMapping.class,
				employeeRoleSectionMappingId);

	}

	@Override
	public void deleteByConditionRoleAndEmpId(long employeeId, long roleId) {

		String queryString = "DELETE FROM EmployeeRoleSectionMapping e WHERE e.employee.employeeId = :employeeId AND e.roleMaster.roleId = :roleId";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("employeeId", employeeId);
		q.setParameter("roleId", roleId);

		q.executeUpdate();

	}

	@Override
	public List<EmployeeRoleSectionMapping> findByCondition(Long roleId,
			Long employeeId, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeRoleSectionMapping> criteriaQuery = cb
				.createQuery(EmployeeRoleSectionMapping.class);
		Root<EmployeeRoleSectionMapping> root = criteriaQuery
				.from(EmployeeRoleSectionMapping.class);

		criteriaQuery.select(root);
		Join<EmployeeRoleSectionMapping, Employee> empRoleMappingEmployeeJoin = root
				.join(EmployeeRoleSectionMapping_.employee);

		Join<EmployeeRoleSectionMapping, RoleMaster> empRoleMappingRoleJoin = root
				.join(EmployeeRoleSectionMapping_.roleMaster);

		Join<EmployeeRoleSectionMapping, Company> empRoleMappingCompanyJoin = root
				.join(EmployeeRoleSectionMapping_.company);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(
				empRoleMappingRoleJoin.get(RoleMaster_.roleId), roleId));

		restriction = cb.and(restriction, cb.equal(
				empRoleMappingEmployeeJoin.get(Employee_.employeeId),
				employeeId));
		restriction = cb.and(restriction, cb.equal(
				empRoleMappingCompanyJoin.get(Company_.companyId), companyId));

		criteriaQuery.where(restriction);

		TypedQuery<EmployeeRoleSectionMapping> employeeTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		List<EmployeeRoleSectionMapping> employeeMappingList = employeeTypedQuery
				.getResultList();
		return employeeMappingList;
	}

	@Override
	public List<Long> findByCondition(Long employeeId, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
		Root<EmployeeRoleSectionMapping> root = criteriaQuery
				.from(EmployeeRoleSectionMapping.class);

		criteriaQuery.select(
				root.get(EmployeeRoleSectionMapping_.formId).as(Long.class))
				.distinct(true);
		Join<EmployeeRoleSectionMapping, Employee> empRoleMappingEmployeeJoin = root
				.join(EmployeeRoleSectionMapping_.employee);

		Join<EmployeeRoleSectionMapping, Company> empRoleMappingCompanyJoin = root
				.join(EmployeeRoleSectionMapping_.company);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(
				empRoleMappingEmployeeJoin.get(Employee_.employeeId),
				employeeId));
		restriction = cb.and(restriction, cb.equal(
				empRoleMappingCompanyJoin.get(Company_.companyId), companyId));

		criteriaQuery.where(restriction);

		TypedQuery<Long> employeeTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		List<Long> formList = employeeTypedQuery.getResultList();
		return formList;
	}

	@Override
	public void deleteByCondition(Long employeeId, Long roleId, Long companyId,
			Long sectionId) {

		String queryString = "DELETE FROM EmployeeRoleSectionMapping e WHERE e.company.companyId = :company AND e.roleMaster.roleId = :roleId AND e.employee.employeeId = :employeeId AND e.formId = :sectionId";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("company", companyId);
		q.setParameter("roleId", roleId);
		q.setParameter("employeeId", employeeId);
		q.setParameter("sectionId", sectionId);

		q.executeUpdate();

	}

	@Override
	public void deleteByCondition(Long employeeId, Long roleId, Long companyId) {

		String queryString = "DELETE FROM EmployeeRoleSectionMapping e WHERE e.company.companyId = :company AND e.roleMaster.roleId = :roleId AND e.employee.employeeId = :employeeId";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("company", companyId);
		q.setParameter("roleId", roleId);
		q.setParameter("employeeId", employeeId);

		q.executeUpdate();

	}

	@Override
	public void deleteByFormIdAndCompanyId(Long formId, Long companyId) {

		String queryString = "DELETE FROM EmployeeRoleSectionMapping e WHERE e.formId = :formId AND e.company.companyId = :companyId";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("formId", formId);
		q.setParameter("companyId", companyId);

		q.executeUpdate();

	}
}
