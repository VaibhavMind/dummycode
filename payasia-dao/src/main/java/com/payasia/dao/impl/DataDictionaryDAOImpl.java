/**
 * @author abhisheksachdeva
 *
 */
package com.payasia.dao.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.NullAwareBeanUtilsBean;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.SortConstants;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.DataDictionaryDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.DataDictionary;
import com.payasia.dao.bean.DataDictionary_;
import com.payasia.dao.bean.EntityMaster;
import com.payasia.dao.bean.EntityMaster_;

/**
 * The Class DataDictionaryDAOImpl.
 */
@Repository
public class DataDictionaryDAOImpl extends BaseDAO implements DataDictionaryDAO {

	private static final Logger LOGGER = Logger
			.getLogger(DataDictionaryDAOImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.DataDictionaryDAO#findById(long)
	 */
	@Override
	public DataDictionary findById(long dataDictionaryId) {
		return super.findById(DataDictionary.class, dataDictionaryId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.DataDictionaryDAO#save(com.payasia.dao.bean.DataDictionary
	 * )
	 */
	@Override
	public void save(DataDictionary dataDictionary) {
		super.save(dataDictionary);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.DataDictionaryDAO#saveReturn(com.payasia.dao.bean.
	 * DataDictionary)
	 */
	@Override
	public DataDictionary saveReturn(DataDictionary saveObj) {
		DataDictionary persistObj = saveObj;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (DataDictionary) getBaseEntity();
			beanUtil.copyProperties(persistObj, saveObj);
		} catch (IllegalAccessException illegalAccessException) {
			LOGGER.error(illegalAccessException.getMessage(),
					illegalAccessException);
			throw new PayAsiaSystemException("errors.dao",
					illegalAccessException);
		} catch (InvocationTargetException invocationTargetException) {
			LOGGER.error(invocationTargetException.getMessage(),
					invocationTargetException);
			throw new PayAsiaSystemException("errors.dao",
					invocationTargetException);
		}
		this.entityManagerFactory.persist(persistObj);
		return persistObj;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.BaseDAO#getBaseEntity()
	 */
	@Override
	protected Object getBaseEntity() {
		DataDictionary dataDictionary = new DataDictionary();
		return dataDictionary;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.DataDictionaryDAO#findByConditionEntityAndCompanyId(java
	 * .lang.Long, java.lang.Long, java.lang.String)
	 */
	@Override
	public List<DataDictionary> findByConditionEntityAndCompanyId(
			Long companyId, Long entityId, String fieldType) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<DataDictionary> criteriaQuery = cb
				.createQuery(DataDictionary.class);
		Root<DataDictionary> dataDictionaryRoot = criteriaQuery
				.from(DataDictionary.class);

		criteriaQuery.select(dataDictionaryRoot);

		Join<DataDictionary, Company> compDataDictJoin = dataDictionaryRoot
				.join(DataDictionary_.company, JoinType.LEFT);

		Join<DataDictionary, EntityMaster> DataDictEntityJoin = dataDictionaryRoot
				.join(DataDictionary_.entityMaster);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.or(
				cb.equal(compDataDictJoin.get(Company_.companyId), companyId),
				cb.isNull(compDataDictJoin.get(Company_.companyId))));

		restriction = cb.and(restriction, cb.equal(
				DataDictEntityJoin.get(EntityMaster_.entityId), entityId));
		if (StringUtils.isNotBlank(fieldType)) {
			restriction = cb.and(restriction, cb.equal(
					dataDictionaryRoot.get(DataDictionary_.fieldType),
					fieldType));
		}

		restriction = cb.and(restriction, cb.equal(
				dataDictionaryRoot.get(DataDictionary_.dataType),
				PayAsiaConstants.PAYASIA_DATA_DICTIONARY_DATA_TYPE_FIELD));

		criteriaQuery.where(restriction);

		TypedQuery<DataDictionary> dataDictionaryTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<DataDictionary> alldataDictionaryList = dataDictionaryTypedQuery
				.getResultList();
		return alldataDictionaryList;
	}

