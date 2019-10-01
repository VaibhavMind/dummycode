package com.payasia.dao.impl;

import java.lang.reflect.InvocationTargetException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.payasia.common.dto.PendingOTTimesheetConditionDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.NullAwareBeanUtilsBean;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.LionTimesheetApplicationReviewerDAO;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeTimesheetApplication;
import com.payasia.dao.bean.EmployeeTimesheetApplication_;
import com.payasia.dao.bean.Employee_;
import com.payasia.dao.bean.LionEmployeeTimesheetApplicationDetail;
import com.payasia.dao.bean.LionEmployeeTimesheetApplicationDetail_;
import com.payasia.dao.bean.LionTimesheetApplicationReviewer;
import com.payasia.dao.bean.LionTimesheetApplicationReviewer_;
import com.payasia.dao.bean.TimesheetBatch_;
import com.payasia.dao.bean.TimesheetStatusMaster;
import com.payasia.dao.bean.TimesheetStatusMaster_;
import com.payasia.dao.bean.WorkFlowRuleMaster;
import com.payasia.dao.bean.WorkFlowRuleMaster_;

@Repository
public class LionTimesheetApplicationReviewerDAOImpl extends BaseDAO implements
		LionTimesheetApplicationReviewerDAO {

	@Override
	protected Object getBaseEntity() {

		LionTimesheetApplicationReviewer lionTimesheetApplicationReviewer = new LionTimesheetApplicationReviewer();
		return lionTimesheetApplicationReviewer;
	}

	@Override
	public void update(
			LionTimesheetApplicationReviewer lionEmployeeTimesheetReviewer) {
		super.update(lionEmployeeTimesheetReviewer);
	}

	@Override
	public void delete(
			LionTimesheetApplicationReviewer lionEmployeeTimesheetReviewer) {
		super.delete(lionEmployeeTimesheetReviewer);
	}

	@Override
	public void save(
			LionTimesheetApplicationReviewer lionEmployeeTimesheetReviewer) {
		super.save(lionEmployeeTimesheetReviewer);
	}

	@Override
	public LionTimesheetApplicationReviewer findById(Long lionReviewerId) {
		return super.findById(LionTimesheetApplicationReviewer.class,
				lionReviewerId);

	}

	@Override
	public long saveReviewerAndReturnId(
			LionTimesheetApplicationReviewer lionTimesheetApplicationReviewer) {

		LionTimesheetApplicationReviewer persistObj = lionTimesheetApplicationReviewer;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (LionTimesheetApplicationReviewer) getBaseEntity();
			beanUtil.copyProperties(persistObj,
					lionTimesheetApplicationReviewer);
		} catch (IllegalAccessException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		} catch (InvocationTargetException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		}
		this.entityManagerFactory.persist(persistObj);
		this.entityManagerFactory.flush();
		long pid = persistObj.getEmployeeTimesheetReviewerId();
		return pid;

	}

	@Override
	public LionTimesheetApplicationReviewer saveAndReturn(
			LionTimesheetApplicationReviewer lionTimesheetApplicationReviewer) {

		LionTimesheetApplicationReviewer persistObj = lionTimesheetApplicationReviewer;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (LionTimesheetApplicationReviewer) getBaseEntity();
			beanUtil.copyProperties(persistObj,
					lionTimesheetApplicationReviewer);
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
	public List<LionTimesheetApplicationReviewer> findByCondition(
			Long employeeId, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LionTimesheetApplicationReviewer> criteriaQuery = cb
				.createQuery(LionTimesheetApplicationReviewer.class);

		Root<LionTimesheetApplicationReviewer> root = criteriaQuery
				.from(LionTimesheetApplicationReviewer.class);
		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(
				root.get(LionTimesheetApplicationReviewer_.employeeReviewer)
						.get(Employee_.employeeId), employeeId));
		restriction = cb.and(restriction, cb.equal(
				root.get(LionTimesheetApplicationReviewer_.pending), true));
		criteriaQuery.select(root).where(restriction);
		criteriaQuery.orderBy(cb.desc(root
				.get(LionTimesheetApplicationReviewer_.updatedDate)));
		TypedQuery<LionTimesheetApplicationReviewer> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return typedQuery.getResultList();

	}

	@Override
	public void deleteByCondition(Long employeeId) {

		String queryString = "DELETE FROM EmployeeTimesheetReviewer e WHERE e.employee.employeeId = :employee ";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("employee", employeeId);

		q.executeUpdate();
	}

	@Override
	public List<LionTimesheetApplicationReviewer> checkEmployeeOTReviewer(
			Long employeeId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LionTimesheetApplicationReviewer> criteriaQuery = cb
				.createQuery(LionTimesheetApplicationReviewer.class);
		Root<LionTimesheetApplicationReviewer> root = criteriaQuery
				.from(LionTimesheetApplicationReviewer.class);
		criteriaQuery.select(root);

		Join<LionTimesheetApplicationReviewer, Employee> employeeJoin2 = root
				.join(LionTimesheetApplicationReviewer_.employeeReviewer);

		Path<Long> employeeId2 = employeeJoin2.get(Employee_.employeeId);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(employeeId2, employeeId));

		criteriaQuery.where(restriction);

		TypedQuery<LionTimesheetApplicationReviewer> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		if (typedQuery.getResultList().size() > 0) {
			return typedQuery.getResultList();
		} else {
			return null;
		}
	}

	@Override
	public LionTimesheetApplicationReviewer findByWorkFlowCondition(
			Long employeeId, Long workFlowRuleId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LionTimesheetApplicationReviewer> criteriaQuery = cb
				.createQuery(LionTimesheetApplicationReviewer.class);
		Root<LionTimesheetApplicationReviewer> root = criteriaQuery
				.from(LionTimesheetApplicationReviewer.class);
		criteriaQuery.select(root);

		Join<LionTimesheetApplicationReviewer, WorkFlowRuleMaster> workFlowRuleJoin = root
				.join(LionTimesheetApplicationReviewer_.workFlowRuleMaster);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(
				workFlowRuleJoin.get(WorkFlowRuleMaster_.workFlowRuleId),
				workFlowRuleId));

		criteriaQuery.where(restriction);

		TypedQuery<LionTimesheetApplicationReviewer> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		List<LionTimesheetApplicationReviewer> empOTRevList = typedQuery
				.getResultList();
		if (empOTRevList != null && !empOTRevList.isEmpty()) {
			return empOTRevList.get(0);
		}
		return null;
	}

	@Override
	public List<Tuple> getCountByConditionReviewer(Long empId,
			PageRequest pageDTO, SortCondition sortDTO,
			PendingOTTimesheetConditionDTO otTimesheetConditionDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Tuple> criteriaQuery = cb.createQuery(Tuple.class);
		Root<LionTimesheetApplicationReviewer> otRoot = criteriaQuery
				.from(LionTimesheetApplicationReviewer.class);

		Join<LionTimesheetApplicationReviewer, Employee> empLeaveJoin = otRoot
				.join(LionTimesheetApplicationReviewer_.employeeReviewer);

		Join<LionTimesheetApplicationReviewer, LionEmployeeTimesheetApplicationDetail> empTimesheetDetailJoin = otRoot
				.join(LionTimesheetApplicationReviewer_.lionEmployeeTimesheetApplicationDetail);

		Join<LionEmployeeTimesheetApplicationDetail, EmployeeTimesheetApplication> empTimesheetJoin = empTimesheetDetailJoin
				.join(LionEmployeeTimesheetApplicationDetail_.employeeTimesheetApplication);

		List<Selection<?>> selectionItems = new ArrayList<Selection<?>>();

		selectionItems.add(empTimesheetJoin.get(
				EmployeeTimesheetApplication_.timesheetId).alias(
				getAlias(EmployeeTimesheetApplication_.timesheetId)));
		selectionItems.add(empTimesheetJoin
				.get(EmployeeTimesheetApplication_.employee)
				.get(Employee_.firstName).alias(getAlias(Employee_.firstName)));
		selectionItems.add(empTimesheetJoin
				.get(EmployeeTimesheetApplication_.employee)
				.get(Employee_.lastName).alias(getAlias(Employee_.lastName)));
		selectionItems.add(empTimesheetJoin
				.get(EmployeeTimesheetApplication_.employee)
				.get(Employee_.employeeNumber)
				.alias(getAlias(Employee_.employeeNumber)));
		selectionItems.add(empTimesheetJoin.get(
				EmployeeTimesheetApplication_.createdDate).alias(
				getAlias(EmployeeTimesheetApplication_.createdDate)));
		selectionItems.add(empTimesheetJoin
				.get(EmployeeTimesheetApplication_.timesheetBatch)
				.get(TimesheetBatch_.timesheetBatchDesc)
				.alias(getAlias(TimesheetBatch_.timesheetBatchDesc)));
		// selectionItems
		// .add(otRoot
		// .get(LionTimesheetApplicationReviewer_.employeeTimesheetReviewerId)
		// .alias(getAlias(LionTimesheetApplicationReviewer_.employeeTimesheetReviewerId)));
		criteriaQuery.multiselect(selectionItems).distinct(true);

		Predicate restriction = cb.conjunction();

		if (StringUtils.isNotBlank(otTimesheetConditionDTO.getCreatedDate())) {
			restriction = cb.and(restriction, cb.equal(
					empTimesheetJoin.get(
							EmployeeTimesheetApplication_.createdDate).as(
							Date.class), DateUtils
							.stringToDate(otTimesheetConditionDTO
									.getCreatedDate())));

		}

		restriction = cb.and(restriction,
				cb.equal(empLeaveJoin.get(Employee_.employeeId), empId));

		/*
		 * restriction = cb.and(restriction, cb.equal(
		 * otRoot.get(LionTimesheetApplicationReviewer_.pending), true));
		 */

		if (StringUtils.isNotBlank(otTimesheetConditionDTO.getBatch())) {

			restriction = cb.and(restriction, cb.like(
					empTimesheetJoin.get(
							EmployeeTimesheetApplication_.timesheetBatch).get(
							TimesheetBatch_.timesheetBatchDesc),
					otTimesheetConditionDTO.getBatch()));
		}

		if (StringUtils.isNotBlank(otTimesheetConditionDTO
				.getOtTimesheetStatusName())) {
			restriction = cb
					.and(restriction,
							cb.like(empTimesheetJoin
									.get(EmployeeTimesheetApplication_.timesheetStatusMaster)
									.get(TimesheetStatusMaster_.timesheetStatusDesc),
									otTimesheetConditionDTO
											.getOtTimesheetStatusName()));

		}

		if (StringUtils.isNotBlank(otTimesheetConditionDTO.getEmployeeName())) {

			restriction = cb.and(restriction, cb.or(
					cb.like(empTimesheetJoin.get(
							EmployeeTimesheetApplication_.employee).get(
							Employee_.firstName),
							otTimesheetConditionDTO.getEmployeeName()),
					cb.like(empTimesheetJoin.get(
							EmployeeTimesheetApplication_.employee).get(
							Employee_.middleName),
							otTimesheetConditionDTO.getEmployeeName()),
					cb.like(empTimesheetJoin.get(
							EmployeeTimesheetApplication_.employee).get(
							Employee_.lastName),
							otTimesheetConditionDTO.getEmployeeName())));
		}
		criteriaQuery.where(restriction);

		// criteriaQuery.orderBy(cb.desc(lundinOTTimesheetJoin
		// .get(EmployeeTimesheetApplication_.updatedDate)));

		TypedQuery<Tuple> otTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return otTypedQuery.getResultList();
	}

	@Override
	public List<Tuple> findReviewListByCondition(Long empId,
			PageRequest pageDTO, SortCondition sortDTO,
			PendingOTTimesheetConditionDTO otTimesheetConditionDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Tuple> criteriaQuery = cb.createQuery(Tuple.class);
		Root<LionTimesheetApplicationReviewer> otRoot = criteriaQuery
				.from(LionTimesheetApplicationReviewer.class);

		Join<LionTimesheetApplicationReviewer, Employee> empLeaveJoin = otRoot
				.join(LionTimesheetApplicationReviewer_.employeeReviewer);

		Join<LionTimesheetApplicationReviewer, LionEmployeeTimesheetApplicationDetail> empTimesheetDetailJoin = otRoot
				.join(LionTimesheetApplicationReviewer_.lionEmployeeTimesheetApplicationDetail);

		Join<LionEmployeeTimesheetApplicationDetail, EmployeeTimesheetApplication> empTimesheetJoin = empTimesheetDetailJoin
				.join(LionEmployeeTimesheetApplicationDetail_.employeeTimesheetApplication);

		List<Selection<?>> selectionItems = new ArrayList<Selection<?>>();

		selectionItems.add(empTimesheetJoin.get(
				EmployeeTimesheetApplication_.timesheetId).alias(
				getAlias(EmployeeTimesheetApplication_.timesheetId)));
		selectionItems.add(empTimesheetJoin
				.get(EmployeeTimesheetApplication_.employee)
				.get(Employee_.firstName).alias(getAlias(Employee_.firstName)));
		selectionItems.add(empTimesheetJoin
				.get(EmployeeTimesheetApplication_.employee)
				.get(Employee_.lastName).alias(getAlias(Employee_.lastName)));
		selectionItems.add(empTimesheetJoin
				.get(EmployeeTimesheetApplication_.employee)
				.get(Employee_.employeeNumber)
				.alias(getAlias(Employee_.employeeNumber)));
		selectionItems.add(empTimesheetJoin.get(
				EmployeeTimesheetApplication_.createdDate).alias(
				getAlias(EmployeeTimesheetApplication_.createdDate)));
		selectionItems.add(empTimesheetJoin
				.get(EmployeeTimesheetApplication_.timesheetBatch)
				.get(TimesheetBatch_.timesheetBatchDesc)
				.alias(getAlias(TimesheetBatch_.timesheetBatchDesc)));
		// selectionItems
		// .add(otRoot
		// .get(LionTimesheetApplicationReviewer_.employeeTimesheetReviewerId)
		// .alias(getAlias(LionTimesheetApplicationReviewer_.employeeTimesheetReviewerId)));
		criteriaQuery.multiselect(selectionItems).distinct(true);

		Predicate restriction = cb.conjunction();

		if (StringUtils.isNotBlank(otTimesheetConditionDTO.getCreatedDate())) {
			restriction = cb.and(restriction, cb.equal(
					empTimesheetJoin.get(
							EmployeeTimesheetApplication_.createdDate).as(
							Date.class), DateUtils
							.stringToDate(otTimesheetConditionDTO
									.getCreatedDate())));

		}

		restriction = cb.and(restriction,
				cb.equal(empLeaveJoin.get(Employee_.employeeId), empId));

		/*
		 * restriction = cb.and(restriction, cb.equal(
		 * otRoot.get(LionTimesheetApplicationReviewer_.pending), true));
		 */

		if (StringUtils.isNotBlank(otTimesheetConditionDTO.getBatch())) {

			restriction = cb.and(restriction, cb.like(
					empTimesheetJoin.get(
							EmployeeTimesheetApplication_.timesheetBatch).get(
							TimesheetBatch_.timesheetBatchDesc),
					otTimesheetConditionDTO.getBatch()));
		}

		if (StringUtils.isNotBlank(otTimesheetConditionDTO
				.getOtTimesheetStatusName())) {
			restriction = cb
					.and(restriction,
							cb.like(empTimesheetJoin
									.get(EmployeeTimesheetApplication_.timesheetStatusMaster)
									.get(TimesheetStatusMaster_.timesheetStatusDesc),
									otTimesheetConditionDTO
											.getOtTimesheetStatusName()));

		}

		if (StringUtils.isNotBlank(otTimesheetConditionDTO.getEmployeeName())) {

			restriction = cb.and(restriction, cb.or(
					cb.like(empTimesheetJoin.get(
							EmployeeTimesheetApplication_.employee).get(
							Employee_.firstName),
							otTimesheetConditionDTO.getEmployeeName()),
					cb.like(empTimesheetJoin.get(
							EmployeeTimesheetApplication_.employee).get(
							Employee_.middleName),
							otTimesheetConditionDTO.getEmployeeName()),
					cb.like(empTimesheetJoin.get(
							EmployeeTimesheetApplication_.employee).get(
							Employee_.lastName),
							otTimesheetConditionDTO.getEmployeeName())));
		}
		criteriaQuery.where(restriction);

		// criteriaQuery.orderBy(cb.desc(lundinOTTimesheetJoin
		// .get(EmployeeTimesheetApplication_.updatedDate)));

		TypedQuery<Tuple> otTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			otTypedQuery.setFirstResult(getStartPosition(pageDTO));
			otTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		return otTypedQuery.getResultList();
	}

	@Override
	public List<Tuple> getCountByCondition(Long companyId, PageRequest pageDTO,
			SortCondition sortDTO,
			PendingOTTimesheetConditionDTO otTimesheetConditionDTO) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Tuple> criteriaQuery = cb.createQuery(Tuple.class);
		Root<LionTimesheetApplicationReviewer> otRoot = criteriaQuery
				.from(LionTimesheetApplicationReviewer.class);

		/*
		 * Join<LionTimesheetApplicationReviewer, Employee> empLeaveJoin =
		 * otRoot .join(LionTimesheetApplicationReviewer_.employeeReviewer);
		 */

		Join<LionTimesheetApplicationReviewer, LionEmployeeTimesheetApplicationDetail> empTimesheetDetailJoin = otRoot
				.join(LionTimesheetApplicationReviewer_.lionEmployeeTimesheetApplicationDetail);

		Join<LionEmployeeTimesheetApplicationDetail, EmployeeTimesheetApplication> empTimesheetJoin = empTimesheetDetailJoin
				.join(LionEmployeeTimesheetApplicationDetail_.employeeTimesheetApplication);

		List<Selection<?>> selectionItems = new ArrayList<Selection<?>>();

		selectionItems.add(empTimesheetJoin.get(
				EmployeeTimesheetApplication_.timesheetId).alias(
				getAlias(EmployeeTimesheetApplication_.timesheetId)));
		selectionItems.add(empTimesheetJoin
				.get(EmployeeTimesheetApplication_.employee)
				.get(Employee_.firstName).alias(getAlias(Employee_.firstName)));
		selectionItems.add(empTimesheetJoin
				.get(EmployeeTimesheetApplication_.employee)
				.get(Employee_.lastName).alias(getAlias(Employee_.lastName)));
		selectionItems.add(empTimesheetJoin
				.get(EmployeeTimesheetApplication_.employee)
				.get(Employee_.employeeNumber)
				.alias(getAlias(Employee_.employeeNumber)));
		selectionItems.add(empTimesheetJoin.get(
				EmployeeTimesheetApplication_.createdDate).alias(
				getAlias(EmployeeTimesheetApplication_.createdDate)));
		selectionItems.add(empTimesheetJoin
				.get(EmployeeTimesheetApplication_.timesheetBatch)
				.get(TimesheetBatch_.timesheetBatchDesc)
				.alias(getAlias(TimesheetBatch_.timesheetBatchDesc)));
		// selectionItems
		// .add(otRoot
		// .get(LionTimesheetApplicationReviewer_.employeeTimesheetReviewerId)
		// .alias(getAlias(LionTimesheetApplicationReviewer_.employeeTimesheetReviewerId)));
		criteriaQuery.multiselect(selectionItems).distinct(true);

		Predicate restriction = cb.conjunction();

		if (StringUtils.isNotBlank(otTimesheetConditionDTO.getCreatedDate())) {
			restriction = cb.and(restriction, cb.equal(
					empTimesheetJoin.get(
							EmployeeTimesheetApplication_.createdDate).as(
							Date.class), DateUtils
							.stringToDate(otTimesheetConditionDTO
									.getCreatedDate())));

		}

		/*
		 * restriction = cb.and(restriction,
		 * cb.equal(empLeaveJoin.get(Employee_.employeeId), empId));
		 */

		/*
		 * restriction = cb.and(restriction, cb.equal(
		 * otRoot.get(LionTimesheetApplicationReviewer_.pending), true));
		 */
		if (StringUtils.isNotBlank(otTimesheetConditionDTO
				.getOtTimesheetStatusName())) {
			restriction = cb
					.and(restriction,
							empTimesheetJoin
									.get(EmployeeTimesheetApplication_.timesheetStatusMaster)
									.get(TimesheetStatusMaster_.timesheetStatusName)
									.in(otTimesheetConditionDTO
											.getOtTimesheetStatusName()));
		}

		if (StringUtils.isNotBlank(otTimesheetConditionDTO.getBatch())) {

			restriction = cb.and(restriction, cb.like(
					empTimesheetJoin.get(
							EmployeeTimesheetApplication_.timesheetBatch).get(
							TimesheetBatch_.timesheetBatchDesc),
					otTimesheetConditionDTO.getBatch()));
		}

		restriction = cb.and(
				restriction,
				cb.equal(
						empTimesheetJoin.get(
								EmployeeTimesheetApplication_.company).get(
								Company_.companyId), companyId));

		if (StringUtils.isNotBlank(otTimesheetConditionDTO.getEmployeeName())) {

			restriction = cb.and(restriction, cb.or(
					cb.like(empTimesheetJoin.get(
							EmployeeTimesheetApplication_.employee).get(
							Employee_.firstName),
							otTimesheetConditionDTO.getEmployeeName()),
					cb.like(empTimesheetJoin.get(
							EmployeeTimesheetApplication_.employee).get(
							Employee_.middleName),
							otTimesheetConditionDTO.getEmployeeName()),
					cb.like(empTimesheetJoin.get(
							EmployeeTimesheetApplication_.employee).get(
							Employee_.lastName),
							otTimesheetConditionDTO.getEmployeeName())));
		}
		criteriaQuery.where(restriction);

		// criteriaQuery.orderBy(cb.desc(lundinOTTimesheetJoin
		// .get(EmployeeTimesheetApplication_.updatedDate)));

		TypedQuery<Tuple> otTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return otTypedQuery.getResultList();
	}

	@Override
	public List<Tuple> findAllReviewListByCondition(Long companyId,
			PageRequest pageDTO, SortCondition sortDTO,
			PendingOTTimesheetConditionDTO otTimesheetConditionDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Tuple> criteriaQuery = cb.createQuery(Tuple.class);
		Root<LionTimesheetApplicationReviewer> otRoot = criteriaQuery
				.from(LionTimesheetApplicationReviewer.class);

		/*
		 * Join<LionTimesheetApplicationReviewer, Employee> empLeaveJoin =
		 * otRoot .join(LionTimesheetApplicationReviewer_.employeeReviewer);
		 */

		Join<LionTimesheetApplicationReviewer, LionEmployeeTimesheetApplicationDetail> empTimesheetDetailJoin = otRoot
				.join(LionTimesheetApplicationReviewer_.lionEmployeeTimesheetApplicationDetail);

		Join<LionEmployeeTimesheetApplicationDetail, EmployeeTimesheetApplication> empTimesheetJoin = empTimesheetDetailJoin
				.join(LionEmployeeTimesheetApplicationDetail_.employeeTimesheetApplication);

		List<Selection<?>> selectionItems = new ArrayList<Selection<?>>();

		selectionItems.add(empTimesheetJoin.get(
				EmployeeTimesheetApplication_.timesheetId).alias(
				getAlias(EmployeeTimesheetApplication_.timesheetId)));
		selectionItems.add(empTimesheetJoin
				.get(EmployeeTimesheetApplication_.employee)
				.get(Employee_.firstName).alias(getAlias(Employee_.firstName)));
		selectionItems.add(empTimesheetJoin
				.get(EmployeeTimesheetApplication_.employee)
				.get(Employee_.lastName).alias(getAlias(Employee_.lastName)));
		selectionItems.add(empTimesheetJoin
				.get(EmployeeTimesheetApplication_.employee)
				.get(Employee_.employeeNumber)
				.alias(getAlias(Employee_.employeeNumber)));
		selectionItems.add(empTimesheetJoin.get(
				EmployeeTimesheetApplication_.createdDate).alias(
				getAlias(EmployeeTimesheetApplication_.createdDate)));
		selectionItems.add(empTimesheetJoin
				.get(EmployeeTimesheetApplication_.timesheetBatch)
				.get(TimesheetBatch_.timesheetBatchDesc)
				.alias(getAlias(TimesheetBatch_.timesheetBatchDesc)));
		// selectionItems
		// .add(otRoot
		// .get(LionTimesheetApplicationReviewer_.employeeTimesheetReviewerId)
		// .alias(getAlias(LionTimesheetApplicationReviewer_.employeeTimesheetReviewerId)));
		criteriaQuery.multiselect(selectionItems).distinct(true);

		Predicate restriction = cb.conjunction();

		if (StringUtils.isNotBlank(otTimesheetConditionDTO.getCreatedDate())) {
			restriction = cb.and(restriction, cb.equal(
					empTimesheetJoin.get(
							EmployeeTimesheetApplication_.createdDate).as(
							Date.class), DateUtils
							.stringToDate(otTimesheetConditionDTO
									.getCreatedDate())));

		}

		/*
		 * restriction = cb.and(restriction,
		 * cb.equal(empLeaveJoin.get(Employee_.employeeId), empId));
		 */

		/*
		 * restriction = cb.and(restriction, cb.equal(
		 * otRoot.get(LionTimesheetApplicationReviewer_.pending), true));
		 */
		if (StringUtils.isNotBlank(otTimesheetConditionDTO
				.getOtTimesheetStatusName())) {
			restriction = cb
					.and(restriction,
							empTimesheetJoin
									.get(EmployeeTimesheetApplication_.timesheetStatusMaster)
									.get(TimesheetStatusMaster_.timesheetStatusName)
									.in(otTimesheetConditionDTO
											.getOtTimesheetStatusName()));
		}

		if (StringUtils.isNotBlank(otTimesheetConditionDTO.getBatch())) {

			restriction = cb.and(restriction, cb.like(
					empTimesheetJoin.get(
							EmployeeTimesheetApplication_.timesheetBatch).get(
							TimesheetBatch_.timesheetBatchDesc),
					otTimesheetConditionDTO.getBatch()));
		}

		restriction = cb.and(
				restriction,
				cb.equal(
						empTimesheetJoin.get(
								EmployeeTimesheetApplication_.company).get(
								Company_.companyId), companyId));

		if (StringUtils.isNotBlank(otTimesheetConditionDTO.getEmployeeName())) {

			restriction = cb.and(restriction, cb.or(
					cb.like(empTimesheetJoin.get(
							EmployeeTimesheetApplication_.employee).get(
							Employee_.firstName),
							otTimesheetConditionDTO.getEmployeeName()),
					cb.like(empTimesheetJoin.get(
							EmployeeTimesheetApplication_.employee).get(
							Employee_.middleName),
							otTimesheetConditionDTO.getEmployeeName()),
					cb.like(empTimesheetJoin.get(
							EmployeeTimesheetApplication_.employee).get(
							Employee_.lastName),
							otTimesheetConditionDTO.getEmployeeName())));
		}
		criteriaQuery.where(restriction);

		// criteriaQuery.orderBy(cb.desc(lundinOTTimesheetJoin
		// .get(EmployeeTimesheetApplication_.updatedDate)));

		TypedQuery<Tuple> otTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			otTypedQuery.setFirstResult(getStartPosition(pageDTO));
			otTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		return otTypedQuery.getResultList();
	}

	@Override
	public LionTimesheetApplicationReviewer findByEmployeeTimesheetDetailId(
			long employeeTimesheetDetailId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LionTimesheetApplicationReviewer> criteriaQuery = cb
				.createQuery(LionTimesheetApplicationReviewer.class);
		Root<LionTimesheetApplicationReviewer> root = criteriaQuery
				.from(LionTimesheetApplicationReviewer.class);

		Predicate restriction = cb.conjunction();
		restriction = cb
				.and(restriction,
						cb.equal(
								root.get(LionTimesheetApplicationReviewer_.lionEmployeeTimesheetApplicationDetail),
								employeeTimesheetDetailId));

		criteriaQuery.select(root).where(restriction);

		TypedQuery<LionTimesheetApplicationReviewer> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		List<LionTimesheetApplicationReviewer> empOTRevList = typedQuery
				.getResultList();
		if (empOTRevList != null && !empOTRevList.isEmpty()) {
			return empOTRevList.get(0);
		}
		return null;
	}

	@Override
	public List<LionTimesheetApplicationReviewer> findByTimesheetStatus(
			List<String> timesheetStatusNames, Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LionTimesheetApplicationReviewer> criteriaQuery = cb
				.createQuery(LionTimesheetApplicationReviewer.class);
		Root<LionTimesheetApplicationReviewer> empRoot = criteriaQuery
				.from(LionTimesheetApplicationReviewer.class);
		criteriaQuery.select(empRoot);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(
				empRoot.get(LionTimesheetApplicationReviewer_.pending), true));
		restriction = cb.and(restriction, cb.equal(
				empRoot.get(LionTimesheetApplicationReviewer_.companyId),
				companyId));
		criteriaQuery.where(restriction);

		TypedQuery<LionTimesheetApplicationReviewer> timesheetTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		List<LionTimesheetApplicationReviewer> lundinTimesheetReviewers = timesheetTypedQuery
				.getResultList();

		return lundinTimesheetReviewers;
	}

	@Override
	public List<LionTimesheetApplicationReviewer> checkOTEmployeeReviewer(
			long employeeId, List<String> otStatusList) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LionTimesheetApplicationReviewer> criteriaQuery = cb
				.createQuery(LionTimesheetApplicationReviewer.class);
		Root<LionTimesheetApplicationReviewer> root = criteriaQuery
				.from(LionTimesheetApplicationReviewer.class);
		criteriaQuery.select(root);

		Predicate restriction = cb.conjunction();

		Join<LionTimesheetApplicationReviewer, Employee> empOTJoin = root
				.join(LionTimesheetApplicationReviewer_.employeeReviewer);

		Join<LionTimesheetApplicationReviewer, LionEmployeeTimesheetApplicationDetail> otTimesheetStatusJoin = root
				.join(LionTimesheetApplicationReviewer_.lionEmployeeTimesheetApplicationDetail);
		Join<LionEmployeeTimesheetApplicationDetail, TimesheetStatusMaster> otStatusJoin = otTimesheetStatusJoin
				.join(LionEmployeeTimesheetApplicationDetail_.timesheetStatusMaster);

		restriction = cb.and(restriction,
				cb.equal(empOTJoin.get(Employee_.employeeId), employeeId));
		restriction = cb.and(restriction,
				otStatusJoin.get(TimesheetStatusMaster_.timesheetStatusName)
						.in(otStatusList));
		criteriaQuery.where(restriction);

		TypedQuery<LionTimesheetApplicationReviewer> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		if (typedQuery.getResultList().size() > 0) {
			return typedQuery.getResultList();
		} else {
			return null;
		}
	}

	//#######################################################
	
	@Override
	public List<LionTimesheetApplicationReviewer> findByCondition(Long employeeId, Long companyId, PageRequest pageDTO,
			SortCondition sortDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LionTimesheetApplicationReviewer> criteriaQuery = cb
				.createQuery(LionTimesheetApplicationReviewer.class);

		Root<LionTimesheetApplicationReviewer> root = criteriaQuery
				.from(LionTimesheetApplicationReviewer.class);
		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(
				root.get(LionTimesheetApplicationReviewer_.employeeReviewer)
						.get(Employee_.employeeId), employeeId));
		restriction = cb.and(restriction, cb.equal(
				root.get(LionTimesheetApplicationReviewer_.pending), true));
		criteriaQuery.select(root).where(restriction);
		criteriaQuery.orderBy(cb.desc(root
				.get(LionTimesheetApplicationReviewer_.updatedDate)));
		TypedQuery<LionTimesheetApplicationReviewer> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		
		// Added by Gaurav for Pagination
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			typedQuery.setFirstResult(getStartPosition(pageDTO));
			typedQuery.setMaxResults(pageDTO.getPageSize());
		}

		return typedQuery.getResultList();

	}

}
