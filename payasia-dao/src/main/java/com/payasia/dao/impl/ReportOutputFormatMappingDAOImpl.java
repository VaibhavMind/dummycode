package com.payasia.dao.impl;

import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.payasia.dao.BaseDAO;
import com.payasia.dao.ReportOutputFormatMappingDAO;
import com.payasia.dao.bean.ReportMaster;
import com.payasia.dao.bean.ReportMaster_;
import com.payasia.dao.bean.ReportOutputFormatMapping;
import com.payasia.dao.bean.ReportOutputFormatMapping_;

/**
 * The Class ReportOutputFormatMappingDAOImpl.
 */
/**
 * @author vivekjain
 * 
 */
@Repository
public class ReportOutputFormatMappingDAOImpl extends BaseDAO implements
		ReportOutputFormatMappingDAO {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.BaseDAO#getBaseEntity()
	 */
	@Override
	protected Object getBaseEntity() {
		ReportOutputFormatMapping reportOutputFormatMapping = new ReportOutputFormatMapping();
		return reportOutputFormatMapping;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.ReportOutputFormatMappingDAO#findByReportId(java.lang
	 * .Long)
	 */
	@Override
	public List<ReportOutputFormatMapping> findByReportId(Long reportId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ReportOutputFormatMapping> criteriaQuery = cb
				.createQuery(ReportOutputFormatMapping.class);
		Root<ReportOutputFormatMapping> reportOutputFormatRoot = criteriaQuery
				.from(ReportOutputFormatMapping.class);
		criteriaQuery.select(reportOutputFormatRoot);
		Join<ReportOutputFormatMapping, ReportMaster> reportOutputFormatJoin = reportOutputFormatRoot
				.join(ReportOutputFormatMapping_.reportMaster);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(
				reportOutputFormatJoin.get(ReportMaster_.reportId), reportId));

		criteriaQuery.where(restriction);

		criteriaQuery.orderBy(cb.asc(reportOutputFormatRoot
				.get(ReportOutputFormatMapping_.reportOutputFormatMappingID)));

		TypedQuery<ReportOutputFormatMapping> reportOutputFormatTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<ReportOutputFormatMapping> allReportOutputFormatList = reportOutputFormatTypedQuery
				.getResultList();
		return allReportOutputFormatList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.ReportOutputFormatMappingDAO#findById(java.lang.Long)
	 */
	@Override
	public ReportOutputFormatMapping findById(Long reportFormatMappingId) {
		return super.findById(ReportOutputFormatMapping.class,
				reportFormatMappingId);
	}
}
