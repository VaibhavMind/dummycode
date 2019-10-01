package com.payasia.dao.impl;

import java.lang.reflect.InvocationTargetException;
import java.sql.Date;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.payasia.common.dto.PendingOTTimesheetConditionDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.NullAwareBeanUtilsBean;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.CoherentOvertimeApplicationReviewerDAO;
import com.payasia.dao.bean.CoherentOvertimeApplication;
import com.payasia.dao.bean.CoherentOvertimeApplicationReviewer;
import com.payasia.dao.bean.CoherentOvertimeApplicationReviewer_;
import com.payasia.dao.bean.CoherentOvertimeApplication_;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeTimesheetApplication_;
import com.payasia.dao.bean.Employee_;
import com.payasia.dao.bean.TimesheetBatch;
import com.payasia.dao.bean.TimesheetBatch_;
import com.payasia.dao.bean.TimesheetStatusMaster;
import com.payasia.dao.bean.TimesheetStatusMaster_;

@Repository
public class CoherentOvertimeApplicationReviewerDAOImpl extends BaseDAO
		implements CoherentOvertimeApplicationReviewerDAO {

	private static final Logger LOGGER = Logger
			.getLogger(CoherentOvertimeApplicationReviewerDAOImpl.class);

	@Override
	protected Object getBaseEntity() {
		CoherentOvertimeApplicationReviewer timesheet = new CoherentOvertimeApplicationReviewer();
		return timesheet;
	}

	@Override
	public CoherentOvertimeApplicationReviewer findById(long id) {
		return super.findById(CoherentOvertimeApplicationReviewer.class, id);
	}

	@Override
	public void save(
			CoherentOvertimeApplicationReviewer coherentOvertimeApplicationReviewer) {
		super.save(coherentOvertimeApplicationReviewer);
	}

	@Override
	public void update(
			CoherentOvertimeApplicationReviewer coherentOvertimeApplicationReviewer) {
		super.update(coherentOvertimeApplicationReviewer);

	}

	@Override
	public void delete(
			CoherentOvertimeApplicationReviewer coherentOvertimeApplicationReviewer) {
		super.delete(coherentOvertimeApplicationReviewer);

	}

	@Override
	public CoherentOvertimeApplicationReviewer saveAndReturn(
			CoherentOvertimeApplicationReviewer coherentOvertimeApplicationReviewer) {
		CoherentOvertimeApplicationReviewer persistObj = coherentOvertimeApplicationReviewer;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (CoherentOvertimeApplicationReviewer) getBaseEntity();
			beanUtil.copyProperties(persistObj,
					coherentOvertimeApplicationReviewer);
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
	public List<CoherentOvertimeApplicationReviewer> checkOTEmployeeReviewer(
			long employeeId, List<String> otStatusList) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CoherentOvertimeApplicationReviewer> criteriaQuery = cb
				.createQuery(CoherentOvertimeApplicationReviewer.class);
		Root<CoherentOvertimeApplicationReviewer> root = criteriaQuery
				.from(CoherentOvertimeApplicationReviewer.class);
		criteriaQuery.select(root);

		Predicate restriction = cb.conjunction();

		Join<CoherentOvertimeApplicationReviewer, Employee> empOTJoin = root
				.join(CoherentOvertimeApplicationReviewer_.employeeReviewer);

		Join<CoherentOvertimeApplicationReviewer, CoherentOvertimeApplication> otTimesheetStatusJoin = root
				.join(CoherentOvertimeApplicationReviewer_.coherentOvertimeApplication);
		Join<CoherentOvertimeApplication, TimesheetStatusMaster> otStatusJoin = otTimesheetStatusJoin
				.join(CoherentOvertimeApplication_.timesheetStatusMaster);

		restriction = cb.and(restriction,
				cb.equal(empOTJoin.get(Employee_.employeeId), employeeId));
		restriction = cb.and(restriction,
				otStatusJoin.get(TimesheetStatusMaster_.timesheetStatusName)
						.in(otStatusList));
		criteriaQuery.where(restriction);

		TypedQuery<CoherentOvertimeApplicationReviewer> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		if (typedQuery.getResultList().size() > 0) {
			return typedQuery.getResultList();
		} else {
			return null;
		}
	}

	@Override
	public List<CoherentOvertimeApplicationReviewer> findByCondition(
			Long empId, PageRequest pageDTO, SortCondition sortDTO,
			PendingOTTimesheetConditionDTO otTimesheetConditionDTO,
			Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CoherentOvertimeApplicationReviewer> criteriaQuery = cb
				.createQuery(CoherentOvertimeApplicationReviewer.class);
		Root<CoherentOvertimeApplicationReviewer> otRoot = criteriaQuery
				.from(CoherentOvertimeApplicationReviewer.class);
		criteriaQuery.select(otRoot);

		Predicate restriction = cb.conjunction();

		Join<CoherentOvertimeApplicationReviewer, Employee> employeeReviewerJoin = otRoot
				.join(CoherentOvertimeApplicationReviewer_.employeeReviewer);

		Join<CoherentOvertimeApplicationReviewer, CoherentOvertimeApplication> timesheetJoin = otRoot
				.join(CoherentOvertimeApplicationReviewer_.coherentOvertimeApplication);

		Join<CoherentOvertimeApplication, TimesheetStatusMaster> otStatusJoin = timesheetJoin
				.join(CoherentOvertimeApplication_.timesheetStatusMaster);
		Join<CoherentOvertimeApplication, TimesheetBatch> timesheetBatchJoin = timesheetJoin
				.join(CoherentOvertimeApplication_.timesheetBatch);

		if (StringUtils.isNotBlank(otTimesheetConditionDTO.getCreatedDate())) {
			restriction = cb.and(restriction, cb.equal(
					timesheetJoin.get(CoherentOvertimeApplication_.createdDate)
							.as(Date.class), DateUtils
							.stringToDate(otTimesheetConditionDTO
									.getCreatedDate())));
		}
		restriction = cb
				.and(restriction, cb.equal(
						employeeReviewerJoin.get(Employee_.employeeId), empId));

		if (otTimesheetConditionDTO.isPendingStatus()) {
			restriction = cb.and(restriction, cb.equal(
					otRoot.get(CoherentOvertimeApplicationReviewer_.pending),
					true));
		}

		if (StringUtils.isNotBlank(otTimesheetConditionDTO.getBatch())) {
			restriction = cb
					.and(restriction,
							cb.like(otRoot
									.get(CoherentOvertimeApplicationReviewer_.coherentOvertimeApplication)
									.get(CoherentOvertimeApplication_.timesheetBatch)
									.get(TimesheetBatch_.timesheetBatchDesc),
									'%' + otTimesheetConditionDTO.getBatch() + '%'));
		}
		if (otTimesheetConditionDTO.getStatusNameList().size() > 0) {
			restriction = cb.and(restriction,
					otStatusJoin
							.get(TimesheetStatusMaster_.timesheetStatusName)
							.in(otTimesheetConditionDTO.getStatusNameList()));

		}
		if (StringUtils.isNotBlank(otTimesheetConditionDTO.getEmployeeName())) {
			restriction = cb
					.and(restriction,
							cb.or(cb.like(
									otRoot.get(
											CoherentOvertimeApplicationReviewer_.coherentOvertimeApplication)
											.get(CoherentOvertimeApplication_.employee)
											.get(Employee_.firstName),
									'%' + otTimesheetConditionDTO
											.getEmployeeName() + '%'),
									cb.like(otRoot
											.get(CoherentOvertimeApplicationReviewer_.coherentOvertimeApplication)
											.get(CoherentOvertimeApplication_.employee)
											.get(Employee_.middleName),
											'%' + otTimesheetConditionDTO
													.getEmployeeName() + '%'),
									cb.like(otRoot
											.get(CoherentOvertimeApplicationReviewer_.coherentOvertimeApplication)
											.get(CoherentOvertimeApplication_.employee)
											.get(Employee_.lastName),
											'%' + otTimesheetConditionDTO
													.getEmployeeName() + '%'),
									cb.like(otRoot
											.get(CoherentOvertimeApplicationReviewer_.coherentOvertimeApplication)
											.get(CoherentOvertimeApplication_.employee)
											.get(Employee_.employeeNumber),
											'%' + otTimesheetConditionDTO
													.getEmployeeName() + '%')));
		}
		criteriaQuery.where(restriction);

		criteriaQuery.orderBy(cb.desc(timesheetBatchJoin
				.get(TimesheetBatch_.startDate)));

		TypedQuery<CoherentOvertimeApplicationReviewer> otTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			otTypedQuery.setFirstResult(getStartPosition(pageDTO));
			otTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		return otTypedQuery.getResultList();
	}

	@Override
	public Integer findByConditionCountRecords(Long empId,
			PendingOTTimesheetConditionDTO otTimesheetConditionDTO,
			Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Integer> criteriaQuery = cb.createQuery(Integer.class);
		Root<CoherentOvertimeApplicationReviewer> otRoot = criteriaQuery
				.from(CoherentOvertimeApplicationReviewer.class);
		criteriaQuery.select(cb.count(otRoot).as(Integer.class));

		Predicate restriction = cb.conjunction();

		Join<CoherentOvertimeApplicationReviewer, Employee> empLeaveJoin = otRoot
				.join(CoherentOvertimeApplicationReviewer_.employeeReviewer);

		Join<CoherentOvertimeApplicationReviewer, CoherentOvertimeApplication> lundinOTTimesheetJoin = otRoot
				.join(CoherentOvertimeApplicationReviewer_.coherentOvertimeApplication);

		Join<CoherentOvertimeApplication, TimesheetStatusMaster> otStatusJoin = lundinOTTimesheetJoin
				.join(CoherentOvertimeApplication_.timesheetStatusMaster);

		if (StringUtils.isNotBlank(otTimesheetConditionDTO.getCreatedDate())) {
			restriction = cb.and(restriction, cb.equal(
					lundinOTTimesheetJoin.get(
							CoherentOvertimeApplication_.createdDate).as(
							Date.class), DateUtils
							.stringToDate(otTimesheetConditionDTO
									.getCreatedDate())));
		}
		restriction = cb.and(restriction,
				cb.equal(empLeaveJoin.get(Employee_.employeeId), empId));
		if (otTimesheetConditionDTO.isPendingStatus()) {
			restriction = cb.and(restriction, cb.equal(
					otRoot.get(CoherentOvertimeApplicationReviewer_.pending),
					true));
		}

		if (StringUtils.isNotBlank(otTimesheetConditionDTO.getBatch())) {
			restriction = cb
					.and(restriction,
							cb.like(otRoot
									.get(CoherentOvertimeApplicationReviewer_.coherentOvertimeApplication)
									.get(CoherentOvertimeApplication_.timesheetBatch)
									.get(TimesheetBatch_.timesheetBatchDesc),
									'%' + otTimesheetConditionDTO.getBatch() + '%'));
		}
		if (otTimesheetConditionDTO.getStatusNameList().size() > 0) {
			restriction = cb.and(restriction,
					otStatusJoin
							.get(TimesheetStatusMaster_.timesheetStatusName)
							.in(otTimesheetConditionDTO.getStatusNameList()));

		}
		if (StringUtils.isNotBlank(otTimesheetConditionDTO.getEmployeeName())) {
			restriction = cb
					.and(restriction,
							cb.or(cb.like(
									otRoot.get(
											CoherentOvertimeApplicationReviewer_.coherentOvertimeApplication)
											.get(CoherentOvertimeApplication_.employee)
											.get(Employee_.firstName),
									'%' + otTimesheetConditionDTO
											.getEmployeeName() + '%'),
									cb.like(otRoot
											.get(CoherentOvertimeApplicationReviewer_.coherentOvertimeApplication)
											.get(CoherentOvertimeApplication_.employee)
											.get(Employee_.middleName),
											'%' + otTimesheetConditionDTO
													.getEmployeeName() + '%'),
									cb.like(otRoot
											.get(CoherentOvertimeApplicationReviewer_.coherentOvertimeApplication)
											.get(CoherentOvertimeApplication_.employee)
											.get(Employee_.lastName),
											'%' + otTimesheetConditionDTO
													.getEmployeeName() + '%'),
									cb.like(otRoot
											.get(CoherentOvertimeApplicationReviewer_.coherentOvertimeApplication)
											.get(CoherentOvertimeApplication_.employee)
											.get(Employee_.employeeNumber),
											'%' + otTimesheetConditionDTO
													.getEmployeeName() + '%')));
		}
		criteriaQuery.where(restriction);

		// criteriaQuery.orderBy(cb.desc(lundinOTTimesheetJoin
		// .get(EmployeeTimesheetApplication_.updatedDate)));

		TypedQuery<Integer> otTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return otTypedQuery.getSingleResult();
	}

	@Override
	public List<CoherentOvertimeApplicationReviewer> findAllByCondition(
			Long empId, PageRequest pageDTO, SortCondition sortDTO,
			PendingOTTimesheetConditionDTO otTimesheetConditionDTO,
			Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CoherentOvertimeApplicationReviewer> criteriaQuery = cb
				.createQuery(CoherentOvertimeApplicationReviewer.class);
		Root<CoherentOvertimeApplicationReviewer> otRoot = criteriaQuery
				.from(CoherentOvertimeApplicationReviewer.class);
		criteriaQuery.select(otRoot);

		Predicate restriction = cb.conjunction();

		Join<CoherentOvertimeApplicationReviewer, Employee> empLeaveJoin = otRoot
				.join(CoherentOvertimeApplicationReviewer_.employeeReviewer);

		Join<CoherentOvertimeApplicationReviewer, CoherentOvertimeApplication> timesheetJoin = otRoot
				.join(CoherentOvertimeApplicationReviewer_.coherentOvertimeApplication);

		Join<CoherentOvertimeApplication, TimesheetStatusMaster> otStatusJoin = timesheetJoin
				.join(CoherentOvertimeApplication_.timesheetStatusMaster);
		Join<CoherentOvertimeApplication, TimesheetBatch> timesheetBatchJoin = timesheetJoin
				.join(CoherentOvertimeApplication_.timesheetBatch);

		if (StringUtils.isNotBlank(otTimesheetConditionDTO.getCreatedDate())) {
			restriction = cb.and(restriction, cb.equal(
					timesheetJoin.get(CoherentOvertimeApplication_.createdDate)
							.as(Date.class), DateUtils
							.stringToDate(otTimesheetConditionDTO
									.getCreatedDate())));
		}
		restriction = cb.and(restriction,
				cb.equal(empLeaveJoin.get(Employee_.employeeId), empId));
		restriction = cb.and(restriction, cb.or(cb.and(
				cb.equal(otRoot
						.get(CoherentOvertimeApplicationReviewer_.pending),
						true), cb.equal(otStatusJoin
						.get(TimesheetStatusMaster_.timesheetStatusName),
						PayAsiaConstants.CLAIM_STATUS_SUBMITTED)),
				otStatusJoin.get(TimesheetStatusMaster_.timesheetStatusName)
						.in(otTimesheetConditionDTO.getStatusNameList())));

		if (StringUtils.isNotBlank(otTimesheetConditionDTO.getBatch())) {
			restriction = cb
					.and(restriction,
							cb.like(otRoot
									.get(CoherentOvertimeApplicationReviewer_.coherentOvertimeApplication)
									.get(CoherentOvertimeApplication_.timesheetBatch)
									.get(TimesheetBatch_.timesheetBatchDesc),
									'%' + otTimesheetConditionDTO.getBatch() + '%'));
		}
		if (StringUtils.isNotBlank(otTimesheetConditionDTO.getEmployeeName())) {
			restriction = cb
					.and(restriction,
							cb.or(cb.like(
									otRoot.get(
											CoherentOvertimeApplicationReviewer_.coherentOvertimeApplication)
											.get(CoherentOvertimeApplication_.employee)
											.get(Employee_.firstName),
									'%' + otTimesheetConditionDTO
											.getEmployeeName() + '%'),
									cb.like(otRoot
											.get(CoherentOvertimeApplicationReviewer_.coherentOvertimeApplication)
											.get(CoherentOvertimeApplication_.employee)
											.get(Employee_.middleName),
											'%' + otTimesheetConditionDTO
													.getEmployeeName() + '%'),
									cb.like(otRoot
											.get(CoherentOvertimeApplicationReviewer_.coherentOvertimeApplication)
											.get(CoherentOvertimeApplication_.employee)
											.get(Employee_.lastName),
											'%' + otTimesheetConditionDTO
													.getEmployeeName() + '%'),
									cb.like(otRoot
											.get(CoherentOvertimeApplicationReviewer_.coherentOvertimeApplication)
											.get(CoherentOvertimeApplication_.employee)
											.get(Employee_.employeeNumber),
											'%' + otTimesheetConditionDTO
													.getEmployeeName() + '%')));
		}
		criteriaQuery.where(restriction);

		criteriaQuery.orderBy(cb.desc(timesheetBatchJoin
				.get(TimesheetBatch_.startDate)));

		TypedQuery<CoherentOvertimeApplicationReviewer> otTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			otTypedQuery.setFirstResult(getStartPosition(pageDTO));
			otTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		return otTypedQuery.getResultList();
	}

	@Override
	public Integer getCountForAllByCondition(Long empId,
			PendingOTTimesheetConditionDTO otTimesheetConditionDTO,
			Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Integer> criteriaQuery = cb.createQuery(Integer.class);
		Root<CoherentOvertimeApplicationReviewer> otRoot = criteriaQuery
				.from(CoherentOvertimeApplicationReviewer.class);
		criteriaQuery.select(cb.count(otRoot).as(Integer.class));

		Predicate restriction = cb.conjunction();

		Join<CoherentOvertimeApplicationReviewer, Employee> empLeaveJoin = otRoot
				.join(CoherentOvertimeApplicationReviewer_.employeeReviewer);

		Join<CoherentOvertimeApplicationReviewer, CoherentOvertimeApplication> lundinOTTimesheetJoin = otRoot
				.join(CoherentOvertimeApplicationReviewer_.coherentOvertimeApplication);

		Join<CoherentOvertimeApplication, TimesheetStatusMaster> otStatusJoin = lundinOTTimesheetJoin
				.join(CoherentOvertimeApplication_.timesheetStatusMaster);

		if (StringUtils.isNotBlank(otTimesheetConditionDTO.getCreatedDate())) {
			restriction = cb.and(restriction, cb.equal(
					lundinOTTimesheetJoin.get(
							CoherentOvertimeApplication_.createdDate).as(
							Date.class), DateUtils
							.stringToDate(otTimesheetConditionDTO
									.getCreatedDate())));
		}
		restriction = cb.and(restriction,
				cb.equal(empLeaveJoin.get(Employee_.employeeId), empId));

		restriction = cb.and(restriction, cb.or(cb.and(
				cb.equal(otRoot
						.get(CoherentOvertimeApplicationReviewer_.pending),
						true), cb.equal(otStatusJoin
						.get(TimesheetStatusMaster_.timesheetStatusName),
						PayAsiaConstants.CLAIM_STATUS_SUBMITTED)),
				otStatusJoin.get(TimesheetStatusMaster_.timesheetStatusName)
						.in(otTimesheetConditionDTO.getStatusNameList())));

		if (StringUtils.isNotBlank(otTimesheetConditionDTO.getBatch())) {
			restriction = cb
					.and(restriction,
							cb.like(otRoot
									.get(CoherentOvertimeApplicationReviewer_.coherentOvertimeApplication)
									.get(CoherentOvertimeApplication_.timesheetBatch)
									.get(TimesheetBatch_.timesheetBatchDesc),
									'%' + otTimesheetConditionDTO.getBatch() + '%'));
		}
		if (StringUtils.isNotBlank(otTimesheetConditionDTO.getEmployeeName())) {
			restriction = cb
					.and(restriction,
							cb.or(cb.like(
									otRoot.get(
											CoherentOvertimeApplicationReviewer_.coherentOvertimeApplication)
											.get(CoherentOvertimeApplication_.employee)
											.get(Employee_.firstName),
									'%' + otTimesheetConditionDTO
											.getEmployeeName() + '%'),
									cb.like(otRoot
											.get(CoherentOvertimeApplicationReviewer_.coherentOvertimeApplication)
											.get(CoherentOvertimeApplication_.employee)
											.get(Employee_.middleName),
											'%' + otTimesheetConditionDTO
													.getEmployeeName() + '%'),
									cb.like(otRoot
											.get(CoherentOvertimeApplicationReviewer_.coherentOvertimeApplication)
											.get(CoherentOvertimeApplication_.employee)
											.get(Employee_.lastName),
											'%' + otTimesheetConditionDTO
													.getEmployeeName() + '%'),
									cb.like(otRoot
											.get(CoherentOvertimeApplicationReviewer_.coherentOvertimeApplication)
											.get(CoherentOvertimeApplication_.employee)
											.get(Employee_.employeeNumber),
											'%' + otTimesheetConditionDTO
													.getEmployeeName() + '%')));
		}
		criteriaQuery.where(restriction);

		// criteriaQuery.orderBy(cb.desc(lundinOTTimesheetJoin
		// .get(EmployeeTimesheetApplication_.updatedDate)));

		TypedQuery<Integer> otTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return otTypedQuery.getSingleResult();
	}

	@Override
	public CoherentOvertimeApplicationReviewer findByCondition(
			long timesheetId, long reviewerId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CoherentOvertimeApplicationReviewer> criteriaQuery = cb
				.createQuery(CoherentOvertimeApplicationReviewer.class);
		Root<CoherentOvertimeApplicationReviewer> otReviewerRoot = criteriaQuery
				.from(CoherentOvertimeApplicationReviewer.class);

		Predicate restriction = cb.conjunction();

		restriction = cb
				.and(restriction,
						cb.equal(
								otReviewerRoot
										.get(CoherentOvertimeApplicationReviewer_.coherentOvertimeApplication)
										.get(CoherentOvertimeApplication_.overtimeApplicationID),
								timesheetId));
		restriction = cb.and(restriction, cb.equal(
				otReviewerRoot.get(
						CoherentOvertimeApplicationReviewer_.employeeReviewer)
						.get(Employee_.employeeId), reviewerId));

		criteriaQuery.select(otReviewerRoot).where(restriction);

		TypedQuery<CoherentOvertimeApplicationReviewer> empTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		try {
			return empTypedQuery.getSingleResult();
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			return null;
		}
	}

	@Override
	public List<CoherentOvertimeApplicationReviewer> findByCoherentOvertimeApplication(
			long timesheetId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CoherentOvertimeApplicationReviewer> criteriaQuery = cb
				.createQuery(CoherentOvertimeApplicationReviewer.class);
		Root<CoherentOvertimeApplicationReviewer> otReviewerRoot = criteriaQuery
				.from(CoherentOvertimeApplicationReviewer.class);

		Predicate restriction = cb.conjunction();

		restriction = cb
				.and(restriction,
						cb.equal(
								otReviewerRoot
										.get(CoherentOvertimeApplicationReviewer_.coherentOvertimeApplication)
										.get(CoherentOvertimeApplication_.overtimeApplicationID),
								timesheetId));

		criteriaQuery.select(otReviewerRoot).where(restriction);

		TypedQuery<CoherentOvertimeApplicationReviewer> empTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		try {
			return empTypedQuery.getResultList();
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			return null;
		}
	}

	@Override
	public Integer getOTTimesheetReviewerCount(long timesheetId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Integer> criteriaQuery = cb.createQuery(Integer.class);
		Root<CoherentOvertimeApplicationReviewer> otReviewerRoot = criteriaQuery
				.from(CoherentOvertimeApplicationReviewer.class);
		criteriaQuery.select(cb.count(otReviewerRoot).as(Integer.class));

		Predicate restriction = cb.conjunction();

		restriction = cb
				.and(restriction,
						cb.equal(
								otReviewerRoot
										.get(CoherentOvertimeApplicationReviewer_.coherentOvertimeApplication)
										.get("overtimeApplicationID")
										.as(Long.class), timesheetId));

		criteriaQuery.where(restriction);

		TypedQuery<Integer> empTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		try {
			return empTypedQuery.getSingleResult();
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			return null;
		}
	}

	@Override
	public List<CoherentOvertimeApplicationReviewer> getPendingTimesheetByIds(
			Long empId, List<Long> timesheetIdsList, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CoherentOvertimeApplicationReviewer> criteriaQuery = cb
				.createQuery(CoherentOvertimeApplicationReviewer.class);
		Root<CoherentOvertimeApplicationReviewer> root = criteriaQuery
				.from(CoherentOvertimeApplicationReviewer.class);
		criteriaQuery.select(root);

		Predicate restriction = cb.conjunction();
		Join<CoherentOvertimeApplicationReviewer, CoherentOvertimeApplication> appJoin = root
				.join(CoherentOvertimeApplicationReviewer_.coherentOvertimeApplication);
		Join<CoherentOvertimeApplication, TimesheetStatusMaster> statusJoin = appJoin
				.join(CoherentOvertimeApplication_.timesheetStatusMaster);

		if (empId != null) {
			Join<CoherentOvertimeApplication, Employee> empJoin = appJoin
					.join(CoherentOvertimeApplication_.employee);
			restriction = cb.and(restriction,
					cb.equal(empJoin.get(Employee_.employeeId), empId));
		}
		restriction = cb.and(
				restriction,
				cb.equal(
						appJoin.get(CoherentOvertimeApplication_.company).get(
								Company_.companyId), companyId));
		restriction = cb.and(restriction, cb.equal(
				statusJoin.get(TimesheetStatusMaster_.timesheetStatusName),
				PayAsiaConstants.CLAIM_LIST_STATUS_SUBMITTED));
		restriction = cb.and(restriction, cb.equal(
				root.get(CoherentOvertimeApplicationReviewer_.pending), true));
		restriction = cb.and(restriction,
				appJoin.get(CoherentOvertimeApplication_.overtimeApplicationID)
						.in(timesheetIdsList));

		criteriaQuery.where(restriction);

		TypedQuery<CoherentOvertimeApplicationReviewer> leaveAppTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return leaveAppTypedQuery.getResultList();
	}

	@Override
	public List<CoherentOvertimeApplicationReviewer> findByTimesheetStatus(
			List<String> timesheetStatusNames, Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CoherentOvertimeApplicationReviewer> criteriaQuery = cb
				.createQuery(CoherentOvertimeApplicationReviewer.class);
		Root<CoherentOvertimeApplicationReviewer> empRoot = criteriaQuery
				.from(CoherentOvertimeApplicationReviewer.class);
		Join<CoherentOvertimeApplicationReviewer, CoherentOvertimeApplication> appJoin = empRoot
				.join(CoherentOvertimeApplicationReviewer_.coherentOvertimeApplication);
		criteriaQuery.select(empRoot);

		Predicate restriction = cb.conjunction();

		restriction = cb.equal(
				empRoot.get(CoherentOvertimeApplicationReviewer_.pending),
				PayAsiaConstants.IS_PENDING);
		restriction = cb.equal(
				appJoin.get(CoherentOvertimeApplication_.company).get(
						Company_.companyId), companyId);

		criteriaQuery.where(restriction);

		TypedQuery<CoherentOvertimeApplicationReviewer> timesheetTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		List<CoherentOvertimeApplicationReviewer> timesheetReviewers = timesheetTypedQuery
				.getResultList();

		return timesheetReviewers;
	}

	@Override
	public List<CoherentOvertimeApplicationReviewer> findByCondition(
			Long employeeId, PageRequest pageDTO, SortCondition sortDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CoherentOvertimeApplicationReviewer> criteriaQuery = cb
				.createQuery(CoherentOvertimeApplicationReviewer.class);
		Root<CoherentOvertimeApplicationReviewer> root = criteriaQuery
				.from(CoherentOvertimeApplicationReviewer.class);
		criteriaQuery.select(root);

		Predicate restriction = cb.conjunction();

		Join<CoherentOvertimeApplicationReviewer, Employee> empJoin = root
				.join(CoherentOvertimeApplicationReviewer_.employeeReviewer);
		Join<CoherentOvertimeApplicationReviewer, CoherentOvertimeApplication> oTTimesheetJoin = root
				.join(CoherentOvertimeApplicationReviewer_.coherentOvertimeApplication);

		restriction = cb.and(restriction,
				cb.equal(empJoin.get(Employee_.employeeId), employeeId));

		restriction = cb.and(restriction, cb.equal(
				root.get(CoherentOvertimeApplicationReviewer_.pending), true));

		criteriaQuery.where(restriction);

		criteriaQuery.orderBy(cb.desc(oTTimesheetJoin
				.get(EmployeeTimesheetApplication_.updatedDate)));
		TypedQuery<CoherentOvertimeApplicationReviewer> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			typedQuery.setFirstResult(getStartPosition(pageDTO));
			typedQuery.setMaxResults(pageDTO.getPageSize());
		}

		return typedQuery.getResultList();
	}

}
