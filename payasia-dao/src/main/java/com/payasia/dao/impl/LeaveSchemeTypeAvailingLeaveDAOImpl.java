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
import com.payasia.dao.LeaveSchemeTypeAvailingLeaveDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.EmployeeLeaveSchemeType;
import com.payasia.dao.bean.EmployeeLeaveSchemeType_;
import com.payasia.dao.bean.LeaveApplication;
import com.payasia.dao.bean.LeaveApplication_;
import com.payasia.dao.bean.LeaveScheme;
import com.payasia.dao.bean.LeaveSchemeType;
import com.payasia.dao.bean.LeaveSchemeTypeAvailingLeave;
import com.payasia.dao.bean.LeaveSchemeTypeAvailingLeave_;
import com.payasia.dao.bean.LeaveSchemeType_;
import com.payasia.dao.bean.LeaveScheme_;
import com.payasia.dao.bean.LeaveTypeMaster;
import com.payasia.dao.bean.LeaveTypeMaster_;

@Repository
public class LeaveSchemeTypeAvailingLeaveDAOImpl extends BaseDAO implements LeaveSchemeTypeAvailingLeaveDAO {

	@Override
	protected Object getBaseEntity() {
		LeaveSchemeTypeAvailingLeave leaveSchemeTypeAvailingLeave = new LeaveSchemeTypeAvailingLeave();
		return leaveSchemeTypeAvailingLeave;
	}

	@Override
	public LeaveSchemeTypeAvailingLeave findById(Long leaveSchemeTypeAvailingLeaveId) {
		LeaveSchemeTypeAvailingLeave leaveSchemeTypeAvailingLeave = super.findById(LeaveSchemeTypeAvailingLeave.class, leaveSchemeTypeAvailingLeaveId);
		return leaveSchemeTypeAvailingLeave;
	}

	@Override
	public void update(LeaveSchemeTypeAvailingLeave leaveSchemeTypeAvailingLeave) {
		super.update(leaveSchemeTypeAvailingLeave);
	}

	@Override
	public void save(LeaveSchemeTypeAvailingLeave leaveSchemeTypeAvailingLeave) {
		super.save(leaveSchemeTypeAvailingLeave);

	}

	@Override
	public void delete(LeaveSchemeTypeAvailingLeave leaveSchemeTypeAvailingLeave) {
		super.delete(leaveSchemeTypeAvailingLeave);
	}

	@Override
	public LeaveSchemeTypeAvailingLeave saveObj(LeaveSchemeTypeAvailingLeave availingLeave) {
		LeaveSchemeTypeAvailingLeave persistObj = availingLeave;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (LeaveSchemeTypeAvailingLeave) getBaseEntity();
			beanUtil.copyProperties(persistObj, availingLeave);
		} catch (IllegalAccessException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		} catch (InvocationTargetException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		}
		this.entityManagerFactory.persist(persistObj);
		return persistObj;
	}

	@Override
	public LeaveSchemeTypeAvailingLeave findByLeaveSchemeType(Long leaveSchemeTypeId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveSchemeTypeAvailingLeave> criteriaQuery = cb.createQuery(LeaveSchemeTypeAvailingLeave.class);
		Root<LeaveSchemeTypeAvailingLeave> root = criteriaQuery.from(LeaveSchemeTypeAvailingLeave.class);

		criteriaQuery.select(root);

		Join<LeaveSchemeTypeAvailingLeave, LeaveSchemeType> leaveSchemeTypejoin = root.join(LeaveSchemeTypeAvailingLeave_.leaveSchemeType);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(leaveSchemeTypejoin.get(LeaveSchemeType_.leaveSchemeTypeId), leaveSchemeTypeId));
		criteriaQuery.where(restriction);

		TypedQuery<LeaveSchemeTypeAvailingLeave> typedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<LeaveSchemeTypeAvailingLeave> leaveSchemeTypeList = typedQuery.getResultList();
		if (leaveSchemeTypeList != null && !leaveSchemeTypeList.isEmpty()) {
			return leaveSchemeTypeList.get(0);
		}
		return null;

	}

	@Override
	public List<LeaveSchemeTypeAvailingLeave> findByCompanyId(Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveSchemeTypeAvailingLeave> criteriaQuery = cb.createQuery(LeaveSchemeTypeAvailingLeave.class);
		Root<LeaveSchemeTypeAvailingLeave> root = criteriaQuery.from(LeaveSchemeTypeAvailingLeave.class);

		criteriaQuery.select(root);

		Join<LeaveSchemeTypeAvailingLeave, LeaveSchemeType> leaveSchemeTypejoin = root.join(LeaveSchemeTypeAvailingLeave_.leaveSchemeType);
		Join<LeaveSchemeType, LeaveScheme> leaveSchemejoin = leaveSchemeTypejoin.join(LeaveSchemeType_.leaveScheme);
		Join<LeaveScheme, Company> leaveSchemeCompanyjoin = leaveSchemejoin.join(LeaveScheme_.company);

		Join<LeaveSchemeType, LeaveTypeMaster> leaveTypejoin = leaveSchemeTypejoin.join(LeaveSchemeType_.leaveTypeMaster);
		Join<LeaveTypeMaster, Company> leaveTypeCompanyjoin = leaveTypejoin.join(LeaveTypeMaster_.company);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(leaveSchemeCompanyjoin.get(Company_.companyId), companyId));
		restriction = cb.and(restriction, cb.equal(leaveTypeCompanyjoin.get(Company_.companyId), companyId));
		criteriaQuery.where(restriction);

		TypedQuery<LeaveSchemeTypeAvailingLeave> typedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<LeaveSchemeTypeAvailingLeave> leaveSchemeTypeList = typedQuery.getResultList();
		return leaveSchemeTypeList;
	}

	@Override
	public LeaveSchemeTypeAvailingLeave findByLeaveAppliationId(Long leaveApplicationId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveSchemeTypeAvailingLeave> criteriaQuery = cb.createQuery(LeaveSchemeTypeAvailingLeave.class);
		Root<LeaveSchemeTypeAvailingLeave> root = criteriaQuery.from(LeaveSchemeTypeAvailingLeave.class);

		criteriaQuery.select(root);

		Join<LeaveSchemeTypeAvailingLeave, LeaveSchemeType> leaveSchemeTypejoin = root.join(LeaveSchemeTypeAvailingLeave_.leaveSchemeType);
		Join<LeaveSchemeType, EmployeeLeaveSchemeType> leaveSchemejoin = leaveSchemeTypejoin.join(LeaveSchemeType_.employeeLeaveSchemeTypes);
		Join<EmployeeLeaveSchemeType, LeaveApplication> leaveSchemeCompanyjoin = leaveSchemejoin.join(EmployeeLeaveSchemeType_.leaveApplications);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(leaveSchemeCompanyjoin.get(LeaveApplication_.leaveApplicationId), leaveApplicationId));

		criteriaQuery.where(restriction);

		TypedQuery<LeaveSchemeTypeAvailingLeave> typedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<LeaveSchemeTypeAvailingLeave> leaveSchemeTypeList = typedQuery.getResultList();

		return leaveSchemeTypeList.get(0);
	}
}
