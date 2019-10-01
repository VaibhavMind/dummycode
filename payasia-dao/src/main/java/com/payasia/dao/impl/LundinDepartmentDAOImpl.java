package com.payasia.dao.impl;

import java.lang.reflect.InvocationTargetException;
import java.sql.Date;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.payasia.common.dto.LundinConditionDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.NullAwareBeanUtilsBean;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.LundinDepartmentDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.DynamicFormFieldRefValue_;
import com.payasia.dao.bean.LundinDepartment;
import com.payasia.dao.bean.LundinDepartment_;

@Repository
public class LundinDepartmentDAOImpl extends BaseDAO implements
		LundinDepartmentDAO {

	@Override
	public LundinDepartment findById(long id) {
		return super.findById(LundinDepartment.class, id);
	}

	@Override
	public void save(LundinDepartment department) {
		super.save(department);

	}

	@Override
	public void update(LundinDepartment department) {
		super.update(department);

	}

	@Override
	public long saveAndReturn(LundinDepartment dept) {
		LundinDepartment persistObj = dept;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (LundinDepartment) getBaseEntity();
			beanUtil.copyProperties(persistObj, dept);
		} catch (IllegalAccessException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		} catch (InvocationTargetException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		}
		this.entityManagerFactory.persist(persistObj);
		this.entityManagerFactory.flush();
		return persistObj.getDepartmentId();

	}

	@Override
	public void delete(long id) {
		super.delete(super.findById(LundinDepartment.class, id));

	}

	@Override
	protected Object getBaseEntity() {
		LundinDepartment obj = new LundinDepartment();
		return obj;
	}

	@Override
	public Long getCountForCondition(LundinConditionDTO conditionDTO) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
		Root<LundinDepartment> lundinDepartmentRoot = criteriaQuery
				.from(LundinDepartment.class);
		criteriaQuery.select(cb.count(lundinDepartmentRoot));

		Predicate restriction = cb.conjunction();

		Join<LundinDepartment, Company> compJoin = lundinDepartmentRoot
				.join(LundinDepartment_.company);

		if (StringUtils.isNotBlank(conditionDTO.getCreatedDate())) {
			restriction = cb.and(restriction, cb.equal(lundinDepartmentRoot
					.get(LundinDepartment_.createdDate).as(Date.class),
					DateUtils.stringToDate(conditionDTO.getCreatedDate())));

		}

		if (conditionDTO.getFromDate() != null) {
			restriction = cb.and(restriction,
					cb.greaterThanOrEqualTo((lundinDepartmentRoot
							.get(LundinDepartment_.effectiveDate)),
							conditionDTO.getFromDate()));
		}
		if (conditionDTO.getToDate() != null) {
			restriction = cb.and(restriction,
					cb.lessThanOrEqualTo((lundinDepartmentRoot
							.get(LundinDepartment_.effectiveDate)),
							conditionDTO.getToDate()));
		}
		// if (conditionDTO.getDepartmentCode() != null) {
		// restriction = cb.and(restriction, cb.like(
		// lundinDepartmentRoot.get(LundinDepartment_.departmentCode)
		// .as(String.class), '%' + String
		// .valueOf(conditionDTO.getDepartmentCode()) + '%'));
		//
		// }
		// if (conditionDTO.getDepartmentName() != null) {
		// restriction = cb.and(restriction, cb.like(
		// lundinDepartmentRoot.get(LundinDepartment_.departmentName)
		// .as(String.class), '%' + String
		// .valueOf(conditionDTO.getDepartmentName()) + '%'));
		//
		// }

		criteriaQuery.where(restriction);

		TypedQuery<Long> lundinAppTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return lundinAppTypedQuery.getSingleResult();

	}

	@Override
	public List<LundinDepartment> findByCondition(
			LundinConditionDTO conditionDTO, PageRequest pageDTO,
			SortCondition sortDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LundinDepartment> criteriaQuery = cb
				.createQuery(LundinDepartment.class);
		Root<LundinDepartment> lundinDepartmentRoot = criteriaQuery
				.from(LundinDepartment.class);
		criteriaQuery.select(lundinDepartmentRoot);

		Predicate restriction = cb.conjunction();

		Join<LundinDepartment, Company> compJoin = lundinDepartmentRoot
				.join(LundinDepartment_.company);

		restriction = cb.and(
				restriction,
				cb.equal(compJoin.get(Company_.companyId),
						conditionDTO.getCompanyId()));

		if (conditionDTO.getFromDate() != null) {
			restriction = cb.and(restriction,
					cb.greaterThanOrEqualTo((lundinDepartmentRoot
							.get(LundinDepartment_.effectiveDate)),
							conditionDTO.getFromDate()));
		}
		if (conditionDTO.getToDate() != null) {
			restriction = cb.and(restriction,
					cb.lessThanOrEqualTo((lundinDepartmentRoot
							.get(LundinDepartment_.effectiveDate)),
							conditionDTO.getToDate()));
		}
		// if (conditionDTO.getDepartmentCode() != null) {
		// restriction = cb.and(restriction, cb.like(
		// lundinDepartmentRoot.get(LundinDepartment_.departmentCode)
		// .as(String.class), '%' + String
		// .valueOf(conditionDTO.getDepartmentCode()) + '%'));
		// }
		// if (conditionDTO.getDepartmentName() != null) {
		// restriction = cb.and(restriction, cb.like(
		// lundinDepartmentRoot.get(LundinDepartment_.departmentName)
		// .as(String.class), '%' + String
		// .valueOf(conditionDTO.getDepartmentName()) + '%'));
		// }
		if (StringUtils.isNotBlank(conditionDTO.getTransactionType())) {
			restriction = cb.and(restriction, cb.equal(lundinDepartmentRoot
					.get(LundinDepartment_.status).as(Boolean.class),
					conditionDTO.isStatus()));
		}
		criteriaQuery.where(restriction);

		criteriaQuery.orderBy(cb.asc(lundinDepartmentRoot
				.get(LundinDepartment_.order)));

		TypedQuery<LundinDepartment> claimAppTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			claimAppTypedQuery.setFirstResult(getStartPosition(pageDTO));
			claimAppTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		return claimAppTypedQuery.getResultList();
	}

	@Override
	public LundinDepartment findByDescCompany(Long deptId,
			String departmentCode, long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LundinDepartment> criteriaQuery = cb
				.createQuery(LundinDepartment.class);

		Root<LundinDepartment> lundinDepartmentRoot = criteriaQuery
				.from(LundinDepartment.class);

		criteriaQuery.select(lundinDepartmentRoot);

		Join<LundinDepartment, Company> departmentCompanyJoin = lundinDepartmentRoot
				.join(LundinDepartment_.company);

		Predicate restriction = cb.equal(
				departmentCompanyJoin.get(Company_.companyId), companyId);

		if (deptId != null) {
			restriction = cb.and(restriction, cb.notEqual(
					lundinDepartmentRoot.get(LundinDepartment_.departmentId),
					deptId));
		}

		// restriction = cb.and(restriction, cb.equal(cb
		// .upper(lundinDepartmentRoot
		// .get(LundinDepartment_.departmentCode)), departmentCode
		// .toUpperCase()));

		criteriaQuery.where(restriction);

		TypedQuery<LundinDepartment> deptTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<LundinDepartment> deptList = deptTypedQuery.getResultList();
		if (deptList != null && !deptList.isEmpty()) {
			return deptList.get(0);
		}
		return null;
	}

	@Override
	public LundinDepartment findByDynamicFieldId(long dynamicFieldId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LundinDepartment> criteriaQuery = cb
				.createQuery(LundinDepartment.class);

		Root<LundinDepartment> lundinDepartmentRoot = criteriaQuery
				.from(LundinDepartment.class);

		criteriaQuery.select(lundinDepartmentRoot);
		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(
				lundinDepartmentRoot.get(
						LundinDepartment_.dynamicFormFieldRefValue).get(
						DynamicFormFieldRefValue_.fieldRefValueId),
				dynamicFieldId));

		criteriaQuery.where(restriction);

		TypedQuery<LundinDepartment> deptTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		deptTypedQuery.setMaxResults(1);
		try {
			return deptTypedQuery.getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public List<LundinDepartment> getAllEntries(Long compId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LundinDepartment> criteriaQuery = cb
				.createQuery(LundinDepartment.class);

		Root<LundinDepartment> lundinDepartmentRoot = criteriaQuery
				.from(LundinDepartment.class);

		criteriaQuery.select(lundinDepartmentRoot);
		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(
				lundinDepartmentRoot.get(LundinDepartment_.company).get(
						Company_.companyId), compId));

		criteriaQuery.where(restriction);

		TypedQuery<LundinDepartment> deptTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		return deptTypedQuery.getResultList();
	}

	@Override
	public List<LundinDepartment> findByCondition(Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LundinDepartment> criteriaQuery = cb
				.createQuery(LundinDepartment.class);
		Root<LundinDepartment> lundinDepartmentRoot = criteriaQuery
				.from(LundinDepartment.class);
		criteriaQuery.select(lundinDepartmentRoot);

		Predicate restriction = cb.conjunction();

		Join<LundinDepartment, Company> compJoin = lundinDepartmentRoot
				.join(LundinDepartment_.company);

		restriction = cb.and(restriction,
				cb.equal(compJoin.get(Company_.companyId), companyId));

		restriction = cb.and(restriction, cb.equal(
				lundinDepartmentRoot.get(LundinDepartment_.status), true));
		criteriaQuery.where(restriction);

		criteriaQuery.orderBy(cb.asc(lundinDepartmentRoot
				.get(LundinDepartment_.order)));

		TypedQuery<LundinDepartment> claimAppTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return claimAppTypedQuery.getResultList();
	}
	
	@Override
	public LundinDepartment findByDepartmentCompanyId(Long deptId,long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LundinDepartment> criteriaQuery = cb
				.createQuery(LundinDepartment.class);

		Root<LundinDepartment> lundinDepartmentRoot = criteriaQuery
				.from(LundinDepartment.class);

		criteriaQuery.select(lundinDepartmentRoot);

		Join<LundinDepartment, Company> departmentCompanyJoin = lundinDepartmentRoot
				.join(LundinDepartment_.company);

		Predicate restriction = cb.equal(
				departmentCompanyJoin.get(Company_.companyId), companyId);

		if (deptId != null) {
			restriction = cb.and(restriction, cb.equal(
					lundinDepartmentRoot.get(LundinDepartment_.departmentId),
					deptId));
		}

		criteriaQuery.where(restriction);

		TypedQuery<LundinDepartment> deptTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<LundinDepartment> deptList = deptTypedQuery.getResultList();
		if (deptList != null && !deptList.isEmpty()) {
			return deptList.get(0);
		}
		return null;
	}

	}
