package com.payasia.dao.impl;

import java.lang.reflect.InvocationTargetException;
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
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.NullAwareBeanUtilsBean;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.LundinAFEDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.LundinAFE;
import com.payasia.dao.bean.LundinAFE_;
import com.payasia.dao.bean.LundinBlock;
import com.payasia.dao.bean.LundinBlock_;

@Repository
public class LundinAFEDAOImpl extends BaseDAO implements LundinAFEDAO {

	@Override
	public LundinAFE findById(long id) {
		return super.findById(LundinAFE.class, id);
	}

	@Override
	public void save(LundinAFE afe) {
		super.save(afe);
	}

	@Override
	public void update(LundinAFE afe) {
		super.update(afe);
	}

	@Override
	public void delete(long id) {
		super.delete(super.findById(LundinAFE.class, id));
	}

	@Override
	protected Object getBaseEntity() {
		LundinAFE obj = new LundinAFE();
		return obj;
	}

	@Override
	public Long getCountForCondition(LundinConditionDTO conditionDTO) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
		Root<LundinAFE> lundinAFERoot = criteriaQuery.from(LundinAFE.class);
		criteriaQuery.select(cb.count(lundinAFERoot));

		Predicate restriction = cb.conjunction();

		restriction = cb.and(
				restriction,
				cb.equal(lundinAFERoot.get(LundinAFE_.companyId),
						conditionDTO.getCompanyId()));

		if (StringUtils.isNotBlank(conditionDTO.getCreatedDate())) {
			restriction = cb.and(restriction, cb.equal(
					lundinAFERoot.get(LundinAFE_.createdDate).as(Date.class),
					DateUtils.stringToDate(conditionDTO.getCreatedDate())));

		}

		if (conditionDTO.getFromDate() != null) {
			restriction = cb.and(restriction, cb.greaterThanOrEqualTo(
					(lundinAFERoot.get(LundinAFE_.effectiveDate)),
					conditionDTO.getFromDate()));
		}
		if (conditionDTO.getToDate() != null) {
			restriction = cb.and(restriction, cb.lessThanOrEqualTo(
					(lundinAFERoot.get(LundinAFE_.effectiveDate)),
					conditionDTO.getToDate()));
		}
		if (conditionDTO.getAfeCode() != null) {
			restriction = cb.and(restriction, cb.like(
					lundinAFERoot.get(LundinAFE_.afeCode).as(String.class),
					'%' + String.valueOf(conditionDTO.getAfeCode()) + '%'));

		}
		if (conditionDTO.getAfeName() != null) {
			restriction = cb.and(restriction, cb.like(
					lundinAFERoot.get(LundinAFE_.afeName).as(String.class),
					'%' + String.valueOf(conditionDTO.getAfeName()) + '%'));
		}
		if (StringUtils.isNotBlank(conditionDTO.getTransactionType())) {
			restriction = cb.and(
					restriction,
					cb.equal(
							lundinAFERoot.get(LundinAFE_.status).as(
									Boolean.class), conditionDTO.isStatus()));
		}

		criteriaQuery.where(restriction);

