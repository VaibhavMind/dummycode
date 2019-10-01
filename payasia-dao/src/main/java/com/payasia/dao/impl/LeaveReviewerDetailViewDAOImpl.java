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

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.payasia.common.dto.LeaveReviewerConditionDTO;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.SortConstants;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.LeaveReviewerDetailViewDAO;
import com.payasia.dao.bean.LeaveReviewerDetailView;
import com.payasia.dao.bean.LeaveReviewerDetailViewPK;
import com.payasia.dao.bean.LeaveReviewerDetailViewPK_;
import com.payasia.dao.bean.LeaveReviewerDetailView_;

@Repository
public class LeaveReviewerDetailViewDAOImpl extends BaseDAO implements
		LeaveReviewerDetailViewDAO {

	@Override
	protected Object getBaseEntity() {
		LeaveReviewerDetailView leaveReviewerDetailView = new LeaveReviewerDetailView();
		return leaveReviewerDetailView;
	}

	@Override
	public List<LeaveReviewerDetailView> findByCondition(
			LeaveReviewerConditionDTO conditionDTO, PageRequest pageDTO,
			SortCondition sortDTO, Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveReviewerDetailView> criteriaQuery = cb
				.createQuery(LeaveReviewerDetailView.class);
		Root<LeaveReviewerDetailView> employeeLeaveReviewerRoot = criteriaQuery
				.from(LeaveReviewerDetailView.class);
		criteriaQuery.select(employeeLeaveReviewerRoot);

		Join<LeaveReviewerDetailView, LeaveReviewerDetailViewPK> leaveReviewerPKJoin = employeeLeaveReviewerRoot
				.join(LeaveReviewerDetailView_.leaveReviewerDetailViewPK);

		Predicate restriction = cb.conjunction();

		if (StringUtils.isNotBlank(conditionDTO.getLeaveSchemeName())) {
			restriction = cb.and(restriction, cb.like(cb
					.upper(employeeLeaveReviewerRoot
							.get(LeaveReviewerDetailView_.leaveSchemeName)),
					conditionDTO.getLeaveSchemeName().toUpperCase()));
		}
		if (StringUtils.isNotBlank(conditionDTO.getLeaveType())) {
			restriction = cb.and(restriction, cb.like(cb
					.upper(employeeLeaveReviewerRoot
							.get(LeaveReviewerDetailView_.leaveTypeName)),
					conditionDTO.getLeaveType().toUpperCase()));
		}
		if (StringUtils.isNotBlank(conditionDTO.getEmployeeName())) {
			restriction = cb
					.and(restriction,
							cb.or(cb.like(
									cb.upper(employeeLeaveReviewerRoot
											.get(LeaveReviewerDetailView_.empFirstName)),
									conditionDTO.getEmployeeName()
											.toUpperCase() + '%'),
									cb.like(cb
											.upper(employeeLeaveReviewerRoot
													.get(LeaveReviewerDetailView_.empLastName)),
											conditionDTO.getEmployeeName()
													.toUpperCase() + '%')));
		}
		if (StringUtils.isNotBlank(conditionDTO.getEmployeeNumber())) {
			restriction = cb.and(restriction, cb.like(employeeLeaveReviewerRoot
					.get(LeaveReviewerDetailView_.empEmployeeNumber),
					conditionDTO.getEmployeeNumber() + "%"));
		}
		if (StringUtils.isNotBlank(conditionDTO.getLeaveReviewer1())) {
			restriction = cb.and(restriction, cb.or(cb.like(cb
					.upper(employeeLeaveReviewerRoot
							.get(LeaveReviewerDetailView_.reviewer1FirstName)),
					conditionDTO.getLeaveReviewer1().toUpperCase()), cb.like(cb
					.upper(employeeLeaveReviewerRoot
							.get(LeaveReviewerDetailView_.reviewer1LastName)),
					conditionDTO.getLeaveReviewer1().toUpperCase())));
		}
		if (StringUtils.isNotBlank(conditionDTO.getLeaveReviewer2())) {
			restriction = cb.and(restriction, cb.or(cb.like(cb
					.upper(employeeLeaveReviewerRoot
							.get(LeaveReviewerDetailView_.reviewer2FirstName)),
					conditionDTO.getLeaveReviewer2().toUpperCase()), cb.like(cb
					.upper(employeeLeaveReviewerRoot
							.get(LeaveReviewerDetailView_.reviewer2LastName)),
					conditionDTO.getLeaveReviewer2().toUpperCase())));
		}
		if (StringUtils.isNotBlank(conditionDTO.getLeaveReviewer3())) {
			restriction = cb.and(restriction, cb.or(cb.like(cb
					.upper(employeeLeaveReviewerRoot
							.get(LeaveReviewerDetailView_.reviewer3FirstName)),
					conditionDTO.getLeaveReviewer3().toUpperCase()), cb.like(cb
					.upper(employeeLeaveReviewerRoot
							.get(LeaveReviewerDetailView_.reviewer3LastName)),
					conditionDTO.getLeaveReviewer3().toUpperCase())));
		}

		restriction = cb.and(restriction, cb.equal(
				leaveReviewerPKJoin.get(LeaveReviewerDetailViewPK_.companyId),
				companyId));

		criteriaQuery.where(restriction);

		if (sortDTO != null) {

			Path<String> sortPath = getSortPathForSearchEmployee(sortDTO,
					employeeLeaveReviewerRoot);
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

		TypedQuery<LeaveReviewerDetailView> employeeLeaveReviewerTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			employeeLeaveReviewerTypedQuery
					.setFirstResult(getStartPosition(pageDTO));
			employeeLeaveReviewerTypedQuery
					.setMaxResults(pageDTO.getPageSize());
		}
		return employeeLeaveReviewerTypedQuery.getResultList();
	}

	@Override
	public Path<String> getSortPathForSearchEmployee(SortCondition sortDTO,
			Root<LeaveReviewerDetailView> employeeLeaveReviewerRoot) {

		List<String> employeeIsColList = new ArrayList<String>();
		employeeIsColList.add(SortConstants.LEAVE_REVIEWER_EMPLOYEE_NAME);
		employeeIsColList.add(SortConstants.LEAVE_REVIEWER_LEAVE_REVIEWER1);
		employeeIsColList.add(SortConstants.LEAVE_REVIEWER_LEAVE_REVIEWER2);
		employeeIsColList.add(SortConstants.LEAVE_REVIEWER_LEAVE_REVIEWER3);
		employeeIsColList.add(SortConstants.LEAVE_REVIEWER_LEAVE_SCHEME);
		employeeIsColList.add(SortConstants.LEAVE_REVIEWER_LEAVE_TYPE);

		Path<String> sortPath = null;

		if (employeeIsColList.contains(sortDTO.getColumnName())) {
			sortPath = employeeLeaveReviewerRoot.get(colMap
					.get(LeaveReviewerDetailView.class
							+ sortDTO.getColumnName()));
		}
		return sortPath;
	}

	@Override
	public Long getCountByCondition(LeaveReviewerConditionDTO conditionDTO,
			Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
		Root<LeaveReviewerDetailView> employeeLeaveReviewerRoot = criteriaQuery
				.from(LeaveReviewerDetailView.class);
		criteriaQuery.select(cb.count(employeeLeaveReviewerRoot));

		Join<LeaveReviewerDetailView, LeaveReviewerDetailViewPK> leaveReviewerPKJoin = employeeLeaveReviewerRoot
				.join(LeaveReviewerDetailView_.leaveReviewerDetailViewPK);

		Predicate restriction = cb.conjunction();

		if (StringUtils.isNotBlank(conditionDTO.getLeaveSchemeName())) {
			restriction = cb.and(restriction, cb.like(cb
					.upper(employeeLeaveReviewerRoot
							.get(LeaveReviewerDetailView_.leaveSchemeName)),
					conditionDTO.getLeaveSchemeName().toUpperCase()));
		}
		if (StringUtils.isNotBlank(conditionDTO.getLeaveType())) {
			restriction = cb.and(restriction, cb.like(cb
					.upper(employeeLeaveReviewerRoot
							.get(LeaveReviewerDetailView_.leaveTypeName)),
					conditionDTO.getLeaveType().toUpperCase()));
		}
		if (StringUtils.isNotBlank(conditionDTO.getEmployeeName())) {
			restriction = cb.and(restriction, cb.or(cb.like(cb
					.upper(employeeLeaveReviewerRoot
							.get(LeaveReviewerDetailView_.empFirstName)),
					conditionDTO.getEmployeeName().toUpperCase()), cb.like(cb
					.upper(employeeLeaveReviewerRoot
							.get(LeaveReviewerDetailView_.empLastName)),
					conditionDTO.getEmployeeName().toUpperCase())));
		}
		if (StringUtils.isNotBlank(conditionDTO.getLeaveReviewer1())) {
			restriction = cb.and(restriction, cb.or(cb.like(cb
					.upper(employeeLeaveReviewerRoot
							.get(LeaveReviewerDetailView_.reviewer1FirstName)),
					conditionDTO.getLeaveReviewer1().toUpperCase()), cb.like(cb
					.upper(employeeLeaveReviewerRoot
							.get(LeaveReviewerDetailView_.reviewer1LastName)),
					conditionDTO.getLeaveReviewer1().toUpperCase())));
		}
		if (StringUtils.isNotBlank(conditionDTO.getEmployeeNumber())) {
			restriction = cb.and(restriction, cb.like(employeeLeaveReviewerRoot
					.get(LeaveReviewerDetailView_.empEmployeeNumber),
					conditionDTO.getEmployeeNumber() + "%"));
		}
		if (StringUtils.isNotBlank(conditionDTO.getLeaveReviewer2())) {
			restriction = cb.and(restriction, cb.or(cb.like(cb
					.upper(employeeLeaveReviewerRoot
							.get(LeaveReviewerDetailView_.reviewer2FirstName)),
					conditionDTO.getLeaveReviewer2().toUpperCase()), cb.like(cb
					.upper(employeeLeaveReviewerRoot
							.get(LeaveReviewerDetailView_.reviewer2LastName)),
					conditionDTO.getLeaveReviewer2().toUpperCase())));
		}
		if (StringUtils.isNotBlank(conditionDTO.getLeaveReviewer3())) {
			restriction = cb.and(restriction, cb.or(cb.like(cb
					.upper(employeeLeaveReviewerRoot
							.get(LeaveReviewerDetailView_.reviewer3FirstName)),
					conditionDTO.getLeaveReviewer3().toUpperCase()), cb.like(cb
					.upper(employeeLeaveReviewerRoot
							.get(LeaveReviewerDetailView_.reviewer3LastName)),
					conditionDTO.getLeaveReviewer3().toUpperCase())));
		}

		restriction = cb.and(restriction, cb.equal(
				leaveReviewerPKJoin.get(LeaveReviewerDetailViewPK_.companyId),
				companyId));

		criteriaQuery.where(restriction);

		TypedQuery<Long> employeeLeaveReviewerTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		return employeeLeaveReviewerTypedQuery.getSingleResult();
	}

}
