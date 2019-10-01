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
import com.payasia.dao.EmpNoSeriesMasterDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.EmpNoSeriesMaster;
import com.payasia.dao.bean.EmpNoSeriesMaster_;

/**
 * The Class EmpNoSeriesMasterDAOImpl.
 */
/**
 * @author vivekjain
 * 
 */
@Repository
public class EmpNoSeriesMasterDAOImpl extends BaseDAO implements
		EmpNoSeriesMasterDAO {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.BaseDAO#getBaseEntity()
	 */
	@Override
	protected Object getBaseEntity() {
		EmpNoSeriesMaster empNoSeriesMaster = new EmpNoSeriesMaster();
		return empNoSeriesMaster;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.EmpNoSeriesMasterDAO#save(com.payasia.dao.bean.
	 * EmpNoSeriesMaster)
	 */
	@Override
	public void save(EmpNoSeriesMaster empNoSeriesMaster) {
		super.save(empNoSeriesMaster);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.EmpNoSeriesMasterDAO#findByConditionCompany(long,
	 * com.payasia.common.form.PageRequest,
	 * com.payasia.common.form.SortCondition)
	 */
	@Override
	public List<EmpNoSeriesMaster> findByConditionCompany(long companyId,
			PageRequest pageDTO, SortCondition sortDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmpNoSeriesMaster> criteriaQuery = cb
				.createQuery(EmpNoSeriesMaster.class);
		Root<EmpNoSeriesMaster> empNoSrRoot = criteriaQuery
				.from(EmpNoSeriesMaster.class);

		criteriaQuery.select(empNoSrRoot);

		Join<EmpNoSeriesMaster, Company> empNoSrRootRootJoin = empNoSrRoot
				.join(EmpNoSeriesMaster_.company);

		Path<Long> companyID = empNoSrRootRootJoin.get(Company_.companyId);

		criteriaQuery.where(cb.equal(companyID, companyId));

		if (sortDTO != null) {

			Path<String> sortPath = getSortPathForEmpNoSeries(sortDTO,
					empNoSrRoot, empNoSrRootRootJoin);
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

		TypedQuery<EmpNoSeriesMaster> empNoSrTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			empNoSrTypedQuery.setFirstResult(getStartPosition(pageDTO));
			empNoSrTypedQuery.setMaxResults(pageDTO.getPageSize());
		}
		List<EmpNoSeriesMaster> empNoSrMasterList = empNoSrTypedQuery
				.getResultList();

		return empNoSrMasterList;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.EmpNoSeriesMasterDAO#getSortPathForEmpNoSeries(com.payasia
	 * .common.form.SortCondition, javax.persistence.criteria.Root,
	 * javax.persistence.criteria.Join)
	 */
	@Override
	public Path<String> getSortPathForEmpNoSeries(SortCondition sortDTO,
			Root<EmpNoSeriesMaster> empNoSrRoot,
			Join<EmpNoSeriesMaster, Company> empNoSrRootRootJoin) {

		List<String> empNoSeriesIsColList = new ArrayList<String>();
		empNoSeriesIsColList.add(SortConstants.EMPLOYEE_SERIES_NUMBER);
		empNoSeriesIsColList
				.add(SortConstants.EMPLOYEE_SERIES_NUMBER_DESCRIPTION);
		empNoSeriesIsColList.add(SortConstants.EMPLOYEE_SERIES_NUMBER_FORMAT);
		empNoSeriesIsColList.add(SortConstants.EMPLOYEE_SERIES_NUMBER_ACTIVE);
		empNoSeriesIsColList.add(SortConstants.EMPLOYEE_SERIES_PREFIX);
		empNoSeriesIsColList.add(SortConstants.EMPLOYEE_SERIES_SUFFIX);

		Path<String> sortPath = null;

		if (empNoSeriesIsColList.contains(sortDTO.getColumnName())) {
			sortPath = empNoSrRoot.get(colMap.get(EmpNoSeriesMaster.class
					+ sortDTO.getColumnName()));
		}
		return sortPath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.EmpNoSeriesMasterDAO#delete(com.payasia.dao.bean.
	 * EmpNoSeriesMaster)
	 */
	@Override
	public void delete(EmpNoSeriesMaster empNoSeriesMaster) {
		super.delete(empNoSeriesMaster);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.EmpNoSeriesMasterDAO#findById(long)
	 */
	@Override
	public EmpNoSeriesMaster findById(long empNoSeriesId) {

		EmpNoSeriesMaster empNoSeriesMaster = super.findById(
				EmpNoSeriesMaster.class, empNoSeriesId);
		return empNoSeriesMaster;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.EmpNoSeriesMasterDAO#update(com.payasia.dao.bean.
	 * EmpNoSeriesMaster)
	 */
	@Override
	public void update(EmpNoSeriesMaster empNoSeriesMaster) {
		super.update(empNoSeriesMaster);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.EmpNoSeriesMasterDAO#getCountForEmpNoSeries(java.lang
	 * .Long)
	 */
	@Override
	public int getCountForEmpNoSeries(Long companyId) {
		return findByConditionCompany(companyId, null, null).size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.EmpNoSeriesMasterDAO#findActiveSeriesByCompany(long)
	 */
	@Override
	public List<EmpNoSeriesMaster> findActiveSeriesByCompany(long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmpNoSeriesMaster> criteriaQuery = cb
				.createQuery(EmpNoSeriesMaster.class);
		Root<EmpNoSeriesMaster> empNoSrRoot = criteriaQuery
				.from(EmpNoSeriesMaster.class);

		criteriaQuery.select(empNoSrRoot);

		Join<EmpNoSeriesMaster, Company> empNoSrRootRootJoin = empNoSrRoot
				.join(EmpNoSeriesMaster_.company);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(
				empNoSrRootRootJoin.get(Company_.companyId), companyId));
		restriction = cb.and(restriction,
				cb.equal(empNoSrRoot.get(EmpNoSeriesMaster_.active), true));
		criteriaQuery.where(restriction);

		TypedQuery<EmpNoSeriesMaster> empNoSrTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<EmpNoSeriesMaster> empNoSrMasterList = empNoSrTypedQuery
				.getResultList();

		return empNoSrMasterList;

	}
	
	@Override
	public EmpNoSeriesMaster findById(long empNoSeriesId,Long companyId)
	{
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmpNoSeriesMaster> criteriaQuery = cb.createQuery(EmpNoSeriesMaster.class);
		Root<EmpNoSeriesMaster> empRoot = criteriaQuery.from(EmpNoSeriesMaster.class);
		criteriaQuery.select(empRoot);

		Join<EmpNoSeriesMaster, Company> empRootCompanyJoin = empRoot.join(
				EmpNoSeriesMaster_.company, JoinType.INNER);

		Predicate restriction = cb.conjunction();

		restriction = cb
				.and(restriction, cb.equal(
						empRootCompanyJoin.get(Company_.companyId), companyId));
		restriction = cb.and(restriction, cb.equal(
				empRoot.get(EmpNoSeriesMaster_.empNoSeriesId),empNoSeriesId));

		criteriaQuery.where(restriction);

		TypedQuery<EmpNoSeriesMaster> empTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		List<EmpNoSeriesMaster> employeeList = empTypedQuery.getResultList();
		if (employeeList != null && !employeeList.isEmpty()) {
			return employeeList.get(0);
		}
		return null;
	}

}
