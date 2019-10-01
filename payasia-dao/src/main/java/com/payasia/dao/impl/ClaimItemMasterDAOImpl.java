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

import com.payasia.common.dto.ClaimItemDTO;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.SortConstants;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.ClaimItemMasterDAO;
import com.payasia.dao.bean.ClaimCategoryMaster;
import com.payasia.dao.bean.ClaimCategoryMaster_;
import com.payasia.dao.bean.ClaimItemMaster;
import com.payasia.dao.bean.ClaimItemMaster_;
import com.payasia.dao.bean.ClaimTemplate;
import com.payasia.dao.bean.ClaimTemplateItem;
import com.payasia.dao.bean.ClaimTemplateItem_;
import com.payasia.dao.bean.ClaimTemplate_;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Company_;

@Repository
public class ClaimItemMasterDAOImpl extends BaseDAO implements ClaimItemMasterDAO {

	@Override
	public List<ClaimItemMaster> getClaimItemByCondition(PageRequest pageDTO, SortCondition sortDTO,
			ClaimItemDTO claimItemDTO, Long companyID) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ClaimItemMaster> criteriaQuery = cb

				.createQuery(ClaimItemMaster.class);
		Root<ClaimItemMaster> claimItemRoot = criteriaQuery.from(ClaimItemMaster.class);

		criteriaQuery.select(claimItemRoot);

		Join<ClaimItemMaster, Company> claimItemCompanyJoin = claimItemRoot.join(ClaimItemMaster_.company);

		Join<ClaimItemMaster, ClaimCategoryMaster> claimItemCategoryJoin = claimItemRoot
				.join(ClaimItemMaster_.claimCategoryMaster);

