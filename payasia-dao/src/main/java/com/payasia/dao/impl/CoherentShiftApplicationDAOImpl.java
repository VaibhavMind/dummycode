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

import com.payasia.common.dto.AddClaimConditionDTO;
import com.payasia.common.dto.LundinPendingTsheetConditionDTO;
import com.payasia.common.dto.LundinTsheetConditionDTO;
import com.payasia.common.dto.PendingOTTimesheetConditionDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.NullAwareBeanUtilsBean;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.CoherentShiftApplicationDAO;
import com.payasia.dao.bean.CoherentShiftApplication;
import com.payasia.dao.bean.CoherentShiftApplicationDetail;
import com.payasia.dao.bean.CoherentShiftApplicationDetail_;
import com.payasia.dao.bean.CoherentShiftApplicationWorkflow;
import com.payasia.dao.bean.CoherentShiftApplicationWorkflow_;
import com.payasia.dao.bean.CoherentShiftApplication_;
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
public class CoherentShiftApplicationDAOImpl extends BaseDAO implements CoherentShiftApplicationDAO {
	@Override
	protected Object getBaseEntity() {
		CoherentShiftApplication timesheet = new CoherentShiftApplication();
		return timesheet;
	}

	@Override
	public CoherentShiftApplication findById(long id) {
		return super.findById(CoherentShiftApplication.class, id);
	}

	@Override
	public void save(CoherentShiftApplication coherentShiftApplication) {
		super.save(coherentShiftApplication);
	}

	@Override
	public void update(CoherentShiftApplication coherentShiftApplication) {
		super.update(coherentShiftApplication);

	}

	/*
	 * @Override public CoherentShiftApplication saveAndReturn(
	 * CoherentShiftApplication coherentShiftApplication) {
	 * CoherentShiftApplication persistObj = coherentShiftApplication; try {
	 * NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
	 * persistObj = (CoherentShiftApplication) getBaseEntity();
	 * beanUtil.copyProperties(persistObj, coherentShiftApplication); } catch
	 * (IllegalAccessException e) { throw new
	 * PayAsiaSystemException("errors.dao", e); } catch
	 * (InvocationTargetException e) { throw new
	 * PayAsiaSystemException("errors.dao", e); }
	 * this.entityManagerFactory.persist(persistObj);
	 * this.entityManagerFactory.flush(); return persistObj; }
	 */

