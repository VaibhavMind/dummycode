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
import com.payasia.dao.ClaimApplicationItemCustomFieldDAO;
import com.payasia.dao.bean.ClaimApplicationItemCustomField;
import com.payasia.dao.bean.ClaimApplicationItemCustomField_;

@Repository
public class ClaimApplicationItemCustomFieldDAOImpl extends BaseDAO implements ClaimApplicationItemCustomFieldDAO {

	@Override
	protected Object getBaseEntity() {
		ClaimApplicationItemCustomField claimApplicationItemCustomField = new ClaimApplicationItemCustomField();
		return claimApplicationItemCustomField;
	}

	@Override
	public void update(ClaimApplicationItemCustomField claimApplicationItemCustomField) {
		super.update(claimApplicationItemCustomField);

	}

	@Override
	public void save(ClaimApplicationItemCustomField claimApplicationItemCustomField) {
		super.save(claimApplicationItemCustomField);
	}

	@Override
	public void delete(ClaimApplicationItemCustomField claimApplicationItemCustomField) {
		super.delete(claimApplicationItemCustomField);

	}

	@Override
	public ClaimApplicationItemCustomField findByID(long claimApplicationItemCustomFieldId) {
		return super.findById(ClaimApplicationItemCustomField.class, claimApplicationItemCustomFieldId);
	}

	@Override
	public ClaimApplicationItemCustomField saveReturn(ClaimApplicationItemCustomField claimApplicationItemCustomField) {

		ClaimApplicationItemCustomField persistObj = claimApplicationItemCustomField;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (ClaimApplicationItemCustomField) getBaseEntity();
			beanUtil.copyProperties(persistObj, claimApplicationItemCustomField);
		} catch (IllegalAccessException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		} catch (InvocationTargetException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		}
		this.entityManagerFactory.persist(persistObj);
		return persistObj;
	}

	@Override
	public List<ClaimApplicationItemCustomField> findByClaimApplicationItemID(Long claimApplicationItem) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ClaimApplicationItemCustomField> criteriaQuery = cb
				.createQuery(ClaimApplicationItemCustomField.class);
		Root<ClaimApplicationItemCustomField> claimApplicationItemCustomFieldRoot = criteriaQuery
				.from(ClaimApplicationItemCustomField.class);
		criteriaQuery.select(claimApplicationItemCustomFieldRoot);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(claimApplicationItemCustomFieldRoot.get(ClaimApplicationItemCustomField_.claimApplicationItem),
						claimApplicationItem));

		criteriaQuery.orderBy(
				cb.desc(claimApplicationItemCustomFieldRoot.get(ClaimApplicationItemCustomField_.updatedDate)));

		criteriaQuery.where(restriction);

		TypedQuery<ClaimApplicationItemCustomField> claimAppTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return claimAppTypedQuery.getResultList();
	}

}
