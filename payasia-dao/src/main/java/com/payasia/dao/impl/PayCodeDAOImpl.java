package com.payasia.dao.impl;

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

import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.SortConstants;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.PayCodeDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.Paycode;
import com.payasia.dao.bean.Paycode_;

/**
 * The Class PayCodeDAOImpl.
 */
/**
 * @author vivekjain
 * 
 */
@Repository
public class PayCodeDAOImpl extends BaseDAO implements PayCodeDAO {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.PayCodeDAO#update(com.payasia.dao.bean.Paycode)
	 */
	@Override
	public void update(Paycode paycode) {
		super.update(paycode);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.PayCodeDAO#save(com.payasia.dao.bean.Paycode)
	 */
	@Override
	public void save(Paycode paycode) {
		super.save(paycode);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.PayCodeDAO#delete(com.payasia.dao.bean.Paycode)
	 */
	@Override
	public void delete(Paycode paycode) {
		super.delete(paycode);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.PayCodeDAO#findByID(long)
	 */
	@Override
	public Paycode findByID(long payCodeId) {
		return super.findById(Paycode.class, payCodeId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.BaseDAO#getBaseEntity()
	 */
	@Override
	protected Object getBaseEntity() {
		Paycode paycode = new Paycode();
		return paycode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.PayCodeDAO#getAllPayCodeByConditionCompany(java.lang.
	 * Long, com.payasia.common.form.PageRequest,
	 * com.payasia.common.form.SortCondition)
	 */
	@Override
	public List<Paycode> getAllPayCodeByConditionCompany(Long companyId,
			PageRequest pageDTO, SortCondition sortDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Paycode> criteriaQuery = cb.createQuery(Paycode.class);
		Root<Paycode> payCodeRoot = criteriaQuery.from(Paycode.class);
		criteriaQuery.select(payCodeRoot);

		Join<Paycode, Company> payCodeRootCompanyJoin = payCodeRoot
				.join(Paycode_.company);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(
				payCodeRootCompanyJoin.get(Company_.companyId), companyId));

		criteriaQuery.where(restriction);

		if (sortDTO != null) {

			Path<String> sortPath = getSortPathForPayCode(sortDTO, payCodeRoot);
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

		TypedQuery<Paycode> payCodeTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			payCodeTypedQuery.setFirstResult(getStartPosition(pageDTO));
			payCodeTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		List<Paycode> payCodeList = payCodeTypedQuery.getResultList();

		return payCodeList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.PayCodeDAO#getSortPathForPayCode(com.payasia.common.form
	 * .SortCondition, javax.persistence.criteria.Root)
	 */
	@Override
	public Path<String> getSortPathForPayCode(SortCondition sortDTO,
			Root<Paycode> payCodeRoot) {

		List<String> payCodeIsColList = new ArrayList<String>();
		payCodeIsColList.add(SortConstants.PAYDATA_COLLECTION_PAYCODE);

		Path<String> sortPath = null;

		if (payCodeIsColList.contains(sortDTO.getColumnName())) {
			sortPath = payCodeRoot.get(colMap.get(Paycode.class
					+ sortDTO.getColumnName()));
		}
		return sortPath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.PayCodeDAO#getPayCode(java.lang.String,
	 * java.lang.Long)
	 */
	@Override
	public List<Paycode> getPayCode(String searchString, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Paycode> criteriaQuery = cb.createQuery(Paycode.class);
		Root<Paycode> payCodeRoot = criteriaQuery.from(Paycode.class);
		criteriaQuery.select(payCodeRoot);

		Join<Paycode, Company> payCodeRootCompanyJoin = payCodeRoot
				.join(Paycode_.company);

		Predicate restriction = cb.conjunction();

		if (StringUtils.isNotBlank(searchString.trim())) {

			restriction = cb.and(
					restriction,
					cb.like(payCodeRoot.get(Paycode_.paycode),
							searchString.trim() + "%"));

		}
		restriction = cb.and(restriction, cb.equal(
				payCodeRootCompanyJoin.get(Company_.companyId), companyId));
		criteriaQuery.where(restriction);

		TypedQuery<Paycode> payCodeTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<Paycode> payCodeList = payCodeTypedQuery.getResultList();

		return payCodeList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.PayCodeDAO#getPayCodeByName(java.lang.String,
	 * java.lang.Long)
	 */
	@Override
	public Paycode getPayCodeByName(String searchString, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Paycode> criteriaQuery = cb.createQuery(Paycode.class);
		Root<Paycode> payCodeRoot = criteriaQuery.from(Paycode.class);
		criteriaQuery.select(payCodeRoot);

		Join<Paycode, Company> payCodeRootCompanyJoin = payCodeRoot
				.join(Paycode_.company);

		Predicate restriction = cb.conjunction();

		if (StringUtils.isNotBlank(searchString.trim())) {

			restriction = cb.and(restriction, cb.equal(
					cb.upper(payCodeRoot.get(Paycode_.paycode)),
					searchString.toUpperCase()));

		}
		restriction = cb.and(restriction, cb.equal(
				payCodeRootCompanyJoin.get(Company_.companyId), companyId));
		criteriaQuery.where(restriction);

		TypedQuery<Paycode> payCodeTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<Paycode> payCodeList = payCodeTypedQuery.getResultList();
		if (payCodeList != null &&  !payCodeList.isEmpty()) {
			return payCodeList.get(0);
		}
		return null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.PayCodeDAO#getCountForAllPayCode(java.lang.Long)
	 */
	@Override
	public int getCountForAllPayCode(Long companyId) {
		return getAllPayCodeByConditionCompany(companyId, null, null).size();
	}
}
