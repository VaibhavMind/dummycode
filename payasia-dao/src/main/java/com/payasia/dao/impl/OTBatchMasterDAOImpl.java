package com.payasia.dao.impl;

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

import com.payasia.common.dto.OTBatchMasterConditionDTO;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.OTBatchMasterDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.OTBatchMaster;
import com.payasia.dao.bean.OTBatchMaster_;

@Repository
public class OTBatchMasterDAOImpl extends BaseDAO implements OTBatchMasterDAO {

	@Override
	public OTBatchMaster findByCompanyIdItemNameItemId(Long companyId,
			String name, Long batchId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<OTBatchMaster> criteriaQuery = cb
				.createQuery(OTBatchMaster.class);
		Root<OTBatchMaster> oTBatchMasterRoot = criteriaQuery
				.from(OTBatchMaster.class);
		criteriaQuery.select(oTBatchMasterRoot);

		Join<OTBatchMaster, Company> oTBatchMasterJoin = oTBatchMasterRoot
				.join(OTBatchMaster_.company);

		Path<Long> companyID = oTBatchMasterJoin.get(Company_.companyId);

		Predicate restriction = cb.conjunction();

		if (batchId != null) {
			restriction = cb.and(restriction, cb.notEqual(
					oTBatchMasterRoot.get(OTBatchMaster_.OTBatchId), batchId));
		}

		restriction = cb.and(restriction, cb.equal(companyID, companyId));

		restriction = cb.and(restriction, cb.equal(
				oTBatchMasterRoot.get(OTBatchMaster_.OTBatchDesc), name));

		criteriaQuery.where(restriction);

		TypedQuery<OTBatchMaster> oTBatchMasterQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		if (oTBatchMasterQuery.getResultList().size() > 0) {
			OTBatchMaster oTBatchMaster = oTBatchMasterQuery.getSingleResult();
			return oTBatchMaster;
		} else {
			return null;
		}
	}

	@Override
	protected Object getBaseEntity() {

		OTBatchMaster oTBatchMaster = new OTBatchMaster();

		return oTBatchMaster;
	}

	@Override
	public void update(OTBatchMaster oTBatchMaster) {
		super.update(oTBatchMaster);
	}

	@Override
	public void save(OTBatchMaster oTBatchMaster) {
		super.save(oTBatchMaster);

	}

	@Override
	public void delete(OTBatchMaster oTBatchMaster) {
		super.delete(oTBatchMaster);
	}

	@Override
	public List<OTBatchMaster> findByCondition(
			OTBatchMasterConditionDTO conditionDTO, PageRequest pageDTO,
			SortCondition sortDTO, Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<OTBatchMaster> criteriaQuery = cb
				.createQuery(OTBatchMaster.class);
		Root<OTBatchMaster> oTBatchMasterRoot = criteriaQuery
				.from(OTBatchMaster.class);
		criteriaQuery.select(oTBatchMasterRoot);

		Join<OTBatchMaster, Company> oTBatchMasterJoin = oTBatchMasterRoot
				.join(OTBatchMaster_.company);

		Path<Long> companyID = oTBatchMasterJoin.get(Company_.companyId);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(companyID, companyId));

		if (StringUtils.isNotBlank(conditionDTO.getDescription())) {

			restriction = cb.and(restriction,
					cb.like(cb.upper(oTBatchMasterRoot
							.get(OTBatchMaster_.OTBatchDesc)), conditionDTO
							.getDescription().toUpperCase()));
		}

		if (conditionDTO.getFromDate() != null) {

			restriction = cb.and(restriction, cb.equal(
					oTBatchMasterRoot.get(OTBatchMaster_.startDate),
					conditionDTO.getFromDate()));
		}

		if (conditionDTO.getToDate() != null) {

			restriction = cb.and(restriction, cb.equal(
					oTBatchMasterRoot.get(OTBatchMaster_.endDate),
					conditionDTO.getToDate()));
		}

		criteriaQuery.where(restriction);

		TypedQuery<OTBatchMaster> otBatchMasterTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			otBatchMasterTypedQuery.setFirstResult(getStartPosition(pageDTO));
			otBatchMasterTypedQuery.setMaxResults(pageDTO.getPageSize());
		}
		return otBatchMasterTypedQuery.getResultList();

	}

	@Override
	public int getCountByCondition(OTBatchMasterConditionDTO conditionDTO,
			Long companyId) {
		return findByCondition(conditionDTO, null, null, companyId).size();
	}

	@Override
	public OTBatchMaster findById(long otBatchId) {

		OTBatchMaster oTBatchMaster = super.findById(OTBatchMaster.class,
				otBatchId);
		return oTBatchMaster;
	}

}
