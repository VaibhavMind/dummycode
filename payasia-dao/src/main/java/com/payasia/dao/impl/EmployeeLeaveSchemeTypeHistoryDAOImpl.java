package com.payasia.dao.impl;

import java.lang.reflect.InvocationTargetException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.hibernate.jdbc.Work;
import org.springframework.stereotype.Repository;

import com.payasia.common.dto.DayWiseLeaveTranReportDTO;
import com.payasia.common.dto.EmployeeLeaveSchemeTypeHistoryDTO;
import com.payasia.common.dto.LeaveTranReportDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.NullAwareBeanUtilsBean;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.EmployeeLeaveSchemeTypeHistoryDAO;
import com.payasia.dao.bean.AppCodeMaster;
import com.payasia.dao.bean.AppCodeMaster_;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeLeaveScheme;
import com.payasia.dao.bean.EmployeeLeaveSchemeType;
import com.payasia.dao.bean.EmployeeLeaveSchemeTypeHistory;
import com.payasia.dao.bean.EmployeeLeaveSchemeTypeHistory_;
import com.payasia.dao.bean.EmployeeLeaveSchemeType_;
import com.payasia.dao.bean.EmployeeLeaveScheme_;
import com.payasia.dao.bean.Employee_;
import com.payasia.dao.bean.LeaveApplication;
import com.payasia.dao.bean.LeaveApplication_;
import com.payasia.dao.bean.LeaveScheme;
import com.payasia.dao.bean.LeaveSchemeType;
import com.payasia.dao.bean.LeaveSchemeType_;
import com.payasia.dao.bean.LeaveScheme_;
import com.payasia.dao.bean.LeaveStatusMaster;
import com.payasia.dao.bean.LeaveStatusMaster_;
import com.payasia.dao.bean.LeaveTypeMaster;
import com.payasia.dao.bean.LeaveTypeMaster_;

@Repository
public class EmployeeLeaveSchemeTypeHistoryDAOImpl extends BaseDAO implements EmployeeLeaveSchemeTypeHistoryDAO {

	@Override
	protected Object getBaseEntity() {
		EmployeeLeaveSchemeTypeHistory employeeLeaveSchemeTypeHistory = new EmployeeLeaveSchemeTypeHistory();
		return employeeLeaveSchemeTypeHistory;
	}

	@Override
	public List<EmployeeLeaveSchemeTypeHistory> findBySchemeType(long leaveSchemeTypeId, Long postLeaveTypeFilterId, String monthStartDate,
			String monthEndDate, String dateFormat, Long employeeId, PageRequest pageDTO, SortCondition sortDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeLeaveSchemeTypeHistory> criteriaQuery = cb.createQuery(EmployeeLeaveSchemeTypeHistory.class);
		Root<EmployeeLeaveSchemeTypeHistory> typeHistoryRoot = criteriaQuery.from(EmployeeLeaveSchemeTypeHistory.class);
		criteriaQuery.select(typeHistoryRoot);

		Join<EmployeeLeaveSchemeTypeHistory, EmployeeLeaveSchemeType> empLeaveSchemeTypeJoin = typeHistoryRoot
				.join(EmployeeLeaveSchemeTypeHistory_.employeeLeaveSchemeType);
		Join<EmployeeLeaveSchemeType, LeaveSchemeType> leaveSchemeTypeJoin = empLeaveSchemeTypeJoin.join(EmployeeLeaveSchemeType_.leaveSchemeType);
		Join<EmployeeLeaveSchemeTypeHistory, AppCodeMaster> appCodeJoin = typeHistoryRoot.join(EmployeeLeaveSchemeTypeHistory_.appCodeMaster);

		Join<EmployeeLeaveSchemeType, EmployeeLeaveScheme> empLeaveSchemeJoin = empLeaveSchemeTypeJoin.join(EmployeeLeaveSchemeType_.employeeLeaveScheme);
		Join<EmployeeLeaveScheme, Employee> empJoin = empLeaveSchemeJoin.join(EmployeeLeaveScheme_.employee);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(empJoin.get(Employee_.employeeId), employeeId));
		restriction = cb.and(restriction, cb.equal(leaveSchemeTypeJoin.get(LeaveSchemeType_.leaveSchemeTypeId), leaveSchemeTypeId));
		if (postLeaveTypeFilterId != 0) {
			restriction = cb.and(restriction, cb.equal(appCodeJoin.get(AppCodeMaster_.appCodeID), postLeaveTypeFilterId));
		}
		restriction = cb.and(restriction, cb.greaterThanOrEqualTo(typeHistoryRoot.get(EmployeeLeaveSchemeTypeHistory_.startDate),
				DateUtils.stringToTimestamp(monthStartDate, dateFormat)));
		restriction = cb.and(restriction, cb.lessThanOrEqualTo(typeHistoryRoot.get(EmployeeLeaveSchemeTypeHistory_.startDate),
				DateUtils.stringToTimestamp(monthEndDate, dateFormat)));

		criteriaQuery.where(restriction);

