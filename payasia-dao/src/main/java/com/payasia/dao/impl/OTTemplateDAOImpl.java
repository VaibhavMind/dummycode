package com.payasia.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.payasia.common.dto.OTTemplateConditionDTO;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.SortConstants;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.OTTemplateDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.OTTemplate;
import com.payasia.dao.bean.OTTemplate_;

@Repository
public class OTTemplateDAOImpl extends BaseDAO implements OTTemplateDAO {

	@Override
	public List<OTTemplate> getOTTemplateList(Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<OTTemplate> criteriaQuery = cb
				.createQuery(OTTemplate.class);
		Root<OTTemplate> otTemplateRoot = criteriaQuery.from(OTTemplate.class);
		criteriaQuery.select(otTemplateRoot);

		Join<OTTemplate, Company> companyJoin = otTemplateRoot
				.join(OTTemplate_.company);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(companyJoin.get(Company_.companyId), companyId));

		criteriaQuery.where(restriction);

		TypedQuery<OTTemplate> oTTemplateTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<OTTemplate> otTemplateList = oTTemplateTypedQuery.getResultList();

		return otTemplateList;
	}

	@Override
	protected Object getBaseEntity() {

		OTTemplate oTTemplate = new OTTemplate();
		return oTTemplate;
	}

	@Override
	public List<OTTemplate> getAllOTTemplateByConditionCompany(Long companyId,
			OTTemplateConditionDTO conditionDTO, PageRequest pageDTO,
			SortCondition sortDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<OTTemplate> criteriaQuery = cb
				.createQuery(OTTemplate.class);
		Root<OTTemplate> otTempRoot = criteriaQuery.from(OTTemplate.class);
		criteriaQuery.select(otTempRoot);

		Join<OTTemplate, Company> otTempRootCompanyJoin = otTempRoot
				.join(OTTemplate_.company);

		Predicate restriction = cb.conjunction();
		if (StringUtils.isNotBlank(conditionDTO.getTemplateName())) {

			restriction = cb.and(restriction, cb.like(
					cb.upper(otTempRoot.get(OTTemplate_.templateName)),
					conditionDTO.getTemplateName().toUpperCase()));
		}

		if (StringUtils.isNotBlank(conditionDTO.getStatus())) {
			if ("visible".equals(conditionDTO.getStatus())) {
				restriction = cb.and(restriction, cb.equal(
						(otTempRoot.get(OTTemplate_.visibility)), true));
			}
			if ("hidden".equals(conditionDTO.getStatus())) {
				restriction = cb.and(restriction, cb.equal(
						(otTempRoot.get(OTTemplate_.visibility)), false));
			}
		}

		restriction = cb.and(restriction, cb.equal(
				otTempRootCompanyJoin.get(Company_.companyId), companyId));

		criteriaQuery.where(restriction);

		if (sortDTO != null) {

			Path<String> sortPath = getSortPathForOTTemplate(sortDTO,
					otTempRoot);
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

		TypedQuery<OTTemplate> otTempTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			otTempTypedQuery.setFirstResult(getStartPosition(pageDTO));
			otTempTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		List<OTTemplate> otTemplateList = otTempTypedQuery.getResultList();

		return otTemplateList;
	}

	@Override
	public Path<String> getSortPathForOTTemplate(SortCondition sortDTO,
			Root<OTTemplate> otTempRoot) {

		List<String> claimTempIsColList = new ArrayList<String>();
		claimTempIsColList.add(SortConstants.OT_TEMPLATE_NAME);
		claimTempIsColList.add(SortConstants.OT_TEMPLATE_VISIBILITY);
		claimTempIsColList.add(SortConstants.OT_TEMPLATE_NUM_OF_ITEMS);

		Path<String> sortPath = null;

		if (claimTempIsColList.contains(sortDTO.getColumnName())) {
			sortPath = otTempRoot.get(colMap.get(OTTemplate.class
					+ sortDTO.getColumnName()));
		}
		return sortPath;
	}

	@Override
	public int getCountForAllOTTemplate(Long companyId,
			OTTemplateConditionDTO conditionDTO) {
		return getAllOTTemplateByConditionCompany(companyId, conditionDTO,
				null, null).size();
	}

	@Override
	public void save(OTTemplate otTemplate) {
		super.save(otTemplate);

	}

	@Override
	public OTTemplate findByOTTemplateAndCompany(Long otTemplateId,
			String otTemplateName, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<OTTemplate> criteriaQuery = cb
				.createQuery(OTTemplate.class);
		Root<OTTemplate> otTemplateRoot = criteriaQuery.from(OTTemplate.class);

		criteriaQuery.select(otTemplateRoot);

		Join<OTTemplate, Company> otTemplateRootJoin = otTemplateRoot
				.join(OTTemplate_.company);

		Predicate restriction = cb.conjunction();

		restriction = cb
				.and(restriction, cb.equal(
						otTemplateRootJoin.get(Company_.companyId), companyId));

		if (otTemplateId != null) {
			restriction = cb
					.and(restriction, cb.notEqual(
							otTemplateRoot.get(OTTemplate_.otTemplateId),
							otTemplateId));
		}

		restriction = cb.and(restriction, cb.equal(
				cb.upper(otTemplateRoot.get(OTTemplate_.templateName)),
				otTemplateName.toUpperCase()));

		criteriaQuery.where(restriction);

		TypedQuery<OTTemplate> otTemplateTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		if (otTemplateTypedQuery.getResultList().size() > 0) {
			OTTemplate otTemplateList = otTemplateTypedQuery.getSingleResult();

			return otTemplateList;
		} else {
			return null;
		}
	}

	@Override
	public OTTemplate findById(Long otTemplateId) {
		return super.findById(OTTemplate.class, otTemplateId);
	}

	@Override
	public void delete(OTTemplate otTemplate) {
		super.save(otTemplate);

	}

	@Override
	public void update(OTTemplate otTemplateVO) {
		super.update(otTemplateVO);

	}

}
