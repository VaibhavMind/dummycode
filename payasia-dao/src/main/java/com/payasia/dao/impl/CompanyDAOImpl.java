/**
 * @author ragulapraveen
 *
 */
package com.payasia.dao.impl;

import java.lang.reflect.InvocationTargetException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Query;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.persistence.criteria.Subquery;

import org.apache.commons.lang.StringUtils;
import org.hibernate.jdbc.Work;
import org.springframework.stereotype.Repository;

import com.payasia.common.dto.CodeDescDTO;
import com.payasia.common.dto.CompanyConditionDTO;
import com.payasia.common.dto.CompanyCopyDTO;
import com.payasia.common.dto.DataImportKeyValueDTO;
import com.payasia.common.dto.ExcelExportQueryDTO;
import com.payasia.common.dto.ManageModuleDTO;
import com.payasia.common.dto.PayAsiaCompanyStatisticReportDTO;
import com.payasia.common.dto.RolePrivilegeReportDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.ExcelExportFiltersForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.NullAwareBeanUtilsBean;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.SortConstants;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.CompanyDefaultLanguageMapping;
import com.payasia.dao.bean.CompanyDefaultLanguageMapping_;
import com.payasia.dao.bean.CompanyGroup;
import com.payasia.dao.bean.CompanyGroup_;
import com.payasia.dao.bean.CompanyModuleMapping;
import com.payasia.dao.bean.CompanyModuleMapping_;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.CountryMaster;
import com.payasia.dao.bean.CountryMaster_;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeRoleMapping;
import com.payasia.dao.bean.EmployeeRoleMapping_;
import com.payasia.dao.bean.Employee_;
import com.payasia.dao.bean.FinancialYearMaster;
import com.payasia.dao.bean.FinancialYearMaster_;
import com.payasia.dao.bean.ModuleMaster;
import com.payasia.dao.bean.ModuleMaster_;
import com.payasia.dao.bean.PayslipFrequency;
import com.payasia.dao.bean.PayslipFrequency_;
import com.payasia.dao.bean.TimeZoneMaster;
import com.payasia.dao.bean.TimeZoneMaster_;

/**
 * The Class CompanyDAOImpl.
 */

@Repository
public class CompanyDAOImpl extends BaseDAO implements CompanyDAO {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.CompanyDAO#findAll(com.payasia.common.form.SortCondition)
	 */
	@Override
	public List<Company> findAll(SortCondition sortDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Company> criteriaQuery = cb.createQuery(Company.class);
		Root<Company> companyRoot = criteriaQuery.from(Company.class);

		criteriaQuery.select(companyRoot);

		Join<Company, CompanyGroup> companyGroupJoin = companyRoot
				.join(Company_.companyGroup);

		Predicate restriction = cb.conjunction();

		criteriaQuery.where(restriction);
		if (sortDTO != null) {

			Path<String> sortPath = getSortPathForAllCompany(sortDTO,
					companyRoot, companyGroupJoin);
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

		TypedQuery<Company> companyTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return companyTypedQuery.getResultList();
	}

	@Override
	public List<Company> getCompanyListOtherThanPaysaiaGroup(
			SortCondition sortDTO, Long payasiaGroupId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Company> criteriaQuery = cb.createQuery(Company.class);
		Root<Company> companyRoot = criteriaQuery.from(Company.class);

		criteriaQuery.select(companyRoot);

		Join<Company, CompanyGroup> companyGroupJoin = companyRoot
				.join(Company_.companyGroup);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.notEqual(
				companyGroupJoin.get(CompanyGroup_.groupId), payasiaGroupId));

		criteriaQuery.where(restriction);
		if (sortDTO != null) {

			Path<String> sortPath = getSortPathForAllCompany(sortDTO,
					companyRoot, companyGroupJoin);
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

		TypedQuery<Company> companyTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return companyTypedQuery.getResultList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.CompanyDAO#getSortPathForAllCompany(com.payasia.common
	 * .form.SortCondition, javax.persistence.criteria.Root,
	 * javax.persistence.criteria.Join)
	 */
	@Override
	public Path<String> getSortPathForAllCompany(SortCondition sortDTO,
			Root<Company> companyRoot,
			Join<Company, CompanyGroup> companyGroupJoin) {

		List<String> companyIsColList = new ArrayList<String>();
		companyIsColList.add(SortConstants.MANAGE_ROLES_COMPANY_NAME);
		companyIsColList.add(SortConstants.MANAGE_ROLES_COMPANY_CODE);

		List<String> companyGroupIsColList = new ArrayList<String>();
		companyGroupIsColList.add(SortConstants.MANAGE_ROLES_GROUP_NAME);
		companyGroupIsColList.add(SortConstants.MANAGE_ROLES_GROUP_CODE);

		Path<String> sortPath = null;

		if (companyIsColList.contains(sortDTO.getColumnName())) {
			sortPath = companyRoot.get(colMap.get(Company.class
					+ sortDTO.getColumnName()));
		}
		if (companyGroupIsColList.contains(sortDTO.getColumnName())) {
			sortPath = companyGroupJoin.get(colMap.get(CompanyGroup.class
					+ sortDTO.getColumnName()));
		}
		return sortPath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.CompanyDAO#findAll(com.payasia.common.dto.CompanyConditionDTO
	 * )
	 */
	@Override
	public List<Company> findAll(CompanyConditionDTO conditionDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Company> criteriaQuery = cb.createQuery(Company.class);
		Root<Company> companyRoot = criteriaQuery.from(Company.class);

		criteriaQuery.select(companyRoot);

		Predicate restriction = cb.conjunction();

		if (StringUtils.isNotBlank(conditionDTO.getCompanyCode())) {

			restriction = cb.and(restriction, cb.like(
					cb.upper(companyRoot.get(Company_.companyCode)),
					conditionDTO.getCompanyCode().toUpperCase()));
		}

		if (StringUtils.isNotBlank(conditionDTO.getCompanyName())) {

			restriction = cb.and(restriction, cb.like(
					cb.upper(companyRoot.get(Company_.companyName)),
					conditionDTO.getCompanyName().toUpperCase()));
		}

		criteriaQuery.where(restriction);

		criteriaQuery.orderBy(cb.asc(companyRoot.get(Company_.companyId)));

		TypedQuery<Company> companyTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return companyTypedQuery.getResultList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.CompanyDAO#findAssignCompanyToUser(com.payasia.common
	 * .form.SortCondition, com.payasia.common.form.PageRequest, java.lang.Long)
	 */
	@Override
	public List<Tuple> findAssignCompanyToUser(SortCondition sortDTO,
			PageRequest pageDTO, Long employeeId,
			CompanyConditionDTO conditionDTO) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Tuple> criteriaQuery = cb.createQuery(Tuple.class);
		Root<Company> companyRoot = criteriaQuery.from(Company.class);

		Join<Company, CompanyGroup> compGroupJoin = companyRoot.join(
				Company_.companyGroup, JoinType.INNER);

		List<Selection<?>> selectionItems = new ArrayList<Selection<?>>();

		selectionItems.add(compGroupJoin.get(CompanyGroup_.groupId).alias(
				getAlias(CompanyGroup_.groupId)));
		selectionItems.add(compGroupJoin.get(CompanyGroup_.groupName).alias(
				getAlias(CompanyGroup_.groupName)));
		selectionItems.add(compGroupJoin.get(CompanyGroup_.groupCode).alias(
				getAlias(CompanyGroup_.groupCode)));
		selectionItems.add(companyRoot.get(Company_.companyId).alias(
				getAlias(Company_.companyId)));
		selectionItems.add(companyRoot.get(Company_.companyCode).alias(
				getAlias(Company_.companyCode)));
		selectionItems.add(companyRoot.get(Company_.companyName).alias(
				getAlias(Company_.companyName)));
		criteriaQuery.multiselect(selectionItems).distinct(true);

		Join<Company, EmployeeRoleMapping> companyEmpJoin = companyRoot
				.join(Company_.employeeRoleMappings);

		Join<EmployeeRoleMapping, Employee> roleEmployeeJoin = companyEmpJoin
				.join(EmployeeRoleMapping_.employee);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(
				roleEmployeeJoin.get(Employee_.employeeId), employeeId));

		if (StringUtils.isNotBlank(conditionDTO.getCompanyName())) {
			restriction = cb.and(restriction, cb.like(
					cb.upper((companyRoot.get(Company_.companyName))),
					'%' + conditionDTO.getCompanyName().toUpperCase() + '%'));
		}
		if (StringUtils.isNotBlank(conditionDTO.getCompanyCode())) {
			restriction = cb.and(restriction, cb.like(
					cb.upper((companyRoot.get(Company_.companyCode))),
					'%' + conditionDTO.getCompanyCode().toUpperCase() + '%'));
		}
		if (StringUtils.isNotBlank(conditionDTO.getGroupCode())) {
			restriction = cb.and(restriction, cb.like(
					cb.upper((compGroupJoin.get(CompanyGroup_.groupCode))),
					'%' + conditionDTO.getGroupCode().toUpperCase() + '%'));
		}
		if (StringUtils.isNotBlank(conditionDTO.getGroupName())) {
			restriction = cb.and(restriction, cb.like(
					cb.upper((compGroupJoin.get(CompanyGroup_.groupName))),
					'%' + conditionDTO.getGroupName().toUpperCase() + '%'));
		}

		if (conditionDTO.isIncludeActiveStatus()) {
			if (conditionDTO.isIncludeInactiveCompany()) {
				restriction = cb.and(restriction,
						cb.equal(companyRoot.get(Company_.active), false));
			} else {
				restriction = cb.and(restriction,
						cb.equal(companyRoot.get(Company_.active), true));
			}
		}

		if (conditionDTO.isIncludeDemoCompanyStatus()) {
			if (conditionDTO.isIncludeDemoCompany()) {
				restriction = cb
						.and(restriction, cb.equal(
								companyRoot.get(Company_.isDemoCompany), true));
			} else {
				restriction = cb.and(restriction, cb.equal(
						companyRoot.get(Company_.isDemoCompany), false));
			}
		}

		criteriaQuery.where(restriction);

