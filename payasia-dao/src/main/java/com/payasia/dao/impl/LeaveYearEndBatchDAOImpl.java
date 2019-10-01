package com.payasia.dao.impl;

import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.payasia.common.dto.YearEndProcessingConditionDTO;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.LeaveYearEndBatchDAO;
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
import com.payasia.dao.bean.LeaveYearEndBatch;
import com.payasia.dao.bean.LeaveYearEndBatch_;

@Repository
public class LeaveYearEndBatchDAOImpl extends BaseDAO implements
		LeaveYearEndBatchDAO {

	@Override
	protected Object getBaseEntity() {
		LeaveYearEndBatch leaveYearEndBatch = new LeaveYearEndBatch();
		return leaveYearEndBatch;
	}

	@Override
	public List<LeaveYearEndBatch> findByCondition(
			YearEndProcessingConditionDTO yearEndProcessingConditionDTO,
			int year, Long leaveTypeId, PageRequest pageDTO,
			SortCondition sortDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveYearEndBatch> criteriaQuery = cb
				.createQuery(LeaveYearEndBatch.class);
		Root<LeaveYearEndBatch> leaveYearEndRoot = criteriaQuery
				.from(LeaveYearEndBatch.class);
		criteriaQuery.select(leaveYearEndRoot);

		Predicate restriction = cb.conjunction();

		Join<LeaveYearEndBatch, Company> companyJoin = leaveYearEndRoot
				.join(LeaveYearEndBatch_.company);

		Join<LeaveYearEndBatch, LeaveSchemeType> leaveSchemeTypeJoin = leaveYearEndRoot
				.join(LeaveYearEndBatch_.leaveSchemeType);
		Join<LeaveSchemeType, LeaveTypeMaster> leaveTypeJoin = leaveSchemeTypeJoin
				.join(LeaveSchemeType_.leaveTypeMaster);
		Join<LeaveSchemeType, LeaveScheme> leaveSchemeJoin = leaveSchemeTypeJoin
				.join(LeaveSchemeType_.leaveScheme);

		if (yearEndProcessingConditionDTO.getLeaveTypeId() != null) {

			restriction = cb.and(restriction, cb.equal(
					leaveTypeJoin.get(LeaveTypeMaster_.leaveTypeId),
					leaveTypeId));
		}

		restriction = cb.and(restriction,
				cb.equal(
						cb.function("year", Integer.class, leaveYearEndRoot
								.get(LeaveYearEndBatch_.processedDate)), year));

		restriction = cb
				.and(restriction, cb.isNull(leaveYearEndRoot
						.get(LeaveYearEndBatch_.deletedDate)));

		if (yearEndProcessingConditionDTO.getCompanyId() != null) {

			restriction = cb.and(restriction, cb.equal(
					companyJoin.get(Company_.companyId),
					yearEndProcessingConditionDTO.getCompanyId()));
		}

		if (yearEndProcessingConditionDTO.getGroupId() != null) {

			Join<Company, CompanyGroup> groupJoin = companyJoin
					.join(Company_.companyGroup);
			restriction = cb.and(restriction, cb.equal(
					groupJoin.get(CompanyGroup_.groupId),
					yearEndProcessingConditionDTO.getGroupId()));
		}

		if (yearEndProcessingConditionDTO.getLeaveSchemeId() != null) {

			restriction = cb.and(restriction, cb.equal(
					leaveSchemeJoin.get(LeaveScheme_.leaveSchemeId),
					yearEndProcessingConditionDTO.getLeaveSchemeId()));
		}

		if (yearEndProcessingConditionDTO.getLeaveTypeId() != null) {

			restriction = cb.and(restriction, cb.equal(
					leaveTypeJoin.get(LeaveTypeMaster_.leaveTypeId),
					yearEndProcessingConditionDTO.getLeaveTypeId()));
		}

		criteriaQuery.where(restriction);

		if (sortDTO != null) {

			 
			 
			 
			 
			 
			 
			 
			 
			 
			 
			 
			 

		}

		TypedQuery<LeaveYearEndBatch> leaveYearEndTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			leaveYearEndTypedQuery.setFirstResult(getStartPosition(pageDTO));
			leaveYearEndTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		return leaveYearEndTypedQuery.getResultList();
	}

	@Override
	public Integer getCountByCondition(
			YearEndProcessingConditionDTO yearEndProcessingConditionDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Integer> criteriaQuery = cb.createQuery(Integer.class);
		Root<LeaveYearEndBatch> leaveYearEndRoot = criteriaQuery
				.from(LeaveYearEndBatch.class);
		criteriaQuery.select(cb.count(leaveYearEndRoot).as(Integer.class));

		Predicate restriction = cb.conjunction();

		Join<LeaveYearEndBatch, Company> companyJoin = leaveYearEndRoot
				.join(LeaveYearEndBatch_.company);
		Join<LeaveYearEndBatch, LeaveSchemeType> leaveSchemeTypeJoin = leaveYearEndRoot
				.join(LeaveYearEndBatch_.leaveSchemeType);
		Join<LeaveSchemeType, LeaveTypeMaster> leaveTypeJoin = leaveSchemeTypeJoin
				.join(LeaveSchemeType_.leaveTypeMaster);
		Join<LeaveSchemeType, LeaveScheme> leaveSchemeJoin = leaveSchemeTypeJoin
				.join(LeaveSchemeType_.leaveScheme);

		if (yearEndProcessingConditionDTO.getLeaveTypeId() != null) {

			restriction = cb.and(restriction, cb.equal(
					leaveTypeJoin.get(LeaveTypeMaster_.leaveTypeId),
					yearEndProcessingConditionDTO.getLeaveTypeId()));
		}

		restriction = cb.and(restriction,
				cb.equal(
						cb.function("year", Integer.class, leaveYearEndRoot
								.get(LeaveYearEndBatch_.processedDate)),
						yearEndProcessingConditionDTO.getYear()));

		restriction = cb
				.and(restriction, cb.isNull(leaveYearEndRoot
						.get(LeaveYearEndBatch_.deletedDate)));
		if (yearEndProcessingConditionDTO.getCompanyId() != null) {
			restriction = cb.and(restriction, cb.equal(
					companyJoin.get(Company_.companyId),
					yearEndProcessingConditionDTO.getCompanyId()));
		}

		if (yearEndProcessingConditionDTO.getGroupId() != null) {

			Join<Company, CompanyGroup> groupJoin = companyJoin
					.join(Company_.companyGroup);
			restriction = cb.and(restriction, cb.equal(
					groupJoin.get(CompanyGroup_.groupId),
					yearEndProcessingConditionDTO.getGroupId()));
		}

		if (yearEndProcessingConditionDTO.getLeaveSchemeId() != null) {

			restriction = cb.and(restriction, cb.equal(
					leaveSchemeJoin.get(LeaveScheme_.leaveSchemeId),
					yearEndProcessingConditionDTO.getLeaveSchemeId()));
		}

		if (yearEndProcessingConditionDTO.getLeaveTypeId() != null) {

			restriction = cb.and(restriction, cb.equal(
					leaveTypeJoin.get(LeaveTypeMaster_.leaveTypeId),
					yearEndProcessingConditionDTO.getLeaveTypeId()));
		}

		criteriaQuery.where(restriction);

		TypedQuery<Integer> leaveYearEndTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return leaveYearEndTypedQuery.getSingleResult();
	}

	@Override
	public List<Integer> getYearFromProcessedDate(Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Integer> criteriaQuery = cb.createQuery(Integer.class);
		Root<LeaveYearEndBatch> leaveYearEndRoot = criteriaQuery
				.from(LeaveYearEndBatch.class);
		criteriaQuery
				.select(cb.function("year", Integer.class,
						leaveYearEndRoot.get(LeaveYearEndBatch_.processedDate)))
				.distinct(true);

		Join<LeaveYearEndBatch, Company> companyJoin = leaveYearEndRoot
				.join(LeaveYearEndBatch_.company);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal((companyJoin.get(Company_.companyId)), companyId));

		criteriaQuery.where(restriction);
		TypedQuery<Integer> yearTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<Integer> yearList = yearTypedQuery.getResultList();
		return yearList;
	}
}
