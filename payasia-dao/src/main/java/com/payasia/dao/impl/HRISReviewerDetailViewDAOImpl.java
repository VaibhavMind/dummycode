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

import com.payasia.common.dto.HRISReviewerConditionDTO;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.SortConstants;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.HRISReviewerDetailViewDAO;
import com.payasia.dao.bean.HRISReviewerDetailView;
import com.payasia.dao.bean.HRISReviewerDetailViewPK;
import com.payasia.dao.bean.HRISReviewerDetailViewPK_;
import com.payasia.dao.bean.HRISReviewerDetailView_;

@Repository
public class HRISReviewerDetailViewDAOImpl extends BaseDAO implements
		HRISReviewerDetailViewDAO {

	@Override
	protected Object getBaseEntity() {
		HRISReviewerDetailView hrisReviewerDetailView = new HRISReviewerDetailView();
		return hrisReviewerDetailView;
	}

	@Override
	public List<HRISReviewerDetailView> findByCondition(
			HRISReviewerConditionDTO conditionDTO, PageRequest pageDTO,
			SortCondition sortDTO, Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<HRISReviewerDetailView> criteriaQuery = cb
				.createQuery(HRISReviewerDetailView.class);
		Root<HRISReviewerDetailView> root = criteriaQuery
				.from(HRISReviewerDetailView.class);
		criteriaQuery.select(root);

		Join<HRISReviewerDetailView, HRISReviewerDetailViewPK> hrisReviewerPKJoin = root
				.join(HRISReviewerDetailView_.hrisReviewerDetailViewPK);

		Predicate restriction = cb.conjunction();

		if (StringUtils.isNotBlank(conditionDTO.getEmployeeName())) {
			restriction = cb.and(restriction, cb.or(cb.like(
					cb.upper(root.get(HRISReviewerDetailView_.empFirstName)),
					conditionDTO.getEmployeeName().toUpperCase()), cb.like(
					cb.upper(root.get(HRISReviewerDetailView_.empLastName)),
					conditionDTO.getEmployeeName().toUpperCase())));
		}
		if (StringUtils.isNotBlank(conditionDTO.getEmployeeNumber())) {
			restriction = cb.and(restriction, cb.like(
					root.get(HRISReviewerDetailView_.empEmployeeNumber),
					conditionDTO.getEmployeeNumber() + '%'));
		}

		if (StringUtils.isNotBlank(conditionDTO.getHrisReviewer1())) {
			restriction = cb.and(restriction, cb.or(cb.like(cb.upper(root
					.get(HRISReviewerDetailView_.reviewer1FirstName)),
					conditionDTO.getHrisReviewer1().toUpperCase()), cb.like(
					cb.upper(root
							.get(HRISReviewerDetailView_.reviewer1LastName)),
					conditionDTO.getHrisReviewer1().toUpperCase())));
		}
		if (StringUtils.isNotBlank(conditionDTO.getHrisReviewer2())) {
			restriction = cb.and(restriction, cb.or(cb.like(cb.upper(root
					.get(HRISReviewerDetailView_.reviewer2FirstName)),
					conditionDTO.getHrisReviewer2().toUpperCase()), cb.like(
					cb.upper(root
							.get(HRISReviewerDetailView_.reviewer2LastName)),
					conditionDTO.getHrisReviewer2().toUpperCase())));
		}
		if (StringUtils.isNotBlank(conditionDTO.getHrisReviewer3())) {
			restriction = cb.and(restriction, cb.or(cb.like(cb.upper(root
					.get(HRISReviewerDetailView_.reviewer3FirstName)),
					conditionDTO.getHrisReviewer3().toUpperCase()), cb.like(
					cb.upper(root
							.get(HRISReviewerDetailView_.reviewer3LastName)),
					conditionDTO.getHrisReviewer3().toUpperCase())));
		}

		restriction = cb.and(restriction, cb.equal(
				hrisReviewerPKJoin.get(HRISReviewerDetailViewPK_.companyId),
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

		TypedQuery<HRISReviewerDetailView> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			typedQuery.setFirstResult(getStartPosition(pageDTO));
			typedQuery.setMaxResults(pageDTO.getPageSize());
		}
		return typedQuery.getResultList();
	}

	public Path<String> getSortPathForSearchEmployee(SortCondition sortDTO,
			Root<HRISReviewerDetailView> root) {

		List<String> employeeIsColList = new ArrayList<String>();
		employeeIsColList.add(SortConstants.LEAVE_REVIEWER_EMPLOYEE_NAME);
		employeeIsColList.add(SortConstants.EMPLOYEE_NUMBER);
		employeeIsColList.add(SortConstants.HRIS_REVIEWER1);
		employeeIsColList.add(SortConstants.HRIS_REVIEWER2);
		employeeIsColList.add(SortConstants.HRIS_REVIEWER3);

		Path<String> sortPath = null;

		if (employeeIsColList.contains(sortDTO.getColumnName())) {
			sortPath = root.get(colMap.get(HRISReviewerDetailView.class
					+ sortDTO.getColumnName()));
		}
		return sortPath;
	}

	@Override
	public int getCountByCondition(HRISReviewerConditionDTO conditionDTO,
			Long companyId) {
		return findByCondition(conditionDTO, null, null, companyId).size();
	}

}
