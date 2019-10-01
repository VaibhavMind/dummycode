package com.payasia.dao.impl;

import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.LeaveYearEndEmployeeDetailDAO;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.Employee_;
import com.payasia.dao.bean.LeaveYearEndBatch;
import com.payasia.dao.bean.LeaveYearEndBatch_;
import com.payasia.dao.bean.LeaveYearEndEmployeeDetail;
import com.payasia.dao.bean.LeaveYearEndEmployeeDetail_;

@Repository
public class LeaveYearEndEmployeeDetailDAOImpl extends BaseDAO implements
		LeaveYearEndEmployeeDetailDAO {

	@Override
	protected Object getBaseEntity() {
		LeaveYearEndEmployeeDetail leaveYearEndEmployeeDetail = new LeaveYearEndEmployeeDetail();
		return leaveYearEndEmployeeDetail;
	}

	@Override
	public LeaveYearEndEmployeeDetail findByID(long leaveYearEndEmployeeDetailId) {
		return super.findById(LeaveYearEndEmployeeDetail.class,
				leaveYearEndEmployeeDetailId);
	}

	@Override
	public void update(LeaveYearEndEmployeeDetail leaveYearEndEmployeeDetail) {
		super.update(leaveYearEndEmployeeDetail);

	}

	@Override
	public List<LeaveYearEndEmployeeDetail> findByCondition(
			Long leaveYearEndBatchId, String employeeNumber,
			PageRequest pageDTO, SortCondition sortDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveYearEndEmployeeDetail> criteriaQuery = cb
				.createQuery(LeaveYearEndEmployeeDetail.class);
		Root<LeaveYearEndEmployeeDetail> leaveYearEndRoot = criteriaQuery
				.from(LeaveYearEndEmployeeDetail.class);
		criteriaQuery.select(leaveYearEndRoot);

		Predicate restriction = cb.conjunction();

		Join<LeaveYearEndEmployeeDetail, LeaveYearEndBatch> leaveYearEndBatchJoin = leaveYearEndRoot
				.join(LeaveYearEndEmployeeDetail_.leaveYearEndBatch);
		Join<LeaveYearEndEmployeeDetail, Employee> employeeJoin = leaveYearEndRoot
				.join(LeaveYearEndEmployeeDetail_.employee);

		restriction = cb.and(restriction, cb.equal(leaveYearEndBatchJoin
				.get(LeaveYearEndBatch_.leaveYearEndBatchId),
				leaveYearEndBatchId));

		restriction = cb.and(restriction, cb.isNull(leaveYearEndRoot
				.get(LeaveYearEndEmployeeDetail_.deletedDate)));
		if (StringUtils.isNotBlank(employeeNumber)) {
			restriction = cb.and(restriction, cb.like(
					cb.upper(employeeJoin.get(Employee_.employeeNumber)),
					employeeNumber.toUpperCase() + '%'));
		}

		criteriaQuery.where(restriction);

		TypedQuery<LeaveYearEndEmployeeDetail> leaveYearEndTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			leaveYearEndTypedQuery.setFirstResult(getStartPosition(pageDTO));
			leaveYearEndTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		return leaveYearEndTypedQuery.getResultList();
	}

	@Override
	public Integer getCountByCondition(Long leaveYearEndBatchId,
			String employeeNumber) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Integer> criteriaQuery = cb.createQuery(Integer.class);
		Root<LeaveYearEndEmployeeDetail> leaveYearEndRoot = criteriaQuery
				.from(LeaveYearEndEmployeeDetail.class);
		criteriaQuery.select(cb.count(leaveYearEndRoot).as(Integer.class));

		Predicate restriction = cb.conjunction();

		Join<LeaveYearEndEmployeeDetail, LeaveYearEndBatch> leaveYearEndBatchJoin = leaveYearEndRoot
				.join(LeaveYearEndEmployeeDetail_.leaveYearEndBatch);
		Join<LeaveYearEndEmployeeDetail, Employee> employeeJoin = leaveYearEndRoot
				.join(LeaveYearEndEmployeeDetail_.employee);

		restriction = cb.and(restriction, cb.equal(leaveYearEndBatchJoin
				.get(LeaveYearEndBatch_.leaveYearEndBatchId),
				leaveYearEndBatchId));

		restriction = cb.and(restriction, cb.isNull(leaveYearEndRoot
				.get(LeaveYearEndEmployeeDetail_.deletedDate)));
		if (StringUtils.isNotBlank(employeeNumber)) {
			restriction = cb
					.and(restriction, cb.equal(
							employeeJoin.get(Employee_.employeeNumber),
							employeeNumber));
		}

		criteriaQuery.where(restriction);

		TypedQuery<Integer> leaveYearEndTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return leaveYearEndTypedQuery.getSingleResult();
	}

}
