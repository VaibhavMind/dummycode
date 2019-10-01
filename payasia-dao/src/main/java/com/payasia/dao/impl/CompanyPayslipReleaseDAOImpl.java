package com.payasia.dao.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.payasia.common.dto.PayslipReleaseConditionDTO;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.SortConstants;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.CompanyPayslipReleaseDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.CompanyPayslipRelease;
import com.payasia.dao.bean.CompanyPayslipRelease_;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.MonthMaster;
import com.payasia.dao.bean.MonthMaster_;

@Repository
public class CompanyPayslipReleaseDAOImpl extends BaseDAO implements CompanyPayslipReleaseDAO {

	@Override
	protected Object getBaseEntity() {
		CompanyPayslipRelease companyPayslipRelease = new CompanyPayslipRelease();
		return companyPayslipRelease;
	}

	@Override
	public void save(CompanyPayslipRelease companyPayslipRelease) {
		super.save(companyPayslipRelease);

	}

	@Override
	public void delete(CompanyPayslipRelease companyPayslipRelease) {
		super.delete(companyPayslipRelease);

	}

	@Override
	public void update(CompanyPayslipRelease companyPayslipRelease) {

		super.update(companyPayslipRelease);
	}

	@Override
	public CompanyPayslipRelease findById(long companyPayslipReleaseId) {

		return super.findById(CompanyPayslipRelease.class, companyPayslipReleaseId);
	}

