package com.payasia.dao.impl;

import java.lang.reflect.InvocationTargetException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import org.apache.commons.lang.StringUtils;
import org.hibernate.jdbc.Work;
import org.springframework.stereotype.Repository;

import com.payasia.common.dto.AddClaimConditionDTO;
import com.payasia.common.dto.AddClaimDTO;
import com.payasia.common.dto.EmployeeWiseConsolidatedClaimReportDataDTO;
import com.payasia.common.dto.EmployeeWiseTemplateClaimReportDataDTO;
import com.payasia.common.dto.MonthlyConsolidatedFinanceReportDataDTO;
import com.payasia.common.dto.ValidateClaimApplicationDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.NullAwareBeanUtilsBean;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.SortConstants;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.ClaimApplicationDAO;
import com.payasia.dao.bean.AppCodeMaster;
import com.payasia.dao.bean.AppCodeMaster_;
import com.payasia.dao.bean.ClaimApplication;
import com.payasia.dao.bean.ClaimApplicationItem;
import com.payasia.dao.bean.ClaimApplicationItem_;
import com.payasia.dao.bean.ClaimApplicationWorkflow;
import com.payasia.dao.bean.ClaimApplicationWorkflow_;
import com.payasia.dao.bean.ClaimApplication_;
import com.payasia.dao.bean.ClaimCategoryMaster;
import com.payasia.dao.bean.ClaimCategoryMaster_;
import com.payasia.dao.bean.ClaimItemMaster;
import com.payasia.dao.bean.ClaimItemMaster_;
import com.payasia.dao.bean.ClaimStatusMaster;
import com.payasia.dao.bean.ClaimStatusMaster_;
import com.payasia.dao.bean.ClaimTemplate;
import com.payasia.dao.bean.ClaimTemplateItem;
import com.payasia.dao.bean.ClaimTemplateItem_;
import com.payasia.dao.bean.ClaimTemplate_;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeClaimTemplate;
import com.payasia.dao.bean.EmployeeClaimTemplateItem;
import com.payasia.dao.bean.EmployeeClaimTemplateItem_;
import com.payasia.dao.bean.EmployeeClaimTemplate_;
import com.payasia.dao.bean.Employee_;

@Repository
public class ClaimApplicationDAOImpl extends BaseDAO implements ClaimApplicationDAO {

	@Override
	protected Object getBaseEntity() {
		ClaimApplication claimApplication = new ClaimApplication();
		return claimApplication;
	}

	@Override
	public ClaimApplication findByID(long claimApplicationId) {
		return super.findById(ClaimApplication.class, claimApplicationId);
	}

