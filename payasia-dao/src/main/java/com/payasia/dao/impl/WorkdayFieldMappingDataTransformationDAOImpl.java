package com.payasia.dao.impl;

import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.payasia.dao.BaseDAO;
import com.payasia.dao.WorkdayFieldMappingDataTransformationDAO;
import com.payasia.dao.bean.WorkdayFieldMappingDataTransformation;
import com.payasia.dao.bean.WorkdayFieldMappingDataTransformation_;
import com.payasia.dao.bean.WorkdayFtpFieldMapping;
import com.payasia.dao.bean.WorkdayFtpFieldMapping_;

@Repository
public class WorkdayFieldMappingDataTransformationDAOImpl extends BaseDAO
		implements WorkdayFieldMappingDataTransformationDAO {
	
	@Override
	protected Object getBaseEntity() {
		return new WorkdayFieldMappingDataTransformation();
	}

	@Override
	public void save(WorkdayFieldMappingDataTransformation workdayFieldMappingDataTransformation) {
		super.save(workdayFieldMappingDataTransformation);
	}
	
	@Override
	public List<WorkdayFieldMappingDataTransformation> getDataTransformationByFieldMappingId(Long fieldMappingId) {
		
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<WorkdayFieldMappingDataTransformation> cq = cb.createQuery(WorkdayFieldMappingDataTransformation.class);
		Root<WorkdayFieldMappingDataTransformation> root = cq.from(WorkdayFieldMappingDataTransformation.class);
		cq.select(root);
		Join<WorkdayFieldMappingDataTransformation, WorkdayFtpFieldMapping> fieldMappingJoin 
			= root.join(WorkdayFieldMappingDataTransformation_.workdayFtpFieldMapping);
		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, 
					cb.equal(fieldMappingJoin.get(WorkdayFtpFieldMapping_.workdayFtpFieldMappingId), fieldMappingId));
		
		cq.where(restriction);
		TypedQuery<WorkdayFieldMappingDataTransformation> typedQuery = entityManagerFactory.createQuery(cq);
		
		return typedQuery.getResultList();
	}

	@Override
	public WorkdayFieldMappingDataTransformation findById(Long dataTransformationId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteByCondition(Long fieldMappingId) {
		String queryString = "DELETE FROM WorkdayFieldMappingDataTransformation fmdt WHERE fmdt.workdayFtpFieldMapping.workdayFtpFieldMappingId = :fieldMappingId ";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("fieldMappingId", fieldMappingId);
		q.executeUpdate();
	}

}
