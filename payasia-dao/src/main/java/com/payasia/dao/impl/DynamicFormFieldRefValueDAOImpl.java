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

import com.payasia.common.dto.LundinConditionDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.util.NullAwareBeanUtilsBean;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.DynamicFormFieldRefValueDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.DataDictionary;
import com.payasia.dao.bean.DataDictionary_;
import com.payasia.dao.bean.DynamicFormFieldRefValue;
import com.payasia.dao.bean.DynamicFormFieldRefValue_;
import com.payasia.dao.bean.EntityMaster;
import com.payasia.dao.bean.EntityMaster_;

@Repository
public class DynamicFormFieldRefValueDAOImpl extends BaseDAO implements
		DynamicFormFieldRefValueDAO {

	@Override
	public void save(DynamicFormFieldRefValue dynamicFormFieldRefValue) {
		super.save(dynamicFormFieldRefValue);
	}

	@Override
	public void update(DynamicFormFieldRefValue dynamicFormFieldRefValue) {
		super.update(dynamicFormFieldRefValue);
	}

	@Override
	public void delete(DynamicFormFieldRefValue dynamicFormFieldRefValue) {
		super.delete(dynamicFormFieldRefValue);
	}

	@Override
	public DynamicFormFieldRefValue findById(Long fieldRefId) {
		DynamicFormFieldRefValue dynamicFormFieldRefValue = super.findById(
				DynamicFormFieldRefValue.class, fieldRefId);
		return dynamicFormFieldRefValue;
	}

	@Override
	public DynamicFormFieldRefValue saveReturn(DynamicFormFieldRefValue saveObj) {
		DynamicFormFieldRefValue persistObj = saveObj;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (DynamicFormFieldRefValue) getBaseEntity();
			beanUtil.copyProperties(persistObj, saveObj);
		} catch (IllegalAccessException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		} catch (InvocationTargetException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		}
		this.entityManagerFactory.persist(persistObj);
		return persistObj;
	}

	@Override
	public DynamicFormFieldRefValue newTranSaveReturn(
			DynamicFormFieldRefValue saveObj) {
		DynamicFormFieldRefValue persistObj = saveObj;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (DynamicFormFieldRefValue) getBaseEntity();
			beanUtil.copyProperties(persistObj, saveObj);
		} catch (IllegalAccessException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		} catch (InvocationTargetException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		}
		this.entityManagerFactory.persist(persistObj);
		this.entityManagerFactory.detach(persistObj);
		return persistObj;
	}

	public <T> void save(T saveObj) {
		T persistObj = saveObj;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (T) getBaseEntity();
			beanUtil.copyProperties(persistObj, saveObj);
		} catch (IllegalAccessException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		} catch (InvocationTargetException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		}
		this.entityManagerFactory.persist(persistObj);
	}

	@Override
	public List<DynamicFormFieldRefValue> findByDataDictionayId(
			Long dataDictionaryId, LundinConditionDTO condition) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<DynamicFormFieldRefValue> criteriaQuery = cb
				.createQuery(DynamicFormFieldRefValue.class);
		Root<DynamicFormFieldRefValue> fieldRefRoot = criteriaQuery
				.from(DynamicFormFieldRefValue.class);

		criteriaQuery.select(fieldRefRoot);
		Predicate restriction = cb.conjunction();
		Join<DynamicFormFieldRefValue, DataDictionary> dataDictionaryJoin = fieldRefRoot
				.join(DynamicFormFieldRefValue_.dataDictionary);
		restriction = cb.and(restriction, cb.equal(
				dataDictionaryJoin.get(DataDictionary_.dataDictionaryId),
				dataDictionaryId));
		if (condition.getDepartmentCode() != null)
			restriction = cb.and(restriction, cb.equal(
					fieldRefRoot.get(DynamicFormFieldRefValue_.code),
					condition.getDepartmentCode()));
		if (condition.getDepartmentName() != null)
			restriction = cb.and(restriction, cb.equal(
					fieldRefRoot.get(DynamicFormFieldRefValue_.description),
					condition.getDepartmentName()));

		criteriaQuery.where(restriction).orderBy(
				cb.asc(fieldRefRoot.get(DynamicFormFieldRefValue_.code)));
		TypedQuery<DynamicFormFieldRefValue> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return typedQuery.getResultList();

	}

	@Override
	public List<DynamicFormFieldRefValue> findByDataDictionayId(
			Long dataDictionaryId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<DynamicFormFieldRefValue> criteriaQuery = cb
				.createQuery(DynamicFormFieldRefValue.class);
		Root<DynamicFormFieldRefValue> fieldRefRoot = criteriaQuery
				.from(DynamicFormFieldRefValue.class);

		criteriaQuery.select(fieldRefRoot);

		Join<DynamicFormFieldRefValue, DataDictionary> dataDictionaryJoin = fieldRefRoot
				.join(DynamicFormFieldRefValue_.dataDictionary);

		criteriaQuery.where(cb.equal(
				dataDictionaryJoin.get(DataDictionary_.dataDictionaryId),
				dataDictionaryId));
		criteriaQuery.orderBy(cb.asc(fieldRefRoot
				.get(DynamicFormFieldRefValue_.code)));
		TypedQuery<DynamicFormFieldRefValue> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return typedQuery.getResultList();

	}

	@Override
	public DynamicFormFieldRefValue findByCondition(Long dataDictionaryId,
			String code) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<DynamicFormFieldRefValue> criteriaQuery = cb
				.createQuery(DynamicFormFieldRefValue.class);
		Root<DynamicFormFieldRefValue> fieldRefRoot = criteriaQuery
				.from(DynamicFormFieldRefValue.class);

		criteriaQuery.select(fieldRefRoot);

		Join<DynamicFormFieldRefValue, DataDictionary> dataDictionaryJoin = fieldRefRoot
				.join(DynamicFormFieldRefValue_.dataDictionary);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(
				dataDictionaryJoin.get(DataDictionary_.dataDictionaryId),
				dataDictionaryId));

		restriction = cb.and(restriction, cb.equal(
				cb.upper(fieldRefRoot.get(DynamicFormFieldRefValue_.code)),
				code.toUpperCase()));

		criteriaQuery.where(restriction);
		TypedQuery<DynamicFormFieldRefValue> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<DynamicFormFieldRefValue> dynamicFormList = typedQuery
				.getResultList();
		if (dynamicFormList != null && !dynamicFormList.isEmpty()) {
			return dynamicFormList.get(0);
		}
		return null;

	}

	@Override
	public List<DynamicFormFieldRefValue> findByCondition(Long companyId,
			Long entityId, Long formId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<DynamicFormFieldRefValue> criteriaQuery = cb
				.createQuery(DynamicFormFieldRefValue.class);
		Root<DynamicFormFieldRefValue> fieldRefRoot = criteriaQuery
				.from(DynamicFormFieldRefValue.class);

		criteriaQuery.select(fieldRefRoot);

		Join<DynamicFormFieldRefValue, DataDictionary> dataDictionaryJoin = fieldRefRoot
				.join(DynamicFormFieldRefValue_.dataDictionary);

		Join<DataDictionary, Company> dataDictCompanyJoin = dataDictionaryJoin
				.join(DataDictionary_.company);

		Join<DataDictionary, EntityMaster> dataDictEntityMstJoin = dataDictionaryJoin
				.join(DataDictionary_.entityMaster);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(
				dataDictCompanyJoin.get(Company_.companyId), companyId));

		restriction = cb.and(restriction, cb.equal(
				dataDictEntityMstJoin.get(EntityMaster_.entityId), entityId));

		restriction = cb.and(restriction, cb.equal(
				dataDictionaryJoin.get(DataDictionary_.formID), formId));

		criteriaQuery.where(restriction);
		criteriaQuery.orderBy(cb.asc(fieldRefRoot
				.get(DynamicFormFieldRefValue_.code)));
		TypedQuery<DynamicFormFieldRefValue> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return typedQuery.getResultList();

	}

	@Override
	protected Object getBaseEntity() {
		DynamicFormFieldRefValue dynamicFormFieldRefValue = new DynamicFormFieldRefValue();
		return dynamicFormFieldRefValue;
	}

	@Override
	public List<DynamicFormFieldRefValue> findByDataDictionayId(Long dataDictionaryId, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<DynamicFormFieldRefValue> criteriaQuery = cb
				.createQuery(DynamicFormFieldRefValue.class);
		Root<DynamicFormFieldRefValue> fieldRefRoot = criteriaQuery
				.from(DynamicFormFieldRefValue.class);

		criteriaQuery.select(fieldRefRoot);

		Join<DynamicFormFieldRefValue, DataDictionary> dataDictionaryJoin = fieldRefRoot
				.join(DynamicFormFieldRefValue_.dataDictionary);
		Join<DataDictionary, Company> dataDictCompanyJoin = dataDictionaryJoin
				.join(DataDictionary_.company);
		
		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(
				dataDictionaryJoin.get(DataDictionary_.dataDictionaryId), dataDictionaryId));

		restriction = cb.and(restriction, cb.equal(
				dataDictCompanyJoin.get(Company_.companyId), ""+companyId));

		criteriaQuery.where(restriction);
		
		criteriaQuery.orderBy(cb.asc(fieldRefRoot
				.get(DynamicFormFieldRefValue_.code)));
		TypedQuery<DynamicFormFieldRefValue> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return typedQuery.getResultList();
	}

}
