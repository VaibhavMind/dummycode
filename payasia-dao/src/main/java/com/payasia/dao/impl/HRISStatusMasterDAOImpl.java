package com.payasia.dao.impl;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.payasia.dao.BaseDAO;
import com.payasia.dao.HRISStatusMasterDAO;
import com.payasia.dao.bean.HRISStatusMaster;
import com.payasia.dao.bean.HRISStatusMaster_;

@Repository
public class HRISStatusMasterDAOImpl extends BaseDAO implements
		HRISStatusMasterDAO {

	@Override
	protected Object getBaseEntity() {
		HRISStatusMaster hrisStatusMaster = new HRISStatusMaster();
		return hrisStatusMaster;
	}

	@Override
	public void update(HRISStatusMaster hrisStatusMaster) {
		this.entityManagerFactory.merge(hrisStatusMaster);
	}

	@Override
	public void delete(HRISStatusMaster hrisStatusMaster) {
		this.entityManagerFactory.remove(hrisStatusMaster);
	}

	@Override
	public void save(HRISStatusMaster hrisStatusMaster) {
		super.save(hrisStatusMaster);
	}

	@Override
	public HRISStatusMaster findById(Long hrisStatusMasterId) {
		return super.findById(HRISStatusMaster.class, hrisStatusMasterId);

	}

	@Override
	public HRISStatusMaster findByName(String hrisStatusName) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<HRISStatusMaster> criteriaQuery = cb
				.createQuery(HRISStatusMaster.class);
		Root<HRISStatusMaster> statusMasterRoot = criteriaQuery
				.from(HRISStatusMaster.class);
		criteriaQuery.select(statusMasterRoot);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(
				statusMasterRoot.get(HRISStatusMaster_.hrisStatusName),
				hrisStatusName));

		criteriaQuery.where(restriction);

		TypedQuery<HRISStatusMaster> statusMasterQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		HRISStatusMaster hRISStatusMaster = statusMasterQuery.getSingleResult();
		if (hRISStatusMaster != null) {
			return hRISStatusMaster;
		}
		return null;
	}
}
