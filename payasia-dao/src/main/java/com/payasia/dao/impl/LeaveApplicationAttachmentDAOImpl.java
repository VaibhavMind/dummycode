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
import com.payasia.dao.BaseDAO;
import com.payasia.dao.LeaveApplicationAttachmentDAO;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.Employee_;
import com.payasia.dao.bean.LeaveApplication;
import com.payasia.dao.bean.LeaveApplicationAttachment;
import com.payasia.dao.bean.LeaveApplicationAttachment_;
import com.payasia.dao.bean.LeaveApplicationReviewer;
import com.payasia.dao.bean.LeaveApplicationReviewer_;
import com.payasia.dao.bean.LeaveApplication_;

@Repository
public class LeaveApplicationAttachmentDAOImpl extends BaseDAO implements
		LeaveApplicationAttachmentDAO {

	@Override
	public void save(LeaveApplicationAttachment leaveApplicationAttachment) {
		super.save(leaveApplicationAttachment);
	}

	@Override
	public LeaveApplicationAttachment saveReturn(
			LeaveApplicationAttachment leaveApplicationAttachment) {
		LeaveApplicationAttachment persistObj = leaveApplicationAttachment;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (LeaveApplicationAttachment) getBaseEntity();
			beanUtil.copyProperties(persistObj, leaveApplicationAttachment);
		} catch (IllegalAccessException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		} catch (InvocationTargetException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		}
		this.entityManagerFactory.persist(persistObj);
		return persistObj;
	}

	@Override
	protected Object getBaseEntity() {
		LeaveApplicationAttachment leaveApplicationAttachment = new LeaveApplicationAttachment();
		return leaveApplicationAttachment;
	}

	@Override
	public LeaveApplicationAttachment findById(Long leaveApplicationAttachmentId) {

		return super.findById(LeaveApplicationAttachment.class,
				leaveApplicationAttachmentId);
	}

	@Override
	public void delete(LeaveApplicationAttachment leaveApplicationAttachment) {
		super.delete(leaveApplicationAttachment);
	}
	
	@Override
	public LeaveApplicationAttachment findAttachmentByEmployeeCompanyId(Long attachmentId, Long empId, Long companyId) {
		
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveApplicationAttachment> criteriaQuery = cb
				.createQuery(LeaveApplicationAttachment.class);
		Root<LeaveApplicationAttachment> leaveApplicationItemAttachRoot = criteriaQuery
				.from(LeaveApplicationAttachment.class);
		criteriaQuery.select(leaveApplicationItemAttachRoot);
		Join<LeaveApplicationAttachment, LeaveApplication> leaveAttachJoin = leaveApplicationItemAttachRoot.join(LeaveApplicationAttachment_.leaveApplication);
			
		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(leaveApplicationItemAttachRoot.get(LeaveApplicationAttachment_.leaveApplicationAttachmentId), attachmentId));
		restriction = cb.and(restriction, cb.equal(leaveAttachJoin.get(LeaveApplication_.employee), empId));
		restriction = cb.and(restriction, cb.equal(leaveAttachJoin.get(LeaveApplication_.company), companyId));
		criteriaQuery.where(restriction);
		TypedQuery<LeaveApplicationAttachment> empTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		List<LeaveApplicationAttachment> list = empTypedQuery.getResultList();
		return list!=null && !list.isEmpty()  ? list.get(0) :null;
				
	}

	@Override
	public LeaveApplicationAttachment viewAttachmentByReviewer(Long attachmentId, Long empReviewerId, Long companyId) {
		
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveApplicationAttachment> criteriaQuery = cb.createQuery(LeaveApplicationAttachment.class);
		Root<LeaveApplicationAttachment> empReviewerRoot = criteriaQuery.from(LeaveApplicationAttachment.class);
		criteriaQuery.select(empReviewerRoot);

		Predicate restriction = cb.conjunction();

		Join<LeaveApplicationAttachment, LeaveApplication> empReviewLeaveJoin = empReviewerRoot.join(LeaveApplicationAttachment_.leaveApplication);

		Join<LeaveApplication, LeaveApplicationReviewer> leaveStatusJoin = empReviewLeaveJoin.join(LeaveApplication_.leaveApplicationReviewers);

		Join<LeaveApplicationReviewer, Employee> empStatusJoin = leaveStatusJoin.join(LeaveApplicationReviewer_.employee);
		
		restriction = cb.and(restriction,
				cb.equal(empReviewerRoot.get(LeaveApplicationAttachment_.leaveApplicationAttachmentId), attachmentId));
		
		restriction = cb.and(restriction,
				cb.equal(empStatusJoin.get(Employee_.employeeId), empReviewerId));
		

		criteriaQuery.where(restriction);

		TypedQuery<LeaveApplicationAttachment> leaveAppTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		 
		return leaveAppTypedQuery.getResultList().get(0);
	}

	@Override
	public LeaveApplicationAttachment findAttachmentByEmployeeCompanyId(Long attachmentId, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveApplicationAttachment> criteriaQuery = cb
				.createQuery(LeaveApplicationAttachment.class);
		Root<LeaveApplicationAttachment> leaveApplicationItemAttachRoot = criteriaQuery
				.from(LeaveApplicationAttachment.class);
		criteriaQuery.select(leaveApplicationItemAttachRoot);
		Join<LeaveApplicationAttachment, LeaveApplication> leaveAttachJoin = leaveApplicationItemAttachRoot.join(LeaveApplicationAttachment_.leaveApplication);
			
		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(leaveApplicationItemAttachRoot.get(LeaveApplicationAttachment_.leaveApplicationAttachmentId), attachmentId));
		restriction = cb.and(restriction, cb.equal(leaveAttachJoin.get(LeaveApplication_.company), companyId));
		criteriaQuery.where(restriction);
		TypedQuery<LeaveApplicationAttachment> empTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		List<LeaveApplicationAttachment> list = empTypedQuery.getResultList();
		return list!=null && !list.isEmpty()  ? list.get(0) :null;
	}

}
