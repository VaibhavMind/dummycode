package com.payasia.dao.impl;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.payasia.common.dto.PendingClaimConditionDTO;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.SortConstants;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.ClaimApplicationReviewerDAO;
import com.payasia.dao.bean.ClaimApplication;
import com.payasia.dao.bean.ClaimApplicationReviewer;
import com.payasia.dao.bean.ClaimApplicationReviewer_;
import com.payasia.dao.bean.ClaimApplication_;
import com.payasia.dao.bean.ClaimStatusMaster;
import com.payasia.dao.bean.ClaimStatusMaster_;
import com.payasia.dao.bean.ClaimTemplate;
import com.payasia.dao.bean.ClaimTemplate_;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeClaimTemplate;
import com.payasia.dao.bean.EmployeeClaimTemplate_;
import com.payasia.dao.bean.Employee_;

@Repository
public class ClaimApplicationReviewerDAOImpl extends BaseDAO implements ClaimApplicationReviewerDAO {
	private static final Logger LOGGER = Logger.getLogger(ClaimApplicationReviewerDAOImpl.class);

	@Override
	protected Object getBaseEntity() {
		ClaimApplicationReviewer claimApplicationReviewer = new ClaimApplicationReviewer();
		return claimApplicationReviewer;
	}

	@Override
	public void save(ClaimApplicationReviewer claimApplicationReviewer) {
		super.save(claimApplicationReviewer);
	}

	@Override
	public void update(ClaimApplicationReviewer claimApplicationReviewer) {
		super.update(claimApplicationReviewer);
	}

	@Override
	public void deleteByCondition(Long claimApplicationId) {

		String queryString = "DELETE FROM ClaimApplicationReviewer car WHERE car.claimApplication.claimApplicationId = :claimApplicationId ";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("claimApplicationId", claimApplicationId);
		q.executeUpdate();
	}

	@Override
	public ClaimApplicationReviewer findByID(long claimApplicationReviewerId) {
		return super.findById(ClaimApplicationReviewer.class, claimApplicationReviewerId);
	}

	@Override
	public List<ClaimApplicationReviewer> findByCondition(Long empId, PageRequest pageDTO, SortCondition sortDTO,
			PendingClaimConditionDTO claimConditionDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ClaimApplicationReviewer> criteriaQuery = cb.createQuery(ClaimApplicationReviewer.class);
		Root<ClaimApplicationReviewer> claimRoot = criteriaQuery.from(ClaimApplicationReviewer.class);
		criteriaQuery.select(claimRoot);

		Predicate restriction = cb.conjunction();

		Join<ClaimApplicationReviewer, Employee> empLeaveJoin = claimRoot.join(ClaimApplicationReviewer_.employee);

		Join<ClaimApplicationReviewer, ClaimApplication> claimApplicationJoin = claimRoot
				.join(ClaimApplicationReviewer_.claimApplication);

		Join<ClaimApplication, ClaimStatusMaster> claimStatusJoin = claimApplicationJoin
				.join(ClaimApplication_.claimStatusMaster);

		Join<ClaimApplication, EmployeeClaimTemplate> employeeClaimTempJoin = claimApplicationJoin
				.join(ClaimApplication_.employeeClaimTemplate);

		Join<EmployeeClaimTemplate, ClaimTemplate> claimTempJoin = employeeClaimTempJoin
				.join(EmployeeClaimTemplate_.claimTemplate);

		if (StringUtils.isNotBlank(claimConditionDTO.getCreatedDate())) {
			restriction = cb.and(restriction,
					cb.equal(claimApplicationJoin.get(ClaimApplication_.createdDate).as(Date.class),
							DateUtils.stringToDate(claimConditionDTO.getCreatedDate())));

		}

		if (StringUtils.isNotBlank(claimConditionDTO.getClaimGroup())) {

			restriction = cb.and(restriction, cb.like(cb.upper(claimTempJoin.get(ClaimTemplate_.templateName)),
					claimConditionDTO.getClaimGroup().toUpperCase()));

		}

		if (claimConditionDTO.getClaimNumber() != null) {

			restriction = cb.and(restriction, cb.equal(claimApplicationJoin.get(ClaimApplication_.claimNumber),
					claimConditionDTO.getClaimNumber()));

		}

		restriction = cb.and(restriction, cb.equal(empLeaveJoin.get(Employee_.employeeId), empId));

		restriction = cb.and(restriction, cb.equal(claimRoot.get(ClaimApplicationReviewer_.pending), true));

		if (StringUtils.isNotBlank(claimConditionDTO.getClaimStatusName())) {

			restriction = cb.and(restriction,
					cb.like(claimStatusJoin.get(ClaimStatusMaster_.claimStatusName).as(String.class),
							claimConditionDTO.getClaimStatusName()));
		}

		criteriaQuery.where(restriction);

		if (StringUtils.isBlank(claimConditionDTO.getClaimNumberSortOrder())
				&& StringUtils.isBlank(claimConditionDTO.getCreatedDateSortOrder())) {
			criteriaQuery.orderBy(cb.desc(claimApplicationJoin.get(ClaimApplication_.updatedDate)));
		} else {
			getClaimGridSortOrder(claimConditionDTO.getClaimNumberSortOrder(),
					claimConditionDTO.getCreatedDateSortOrder(), cb, criteriaQuery, claimApplicationJoin);
		}

		if (sortDTO != null) {

			Path<String> sortPath = getSortPathForPendingClaim(sortDTO, claimTempJoin, claimApplicationJoin);
			if (sortPath != null) {
				if (SortConstants.DB_ORDER_BY_ASC.equalsIgnoreCase(sortDTO.getOrderType())) {
					criteriaQuery.orderBy(cb.asc(sortPath));
				}
				if (SortConstants.DB_ORDER_BY_DESC.equalsIgnoreCase(sortDTO.getOrderType())) {
					criteriaQuery.orderBy(cb.desc(sortPath));
				}
			}
		}
		
		TypedQuery<ClaimApplicationReviewer> claimTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			claimTypedQuery.setFirstResult(getStartPosition(pageDTO));
			claimTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		return claimTypedQuery.getResultList();
	}

