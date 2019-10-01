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
import com.payasia.dao.CoherentOvertimeApplicationDetailDAO;
import com.payasia.dao.bean.AppCodeMaster_;
import com.payasia.dao.bean.CoherentOvertimeApplication;
import com.payasia.dao.bean.CoherentOvertimeApplicationDetail;
import com.payasia.dao.bean.CoherentOvertimeApplicationDetail_;
import com.payasia.dao.bean.CoherentOvertimeApplicationWorkflow;
import com.payasia.dao.bean.CoherentOvertimeApplicationWorkflow_;
import com.payasia.dao.bean.CoherentOvertimeApplication_;
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
public class CoherentOvertimeApplicationDetailDAOImpl extends BaseDAO implements
		CoherentOvertimeApplicationDetailDAO {
	@Override
	protected Object getBaseEntity() {
		CoherentOvertimeApplicationDetail timesheet = new CoherentOvertimeApplicationDetail();
		return timesheet;
	}

	@Override
	public CoherentOvertimeApplicationDetail findById(long id) {
		return super.findById(CoherentOvertimeApplicationDetail.class, id);
	}

	@Override
	public void save(
			CoherentOvertimeApplicationDetail coherentOvertimeApplicationDetail) {
		super.save(coherentOvertimeApplicationDetail);
	}

	@Override
	public void update(
			CoherentOvertimeApplicationDetail coherentOvertimeApplicationDetail) {
		super.update(coherentOvertimeApplicationDetail);

	}

	@Override
	public CoherentOvertimeApplicationDetail saveAndReturn(
			CoherentOvertimeApplicationDetail coherentOvertimeApplicationDetail) {
		CoherentOvertimeApplicationDetail persistObj = coherentOvertimeApplicationDetail;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (CoherentOvertimeApplicationDetail) getBaseEntity();
			beanUtil.copyProperties(persistObj,
					coherentOvertimeApplicationDetail);
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
	public List<CoherentOvertimeApplicationDetail> getTimesheetDetailsByTimesheetId(
			long timesheeetId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CoherentOvertimeApplicationDetail> criteriaQuery = cb
				.createQuery(CoherentOvertimeApplicationDetail.class);

		Root<CoherentOvertimeApplicationDetail> root = criteriaQuery
				.from(CoherentOvertimeApplicationDetail.class);
		Predicate restriction = cb.conjunction();
		restriction = cb
				.and(restriction,
						cb.equal(
								root.get(
										CoherentOvertimeApplicationDetail_.coherentOvertimeApplication)
										.get(CoherentOvertimeApplication_.overtimeApplicationID),
								timesheeetId));
		criteriaQuery.select(root).where(restriction);
		TypedQuery<CoherentOvertimeApplicationDetail> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		try {

			return typedQuery.getResultList();
		} catch (Exception ex) {
			return null;
		}

	}

	@Override
	public List<Tuple> getEmployeeOvertimeReportDetail(
			LundinTsheetConditionDTO conditionDTO, Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Tuple> criteriaQuery = cb.createQuery(Tuple.class);
		Root<CoherentOvertimeApplicationDetail> root = criteriaQuery
				.from(CoherentOvertimeApplicationDetail.class);

		Join<CoherentOvertimeApplicationDetail, CoherentOvertimeApplication> appJoin = root
				.join(CoherentOvertimeApplicationDetail_.coherentOvertimeApplication);
		Join<CoherentOvertimeApplication, TimesheetStatusMaster> leaveStatusJoin = appJoin
				.join(CoherentOvertimeApplication_.timesheetStatusMaster);
		Join<CoherentOvertimeApplication, Employee> employeeJoin = appJoin
				.join(CoherentOvertimeApplication_.employee);
		Join<CoherentOvertimeApplication, TimesheetBatch> batchJoin = appJoin
				.join(CoherentOvertimeApplication_.timesheetBatch);

		List<Selection<?>> selectionItems = new ArrayList<Selection<?>>();

		selectionItems.add(batchJoin.get(TimesheetBatch_.timesheetBatchId)
				.alias(getAlias(TimesheetBatch_.timesheetBatchId)));
		selectionItems.add(batchJoin.get(TimesheetBatch_.timesheetBatchDesc)
				.alias(getAlias(TimesheetBatch_.timesheetBatchDesc)));
		selectionItems.add(batchJoin.get(TimesheetBatch_.startDate).alias(
				getAlias(TimesheetBatch_.startDate)));
		selectionItems.add(appJoin.get(
				CoherentOvertimeApplication_.overtimeApplicationID).alias(
				getAlias(CoherentOvertimeApplication_.overtimeApplicationID)));
		selectionItems.add(appJoin.get(CoherentOvertimeApplication_.employee)
				.get(Employee_.employeeId)
				.alias(getAlias(Employee_.employeeId)));
		selectionItems.add(appJoin.get(CoherentOvertimeApplication_.employee)
				.get(Employee_.employeeNumber)
				.alias(getAlias(Employee_.employeeNumber)));
		selectionItems.add(appJoin.get(CoherentOvertimeApplication_.employee)
				.get(Employee_.firstName).alias(getAlias(Employee_.firstName)));
		selectionItems.add(appJoin.get(CoherentOvertimeApplication_.employee)
				.get(Employee_.lastName).alias(getAlias(Employee_.lastName)));
		selectionItems.add(appJoin
				.get(CoherentOvertimeApplication_.updatedDate).alias(
						getAlias(CoherentOvertimeApplication_.updatedDate)));
		selectionItems.add(root.get(CoherentOvertimeApplicationDetail_.dayType)
				.get(AppCodeMaster_.codeDesc)
				.alias(getAlias(AppCodeMaster_.codeDesc)));
		selectionItems.add(cb.sum(
				root.get(CoherentOvertimeApplicationDetail_.otHours)).alias(
				getAlias(CoherentOvertimeApplicationDetail_.otHours)));
		selectionItems.add(cb.sum(
				root.get(CoherentOvertimeApplicationDetail_.ot15Hours)).alias(
				getAlias(CoherentOvertimeApplicationDetail_.ot15Hours)));
		selectionItems.add(cb.sum(
				root.get(CoherentOvertimeApplicationDetail_.ot10Day)).alias(
				getAlias(CoherentOvertimeApplicationDetail_.ot10Day)));
		selectionItems.add(cb.sum(
				root.get(CoherentOvertimeApplicationDetail_.ot20Day)).alias(
				getAlias(CoherentOvertimeApplicationDetail_.ot20Day)));
		criteriaQuery.multiselect(selectionItems).distinct(true);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(
				restriction,
				cb.equal(
						appJoin.get(CoherentOvertimeApplication_.company).get(
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
			restriction = cb.and(restriction, cb.greaterThanOrEqualTo((appJoin
					.get(CoherentOvertimeApplication_.updatedDate)
					.as(Date.class)), conditionDTO.getApprovedFromDate()));
		}
		if (conditionDTO.getApprovedToDate() != null) {
			restriction = cb.and(restriction, cb.lessThanOrEqualTo((appJoin
					.get(CoherentOvertimeApplication_.updatedDate)
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

		criteriaQuery.where(restriction);
		criteriaQuery
				.groupBy(
						batchJoin.get(TimesheetBatch_.timesheetBatchId),
						batchJoin.get(TimesheetBatch_.timesheetBatchDesc),
						batchJoin.get(TimesheetBatch_.startDate),
						root.get(CoherentOvertimeApplicationDetail_.dayType)
								.get(AppCodeMaster_.codeDesc),
						appJoin.get(CoherentOvertimeApplication_.overtimeApplicationID),
						appJoin.get(CoherentOvertimeApplication_.employee).get(
								Employee_.employeeId),
						appJoin.get(CoherentOvertimeApplication_.employee).get(
								Employee_.employeeNumber),
						appJoin.get(CoherentOvertimeApplication_.employee).get(
								Employee_.firstName),
						appJoin.get(CoherentOvertimeApplication_.employee).get(
								Employee_.lastName), appJoin
								.get(CoherentOvertimeApplication_.updatedDate));
		// criteriaQuery
		// .orderBy(cb.desc(batchJoin.get(TimesheetBatch_.startDate)));

		TypedQuery<Tuple> claimAppTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return claimAppTypedQuery.getResultList();
	}

	@Override
	public List<Tuple> getEmployeeOvertimeReportDetailWithRevAppDate(
			LundinTsheetConditionDTO conditionDTO, Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Tuple> criteriaQuery = cb.createQuery(Tuple.class);
		Root<CoherentOvertimeApplicationDetail> root = criteriaQuery
				.from(CoherentOvertimeApplicationDetail.class);

		Join<CoherentOvertimeApplicationDetail, CoherentOvertimeApplication> appJoin = root
				.join(CoherentOvertimeApplicationDetail_.coherentOvertimeApplication);
		Join<CoherentOvertimeApplication, CoherentOvertimeApplicationWorkflow> workflowJoin = appJoin
				.join(CoherentOvertimeApplication_.coherentOvertimeApplicationWorkflows);
		Join<CoherentOvertimeApplication, TimesheetStatusMaster> leaveStatusJoin = appJoin
				.join(CoherentOvertimeApplication_.timesheetStatusMaster);
		Join<CoherentOvertimeApplication, Employee> employeeJoin = appJoin
				.join(CoherentOvertimeApplication_.employee);
		Join<CoherentOvertimeApplication, TimesheetBatch> batchJoin = appJoin
				.join(CoherentOvertimeApplication_.timesheetBatch);

		List<Selection<?>> selectionItems = new ArrayList<Selection<?>>();

		selectionItems.add(batchJoin.get(TimesheetBatch_.timesheetBatchId)
				.alias(getAlias(TimesheetBatch_.timesheetBatchId)));
		selectionItems.add(batchJoin.get(TimesheetBatch_.timesheetBatchDesc)
				.alias(getAlias(TimesheetBatch_.timesheetBatchDesc)));
		selectionItems.add(batchJoin.get(TimesheetBatch_.startDate).alias(
				getAlias(TimesheetBatch_.startDate)));
		selectionItems.add(appJoin.get(
				CoherentOvertimeApplication_.overtimeApplicationID).alias(
				getAlias(CoherentOvertimeApplication_.overtimeApplicationID)));
		selectionItems.add(appJoin.get(CoherentOvertimeApplication_.employee)
				.get(Employee_.employeeId)
				.alias(getAlias(Employee_.employeeId)));
		selectionItems.add(appJoin.get(CoherentOvertimeApplication_.employee)
				.get(Employee_.employeeNumber)
				.alias(getAlias(Employee_.employeeNumber)));
		selectionItems.add(appJoin.get(CoherentOvertimeApplication_.employee)
				.get(Employee_.firstName).alias(getAlias(Employee_.firstName)));
		selectionItems.add(appJoin.get(CoherentOvertimeApplication_.employee)
				.get(Employee_.lastName).alias(getAlias(Employee_.lastName)));
		selectionItems.add(workflowJoin.get(
				CoherentOvertimeApplicationWorkflow_.createdDate).alias(
				getAlias(CoherentOvertimeApplication_.updatedDate)));
		selectionItems.add(root.get(CoherentOvertimeApplicationDetail_.dayType)
				.get(AppCodeMaster_.codeDesc)
				.alias(getAlias(AppCodeMaster_.codeDesc)));
		selectionItems.add(cb.sum(
				root.get(CoherentOvertimeApplicationDetail_.otHours)).alias(
				getAlias(CoherentOvertimeApplicationDetail_.otHours)));
		selectionItems.add(cb.sum(
				root.get(CoherentOvertimeApplicationDetail_.ot15Hours)).alias(
				getAlias(CoherentOvertimeApplicationDetail_.ot15Hours)));
		selectionItems.add(cb.sum(
				root.get(CoherentOvertimeApplicationDetail_.ot10Day)).alias(
				getAlias(CoherentOvertimeApplicationDetail_.ot10Day)));
		selectionItems.add(cb.sum(
				root.get(CoherentOvertimeApplicationDetail_.ot20Day)).alias(
				getAlias(CoherentOvertimeApplicationDetail_.ot20Day)));
		criteriaQuery.multiselect(selectionItems).distinct(true);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(
				restriction,
				cb.equal(
						appJoin.get(CoherentOvertimeApplication_.company).get(
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
											.get(CoherentOvertimeApplicationWorkflow_.timesheetStatusMaster)
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
			restriction = cb
					.and(restriction,
							cb.greaterThanOrEqualTo(
									(workflowJoin
											.get(CoherentOvertimeApplicationWorkflow_.createdDate)
											.as(Date.class)), conditionDTO
											.getApprovedFromDate()));
		}
		if (conditionDTO.getApprovedToDate() != null) {
			restriction = cb
					.and(restriction,
							cb.lessThanOrEqualTo(
									(workflowJoin
											.get(CoherentOvertimeApplicationWorkflow_.createdDate)
											.as(Date.class)), conditionDTO
											.getApprovedToDate()));
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

		criteriaQuery.where(restriction);
		criteriaQuery
				.groupBy(
						batchJoin.get(TimesheetBatch_.timesheetBatchId),
						batchJoin.get(TimesheetBatch_.timesheetBatchDesc),
						batchJoin.get(TimesheetBatch_.startDate),
						workflowJoin
								.get(CoherentOvertimeApplicationWorkflow_.createdDate),
						root.get(CoherentOvertimeApplicationDetail_.dayType)
								.get(AppCodeMaster_.codeDesc),
						appJoin.get(CoherentOvertimeApplication_.overtimeApplicationID),
						appJoin.get(CoherentOvertimeApplication_.employee).get(
								Employee_.employeeId),
						appJoin.get(CoherentOvertimeApplication_.employee).get(
								Employee_.employeeNumber),
						appJoin.get(CoherentOvertimeApplication_.employee).get(
								Employee_.firstName),
						appJoin.get(CoherentOvertimeApplication_.employee).get(
								Employee_.lastName), appJoin
								.get(CoherentOvertimeApplication_.updatedDate));
		// criteriaQuery
		// .orderBy(cb.desc(batchJoin.get(TimesheetBatch_.startDate)));

		TypedQuery<Tuple> claimAppTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return claimAppTypedQuery.getResultList();
	}

	@Override
	public CoherentOvertimeApplicationDetail findById(long id, Long employeeId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CoherentOvertimeApplicationDetail> criteriaQuery = cb
				.createQuery(CoherentOvertimeApplicationDetail.class);
		Root<CoherentOvertimeApplicationDetail> root = criteriaQuery
				.from(CoherentOvertimeApplicationDetail.class);
		criteriaQuery.select(root);

		Predicate restriction = cb.conjunction();
		
		Join<CoherentOvertimeApplicationDetail, CoherentOvertimeApplication> detailJoin = root
				.join(CoherentOvertimeApplicationDetail_.coherentOvertimeApplication);
		
		Join<CoherentOvertimeApplication, Employee> empJoin = detailJoin
				.join(CoherentOvertimeApplication_.employee);

		restriction = cb.and(restriction,
				cb.equal(root.get(CoherentOvertimeApplicationDetail_.overtimeApplicationDetailID),id));
		restriction = cb.and(restriction, cb.equal(
				empJoin.get(Employee_.employeeId),employeeId));
		criteriaQuery.where(restriction);

		TypedQuery<CoherentOvertimeApplicationDetail> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		List<CoherentOvertimeApplicationDetail> CoherentOvertimeApplicationList = typedQuery
				.getResultList();
		if (!CoherentOvertimeApplicationList.isEmpty()
				&& CoherentOvertimeApplicationList.size() > 0) {
			return CoherentOvertimeApplicationList.get(0);
		}
		return null;

	}

}
