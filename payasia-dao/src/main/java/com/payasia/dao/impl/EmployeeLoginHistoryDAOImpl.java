package com.payasia.dao.impl;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.payasia.common.util.DateUtils;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.EmployeeLoginHistoryDAO;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeLoginHistory;
import com.payasia.dao.bean.EmployeeLoginHistory_;
import com.payasia.dao.bean.Employee_;

/**
 * The Class EmployeeLoginHistoryDAOImpl.
 */
/**
 * @author vivekjain
 * 
 */
@Repository
public class EmployeeLoginHistoryDAOImpl extends BaseDAO implements
		EmployeeLoginHistoryDAO {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.EmployeeLoginHistoryDAO#save(com.payasia.dao.bean.
	 * EmployeeLoginHistory)
	 */
	@Override
	public void save(EmployeeLoginHistory employeeLoginHistory) {
		super.save(employeeLoginHistory);
	}

	@Override
	public EmployeeLoginHistory findById(Long employeeId) {
		return super.findById(EmployeeLoginHistory.class, employeeId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.BaseDAO#getBaseEntity()
	 */
	@Override
	protected Object getBaseEntity() {
		EmployeeLoginHistory employeeLoginHistory = new EmployeeLoginHistory();
		return employeeLoginHistory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.EmployeeLoginHistoryDAO#findEmployeeLoginHistory(java
	 * .lang.Long)
	 */
	@Override
	public List<Object[]> findEmployeeLoginHistory(Long companyId) {

		String queryString = " select DISTINCT DATEPART(month,logged_in_date) as login_month,count(*) as login_count from Employee_Login_History ELH ";
		queryString += "	inner join Employee EMP on EMP.Employee_Id = ELH.Employee_Id";
		queryString += " where logged_in_date >=:loggedInDate  and EMP.Company_Id= :company";

		queryString += " group by DATEPART(month,logged_in_date),DATEPART(YEAR,LOGGED_IN_DATE)";

		Query q = entityManagerFactory.createNativeQuery(queryString);

		Calendar cal = Calendar.getInstance();
		cal = new GregorianCalendar(cal.get(Calendar.YEAR), 0, 1, 0, 0, 0);
		Timestamp dateTime = new Timestamp(cal.getTimeInMillis());

		q.setParameter("loggedInDate", dateTime);
		q.setParameter("company", companyId);

		List<Object[]> tupleList = q.getResultList();

		return tupleList;

	}

	@Override
	public List<EmployeeLoginHistory> findByEmployeeId(Long employeeId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeLoginHistory> criteriaQuery = cb
				.createQuery(EmployeeLoginHistory.class);
		Root<EmployeeLoginHistory> empRoot = criteriaQuery
				.from(EmployeeLoginHistory.class);

		criteriaQuery.select(empRoot);

		Join<EmployeeLoginHistory, Employee> empJoin = empRoot
				.join(EmployeeLoginHistory_.employee);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction,
				cb.equal(empJoin.get(Employee_.employeeId), employeeId));
		criteriaQuery.where(restriction);

		TypedQuery<EmployeeLoginHistory> empTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return empTypedQuery.getResultList();
	}

	@Override
	public boolean isEmployeeLoginHistoryExist(long employeeId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Integer> criteriaQuery = cb.createQuery(Integer.class);
		Root<EmployeeLoginHistory> empLoginHistoryRoot = criteriaQuery
				.from(EmployeeLoginHistory.class);
		criteriaQuery.select(cb.count(empLoginHistoryRoot).as(Integer.class));

		Join<EmployeeLoginHistory, Employee> employeeJoin = empLoginHistoryRoot
				.join(EmployeeLoginHistory_.employee);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction,
				cb.equal(employeeJoin.get(Employee_.employeeId), employeeId));
		criteriaQuery.where(restriction);

		TypedQuery<Integer> empLoginHistoryTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		int employeeLoginHistoryCount = empLoginHistoryTypedQuery
				.getSingleResult();
		if (employeeLoginHistoryCount > 0) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public List<EmployeeLoginHistory> findByEmployeeIdAndDate(
			List<Long> employeeIds, String fromDate, String toDate,
			String dateFormat) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeLoginHistory> criteriaQuery = cb
				.createQuery(EmployeeLoginHistory.class);
		Root<EmployeeLoginHistory> empRoot = criteriaQuery
				.from(EmployeeLoginHistory.class);

		criteriaQuery.select(empRoot);

		Join<EmployeeLoginHistory, Employee> empJoin = empRoot
				.join(EmployeeLoginHistory_.employee);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.between(
				empRoot.get(EmployeeLoginHistory_.loggedInDate).as(Date.class),
				DateUtils.stringToTimestamp(fromDate, dateFormat),
				DateUtils.stringToTimestamp(toDate, dateFormat)));
		restriction = cb.and(restriction,
				cb.and(empJoin.get(Employee_.employeeId).in(employeeIds)));
		criteriaQuery.where(restriction);

		TypedQuery<EmployeeLoginHistory> empTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return empTypedQuery.getResultList();
	}
}
