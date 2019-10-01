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

import com.payasia.common.dto.LeaveBatchConditionDTO;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.SortConstants;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.LeaveBatchMasterDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.LeaveBatchMaster;
import com.payasia.dao.bean.LeaveBatchMaster_;

@Repository
public class LeaveBatchMasterDAOImpl extends BaseDAO implements
		LeaveBatchMasterDAO {

	@Override
	public List<LeaveBatchMaster> getLeaveBatchAll(PageRequest pageDTO,
			SortCondition sortDTO, Long companyID) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveBatchMaster> criteriaQuery = cb
				.createQuery(LeaveBatchMaster.class);
		Root<LeaveBatchMaster> leaveBatchMasterRoot = criteriaQuery
				.from(LeaveBatchMaster.class);

		criteriaQuery.select(leaveBatchMasterRoot);

		Join<LeaveBatchMaster, Company> leaveBatchMasterCompanyJoin = leaveBatchMasterRoot
				.join(LeaveBatchMaster_.company);

		Predicate restriction = cb.conjunction();
		restriction = cb
				.and(restriction, cb.equal(
						leaveBatchMasterCompanyJoin.get(Company_.companyId),
						companyID));
		criteriaQuery.where(restriction);

		if (sortDTO != null) {

			Path<String> sortPath = getSortPathForLeaveBatch(sortDTO,
					leaveBatchMasterRoot);
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

		TypedQuery<LeaveBatchMaster> leaveBatchMasterQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			leaveBatchMasterQuery.setFirstResult(getStartPosition(pageDTO));
			leaveBatchMasterQuery.setMaxResults(pageDTO.getPageSize());
		}

		return leaveBatchMasterQuery.getResultList();

	}

	@Override
	public Path<String> getSortPathForLeaveBatch(SortCondition sortDTO,
			Root<LeaveBatchMaster> leaveBatchRoot) {

		List<String> leaveBatchIdList = new ArrayList<String>();
		leaveBatchIdList.add(SortConstants.LEAVE_BATCH_ID);

		List<String> leaveBatchColList = new ArrayList<String>();
		leaveBatchColList.add(SortConstants.LEAVE_BATCH_DESCRIPTION);
		leaveBatchColList.add(SortConstants.LEAVE_BATCH_START_DATE);
		leaveBatchColList.add(SortConstants.LEAVE_BATCH_END_DATE);

		Path<String> sortPath = null;

		if (leaveBatchColList.contains(sortDTO.getColumnName())) {
			sortPath = leaveBatchRoot.get(colMap.get(LeaveBatchMaster.class
					+ sortDTO.getColumnName()));
		}
		return sortPath;
	}

	@Override
	protected Object getBaseEntity() {
		LeaveBatchMaster leaveBatchMaster = new LeaveBatchMaster();
		return leaveBatchMaster;
	}

	@Override
	public void save(LeaveBatchMaster leaveBatchMaster) {
		super.save(leaveBatchMaster);
	}

	public List<LeaveBatchMaster> getLeaveBatchByCondition(PageRequest pageDTO,
			SortCondition sortDTO, LeaveBatchConditionDTO leaveBatchDTO,
			Long companyID) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveBatchMaster> criteriaQuery = cb
				.createQuery(LeaveBatchMaster.class);

		Root<LeaveBatchMaster> leaveBatchRoot = criteriaQuery
				.from(LeaveBatchMaster.class);

		criteriaQuery.select(leaveBatchRoot);

		Join<LeaveBatchMaster, Company> leaveBatchCompanyJoin = leaveBatchRoot
				.join(LeaveBatchMaster_.company);

		Path<Long> companyId = leaveBatchCompanyJoin.get(Company_.companyId);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(companyId, companyID));

		if (StringUtils.isNotBlank(leaveBatchDTO.getDescription())) {

			restriction = cb.and(restriction, cb.like(cb.upper(leaveBatchRoot
					.get(LeaveBatchMaster_.leaveBatchDesc)), leaveBatchDTO
					.getDescription().toUpperCase()));
		}

		if (leaveBatchDTO.getStartDate() != null) {

			restriction = cb.and(restriction, cb.equal(
					leaveBatchRoot.get(LeaveBatchMaster_.startDate),
					leaveBatchDTO.getStartDate()));
		}
		if (leaveBatchDTO.getEndDate() != null) {

			restriction = cb.and(restriction, cb.equal(
					leaveBatchRoot.get(LeaveBatchMaster_.endDate),
					leaveBatchDTO.getEndDate()));
		}

		criteriaQuery.where(restriction);

		if (sortDTO != null) {

			Path<String> sortPath = getSortPathForLeaveBatch(sortDTO,
					leaveBatchRoot);
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

		TypedQuery<LeaveBatchMaster> leaveBatchTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			leaveBatchTypedQuery.setFirstResult(getStartPosition(pageDTO));
			leaveBatchTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		List<LeaveBatchMaster> conditionLeaveBatchList = leaveBatchTypedQuery
				.getResultList();

		return conditionLeaveBatchList;
	}

	@Override
	public int countLeaveBatch(LeaveBatchConditionDTO leaveBatchDTO,
			Long companyId) {
		return getLeaveBatchByCondition(null, null, leaveBatchDTO, companyId)
				.size();
	}

	@Override
	public LeaveBatchMaster findByID(Long leaveBatchId) {
		return super.findById(LeaveBatchMaster.class, leaveBatchId);
	}

	@Override
	public void update(LeaveBatchMaster leaveBatchMaster) {
		super.update(leaveBatchMaster);
	}

	@Override
	public void delete(LeaveBatchMaster leaveBatchMaster) {
		super.delete(leaveBatchMaster);

	}

	@Override
	public LeaveBatchMaster findByDescCompany(Long leaveBatchId,
			String leaveDesc, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveBatchMaster> criteriaQuery = cb
				.createQuery(LeaveBatchMaster.class);

		Root<LeaveBatchMaster> leaveBatchRoot = criteriaQuery
				.from(LeaveBatchMaster.class);

		criteriaQuery.select(leaveBatchRoot);

		Join<LeaveBatchMaster, Company> leaveBatchCompanyJoin = leaveBatchRoot
				.join(LeaveBatchMaster_.company);

		Predicate restriction = cb.equal(
				leaveBatchCompanyJoin.get(Company_.companyId), companyId);

		restriction = cb.and(restriction, cb.equal(
				cb.upper(leaveBatchRoot.get(LeaveBatchMaster_.leaveBatchDesc)),
				leaveDesc.toUpperCase()));

		if (leaveBatchId != null) {
			restriction = cb.and(restriction, cb.notEqual(
					leaveBatchRoot.get(LeaveBatchMaster_.leaveBatchId),
					leaveBatchId));
		}

		criteriaQuery.where(restriction);

		TypedQuery<LeaveBatchMaster> leaveBatchTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		List<LeaveBatchMaster> leaveBatchList = leaveBatchTypedQuery
				.getResultList();
		if (leaveBatchList != null &&  !leaveBatchList.isEmpty()) {
			return leaveBatchList.get(0);
		}
		return null;
	}

	@Override
	public LeaveBatchMaster findByCompID(Long leaveBatchId, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveBatchMaster> criteriaQuery = cb
				.createQuery(LeaveBatchMaster.class);

		Root<LeaveBatchMaster> leaveBatchRoot = criteriaQuery
				.from(LeaveBatchMaster.class);

		criteriaQuery.select(leaveBatchRoot);

		Join<LeaveBatchMaster, Company> leaveBatchCompanyJoin = leaveBatchRoot
				.join(LeaveBatchMaster_.company);

		Predicate restriction = cb.equal(
				leaveBatchCompanyJoin.get(Company_.companyId), companyId);

		if (leaveBatchId != null) {
			restriction = cb.and(restriction, cb.notEqual(
					leaveBatchRoot.get(LeaveBatchMaster_.leaveBatchId),
					leaveBatchId));
		}

		criteriaQuery.where(restriction);

		TypedQuery<LeaveBatchMaster> leaveBatchTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		List<LeaveBatchMaster> leaveBatchList = leaveBatchTypedQuery
				.getResultList();
		if (leaveBatchList != null &&  !leaveBatchList.isEmpty()) {
			return leaveBatchList.get(0);
		}
		return null;
	}
}