		if (sortDTO != null) {

			Path<String> sortPath = getSortPathForAllCompany(sortDTO,
					companyRoot, compGroupJoin);
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

		TypedQuery<Tuple> companyTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			companyTypedQuery.setFirstResult(getStartPosition(pageDTO));
			companyTypedQuery.setMaxResults(pageDTO.getPageSize());
		}
		return companyTypedQuery.getResultList();
	}

	@Override
	public List<Tuple> findAssignedCompanyToUserByCond(SortCondition sortDTO,
			PageRequest pageDTO, Long employeeId,
			CompanyConditionDTO conditionDTO) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Tuple> criteriaQuery = cb.createQuery(Tuple.class);
		Root<Company> companyRoot = criteriaQuery.from(Company.class);

		Join<Company, CompanyGroup> compGroupJoin = companyRoot.join(
				Company_.companyGroup, JoinType.INNER);

		List<Selection<?>> selectionItems = new ArrayList<Selection<?>>();

		selectionItems.add(compGroupJoin.get(CompanyGroup_.groupId).alias(
				getAlias(CompanyGroup_.groupId)));
		selectionItems.add(compGroupJoin.get(CompanyGroup_.groupName).alias(
				getAlias(CompanyGroup_.groupName)));
		selectionItems.add(compGroupJoin.get(CompanyGroup_.groupCode).alias(
				getAlias(CompanyGroup_.groupCode)));
		selectionItems.add(companyRoot.get(Company_.companyId).alias(
				getAlias(Company_.companyId)));
		selectionItems.add(companyRoot.get(Company_.companyCode).alias(
				getAlias(Company_.companyCode)));
		selectionItems.add(companyRoot.get(Company_.companyName).alias(
				getAlias(Company_.companyName)));
		criteriaQuery.multiselect(selectionItems).distinct(true);

		Join<Company, EmployeeRoleMapping> companyEmpJoin = companyRoot
				.join(Company_.employeeRoleMappings);

		Join<EmployeeRoleMapping, Employee> roleEmployeeJoin = companyEmpJoin
				.join(EmployeeRoleMapping_.employee);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(
				roleEmployeeJoin.get(Employee_.employeeId), employeeId));

		if (StringUtils.isNotBlank(conditionDTO.getCompanyName())) {
			restriction = cb.and(restriction, cb.like(
					cb.upper((companyRoot.get(Company_.companyName))),
					'%' + conditionDTO.getCompanyName().toUpperCase() + '%'));
		}
		if (StringUtils.isNotBlank(conditionDTO.getCompanyCode())) {
			restriction = cb.and(restriction, cb.like(
					cb.upper((companyRoot.get(Company_.companyCode))),
					'%' + conditionDTO.getCompanyCode().toUpperCase() + '%'));
		}
		if (StringUtils.isNotBlank(conditionDTO.getGroupCode())) {
			restriction = cb.and(restriction, cb.like(
					cb.upper((compGroupJoin.get(CompanyGroup_.groupCode))),
					'%' + conditionDTO.getGroupCode().toUpperCase() + '%'));
		}
		if (StringUtils.isNotBlank(conditionDTO.getGroupName())) {
			restriction = cb.and(restriction, cb.like(
					cb.upper((compGroupJoin.get(CompanyGroup_.groupName))),
					'%' + conditionDTO.getGroupName().toUpperCase() + '%'));
		}

		if (conditionDTO.isIncludeActiveStatus()) {
			if (conditionDTO.isIncludeInactiveCompany()) {

			} else {
				restriction = cb.and(restriction,
						cb.equal(companyRoot.get(Company_.active), true));
			}
		}

		if (conditionDTO.isIncludeDemoCompanyStatus()) {
			if (conditionDTO.isIncludeDemoCompany()) {
				restriction = cb
						.and(restriction, cb.equal(
								companyRoot.get(Company_.isDemoCompany), true));
			} else {
				restriction = cb.and(restriction, cb.equal(
						companyRoot.get(Company_.isDemoCompany), false));
			}
		}

		criteriaQuery.where(restriction);

		if (sortDTO != null) {

			Path<String> sortPath = getSortPathForAllCompany(sortDTO,
					companyRoot, compGroupJoin);
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

		TypedQuery<Tuple> companyTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			companyTypedQuery.setFirstResult(getStartPosition(pageDTO));
			companyTypedQuery.setMaxResults(pageDTO.getPageSize());
		}
		return companyTypedQuery.getResultList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.CompanyDAO#findAssignCompanyToUser(com.payasia.common
	 * .form.SortCondition, com.payasia.common.form.PageRequest, java.lang.Long)
	 */
	@Override
	public List<Tuple> findAssignGroupCompanyToUser(SortCondition sortDTO,
			PageRequest pageDTO, Long employeeId, Long groupID) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Tuple> criteriaQuery = cb.createQuery(Tuple.class);
		Root<Company> companyRoot = criteriaQuery.from(Company.class);

		Join<Company, CompanyGroup> compGroupJoin = companyRoot.join(
				Company_.companyGroup, JoinType.INNER);

		List<Selection<?>> selectionItems = new ArrayList<Selection<?>>();

		selectionItems.add(compGroupJoin.get(CompanyGroup_.groupId).alias(
				getAlias(CompanyGroup_.groupId)));
		selectionItems.add(compGroupJoin.get(CompanyGroup_.groupName).alias(
				getAlias(CompanyGroup_.groupName)));
		selectionItems.add(compGroupJoin.get(CompanyGroup_.groupCode).alias(
				getAlias(CompanyGroup_.groupCode)));
		selectionItems.add(companyRoot.get(Company_.companyId).alias(
				getAlias(Company_.companyId)));
		selectionItems.add(companyRoot.get(Company_.companyCode).alias(
				getAlias(Company_.companyCode)));
		selectionItems.add(companyRoot.get(Company_.companyName).alias(
				getAlias(Company_.companyName)));
		criteriaQuery.multiselect(selectionItems).distinct(true);

		Join<Company, EmployeeRoleMapping> companyEmpJoin = companyRoot
				.join(Company_.employeeRoleMappings);

		Join<EmployeeRoleMapping, Employee> roleEmployeeJoin = companyEmpJoin
				.join(EmployeeRoleMapping_.employee);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(
				roleEmployeeJoin.get(Employee_.employeeId), employeeId));
		restriction = cb.and(restriction,
				cb.equal(compGroupJoin.get(CompanyGroup_.groupId), groupID));

		criteriaQuery.where(restriction);

		if (sortDTO != null) {

			Path<String> sortPath = getSortPathForAllCompany(sortDTO,
					companyRoot, compGroupJoin);
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

		TypedQuery<Tuple> companyTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			companyTypedQuery.setFirstResult(getStartPosition(pageDTO));
			companyTypedQuery.setMaxResults(pageDTO.getPageSize());
		}
		return companyTypedQuery.getResultList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.CompanyDAO#getSortPathForAssignCompany(com.payasia.common
	 * .form.SortCondition, javax.persistence.criteria.Root,
	 * javax.persistence.criteria.Join)
	 */
	@Override
	public Path<String> getSortPathForAssignCompany(SortCondition sortDTO,
			Root<Company> companyRoot,
			Join<Company, CompanyGroup> companyGroupJoin) {

		List<String> companyIsColList = new ArrayList<String>();
		companyIsColList.add(SortConstants.SWITCH_COMPANY_COMPANY_NAME);
		companyIsColList.add(SortConstants.SWITCH_COMPANY_COMPANY_CODE);

		List<String> companyGroupIsColList = new ArrayList<String>();
		companyGroupIsColList.add(SortConstants.SWITCH_COMPANY_GROUP_NAME);
		companyGroupIsColList.add(SortConstants.SWITCH_COMPANY_GROUP_CODE);

		Path<String> sortPath = null;

		if (companyIsColList.contains(sortDTO.getColumnName())) {
			sortPath = companyRoot.get(colMap.get(Company.class
					+ sortDTO.getColumnName()));
		}
		if (companyGroupIsColList.contains(sortDTO.getColumnName())) {
			sortPath = companyGroupJoin.get(colMap.get(CompanyGroup.class
					+ sortDTO.getColumnName()));
		}
		return sortPath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.CompanyDAO#getCountForAssignCompany(java.lang.Long)
	 */
	@Override
	public int getCountForAssignCompany(Long empId,
			CompanyConditionDTO conditionDTO) {
		return findAssignCompanyToUser(null, null, empId, conditionDTO).size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.BaseDAO#getBaseEntity()
	 */
	@Override
	protected Object getBaseEntity() {
		Company company = new Company();
		return company;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.CompanyDAO#findById(long)
	 */
	@Override
	public Company findById(long companyId) {

		return super.findById(Company.class, companyId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.CompanyDAO#save(com.payasia.dao.bean.Company)
	 */
	@Override
	public Company save(Company company) {
		setdefaultPayslipFrequency(company);
		Company persistObj = company;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (Company) getBaseEntity();
			beanUtil.copyProperties(persistObj, company);
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
	 * @see com.payasia.dao.CompanyDAO#newTranSave(com.payasia.dao.bean.Company)
	 */
	@Override
	public void newTranSave(Company company) {
		super.save(company);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.CompanyDAO#update(com.payasia.dao.bean.Company)
	 */
	@Override
	public void update(Company company) {

		super.update(company);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.CompanyDAO#newTranUpdate(com.payasia.dao.bean.Company)
	 */
	@Override
	public void newTranUpdate(Company company) {

		super.update(company);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.CompanyDAO#delete(com.payasia.dao.bean.Company)
	 */
	@Override
	public void delete(Company company) {
		super.delete(company);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.CompanyDAO#newTranDelete(com.payasia.dao.bean.Company)
	 */
	@Override
	public void newTranDelete(Company company) {
		super.delete(company);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.CompanyDAO#findByCondition(com.payasia.common.dto.
	 * CompanyConditionDTO)
	 */
	@Override
	public Company findByCondition(CompanyConditionDTO conditionDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Company> criteriaQuery = cb.createQuery(Company.class);
		Root<Company> companyRoot = criteriaQuery.from(Company.class);

		criteriaQuery.select(companyRoot);

		Join<Company, CompanyGroup> companyGroupJoin = companyRoot
				.join(Company_.companyGroup);

		Predicate restriction = cb.conjunction();

		if (StringUtils.isNotBlank(conditionDTO.getCompanyName())) {
			restriction = cb.and(restriction, cb.equal(
					companyRoot.get(Company_.companyName),
					conditionDTO.getCompanyName()));
		}

		if (conditionDTO.getGroupId() != null) {
			restriction = cb.and(restriction, cb.equal(
					companyGroupJoin.get(CompanyGroup_.groupId),
					conditionDTO.getGroupId()));
		}

		if (StringUtils.isNotBlank(conditionDTO.getCompanyCode())) {
			restriction = cb.and(restriction, cb.equal(
					cb.upper(companyRoot.get(Company_.companyCode)),
					conditionDTO.getCompanyCode().toUpperCase()));
		}
		if (conditionDTO.getCompanyId() != null) {
			restriction = cb.and(restriction, cb.notEqual(
					companyRoot.get(Company_.companyId),
					conditionDTO.getCompanyId()));
		}

		criteriaQuery.where(restriction);

		TypedQuery<Company> companyTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<Company> companyList = companyTypedQuery.getResultList();
		if (companyList != null && !companyList.isEmpty()) {
			return companyList.get(0);
		}
		return null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.CompanyDAO#saveReturn(com.payasia.dao.bean.Company)
	 */
	@Override
	public Company saveReturn(Company company) {
		setdefaultPayslipFrequency(company);
		Company persistObj = company;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (Company) getBaseEntity();
			beanUtil.copyProperties(persistObj, company);
		} catch (IllegalAccessException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		} catch (InvocationTargetException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		}
		this.entityManagerFactory.persist(persistObj);

		return persistObj;
	}

	private Company setdefaultPayslipFrequency(Company company) {
		if (company.getPayslipFrequency() == null) {
			PayslipFrequency payslipFrequency = new PayslipFrequency();
			payslipFrequency.setPayslipFrequencyID(4l);
			company.setPayslipFrequency(payslipFrequency);
			return company;
		}
		return company;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.CompanyDAO#newTranSaveReturn(com.payasia.dao.bean.Company )
	 */
	@Override
	public Company newTranSaveReturn(Company company) {
		Company persistObj = company;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (Company) getBaseEntity();
			beanUtil.copyProperties(persistObj, company);
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
	 * @see com.payasia.dao.CompanyDAO#findByCondition(java.util.Map,
	 * java.util.List, java.util.List, java.lang.Long)
	 */
	@Override
	public List<Object[]> findByCondition(
			Map<String, DataImportKeyValueDTO> colMap, List<Long> formIds,
			List<ExcelExportFiltersForm> finalFilterList, Long employeeId,
			Map<String, DataImportKeyValueDTO> tableRecordInfo) {
		Map<String, String> paramValueMap = new HashMap<String, String>();
		ExcelExportQueryDTO selectDTO = createSelect(colMap, tableRecordInfo);
		String from = createFrom(colMap, formIds, selectDTO, tableRecordInfo);
		String where = createWhere(finalFilterList, employeeId,
				tableRecordInfo, paramValueMap);
		String queryString = selectDTO.getSelectQuery() + from + where;
		queryString = queryString.replace("?", "");
		queryString = queryString.replace("%", "");

		Query q = entityManagerFactory.createNativeQuery(queryString);
		q.setParameter("employeeId", employeeId);
		for (Map.Entry<String, String> entry : paramValueMap.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}

		return q.getResultList();

	}

	@Override
	public List<Object[]> findByConditionCompanyId(
			Map<String, DataImportKeyValueDTO> colMap, List<Long> formIds,
			List<ExcelExportFiltersForm> finalFilterList, Long companyId,
			Map<String, DataImportKeyValueDTO> tableRecordInfo) {
		Map<String, String> paramValueMap = new HashMap<String, String>();
		ExcelExportQueryDTO selectDTO = createSelect(colMap, tableRecordInfo);
		String from = createFromForCompany(colMap, formIds, selectDTO,
				tableRecordInfo);
		String where = createWhereForCompany(finalFilterList, companyId,
				tableRecordInfo, paramValueMap);
		String queryString = selectDTO.getSelectQuery() + from + where;
		queryString = queryString.replace("?", "");
		queryString = queryString.replace("%", "");

		Query q = entityManagerFactory.createNativeQuery(queryString);
		q.setParameter("companyId", companyId);
		for (Map.Entry<String, String> entry : paramValueMap.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}

		return q.getResultList();

	}

	@Override
	public List<Object[]> findByConditionGroupCompanyId(
			Map<String, DataImportKeyValueDTO> colMap, List<Long> formIds,
			List<ExcelExportFiltersForm> finalFilterList, Long companyId,
			Map<String, DataImportKeyValueDTO> tableRecordInfo) {
		Map<String, String> paramValueMap = new HashMap<String, String>();
		ExcelExportQueryDTO selectDTO = createSelectGroup(colMap,
				tableRecordInfo);
		String from = createFromForCompanyGroup(colMap, formIds, selectDTO,
				tableRecordInfo);
		String where = createWhereForCompanyGroup(finalFilterList, companyId,
				tableRecordInfo, paramValueMap);
		String queryString = selectDTO.getSelectQuery() + from + where;
		queryString = queryString.replace("?", "");
		queryString = queryString.replace("%", "");

		Query q = entityManagerFactory.createNativeQuery(queryString);
		q.setParameter("companyId", companyId);
		for (Map.Entry<String, String> entry : paramValueMap.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}

		return q.getResultList();

	}

	/**
	 * Creates the select. Group
	 * 
	 * @param colMap
	 *            the col map
	 * @return the excel export query dto
	 */
	public ExcelExportQueryDTO createSelectGroup(
			Map<String, DataImportKeyValueDTO> colMap,
			Map<String, DataImportKeyValueDTO> tableRecordInfo) {

		ExcelExportQueryDTO excelExportQueryDTO = new ExcelExportQueryDTO();
		List<CodeDescDTO> codeDescDTOs = new ArrayList<CodeDescDTO>();

		String selectQuery = "SELECT ";
		String tablePosition = null;
		Set<String> xlKeySet = colMap.keySet();

		int count = 1;

		for (Iterator<String> itr = xlKeySet.iterator(); itr.hasNext();) {
			if (count < xlKeySet.size()) {

				String key = (String) itr.next();
				DataImportKeyValueDTO valueDTO = (DataImportKeyValueDTO) colMap
						.get(key);
				String fieldName = valueDTO.getMethodName();
				key = key.replaceAll("\\.", "_");
				String codeKey = key;
				key = "[" + key + "]";

				if (valueDTO.isStatic()) {
					if ("GroupCode".equalsIgnoreCase(valueDTO.getMethodName())) {
						selectQuery = selectQuery + "companyGroup.Group_Code"
								+ " AS " + key + ", ";
					} else if ("GroupName".equalsIgnoreCase(valueDTO
							.getMethodName())) {
						selectQuery = selectQuery + "companyGroup.Group_Name"
								+ " AS " + key + ", ";
					} else if ("CountryName".equalsIgnoreCase(valueDTO
							.getMethodName())) {
						selectQuery = selectQuery + "country.Country_Name"
								+ " AS " + key + ", ";
					} else if ("Frequency".equalsIgnoreCase(valueDTO
							.getMethodName())) {
						selectQuery = selectQuery
								+ "payslipFrequency.Frequency" + " AS " + key
								+ ", ";
					} else if ("TimeZoneName".equalsIgnoreCase(valueDTO
							.getMethodName())) {
						selectQuery = selectQuery
								+ "timeZoneMaster.Time_Zone_Name" + " AS "
								+ key + ", ";
					} else {
						String methodName = valueDTO.getActualColName();

						selectQuery = selectQuery + "company." + methodName
								+ " AS " + key + ", ";
					}
				}

				else {

					String methodName = "Col_" + fieldName;

					if (valueDTO.getMethodName().equalsIgnoreCase(
							PayAsiaConstants.FIELD_NOT_EXISTS)) {

						selectQuery = selectQuery + " null"

								+ " AS " + key + ", ";

					} else if (!valueDTO.isChild()) {

						if ("codedesc"
								.equalsIgnoreCase(valueDTO.getFieldType())) {

							CodeDescDTO codeDescDTO = new CodeDescDTO();
							codeDescDTO.setMethodName(methodName);
							codeDescDTO.setFormId(valueDTO.getFormId());
							codeDescDTO.setChildVal(false);
							codeDescDTOs.add(codeDescDTO);

							selectQuery = selectQuery
									+ " concat(dynamicFormFieldRefValue"
									+ valueDTO.getFormId() + methodName
									+ ".Code ,'-', "
									+ " dynamicFormFieldRefValue"
									+ valueDTO.getFormId() + methodName
									+ ".Description) AS [" + codeKey
									+ "_CodeDescription]" + ", ";

						} else {
							selectQuery = selectQuery + " dynamicFormRecord"
									+ valueDTO.getFormId() + "." + methodName
									+ " AS " + key + ", ";
						}

					}

					else {

						if ("codedesc"
								.equalsIgnoreCase(valueDTO.getFieldType())) {
							CodeDescDTO codeDescDTO = new CodeDescDTO();
							codeDescDTO.setMethodName(methodName);
							codeDescDTO.setFormId(valueDTO.getFormId());
							codeDescDTO.setChildVal(true);
							codeDescDTO.setTablePosition(valueDTO
									.getTablePosition());
							codeDescDTOs.add(codeDescDTO);

							selectQuery = selectQuery
									+ " concat(dynamicFormFieldRefValueT"
									+ valueDTO.getFormId()
									+ valueDTO.getTablePosition() + methodName
									+ ".Code ,'-', "
									+ " dynamicFormFieldRefValueT"
									+ valueDTO.getFormId()
									+ valueDTO.getTablePosition() + methodName
									+ ".Description) AS [" + codeKey
									+ "Code Description]" + ", ";

						} else {

							selectQuery = selectQuery
									+ " dynamicFormTableRecord_"
									+ valueDTO.getFormId()
									+ valueDTO.getTablePosition() + "."
									+ methodName + " AS " + key + ", ";

						}

						tablePosition = valueDTO.getTablePosition();
						excelExportQueryDTO.setTablePosition(tablePosition);
						excelExportQueryDTO.setFormId(valueDTO.getFormId());

					}
				}

			} else {

				String key = (String) itr.next();
				DataImportKeyValueDTO valueDTO = (DataImportKeyValueDTO) colMap
						.get(key);
				String fieldName = valueDTO.getMethodName();
				key = key.replaceAll("\\.", "_");
				String codeKey = key;
				key = "[" + key + "]";

				if (valueDTO.isStatic()) {
					if ("GroupCode".equalsIgnoreCase(valueDTO.getMethodName())) {
						selectQuery = selectQuery + "companyGroup.Group_Code"
								+ " AS " + key + " ";
					} else if ("GroupName".equalsIgnoreCase(valueDTO
							.getMethodName())) {
						selectQuery = selectQuery + "companyGroup.Group_Name"
								+ " AS " + key + " ";
					} else if ("CountryName".equalsIgnoreCase(valueDTO
							.getMethodName())) {
						selectQuery = selectQuery + "country.Country_Name"
								+ " AS " + key + " ";
					} else if ("Frequency".equalsIgnoreCase(valueDTO
							.getMethodName())) {
						selectQuery = selectQuery
								+ "payslipFrequency.Frequency" + " AS " + key
								+ " ";
					} else if ("TimeZoneName".equalsIgnoreCase(valueDTO
							.getMethodName())) {
						selectQuery = selectQuery
								+ "timeZoneMaster.Time_Zone_Name" + " AS "
								+ key + " ";
					} else {
						String methodName = valueDTO.getActualColName();

						selectQuery = selectQuery + "company." + methodName
								+ " AS " + key + " ";
					}
				}

				else {

					String methodName = "Col_" + fieldName;

					if (valueDTO.getMethodName().equalsIgnoreCase(
							PayAsiaConstants.FIELD_NOT_EXISTS)) {

						selectQuery = selectQuery + " null"

								+ " AS " + key + " ";

					} else if (!valueDTO.isChild()) {
						if ("codedesc"
								.equalsIgnoreCase(valueDTO.getFieldType())) {

							CodeDescDTO codeDescDTO = new CodeDescDTO();
							codeDescDTO.setMethodName(methodName);
							codeDescDTO.setFormId(valueDTO.getFormId());
							codeDescDTO.setChildVal(false);
							codeDescDTOs.add(codeDescDTO);

							selectQuery = selectQuery
									+ " concat(dynamicFormFieldRefValue"
									+ valueDTO.getFormId() + methodName
									+ ".Code ,'-', "
									+ " dynamicFormFieldRefValue"
									+ valueDTO.getFormId() + methodName
									+ ".Description) AS [" + codeKey
									+ "_CodeDescription]" + " ";

						} else {
							selectQuery = selectQuery + " dynamicFormRecord"
									+ valueDTO.getFormId() + "." + methodName
									+ " AS " + key + " ";
						}

					} else {

						if ("codedesc"
								.equalsIgnoreCase(valueDTO.getFieldType())) {
							CodeDescDTO codeDescDTO = new CodeDescDTO();
							codeDescDTO.setMethodName(methodName);
							codeDescDTO.setFormId(valueDTO.getFormId());
							codeDescDTO.setChildVal(true);
							codeDescDTO.setTablePosition(valueDTO
									.getTablePosition());
							codeDescDTOs.add(codeDescDTO);

							selectQuery = selectQuery
									+ " concat(dynamicFormFieldRefValueT"
									+ valueDTO.getFormId()
									+ valueDTO.getTablePosition() + methodName
									+ ".Code ,'-', "
									+ " dynamicFormFieldRefValueT"
									+ valueDTO.getFormId()
									+ valueDTO.getTablePosition() + methodName
									+ ".Description) AS [" + codeKey
									+ "Code Description]" + " ";

						} else {

							selectQuery = selectQuery
									+ " dynamicFormTableRecord_"
									+ valueDTO.getFormId()
									+ valueDTO.getTablePosition() + "."
									+ methodName + " AS " + key + " ";

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

	/**
	 * Creates the select.
	 * 
	 * @param colMap
	 *            the col map
	 * @return the excel export query dto
	 */
	public ExcelExportQueryDTO createSelect(
			Map<String, DataImportKeyValueDTO> colMap,
			Map<String, DataImportKeyValueDTO> tableRecordInfo) {

		ExcelExportQueryDTO excelExportQueryDTO = new ExcelExportQueryDTO();
		List<CodeDescDTO> codeDescDTOs = new ArrayList<CodeDescDTO>();

		String selectQuery = "SELECT ";
		String tablePosition = null;
		Set<String> xlKeySet = colMap.keySet();

		int count = 1;

		for (Iterator<String> itr = xlKeySet.iterator(); itr.hasNext();) {
			if (count < xlKeySet.size()) {

				String key = (String) itr.next();
				DataImportKeyValueDTO valueDTO = (DataImportKeyValueDTO) colMap
						.get(key);
				String fieldName = valueDTO.getMethodName();
				key = key.replaceAll("\\.", "_");
				String codeKey = key;
				key = "[" + key + "]";

				if (valueDTO.isStatic()) {
					if ("GroupCode".equalsIgnoreCase(valueDTO.getMethodName())) {
						selectQuery = selectQuery + "companyGroup.Group_Code"
								+ " AS " + key + ", ";
					} else if ("GroupName".equalsIgnoreCase(valueDTO
							.getMethodName())) {
						selectQuery = selectQuery + "companyGroup.Group_Name"
								+ " AS " + key + ", ";
					} else if ("CountryName".equalsIgnoreCase(valueDTO
							.getMethodName())) {
						selectQuery = selectQuery + "country.Country_Name"
								+ " AS " + key + ", ";
					} else if ("Frequency".equalsIgnoreCase(valueDTO
							.getMethodName())) {
						selectQuery = selectQuery
								+ "payslipFrequency.Frequency" + " AS " + key
								+ ", ";
					} else if ("TimeZoneName".equalsIgnoreCase(valueDTO
							.getMethodName())) {
						selectQuery = selectQuery
								+ "timeZoneMaster.Time_Zone_Name" + " AS "
								+ key + ", ";
					} else {
						String methodName = valueDTO.getActualColName();

						selectQuery = selectQuery + "company." + methodName
								+ " AS " + key + ", ";
					}
				} else {

					String methodName = "Col_" + fieldName;

					if (!valueDTO.isChild()) {

						if ("codedesc"
								.equalsIgnoreCase(valueDTO.getFieldType())) {

							CodeDescDTO codeDescDTO = new CodeDescDTO();
							codeDescDTO.setMethodName(methodName);
							codeDescDTO.setFormId(valueDTO.getFormId());
							codeDescDTO.setChildVal(false);
							codeDescDTOs.add(codeDescDTO);

							selectQuery = selectQuery
									+ " dynamicFormFieldRefValue"
									+ valueDTO.getFormId() + methodName
									+ ".Code AS [" + codeKey + "_Code]" + ", ";

							selectQuery = selectQuery
									+ " dynamicFormFieldRefValue"
									+ valueDTO.getFormId() + methodName
									+ ".Description AS [" + codeKey
									+ "_Description]" + ", ";
						} else {
							selectQuery = selectQuery + " dynamicFormRecord"
									+ valueDTO.getFormId() + "." + methodName
									+ " AS " + key + ", ";
						}

					} else {

						if ("codedesc"
								.equalsIgnoreCase(valueDTO.getFieldType())) {
							CodeDescDTO codeDescDTO = new CodeDescDTO();
							codeDescDTO.setMethodName(methodName);
							codeDescDTO.setFormId(valueDTO.getFormId());
							codeDescDTO.setTablePosition(valueDTO
									.getTablePosition());
							codeDescDTO.setChildVal(true);
							codeDescDTOs.add(codeDescDTO);

							selectQuery = selectQuery
									+ " dynamicFormFieldRefValueT"
									+ valueDTO.getFormId()
									+ valueDTO.getTablePosition() + methodName
									+ ".Code AS [" + codeKey + "_Code]" + ", ";

							selectQuery = selectQuery
									+ " dynamicFormFieldRefValueT"
									+ valueDTO.getFormId()
									+ valueDTO.getTablePosition() + methodName
									+ ".Description AS [" + codeKey
									+ "_Description]" + ", ";

						} else {

							selectQuery = selectQuery
									+ " dynamicFormTableRecord_"
									+ valueDTO.getFormId()
									+ valueDTO.getTablePosition() + "."
									+ methodName + " AS " + key + ", ";

						}

						tablePosition = valueDTO.getTablePosition();
						excelExportQueryDTO.setTablePosition(tablePosition);
						excelExportQueryDTO.setFormId(valueDTO.getFormId());

					}
				}

			} else {

				String key = (String) itr.next();
				DataImportKeyValueDTO valueDTO = (DataImportKeyValueDTO) colMap
						.get(key);
				String fieldName = valueDTO.getMethodName();
				key = key.replaceAll("\\.", "_");
				String codeKey = key;
				key = "[" + key + "]";

				if (valueDTO.isStatic()) {
					if ("GroupCode".equalsIgnoreCase(valueDTO.getMethodName())) {
						selectQuery = selectQuery + "companyGroup.Group_Code"
								+ " AS " + key + " ";
					} else if ("GroupName".equalsIgnoreCase(valueDTO
							.getMethodName())) {
						selectQuery = selectQuery + "companyGroup.Group_Name"
								+ " AS " + key + " ";
					} else if ("CountryName".equalsIgnoreCase(valueDTO
							.getMethodName())) {
						selectQuery = selectQuery + "country.Country_Name"
								+ " AS " + key + " ";
					} else if ("Frequency".equalsIgnoreCase(valueDTO
							.getMethodName())) {
						selectQuery = selectQuery
								+ "payslipFrequency.Frequency" + " AS " + key
								+ " ";
					} else if ("TimeZoneName".equalsIgnoreCase(valueDTO
							.getMethodName())) {
						selectQuery = selectQuery
								+ "timeZoneMaster.Time_Zone_Name" + " AS "
								+ key + " ";
					} else {
						String methodName = valueDTO.getActualColName();

						selectQuery = selectQuery + "company." + methodName
								+ " AS " + key + " ";
					}
				} else {

					String methodName = "Col_" + fieldName;

					if (!valueDTO.isChild()) {
						if ("codedesc"
								.equalsIgnoreCase(valueDTO.getFieldType())) {

							CodeDescDTO codeDescDTO = new CodeDescDTO();
							codeDescDTO.setMethodName(methodName);
							codeDescDTO.setFormId(valueDTO.getFormId());
							codeDescDTO.setChildVal(false);
							codeDescDTOs.add(codeDescDTO);

							selectQuery = selectQuery
									+ " dynamicFormFieldRefValue"
									+ valueDTO.getFormId() + methodName
									+ ".Code AS [" + codeKey + "_Code]" + ", ";

							selectQuery = selectQuery
									+ " dynamicFormFieldRefValue"
									+ valueDTO.getFormId() + methodName
									+ ".Description AS [" + codeKey
									+ "_Description]" + " ";

						} else {
							selectQuery = selectQuery + " dynamicFormRecord"
									+ valueDTO.getFormId() + "." + methodName
									+ " AS " + key + " ";
						}

					} else {

						if ("codedesc"
								.equalsIgnoreCase(valueDTO.getFieldType())) {
							CodeDescDTO codeDescDTO = new CodeDescDTO();
							codeDescDTO.setMethodName(methodName);
							codeDescDTO.setTablePosition(valueDTO
									.getTablePosition());
							codeDescDTO.setFormId(valueDTO.getFormId());
							codeDescDTO.setChildVal(true);
							codeDescDTOs.add(codeDescDTO);

							selectQuery = selectQuery
									+ " dynamicFormFieldRefValueT"
									+ valueDTO.getFormId()
									+ valueDTO.getTablePosition() + methodName
									+ ".Code AS [" + codeKey + "_Code]" + ", ";

							selectQuery = selectQuery
									+ " dynamicFormFieldRefValueT"
									+ valueDTO.getFormId()
									+ valueDTO.getTablePosition() + methodName
									+ ".Description AS [" + codeKey
									+ "_Description]" + " ";

						} else {

							selectQuery = selectQuery
									+ " dynamicFormTableRecord_"
									+ valueDTO.getFormId()
									+ valueDTO.getTablePosition() + "."
									+ methodName + " AS " + key + " ";

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

	/**
	 * Creates the from.
	 * 
	 * @param colMap
	 *            the col map
	 * @param formIds
	 *            the form ids
	 * @param queryDTO
	 *            the query dto
	 * @return the string
	 */
	public String createFrom(Map<String, DataImportKeyValueDTO> colMap,
			List<Long> formIds, ExcelExportQueryDTO queryDTO,
			Map<String, DataImportKeyValueDTO> tableRecordInfo) {
		StringBuilder builder = new StringBuilder();
		builder = builder
				.append("FROM Company AS company LEFT OUTER JOIN Company_Group AS companyGroup ON (company.Group_ID = companyGroup.Group_ID) ");
		builder = builder
				.append("LEFT OUTER JOIN Payslip_Frequency AS payslipFrequency ON (company.Payslip_Frequency_ID = payslipFrequency.Payslip_Frequency_ID) ");
		builder = builder
				.append("LEFT OUTER JOIN Country_Master AS country ON (company.Country_ID = country.Country_ID) ");
		builder = builder
				.append("LEFT OUTER JOIN Time_Zone_Master AS timeZoneMaster ON (company.Time_Zone_ID = timeZoneMaster.Time_Zone_ID) ");
		builder = builder
				.append("INNER JOIN Employee_Role_Mapping AS rolemapping ON (company.Company_ID = rolemapping.Company_ID) ");

		for (Long formId : formIds) {
			builder = builder
					.append("LEFT OUTER JOIN Dynamic_Form_Record AS dynamicFormRecord");
			builder = builder.append(formId);
			builder = builder.append(" ");
			builder = builder
					.append("ON (company.Company_ID = dynamicFormRecord");
			builder = builder.append(formId);
			builder = builder.append(".Entity_Key) AND (dynamicFormRecord");
			builder = builder.append(formId);
			builder = builder.append(".Form_ID = ");
			builder = builder.append(formId + ")");

		}

		Integer noOfTables = tableRecordInfo.size();
		if (noOfTables <= 1) {

			if (queryDTO.getTablePosition() != null) {
				builder = builder
						.append("LEFT OUTER JOIN Dynamic_Form_Table_Record AS dynamicFormTableRecord_");
				builder = builder.append(queryDTO.getFormId());
				builder = builder.append(queryDTO.getTablePosition());
				builder = builder.append(" ON (dynamicFormRecord");
				builder = builder.append(queryDTO.getFormId());
				builder = builder.append(".Col_");
				builder = builder.append(queryDTO.getTablePosition());
				builder = builder.append(" = dynamicFormTableRecord_");
				builder = builder.append(queryDTO.getFormId());
				builder = builder.append(queryDTO.getTablePosition());
				builder = builder.append(".Dynamic_Form_Table_Record_ID ) ");

			}

		} else {

			for (Map.Entry<String, DataImportKeyValueDTO> entry : tableRecordInfo
					.entrySet()) {

				entry.getKey();
				entry.getValue();

				builder = builder
						.append(" LEFT OUTER JOIN Dynamic_Form_Table_Record AS dynamicFormTableRecord_");
				builder = builder.append(entry.getKey());
				builder = builder.append(" ON dynamicFormRecord");
				builder = builder.append(entry.getValue().getFormId());
				builder = builder.append(".Col_");
				builder = builder.append(entry.getValue().getTablePosition());
				builder = builder.append(" = dynamicFormTableRecord_");
				builder = builder.append(entry.getKey());
				builder = builder.append(".Dynamic_Form_Table_Record_ID  ");
				builder = builder
						.append("and CONVERT(date,dynamicFormTableRecord_");
				builder = builder
						.append(".Col_1) = (select max(CONVERT(date, dynamicFormTableRecord_max");
				builder = builder.append(entry.getKey());
				builder = builder
						.append(".Col_1)) from Dynamic_Form_Table_Record dynamicFormTableRecord_max");
				builder = builder.append(entry.getKey());
				builder = builder.append(" where dynamicFormTableRecord_max");
				builder = builder.append(entry.getKey());
				builder = builder
						.append(".Dynamic_Form_Table_Record_ID =dynamicFormTableRecord_");
				builder = builder.append(entry.getKey());
				builder = builder
						.append(".Dynamic_Form_Table_Record_ID  and CONVERT(date, dynamicFormTableRecord_max");
				builder = builder.append(entry.getKey());
				builder = builder.append(".Col_1) <= getdate())");

			}

		}

		for (CodeDescDTO codeDescDTO : queryDTO.getCodeDescList()) {

			if (codeDescDTO.isChildVal()) {

				builder = builder
						.append("LEFT OUTER JOIN Dynamic_Form_Field_Ref_Value AS dynamicFormFieldRefValueT");
				builder = builder.append(codeDescDTO.getFormId());
				builder = builder.append(codeDescDTO.getTablePosition());
				builder = builder.append(codeDescDTO.getMethodName());
				builder = builder.append(" ");

				if (noOfTables <= 1) {
					builder = builder.append(" ON (dynamicFormTableRecord_");
					builder = builder.append(codeDescDTO.getFormId());
					builder = builder.append(codeDescDTO.getTablePosition());
					builder = builder.append(".");
					builder = builder.append(codeDescDTO.getMethodName());
					builder = builder.append(" = dynamicFormFieldRefValueT");
					builder = builder.append(codeDescDTO.getFormId());
					builder = builder.append(codeDescDTO.getTablePosition());
					builder = builder.append(codeDescDTO.getMethodName());
					builder = builder.append(".Field_Ref_Value_ID ) ");

				} else {
					builder = builder.append(" ON (dynamicFormTableRecord_");
					builder = builder.append(codeDescDTO.getFormId());
					builder = builder.append(codeDescDTO.getTablePosition());
					builder = builder.append(".");
					builder = builder.append(codeDescDTO.getMethodName());
					builder = builder.append(" = dynamicFormFieldRefValueT");
					builder = builder.append(codeDescDTO.getFormId());
					builder = builder.append(codeDescDTO.getTablePosition());
					builder = builder.append(codeDescDTO.getMethodName());
					builder = builder.append(".Field_Ref_Value_ID ) ");

				}
			} else {
				builder = builder
						.append("LEFT OUTER JOIN Dynamic_Form_Field_Ref_Value AS dynamicFormFieldRefValue");
				builder = builder.append(codeDescDTO.getFormId());
				builder = builder.append(codeDescDTO.getMethodName());
				builder = builder.append(" ");
				builder = builder.append(" ON (dynamicFormRecord");
				builder = builder.append(codeDescDTO.getFormId());
				builder = builder.append(".");
				builder = builder.append(codeDescDTO.getMethodName());
				builder = builder.append(" = dynamicFormFieldRefValue");
				builder = builder.append(codeDescDTO.getFormId());
				builder = builder.append(codeDescDTO.getMethodName());
				builder = builder.append(".Field_Ref_Value_ID ) ");

			}

		}
		return builder.toString();

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
	 * @return the string
	 */
	public String createFromForCompany(
			Map<String, DataImportKeyValueDTO> colMap, List<Long> formIds,
			ExcelExportQueryDTO queryDTO,
			Map<String, DataImportKeyValueDTO> tableRecordInfo) {
		StringBuilder builder = new StringBuilder(
				"FROM Company AS company LEFT OUTER JOIN Company_Group AS companyGroup ON (company.Group_ID = companyGroup.Group_ID) ");
		builder.append(
				"LEFT OUTER JOIN Payslip_Frequency AS payslipFrequency ON (company.Payslip_Frequency_ID = payslipFrequency.Payslip_Frequency_ID) ");
		builder.append("LEFT OUTER JOIN Country_Master AS country ON (company.Country_ID = country.Country_ID) ");
		builder.append(
				"LEFT OUTER JOIN Time_Zone_Master AS timeZoneMaster ON (company.Time_Zone_ID = timeZoneMaster.Time_Zone_ID) ");

		for (Long formId : formIds) {
			builder.append("LEFT OUTER JOIN Dynamic_Form_Record AS dynamicFormRecord");
			builder.append(formId);
			builder.append(" ");
			builder.append("ON (company.Company_ID = dynamicFormRecord");
			builder.append(formId);
			builder.append(".Entity_Key) AND (dynamicFormRecord");
			builder.append(formId);
			builder.append(".Form_ID = ");
			builder.append(formId);
			builder.append(" ) ");

		}

		Integer noOfTables = tableRecordInfo.size();
		if (noOfTables <= 1) {

			if (queryDTO.getTablePosition() != null) {
				builder.append("LEFT OUTER JOIN Dynamic_Form_Table_Record AS dynamicFormTableRecord_");
				builder.append(queryDTO.getFormId());
				builder.append(queryDTO.getTablePosition());
				builder.append(" ON (dynamicFormRecord");
				builder.append(queryDTO.getFormId());
				builder.append(".Col_");
				builder.append(queryDTO.getTablePosition());
				builder.append(" = dynamicFormTableRecord_");
				builder.append(queryDTO.getFormId());
				builder.append(queryDTO.getTablePosition());
				builder.append(".Dynamic_Form_Table_Record_ID ) ");
			}

		} else {

			for (Map.Entry<String, DataImportKeyValueDTO> entry : tableRecordInfo
					.entrySet()) {

				entry.getKey();
				entry.getValue();
				builder.append(" LEFT OUTER JOIN Dynamic_Form_Table_Record AS dynamicFormTableRecord_");
				builder.append(entry.getKey());
				builder.append(" ON dynamicFormRecord");
				builder.append(entry.getValue().getFormId());
				builder.append(".Col_");
				builder.append(entry.getValue().getTablePosition());
				builder.append(" = dynamicFormTableRecord_");
				builder.append(entry.getKey());
				builder.append(".Dynamic_Form_Table_Record_ID  ");
				builder.append("and CONVERT(date,dynamicFormTableRecord_");
				builder.append(entry.getKey());
				builder.append(".Col_1) = (select max(CONVERT(date, dynamicFormTableRecord_max");
				builder.append(entry.getKey());
				builder.append(".Col_1)) from Dynamic_Form_Table_Record dynamicFormTableRecord_max");
				builder.append(entry.getKey());
				builder.append(" where dynamicFormTableRecord_max");
				builder.append(entry.getKey());
				builder.append(".Dynamic_Form_Table_Record_ID =dynamicFormTableRecord_");
				builder.append(entry.getKey());
				builder.append(".Dynamic_Form_Table_Record_ID  and CONVERT(date, dynamicFormTableRecord_max");
				builder.append(entry.getKey());
				builder.append(".Col_1) <= getdate())");

			}

		}

		for (CodeDescDTO codeDescDTO : queryDTO.getCodeDescList()) {

			if (codeDescDTO.isChildVal()) {
				builder.append("LEFT OUTER JOIN Dynamic_Form_Field_Ref_Value AS dynamicFormFieldRefValueT");
				builder.append(codeDescDTO.getFormId());
				builder.append(codeDescDTO.getTablePosition());
				builder.append(codeDescDTO.getMethodName());
				builder.append(" ");

				if (noOfTables <= 1) {
					builder.append(" ON (dynamicFormTableRecord_");
					builder.append(codeDescDTO.getFormId());
					builder.append(codeDescDTO.getTablePosition());
					builder.append(".");
					builder.append(codeDescDTO.getMethodName());
					builder.append(" = dynamicFormFieldRefValueT");
					builder.append(codeDescDTO.getFormId());
					builder.append(codeDescDTO.getTablePosition());
					builder.append(codeDescDTO.getMethodName());
					builder.append(".Field_Ref_Value_ID ) ");

				} else {
					builder.append(" ON (dynamicFormTableRecord_");
					builder.append(codeDescDTO.getFormId());
					builder.append(codeDescDTO.getTablePosition());
					builder.append(".");
					builder.append(codeDescDTO.getMethodName());
					builder.append(" = dynamicFormFieldRefValueT");
					builder.append(codeDescDTO.getFormId());
					builder.append(codeDescDTO.getTablePosition());
					builder.append(codeDescDTO.getMethodName());
					builder.append(".Field_Ref_Value_ID ) ");

				}
			} else {
				builder.append("LEFT OUTER JOIN Dynamic_Form_Field_Ref_Value AS dynamicFormFieldRefValue");
				builder.append(codeDescDTO.getFormId());
				builder.append(codeDescDTO.getMethodName());
				builder.append(" ");
				builder.append(" ON (dynamicFormRecord");
				builder.append(codeDescDTO.getFormId());
				builder.append(".");
				builder.append(codeDescDTO.getMethodName());
				builder.append(" = dynamicFormFieldRefValue");
				builder.append(codeDescDTO.getFormId());
				builder.append(codeDescDTO.getMethodName());
				builder.append(".Field_Ref_Value_ID ) ");
			}

		}

		return builder.toString();

	}

	/**
	 * Creates the from.Group
	 * 
	 * @param colMap
	 *            the col map
	 * @param formIds
	 *            the form ids
	 * @param queryDTO
	 *            the query dto
	 * @return the string
	 */
	public String createFromForCompanyGroup(
			Map<String, DataImportKeyValueDTO> colMap, List<Long> formIds,
			ExcelExportQueryDTO queryDTO,
			Map<String, DataImportKeyValueDTO> tableRecordInfo) {
		String from = "FROM Company AS company LEFT OUTER JOIN Company_Group AS companyGroup ON (company.Group_ID = companyGroup.Group_ID) ";
		from = from
				+ "LEFT OUTER JOIN Payslip_Frequency AS payslipFrequency ON (company.Payslip_Frequency_ID = payslipFrequency.Payslip_Frequency_ID) ";
		from = from
				+ "LEFT OUTER JOIN Country_Master AS country ON (company.Country_ID = country.Country_ID) ";
		from = from
				+ "LEFT OUTER JOIN Time_Zone_Master AS timeZoneMaster ON (company.Time_Zone_ID = timeZoneMaster.Time_Zone_ID) ";

		for (Long formId : formIds) {

			from = from
					+ "LEFT OUTER JOIN Dynamic_Form_Record AS dynamicFormRecord"
					+ formId + " ";
			from = from + "ON (company.Company_ID = dynamicFormRecord" + formId
					+ ".Entity_Key) AND (dynamicFormRecord" + formId
					+ ".Form_ID = " + formId + " ) ";

		}

		Integer noOfTables = tableRecordInfo.size();
		if (noOfTables <= 1) {

			if (queryDTO.getTablePosition() != null) {
				from = from
						+ "LEFT OUTER JOIN Dynamic_Form_Table_Record AS dynamicFormTableRecord_"
						+ queryDTO.getFormId() + queryDTO.getTablePosition();
				from = from + " ON (dynamicFormRecord" + queryDTO.getFormId()
						+ ".Col_" + queryDTO.getTablePosition()
						+ " = dynamicFormTableRecord_" + queryDTO.getFormId()
						+ queryDTO.getTablePosition()
						+ ".Dynamic_Form_Table_Record_ID ) ";

			}

		} else {

			for (Map.Entry<String, DataImportKeyValueDTO> entry : tableRecordInfo
					.entrySet()) {

				entry.getKey();
				entry.getValue();

				from = from
						+ " LEFT OUTER JOIN Dynamic_Form_Table_Record AS dynamicFormTableRecord_"
						+ entry.getKey();
				from = from
						+ " ON dynamicFormRecord"
						+ entry.getValue().getFormId()
						+ ".Col_"
						+ entry.getValue().getTablePosition()
						+ " = dynamicFormTableRecord_"
						+ entry.getKey()
						+ ".Dynamic_Form_Table_Record_ID  "
						+ "and CONVERT(date,dynamicFormTableRecord_"
						+ entry.getKey()
						+ ".Col_1) = (select max(CONVERT(date, dynamicFormTableRecord_max"
						+ entry.getKey()
						+ ".Col_1)) from Dynamic_Form_Table_Record dynamicFormTableRecord_max"
						+ entry.getKey()
						+ " where dynamicFormTableRecord_max"
						+ entry.getKey()
						+ ".Dynamic_Form_Table_Record_ID =dynamicFormTableRecord_"
						+ entry.getKey()
						+ ".Dynamic_Form_Table_Record_ID  and CONVERT(date, dynamicFormTableRecord_max"
						+ entry.getKey() + ".Col_1) <= getdate())";

			}

		}

		for (CodeDescDTO codeDescDTO : queryDTO.getCodeDescList()) {

			if (codeDescDTO.isChildVal()) {
				from = from
						+ "LEFT OUTER JOIN Dynamic_Form_Field_Ref_Value AS dynamicFormFieldRefValueT"
						+ codeDescDTO.getFormId()
						+ codeDescDTO.getTablePosition()
						+ codeDescDTO.getMethodName() + " ";
				if (noOfTables <= 1) {

					from = from + " ON (dynamicFormTableRecord_"
							+ codeDescDTO.getFormId()
							+ codeDescDTO.getTablePosition() + "."
							+ codeDescDTO.getMethodName()
							+ " = dynamicFormFieldRefValueT"
							+ codeDescDTO.getFormId()
							+ codeDescDTO.getTablePosition()
							+ codeDescDTO.getMethodName()
							+ ".Field_Ref_Value_ID ) ";

				} else {
					from = from + " ON (dynamicFormTableRecord_"
							+ codeDescDTO.getFormId()
							+ codeDescDTO.getTablePosition() + "."
							+ codeDescDTO.getMethodName()
							+ " = dynamicFormFieldRefValueT"
							+ codeDescDTO.getFormId()
							+ codeDescDTO.getTablePosition()
							+ codeDescDTO.getMethodName()
							+ ".Field_Ref_Value_ID ) ";

				}
			} else {
				from = from
						+ "LEFT OUTER JOIN Dynamic_Form_Field_Ref_Value AS dynamicFormFieldRefValue"
						+ codeDescDTO.getFormId() + codeDescDTO.getMethodName()
						+ " ";
				from = from + " ON (dynamicFormRecord"
						+ codeDescDTO.getFormId() + "."
						+ codeDescDTO.getMethodName()
						+ " = dynamicFormFieldRefValue"
						+ codeDescDTO.getFormId() + codeDescDTO.getMethodName()
						+ ".Field_Ref_Value_ID ) ";
			}

		}

		return from;

	}

	/**
	 * Creates the where.
	 * 
	 * @param finalFilterList
	 *            the final filter list
	 * @param employeeId
	 *            the employee id
	 * @return the string
	 */
	public String createWhereForCompany(
			List<ExcelExportFiltersForm> finalFilterList, Long companyId,
			Map<String, DataImportKeyValueDTO> tableRecordInfo,
			Map<String, String> paramValueMap) {
		Integer noOfTables = tableRecordInfo.size();
		String where = " WHERE company.Company_ID =:companyId  ";
		if (!finalFilterList.isEmpty()) {
			where = where + " AND ";
			int count = 1;
			for (ExcelExportFiltersForm excelExportFiltersForm : finalFilterList) {

				if (excelExportFiltersForm.getDataImportKeyValueDTO()
						.isStatic()) {
					if ("GroupCode".equalsIgnoreCase(excelExportFiltersForm
							.getDataImportKeyValueDTO().getMethodName())) {
						where = where
								+ " "
								+ excelExportFiltersForm.getOpenBracket()
								+ " companyGroup."
								+ excelExportFiltersForm
										.getDataImportKeyValueDTO()
										.getActualColName() + " ";
					} else if ("GroupName"
							.equalsIgnoreCase(excelExportFiltersForm
									.getDataImportKeyValueDTO().getMethodName())) {
						where = where
								+ " "
								+ excelExportFiltersForm.getOpenBracket()
								+ " companyGroup."
								+ excelExportFiltersForm
										.getDataImportKeyValueDTO()
										.getActualColName() + " ";
					} else if ("CountryName"
							.equalsIgnoreCase(excelExportFiltersForm
									.getDataImportKeyValueDTO().getMethodName())) {
						where = where
								+ " "
								+ excelExportFiltersForm.getOpenBracket()
								+ " country."
								+ excelExportFiltersForm
										.getDataImportKeyValueDTO()
										.getActualColName() + " ";

					} else if ("Frequency"
							.equalsIgnoreCase(excelExportFiltersForm
									.getDataImportKeyValueDTO().getMethodName())) {
						where = where
								+ " "
								+ excelExportFiltersForm.getOpenBracket()
								+ " payslipFrequency."
								+ excelExportFiltersForm
										.getDataImportKeyValueDTO()
										.getActualColName() + " ";

					} else if ("TimeZoneName"
							.equalsIgnoreCase(excelExportFiltersForm
									.getDataImportKeyValueDTO().getMethodName())) {
						where = where
								+ " "
								+ excelExportFiltersForm.getOpenBracket()
								+ " timeZoneMaster."
								+ excelExportFiltersForm
										.getDataImportKeyValueDTO()
										.getActualColName() + " ";

					} else {
						where = where
								+ " "
								+ excelExportFiltersForm.getOpenBracket()
								+ " company."
								+ excelExportFiltersForm
										.getDataImportKeyValueDTO()
										.getActualColName() + " ";
					}
				} else {

					if (!excelExportFiltersForm.getDataImportKeyValueDTO()
							.isChild()) {

						if ("numeric".equalsIgnoreCase(excelExportFiltersForm
								.getDataImportKeyValueDTO().getFieldType())) {
							where = where
									+ " "
									+ excelExportFiltersForm.getOpenBracket()
									+ " dynamicFormRecord"
									+ excelExportFiltersForm
											.getDataImportKeyValueDTO()
											.getFormId()
									+ ".Col_"
									+ excelExportFiltersForm
											.getDataImportKeyValueDTO()
											.getMethodName() + "  ";
						} else if ("date"
								.equalsIgnoreCase(excelExportFiltersForm
										.getDataImportKeyValueDTO()
										.getFieldType())) {

							where = where
									+ " "
									+ excelExportFiltersForm.getOpenBracket()
									+ "convert(datetime, dynamicFormRecord"
									+ excelExportFiltersForm
											.getDataImportKeyValueDTO()
											.getFormId()
									+ ".Col_"
									+ excelExportFiltersForm
											.getDataImportKeyValueDTO()
											.getMethodName() + ") ";

						} else if ("codedesc"
								.equalsIgnoreCase(excelExportFiltersForm
										.getDataImportKeyValueDTO()
										.getFieldType())) {

							where = where
									+ " "
									+ excelExportFiltersForm.getOpenBracket()
									+ " dynamicFormFieldRefValue"
									+ excelExportFiltersForm
											.getDataImportKeyValueDTO()
											.getFormId()
									+ "Col_"
									+ excelExportFiltersForm
											.getDataImportKeyValueDTO()
											.getMethodName() + ".Code ";

						} else {
							where = where
									+ " "
									+ excelExportFiltersForm.getOpenBracket()
									+ " dynamicFormRecord"
									+ excelExportFiltersForm
											.getDataImportKeyValueDTO()
											.getFormId()
									+ ".Col_"
									+ excelExportFiltersForm
											.getDataImportKeyValueDTO()
											.getMethodName() + " ";
						}
					} else {

						if ("numeric".equalsIgnoreCase(excelExportFiltersForm
								.getDataImportKeyValueDTO().getFieldType())) {
							if (noOfTables <= 1) {

								where = where
										+ " "
										+ excelExportFiltersForm
												.getOpenBracket()
										+ " dynamicFormTableRecord_"
										+ excelExportFiltersForm
												.getDataImportKeyValueDTO()
												.getFormId()
										+ excelExportFiltersForm
												.getDataImportKeyValueDTO()
												.getTablePosition()
										+ ".Col_"
										+ excelExportFiltersForm
												.getDataImportKeyValueDTO()
												.getMethodName() + " ";

							} else {
								where = where
										+ " "
										+ excelExportFiltersForm
												.getOpenBracket()
										+ " dynamicFormTableRecord_"
										+ excelExportFiltersForm
												.getDataImportKeyValueDTO()
												.getFormId()
										+ excelExportFiltersForm
												.getDataImportKeyValueDTO()
												.getTablePosition()
										+ ".Col_"
										+ excelExportFiltersForm
												.getDataImportKeyValueDTO()
												.getMethodName() + " ";

							}
						} else if ("date"
								.equalsIgnoreCase(excelExportFiltersForm
										.getDataImportKeyValueDTO()
										.getFieldType())) {

							where = where
									+ " "
									+ excelExportFiltersForm.getOpenBracket()
									+ "convert(datetime, ltrim(dynamicFormTableRecord_"
									+ excelExportFiltersForm
											.getDataImportKeyValueDTO()
											.getFormId()
									+ excelExportFiltersForm
											.getDataImportKeyValueDTO()
											.getTablePosition()
									+ ".Col_"
									+ excelExportFiltersForm
											.getDataImportKeyValueDTO()
											.getMethodName() + ")) ";

						} else if ("codedesc"
								.equalsIgnoreCase(excelExportFiltersForm
										.getDataImportKeyValueDTO()
										.getFieldType())) {
							where = where
									+ " "
									+ excelExportFiltersForm.getOpenBracket()
									+ " dynamicFormFieldRefValueT"
									+ excelExportFiltersForm
											.getDataImportKeyValueDTO()
											.getFormId()
									+ excelExportFiltersForm
											.getDataImportKeyValueDTO()
											.getTablePosition()
									+ "Col_"
									+ excelExportFiltersForm
											.getDataImportKeyValueDTO()
											.getMethodName() + ".Code ";
						} else {

							where = where
									+ " "
									+ excelExportFiltersForm.getOpenBracket()
									+ " dynamicFormTableRecord_"
									+ excelExportFiltersForm
											.getDataImportKeyValueDTO()
											.getFormId()
									+ excelExportFiltersForm
											.getDataImportKeyValueDTO()
											.getTablePosition()
									+ ".Col_"
									+ excelExportFiltersForm
											.getDataImportKeyValueDTO()
											.getMethodName() + " ";

						}
					}

				}

				if (count < finalFilterList.size()) {
					paramValueMap.put("param" + count,
							excelExportFiltersForm.getValue());
					if ("numeric".equalsIgnoreCase(excelExportFiltersForm
							.getDataImportKeyValueDTO().getFieldType())) {
						where = where
								+ excelExportFiltersForm.getEqualityOperator()
								+ ":param" + count + " "
								+ excelExportFiltersForm.getCloseBracket()
								+ " "
								+ excelExportFiltersForm.getLogicalOperator()
								+ " ";
					} else {
						where = where
								+ excelExportFiltersForm.getEqualityOperator()
								+ ":param" + count + " "
								+ excelExportFiltersForm.getCloseBracket()
								+ " "
								+ excelExportFiltersForm.getLogicalOperator()
								+ " ";
					}

				} else {
					paramValueMap.put("param" + count,
							excelExportFiltersForm.getValue());
					if ("numeric".equalsIgnoreCase(excelExportFiltersForm
							.getDataImportKeyValueDTO().getFieldType())) {
						where = where
								+ excelExportFiltersForm.getEqualityOperator()
								+ ":param" + count + " "
								+ excelExportFiltersForm.getCloseBracket()
								+ " ";
					} else {
						where = where
								+ excelExportFiltersForm.getEqualityOperator()
								+ ":param" + count + " "
								+ excelExportFiltersForm.getCloseBracket()
								+ " ";
					}

				}

				count++;
			}

		}
		return where + " ORDER BY company.Company_ID DESC";

	}

	/**
	 * Creates the where.Group
	 * 
	 * @param finalFilterList
	 *            the final filter list
	 * @param employeeId
	 *            the employee id
	 * @return the string
	 */
	public String createWhereForCompanyGroup(
			List<ExcelExportFiltersForm> finalFilterList, Long companyId,
			Map<String, DataImportKeyValueDTO> tableRecordInfo,
			Map<String, String> paramValueMap) {
		String where = " WHERE company.Company_ID =:companyId ";
		if (!finalFilterList.isEmpty()) {
			where = where + " AND ";
			int count = 1;
			for (ExcelExportFiltersForm excelExportFiltersForm : finalFilterList) {

				if (excelExportFiltersForm.getDataImportKeyValueDTO()
						.isStatic()) {
					if ("GroupCode".equalsIgnoreCase(excelExportFiltersForm
							.getDataImportKeyValueDTO().getMethodName())) {
						where = where
								+ " "
								+ excelExportFiltersForm.getOpenBracket()
								+ " companyGroup."
								+ excelExportFiltersForm
										.getDataImportKeyValueDTO()
										.getActualColName() + " ";
					} else if ("GroupName"
							.equalsIgnoreCase(excelExportFiltersForm
									.getDataImportKeyValueDTO().getMethodName())) {
						where = where
								+ " "
								+ excelExportFiltersForm.getOpenBracket()
								+ " companyGroup."
								+ excelExportFiltersForm
										.getDataImportKeyValueDTO()
										.getActualColName() + " ";
					} else if ("CountryName"
							.equalsIgnoreCase(excelExportFiltersForm
									.getDataImportKeyValueDTO().getMethodName())) {
						where = where
								+ " "
								+ excelExportFiltersForm.getOpenBracket()
								+ " country."
								+ excelExportFiltersForm
										.getDataImportKeyValueDTO()
										.getActualColName() + " ";

					} else if ("Frequency"
							.equalsIgnoreCase(excelExportFiltersForm
									.getDataImportKeyValueDTO().getMethodName())) {
						where = where
								+ " "
								+ excelExportFiltersForm.getOpenBracket()
								+ " payslipFrequency."
								+ excelExportFiltersForm
										.getDataImportKeyValueDTO()
										.getActualColName() + " ";

					} else if ("TimeZoneName"
							.equalsIgnoreCase(excelExportFiltersForm
									.getDataImportKeyValueDTO().getMethodName())) {
						where = where
								+ " "
								+ excelExportFiltersForm.getOpenBracket()
								+ " timeZoneMaster."
								+ excelExportFiltersForm
										.getDataImportKeyValueDTO()
										.getActualColName() + " ";

					} else {
						where = where
								+ " "
								+ excelExportFiltersForm.getOpenBracket()
								+ " company."
								+ excelExportFiltersForm
										.getDataImportKeyValueDTO()
										.getActualColName() + " ";
					}
				} else {

					if (!excelExportFiltersForm.getDataImportKeyValueDTO()
							.isChild()) {

						if ("numeric".equalsIgnoreCase(excelExportFiltersForm
								.getDataImportKeyValueDTO().getFieldType())) {
							where = where
									+ " "
									+ excelExportFiltersForm.getOpenBracket()
									+ " dynamicFormRecord"
									+ excelExportFiltersForm
											.getDataImportKeyValueDTO()
											.getFormId()
									+ ".Col_"
									+ excelExportFiltersForm
											.getDataImportKeyValueDTO()
											.getMethodName() + "  ";
						} else if ("date"
								.equalsIgnoreCase(excelExportFiltersForm
										.getDataImportKeyValueDTO()
										.getFieldType())) {

							where = where
									+ " "
									+ excelExportFiltersForm.getOpenBracket()
									+ "convert(datetime, dynamicFormRecord"
									+ excelExportFiltersForm
											.getDataImportKeyValueDTO()
											.getFormId()
									+ ".Col_"
									+ excelExportFiltersForm
											.getDataImportKeyValueDTO()
											.getMethodName() + ") ";

						} else if ("codedesc"
								.equalsIgnoreCase(excelExportFiltersForm
										.getDataImportKeyValueDTO()
										.getFieldType())) {

							where = where
									+ " "
									+ excelExportFiltersForm.getOpenBracket()
									+ " dynamicFormFieldRefValue"
									+ excelExportFiltersForm
											.getDataImportKeyValueDTO()
											.getFormId()
									+ "Col_"
									+ excelExportFiltersForm
											.getDataImportKeyValueDTO()
											.getMethodName() + ".Code ";

						} else {
							where = where
									+ " "
									+ excelExportFiltersForm.getOpenBracket()
									+ " dynamicFormRecord"
									+ excelExportFiltersForm
											.getDataImportKeyValueDTO()
											.getFormId()
									+ ".Col_"
									+ excelExportFiltersForm
											.getDataImportKeyValueDTO()
											.getMethodName() + " ";
						}
					} else {

						if ("numeric".equalsIgnoreCase(excelExportFiltersForm
								.getDataImportKeyValueDTO().getFieldType())) {

							where = where
									+ " "
									+ excelExportFiltersForm.getOpenBracket()
									+ " dynamicFormTableRecord_"
									+ excelExportFiltersForm
											.getDataImportKeyValueDTO()
											.getFormId()
									+ excelExportFiltersForm
											.getDataImportKeyValueDTO()
											.getTablePosition()
									+ ".Col_"
									+ excelExportFiltersForm
											.getDataImportKeyValueDTO()
											.getMethodName() + "  ";

						} else if ("date"
								.equalsIgnoreCase(excelExportFiltersForm
										.getDataImportKeyValueDTO()
										.getFieldType())) {

							where = where
									+ " "
									+ excelExportFiltersForm.getOpenBracket()
									+ "convert(datetime, ltrim(dynamicFormTableRecord_"
									+ excelExportFiltersForm
											.getDataImportKeyValueDTO()
											.getFormId()
									+ excelExportFiltersForm
											.getDataImportKeyValueDTO()
											.getTablePosition()
									+ ".Col_"
									+ excelExportFiltersForm
											.getDataImportKeyValueDTO()
											.getMethodName() + ")) ";

						} else if ("codedesc"
								.equalsIgnoreCase(excelExportFiltersForm
										.getDataImportKeyValueDTO()
										.getFieldType())) {
							where = where
									+ " "
									+ excelExportFiltersForm.getOpenBracket()
									+ " dynamicFormFieldRefValueT"
									+ excelExportFiltersForm
											.getDataImportKeyValueDTO()
											.getFormId()
									+ excelExportFiltersForm
											.getDataImportKeyValueDTO()
											.getTablePosition()
									+ "Col_"
									+ excelExportFiltersForm
											.getDataImportKeyValueDTO()
											.getMethodName() + ".Code ";
						} else {

							where = where
									+ " "
									+ excelExportFiltersForm.getOpenBracket()
									+ " dynamicFormTableRecord_"
									+ excelExportFiltersForm
											.getDataImportKeyValueDTO()
											.getFormId()
									+ excelExportFiltersForm
											.getDataImportKeyValueDTO()
											.getTablePosition()
									+ ".Col_"
									+ excelExportFiltersForm
											.getDataImportKeyValueDTO()
											.getMethodName() + " ";

						}
					}

				}

				if (count < finalFilterList.size()) {
					paramValueMap.put("param" + count,
							excelExportFiltersForm.getValue());
					if ("numeric".equalsIgnoreCase(excelExportFiltersForm
							.getDataImportKeyValueDTO().getFieldType())) {
						where = where
								+ excelExportFiltersForm.getEqualityOperator()
								+ ":param" + count + " "
								+ excelExportFiltersForm.getCloseBracket()
								+ " "
								+ excelExportFiltersForm.getLogicalOperator()
								+ " ";
					} else {
						where = where
								+ excelExportFiltersForm.getEqualityOperator()
								+ ":param" + count + " "
								+ excelExportFiltersForm.getCloseBracket()
								+ " "
								+ excelExportFiltersForm.getLogicalOperator()
								+ " ";
					}

				} else {
					paramValueMap.put("param" + count,
							excelExportFiltersForm.getValue());
					if ("numeric".equalsIgnoreCase(excelExportFiltersForm
							.getDataImportKeyValueDTO().getFieldType())) {
						where = where
								+ excelExportFiltersForm.getEqualityOperator()
								+ ":param" + count + " "
								+ excelExportFiltersForm.getCloseBracket()
								+ " ";
					} else {
						where = where
								+ excelExportFiltersForm.getEqualityOperator()
								+ ":param" + count + " "
								+ excelExportFiltersForm.getCloseBracket()
								+ " ";
					}

				}

				count++;
			}

		}
		return where + " ORDER BY company.Company_ID DESC";

	}

	/**
	 * Creates the where.
	 * 
	 * @param finalFilterList
	 *            the final filter list
	 * @param employeeId
	 *            the employee id
	 * @return the string
	 */
	public String createWhere(List<ExcelExportFiltersForm> finalFilterList,
			Long employeeId,
			Map<String, DataImportKeyValueDTO> tableRecordInfo,
			Map<String, String> paramValueMap) {
		String where = " WHERE rolemapping.Employee_ID =:employeeId ";
		if (!finalFilterList.isEmpty()) {
			where = where + " AND ";
			int count = 1;
			for (ExcelExportFiltersForm excelExportFiltersForm : finalFilterList) {

				if (excelExportFiltersForm.getDataImportKeyValueDTO()
						.isStatic()) {
					if ("GroupCode".equalsIgnoreCase(excelExportFiltersForm
							.getDataImportKeyValueDTO().getMethodName())) {
						where = where
								+ " "
								+ excelExportFiltersForm.getOpenBracket()
								+ " companyGroup."
								+ excelExportFiltersForm
										.getDataImportKeyValueDTO()
										.getActualColName() + " ";
					} else if ("GroupName"
							.equalsIgnoreCase(excelExportFiltersForm
									.getDataImportKeyValueDTO().getMethodName())) {
						where = where
								+ " "
								+ excelExportFiltersForm.getOpenBracket()
								+ " companyGroup."
								+ excelExportFiltersForm
										.getDataImportKeyValueDTO()
										.getActualColName() + " ";
					} else if ("CountryName"
							.equalsIgnoreCase(excelExportFiltersForm
									.getDataImportKeyValueDTO().getMethodName())) {
						where = where
								+ " "
								+ excelExportFiltersForm.getOpenBracket()
								+ " country."
								+ excelExportFiltersForm
										.getDataImportKeyValueDTO()
										.getActualColName() + " ";

					} else if ("Frequency"
							.equalsIgnoreCase(excelExportFiltersForm
									.getDataImportKeyValueDTO().getMethodName())) {
						where = where
								+ " "
								+ excelExportFiltersForm.getOpenBracket()
								+ " payslipFrequency."
								+ excelExportFiltersForm
										.getDataImportKeyValueDTO()
										.getActualColName() + " ";

					} else if ("TimeZoneName"
							.equalsIgnoreCase(excelExportFiltersForm
									.getDataImportKeyValueDTO().getMethodName())) {
						where = where
								+ " "
								+ excelExportFiltersForm.getOpenBracket()
								+ " timeZoneMaster."
								+ excelExportFiltersForm
										.getDataImportKeyValueDTO()
										.getActualColName() + " ";

					} else {
						where = where
								+ " "
								+ excelExportFiltersForm.getOpenBracket()
								+ " company."
								+ excelExportFiltersForm
										.getDataImportKeyValueDTO()
										.getActualColName() + " ";
					}
				} else {

					if (!excelExportFiltersForm.getDataImportKeyValueDTO()
							.isChild()) {

						if ("numeric".equalsIgnoreCase(excelExportFiltersForm
								.getDataImportKeyValueDTO().getFieldType())) {
							where = where
									+ " "
									+ excelExportFiltersForm.getOpenBracket()
									+ " dynamicFormRecord"
									+ excelExportFiltersForm
											.getDataImportKeyValueDTO()
											.getFormId()
									+ ".Col_"
									+ excelExportFiltersForm
											.getDataImportKeyValueDTO()
											.getMethodName() + " ";
						} else if ("date"
								.equalsIgnoreCase(excelExportFiltersForm
										.getDataImportKeyValueDTO()
										.getFieldType())) {

							where = where
									+ " "
									+ excelExportFiltersForm.getOpenBracket()
									+ "convert(datetime, dynamicFormRecord"
									+ excelExportFiltersForm
											.getDataImportKeyValueDTO()
											.getFormId()
									+ ".Col_"
									+ excelExportFiltersForm
											.getDataImportKeyValueDTO()
											.getMethodName() + ") ";

						} else if ("codedesc"
								.equalsIgnoreCase(excelExportFiltersForm
										.getDataImportKeyValueDTO()
										.getFieldType())) {

							where = where
									+ " "
									+ excelExportFiltersForm.getOpenBracket()
									+ " dynamicFormFieldRefValue"
									+ excelExportFiltersForm
											.getDataImportKeyValueDTO()
											.getFormId()
									+ "Col_"
									+ excelExportFiltersForm
											.getDataImportKeyValueDTO()
											.getMethodName() + ".Code ";

						} else {
							where = where
									+ " "
									+ excelExportFiltersForm.getOpenBracket()
									+ " dynamicFormRecord"
									+ excelExportFiltersForm
											.getDataImportKeyValueDTO()
											.getFormId()
									+ ".Col_"
									+ excelExportFiltersForm
											.getDataImportKeyValueDTO()
											.getMethodName() + " ";
						}
					} else {

						if ("numeric".equalsIgnoreCase(excelExportFiltersForm
								.getDataImportKeyValueDTO().getFieldType())) {

							where = where
									+ " "
									+ excelExportFiltersForm.getOpenBracket()
									+ " dynamicFormTableRecord_"
									+ excelExportFiltersForm
											.getDataImportKeyValueDTO()
											.getFormId()
									+ excelExportFiltersForm
											.getDataImportKeyValueDTO()
											.getTablePosition()
									+ ".Col_"
									+ excelExportFiltersForm
											.getDataImportKeyValueDTO()
											.getMethodName() + "  ";

						} else if ("date"
								.equalsIgnoreCase(excelExportFiltersForm
										.getDataImportKeyValueDTO()
										.getFieldType())) {

							where = where
									+ " "
									+ excelExportFiltersForm.getOpenBracket()
									+ "convert(datetime, ltrim(dynamicFormTableRecord_"
									+ excelExportFiltersForm
											.getDataImportKeyValueDTO()
											.getFormId()
									+ excelExportFiltersForm
											.getDataImportKeyValueDTO()
											.getTablePosition()
									+ ".Col_"
									+ excelExportFiltersForm
											.getDataImportKeyValueDTO()
											.getMethodName() + ")) ";

						} else if ("codedesc"
								.equalsIgnoreCase(excelExportFiltersForm
										.getDataImportKeyValueDTO()
										.getFieldType())) {
							where = where
									+ " "
									+ excelExportFiltersForm.getOpenBracket()
									+ " dynamicFormFieldRefValueT"
									+ excelExportFiltersForm
											.getDataImportKeyValueDTO()
											.getFormId()
									+ excelExportFiltersForm
											.getDataImportKeyValueDTO()
											.getTablePosition()
									+ "Col_"
									+ excelExportFiltersForm
											.getDataImportKeyValueDTO()
											.getMethodName() + ".Code ";
						} else {

							where = where
									+ " "
									+ excelExportFiltersForm.getOpenBracket()
									+ " dynamicFormTableRecord_"
									+ excelExportFiltersForm
											.getDataImportKeyValueDTO()
											.getFormId()
									+ excelExportFiltersForm
											.getDataImportKeyValueDTO()
											.getTablePosition()
									+ ".Col_"
									+ excelExportFiltersForm
											.getDataImportKeyValueDTO()
											.getMethodName() + " ";

						}
					}

				}

				if (count < finalFilterList.size()) {
					paramValueMap.put("param" + count,
							excelExportFiltersForm.getValue());
					if ("numeric".equalsIgnoreCase(excelExportFiltersForm
							.getDataImportKeyValueDTO().getFieldType())) {
						where = where
								+ excelExportFiltersForm.getEqualityOperator()
								+ ":param" + count + " "
								+ excelExportFiltersForm.getCloseBracket()
								+ " "
								+ excelExportFiltersForm.getLogicalOperator()
								+ " ";
					} else {
						where = where
								+ excelExportFiltersForm.getEqualityOperator()
								+ ":param" + count + " "
								+ excelExportFiltersForm.getCloseBracket()
								+ " "
								+ excelExportFiltersForm.getLogicalOperator()
								+ " ";
					}

				} else {
					paramValueMap.put("param" + count,
							excelExportFiltersForm.getValue());
					if ("numeric".equalsIgnoreCase(excelExportFiltersForm
							.getDataImportKeyValueDTO().getFieldType())) {
						where = where
								+ excelExportFiltersForm.getEqualityOperator()
								+ ":param" + count + " "
								+ excelExportFiltersForm.getCloseBracket()
								+ " ";
					} else {
						where = where
								+ excelExportFiltersForm.getEqualityOperator()
								+ ":param" + count + " "
								+ excelExportFiltersForm.getCloseBracket()
								+ " ";
					}

				}

				count++;
			}

		}
		return where + " ORDER BY company.Company_ID DESC";

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.CompanyDAO#findByConditionAndEmployeeId(com.payasia.common
	 * .dto.CompanyConditionDTO, java.lang.Long,
	 * com.payasia.common.form.PageRequest, com.payasia.common.form.SortCondition)
	 */
	@Override
	public List<Company> findByConditionAndEmployeeId(
			CompanyConditionDTO conditionDTO, Long empId, PageRequest pageDTO,
			SortCondition sortDTO) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Company> criteriaQuery = cb.createQuery(Company.class);
		Root<Company> companyRoot = criteriaQuery.from(Company.class);

		Join<Company, CompanyGroup> companyCompanyGroupJoin = companyRoot.join(
				Company_.companyGroup, JoinType.LEFT);

		Join<Company, FinancialYearMaster> companyFinancialYearMasterJoin = companyRoot
				.join(Company_.financialYearMaster, JoinType.LEFT);

		Join<Company, CountryMaster> companyCountryMasterJoin = companyRoot
				.join(Company_.countryMaster, JoinType.LEFT);

		Join<Company, PayslipFrequency> companyPayslipFrequencyJoin = companyRoot
				.join(Company_.payslipFrequency, JoinType.LEFT);

		criteriaQuery.select(companyRoot);

		Predicate restriction = cb.conjunction();

		if (StringUtils.isNotBlank(conditionDTO.getCompanyCode())) {

			restriction = cb.and(restriction, cb.like(
					cb.upper(companyRoot.get(Company_.companyCode)),
					conditionDTO.getCompanyCode().toUpperCase()));
		}

		if (StringUtils.isNotBlank(conditionDTO.getCompanyName())) {

			restriction = cb.and(restriction, cb.like(
					cb.upper(companyRoot.get(Company_.companyName)),
					conditionDTO.getCompanyName().toUpperCase()));
		}

		if (StringUtils.isNotBlank(conditionDTO.getGroup())) {

			restriction = cb.and(restriction, cb.equal(
					companyCompanyGroupJoin.get(CompanyGroup_.groupId),
					Long.parseLong(conditionDTO.getGroup())));
		}
		if (StringUtils.isNotBlank(conditionDTO.getFinancialYear())) {

			restriction = cb.and(restriction, cb.equal(
					companyFinancialYearMasterJoin
							.get(FinancialYearMaster_.finYearId), Long
									.parseLong(conditionDTO.getFinancialYear())));
		}

		if (StringUtils.isNotBlank(conditionDTO.getCountry())) {

			restriction = cb.and(restriction, cb.equal(
					companyCountryMasterJoin.get(CountryMaster_.countryId),
					Long.parseLong(conditionDTO.getCountry())));
		}

		if (StringUtils.isNotBlank(conditionDTO.getPaySlipFrequency())) {

			restriction = cb.and(restriction, cb.equal(
					companyPayslipFrequencyJoin
							.get(PayslipFrequency_.payslipFrequencyID), Long
									.parseLong(conditionDTO.getPaySlipFrequency())));
		}

		Subquery<Long> subquery = criteriaQuery.subquery(Long.class);
		Root<EmployeeRoleMapping> employeeRoleMap = subquery
				.from(EmployeeRoleMapping.class);
		subquery.select(employeeRoleMap.get(EmployeeRoleMapping_.company)
				.get("companyId").as(Long.class));

		Join<EmployeeRoleMapping, Employee> employeeRoleMapSubJoin = employeeRoleMap
				.join(EmployeeRoleMapping_.employee);

		Path<Long> empIdVO = employeeRoleMapSubJoin.get(Employee_.employeeId);

		Predicate subRestriction = cb.conjunction();

		subRestriction = cb.and(subRestriction, cb.equal(empIdVO, empId));

		subquery.where(subRestriction);

		restriction = cb.and(restriction,
				cb.in(companyRoot.get(Company_.companyId)).value(subquery));

		criteriaQuery.where(restriction);

		criteriaQuery.orderBy(cb.asc(companyRoot.get(Company_.companyId)));

		if (sortDTO != null) {

			Path<String> sortPath = getSortPathForSearchEmployee(sortDTO,
					companyRoot, companyCompanyGroupJoin,
					companyCountryMasterJoin, companyPayslipFrequencyJoin);
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

		TypedQuery<Company> companyTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			companyTypedQuery.setFirstResult(getStartPosition(pageDTO));
			companyTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		return companyTypedQuery.getResultList();
	}

	/**
	 * Gets the sort path for search employee.
	 * 
	 * @param sortDTO
	 *            the sort dto
	 * @param companyRoot
	 *            the company root
	 * @param companyCompanyGroupJoin
	 *            the company company group join
	 * @param companyCountryMasterJoin
	 *            the company country master join
	 * @param companyPayslipFrequencyJoin
	 *            the company payslip frequency join
	 * @return the sort path for search employee
	 */
	private Path<String> getSortPathForSearchEmployee(SortCondition sortDTO,
			Root<Company> companyRoot,
			Join<Company, CompanyGroup> companyCompanyGroupJoin,
			Join<Company, CountryMaster> companyCountryMasterJoin,
			Join<Company, PayslipFrequency> companyPayslipFrequencyJoin) {

		List<String> companyIdList = new ArrayList<String>();
		companyIdList.add(SortConstants.COMPANY_INFORMTAION_COMPANY_ID);

		List<String> companyIsColList = new ArrayList<String>();
		companyIsColList.add(SortConstants.COMPANY_INFORMTAION_COMPANY_CODE);
		companyIsColList.add(SortConstants.COMPANY_INFORMTAION_COMPANY_NAME);

		List<String> companyGroupIsColList = new ArrayList<String>();
		companyGroupIsColList.add(SortConstants.COMPANY_INFORMTAION_GROUP_NAME);
		companyGroupIsColList.add(SortConstants.COMPANY_INFORMTAION_GROUP_CODE);

		List<String> countryIsColList = new ArrayList<String>();
		countryIsColList.add(SortConstants.COMPANY_INFORMTAION_COUNTRY);

		List<String> payslipFrequencyColList = new ArrayList<String>();
		payslipFrequencyColList
				.add(SortConstants.COMPANY_INFORMTAION_PAYSLIP_FREQUENCY);

		Path<String> sortPath = null;

		if (companyIsColList.contains(sortDTO.getColumnName())) {
			sortPath = companyRoot.get(colMap.get(Company.class
					+ sortDTO.getColumnName()));
		}
		if (companyGroupIsColList.contains(sortDTO.getColumnName())) {
			sortPath = companyCompanyGroupJoin.get(colMap
					.get(CompanyGroup.class + sortDTO.getColumnName()));
		}
		if (countryIsColList.contains(sortDTO.getColumnName())) {
			sortPath = companyCountryMasterJoin.get(colMap
					.get(CountryMaster.class + sortDTO.getColumnName()));
		}
		if (payslipFrequencyColList.contains(sortDTO.getColumnName())) {
			sortPath = companyPayslipFrequencyJoin.get(colMap
					.get(PayslipFrequency.class + sortDTO.getColumnName()));
		}
		return sortPath;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.CompanyDAO#getCountForConditionAndEmployeeId(com.payasia
	 * .common.dto.CompanyConditionDTO, java.lang.Long)
	 */
	@Override
	public int getCountForConditionAndEmployeeId(
			CompanyConditionDTO conditionDTO, Long empId) {
		return findByConditionAndEmployeeId(conditionDTO, empId, null, null)
				.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.CompanyDAO#copyCompany(com.payasia.common.dto.CompanyCopyDTO
	 * )
	 */
	@Override
	public CompanyCopyDTO copyCompany(final CompanyCopyDTO companyCopyDTO) {
		final CompanyCopyDTO copyDTO = new CompanyCopyDTO();
		final HashMap<Long, Long> dictionaryMap = new HashMap<>();

		getSession().doWork(new Work() {

			@Override
			public void execute(Connection connection) throws SQLException {
				try {
					CallableStatement cstmt = null;
					cstmt = connection
							.prepareCall(
									"{call Manage_Copy_Company (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
					cstmt.setString("@User_ID",
							String.valueOf(companyCopyDTO.getEmployeeId()));
					cstmt.setInt("@Company_ID", companyCopyDTO.getCompanyId());
					cstmt.setString("@Company_Name",
							companyCopyDTO.getCopyCompanyName());
					cstmt.setString("@Company_Code", companyCopyDTO
							.getCopyCompanyCode().toLowerCase());
					cstmt.setBoolean("@Copy_Company_Form_Designer",
							companyCopyDTO.getCopyCompanyFormDesigner());
					cstmt.setBoolean("@Copy_Company_Import_Template",
							companyCopyDTO.getCopyCompanyImportTemplate());
					cstmt.setBoolean("@Copy_Company_Export_Template",
							companyCopyDTO.getCopyCompanyExportTemplate());
					cstmt.setBoolean("@Copy_Employee_Form_Designer",
							companyCopyDTO.getCopyEmployeeFormDesigner());
					cstmt.setBoolean("@Copy_Employee_Import_Template",
							companyCopyDTO.getCopyEmployeeImportTemplate());
					cstmt.setBoolean("@Copy_Employee_Export_Template",
							companyCopyDTO.getCopyEmployeeExportTemplate());
					cstmt.setBoolean("@Copy_Payslip_Form_Designer",
							companyCopyDTO.getCopyPayslipFormDesigner());
					cstmt.setBoolean("@Copy_Payslip_Designer",
							companyCopyDTO.getCopyPayslipDesigner());
					cstmt.setBoolean("@Copy_Payslip_Import_Template",
							companyCopyDTO.getCopyPayslipImportTemplate());
					cstmt.setBoolean("@Copy_Payslip_Export_Template",
							companyCopyDTO.getCopyPayslipExportTemplate());
					cstmt.setBoolean("@Copy_Leave_Preference",
							companyCopyDTO.getCopyLeavePreference());
					cstmt.setBoolean("@Copy_Leave_Type",
							companyCopyDTO.getCopyLeaveType());
					cstmt.setBoolean("@Copy_Leave_Scheme",
							companyCopyDTO.getCopyLeaveScheme());
					cstmt.setBoolean("@Copy_Company_Calendar",
							companyCopyDTO.getCopyCompanyCalendar());
					cstmt.setBoolean("@Copy_Holiday_Calendar",
							companyCopyDTO.getCopyHolidayCalendar());
					cstmt.setBoolean("@Copy_Claim_Preference",
							companyCopyDTO.getCopyClaimPreference());
					cstmt.setBoolean("@Copy_Claim_Item",
							companyCopyDTO.getCopyClaimItem());
					cstmt.setBoolean("@Copy_Claim_Template",
							companyCopyDTO.getCopyClaimTemplate());
					cstmt.setBoolean("@Copy_Claim_Batch",
							companyCopyDTO.getCopyClaimBatch());
					cstmt.setBoolean("@Copy_Company_Role",
							companyCopyDTO.getCopyCompanyRole());
					cstmt.setBoolean("@Copy_Company_Privilege",
							companyCopyDTO.getCopyCompanyPrivilege());

					cstmt.registerOutParameter("@Status", java.sql.Types.BIT);
					cstmt.registerOutParameter("@Created_Company_ID",
							java.sql.Types.BIGINT);
					ResultSet rs = cstmt.executeQuery();
					if (rs != null) {
						while (rs.next()) {
							dictionaryMap.put(
									rs.getLong("Old_Data_Dictionary_ID"),
									rs.getLong("New_Data_Dictionary_ID"));
						}
					}
					rs.close();
					copyDTO.setStatus(cstmt.getBoolean("@Status"));
					copyDTO.setDataDictionaryMap(dictionaryMap);
					copyDTO.setNewCompanyID(cstmt
							.getLong("@Created_Company_ID"));
				} catch (SQLException e) {
					throw new PayAsiaSystemException(e.getMessage(), e);
				}

			}
		});

		return copyDTO;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.CompanyDAO#findByCompanyCode(java.lang.String,
	 * java.lang.Long)
	 */
	@Override
	public Company findByCompanyCode(String companyCode, Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Company> criteriaQuery = cb.createQuery(Company.class);
		Root<Company> companyRoot = criteriaQuery.from(Company.class);

		criteriaQuery.select(companyRoot);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(
				restriction,
				cb.equal(companyRoot.get(Company_.companyCode),
						companyCode.toLowerCase()));

		if (companyId != null) {
			restriction = cb
					.and(restriction, cb.notEqual(
							companyRoot.get(Company_.companyId), companyId));
		}

		criteriaQuery.where(restriction);

		TypedQuery<Company> companyTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<Company> companyList = companyTypedQuery.getResultList();
		if (companyList != null && !companyList.isEmpty()) {
			return companyList.get(0);
		}
		return null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.CompanyDAO#findByCompanyName(java.lang.String)
	 */
	@Override
	public Company findByCompanyName(String companyName) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Company> criteriaQuery = cb.createQuery(Company.class);
		Root<Company> companyRoot = criteriaQuery.from(Company.class);

		criteriaQuery.select(companyRoot);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(
				cb.upper(companyRoot.get(Company_.companyName)),
				companyName.toUpperCase()));

		criteriaQuery.where(restriction);

		TypedQuery<Company> companyTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<Company> companyList = companyTypedQuery.getResultList();
		if (companyList != null && !companyList.isEmpty()) {
			return companyList.get(0);
		}
		return null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.CompanyDAO#findByGroupId(com.payasia.common.form.
	 * SortCondition, java.lang.Long)
	 */
	@Override
	public List<Company> findByGroupId(SortCondition sortDTO,
			Long companyGroupId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Company> criteriaQuery = cb.createQuery(Company.class);
		Root<Company> companyRoot = criteriaQuery.from(Company.class);

		criteriaQuery.select(companyRoot);

		Join<Company, CompanyGroup> companyGroupJoin = companyRoot
				.join(Company_.companyGroup);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(
				companyGroupJoin.get(CompanyGroup_.groupId), companyGroupId));

		criteriaQuery.where(restriction);
		if (sortDTO != null) {

			Path<String> sortPath = getSortPathForAllCompany(sortDTO,
					companyRoot, companyGroupJoin);
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

		TypedQuery<Company> companyTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return companyTypedQuery.getResultList();
	}

	@Override
	public Company findByIdDetached(long companyId) {
		Company company = super.findById(Company.class, companyId);
		this.entityManagerFactory.detach(company);
		return company;
	}

	@Override
	public Long getCountOfCompanies(CompanyConditionDTO conditionDTO, Long empId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
		Root<Company> companyRoot = criteriaQuery.from(Company.class);

		Join<Company, CompanyGroup> companyCompanyGroupJoin = companyRoot.join(
				Company_.companyGroup, JoinType.LEFT);

		Join<Company, FinancialYearMaster> companyFinancialYearMasterJoin = companyRoot
				.join(Company_.financialYearMaster, JoinType.LEFT);

		Join<Company, CountryMaster> companyCountryMasterJoin = companyRoot
				.join(Company_.countryMaster, JoinType.LEFT);

		Join<Company, PayslipFrequency> companyPayslipFrequencyJoin = companyRoot
				.join(Company_.payslipFrequency, JoinType.LEFT);

		criteriaQuery.select(cb.count(companyRoot));

		Predicate restriction = cb.conjunction();

		if (StringUtils.isNotBlank(conditionDTO.getCompanyCode())) {

			restriction = cb.and(restriction, cb.like(
					cb.upper(companyRoot.get(Company_.companyCode)),
					conditionDTO.getCompanyCode().toUpperCase()));
		}

		if (StringUtils.isNotBlank(conditionDTO.getCompanyName())) {

			restriction = cb.and(restriction, cb.like(
					cb.upper(companyRoot.get(Company_.companyName)),
					conditionDTO.getCompanyName().toUpperCase()));
		}

		if (StringUtils.isNotBlank(conditionDTO.getGroup())) {

			restriction = cb.and(restriction, cb.equal(
					companyCompanyGroupJoin.get(CompanyGroup_.groupId),
					Long.parseLong(conditionDTO.getGroup())));
		}
		if (StringUtils.isNotBlank(conditionDTO.getFinancialYear())) {

			restriction = cb.and(restriction, cb.equal(
					companyFinancialYearMasterJoin
							.get(FinancialYearMaster_.finYearId), Long
									.parseLong(conditionDTO.getFinancialYear())));
		}

		if (StringUtils.isNotBlank(conditionDTO.getCountry())) {

			restriction = cb.and(restriction, cb.equal(
					companyCountryMasterJoin.get(CountryMaster_.countryId),
					Long.parseLong(conditionDTO.getCountry())));
		}

		if (StringUtils.isNotBlank(conditionDTO.getPaySlipFrequency())) {

			restriction = cb.and(restriction, cb.equal(
					companyPayslipFrequencyJoin
							.get(PayslipFrequency_.payslipFrequencyID), Long
									.parseLong(conditionDTO.getPaySlipFrequency())));
		}

		Subquery<Long> subquery = criteriaQuery.subquery(Long.class);
		Root<EmployeeRoleMapping> employeeRoleMap = subquery
				.from(EmployeeRoleMapping.class);
		subquery.select(employeeRoleMap.get(EmployeeRoleMapping_.company)
				.get("companyId").as(Long.class));

		Join<EmployeeRoleMapping, Employee> employeeRoleMapSubJoin = employeeRoleMap
				.join(EmployeeRoleMapping_.employee);

		Path<Long> empIdVO = employeeRoleMapSubJoin.get(Employee_.employeeId);

		Predicate subRestriction = cb.conjunction();

		subRestriction = cb.and(subRestriction, cb.equal(empIdVO, empId));

		subquery.where(subRestriction);

		restriction = cb.and(restriction,
				cb.in(companyRoot.get(Company_.companyId)).value(subquery));

		criteriaQuery.where(restriction);

		TypedQuery<Long> companyTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return companyTypedQuery.getSingleResult();
	}

	@Override
	public Long getCountForSwitchCompany(Long employeeId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
		Root<Company> companyRoot = criteriaQuery.from(Company.class);
		companyRoot.join(Company_.companyGroup, JoinType.INNER);

		criteriaQuery.select(cb.countDistinct(companyRoot
				.get(Company_.companyName)));

		Join<Company, EmployeeRoleMapping> companyEmpJoin = companyRoot
				.join(Company_.employeeRoleMappings);

		Join<EmployeeRoleMapping, Employee> roleEmployeeJoin = companyEmpJoin
				.join(EmployeeRoleMapping_.employee);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(
				roleEmployeeJoin.get(Employee_.employeeId), employeeId));

		criteriaQuery.where(restriction);

		TypedQuery<Long> companyTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return companyTypedQuery.getSingleResult();
	}

	@Override
	public Long getCountForSwitchGroupCompany(Long employeeId, Long groupId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
		Root<Company> companyRoot = criteriaQuery.from(Company.class);
		Join<Company, CompanyGroup> compGroupJoin = companyRoot.join(
				Company_.companyGroup, JoinType.INNER);

		criteriaQuery.select(cb.countDistinct(companyRoot
				.get(Company_.companyName)));

		Join<Company, EmployeeRoleMapping> companyEmpJoin = companyRoot
				.join(Company_.employeeRoleMappings);

		Join<EmployeeRoleMapping, Employee> roleEmployeeJoin = companyEmpJoin
				.join(EmployeeRoleMapping_.employee);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(
				roleEmployeeJoin.get(Employee_.employeeId), employeeId));
		restriction = cb.and(restriction,
				cb.equal(compGroupJoin.get(CompanyGroup_.groupId), groupId));

		criteriaQuery.where(restriction);

		TypedQuery<Long> companyTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return companyTypedQuery.getSingleResult();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.CompanyDAO#findAllCmps()
	 */
	@Override
	public List<Company> findAllCmps() {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Company> criteriaQuery = cb.createQuery(Company.class);
		Root<Company> companyRoot = criteriaQuery.from(Company.class);

		criteriaQuery.select(companyRoot);

		TypedQuery<Company> companyTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<Company> allCompanyList = companyTypedQuery.getResultList();
		return allCompanyList;
	}

	@Override
	public List<Long> getDistinctAssignedGroupCompanies(Long employeeId,
			Long groupId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
		Root<EmployeeRoleMapping> empRoot = criteriaQuery
				.from(EmployeeRoleMapping.class);
		criteriaQuery.select(
				empRoot.get(EmployeeRoleMapping_.company).get("companyId")
						.as(Long.class)).distinct(true);

		Join<EmployeeRoleMapping, Employee> roleEmployeeJoin = empRoot
				.join(EmployeeRoleMapping_.employee);

		Join<EmployeeRoleMapping, Company> companyJoin = empRoot
				.join(EmployeeRoleMapping_.company);

		Join<Company, CompanyGroup> companyGroupJoin = companyJoin
				.join(Company_.companyGroup);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(
				roleEmployeeJoin.get(Employee_.employeeId), employeeId));

		restriction = cb.and(restriction,
				cb.equal(companyGroupJoin.get(CompanyGroup_.groupId), groupId));

		criteriaQuery.where(restriction);

		TypedQuery<Long> employeeRoleMappingQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return employeeRoleMappingQuery.getResultList();
	}

	@Override
	public List<Long> getDistinctTimeZoneIds() {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
		Root<Company> cmpRoot = criteriaQuery.from(Company.class);
		criteriaQuery.select(
				cmpRoot.get(Company_.timeZoneMaster).get("timeZoneId")
						.as(Long.class)).distinct(true);

		TypedQuery<Long> companyMappingQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return companyMappingQuery.getResultList();
	}

	@Override
	public List<Company> findCompanyByTimeZone(Long timeZoneId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Company> criteriaQuery = cb.createQuery(Company.class);
		Root<Company> companyRoot = criteriaQuery.from(Company.class);
		criteriaQuery.select(companyRoot);

		Join<Company, TimeZoneMaster> timeZoneJoin = companyRoot
				.join(Company_.timeZoneMaster);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(
				timeZoneJoin.get(TimeZoneMaster_.timeZoneId), timeZoneId));

		restriction = cb.and(restriction,
				cb.equal(companyRoot.get(Company_.active), true));

		criteriaQuery.where(restriction);

		TypedQuery<Company> companyTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return companyTypedQuery.getResultList();
	}

	@Override
	public List<Object[]> createQueryForCompanyCustomField(
			Map<String, DataImportKeyValueDTO> colMap, Long companyId,
			List<Long> formIds, String dateFormat,
			Map<String, DataImportKeyValueDTO> tableRecordInfo,
			boolean showOnlyCompanyDynFieldCode) {
		Map<String, List<Long>> paramValueMap = new HashMap<String, List<Long>>();
		List<String> tableJoinsList = new ArrayList<>();
		ExcelExportQueryDTO selectDTO = createCustomFieldSelect(colMap,
				tableRecordInfo, showOnlyCompanyDynFieldCode);
		String from = createCustomFieldFrom(colMap, formIds, selectDTO,
				tableRecordInfo, tableJoinsList);
		String where = createCustomFieldWhere(companyId, tableJoinsList,
				paramValueMap);
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

	public ExcelExportQueryDTO createCustomFieldSelect(
			Map<String, DataImportKeyValueDTO> colMap,
			Map<String, DataImportKeyValueDTO> tableRecordInfo,
			boolean showOnlyCompanyDynFieldCode) {

		ExcelExportQueryDTO excelExportQueryDTO = new ExcelExportQueryDTO();
		List<CodeDescDTO> codeDescDTOs = new ArrayList<CodeDescDTO>();

		String selectQuery = "SELECT ";
		String tablePosition = null;
		Set<String> xlKeySet = colMap.keySet();

		int count = 1;

		selectQuery = selectQuery + "company.Company_ID AS companyId ,";

		for (Iterator<String> itr = xlKeySet.iterator(); itr.hasNext();) {
			if (count < xlKeySet.size()) {
				String key = (String) itr.next();
				DataImportKeyValueDTO valueDTO = (DataImportKeyValueDTO) colMap
						.get(key);
				String fieldName = valueDTO.getMethodName();
				key = key.replaceAll("\\.", "_");
				String codeKey = key;
				key = "[" + key + "]";
				if (valueDTO.isStatic()) {
					String methodName = valueDTO.getActualColName();

					selectQuery = selectQuery + "company." + methodName
							+ " AS " + key + ", ";
				} else {

					String methodName = "Col_" + fieldName;

					if (!valueDTO.isChild()) {

						if (valueDTO.getFieldType()
								.equalsIgnoreCase("codedesc")) {

							CodeDescDTO codeDescDTO = new CodeDescDTO();
							codeDescDTO.setMethodName(methodName);
							codeDescDTO.setFormId(valueDTO.getFormId());
							codeDescDTO.setChildVal(false);
							codeDescDTOs.add(codeDescDTO);

							if (showOnlyCompanyDynFieldCode) {
								selectQuery = selectQuery
										+ " dynamicFormFieldRefValue"
										+ valueDTO.getFormId() + methodName
										+ ".Code  AS [" + codeKey
										+ "_CodeDescription] " + ", ";
							} else {
								selectQuery = selectQuery
										+ " dynamicFormFieldRefValue"
										+ valueDTO.getFormId() + methodName
										+ ".Description  AS [" + codeKey
										+ "_CodeDescription] " + ", ";
							}

						} else {
							selectQuery = selectQuery + " dynamicFormRecord"
									+ valueDTO.getFormId() + "." + methodName
									+ " AS " + key + ", ";
						}

					} else {

						if (valueDTO.getFieldType()
								.equalsIgnoreCase("codedesc")) {
							CodeDescDTO codeDescDTO = new CodeDescDTO();
							codeDescDTO.setMethodName(methodName);
							codeDescDTO.setFormId(valueDTO.getFormId());
							codeDescDTO.setChildVal(true);
							codeDescDTO.setTablePosition(valueDTO
									.getTablePosition());
							codeDescDTOs.add(codeDescDTO);

							if (showOnlyCompanyDynFieldCode) {
								selectQuery = selectQuery
										+ " dynamicFormFieldRefValueT"
										+ valueDTO.getFormId()
										+ valueDTO.getTablePosition()
										+ methodName + ".Code  AS [" + codeKey
										+ "_CodeDescription] " + ", ";
							} else {
								selectQuery = selectQuery
										+ " dynamicFormFieldRefValue"
										+ valueDTO.getFormId() + methodName
										+ ".Description  AS [" + codeKey
										+ "_CodeDescription] " + ", ";
							}

						} else {

							selectQuery = selectQuery
									+ " dynamicFormTableRecord_"
									+ valueDTO.getFormId()
									+ valueDTO.getTablePosition() + "."
									+ methodName + " AS " + key + ", ";

						}

						tablePosition = valueDTO.getTablePosition();
						excelExportQueryDTO.setTablePosition(tablePosition);
						excelExportQueryDTO.setFormId(valueDTO.getFormId());

					}
				}
			} else {
				String key = (String) itr.next();
				DataImportKeyValueDTO valueDTO = (DataImportKeyValueDTO) colMap
						.get(key);
				String fieldName = valueDTO.getMethodName();
				key = key.replaceAll("\\.", "_");
				String codeKey = key;
				key = "[" + key + "]";
				if (valueDTO.isStatic()) {
					String methodName = valueDTO.getActualColName();
					;

					selectQuery = selectQuery + "company." + methodName
							+ " AS " + key + " ";
				} else {

					String methodName = "Col_" + fieldName;

					if (!valueDTO.isChild()) {

						if (valueDTO.getFieldType()
								.equalsIgnoreCase("codedesc")) {

							CodeDescDTO codeDescDTO = new CodeDescDTO();
							codeDescDTO.setMethodName(methodName);
							codeDescDTO.setFormId(valueDTO.getFormId());
							codeDescDTO.setChildVal(false);
							codeDescDTOs.add(codeDescDTO);

							if (showOnlyCompanyDynFieldCode) {
								selectQuery = selectQuery
										+ " dynamicFormFieldRefValue"
										+ valueDTO.getFormId() + methodName
										+ ".Code  AS [" + codeKey
										+ "_CodeDescription] ";
							} else {
								selectQuery = selectQuery
										+ " dynamicFormFieldRefValue"
										+ valueDTO.getFormId() + methodName
										+ ".Description  AS [" + codeKey
										+ "_CodeDescription]  ";
							}

						} else {
							selectQuery = selectQuery + " dynamicFormRecord"
									+ valueDTO.getFormId() + "." + methodName
									+ " AS " + key + " ";
						}

					} else {

						if (valueDTO.getFieldType()
								.equalsIgnoreCase("codedesc")) {
							CodeDescDTO codeDescDTO = new CodeDescDTO();
							codeDescDTO.setMethodName(methodName);
							codeDescDTO.setFormId(valueDTO.getFormId());
							codeDescDTO.setChildVal(true);
							codeDescDTO.setTablePosition(valueDTO
									.getTablePosition());
							codeDescDTOs.add(codeDescDTO);

							if (showOnlyCompanyDynFieldCode) {
								selectQuery = selectQuery
										+ " dynamicFormFieldRefValueT"
										+ valueDTO.getFormId()
										+ valueDTO.getTablePosition()
										+ methodName + ".Code AS [" + codeKey
										+ "_CodeDescription] ";
							} else {
								selectQuery = selectQuery
										+ " dynamicFormFieldRefValue"
										+ valueDTO.getFormId() + methodName
										+ ".Description  AS [" + codeKey
										+ "_CodeDescription] " + ", ";
							}

						} else {

							selectQuery = selectQuery
									+ " dynamicFormTableRecord_"
									+ valueDTO.getFormId()
									+ valueDTO.getTablePosition() + "."
									+ methodName + " AS " + key + " ";

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

	public String createCustomFieldFrom(
			Map<String, DataImportKeyValueDTO> colMap, List<Long> formIds,
			ExcelExportQueryDTO queryDTO,
			Map<String, DataImportKeyValueDTO> tableRecordInfo,
			List<String> tableJoinsList) {
		String tableDateStr = "";

		String from = "FROM Company AS company ";
		tableJoinsList.add("company");
		for (Long formId : formIds) {

			from = from
					+ "LEFT OUTER JOIN Dynamic_Form_Record AS dynamicFormRecord"
					+ formId + " ";
			from = from + " ON (company.Company_ID = dynamicFormRecord"
					+ formId + ".Entity_Key) AND (dynamicFormRecord" + formId
					+ ".Form_ID = " + formId + " ) ";
			tableJoinsList.add("dynamicFormRecord" + formId);
		}

		Integer noOfTables = tableRecordInfo.size();
		if (noOfTables <= 1) {

			if (queryDTO.getTablePosition() != null) {
				from = from
						+ "LEFT OUTER JOIN Dynamic_Form_Table_Record AS dynamicFormTableRecord_"
						+ queryDTO.getFormId() + queryDTO.getTablePosition();
				from = from + " ON (dynamicFormRecord" + queryDTO.getFormId()
						+ ".Col_" + queryDTO.getTablePosition()
						+ " = dynamicFormTableRecord_" + queryDTO.getFormId()
						+ queryDTO.getTablePosition()
						+ ".Dynamic_Form_Table_Record_ID ) ";
				tableJoinsList.add("dynamicFormTableRecord");
			}

		} else {

			for (Map.Entry<String, DataImportKeyValueDTO> entry : tableRecordInfo
					.entrySet()) {

				entry.getKey();
				entry.getValue();

				from = from
						+ " LEFT OUTER JOIN Dynamic_Form_Table_Record AS dynamicFormTableRecord_"
						+ entry.getKey();
				tableJoinsList.add("dynamicFormTableRecord_" + entry.getKey());

				tableDateStr = "getDate()";

				from = from
						+ " ON dynamicFormRecord"
						+ entry.getValue().getFormId()
						+ ".Col_"
						+ entry.getValue().getTablePosition()
						+ " = dynamicFormTableRecord_"
						+ entry.getKey()
						+ ".Dynamic_Form_Table_Record_ID  "
						+ "and CONVERT(datetime,dynamicFormTableRecord_"
						+ entry.getKey()
						+ ".Col_1) = (select top 1 max(CONVERT(datetime, dynamicFormTableRecord_max"
						+ entry.getKey()
						+ ".Col_1)) from Dynamic_Form_Table_Record dynamicFormTableRecord_max"
						+ entry.getKey()
						+ " where dynamicFormTableRecord_max"
						+ entry.getKey()
						+ ".Dynamic_Form_Table_Record_ID =dynamicFormTableRecord_"
						+ entry.getKey()
						+ ".Dynamic_Form_Table_Record_ID  and CONVERT(date, dynamicFormTableRecord_max"
						+ entry.getKey() + ".Col_1) <= " + tableDateStr + ")";

			}

		}

		for (CodeDescDTO codeDescDTO : queryDTO.getCodeDescList()) {

			if (codeDescDTO.isChildVal()) {
				from = from
						+ "LEFT OUTER JOIN Dynamic_Form_Field_Ref_Value AS dynamicFormFieldRefValueT"
						+ codeDescDTO.getFormId()
						+ codeDescDTO.getTablePosition()
						+ codeDescDTO.getMethodName() + " ";
				tableJoinsList.add("dynamicFormFieldRefValueT"
						+ codeDescDTO.getFormId()
						+ codeDescDTO.getTablePosition()
						+ codeDescDTO.getMethodName());
				if (noOfTables <= 1) {

					from = from + " ON (dynamicFormTableRecord_"
							+ codeDescDTO.getFormId()
							+ codeDescDTO.getTablePosition() + "."
							+ codeDescDTO.getMethodName()
							+ " = dynamicFormFieldRefValueT"
							+ codeDescDTO.getFormId()
							+ codeDescDTO.getTablePosition()
							+ codeDescDTO.getMethodName()
							+ ".Field_Ref_Value_ID ) ";

				} else {
					from = from + " ON (dynamicFormTableRecord_"
							+ codeDescDTO.getFormId()
							+ codeDescDTO.getTablePosition() + "."
							+ codeDescDTO.getMethodName()
							+ " = dynamicFormFieldRefValueT"
							+ codeDescDTO.getFormId()
							+ codeDescDTO.getTablePosition()
							+ codeDescDTO.getMethodName()
							+ ".Field_Ref_Value_ID ) ";

				}

			} else {
				from = from
						+ "LEFT OUTER JOIN Dynamic_Form_Field_Ref_Value AS dynamicFormFieldRefValue"
						+ codeDescDTO.getFormId() + codeDescDTO.getMethodName()
						+ " ";
				tableJoinsList
						.add("dynamicFormFieldRefValue"
								+ codeDescDTO.getFormId()
								+ codeDescDTO.getMethodName());
				from = from + " ON (dynamicFormRecord"
						+ codeDescDTO.getFormId() + "."
						+ codeDescDTO.getMethodName()
						+ " = dynamicFormFieldRefValue"
						+ codeDescDTO.getFormId() + codeDescDTO.getMethodName()
						+ ".Field_Ref_Value_ID ) ";
			}

		}
		return from;
	}

	public String createCustomFieldWhere(long companyId,
			List<String> tableJoinsList, Map<String, List<Long>> paramValueMap) {
		String where = " WHERE company.Company_ID =:companyId ";

		where = where + " ORDER BY company.Company_ID DESC";

		return where;
	}

	@Override
	public List<PayAsiaCompanyStatisticReportDTO> getPayAsiaCompanyStatisticReport(
			final String asOnDate, final String dateFormat,
			final String companyIdList, final boolean isIncludeInactiveCompany) {
		final List<PayAsiaCompanyStatisticReportDTO> companyStatisticReportDTOs = new ArrayList<>();
		getSession().doWork(new Work() {
			@Override
			public void execute(Connection connection) throws SQLException {
				try {
					CallableStatement cstmt = null;
					cstmt = connection
							.prepareCall("{call PayAsia_Company_Statistic_Report (?,?,?)}");

					cstmt.setTimestamp("@As_On_Date",
							DateUtils.stringToTimestamp(asOnDate, dateFormat));
					cstmt.setString("@Company_ID_List", companyIdList);
					cstmt.setBoolean("@IsIncludeInactiveCompany",
							isIncludeInactiveCompany);

					ResultSet rs = cstmt.executeQuery();
					if (rs != null) {
						while (rs.next()) {
							PayAsiaCompanyStatisticReportDTO companyStatisticReportDTO = new PayAsiaCompanyStatisticReportDTO();
							companyStatisticReportDTO.setCompanyGroup(rs
									.getString("Group_Name"));
							companyStatisticReportDTO.setCompanyCode(rs
									.getString("Company_Code"));
							companyStatisticReportDTO.setCompanyName(rs
									.getString("Company_Name"));
							companyStatisticReportDTO.setEmpHeadCount(rs
									.getInt("Emp_Head_Count"));
							companyStatisticReportDTO.setNoOfUsersAllowToAccess(rs
									.getInt("Not_Resigned_Emp_Head_Count"));
							companyStatisticReportDTO.setActiveHeadCount(rs
									.getInt("Active_Emp_Head_Count"));
							// companyStatisticReportDTO.setHasPayslip(rs
							// .getString("Has_Payslip"));
							// companyStatisticReportDTO
							// .setNoOfMonthsPayslipUploaded(rs
							// .getInt("Month_Payslip_Uploaded"));
							companyStatisticReportDTO.setHasHrisModule(rs
									.getString("Has_HRIS"));
							companyStatisticReportDTO.setHasLeaveModule(rs
									.getString("Has_Leave"));
							companyStatisticReportDTO.setHasClaimModule(rs
									.getString("Has_Claim"));
							companyStatisticReportDTO.setHasMobileModule(rs
									.getString("Has_Mobile"));
							companyStatisticReportDTO.setNoOfUsersWithLeaveScheme(rs
									.getInt("Num_Of_Emp_With_Leave_Scheme"));
							companyStatisticReportDTO.setNoOfUsersWithClaimTemplate(rs
									.getInt("Num_Of_Emp_With_Claim_Template"));

							companyStatisticReportDTO
									.setNoOfUsersAsLeaveReviewerNotLeaveSchemeAssigned(rs
											.getInt("Num_Of_Emp_AS_Leave_Rev_With_NO_Leave_Scheme"));
							companyStatisticReportDTO
									.setNoOfUsersAsClaimReviewerNotClaimTemplateAssigned(rs
											.getInt("Num_Of_Emp_AS_Claim_Rev_With_NO_Claim_Template"));

							companyStatisticReportDTOs
									.add(companyStatisticReportDTO);
						}
					}
				} catch (SQLException e) {
					throw new PayAsiaSystemException(e.getMessage(), e);
				}
			}
		});
		return companyStatisticReportDTOs;
	}

	@Override
	public List<RolePrivilegeReportDTO> getEmployeeRolePrivilegeReport(
			final String companyIdList, final Long groupId) {
		final List<RolePrivilegeReportDTO> rolePrivilegeReportDTOs = new ArrayList<RolePrivilegeReportDTO>();

		getSession().doWork(new Work() {
			@Override
			public void execute(Connection connection) throws SQLException {
				try {
					CallableStatement cstmt = null;
					cstmt = connection
							.prepareCall("{call Get_Employee_List_with_Role_And_privilege (?,?)}");

					cstmt.setLong("@Group_Id", groupId);
					cstmt.setString("@Company_ID_List", companyIdList);

					ResultSet rs = cstmt.executeQuery();
					if (rs != null) {
						while (rs.next()) {
							RolePrivilegeReportDTO rolePrivilegeReportDTO = new RolePrivilegeReportDTO();
							rolePrivilegeReportDTO.setEmployeeNumber(rs
									.getString("EMPLOYEE_NUMBER"));
							rolePrivilegeReportDTO.setEmployeeName(rs
									.getString("EMPLOYEE_NAME"));
							rolePrivilegeReportDTO.setCompanyName(rs
									.getString("COMPANY_NAME"));
							rolePrivilegeReportDTO.setRole(rs
									.getString("ROLE_NAME"));
							rolePrivilegeReportDTO.setPrivilege(rs
									.getString("PRIVILEGE_DESC"));
							rolePrivilegeReportDTO.setModule(rs
									.getString("Module_Name"));

							rolePrivilegeReportDTOs.add(rolePrivilegeReportDTO);
						}
					}
				} catch (SQLException e) {
					throw new PayAsiaSystemException(e.getMessage(), e);
				}
			}
		});
		return rolePrivilegeReportDTOs;
	}

	@Override
	public void deleteCompanyProc(final Long companyId) {
		getSession().doWork(new Work() {
			@Override
			public void execute(Connection connection) throws SQLException {
				try {
					CallableStatement cstmt = null;
					cstmt = connection.prepareCall("{call Delete_Company (?)}");

					cstmt.setLong("@Company_ID", companyId);
					cstmt.execute();
				} catch (SQLException e) {
					throw new PayAsiaSystemException(e.getMessage(), e);
				}
			}
		});
	}

	@Override
	public List<ManageModuleDTO> findCompanyWithGroupAndModule(
			CompanyConditionDTO conditionDTO, PageRequest pageDTO,
			SortCondition sortDTO) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ManageModuleDTO> criteriaQuery = cb
				.createQuery(ManageModuleDTO.class);

		Root<Company> companyRoot = criteriaQuery.from(Company.class);

		Join<Company, CompanyGroup> companyCompanyGroupJoin = companyRoot
				.join(Company_.companyGroup);

		Join<Company, CompanyModuleMapping> companyModMappingJoin = companyRoot
				.join(Company_.companyModuleMappings, JoinType.LEFT);

		Join<CompanyModuleMapping, ModuleMaster> companyModuleMappingMasterJoin = companyModMappingJoin
				.join(CompanyModuleMapping_.moduleMaster, JoinType.LEFT);

		criteriaQuery.multiselect(companyRoot.get(Company_.companyName),
				companyRoot.get(Company_.companyCode),
				companyRoot.get(Company_.companyId),
				companyCompanyGroupJoin.get(CompanyGroup_.groupName),
				companyCompanyGroupJoin.get(CompanyGroup_.groupId),
				companyCompanyGroupJoin.get(CompanyGroup_.groupCode),
				companyModuleMappingMasterJoin.get(ModuleMaster_.moduleId),
				companyModuleMappingMasterJoin.get(ModuleMaster_.moduleName));

		// criteriaQuery.select(companyRoot);

		Predicate restriction = cb.conjunction();

		if (StringUtils.isNotBlank(conditionDTO.getCompanyCode())) {

			restriction = cb.and(restriction, cb.like(
					cb.upper(companyRoot.get(Company_.companyCode)),
					conditionDTO.getCompanyCode().toUpperCase()));
		}

		if (StringUtils.isNotBlank(conditionDTO.getCompanyName())) {

			restriction = cb.and(restriction, cb.like(
					cb.upper(companyRoot.get(Company_.companyName)),
					conditionDTO.getCompanyName().toUpperCase()));
		}

		if (StringUtils.isNotBlank(conditionDTO.getGroupName())) {

			restriction = cb.and(restriction, cb.like(
					cb.upper(companyCompanyGroupJoin
							.get(CompanyGroup_.groupName)), conditionDTO
									.getCompanyName().toUpperCase()));
		}

		criteriaQuery.where(restriction);

		criteriaQuery.orderBy(cb.asc(companyRoot.get(Company_.companyId)));

		/*
		 * if (sortDTO != null) {
		 * 
		 * Path<String> sortPath = getSortPathForSearchEmployee(sortDTO, companyRoot,
		 * companyCompanyGroupJoin, companyCountryMasterJoin,
		 * companyPayslipFrequencyJoin); if (sortPath != null) { if
		 * (SortConstants.DB_ORDER_BY_ASC.equalsIgnoreCase(sortDTO .getOrderType())) {
		 * criteriaQuery.orderBy(cb.asc(sortPath)); } if
		 * (SortConstants.DB_ORDER_BY_DESC.equalsIgnoreCase(sortDTO .getOrderType())) {
		 * criteriaQuery.orderBy(cb.desc(sortPath)); } }
		 * 
		 * }
		 */

		TypedQuery<ManageModuleDTO> companyTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			companyTypedQuery.setFirstResult(getStartPosition(pageDTO));
			companyTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		return companyTypedQuery.getResultList();
	}

	@Override
	public String getCompanyDefaultLanguage(Long companyCode) {
		String languageCode = null;
		try {
			CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
			CriteriaQuery<CompanyDefaultLanguageMapping> criteriaQuery = cb
					.createQuery(CompanyDefaultLanguageMapping.class);
			Root<CompanyDefaultLanguageMapping> companyDefaultLanguageRoot = criteriaQuery
					.from(CompanyDefaultLanguageMapping.class);
			criteriaQuery.select(companyDefaultLanguageRoot);
			criteriaQuery
					.where(cb.equal(companyDefaultLanguageRoot
							.get(CompanyDefaultLanguageMapping_.companyId),
							companyCode));
			CompanyDefaultLanguageMapping companyDefaultLanguage = entityManagerFactory
					.createQuery(criteriaQuery).getSingleResult();

			languageCode = companyDefaultLanguage.getLanguageMaster()
					.getLanguageCode();
		} catch (javax.persistence.NoResultException e) {
			languageCode = "en_US";
		}
		return languageCode;

	}

	@Override
	public Company findCompanyByEmpId(long empId) {
		String query = new StringBuilder().append("SELECT cmp FROM Employee emp INNER JOIN emp.company cmp")
				.append(" WHERE emp.employeeId = :empId").toString();
		TypedQuery<Company> tq = entityManagerFactory.createQuery(query, Company.class);
		tq.setParameter("empId", empId);
		return tq.getSingleResult();
	}

}
