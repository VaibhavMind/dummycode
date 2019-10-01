package com.payasia.dao.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.SortConstants;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.LeaveGrantBatchDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.LeaveGrantBatch;
import com.payasia.dao.bean.LeaveGrantBatchDetail;
import com.payasia.dao.bean.LeaveGrantBatchDetail_;
import com.payasia.dao.bean.LeaveGrantBatch_;
import com.payasia.dao.bean.LeaveScheme;
import com.payasia.dao.bean.LeaveSchemeType;
import com.payasia.dao.bean.LeaveSchemeType_;
import com.payasia.dao.bean.LeaveTypeMaster;
import com.payasia.dao.bean.LeaveTypeMaster_;

@Repository
public class LeaveGrantBatchDAOImpl extends BaseDAO implements
		LeaveGrantBatchDAO {

	@Override
	protected Object getBaseEntity() {
		LeaveGrantBatch leaveGrantBatch = new LeaveGrantBatch();
		return leaveGrantBatch;
	}

	@Override
	public void update(LeaveGrantBatch leaveGrantBatch) {
		super.update(leaveGrantBatch);
	}

	@Override
	public void delete(LeaveGrantBatch leaveGrantBatch) {
		super.delete(leaveGrantBatch);
	}

	@Override
	public void save(LeaveGrantBatch leaveGrantBatch) {
		super.save(leaveGrantBatch);
	}

	@Override
	public LeaveGrantBatch findByID(Long leaveGrantBatchId) {
		return super.findById(LeaveGrantBatch.class, leaveGrantBatchId);
	}

	@Override
	public List<LeaveGrantBatch> findByCondition(Long companyId,
			String fromDate, String toDate, Long leaveType,
			PageRequest pageDTO, SortCondition sortDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveGrantBatch> criteriaQuery = cb
				.createQuery(LeaveGrantBatch.class);
		Root<LeaveGrantBatch> leaveGrantRoot = criteriaQuery
				.from(LeaveGrantBatch.class);
		criteriaQuery.select(leaveGrantRoot);

		Predicate restriction = cb.conjunction();

		Join<LeaveGrantBatch, Company> companyJoin = leaveGrantRoot
				.join(LeaveGrantBatch_.company);
		Join<LeaveGrantBatch, LeaveGrantBatchDetail> leaveGrantBatchDetailJoin = leaveGrantRoot
				.join(LeaveGrantBatch_.leaveGrantBatchDetails);
		Join<LeaveGrantBatchDetail, LeaveSchemeType> leaveSchemeTypeJoin = leaveGrantBatchDetailJoin
				.join(LeaveGrantBatchDetail_.leaveSchemeType);
		Join<LeaveSchemeType, LeaveTypeMaster> leaveTypeJoin = leaveSchemeTypeJoin
				.join(LeaveSchemeType_.leaveTypeMaster);

		if (leaveType != 0) {
			restriction = cb
					.and(restriction, cb.equal(
							leaveTypeJoin.get(LeaveTypeMaster_.leaveTypeId),
							leaveType));
		}

		if (StringUtils.isNotBlank(fromDate)) {

			restriction = cb.and(
					restriction,
					cb.greaterThanOrEqualTo(
							leaveGrantRoot.get(LeaveGrantBatch_.batchDate).as(
									Date.class),
							DateUtils.stringToDate(fromDate)));
		}
		if (StringUtils.isNotBlank(toDate)) {

			restriction = cb.and(restriction, cb.lessThanOrEqualTo(
					leaveGrantRoot.get(LeaveGrantBatch_.batchDate).as(
							Date.class), DateUtils.stringToDate(toDate)));
		}

		restriction = cb.and(restriction,
				cb.isNull(leaveGrantRoot.get(LeaveGrantBatch_.deletedDate)));
		restriction = cb.and(restriction, cb.isNull(leaveGrantBatchDetailJoin
				.get(LeaveGrantBatchDetail_.deletedDate)));

		restriction = cb.and(restriction,
				cb.equal(companyJoin.get(Company_.companyId), companyId));

		criteriaQuery.where(restriction);

		if (sortDTO != null) {

			Path<String> sortPath = getSortPathForLeaveGrant(sortDTO,
					leaveGrantRoot);
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

		TypedQuery<LeaveGrantBatch> leaveGrantTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			leaveGrantTypedQuery.setFirstResult(getStartPosition(pageDTO));
			leaveGrantTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		return leaveGrantTypedQuery.getResultList();
	}

	@Override
	public Integer getCountByCondition(Long companyId, String fromDate,
			String toDate, Long leaveType) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Integer> criteriaQuery = cb.createQuery(Integer.class);
		Root<LeaveGrantBatch> leaveGrantRoot = criteriaQuery
				.from(LeaveGrantBatch.class);
		criteriaQuery.select(cb.count(leaveGrantRoot).as(Integer.class));

		Predicate restriction = cb.conjunction();

		Join<LeaveGrantBatch, Company> companyJoin = leaveGrantRoot
				.join(LeaveGrantBatch_.company);
		Join<LeaveGrantBatch, LeaveGrantBatchDetail> leaveGrantBatchDetailJoin = leaveGrantRoot
				.join(LeaveGrantBatch_.leaveGrantBatchDetails);
		Join<LeaveGrantBatchDetail, LeaveSchemeType> leaveSchemeTypeJoin = leaveGrantBatchDetailJoin
				.join(LeaveGrantBatchDetail_.leaveSchemeType);
		Join<LeaveSchemeType, LeaveTypeMaster> leaveTypeJoin = leaveSchemeTypeJoin
				.join(LeaveSchemeType_.leaveTypeMaster);

		if (leaveType != 0) {
			restriction = cb
					.and(restriction, cb.equal(
							leaveTypeJoin.get(LeaveTypeMaster_.leaveTypeId),
							leaveType));
		}
		if (StringUtils.isNotBlank(fromDate) && StringUtils.isNotBlank(toDate)) {

			restriction = cb.and(restriction, cb.between(
					leaveGrantRoot.get(LeaveGrantBatch_.batchDate).as(
							Date.class), DateUtils.stringToDate(fromDate),
					DateUtils.stringToDate(toDate)));
		}

		if (StringUtils.isNotBlank(fromDate) && StringUtils.isBlank(toDate)) {

			restriction = cb.and(
					restriction,
					cb.greaterThanOrEqualTo(
							leaveGrantRoot.get(LeaveGrantBatch_.batchDate).as(
									Date.class),
							DateUtils.stringToDate(fromDate)));
		}
		restriction = cb.and(restriction,
				cb.isNull(leaveGrantRoot.get(LeaveGrantBatch_.deletedDate)));
		restriction = cb.and(restriction, cb.isNull(leaveGrantBatchDetailJoin
				.get(LeaveGrantBatchDetail_.deletedDate)));
		restriction = cb.and(restriction,
				cb.equal(companyJoin.get(Company_.companyId), companyId));

		criteriaQuery.where(restriction);

		TypedQuery<Integer> leaveGrantTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return leaveGrantTypedQuery.getSingleResult();
	}

	public Path<String> getSortPathForLeaveGrant(SortCondition sortDTO,
			Root<LeaveGrantBatch> leaveGrantRoot) {

		Join<LeaveGrantBatch, LeaveGrantBatchDetail> leaveGrantBatchDetailJoin = leaveGrantRoot
				.join(LeaveGrantBatch_.leaveGrantBatchDetails);
		Join<LeaveGrantBatchDetail, LeaveSchemeType> leaveSchemeTypeJoin = leaveGrantBatchDetailJoin
				.join(LeaveGrantBatchDetail_.leaveSchemeType);
		Join<LeaveSchemeType, LeaveScheme> leaveSchemeJoin = leaveSchemeTypeJoin
				.join(LeaveSchemeType_.leaveScheme);
		Join<LeaveSchemeType, LeaveTypeMaster> leaveTypeJoin = leaveSchemeTypeJoin
				.join(LeaveSchemeType_.leaveTypeMaster);

		List<String> leaveGrantIsColList = new ArrayList<String>();
		leaveGrantIsColList.add(SortConstants.LEAVE_GRANTER_BATCH_DATE);
		leaveGrantIsColList.add(SortConstants.LEAVE_GRANTER_BATCH_NUMBER);

		List<String> leaveGrantDetailIsColList = new ArrayList<String>();
		leaveGrantDetailIsColList
				.add(SortConstants.LEAVE_GRANTER_EMPLOYEE_COUNT);
		leaveGrantDetailIsColList.add(SortConstants.LEAVE_GRANTER_FROM_PERIOD);
		leaveGrantDetailIsColList.add(SortConstants.LEAVE_GRANTER_TO_PERIOD);

		List<String> leaveTypeIsColList = new ArrayList<String>();
		leaveTypeIsColList.add(SortConstants.LEAVE_GRANTER_LEAVE_TYPE);

		List<String> leaveSchemeIsColList = new ArrayList<String>();
		leaveSchemeIsColList.add(SortConstants.LEAVE_GRANTER_LEAVE_SCHEME);

		Path<String> sortPath = null;

		if (leaveGrantIsColList.contains(sortDTO.getColumnName())) {
			sortPath = leaveGrantRoot.get(colMap.get(LeaveGrantBatch.class
					+ sortDTO.getColumnName()));
		}
		if (leaveGrantDetailIsColList.contains(sortDTO.getColumnName())) {
			sortPath = leaveGrantBatchDetailJoin
					.get(colMap.get(LeaveGrantBatchDetail.class
							+ sortDTO.getColumnName()));
		}
		if (leaveTypeIsColList.contains(sortDTO.getColumnName())) {
			sortPath = leaveTypeJoin.get(colMap.get(LeaveTypeMaster.class
					+ sortDTO.getColumnName()));
		}
		if (leaveSchemeIsColList.contains(sortDTO.getColumnName())) {
			sortPath = leaveSchemeJoin.get(colMap.get(LeaveScheme.class
					+ sortDTO.getColumnName()));
		}
		return sortPath;
	}

	@Override
	public Boolean callDeleteLeaveGrantBatchProc(
			final Long leaveGrantBatchDetailID,
			final Long leaveGrantBatchEmployeeDetailID) {

		final List<Boolean> statusList = new ArrayList<>();

		getSession().doWork(new Work() {

			@Override
			public void execute(Connection connection) throws SQLException {
				try {
					CallableStatement cstmt = null;
					cstmt = connection
							.prepareCall("{call Delete_Leave_Grant_Batch (?,?,?,?)}");
					if (leaveGrantBatchDetailID == null) {
						cstmt.setNull("@Leave_Grant_Batch_Detail_ID",
								java.sql.Types.BIGINT);
					} else {
						cstmt.setLong("@Leave_Grant_Batch_Detail_ID",
								leaveGrantBatchDetailID);
					}
					if (leaveGrantBatchEmployeeDetailID == null) {
						cstmt.setNull("@Leave_Grant_Batch_Employee_Detail_ID",
								java.sql.Types.BIGINT);
					} else {
						cstmt.setLong("@Leave_Grant_Batch_Employee_Detail_ID",
								leaveGrantBatchEmployeeDetailID);
					}
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

}
