package com.payasia.dao.impl;

import java.lang.reflect.InvocationTargetException;

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
import com.payasia.dao.ClaimTemplateItemGeneralDAO;
import com.payasia.dao.bean.ClaimTemplateItem;
import com.payasia.dao.bean.ClaimTemplateItemGeneral;
import com.payasia.dao.bean.ClaimTemplateItemGeneral_;
import com.payasia.dao.bean.ClaimTemplateItem_;

@Repository
public class ClaimTemplateItemGeneralDAOImpl extends BaseDAO implements ClaimTemplateItemGeneralDAO {

	@Override
	protected Object getBaseEntity() {
		ClaimTemplateItemGeneral claimTemplateItemGeneral = new ClaimTemplateItemGeneral();
		return claimTemplateItemGeneral;
	}

	@Override
	public void update(ClaimTemplateItemGeneral claimTemplateItemGeneral) {
		super.update(claimTemplateItemGeneral);

	}

	@Override
	public void save(ClaimTemplateItemGeneral claimTemplateItemGeneral) {
		super.save(claimTemplateItemGeneral);
	}

	@Override
	public void delete(ClaimTemplateItemGeneral claimTemplateItemGeneral) {
		super.delete(claimTemplateItemGeneral);

	}

	@Override
	public ClaimTemplateItemGeneral findByID(long claimTemplateItemGeneralId) {
		return super.findById(ClaimTemplateItemGeneral.class, claimTemplateItemGeneralId);
	}

	@Override
	public ClaimTemplateItemGeneral saveReturn(ClaimTemplateItemGeneral claimTemplateItemGeneral) {

		ClaimTemplateItemGeneral persistObj = claimTemplateItemGeneral;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (ClaimTemplateItemGeneral) getBaseEntity();
			beanUtil.copyProperties(persistObj, claimTemplateItemGeneral);
		} catch (IllegalAccessException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		} catch (InvocationTargetException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		}
		this.entityManagerFactory.persist(persistObj);
		return persistObj;
	}

	@Override
	public ClaimTemplateItemGeneral findByItemId(String itemId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ClaimTemplateItemGeneral> criteriaQuery = cb.createQuery(ClaimTemplateItemGeneral.class);
		Root<ClaimTemplateItemGeneral> claimTemplateItemGeneralRoot = criteriaQuery
				.from(ClaimTemplateItemGeneral.class);
		criteriaQuery.select(claimTemplateItemGeneralRoot);

		Join<ClaimTemplateItemGeneral, ClaimTemplateItem> claimTemplateItemJoin = claimTemplateItemGeneralRoot
				.join(ClaimTemplateItemGeneral_.claimTemplateItem);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(claimTemplateItemJoin.get(ClaimTemplateItem_.claimTemplateItemId), itemId));

		criteriaQuery.where(restriction);

		TypedQuery<ClaimTemplateItemGeneral> claimAppTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		if (claimAppTypedQuery.getResultList().size() > 0) {
			return claimAppTypedQuery.getResultList().get(0);
		} else {
			return null;
		}

	}

}
