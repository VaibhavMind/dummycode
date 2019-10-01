package com.payasia.dao.impl;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.payasia.common.dto.LundinConditionDTO;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.DateUtils;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.LundinBlockDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.LundinBlock;
import com.payasia.dao.bean.LundinBlock_;

@Repository
public class LundinBlockDAOImpl extends BaseDAO implements LundinBlockDAO {

	@Override
	public LundinBlock findById(long id) {
		return super.findById(LundinBlock.class, id);
	}

	@Override
	public void save(LundinBlock block) {
		super.save(block);

	}

	@Override
	public void update(LundinBlock block) {
		super.update(block);

	}

	@Override
	public void delete(long id) {
		super.delete(super.findById(LundinBlock.class, id));

	}

	@Override
	protected Object getBaseEntity() {
		LundinBlock obj = new LundinBlock();
		return obj;
	}

	@Override
	public Long getCountForCondition(LundinConditionDTO conditionDTO) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
		Root<LundinBlock> lundinBlockRoot = criteriaQuery
				.from(LundinBlock.class);
		criteriaQuery.select(cb.count(lundinBlockRoot));

		Predicate restriction = cb.conjunction();

		Join<LundinBlock, Company> compJoin = lundinBlockRoot
				.join(LundinBlock_.company);

		restriction = cb.and(
				restriction,
				cb.equal(compJoin.get(Company_.companyId),
						conditionDTO.getCompanyId()));

		if (StringUtils.isNotBlank(conditionDTO.getCreatedDate())) {
			restriction = cb.and(restriction, cb.equal(
					lundinBlockRoot.get(LundinBlock_.createdDate)
							.as(Date.class), DateUtils
							.stringToDate(conditionDTO.getCreatedDate())));

		}

		if (conditionDTO.getFromDate() != null) {
			restriction = cb.and(restriction, cb.greaterThanOrEqualTo(
					(lundinBlockRoot.get(LundinBlock_.effectiveDate)),
					conditionDTO.getFromDate()));
		}
		if (conditionDTO.getToDate() != null) {
			restriction = cb.and(restriction, cb.lessThanOrEqualTo(
					(lundinBlockRoot.get(LundinBlock_.effectiveDate)),
					conditionDTO.getToDate()));
		}
		if (conditionDTO.getBlockCode() != null) {
			restriction = cb.and(restriction, cb.like(
					lundinBlockRoot.get(LundinBlock_.blockCode)
							.as(String.class), '%' + String
							.valueOf(conditionDTO.getBlockCode()) + '%'));

		}
		if (conditionDTO.getBlockName() != null) {
			restriction = cb.and(restriction, cb.like(
					lundinBlockRoot.get(LundinBlock_.blockName)
							.as(String.class), '%' + String
							.valueOf(conditionDTO.getBlockName()) + '%'));
		}
		if (StringUtils.isNotBlank(conditionDTO.getTransactionType())) {
			restriction = cb.and(
					restriction,
					cb.equal(
							lundinBlockRoot.get(LundinBlock_.status).as(
									Boolean.class), conditionDTO.isStatus()));
		}

		criteriaQuery.where(restriction);

