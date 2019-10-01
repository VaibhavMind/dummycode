package com.payasia.dao.impl;

import java.lang.reflect.InvocationTargetException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.payasia.common.dto.CoherentConditionDTO;
import com.payasia.common.dto.LundinPendingTsheetConditionDTO;
import com.payasia.common.dto.PendingOTTimesheetConditionDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.NullAwareBeanUtilsBean;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.CoherentOvertimeApplicationDAO;
import com.payasia.dao.bean.CoherentOvertimeApplication;
import com.payasia.dao.bean.CoherentOvertimeApplicationReviewer;
import com.payasia.dao.bean.CoherentOvertimeApplicationReviewer_;
import com.payasia.dao.bean.CoherentOvertimeApplication_;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeTimesheetApplication_;
import com.payasia.dao.bean.Employee_;
import com.payasia.dao.bean.TimesheetBatch;
import com.payasia.dao.bean.TimesheetBatch_;
import com.payasia.dao.bean.TimesheetStatusMaster;
import com.payasia.dao.bean.TimesheetStatusMaster_;

@Repository
public class CoherentOvertimeApplicationDAOImpl extends BaseDAO implements CoherentOvertimeApplicationDAO {
	@Override
	protected Object getBaseEntity() {
		CoherentOvertimeApplication timesheet = new CoherentOvertimeApplication();
		return timesheet;
	}

	@Override
	public CoherentOvertimeApplication findById(long id) {
		return super.findById(CoherentOvertimeApplication.class, id);
	}

	@Override
	public void save(CoherentOvertimeApplication coherentOvertimeApplication) {
		super.save(coherentOvertimeApplication);
	}

	@Override
	public void update(CoherentOvertimeApplication coherentOvertimeApplication) {
		super.update(coherentOvertimeApplication);

	}

