package com.payasia.dao.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;
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
import com.payasia.dao.WorkdayFtpFieldMappingDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.DataDictionary;
import com.payasia.dao.bean.DataDictionary_;
import com.payasia.dao.bean.WorkdayFieldMaster;
import com.payasia.dao.bean.WorkdayFieldMaster_;
import com.payasia.dao.bean.WorkdayFtpFieldMapping;
import com.payasia.dao.bean.WorkdayFtpFieldMapping_;

@Repository
public class WorkdayFTPFieldMappingDAOImpl extends BaseDAO implements WorkdayFtpFieldMappingDAO {

	@Override
	protected Object getBaseEntity() {
		return new WorkdayFtpFieldMapping();
	}
	
	@Override
	public WorkdayFtpFieldMapping findById(Long fieldMappingId) {
		return super.findById(WorkdayFtpFieldMapping.class, fieldMappingId);
	}

	@Override
	public void save(WorkdayFtpFieldMapping WorkdayFTPFieldMapping) {
		super.save(WorkdayFTPFieldMapping);		
	}

	@Override
	public void update(WorkdayFtpFieldMapping WorkdayFTPFieldMapping) {
		super.update(WorkdayFTPFieldMapping);
	}
	
	@Override
	public void delete(WorkdayFtpFieldMapping WorkdayFTPFieldMapping) {
		super.delete(WorkdayFTPFieldMapping);
	}

	@Override
	public WorkdayFtpFieldMapping saveReturn(WorkdayFtpFieldMapping WorkdayFTPFieldMapping) {
		WorkdayFtpFieldMapping persistObj = WorkdayFTPFieldMapping;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (WorkdayFtpFieldMapping) getBaseEntity();
			beanUtil.copyProperties(persistObj, WorkdayFTPFieldMapping);
		} catch (IllegalAccessException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		} catch (InvocationTargetException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		}
		this.entityManagerFactory.persist(persistObj);
		return persistObj;
	}
	
	@Override
	public List<WorkdayFtpFieldMapping> findAllWorkdayFTPFieldMappingByCompanyId(Long companyId) {
		
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<WorkdayFtpFieldMapping> criteriaQuery = cb
				.createQuery(WorkdayFtpFieldMapping.class);
		Root<WorkdayFtpFieldMapping> workdayFTPFieldMappingRoot = criteriaQuery
				.from(WorkdayFtpFieldMapping.class);

		criteriaQuery.select(workdayFTPFieldMappingRoot);

		Join<WorkdayFtpFieldMapping, Company> companyJoin = workdayFTPFieldMappingRoot
				.join(WorkdayFtpFieldMapping_.company);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction,
				cb.equal(companyJoin.get(Company_.companyId), companyId));
		criteriaQuery.where(restriction);
		TypedQuery<WorkdayFtpFieldMapping> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		List<WorkdayFtpFieldMapping> ftpConfigPublickeyAttachment = null;

		ftpConfigPublickeyAttachment = typedQuery.getResultList();

		return ftpConfigPublickeyAttachment;
	}

	@Override
	public List<WorkdayFtpFieldMapping> findAllWorkdayFTPFieldMappingByCompanyId(Long companyId, boolean isEmployeeData) {
		
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<WorkdayFtpFieldMapping> criteriaQuery = cb
				.createQuery(WorkdayFtpFieldMapping.class);
		Root<WorkdayFtpFieldMapping> workdayFTPFieldMappingRoot = criteriaQuery
				.from(WorkdayFtpFieldMapping.class);

		criteriaQuery.select(workdayFTPFieldMappingRoot);

		Join<WorkdayFtpFieldMapping, Company> companyJoin = workdayFTPFieldMappingRoot
				.join(WorkdayFtpFieldMapping_.company);
		Join<WorkdayFtpFieldMapping, WorkdayFieldMaster> workdayFieldJoin = workdayFTPFieldMappingRoot
				.join(WorkdayFtpFieldMapping_.workdayField);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction,
				cb.equal(companyJoin.get(Company_.companyId), companyId));
		restriction = cb.and(restriction,
				cb.equal(workdayFieldJoin.get(WorkdayFieldMaster_.entityName), isEmployeeData ? "Employee" : "Payroll"));
		criteriaQuery.where(restriction);
		TypedQuery<WorkdayFtpFieldMapping> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		List<WorkdayFtpFieldMapping> ftpConfigPublickeyAttachment = null;

		ftpConfigPublickeyAttachment = typedQuery.getResultList();

		return ftpConfigPublickeyAttachment;
	}

	@Override
	public WorkdayFtpFieldMapping findByCompanyIdAndWorkdayFieldId(Long companyId,
			Long workdayFieldId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<WorkdayFtpFieldMapping> criteriaQuery = cb
				.createQuery(WorkdayFtpFieldMapping.class);
		Root<WorkdayFtpFieldMapping> workdayFieldMappingRoot = criteriaQuery
				.from(WorkdayFtpFieldMapping.class);
		criteriaQuery.select(workdayFieldMappingRoot);
		Join<WorkdayFtpFieldMapping, Company> companyJoin = workdayFieldMappingRoot
				.join(WorkdayFtpFieldMapping_.company);
		Join<WorkdayFtpFieldMapping, WorkdayFieldMaster> workdayFieldJoin = workdayFieldMappingRoot
				.join(WorkdayFtpFieldMapping_.workdayField);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(
				workdayFieldJoin.get(WorkdayFieldMaster_.workdayFieldId),
				workdayFieldId));
		restriction = cb.and(restriction,
				cb.equal(companyJoin.get(Company_.companyId), companyId));

		criteriaQuery.where(restriction);
		TypedQuery<WorkdayFtpFieldMapping> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		WorkdayFtpFieldMapping workdayFTPFieldMapping;
		try {
			workdayFTPFieldMapping = typedQuery.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}

		return workdayFTPFieldMapping;
	}

	@Override
	public void deleteById(Long fieldMappingId) {
		
		String queryString = "DELETE FROM WorkdayFtpFieldMapping fm WHERE fm.workdayFtpFieldMappingId = :fieldMappingId ";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("fieldMappingId", fieldMappingId);
		q.executeUpdate();
	}
	
	@Override
	public WorkdayFtpFieldMapping getEmployeeNumberMapping(Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<WorkdayFtpFieldMapping> criteriaQuery = cb
				.createQuery(WorkdayFtpFieldMapping.class);
		Root<WorkdayFtpFieldMapping> ftpFieldMappingRoot = criteriaQuery
				.from(WorkdayFtpFieldMapping.class);

		criteriaQuery.select(ftpFieldMappingRoot);

		Join<WorkdayFtpFieldMapping, Company> companyJoin = ftpFieldMappingRoot
				.join(WorkdayFtpFieldMapping_.company);

		Join<WorkdayFtpFieldMapping, DataDictionary> dataDictionaryJoin = ftpFieldMappingRoot
				.join(WorkdayFtpFieldMapping_.hroField);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction,
				cb.equal(companyJoin.get(Company_.companyId), companyId));
		restriction = cb.and(restriction, cb.equal(
				dataDictionaryJoin.get(DataDictionary_.columnName),
				PayAsiaConstants.PAYASIA_EMPLOYEE_NUMBER));
		criteriaQuery.where(restriction);
		TypedQuery<WorkdayFtpFieldMapping> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<WorkdayFtpFieldMapping> employeeNumberMappings =  typedQuery.getResultList();
		
		return employeeNumberMappings.isEmpty() ? null : employeeNumberMappings.get(0);
	}
}
