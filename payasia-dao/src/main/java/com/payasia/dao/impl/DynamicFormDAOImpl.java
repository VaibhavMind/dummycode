/**
 * @author abhisheksachdeva
 * 
 */
package com.payasia.dao.impl;

import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.persistence.Query;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.persistence.criteria.Subquery;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.payasia.common.dto.ManageRolesConditionDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.util.NullAwareBeanUtilsBean;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.DynamicFormDAO;
import com.payasia.dao.DynamicFormSeqDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.CompanyGroup;
import com.payasia.dao.bean.CompanyGroup_;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.DynamicForm;
import com.payasia.dao.bean.DynamicFormPK;
import com.payasia.dao.bean.DynamicFormPK_;
import com.payasia.dao.bean.DynamicForm_;
import com.payasia.dao.bean.EntityMaster;
import com.payasia.dao.bean.EntityMaster_;
import com.payasia.dao.bean.MonthMaster;
import com.payasia.dao.bean.MonthMaster_;

/**
 * The Class DynamicFormDAOImpl.
 */
@Repository
public class DynamicFormDAOImpl extends BaseDAO implements DynamicFormDAO {
	private static final Logger LOGGER = Logger.getLogger(DynamicFormDAOImpl.class);
	@Resource
	DynamicFormSeqDAO dynamicFormSeqDAO;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.DynamicFormDAO#save(com.payasia.dao.bean.DynamicForm)
	 */
	@Override
	public void save(DynamicForm dynamicForm) {

		super.save(dynamicForm);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.DynamicFormDAO#update(com.payasia.dao.bean.DynamicForm)
	 */
	@Override
	public void update(DynamicForm dynamicForm) {

		super.update(dynamicForm);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.BaseDAO#getBaseEntity()
	 */
	@Override
	protected Object getBaseEntity() {
		DynamicForm dynamicForm = new DynamicForm();
		return dynamicForm;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.DynamicFormDAO#findMaxVersionByFormId(long, long,
	 * java.lang.Long)
	 */
	@Override
	public DynamicForm findMaxVersionByFormId(long companyId, long entityId, Long formId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<DynamicForm> criteriaQuery = cb.createQuery(DynamicForm.class);
		Root<DynamicForm> dynamicFormRoot = criteriaQuery.from(DynamicForm.class);
		criteriaQuery.select(dynamicFormRoot);

		Join<DynamicForm, Company> dynamicFormCompanyJoin = dynamicFormRoot.join(DynamicForm_.company);

		Join<DynamicForm, EntityMaster> dynamicFormEntityJoin = dynamicFormRoot.join(DynamicForm_.entityMaster);

		Join<DynamicForm, DynamicFormPK> dynamicFormPKJoin = dynamicFormRoot.join(DynamicForm_.id);

		Path<Long> companyID = dynamicFormCompanyJoin.get(Company_.companyId);

		Path<Long> entityID = dynamicFormEntityJoin.get(EntityMaster_.entityId);

		Path<Long> formID = dynamicFormPKJoin.get(DynamicFormPK_.formId);

		Path<Integer> version = dynamicFormPKJoin.get(DynamicFormPK_.version);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(companyID, companyId));

		restriction = cb.and(restriction, cb.equal(entityID, entityId));

		restriction = cb.and(restriction, cb.equal(formID, formId));

		Subquery<Integer> subquery = criteriaQuery.subquery(Integer.class);
		Root<DynamicForm> fromDynamicForm = subquery.from(DynamicForm.class);
		subquery.select(cb.max(fromDynamicForm.get(DynamicForm_.id).get("version").as(Integer.class)));

		Join<DynamicForm, Company> dynamicFormCompanySubJoin = fromDynamicForm.join(DynamicForm_.company);

		Join<DynamicForm, EntityMaster> dynamicFormEntitySubJoin = fromDynamicForm.join(DynamicForm_.entityMaster);

		Join<DynamicForm, DynamicFormPK> dynamicFormPKSubJoin = fromDynamicForm.join(DynamicForm_.id);

		Path<Long> companySubID = dynamicFormCompanySubJoin.get(Company_.companyId);

		Path<Long> entitySubID = dynamicFormEntitySubJoin.get(EntityMaster_.entityId);

		Path<Long> SubFormID = dynamicFormPKSubJoin.get(DynamicFormPK_.formId);

		Predicate subRestriction = cb.conjunction();

		subRestriction = cb.and(subRestriction, cb.equal(companySubID, companyId));

		subRestriction = cb.and(subRestriction, cb.equal(entitySubID, entityId));

		subRestriction = cb.and(subRestriction, cb.equal(SubFormID, formId));

		subquery.where(subRestriction);

		restriction = cb.and(restriction, cb.in(version).value(subquery));

		criteriaQuery.where(restriction);

		TypedQuery<DynamicForm> maxVersionQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<DynamicForm> maxVersionQueryList = maxVersionQuery.getResultList();
		if (maxVersionQueryList != null && !maxVersionQueryList.isEmpty()) {
			return maxVersionQueryList.get(0);
		}
		return null;

	}

	@Override
	public DynamicForm findMaxVersionBySection(long companyId, long entityId, String sectionName) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<DynamicForm> criteriaQuery = cb.createQuery(DynamicForm.class);
		Root<DynamicForm> dynamicFormRoot = criteriaQuery.from(DynamicForm.class);
		criteriaQuery.select(dynamicFormRoot);

		Join<DynamicForm, Company> dynamicFormCompanyJoin = dynamicFormRoot.join(DynamicForm_.company);

		Join<DynamicForm, EntityMaster> dynamicFormEntityJoin = dynamicFormRoot.join(DynamicForm_.entityMaster);

		Join<DynamicForm, DynamicFormPK> dynamicFormPKJoin = dynamicFormRoot.join(DynamicForm_.id);

		Path<Integer> version = dynamicFormPKJoin.get(DynamicFormPK_.version);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(dynamicFormCompanyJoin.get(Company_.companyId), companyId));

