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

import org.springframework.stereotype.Repository;

import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.SortConstants;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.ClaimCategoryDAO;
import com.payasia.dao.bean.ClaimCategoryMaster;
import com.payasia.dao.bean.ClaimCategoryMaster_;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Company_;

@Repository
public class ClaimCategoryDAOImpl extends BaseDAO implements ClaimCategoryDAO {

	@Override
	public List<ClaimCategoryMaster> getCategoryAll(long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ClaimCategoryMaster> criteriaQuery = cb.createQuery(ClaimCategoryMaster.class);

		Root<ClaimCategoryMaster> claimCatRoot = criteriaQuery.from(ClaimCategoryMaster.class);
		criteriaQuery.select(claimCatRoot);
		Join<ClaimCategoryMaster, Company> claimCategoryCompanyJoin = claimCatRoot.join(ClaimCategoryMaster_.company);
		Predicate restriction = cb.equal(claimCategoryCompanyJoin.get(Company_.companyId), companyId);

		criteriaQuery.where(restriction);
		criteriaQuery.orderBy(cb.asc(claimCatRoot.get(ClaimCategoryMaster_.claimCategoryId)));

		TypedQuery<ClaimCategoryMaster> claimItemTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		return claimItemTypedQuery.getResultList();

	}

	@Override
	public ClaimCategoryMaster findCategoryById(long categoryId) {
		return super.findById(ClaimCategoryMaster.class, categoryId);
	}

	@Override
	protected Object getBaseEntity() {
		ClaimCategoryMaster claimCatMaster = new ClaimCategoryMaster();
		return claimCatMaster;
	}

	@Override
	public List<ClaimCategoryMaster> getClaimCategory(PageRequest pageDTO, SortCondition sortDTO, Long companyID) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ClaimCategoryMaster> criteriaQuery = cb.createQuery(ClaimCategoryMaster.class);

		Root<ClaimCategoryMaster> claimCatRoot = criteriaQuery.from(ClaimCategoryMaster.class);

		criteriaQuery.select(claimCatRoot);

		Join<ClaimCategoryMaster, Company> claimCatCompanyJoin = claimCatRoot.join(ClaimCategoryMaster_.company);

