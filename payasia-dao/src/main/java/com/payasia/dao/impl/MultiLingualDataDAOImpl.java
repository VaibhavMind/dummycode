package com.payasia.dao.impl;

import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.payasia.dao.BaseDAO;
import com.payasia.dao.MultiLingualDataDAO;
import com.payasia.dao.bean.DataDictionary;
import com.payasia.dao.bean.DataDictionary_;
import com.payasia.dao.bean.MultiLingualData;
import com.payasia.dao.bean.MultiLingualData_;

/**
 * The Class MultiLingualDataDAOImpl.
 */
/**
 * @author vivekjain
 * 
 */
@Repository
public class MultiLingualDataDAOImpl extends BaseDAO implements
		MultiLingualDataDAO {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.MultiLingualDataDAO#findByDictionaryIdAndLanguage(java
	 * .lang.Long, java.lang.Long)
	 */
	@Override
	public MultiLingualData findByDictionaryIdAndLanguage(Long dictionaryId,
			Long languageId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<MultiLingualData> criteriaQuery = cb
				.createQuery(MultiLingualData.class);
		Root<MultiLingualData> multilingualRoot = criteriaQuery
				.from(MultiLingualData.class);
		criteriaQuery.select(multilingualRoot);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(
						multilingualRoot.get(MultiLingualData_.id)
								.get("data_Dictionary_ID").as(Long.class),
						dictionaryId));

		restriction = cb
				.and(restriction,
						cb.equal(multilingualRoot.get(MultiLingualData_.id)
								.get("language_ID").as(Long.class), languageId));

		criteriaQuery.where(restriction);

		TypedQuery<MultiLingualData> multilingualQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<MultiLingualData> multilingualList = multilingualQuery
				.getResultList();
		if (multilingualList != null &&  !multilingualList.isEmpty()) {
			return multilingualList.get(0);
		}
		return null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.MultiLingualDataDAO#update(com.payasia.dao.bean.
	 * MultiLingualData)
	 */
	public void update(MultiLingualData multiLingualData) {
		super.update(multiLingualData);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.MultiLingualDataDAO#delete(com.payasia.dao.bean.
	 * MultiLingualData)
	 */
	public void delete(MultiLingualData multiLingualData) {
		super.delete(multiLingualData);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.MultiLingualDataDAO#save(com.payasia.dao.bean.
	 * MultiLingualData)
	 */
	public void save(MultiLingualData multiLingualData) {
		super.save(multiLingualData);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.BaseDAO#getBaseEntity()
	 */
	@Override
	protected Object getBaseEntity() {
		MultiLingualData multiLingualData = new MultiLingualData();
		return multiLingualData;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.MultiLingualDataDAO#findByEntityIdCompanyIdAndDictionaryName
	 * (java.lang.Long, java.lang.Long, java.lang.Long, java.lang.Long,
	 * java.lang.String)
	 */
	@Override
	public MultiLingualData findByEntityIdCompanyIdAndDictionaryName(
			Long dictionaryId, Long languageId, Long companyId, Long enityId,
			String dataDictionaryName) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<MultiLingualData> criteriaQuery = cb
				.createQuery(MultiLingualData.class);
		Root<MultiLingualData> multilingualRoot = criteriaQuery
				.from(MultiLingualData.class);
		criteriaQuery.select(multilingualRoot);

		Join<MultiLingualData, DataDictionary> multiLingualDataDataDictionaryJoin = multilingualRoot
				.join(MultiLingualData_.dataDictionary);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(
				restriction,
				cb.equal(
						multiLingualDataDataDictionaryJoin
								.get(DataDictionary_.company).get("companyId")
								.as(Long.class), companyId));

		restriction = cb.and(
				restriction,
				cb.equal(
						multiLingualDataDataDictionaryJoin
								.get(DataDictionary_.entityMaster)
								.get("entityId").as(Long.class), enityId));

		restriction = cb
				.and(restriction,
						cb.equal(multilingualRoot.get(MultiLingualData_.id)
								.get("language_ID").as(Long.class), languageId));

		restriction = cb
				.and(restriction, cb.equal(multiLingualDataDataDictionaryJoin
						.get(DataDictionary_.dataDictName), dataDictionaryName));

		criteriaQuery.where(restriction);

		TypedQuery<MultiLingualData> multilingualQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<MultiLingualData> multilingualList = multilingualQuery
				.getResultList();
		if (multilingualList != null &&  !multilingualList.isEmpty()) {
			return multilingualList.get(0);
		}
		return null;

	}

	@Override
	public List<MultiLingualData> findByLanguageEntityCompany(Long languageId,
			Long companyId, Long entityId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<MultiLingualData> criteriaQuery = cb
				.createQuery(MultiLingualData.class);
		Root<MultiLingualData> multilingualRoot = criteriaQuery
				.from(MultiLingualData.class);
		criteriaQuery.select(multilingualRoot);

		Join<MultiLingualData, DataDictionary> multiLingualDataDataDictionaryJoin = multilingualRoot
				.join(MultiLingualData_.dataDictionary);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(
				restriction,
				cb.equal(
						multiLingualDataDataDictionaryJoin
								.get(DataDictionary_.company).get("companyId")
								.as(Long.class), companyId));
		if (entityId != null) {
			restriction = cb.and(
					restriction,
					cb.equal(
							multiLingualDataDataDictionaryJoin
									.get(DataDictionary_.entityMaster)
									.get("entityId").as(Long.class), entityId));
		}

		restriction = cb
				.and(restriction,
						cb.equal(multilingualRoot.get(MultiLingualData_.id)
								.get("language_ID").as(Long.class), languageId));

		criteriaQuery.where(restriction);

		TypedQuery<MultiLingualData> multilingualQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return multilingualQuery.getResultList();

	}

	@Override
	public MultiLingualData findByDictionaryIdCompanyAndLanguage(Long dictionaryId, Long languageId, Long companyId) {
		
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<MultiLingualData> criteriaQuery = cb.createQuery(MultiLingualData.class);
		Root<MultiLingualData> multilingualRoot = criteriaQuery.from(MultiLingualData.class);
		criteriaQuery.select(multilingualRoot);
		
		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(multilingualRoot.get(MultiLingualData_.id).get("data_Dictionary_ID").as(Long.class), dictionaryId));

		restriction = cb.and(restriction,cb.equal(multilingualRoot.get(MultiLingualData_.id).get("language_ID").as(Long.class), languageId));

		restriction = cb.and(restriction, cb.equal(multilingualRoot.get(MultiLingualData_.companyId), companyId));

		criteriaQuery.where(restriction);

		TypedQuery<MultiLingualData> multilingualQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<MultiLingualData> multilingualList = multilingualQuery.getResultList();
		if (multilingualList != null && !multilingualList.isEmpty()) {
			return multilingualList.get(0);
		}
		return null;

	}

}