	private void getClaimGridSortOrder(String claimNumberSortOrder, String createdDateSortOrder, CriteriaBuilder cb,
			CriteriaQuery<ClaimApplicationReviewer> criteriaQuery,
			Join<ClaimApplicationReviewer, ClaimApplication> ClaimAppRoot) {
		Order order1 = null;
		Order order2 = null;
		if (StringUtils.isNotBlank(createdDateSortOrder) && StringUtils.isNotBlank(claimNumberSortOrder)) {
			if (createdDateSortOrder.equalsIgnoreCase(PayAsiaConstants.ASC)
					&& claimNumberSortOrder.equalsIgnoreCase(PayAsiaConstants.ASC)) {
				order1 = cb.asc(ClaimAppRoot.get(ClaimApplication_.createdDate).as(Date.class));
				order2 = cb.asc(ClaimAppRoot.get(ClaimApplication_.claimNumber));
			} else if (createdDateSortOrder.equalsIgnoreCase(PayAsiaConstants.ASC)
					&& claimNumberSortOrder.equalsIgnoreCase(PayAsiaConstants.DESC)) {
				order1 = cb.asc(ClaimAppRoot.get(ClaimApplication_.createdDate).as(Date.class));
				order2 = cb.desc(ClaimAppRoot.get(ClaimApplication_.claimNumber));
			} else if (createdDateSortOrder.equalsIgnoreCase(PayAsiaConstants.DESC)
					&& claimNumberSortOrder.equalsIgnoreCase(PayAsiaConstants.ASC)) {
				order1 = cb.desc(ClaimAppRoot.get(ClaimApplication_.createdDate).as(Date.class));
				order2 = cb.asc(ClaimAppRoot.get(ClaimApplication_.claimNumber));
			} else {
				order1 = cb.desc(ClaimAppRoot.get(ClaimApplication_.createdDate).as(Date.class));
				order2 = cb.desc(ClaimAppRoot.get(ClaimApplication_.claimNumber));
			}
			criteriaQuery.orderBy(order1, order2);
		} else if (StringUtils.isNotBlank(createdDateSortOrder) && StringUtils.isBlank(claimNumberSortOrder)) {
			if (createdDateSortOrder.equalsIgnoreCase(PayAsiaConstants.DESC)) {
				order1 = cb.desc(ClaimAppRoot.get(ClaimApplication_.createdDate).as(Date.class));
			}
			if (createdDateSortOrder.equalsIgnoreCase(PayAsiaConstants.ASC)) {
				order1 = cb.asc(ClaimAppRoot.get(ClaimApplication_.createdDate).as(Date.class));
			}
			criteriaQuery.orderBy(order1);
		} else if (StringUtils.isNotBlank(claimNumberSortOrder) && StringUtils.isBlank(createdDateSortOrder)) {
			if (claimNumberSortOrder.equalsIgnoreCase(PayAsiaConstants.DESC)) {
				order1 = cb.desc(ClaimAppRoot.get(ClaimApplication_.claimNumber));
			}
			if (claimNumberSortOrder.equalsIgnoreCase(PayAsiaConstants.ASC)) {
				order1 = cb.asc(ClaimAppRoot.get(ClaimApplication_.claimNumber));
			}
			criteriaQuery.orderBy(order1);
		}
	}

