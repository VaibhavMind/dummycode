package com.payasia.dao.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.payasia.common.bean.util.UserContext;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.util.NullAwareBeanUtilsBean;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.DynamicFormTableRecordDAO;
import com.payasia.dao.DynamicFormTableRecordSeqDAO;
import com.payasia.dao.bean.DynamicFormTableRecord;
import com.payasia.dao.bean.DynamicFormTableRecord_;

@Repository
public class DynamicFormTableRecordDAOImpl extends BaseDAO implements
		DynamicFormTableRecordDAO {

	@Resource
	DynamicFormTableRecordSeqDAO dynamicFormTableRecordSeqDAO;

	@Override
	protected Object getBaseEntity() {

		DynamicFormTableRecord dynamicFormTableRecord = new DynamicFormTableRecord();

		return dynamicFormTableRecord;
	}

	@Override
	public synchronized Long getMaxTableRecordId() {

		long maxTableRecordId = dynamicFormTableRecordSeqDAO.getNextVal();

		return maxTableRecordId;

	}

	@Override
	public void save(DynamicFormTableRecord dynamicFormTableRecord) {
		super.save(dynamicFormTableRecord);
	}

	@Override
	public void saveWithFlush(DynamicFormTableRecord dynamicFormTableRecord) {
		DynamicFormTableRecord persistObj = dynamicFormTableRecord;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (DynamicFormTableRecord) getBaseEntity();
			beanUtil.copyProperties(persistObj, dynamicFormTableRecord);
		} catch (IllegalAccessException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		} catch (InvocationTargetException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		}
		this.entityManagerFactory.persist(persistObj);
		this.entityManagerFactory.flush();
	}

	@Override
	public List<DynamicFormTableRecord> getTableRecords(Long tid,
			String sortOrder, String sortBy) {
		Long companyId = null;
		if (StringUtils.isNotBlank(UserContext.getWorkingCompanyId())) {
			companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		} else {
			companyId = null;
		}
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<DynamicFormTableRecord> criteriaQuery = cb
				.createQuery(DynamicFormTableRecord.class);
		Root<DynamicFormTableRecord> dynamicFormTableRecordRoot = criteriaQuery
				.from(DynamicFormTableRecord.class);

		criteriaQuery.select(dynamicFormTableRecordRoot);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(dynamicFormTableRecordRoot
				.get(DynamicFormTableRecord_.id)
				.get("dynamicFormTableRecordId").as(Long.class), tid));
		restriction = cb.and(restriction, cb.or(
				cb.equal(
						dynamicFormTableRecordRoot.get("companyId").as(
								Long.class), companyId),
				cb.isNull(dynamicFormTableRecordRoot.get("companyId").as(
						Long.class))));

		criteriaQuery.where(restriction);
		if (StringUtils.isNotBlank(sortOrder) && StringUtils.isNotBlank(sortBy)) {
			if (sortOrder
					.equalsIgnoreCase(PayAsiaConstants.SORT_ORDER_NEWEST_TO_OLDEST)) {
				criteriaQuery.orderBy(cb.desc(dynamicFormTableRecordRoot
						.get(sortBy)));
			} else {
				criteriaQuery.orderBy(cb.asc(dynamicFormTableRecordRoot
						.get(sortBy)));
			}
		}

		TypedQuery<DynamicFormTableRecord> dynamicFormTableRecordTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return dynamicFormTableRecordTypedQuery.getResultList();

	}

	@Override
	public DynamicFormTableRecord findByEffectiveDate(Long tid,
			String effictiveDate) {
		Long companyId = null;
		if (StringUtils.isNotBlank(UserContext.getWorkingCompanyId())) {
			companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		} else {
			companyId = null;
		}
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<DynamicFormTableRecord> criteriaQuery = cb
				.createQuery(DynamicFormTableRecord.class);
		Root<DynamicFormTableRecord> dynamicFormTableRecordRoot = criteriaQuery
				.from(DynamicFormTableRecord.class);

		criteriaQuery.select(dynamicFormTableRecordRoot);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(dynamicFormTableRecordRoot
				.get(DynamicFormTableRecord_.id)
				.get("dynamicFormTableRecordId").as(Long.class), tid));

		restriction = cb.and(restriction, cb.equal(cb.substring(
				dynamicFormTableRecordRoot.get(DynamicFormTableRecord_.col1),
				0, 11), effictiveDate));
		restriction = cb.and(restriction, cb.or(
				cb.equal(
						dynamicFormTableRecordRoot.get("companyId").as(
								Long.class), companyId),
				cb.isNull(dynamicFormTableRecordRoot.get("companyId").as(
						Long.class))));

		criteriaQuery.where(restriction);

		TypedQuery<DynamicFormTableRecord> dynamicFormTableRecordTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<DynamicFormTableRecord> dynamicFormTableRecordList = dynamicFormTableRecordTypedQuery
				.getResultList();
		if (dynamicFormTableRecordList != null
				&& !dynamicFormTableRecordList.isEmpty()) {
			return dynamicFormTableRecordList.get(0);
		}
		return null;
	}

	@Override
	public DynamicFormTableRecord findById(Long tableID) {

		DynamicFormTableRecord dynamicFormTableRecord = super.findById(
				DynamicFormTableRecord.class, tableID);
		return dynamicFormTableRecord;
	}

	@Override
	public void update(DynamicFormTableRecord dynamicFormTableRecord) {

		super.update(dynamicFormTableRecord);
		this.entityManagerFactory.flush();
	}

	@Override
	public void delete(DynamicFormTableRecord dynamicFormTableRecord) {
		super.delete(dynamicFormTableRecord);
	}

	@Override
	public void deleteByCondition(Long tableID) {

		String queryString = "DELETE FROM DynamicFormTableRecord d WHERE d.id.dynamicFormTableRecordId = :tid ";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("tid", tableID);

		q.executeUpdate();
	}

	@Override
	public int getMaxSequenceNumber(Long tableId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Integer> criteriaQuery = cb.createQuery(Integer.class);
		Root<DynamicFormTableRecord> dynamicFormTableRecordRoot = criteriaQuery
				.from(DynamicFormTableRecord.class);
		criteriaQuery.select(cb.max(dynamicFormTableRecordRoot
				.get(DynamicFormTableRecord_.id).get("sequence")
				.as(Integer.class)));

		criteriaQuery.where(cb.equal(
				dynamicFormTableRecordRoot.get(DynamicFormTableRecord_.id)
						.get("dynamicFormTableRecordId").as(Long.class),
				tableId));

		TypedQuery<Integer> maxDynamicTableRecordQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		Integer maxTableRecordID = maxDynamicTableRecordQuery.getSingleResult();
		if (maxTableRecordID == null) {
			maxTableRecordID = (int) 0;
		}
		return maxTableRecordID;

	}

	@Override
	public List<String> findDataForDictionary(String colNumber,
			String tablePosition) {
		String colName = "col" + colNumber;
		String queryString = "SELECT d."
				+ colName
				+ " FROM DynamicFormTableRecord d WHERE d.id.dynamicFormTableRecordId = :dynamicFormTableRecordId";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("dynamicFormTableRecordId",
				Long.parseLong(tablePosition));
		List<String> dataList = q.getResultList();
		return dataList;
	}

	@Override
	public void deleteByConditionEmployeeTableRecord(Long tableId, Integer seqNo) {

		String queryString = "DELETE FROM DynamicFormTableRecord d WHERE d.id.dynamicFormTableRecordId = :tableId AND d.id.sequence = :seqNo ";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("tableId", tableId);
		q.setParameter("seqNo", seqNo);

		q.executeUpdate();

	}

	@Override
	public DynamicFormTableRecord findByIdAndSeq(Long tid, Integer seq) {
		Long companyId = null;
		if (StringUtils.isNotBlank(UserContext.getWorkingCompanyId())) {
			companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		} else {
			companyId = null;
		}
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<DynamicFormTableRecord> criteriaQuery = cb
				.createQuery(DynamicFormTableRecord.class);
		Root<DynamicFormTableRecord> dynamicFormTableRecordRoot = criteriaQuery
				.from(DynamicFormTableRecord.class);

		criteriaQuery.select(dynamicFormTableRecordRoot);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(dynamicFormTableRecordRoot
				.get(DynamicFormTableRecord_.id)
				.get("dynamicFormTableRecordId").as(Long.class), tid));

		restriction = cb.and(
				restriction,
				cb.equal(
						dynamicFormTableRecordRoot
								.get(DynamicFormTableRecord_.id)
								.get("sequence").as(Integer.class), seq));
		restriction = cb.and(restriction, cb.or(
				cb.equal(
						dynamicFormTableRecordRoot.get("companyId").as(
								Long.class), companyId),
				cb.isNull(dynamicFormTableRecordRoot.get("companyId").as(
						Long.class))));
		criteriaQuery.where(restriction);

		TypedQuery<DynamicFormTableRecord> dynamicFormTableRecordTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<DynamicFormTableRecord> dynamicFormTableRecordList = dynamicFormTableRecordTypedQuery
				.getResultList();
		if (dynamicFormTableRecordList != null
				&& !dynamicFormTableRecordList.isEmpty()) {
			return dynamicFormTableRecordList.get(0);
		}
		return null;

	}

	@Override
	public DynamicFormTableRecord findByIdAndSeqForHrisManager(Long tid,
			Integer seq) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<DynamicFormTableRecord> criteriaQuery = cb
				.createQuery(DynamicFormTableRecord.class);
		Root<DynamicFormTableRecord> dynamicFormTableRecordRoot = criteriaQuery
				.from(DynamicFormTableRecord.class);

		criteriaQuery.select(dynamicFormTableRecordRoot);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(dynamicFormTableRecordRoot
				.get(DynamicFormTableRecord_.id)
				.get("dynamicFormTableRecordId").as(Long.class), tid));

		restriction = cb.and(
				restriction,
				cb.equal(
						dynamicFormTableRecordRoot
								.get(DynamicFormTableRecord_.id)
								.get("sequence").as(Integer.class), seq));
		criteriaQuery.where(restriction);

		TypedQuery<DynamicFormTableRecord> dynamicFormTableRecordTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<DynamicFormTableRecord> dynamicFormTableRecordList = dynamicFormTableRecordTypedQuery
				.getResultList();
		if (dynamicFormTableRecordList != null
				&& !dynamicFormTableRecordList.isEmpty()) {
			return dynamicFormTableRecordList.get(0);
		}
		return null;

	}

	@Override
	public List<Object> getMaxEffectiveDate(Long dynamicFormTableRecordId) {
		Long companyId = null;
		if (StringUtils.isNotBlank(UserContext.getWorkingCompanyId())) {
			companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		} else {
			companyId = null;
		}
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Object> criteriaQuery = cb.createQuery(Object.class);
		Root<DynamicFormTableRecord> dynamicFormTableRecordRoot = criteriaQuery
				.from(DynamicFormTableRecord.class);
		criteriaQuery.select(dynamicFormTableRecordRoot.get("col1").as(
				Date.class));

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(dynamicFormTableRecordRoot
				.get(DynamicFormTableRecord_.id)
				.get("dynamicFormTableRecordId").as(Long.class),
				dynamicFormTableRecordId));
		restriction = cb.and(restriction, cb.or(
				cb.equal(
						dynamicFormTableRecordRoot.get("companyId").as(
								Long.class), companyId),
				cb.isNull(dynamicFormTableRecordRoot.get("companyId").as(
						Long.class))));
		criteriaQuery.where(restriction);
		criteriaQuery.orderBy(cb.desc(dynamicFormTableRecordRoot.get("col1")
				.as(Date.class)));
		TypedQuery<Object> maxFormIdQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		maxFormIdQuery.setMaxResults(1);
		List<Object> maxEffectiveDate = maxFormIdQuery.getResultList();

		return maxEffectiveDate;
	}

	@Override
	public List<String> getExistanceOfCodeDescDynField(String colNumber,
			String fieldRefValueId) {
		String colName = "col" + colNumber;
		String queryString = "SELECT d." + colName
				+ " FROM DynamicFormTableRecord d WHERE  d." + colName
				+ " = :colName ";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("colName", fieldRefValueId);

		List<String> dataList = q.getResultList();

		return dataList;
	}
	
	@Override
	public List<DynamicFormTableRecord> findByTableId(Long tableId) {
		
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<DynamicFormTableRecord> criteriaQuery = cb
				.createQuery(DynamicFormTableRecord.class);
		Root<DynamicFormTableRecord> dynamicFormTableRecordRoot = criteriaQuery
				.from(DynamicFormTableRecord.class);

		criteriaQuery.select(dynamicFormTableRecordRoot);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(dynamicFormTableRecordRoot
				.get(DynamicFormTableRecord_.id)
				.get("dynamicFormTableRecordId").as(Long.class), tableId));

		criteriaQuery.where(restriction);

		TypedQuery<DynamicFormTableRecord> dynamicFormTableRecordTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return dynamicFormTableRecordTypedQuery
				.getResultList();

	}


}
