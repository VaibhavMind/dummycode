package com.payasia.dao.impl;

import java.lang.reflect.InvocationTargetException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import org.apache.commons.lang.StringUtils;
import org.hibernate.jdbc.Work;
import org.springframework.stereotype.Repository;

import com.payasia.common.dto.LeaveSchemeConditionDTO;
import com.payasia.common.dto.LeaveSchemeProcDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.NullAwareBeanUtilsBean;
import com.payasia.common.util.SortConstants;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.LeaveSchemeDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.CompanyGroup;
import com.payasia.dao.bean.CompanyGroup_;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.LeaveScheme;
import com.payasia.dao.bean.LeaveScheme_;

@Repository
public class LeaveSchemeDAOImpl extends BaseDAO implements LeaveSchemeDAO {

	@Override
	protected Object getBaseEntity() {
		LeaveScheme leaveScheme = new LeaveScheme();
		return leaveScheme;
	}

	@Override
	public void update(LeaveScheme leaveScheme) {
		super.update(leaveScheme);

	}

	@Override
	public void save(LeaveScheme leaveScheme) {
		super.save(leaveScheme);
	}

	@Override
	public void delete(LeaveScheme leaveScheme) {
		super.delete(leaveScheme);

	}

	@Override
	public LeaveScheme findByID(long leaveSchemeId) {
		return super.findById(LeaveScheme.class, leaveSchemeId);
	}

	@Override
	public List<Tuple> getLeaveSchemeNameTupleList(Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Tuple> criteriaQuery = cb.createQuery(Tuple.class);
		Root<LeaveScheme> LeaveSchemeRoot = criteriaQuery
				.from(LeaveScheme.class);

		Join<LeaveScheme, Company> compJoin = LeaveSchemeRoot
				.join(LeaveScheme_.company);

		List<Selection<?>> selectionItems = new ArrayList<Selection<?>>();

		selectionItems.add(LeaveSchemeRoot.get(LeaveScheme_.schemeName).alias(
				getAlias(LeaveScheme_.schemeName)));
		criteriaQuery.multiselect(selectionItems).distinct(true);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction,
				cb.equal(compJoin.get(Company_.companyId), companyId));

		criteriaQuery.where(restriction);