		TypedQuery<Long> lundinAppTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return lundinAppTypedQuery.getSingleResult();

	}

	@Override
	public List<LundinBlock> findByCondition(LundinConditionDTO conditionDTO,
			PageRequest pageDTO, SortCondition sortDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LundinBlock> criteriaQuery = cb
				.createQuery(LundinBlock.class);
		Root<LundinBlock> lundinBlockRoot = criteriaQuery
				.from(LundinBlock.class);
		criteriaQuery.select(lundinBlockRoot);

		Predicate restriction = cb.conjunction();

		Join<LundinBlock, Company> compJoin = lundinBlockRoot
				.join(LundinBlock_.company);

		restriction = cb.and(
				restriction,
				cb.equal(compJoin.get(Company_.companyId),
						conditionDTO.getCompanyId()));

		if (conditionDTO.getFromDate() != null) {
			restriction = cb.and(restriction, cb.greaterThanOrEqualTo(
					(lundinBlockRoot.get(LundinBlock_.effectiveDate)),
					conditionDTO.getFromDate()));
		}
		if (conditionDTO.getToDate() != null) {
			restriction = cb.and(restriction, cb.lessThanOrEqualTo(
					(lundinBlockRoot.get(LundinBlock_.effectiveDate)),
					conditionDTO.getToDate()));
		}
		if (conditionDTO.getBlockCode() != null) {
			restriction = cb.and(restriction, cb.like(
					lundinBlockRoot.get(LundinBlock_.blockCode)
							.as(String.class), '%' + String
							.valueOf(conditionDTO.getBlockCode()) + '%'));

		}
		if (conditionDTO.getBlockName() != null) {
			restriction = cb.and(restriction, cb.like(
					lundinBlockRoot.get(LundinBlock_.blockName)
							.as(String.class), '%' + String
							.valueOf(conditionDTO.getBlockName()) + '%'));
		}
		if (StringUtils.isNotBlank(conditionDTO.getTransactionType())) {
			restriction = cb.and(
					restriction,
					cb.equal(
							lundinBlockRoot.get(LundinBlock_.status).as(
									Boolean.class), conditionDTO.isStatus()));
		}

		criteriaQuery.where(restriction);

		TypedQuery<LundinBlock> lundinAppTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			lundinAppTypedQuery.setFirstResult(getStartPosition(pageDTO));
			lundinAppTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		return lundinAppTypedQuery.getResultList();
	}

	@Override
	public List<LundinBlock> getBlocks(Timestamp effectiveDate, long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LundinBlock> criteriaQuery = cb
				.createQuery(LundinBlock.class);
		Root<LundinBlock> lundinBlockRoot = criteriaQuery
				.from(LundinBlock.class);
		criteriaQuery.select(lundinBlockRoot);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(
				restriction,
				cb.equal(
						lundinBlockRoot.get(LundinBlock_.company).get(
								Company_.companyId), companyId));

		restriction = cb.and(restriction,
				cb.equal(lundinBlockRoot.get(LundinBlock_.status), true));

		// restriction = cb.and(
		// restriction,
		// cb.lessThanOrEqualTo(
		// blockMapAfe.get(LundinBlockAFEMapping_.lundinAfe).get(
		// LundinAFE_.effectiveDate), effectiveDate));
		//
		restriction = cb
				.and(restriction, cb.lessThanOrEqualTo(
						lundinBlockRoot.get(LundinBlock_.effectiveDate),
						effectiveDate));

		criteriaQuery.where(restriction);

		TypedQuery<LundinBlock> lundinAppTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return lundinAppTypedQuery.getResultList();
	}

	@Override
	public LundinBlock findByDescCompany(Long blockId, String blockCode,
			long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LundinBlock> criteriaQuery = cb
				.createQuery(LundinBlock.class);

		Root<LundinBlock> lundinBlockRoot = criteriaQuery
				.from(LundinBlock.class);

		criteriaQuery.select(lundinBlockRoot);

		Join<LundinBlock, Company> departmentCompanyJoin = lundinBlockRoot
				.join(LundinBlock_.company);

		Predicate restriction = cb.equal(
				departmentCompanyJoin.get(Company_.companyId), companyId);

		if (blockId != null) {
			restriction = cb.and(restriction, cb.notEqual(
					lundinBlockRoot.get(LundinBlock_.blockId), blockId));
		}

		restriction = cb.and(restriction, cb.equal(
				cb.upper(lundinBlockRoot.get(LundinBlock_.blockCode)),
				blockCode.toUpperCase()));

		criteriaQuery.where(restriction);

		TypedQuery<LundinBlock> deptTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<LundinBlock> deptList = deptTypedQuery.getResultList();
		if (deptList != null && !deptList.isEmpty()) {
			return deptList.get(0);
		}
		return null;
	}

	@Override
	public List<LundinBlock> findByCondition(Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LundinBlock> criteriaQuery = cb
				.createQuery(LundinBlock.class);

		Root<LundinBlock> lundinBlockRoot = criteriaQuery
				.from(LundinBlock.class);

		criteriaQuery.select(lundinBlockRoot);

		Join<LundinBlock, Company> departmentCompanyJoin = lundinBlockRoot
				.join(LundinBlock_.company);
		Predicate restriction = cb.conjunction();
		restriction = cb.equal(departmentCompanyJoin.get(Company_.companyId),
				companyId);

		restriction = cb.and(restriction,
				cb.equal(lundinBlockRoot.get(LundinBlock_.status), true));

		criteriaQuery.where(restriction);
		criteriaQuery.orderBy(cb.asc(lundinBlockRoot
				.get(LundinBlock_.blockName)));
		TypedQuery<LundinBlock> deptTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return deptTypedQuery.getResultList();
	}

	@Override
	public LundinBlock findById(long id, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LundinBlock> criteriaQuery = cb
				.createQuery(LundinBlock.class);

		Root<LundinBlock> lundinBlockRoot = criteriaQuery
				.from(LundinBlock.class);

		criteriaQuery.select(lundinBlockRoot);

		Join<LundinBlock, Company> departmentCompanyJoin = lundinBlockRoot
				.join(LundinBlock_.company);

		Predicate restriction = cb.equal(
				departmentCompanyJoin.get(Company_.companyId), companyId);

		
			restriction = cb.and(restriction, cb.equal(
					lundinBlockRoot.get(LundinBlock_.blockId), id));
		

		criteriaQuery.where(restriction);
		TypedQuery<LundinBlock> deptTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<LundinBlock> deptList = deptTypedQuery.getResultList();
		if (deptList != null && !deptList.isEmpty()) {
			return deptList.get(0);
		}
		return null;
	}

}
