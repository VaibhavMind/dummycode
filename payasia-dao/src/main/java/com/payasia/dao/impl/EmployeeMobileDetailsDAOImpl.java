package com.payasia.dao.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.payasia.common.dto.EmployeeMobileConditionDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.NullAwareBeanUtilsBean;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.EmployeeMobileDetailsDAO;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeActivationCode;
import com.payasia.dao.bean.EmployeeActivationCode_;
import com.payasia.dao.bean.EmployeeMobileDetails;
import com.payasia.dao.bean.EmployeeMobileDetails_;
import com.payasia.dao.bean.Employee_;

@Repository
public class EmployeeMobileDetailsDAOImpl extends BaseDAO implements
		EmployeeMobileDetailsDAO {

	@Override
	protected Object getBaseEntity() {
		EmployeeMobileDetails employeeMobileDetails = new EmployeeMobileDetails();
		return employeeMobileDetails;
	}

	@Override
	public void update(EmployeeMobileDetails employeeMobileDetails) {
		super.update(employeeMobileDetails);

	}

	@Override
	public void save(EmployeeMobileDetails employeeMobileDetails) {
		super.save(employeeMobileDetails);
	}

	@Override
	public void delete(EmployeeMobileDetails employeeMobileDetails) {
		super.delete(employeeMobileDetails);

	}

	@Override
	public EmployeeMobileDetails findByID(long employeeMobileDetailsId) {
		return super.findById(EmployeeMobileDetails.class,
				employeeMobileDetailsId);
	}

	@Override
	public EmployeeMobileDetails saveReturn(
			EmployeeMobileDetails employeeMobileDetails) {

		EmployeeMobileDetails persistObj = employeeMobileDetails;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (EmployeeMobileDetails) getBaseEntity();
			beanUtil.copyProperties(persistObj, employeeMobileDetails);
		} catch (IllegalAccessException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		} catch (InvocationTargetException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		}
		this.entityManagerFactory.persist(persistObj);
		return persistObj;
	}

	@Override
	public List<EmployeeMobileDetails> findByCondition(
			EmployeeMobileConditionDTO conditionDTO, PageRequest pageDTO,
			SortCondition sortDTO, Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeMobileDetails> criteriaQuery = cb
				.createQuery(EmployeeMobileDetails.class);
		Root<EmployeeMobileDetails> employeeMobileDetailsRoot = criteriaQuery
				.from(EmployeeMobileDetails.class);
		criteriaQuery.select(employeeMobileDetailsRoot);

		Join<EmployeeMobileDetails, EmployeeActivationCode> employeeActivationCodeJoin = employeeMobileDetailsRoot
				.join(EmployeeMobileDetails_.employeeActivationCode);

		Join<EmployeeActivationCode, Employee> employeeJoin = employeeActivationCodeJoin
				.join(EmployeeActivationCode_.employee);

		Predicate restriction = cb.conjunction();

		if (StringUtils.isNotBlank(conditionDTO.getEmployeeNumber())) {

			restriction = cb.and(restriction, cb.like(
					cb.upper(employeeJoin.get(Employee_.employeeNumber)),
					conditionDTO.getEmployeeNumber().toUpperCase() + '%'));
		}

		if (StringUtils.isNotBlank(conditionDTO.getFirstName())) {

			restriction = cb.and(restriction, cb.like(
					cb.upper(employeeJoin.get(Employee_.firstName)),
					conditionDTO.getFirstName().toUpperCase() + '%'));
		}

		restriction = cb.and(restriction,
				cb.equal(employeeMobileDetailsRoot
						.get(EmployeeMobileDetails_.companyId), companyId));

		criteriaQuery.where(restriction);

		TypedQuery<EmployeeMobileDetails> empTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			empTypedQuery.setFirstResult(getStartPosition(pageDTO));
			empTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		List<EmployeeMobileDetails> employeeMobileDetailsList = empTypedQuery
				.getResultList();

		return employeeMobileDetailsList;
	}

	@Override
	public Integer getCountForCondition(
			EmployeeMobileConditionDTO conditionDTO, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Integer> criteriaQuery = cb.createQuery(Integer.class);
		Root<EmployeeMobileDetails> employeeMobileDetailsRoot = criteriaQuery
				.from(EmployeeMobileDetails.class);
		criteriaQuery.select(cb.count(employeeMobileDetailsRoot).as(
				Integer.class));

		Join<EmployeeMobileDetails, EmployeeActivationCode> employeeActivationCodeJoin = employeeMobileDetailsRoot
				.join(EmployeeMobileDetails_.employeeActivationCode);

		Join<EmployeeActivationCode, Employee> employeeJoin = employeeActivationCodeJoin
				.join(EmployeeActivationCode_.employee);

		Predicate restriction = cb.conjunction();

		if (StringUtils.isNotBlank(conditionDTO.getEmployeeNumber())) {

			restriction = cb.and(restriction, cb.like(
					cb.upper(employeeJoin.get(Employee_.employeeNumber)),
					conditionDTO.getEmployeeNumber().toUpperCase() + '%'));
		}

		if (StringUtils.isNotBlank(conditionDTO.getFirstName())) {

			restriction = cb.and(restriction, cb.like(
					cb.upper(employeeJoin.get(Employee_.firstName)),
					conditionDTO.getFirstName().toUpperCase() + '%'));
		}

		restriction = cb.and(restriction,
				cb.equal(employeeMobileDetailsRoot
						.get(EmployeeMobileDetails_.companyId), companyId));

		criteriaQuery.where(restriction);

		TypedQuery<Integer> empTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return empTypedQuery.getSingleResult();
	}

	@Override
	public EmployeeMobileDetails findByEmployeeActivationCode(
			Long employeeActivationCodeId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeMobileDetails> criteriaQuery = cb
				.createQuery(EmployeeMobileDetails.class);
		Root<EmployeeMobileDetails> employeeMobileDetailsRoot = criteriaQuery
				.from(EmployeeMobileDetails.class);
		criteriaQuery.select(employeeMobileDetailsRoot);

		Join<EmployeeMobileDetails, EmployeeActivationCode> employeeActivationCodeJoin = employeeMobileDetailsRoot
				.join(EmployeeMobileDetails_.employeeActivationCode);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(employeeActivationCodeJoin
				.get(EmployeeActivationCode_.employeeActivationCodeId),
				employeeActivationCodeId));

		criteriaQuery.where(restriction);

		TypedQuery<EmployeeMobileDetails> empTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		if (empTypedQuery.getResultList().size() > 0) {
			return empTypedQuery.getSingleResult();
		}

		return null;
	}

	@Override
	public void updateByDeviceId(String deviceID, Long employeeActivationCodeId) {
		String queryString = "UPDATE EmployeeMobileDetails EMD SET EMD.active = :active where EMD.deviceID =:deviceID and EMD.employeeActivationCode.employeeActivationCodeId <> :employeeActivationCodeId";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("active", false);
		q.setParameter("deviceID", deviceID);
		q.setParameter("employeeActivationCodeId", employeeActivationCodeId);
		q.executeUpdate();

	}

	@Override
	public EmployeeMobileDetails findByMobileId(String mobileId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeMobileDetails> criteriaQuery = cb
				.createQuery(EmployeeMobileDetails.class);
		Root<EmployeeMobileDetails> employeeMobileDetailsRoot = criteriaQuery
				.from(EmployeeMobileDetails.class);
		criteriaQuery.select(employeeMobileDetailsRoot);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(
				employeeMobileDetailsRoot.get(EmployeeMobileDetails_.deviceID),
				mobileId));

		restriction = cb.and(restriction, cb.equal(
				employeeMobileDetailsRoot.get(EmployeeMobileDetails_.active),
				true));

		criteriaQuery.where(restriction);

		TypedQuery<EmployeeMobileDetails> empTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		if (empTypedQuery.getResultList().size() > 0) {
			return empTypedQuery.getResultList().get(0);
		}

		return null;
	}

}
