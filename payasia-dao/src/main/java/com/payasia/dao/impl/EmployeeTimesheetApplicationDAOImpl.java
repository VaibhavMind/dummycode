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

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.hibernate.jdbc.Work;
import org.springframework.stereotype.Repository;

import com.payasia.common.dto.AddClaimConditionDTO;
import com.payasia.common.dto.EmployeeShortListDTO;
import com.payasia.common.dto.LionTimesheetDetailDTO;
import com.payasia.common.dto.LundinDailyPaidTimesheetDTO;
import com.payasia.common.dto.LundinPendingTsheetConditionDTO;
import com.payasia.common.dto.LundinTimesheetStatusReportDTO;
import com.payasia.common.dto.LundinTimewritingDeptReportDTO;
import com.payasia.common.dto.LundinTsheetConditionDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.NullAwareBeanUtilsBean;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.EmployeeTimesheetApplicationDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeTimesheetApplication;
import com.payasia.dao.bean.EmployeeTimesheetApplication_;
import com.payasia.dao.bean.EmployeeTimesheetReviewer;
import com.payasia.dao.bean.EmployeeTimesheetReviewer_;
import com.payasia.dao.bean.Employee_;
import com.payasia.dao.bean.TimesheetApplicationReviewer;
import com.payasia.dao.bean.TimesheetApplicationReviewer_;
import com.payasia.dao.bean.TimesheetBatch;
import com.payasia.dao.bean.TimesheetBatch_;
import com.payasia.dao.bean.TimesheetStatusMaster;
import com.payasia.dao.bean.TimesheetStatusMaster_;

@Repository
public class EmployeeTimesheetApplicationDAOImpl extends BaseDAO implements EmployeeTimesheetApplicationDAO {
	@Override
	protected Object getBaseEntity() {
		EmployeeTimesheetApplication timesheet = new EmployeeTimesheetApplication();
		return timesheet;
	}

	@Override
	public EmployeeTimesheetApplication findById(long id) {
		return super.findById(EmployeeTimesheetApplication.class, id);
	}

	@Override
	public void save(EmployeeTimesheetApplication otTimesheet) {
		super.save(otTimesheet);
	}

	@Override
	public void update(EmployeeTimesheetApplication lundinOTTimesheet) {
		super.update(lundinOTTimesheet);

	}

	@Override
	public EmployeeTimesheetApplication findReviewerById(long id) {
		return super.findById(EmployeeTimesheetApplication.class, id);
	}

