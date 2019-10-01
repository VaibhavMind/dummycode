package com.payasia.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.SortConstants;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.LeaveGrantBatchEmployeeDetailDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.Employee_;
import com.payasia.dao.bean.LeaveGrantBatch;
import com.payasia.dao.bean.LeaveGrantBatchDetail;
import com.payasia.dao.bean.LeaveGrantBatchDetail_;
import com.payasia.dao.bean.LeaveGrantBatchEmployeeDetail;
import com.payasia.dao.bean.LeaveGrantBatchEmployeeDetail_;
import com.payasia.dao.bean.LeaveGrantBatch_;

@Repository
public class LeaveGrantBatchEmployeeDetailDAOImpl extends BaseDAO implements
		LeaveGrantBatchEmployeeDetailDAO {

	@Override
	protected Object getBaseEntity() {
		LeaveGrantBatchEmployeeDetail leaveGrantBatchEmployeeDetail = new LeaveGrantBatchEmployeeDetail();
		return leaveGrantBatchEmployeeDetail;
	}

	@Override
	public void update(
			LeaveGrantBatchEmployeeDetail leaveGrantBatchEmployeeDetail) {
		super.update(leaveGrantBatchEmployeeDetail);
	}

	@Override
	public void delete(
			LeaveGrantBatchEmployeeDetail leaveGrantBatchEmployeeDetail) {
		super.delete(leaveGrantBatchEmployeeDetail);
	}

	@Override
	public void save(LeaveGrantBatchEmployeeDetail leaveGrantBatchEmployeeDetail) {
		super.save(leaveGrantBatchEmployeeDetail);
	}

	@Override
	public LeaveGrantBatchEmployeeDetail findByID(
			Long leaveGrantBatchEmployeeDetailId) {
		return super.findById(LeaveGrantBatchEmployeeDetail.class,
				leaveGrantBatchEmployeeDetailId);
	}

	@Override
	public List<LeaveGrantBatchEmployeeDetail> findByCondition(
			Long leaveGrantBatchDetailId, String employeeNumber,
			PageRequest pageDTO, SortCondition sortDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveGrantBatchEmployeeDetail> criteriaQuery = cb
				.createQuery(LeaveGrantBatchEmployeeDetail.class);
		Root<LeaveGrantBatchEmployeeDetail> leaveGrantRoot = criteriaQuery
				.from(LeaveGrantBatchEmployeeDetail.class);
		criteriaQuery.select(leaveGrantRoot);

		Predicate restriction = cb.conjunction();

		Join<LeaveGrantBatchEmployeeDetail, LeaveGrantBatchDetail> leaveGrantBatchDetailJoin = leaveGrantRoot
				.join(LeaveGrantBatchEmployeeDetail_.leaveGrantBatchDetail);
		Join<LeaveGrantBatchEmployeeDetail, Employee> employeeJoin = leaveGrantRoot
				.join(LeaveGrantBatchEmployeeDetail_.employee);

		restriction = cb.and(restriction, cb.equal(leaveGrantBatchDetailJoin
				.get(LeaveGrantBatchDetail_.leaveGrantBatchDetailId),
				leaveGrantBatchDetailId));

		restriction = cb.and(restriction, cb.isNull(leaveGrantRoot
				.get(LeaveGrantBatchEmployeeDetail_.deletedDate)));
		if (StringUtils.isNotBlank(employeeNumber)) {
			restriction = cb.and(restriction, cb.like(
					cb.upper(employeeJoin.get(Employee_.employeeNumber)),
					employeeNumber.toUpperCase() + '%'));
		}

		criteriaQuery.where(restriction);

		if (sortDTO != null) {

			Path<String> sortPath = getSortPathForLeaveGrant(sortDTO,
					leaveGrantRoot);
			if (sortPath != null) {
				if (SortConstants.DB_ORDER_BY_ASC.equalsIgnoreCase(sortDTO
						.getOrderType())) {
					criteriaQuery.orderBy(cb.asc(sortPath));
				}
				if (SortConstants.DB_ORDER_BY_DESC.equalsIgnoreCase(sortDTO
						.getOrderType())) {
					criteriaQuery.orderBy(cb.desc(sortPath));
				}
			}

		}

		TypedQuery<LeaveGrantBatchEmployeeDetail> leaveGrantTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			leaveGrantTypedQuery.setFirstResult(getStartPosition(pageDTO));
			leaveGrantTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		return leaveGrantTypedQuery.getResultList();
	}

	@Override
	public Integer getCountByCondition(Long leaveGrantBatchDetailId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Integer> criteriaQuery = cb.createQuery(Integer.class);
		Root<LeaveGrantBatchEmployeeDetail> leaveGrantRoot = criteriaQuery
				.from(LeaveGrantBatchEmployeeDetail.class);
		criteriaQuery.select(cb.count(leaveGrantRoot).as(Integer.class));

		Predicate restriction = cb.conjunction();

		Join<LeaveGrantBatchEmployeeDetail, LeaveGrantBatchDetail> leaveGrantBatchDetailJoin = leaveGrantRoot
				.join(LeaveGrantBatchEmployeeDetail_.leaveGrantBatchDetail);

		restriction = cb.and(restriction, cb.equal(leaveGrantBatchDetailJoin
				.get(LeaveGrantBatchDetail_.leaveGrantBatchDetailId),
				leaveGrantBatchDetailId));

		criteriaQuery.where(restriction);

		TypedQuery<Integer> leaveGrantTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return leaveGrantTypedQuery.getSingleResult();
	}

	private Path<String> getSortPathForLeaveGrant(SortCondition sortDTO,
			Root<LeaveGrantBatchEmployeeDetail> leaveGrantRoot) {
		Join<LeaveGrantBatchEmployeeDetail, Employee> employeeJoin = leaveGrantRoot
				.join(LeaveGrantBatchEmployeeDetail_.employee);

		List<String> leaveGrantIsColList = new ArrayList<String>();
		leaveGrantIsColList.add(SortConstants.LEAVE_GRANTER_GRANTED_DAYS);

		List<String> employeesIsColList = new ArrayList<String>();
		employeesIsColList.add(SortConstants.LEAVE_GRANTER_EMPLOYEE_NAME);
		employeesIsColList.add(SortConstants.LEAVE_GRANTER_EMPLOYEE_NUMBER);
		employeesIsColList.add(SortConstants.LEAVE_GRANTER_HIRE_DATE);

		Path<String> sortPath = null;

		if (leaveGrantIsColList.contains(sortDTO.getColumnName())) {
			sortPath = leaveGrantRoot.get(colMap
					.get(LeaveGrantBatchEmployeeDetail.class
							+ sortDTO.getColumnName()));
		}
		if (employeesIsColList.contains(sortDTO.getColumnName())) {
			sortPath = employeeJoin.get(colMap.get(Employee.class
					+ sortDTO.getColumnName()));
		}
		return sortPath;
	}

	@Override
	public LeaveGrantBatchEmployeeDetail findLeaveGrantEmpDetailByCompID(Long leaveGrantBatchEmpDetailId,
			Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveGrantBatchEmployeeDetail> criteriaQuery = cb
				.createQuery(LeaveGrantBatchEmployeeDetail.class);
		Root<LeaveGrantBatchEmployeeDetail> leaveGrantBatchRoot = criteriaQuery
				.from(LeaveGrantBatchEmployeeDetail.class);
		criteriaQuery.select(leaveGrantBatchRoot);
		Join<LeaveGrantBatchEmployeeDetail, LeaveGrantBatchDetail> leaveGrantBatchEmpDetailJoin = leaveGrantBatchRoot
				.join(LeaveGrantBatchEmployeeDetail_.leaveGrantBatchDetail);
		Join<LeaveGrantBatchDetail, LeaveGrantBatch> leaveGrantBranchJoin = leaveGrantBatchEmpDetailJoin
				.join(LeaveGrantBatchDetail_.leaveGrantBatch);
		Join<LeaveGrantBatch, Company> leaveGrantBatchCompanyJoin = leaveGrantBranchJoin
				.join(LeaveGrantBatch_.company);
		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(leaveGrantBatchRoot
				.get(LeaveGrantBatchEmployeeDetail_.leaveGrantBatchEmployeeDetailId),
				leaveGrantBatchEmpDetailId));
		restriction = cb.and(restriction, cb.equal(
				leaveGrantBatchCompanyJoin.get(Company_.companyId), companyId));
		criteriaQuery.where(restriction);
		TypedQuery<LeaveGrantBatchEmployeeDetail> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		List<LeaveGrantBatchEmployeeDetail> leaveGrantBatchDetailList = typedQuery
				.getResultList();
		if (leaveGrantBatchDetailList != null
				&& !leaveGrantBatchDetailList.isEmpty()) {
			return leaveGrantBatchDetailList.get(0);
		}
		return null;
	}
}
