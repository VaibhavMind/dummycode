package com.payasia.dao.impl;

import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.payasia.dao.BaseDAO;
import com.payasia.dao.ReportMasterDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.ReportMaster;
import com.payasia.dao.bean.ReportMaster_;

/**
 * The Class ReportMasterDAOImpl.
 */
/**
 * @author vivekjain
 * 
 */
@Repository
public class ReportMasterDAOImpl extends BaseDAO implements ReportMasterDAO {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.BaseDAO#getBaseEntity()
	 */
	@Override
	protected Object getBaseEntity() {
		ReportMaster reportMaster = new ReportMaster();
		return reportMaster;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.ReportMasterDAO#findByConditionCompany(java.lang.Long)
	 */
	@Override
	public List<ReportMaster> findByConditionCompany(Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ReportMaster> criteriaQuery = cb
				.createQuery(ReportMaster.class);
		Root<ReportMaster> reportMasterRoot = criteriaQuery
				.from(ReportMaster.class);

		criteriaQuery.select(reportMasterRoot);

		Join<ReportMaster, Company> passTypeRootJoin = reportMasterRoot
				.join(ReportMaster_.company);

		Path<Long> companyID = passTypeRootJoin.get(Company_.companyId);

		criteriaQuery.where(cb.equal(companyID, companyId));

		criteriaQuery.orderBy(cb.asc(reportMasterRoot
				.get(ReportMaster_.reportId)));

		TypedQuery<ReportMaster> reportMasterTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<ReportMaster> allReportMasterList = reportMasterTypedQuery
				.getResultList();
		return allReportMasterList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.ReportMasterDAO#findById(java.lang.Long)
	 */
	@Override
	public ReportMaster findById(Long reportId) {
		return super.findById(ReportMaster.class, reportId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.ReportMasterDAO#update(com.payasia.dao.bean.ReportMaster)
	 */
	@Override
	public void update(ReportMaster reportMaster) {
		super.update(reportMaster);

	}
}
