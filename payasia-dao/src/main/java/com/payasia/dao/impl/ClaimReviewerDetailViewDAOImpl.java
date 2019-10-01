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

import com.payasia.common.dto.ClaimReviewerConditionDTO;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.SortConstants;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.ClaimReviewerDetailViewDAO;
import com.payasia.dao.bean.ClaimReviewerDetailView;
import com.payasia.dao.bean.ClaimReviewerDetailViewPK;
import com.payasia.dao.bean.ClaimReviewerDetailViewPK_;
import com.payasia.dao.bean.ClaimReviewerDetailView_;

@Repository
public class ClaimReviewerDetailViewDAOImpl extends BaseDAO implements
		ClaimReviewerDetailViewDAO {

	@Override
	protected Object getBaseEntity() {
		ClaimReviewerDetailView claimReviewerDetailView = new ClaimReviewerDetailView();
		return claimReviewerDetailView;
	}

	@Override
	public List<ClaimReviewerDetailView> findByCondition(
			ClaimReviewerConditionDTO conditionDTO, PageRequest pageDTO,
			SortCondition sortDTO, Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ClaimReviewerDetailView> criteriaQuery = cb
				.createQuery(ClaimReviewerDetailView.class);
		Root<ClaimReviewerDetailView> claimReviewerRoot = criteriaQuery
				.from(ClaimReviewerDetailView.class);
		criteriaQuery.select(claimReviewerRoot);

		Join<ClaimReviewerDetailView, ClaimReviewerDetailViewPK> claimReviewerPKJoin = claimReviewerRoot
				.join(ClaimReviewerDetailView_.claimReviewerDetailViewPK);

		Predicate restriction = cb.conjunction();

		if (StringUtils.isNotBlank(conditionDTO.getClaimTemplateName())) {
			restriction = cb.and(restriction, cb.like(cb
					.upper(claimReviewerRoot
							.get(ClaimReviewerDetailView_.claimTemplateName)),
					conditionDTO.getClaimTemplateName().toUpperCase()));
		}
		if (StringUtils.isNotBlank(conditionDTO.getEmployeeName())) {
			restriction = cb.and(restriction, cb.or(cb.like(cb
					.upper(claimReviewerRoot
							.get(ClaimReviewerDetailView_.empFirstName)),
					conditionDTO.getEmployeeName().toUpperCase()), cb.like(cb
					.upper(claimReviewerRoot
							.get(ClaimReviewerDetailView_.empLastName)),
					conditionDTO.getEmployeeName().toUpperCase())));
		}
		if (StringUtils.isNotBlank(conditionDTO.getLeaveReviewer1())) {
			restriction = cb.and(restriction, cb.or(cb.like(cb
					.upper(claimReviewerRoot
							.get(ClaimReviewerDetailView_.reviewer1FirstName)),
					conditionDTO.getLeaveReviewer1().toUpperCase()), cb.like(cb
					.upper(claimReviewerRoot
							.get(ClaimReviewerDetailView_.reviewer1LastName)),
					conditionDTO.getLeaveReviewer1().toUpperCase())));
		}
		if (StringUtils.isNotBlank(conditionDTO.getLeaveReviewer2())) {
			restriction = cb.and(restriction, cb.or(cb.like(cb
					.upper(claimReviewerRoot
							.get(ClaimReviewerDetailView_.reviewer2FirstName)),
					conditionDTO.getLeaveReviewer2().toUpperCase()), cb.like(cb
					.upper(claimReviewerRoot
							.get(ClaimReviewerDetailView_.reviewer2LastName)),
					conditionDTO.getLeaveReviewer2().toUpperCase())));
		}
		if (StringUtils.isNotBlank(conditionDTO.getLeaveReviewer3())) {
			restriction = cb.and(restriction, cb.or(cb.like(cb
					.upper(claimReviewerRoot
							.get(ClaimReviewerDetailView_.reviewer3FirstName)),
					conditionDTO.getLeaveReviewer3().toUpperCase()), cb.like(cb
					.upper(claimReviewerRoot
							.get(ClaimReviewerDetailView_.reviewer3LastName)),
					conditionDTO.getLeaveReviewer3().toUpperCase())));
		}

		restriction = cb.and(restriction, cb.equal(
				claimReviewerPKJoin.get(ClaimReviewerDetailViewPK_.companyId),
				companyId));

		criteriaQuery.where(restriction);

		if (sortDTO != null) {

			Path<String> sortPath = getSortPathForSearchEmployee(sortDTO,
					claimReviewerRoot);
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

		TypedQuery<ClaimReviewerDetailView> claimReviewerTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			claimReviewerTypedQuery.setFirstResult(getStartPosition(pageDTO));
			claimReviewerTypedQuery.setMaxResults(pageDTO.getPageSize());
		}
		return claimReviewerTypedQuery.getResultList();
	}

	@Override
	public Path<String> getSortPathForSearchEmployee(SortCondition sortDTO,
			Root<ClaimReviewerDetailView> claimReviewerRoot) {

		List<String> employeeIsColList = new ArrayList<String>();
		employeeIsColList.add(SortConstants.CLAIM_REVIEWER_EMPLOYEE_NAME);
		employeeIsColList.add(SortConstants.CLAIM_REVIEWER_LEAVE_REVIEWER1);
		employeeIsColList.add(SortConstants.CLAIM_REVIEWER_LEAVE_REVIEWER2);
		employeeIsColList.add(SortConstants.CLAIM_REVIEWER_LEAVE_REVIEWER3);
		employeeIsColList.add(SortConstants.CLAIM_REVIEWER_LEAVE_SCHEME);

		Path<String> sortPath = null;

		if (employeeIsColList.contains(sortDTO.getColumnName())) {
			sortPath = claimReviewerRoot.get(colMap
					.get(ClaimReviewerDetailView.class
							+ sortDTO.getColumnName()));
		}
		return sortPath;
	}

	@Override
	public Long getCountByCondition(ClaimReviewerConditionDTO conditionDTO,
			Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
		Root<ClaimReviewerDetailView> claimReviewerRoot = criteriaQuery
				.from(ClaimReviewerDetailView.class);
		criteriaQuery.select(cb.count(claimReviewerRoot));

		Join<ClaimReviewerDetailView, ClaimReviewerDetailViewPK> claimReviewerPKJoin = claimReviewerRoot
				.join(ClaimReviewerDetailView_.claimReviewerDetailViewPK);

		Predicate restriction = cb.conjunction();

		if (StringUtils.isNotBlank(conditionDTO.getClaimTemplateName())) {
			restriction = cb.and(restriction, cb.like(cb
					.upper(claimReviewerRoot
							.get(ClaimReviewerDetailView_.claimTemplateName)),
					conditionDTO.getClaimTemplateName().toUpperCase()));
		}
		if (StringUtils.isNotBlank(conditionDTO.getEmployeeName())) {
			restriction = cb.and(restriction, cb.or(cb.like(cb
					.upper(claimReviewerRoot
							.get(ClaimReviewerDetailView_.empFirstName)),
					conditionDTO.getEmployeeName().toUpperCase()), cb.like(cb
					.upper(claimReviewerRoot
							.get(ClaimReviewerDetailView_.empLastName)),
					conditionDTO.getEmployeeName().toUpperCase())));
		}
		if (StringUtils.isNotBlank(conditionDTO.getLeaveReviewer1())) {
			restriction = cb.and(restriction, cb.or(cb.like(cb
					.upper(claimReviewerRoot
							.get(ClaimReviewerDetailView_.reviewer1FirstName)),
					conditionDTO.getLeaveReviewer1().toUpperCase()), cb.like(cb
					.upper(claimReviewerRoot
							.get(ClaimReviewerDetailView_.reviewer1LastName)),
					conditionDTO.getLeaveReviewer1().toUpperCase())));
		}
		if (StringUtils.isNotBlank(conditionDTO.getLeaveReviewer2())) {
			restriction = cb.and(restriction, cb.or(cb.like(cb
					.upper(claimReviewerRoot
							.get(ClaimReviewerDetailView_.reviewer2FirstName)),
					conditionDTO.getLeaveReviewer2().toUpperCase()), cb.like(cb
					.upper(claimReviewerRoot
							.get(ClaimReviewerDetailView_.reviewer2LastName)),
					conditionDTO.getLeaveReviewer2().toUpperCase())));
		}
		if (StringUtils.isNotBlank(conditionDTO.getLeaveReviewer3())) {
			restriction = cb.and(restriction, cb.or(cb.like(cb
					.upper(claimReviewerRoot
							.get(ClaimReviewerDetailView_.reviewer3FirstName)),
					conditionDTO.getLeaveReviewer3().toUpperCase()), cb.like(cb
					.upper(claimReviewerRoot
							.get(ClaimReviewerDetailView_.reviewer3LastName)),
					conditionDTO.getLeaveReviewer3().toUpperCase())));
		}

		restriction = cb.and(restriction, cb.equal(
				claimReviewerPKJoin.get(ClaimReviewerDetailViewPK_.companyId),
				companyId));

		criteriaQuery.where(restriction);

		TypedQuery<Long> claimReviewerTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		return claimReviewerTypedQuery.getSingleResult();
	}

}