		restriction = cb.and(restriction, cb.equal(dynamicFormEntityJoin.get(EntityMaster_.entityId), entityId));

		restriction = cb.and(restriction, cb.equal(dynamicFormRoot.get(DynamicForm_.tabName), sectionName));

		Subquery<Integer> subquery = criteriaQuery.subquery(Integer.class);
		Root<DynamicForm> fromDynamicForm = subquery.from(DynamicForm.class);
		subquery.select(cb.max(fromDynamicForm.get(DynamicForm_.id).get("version").as(Integer.class)));

		Join<DynamicForm, Company> dynamicFormCompanySubJoin = fromDynamicForm.join(DynamicForm_.company);

		Join<DynamicForm, EntityMaster> dynamicFormEntitySubJoin = fromDynamicForm.join(DynamicForm_.entityMaster);

		fromDynamicForm.join(DynamicForm_.id);

		Predicate subRestriction = cb.conjunction();

		subRestriction = cb.and(subRestriction, cb.equal(dynamicFormCompanySubJoin.get(Company_.companyId), companyId));

		subRestriction = cb.and(subRestriction, cb.equal(dynamicFormEntitySubJoin.get(EntityMaster_.entityId), entityId));

		subRestriction = cb.and(subRestriction, cb.equal(fromDynamicForm.get(DynamicForm_.tabName), sectionName));

		subquery.where(subRestriction);

		restriction = cb.and(restriction, cb.in(version).value(subquery));

		criteriaQuery.where(restriction);

