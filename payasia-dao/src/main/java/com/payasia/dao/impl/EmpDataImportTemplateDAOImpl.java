package com.payasia.dao.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.payasia.common.dto.ExcelImportExportConditionDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.NullAwareBeanUtilsBean;
import com.payasia.common.util.SortConstants;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.EmpDataImportTemplateDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.EmpDataImportTemplate;
import com.payasia.dao.bean.EmpDataImportTemplate_;
import com.payasia.dao.bean.EntityMaster;
import com.payasia.dao.bean.EntityMaster_;

@Repository
public class EmpDataImportTemplateDAOImpl extends BaseDAO implements
		EmpDataImportTemplateDAO {

	@Override
	public EmpDataImportTemplate save(
			EmpDataImportTemplate empDataImportTemplate) {
		EmpDataImportTemplate persistObj = empDataImportTemplate;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (EmpDataImportTemplate) getBaseEntity();
			beanUtil.copyProperties(persistObj, empDataImportTemplate);
		} catch (IllegalAccessException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		} catch (InvocationTargetException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		}
		this.entityManagerFactory.persist(persistObj);
		return persistObj;
	}

	@Override
	public List<EmpDataImportTemplate> findByName(String templateName,
			long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmpDataImportTemplate> criteriaQuery = cb
				.createQuery(EmpDataImportTemplate.class);
		Root<EmpDataImportTemplate> empImportRoot = criteriaQuery
				.from(EmpDataImportTemplate.class);

		Join<EmpDataImportTemplate, Company> empCompanyJoin = empImportRoot
				.join(EmpDataImportTemplate_.company);

		criteriaQuery.select(empImportRoot);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(cb.upper(empImportRoot
				.get(EmpDataImportTemplate_.templateName)), templateName
				.toUpperCase()));

		restriction = cb.and(restriction,
				cb.equal(empCompanyJoin.get(Company_.companyId), companyId));

		criteriaQuery.where(restriction);

		TypedQuery<EmpDataImportTemplate> empTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return empTypedQuery.getResultList();

	}

	@Override
	protected Object getBaseEntity() {
		EmpDataImportTemplate empDataImportTemplate = new EmpDataImportTemplate();
		return empDataImportTemplate;
	}

	@Override
	public List<EmpDataImportTemplate> findAll(PageRequest pageDTO,
			SortCondition sortDTO, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmpDataImportTemplate> criteriaQuery = cb
				.createQuery(EmpDataImportTemplate.class);
		Root<EmpDataImportTemplate> empImportRoot = criteriaQuery
				.from(EmpDataImportTemplate.class);

		Join<EmpDataImportTemplate, Company> empCompanyJoin = empImportRoot
				.join(EmpDataImportTemplate_.company);

		criteriaQuery.select(empImportRoot);
		criteriaQuery.where(cb.equal(empCompanyJoin.get(Company_.companyId),
				companyId));

		if (sortDTO != null) {
			Path<String> sortPath = getSortPathForImportList(sortDTO,
					empImportRoot);
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

		TypedQuery<EmpDataImportTemplate> empTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			empTypedQuery.setFirstResult(getStartPosition(pageDTO));
			empTypedQuery.setMaxResults(pageDTO.getPageSize());
		}
		return empTypedQuery.getResultList();
	}

	@Override
	public List<EmpDataImportTemplate> findByCondition(PageRequest pageDTO,
			SortCondition sortDTO, Long companyId,
			ExcelImportExportConditionDTO conditionDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmpDataImportTemplate> criteriaQuery = cb
				.createQuery(EmpDataImportTemplate.class);
		Root<EmpDataImportTemplate> empImportRoot = criteriaQuery
				.from(EmpDataImportTemplate.class);

		Join<EmpDataImportTemplate, Company> empCompanyJoin = empImportRoot
				.join(EmpDataImportTemplate_.company);
		Join<EmpDataImportTemplate, EntityMaster> entityJoin = empImportRoot
				.join(EmpDataImportTemplate_.entityMaster);
		criteriaQuery.select(empImportRoot);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(empCompanyJoin.get(Company_.companyId), companyId));

		if (conditionDTO.getEntityId() != null) {
			restriction = cb.and(restriction, cb.equal(
					entityJoin.get(EntityMaster_.entityId),
					conditionDTO.getEntityId()));
		}

		if (conditionDTO.getSearchString() != null) {
			restriction = cb.and(restriction, cb.like(cb.upper(empImportRoot
					.get(EmpDataImportTemplate_.templateName)), conditionDTO
					.getSearchString().toUpperCase()));
		}

		if (conditionDTO.getDescriptionSearchString() != null) {
			restriction = cb.and(restriction, cb.like(cb.upper(empImportRoot
					.get(EmpDataImportTemplate_.description)), conditionDTO
					.getDescriptionSearchString().toUpperCase()));
		}

		criteriaQuery.where(restriction);

		if (sortDTO != null) {
			Path<String> sortPath = getSortPathForImportList(sortDTO,
					empImportRoot);
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

		TypedQuery<EmpDataImportTemplate> empTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			empTypedQuery.setFirstResult(getStartPosition(pageDTO));
			empTypedQuery.setMaxResults(pageDTO.getPageSize());
		}
		return empTypedQuery.getResultList();
	}

	@Override
	public Path<String> getSortPathForImportList(SortCondition sortDTO,
			Root<EmpDataImportTemplate> empImportRoot) {

		List<String> importTemplateIsIdList = new ArrayList<String>();
		importTemplateIsIdList.add(SortConstants.EXCEL_IMPORT_TEMPLATE_ID);

		List<String> importTemplateIsColList = new ArrayList<String>();
		importTemplateIsColList.add(SortConstants.EXCEL_IMPORT_CATEGORY);
		importTemplateIsColList
				.add(SortConstants.EXCEL_IMPORT_TEMPLATE_DESCRIPTION);
		importTemplateIsColList.add(SortConstants.EXCEL_IMPORT_TEMPLATE_NAME);

		Path<String> sortPath = null;

		if (importTemplateIsColList.contains(sortDTO.getColumnName())) {
			sortPath = empImportRoot.get(colMap.get(EmpDataImportTemplate.class
					+ sortDTO.getColumnName()));
		}
		return sortPath;
	}

	@Override
	public List<EmpDataImportTemplate> findByEntity(long entityId,
			Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmpDataImportTemplate> criteriaQuery = cb
				.createQuery(EmpDataImportTemplate.class);
		Root<EmpDataImportTemplate> empImportRoot = criteriaQuery
				.from(EmpDataImportTemplate.class);
		Join<EmpDataImportTemplate, Company> empCompanyJoin = empImportRoot
				.join(EmpDataImportTemplate_.company);

		criteriaQuery.select(empImportRoot);
		Join<EmpDataImportTemplate, EntityMaster> entityJoin = empImportRoot
				.join(EmpDataImportTemplate_.entityMaster);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(entityJoin.get(EntityMaster_.entityId), entityId));

		if (companyId != null) {
			restriction = cb
					.and(restriction, cb.equal(
							empCompanyJoin.get(Company_.companyId), companyId));
		}
		criteriaQuery.where(restriction);
		criteriaQuery.orderBy(cb.asc(empImportRoot
				.get(EmpDataImportTemplate_.importTemplateId)));
		TypedQuery<EmpDataImportTemplate> empTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return empTypedQuery.getResultList();
	}

	@Override
	public int getCountForAll(Long companyId) {
		return findAll(null, null, companyId).size();
	}

	@Override
	public int getCountForAll(Long companyId,
			ExcelImportExportConditionDTO conditionDTO) {
		return findByCondition(null, null, companyId, conditionDTO).size();
	}

	@Override
	public void delete(EmpDataImportTemplate empDataImportTemplate) {
		super.delete(empDataImportTemplate);
	}

	@Override
	public EmpDataImportTemplate findById(Long templateId) {
		EmpDataImportTemplate empDataImportTemplate = super.findById(
				EmpDataImportTemplate.class, templateId);
		return empDataImportTemplate;
	}

	@Override
	public void update(EmpDataImportTemplate empDataImportTemplate) {
		super.update(empDataImportTemplate);
	}

}
