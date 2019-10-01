package com.payasia.dao.impl;

import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.springframework.stereotype.Repository;

import com.payasia.common.dto.NotificationAlertConditionDTO;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.NotificationAlertDAO;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeActivationCode;
import com.payasia.dao.bean.EmployeeActivationCode_;
import com.payasia.dao.bean.EmployeeMobileDetails;
import com.payasia.dao.bean.EmployeeMobileDetails_;
import com.payasia.dao.bean.Employee_;
import com.payasia.dao.bean.NotificationAlert;
import com.payasia.dao.bean.NotificationAlert_;

@Repository
public class NotificationAlertDAOImpl extends BaseDAO implements
		NotificationAlertDAO {

	@Override
	protected Object getBaseEntity() {
		NotificationAlert notificationAlert = new NotificationAlert();
		return notificationAlert;
	}

	@Override
	public void update(NotificationAlert notificationAlert) {
		super.update(notificationAlert);

	}

	@Override
	public void save(NotificationAlert notificationAlert) {
		super.save(notificationAlert);
	}

	@Override
	public void delete(NotificationAlert notificationAlert) {
		super.delete(notificationAlert);

	}

	@Override
	public NotificationAlert findByID(long notificationAlertId) {
		return super.findById(NotificationAlert.class, notificationAlertId);
	}

	@Override
	public List<NotificationAlert> findByCondtion(
			NotificationAlertConditionDTO notificationAlertConditionDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<NotificationAlert> criteriaQuery = cb
				.createQuery(NotificationAlert.class);
		Root<NotificationAlert> notificationAlertRoot = criteriaQuery
				.from(NotificationAlert.class);
		criteriaQuery.select(notificationAlertRoot);

		Join<NotificationAlert, Employee> employeeJoin = notificationAlertRoot
				.join(NotificationAlert_.employee);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(
				employeeJoin.get(Employee_.employeeId),
				notificationAlertConditionDTO.getEmployeeId()));
		restriction = cb.and(restriction, cb.equal(
				notificationAlertRoot.get(NotificationAlert_.shownStatus),
				notificationAlertConditionDTO.getShownStatus()));

		criteriaQuery.where(restriction);

		TypedQuery<NotificationAlert> empTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<NotificationAlert> notificationAlertList = empTypedQuery
				.getResultList();

		return notificationAlertList;
	}

	@Override
	public void updateById(List<Long> notificationAlertIds) {
		String queryString = "UPDATE NotificationAlert NA SET NA.shownStatus = :shownStatus where NA.notificationAlertId in :notificationAlertIds";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("shownStatus", true);
		q.setParameter("notificationAlertIds", notificationAlertIds);
		q.executeUpdate();

	}

	@Override
	public List<NotificationAlert> getIOSDeviceNotificationsAlerts() {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<NotificationAlert> criteriaQuery = cb
				.createQuery(NotificationAlert.class);
		Root<NotificationAlert> notificationAlertRoot = criteriaQuery
				.from(NotificationAlert.class);
		criteriaQuery.select(notificationAlertRoot);

		Join<NotificationAlert, Employee> employeeJoin = notificationAlertRoot
				.join(NotificationAlert_.employee);

		Predicate restriction = cb.conjunction();

		Subquery<Long> subquery = criteriaQuery.subquery(Long.class);
		Root<EmployeeActivationCode> employeeActivationCodeRoot = subquery
				.from(EmployeeActivationCode.class);
		subquery.select(
				employeeActivationCodeRoot
						.get(EmployeeActivationCode_.employee)
						.get("employeeId").as(Long.class)).distinct(true);

		Join<EmployeeActivationCode, EmployeeMobileDetails> employeeMobileDetailsSubJoin = employeeActivationCodeRoot
				.join(EmployeeActivationCode_.employeeMobileDetails);

		Predicate subRestriction = cb.conjunction();

		subRestriction = cb.and(subRestriction, cb.equal(
				employeeMobileDetailsSubJoin
						.get(EmployeeMobileDetails_.deviceOS), "iOS"));
		subquery.where(subRestriction);

		restriction = cb.and(restriction,
				cb.in(employeeJoin.get(Employee_.employeeId)).value(subquery));

		restriction = cb.and(restriction, cb.equal(
				notificationAlertRoot.get(NotificationAlert_.shownStatus),
				false));

		criteriaQuery.where(restriction);

		TypedQuery<NotificationAlert> empTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<NotificationAlert> notificationAlertList = empTypedQuery
				.getResultList();

		return notificationAlertList;
	}

}
