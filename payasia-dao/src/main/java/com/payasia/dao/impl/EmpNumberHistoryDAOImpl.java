/**
 * @author abhisheksachdeva
 *
 */
package com.payasia.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.SortConstants;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.EmpNumberHistoryDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.EmpNumberHistory;
import com.payasia.dao.bean.EmpNumberHistory_;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.Employee_;

/**
 * The Class EmpNumberHistoryDAOImpl.
 */
@Repository
@Component
public class EmpNumberHistoryDAOImpl extends BaseDAO implements
		EmpNumberHistoryDAO {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.BaseDAO#getBaseEntity()
	 */
	@Override
	protected Object getBaseEntity() {
		EmpNumberHistory empNumberHistory = new EmpNumberHistory();
		return empNumberHistory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.EmpNumberHistoryDAO#findAll()
	 */
	@Override
	public List<EmpNumberHistory> findAll(PageRequest pageDTO,
			SortCondition sortDTO, Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmpNumberHistory> criteriaQuery = cb
				.createQuery(EmpNumberHistory.class);
		Root<EmpNumberHistory> empNumberHistoryRoot = criteriaQuery
				.from(EmpNumberHistory.class);

		criteriaQuery.select(empNumberHistoryRoot);
		Join<EmpNumberHistory, Employee> empJoin = empNumberHistoryRoot
				.join(EmpNumberHistory_.employee1);
		Join<Employee, Company> empCompanyJoin = empJoin
				.join(Employee_.company);
		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction,
				cb.equal(empCompanyJoin.get(Company_.companyId), companyId));
		criteriaQuery.where(restriction);
		if (sortDTO != null) {

			Path<String> sortPath = getSortPathForEmpNumberHistory(sortDTO,
					empNumberHistoryRoot);
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

		 
		 

		TypedQuery<EmpNumberHistory> empNumberChangedTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			empNumberChangedTypedQuery
					.setFirstResult(getStartPosition(pageDTO));
			empNumberChangedTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		return empNumberChangedTypedQuery.getResultList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.EmpNumberHistoryDAO#getSortPathForEmpNumberHistory(com
	 * .payasia.common.form.SortCondition, javax.persistence.criteria.Root)
	 */
	@Override
	public Path<String> getSortPathForEmpNumberHistory(SortCondition sortDTO,
			Root<EmpNumberHistory> empNumberHistoryRoot) {

		List<String> empNumberHistoryIsIdList = new ArrayList<String>();
		empNumberHistoryIsIdList.add(SortConstants.EMPLOYEE_NUMBER_HISTORY_ID);

		List<String> empNumberHistoryEmployeeIsIdList = new ArrayList<String>();
		empNumberHistoryEmployeeIsIdList
				.add(SortConstants.EMPLOYEE_NUMBER_HISTORY_EMP_ID);

		List<String> empNumberHistoryChangeEmpIsIdList = new ArrayList<String>();
		empNumberHistoryChangeEmpIsIdList
				.add(SortConstants.EMPLOYEE_NUMBER_HISTORY_CHANGE_BY_EMP_ID);

		List<String> empNumberHistoryIsColList = new ArrayList<String>();
		empNumberHistoryIsColList
				.add(SortConstants.EMPLOYEE_NUMBER_HISTORY_OLD_EMP_NUMBER);
		empNumberHistoryIsColList
				.add(SortConstants.EMPLOYEE_NUMBER_HISTORY_CHANGE_TO);
		empNumberHistoryIsColList
				.add(SortConstants.EMPLOYEE_NUMBER_HISTORY_CHANGE_ON);
		empNumberHistoryIsColList
				.add(SortConstants.EMPLOYEE_NUMBER_HISTORY_CHANGED_BY);
		empNumberHistoryIsColList
				.add(SortConstants.EMPLOYEE_NUMBER_HISTORY_CHANGE_REASON);

		Path<String> sortPath = null;

		if (empNumberHistoryIsColList.contains(sortDTO.getColumnName())) {
			sortPath = empNumberHistoryRoot.get(colMap
					.get(EmpNumberHistory.class + sortDTO.getColumnName()));
		}
		return sortPath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.EmpNumberHistoryDAO#save(com.payasia.dao.bean.
	 * EmpNumberHistory)
	 */
	@Override
	public void save(EmpNumberHistory empNumberHistory) {
		super.save(empNumberHistory);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.EmpNumberHistoryDAO#getCountForAll()
	 */
	@Override
	public int getCountForAll(Long companyId) {

		return findAll(null, null, companyId).size();
	}

}
