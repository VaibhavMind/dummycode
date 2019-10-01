package com.payasia.dao.impl;

import java.lang.reflect.InvocationTargetException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.hibernate.jdbc.Work;
import org.springframework.stereotype.Repository;

import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.LeaveGrantProcDTO;
import com.payasia.common.dto.RollBackProcDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.NullAwareBeanUtilsBean;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.LeaveSchemeTypeDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.LeaveScheme;
import com.payasia.dao.bean.LeaveSchemeType;
import com.payasia.dao.bean.LeaveSchemeType_;
import com.payasia.dao.bean.LeaveScheme_;
import com.payasia.dao.bean.LeaveTypeMaster;
import com.payasia.dao.bean.LeaveTypeMaster_;

@Repository
public class LeaveSchemeTypeDAOImpl extends BaseDAO implements
		LeaveSchemeTypeDAO {

	@Override
	public List<LeaveSchemeType> findByConditionLeaveScheme(Long leaveSchemeId,
			Long leaveSchemeTypeId, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveSchemeType> criteriaQuery = cb
				.createQuery(LeaveSchemeType.class);
		Root<LeaveSchemeType> leaveTypeRoot = criteriaQuery
				.from(LeaveSchemeType.class);

		criteriaQuery.select(leaveTypeRoot);

		Join<LeaveSchemeType, LeaveScheme> leaveSchemeTypeJoin = leaveTypeRoot
				.join(LeaveSchemeType_.leaveScheme);

		Join<LeaveScheme, Company> leaveSchemeCompanyTypeJoin = leaveSchemeTypeJoin
				.join(LeaveScheme_.company);

		Join<LeaveSchemeType, LeaveTypeMaster> leaveSchemeLeaveTypeJoin = leaveTypeRoot
				.join(LeaveSchemeType_.leaveTypeMaster);
		Join<LeaveTypeMaster, Company> LeaveTypeCompanyJoin = leaveSchemeLeaveTypeJoin
				.join(LeaveTypeMaster_.company);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(
				leaveSchemeTypeJoin.get(LeaveScheme_.leaveSchemeId),
				leaveSchemeId));
		restriction = cb.and(restriction, cb.notEqual(
				leaveTypeRoot.get(LeaveSchemeType_.leaveSchemeTypeId),
				leaveSchemeTypeId));
		restriction = cb.and(restriction,
				cb.equal(leaveSchemeLeaveTypeJoin
						.get(LeaveTypeMaster_.visibility), true));
		restriction = cb.and(restriction, cb.equal(
				leaveSchemeCompanyTypeJoin.get(Company_.companyId), companyId));

		restriction = cb.and(restriction, cb.equal(
				LeaveTypeCompanyJoin.get(Company_.companyId), companyId));

		criteriaQuery.where(restriction);

		criteriaQuery.orderBy(cb.asc(leaveSchemeLeaveTypeJoin
				.get(LeaveTypeMaster_.sortOrder)));

		TypedQuery<LeaveSchemeType> leaveTypeDefinitionTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<LeaveSchemeType> leaveSchemeTypeList = leaveTypeDefinitionTypedQuery
				.getResultList();

		return leaveSchemeTypeList;
	}

	@Override
	public List<LeaveSchemeType> findByCondition(Long leaveSchemeId,
			Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveSchemeType> criteriaQuery = cb
				.createQuery(LeaveSchemeType.class);
		Root<LeaveSchemeType> leaveTypeRoot = criteriaQuery
				.from(LeaveSchemeType.class);

		criteriaQuery.select(leaveTypeRoot);

		Join<LeaveSchemeType, LeaveScheme> leaveSchemeTypeJoin = leaveTypeRoot
				.join(LeaveSchemeType_.leaveScheme);

		Join<LeaveScheme, Company> leaveSchemeCompanyTypeJoin = leaveSchemeTypeJoin
				.join(LeaveScheme_.company);

		Join<LeaveSchemeType, LeaveTypeMaster> leaveSchemeLeaveTypeJoin = leaveTypeRoot
				.join(LeaveSchemeType_.leaveTypeMaster);
		Join<LeaveTypeMaster, Company> LeaveTypeCompanyJoin = leaveSchemeLeaveTypeJoin
				.join(LeaveTypeMaster_.company);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(
				leaveSchemeTypeJoin.get(LeaveScheme_.leaveSchemeId),
				leaveSchemeId));

		restriction = cb.and(restriction,
				cb.equal(leaveSchemeLeaveTypeJoin
						.get(LeaveTypeMaster_.visibility), true));
		restriction = cb.and(restriction, cb.equal(
				leaveSchemeCompanyTypeJoin.get(Company_.companyId), companyId));

		restriction = cb.and(restriction, cb.equal(
				LeaveTypeCompanyJoin.get(Company_.companyId), companyId));

		criteriaQuery.where(restriction);

		criteriaQuery.orderBy(cb.asc(leaveSchemeLeaveTypeJoin
				.get(LeaveTypeMaster_.sortOrder)));

		TypedQuery<LeaveSchemeType> leaveTypeDefinitionTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<LeaveSchemeType> leaveSchemeTypeList = leaveTypeDefinitionTypedQuery
				.getResultList();

		return leaveSchemeTypeList;
	}

	@Override
	public List<LeaveSchemeType> findByLeaveSchemeAndEmployee(
			Long leaveSchemeId, Long employeeId, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveSchemeType> criteriaQuery = cb
				.createQuery(LeaveSchemeType.class);
		Root<LeaveSchemeType> leaveTypeRoot = criteriaQuery
				.from(LeaveSchemeType.class);

		criteriaQuery.select(leaveTypeRoot);

		Join<LeaveSchemeType, LeaveScheme> leaveSchemeTypeJoin = leaveTypeRoot
				.join(LeaveSchemeType_.leaveScheme);

		Join<LeaveScheme, Company> leaveSchemeCompanyTypeJoin = leaveSchemeTypeJoin
				.join(LeaveScheme_.company);

		Join<LeaveSchemeType, LeaveTypeMaster> leaveSchemeLeaveTypeJoin = leaveTypeRoot
				.join(LeaveSchemeType_.leaveTypeMaster);
		Join<LeaveTypeMaster, Company> LeaveTypeCompanyJoin = leaveSchemeLeaveTypeJoin
				.join(LeaveTypeMaster_.company);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(
				leaveSchemeTypeJoin.get(LeaveScheme_.leaveSchemeId),
				leaveSchemeId));
		restriction = cb.and(restriction,
				cb.equal(leaveSchemeLeaveTypeJoin
						.get(LeaveTypeMaster_.visibility), true));
		restriction = cb.and(restriction, cb.equal(
				leaveSchemeCompanyTypeJoin.get(Company_.companyId), companyId));

		restriction = cb.and(restriction, cb.equal(
				LeaveTypeCompanyJoin.get(Company_.companyId), companyId));

		criteriaQuery.where(restriction);

		TypedQuery<LeaveSchemeType> leaveTypeDefinitionTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<LeaveSchemeType> leaveSchemeTypeList = leaveTypeDefinitionTypedQuery
				.getResultList();

		return leaveSchemeTypeList;
	}

	@Override
	public List<LeaveSchemeType> findByCompany(Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveSchemeType> criteriaQuery = cb
				.createQuery(LeaveSchemeType.class);
		Root<LeaveSchemeType> leaveTypeRoot = criteriaQuery
				.from(LeaveSchemeType.class);

		criteriaQuery.select(leaveTypeRoot);

		Join<LeaveSchemeType, LeaveScheme> leaveSchemeTypeJoin = leaveTypeRoot
				.join(LeaveSchemeType_.leaveScheme);

		Join<LeaveScheme, Company> leaveSchemeCompanyJoin = leaveSchemeTypeJoin
				.join(LeaveScheme_.company);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(
				leaveSchemeCompanyJoin.get(Company_.companyId), companyId));
		restriction = cb.and(restriction, cb.equal(
				leaveSchemeTypeJoin.get(LeaveScheme_.visibility), true));

		criteriaQuery.where(restriction);
		criteriaQuery.orderBy(cb.asc(leaveSchemeTypeJoin
				.get(LeaveScheme_.schemeName)));

		TypedQuery<LeaveSchemeType> leaveTypeDefinitionTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<LeaveSchemeType> leaveSchemeTypeList = leaveTypeDefinitionTypedQuery
				.getResultList();

		return leaveSchemeTypeList;
	}

	@Override
	public Long findByConditionCount(Long leaveSchemeId, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
		Root<LeaveSchemeType> leaveTypeRoot = criteriaQuery
				.from(LeaveSchemeType.class);

		criteriaQuery.select(cb.count(leaveTypeRoot));

		Join<LeaveSchemeType, LeaveScheme> leaveSchemeTypeJoin = leaveTypeRoot
				.join(LeaveSchemeType_.leaveScheme);

		Join<LeaveScheme, Company> leaveSchemeCompanyTypeJoin = leaveSchemeTypeJoin
				.join(LeaveScheme_.company);

		Join<LeaveSchemeType, LeaveTypeMaster> leaveSchemeLeaveTypeJoin = leaveTypeRoot
				.join(LeaveSchemeType_.leaveTypeMaster);
		Join<LeaveTypeMaster, Company> LeaveTypeCompanyJoin = leaveSchemeLeaveTypeJoin
				.join(LeaveTypeMaster_.company);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(
				leaveSchemeTypeJoin.get(LeaveScheme_.leaveSchemeId),
				leaveSchemeId));
		restriction = cb.and(restriction,
				cb.equal(leaveSchemeLeaveTypeJoin
						.get(LeaveTypeMaster_.visibility), true));
		restriction = cb.and(restriction, cb.equal(
				leaveSchemeCompanyTypeJoin.get(Company_.companyId), companyId));

		restriction = cb.and(restriction, cb.equal(
				LeaveTypeCompanyJoin.get(Company_.companyId), companyId));

		criteriaQuery.where(restriction);

		TypedQuery<Long> leaveTypeDefinitionTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return leaveTypeDefinitionTypedQuery.getSingleResult();
	}

	@Override
	protected Object getBaseEntity() {
		LeaveSchemeType leaveSchemeType = new LeaveSchemeType();
		return leaveSchemeType;
	}

	@Override
	public LeaveSchemeType findById(Long leaveSchemeTypeId) {
		LeaveSchemeType leaveSchemeType = super.findById(LeaveSchemeType.class,
				leaveSchemeTypeId);
		return leaveSchemeType;
	}

	@Override
	public void update(LeaveSchemeType leaveSchemeType) {
		super.update(leaveSchemeType);
	}

	@Override
	public void save(LeaveSchemeType leaveSchemeType) {
		super.save(leaveSchemeType);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.EmployeeDAO#saveReturn(com.payasia.dao.bean.Employee)
	 */
	@Override
	public LeaveSchemeType saveReturn(LeaveSchemeType leaveSchemeType) {

		LeaveSchemeType persistObj = leaveSchemeType;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (LeaveSchemeType) getBaseEntity();
			beanUtil.copyProperties(persistObj, leaveSchemeType);
		} catch (IllegalAccessException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		} catch (InvocationTargetException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		}
		this.entityManagerFactory.persist(persistObj);
		return persistObj;
	}

	@Override
	public void delete(LeaveSchemeType leaveSchemeType) {
		super.delete(leaveSchemeType);
	}

	@Override
	public LeaveSchemeType findBySchemeType(Long leaveSchemeId, Long leaveTypeId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveSchemeType> criteriaQuery = cb
				.createQuery(LeaveSchemeType.class);
		Root<LeaveSchemeType> leaveTypeRoot = criteriaQuery
				.from(LeaveSchemeType.class);

		criteriaQuery.select(leaveTypeRoot);

		Join<LeaveSchemeType, LeaveScheme> leaveSchemeTypeJoin = leaveTypeRoot
				.join(LeaveSchemeType_.leaveScheme);

		Join<LeaveSchemeType, LeaveTypeMaster> leaveSchemeLeaveTypeJoin = leaveTypeRoot
				.join(LeaveSchemeType_.leaveTypeMaster);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(
				leaveSchemeTypeJoin.get(LeaveScheme_.leaveSchemeId),
				leaveSchemeId));

		restriction = cb.and(restriction, cb.equal(
				leaveSchemeLeaveTypeJoin.get(LeaveTypeMaster_.leaveTypeId),
				leaveTypeId));

		criteriaQuery.where(restriction);

		TypedQuery<LeaveSchemeType> leaveTypeDefinitionTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<LeaveSchemeType> leaveTypeDefinitionList = leaveTypeDefinitionTypedQuery
				.getResultList();
		if (leaveTypeDefinitionList != null
				&& !leaveTypeDefinitionList.isEmpty()) {
			return leaveTypeDefinitionList.get(0);
		}
		return null;

	}

	@Override
	public List<LeaveSchemeType> findByConditionLeaveSchemeIdCompanyId(
			Long leaveSchemeId, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveSchemeType> criteriaQuery = cb
				.createQuery(LeaveSchemeType.class);
		Root<LeaveSchemeType> leaveTypeRoot = criteriaQuery
				.from(LeaveSchemeType.class);

		criteriaQuery.select(leaveTypeRoot);

		Join<LeaveSchemeType, LeaveScheme> leaveSchemeTypeJoin = leaveTypeRoot
				.join(LeaveSchemeType_.leaveScheme);

		Join<LeaveScheme, Company> leaveSchemeCompanyTypeJoin = leaveSchemeTypeJoin
				.join(LeaveScheme_.company);

		Join<LeaveSchemeType, LeaveTypeMaster> leaveSchemeLeaveTypeJoin = leaveTypeRoot
				.join(LeaveSchemeType_.leaveTypeMaster);
		Join<LeaveTypeMaster, Company> LeaveTypeCompanyJoin = leaveSchemeLeaveTypeJoin
				.join(LeaveTypeMaster_.company);

		Predicate restriction = cb.conjunction();

		if (leaveSchemeId != 0) {
			restriction = cb.and(restriction, cb.equal(
					leaveSchemeTypeJoin.get(LeaveScheme_.leaveSchemeId),
					leaveSchemeId));
		}

		restriction = cb.and(restriction,
				cb.equal(leaveSchemeLeaveTypeJoin
						.get(LeaveTypeMaster_.visibility), true));
		restriction = cb.and(restriction, cb.equal(
				leaveSchemeCompanyTypeJoin.get(Company_.companyId), companyId));

		restriction = cb.and(restriction, cb.equal(
				LeaveTypeCompanyJoin.get(Company_.companyId), companyId));

		criteriaQuery.where(restriction);

		criteriaQuery.orderBy(cb.asc(leaveSchemeLeaveTypeJoin
				.get(LeaveTypeMaster_.sortOrder)));

		TypedQuery<LeaveSchemeType> leaveTypeDefinitionTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<LeaveSchemeType> leaveSchemeTypeList = leaveTypeDefinitionTypedQuery
				.getResultList();

		return leaveSchemeTypeList;
	}

	@Override
	public Boolean callLeaveGrantProc(final LeaveGrantProcDTO leaveGrantProcDTO) {
		final List<Boolean> statusList = new ArrayList<>();

		getSession().doWork(new Work() {

			@Override
			public void execute(Connection connection) throws SQLException {
				try {
					CallableStatement cstmt = null;
					cstmt = connection
							.prepareCall("{call Leave_Granter (?,?,?,?,?,?,?,?)}");
					cstmt.setLong("@Company_ID",
							leaveGrantProcDTO.getCompanyId());
					cstmt.setString("@Leave_Scheme_Type_ID_List",
							leaveGrantProcDTO.getLeaveSchemeTypeIds());
					cstmt.setString("@Employee_ID_List",
							leaveGrantProcDTO.getEmployeeIds());
					cstmt.setTimestamp("@From_Date",
							leaveGrantProcDTO.getFromDate());
					cstmt.setTimestamp("@To_Date",
							leaveGrantProcDTO.getToDate());
					cstmt.setBoolean("@Is_New_Hires",
							leaveGrantProcDTO.getIsNewHires());
					cstmt.setString("@User_ID", UserContext.getUserId());
					cstmt.registerOutParameter("@Status",
							java.sql.Types.BOOLEAN);
					cstmt.execute();
					statusList.add(cstmt.getBoolean("@Status"));
				} catch (SQLException e) {
					throw new PayAsiaSystemException(e.getMessage(), e);
				}

			}
		});

		return statusList.get(0);
	}

	@Override
	public RollBackProcDTO rollbackResignedEmployeeLeaveProc(
			final long employeeId, final long loggedInEmployeeId) {

		final RollBackProcDTO rollBackProcDTO = new RollBackProcDTO();

		getSession().doWork(new Work() {

			@Override
			public void execute(Connection connection) throws SQLException {
				try {
					CallableStatement cstmt = null;
					cstmt = connection
							.prepareCall("{call Rollback_Resigned_Employee_Leave (?,?,?,?,?,?)}");
					cstmt.setLong("@Employee_ID", employeeId);
					cstmt.setLong("@User_ID", loggedInEmployeeId);
					cstmt.setBoolean("@Is_Resignation", true);
					cstmt.setTimestamp("@Current_Date",
							DateUtils.getCurrentTimestamp());
					cstmt.registerOutParameter("@Status",
							java.sql.Types.BOOLEAN);
					cstmt.registerOutParameter("@Err_Msg",
							java.sql.Types.VARCHAR);
					cstmt.execute();
					rollBackProcDTO.setStatus(cstmt.getBoolean("@Status"));
					rollBackProcDTO.setErrorMsg(cstmt.getString("@Err_Msg"));

				} catch (SQLException e) {
					throw new PayAsiaSystemException(e.getMessage(), e);
				}

			}
		});

		return rollBackProcDTO;
	}

	@Override
	public LeaveSchemeType findByLeaveSchemeAndTypeName(String leaveSchemeName,
			String leaveTypeName, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveSchemeType> criteriaQuery = cb
				.createQuery(LeaveSchemeType.class);
		Root<LeaveSchemeType> leaveTypeRoot = criteriaQuery
				.from(LeaveSchemeType.class);

		criteriaQuery.select(leaveTypeRoot);

		Join<LeaveSchemeType, LeaveScheme> leaveSchemeJoin = leaveTypeRoot
				.join(LeaveSchemeType_.leaveScheme);
		Join<LeaveScheme, Company> leaveSchemeCompanyJoin = leaveSchemeJoin
				.join(LeaveScheme_.company);

		Join<LeaveSchemeType, LeaveTypeMaster> leaveTypeJoin = leaveTypeRoot
				.join(LeaveSchemeType_.leaveTypeMaster);
		Join<LeaveTypeMaster, Company> leaveTypeCompanyJoin = leaveTypeJoin
				.join(LeaveTypeMaster_.company);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(
				leaveSchemeJoin.get(LeaveScheme_.schemeName), leaveSchemeName));
		restriction = cb.and(restriction, cb.equal(
				leaveSchemeCompanyJoin.get(Company_.companyId), companyId));
		restriction = cb.and(restriction, cb.equal(
				leaveTypeJoin.get(LeaveTypeMaster_.leaveTypeName),
				leaveTypeName));
		restriction = cb.and(restriction, cb.equal(
				leaveTypeCompanyJoin.get(Company_.companyId), companyId));

		criteriaQuery.where(restriction);

		TypedQuery<LeaveSchemeType> leaveTypeDefinitionTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<LeaveSchemeType> leaveTypeDefinitionList = leaveTypeDefinitionTypedQuery
				.getResultList();
		if (leaveTypeDefinitionList != null
				&& !leaveTypeDefinitionList.isEmpty()) {
			return leaveTypeDefinitionList.get(0);
		}
		return null;
	}

	@Override
	public List<LeaveSchemeType> findLeaveSchTypeByCmpId(long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveSchemeType> criteriaQuery = cb
				.createQuery(LeaveSchemeType.class);
		Root<LeaveSchemeType> leaveTypeRoot = criteriaQuery
				.from(LeaveSchemeType.class);

		criteriaQuery.select(leaveTypeRoot);

		Join<LeaveSchemeType, LeaveScheme> leaveSchemeTypeJoin = leaveTypeRoot
				.join(LeaveSchemeType_.leaveScheme);

		Join<LeaveScheme, Company> leaveSchemeCompanyTypeJoin = leaveSchemeTypeJoin
				.join(LeaveScheme_.company);

		Join<LeaveSchemeType, LeaveTypeMaster> leaveSchemeLeaveTypeJoin = leaveTypeRoot
				.join(LeaveSchemeType_.leaveTypeMaster);
		Join<LeaveTypeMaster, Company> LeaveTypeCompanyJoin = leaveSchemeLeaveTypeJoin
				.join(LeaveTypeMaster_.company);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(leaveSchemeLeaveTypeJoin
						.get(LeaveTypeMaster_.visibility), true));

		restriction = cb.and(restriction,
				cb.equal(leaveTypeRoot.get(LeaveSchemeType_.visibility), true));

		restriction = cb.and(restriction, cb.equal(
				leaveSchemeCompanyTypeJoin.get(Company_.companyId), companyId));

		restriction = cb.and(restriction, cb.equal(
				LeaveTypeCompanyJoin.get(Company_.companyId), companyId));

		criteriaQuery.where(restriction);

		criteriaQuery.orderBy(cb.asc(leaveSchemeLeaveTypeJoin
				.get(LeaveTypeMaster_.sortOrder)));

		TypedQuery<LeaveSchemeType> leaveTypeDefinitionTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<LeaveSchemeType> leaveSchemeTypeList = leaveTypeDefinitionTypedQuery
				.getResultList();

		return leaveSchemeTypeList;
	}

	@Override
	public RollBackProcDTO childCareLeaveEntitlementProc(final long companyId,
			final long employeeId, final String leaveSchemeTypeIds) {

		final RollBackProcDTO rollBackProcDTO = new RollBackProcDTO();

		getSession().doWork(new Work() {

			@Override
			public void execute(Connection connection) throws SQLException {
				try {
					CallableStatement cstmt = null;
					cstmt = connection
							.prepareCall("{call Child_Care_Leave_Entitlement (?,?,?,?,?)}");
					cstmt.setLong("@Company_ID", companyId);
					cstmt.setLong("@Employee_ID", employeeId);

					if (StringUtils.isBlank(leaveSchemeTypeIds)) {
						cstmt.setNull("@Leave_Scheme_Type_ID_List",
								java.sql.Types.VARCHAR);
					} else {
						cstmt.setString("@Leave_Scheme_Type_ID_List",
								leaveSchemeTypeIds);
					}
					cstmt.registerOutParameter("@Status",
							java.sql.Types.BOOLEAN);
					cstmt.registerOutParameter("@Err_Msg",
							java.sql.Types.VARCHAR);
					cstmt.execute();
					rollBackProcDTO.setStatus(cstmt.getBoolean("@Status"));
					rollBackProcDTO.setErrorMsg(cstmt.getString("@Err_Msg"));

				} catch (SQLException e) {
					throw new PayAsiaSystemException(e.getMessage(), e);
				}

			}
		});

		return rollBackProcDTO;
	}

	@Override
	public LeaveSchemeType findLeaveSchemeTypeByCompId(Long leaveSchemeTypeId, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveSchemeType> criteriaQuery = cb
				.createQuery(LeaveSchemeType.class);
		Root<LeaveSchemeType> leaveTypeRoot = criteriaQuery
				.from(LeaveSchemeType.class);

		criteriaQuery.select(leaveTypeRoot);

		Join<LeaveSchemeType, LeaveScheme> leaveSchemeTypeJoin = leaveTypeRoot
				.join(LeaveSchemeType_.leaveScheme);

		Join<LeaveScheme, Company> leaveSchemeCompanyTypeJoin = leaveSchemeTypeJoin
				.join(LeaveScheme_.company);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(
				leaveTypeRoot.get(LeaveSchemeType_.leaveSchemeTypeId), leaveSchemeTypeId));
		
		restriction = cb.and(restriction, cb.equal(
				leaveSchemeCompanyTypeJoin.get(Company_.companyId), companyId));

		criteriaQuery.where(restriction);
		
		TypedQuery<LeaveSchemeType> leaveTypeDefinitionTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<LeaveSchemeType> leaveSchemeTypeList = leaveTypeDefinitionTypedQuery
				.getResultList();

		if (leaveSchemeTypeList != null
				&& !leaveSchemeTypeList.isEmpty()) {
			return leaveSchemeTypeList.get(0);
		}
		return null;
	}

}