	@Override
	public CoherentOvertimeApplication saveAndReturn(CoherentOvertimeApplication coherentOvertimeApplication) {
		CoherentOvertimeApplication persistObj = coherentOvertimeApplication;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (CoherentOvertimeApplication) getBaseEntity();
			beanUtil.copyProperties(persistObj, coherentOvertimeApplication);
		} catch (IllegalAccessException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		} catch (InvocationTargetException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		}
		this.entityManagerFactory.persist(persistObj);
		this.entityManagerFactory.flush();
		return persistObj;
	}

	@Override
	public List<CoherentOvertimeApplication> findByTimesheetBatchId(long timesheetBatchId, long companyId,
			long employeeId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CoherentOvertimeApplication> criteriaQuery = cb.createQuery(CoherentOvertimeApplication.class);
		Root<CoherentOvertimeApplication> root = criteriaQuery.from(CoherentOvertimeApplication.class);
		criteriaQuery.select(root);

		Predicate restriction = cb.conjunction();

		Join<CoherentOvertimeApplication, Company> companyJoin = root.join(CoherentOvertimeApplication_.company);

		Join<CoherentOvertimeApplication, Employee> employeeJoin = root.join(CoherentOvertimeApplication_.employee);

		Join<CoherentOvertimeApplication, TimesheetBatch> timesheetBatchJoin = root
				.join(CoherentOvertimeApplication_.timesheetBatch);

		restriction = cb.and(restriction, cb.equal(companyJoin.get(Company_.companyId), companyId));

		restriction = cb.and(restriction, cb.equal(employeeJoin.get(Employee_.employeeId), employeeId));

		restriction = cb.and(restriction,
				cb.equal(timesheetBatchJoin.get(TimesheetBatch_.timesheetBatchId), timesheetBatchId));

		criteriaQuery.where(restriction);

		TypedQuery<CoherentOvertimeApplication> claimAppTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		return (List<CoherentOvertimeApplication>) claimAppTypedQuery.getResultList();

	}

	@Override
	public List<CoherentOvertimeApplication> findByCondition(CoherentConditionDTO conditionDTO, PageRequest pageDTO,
			SortCondition sortDTO, Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CoherentOvertimeApplication> criteriaQuery = cb.createQuery(CoherentOvertimeApplication.class);
		Root<CoherentOvertimeApplication> root = criteriaQuery.from(CoherentOvertimeApplication.class);
		criteriaQuery.select(root);

		Predicate restriction = cb.conjunction();

		Join<CoherentOvertimeApplication, Employee> empLeaveJoin = root.join(CoherentOvertimeApplication_.employee);
		Join<CoherentOvertimeApplication, TimesheetStatusMaster> leaveStatusJoin = root
				.join(CoherentOvertimeApplication_.timesheetStatusMaster);
		Join<CoherentOvertimeApplication, TimesheetBatch> batchJoin = root
				.join(CoherentOvertimeApplication_.timesheetBatch);

		restriction = cb.and(restriction,
				cb.equal(root.get(CoherentOvertimeApplication_.company).get(Company_.companyId), companyId));
		if (StringUtils.isNotBlank(conditionDTO.getCreatedDate())) {
			restriction = cb.and(restriction,
					cb.equal(root.get(CoherentOvertimeApplication_.createdDate).as(Date.class),
							DateUtils.stringToDate(conditionDTO.getCreatedDate())));

		}
		if (conditionDTO.getBatch() != null) {
			restriction = cb.and(restriction,
					cb.like(batchJoin.get(TimesheetBatch_.timesheetBatchDesc), '%' + conditionDTO.getBatch() + '%'));
		}
		if (StringUtils.isNotBlank(conditionDTO.getReviewer())) {
			Join<CoherentOvertimeApplication, CoherentOvertimeApplicationReviewer> reviewersJoin = root
					.join(CoherentOvertimeApplication_.coherentOvertimeApplicationReviewers);
			restriction = cb.and(restriction,
					cb.or(cb.like(reviewersJoin.get(CoherentOvertimeApplicationReviewer_.employeeReviewer)
							.get(Employee_.firstName), '%' + conditionDTO.getReviewer() + '%'),
							cb.like(reviewersJoin.get(CoherentOvertimeApplicationReviewer_.employeeReviewer)
									.get(Employee_.lastName), '%' + conditionDTO.getReviewer() + '%')));
		}

		if (conditionDTO.getEmployeeId() != null) {
			restriction = cb.and(restriction,
					cb.equal(empLeaveJoin.get(Employee_.employeeId), conditionDTO.getEmployeeId()));
		}

		if (conditionDTO.getClaimStatus().size() > 0) {
			restriction = cb.and(restriction,
					leaveStatusJoin.get(TimesheetStatusMaster_.timesheetStatusName).in(conditionDTO.getClaimStatus()));

		}
		if (conditionDTO.getFromDate() != null) {
			restriction = cb.and(restriction, cb.greaterThanOrEqualTo(
					(root.get(CoherentOvertimeApplication_.createdDate).as(Date.class)), conditionDTO.getFromDate()));
		}
		if (conditionDTO.getToDate() != null) {
			restriction = cb.and(restriction, cb.lessThanOrEqualTo(
					(root.get(CoherentOvertimeApplication_.createdDate).as(Date.class)), conditionDTO.getToDate()));
		}

		criteriaQuery.where(restriction);

		criteriaQuery.orderBy(cb.desc(batchJoin.get(TimesheetBatch_.startDate)));

		TypedQuery<CoherentOvertimeApplication> claimAppTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			claimAppTypedQuery.setFirstResult(getStartPosition(pageDTO));
			claimAppTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		return claimAppTypedQuery.getResultList();
	}

	@Override
	public Long getCountForCondition(CoherentConditionDTO conditionDTO, Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
		Root<CoherentOvertimeApplication> root = criteriaQuery.from(CoherentOvertimeApplication.class);
		criteriaQuery.select(cb.count(root));

		Predicate restriction = cb.conjunction();

		Join<CoherentOvertimeApplication, Employee> empLeaveJoin = root.join(CoherentOvertimeApplication_.employee);

		Join<CoherentOvertimeApplication, TimesheetStatusMaster> leaveStatusJoin = root
				.join(CoherentOvertimeApplication_.timesheetStatusMaster);
		Join<CoherentOvertimeApplication, TimesheetBatch> batchJoin = root
				.join(CoherentOvertimeApplication_.timesheetBatch);

		restriction = cb.and(restriction,
				cb.equal(root.get(CoherentOvertimeApplication_.company).get(Company_.companyId), companyId));
		if (StringUtils.isNotBlank(conditionDTO.getCreatedDate())) {
			restriction = cb.and(restriction,
					cb.equal(root.get(CoherentOvertimeApplication_.createdDate).as(Date.class),
							DateUtils.stringToDate(conditionDTO.getCreatedDate())));

		}
		if (StringUtils.isNotBlank(conditionDTO.getBatch())) {
			restriction = cb.and(restriction,
					cb.like(batchJoin.get(TimesheetBatch_.timesheetBatchDesc), '%' + conditionDTO.getBatch() + '%'));
		}
		if (StringUtils.isNotBlank(conditionDTO.getReviewer())) {
			Join<CoherentOvertimeApplication, CoherentOvertimeApplicationReviewer> reviewersJoin = root
					.join(CoherentOvertimeApplication_.coherentOvertimeApplicationReviewers);
			restriction = cb.and(restriction,
					cb.or(cb.like(reviewersJoin.get(CoherentOvertimeApplicationReviewer_.employeeReviewer)
							.get(Employee_.firstName), '%' + conditionDTO.getReviewer() + '%'),
							cb.like(reviewersJoin.get(CoherentOvertimeApplicationReviewer_.employeeReviewer)
									.get(Employee_.lastName), '%' + conditionDTO.getReviewer() + '%')));
		}

		if (conditionDTO.getEmployeeId() != null) {
			restriction = cb.and(restriction,
					cb.equal(empLeaveJoin.get(Employee_.employeeId), conditionDTO.getEmployeeId()));
		}

		if (conditionDTO.getClaimStatus().size() > 0) {
			restriction = cb.and(restriction,
					leaveStatusJoin.get(TimesheetStatusMaster_.timesheetStatusName).in(conditionDTO.getClaimStatus()));

		}
		if (conditionDTO.getFromDate() != null) {
			restriction = cb.and(restriction, cb.greaterThanOrEqualTo(
					(root.get(CoherentOvertimeApplication_.createdDate).as(Date.class)), conditionDTO.getFromDate()));
		}
		if (conditionDTO.getToDate() != null) {
			restriction = cb.and(restriction, cb.lessThanOrEqualTo(
					(root.get(CoherentOvertimeApplication_.createdDate).as(Date.class)), conditionDTO.getToDate()));
		}

		criteriaQuery.where(restriction);

		TypedQuery<Long> claimAppTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		return claimAppTypedQuery.getSingleResult();
	}

	@Override
	public List<Long> findByBatchId(Long employeeId, List<Long> batchIdList, List<String> statusList) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
		Root<CoherentOvertimeApplication> root = criteriaQuery.from(CoherentOvertimeApplication.class);
		criteriaQuery.select(root.get(CoherentOvertimeApplication_.timesheetBatch).get(TimesheetBatch_.timesheetBatchId)
				.as(Long.class));

		Predicate restriction = cb.conjunction();

		Join<CoherentOvertimeApplication, Employee> empLeaveJoin = root.join(CoherentOvertimeApplication_.employee);
		Join<CoherentOvertimeApplication, TimesheetBatch> batchJoin = root
				.join(CoherentOvertimeApplication_.timesheetBatch);
		Join<CoherentOvertimeApplication, TimesheetStatusMaster> leaveStatusJoin = root
				.join(CoherentOvertimeApplication_.timesheetStatusMaster);

		restriction = cb.and(restriction, cb.equal(empLeaveJoin.get(Employee_.employeeId), employeeId));
		restriction = cb.and(restriction,
				cb.not(leaveStatusJoin.get(TimesheetStatusMaster_.timesheetStatusName).in(statusList)));
		restriction = cb.and(restriction, batchJoin.get(TimesheetBatch_.timesheetBatchId).in(batchIdList));

		criteriaQuery.where(restriction);

		criteriaQuery.orderBy(cb.desc(root.get(CoherentOvertimeApplication_.updatedDate)));

		TypedQuery<Long> claimAppTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		return claimAppTypedQuery.getResultList();
	}

	@Override
	public List<CoherentOvertimeApplication> findByConditionForEmployee(PageRequest pageDTO, SortCondition sortDTO,
			Long employeeId, String fromDate, String toDate, Boolean visibleToEmployee,
			CoherentConditionDTO conditionDTO) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CoherentOvertimeApplication> criteriaQuery = cb.createQuery(CoherentOvertimeApplication.class);
		Root<CoherentOvertimeApplication> otTimesheetRoot = criteriaQuery.from(CoherentOvertimeApplication.class);
		criteriaQuery.select(otTimesheetRoot);

		Predicate restriction = cb.conjunction();

		Join<CoherentOvertimeApplication, Employee> empJoin = otTimesheetRoot
				.join(CoherentOvertimeApplication_.employee);
		Join<CoherentOvertimeApplication, TimesheetBatch> batchJoin = otTimesheetRoot
				.join(CoherentOvertimeApplication_.timesheetBatch);
		restriction = cb.and(restriction, cb.equal(empJoin.get(Employee_.employeeId), employeeId));

		if (StringUtils.isNotBlank(conditionDTO.getCreatedDate())) {
			restriction = cb.and(restriction,
					cb.equal(otTimesheetRoot.get(CoherentOvertimeApplication_.createdDate).as(Date.class),
							DateUtils.stringToDate(conditionDTO.getCreatedDate())));

		}
		if (StringUtils.isNotBlank(conditionDTO.getBatch())) {
			restriction = cb.and(restriction,
					cb.like(batchJoin.get(TimesheetBatch_.timesheetBatchDesc), '%' + conditionDTO.getBatch() + '%'));
		}
		if (StringUtils.isNotBlank(conditionDTO.getReviewer())) {
			Join<CoherentOvertimeApplication, CoherentOvertimeApplicationReviewer> reviewersJoin = otTimesheetRoot
					.join(CoherentOvertimeApplication_.coherentOvertimeApplicationReviewers);
			restriction = cb.and(restriction,
					cb.or(cb.like(reviewersJoin.get(CoherentOvertimeApplicationReviewer_.employeeReviewer)
							.get(Employee_.firstName), '%' + conditionDTO.getReviewer() + '%'),
							cb.like(reviewersJoin.get(CoherentOvertimeApplicationReviewer_.employeeReviewer)
									.get(Employee_.lastName), '%' + conditionDTO.getReviewer() + '%')));
		}

		if (conditionDTO.getEmployeeId() != null) {
			restriction = cb.and(restriction,
					cb.equal(empJoin.get(Employee_.employeeId), conditionDTO.getEmployeeId()));
		}

		if (conditionDTO.getFromDate() != null) {
			restriction = cb.and(restriction,
					cb.greaterThanOrEqualTo(
							(otTimesheetRoot.get(CoherentOvertimeApplication_.createdDate).as(Date.class)),
							conditionDTO.getFromDate()));
		}
		if (conditionDTO.getToDate() != null) {
			restriction = cb.and(restriction,
					cb.lessThanOrEqualTo((otTimesheetRoot.get(CoherentOvertimeApplication_.createdDate).as(Date.class)),
							conditionDTO.getToDate()));
		}
		criteriaQuery.where(restriction);

		criteriaQuery.orderBy(cb.desc(batchJoin.get(TimesheetBatch_.startDate)));

		TypedQuery<CoherentOvertimeApplication> claimAppTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			claimAppTypedQuery.setFirstResult(getStartPosition(pageDTO));
			claimAppTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		return claimAppTypedQuery.getResultList();
	}

	@Override
	public Integer getCountByConditionForEmployee(Long employeeId, String fromDate, String toDate,
			CoherentConditionDTO conditionDTO) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Integer> criteriaQuery = cb.createQuery(Integer.class);
		Root<CoherentOvertimeApplication> root = criteriaQuery.from(CoherentOvertimeApplication.class);
		criteriaQuery.select(cb.count(root).as(Integer.class));

		Predicate restriction = cb.conjunction();

		Join<CoherentOvertimeApplication, Employee> empJoin = root.join(CoherentOvertimeApplication_.employee);

		Join<CoherentOvertimeApplication, TimesheetBatch> batchJoin = root
				.join(CoherentOvertimeApplication_.timesheetBatch);
		restriction = cb.and(restriction, cb.equal(empJoin.get(Employee_.employeeId), employeeId));

		if (StringUtils.isNotBlank(conditionDTO.getCreatedDate())) {
			restriction = cb.and(restriction,
					cb.equal(root.get(CoherentOvertimeApplication_.createdDate).as(Date.class),
							DateUtils.stringToDate(conditionDTO.getCreatedDate())));

		}
		if (StringUtils.isNotBlank(conditionDTO.getBatch())) {
			restriction = cb.and(restriction,
					cb.like(batchJoin.get(TimesheetBatch_.timesheetBatchDesc), '%' + conditionDTO.getBatch() + '%'));
		}
		if (StringUtils.isNotBlank(conditionDTO.getReviewer())) {
			Join<CoherentOvertimeApplication, CoherentOvertimeApplicationReviewer> reviewersJoin = root
					.join(CoherentOvertimeApplication_.coherentOvertimeApplicationReviewers);
			restriction = cb.and(restriction,
					cb.or(cb.like(reviewersJoin.get(CoherentOvertimeApplicationReviewer_.employeeReviewer)
							.get(Employee_.firstName), '%' + conditionDTO.getReviewer() + '%'),
							cb.like(reviewersJoin.get(CoherentOvertimeApplicationReviewer_.employeeReviewer)
									.get(Employee_.lastName), '%' + conditionDTO.getReviewer() + '%')));
		}

		if (conditionDTO.getEmployeeId() != null) {
			restriction = cb.and(restriction,
					cb.equal(empJoin.get(Employee_.employeeId), conditionDTO.getEmployeeId()));
		}

		if (conditionDTO.getFromDate() != null) {
			restriction = cb.and(restriction, cb.greaterThanOrEqualTo(
					(root.get(CoherentOvertimeApplication_.createdDate).as(Date.class)), conditionDTO.getFromDate()));
		}
		if (conditionDTO.getToDate() != null) {
			restriction = cb.and(restriction, cb.lessThanOrEqualTo(
					(root.get(CoherentOvertimeApplication_.createdDate).as(Date.class)), conditionDTO.getToDate()));
		}
		criteriaQuery.where(restriction);
		TypedQuery<Integer> claimAppTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		return claimAppTypedQuery.getSingleResult();

	}

	@Override
	public List<CoherentOvertimeApplication> findByCondition(PageRequest pageDTO, SortCondition sortDTO,
			PendingOTTimesheetConditionDTO otTimesheetConditionDTO, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CoherentOvertimeApplication> criteriaQuery = cb.createQuery(CoherentOvertimeApplication.class);
		Root<CoherentOvertimeApplication> otRoot = criteriaQuery.from(CoherentOvertimeApplication.class);
		criteriaQuery.select(otRoot);

		Predicate restriction = cb.conjunction();

		Join<CoherentOvertimeApplication, TimesheetStatusMaster> otStatusJoin = otRoot
				.join(CoherentOvertimeApplication_.timesheetStatusMaster);
		Join<CoherentOvertimeApplication, TimesheetBatch> timesheetBatchJoin = otRoot
				.join(CoherentOvertimeApplication_.timesheetBatch);

		restriction = cb.and(restriction,
				cb.equal(otRoot.get(CoherentOvertimeApplication_.company).get(Company_.companyId), companyId));

		if (StringUtils.isNotBlank(otTimesheetConditionDTO.getCreatedDate())) {
			restriction = cb.and(restriction,
					cb.equal(otRoot.get(CoherentOvertimeApplication_.createdDate).as(Date.class),
							DateUtils.stringToDate(otTimesheetConditionDTO.getCreatedDate())));
		}

		if (StringUtils.isNotBlank(otTimesheetConditionDTO.getBatch())) {
			restriction = cb.and(restriction, cb.like(timesheetBatchJoin.get(TimesheetBatch_.timesheetBatchDesc),
					'%' + otTimesheetConditionDTO.getBatch() + '%'));
		}
		if (otTimesheetConditionDTO.getStatusNameList().size() > 0) {
			restriction = cb.and(restriction, otStatusJoin.get(TimesheetStatusMaster_.timesheetStatusName)
					.in(otTimesheetConditionDTO.getStatusNameList()));

		}
		if (StringUtils.isNotBlank(otTimesheetConditionDTO.getEmployeeName())) {
			restriction = cb.and(restriction,
					cb.or(cb.like(otRoot.get(CoherentOvertimeApplication_.employee).get(Employee_.firstName),
							'%' + otTimesheetConditionDTO.getEmployeeName() + '%'),
							cb.like(
									otRoot.get(CoherentOvertimeApplication_.employee).get(Employee_.middleName),
									'%' + otTimesheetConditionDTO.getEmployeeName() + '%'),
							cb.like(otRoot.get(CoherentOvertimeApplication_.employee).get(Employee_.lastName),
									'%' + otTimesheetConditionDTO.getEmployeeName() + '%'),
							cb.like(otRoot.get(CoherentOvertimeApplication_.employee).get(Employee_.employeeNumber),
									'%' + otTimesheetConditionDTO.getEmployeeName() + '%')));
		}
		criteriaQuery.where(restriction);

		criteriaQuery.orderBy(cb.desc(timesheetBatchJoin.get(TimesheetBatch_.startDate)));

		TypedQuery<CoherentOvertimeApplication> otTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			otTypedQuery.setFirstResult(getStartPosition(pageDTO));
			otTypedQuery.setMaxResults(pageDTO.getPageSize());
		}
		return otTypedQuery.getResultList();
	}

	@Override
	public Integer getCountForFindByCondition(PendingOTTimesheetConditionDTO otTimesheetConditionDTO, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Integer> criteriaQuery = cb.createQuery(Integer.class);
		Root<CoherentOvertimeApplication> otRoot = criteriaQuery.from(CoherentOvertimeApplication.class);
		criteriaQuery.select(cb.count(otRoot).as(Integer.class));

		Predicate restriction = cb.conjunction();

		Join<CoherentOvertimeApplication, TimesheetStatusMaster> otStatusJoin = otRoot
				.join(CoherentOvertimeApplication_.timesheetStatusMaster);
		Join<CoherentOvertimeApplication, TimesheetBatch> timesheetBatchJoin = otRoot
				.join(CoherentOvertimeApplication_.timesheetBatch);

		restriction = cb.and(restriction,
				cb.equal(otRoot.get(CoherentOvertimeApplication_.company).get(Company_.companyId), companyId));

		if (StringUtils.isNotBlank(otTimesheetConditionDTO.getCreatedDate())) {
			restriction = cb.and(restriction,
					cb.equal(otRoot.get(CoherentOvertimeApplication_.createdDate).as(Date.class),
							DateUtils.stringToDate(otTimesheetConditionDTO.getCreatedDate())));
		}

		if (StringUtils.isNotBlank(otTimesheetConditionDTO.getBatch())) {
			restriction = cb.and(restriction, cb.like(timesheetBatchJoin.get(TimesheetBatch_.timesheetBatchDesc),
					'%' + otTimesheetConditionDTO.getBatch() + '%'));
		}
		if (otTimesheetConditionDTO.getStatusNameList().size() > 0) {
			restriction = cb.and(restriction, otStatusJoin.get(TimesheetStatusMaster_.timesheetStatusName)
					.in(otTimesheetConditionDTO.getStatusNameList()));

		}
		if (StringUtils.isNotBlank(otTimesheetConditionDTO.getEmployeeName())) {
			restriction = cb.and(restriction,
					cb.or(cb.like(otRoot.get(CoherentOvertimeApplication_.employee).get(Employee_.firstName),
							'%' + otTimesheetConditionDTO.getEmployeeName() + '%'),
							cb.like(
									otRoot.get(CoherentOvertimeApplication_.employee).get(Employee_.middleName),
									'%' + otTimesheetConditionDTO.getEmployeeName() + '%'),
							cb.like(otRoot.get(CoherentOvertimeApplication_.employee).get(Employee_.lastName),
									'%' + otTimesheetConditionDTO.getEmployeeName() + '%'),
							cb.like(otRoot.get(CoherentOvertimeApplication_.employee).get(Employee_.employeeNumber),
									'%' + otTimesheetConditionDTO.getEmployeeName() + '%')));
		}
		criteriaQuery.where(restriction);

		TypedQuery<Integer> otTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		return otTypedQuery.getSingleResult();
	}

	@Override
	public List<CoherentOvertimeApplication> findAllByCondition(PageRequest pageDTO, SortCondition sortDTO,
			PendingOTTimesheetConditionDTO otTimesheetConditionDTO, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CoherentOvertimeApplication> criteriaQuery = cb.createQuery(CoherentOvertimeApplication.class);
		Root<CoherentOvertimeApplication> otRoot = criteriaQuery.from(CoherentOvertimeApplication.class);
		criteriaQuery.select(otRoot);

		Predicate restriction = cb.conjunction();

		Join<CoherentOvertimeApplication, TimesheetBatch> timesheetBatchJoin = otRoot
				.join(CoherentOvertimeApplication_.timesheetBatch);
		Join<CoherentOvertimeApplication, TimesheetStatusMaster> otStatusJoin = otRoot
				.join(CoherentOvertimeApplication_.timesheetStatusMaster);

		restriction = cb.and(restriction,
				cb.equal(otRoot.get(CoherentOvertimeApplication_.company).get(Company_.companyId), companyId));

		if (StringUtils.isNotBlank(otTimesheetConditionDTO.getCreatedDate())) {
			restriction = cb.and(restriction,
					cb.equal(otRoot.get(CoherentOvertimeApplication_.createdDate).as(Date.class),
							DateUtils.stringToDate(otTimesheetConditionDTO.getCreatedDate())));
		}

		if (StringUtils.isNotBlank(otTimesheetConditionDTO.getBatch())) {
			restriction = cb.and(restriction, cb.like(timesheetBatchJoin.get(TimesheetBatch_.timesheetBatchDesc),
					'%' + otTimesheetConditionDTO.getBatch() + '%'));
		}
		if (otTimesheetConditionDTO.getStatusNameList().size() > 0) {
			restriction = cb.and(restriction, otStatusJoin.get(TimesheetStatusMaster_.timesheetStatusName)
					.in(otTimesheetConditionDTO.getStatusNameList()));

		}
		if (StringUtils.isNotBlank(otTimesheetConditionDTO.getEmployeeName())) {
			restriction = cb.and(restriction,
					cb.or(cb.like(otRoot.get(CoherentOvertimeApplication_.employee).get(Employee_.firstName),
							'%' + otTimesheetConditionDTO.getEmployeeName() + '%'),
							cb.like(
									otRoot.get(CoherentOvertimeApplication_.employee).get(Employee_.middleName),
									'%' + otTimesheetConditionDTO.getEmployeeName() + '%'),
							cb.like(otRoot.get(CoherentOvertimeApplication_.employee).get(Employee_.lastName),
									'%' + otTimesheetConditionDTO.getEmployeeName() + '%'),
							cb.like(otRoot.get(CoherentOvertimeApplication_.employee).get(Employee_.employeeNumber),
									'%' + otTimesheetConditionDTO.getEmployeeName() + '%')));
		}
		criteriaQuery.where(restriction);

		criteriaQuery.orderBy(cb.desc(timesheetBatchJoin.get(TimesheetBatch_.startDate)));

		TypedQuery<CoherentOvertimeApplication> otTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			otTypedQuery.setFirstResult(getStartPosition(pageDTO));
			otTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		return otTypedQuery.getResultList();
	}

	@Override
	public Integer getCountForFindAllByCondition(PendingOTTimesheetConditionDTO otTimesheetConditionDTO,
			Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Integer> criteriaQuery = cb.createQuery(Integer.class);
		Root<CoherentOvertimeApplication> otRoot = criteriaQuery.from(CoherentOvertimeApplication.class);
		criteriaQuery.select(cb.count(otRoot).as(Integer.class));

		Predicate restriction = cb.conjunction();

		Join<CoherentOvertimeApplication, TimesheetBatch> timesheetBatchJoin = otRoot
				.join(CoherentOvertimeApplication_.timesheetBatch);
		Join<CoherentOvertimeApplication, TimesheetStatusMaster> otStatusJoin = otRoot
				.join(CoherentOvertimeApplication_.timesheetStatusMaster);
		restriction = cb.and(restriction,
				cb.equal(otRoot.get(CoherentOvertimeApplication_.company).get(Company_.companyId), companyId));

		if (StringUtils.isNotBlank(otTimesheetConditionDTO.getCreatedDate())) {
			restriction = cb.and(restriction,
					cb.equal(otRoot.get(CoherentOvertimeApplication_.createdDate).as(Date.class),
							DateUtils.stringToDate(otTimesheetConditionDTO.getCreatedDate())));
		}
		if (otTimesheetConditionDTO.getStatusNameList().size() > 0) {
			restriction = cb.and(restriction, otStatusJoin.get(TimesheetStatusMaster_.timesheetStatusName)
					.in(otTimesheetConditionDTO.getStatusNameList()));

		}
		if (StringUtils.isNotBlank(otTimesheetConditionDTO.getBatch())) {
			restriction = cb.and(restriction, cb.like(timesheetBatchJoin.get(TimesheetBatch_.timesheetBatchDesc),
					'%' + otTimesheetConditionDTO.getBatch() + '%'));
		}
		if (StringUtils.isNotBlank(otTimesheetConditionDTO.getEmployeeName())) {
			restriction = cb.and(restriction,
					cb.or(cb.like(otRoot.get(CoherentOvertimeApplication_.employee).get(Employee_.firstName),
							'%' + otTimesheetConditionDTO.getEmployeeName() + '%'),
							cb.like(
									otRoot.get(CoherentOvertimeApplication_.employee).get(Employee_.middleName),
									'%' + otTimesheetConditionDTO.getEmployeeName() + '%'),
							cb.like(otRoot.get(CoherentOvertimeApplication_.employee).get(Employee_.lastName),
									'%' + otTimesheetConditionDTO.getEmployeeName() + '%'),
							cb.like(otRoot.get(CoherentOvertimeApplication_.employee).get(Employee_.employeeNumber),
									'%' + otTimesheetConditionDTO.getEmployeeName() + '%')));
		}
		criteriaQuery.where(restriction);

		TypedQuery<Integer> otTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		return otTypedQuery.getSingleResult();
	}

	@Override
	public List<CoherentOvertimeApplication> findSubmitTimesheetEmp(Long batchId, Long companyId) {

		List<CoherentOvertimeApplication> emplyeeList = new ArrayList<CoherentOvertimeApplication>();
		List<String> timesheetStatusNames = new ArrayList<>();
		timesheetStatusNames.add(PayAsiaConstants.LUNDIN_TIMESHEET_STATUS_SUBMITTED);
		timesheetStatusNames.add(PayAsiaConstants.LUNDIN_TIMESHEET_STATUS_APPROVED);
		timesheetStatusNames.add(PayAsiaConstants.LUNDIN_TIMESHEET_STATUS_COMPLETED);
		try {
			CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
			CriteriaQuery<CoherentOvertimeApplication> criteriaQuery = cb
					.createQuery(CoherentOvertimeApplication.class);
			Root<CoherentOvertimeApplication> root = criteriaQuery.from(CoherentOvertimeApplication.class);

			Join<CoherentOvertimeApplication, Company> companyJoin = root.join(CoherentOvertimeApplication_.company);
			Join<CoherentOvertimeApplication, TimesheetStatusMaster> timesheetStatusJoin = root
					.join(CoherentOvertimeApplication_.timesheetStatusMaster);

			criteriaQuery.select(root);
			Predicate restriction = cb.conjunction();

			restriction = cb.and(restriction, cb.equal(companyJoin.get(Company_.companyId), companyId));

			restriction = cb.and(restriction, cb.equal(root.get(CoherentOvertimeApplication_.timesheetBatch), batchId));

			restriction = cb.and(restriction,
					timesheetStatusJoin.get(TimesheetStatusMaster_.timesheetStatusDesc).in(timesheetStatusNames));

			criteriaQuery.where(restriction);

			TypedQuery<CoherentOvertimeApplication> timesheetMasterTypedQuery = entityManagerFactory
					.createQuery(criteriaQuery);

			emplyeeList = timesheetMasterTypedQuery.getResultList();
			return emplyeeList;

		} catch (Exception e) {
			return emplyeeList;
		}
	}

	@Override
	public List<CoherentOvertimeApplication> findByConditionSubmitted(LundinPendingTsheetConditionDTO conditionDTO,
			PageRequest pageDTO, SortCondition sortDTO) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CoherentOvertimeApplication> criteriaQuery = cb.createQuery(CoherentOvertimeApplication.class);
		Root<CoherentOvertimeApplication> root = criteriaQuery.from(CoherentOvertimeApplication.class);
		criteriaQuery.select(root);

		Predicate restriction = cb.conjunction();

		Join<CoherentOvertimeApplication, Employee> empChangeReqJoin = root.join(CoherentOvertimeApplication_.employee);

		Join<CoherentOvertimeApplication, TimesheetStatusMaster> hrisStatusJoin = root
				.join(CoherentOvertimeApplication_.timesheetStatusMaster);

		if (conditionDTO.getEmployeeId() != null) {
			restriction = cb.and(restriction,
					cb.equal(empChangeReqJoin.get(Employee_.employeeId), conditionDTO.getEmployeeId()));
		}

		if (conditionDTO.getStatusNameList().size() > 0) {
			restriction = cb.and(restriction, hrisStatusJoin.get(TimesheetStatusMaster_.timesheetStatusName)
					.in(conditionDTO.getStatusNameList()));
		}

		criteriaQuery.where(restriction);

		criteriaQuery.orderBy(cb.desc(root.get(EmployeeTimesheetApplication_.updatedDate)));

		TypedQuery<CoherentOvertimeApplication> typedQuery = entityManagerFactory.createQuery(criteriaQuery);

		// Added by Gaurav for Pagination
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			typedQuery.setFirstResult(getStartPosition(pageDTO));
			typedQuery.setMaxResults(pageDTO.getPageSize());
		}

		return typedQuery.getResultList();
	}

	@Override
	public CoherentOvertimeApplication findByCondition(Long companyId, Long employeeId, Long batchId, String remarks) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CoherentOvertimeApplication> criteriaQuery = cb.createQuery(CoherentOvertimeApplication.class);
		Root<CoherentOvertimeApplication> root = criteriaQuery.from(CoherentOvertimeApplication.class);
		criteriaQuery.select(root);

		Predicate restriction = cb.conjunction();

		Join<CoherentOvertimeApplication, Employee> empJoin = root.join(CoherentOvertimeApplication_.employee);
		Join<CoherentOvertimeApplication, Company> companyJoin = root.join(CoherentOvertimeApplication_.company);
		Join<CoherentOvertimeApplication, TimesheetBatch> batchJoin = root
				.join(CoherentOvertimeApplication_.timesheetBatch);

		restriction = cb.and(restriction, cb.equal(empJoin.get(Employee_.employeeId), employeeId));
		restriction = cb.and(restriction, cb.equal(companyJoin.get(Company_.companyId), companyId));
		restriction = cb.and(restriction, cb.equal(batchJoin.get(TimesheetBatch_.timesheetBatchId), batchId));
		if (StringUtils.isNotBlank(remarks)) {
			restriction = cb.and(restriction,
					cb.like(root.get(CoherentOvertimeApplication_.remarks), '%' + remarks + '%'));
		}

		criteriaQuery.where(restriction);

		TypedQuery<CoherentOvertimeApplication> typedQuery = entityManagerFactory.createQuery(criteriaQuery);
		List<CoherentOvertimeApplication> CoherentOvertimeApplicationList = typedQuery.getResultList();
		if (!CoherentOvertimeApplicationList.isEmpty() && CoherentOvertimeApplicationList.size() > 0) {
			return CoherentOvertimeApplicationList.get(0);
		}
		return null;
	}

	/**
	 * @author pranavuniyal Employee check remove to get reviwer name and date
	 *         data
	 * 
	 */
	@Override
	public CoherentOvertimeApplication findById(long id, Long employeeId, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CoherentOvertimeApplication> criteriaQuery = cb.createQuery(CoherentOvertimeApplication.class);
		Root<CoherentOvertimeApplication> root = criteriaQuery.from(CoherentOvertimeApplication.class);
		criteriaQuery.select(root);

		Predicate restriction = cb.conjunction();

		Join<CoherentOvertimeApplication, Employee> empJoin = root.join(CoherentOvertimeApplication_.employee);
		Join<CoherentOvertimeApplication, Company> companyJoin = root.join(CoherentOvertimeApplication_.company);
		/*
		 * restriction = cb.and(restriction,
		 * cb.equal(empJoin.get(Employee_.employeeId), employeeId));
		 */
		restriction = cb.and(restriction, cb.equal(companyJoin.get(Company_.companyId), companyId));
		restriction = cb.and(restriction, cb.equal(root.get(CoherentOvertimeApplication_.overtimeApplicationID), id));
		criteriaQuery.where(restriction);

		TypedQuery<CoherentOvertimeApplication> typedQuery = entityManagerFactory.createQuery(criteriaQuery);
		List<CoherentOvertimeApplication> CoherentOvertimeApplicationList = typedQuery.getResultList();
		if (!CoherentOvertimeApplicationList.isEmpty() && CoherentOvertimeApplicationList.size() > 0) {
			return CoherentOvertimeApplicationList.get(0);
		}
		return null;
	}

	@Override
	public CoherentOvertimeApplication findById(long id, Long employeeId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CoherentOvertimeApplication> criteriaQuery = cb.createQuery(CoherentOvertimeApplication.class);
		Root<CoherentOvertimeApplication> root = criteriaQuery.from(CoherentOvertimeApplication.class);
		criteriaQuery.select(root);

		Predicate restriction = cb.conjunction();

		Join<CoherentOvertimeApplication, Employee> empJoin = root.join(CoherentOvertimeApplication_.employee);
		restriction = cb.and(restriction, cb.equal(empJoin.get(Employee_.employeeId), employeeId));
		restriction = cb.and(restriction, cb.equal(root.get(CoherentOvertimeApplication_.overtimeApplicationID), id));
		criteriaQuery.where(restriction);

		TypedQuery<CoherentOvertimeApplication> typedQuery = entityManagerFactory.createQuery(criteriaQuery);
		List<CoherentOvertimeApplication> CoherentOvertimeApplicationList = typedQuery.getResultList();
		if (!CoherentOvertimeApplicationList.isEmpty() && CoherentOvertimeApplicationList.size() > 0) {
			return CoherentOvertimeApplicationList.get(0);
		}
		return null;
	}

	@Override
	public CoherentOvertimeApplication findByCompanyId(Long id, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CoherentOvertimeApplication> criteriaQuery = cb.createQuery(CoherentOvertimeApplication.class);
		Root<CoherentOvertimeApplication> root = criteriaQuery.from(CoherentOvertimeApplication.class);
		criteriaQuery.select(root);

		Predicate restriction = cb.conjunction();

		Join<CoherentOvertimeApplication, Company> empJoin = root.join(CoherentOvertimeApplication_.company);
		restriction = cb.and(restriction, cb.equal(empJoin.get(Company_.companyId), companyId));
		restriction = cb.and(restriction, cb.equal(root.get(CoherentOvertimeApplication_.overtimeApplicationID), id));
		criteriaQuery.where(restriction);

		TypedQuery<CoherentOvertimeApplication> typedQuery = entityManagerFactory.createQuery(criteriaQuery);
		List<CoherentOvertimeApplication> CoherentOvertimeApplicationList = typedQuery.getResultList();
		if (!CoherentOvertimeApplicationList.isEmpty() && CoherentOvertimeApplicationList.size() > 0) {
			return CoherentOvertimeApplicationList.get(0);
		}
		return null;
	}
}
