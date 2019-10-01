package com.payasia.dao.impl;

import java.lang.reflect.InvocationTargetException;
import java.sql.Date;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.payasia.common.dto.PendingLeaveDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.NullAwareBeanUtilsBean;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.SortConstants;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.LeaveApplicationReviewerDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.Employee_;
import com.payasia.dao.bean.LeaveApplication;
import com.payasia.dao.bean.LeaveApplicationReviewer;
import com.payasia.dao.bean.LeaveApplicationReviewer_;
import com.payasia.dao.bean.LeaveApplication_;
import com.payasia.dao.bean.LeaveStatusMaster;
import com.payasia.dao.bean.LeaveStatusMaster_;
import com.payasia.dao.bean.WorkFlowRuleMaster;
import com.payasia.dao.bean.WorkFlowRuleMaster_;

@Repository
public class LeaveApplicationReviewerDAOImpl extends BaseDAO implements
		LeaveApplicationReviewerDAO {

	@Override
	public void save(LeaveApplicationReviewer leaveApplicationReviewer) {
		super.save(leaveApplicationReviewer);
	}

	@Override
	public void update(LeaveApplicationReviewer leaveApplicationReviewer) {
		super.update(leaveApplicationReviewer);
	}

	@Override
	public LeaveApplicationReviewer findById(Long reviewId) {
		return super.findById(LeaveApplicationReviewer.class, reviewId);
	}

	@Override
	public LeaveApplicationReviewer saveReturn(
			LeaveApplicationReviewer leaveApplicationReviewer) {
		LeaveApplicationReviewer persistObj = leaveApplicationReviewer;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (LeaveApplicationReviewer) getBaseEntity();
			beanUtil.copyProperties(persistObj, leaveApplicationReviewer);
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
		LeaveApplicationReviewer leaveApplicationReviewer = new LeaveApplicationReviewer();
		return leaveApplicationReviewer;
	}

	@Override
	public List<LeaveApplicationReviewer> findByCondition(Long empId,
			PageRequest pageDTO, SortCondition sortDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveApplicationReviewer> criteriaQuery = cb
				.createQuery(LeaveApplicationReviewer.class);
		Root<LeaveApplicationReviewer> empRoot = criteriaQuery
				.from(LeaveApplicationReviewer.class);
		criteriaQuery.select(empRoot);

		Predicate restriction = cb.conjunction();

		Join<LeaveApplicationReviewer, Employee> empLeaveJoin = empRoot
				.join(LeaveApplicationReviewer_.employee);
		Join<LeaveApplicationReviewer, LeaveApplication> leaveAppJoin = empRoot
				.join(LeaveApplicationReviewer_.leaveApplication);

		restriction = cb.and(restriction,
				cb.equal(empLeaveJoin.get(Employee_.employeeId), empId));

		restriction = cb.and(restriction,
				cb.equal(empRoot.get(LeaveApplicationReviewer_.pending), true));

		criteriaQuery.where(restriction);

		criteriaQuery.orderBy(cb.desc(leaveAppJoin
				.get(LeaveApplication_.updatedDate)));
		TypedQuery<LeaveApplicationReviewer> leaveAppTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			leaveAppTypedQuery.setFirstResult(getStartPosition(pageDTO));
			leaveAppTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		return leaveAppTypedQuery.getResultList();
	}

	@Override
	public Long getCountForCondition(Long employeeId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
		Root<LeaveApplicationReviewer> empRoot = criteriaQuery
				.from(LeaveApplicationReviewer.class);
		criteriaQuery.select(cb.count(empRoot));

		Predicate restriction = cb.conjunction();

		Join<LeaveApplicationReviewer, Employee> empLeaveJoin = empRoot
				.join(LeaveApplicationReviewer_.employee);

		if (employeeId != null) {
			restriction = cb.and(restriction, cb.equal(
					empLeaveJoin.get(Employee_.employeeId), employeeId));
		}

		restriction = cb.and(restriction,
				cb.equal(empRoot.get(LeaveApplicationReviewer_.pending), true));

		criteriaQuery.where(restriction);

		TypedQuery<Long> leaveAppTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return leaveAppTypedQuery.getSingleResult();
	}

	@Override
	public Long getCountForConditionForAdmin(Long employeeId, Long companyId,
			PendingLeaveDTO pendingLeaveDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
		Root<LeaveApplicationReviewer> empRoot = criteriaQuery
				.from(LeaveApplicationReviewer.class);
		criteriaQuery.select(cb.count(empRoot));

		Predicate restriction = cb.conjunction();

		Join<LeaveApplicationReviewer, Employee> empLeaveJoin = empRoot
				.join(LeaveApplicationReviewer_.employee);
		Join<LeaveApplicationReviewer, LeaveApplication> leaveApplicationJoin = empRoot
				.join(LeaveApplicationReviewer_.leaveApplication);
		Join<LeaveApplication, Company> leaveApplicationCompJoin = leaveApplicationJoin
				.join(LeaveApplication_.company);
		if (employeeId != null) {
			restriction = cb.and(restriction, cb.equal(
					empLeaveJoin.get(Employee_.employeeId), employeeId));
		}
		restriction = cb.and(restriction, cb.equal(
				leaveApplicationCompJoin.get(Company_.companyId), companyId));
		restriction = cb.and(restriction,
				cb.equal(empRoot.get(LeaveApplicationReviewer_.pending), true));

		if (pendingLeaveDTO.getLeaveType().equals(
				PayAsiaConstants.WORK_FLOW_TYPE_LEAVE)) {
			restriction = cb.and(restriction, cb.isNull(leaveApplicationJoin
					.get(LeaveApplication_.leaveCancelApplication)));
		} else if (pendingLeaveDTO.getLeaveType().equals(
				PayAsiaConstants.WORK_FLOW_TYPE_LEAVE_CANCEL)) {
			restriction = cb.and(restriction, cb.isNotNull(leaveApplicationJoin
					.get(LeaveApplication_.leaveCancelApplication)));
		}

		criteriaQuery.where(restriction);

		TypedQuery<Long> leaveAppTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return leaveAppTypedQuery.getSingleResult();
	}

	@Override
	public List<LeaveApplicationReviewer> findByConditionLeaveType(Long empId,
			PageRequest pageDTO, SortCondition sortDTO,
			PendingLeaveDTO pendingLeaveDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveApplicationReviewer> criteriaQuery = cb
				.createQuery(LeaveApplicationReviewer.class);
		Root<LeaveApplicationReviewer> empRoot = criteriaQuery
				.from(LeaveApplicationReviewer.class);
		criteriaQuery.select(empRoot);

		Predicate restriction = cb.conjunction();

		Join<LeaveApplicationReviewer, Employee> empLeaveJoin = empRoot
				.join(LeaveApplicationReviewer_.employee);

		Join<LeaveApplicationReviewer, LeaveApplication> leaveApplicationJoin = empRoot
				.join(LeaveApplicationReviewer_.leaveApplication);

		Join<LeaveApplication, LeaveStatusMaster> leaveStatusJoin = leaveApplicationJoin
				.join(LeaveApplication_.leaveStatusMaster);
		if (empId != null) {
			restriction = cb.and(restriction,
					cb.equal(empLeaveJoin.get(Employee_.employeeId), empId));
		}

		restriction = cb.and(restriction,
				cb.equal(empRoot.get(LeaveApplicationReviewer_.pending), true));

		if (pendingLeaveDTO.getLeaveType().equals(
				PayAsiaConstants.WORK_FLOW_TYPE_LEAVE)) {
			restriction = cb.and(restriction, cb.isNull(leaveApplicationJoin
					.get(LeaveApplication_.leaveCancelApplication)));
		} else if (pendingLeaveDTO.getLeaveType().equals(
				PayAsiaConstants.WORK_FLOW_TYPE_LEAVE_CANCEL)) {
			restriction = cb.and(restriction, cb.isNotNull(leaveApplicationJoin
					.get(LeaveApplication_.leaveCancelApplication)));
		}

		if (StringUtils.isNotBlank(pendingLeaveDTO.getStatus())) {

			restriction = cb.and(restriction, cb.like(
					leaveStatusJoin.get(LeaveStatusMaster_.leaveStatusName).as(
							String.class), pendingLeaveDTO.getStatus()));
		}

		if (StringUtils.isNotBlank(pendingLeaveDTO.getPendingEmployeeName())) {

			restriction = cb.and(
					restriction,
					cb.like(leaveApplicationJoin
							.get(LeaveApplication_.employee).get("firstName")
							.as(String.class),
							pendingLeaveDTO.getPendingEmployeeName()));
		}

		if (StringUtils.isNotBlank(pendingLeaveDTO.getCreatedDate())) {

			restriction = cb.and(restriction, cb.equal(leaveApplicationJoin
					.get(LeaveApplication_.createdDate).as(Date.class),
					DateUtils.stringToDate(pendingLeaveDTO.getCreatedDate())));
		}

		if (StringUtils.isNotBlank(pendingLeaveDTO.getFromDate())) {

			restriction = cb.and(restriction, cb.equal(leaveApplicationJoin
					.get(LeaveApplication_.startDate).as(Date.class), DateUtils
					.stringToDate(pendingLeaveDTO.getFromDate())));
		}

		if (StringUtils.isNotBlank(pendingLeaveDTO.getToDate())) {

			restriction = cb.and(restriction, cb.equal(leaveApplicationJoin
					.get(LeaveApplication_.endDate).as(Date.class), DateUtils
					.stringToDate(pendingLeaveDTO.getToDate())));
		}

		criteriaQuery.orderBy(cb.desc(leaveApplicationJoin
				.get(LeaveApplication_.createdDate)));
	
		criteriaQuery.where(restriction);

		TypedQuery<LeaveApplicationReviewer> leaveAppTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			leaveAppTypedQuery.setFirstResult(getStartPosition(pageDTO));
			leaveAppTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		return leaveAppTypedQuery.getResultList();
	}

	@Override
	public List<LeaveApplicationReviewer> findByConditionLeaveTypeForAdmin(
			Long empId, Long companyId, PageRequest pageDTO,
			SortCondition sortDTO, List<String> leaveStatusNames,
			PendingLeaveDTO pendingLeaveDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveApplicationReviewer> criteriaQuery = cb
				.createQuery(LeaveApplicationReviewer.class);
		Root<LeaveApplicationReviewer> empRoot = criteriaQuery
				.from(LeaveApplicationReviewer.class);
		criteriaQuery.select(empRoot);

		Predicate restriction = cb.conjunction();

		Join<LeaveApplicationReviewer, Employee> empLeaveJoin = empRoot
				.join(LeaveApplicationReviewer_.employee);

		Join<LeaveApplicationReviewer, LeaveApplication> leaveApplicationJoin = empRoot
				.join(LeaveApplicationReviewer_.leaveApplication);
		Join<LeaveApplication, Company> leaveApplicationCompJoin = leaveApplicationJoin
				.join(LeaveApplication_.company);

		Join<LeaveApplication, LeaveStatusMaster> leaveStatusJoin = leaveApplicationJoin
				.join(LeaveApplication_.leaveStatusMaster);
		Join<LeaveApplicationReviewer, WorkFlowRuleMaster> workFlowRuleMasterJoin = empRoot
				.join(LeaveApplicationReviewer_.workFlowRuleMaster);
		if (empId != null) {
			restriction = cb.and(restriction,
					cb.equal(empLeaveJoin.get(Employee_.employeeId), empId));
		}

		restriction = cb.and(restriction,
				cb.equal(empRoot.get(LeaveApplicationReviewer_.pending), true));
		restriction = cb.and(restriction, cb.equal(
				leaveApplicationCompJoin.get(Company_.companyId), companyId));

		if (pendingLeaveDTO.getLeaveType().equals(
				PayAsiaConstants.WORK_FLOW_TYPE_LEAVE)) {
			restriction = cb.and(restriction, cb.isNull(leaveApplicationJoin
					.get(LeaveApplication_.leaveCancelApplication)));
		} else if (pendingLeaveDTO.getLeaveType().equals(
				PayAsiaConstants.WORK_FLOW_TYPE_LEAVE_CANCEL)) {
			restriction = cb.and(restriction, cb.isNotNull(leaveApplicationJoin
					.get(LeaveApplication_.leaveCancelApplication)));
		}

		restriction = cb.and(
				restriction,
				leaveStatusJoin.get(LeaveStatusMaster_.leaveStatusName).in(
						leaveStatusNames));

		if (StringUtils.isNotBlank(pendingLeaveDTO.getPendingEmployeeName())) {

			restriction = cb.and(
					restriction,
					cb.like(leaveApplicationJoin
							.get(LeaveApplication_.employee).get("firstName")
							.as(String.class),
							pendingLeaveDTO.getPendingEmployeeName()));
		}

		if (StringUtils.isNotBlank(pendingLeaveDTO.getCreatedDate())) {

			restriction = cb.and(restriction, cb.equal(leaveApplicationJoin
					.get(LeaveApplication_.createdDate).as(Date.class),
					DateUtils.stringToDate(pendingLeaveDTO.getCreatedDate())));
		}

		if (StringUtils.isNotBlank(pendingLeaveDTO.getFromDate())) {

			restriction = cb.and(restriction, cb.equal(leaveApplicationJoin
					.get(LeaveApplication_.startDate).as(Date.class), DateUtils
					.stringToDate(pendingLeaveDTO.getFromDate())));
		}

		if (StringUtils.isNotBlank(pendingLeaveDTO.getToDate())) {

			restriction = cb.and(restriction, cb.equal(leaveApplicationJoin
					.get(LeaveApplication_.endDate).as(Date.class), DateUtils
					.stringToDate(pendingLeaveDTO.getToDate())));
		}

		criteriaQuery.where(restriction);

		if (sortDTO != null) {

			Path<String> sortPath = getSortPathForLeave(sortDTO, empRoot);
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
		criteriaQuery.orderBy(cb.desc(workFlowRuleMasterJoin
				.get(WorkFlowRuleMaster_.workFlowRuleId)));
		TypedQuery<LeaveApplicationReviewer> leaveAppTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			leaveAppTypedQuery.setFirstResult(getStartPosition(pageDTO));
			leaveAppTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		return leaveAppTypedQuery.getResultList();
	}

	private Path<String> getSortPathForLeave(SortCondition sortDTO,
			Root<LeaveApplicationReviewer> empRoot) {

		return null;
	}

	@Override
	public List<LeaveApplicationReviewer> checkEmployeeReviewer(
			Long employeeId, List<String> leaveStatusList) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveApplicationReviewer> criteriaQuery = cb
				.createQuery(LeaveApplicationReviewer.class);
		Root<LeaveApplicationReviewer> leaveAppRevRoot = criteriaQuery
				.from(LeaveApplicationReviewer.class);
		criteriaQuery.select(leaveAppRevRoot);

		Predicate restriction = cb.conjunction();

		Join<LeaveApplicationReviewer, Employee> empLeaveJoin = leaveAppRevRoot
				.join(LeaveApplicationReviewer_.employee);

		Join<LeaveApplicationReviewer, LeaveApplication> leaveAppJoin = leaveAppRevRoot
				.join(LeaveApplicationReviewer_.leaveApplication);
		Join<LeaveApplication, LeaveStatusMaster> leaveStatusJoin = leaveAppJoin
				.join(LeaveApplication_.leaveStatusMaster);

		restriction = cb.and(restriction,
				cb.equal(empLeaveJoin.get(Employee_.employeeId), employeeId));
		restriction = cb.and(
				restriction,
				leaveStatusJoin.get(LeaveStatusMaster_.leaveStatusName).in(
						leaveStatusList));
		criteriaQuery.where(restriction);

		TypedQuery<LeaveApplicationReviewer> leaveAppTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		if (leaveAppTypedQuery.getResultList().size() > 0) {
			return leaveAppTypedQuery.getResultList();
		} else {
			return null;
		}
	}

	@Override
	public void deleteByCondition(Long leaveApplicationId) {

		String queryString = "DELETE FROM LeaveApplicationReviewer lea WHERE  lea.leaveApplication.leaveApplicationId = :leaveApplicationId ";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("leaveApplicationId", leaveApplicationId);

		q.executeUpdate();
	}

	@Override
	public Integer getLeaveReviewerCount(Long leaveApplicationId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Integer> criteriaQuery = cb.createQuery(Integer.class);
		Root<LeaveApplicationReviewer> leaveApplicationReviewerRoot = criteriaQuery
				.from(LeaveApplicationReviewer.class);
		criteriaQuery.select(cb.count(leaveApplicationReviewerRoot).as(
				Integer.class));

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(
				leaveApplicationReviewerRoot
						.get(LeaveApplicationReviewer_.leaveApplication)
						.get("leaveApplicationId").as(Long.class),
				leaveApplicationId));

		criteriaQuery.where(restriction);

		TypedQuery<Integer> empTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return empTypedQuery.getSingleResult();

	}

	@Override
	public List<LeaveApplicationReviewer> getPendingLeaveApplicationByIds(
			Long empId, List<Long> leaveApplicationRevIdsList) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveApplicationReviewer> criteriaQuery = cb
				.createQuery(LeaveApplicationReviewer.class);
		Root<LeaveApplicationReviewer> leaveAppRevRoot = criteriaQuery
				.from(LeaveApplicationReviewer.class);
		criteriaQuery.select(leaveAppRevRoot);

		Predicate restriction = cb.conjunction();

		Join<LeaveApplicationReviewer, Employee> empLeaveJoin = leaveAppRevRoot
				.join(LeaveApplicationReviewer_.employee);

		if (empId != null) {
			restriction = cb.and(restriction,
					cb.equal(empLeaveJoin.get(Employee_.employeeId), empId));
		}

		restriction = cb.and(restriction, cb.equal(
				leaveAppRevRoot.get(LeaveApplicationReviewer_.pending), true));
		restriction = cb.and(
				restriction,
				leaveAppRevRoot.get(
						LeaveApplicationReviewer_.leaveApplicationReviewerId)
						.in(leaveApplicationRevIdsList));

		criteriaQuery.where(restriction);

		TypedQuery<LeaveApplicationReviewer> leaveAppTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return leaveAppTypedQuery.getResultList();
	}
	
	@Override
	public LeaveApplicationReviewer getLeaveApplicationReviewerDetail(Long leaveApplicationReviewerId,
			Long employeeId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveApplicationReviewer> criteriaQuery = cb.createQuery(LeaveApplicationReviewer.class);
		Root<LeaveApplicationReviewer> leaveApplicationReviewerRoot = criteriaQuery
				.from(LeaveApplicationReviewer.class);

		Join<LeaveApplicationReviewer, Employee> empClaimJoin = leaveApplicationReviewerRoot
				.join(LeaveApplicationReviewer_.employee);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(leaveApplicationReviewerRoot.get(LeaveApplicationReviewer_.leaveApplicationReviewerId),
						leaveApplicationReviewerId));
		restriction = cb.and(restriction,
				cb.equal(leaveApplicationReviewerRoot.get(LeaveApplicationReviewer_.pending), true));
		restriction = cb.and(restriction, cb.equal(empClaimJoin.get(Employee_.employeeId), employeeId));

		criteriaQuery.where(restriction);

		TypedQuery<LeaveApplicationReviewer> empTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		
		
		if(!empTypedQuery.getResultList().isEmpty()){
			
		   return empTypedQuery.getResultList().get(0);
		}
		
		return null; 

	}
	@Override
	public boolean checkLeaveReviewers(LeaveApplication leaveApplication,Long leaveReviewerId)
	{
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveApplicationReviewer> criteriaQuery = cb.createQuery(LeaveApplicationReviewer.class);
		Root<LeaveApplicationReviewer> leaveApplicationReviewerRoot = criteriaQuery
				.from(LeaveApplicationReviewer.class);

		//Join<LeaveApplicationReviewer,LeaveApplication> leaveReviewerJoin = leaveApplicationReviewerRoot.join(LeaveApplicationReviewer_.leaveApplication);
		Join<LeaveApplicationReviewer, Employee> empClaimJoin = leaveApplicationReviewerRoot
				.join(LeaveApplicationReviewer_.employee);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(leaveApplicationReviewerRoot.get(LeaveApplicationReviewer_.leaveApplication),
						leaveApplication.getLeaveApplicationId()));
		
		restriction = cb.and(restriction, cb.equal(empClaimJoin.get(Employee_.employeeId), leaveReviewerId));
		criteriaQuery.where(restriction);

		TypedQuery<LeaveApplicationReviewer> empTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		
		
		if(!empTypedQuery.getResultList().isEmpty()){
			
		   return true;
		}
		
		return false; 
	}

}
