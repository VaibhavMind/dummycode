package com.payasia.dao.impl;

import java.sql.Date;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.payasia.common.dto.LundinTsheetConditionDTO;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.LionEmployeeTimesheetApplicationDetailDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeTimesheetApplication;
import com.payasia.dao.bean.EmployeeTimesheetApplication_;
import com.payasia.dao.bean.EmployeeTimesheetReviewer;
import com.payasia.dao.bean.EmployeeTimesheetReviewer_;
import com.payasia.dao.bean.Employee_;
import com.payasia.dao.bean.LionEmployeeTimesheetApplicationDetail;
import com.payasia.dao.bean.LionEmployeeTimesheetApplicationDetail_;
import com.payasia.dao.bean.TimesheetBatch;
import com.payasia.dao.bean.TimesheetBatch_;
import com.payasia.dao.bean.TimesheetStatusMaster;
import com.payasia.dao.bean.TimesheetStatusMaster_;

@Repository
public class LionEmployeeTimesheetApplicationDetailDAOImpl extends BaseDAO
		implements LionEmployeeTimesheetApplicationDetailDAO {

	@Override
	protected Object getBaseEntity() {
		LionEmployeeTimesheetApplicationDetail lionEmployeeTimesheetApplicationDetail = new LionEmployeeTimesheetApplicationDetail();
		return lionEmployeeTimesheetApplicationDetail;
	}

	@Override
	public void save(
			LionEmployeeTimesheetApplicationDetail lionEmployeeTimesheetApplicationDetail) {
		super.save(lionEmployeeTimesheetApplicationDetail);

	}

	@Override
	public void update(
			LionEmployeeTimesheetApplicationDetail lionEmployeeTimesheetApplicationDetail) {
		super.update(lionEmployeeTimesheetApplicationDetail);

	}

	@Override
	public LionEmployeeTimesheetApplicationDetail saveReturn(
			LionEmployeeTimesheetApplicationDetail lionEmployeeTimesheetApplicationDetail) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LionEmployeeTimesheetApplicationDetail findById(
			Long lionEmployeeTimesheetId) {
		return super.findById(LionEmployeeTimesheetApplicationDetail.class,
				lionEmployeeTimesheetId);
	}

	@Override
	public void delete(
			LionEmployeeTimesheetApplicationDetail lionEmployeeTimesheetApplicationDetail) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<LionEmployeeTimesheetApplicationDetail> getLionEmployeeTimesheetApplicationDetails(
			long timesheeetId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LionEmployeeTimesheetApplicationDetail> criteriaQuery = cb
				.createQuery(LionEmployeeTimesheetApplicationDetail.class);

		Root<LionEmployeeTimesheetApplicationDetail> root = criteriaQuery
				.from(LionEmployeeTimesheetApplicationDetail.class);
		Predicate restriction = cb.conjunction();
		restriction = cb
				.and(restriction,
						cb.equal(
								root.get(LionEmployeeTimesheetApplicationDetail_.employeeTimesheetApplication),
								timesheeetId));
		criteriaQuery.select(root).where(restriction);
		TypedQuery<LionEmployeeTimesheetApplicationDetail> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		try {

			return typedQuery.getResultList();
		} catch (Exception ex) {
			return null;
		}

	}

	@Override
	public List<LionEmployeeTimesheetApplicationDetail> findLionTimsheetDetailsByCondition(
			Long companyId, LundinTsheetConditionDTO conditionDTO) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LionEmployeeTimesheetApplicationDetail> criteriaQuery = cb
				.createQuery(LionEmployeeTimesheetApplicationDetail.class);
		Root<LionEmployeeTimesheetApplicationDetail> root = criteriaQuery
				.from(LionEmployeeTimesheetApplicationDetail.class);
		criteriaQuery.select(root);

		Predicate restriction = cb.conjunction();

		Join<LionEmployeeTimesheetApplicationDetail, EmployeeTimesheetApplication> empTimesheetJoin = root
				.join(LionEmployeeTimesheetApplicationDetail_.employeeTimesheetApplication);
		Join<EmployeeTimesheetApplication, Company> companyJoin = empTimesheetJoin
				.join(EmployeeTimesheetApplication_.company);

		Join<EmployeeTimesheetApplication, Employee> employeeJoin = empTimesheetJoin
				.join(EmployeeTimesheetApplication_.employee);

		if (conditionDTO.getApprovedFromDate() != null) {
			restriction = cb
					.and(restriction,
							cb.greaterThanOrEqualTo(
									root.get(
											LionEmployeeTimesheetApplicationDetail_.createdDate)
											.as(Date.class), conditionDTO
											.getApprovedFromDate()));
		}
		if (conditionDTO.getApprovedToDate() != null) {
			restriction = cb
					.and(restriction,
							cb.lessThanOrEqualTo(
									root.get(
											LionEmployeeTimesheetApplicationDetail_.createdDate)
											.as(Date.class), conditionDTO
											.getApprovedToDate()));
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
			Join<LionEmployeeTimesheetApplicationDetail, TimesheetStatusMaster> hrisStatusJoin = root
					.join(LionEmployeeTimesheetApplicationDetail_.timesheetStatusMaster);
			restriction = cb.and(
					restriction,
					hrisStatusJoin.get(
							TimesheetStatusMaster_.timesheetStatusName).in(
							conditionDTO.getStatusNameList()));
		}
		if (conditionDTO.getBatchId() != null) {
			Join<EmployeeTimesheetApplication, TimesheetBatch> batchJoin = empTimesheetJoin
					.join(EmployeeTimesheetApplication_.timesheetBatch);
			restriction = cb.and(restriction, cb.equal(
					batchJoin.get(TimesheetBatch_.timesheetBatchId),
					conditionDTO.getBatchId()));
		}
		restriction = cb.and(restriction,
				cb.equal(companyJoin.get(Company_.companyId), companyId));

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

		TypedQuery<LionEmployeeTimesheetApplicationDetail> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return typedQuery.getResultList();
	}

}
