package com.payasia.dao.impl;

import java.lang.reflect.InvocationTargetException;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.springframework.stereotype.Repository;

import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.NullAwareBeanUtilsBean;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.TimesheetBatchDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeTimesheetApplication;
import com.payasia.dao.bean.EmployeeTimesheetApplication_;
import com.payasia.dao.bean.Employee_;
import com.payasia.dao.bean.TimesheetBatch;
import com.payasia.dao.bean.TimesheetBatch_;
import com.payasia.dao.bean.TimesheetStatusMaster;
import com.payasia.dao.bean.TimesheetStatusMaster_;

@Repository
public class TimesheetBatchDAOImpl extends BaseDAO implements TimesheetBatchDAO {

	@Override
	public TimesheetBatch findById(long id) {
		return super.findById(TimesheetBatch.class, id);
	}

	@Override
	public void save(TimesheetBatch lundinTimesheetBatch) {
		super.save(lundinTimesheetBatch);

	}

	@Override
	protected Object getBaseEntity() {
		TimesheetBatch lundinTimesheetBatch = new TimesheetBatch();
		return lundinTimesheetBatch;
	}

	@Override
	public long saveOTBatchAndReturnId(TimesheetBatch lundinTimesheetBatch) {

		TimesheetBatch persistObj = lundinTimesheetBatch;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (TimesheetBatch) getBaseEntity();
			beanUtil.copyProperties(persistObj, lundinTimesheetBatch);
		} catch (IllegalAccessException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		} catch (InvocationTargetException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		}
		this.entityManagerFactory.persist(persistObj);
		this.entityManagerFactory.flush();
		long pid = persistObj.getTimesheetBatchId();
		return pid;

	}

	@Override
	public List<TimesheetBatch> getOTBacthesByCompanyId(Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<TimesheetBatch> criteriaQuery = cb
				.createQuery(TimesheetBatch.class);
		Root<TimesheetBatch> root = criteriaQuery.from(TimesheetBatch.class);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction,
				cb.equal(root.get(TimesheetBatch_.company), companyId));

		criteriaQuery.select(root).where(restriction);
		TypedQuery<TimesheetBatch> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		return typedQuery.getResultList();
	}

	@Override
	public List<TimesheetBatch> getOTBacthesByStatus(Long companyId,
			Long employeeId, List<String> otStatusList) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<TimesheetBatch> criteriaQuery = cb
				.createQuery(TimesheetBatch.class);
		Root<TimesheetBatch> otBatchRoot = criteriaQuery
				.from(TimesheetBatch.class);

		criteriaQuery.select(otBatchRoot);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(otBatchRoot.get(TimesheetBatch_.company), companyId));

		Subquery<Long> subquery = criteriaQuery.subquery(Long.class);
		Root<TimesheetBatch> subRoot = subquery.from(TimesheetBatch.class);
		subquery.select(
				subRoot.get(TimesheetBatch_.timesheetBatchId).as(Long.class))
				.distinct(true);

		Join<TimesheetBatch, EmployeeTimesheetApplication> otTimeSheetJoin = subRoot
				.join(TimesheetBatch_.employeeTimesheetApplications);
		Join<EmployeeTimesheetApplication, Employee> employeeJoin = otTimeSheetJoin
				.join(EmployeeTimesheetApplication_.employee);
		Join<EmployeeTimesheetApplication, TimesheetStatusMaster> statusMasterJoin = otTimeSheetJoin
				.join(EmployeeTimesheetApplication_.timesheetStatusMaster);

		Predicate subRestriction = cb.conjunction();

		subRestriction = cb.and(subRestriction,
				cb.equal(employeeJoin.get(Employee_.employeeId), employeeId));

		subRestriction = cb.and(
				subRestriction,
				cb.not(statusMasterJoin.get(
						TimesheetStatusMaster_.timesheetStatusName).in(
						otStatusList)));

		subquery.where(subRestriction);

		restriction = cb.and(restriction, cb.not(cb.in(
				otBatchRoot.get(TimesheetBatch_.timesheetBatchId)).value(
				subquery)));

		criteriaQuery.where(restriction);

		TypedQuery<TimesheetBatch> companyTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		List<TimesheetBatch> timesheetBatches = companyTypedQuery
				.getResultList();

		return timesheetBatches;
	}

	@Override
	public List<TimesheetBatch> getLionHKOTBacthes(Long companyId,
			Long employeeId, List<String> otStatusList) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<TimesheetBatch> criteriaQuery = cb
				.createQuery(TimesheetBatch.class);
		Root<TimesheetBatch> otBatchRoot = criteriaQuery
				.from(TimesheetBatch.class);

		criteriaQuery.select(otBatchRoot);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(otBatchRoot.get(TimesheetBatch_.company), companyId));

		criteriaQuery.orderBy(cb.asc(otBatchRoot
				.get(TimesheetBatch_.createdDate)));

		criteriaQuery.where(restriction);

		TypedQuery<TimesheetBatch> companyTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		List<TimesheetBatch> timesheetBatches = companyTypedQuery
				.getResultList();

		return timesheetBatches;

	}

	@Override
	public TimesheetBatch findOtBatchExist(Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<TimesheetBatch> criteriaQuery = cb
				.createQuery(TimesheetBatch.class);
		Root<TimesheetBatch> root = criteriaQuery.from(TimesheetBatch.class);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction,
				cb.equal(root.get(TimesheetBatch_.company), companyId));

		criteriaQuery.select(root).where(restriction);
		TypedQuery<TimesheetBatch> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		try {
			List<TimesheetBatch> timesheetResult = typedQuery.getResultList();
			if (timesheetResult != null && timesheetResult.size() > 5)
				return timesheetResult.get(5);
			else
				return null;
		} catch (Exception e) {
			return null;
		}

	}

	@Override
	public List<TimesheetBatch> getOTBatchesByYear(Long companyId, int year) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<TimesheetBatch> criteriaQuery = cb
				.createQuery(TimesheetBatch.class);
		Root<TimesheetBatch> root = criteriaQuery.from(TimesheetBatch.class);
		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction,
				cb.equal(root.get(TimesheetBatch_.company), companyId));
		restriction = cb.and(
				restriction,
				cb.equal(
						cb.function("year", Integer.class,
								root.get(TimesheetBatch_.endDate)), year));
		criteriaQuery.select(root).where(restriction);
		TypedQuery<TimesheetBatch> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		return typedQuery.getResultList();
	}

	@Override
	public TimesheetBatch findLatestBatchLion(Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<TimesheetBatch> criteriaQuery = cb
				.createQuery(TimesheetBatch.class);
		Root<TimesheetBatch> root = criteriaQuery.from(TimesheetBatch.class);
		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction,
				cb.equal(root.get(TimesheetBatch_.company), companyId));

		criteriaQuery.select(root).where(restriction);
		criteriaQuery.orderBy(cb.desc(root.get(TimesheetBatch_.endDate)));

		TypedQuery<TimesheetBatch> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery).setMaxResults(1);

		List<TimesheetBatch> timesheetBatchList = typedQuery.getResultList();

		if (timesheetBatchList.size() > 0) {
			return timesheetBatchList.get(0);
		} else {
			return null;
		}

	}

	@Override
	public TimesheetBatch getBatchByCurrentDate(Long companyId, Timestamp date) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<TimesheetBatch> criteriaQuery = cb
				.createQuery(TimesheetBatch.class);
		Root<TimesheetBatch> otBatchRoot = criteriaQuery
				.from(TimesheetBatch.class);

		criteriaQuery.select(otBatchRoot);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(otBatchRoot.get(TimesheetBatch_.company), companyId));

		restriction = cb.and(restriction, cb.lessThanOrEqualTo(
				otBatchRoot.get(TimesheetBatch_.startDate).as(Date.class),
				DateUtils.stringToDate(DateUtils.timeStampToString(date))), cb
				.greaterThanOrEqualTo(otBatchRoot.get(TimesheetBatch_.endDate)
						.as(Date.class), DateUtils.stringToDate(DateUtils
						.timeStampToString(date))));
		criteriaQuery.orderBy(cb.asc(otBatchRoot
				.get(TimesheetBatch_.createdDate)));

		criteriaQuery.where(restriction);

		TypedQuery<TimesheetBatch> companyTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		List<TimesheetBatch> timesheetBatches = companyTypedQuery
				.getResultList();
		if (timesheetBatches.size() > 0) {
			return timesheetBatches.get(0);
		}
		return null;

	}

	@Override
	public List<TimesheetBatch> getCurrentAndPreviousBatchByCompany(
			Long companyId, Timestamp date) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<TimesheetBatch> criteriaQuery = cb
				.createQuery(TimesheetBatch.class);
		Root<TimesheetBatch> otBatchRoot = criteriaQuery
				.from(TimesheetBatch.class);

		criteriaQuery.select(otBatchRoot);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(otBatchRoot.get(TimesheetBatch_.company), companyId));
		restriction = cb.and(restriction, cb
				.lessThanOrEqualTo(otBatchRoot.get(TimesheetBatch_.startDate)
						.as(Date.class), date));

		criteriaQuery
				.orderBy(cb.desc(otBatchRoot.get(TimesheetBatch_.endDate)));
		criteriaQuery.where(restriction);

		TypedQuery<TimesheetBatch> companyTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery).setMaxResults(2);

		List<TimesheetBatch> timesheetBatches = companyTypedQuery
				.getResultList();
		return timesheetBatches;
	}

	@Override
	public TimesheetBatch findBatchByDate(Timestamp endDate, Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<TimesheetBatch> criteriaQuery = cb
				.createQuery(TimesheetBatch.class);
		Root<TimesheetBatch> root = criteriaQuery.from(TimesheetBatch.class);
		criteriaQuery.select(root);

		Predicate restriction = cb.conjunction();

		Join<TimesheetBatch, Company> companyJoin = root
				.join(TimesheetBatch_.company);

		restriction = cb.and(restriction,
				cb.equal(companyJoin.get(Company_.companyId), companyId));

		restriction = cb.and(restriction, cb.lessThan(
				root.get(TimesheetBatch_.endDate).as(Date.class), endDate));

		criteriaQuery.where(restriction);

		criteriaQuery.orderBy(cb.desc(root.get(TimesheetBatch_.endDate)));
		TypedQuery<TimesheetBatch> timesheetTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		TimesheetBatch lundinTimesheetBatch = timesheetTypedQuery
				.getResultList().get(0);
		return lundinTimesheetBatch;

	}

	@Override
	public TimesheetBatch findCurrentBatchForDate(Timestamp date, Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<TimesheetBatch> criteriaQuery = cb
				.createQuery(TimesheetBatch.class);
		Root<TimesheetBatch> root = criteriaQuery.from(TimesheetBatch.class);
		criteriaQuery.select(root);

		Predicate restriction = cb.conjunction();

		Join<TimesheetBatch, Company> companyJoin = root
				.join(TimesheetBatch_.company);

		restriction = cb.and(restriction,
				cb.equal(companyJoin.get(Company_.companyId), companyId));

		restriction = cb.and(restriction, cb.lessThan(
				root.get(TimesheetBatch_.startDate).as(Date.class), date));
		restriction = cb.and(restriction, cb.greaterThan(
				root.get(TimesheetBatch_.endDate).as(Date.class), date));
		criteriaQuery.where(restriction);

		criteriaQuery.orderBy(cb.desc(root.get(TimesheetBatch_.endDate)));
		TypedQuery<TimesheetBatch> timesheetTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		TimesheetBatch lundinTimesheetBatch = timesheetTypedQuery
				.getResultList().get(0);
		return lundinTimesheetBatch;

	}

	@Override
	public TimesheetBatch getBatchByStartDate(Long companyId, Timestamp date) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<TimesheetBatch> criteriaQuery = cb
				.createQuery(TimesheetBatch.class);
		Root<TimesheetBatch> otBatchRoot = criteriaQuery
				.from(TimesheetBatch.class);

		criteriaQuery.select(otBatchRoot);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(otBatchRoot.get(TimesheetBatch_.company), companyId));

		restriction = cb.and(restriction,
				cb.equal(otBatchRoot.get(TimesheetBatch_.startDate), date));
		criteriaQuery.orderBy(cb.asc(otBatchRoot
				.get(TimesheetBatch_.createdDate)));

		criteriaQuery.where(restriction);

		TypedQuery<TimesheetBatch> companyTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		List<TimesheetBatch> timesheetBatches = companyTypedQuery
				.getResultList();
		if (timesheetBatches.size() > 0) {
			return timesheetBatches.get(0);
		}
		return null;

	}

	@Override
	public TimesheetBatch findById(long id,Long companyId,
			Long employeeId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<TimesheetBatch> criteriaQuery = cb
				.createQuery(TimesheetBatch.class);
		Root<TimesheetBatch> root = criteriaQuery.from(TimesheetBatch.class);
		criteriaQuery.select(root);

		Predicate restriction = cb.conjunction();

		Join<TimesheetBatch, Company> companyJoin = root
				.join(TimesheetBatch_.company);
		
		restriction = cb.and(restriction,
				cb.equal(companyJoin.get(Company_.companyId), companyId));
		
		restriction = cb.and(restriction,
				cb.equal(root.get(TimesheetBatch_.timesheetBatchId), id));

		criteriaQuery.where(restriction);
		TypedQuery<TimesheetBatch> timesheetTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		TimesheetBatch lundinTimesheetBatch = timesheetTypedQuery
				.getResultList().get(0);
		return lundinTimesheetBatch;
	}
}
