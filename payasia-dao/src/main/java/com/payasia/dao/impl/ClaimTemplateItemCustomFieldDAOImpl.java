package com.payasia.dao.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.util.NullAwareBeanUtilsBean;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.ClaimTemplateItemCustomFieldDAO;
import com.payasia.dao.bean.ClaimTemplateItemCustomField;
import com.payasia.dao.bean.ClaimTemplateItemCustomField_;

@Repository
public class ClaimTemplateItemCustomFieldDAOImpl extends BaseDAO implements
		ClaimTemplateItemCustomFieldDAO {

	@Override
	protected Object getBaseEntity() {
		ClaimTemplateItemCustomField claimTemplateItemCustomField = new ClaimTemplateItemCustomField();
		return claimTemplateItemCustomField;
	}

	@Override
	public void update(ClaimTemplateItemCustomField claimTemplateItemCustomField) {
		super.update(claimTemplateItemCustomField);

	}

	@Override
	public void save(ClaimTemplateItemCustomField claimTemplateItemCustomField) {
		super.save(claimTemplateItemCustomField);
	}

	@Override
	public void delete(ClaimTemplateItemCustomField claimTemplateItemCustomField) {
		super.delete(claimTemplateItemCustomField);

	}

	@Override
	public ClaimTemplateItemCustomField findByID(long claimTemplateItemCustomId) {
		return super.findById(ClaimTemplateItemCustomField.class,
				claimTemplateItemCustomId);
	}

	@Override
	public ClaimTemplateItemCustomField saveReturn(
			ClaimTemplateItemCustomField claimTemplateItemCustomField) {

		ClaimTemplateItemCustomField persistObj = claimTemplateItemCustomField;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (ClaimTemplateItemCustomField) getBaseEntity();
			beanUtil.copyProperties(persistObj, claimTemplateItemCustomField);
		} catch (IllegalAccessException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		} catch (InvocationTargetException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		}
		this.entityManagerFactory.persist(persistObj);
		return persistObj;
	}

	@Override
	public List<ClaimTemplateItemCustomField> findByClaimTemplateItemId(
			Long claimTemplateItemId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ClaimTemplateItemCustomField> criteriaQuery = cb
				.createQuery(ClaimTemplateItemCustomField.class);
		Root<ClaimTemplateItemCustomField> itemRoot = criteriaQuery
				.from(ClaimTemplateItemCustomField.class);
		criteriaQuery.select(itemRoot);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(
				itemRoot.get(ClaimTemplateItemCustomField_.claimTemplateItem)
						.get("claimTemplateItemId").as(Long.class),
				claimTemplateItemId));

		criteriaQuery.where(restriction);

		TypedQuery<ClaimTemplateItemCustomField> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<ClaimTemplateItemCustomField> customFieldList = typedQuery
				.getResultList();

		return customFieldList;
	}

}
