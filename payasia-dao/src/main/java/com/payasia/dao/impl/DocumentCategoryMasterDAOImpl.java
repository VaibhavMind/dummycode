package com.payasia.dao.impl;

import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.payasia.dao.BaseDAO;
import com.payasia.dao.DocumentCategoryMasterDAO;
import com.payasia.dao.bean.DocumentCategoryMaster;
import com.payasia.dao.bean.DocumentCategoryMaster_;

/**
 * The Class DocumentCategoryMasterDAOImpl.
 */
/**
 * @author vivekjain
 * 
 */
@Repository
public class DocumentCategoryMasterDAOImpl extends BaseDAO implements
		DocumentCategoryMasterDAO {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.DocumentCategoryMasterDAO#findAll()
	 */
	@Override
	public List<DocumentCategoryMaster> findAll() {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<DocumentCategoryMaster> criteriaQuery = cb
				.createQuery(DocumentCategoryMaster.class);
		Root<DocumentCategoryMaster> documentCategoryRoot = criteriaQuery
				.from(DocumentCategoryMaster.class);

		criteriaQuery.select(documentCategoryRoot);

		criteriaQuery.orderBy(cb.asc(documentCategoryRoot
				.get(DocumentCategoryMaster_.documentCategoryId)));

		TypedQuery<DocumentCategoryMaster> documentMaster = entityManagerFactory
				.createQuery(criteriaQuery);

		List<DocumentCategoryMaster> categoryList = documentMaster
				.getResultList();
		return categoryList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.DocumentCategoryMasterDAO#findByCondition(java.lang.String
	 * )
	 */
	@Override
	public DocumentCategoryMaster findByCondition(String entityName) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<DocumentCategoryMaster> criteriaQuery = cb
				.createQuery(DocumentCategoryMaster.class);
		Root<DocumentCategoryMaster> documentCategoryMasterRoot = criteriaQuery
				.from(DocumentCategoryMaster.class);

		criteriaQuery.select(documentCategoryMasterRoot);

		criteriaQuery.where(cb.equal(cb.upper(documentCategoryMasterRoot
				.get(DocumentCategoryMaster_.categoryName)), entityName
				.toUpperCase()));

		TypedQuery<DocumentCategoryMaster> documentCategoryMasterTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		DocumentCategoryMaster documentCategoryMaster = documentCategoryMasterTypedQuery
				.getSingleResult();

		return documentCategoryMaster;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.BaseDAO#getBaseEntity()
	 */
	@Override
	protected Object getBaseEntity() {
		DocumentCategoryMaster documentCategoryMaster = new DocumentCategoryMaster();
		return documentCategoryMaster;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.DocumentCategoryMasterDAO#findById(long)
	 */
	@Override
	public DocumentCategoryMaster findById(long docId) {
		return super.findById(DocumentCategoryMaster.class, docId);
	}

}
