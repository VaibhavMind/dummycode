package com.payasia.dao.impl;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.payasia.dao.BaseDAO;
import com.payasia.dao.LeaveSchemeTypeGrantConditionDAO;
import com.payasia.dao.bean.LeaveSchemeTypeGrantCondition;

@Repository
public class LeaveSchemeTypeGrantConditionDAOImpl extends BaseDAO implements
		LeaveSchemeTypeGrantConditionDAO {

	@Override
	protected Object getBaseEntity() {
		LeaveSchemeTypeGrantCondition leaveSchemeTypeGrantCondition = new LeaveSchemeTypeGrantCondition();
		return leaveSchemeTypeGrantCondition;
	}

	@Override
	public void save(LeaveSchemeTypeGrantCondition leaveSchemeTypeGrantCondition) {
		super.save(leaveSchemeTypeGrantCondition);
	}

	@Override
	public void deleteByAvailingId(Long leaveSchemeTypeAvailingId) {
		String queryString = "DELETE FROM LeaveSchemeTypeGrantCondition e WHERE e.leaveSchemeTypeAvailingLeave.leaveSchemeTypeAvailingLeaveId = :leaveSchemeTypeAvailingId";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("leaveSchemeTypeAvailingId", leaveSchemeTypeAvailingId);
		q.executeUpdate();

	}

}
