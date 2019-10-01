package com.payasia.dao.impl;

import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.payasia.dao.BaseDAO;
import com.payasia.dao.OTTemplateItemDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.OTItemMaster;
import com.payasia.dao.bean.OTItemMaster_;
import com.payasia.dao.bean.OTTemplate;
import com.payasia.dao.bean.OTTemplateItem;
import com.payasia.dao.bean.OTTemplateItem_;
import com.payasia.dao.bean.OTTemplate_;

@Repository
public class OTTemplateItemDAOImpl extends BaseDAO implements OTTemplateItemDAO {

	@Override
	protected Object getBaseEntity() {
		OTTemplateItem oTTemplateItem = new OTTemplateItem();
		return oTTemplateItem;
	}

	@Override
	public void save(OTTemplateItem otTemplateItem) {
		super.save(otTemplateItem);

	}

	@Override
	public List<OTTemplateItem> findByCondition(Long otTemplateId,
			Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<OTTemplateItem> criteriaQuery = cb
				.createQuery(OTTemplateItem.class);
		Root<OTTemplateItem> otTempItemRoot = criteriaQuery
				.from(OTTemplateItem.class);

		criteriaQuery.select(otTempItemRoot);

		Join<OTTemplateItem, OTTemplate> otTempTypeJoin = otTempItemRoot
				.join(OTTemplateItem_.otTemplate);

		Join<OTTemplate, Company> otTempCompanyTypeJoin = otTempTypeJoin
				.join(OTTemplate_.company);

		Join<OTTemplateItem, OTItemMaster> otTempItemTypeJoin = otTempItemRoot
				.join(OTTemplateItem_.otItemMaster);
		Join<OTItemMaster, Company> otItemCompanyJoin = otTempItemTypeJoin
				.join(OTItemMaster_.company);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(
				otTempTypeJoin.get(OTTemplate_.otTemplateId), otTemplateId));

		restriction = cb.and(restriction,
				cb.equal(otItemCompanyJoin.get(Company_.companyId), companyId));

		restriction = cb.and(restriction, cb.equal(
				otTempCompanyTypeJoin.get(Company_.companyId), companyId));

		criteriaQuery.where(restriction);

		TypedQuery<OTTemplateItem> otTemplateItemTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<OTTemplateItem> otTemplateItemList = otTemplateItemTypedQuery
				.getResultList();

		return otTemplateItemList;
	}

	@Override
	public OTTemplateItem findById(Long otTemplateTypeId) {
		return super.findById(OTTemplateItem.class, otTemplateTypeId);
	}

	@Override
	public void update(OTTemplateItem otTemplateItem) {
		super.update(otTemplateItem);

	}

}