		TypedQuery<Long> lundinAppTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return lundinAppTypedQuery.getSingleResult();

	}

	@Override
	public List<LundinAFE> findByCondition(LundinConditionDTO conditionDTO,
			PageRequest pageDTO, SortCondition sortDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LundinAFE> criteriaQuery = cb
				.createQuery(LundinAFE.class);
		Root<LundinAFE> LundinAFERoot = criteriaQuery.from(LundinAFE.class);
		criteriaQuery.select(LundinAFERoot);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(
				restriction,
				cb.equal(LundinAFERoot.get(LundinAFE_.companyId),
						conditionDTO.getCompanyId()));

		if (conditionDTO.getFromDate() != null) {
			restriction = cb.and(restriction, cb.greaterThanOrEqualTo(
					(LundinAFERoot.get(LundinAFE_.effectiveDate)),
					conditionDTO.getFromDate()));
		}
		if (conditionDTO.getToDate() != null) {
			restriction = cb.and(restriction, cb.lessThanOrEqualTo(
					(LundinAFERoot.get(LundinAFE_.effectiveDate)),
					conditionDTO.getToDate()));
		}
		if (conditionDTO.getAfeCode() != null) {
			restriction = cb.and(restriction, cb.like(
					LundinAFERoot.get(LundinAFE_.afeCode).as(String.class),
					'%' + String.valueOf(conditionDTO.getAfeCode()) + '%'));

		}
		if (conditionDTO.getAfeName() != null) {
			restriction = cb.and(restriction, cb.like(
					LundinAFERoot.get(LundinAFE_.afeName).as(String.class),
					'%' + String.valueOf(conditionDTO.getAfeName()) + '%'));
		}
		if (StringUtils.isNotBlank(conditionDTO.getTransactionType())) {
			restriction = cb.and(
					restriction,
					cb.equal(
							LundinAFERoot.get(LundinAFE_.status).as(
									Boolean.class), conditionDTO.isStatus()));
		}

		criteriaQuery.where(restriction);

		TypedQuery<LundinAFE> lundinAppTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			lundinAppTypedQuery.setFirstResult(getStartPosition(pageDTO));
			lundinAppTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		return lundinAppTypedQuery.getResultList();
	}

	@Override
	public List<LundinBlock> getActiveBlocksList(Timestamp effectiveDate,
			long companyId) {
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

		criteriaQuery.where(restriction);

		criteriaQuery.orderBy(cb.asc(lundinBlockRoot
				.get(LundinBlock_.blockCode)));

		TypedQuery<LundinBlock> lundinAppTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return lundinAppTypedQuery.getResultList();
	}

	@Override
	public LundinAFE saveReturn(LundinAFE lundinAfeObj) {
		LundinAFE persistObj = new LundinAFE();
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (LundinAFE) getBaseEntity();
			beanUtil.copyProperties(persistObj, lundinAfeObj);
		} catch (IllegalAccessException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		} catch (InvocationTargetException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		}
		this.entityManagerFactory.persist(persistObj);
		return persistObj;
	}

	@Override
	public LundinAFE findByAfeCode(Long afeId, String afeCode, long company) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LundinAFE> criteriaQuery = cb
				.createQuery(LundinAFE.class);

		Root<LundinAFE> lundinAfeRoot = criteriaQuery.from(LundinAFE.class);

		criteriaQuery.select(lundinAfeRoot);

		Predicate restriction = cb.equal(
				cb.upper(lundinAfeRoot.get(LundinAFE_.afeCode)),
				afeCode.toUpperCase());

		restriction = cb.and(restriction,
				cb.equal(lundinAfeRoot.get(LundinAFE_.companyId), company));

		if (afeId != null) {
			restriction = cb.and(restriction,
					cb.notEqual(lundinAfeRoot.get(LundinAFE_.afeId), afeId));
		}

		criteriaQuery.where(restriction);

		TypedQuery<LundinAFE> deptTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<LundinAFE> deptList = deptTypedQuery.getResultList();
		if (deptList != null && !deptList.isEmpty()) {
			return deptList.get(0);
		}
		return null;
	}

	@Override
	public List<LundinAFE> findByCondition(Long blockId, Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LundinAFE> criteriaQuery = cb
				.createQuery(LundinAFE.class);

		Root<LundinAFE> lundinAfeRoot = criteriaQuery.from(LundinAFE.class);

		Join<LundinAFE, LundinBlock> lundinBlockJoin = lundinAfeRoot
				.join(LundinAFE_.lundinBlocks);
		Join<LundinBlock, Company> companyJoin = lundinBlockJoin
				.join(LundinBlock_.company);

		criteriaQuery.select(lundinAfeRoot);
		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction,
				cb.equal(lundinBlockJoin.get(LundinBlock_.blockId), blockId));

		restriction = cb.and(restriction,
				cb.equal(companyJoin.get(Company_.companyId), companyId));

		restriction = cb.and(restriction,
				cb.equal(lundinAfeRoot.get(LundinAFE_.status), true));

		criteriaQuery.where(restriction);

		TypedQuery<LundinAFE> deptTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<LundinAFE> afeList = deptTypedQuery.getResultList();
		if (afeList != null && !afeList.isEmpty()) {
			return afeList;
		}
		return null;
	}

	@Override
	public LundinAFE findById(Long afeId, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LundinAFE> criteriaQuery = cb
				.createQuery(LundinAFE.class);

		Root<LundinAFE> lundinAfeRoot = criteriaQuery.from(LundinAFE.class);

		criteriaQuery.select(lundinAfeRoot);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(lundinAfeRoot.get(LundinAFE_.companyId), companyId));

		if (afeId != null) {
			restriction = cb.and(restriction,
					cb.equal(lundinAfeRoot.get(LundinAFE_.afeId), afeId));
		}

		criteriaQuery.where(restriction);

		TypedQuery<LundinAFE> deptTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<LundinAFE> deptList = deptTypedQuery.getResultList();
		if (deptList != null && !deptList.isEmpty()) {
			return deptList.get(0);
		}
		return null;
	}

}
