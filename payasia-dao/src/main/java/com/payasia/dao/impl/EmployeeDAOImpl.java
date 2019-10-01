package com.payasia.dao.impl;

import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Resource;
import javax.persistence.Query;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.persistence.criteria.Subquery;

import org.apache.commons.lang.StringUtils;
import org.hibernate.jdbc.Work;
import org.springframework.stereotype.Repository;

import com.payasia.common.dto.AccessControlConditionDTO;
import com.payasia.common.dto.CodeDescDTO;
import com.payasia.common.dto.ColumnPropertyDTO;
import com.payasia.common.dto.DataImportKeyValueDTO;
import com.payasia.common.dto.EmployeeConditionDTO;
import com.payasia.common.dto.EmployeeShortListDTO;
import com.payasia.common.dto.ExcelExportQueryDTO;
import com.payasia.common.dto.ManageRolesConditionDTO;
import com.payasia.common.dto.SortConditionDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.EmployeeListForm;
import com.payasia.common.form.ExcelExportFiltersForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.NullAwareBeanUtilsBean;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.SortConstants;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.GeneralDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.CompanyGroup;
import com.payasia.dao.bean.CompanyGroup_;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeCalendarConfig;
import com.payasia.dao.bean.EmployeeCalendarConfig_;
import com.payasia.dao.bean.EmployeeClaimAdjustment;
import com.payasia.dao.bean.EmployeeClaimAdjustment_;
import com.payasia.dao.bean.EmployeeClaimTemplate;
import com.payasia.dao.bean.EmployeeClaimTemplate_;
import com.payasia.dao.bean.EmployeeLeaveScheme;
import com.payasia.dao.bean.EmployeeLeaveSchemeType;
import com.payasia.dao.bean.EmployeeLeaveSchemeType_;
import com.payasia.dao.bean.EmployeeLeaveScheme_;
import com.payasia.dao.bean.EmployeeLoginDetail;
import com.payasia.dao.bean.EmployeeLoginDetail_;
import com.payasia.dao.bean.EmployeeRoleMapping;
import com.payasia.dao.bean.EmployeeRoleMapping_;
import com.payasia.dao.bean.Employee_;
import com.payasia.dao.bean.LeaveApplicationReviewer;
import com.payasia.dao.bean.LeaveApplicationReviewer_;
import com.payasia.dao.bean.LeaveScheme;
import com.payasia.dao.bean.LeaveSchemeType;
import com.payasia.dao.bean.LeaveSchemeType_;
import com.payasia.dao.bean.LeaveScheme_;
import com.payasia.dao.bean.LeaveTypeMaster;
import com.payasia.dao.bean.LeaveTypeMaster_;
import com.payasia.dao.bean.RoleMaster;
import com.payasia.dao.bean.RoleMaster_;

/**
 * The Class EmployeeDAOImpl.
 * 
 * @author ragulapraveen
 */
@Repository
public class EmployeeDAOImpl extends BaseDAO implements EmployeeDAO {

	@Resource
	GeneralDAO generalDAO;