		TypedQuery<Tuple> companyTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<Tuple> companyGroupList = companyTypedQuery.getResultList();
		return companyGroupList;
	}

	@Override
	public List<LeaveScheme> getAllLeaveSchemeByConditionCompany(
			Long companyId, LeaveSchemeConditionDTO conditionDTO,
			PageRequest pageDTO, SortCondition sortDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveScheme> criteriaQuery = cb
				.createQuery(LeaveScheme.class);
		Root<LeaveScheme> leaveSchemeRoot = criteriaQuery
				.from(LeaveScheme.class);
		criteriaQuery.select(leaveSchemeRoot);

		Join<LeaveScheme, Company> leaveSchemeRootCompanyJoin = leaveSchemeRoot
				.join(LeaveScheme_.company);

		Predicate restriction = cb.conjunction();
		if (StringUtils.isNotBlank(conditionDTO.getSchemeName())) {

			restriction = cb.and(restriction, cb.like(
					cb.upper(leaveSchemeRoot.get(LeaveScheme_.schemeName)),
					conditionDTO.getSchemeName().toUpperCase()));
		}

		if (StringUtils.isNotBlank(conditionDTO.getStatus())) {
			if ("visible".equals(conditionDTO.getStatus())) {
				restriction = cb.and(restriction, cb.equal(
						(leaveSchemeRoot.get(LeaveScheme_.visibility)), true));
			}
			if ("hidden".equals(conditionDTO.getStatus())) {
				restriction = cb.and(restriction, cb.equal(
						(leaveSchemeRoot.get(LeaveScheme_.visibility)), false));
			}
		}

		restriction = cb.and(restriction, cb.equal(
				leaveSchemeRootCompanyJoin.get(Company_.companyId), companyId));

		criteriaQuery.where(restriction);

		if (sortDTO != null) {

			Path<String> sortPath = getSortPathForleaveScheme(sortDTO,
					leaveSchemeRoot);
			if (sortPath != null) {
				if (SortConstants.DB_ORDER_BY_ASC.equalsIgnoreCase(sortDTO
						.getOrderType())) {
					criteriaQuery.orderBy(cb.asc(sortPath));
				}
				if (SortConstants.DB_ORDER_BY_DESC.equalsIgnoreCase(sortDTO
						.getOrderType())) {
					criteriaQuery.orderBy(cb.desc(sortPath));
				}
			}

		}

		TypedQuery<LeaveScheme> leaveSchemeTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			leaveSchemeTypedQuery.setFirstResult(getStartPosition(pageDTO));
			leaveSchemeTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		List<LeaveScheme> leaveSchemeList = leaveSchemeTypedQuery
				.getResultList();

		return leaveSchemeList;
	}

	@Override
	public Path<String> getSortPathForleaveScheme(SortCondition sortDTO,
			Root<LeaveScheme> leaveSchemeRoot) {

		List<String> leaveSchemeIsColList = new ArrayList<String>();
		leaveSchemeIsColList.add(SortConstants.LEAVE_SCHEME_NAME);
		leaveSchemeIsColList.add(SortConstants.LEAVE_SCHEME_VISIBILITY);
		leaveSchemeIsColList.add(SortConstants.LEAVE_SCHEME_NUM_OF_ITEMS);

		Path<String> sortPath = null;

		if (leaveSchemeIsColList.contains(sortDTO.getColumnName())) {
			sortPath = leaveSchemeRoot.get(colMap.get(LeaveScheme.class
					+ sortDTO.getColumnName()));
		}
		return sortPath;
	}

	@Override
	public Long getCountForAllLeaveScheme(Long companyId,
			LeaveSchemeConditionDTO conditionDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
		Root<LeaveScheme> leaveSchemeRoot = criteriaQuery
				.from(LeaveScheme.class);
		criteriaQuery.select(cb.count(leaveSchemeRoot));

		Join<LeaveScheme, Company> leaveSchemeRootCompanyJoin = leaveSchemeRoot
				.join(LeaveScheme_.company);

		Predicate restriction = cb.conjunction();
		if (StringUtils.isNotBlank(conditionDTO.getSchemeName())) {

			restriction = cb.and(restriction, cb.like(
					cb.upper(leaveSchemeRoot.get(LeaveScheme_.schemeName)),
					conditionDTO.getSchemeName().toUpperCase()));
		}

		if (StringUtils.isNotBlank(conditionDTO.getStatus())) {
			if ("visible".equals(conditionDTO.getStatus())) {
				restriction = cb.and(restriction, cb.equal(
						(leaveSchemeRoot.get(LeaveScheme_.visibility)), true));
			}
			if ("hidden".equals(conditionDTO.getStatus())) {
				restriction = cb.and(restriction, cb.equal(
						(leaveSchemeRoot.get(LeaveScheme_.visibility)), false));
			}
		}

		restriction = cb.and(restriction, cb.equal(
				leaveSchemeRootCompanyJoin.get(Company_.companyId), companyId));

		criteriaQuery.where(restriction);

		TypedQuery<Long> leaveSchemeTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return leaveSchemeTypedQuery.getSingleResult();
	}

	@Override
	public LeaveScheme findByLeaveSchemeAndCompany(Long leaveSchemeId,
			String leaveSchemeName, Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveScheme> criteriaQuery = cb
				.createQuery(LeaveScheme.class);
		Root<LeaveScheme> leaveSchemeRoot = criteriaQuery
				.from(LeaveScheme.class);

		criteriaQuery.select(leaveSchemeRoot);

		Join<LeaveScheme, Company> leaveSchemeRootJoin = leaveSchemeRoot
				.join(LeaveScheme_.company);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(
				leaveSchemeRootJoin.get(Company_.companyId), companyId));

		if (leaveSchemeId != null) {
			restriction = cb.and(restriction, cb.notEqual(
					leaveSchemeRoot.get(LeaveScheme_.leaveSchemeId),
					leaveSchemeId));
		}

		restriction = cb.and(restriction, cb.equal(
				cb.upper(leaveSchemeRoot.get(LeaveScheme_.schemeName)),
				leaveSchemeName.toUpperCase()));

		criteriaQuery.where(restriction);

		TypedQuery<LeaveScheme> leaveSchemeTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<LeaveScheme> leaveSchemeList = leaveSchemeTypedQuery
				.getResultList();
		if (leaveSchemeList != null && !leaveSchemeList.isEmpty()) {
			return leaveSchemeList.get(0);
		}
		return null;

	}

	@Override
	public List<LeaveScheme> getAllLeaveScheme(Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveScheme> criteriaQuery = cb
				.createQuery(LeaveScheme.class);
		Root<LeaveScheme> leaveSchemeRoot = criteriaQuery
				.from(LeaveScheme.class);
		criteriaQuery.select(leaveSchemeRoot);

		Predicate restriction = cb.conjunction();

		if (companyId != null) {
			Join<LeaveScheme, Company> leaveSchemeRootCompanyJoin = leaveSchemeRoot
					.join(LeaveScheme_.company);

			restriction = cb.and(restriction, cb.equal(
					leaveSchemeRootCompanyJoin.get(Company_.companyId),
					companyId));
		}

		restriction = cb.and(restriction,
				cb.equal(leaveSchemeRoot.get(LeaveScheme_.visibility), true));

		criteriaQuery.where(restriction);

		TypedQuery<LeaveScheme> leaveSchemeTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<LeaveScheme> leaveSchemeList = leaveSchemeTypedQuery
				.getResultList();

		return leaveSchemeList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.EmployeeDAO#saveReturn(com.payasia.dao.bean.Employee)
	 */
	@Override
	public LeaveScheme saveReturn(LeaveScheme leaveScheme) {

		LeaveScheme persistObj = leaveScheme;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (LeaveScheme) getBaseEntity();
			beanUtil.copyProperties(persistObj, leaveScheme);
		} catch (IllegalAccessException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		} catch (InvocationTargetException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		}
		this.entityManagerFactory.persist(persistObj);
		return persistObj;
	}

	@Override
	public LeaveSchemeProcDTO callRedistributeProc(final Long leaveSchemeTypeId) {
		final LeaveSchemeProcDTO leaveSchemeProcDTO = new LeaveSchemeProcDTO();

		getSession().doWork(new Work() {

			@Override
			public void execute(Connection connection) throws SQLException {
				try {
					CallableStatement cstmt = null;
					cstmt = connection
							.prepareCall("{call Redistribute_Employee_Leave (?,?,?)}");

					cstmt.setLong("@Leave_Scheme_Type_ID", leaveSchemeTypeId);

					cstmt.registerOutParameter("@Status",
							java.sql.Types.BOOLEAN);
					cstmt.registerOutParameter("@Err_Msg ",
							java.sql.Types.VARCHAR);
					cstmt.execute();
					leaveSchemeProcDTO.setStatus(cstmt.getBoolean(2));
					leaveSchemeProcDTO.setErrorMsg(cstmt.getString(3));
				} catch (SQLException e) {
					throw new PayAsiaSystemException(e.getMessage(), e);
				}

			}
		});

		return leaveSchemeProcDTO;

	}

	@Override
	public List<LeaveScheme> getAllLeaveSchemes(Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveScheme> criteriaQuery = cb
				.createQuery(LeaveScheme.class);
		Root<LeaveScheme> leaveSchemeRoot = criteriaQuery
				.from(LeaveScheme.class);
		criteriaQuery.select(leaveSchemeRoot);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(leaveSchemeRoot.get(LeaveScheme_.visibility), true));

		criteriaQuery.where(restriction);

		TypedQuery<LeaveScheme> leaveSchemeTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<LeaveScheme> leaveSchemeList = leaveSchemeTypedQuery
				.getResultList();

		return leaveSchemeList;
	}

	@Override
	public List<LeaveScheme> getAllLeaveSchemeBasedOnGroupId(Long groupId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveScheme> criteriaQuery = cb
				.createQuery(LeaveScheme.class);
		Root<LeaveScheme> leaveSchemeRoot = criteriaQuery
				.from(LeaveScheme.class);
		criteriaQuery.select(leaveSchemeRoot);

		Predicate restriction = cb.conjunction();

		if (groupId != null) {
			Join<LeaveScheme, Company> leaveSchemeRootCompanyJoin = leaveSchemeRoot
					.join(LeaveScheme_.company);

			Join<Company, CompanyGroup> companyGroupJoin = leaveSchemeRootCompanyJoin
					.join(Company_.companyGroup);

			restriction = cb.and(restriction, cb.equal(
					companyGroupJoin.get(CompanyGroup_.groupId), groupId));
		}

		restriction = cb.and(restriction,
				cb.equal(leaveSchemeRoot.get(LeaveScheme_.visibility), true));

		criteriaQuery.where(restriction);

		TypedQuery<LeaveScheme> leaveSchemeTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<LeaveScheme> leaveSchemeList = leaveSchemeTypedQuery
				.getResultList();

		return leaveSchemeList;
	}

	@Override
	public LeaveScheme findSchemeByCompanyID(Long leaveSchemeId, Long companyId) {
		
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveScheme> criteriaQuery = cb
				.createQuery(LeaveScheme.class);
		Root<LeaveScheme> leaveSchemeRoot = criteriaQuery
				.from(LeaveScheme.class);
		criteriaQuery.select(leaveSchemeRoot);

		Predicate restriction = cb.conjunction();
		Join<LeaveScheme, Company> leaveSchemeRootCompanyJoin = leaveSchemeRoot
					.join(LeaveScheme_.company);

		restriction = cb.and(restriction, cb.equal(
				leaveSchemeRootCompanyJoin.get(Company_.companyId), companyId));

		if (leaveSchemeId != null) {
			restriction = cb.and(restriction, cb.equal(
					leaveSchemeRoot.get(LeaveScheme_.leaveSchemeId),
					leaveSchemeId));
		}
		
		criteriaQuery.where(restriction);

		TypedQuery<LeaveScheme> leaveSchemeTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<LeaveScheme> leaveSchemeList = leaveSchemeTypedQuery
				.getResultList();

		if (leaveSchemeList != null && !leaveSchemeList.isEmpty()) {
			return leaveSchemeList.get(0);
		}
		return null;
	}

}
