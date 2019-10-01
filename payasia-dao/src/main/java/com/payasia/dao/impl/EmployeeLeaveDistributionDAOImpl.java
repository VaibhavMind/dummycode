package com.payasia.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.SortConstants;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.EmployeeLeaveDistributionDAO;
import com.payasia.dao.bean.EmployeeLeaveDistribution;
import com.payasia.dao.bean.EmployeeLeaveDistribution_;
import com.payasia.dao.bean.EmployeeLeaveScheme;
import com.payasia.dao.bean.EmployeeLeaveSchemeType;
import com.payasia.dao.bean.EmployeeLeaveSchemeType_;
import com.payasia.dao.bean.EmployeeLeaveScheme_;
import com.payasia.dao.bean.LeaveSchemeType;
import com.payasia.dao.bean.LeaveSchemeType_;
import com.payasia.dao.bean.LeaveTypeMaster;

@Repository
public class EmployeeLeaveDistributionDAOImpl extends BaseDAO implements
		EmployeeLeaveDistributionDAO {

	@Override
	protected Object getBaseEntity() {
		EmployeeLeaveDistribution employeeLeaveDistribution = new EmployeeLeaveDistribution();
		return employeeLeaveDistribution;
	}

	@Override
	public void save(EmployeeLeaveDistribution employeeLeaveDistribution) {
		super.save(employeeLeaveDistribution);
	}

	@Override
	public EmployeeLeaveDistribution findById(Long employeeLeaveDistributionId) {
		return super.findById(EmployeeLeaveDistribution.class,
				employeeLeaveDistributionId);
	}

	@Override
	public void update(EmployeeLeaveDistribution employeeLeaveDistribution) {
		super.update(employeeLeaveDistribution);
	}

	@Override
	public void delete(EmployeeLeaveDistribution employeeLeaveDistribution) {
		super.delete(employeeLeaveDistribution);
	}

	@Override
	public void deleteByCondition(Long employeeLeaveSchemeTypeId) {

		String queryString = "DELETE FROM EmployeeLeaveDistribution e WHERE e.employeeLeaveSchemeType.employeeLeaveSchemeTypeId = :employeeLeaveSchemeTypeId";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("employeeLeaveSchemeTypeId", employeeLeaveSchemeTypeId);

		q.executeUpdate();

	}

	@Override
	public List<EmployeeLeaveDistribution> findByCondition(PageRequest pageDTO,
			SortCondition sortDTO, Long empLeaveSchemeId, Integer year) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeLeaveDistribution> criteriaQuery = cb
				.createQuery(EmployeeLeaveDistribution.class);
		Root<EmployeeLeaveDistribution> empRoot = criteriaQuery
				.from(EmployeeLeaveDistribution.class);
		criteriaQuery.select(empRoot);

		Join<EmployeeLeaveDistribution, EmployeeLeaveSchemeType> empLeaveSchemeTypeJoin = empRoot
				.join(EmployeeLeaveDistribution_.employeeLeaveSchemeType);
		Join<EmployeeLeaveSchemeType, EmployeeLeaveScheme> empLeaveSchemeJoin = empLeaveSchemeTypeJoin
				.join(EmployeeLeaveSchemeType_.employeeLeaveScheme);

		Join<EmployeeLeaveSchemeType, LeaveSchemeType> leaveSchemeTypeJoin = empLeaveSchemeTypeJoin
				.join(EmployeeLeaveSchemeType_.leaveSchemeType);
		Join<LeaveSchemeType, LeaveTypeMaster> leaveTypeJoin = leaveSchemeTypeJoin
				.join(LeaveSchemeType_.leaveTypeMaster);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal((empLeaveSchemeJoin
				.get(EmployeeLeaveScheme_.employeeLeaveSchemeId)),
				empLeaveSchemeId));

		restriction = cb.and(restriction,
				cb.equal((empRoot.get(EmployeeLeaveDistribution_.year)), year));

		criteriaQuery.where(restriction);

		if (sortDTO != null) {

			Path<String> sortPath = getSortPathForEmpLeaveDistribution(sortDTO,
					empRoot, empLeaveSchemeJoin, leaveTypeJoin);
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

		TypedQuery<EmployeeLeaveDistribution> empTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			empTypedQuery.setFirstResult(getStartPosition(pageDTO));
			empTypedQuery.setMaxResults(pageDTO.getPageSize());
		}
		List<EmployeeLeaveDistribution> empLeaveSchemeList = empTypedQuery
				.getResultList();
		return empLeaveSchemeList;

	}

	@Override
	public Path<String> getSortPathForEmpLeaveDistribution(
			SortCondition sortDTO,
			Root<EmployeeLeaveDistribution> empRoot,
			Join<EmployeeLeaveSchemeType, EmployeeLeaveScheme> empLeaveSchemeJoin,
			Join<LeaveSchemeType, LeaveTypeMaster> leaveTypeJoin) {

		List<String> leaveDistIsColList = new ArrayList<String>();
		leaveDistIsColList
				.add(SortConstants.ASSIGN_LEAVE_SCHEME_LEAVE_DISTRIBUTION_YEAR);
		leaveDistIsColList
				.add(SortConstants.ASSIGN_LEAVE_SCHEME_LEAVE_DISTRIBUTION_JAN);
		leaveDistIsColList
				.add(SortConstants.ASSIGN_LEAVE_SCHEME_LEAVE_DISTRIBUTION_FEB);
		leaveDistIsColList
				.add(SortConstants.ASSIGN_LEAVE_SCHEME_LEAVE_DISTRIBUTION_MAR);
		leaveDistIsColList
				.add(SortConstants.ASSIGN_LEAVE_SCHEME_LEAVE_DISTRIBUTION_APR);
		leaveDistIsColList
				.add(SortConstants.ASSIGN_LEAVE_SCHEME_LEAVE_DISTRIBUTION_MAY);
		leaveDistIsColList
				.add(SortConstants.ASSIGN_LEAVE_SCHEME_LEAVE_DISTRIBUTION_JUNE);
		leaveDistIsColList
				.add(SortConstants.ASSIGN_LEAVE_SCHEME_LEAVE_DISTRIBUTION_JULY);
		leaveDistIsColList
				.add(SortConstants.ASSIGN_LEAVE_SCHEME_LEAVE_DISTRIBUTION_AUG);
		leaveDistIsColList
				.add(SortConstants.ASSIGN_LEAVE_SCHEME_LEAVE_DISTRIBUTION_SEPT);
		leaveDistIsColList
				.add(SortConstants.ASSIGN_LEAVE_SCHEME_LEAVE_DISTRIBUTION_OCT);
		leaveDistIsColList
				.add(SortConstants.ASSIGN_LEAVE_SCHEME_LEAVE_DISTRIBUTION_NOV);
		leaveDistIsColList
				.add(SortConstants.ASSIGN_LEAVE_SCHEME_LEAVE_DISTRIBUTION_DEC);

		List<String> leaveTypeIsColList = new ArrayList<String>();
		leaveTypeIsColList.add(SortConstants.ASSIGN_LEAVE_SCHEME_LEAVE_TYPE);

		Path<String> sortPath = null;

		if (leaveDistIsColList.contains(sortDTO.getColumnName())) {
			sortPath = empRoot.get(colMap.get(EmployeeLeaveDistribution.class
					+ sortDTO.getColumnName()));
		}

		if (leaveTypeIsColList.contains(sortDTO.getColumnName())) {
			sortPath = leaveTypeJoin.get(colMap.get(LeaveTypeMaster.class
					+ sortDTO.getColumnName()));
		}
		return sortPath;
	}

	@Override
	public Integer getCountForCondition(PageRequest pageDTO,
			SortCondition sortDTO, Long empLeaveSchemeId, Integer year) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Integer> criteriaQuery = cb.createQuery(Integer.class);
		Root<EmployeeLeaveDistribution> empRoot = criteriaQuery
				.from(EmployeeLeaveDistribution.class);
		criteriaQuery.select(cb.count(empRoot).as(Integer.class));

		Join<EmployeeLeaveDistribution, EmployeeLeaveSchemeType> empLeaveSchemeTypeJoin = empRoot
				.join(EmployeeLeaveDistribution_.employeeLeaveSchemeType);
		Join<EmployeeLeaveSchemeType, EmployeeLeaveScheme> empLeaveSchemeJoin = empLeaveSchemeTypeJoin
				.join(EmployeeLeaveSchemeType_.employeeLeaveScheme);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal((empLeaveSchemeJoin
				.get(EmployeeLeaveScheme_.employeeLeaveSchemeId)),
				empLeaveSchemeId));

		restriction = cb.and(restriction,
				cb.equal((empRoot.get(EmployeeLeaveDistribution_.year)), year));

		criteriaQuery.where(restriction);

		TypedQuery<Integer> empTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return empTypedQuery.getSingleResult();
	}

	@Override
	public EmployeeLeaveDistribution findByCondition(Long empLeaveSchemeTypeId,
			Integer year) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeLeaveDistribution> criteriaQuery = cb
				.createQuery(EmployeeLeaveDistribution.class);
		Root<EmployeeLeaveDistribution> empRoot = criteriaQuery
				.from(EmployeeLeaveDistribution.class);
		criteriaQuery.select(empRoot);

		Join<EmployeeLeaveDistribution, EmployeeLeaveSchemeType> empLeaveSchemeTypeJoin = empRoot
				.join(EmployeeLeaveDistribution_.employeeLeaveSchemeType);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal((empLeaveSchemeTypeJoin
				.get(EmployeeLeaveSchemeType_.employeeLeaveSchemeTypeId)),
				empLeaveSchemeTypeId));

		restriction = cb.and(restriction,
				cb.equal((empRoot.get(EmployeeLeaveDistribution_.year)), year));

		criteriaQuery.where(restriction);

		TypedQuery<EmployeeLeaveDistribution> empTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		List<EmployeeLeaveDistribution> empLeaveSchemeList = empTypedQuery
				.getResultList();
		if (empLeaveSchemeList != null && !empLeaveSchemeList.isEmpty()) {
			return empLeaveSchemeList.get(0);
		}
		return null;
	}
}
