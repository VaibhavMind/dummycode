package com.payasia.dao.impl;

import java.lang.reflect.InvocationTargetException;
import java.math.RoundingMode;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
import org.hibernate.jdbc.Work;
import org.springframework.stereotype.Repository;

import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.EmployeeAsOnLeaveDTO;
import com.payasia.common.dto.EmployeeLeaveSchemeTypeDTO;
import com.payasia.common.dto.LeaveDTO;
import com.payasia.common.dto.YearWiseSummarryDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.LeavePreferenceForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.NullAwareBeanUtilsBean;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.SortConstants;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.EmployeeLeaveSchemeTypeDAO;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeLeaveScheme;
import com.payasia.dao.bean.EmployeeLeaveSchemeType;
import com.payasia.dao.bean.EmployeeLeaveSchemeType_;
import com.payasia.dao.bean.EmployeeLeaveScheme_;
import com.payasia.dao.bean.Employee_;
import com.payasia.dao.bean.LeaveSchemeType;
import com.payasia.dao.bean.LeaveSchemeType_;
import com.payasia.dao.bean.LeaveTypeMaster;
import com.payasia.dao.bean.LeaveTypeMaster_;





@Repository
public class EmployeeLeaveSchemeTypeDAOImpl extends BaseDAO implements
		EmployeeLeaveSchemeTypeDAO {
	
	
	@Override
	public List<EmployeeLeaveSchemeType> findByEmpLeaveSchemeId(
			Long empLeaveSchemeId, PageRequest pageDTO, SortCondition sortDTO) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeLeaveSchemeType> criteriaQuery = cb
				.createQuery(EmployeeLeaveSchemeType.class);
		Root<EmployeeLeaveSchemeType> leaveSchemeRoot = criteriaQuery
				.from(EmployeeLeaveSchemeType.class);
		criteriaQuery.select(leaveSchemeRoot);
		Join<EmployeeLeaveSchemeType, EmployeeLeaveScheme> empLeaveSchemeJoin = leaveSchemeRoot
				.join(EmployeeLeaveSchemeType_.employeeLeaveScheme);
		Join<EmployeeLeaveSchemeType, LeaveSchemeType> leaveSchemeTypeJoin = leaveSchemeRoot
				.join(EmployeeLeaveSchemeType_.leaveSchemeType);
		Join<LeaveSchemeType, LeaveTypeMaster> leaveTypeJoin = leaveSchemeTypeJoin
				.join(LeaveSchemeType_.leaveTypeMaster);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(
				leaveSchemeRoot.get(EmployeeLeaveSchemeType_.active), true));

		restriction = cb.and(restriction, cb.equal(empLeaveSchemeJoin
				.get(EmployeeLeaveScheme_.employeeLeaveSchemeId),
				empLeaveSchemeId));
		criteriaQuery.where(restriction);
		if (sortDTO != null) {

			Path<String> sortPath = getSortPath(sortDTO, leaveSchemeRoot,
					leaveTypeJoin);
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

		criteriaQuery.orderBy(cb.asc(leaveTypeJoin
				.get(LeaveTypeMaster_.sortOrder)));

		TypedQuery<EmployeeLeaveSchemeType> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			typedQuery.setFirstResult(getStartPosition(pageDTO));
			typedQuery.setMaxResults(pageDTO.getPageSize());
		}
		List<EmployeeLeaveSchemeType> resultList = typedQuery.getResultList();
		return resultList;

	}

	@Override
	public List<EmployeeLeaveSchemeType> findByConditionEmpLeaveSchemeId(
			Long empLeaveSchemeId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeLeaveSchemeType> criteriaQuery = cb
				.createQuery(EmployeeLeaveSchemeType.class);
		Root<EmployeeLeaveSchemeType> leaveSchemeRoot = criteriaQuery
				.from(EmployeeLeaveSchemeType.class);
		criteriaQuery.select(leaveSchemeRoot);
		Join<EmployeeLeaveSchemeType, EmployeeLeaveScheme> empLeaveSchemeJoin = leaveSchemeRoot
				.join(EmployeeLeaveSchemeType_.employeeLeaveScheme);
		Join<EmployeeLeaveSchemeType, LeaveSchemeType> leaveSchemeTypeJoin = leaveSchemeRoot
				.join(EmployeeLeaveSchemeType_.leaveSchemeType);

		Join<LeaveSchemeType, LeaveTypeMaster> leaveTypeMasterJoin = leaveSchemeTypeJoin
				.join(LeaveSchemeType_.leaveTypeMaster);
		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(empLeaveSchemeJoin
				.get(EmployeeLeaveScheme_.employeeLeaveSchemeId),
				empLeaveSchemeId));
		restriction = cb.and(restriction, cb.equal(
				leaveSchemeRoot.get(EmployeeLeaveSchemeType_.active), true));

		criteriaQuery.where(restriction);
		criteriaQuery.orderBy(cb.asc(leaveTypeMasterJoin
				.get(LeaveTypeMaster_.sortOrder)));
		TypedQuery<EmployeeLeaveSchemeType> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		List<EmployeeLeaveSchemeType> resultList = typedQuery.getResultList();
		return resultList;

	}

	@Override
	public List<EmployeeLeaveSchemeType> findAllByEmpLeaveSchemeId(
			Long empLeaveSchemeId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeLeaveSchemeType> criteriaQuery = cb
				.createQuery(EmployeeLeaveSchemeType.class);
		Root<EmployeeLeaveSchemeType> leaveSchemeRoot = criteriaQuery
				.from(EmployeeLeaveSchemeType.class);
		criteriaQuery.select(leaveSchemeRoot);
		Join<EmployeeLeaveSchemeType, EmployeeLeaveScheme> empLeaveSchemeJoin = leaveSchemeRoot
				.join(EmployeeLeaveSchemeType_.employeeLeaveScheme);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(empLeaveSchemeJoin
				.get(EmployeeLeaveScheme_.employeeLeaveSchemeId),
				empLeaveSchemeId));

		criteriaQuery.where(restriction);

		TypedQuery<EmployeeLeaveSchemeType> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		List<EmployeeLeaveSchemeType> resultList = typedQuery.getResultList();
		return resultList;

	}

	@Override
	public Path<String> getSortPath(SortCondition sortDTO,
			Root<EmployeeLeaveSchemeType> leaveSchemeRoot,
			Join<LeaveSchemeType, LeaveTypeMaster> leaveTypeJoin) {

		List<String> leaveSchemeIsColList = new ArrayList<String>();
		leaveSchemeIsColList.add(SortConstants.EMPLOYEE_LEAVE_SCHEME_CREDITED);
		leaveSchemeIsColList
				.add(SortConstants.EMPLOYEE_LEAVE_SCHEME_CARRIED_FORWARD);
		leaveSchemeIsColList.add(SortConstants.EMPLOYEE_LEAVE_SCHEME_BALANCE);
		leaveSchemeIsColList.add(SortConstants.EMPLOYEE_LEAVE_SCHEME_ENCASHED);
		leaveSchemeIsColList
				.add(SortConstants.EMPLOYEE_LEAVE_SCHEME_FORTFEITED);
		leaveSchemeIsColList.add(SortConstants.EMPLOYEE_LEAVE_SCHEME_PENDING);
		leaveSchemeIsColList.add(SortConstants.EMPLOYEE_LEAVE_SCHEME_TAKEN);

		List<String> leaveTypeIsColList = new ArrayList<String>();
		leaveTypeIsColList.add(SortConstants.LEAVE_REVIEWER_LEAVE_TYPE);

		Path<String> sortPath = null;

		if (leaveSchemeIsColList.contains(sortDTO.getColumnName())) {
			sortPath = leaveSchemeRoot.get(colMap
					.get(EmployeeLeaveSchemeType.class
							+ sortDTO.getColumnName()));
		}
		if (leaveTypeIsColList.contains(sortDTO.getColumnName())) {
			sortPath = leaveTypeJoin.get(colMap.get(LeaveTypeMaster.class
					+ sortDTO.getColumnName()));
		}
		return sortPath;
	}

	@Override
	public EmployeeLeaveSchemeType findByLeaveType(Long empLeaveSchemeId,
			Long leaveTypeId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeLeaveSchemeType> criteriaQuery = cb
				.createQuery(EmployeeLeaveSchemeType.class);
		Root<EmployeeLeaveSchemeType> leaveSchemeRoot = criteriaQuery
				.from(EmployeeLeaveSchemeType.class);
		criteriaQuery.select(leaveSchemeRoot);
		Join<EmployeeLeaveSchemeType, EmployeeLeaveScheme> empLeaveSchemeJoin = leaveSchemeRoot
				.join(EmployeeLeaveSchemeType_.employeeLeaveScheme);

		Join<EmployeeLeaveSchemeType, LeaveSchemeType> leaveSchemeTypeJoin = leaveSchemeRoot
				.join(EmployeeLeaveSchemeType_.leaveSchemeType);
		Join<LeaveSchemeType, LeaveTypeMaster> leaveTypeJoin = leaveSchemeTypeJoin
				.join(LeaveSchemeType_.leaveTypeMaster);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(empLeaveSchemeJoin
				.get(EmployeeLeaveScheme_.employeeLeaveSchemeId),
				empLeaveSchemeId));
		restriction = cb.and(restriction, cb.equal(
				leaveTypeJoin.get(LeaveTypeMaster_.leaveTypeId), leaveTypeId));
		criteriaQuery.where(restriction);

		TypedQuery<EmployeeLeaveSchemeType> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<EmployeeLeaveSchemeType> EmployeeLeaveSchemeList = typedQuery
				.getResultList();
		if (EmployeeLeaveSchemeList != null
				&& !EmployeeLeaveSchemeList.isEmpty()) {
			return EmployeeLeaveSchemeList.get(0);
		}
		return null;

	}

	@Override
	public EmployeeLeaveSchemeType findByEmpLeaveSchemeAndLeaveType(
			Long empLeaveSchemeId, String leaveTypeName) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeLeaveSchemeType> criteriaQuery = cb
				.createQuery(EmployeeLeaveSchemeType.class);
		Root<EmployeeLeaveSchemeType> leaveSchemeRoot = criteriaQuery
				.from(EmployeeLeaveSchemeType.class);
		criteriaQuery.select(leaveSchemeRoot);
		Join<EmployeeLeaveSchemeType, EmployeeLeaveScheme> empLeaveSchemeJoin = leaveSchemeRoot
				.join(EmployeeLeaveSchemeType_.employeeLeaveScheme);

		Join<EmployeeLeaveSchemeType, LeaveSchemeType> leaveSchemeTypeJoin = leaveSchemeRoot
				.join(EmployeeLeaveSchemeType_.leaveSchemeType);
		Join<LeaveSchemeType, LeaveTypeMaster> leaveTypeJoin = leaveSchemeTypeJoin
				.join(LeaveSchemeType_.leaveTypeMaster);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(empLeaveSchemeJoin
				.get(EmployeeLeaveScheme_.employeeLeaveSchemeId),
				empLeaveSchemeId));
		restriction = cb.and(restriction, cb.equal(
				cb.upper(leaveTypeJoin.get(LeaveTypeMaster_.leaveTypeName)),
				leaveTypeName.toUpperCase()));
		criteriaQuery.where(restriction);

		TypedQuery<EmployeeLeaveSchemeType> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<EmployeeLeaveSchemeType> EmployeeLeaveSchemeList = typedQuery
				.getResultList();
		if (EmployeeLeaveSchemeList != null
				&& !EmployeeLeaveSchemeList.isEmpty()) {
			return EmployeeLeaveSchemeList.get(0);
		}
		return null;

	}

	@Override
	public Long getCountByEmpLeaveSchemeId(Long empLeaveSchemeId,
			PageRequest pageDTO, SortCondition sortDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
		Root<EmployeeLeaveSchemeType> leaveSchemeRoot = criteriaQuery
				.from(EmployeeLeaveSchemeType.class);
		criteriaQuery.select(cb.count(leaveSchemeRoot));
		Join<EmployeeLeaveSchemeType, EmployeeLeaveScheme> empLeaveSchemeJoin = leaveSchemeRoot
				.join(EmployeeLeaveSchemeType_.employeeLeaveScheme);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(empLeaveSchemeJoin
				.get(EmployeeLeaveScheme_.employeeLeaveSchemeId),
				empLeaveSchemeId));
		criteriaQuery.where(restriction);

		TypedQuery<Long> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		return typedQuery.getSingleResult();

	}

	@Override
	public EmployeeLeaveSchemeType findById(Long empLeaveSchemeTypeId) {
		return super.findById(EmployeeLeaveSchemeType.class,
				empLeaveSchemeTypeId);
	}

	@Override
	protected Object getBaseEntity() {
		EmployeeLeaveSchemeType employeeLeaveSchemeType = new EmployeeLeaveSchemeType();
		return employeeLeaveSchemeType;
	}

	@Override
	public void update(EmployeeLeaveSchemeType employeeLeaveSchemeType) {
		super.update(employeeLeaveSchemeType);
	}

	@Override
	public void save(EmployeeLeaveSchemeType employeeLeaveSchemeType) {
		super.save(employeeLeaveSchemeType);
	}

	@Override
	public EmployeeLeaveSchemeType findByLeaveTypeAndYear(
			Long empLeaveSchemeId, Long leaveTypeId, String year) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeLeaveSchemeType> criteriaQuery = cb
				.createQuery(EmployeeLeaveSchemeType.class);
		Root<EmployeeLeaveSchemeType> leaveSchemeRoot = criteriaQuery
				.from(EmployeeLeaveSchemeType.class);
		criteriaQuery.select(leaveSchemeRoot);
		Join<EmployeeLeaveSchemeType, EmployeeLeaveScheme> empLeaveSchemeJoin = leaveSchemeRoot
				.join(EmployeeLeaveSchemeType_.employeeLeaveScheme);

		Join<EmployeeLeaveSchemeType, LeaveSchemeType> leaveSchemeTypeJoin = leaveSchemeRoot
				.join(EmployeeLeaveSchemeType_.leaveSchemeType);
		Join<LeaveSchemeType, LeaveTypeMaster> leaveTypeJoin = leaveSchemeTypeJoin
				.join(LeaveSchemeType_.leaveTypeMaster);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(empLeaveSchemeJoin
				.get(EmployeeLeaveScheme_.employeeLeaveSchemeId),
				empLeaveSchemeId));
		restriction = cb.and(restriction, cb.equal(
				leaveTypeJoin.get(LeaveTypeMaster_.leaveTypeId), leaveTypeId));

		criteriaQuery.where(restriction);

		TypedQuery<EmployeeLeaveSchemeType> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<EmployeeLeaveSchemeType> EmployeeLeaveSchemeList = typedQuery
				.getResultList();
		if (EmployeeLeaveSchemeList != null
				&& !EmployeeLeaveSchemeList.isEmpty()) {
			return EmployeeLeaveSchemeList.get(0);
		}
		return null;

	}

	@Override
	public LeaveDTO getLeaveBalance(final Long employeeLeaveSchemeTypeId) {

		final LeaveDTO leaveDTO = new LeaveDTO();

		getSession().doWork(new Work() {

			@Override
			public void execute(Connection connection) throws SQLException {
				try {
					CallableStatement cstmt = null;
					cstmt = connection
							.prepareCall("{call Get_Leave_Balance (?,?)}");

					cstmt.setLong("@Employee_Leave_Scheme_Type_ID",
							employeeLeaveSchemeTypeId);
					cstmt.registerOutParameter("@Leave_Balance",
							java.sql.Types.DECIMAL);

					cstmt.execute();
					leaveDTO.setLeaveBalance(cstmt
							.getBigDecimal("@Leave_Balance"));
				} catch (SQLException e) {
					throw new PayAsiaSystemException(e.getMessage(), e);
				}

			}
		});

		return leaveDTO;
	}

	@Override
	public List<EmployeeLeaveSchemeTypeDTO> getEmployeeLeaveBalSummaryProc(
			final Long employeeId, final Integer year,
			final Timestamp leaveCalendarStartDate,
			final Timestamp leaveCalendarEndDate) {
		final List<EmployeeLeaveSchemeTypeDTO> employeeLeaveSchemeTypeDTOList = new ArrayList<>();
		
		
		
		//
		getSession().doWork(new Work() {

			@Override
			public void execute(Connection connection) throws SQLException {
				try {
					CallableStatement cstmt = null;
					cstmt = connection
							.prepareCall("{call Employee_Leave_Balance_Summary (?,?,?,?,?)}");

					cstmt.setLong("@Employee_ID", employeeId);
					cstmt.setInt("@Year", year);
					cstmt.setLong("@User_ID",
							Long.valueOf(UserContext.getUserId()));
					cstmt.setTimestamp("@Leave_Calendar_Start_Date",
							leaveCalendarStartDate);
					cstmt.setTimestamp("@Leave_Calendar_End_Date",
							leaveCalendarEndDate);

					ResultSet rs = cstmt.executeQuery();
					if (rs != null) {
						while (rs.next()) {
							EmployeeLeaveSchemeTypeDTO employeeLeaveSchemeTypeDTO = new EmployeeLeaveSchemeTypeDTO();
							employeeLeaveSchemeTypeDTO.setLeaveSchemeTypeId(rs
									.getLong("Leave_Scheme_Type_ID"));
							employeeLeaveSchemeTypeDTO.setBalance(rs
									.getBigDecimal("Balance").setScale(2,
											RoundingMode.CEILING));
							employeeLeaveSchemeTypeDTO.setCarriedForward(rs
									.getBigDecimal("Carried_Forward").setScale(
											2, RoundingMode.CEILING));
							employeeLeaveSchemeTypeDTO.setCredited(rs
									.getBigDecimal("Credited").setScale(2,
											RoundingMode.CEILING));
							employeeLeaveSchemeTypeDTO.setEncashed(rs
									.getBigDecimal("Enchased").setScale(2,
											RoundingMode.CEILING));
							employeeLeaveSchemeTypeDTO.setForfeited(rs
									.getBigDecimal("ForFeited").setScale(2,
											RoundingMode.CEILING));
							employeeLeaveSchemeTypeDTO.setLeaveType(rs
									.getString("Leave_Type_Name"));
							employeeLeaveSchemeTypeDTO.setPending(rs
									.getBigDecimal("Pending").setScale(2,
											RoundingMode.CEILING));
							employeeLeaveSchemeTypeDTO.setTaken(rs
									.getBigDecimal("Taken").setScale(2,
											RoundingMode.CEILING));
							employeeLeaveSchemeTypeDTO.setFullEntitlement(rs
									.getBigDecimal("Full_Entitlement")
									.setScale(2, RoundingMode.CEILING));
							employeeLeaveSchemeTypeDTO.setAction(true);
							employeeLeaveSchemeTypeDTOList
									.add(employeeLeaveSchemeTypeDTO);
						}
					}
				} catch (SQLException e) {
					throw new PayAsiaSystemException(e.getMessage(), e);
				}

			}
		});

		return employeeLeaveSchemeTypeDTOList;
	}

	
	
	
	@Override
	public List<EmployeeLeaveSchemeTypeDTO> getEmployeeLeaveBalSummaryProcNew(
			final Long employeeId, final Integer year,
			final Timestamp leaveCalendarStartDate,
			final Timestamp leaveCalendarEndDate, final LeavePreferenceForm leavePreferenceForm) {
		final List<EmployeeLeaveSchemeTypeDTO> employeeLeaveSchemeTypeDTOList = new ArrayList<>();
		
		
		
		//
		getSession().doWork(new Work() {

			@Override
			public void execute(Connection connection) throws SQLException {
				try {
					CallableStatement cstmt = null;
					cstmt = connection
							.prepareCall("{call Employee_Leave_Balance_Summary (?,?,?,?,?)}");

					cstmt.setLong("@Employee_ID", employeeId);
					cstmt.setInt("@Year", year);
					cstmt.setLong("@User_ID",
							Long.valueOf(UserContext.getUserId()));
					cstmt.setTimestamp("@Leave_Calendar_Start_Date",
							leaveCalendarStartDate);
					cstmt.setTimestamp("@Leave_Calendar_End_Date",
							leaveCalendarEndDate);

					ResultSet rs = cstmt.executeQuery();
					if (rs != null) {
						while (rs.next()) {
							EmployeeLeaveSchemeTypeDTO employeeLeaveSchemeTypeDTO = new EmployeeLeaveSchemeTypeDTO();
							employeeLeaveSchemeTypeDTO.setLeaveSchemeTypeId(rs
									.getLong("Leave_Scheme_Type_ID"));
							employeeLeaveSchemeTypeDTO.setBalance(rs
									.getBigDecimal("Balance").setScale(2,
											RoundingMode.CEILING));
							employeeLeaveSchemeTypeDTO.setCarriedForward(rs
									.getBigDecimal("Carried_Forward").setScale(
											2, RoundingMode.CEILING));
							employeeLeaveSchemeTypeDTO.setCredited(rs
									.getBigDecimal("Credited").setScale(2,
											RoundingMode.CEILING));
							if(leavePreferenceForm.getShowEncashed())
							{
							employeeLeaveSchemeTypeDTO.setEncashed(rs
									.getBigDecimal("Enchased").setScale(2,
											RoundingMode.CEILING));
						    }
							employeeLeaveSchemeTypeDTO.setForfeited(rs
									.getBigDecimal("ForFeited").setScale(2,
											RoundingMode.CEILING));
							employeeLeaveSchemeTypeDTO.setLeaveType(rs
									.getString("Leave_Type_Name"));
							employeeLeaveSchemeTypeDTO.setPending(rs
									.getBigDecimal("Pending").setScale(2,
											RoundingMode.CEILING));
							employeeLeaveSchemeTypeDTO.setTaken(rs
									.getBigDecimal("Taken").setScale(2,
											RoundingMode.CEILING));
							if(leavePreferenceForm.getShowFullEntitlement())
							{
								employeeLeaveSchemeTypeDTO.setFullEntitlement(rs
										.getBigDecimal("Full_Entitlement")
										.setScale(2, RoundingMode.CEILING));
							}
							
							employeeLeaveSchemeTypeDTO.setAction(true);
							
							employeeLeaveSchemeTypeDTOList
									.add(employeeLeaveSchemeTypeDTO);
						}
					}
				} catch (SQLException e) {
					throw new PayAsiaSystemException(e.getMessage(), e);
				}

			}
		});

		return employeeLeaveSchemeTypeDTOList;
	}
	
	
	
	
	@Override
	public LeaveDTO getNoOfDays(final LeaveDTO leaveDTO) {
		final LeaveDTO lDTO = new LeaveDTO();

		Timestamp fromDate = DateUtils
				.stringToTimestamp(leaveDTO.getFromDate());
		Timestamp toDate = DateUtils.stringToTimestamp(leaveDTO.getToDate());
		Calendar calFromDate = new GregorianCalendar(1753, 1, 1);
		Calendar calToDate = new GregorianCalendar(9999, 12, 31);
		Timestamp sqlValidFromDate = new Timestamp(
				calFromDate.getTimeInMillis());
		Timestamp sqlValidToDate = new Timestamp(calToDate.getTimeInMillis());

		if (fromDate.before(sqlValidFromDate) || fromDate.after(sqlValidToDate)
				|| toDate.before(sqlValidFromDate)
				|| toDate.after(sqlValidToDate)) {
			lDTO.setErrorCode(1);
			lDTO.setErrorKey("Invalid_Date");
			return lDTO;
		}

		getSession().doWork(new Work() {

			@Override
			public void execute(Connection connection) throws SQLException {
				try {
					CallableStatement cstmt = null;

					cstmt = connection
							.prepareCall("{call Get_Days_Between_Leave_Dates (?,?,?,?,?,?,?,?)}");

					cstmt.setLong("@Employee_Leave_Scheme_Type_ID",
							leaveDTO.getEmployeeLeaveSchemeTypeId());
					cstmt.setTimestamp("@Leave_Start_Date",
							DateUtils.stringToTimestamp(leaveDTO.getFromDate()));
					cstmt.setTimestamp("@Leave_End_Date",
							DateUtils.stringToTimestamp(leaveDTO.getToDate()));
					cstmt.setLong("@Leave_Start_Session_ID",
							leaveDTO.getSession1());
					cstmt.setLong("@Leave_End_Session_ID",
							leaveDTO.getSession2());
					cstmt.registerOutParameter("@Days_Between_Dates",
							java.sql.Types.DECIMAL);
					cstmt.registerOutParameter("@Error_Code",
							java.sql.Types.INTEGER);
					cstmt.registerOutParameter("@Error_Key",
							java.sql.Types.VARCHAR);

					cstmt.execute();
					Integer errorCode = cstmt.getInt("@Error_Code");
					String errorKey = cstmt.getString("@Error_Key");
					lDTO.setDays(cstmt.getBigDecimal("@Days_Between_Dates"));
					lDTO.setErrorCode(errorCode);
					lDTO.setErrorKey(errorKey);
				} catch (SQLException e) {
					throw new PayAsiaSystemException(e.getMessage(), e);
				}

			}
		});

		return lDTO;
	}

	@Override
	public LeaveDTO getNoOfDaysForPostLeaveTranImport(final LeaveDTO leaveDTO,
			final String dateFormat) {

		final LeaveDTO lDTO = new LeaveDTO();

		getSession().doWork(new Work() {

			@Override
			public void execute(Connection connection) throws SQLException {
				try {
					CallableStatement cstmt = null;
					cstmt = connection
							.prepareCall("{call Get_Days_Between_Leave_Dates (?,?,?,?,?,?,?,?)}");

					cstmt.setLong("@Employee_Leave_Scheme_Type_ID",
							leaveDTO.getEmployeeLeaveSchemeTypeId());
					cstmt.setTimestamp("@Leave_Start_Date", DateUtils
							.stringToTimestamp(leaveDTO.getFromDate(),
									dateFormat));
					cstmt.setTimestamp("@Leave_End_Date",
							DateUtils.stringToTimestamp(leaveDTO.getToDate(),
									dateFormat));
					cstmt.setLong("@Leave_Start_Session_ID",
							leaveDTO.getSession1());
					cstmt.setLong("@Leave_End_Session_ID",
							leaveDTO.getSession2());
					cstmt.registerOutParameter("@Days_Between_Dates",
							java.sql.Types.DECIMAL);
					cstmt.registerOutParameter("@Error_Code",
							java.sql.Types.INTEGER);
					cstmt.registerOutParameter("@Error_Key",
							java.sql.Types.VARCHAR);

					cstmt.execute();
					Integer errorCode = cstmt.getInt("@Error_Code");
					String errorKey = cstmt.getString("@Error_Key");
					lDTO.setDays(cstmt.getBigDecimal("@Days_Between_Dates"));
					lDTO.setErrorCode(errorCode);
					lDTO.setErrorKey(errorKey);
				} catch (SQLException e) {
					throw new PayAsiaSystemException(e.getMessage(), e);
				}

			}
		});

		return lDTO;
	}

	@Override
	public EmployeeLeaveSchemeType saveReturn(
			EmployeeLeaveSchemeType employeeLeaveSchemeType) {
		EmployeeLeaveSchemeType persistObj = employeeLeaveSchemeType;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (EmployeeLeaveSchemeType) getBaseEntity();
			beanUtil.copyProperties(persistObj, employeeLeaveSchemeType);
		} catch (IllegalAccessException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		} catch (InvocationTargetException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		}
		this.entityManagerFactory.persist(persistObj);
		return persistObj;
	}

	@Override
	public LeaveDTO distributeEmployeeLeave(final Long employeeId,
			final Long employeeLeaveSchemeTypeId) {

		final LeaveDTO lDTO = new LeaveDTO();

		getSession().doWork(new Work() {

			@Override
			public void execute(Connection connection) throws SQLException {
				try {
					CallableStatement cstmt = null;
					cstmt = connection
							.prepareCall("{call Distribute_Employee_Leave (?,?,?,?)}");

					cstmt.setLong("@Employee_ID", employeeId);
					cstmt.setLong("@Employee_Leave_Scheme_Type_ID",
							employeeLeaveSchemeTypeId);
					cstmt.registerOutParameter("@Status",
							java.sql.Types.BOOLEAN);
					cstmt.registerOutParameter("@Err_Msg",
							java.sql.Types.VARCHAR);

					cstmt.execute();
					lDTO.setStatus(cstmt.getBoolean("@Status"));
					lDTO.setErrorKey(cstmt.getString("@Err_Msg"));
				} catch (SQLException e) {
					throw new PayAsiaSystemException(e.getMessage(), e);
				}

			}
		});

		return lDTO;
	}

	@Override
	public EmployeeLeaveSchemeType findByCondition(String employeeNumber,
			String leaveTypeName, Long companyId, String date, String dateFormat) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeLeaveSchemeType> criteriaQuery = cb
				.createQuery(EmployeeLeaveSchemeType.class);
		Root<EmployeeLeaveSchemeType> leaveSchemeRoot = criteriaQuery
				.from(EmployeeLeaveSchemeType.class);
		criteriaQuery.select(leaveSchemeRoot);
		Join<EmployeeLeaveSchemeType, EmployeeLeaveScheme> empLeaveSchemeJoin = leaveSchemeRoot
				.join(EmployeeLeaveSchemeType_.employeeLeaveScheme);
		Join<EmployeeLeaveScheme, Employee> empLeaveSchemeEmpJoin = empLeaveSchemeJoin
				.join(EmployeeLeaveScheme_.employee);

		Join<EmployeeLeaveSchemeType, LeaveSchemeType> leaveSchemeTypeJoin = leaveSchemeRoot
				.join(EmployeeLeaveSchemeType_.leaveSchemeType);
		Join<LeaveSchemeType, LeaveTypeMaster> leaveTypeJoin = leaveSchemeTypeJoin
				.join(LeaveSchemeType_.leaveTypeMaster);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(
				cb.upper(empLeaveSchemeEmpJoin.get(Employee_.employeeNumber)),
				employeeNumber.toUpperCase()));
		restriction = cb.and(restriction, cb.equal(
				cb.upper(leaveTypeJoin.get(LeaveTypeMaster_.leaveTypeName)),
				leaveTypeName.toUpperCase()));
		restriction = cb.and(
				restriction,
				cb.equal(
						leaveTypeJoin.get(LeaveTypeMaster_.company)
								.get("companyId").as(Long.class), companyId));
		restriction = cb.and(
				restriction,
				cb.equal(
						empLeaveSchemeEmpJoin.get(Employee_.company)
								.get("companyId").as(Long.class), companyId));
		Calendar dateForNull = new GregorianCalendar(
				PayAsiaConstants.LAST_YEAR, PayAsiaConstants.LAST_MONTH,
				PayAsiaConstants.LAST_DAY);
		Timestamp timeStampForNull = new Timestamp(
				dateForNull.getTimeInMillis());

		restriction = cb.and(restriction, cb.lessThanOrEqualTo(
				empLeaveSchemeJoin.get(EmployeeLeaveScheme_.startDate),
				DateUtils.stringToTimestamp(date, dateFormat)), cb
				.greaterThanOrEqualTo(cb.coalesce(
						empLeaveSchemeJoin.get(EmployeeLeaveScheme_.endDate),
						timeStampForNull), DateUtils.stringToTimestamp(date,
						dateFormat)));

		criteriaQuery.where(restriction);

		TypedQuery<EmployeeLeaveSchemeType> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<EmployeeLeaveSchemeType> EmployeeLeaveSchemeList = typedQuery
				.getResultList();
		if (EmployeeLeaveSchemeList != null
				&& !EmployeeLeaveSchemeList.isEmpty()) {
			return EmployeeLeaveSchemeList.get(0);
		}
		return null;

	}

	@Override
	public List<EmployeeAsOnLeaveDTO> findEmpLeavesAsOnDate(
			final Long companyId, final String employeeIds,
			final Boolean isAllEmployees, final String leaveBalAsOnDate) {
		final List<EmployeeAsOnLeaveDTO> employeeAsOnLeaveDTOs = new ArrayList<>();

		getSession().doWork(new Work() {

			@Override
			public void execute(Connection connection) throws SQLException {
				try {
					CallableStatement cstmt = null;
					cstmt = connection
							.prepareCall("{call Leave_Balance_As_On_Day (?,?,?,?)}");

					cstmt.setLong("@Company_ID", companyId);
					cstmt.setString("@Employee_ID_List", employeeIds);
					cstmt.setBoolean("@All_Employees", isAllEmployees);
					cstmt.setTimestamp("@As_On_Date",
							DateUtils.stringToTimestamp(leaveBalAsOnDate));
					ResultSet rs = cstmt.executeQuery();
					if (rs != null) {
						while (rs.next()) {
							EmployeeAsOnLeaveDTO asOnLeaveDTO = new EmployeeAsOnLeaveDTO();
							asOnLeaveDTO.setEmployeeId(rs
									.getLong("Employee_ID"));
							asOnLeaveDTO.setEmployeeNumber(rs
									.getString("Employee_Number"));
							asOnLeaveDTO.setFirstName(rs
									.getString("First_Name"));
							asOnLeaveDTO.setLastName(rs.getString("Last_Name"));
							asOnLeaveDTO.setLeaveTypeName(rs
									.getString("Leave_Type_Name"));
							asOnLeaveDTO.setBalance(rs.getBigDecimal("Balance"));

							employeeAsOnLeaveDTOs.add(asOnLeaveDTO);
						}
					}
				} catch (SQLException e) {
					throw new PayAsiaSystemException(e.getMessage(), e);
				}

			}
		});

		return employeeAsOnLeaveDTOs;
	}

	@Override
	public List<EmployeeAsOnLeaveDTO> findLeaveBalAsOnDateCustomEmpReport(
			final Long companyId, final String employeeIds,
			final Boolean isAllEmployees, final String leaveBalAsOnDate) {
		final List<EmployeeAsOnLeaveDTO> employeeAsOnLeaveDTOs = new ArrayList<>();

		getSession().doWork(new Work() {

			@Override
			public void execute(Connection connection) throws SQLException {
				try {
					CallableStatement cstmt = null;
					cstmt = connection
							.prepareCall("{call Leave_Balance_As_On_Day (?,?,?,?)}");

					cstmt.setLong("@Company_ID", companyId);
					cstmt.setString("@Employee_ID_List", employeeIds);
					cstmt.setBoolean("@All_Employees", isAllEmployees);
					cstmt.setTimestamp("@As_On_Date",
							DateUtils.stringToTimestamp(leaveBalAsOnDate));
					ResultSet rs = cstmt.executeQuery();
					if (rs != null) {
						while (rs.next()) {
							EmployeeAsOnLeaveDTO asOnLeaveDTO = new EmployeeAsOnLeaveDTO();
							asOnLeaveDTO.setEmployeeId(rs
									.getLong("Employee_ID"));
							asOnLeaveDTO.setEmployeeNumber(rs
									.getString("Employee_Number"));
							asOnLeaveDTO.setFirstName(rs
									.getString("First_Name"));
							asOnLeaveDTO.setLastName(rs.getString("Last_Name"));
							asOnLeaveDTO.setLeaveTypeName(rs
									.getString("Leave_Type_Name"));
							asOnLeaveDTO.setBalance(rs.getBigDecimal("Balance"));
							asOnLeaveDTO.setTaken(rs.getBigDecimal("Taken"));
							asOnLeaveDTO.setAvailable(rs
									.getBigDecimal("Available"));

							employeeAsOnLeaveDTOs.add(asOnLeaveDTO);
						}
					}
				} catch (SQLException e) {
					throw new PayAsiaSystemException(e.getMessage(), e);
				}

			}
		});

		return employeeAsOnLeaveDTOs;
	}

	@Override
	public List<EmployeeAsOnLeaveDTO> findLeaveReviewerReportData(
			final Long companyId, final String employeeIds,
			final Boolean isAllEmployees) {
		final List<EmployeeAsOnLeaveDTO> employeeAsOnLeaveDTOs = new ArrayList<>();

		getSession().doWork(new Work() {

			@Override
			public void execute(Connection connection) throws SQLException {
				try {
					CallableStatement cstmt = null;
					cstmt = connection
							.prepareCall("{call Leave_Reviewer (?,?,?)}");

					cstmt.setLong("@Company_ID", companyId);
					cstmt.setString("@Employee_ID_List", employeeIds);
					cstmt.setBoolean("@All_Employees", isAllEmployees);

					ResultSet rs = cstmt.executeQuery();
					if (rs != null) {
						while (rs.next()) {
							EmployeeAsOnLeaveDTO asOnLeaveDTO = new EmployeeAsOnLeaveDTO();
							asOnLeaveDTO.setEmployeeNumber(rs
									.getString("Employee_Number"));
							asOnLeaveDTO.setLeaveSchemeName(rs
									.getString("Leave_Scheme_Name"));
							asOnLeaveDTO.setLeaveTypeName(rs
									.getString("Leave_Type_Name"));
							asOnLeaveDTO.setReviewer1EmployeeNo(rs
									.getString("Reviewer1_Employee_Number"));
							asOnLeaveDTO.setReviewer1FirstName(rs
									.getString("Reviewer1_First_Name"));
							asOnLeaveDTO.setReviewer1LastName(rs
									.getString("Reviewer1_Last_Name"));
							asOnLeaveDTO.setReviewer1Email(rs
									.getString("Reviewer1_Email"));
							asOnLeaveDTO.setReviewer2EmployeeNo(rs
									.getString("Reviewer2_Employee_Number"));
							asOnLeaveDTO.setReviewer2FirstName(rs
									.getString("Reviewer2_First_Name"));
							asOnLeaveDTO.setReviewer2LastName(rs
									.getString("Reviewer2_Last_Name"));
							asOnLeaveDTO.setReviewer2Email(rs
									.getString("Reviewer2_Email"));
							asOnLeaveDTO.setReviewer3EmployeeNo(rs
									.getString("Reviewer3_Employee_Number"));
							asOnLeaveDTO.setReviewer3FirstName(rs
									.getString("Reviewer3_First_Name"));
							asOnLeaveDTO.setReviewer3LastName(rs
									.getString("Reviewer3_Last_Name"));
							asOnLeaveDTO.setReviewer3Email(rs
									.getString("Reviewer3_Email"));
							asOnLeaveDTO.setFirstName(rs
									.getString("First_Name"));
							asOnLeaveDTO.setLastName(rs.getString("Last_Name"));

							employeeAsOnLeaveDTOs.add(asOnLeaveDTO);
						}
					}
				} catch (SQLException e) {
					throw new PayAsiaSystemException(e.getMessage(), e);
				}

			}
		});

		return employeeAsOnLeaveDTOs;
	}

	@Override
	public List<YearWiseSummarryDTO> findYearWiseSummaryReport(
			final Long companyId, final Integer year, final String fromDate,
			final String toDate, final String leaveTypeIds,
			final String employeeIds, Boolean isManager,
			final boolean isIncludeResignedEmployees,
			final String defaultDateFormat, final Long leaveReviewerId) {

		final List<YearWiseSummarryDTO> yearWiseSummarryDTOs = new ArrayList<>();
		if (isManager) {
			if (StringUtils.isBlank(employeeIds)) {
				return yearWiseSummarryDTOs;
			}
		}

		getSession().doWork(new Work() {

			@Override
			public void execute(Connection connection) throws SQLException {
				try {
					CallableStatement cstmt = null;
					cstmt = connection
							.prepareCall("{call Leave_Year_Wise_Summary (?,?,?,?,?,?,?,?)}");

					cstmt.setLong("@Company_ID", companyId);

					if (year == 0) {
						cstmt.setNull("@Year", java.sql.Types.INTEGER);
					} else {
						cstmt.setInt("@Year", year);
					}

					if (StringUtils.isBlank(fromDate)) {
						cstmt.setNull("@From_Date", java.sql.Types.TIMESTAMP);
					} else {
						cstmt.setTimestamp("@From_Date", DateUtils
								.stringToTimestamp(fromDate, defaultDateFormat));
					}

					if (StringUtils.isBlank(toDate)) {
						cstmt.setNull("@To_Date", java.sql.Types.TIMESTAMP);
					} else {
						cstmt.setTimestamp("@To_Date", DateUtils
								.stringToTimestamp(toDate, defaultDateFormat));
					}

					if (leaveTypeIds.equals("0")) {
						cstmt.setNull("@Leave_Type_Id_List",
								java.sql.Types.VARCHAR);
					} else {
						cstmt.setString("@Leave_Type_Id_List", leaveTypeIds);
					}

					if (StringUtils.isBlank(employeeIds)) {
						cstmt.setNull("@Employee_Id_List",
								java.sql.Types.VARCHAR);
					} else {
						cstmt.setString("@Employee_Id_List", employeeIds);
					}
					cstmt.setBoolean("@Include_Resigned_Employee",
							isIncludeResignedEmployees);
					if (leaveReviewerId == null) {
						cstmt.setNull("@Leave_Reviewer_ID",
								java.sql.Types.BIGINT);
					} else {
						cstmt.setLong("@Leave_Reviewer_ID", leaveReviewerId);
					}
					ResultSet rs = cstmt.executeQuery();
					if (rs != null) {
						while (rs.next()) {
							YearWiseSummarryDTO yearWiseSummarryDTO = new YearWiseSummarryDTO();

							yearWiseSummarryDTO.setEmployeeId(rs
									.getLong("Employee_ID"));

							yearWiseSummarryDTO.setEmployeeNumber(rs
									.getString("Employee_Number"));
							yearWiseSummarryDTO.setFirstName(rs
									.getString("First_Name"));
							yearWiseSummarryDTO.setLastName(rs
									.getString("Last_Name"));
							yearWiseSummarryDTO.setCarryForward(rs
									.getString("Carried_Forward") == null ? rs
									.getString("Carried_Forward") : String
									.valueOf(rs.getFloat("Carried_Forward")));
							yearWiseSummarryDTO.setCredited(rs
									.getString("Credited") == null ? rs
									.getString("Credited") : String.valueOf(rs
									.getFloat("Credited")));
							yearWiseSummarryDTO.setEncashed(rs
									.getString("Encashed") == null ? rs
									.getString("Encashed") : String.valueOf(rs
									.getFloat("Encashed")));
							yearWiseSummarryDTO.setForfeited(rs
									.getString("Forfeited") == null ? rs
									.getString("Forfeited") : String.valueOf(rs
									.getFloat("Forfeited")));
							yearWiseSummarryDTO
									.setTaken(rs.getString("Taken") == null ? rs
											.getString("Taken") : String
											.valueOf(rs.getFloat("Taken")));
							yearWiseSummarryDTO.setLeaveTypeName(rs
									.getString("Leave_Type_Name"));
							yearWiseSummarryDTO.setClosingBalance(rs
									.getString("Closing_Balance") == null ? rs
									.getString("Closing_Balance") : String
									.valueOf(rs.getFloat("Closing_Balance")));

							yearWiseSummarryDTOs.add(yearWiseSummarryDTO);
						}
					}
				} catch (SQLException e) {
					throw new PayAsiaSystemException(e.getMessage(), e);
				}

			}
		});

		return yearWiseSummarryDTOs;
	}

	@Override
	public EmployeeLeaveSchemeType findByLeaveSchIdAndEmpId(Long employeeId,
			Long leaveSchemeTypeId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeLeaveSchemeType> criteriaQuery = cb
				.createQuery(EmployeeLeaveSchemeType.class);
		Root<EmployeeLeaveSchemeType> leaveSchemeRoot = criteriaQuery
				.from(EmployeeLeaveSchemeType.class);
		criteriaQuery.select(leaveSchemeRoot);
		Join<EmployeeLeaveSchemeType, EmployeeLeaveScheme> empLeaveSchemeJoin = leaveSchemeRoot
				.join(EmployeeLeaveSchemeType_.employeeLeaveScheme);
		Join<EmployeeLeaveScheme, Employee> empLeaveSchemeEmpJoin = empLeaveSchemeJoin
				.join(EmployeeLeaveScheme_.employee);
		Join<EmployeeLeaveSchemeType, LeaveSchemeType> leaveSchemeTypeJoin = leaveSchemeRoot
				.join(EmployeeLeaveSchemeType_.leaveSchemeType);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(
				empLeaveSchemeEmpJoin.get(Employee_.employeeId), employeeId));
		restriction = cb.and(restriction, cb.equal(
				leaveSchemeTypeJoin.get(LeaveSchemeType_.leaveSchemeTypeId),
				leaveSchemeTypeId));

		criteriaQuery.where(restriction);

		TypedQuery<EmployeeLeaveSchemeType> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<EmployeeLeaveSchemeType> EmployeeLeaveSchemeList = typedQuery
				.getResultList();
		if (EmployeeLeaveSchemeList != null
				&& !EmployeeLeaveSchemeList.isEmpty()) {
			return EmployeeLeaveSchemeList.get(0);
		}
		return null;

	}

	@Override
	public List<EmployeeLeaveSchemeType> findByCompany(Long companyId,
			Date currentDate) {

		String queryString = "select ELST from EmployeeLeaveSchemeType ELST inner join ELST.employeeLeaveScheme ELS inner join ELS.leaveScheme LS where LS.company.companyId = :companyId  AND   cast(:currentDate as date) between ELS.startDate and isnull(ELS.endDate,'2999-12-31') ";
		Query query = entityManagerFactory.createQuery(queryString);
		query.setParameter("companyId", companyId);
		query.setParameter("currentDate",
				DateUtils.convertDateToTimeStamp(currentDate));
		List<EmployeeLeaveSchemeType> employeeLeaveSchemeTypes = query
				.getResultList();
		return employeeLeaveSchemeTypes;

	}
	
	@Override
	public EmployeeLeaveSchemeType findByleaveSchemeTypeIdAndCompanyIdAndEmpId(Long employeeLeaveSchemeTypeId, Long companyId,
			Long employeeId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeLeaveSchemeType> criteriaQuery = cb.createQuery(EmployeeLeaveSchemeType.class);
		Root<EmployeeLeaveSchemeType> empLeaveSchemeTypeRoot = criteriaQuery.from(EmployeeLeaveSchemeType.class);
		criteriaQuery.select(empLeaveSchemeTypeRoot);

		Join<EmployeeLeaveSchemeType, EmployeeLeaveScheme> empLeaveSchemeJoin = empLeaveSchemeTypeRoot
				.join(EmployeeLeaveSchemeType_.employeeLeaveScheme);
		Join<EmployeeLeaveScheme, Employee> empLeaveSchemeEmpJoin = empLeaveSchemeJoin
				.join(EmployeeLeaveScheme_.employee);
		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction,
				cb.equal(empLeaveSchemeTypeRoot.get(EmployeeLeaveSchemeType_.employeeLeaveSchemeTypeId), employeeLeaveSchemeTypeId));
		restriction = cb.and(restriction, cb.equal(empLeaveSchemeTypeRoot.get(EmployeeLeaveSchemeType_.companyId), companyId));
		restriction = cb.and(restriction, cb.equal(empLeaveSchemeEmpJoin.get(Employee_.employeeId), employeeId));

		criteriaQuery.where(restriction);

		TypedQuery<EmployeeLeaveSchemeType> typedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<EmployeeLeaveSchemeType> EmployeeLeaveSchemeList = typedQuery.getResultList();
		if (EmployeeLeaveSchemeList != null && !EmployeeLeaveSchemeList.isEmpty()) {
			return EmployeeLeaveSchemeList.get(0);
		}
		return null;

	}

	@Override
	public EmployeeLeaveSchemeType findSchemeTypeByCompanyId(Long employeeLeaveSchemeTypeId, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeLeaveSchemeType> criteriaQuery = cb.createQuery(EmployeeLeaveSchemeType.class);
		Root<EmployeeLeaveSchemeType> empLeaveSchemeTypeRoot = criteriaQuery.from(EmployeeLeaveSchemeType.class);
		criteriaQuery.select(empLeaveSchemeTypeRoot);
		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction,
				cb.equal(empLeaveSchemeTypeRoot.get(EmployeeLeaveSchemeType_.employeeLeaveSchemeTypeId), employeeLeaveSchemeTypeId));
		restriction = cb.and(restriction, cb.equal(empLeaveSchemeTypeRoot.get(EmployeeLeaveSchemeType_.companyId), companyId));
		criteriaQuery.where(restriction);

		TypedQuery<EmployeeLeaveSchemeType> typedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<EmployeeLeaveSchemeType> EmployeeLeaveSchemeList = typedQuery.getResultList();
		if (EmployeeLeaveSchemeList != null && !EmployeeLeaveSchemeList.isEmpty()) {
			return EmployeeLeaveSchemeList.get(0);
		}
		return null;
	}

}