	@Override
	public long saveAndReturn(EmployeeTimesheetApplication timesheet) {
		EmployeeTimesheetApplication persistObj = timesheet;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (EmployeeTimesheetApplication) getBaseEntity();
			beanUtil.copyProperties(persistObj, timesheet);
		} catch (IllegalAccessException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		} catch (InvocationTargetException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		}
		this.entityManagerFactory.persist(persistObj);
		this.entityManagerFactory.flush();
		return persistObj.getTimesheetId();

	}

	@Override
	public List<EmployeeTimesheetApplication> findByCondition(AddClaimConditionDTO conditionDTO, PageRequest pageDTO,
			SortCondition sortDTO) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeTimesheetApplication> criteriaQuery = cb.createQuery(EmployeeTimesheetApplication.class);
		Root<EmployeeTimesheetApplication> claimApplicationRoot = criteriaQuery
				.from(EmployeeTimesheetApplication.class);
		criteriaQuery.select(claimApplicationRoot);

		Predicate restriction = cb.conjunction();

		Join<EmployeeTimesheetApplication, Employee> empLeaveJoin = claimApplicationRoot
				.join(EmployeeTimesheetApplication_.employee);

		Join<EmployeeTimesheetApplication, TimesheetStatusMaster> leaveStatusJoin = claimApplicationRoot
				.join(EmployeeTimesheetApplication_.timesheetStatusMaster);

		if (StringUtils.isNotBlank(conditionDTO.getCreatedDate())) {
			restriction = cb.and(restriction,
					cb.equal(claimApplicationRoot.get(EmployeeTimesheetApplication_.createdDate).as(Date.class),
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
					(claimApplicationRoot.get(EmployeeTimesheetApplication_.createdDate)), conditionDTO.getFromDate()));
		}
		if (conditionDTO.getToDate() != null) {
			restriction = cb.and(restriction, cb.lessThanOrEqualTo(
					(claimApplicationRoot.get(EmployeeTimesheetApplication_.createdDate)), conditionDTO.getToDate()));
		}

		criteriaQuery.where(restriction);

		criteriaQuery.orderBy(cb.desc(claimApplicationRoot.get(EmployeeTimesheetApplication_.updatedDate)));

		TypedQuery<EmployeeTimesheetApplication> claimAppTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			claimAppTypedQuery.setFirstResult(getStartPosition(pageDTO));
			claimAppTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		return claimAppTypedQuery.getResultList();
	}

	@Override
	public List<EmployeeTimesheetApplication> findByConditionLion(AddClaimConditionDTO conditionDTO,
			PageRequest pageDTO, SortCondition sortDTO) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeTimesheetApplication> criteriaQuery = cb.createQuery(EmployeeTimesheetApplication.class);
		Root<EmployeeTimesheetApplication> claimApplicationRoot = criteriaQuery
				.from(EmployeeTimesheetApplication.class);
		criteriaQuery.select(claimApplicationRoot);

		Predicate restriction = cb.conjunction();

		Join<EmployeeTimesheetApplication, Employee> empLeaveJoin = claimApplicationRoot
				.join(EmployeeTimesheetApplication_.employee);

		Join<EmployeeTimesheetApplication, TimesheetStatusMaster> leaveStatusJoin = claimApplicationRoot
				.join(EmployeeTimesheetApplication_.timesheetStatusMaster);

		if (StringUtils.isNotBlank(conditionDTO.getCreatedDate())) {
			restriction = cb.and(restriction,
					cb.equal(claimApplicationRoot.get(EmployeeTimesheetApplication_.createdDate).as(Date.class),
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
					(claimApplicationRoot.get(EmployeeTimesheetApplication_.createdDate)), conditionDTO.getFromDate()));
		}
		if (conditionDTO.getToDate() != null) {
			restriction = cb.and(restriction, cb.lessThanOrEqualTo(
					(claimApplicationRoot.get(EmployeeTimesheetApplication_.createdDate)), conditionDTO.getToDate()));
		}

		criteriaQuery.where(restriction);

		/*
		 * criteriaQuery.orderBy(cb.desc(claimApplicationRoot
		 * .get(EmployeeTimesheetApplication_.updatedDate)));
		 */
		criteriaQuery.orderBy(cb.desc(claimApplicationRoot.get(EmployeeTimesheetApplication_.timesheetBatch)
				.get(TimesheetBatch_.createdDate)));

		TypedQuery<EmployeeTimesheetApplication> claimAppTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			claimAppTypedQuery.setFirstResult(getStartPosition(pageDTO));
			claimAppTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		return claimAppTypedQuery.getResultList();
	}

	@Override
	public Long getCountForCondition(AddClaimConditionDTO conditionDTO) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
		Root<EmployeeTimesheetApplication> claimApplicationRoot = criteriaQuery
				.from(EmployeeTimesheetApplication.class);
		criteriaQuery.select(cb.count(claimApplicationRoot));

		Predicate restriction = cb.conjunction();

		Join<EmployeeTimesheetApplication, Employee> empLeaveJoin = claimApplicationRoot
				.join(EmployeeTimesheetApplication_.employee);

		Join<EmployeeTimesheetApplication, TimesheetStatusMaster> leaveStatusJoin = claimApplicationRoot
				.join(EmployeeTimesheetApplication_.timesheetStatusMaster);

		if (StringUtils.isNotBlank(conditionDTO.getCreatedDate())) {
			restriction = cb.and(restriction,
					cb.equal(claimApplicationRoot.get(EmployeeTimesheetApplication_.createdDate).as(Date.class),
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
					(claimApplicationRoot.get(EmployeeTimesheetApplication_.createdDate)), conditionDTO.getFromDate()));
		}
		if (conditionDTO.getToDate() != null) {
			restriction = cb.and(restriction, cb.lessThanOrEqualTo(
					(claimApplicationRoot.get(EmployeeTimesheetApplication_.createdDate)), conditionDTO.getToDate()));
		}

		criteriaQuery.where(restriction);

		TypedQuery<Long> claimAppTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		return claimAppTypedQuery.getSingleResult();

	}

	@Override
	public List<EmployeeTimesheetApplication> findByConditionForEmployee(PageRequest pageDTO, SortCondition sortDTO,
			Long employeeId, String fromDate, String toDate, Boolean visibleToEmployee,
			LundinTsheetConditionDTO conditionDTO) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeTimesheetApplication> criteriaQuery = cb.createQuery(EmployeeTimesheetApplication.class);
		Root<EmployeeTimesheetApplication> otTimesheetRoot = criteriaQuery.from(EmployeeTimesheetApplication.class);
		criteriaQuery.select(otTimesheetRoot);

		Predicate restriction = cb.conjunction();

		Join<EmployeeTimesheetApplication, Employee> empJoin = otTimesheetRoot
				.join(EmployeeTimesheetApplication_.employee);

		restriction = cb.and(restriction, cb.equal(empJoin.get(Employee_.employeeId), employeeId));

		if (StringUtils.isNotBlank(conditionDTO.getBatch())) {
			restriction = cb.and(restriction, cb.like(otTimesheetRoot.get(EmployeeTimesheetApplication_.timesheetBatch)
					.get(TimesheetBatch_.timesheetBatchDesc), conditionDTO.getBatch()));

		}
		if (StringUtils.isNotBlank(conditionDTO.getStatus())) {
			restriction = cb.and(restriction,
					cb.like(otTimesheetRoot.get(EmployeeTimesheetApplication_.timesheetStatusMaster)
							.get(TimesheetStatusMaster_.timesheetStatusDesc), conditionDTO.getStatus()));

		}

		if (StringUtils.isNotBlank(fromDate)) {
			restriction = cb.and(restriction,
					cb.greaterThanOrEqualTo(
							(otTimesheetRoot.get(EmployeeTimesheetApplication_.createdDate).as(Date.class)),
							DateUtils.stringToTimestamp(fromDate)));
		}
		if (StringUtils.isNotBlank(toDate)) {
			restriction = cb.and(restriction,
					cb.lessThanOrEqualTo(
							(otTimesheetRoot.get(EmployeeTimesheetApplication_.createdDate).as(Date.class)),
							DateUtils.stringToTimestamp(toDate)));
		}
		criteriaQuery.where(restriction);

		TypedQuery<EmployeeTimesheetApplication> claimAppTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			claimAppTypedQuery.setFirstResult(getStartPosition(pageDTO));
			claimAppTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		return claimAppTypedQuery.getResultList();
	}

	@Override
	public List<EmployeeTimesheetApplication> findByConditionForEmployeeLion(PageRequest pageDTO, SortCondition sortDTO,
			Long employeeId, String fromDate, String toDate, Boolean visibleToEmployee,
			LundinTsheetConditionDTO conditionDTO) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeTimesheetApplication> criteriaQuery = cb.createQuery(EmployeeTimesheetApplication.class);
		Root<EmployeeTimesheetApplication> otTimesheetRoot = criteriaQuery.from(EmployeeTimesheetApplication.class);
		criteriaQuery.select(otTimesheetRoot);

		Predicate restriction = cb.conjunction();

		Join<EmployeeTimesheetApplication, Employee> empJoin = otTimesheetRoot
				.join(EmployeeTimesheetApplication_.employee);

		restriction = cb.and(restriction, cb.equal(empJoin.get(Employee_.employeeId), employeeId));

		if (StringUtils.isNotBlank(conditionDTO.getBatch())) {
			restriction = cb.and(restriction, cb.like(otTimesheetRoot.get(EmployeeTimesheetApplication_.timesheetBatch)
					.get(TimesheetBatch_.timesheetBatchDesc), conditionDTO.getBatch()));

		}
		if (StringUtils.isNotBlank(conditionDTO.getStatus())) {
			restriction = cb.and(restriction,
					cb.like(otTimesheetRoot.get(EmployeeTimesheetApplication_.timesheetStatusMaster)
							.get(TimesheetStatusMaster_.timesheetStatusDesc), conditionDTO.getStatus()));

		}

		if (StringUtils.isNotBlank(fromDate)) {
			restriction = cb.and(restriction,
					cb.greaterThanOrEqualTo(
							(otTimesheetRoot.get(EmployeeTimesheetApplication_.createdDate).as(Date.class)),
							DateUtils.stringToTimestamp(fromDate)));
		}
		if (StringUtils.isNotBlank(toDate)) {
			restriction = cb.and(restriction,
					cb.lessThanOrEqualTo(
							(otTimesheetRoot.get(EmployeeTimesheetApplication_.createdDate).as(Date.class)),
							DateUtils.stringToTimestamp(toDate)));
		}
		criteriaQuery.where(restriction);

		criteriaQuery.orderBy(cb.desc(
				otTimesheetRoot.get(EmployeeTimesheetApplication_.timesheetBatch).get(TimesheetBatch_.createdDate)));

		TypedQuery<EmployeeTimesheetApplication> claimAppTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			claimAppTypedQuery.setFirstResult(getStartPosition(pageDTO));
			claimAppTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		return claimAppTypedQuery.getResultList();
	}

	@Override
	public Integer getCountByConditionForEmployee(Long employeeId, String fromDate, String toDate,
			LundinTsheetConditionDTO conditionDTO) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Integer> criteriaQuery = cb.createQuery(Integer.class);
		Root<EmployeeTimesheetApplication> otTimesheetRoot = criteriaQuery.from(EmployeeTimesheetApplication.class);
		criteriaQuery.select(cb.count(otTimesheetRoot).as(Integer.class));

		Predicate restriction = cb.conjunction();

		Join<EmployeeTimesheetApplication, Employee> empJoin = otTimesheetRoot
				.join(EmployeeTimesheetApplication_.employee);

		restriction = cb.and(restriction, cb.equal(empJoin.get(Employee_.employeeId), employeeId));

		if (StringUtils.isNotBlank(conditionDTO.getBatch())) {
			restriction = cb.and(restriction, cb.like(otTimesheetRoot.get(EmployeeTimesheetApplication_.timesheetBatch)
					.get(TimesheetBatch_.timesheetBatchDesc), conditionDTO.getBatch()));

		}
		if (StringUtils.isNotBlank(conditionDTO.getStatus())) {
			restriction = cb.and(restriction,
					cb.like(otTimesheetRoot.get(EmployeeTimesheetApplication_.timesheetStatusMaster)
							.get(TimesheetStatusMaster_.timesheetStatusDesc), conditionDTO.getStatus()));

		}

		if (StringUtils.isNotBlank(fromDate)) {
			restriction = cb.and(restriction,
					cb.greaterThanOrEqualTo(
							(otTimesheetRoot.get(EmployeeTimesheetApplication_.createdDate).as(Date.class)),
							DateUtils.stringToTimestamp(fromDate)));
		}
		if (StringUtils.isNotBlank(toDate)) {
			restriction = cb.and(restriction,
					cb.lessThanOrEqualTo(
							(otTimesheetRoot.get(EmployeeTimesheetApplication_.createdDate).as(Date.class)),
							DateUtils.stringToTimestamp(toDate)));
		}

		criteriaQuery.where(restriction);

		TypedQuery<Integer> claimAppTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		return claimAppTypedQuery.getSingleResult();

	}

	@Override
	public List<EmployeeTimesheetApplication> getEmployeeTimesheetReport(Long employeeId, Long companyId, Long batchId,
			boolean includeResignedEmployees, EmployeeShortListDTO employeeShortListDTO) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeTimesheetApplication> criteriaQuery = cb.createQuery(EmployeeTimesheetApplication.class);
		Root<EmployeeTimesheetApplication> root = criteriaQuery.from(EmployeeTimesheetApplication.class);
		criteriaQuery.select(root);

		Predicate restriction = cb.conjunction();
		Join<EmployeeTimesheetApplication, TimesheetStatusMaster> otStatusJoin = root
				.join(EmployeeTimesheetApplication_.timesheetStatusMaster);
		Join<EmployeeTimesheetApplication, TimesheetBatch> batchJoin = root
				.join(EmployeeTimesheetApplication_.timesheetBatch);
		Join<EmployeeTimesheetApplication, Employee> employeeJoin = root.join(EmployeeTimesheetApplication_.employee);
		Join<EmployeeTimesheetApplication, Company> companyJoin = root.join(EmployeeTimesheetApplication_.company);

		restriction = cb.and(restriction, cb.equal(companyJoin.get(Company_.companyId), companyId));
		restriction = cb.and(restriction, cb.equal(batchJoin.get(TimesheetBatch_.timesheetBatchId), batchId));
		if (employeeShortListDTO.getEmployeeShortList() && employeeShortListDTO.getShortListEmployeeIds().isEmpty()) {

			restriction = cb.and(restriction, employeeJoin.get(Employee_.employeeId).in(-1));

		} else if (employeeShortListDTO.getEmployeeShortList()
				&& !employeeShortListDTO.getShortListEmployeeIds().isEmpty()) {
			restriction = cb.and(restriction,
					employeeJoin.get(Employee_.employeeId).in(employeeShortListDTO.getShortListEmployeeIds()));

		}
		restriction = cb.and(restriction, cb.equal(otStatusJoin.get(TimesheetStatusMaster_.timesheetStatusName),
				PayAsiaConstants.OT_STATUS_COMPLETED));
		if (!includeResignedEmployees) {
			restriction = cb.and(restriction, cb.equal(employeeJoin.get(Employee_.status), true));
		}

		criteriaQuery.where(restriction);

		TypedQuery<EmployeeTimesheetApplication> claimAppTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		return claimAppTypedQuery.getResultList();
	}

	@Override
	public List<EmployeeTimesheetApplication> findByConditionSubmitted(LundinPendingTsheetConditionDTO conditionDTO,
			PageRequest pageDTO, SortCondition sortDTO) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeTimesheetApplication> criteriaQuery = cb.createQuery(EmployeeTimesheetApplication.class);
		Root<EmployeeTimesheetApplication> root = criteriaQuery.from(EmployeeTimesheetApplication.class);
		criteriaQuery.select(root);

		Predicate restriction = cb.conjunction();

		Join<EmployeeTimesheetApplication, Employee> empChangeReqJoin = root
				.join(EmployeeTimesheetApplication_.employee);

		Join<EmployeeTimesheetApplication, TimesheetStatusMaster> hrisStatusJoin = root
				.join(EmployeeTimesheetApplication_.timesheetStatusMaster);

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

		TypedQuery<EmployeeTimesheetApplication> typedQuery = entityManagerFactory.createQuery(criteriaQuery);

		// Added by Gaurav for Pagination
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			typedQuery.setFirstResult(getStartPosition(pageDTO));
			typedQuery.setMaxResults(pageDTO.getPageSize());
		}

		return typedQuery.getResultList();
	}

	@Override
	public List<TimesheetApplicationReviewer> findByTimesheetStatus(List<String> timesheetStatusNames, Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<TimesheetApplicationReviewer> criteriaQuery = cb.createQuery(TimesheetApplicationReviewer.class);
		Root<TimesheetApplicationReviewer> empRoot = criteriaQuery.from(TimesheetApplicationReviewer.class);
		criteriaQuery.select(empRoot);

		Predicate restriction = cb.conjunction();

		restriction = cb.equal(empRoot.get(TimesheetApplicationReviewer_.pending), PayAsiaConstants.IS_PENDING);

		criteriaQuery.where(restriction);

		TypedQuery<TimesheetApplicationReviewer> timesheetTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		List<TimesheetApplicationReviewer> lundinTimesheetReviewers = timesheetTypedQuery.getResultList();

		return lundinTimesheetReviewers;
	}

	@Override
	public TimesheetBatch findCurrentBatchForDate(Timestamp date, Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<TimesheetBatch> criteriaQuery = cb.createQuery(TimesheetBatch.class);
		Root<TimesheetBatch> root = criteriaQuery.from(TimesheetBatch.class);
		criteriaQuery.select(root);

		Predicate restriction = cb.conjunction();

		Join<TimesheetBatch, Company> companyJoin = root.join(TimesheetBatch_.company);

		restriction = cb.and(restriction, cb.equal(companyJoin.get(Company_.companyId), companyId));

		restriction = cb.and(restriction, cb.lessThan(root.get(TimesheetBatch_.startDate).as(Date.class), date));
		restriction = cb.and(restriction, cb.greaterThan(root.get(TimesheetBatch_.endDate).as(Date.class), date));
		criteriaQuery.where(restriction);

		criteriaQuery.orderBy(cb.desc(root.get(TimesheetBatch_.endDate)));
		TypedQuery<TimesheetBatch> timesheetTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		TimesheetBatch lundinTimesheetBatch = timesheetTypedQuery.getResultList().get(0);
		return lundinTimesheetBatch;

	}

	@Override
	public Boolean isTimesheetSubmitted(Long batchId, Long employeeId, Long companyId) {

		boolean isSubmit = false;
		try {
			CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
			CriteriaQuery<EmployeeTimesheetApplication> criteriaQuery = cb
					.createQuery(EmployeeTimesheetApplication.class);
			Root<EmployeeTimesheetApplication> root = criteriaQuery.from(EmployeeTimesheetApplication.class);

			Join<EmployeeTimesheetApplication, Company> companyJoin = root.join(EmployeeTimesheetApplication_.company);
			Join<EmployeeTimesheetApplication, TimesheetStatusMaster> timesheetStatusJoin = root
					.join(EmployeeTimesheetApplication_.timesheetStatusMaster);

			criteriaQuery.select(root);
			Predicate restriction = cb.conjunction();

			restriction = cb.and(restriction, cb.equal(companyJoin.get(Company_.companyId), companyId));

			restriction = cb.and(restriction,
					cb.equal(root.get(EmployeeTimesheetApplication_.timesheetBatch), batchId));

			restriction = cb.and(restriction, cb.equal(root.get(EmployeeTimesheetApplication_.employee), employeeId));

			restriction = cb.and(restriction,
					cb.equal(timesheetStatusJoin.get(TimesheetStatusMaster_.timesheetStatusDesc),
							PayAsiaConstants.LUNDIN_TIMESHEET_STATUS_SUBMITTED));

			criteriaQuery.where(restriction);

			TypedQuery<EmployeeTimesheetApplication> timesheetMasterTypedQuery = entityManagerFactory
					.createQuery(criteriaQuery);

			isSubmit = timesheetMasterTypedQuery.getResultList().isEmpty();
			return !isSubmit;

		} catch (Exception e) {
			return isSubmit;
		}
	}

	@Override
	public List<EmployeeTimesheetApplication> findSubmitTimesheetEmp(Long batchId, Long companyId) {

		List<EmployeeTimesheetApplication> emplyeeList = new ArrayList<EmployeeTimesheetApplication>();
		List<String> timesheetStatusNames = new ArrayList<>();
		timesheetStatusNames.add(PayAsiaConstants.LUNDIN_TIMESHEET_STATUS_SUBMITTED);
		timesheetStatusNames.add(PayAsiaConstants.LUNDIN_TIMESHEET_STATUS_APPROVED);
		timesheetStatusNames.add(PayAsiaConstants.LUNDIN_TIMESHEET_STATUS_COMPLETED);
		try {
			CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
			CriteriaQuery<EmployeeTimesheetApplication> criteriaQuery = cb
					.createQuery(EmployeeTimesheetApplication.class);
			Root<EmployeeTimesheetApplication> root = criteriaQuery.from(EmployeeTimesheetApplication.class);

			Join<EmployeeTimesheetApplication, Company> companyJoin = root.join(EmployeeTimesheetApplication_.company);
			Join<EmployeeTimesheetApplication, TimesheetStatusMaster> timesheetStatusJoin = root
					.join(EmployeeTimesheetApplication_.timesheetStatusMaster);

			criteriaQuery.select(root);
			Predicate restriction = cb.conjunction();

			restriction = cb.and(restriction, cb.equal(companyJoin.get(Company_.companyId), companyId));

			restriction = cb.and(restriction,
					cb.equal(root.get(EmployeeTimesheetApplication_.timesheetBatch), batchId));

			restriction = cb.and(restriction,
					timesheetStatusJoin.get(TimesheetStatusMaster_.timesheetStatusDesc).in(timesheetStatusNames));

			criteriaQuery.where(restriction);

			TypedQuery<EmployeeTimesheetApplication> timesheetMasterTypedQuery = entityManagerFactory
					.createQuery(criteriaQuery);

			emplyeeList = timesheetMasterTypedQuery.getResultList();
			return emplyeeList;

		} catch (Exception e) {
			return emplyeeList;
		}
	}

	@Override
	public List<LundinTimewritingDeptReportDTO> LundinTimewritingReportProc(final Long companyId,
			final Long startBatchId, final Long endBatchId, final Long blockId, final Long afeId,
			final String employeeNumber, final boolean isIncludeResignedEmployees, final String employeeIdList) {
		final List<LundinTimewritingDeptReportDTO> reportDTOList = new ArrayList<>();

		getSession().doWork(new Work() {

			@Override
			public void execute(Connection connection) throws SQLException {
				try {
					CallableStatement cstmt = null;
					cstmt = connection.prepareCall("{call Lundin_Timewriting_Report (?,?,?,?,?,?,?,?)}");

					cstmt.setLong("@From_Batch_ID", startBatchId);
					cstmt.setLong("@To_Batch_ID", endBatchId);

					if (blockId != null && blockId != 0) {
						cstmt.setLong("@Block_ID", blockId);
					} else {
						cstmt.setNull("@Block_ID", java.sql.Types.BIGINT);

					}
					if (afeId != null && afeId != 0) {
						cstmt.setLong("@AFE_ID", afeId);
					} else {
						cstmt.setNull("@AFE_ID", java.sql.Types.BIGINT);

					}
					cstmt.setString("@Employee_ID", employeeNumber);

					cstmt.setLong("@Company_ID", companyId);
					cstmt.setBoolean("@Include_Resigned_Employees", isIncludeResignedEmployees);
					cstmt.setString("@Employee_ID_List", employeeIdList);

					ResultSet rs = cstmt.executeQuery();
					if (rs != null) {
						while (rs.next()) {
							LundinTimewritingDeptReportDTO reportDTO = new LundinTimewritingDeptReportDTO();
							reportDTO.setEmployeeNumber(rs.getString("Employee_Number"));
							reportDTO.setFirstName(rs.getString("First_Name"));
							reportDTO.setLastName(rs.getString("Last_Name"));
							reportDTO.setBlockId(rs.getLong("Block_Id"));
							reportDTO.setBlockCode(rs.getString("Block_Code"));
							reportDTO.setBlockName(rs.getString("Block_Name"));
							reportDTO.setAfeId(rs.getLong("AFE_Id"));
							reportDTO.setAfeName(rs.getString("AFE_Name"));
							reportDTO.setValue(rs.getDouble("Value"));
							reportDTO.setBlockEffectiveAllocation(rs.getBoolean("Effective_Allocation"));
							reportDTOList.add(reportDTO);

						}
					}
				} catch (SQLException e) {
					throw new PayAsiaSystemException(e.getMessage(), e);
				}

			}
		});

		return reportDTOList;
	}

	@Override
	public void deleteByCondition(long blockId, long afeId, long timesheetId) {
		String queryString = "DELETE FROM LundinTimesheetDetail ltd WHERE  ltd.lundinBlock.blockId = :blockId AND ltd.lundinAFE.afeId =:afeId AND ltd.employeeTimesheetApplication.timesheetId =:timesheetId";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("blockId", blockId);
		q.setParameter("afeId", afeId);
		q.setParameter("timesheetId", timesheetId);

		q.executeUpdate();
	}

	@Override
	public List<LundinDailyPaidTimesheetDTO> dailyPaidTimesheetReportProc(final Long companyId, final Long batchId,
			final String year, final String Dynamic_Form_Record_Col_Name,
			final String Dynamic_Form_Table_Record_Col_Name, final String Form_ID,
			final boolean isIncludeResignedEmployees, final String employeeIdList) {
		final List<LundinDailyPaidTimesheetDTO> reportDTOList = new ArrayList<>();

		getSession().doWork(new Work() {

			@Override
			public void execute(Connection connection) throws SQLException {
				try {
					CallableStatement cstmt = null;
					cstmt = connection.prepareCall("{call Lundin_Daily_Paid_Report (?,?,?,?,?,?,?,?)}");
					cstmt.setString("@Year", year);
					cstmt.setLong("@Batch_ID", batchId);
					cstmt.setString("@Dynamic_Form_Record_Col_Name", Dynamic_Form_Record_Col_Name);
					cstmt.setString("@Dynamic_Form_Table_Record_Col_Name", Dynamic_Form_Table_Record_Col_Name);
					cstmt.setString("@Form_ID", Form_ID);
					cstmt.setLong("@Company_ID", companyId);
					cstmt.setBoolean("@Include_Resigned_Employees", isIncludeResignedEmployees);
					cstmt.setString("@Employee_ID_List", employeeIdList);

					ResultSet rs = cstmt.executeQuery();
					if (rs != null) {
						while (rs.next()) {
							LundinDailyPaidTimesheetDTO reportDTO = new LundinDailyPaidTimesheetDTO();
							reportDTO.setEmployeeNumber(rs.getString("Employee_Number"));
							reportDTO.setEmployeeId(rs.getLong("Employee_ID"));
							reportDTO.setAnnualCumManDays(rs.getDouble("Annual_Cum_Man_Days"));
							reportDTO.setAnnualCumAmount(rs.getDouble("Annual_Cum_Amount"));
							reportDTO.setTotalManDays(rs.getDouble("Total_Man_Days"));
							reportDTO.setDailyRate(rs.getDouble("Daily_Rate"));
							reportDTO.setTotalAmount(rs.getDouble("Total_Amount"));
							reportDTOList.add(reportDTO);
						}
					}
				} catch (SQLException e) {
					throw new PayAsiaSystemException(e.getMessage(), e);
				}

			}
		});

		return reportDTOList;
	}

	@Override
	public List<LundinTimesheetStatusReportDTO> lundinTimesheetStatusReportProc(final Long companyId,
			final Long fromBatchId, final Long toBatchId, final String timesheetStatus,
			final boolean isIncludeResignedEmployees, final String employeeIdList) {
		final List<LundinTimesheetStatusReportDTO> reportDTOList = new ArrayList<>();

		getSession().doWork(new Work() {

			@Override
			public void execute(Connection connection) throws SQLException {
				try {
					CallableStatement cstmt = null;
					cstmt = connection.prepareCall("{call Lundin_Timesheet_Status_Report (?,?,?,?,?,?)}");
					cstmt.setLong("@Company_ID", companyId);
					cstmt.setString("@Timesheet_Status", timesheetStatus);
					cstmt.setLong("@From_Batch_ID", fromBatchId);
					cstmt.setLong("@To_Batch_ID", toBatchId);
					cstmt.setBoolean("@Include_Resigned_Employees", isIncludeResignedEmployees);
					cstmt.setString("@Employee_ID_List", employeeIdList);

					ResultSet rs = cstmt.executeQuery();
					if (rs != null) {
						while (rs.next()) {
							LundinTimesheetStatusReportDTO reportDTO = new LundinTimesheetStatusReportDTO();
							reportDTO.setTimesheetId(rs.getLong("Timesheet_ID"));
							reportDTO.setEmployeeNumber(rs.getString("Employee_Number"));
							reportDTO.setEmployeeId(rs.getLong("Employee_ID"));
							reportDTO.setEmployeeName(rs.getString("Employee_Name"));
							reportDTO.setTotalManDays(rs.getDouble("Total_Man_Days"));
							reportDTO.setEmployeeReviewer1Id(rs.getLong("Reviewer1_ID"));
							reportDTO.setEmployeeReviewer1Name(rs.getString("Reviewer1_Name"));
							reportDTO.setEmployeeReviewer1Status(rs.getString("Reviewer1_Status"));

							reportDTO.setEmployeeReviewer2Id(rs.getLong("Reviewer2_ID"));
							reportDTO.setEmployeeReviewer2Name(rs.getString("Reviewer2_Name"));
							reportDTO.setEmployeeReviewer2Status(rs.getString("Reviewer2_Status"));

							reportDTO.setEmployeeReviewer3Id(rs.getLong("Reviewer3_ID"));
							reportDTO.setEmployeeReviewer3Name(rs.getString("Reviewer3_Name"));
							reportDTO.setEmployeeReviewer3Status(rs.getString("Reviewer3_Status"));
							reportDTO.setCutoff(rs.getString("CutOff"));
							reportDTO.setTimesheetStatusName(rs.getString("Timesheet_Status_Name"));
							reportDTOList.add(reportDTO);
						}
					}
				} catch (SQLException e) {
					throw new PayAsiaSystemException(e.getMessage(), e);
				}
			}
		});
		return reportDTOList;
	}

	@Override
	public List<EmployeeTimesheetApplication> findByTimsheetCondition(Long companyId,
			LundinPendingTsheetConditionDTO conditionDTO, PageRequest pageDTO, SortCondition sortDTO) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeTimesheetApplication> criteriaQuery = cb.createQuery(EmployeeTimesheetApplication.class);
		Root<EmployeeTimesheetApplication> root = criteriaQuery.from(EmployeeTimesheetApplication.class);
		criteriaQuery.select(root);

		Predicate restriction = cb.conjunction();

		Join<EmployeeTimesheetApplication, Company> companyJoin = root.join(EmployeeTimesheetApplication_.company);

		Join<EmployeeTimesheetApplication, Employee> employeeJoin = root.join(EmployeeTimesheetApplication_.employee);

		if (conditionDTO.getEmployeeId() != null) {
			restriction = cb.and(restriction,
					cb.equal(employeeJoin.get(Employee_.employeeId), conditionDTO.getEmployeeId()));
		}
		if (conditionDTO.getStatusNameList().size() > 0) {
			Join<EmployeeTimesheetApplication, TimesheetStatusMaster> hrisStatusJoin = root
					.join(EmployeeTimesheetApplication_.timesheetStatusMaster);
			restriction = cb.and(restriction, hrisStatusJoin.get(TimesheetStatusMaster_.timesheetStatusName)
					.in(conditionDTO.getStatusNameList()));
		}
		if (StringUtils.isNotBlank(conditionDTO.getBatch())) {
			Join<EmployeeTimesheetApplication, TimesheetBatch> batchJoin = root
					.join(EmployeeTimesheetApplication_.timesheetBatch);
			restriction = cb.and(restriction,
					cb.equal(batchJoin.get(TimesheetBatch_.timesheetBatchDesc), conditionDTO.getBatch()));
		}
		if (StringUtils.isNotBlank(conditionDTO.getEmployeeName())) {
			restriction = cb.and(restriction,
					cb.or(cb.like(cb.upper(employeeJoin.get(Employee_.firstName)),
							'%' + conditionDTO.getEmployeeName().toUpperCase() + '%'),
							cb.like(cb.upper(employeeJoin.get(Employee_.lastName)),
									'%' + conditionDTO.getEmployeeName().toUpperCase() + '%')));
		}
		if (StringUtils.isNotBlank(conditionDTO.getCreatedDate())) {
			restriction = cb.and(restriction,
					cb.equal((root.get(EmployeeTimesheetApplication_.createdDate).as(Date.class)),
							conditionDTO.getCreatedDate()));
		}
		restriction = cb.and(restriction, cb.equal(companyJoin.get(Company_.companyId), companyId));

		criteriaQuery.where(restriction);

		// criteriaQuery.orderBy(cb.desc(root
		// .get(EmployeeTimesheetApplication_.updatedDate)));

		TypedQuery<EmployeeTimesheetApplication> typedQuery = entityManagerFactory.createQuery(criteriaQuery);

		return typedQuery.getResultList();
	}

	@Override
	public Long getCountForTimesheetCondition(Long companyId, LundinPendingTsheetConditionDTO conditionDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
		Root<EmployeeTimesheetApplication> root = criteriaQuery.from(EmployeeTimesheetApplication.class);
		criteriaQuery.select(cb.count(root));

		Predicate restriction = cb.conjunction();

		Join<EmployeeTimesheetApplication, Company> companyJoin = root.join(EmployeeTimesheetApplication_.company);

		Join<EmployeeTimesheetApplication, Employee> employeeJoin = root.join(EmployeeTimesheetApplication_.employee);

		if (conditionDTO.getEmployeeId() != null) {
			restriction = cb.and(restriction,
					cb.equal(employeeJoin.get(Employee_.employeeId), conditionDTO.getEmployeeId()));
		}
		if (conditionDTO.getStatusNameList().size() > 0) {
			Join<EmployeeTimesheetApplication, TimesheetStatusMaster> hrisStatusJoin = root
					.join(EmployeeTimesheetApplication_.timesheetStatusMaster);
			restriction = cb.and(restriction, hrisStatusJoin.get(TimesheetStatusMaster_.timesheetStatusName)
					.in(conditionDTO.getStatusNameList()));
		}
		if (StringUtils.isNotBlank(conditionDTO.getBatch())) {
			Join<EmployeeTimesheetApplication, TimesheetBatch> batchJoin = root
					.join(EmployeeTimesheetApplication_.timesheetBatch);
			restriction = cb.and(restriction,
					cb.equal(batchJoin.get(TimesheetBatch_.timesheetBatchDesc), conditionDTO.getBatch()));
		}
		if (StringUtils.isNotBlank(conditionDTO.getEmployeeName())) {
			restriction = cb.and(restriction,
					cb.or(cb.like(cb.upper(employeeJoin.get(Employee_.firstName)),
							'%' + conditionDTO.getEmployeeName().toUpperCase() + '%'),
							cb.like(cb.upper(employeeJoin.get(Employee_.lastName)),
									'%' + conditionDTO.getEmployeeName().toUpperCase() + '%')));
		}
		if (StringUtils.isNotBlank(conditionDTO.getCreatedDate())) {
			restriction = cb.and(restriction,
					cb.equal((root.get(EmployeeTimesheetApplication_.createdDate).as(Date.class)),
							conditionDTO.getCreatedDate()));
		}
		restriction = cb.and(restriction, cb.equal(companyJoin.get(Company_.companyId), companyId));

		criteriaQuery.where(restriction);

		// criteriaQuery.orderBy(cb.desc(root
		// .get(EmployeeTimesheetApplication_.updatedDate)));

		TypedQuery<Long> claimAppTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		return claimAppTypedQuery.getSingleResult();
	}

	@Override
	public List<EmployeeTimesheetApplication> findByTimesheetBatchId(long timesheetBatchId, long companyId,
			long employeeId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeTimesheetApplication> criteriaQuery = cb.createQuery(EmployeeTimesheetApplication.class);
		Root<EmployeeTimesheetApplication> root = criteriaQuery.from(EmployeeTimesheetApplication.class);
		criteriaQuery.select(root);

		Predicate restriction = cb.conjunction();

		Join<EmployeeTimesheetApplication, Company> companyJoin = root.join(EmployeeTimesheetApplication_.company);

		Join<EmployeeTimesheetApplication, Employee> employeeJoin = root.join(EmployeeTimesheetApplication_.employee);

		Join<EmployeeTimesheetApplication, TimesheetBatch> timesheetBatchJoin = root
				.join(EmployeeTimesheetApplication_.timesheetBatch);

		restriction = cb.and(restriction, cb.equal(companyJoin.get(Company_.companyId), companyId));

		restriction = cb.and(restriction, cb.equal(employeeJoin.get(Employee_.employeeId), employeeId));

		restriction = cb.and(restriction,
				cb.equal(timesheetBatchJoin.get(TimesheetBatch_.timesheetBatchId), timesheetBatchId));

		criteriaQuery.where(restriction);

		TypedQuery<EmployeeTimesheetApplication> claimAppTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		return (List<EmployeeTimesheetApplication>) claimAppTypedQuery.getResultList();

	}

	@Override
	public List<EmployeeTimesheetApplication> findByCompanyAndEmployee(long companyId, long employeeId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeTimesheetApplication> criteriaQuery = cb.createQuery(EmployeeTimesheetApplication.class);
		Root<EmployeeTimesheetApplication> root = criteriaQuery.from(EmployeeTimesheetApplication.class);
		criteriaQuery.select(root);

		Predicate restriction = cb.conjunction();

		Join<EmployeeTimesheetApplication, Company> companyJoin = root.join(EmployeeTimesheetApplication_.company);

		Join<EmployeeTimesheetApplication, Employee> employeeJoin = root.join(EmployeeTimesheetApplication_.employee);

		restriction = cb.and(restriction, cb.equal(companyJoin.get(Company_.companyId), companyId));

		restriction = cb.and(restriction, cb.equal(employeeJoin.get(Employee_.employeeId), employeeId));

		criteriaQuery.where(restriction);

		TypedQuery<EmployeeTimesheetApplication> claimAppTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		return (List<EmployeeTimesheetApplication>) claimAppTypedQuery.getResultList();

	}

	@Override
	public List<EmployeeTimesheetApplication> findLionTimsheetByCondition(Long companyId,
			LundinTsheetConditionDTO conditionDTO) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeTimesheetApplication> criteriaQuery = cb.createQuery(EmployeeTimesheetApplication.class);
		Root<EmployeeTimesheetApplication> root = criteriaQuery.from(EmployeeTimesheetApplication.class);
		criteriaQuery.select(root);

		Predicate restriction = cb.conjunction();

		Join<EmployeeTimesheetApplication, Company> companyJoin = root.join(EmployeeTimesheetApplication_.company);

		Join<EmployeeTimesheetApplication, Employee> employeeJoin = root.join(EmployeeTimesheetApplication_.employee);

		if (conditionDTO.getEmployeeId() != null) {
			restriction = cb.and(restriction,
					cb.equal(employeeJoin.get(Employee_.employeeId), conditionDTO.getEmployeeId()));
		}
		if (conditionDTO.getEmployeeReviewerId() != null) {
			Join<Employee, EmployeeTimesheetReviewer> employeeTSReviewerJoin = employeeJoin
					.join(Employee_.employeeTimesheetReviewer1);
			restriction = cb.and(restriction, cb.equal(
					employeeTSReviewerJoin.get(EmployeeTimesheetReviewer_.employeeReviewer).get(Employee_.employeeId),
					conditionDTO.getEmployeeReviewerId()));
		}
		if (conditionDTO.getStatusNameList().size() > 0) {
			Join<EmployeeTimesheetApplication, TimesheetStatusMaster> hrisStatusJoin = root
					.join(EmployeeTimesheetApplication_.timesheetStatusMaster);
			restriction = cb.and(restriction, hrisStatusJoin.get(TimesheetStatusMaster_.timesheetStatusName)
					.in(conditionDTO.getStatusNameList()));
		}
		if (conditionDTO.getBatchId() != null) {
			Join<EmployeeTimesheetApplication, TimesheetBatch> batchJoin = root
					.join(EmployeeTimesheetApplication_.timesheetBatch);
			restriction = cb.and(restriction,
					cb.equal(batchJoin.get(TimesheetBatch_.timesheetBatchId), conditionDTO.getBatchId()));
		}
		restriction = cb.and(restriction, cb.equal(companyJoin.get(Company_.companyId), companyId));

		if (conditionDTO.getEmployeeShortListDTO().getEmployeeShortList()
				&& conditionDTO.getEmployeeShortListDTO().getShortListEmployeeIds().size() == 0) {

			restriction = cb.and(restriction, employeeJoin.get(Employee_.employeeId).in(-1));

		} else if (conditionDTO.getEmployeeShortListDTO().getEmployeeShortList()
				&& conditionDTO.getEmployeeShortListDTO().getShortListEmployeeIds().size() > 0) {
			restriction = cb.and(restriction, employeeJoin.get(Employee_.employeeId)
					.in(conditionDTO.getEmployeeShortListDTO().getShortListEmployeeIds()));

		}

		if (conditionDTO.isEmpStatus() == false) {
			restriction = cb.and(restriction, cb.equal(employeeJoin.get(Employee_.status), true));
		}
		criteriaQuery.where(restriction);

		// criteriaQuery.orderBy(cb.desc(root
		// .get(EmployeeTimesheetApplication_.updatedDate)));

		TypedQuery<EmployeeTimesheetApplication> typedQuery = entityManagerFactory.createQuery(criteriaQuery);

		return typedQuery.getResultList();
	}

	@Override
	public List<LionTimesheetDetailDTO> getlionTimesheetNotFilledDetailProc(final Long companyId,
			final Timestamp fromDate, final Timestamp toDate) {
		final List<LionTimesheetDetailDTO> reportDTOList = new ArrayList<>();

		getSession().doWork(new Work() {

			@Override
			public void execute(Connection connection) throws SQLException {
				try {
					CallableStatement cstmt = null;
					cstmt = connection.prepareCall("{call Lion_Timesheet_Not_Filled_Detail (?,?,?)}");
					cstmt.setLong("@Company_ID", companyId);
					if (fromDate != null) {
						cstmt.setTimestamp("@Start_Date", fromDate);
					} else {
						cstmt.setNull("@Start_Date", java.sql.Types.TIMESTAMP);

					}
					if (toDate != null) {
						cstmt.setTimestamp("@End_Date", toDate);
					} else {
						cstmt.setNull("@End_Date", java.sql.Types.TIMESTAMP);

					}

					ResultSet rs = cstmt.executeQuery();
					if (rs != null) {
						while (rs.next()) {
							LionTimesheetDetailDTO reportDTO = new LionTimesheetDetailDTO();
							reportDTO.setTimesheetId(rs.getLong("Timesheet_ID"));
							reportDTO.setEmployeeId(rs.getLong("Employee_ID"));
							reportDTO.setTimesheetDate(rs.getString("Timesheet_Date"));
							reportDTO.setTimesheetStatus(rs.getString("Timesheet_Status"));
							reportDTOList.add(reportDTO);
						}
					}
				} catch (SQLException e) {
					throw new PayAsiaSystemException(e.getMessage(), e);
				}
			}
		});
		return reportDTOList;
	}

	@Override
	public List<EmployeeTimesheetApplication> findTimesheetDetailsByDate(Timestamp fromBatchdate, Timestamp toBatchDate,
			List<Long> employeeIdList, Long companyId, List<String> timesheetStatus) {

		List<EmployeeTimesheetApplication> lundinTimesheetList = new ArrayList<EmployeeTimesheetApplication>();

		try {
			CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
			CriteriaQuery<EmployeeTimesheetApplication> criteriaQuery = cb
					.createQuery(EmployeeTimesheetApplication.class);
			Root<EmployeeTimesheetApplication> root = criteriaQuery.from(EmployeeTimesheetApplication.class);

			Join<EmployeeTimesheetApplication, Company> companyJoin = root.join(EmployeeTimesheetApplication_.company);
			Join<EmployeeTimesheetApplication, TimesheetBatch> timesheetBatchJoin = root
					.join(EmployeeTimesheetApplication_.timesheetBatch);
			Join<EmployeeTimesheetApplication, Employee> employeeJoin = root
					.join(EmployeeTimesheetApplication_.employee);
			Join<EmployeeTimesheetApplication, TimesheetStatusMaster> leaveStatusJoin = root
					.join(EmployeeTimesheetApplication_.timesheetStatusMaster);

			criteriaQuery.select(root);
			Predicate restriction = cb.conjunction();

			restriction = cb.and(restriction, cb.equal(companyJoin.get(Company_.companyId), companyId));
			restriction = cb.and(restriction, employeeJoin.get(Employee_.employeeId).in(employeeIdList));

			restriction = cb.and(restriction,
					cb.or(cb.between(timesheetBatchJoin.get(TimesheetBatch_.startDate).as(Date.class), fromBatchdate,
							toBatchDate),
							cb.between(timesheetBatchJoin.get(TimesheetBatch_.endDate).as(Date.class), fromBatchdate,
									toBatchDate)));
			restriction = cb.and(restriction,
					leaveStatusJoin.get(TimesheetStatusMaster_.timesheetStatusName).in(timesheetStatus));
			criteriaQuery.where(restriction);

			/*
			 * criteriaQuery.orderBy(cb.asc(employeeJoin
			 * .get(Employee_.employeeNumber)));
			 */
			TypedQuery<EmployeeTimesheetApplication> timesheetMasterTypedQuery = entityManagerFactory
					.createQuery(criteriaQuery);

			lundinTimesheetList = timesheetMasterTypedQuery.getResultList();
			return lundinTimesheetList;

		} catch (Exception e) {
			return lundinTimesheetList;
		}
	}

	/**
	 * @author sheetalagarwal
	 * @param :
	 *            This method is used for findByConditionForEmployee
	 */
	@Override
	public List<EmployeeTimesheetApplication> findByConditionForEmployee(PageRequest pageDTO, SortCondition sortDTO,
			Long employeeId, String fromDate, String toDate, Boolean visibleToEmployee,
			LundinTsheetConditionDTO conditionDTO, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeTimesheetApplication> criteriaQuery = cb.createQuery(EmployeeTimesheetApplication.class);
		Root<EmployeeTimesheetApplication> otTimesheetRoot = criteriaQuery.from(EmployeeTimesheetApplication.class);
		criteriaQuery.select(otTimesheetRoot);

		Predicate restriction = cb.conjunction();

		Join<EmployeeTimesheetApplication, Employee> empJoin = otTimesheetRoot
				.join(EmployeeTimesheetApplication_.employee);

		restriction = cb.and(restriction, cb.equal(empJoin.get(Employee_.employeeId), employeeId));

		restriction = cb.and(restriction,
				cb.equal(otTimesheetRoot.get(EmployeeTimesheetApplication_.company), companyId));

		if (StringUtils.isNotBlank(conditionDTO.getBatch())) {
			restriction = cb.and(restriction, cb.like(otTimesheetRoot.get(EmployeeTimesheetApplication_.timesheetBatch)
					.get(TimesheetBatch_.timesheetBatchDesc), conditionDTO.getBatch()));

		}
		if (StringUtils.isNotBlank(conditionDTO.getStatus())) {
			restriction = cb.and(restriction,
					cb.like(otTimesheetRoot.get(EmployeeTimesheetApplication_.timesheetStatusMaster)
							.get(TimesheetStatusMaster_.timesheetStatusDesc), conditionDTO.getStatus()));

		}

		if (StringUtils.isNotBlank(fromDate)) {
			restriction = cb.and(restriction,
					cb.greaterThanOrEqualTo(
							(otTimesheetRoot.get(EmployeeTimesheetApplication_.createdDate).as(Date.class)),
							DateUtils.stringToTimestamp(fromDate)));
		}
		if (StringUtils.isNotBlank(toDate)) {
			restriction = cb.and(restriction,
					cb.lessThanOrEqualTo(
							(otTimesheetRoot.get(EmployeeTimesheetApplication_.createdDate).as(Date.class)),
							DateUtils.stringToTimestamp(toDate)));
		}
		criteriaQuery.where(restriction);

		TypedQuery<EmployeeTimesheetApplication> claimAppTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			claimAppTypedQuery.setFirstResult(getStartPosition(pageDTO));
			claimAppTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		return claimAppTypedQuery.getResultList();
	}

	/**
	 * @param :
	 *            remove employee check to get Lundin Timesheet Data in reviewer
	 */
	@Override
	public EmployeeTimesheetApplication findById(long otTimesheetId, Long employeeId, long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeTimesheetApplication> criteriaQuery = cb.createQuery(EmployeeTimesheetApplication.class);
		Root<EmployeeTimesheetApplication> root = criteriaQuery.from(EmployeeTimesheetApplication.class);
		criteriaQuery.select(root);

		Predicate restriction = cb.conjunction();
		Join<EmployeeTimesheetApplication, Employee> employeeJoin = root.join(EmployeeTimesheetApplication_.employee);
		Join<EmployeeTimesheetApplication, Company> companyJoin = root.join(EmployeeTimesheetApplication_.company);

		/*
		 * restriction = cb.and(restriction,
		 * cb.equal(employeeJoin.get(Employee_.employeeId), employeeId));
		 */

		restriction = cb.and(restriction, cb.equal(root.get(EmployeeTimesheetApplication_.timesheetId), otTimesheetId));

		criteriaQuery.where(restriction);

		TypedQuery<EmployeeTimesheetApplication> claimAppTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		if (claimAppTypedQuery.getResultList() != null && !claimAppTypedQuery.getResultList().isEmpty()) {
			return claimAppTypedQuery.getResultList().get(0);
		}
		return null;
	}

	@Override
	public EmployeeTimesheetApplication findById(long otTimesheetId, Long employeeId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeTimesheetApplication> criteriaQuery = cb.createQuery(EmployeeTimesheetApplication.class);
		Root<EmployeeTimesheetApplication> root = criteriaQuery.from(EmployeeTimesheetApplication.class);
		criteriaQuery.select(root);

		Predicate restriction = cb.conjunction();
		Join<EmployeeTimesheetApplication, Employee> employeeJoin = root.join(EmployeeTimesheetApplication_.employee);

		restriction = cb.and(restriction, cb.equal(employeeJoin.get(Employee_.employeeId), employeeId));

		restriction = cb.and(restriction, cb.equal(root.get(EmployeeTimesheetApplication_.timesheetId), otTimesheetId));

		criteriaQuery.where(restriction);

		TypedQuery<EmployeeTimesheetApplication> claimAppTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		return claimAppTypedQuery.getResultList().get(0);
	}

	@Override
	public EmployeeTimesheetApplication findTimesheetByCompanyId(long otTimesheetId, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeTimesheetApplication> criteriaQuery = cb.createQuery(EmployeeTimesheetApplication.class);
		Root<EmployeeTimesheetApplication> root = criteriaQuery.from(EmployeeTimesheetApplication.class);
		criteriaQuery.select(root);

		Predicate restriction = cb.conjunction();
		Join<EmployeeTimesheetApplication, Company> companyJoin = root.join(EmployeeTimesheetApplication_.company);

		restriction = cb.and(restriction, cb.equal(companyJoin.get(Company_.companyId), companyId));

		restriction = cb.and(restriction, cb.equal(root.get(EmployeeTimesheetApplication_.timesheetId), otTimesheetId));

		criteriaQuery.where(restriction);

		TypedQuery<EmployeeTimesheetApplication> claimAppTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		return claimAppTypedQuery.getResultList().get(0);
	}
	
	@Override
	public List<EmployeeTimesheetApplication> findByConditionSubmitted2(LundinPendingTsheetConditionDTO conditionDTO,
			PageRequest pageDTO, SortCondition sortDTO, int position) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeTimesheetApplication> criteriaQuery = cb.createQuery(EmployeeTimesheetApplication.class);
		Root<EmployeeTimesheetApplication> root = criteriaQuery.from(EmployeeTimesheetApplication.class);
		criteriaQuery.select(root);

		Predicate restriction = cb.conjunction();

		Join<EmployeeTimesheetApplication, Employee> empChangeReqJoin = root
				.join(EmployeeTimesheetApplication_.employee);

		Join<EmployeeTimesheetApplication, TimesheetStatusMaster> hrisStatusJoin = root
				.join(EmployeeTimesheetApplication_.timesheetStatusMaster);

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

		TypedQuery<EmployeeTimesheetApplication> typedQuery = entityManagerFactory.createQuery(criteriaQuery);

		// Added by Gaurav for Pagination
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			typedQuery.setFirstResult(position);
			typedQuery.setMaxResults(pageDTO.getPageSize());
		}

		return typedQuery.getResultList();
	}

}