	@Override
	public List<DataDictionary> findByConditionEntityAndCompanyIdAndFormula(
			Long companyId, Long entityId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<DataDictionary> criteriaQuery = cb
				.createQuery(DataDictionary.class);
		Root<DataDictionary> dataDictionaryRoot = criteriaQuery
				.from(DataDictionary.class);

		criteriaQuery.select(dataDictionaryRoot);

		Join<DataDictionary, Company> compDataDictJoin = dataDictionaryRoot
				.join(DataDictionary_.company, JoinType.LEFT);

		Join<DataDictionary, EntityMaster> DataDictEntityJoin = dataDictionaryRoot
				.join(DataDictionary_.entityMaster);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.or(
				cb.equal(compDataDictJoin.get(Company_.companyId), companyId),
				cb.isNull(compDataDictJoin.get(Company_.companyId))));

		restriction = cb.and(restriction, cb.equal(
				DataDictEntityJoin.get(EntityMaster_.entityId), entityId));
		restriction = cb
				.and(restriction,
						cb.or(cb.equal(
								dataDictionaryRoot
										.get(DataDictionary_.dataType),
								PayAsiaConstants.PAYASIA_DATA_DICTIONARY_DATA_TYPE_FIELD),
								cb.equal(
										dataDictionaryRoot
												.get(DataDictionary_.dataType),
										PayAsiaConstants.PAYASIA_DATA_DICTIONARY_DATA_TYPE_FORMULA)));

		criteriaQuery.where(restriction);

