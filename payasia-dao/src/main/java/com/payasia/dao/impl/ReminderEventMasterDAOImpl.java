package com.payasia.dao.impl;

import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.payasia.dao.BaseDAO;
import com.payasia.dao.ReminderEventMasterDAO;
import com.payasia.dao.bean.ModuleMaster;
import com.payasia.dao.bean.ModuleMaster_;
import com.payasia.dao.bean.ReminderEventMaster;
import com.payasia.dao.bean.ReminderEventMaster_;

@Repository
public class ReminderEventMasterDAOImpl extends BaseDAO implements
		ReminderEventMasterDAO {

	@Override
	protected Object getBaseEntity() {
		ReminderEventMaster reminderEventMaster = new ReminderEventMaster();
		return reminderEventMaster;
	}

	@Override
	public List<ReminderEventMaster> getAllReminderEvents(
			String leaveEventReminderModuleLeave) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ReminderEventMaster> criteriaQuery = cb
				.createQuery(ReminderEventMaster.class);
		Root<ReminderEventMaster> reminderEventRoot = criteriaQuery
				.from(ReminderEventMaster.class);
		criteriaQuery.select(reminderEventRoot);
		Join<ReminderEventMaster, ModuleMaster> moduleMasterJoin = reminderEventRoot
				.join(ReminderEventMaster_.moduleMaster);
		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(
				moduleMasterJoin.get(ModuleMaster_.moduleName),
				leaveEventReminderModuleLeave));
		criteriaQuery.where(restriction);
		TypedQuery<ReminderEventMaster> reminderEventTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		List<ReminderEventMaster> reminderEvents = reminderEventTypedQuery
				.getResultList();
		return reminderEvents;
	}

	@Override
	public ReminderEventMaster findById(long eventMasterId) {

		return super.findById(ReminderEventMaster.class, eventMasterId);
	}

}
