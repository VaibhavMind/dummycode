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
import com.payasia.dao.ClaimTemplateItemShortlistDAO;
import com.payasia.dao.bean.ClaimTemplateItem;
import com.payasia.dao.bean.ClaimTemplateItemShortlist;
import com.payasia.dao.bean.ClaimTemplateItemShortlist_;
import com.payasia.dao.bean.ClaimTemplateItem_;
import com.payasia.dao.bean.CompanyBaseEntity_;

@Repository
public class ClaimTemplateItemShortlistDAOImpl extends BaseDAO implements ClaimTemplateItemShortlistDAO {

	@Override
	protected Object getBaseEntity() {
		ClaimTemplateItemShortlist claimTemplateItemShortlist = new ClaimTemplateItemShortlist();
		return claimTemplateItemShortlist;
	}

	@Override
	public void save(ClaimTemplateItemShortlist claimTemplateItemShortlist) {
		super.save(claimTemplateItemShortlist);
	}

	@Override
	public void delete(ClaimTemplateItemShortlist claimTemplateItemShortlist) {
		super.delete(claimTemplateItemShortlist);
	}

	@Override
	public void deleteByCondition(Long claimTemplateItemId) {

		String queryString = "DELETE FROM ClaimTemplateItemShortlist c WHERE c.claimTemplateItem.claimTemplateItemId = :claimTemplateItemId";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("claimTemplateItemId", claimTemplateItemId);

		q.executeUpdate();
	}

	@Override
	public ClaimTemplateItemShortlist findByID(long claimTemplateItemId) {
		return super.findById(ClaimTemplateItemShortlist.class, claimTemplateItemId);
	}

	@Override
	public List<ClaimTemplateItemShortlist> findByCondition(Long claimTemplateItemId,Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ClaimTemplateItemShortlist> criteriaQuery = cb.createQuery(ClaimTemplateItemShortlist.class);
		Root<ClaimTemplateItemShortlist> claimTemplateItemShortlistRoot = criteriaQuery
				.from(ClaimTemplateItemShortlist.class);
		criteriaQuery.select(claimTemplateItemShortlistRoot);
		Join<ClaimTemplateItemShortlist, ClaimTemplateItem> claimTemplateItemJoin = claimTemplateItemShortlistRoot
				.join(ClaimTemplateItemShortlist_.claimTemplateItem);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(claimTemplateItemShortlistRoot.get(CompanyBaseEntity_.companyId), companyId));

		restriction = cb.and(restriction,
				cb.equal(claimTemplateItemJoin.get(ClaimTemplateItem_.claimTemplateItemId), claimTemplateItemId));

		criteriaQuery.where(restriction);

		criteriaQuery.orderBy(cb.asc(claimTemplateItemShortlistRoot.get(ClaimTemplateItemShortlist_.short_List_ID)));

		TypedQuery<ClaimTemplateItemShortlist> typedQuery = entityManagerFactory.createQuery(criteriaQuery);
		List<ClaimTemplateItemShortlist> claimTemplateItemShortlist = typedQuery.getResultList();
		return claimTemplateItemShortlist;

	}

	@Override
	public ClaimTemplateItemShortlist findByClaimTemplateItemShortlistID(Long filterId, Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ClaimTemplateItemShortlist> criteriaQuery = cb.createQuery(ClaimTemplateItemShortlist.class);
		Root<ClaimTemplateItemShortlist> claimTemplateItemShortlistRoot = criteriaQuery
				.from(ClaimTemplateItemShortlist.class);
		criteriaQuery.select(claimTemplateItemShortlistRoot);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(claimTemplateItemShortlistRoot.get(ClaimTemplateItemShortlist_.short_List_ID), filterId));
		restriction = cb.and(restriction,
				cb.equal(claimTemplateItemShortlistRoot.get(CompanyBaseEntity_.companyId), companyId));

		criteriaQuery.where(restriction);

		TypedQuery<ClaimTemplateItemShortlist> typedQuery = entityManagerFactory.createQuery(criteriaQuery);
		List<ClaimTemplateItemShortlist> claimTemplateItemShortlist = typedQuery.getResultList();
		if(claimTemplateItemShortlist!=null && !claimTemplateItemShortlist.isEmpty()){
			return claimTemplateItemShortlist.get(0);
		}
		return null;
	}

}