		TypedQuery<DataDictionary> dataDictionaryTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<DataDictionary> alldataDictionaryList = dataDictionaryTypedQuery
				.getResultList();
		return alldataDictionaryList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.DataDictionaryDAO#findByConditionImportable(java.lang
	 * .Long, java.lang.Long, java.lang.String, java.lang.Boolean)
	 */
	@Override
	public List<DataDictionary> findByConditionImportable(Long companyId,
			Long entityId, String fieldType, Boolean importable) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<DataDictionary> criteriaQuery = cb
				.createQuery(DataDictionary.class);
		Root<DataDictionary> dataDictionaryRoot = criteriaQuery
				.from(DataDictionary.class);

		criteriaQuery.select(dataDictionaryRoot);

		Join<DataDictionary, Company> compDataDictJoin = dataDictionaryRoot
				.join(DataDictionary_.company, JoinType.LEFT);

		Join<DataDictionary, EntityMaster> DataDictEntityJoin = dataDictionaryRoot
				.join(DataDictionary_.entityMaster);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.or(
				cb.equal(compDataDictJoin.get(Company_.companyId), companyId),
				cb.isNull(compDataDictJoin.get(Company_.companyId))));

		restriction = cb.and(restriction, cb.equal(
				DataDictEntityJoin.get(EntityMaster_.entityId), entityId));
		if (StringUtils.isNotBlank(fieldType)) {
			restriction = cb.and(restriction, cb.equal(
					dataDictionaryRoot.get(DataDictionary_.fieldType),
					fieldType));
		}

		if (importable != null) {
			restriction = cb.and(restriction, cb.equal(
					dataDictionaryRoot.get(DataDictionary_.importable),
					importable));
		}
		restriction = cb.and(restriction, cb.equal(
				dataDictionaryRoot.get(DataDictionary_.dataType),
				PayAsiaConstants.PAYASIA_DATA_DICTIONARY_DATA_TYPE_FIELD));
		criteriaQuery.where(restriction);

		TypedQuery<DataDictionary> dataDictionaryTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<DataDictionary> alldataDictionaryList = dataDictionaryTypedQuery
				.getResultList();
		return alldataDictionaryList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.DataDictionaryDAO#findByConditionEntity(java.lang.Long,
	 * java.lang.String)
	 */
	@Override
	public List<DataDictionary> findByConditionEntity(Long entityId,
			String fieldType) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<DataDictionary> criteriaQuery = cb
				.createQuery(DataDictionary.class);
		Root<DataDictionary> dataDictionaryRoot = criteriaQuery
				.from(DataDictionary.class);

		criteriaQuery.select(dataDictionaryRoot);

		Join<DataDictionary, EntityMaster> DataDictEntityJoin = dataDictionaryRoot
				.join(DataDictionary_.entityMaster);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(
				DataDictEntityJoin.get(EntityMaster_.entityId), entityId));
		if (StringUtils.isNotBlank(fieldType)) {
			restriction = cb.and(restriction, cb.equal(
					dataDictionaryRoot.get(DataDictionary_.fieldType),
					fieldType));
		}
		restriction = cb.and(restriction, cb.equal(
				dataDictionaryRoot.get(DataDictionary_.dataType),
				PayAsiaConstants.PAYASIA_DATA_DICTIONARY_DATA_TYPE_FIELD));
		criteriaQuery.where(restriction);

		TypedQuery<DataDictionary> dataDictionaryTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<DataDictionary> alldataDictionaryList = dataDictionaryTypedQuery
				.getResultList();
		return alldataDictionaryList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.DataDictionaryDAO#findByConditionFormId(java.lang.Long,
	 * java.lang.Long, java.lang.Long)
	 */
	@Override
	public List<DataDictionary> findByConditionFormId(Long companyId,
			Long entityId, Long formId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<DataDictionary> criteriaQuery = cb
				.createQuery(DataDictionary.class);
		Root<DataDictionary> dataDictionaryRoot = criteriaQuery
				.from(DataDictionary.class);

		criteriaQuery.select(dataDictionaryRoot);

		Join<DataDictionary, Company> compDataDictJoin = dataDictionaryRoot
				.join(DataDictionary_.company);

		Join<DataDictionary, EntityMaster> DataDictEntityJoin = dataDictionaryRoot
				.join(DataDictionary_.entityMaster);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(compDataDictJoin.get(Company_.companyId), companyId));

		restriction = cb.and(restriction, cb.equal(
				DataDictEntityJoin.get(EntityMaster_.entityId), entityId));

		restriction = cb.and(restriction, cb.equal(
				dataDictionaryRoot.get(DataDictionary_.formID), formId));

		criteriaQuery.where(restriction);

		TypedQuery<DataDictionary> dataDictionaryTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<DataDictionary> alldataDictionaryList = dataDictionaryTypedQuery
				.getResultList();
		return alldataDictionaryList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.DataDictionaryDAO#
	 * getCountByconditionLanguageIdEntityIdCompanyId(java.lang.Long,
	 * java.lang.Long, java.lang.Long, com.payasia.common.form.PageRequest,
	 * com.payasia.common.form.SortCondition)
	 */
	@Override
	public int getCountByconditionLanguageIdEntityIdCompanyId(Long languageId,
			Long entityId, Long companyId, PageRequest pageDTO,
			SortCondition sortDTO) {
		return findByConditionLanguageIdEntityIdCompanyId(languageId, entityId,
				companyId, pageDTO, sortDTO).size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.DataDictionaryDAO#findByConditionLanguageIdEntityIdCompanyId
	 * (java.lang.Long, java.lang.Long, java.lang.Long,
	 * com.payasia.common.form.PageRequest,
	 * com.payasia.common.form.SortCondition)
	 */
	@Override
	public List<DataDictionary> findByConditionLanguageIdEntityIdCompanyId(
			Long languageId, Long entityId, Long companyId,
			PageRequest pageDTO, SortCondition sortDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<DataDictionary> criteriaQuery = cb
				.createQuery(DataDictionary.class);
		Root<DataDictionary> dataDictionaryRoot = criteriaQuery
				.from(DataDictionary.class);

		criteriaQuery.select(dataDictionaryRoot);

		Join<DataDictionary, EntityMaster> dataEntityJoin = dataDictionaryRoot
				.join(DataDictionary_.entityMaster);

		Join<DataDictionary, Company> compDataDictJoin = dataDictionaryRoot
				.join(DataDictionary_.company);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(dataEntityJoin.get(EntityMaster_.entityId), entityId));

		restriction = cb.and(restriction,
				cb.equal(compDataDictJoin.get(Company_.companyId), companyId));

		criteriaQuery.where(restriction);

		if (sortDTO != null) {

			Path<String> sortPath = getSortPathForMultilingualLabels(sortDTO,
					dataDictionaryRoot);
			if (sortPath != null) {
				if (SortConstants.DB_ORDER_BY_ASC.equalsIgnoreCase(sortDTO
						.getOrderType())) {
					criteriaQuery.orderBy(cb.asc(sortPath));
				}
				if (SortConstants.DB_ORDER_BY_DESC.equalsIgnoreCase(sortDTO
						.getOrderType())) {
					criteriaQuery.orderBy(cb.desc(sortPath));
				}
			}

		}
		TypedQuery<DataDictionary> dataDictionaryTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			dataDictionaryTypedQuery.setFirstResult(getStartPosition(pageDTO));
			dataDictionaryTypedQuery.setMaxResults(pageDTO.getPageSize());
		}
		List<DataDictionary> alldataDictionaryList = dataDictionaryTypedQuery
				.getResultList();
		return alldataDictionaryList;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.DataDictionaryDAO#getCountByCondition(java.lang.Long,
	 * java.lang.Long, java.lang.Long)
	 */
	@Override
	public int getCountByCondition(Long companyId, Long entityId, Long formId) {
		return findByConditionFormId(companyId, entityId, formId).size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.DataDictionaryDAO#deleteByCondition(java.lang.Long,
	 * java.lang.Long, java.lang.Long)
	 */
	@Override
	public void deleteByCondition(Long companyId, Long entityId, Long formId) {

		String queryString = "DELETE FROM DataDictionary d WHERE d.company.companyId = :company AND d.entityMaster.entityId = :entity AND d.formID =:form";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("entity", entityId);
		q.setParameter("company", companyId);
		q.setParameter("form", formId);

		q.executeUpdate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.DataDictionaryDAO#findByDictionaryName(java.lang.Long,
	 * java.lang.Long, java.lang.String, java.lang.Long)
	 */
	@Override
	public DataDictionary findByDictionaryName(Long companyId, Long entityId,
			String dictionaryName, Long formId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<DataDictionary> criteriaQuery = cb
				.createQuery(DataDictionary.class);
		Root<DataDictionary> dataDictionaryRoot = criteriaQuery
				.from(DataDictionary.class);

		criteriaQuery.select(dataDictionaryRoot);

		Join<DataDictionary, Company> compDataDictJoin = dataDictionaryRoot
				.join(DataDictionary_.company);

		Join<DataDictionary, EntityMaster> DataDictEntityJoin = dataDictionaryRoot
				.join(DataDictionary_.entityMaster);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(compDataDictJoin.get(Company_.companyId), companyId));

		restriction = cb.and(restriction, cb.equal(
				DataDictEntityJoin.get(EntityMaster_.entityId), entityId));

		restriction = cb.and(restriction, cb.equal(
				cb.upper(dataDictionaryRoot.get(DataDictionary_.dataDictName)),
				dictionaryName.toUpperCase()));

		if (formId != null) {

			restriction = cb.and(restriction, cb.equal(
					dataDictionaryRoot.get(DataDictionary_.formID), formId));
		}

		criteriaQuery.where(restriction);

		TypedQuery<DataDictionary> dataDictionaryTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<DataDictionary> dataDictionaryList = dataDictionaryTypedQuery
				.getResultList();
		if (dataDictionaryList != null && !dataDictionaryList.isEmpty()) {
			return dataDictionaryList.get(0);
		}
		return null;
	}

	@Override
	public DataDictionary findByDictionaryName(Long companyId, Long entityId,
			String dictionaryName) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<DataDictionary> criteriaQuery = cb
				.createQuery(DataDictionary.class);
		Root<DataDictionary> dataDictionaryRoot = criteriaQuery
				.from(DataDictionary.class);

		criteriaQuery.select(dataDictionaryRoot);

		Join<DataDictionary, Company> compDataDictJoin = dataDictionaryRoot
				.join(DataDictionary_.company);

		Join<DataDictionary, EntityMaster> DataDictEntityJoin = dataDictionaryRoot
				.join(DataDictionary_.entityMaster);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(compDataDictJoin.get(Company_.companyId), companyId));

		restriction = cb.and(restriction, cb.equal(
				DataDictEntityJoin.get(EntityMaster_.entityId), entityId));

		restriction = cb.and(restriction, cb.equal(
				cb.upper(dataDictionaryRoot.get(DataDictionary_.label)),
				dictionaryName.toUpperCase()));

		criteriaQuery.where(restriction);

		TypedQuery<DataDictionary> dataDictionaryTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<DataDictionary> dataDictionaryList = dataDictionaryTypedQuery
				.getResultList();
		if (dataDictionaryList != null && !dataDictionaryList.isEmpty()) {
			return dataDictionaryList.get(0);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.DataDictionaryDAO#findByDictionaryName(java.lang.Long,
	 * java.lang.Long, java.lang.String, java.lang.Long)
	 */
	@Override
	public DataDictionary findByDictionaryNameGroup(Long companyId,
			Long entityId, String dictionaryName, Long formId, String fieldType) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<DataDictionary> criteriaQuery = cb
				.createQuery(DataDictionary.class);
		Root<DataDictionary> dataDictionaryRoot = criteriaQuery
				.from(DataDictionary.class);

		criteriaQuery.select(dataDictionaryRoot);

		Join<DataDictionary, EntityMaster> DataDictEntityJoin = dataDictionaryRoot
				.join(DataDictionary_.entityMaster);

		Predicate restriction = cb.conjunction();

		if (companyId != null) {

			Join<DataDictionary, Company> compDataDictJoin = dataDictionaryRoot
					.join(DataDictionary_.company);

			restriction = cb.and(restriction, cb.equal(
					compDataDictJoin.get(Company_.companyId), companyId));
		}

		restriction = cb.and(restriction, cb.equal(
				DataDictEntityJoin.get(EntityMaster_.entityId), entityId));

		restriction = cb.and(restriction, cb.equal(
				cb.upper(dataDictionaryRoot.get(DataDictionary_.dataDictName)),
				dictionaryName.toUpperCase()));

		if (formId != null) {

			restriction = cb.and(restriction, cb.equal(
					dataDictionaryRoot.get(DataDictionary_.formID), formId));
		}

		if (fieldType != null) {

			restriction = cb.and(restriction, cb.equal(
					dataDictionaryRoot.get(DataDictionary_.fieldType),
					fieldType));
		}

		criteriaQuery.where(restriction);

		TypedQuery<DataDictionary> dataDictionaryTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<DataDictionary> dataDictionaryList = dataDictionaryTypedQuery
				.getResultList();
		if (dataDictionaryList != null && !dataDictionaryList.isEmpty()) {
			return dataDictionaryList.get(0);
		}
		return null;
	}

	/**
	 * 
	 */
	@Override
	public List<DataDictionary> findByCompanyEntityForm(Long companyId,
			Long entityId, Long formId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<DataDictionary> criteriaQuery = cb
				.createQuery(DataDictionary.class);
		Root<DataDictionary> dataDictionaryRoot = criteriaQuery
				.from(DataDictionary.class);

		criteriaQuery.select(dataDictionaryRoot);

		Join<DataDictionary, Company> compDataDictJoin = dataDictionaryRoot
				.join(DataDictionary_.company);

		Join<DataDictionary, EntityMaster> DataDictEntityJoin = dataDictionaryRoot
				.join(DataDictionary_.entityMaster);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(compDataDictJoin.get(Company_.companyId), companyId));
		if (entityId != null) {
			restriction = cb.and(restriction, cb.equal(
					DataDictEntityJoin.get(EntityMaster_.entityId), entityId));
		}
		if (formId != null) {
			restriction = cb.and(restriction, cb.equal(
					dataDictionaryRoot.get(DataDictionary_.formID), formId));
		}

		criteriaQuery.where(restriction);

		TypedQuery<DataDictionary> dataDictionaryTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return dataDictionaryTypedQuery.getResultList();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.DataDictionaryDAO#getSortPathForMultilingualLabels(com
	 * .payasia.common.form.SortCondition, javax.persistence.criteria.Root)
	 */
	@Override
	public Path<String> getSortPathForMultilingualLabels(SortCondition sortDTO,
			Root<DataDictionary> dataDictionaryRoot) {
		List<String> dataDictionaryIsIdList = new ArrayList<String>();
		dataDictionaryIsIdList.add(SortConstants.DATA_DICTIONARY_ID);

		List<String> dataDictionaryIsColList = new ArrayList<String>();
		dataDictionaryIsColList.add(SortConstants.DATA_DICTIONARY_LABEL);

		List<String> multiligualIsColList = new ArrayList<String>();
		multiligualIsColList.add(SortConstants.DATA_DICTIONARY_LABEL_VALUE);

		Path<String> sortPath = null;

		if (dataDictionaryIsColList.contains(sortDTO.getColumnName())) {
			sortPath = dataDictionaryRoot.get(colMap.get(DataDictionary.class
					+ sortDTO.getColumnName()));
		}
		return sortPath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.DataDictionaryDAO#update(com.payasia.dao.bean.DataDictionary
	 * )
	 */
	@Override
	public void update(DataDictionary dataDictionary) {

		super.update(dataDictionary);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.DataDictionaryDAO#delete(com.payasia.dao.bean.DataDictionary
	 * )
	 */
	@Override
	public void delete(DataDictionary dataDictionary) {
		super.delete(dataDictionary);

	}

	@Override
	public List<Object[]> findAllTabFields(long entityId,
			List<String> tabNameList, long groupId) {

		String queryString;
		if (tabNameList.size() == 1
				&& (tabNameList
						.contains(PayAsiaConstants.EMPLOYEE_BASIC_TAB_NAME) || (tabNameList
						.contains(PayAsiaConstants.COMPANY_BASIC_TAB_NAME)))) {
			queryString = " select dd.Data_Dict_Name,dd.label,(case when df.tab_name is null then 'Basic Information' else df.tab_name end) as tab_name,count(label)  from   data_dictionary dd "
					+ " left outer join dynamic_form df on dd.Form_ID= df.form_id  "
					+ " where dd.entity_id=:entityId"
					+ " and dd.data_type in ('Field','Formula') "
					+ " and ( df.Tab_Name in (:tabNameList)"
					+ " or  df.Tab_Name is null ) "
					+ " and (df.Version is null or  df.Version = (select max(idf.Version) from dynamic_form idf where idf.Form_ID=df.Form_ID ) "
					+ ") and ( dd.company_id is null or dd.company_id in (select company_id  from company where group_id=:groupId"
					+ ")) "
					+ " group by df.tab_name,dd.Data_Dict_Name,dd.label  order by dd.Data_Dict_Name";

		} else {

			queryString = " select dd.Data_Dict_Name,dd.label,(case when df.tab_name is null then 'Basic Information' else df.tab_name end) as tab_name,count(label),dd.Data_Type from  data_dictionary dd "
					+ " left outer join dynamic_form df on dd.Form_ID= df.form_id "
					+ " where dd.entity_id=:entityId"
					+ " and dd.data_type in ('Field','Formula') "
					+ " and ( df.Tab_Name in (:tabNameList)"
					+ " or df.Tab_Name is null) and (df.Version is null or  df.Version = (select max(idf.Version) from dynamic_form idf where idf.Form_ID=df.Form_ID ) "
					+ ") and ( dd.company_id is null or dd.company_id in (select company_id  from company where group_id=:groupId"
					+ ")) "
					+ " group by df.tab_name,dd.Data_Dict_Name,dd.Data_Type,dd.label  order by dd.Data_Dict_Name";
		}
		queryString = queryString.replace("?", "");

		Query q = entityManagerFactory.createNativeQuery(queryString);
		q.setParameter("entityId", entityId);
		q.setParameter("tabNameList", tabNameList);
		q.setParameter("groupId", groupId);
		List<Object[]> tupleList = q.getResultList();

		return tupleList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.DataDictionaryDAO#findByConditionFormId(java.lang.Long,
	 * java.lang.Long, java.lang.Long)
	 */
	@Override
	public List<DataDictionary> findByCondition(Long companyId, Long entityId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<DataDictionary> criteriaQuery = cb
				.createQuery(DataDictionary.class);
		Root<DataDictionary> dataDictionaryRoot = criteriaQuery
				.from(DataDictionary.class);

		criteriaQuery.select(dataDictionaryRoot);

		Join<DataDictionary, Company> compDataDictJoin = dataDictionaryRoot
				.join(DataDictionary_.company, JoinType.LEFT);

		Join<DataDictionary, EntityMaster> DataDictEntityJoin = dataDictionaryRoot
				.join(DataDictionary_.entityMaster, JoinType.LEFT);

		Predicate restriction = cb.or(
				cb.equal(compDataDictJoin.get(Company_.companyId), companyId),
				cb.isNull(compDataDictJoin.get(Company_.companyId)));

		restriction = cb.and(restriction, cb.equal(
				DataDictEntityJoin.get(EntityMaster_.entityId), entityId));
		restriction = cb
				.and(restriction,
						cb.or(cb.equal(
								dataDictionaryRoot
										.get(DataDictionary_.dataType),
								PayAsiaConstants.PAYASIA_DATA_DICTIONARY_DATA_TYPE_FIELD),
								cb.equal(
										dataDictionaryRoot
												.get(DataDictionary_.dataType),
										PayAsiaConstants.PAYASIA_DATA_DICTIONARY_DATA_TYPE_FORMULA)));

		criteriaQuery.where(restriction);

		TypedQuery<DataDictionary> dataDictionaryTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<DataDictionary> alldataDictionaryList = dataDictionaryTypedQuery
				.getResultList();
		return alldataDictionaryList;
	}

	@Override
	public List<DataDictionary> findByCompanyId(Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<DataDictionary> criteriaQuery = cb
				.createQuery(DataDictionary.class);
		Root<DataDictionary> dataDictionaryRoot = criteriaQuery
				.from(DataDictionary.class);

		criteriaQuery.select(dataDictionaryRoot);

		Join<DataDictionary, Company> compDataDictJoin = dataDictionaryRoot
				.join(DataDictionary_.company);

		Predicate restriction = cb.conjunction();

		Predicate compRestriction = cb.or(
				cb.equal(compDataDictJoin.get(Company_.companyId), companyId),
				cb.isNull(compDataDictJoin.get(Company_.companyId)));

		restriction = cb.and(restriction, compRestriction);

		criteriaQuery.where(restriction);

		TypedQuery<DataDictionary> dataDictionaryTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return dataDictionaryTypedQuery.getResultList();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.DataDictionaryDAO#findByConditionImportable(java.lang
	 * .Long, java.lang.Long, java.lang.String, java.lang.Boolean)
	 */
	@Override
	public List<DataDictionary> findByEntityIdFieldType(Long entityId,
			String fieldType) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<DataDictionary> criteriaQuery = cb
				.createQuery(DataDictionary.class);
		Root<DataDictionary> dataDictionaryRoot = criteriaQuery
				.from(DataDictionary.class);

		criteriaQuery.select(dataDictionaryRoot);

		Join<DataDictionary, EntityMaster> DataDictEntityJoin = dataDictionaryRoot
				.join(DataDictionary_.entityMaster);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(
				DataDictEntityJoin.get(EntityMaster_.entityId), entityId));
		if (StringUtils.isNotBlank(fieldType)) {
			restriction = cb.and(restriction, cb.equal(
					dataDictionaryRoot.get(DataDictionary_.fieldType),
					fieldType));
		}

		restriction = cb.and(restriction, cb.equal(
				dataDictionaryRoot.get(DataDictionary_.dataType),
				PayAsiaConstants.PAYASIA_DATA_DICTIONARY_DATA_TYPE_FIELD));
		criteriaQuery.where(restriction);

		TypedQuery<DataDictionary> dataDictionaryTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<DataDictionary> alldataDictionaryList = dataDictionaryTypedQuery
				.getResultList();
		return alldataDictionaryList;
	}

	@Override
	public List<DataDictionary> getStaticAndCustomFieldList(Long companyId,
			Long entityIdForStaticField) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<DataDictionary> criteriaQuery = cb
				.createQuery(DataDictionary.class);
		Root<DataDictionary> dataDictionaryRoot = criteriaQuery
				.from(DataDictionary.class);

		criteriaQuery.select(dataDictionaryRoot);

		Join<DataDictionary, Company> compDataDictJoin = dataDictionaryRoot
				.join(DataDictionary_.company, JoinType.LEFT);
		Join<DataDictionary, EntityMaster> entityMasterDataDictJoin = dataDictionaryRoot
				.join(DataDictionary_.entityMaster);
		Predicate restriction = cb.equal(
				compDataDictJoin.get(Company_.companyId), companyId);

		Predicate companyIdRestriction = cb.isNull(compDataDictJoin
				.get(Company_.companyId));

		Predicate entityIdRestriction = cb.equal(
				entityMasterDataDictJoin.get(EntityMaster_.entityId),
				entityIdForStaticField);

		Predicate subRestriction = cb.and(companyIdRestriction,
				entityIdRestriction);

		restriction = cb.or(restriction, subRestriction);

		criteriaQuery.where(restriction);

		TypedQuery<DataDictionary> dataDictionaryTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return dataDictionaryTypedQuery.getResultList();

	}

	@Override
	public DataDictionary findByCondition(Long companyId, Long entityId,
			Long formId, String dataDictName, String dataType) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<DataDictionary> criteriaQuery = cb
				.createQuery(DataDictionary.class);
		Root<DataDictionary> dataDictionaryRoot = criteriaQuery
				.from(DataDictionary.class);

		criteriaQuery.select(dataDictionaryRoot);

		Join<DataDictionary, EntityMaster> DataDictEntityJoin = dataDictionaryRoot
				.join(DataDictionary_.entityMaster);

		Predicate restriction = cb.conjunction();

		if (companyId != null) {

			Join<DataDictionary, Company> compDataDictJoin = dataDictionaryRoot
					.join(DataDictionary_.company);

			restriction = cb.and(restriction, cb.equal(
					compDataDictJoin.get(Company_.companyId), companyId));
		}

		restriction = cb.and(restriction, cb.equal(
				DataDictEntityJoin.get(EntityMaster_.entityId), entityId));

		restriction = cb.and(restriction, cb.equal(
				cb.upper(dataDictionaryRoot.get(DataDictionary_.dataDictName)),
				dataDictName.toUpperCase()));

		if (formId != null) {

			restriction = cb.and(restriction, cb.equal(
					dataDictionaryRoot.get(DataDictionary_.formID), formId));
		}

		if (dataType != null) {

			restriction = cb
					.and(restriction, cb.equal(
							dataDictionaryRoot.get(DataDictionary_.dataType),
							dataType));
		}

		criteriaQuery.where(restriction);

		TypedQuery<DataDictionary> dataDictionaryTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<DataDictionary> dataDictionaryList = dataDictionaryTypedQuery
				.getResultList();
		if (dataDictionaryList != null && !dataDictionaryList.isEmpty()) {
			return dataDictionaryList.get(0);
		}
		return null;
	}

	@Override
	public DataDictionary findById(long dataDictionaryId, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<DataDictionary> criteriaQuery = cb
				.createQuery(DataDictionary.class);
		Root<DataDictionary> dataDictionaryRoot = criteriaQuery
				.from(DataDictionary.class);

		criteriaQuery.select(dataDictionaryRoot);

		Join<DataDictionary, Company> compDataDictJoin = dataDictionaryRoot
				.join(DataDictionary_.company);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(compDataDictJoin.get(Company_.companyId), companyId));


		restriction = cb.and(restriction, cb.equal(dataDictionaryRoot.get(DataDictionary_.dataDictionaryId),
				dataDictionaryId));

		criteriaQuery.where(restriction);

		TypedQuery<DataDictionary> dataDictionaryTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<DataDictionary> dataDictionaryList = dataDictionaryTypedQuery
				.getResultList();
		if (dataDictionaryList != null && !dataDictionaryList.isEmpty()) {
			return dataDictionaryList.get(0);
		}
		return null;
	}

	@Override
	public List<DataDictionary> findAllSection(Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<DataDictionary> criteriaQuery = cb
				.createQuery(DataDictionary.class);
		Root<DataDictionary> dataDictionaryRoot = criteriaQuery
				.from(DataDictionary.class);

		criteriaQuery.select(dataDictionaryRoot);

		Join<DataDictionary, Company> compDataDictJoin = dataDictionaryRoot
				.join(DataDictionary_.company);

		Join<DataDictionary, EntityMaster> dataDictEntityJoin = dataDictionaryRoot
				.join(DataDictionary_.entityMaster);
		
		Predicate restriction = cb.conjunction();
		
		restriction = cb.and(restriction,
				cb.equal(dataDictionaryRoot.get(DataDictionary_.dataType), PayAsiaConstants.PAYASIA_DATA_DICTIONARY_DATA_TYPE_SECTION));
		
		restriction = cb.and(restriction,
				cb.equal(compDataDictJoin.get(Company_.companyId), companyId));

		restriction = cb.and(restriction,
				cb.equal(dataDictEntityJoin.get(EntityMaster_.entityName), PayAsiaConstants.EMPLOYEE_ENTITY_NAME));
		criteriaQuery.where(restriction);

		TypedQuery<DataDictionary> dataDictionaryTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return dataDictionaryTypedQuery
				.getResultList();
	}

}