	@Override
	public Integer findByConditionCountRecords(Long empId, PendingClaimConditionDTO claimConditionDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Integer> criteriaQuery = cb.createQuery(Integer.class);
		Root<ClaimApplicationReviewer> claimRoot = criteriaQuery.from(ClaimApplicationReviewer.class);
		criteriaQuery.select(cb.count(claimRoot).as(Integer.class));

		Predicate restriction = cb.conjunction();

		Join<ClaimApplicationReviewer, Employee> empLeaveJoin = claimRoot.join(ClaimApplicationReviewer_.employee);

		Join<ClaimApplicationReviewer, ClaimApplication> claimApplicationJoin = claimRoot
				.join(ClaimApplicationReviewer_.claimApplication);

		Join<ClaimApplication, ClaimStatusMaster> claimStatusJoin = claimApplicationJoin
				.join(ClaimApplication_.claimStatusMaster);

		Join<ClaimApplication, EmployeeClaimTemplate> employeeClaimTempJoin = claimApplicationJoin
				.join(ClaimApplication_.employeeClaimTemplate);

		Join<EmployeeClaimTemplate, ClaimTemplate> claimTempJoin = employeeClaimTempJoin
				.join(EmployeeClaimTemplate_.claimTemplate);

		if (StringUtils.isNotBlank(claimConditionDTO.getCreatedDate())) {
			restriction = cb.and(restriction,
					cb.equal(claimApplicationJoin.get(ClaimApplication_.createdDate).as(Date.class),
							DateUtils.stringToDate(claimConditionDTO.getCreatedDate())));

		}

		if (StringUtils.isNotBlank(claimConditionDTO.getClaimGroup())) {

			restriction = cb.and(restriction, cb.like(cb.upper(claimTempJoin.get(ClaimTemplate_.templateName)),
					claimConditionDTO.getClaimGroup().toUpperCase()));

		}

		if (claimConditionDTO.getClaimNumber() != null) {

			restriction = cb.and(restriction, cb.equal(claimApplicationJoin.get(ClaimApplication_.claimNumber),
					claimConditionDTO.getClaimNumber()));

		}

		restriction = cb.and(restriction, cb.equal(empLeaveJoin.get(Employee_.employeeId), empId));

		restriction = cb.and(restriction, cb.equal(claimRoot.get(ClaimApplicationReviewer_.pending), true));

		if (StringUtils.isNotBlank(claimConditionDTO.getClaimStatusName())) {

			restriction = cb.and(restriction,
					cb.like(claimStatusJoin.get(ClaimStatusMaster_.claimStatusName).as(String.class),
							claimConditionDTO.getClaimStatusName()));
		}

		criteriaQuery.where(restriction);

		TypedQuery<Integer> claimTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		return claimTypedQuery.getSingleResult();
	}

	@Override
	public List<ClaimApplicationReviewer> checkClaimEmployeeReviewer(Long employeeId, List<String> claimStatusList) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ClaimApplicationReviewer> criteriaQuery = cb.createQuery(ClaimApplicationReviewer.class);
		Root<ClaimApplicationReviewer> claimAppRevRoot = criteriaQuery.from(ClaimApplicationReviewer.class);
		criteriaQuery.select(claimAppRevRoot);

		Predicate restriction = cb.conjunction();

