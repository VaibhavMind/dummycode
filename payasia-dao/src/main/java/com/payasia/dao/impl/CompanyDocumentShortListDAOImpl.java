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
import com.payasia.dao.CompanyDocumentShortListDAO;
import com.payasia.dao.bean.CompanyDocument;
import com.payasia.dao.bean.CompanyDocumentShortList;
import com.payasia.dao.bean.CompanyDocumentShortList_;
import com.payasia.dao.bean.CompanyDocument_;

/**
 * The Class CompanyDocumentShortListDAOImpl.
 */
/**
 * @author vivekjain
 * 
 */
@Repository
public class CompanyDocumentShortListDAOImpl extends BaseDAO implements
		CompanyDocumentShortListDAO {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.BaseDAO#getBaseEntity()
	 */
	@Override
	protected Object getBaseEntity() {
		CompanyDocumentShortList companyDocumentShortList = new CompanyDocumentShortList();
		return companyDocumentShortList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.CompanyDocumentShortListDAO#findByCondition(java.lang
	 * .Long)
	 */
	@Override
	public List<CompanyDocumentShortList> findByCondition(Long documentId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CompanyDocumentShortList> criteriaQuery = cb
				.createQuery(CompanyDocumentShortList.class);
		Root<CompanyDocumentShortList> companyDocumentShortListRoot = criteriaQuery
				.from(CompanyDocumentShortList.class);

		criteriaQuery.select(companyDocumentShortListRoot);
		Join<CompanyDocumentShortList, CompanyDocument> compDocShortListJoin = companyDocumentShortListRoot
				.join(CompanyDocumentShortList_.companyDocument);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(
				compDocShortListJoin.get(CompanyDocument_.documentId),
				documentId));

		criteriaQuery.where(restriction);

		criteriaQuery.orderBy(cb.asc(companyDocumentShortListRoot
				.get(CompanyDocumentShortList_.shortListId)));

		TypedQuery<CompanyDocumentShortList> compDocTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		List<CompanyDocumentShortList> companyDocumentShortList = compDocTypedQuery
				.getResultList();
		return companyDocumentShortList;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.CompanyDocumentShortListDAO#findById(long)
	 */
	@Override
	public CompanyDocumentShortList findById(long companyDocumentShortListId) {

		return super.findById(CompanyDocumentShortList.class,
				companyDocumentShortListId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.CompanyDocumentShortListDAO#update(com.payasia.dao.bean
	 * .CompanyDocumentShortList)
	 */
	@Override
	public void update(CompanyDocumentShortList companyDocumentShortList) {

		super.update(companyDocumentShortList);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.CompanyDocumentShortListDAO#delete(com.payasia.dao.bean
	 * .CompanyDocumentShortList)
	 */
	@Override
	public void delete(CompanyDocumentShortList companyDocumentShortList) {
		super.delete(companyDocumentShortList);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.CompanyDocumentShortListDAO#deleteByCondition(java.lang
	 * .Long)
	 */
	@Override
	public void deleteByCondition(Long documentId) {

		String queryString = "DELETE FROM CompanyDocumentShortList c WHERE c.companyDocument.documentId = :documentId";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("documentId", documentId);

		q.executeUpdate();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.CompanyDocumentShortListDAO#save(com.payasia.dao.bean
	 * .CompanyDocumentShortList)
	 */
	@Override
	public void save(CompanyDocumentShortList companyDocumentShortList) {
		super.save(companyDocumentShortList);

	}

}