	@Override
	public CompanyPayslipRelease findByCompanyPayslipReleaseId(Long companyPayslipReleaseId, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CompanyPayslipRelease> criteriaQuery = cb.createQuery(CompanyPayslipRelease.class);
		Root<CompanyPayslipRelease> companyPayRelRoot = criteriaQuery.from(CompanyPayslipRelease.class);
		criteriaQuery.select(companyPayRelRoot);
		Join<CompanyPayslipRelease, Company> companyJoin = companyPayRelRoot.join(CompanyPayslipRelease_.company);
		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(companyPayRelRoot.get(CompanyPayslipRelease_.companyPayslipReleaseId), companyPayslipReleaseId));
		restriction = cb.and(restriction, cb.equal(companyJoin.get(Company_.companyId), companyId));
		criteriaQuery.where(restriction);
		TypedQuery<CompanyPayslipRelease> companyTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		List<CompanyPayslipRelease> allCompanyPayRelList = companyTypedQuery.getResultList();
		if (allCompanyPayRelList!=null && !allCompanyPayRelList.isEmpty()) {
			return allCompanyPayRelList.get(0);
		}
		return null;
	}

	@Override
	public List<CompanyPayslipRelease> findByCondition(PayslipReleaseConditionDTO conditionDTO, Long companyId,
			PageRequest pageDTO, SortCondition sortDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CompanyPayslipRelease> criteriaQuery = cb.createQuery(CompanyPayslipRelease.class);
		Root<CompanyPayslipRelease> companyPayRelRoot = criteriaQuery.from(CompanyPayslipRelease.class);

		criteriaQuery.select(companyPayRelRoot);

		Join<CompanyPayslipRelease, MonthMaster> monthJoin = companyPayRelRoot.join(CompanyPayslipRelease_.monthMaster);

		Join<CompanyPayslipRelease, Company> companyJoin = companyPayRelRoot.join(CompanyPayslipRelease_.company);

		Predicate restriction = cb.conjunction();

		if (StringUtils.isNotBlank(conditionDTO.getName())) {
			restriction = cb.and(restriction, cb.like(cb.upper(companyPayRelRoot.get(CompanyPayslipRelease_.name)),
					conditionDTO.getName() + '%'));
		}
		if (conditionDTO.getYear() != 0) {
			restriction = cb.and(restriction,
					cb.equal(companyPayRelRoot.get(CompanyPayslipRelease_.year), conditionDTO.getYear()));
		}
		if (conditionDTO.getMonthId() != null) {
			restriction = cb.and(restriction, cb.equal(monthJoin.get(MonthMaster_.monthId), conditionDTO.getMonthId()));
		}

		restriction = cb.and(restriction, cb.equal(companyJoin.get(Company_.companyId), companyId));

		criteriaQuery.where(restriction);
		if (sortDTO != null) {

			Path<String> sortPath = getSortPathForCompanyPayRel(sortDTO, companyPayRelRoot, monthJoin);
			if (sortPath != null) {
				if (SortConstants.DB_ORDER_BY_ASC.equalsIgnoreCase(sortDTO.getOrderType())) {
					criteriaQuery.orderBy(cb.asc(sortPath));
				}
				if (SortConstants.DB_ORDER_BY_DESC.equalsIgnoreCase(sortDTO.getOrderType())) {
					criteriaQuery.orderBy(cb.desc(sortPath));
				}
			}

		}

		TypedQuery<CompanyPayslipRelease> companyTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			companyTypedQuery.setFirstResult(getStartPosition(pageDTO));
			companyTypedQuery.setMaxResults(pageDTO.getPageSize());
		}
		List<CompanyPayslipRelease> allCompanyPayRelList = companyTypedQuery.getResultList();
		return allCompanyPayRelList;
	}

	private Path<String> getSortPathForCompanyPayRel(SortCondition sortDTO,
			Root<CompanyPayslipRelease> companyPayRelRoot, Join<CompanyPayslipRelease, MonthMaster> monthJoin) {

		List<String> companyPayRelList = new ArrayList<String>();
		companyPayRelList.add(SortConstants.PAYSLIP_RELEASE_NAME);
		companyPayRelList.add(SortConstants.PAYSLIP_RELEASE_PART);
		companyPayRelList.add(SortConstants.PAYSLIP_RELEASE_RELEASE);
		companyPayRelList.add(SortConstants.PAYSLIP_RELEASE_YEAR);

		List<String> monthIsColList = new ArrayList<String>();
		monthIsColList.add(SortConstants.PAYSLIP_RELEASE_MONTH);

		Path<String> sortPath = null;

		if (companyPayRelList.contains(sortDTO.getColumnName())) {
			sortPath = companyPayRelRoot.get(colMap.get(CompanyPayslipRelease.class + sortDTO.getColumnName()));
		}
		if (monthIsColList.contains(sortDTO.getColumnName())) {
			sortPath = monthJoin.get(colMap.get(MonthMaster.class + sortDTO.getColumnName()));
		}
		return sortPath;

	}

	@Override
	public Integer getCountByCondition(PayslipReleaseConditionDTO conditionDTO, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Integer> criteriaQuery = cb.createQuery(Integer.class);
		Root<CompanyPayslipRelease> companyPayRelRoot = criteriaQuery.from(CompanyPayslipRelease.class);

		criteriaQuery.select(cb.count(companyPayRelRoot).as(Integer.class));

		Join<CompanyPayslipRelease, MonthMaster> monthJoin = companyPayRelRoot.join(CompanyPayslipRelease_.monthMaster);
		Join<CompanyPayslipRelease, Company> companyJoin = companyPayRelRoot.join(CompanyPayslipRelease_.company);
		Predicate restriction = cb.conjunction();

		if (StringUtils.isNotBlank(conditionDTO.getName())) {
			restriction = cb.and(restriction, cb.like(cb.upper(companyPayRelRoot.get(CompanyPayslipRelease_.name)),
					conditionDTO.getName() + '%'));
		}
		if (conditionDTO.getYear() != 0) {
			restriction = cb.and(restriction,
					cb.equal(companyPayRelRoot.get(CompanyPayslipRelease_.year), conditionDTO.getYear()));
		}
		if (conditionDTO.getMonthId() != null) {
			restriction = cb.and(restriction, cb.equal(monthJoin.get(MonthMaster_.monthId), conditionDTO.getMonthId()));
		}
		restriction = cb.and(restriction, cb.equal(companyJoin.get(Company_.companyId), companyId));
		criteriaQuery.where(restriction);

		TypedQuery<Integer> companyTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		return companyTypedQuery.getSingleResult();
	}

	@Override
	public CompanyPayslipRelease findByCondition(Long companyPayslipReleaseId, Long monthId, int year, Integer part,
			Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CompanyPayslipRelease> criteriaQuery = cb.createQuery(CompanyPayslipRelease.class);
		Root<CompanyPayslipRelease> companyPayRelRoot = criteriaQuery.from(CompanyPayslipRelease.class);

		criteriaQuery.select(companyPayRelRoot);

		Join<CompanyPayslipRelease, MonthMaster> monthJoin = companyPayRelRoot.join(CompanyPayslipRelease_.monthMaster);

		Join<CompanyPayslipRelease, Company> companyJoin = companyPayRelRoot.join(CompanyPayslipRelease_.company);

		Predicate restriction = cb.conjunction();
		if (companyPayslipReleaseId != null) {
			restriction = cb.and(restriction, cb.notEqual(
					companyPayRelRoot.get(CompanyPayslipRelease_.companyPayslipReleaseId), companyPayslipReleaseId));
		}
		restriction = cb.and(restriction, cb.equal(companyPayRelRoot.get(CompanyPayslipRelease_.part), part));
		restriction = cb.and(restriction, cb.equal(companyPayRelRoot.get(CompanyPayslipRelease_.year), year));
		restriction = cb.and(restriction, cb.equal(monthJoin.get(MonthMaster_.monthId), monthId));

		restriction = cb.and(restriction, cb.equal(companyJoin.get(Company_.companyId), companyId));

		criteriaQuery.where(restriction);

		TypedQuery<CompanyPayslipRelease> companyTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		List<CompanyPayslipRelease> allCompanyPayRelList = companyTypedQuery.getResultList();
		if (!allCompanyPayRelList.isEmpty()) {
			return allCompanyPayRelList.get(0);
		}
		return null;
	}

	@Override
	public List<CompanyPayslipRelease> findByCompany(Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CompanyPayslipRelease> criteriaQuery = cb.createQuery(CompanyPayslipRelease.class);
		Root<CompanyPayslipRelease> companyPayRelRoot = criteriaQuery.from(CompanyPayslipRelease.class);

		criteriaQuery.select(companyPayRelRoot);

		Join<CompanyPayslipRelease, Company> companyJoin = companyPayRelRoot.join(CompanyPayslipRelease_.company);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(companyJoin.get(Company_.companyId), companyId));

		criteriaQuery.where(restriction);

		TypedQuery<CompanyPayslipRelease> companyTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		List<CompanyPayslipRelease> allCompanyPayRelList = companyTypedQuery.getResultList();
		return allCompanyPayRelList;
	}

	@Override
	public CompanyPayslipRelease findMaxPayslipRelease(Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CompanyPayslipRelease> criteriaQuery = cb.createQuery(CompanyPayslipRelease.class);
		Root<CompanyPayslipRelease> companyPayRelRoot = criteriaQuery.from(CompanyPayslipRelease.class);

		criteriaQuery.select(companyPayRelRoot);

		Join<CompanyPayslipRelease, Company> companyJoin = companyPayRelRoot.join(CompanyPayslipRelease_.company);
		Join<CompanyPayslipRelease, MonthMaster> monthJoin = companyPayRelRoot.join(CompanyPayslipRelease_.monthMaster);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(companyJoin.get(Company_.companyId), companyId));

		criteriaQuery.where(restriction);
		criteriaQuery.orderBy(cb.asc(companyPayRelRoot.get(CompanyPayslipRelease_.year)));
		criteriaQuery.orderBy(cb.asc(monthJoin.get(MonthMaster_.monthId)));
		TypedQuery<CompanyPayslipRelease> companyTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		List<CompanyPayslipRelease> allCompanyPayRelList = companyTypedQuery.getResultList();
		if (!allCompanyPayRelList.isEmpty()) {
			return allCompanyPayRelList.get(0);
		}
		return null;

	}

	@Override
	public CompanyPayslipRelease findLastPayslipReleased(Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CompanyPayslipRelease> criteriaQuery = cb.createQuery(CompanyPayslipRelease.class);
		Root<CompanyPayslipRelease> companyPayRelRoot = criteriaQuery.from(CompanyPayslipRelease.class);

		criteriaQuery.select(companyPayRelRoot);

		Join<CompanyPayslipRelease, Company> companyJoin = companyPayRelRoot.join(CompanyPayslipRelease_.company);
		Join<CompanyPayslipRelease, MonthMaster> monthJoin = companyPayRelRoot.join(CompanyPayslipRelease_.monthMaster);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(companyJoin.get(Company_.companyId), companyId));
		restriction = cb.and(restriction, cb.equal(companyPayRelRoot.get(CompanyPayslipRelease_.released), true));

		criteriaQuery.where(restriction);
		criteriaQuery.orderBy(cb.desc(companyPayRelRoot.get(CompanyPayslipRelease_.year)));
		criteriaQuery.orderBy(cb.desc(monthJoin.get(MonthMaster_.monthId)));
		TypedQuery<CompanyPayslipRelease> companyTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		List<CompanyPayslipRelease> allCompanyPayRelList = companyTypedQuery.getResultList();
		if (!allCompanyPayRelList.isEmpty()) {
			return allCompanyPayRelList.get(0);
		}
		return null;

	}

	@Override
	public boolean isFuturePaySlipReleased(Long monthId, int year, Long companyId, int part) {
		Calendar cal = Calendar.getInstance();
		cal.set(year, (monthId.intValue() - 1), part);
		Timestamp dateTime = new Timestamp(cal.getTimeInMillis());

		String queryString = "select top 1 * from Company_Payslip_Release cpr ";
		queryString += " where cpr.Company_ID= :company"
				+ " and datefromparts(cpr.Year,cpr.Month_ID,cpr.Part) >= :dateTime ";

		Query query = entityManagerFactory.createNativeQuery(queryString);
		query.setParameter("company", companyId);
		query.setParameter("dateTime", dateTime);
		List<Object[]> tuples = query.getResultList();
		if (!tuples.isEmpty()) {
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public List<CompanyPayslipRelease> findByCondition(Long companyId,boolean isReleaseStatus, Date currntDate) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CompanyPayslipRelease> criteriaQuery = cb.createQuery(CompanyPayslipRelease.class);
		Root<CompanyPayslipRelease> companyPayRelRoot = criteriaQuery.from(CompanyPayslipRelease.class);

		criteriaQuery.select(companyPayRelRoot);

		Join<CompanyPayslipRelease, Company> companyJoin = companyPayRelRoot.join(CompanyPayslipRelease_.company);
		
		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(companyJoin.get(Company_.companyId), companyId));
		restriction = cb.and(restriction, cb.equal(companyPayRelRoot.get(CompanyPayslipRelease_.released), isReleaseStatus));
		restriction = cb.and(restriction, cb.lessThanOrEqualTo(companyPayRelRoot.get(CompanyPayslipRelease_.releaseDateTime), currntDate));

		criteriaQuery.where(restriction);
		TypedQuery<CompanyPayslipRelease> companyTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		List<CompanyPayslipRelease> allCompanyPayRelList = companyTypedQuery.getResultList();
		if (!allCompanyPayRelList.isEmpty()) {
			return allCompanyPayRelList;
		}
		return allCompanyPayRelList;
	}

}
