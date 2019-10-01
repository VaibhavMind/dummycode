package com.payasia.dao.impl;

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

import com.payasia.common.dto.LundinPendingTsheetConditionDTO;
import com.payasia.common.dto.PendingOTTimesheetConditionDTO;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.DateUtils;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.TimesheetApplicationReviewerDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeTimesheetApplication;
import com.payasia.dao.bean.EmployeeTimesheetApplication_;
import com.payasia.dao.bean.Employee_;
import com.payasia.dao.bean.TimesheetApplicationReviewer;
import com.payasia.dao.bean.TimesheetApplicationReviewer_;
import com.payasia.dao.bean.TimesheetBatch_;
import com.payasia.dao.bean.TimesheetStatusMaster;
import com.payasia.dao.bean.TimesheetStatusMaster_;

@Repository
public class TimesheetApplicationReviewerDAOImpl extends BaseDAO implements
		TimesheetApplicationReviewerDAO {
	private static final Logger LOGGER = Logger
			.getLogger(TimesheetApplicationReviewerDAOImpl.class);

	@Override
	public void save(TimesheetApplicationReviewer lundinOTTimesheetReviewer) {
		super.save(lundinOTTimesheetReviewer);

	}

	@Override
	public TimesheetApplicationReviewer findById(long id) {
		return super.findById(TimesheetApplicationReviewer.class, id);
	}

	@Override
	protected Object getBaseEntity() {
		TimesheetApplicationReviewer lundinOTTimesheetReviewer = new TimesheetApplicationReviewer();
		return lundinOTTimesheetReviewer;
	}

	@Override
	public void update(TimesheetApplicationReviewer lundinOTTimesheetReviewer) {
		super.update(lundinOTTimesheetReviewer);
	}

	@Override
	public List<TimesheetApplicationReviewer> findByCondition(Long empId,
			PageRequest pageDTO, SortCondition sortDTO,
			PendingOTTimesheetConditionDTO otTimesheetConditionDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<TimesheetApplicationReviewer> criteriaQuery = cb
				.createQuery(TimesheetApplicationReviewer.class);
		Root<TimesheetApplicationReviewer> otRoot = criteriaQuery
				.from(TimesheetApplicationReviewer.class);
		criteriaQuery.select(otRoot);

		Predicate restriction = cb.conjunction();

		Join<TimesheetApplicationReviewer, Employee> empLeaveJoin = otRoot
				.join(TimesheetApplicationReviewer_.employee);

		Join<TimesheetApplicationReviewer, EmployeeTimesheetApplication> lundinOTTimesheetJoin = otRoot
				.join(TimesheetApplicationReviewer_.employeeTimesheetApplication);

		Join<EmployeeTimesheetApplication, TimesheetStatusMaster> otStatusJoin = lundinOTTimesheetJoin
				.join(EmployeeTimesheetApplication_.timesheetStatusMaster);

		if (StringUtils.isNotBlank(otTimesheetConditionDTO.getCreatedDate())) {
			restriction = cb.and(restriction, cb.equal(
					lundinOTTimesheetJoin.get(
							EmployeeTimesheetApplication_.createdDate).as(
							Date.class), DateUtils
							.stringToDate(otTimesheetConditionDTO
									.getCreatedDate())));

		}

		restriction = cb.and(restriction,
				cb.equal(empLeaveJoin.get(Employee_.employeeId), empId));

		restriction = cb.and(restriction, cb.equal(
				otRoot.get(TimesheetApplicationReviewer_.pending), true));

		if (StringUtils.isNotBlank(otTimesheetConditionDTO.getBatch())) {

			restriction = cb
					.and(restriction,
							cb.like(otRoot
									.get(TimesheetApplicationReviewer_.employeeTimesheetApplication)
									.get(EmployeeTimesheetApplication_.timesheetBatch)
									.get(TimesheetBatch_.timesheetBatchDesc),
									otTimesheetConditionDTO.getBatch()));
		}
		if (StringUtils.isNotBlank(otTimesheetConditionDTO.getEmployeeName())) {

			restriction = cb
					.and(restriction,
							cb.or(cb.like(
									otRoot.get(
											TimesheetApplicationReviewer_.employeeTimesheetApplication)
											.get(EmployeeTimesheetApplication_.employee)
											.get(Employee_.firstName),
									otTimesheetConditionDTO.getEmployeeName()),
									cb.like(otRoot
											.get(TimesheetApplicationReviewer_.employeeTimesheetApplication)
											.get(EmployeeTimesheetApplication_.employee)
											.get(Employee_.middleName),
											otTimesheetConditionDTO
													.getEmployeeName()),
									cb.like(otRoot
											.get(TimesheetApplicationReviewer_.employeeTimesheetApplication)
											.get(EmployeeTimesheetApplication_.employee)
											.get(Employee_.lastName),
											otTimesheetConditionDTO
													.getEmployeeName())));
		}
		criteriaQuery.where(restriction);

		// criteriaQuery.orderBy(cb.desc(lundinOTTimesheetJoin
		// .get(EmployeeTimesheetApplication_.updatedDate)));

		TypedQuery<TimesheetApplicationReviewer> otTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			otTypedQuery.setFirstResult(getStartPosition(pageDTO));
			otTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		return otTypedQuery.getResultList();
	}

	@Override
	public Integer findByConditionCountRecords(Long empId,
			PendingOTTimesheetConditionDTO otTimesheetConditionDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Integer> criteriaQuery = cb.createQuery(Integer.class);
		Root<TimesheetApplicationReviewer> otRoot = criteriaQuery
				.from(TimesheetApplicationReviewer.class);
		criteriaQuery.select(cb.count(otRoot).as(Integer.class));

		Predicate restriction = cb.conjunction();

		Join<TimesheetApplicationReviewer, Employee> empLeaveJoin = otRoot
				.join(TimesheetApplicationReviewer_.employee);

		Join<TimesheetApplicationReviewer, EmployeeTimesheetApplication> lundinOTTimesheetJoin = otRoot
				.join(TimesheetApplicationReviewer_.employeeTimesheetApplication);

		Join<EmployeeTimesheetApplication, TimesheetStatusMaster> otStatusJoin = lundinOTTimesheetJoin
				.join(EmployeeTimesheetApplication_.timesheetStatusMaster);

		if (StringUtils.isNotBlank(otTimesheetConditionDTO.getCreatedDate())) {
			restriction = cb.and(restriction, cb.equal(
					lundinOTTimesheetJoin.get(
							EmployeeTimesheetApplication_.createdDate).as(
							String.class),
					otTimesheetConditionDTO.getCreatedDate()));

		}

		restriction = cb.and(restriction,
				cb.equal(empLeaveJoin.get(Employee_.employeeId), empId));

		restriction = cb.and(restriction, cb.equal(
				otRoot.get(TimesheetApplicationReviewer_.pending), true));
		if (StringUtils.isNotBlank(otTimesheetConditionDTO.getEmployeeName())) {

			restriction = cb
					.and(restriction,
							cb.or(cb.like(
									otRoot.get(
											TimesheetApplicationReviewer_.employeeTimesheetApplication)
											.get(EmployeeTimesheetApplication_.employee)
											.get(Employee_.firstName),
									otTimesheetConditionDTO.getEmployeeName()),
									cb.like(otRoot
											.get(TimesheetApplicationReviewer_.employeeTimesheetApplication)
											.get(EmployeeTimesheetApplication_.employee)
											.get(Employee_.middleName),
											otTimesheetConditionDTO
													.getEmployeeName()),
									cb.like(otRoot
											.get(TimesheetApplicationReviewer_.employeeTimesheetApplication)
											.get(EmployeeTimesheetApplication_.employee)
											.get(Employee_.lastName),
											otTimesheetConditionDTO
													.getEmployeeName())));
		}
		if (StringUtils.isNotBlank(otTimesheetConditionDTO.getBatch())) {

			restriction = cb
					.and(restriction,
							cb.like(otRoot
									.get(TimesheetApplicationReviewer_.employeeTimesheetApplication)
									.get(EmployeeTimesheetApplication_.timesheetBatch)
									.get(TimesheetBatch_.timesheetBatchDesc),
									otTimesheetConditionDTO.getBatch()));
		}

		criteriaQuery.where(restriction);

		// criteriaQuery.orderBy(cb.desc(lundinOTTimesheetJoin
		// .get(EmployeeTimesheetApplication_.updatedDate)));

		TypedQuery<Integer> otTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return otTypedQuery.getSingleResult();
	}

	@Override
	public Integer getOTTimesheetReviewerCount(long timesheetId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Integer> criteriaQuery = cb.createQuery(Integer.class);
		Root<TimesheetApplicationReviewer> otReviewerRoot = criteriaQuery
				.from(TimesheetApplicationReviewer.class);
		criteriaQuery.select(cb.count(otReviewerRoot).as(Integer.class));

		Predicate restriction = cb.conjunction();

		restriction = cb
				.and(restriction,
						cb.equal(
								otReviewerRoot
										.get(TimesheetApplicationReviewer_.employeeTimesheetApplication)
										.get("timesheetId").as(Long.class),
								timesheetId));

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
	public List<TimesheetApplicationReviewer> checkOTEmployeeReviewer(
			long employeeId, List<String> otStatusList) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<TimesheetApplicationReviewer> criteriaQuery = cb
				.createQuery(TimesheetApplicationReviewer.class);
		Root<TimesheetApplicationReviewer> root = criteriaQuery
				.from(TimesheetApplicationReviewer.class);
		criteriaQuery.select(root);

		Predicate restriction = cb.conjunction();

		Join<TimesheetApplicationReviewer, Employee> empOTJoin = root
				.join(TimesheetApplicationReviewer_.employee);

		Join<TimesheetApplicationReviewer, EmployeeTimesheetApplication> otTimesheetStatusJoin = root
				.join(TimesheetApplicationReviewer_.employeeTimesheetApplication);
		Join<EmployeeTimesheetApplication, TimesheetStatusMaster> otStatusJoin = otTimesheetStatusJoin
				.join(EmployeeTimesheetApplication_.timesheetStatusMaster);
		
		restriction = cb.and(restriction,
				cb.equal(empOTJoin.get(Employee_.employeeId), employeeId));
		restriction = cb.and(restriction,
				otStatusJoin.get(TimesheetStatusMaster_.timesheetStatusName)
						.in(otStatusList));
		criteriaQuery.where(restriction);

		TypedQuery<TimesheetApplicationReviewer> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		if (typedQuery.getResultList().size() > 0) {
			return typedQuery.getResultList();
		} else {
			return null;
		}
	}

	@Override
	public List<TimesheetApplicationReviewer> findByCondition(Long employeeId,
			PageRequest pageDTO, SortCondition sortDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<TimesheetApplicationReviewer> criteriaQuery = cb
				.createQuery(TimesheetApplicationReviewer.class);
		Root<TimesheetApplicationReviewer> root = criteriaQuery
				.from(TimesheetApplicationReviewer.class);
		criteriaQuery.select(root);

		Predicate restriction = cb.conjunction();

		Join<TimesheetApplicationReviewer, Employee> empJoin = root
				.join(TimesheetApplicationReviewer_.employee);
		Join<TimesheetApplicationReviewer, EmployeeTimesheetApplication> lundinOTTimesheetJoin = root
				.join(TimesheetApplicationReviewer_.employeeTimesheetApplication);

		restriction = cb.and(restriction,
				cb.equal(empJoin.get(Employee_.employeeId), employeeId));

		restriction = cb
				.and(restriction, cb.equal(
						root.get(TimesheetApplicationReviewer_.pending), true));

		criteriaQuery.where(restriction);

		criteriaQuery.orderBy(cb.desc(lundinOTTimesheetJoin
				.get(EmployeeTimesheetApplication_.updatedDate)));
		TypedQuery<TimesheetApplicationReviewer> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			typedQuery.setFirstResult(getStartPosition(pageDTO));
			typedQuery.setMaxResults(pageDTO.getPageSize());
		}

		return typedQuery.getResultList();
	}

	@Override
	public TimesheetApplicationReviewer findByCondition(long timesheetId,
			long reviewerId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<TimesheetApplicationReviewer> criteriaQuery = cb
				.createQuery(TimesheetApplicationReviewer.class);
		Root<TimesheetApplicationReviewer> otReviewerRoot = criteriaQuery
				.from(TimesheetApplicationReviewer.class);

		Predicate restriction = cb.conjunction();

		restriction = cb
				.and(restriction,
						cb.equal(
								otReviewerRoot
										.get(TimesheetApplicationReviewer_.employeeTimesheetApplication)
										.get(EmployeeTimesheetApplication_.timesheetId),
								timesheetId));
		restriction = cb.and(
				restriction,
				cb.equal(
						otReviewerRoot.get(
								TimesheetApplicationReviewer_.employee).get(
								Employee_.employeeId), reviewerId));

		criteriaQuery.select(otReviewerRoot).where(restriction);

		TypedQuery<TimesheetApplicationReviewer> empTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		try {
			return empTypedQuery.getSingleResult();
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			return null;
		}
	}

	@Override
	public void delete(TimesheetApplicationReviewer otTsheetReviewer) {
		super.delete(otTsheetReviewer);

	}

	@Override
	public List<TimesheetApplicationReviewer> getPendingTSApplicationByIds(
			Long empId, List<Long> tsApplicationRevIdsList) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<TimesheetApplicationReviewer> criteriaQuery = cb
				.createQuery(TimesheetApplicationReviewer.class);
		Root<TimesheetApplicationReviewer> root = criteriaQuery
				.from(TimesheetApplicationReviewer.class);
		criteriaQuery.select(root);

		Predicate restriction = cb.conjunction();

		Join<TimesheetApplicationReviewer, Employee> empLeaveJoin = root
				.join(TimesheetApplicationReviewer_.employee);

		if (empId != null) {
			restriction = cb.and(restriction,
					cb.equal(empLeaveJoin.get(Employee_.employeeId), empId));
		}

		restriction = cb
				.and(restriction, cb.equal(
						root.get(TimesheetApplicationReviewer_.pending), true));
		restriction = cb.and(
				restriction,
				root.get(TimesheetApplicationReviewer_.timesheetReviewerId).in(
						tsApplicationRevIdsList));

		criteriaQuery.where(restriction);

		TypedQuery<TimesheetApplicationReviewer> leaveAppTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return leaveAppTypedQuery.getResultList();
	}

	@Override
	public List<TimesheetApplicationReviewer> findByTimesheetCondition(
			Long companyId,
			LundinPendingTsheetConditionDTO otTimesheetConditionDTO,
			PageRequest pageDTO, SortCondition sortDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<TimesheetApplicationReviewer> criteriaQuery = cb
				.createQuery(TimesheetApplicationReviewer.class);
		Root<TimesheetApplicationReviewer> otRoot = criteriaQuery
				.from(TimesheetApplicationReviewer.class);
		criteriaQuery.select(otRoot);

		Predicate restriction = cb.conjunction();
		Join<TimesheetApplicationReviewer, EmployeeTimesheetApplication> lundinOTTimesheetJoin = otRoot
				.join(TimesheetApplicationReviewer_.employeeTimesheetApplication);
		Join<EmployeeTimesheetApplication, Company> companyJoin = lundinOTTimesheetJoin
				.join(EmployeeTimesheetApplication_.company);

		if (otTimesheetConditionDTO.getEmployeeId() != null) {
			Join<TimesheetApplicationReviewer, Employee> employeeJoin = otRoot
					.join(TimesheetApplicationReviewer_.employee);
			restriction = cb.and(restriction, cb.equal(
					employeeJoin.get(Employee_.employeeId),
					otTimesheetConditionDTO.getEmployeeId()));
		}
		if (otTimesheetConditionDTO.getStatusNameList().size() > 0) {
			Join<EmployeeTimesheetApplication, TimesheetStatusMaster> otStatusJoin = lundinOTTimesheetJoin
					.join(EmployeeTimesheetApplication_.timesheetStatusMaster);
			restriction = cb.and(restriction,
					otStatusJoin
							.get(TimesheetStatusMaster_.timesheetStatusName)
							.in(otTimesheetConditionDTO.getStatusNameList()));
		}
		if (StringUtils.isNotBlank(otTimesheetConditionDTO.getCreatedDate())) {
			restriction = cb.and(restriction, cb.equal(
					lundinOTTimesheetJoin.get(
							EmployeeTimesheetApplication_.createdDate).as(
							Date.class), DateUtils
							.stringToDate(otTimesheetConditionDTO
									.getCreatedDate())));
		}
		if (StringUtils.isNotBlank(otTimesheetConditionDTO.getBatch())) {
			restriction = cb
					.and(restriction,
							cb.like(otRoot
									.get(TimesheetApplicationReviewer_.employeeTimesheetApplication)
									.get(EmployeeTimesheetApplication_.timesheetBatch)
									.get(TimesheetBatch_.timesheetBatchDesc),
									otTimesheetConditionDTO.getBatch()));
		}
		if (StringUtils.isNotBlank(otTimesheetConditionDTO.getEmployeeName())) {
			restriction = cb
					.and(restriction,
							cb.or(cb.like(
									otRoot.get(
											TimesheetApplicationReviewer_.employeeTimesheetApplication)
											.get(EmployeeTimesheetApplication_.employee)
											.get(Employee_.firstName),
									otTimesheetConditionDTO.getEmployeeName()),
									cb.like(otRoot
											.get(TimesheetApplicationReviewer_.employeeTimesheetApplication)
											.get(EmployeeTimesheetApplication_.employee)
											.get(Employee_.middleName),
											otTimesheetConditionDTO
													.getEmployeeName()),
									cb.like(otRoot
											.get(TimesheetApplicationReviewer_.employeeTimesheetApplication)
											.get(EmployeeTimesheetApplication_.employee)
											.get(Employee_.lastName),
											otTimesheetConditionDTO
													.getEmployeeName())));
		}
		restriction = cb.and(restriction, cb.equal(
				otRoot.get(TimesheetApplicationReviewer_.pending), true));
		restriction = cb.and(restriction,
				cb.equal(companyJoin.get(Company_.companyId), companyId));
		criteriaQuery.where(restriction);

		// criteriaQuery.orderBy(cb.desc(lundinOTTimesheetJoin
		// .get(EmployeeTimesheetApplication_.updatedDate)));

		TypedQuery<TimesheetApplicationReviewer> otTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			otTypedQuery.setFirstResult(getStartPosition(pageDTO));
			otTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		return otTypedQuery.getResultList();
	}

	@Override
	public Long getCountForTimesheetCondition(Long companyId,
			LundinPendingTsheetConditionDTO otTimesheetConditionDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
		Root<TimesheetApplicationReviewer> otRoot = criteriaQuery
				.from(TimesheetApplicationReviewer.class);
		criteriaQuery.select(cb.count(otRoot));

		Predicate restriction = cb.conjunction();
		Join<TimesheetApplicationReviewer, EmployeeTimesheetApplication> lundinOTTimesheetJoin = otRoot
				.join(TimesheetApplicationReviewer_.employeeTimesheetApplication);
		Join<EmployeeTimesheetApplication, Company> companyJoin = lundinOTTimesheetJoin
				.join(EmployeeTimesheetApplication_.company);

		if (otTimesheetConditionDTO.getEmployeeId() != null) {
			Join<TimesheetApplicationReviewer, Employee> employeeJoin = otRoot
					.join(TimesheetApplicationReviewer_.employee);
			restriction = cb.and(restriction, cb.equal(
					employeeJoin.get(Employee_.employeeId),
					otTimesheetConditionDTO.getEmployeeId()));
		}
		if (otTimesheetConditionDTO.getStatusNameList().size() > 0) {
			Join<EmployeeTimesheetApplication, TimesheetStatusMaster> otStatusJoin = lundinOTTimesheetJoin
					.join(EmployeeTimesheetApplication_.timesheetStatusMaster);
			restriction = cb.and(restriction,
					otStatusJoin
							.get(TimesheetStatusMaster_.timesheetStatusName)
							.in(otTimesheetConditionDTO.getStatusNameList()));
		}
		if (StringUtils.isNotBlank(otTimesheetConditionDTO.getCreatedDate())) {
			restriction = cb.and(restriction, cb.equal(
					lundinOTTimesheetJoin.get(
							EmployeeTimesheetApplication_.createdDate).as(
							Date.class), DateUtils
							.stringToDate(otTimesheetConditionDTO
									.getCreatedDate())));
		}
		if (StringUtils.isNotBlank(otTimesheetConditionDTO.getBatch())) {
			restriction = cb
					.and(restriction,
							cb.like(otRoot
									.get(TimesheetApplicationReviewer_.employeeTimesheetApplication)
									.get(EmployeeTimesheetApplication_.timesheetBatch)
									.get(TimesheetBatch_.timesheetBatchDesc),
									otTimesheetConditionDTO.getBatch()));
		}
		if (StringUtils.isNotBlank(otTimesheetConditionDTO.getEmployeeName())) {
			restriction = cb
					.and(restriction,
							cb.or(cb.like(
									otRoot.get(
											TimesheetApplicationReviewer_.employeeTimesheetApplication)
											.get(EmployeeTimesheetApplication_.employee)
											.get(Employee_.firstName),
									otTimesheetConditionDTO.getEmployeeName()),
									cb.like(otRoot
											.get(TimesheetApplicationReviewer_.employeeTimesheetApplication)
											.get(EmployeeTimesheetApplication_.employee)
											.get(Employee_.middleName),
											otTimesheetConditionDTO
													.getEmployeeName()),
									cb.like(otRoot
											.get(TimesheetApplicationReviewer_.employeeTimesheetApplication)
											.get(EmployeeTimesheetApplication_.employee)
											.get(Employee_.lastName),
											otTimesheetConditionDTO
													.getEmployeeName())));
		}
		restriction = cb.and(restriction, cb.equal(
				otRoot.get(TimesheetApplicationReviewer_.pending), true));
		restriction = cb.and(restriction,
				cb.equal(companyJoin.get(Company_.companyId), companyId));
		criteriaQuery.where(restriction);

		// criteriaQuery.orderBy(cb.desc(lundinOTTimesheetJoin
		// .get(EmployeeTimesheetApplication_.updatedDate)));

		TypedQuery<Long> claimAppTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return claimAppTypedQuery.getSingleResult();
	}
}
