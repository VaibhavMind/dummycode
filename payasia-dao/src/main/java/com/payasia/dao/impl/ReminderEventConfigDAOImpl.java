package com.payasia.dao.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.payasia.common.dto.LeaveEventReminderConditionDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.PageRequest;
import com.payasia.common.util.NullAwareBeanUtilsBean;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.ReminderEventConfigDAO;
import com.payasia.dao.bean.AppCodeMaster;
import com.payasia.dao.bean.AppCodeMaster_;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.LeaveScheme;
import com.payasia.dao.bean.LeaveScheme_;
import com.payasia.dao.bean.LeaveTypeMaster;
import com.payasia.dao.bean.LeaveTypeMaster_;
import com.payasia.dao.bean.ReminderEventConfig;
import com.payasia.dao.bean.ReminderEventConfig_;
import com.payasia.dao.bean.ReminderEventMaster;
import com.payasia.dao.bean.ReminderEventMaster_;

@Repository
public class ReminderEventConfigDAOImpl extends BaseDAO implements
		ReminderEventConfigDAO {

	@Override
	protected Object getBaseEntity() {
		ReminderEventConfig reminderEventConfig = new ReminderEventConfig();

		return reminderEventConfig;
	}

	@Override
	public ReminderEventConfig save(ReminderEventConfig reminderEventConfig) {

		ReminderEventConfig persistObj = reminderEventConfig;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (ReminderEventConfig) getBaseEntity();
			beanUtil.copyProperties(persistObj, reminderEventConfig);
		} catch (IllegalAccessException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		} catch (InvocationTargetException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		}
		this.entityManagerFactory.persist(persistObj);
		return persistObj;
	}

	@Override
	public List<ReminderEventConfig> findByCondition(
			LeaveEventReminderConditionDTO conditionDTO, PageRequest pageDTO) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ReminderEventConfig> criteriaQuery = cb
				.createQuery(ReminderEventConfig.class);
		Root<ReminderEventConfig> reminderRoot = criteriaQuery
				.from(ReminderEventConfig.class);
		criteriaQuery.select(reminderRoot);

		Join<ReminderEventConfig, Company> companyJoin = reminderRoot
				.join(ReminderEventConfig_.company);
		Join<ReminderEventConfig, ReminderEventMaster> reminderEventMasterJoin = reminderRoot
				.join(ReminderEventConfig_.reminderEventMaster);

		Join<ReminderEventConfig, AppCodeMaster> recepJoin = reminderRoot
				.join(ReminderEventConfig_.recipientType);

		Predicate restriction = cb.conjunction();

		if (StringUtils.isNotBlank(conditionDTO.getSearchType())
				&& conditionDTO.getSearchType().equalsIgnoreCase(
						PayAsiaConstants.LEAVE_Event_Reminder_TYPE_EVENT)) {

			if (conditionDTO.getSearchTypeId() != null) {
				restriction = cb.and(restriction, cb.equal(
						reminderEventMasterJoin
								.get(ReminderEventMaster_.reminderEventId),
						conditionDTO.getSearchTypeId()));

			}

		} else if (StringUtils.isNotBlank(conditionDTO.getSearchType())
				&& conditionDTO.getSearchType().equalsIgnoreCase(
						PayAsiaConstants.LEAVE_Event_Reminder_TYPE_LEAVE_TYPE)) {
			if (conditionDTO.getSearchTypeId() != null) {
				restriction = cb.and(restriction, cb.equal(
						reminderRoot.get(ReminderEventConfig_.leaveTypeMaster)
								.get("leaveTypeId").as(Long.class),
						conditionDTO.getSearchTypeId()));
			}

		} else if (StringUtils.isNotBlank(conditionDTO.getSearchType())
				&& conditionDTO
						.getSearchType()
						.equalsIgnoreCase(
								PayAsiaConstants.LEAVE_Event_Reminder_TYPE_LEAVE_SCHEME)) {
			if (conditionDTO.getSearchTypeId() != null) {

				restriction = cb.and(restriction, cb.equal(
						reminderRoot.get(ReminderEventConfig_.leaveScheme)
								.get("leaveSchemeId").as(Long.class),
						conditionDTO.getSearchTypeId()));

			}

		} else if (StringUtils.isNotBlank(conditionDTO.getSearchType())
				&& conditionDTO.getSearchType().equalsIgnoreCase(
						PayAsiaConstants.LEAVE_Event_Reminder_TYPE_LEAVE_RECEP)) {
			if (conditionDTO.getSearchTypeId() != null) {

				restriction = cb.and(restriction, cb.equal(
						recepJoin.get(AppCodeMaster_.appCodeID),
						conditionDTO.getSearchTypeId()));

			}

		} else if (StringUtils.isNotBlank(conditionDTO.getSearchType())
				&& conditionDTO.getSearchType().equalsIgnoreCase(
						PayAsiaConstants.TIMESHEET_Event_Reminder_TYPE_EVENT)) {
			if (conditionDTO.getSearchTypeId() != null) {

				restriction = cb.and(restriction, cb.equal(
						reminderEventMasterJoin
								.get(ReminderEventMaster_.reminderEventId),
						conditionDTO.getSearchTypeId()));

			}
		} else if (StringUtils.isNotBlank(conditionDTO.getSearchType())
				&& conditionDTO.getSearchType().equalsIgnoreCase(
						PayAsiaConstants.TIMESHEET_Event_Reminder_TYPE_RECEP)) {
			if (conditionDTO.getSearchTypeId() != null) {

				restriction = cb.and(restriction, cb.equal(
						recepJoin.get(AppCodeMaster_.appCodeID),
						conditionDTO.getSearchTypeId()));

			}
		}

		restriction = cb.and(
				restriction,
				cb.equal(companyJoin.get(Company_.companyId),
						conditionDTO.getCompanyId()));

		criteriaQuery.where(restriction);
		TypedQuery<ReminderEventConfig> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			typedQuery.setFirstResult(getStartPosition(pageDTO));
			typedQuery.setMaxResults(pageDTO.getPageSize());
		}
		List<ReminderEventConfig> reminderEventConfigs = typedQuery
				.getResultList();

		return reminderEventConfigs;
	}

	@Override
	public ReminderEventConfig findByTypeSchemeEvent(Long leaveTypeId,
			Long leaveSchemeId, String eventName, Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ReminderEventConfig> criteriaQuery = cb
				.createQuery(ReminderEventConfig.class);
		Root<ReminderEventConfig> reminderRoot = criteriaQuery
				.from(ReminderEventConfig.class);
		criteriaQuery.select(reminderRoot);
		Predicate restriction = cb.conjunction();

		Join<ReminderEventConfig, ReminderEventMaster> reminderEventMasterJoin = reminderRoot
				.join(ReminderEventConfig_.reminderEventMaster);
		Join<ReminderEventConfig, LeaveScheme> leaveSchemeJoin = reminderRoot
				.join(ReminderEventConfig_.leaveScheme);
		Join<ReminderEventConfig, LeaveTypeMaster> leaveTypeJoin = reminderRoot
				.join(ReminderEventConfig_.leaveTypeMaster);

		restriction = cb.and(restriction, cb.equal(
				reminderEventMasterJoin.get(ReminderEventMaster_.event),
				eventName));

		restriction = cb
				.and(restriction, cb.equal(
						leaveSchemeJoin.get(LeaveScheme_.leaveSchemeId),
						leaveSchemeId));

		restriction = cb.and(
				restriction,
				cb.equal(
						reminderRoot.get(ReminderEventConfig_.company)
								.get("companyId").as(Long.class), companyId));

		restriction = cb.and(restriction, cb.equal(
				leaveTypeJoin.get(LeaveTypeMaster_.leaveTypeId), leaveTypeId));
		criteriaQuery.where(restriction);
		TypedQuery<ReminderEventConfig> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<ReminderEventConfig> reminderEventConfigs = typedQuery
				.getResultList();
		if (!reminderEventConfigs.isEmpty()) {
			return reminderEventConfigs.get(0);
		}

		return null;
	}

	@Override
	public ReminderEventConfig findBySchemeEvent(Long leaveSchemeId,
			String eventName, Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ReminderEventConfig> criteriaQuery = cb
				.createQuery(ReminderEventConfig.class);
		Root<ReminderEventConfig> reminderRoot = criteriaQuery
				.from(ReminderEventConfig.class);
		criteriaQuery.select(reminderRoot);
		Predicate restriction = cb.conjunction();

		Join<ReminderEventConfig, ReminderEventMaster> reminderEventMasterJoin = reminderRoot
				.join(ReminderEventConfig_.reminderEventMaster);
		Join<ReminderEventConfig, LeaveScheme> leaveSchemeJoin = reminderRoot
				.join(ReminderEventConfig_.leaveScheme);

		restriction = cb.and(restriction, cb.equal(
				reminderEventMasterJoin.get(ReminderEventMaster_.event),
				eventName));

		restriction = cb.and(
				restriction,
				cb.equal(
						reminderRoot.get(ReminderEventConfig_.company)
								.get("companyId").as(Long.class), companyId));
		restriction = cb
				.and(restriction, cb.equal(
						leaveSchemeJoin.get(LeaveScheme_.leaveSchemeId),
						leaveSchemeId));
		criteriaQuery.where(restriction);
		TypedQuery<ReminderEventConfig> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<ReminderEventConfig> reminderEventConfigs = typedQuery
				.getResultList();
		if (!reminderEventConfigs.isEmpty()) {
			return reminderEventConfigs.get(0);
		}

		return null;
	}

	@Override
	public ReminderEventConfig findByTypeEvent(Long leaveTypeId,
			String eventName, Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ReminderEventConfig> criteriaQuery = cb
				.createQuery(ReminderEventConfig.class);
		Root<ReminderEventConfig> reminderRoot = criteriaQuery
				.from(ReminderEventConfig.class);
		criteriaQuery.select(reminderRoot);
		Predicate restriction = cb.conjunction();

		Join<ReminderEventConfig, ReminderEventMaster> reminderEventMasterJoin = reminderRoot
				.join(ReminderEventConfig_.reminderEventMaster);

		Join<ReminderEventConfig, LeaveTypeMaster> leaveTypeJoin = reminderRoot
				.join(ReminderEventConfig_.leaveTypeMaster);

		restriction = cb.and(restriction, cb.equal(
				reminderEventMasterJoin.get(ReminderEventMaster_.event),
				eventName));

		restriction = cb.and(restriction, cb.equal(
				leaveTypeJoin.get(LeaveTypeMaster_.leaveTypeId), leaveTypeId));

		restriction = cb.and(
				restriction,
				cb.equal(
						reminderRoot.get(ReminderEventConfig_.company)
								.get("companyId").as(Long.class), companyId));
		criteriaQuery.where(restriction);
		TypedQuery<ReminderEventConfig> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<ReminderEventConfig> reminderEventConfigs = typedQuery
				.getResultList();
		if (!reminderEventConfigs.isEmpty()) {
			return reminderEventConfigs.get(0);
		}

		return null;
	}

	@Override
	public ReminderEventConfig findByEvent(String eventName, Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ReminderEventConfig> criteriaQuery = cb
				.createQuery(ReminderEventConfig.class);
		Root<ReminderEventConfig> reminderRoot = criteriaQuery
				.from(ReminderEventConfig.class);
		criteriaQuery.select(reminderRoot);
		Predicate restriction = cb.conjunction();

		Join<ReminderEventConfig, ReminderEventMaster> reminderEventMasterJoin = reminderRoot
				.join(ReminderEventConfig_.reminderEventMaster);

		restriction = cb.and(restriction, cb.equal(
				reminderEventMasterJoin.get(ReminderEventMaster_.event),
				eventName));
		restriction = cb.and(
				restriction,
				cb.equal(
						reminderRoot.get(ReminderEventConfig_.company)
								.get("companyId").as(Long.class), companyId));
		criteriaQuery.where(restriction);
		TypedQuery<ReminderEventConfig> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<ReminderEventConfig> reminderEventConfigs = typedQuery
				.getResultList();
		if (!reminderEventConfigs.isEmpty()) {
			return reminderEventConfigs.get(0);
		}

		return null;
	}

	@Override
	public Integer getCountForCondition(
			LeaveEventReminderConditionDTO leaveEventReminderConditionDTO) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Integer> criteriaQuery = cb.createQuery(Integer.class);
		Root<ReminderEventConfig> reminderRoot = criteriaQuery
				.from(ReminderEventConfig.class);
		criteriaQuery.select(cb.count(reminderRoot).as(Integer.class));

		Join<ReminderEventConfig, Company> companyJoin = reminderRoot
				.join(ReminderEventConfig_.company);
		Join<ReminderEventConfig, ReminderEventMaster> reminderEventMasterJoin = reminderRoot
				.join(ReminderEventConfig_.reminderEventMaster);

		Join<ReminderEventConfig, AppCodeMaster> recepJoin = reminderRoot
				.join(ReminderEventConfig_.recipientType);

		Predicate restriction = cb.conjunction();

		if (StringUtils.isNotBlank(leaveEventReminderConditionDTO
				.getSearchType())
				&& leaveEventReminderConditionDTO
						.getSearchType()
						.equalsIgnoreCase(
								PayAsiaConstants.LEAVE_Event_Reminder_TYPE_EVENT)) {

			if (leaveEventReminderConditionDTO.getSearchTypeId() != null) {
				restriction = cb.and(restriction, cb.equal(
						reminderEventMasterJoin
								.get(ReminderEventMaster_.reminderEventId),
						leaveEventReminderConditionDTO.getSearchTypeId()));

			}

		} else if (StringUtils.isNotBlank(leaveEventReminderConditionDTO
				.getSearchType())
				&& leaveEventReminderConditionDTO
						.getSearchType()
						.equalsIgnoreCase(
								PayAsiaConstants.LEAVE_Event_Reminder_TYPE_LEAVE_TYPE)) {
			if (leaveEventReminderConditionDTO.getSearchTypeId() != null) {
				restriction = cb.and(restriction, cb.equal(
						reminderRoot.get(ReminderEventConfig_.leaveTypeMaster)
								.get("leaveTypeId").as(Long.class),
						leaveEventReminderConditionDTO.getSearchTypeId()));
			}

		} else if (StringUtils.isNotBlank(leaveEventReminderConditionDTO
				.getSearchType())
				&& leaveEventReminderConditionDTO
						.getSearchType()
						.equalsIgnoreCase(
								PayAsiaConstants.LEAVE_Event_Reminder_TYPE_LEAVE_SCHEME)) {
			if (leaveEventReminderConditionDTO.getSearchTypeId() != null) {

				restriction = cb.and(restriction, cb.equal(
						reminderRoot.get(ReminderEventConfig_.leaveScheme)
								.get("leaveSchemeId").as(Long.class),
						leaveEventReminderConditionDTO.getSearchTypeId()));

			}

		} else if (StringUtils.isNotBlank(leaveEventReminderConditionDTO
				.getSearchType())
				&& leaveEventReminderConditionDTO
						.getSearchType()
						.equalsIgnoreCase(
								PayAsiaConstants.LEAVE_Event_Reminder_TYPE_LEAVE_RECEP)) {
			if (leaveEventReminderConditionDTO.getSearchTypeId() != null) {

				restriction = cb.and(restriction, cb.equal(
						recepJoin.get(AppCodeMaster_.appCodeID),
						leaveEventReminderConditionDTO.getSearchTypeId()));

			}

		} else if (StringUtils.isNotBlank(leaveEventReminderConditionDTO
				.getSearchType())
				&& leaveEventReminderConditionDTO
						.getSearchType()
						.equalsIgnoreCase(
								PayAsiaConstants.TIMESHEET_Event_Reminder_TYPE_EVENT)) {
			if (leaveEventReminderConditionDTO.getSearchTypeId() != null) {

				restriction = cb.and(restriction, cb.equal(
						reminderEventMasterJoin
								.get(ReminderEventMaster_.reminderEventId),
						leaveEventReminderConditionDTO.getSearchTypeId()));

			}
		} else if (StringUtils.isNotBlank(leaveEventReminderConditionDTO
				.getSearchType())
				&& leaveEventReminderConditionDTO
						.getSearchType()
						.equalsIgnoreCase(
								PayAsiaConstants.TIMESHEET_Event_Reminder_TYPE_RECEP)) {
			if (leaveEventReminderConditionDTO.getSearchTypeId() != null) {

				restriction = cb.and(restriction, cb.equal(
						recepJoin.get(AppCodeMaster_.appCodeID),
						leaveEventReminderConditionDTO.getSearchTypeId()));

			}
		}

		restriction = cb.and(restriction, cb.equal(
				companyJoin.get(Company_.companyId),
				leaveEventReminderConditionDTO.getCompanyId()));
		criteriaQuery.where(restriction);
		TypedQuery<Integer> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return typedQuery.getSingleResult();
	}

	@Override
	public ReminderEventConfig findById(long reminderEventConfigId) {

		ReminderEventConfig reminderEventConfig = super.findById(
				ReminderEventConfig.class, reminderEventConfigId);
		return reminderEventConfig;
	}

	@Override
	public void update(ReminderEventConfig reminderEventConfig) {

		super.update(reminderEventConfig);
	}

	@Override
	public void delete(ReminderEventConfig reminderEventConfig) {
		super.delete(reminderEventConfig);
	}

	@Override
	public ReminderEventConfig findEventReminderConfigByCompanyId(Long eventReminderConfigId, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ReminderEventConfig> criteriaQuery = cb
				.createQuery(ReminderEventConfig.class);
		Root<ReminderEventConfig> reminderRoot = criteriaQuery
				.from(ReminderEventConfig.class);
		criteriaQuery.select(reminderRoot);
		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(
				reminderRoot.get(ReminderEventConfig_.reminderEventConfigId),eventReminderConfigId));
		restriction = cb.and(restriction,cb.equal(
				reminderRoot.get(ReminderEventConfig_.company).get("companyId").as(Long.class), companyId));
		criteriaQuery.where(restriction);
		TypedQuery<ReminderEventConfig> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<ReminderEventConfig> reminderEventConfigs = typedQuery
				.getResultList();
		if (!reminderEventConfigs.isEmpty()) {
			return reminderEventConfigs.get(0);
		}

		return null;
	}

}
