package com.payasia.dao.impl;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.payasia.dao.BaseDAO;
import com.payasia.dao.LundinTimesheetDetailDAO;
import com.payasia.dao.bean.EmployeeTimesheetApplication_;
import com.payasia.dao.bean.LundinAFE_;
import com.payasia.dao.bean.LundinBlock_;
import com.payasia.dao.bean.LundinTimesheetDetail;
import com.payasia.dao.bean.LundinTimesheetDetail_;

@Repository
public class LundinTimesheetDetailDAOImpl extends BaseDAO implements
		LundinTimesheetDetailDAO {

	@Override
	protected Object getBaseEntity() {
		LundinTimesheetDetail obj = new LundinTimesheetDetail();
		return obj;
	}

	@Override
	public LundinTimesheetDetail findById(long id) {
		return super.findById(LundinTimesheetDetail.class, id);
	}

	@Override
	public void save(LundinTimesheetDetail timesheetDetail) {
		super.save(timesheetDetail);
	}

	@Override
	public List<LundinTimesheetDetail> findByTimesheetId(long id) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LundinTimesheetDetail> criteriaQuery = cb
				.createQuery(LundinTimesheetDetail.class);
		Root<LundinTimesheetDetail> root = criteriaQuery
				.from(LundinTimesheetDetail.class);
		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(
				root.get(LundinTimesheetDetail_.employeeTimesheetApplication)
						.get(EmployeeTimesheetApplication_.timesheetId), id));
		criteriaQuery
				.select(root)
				.where(restriction)
				.orderBy(
						cb.asc(root.get(LundinTimesheetDetail_.lundinBlock)
								.get(LundinBlock_.blockId)),
						cb.asc(root.get(LundinTimesheetDetail_.timesheetDate)));
		TypedQuery<LundinTimesheetDetail> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		return typedQuery.getResultList();
	}

	@Override
	public void update(LundinTimesheetDetail timesheetDetail) {
		super.update(timesheetDetail);

	}

	@Override
	public LundinTimesheetDetail findIfExists(long otTimesheetId,
			Timestamp otDate, long afeId, long blockId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LundinTimesheetDetail> criteriaQuery = cb
				.createQuery(LundinTimesheetDetail.class);
		Root<LundinTimesheetDetail> root = criteriaQuery
				.from(LundinTimesheetDetail.class);
		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(
				root.get(LundinTimesheetDetail_.employeeTimesheetApplication)
						.get(EmployeeTimesheetApplication_.timesheetId),
				otTimesheetId));
		restriction = cb.and(restriction, cb.equal(
				root.get(LundinTimesheetDetail_.timesheetDate), otDate));
		restriction = cb.and(
				restriction,
				cb.equal(
						root.get(LundinTimesheetDetail_.lundinAFE).get(
								LundinAFE_.afeId), afeId));
		restriction = cb.and(
				restriction,
				cb.equal(
						root.get(LundinTimesheetDetail_.lundinBlock).get(
								LundinBlock_.blockId), blockId));
		
				
		criteriaQuery.select(root).where(restriction);
		TypedQuery<LundinTimesheetDetail> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		try {
			return typedQuery.getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public void delete(long id) {
		super.delete(super.findById(LundinTimesheetDetail.class, id));
	}

	@Override
	public List<LundinTimesheetDetail> findByBlockAndAfe(long blockId,
			long afeId, long timesheetId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LundinTimesheetDetail> criteriaQuery = cb
				.createQuery(LundinTimesheetDetail.class);
		Root<LundinTimesheetDetail> root = criteriaQuery
				.from(LundinTimesheetDetail.class);
		Predicate restriction = cb.conjunction();
		restriction = cb.and(
				restriction,
				cb.equal(
						root.get(LundinTimesheetDetail_.lundinBlock).get(
								LundinBlock_.blockId), blockId));
		restriction = cb.and(
				restriction,
				cb.equal(
						root.get(LundinTimesheetDetail_.lundinAFE).get(
								LundinAFE_.afeId), afeId));
		restriction = cb.and(restriction, cb.equal(
				root.get(LundinTimesheetDetail_.employeeTimesheetApplication)
						.get(EmployeeTimesheetApplication_.timesheetId),
				timesheetId));
		criteriaQuery
				.select(root)
				.where(restriction)
				.orderBy(
						cb.asc(root.get(LundinTimesheetDetail_.lundinBlock)
								.get(LundinBlock_.blockId)),
						cb.asc(root.get(LundinTimesheetDetail_.lundinAFE).get(
								LundinAFE_.afeId)),
						cb.asc(root.get(LundinTimesheetDetail_.timesheetDate)));
		TypedQuery<LundinTimesheetDetail> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		try {
			return typedQuery.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public LundinTimesheetDetail findIfExists(long otTimesheetId, Timestamp otDate, long afeId, long blockId,
			Long companyId) {
		
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LundinTimesheetDetail> criteriaQuery = cb
				.createQuery(LundinTimesheetDetail.class);
		Root<LundinTimesheetDetail> root = criteriaQuery
				.from(LundinTimesheetDetail.class);
		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(
				root.get(LundinTimesheetDetail_.employeeTimesheetApplication)
						.get(EmployeeTimesheetApplication_.timesheetId),
				otTimesheetId));
		restriction = cb.and(restriction, cb.equal(
				root.get(LundinTimesheetDetail_.timesheetDate), otDate));
		restriction = cb.and(
				restriction,
				cb.equal(
						root.get(LundinTimesheetDetail_.lundinAFE).get(
								LundinAFE_.afeId), afeId));
		restriction = cb.and(
				restriction,
				cb.equal(
						root.get(LundinTimesheetDetail_.lundinBlock).get(
								LundinBlock_.blockId), blockId));
		
		
		restriction = cb.and(restriction,
				cb.equal(root.get(LundinTimesheetDetail_.companyId), companyId));
		
		criteriaQuery.select(root).where(restriction);
		TypedQuery<LundinTimesheetDetail> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		try {
			return typedQuery.getSingleResult();
		} catch (Exception e) {
			return null;
		}
		

	}
}