		TypedQuery<EmployeeLeaveSchemeTypeHistory> empTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			empTypedQuery.setFirstResult(getStartPosition(pageDTO));
			empTypedQuery.setMaxResults(pageDTO.getPageSize());
		}
		List<EmployeeLeaveSchemeTypeHistory> empLeaveSchemeHistoryList = empTypedQuery.getResultList();
		return empLeaveSchemeHistoryList;

	}

	@Override
	public Long getCountBySchemeType(Long leaveSchemeTypeId, Long postLeaveTypeFilterId, String monthStartDate, String monthEndDate, String dateFormat,
			Long employeeId, PageRequest pageDTO, SortCondition sortDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
		Root<EmployeeLeaveSchemeTypeHistory> typeHistoryRoot = criteriaQuery.from(EmployeeLeaveSchemeTypeHistory.class);
		criteriaQuery.select(cb.count(typeHistoryRoot));

		Join<EmployeeLeaveSchemeTypeHistory, EmployeeLeaveSchemeType> empLeaveSchemeTypeJoin = typeHistoryRoot
				.join(EmployeeLeaveSchemeTypeHistory_.employeeLeaveSchemeType);
		Join<EmployeeLeaveSchemeType, LeaveSchemeType> leaveSchemeTypeJoin = empLeaveSchemeTypeJoin.join(EmployeeLeaveSchemeType_.leaveSchemeType);
		Join<EmployeeLeaveSchemeTypeHistory, AppCodeMaster> appCodeJoin = typeHistoryRoot.join(EmployeeLeaveSchemeTypeHistory_.appCodeMaster);

		Join<EmployeeLeaveSchemeType, EmployeeLeaveScheme> empLeaveSchemeJoin = empLeaveSchemeTypeJoin.join(EmployeeLeaveSchemeType_.employeeLeaveScheme);
		Join<EmployeeLeaveScheme, Employee> empJoin = empLeaveSchemeJoin.join(EmployeeLeaveScheme_.employee);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(empJoin.get(Employee_.employeeId), employeeId));
		restriction = cb.and(restriction, cb.equal(leaveSchemeTypeJoin.get(LeaveSchemeType_.leaveSchemeTypeId), leaveSchemeTypeId));
		if (postLeaveTypeFilterId != 0) {
			restriction = cb.and(restriction, cb.equal(appCodeJoin.get(AppCodeMaster_.appCodeID), postLeaveTypeFilterId));
		}

		restriction = cb.and(restriction, cb.greaterThanOrEqualTo(typeHistoryRoot.get(EmployeeLeaveSchemeTypeHistory_.startDate),
				DateUtils.stringToTimestamp(monthStartDate, dateFormat)));
		restriction = cb.and(restriction, cb.lessThanOrEqualTo(typeHistoryRoot.get(EmployeeLeaveSchemeTypeHistory_.startDate),
				DateUtils.stringToTimestamp(monthEndDate, dateFormat)));
		criteriaQuery.where(restriction);

		TypedQuery<Long> empTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		return empTypedQuery.getSingleResult();
	}

	@Override
	public List<EmployeeLeaveSchemeTypeHistory> getLeaveTransactionreport(Long companyId, String startDate, String endDate, Long leaveTransactionId,
			Long leaveTypeId, String dateFormat, List<Long> empIdsList, Long leaveStatusId, Boolean isManager) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeLeaveSchemeTypeHistory> criteriaQuery = cb.createQuery(EmployeeLeaveSchemeTypeHistory.class);
		Root<EmployeeLeaveSchemeTypeHistory> typeHistoryRoot = criteriaQuery.from(EmployeeLeaveSchemeTypeHistory.class);
		criteriaQuery.select(typeHistoryRoot);

		Join<EmployeeLeaveSchemeTypeHistory, EmployeeLeaveSchemeType> empLeaveSchemeTypeJoin = typeHistoryRoot
				.join(EmployeeLeaveSchemeTypeHistory_.employeeLeaveSchemeType);
		Join<EmployeeLeaveSchemeType, EmployeeLeaveScheme> employeeLeaveSchemeJoin = empLeaveSchemeTypeJoin
				.join(EmployeeLeaveSchemeType_.employeeLeaveScheme);
		Join<EmployeeLeaveScheme, LeaveScheme> leaveSJoin = employeeLeaveSchemeJoin.join(EmployeeLeaveScheme_.leaveScheme);
		Join<EmployeeLeaveScheme, Employee> employeeJoin = employeeLeaveSchemeJoin.join(EmployeeLeaveScheme_.employee);
		Join<LeaveScheme, Company> compJoin = leaveSJoin.join(LeaveScheme_.company);
		Join<EmployeeLeaveSchemeTypeHistory, AppCodeMaster> appCodeJoin = typeHistoryRoot.join(EmployeeLeaveSchemeTypeHistory_.appCodeMaster);

		Join<EmployeeLeaveSchemeType, LeaveSchemeType> leaveSchemeTypeJoin = empLeaveSchemeTypeJoin.join(EmployeeLeaveSchemeType_.leaveSchemeType);

		Predicate restriction = cb.conjunction();

		if (leaveTypeId != 0) {
			Join<LeaveSchemeType, LeaveTypeMaster> leaveTypeJoin = leaveSchemeTypeJoin.join(LeaveSchemeType_.leaveTypeMaster);
			restriction = cb.and(restriction, cb.equal(leaveTypeJoin.get(LeaveTypeMaster_.leaveTypeId), leaveTypeId));
		}

		restriction = cb.and(restriction, cb.equal(appCodeJoin.get(AppCodeMaster_.appCodeID), leaveTransactionId));

		restriction = cb.and(restriction, cb.equal(compJoin.get(Company_.companyId), companyId));

		restriction = cb.and(restriction, cb.between(typeHistoryRoot.get(EmployeeLeaveSchemeTypeHistory_.startDate),
				DateUtils.stringToTimestamp(startDate, dateFormat), DateUtils.stringToTimestamp(endDate, dateFormat)));
		if (isManager) {
			if (!empIdsList.isEmpty()) {
				restriction = cb.and(restriction, employeeJoin.get(Employee_.employeeId).in(empIdsList));
			} else {
				restriction = cb.and(restriction, employeeJoin.get(Employee_.employeeId).in(-1));
			}
		}

		if (leaveStatusId != null) {
			Join<EmployeeLeaveSchemeTypeHistory, LeaveStatusMaster> leaveStatusJoin = typeHistoryRoot
					.join(EmployeeLeaveSchemeTypeHistory_.leaveStatusMaster);
			Join<EmployeeLeaveSchemeTypeHistory, LeaveApplication> leaveAppJoin = typeHistoryRoot.join(EmployeeLeaveSchemeTypeHistory_.leaveApplication);
			Join<LeaveApplication, LeaveStatusMaster> leaveAppStatusJoin = leaveAppJoin.join(LeaveApplication_.leaveStatusMaster);
			restriction = cb.and(restriction, cb.equal(leaveStatusJoin.get(LeaveStatusMaster_.leaveStatusID), leaveStatusId));
			restriction = cb.and(restriction, cb.equal(leaveAppStatusJoin.get(LeaveStatusMaster_.leaveStatusID), leaveStatusId));
		}
		criteriaQuery.where(restriction);

		TypedQuery<EmployeeLeaveSchemeTypeHistory> empTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		List<EmployeeLeaveSchemeTypeHistory> empLeaveSchemeHistoryList = empTypedQuery.getResultList();
		return empLeaveSchemeHistoryList;
	}

	@Override
	public List<EmployeeLeaveSchemeTypeHistoryDTO> getEmployeeLeaveTransDetailProc(final Long employeeId, final Long leaveSchemeTypeId, final Integer year,
			final String dateFormat, final Long loggedInUser, final Timestamp leaveCalendarStartDate, final Timestamp leaveCalendarEndDate) {
		final List<EmployeeLeaveSchemeTypeHistoryDTO> employeeLeaveSchemeTypeDTOList = new ArrayList<>();

		getSession().doWork(new Work() {

			@Override
			public void execute(Connection connection) throws SQLException {
				try {
					CallableStatement cstmt = null;
					cstmt = connection.prepareCall("{call Get_Employee_Leave_Transaction_Detail (?,?,?,?,?,?)}");

					cstmt.setLong("@Employee_ID", employeeId);
					cstmt.setLong("@Leave_Scheme_Type_ID", leaveSchemeTypeId);
					cstmt.setInt("@Year", year);
					cstmt.setLong("@User_ID", loggedInUser);
					cstmt.setTimestamp("@Leave_Calendar_Start_Date", leaveCalendarStartDate);
					cstmt.setTimestamp("@Leave_Calendar_End_Date", leaveCalendarEndDate);

					ResultSet rs = cstmt.executeQuery();
					if (rs != null) {
						while (rs.next()) {
							EmployeeLeaveSchemeTypeHistoryDTO employeeLeaveSchemeTypeDTO = new EmployeeLeaveSchemeTypeHistoryDTO();
							employeeLeaveSchemeTypeDTO.setEmployeeLeaveSchemeTypeHistoryId(rs.getLong("Employee_Leave_Scheme_Type_History_ID"));
							employeeLeaveSchemeTypeDTO.setLeaveApplicationId(rs.getLong("Leave_Application_ID"));
							if (rs.getLong("Employee_Leave_Scheme_Type_History_ID") != 0) {
								employeeLeaveSchemeTypeDTO.setTransactionRowId("ELSTH_" + rs.getLong("Employee_Leave_Scheme_Type_History_ID"));
							}
							if (rs.getLong("Leave_Application_ID") != 0) {
								employeeLeaveSchemeTypeDTO.setTransactionRowId("LA_" + rs.getLong("Leave_Application_ID"));
							}

							employeeLeaveSchemeTypeDTO.setStartDateT(rs.getTimestamp("Start_Date"));

							employeeLeaveSchemeTypeDTO.setEndDateT(rs.getTimestamp("End_Date"));

							employeeLeaveSchemeTypeDTO.setType(rs.getString("Transaction_Type"));
							employeeLeaveSchemeTypeDTO.setTypeName(rs.getString("Transaction_Type"));
							employeeLeaveSchemeTypeDTO.setCreatedDate(DateUtils.timeStampToStringWOTimezone(rs.getTimestamp("Posted_Date"), dateFormat));
							employeeLeaveSchemeTypeDTO.setReason(rs.getString("Reason"));
							employeeLeaveSchemeTypeDTO.setDays(rs.getBigDecimal("Days"));
							employeeLeaveSchemeTypeDTO.setIsAdmin(rs.getBoolean("Is_Admin"));
							employeeLeaveSchemeTypeDTO.setFromSessionLabelKey(rs.getString("Start_Session_Label_Key"));
							employeeLeaveSchemeTypeDTO.setToSessionLabelKey(rs.getString("End_Session_Label_Key"));
							employeeLeaveSchemeTypeDTOList.add(employeeLeaveSchemeTypeDTO);
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
	public void update(EmployeeLeaveSchemeTypeHistory employeeLeaveSchemeTypeHistory) {
		super.update(employeeLeaveSchemeTypeHistory);
	}

	@Override
	public void save(EmployeeLeaveSchemeTypeHistory employeeLeaveSchemeTypeHistory) {
		super.save(employeeLeaveSchemeTypeHistory);

	}

	@Override
	public EmployeeLeaveSchemeTypeHistory saveReturn(EmployeeLeaveSchemeTypeHistory employeeLeaveSchemeTypeHistory) {
		EmployeeLeaveSchemeTypeHistory persistObj = employeeLeaveSchemeTypeHistory;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (EmployeeLeaveSchemeTypeHistory) getBaseEntity();
			beanUtil.copyProperties(persistObj, employeeLeaveSchemeTypeHistory);
		} catch (IllegalAccessException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		} catch (InvocationTargetException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		}
		this.entityManagerFactory.persist(persistObj);
		return persistObj;
	}

	@Override
	public void delete(EmployeeLeaveSchemeTypeHistory employeeLeaveSchemeTypeHistory) {
		super.delete(employeeLeaveSchemeTypeHistory);
	}

	@Override
	public EmployeeLeaveSchemeTypeHistory findById(long employeeLeaveSchemeTypeHistoryId) {

		EmployeeLeaveSchemeTypeHistory employeeLeaveSchemeTypeHistory = super.findById(EmployeeLeaveSchemeTypeHistory.class,
				employeeLeaveSchemeTypeHistoryId);
		return employeeLeaveSchemeTypeHistory;
	}

	@Override
	public List<Integer> getYearList(Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Integer> criteriaQuery = cb.createQuery(Integer.class);
		Root<EmployeeLeaveSchemeTypeHistory> eLSTHRoot = criteriaQuery.from(EmployeeLeaveSchemeTypeHistory.class);
		criteriaQuery.select(cb.function("year", Integer.class, eLSTHRoot.get(EmployeeLeaveSchemeTypeHistory_.startDate))).distinct(true);

		Join<EmployeeLeaveSchemeTypeHistory, EmployeeLeaveSchemeType> employeeLeaveSchemeTypeJoin = eLSTHRoot
				.join(EmployeeLeaveSchemeTypeHistory_.employeeLeaveSchemeType);
		Join<EmployeeLeaveSchemeType, EmployeeLeaveScheme> employeeLeaveSchemeJoin = employeeLeaveSchemeTypeJoin
				.join(EmployeeLeaveSchemeType_.employeeLeaveScheme);
		Join<EmployeeLeaveScheme, LeaveScheme> leaveSchemJoin = employeeLeaveSchemeJoin.join(EmployeeLeaveScheme_.leaveScheme);
		Join<LeaveScheme, Company> companyJoin = leaveSchemJoin.join(LeaveScheme_.company);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal((companyJoin.get(Company_.companyId)), companyId));

		criteriaQuery.where(restriction);
		TypedQuery<Integer> yearTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<Integer> yearList = yearTypedQuery.getResultList();
		return yearList;
	}

	@Override
	public List<LeaveTranReportDTO> getLeaveTransactionReportProc(final Long companyId, final String employeeIdList, final String fromdate,
			final String todate, final String leaveTypeIdList, final String leaveTransactionList, final boolean multipleRecord,
			final boolean includeApprovalCancel, final boolean isIncludeResignedEmployees, final String dateFormat, Boolean isManager,
			final Long leaveReviewerId, final boolean leavePreferencePreApproval, final boolean leaveExtensionPreference) {
		final List<LeaveTranReportDTO> leaveTranReportDTOList = new ArrayList<>();

		getSession().doWork(new Work() {
			@Override
			public void execute(Connection connection) throws SQLException {
				try {
					CallableStatement cstmt = null;
					cstmt = connection.prepareCall("{call Leave_Transaction_Report (?,?,?,?,?,?,?,?,?,?)}");


					cstmt.setLong("@Company_ID", companyId);
					cstmt.setString("@Employee_ID_List", employeeIdList);
					cstmt.setTimestamp("@From_Date", DateUtils.stringToTimestamp(fromdate));
					cstmt.setTimestamp("@To_Date", DateUtils.stringToTimestamp(todate));
					cstmt.setString("@Leave_Type_ID", leaveTypeIdList);
					cstmt.setString("@Leave_Transaction_Type", leaveTransactionList);
					cstmt.setBoolean("@Multiple_Record", multipleRecord);
					cstmt.setBoolean("@Include_Approved_Cancel", includeApprovalCancel);
					cstmt.setBoolean("@Include_Resigned_Employee", isIncludeResignedEmployees);
					if (leaveReviewerId == null) {
						cstmt.setNull("@Leave_Reviewer_ID", java.sql.Types.BIGINT);
					} else {
						cstmt.setLong("@Leave_Reviewer_ID", leaveReviewerId);
					}
					ResultSet rs = cstmt.executeQuery();
					if (rs != null) {
						while (rs.next()) {
							LeaveTranReportDTO leaveTranReportDTO = new LeaveTranReportDTO();
							SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
							leaveTranReportDTO.setEmployeeId(rs.getLong("Employee_ID"));
							leaveTranReportDTO.setEmployeeNo(rs.getString("Employee_Number"));
							leaveTranReportDTO.setEmployeeName(rs.getString("Employee_Name"));

							if ((Long) rs.getLong("Leave_Application_Id") != null && rs.getLong("Leave_Application_Id")!=0) {
								leaveTranReportDTO.setLeaveApplicationId(rs.getLong("Leave_Application_Id"));

								
							}
							if(rs.getString("Leave_Transaction_Status") != null){

								leaveTranReportDTO.setLeaveTransactionType(rs.getString("Leave_Transaction_Status"));	
							}

							if (leavePreferencePreApproval) {
								if (rs.getInt("Pre_Approval_Request") == 1) {
									leaveTranReportDTO.setPreApproval("Yes");
								} else {
									leaveTranReportDTO.setPreApproval("No");
								}
							}

							if (leaveExtensionPreference) {
								if (rs.getInt("Leave_Extension") == 1) {
									leaveTranReportDTO.setLeaveExtension("Yes");
								} else {
									leaveTranReportDTO.setLeaveExtension("No");
								}
							}

							if (multipleRecord) {
								leaveTranReportDTO.setLeaveDate(formatter.format(rs.getDate("Leave_Date")));
								leaveTranReportDTO.setLeaveDuration(rs.getBigDecimal("Leave_Duration"));
								leaveTranReportDTO.setLeaveApplyType(rs.getString("Leave_Apply_Type"));
								leaveTranReportDTO.setLeaveTypeName(rs.getString("Leave_Type_Code"));

							} else {
								leaveTranReportDTO.setLeaveTypeName(rs.getString("Leave_Type_Name"));
								leaveTranReportDTO.setFromDate(formatter.format(rs.getDate("From_Date")));
								leaveTranReportDTO.setToDate(formatter.format(rs.getDate("To_Date")));
								if (rs.getDate("Posted_Date") != null) {
									leaveTranReportDTO.setPostedDate(formatter.format(rs.getDate("Posted_Date")));
								}

								if (rs.getDate("Approved_Date") != null) {
									leaveTranReportDTO.setApprovedDate(formatter.format(rs.getDate("Approved_Date")));
								}
								leaveTranReportDTO.setLeaveSchemeName(rs.getString("Leave_Scheme_Name"));

								leaveTranReportDTO.setDays(rs.getBigDecimal("Days"));
								leaveTranReportDTO.setRemarks(rs.getString("Remarks"));
								if (StringUtils.isNotBlank(rs.getString("From_Session")) && rs.getString("From_Session").equalsIgnoreCase("1")) {
									leaveTranReportDTO.setSession1("Session 1");
								}
								if (StringUtils.isNotBlank(rs.getString("From_Session")) && rs.getString("From_Session").equalsIgnoreCase("2")) {
									leaveTranReportDTO.setSession1("Session 2");
								}
								if (StringUtils.isNotBlank(rs.getString("To_Session")) && rs.getString("To_Session").equalsIgnoreCase("1")) {
									leaveTranReportDTO.setSession2("Session 1");
								}
								if (StringUtils.isNotBlank(rs.getString("To_Session")) && rs.getString("To_Session").equalsIgnoreCase("2")) {
									leaveTranReportDTO.setSession2("Session 2");
								}

							}

							leaveTranReportDTOList.add(leaveTranReportDTO);

						}
					}
				} catch (SQLException e) {
					throw new PayAsiaSystemException(e.getMessage(), e);
				}

			}
		});

		return leaveTranReportDTOList;
	}

	@Override
	public List<DayWiseLeaveTranReportDTO> getDayWiseLeaveTransactionReportProc(final Long companyId, final String employeeIdList, final String fromdate,
			final String todate, final String leaveTypeList, final String leaveTransactionList, final boolean multipleRecord,
			final boolean includeApprovalCancel, final boolean isIncludeResignedEmployees, final String dateFormat, Boolean isManager,
			final Long leaveReviewerId, final boolean leavePreferencePreApproval, final boolean leaveExtensionPreference) {
		final List<DayWiseLeaveTranReportDTO> leaveTranReportDTOList = new ArrayList<>();

		getSession().doWork(new Work() {
			@Override
			public void execute(Connection connection) throws SQLException {
				try {
					CallableStatement cstmt = null;
					cstmt = connection.prepareCall("{call Leave_Transaction_Report (?,?,?,?,?,?,?,?,?,?)}");

					cstmt.setLong("@Company_ID", companyId);
					cstmt.setString("@Employee_ID_List", employeeIdList);
					cstmt.setTimestamp("@From_Date", DateUtils.stringToTimestamp(fromdate));
					cstmt.setTimestamp("@To_Date", DateUtils.stringToTimestamp(todate));
					cstmt.setString("@Leave_Type_ID", leaveTypeList);
					cstmt.setString("@Leave_Transaction_Type", leaveTransactionList);
					cstmt.setBoolean("@Multiple_Record", multipleRecord);
					cstmt.setBoolean("@Include_Approved_Cancel", includeApprovalCancel);
					cstmt.setBoolean("@Include_Resigned_Employee", isIncludeResignedEmployees);
					if (leaveReviewerId == null) {
						cstmt.setNull("@Leave_Reviewer_ID", java.sql.Types.BIGINT);
					} else {
						cstmt.setLong("@Leave_Reviewer_ID", leaveReviewerId);
					}

					ResultSet rs = cstmt.executeQuery();
					if (rs != null) {
						while (rs.next()) {
							DayWiseLeaveTranReportDTO leaveTranReportDTO = new DayWiseLeaveTranReportDTO();
							SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
							leaveTranReportDTO.setEmployeeId(rs.getLong("Employee_ID"));
							leaveTranReportDTO.setEmployeeNo(rs.getString("Employee_Number"));
							leaveTranReportDTO.setEmployeeName(rs.getString("Employee_Name"));
							leaveTranReportDTO.setLeaveTypeName(rs.getString("Leave_Type_Name"));
							if ((Long) rs.getLong("Leave_Application_Id") != null) {
								leaveTranReportDTO.setLeaveApplicationId(rs.getLong("Leave_Application_Id"));
							}
							if (leavePreferencePreApproval) {
								if (rs.getInt("Pre_Approval_Request") == 1) {
									leaveTranReportDTO.setPreApproved("Yes");
								} else {
									leaveTranReportDTO.setPreApproved("No");
								}
							}
							if (leaveExtensionPreference) {
								if (rs.getInt("Leave_Extension") == 1) {
									leaveTranReportDTO.setLeaveExtension("Yes");
								} else {
									leaveTranReportDTO.setLeaveExtension("No");
								}
							}

							leaveTranReportDTO.setFromDate(formatter.format(rs.getDate("From_Date")));
							leaveTranReportDTO.setToDate(formatter.format(rs.getDate("To_Date")));
							if ((Long) rs.getLong("From_Session") != null) {
								leaveTranReportDTO.setFromSession(rs.getLong("From_Session"));
							}
							if ((Long) rs.getLong("To_Session") != null) {
								leaveTranReportDTO.setToSession(rs.getLong("To_Session"));
							}

							leaveTranReportDTO.setRemarks(rs.getString("Remarks"));
							leaveTranReportDTO.setLeaveDate(formatter.format(rs.getDate("Leave_Date")));
							leaveTranReportDTO.setLeaveDuration(rs.getBigDecimal("Leave_Duration"));

							leaveTranReportDTOList.add(leaveTranReportDTO);

						}
					}
				} catch (SQLException e) {
					throw new PayAsiaSystemException(e.getMessage(), e);
				}

			}
		});

		return leaveTranReportDTOList;
	}

	@Override
	public void deleteByCondition(Long employeeLeaveSchemeTypeId, Long transactionTypeId, Long leaveApplicationId) {

		String queryString = "DELETE FROM EmployeeLeaveSchemeTypeHistory elsth WHERE elsth.employeeLeaveSchemeType.employeeLeaveSchemeTypeId = :employeeLeaveSchemeTypeId AND elsth.leaveApplication.leaveApplicationId = :leaveApplicationId AND elsth.appCodeMaster.appCodeID = :appCodeID";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("employeeLeaveSchemeTypeId", employeeLeaveSchemeTypeId);
		q.setParameter("leaveApplicationId", leaveApplicationId);
		q.setParameter("appCodeID", transactionTypeId);

		q.executeUpdate();

	}

	@Override
	public EmployeeLeaveSchemeTypeHistory findByCondition(Long employeeLeaveSchemeTypeId, Long transactionTypeId, Long leaveApplicationId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeLeaveSchemeTypeHistory> criteriaQuery = cb.createQuery(EmployeeLeaveSchemeTypeHistory.class);
		Root<EmployeeLeaveSchemeTypeHistory> typeHistoryRoot = criteriaQuery.from(EmployeeLeaveSchemeTypeHistory.class);
		criteriaQuery.select(typeHistoryRoot);

		Join<EmployeeLeaveSchemeTypeHistory, EmployeeLeaveSchemeType> empLeaveSchemeTypeJoin = typeHistoryRoot
				.join(EmployeeLeaveSchemeTypeHistory_.employeeLeaveSchemeType);

		Join<EmployeeLeaveSchemeTypeHistory, AppCodeMaster> appCodeJoin = typeHistoryRoot.join(EmployeeLeaveSchemeTypeHistory_.appCodeMaster);
		Join<EmployeeLeaveSchemeTypeHistory, LeaveApplication> leaveAppJoin = typeHistoryRoot.join(EmployeeLeaveSchemeTypeHistory_.leaveApplication);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(leaveAppJoin.get(LeaveApplication_.leaveApplicationId), leaveApplicationId));
		restriction = cb.and(restriction,
				cb.equal(empLeaveSchemeTypeJoin.get(EmployeeLeaveSchemeType_.employeeLeaveSchemeTypeId), employeeLeaveSchemeTypeId));
		restriction = cb.and(restriction, cb.equal(appCodeJoin.get(AppCodeMaster_.appCodeID), transactionTypeId));

		criteriaQuery.where(restriction);

		TypedQuery<EmployeeLeaveSchemeTypeHistory> empTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		List<EmployeeLeaveSchemeTypeHistory> empLeaveSchemeHistoryList = empTypedQuery.getResultList();
		if (empLeaveSchemeHistoryList != null && !empLeaveSchemeHistoryList.isEmpty()) {
			return empLeaveSchemeHistoryList.get(0);
		}
		return null;

	}

	@Override
	public EmployeeLeaveSchemeTypeHistory findByCondition(Long employeeLeaveSchemeTypeId, Long transactionTypeId, Timestamp startDate, Timestamp endDate) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeLeaveSchemeTypeHistory> criteriaQuery = cb.createQuery(EmployeeLeaveSchemeTypeHistory.class);
		Root<EmployeeLeaveSchemeTypeHistory> typeHistoryRoot = criteriaQuery.from(EmployeeLeaveSchemeTypeHistory.class);
		criteriaQuery.select(typeHistoryRoot);

		Join<EmployeeLeaveSchemeTypeHistory, EmployeeLeaveSchemeType> empLeaveSchemeTypeJoin = typeHistoryRoot
				.join(EmployeeLeaveSchemeTypeHistory_.employeeLeaveSchemeType);

		Join<EmployeeLeaveSchemeTypeHistory, AppCodeMaster> appCodeJoin = typeHistoryRoot.join(EmployeeLeaveSchemeTypeHistory_.appCodeMaster);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(empLeaveSchemeTypeJoin.get(EmployeeLeaveSchemeType_.employeeLeaveSchemeTypeId), employeeLeaveSchemeTypeId));
		restriction = cb.and(restriction, cb.equal(appCodeJoin.get(AppCodeMaster_.appCodeID), transactionTypeId));

		restriction = cb.and(restriction, cb.equal(typeHistoryRoot.get(EmployeeLeaveSchemeTypeHistory_.startDate), startDate));
		restriction = cb.and(restriction, cb.equal(typeHistoryRoot.get(EmployeeLeaveSchemeTypeHistory_.endDate), endDate));

		criteriaQuery.where(restriction);

		TypedQuery<EmployeeLeaveSchemeTypeHistory> empTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		List<EmployeeLeaveSchemeTypeHistory> empLeaveSchemeHistoryList = empTypedQuery.getResultList();
		if (empLeaveSchemeHistoryList != null && !empLeaveSchemeHistoryList.isEmpty()) {
			return empLeaveSchemeHistoryList.get(0);
		}
		return null;

	}

	@Override
	public LeaveApplication findByLeaveApplicationId(Long leaveApplicationId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveApplication> criteriaQuery = cb.createQuery(LeaveApplication.class);
		Root<LeaveApplication> root = criteriaQuery.from(LeaveApplication.class);
		criteriaQuery.select(root);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(root.get(LeaveApplication_.leaveApplicationId), leaveApplicationId));
		criteriaQuery.where(restriction);

		TypedQuery<LeaveApplication> empTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		List<LeaveApplication> leaveApplicationExtensionDetailsList = empTypedQuery.getResultList();
		if (leaveApplicationExtensionDetailsList != null && !leaveApplicationExtensionDetailsList.isEmpty()) {
			return leaveApplicationExtensionDetailsList.get(0);
		}
		return null;

	}

	@Override
	public EmployeeLeaveSchemeTypeHistory findLeaveTransByCompanyId(Long leaveTranId, Long companyId) {
		
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeLeaveSchemeTypeHistory> criteriaQuery = cb.createQuery(EmployeeLeaveSchemeTypeHistory.class);
		Root<EmployeeLeaveSchemeTypeHistory> typeHistoryRoot = criteriaQuery.from(EmployeeLeaveSchemeTypeHistory.class);
		criteriaQuery.select(typeHistoryRoot);

		Join<EmployeeLeaveSchemeTypeHistory, LeaveApplication> empLeaveSchemeTypeJoin = typeHistoryRoot.join(EmployeeLeaveSchemeTypeHistory_.leaveApplication);
		Join<LeaveApplication, Company> empLeaveCompJoin = empLeaveSchemeTypeJoin.join(LeaveApplication_.company);
		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(empLeaveCompJoin.get(Company_.companyId), companyId));
		restriction = cb.and(restriction, cb.equal(typeHistoryRoot.get(EmployeeLeaveSchemeTypeHistory_.employeeLeaveSchemeTypeHistoryId), leaveTranId));
		criteriaQuery.where(restriction);

		TypedQuery<EmployeeLeaveSchemeTypeHistory> empTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		List<EmployeeLeaveSchemeTypeHistory> empLeaveSchemeHistoryList = empTypedQuery.getResultList();
		if (empLeaveSchemeHistoryList != null && !empLeaveSchemeHistoryList.isEmpty()) {
			return empLeaveSchemeHistoryList.get(0);
		}
		return null;
	}

	@Override
	public EmployeeLeaveSchemeTypeHistory findHistoryByCompanyId(Long historyId, Long companyId) {
		
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeLeaveSchemeTypeHistory> criteriaQuery = cb.createQuery(EmployeeLeaveSchemeTypeHistory.class);
		Root<EmployeeLeaveSchemeTypeHistory> typeHistoryRoot = criteriaQuery.from(EmployeeLeaveSchemeTypeHistory.class);
		criteriaQuery.select(typeHistoryRoot);

		Join<EmployeeLeaveSchemeTypeHistory, LeaveApplication> empLeaveSchemeTypeJoin = typeHistoryRoot.join(EmployeeLeaveSchemeTypeHistory_.leaveApplication);
		Join<LeaveApplication, Company> empLeaveCompJoin = empLeaveSchemeTypeJoin.join(LeaveApplication_.company);
		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(empLeaveCompJoin.get(Company_.companyId), companyId));
		restriction = cb.and(restriction, cb.equal(typeHistoryRoot.get(EmployeeLeaveSchemeTypeHistory_.employeeLeaveSchemeTypeHistoryId), historyId));
		criteriaQuery.where(restriction);

		TypedQuery<EmployeeLeaveSchemeTypeHistory> empTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		List<EmployeeLeaveSchemeTypeHistory> empLeaveSchemeHistoryList = empTypedQuery.getResultList();
		if (empLeaveSchemeHistoryList != null && !empLeaveSchemeHistoryList.isEmpty()) {
			return empLeaveSchemeHistoryList.get(0);
		}
		return null;
	}
	
	/**
	 * @param : This method is used to get EmployeeLeaveSchemeTypeHistory object of credited type transaction.
	 * */
	
	@Override
	public EmployeeLeaveSchemeTypeHistory findEmployeeLeaveSchemeCCLCreditObj(Long employeeLeaveSchemeTypeId, Long transactionTypeId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeLeaveSchemeTypeHistory> criteriaQuery = cb.createQuery(EmployeeLeaveSchemeTypeHistory.class);
		Root<EmployeeLeaveSchemeTypeHistory> typeHistoryRoot = criteriaQuery.from(EmployeeLeaveSchemeTypeHistory.class);
		criteriaQuery.select(typeHistoryRoot);

		Join<EmployeeLeaveSchemeTypeHistory, EmployeeLeaveSchemeType> empLeaveSchemeTypeJoin = typeHistoryRoot
				.join(EmployeeLeaveSchemeTypeHistory_.employeeLeaveSchemeType);

		Join<EmployeeLeaveSchemeTypeHistory, AppCodeMaster> appCodeJoin = typeHistoryRoot.join(EmployeeLeaveSchemeTypeHistory_.appCodeMaster);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(empLeaveSchemeTypeJoin.get(EmployeeLeaveSchemeType_.employeeLeaveSchemeTypeId), employeeLeaveSchemeTypeId));
		restriction = cb.and(restriction, cb.equal(appCodeJoin.get(AppCodeMaster_.appCodeID), transactionTypeId));

		criteriaQuery.where(restriction);

		TypedQuery<EmployeeLeaveSchemeTypeHistory> empTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		List<EmployeeLeaveSchemeTypeHistory> empLeaveSchemeHistoryList = empTypedQuery.getResultList();
		if (empLeaveSchemeHistoryList != null && !empLeaveSchemeHistoryList.isEmpty()) {
			return empLeaveSchemeHistoryList.get(0);
		}
		return null;

	}
	
	@Override
	public void deleteByCondition(Long employeeLeaveSchemeTypeId, Long transactionTypeId) {

		String queryString = "DELETE FROM EmployeeLeaveSchemeTypeHistory elsth WHERE elsth.employeeLeaveSchemeType.employeeLeaveSchemeTypeId = :employeeLeaveSchemeTypeId AND  elsth.appCodeMaster.appCodeID = :appCodeID";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("employeeLeaveSchemeTypeId", employeeLeaveSchemeTypeId);
		q.setParameter("appCodeID", transactionTypeId);

		q.executeUpdate();

	}
	
	

}