		Path<Long> companyId = claimItemCompanyJoin.get(Company_.companyId);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(companyId, companyID));

		if (StringUtils.isNotBlank(claimItemDTO.getAccountCode())) {

			restriction = cb.and(restriction, cb.like(cb.upper(claimItemRoot.get(ClaimItemMaster_.accountCode)),
					claimItemDTO.getAccountCode().toUpperCase()));
		}
		if (StringUtils.isNotBlank(claimItemDTO.getClaimCategory())) {

			restriction = cb.and(restriction,
					cb.like(cb.upper(claimItemCategoryJoin.get(ClaimCategoryMaster_.claimCategoryName)),
							claimItemDTO.getClaimCategory().toUpperCase()));
		}
		if (StringUtils.isNotBlank(claimItemDTO.getCode())) {

			restriction = cb.and(restriction,
					cb.like(cb.upper(claimItemRoot.get(ClaimItemMaster_.code)), claimItemDTO.getCode().toUpperCase()));
		}
		if (StringUtils.isNotBlank(claimItemDTO.getDescription())) {

			restriction = cb.and(restriction, cb.like(cb.upper(claimItemRoot.get(ClaimItemMaster_.claimItemDesc)),
					claimItemDTO.getDescription().toUpperCase()));
		}
		if (StringUtils.isNotBlank(claimItemDTO.getName())) {

			restriction = cb.and(restriction, cb.like(cb.upper(claimItemRoot.get(ClaimItemMaster_.claimItemName)),
					claimItemDTO.getName().toUpperCase()));
		}
		if (StringUtils.isNotBlank(claimItemDTO.getVisibility())) {
			restriction = cb.and(restriction, cb.equal(claimItemRoot.get(ClaimItemMaster_.visibility),
					Boolean.parseBoolean(claimItemDTO.getVisibility())));
		}

		criteriaQuery.where(restriction);

		criteriaQuery.orderBy(cb.asc(claimItemRoot.get(ClaimItemMaster_.sortOrder)));

		if (sortDTO != null) {

			Path<String> sortPath = getSortPathForclaimItem(sortDTO, claimItemRoot, claimItemCategoryJoin);
			if (sortPath != null) {
				if (SortConstants.DB_ORDER_BY_ASC.equalsIgnoreCase(sortDTO.getOrderType())) {
					criteriaQuery.orderBy(cb.asc(sortPath));
				}
				if (SortConstants.DB_ORDER_BY_DESC.equalsIgnoreCase(sortDTO.getOrderType())) {
					criteriaQuery.orderBy(cb.desc(sortPath));
				}
			}

		}

		TypedQuery<ClaimItemMaster> claimItemTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			claimItemTypedQuery.setFirstResult(getStartPosition(pageDTO));
			claimItemTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		List<ClaimItemMaster> conditionClaimItemList = claimItemTypedQuery.getResultList();

		return conditionClaimItemList;

	}

	@Override
	public Path<String> getSortPathForclaimItem(SortCondition sortDTO, Root<ClaimItemMaster> claimItemRoot,
			Join<ClaimItemMaster, ClaimCategoryMaster> claimItemCategoryJoin) {

		List<String> claimItemColList = new ArrayList<String>();
		claimItemColList.add(SortConstants.CLAIM_ITEM_NAME);
		claimItemColList.add(SortConstants.CLAIM_ITEM_CODE);
		claimItemColList.add(SortConstants.CLAIM_ITEM_ACCOUNT_CODE);
		claimItemColList.add(SortConstants.CLAIM_ITEM_VISIBLE_OR_HIDDEN);
		claimItemColList.add(SortConstants.CLAIM_ITEM_DESCRIPTION);

		List<String> claimCategoryColList = new ArrayList<String>();
		claimCategoryColList.add(SortConstants.CLAIM_ITEM_CLAIM_CATEGORY);

		Path<String> sortPath = null;

		if (claimItemColList.contains(sortDTO.getColumnName())) {
			sortPath = claimItemRoot.get(colMap.get(ClaimItemMaster.class + sortDTO.getColumnName()));
		}
		if (claimCategoryColList.contains(sortDTO.getColumnName())) {
			sortPath = claimItemCategoryJoin.get(colMap.get(ClaimCategoryMaster.class + sortDTO.getColumnName()));
		}
		return sortPath;
	}

	@Override
	protected Object getBaseEntity() {
		ClaimItemMaster claimItemMaster = new ClaimItemMaster();
		return claimItemMaster;
	}

	@Override
	public void saveClaimItem(ClaimItemMaster claimItemMaster) {
		super.save(claimItemMaster);
	}

	@Override
	public ClaimItemMaster findByID(Long itemId) {
		return super.findById(ClaimItemMaster.class, itemId);
	}

	@Override
	public void update(ClaimItemMaster claimItemMaster) {
		super.update(claimItemMaster);
	}

	@Override
	public void delete(ClaimItemMaster claimItemMaster) {
		super.delete(claimItemMaster);
	}

	@Override
	public ClaimItemMaster findById(Long claimTypeId) {
		ClaimItemMaster claimItemMaster = super.findById(ClaimItemMaster.class, claimTypeId);
		return claimItemMaster;
	}

	@Override
	public List<ClaimItemMaster> findByCondition(Long claimTemplateId,Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ClaimItemMaster> criteriaQuery = cb.createQuery(ClaimItemMaster.class);
		Root<ClaimItemMaster> claimItemRoot = criteriaQuery.from(ClaimItemMaster.class);

		criteriaQuery.select(claimItemRoot);

		Join<ClaimItemMaster, ClaimTemplateItem> claimItemtemplateTypeJoin = claimItemRoot
				.join(ClaimItemMaster_.claimTemplateItems);

		Join<ClaimTemplateItem, ClaimTemplate> claimTemplateJoin = claimItemtemplateTypeJoin
				.join(ClaimTemplateItem_.claimTemplate);
		
		Join<ClaimItemMaster, Company> companyJoin = claimItemRoot
				.join(ClaimItemMaster_.company);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction,
				cb.equal(claimTemplateJoin.get(ClaimTemplate_.claimTemplateId), claimTemplateId));
		restriction = cb.and(restriction,
				cb.equal(companyJoin.get(Company_.companyId), companyId));


		criteriaQuery.where(restriction);

		criteriaQuery.orderBy(cb.asc(claimItemRoot.get(ClaimItemMaster_.sortOrder)));

		TypedQuery<ClaimItemMaster> claimItemTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<ClaimItemMaster> claimItemList = claimItemTypedQuery.getResultList();

		return claimItemList;
	}

	@Override
	public List<ClaimItemMaster> findAll(Long companyId, PageRequest pageDTO, SortCondition sortDTO,
			Long itemCategoryId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ClaimItemMaster> criteriaQuery = cb.createQuery(ClaimItemMaster.class);
		Root<ClaimItemMaster> claimItemRoot = criteriaQuery.from(ClaimItemMaster.class);

		criteriaQuery.select(claimItemRoot);

		Join<ClaimItemMaster, Company> claimItemMasterCompanyJoin = claimItemRoot.join(ClaimItemMaster_.company);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(claimItemMasterCompanyJoin.get(Company_.companyId), companyId));

		if (itemCategoryId != null) {

			restriction = cb.and(restriction, cb.equal(
					claimItemRoot.get(ClaimItemMaster_.claimCategoryMaster).get("claimCategoryId").as(Long.class),
					itemCategoryId));
		}

		restriction = cb.and(restriction, cb.equal(claimItemRoot.get(ClaimItemMaster_.visibility), true));
		criteriaQuery.where(restriction);

		criteriaQuery.orderBy(cb.asc(claimItemRoot.get(ClaimItemMaster_.sortOrder)));

		TypedQuery<ClaimItemMaster> claimItemTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			claimItemTypedQuery.setFirstResult(getStartPosition(pageDTO));
			claimItemTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		return claimItemTypedQuery.getResultList();

	}

	@Override
	public ClaimItemMaster findByNameCategoryCompany(Long claimItemId, String itemName, Long categoryId,
			Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ClaimItemMaster> criteriaQuery = cb.createQuery(ClaimItemMaster.class);

		Root<ClaimItemMaster> claimItemRoot = criteriaQuery.from(ClaimItemMaster.class);

		criteriaQuery.select(claimItemRoot);

		Join<ClaimItemMaster, Company> claimItemCompanyJoin = claimItemRoot.join(ClaimItemMaster_.company);

		Join<ClaimItemMaster, ClaimCategoryMaster> claimItemCategoryJoin = claimItemRoot
				.join(ClaimItemMaster_.claimCategoryMaster);

		Predicate restriction = cb.equal(claimItemCompanyJoin.get(Company_.companyId), companyId);

		restriction = cb.and(restriction,
				cb.equal(claimItemCategoryJoin.get(ClaimCategoryMaster_.claimCategoryId), categoryId));

		restriction = cb.and(restriction,
				cb.equal(cb.upper(claimItemRoot.get(ClaimItemMaster_.claimItemName)), itemName.toUpperCase()));

		if (claimItemId != null) {
			restriction = cb.and(restriction,
					cb.notEqual(claimItemRoot.get(ClaimItemMaster_.claimItemId), claimItemId));
		}

		criteriaQuery.where(restriction);

		TypedQuery<ClaimItemMaster> claimItemTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<ClaimItemMaster> claimItemList = claimItemTypedQuery.getResultList();
		if (claimItemList != null && !claimItemList.isEmpty()) {
			return claimItemList.get(0);
		}
		return null;
	}

	@Override
	public ClaimItemMaster findByCodeCategoryCompany(Long claimItemId, String itemCode, Long categoryId,
			Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ClaimItemMaster> criteriaQuery = cb.createQuery(ClaimItemMaster.class);

		Root<ClaimItemMaster> claimItemRoot = criteriaQuery.from(ClaimItemMaster.class);

		criteriaQuery.select(claimItemRoot);

		Join<ClaimItemMaster, Company> claimItemCompanyJoin = claimItemRoot.join(ClaimItemMaster_.company);

		Join<ClaimItemMaster, ClaimCategoryMaster> claimItemCategoryJoin = claimItemRoot
				.join(ClaimItemMaster_.claimCategoryMaster);

		Predicate restriction = cb.equal(claimItemCompanyJoin.get(Company_.companyId), companyId);

		restriction = cb.and(restriction,
				cb.equal(claimItemCategoryJoin.get(ClaimCategoryMaster_.claimCategoryId), categoryId));

		restriction = cb.and(restriction,
				cb.equal(cb.upper(claimItemRoot.get(ClaimItemMaster_.code)), itemCode.toUpperCase()));

		if (claimItemId != null) {
			restriction = cb.and(restriction,
					cb.notEqual(claimItemRoot.get(ClaimItemMaster_.claimItemId), claimItemId));
		}

		criteriaQuery.where(restriction);

		TypedQuery<ClaimItemMaster> claimItemTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<ClaimItemMaster> claimItemList = claimItemTypedQuery.getResultList();
		if (claimItemList != null && !claimItemList.isEmpty()) {
			return claimItemList.get(0);
		}
		return null;
	}

	@Override
	public List<ClaimItemMaster> findByCategoryId(Long categoryId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ClaimItemMaster> criteriaQuery = cb.createQuery(ClaimItemMaster.class);

		Root<ClaimItemMaster> claimItemRoot = criteriaQuery.from(ClaimItemMaster.class);

		criteriaQuery.select(claimItemRoot);

		Join<ClaimItemMaster, ClaimCategoryMaster> claimItemCategoryJoin = claimItemRoot
				.join(ClaimItemMaster_.claimCategoryMaster);

		Predicate restriction = cb.equal(claimItemCategoryJoin.get(ClaimCategoryMaster_.claimCategoryId), categoryId);

		criteriaQuery.where(restriction);

		TypedQuery<ClaimItemMaster> claimItemTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		return claimItemTypedQuery.getResultList();
	}

	@Override
	public List<ClaimItemMaster> findByCompany(Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ClaimItemMaster> criteriaQuery = cb.createQuery(ClaimItemMaster.class);

		Root<ClaimItemMaster> claimItemRoot = criteriaQuery.from(ClaimItemMaster.class);

		criteriaQuery.select(claimItemRoot);

		Predicate restriction = cb.equal(claimItemRoot.get(ClaimItemMaster_.company).get("companyId").as(Long.class),
				companyId);

		criteriaQuery.where(restriction);

		TypedQuery<ClaimItemMaster> claimItemTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		return claimItemTypedQuery.getResultList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.EmployeeDAO#getMaxEmployeeId()
	 */
	@Override
	public Integer getMaxClaimTypeSortOrder(Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Integer> criteriaQuery = cb.createQuery(Integer.class);
		Root<ClaimItemMaster> claimItemRoot = criteriaQuery.from(ClaimItemMaster.class);
		Join<ClaimItemMaster, Company> leaveTypeMasterCompanyJoin = claimItemRoot.join(ClaimItemMaster_.company);

		criteriaQuery.select(cb.max(claimItemRoot.get(ClaimItemMaster_.sortOrder)));

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(leaveTypeMasterCompanyJoin.get(Company_.companyId), companyId));

		criteriaQuery.where(restriction);
		TypedQuery<Integer> maxClaimTypeSortOrderQuery = entityManagerFactory.createQuery(criteriaQuery);

		Integer maxSortOrder = maxClaimTypeSortOrderQuery.getSingleResult();
		if (maxSortOrder == null) {
			maxSortOrder = 0;
		}
		return maxSortOrder;

	}

	@Override
	public ClaimItemMaster findByClaimItemMasterId(Long claimItemId, Long companyId) {
		
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ClaimItemMaster> criteriaQuery = cb.createQuery(ClaimItemMaster.class);

		Root<ClaimItemMaster> claimItemRoot = criteriaQuery.from(ClaimItemMaster.class);

		criteriaQuery.select(claimItemRoot);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,cb.equal(claimItemRoot.get(ClaimItemMaster_.claimItemId), claimItemId));
		restriction = cb.and(restriction,cb.equal(claimItemRoot.get(ClaimItemMaster_.company).get("companyId").as(Long.class), companyId));

		criteriaQuery.where(restriction);

		TypedQuery<ClaimItemMaster> claimItemTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		return (claimItemTypedQuery.getResultList() != null && !claimItemTypedQuery.getResultList().isEmpty()) ? claimItemTypedQuery.getResultList().get(0) : null;
	}

}
