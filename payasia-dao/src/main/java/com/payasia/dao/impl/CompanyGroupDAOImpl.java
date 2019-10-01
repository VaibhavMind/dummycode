package com.payasia.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.SortConstants;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.CompanyGroupDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.CompanyGroup;
import com.payasia.dao.bean.CompanyGroup_;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.Employee_;

/**
 * The Class CompanyGroupDAOImpl.
 */
@Repository
public class CompanyGroupDAOImpl extends BaseDAO implements CompanyGroupDAO {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.CompanyGroupDAO#findAll()
	 */
	@Override
	public List<CompanyGroup> findAll() {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CompanyGroup> criteriaQuery = cb
				.createQuery(CompanyGroup.class);
		Root<CompanyGroup> companyGroupRoot = criteriaQuery
				.from(CompanyGroup.class);

		criteriaQuery.select(companyGroupRoot);

		criteriaQuery.orderBy(cb.asc(companyGroupRoot
				.get(CompanyGroup_.groupId)));

		TypedQuery<CompanyGroup> companyGroupTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<CompanyGroup> allCompanyGroupList = companyGroupTypedQuery
				.getResultList();
		return allCompanyGroupList;
	}

	@Override
	public List<CompanyGroup> findAllYEP() {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CompanyGroup> criteriaQuery = cb
				.createQuery(CompanyGroup.class);
		Root<CompanyGroup> companyGroupRoot = criteriaQuery
				.from(CompanyGroup.class);
		companyGroupRoot.fetch(CompanyGroup_.companies, JoinType.LEFT);

		criteriaQuery.select(companyGroupRoot);

		criteriaQuery.orderBy(cb.asc(companyGroupRoot
				.get(CompanyGroup_.groupId)));

		TypedQuery<CompanyGroup> companyGroupTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<CompanyGroup> allCompanyGroupList = companyGroupTypedQuery
				.getResultList();
		return allCompanyGroupList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.BaseDAO#getBaseEntity()
	 */
	@Override
	protected Object getBaseEntity() {
		CompanyGroup companyGroup = new CompanyGroup();
		return companyGroup;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.CompanyGroupDAO#findById(long)
	 */
	@Override
	public CompanyGroup findById(long groupId) {

		return super.findById(CompanyGroup.class, groupId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.CompanyGroupDAO#findByCode(java.lang.String)
	 */
	@Override
	public CompanyGroup findByCode(String groupCode) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CompanyGroup> criteriaQuery = cb
				.createQuery(CompanyGroup.class);
		Root<CompanyGroup> companyGroupRoot = criteriaQuery
				.from(CompanyGroup.class);

		criteriaQuery.select(companyGroupRoot);
		criteriaQuery.where(cb.equal(
				cb.upper(companyGroupRoot.get(CompanyGroup_.groupCode)),
				groupCode.toUpperCase()));
		TypedQuery<CompanyGroup> companyGroupTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<CompanyGroup> companyGroupList = companyGroupTypedQuery
				.getResultList();
		if (companyGroupList != null && !companyGroupList.isEmpty()) {
			return companyGroupList.get(0);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.CompanyGroupDAO#getCompanyGroups(com.payasia.common.form
	 * .PageRequest, com.payasia.common.form.SortCondition)
	 */
	@Override
	public List<CompanyGroup> getCompanyGroups(PageRequest pageDTO,
			SortCondition sortDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CompanyGroup> criteriaQuery = cb
				.createQuery(CompanyGroup.class);

		Root<CompanyGroup> claimCatRoot = criteriaQuery
				.from(CompanyGroup.class);

		criteriaQuery.select(claimCatRoot);

		if (sortDTO != null) {

			Path<String> sortPath = getSortPathForCompanyCategory(sortDTO,
					claimCatRoot);
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

		TypedQuery<CompanyGroup> companyGroupQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			companyGroupQuery.setFirstResult(getStartPosition(pageDTO));
			companyGroupQuery.setMaxResults(pageDTO.getPageSize());
		}

		List<CompanyGroup> companyGroupList = companyGroupQuery.getResultList();

		return companyGroupList;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.CompanyGroupDAO#getSortPathForCompanyCategory(com.payasia
	 * .common.form.SortCondition, javax.persistence.criteria.Root)
	 */
	@Override
	public Path<String> getSortPathForCompanyCategory(SortCondition sortDTO,
			Root<CompanyGroup> companyGroupRoot) {
		List<String> companyGroupList = new ArrayList<String>();

		companyGroupList.add(SortConstants.GROUP_NAME);
		companyGroupList.add(SortConstants.GROUP_DESC);
		companyGroupList.add(SortConstants.GROUP_CODE);

		Path<String> sortPath = null;

		return sortPath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.CompanyGroupDAO#save(com.payasia.dao.bean.CompanyGroup)
	 */
	@Override
	public void save(CompanyGroup companyGroup) {
		super.save(companyGroup);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.CompanyGroupDAO#update(com.payasia.dao.bean.CompanyGroup)
	 */
	@Override
	public void update(CompanyGroup companyGroup) {
		super.update(companyGroup);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.CompanyGroupDAO#delete(com.payasia.dao.bean.CompanyGroup)
	 */
	@Override
	public void delete(CompanyGroup companyGroup) {
		super.delete(companyGroup);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.CompanyGroupDAO#findByGroupCodeGroupId(java.lang.String,
	 * java.lang.Long)
	 */
	@Override
	public CompanyGroup findByGroupCodeGroupId(String groupCode, Long groupId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CompanyGroup> criteriaQuery = cb
				.createQuery(CompanyGroup.class);
		Root<CompanyGroup> companyGroupRoot = criteriaQuery
				.from(CompanyGroup.class);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(
				cb.upper(companyGroupRoot.get(CompanyGroup_.groupCode)),
				groupCode.toUpperCase()));

		criteriaQuery.select(companyGroupRoot);

		if (groupId != null) {

			restriction = cb.and(restriction, cb.notEqual(
					companyGroupRoot.get(CompanyGroup_.groupId), groupId));

		}

		criteriaQuery.where(restriction);
		TypedQuery<CompanyGroup> companyGroupTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<CompanyGroup> companyGroupList = companyGroupTypedQuery
				.getResultList();
		if (companyGroupList != null && !companyGroupList.isEmpty()) {
			return companyGroupList.get(0);
		}
		return null;
	}

	@Override
	public CompanyGroup findByGroupName(String groupName) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CompanyGroup> criteriaQuery = cb
				.createQuery(CompanyGroup.class);
		Root<CompanyGroup> companyGroupRoot = criteriaQuery
				.from(CompanyGroup.class);

		Predicate restriction = cb.conjunction();

		criteriaQuery.select(companyGroupRoot);

		if (groupName != null) {

			restriction = cb.equal(
					companyGroupRoot.get(CompanyGroup_.groupName), groupName);

		}

		criteriaQuery.where(restriction);
		TypedQuery<CompanyGroup> companyGroupTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<CompanyGroup> companyGroupList = companyGroupTypedQuery
				.getResultList();
		if (companyGroupList != null && !companyGroupList.isEmpty()) {
			return companyGroupList.get(0);
		}
		return null;
	}

	@Override
	public CompanyGroup findByCompanyId(Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CompanyGroup> criteriaQuery = cb
				.createQuery(CompanyGroup.class);
		Root<CompanyGroup> companyGroupRoot = criteriaQuery
				.from(CompanyGroup.class);

		criteriaQuery.select(companyGroupRoot);

		Join<CompanyGroup, Company> companyGroupJoin = companyGroupRoot
				.join(CompanyGroup_.companies);
		criteriaQuery.where(cb.equal(companyGroupJoin.get(Company_.companyId),
				companyId));
		TypedQuery<CompanyGroup> companyGroupTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<CompanyGroup> companyGroupList = companyGroupTypedQuery
				.getResultList();
		if (companyGroupList != null && !companyGroupList.isEmpty()) {
			return companyGroupList.get(0);
		}
		return null;
	}

	@Override
	public Company getEmployeeByCompany(Long employeeId, Long companyId) {
		
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Company> criteriaQuery = cb
				.createQuery(Company.class);
		Root<Company> companyGroupRoot = criteriaQuery
				.from(Company.class);

		criteriaQuery.select(companyGroupRoot);

		Join<Company, Employee> companyEmpJoin = companyGroupRoot
				.join(Company_.employees);
		
		criteriaQuery.where(cb.equal(companyGroupRoot.get(Company_.companyId),
				companyId));
		
		criteriaQuery.where(cb.equal(companyEmpJoin.get(Employee_.employeeId),
				employeeId));
		
		TypedQuery<Company> companyGroupTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<Company> companyGroupList = companyGroupTypedQuery
				.getResultList();
		if (companyGroupList != null && !companyGroupList.isEmpty()) {
			return companyGroupList.get(0);
		}
		return null;
	}

}