		TypedQuery<DynamicForm> maxVersionQuery = entityManagerFactory.createQuery(criteriaQuery);
		List<DynamicForm> maxVersionQueryList = maxVersionQuery.getResultList();
		if (maxVersionQueryList != null && !maxVersionQueryList.isEmpty()) {
			return maxVersionQueryList.get(0);
		}
		return null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.DynamicFormDAO#findByCondition(long, long,
	 * java.lang.String)
	 */
	@Override
	public List<DynamicForm> findByCondition(long companyId, long entityId, String tabName) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<DynamicForm> criteriaQuery = cb.createQuery(DynamicForm.class);
		Root<DynamicForm> dynamicFormRoot = criteriaQuery.from(DynamicForm.class);
		criteriaQuery.select(dynamicFormRoot);
		Join<DynamicForm, Company> dynamicFormCompanyJoin = dynamicFormRoot.join(DynamicForm_.company);

		Join<DynamicForm, EntityMaster> dynamicFormEntityJoin = dynamicFormRoot.join(DynamicForm_.entityMaster);

		Path<Long> companyID = dynamicFormCompanyJoin.get(Company_.companyId);

		Path<Long> entityID = dynamicFormEntityJoin.get(EntityMaster_.entityId);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(companyID, companyId));

		restriction = cb.and(restriction, cb.equal(entityID, entityId));

		restriction = cb.and(restriction,
				cb.equal(cb.upper(dynamicFormRoot.get(DynamicForm_.tabName)), tabName.toUpperCase()));

		criteriaQuery.where(restriction);

		TypedQuery<DynamicForm> dynamicFormQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<DynamicForm> dynamicForms = dynamicFormQuery.getResultList();

		return dynamicForms;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.DynamicFormDAO#getCountByCondition(long, long,
	 * java.lang.String)
	 */
	@Override
	public int getCountByCondition(long companyId, long entityId, String tabName) {

		return findByCondition(companyId, entityId, tabName).size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.DynamicFormDAO#getMaxFormId(java.lang.Long,
	 * java.lang.Long)
	 */

	@Override
	public synchronized Long getMaxFormId(Long companyId, Long entityId) {
		long maxFormId = dynamicFormSeqDAO.getNextVal();
		return maxFormId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.DynamicFormDAO#getMaxVersion(long, long,
	 * java.lang.String)
	 */
	@Override
	public Integer getMaxVersion(long companyId, long entityId, String tabName) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Integer> criteriaQuery = cb.createQuery(Integer.class);
		Root<DynamicForm> dynamicFormRoot = criteriaQuery.from(DynamicForm.class);
		criteriaQuery.select(cb.max(dynamicFormRoot.get(DynamicForm_.id).get("version").as(Integer.class)));

		Join<DynamicForm, Company> dynamicFormCompanyJoin = dynamicFormRoot.join(DynamicForm_.company);

		Join<DynamicForm, EntityMaster> dynamicFormEntityJoin = dynamicFormRoot.join(DynamicForm_.entityMaster);

		Path<Long> companyID = dynamicFormCompanyJoin.get(Company_.companyId);

		Path<Long> entityID = dynamicFormEntityJoin.get(EntityMaster_.entityId);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(companyID, companyId));

		restriction = cb.and(restriction, cb.equal(entityID, entityId));
		if (StringUtils.isNotBlank(tabName)) {
			restriction = cb.and(restriction,
					cb.equal(cb.upper(dynamicFormRoot.get(DynamicForm_.tabName)), tabName.toUpperCase()));
		}

		criteriaQuery.where(restriction);

		TypedQuery<Integer> dynamicFormTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		if (dynamicFormTypedQuery.getResultList() != null) {
			return dynamicFormTypedQuery.getSingleResult();
		} else {
			return 0;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.DynamicFormDAO#findByMaxVersion(long, long, int,
	 * java.lang.String)
	 */
	@Override
	public DynamicForm findByMaxVersion(long companyId, long entityId, int version, String tabName) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<DynamicForm> criteriaQuery = cb.createQuery(DynamicForm.class);
		Root<DynamicForm> dynamicFormRoot = criteriaQuery.from(DynamicForm.class);
		criteriaQuery.select(dynamicFormRoot);

		Join<DynamicForm, Company> dynamicFormCompanyJoin = dynamicFormRoot.join(DynamicForm_.company);

		Join<DynamicForm, EntityMaster> dynamicFormEntityJoin = dynamicFormRoot.join(DynamicForm_.entityMaster);

		Join<DynamicForm, DynamicFormPK> dynamicFormPKJoin = dynamicFormRoot.join(DynamicForm_.id);

		Path<Long> companyID = dynamicFormCompanyJoin.get(Company_.companyId);

		Path<Long> entityID = dynamicFormEntityJoin.get(EntityMaster_.entityId);

		Path<Integer> tabVersion = dynamicFormPKJoin.get(DynamicFormPK_.version);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(companyID, companyId));

		restriction = cb.and(restriction, cb.equal(entityID, entityId));

		restriction = cb.and(restriction, cb.equal(tabVersion, version));
		if (StringUtils.isNotBlank(tabName)) {
			restriction = cb.and(restriction,
					cb.equal(cb.upper(dynamicFormRoot.get(DynamicForm_.tabName)), tabName.toUpperCase()));
		}
		criteriaQuery.where(restriction);

		TypedQuery<DynamicForm> dynamicFormQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<DynamicForm> dynamicFormList = dynamicFormQuery.getResultList();
		if (dynamicFormList != null && !dynamicFormList.isEmpty()) {
			return dynamicFormList.get(0);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.DynamicFormDAO#getDistinctFormId(java.lang.Long,
	 * java.lang.Long)
	 */
	@Override
	public List<Long> getDistinctFormId(Long companyId, Long entityId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
		Root<DynamicForm> dynamicFormRoot = criteriaQuery.from(DynamicForm.class);

		criteriaQuery.select(dynamicFormRoot.get(DynamicForm_.id).get("formId").as(Long.class)).distinct(true);

		Join<DynamicForm, Company> dynamicFormCompanyJoin = dynamicFormRoot.join(DynamicForm_.company);

		Join<DynamicForm, EntityMaster> dynamicFormEntityJoin = dynamicFormRoot.join(DynamicForm_.entityMaster);

		Path<Long> companyID = dynamicFormCompanyJoin.get(Company_.companyId);

		Path<Long> entityID = dynamicFormEntityJoin.get(EntityMaster_.entityId);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(companyID, companyId));

		restriction = cb.and(restriction, cb.equal(entityID, entityId));

		criteriaQuery.where(restriction);

		TypedQuery<Long> dynamicFormTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<Long> formList = dynamicFormTypedQuery.getResultList();

		return formList;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.DynamicFormDAO#getMaxVersionByFormId(java.lang.Long,
	 * long, long)
	 */
	@Override
	public int getMaxVersionByFormId(Long companyId, long entityId, long formId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Integer> criteriaQuery = cb.createQuery(Integer.class);
		Root<DynamicForm> dynamicFormRoot = criteriaQuery.from(DynamicForm.class);
		criteriaQuery.select(cb.max(dynamicFormRoot.get(DynamicForm_.id).get("version").as(Integer.class)));

		Join<DynamicForm, Company> dynamicFormCompanyJoin = dynamicFormRoot.join(DynamicForm_.company);

		Join<DynamicForm, EntityMaster> dynamicFormEntityJoin = dynamicFormRoot.join(DynamicForm_.entityMaster);

		Join<DynamicForm, DynamicFormPK> dynamicFormPKJoin = dynamicFormRoot.join(DynamicForm_.id);

		Path<Long> companyID = dynamicFormCompanyJoin.get(Company_.companyId);

		Path<Long> entityID = dynamicFormEntityJoin.get(EntityMaster_.entityId);

		Path<Long> formID = dynamicFormPKJoin.get(DynamicFormPK_.formId);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(companyID, companyId));

		restriction = cb.and(restriction, cb.equal(entityID, entityId));

		restriction = cb.and(restriction, cb.equal(formID, formId));

		criteriaQuery.where(restriction);

		TypedQuery<Integer> dynamicFormTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		if (dynamicFormTypedQuery.getResultList().size() > 0) {
			try {
				return dynamicFormTypedQuery.getSingleResult();
			} catch (NullPointerException e) {
				LOGGER.error(e.getMessage(), e);
				return 0;
			}
		} else {
			return 0;

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.DynamicFormDAO#findByMaxVersionByFormId(java.lang.Long,
	 * long, int, long)
	 */
	@Override
	public DynamicForm findByMaxVersionByFormId(Long companyId, long entityId, int maxVersion, long formId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<DynamicForm> criteriaQuery = cb.createQuery(DynamicForm.class);
		Root<DynamicForm> dynamicFormRoot = criteriaQuery.from(DynamicForm.class);
		criteriaQuery.select(dynamicFormRoot);

		Join<DynamicForm, Company> dynamicFormCompanyJoin = dynamicFormRoot.join(DynamicForm_.company);

		Join<DynamicForm, EntityMaster> dynamicFormEntityJoin = dynamicFormRoot.join(DynamicForm_.entityMaster);

		Join<DynamicForm, DynamicFormPK> dynamicFormPKJoin = dynamicFormRoot.join(DynamicForm_.id);

		Path<Long> companyID = dynamicFormCompanyJoin.get(Company_.companyId);

		Path<Long> entityID = dynamicFormEntityJoin.get(EntityMaster_.entityId);

		Path<Integer> tabVersion = dynamicFormPKJoin.get(DynamicFormPK_.version);

		Path<Long> formID = dynamicFormPKJoin.get(DynamicFormPK_.formId);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(companyID, companyId));

		restriction = cb.and(restriction, cb.equal(entityID, entityId));

		restriction = cb.and(restriction, cb.equal(tabVersion, maxVersion));

		restriction = cb.and(restriction, cb.equal(formID, formId));

		criteriaQuery.where(restriction);

		TypedQuery<DynamicForm> dynamicFormQuery = entityManagerFactory.createQuery(criteriaQuery);
		if (dynamicFormQuery.getSingleResult() != null) {
			return dynamicFormQuery.getSingleResult();
		} else {
			return new DynamicForm();
		}
	}

	@Override
	public DynamicForm findByMaxVersionByFormIdReadWrite(Long companyId, long entityId, int maxVersion, long formId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<DynamicForm> criteriaQuery = cb.createQuery(DynamicForm.class);
		Root<DynamicForm> dynamicFormRoot = criteriaQuery.from(DynamicForm.class);
		criteriaQuery.select(dynamicFormRoot);

		Join<DynamicForm, Company> dynamicFormCompanyJoin = dynamicFormRoot.join(DynamicForm_.company);

		Join<DynamicForm, EntityMaster> dynamicFormEntityJoin = dynamicFormRoot.join(DynamicForm_.entityMaster);

		Join<DynamicForm, DynamicFormPK> dynamicFormPKJoin = dynamicFormRoot.join(DynamicForm_.id);

		Path<Long> companyID = dynamicFormCompanyJoin.get(Company_.companyId);

		Path<Long> entityID = dynamicFormEntityJoin.get(EntityMaster_.entityId);

		Path<Integer> tabVersion = dynamicFormPKJoin.get(DynamicFormPK_.version);

		Path<Long> formID = dynamicFormPKJoin.get(DynamicFormPK_.formId);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(companyID, companyId));

		restriction = cb.and(restriction, cb.equal(entityID, entityId));

		restriction = cb.and(restriction, cb.equal(tabVersion, maxVersion));

		restriction = cb.and(restriction, cb.equal(formID, formId));

		criteriaQuery.where(restriction);

		TypedQuery<DynamicForm> dynamicFormQuery = entityManagerFactory.createQuery(criteriaQuery);
		if (dynamicFormQuery.getSingleResult() != null) {
			return dynamicFormQuery.getSingleResult();
		} else {
			return new DynamicForm();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.DynamicFormDAO#deleteByCondition(long, long, long)
	 */
	@Override
	public void deleteByCondition(long companyId, long entityId, long formId) {

		String queryString = "DELETE FROM DynamicForm d WHERE d.company.companyId = :company AND d.entityMaster.entityId = :entity AND d.id.formId =:form";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("entity", entityId);
		q.setParameter("company", companyId);
		q.setParameter("form", formId);

		q.executeUpdate();

	}

	@Override
	public List<Object[]> getSectionsForEffectiveDate(Long companyId, Long entityId, Long formId) {

		String queryString = "SELECT d.id.formId, MAX(d.id.version) from DynamicForm d WHERE d.company.companyId = :company AND d.entityMaster.entityId = :entity AND d.id.formId !=:form GROUP BY d.id.formId";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("entity", entityId);
		q.setParameter("company", companyId);
		q.setParameter("form", formId);
		return q.getResultList();
	}

	@Override
	public List<Object[]> getEffectiveDateByCondition(Long companyId, Long entityId, String tabName, int effectiveYear,
			Long monthId, int effectivePart) {
		String query = "";

		Calendar cal = Calendar.getInstance();
		cal.set(effectiveYear, (monthId.intValue() - 1), effectivePart);
		Timestamp dateTime = new Timestamp(cal.getTimeInMillis());

		String queryString = "select top 1 df.Effective_Year, df.Effective_Month, df.Effective_Part from Dynamic_Form df ";
		queryString += " where df.Company_ID= :company and df.Entity_ID= :entity and df.Tab_Name= :tabName "
				+ " and datefromparts(df.Effective_Year,df.Effective_Month,df.Effective_Part) <= :dateTime ";
		queryString += " ORDER BY df.Effective_Year DESC , df.Effective_Month DESC,df.Effective_Part DESC ";

		Query query1 = entityManagerFactory.createNativeQuery(queryString);
		query1.setParameter("company", companyId);
		query1.setParameter("entity", entityId);
		query1.setParameter("tabName", tabName);
		query1.setParameter("dateTime", dateTime);
		List<Object[]> tuples = query1.getResultList();

		if (tuples.size() == 0) {
			query = "SELECT DISTINCT d.effectiveYear, d.monthMaster, d.effectivePart FROM DynamicForm d ";
			query += "WHERE d.company.companyId = :company AND d.entityMaster.entityId = :entity AND ";
			query += "d.tabName = :tabName AND d.effectiveYear <= :effectiveYear  ";
			query += "ORDER BY d.effectiveYear DESC , d.monthMaster DESC,d.effectivePart DESC ";
			Query q = entityManagerFactory.createQuery(query);
			q = entityManagerFactory.createQuery(query);
			q.setParameter("entity", entityId);
			q.setParameter("company", companyId);
			q.setParameter("tabName", tabName);
			q.setParameter("effectiveYear", effectiveYear);
			q.setMaxResults(1);
			tuples = q.getResultList();
		}
		return tuples;
	}

	@Override
	public List<Object[]> getEffectiveDateByCondition(Long companyId, Long entityId, Long formId, int effectiveYear,
			Long monthId, int effectivePart) {
		String query = "";

		Calendar cal = Calendar.getInstance();
		cal.set(effectiveYear, (monthId.intValue() - 1), effectivePart);
		Timestamp dateTime = new Timestamp(cal.getTimeInMillis());

		String queryString = "select top 1 df.Effective_Year, df.Effective_Month, df.Effective_Part from Dynamic_Form df ";
		queryString += " where df.Company_ID= :company and df.Entity_ID= :entity and df.Form_ID= :formId "
				+ " and datefromparts(df.Effective_Year,df.Effective_Month,df.Effective_Part) <= :dateTime ";
		queryString += " ORDER BY df.Effective_Year DESC , df.Effective_Month DESC,df.Effective_Part DESC ";

		Query query1 = entityManagerFactory.createNativeQuery(queryString);
		query1.setParameter("company", companyId);
		query1.setParameter("entity", entityId);
		query1.setParameter("formId", formId);
		query1.setParameter("dateTime", dateTime);
		List<Object[]> tuples = query1.getResultList();

		if (tuples.size() == 0) {
			query = "SELECT DISTINCT d.effectiveYear, d.monthMaster, d.effectivePart FROM DynamicForm d ";
			query += "WHERE d.company.companyId = :company AND d.entityMaster.entityId = :entity AND ";
			query += "d.id.formId = :formId AND d.effectiveYear <= :effectiveYear  ";
			query += "ORDER BY d.effectiveYear DESC , d.monthMaster DESC,d.effectivePart DESC ";
			Query q = entityManagerFactory.createQuery(query);
			q = entityManagerFactory.createQuery(query);
			q.setParameter("entity", entityId);
			q.setParameter("company", companyId);
			q.setParameter("formId", formId);
			q.setParameter("effectiveYear", effectiveYear);
			q.setMaxResults(1);
			tuples = q.getResultList();
		}
		return tuples;
	}

	@Override
	public DynamicForm getDynamicFormBasedOnEffectiveDate(Long companyId, Long entityId, Long formId, int effectiveYear,
			Long monthId, int effectivePart) {
		String query = "SELECT d FROM DynamicForm d ";
		query += "WHERE d.company.companyId = :company AND d.entityMaster.entityId = :entity AND ";
		query += "d.id.formId = :formId AND d.effectiveYear = :effectiveYear AND d.monthMaster.monthId = :monthId AND d.effectivePart = :effectivePart ";
		query += "ORDER BY d.id.version DESC ";

		Query q = entityManagerFactory.createQuery(query);
		q.setParameter("entity", entityId);
		q.setParameter("company", companyId);
		q.setParameter("formId", formId);
		q.setParameter("effectiveYear", effectiveYear);
		q.setParameter("monthId", monthId);
		q.setParameter("effectivePart", effectivePart);
		q.setMaxResults(1);
		DynamicForm dynamicForm = (DynamicForm) q.getSingleResult();
		return dynamicForm;
	}

	@Override
	public DynamicForm getDynamicFormBasedOnEffectiveDate(Long companyId, Long entityId, String tabName,
			int effectiveYear, Long monthId, int effectivePart) {
		String query = "SELECT d FROM DynamicForm d ";
		query += "WHERE d.company.companyId = :company AND d.entityMaster.entityId = :entity AND ";
		query += "d.tabName = :tabName AND d.effectiveYear = :effectiveYear AND d.monthMaster.monthId = :monthId AND d.effectivePart = :effectivePart ";
		query += "ORDER BY d.id.version DESC ";

		Query q = entityManagerFactory.createQuery(query);
		q.setParameter("entity", entityId);
		q.setParameter("company", companyId);
		q.setParameter("tabName", tabName);
		q.setParameter("effectiveYear", effectiveYear);
		q.setParameter("monthId", monthId);
		q.setParameter("effectivePart", effectivePart);
		q.setMaxResults(1);
		DynamicForm dynamicForm = (DynamicForm) q.getSingleResult();
		return dynamicForm;
	}

	@Override
	public void getDetachedEntity(DynamicForm dynamicForm) {
		entityManagerFactory.detach(dynamicForm);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.DynamicFormDAO#getDistinctFormId(java.lang.Long,
	 * java.lang.Long)
	 */
	@Override
	public List<String> getDistinctTabNames(Long companyGroupId, Long entityId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<String> criteriaQuery = cb.createQuery(String.class);
		Root<DynamicForm> dynamicFormRoot = criteriaQuery.from(DynamicForm.class);
		criteriaQuery.select(dynamicFormRoot.get(DynamicForm_.tabName)).distinct(true);

		Join<DynamicForm, Company> dynamicFormCompanyJoin = dynamicFormRoot.join(DynamicForm_.company);

		Join<DynamicForm, EntityMaster> dynamicFormEntityJoin = dynamicFormRoot.join(DynamicForm_.entityMaster);

		Path<Long> companyID = dynamicFormCompanyJoin.get(Company_.companyId);

		Path<Long> entityID = dynamicFormEntityJoin.get(EntityMaster_.entityId);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(entityID, entityId));

		Subquery<Long> subquery = criteriaQuery.subquery(Long.class);
		Root<Company> fromCompany = subquery.from(Company.class);
		subquery.select(fromCompany.get(Company_.companyId).as(Long.class));

		Join<Company, CompanyGroup> dynamicCompanyGroupJoin = fromCompany.join(Company_.companyGroup);

		Path<Long> subGroupId = dynamicCompanyGroupJoin.get(CompanyGroup_.groupId);

		Predicate subRestriction = cb.conjunction();

		subRestriction = cb.and(subRestriction, cb.equal(subGroupId, companyGroupId));

		subquery.where(subRestriction);

		restriction = cb.and(restriction, cb.in(companyID).value(subquery));

		criteriaQuery.where(restriction);

		TypedQuery<String> dynamicFormTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<String> tabList = dynamicFormTypedQuery.getResultList();

		return tabList;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.DynamicFormDAO#findByCondition(long, long,
	 * java.lang.String)
	 */
	@Override
	public DynamicForm findByCondition(long companyId, long entityId, Integer year, Long month, Integer part) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<DynamicForm> criteriaQuery = cb.createQuery(DynamicForm.class);
		Root<DynamicForm> dynamicFormRoot = criteriaQuery.from(DynamicForm.class);
		criteriaQuery.select(dynamicFormRoot);
		Join<DynamicForm, Company> dynamicFormCompanyJoin = dynamicFormRoot.join(DynamicForm_.company);

		Join<DynamicForm, EntityMaster> dynamicFormEntityJoin = dynamicFormRoot.join(DynamicForm_.entityMaster);
		Join<DynamicForm, MonthMaster> dynamicFormMonthMasterJoin = dynamicFormRoot.join(DynamicForm_.monthMaster);

		Path<Long> companyID = dynamicFormCompanyJoin.get(Company_.companyId);

		Path<Long> entityID = dynamicFormEntityJoin.get(EntityMaster_.entityId);

		Path<Long> monthID = dynamicFormMonthMasterJoin.get(MonthMaster_.monthId);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(companyID, companyId));

		restriction = cb.and(restriction, cb.equal(entityID, entityId));

		restriction = cb.and(restriction, cb.equal(dynamicFormRoot.get(DynamicForm_.effectiveYear), year));
		restriction = cb.and(restriction, cb.equal(dynamicFormRoot.get(DynamicForm_.effectivePart), part));
		restriction = cb.and(restriction, cb.equal(monthID, month));
		criteriaQuery.where(restriction);

		TypedQuery<DynamicForm> dynamicFormQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<DynamicForm> dynamicForms = dynamicFormQuery.getResultList();

		if (dynamicForms != null && !dynamicForms.isEmpty()) {
			return dynamicForms.get(0);
		}
		return null;

	}

	@Override
	public DynamicForm saveAndReturn(DynamicForm dynamicForm) {
		DynamicForm persistObj = dynamicForm;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (DynamicForm) getBaseEntity();
			beanUtil.copyProperties(persistObj, dynamicForm);
		} catch (IllegalAccessException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		} catch (InvocationTargetException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		}
		this.entityManagerFactory.persist(persistObj);
		return persistObj;
	}

	@Override
	public Integer getDynamicFormCount(Long companyId, String entityName) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Integer> criteriaQuery = cb.createQuery(Integer.class);
		Root<DynamicForm> dynamicFormRoot = criteriaQuery.from(DynamicForm.class);
		criteriaQuery.select(cb.count(dynamicFormRoot).as(Integer.class));

		Join<DynamicForm, Company> dynamicFormCompanyJoin = dynamicFormRoot.join(DynamicForm_.company);

		Join<DynamicForm, EntityMaster> dynamicFormEntityJoin = dynamicFormRoot.join(DynamicForm_.entityMaster);

		Path<Long> companyID = dynamicFormCompanyJoin.get(Company_.companyId);

		Predicate restriction = cb.conjunction();

		if (companyId != null) {

			restriction = cb.and(restriction, cb.equal(companyID, companyId));

		}

		if (StringUtils.isNotBlank(entityName)) {

			restriction = cb.and(restriction,
					cb.equal(dynamicFormEntityJoin.get(EntityMaster_.entityName), entityName));
		}

		criteriaQuery.where(restriction);

		TypedQuery<Integer> typedQuery = entityManagerFactory.createQuery(criteriaQuery);
		if (typedQuery.getResultList().size() > 0) {
			return typedQuery.getSingleResult();
		}

		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.DynamicFormDAO#getDistinctFormIdForCompanyIRFMY(java.
	 * lang.Long, java.lang.Long, boolean, java.util.List)
	 */
	@Override
	public List<Long> getDistinctFormIdForCompanyGroupIRF(Long companyId, Long entityId, boolean isCompanyGroupIFR,
			List<String> tabListForCompanyGroupIRF) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
		Root<DynamicForm> dynamicFormRoot = criteriaQuery.from(DynamicForm.class);

		criteriaQuery.select(dynamicFormRoot.get(DynamicForm_.id).get("formId").as(Long.class)).distinct(true);

		Join<DynamicForm, Company> dynamicFormCompanyJoin = dynamicFormRoot.join(DynamicForm_.company);

		Join<DynamicForm, EntityMaster> dynamicFormEntityJoin = dynamicFormRoot.join(DynamicForm_.entityMaster);

		Path<Long> companyID = dynamicFormCompanyJoin.get(Company_.companyId);

		Path<Long> entityID = dynamicFormEntityJoin.get(EntityMaster_.entityId);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(companyID, companyId));

		restriction = cb.and(restriction, cb.equal(entityID, entityId));

		if (isCompanyGroupIFR) {
			restriction = cb.and(restriction,
					cb.not(dynamicFormRoot.get(DynamicForm_.tabName).in(tabListForCompanyGroupIRF)));
		}

		criteriaQuery.where(restriction);

		TypedQuery<Long> dynamicFormTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<Long> formList = dynamicFormTypedQuery.getResultList();

		return formList;

	}

	@Override
	public List<Tuple> getDistinctSectionNameAndFormId(Long companyId, Long entityId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Tuple> criteriaQuery = cb.createQuery(Tuple.class);
		Root<DynamicForm> dynamicFormRoot = criteriaQuery.from(DynamicForm.class);

		List<Selection<?>> selectionItems = new ArrayList<Selection<?>>();

		Join<DynamicForm, DynamicFormPK> dynamicFormPkJoin = dynamicFormRoot.join(DynamicForm_.id);

		selectionItems.add(dynamicFormRoot.get(DynamicForm_.tabName).alias(getAlias(DynamicForm_.tabName)));
		selectionItems.add(dynamicFormPkJoin.get(DynamicFormPK_.formId).alias(getAlias(DynamicFormPK_.formId)));
		criteriaQuery.multiselect(selectionItems).distinct(true);

		Join<DynamicForm, Company> dynamicFormCompanyJoin = dynamicFormRoot.join(DynamicForm_.company);

		Join<DynamicForm, EntityMaster> dynamicFormEntityJoin = dynamicFormRoot.join(DynamicForm_.entityMaster);

		Path<Long> companyID = dynamicFormCompanyJoin.get(Company_.companyId);

		Path<Long> entityID = dynamicFormEntityJoin.get(EntityMaster_.entityId);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(companyID, companyId));

		restriction = cb.and(restriction, cb.equal(entityID, entityId));

		criteriaQuery.where(restriction);

		TypedQuery<Tuple> dynamicFormTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<Tuple> tabList = dynamicFormTypedQuery.getResultList();

		return tabList;

	}

	// @Override
	// public List<Object[]> getTabNameAndFormIdByMaxVersion(long companyId,
	// Long entityId, ManageRolesConditionDTO conditionDTO) {
	// String queryString = "select tmp.Form_ID,df.Tab_Name from ";
	// queryString +=
	// "(select form_id,max(version) as version from dynamic_form where
	// company_id=:companyId "
	// + " and entity_id=:entityId ";
	// if (StringUtils.isNotBlank(conditionDTO.getSection())) {
	// queryString += " and Tab_Name like :sectionName";
	// }
	// queryString += " group by form_id) tmp ";
	// queryString +=
	// "inner join dynamic_form df on df.form_id = tmp.form_id and df.Version =
	// tmp.version ";
	// Query q = entityManagerFactory.createNativeQuery(queryString);
	// q.setParameter("companyId", companyId);
	// q.setParameter("entityId", entityId);
	// q.setParameter("sectionName", conditionDTO.getSection() + "%");
	// List<Object[]> tupleList = q.getResultList();
	//
	// return tupleList;
	//
	// }

	@Override
	public List<Object[]> getTabNameAndFormIdByMaxVersion(long companyId, Long entityId,
			ManageRolesConditionDTO conditionDTO) {
		String queryString = "select tmp.Form_ID,df.Tab_Name from ";
		queryString += "(select form_id,max(version) as version from dynamic_form  where company_id= " + companyId
				+ " and entity_id= " + entityId;
		if (StringUtils.isNotBlank(conditionDTO.getSection())) {
			queryString += " and Tab_Name like '" + conditionDTO.getSection() + "%'";
		}
		queryString += " group by form_id) tmp ";
		queryString += "inner join dynamic_form df on  df.form_id = tmp.form_id and df.Version = tmp.version ";
		Query q = entityManagerFactory.createNativeQuery(queryString);

		return q.getResultList();

	}

	@Override
	public List<Long> getAuthorizedDistinctFormIds(Long companyId, Long entityId, Set<Long> sectionIds) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
		Root<DynamicForm> dynamicFormRoot = criteriaQuery.from(DynamicForm.class);

		criteriaQuery.select(dynamicFormRoot.get(DynamicForm_.id).get("formId").as(Long.class)).distinct(true);

		Join<DynamicForm, Company> dynamicFormCompanyJoin = dynamicFormRoot.join(DynamicForm_.company);

		Join<DynamicForm, DynamicFormPK> dynamicFormPKJoin = dynamicFormRoot.join(DynamicForm_.id);

		Join<DynamicForm, EntityMaster> dynamicFormEntityJoin = dynamicFormRoot.join(DynamicForm_.entityMaster);

		Path<Long> companyID = dynamicFormCompanyJoin.get(Company_.companyId);

		Path<Long> entityID = dynamicFormEntityJoin.get(EntityMaster_.entityId);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(companyID, companyId));

		restriction = cb.and(restriction, cb.equal(entityID, entityId));

		restriction = cb.and(restriction, dynamicFormPKJoin.get(DynamicFormPK_.formId).in(sectionIds));

		criteriaQuery.where(restriction);

		TypedQuery<Long> dynamicFormTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<Long> formList = dynamicFormTypedQuery.getResultList();

		return formList;

	}
}
