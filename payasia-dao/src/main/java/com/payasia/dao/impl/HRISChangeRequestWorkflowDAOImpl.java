package com.payasia.dao.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.util.NullAwareBeanUtilsBean;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.HRISChangeRequestWorkflowDAO;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.Employee_;
import com.payasia.dao.bean.HRISChangeRequest;
import com.payasia.dao.bean.HRISChangeRequestWorkflow;
import com.payasia.dao.bean.HRISChangeRequestWorkflow_;
import com.payasia.dao.bean.HRISChangeRequest_;
import com.payasia.dao.bean.HRISStatusMaster;
import com.payasia.dao.bean.HRISStatusMaster_;

@Repository
public class HRISChangeRequestWorkflowDAOImpl extends BaseDAO implements
		HRISChangeRequestWorkflowDAO {

	@Override
	protected Object getBaseEntity() {
		HRISChangeRequestWorkflow hrisChangeRequestWorkflow = new HRISChangeRequestWorkflow();
		return hrisChangeRequestWorkflow;
	}

	@Override
	public void update(HRISChangeRequestWorkflow hrisChangeRequestWorkflow) {
		this.entityManagerFactory.merge(hrisChangeRequestWorkflow);
	}

	@Override
	public void delete(HRISChangeRequestWorkflow hrisChangeRequestWorkflow) {
		this.entityManagerFactory.remove(hrisChangeRequestWorkflow);
	}

	@Override
	public void save(HRISChangeRequestWorkflow hrisChangeRequestWorkflow) {
		super.save(hrisChangeRequestWorkflow);
	}

	@Override
	public HRISChangeRequestWorkflow findById(Long hrisChangeRequestWorkflowId) {
		return super.findById(HRISChangeRequestWorkflow.class,
				hrisChangeRequestWorkflowId);

	}

	@Override
	public HRISChangeRequestWorkflow savePersist(
			HRISChangeRequestWorkflow hrisChangeRequestWorkflow) {
		HRISChangeRequestWorkflow persistObj = hrisChangeRequestWorkflow;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (HRISChangeRequestWorkflow) getBaseEntity();
			beanUtil.copyProperties(persistObj, hrisChangeRequestWorkflow);
		} catch (IllegalAccessException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		} catch (InvocationTargetException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		}
		this.entityManagerFactory.persist(persistObj);
		return persistObj;
	}

	@Override
	public HRISChangeRequestWorkflow findByCondition(Long hrisChangeReqId,
			Long createdById, List<String> hrisStatusList) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<HRISChangeRequestWorkflow> criteriaQuery = cb
				.createQuery(HRISChangeRequestWorkflow.class);
		Root<HRISChangeRequestWorkflow> root = criteriaQuery
				.from(HRISChangeRequestWorkflow.class);
		criteriaQuery.select(root);

		Predicate restriction = cb.conjunction();
		Join<HRISChangeRequestWorkflow, HRISChangeRequest> changeReqJoin = root
				.join(HRISChangeRequestWorkflow_.hrisChangeRequest);

		Join<HRISChangeRequestWorkflow, HRISStatusMaster> statusJoin = root
				.join(HRISChangeRequestWorkflow_.hrisStatusMaster);

		if (createdById != null) {
			Join<HRISChangeRequestWorkflow, Employee> empJoin = root
					.join(HRISChangeRequestWorkflow_.employee);

			restriction = cb.and(restriction,
					cb.equal(empJoin.get(Employee_.employeeId), createdById));
		}

		restriction = cb.and(restriction, cb.equal(
				changeReqJoin.get(HRISChangeRequest_.hrisChangeRequestId),
				hrisChangeReqId));
		if ( !hrisStatusList.isEmpty()) {
			restriction = cb.and(
					restriction,
					statusJoin.get(HRISStatusMaster_.hrisStatusName).in(
							hrisStatusList));
		} else {

			restriction = cb.and(restriction, cb.notEqual(
					statusJoin.get(HRISStatusMaster_.hrisStatusName),
					PayAsiaConstants.HRIS_STATUS_SUBMITTED));
		}
		criteriaQuery.where(restriction);

		TypedQuery<HRISChangeRequestWorkflow> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<HRISChangeRequestWorkflow> changeRequestList = typedQuery
				.getResultList();
		if (changeRequestList != null &&  !changeRequestList.isEmpty()) {
			return changeRequestList.get(0);
		}
		return null;
	}

	@Override
	public HRISChangeRequestWorkflow findByCondition(Long hrisChangeReqId,
			Long createdById) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<HRISChangeRequestWorkflow> criteriaQuery = cb
				.createQuery(HRISChangeRequestWorkflow.class);
		Root<HRISChangeRequestWorkflow> root = criteriaQuery
				.from(HRISChangeRequestWorkflow.class);
		criteriaQuery.select(root);

		Predicate restriction = cb.conjunction();
		Join<HRISChangeRequestWorkflow, HRISChangeRequest> changeReqJoin = root
				.join(HRISChangeRequestWorkflow_.hrisChangeRequest);

		Join<HRISChangeRequestWorkflow, HRISStatusMaster> statusJoin = root
				.join(HRISChangeRequestWorkflow_.hrisStatusMaster);

		if (createdById != null) {
			Join<HRISChangeRequestWorkflow, Employee> empJoin = root
					.join(HRISChangeRequestWorkflow_.employee);

			restriction = cb.and(restriction,
					cb.equal(empJoin.get(Employee_.employeeId), createdById));
		}

		restriction = cb.and(restriction, cb.equal(
				changeReqJoin.get(HRISChangeRequest_.hrisChangeRequestId),
				hrisChangeReqId));

		restriction = cb.and(restriction, cb.equal(
				statusJoin.get(HRISStatusMaster_.hrisStatusName),
				PayAsiaConstants.HRIS_STATUS_SUBMITTED));

		criteriaQuery.where(restriction);

		TypedQuery<HRISChangeRequestWorkflow> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<HRISChangeRequestWorkflow> changeRequestList = typedQuery
				.getResultList();
		if (changeRequestList != null &&  !changeRequestList.isEmpty()) {
			return changeRequestList.get(0);
		}
		return null;
	}

	@Override
	public HRISChangeRequestWorkflow findByConditionChangeRequest(
			Long hrisChangeReqId, Long createdById) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<HRISChangeRequestWorkflow> criteriaQuery = cb
				.createQuery(HRISChangeRequestWorkflow.class);
		Root<HRISChangeRequestWorkflow> root = criteriaQuery
				.from(HRISChangeRequestWorkflow.class);
		criteriaQuery.select(root);

		Predicate restriction = cb.conjunction();
		Join<HRISChangeRequestWorkflow, HRISChangeRequest> changeReqJoin = root
				.join(HRISChangeRequestWorkflow_.hrisChangeRequest);

		root.join(HRISChangeRequestWorkflow_.hrisStatusMaster);

		if (createdById != null) {
			Join<HRISChangeRequestWorkflow, Employee> empJoin = root
					.join(HRISChangeRequestWorkflow_.employee);

			restriction = cb.and(restriction,
					cb.equal(empJoin.get(Employee_.employeeId), createdById));
		}

		restriction = cb.and(restriction, cb.equal(
				changeReqJoin.get(HRISChangeRequest_.hrisChangeRequestId),
				hrisChangeReqId));

		criteriaQuery.where(restriction);

		TypedQuery<HRISChangeRequestWorkflow> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<HRISChangeRequestWorkflow> changeRequestList = typedQuery
				.getResultList();
		if (changeRequestList != null &&  !changeRequestList.isEmpty()) {
			return changeRequestList.get(0);
		}
		return null;
	}

}
