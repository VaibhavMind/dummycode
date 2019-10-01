package com.payasia.dao.impl;

import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.payasia.dao.BaseDAO;
import com.payasia.dao.EntityListViewFieldDAO;
import com.payasia.dao.bean.EntityListView;
import com.payasia.dao.bean.EntityListViewField;
import com.payasia.dao.bean.EntityListViewField_;
import com.payasia.dao.bean.EntityListView_;

@Repository
public class EntityListViewFieldDAOImpl extends BaseDAO implements
		EntityListViewFieldDAO {

	@Override
	public List<EntityListViewField> findAll() {
		return null;

	}

	@Override
	public List<EntityListViewField> findByEntityListViewId(
			long entityListViewId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EntityListViewField> criteriaQuery = cb
				.createQuery(EntityListViewField.class);
		Root<EntityListViewField> entityListViewFieldRoot = criteriaQuery
				.from(EntityListViewField.class);

		criteriaQuery.select(entityListViewFieldRoot);

		Join<EntityListViewField, EntityListView> entityListViewJoin = entityListViewFieldRoot
				.join(EntityListViewField_.entityListView);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(
				entityListViewJoin.get(EntityListView_.entityListViewId),
				entityListViewId));

		criteriaQuery.where(restriction);

		TypedQuery<EntityListViewField> entityListViewFieldTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<EntityListViewField> allEntityListViewFieldList = entityListViewFieldTypedQuery
				.getResultList();

		return allEntityListViewFieldList;
	}

	@Override
	public void save(EntityListViewField entityListViewField) {
		super.save(entityListViewField);

	}

	@Override
	protected Object getBaseEntity() {
		EntityListViewField entityListViewField = new EntityListViewField();
		return entityListViewField;
	}

	@Override
	public List<Long> findViewIDs() {
		 
		return null;
	}

	@Override
	public void deleteByCondition(Long viewId) {

		String queryString = "DELETE FROM EntityListViewField e WHERE e.entityListView.entityListViewId = :viewId ";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("viewId", viewId);

		q.executeUpdate();
	}

	@Override
	public List<EntityListViewField> findByEntityListViewId(long entityListViewId, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EntityListViewField> criteriaQuery = cb
				.createQuery(EntityListViewField.class);
		Root<EntityListViewField> entityListViewFieldRoot = criteriaQuery
				.from(EntityListViewField.class);

		criteriaQuery.select(entityListViewFieldRoot);

		Join<EntityListViewField, EntityListView> entityListViewJoin = entityListViewFieldRoot
				.join(EntityListViewField_.entityListView);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(
				entityListViewJoin.get(EntityListView_.entityListViewId),
				entityListViewId));
		
		restriction = cb.and(restriction, cb.equal(
				entityListViewFieldRoot.get(EntityListViewField_.companyId),
				companyId));

		criteriaQuery.where(restriction);

		TypedQuery<EntityListViewField> entityListViewFieldTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<EntityListViewField> allEntityListViewFieldList = entityListViewFieldTypedQuery
				.getResultList();

		return allEntityListViewFieldList;
	}

}
