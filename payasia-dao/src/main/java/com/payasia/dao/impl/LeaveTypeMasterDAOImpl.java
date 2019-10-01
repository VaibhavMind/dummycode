package com.payasia.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.payasia.common.dto.LeaveTypeMasterConditionDTO;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.SortConstants;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.LeaveTypeMasterDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.CompanyGroup;
import com.payasia.dao.bean.CompanyGroup_;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.LeaveScheme;
import com.payasia.dao.bean.LeaveSchemeType;
import com.payasia.dao.bean.LeaveSchemeType_;
import com.payasia.dao.bean.LeaveScheme_;
import com.payasia.dao.bean.LeaveTypeMaster;
import com.payasia.dao.bean.LeaveTypeMaster_;

@Repository
public class LeaveTypeMasterDAOImpl extends BaseDAO implements
		LeaveTypeMasterDAO {
	@Override
	public List<LeaveTypeMaster> findAll(long companyId, PageRequest pageDTO,
			SortCondition sortDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveTypeMaster> criteriaQuery = cb
				.createQuery(LeaveTypeMaster.class);
		Root<LeaveTypeMaster> leaveTypeRoot = criteriaQuery
				.from(LeaveTypeMaster.class);

		criteriaQuery.select(leaveTypeRoot);

		Join<LeaveTypeMaster, Company> leaveTypeMasterCompanyJoin = leaveTypeRoot
				.join(LeaveTypeMaster_.company);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(
				leaveTypeMasterCompanyJoin.get(Company_.companyId), companyId));

		criteriaQuery.where(restriction);

		if (sortDTO != null) {
			Path<String> sortPath = getSortPathForLeaveType(sortDTO,
					leaveTypeRoot);
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

		TypedQuery<LeaveTypeMaster> leaveTypeDefinitionTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			leaveTypeDefinitionTypedQuery
					.setFirstResult(getStartPosition(pageDTO));
			leaveTypeDefinitionTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		return leaveTypeDefinitionTypedQuery.getResultList();
	}

	@Override
	public List<LeaveTypeMaster> findByCompanyAndVisibility(long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveTypeMaster> criteriaQuery = cb
				.createQuery(LeaveTypeMaster.class);
		Root<LeaveTypeMaster> leaveTypeRoot = criteriaQuery
				.from(LeaveTypeMaster.class);

		criteriaQuery.select(leaveTypeRoot);

		Join<LeaveTypeMaster, Company> leaveTypeMasterCompanyJoin = leaveTypeRoot
				.join(LeaveTypeMaster_.company);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(
				leaveTypeMasterCompanyJoin.get(Company_.companyId), companyId));
		restriction = cb.and(restriction,
				cb.equal(leaveTypeRoot.get(LeaveTypeMaster_.visibility), true));

		criteriaQuery.where(restriction);
		criteriaQuery.orderBy(cb.asc(leaveTypeRoot
				.get(LeaveTypeMaster_.sortOrder)));

		TypedQuery<LeaveTypeMaster> leaveTypeDefinitionTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return leaveTypeDefinitionTypedQuery.getResultList();
	}

	@Override
	public List<LeaveTypeMaster> findByConditionAndVisibility(
			Long leaveSchemeId, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveTypeMaster> criteriaQuery = cb
				.createQuery(LeaveTypeMaster.class);
		Root<LeaveTypeMaster> leaveTypeRoot = criteriaQuery
				.from(LeaveTypeMaster.class);

		criteriaQuery.select(leaveTypeRoot).distinct(true);

		Join<LeaveTypeMaster, LeaveSchemeType> leaveSchemeTypeJoin = leaveTypeRoot
				.join(LeaveTypeMaster_.leaveSchemeTypes);

		Join<LeaveTypeMaster, Company> companyJoin = leaveTypeRoot
				.join(LeaveTypeMaster_.company);

		Join<LeaveSchemeType, LeaveScheme> leaveSchemeJoin = leaveSchemeTypeJoin
				.join(LeaveSchemeType_.leaveScheme);

		Predicate restriction = cb.conjunction();
		if (leaveSchemeId != null) {
			restriction = cb.and(restriction, cb.equal(
					leaveSchemeJoin.get(LeaveScheme_.leaveSchemeId),
					leaveSchemeId));
		}

		restriction = cb.and(restriction,
				cb.equal(leaveTypeRoot.get(LeaveTypeMaster_.visibility), true));

		restriction = cb.and(restriction,
				cb.equal(companyJoin.get(Company_.companyId), companyId));
		criteriaQuery.where(restriction);

		criteriaQuery.orderBy(cb.asc(leaveTypeRoot
				.get(LeaveTypeMaster_.sortOrder)));

		TypedQuery<LeaveTypeMaster> leaveTypeDefinitionTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<LeaveTypeMaster> leaveTypeList = leaveTypeDefinitionTypedQuery
				.getResultList();

		return leaveTypeList;
	}

	@Override
	public List<LeaveTypeMaster> findByCondition(
			LeaveTypeMasterConditionDTO conditionDTO, PageRequest pageDTO,
			SortCondition sortDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveTypeMaster> criteriaQuery = cb
				.createQuery(LeaveTypeMaster.class);
		Root<LeaveTypeMaster> leaveTypeRoot = criteriaQuery
				.from(LeaveTypeMaster.class);

		criteriaQuery.select(leaveTypeRoot);

		Join<LeaveTypeMaster, Company> leaveTypeMasterCompanyJoin = leaveTypeRoot
				.join(LeaveTypeMaster_.company);

		Predicate restriction = cb.conjunction();

		if (StringUtils.isNotBlank(conditionDTO.getName())) {

			restriction = cb.and(restriction,
					cb.like(cb.upper(leaveTypeRoot
							.get(LeaveTypeMaster_.leaveTypeName)), conditionDTO
							.getName().toUpperCase()));
		}

		if (StringUtils.isNotBlank(conditionDTO.getCode())) {

			restriction = cb.and(restriction, cb.like(
					cb.upper(leaveTypeRoot.get(LeaveTypeMaster_.code)),
					conditionDTO.getCode().toUpperCase()));
		}

		if (StringUtils.isNotBlank(conditionDTO.getAccountCode())) {
			restriction = cb.and(restriction, cb.like(
					cb.upper(leaveTypeRoot.get(LeaveTypeMaster_.accountCode)),
					conditionDTO.getAccountCode().toUpperCase()));
		}

		if (StringUtils.isNotBlank(conditionDTO.getVisibleOrHidden())) {
			if (conditionDTO.getVisibleOrHidden() == "visible") {
				restriction = cb.and(restriction, cb.equal(
						leaveTypeRoot.get(LeaveTypeMaster_.visibility), true));
			} else if (conditionDTO.getVisibleOrHidden() == "hidden") {
				restriction = cb.and(restriction, cb.equal(
						leaveTypeRoot.get(LeaveTypeMaster_.visibility), false));
			}
		}

		restriction = cb.and(restriction, cb.equal(
				leaveTypeMasterCompanyJoin.get(Company_.companyId),
				conditionDTO.getCompanyId()));

		criteriaQuery.where(restriction);

		if (sortDTO != null) {
			Path<String> sortPath = getSortPathForLeaveType(sortDTO,
					leaveTypeRoot);
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

		criteriaQuery.orderBy(cb.asc(leaveTypeRoot
				.get(LeaveTypeMaster_.sortOrder)));

		TypedQuery<LeaveTypeMaster> leaveTypeDefinitionTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			leaveTypeDefinitionTypedQuery
					.setFirstResult(getStartPosition(pageDTO));
			leaveTypeDefinitionTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		return leaveTypeDefinitionTypedQuery.getResultList();

	}

	@Override
	public LeaveTypeMaster findByLeaveIdAndName(Long leaveTypeId,
			String leaveName, Long companyId, String leaveCode) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveTypeMaster> criteriaQuery = cb
				.createQuery(LeaveTypeMaster.class);

		Root<LeaveTypeMaster> leaveTypeRoot = criteriaQuery
				.from(LeaveTypeMaster.class);

		criteriaQuery.select(leaveTypeRoot);

		Join<LeaveTypeMaster, Company> leaveTypeCompanyJoin = leaveTypeRoot
				.join(LeaveTypeMaster_.company);

		Predicate restriction = cb.equal(
				leaveTypeCompanyJoin.get(Company_.companyId), companyId);

		if (leaveName != null) {
			restriction = cb.and(restriction,
					cb.equal(cb.upper(leaveTypeRoot
							.get(LeaveTypeMaster_.leaveTypeName)), leaveName
							.toUpperCase()));
		}

		if (leaveCode != null) {
			restriction = cb.and(restriction, cb.equal(
					cb.upper(leaveTypeRoot.get(LeaveTypeMaster_.code)),
					leaveCode.toUpperCase()));
		}

		if (leaveTypeId != null) {
			restriction = cb.and(restriction, cb.notEqual(
					leaveTypeRoot.get(LeaveTypeMaster_.leaveTypeId),
					leaveTypeId));
		}

		criteriaQuery.where(restriction);

		TypedQuery<LeaveTypeMaster> leaveTypeTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<LeaveTypeMaster> leaveTypeList = leaveTypeTypedQuery
				.getResultList();
		if (leaveTypeList != null &&  !leaveTypeList.isEmpty()) {
			return leaveTypeList.get(0);
		}
		return null;
	}

	@Override
	public Path<String> getSortPathForLeaveType(SortCondition sortDTO,
			Root<LeaveTypeMaster> leaveTypeRoot) {
		List<String> leaveBatchIdList = new ArrayList<String>();
		leaveBatchIdList.add(SortConstants.LEAVE_BATCH_ID);

		List<String> leaveTypeIdList = new ArrayList<String>();
		leaveTypeIdList.add(SortConstants.LEAVE_TYPE_ID);

		List<String> leaveTypeColList = new ArrayList<String>();
		leaveTypeColList.add(SortConstants.LEAVE_TYPE_NAME);
		leaveTypeColList.add(SortConstants.LEAVE_TYPE_CODE);
		leaveTypeColList.add(SortConstants.LEAVE_TYPE_ACCOUNT_CODE);
		leaveTypeColList.add(SortConstants.LEAVE_TYPE_DESCRIPTION);
		leaveTypeColList.add(SortConstants.LEAVE_TYPE_VISIBLE_OR_HIDDEN);

		Path<String> sortPath = null;
		if (leaveTypeColList.contains(sortDTO.getColumnName())) {
			sortPath = leaveTypeRoot.get(colMap.get(LeaveTypeMaster.class
					+ sortDTO.getColumnName()));
		}

		return sortPath;
	}

	@Override
	public Long getCountByCondition(LeaveTypeMasterConditionDTO conditionDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
		Root<LeaveTypeMaster> leaveTypeRoot = criteriaQuery
				.from(LeaveTypeMaster.class);

		criteriaQuery.select(cb.count(leaveTypeRoot));

		Join<LeaveTypeMaster, Company> leaveTypeMasterCompanyJoin = leaveTypeRoot
				.join(LeaveTypeMaster_.company);

		Predicate restriction = cb.conjunction();

		if (StringUtils.isNotBlank(conditionDTO.getName())) {

			restriction = cb.and(restriction,
					cb.like(cb.upper(leaveTypeRoot
							.get(LeaveTypeMaster_.leaveTypeName)), conditionDTO
							.getName().toUpperCase()));
		}

		if (StringUtils.isNotBlank(conditionDTO.getCode())) {

			restriction = cb.and(restriction, cb.like(
					cb.upper(leaveTypeRoot.get(LeaveTypeMaster_.code)),
					conditionDTO.getCode().toUpperCase()));
		}

		if (StringUtils.isNotBlank(conditionDTO.getAccountCode())) {
			restriction = cb.and(restriction, cb.like(
					cb.upper(leaveTypeRoot.get(LeaveTypeMaster_.accountCode)),
					conditionDTO.getAccountCode().toUpperCase()));
		}

		if (StringUtils.isNotBlank(conditionDTO.getVisibleOrHidden())) {
			if (conditionDTO.getVisibleOrHidden() == "visible") {
				restriction = cb.and(restriction, cb.equal(
						leaveTypeRoot.get(LeaveTypeMaster_.visibility), true));
			} else if (conditionDTO.getVisibleOrHidden() == "hidden") {
				restriction = cb.and(restriction, cb.equal(
						leaveTypeRoot.get(LeaveTypeMaster_.visibility), false));
			}
		}

		restriction = cb.and(restriction, cb.equal(
				leaveTypeMasterCompanyJoin.get(Company_.companyId),
				conditionDTO.getCompanyId()));

		criteriaQuery.where(restriction);

		TypedQuery<Long> leaveTypeDefinitionTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return leaveTypeDefinitionTypedQuery.getSingleResult();
	}

	@Override
	public LeaveTypeMaster findById(Long leaveTypeId) {
		LeaveTypeMaster leaveTypeMaster = super.findById(LeaveTypeMaster.class,
				leaveTypeId);
		return leaveTypeMaster;
	}

	@Override
	public void update(LeaveTypeMaster leaveTypeMaster) {
		super.update(leaveTypeMaster);
	}

	@Override
	public void save(LeaveTypeMaster leaveTypeMaster) {
		super.save(leaveTypeMaster);

	}

	@Override
	public void delete(LeaveTypeMaster leaveTypeMaster) {
		super.delete(leaveTypeMaster);
	}

	@Override
	protected Object getBaseEntity() {
		LeaveTypeMaster leaveTypeMaster = new LeaveTypeMaster();
		return leaveTypeMaster;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.EmployeeDAO#getMaxEmployeeId()
	 */
	@Override
	public Integer getMaxLeaveTypeSortOrder(Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Integer> criteriaQuery = cb.createQuery(Integer.class);
		Root<LeaveTypeMaster> leaveTypeRoot = criteriaQuery
				.from(LeaveTypeMaster.class);
		Join<LeaveTypeMaster, Company> leaveTypeMasterCompanyJoin = leaveTypeRoot
				.join(LeaveTypeMaster_.company);

		criteriaQuery.select(cb.max(leaveTypeRoot
				.get(LeaveTypeMaster_.sortOrder)));

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(
				leaveTypeMasterCompanyJoin.get(Company_.companyId), companyId));

		criteriaQuery.where(restriction);
		TypedQuery<Integer> maxLeaveTypeSortOrderQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		Integer maxSortOrder = maxLeaveTypeSortOrderQuery.getSingleResult();
		if (maxSortOrder == null) {
			maxSortOrder = 0;
		}
		return maxSortOrder;

	}

	@Override
	public List<LeaveTypeMaster> getAllLeaveTypes(Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveTypeMaster> criteriaQuery = cb
				.createQuery(LeaveTypeMaster.class);
		Root<LeaveTypeMaster> leaveTypeMasterRoot = criteriaQuery
				.from(LeaveTypeMaster.class);
		criteriaQuery.select(leaveTypeMasterRoot);
		Predicate restriction = cb.conjunction();

		if (companyId != null) {
			Join<LeaveTypeMaster, Company> leaveTypeRootCompanyJoin = leaveTypeMasterRoot
					.join(LeaveTypeMaster_.company);

			restriction = cb.and(restriction,
					cb.equal(leaveTypeRootCompanyJoin.get(Company_.companyId),
							companyId));
		}

		criteriaQuery.where(restriction);

		TypedQuery<LeaveTypeMaster> leaveTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<LeaveTypeMaster> leaveSchemeList = leaveTypedQuery.getResultList();

		return leaveSchemeList;
	}

	@Override
	public List<Tuple> getLeaveTypeNameTupleList(Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Tuple> criteriaQuery = cb.createQuery(Tuple.class);
		Root<LeaveTypeMaster> leaveTypeRoot = criteriaQuery
				.from(LeaveTypeMaster.class);

		Join<LeaveTypeMaster, Company> compJoin = leaveTypeRoot
				.join(LeaveTypeMaster_.company);

		List<Selection<?>> selectionItems = new ArrayList<Selection<?>>();

		selectionItems.add(leaveTypeRoot.get(LeaveTypeMaster_.leaveTypeName)
				.alias(getAlias(LeaveTypeMaster_.leaveTypeName)));
		criteriaQuery.multiselect(selectionItems).distinct(true);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction,
				cb.equal(compJoin.get(Company_.companyId), companyId));

		criteriaQuery.where(restriction);

		TypedQuery<Tuple> companyTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<Tuple> companyGroupList = companyTypedQuery.getResultList();
		return companyGroupList;
	}

	@Override
	public List<LeaveTypeMaster> getAllLeaveTypesBasedOnGroupId(Long groupId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveTypeMaster> criteriaQuery = cb
				.createQuery(LeaveTypeMaster.class);
		Root<LeaveTypeMaster> leaveTypeMasterRoot = criteriaQuery
				.from(LeaveTypeMaster.class);
		criteriaQuery.select(leaveTypeMasterRoot);
		Predicate restriction = cb.conjunction();

		if (groupId != null) {
			Join<LeaveTypeMaster, Company> leaveTypeRootCompanyJoin = leaveTypeMasterRoot
					.join(LeaveTypeMaster_.company);

			Join<Company, CompanyGroup> companyGroupJoin = leaveTypeRootCompanyJoin
					.join(Company_.companyGroup);

			restriction = cb.and(restriction, cb.equal(
					companyGroupJoin.get(CompanyGroup_.groupId), groupId));
		}

		criteriaQuery.where(restriction);

		TypedQuery<LeaveTypeMaster> leaveTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<LeaveTypeMaster> leaveTypesList = leaveTypedQuery.getResultList();

		return leaveTypesList;
	}

	@Override
	public LeaveTypeMaster findLeaveTypeByCompId(Long leaveTypeId, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveTypeMaster> criteriaQuery = cb
				.createQuery(LeaveTypeMaster.class);
		Root<LeaveTypeMaster> leaveTypeRoot = criteriaQuery
				.from(LeaveTypeMaster.class);

		criteriaQuery.select(leaveTypeRoot).distinct(true);

		Join<LeaveTypeMaster, Company> companyJoin = leaveTypeRoot
				.join(LeaveTypeMaster_.company);
		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction,
				cb.equal(leaveTypeRoot.get(LeaveTypeMaster_.leaveTypeId), leaveTypeId));
		restriction = cb.and(restriction,
				cb.equal(companyJoin.get(Company_.companyId), companyId));
		criteriaQuery.where(restriction);
		TypedQuery<LeaveTypeMaster> leaveTypeDefinitionTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<LeaveTypeMaster> leaveTypeList = leaveTypeDefinitionTypedQuery
				.getResultList();
		if (leaveTypeList != null && !leaveTypeList.isEmpty()) {
			return leaveTypeList.get(0);
		}
		return null;
	}

}
