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
import com.payasia.dao.EmployeeDocumentHistoryDAO;
import com.payasia.dao.bean.DynamicFormTableRecord;
import com.payasia.dao.bean.DynamicFormTableRecordPK;
import com.payasia.dao.bean.DynamicFormTableRecordPK_;
import com.payasia.dao.bean.DynamicFormTableRecord_;
import com.payasia.dao.bean.EmployeeDocumentHistory;
import com.payasia.dao.bean.EmployeeDocumentHistory_;

/**
 * @author vivekjain
 * 
 */
@Repository
public class EmployeeDocumentHistoryDAOImpl extends BaseDAO implements
		EmployeeDocumentHistoryDAO {

	@Override
	protected Object getBaseEntity() {
		EmployeeDocumentHistory employeeDocumentHistory = new EmployeeDocumentHistory();
		return employeeDocumentHistory;
	}

	@Override
	public void update(EmployeeDocumentHistory employeeDocumentHistory) {
		super.update(employeeDocumentHistory);
	}

	@Override
	public void save(EmployeeDocumentHistory employeeDocumentHistory) {
		super.save(employeeDocumentHistory);
	}

	@Override
	public void delete(EmployeeDocumentHistory employeeDocumentHistory) {
		super.delete(employeeDocumentHistory);
	}

	@Override
	public EmployeeDocumentHistory findById(long employeeDocumentHistoryId) {

		EmployeeDocumentHistory employeeDocumentHistory = super.findById(
				EmployeeDocumentHistory.class, employeeDocumentHistoryId);
		return employeeDocumentHistory;
	}

	@Override
	public List<EmployeeDocumentHistory> findByCondition(Long tableId,
			Integer seqNo) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeDocumentHistory> criteriaQuery = cb
				.createQuery(EmployeeDocumentHistory.class);
		Root<EmployeeDocumentHistory> empDocRoot = criteriaQuery
				.from(EmployeeDocumentHistory.class);
		criteriaQuery.select(empDocRoot);

		Predicate restriction = cb.conjunction();

		Join<EmployeeDocumentHistory, DynamicFormTableRecord> dynamicFormJoin = empDocRoot
				.join(EmployeeDocumentHistory_.dynamicFormTableRecord);
		Join<DynamicFormTableRecord, DynamicFormTableRecordPK> dynamicFormPkJoin = dynamicFormJoin
				.join(DynamicFormTableRecord_.id);

		restriction = cb.and(restriction, cb.equal(dynamicFormPkJoin
				.get(DynamicFormTableRecordPK_.dynamicFormTableRecordId),
				tableId));
		restriction = cb.and(restriction, cb.equal(
				dynamicFormPkJoin.get(DynamicFormTableRecordPK_.sequence),
				seqNo));
		criteriaQuery.where(restriction);

		TypedQuery<EmployeeDocumentHistory> empDocTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return empDocTypedQuery.getResultList();
	}

	@Override
	public void deleteByConditionEmployeeTableRecord(Long tableId, Integer seqNo) {

		String queryString = "DELETE FROM EmployeeDocumentHistory d WHERE d.dynamicFormTableRecord.id.dynamicFormTableRecordId = :tableId AND d.dynamicFormTableRecord.id.sequence = :seqNo ";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("tableId", tableId);
		q.setParameter("seqNo", seqNo);

		q.executeUpdate();

	}

}
