package com.payasia.dao.impl;

import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.springframework.stereotype.Repository;

import com.payasia.dao.BaseDAO;
import com.payasia.dao.EmpDataImportTemplateFieldDAO;
import com.payasia.dao.bean.DataDictionary;
import com.payasia.dao.bean.DataDictionary_;
import com.payasia.dao.bean.EmpDataImportTemplateField;
import com.payasia.dao.bean.EmpDataImportTemplateField_;

@Repository
public class EmpDataImportTemplateFieldDAOImpl extends BaseDAO implements
		EmpDataImportTemplateFieldDAO {

	@Override
	public void save(EmpDataImportTemplateField empDataImportTemplateField) {
		super.save(empDataImportTemplateField);
	}

	@Override
	protected Object getBaseEntity() {
		EmpDataImportTemplateField empDataImportTemplateField = new EmpDataImportTemplateField();
		return empDataImportTemplateField;
	}

	@Override
	public void update(EmpDataImportTemplateField empDataImportTemplateField) {
		super.update(empDataImportTemplateField);

	}

	@Override
	public void delete(EmpDataImportTemplateField empDataImportTemplateField) {
		super.delete(empDataImportTemplateField);

	}

	@Override
	public EmpDataImportTemplateField findById(long fieldId) {
		EmpDataImportTemplateField empDataImportTemplateField = super.findById(
				EmpDataImportTemplateField.class, fieldId);
		return empDataImportTemplateField;
	}

	@Override
	public List<EmpDataImportTemplateField> findByConditionFormId(
			Long companyId, Long entityId, Long formId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmpDataImportTemplateField> criteriaQuery = cb
				.createQuery(EmpDataImportTemplateField.class);
		Root<EmpDataImportTemplateField> empImportRoot = criteriaQuery
				.from(EmpDataImportTemplateField.class);
		criteriaQuery.select(empImportRoot);

		Predicate restriction = cb.conjunction();

		Subquery<Long> subquery = criteriaQuery.subquery(Long.class);
		Root<DataDictionary> dataDictionaryRoot = subquery
				.from(DataDictionary.class);
		subquery.select(dataDictionaryRoot
				.get(DataDictionary_.dataDictionaryId));

		Predicate subRestriction = cb.conjunction();

		subRestriction = cb.and(
				subRestriction,
				cb.equal(
						dataDictionaryRoot.get(DataDictionary_.company)
								.get("companyId").as(Long.class), companyId));

		subRestriction = cb.and(
				subRestriction,
				cb.equal(dataDictionaryRoot.get(DataDictionary_.entityMaster)
						.get("entityId").as(Long.class), entityId));

		subRestriction = cb.and(subRestriction, cb.equal(
				dataDictionaryRoot.get(DataDictionary_.formID), formId));

		subquery.where(subRestriction);

		restriction = cb.and(
				restriction,
				cb.in(empImportRoot
						.get(EmpDataImportTemplateField_.dataDictionary)
						.get("dataDictionaryId").as(Long.class))
						.value(subquery));

		criteriaQuery.where(restriction);

		TypedQuery<EmpDataImportTemplateField> empImportQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<EmpDataImportTemplateField> importList = empImportQuery
				.getResultList();
		return importList;
	}

	@Override
	public List<EmpDataImportTemplateField> findByExcelField(
			String excelFieldName, long templateId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmpDataImportTemplateField> criteriaQuery = cb
				.createQuery(EmpDataImportTemplateField.class);
		Root<EmpDataImportTemplateField> empImportRoot = criteriaQuery
				.from(EmpDataImportTemplateField.class);
		criteriaQuery.select(empImportRoot);

		Predicate restrictions = cb.conjunction();

		restrictions = cb.and(restrictions, cb.equal(cb.upper(empImportRoot
				.get(EmpDataImportTemplateField_.excelFieldName)),
				excelFieldName.toUpperCase()));

		restrictions = cb.and(restrictions, cb.equal(
				empImportRoot
						.get(EmpDataImportTemplateField_.empDataImportTemplate)
						.get("importTemplateId").as(Long.class), templateId));

		criteriaQuery.where(restrictions);

		TypedQuery<EmpDataImportTemplateField> empImportQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return empImportQuery.getResultList();
	}

	@Override
	public void deleteByCondition(Long importTemplateId) {

		String queryString = "DELETE FROM EmpDataImportTemplateField d WHERE d.empDataImportTemplate.importTemplateId = :importTemplateId";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("importTemplateId", importTemplateId);

		q.executeUpdate();
	}

}
