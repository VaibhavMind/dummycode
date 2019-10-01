package com.payasia.dao.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.jdbc.Work;
import org.springframework.stereotype.Repository;

import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.GrantEmployeeLeaveVO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.util.DateUtils;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.LeavePreferenceDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.LeaveApplication;
import com.payasia.dao.bean.LeaveApplicationWorkflow;
import com.payasia.dao.bean.LeaveApplicationWorkflow_;
import com.payasia.dao.bean.LeaveApplication_;
import com.payasia.dao.bean.LeavePreference;
import com.payasia.dao.bean.LeavePreference_;

@Repository
public class LeavePreferenceDAOImpl extends BaseDAO implements
		LeavePreferenceDAO {

	@Override
	protected Object getBaseEntity() {
		LeavePreference leavePreference = new LeavePreference();
		return leavePreference;
	}

	@Override
	public void update(LeavePreference leavePreference) {
		super.update(leavePreference);
	}

	@Override
	public void delete(LeavePreference leavePreference) {
		super.delete(leavePreference);
	}

	@Override
	public void save(LeavePreference leavePreference) {
		super.save(leavePreference);
	}

	@Override
	public LeavePreference findByID(Long leavePreferenceId) {
		return super.findById(LeavePreference.class, leavePreferenceId);
	}

	@Override
	public LeavePreference findByCompanyId(Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeavePreference> criteriaQuery = cb
				.createQuery(LeavePreference.class);
		Root<LeavePreference> leavePrefRoot = criteriaQuery
				.from(LeavePreference.class);

		criteriaQuery.select(leavePrefRoot);
		Join<LeavePreference, Company> companyJoin = leavePrefRoot
				.join(LeavePreference_.company);
		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction,
				cb.equal(companyJoin.get(Company_.companyId), companyId));
		criteriaQuery.where(restriction);
		TypedQuery<LeavePreference> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<LeavePreference> leaveApplicationList = typedQuery.getResultList();
		if (leaveApplicationList != null && !leaveApplicationList.isEmpty()) {
			return leaveApplicationList.get(0);
		}
		return null;
	}

	@Override
	public void callGrantEmployeeLeaveProc(
			final GrantEmployeeLeaveVO grantEmployeeLeaveVO) {

		getSession().doWork(new Work() {

			@Override
			public void execute(Connection connection) throws SQLException {
				try {
					CallableStatement cstmt = null;
					cstmt = connection
							.prepareCall("{call Grant_Employee_Leave (?,?,?,?,?,?,?,?)}");
					cstmt.setLong("@Company_ID",
							grantEmployeeLeaveVO.getCompanyId());
					cstmt.setNull("@Leave_Scheme_Type_ID_List",
							java.sql.Types.VARCHAR);
					cstmt.setNull("@Employee_ID_List", java.sql.Types.VARCHAR);
					cstmt.setTimestamp("@Current_Date",
							grantEmployeeLeaveVO.getCurrentDate());
					cstmt.setBoolean("@Is_Year_End_Process",
							grantEmployeeLeaveVO.getIsYearEndProcess());
					cstmt.setBoolean("@Is_New_Hires", true);
					cstmt.setString("@User_ID", UserContext.getUserId());
					cstmt.registerOutParameter("@Status",
							java.sql.Types.BOOLEAN);
					cstmt.execute();
				} catch (SQLException e) {
					throw new PayAsiaSystemException(e.getMessage(), e);
				}

			}
		});

	}

	@Override
	public void callForfeitProc(final Long companyId, final Date currentDate) {
		getSession().doWork(new Work() {

			@Override
			public void execute(Connection connection) throws SQLException {
				try {
					CallableStatement cstmt = null;
					cstmt = connection
							.prepareCall("{call Forfeit_Leave_Transaction (?,?)}");
					cstmt.setLong("@Company_ID", companyId);
					cstmt.setTimestamp("@Current_Date",
							DateUtils.convertDateToTimeStamp(currentDate));
					cstmt.execute();
				} catch (SQLException e) {
					throw new PayAsiaSystemException(e.getMessage(), e);
				}

			}
		});

	}

	@Override
	public LeaveApplicationWorkflow findWorkFlowByStatusAndLeaveAppId(long statusId, Long leaveApplicationId) {
		// TODO Auto-generated method stub
		CriteriaBuilder criteriaBuilder = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveApplicationWorkflow> criteriaQuery = criteriaBuilder.createQuery(LeaveApplicationWorkflow.class);
		Root<LeaveApplicationWorkflow> root = criteriaQuery.from(LeaveApplicationWorkflow.class);
		Join<LeaveApplicationWorkflow,LeaveApplication> item = root.join(LeaveApplicationWorkflow_.leaveApplication, JoinType.INNER);
		criteriaQuery.select(root);
		criteriaQuery.where(criteriaBuilder.equal(item.get(LeaveApplication_.leaveApplicationId),leaveApplicationId));
		TypedQuery<LeaveApplicationWorkflow> typeQuery = entityManagerFactory.createQuery(criteriaQuery);
		for(LeaveApplicationWorkflow leaveAppWork:typeQuery.getResultList())
		{
			if(leaveAppWork.getLeaveStatusMaster().getLeaveStatusID()==statusId)
			{
				return leaveAppWork;
			}
		}
		return null;
	}

}
