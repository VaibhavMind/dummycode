package com.payasia.dao.impl;

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

import com.payasia.common.dto.OTItemMasterConditionDTO;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.OTItemMasterDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.OTItemMaster;
import com.payasia.dao.bean.OTItemMaster_;
import com.payasia.dao.bean.OTTemplate;
import com.payasia.dao.bean.OTTemplateItem;
import com.payasia.dao.bean.OTTemplateItem_;
import com.payasia.dao.bean.OTTemplate_;

@Repository
public class OTItemMasterDAOImpl extends BaseDAO implements OTItemMasterDAO {
	@Override
	public List<OTItemMaster> findAll(long companyId, PageRequest pageDTO,
			SortCondition sortDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<OTItemMaster> criteriaQuery = cb
				.createQuery(OTItemMaster.class);
		Root<OTItemMaster> oTItemMasterRoot = criteriaQuery
				.from(OTItemMaster.class);
		criteriaQuery.select(oTItemMasterRoot);

		Join<OTItemMaster, Company> oTItemMasterJoin = oTItemMasterRoot
				.join(OTItemMaster_.company);
		Path<Long> companyID = oTItemMasterJoin.get(Company_.companyId);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(companyID, companyId));

		criteriaQuery.where(restriction);

		TypedQuery<OTItemMaster> otItemMasterTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			otItemMasterTypedQuery.setFirstResult(getStartPosition(pageDTO));
			otItemMasterTypedQuery.setMaxResults(pageDTO.getPageSize());
		}
		return otItemMasterTypedQuery.getResultList();
	}

	@Override
	public OTItemMaster findById(Long otItemId) {
		OTItemMaster otItemMaster = super
				.findById(OTItemMaster.class, otItemId);
		return otItemMaster;
	}

	@Override
	public void update(OTItemMaster itemMaster) {
		super.update(itemMaster);
	}

	@Override
	public void save(OTItemMaster itemMaster) {
		super.save(itemMaster);

	}

	@Override
	public void delete(OTItemMaster itemMaster) {
		super.delete(itemMaster);
	}

	@Override
	public List<OTItemMaster> findByCondition(
			OTItemMasterConditionDTO conditionDTO, PageRequest pageDTO,
			SortCondition sortDTO, Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<OTItemMaster> criteriaQuery = cb
				.createQuery(OTItemMaster.class);
		Root<OTItemMaster> oTItemMasterRoot = criteriaQuery
				.from(OTItemMaster.class);
		criteriaQuery.select(oTItemMasterRoot);

		Join<OTItemMaster, Company> oTItemMasterJoin = oTItemMasterRoot
				.join(OTItemMaster_.company);

		Path<Long> companyID = oTItemMasterJoin.get(Company_.companyId);

		Predicate restriction = cb.conjunction();

		if (StringUtils.isNotBlank(conditionDTO.getName())) {
			restriction = cb.and(restriction, cb.like(
					cb.upper(oTItemMasterRoot.get(OTItemMaster_.otItemName)),
					conditionDTO.getName().toUpperCase()));
		}

		if (StringUtils.isNotBlank(conditionDTO.getCode())) {
			restriction = cb.and(restriction, cb.like(
					cb.upper(oTItemMasterRoot.get(OTItemMaster_.code)),
					conditionDTO.getCode().toUpperCase()));
		}

		if (StringUtils.isNotBlank(conditionDTO.getDescription())) {
			restriction = cb.and(restriction, cb.like(
					cb.upper(oTItemMasterRoot.get(OTItemMaster_.otItemDesc)),
					conditionDTO.getDescription().toUpperCase()));
		}

		if (StringUtils.isNotBlank(conditionDTO.getVisibleOrHidden())) {
			if ("visible".equalsIgnoreCase(conditionDTO.getVisibleOrHidden())) {
				restriction = cb.and(restriction, cb.equal(
						oTItemMasterRoot.get(OTItemMaster_.visibility), true));
			}
			if ("hidden".equalsIgnoreCase(conditionDTO.getVisibleOrHidden())) {
				restriction = cb.and(restriction, cb.equal(
						oTItemMasterRoot.get(OTItemMaster_.visibility), false));
			}
		}

		restriction = cb.and(restriction, cb.equal(companyID, companyId));

		criteriaQuery.where(restriction);
		if (sortDTO != null) {

		}
		TypedQuery<OTItemMaster> otItemMasterTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			otItemMasterTypedQuery.setFirstResult(getStartPosition(pageDTO));
			otItemMasterTypedQuery.setMaxResults(pageDTO.getPageSize());
		}
		return otItemMasterTypedQuery.getResultList();

	}

	@Override
	public int getCountByCondition(OTItemMasterConditionDTO conditionDTO,
			Long companyId) {
		return findByCondition(conditionDTO, null, null, companyId).size();
	}

	@Override
	protected Object getBaseEntity() {
		OTItemMaster otItemMaster = new OTItemMaster();
		return otItemMaster;
	}

	@Override
	public OTItemMaster findByCompanyIdItemNameItemId(Long companyId,
			String name, Long itemId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<OTItemMaster> criteriaQuery = cb
				.createQuery(OTItemMaster.class);
		Root<OTItemMaster> oTItemMasterRoot = criteriaQuery
				.from(OTItemMaster.class);
		criteriaQuery.select(oTItemMasterRoot);

		Join<OTItemMaster, Company> oTItemMasterJoin = oTItemMasterRoot
				.join(OTItemMaster_.company);

		Path<Long> companyID = oTItemMasterJoin.get(Company_.companyId);

		Predicate restriction = cb.conjunction();

		if (itemId != null) {
			restriction = cb.and(restriction, cb.notEqual(
					oTItemMasterRoot.get(OTItemMaster_.otItemId), itemId));
		}

		restriction = cb.and(restriction, cb.equal(companyID, companyId));

		restriction = cb.and(restriction,
				cb.equal(oTItemMasterRoot.get(OTItemMaster_.otItemName), name));

		criteriaQuery.where(restriction);

		TypedQuery<OTItemMaster> OTItemMasterQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		if (OTItemMasterQuery.getResultList().size() > 0) {
			OTItemMaster oTItemMaster = OTItemMasterQuery.getSingleResult();
			return oTItemMaster;
		} else {
			return null;
		}
	}

	@Override
	public List<OTItemMaster> findByCondition(Long otTemplateId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<OTItemMaster> criteriaQuery = cb
				.createQuery(OTItemMaster.class);
		Root<OTItemMaster> claimItemRoot = criteriaQuery
				.from(OTItemMaster.class);

		criteriaQuery.select(claimItemRoot);

		Join<OTItemMaster, OTTemplateItem> otItemtemplateTypeJoin = claimItemRoot
				.join(OTItemMaster_.otTemplateItems);

		Join<OTTemplateItem, OTTemplate> otTemplateJoin = otItemtemplateTypeJoin
				.join(OTTemplateItem_.otTemplate);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(
				otTemplateJoin.get(OTTemplate_.otTemplateId), otTemplateId));

		criteriaQuery.where(restriction);

		TypedQuery<OTItemMaster> otItemTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<OTItemMaster> otItemList = otItemTypedQuery.getResultList();

		return otItemList;
	}
}
