package com.payasia.dao.impl;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.payasia.dao.BaseDAO;
import com.payasia.dao.SchedulerStatusDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.SchedulerMaster;
import com.payasia.dao.bean.SchedulerMaster_;
import com.payasia.dao.bean.SchedulerStatus;
import com.payasia.dao.bean.SchedulerStatus_;

@Repository
public class SchedulerStatusDAOImpl extends BaseDAO implements
		SchedulerStatusDAO {

	@Override
	protected Object getBaseEntity() {
		SchedulerStatus schedulerStatus = new SchedulerStatus();
		return schedulerStatus;
	}

	@Override
	public void update(SchedulerStatus schedulerStatus) {
		super.update(schedulerStatus);

	}

	@Override
	public void updateSchedulerStatus(SchedulerStatus schedulerStatus) {
		String queryString = "UPDATE SchedulerStatus ss SET ss.lastRunDate = :lastRunDate, ss.lastRunStatus =:lastRunStatus,ss.failureMessage =:failureMessage WHERE ss.schedulerStatusId = :schedulerStatusId";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("lastRunDate", schedulerStatus.getLastRunDate());
		q.setParameter("lastRunStatus", schedulerStatus.isLastRunStatus());
		q.setParameter("failureMessage", schedulerStatus.getFailureMessage());
		q.setParameter("schedulerStatusId",
				schedulerStatus.getSchedulerStatusId());
		q.executeUpdate();

	}

	@Override
	public void save(SchedulerStatus schedulerStatus) {
		super.save(schedulerStatus);
	}

	@Override
	public void delete(SchedulerStatus schedulerStatus) {
		super.delete(schedulerStatus);

	}

	@Override
	public SchedulerStatus findByID(long schedulerStatusId) {
		return super.findById(SchedulerStatus.class, schedulerStatusId);
	}

	@Override
	public SchedulerStatus findByCondition(long companyId, long schedulerId,
			Date currentDate, boolean b) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<SchedulerStatus> criteriaQuery = cb
				.createQuery(SchedulerStatus.class);
		Root<SchedulerStatus> schedulerStatusRoot = criteriaQuery
				.from(SchedulerStatus.class);
		criteriaQuery.select(schedulerStatusRoot);

		Join<SchedulerStatus, Company> empRootCompanyJoin = schedulerStatusRoot
				.join(SchedulerStatus_.company);

		Join<SchedulerStatus, SchedulerMaster> schedularMasterJoin = schedulerStatusRoot
				.join(SchedulerStatus_.schedulerMaster);

		Predicate restriction = cb.conjunction();

		restriction = cb
				.and(restriction, cb.equal(
						empRootCompanyJoin.get(Company_.companyId), companyId));

		restriction = cb.and(restriction, cb.equal(
				schedularMasterJoin.get(SchedulerMaster_.schedulerId),
				schedulerId));

		 
		 
		 
		 
		 
		 

		criteriaQuery.where(restriction);

		TypedQuery<SchedulerStatus> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		List<SchedulerStatus> list = typedQuery.getResultList();
		if (list != null &&  !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}

}
