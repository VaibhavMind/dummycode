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
import org.springframework.stereotype.Repository;

import com.payasia.common.dto.PendingOTTimesheetConditionDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.NullAwareBeanUtilsBean;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.CoherentShiftApplicationReviewerDAO;
import com.payasia.dao.bean.CoherentShiftApplication;
import com.payasia.dao.bean.CoherentShiftApplicationReviewer;
import com.payasia.dao.bean.CoherentShiftApplicationReviewer_;
import com.payasia.dao.bean.CoherentShiftApplication_;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeTimesheetApplication_;
import com.payasia.dao.bean.Employee_;
import com.payasia.dao.bean.TimesheetBatch_;
import com.payasia.dao.bean.TimesheetStatusMaster;
import com.payasia.dao.bean.TimesheetStatusMaster_;

@Repository
public class CoherentShiftApplicationReviewerDAOImpl extends BaseDAO implements
		CoherentShiftApplicationReviewerDAO {
	@Override
	protected Object getBaseEntity() {
		CoherentShiftApplicationReviewer timesheet = new CoherentShiftApplicationReviewer();
		return timesheet;
	}

	@Override
	public CoherentShiftApplicationReviewer findById(long id) {
		return super.findById(CoherentShiftApplicationReviewer.class, id);
	}

	@Override
	public void save(
			CoherentShiftApplicationReviewer coherentShiftApplicationReviewer) {
		super.save(coherentShiftApplicationReviewer);
	}

	@Override
	public void update(
			CoherentShiftApplicationReviewer coherentShiftApplicationReviewer) {
		super.update(coherentShiftApplicationReviewer);

	}

	@Override
	public CoherentShiftApplicationReviewer saveAndReturn(
			CoherentShiftApplicationReviewer coherentShiftApplicationReviewer) {
		CoherentShiftApplicationReviewer persistObj = coherentShiftApplicationReviewer;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (CoherentShiftApplicationReviewer) getBaseEntity();
			beanUtil.copyProperties(persistObj,
					coherentShiftApplicationReviewer);
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
	public void delete(
			CoherentShiftApplicationReviewer coherentShiftApplicationReviewer) {
		super.delete(coherentShiftApplicationReviewer);

	}

	@Override
	public Integer findByConditionCountRecords(Long empId,
			PendingOTTimesheetConditionDTO otTimesheetConditionDTO,
			Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Integer> criteriaQuery = cb.createQuery(Integer.class);
		Root<CoherentShiftApplicationReviewer> otRoot = criteriaQuery
				.from(CoherentShiftApplicationReviewer.class);
		criteriaQuery.select(cb.count(otRoot).as(Integer.class));

		Predicate restriction = cb.conjunction();

		Join<CoherentShiftApplicationReviewer, Employee> empLeaveJoin = otRoot
				.join(CoherentShiftApplicationReviewer_.employeeReviewer);

		Join<CoherentShiftApplicationReviewer, CoherentShiftApplication> lundinOTTimesheetJoin = otRoot
				.join(CoherentShiftApplicationReviewer_.coherentShiftApplication);

		Join<CoherentShiftApplication, TimesheetStatusMaster> otStatusJoin = lundinOTTimesheetJoin
				.join(CoherentShiftApplication_.timesheetStatusMaster);

		restriction = cb.and(
				restriction,
				cb.equal(
						lundinOTTimesheetJoin.get(
								CoherentShiftApplication_.company).get(
								Company_.companyId), companyId));
		if (StringUtils.isNotBlank(otTimesheetConditionDTO.getCreatedDate())) {
			restriction = cb.and(restriction, cb.equal(lundinOTTimesheetJoin
					.get(CoherentShiftApplication_.createdDate).as(Date.class),
					DateUtils.stringToDate(otTimesheetConditionDTO
							.getCreatedDate())));
		}

		restriction = cb.and(restriction,
				cb.equal(empLeaveJoin.get(Employee_.employeeId), empId));
		if (otTimesheetConditionDTO.isPendingStatus()) {
			restriction = cb.and(restriction,
					cb.equal(otRoot
							.get(CoherentShiftApplicationReviewer_.pending),
							true));
		}
		if (StringUtils.isNotBlank(otTimesheetConditionDTO.getBatch())) {
			restriction = cb
					.and(restriction,
							cb.like(otRoot
									.get(CoherentShiftApplicationReviewer_.coherentShiftApplication)
									.get(CoherentShiftApplication_.timesheetBatch)
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
											CoherentShiftApplicationReviewer_.coherentShiftApplication)
											.get(CoherentShiftApplication_.employee)
											.get(Employee_.firstName),
									'%' + otTimesheetConditionDTO
											.getEmployeeName() + '%'),
									cb.like(otRoot
											.get(CoherentShiftApplicationReviewer_.coherentShiftApplication)
											.get(CoherentShiftApplication_.employee)
											.get(Employee_.middleName),
											'%' + otTimesheetConditionDTO
													.getEmployeeName() + '%'),
									cb.like(otRoot
											.get(CoherentShiftApplicationReviewer_.coherentShiftApplication)
											.get(CoherentShiftApplication_.employee)
											.get(Employee_.lastName),
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
	public List<CoherentShiftApplicationReviewer> findByCondition(Long empId,
			PageRequest pageDTO, SortCondition sortDTO,
			PendingOTTimesheetConditionDTO otTimesheetConditionDTO,
			Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CoherentShiftApplicationReviewer> criteriaQuery = cb
				.createQuery(CoherentShiftApplicationReviewer.class);
		Root<CoherentShiftApplicationReviewer> otRoot = criteriaQuery
				.from(CoherentShiftApplicationReviewer.class);
		criteriaQuery.select(otRoot);

		Predicate restriction = cb.conjunction();

		Join<CoherentShiftApplicationReviewer, Employee> empLeaveJoin = otRoot
				.join(CoherentShiftApplicationReviewer_.employeeReviewer);

		Join<CoherentShiftApplicationReviewer, CoherentShiftApplication> lundinOTTimesheetJoin = otRoot
				.join(CoherentShiftApplicationReviewer_.coherentShiftApplication);

		Join<CoherentShiftApplication, TimesheetStatusMaster> otStatusJoin = lundinOTTimesheetJoin
				.join(CoherentShiftApplication_.timesheetStatusMaster);

		restriction = cb.and(
				restriction,
				cb.equal(
						lundinOTTimesheetJoin.get(
								CoherentShiftApplication_.company).get(
								Company_.companyId), companyId));
		if (StringUtils.isNotBlank(otTimesheetConditionDTO.getCreatedDate())) {
			restriction = cb.and(restriction, cb.equal(lundinOTTimesheetJoin
					.get(CoherentShiftApplication_.createdDate).as(Date.class),
					DateUtils.stringToDate(otTimesheetConditionDTO
							.getCreatedDate())));
		}

		restriction = cb.and(restriction,
				cb.equal(empLeaveJoin.get(Employee_.employeeId), empId));
		if (otTimesheetConditionDTO.isPendingStatus()) {
			restriction = cb.and(restriction,
					cb.equal(otRoot
							.get(CoherentShiftApplicationReviewer_.pending),
							true));
		}
		if (StringUtils.isNotBlank(otTimesheetConditionDTO.getBatch())) {
			restriction = cb
					.and(restriction,
							cb.like(otRoot
									.get(CoherentShiftApplicationReviewer_.coherentShiftApplication)
									.get(CoherentShiftApplication_.timesheetBatch)
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
											CoherentShiftApplicationReviewer_.coherentShiftApplication)
											.get(CoherentShiftApplication_.employee)
											.get(Employee_.firstName),
									'%' + otTimesheetConditionDTO
											.getEmployeeName() + '%'),
									cb.like(otRoot
											.get(CoherentShiftApplicationReviewer_.coherentShiftApplication)
											.get(CoherentShiftApplication_.employee)
											.get(Employee_.middleName),
											'%' + otTimesheetConditionDTO
													.getEmployeeName() + '%'),
									cb.like(otRoot
											.get(CoherentShiftApplicationReviewer_.coherentShiftApplication)
											.get(CoherentShiftApplication_.employee)
											.get(Employee_.lastName),
											'%' + otTimesheetConditionDTO
													.getEmployeeName() + '%')));
		}
		criteriaQuery.where(restriction);

		// criteriaQuery.orderBy(cb.desc(lundinOTTimesheetJoin
		// .get(EmployeeTimesheetApplication_.updatedDate)));

		TypedQuery<CoherentShiftApplicationReviewer> otTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			otTypedQuery.setFirstResult(getStartPosition(pageDTO));
			otTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		return otTypedQuery.getResultList();
	}

	@Override
	public CoherentShiftApplicationReviewer findByCondition(long timesheetId,
			long reviewerId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CoherentShiftApplicationReviewer> criteriaQuery = cb
				.createQuery(CoherentShiftApplicationReviewer.class);
		Root<CoherentShiftApplicationReviewer> otReviewerRoot = criteriaQuery
				.from(CoherentShiftApplicationReviewer.class);

		Predicate restriction = cb.conjunction();

		restriction = cb
				.and(restriction,
						cb.equal(
								otReviewerRoot
										.get(CoherentShiftApplicationReviewer_.coherentShiftApplication)
										.get(CoherentShiftApplication_.shiftApplicationID),
								timesheetId));
		restriction = cb.and(restriction, cb.equal(
				otReviewerRoot.get(
						CoherentShiftApplicationReviewer_.employeeReviewer)
						.get(Employee_.employeeId), reviewerId));

		criteriaQuery.select(otReviewerRoot).where(restriction);

		TypedQuery<CoherentShiftApplicationReviewer> empTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		try {
			return empTypedQuery.getSingleResult();
		} catch (Exception ex) {
			// LOGGER.error(ex.getMessage(), ex);
			return null;
		}
	}

	@Override
	public Integer getOTTimesheetReviewerCount(long timesheetId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Integer> criteriaQuery = cb.createQuery(Integer.class);
		Root<CoherentShiftApplicationReviewer> otReviewerRoot = criteriaQuery
				.from(CoherentShiftApplicationReviewer.class);
		criteriaQuery.select(cb.count(otReviewerRoot).as(Integer.class));

		Predicate restriction = cb.conjunction();

		restriction = cb
				.and(restriction,
						cb.equal(
								otReviewerRoot
										.get(CoherentShiftApplicationReviewer_.coherentShiftApplication)
										.get(CoherentShiftApplication_.shiftApplicationID),
								timesheetId));

		criteriaQuery.where(restriction);

		TypedQuery<Integer> empTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		try {
			return empTypedQuery.getSingleResult();
		} catch (Exception ex) {
			// LOGGER.error(ex.getMessage(), ex);
			return null;
		}

	}

	@Override
	public Integer getCountForAllByCondition(Long empId,
			PendingOTTimesheetConditionDTO otTimesheetConditionDTO,
			Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Integer> criteriaQuery = cb.createQuery(Integer.class);
		Root<CoherentShiftApplicationReviewer> otRoot = criteriaQuery
				.from(CoherentShiftApplicationReviewer.class);
		criteriaQuery.select(cb.count(otRoot).as(Integer.class));

		Predicate restriction = cb.conjunction();

		Join<CoherentShiftApplicationReviewer, Employee> empLeaveJoin = otRoot
				.join(CoherentShiftApplicationReviewer_.employeeReviewer);

		Join<CoherentShiftApplicationReviewer, CoherentShiftApplication> lundinOTTimesheetJoin = otRoot
				.join(CoherentShiftApplicationReviewer_.coherentShiftApplication);

		Join<CoherentShiftApplication, TimesheetStatusMaster> otStatusJoin = lundinOTTimesheetJoin
				.join(CoherentShiftApplication_.timesheetStatusMaster);

		restriction = cb.and(
				restriction,
				cb.equal(
						lundinOTTimesheetJoin.get(
								CoherentShiftApplication_.company).get(
								Company_.companyId), companyId));

		if (StringUtils.isNotBlank(otTimesheetConditionDTO.getCreatedDate())) {
			restriction = cb.and(restriction, cb.equal(lundinOTTimesheetJoin
					.get(CoherentShiftApplication_.createdDate).as(Date.class),
					DateUtils.stringToDate(otTimesheetConditionDTO
							.getCreatedDate())));
		}

		restriction = cb.and(restriction,
				cb.equal(empLeaveJoin.get(Employee_.employeeId), empId));
		// if (otTimesheetConditionDTO.isPendingStatus()) {
		// restriction = cb.and(restriction, cb.equal(
		// otRoot.get(CoherentOvertimeApplicationReviewer_.pending),
		// true));
		// }

		restriction = cb.and(restriction, cb.or(cb.and(cb.equal(
				otRoot.get(CoherentShiftApplicationReviewer_.pending), true),
				cb.equal(otStatusJoin
						.get(TimesheetStatusMaster_.timesheetStatusName),
						PayAsiaConstants.CLAIM_STATUS_SUBMITTED)),
				otStatusJoin.get(TimesheetStatusMaster_.timesheetStatusName)
						.in(otTimesheetConditionDTO.getStatusNameList())));

		if (StringUtils.isNotBlank(otTimesheetConditionDTO.getBatch())) {
			restriction = cb
					.and(restriction,
							cb.like(otRoot
									.get(CoherentShiftApplicationReviewer_.coherentShiftApplication)
									.get(CoherentShiftApplication_.timesheetBatch)
									.get(TimesheetBatch_.timesheetBatchDesc),
									'%' + otTimesheetConditionDTO.getBatch() + '%'));
		}
		// if (otTimesheetConditionDTO.getStatusNameList().size() > 0) {
		// restriction = cb.and(restriction,
		// otStatusJoin
		// .get(TimesheetStatusMaster_.timesheetStatusName)
		// .in(otTimesheetConditionDTO.getStatusNameList()));
		//
		// }
		if (StringUtils.isNotBlank(otTimesheetConditionDTO.getEmployeeName())) {
			restriction = cb
					.and(restriction,
							cb.or(cb.like(
									otRoot.get(
											CoherentShiftApplicationReviewer_.coherentShiftApplication)
											.get(CoherentShiftApplication_.employee)
											.get(Employee_.firstName),
									'%' + otTimesheetConditionDTO
											.getEmployeeName() + '%'),
									cb.like(otRoot
											.get(CoherentShiftApplicationReviewer_.coherentShiftApplication)
											.get(CoherentShiftApplication_.employee)
											.get(Employee_.middleName),
											'%' + otTimesheetConditionDTO
													.getEmployeeName() + '%'),
									cb.like(otRoot
											.get(CoherentShiftApplicationReviewer_.coherentShiftApplication)
											.get(CoherentShiftApplication_.employee)
											.get(Employee_.lastName),
											'%' + otTimesheetConditionDTO
													.getEmployeeName() + '%'),
									cb.like(otRoot
											.get(CoherentShiftApplicationReviewer_.coherentShiftApplication)
											.get(CoherentShiftApplication_.employee)
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
	public List<CoherentShiftApplicationReviewer> findAllByCondition(
			Long empId, PageRequest pageDTO, SortCondition sortDTO,
			PendingOTTimesheetConditionDTO otTimesheetConditionDTO,
			Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CoherentShiftApplicationReviewer> criteriaQuery = cb
				.createQuery(CoherentShiftApplicationReviewer.class);
		Root<CoherentShiftApplicationReviewer> otRoot = criteriaQuery
				.from(CoherentShiftApplicationReviewer.class);
		criteriaQuery.select(otRoot);

		Predicate restriction = cb.conjunction();

		Join<CoherentShiftApplicationReviewer, Employee> empLeaveJoin = otRoot
				.join(CoherentShiftApplicationReviewer_.employeeReviewer);

		Join<CoherentShiftApplicationReviewer, CoherentShiftApplication> lundinOTTimesheetJoin = otRoot
				.join(CoherentShiftApplicationReviewer_.coherentShiftApplication);

		Join<CoherentShiftApplication, TimesheetStatusMaster> otStatusJoin = lundinOTTimesheetJoin
				.join(CoherentShiftApplication_.timesheetStatusMaster);

		restriction = cb.and(
				restriction,
				cb.equal(
						lundinOTTimesheetJoin.get(
								CoherentShiftApplication_.company).get(
								Company_.companyId), companyId));

		if (StringUtils.isNotBlank(otTimesheetConditionDTO.getCreatedDate())) {
			restriction = cb.and(restriction, cb.equal(lundinOTTimesheetJoin
					.get(CoherentShiftApplication_.createdDate).as(Date.class),
					DateUtils.stringToDate(otTimesheetConditionDTO
							.getCreatedDate())));
		}

		restriction = cb.and(restriction,
				cb.equal(empLeaveJoin.get(Employee_.employeeId), empId));
		// if (otTimesheetConditionDTO.isPendingStatus()) {
		// restriction = cb.and(restriction, cb.equal(
		// otRoot.get(CoherentOvertimeApplicationReviewer_.pending),
		// true));
		// }

		restriction = cb.and(restriction, cb.or(cb.and(cb.equal(
				otRoot.get(CoherentShiftApplicationReviewer_.pending), true),
				cb.equal(otStatusJoin
						.get(TimesheetStatusMaster_.timesheetStatusName),
						PayAsiaConstants.CLAIM_STATUS_SUBMITTED)),
				otStatusJoin.get(TimesheetStatusMaster_.timesheetStatusName)
						.in(otTimesheetConditionDTO.getStatusNameList())));

		if (StringUtils.isNotBlank(otTimesheetConditionDTO.getBatch())) {
			restriction = cb
					.and(restriction,
							cb.like(otRoot
									.get(CoherentShiftApplicationReviewer_.coherentShiftApplication)
									.get(CoherentShiftApplication_.timesheetBatch)
									.get(TimesheetBatch_.timesheetBatchDesc),
									'%' + otTimesheetConditionDTO.getBatch() + '%'));
		}
		// if (otTimesheetConditionDTO.getStatusNameList().size() > 0) {
		// restriction = cb.and(restriction,
		// otStatusJoin
		// .get(TimesheetStatusMaster_.timesheetStatusName)
		// .in(otTimesheetConditionDTO.getStatusNameList()));
		//
		// }
		if (StringUtils.isNotBlank(otTimesheetConditionDTO.getEmployeeName())) {
			restriction = cb
					.and(restriction,
							cb.or(cb.like(
									otRoot.get(
											CoherentShiftApplicationReviewer_.coherentShiftApplication)
											.get(CoherentShiftApplication_.employee)
											.get(Employee_.firstName),
									'%' + otTimesheetConditionDTO
											.getEmployeeName() + '%'),
									cb.like(otRoot
											.get(CoherentShiftApplicationReviewer_.coherentShiftApplication)
											.get(CoherentShiftApplication_.employee)
											.get(Employee_.middleName),
											'%' + otTimesheetConditionDTO
													.getEmployeeName() + '%'),
									cb.like(otRoot
											.get(CoherentShiftApplicationReviewer_.coherentShiftApplication)
											.get(CoherentShiftApplication_.employee)
											.get(Employee_.lastName),
											'%' + otTimesheetConditionDTO
													.getEmployeeName() + '%'),
									cb.like(otRoot
											.get(CoherentShiftApplicationReviewer_.coherentShiftApplication)
											.get(CoherentShiftApplication_.employee)
											.get(Employee_.employeeNumber),
											'%' + otTimesheetConditionDTO
													.getEmployeeName() + '%')));
		}
		criteriaQuery.where(restriction);

		// criteriaQuery.orderBy(cb.desc(lundinOTTimesheetJoin
		// .get(EmployeeTimesheetApplication_.updatedDate)));

		TypedQuery<CoherentShiftApplicationReviewer> otTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			otTypedQuery.setFirstResult(getStartPosition(pageDTO));
			otTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		return otTypedQuery.getResultList();
	}

	@Override
	public List<CoherentShiftApplicationReviewer> getPendingTimesheetByIds(
			Long empId, List<Long> timesheetIdsList) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CoherentShiftApplicationReviewer> criteriaQuery = cb
				.createQuery(CoherentShiftApplicationReviewer.class);
		Root<CoherentShiftApplicationReviewer> root = criteriaQuery
				.from(CoherentShiftApplicationReviewer.class);
		criteriaQuery.select(root);

		Predicate restriction = cb.conjunction();
		Join<CoherentShiftApplicationReviewer, CoherentShiftApplication> appJoin = root
				.join(CoherentShiftApplicationReviewer_.coherentShiftApplication);
		Join<CoherentShiftApplication, TimesheetStatusMaster> statusJoin = appJoin
				.join(CoherentShiftApplication_.timesheetStatusMaster);

		if (empId != null) {
			Join<CoherentShiftApplication, Employee> empJoin = appJoin
					.join(CoherentShiftApplication_.employee);
			restriction = cb.and(restriction,
					cb.equal(empJoin.get(Employee_.employeeId), empId));
		}
		restriction = cb.and(restriction, cb.equal(
				statusJoin.get(TimesheetStatusMaster_.timesheetStatusName),
				PayAsiaConstants.CLAIM_LIST_STATUS_SUBMITTED));
		restriction = cb.and(restriction, cb.equal(
				root.get(CoherentShiftApplicationReviewer_.pending), true));
		restriction = cb.and(
				restriction,
				appJoin.get(CoherentShiftApplication_.shiftApplicationID).in(
						timesheetIdsList));

		criteriaQuery.where(restriction);

		TypedQuery<CoherentShiftApplicationReviewer> leaveAppTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return leaveAppTypedQuery.getResultList();
	}

	@Override
	public List<CoherentShiftApplicationReviewer> findByTimesheetStatus(
			List<String> timesheetStatusNames, Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CoherentShiftApplicationReviewer> criteriaQuery = cb
				.createQuery(CoherentShiftApplicationReviewer.class);
		Root<CoherentShiftApplicationReviewer> empRoot = criteriaQuery
				.from(CoherentShiftApplicationReviewer.class);
		Join<CoherentShiftApplicationReviewer, CoherentShiftApplication> appJoin = empRoot
				.join(CoherentShiftApplicationReviewer_.coherentShiftApplication);
		criteriaQuery.select(empRoot);

		Predicate restriction = cb.conjunction();

		restriction = cb.equal(
				empRoot.get(CoherentShiftApplicationReviewer_.pending),
				PayAsiaConstants.IS_PENDING);
		restriction = cb.equal(appJoin.get(CoherentShiftApplication_.company)
				.get(Company_.companyId), companyId);

		criteriaQuery.where(restriction);

		TypedQuery<CoherentShiftApplicationReviewer> timesheetTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		List<CoherentShiftApplicationReviewer> timesheetReviewers = timesheetTypedQuery
				.getResultList();

		return timesheetReviewers;
	}

	@Override
	public List<CoherentShiftApplicationReviewer> findByCondition(
			Long employeeId, PageRequest pageDTO, SortCondition sortDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CoherentShiftApplicationReviewer> criteriaQuery = cb
				.createQuery(CoherentShiftApplicationReviewer.class);
		Root<CoherentShiftApplicationReviewer> root = criteriaQuery
				.from(CoherentShiftApplicationReviewer.class);
		criteriaQuery.select(root);

		Predicate restriction = cb.conjunction();

		Join<CoherentShiftApplicationReviewer, Employee> empJoin = root
				.join(CoherentShiftApplicationReviewer_.employeeReviewer);
		Join<CoherentShiftApplicationReviewer, CoherentShiftApplication> oTTimesheetJoin = root
				.join(CoherentShiftApplicationReviewer_.coherentShiftApplication);

		restriction = cb.and(restriction,
				cb.equal(empJoin.get(Employee_.employeeId), employeeId));

		restriction = cb.and(restriction, cb.equal(
				root.get(CoherentShiftApplicationReviewer_.pending), true));

		criteriaQuery.where(restriction);

		criteriaQuery.orderBy(cb.desc(oTTimesheetJoin
				.get(EmployeeTimesheetApplication_.updatedDate)));
		TypedQuery<CoherentShiftApplicationReviewer> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			typedQuery.setFirstResult(getStartPosition(pageDTO));
			typedQuery.setMaxResults(pageDTO.getPageSize());
		}

		return typedQuery.getResultList();
	}

	@Override
	public List<CoherentShiftApplicationReviewer> findByCoherentShiftApplication(
			long timesheetId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CoherentShiftApplicationReviewer> criteriaQuery = cb
				.createQuery(CoherentShiftApplicationReviewer.class);
		Root<CoherentShiftApplicationReviewer> otReviewerRoot = criteriaQuery
				.from(CoherentShiftApplicationReviewer.class);

		Predicate restriction = cb.conjunction();

		restriction = cb
				.and(restriction,
						cb.equal(
								otReviewerRoot
										.get(CoherentShiftApplicationReviewer_.coherentShiftApplication)
										.get(CoherentShiftApplication_.shiftApplicationID),
								timesheetId));

		criteriaQuery.select(otReviewerRoot).where(restriction);

		TypedQuery<CoherentShiftApplicationReviewer> empTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		try {
			return empTypedQuery.getResultList();
		} catch (Exception ex) {
			return null;
		}
	}

}
