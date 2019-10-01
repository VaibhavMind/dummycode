package com.payasia.dao.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.payasia.common.dto.HRLetterConditionDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.NullAwareBeanUtilsBean;
import com.payasia.common.util.SortConstants;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.HRLetterDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.HRLetter;
import com.payasia.dao.bean.HRLetter_;

/**
 * The Class HRLetterDAOImpl.
 */
/**
 * @author vivekjain
 * 
 */
@Repository
public class HRLetterDAOImpl extends BaseDAO implements HRLetterDAO {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.HRLetterDAO#save(com.payasia.dao.bean.HRLetter)
	 */
	@Override
	public HRLetter save(HRLetter hrLetter) {

		HRLetter persistObj = hrLetter;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (HRLetter) getBaseEntity();
			beanUtil.copyProperties(persistObj, hrLetter);
		} catch (IllegalAccessException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		} catch (InvocationTargetException e) {
			throw new PayAsiaSystemException("errors.dao", e);
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

		HRLetter hrLetter = new HRLetter();
		return hrLetter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.HRLetterDAO#findByCompany(long,
	 * com.payasia.common.form.PageRequest,
	 * com.payasia.common.form.SortCondition)
	 */
	@Override
	public List<HRLetter> findByCompany(long companyId, PageRequest pageDTO,
			SortCondition sortDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<HRLetter> criteriaQuery = cb.createQuery(HRLetter.class);
		Root<HRLetter> hrLetterRoot = criteriaQuery.from(HRLetter.class);

		criteriaQuery.select(hrLetterRoot);

		Join<HRLetter, Company> hrLetterRootJoin = hrLetterRoot
				.join(HRLetter_.company);

		Path<Long> companyID = hrLetterRootJoin.get(Company_.companyId);

		criteriaQuery.where(cb.equal(companyID, companyId));

		if (sortDTO != null) {

			Path<String> sortPath = getSortPathForHRLetter(sortDTO,
					hrLetterRoot, hrLetterRootJoin);
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

		 

		TypedQuery<HRLetter> hrLetterTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			hrLetterTypedQuery.setFirstResult(getStartPosition(pageDTO));
			hrLetterTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		List<HRLetter> hrLetterList = hrLetterTypedQuery.getResultList();

		return hrLetterList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.HRLetterDAO#getSortPathForHRLetter(com.payasia.common
	 * .form.SortCondition, javax.persistence.criteria.Root,
	 * javax.persistence.criteria.Join)
	 */
	@Override
	public Path<String> getSortPathForHRLetter(SortCondition sortDTO,
			Root<HRLetter> hrLetterRoot,
			Join<HRLetter, Company> hrLetterRootJoin) {

		List<String> hrLetterIsIdList = new ArrayList<String>();
		hrLetterIsIdList.add(SortConstants.HR_LETTER_ID);

		List<String> hrLetterIsCompanyIdList = new ArrayList<String>();
		hrLetterIsCompanyIdList.add(SortConstants.HR_LETTER_COMPANY_ID);

		List<String> hrLetterIsColList = new ArrayList<String>();
		hrLetterIsColList.add(SortConstants.HR_LETTER_NAME);
		hrLetterIsColList.add(SortConstants.HR_LETTER_DESCRIPTION);
		hrLetterIsColList.add(SortConstants.EMPLOYEE_PHONE);
		hrLetterIsColList.add(SortConstants.HR_LETTER_SUBJECT);
		hrLetterIsColList.add(SortConstants.HR_LETTER_ACTIVE);
		
		Path<String> sortPath = null;

		if (hrLetterIsColList.contains(sortDTO.getColumnName())) {
			sortPath = hrLetterRoot.get(colMap.get(HRLetter.class
					+ sortDTO.getColumnName()));
		}
		return sortPath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.HRLetterDAO#delete(com.payasia.dao.bean.HRLetter)
	 */
	@Override
	public void delete(HRLetter hrLetter) {

		super.delete(hrLetter);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.HRLetterDAO#findById(long)
	 */
	@Override
	public HRLetter findById(long letterId) {

		HRLetter hrLetter = super.findById(HRLetter.class, letterId);
		return hrLetter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.HRLetterDAO#update(com.payasia.dao.bean.HRLetter)
	 */
	@Override
	public void update(HRLetter hrLetter) {
		super.update(hrLetter);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.HRLetterDAO#getCountHRLetterByCompany(java.lang.Long)
	 */
	@Override
	public int getCountHRLetterByCondition(HRLetterConditionDTO conditionDTO,
			Long companyId, boolean isActive) {
		return findByCondition(conditionDTO, companyId, isActive, null, null)
				.size();
	}

	@Override
	public int getCountHRLetterByCompany(Long companyId) {
		return findByCompany(companyId, null, null).size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.HRLetterDAO#findByCondition(com.payasia.common.dto.
	 * HRLetterConditionDTO, java.lang.Long,
	 * com.payasia.common.form.PageRequest,
	 * com.payasia.common.form.SortCondition)
	 */
	@Override
	public List<HRLetter> findByCondition(HRLetterConditionDTO conditionDTO,
			Long companyId, boolean isActive, PageRequest pageDTO,
			SortCondition sortDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<HRLetter> criteriaQuery = cb.createQuery(HRLetter.class);
		Root<HRLetter> hrLetterRoot = criteriaQuery.from(HRLetter.class);

		criteriaQuery.select(hrLetterRoot);

		Join<HRLetter, Company> hrLetterCompanyJoin = hrLetterRoot
				.join(HRLetter_.company);

		Predicate restriction = cb.conjunction();

		if (StringUtils.isNotBlank(conditionDTO.getLetterName())) {

			restriction = cb.and(restriction, cb.like(
					cb.upper(hrLetterRoot.get(HRLetter_.letterName)),
					conditionDTO.getLetterName().toUpperCase()));
		}

		if (StringUtils.isNotBlank(conditionDTO.getLetterDescription())) {

			restriction = cb.and(restriction, cb.like(
					cb.upper(hrLetterRoot.get(HRLetter_.letterDesc)),
					conditionDTO.getLetterDescription().toUpperCase()));
		}

		if (StringUtils.isNotBlank(conditionDTO.getLetterSubject())) {

			restriction = cb.and(restriction, cb.like(cb.upper(hrLetterRoot
					.get(HRLetter_.subject)), conditionDTO.getLetterSubject()
					.toUpperCase()));
		}
		if (isActive) {
			restriction = cb.and(restriction,
					cb.equal(hrLetterRoot.get(HRLetter_.active), isActive));
		}

		restriction = cb.and(restriction, cb.equal(
				hrLetterCompanyJoin.get(Company_.companyId), companyId));
		criteriaQuery.where(restriction);

		if (sortDTO != null) {

			Path<String> sortPath = getSortPathForHRLetter(sortDTO,
					hrLetterRoot, hrLetterCompanyJoin);
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

		TypedQuery<HRLetter> hrLetterTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			hrLetterTypedQuery.setFirstResult(getStartPosition(pageDTO));
			hrLetterTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		List<HRLetter> hrLetterList = hrLetterTypedQuery.getResultList();

		return hrLetterList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.HRLetterDAO#findByLetterAndCompany(java.lang.Long,
	 * java.lang.String, long)
	 */
	@Override
	public HRLetter findByLetterAndCompany(Long letterId, String letterName,
			long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<HRLetter> criteriaQuery = cb.createQuery(HRLetter.class);
		Root<HRLetter> hrLetterRoot = criteriaQuery.from(HRLetter.class);

		criteriaQuery.select(hrLetterRoot);

		Join<HRLetter, Company> hrLetterRootJoin = hrLetterRoot
				.join(HRLetter_.company);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(hrLetterRootJoin.get(Company_.companyId), companyId));

		if (letterId != null) {
			restriction = cb.and(restriction, cb.notEqual(
					hrLetterRoot.get(HRLetter_.hrLetterId), letterId));
		}

		restriction = cb.and(restriction, cb.equal(
				cb.upper(hrLetterRoot.get(HRLetter_.letterName)),
				letterName.toUpperCase()));

		criteriaQuery.where(restriction);

		TypedQuery<HRLetter> hrLetterTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<HRLetter> hrLetterList = hrLetterTypedQuery.getResultList();
		if (hrLetterList != null &&  !hrLetterList.isEmpty()) {
			return hrLetterList.get(0);
		}
		return null;

	}
	
	@Override
	public HRLetter findByHRLetterId(long letterId, Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<HRLetter> criteriaQuery = cb.createQuery(HRLetter.class);
		Root<HRLetter> hrLetterRoot = criteriaQuery.from(HRLetter.class);

		criteriaQuery.select(hrLetterRoot);

		Join<HRLetter, Company> hrLetterRootJoin = hrLetterRoot.join(HRLetter_.company);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(hrLetterRootJoin.get(Company_.companyId), companyId));

		restriction = cb.and(restriction, cb.equal(hrLetterRoot.get(HRLetter_.hrLetterId), letterId));

		criteriaQuery.where(restriction);

		TypedQuery<HRLetter> hrLetterTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<HRLetter> hrLetterList = hrLetterTypedQuery.getResultList();
		if (hrLetterList != null && !hrLetterList.isEmpty()) {
			return hrLetterList.get(0);
		}
		return null;

	}
	}