	@Resource
	CompanyDAO companyDAO;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.EmployeeDAO#findAll(long,
	 * com.payasia.common.form.PageRequest,
	 * com.payasia.common.form.SortCondition)
	 */
	@Override
	public List<Employee> findAll(long companyId, PageRequest pageDTO, SortCondition sortDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Employee> criteriaQuery = cb.createQuery(Employee.class);
		Root<Employee> empRoot = criteriaQuery.from(Employee.class);

		criteriaQuery.select(empRoot);

		Join<Employee, Company> empCompanyJoin = empRoot.join(Employee_.company);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(empCompanyJoin.get(Company_.companyId), companyId));
		criteriaQuery.where(restriction);
		if (sortDTO != null) {

			Path<String> sortPath = getSortPathForAllEmployee(sortDTO, empRoot, empCompanyJoin);
			if (sortPath != null) {
				if (SortConstants.DB_ORDER_BY_ASC.equalsIgnoreCase(sortDTO.getOrderType())) {
					criteriaQuery.orderBy(cb.asc(sortPath));
				}
				if (SortConstants.DB_ORDER_BY_DESC.equalsIgnoreCase(sortDTO.getOrderType())) {
					criteriaQuery.orderBy(cb.desc(sortPath));
				}
			}

		}

		TypedQuery<Employee> empTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			empTypedQuery.setFirstResult(getStartPosition(pageDTO));
			empTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		return empTypedQuery.getResultList();
	}

	@Override
	public List<Employee> findByShortlist(long companyId, Long employeeId, EmployeeShortListDTO employeeShortListDTO,
			PageRequest pageDTO, SortCondition sortDTO, ManageRolesConditionDTO conditionDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Employee> criteriaQuery = cb.createQuery(Employee.class);
		Root<Employee> empRoot = criteriaQuery.from(Employee.class);

		criteriaQuery.select(empRoot);

		Join<Employee, Company> empCompanyJoin = empRoot.join(Employee_.company);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(empCompanyJoin.get(Company_.companyId), companyId));
		if (employeeId != null) {
			restriction = cb.and(restriction, cb.notEqual(empRoot.get(Employee_.employeeId), employeeId));
		}

		if (StringUtils.isNotBlank(conditionDTO.getEmployeeNumber())
				|| StringUtils.isNotBlank(conditionDTO.getUsername())) {
			restriction = cb.and(restriction,
					cb.like(empRoot.get(Employee_.employeeNumber), conditionDTO.getEmployeeNumber() + '%'));
		}

		if (StringUtils.isNotBlank(conditionDTO.getFirstName())) {

			restriction = cb.and(restriction,
					cb.like(empRoot.get(Employee_.firstName), conditionDTO.getFirstName() + '%'));
		}
		if (StringUtils.isNotBlank(conditionDTO.getLastName())) {
			restriction = cb.and(restriction,
					cb.like(empRoot.get(Employee_.lastName), conditionDTO.getLastName() + '%'));

		}

		if (employeeShortListDTO.getEmployeeShortList() && employeeShortListDTO.getShortListEmployeeIds().isEmpty()) {

			restriction = cb.and(restriction, empRoot.get(Employee_.employeeId).in(-1));

		} else if (employeeShortListDTO.getEmployeeShortList()
				&& !employeeShortListDTO.getShortListEmployeeIds().isEmpty()) {
			restriction = cb.and(restriction,
					empRoot.get(Employee_.employeeId).in(employeeShortListDTO.getShortListEmployeeIds()));

		}
		restriction = cb.and(restriction, cb.equal(empRoot.get(Employee_.status), true));
		criteriaQuery.where(restriction);
		if (sortDTO != null) {

			Path<String> sortPath = getSortPathForAllEmployee(sortDTO, empRoot, empCompanyJoin);
			if (sortPath != null) {
				if (SortConstants.DB_ORDER_BY_ASC.equalsIgnoreCase(sortDTO.getOrderType())) {
					criteriaQuery.orderBy(cb.asc(sortPath));
				}
				if (SortConstants.DB_ORDER_BY_DESC.equalsIgnoreCase(sortDTO.getOrderType())) {
					criteriaQuery.orderBy(cb.desc(sortPath));
				}
			}

		}

		TypedQuery<Employee> empTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			empTypedQuery.setFirstResult(getStartPosition(pageDTO));
			empTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		return empTypedQuery.getResultList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.EmployeeDAO#checkForEmployeeDocuments(java.lang.String)
	 */public EmployeeDAOImpl() {

	}

	@Override
	public void addOrder(CriteriaBuilder cb, List<Order> orderList, SortConditionDTO sortDTO, Path<String> sortPath) {

		super.addOrder(cb, orderList, sortDTO, sortPath);
	}

	@Override
	public List<BigInteger> checkForEmployeeDocuments(String queryString, Map<String, String> paramValueMap,
			Long employeeId, Long companyId) {
		Query q = entityManagerFactory.createNativeQuery(queryString);
		if (employeeId != null) {
			q.setParameter("employeeId", employeeId);
		}
		if (companyId != null) {
			q.setParameter("companyId", companyId);
		}
		for (Map.Entry<String, String> entry : paramValueMap.entrySet()) {
			if (entry.getKey().startsWith("sysdateParam")) {
				q.setParameter(entry.getKey(), Integer.parseInt(entry.getValue()));
			} else {
				q.setParameter(entry.getKey(), entry.getValue());
			}
		}

		return q.getResultList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.EmployeeDAO#getSortPathForAllEmployee(com.payasia.common
	 * .form.SortCondition, javax.persistence.criteria.Root,
	 * javax.persistence.criteria.Join)
	 */
	@Override
	public Path<String> getSortPathForAllEmployee(SortCondition sortDTO, Root<Employee> empRoot,
			Join<Employee, Company> empCompanyJoin) {

		List<String> employeeIsIdList = new ArrayList<String>();
		employeeIsIdList.add(SortConstants.EMPLOYEE_ID);

		List<String> employeeIsColList = new ArrayList<String>();
		employeeIsColList.add(SortConstants.EMPLOYEE_NUMBER);
		employeeIsColList.add(SortConstants.EMPLOYEE_FIRST_NAME);
		employeeIsColList.add(SortConstants.EMPLOYEE_LAST_NAME);
		employeeIsColList.add(SortConstants.EMPLOYEE_USER_NAME);
		employeeIsColList.add(SortConstants.EMPLOYEE_NAME);
		employeeIsColList.add(SortConstants.EMPLOYEE_JOIN_DATE);

		List<String> companyIsIdList = new ArrayList<String>();
		companyIsIdList.add(SortConstants.EMPLOYEE_COMPANY_ID);

		List<String> companyIsColList = new ArrayList<String>();
		companyIsColList.add(SortConstants.EMPLOYEE_COMPANY_NAME);

		Path<String> sortPath = null;

		if (employeeIsColList.contains(sortDTO.getColumnName())) {
			sortPath = empRoot.get(colMap.get(Employee.class + sortDTO.getColumnName()));
		}
		if (companyIsColList.contains(sortDTO.getColumnName())) {
			sortPath = empCompanyJoin.get(colMap.get(Company.class + sortDTO.getColumnName()));
		}
		return sortPath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.EmployeeDAO#findByRole(long, long)
	 */
	@Override
	public List<Employee> findByRole(long roleId, long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Employee> criteriaQuery = cb.createQuery(Employee.class);
		Root<Employee> employeeRoot = criteriaQuery.from(Employee.class);

		criteriaQuery.select(employeeRoot);
		Join<Employee, EmployeeRoleMapping> empRoleMappingJoin = employeeRoot.join(Employee_.employeeRoleMappings);

		Join<EmployeeRoleMapping, RoleMaster> empRoleMappingRoleJoin = empRoleMappingJoin
				.join(EmployeeRoleMapping_.roleMaster);

		Join<EmployeeRoleMapping, Company> empRoleMappingCompanyJoin = empRoleMappingJoin
				.join(EmployeeRoleMapping_.company);

		Join<Employee, Company> empCompanyJoin = employeeRoot.join(Employee_.company);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(empRoleMappingRoleJoin.get(RoleMaster_.roleId), roleId));

		restriction = cb.and(restriction, cb.equal(empCompanyJoin.get(Company_.companyId), companyId));

		restriction = cb.and(restriction, cb.equal(empRoleMappingCompanyJoin.get(Company_.companyId), companyId));

		criteriaQuery.where(restriction);

		criteriaQuery.orderBy(cb.asc(employeeRoot.get(Employee_.employeeId)));

		TypedQuery<Employee> employeeTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		return employeeTypedQuery.getResultList();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.EmployeeDAO#findByRoleForPayAsiaUsers(long, long)
	 */
	@Override
	public List<Employee> findByRoleForPayAsiaUsers(long roleId, long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Employee> criteriaQuery = cb.createQuery(Employee.class);
		Root<Employee> employeeRoot = criteriaQuery.from(Employee.class);

		criteriaQuery.select(employeeRoot);

		Join<Employee, Company> empCompanyJoin = employeeRoot.join(Employee_.company);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(empCompanyJoin.get(Company_.companyId), companyId));

		Subquery<Long> subquery = criteriaQuery.subquery(Long.class);
		Root<EmployeeRoleMapping> empRoleMappingRoot = subquery.from(EmployeeRoleMapping.class);
		subquery.select(empRoleMappingRoot.get(EmployeeRoleMapping_.id).get("employeeId").as(Long.class))
				.distinct(true);
		Join<EmployeeRoleMapping, RoleMaster> empRoleMappingRoleSubJoin = empRoleMappingRoot
				.join(EmployeeRoleMapping_.roleMaster);

		Predicate subRestriction = cb.conjunction();

		subRestriction = cb.and(subRestriction, cb.equal(empRoleMappingRoleSubJoin.get(RoleMaster_.roleId), roleId));
		subquery.where(subRestriction);

		restriction = cb.and(restriction, cb.in(employeeRoot.get(Employee_.employeeId)).value(subquery));

		criteriaQuery.where(restriction);

		TypedQuery<Employee> employeeTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		return employeeTypedQuery.getResultList();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.EmployeeDAO#findByCondition(com.payasia.common.dto.
	 * AccessControlConditionDTO, com.payasia.common.form.PageRequest,
	 * com.payasia.common.form.SortCondition, java.lang.Long)
	 */
	@Override
	public List<Employee> findByCondition(AccessControlConditionDTO accessControlconditionDTO, PageRequest pageDTO,
			SortCondition sortDTO, Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Employee> criteriaQuery = cb.createQuery(Employee.class);
		Root<Employee> empRoot = criteriaQuery.from(Employee.class);
		criteriaQuery.select(empRoot);

		Predicate restriction = cb.conjunction();

		Join<Employee, Company> empCompanyJoin = empRoot.join(Employee_.company);

		Path<Long> companyID = empCompanyJoin.get(Company_.companyId);

		Path<Long> employeeID = empRoot.get(Employee_.employeeId);

		restriction = cb.and(restriction, cb.equal(companyID, companyId));

		if (StringUtils.isNotBlank(accessControlconditionDTO.getEmployeeNumber())) {

			restriction = cb.and(restriction,
					cb.like(empRoot.get(Employee_.employeeNumber), accessControlconditionDTO.getEmployeeNumber()));
		}

		if (StringUtils.isNotBlank(accessControlconditionDTO.getFirstName())) {

			restriction = cb.and(restriction, cb.like(cb.upper(empRoot.get(Employee_.firstName)),
					accessControlconditionDTO.getFirstName().toUpperCase()));
		}

		if (StringUtils.isNotBlank(accessControlconditionDTO.getLastName())) {

			restriction = cb.and(restriction, cb.like(cb.upper(empRoot.get(Employee_.lastName)),
					accessControlconditionDTO.getLastName().toUpperCase()));
		}

		if (StringUtils.isNotBlank(accessControlconditionDTO.getEmail())) {

			restriction = cb.and(restriction, cb.like(cb.upper(empRoot.get(Employee_.email)),
					accessControlconditionDTO.getEmail().toUpperCase()));
		}

		if (accessControlconditionDTO.getEmployeeShortListDTO().getEmployeeShortList()
				&& accessControlconditionDTO.getEmployeeShortListDTO().getShortListEmployeeIds().isEmpty()) {

			restriction = cb.and(restriction, employeeID.in(-1));

		} else if (accessControlconditionDTO.getEmployeeShortListDTO().getEmployeeShortList()
				&& !accessControlconditionDTO.getEmployeeShortListDTO().getShortListEmployeeIds().isEmpty()) {
			restriction = cb.and(restriction,
					employeeID.in(accessControlconditionDTO.getEmployeeShortListDTO().getShortListEmployeeIds()));

		}

		if (StringUtils.isNotBlank(accessControlconditionDTO.getEmployeeStatus())) {
			if ("enabled".equals(accessControlconditionDTO.getEmployeeStatus())) {
				restriction = cb.and(restriction, cb.equal((empRoot.get(Employee_.status)), true));
			}
			if (accessControlconditionDTO.getEmployeeStatus().equals("disabled")) {
				restriction = cb.and(restriction, cb.equal((empRoot.get(Employee_.status)), false));
			}

		}
		criteriaQuery.where(restriction);

		if (sortDTO != null) {

			Path<String> sortPath = getSortPathForSearchEmployee(sortDTO, empRoot);
			if (sortPath != null) {
				if (SortConstants.DB_ORDER_BY_ASC.equalsIgnoreCase(sortDTO.getOrderType())) {
					criteriaQuery.orderBy(cb.asc(sortPath));
				}
				if (SortConstants.DB_ORDER_BY_DESC.equalsIgnoreCase(sortDTO.getOrderType())) {
					criteriaQuery.orderBy(cb.desc(sortPath));
				}
			}

		}

		TypedQuery<Employee> empTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			empTypedQuery.setFirstResult(getStartPosition(pageDTO));
			empTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		return empTypedQuery.getResultList();

	}

	@Override
	public List<Employee> findEmpForSendPassword(AccessControlConditionDTO accessControlconditionDTO,
			PageRequest pageDTO, SortCondition sortDTO, Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Employee> criteriaQuery = cb.createQuery(Employee.class);
		Root<Employee> empRoot = criteriaQuery.from(Employee.class);
		criteriaQuery.select(empRoot);

		Predicate restriction = cb.conjunction();

		Join<Employee, Company> empCompanyJoin = empRoot.join(Employee_.company);

		Join<Employee, EmployeeLoginDetail> loginDetailJoin = empRoot.join(Employee_.employeeLoginDetails);

		Path<Long> companyID = empCompanyJoin.get(Company_.companyId);

		Path<Long> employeeID = empRoot.get(Employee_.employeeId);

		restriction = cb.and(restriction, cb.equal(companyID, companyId));

		if (StringUtils.isNotBlank(accessControlconditionDTO.getEmployeeNumber())) {

			restriction = cb.and(restriction,
					cb.like(empRoot.get(Employee_.employeeNumber), accessControlconditionDTO.getEmployeeNumber()));
		}

		if (StringUtils.isNotBlank(accessControlconditionDTO.getFirstName())) {

			restriction = cb.and(restriction, cb.like(cb.upper(empRoot.get(Employee_.firstName)),
					accessControlconditionDTO.getFirstName().toUpperCase()));
		}

		if (StringUtils.isNotBlank(accessControlconditionDTO.getLastName())) {

			restriction = cb.and(restriction, cb.like(cb.upper(empRoot.get(Employee_.lastName)),
					accessControlconditionDTO.getLastName().toUpperCase()));
		}

		if (StringUtils.isNotBlank(accessControlconditionDTO.getEmail())) {

			restriction = cb.and(restriction, cb.like(cb.upper(empRoot.get(Employee_.email)),
					accessControlconditionDTO.getEmail().toUpperCase()));
		}

		if (accessControlconditionDTO.getEmployeeShortListDTO().getEmployeeShortList()
				&& accessControlconditionDTO.getEmployeeShortListDTO().getShortListEmployeeIds().isEmpty()) {

			restriction = cb.and(restriction, employeeID.in(-1));

		} else if (accessControlconditionDTO.getEmployeeShortListDTO().getEmployeeShortList()
				&& !accessControlconditionDTO.getEmployeeShortListDTO().getShortListEmployeeIds().isEmpty()) {
			restriction = cb.and(restriction,
					employeeID.in(accessControlconditionDTO.getEmployeeShortListDTO().getShortListEmployeeIds()));

		}

		restriction = cb.and(restriction, cb.equal((empRoot.get(Employee_.status)), true));

		restriction = cb.and(restriction, cb.equal((loginDetailJoin.get(EmployeeLoginDetail_.passwordSent)), false));
		criteriaQuery.where(restriction);

		if (sortDTO != null) {

			Path<String> sortPath = getSortPathForSearchEmployee(sortDTO, empRoot);
			if (sortPath != null) {
				if (SortConstants.DB_ORDER_BY_ASC.equalsIgnoreCase(sortDTO.getOrderType())) {
					criteriaQuery.orderBy(cb.asc(sortPath));
				}
				if (SortConstants.DB_ORDER_BY_DESC.equalsIgnoreCase(sortDTO.getOrderType())) {
					criteriaQuery.orderBy(cb.desc(sortPath));
				}
			}

		}

		TypedQuery<Employee> empTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			empTypedQuery.setFirstResult(getStartPosition(pageDTO));
			empTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		return empTypedQuery.getResultList();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.EmployeeDAO#getSortPathForSearchEmployee(com.payasia.
	 * common.form.SortCondition, javax.persistence.criteria.Root)
	 */
	@Override
	public Path<String> getSortPathForSearchEmployee(SortCondition sortDTO, Root<Employee> empRoot) {

		List<String> employeeIsIdList = new ArrayList<String>();
		employeeIsIdList.add(SortConstants.EMPLOYEE_ID);

		List<String> employeeIsColList = new ArrayList<String>();
		employeeIsColList.add(SortConstants.EMPLOYEE_NUMBER);
		employeeIsColList.add(SortConstants.EMPLOYEE_FIRST_NAME);
		employeeIsColList.add(SortConstants.EMPLOYEE_LAST_NAME);
		employeeIsColList.add(SortConstants.EMPLOYEE_NAME);
		employeeIsColList.add(SortConstants.EMPLOYEE_STATUS);
		employeeIsColList.add(SortConstants.EMPLOYEE_STATUS_MSG);
		employeeIsColList.add(SortConstants.EMPLOYEE_PHONE);
		employeeIsColList.add(SortConstants.EMPLOYEE_DOB);
		employeeIsColList.add(SortConstants.EMPLOYEE_EMAIL);
		employeeIsColList.add(SortConstants.LEAVE_GRANTER_HIRE_DATE);
		employeeIsColList.add(SortConstants.LEAVE_GRANTER_RESIGNATION_DATE);

		Path<String> sortPath = null;

		if (employeeIsColList.contains(sortDTO.getColumnName())) {
			sortPath = empRoot.get(colMap.get(Employee.class + sortDTO.getColumnName()));
		}
		return sortPath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.BaseDAO#getBaseEntity()
	 */
	@Override
	protected Object getBaseEntity() {
		Employee employee = new Employee();
		return employee;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.EmployeeDAO#findByCondition(com.payasia.common.dto.
	 * EmployeeConditionDTO, com.payasia.common.form.PageRequest,
	 * com.payasia.common.form.SortCondition, java.lang.Long)
	 */
	@Override
	public List<Employee> findByCondition(EmployeeConditionDTO conditionDTO, PageRequest pageDTO, SortCondition sortDTO,
			Long companyId, Long employeeId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Employee> criteriaQuery = cb.createQuery(Employee.class);
		Root<Employee> empRoot = criteriaQuery.from(Employee.class);
		criteriaQuery.select(empRoot);

		Join<Employee, Company> empCompanyJoin = empRoot.join(Employee_.company);

		Predicate restriction = cb.conjunction();

		if (StringUtils.isNotBlank(conditionDTO.getEmployeeNumber())) {

			restriction = cb.and(restriction, cb.like(cb.upper(empRoot.get(Employee_.employeeNumber)),
					conditionDTO.getEmployeeNumber().toUpperCase() + '%'));
		}

		if (StringUtils.isNotBlank(conditionDTO.getFirstName())) {

			restriction = cb.and(restriction, cb.like(cb.upper(empRoot.get(Employee_.firstName)),
					conditionDTO.getFirstName().toUpperCase() + '%'));
		}
		if (StringUtils.isNotBlank(conditionDTO.getLastName())) {

			restriction = cb.and(restriction,
					cb.like(cb.upper(empRoot.get(Employee_.lastName)), conditionDTO.getLastName().toUpperCase() + '%'));
		}

		if (StringUtils.isNotBlank(conditionDTO.getEmployeeName())) {

			restriction = cb.and(restriction, cb.like(cb.upper(empRoot.get(Employee_.firstName)),
					conditionDTO.getEmployeeName().toUpperCase() + '%'));

			restriction = cb.or(restriction, cb.like(cb.upper(empRoot.get(Employee_.lastName)),
					conditionDTO.getEmployeeName().toUpperCase() + '%'));
		}

		if (conditionDTO.getEmployeeShortListDTO() != null) {
			if (conditionDTO.getEmployeeShortListDTO().getEmployeeShortList()
					&& conditionDTO.getEmployeeShortListDTO().getShortListEmployeeIds().isEmpty()) {

				restriction = cb.and(restriction, empRoot.get("employeeId").in(-1));

			} else if (conditionDTO.getEmployeeShortListDTO().getEmployeeShortList()
					&& !conditionDTO.getEmployeeShortListDTO().getShortListEmployeeIds().isEmpty()) {
				restriction = cb.and(restriction,
						empRoot.get("employeeId").in(conditionDTO.getEmployeeShortListDTO().getShortListEmployeeIds()));
			}
		}

		if (employeeId != -1L) {
			restriction = cb.and(restriction, cb.notEqual(empRoot.get(Employee_.employeeId), employeeId));
		}
		restriction = cb.and(restriction, cb.equal(empCompanyJoin.get(Company_.companyId), companyId));
		if (conditionDTO.getStatus() == null || conditionDTO.getStatus() == false) {
			restriction = cb.and(restriction, cb.equal(empRoot.get(Employee_.status), true));
		}
		if (StringUtils.isNotBlank(conditionDTO.getRoleName())) {
			Subquery<Long> subquery = criteriaQuery.subquery(Long.class);
			Root<EmployeeRoleMapping> employeeRoleMap = subquery.from(EmployeeRoleMapping.class);
			subquery.select(employeeRoleMap.get(EmployeeRoleMapping_.employee).get("employeeId").as(Long.class));

			Path<String> empRole = employeeRoleMap.get(EmployeeRoleMapping_.roleMaster).get("roleName");

			Predicate subRestriction = cb.conjunction();

			subRestriction = cb.and(subRestriction, cb.equal(empRole, conditionDTO.getRoleName()));

			subquery.where(subRestriction);

			restriction = cb.and(restriction, cb.not(cb.in(empRoot.get(Employee_.employeeId)).value(subquery)));

		}

		criteriaQuery.where(restriction);

		if (sortDTO != null) {

			Path<String> sortPath = getSortPathForSearchEmployee(sortDTO, empRoot);
			if (sortPath != null) {
				if (SortConstants.DB_ORDER_BY_ASC.equalsIgnoreCase(sortDTO.getOrderType())) {
					criteriaQuery.orderBy(cb.asc(sortPath));
				}
				if (SortConstants.DB_ORDER_BY_DESC.equalsIgnoreCase(sortDTO.getOrderType())) {
					criteriaQuery.orderBy(cb.desc(sortPath));
				}
			}

		}

		TypedQuery<Employee> empTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			empTypedQuery.setFirstResult(getStartPosition(pageDTO));
			empTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		List<Employee> employeeList = empTypedQuery.getResultList();

		return employeeList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.EmployeeDAO#findByCondition(com.payasia.common.dto.
	 * EmployeeConditionDTO, com.payasia.common.form.PageRequest,
	 * com.payasia.common.form.SortCondition, java.lang.Long)
	 */
	@Override
	public List<Employee> findEmployeesOfGroupCompanies(EmployeeConditionDTO conditionDTO, PageRequest pageDTO,
			SortCondition sortDTO, Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Employee> criteriaQuery = cb.createQuery(Employee.class);
		Root<Employee> empRoot = criteriaQuery.from(Employee.class);
		criteriaQuery.select(empRoot);

		Join<Employee, Company> empCompanyJoin = empRoot.join(Employee_.company);

		Join<Company, CompanyGroup> groupJoin = empCompanyJoin.join(Company_.companyGroup);

		Predicate restriction = cb.conjunction();

		if (StringUtils.isNotBlank(conditionDTO.getEmployeeNumber())) {

			restriction = cb.and(restriction, cb.like(cb.upper(empRoot.get(Employee_.employeeNumber)),
					conditionDTO.getEmployeeNumber().toUpperCase() + '%'));
		}

		if (StringUtils.isNotBlank(conditionDTO.getEmployeeName())) {

			restriction = cb.and(restriction,
					cb.or(cb.like(cb.upper(empRoot.get(Employee_.firstName)),
							conditionDTO.getEmployeeName().toUpperCase() + '%'),
							cb.like(cb.upper(empRoot.get(Employee_.lastName)),
									conditionDTO.getEmployeeName().toUpperCase() + '%')));
		}

		restriction = cb.and(restriction, cb.equal(empRoot.get(Employee_.status), true));

		restriction = cb.and(restriction, cb.equal(groupJoin.get(CompanyGroup_.groupId), conditionDTO.getGroupId()));
		criteriaQuery.where(restriction);

		if (sortDTO != null) {

			Path<String> sortPath = getSortPathForSearchEmployee(sortDTO, empRoot);
			if (sortPath != null) {
				if (SortConstants.DB_ORDER_BY_ASC.equalsIgnoreCase(sortDTO.getOrderType())) {
					criteriaQuery.orderBy(cb.asc(sortPath));
				}
				if (SortConstants.DB_ORDER_BY_DESC.equalsIgnoreCase(sortDTO.getOrderType())) {
					criteriaQuery.orderBy(cb.desc(sortPath));
				}
			}

		}

		TypedQuery<Employee> empTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			empTypedQuery.setFirstResult(getStartPosition(pageDTO));
			empTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		List<Employee> employeeList = empTypedQuery.getResultList();

		return employeeList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.EmployeeDAO#findByCondition(com.payasia.common.dto.
	 * EmployeeConditionDTO, com.payasia.common.form.PageRequest,
	 * com.payasia.common.form.SortCondition)
	 */
	@Override
	public List<Employee> findByCondition(EmployeeConditionDTO conditionDTO, PageRequest pageDTO,
			SortCondition sortDTO) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Employee> criteriaQuery = cb.createQuery(Employee.class);
		Root<Employee> empRoot = criteriaQuery.from(Employee.class);
		criteriaQuery.select(empRoot);

		Predicate restriction = cb.conjunction();

		if (StringUtils.isNotBlank(conditionDTO.getEmployeeNumber())) {

			restriction = cb.and(restriction, cb.like(cb.upper(empRoot.get(Employee_.employeeNumber)),
					conditionDTO.getEmployeeNumber().toUpperCase()));
		}

		if (StringUtils.isNotBlank(conditionDTO.getEmployeeName())) {

			restriction = cb.and(restriction, cb.or(
					cb.like(cb.upper(empRoot.get(Employee_.firstName)), conditionDTO.getEmployeeName().toUpperCase()),
					cb.like(cb.upper(empRoot.get(Employee_.lastName)), conditionDTO.getEmployeeName().toUpperCase())));
		}
		criteriaQuery.where(restriction);

		if (sortDTO != null) {

			Path<String> sortPath = getSortPathForSearchEmployee(sortDTO, empRoot);
			if (sortPath != null) {
				if (SortConstants.DB_ORDER_BY_ASC.equalsIgnoreCase(sortDTO.getOrderType())) {
					criteriaQuery.orderBy(cb.asc(sortPath));
				}
				if (SortConstants.DB_ORDER_BY_DESC.equalsIgnoreCase(sortDTO.getOrderType())) {
					criteriaQuery.orderBy(cb.desc(sortPath));
				}
			}

		}

		TypedQuery<Employee> empTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			empTypedQuery.setFirstResult(getStartPosition(pageDTO));
			empTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		List<Employee> employeeList = empTypedQuery.getResultList();

		return employeeList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.EmployeeDAO#findByConditionCompany(java.lang.Long,
	 * com.payasia.common.dto.EmployeeConditionDTO,
	 * com.payasia.common.form.PageRequest,
	 * com.payasia.common.form.SortCondition)
	 */
	@Override
	public List<Employee> findByConditionCompany(Long companyId, EmployeeConditionDTO conditionDTO, PageRequest pageDTO,
			SortCondition sortDTO) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Employee> criteriaQuery = cb.createQuery(Employee.class);
		Root<Employee> empRoot = criteriaQuery.from(Employee.class);
		criteriaQuery.select(empRoot);

		Join<Employee, Company> empRootCompanyJoin = empRoot.join(Employee_.company);

		Predicate restriction = cb.conjunction();

		if (StringUtils.isNotBlank(conditionDTO.getEmployeeNumber())) {

			restriction = cb.and(restriction, cb.like(cb.upper(empRoot.get(Employee_.employeeNumber)),
					conditionDTO.getEmployeeNumber().toUpperCase()));
		}

		if (StringUtils.isNotBlank(conditionDTO.getEmployeeName())) {

			restriction = cb.and(restriction, cb.or(
					cb.like(cb.upper(empRoot.get(Employee_.firstName)), conditionDTO.getEmployeeName().toUpperCase()),
					cb.like(cb.upper(empRoot.get(Employee_.lastName)), conditionDTO.getEmployeeName().toUpperCase())));
		}

		restriction = cb.and(restriction, cb.equal(empRootCompanyJoin.get(Company_.companyId), companyId));

		criteriaQuery.where(restriction);

		if (sortDTO != null) {

			Path<String> sortPath = getSortPathForSearchEmployee(sortDTO, empRoot);
			if (sortPath != null) {
				if (SortConstants.DB_ORDER_BY_ASC.equalsIgnoreCase(sortDTO.getOrderType())) {
					criteriaQuery.orderBy(cb.asc(sortPath));
				}
				if (SortConstants.DB_ORDER_BY_DESC.equalsIgnoreCase(sortDTO.getOrderType())) {
					criteriaQuery.orderBy(cb.desc(sortPath));
				}
			}

		}

		TypedQuery<Employee> empTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			empTypedQuery.setFirstResult(getStartPosition(pageDTO));
			empTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		List<Employee> employeeList = empTypedQuery.getResultList();

		return employeeList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.EmployeeDAO#findByNumber(java.lang.String,
	 * java.lang.Long)
	 */
	@Override
	public Employee findByNumber(String empNumber, Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Employee> criteriaQuery = cb.createQuery(Employee.class);
		Root<Employee> empRoot = criteriaQuery.from(Employee.class);
		criteriaQuery.select(empRoot);

		Join<Employee, Company> empRootCompanyJoin = empRoot.join(Employee_.company, JoinType.INNER);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(empRootCompanyJoin.get(Company_.companyId), companyId));
		restriction = cb.and(restriction,
				cb.equal(cb.upper(empRoot.get(Employee_.employeeNumber)), empNumber.toUpperCase()));

		criteriaQuery.where(restriction);

		TypedQuery<Employee> empTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		List<Employee> employeeList = empTypedQuery.getResultList();
		if (employeeList != null && !employeeList.isEmpty()) {
			return employeeList.get(0);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.EmployeeDAO#findByID(long)
	 */
	@Override
	public Employee findByID(long userId) {

		return super.findById(Employee.class, userId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.EmployeeDAO#findById(long)
	 */
	@Override
	public Employee findById(long empID) {

		Employee employee = super.findById(Employee.class, empID);
		return employee;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.EmployeeDAO#update(com.payasia.dao.bean.Employee)
	 */
	@Override
	public void update(Employee employee) {
		Timestamp currentTimestamp = DateUtils.getCurrentTimestamp();
		Timestamp resignationDate = employee.getResignationDate();
		if (resignationDate == null || resignationDate.after(currentTimestamp)) {

			employee.setStatus(true);

		}
		if (resignationDate != null && resignationDate.before(currentTimestamp)) {
			employee.setStatus(false);
		}
		super.update(employee);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.EmployeeDAO#save(com.payasia.dao.bean.Employee)
	 */
	@Override
	public Employee save(Employee employee) {
		Timestamp currentTimestamp = DateUtils.getCurrentTimestamp();
		Timestamp resignationDate = employee.getResignationDate();
		if (resignationDate != null && resignationDate.before(currentTimestamp)) {
			employee.setStatus(false);
		}

		Employee persistObj = employee;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (Employee) getBaseEntity();
			beanUtil.copyProperties(persistObj, employee);
		} catch (IllegalAccessException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		} catch (InvocationTargetException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		}
		this.entityManagerFactory.persist(persistObj);
		return persistObj;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.EmployeeDAO#newTransactionSave(com.payasia.dao.bean.
	 * Employee )
	 */
	@Override
	public void newTransactionSave(Employee employee) {
		super.save(employee);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.EmployeeDAO#delete(com.payasia.dao.bean.Employee)
	 */
	@Override
	public void delete(Employee employee) {
		super.delete(employee);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.EmployeeDAO#getCountForAll(long)
	 */
	@Override
	public int getCountForAll(long companyId) {

		return findAll(companyId, null, null).size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.EmployeeDAO#getCountForCondition(com.payasia.common.dto
	 * .AccessControlConditionDTO, java.lang.Long)
	 */
	@Override
	public Integer getCountForCondition(AccessControlConditionDTO accessControlconditionDTO, Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Integer> criteriaQuery = cb.createQuery(Integer.class);
		Root<Employee> empRoot = criteriaQuery.from(Employee.class);
		criteriaQuery.select(cb.count(empRoot).as(Integer.class));

		Predicate restriction = cb.conjunction();

		Join<Employee, Company> empCompanyJoin = empRoot.join(Employee_.company);

		Path<Long> employeeID = empRoot.get(Employee_.employeeId);
		restriction = cb.and(restriction, cb.equal(empCompanyJoin.get(Company_.companyId), companyId));
		if (StringUtils.isNotBlank(accessControlconditionDTO.getEmployeeNumber())) {

			restriction = cb.and(restriction,
					cb.like(empRoot.get(Employee_.employeeNumber), accessControlconditionDTO.getEmployeeNumber()));
		}

		if (StringUtils.isNotBlank(accessControlconditionDTO.getFirstName())) {

			restriction = cb.and(restriction, cb.like(cb.upper(empRoot.get(Employee_.firstName)),
					accessControlconditionDTO.getFirstName().toUpperCase()));
		}

		if (StringUtils.isNotBlank(accessControlconditionDTO.getLastName())) {

			restriction = cb.and(restriction, cb.like(cb.upper(empRoot.get(Employee_.lastName)),
					accessControlconditionDTO.getLastName().toUpperCase()));
		}

		if (StringUtils.isNotBlank(accessControlconditionDTO.getEmail())) {

			restriction = cb.and(restriction, cb.like(cb.upper(empRoot.get(Employee_.email)),
					accessControlconditionDTO.getEmail().toUpperCase()));
		}

		if (accessControlconditionDTO.getEmployeeShortListDTO().getEmployeeShortList()
				&& accessControlconditionDTO.getEmployeeShortListDTO().getShortListEmployeeIds().isEmpty()) {

			restriction = cb.and(restriction, employeeID.in(-1));

		} else if (accessControlconditionDTO.getEmployeeShortListDTO().getEmployeeShortList()
				&& !accessControlconditionDTO.getEmployeeShortListDTO().getShortListEmployeeIds().isEmpty()) {
			restriction = cb.and(restriction,
					employeeID.in(accessControlconditionDTO.getEmployeeShortListDTO().getShortListEmployeeIds()));

		}

		if (StringUtils.isNotBlank(accessControlconditionDTO.getEmployeeStatus())) {
			if ("enabled".equals(accessControlconditionDTO.getEmployeeStatus())) {
				restriction = cb.and(restriction, cb.equal((empRoot.get(Employee_.status)), true));
			}
			if (accessControlconditionDTO.getEmployeeStatus().equals("disabled")) {
				restriction = cb.and(restriction, cb.equal((empRoot.get(Employee_.status)), false));
			}

		}
		criteriaQuery.where(restriction);

		TypedQuery<Integer> empTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		return empTypedQuery.getSingleResult();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.EmployeeDAO#getEmpCountForCondition(com.payasia.common
	 * .dto.AccessControlConditionDTO, java.lang.Long)
	 */
	@Override
	public int getEmpCountForCondition(AccessControlConditionDTO accessControlconditionDTO, Long companyId) {
		return findEmpForSendPassword(accessControlconditionDTO, null, null, companyId).size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.EmployeeDAO#getCountForRole(java.lang.Long,
	 * java.lang.Long)
	 */
	@Override
	public int getCountForRole(Long roleId, Long companyId) {
		return findByRole(roleId, companyId).size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.EmployeeDAO#getCountForCondition(com.payasia.common.dto
	 * .EmployeeConditionDTO)
	 */
	@Override
	public int getCountForCondition(EmployeeConditionDTO conditionDTO) {
		return findByCondition(conditionDTO, null, null).size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.EmployeeDAO#getCountForCondition(com.payasia.common.dto
	 * .EmployeeConditionDTO, java.lang.Long)
	 */
	@Override
	public int getCountForCondition(EmployeeConditionDTO conditionDTO, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Integer> criteriaQuery = cb.createQuery(Integer.class);
		Root<Employee> empRoot = criteriaQuery.from(Employee.class);
		criteriaQuery.select(cb.count(empRoot).as(Integer.class));

		Join<Employee, Company> empCompanyJoin = empRoot.join(Employee_.company);

		// Join<Employee, EmployeeRoleMapping> empRoleMappingJoin =
		// empRoot.join(
		// Employee_.employeeRoleMappings, JoinType.INNER);
		//
		// Join<EmployeeRoleMapping, RoleMaster> empRoleMappingRoleJoin =
		// empRoleMappingJoin
		// .join(EmployeeRoleMapping_.roleMaster, JoinType.INNER);
		Predicate restriction = cb.conjunction();

		if (StringUtils.isNotBlank(conditionDTO.getEmployeeNumber())) {

			restriction = cb.and(restriction, cb.like(cb.upper(empRoot.get(Employee_.employeeNumber)),
					conditionDTO.getEmployeeNumber().toUpperCase()));
		}

		if (StringUtils.isNotBlank(conditionDTO.getEmployeeName())) {

			restriction = cb.and(restriction, cb.or(
					cb.like(cb.upper(empRoot.get(Employee_.firstName)), conditionDTO.getEmployeeName().toUpperCase()),
					cb.like(cb.upper(empRoot.get(Employee_.lastName)), conditionDTO.getEmployeeName().toUpperCase())));
		}

		if (StringUtils.isNotBlank(conditionDTO.getEmployeeName())) {

			restriction = cb.and(restriction, cb.like(cb.upper(empRoot.get(Employee_.firstName)),
					conditionDTO.getEmployeeName().toUpperCase() + '%'));

			restriction = cb.or(restriction, cb.like(cb.upper(empRoot.get(Employee_.lastName)),
					conditionDTO.getEmployeeName().toUpperCase() + '%'));
		}

		if (conditionDTO.getEmployeeShortListDTO().getEmployeeShortList()
				&& conditionDTO.getEmployeeShortListDTO().getShortListEmployeeIds().isEmpty()) {

			restriction = cb.and(restriction, empRoot.get("employeeId").in(-1));

		} else if (conditionDTO.getEmployeeShortListDTO().getEmployeeShortList()
				&& !conditionDTO.getEmployeeShortListDTO().getShortListEmployeeIds().isEmpty()) {
			restriction = cb.and(restriction,
					empRoot.get("employeeId").in(conditionDTO.getEmployeeShortListDTO().getShortListEmployeeIds()));

		}
		// if (conditionDTO.getRoleName() != null) {
		// restriction = cb.and(restriction, cb.notEqual(
		// empRoleMappingRoleJoin.get(RoleMaster_.roleName),
		// conditionDTO.getRoleName()));
		// }
		restriction = cb.and(restriction, cb.equal(empCompanyJoin.get(Company_.companyId), companyId));
		if (conditionDTO.getStatus() == null || conditionDTO.getStatus() == false) {
			restriction = cb.and(restriction, cb.equal(empRoot.get(Employee_.status), true));
		}
		if (StringUtils.isNotBlank(conditionDTO.getRoleName())) {
			Subquery<Long> subquery = criteriaQuery.subquery(Long.class);
			Root<EmployeeRoleMapping> employeeRoleMap = subquery.from(EmployeeRoleMapping.class);
			subquery.select(employeeRoleMap.get(EmployeeRoleMapping_.employee).get("employeeId").as(Long.class));

			Path<String> empRole = employeeRoleMap.get(EmployeeRoleMapping_.roleMaster).get("roleName");

			Predicate subRestriction = cb.conjunction();

			subRestriction = cb.and(subRestriction, cb.equal(empRole, conditionDTO.getRoleName()));

			subquery.where(subRestriction);

			restriction = cb.and(restriction, cb.not(cb.in(empRoot.get(Employee_.employeeId)).value(subquery)));
		}
		criteriaQuery.where(restriction);

		TypedQuery<Integer> empTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		return empTypedQuery.getSingleResult();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.EmployeeDAO#getCountForConditionCompany(java.lang.Long,
	 * com.payasia.common.dto.EmployeeConditionDTO)
	 */
	@Override
	public int getCountForConditionCompany(Long companyId, EmployeeConditionDTO conditionDTO) {
		return findByConditionCompany(companyId, conditionDTO, null, null).size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.EmployeeDAO#getMaxEmployeeId()
	 */
	@Override
	public Long getMaxEmployeeId() {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
		Root<Employee> employeeRoot = criteriaQuery.from(Employee.class);
		criteriaQuery.select(cb.max(employeeRoot.get(Employee_.employeeId)));

		TypedQuery<Long> maxEmployeeIdQuery = entityManagerFactory.createQuery(criteriaQuery);

		Long maxEmpId = maxEmployeeIdQuery.getSingleResult();
		if (maxEmpId == null) {
			maxEmpId = (long) 0;
		}
		return maxEmpId;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.EmployeeDAO#findEmployee(java.lang.String,
	 * java.lang.Long, java.lang.String)
	 */
	@Override
	public Employee findEmployee(String employeeNumber, Long companyId, String email) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Employee> criteriaQuery = cb.createQuery(Employee.class);
		Root<Employee> employeeRoot = criteriaQuery.from(Employee.class);
		criteriaQuery.select(employeeRoot);

		Join<Employee, Company> empCompanyJoin = employeeRoot.join(Employee_.company);

		Path<Long> companyID = empCompanyJoin.get(Company_.companyId);
		Predicate restriction = cb.conjunction();
		if (employeeNumber != null) {
			restriction = cb.and(restriction, cb.equal(employeeRoot.get(Employee_.employeeNumber), employeeNumber));
		}

		if (companyId != null) {
			restriction = cb.and(restriction, cb.equal(companyID, companyId));

		}

		if (email != null) {
			restriction = cb.and(restriction, cb.equal(employeeRoot.get(Employee_.email), email));
		}

		criteriaQuery.where(restriction);

		TypedQuery<Employee> maxEmployeeIdQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<Employee> employeeList = maxEmployeeIdQuery.getResultList();
		if (employeeList != null && !employeeList.isEmpty()) {
			return employeeList.get(0);
		}
		return null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.EmployeeDAO#getEmployeeIds(java.lang.String,
	 * java.lang.Long)
	 */
	@Override
	public List<Employee> getEmployeeIds(String searchString, Long companyId,
			EmployeeShortListDTO employeeShortListDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Employee> criteriaQuery = cb.createQuery(Employee.class);
		Root<Employee> empRoot = criteriaQuery.from(Employee.class);
		criteriaQuery.select(empRoot);

		Join<Employee, Company> empCompanyJoin = empRoot.join(Employee_.company);
		Path<Long> companyID = empCompanyJoin.get(Company_.companyId);
		Predicate restriction = cb.conjunction();
		Path<Long> employeeID = empRoot.get(Employee_.employeeId);
		if (StringUtils.isNotBlank(searchString.trim())) {

			restriction = cb.and(restriction,
					cb.like(empRoot.get(Employee_.employeeNumber), searchString.trim() + "%"));

		}

		if (employeeShortListDTO.getEmployeeShortList() && employeeShortListDTO.getShortListEmployeeIds().isEmpty()) {

			restriction = cb.and(restriction, employeeID.in(-1));

		} else if (employeeShortListDTO.getEmployeeShortList()
				&& !employeeShortListDTO.getShortListEmployeeIds().isEmpty()) {
			restriction = cb.and(restriction, employeeID.in(employeeShortListDTO.getShortListEmployeeIds()));

		}

		restriction = cb.and(restriction, cb.equal(companyID, companyId));

		criteriaQuery.where(restriction);

		TypedQuery<Employee> empTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<Employee> employeeList = empTypedQuery.getResultList();

		return employeeList;
	}

	@Override
	public List<Employee> getEmployeeIdsForGroupCompany(String searchString, Long companyId, Long companyGroupId,
			EmployeeShortListDTO employeeShortListDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Employee> criteriaQuery = cb.createQuery(Employee.class);
		Root<Employee> empRoot = criteriaQuery.from(Employee.class);
		criteriaQuery.select(empRoot);

		Join<Employee, Company> empCompanyJoin = empRoot.join(Employee_.company);
		Join<Company, CompanyGroup> groupJoin = empCompanyJoin.join(Company_.companyGroup);

		Predicate restriction = cb.conjunction();
		Path<Long> employeeID = empRoot.get(Employee_.employeeId);
		if (StringUtils.isNotBlank(searchString.trim())) {

			restriction = cb.and(restriction,
					cb.like(empRoot.get(Employee_.employeeNumber), searchString.trim() + "%"));

		}

		if (employeeShortListDTO.getEmployeeShortList() && employeeShortListDTO.getShortListEmployeeIds().isEmpty()) {

			restriction = cb.and(restriction, employeeID.in(-1));

		} else if (employeeShortListDTO.getEmployeeShortList()
				&& !employeeShortListDTO.getShortListEmployeeIds().isEmpty()) {
			restriction = cb.and(restriction, employeeID.in(employeeShortListDTO.getShortListEmployeeIds()));

		}
		restriction = cb.and(restriction, cb.equal(empRoot.get(Employee_.status), true));
		restriction = cb.and(restriction, cb.equal(groupJoin.get(CompanyGroup_.groupId), companyGroupId));

		criteriaQuery.where(restriction);

		TypedQuery<Employee> empTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<Employee> employeeList = empTypedQuery.getResultList();

		return employeeList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.EmployeeDAO#saveReturn(com.payasia.dao.bean.Employee)
	 */
	@Override
	public Employee saveReturn(Employee employee) {

		Timestamp currentTimestamp = DateUtils.getCurrentTimestamp();
		Timestamp resignationDate = employee.getResignationDate();
		if (resignationDate != null) {
			if (resignationDate.before(currentTimestamp)) {
				employee.setStatus(false);
			}
		}

		Employee persistObj = employee;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (Employee) getBaseEntity();
			beanUtil.copyProperties(persistObj, employee);
		} catch (IllegalAccessException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		} catch (InvocationTargetException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		}
		this.entityManagerFactory.persist(persistObj);
		return persistObj;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.EmployeeDAO#authenticateEmployee(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public Boolean authenticateEmployee(String loginName, String password, String companyCode) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Employee> criteriaQuery = cb.createQuery(Employee.class);
		Root<Employee> empRoot = criteriaQuery.from(Employee.class);
		criteriaQuery.select(empRoot);
		Join<Employee, Company> empCompanyJoin = empRoot.join(Employee_.company);
		Join<Employee, EmployeeLoginDetail> loginDetailJoin = empRoot.join(Employee_.employeeLoginDetails);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(loginDetailJoin.get(EmployeeLoginDetail_.loginName), loginName.trim()));

		restriction = cb.and(restriction,
				cb.equal(loginDetailJoin.get(EmployeeLoginDetail_.password), password.trim()));

		restriction = cb.and(restriction,
				cb.equal(cb.upper(empCompanyJoin.get(Company_.companyCode)), companyCode.toUpperCase()));

		criteriaQuery.where(restriction);

		TypedQuery<Employee> empTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		if (empTypedQuery.getResultList().size() == 1) {
			return true;
		} else {
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.EmployeeDAO#getEmployeeByLoginName(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public Employee getEmployeeByLoginName(String loginName, String companyCode) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Employee> criteriaQuery = cb.createQuery(Employee.class);
		Root<Employee> empRoot = criteriaQuery.from(Employee.class);
		criteriaQuery.select(empRoot);
		Join<Employee, Company> empCompanyJoin = empRoot.join(Employee_.company);

		Join<Employee, EmployeeLoginDetail> loginDetailJoin = empRoot.join(Employee_.employeeLoginDetails);

		Predicate restriction = cb.conjunction();

		if (StringUtils.isNotBlank(loginName.trim())) {

			restriction = cb.and(restriction,
					cb.equal(loginDetailJoin.get(EmployeeLoginDetail_.loginName), loginName.trim()));

		}
		if (StringUtils.isNotBlank(companyCode.trim())) {

			restriction = cb.and(restriction,
					cb.equal(empCompanyJoin.get(Company_.companyCode), companyCode.toLowerCase()));

		}
		criteriaQuery.where(restriction);

		TypedQuery<Employee> empTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<Employee> employeeList = empTypedQuery.getResultList();
		if (employeeList != null && !employeeList.isEmpty()) {
			return employeeList.get(0);
		}
		return null;
	}

	@Override
	public Employee getEmpByLoginNameOrEmail(String loginNameOrEmail, String companyCode) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Employee> criteriaQuery = cb.createQuery(Employee.class);
		Root<Employee> empRoot = criteriaQuery.from(Employee.class);
		criteriaQuery.select(empRoot);
		Join<Employee, Company> empCompanyJoin = empRoot.join(Employee_.company);

		Predicate restriction = cb.conjunction();

		if (StringUtils.isNotBlank(loginNameOrEmail.trim())) {
			restriction = cb.and(restriction,
					cb.or(cb.equal(empRoot.get(Employee_.employeeNumber).as(String.class), loginNameOrEmail),
							cb.equal(empRoot.get(Employee_.email).as(String.class), loginNameOrEmail)));
		}
		if (StringUtils.isNotBlank(companyCode.trim())) {
			restriction = cb.and(restriction,
					cb.equal(empCompanyJoin.get(Company_.companyCode), companyCode.toLowerCase()));
		}
		criteriaQuery.where(restriction);

		TypedQuery<Employee> empTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<Employee> employeeList = empTypedQuery.getResultList();
		if (employeeList != null && !employeeList.isEmpty()) {
			return employeeList.get(0);
		}
		return null;
	}

	@Override
	public List<Employee> getEmployeeByEmail(String email, String companyCode) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Employee> criteriaQuery = cb.createQuery(Employee.class);
		Root<Employee> empRoot = criteriaQuery.from(Employee.class);
		criteriaQuery.select(empRoot);
		Join<Employee, Company> empCompanyJoin = empRoot.join(Employee_.company);

		Predicate restriction = cb.conjunction();

		if (StringUtils.isNotBlank(email.trim())) {

			restriction = cb.and(restriction, cb.equal(empRoot.get(Employee_.email), email.trim()));

		}
		if (StringUtils.isNotBlank(companyCode.trim())) {

			restriction = cb.and(restriction,
					cb.equal(empCompanyJoin.get(Company_.companyCode), companyCode.toLowerCase()));

		}
		criteriaQuery.where(restriction);

		TypedQuery<Employee> empTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<Employee> employeeList = empTypedQuery.getResultList();

		return employeeList;
	}

	@Override
	public Employee getEmployeeByEmpNumAndCompCode(String employeeNum, String companyCode) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Employee> criteriaQuery = cb.createQuery(Employee.class);
		Root<Employee> empRoot = criteriaQuery.from(Employee.class);
		criteriaQuery.select(empRoot);
		Join<Employee, Company> empCompanyJoin = empRoot.join(Employee_.company);

		Predicate restriction = cb.conjunction();

		if (StringUtils.isNotBlank(employeeNum.trim())) {

			restriction = cb.and(restriction, cb.equal(empRoot.get(Employee_.employeeNumber), employeeNum.trim()));

		}
		if (StringUtils.isNotBlank(companyCode.trim())) {

			restriction = cb.and(restriction,
					cb.equal(cb.upper(empCompanyJoin.get(Company_.companyCode)), companyCode.toUpperCase()));

		}
		criteriaQuery.where(restriction);

		TypedQuery<Employee> empTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<Employee> employeeList = empTypedQuery.getResultList();
		if (employeeList != null && !employeeList.isEmpty()) {
			return employeeList.get(0);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.EmployeeDAO#getEmployeeByLoginName(java.lang.String)
	 */
	@Override
	public Employee getEmployeeByLoginName(String loginName) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Employee> criteriaQuery = cb.createQuery(Employee.class);
		Root<Employee> empRoot = criteriaQuery.from(Employee.class);
		criteriaQuery.select(empRoot);

		Join<Employee, EmployeeLoginDetail> loginDetailJoin = empRoot.join(Employee_.employeeLoginDetails);

		Predicate restriction = cb.conjunction();

		if (StringUtils.isNotBlank(loginName.trim())) {

			restriction = cb.and(restriction,
					cb.equal(loginDetailJoin.get(EmployeeLoginDetail_.loginName), loginName.trim()));

		}
		criteriaQuery.where(restriction);

		TypedQuery<Employee> empTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<Employee> employeeList = empTypedQuery.getResultList();
		if (employeeList != null && !employeeList.isEmpty()) {
			return employeeList.get(0);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.EmployeeDAO#getEmployeeByEmail(java.lang.String)
	 */
	@Override
	public Employee getEmployeeByEmail(String EmailId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Employee> criteriaQuery = cb.createQuery(Employee.class);
		Root<Employee> empRoot = criteriaQuery.from(Employee.class);
		criteriaQuery.select(empRoot);

		Predicate restriction = cb.conjunction();

		if (StringUtils.isNotBlank(EmailId.trim())) {

			restriction = cb.and(restriction, cb.equal(empRoot.get(Employee_.email), EmailId.trim()));

		}
		criteriaQuery.where(restriction);

		TypedQuery<Employee> empTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<Employee> employeeList = empTypedQuery.getResultList();
		if (employeeList != null && !employeeList.isEmpty()) {
			return employeeList.get(0);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.EmployeeDAO#newTranSaveReturn(com.payasia.dao.bean.
	 * Employee )
	 */
	@Override
	public Employee newTranSaveReturn(Employee employee) {

		Employee persistObj = employee;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (Employee) getBaseEntity();
			beanUtil.copyProperties(persistObj, employee);
		} catch (IllegalAccessException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		} catch (InvocationTargetException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		}
		this.entityManagerFactory.persist(persistObj);
		return persistObj;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.EmployeeDAO#newTranUpdate(com.payasia.dao.bean.Employee)
	 */
	@Override
	public void newTranUpdate(Employee employee) {
		super.update(employee);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.EmployeeDAO#newTranDelete(com.payasia.dao.bean.Employee)
	 */
	@Override
	public void newTranDelete(Employee employee) {
		super.delete(employee);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.EmployeeDAO#findByCondition(java.util.Map,
	 * java.util.List, java.lang.Long, java.util.List, java.lang.String)
	 */
	@Override
	public List<Object[]> findByCondition(Map<String, DataImportKeyValueDTO> colMap, List<Long> formIds, Long companyId,
			List<ExcelExportFiltersForm> finalFilterList, String dataFormat,
			Map<String, DataImportKeyValueDTO> tableRecordInfo, List<DataImportKeyValueDTO> tableElements,
			EmployeeShortListDTO employeeShortListDTO, boolean showByEffectiveDateTableData) {
		Map<String, String> paramValueMap = new HashMap<String, String>();
		Map<String, List<BigInteger>> paramEmpIdsMap = new HashMap<String, List<BigInteger>>();
		List<String> tableJoinsList = new ArrayList<>();
		List<String> selectFieldTableNameList = new ArrayList<>();
		ExcelExportQueryDTO selectDTO = createSelect(colMap, tableRecordInfo, selectFieldTableNameList);

		String from = createFrom(colMap, formIds, selectDTO, tableRecordInfo, tableJoinsList, finalFilterList,
				selectFieldTableNameList, companyId, showByEffectiveDateTableData,
				employeeShortListDTO.getLundinBatchEndDate());
		String where = createWhere(companyId, finalFilterList, dataFormat, tableRecordInfo, employeeShortListDTO,
				tableJoinsList, selectFieldTableNameList, paramValueMap, paramEmpIdsMap);
		String queryString = selectDTO.getSelectQuery() + from + where;
		// queryString = queryString.replace("?", "");
		// queryString = queryString.replace("%", "");

		// System.out.println(queryString);
		Query query = entityManagerFactory.createNativeQuery(queryString);
		query.setParameter("companyId", companyId);
		for (Map.Entry<String, String> entry : paramValueMap.entrySet()) {
			query.setParameter(entry.getKey(), entry.getValue());
		}
		for (Map.Entry<String, List<BigInteger>> entry : paramEmpIdsMap.entrySet()) {
			query.setParameter(entry.getKey(), entry.getValue());
		}

		return query.getResultList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.EmployeeDAO#findByCondition(java.util.Map,
	 * java.util.List, java.lang.Long, java.util.List, java.lang.String)
	 */
	@Override
	public List<Object[]> findByConditionGroup(Map<String, DataImportKeyValueDTO> colMap, List<Long> formIds,
			Long companyId, List<ExcelExportFiltersForm> finalFilterList,
			Map<String, DataImportKeyValueDTO> tableRecordInfo, List<DataImportKeyValueDTO> tableElements,
			EmployeeShortListDTO employeeShortListDTO) {
		Map<String, String> paramValueMap = new HashMap<String, String>();
		Map<String, List<BigInteger>> paramEmpIdsMap = new HashMap<String, List<BigInteger>>();
		List<String> selectFieldTableNameList = new ArrayList<>();
		ExcelExportQueryDTO selectDTO = createSelectGroup(colMap, tableRecordInfo, selectFieldTableNameList);
		String from = createFromGroup(colMap, formIds, selectDTO, tableRecordInfo, selectFieldTableNameList,
				finalFilterList, companyId);
		String where = createWhereGroup(companyId, finalFilterList, tableRecordInfo, employeeShortListDTO,
				selectFieldTableNameList, paramValueMap, paramEmpIdsMap);
		String queryString = selectDTO.getSelectQuery() + from + where;
		// queryString = queryString.replace("?", "");
		// queryString = queryString.replace("%", "");

		// System.out.println(queryString);

		Query q = entityManagerFactory.createNativeQuery(queryString);
		q.setParameter("companyId", companyId);
		for (Map.Entry<String, String> entry : paramValueMap.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}
		for (Map.Entry<String, List<BigInteger>> entry : paramEmpIdsMap.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}

		return q.getResultList();
	}

	/**
	 * Creates the select.
	 * 
	 * @param colMap
	 *            the col map
	 * @param tableElements
	 * @return the excel export query dto
	 */
	public ExcelExportQueryDTO createSelect(Map<String, DataImportKeyValueDTO> colMap,
			Map<String, DataImportKeyValueDTO> tableRecordInfo, List<String> selectFieldTableNameList) {

		ExcelExportQueryDTO excelExportQueryDTO = new ExcelExportQueryDTO();
		List<CodeDescDTO> codeDescDTOs = new ArrayList<CodeDescDTO>();

		Integer noOfTables = tableRecordInfo.size();

		StringBuilder selectQuery = new StringBuilder("SELECT ");
		String tablePosition = null;
		Set<String> xlKeySet = colMap.keySet();

		int count = 1;

		for (Iterator<String> itr = xlKeySet.iterator(); itr.hasNext();) {
			if (count < xlKeySet.size()) {
				String key = (String) itr.next();
				DataImportKeyValueDTO valueDTO = (DataImportKeyValueDTO) colMap.get(key);
				String fieldName = valueDTO.getMethodName();
				key = key.replaceAll("\\.", "_");
				String codeKey = key;
				key = "[" + key + "]";
				if (valueDTO.isStatic()) {
					String methodName = valueDTO.getActualColName();
					if (!methodName.equalsIgnoreCase(PayAsiaConstants.COLUMN_NAME_EMPLOYEE_NUMBER)) {
						selectFieldTableNameList.add("employee");
					}

					selectQuery.append("employee.").append(methodName).append(" AS ").append(key).append(", ");

				} else {

					String methodName = "Col_" + fieldName;
					String actFieldName = valueDTO.getActualColName();
					if (!valueDTO.isChild()) {

						if (valueDTO.getFieldType().equalsIgnoreCase("codedesc")) {

							CodeDescDTO codeDescDTO = new CodeDescDTO();
							codeDescDTO.setMethodName(methodName);
							codeDescDTO.setFormId(valueDTO.getFormId());
							codeDescDTO.setChildVal(false);
							codeDescDTOs.add(codeDescDTO);

							selectQuery.append(" dynamicFormFieldRefValue").append(valueDTO.getFormId() + methodName)
									.append(".Code AS [" + codeKey + "_Code]").append(", ");

							selectQuery.append(" dynamicFormFieldRefValue").append(valueDTO.getFormId())
									.append(methodName).append(".Description AS [").append(codeKey)
									.append("_Description]").append(", ");

							selectFieldTableNameList
									.add("dynamicFormFieldRefValue" + valueDTO.getFormId() + methodName);
						} else {

							if (valueDTO.getFieldType().equalsIgnoreCase(PayAsiaConstants.EMPLOYEE_lIST_REFERENCE)) {
								selectQuery.append(valueDTO.getEmpLstSelectField()).append(" AS ").append(key)
										.append(" , ");
							} else {
								selectQuery.append(" dynamicFormRecord").append(valueDTO.getFormId()).append(".")
										.append(methodName).append(" AS ").append(key).append(", ");

							}

							if (actFieldName != null
									&& !actFieldName.equalsIgnoreCase(PayAsiaConstants.EMPLOYEE_NAME)) {
								selectFieldTableNameList.add("dynamicFormRecord" + valueDTO.getFormId());
							}

						}

					} else {

						if (valueDTO.getFieldType().equalsIgnoreCase("codedesc")) {
							CodeDescDTO codeDescDTO = new CodeDescDTO();
							codeDescDTO.setMethodName(methodName);
							codeDescDTO.setFormId(valueDTO.getFormId());
							codeDescDTO.setChildVal(true);
							codeDescDTO.setTablePosition(valueDTO.getTablePosition());
							codeDescDTOs.add(codeDescDTO);

							selectQuery.append(" dynamicFormFieldRefValueT").append(valueDTO.getFormId())
									.append(valueDTO.getTablePosition()).append(methodName).append(".Code AS [")
									.append(codeKey).append("_Code]").append(", ");

							selectQuery.append(" dynamicFormFieldRefValueT").append(valueDTO.getFormId())
									.append(valueDTO.getTablePosition() + methodName).append(".Description AS [")
									.append(codeKey).append("_Description]").append(", ");
							selectFieldTableNameList.add("dynamicFormFieldRefValueT" + valueDTO.getFormId()
									+ valueDTO.getTablePosition() + methodName);

						} else {

							if (noOfTables <= 1) {

								if (valueDTO.getFieldType()
										.equalsIgnoreCase(PayAsiaConstants.EMPLOYEE_lIST_REFERENCE)) {
									selectQuery.append(valueDTO.getEmpLstSelectField()).append(" AS ").append(key)
											.append(", ");
								} else {
									selectQuery.append(" dynamicFormTableRecord_").append(valueDTO.getFormId())
											.append(valueDTO.getTablePosition()).append(".").append(methodName + " AS ")
											.append(key).append(", ");
								}

								selectFieldTableNameList.add(
										"dynamicFormTableRecord_" + valueDTO.getFormId() + valueDTO.getTablePosition());

							} else {

								if (valueDTO.getFieldType()
										.equalsIgnoreCase(PayAsiaConstants.EMPLOYEE_lIST_REFERENCE)) {
									selectQuery.append(valueDTO.getEmpLstSelectField()).append(" AS ").append(key)
											.append(", ");
								} else {

									selectQuery.append(" dynamicFormTableRecord_").append(valueDTO.getFormId())
											.append(valueDTO.getTablePosition()).append(".").append(methodName)
											.append(" AS ").append(key).append(", ");
								}
								selectFieldTableNameList.add(
										"dynamicFormTableRecord_" + valueDTO.getFormId() + valueDTO.getTablePosition());
							}

						}

						tablePosition = valueDTO.getTablePosition();
						excelExportQueryDTO.setTablePosition(tablePosition);
						excelExportQueryDTO.setFormId(valueDTO.getFormId());

					}
				}
			} else {
				String key = (String) itr.next();
				DataImportKeyValueDTO valueDTO = (DataImportKeyValueDTO) colMap.get(key);
				String fieldName = valueDTO.getMethodName();
				key = key.replaceAll("\\.", "_");
				String codeKey = key;
				key = "[" + key + "]";
				if (valueDTO.isStatic()) {
					String methodName = valueDTO.getActualColName();
					if (!methodName.equalsIgnoreCase(PayAsiaConstants.COLUMN_NAME_EMPLOYEE_NUMBER)) {
						selectFieldTableNameList.add("employee");
					}

					selectQuery.append("employee.").append(methodName).append(" AS ").append(key).append(" ");
				} else {

					String methodName = "Col_" + fieldName;
					String actFieldName = valueDTO.getActualColName();
					if (!valueDTO.isChild()) {

						if (valueDTO.getFieldType().equalsIgnoreCase("codedesc")) {

							CodeDescDTO codeDescDTO = new CodeDescDTO();
							codeDescDTO.setMethodName(methodName);
							codeDescDTO.setFormId(valueDTO.getFormId());
							codeDescDTO.setChildVal(false);
							codeDescDTOs.add(codeDescDTO);

							selectQuery.append(" dynamicFormFieldRefValue").append(valueDTO.getFormId())
									.append(methodName).append(".Code AS [").append(codeKey).append("_Code]")
									.append(", ");

							selectQuery.append(" dynamicFormFieldRefValue").append(valueDTO.getFormId())
									.append(methodName).append(".Description AS [").append(codeKey)
									.append("_Description]").append(" ");
							selectFieldTableNameList
									.add("dynamicFormFieldRefValue" + valueDTO.getFormId() + methodName);

						} else {
							if (valueDTO.getFieldType().equalsIgnoreCase(PayAsiaConstants.EMPLOYEE_lIST_REFERENCE)) {
								selectQuery.append(valueDTO.getEmpLstSelectField()).append(" AS ").append(key)
										.append(" ");
							} else {
								selectQuery.append(" dynamicFormRecord").append(valueDTO.getFormId()).append(".")
										.append(methodName).append(" AS ").append(key).append(" ");
							}
							if (actFieldName != null
									&& !actFieldName.equalsIgnoreCase(PayAsiaConstants.EMPLOYEE_NAME)) {
								selectFieldTableNameList.add("dynamicFormRecord" + valueDTO.getFormId());
							}

						}

					} else {

						if (valueDTO.getFieldType().equalsIgnoreCase("codedesc")) {
							CodeDescDTO codeDescDTO = new CodeDescDTO();
							codeDescDTO.setMethodName(methodName);
							codeDescDTO.setFormId(valueDTO.getFormId());
							codeDescDTO.setChildVal(true);
							codeDescDTO.setTablePosition(valueDTO.getTablePosition());
							codeDescDTOs.add(codeDescDTO);

							selectQuery.append(" dynamicFormFieldRefValueT").append(valueDTO.getFormId())
									.append(valueDTO.getTablePosition()).append(methodName).append(".Code AS [")
									.append(codeKey).append("_Code]").append(", ");

							selectQuery.append(" dynamicFormFieldRefValueT").append(valueDTO.getFormId())
									.append(valueDTO.getTablePosition()).append(methodName).append(".Description AS [")
									.append(codeKey).append("_Description]").append(" ");
							selectFieldTableNameList.add("dynamicFormFieldRefValueT" + valueDTO.getFormId()
									+ valueDTO.getTablePosition() + methodName);

						} else {

							if (valueDTO.getFieldType().equalsIgnoreCase(PayAsiaConstants.EMPLOYEE_lIST_REFERENCE)) {
								selectQuery.append(valueDTO.getEmpLstSelectField()).append(" AS ").append(key)
										.append(" ");
							} else {
								selectQuery.append(" dynamicFormTableRecord_").append(valueDTO.getFormId())
										.append(valueDTO.getTablePosition()).append(".").append(methodName)
										.append(" AS ").append(key).append(" ");
							}
							selectFieldTableNameList.add(
									"dynamicFormTableRecord_" + valueDTO.getFormId() + valueDTO.getTablePosition());

						}

						tablePosition = valueDTO.getTablePosition();
						excelExportQueryDTO.setTablePosition(tablePosition);
						excelExportQueryDTO.setFormId(valueDTO.getFormId());

					}
				}
			}

			count++;
		}
		excelExportQueryDTO.setCodeDescList(codeDescDTOs);
		excelExportQueryDTO.setSelectQuery(String.valueOf(selectQuery));

		return excelExportQueryDTO;
	}

	/**
	 * Creates the select. Group
	 * 
	 * @param colMap
	 *            the col map
	 * @param tableElements
	 * @return the excel export query dto
	 */
	public ExcelExportQueryDTO createSelectGroup(Map<String, DataImportKeyValueDTO> colMap,
			Map<String, DataImportKeyValueDTO> tableRecordInfo, List<String> selectFieldTableNameList) {

		ExcelExportQueryDTO excelExportQueryDTO = new ExcelExportQueryDTO();
		List<CodeDescDTO> codeDescDTOs = new ArrayList<CodeDescDTO>();

		StringBuilder selectQuery = new StringBuilder("SELECT ");
		String tablePosition = null;
		Set<String> xlKeySet = colMap.keySet();

		int count = 1;

		for (Iterator<String> itr = xlKeySet.iterator(); itr.hasNext();) {
			if (count < xlKeySet.size()) {
				String key = (String) itr.next();
				DataImportKeyValueDTO valueDTO = (DataImportKeyValueDTO) colMap.get(key);
				String fieldName = valueDTO.getMethodName();
				key = key.replaceAll("\\.", "_");
				String codeKey = key;
				key = "[" + key + "]";
				if (valueDTO.isStatic()) {
					String methodName = valueDTO.getActualColName();
					if (!methodName.equalsIgnoreCase(PayAsiaConstants.COLUMN_NAME_EMPLOYEE_NUMBER)) {
						selectFieldTableNameList.add("employee");
					}
					selectQuery.append("employee.").append(methodName).append(" AS ").append(key).append(", ");
				}

				else {

					String methodName = "Col_" + fieldName;
					String actFieldName = valueDTO.getActualColName();
					if (valueDTO.getMethodName().equalsIgnoreCase(PayAsiaConstants.FIELD_NOT_EXISTS)) {

						selectQuery.append(" null"

						).append(" AS ").append(key).append(", ");

					}

					else if (!valueDTO.isChild()) {

						if (valueDTO.getFieldType().equalsIgnoreCase("codedesc")) {

							CodeDescDTO codeDescDTO = new CodeDescDTO();
							codeDescDTO.setMethodName(methodName);
							codeDescDTO.setFormId(valueDTO.getFormId());
							codeDescDTO.setChildVal(false);
							codeDescDTOs.add(codeDescDTO);

							selectQuery.append(" concat(dynamicFormFieldRefValue").append(valueDTO.getFormId())
									.append(methodName).append(".Code ,'-', ").append(" dynamicFormFieldRefValue")
									.append(valueDTO.getFormId()).append(methodName).append(".Description) AS [")
									.append(codeKey).append("_CodeDescription]" + ", ");

							selectFieldTableNameList
									.add("dynamicFormFieldRefValue" + valueDTO.getFormId() + methodName);

						} else {

							if (valueDTO.getFieldType().equalsIgnoreCase(PayAsiaConstants.EMPLOYEE_lIST_REFERENCE)) {
								selectQuery.append(valueDTO.getEmpLstSelectField()).append(" AS ").append(key)
										.append(" , ");
							} else {
								selectQuery.append(" dynamicFormRecord").append(valueDTO.getFormId()).append(".")
										.append(methodName).append(" AS ").append(key).append(", ");
							}

							if (actFieldName != null
									&& !actFieldName.equalsIgnoreCase(PayAsiaConstants.EMPLOYEE_NAME)) {
								selectFieldTableNameList.add("dynamicFormRecord" + valueDTO.getFormId());
							}

						}

					} else {

						if (valueDTO.getFieldType().equalsIgnoreCase("codedesc")) {
							CodeDescDTO codeDescDTO = new CodeDescDTO();
							codeDescDTO.setMethodName(methodName);
							codeDescDTO.setFormId(valueDTO.getFormId());
							codeDescDTO.setChildVal(true);
							codeDescDTO.setTablePosition(valueDTO.getTablePosition());
							codeDescDTOs.add(codeDescDTO);

							selectQuery.append(" concat(dynamicFormFieldRefValueT").append(valueDTO.getFormId())
									.append(valueDTO.getTablePosition() + methodName).append(".Code ,'-', ")
									.append(" dynamicFormFieldRefValueT").append(valueDTO.getFormId())
									.append(valueDTO.getTablePosition()).append(methodName).append(".Description) AS [")
									.append(codeKey).append("Code Description]").append(", ");

							selectFieldTableNameList.add("dynamicFormFieldRefValueT" + valueDTO.getFormId()
									+ valueDTO.getTablePosition() + methodName);

						} else {

							if (valueDTO.getFieldType().equalsIgnoreCase(PayAsiaConstants.EMPLOYEE_lIST_REFERENCE)) {
								selectQuery.append(valueDTO.getEmpLstSelectField()).append(" AS ").append(key)
										.append(" , ");
							} else {

								selectQuery.append(" dynamicFormTableRecord_").append(valueDTO.getFormId())
										.append(valueDTO.getTablePosition()).append(".").append(methodName)
										.append(" AS ").append(key).append(", ");
							}

							selectFieldTableNameList.add(
									"dynamicFormTableRecord_" + valueDTO.getFormId() + valueDTO.getTablePosition());

						}

						tablePosition = valueDTO.getTablePosition();
						excelExportQueryDTO.setTablePosition(tablePosition);
						excelExportQueryDTO.setFormId(valueDTO.getFormId());

					}
				}
			} else {
				String key = (String) itr.next();
				DataImportKeyValueDTO valueDTO = (DataImportKeyValueDTO) colMap.get(key);
				String fieldName = valueDTO.getMethodName();
				key = key.replaceAll("\\.", "_");
				String codeKey = key;
				key = "[" + key + "]";
				if (valueDTO.isStatic()) {
					String methodName = valueDTO.getActualColName();
					if (!methodName.equalsIgnoreCase(PayAsiaConstants.COLUMN_NAME_EMPLOYEE_NUMBER)) {
						selectFieldTableNameList.add("employee");
					}

					selectQuery.append("employee.").append(methodName).append(" AS ").append(key).append(" ");
				}

				else {

					String methodName = "Col_" + fieldName;
					String actFieldName = valueDTO.getActualColName();
					if (valueDTO.getMethodName().equalsIgnoreCase(PayAsiaConstants.FIELD_NOT_EXISTS)) {

						selectQuery.append(" null"

						).append(" AS ").append(key).append(" ");

					}

					else if (!valueDTO.isChild()) {

						if (valueDTO.getFieldType().equalsIgnoreCase("codedesc")) {

							CodeDescDTO codeDescDTO = new CodeDescDTO();
							codeDescDTO.setMethodName(methodName);
							codeDescDTO.setFormId(valueDTO.getFormId());
							codeDescDTO.setChildVal(false);
							codeDescDTOs.add(codeDescDTO);

							selectQuery.append(" concat(dynamicFormFieldRefValue").append(valueDTO.getFormId())
									.append(methodName).append(".Code  ,'-', ").append(" dynamicFormFieldRefValue")
									.append(valueDTO.getFormId()).append(methodName).append(".Description) AS [")
									.append(codeKey).append("_CodeDescription]").append(" ");

							selectFieldTableNameList
									.add("dynamicFormFieldRefValue" + valueDTO.getFormId() + methodName);

						} else {

							if (fieldName.equalsIgnoreCase(PayAsiaConstants.FIELD_NOT_EXISTS)) {

								selectQuery.append(" null "

								).append(" AS ").append(key).append(" ");

							} else {

								if (valueDTO.getFieldType()
										.equalsIgnoreCase(PayAsiaConstants.EMPLOYEE_lIST_REFERENCE)) {
									selectQuery.append(valueDTO.getEmpLstSelectField()).append(" AS ").append(key)
											.append("  ");
								} else {

									selectQuery.append(" dynamicFormRecord").append(valueDTO.getFormId()).append(".")
											.append(methodName).append(" AS ").append(key).append(" ");
								}

								if (actFieldName != null
										&& !actFieldName.equalsIgnoreCase(PayAsiaConstants.EMPLOYEE_NAME)) {
									selectFieldTableNameList.add("dynamicFormRecord" + valueDTO.getFormId());
								}

							}

						}

					} else {

						if (valueDTO.getFieldType().equalsIgnoreCase("codedesc")) {
							CodeDescDTO codeDescDTO = new CodeDescDTO();
							codeDescDTO.setMethodName(methodName);
							codeDescDTO.setFormId(valueDTO.getFormId());
							codeDescDTO.setChildVal(true);
							codeDescDTO.setTablePosition(valueDTO.getTablePosition());
							codeDescDTOs.add(codeDescDTO);

							selectQuery.append(" concat(dynamicFormFieldRefValueT").append(valueDTO.getFormId())
									.append(valueDTO.getTablePosition()).append(methodName).append(".Code ,'-', ")
									.append(" dynamicFormFieldRefValueT").append(valueDTO.getFormId())
									.append(valueDTO.getTablePosition() + methodName).append(".Description) AS [")
									.append(codeKey).append("_CodeDescription]").append(" ");

							selectFieldTableNameList.add("dynamicFormFieldRefValueT" + valueDTO.getFormId()
									+ valueDTO.getTablePosition() + methodName);

						} else {

							if (valueDTO.getFieldType().equalsIgnoreCase(PayAsiaConstants.EMPLOYEE_lIST_REFERENCE)) {
								selectQuery.append(valueDTO.getEmpLstSelectField()).append(" AS ").append(key)
										.append("  ");
							} else {
								selectQuery.append(" dynamicFormTableRecord_").append(valueDTO.getFormId())
										.append(valueDTO.getTablePosition()).append(".").append(methodName)
										.append(" AS ").append(key).append(" ");

							}

							selectFieldTableNameList.add(
									"dynamicFormTableRecord_" + valueDTO.getFormId() + valueDTO.getTablePosition());

						}

						tablePosition = valueDTO.getTablePosition();
						excelExportQueryDTO.setTablePosition(tablePosition);
						excelExportQueryDTO.setFormId(valueDTO.getFormId());

					}
				}
			}

			count++;
		}
		excelExportQueryDTO.setCodeDescList(codeDescDTOs);
		excelExportQueryDTO.setSelectQuery(String.valueOf(selectQuery));

		return excelExportQueryDTO;
	}

	public String getUpdatedDateFieldStringOnTable(List<String> selectFieldTableNameList,
			List<ExcelExportFiltersForm> finalFilterList, String tableName) {
		String updatedDateTableField = "";
		Boolean updatedBooleanStatus = false;
		Boolean updatedDateExists = false;

		Set<String> selectFieldTableNameListSet = new HashSet<>(selectFieldTableNameList);
		StringBuilder builder = new StringBuilder();
		builder = builder.append("AND (");
		for (String tableJoinValue : selectFieldTableNameListSet) {
			int filterValCount = 0;
			Boolean status = true;
			if (!tableJoinValue.equalsIgnoreCase(tableName)) {
				continue;
			}

			if (tableJoinValue.equalsIgnoreCase(PayAsiaConstants.EMPLOYEE_DEFAULT_ROLE)) {
				continue;
			}

			for (ExcelExportFiltersForm excelExportFiltersForm : finalFilterList) {

				if (filterValCount > 2) {
					updatedBooleanStatus = true;
				}
				if (excelExportFiltersForm.getDataImportKeyValueDTO().getActualColName() == null) {
					continue;
				}
				if (!excelExportFiltersForm.getDataImportKeyValueDTO().getActualColName()
						.equals(PayAsiaConstants.EMPLOYEE_TABLE_COLUMN_UPDATED_DATE)) {
					continue;
				}

				if (status) {
					updatedDateExists = true;
					if (filterValCount == 0) {
						builder = builder.append("(");
					} else {
						builder = builder.append("AND");
					}
					builder = builder.append(" CAST(");
					builder = builder.append(tableJoinValue);
					builder = builder.append(".Updated_Date AS DATE) ");
					builder = builder.append(excelExportFiltersForm.getEqualityOperator());
					builder = builder.append(" '");
					builder = builder.append(excelExportFiltersForm.getValue());
					builder = builder.append("' ");

					filterValCount++;
				}
			}
			builder = builder.append(")");

		}
		builder = builder.append(")");
		updatedDateTableField = builder.toString();
		if (!updatedBooleanStatus && updatedDateExists) {
			return updatedDateTableField;
		}

		return "";
	}

	/**
	 * Creates the from.
	 * 
	 * @param colMap
	 *            the col map
	 * @param formIds
	 *            the form ids
	 * @param queryDTO
	 *            the query dto
	 * @param tableRecordInfo
	 * @return the string
	 */
	public String createFrom(Map<String, DataImportKeyValueDTO> colMap, List<Long> formIds,
			ExcelExportQueryDTO queryDTO, Map<String, DataImportKeyValueDTO> tableRecordInfo,
			List<String> tableJoinsList, List<ExcelExportFiltersForm> finalFilterList,
			List<String> selectFieldTableNameList, Long companyId, boolean showByEffectiveDateTableData,
			String lundinBatchEndDate) {
		Set<String> selectFieldTableNameListSet = new HashSet<>(selectFieldTableNameList);

		String tableDateStr = "";
		Boolean isUpdatedDateExists = false;
		String updateDateValue = "";
		for (ExcelExportFiltersForm excelExportFiltersForm : finalFilterList) {

			if (excelExportFiltersForm.getDataImportKeyValueDTO().getActualColName() == null) {
				continue;
			}

			if (excelExportFiltersForm.getDataImportKeyValueDTO().getActualColName()
					.equals(PayAsiaConstants.EMPLOYEE_TABLE_COLUMN_UPDATED_DATE)) {
				if ((excelExportFiltersForm.getEqualityOperator()
						.equals(PayAsiaConstants.EMPLOYEE_FILTER_LESS_THAN_OPERATOR))
						|| (excelExportFiltersForm.getEqualityOperator()
								.equals(PayAsiaConstants.EMPLOYEE_FILTER_LESS_THAN_AND_EQUAL_OPERATOR))) {
					updateDateValue = excelExportFiltersForm.getValue();
					isUpdatedDateExists = true;
				}

			}

		}

		StringBuilder from = new StringBuilder("FROM Employee AS employee ");

		tableJoinsList.add("employee");
		for (Long formId : formIds) {

			from.append("LEFT OUTER JOIN Dynamic_Form_Record AS dynamicFormRecord");
			from.append(formId);
			from.append(" ");
			from.append(" ON (employee.Employee_ID = dynamicFormRecord");
			from.append(formId);
			from.append(".Entity_Key) AND (dynamicFormRecord");
			from.append(formId);
			from.append(".Form_ID = ");
			from.append(formId);
			from.append(")");

			String tempTableName = "dynamicFormRecord" + formId;
			if (selectFieldTableNameListSet.contains(tempTableName)) {

				from.append(getUpdatedDateFieldStringOnTable(selectFieldTableNameList, finalFilterList, tempTableName));
			}
			tableJoinsList.add("dynamicFormRecord" + formId);
		}

		Integer noOfTables = tableRecordInfo.size();
		if (noOfTables <= 1) {

			if (queryDTO.getTablePosition() != null) {
				if (isUpdatedDateExists) {
					tableDateStr = "'" + updateDateValue + "'";
				} else {
					if (StringUtils.isNotBlank(lundinBatchEndDate)) {
						tableDateStr = "'" + lundinBatchEndDate + "'";
					} else {
						tableDateStr = "getDate()";
					}
				}

				if (showByEffectiveDateTableData) {
					from.append("LEFT OUTER JOIN Dynamic_Form_Table_Record AS dynamicFormTableRecord_")
							.append(queryDTO.getFormId()).append(queryDTO.getTablePosition())
							.append(" ON (dynamicFormRecord").append(queryDTO.getFormId()).append(".Col_")
							.append(queryDTO.getTablePosition()).append(" = dynamicFormTableRecord_")
							.append(queryDTO.getFormId()).append(queryDTO.getTablePosition())
							.append(".Dynamic_Form_Table_Record_ID  ")
							.append("and CONVERT(datetime,dynamicFormTableRecord_").append(queryDTO.getFormId())
							.append(queryDTO.getTablePosition())
							.append(".Col_1) = (select top 1 max(CONVERT(datetime, dynamicFormTableRecord_max")
							.append(queryDTO.getFormId()).append(queryDTO.getTablePosition())
							.append(".Col_1)) from Dynamic_Form_Table_Record dynamicFormTableRecord_max")
							.append(queryDTO.getFormId()).append(queryDTO.getTablePosition())
							.append(" where dynamicFormTableRecord_max").append(queryDTO.getFormId())
							.append(queryDTO.getTablePosition())
							.append(".Dynamic_Form_Table_Record_ID =dynamicFormTableRecord_")
							.append(queryDTO.getFormId()).append(queryDTO.getTablePosition())
							.append(".Dynamic_Form_Table_Record_ID  and CONVERT(date, dynamicFormTableRecord_max")
							.append(queryDTO.getFormId()).append(queryDTO.getTablePosition()).append(".Col_1) <= ")
							.append(tableDateStr).append(") AND ISNULL(dynamicFormTableRecord_")
							.append(queryDTO.getFormId()).append(queryDTO.getTablePosition()).append(".Company_ID,")
							.append(companyId).append(") =").append(companyId + " )");
				} else {
					from.append("LEFT OUTER JOIN Dynamic_Form_Table_Record AS dynamicFormTableRecord_")
							.append(queryDTO.getFormId()).append(queryDTO.getTablePosition())
							.append(" ON (dynamicFormRecord").append(queryDTO.getFormId()).append(".Col_")
							.append(queryDTO.getTablePosition()).append(" = dynamicFormTableRecord_")
							.append(queryDTO.getFormId()).append(queryDTO.getTablePosition())
							.append(".Dynamic_Form_Table_Record_ID   AND ISNULL(dynamicFormTableRecord_")
							.append(queryDTO.getFormId()).append(queryDTO.getTablePosition()).append(".Company_ID,")
							.append(companyId).append(") =").append(companyId).append(" ) ");
				}

				String tempTableName = "dynamicFormTableRecord_" + queryDTO.getFormId() + queryDTO.getTablePosition();
				if (selectFieldTableNameListSet.contains(tempTableName)) {
					from.append(
							getUpdatedDateFieldStringOnTable(selectFieldTableNameList, finalFilterList, tempTableName));

				}
				tableJoinsList.add("dynamicFormTableRecord_" + queryDTO.getFormId() + queryDTO.getTablePosition());
			}

		} else {

			for (Map.Entry<String, DataImportKeyValueDTO> entry : tableRecordInfo.entrySet()) {

				entry.getKey();
				entry.getValue();

				from.append(" LEFT OUTER JOIN Dynamic_Form_Table_Record AS dynamicFormTableRecord_");
				from.append(entry.getKey());

				tableJoinsList.add("dynamicFormTableRecord_" + entry.getKey());

				if (isUpdatedDateExists) {
					tableDateStr = "'" + updateDateValue + "'";
				} else {
					if (StringUtils.isNotBlank(lundinBatchEndDate)) {
						tableDateStr = "'" + lundinBatchEndDate + "'";
					} else {
						tableDateStr = "getDate()";
					}
				}

				from.append(" ON dynamicFormRecord").append(entry.getValue().getFormId()).append(".Col_")
						.append(entry.getValue().getTablePosition()).append(" = dynamicFormTableRecord_")
						.append(entry.getKey()).append(".Dynamic_Form_Table_Record_ID  ")
						.append("and CONVERT(datetime,dynamicFormTableRecord_").append(entry.getKey())
						.append(".Col_1) = (select top 1 max(CONVERT(datetime, dynamicFormTableRecord_max")
						.append(entry.getKey())
						.append(".Col_1)) from Dynamic_Form_Table_Record dynamicFormTableRecord_max")
						.append(entry.getKey()).append(" where dynamicFormTableRecord_max").append(entry.getKey())
						.append(".Dynamic_Form_Table_Record_ID =dynamicFormTableRecord_").append(entry.getKey())
						.append(".Dynamic_Form_Table_Record_ID  and CONVERT(date, dynamicFormTableRecord_max")
						.append(entry.getKey()).append(".Col_1) <= ").append(tableDateStr)
						.append(") AND ISNULL(dynamicFormTableRecord_").append(entry.getKey()).append(".Company_ID,")
						.append(companyId).append(") =").append(companyId + " ");

				String tempTableName = "dynamicFormTableRecord_" + entry.getKey();
				if (selectFieldTableNameListSet.contains(tempTableName)) {
					from.append(
							getUpdatedDateFieldStringOnTable(selectFieldTableNameList, finalFilterList, tempTableName));

				}
			}

		}

		for (CodeDescDTO codeDescDTO : queryDTO.getCodeDescList()) {

			if (codeDescDTO.isChildVal()) {

				from.append("LEFT OUTER JOIN Dynamic_Form_Field_Ref_Value AS dynamicFormFieldRefValueT")
						.append(codeDescDTO.getFormId()).append(codeDescDTO.getTablePosition())
						.append(codeDescDTO.getMethodName()).append(" ");
				tableJoinsList.add("dynamicFormFieldRefValueT" + codeDescDTO.getFormId()
						+ codeDescDTO.getTablePosition() + codeDescDTO.getMethodName());
				if (noOfTables <= 1) {

					from.append("");

					from.append(" ON (dynamicFormTableRecord_").append(codeDescDTO.getFormId())
							.append(codeDescDTO.getTablePosition()).append(".").append(codeDescDTO.getMethodName())
							.append(" = dynamicFormFieldRefValueT").append(codeDescDTO.getFormId())
							.append(codeDescDTO.getTablePosition()).append(codeDescDTO.getMethodName())
							.append(".Field_Ref_Value_ID ) ");

				} else {
					from.append(" ON (dynamicFormTableRecord_").append(codeDescDTO.getFormId())
							.append(codeDescDTO.getTablePosition()).append(".").append(codeDescDTO.getMethodName())
							.append(" = dynamicFormFieldRefValueT").append(codeDescDTO.getFormId())
							.append(codeDescDTO.getTablePosition()).append(codeDescDTO.getMethodName())
							.append(".Field_Ref_Value_ID ) ");

				}

			} else {
				from.append("LEFT OUTER JOIN Dynamic_Form_Field_Ref_Value AS dynamicFormFieldRefValue")
						.append(codeDescDTO.getFormId()).append(codeDescDTO.getMethodName()).append(" ");
				StringBuilder temp = new StringBuilder("dynamicFormFieldRefValue");
				tableJoinsList.add(temp.append(codeDescDTO.getFormId()).append(codeDescDTO.getMethodName()).toString());

				from.append(" ON (dynamicFormRecord").append(codeDescDTO.getFormId()).append(".")
						.append(codeDescDTO.getMethodName()).append(" = dynamicFormFieldRefValue")
						.append(codeDescDTO.getFormId()).append(codeDescDTO.getMethodName())
						.append(".Field_Ref_Value_ID ) ");

			}

		}

		Iterator<Map.Entry<String, DataImportKeyValueDTO>> entries = colMap.entrySet().iterator();
		while (entries.hasNext()) {
			Entry<String, DataImportKeyValueDTO> thisEntry = entries.next();

			String key = thisEntry.getKey();
			DataImportKeyValueDTO valueDTO = colMap.get(key);
			if (valueDTO.getFieldType().equalsIgnoreCase(PayAsiaConstants.EMPLOYEE_lIST_REFERENCE)) {
				from.append("LEFT OUTER JOIN EMPLOYEE AS ").append(valueDTO.getEmpLstFromEmpField()).append(" ON ")
						.append(valueDTO.getEmpLstFromEmpField()).append(".EMPLOYEE_ID = ")
						.append(valueDTO.getEmpLstFromDynField()).append(" ");
			}

		}

		return String.valueOf(from);
	}

	/**
	 * Creates the from. Group
	 * 
	 * @param colMap
	 *            the col map
	 * @param formIds
	 *            the form ids
	 * @param queryDTO
	 *            the query dto
	 * @param tableRecordInfo
	 * @return the string
	 */
	public String createFromGroup(Map<String, DataImportKeyValueDTO> colMap, List<Long> formIds,
			ExcelExportQueryDTO queryDTO, Map<String, DataImportKeyValueDTO> tableRecordInfo,
			List<String> selectFieldTableNameList, List<ExcelExportFiltersForm> finalFilterList, Long companyId) {
		Set<String> selectFieldTableNameListSet = new HashSet<>(selectFieldTableNameList);

		StringBuilder from = new StringBuilder("FROM Employee AS employee ");

		for (Long formId : formIds) {

			from.append("LEFT OUTER JOIN Dynamic_Form_Record AS dynamicFormRecord").append(formId).append(" ");
			from.append(" ON (employee.Employee_ID = dynamicFormRecord").append(formId)
					.append(".Entity_Key) AND (dynamicFormRecord").append(formId).append(".Form_ID = ").append(formId)
					.append(" ) ");
			String tempTableName = "dynamicFormRecord" + formId;
			if (selectFieldTableNameListSet.contains(tempTableName)) {
				from.append(getUpdatedDateFieldStringOnTable(selectFieldTableNameList, finalFilterList, tempTableName));
			}

		}

		Integer noOfTables = tableRecordInfo.size();
		if (noOfTables <= 1) {

			if (queryDTO.getTablePosition() != null) {
				from.append("LEFT OUTER JOIN Dynamic_Form_Table_Record AS dynamicFormTableRecord_")
						.append(queryDTO.getFormId()).append(queryDTO.getTablePosition());
				from.append(" ON (dynamicFormRecord").append(queryDTO.getFormId()).append(".Col_")
						.append(queryDTO.getTablePosition()).append(" = dynamicFormTableRecord_")
						.append(queryDTO.getFormId()).append(queryDTO.getTablePosition())
						.append(".Dynamic_Form_Table_Record_ID   AND ISNULL(dynamicFormTableRecord_")
						.append(queryDTO.getFormId()).append(queryDTO.getTablePosition()).append(".Company_ID,")
						.append(companyId).append(") =").append(companyId).append(" ) ");

				String tempTableName = "dynamicFormTableRecord_" + queryDTO.getFormId() + queryDTO.getTablePosition();
				if (selectFieldTableNameListSet.contains(tempTableName)) {
					from.append(
							getUpdatedDateFieldStringOnTable(selectFieldTableNameList, finalFilterList, tempTableName));
				}

			}

		} else {

			for (Map.Entry<String, DataImportKeyValueDTO> entry : tableRecordInfo.entrySet()) {

				entry.getKey();
				entry.getValue();

				from.append(" LEFT OUTER JOIN Dynamic_Form_Table_Record AS dynamicFormTableRecord_")
						.append(entry.getKey());
				from.append(" ON dynamicFormRecord").append(entry.getValue().getFormId()).append(".Col_")
						.append(entry.getValue().getTablePosition()).append(" = dynamicFormTableRecord_")
						.append(entry.getKey()).append(".Dynamic_Form_Table_Record_ID  ")
						.append("and CONVERT(datetime,dynamicFormTableRecord_").append(entry.getKey())
						.append(".Col_1) = (select max(CONVERT(datetime, dynamicFormTableRecord_max")
						.append(entry.getKey())
						.append(".Col_1)) from Dynamic_Form_Table_Record dynamicFormTableRecord_max")
						.append(entry.getKey()).append(" where dynamicFormTableRecord_max").append(entry.getKey())
						.append(".Dynamic_Form_Table_Record_ID =dynamicFormTableRecord_").append(entry.getKey())
						.append(".Dynamic_Form_Table_Record_ID  and CONVERT(date, dynamicFormTableRecord_max")
						.append(entry.getKey()).append(".Col_1) <= getdate()) ")
						.append(" AND ISNULL(dynamicFormTableRecord_").append(entry.getKey()).append(".Company_ID,")
						.append(companyId).append(") =").append(companyId + " ");

				String tempTableName = "dynamicFormTableRecord_" + entry.getKey();
				if (selectFieldTableNameListSet.contains(tempTableName)) {
					from.append(
							getUpdatedDateFieldStringOnTable(selectFieldTableNameList, finalFilterList, tempTableName));
				}

			}

		}

		for (CodeDescDTO codeDescDTO : queryDTO.getCodeDescList()) {

			if (codeDescDTO.isChildVal()) {
				from.append("LEFT OUTER JOIN Dynamic_Form_Field_Ref_Value AS dynamicFormFieldRefValueT")
						.append(codeDescDTO.getFormId()).append(codeDescDTO.getTablePosition())
						.append(codeDescDTO.getMethodName()).append(" ");

				if (noOfTables <= 1) {

					from.append(" ON (dynamicFormTableRecord_").append(codeDescDTO.getFormId())
							.append(codeDescDTO.getTablePosition()).append(".").append(codeDescDTO.getMethodName())
							.append(" = dynamicFormFieldRefValueT").append(codeDescDTO.getFormId())
							.append(codeDescDTO.getTablePosition()).append(codeDescDTO.getMethodName())
							.append(".Field_Ref_Value_ID ) ");

				} else {
					from.append(" ON (dynamicFormTableRecord_").append(codeDescDTO.getFormId())
							.append(codeDescDTO.getTablePosition()).append(".").append(codeDescDTO.getMethodName())
							.append(" = dynamicFormFieldRefValueT").append(codeDescDTO.getFormId())
							.append(codeDescDTO.getTablePosition()).append(codeDescDTO.getMethodName())
							.append(".Field_Ref_Value_ID ) ");

				}

			} else {
				from.append("LEFT OUTER JOIN Dynamic_Form_Field_Ref_Value AS dynamicFormFieldRefValue")
						.append(codeDescDTO.getFormId()).append(codeDescDTO.getMethodName()).append(" ");
				from.append(" ON (dynamicFormRecord").append(codeDescDTO.getFormId()).append(".")
						.append(codeDescDTO.getMethodName()).append(" = dynamicFormFieldRefValue")
						.append(codeDescDTO.getFormId()).append(codeDescDTO.getMethodName())
						.append(".Field_Ref_Value_ID) ");

			}

		}

		Iterator<Map.Entry<String, DataImportKeyValueDTO>> entries = colMap.entrySet().iterator();
		while (entries.hasNext()) {
			Entry<String, DataImportKeyValueDTO> thisEntry = entries.next();
			String key = thisEntry.getKey();
			DataImportKeyValueDTO valueDTO = colMap.get(key);
			if (valueDTO.getFieldType().equalsIgnoreCase(PayAsiaConstants.EMPLOYEE_lIST_REFERENCE)) {
				from.append("LEFT OUTER JOIN EMPLOYEE AS ").append(valueDTO.getEmpLstFromEmpField()).append(" ON ")
						.append(valueDTO.getEmpLstFromEmpField()).append(".EMPLOYEE_ID = ")
						.append(valueDTO.getEmpLstFromDynField()).append(" ");
			}

		}

		return String.valueOf(from);
	}

	/**
	 * Creates the where.
	 * 
	 * @param companyId
	 *            the company id
	 * @param finalFilterList
	 *            the final filter list
	 * @param dataFormat
	 *            the data format
	 * @return the string
	 */
	public String createWhere(long companyId, List<ExcelExportFiltersForm> finalFilterList, String dataFormat,
			Map<String, DataImportKeyValueDTO> tableRecordInfo, EmployeeShortListDTO employeeShortListDTO,
			List<String> tableJoinsList, List<String> selectFieldTableNameList, Map<String, String> paramValueMap,
			Map<String, List<BigInteger>> paramEmpIdsMap) {
		Integer noOfTables = tableRecordInfo.size();
		StringBuilder where = new StringBuilder(" WHERE employee.Company_ID =:companyId ");

		if (!finalFilterList.isEmpty()) {
			where.append(" AND ");
		}
		// String updatedDateTableField = "(";
		int tableJoinCount = 0;
		Boolean updatedBooleanStatus = false;
		Boolean updatedDateExists = false;

		Set<String> selectFieldTableNameListSet = new HashSet<>(selectFieldTableNameList);
		StringBuilder updateDateTableFieldBuilder = new StringBuilder("(");
		int updatedDateParamCtr = 1;
		for (String tableJoinValue : selectFieldTableNameListSet) {
			int filterValCount = 0;
			Boolean status = true;

			if (tableJoinValue.startsWith(PayAsiaConstants.PAYASIA_DYNAMIC_FORM_FIELD_REF_VALUE_TABLE)) {
				continue;
			}

			if (tableJoinCount > 0) {
				updateDateTableFieldBuilder.append("OR");
			}
			for (ExcelExportFiltersForm excelExportFiltersForm : finalFilterList) {

				if (filterValCount > 2) {
					updatedBooleanStatus = true;
				}
				if (excelExportFiltersForm.getDataImportKeyValueDTO().getActualColName() == null) {
					continue;
				}
				if (!excelExportFiltersForm.getDataImportKeyValueDTO().getActualColName()
						.equals(PayAsiaConstants.EMPLOYEE_TABLE_COLUMN_UPDATED_DATE)) {
					continue;
				}

				if (status) {
					updatedDateExists = true;
					if (filterValCount == 0) {
						updateDateTableFieldBuilder.append("(");
					} else {
						updateDateTableFieldBuilder.append("AND ");

					}
					paramValueMap.put("updatedDate" + updatedDateParamCtr, excelExportFiltersForm.getValue());
					updateDateTableFieldBuilder.append(" CAST(");
					updateDateTableFieldBuilder.append(tableJoinValue);
					updateDateTableFieldBuilder.append(".Updated_Date AS DATE) ");
					updateDateTableFieldBuilder.append(excelExportFiltersForm.getEqualityOperator());
					updateDateTableFieldBuilder.append(":updatedDate" + updatedDateParamCtr + " ");

					filterValCount++;
					updatedDateParamCtr++;
				}

			}

			tableJoinCount++;
			updateDateTableFieldBuilder.append(")");

		}
		updateDateTableFieldBuilder.append(")");
		int count = 1;
		for (ExcelExportFiltersForm excelExportFiltersForm : finalFilterList) {

			Boolean sysDateField = false;
			ColumnPropertyDTO columnPropertyDTO = null;
			if (excelExportFiltersForm.getEqualityOperator().equals(PayAsiaConstants.IS_EQUAL_TO_SYSDATE_BY)
					|| excelExportFiltersForm.getEqualityOperator()
							.equals(PayAsiaConstants.IS_GREATER_THAN_EQUAL_TO_SYSDATE_BY)
					|| excelExportFiltersForm.getEqualityOperator().equals(PayAsiaConstants.IS_GREATER_THAN_SYSDATE_BY)
					|| excelExportFiltersForm.getEqualityOperator()
							.equals(PayAsiaConstants.IS_LESS_THAN_EQUAL_TO_SYSDATE_BY)
					|| excelExportFiltersForm.getEqualityOperator().equals(PayAsiaConstants.IS_LESS_THAN_SYSDATE_BY)
					|| excelExportFiltersForm.getEqualityOperator()
							.equals(PayAsiaConstants.IS_NOT_EQUAL_TO_SYSDATE_BY)) {
				sysDateField = true;

			} else {

				sysDateField = false;

			}

			if (excelExportFiltersForm.getDataImportKeyValueDTO().getActualColName() != null) {
				if (excelExportFiltersForm.getDataImportKeyValueDTO().getActualColName()
						.equals(PayAsiaConstants.EMPLOYEE_TABLE_COLUMN_UPDATED_DATE)) {
					continue;
				}
			}

			if (excelExportFiltersForm.getDataImportKeyValueDTO().isStatic()) {

				columnPropertyDTO = generalDAO.getColumnProperties(PayAsiaConstants.EMPLOYEE_TABLE_NAME,
						excelExportFiltersForm.getDataImportKeyValueDTO().getActualColName());

				if (sysDateField) {
					paramValueMap.put("sysDate" + count, excelExportFiltersForm.getValue());
					where.append(" ").append(excelExportFiltersForm.getOpenBracket()).append("cast(dateadd(day,")
							.append(":sysDate" + count).append(",employee.")
							.append(excelExportFiltersForm.getDataImportKeyValueDTO().getActualColName())
							.append(") as date)").append(" ");

				} else {
					where.append(" ").append(excelExportFiltersForm.getOpenBracket()).append(" employee.")
							.append(excelExportFiltersForm.getDataImportKeyValueDTO().getActualColName()).append(" ");
				}

			} else {

				if (!excelExportFiltersForm.getDataImportKeyValueDTO().isChild()) {

					if (excelExportFiltersForm.getDataImportKeyValueDTO().getFieldType().equalsIgnoreCase("numeric")) {
						where.append(" ").append(excelExportFiltersForm.getOpenBracket()).append(" dynamicFormRecord")
								.append(excelExportFiltersForm.getDataImportKeyValueDTO().getFormId()).append(".Col_")
								.append(excelExportFiltersForm.getDataImportKeyValueDTO().getMethodName()).append(" ");
					} else if (excelExportFiltersForm.getDataImportKeyValueDTO().getFieldType()
							.equalsIgnoreCase("date")) {

						if (sysDateField) {
							paramValueMap.put("dynamicFormRecordSysDate" + count, excelExportFiltersForm.getValue());
							where.append(" ").append(excelExportFiltersForm.getOpenBracket())
									.append("dateadd(day,:dynamicFormRecordSysDate" + count
											+ ",convert(date, dynamicFormRecord")
									.append(excelExportFiltersForm.getDataImportKeyValueDTO().getFormId())
									.append(".Col_")
									.append(excelExportFiltersForm.getDataImportKeyValueDTO().getMethodName())
									.append(")) ");

						} else {
							where.append(" ").append(excelExportFiltersForm.getOpenBracket())
									.append("convert(datetime, dynamicFormRecord")
									.append(excelExportFiltersForm.getDataImportKeyValueDTO().getFormId())
									.append(".Col_")
									.append(excelExportFiltersForm.getDataImportKeyValueDTO().getMethodName())
									.append(") ");
						}

					} else if (excelExportFiltersForm.getDataImportKeyValueDTO().getFieldType()
							.equalsIgnoreCase("codedesc")) {

						where.append(" ").append(excelExportFiltersForm.getOpenBracket())
								.append(" dynamicFormFieldRefValue")
								.append(excelExportFiltersForm.getDataImportKeyValueDTO().getFormId()).append("Col_")
								.append(excelExportFiltersForm.getDataImportKeyValueDTO().getMethodName())
								.append(".Code ");

					} else {
						where.append(" ").append(excelExportFiltersForm.getOpenBracket()).append(" dynamicFormRecord")
								.append(excelExportFiltersForm.getDataImportKeyValueDTO().getFormId()).append(".Col_")
								.append(excelExportFiltersForm.getDataImportKeyValueDTO().getMethodName()).append(" ");
					}
				} else {

					if (excelExportFiltersForm.getDataImportKeyValueDTO().getFieldType().equalsIgnoreCase("numeric")) {

						if (noOfTables <= 1) {

							where.append(" ").append(excelExportFiltersForm.getOpenBracket())
									.append("dynamicFormTableRecord_")
									.append(excelExportFiltersForm.getDataImportKeyValueDTO().getFormId())
									.append(excelExportFiltersForm.getDataImportKeyValueDTO().getTablePosition())
									.append(".Col_")
									.append(excelExportFiltersForm.getDataImportKeyValueDTO().getMethodName())
									.append("  ");

						} else {
							where.append(" ").append(excelExportFiltersForm.getOpenBracket())
									.append(" dynamicFormTableRecord_")
									.append(excelExportFiltersForm.getDataImportKeyValueDTO().getFormId())
									.append(excelExportFiltersForm.getDataImportKeyValueDTO().getTablePosition())
									.append(".Col_")
									.append(excelExportFiltersForm.getDataImportKeyValueDTO().getMethodName())
									.append("  ");

						}

					} else if (excelExportFiltersForm.getDataImportKeyValueDTO().getFieldType()
							.equalsIgnoreCase("date")) {

						if (sysDateField) {
							paramValueMap.put("dynamicFormTableRecordSysDate" + count,
									excelExportFiltersForm.getValue());
							where.append(" ").append(excelExportFiltersForm.getOpenBracket()).append("dateadd(day,")
									.append(":dynamicFormTableRecordSysDate" + count)
									.append(",convert(datetime, ltrim(dynamicFormTableRecord_")
									.append(excelExportFiltersForm.getDataImportKeyValueDTO().getFormId())
									.append(excelExportFiltersForm.getDataImportKeyValueDTO().getTablePosition())
									.append(".Col_")
									.append(excelExportFiltersForm.getDataImportKeyValueDTO().getMethodName())
									.append("))) ");

						} else {

							where.append(" ");
							where.append(excelExportFiltersForm.getOpenBracket());
							where.append("convert(datetime, ltrim(dynamicFormTableRecord_");
							where.append(excelExportFiltersForm.getDataImportKeyValueDTO().getFormId());
							where.append(excelExportFiltersForm.getDataImportKeyValueDTO().getTablePosition());
							where.append(".Col_")
									.append(excelExportFiltersForm.getDataImportKeyValueDTO().getMethodName());
							where.append(")) ");

						}

					} else if (excelExportFiltersForm.getDataImportKeyValueDTO().getFieldType()
							.equalsIgnoreCase("codedesc")) {
						where.append(" ").append(excelExportFiltersForm.getOpenBracket())
								.append(" dynamicFormFieldRefValueT")
								.append(excelExportFiltersForm.getDataImportKeyValueDTO().getFormId())
								.append(excelExportFiltersForm.getDataImportKeyValueDTO().getTablePosition())
								.append("Col_")
								.append(excelExportFiltersForm.getDataImportKeyValueDTO().getMethodName())
								.append(".Code ");
					} else {

						if (noOfTables <= 1) {

							where.append(" ").append(excelExportFiltersForm.getOpenBracket())
									.append(" dynamicFormTableRecord_")
									.append(excelExportFiltersForm.getDataImportKeyValueDTO().getFormId())
									.append(excelExportFiltersForm.getDataImportKeyValueDTO().getTablePosition())
									.append(".Col_")
									.append(excelExportFiltersForm.getDataImportKeyValueDTO().getMethodName() + " ");

						} else {

							where.append(" ").append(excelExportFiltersForm.getOpenBracket())
									.append(" dynamicFormTableRecord_")
									.append(excelExportFiltersForm.getDataImportKeyValueDTO().getFormId())
									.append(excelExportFiltersForm.getDataImportKeyValueDTO().getTablePosition())
									.append(".Col_")
									.append(excelExportFiltersForm.getDataImportKeyValueDTO().getMethodName())
									.append(" ");

						}

					}
				}

			}

			if (count < finalFilterList.size()) {

				if (excelExportFiltersForm.getDataImportKeyValueDTO().getFieldType().equalsIgnoreCase("numeric")) {
					StringBuilder sb = new StringBuilder("param");
					paramValueMap.put(sb.append(count).toString(), excelExportFiltersForm.getValue());
					where.append(excelExportFiltersForm.getEqualityOperator()).append(":param").append(count)
							.append(" ").append(excelExportFiltersForm.getCloseBracket()).append(" ")
							.append(excelExportFiltersForm.getLogicalOperator()).append(" ");
				} else {
					StringBuilder sb = new StringBuilder("param");
					paramValueMap.put(sb.append(count).toString(),
							getEqualityOperatorValue(excelExportFiltersForm.getEqualityOperator(),
									excelExportFiltersForm.getValue(), companyId, columnPropertyDTO,
									excelExportFiltersForm));
					where.append(getEqualityOperator(excelExportFiltersForm.getEqualityOperator()))
							// .append(" N")
							.append(":param").append(count).append(" ").append(" ")
							.append(excelExportFiltersForm.getCloseBracket()).append(" ")
							.append(excelExportFiltersForm.getLogicalOperator()).append(" ");

				}

			} else {

				if (excelExportFiltersForm.getDataImportKeyValueDTO().getFieldType().equalsIgnoreCase("numeric")) {
					paramValueMap.put("param" + count, excelExportFiltersForm.getValue());
					where.append(excelExportFiltersForm.getEqualityOperator()).append(":param").append(count)
							.append(" ").append(excelExportFiltersForm.getCloseBracket()).append(" ");
				} else {
					StringBuilder sb = new StringBuilder("param");
					paramValueMap.put(sb.append(count).toString(),
							getEqualityOperatorValue(excelExportFiltersForm.getEqualityOperator(),
									excelExportFiltersForm.getValue(), companyId, columnPropertyDTO,
									excelExportFiltersForm));
					where.append(getEqualityOperator(excelExportFiltersForm.getEqualityOperator()))
							// .append(" N")
							.append(":param").append(count).append(" ").append(" ")
							.append(excelExportFiltersForm.getCloseBracket()).append(" ");
				}

			}

			count++;
		}

		if (!updatedBooleanStatus && updatedDateExists) {
			where.append(updateDateTableFieldBuilder.toString());
		}

		if (employeeShortListDTO.getEmployeeShortList() && employeeShortListDTO.getShortListEmployeeIds().isEmpty()) {

			where.append(" and ").append(" employee.Employee_ID in (-1)");

		} else if (employeeShortListDTO.getEmployeeShortList()
				&& !employeeShortListDTO.getShortListEmployeeIds().isEmpty()) {
			where.append(" and ")
					.append(getEmployeeCond(employeeShortListDTO.getShortListEmployeeIds(), paramEmpIdsMap));

		}

		String subWhere = String.valueOf(where);

		subWhere = subWhere.trim();
		if (subWhere.endsWith("AND")) {
			subWhere = StringUtils.removeEnd(subWhere, "AND");
		}
		subWhere = subWhere + " ORDER BY employee.Employee_ID DESC";

		return subWhere;
	}

	private String getEmployeeCond(List<BigInteger> employeeIds, Map<String, List<BigInteger>> paramEmpIdsMap) {

		paramEmpIdsMap.put("shortListEmployeeIds", employeeIds);
		StringBuilder builder = new StringBuilder();
		builder = builder.append(" employee.Employee_ID in (:shortListEmployeeIds) ");
		// for (int empCount = 1; empCount <= employeeIds.size(); empCount++) {
		//
		// builder = builder.append(employeeIds.get(empCount - 1));
		// if (empCount != employeeIds.size()) {
		// builder = builder.append(",");
		// }
		//
		// }
		// builder = builder.append(" ) ");
		String employeeCondition = builder.toString();
		return employeeCondition;
	}

	/**
	 * Creates the where. Group
	 * 
	 * @param companyId
	 *            the company id
	 * @param finalFilterList
	 *            the final filter list
	 * @param dataFormat
	 *            the data format
	 * @return the string
	 */
	public String createWhereGroup(long companyId, List<ExcelExportFiltersForm> finalFilterList,
			Map<String, DataImportKeyValueDTO> tableRecordInfo, EmployeeShortListDTO employeeShortListDTO,
			List<String> selectFieldTableNameList, Map<String, String> paramValueMap,
			Map<String, List<BigInteger>> paramEmpIdsMap) {

		StringBuilder where = new StringBuilder(" WHERE employee.Company_ID =:companyId ");

		if (!finalFilterList.isEmpty()) {
			where.append(" AND ");
		}
		StringBuilder updateDateTableFieldBuilder = new StringBuilder();
		updateDateTableFieldBuilder = updateDateTableFieldBuilder.append("(");

		int tableJoinCount = 0;
		Boolean updatedBooleanStatus = false;
		Boolean updatedDateExists = false;

		Set<String> selectFieldTableNameListSet = new HashSet<>(selectFieldTableNameList);
		int updatedDateParamCtr = 1;
		for (String tableJoinValue : selectFieldTableNameListSet) {
			int filterValCount = 0;
			Boolean status = true;

			if (tableJoinValue.startsWith(PayAsiaConstants.PAYASIA_DYNAMIC_FORM_FIELD_REF_VALUE_TABLE)) {
				continue;
			}

			if (tableJoinCount > 0) {
				updateDateTableFieldBuilder = updateDateTableFieldBuilder.append("OR");
			}

			for (ExcelExportFiltersForm excelExportFiltersForm : finalFilterList) {

				if (filterValCount > 2) {
					updatedBooleanStatus = true;
				}
				if (excelExportFiltersForm.getDataImportKeyValueDTO().getActualColName() == null) {
					continue;
				}
				if (!excelExportFiltersForm.getDataImportKeyValueDTO().getActualColName()
						.equals(PayAsiaConstants.EMPLOYEE_TABLE_COLUMN_UPDATED_DATE)) {
					continue;
				}

				if (status) {
					updatedDateExists = true;
					if (filterValCount == 0) {
						updateDateTableFieldBuilder = updateDateTableFieldBuilder.append("(");
					} else {
						updateDateTableFieldBuilder = updateDateTableFieldBuilder.append("AND ");
					}
					updateDateTableFieldBuilder = updateDateTableFieldBuilder.append(" CAST(");
					updateDateTableFieldBuilder = updateDateTableFieldBuilder.append(tableJoinValue);
					updateDateTableFieldBuilder = updateDateTableFieldBuilder.append(".Updated_Date AS DATE) ");
					updateDateTableFieldBuilder = updateDateTableFieldBuilder
							.append(excelExportFiltersForm.getEqualityOperator());
					paramValueMap.put("updatedDate" + updatedDateParamCtr, excelExportFiltersForm.getValue());
					updateDateTableFieldBuilder = updateDateTableFieldBuilder
							.append(":updatedDate" + updatedDateParamCtr + " ");
					updatedDateParamCtr++;
					filterValCount++;
				}

			}

			tableJoinCount++;
			updateDateTableFieldBuilder = updateDateTableFieldBuilder.append(")");

		}
		updateDateTableFieldBuilder = updateDateTableFieldBuilder.append(")");

		String updatedDateTableField = updateDateTableFieldBuilder.toString();
		int count = 1;
		for (ExcelExportFiltersForm excelExportFiltersForm : finalFilterList) {

			Boolean sysDateField = false;
			ColumnPropertyDTO columnPropertyDTO = null;
			if (excelExportFiltersForm.getEqualityOperator().equals(PayAsiaConstants.IS_EQUAL_TO_SYSDATE_BY)
					|| excelExportFiltersForm.getEqualityOperator()
							.equals(PayAsiaConstants.IS_GREATER_THAN_EQUAL_TO_SYSDATE_BY)
					|| excelExportFiltersForm.getEqualityOperator().equals(PayAsiaConstants.IS_GREATER_THAN_SYSDATE_BY)
					|| excelExportFiltersForm.getEqualityOperator()
							.equals(PayAsiaConstants.IS_LESS_THAN_EQUAL_TO_SYSDATE_BY)
					|| excelExportFiltersForm.getEqualityOperator().equals(PayAsiaConstants.IS_LESS_THAN_SYSDATE_BY)
					|| excelExportFiltersForm.getEqualityOperator()
							.equals(PayAsiaConstants.IS_NOT_EQUAL_TO_SYSDATE_BY)) {
				sysDateField = true;

			} else {

				sysDateField = false;

			}

			if (excelExportFiltersForm.getDataImportKeyValueDTO().getActualColName() != null) {
				if (excelExportFiltersForm.getDataImportKeyValueDTO().getActualColName()
						.equals(PayAsiaConstants.EMPLOYEE_TABLE_COLUMN_UPDATED_DATE)) {
					continue;
				}
			}

			if (excelExportFiltersForm.getDataImportKeyValueDTO().isStatic()) {

				columnPropertyDTO = generalDAO.getColumnProperties(PayAsiaConstants.EMPLOYEE_TABLE_NAME,
						excelExportFiltersForm.getDataImportKeyValueDTO().getActualColName());

				if (sysDateField) {
					paramValueMap.put("sysDate" + count, excelExportFiltersForm.getValue());
					where.append(" ").append(excelExportFiltersForm.getOpenBracket()).append("cast(dateadd(day,")
							.append(":sysDate" + count).append(",employee.")
							.append(excelExportFiltersForm.getDataImportKeyValueDTO().getActualColName())
							.append(") as date)").append(" ");

				} else {
					where.append(" ").append(excelExportFiltersForm.getOpenBracket()).append(" employee.")
							.append(excelExportFiltersForm.getDataImportKeyValueDTO().getActualColName()).append(" ");
				}

			} else {

				if (!excelExportFiltersForm.getDataImportKeyValueDTO().isChild()) {

					if (excelExportFiltersForm.getDataImportKeyValueDTO().getFieldType().equalsIgnoreCase("numeric")) {
						where.append(" ").append(excelExportFiltersForm.getOpenBracket()).append(" dynamicFormRecord")
								.append(excelExportFiltersForm.getDataImportKeyValueDTO().getFormId()).append(".Col_")
								.append(excelExportFiltersForm.getDataImportKeyValueDTO().getMethodName()).append("  ");
					} else if (excelExportFiltersForm.getDataImportKeyValueDTO().getFieldType()
							.equalsIgnoreCase("date")) {

						if (sysDateField) {
							paramValueMap.put("dynamicFormRecordSysDate" + count, excelExportFiltersForm.getValue());
							where.append(" ").append(excelExportFiltersForm.getOpenBracket()).append("dateadd(day,")
									.append(":dynamicFormRecordSysDate" + count)
									.append(",convert(datetime, dynamicFormRecord")
									.append(excelExportFiltersForm.getDataImportKeyValueDTO().getFormId())
									.append(".Col_")
									.append(excelExportFiltersForm.getDataImportKeyValueDTO().getMethodName())
									.append(")) ");

						} else {
							where.append(" ").append(excelExportFiltersForm.getOpenBracket())
									.append("convert(datetime, dynamicFormRecord")
									.append(excelExportFiltersForm.getDataImportKeyValueDTO().getFormId())
									.append(".Col_")
									.append(excelExportFiltersForm.getDataImportKeyValueDTO().getMethodName())
									.append(") ");
						}

					} else if (excelExportFiltersForm.getDataImportKeyValueDTO().getFieldType()
							.equalsIgnoreCase("codedesc")) {

						where.append(" ").append(excelExportFiltersForm.getOpenBracket())
								.append(" dynamicFormFieldRefValue")
								.append(excelExportFiltersForm.getDataImportKeyValueDTO().getFormId()).append("Col_")
								.append(excelExportFiltersForm.getDataImportKeyValueDTO().getMethodName())
								.append(".Code ");

					} else {
						where.append(" ").append(excelExportFiltersForm.getOpenBracket()).append(" dynamicFormRecord")
								.append(excelExportFiltersForm.getDataImportKeyValueDTO().getFormId()).append(".Col_")
								.append(excelExportFiltersForm.getDataImportKeyValueDTO().getMethodName() + " ");
					}
				} else {

					if (excelExportFiltersForm.getDataImportKeyValueDTO().getFieldType().equalsIgnoreCase("numeric")) {

						where.append(" ").append(excelExportFiltersForm.getOpenBracket())
								.append(" dynamicFormTableRecord_")
								.append(excelExportFiltersForm.getDataImportKeyValueDTO().getFormId())
								.append(excelExportFiltersForm.getDataImportKeyValueDTO().getTablePosition())
								.append(".Col_")
								.append(excelExportFiltersForm.getDataImportKeyValueDTO().getMethodName() + " ");

					} else if (excelExportFiltersForm.getDataImportKeyValueDTO().getFieldType()
							.equalsIgnoreCase("date")) {

						if (sysDateField) {
							paramValueMap.put("dynamicFormTableRecordSysDate" + count,
									excelExportFiltersForm.getValue());
							where.append(" ").append(excelExportFiltersForm.getOpenBracket()).append("dateadd(day,")
									.append(":dynamicFormTableRecordSysDate" + count)
									.append(",convert(datetime, ltrim(dynamicFormTableRecord_")
									.append(excelExportFiltersForm.getDataImportKeyValueDTO().getFormId())
									.append(excelExportFiltersForm.getDataImportKeyValueDTO().getTablePosition())
									.append(".Col_")
									.append(excelExportFiltersForm.getDataImportKeyValueDTO().getMethodName() + "))) ");

						} else {
							where.append(" ").append(excelExportFiltersForm.getOpenBracket())
									.append("convert(datetime, ltrim(dynamicFormTableRecord_")
									.append(excelExportFiltersForm.getDataImportKeyValueDTO().getFormId())
									.append(excelExportFiltersForm.getDataImportKeyValueDTO().getTablePosition())
									.append(".Col_")
									.append(excelExportFiltersForm.getDataImportKeyValueDTO().getMethodName() + ")) ");
						}

					} else if (excelExportFiltersForm.getDataImportKeyValueDTO().getFieldType()
							.equalsIgnoreCase("codedesc")) {
						where.append(" ").append(excelExportFiltersForm.getOpenBracket())
								.append(" dynamicFormFieldRefValueT")
								.append(excelExportFiltersForm.getDataImportKeyValueDTO().getFormId())
								.append(excelExportFiltersForm.getDataImportKeyValueDTO().getTablePosition())
								.append("Col_")
								.append(excelExportFiltersForm.getDataImportKeyValueDTO().getMethodName() + ".Code ");
					} else {

						where.append(" ").append(excelExportFiltersForm.getOpenBracket())
								.append(" dynamicFormTableRecord_")
								.append(excelExportFiltersForm.getDataImportKeyValueDTO().getFormId())
								.append(excelExportFiltersForm.getDataImportKeyValueDTO().getTablePosition())
								.append(".Col_")
								.append(excelExportFiltersForm.getDataImportKeyValueDTO().getMethodName() + " ");

					}
				}

			}
			if (count < finalFilterList.size()) {

				if (excelExportFiltersForm.getDataImportKeyValueDTO().getFieldType().equalsIgnoreCase("numeric")) {
					paramValueMap.put("param" + count, excelExportFiltersForm.getValue());
					where.append(excelExportFiltersForm.getEqualityOperator()).append(":param" + count + " ")
							.append(excelExportFiltersForm.getCloseBracket() + " ")
							.append(excelExportFiltersForm.getLogicalOperator() + " ");
				} else {
					paramValueMap.put("param" + count,
							getEqualityOperatorValue(excelExportFiltersForm.getEqualityOperator(),
									excelExportFiltersForm.getValue(), companyId, columnPropertyDTO,
									excelExportFiltersForm));
					where.append(getEqualityOperator(excelExportFiltersForm.getEqualityOperator()))
							// .append(" N'")
							.append(":param" + count + " " + " ").append(excelExportFiltersForm.getCloseBracket() + " ")
							.append(excelExportFiltersForm.getLogicalOperator() + " ");

				}

			} else {

				if (excelExportFiltersForm.getDataImportKeyValueDTO().getFieldType().equalsIgnoreCase("numeric")) {
					paramValueMap.put("param" + count, excelExportFiltersForm.getValue());
					where.append(excelExportFiltersForm.getEqualityOperator()).append(":param" + count + " ")
							.append(" ").append(excelExportFiltersForm.getCloseBracket() + " ");
				} else {
					paramValueMap.put("param" + count,
							getEqualityOperatorValue(excelExportFiltersForm.getEqualityOperator(),
									excelExportFiltersForm.getValue(), companyId, columnPropertyDTO,
									excelExportFiltersForm));
					where.append(getEqualityOperator(excelExportFiltersForm.getEqualityOperator()))
							// .append(" N'")
							.append(":param" + count + " " + " ")
							.append(excelExportFiltersForm.getCloseBracket() + " ");
				}

			}

			count++;
		}

		if (!updatedBooleanStatus && updatedDateExists) {
			where.append(updatedDateTableField);
		}

		if (employeeShortListDTO.getEmployeeShortList() && employeeShortListDTO.getShortListEmployeeIds().isEmpty()) {

			where.append(" and ").append(" employee.Employee_ID in (-1)");

		} else if (employeeShortListDTO.getEmployeeShortList()
				&& !employeeShortListDTO.getShortListEmployeeIds().isEmpty()) {
			where.append(" and ")
					.append(getEmployeeCond(employeeShortListDTO.getShortListEmployeeIds(), paramEmpIdsMap));

		}
		String subWhere = String.valueOf(where).trim();
		if (subWhere.endsWith("AND")) {
			subWhere = StringUtils.removeEnd(subWhere, "AND");
		}
		subWhere = subWhere + " ORDER BY employee.Employee_ID DESC";

		return subWhere;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.EmployeeDAO#findByEmailId(java.lang.Long,
	 * java.lang.String)
	 */
	@Override
	public Employee findByEmailId(Long employeeID, String email) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Employee> criteriaQuery = cb.createQuery(Employee.class);
		Root<Employee> employeeRoot = criteriaQuery.from(Employee.class);
		criteriaQuery.select(employeeRoot);

		Predicate restriction = cb.conjunction();
		if (email != null) {
			restriction = cb.and(restriction, cb.equal(employeeRoot.get(Employee_.email), email));
		}

		if (employeeID != null) {
			restriction = cb.and(restriction, cb.notEqual(employeeRoot.get(Employee_.employeeId), employeeID));

		}

		criteriaQuery.where(restriction);

		TypedQuery<Employee> maxEmployeeIdQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<Employee> employeeList = maxEmployeeIdQuery.getResultList();
		if (employeeList != null && !employeeList.isEmpty()) {
			return employeeList.get(0);
		}
		return null;
	}

	@Override
	public Long getEmpCountForSendPwd(AccessControlConditionDTO conditionDTO, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
		Root<Employee> empRoot = criteriaQuery.from(Employee.class);
		criteriaQuery.select(cb.count(empRoot));

		Join<Employee, EmployeeLoginDetail> loginDetailJoin = empRoot.join(Employee_.employeeLoginDetails);

		Predicate restriction = cb.conjunction();

		Join<Employee, Company> empCompanyJoin = empRoot.join(Employee_.company);

		Path<Long> companyID = empCompanyJoin.get(Company_.companyId);

		restriction = cb.and(restriction, cb.equal(companyID, companyId));

		if (StringUtils.isNotBlank(conditionDTO.getEmployeeNumber())) {

			restriction = cb.and(restriction,
					cb.like(empRoot.get(Employee_.employeeNumber), conditionDTO.getEmployeeNumber()));
		}

		if (StringUtils.isNotBlank(conditionDTO.getFirstName())) {

			restriction = cb.and(restriction,
					cb.like(cb.upper(empRoot.get(Employee_.firstName)), conditionDTO.getFirstName().toUpperCase()));
		}

		if (StringUtils.isNotBlank(conditionDTO.getLastName())) {

			restriction = cb.and(restriction,
					cb.like(cb.upper(empRoot.get(Employee_.lastName)), conditionDTO.getLastName().toUpperCase()));
		}

		if (StringUtils.isNotBlank(conditionDTO.getEmail())) {

			restriction = cb.and(restriction,
					cb.like(cb.upper(empRoot.get(Employee_.email)), conditionDTO.getEmail().toUpperCase()));
		}

		restriction = cb.and(restriction, cb.equal((empRoot.get(Employee_.status)), true));

		restriction = cb.and(restriction, cb.equal((loginDetailJoin.get(EmployeeLoginDetail_.passwordSent)), false));
		criteriaQuery.where(restriction);

		TypedQuery<Long> empTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		return empTypedQuery.getSingleResult();
	}

	@Override
	public Long getCountForWorkFlowEmployee(EmployeeConditionDTO conditionDTO, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
		Root<Employee> empRoot = criteriaQuery.from(Employee.class);
		criteriaQuery.select(cb.count(empRoot));

		Join<Employee, Company> empCompanyJoin = empRoot.join(Employee_.company);

		Predicate restriction = cb.conjunction();

		if (StringUtils.isNotBlank(conditionDTO.getEmployeeNumber())) {

			restriction = cb.and(restriction, cb.like(cb.upper(empRoot.get(Employee_.employeeNumber)),
					conditionDTO.getEmployeeNumber().toUpperCase()));
		}

		if (StringUtils.isNotBlank(conditionDTO.getEmployeeName())) {

			restriction = cb.and(restriction, cb.or(
					cb.like(cb.upper(empRoot.get(Employee_.firstName)), conditionDTO.getEmployeeName().toUpperCase()),
					cb.like(cb.upper(empRoot.get(Employee_.lastName)), conditionDTO.getEmployeeName().toUpperCase())));
		}
		restriction = cb.and(restriction, cb.equal(empCompanyJoin.get(Company_.companyId), companyId));
		criteriaQuery.where(restriction);

		TypedQuery<Long> empTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		return empTypedQuery.getSingleResult();
	}

	@Override
	public List<Employee> findEmployeesCalendarTemplate(AccessControlConditionDTO accessControlconditionDTO,
			PageRequest pageDTO, SortCondition sortDTO, Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Employee> criteriaQuery = cb.createQuery(Employee.class);
		Root<Employee> employeeRoot = criteriaQuery.from(Employee.class);

		criteriaQuery.select(employeeRoot);

		Join<Employee, Company> empCompanyJoin = employeeRoot.join(Employee_.company);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(empCompanyJoin.get(Company_.companyId), companyId));

		if (StringUtils.isNotBlank(accessControlconditionDTO.getEmployeeNumber())) {

			restriction = cb.and(restriction,
					cb.like(employeeRoot.get(Employee_.employeeNumber), accessControlconditionDTO.getEmployeeNumber()));
		}

		if (StringUtils.isNotBlank(accessControlconditionDTO.getFirstName())) {

			restriction = cb.and(restriction, cb.like(cb.upper(employeeRoot.get(Employee_.firstName)),
					accessControlconditionDTO.getFirstName().toUpperCase()));
		}

		if (StringUtils.isNotBlank(accessControlconditionDTO.getLastName())) {

			restriction = cb.and(restriction, cb.like(cb.upper(employeeRoot.get(Employee_.lastName)),
					accessControlconditionDTO.getLastName().toUpperCase()));
		}

		if (accessControlconditionDTO.getEmployeeShortListDTO().getEmployeeShortList()
				&& accessControlconditionDTO.getEmployeeShortListDTO().getShortListEmployeeIds().isEmpty()) {

			restriction = cb.and(restriction, employeeRoot.get("employeeId").in(-1));

		} else if (accessControlconditionDTO.getEmployeeShortListDTO().getEmployeeShortList()
				&& !accessControlconditionDTO.getEmployeeShortListDTO().getShortListEmployeeIds().isEmpty()) {
			restriction = cb.and(restriction, employeeRoot.get("employeeId")
					.in(accessControlconditionDTO.getEmployeeShortListDTO().getShortListEmployeeIds()));

		}

		criteriaQuery.where(restriction);

		if (sortDTO != null) {

			Path<String> sortPath = getSortPathForEmp(sortDTO, employeeRoot);
			if (sortPath != null) {
				if (SortConstants.DB_ORDER_BY_ASC.equalsIgnoreCase(sortDTO.getOrderType())) {
					criteriaQuery.orderBy(cb.asc(sortPath));
				}
				if (SortConstants.DB_ORDER_BY_DESC.equalsIgnoreCase(sortDTO.getOrderType())) {
					criteriaQuery.orderBy(cb.desc(sortPath));
				}
			}

		}

		TypedQuery<Employee> employeeTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		return employeeTypedQuery.getResultList();

	}

	@Override
	public List<Employee> findUnAssignEmployeesCalendarTemplate(AccessControlConditionDTO accessControlconditionDTO,
			PageRequest pageDTO, SortCondition sortDTO, Long companyId, Long calendartemplateId, String startDate,
			String dateFormat) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Employee> criteriaQuery = cb.createQuery(Employee.class);
		Root<Employee> employeeRoot = criteriaQuery.from(Employee.class);

		criteriaQuery.select(employeeRoot);

		Join<Employee, Company> empCompanyJoin = employeeRoot.join(Employee_.company);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(empCompanyJoin.get(Company_.companyId), companyId));

		if (StringUtils.isNotBlank(accessControlconditionDTO.getEmployeeNumber())) {

			restriction = cb.and(restriction,
					cb.like(employeeRoot.get(Employee_.employeeNumber), accessControlconditionDTO.getEmployeeNumber()));
		}

		if (StringUtils.isNotBlank(accessControlconditionDTO.getFirstName())) {

			restriction = cb.and(restriction, cb.like(cb.upper(employeeRoot.get(Employee_.firstName)),
					accessControlconditionDTO.getFirstName().toUpperCase()));
		}

		if (StringUtils.isNotBlank(accessControlconditionDTO.getLastName())) {

			restriction = cb.and(restriction, cb.like(cb.upper(employeeRoot.get(Employee_.lastName)),
					accessControlconditionDTO.getLastName().toUpperCase()));
		}

		if (accessControlconditionDTO.getEmployeeShortListDTO().getEmployeeShortList()
				&& accessControlconditionDTO.getEmployeeShortListDTO().getShortListEmployeeIds().isEmpty()) {

			restriction = cb.and(restriction, employeeRoot.get("employeeId").in(-1));

		} else if (accessControlconditionDTO.getEmployeeShortListDTO().getEmployeeShortList()
				&& !accessControlconditionDTO.getEmployeeShortListDTO().getShortListEmployeeIds().isEmpty()) {
			restriction = cb.and(restriction, employeeRoot.get("employeeId")
					.in(accessControlconditionDTO.getEmployeeShortListDTO().getShortListEmployeeIds()));

		}

		Subquery<Long> subquery = criteriaQuery.subquery(Long.class);
		Root<EmployeeCalendarConfig> employeeCalendarRoot = subquery.from(EmployeeCalendarConfig.class);
		subquery.select(employeeCalendarRoot.get(EmployeeCalendarConfig_.employee).get("employeeId").as(Long.class))
				.distinct(true);

		Predicate subRestriction = cb.conjunction();

		Calendar dateForNull = new GregorianCalendar(PayAsiaConstants.LAST_YEAR, PayAsiaConstants.LAST_MONTH,
				PayAsiaConstants.LAST_DAY);
		Timestamp timeStampForNull = new Timestamp(dateForNull.getTimeInMillis());

		subRestriction = cb.and(subRestriction,
				cb.lessThanOrEqualTo(employeeCalendarRoot.get(EmployeeCalendarConfig_.startDate),
						DateUtils.stringToTimestamp(startDate, dateFormat)),
				cb.greaterThanOrEqualTo(
						cb.coalesce(employeeCalendarRoot.get(EmployeeCalendarConfig_.endDate), timeStampForNull),
						DateUtils.stringToTimestamp(startDate, dateFormat)));

		subquery.where(subRestriction);

		restriction = cb.and(restriction, cb.not(cb.in(employeeRoot.get(Employee_.employeeId)).value(subquery)));

		criteriaQuery.where(restriction);

		if (sortDTO != null) {

			Path<String> sortPath = getSortPathForEmp(sortDTO, employeeRoot);
			if (sortPath != null) {
				if (SortConstants.DB_ORDER_BY_ASC.equalsIgnoreCase(sortDTO.getOrderType())) {
					criteriaQuery.orderBy(cb.asc(sortPath));
				}
				if (SortConstants.DB_ORDER_BY_DESC.equalsIgnoreCase(sortDTO.getOrderType())) {
					criteriaQuery.orderBy(cb.desc(sortPath));
				}
			}

		}

		TypedQuery<Employee> employeeTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		return employeeTypedQuery.getResultList();

	}

	public Path<String> getSortPathForEmp(SortCondition sortDTO, Root<Employee> employeeRoot) {

		List<String> empColList = new ArrayList<String>();
		empColList.add(SortConstants.EMPLOYEE_FIRST_NAME);
		empColList.add(SortConstants.EMPLOYEE_LAST_NAME);
		empColList.add(SortConstants.EMPLOYEE_NUMBER);

		Path<String> sortPath = null;

		if (empColList.contains(sortDTO.getColumnName())) {
			sortPath = employeeRoot.get(colMap.get(Employee.class + sortDTO.getColumnName()));
		}
		return sortPath;
	}

	@Override
	public List<Employee> findGranterEmployees(Long companyId, Long leaveSchemeId, Long leaveTypeId,
			PageRequest pageDTO, SortCondition sortDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Employee> criteriaQuery = cb.createQuery(Employee.class);
		Root<Employee> empRoot = criteriaQuery.from(Employee.class);
		criteriaQuery.select(empRoot);

		Predicate restriction = cb.conjunction();

		Join<Employee, Company> empCompanyJoin = empRoot.join(Employee_.company);
		Join<Employee, EmployeeLeaveScheme> empLeaveSchemeJoin = empRoot.join(Employee_.employeeLeaveSchemes);
		Join<EmployeeLeaveScheme, LeaveScheme> leaveSchemeJoin = empLeaveSchemeJoin
				.join(EmployeeLeaveScheme_.leaveScheme);
		Join<EmployeeLeaveScheme, EmployeeLeaveSchemeType> empLeaveSchemeTypeJoin = empLeaveSchemeJoin
				.join(EmployeeLeaveScheme_.employeeLeaveSchemeTypes);
		Join<EmployeeLeaveSchemeType, LeaveSchemeType> leaveSchemeTypeJoin = empLeaveSchemeTypeJoin
				.join(EmployeeLeaveSchemeType_.leaveSchemeType);
		Join<LeaveSchemeType, LeaveTypeMaster> leaveTypeJoin = leaveSchemeTypeJoin
				.join(LeaveSchemeType_.leaveTypeMaster);

		Path<Long> companyID = empCompanyJoin.get(Company_.companyId);

		restriction = cb.and(restriction, cb.equal(companyID, companyId));

		restriction = cb.and(restriction, cb.equal(empRoot.get(Employee_.status), true));

		if (leaveSchemeId != 0) {

			restriction = cb.and(restriction, cb.equal(leaveSchemeJoin.get(LeaveScheme_.leaveSchemeId), leaveSchemeId));
		}

		if (leaveTypeId != 0) {

			restriction = cb.and(restriction, cb.equal(leaveTypeJoin.get(LeaveTypeMaster_.leaveTypeId), leaveTypeId));
		}

		criteriaQuery.where(restriction);

		if (sortDTO != null) {

			Path<String> sortPath = getSortPathForSearchEmployee(sortDTO, empRoot);
			if (sortPath != null) {
				if (SortConstants.DB_ORDER_BY_ASC.equalsIgnoreCase(sortDTO.getOrderType())) {
					criteriaQuery.orderBy(cb.asc(sortPath));
				}
				if (SortConstants.DB_ORDER_BY_DESC.equalsIgnoreCase(sortDTO.getOrderType())) {
					criteriaQuery.orderBy(cb.desc(sortPath));
				}
			}

		}

		TypedQuery<Employee> empTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			empTypedQuery.setFirstResult(getStartPosition(pageDTO));
			empTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		return empTypedQuery.getResultList();
	}

	@Override
	public Integer getCountForGranterEmployees(Long companyId, Long leaveSchemeId, Long leaveTypeId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Integer> criteriaQuery = cb.createQuery(Integer.class);
		Root<Employee> empRoot = criteriaQuery.from(Employee.class);
		criteriaQuery.select(cb.count(empRoot).as(Integer.class));

		Predicate restriction = cb.conjunction();

		Join<Employee, Company> empCompanyJoin = empRoot.join(Employee_.company);
		Join<Employee, EmployeeLeaveScheme> empLeaveSchemeJoin = empRoot.join(Employee_.employeeLeaveSchemes);
		Join<EmployeeLeaveScheme, LeaveScheme> leaveSchemeJoin = empLeaveSchemeJoin
				.join(EmployeeLeaveScheme_.leaveScheme);
		Join<LeaveScheme, LeaveSchemeType> leaveSchemeTypeJoin = leaveSchemeJoin.join(LeaveScheme_.leaveSchemeTypes);
		Join<LeaveSchemeType, LeaveTypeMaster> leaveTypeJoin = leaveSchemeTypeJoin
				.join(LeaveSchemeType_.leaveTypeMaster);

		Path<Long> companyID = empCompanyJoin.get(Company_.companyId);

		restriction = cb.and(restriction, cb.equal(companyID, companyId));

		restriction = cb.and(restriction, cb.equal(empRoot.get(Employee_.status), true));

		if (leaveSchemeId != 0) {

			restriction = cb.and(restriction, cb.equal(leaveSchemeJoin.get(LeaveScheme_.leaveSchemeId), leaveSchemeId));
		}

		if (leaveTypeId != 0) {

			restriction = cb.and(restriction, cb.equal(leaveTypeJoin.get(LeaveTypeMaster_.leaveTypeId), leaveTypeId));
		}

		criteriaQuery.where(restriction);

		TypedQuery<Integer> empTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		return empTypedQuery.getSingleResult();
	}

	@Override
	public List<Employee> findAnnualRollbackEmps(Long companyId, String fromDate, String toDate, PageRequest pageDTO,
			SortCondition sortDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Employee> criteriaQuery = cb.createQuery(Employee.class);
		Root<Employee> empRoot = criteriaQuery.from(Employee.class);
		criteriaQuery.select(empRoot);

		Predicate restriction = cb.conjunction();

		Join<Employee, Company> companyJoin = empRoot.join(Employee_.company);

		if (StringUtils.isNotBlank(fromDate) && StringUtils.isNotBlank(toDate)) {

			restriction = cb.and(restriction, cb.between(empRoot.get(Employee_.resignationDate).as(Date.class),
					DateUtils.stringToDate(fromDate), DateUtils.stringToDate(toDate)));
		}

		if (StringUtils.isNotBlank(fromDate) && StringUtils.isBlank(toDate)) {

			restriction = cb.and(restriction, cb.greaterThanOrEqualTo(
					empRoot.get(Employee_.resignationDate).as(Date.class), DateUtils.stringToDate(fromDate)));
		}
		if (StringUtils.isBlank(fromDate) && StringUtils.isBlank(toDate)) {
			restriction = cb.and(restriction, cb.isNotNull(empRoot.get(Employee_.resignationDate)));
		}

		restriction = cb.and(restriction, cb.equal(companyJoin.get(Company_.companyId), companyId));

		criteriaQuery.where(restriction);

		if (sortDTO != null) {

			Path<String> sortPath = getSortPathForSearchEmployee(sortDTO, empRoot);
			if (sortPath != null) {
				if (SortConstants.DB_ORDER_BY_ASC.equalsIgnoreCase(sortDTO.getOrderType())) {
					criteriaQuery.orderBy(cb.asc(sortPath));
				}
				if (SortConstants.DB_ORDER_BY_DESC.equalsIgnoreCase(sortDTO.getOrderType())) {
					criteriaQuery.orderBy(cb.desc(sortPath));
				}
			}

		}

		TypedQuery<Employee> empTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			empTypedQuery.setFirstResult(getStartPosition(pageDTO));
			empTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		return empTypedQuery.getResultList();
	}

	@Override
	public List<Employee> findResignedEmpForClaim(Long companyId, String fromDate, String toDate, PageRequest pageDTO,
			SortCondition sortDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Employee> criteriaQuery = cb.createQuery(Employee.class);
		Root<Employee> empRoot = criteriaQuery.from(Employee.class);
		criteriaQuery.select(empRoot).distinct(true);

		Predicate restriction = cb.conjunction();

		Join<Employee, Company> companyJoin = empRoot.join(Employee_.company);

		if (StringUtils.isNotBlank(fromDate) && StringUtils.isNotBlank(toDate)) {

			restriction = cb.and(restriction, cb.between(empRoot.get(Employee_.resignationDate).as(Date.class),
					DateUtils.stringToDate(fromDate), DateUtils.stringToDate(toDate)));
		}

		if (StringUtils.isNotBlank(fromDate) && StringUtils.isBlank(toDate)) {

			restriction = cb.and(restriction, cb.greaterThanOrEqualTo(
					empRoot.get(Employee_.resignationDate).as(Date.class), DateUtils.stringToDate(fromDate)));
		}
		if (StringUtils.isBlank(fromDate) && StringUtils.isBlank(toDate)) {
			restriction = cb.and(restriction, cb.isNotNull(empRoot.get(Employee_.resignationDate)));
		}

		restriction = cb.and(restriction, cb.equal(companyJoin.get(Company_.companyId), companyId));

		Join<Employee, EmployeeClaimTemplate> claimJoin = empRoot.join(Employee_.employeeClaimTemplates);
		Join<EmployeeClaimTemplate, EmployeeClaimAdjustment> adjustmentJoin = claimJoin
				.join(EmployeeClaimTemplate_.employeeClaimAdjustments, JoinType.LEFT);

		restriction = cb.and(restriction,
				cb.or(cb.equal(adjustmentJoin.get(EmployeeClaimAdjustment_.resignationRollback), 0),
						cb.isNull(adjustmentJoin.get(EmployeeClaimAdjustment_.resignationRollback))));
		restriction = cb.and(restriction, cb.equal(empRoot.get(Employee_.status), 1));

		criteriaQuery.where(restriction);

		if (sortDTO != null) {

			Path<String> sortPath = getSortPathForSearchEmployee(sortDTO, empRoot);
			if (sortPath != null) {
				if (SortConstants.DB_ORDER_BY_ASC.equalsIgnoreCase(sortDTO.getOrderType())) {
					criteriaQuery.orderBy(cb.asc(sortPath));
				}
				if (SortConstants.DB_ORDER_BY_DESC.equalsIgnoreCase(sortDTO.getOrderType())) {
					criteriaQuery.orderBy(cb.desc(sortPath));
				}
			}

		}

		TypedQuery<Employee> empTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			empTypedQuery.setFirstResult(getStartPosition(pageDTO));
			empTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		return empTypedQuery.getResultList();
	}

	@Override
	public Integer getCountAnnualRollbackEmps(Long companyId, String fromDate, String toDate) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Integer> criteriaQuery = cb.createQuery(Integer.class);
		Root<Employee> empRoot = criteriaQuery.from(Employee.class);
		criteriaQuery.select(cb.count(empRoot).as(Integer.class));

		Predicate restriction = cb.conjunction();

		Join<Employee, Company> companyJoin = empRoot.join(Employee_.company);

		if (StringUtils.isNotBlank(fromDate) && StringUtils.isNotBlank(toDate)) {

			restriction = cb.and(restriction, cb.between(empRoot.get(Employee_.resignationDate).as(Date.class),
					DateUtils.stringToDate(fromDate), DateUtils.stringToDate(toDate)));
		}

		if (StringUtils.isNotBlank(fromDate) && StringUtils.isBlank(toDate)) {

			restriction = cb.and(restriction, cb.greaterThanOrEqualTo(
					empRoot.get(Employee_.resignationDate).as(Date.class), DateUtils.stringToDate(fromDate)));
		}
		if (StringUtils.isBlank(fromDate) && StringUtils.isBlank(toDate)) {
			restriction = cb.and(restriction, cb.isNotNull(empRoot.get(Employee_.resignationDate)));
		}
		restriction = cb.and(restriction, cb.equal(companyJoin.get(Company_.companyId), companyId));

		criteriaQuery.where(restriction);

		TypedQuery<Integer> empTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		return empTypedQuery.getSingleResult();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.EmployeeDAO#getCountForCondition(com.payasia.common.dto
	 * .EmployeeConditionDTO, java.lang.Long)
	 */
	@Override
	public int getGroupCompanyEmployeeCount(EmployeeConditionDTO conditionDTO, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Integer> criteriaQuery = cb.createQuery(Integer.class);
		Root<Employee> empRoot = criteriaQuery.from(Employee.class);
		criteriaQuery.select(cb.count(empRoot).as(Integer.class));

		Join<Employee, Company> empCompanyJoin = empRoot.join(Employee_.company);

		Join<Company, CompanyGroup> groupJoin = empCompanyJoin.join(Company_.companyGroup);

		Predicate restriction = cb.conjunction();

		if (StringUtils.isNotBlank(conditionDTO.getEmployeeNumber())) {

			restriction = cb.and(restriction, cb.like(cb.upper(empRoot.get(Employee_.employeeNumber)),
					conditionDTO.getEmployeeNumber().toUpperCase() + '%'));
		}

		if (StringUtils.isNotBlank(conditionDTO.getFirstName())) {

			restriction = cb.and(restriction, cb.like(cb.upper(empRoot.get(Employee_.firstName)),
					conditionDTO.getFirstName().toUpperCase() + '%'));
		}
		if (StringUtils.isNotBlank(conditionDTO.getLastName())) {

			restriction = cb.and(restriction,
					cb.like(cb.upper(empRoot.get(Employee_.lastName)), conditionDTO.getLastName().toUpperCase() + '%'));
		}

		restriction = cb.and(restriction, cb.equal(empRoot.get(Employee_.status), true));

		restriction = cb.and(restriction, cb.equal(groupJoin.get(CompanyGroup_.groupId), conditionDTO.getGroupId()));
		criteriaQuery.where(restriction);

		TypedQuery<Integer> empTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		return empTypedQuery.getSingleResult();
	}
	
	@Override
	public List<Employee> getGroupCompanyEmployee(Long groupId) {
		
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Employee> criteriaQuery = cb.createQuery(Employee.class);
		Root<Employee> empRoot = criteriaQuery.from(Employee.class);
		
		Join<Employee, Company> empCompanyJoin = empRoot.join(Employee_.company);

		Join<Company, CompanyGroup> groupJoin = empCompanyJoin.join(Company_.companyGroup);

		Predicate restriction = cb.conjunction();
		
		restriction = cb.and(restriction, cb.equal(groupJoin.get(CompanyGroup_.groupId), groupId));

		restriction = cb.and(restriction, cb.equal(empRoot.get(Employee_.status), true));

		criteriaQuery.where(restriction);

		TypedQuery<Employee> empTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<Employee> employeeList = empTypedQuery.getResultList();

		return employeeList;
	}

	@Override
	public Integer getCountForPrivilegeUser(long companyId, Long employeeId,
			EmployeeShortListDTO employeeShortListDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Integer> criteriaQuery = cb.createQuery(Integer.class);
		Root<Employee> empRoot = criteriaQuery.from(Employee.class);

		criteriaQuery.select(cb.count(empRoot).as(Integer.class));

		Join<Employee, Company> empCompanyJoin = empRoot.join(Employee_.company);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(empCompanyJoin.get(Company_.companyId), companyId));
		if (employeeId != null) {
			restriction = cb.and(restriction, cb.notEqual(empRoot.get(Employee_.employeeId), employeeId));
		}

		if (employeeShortListDTO.getEmployeeShortList() && employeeShortListDTO.getShortListEmployeeIds().size() == 0) {

			restriction = cb.and(restriction, empRoot.get(Employee_.employeeId).in(-1));

		} else if (employeeShortListDTO.getEmployeeShortList()
				&& employeeShortListDTO.getShortListEmployeeIds().size() > 0) {
			restriction = cb.and(restriction,
					empRoot.get(Employee_.employeeId).in(employeeShortListDTO.getShortListEmployeeIds()));

		}
		restriction = cb.and(restriction, cb.equal((empRoot.get(Employee_.status)), true));
		criteriaQuery.where(restriction);

		TypedQuery<Integer> empTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		return empTypedQuery.getSingleResult();
	}

	@Override
	public List<Tuple> getEmployeeNameTupleList(Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Tuple> criteriaQuery = cb.createQuery(Tuple.class);
		Root<Employee> empeRoot = criteriaQuery.from(Employee.class);

		Join<Employee, Company> compJoin = empeRoot.join(Employee_.company);

		List<Selection<?>> selectionItems = new ArrayList<Selection<?>>();

		selectionItems.add(empeRoot.get(Employee_.employeeNumber).alias(getAlias(Employee_.employeeNumber)));
		selectionItems.add(empeRoot.get(Employee_.employeeId).alias(getAlias(Employee_.employeeId)));
		criteriaQuery.multiselect(selectionItems).distinct(true);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(compJoin.get(Company_.companyId), companyId));

		criteriaQuery.where(restriction);

		TypedQuery<Tuple> companyTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<Tuple> companyGroupList = companyTypedQuery.getResultList();
		return companyGroupList;
	}

	@Override
	public List<Employee> getEmployeeListByAdvanceFilter(EmployeeConditionDTO conditionDTO, PageRequest pageDTO,
			SortCondition sortDTO, Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Employee> criteriaQuery = cb.createQuery(Employee.class);
		Root<Employee> empRoot = criteriaQuery.from(Employee.class);
		criteriaQuery.select(empRoot);

		Join<Employee, Company> empCompanyJoin = empRoot.join(Employee_.company);

		Predicate restriction = cb.conjunction();

		if (StringUtils.isNotBlank(conditionDTO.getEmployeeNumber())) {

			restriction = cb.and(restriction, cb.like(cb.upper(empRoot.get(Employee_.employeeNumber)),
					conditionDTO.getEmployeeNumber().toUpperCase()));
		}

		if (StringUtils.isNotBlank(conditionDTO.getEmployeeName())) {

			restriction = cb.and(restriction, cb.or(
					cb.like(cb.upper(empRoot.get(Employee_.firstName)), conditionDTO.getEmployeeName().toUpperCase()),
					cb.like(cb.upper(empRoot.get(Employee_.lastName)), conditionDTO.getEmployeeName().toUpperCase())));
		}
		if (conditionDTO.getEmployeeShortList() && conditionDTO.getShortListEmployeeIds().isEmpty()) {

			restriction = cb.and(restriction, empRoot.get(Employee_.employeeId).in(-1));

		} else if (conditionDTO.getEmployeeShortList() && !conditionDTO.getShortListEmployeeIds().isEmpty()) {
			restriction = cb.and(restriction,
					empRoot.get(Employee_.employeeId).in(conditionDTO.getShortListEmployeeIds()));

		}
		restriction = cb.and(restriction, cb.equal(empRoot.get(Employee_.status), true));

		restriction = cb.and(restriction, cb.equal(empCompanyJoin.get(Company_.companyId), companyId));
		criteriaQuery.where(restriction);

		TypedQuery<Employee> empTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<Employee> employeeList = empTypedQuery.getResultList();

		return employeeList;
	}

	@Override
	public Integer getEmployeeListByAdvanceFilterCount(EmployeeConditionDTO conditionDTO, Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Integer> criteriaQuery = cb.createQuery(Integer.class);
		Root<Employee> empRoot = criteriaQuery.from(Employee.class);
		criteriaQuery.select(cb.count(empRoot).as(Integer.class));

		Join<Employee, Company> empCompanyJoin = empRoot.join(Employee_.company);

		Predicate restriction = cb.conjunction();

		if (StringUtils.isNotBlank(conditionDTO.getEmployeeNumber())) {

			restriction = cb.and(restriction, cb.like(cb.upper(empRoot.get(Employee_.employeeNumber)),
					conditionDTO.getEmployeeNumber().toUpperCase()));
		}

		if (StringUtils.isNotBlank(conditionDTO.getEmployeeName())) {

			restriction = cb.and(restriction, cb.or(
					cb.like(cb.upper(empRoot.get(Employee_.firstName)), conditionDTO.getEmployeeName().toUpperCase()),
					cb.like(cb.upper(empRoot.get(Employee_.lastName)), conditionDTO.getEmployeeName().toUpperCase())));
		}
		if (conditionDTO.getEmployeeShortList() && conditionDTO.getShortListEmployeeIds().isEmpty()) {

			restriction = cb.and(restriction, empRoot.get(Employee_.employeeId).in(-1));

		} else if (conditionDTO.getEmployeeShortList() && !conditionDTO.getShortListEmployeeIds().isEmpty()) {
			restriction = cb.and(restriction,
					empRoot.get(Employee_.employeeId).in(conditionDTO.getShortListEmployeeIds()));

		}

		restriction = cb.and(restriction, cb.equal(empCompanyJoin.get(Company_.companyId), companyId));
		criteriaQuery.where(restriction);

		TypedQuery<Integer> empTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		Integer employeeCount = empTypedQuery.getSingleResult();

		return employeeCount;
	}

	@Override
	public void updateEmploymentStatus(Long companyId, java.util.Date dateCtr) {

		String queryString = "UPDATE Employee EMP SET EMP.employmentStatus = :employmentStatus where cast(EMP.confirmationDate as date) = cast(:currentDate as date) and EMP.company.companyId = :companyId";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("employmentStatus", "Confirmed");
		q.setParameter("companyId", companyId);
		q.setParameter("currentDate", DateUtils.convertDateToTimeStamp(dateCtr));
		q.executeUpdate();

	}

	@Override
	public void updateEmployeeStatus(Long companyId, java.util.Date dateCtr) {

		String queryString = "UPDATE Employee EMP SET EMP.status = :status where DATEADD(DAY, 1, cast(EMP.resignationDate as date))  <= cast(:currentDate as date) and EMP.company.companyId = :companyId and EMP.status <> 0";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("status", false);
		q.setParameter("companyId", companyId);
		q.setParameter("currentDate", DateUtils.convertDateToTimeStamp(dateCtr));
		q.executeUpdate();

	}

	private String getEqualityOperator(String equalityOperator) {

		switch (equalityOperator) {
		case PayAsiaConstants.IS_EQUAL_TO_SYSDATE_BY:
			equalityOperator = "=";

			break;

		case PayAsiaConstants.IS_GREATER_THAN_EQUAL_TO_SYSDATE_BY:

			equalityOperator = "<=";
			break;

		case PayAsiaConstants.IS_GREATER_THAN_SYSDATE_BY:
			equalityOperator = "<";

			break;

		case PayAsiaConstants.IS_LESS_THAN_EQUAL_TO_SYSDATE_BY:
			equalityOperator = ">=";
			break;

		case PayAsiaConstants.IS_LESS_THAN_SYSDATE_BY:
			equalityOperator = ">";
			break;

		case PayAsiaConstants.IS_NOT_EQUAL_TO_SYSDATE_BY:
			equalityOperator = "!=";
			break;

		}

		return equalityOperator;

	}

	private String getEqualityOperatorValue(String equalityOperator, String equalityOperatorValue, Long companyId,
			ColumnPropertyDTO columnPropertyDTO, ExcelExportFiltersForm excelExportFiltersForm) {

		Company companyVO = companyDAO.findById(companyId);

		if (equalityOperator.equals(PayAsiaConstants.IS_EQUAL_TO_SYSDATE_BY)
				|| equalityOperator.equals(PayAsiaConstants.IS_GREATER_THAN_EQUAL_TO_SYSDATE_BY)
				|| equalityOperator.equals(PayAsiaConstants.IS_GREATER_THAN_SYSDATE_BY)
				|| equalityOperator.equals(PayAsiaConstants.IS_LESS_THAN_EQUAL_TO_SYSDATE_BY)
				|| equalityOperator.equals(PayAsiaConstants.IS_LESS_THAN_SYSDATE_BY)
				|| equalityOperator.equals(PayAsiaConstants.IS_NOT_EQUAL_TO_SYSDATE_BY)) {

			Calendar calCurrentDate = Calendar.getInstance();
			java.util.Date date = DateUtils.getDateAccToTimeZone(calCurrentDate.getTime(),
					companyVO.getTimeZoneMaster().getGmtOffset());
			SimpleDateFormat sdfDestination = new SimpleDateFormat("yyyy-MM-dd");
			return sdfDestination.format(date);

		} else {

			if (excelExportFiltersForm.getDataImportKeyValueDTO().isStatic()) {

				if ("bit".equals(columnPropertyDTO.getColumnType())) {

					if (excelExportFiltersForm.getValue().equalsIgnoreCase(PayAsiaConstants.SHORTLIST_ENABLE)) {
						equalityOperatorValue = "1";

					} else if (excelExportFiltersForm.getValue().equalsIgnoreCase(PayAsiaConstants.SHORTLIST_DISABLE)) {
						equalityOperatorValue = "0";

					} else {
						equalityOperatorValue = "-1";

					}

				}
			}

			return equalityOperatorValue;
		}

	}

	@Override
	public List<BigInteger> findApplicableEmployeeIds(String queryString, Long companyId,
			Map<String, String> paramValueMap) {
		Query q = entityManagerFactory.createNativeQuery(queryString);
		q.setParameter("companyId", companyId);
		for (Map.Entry<String, String> entry : paramValueMap.entrySet()) {
			if (entry.getKey().startsWith("sysdateParam")) {
				q.setParameter(entry.getKey(), Integer.parseInt(entry.getValue()));
			} else {
				q.setParameter(entry.getKey(), entry.getValue());
			}

		}

		return q.getResultList();
	}

	@Override
	public List<String> findShortListedEmployeeIds(String queryString, Map<String, String> paramValueMap,
			Long companyId) {
		Query q = entityManagerFactory.createNativeQuery(queryString);
		q.setParameter("companyId", companyId);
		for (Map.Entry<String, String> entry : paramValueMap.entrySet()) {
			if (entry.getKey().startsWith("sysdateParam")) {
				q.setParameter(entry.getKey(), Integer.parseInt(entry.getValue()));
			} else {
				q.setParameter(entry.getKey(), entry.getValue());
			}
		}

		return q.getResultList();
	}

	@Override
	public List<Object[]> createQueryForCustomFieldReport(Map<String, DataImportKeyValueDTO> colMap, List<Long> formIds,
			Long companyId, String dateFormat, Map<String, DataImportKeyValueDTO> tableRecordInfo,
			List<Long> employeeIdsList, boolean showOnlyEmployeeDynFieldCode) {
		Map<String, List<Long>> paramValueMap = new HashMap<String, List<Long>>();
		List<String> tableJoinsList = new ArrayList<>();
		ExcelExportQueryDTO selectDTO = createCustomFieldSelect(colMap, tableRecordInfo, showOnlyEmployeeDynFieldCode);
		String from = createCustomFieldFrom(colMap, formIds, selectDTO, tableRecordInfo, tableJoinsList);
		String where = createCustomFieldWhere(companyId, tableJoinsList, employeeIdsList, paramValueMap);
		String queryString = selectDTO.getSelectQuery() + from + where;
		queryString = queryString.replace("?", "");
		queryString = queryString.replace("%", "");

		Query q = entityManagerFactory.createNativeQuery(queryString);
		q.setParameter("companyId", companyId);
		for (Map.Entry<String, List<Long>> entry : paramValueMap.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}

		return q.getResultList();
	}

	public ExcelExportQueryDTO createCustomFieldSelect(Map<String, DataImportKeyValueDTO> colMap,
			Map<String, DataImportKeyValueDTO> tableRecordInfo, boolean showOnlyEmployeeDynFieldCode) {

		ExcelExportQueryDTO excelExportQueryDTO = new ExcelExportQueryDTO();
		List<CodeDescDTO> codeDescDTOs = new ArrayList<CodeDescDTO>();

		String selectQuery = "SELECT ";
		String tablePosition = null;
		Set<String> xlKeySet = colMap.keySet();

		int count = 1;

		selectQuery = selectQuery + "employee.Employee_ID AS employeeId ,";

		for (Iterator<String> itr = xlKeySet.iterator(); itr.hasNext();) {
			if (count < xlKeySet.size()) {
				String key = (String) itr.next();
				DataImportKeyValueDTO valueDTO = (DataImportKeyValueDTO) colMap.get(key);
				String fieldName = valueDTO.getMethodName();
				key = key.replaceAll("\\.", "_");
				String codeKey = key;
				key = "[" + key + "]";
				if (valueDTO.isStatic()) {
					String methodName = valueDTO.getActualColName();

					selectQuery = selectQuery + "employee." + methodName + " AS " + key + ", ";
				} else {

					String methodName = "Col_" + fieldName;

					if (!valueDTO.isChild()) {

						if (valueDTO.getFieldType().equalsIgnoreCase("codedesc")) {

							CodeDescDTO codeDescDTO = new CodeDescDTO();
							codeDescDTO.setMethodName(methodName);
							codeDescDTO.setFormId(valueDTO.getFormId());
							codeDescDTO.setChildVal(false);
							codeDescDTOs.add(codeDescDTO);

							if (showOnlyEmployeeDynFieldCode) {
								selectQuery = selectQuery + " dynamicFormFieldRefValue" + valueDTO.getFormId()
										+ methodName + ".Code  AS [" + codeKey + "_CodeDescription] " + ", ";
							} else {
								selectQuery = selectQuery + " concat(dynamicFormFieldRefValue" + valueDTO.getFormId()
										+ methodName + ".Code ,'-', dynamicFormFieldRefValue" + valueDTO.getFormId()
										+ methodName + ".Description) AS [" + codeKey + "_CodeDescription] " + ", ";
							}

						} else {
							selectQuery = selectQuery + " dynamicFormRecord" + valueDTO.getFormId() + "." + methodName
									+ " AS " + key + ", ";
						}

					} else {

						if (valueDTO.getFieldType().equalsIgnoreCase("codedesc")) {
							CodeDescDTO codeDescDTO = new CodeDescDTO();
							codeDescDTO.setMethodName(methodName);
							codeDescDTO.setFormId(valueDTO.getFormId());
							codeDescDTO.setChildVal(true);
							codeDescDTO.setTablePosition(valueDTO.getTablePosition());
							codeDescDTOs.add(codeDescDTO);

							if (showOnlyEmployeeDynFieldCode) {
								selectQuery = selectQuery + " dynamicFormFieldRefValueT" + valueDTO.getFormId()
										+ valueDTO.getTablePosition() + methodName + ".Code  AS [" + codeKey
										+ "_CodeDescription] " + ", ";
							} else {
								selectQuery = selectQuery + " concat(dynamicFormFieldRefValueT" + valueDTO.getFormId()
										+ valueDTO.getTablePosition() + methodName
										+ ".Code ,'-', dynamicFormFieldRefValueT" + valueDTO.getFormId()
										+ valueDTO.getTablePosition() + methodName + ".Description) AS [" + codeKey
										+ "_CodeDescription] " + ", ";
							}

						} else {

							selectQuery = selectQuery + " dynamicFormTableRecord_" + valueDTO.getFormId()
									+ valueDTO.getTablePosition() + "." + methodName + " AS " + key + ", ";

						}

						tablePosition = valueDTO.getTablePosition();
						excelExportQueryDTO.setTablePosition(tablePosition);
						excelExportQueryDTO.setFormId(valueDTO.getFormId());

					}
				}
			} else {
				String key = (String) itr.next();
				DataImportKeyValueDTO valueDTO = (DataImportKeyValueDTO) colMap.get(key);
				String fieldName = valueDTO.getMethodName();
				key = key.replaceAll("\\.", "_");
				String codeKey = key;
				key = "[" + key + "]";
				if (valueDTO.isStatic()) {
					String methodName = valueDTO.getActualColName();
					;

					selectQuery = selectQuery + "employee." + methodName + " AS " + key + " ";
				} else {

					String methodName = "Col_" + fieldName;

					if (!valueDTO.isChild()) {

						if (valueDTO.getFieldType().equalsIgnoreCase("codedesc")) {

							CodeDescDTO codeDescDTO = new CodeDescDTO();
							codeDescDTO.setMethodName(methodName);
							codeDescDTO.setFormId(valueDTO.getFormId());
							codeDescDTO.setChildVal(false);
							codeDescDTOs.add(codeDescDTO);

							if (showOnlyEmployeeDynFieldCode) {
								selectQuery = selectQuery + " dynamicFormFieldRefValue" + valueDTO.getFormId()
										+ methodName + ".Code  AS [" + codeKey + "_CodeDescription] ";
							} else {
								selectQuery = selectQuery + " concat(dynamicFormFieldRefValue" + valueDTO.getFormId()
										+ methodName + ".Code ,'-', dynamicFormFieldRefValue" + valueDTO.getFormId()
										+ methodName + ".Description) AS [" + codeKey + "_CodeDescription] ";
							}

						} else {
							selectQuery = selectQuery + " dynamicFormRecord" + valueDTO.getFormId() + "." + methodName
									+ " AS " + key + " ";
						}

					} else {

						if (valueDTO.getFieldType().equalsIgnoreCase("codedesc")) {
							CodeDescDTO codeDescDTO = new CodeDescDTO();
							codeDescDTO.setMethodName(methodName);
							codeDescDTO.setFormId(valueDTO.getFormId());
							codeDescDTO.setChildVal(true);
							codeDescDTO.setTablePosition(valueDTO.getTablePosition());
							codeDescDTOs.add(codeDescDTO);

							if (showOnlyEmployeeDynFieldCode) {
								selectQuery = selectQuery + " dynamicFormFieldRefValueT" + valueDTO.getFormId()
										+ valueDTO.getTablePosition() + methodName + ".Code AS [" + codeKey
										+ "_CodeDescription] ";
							} else {
								selectQuery = selectQuery + " concat(dynamicFormFieldRefValueT" + valueDTO.getFormId()
										+ valueDTO.getTablePosition() + methodName
										+ ".Code ,'-', dynamicFormFieldRefValueT" + valueDTO.getFormId()
										+ valueDTO.getTablePosition() + methodName + ".Description) AS [" + codeKey
										+ "_CodeDescription] ";
							}

						} else {

							selectQuery = selectQuery + " dynamicFormTableRecord_" + valueDTO.getFormId()
									+ valueDTO.getTablePosition() + "." + methodName + " AS " + key + " ";

						}

						tablePosition = valueDTO.getTablePosition();
						excelExportQueryDTO.setTablePosition(tablePosition);
						excelExportQueryDTO.setFormId(valueDTO.getFormId());

					}
				}
			}

			count++;
		}
		excelExportQueryDTO.setCodeDescList(codeDescDTOs);
		excelExportQueryDTO.setSelectQuery(selectQuery);

		return excelExportQueryDTO;
	}

	public String createCustomFieldFrom(Map<String, DataImportKeyValueDTO> colMap, List<Long> formIds,
			ExcelExportQueryDTO queryDTO, Map<String, DataImportKeyValueDTO> tableRecordInfo,
			List<String> tableJoinsList) {
		String tableDateStr = "";

		String from = "FROM Employee AS employee ";
		tableJoinsList.add("employee");
		for (Long formId : formIds) {

			from = from + "LEFT OUTER JOIN Dynamic_Form_Record AS dynamicFormRecord" + formId + " ";
			from = from + " ON (employee.Employee_ID = dynamicFormRecord" + formId
					+ ".Entity_Key) AND (dynamicFormRecord" + formId + ".Form_ID = " + formId + " ) ";
			tableJoinsList.add("dynamicFormRecord" + formId);
		}

		Integer noOfTables = tableRecordInfo.size();
		if (noOfTables <= 1) {

			if (queryDTO.getTablePosition() != null) {
				from = from + "LEFT OUTER JOIN Dynamic_Form_Table_Record AS dynamicFormTableRecord_"
						+ queryDTO.getFormId() + queryDTO.getTablePosition();
				from = from + " ON (dynamicFormRecord" + queryDTO.getFormId() + ".Col_" + queryDTO.getTablePosition()
						+ " = dynamicFormTableRecord_" + queryDTO.getFormId() + queryDTO.getTablePosition()
						+ ".Dynamic_Form_Table_Record_ID ) ";
				tableJoinsList.add("dynamicFormTableRecord");
			}

		} else {

			for (Map.Entry<String, DataImportKeyValueDTO> entry : tableRecordInfo.entrySet()) {

				entry.getKey();
				entry.getValue();

				from = from + " LEFT OUTER JOIN Dynamic_Form_Table_Record AS dynamicFormTableRecord_" + entry.getKey();
				tableJoinsList.add("dynamicFormTableRecord_" + entry.getKey());

				tableDateStr = "getDate()";

				from = from + " ON dynamicFormRecord" + entry.getValue().getFormId() + ".Col_"
						+ entry.getValue().getTablePosition() + " = dynamicFormTableRecord_" + entry.getKey()
						+ ".Dynamic_Form_Table_Record_ID  " + "and CONVERT(datetime,dynamicFormTableRecord_"
						+ entry.getKey() + ".Col_1) = (select top 1 max(CONVERT(datetime, dynamicFormTableRecord_max"
						+ entry.getKey() + ".Col_1)) from Dynamic_Form_Table_Record dynamicFormTableRecord_max"
						+ entry.getKey() + " where dynamicFormTableRecord_max" + entry.getKey()
						+ ".Dynamic_Form_Table_Record_ID =dynamicFormTableRecord_" + entry.getKey()
						+ ".Dynamic_Form_Table_Record_ID  and CONVERT(date, dynamicFormTableRecord_max" + entry.getKey()
						+ ".Col_1) <= " + tableDateStr + ")";

			}

		}

		for (CodeDescDTO codeDescDTO : queryDTO.getCodeDescList()) {

			if (codeDescDTO.isChildVal()) {
				from = from + "LEFT OUTER JOIN Dynamic_Form_Field_Ref_Value AS dynamicFormFieldRefValueT"
						+ codeDescDTO.getFormId() + codeDescDTO.getTablePosition() + codeDescDTO.getMethodName() + " ";
				tableJoinsList.add("dynamicFormFieldRefValueT" + codeDescDTO.getFormId()
						+ codeDescDTO.getTablePosition() + codeDescDTO.getMethodName());
				if (noOfTables <= 1) {

					from = from + " ON (dynamicFormTableRecord_" + codeDescDTO.getFormId()
							+ codeDescDTO.getTablePosition() + "." + codeDescDTO.getMethodName()
							+ " = dynamicFormFieldRefValueT" + codeDescDTO.getFormId() + codeDescDTO.getTablePosition()
							+ codeDescDTO.getMethodName() + ".Field_Ref_Value_ID ) ";

				} else {
					from = from + " ON (dynamicFormTableRecord_" + codeDescDTO.getFormId()
							+ codeDescDTO.getTablePosition() + "." + codeDescDTO.getMethodName()
							+ " = dynamicFormFieldRefValueT" + codeDescDTO.getFormId() + codeDescDTO.getTablePosition()
							+ codeDescDTO.getMethodName() + ".Field_Ref_Value_ID ) ";

				}

			} else {
				from = from + "LEFT OUTER JOIN Dynamic_Form_Field_Ref_Value AS dynamicFormFieldRefValue"
						+ codeDescDTO.getFormId() + codeDescDTO.getMethodName() + " ";
				tableJoinsList.add("dynamicFormFieldRefValue" + codeDescDTO.getFormId() + codeDescDTO.getMethodName());
				from = from + " ON (dynamicFormRecord" + codeDescDTO.getFormId() + "." + codeDescDTO.getMethodName()
						+ " = dynamicFormFieldRefValue" + codeDescDTO.getFormId() + codeDescDTO.getMethodName()
						+ ".Field_Ref_Value_ID ) ";
			}

		}
		return from;
	}

	public String createCustomFieldWhere(long companyId, List<String> tableJoinsList, List<Long> employeeIdsList,
			Map<String, List<Long>> paramValueMap) {
		String where = " WHERE employee.Company_ID =:companyId ";

		String whereAnd = " AND ";

		StringBuilder empConditionBuilder = new StringBuilder();
		empConditionBuilder = empConditionBuilder.append(" employee.Employee_ID in ( ");
		for (String tableJoinVal : tableJoinsList) {
			if ("employee".equals(tableJoinVal)) {

				paramValueMap.put("employeeIdsList", employeeIdsList);
				empConditionBuilder = empConditionBuilder.append(":employeeIdsList");
				// for (int empCount = 1; empCount <= employeeIdsList.size();
				// empCount++) {
				// empConditionBuilder = empConditionBuilder
				// .append(employeeIdsList.get(empCount - 1));
				// if (empCount != employeeIdsList.size()) {
				// empConditionBuilder = empConditionBuilder.append(",");
				// }
				//
				// }
				empConditionBuilder = empConditionBuilder.append(" ) ");

			}
		}
		String employeeCondition = empConditionBuilder.toString();
		if (!employeeIdsList.isEmpty()) {
			where = where + whereAnd + employeeCondition;
		}

		where = where + " ORDER BY employee.Employee_ID DESC";

		return where;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.EmployeeDAO#findAllByCondition(com.payasia.common.dto
	 * .EmployeeConditionDTO, com.payasia.common.form.PageRequest,
	 * com.payasia.common.form.SortCondition, java.lang.Long)
	 */
	@Override
	public List<Employee> findAllByCondition(EmployeeConditionDTO conditionDTO, PageRequest pageDTO,
			SortCondition sortDTO, Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Employee> criteriaQuery = cb.createQuery(Employee.class);
		Root<Employee> empRoot = criteriaQuery.from(Employee.class);
		criteriaQuery.select(empRoot);

		Join<Employee, Company> empCompanyJoin = empRoot.join(Employee_.company);

		Predicate restriction = cb.conjunction();

		if (StringUtils.isNotBlank(conditionDTO.getEmployeeNumber())) {

			restriction = cb.and(restriction, cb.equal(cb.upper(empRoot.get(Employee_.employeeNumber)),
					conditionDTO.getEmployeeNumber().toUpperCase()));
		}

		if (StringUtils.isNotBlank(conditionDTO.getFirstName())) {

			restriction = cb.and(restriction, cb.like(cb.upper(empRoot.get(Employee_.firstName)),
					conditionDTO.getFirstName().toUpperCase() + '%'));
		}
		if (StringUtils.isNotBlank(conditionDTO.getLastName())) {

			restriction = cb.and(restriction,
					cb.like(cb.upper(empRoot.get(Employee_.lastName)), conditionDTO.getLastName().toUpperCase() + '%'));
		}

		if (StringUtils.isNotBlank(conditionDTO.getEmployeeName())) {

			restriction = cb.and(restriction, cb.like(cb.upper(empRoot.get(Employee_.firstName)),
					conditionDTO.getEmployeeName().toUpperCase() + '%'));

			restriction = cb.or(restriction, cb.like(cb.upper(empRoot.get(Employee_.lastName)),
					conditionDTO.getEmployeeName().toUpperCase() + '%'));
		}

		if (conditionDTO.getEmployeeShortListDTO() != null) {
			if (conditionDTO.getEmployeeShortListDTO().getEmployeeShortList()
					&& conditionDTO.getEmployeeShortListDTO().getShortListEmployeeIds().isEmpty()) {

				restriction = cb.and(restriction, empRoot.get("employeeId").in(-1));

			} else if (conditionDTO.getEmployeeShortListDTO().getEmployeeShortList()
					&& !conditionDTO.getEmployeeShortListDTO().getShortListEmployeeIds().isEmpty()) {
				restriction = cb.and(restriction,
						empRoot.get("employeeId").in(conditionDTO.getEmployeeShortListDTO().getShortListEmployeeIds()));
			}
		}

		restriction = cb.and(restriction, cb.equal(empCompanyJoin.get(Company_.companyId), companyId));

		criteriaQuery.where(restriction);

		if (sortDTO != null) {

			Path<String> sortPath = getSortPathForSearchEmployee(sortDTO, empRoot);
			if (sortPath != null) {
				if (SortConstants.DB_ORDER_BY_ASC.equalsIgnoreCase(sortDTO.getOrderType())) {
					criteriaQuery.orderBy(cb.asc(sortPath));
				}
				if (SortConstants.DB_ORDER_BY_DESC.equalsIgnoreCase(sortDTO.getOrderType())) {
					criteriaQuery.orderBy(cb.desc(sortPath));
				}
			}

		}

		TypedQuery<Employee> empTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			empTypedQuery.setFirstResult(getStartPosition(pageDTO));
			empTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		List<Employee> employeeList = empTypedQuery.getResultList();

		return employeeList;
	}

	@Override
	public List<Employee> findByCompany(Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Employee> criteriaQuery = cb.createQuery(Employee.class);
		Root<Employee> empRoot = criteriaQuery.from(Employee.class);
		criteriaQuery.select(empRoot);

		Join<Employee, Company> empRootCompanyJoin = empRoot.join(Employee_.company);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(empRootCompanyJoin.get(Company_.companyId), companyId));

		criteriaQuery.where(restriction);

		TypedQuery<Employee> empTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<Employee> employeeList = empTypedQuery.getResultList();

		return employeeList;
	}

	@Override
	public List<Employee> findEmployeesByCondition(EmployeeConditionDTO conditionDTO, PageRequest pageDTO,
			SortCondition sortDTO, Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Employee> criteriaQuery = cb.createQuery(Employee.class);
		Root<Employee> empRoot = criteriaQuery.from(Employee.class);
		criteriaQuery.select(empRoot);

		Join<Employee, Company> empCompanyJoin = empRoot.join(Employee_.company);

		Predicate restriction = cb.conjunction();

		if (StringUtils.isNotBlank(conditionDTO.getEmployeeNumber())) {

			restriction = cb.and(restriction, cb.like(cb.upper(empRoot.get(Employee_.employeeNumber)),
					conditionDTO.getEmployeeNumber().toUpperCase() + '%'));
		}

		if (StringUtils.isNotBlank(conditionDTO.getFirstName())) {

			restriction = cb.and(restriction, cb.like(cb.upper(empRoot.get(Employee_.firstName)),
					conditionDTO.getFirstName().toUpperCase() + '%'));
		}
		if (StringUtils.isNotBlank(conditionDTO.getLastName())) {

			restriction = cb.and(restriction,
					cb.like(cb.upper(empRoot.get(Employee_.lastName)), conditionDTO.getLastName().toUpperCase() + '%'));
		}

		if (StringUtils.isNotBlank(conditionDTO.getEmployeeName())) {

			restriction = cb.and(restriction, cb.like(cb.upper(empRoot.get(Employee_.firstName)),
					conditionDTO.getEmployeeName().toUpperCase() + '%'));

			restriction = cb.or(restriction, cb.like(cb.upper(empRoot.get(Employee_.lastName)),
					conditionDTO.getEmployeeName().toUpperCase() + '%'));
		}

		if (conditionDTO.getEmployeeShortListDTO() != null) {
			if (conditionDTO.getEmployeeShortListDTO().getEmployeeShortList()
					&& conditionDTO.getEmployeeShortListDTO().getShortListEmployeeIds().isEmpty()) {

				restriction = cb.and(restriction, empRoot.get("employeeId").in(-1));

			} else if (conditionDTO.getEmployeeShortListDTO().getEmployeeShortList()
					&& !conditionDTO.getEmployeeShortListDTO().getShortListEmployeeIds().isEmpty()) {
				restriction = cb.and(restriction,
						empRoot.get("employeeId").in(conditionDTO.getEmployeeShortListDTO().getShortListEmployeeIds()));
			}
		}

		restriction = cb.and(restriction, cb.equal(empCompanyJoin.get(Company_.companyId), companyId));
		criteriaQuery.where(restriction);

		if (sortDTO != null) {

			Path<String> sortPath = getSortPathForSearchEmployee(sortDTO, empRoot);
			if (sortPath != null) {
				if (SortConstants.DB_ORDER_BY_ASC.equalsIgnoreCase(sortDTO.getOrderType())) {
					criteriaQuery.orderBy(cb.asc(sortPath));
				}
				if (SortConstants.DB_ORDER_BY_DESC.equalsIgnoreCase(sortDTO.getOrderType())) {
					criteriaQuery.orderBy(cb.desc(sortPath));
				}
			}

		}

		TypedQuery<Employee> empTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			empTypedQuery.setFirstResult(getStartPosition(pageDTO));
			empTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		List<Employee> employeeList = empTypedQuery.getResultList();

		return employeeList;
	}

	@Override
	public EmployeeListForm deleteEmployeeProc(final Long employeeId) {
		final EmployeeListForm employeeListForm = new EmployeeListForm();

		getSession().doWork(new Work() {

			@Override
			public void execute(Connection connection) throws SQLException {
				try {
					CallableStatement cstmt = null;
					cstmt = connection.prepareCall("{call DELETE_Employee (?,?)}");

					cstmt.setLong("@Employee_ID", employeeId);
					cstmt.registerOutParameter("@Status", java.sql.Types.BOOLEAN);
					cstmt.execute();
					employeeListForm.setStatus(cstmt.getBoolean("@Status"));
				} catch (SQLException e) {
					throw new PayAsiaSystemException(e.getMessage(), e);
				}

			}
		});

		return employeeListForm;
	}

	@Override
	public int findEmployeesOfGroupCompaniesCount(EmployeeConditionDTO conditionDTO, PageRequest pageDTO,
			SortCondition sortDTO, Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Integer> criteriaQuery = cb.createQuery(Integer.class);
		Root<Employee> empRoot = criteriaQuery.from(Employee.class);
		criteriaQuery.select(cb.count(empRoot).as(Integer.class));

		Join<Employee, Company> empCompanyJoin = empRoot.join(Employee_.company);

		Join<Company, CompanyGroup> groupJoin = empCompanyJoin.join(Company_.companyGroup);

		Predicate restriction = cb.conjunction();

		if (StringUtils.isNotBlank(conditionDTO.getEmployeeNumber())) {

			restriction = cb.and(restriction, cb.like(cb.upper(empRoot.get(Employee_.employeeNumber)),
					conditionDTO.getEmployeeNumber().toUpperCase() + '%'));
		}

		if (StringUtils.isNotBlank(conditionDTO.getEmployeeName())) {

			restriction = cb.and(restriction,
					cb.or(cb.like(cb.upper(empRoot.get(Employee_.firstName)),
							conditionDTO.getEmployeeName().toUpperCase() + '%'),
							cb.like(cb.upper(empRoot.get(Employee_.lastName)),
									conditionDTO.getEmployeeName().toUpperCase() + '%')));
		}

		restriction = cb.and(restriction, cb.equal(empRoot.get(Employee_.status), true));

		restriction = cb.and(restriction, cb.equal(groupJoin.get(CompanyGroup_.groupId), conditionDTO.getGroupId()));
		criteriaQuery.where(restriction);

		TypedQuery<Integer> empTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		return empTypedQuery.getSingleResult();

	}

	@Override
	public List<String> findAllEmpNumberByCompany(Long companyId) {
		StringBuilder sqlQuery = new StringBuilder("");
		sqlQuery.append(" SELECT NEW java.lang.String(emp.employeeNumber) FROM  Employee emp ");
		sqlQuery.append(" WHERE emp.company.companyId=:companyId");
		TypedQuery<String> query = entityManagerFactory.createQuery(sqlQuery.toString(), String.class);
		query.setParameter("companyId", companyId);
		return query.getResultList();
	}

	@Override
	public List<Employee> findByCondition(EmployeeConditionDTO conditionDTO, PageRequest pageDTO, SortCondition sortDTO,
			Long companyId) {
		return findByCondition(conditionDTO, pageDTO, sortDTO, companyId, -1L);
	}

	@Override
	public List<Employee> findByCondition(EmployeeConditionDTO conditionDTO, PageRequest pageDTO, SortCondition sortDTO,
			Long companyId, Long employeeId,Long groupCompanyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Employee> criteriaQuery = cb.createQuery(Employee.class);
		Root<Employee> empRoot = criteriaQuery.from(Employee.class);
		criteriaQuery.select(empRoot);

		Join<Employee, Company> empCompanyJoin = empRoot.join(Employee_.company);
		Join<Company, CompanyGroup> groupJoin = empCompanyJoin.join(Company_.companyGroup);

		Predicate restriction = cb.conjunction();

		if (StringUtils.isNotBlank(conditionDTO.getEmployeeNumber())) {

			restriction = cb.and(restriction, cb.like(cb.upper(empRoot.get(Employee_.employeeNumber)),
					conditionDTO.getEmployeeNumber().toUpperCase() + '%'));
		}

		if (StringUtils.isNotBlank(conditionDTO.getFirstName())) {

			restriction = cb.and(restriction, cb.like(cb.upper(empRoot.get(Employee_.firstName)),
					conditionDTO.getFirstName().toUpperCase() + '%'));
		}
		if (StringUtils.isNotBlank(conditionDTO.getLastName())) {

			restriction = cb.and(restriction,
					cb.like(cb.upper(empRoot.get(Employee_.lastName)), conditionDTO.getLastName().toUpperCase() + '%'));
		}

		if (StringUtils.isNotBlank(conditionDTO.getEmployeeName())) {

			restriction = cb.and(restriction, cb.like(cb.upper(empRoot.get(Employee_.firstName)),
					conditionDTO.getEmployeeName().toUpperCase() + '%'));

			restriction = cb.or(restriction, cb.like(cb.upper(empRoot.get(Employee_.lastName)),
					conditionDTO.getEmployeeName().toUpperCase() + '%'));
		}

		if (conditionDTO.getEmployeeShortListDTO() != null) {
			if (conditionDTO.getEmployeeShortListDTO().getEmployeeShortList()
					&& conditionDTO.getEmployeeShortListDTO().getShortListEmployeeIds().isEmpty()) {

				restriction = cb.and(restriction, empRoot.get("employeeId").in(-1));

			} else if (conditionDTO.getEmployeeShortListDTO().getEmployeeShortList()
					&& !conditionDTO.getEmployeeShortListDTO().getShortListEmployeeIds().isEmpty()) {
				restriction = cb.and(restriction,
						empRoot.get("employeeId").in(conditionDTO.getEmployeeShortListDTO().getShortListEmployeeIds()));
			}
		}

		if (employeeId != -1L) {
			restriction = cb.and(restriction, cb.notEqual(empRoot.get(Employee_.employeeId), employeeId));
		}
		if(groupCompanyId==null){
			restriction = cb.and(restriction, cb.equal(groupJoin.get(CompanyGroup_.groupId), companyDAO.findById(companyId).getCompanyGroup().getGroupId()));
		}
		else{
			restriction = cb.and(restriction, cb.equal(groupJoin.get(CompanyGroup_.groupId), groupCompanyId));
			
		}
		if (conditionDTO.getStatus() == null || conditionDTO.getStatus() == false) {
			restriction = cb.and(restriction, cb.equal(empRoot.get(Employee_.status), true));
		}
		if (StringUtils.isNotBlank(conditionDTO.getRoleName())) {
			Subquery<Long> subquery = criteriaQuery.subquery(Long.class);
			Root<EmployeeRoleMapping> employeeRoleMap = subquery.from(EmployeeRoleMapping.class);
			subquery.select(employeeRoleMap.get(EmployeeRoleMapping_.employee).get("employeeId").as(Long.class));

			Path<String> empRole = employeeRoleMap.get(EmployeeRoleMapping_.roleMaster).get("roleName");

			Predicate subRestriction = cb.conjunction();

			subRestriction = cb.and(subRestriction, cb.equal(empRole, conditionDTO.getRoleName()));

			subquery.where(subRestriction);

			restriction = cb.and(restriction, cb.not(cb.in(empRoot.get(Employee_.employeeId)).value(subquery)));

		}

		criteriaQuery.where(restriction);

		if (sortDTO != null) {

			Path<String> sortPath = getSortPathForSearchEmployee(sortDTO, empRoot);
			if (sortPath != null) {
				if (SortConstants.DB_ORDER_BY_ASC.equalsIgnoreCase(sortDTO.getOrderType())) {
					criteriaQuery.orderBy(cb.asc(sortPath));
				}
				if (SortConstants.DB_ORDER_BY_DESC.equalsIgnoreCase(sortDTO.getOrderType())) {
					criteriaQuery.orderBy(cb.desc(sortPath));
				}
			}

		}

		TypedQuery<Employee> empTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			empTypedQuery.setFirstResult(getStartPosition(pageDTO));
			empTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		List<Employee> employeeList = empTypedQuery.getResultList();

		return employeeList;
	}
	@Override
	public List<Employee> getEmployeeListByEmpNum(Long companyId, List<String> employeeNumberList) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Employee> criteriaQuery = cb.createQuery(Employee.class);
		Root<Employee> empRoot = criteriaQuery.from(Employee.class);
		criteriaQuery.select(empRoot);

		Join<Employee, Company> empCompanyJoin = empRoot.join(Employee_.company);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(empRoot.get(Employee_.status), true));

		restriction = cb.and(restriction, empRoot.get(Employee_.employeeNumber).in(employeeNumberList));

		restriction = cb.and(restriction, cb.equal(empCompanyJoin.get(Company_.companyId), companyId));
		criteriaQuery.where(restriction);

		TypedQuery<Employee> empTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<Employee> employeeList = empTypedQuery.getResultList();

		return employeeList;
	}

	@Override
	public List<Employee> getEmpByUsernameOrEmailOrFullName(String emailOrUsernameOrFullName, String companyCode) {
		emailOrUsernameOrFullName = emailOrUsernameOrFullName.replace(" ", "");
		String query = "SELECT emp FROM Employee emp ";
		query += "WHERE emp.company.companyCode = :company AND (emp.email = :emailOrUsernameOrFullName OR emp.employeeNumber = :emailOrUsernameOrFullName OR ";
		query += "(replace(emp.firstName,' ','') + replace(isnull(emp.middleName,''),' ','') + replace(isnull(emp.lastName,''),' ','')) = :emailOrUsernameOrFullName) ";

		Query q = entityManagerFactory.createQuery(query);
		q.setParameter("company", companyCode);
		q.setParameter("emailOrUsernameOrFullName", emailOrUsernameOrFullName);
		List<Employee> employeeList = q.getResultList();
		return employeeList;
	}

	@Override
	public List<Employee> getEmpByEmailOrFullName(String emailOrFullName, String companyCode) {
		emailOrFullName = emailOrFullName.replace(" ", "");
		String query = "SELECT emp FROM Employee emp ";
		query += "WHERE emp.company.companyCode = :company AND (emp.email = :emailOrFullName OR ";
		query += "(replace(emp.firstName,' ','') + replace(isnull(emp.middleName,''),' ','') + replace(isnull(emp.lastName,''),' ','')) = :emailOrFullName) ";

		Query q = entityManagerFactory.createQuery(query);
		q.setParameter("company", companyCode);
		q.setParameter("emailOrFullName", emailOrFullName);
		List<Employee> employeeList = q.getResultList();
		return employeeList;
	}

	@Override
	public List<Employee> findByEmpStaticInfo(EmployeeConditionDTO conditionDTO, PageRequest pageDTO, Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Employee> criteriaQuery = cb.createQuery(Employee.class);
		Root<Employee> empRoot = criteriaQuery.from(Employee.class);
		criteriaQuery.select(empRoot);

		Join<Employee, Company> empCompanyJoin = empRoot.join(Employee_.company);

		Predicate restriction = cb.conjunction();

		if (StringUtils.isNotBlank(conditionDTO.getEmployeeId())) {
			restriction = cb.and(restriction,
					cb.equal(empRoot.get(Employee_.employeeId), conditionDTO.getEmployeeId()));
		}

		if (StringUtils.isNotBlank(conditionDTO.getFirstName())) {
			restriction = cb.and(restriction,
					cb.equal(cb.upper(empRoot.get(Employee_.firstName)), conditionDTO.getFirstName().toUpperCase()));
		}
		if (StringUtils.isNotBlank(conditionDTO.getMiddleName())) {
			restriction = cb.and(restriction,
					cb.equal(cb.upper(empRoot.get(Employee_.middleName)), conditionDTO.getMiddleName().toUpperCase()));
		}
		if (StringUtils.isNotBlank(conditionDTO.getLastName())) {
			restriction = cb.and(restriction,
					cb.equal(cb.upper(empRoot.get(Employee_.lastName)), conditionDTO.getLastName().toUpperCase()));
		}
		if (StringUtils.isNotBlank(conditionDTO.getEmail())) {
			restriction = cb.and(restriction,
					cb.equal(cb.upper(empRoot.get(Employee_.email)), conditionDTO.getEmail().toUpperCase()));
		}
		if (StringUtils.isNotBlank(conditionDTO.getHireDate())) {
			restriction = cb.and(restriction, cb.equal(empRoot.get(Employee_.hireDate).as(Date.class),
					DateUtils.stringToDate(conditionDTO.getHireDate(), PayAsiaConstants.SERVER_SIDE_DATE_FORMAT)));
		}
		if (StringUtils.isNotBlank(conditionDTO.getOriginalHireDate())) {
			restriction = cb.and(restriction, cb.equal(empRoot.get(Employee_.originalHireDate).as(Date.class), DateUtils
					.stringToDate(conditionDTO.getOriginalHireDate(), PayAsiaConstants.SERVER_SIDE_DATE_FORMAT)));
		}
		if (StringUtils.isNotBlank(conditionDTO.getConfirmationDate())) {
			restriction = cb.and(restriction, cb.equal(empRoot.get(Employee_.confirmationDate).as(Date.class), DateUtils
					.stringToDate(conditionDTO.getConfirmationDate(), PayAsiaConstants.SERVER_SIDE_DATE_FORMAT)));
		}
		if (StringUtils.isNotBlank(conditionDTO.getResignationDate())) {
			restriction = cb.and(restriction, cb.equal(empRoot.get(Employee_.resignationDate).as(Date.class), DateUtils
					.stringToDate(conditionDTO.getResignationDate(), PayAsiaConstants.SERVER_SIDE_DATE_FORMAT)));
		}
		if (StringUtils.isNotBlank(conditionDTO.getEmploymentStatus())) {
			restriction = cb.and(restriction,
					cb.equal(empRoot.get(Employee_.employmentStatus), conditionDTO.getEmploymentStatus()));
		}
		// if (conditionDTO.getStatus() == null
		// || conditionDTO.getStatus() == false) {
		// restriction = cb.and(restriction,
		// cb.equal(empRoot.get(Employee_.status), true));
		// }

		restriction = cb.and(restriction, cb.equal(empCompanyJoin.get(Company_.companyId), companyId));
		criteriaQuery.where(restriction);

		TypedQuery<Employee> empTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			empTypedQuery.setFirstResult(getStartPosition(pageDTO));
			empTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		List<Employee> employeeIdList = empTypedQuery.getResultList();
		return employeeIdList;
	}

	@Override
	public List<Tuple> getLundinManagerInfoDetail(EmployeeConditionDTO conditionDTO, Long employeeId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Tuple> criteriaQuery = cb.createQuery(Tuple.class);
		Root<Employee> empRevRoot = criteriaQuery.from(Employee.class);

		List<Selection<?>> selectionItems = new ArrayList<Selection<?>>();

		selectionItems.add(empRevRoot.get(Employee_.employeeId).alias(getAlias(Employee_.employeeId)));

		selectionItems.add(empRevRoot.get(Employee_.employeeNumber).alias(getAlias(Employee_.employeeNumber)));
		selectionItems.add(empRevRoot.get(Employee_.firstName).alias(getAlias(Employee_.firstName)));
		selectionItems.add(empRevRoot.get(Employee_.lastName).alias(getAlias(Employee_.lastName)));
		criteriaQuery.multiselect(selectionItems).distinct(true);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(empRevRoot.get(Employee_.employeeId), employeeId));

		if (StringUtils.isNotBlank(conditionDTO.getEmployeeNumber())) {

			restriction = cb.and(restriction, cb.like(cb.upper(empRevRoot.get(Employee_.employeeNumber)),
					conditionDTO.getEmployeeNumber().toUpperCase() + '%'));
		}

		if (StringUtils.isNotBlank(conditionDTO.getFirstName())) {

			restriction = cb.and(restriction, cb.like(cb.upper(empRevRoot.get(Employee_.firstName)),
					conditionDTO.getFirstName().toUpperCase() + '%'));
		}
		if (StringUtils.isNotBlank(conditionDTO.getLastName())) {

			restriction = cb.and(restriction, cb.like(cb.upper(empRevRoot.get(Employee_.lastName)),
					conditionDTO.getLastName().toUpperCase() + '%'));
		}

		if (StringUtils.isNotBlank(conditionDTO.getEmployeeName())) {

			restriction = cb.and(restriction, cb.like(cb.upper(empRevRoot.get(Employee_.firstName)),
					conditionDTO.getEmployeeName().toUpperCase() + '%'));

			restriction = cb.or(restriction, cb.like(cb.upper(empRevRoot.get(Employee_.lastName)),
					conditionDTO.getEmployeeName().toUpperCase() + '%'));
		}

		if (conditionDTO.getStatus() == null || conditionDTO.getStatus() == false) {
			restriction = cb.and(restriction, cb.equal(empRevRoot.get(Employee_.status), true));
		}

		criteriaQuery.where(restriction);

		TypedQuery<Tuple> empTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<Tuple> employeeList = empTypedQuery.getResultList();
		return employeeList;
	}

	@Override
	public List<Employee> getEmpByPrivilageName(Long companyId, String PrivilageName) {
		String query = new StringBuilder()
				.append("SELECT emp FROM Employee emp INNER JOIN emp.employeeRoleMappings erm")
				.append(" INNER JOIN erm.roleMaster rm").append(" INNER JOIN rm.privilegeMasters pm")
				.append(" WHERE pm.privilegeName= :privilegeName and emp.company.companyId= :companyId").toString();
		Query q = entityManagerFactory.createQuery(query);
		q.setParameter("privilegeName", PrivilageName);
		q.setParameter("companyId", companyId);
		List<Employee> employeeList = q.getResultList();
		return employeeList;

	}

	@Override
	public Employee findByNumber(String employeeNumber) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Employee> criteriaQuery = cb.createQuery(Employee.class);
		Root<Employee> employeeRoot = criteriaQuery.from(Employee.class);

		criteriaQuery.select(employeeRoot);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.like(cb.upper(employeeRoot.get(Employee_.employeeNumber)), employeeNumber.toUpperCase()));

		criteriaQuery.where(restriction);

		TypedQuery<Employee> typedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<Employee> employeeList = typedQuery.getResultList();
		if (employeeList != null && !employeeList.isEmpty()) {
			return employeeList.get(0);
		}
		return null;

	}

	@Override
	public Object getEmpByValueCustomField(Long companyId, Long employeeId, String custonField) {

		StringBuilder query = new StringBuilder(" SELECT ");
		query.append(custonField);
		query.append(" FROM Employee where Company_ID = :companyId  AND Employee_ID = :employeeId ");

		Query q = entityManagerFactory.createNativeQuery(query.toString());
		q.setParameter("companyId", companyId);
		q.setParameter("employeeId", employeeId);
		Object employeeValue = q.getResultList().get(0);
		return employeeValue;
	}

	@Override
	public boolean isEmployeeExistInCompany(Long employeeId, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Employee> criteriaQuery = cb.createQuery(Employee.class);
		Root<Employee> employeeRoot = criteriaQuery.from(Employee.class);

		criteriaQuery.select(employeeRoot);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(employeeRoot.get(Employee_.employeeId), employeeId));

		restriction = cb.and(restriction, cb.equal(employeeRoot.get(Employee_.company), companyId));
		criteriaQuery.where(restriction);

		TypedQuery<Employee> typedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<Employee> list = typedQuery.getResultList();
		return list != null && !list.isEmpty() ? true : false;

	}

	@Override
	public List<Long> findAllEmpIdByCompany(Long companyId) {
		StringBuilder sqlQuery = new StringBuilder("");
		sqlQuery.append(" SELECT NEW java.lang.Long(emp.employeeId) FROM  Employee emp ");
		sqlQuery.append(" WHERE emp.company.companyId=:companyId");
		TypedQuery<Long> query = entityManagerFactory.createQuery(sqlQuery.toString(), Long.class);
		query.setParameter("companyId", companyId);
		return query.getResultList();
	}

	@Override
	public Employee findByNumberEmp(String employeeNumber, Long companyId, Long loggedInUser) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Employee> criteriaQuery = cb.createQuery(Employee.class);
		Root<Employee> empRoot = criteriaQuery.from(Employee.class);
		criteriaQuery.select(empRoot);

		Join<Employee, Company> empRootCompanyJoin = empRoot.join(Employee_.company, JoinType.INNER);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(empRootCompanyJoin.get(Company_.companyId), companyId));
		restriction = cb.and(restriction,
				cb.equal(cb.upper(empRoot.get(Employee_.employeeNumber)), employeeNumber.toUpperCase()));
		restriction = cb.and(restriction, cb.equal(empRoot.get(Employee_.employeeId), loggedInUser));
		criteriaQuery.where(restriction);

		TypedQuery<Employee> empTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		List<Employee> employeeList = empTypedQuery.getResultList();
		if (employeeList != null && !employeeList.isEmpty()) {
			return employeeList.get(0);
		}
		return null;
	}

	@Override
	public Employee findEmpByIdinReviewers(Long employeeId, Long leaveApplicationID) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Employee> criteriaQuery = cb.createQuery(Employee.class);
		Root<Employee> empRoot = criteriaQuery.from(Employee.class);
		criteriaQuery.select(empRoot);

		Join<Employee, LeaveApplicationReviewer> empRootReviewerJoin = empRoot.join(Employee_.leaveApplicationReviewers,
				JoinType.INNER);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction,
				cb.equal(empRootReviewerJoin.get(LeaveApplicationReviewer_.employee), employeeId));
		restriction = cb.and(restriction,
				cb.equal(empRootReviewerJoin.get(LeaveApplicationReviewer_.leaveApplication), leaveApplicationID));
		restriction = cb.and(restriction, cb.equal(empRootReviewerJoin.get(LeaveApplicationReviewer_.pending), true));
		restriction = cb.and(restriction, cb.equal(empRoot.get(Employee_.employeeId), employeeId));
		criteriaQuery.where(restriction);

		TypedQuery<Employee> empTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		List<Employee> employeeList = empTypedQuery.getResultList();
		if (employeeList != null && !employeeList.isEmpty()) {
			return employeeList.get(0);
		}
		return null;
	}

	@Override
	public Employee findById(long userId, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Employee> criteriaQuery = cb.createQuery(Employee.class);
		Root<Employee> empRoot = criteriaQuery.from(Employee.class);
		criteriaQuery.select(empRoot);

		Join<Employee, Company> empRootCompanyJoin = empRoot.join(Employee_.company, JoinType.INNER);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(empRootCompanyJoin.get(Company_.companyId), companyId));
		restriction = cb.and(restriction, cb.equal(empRoot.get(Employee_.employeeId), userId));

		criteriaQuery.where(restriction);

		TypedQuery<Employee> empTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		List<Employee> employeeList = empTypedQuery.getResultList();
		if (employeeList != null && !employeeList.isEmpty()) {
			return employeeList.get(0);
		}
		return null;
	}

	@Override
	public Employee findByGroupCompanyId(Long reviewerId, Long groupId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Employee> criteriaQuery = cb.createQuery(Employee.class);
		Root<Employee> empRoot = criteriaQuery.from(Employee.class);
		criteriaQuery.select(empRoot);

		Join<Employee, Company> empRootCompanyJoin = empRoot.join(Employee_.company, JoinType.INNER);
		Join<Company, CompanyGroup> companyGroupJoin = empRootCompanyJoin.join(Company_.companyGroup, JoinType.INNER);
		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(companyGroupJoin.get(CompanyGroup_.groupId), groupId));
		restriction = cb.and(restriction, cb.equal(empRoot.get(Employee_.employeeId), reviewerId));

		criteriaQuery.where(restriction);

		TypedQuery<Employee> empTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		List<Employee> employeeList = empTypedQuery.getResultList();
		if (employeeList != null && !employeeList.isEmpty()) {
			return employeeList.get(0);
		}
		return null;
	}
	
	/**
	 * Used for the purpose of fetching Employee data on the basis of email-id
	 */
	@Override
	public List<Employee> getEmployeeByEmailWithCompanyCode(String email, String companyCode) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Employee> criteriaQuery = cb.createQuery(Employee.class);
		Root<Employee> empRoot = criteriaQuery.from(Employee.class);
		criteriaQuery.select(empRoot);
		Join<Employee, Company> empCompanyJoin = empRoot.join(Employee_.company);
		
		Predicate restriction = cb.conjunction();

		if (StringUtils.isNotBlank(email.trim())) {
			restriction = cb.and(restriction, cb.equal(empRoot.get(Employee_.email), email.trim()));
		}
		if (StringUtils.isNotBlank(companyCode.trim())) {
			restriction = cb.and(restriction, cb.equal(empCompanyJoin.get(Company_.companyCode), companyCode.toLowerCase()));
		}

		restriction = cb.and(restriction, cb.equal(empRoot.get(Employee_.status), true));
		criteriaQuery.where(restriction);

		TypedQuery<Employee> empTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<Employee> employeeList = empTypedQuery.getResultList();

		return employeeList;
	}
	
	/*
	 	ADDED FOR COMPANY_GROUP CHECK
	 */
	@Override
	public int getCountForGroupCondition(EmployeeConditionDTO conditionDTO, Long companyId, Long companyGroupId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Integer> criteriaQuery = cb.createQuery(Integer.class);
		Root<Employee> empRoot = criteriaQuery.from(Employee.class);
		criteriaQuery.select(cb.count(empRoot).as(Integer.class));

		Join<Employee, Company> empCompanyJoin = empRoot.join(Employee_.company);
		
		// Added for companyGroup check
		Join<Company, CompanyGroup> groupJoin = empCompanyJoin.join(Company_.companyGroup);

		Predicate restriction = cb.conjunction();

		if (StringUtils.isNotBlank(conditionDTO.getEmployeeNumber())) {

			restriction = cb.and(restriction, cb.like(cb.upper(empRoot.get(Employee_.employeeNumber)),
					conditionDTO.getEmployeeNumber().toUpperCase()));
		}

		if (StringUtils.isNotBlank(conditionDTO.getEmployeeName())) {

			restriction = cb.and(restriction, cb.or(
					cb.like(cb.upper(empRoot.get(Employee_.firstName)), conditionDTO.getEmployeeName().toUpperCase()),
					cb.like(cb.upper(empRoot.get(Employee_.lastName)), conditionDTO.getEmployeeName().toUpperCase())));
		}

		if (StringUtils.isNotBlank(conditionDTO.getEmployeeName())) {

			restriction = cb.and(restriction, cb.like(cb.upper(empRoot.get(Employee_.firstName)),
					conditionDTO.getEmployeeName().toUpperCase() + '%'));

			restriction = cb.or(restriction, cb.like(cb.upper(empRoot.get(Employee_.lastName)),
					conditionDTO.getEmployeeName().toUpperCase() + '%'));
		}

		if (conditionDTO.getEmployeeShortListDTO().getEmployeeShortList()
				&& conditionDTO.getEmployeeShortListDTO().getShortListEmployeeIds().isEmpty()) {

			restriction = cb.and(restriction, empRoot.get("employeeId").in(-1));

		} else if (conditionDTO.getEmployeeShortListDTO().getEmployeeShortList()
				&& !conditionDTO.getEmployeeShortListDTO().getShortListEmployeeIds().isEmpty()) {
			restriction = cb.and(restriction,
					empRoot.get("employeeId").in(conditionDTO.getEmployeeShortListDTO().getShortListEmployeeIds()));

		}
		
		restriction = cb.and(restriction, cb.equal(groupJoin.get(CompanyGroup_.groupId), companyGroupId));
		
		if (conditionDTO.getStatus() == null || conditionDTO.getStatus() == false) {
			restriction = cb.and(restriction, cb.equal(empRoot.get(Employee_.status), true));
		}
		if (StringUtils.isNotBlank(conditionDTO.getRoleName())) {
			Subquery<Long> subquery = criteriaQuery.subquery(Long.class);
			Root<EmployeeRoleMapping> employeeRoleMap = subquery.from(EmployeeRoleMapping.class);
			subquery.select(employeeRoleMap.get(EmployeeRoleMapping_.employee).get("employeeId").as(Long.class));

			Path<String> empRole = employeeRoleMap.get(EmployeeRoleMapping_.roleMaster).get("roleName");

			Predicate subRestriction = cb.conjunction();

			subRestriction = cb.and(subRestriction, cb.equal(empRole, conditionDTO.getRoleName()));

			subquery.where(subRestriction);

			restriction = cb.and(restriction, cb.not(cb.in(empRoot.get(Employee_.employeeId)).value(subquery)));
		}
		criteriaQuery.where(restriction);

		TypedQuery<Integer> empTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		return empTypedQuery.getSingleResult();
	}
	
	/*
	 	ADDED FOR COMPANY_GROUP CHECK
	 */
	@Override
	public List<Employee> findByGroupCondition(EmployeeConditionDTO conditionDTO, PageRequest pageDTO, SortCondition sortDTO,
			Long companyId, Long companyGroupId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Employee> criteriaQuery = cb.createQuery(Employee.class);
		Root<Employee> empRoot = criteriaQuery.from(Employee.class);
		criteriaQuery.select(empRoot);

		Join<Employee, Company> empCompanyJoin = empRoot.join(Employee_.company);
		
		// Added for companyGroup check
		Join<Company, CompanyGroup> groupJoin = empCompanyJoin.join(Company_.companyGroup);
		Predicate restriction = cb.conjunction();

		if (StringUtils.isNotBlank(conditionDTO.getEmployeeNumber())) {

			restriction = cb.and(restriction, cb.like(cb.upper(empRoot.get(Employee_.employeeNumber)),
					conditionDTO.getEmployeeNumber().toUpperCase() + '%'));
		}

		if (StringUtils.isNotBlank(conditionDTO.getFirstName())) {

			restriction = cb.and(restriction, cb.like(cb.upper(empRoot.get(Employee_.firstName)),
					conditionDTO.getFirstName().toUpperCase() + '%'));
		}
		if (StringUtils.isNotBlank(conditionDTO.getLastName())) {

			restriction = cb.and(restriction,
					cb.like(cb.upper(empRoot.get(Employee_.lastName)), conditionDTO.getLastName().toUpperCase() + '%'));
		}

		if (StringUtils.isNotBlank(conditionDTO.getEmployeeName())) {

			restriction = cb.and(restriction, cb.like(cb.upper(empRoot.get(Employee_.firstName)),
					conditionDTO.getEmployeeName().toUpperCase() + '%'));

			restriction = cb.or(restriction, cb.like(cb.upper(empRoot.get(Employee_.lastName)),
					conditionDTO.getEmployeeName().toUpperCase() + '%'));
		}

		if (conditionDTO.getEmployeeShortListDTO() != null) {
			if (conditionDTO.getEmployeeShortListDTO().getEmployeeShortList()
					&& conditionDTO.getEmployeeShortListDTO().getShortListEmployeeIds().isEmpty()) {

				restriction = cb.and(restriction, empRoot.get("employeeId").in(-1));

			} else if (conditionDTO.getEmployeeShortListDTO().getEmployeeShortList()
					&& !conditionDTO.getEmployeeShortListDTO().getShortListEmployeeIds().isEmpty()) {
				restriction = cb.and(restriction,
						empRoot.get("employeeId").in(conditionDTO.getEmployeeShortListDTO().getShortListEmployeeIds()));
			}
		}

//		if (employeeId != -1L) {
//			restriction = cb.and(restriction, cb.notEqual(empRoot.get(Employee_.employeeId), employeeId));
//		}
		
		restriction = cb.and(restriction, cb.equal(groupJoin.get(CompanyGroup_.groupId), companyGroupId));
		
		if (conditionDTO.getStatus() == null || conditionDTO.getStatus() == false) {
			restriction = cb.and(restriction, cb.equal(empRoot.get(Employee_.status), true));
		}
		if (StringUtils.isNotBlank(conditionDTO.getRoleName())) {
			Subquery<Long> subquery = criteriaQuery.subquery(Long.class);
			Root<EmployeeRoleMapping> employeeRoleMap = subquery.from(EmployeeRoleMapping.class);
			subquery.select(employeeRoleMap.get(EmployeeRoleMapping_.employee).get("employeeId").as(Long.class));

			Path<String> empRole = employeeRoleMap.get(EmployeeRoleMapping_.roleMaster).get("roleName");

			Predicate subRestriction = cb.conjunction();

			subRestriction = cb.and(subRestriction, cb.equal(empRole, conditionDTO.getRoleName()));

			subquery.where(subRestriction);

			restriction = cb.and(restriction, cb.not(cb.in(empRoot.get(Employee_.employeeId)).value(subquery)));

		}

		criteriaQuery.where(restriction);

		if (sortDTO != null) {

			Path<String> sortPath = getSortPathForSearchEmployee(sortDTO, empRoot);
			if (sortPath != null) {
				if (SortConstants.DB_ORDER_BY_ASC.equalsIgnoreCase(sortDTO.getOrderType())) {
					criteriaQuery.orderBy(cb.asc(sortPath));
				}
				if (SortConstants.DB_ORDER_BY_DESC.equalsIgnoreCase(sortDTO.getOrderType())) {
					criteriaQuery.orderBy(cb.desc(sortPath));
				}
			}

		}

		TypedQuery<Employee> empTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			empTypedQuery.setFirstResult(getStartPosition(pageDTO));
			empTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		List<Employee> employeeList = empTypedQuery.getResultList();

		return employeeList;
	}


}
