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

import com.payasia.common.dto.OTReviewerConditionDTO;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.SortConstants;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.LundinOTReviewerDetailViewDAO;
import com.payasia.dao.bean.LundinOTReviewerDetailView;
import com.payasia.dao.bean.LundinOTReviewerDetailViewPK;
import com.payasia.dao.bean.LundinOTReviewerDetailViewPK_;
import com.payasia.dao.bean.LundinOTReviewerDetailView_;

@Repository
public class LundinOTReviewerDetailViewDAOImpl extends BaseDAO implements
		LundinOTReviewerDetailViewDAO {

	@Override
	protected Object getBaseEntity() {
		LundinOTReviewerDetailView lundinOTReviewerDetailView = new LundinOTReviewerDetailView();
		return lundinOTReviewerDetailView;
	}

	@Override
	public List<LundinOTReviewerDetailView> findByCondition(
			OTReviewerConditionDTO conditionDTO, PageRequest pageDTO,
			SortCondition sortDTO, Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LundinOTReviewerDetailView> criteriaQuery = cb
				.createQuery(LundinOTReviewerDetailView.class);
		Root<LundinOTReviewerDetailView> root = criteriaQuery
				.from(LundinOTReviewerDetailView.class);
		criteriaQuery.select(root);

		Join<LundinOTReviewerDetailView, LundinOTReviewerDetailViewPK> reviewerPKJoin = root
				.join(LundinOTReviewerDetailView_.lundinOTReviewerDetailViewPK);

		Predicate restriction = cb.conjunction();

		if (StringUtils.isNotBlank(conditionDTO.getEmployeeName())) {
			restriction = cb
					.and(restriction,
							cb.or(cb.like(
									cb.upper(root
											.get(LundinOTReviewerDetailView_.empFirstName)),
									'%' + conditionDTO.getEmployeeName()
											.toUpperCase() + '%'),
									cb.like(cb
											.upper(root
													.get(LundinOTReviewerDetailView_.empLastName)),
											'%' + conditionDTO
													.getEmployeeName()
													.toUpperCase() + '%')));
		}
		if (StringUtils.isNotBlank(conditionDTO.getEmployeeNumber())) {
			restriction = cb.and(restriction, cb.like(
					root.get(LundinOTReviewerDetailView_.empEmployeeNumber),
					'%' + conditionDTO.getEmployeeNumber() + '%'));
		}
		if (StringUtils.isNotBlank(conditionDTO.getEmail())) {
			restriction = cb.and(restriction, cb.like(
					root.get(LundinOTReviewerDetailView_.email),
					'%' + conditionDTO.getEmail() + '%'));
		}

		if (StringUtils.isNotBlank(conditionDTO.getOtReviewer1())) {
			restriction = cb
					.and(restriction,
							cb.or(cb.like(
									cb.upper(root
											.get(LundinOTReviewerDetailView_.reviewer1FirstName)),
									'%' + conditionDTO.getOtReviewer1()
											.toUpperCase() + '%'),
									cb.like(cb
											.upper(root
													.get(LundinOTReviewerDetailView_.reviewer1LastName)),
											'%' + conditionDTO.getOtReviewer1()
													.toUpperCase() + '%')));
		}
		if (StringUtils.isNotBlank(conditionDTO.getOtReviewer2())) {
			restriction = cb
					.and(restriction,
							cb.or(cb.like(
									cb.upper(root
											.get(LundinOTReviewerDetailView_.reviewer2FirstName)),
									'%' + conditionDTO.getOtReviewer2()
											.toUpperCase() + '%'),
									cb.like(cb
											.upper(root
													.get(LundinOTReviewerDetailView_.reviewer2LastName)),
											'%' + conditionDTO.getOtReviewer2()
													.toUpperCase() + '%')));
		}
		if (StringUtils.isNotBlank(conditionDTO.getOtReviewer3())) {
			restriction = cb
					.and(restriction,
							cb.or(cb.like(
									cb.upper(root
											.get(LundinOTReviewerDetailView_.reviewer3FirstName)),
									'%' + conditionDTO.getOtReviewer3()
											.toUpperCase() + '%'),
									cb.like(cb
											.upper(root
													.get(LundinOTReviewerDetailView_.reviewer3LastName)),
											'%' + conditionDTO.getOtReviewer3()
													.toUpperCase() + '%')));
		}

		restriction = cb.and(restriction, cb.equal(
				reviewerPKJoin.get(LundinOTReviewerDetailViewPK_.companyId),
				companyId));

		criteriaQuery.where(restriction);

		if (sortDTO != null) {

			Path<String> sortPath = getSortPathForSearchEmployee(sortDTO, root);
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

		TypedQuery<LundinOTReviewerDetailView> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			typedQuery.setFirstResult(getStartPosition(pageDTO));
			typedQuery.setMaxResults(pageDTO.getPageSize());
		}
		List<LundinOTReviewerDetailView> detailViews = typedQuery
				.getResultList();
		return detailViews;
	}

	public Path<String> getSortPathForSearchEmployee(SortCondition sortDTO,
			Root<LundinOTReviewerDetailView> root) {

		List<String> employeeIsColList = new ArrayList<String>();
		employeeIsColList.add(SortConstants.LEAVE_REVIEWER_EMPLOYEE_NAME);
		employeeIsColList.add(SortConstants.EMPLOYEE_NUMBER);
		employeeIsColList.add(SortConstants.LUNDIN_REVIEWER1);
		employeeIsColList.add(SortConstants.LUNDIN_REVIEWER2);
		employeeIsColList.add(SortConstants.LUNDIN_REVIEWER3);

		Path<String> sortPath = null;

		if (employeeIsColList.contains(sortDTO.getColumnName())) {
			sortPath = root.get(colMap.get(LundinOTReviewerDetailView.class
					+ sortDTO.getColumnName()));
		}
		return sortPath;
	}

	@Override
	public int getCountByCondition(OTReviewerConditionDTO conditionDTO,
			Long companyId) {
		return findByCondition(conditionDTO, null, null, companyId).size();
	}

}
