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

import com.payasia.common.dto.AssignClaimTemplateConditionDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.util.NullAwareBeanUtilsBean;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.ClaimTemplateItemDAO;
import com.payasia.dao.bean.ClaimItemMaster;
import com.payasia.dao.bean.ClaimItemMaster_;
import com.payasia.dao.bean.ClaimTemplate;
import com.payasia.dao.bean.ClaimTemplateItem;
import com.payasia.dao.bean.ClaimTemplateItem_;
import com.payasia.dao.bean.ClaimTemplate_;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.CompanyBaseEntity_;
import com.payasia.dao.bean.Company_;

@Repository
public class ClaimTemplateItemDAOImpl extends BaseDAO implements ClaimTemplateItemDAO {

	@Override
	protected Object getBaseEntity() {
		ClaimTemplateItem claimTemplateItem = new ClaimTemplateItem();
		return claimTemplateItem;
	}

	@Override
	public void save(ClaimTemplateItem claimTemplateItem) {
		super.save(claimTemplateItem);

	}

	@Override
	public ClaimTemplateItem saveReturn(ClaimTemplateItem claimTemplateItem) {

		ClaimTemplateItem persistObj = claimTemplateItem;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (ClaimTemplateItem) getBaseEntity();
			beanUtil.copyProperties(persistObj, claimTemplateItem);
		} catch (IllegalAccessException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		} catch (InvocationTargetException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		}
		this.entityManagerFactory.persist(persistObj);
		return persistObj;
	}

	@Override
	public List<ClaimTemplateItem> findByCondition(List<Long> multipleClaimTemplateIdList, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ClaimTemplateItem> criteriaQuery = cb.createQuery(ClaimTemplateItem.class);
		Root<ClaimTemplateItem> claimTempItemRoot = criteriaQuery.from(ClaimTemplateItem.class);

		criteriaQuery.select(claimTempItemRoot);

		Join<ClaimTemplateItem, ClaimTemplate> claimTempTypeJoin = claimTempItemRoot
				.join(ClaimTemplateItem_.claimTemplate);

		Join<ClaimTemplate, Company> claimTempCompanyTypeJoin = claimTempTypeJoin.join(ClaimTemplate_.company);

		Join<ClaimTemplateItem, ClaimItemMaster> claimTempItemTypeJoin = claimTempItemRoot
				.join(ClaimTemplateItem_.claimItemMaster);
		Join<ClaimItemMaster, Company> claimItemCompanyJoin = claimTempItemTypeJoin.join(ClaimItemMaster_.company);

		Predicate restriction = cb.conjunction();
		if (multipleClaimTemplateIdList.size() > 0) {
			restriction = cb.and(restriction,
					claimTempTypeJoin.get(ClaimTemplate_.claimTemplateId).in(multipleClaimTemplateIdList));
		}

		restriction = cb.and(restriction, cb.equal(claimItemCompanyJoin.get(Company_.companyId), companyId));

		restriction = cb.and(restriction, cb.equal(claimTempCompanyTypeJoin.get(Company_.companyId), companyId));

		criteriaQuery.where(restriction);

		criteriaQuery.orderBy(cb.asc(claimTempItemTypeJoin.get(ClaimItemMaster_.sortOrder)));

		TypedQuery<ClaimTemplateItem> claimTemplateItemTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<ClaimTemplateItem> claimTemplateItemList = claimTemplateItemTypedQuery.getResultList();

		return claimTemplateItemList;
	}

	@Override
	public List<ClaimTemplateItem> findByCondition(Long claimTemplateId, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ClaimTemplateItem> criteriaQuery = cb.createQuery(ClaimTemplateItem.class);
		Root<ClaimTemplateItem> claimTempItemRoot = criteriaQuery.from(ClaimTemplateItem.class);

		criteriaQuery.select(claimTempItemRoot);

		Join<ClaimTemplateItem, ClaimTemplate> claimTempTypeJoin = claimTempItemRoot
				.join(ClaimTemplateItem_.claimTemplate);

		Join<ClaimTemplate, Company> claimTempCompanyTypeJoin = claimTempTypeJoin.join(ClaimTemplate_.company);

		Join<ClaimTemplateItem, ClaimItemMaster> claimTempItemTypeJoin = claimTempItemRoot
				.join(ClaimTemplateItem_.claimItemMaster);
		Join<ClaimItemMaster, Company> claimItemCompanyJoin = claimTempItemTypeJoin.join(ClaimItemMaster_.company);

		Predicate restriction = cb.conjunction();
		if (claimTemplateId != 0) {
			restriction = cb.and(restriction,
					cb.equal(claimTempTypeJoin.get(ClaimTemplate_.claimTemplateId), claimTemplateId));
		}

		restriction = cb.and(restriction, cb.equal(claimItemCompanyJoin.get(Company_.companyId), companyId));

		restriction = cb.and(restriction, cb.equal(claimTempCompanyTypeJoin.get(Company_.companyId), companyId));

		criteriaQuery.where(restriction);

		criteriaQuery.orderBy(cb.asc(claimTempItemTypeJoin.get(ClaimItemMaster_.sortOrder)));

		TypedQuery<ClaimTemplateItem> claimTemplateItemTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<ClaimTemplateItem> claimTemplateItemList = claimTemplateItemTypedQuery.getResultList();

		return claimTemplateItemList;
	}

	@Override
	public void update(ClaimTemplateItem claimTemplateItem) {
		super.update(claimTemplateItem);

	}

	@Override
	public ClaimTemplateItem findById(Long claimTemplateTypeId) {

		return super.findById(ClaimTemplateItem.class, claimTemplateTypeId);
	}

	@Override
	public List<ClaimTemplateItem> findByTemplateIdCompanyId(Long claimTemplateId, Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ClaimTemplateItem> criteriaQuery = cb.createQuery(ClaimTemplateItem.class);
		Root<ClaimTemplateItem> claimTempItemRoot = criteriaQuery.from(ClaimTemplateItem.class);

		criteriaQuery.select(claimTempItemRoot);

		Join<ClaimTemplateItem, ClaimTemplate> claimTempTypeJoin = claimTempItemRoot
				.join(ClaimTemplateItem_.claimTemplate);

		Join<ClaimTemplate, Company> claimTempCompanyTypeJoin = claimTempTypeJoin.join(ClaimTemplate_.company);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction,
				cb.equal(claimTempTypeJoin.get(ClaimTemplate_.claimTemplateId), claimTemplateId));

		restriction = cb.and(restriction, cb.equal(claimTempCompanyTypeJoin.get(Company_.companyId), companyId));

		criteriaQuery.where(restriction);

		TypedQuery<ClaimTemplateItem> claimTemplateItemTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<ClaimTemplateItem> claimTemplateItemList = claimTemplateItemTypedQuery.getResultList();

		return claimTemplateItemList;

	}

	@Override
	public Long findByConditionCount(Long claimTemplateId, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
		Root<ClaimTemplateItem> claimItemRoot = criteriaQuery.from(ClaimTemplateItem.class);

		criteriaQuery.select(cb.count(claimItemRoot));

		Join<ClaimTemplateItem, ClaimTemplate> claimTempTypeJoin = claimItemRoot.join(ClaimTemplateItem_.claimTemplate);

		Join<ClaimTemplate, Company> claimTempCompanyTypeJoin = claimTempTypeJoin.join(ClaimTemplate_.company);

		Join<ClaimTemplateItem, ClaimItemMaster> claimTempItemTypeJoin = claimItemRoot
				.join(ClaimTemplateItem_.claimItemMaster);
		Join<ClaimItemMaster, Company> claimItemCompanyJoin = claimTempItemTypeJoin.join(ClaimItemMaster_.company);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction,
				cb.equal(claimTempTypeJoin.get(ClaimTemplate_.claimTemplateId), claimTemplateId));

		restriction = cb.and(restriction, cb.equal(claimItemCompanyJoin.get(Company_.companyId), companyId));

		restriction = cb.and(restriction, cb.equal(claimTempCompanyTypeJoin.get(Company_.companyId), companyId));

		criteriaQuery.where(restriction);

		TypedQuery<Long> claimItemDefinitionTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		return claimItemDefinitionTypedQuery.getSingleResult();

	}

	@Override
	public void delete(ClaimTemplateItem claimTemplateItem) {
		super.delete(claimTemplateItem);

	}

	@Override
	public List<ClaimTemplateItem> findByCondition(AssignClaimTemplateConditionDTO assignClaimTemplateConditionDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ClaimTemplateItem> criteriaQuery = cb.createQuery(ClaimTemplateItem.class);
		Root<ClaimTemplateItem> claimTemplateItemRoot = criteriaQuery.from(ClaimTemplateItem.class);

		criteriaQuery.select(claimTemplateItemRoot);

		Join<ClaimTemplateItem, ClaimTemplate> claimTemplateJoin = claimTemplateItemRoot
				.join(ClaimTemplateItem_.claimTemplate);

		Join<ClaimTemplate, Company> companyJoin = claimTemplateJoin.join(ClaimTemplate_.company);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction,
				cb.equal(companyJoin.get(Company_.companyId), assignClaimTemplateConditionDTO.getCompanyId()));
		restriction = cb.and(restriction, cb.equal(claimTemplateJoin.get(ClaimTemplate_.visibility), true));

		criteriaQuery.where(restriction);
		criteriaQuery.orderBy(cb.asc(claimTemplateJoin.get(ClaimTemplate_.templateName)));

		TypedQuery<ClaimTemplateItem> typedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<ClaimTemplateItem> claimTemplateItemList = typedQuery.getResultList();

		return claimTemplateItemList;
	}

	@Override
	public List<ClaimTemplateItem> findByCompanyId(Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ClaimTemplateItem> criteriaQuery = cb.createQuery(ClaimTemplateItem.class);
		Root<ClaimTemplateItem> claimTempItemRoot = criteriaQuery.from(ClaimTemplateItem.class);

		criteriaQuery.select(claimTempItemRoot);

		Join<ClaimTemplateItem, ClaimTemplate> claimTempTypeJoin = claimTempItemRoot
				.join(ClaimTemplateItem_.claimTemplate);

		Join<ClaimTemplate, Company> claimTempCompanyTypeJoin = claimTempTypeJoin.join(ClaimTemplate_.company);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(claimTempCompanyTypeJoin.get(Company_.companyId), companyId));

		criteriaQuery.where(restriction);

		TypedQuery<ClaimTemplateItem> claimTemplateItemTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<ClaimTemplateItem> claimTemplateItemList = claimTemplateItemTypedQuery.getResultList();

		return claimTemplateItemList;

	}

	@Override
	public ClaimTemplateItem findByClaimTemplateItemId(Long claimTemplateItemId, Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ClaimTemplateItem> criteriaQuery = cb.createQuery(ClaimTemplateItem.class);
		Root<ClaimTemplateItem> claimTempItemRoot = criteriaQuery.from(ClaimTemplateItem.class);

		criteriaQuery.select(claimTempItemRoot);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(claimTempItemRoot.get(ClaimTemplateItem_.claimTemplateItemId), claimTemplateItemId));

		restriction = cb.and(restriction,
				cb.equal(claimTempItemRoot.get(CompanyBaseEntity_.companyId), companyId));

		criteriaQuery.where(restriction);

		TypedQuery<ClaimTemplateItem> claimTemplateItemTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<ClaimTemplateItem> claimTemplateItemList = claimTemplateItemTypedQuery.getResultList();
		
		if(claimTemplateItemList!=null && !claimTemplateItemList.isEmpty()){
			return claimTemplateItemList.get(0);
		}

		return null;
	}

}
