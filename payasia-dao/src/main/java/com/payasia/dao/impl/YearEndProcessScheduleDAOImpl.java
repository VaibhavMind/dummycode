package com.payasia.dao.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.log4j.Logger;
import org.hibernate.jdbc.Work;
import org.springframework.stereotype.Repository;

import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.YearEndProcessScheduleDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.YearEndProcessSchedule;
import com.payasia.dao.bean.YearEndProcessSchedule_;

@Repository
public class YearEndProcessScheduleDAOImpl extends BaseDAO implements YearEndProcessScheduleDAO {

	private static final Logger LOGGER = Logger.getLogger(YearEndProcessScheduleDAOImpl.class);

	@Override
	public void update(YearEndProcessSchedule yearEndProcessSchedule) {
		super.update(yearEndProcessSchedule);

	}

	@Override
	public void save(YearEndProcessSchedule yearEndProcessSchedule) {
		super.save(yearEndProcessSchedule);
	}

	@Override
	public void delete(YearEndProcessSchedule yearEndProcessSchedule) {
		super.delete(yearEndProcessSchedule);

	}

	@Override
	public YearEndProcessSchedule findByID(long yearEndProcessScheduleId) {
		return super.findById(YearEndProcessSchedule.class, yearEndProcessScheduleId);
	}

	@Override
	protected Object getBaseEntity() {
		YearEndProcessSchedule yearEndProcessSchedule = new YearEndProcessSchedule();
		return yearEndProcessSchedule;
	}

	@Override
	public List<YearEndProcessSchedule> findAll() {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<YearEndProcessSchedule> criteriaQuery = cb.createQuery(YearEndProcessSchedule.class);
		Root<YearEndProcessSchedule> yearEndProcessScheduleRoot = criteriaQuery.from(YearEndProcessSchedule.class);

		criteriaQuery.select(yearEndProcessScheduleRoot);

		TypedQuery<YearEndProcessSchedule> yearEndProcessScheduleTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<YearEndProcessSchedule> yearEndProcessScheduleList = yearEndProcessScheduleTypedQuery.getResultList();
		return yearEndProcessScheduleList;
	}

	@Override
	public YearEndProcessSchedule findByCompanyId(Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<YearEndProcessSchedule> criteriaQuery = cb.createQuery(YearEndProcessSchedule.class);
		Root<YearEndProcessSchedule> yearEndProcessScheduleRoot = criteriaQuery.from(YearEndProcessSchedule.class);
		Join<YearEndProcessSchedule, Company> cmpJoin = yearEndProcessScheduleRoot
				.join(YearEndProcessSchedule_.company);

		criteriaQuery.select(yearEndProcessScheduleRoot);
		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(cmpJoin.get(Company_.companyId), companyId));
		criteriaQuery.where(restriction);

		TypedQuery<YearEndProcessSchedule> yearEndProcessScheduleTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<YearEndProcessSchedule> yearEndProcessScheduleList = yearEndProcessScheduleTypedQuery.getResultList();
		if (!yearEndProcessScheduleList.isEmpty()) {
			return yearEndProcessScheduleList.get(0);
		}

		return null;
	}

	@Override
	public void callProcessYearEndRollOverProc(final Long companyId, final Integer year) {
		getSession().doWork(new Work() {

			@Override
			public void execute(Connection connection) throws SQLException {
				try {
					LOGGER.info("### Calling SP...");
					CallableStatement cstmt = null;
					cstmt = connection.prepareCall("{call Process_Year_End_Roll_Over (?,?)}");
					cstmt.setLong("@Company_ID", companyId);
					cstmt.setInt("@Year", year);
					cstmt.execute();
				} catch (SQLException e) {
					throw new PayAsiaSystemException(e.getMessage(), e);
				}

			}
		});

	}

	@Override
	public void callyearEndLeaveActivateProc(final Long companyId, final Integer year) {
		getSession().doWork(new Work() {

			@Override
			public void execute(Connection connection) throws SQLException {
				try {
					CallableStatement cstmt = null;
					cstmt = connection.prepareCall("{call Process_Year_End_Leave_Activate (?,?,?,?)}");
					cstmt.setLong("@Company_ID", companyId);
					cstmt.setInt("@Year", year);
					cstmt.registerOutParameter("@Status", java.sql.Types.BOOLEAN);
					cstmt.registerOutParameter("@Err_Msg", java.sql.Types.VARCHAR);
					cstmt.execute();
				} catch (SQLException e) {
					throw new PayAsiaSystemException(e.getMessage(), e);
				}

			}
		});

	}

}
