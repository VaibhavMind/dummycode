package com.payasia.dao.impl;

import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.payasia.dao.BaseDAO;
import com.payasia.dao.WorkdayFieldMasterDAO;
import com.payasia.dao.bean.WorkdayFieldMaster;
import com.payasia.dao.bean.WorkdayFieldMaster_;

@Repository
public class WorkdayFieldMasterDAOImpl extends BaseDAO implements WorkdayFieldMasterDAO {

	@Override
	protected Object getBaseEntity() {
		return new WorkdayFieldMaster();
	}
	
	@Override
	public WorkdayFieldMaster findById(long workdayFieldID) {
		return super.findById(WorkdayFieldMaster.class, workdayFieldID);
	}

	@Override
	public List<WorkdayFieldMaster> findByEntityName(String entityName) {
		
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<WorkdayFieldMaster> criteriaQuery = cb
				.createQuery(WorkdayFieldMaster.class);
		Root<WorkdayFieldMaster> workdayFieldRoot = criteriaQuery
				.from(WorkdayFieldMaster.class);
		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(
				workdayFieldRoot.get(WorkdayFieldMaster_.entityName), entityName));
		restriction = cb.and(restriction, cb.equal(
				workdayFieldRoot.get(WorkdayFieldMaster_.displayable), 1));
		criteriaQuery.where(restriction);
		criteriaQuery.select(workdayFieldRoot);
		TypedQuery<WorkdayFieldMaster> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		return typedQuery.getResultList();
	}

	@Override
	public WorkdayFieldMaster findBySectionAndFieldName(String sectionName, String fieldName) {
		
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<WorkdayFieldMaster> criteriaQuery = cb.createQuery(WorkdayFieldMaster.class);
		Root<WorkdayFieldMaster> workdayFieldRoot = criteriaQuery.from(WorkdayFieldMaster.class);
		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(workdayFieldRoot.get(WorkdayFieldMaster_.sectionName), sectionName));
		restriction = cb.and(restriction, cb.equal(workdayFieldRoot.get(WorkdayFieldMaster_.fieldName), fieldName));
		criteriaQuery.where(restriction);
		criteriaQuery.select(workdayFieldRoot);
		TypedQuery<WorkdayFieldMaster> typedQuery = entityManagerFactory.createQuery(criteriaQuery);
		List<WorkdayFieldMaster> resultList = typedQuery.getResultList();
		if(resultList.isEmpty())
			return null;
		else return resultList.get(0);
				
	}
}