		Path<Long> companyId = claimCatCompanyJoin.get(Company_.companyId);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(companyId, companyID));
		criteriaQuery.where(restriction);

		if (sortDTO != null) {

			Path<String> sortPath = getSortPathForclaimCat(sortDTO, claimCatRoot);
			if (sortPath != null) {
				if (SortConstants.DB_ORDER_BY_ASC.equalsIgnoreCase(sortDTO.getOrderType())) {
					criteriaQuery.orderBy(cb.asc(sortPath));
				}
				if (SortConstants.DB_ORDER_BY_DESC.equalsIgnoreCase(sortDTO.getOrderType())) {
					criteriaQuery.orderBy(cb.desc(sortPath));
				}
			}

		}

		TypedQuery<ClaimCategoryMaster> claimCatTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			claimCatTypedQuery.setFirstResult(getStartPosition(pageDTO));
			claimCatTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		List<ClaimCategoryMaster> ClaimCatList = claimCatTypedQuery.getResultList();

		return ClaimCatList;

	}

	@Override
	public Path<String> getSortPathForclaimCat(SortCondition sortDTO, Root<ClaimCategoryMaster> claimCatRoot) {
		List<String> claimCatColList = new ArrayList<String>();

		claimCatColList.add(SortConstants.CLAIM_CATEGORY_CODE);
		claimCatColList.add(SortConstants.CLAIM_CATEGORY_DESCRIPTION);
		claimCatColList.add(SortConstants.CLAIM_CATEGORY_NAME);

		Path<String> sortPath = null;

		if (claimCatColList.contains(sortDTO.getColumnName())) {
			sortPath = claimCatRoot.get(colMap.get(ClaimCategoryMaster.class + sortDTO.getColumnName()));
		}

		return sortPath;
	}

	@Override
	public void saveClaimCat(ClaimCategoryMaster claimCatMaster) {
		super.save(claimCatMaster);
	}

	@Override
	public void update(ClaimCategoryMaster claimCatMaster) {
		super.update(claimCatMaster);
	}

	@Override
	public void delete(ClaimCategoryMaster claimCatMaster) {
		super.delete(claimCatMaster);
	}

	@Override
	public ClaimCategoryMaster findByNameCompany(Long categoryId, String categoryName, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ClaimCategoryMaster> criteriaQuery = cb.createQuery(ClaimCategoryMaster.class);

		Root<ClaimCategoryMaster> claimCategoryRoot = criteriaQuery.from(ClaimCategoryMaster.class);

		criteriaQuery.select(claimCategoryRoot);

		Join<ClaimCategoryMaster, Company> claimCategoryCompanyJoin = claimCategoryRoot
				.join(ClaimCategoryMaster_.company);

		Predicate restriction = cb.equal(claimCategoryCompanyJoin.get(Company_.companyId), companyId);

		restriction = cb.and(restriction, cb.equal(
				cb.upper(claimCategoryRoot.get(ClaimCategoryMaster_.claimCategoryName)), categoryName.toUpperCase()));

		if (categoryId != null) {
			restriction = cb.and(restriction,
					cb.notEqual(claimCategoryRoot.get(ClaimCategoryMaster_.claimCategoryId), categoryId));
		}

		criteriaQuery.where(restriction);

		TypedQuery<ClaimCategoryMaster> claimCategoryTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<ClaimCategoryMaster> claimCategoryList = claimCategoryTypedQuery.getResultList();
		if (claimCategoryList != null && !claimCategoryList.isEmpty()) {
			return claimCategoryList.get(0);
		}
		return null;
	}

	@Override
	public ClaimCategoryMaster findByCodeCompany(Long categoryId, String categoryCode, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ClaimCategoryMaster> criteriaQuery = cb.createQuery(ClaimCategoryMaster.class);

		Root<ClaimCategoryMaster> claimCategoryRoot = criteriaQuery.from(ClaimCategoryMaster.class);

		criteriaQuery.select(claimCategoryRoot);

		Join<ClaimCategoryMaster, Company> claimCategoryCompanyJoin = claimCategoryRoot
				.join(ClaimCategoryMaster_.company);

		Predicate restriction = cb.equal(claimCategoryCompanyJoin.get(Company_.companyId), companyId);

		restriction = cb.and(restriction,
				cb.equal(cb.upper(claimCategoryRoot.get(ClaimCategoryMaster_.code)), categoryCode.toUpperCase()));

		if (categoryId != null) {
			restriction = cb.and(restriction,
					cb.notEqual(claimCategoryRoot.get(ClaimCategoryMaster_.claimCategoryId), categoryId));
		}

		criteriaQuery.where(restriction);

		TypedQuery<ClaimCategoryMaster> claimCategoryTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<ClaimCategoryMaster> claimCategoryList = claimCategoryTypedQuery.getResultList();
		if (claimCategoryList != null && !claimCategoryList.isEmpty()) {
			return claimCategoryList.get(0);
		}
		return null;
	}

	@Override
	public ClaimCategoryMaster findByClaimCategoryId(Long claimCategoryId, Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();

		CriteriaQuery<ClaimCategoryMaster> criteriaQuery = cb.createQuery(ClaimCategoryMaster.class);

		Root<ClaimCategoryMaster> claimCategoryRoot = criteriaQuery.from(ClaimCategoryMaster.class);

		criteriaQuery.select(claimCategoryRoot);

		Join<ClaimCategoryMaster, Company> claimCategoryCompanyJoin = claimCategoryRoot
				.join(ClaimCategoryMaster_.company);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(claimCategoryRoot.get(ClaimCategoryMaster_.claimCategoryId), claimCategoryId));
		restriction = cb.and(restriction, cb.equal(claimCategoryCompanyJoin.get(Company_.companyId), companyId));

		criteriaQuery.where(restriction);

		TypedQuery<ClaimCategoryMaster> claimCategoryTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<ClaimCategoryMaster> claimCategoryList = claimCategoryTypedQuery.getResultList();
		if (claimCategoryList != null && !claimCategoryList.isEmpty()) {
			return claimCategoryList.get(0);
		}
		return null;

	}

}
