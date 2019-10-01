package com.payasia.dao.impl;

import java.lang.reflect.InvocationTargetException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import org.springframework.stereotype.Repository;

import com.payasia.common.dto.LundinTsheetConditionDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.util.NullAwareBeanUtilsBean;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.CoherentShiftApplicationDetailDAO;
import com.payasia.dao.bean.AppCodeMaster_;
import com.payasia.dao.bean.CoherentShiftApplication;
import com.payasia.dao.bean.CoherentShiftApplicationDetail;
import com.payasia.dao.bean.CoherentShiftApplicationDetail_;
import com.payasia.dao.bean.CoherentShiftApplicationWorkflow;
import com.payasia.dao.bean.CoherentShiftApplicationWorkflow_;
import com.payasia.dao.bean.CoherentShiftApplication_;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeTimesheetReviewer;
import com.payasia.dao.bean.EmployeeTimesheetReviewer_;
import com.payasia.dao.bean.Employee_;
import com.payasia.dao.bean.TimesheetBatch;
import com.payasia.dao.bean.TimesheetBatch_;
import com.payasia.dao.bean.TimesheetStatusMaster;
import com.payasia.dao.bean.TimesheetStatusMaster_;

@Repository
public class CoherentShiftApplicationDetailDAOImpl extends BaseDAO implements
		CoherentShiftApplicationDetailDAO {
	@Override
	protected Object getBaseEntity() {
		CoherentShiftApplicationDetail timesheet = new CoherentShiftApplicationDetail();
		return timesheet;
	}

	@Override
	public CoherentShiftApplicationDetail findById(long id) {
		return super.findById(CoherentShiftApplicationDetail.class, id);
	}

	@Override
	public void save(
			CoherentShiftApplicationDetail coherentShiftApplicationDetail) {
		super.save(coherentShiftApplicationDetail);
	}

	@Override
	public void update(
			CoherentShiftApplicationDetail coherentShiftApplicationDetail) {
		super.update(coherentShiftApplicationDetail);

	}

	@Override
	public CoherentShiftApplicationDetail saveAndReturn(
			CoherentShiftApplicationDetail coherentShiftApplicationDetail) {
		CoherentShiftApplicationDetail persistObj = coherentShiftApplicationDetail;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (CoherentShiftApplicationDetail) getBaseEntity();
			beanUtil.copyProperties(persistObj, coherentShiftApplicationDetail);
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
	public List<CoherentShiftApplicationDetail> findByShiftId(long id) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CoherentShiftApplicationDetail> criteriaQuery = cb
				.createQuery(CoherentShiftApplicationDetail.class);
		Root<CoherentShiftApplicationDetail> root = criteriaQuery
				.from(CoherentShiftApplicationDetail.class);
		Predicate restriction = cb.conjunction();
		restriction = cb
				.and(restriction,
						cb.equal(
								root.get(
										CoherentShiftApplicationDetail_.coherentShiftApplication)
										.get(CoherentShiftApplication_.shiftApplicationID),
								id));
		/*
		 * criteriaQuery .select(root) .where(restriction) .orderBy(
		 * cb.asc(root.get(CoherentShiftApplicationDetail_.l)
		 * .get(LundinBlock_.blockId)),
		 * cb.asc(root.get(CoherentShiftApplicationDetail_.)));
		 */
		TypedQuery<CoherentShiftApplicationDetail> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		return typedQuery.getResultList();
	}

	@Override
	public List<Tuple> getEmployeeShiftReportDetail(
			LundinTsheetConditionDTO conditionDTO, Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Tuple> criteriaQuery = cb.createQuery(Tuple.class);
		Root<CoherentShiftApplicationDetail> root = criteriaQuery
				.from(CoherentShiftApplicationDetail.class);

		Join<CoherentShiftApplicationDetail, CoherentShiftApplication> appJoin = root
				.join(CoherentShiftApplicationDetail_.coherentShiftApplication);
		Join<CoherentShiftApplication, TimesheetStatusMaster> leaveStatusJoin = appJoin
				.join(CoherentShiftApplication_.timesheetStatusMaster);
		Join<CoherentShiftApplication, Employee> employeeJoin = appJoin
				.join(CoherentShiftApplication_.employee);
		Join<CoherentShiftApplication, TimesheetBatch> batchJoin = appJoin
				.join(CoherentShiftApplication_.timesheetBatch);

		List<Selection<?>> selectionItems = new ArrayList<Selection<?>>();

		selectionItems.add(batchJoin.get(TimesheetBatch_.timesheetBatchId)
				.alias(getAlias(TimesheetBatch_.timesheetBatchId)));
		selectionItems.add(batchJoin.get(TimesheetBatch_.startDate).alias(
				getAlias(TimesheetBatch_.startDate)));
		selectionItems.add(appJoin.get(
				CoherentShiftApplication_.shiftApplicationID).alias(
				getAlias(CoherentShiftApplication_.shiftApplicationID)));
		selectionItems.add(appJoin.get(CoherentShiftApplication_.employee)
				.get(Employee_.employeeId)
				.alias(getAlias(Employee_.employeeId)));

		selectionItems.add(appJoin.get(CoherentShiftApplication_.employee)
				.get(Employee_.employeeNumber)
				.alias(getAlias(Employee_.employeeNumber)));

		selectionItems.add(appJoin.get(CoherentShiftApplication_.employee)
				.get(Employee_.firstName).alias(getAlias(Employee_.firstName)));

		selectionItems.add(appJoin.get(CoherentShiftApplication_.employee)
				.get(Employee_.lastName).alias(getAlias(Employee_.lastName)));

		selectionItems.add(appJoin.get(CoherentShiftApplication_.updatedDate)
				.alias(getAlias(CoherentShiftApplication_.updatedDate)));

		selectionItems.add(appJoin
				.get(CoherentShiftApplication_.timesheetBatch)
				.get(TimesheetBatch_.timesheetBatchDesc)
				.alias(getAlias(TimesheetBatch_.timesheetBatchDesc)));

		selectionItems.add(root.get(CoherentShiftApplicationDetail_.shiftType)
				.get(AppCodeMaster_.codeDesc)
				.alias(getAlias(AppCodeMaster_.codeDesc)));

		selectionItems.add(cb.count(
				root.get(CoherentShiftApplicationDetail_.shift)).alias(
				getAlias(CoherentShiftApplication_.totalShifts)));

		criteriaQuery.multiselect(selectionItems).distinct(true);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(
				restriction,
				cb.equal(
						appJoin.get(CoherentShiftApplication_.company).get(
								Company_.companyId), companyId));

		if (conditionDTO.getEmployeeId() != null) {
			restriction = cb.and(restriction, cb.equal(
					(employeeJoin.get(Employee_.employeeId)),
					conditionDTO.getEmployeeId()));
		}
		if (conditionDTO.getEmployeeReviewerId() != null) {
			Join<Employee, EmployeeTimesheetReviewer> employeeTSReviewerJoin = employeeJoin
					.join(Employee_.employeeTimesheetReviewer1);
			restriction = cb.and(restriction, cb.equal(
					employeeTSReviewerJoin.get(
							EmployeeTimesheetReviewer_.employeeReviewer).get(
							Employee_.employeeId),
					conditionDTO.getEmployeeReviewerId()));
		}
		if (conditionDTO.getStatusNameList().size() > 0) {
			restriction = cb.and(
					restriction,
					leaveStatusJoin.get(
							TimesheetStatusMaster_.timesheetStatusName).in(
							conditionDTO.getStatusNameList()));
		}
		if (conditionDTO.getFromBatchDate() != null) {
			restriction = cb.and(restriction, cb.greaterThanOrEqualTo(
					(batchJoin.get(TimesheetBatch_.startDate)),
					conditionDTO.getFromBatchDate()));
		}
		if (conditionDTO.getToBatchDate() != null) {
			restriction = cb.and(restriction, cb.lessThanOrEqualTo(
					(batchJoin.get(TimesheetBatch_.endDate)),
					conditionDTO.getToBatchDate()));
		}
		if (conditionDTO.getApprovedFromDate() != null) {
			restriction = cb.and(restriction, cb.greaterThanOrEqualTo(
					(appJoin.get(CoherentShiftApplication_.updatedDate)
							.as(Date.class)), conditionDTO
							.getApprovedFromDate()));
		}
		if (conditionDTO.getApprovedToDate() != null) {
			restriction = cb
					.and(restriction, cb.lessThanOrEqualTo((appJoin
							.get(CoherentShiftApplication_.updatedDate)
							.as(Date.class)), conditionDTO.getApprovedToDate()));
		}

		if (conditionDTO.getEmployeeShortListDTO().getEmployeeShortList()
				&& conditionDTO.getEmployeeShortListDTO()
						.getShortListEmployeeIds().size() == 0) {

			restriction = cb.and(restriction,
					employeeJoin.get(Employee_.employeeId).in(-1));

		} else if (conditionDTO.getEmployeeShortListDTO()
				.getEmployeeShortList()
				&& conditionDTO.getEmployeeShortListDTO()
						.getShortListEmployeeIds().size() > 0) {
			restriction = cb.and(
					restriction,
					employeeJoin.get(Employee_.employeeId).in(
							conditionDTO.getEmployeeShortListDTO()
									.getShortListEmployeeIds()));

		}

		if (conditionDTO.isEmpStatus() == false) {
			restriction = cb.and(restriction,
					cb.equal(employeeJoin.get(Employee_.status), true));
		}
		if (!conditionDTO.getShiftType().equalsIgnoreCase("0")) {
			restriction = cb.and(restriction, cb.equal(
					root.get(CoherentShiftApplicationDetail_.shiftType).get(
							AppCodeMaster_.codeDesc),
					conditionDTO.getShiftType()));
		}
		restriction = cb
				.and(restriction, cb.equal(
						root.get(CoherentShiftApplicationDetail_.shift), true));
		criteriaQuery.where(restriction);
		criteriaQuery.groupBy(
				/* appJoin.get(CoherentShiftApplication_.shiftType), */
				batchJoin.get(TimesheetBatch_.timesheetBatchId),
				batchJoin.get(TimesheetBatch_.startDate),
				appJoin.get(CoherentShiftApplication_.shiftApplicationID),
				appJoin.get(CoherentShiftApplication_.employee).get(
						Employee_.employeeId),
				appJoin.get(CoherentShiftApplication_.employee).get(
						Employee_.employeeNumber),

				appJoin.get(CoherentShiftApplication_.employee).get(
						Employee_.firstName),
				appJoin.get(CoherentShiftApplication_.employee).get(
						Employee_.lastName),
				appJoin.get(CoherentShiftApplication_.updatedDate),
				appJoin.get(CoherentShiftApplication_.timesheetBatch).get(
						TimesheetBatch_.timesheetBatchDesc),

				root.get(CoherentShiftApplicationDetail_.shiftType).get(
						AppCodeMaster_.codeDesc),

				appJoin.get(CoherentShiftApplication_.totalShifts));

		// criteriaQuery
		// .orderBy(cb.desc(batchJoin.get(TimesheetBatch_.startDate)));

		TypedQuery<Tuple> claimAppTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return claimAppTypedQuery.getResultList();
	}

	@Override
	public List<Tuple> getEmployeeShiftReportDetailWithRevAppDate(
			LundinTsheetConditionDTO conditionDTO, Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Tuple> criteriaQuery = cb.createQuery(Tuple.class);
		Root<CoherentShiftApplicationDetail> root = criteriaQuery
				.from(CoherentShiftApplicationDetail.class);

		Join<CoherentShiftApplicationDetail, CoherentShiftApplication> appJoin = root
				.join(CoherentShiftApplicationDetail_.coherentShiftApplication);
		Join<CoherentShiftApplication, CoherentShiftApplicationWorkflow> workflowJoin = appJoin
				.join(CoherentShiftApplication_.coherentShiftApplicationWorkflows);
		Join<CoherentShiftApplication, TimesheetStatusMaster> leaveStatusJoin = appJoin
				.join(CoherentShiftApplication_.timesheetStatusMaster);
		Join<CoherentShiftApplication, Employee> employeeJoin = appJoin
				.join(CoherentShiftApplication_.employee);
		Join<CoherentShiftApplication, TimesheetBatch> batchJoin = appJoin
				.join(CoherentShiftApplication_.timesheetBatch);

		List<Selection<?>> selectionItems = new ArrayList<Selection<?>>();

		selectionItems.add(batchJoin.get(TimesheetBatch_.timesheetBatchId)
				.alias(getAlias(TimesheetBatch_.timesheetBatchId)));
		selectionItems.add(batchJoin.get(TimesheetBatch_.startDate).alias(
				getAlias(TimesheetBatch_.startDate)));
		selectionItems.add(appJoin.get(
				CoherentShiftApplication_.shiftApplicationID).alias(
				getAlias(CoherentShiftApplication_.shiftApplicationID)));
		selectionItems.add(appJoin.get(CoherentShiftApplication_.employee)
				.get(Employee_.employeeId)
				.alias(getAlias(Employee_.employeeId)));

		selectionItems.add(appJoin.get(CoherentShiftApplication_.employee)
				.get(Employee_.employeeNumber)
				.alias(getAlias(Employee_.employeeNumber)));

		selectionItems.add(appJoin.get(CoherentShiftApplication_.employee)
				.get(Employee_.firstName).alias(getAlias(Employee_.firstName)));

		selectionItems.add(appJoin.get(CoherentShiftApplication_.employee)
				.get(Employee_.lastName).alias(getAlias(Employee_.lastName)));

		selectionItems.add(workflowJoin.get(
				CoherentShiftApplicationWorkflow_.createdDate).alias(
				getAlias(CoherentShiftApplication_.updatedDate)));

		selectionItems.add(appJoin
				.get(CoherentShiftApplication_.timesheetBatch)
				.get(TimesheetBatch_.timesheetBatchDesc)
				.alias(getAlias(TimesheetBatch_.timesheetBatchDesc)));

		selectionItems.add(root.get(CoherentShiftApplicationDetail_.shiftType)
				.get(AppCodeMaster_.codeDesc)
				.alias(getAlias(AppCodeMaster_.codeDesc)));

		selectionItems.add(cb.count(
				root.get(CoherentShiftApplicationDetail_.shift)).alias(
				getAlias(CoherentShiftApplication_.totalShifts)));

		criteriaQuery.multiselect(selectionItems).distinct(true);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(
				restriction,
				cb.equal(
						appJoin.get(CoherentShiftApplication_.company).get(
								Company_.companyId), companyId));

		if (conditionDTO.getEmployeeId() != null) {
			restriction = cb.and(restriction, cb.equal(
					(employeeJoin.get(Employee_.employeeId)),
					conditionDTO.getEmployeeId()));
		}
		if (conditionDTO.getEmployeeReviewerId() != null) {
			Join<Employee, EmployeeTimesheetReviewer> employeeTSReviewerJoin = employeeJoin
					.join(Employee_.employeeTimesheetReviewer1);
			restriction = cb.and(restriction, cb.equal(
					employeeTSReviewerJoin.get(
							EmployeeTimesheetReviewer_.employeeReviewer).get(
							Employee_.employeeId),
					conditionDTO.getEmployeeReviewerId()));
		}
		if (conditionDTO.getStatusNameList().size() > 0) {
			restriction = cb.and(restriction, cb.equal((leaveStatusJoin
					.get(TimesheetStatusMaster_.timesheetStatusName)),
					PayAsiaConstants.OT_STATUS_COMPLETED));
			restriction = cb
					.and(restriction,
							cb.equal(
									(workflowJoin
											.get(CoherentShiftApplicationWorkflow_.timesheetStatusMaster)
											.get(TimesheetStatusMaster_.timesheetStatusName)),
									PayAsiaConstants.OT_STATUS_COMPLETED));

		}
		if (conditionDTO.getFromBatchDate() != null) {
			restriction = cb.and(restriction, cb.greaterThanOrEqualTo(
					(batchJoin.get(TimesheetBatch_.startDate)),
					conditionDTO.getFromBatchDate()));
		}
		if (conditionDTO.getToBatchDate() != null) {
			restriction = cb.and(restriction, cb.lessThanOrEqualTo(
					(batchJoin.get(TimesheetBatch_.endDate)),
					conditionDTO.getToBatchDate()));
		}
		if (conditionDTO.getApprovedFromDate() != null) {
			restriction = cb.and(restriction, cb.greaterThanOrEqualTo(
					(workflowJoin
							.get(CoherentShiftApplicationWorkflow_.createdDate)
							.as(Date.class)), conditionDTO
							.getApprovedFromDate()));
		}
		if (conditionDTO.getApprovedToDate() != null) {
			restriction = cb
					.and(restriction, cb.lessThanOrEqualTo((workflowJoin
							.get(CoherentShiftApplicationWorkflow_.createdDate)
							.as(Date.class)), conditionDTO.getApprovedToDate()));
		}

		if (conditionDTO.getEmployeeShortListDTO().getEmployeeShortList()
				&& conditionDTO.getEmployeeShortListDTO()
						.getShortListEmployeeIds().size() == 0) {

			restriction = cb.and(restriction,
					employeeJoin.get(Employee_.employeeId).in(-1));

		} else if (conditionDTO.getEmployeeShortListDTO()
				.getEmployeeShortList()
				&& conditionDTO.getEmployeeShortListDTO()
						.getShortListEmployeeIds().size() > 0) {
			restriction = cb.and(
					restriction,
					employeeJoin.get(Employee_.employeeId).in(
							conditionDTO.getEmployeeShortListDTO()
									.getShortListEmployeeIds()));

		}

		if (conditionDTO.isEmpStatus() == false) {
			restriction = cb.and(restriction,
					cb.equal(employeeJoin.get(Employee_.status), true));
		}
		if (!conditionDTO.getShiftType().equalsIgnoreCase("0")) {
			restriction = cb.and(restriction, cb.equal(
					root.get(CoherentShiftApplicationDetail_.shiftType).get(
							AppCodeMaster_.codeDesc),
					conditionDTO.getShiftType()));
		}
		restriction = cb
				.and(restriction, cb.equal(
						root.get(CoherentShiftApplicationDetail_.shift), true));
		criteriaQuery.where(restriction);
		criteriaQuery
				.groupBy(
						/* appJoin.get(CoherentShiftApplication_.shiftType), */
						batchJoin.get(TimesheetBatch_.timesheetBatchId),
						batchJoin.get(TimesheetBatch_.startDate),
						workflowJoin
								.get(CoherentShiftApplicationWorkflow_.createdDate),
						appJoin.get(CoherentShiftApplication_.shiftApplicationID),
						appJoin.get(CoherentShiftApplication_.employee).get(
								Employee_.employeeId),
						appJoin.get(CoherentShiftApplication_.employee).get(
								Employee_.employeeNumber),

						appJoin.get(CoherentShiftApplication_.employee).get(
								Employee_.firstName),
						appJoin.get(CoherentShiftApplication_.employee).get(
								Employee_.lastName),
						appJoin.get(CoherentShiftApplication_.timesheetBatch)
								.get(TimesheetBatch_.timesheetBatchDesc),

						root.get(CoherentShiftApplicationDetail_.shiftType)
								.get(AppCodeMaster_.codeDesc),

						appJoin.get(CoherentShiftApplication_.totalShifts));

		// criteriaQuery
		// .orderBy(cb.desc(batchJoin.get(TimesheetBatch_.startDate)));

		TypedQuery<Tuple> claimAppTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return claimAppTypedQuery.getResultList();
	}

	@Override
	public List<CoherentShiftApplicationDetail> getTimesheetDetailsByTimesheetId(
			long timesheeetId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CoherentShiftApplicationDetail> criteriaQuery = cb
				.createQuery(CoherentShiftApplicationDetail.class);

		Root<CoherentShiftApplicationDetail> root = criteriaQuery
				.from(CoherentShiftApplicationDetail.class);
		Predicate restriction = cb.conjunction();
		restriction = cb
				.and(restriction,
						cb.equal(
								root.get(
										CoherentShiftApplicationDetail_.coherentShiftApplication)
										.get(CoherentShiftApplication_.shiftApplicationID),
								timesheeetId));
		criteriaQuery.select(root).where(restriction);
		TypedQuery<CoherentShiftApplicationDetail> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		try {

			return typedQuery.getResultList();
		} catch (Exception ex) {
			return null;
		}

	}
	
	@Override
	public CoherentShiftApplicationDetail findById(long id, Long employeeId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CoherentShiftApplicationDetail> criteriaQuery = cb
				.createQuery(CoherentShiftApplicationDetail.class);
		Root<CoherentShiftApplicationDetail> root = criteriaQuery
				.from(CoherentShiftApplicationDetail.class);
		criteriaQuery.select(root);

		Predicate restriction = cb.conjunction();
		
		Join<CoherentShiftApplicationDetail, CoherentShiftApplication> detailJoin = root
				.join(CoherentShiftApplicationDetail_.coherentShiftApplication);
		
		Join<CoherentShiftApplication, Employee> empJoin = detailJoin
				.join(CoherentShiftApplication_.employee);

		restriction = cb.and(restriction,
				cb.equal(root.get(CoherentShiftApplicationDetail_.shiftApplicationDetailID),id));
		restriction = cb.and(restriction, cb.equal(
				empJoin.get(Employee_.employeeId),employeeId));
		criteriaQuery.where(restriction);

		TypedQuery<CoherentShiftApplicationDetail> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		List<CoherentShiftApplicationDetail> CoherentShiftApplicationList = typedQuery
				.getResultList();
		if (!CoherentShiftApplicationList.isEmpty()
				&& CoherentShiftApplicationList.size() > 0) {
			return CoherentShiftApplicationList.get(0);
		}
		return null;

	}

}
