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
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.SortConstants;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.ClaimBatchMasterDAO;
import com.payasia.dao.bean.ClaimBatchMaster;
import com.payasia.dao.bean.ClaimBatchMaster_;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Company_;

@Repository
public class ClaimBatchMasterDAOImpl extends BaseDAO implements ClaimBatchMasterDAO {

	public List<ClaimBatchMaster> getClaimBatchByCondition(PageRequest pageDTO, SortCondition sortDTO,
			LeaveBatchConditionDTO leaveBatchDTO, Long companyID, int year) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ClaimBatchMaster> criteriaQuery = cb.createQuery(ClaimBatchMaster.class);

		Root<ClaimBatchMaster> claimBatchRoot = criteriaQuery.from(ClaimBatchMaster.class);

		criteriaQuery.select(claimBatchRoot);

		Join<ClaimBatchMaster, Company> claimBatchCompanyJoin = claimBatchRoot.join(ClaimBatchMaster_.company);

		Path<Long> companyId = claimBatchCompanyJoin.get(Company_.companyId);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(companyId, companyID));

		if (StringUtils.isNotBlank(leaveBatchDTO.getDescription())) {

			restriction = cb.and(restriction, cb.like(cb.upper(claimBatchRoot.get(ClaimBatchMaster_.claimBatchDesc)),
					leaveBatchDTO.getDescription().toUpperCase()));
		}

		if (leaveBatchDTO.getStartDate() != null) {

			restriction = cb.and(restriction, cb.greaterThanOrEqualTo(claimBatchRoot.get(ClaimBatchMaster_.startDate),
					leaveBatchDTO.getStartDate()));
		}

		if (leaveBatchDTO.getEndDate() != null) {

			restriction = cb.and(restriction,
					cb.lessThanOrEqualTo(claimBatchRoot.get(ClaimBatchMaster_.endDate), leaveBatchDTO.getEndDate()));
		}

		restriction = cb.and(restriction, cb.lessThanOrEqualTo(
				cb.function("year", Integer.class, claimBatchRoot.get(ClaimBatchMaster_.startDate)), year));
		restriction = cb.and(restriction, cb.greaterThanOrEqualTo(
				cb.function("year", Integer.class, claimBatchRoot.get(ClaimBatchMaster_.endDate)), year));

		criteriaQuery.where(restriction);

		if (sortDTO != null) {

			Path<String> sortPath = getSortPathForclaimBatch(sortDTO, claimBatchRoot);
			if (sortPath != null) {
				if (SortConstants.DB_ORDER_BY_ASC.equalsIgnoreCase(sortDTO.getOrderType())) {
					criteriaQuery.orderBy(cb.asc(sortPath));
				}
				if (SortConstants.DB_ORDER_BY_DESC.equalsIgnoreCase(sortDTO.getOrderType())) {
					criteriaQuery.orderBy(cb.desc(sortPath));
				}
			}

		}

		TypedQuery<ClaimBatchMaster> claimBatchTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			claimBatchTypedQuery.setFirstResult(getStartPosition(pageDTO));
			claimBatchTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		List<ClaimBatchMaster> conditionClaimBatchList = claimBatchTypedQuery.getResultList();

		return conditionClaimBatchList;

	}

	@Override
	public Integer getClaimBatchCountByCondition(LeaveBatchConditionDTO leaveBatchDTO, Long companyID, int year) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Integer> criteriaQuery = cb.createQuery(Integer.class);

		Root<ClaimBatchMaster> claimBatchRoot = criteriaQuery.from(ClaimBatchMaster.class);

		criteriaQuery.select(cb.count(claimBatchRoot).as(Integer.class));

		Join<ClaimBatchMaster, Company> claimBatchCompanyJoin = claimBatchRoot.join(ClaimBatchMaster_.company);

		Path<Long> companyId = claimBatchCompanyJoin.get(Company_.companyId);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(companyId, companyID));

		if (StringUtils.isNotBlank(leaveBatchDTO.getDescription())) {

			restriction = cb.and(restriction, cb.like(cb.upper(claimBatchRoot.get(ClaimBatchMaster_.claimBatchDesc)),
					leaveBatchDTO.getDescription().toUpperCase()));
		}

		restriction = cb.and(restriction, cb.lessThanOrEqualTo(
				cb.function("year", Integer.class, claimBatchRoot.get(ClaimBatchMaster_.startDate)), year));
		restriction = cb.and(restriction, cb.greaterThanOrEqualTo(
				cb.function("year", Integer.class, claimBatchRoot.get(ClaimBatchMaster_.endDate)), year));

		criteriaQuery.where(restriction);

		TypedQuery<Integer> claimBatchTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		return claimBatchTypedQuery.getSingleResult();

	}

	@Override
	public Path<String> getSortPathForclaimBatch(SortCondition sortDTO, Root<ClaimBatchMaster> claimBatchRoot) {

		List<String> claimBatchIdList = new ArrayList<String>();
		claimBatchIdList.add(SortConstants.CLAIM_BATCH_ID);

		List<String> claimBatchColList = new ArrayList<String>();
		claimBatchColList.add(SortConstants.CLAIM_BATCH_DESCRIPTION);
		claimBatchColList.add(SortConstants.CLAIM_BATCH_START_DATE);
		claimBatchColList.add(SortConstants.CLAIM_BATCH_END_DATE);

		Path<String> sortPath = null;

		if (claimBatchColList.contains(sortDTO.getColumnName())) {
			sortPath = claimBatchRoot.get(colMap.get(ClaimBatchMaster.class + sortDTO.getColumnName()));
		}
		return sortPath;
	}

	@Override
	protected Object getBaseEntity() {
		ClaimBatchMaster claimBatchMaster = new ClaimBatchMaster();
		return claimBatchMaster;
	}

	@Override
	public void save(ClaimBatchMaster claimBatchMaster) {
		super.save(claimBatchMaster);
	}

	@Override
	public ClaimBatchMaster findByID(Long claimBatchId) {
		return super.findById(ClaimBatchMaster.class, claimBatchId);
	}

	@Override
	public void update(ClaimBatchMaster claimBatchMaster) {
		super.update(claimBatchMaster);
	}

	@Override
	public void delete(ClaimBatchMaster claimBatchMaster) {
		super.delete(claimBatchMaster);
	}

	@Override
	public ClaimBatchMaster findByDescCompany(Long claimBatchId, String claimDesc, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ClaimBatchMaster> criteriaQuery = cb.createQuery(ClaimBatchMaster.class);

		Root<ClaimBatchMaster> claimBatchRoot = criteriaQuery.from(ClaimBatchMaster.class);

		criteriaQuery.select(claimBatchRoot);

		Join<ClaimBatchMaster, Company> claimBatchCompanyJoin = claimBatchRoot.join(ClaimBatchMaster_.company);

		Predicate restriction = cb.equal(claimBatchCompanyJoin.get(Company_.companyId), companyId);

		restriction = cb.and(restriction,
				cb.equal(cb.upper(claimBatchRoot.get(ClaimBatchMaster_.claimBatchDesc)), claimDesc.toUpperCase()));

		if (claimBatchId != null) {
			restriction = cb.and(restriction,
					cb.notEqual(claimBatchRoot.get(ClaimBatchMaster_.claimBatchID), claimBatchId));
		}

		criteriaQuery.where(restriction);

		TypedQuery<ClaimBatchMaster> claimBatchTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<ClaimBatchMaster> claimBatchList = claimBatchTypedQuery.getResultList();
		if (claimBatchList != null && !claimBatchList.isEmpty()) {
			return claimBatchList.get(0);
		}
		return null;
	}

	@Override
	public ClaimBatchMaster checkClaimBatchByDate(Long claimBatchId, Long companyId, String date, String dateFormat) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ClaimBatchMaster> criteriaQuery = cb.createQuery(ClaimBatchMaster.class);
		Root<ClaimBatchMaster> claimRoot = criteriaQuery.from(ClaimBatchMaster.class);
		criteriaQuery.select(claimRoot);

		Join<ClaimBatchMaster, Company> companyJoin = claimRoot.join(ClaimBatchMaster_.company);

		Predicate restriction = cb.conjunction();
		if (claimBatchId != null) {
			restriction = cb.and(restriction, cb.notEqual(claimRoot.get(ClaimBatchMaster_.claimBatchID), claimBatchId));
		}

		restriction = cb.and(restriction, cb.equal(companyJoin.get(Company_.companyId), companyId));
		restriction = cb.and(restriction,

				restriction = cb.and(
						cb.and(restriction,
								cb.lessThanOrEqualTo(claimRoot.get(ClaimBatchMaster_.startDate),
										DateUtils.stringToTimestamp(date, dateFormat))),
						cb.and(restriction, cb.greaterThanOrEqualTo(claimRoot.get(ClaimBatchMaster_.endDate),
								DateUtils.stringToTimestamp(date, dateFormat)))));
		criteriaQuery.where(restriction);

		TypedQuery<ClaimBatchMaster> empTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<ClaimBatchMaster> claimBatchList = empTypedQuery.getResultList();
		if (claimBatchList != null && !claimBatchList.isEmpty()) {
			return claimBatchList.get(0);
		}
		return null;

	}

	@Override
	public List<ClaimBatchMaster> getClaimBatchByCompany(Long companyID) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ClaimBatchMaster> criteriaQuery = cb.createQuery(ClaimBatchMaster.class);

		Root<ClaimBatchMaster> claimBatchRoot = criteriaQuery.from(ClaimBatchMaster.class);

		criteriaQuery.select(claimBatchRoot);

		Join<ClaimBatchMaster, Company> claimBatchCompanyJoin = claimBatchRoot.join(ClaimBatchMaster_.company);

		Path<Long> companyId = claimBatchCompanyJoin.get(Company_.companyId);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(companyId, companyID));

		criteriaQuery.where(restriction);

		TypedQuery<ClaimBatchMaster> claimBatchTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<ClaimBatchMaster> conditionClaimBatchList = claimBatchTypedQuery.getResultList();

		return conditionClaimBatchList;

	}

	@Override
	public List<ClaimBatchMaster> getClaimBatchMastersByDateRange(Long companyId, String startDate, String endDate,
			String dateFormat, String paidStatus) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ClaimBatchMaster> criteriaQuery = cb.createQuery(ClaimBatchMaster.class);

		Root<ClaimBatchMaster> claimBatchRoot = criteriaQuery.from(ClaimBatchMaster.class);

		criteriaQuery.select(claimBatchRoot);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction,
				cb.equal(claimBatchRoot.get(ClaimBatchMaster_.company).get(Company_.companyId), companyId));

		if (startDate != null && endDate != null && dateFormat != null) {

			restriction = cb.or(
					cb.and(cb.and(restriction,
							cb.lessThanOrEqualTo(claimBatchRoot.get(ClaimBatchMaster_.endDate),
									DateUtils.stringToTimestamp(endDate, dateFormat))),
							cb.and(restriction,
									cb.greaterThanOrEqualTo(claimBatchRoot.get(ClaimBatchMaster_.endDate),
											DateUtils.stringToTimestamp(startDate, dateFormat)))),
					cb.and(cb.and(restriction,
							cb.lessThanOrEqualTo(claimBatchRoot.get(ClaimBatchMaster_.startDate),
									DateUtils.stringToTimestamp(endDate, dateFormat))),
							cb.and(restriction, cb.greaterThanOrEqualTo(claimBatchRoot.get(ClaimBatchMaster_.startDate),
									DateUtils.stringToTimestamp(startDate, dateFormat)))));

		}

		criteriaQuery.where(restriction);

		TypedQuery<ClaimBatchMaster> claimBatchTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<ClaimBatchMaster> list = claimBatchTypedQuery.getResultList();

		return list;

	}

	@Override
	public ClaimBatchMaster findByClaimBatchMasterId(Long claimBatchId,Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ClaimBatchMaster> criteriaQuery = cb.createQuery(ClaimBatchMaster.class);
		Root<ClaimBatchMaster> claimRoot = criteriaQuery.from(ClaimBatchMaster.class);
		criteriaQuery.select(claimRoot);

		Join<ClaimBatchMaster, Company> companyJoin = claimRoot.join(ClaimBatchMaster_.company);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(claimRoot.get(ClaimBatchMaster_.claimBatchID), claimBatchId));
		restriction = cb.and(restriction, cb.equal(companyJoin.get(Company_.companyId), companyId));
	
		criteriaQuery.where(restriction);

		TypedQuery<ClaimBatchMaster> empTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<ClaimBatchMaster> claimBatchList = empTypedQuery.getResultList();
		if (claimBatchList != null && !claimBatchList.isEmpty()) {
			return claimBatchList.get(0);
		}
		return null;


	}

}