		Join<ClaimApplicationReviewer, Employee> empClaimJoin = claimAppRevRoot
				.join(ClaimApplicationReviewer_.employee);

		Join<ClaimApplicationReviewer, ClaimApplication> claimAppJoin = claimAppRevRoot
				.join(ClaimApplicationReviewer_.claimApplication);
		Join<ClaimApplication, ClaimStatusMaster> claimStatusJoin = claimAppJoin
				.join(ClaimApplication_.claimStatusMaster);

		restriction = cb.and(restriction, cb.equal(empClaimJoin.get(Employee_.employeeId), employeeId));
		restriction = cb.and(restriction, claimStatusJoin.get(ClaimStatusMaster_.claimStatusName).in(claimStatusList));
		criteriaQuery.where(restriction);

		TypedQuery<ClaimApplicationReviewer> claimAppTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		if (claimAppTypedQuery.getResultList().size() > 0) {
			return claimAppTypedQuery.getResultList();
		} else {
			return null;
		}
	}

	@Override
	public Integer getClaimReviewerCount(long claimApplicationId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Integer> criteriaQuery = cb.createQuery(Integer.class);
		Root<ClaimApplicationReviewer> claimApplicationReviewerRoot = criteriaQuery
				.from(ClaimApplicationReviewer.class);
		criteriaQuery.select(cb.count(claimApplicationReviewerRoot).as(Integer.class));

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(claimApplicationReviewerRoot.get(ClaimApplicationReviewer_.claimApplication)
						.get("claimApplicationId").as(Long.class), claimApplicationId));

		criteriaQuery.where(restriction);

		TypedQuery<Integer> empTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		try {
			return empTypedQuery.getSingleResult();
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			return null;
		}

	}

	@Override
	public ClaimApplicationReviewer getClaimApplicationReviewerDetail(Long claimApplicationReviewerId,
			Long employeeId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ClaimApplicationReviewer> criteriaQuery = cb.createQuery(ClaimApplicationReviewer.class);
		Root<ClaimApplicationReviewer> claimApplicationReviewerRoot = criteriaQuery
				.from(ClaimApplicationReviewer.class);

		Join<ClaimApplicationReviewer, Employee> empClaimJoin = claimApplicationReviewerRoot
				.join(ClaimApplicationReviewer_.employee);

		Predicate restriction = cb.conjunction();

		if(claimApplicationReviewerId!=null){
		restriction = cb.and(restriction,
				cb.equal(claimApplicationReviewerRoot.get(ClaimApplicationReviewer_.claimApplicationReviewerId),
						claimApplicationReviewerId));
		}
		restriction = cb.and(restriction,
				cb.equal(claimApplicationReviewerRoot.get(ClaimApplicationReviewer_.pending), true));
		restriction = cb.and(restriction, cb.equal(empClaimJoin.get(Employee_.employeeId), employeeId));

		criteriaQuery.where(restriction);

		TypedQuery<ClaimApplicationReviewer> empTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		List<ClaimApplicationReviewer> list = empTypedQuery.getResultList();
		return list != null && !list.isEmpty() ? list.get(0) : null;

	}
	
	private Path<String> getSortPathForPendingClaim(SortCondition sortDTO, Join<EmployeeClaimTemplate, ClaimTemplate> claimTempJoin, 
			Join<ClaimApplicationReviewer, ClaimApplication> claimApplicationJoin) {
		
		List<String> claimAppColList = new ArrayList<>();
		List<String> claimTempColList = new ArrayList<>();

		claimTempColList.add("claimTemplateName");
		claimAppColList.add(PayAsiaConstants.MY_REQUEST_CLAIM_NUMBER);
		claimAppColList.add("totalItems");
		claimAppColList.add("createDate");
		claimAppColList.add("createdBy");

		Path<String> sortPath = null;

		if (claimTempColList.contains(sortDTO.getColumnName())) {
			sortPath = claimTempJoin.get(colMap.get(ClaimTemplate.class + sortDTO.getColumnName()));
		}
		if (claimAppColList.contains(sortDTO.getColumnName())) {
			sortPath = claimApplicationJoin.get(colMap.get(ClaimApplication.class + sortDTO.getColumnName()));
		}
		return sortPath;
	}
}
