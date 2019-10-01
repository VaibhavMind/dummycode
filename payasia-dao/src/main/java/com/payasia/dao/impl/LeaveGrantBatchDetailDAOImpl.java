package com.payasia.dao.impl;

import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.payasia.dao.BaseDAO;
import com.payasia.dao.LeaveGrantBatchDetailDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.LeaveGrantBatch;
import com.payasia.dao.bean.LeaveGrantBatchDetail;
import com.payasia.dao.bean.LeaveGrantBatchDetail_;
import com.payasia.dao.bean.LeaveGrantBatch_;

@Repository
public class LeaveGrantBatchDetailDAOImpl extends BaseDAO implements
		LeaveGrantBatchDetailDAO {

	@Override
	protected Object getBaseEntity() {
		LeaveGrantBatchDetail leaveGrantBatchDetail = new LeaveGrantBatchDetail();
		return leaveGrantBatchDetail;
	}

	@Override
	public void update(LeaveGrantBatchDetail leaveGrantBatchDetail) {
		super.update(leaveGrantBatchDetail);
	}

	@Override
	public void delete(LeaveGrantBatchDetail leaveGrantBatchDetail) {
		super.delete(leaveGrantBatchDetail);
	}

	@Override
	public void save(LeaveGrantBatchDetail leaveGrantBatchDetail) {
		super.save(leaveGrantBatchDetail);
	}

	@Override
	public LeaveGrantBatchDetail findByID(Long leaveGrantBatchDetailId) {
		return super.findById(LeaveGrantBatchDetail.class,
				leaveGrantBatchDetailId);
	}

	@Override
	public LeaveGrantBatchDetail findLeaveGrantBranchDetailByCompID(Long leaveGrantBatchDetailId, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveGrantBatchDetail> criteriaQuery = cb
				.createQuery(LeaveGrantBatchDetail.class);
		Root<LeaveGrantBatchDetail> leaveBranchRoot = criteriaQuery
				.from(LeaveGrantBatchDetail.class);
		criteriaQuery.select(leaveBranchRoot);
		Join<LeaveGrantBatchDetail, LeaveGrantBatch> leaveGrantBranchJoin = leaveBranchRoot
				.join(LeaveGrantBatchDetail_.leaveGrantBatch);
		Join<LeaveGrantBatch, Company> leaveGrantBatchCompanyJoin = leaveGrantBranchJoin
				.join(LeaveGrantBatch_.company);
		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(leaveBranchRoot
				.get(LeaveGrantBatchDetail_.leaveGrantBatchDetailId),
				leaveGrantBatchDetailId));
		restriction = cb.and(restriction, cb.equal(
				leaveGrantBatchCompanyJoin.get(Company_.companyId), companyId));
		criteriaQuery.where(restriction);
		TypedQuery<LeaveGrantBatchDetail> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<LeaveGrantBatchDetail> leaveGrantBatchDetailList = typedQuery
				.getResultList();
		if (leaveGrantBatchDetailList != null
				&& !leaveGrantBatchDetailList.isEmpty()) {
			return leaveGrantBatchDetailList.get(0);
		}
		return null;
	}

}