	@Override
	public Long getCountForCondition(AddClaimConditionDTO conditionDTO, Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
		Root<CoherentShiftApplication> claimApplicationRoot = criteriaQuery.from(CoherentShiftApplication.class);
		criteriaQuery.select(cb.count(claimApplicationRoot));

		Predicate restriction = cb.conjunction();

		Join<CoherentShiftApplication, Employee> empLeaveJoin = claimApplicationRoot
				.join(CoherentShiftApplication_.employee);

		Join<CoherentShiftApplication, TimesheetStatusMaster> leaveStatusJoin = claimApplicationRoot
				.join(CoherentShiftApplication_.timesheetStatusMaster);

		restriction = cb.and(restriction, cb
				.equal(claimApplicationRoot.get(CoherentShiftApplication_.company).get(Company_.companyId), companyId));
		if (StringUtils.isNotBlank(conditionDTO.getCreatedDate())) {
			restriction = cb.and(restriction,
					cb.equal(claimApplicationRoot.get(CoherentShiftApplication_.createdDate).as(Date.class),
							DateUtils.stringToDate(conditionDTO.getCreatedDate())));

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
					(claimApplicationRoot.get(CoherentShiftApplication_.createdDate)), conditionDTO.getFromDate()));
		}
		if (conditionDTO.getToDate() != null) {
			restriction = cb.and(restriction, cb.lessThanOrEqualTo(
					(claimApplicationRoot.get(CoherentShiftApplication_.createdDate)), conditionDTO.getToDate()));
		}

		criteriaQuery.where(restriction);

		TypedQuery<Long> claimAppTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		return claimAppTypedQuery.getSingleResult();

	}

	@Override
	public List<CoherentShiftApplication> findByCondition(AddClaimConditionDTO conditionDTO, PageRequest pageDTO,
			SortCondition sortDTO, Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CoherentShiftApplication> criteriaQuery = cb.createQuery(CoherentShiftApplication.class);
		Root<CoherentShiftApplication> claimApplicationRoot = criteriaQuery.from(CoherentShiftApplication.class);
		criteriaQuery.select(claimApplicationRoot);

		Predicate restriction = cb.conjunction();

		Join<CoherentShiftApplication, Employee> empLeaveJoin = claimApplicationRoot
				.join(CoherentShiftApplication_.employee);

		Join<CoherentShiftApplication, TimesheetStatusMaster> leaveStatusJoin = claimApplicationRoot
				.join(CoherentShiftApplication_.timesheetStatusMaster);

		restriction = cb.and(restriction, cb
				.equal(claimApplicationRoot.get(CoherentShiftApplication_.company).get(Company_.companyId), companyId));

		if (StringUtils.isNotBlank(conditionDTO.getCreatedDate())) {
			restriction = cb.and(restriction,
					cb.equal(claimApplicationRoot.get(CoherentShiftApplication_.createdDate).as(Date.class),
							DateUtils.stringToDate(conditionDTO.getCreatedDate())));

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
					(claimApplicationRoot.get(CoherentShiftApplication_.createdDate)), conditionDTO.getFromDate()));
		}
		if (conditionDTO.getToDate() != null) {
			restriction = cb.and(restriction, cb.lessThanOrEqualTo(
					(claimApplicationRoot.get(CoherentShiftApplication_.createdDate)), conditionDTO.getToDate()));
		}

		criteriaQuery.where(restriction);

		criteriaQuery.orderBy(cb.desc(claimApplicationRoot.get(CoherentShiftApplication_.updatedDate)));

		TypedQuery<CoherentShiftApplication> claimAppTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			claimAppTypedQuery.setFirstResult(getStartPosition(pageDTO));
			claimAppTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		return claimAppTypedQuery.getResultList();
	}

	@Override
	public CoherentShiftApplicationWorkflow findByCondition(Long otTimesheetId, Long createdById) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CoherentShiftApplicationWorkflow> criteriaQuery = cb
				.createQuery(CoherentShiftApplicationWorkflow.class);
		Root<CoherentShiftApplicationWorkflow> otTimesheetWorkflowRoot = criteriaQuery
				.from(CoherentShiftApplicationWorkflow.class);
		criteriaQuery.select(otTimesheetWorkflowRoot);

		Predicate restriction = cb.conjunction();

		Join<CoherentShiftApplicationWorkflow, Employee> empLeaveJoin = otTimesheetWorkflowRoot
				.join(CoherentShiftApplicationWorkflow_.createdBy);

		Join<CoherentShiftApplicationWorkflow, CoherentShiftApplication> otTimesheetJoin = otTimesheetWorkflowRoot
				.join(CoherentShiftApplicationWorkflow_.coherentShiftApplication);

		restriction = cb.and(restriction, cb.equal(empLeaveJoin.get(Employee_.employeeId), createdById));

		restriction = cb.and(restriction,
				cb.equal(otTimesheetJoin.get(CoherentShiftApplication_.shiftApplicationID), otTimesheetId));
		criteriaQuery.where(restriction);
		criteriaQuery.orderBy(
				cb.desc(otTimesheetWorkflowRoot.get(CoherentShiftApplicationWorkflow_.shiftApplicationWorkflowID)));
		TypedQuery<CoherentShiftApplicationWorkflow> otWorkflowTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		otWorkflowTypedQuery.setMaxResults(1);
		List<CoherentShiftApplicationWorkflow> otworkflowList = otWorkflowTypedQuery.getResultList();
		if (otworkflowList != null && !otworkflowList.isEmpty()) {
			return otworkflowList.get(0);
		}
		return null;

	}

	@Override
	public List<CoherentShiftApplication> findByTimesheetBatchId(long timesheetBatchId, long companyId,
			long employeeId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CoherentShiftApplication> criteriaQuery = cb.createQuery(CoherentShiftApplication.class);
		Root<CoherentShiftApplication> root = criteriaQuery.from(CoherentShiftApplication.class);
		criteriaQuery.select(root);

		Predicate restriction = cb.conjunction();

		Join<CoherentShiftApplication, Company> companyJoin = root.join(CoherentShiftApplication_.company);

		Join<CoherentShiftApplication, Employee> employeeJoin = root.join(CoherentShiftApplication_.employee);

		Join<CoherentShiftApplication, TimesheetBatch> timesheetBatchJoin = root
				.join(CoherentShiftApplication_.timesheetBatch);

		restriction = cb.and(restriction, cb.equal(companyJoin.get(Company_.companyId), companyId));

		restriction = cb.and(restriction, cb.equal(employeeJoin.get(Employee_.employeeId), employeeId));

		restriction = cb.and(restriction,
				cb.equal(timesheetBatchJoin.get(TimesheetBatch_.timesheetBatchId), timesheetBatchId));

		criteriaQuery.where(restriction);

		TypedQuery<CoherentShiftApplication> claimAppTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		return (List<CoherentShiftApplication>) claimAppTypedQuery.getResultList();

	}

	@Override
	public long saveAndReturn(CoherentShiftApplication shift) {
		CoherentShiftApplication persistObj = shift;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (CoherentShiftApplication) getBaseEntity();
			beanUtil.copyProperties(persistObj, shift);
		} catch (IllegalAccessException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		} catch (InvocationTargetException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		}
		this.entityManagerFactory.persist(persistObj);
		this.entityManagerFactory.flush();
		return persistObj.getShiftApplicationID();

	}

	@Override
	public CoherentShiftApplication saveAndReturnWithFlush(CoherentShiftApplication shift) {
		CoherentShiftApplication persistObj = shift;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (CoherentShiftApplication) getBaseEntity();
			beanUtil.copyProperties(persistObj, shift);
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
	public List<CoherentShiftApplicationDetail> getCoherentShiftApplicationDetails(long shiftId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CoherentShiftApplicationDetail> criteriaQuery = cb
				.createQuery(CoherentShiftApplicationDetail.class);

		Root<CoherentShiftApplicationDetail> root = criteriaQuery.from(CoherentShiftApplicationDetail.class);
		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction,
				cb.equal(root.get(CoherentShiftApplicationDetail_.coherentShiftApplication), shiftId));
		criteriaQuery.select(root).where(restriction);
		TypedQuery<CoherentShiftApplicationDetail> typedQuery = entityManagerFactory.createQuery(criteriaQuery);
		try {

			return typedQuery.getResultList();
		} catch (Exception ex) {
			return null;
		}

	}

	@Override
	public Integer getCountByConditionForEmployee(Long employeeId, String fromDate, String toDate,
			LundinTsheetConditionDTO conditionDTO, Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Integer> criteriaQuery = cb.createQuery(Integer.class);
		Root<CoherentShiftApplication> otTimesheetRoot = criteriaQuery.from(CoherentShiftApplication.class);
		criteriaQuery.select(cb.count(otTimesheetRoot).as(Integer.class));

		Predicate restriction = cb.conjunction();

		Join<CoherentShiftApplication, Employee> empJoin = otTimesheetRoot.join(CoherentShiftApplication_.employee);

		restriction = cb.and(restriction,
				cb.equal(otTimesheetRoot.get(CoherentShiftApplication_.company).get(Company_.companyId), companyId));

		restriction = cb.and(restriction, cb.equal(empJoin.get(Employee_.employeeId), employeeId));

		if (StringUtils.isNotBlank(conditionDTO.getBatch())) {
			restriction = cb.and(restriction, cb.like(otTimesheetRoot.get(CoherentShiftApplication_.timesheetBatch)
					.get(TimesheetBatch_.timesheetBatchDesc), conditionDTO.getBatch()));

		}
		if (StringUtils.isNotBlank(conditionDTO.getStatus())) {
			restriction = cb.and(restriction,
					cb.like(otTimesheetRoot.get(CoherentShiftApplication_.timesheetStatusMaster)
							.get(TimesheetStatusMaster_.timesheetStatusDesc), conditionDTO.getStatus()));

		}

		if (StringUtils.isNotBlank(fromDate)) {
			restriction = cb.and(restriction,
					cb.greaterThanOrEqualTo((otTimesheetRoot.get(CoherentShiftApplication_.createdDate).as(Date.class)),
							DateUtils.stringToTimestamp(fromDate)));
		}
		if (StringUtils.isNotBlank(toDate)) {
			restriction = cb.and(restriction,
					cb.lessThanOrEqualTo((otTimesheetRoot.get(CoherentShiftApplication_.createdDate).as(Date.class)),
							DateUtils.stringToTimestamp(toDate)));
		}

		criteriaQuery.where(restriction);

		TypedQuery<Integer> claimAppTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		return claimAppTypedQuery.getSingleResult();

	}

	@Override
	public List<CoherentShiftApplication> findByConditionForEmployee(PageRequest pageDTO, SortCondition sortDTO,
			Long employeeId, String fromDate, String toDate, Boolean visibleToEmployee,
			LundinTsheetConditionDTO conditionDTO, Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CoherentShiftApplication> criteriaQuery = cb.createQuery(CoherentShiftApplication.class);
		Root<CoherentShiftApplication> otTimesheetRoot = criteriaQuery.from(CoherentShiftApplication.class);
		criteriaQuery.select(otTimesheetRoot);

		Predicate restriction = cb.conjunction();

		Join<CoherentShiftApplication, Employee> empJoin = otTimesheetRoot.join(CoherentShiftApplication_.employee);

		restriction = cb.and(restriction,
				cb.equal(otTimesheetRoot.get(CoherentShiftApplication_.company).get(Company_.companyId), companyId));
		restriction = cb.and(restriction, cb.equal(empJoin.get(Employee_.employeeId), employeeId));

		if (StringUtils.isNotBlank(conditionDTO.getBatch())) {
			restriction = cb.and(restriction, cb.like(otTimesheetRoot.get(CoherentShiftApplication_.timesheetBatch)
					.get(TimesheetBatch_.timesheetBatchDesc), conditionDTO.getBatch()));

		}
		if (StringUtils.isNotBlank(conditionDTO.getStatus())) {
			restriction = cb.and(restriction,
					cb.like(otTimesheetRoot.get(CoherentShiftApplication_.timesheetStatusMaster)
							.get(TimesheetStatusMaster_.timesheetStatusDesc), conditionDTO.getStatus()));

		}

		if (StringUtils.isNotBlank(fromDate)) {
			restriction = cb.and(restriction,
					cb.greaterThanOrEqualTo((otTimesheetRoot.get(CoherentShiftApplication_.createdDate).as(Date.class)),
							DateUtils.stringToTimestamp(fromDate)));
		}
		if (StringUtils.isNotBlank(toDate)) {
			restriction = cb.and(restriction,
					cb.lessThanOrEqualTo((otTimesheetRoot.get(CoherentShiftApplication_.createdDate).as(Date.class)),
							DateUtils.stringToTimestamp(toDate)));
		}
		criteriaQuery.where(restriction);

		TypedQuery<CoherentShiftApplication> claimAppTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			claimAppTypedQuery.setFirstResult(getStartPosition(pageDTO));
			claimAppTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		return claimAppTypedQuery.getResultList();
	}

	@Override
	public List<Long> findByBatchId(Long employeeId, List<Long> batchIdList, List<String> statusList) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
		Root<CoherentShiftApplication> root = criteriaQuery.from(CoherentShiftApplication.class);
		criteriaQuery.select(root.get(CoherentShiftApplication_.timesheetBatch).get(TimesheetBatch_.timesheetBatchId)
				.as(Long.class));

		Predicate restriction = cb.conjunction();

		Join<CoherentShiftApplication, Employee> empLeaveJoin = root.join(CoherentShiftApplication_.employee);
		Join<CoherentShiftApplication, TimesheetBatch> batchJoin = root.join(CoherentShiftApplication_.timesheetBatch);
		Join<CoherentShiftApplication, TimesheetStatusMaster> leaveStatusJoin = root
				.join(CoherentShiftApplication_.timesheetStatusMaster);

		restriction = cb.and(restriction, cb.equal(empLeaveJoin.get(Employee_.employeeId), employeeId));
		restriction = cb.and(restriction,
				cb.not(leaveStatusJoin.get(TimesheetStatusMaster_.timesheetStatusName).in(statusList)));
		restriction = cb.and(restriction, batchJoin.get(TimesheetBatch_.timesheetBatchId).in(batchIdList));

		criteriaQuery.where(restriction);

		criteriaQuery.orderBy(cb.desc(root.get(CoherentShiftApplication_.updatedDate)));

		TypedQuery<Long> claimAppTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		return claimAppTypedQuery.getResultList();
	}

	@Override
	public Integer getCountForFindByCondition(PendingOTTimesheetConditionDTO otTimesheetConditionDTO, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Integer> criteriaQuery = cb.createQuery(Integer.class);
		Root<CoherentShiftApplication> otRoot = criteriaQuery.from(CoherentShiftApplication.class);
		criteriaQuery.select(cb.count(otRoot).as(Integer.class));

		Predicate restriction = cb.conjunction();

		Join<CoherentShiftApplication, TimesheetStatusMaster> otStatusJoin = otRoot
				.join(CoherentShiftApplication_.timesheetStatusMaster);
		Join<CoherentShiftApplication, TimesheetBatch> timesheetBatchJoin = otRoot
				.join(CoherentShiftApplication_.timesheetBatch);

		restriction = cb.and(restriction,
				cb.equal(otRoot.get(CoherentShiftApplication_.company).get(Company_.companyId), companyId));

		if (StringUtils.isNotBlank(otTimesheetConditionDTO.getCreatedDate())) {
			restriction = cb.and(restriction, cb.equal(otRoot.get(CoherentShiftApplication_.createdDate).as(Date.class),
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
					cb.or(cb.like(otRoot.get(CoherentShiftApplication_.employee).get(Employee_.firstName),
							'%' + otTimesheetConditionDTO.getEmployeeName() + '%'),
							cb.like(
									otRoot.get(CoherentShiftApplication_.employee).get(Employee_.middleName),
									'%' + otTimesheetConditionDTO.getEmployeeName() + '%'),
							cb.like(otRoot.get(CoherentShiftApplication_.employee).get(Employee_.lastName),
									'%' + otTimesheetConditionDTO.getEmployeeName() + '%'),
							cb.like(otRoot.get(CoherentShiftApplication_.employee).get(Employee_.employeeNumber),
									'%' + otTimesheetConditionDTO.getEmployeeName() + '%')));
		}
		criteriaQuery.where(restriction);

		TypedQuery<Integer> otTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		return otTypedQuery.getSingleResult();
	}

	@Override
	public List<CoherentShiftApplication> findByCondition(PageRequest pageDTO, SortCondition sortDTO,
			PendingOTTimesheetConditionDTO otTimesheetConditionDTO, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CoherentShiftApplication> criteriaQuery = cb.createQuery(CoherentShiftApplication.class);
		Root<CoherentShiftApplication> otRoot = criteriaQuery.from(CoherentShiftApplication.class);
		criteriaQuery.select(otRoot);

		Predicate restriction = cb.conjunction();

		Join<CoherentShiftApplication, TimesheetStatusMaster> otStatusJoin = otRoot
				.join(CoherentShiftApplication_.timesheetStatusMaster);
		Join<CoherentShiftApplication, TimesheetBatch> timesheetBatchJoin = otRoot
				.join(CoherentShiftApplication_.timesheetBatch);

		restriction = cb.and(restriction,
				cb.equal(otRoot.get(CoherentShiftApplication_.company).get(Company_.companyId), companyId));

		if (StringUtils.isNotBlank(otTimesheetConditionDTO.getCreatedDate())) {
			restriction = cb.and(restriction, cb.equal(otRoot.get(CoherentShiftApplication_.createdDate).as(Date.class),
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
					cb.or(cb.like(otRoot.get(CoherentShiftApplication_.employee).get(Employee_.firstName),
							'%' + otTimesheetConditionDTO.getEmployeeName() + '%'),
							cb.like(
									otRoot.get(CoherentShiftApplication_.employee).get(Employee_.middleName),
									'%' + otTimesheetConditionDTO.getEmployeeName() + '%'),
							cb.like(otRoot.get(CoherentShiftApplication_.employee).get(Employee_.lastName),
									'%' + otTimesheetConditionDTO.getEmployeeName() + '%'),
							cb.like(otRoot.get(CoherentShiftApplication_.employee).get(Employee_.employeeNumber),
									'%' + otTimesheetConditionDTO.getEmployeeName() + '%')));

		}
		criteriaQuery.where(restriction);

		criteriaQuery.orderBy(cb.desc(timesheetBatchJoin.get(TimesheetBatch_.startDate)));

		TypedQuery<CoherentShiftApplication> otTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
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
		Root<CoherentShiftApplication> otRoot = criteriaQuery.from(CoherentShiftApplication.class);
		criteriaQuery.select(cb.count(otRoot).as(Integer.class));

		Predicate restriction = cb.conjunction();

		Join<CoherentShiftApplication, TimesheetBatch> timesheetBatchJoin = otRoot
				.join(CoherentShiftApplication_.timesheetBatch);
		Join<CoherentShiftApplication, TimesheetStatusMaster> otStatusJoin = otRoot
				.join(CoherentShiftApplication_.timesheetStatusMaster);

		restriction = cb.and(restriction,
				cb.equal(otRoot.get(CoherentShiftApplication_.company).get(Company_.companyId), companyId));

		if (StringUtils.isNotBlank(otTimesheetConditionDTO.getCreatedDate())) {
			restriction = cb.and(restriction, cb.equal(otRoot.get(CoherentShiftApplication_.createdDate).as(Date.class),
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
					cb.or(cb.like(otRoot.get(CoherentShiftApplication_.employee).get(Employee_.firstName),
							'%' + otTimesheetConditionDTO.getEmployeeName() + '%'),
							cb.like(
									otRoot.get(CoherentShiftApplication_.employee).get(Employee_.middleName),
									'%' + otTimesheetConditionDTO.getEmployeeName() + '%'),
							cb.like(otRoot.get(CoherentShiftApplication_.employee).get(Employee_.lastName),
									'%' + otTimesheetConditionDTO.getEmployeeName() + '%'),
							cb.like(otRoot.get(CoherentShiftApplication_.employee).get(Employee_.employeeNumber),
									'%' + otTimesheetConditionDTO.getEmployeeName() + '%')));
		}
		criteriaQuery.where(restriction);

		TypedQuery<Integer> otTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		return otTypedQuery.getSingleResult();
	}

	@Override
	public List<CoherentShiftApplication> findAllByCondition(PageRequest pageDTO, SortCondition sortDTO,
			PendingOTTimesheetConditionDTO otTimesheetConditionDTO, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CoherentShiftApplication> criteriaQuery = cb.createQuery(CoherentShiftApplication.class);
		Root<CoherentShiftApplication> otRoot = criteriaQuery.from(CoherentShiftApplication.class);
		criteriaQuery.select(otRoot);

		Predicate restriction = cb.conjunction();

		Join<CoherentShiftApplication, TimesheetBatch> timesheetBatchJoin = otRoot
				.join(CoherentShiftApplication_.timesheetBatch);
		Join<CoherentShiftApplication, TimesheetStatusMaster> otStatusJoin = otRoot
				.join(CoherentShiftApplication_.timesheetStatusMaster);

		restriction = cb.and(restriction,
				cb.equal(otRoot.get(CoherentShiftApplication_.company).get(Company_.companyId), companyId));

		if (StringUtils.isNotBlank(otTimesheetConditionDTO.getCreatedDate())) {
			restriction = cb.and(restriction, cb.equal(otRoot.get(CoherentShiftApplication_.createdDate).as(Date.class),
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
					cb.or(cb.like(otRoot.get(CoherentShiftApplication_.employee).get(Employee_.firstName),
							'%' + otTimesheetConditionDTO.getEmployeeName() + '%'),
							cb.like(
									otRoot.get(CoherentShiftApplication_.employee).get(Employee_.middleName),
									'%' + otTimesheetConditionDTO.getEmployeeName() + '%'),
							cb.like(otRoot.get(CoherentShiftApplication_.employee).get(Employee_.lastName),
									'%' + otTimesheetConditionDTO.getEmployeeName() + '%'),
							cb.like(otRoot.get(CoherentShiftApplication_.employee).get(Employee_.employeeNumber),
									'%' + otTimesheetConditionDTO.getEmployeeName() + '%')));
		}
		criteriaQuery.where(restriction);

		criteriaQuery.orderBy(cb.desc(timesheetBatchJoin.get(TimesheetBatch_.startDate)));

		TypedQuery<CoherentShiftApplication> otTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			otTypedQuery.setFirstResult(getStartPosition(pageDTO));
			otTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		return otTypedQuery.getResultList();
	}

	@Override
	public List<CoherentShiftApplication> findSubmitTimesheetEmp(Long batchId, Long companyId) {

		List<CoherentShiftApplication> emplyeeList = new ArrayList<CoherentShiftApplication>();
		List<String> timesheetStatusNames = new ArrayList<>();
		timesheetStatusNames.add(PayAsiaConstants.LUNDIN_TIMESHEET_STATUS_SUBMITTED);
		timesheetStatusNames.add(PayAsiaConstants.LUNDIN_TIMESHEET_STATUS_APPROVED);
		timesheetStatusNames.add(PayAsiaConstants.LUNDIN_TIMESHEET_STATUS_COMPLETED);
		try {
			CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
			CriteriaQuery<CoherentShiftApplication> criteriaQuery = cb.createQuery(CoherentShiftApplication.class);
			Root<CoherentShiftApplication> root = criteriaQuery.from(CoherentShiftApplication.class);

			Join<CoherentShiftApplication, Company> companyJoin = root.join(CoherentShiftApplication_.company);
			Join<CoherentShiftApplication, TimesheetStatusMaster> timesheetStatusJoin = root
					.join(CoherentShiftApplication_.timesheetStatusMaster);

			criteriaQuery.select(root);
			Predicate restriction = cb.conjunction();

			restriction = cb.and(restriction, cb.equal(companyJoin.get(Company_.companyId), companyId));

			restriction = cb.and(restriction, cb.equal(root.get(CoherentShiftApplication_.timesheetBatch), batchId));

			restriction = cb.and(restriction,
					timesheetStatusJoin.get(TimesheetStatusMaster_.timesheetStatusDesc).in(timesheetStatusNames));

			criteriaQuery.where(restriction);

			TypedQuery<CoherentShiftApplication> timesheetMasterTypedQuery = entityManagerFactory
					.createQuery(criteriaQuery);

			emplyeeList = timesheetMasterTypedQuery.getResultList();
			return emplyeeList;

		} catch (Exception e) {
			return emplyeeList;
		}
	}

	@Override
	public List<CoherentShiftApplication> findByConditionSubmitted(LundinPendingTsheetConditionDTO conditionDTO,
			PageRequest pageDTO, SortCondition sortDTO) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CoherentShiftApplication> criteriaQuery = cb.createQuery(CoherentShiftApplication.class);
		Root<CoherentShiftApplication> root = criteriaQuery.from(CoherentShiftApplication.class);
		criteriaQuery.select(root);

		Predicate restriction = cb.conjunction();

		Join<CoherentShiftApplication, Employee> empChangeReqJoin = root.join(CoherentShiftApplication_.employee);

		Join<CoherentShiftApplication, TimesheetStatusMaster> hrisStatusJoin = root
				.join(CoherentShiftApplication_.timesheetStatusMaster);

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

		TypedQuery<CoherentShiftApplication> typedQuery = entityManagerFactory.createQuery(criteriaQuery);

		// Added by Gaurav for Pagination
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			typedQuery.setFirstResult(getStartPosition(pageDTO));
			typedQuery.setMaxResults(pageDTO.getPageSize());
		}

		return typedQuery.getResultList();
	}

	@Override
	public CoherentShiftApplication findById(long id, Long employeeId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CoherentShiftApplication> criteriaQuery = cb.createQuery(CoherentShiftApplication.class);
		Root<CoherentShiftApplication> otTimesheetWorkflowRoot = criteriaQuery.from(CoherentShiftApplication.class);
		criteriaQuery.select(otTimesheetWorkflowRoot);
		Predicate restriction = cb.conjunction();

		Join<CoherentShiftApplication, Employee> otTimesheetJoin = otTimesheetWorkflowRoot
				.join(CoherentShiftApplication_.employee);

		restriction = cb.and(restriction, cb.equal(otTimesheetJoin.get(Employee_.employeeId), employeeId));

		restriction = cb.and(restriction,
				cb.equal(otTimesheetWorkflowRoot.get(CoherentShiftApplication_.shiftApplicationID), id));
		criteriaQuery.where(restriction);
		TypedQuery<CoherentShiftApplication> otWorkflowTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		otWorkflowTypedQuery.setMaxResults(1);
		List<CoherentShiftApplication> otworkflowList = otWorkflowTypedQuery.getResultList();
		if (otworkflowList != null && !otworkflowList.isEmpty()) {
			return otworkflowList.get(0);
		}
		return null;
	}

	@Override
	public CoherentShiftApplication findByCompanyShiftId(long id, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CoherentShiftApplication> criteriaQuery = cb.createQuery(CoherentShiftApplication.class);
		Root<CoherentShiftApplication> otTimesheetWorkflowRoot = criteriaQuery.from(CoherentShiftApplication.class);
		criteriaQuery.select(otTimesheetWorkflowRoot);
		Predicate restriction = cb.conjunction();

		Join<CoherentShiftApplication, Company> otTimesheetJoin = otTimesheetWorkflowRoot
				.join(CoherentShiftApplication_.company);

		restriction = cb.and(restriction, cb.equal(otTimesheetJoin.get(Company_.companyId), companyId));

		restriction = cb.and(restriction,
				cb.equal(otTimesheetWorkflowRoot.get(CoherentShiftApplication_.shiftApplicationID), id));
		criteriaQuery.where(restriction);
		TypedQuery<CoherentShiftApplication> otWorkflowTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		otWorkflowTypedQuery.setMaxResults(1);
		List<CoherentShiftApplication> otworkflowList = otWorkflowTypedQuery.getResultList();
		if (otworkflowList != null && !otworkflowList.isEmpty()) {
			return otworkflowList.get(0);
		}
		return null;
	}
}
