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
import com.payasia.dao.HRLetterShortlistDAO;
import com.payasia.dao.bean.CompanyBaseEntity_;
import com.payasia.dao.bean.HRLetter;
import com.payasia.dao.bean.HRLetterShortlist;
import com.payasia.dao.bean.HRLetterShortlist_;
import com.payasia.dao.bean.HRLetter_;

@Repository
public class HRLetterShortlistDAOImpl extends BaseDAO implements
		HRLetterShortlistDAO {

	@Override
	protected Object getBaseEntity() {
		HRLetterShortlist hRLetterShortlist = new HRLetterShortlist();
		return hRLetterShortlist;
	}

	@Override
	public void update(HRLetterShortlist hRLetterShortlist) {
		super.update(hRLetterShortlist);

	}

	@Override
	public void save(HRLetterShortlist hRLetterShortlist) {
		super.save(hRLetterShortlist);
	}

	@Override
	public void delete(HRLetterShortlist hRLetterShortlist) {
		super.delete(hRLetterShortlist);

	}

	@Override
	public HRLetterShortlist findByID(long hRLetterShortlistId) {
		return super.findById(HRLetterShortlist.class, hRLetterShortlistId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.CompanyDocumentShortListDAO#deleteByCondition(java.lang
	 * .Long)
	 */
	@Override
	public void deleteByCondition(Long hrLetterId) {

		String queryString = "DELETE FROM HRLetterShortlist hrs WHERE hrs.hRLetter.hrLetterId = :hrLetterId";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("hrLetterId", hrLetterId);

		q.executeUpdate();

	}

	@Override
	public List<HRLetterShortlist> findByCondition(Long hrLetterId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<HRLetterShortlist> criteriaQuery = cb
				.createQuery(HRLetterShortlist.class);
		Root<HRLetterShortlist> hrLetterShortListRoot = criteriaQuery
				.from(HRLetterShortlist.class);

		criteriaQuery.select(hrLetterShortListRoot);
		Join<HRLetterShortlist, HRLetter> hrLetterShortListJoin = hrLetterShortListRoot
				.join(HRLetterShortlist_.hRLetter);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(
				hrLetterShortListJoin.get(HRLetter_.hrLetterId), hrLetterId));

		criteriaQuery.where(restriction);

		criteriaQuery.orderBy(cb.asc(hrLetterShortListRoot
				.get(HRLetterShortlist_.short_List_ID)));

		TypedQuery<HRLetterShortlist> hrLetterTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		List<HRLetterShortlist> hrLetterShortList = hrLetterTypedQuery
				.getResultList();
		return hrLetterShortList;

	}
	
	@Override
	public List<HRLetterShortlist> findByHRLetterShortlistCondition(Long hrLetterId,Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<HRLetterShortlist> criteriaQuery = cb
				.createQuery(HRLetterShortlist.class);
		Root<HRLetterShortlist> hrLetterShortListRoot = criteriaQuery
				.from(HRLetterShortlist.class);

		criteriaQuery.select(hrLetterShortListRoot);
		Join<HRLetterShortlist, HRLetter> hrLetterShortListJoin = hrLetterShortListRoot
				.join(HRLetterShortlist_.hRLetter);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(
				hrLetterShortListJoin.get(HRLetter_.hrLetterId), hrLetterId));
		
		restriction = cb.and(restriction, cb.equal(
				hrLetterShortListRoot.get(CompanyBaseEntity_.companyId), companyId));

		criteriaQuery.where(restriction);

		criteriaQuery.orderBy(cb.asc(hrLetterShortListRoot
				.get(HRLetterShortlist_.short_List_ID)));

		TypedQuery<HRLetterShortlist> hrLetterTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		List<HRLetterShortlist> hrLetterShortList = hrLetterTypedQuery
				.getResultList();
		return hrLetterShortList;

	}

	@Override
	public HRLetterShortlist findByID(long hRLetterShortlistId, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<HRLetterShortlist> criteriaQuery = cb
				.createQuery(HRLetterShortlist.class);
		Root<HRLetterShortlist> hrLetterShortListRoot = criteriaQuery
				.from(HRLetterShortlist.class);

		criteriaQuery.select(hrLetterShortListRoot);
		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(
				hrLetterShortListRoot.get(HRLetterShortlist_.short_List_ID), hRLetterShortlistId));
		
		restriction = cb.and(restriction, cb.equal(
				hrLetterShortListRoot.get(CompanyBaseEntity_.companyId), companyId));

		criteriaQuery.where(restriction);

		criteriaQuery.orderBy(cb.asc(hrLetterShortListRoot
				.get(HRLetterShortlist_.short_List_ID)));

		TypedQuery<HRLetterShortlist> hrLetterTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		List<HRLetterShortlist> hrLetterShortList = hrLetterTypedQuery
				.getResultList();
		return hrLetterShortList.get(0);
	}

	

}
