package com.payasia.dao.impl;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.payasia.dao.BaseDAO;
import com.payasia.dao.CoherentOTEmployeeListDAO;
import com.payasia.dao.bean.CoherentOTEmployeeList;
import com.payasia.dao.bean.CoherentOTEmployeeList_;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.Employee_;

@Repository
public class CoherentOTEmployeeListDAOImpl extends BaseDAO implements
		CoherentOTEmployeeListDAO {

	@Override
	protected Object getBaseEntity() {
		CoherentOTEmployeeList coherentOTEmployeeList = new CoherentOTEmployeeList();
		return coherentOTEmployeeList;
	}

	@Override
	public List<CoherentOTEmployeeList> findAll() {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CoherentOTEmployeeList> criteriaQuery = cb
				.createQuery(CoherentOTEmployeeList.class);
		Root<CoherentOTEmployeeList> coherentOTEmployeeListRoot = criteriaQuery
				.from(CoherentOTEmployeeList.class);
		criteriaQuery.select(coherentOTEmployeeListRoot);
		TypedQuery<CoherentOTEmployeeList> coherentOTEmployeeListTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<CoherentOTEmployeeList> allCurrencyMasterList = coherentOTEmployeeListTypedQuery
				.getResultList();
		return allCurrencyMasterList;
	}

	@Override
	public void save(CoherentOTEmployeeList coherentOTEmployeeList) {
		super.save(coherentOTEmployeeList);
	}

	@Override
	public CoherentOTEmployeeList findById(Long employeeId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CoherentOTEmployeeList> criteriaQuery = cb
				.createQuery(CoherentOTEmployeeList.class);
		Root<CoherentOTEmployeeList> coherentOTEmployeeListRoot = criteriaQuery
				.from(CoherentOTEmployeeList.class);
		criteriaQuery.select(coherentOTEmployeeListRoot);

		Join<CoherentOTEmployeeList, Employee> employeeJoin = coherentOTEmployeeListRoot
				.join(CoherentOTEmployeeList_.employee);
		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(coherentOTEmployeeListRoot
				.get(CoherentOTEmployeeList_.employee), employeeId));
		criteriaQuery.where(restriction);

		TypedQuery<CoherentOTEmployeeList> coherentOTEmployeeListTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		CoherentOTEmployeeList coherentOTEmployeeList = null;
		try {
			coherentOTEmployeeList = coherentOTEmployeeListTypedQuery
					.getSingleResult();
		} catch (NoResultException ex) {

		}
		return coherentOTEmployeeList;
	}

	@Override
	public void delete(CoherentOTEmployeeList coherentOTEmployeeList) {
		super.delete(coherentOTEmployeeList);

	}

	@Override
	public List<CoherentOTEmployeeList> findByCondition(String searchCondition,
			String searchText, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CoherentOTEmployeeList> criteriaQuery = cb
				.createQuery(CoherentOTEmployeeList.class);
		Root<CoherentOTEmployeeList> root = criteriaQuery
				.from(CoherentOTEmployeeList.class);
		criteriaQuery.select(root);

		Predicate restriction = cb.conjunction();

		Join<CoherentOTEmployeeList, Employee> empLeaveJoin = root
				.join(CoherentOTEmployeeList_.employee);
		Join<CoherentOTEmployeeList, Company> companyJoin = root
				.join(CoherentOTEmployeeList_.company);

		restriction = cb.and(
				restriction,
				cb.equal(
						root.get(CoherentOTEmployeeList_.company).get(
								Company_.companyId), companyId));

		if (StringUtils.isNotBlank(searchCondition)
				&& StringUtils.isNotBlank(searchText)) {

			if (searchCondition.equalsIgnoreCase("empno")) {
				restriction = cb.and(restriction, cb.or(cb.like(
						root.get(CoherentOTEmployeeList_.employee).get(
								Employee_.employeeNumber),
						'%' + searchText + '%')));

			} else if (searchCondition.equalsIgnoreCase("firstName")) {
				restriction = cb.and(restriction, cb.or(cb.like(
						root.get(CoherentOTEmployeeList_.employee).get(
								Employee_.firstName), '%' + searchText + '%')));

			} else if (searchCondition.equalsIgnoreCase("lastName")) {
				restriction = cb.and(restriction, cb.or(cb.like(
						root.get(CoherentOTEmployeeList_.employee).get(
								Employee_.lastName), '%' + searchText + '%')));

			}

		}

		criteriaQuery.where(restriction);

		TypedQuery<CoherentOTEmployeeList> claimAppTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return claimAppTypedQuery.getResultList();
	}

	@Override
	public void deleteCoherentOTEmployee(long employeeId) {
		String queryString = "DELETE FROM CoherentOTEmployeeList caw WHERE caw.employee.employeeId = :employeeId ";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("employeeId", employeeId);
		q.executeUpdate();
	}

}
