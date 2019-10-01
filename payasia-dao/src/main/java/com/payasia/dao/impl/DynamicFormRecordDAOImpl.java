package com.payasia.dao.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.util.NullAwareBeanUtilsBean;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.DynamicFormRecordDAO;
import com.payasia.dao.bean.DynamicFormRecord;
import com.payasia.dao.bean.DynamicFormRecord_;

@Repository
public class DynamicFormRecordDAOImpl extends BaseDAO implements
		DynamicFormRecordDAO {

	@Override
	protected Object getBaseEntity() {

		DynamicFormRecord dynamicFormRecord = new DynamicFormRecord();
		return dynamicFormRecord;
	}

	@Override
	public void save(DynamicFormRecord dynamicFormRecord) {
		super.save(dynamicFormRecord);
	}

	@Override
	public DynamicFormRecord saveReturn(DynamicFormRecord dynamicFormRecord) {
		DynamicFormRecord persistObj = dynamicFormRecord;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (DynamicFormRecord) getBaseEntity();
			beanUtil.copyProperties(persistObj, dynamicFormRecord);
		} catch (IllegalAccessException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		} catch (InvocationTargetException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		}
		this.entityManagerFactory.persist(persistObj);
		return persistObj;
	}

	@Override
	public Long getMaxFormId(Long companyId, Long entityId, Long formId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
		Root<DynamicFormRecord> dynamicFormRecordRoot = criteriaQuery
				.from(DynamicFormRecord.class);
		criteriaQuery.select(cb.max(dynamicFormRecordRoot
				.get(DynamicFormRecord_.recordId)));

		Predicate restriction = cb.conjunction();

		if (companyId != null) {

			restriction = cb.and(restriction, cb.equal(
					dynamicFormRecordRoot.get(DynamicFormRecord_.company_ID),
					companyId));

		}

		if (entityId != null) {

			restriction = cb.and(restriction, cb.equal(
					dynamicFormRecordRoot.get(DynamicFormRecord_.entity_ID),
					entityId));
		}

		if (formId != null) {

			restriction = cb.and(restriction, cb.equal(
					dynamicFormRecordRoot.get(DynamicFormRecord_.form_ID),
					formId));
		}

		criteriaQuery.where(restriction);

		TypedQuery<Long> maxRecordIdQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		Long maxRecordId = maxRecordIdQuery.getSingleResult();
		if (maxRecordId == null) {
			maxRecordId = (long) 0;
		}

		return maxRecordId;
	}

	@Override
	public DynamicFormRecord findById(Long recordId) {

		return super.findById(DynamicFormRecord.class, recordId);

	}

	@Override
	public DynamicFormRecord getEmpRecords(Long entityKey, Integer version,
			Long dynamicFormId, Long entityId, Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<DynamicFormRecord> criteriaQuery = cb
				.createQuery(DynamicFormRecord.class);
		Root<DynamicFormRecord> dynamicFormRecordRoot = criteriaQuery
				.from(DynamicFormRecord.class);
		criteriaQuery.select(dynamicFormRecordRoot);

		Predicate restriction = cb.conjunction();

		if (dynamicFormId != null) {
			restriction = cb.and(restriction, cb.equal(
					dynamicFormRecordRoot.get(DynamicFormRecord_.form_ID),
					dynamicFormId));
		}

		restriction = cb.and(restriction, cb.equal(
				dynamicFormRecordRoot.get(DynamicFormRecord_.entityKey),
				entityKey));

		if (entityId != null) {
			restriction = cb.and(restriction, cb.equal(
					dynamicFormRecordRoot.get(DynamicFormRecord_.entity_ID),
					entityId));
		}
		if (companyId != null) {
			restriction = cb.and(restriction, cb.equal(
					dynamicFormRecordRoot.get(DynamicFormRecord_.company_ID),
					companyId));
		}

		criteriaQuery.where(restriction);

		TypedQuery<DynamicFormRecord> maxRecordIdQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<DynamicFormRecord> maxRecordIdList = maxRecordIdQuery
				.getResultList();
		if (maxRecordIdList != null && !maxRecordIdList.isEmpty()) {
			return maxRecordIdList.get(0);
		}
		return null;

	}

	@Override
	public void update(DynamicFormRecord dynamicFormRecord) {
		this.entityManagerFactory.merge(dynamicFormRecord);
		this.entityManagerFactory.flush();
	}

	@Override
	public void delete(DynamicFormRecord dynamicFormRecord) {
		this.entityManagerFactory.remove(dynamicFormRecord);
	}

	@Override
	public List<DynamicFormRecord> findByEntityKey(Long entityKey,
			Long entityId, Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<DynamicFormRecord> criteriaQuery = cb
				.createQuery(DynamicFormRecord.class);
		Root<DynamicFormRecord> dynamicFormRecordRoot = criteriaQuery
				.from(DynamicFormRecord.class);
		criteriaQuery.select(dynamicFormRecordRoot);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(
				dynamicFormRecordRoot.get(DynamicFormRecord_.entityKey),
				entityKey));

		if (entityId != null) {
			restriction = cb.and(restriction, cb.equal(
					dynamicFormRecordRoot.get(DynamicFormRecord_.entity_ID),
					entityId));
		}
		if (companyId != null) {
			restriction = cb.and(restriction, cb.equal(
					dynamicFormRecordRoot.get(DynamicFormRecord_.company_ID),
					companyId));
		}

		criteriaQuery.where(restriction);

		TypedQuery<DynamicFormRecord> maxRecordIdQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<DynamicFormRecord> resultList = maxRecordIdQuery.getResultList();
		return resultList;

	}

	@Override
	public List<DynamicFormRecord> findByFormId(Long formId, Long entityId,
			Long companyId, Integer version) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<DynamicFormRecord> criteriaQuery = cb
				.createQuery(DynamicFormRecord.class);
		Root<DynamicFormRecord> dynamicFormRecordRoot = criteriaQuery
				.from(DynamicFormRecord.class);
		criteriaQuery.select(dynamicFormRecordRoot);

		Predicate restriction = cb.conjunction();

		if (formId != null) {
			restriction = cb.and(restriction, cb.equal(
					dynamicFormRecordRoot.get(DynamicFormRecord_.form_ID),
					formId));
		}

		if (entityId != null) {
			restriction = cb.and(restriction, cb.equal(
					dynamicFormRecordRoot.get(DynamicFormRecord_.entity_ID),
					entityId));
		}
		if (companyId != null) {
			restriction = cb.and(restriction, cb.equal(
					dynamicFormRecordRoot.get(DynamicFormRecord_.company_ID),
					companyId));
		}

		if (version != null) {
			restriction = cb.and(restriction, cb.equal(
					dynamicFormRecordRoot.get(DynamicFormRecord_.version),
					version));
		}

		criteriaQuery.where(restriction);

		TypedQuery<DynamicFormRecord> maxRecordIdQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<DynamicFormRecord> resultList = maxRecordIdQuery.getResultList();

		return resultList;

	}

	@Override
	public List<String> findDataForDictionary(Long formId, Long companyId,
			String colNumber, Long entityId) {
		String colName = "col" + colNumber;
		String queryString = "SELECT d."
				+ colName
				+ " FROM DynamicFormRecord d WHERE d.company_ID = :company AND d.entity_ID = :entity AND d.form_ID =:form ";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("entity", entityId);
		q.setParameter("company", companyId);
		q.setParameter("form", formId);

		List<String> dataList = q.getResultList();

		return dataList;
	}

	@Override
	public List<String> getExistanceOfCodeDescDynField(String colNumber,
			String fieldRefValueId) {
		String colName = "col" + colNumber;
		String queryString = "SELECT d." + colName
				+ " FROM DynamicFormRecord d WHERE  d." + colName
				+ " = :colName ";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("colName", fieldRefValueId);

		List<String> dataList = q.getResultList();

		return dataList;
	}

	@Override
	public void updateEmployeeExternalId(Long companyId, Long entityKey,
			Long entityId, Long formId, Long recordId, String columnName,
			String externalId) {
		String queryString = "UPDATE DynamicFormRecord dfr set  dfr."
				+ columnName
				+ " = :externalId where  dfr.company_ID = :companyId and dfr.entity_ID = :entityId and dfr.entityKey = :entityKey and dfr.form_ID = :formId  and dfr.recordId = :recordId";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("externalId", externalId);
		q.setParameter("companyId", companyId);
		q.setParameter("entityKey", entityKey);
		q.setParameter("entityId", entityId);
		q.setParameter("formId", formId);
		q.setParameter("recordId", recordId);
		q.executeUpdate();
	}

}