	@Override
	public ClaimApplication saveReturn(ClaimApplication claimApplication) {

		ClaimApplication persistObj = claimApplication;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (ClaimApplication) getBaseEntity();
			beanUtil.copyProperties(persistObj, claimApplication);
		} catch (IllegalAccessException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		} catch (InvocationTargetException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		}
		this.entityManagerFactory.persist(persistObj);
		return persistObj;
	}

	@Override
	public void update(ClaimApplication claimApplication) {
		super.update(claimApplication);

	}

	@Override
	public void delete(ClaimApplication claimApplication) {
		super.delete(claimApplication);

	}

	@Override
	public List<ClaimApplication> findByCondition(AddClaimConditionDTO conditionDTO, PageRequest pageDTO,
			SortCondition sortDTO) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ClaimApplication> criteriaQuery = cb.createQuery(ClaimApplication.class);
		Root<ClaimApplication> claimApplicationRoot = criteriaQuery.from(ClaimApplication.class);
		criteriaQuery.select(claimApplicationRoot);

		Predicate restriction = cb.conjunction();

		Join<ClaimApplication, Employee> empLeaveJoin = claimApplicationRoot.join(ClaimApplication_.employee);

		Join<ClaimApplication, ClaimStatusMaster> leaveStatusJoin = claimApplicationRoot
				.join(ClaimApplication_.claimStatusMaster);

		Join<ClaimApplication, EmployeeClaimTemplate> employeeClaimTempJoin = claimApplicationRoot
				.join(ClaimApplication_.employeeClaimTemplate);

		Join<EmployeeClaimTemplate, ClaimTemplate> claimTempJoin = employeeClaimTempJoin
				.join(EmployeeClaimTemplate_.claimTemplate);

		Join<ClaimTemplate, AppCodeMaster> appCodeJoin = claimTempJoin.join(ClaimTemplate_.frontEndViewMode,
				JoinType.LEFT);

		restriction = cb.and(restriction,
				cb.notEqual(
						cb.coalesce(appCodeJoin.get(AppCodeMaster_.codeDesc),
								PayAsiaConstants.CLAIM_TEMPLATE_VIEW_MODE_ON),
						PayAsiaConstants.CLAIM_TEMPLATE_VIEW_MODE_OFF));

		if (StringUtils.isNotBlank(conditionDTO.getCreatedDate())) {
			restriction = cb.and(restriction,
					cb.equal(claimApplicationRoot.get(ClaimApplication_.createdDate).as(Date.class),
							DateUtils.stringToDate(conditionDTO.getCreatedDate())));

		}
		if (conditionDTO.getVisibleToEmployee() != null) {
			restriction = cb.and(restriction, cb.equal(claimApplicationRoot.get(ClaimApplication_.visibleToEmployee),
					conditionDTO.getVisibleToEmployee()));

		}

		if (StringUtils.isNotBlank(conditionDTO.getClaimGroup())) {

			restriction = cb.and(restriction, cb.like(cb.upper(claimTempJoin.get(ClaimTemplate_.templateName)),
					conditionDTO.getClaimGroup().toUpperCase()));

		}

		if (conditionDTO.getClaimNumber() != null) {
			restriction = cb.and(restriction,
					cb.like(claimApplicationRoot.get(ClaimApplication_.claimNumber).as(String.class),
							'%' + String.valueOf(conditionDTO.getClaimNumber()) + '%'));

		}

		if (conditionDTO.getEmployeeId() != null) {
			restriction = cb.and(restriction,
					cb.equal(empLeaveJoin.get(Employee_.employeeId), conditionDTO.getEmployeeId()));
		}

		if (conditionDTO.getClaimStatus().size() > 0) {
			restriction = cb.and(restriction,
					leaveStatusJoin.get(ClaimStatusMaster_.claimStatusName).in(conditionDTO.getClaimStatus()));

		}
		if (conditionDTO.getFromDate() != null) {
			restriction = cb.and(restriction, cb.greaterThanOrEqualTo(
					(claimApplicationRoot.get(ClaimApplication_.createdDate)), conditionDTO.getFromDate()));
		}
		if (conditionDTO.getToDate() != null) {
			restriction = cb.and(restriction, cb.lessThanOrEqualTo(
					(claimApplicationRoot.get(ClaimApplication_.createdDate)), conditionDTO.getToDate()));
		}

		criteriaQuery.where(restriction);

		if (StringUtils.isBlank(conditionDTO.getClaimNumberSortOrder())
				&& StringUtils.isBlank(conditionDTO.getCreatedDateSortOrder())) {
			criteriaQuery.orderBy(cb.desc(claimApplicationRoot.get(ClaimApplication_.updatedDate)));
		} else {
			getClaimGridSortOrder(conditionDTO.getClaimNumberSortOrder(), conditionDTO.getCreatedDateSortOrder(), cb,
					criteriaQuery, claimApplicationRoot);
		}
		
		if (sortDTO != null) {

			Path<String> sortPath = getSortPathForMyClaim(sortDTO, claimApplicationRoot, claimTempJoin, leaveStatusJoin);
			if (sortPath != null) {
				if (SortConstants.DB_ORDER_BY_ASC.equalsIgnoreCase(sortDTO.getOrderType())) {
					criteriaQuery.orderBy(cb.asc(sortPath));
				}
				if (SortConstants.DB_ORDER_BY_DESC.equalsIgnoreCase(sortDTO.getOrderType())) {
					criteriaQuery.orderBy(cb.desc(sortPath));
				}
			}
		}

		TypedQuery<ClaimApplication> claimAppTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			claimAppTypedQuery.setFirstResult(getStartPosition(pageDTO));
			claimAppTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		return claimAppTypedQuery.getResultList();
	}

	private Path<String> getSortPathForMyClaim(SortCondition sortDTO, Root<ClaimApplication> claimApplicationRoot, 
			Join<EmployeeClaimTemplate, ClaimTemplate> claimTempJoin, Join<ClaimApplication, ClaimStatusMaster> leaveStatusJoin) {
		
		List<String> claimAppColList = new ArrayList<>();
		List<String> claimStatusMasterColList = new ArrayList<>();
		List<String> claimTempColList = new ArrayList<>();

		claimTempColList.add("claimTemplateName");
		claimAppColList.add(PayAsiaConstants.MY_REQUEST_CLAIM_NUMBER);
		claimAppColList.add("totalItems");
		claimAppColList.add("createDate");
		claimStatusMasterColList.add("status");
		
		Path<String> sortPath = null;

		if (claimAppColList.contains(sortDTO.getColumnName())) {
			sortPath = claimApplicationRoot.get(colMap.get(ClaimApplication.class + sortDTO.getColumnName()));
		}
		if (claimTempColList.contains(sortDTO.getColumnName())) {
			sortPath = claimTempJoin.get(colMap.get(ClaimTemplate.class + sortDTO.getColumnName()));
		}
		if (claimStatusMasterColList.contains(sortDTO.getColumnName())) {
			sortPath = leaveStatusJoin.get(colMap.get(ClaimStatusMaster.class + sortDTO.getColumnName()));
		}
		return sortPath;
	}

	@Override
	public Long getCountForCondition(AddClaimConditionDTO conditionDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
		Root<ClaimApplication> claimApplicationRoot = criteriaQuery.from(ClaimApplication.class);
		criteriaQuery.select(cb.count(claimApplicationRoot));

		Predicate restriction = cb.conjunction();

		Join<ClaimApplication, Employee> empLeaveJoin = claimApplicationRoot.join(ClaimApplication_.employee);

		Join<ClaimApplication, ClaimStatusMaster> leaveStatusJoin = claimApplicationRoot
				.join(ClaimApplication_.claimStatusMaster);

		Join<ClaimApplication, EmployeeClaimTemplate> employeeClaimTempJoin = claimApplicationRoot
				.join(ClaimApplication_.employeeClaimTemplate);

		Join<EmployeeClaimTemplate, ClaimTemplate> claimTempJoin = employeeClaimTempJoin
				.join(EmployeeClaimTemplate_.claimTemplate);
		Join<ClaimTemplate, AppCodeMaster> appCodeJoin = claimTempJoin.join(ClaimTemplate_.frontEndViewMode,
				JoinType.LEFT);

		restriction = cb.and(restriction,
				cb.notEqual(
						cb.coalesce(appCodeJoin.get(AppCodeMaster_.codeDesc),
								PayAsiaConstants.CLAIM_TEMPLATE_VIEW_MODE_ON),
						PayAsiaConstants.CLAIM_TEMPLATE_VIEW_MODE_OFF));

		restriction = cb.and(restriction, cb.equal((claimApplicationRoot.get(ClaimApplication_.visibleToEmployee)), 1));

		if (StringUtils.isNotBlank(conditionDTO.getCreatedDate())) {
			restriction = cb.and(restriction,
					cb.equal(claimApplicationRoot.get(ClaimApplication_.createdDate).as(Date.class),
							DateUtils.stringToDate(conditionDTO.getCreatedDate())));

		}

		if (StringUtils.isNotBlank(conditionDTO.getClaimGroup())) {

			restriction = cb.and(restriction, cb.like(cb.upper(claimTempJoin.get(ClaimTemplate_.templateName)),
					conditionDTO.getClaimGroup().toUpperCase()));

		}

		if (conditionDTO.getClaimNumber() != null) {

			restriction = cb.and(restriction,
					cb.like(claimApplicationRoot.get(ClaimApplication_.claimNumber).as(String.class),
							'%' + String.valueOf(conditionDTO.getClaimNumber()) + '%'));

		}

		if (conditionDTO.getEmployeeId() != null) {
			restriction = cb.and(restriction,
					cb.equal(empLeaveJoin.get(Employee_.employeeId), conditionDTO.getEmployeeId()));
		}

		if (conditionDTO.getClaimStatus().size() > 0) {
			restriction = cb.and(restriction,
					leaveStatusJoin.get(ClaimStatusMaster_.claimStatusName).in(conditionDTO.getClaimStatus()));

		}
		if (conditionDTO.getFromDate() != null) {
			restriction = cb.and(restriction, cb.greaterThanOrEqualTo(
					(claimApplicationRoot.get(ClaimApplication_.createdDate)), conditionDTO.getFromDate()));
		}
		if (conditionDTO.getToDate() != null) {
			restriction = cb.and(restriction, cb.lessThanOrEqualTo(
					(claimApplicationRoot.get(ClaimApplication_.createdDate)), conditionDTO.getToDate()));
		}

		criteriaQuery.where(restriction);

		TypedQuery<Long> claimAppTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		return claimAppTypedQuery.getSingleResult();
	}

	@Override
	public Long getMaxClaimNumber() {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
		Root<ClaimApplication> claimRoot = criteriaQuery.from(ClaimApplication.class);
		criteriaQuery.select(cb.max(claimRoot.get(ClaimApplication_.claimNumber)));

		TypedQuery<Long> maxEmployeeIdQuery = entityManagerFactory.createQuery(criteriaQuery);

		Long claimNum = maxEmployeeIdQuery.getSingleResult();
		if (claimNum == null) {
			claimNum = (long) 0;
		}
		return claimNum;

	}

	@Override
	public List<ClaimApplication> findClaimsByBatchPeriod(PageRequest pageDTO, SortCondition sortDTO,
			String claimStatusName, Timestamp fromDate, Timestamp toDate, Long companyID) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ClaimApplication> criteriaQuery = cb.createQuery(ClaimApplication.class);
		Root<ClaimApplication> ClaimAppRoot = criteriaQuery.from(ClaimApplication.class);
		criteriaQuery.select(ClaimAppRoot);

		Predicate restriction = cb.conjunction();

		Join<ClaimApplication, ClaimStatusMaster> claimStatusJoin = ClaimAppRoot
				.join(ClaimApplication_.claimStatusMaster);
		Join<ClaimApplication, Company> companyJoin = ClaimAppRoot.join(ClaimApplication_.company);

		Join<ClaimApplication, ClaimApplicationWorkflow> claimAppWorkflowJoin = ClaimAppRoot
				.join(ClaimApplication_.claimApplicationWorkflows);
		Join<ClaimApplicationWorkflow, ClaimStatusMaster> claimAppWorkflowStatusJoin = claimAppWorkflowJoin
				.join(ClaimApplicationWorkflow_.claimStatusMaster);

		restriction = cb.and(restriction, cb.greaterThanOrEqualTo(
				(claimAppWorkflowJoin.get(ClaimApplicationWorkflow_.createdDate).as(Date.class)), fromDate));
		if (toDate != null) {
			restriction = cb.and(restriction, cb.lessThanOrEqualTo(
					(claimAppWorkflowJoin.get(ClaimApplicationWorkflow_.createdDate).as(Date.class)), toDate));
		}
		restriction = cb.and(restriction,
				cb.equal(claimStatusJoin.get(ClaimStatusMaster_.claimStatusName), claimStatusName));
		restriction = cb.and(restriction,
				cb.equal(claimAppWorkflowStatusJoin.get(ClaimStatusMaster_.claimStatusName), claimStatusName));
		restriction = cb.and(restriction, cb.equal(companyJoin.get(Company_.companyId), companyID));
		criteriaQuery.where(restriction);

		TypedQuery<ClaimApplication> claimAppTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			claimAppTypedQuery.setFirstResult(getStartPosition(pageDTO));
			claimAppTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		return claimAppTypedQuery.getResultList();
	}

	@Override
	public Integer getClaimsByBatchPeriodCount(String claimStatusName, Timestamp fromDate, Timestamp toDate,
			Long companyID) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Integer> criteriaQuery = cb.createQuery(Integer.class);
		Root<ClaimApplication> ClaimAppRoot = criteriaQuery.from(ClaimApplication.class);
		criteriaQuery.select(cb.count(ClaimAppRoot).as(Integer.class));

		Predicate restriction = cb.conjunction();

		Join<ClaimApplication, ClaimStatusMaster> claimStatusJoin = ClaimAppRoot
				.join(ClaimApplication_.claimStatusMaster);
		Join<ClaimApplication, Company> companyJoin = ClaimAppRoot.join(ClaimApplication_.company);
		Join<ClaimApplication, ClaimApplicationWorkflow> claimAppWorkflowJoin = ClaimAppRoot
				.join(ClaimApplication_.claimApplicationWorkflows);
		Join<ClaimApplicationWorkflow, ClaimStatusMaster> claimAppWorkflowStatusJoin = claimAppWorkflowJoin
				.join(ClaimApplicationWorkflow_.claimStatusMaster);
		restriction = cb.and(restriction, cb.greaterThanOrEqualTo(
				(claimAppWorkflowJoin.get(ClaimApplicationWorkflow_.createdDate).as(Date.class)), fromDate));
		if (toDate != null) {
			restriction = cb.and(restriction, cb.lessThanOrEqualTo(
					(claimAppWorkflowJoin.get(ClaimApplicationWorkflow_.createdDate).as(Date.class)), toDate));
		}
		restriction = cb.and(restriction,
				cb.equal(claimStatusJoin.get(ClaimStatusMaster_.claimStatusName), claimStatusName));
		restriction = cb.and(restriction,
				cb.equal(claimAppWorkflowStatusJoin.get(ClaimStatusMaster_.claimStatusName), claimStatusName));
		restriction = cb.and(restriction, cb.equal(companyJoin.get(Company_.companyId), companyID));
		criteriaQuery.where(restriction);

		TypedQuery<Integer> claimAppTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		return claimAppTypedQuery.getSingleResult();
	}

	@Override
	public List<ClaimApplication> findByConditionForAdmin(PageRequest pageDTO, SortCondition sortDTO, Long employeeId,
			List<String> claimStatus, String fromDate, String toDate, Boolean visibleToEmployee,
			String claimNumberSortOrder, String createdDateSortOrder) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ClaimApplication> criteriaQuery = cb.createQuery(ClaimApplication.class);
		Root<ClaimApplication> ClaimAppRoot = criteriaQuery.from(ClaimApplication.class);
		criteriaQuery.select(ClaimAppRoot);

		Predicate restriction = cb.conjunction();

		Join<ClaimApplication, Employee> empJoin = ClaimAppRoot.join(ClaimApplication_.employee);

		Join<ClaimApplication, EmployeeClaimTemplate> empClaimTempJoin = ClaimAppRoot
				.join(ClaimApplication_.employeeClaimTemplate);
		Join<EmployeeClaimTemplate, ClaimTemplate> claimtempJoin = empClaimTempJoin
				.join(EmployeeClaimTemplate_.claimTemplate);
		Join<ClaimTemplate, AppCodeMaster> appCodeJoin = claimtempJoin.join(ClaimTemplate_.backEndViewMode,
				JoinType.LEFT);

		Join<ClaimApplication, ClaimStatusMaster> leaveStatusJoin = ClaimAppRoot
				.join(ClaimApplication_.claimStatusMaster);

		restriction = cb.and(restriction, cb.equal(empJoin.get(Employee_.employeeId), employeeId));
		restriction = cb.and(restriction,
				cb.notEqual(
						cb.coalesce(appCodeJoin.get(AppCodeMaster_.codeDesc),
								PayAsiaConstants.CLAIM_TEMPLATE_VIEW_MODE_ON),
						PayAsiaConstants.CLAIM_TEMPLATE_VIEW_MODE_OFF));

		if (!claimStatus.isEmpty()) {
			restriction = cb.and(restriction, leaveStatusJoin.get(ClaimStatusMaster_.claimStatusName).in(claimStatus));
		}
		if (StringUtils.isNotBlank(fromDate)) {
			restriction = cb.and(restriction,
					cb.greaterThanOrEqualTo((ClaimAppRoot.get(ClaimApplication_.createdDate).as(Date.class)),
							DateUtils.stringToTimestamp(fromDate)));
		}
		if (StringUtils.isNotBlank(toDate)) {
			restriction = cb.and(restriction,
					cb.lessThanOrEqualTo((ClaimAppRoot.get(ClaimApplication_.createdDate).as(Date.class)),
							DateUtils.stringToTimestamp(toDate)));
		}
		if (visibleToEmployee != null) {
			restriction = cb.and(restriction,
					cb.equal((ClaimAppRoot.get(ClaimApplication_.visibleToEmployee)), visibleToEmployee));
		}

		criteriaQuery.where(restriction);

		getClaimGridSortOrder(claimNumberSortOrder, createdDateSortOrder, cb, criteriaQuery, ClaimAppRoot);

		TypedQuery<ClaimApplication> claimAppTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		return claimAppTypedQuery.getResultList();
	}

	private void getClaimGridSortOrder(String claimNumberSortOrder, String createdDateSortOrder, CriteriaBuilder cb,
			CriteriaQuery<ClaimApplication> criteriaQuery, Root<ClaimApplication> ClaimAppRoot) {
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
	public List<ClaimApplication> findByConditionForEmployee(PageRequest pageDTO, SortCondition sortDTO,
			Long employeeId, List<String> claimStatus, String fromDate, String toDate, Boolean visibleToEmployee,
			AddClaimConditionDTO conditionDTO, String claimNumberSortOrder, String createdDateSortOrder) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ClaimApplication> criteriaQuery = cb.createQuery(ClaimApplication.class);
		Root<ClaimApplication> ClaimAppRoot = criteriaQuery.from(ClaimApplication.class);
		criteriaQuery.select(ClaimAppRoot);

		Predicate restriction = cb.conjunction();

		Join<ClaimApplication, Employee> empJoin = ClaimAppRoot.join(ClaimApplication_.employee);

		Join<ClaimApplication, EmployeeClaimTemplate> empClaimTempJoin = ClaimAppRoot
				.join(ClaimApplication_.employeeClaimTemplate);
		Join<EmployeeClaimTemplate, ClaimTemplate> claimtempJoin = empClaimTempJoin
				.join(EmployeeClaimTemplate_.claimTemplate);
		Join<ClaimTemplate, AppCodeMaster> appCodeJoin = claimtempJoin.join(ClaimTemplate_.frontEndViewMode,
				JoinType.LEFT);

		Join<ClaimApplication, ClaimStatusMaster> leaveStatusJoin = ClaimAppRoot
				.join(ClaimApplication_.claimStatusMaster);

		restriction = cb.and(restriction, cb.equal(empJoin.get(Employee_.employeeId), employeeId));
		restriction = cb.and(restriction,
				cb.notEqual(
						cb.coalesce(appCodeJoin.get(AppCodeMaster_.codeDesc),
								PayAsiaConstants.CLAIM_TEMPLATE_VIEW_MODE_ON),
						PayAsiaConstants.CLAIM_TEMPLATE_VIEW_MODE_OFF));

		if (StringUtils.isNotBlank(conditionDTO.getCreatedDate())) {
			restriction = cb.and(restriction, cb.equal(ClaimAppRoot.get(ClaimApplication_.createdDate).as(Date.class),
					DateUtils.stringToDate(conditionDTO.getCreatedDate())));
		}

		if (StringUtils.isNotBlank(conditionDTO.getClaimGroup())) {
			restriction = cb.and(restriction, cb.like(cb.upper(claimtempJoin.get(ClaimTemplate_.templateName)),
					conditionDTO.getClaimGroup().toUpperCase()));
		}

		if (conditionDTO.getClaimNumber() != null) {
			restriction = cb.and(restriction, cb.like(ClaimAppRoot.get(ClaimApplication_.claimNumber).as(String.class),
					'%' + String.valueOf(conditionDTO.getClaimNumber()) + '%'));
		}

		if (!claimStatus.isEmpty()) {
			restriction = cb.and(restriction, leaveStatusJoin.get(ClaimStatusMaster_.claimStatusName).in(claimStatus));
		}

		if (StringUtils.isNotBlank(fromDate)) {
			restriction = cb.and(restriction,
					cb.greaterThanOrEqualTo((ClaimAppRoot.get(ClaimApplication_.createdDate).as(Date.class)),
							DateUtils.stringToTimestamp(fromDate)));
		}

		if (StringUtils.isNotBlank(toDate)) {
			restriction = cb.and(restriction,
					cb.lessThanOrEqualTo((ClaimAppRoot.get(ClaimApplication_.createdDate).as(Date.class)),
							DateUtils.stringToTimestamp(toDate)));
		}

		if (visibleToEmployee != null) {
			restriction = cb.and(restriction,
					cb.equal((ClaimAppRoot.get(ClaimApplication_.visibleToEmployee)), visibleToEmployee));
		}

		criteriaQuery.where(restriction);

		getClaimGridSortOrder(claimNumberSortOrder, createdDateSortOrder, cb, criteriaQuery, ClaimAppRoot);

		if (sortDTO != null) {

			Path<String> sortPath = getSortPathForMyClaim(sortDTO, ClaimAppRoot, claimtempJoin, leaveStatusJoin);
			if (sortPath != null) {
				if (SortConstants.DB_ORDER_BY_ASC.equalsIgnoreCase(sortDTO.getOrderType())) {
					criteriaQuery.orderBy(cb.asc(sortPath));
				}
				if (SortConstants.DB_ORDER_BY_DESC.equalsIgnoreCase(sortDTO.getOrderType())) {
					criteriaQuery.orderBy(cb.desc(sortPath));
				}
			}
		}
		
		TypedQuery<ClaimApplication> claimAppTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			claimAppTypedQuery.setFirstResult(getStartPosition(pageDTO));
			claimAppTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		return claimAppTypedQuery.getResultList();
	}

	@Override
	public Integer getCountByConditionForEmployee(Long employeeId, List<String> claimStatus, String fromDate,
			String toDate, AddClaimConditionDTO conditionDTO) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Integer> criteriaQuery = cb.createQuery(Integer.class);
		Root<ClaimApplication> ClaimAppRoot = criteriaQuery.from(ClaimApplication.class);
		criteriaQuery.select(cb.count(ClaimAppRoot).as(Integer.class));

		Predicate restriction = cb.conjunction();

		Join<ClaimApplication, Employee> empJoin = ClaimAppRoot.join(ClaimApplication_.employee);

		Join<ClaimApplication, EmployeeClaimTemplate> empClaimTempJoin = ClaimAppRoot
				.join(ClaimApplication_.employeeClaimTemplate);
		Join<EmployeeClaimTemplate, ClaimTemplate> claimtempJoin = empClaimTempJoin
				.join(EmployeeClaimTemplate_.claimTemplate);
		Join<ClaimTemplate, AppCodeMaster> appCodeJoin = claimtempJoin.join(ClaimTemplate_.frontEndViewMode,
				JoinType.LEFT);

		Join<ClaimApplication, ClaimStatusMaster> leaveStatusJoin = ClaimAppRoot
				.join(ClaimApplication_.claimStatusMaster);

		restriction = cb.and(restriction, cb.equal(empJoin.get(Employee_.employeeId), employeeId));
		restriction = cb.and(restriction,
				cb.notEqual(
						cb.coalesce(appCodeJoin.get(AppCodeMaster_.codeDesc),
								PayAsiaConstants.CLAIM_TEMPLATE_VIEW_MODE_ON),
						PayAsiaConstants.CLAIM_TEMPLATE_VIEW_MODE_OFF));

		if (StringUtils.isNotBlank(conditionDTO.getCreatedDate())) {
			restriction = cb.and(restriction, cb.equal(ClaimAppRoot.get(ClaimApplication_.createdDate).as(Date.class),
					DateUtils.stringToDate(conditionDTO.getCreatedDate())));

		}

		if (StringUtils.isNotBlank(conditionDTO.getClaimGroup())) {

			restriction = cb.and(restriction, cb.like(cb.upper(claimtempJoin.get(ClaimTemplate_.templateName)),
					conditionDTO.getClaimGroup().toUpperCase()));

		}

		if (conditionDTO.getClaimNumber() != null) {

			restriction = cb.and(restriction, cb.like(ClaimAppRoot.get(ClaimApplication_.claimNumber).as(String.class),
					'%' + String.valueOf(conditionDTO.getClaimNumber()) + '%'));

		}
		if (!claimStatus.isEmpty()) {
			restriction = cb.and(restriction, leaveStatusJoin.get(ClaimStatusMaster_.claimStatusName).in(claimStatus));
		}
		if (StringUtils.isNotBlank(fromDate)) {
			restriction = cb.and(restriction,
					cb.greaterThanOrEqualTo((ClaimAppRoot.get(ClaimApplication_.createdDate).as(Date.class)),
							DateUtils.stringToTimestamp(fromDate)));
		}
		if (StringUtils.isNotBlank(toDate)) {
			restriction = cb.and(restriction,
					cb.lessThanOrEqualTo((ClaimAppRoot.get(ClaimApplication_.createdDate).as(Date.class)),
							DateUtils.stringToTimestamp(toDate)));
		}
		
		restriction = cb.and(restriction, cb.equal((ClaimAppRoot.get(ClaimApplication_.visibleToEmployee)), true));

		criteriaQuery.where(restriction);

		TypedQuery<Integer> claimAppTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		return claimAppTypedQuery.getSingleResult();
	}

	@Override
	public Integer getCountByConditionForAdmin(Long employeeId, List<String> claimStatus, String fromDate,
			String toDate) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Integer> criteriaQuery = cb.createQuery(Integer.class);
		Root<ClaimApplication> ClaimAppRoot = criteriaQuery.from(ClaimApplication.class);
		criteriaQuery.select(cb.count(ClaimAppRoot).as(Integer.class));

		Predicate restriction = cb.conjunction();

		Join<ClaimApplication, Employee> empJoin = ClaimAppRoot.join(ClaimApplication_.employee);

		Join<ClaimApplication, EmployeeClaimTemplate> empClaimTempJoin = ClaimAppRoot
				.join(ClaimApplication_.employeeClaimTemplate);
		Join<EmployeeClaimTemplate, ClaimTemplate> claimtempJoin = empClaimTempJoin
				.join(EmployeeClaimTemplate_.claimTemplate);
		Join<ClaimTemplate, AppCodeMaster> appCodeJoin = claimtempJoin.join(ClaimTemplate_.backEndViewMode,
				JoinType.LEFT);

		Join<ClaimApplication, ClaimStatusMaster> leaveStatusJoin = ClaimAppRoot
				.join(ClaimApplication_.claimStatusMaster);

		restriction = cb.and(restriction, cb.equal(empJoin.get(Employee_.employeeId), employeeId));
		restriction = cb.and(restriction,
				cb.notEqual(
						cb.coalesce(appCodeJoin.get(AppCodeMaster_.codeDesc),
								PayAsiaConstants.CLAIM_TEMPLATE_VIEW_MODE_ON),
						PayAsiaConstants.CLAIM_TEMPLATE_VIEW_MODE_OFF));

		if (!claimStatus.isEmpty()) {
			restriction = cb.and(restriction, leaveStatusJoin.get(ClaimStatusMaster_.claimStatusName).in(claimStatus));
		}
		if (StringUtils.isNotBlank(fromDate)) {
			restriction = cb.and(restriction,
					cb.greaterThanOrEqualTo((ClaimAppRoot.get(ClaimApplication_.createdDate).as(Date.class)),
							DateUtils.stringToTimestamp(fromDate)));
		}
		if (StringUtils.isNotBlank(toDate)) {
			restriction = cb.and(restriction,
					cb.lessThanOrEqualTo((ClaimAppRoot.get(ClaimApplication_.createdDate).as(Date.class)),
							DateUtils.stringToTimestamp(toDate)));
		}

		criteriaQuery.where(restriction);

		TypedQuery<Integer> claimAppTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		return claimAppTypedQuery.getSingleResult();
	}

	@Override
	public ValidateClaimApplicationDTO validateClaimApplication(final Long employeeClaimAppId,
			final Long employeeClaimTemplateId, final boolean isAdmin) {

		final ValidateClaimApplicationDTO validateClaimApplicationDTO = new ValidateClaimApplicationDTO();

		getSession().doWork(new Work() {

			@Override
			public void execute(Connection connection) throws SQLException {
				try {
					CallableStatement cstmt = null;
					cstmt = connection.prepareCall("{call Validate_Claim_Application (?,?,?,?,?,?)}");

					if (employeeClaimAppId != null) {
						cstmt.setLong("@Employee_Claim_Application_Id", employeeClaimAppId);
					} else {
						cstmt.setNull("@Employee_Claim_Application_Id", java.sql.Types.BIGINT);

					}

					if (employeeClaimTemplateId != null) {
						cstmt.setLong("@Employee_Claim_Template_ID", employeeClaimTemplateId);
					} else {
						cstmt.setNull("@Employee_Claim_Template_ID", java.sql.Types.BIGINT);

					}
					cstmt.setBoolean("@Is_Admin", isAdmin);
					cstmt.registerOutParameter("@Error_Code", java.sql.Types.INTEGER);
					cstmt.registerOutParameter("@Error_Key", java.sql.Types.VARCHAR);
					cstmt.registerOutParameter("@Error_Value", java.sql.Types.VARCHAR);

					cstmt.execute();

					validateClaimApplicationDTO.setErrorCode(cstmt.getInt("@Error_Code"));
					validateClaimApplicationDTO.setErrorKey(cstmt.getString("@Error_Key"));
					validateClaimApplicationDTO.setErrorValue(cstmt.getString("@Error_Value"));

				} catch (SQLException e) {
					throw new PayAsiaSystemException(e.getMessage(), e);
				}

			}
		});
		return validateClaimApplicationDTO;

	}

	@Override
	public List<EmployeeWiseConsolidatedClaimReportDataDTO> getEmloyeeWiseConsClaimReportProc(final Long companyId,
			final int year, final Long claimTemplateId, final String claimItemIdList, final Boolean visibleToEmployee,
			final Boolean orderBy, final String employeeIdList, final boolean isIncludeResignedEmployees) {
		final List<EmployeeWiseConsolidatedClaimReportDataDTO> empWiseDataDTOs = new ArrayList<>();

		getSession().doWork(new Work() {

			@Override
			public void execute(Connection connection) throws SQLException {
				try {
					CallableStatement cstmt = null;
					cstmt = connection.prepareCall("{call Employee_Wise_Consolidated_Claim_Report (?,?,?,?,?,?,?,?)}");

					cstmt.setLong("@Company_ID", companyId);
					cstmt.setInt("@Year", year);
					cstmt.setLong("@Claim_Template_ID", claimTemplateId);
					cstmt.setString("@Claim_Item_ID_List", claimItemIdList);
					if (visibleToEmployee == null) {
						cstmt.setNull("@Visible_To_Employee", java.sql.Types.BIT);
					} else {
						cstmt.setBoolean("@Visible_To_Employee", visibleToEmployee);
					}

					cstmt.setBoolean("@Order_By_Employee_No", orderBy);
					cstmt.setString("@Employee_ID_List", employeeIdList);
					cstmt.setBoolean("@Include_Resigned_Employee", isIncludeResignedEmployees);
					ResultSet rs = cstmt.executeQuery();
					if (rs != null) {
						int count = 1;
						while (rs.next()) {
							EmployeeWiseConsolidatedClaimReportDataDTO empWiseDataDTO = new EmployeeWiseConsolidatedClaimReportDataDTO();
							empWiseDataDTO.setSerialNum(count);

							empWiseDataDTO.setEmployeeId(rs.getLong("Employee_ID"));
							empWiseDataDTO.setEmployeeNo(rs.getString("Employee_Number"));
							empWiseDataDTO
									.setEmployeeName(rs.getString("First_Name") + " " + rs.getString("Last_Name"));
							empWiseDataDTO.setClaimTemplateName(rs.getString("Claim_Template_Name"));
							empWiseDataDTO.setClaimItemName(rs.getString("Claim_Item_Name"));
							empWiseDataDTO.setClaimItemId(Long.valueOf(rs.getString("Claim_Item_ID")));
							empWiseDataDTO.setEntitlement(rs.getString("Entitlement"));
							empWiseDataDTO.setClaimed(rs.getString("Claimed"));
							empWiseDataDTO.setEntitlementBalance(rs.getString("Entitlement_Balance"));
							count++;
							empWiseDataDTOs.add(empWiseDataDTO);
						}
					}
				} catch (SQLException e) {
					throw new PayAsiaSystemException(e.getMessage(), e);
				}

			}
		});

		return empWiseDataDTOs;
	}

	@Override
	public List<EmployeeWiseTemplateClaimReportDataDTO> getEmloyeeWiseTemplateClaimReportProc(final Long companyId,
			final int year, final Long claimTemplateId, final String employeeIdList) {
		final List<EmployeeWiseTemplateClaimReportDataDTO> empWiseTemplateDataDTOs = new ArrayList<>();

		getSession().doWork(new Work() {

			@Override
			public void execute(Connection connection) throws SQLException {
				try {
					CallableStatement cstmt = null;
					cstmt = connection.prepareCall("{call Employee_Wise_Template_Claim_Report (?,?,?,?)}");

					cstmt.setLong("@Company_ID", companyId);
					cstmt.setInt("@Year", year);
					cstmt.setLong("@Claim_Template_ID", claimTemplateId);
					cstmt.setString("@Employee_ID_List", employeeIdList);

					ResultSet rs = cstmt.executeQuery();
					if (rs != null) {
						int count = 1;
						while (rs.next()) {

							EmployeeWiseTemplateClaimReportDataDTO empWiseTemplateDataDTO = new EmployeeWiseTemplateClaimReportDataDTO();
							empWiseTemplateDataDTO.setSerialNum(count);
							empWiseTemplateDataDTO.setEmployeeId(Long.valueOf(rs.getString("Employee_ID")));
							empWiseTemplateDataDTO.setEmployeeNo(rs.getString("Employee_Number"));
							empWiseTemplateDataDTO
									.setEmployeeName(rs.getString("First_Name") + " " + rs.getString("Last_Name"));

							empWiseTemplateDataDTO.setClaimTemplateName(rs.getString("Claim_Template_Name"));
							empWiseTemplateDataDTO.setClaimItemName(rs.getString("Claim_Item_Name"));
							empWiseTemplateDataDTO.setEntitlement(rs.getString("Entitlement"));
							empWiseTemplateDataDTO.setClaimed(rs.getString("Claimed"));
							empWiseTemplateDataDTO.setEntitlementBalance(rs.getString("Entitlement_Balance"));
							empWiseTemplateDataDTO.setClaimItemId(rs.getLong("Claim_Item_Id"));
							empWiseTemplateDataDTO
									.setClaimBalanceFromOtherCT(rs.getBoolean("Claim_Balance_From_Other_CT"));

							empWiseTemplateDataDTOs.add(empWiseTemplateDataDTO);
						}
					}
				} catch (SQLException e) {
					throw new PayAsiaSystemException(e.getMessage(), e);
				}

			}
		});

		return empWiseTemplateDataDTOs;
	}

	@Override
	public List<MonthlyConsolidatedFinanceReportDataDTO> getMonthlyConsFinanceReportProc(final Long companyId,
			final Timestamp fromDate, final Timestamp toDate, final String claimTemplateIdList,
			final String claimItemIdList, final Boolean orderBy, final String employeeIdList,
			final boolean isIncludeResignedEmployees) {
		final List<MonthlyConsolidatedFinanceReportDataDTO> monthlyConsTemplateDataDTOs = new ArrayList<>();

		getSession().doWork(new Work() {

			@Override
			public void execute(Connection connection) throws SQLException {
				try {
					CallableStatement cstmt = null;
					cstmt = connection
							.prepareCall("{call Monthly_Consolidated_Claim_Finance_Report (?,?,?,?,?,?,?,?)}");

					cstmt.setLong("@Company_ID", companyId);
					cstmt.setTimestamp("@From_Date", fromDate);
					cstmt.setTimestamp("@To_Date", toDate);

					cstmt.setString("@Claim_Template_ID_List", claimTemplateIdList);
					cstmt.setString("@Claim_Item_ID_List", claimItemIdList);

					cstmt.setBoolean("@Order_By_Employee_No", orderBy);
					cstmt.setString("@Employee_ID_List", employeeIdList);
					cstmt.setBoolean("@Include_Resigned_Employee", isIncludeResignedEmployees);

					ResultSet rs = cstmt.executeQuery();
					if (rs != null) {
						int count = 1;
						while (rs.next()) {
							MonthlyConsolidatedFinanceReportDataDTO monthlyConsTemplateDataDTO = new MonthlyConsolidatedFinanceReportDataDTO();
							monthlyConsTemplateDataDTO.setSerialNum(count);
							monthlyConsTemplateDataDTO.setEmployeeId(rs.getLong("Employee_ID"));
							monthlyConsTemplateDataDTO.setEmployeeNo(rs.getString("Employee_Number"));
							String lastName = rs.getString("Last_Name");
							if (StringUtils.isBlank(lastName) || lastName.equalsIgnoreCase("null")) {
								lastName = "";
							}
							monthlyConsTemplateDataDTO.setEmployeeName(rs.getString("First_Name") + " " + lastName);
							monthlyConsTemplateDataDTO.setClaimTemplateName(rs.getString("Template_Name"));
							monthlyConsTemplateDataDTO.setClaimItemName(rs.getString("Claim_Item_Name"));
							monthlyConsTemplateDataDTO.setCurrency(rs.getString("Currency"));
							monthlyConsTemplateDataDTO.setSubTotal(rs.getDouble("Sub_Total"));
							count++;
							monthlyConsTemplateDataDTOs.add(monthlyConsTemplateDataDTO);
						}
					}
				} catch (SQLException e) {
					throw new PayAsiaSystemException(e.getMessage(), e);
				}

			}
		});

		return monthlyConsTemplateDataDTOs;
	}

	@Override
	public List<Tuple> getCategoryWiseTotalCount(Long claimApplicationId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Tuple> criteriaQuery = cb.createQuery(Tuple.class);
		Root<ClaimApplication> claimApplicationRoot = criteriaQuery.from(ClaimApplication.class);

		Join<ClaimApplication, ClaimApplicationItem> claimAppItemJoin = claimApplicationRoot
				.join(ClaimApplication_.claimApplicationItems);
		Join<ClaimApplicationItem, EmployeeClaimTemplateItem> empClaimTempItemJoin = claimAppItemJoin
				.join(ClaimApplicationItem_.employeeClaimTemplateItem);
		Join<EmployeeClaimTemplateItem, ClaimTemplateItem> claimTempItemJoin = empClaimTempItemJoin
				.join(EmployeeClaimTemplateItem_.claimTemplateItem);
		Join<ClaimTemplateItem, ClaimItemMaster> claimItemJoin = claimTempItemJoin
				.join(ClaimTemplateItem_.claimItemMaster);
		Join<ClaimItemMaster, ClaimCategoryMaster> claimCategoryJoin = claimItemJoin
				.join(ClaimItemMaster_.claimCategoryMaster);

		List<Selection<?>> selectionItems = new ArrayList<Selection<?>>();

		selectionItems.add(claimCategoryJoin.get(ClaimCategoryMaster_.claimCategoryName)
				.alias(getAlias(ClaimCategoryMaster_.claimCategoryName)));
		selectionItems.add(cb.sum(claimAppItemJoin.get(ClaimApplicationItem_.claimAmount))
				.alias(getAlias(ClaimApplicationItem_.claimAmount)));

		criteriaQuery.multiselect(selectionItems);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction,
				cb.equal(claimApplicationRoot.get(ClaimApplication_.claimApplicationId), claimApplicationId));

		restriction = cb.and(restriction, cb.equal(claimAppItemJoin.get(ClaimApplicationItem_.active), true));

		criteriaQuery.where(restriction);
		criteriaQuery.groupBy(claimCategoryJoin.get(ClaimCategoryMaster_.claimCategoryName));

		TypedQuery<Tuple> companyTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		List<Tuple> categoryAmountCountList = companyTypedQuery.getResultList();
		return categoryAmountCountList;
	}

	@Override
	public ClaimApplication findByClaimApplicationID(AddClaimDTO addClaimDTO) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ClaimApplication> criteriaQuery = cb.createQuery(ClaimApplication.class);
		Root<ClaimApplication> root = criteriaQuery.from(ClaimApplication.class);
		criteriaQuery.select(root);

		Predicate restriction = cb.conjunction();
		Join<ClaimApplication, Company> companyJoin = root.join(ClaimApplication_.company);

		restriction = cb.and(restriction,
				cb.equal(root.get(ClaimApplication_.claimApplicationId), addClaimDTO.getClaimApplicationId()));

		restriction = cb.and(restriction, cb.equal(companyJoin.get(Company_.companyId), addClaimDTO.getCompanyId()));

		if (!addClaimDTO.getAdmin()) {
			Join<ClaimApplication, Employee> claimAppJoin = root.join(ClaimApplication_.employee);
			restriction = cb.and(restriction,
					cb.equal(claimAppJoin.get(Employee_.employeeId), addClaimDTO.getEmployeeId()));
		}

		criteriaQuery.where(restriction);

		TypedQuery<ClaimApplication> claimAppTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		List<ClaimApplication> list = claimAppTypedQuery.getResultList();
		return (list != null && !list.isEmpty()) ? list.get(0) : null;
	}

	@Override
	public List<ClaimApplication> findByCondition2(AddClaimConditionDTO conditionDTO, PageRequest pageDTO,
			SortCondition sortDTO, int position) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ClaimApplication> criteriaQuery = cb.createQuery(ClaimApplication.class);
		Root<ClaimApplication> claimApplicationRoot = criteriaQuery.from(ClaimApplication.class);
		criteriaQuery.select(claimApplicationRoot);

		Predicate restriction = cb.conjunction();

		Join<ClaimApplication, Employee> empLeaveJoin = claimApplicationRoot.join(ClaimApplication_.employee);

		Join<ClaimApplication, ClaimStatusMaster> leaveStatusJoin = claimApplicationRoot
				.join(ClaimApplication_.claimStatusMaster);

		Join<ClaimApplication, EmployeeClaimTemplate> employeeClaimTempJoin = claimApplicationRoot
				.join(ClaimApplication_.employeeClaimTemplate);

		Join<EmployeeClaimTemplate, ClaimTemplate> claimTempJoin = employeeClaimTempJoin
				.join(EmployeeClaimTemplate_.claimTemplate);

		Join<ClaimTemplate, AppCodeMaster> appCodeJoin = claimTempJoin.join(ClaimTemplate_.frontEndViewMode,
				JoinType.LEFT);

		restriction = cb.and(restriction,
				cb.notEqual(
						cb.coalesce(appCodeJoin.get(AppCodeMaster_.codeDesc),
								PayAsiaConstants.CLAIM_TEMPLATE_VIEW_MODE_ON),
						PayAsiaConstants.CLAIM_TEMPLATE_VIEW_MODE_OFF));

		if (StringUtils.isNotBlank(conditionDTO.getCreatedDate())) {
			restriction = cb.and(restriction,
					cb.equal(claimApplicationRoot.get(ClaimApplication_.createdDate).as(Date.class),
							DateUtils.stringToDate(conditionDTO.getCreatedDate())));

		}
		if (conditionDTO.getVisibleToEmployee() != null) {
			restriction = cb.and(restriction, cb.equal(claimApplicationRoot.get(ClaimApplication_.visibleToEmployee),
					conditionDTO.getVisibleToEmployee()));

		}

		if (StringUtils.isNotBlank(conditionDTO.getClaimGroup())) {

			restriction = cb.and(restriction, cb.like(cb.upper(claimTempJoin.get(ClaimTemplate_.templateName)),
					conditionDTO.getClaimGroup().toUpperCase()));

		}

		if (conditionDTO.getClaimNumber() != null) {
			restriction = cb.and(restriction,
					cb.like(claimApplicationRoot.get(ClaimApplication_.claimNumber).as(String.class),
							'%' + String.valueOf(conditionDTO.getClaimNumber()) + '%'));

		}

		if (conditionDTO.getEmployeeId() != null) {
			restriction = cb.and(restriction,
					cb.equal(empLeaveJoin.get(Employee_.employeeId), conditionDTO.getEmployeeId()));
		}

		if (conditionDTO.getClaimStatus().size() > 0) {
			restriction = cb.and(restriction,
					leaveStatusJoin.get(ClaimStatusMaster_.claimStatusName).in(conditionDTO.getClaimStatus()));

		}
		if (conditionDTO.getFromDate() != null) {
			restriction = cb.and(restriction, cb.greaterThanOrEqualTo(
					(claimApplicationRoot.get(ClaimApplication_.createdDate)), conditionDTO.getFromDate()));
		}
		if (conditionDTO.getToDate() != null) {
			restriction = cb.and(restriction, cb.lessThanOrEqualTo(
					(claimApplicationRoot.get(ClaimApplication_.createdDate)), conditionDTO.getToDate()));
		}

		criteriaQuery.where(restriction);

		if (StringUtils.isBlank(conditionDTO.getClaimNumberSortOrder())
				&& StringUtils.isBlank(conditionDTO.getCreatedDateSortOrder())) {
			criteriaQuery.orderBy(cb.desc(claimApplicationRoot.get(ClaimApplication_.updatedDate)));
		} else {
			getClaimGridSortOrder(conditionDTO.getClaimNumberSortOrder(), conditionDTO.getCreatedDateSortOrder(), cb,
					criteriaQuery, claimApplicationRoot);
		}

		TypedQuery<ClaimApplication> claimAppTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			claimAppTypedQuery.setFirstResult(position);
			claimAppTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		return claimAppTypedQuery.getResultList();
	}

}
