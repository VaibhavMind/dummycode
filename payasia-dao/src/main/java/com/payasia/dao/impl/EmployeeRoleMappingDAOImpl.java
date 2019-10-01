package com.payasia.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.payasia.common.dto.EmployeeConditionDTO;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.SortConstants;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.EmployeeRoleMappingDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.CompanyGroup;
import com.payasia.dao.bean.CompanyGroup_;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeRoleMapping;
import com.payasia.dao.bean.EmployeeRoleMapping_;
import com.payasia.dao.bean.Employee_;
import com.payasia.dao.bean.RoleMaster;
import com.payasia.dao.bean.RoleMaster_;

/**
 * The Class EmployeeRoleMappingDAOImpl.
 */
/**
 * @author vivekjain
 * 
 */
@Repository
public class EmployeeRoleMappingDAOImpl extends BaseDAO implements
		EmployeeRoleMappingDAO {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.BaseDAO#getBaseEntity()
	 */
	@Override
	protected Object getBaseEntity() {
		EmployeeRoleMapping employeeRoleMapping = new EmployeeRoleMapping();
		return employeeRoleMapping;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.EmployeeRoleMappingDAO#save(com.payasia.dao.bean.
	 * EmployeeRoleMapping)
	 */
	@Override
	public void save(EmployeeRoleMapping employeeRoleMapping) {
		super.save(employeeRoleMapping);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.EmployeeRoleMappingDAO#newTranSave(com.payasia.dao.bean
	 * .EmployeeRoleMapping)
	 */
	@Override
	public void newTranSave(EmployeeRoleMapping employeeRoleMapping) {
		super.save(employeeRoleMapping);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.EmployeeRoleMappingDAO#update(com.payasia.dao.bean.
	 * EmployeeRoleMapping)
	 */
	@Override
	public void update(EmployeeRoleMapping employeeRoleMapping) {
		super.update(employeeRoleMapping);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.EmployeeRoleMappingDAO#delete(com.payasia.dao.bean.
	 * EmployeeRoleMapping)
	 */
	@Override
	public void delete(EmployeeRoleMapping employeeRoleMapping) {
		super.delete(employeeRoleMapping);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.EmployeeRoleMappingDAO#findByCondition(java.lang.Long,
	 * java.lang.Long, java.lang.Long)
	 */
	@Override
	public EmployeeRoleMapping findByCondition(Long roleId, Long companyId,
			Long employeeId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeRoleMapping> criteriaQuery = cb
				.createQuery(EmployeeRoleMapping.class);
		Root<EmployeeRoleMapping> EmployeeRoot = criteriaQuery
				.from(EmployeeRoleMapping.class);

		criteriaQuery.select(EmployeeRoot);
		Join<EmployeeRoleMapping, Employee> empRoleMappingEmployeeJoin = EmployeeRoot
				.join(EmployeeRoleMapping_.employee);

		Join<EmployeeRoleMapping, RoleMaster> empRoleMappingRoleJoin = EmployeeRoot
				.join(EmployeeRoleMapping_.roleMaster);

		Join<EmployeeRoleMapping, Company> empCompanyJoin = EmployeeRoot
				.join(EmployeeRoleMapping_.company);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(
				empRoleMappingRoleJoin.get(RoleMaster_.roleId), roleId));

		restriction = cb.and(restriction, cb.equal(
				empRoleMappingEmployeeJoin.get(Employee_.employeeId),
				employeeId));

		restriction = cb.and(restriction,
				cb.equal(empCompanyJoin.get(Company_.companyId), companyId));

		criteriaQuery.where(restriction);

		criteriaQuery
				.orderBy(cb.asc(EmployeeRoot.get(EmployeeRoleMapping_.id)));

		TypedQuery<EmployeeRoleMapping> employeeTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		List<EmployeeRoleMapping> employeeRoleMapList = employeeTypedQuery
				.getResultList();
		if (employeeRoleMapList != null && !employeeRoleMapList.isEmpty()) {
			return employeeRoleMapList.get(0);
		}
		return null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.EmployeeRoleMappingDAO#findByCondition(java.lang.Long)
	 */
	@Override
	public List<EmployeeRoleMapping> findByCondition(Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeRoleMapping> criteriaQuery = cb
				.createQuery(EmployeeRoleMapping.class);
		Root<EmployeeRoleMapping> EmployeeRoot = criteriaQuery
				.from(EmployeeRoleMapping.class);

		criteriaQuery.select(EmployeeRoot);

		Join<EmployeeRoleMapping, Company> empCompanyJoin = EmployeeRoot
				.join(EmployeeRoleMapping_.company);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction,
				cb.equal(empCompanyJoin.get(Company_.companyId), companyId));
		criteriaQuery.where(restriction);

		TypedQuery<EmployeeRoleMapping> employeeTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		List<EmployeeRoleMapping> employeeRoleMappingList = employeeTypedQuery
				.getResultList();
		return employeeRoleMappingList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.EmployeeRoleMappingDAO#findByConditionRoleAndEmpID(java
	 * .lang.Long, java.lang.Long)
	 */
	@Override
	public List<EmployeeRoleMapping> findByConditionRoleAndEmpID(Long roleId,
			Long employeeId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeRoleMapping> criteriaQuery = cb
				.createQuery(EmployeeRoleMapping.class);
		Root<EmployeeRoleMapping> EmployeeRoot = criteriaQuery
				.from(EmployeeRoleMapping.class);

		criteriaQuery.select(EmployeeRoot);
		Join<EmployeeRoleMapping, Employee> empRoleMappingEmployeeJoin = EmployeeRoot
				.join(EmployeeRoleMapping_.employee);

		Join<EmployeeRoleMapping, RoleMaster> empRoleMappingRoleJoin = EmployeeRoot
				.join(EmployeeRoleMapping_.roleMaster);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(
				empRoleMappingRoleJoin.get(RoleMaster_.roleId), roleId));

		restriction = cb.and(restriction, cb.equal(
				empRoleMappingEmployeeJoin.get(Employee_.employeeId),
				employeeId));

		criteriaQuery.where(restriction);

		TypedQuery<EmployeeRoleMapping> employeeTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		List<EmployeeRoleMapping> employeeRoleMappingList = employeeTypedQuery
				.getResultList();
		return employeeRoleMappingList;
	}

	@Override
	public List<EmployeeRoleMapping> findByConditionRoleAndEmpID(Long roleId,
			Long employeeId, boolean isPayasiaUserRoleOtherThanAdmin,
			Long payasiaCompanyGroupId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeRoleMapping> criteriaQuery = cb
				.createQuery(EmployeeRoleMapping.class);
		Root<EmployeeRoleMapping> EmployeeRoot = criteriaQuery
				.from(EmployeeRoleMapping.class);

		criteriaQuery.select(EmployeeRoot);
		Join<EmployeeRoleMapping, Employee> empRoleMappingEmployeeJoin = EmployeeRoot
				.join(EmployeeRoleMapping_.employee);

		Join<EmployeeRoleMapping, RoleMaster> empRoleMappingRoleJoin = EmployeeRoot
				.join(EmployeeRoleMapping_.roleMaster);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(
				empRoleMappingRoleJoin.get(RoleMaster_.roleId), roleId));

		restriction = cb.and(restriction, cb.equal(
				empRoleMappingEmployeeJoin.get(Employee_.employeeId),
				employeeId));

		if (isPayasiaUserRoleOtherThanAdmin) {
			Join<EmployeeRoleMapping, Company> empRoleMappingCompanyJoin = EmployeeRoot
					.join(EmployeeRoleMapping_.company);
			Join<Company, CompanyGroup> empRoleMappingCompanyGroupJoin = empRoleMappingCompanyJoin
					.join(Company_.companyGroup);
			restriction = cb.and(restriction, cb.notEqual(
					empRoleMappingCompanyGroupJoin.get(CompanyGroup_.groupId),
					payasiaCompanyGroupId));
		}

		criteriaQuery.where(restriction);

		TypedQuery<EmployeeRoleMapping> employeeTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		List<EmployeeRoleMapping> employeeRoleMappingList = employeeTypedQuery
				.getResultList();
		return employeeRoleMappingList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.EmployeeRoleMappingDAO#findByConditionRoleID(java.lang
	 * .Long)
	 */
	@Override
	public List<EmployeeRoleMapping> findByConditionRoleID(Long roleId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeRoleMapping> criteriaQuery = cb
				.createQuery(EmployeeRoleMapping.class);
		Root<EmployeeRoleMapping> EmployeeRoot = criteriaQuery
				.from(EmployeeRoleMapping.class);

		criteriaQuery.select(EmployeeRoot);

		Join<EmployeeRoleMapping, RoleMaster> empRoleMappingRoleJoin = EmployeeRoot
				.join(EmployeeRoleMapping_.roleMaster);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(
				empRoleMappingRoleJoin.get(RoleMaster_.roleId), roleId));

		criteriaQuery.where(restriction);

		TypedQuery<EmployeeRoleMapping> employeeTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		List<EmployeeRoleMapping> employeeRoleMappingList = employeeTypedQuery
				.getResultList();
		return employeeRoleMappingList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.EmployeeRoleMappingDAO#getSwitchCompanyList(java.lang
	 * .Long, com.payasia.common.form.PageRequest,
	 * com.payasia.common.form.SortCondition)
	 */
	@Override
	public List<EmployeeRoleMapping> getSwitchCompanyList(Long employeeId,
			PageRequest pageDTO, SortCondition sortDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeRoleMapping> criteriaQuery = cb
				.createQuery(EmployeeRoleMapping.class);
		Root<EmployeeRoleMapping> empRoleMappingRoot = criteriaQuery
				.from(EmployeeRoleMapping.class);

		Root<Company> companyRoot = criteriaQuery.from(Company.class);
		criteriaQuery.select(empRoleMappingRoot).distinct(true);

		Join<EmployeeRoleMapping, Employee> empRoleMappingEmployeeJoin = empRoleMappingRoot
				.join(EmployeeRoleMapping_.employee);

		empRoleMappingRoot.join(EmployeeRoleMapping_.company);

		Join<Company, CompanyGroup> groupCompanyJoin = companyRoot
				.join(Company_.companyGroup);
		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(
				empRoleMappingEmployeeJoin.get(Employee_.employeeId),
				employeeId));

		criteriaQuery.where(restriction);

		if (sortDTO != null) {

			Path<String> sortPath = getSortPathForCompanyAndCompanyGroup(
					sortDTO, companyRoot, groupCompanyJoin);
			if (sortPath != null) {
				if (SortConstants.DB_ORDER_BY_ASC.equalsIgnoreCase(sortDTO
						.getOrderType())) {
					criteriaQuery.orderBy(cb.asc(sortPath));
				}
				if (SortConstants.DB_ORDER_BY_DESC.equalsIgnoreCase(sortDTO
						.getOrderType())) {
					criteriaQuery.orderBy(cb.desc(sortPath));
				}
			}

		}

		TypedQuery<EmployeeRoleMapping> empRoleMappingTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			empRoleMappingTypedQuery.setFirstResult(getStartPosition(pageDTO));
			empRoleMappingTypedQuery.setMaxResults(pageDTO.getPageSize());
		}
		List<EmployeeRoleMapping> allEmpRoleMappingList = empRoleMappingTypedQuery
				.getResultList();
		return allEmpRoleMappingList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.EmployeeRoleMappingDAO#getSortPathForCompanyAndCompanyGroup
	 * (com.payasia.common.form.SortCondition, javax.persistence.criteria.Root,
	 * javax.persistence.criteria.Join)
	 */
	@Override
	public Path<String> getSortPathForCompanyAndCompanyGroup(
			SortCondition sortDTO, Root<Company> companyRoot,
			Join<Company, CompanyGroup> groupCompanyJoin) {

		List<String> companyIsColList = new ArrayList<String>();
		companyIsColList.add(SortConstants.MANAGE_ROLES_COMPANY_NAME);
		companyIsColList.add(SortConstants.MANAGE_ROLES_COMPANY_CODE);

		List<String> companyGroupIsColList = new ArrayList<String>();
		companyGroupIsColList.add(SortConstants.MANAGE_ROLES_GROUP_NAME);
		companyGroupIsColList.add(SortConstants.MANAGE_ROLES_GROUP_CODE);

		Path<String> sortPath = null;

		if (companyIsColList.contains(sortDTO.getColumnName())) {
			sortPath = companyRoot.get(colMap.get(Company.class
					+ sortDTO.getColumnName()));
		}
		if (companyGroupIsColList.contains(sortDTO.getColumnName())) {
			sortPath = groupCompanyJoin.get(colMap.get(CompanyGroup.class
					+ sortDTO.getColumnName()));
		}
		return sortPath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.EmployeeRoleMappingDAO#getCountForGetSwitchCompanyList
	 * (java.lang.Long)
	 */
	@Override
	public int getCountForGetSwitchCompanyList(Long employeeId) {
		return getSwitchCompanyList(employeeId, null, null).size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.EmployeeRoleMappingDAO#findByConditionEmpIdAndCompanyId
	 * (java.lang.Long, java.lang.Long)
	 */
	@Override
	public List<EmployeeRoleMapping> findByConditionEmpIdAndCompanyId(
			Long companyId, Long employeeId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeRoleMapping> criteriaQuery = cb
				.createQuery(EmployeeRoleMapping.class);
		Root<EmployeeRoleMapping> EmployeeRoot = criteriaQuery
				.from(EmployeeRoleMapping.class);

		criteriaQuery.select(EmployeeRoot);
		Join<EmployeeRoleMapping, Employee> empRoleMappingEmployeeJoin = EmployeeRoot
				.join(EmployeeRoleMapping_.employee);

		Join<EmployeeRoleMapping, Company> empCompanyJoin = EmployeeRoot
				.join(EmployeeRoleMapping_.company);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(
				empRoleMappingEmployeeJoin.get(Employee_.employeeId),
				employeeId));

		restriction = cb.and(restriction,
				cb.equal(empCompanyJoin.get(Company_.companyId), companyId));

		criteriaQuery.where(restriction);

		TypedQuery<EmployeeRoleMapping> employeeTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<EmployeeRoleMapping> employeeRoleMappingList = employeeTypedQuery
				.getResultList();
		return employeeRoleMappingList;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.EmployeeRoleMappingDAO#deleteByCondition(java.lang.Long,
	 * java.lang.Long, java.lang.Long)
	 */
	@Override
	public void deleteByCondition(Long employeeId, Long roleId, Long companyId) {

		String queryString = "DELETE FROM EmployeeRoleMapping e WHERE e.id.companyId = :company AND e.id.roleId = :roleId AND e.id.employeeId = :employeeId";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("company", companyId);
		q.setParameter("roleId", roleId);
		q.setParameter("employeeId", employeeId);

		q.executeUpdate();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.EmployeeRoleMappingDAO#deleteByConditionRoleAndEmpId(
	 * long, long)
	 */
	@Override
	public void deleteByConditionRoleAndEmpId(long employeeId, long roleId) {

		String queryString = "DELETE FROM EmployeeRoleMapping e WHERE e.id.employeeId = :employeeId AND e.id.roleId = :roleId";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("employeeId", employeeId);
		q.setParameter("roleId", roleId);

		q.executeUpdate();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.EmployeeRoleMappingDAO#deleteByCondition(long)
	 */
	@Override
	public void deleteByCondition(long employeeId,Long companyId) {

		String queryString = "DELETE FROM EmployeeRoleMapping e WHERE e.id.employeeId = :employeeId and  e.id.companyId = :companyId";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("employeeId", employeeId);
		q.setParameter("employeeId", companyId);
		q.executeUpdate();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.EmployeeRoleMappingDAO#deleteByConditionCompanyAndEmpId
	 * (long, long)
	 */
	@Override
	public void deleteByConditionCompanyAndEmpId(long employeeId, long companyId) {
		String queryString = "DELETE FROM EmployeeRoleMapping e WHERE e.id.companyId = :company AND e.id.employeeId = :employeeId";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("employeeId", employeeId);
		q.setParameter("company", companyId);
		q.executeUpdate();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.EmployeeRoleMappingDAO#deleteByCondition(java.lang.Long,
	 * java.lang.Long)
	 */
	@Override
	public void deleteByCondition(Long companyId, Long roleId) {
		String queryString = "DELETE FROM EmployeeRoleMapping e WHERE e.id.companyId = :company AND e.id.roleId = :roleId";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("company", companyId);
		q.setParameter("roleId", roleId);

		q.executeUpdate();

	}

	@Override
	public List<EmployeeRoleMapping> findByConditionRoleIdAndCompanyId(
			Long companyId, Long roleId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeRoleMapping> criteriaQuery = cb
				.createQuery(EmployeeRoleMapping.class);
		Root<EmployeeRoleMapping> EmployeeRoot = criteriaQuery
				.from(EmployeeRoleMapping.class);

		criteriaQuery.select(EmployeeRoot);
		Join<EmployeeRoleMapping, RoleMaster> empRoleMappingRoleJoin = EmployeeRoot
				.join(EmployeeRoleMapping_.roleMaster);

		Join<EmployeeRoleMapping, Company> empCompanyJoin = EmployeeRoot
				.join(EmployeeRoleMapping_.company);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(
				empRoleMappingRoleJoin.get(RoleMaster_.roleId), roleId));

		restriction = cb.and(restriction,
				cb.equal(empCompanyJoin.get(Company_.companyId), companyId));

		criteriaQuery.where(restriction);

		TypedQuery<EmployeeRoleMapping> employeeTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<EmployeeRoleMapping> employeeRoleMappingList = employeeTypedQuery
				.getResultList();
		return employeeRoleMappingList;

	}

	@Override
	public List<Tuple> findByConditionCompany(Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Tuple> criteriaQuery = cb.createQuery(Tuple.class);
		Root<EmployeeRoleMapping> EmployeeRoot = criteriaQuery
				.from(EmployeeRoleMapping.class);
		Join<EmployeeRoleMapping, Company> empCompanyJoin = EmployeeRoot
				.join(EmployeeRoleMapping_.company);
		Join<EmployeeRoleMapping, Employee> empJoin = EmployeeRoot
				.join(EmployeeRoleMapping_.employee);
		Join<Employee, Company> companyJoin = empJoin.join(Employee_.company);
		Join<EmployeeRoleMapping, RoleMaster> empRoleJoin = EmployeeRoot
				.join(EmployeeRoleMapping_.roleMaster);
		List<Selection<?>> selectionItems = new ArrayList<Selection<?>>();

		selectionItems.add(empJoin.get(Employee_.employeeId).alias(
				getAlias(Employee_.employeeId)));
		selectionItems.add(empJoin.get(Employee_.employeeNumber).alias(
				getAlias(Employee_.employeeNumber)));
		selectionItems.add(empJoin.get(Employee_.firstName).alias(
				getAlias(Employee_.firstName)));
		selectionItems.add(empJoin.get(Employee_.middleName).alias(
				getAlias(Employee_.middleName)));
		selectionItems.add(empJoin.get(Employee_.lastName).alias(
				getAlias(Employee_.lastName)));
		selectionItems.add(empJoin.get(Employee_.email).alias(
				getAlias(Employee_.email)));
		selectionItems.add(empRoleJoin.get(RoleMaster_.roleId).alias(
				getAlias(RoleMaster_.roleId)));

		criteriaQuery.multiselect(selectionItems).distinct(true);
		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction,
				cb.equal(empCompanyJoin.get(Company_.companyId), companyId));
		restriction = cb.and(restriction,
				cb.equal(companyJoin.get(Company_.companyId), companyId));
		restriction = cb.and(restriction,
				cb.equal(empJoin.get(Employee_.status), true));
		criteriaQuery.where(restriction);

		TypedQuery<Tuple> employeeTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		List<Tuple> employeeRoleMappingList = employeeTypedQuery
				.getResultList();
		return employeeRoleMappingList;
	}

	@Override
	public List<Tuple> findPayAsiaUsersByCond(
			EmployeeConditionDTO conditionDTO, Long payAsiaCompanyId,
			Long sessionCompanyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Tuple> criteriaQuery = cb.createQuery(Tuple.class);
		Root<EmployeeRoleMapping> EmployeeRoot = criteriaQuery
				.from(EmployeeRoleMapping.class);

		Join<EmployeeRoleMapping, Company> companyJoin = EmployeeRoot
				.join(EmployeeRoleMapping_.company);
		Join<EmployeeRoleMapping, Employee> empJoin = EmployeeRoot
				.join(EmployeeRoleMapping_.employee);
		Join<Employee, Company> empCompJoin = empJoin.join(Employee_.company);

		List<Selection<?>> selectionItems = new ArrayList<Selection<?>>();
		selectionItems.add(empJoin.get(Employee_.employeeId).alias(
				getAlias(Employee_.employeeId)));
		selectionItems.add(empJoin.get(Employee_.employeeNumber).alias(
				getAlias(Employee_.employeeNumber)));
		selectionItems.add(empJoin.get(Employee_.firstName).alias(
				getAlias(Employee_.firstName)));
		selectionItems.add(empJoin.get(Employee_.middleName).alias(
				getAlias(Employee_.middleName)));
		selectionItems.add(empJoin.get(Employee_.lastName).alias(
				getAlias(Employee_.lastName)));
		selectionItems.add(empJoin.get(Employee_.email).alias(
				getAlias(Employee_.email)));
		selectionItems.add(empCompJoin.get(Company_.companyName).alias(
				getAlias(Company_.companyName)));

		criteriaQuery.multiselect(selectionItems).distinct(true);

		Predicate restriction = cb.conjunction();

		if (StringUtils.isNotBlank(conditionDTO.getEmployeeNumber())) {

			restriction = cb.and(restriction, cb.like(
					cb.upper(empJoin.get(Employee_.employeeNumber)),
					conditionDTO.getEmployeeNumber().toUpperCase() + '%'));
		}

		if (StringUtils.isNotBlank(conditionDTO.getEmployeeName())) {

			restriction = cb.and(restriction, cb.or(cb.like(cb.upper(empJoin
					.get(Employee_.firstName)), conditionDTO.getEmployeeName()
					.toUpperCase() + '%'), cb.like(cb.upper(empJoin
					.get(Employee_.middleName)), conditionDTO.getEmployeeName()
					.toUpperCase() + '%'), cb.like(cb.upper(empJoin
					.get(Employee_.lastName)), conditionDTO.getEmployeeName()
					.toUpperCase() + '%')));
		}

		restriction = cb
				.and(restriction, cb.equal(empCompJoin.get(Company_.companyId),
						payAsiaCompanyId));
		restriction = cb
				.and(restriction, cb.equal(companyJoin.get(Company_.companyId),
						sessionCompanyId));
		restriction = cb.and(restriction,
				cb.equal(empJoin.get(Employee_.status), true));
		criteriaQuery.where(restriction);

		TypedQuery<Tuple> employeeTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		List<Tuple> employeeRoleMappingList = employeeTypedQuery
				.getResultList();
		return employeeRoleMappingList;
	}
	
	
	@Override
	public List<EmployeeRoleMapping> findByConditionEmpIdAndCompanyId(
			Long companyId, Long employeeId,Long roleId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeRoleMapping> criteriaQuery = cb
				.createQuery(EmployeeRoleMapping.class);
		Root<EmployeeRoleMapping> EmployeeRoot = criteriaQuery
				.from(EmployeeRoleMapping.class);

		criteriaQuery.select(EmployeeRoot);
		Join<EmployeeRoleMapping, Employee> empRoleMappingEmployeeJoin = EmployeeRoot
				.join(EmployeeRoleMapping_.employee);

		Join<EmployeeRoleMapping, Company> empCompanyJoin = EmployeeRoot
				.join(EmployeeRoleMapping_.company);
		
		Join<EmployeeRoleMapping, RoleMaster> empRoleJoin = EmployeeRoot
				.join(EmployeeRoleMapping_.roleMaster);


		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(
				empRoleMappingEmployeeJoin.get(Employee_.employeeId),
				employeeId));

		restriction = cb.and(restriction,
				cb.equal(empCompanyJoin.get(Company_.companyId), companyId));
		
		restriction = cb.and(restriction,
				cb.equal(empRoleJoin.get(RoleMaster_.roleId), roleId));
		

		criteriaQuery.where(restriction);

		TypedQuery<EmployeeRoleMapping> employeeTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<EmployeeRoleMapping> employeeRoleMappingList = employeeTypedQuery
				.getResultList();
		return employeeRoleMappingList;

	}


}
