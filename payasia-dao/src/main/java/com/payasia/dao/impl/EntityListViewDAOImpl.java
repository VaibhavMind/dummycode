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

import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.util.NullAwareBeanUtilsBean;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.EntityListViewDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.EntityListView;
import com.payasia.dao.bean.EntityListView_;
import com.payasia.dao.bean.EntityMaster;
import com.payasia.dao.bean.EntityMaster_;

@Repository
public class EntityListViewDAOImpl extends BaseDAO implements EntityListViewDAO {

	@Override
	public List<EntityListView> findByConditionEntityAndCompanyId(
			Long companyId, Long entityId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EntityListView> criteriaQuery = cb
				.createQuery(EntityListView.class);
		Root<EntityListView> entityListViewRoot = criteriaQuery
				.from(EntityListView.class);

		criteriaQuery.select(entityListViewRoot);

		Join<EntityListView, Company> compEntityListViewJoin = entityListViewRoot
				.join(EntityListView_.company);

		Join<EntityListView, EntityMaster> entityListViewJoin = entityListViewRoot
				.join(EntityListView_.entityMaster);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(
				compEntityListViewJoin.get(Company_.companyId), companyId));

		restriction = cb.and(restriction, cb.equal(
				entityListViewJoin.get(EntityMaster_.entityId), entityId));

		criteriaQuery.where(restriction);

		TypedQuery<EntityListView> entityListViewTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<EntityListView> allEntityListViewList = entityListViewTypedQuery
				.getResultList();
		return allEntityListViewList;
	}

	@Override
	public EntityListView findById(long entityListViewId) {
		return super.findById(EntityListView.class, entityListViewId);
	}

	@Override
	protected Object getBaseEntity() {
		EntityListView entityListView = new EntityListView();
		return entityListView;
	}

	@Override
	public EntityListView save(EntityListView entityListView) {

		EntityListView persistObj = entityListView;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (EntityListView) getBaseEntity();
			beanUtil.copyProperties(persistObj, entityListView);
		} catch (IllegalAccessException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		} catch (InvocationTargetException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		}
		this.entityManagerFactory.persist(persistObj);
		return persistObj;

	}

	@Override
	public void update(EntityListView entityListView) {

		super.update(entityListView);
	}

	@Override
	public void delete(EntityListView entityListView) {
		super.delete(entityListView);
	}

	@Override
	public EntityListView findByEntityIdCompanyIdAndViewName(Long companyId,
			Long entityId, String viewName) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EntityListView> criteriaQuery = cb
				.createQuery(EntityListView.class);
		Root<EntityListView> entityListViewRoot = criteriaQuery
				.from(EntityListView.class);

		criteriaQuery.select(entityListViewRoot);

		Join<EntityListView, Company> compEntityListViewJoin = entityListViewRoot
				.join(EntityListView_.company);

		Join<EntityListView, EntityMaster> entityListViewJoin = entityListViewRoot
				.join(EntityListView_.entityMaster);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(
				compEntityListViewJoin.get(Company_.companyId), companyId));

		restriction = cb.and(restriction, cb.equal(
				entityListViewJoin.get(EntityMaster_.entityId), entityId));

		restriction = cb.and(restriction, cb.like(
				cb.upper(entityListViewRoot.get(EntityListView_.viewName)),
				viewName));

		criteriaQuery.where(restriction);

		TypedQuery<EntityListView> entityListViewTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<EntityListView> entityListViewList = entityListViewTypedQuery
				.getResultList();
		if (entityListViewList != null &&  !entityListViewList.isEmpty()) {
			return entityListViewList.get(0);
		}
		return null;
	}

	@Override
	public EntityListView findByEntityIdCompanyIdAndViewNameViewId(
			Long companyId, Long entityId, String viewName, Long viewId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EntityListView> criteriaQuery = cb
				.createQuery(EntityListView.class);
		Root<EntityListView> entityListViewRoot = criteriaQuery
				.from(EntityListView.class);

		criteriaQuery.select(entityListViewRoot);

		Join<EntityListView, Company> compEntityListViewJoin = entityListViewRoot
				.join(EntityListView_.company);

		Join<EntityListView, EntityMaster> entityListViewJoin = entityListViewRoot
				.join(EntityListView_.entityMaster);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(
				compEntityListViewJoin.get(Company_.companyId), companyId));

		restriction = cb.and(restriction, cb.equal(
				entityListViewJoin.get(EntityMaster_.entityId), entityId));

		restriction = cb.and(restriction, cb.like(
				cb.upper(entityListViewRoot.get(EntityListView_.viewName)),
				viewName));

		restriction = cb.and(restriction, cb.like(
				cb.upper(entityListViewRoot.get(EntityListView_.viewName)),
				viewName));

		restriction = cb.and(restriction, cb.notEqual(
				entityListViewRoot.get(EntityListView_.entityListViewId),
				viewId));

		criteriaQuery.where(restriction);

		TypedQuery<EntityListView> entityListViewTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<EntityListView> entityListViewList = entityListViewTypedQuery
				.getResultList();
		if (entityListViewList != null &&  !entityListViewList.isEmpty()) {
			return entityListViewList.get(0);
		}
		return null;
	}
	
	
	@Override
	public boolean isViewGridExistInCom(Long viewId, Long companyId) {
		
		
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EntityListView> criteriaQuery = cb.createQuery(EntityListView.class);
		Root<EntityListView> viewgridRoot = criteriaQuery.from(EntityListView.class);

		criteriaQuery.select(viewgridRoot);

		Predicate restriction = cb.conjunction();
		
		restriction = cb.and(restriction,cb.equal(viewgridRoot.get(EntityListView_.entityListViewId), viewId));
		
		restriction = cb.and(restriction,cb.equal(viewgridRoot.get(EntityListView_.company), companyId));
		criteriaQuery.where(restriction);

		TypedQuery<EntityListView> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<EntityListView> list = typedQuery.getResultList();
		return list!=null && !list.isEmpty()  ? true :false;
		
	}

}
