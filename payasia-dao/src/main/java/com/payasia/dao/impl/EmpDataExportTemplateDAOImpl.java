/**
 * @author abhisheksachdeva
 * 
 */
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
import com.payasia.dao.EmpDataExportTemplateDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.EmpDataExportTemplate;
import com.payasia.dao.bean.EmpDataExportTemplate_;
import com.payasia.dao.bean.EntityMaster;
import com.payasia.dao.bean.EntityMaster_;

/**
 * The Class EmpDataExportTemplateDAOImpl.
 */
@Repository
public class EmpDataExportTemplateDAOImpl extends BaseDAO implements
		EmpDataExportTemplateDAO {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.EmpDataExportTemplateDAO#save(com.payasia.dao.bean.
	 * EmpDataExportTemplate)
	 */
	@Override
	public EmpDataExportTemplate save(
			EmpDataExportTemplate empDataExportTemplate) {
		EmpDataExportTemplate persistObj = empDataExportTemplate;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (EmpDataExportTemplate) getBaseEntity();
			beanUtil.copyProperties(persistObj, empDataExportTemplate);
		} catch (IllegalAccessException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		} catch (InvocationTargetException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		}
		this.entityManagerFactory.persist(persistObj);
		return persistObj;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.BaseDAO#getBaseEntity()
	 */
	@Override
	protected Object getBaseEntity() {
		EmpDataExportTemplate empDataExportTemplate = new EmpDataExportTemplate();
		return empDataExportTemplate;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.EmpDataExportTemplateDAO#update(com.payasia.dao.bean.
	 * EmpDataExportTemplate)
	 */
	@Override
	public void update(EmpDataExportTemplate empDataExportTemplate) {
		super.update(empDataExportTemplate);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.EmpDataExportTemplateDAO#findByName(java.lang.String,
	 * long)
	 */
	@Override
	public List<EmpDataExportTemplate> findByName(String templateName,
			long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmpDataExportTemplate> criteriaQuery = cb
				.createQuery(EmpDataExportTemplate.class);
		Root<EmpDataExportTemplate> empExportRoot = criteriaQuery
				.from(EmpDataExportTemplate.class);

		Join<EmpDataExportTemplate, Company> empCompanyJoin = empExportRoot
				.join(EmpDataExportTemplate_.company);

		criteriaQuery.select(empExportRoot);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(cb.upper(empExportRoot
				.get(EmpDataExportTemplate_.templateName)), templateName
				.toUpperCase()));

		restriction = cb.and(restriction,
				cb.equal(empCompanyJoin.get(Company_.companyId), companyId));

		criteriaQuery.where(restriction);

		TypedQuery<EmpDataExportTemplate> empTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return empTypedQuery.getResultList();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.EmpDataExportTemplateDAO#findAll(com.payasia.common.form
	 * .PageRequest, com.payasia.common.form.SortCondition, java.lang.Long)
	 */
	@Override
	public List<EmpDataExportTemplate> findAll(PageRequest pageDTO,
			SortCondition sortDTO, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmpDataExportTemplate> criteriaQuery = cb
				.createQuery(EmpDataExportTemplate.class);
		Root<EmpDataExportTemplate> empExportRoot = criteriaQuery
				.from(EmpDataExportTemplate.class);

		Join<EmpDataExportTemplate, Company> empCompanyJoin = empExportRoot
				.join(EmpDataExportTemplate_.company);

		criteriaQuery.select(empExportRoot);

		criteriaQuery.where(cb.equal(empCompanyJoin.get(Company_.companyId),
				companyId));

		if (sortDTO != null) {
			Path<String> sortPath = getSortPathForExportList(sortDTO,
					empExportRoot);
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

		TypedQuery<EmpDataExportTemplate> empTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			empTypedQuery.setFirstResult(getStartPosition(pageDTO));
			empTypedQuery.setMaxResults(pageDTO.getPageSize());
		}
		return empTypedQuery.getResultList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.EmpDataExportTemplateDAO#findByCondition(com.payasia.
	 * common.form.PageRequest, com.payasia.common.form.SortCondition,
	 * java.lang.Long, com.payasia.common.dto.ExcelImportExportConditionDTO)
	 */
	@Override
	public List<EmpDataExportTemplate> findByCondition(PageRequest pageDTO,
			SortCondition sortDTO, Long companyId,
			ExcelImportExportConditionDTO conditionDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmpDataExportTemplate> criteriaQuery = cb
				.createQuery(EmpDataExportTemplate.class);
		Root<EmpDataExportTemplate> empExportRoot = criteriaQuery
				.from(EmpDataExportTemplate.class);

		Join<EmpDataExportTemplate, Company> empCompanyJoin = empExportRoot
				.join(EmpDataExportTemplate_.company);
		Join<EmpDataExportTemplate, EntityMaster> entityJoin = empExportRoot
				.join(EmpDataExportTemplate_.entityMaster);

		criteriaQuery.select(empExportRoot);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(empCompanyJoin.get(Company_.companyId), companyId));

		if (conditionDTO.getEntityId() != null) {
			restriction = cb.and(restriction, cb.equal(
					entityJoin.get(EntityMaster_.entityId),
					conditionDTO.getEntityId()));
		}

		if (conditionDTO.getSearchString() != null) {
			restriction = cb.and(restriction, cb.like(cb.upper(empExportRoot
					.get(EmpDataExportTemplate_.templateName)), conditionDTO
					.getSearchString().toUpperCase()));
		}

		if (conditionDTO.getDescriptionSearchString() != null) {
			restriction = cb.and(restriction, cb.like(cb.upper(empExportRoot
					.get(EmpDataExportTemplate_.description)), conditionDTO
					.getDescriptionSearchString().toUpperCase()));
		}
		if (conditionDTO.getScopeSearchString() != null) {
			restriction = cb.and(restriction, cb.like(
					cb.upper(empExportRoot.get(EmpDataExportTemplate_.scope)),
					conditionDTO.getScopeSearchString().toUpperCase()));
		}

		criteriaQuery.where(restriction);

		if (sortDTO != null) {
			Path<String> sortPath = getSortPathForExportList(sortDTO,
					empExportRoot);
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

		criteriaQuery.orderBy(cb.asc(empExportRoot
				.get(EmpDataExportTemplate_.templateName)));

		TypedQuery<EmpDataExportTemplate> empTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			empTypedQuery.setFirstResult(getStartPosition(pageDTO));
			empTypedQuery.setMaxResults(pageDTO.getPageSize());
		}
		return empTypedQuery.getResultList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.EmpDataExportTemplateDAO#getSortPathForExportList(com
	 * .payasia.common.form.SortCondition, javax.persistence.criteria.Root)
	 */
	@Override
	public Path<String> getSortPathForExportList(SortCondition sortDTO,
			Root<EmpDataExportTemplate> empExportRoot) {

		List<String> exportTemplateIsIdList = new ArrayList<String>();
		exportTemplateIsIdList.add(SortConstants.EXCEL_EXPORT_TEMPLATE_ID);

		List<String> exportTemplateIsColList = new ArrayList<String>();
		exportTemplateIsColList.add(SortConstants.EXCEL_EXPORT_CATEGORY);
		exportTemplateIsColList
				.add(SortConstants.EXCEL_EXPORT_TEMPLATE_DESCRIPTION);
		exportTemplateIsColList.add(SortConstants.EXCEL_EXPORT_TEMPLATE_NAME);

		Path<String> sortPath = null;

		if (exportTemplateIsColList.contains(sortDTO.getColumnName())) {
			sortPath = empExportRoot.get(colMap.get(EmpDataExportTemplate.class
					+ sortDTO.getColumnName()));
		}
		return sortPath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.EmpDataExportTemplateDAO#getCountForAll(java.lang.Long)
	 */
	@Override
	public int getCountForAll(Long companyId) {
		return findAll(null, null, companyId).size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.EmpDataExportTemplateDAO#getCountForAll(java.lang.Long,
	 * com.payasia.common.dto.ExcelImportExportConditionDTO)
	 */
	@Override
	public int getCountForAll(Long companyId,
			ExcelImportExportConditionDTO conditionDTO) {
		return findByCondition(null, null, companyId, conditionDTO).size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.EmpDataExportTemplateDAO#delete(com.payasia.dao.bean.
	 * EmpDataExportTemplate)
	 */
	@Override
	public void delete(EmpDataExportTemplate empDataExportTemplate) {
		super.delete(empDataExportTemplate);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.EmpDataExportTemplateDAO#findById(java.lang.Long)
	 */
	@Override
	public EmpDataExportTemplate findById(Long templateId) {

		EmpDataExportTemplate empDataExportTemplate = super.findById(
				EmpDataExportTemplate.class, templateId);

		return empDataExportTemplate;
	}

	@Override
	public Long getCountForDataExport(Long companyId,
			ExcelImportExportConditionDTO conditionDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
		Root<EmpDataExportTemplate> empExportRoot = criteriaQuery
				.from(EmpDataExportTemplate.class);

		Join<EmpDataExportTemplate, Company> empCompanyJoin = empExportRoot
				.join(EmpDataExportTemplate_.company);
		Join<EmpDataExportTemplate, EntityMaster> entityJoin = empExportRoot
				.join(EmpDataExportTemplate_.entityMaster);

		criteriaQuery.select(cb.count(empExportRoot));

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(empCompanyJoin.get(Company_.companyId), companyId));

		if (conditionDTO.getEntityId() != null) {
			restriction = cb.and(restriction, cb.equal(
					entityJoin.get(EntityMaster_.entityId),
					conditionDTO.getEntityId()));
		}

		if (conditionDTO.getSearchString() != null) {
			restriction = cb.and(restriction, cb.like(cb.upper(empExportRoot
					.get(EmpDataExportTemplate_.templateName)), conditionDTO
					.getSearchString().toUpperCase()));
		}

		if (conditionDTO.getDescriptionSearchString() != null) {
			restriction = cb.and(restriction, cb.like(cb.upper(empExportRoot
					.get(EmpDataExportTemplate_.description)), conditionDTO
					.getDescriptionSearchString().toUpperCase()));
		}

		criteriaQuery.where(restriction);

		TypedQuery<Long> empTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return empTypedQuery.getSingleResult();
	}

	@Override
	public Long getCountForExcelExport(Long companyId,
			ExcelImportExportConditionDTO conditionDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
		Root<EmpDataExportTemplate> empExportRoot = criteriaQuery
				.from(EmpDataExportTemplate.class);

		Join<EmpDataExportTemplate, Company> empCompanyJoin = empExportRoot
				.join(EmpDataExportTemplate_.company);
		Join<EmpDataExportTemplate, EntityMaster> entityJoin = empExportRoot
				.join(EmpDataExportTemplate_.entityMaster);

		criteriaQuery.select(cb.count(empExportRoot));

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(empCompanyJoin.get(Company_.companyId), companyId));

		if (conditionDTO.getEntityId() != null) {
			restriction = cb.and(restriction, cb.equal(
					entityJoin.get(EntityMaster_.entityId),
					conditionDTO.getEntityId()));
		}

		if (conditionDTO.getSearchString() != null) {
			restriction = cb.and(restriction, cb.like(cb.upper(empExportRoot
					.get(EmpDataExportTemplate_.templateName)), conditionDTO
					.getSearchString().toUpperCase()));
		}

		if (conditionDTO.getDescriptionSearchString() != null) {
			restriction = cb.and(restriction, cb.like(cb.upper(empExportRoot
					.get(EmpDataExportTemplate_.description)), conditionDTO
					.getDescriptionSearchString().toUpperCase()));
		}

		criteriaQuery.where(restriction);

		TypedQuery<Long> empTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return empTypedQuery.getSingleResult();
	}
}
