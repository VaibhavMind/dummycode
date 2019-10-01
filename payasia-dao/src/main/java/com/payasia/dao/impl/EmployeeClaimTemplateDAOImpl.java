package com.payasia.dao.impl;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.hibernate.jdbc.Work;
import org.springframework.stereotype.Repository;

import com.payasia.common.dto.AddClaimDTO;
import com.payasia.common.dto.AssignClaimTemplateConditionDTO;
import com.payasia.common.dto.ClaimReviewerConditionDTO;
import com.payasia.common.dto.EmployeeClaimTemplateDataDTO;
import com.payasia.common.dto.EmployeeHeadCountReportDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.NullAwareBeanUtilsBean;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.SortConstants;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.EmployeeClaimTemplateDAO;
import com.payasia.dao.bean.ClaimItemMaster;
import com.payasia.dao.bean.ClaimItemMaster_;
import com.payasia.dao.bean.ClaimTemplate;
import com.payasia.dao.bean.ClaimTemplateItem;
import com.payasia.dao.bean.ClaimTemplateItem_;
import com.payasia.dao.bean.ClaimTemplate_;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeClaimTemplate;
import com.payasia.dao.bean.EmployeeClaimTemplateItem;
import com.payasia.dao.bean.EmployeeClaimTemplateItem_;
import com.payasia.dao.bean.EmployeeClaimTemplate_;
import com.payasia.dao.bean.Employee_;

@Repository
public class EmployeeClaimTemplateDAOImpl extends BaseDAO implements EmployeeClaimTemplateDAO {

	@Override
	protected Object getBaseEntity() {
		EmployeeClaimTemplate employeeClaimTemplate = new EmployeeClaimTemplate();
		return employeeClaimTemplate;
	}

	@Override
	public void update(EmployeeClaimTemplate employeeClaimTemplate) {
		super.update(employeeClaimTemplate);

	}

	@Override
	public void save(EmployeeClaimTemplate employeeClaimTemplate) {
		super.save(employeeClaimTemplate);
	}

	@Override
	public void delete(EmployeeClaimTemplate employeeClaimTemplate) {
		super.delete(employeeClaimTemplate);

	}

	@Override
	public EmployeeClaimTemplate findByID(long employeeClaimTemplateId) {
		return super.findById(EmployeeClaimTemplate.class, employeeClaimTemplateId);
	}

	@Override
	public EmployeeClaimTemplate saveReturn(EmployeeClaimTemplate employeeClaimTemplate) {

		EmployeeClaimTemplate persistObj = employeeClaimTemplate;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (EmployeeClaimTemplate) getBaseEntity();
			beanUtil.copyProperties(persistObj, employeeClaimTemplate);
		} catch (IllegalAccessException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		} catch (InvocationTargetException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		}
		this.entityManagerFactory.persist(persistObj);
		return persistObj;
	}

	@Override
	public EmployeeClaimTemplate findByEmpIdAndEndDate(Long employeeId, Long claimTemplateId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeClaimTemplate> criteriaQuery = cb.createQuery(EmployeeClaimTemplate.class);
		Root<EmployeeClaimTemplate> empRoot = criteriaQuery.from(EmployeeClaimTemplate.class);
		criteriaQuery.select(empRoot);

		Join<EmployeeClaimTemplate, Employee> empJoin = empRoot.join(EmployeeClaimTemplate_.employee);
		Join<EmployeeClaimTemplate, ClaimTemplate> claimTemplateJoin = empRoot
				.join(EmployeeClaimTemplate_.claimTemplate);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(empJoin.get(Employee_.employeeId), employeeId));
		restriction = cb.and(restriction,
				cb.equal(claimTemplateJoin.get(ClaimTemplate_.claimTemplateId), claimTemplateId));
		restriction = cb.and(restriction, cb.isNull(empRoot.get(EmployeeClaimTemplate_.endDate)));

		criteriaQuery.where(restriction);

		TypedQuery<EmployeeClaimTemplate> empTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<EmployeeClaimTemplate> empList = empTypedQuery.getResultList();
		if (empList != null && !empList.isEmpty()) {
			return empList.get(0);
		}
		return null;

	}

	@Override
	public List<EmployeeClaimTemplate> findByCondition(AssignClaimTemplateConditionDTO conditionDTO,
			PageRequest pageDTO, SortCondition sortDTO, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeClaimTemplate> criteriaQuery = cb.createQuery(EmployeeClaimTemplate.class);
		Root<EmployeeClaimTemplate> empRoot = criteriaQuery.from(EmployeeClaimTemplate.class);
		criteriaQuery.select(empRoot);

		Join<EmployeeClaimTemplate, Employee> empJoin = empRoot.join(EmployeeClaimTemplate_.employee);
		Join<Employee, Company> empCompanyJoin = empJoin.join(Employee_.company);

		Join<EmployeeClaimTemplate, ClaimTemplate> claimTemplateJoin = empRoot
				.join(EmployeeClaimTemplate_.claimTemplate);
		Join<ClaimTemplate, Company> claimTemplateCompanyJoin = claimTemplateJoin.join(ClaimTemplate_.company);

		Predicate restriction = cb.conjunction();

		if (StringUtils.isNotBlank(conditionDTO.getEmployeeNumber())) {

			restriction = cb.and(restriction, cb.like(cb.upper(empJoin.get(Employee_.employeeNumber)),
					conditionDTO.getEmployeeNumber().toUpperCase()));
		}

		if (StringUtils.isNotBlank(conditionDTO.getEmployeeName())) {

			restriction = cb.and(restriction, cb.or(
					cb.like(cb.upper(empJoin.get(Employee_.firstName)), conditionDTO.getEmployeeName().toUpperCase()),
					cb.like(cb.upper(empJoin.get(Employee_.lastName)), conditionDTO.getEmployeeName().toUpperCase())));
		}

		if (StringUtils.isNotBlank(conditionDTO.getClaimTemplateName())) {

			restriction = cb.and(restriction, cb.like(cb.upper(claimTemplateJoin.get(ClaimTemplate_.templateName)),
					conditionDTO.getClaimTemplateName().toUpperCase()));
		}

		if (conditionDTO.getFromDate() != null) {
			restriction = cb.and(restriction, cb.greaterThanOrEqualTo((empRoot.get(EmployeeClaimTemplate_.startDate)),
					conditionDTO.getFromDate()));
		}
		if (conditionDTO.getToDate() != null) {
			restriction = cb.and(restriction,
					cb.lessThanOrEqualTo((empRoot.get(EmployeeClaimTemplate_.endDate)), conditionDTO.getToDate()));
		}

		restriction = cb.and(restriction, cb.equal((empCompanyJoin.get(Company_.companyId)), companyId));
		restriction = cb.and(restriction, cb.equal((claimTemplateCompanyJoin.get(Company_.companyId)), companyId));

		criteriaQuery.where(restriction);

		if (sortDTO != null) {

			Path<String> sortPath = getSortPathForSearchEmployee(sortDTO, empRoot, empJoin, claimTemplateJoin);
			if (sortPath != null) {
				if (SortConstants.DB_ORDER_BY_ASC.equalsIgnoreCase(sortDTO.getOrderType())) {
					criteriaQuery.orderBy(cb.asc(sortPath));
				}
				if (SortConstants.DB_ORDER_BY_DESC.equalsIgnoreCase(sortDTO.getOrderType())) {
					criteriaQuery.orderBy(cb.desc(sortPath));
				}
			}

		}

		TypedQuery<EmployeeClaimTemplate> empTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			empTypedQuery.setFirstResult(getStartPosition(pageDTO));
			empTypedQuery.setMaxResults(pageDTO.getPageSize());
		}
		List<EmployeeClaimTemplate> employeeClaimTemplateList = empTypedQuery.getResultList();
		return employeeClaimTemplateList;
	}

	@Override
	public Long getCountForCondition(AssignClaimTemplateConditionDTO conditionDTO, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
		Root<EmployeeClaimTemplate> empRoot = criteriaQuery.from(EmployeeClaimTemplate.class);
		criteriaQuery.select(cb.count(empRoot));

		Join<EmployeeClaimTemplate, Employee> empJoin = empRoot.join(EmployeeClaimTemplate_.employee);
		Join<Employee, Company> empCompanyJoin = empJoin.join(Employee_.company);

		Join<EmployeeClaimTemplate, ClaimTemplate> claimTemplateJoin = empRoot
				.join(EmployeeClaimTemplate_.claimTemplate);
		Join<ClaimTemplate, Company> claimTemplateCompanyJoin = claimTemplateJoin.join(ClaimTemplate_.company);

		Predicate restriction = cb.conjunction();

		if (StringUtils.isNotBlank(conditionDTO.getEmployeeNumber())) {

			restriction = cb.and(restriction, cb.like(cb.upper(empJoin.get(Employee_.employeeNumber)),
					conditionDTO.getEmployeeNumber().toUpperCase()));
		}

		if (StringUtils.isNotBlank(conditionDTO.getEmployeeName())) {

			restriction = cb.and(restriction, cb.or(
					cb.like(cb.upper(empJoin.get(Employee_.firstName)), conditionDTO.getEmployeeName().toUpperCase()),
					cb.like(cb.upper(empJoin.get(Employee_.lastName)), conditionDTO.getEmployeeName().toUpperCase())));
		}

		if (StringUtils.isNotBlank(conditionDTO.getClaimTemplateName())) {

			restriction = cb.and(restriction, cb.like(cb.upper(claimTemplateJoin.get(ClaimTemplate_.templateName)),
					conditionDTO.getClaimTemplateName().toUpperCase()));
		}

		if (conditionDTO.getFromDate() != null) {
			restriction = cb.and(restriction, cb.greaterThanOrEqualTo((empRoot.get(EmployeeClaimTemplate_.startDate)),
					conditionDTO.getFromDate()));
		}
		if (conditionDTO.getToDate() != null) {
			restriction = cb.and(restriction,
					cb.lessThanOrEqualTo((empRoot.get(EmployeeClaimTemplate_.endDate)), conditionDTO.getToDate()));
		}

		restriction = cb.and(restriction, cb.equal((empCompanyJoin.get(Company_.companyId)), companyId));
		restriction = cb.and(restriction, cb.equal((claimTemplateCompanyJoin.get(Company_.companyId)), companyId));

		criteriaQuery.where(restriction);

		TypedQuery<Long> empTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		return empTypedQuery.getSingleResult();
	}

	@Override
	public Path<String> getSortPathForSearchEmployee(SortCondition sortDTO, Root<EmployeeClaimTemplate> empRoot,
			Join<EmployeeClaimTemplate, Employee> empJoin,
			Join<EmployeeClaimTemplate, ClaimTemplate> claimTemplateJoin) {

		List<String> employeeIsColList = new ArrayList<String>();
		employeeIsColList.add(SortConstants.ASSIGN_CLAIM_TEMPLATE_EMPLOYEE_NAME);
		employeeIsColList.add(SortConstants.ASSIGN_CLAIM_TEMPLATE_EMPLOYEE_NUMBER);

		List<String> empLeaveSchemeColList = new ArrayList<String>();
		empLeaveSchemeColList.add(SortConstants.ASSIGN_CLAIM_TEMPLATE_ACTIVE);
		empLeaveSchemeColList.add(SortConstants.ASSIGN_CLAIM_TEMPLATE_FROM_DATE);
		empLeaveSchemeColList.add(SortConstants.ASSIGN_CLAIM_TEMPLATE_TO_DATE);

		List<String> leaveSchemeIsColList = new ArrayList<String>();
		leaveSchemeIsColList.add(SortConstants.ASSIGN_CLAIM_TEMPLATE_CLAIM_ITEM);

		Path<String> sortPath = null;

		if (employeeIsColList.contains(sortDTO.getColumnName())) {
			sortPath = empJoin.get(colMap.get(Employee.class + sortDTO.getColumnName()));
		}
		if (empLeaveSchemeColList.contains(sortDTO.getColumnName())) {
			sortPath = empRoot.get(colMap.get(EmployeeClaimTemplate.class + sortDTO.getColumnName()));
		}
		if (leaveSchemeIsColList.contains(sortDTO.getColumnName())) {
			sortPath = claimTemplateJoin.get(colMap.get(ClaimTemplate.class + sortDTO.getColumnName()));
		}
		return sortPath;
	}

	@Override
	public EmployeeClaimTemplate checkEmpClaimTemplateDate(Long employeeClaimTemplateId, Long employeeId, String date,
			String dateFormat) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeClaimTemplate> criteriaQuery = cb.createQuery(EmployeeClaimTemplate.class);
		Root<EmployeeClaimTemplate> empRoot = criteriaQuery.from(EmployeeClaimTemplate.class);
		criteriaQuery.select(empRoot);

		Join<EmployeeClaimTemplate, Employee> empLeaveSchemeJoin = empRoot.join(EmployeeClaimTemplate_.employee);

		Predicate restriction = cb.conjunction();
		if (employeeClaimTemplateId != null) {
			restriction = cb.and(restriction,
					cb.notEqual(empRoot.get(EmployeeClaimTemplate_.employeeClaimTemplateId), employeeClaimTemplateId));
		}

		Calendar dateForNull = new GregorianCalendar(PayAsiaConstants.LAST_YEAR, PayAsiaConstants.LAST_MONTH,
				PayAsiaConstants.LAST_DAY);
		Timestamp timeStampForNull = new Timestamp(dateForNull.getTimeInMillis());

		restriction = cb.and(restriction, cb.equal(empLeaveSchemeJoin.get(Employee_.employeeId), employeeId));
		restriction = cb.and(restriction,

				restriction = cb.and(
						cb.and(restriction,
								cb.lessThanOrEqualTo(empRoot.get(EmployeeClaimTemplate_.startDate),
										DateUtils.stringToTimestamp(date, dateFormat))),
						cb.and(restriction,
								cb.greaterThanOrEqualTo(
										cb.coalesce(empRoot.get(EmployeeClaimTemplate_.endDate), timeStampForNull),
										DateUtils.stringToTimestamp(date, dateFormat)))));
		criteriaQuery.where(restriction);

		TypedQuery<EmployeeClaimTemplate> empTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<EmployeeClaimTemplate> empList = empTypedQuery.getResultList();
		if (empList != null && !empList.isEmpty()) {
			return empList.get(0);
		}
		return null;

	}

	@SuppressWarnings("unchecked")
	@Override
	public EmployeeClaimTemplate checkEmpClaimTemplateOverlap(Long claimTemplateId, Long employeeId, String startDate,
			String endDate, String dateFormat, Long employeeClaimTemplateId) {

		String queryString = "select ECT FROM EmployeeClaimTemplate ECT WHERE ECT.claimTemplate.claimTemplateId = :claimTemplateId AND ECT.employee.employeeId = :employeeId ";
		queryString += "and ((ECT.startDate between :startDate and :endDate) or (ECT.startDate <= :startDate and ISNULL(ECT.endDate,'2999-12-31') >= :endDate ) or (ISNULL(ECT.endDate,'2999-12-31') between :startDate and :endDate)  )";
		if (employeeClaimTemplateId != null) {
			queryString += "and ECT.employeeClaimTemplateId != :employeeClaimTemplateId";
		}

		Calendar dateForNull = new GregorianCalendar(PayAsiaConstants.LAST_YEAR, PayAsiaConstants.LAST_MONTH,
				PayAsiaConstants.LAST_DAY);
		Timestamp timeStampForNull = new Timestamp(dateForNull.getTimeInMillis());

		Query query = entityManagerFactory.createQuery(queryString);
		query.setParameter("claimTemplateId", claimTemplateId);
		if (employeeClaimTemplateId != null) {
			query.setParameter("employeeClaimTemplateId", employeeClaimTemplateId);
		}
		query.setParameter("employeeId", employeeId);
		query.setParameter("startDate", DateUtils.stringToTimestamp(startDate, dateFormat));
		query.setParameter("endDate",
				(endDate != null && !endDate.equals("")) ? DateUtils.stringToTimestamp(endDate, dateFormat) : timeStampForNull);

		List<EmployeeClaimTemplate> empList = query.getResultList();
		if (empList != null && !empList.isEmpty()) {
			return empList.get(0);
		}
		return null;

	}

	@Override
	public List<EmployeeClaimTemplate> findByCondition(ClaimReviewerConditionDTO claimReviewerConditionDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeClaimTemplate> criteriaQuery = cb.createQuery(EmployeeClaimTemplate.class);
		Root<EmployeeClaimTemplate> empRoot = criteriaQuery.from(EmployeeClaimTemplate.class);
		criteriaQuery.select(empRoot);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(empRoot.get(EmployeeClaimTemplate_.employee).get("employeeId").as(Long.class),
						claimReviewerConditionDTO.getEmployeeId()));

		criteriaQuery.where(restriction);

		TypedQuery<EmployeeClaimTemplate> empTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<EmployeeClaimTemplate> empList = empTypedQuery.getResultList();

		return empList;

	}

	@Override
	public List<EmployeeClaimTemplate> checkEmpClaimTemplateByDate(Long empClaimTemplateId, Long employeeId,
			String date, String dateFormat) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeClaimTemplate> criteriaQuery = cb.createQuery(EmployeeClaimTemplate.class);
		Root<EmployeeClaimTemplate> empRoot = criteriaQuery.from(EmployeeClaimTemplate.class);
		criteriaQuery.select(empRoot).distinct(true);

		Join<EmployeeClaimTemplate, Employee> employeeClaimTemplateJoin = empRoot.join(EmployeeClaimTemplate_.employee);

		Join<EmployeeClaimTemplate, EmployeeClaimTemplateItem> employeeClaimTemplateItemJoin = empRoot
				.join(EmployeeClaimTemplate_.employeeClaimTemplateItems);

		Join<EmployeeClaimTemplate, ClaimTemplate> claimTemplateJoin = empRoot
				.join(EmployeeClaimTemplate_.claimTemplate);

		Predicate restriction = cb.conjunction();
		if (empClaimTemplateId != null) {

		}

		restriction = cb.and(restriction,
				cb.equal(employeeClaimTemplateItemJoin.get(EmployeeClaimTemplateItem_.active), true));

		restriction = cb.and(restriction, cb.equal(claimTemplateJoin.get(ClaimTemplate_.visibility), true));

		Calendar dateForNull = new GregorianCalendar(PayAsiaConstants.LAST_YEAR, PayAsiaConstants.LAST_MONTH,
				PayAsiaConstants.LAST_DAY);
		Timestamp timeStampForNull = new Timestamp(dateForNull.getTimeInMillis());

		restriction = cb.and(restriction, cb.equal(employeeClaimTemplateJoin.get(Employee_.employeeId), employeeId));
		restriction = cb.and(restriction,

				restriction = cb.and(
						cb.and(restriction,
								cb.lessThanOrEqualTo(empRoot.get(EmployeeClaimTemplate_.startDate),
										DateUtils.stringToTimestamp(date, dateFormat))),
						cb.and(restriction,
								cb.greaterThanOrEqualTo(
										cb.coalesce(empRoot.get(EmployeeClaimTemplate_.endDate), timeStampForNull),
										DateUtils.stringToTimestamp(date, dateFormat)))));
		criteriaQuery.where(restriction);

		TypedQuery<EmployeeClaimTemplate> empTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<EmployeeClaimTemplate> empList = empTypedQuery.getResultList();

		return empList;

	}

	@Override
	public EmployeeClaimTemplate checkEmployeeClaimTemplateyName(String claimTemplateName, Long employeeId, String date,
			String dateFormat) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeClaimTemplate> criteriaQuery = cb.createQuery(EmployeeClaimTemplate.class);
		Root<EmployeeClaimTemplate> employeeClaimTemplateRoot = criteriaQuery.from(EmployeeClaimTemplate.class);
		criteriaQuery.select(employeeClaimTemplateRoot);

		Join<EmployeeClaimTemplate, Employee> employeeClaimTemplateJoin = employeeClaimTemplateRoot
				.join(EmployeeClaimTemplate_.employee);

		Join<EmployeeClaimTemplate, ClaimTemplate> claimTemplateJoin = employeeClaimTemplateRoot
				.join(EmployeeClaimTemplate_.claimTemplate);

		Predicate restriction = cb.conjunction();

		Calendar dateForNull = new GregorianCalendar(PayAsiaConstants.LAST_YEAR, PayAsiaConstants.LAST_MONTH,
				PayAsiaConstants.LAST_DAY);
		Timestamp timeStampForNull = new Timestamp(dateForNull.getTimeInMillis());

		restriction = cb.and(restriction, cb.equal(employeeClaimTemplateJoin.get(Employee_.employeeId), employeeId));

		restriction = cb.and(restriction,
				cb.equal(claimTemplateJoin.get(ClaimTemplate_.templateName), claimTemplateName));

		restriction = cb.and(restriction,

				restriction = cb
						.and(cb.and(restriction,
								cb.lessThanOrEqualTo(employeeClaimTemplateRoot.get(EmployeeClaimTemplate_.startDate),
										DateUtils.stringToTimestamp(date, dateFormat))),
								cb.and(restriction,
										cb.greaterThanOrEqualTo(cb.coalesce(
												employeeClaimTemplateRoot.get(EmployeeClaimTemplate_.endDate),
												timeStampForNull), DateUtils.stringToTimestamp(date, dateFormat)))));
		criteriaQuery.where(restriction);

		TypedQuery<EmployeeClaimTemplate> empTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		if (empTypedQuery.getResultList().size() > 0) {
			EmployeeClaimTemplate employeeClaimTemplate = empTypedQuery.getResultList().get(0);

			return employeeClaimTemplate;
		}
		return null;

	}

	@Override
	public EmployeeClaimTemplate checkEmployeeClaimTemplateByDate(Long empClaimTemplateId, Long employeeId, String date,
			String dateFormat) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeClaimTemplate> criteriaQuery = cb.createQuery(EmployeeClaimTemplate.class);
		Root<EmployeeClaimTemplate> empRoot = criteriaQuery.from(EmployeeClaimTemplate.class);
		criteriaQuery.select(empRoot);

		Join<EmployeeClaimTemplate, Employee> empClaimTemplateJoin = empRoot.join(EmployeeClaimTemplate_.employee);

		Join<EmployeeClaimTemplate, EmployeeClaimTemplateItem> employeeClaimTemplateItemJoin = empRoot
				.join(EmployeeClaimTemplate_.employeeClaimTemplateItems);

		Join<EmployeeClaimTemplateItem, ClaimTemplateItem> claimTemplateItemJoin = employeeClaimTemplateItemJoin
				.join(EmployeeClaimTemplateItem_.claimTemplateItem);

		Join<ClaimTemplateItem, ClaimItemMaster> claimItemMasterJoin = claimTemplateItemJoin
				.join(ClaimTemplateItem_.claimItemMaster);

		Predicate restriction = cb.conjunction();
		if (empClaimTemplateId != null) {
			restriction = cb.and(restriction,
					cb.notEqual(empRoot.get(EmployeeClaimTemplate_.employeeClaimTemplateId), empClaimTemplateId));
		}

		Calendar dateForNull = new GregorianCalendar(PayAsiaConstants.LAST_YEAR, PayAsiaConstants.LAST_MONTH,
				PayAsiaConstants.LAST_DAY);
		Timestamp timeStampForNull = new Timestamp(dateForNull.getTimeInMillis());

		restriction = cb.and(restriction, cb.equal(empClaimTemplateJoin.get(Employee_.employeeId), employeeId));

		restriction = cb.and(restriction,

				restriction = cb.and(
						cb.and(restriction,
								cb.lessThanOrEqualTo(empRoot.get(EmployeeClaimTemplate_.startDate),
										DateUtils.stringToTimestamp(date, dateFormat))),
						cb.and(restriction,
								cb.greaterThanOrEqualTo(
										cb.coalesce(empRoot.get(EmployeeClaimTemplate_.endDate), timeStampForNull),
										DateUtils.stringToTimestamp(date, dateFormat)))));
		criteriaQuery.where(restriction);

		criteriaQuery.orderBy(cb.asc(claimItemMasterJoin.get(ClaimItemMaster_.sortOrder)));
		TypedQuery<EmployeeClaimTemplate> empTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<EmployeeClaimTemplate> empList = empTypedQuery.getResultList();
		if (empList != null && !empList.isEmpty()) {
			return empList.get(0);
		}
		return null;

	}

	@Override
	public List<Long> getEmployeesOfClaimTemplate(Long claimTemplateId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
		Root<EmployeeClaimTemplate> employeeClaimTemplateRoot = criteriaQuery.from(EmployeeClaimTemplate.class);
		criteriaQuery.select(
				employeeClaimTemplateRoot.get(EmployeeClaimTemplate_.employee).get("employeeId").as(Long.class));

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal((employeeClaimTemplateRoot.get(EmployeeClaimTemplate_.claimTemplate)
				.get("claimTemplateId").as(Long.class)), claimTemplateId));

		Calendar dateForNull = new GregorianCalendar(PayAsiaConstants.LAST_YEAR, PayAsiaConstants.LAST_MONTH,
				PayAsiaConstants.LAST_DAY);
		Timestamp timeStampForNull = new Timestamp(dateForNull.getTimeInMillis());
		Calendar cal = Calendar.getInstance();
		restriction = cb.and(restriction,

				restriction = cb
						.and(cb.and(restriction,
								cb.lessThanOrEqualTo(employeeClaimTemplateRoot.get(EmployeeClaimTemplate_.startDate),
										DateUtils.convertDateToTimeStamp(cal.getTime()))),
								cb.and(restriction,
										cb.greaterThanOrEqualTo(cb.coalesce(
												employeeClaimTemplateRoot.get(EmployeeClaimTemplate_.endDate),
												timeStampForNull), DateUtils.convertDateToTimeStamp(cal.getTime())))));

		criteriaQuery.where(restriction);
		TypedQuery<Long> typedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<Long> employeeIdList = typedQuery.getResultList();
		return employeeIdList;
	}

	@Override
	public EmployeeClaimTemplate findByEmpIdAndClaimTemplateId(Long employeeId, Long claimTemplateId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeClaimTemplate> criteriaQuery = cb.createQuery(EmployeeClaimTemplate.class);
		Root<EmployeeClaimTemplate> employeeClaimTemplateRoot = criteriaQuery.from(EmployeeClaimTemplate.class);
		criteriaQuery.select(employeeClaimTemplateRoot);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(
						employeeClaimTemplateRoot.get(EmployeeClaimTemplate_.employee).get("employeeId").as(Long.class),
						employeeId));

		restriction = cb.and(restriction, cb.equal(employeeClaimTemplateRoot.get(EmployeeClaimTemplate_.claimTemplate)
				.get("claimTemplateId").as(Long.class), claimTemplateId));

		criteriaQuery.where(restriction);

		TypedQuery<EmployeeClaimTemplate> empTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<EmployeeClaimTemplate> empList = empTypedQuery.getResultList();
		if (empList != null && !empList.isEmpty()) {
			return empList.get(0);
		}
		return null;

	}

	@Override
	public List<EmployeeClaimTemplate> findByClaimTemplate(long claimTemplateId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeClaimTemplate> criteriaQuery = cb.createQuery(EmployeeClaimTemplate.class);
		Root<EmployeeClaimTemplate> employeeClaimTemplateRoot = criteriaQuery.from(EmployeeClaimTemplate.class);
		criteriaQuery.select(employeeClaimTemplateRoot);

		Join<EmployeeClaimTemplate, ClaimTemplate> claimTemplateJoin = employeeClaimTemplateRoot
				.join(EmployeeClaimTemplate_.claimTemplate);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal((claimTemplateJoin.get(ClaimTemplate_.claimTemplateId)), claimTemplateId));

		criteriaQuery.where(restriction);

		TypedQuery<EmployeeClaimTemplate> empLeaveSchQuery = entityManagerFactory.createQuery(criteriaQuery);
		List<EmployeeClaimTemplate> employeeClaimTemplateList = empLeaveSchQuery.getResultList();
		return employeeClaimTemplateList;
	}

	@Override
	public List<EmployeeHeadCountReportDTO> getClaimHeadCountReportDetail(final String startDate, final String endDate,
			final String dateFormat, final String companyIdList) {
		final List<EmployeeHeadCountReportDTO> employeeHeadCountReportDTOs = new ArrayList<>();
		getSession().doWork(new Work() {
			@Override
			public void execute(Connection connection) throws SQLException {
				try {
					CallableStatement cstmt = null;
					cstmt = connection.prepareCall("{call Claim_Head_Count_Report (?,?,?)}");

					cstmt.setTimestamp("@From_Date", DateUtils.stringToTimestamp(startDate, dateFormat));
					cstmt.setTimestamp("@To_Date", DateUtils.stringToTimestamp(endDate, dateFormat));
					cstmt.setString("@Company_ID_List", companyIdList);

					ResultSet rs = cstmt.executeQuery();
					if (rs != null) {
						while (rs.next()) {
							EmployeeHeadCountReportDTO empHeadCountDto = new EmployeeHeadCountReportDTO();
							empHeadCountDto.setEmployeeId(rs.getLong("Employee_ID"));
							empHeadCountDto.setEmployeeNumber(rs.getString("Employee_Number"));
							empHeadCountDto.setFirstName(rs.getString("First_Name"));
							empHeadCountDto.setLastName(rs.getString("Last_Name"));
							empHeadCountDto.setClaimTemplateName(rs.getString("Claim_Template_Name"));
							empHeadCountDto.setIsReviewer(rs.getString("Is_Claim_Reviewer"));
							if (rs.getTimestamp("Original_Hire_Date") != null) {
								empHeadCountDto.setOriginalHireDate(
										DateUtils.timeStampToString(rs.getTimestamp("Original_Hire_Date"), dateFormat));
							}
							if (rs.getTimestamp("Resignation_Date") != null) {
								empHeadCountDto.setResignationDate(
										DateUtils.timeStampToString(rs.getTimestamp("Resignation_Date"), dateFormat));
							}
							empHeadCountDto.setCompanyCode(rs.getString("Company_Code"));
							empHeadCountDto.setCompanyName(rs.getString("Company_Name"));
							empHeadCountDto.setCompanyId(rs.getLong("Company_ID"));
							empHeadCountDto.setCountryName(rs.getString("Country_Name"));

							employeeHeadCountReportDTOs.add(empHeadCountDto);
						}
					}
				} catch (SQLException e) {
					throw new PayAsiaSystemException(e.getMessage(), e);
				}
			}
		});
		return employeeHeadCountReportDTOs;
	}

	@Override
	public BigDecimal getTotalBalance(Long claimTemplateItemId, Long employeeId) {

		String queryString = "select SUM(CAI.Claim_Amount) from Employee_Claim_Template as ECT join Employee_Claim_Template_Item as ECTI on ECT.Employee_Claim_Template_ID = ECTI.Employee_Claim_Template_ID";
		queryString += " join Claim_Application_Item as CAI on ECTI.Employee_Claim_Template_Item_ID=CAI.Employee_Claim_Template_Item_ID";
		queryString += " join  Claim_Application as CA on CA.Claim_Application_ID=CAI.Claim_Application_ID";

		queryString += " where ECTI.Claim_Template_Item_ID=:claimTemplateItemId and ECT.Employee_ID=:employeeId and CA.Claim_Status_ID IN (1,5,6,3) and CAI.Claim_Date > ECT.Start_Date";

		Query query1 = entityManagerFactory.createNativeQuery(queryString);
		query1.setParameter("claimTemplateItemId", claimTemplateItemId);
		query1.setParameter("employeeId", employeeId);

		BigDecimal amount = (BigDecimal) query1.getSingleResult();
		return amount;
	}

	@Override
	public EmployeeClaimTemplate findByEmployeeClaimTemplateID(AddClaimDTO addClaimDTO)
	{
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeClaimTemplate> criteriaQuery = cb.createQuery(EmployeeClaimTemplate.class);
		Root<EmployeeClaimTemplate> employeeClaimTemplateRoot = criteriaQuery.from(EmployeeClaimTemplate.class);
		criteriaQuery.select(employeeClaimTemplateRoot);
		
		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(employeeClaimTemplateRoot.get(EmployeeClaimTemplate_.employeeClaimTemplateId),
						addClaimDTO.getEmployeeClaimTemplateId()));
		
		if(!addClaimDTO.getAdmin()){
		Join<EmployeeClaimTemplate, Employee> employeeJoin = employeeClaimTemplateRoot
				.join(EmployeeClaimTemplate_.employee);
		restriction = cb.and(restriction,
				cb.equal(employeeJoin.get(Employee_.employeeId), addClaimDTO.getEmployeeId()));
		}
		
		restriction = cb.and(restriction,
				cb.equal(employeeClaimTemplateRoot.get(EmployeeClaimTemplate_.companyId), addClaimDTO.getCompanyId()));

		criteriaQuery.where(restriction);

		TypedQuery<EmployeeClaimTemplate> typedQuery = entityManagerFactory.createQuery(criteriaQuery);
		List<EmployeeClaimTemplate> list = typedQuery.getResultList();
		return list != null && !list.isEmpty() ? list.get(0) : null;
	}

	/*
	 * 	NEW METHOD FOR CLAIM TEMPLATE DATA
	 */
	@Override
	public List<EmployeeClaimTemplateDataDTO> getEmployeeTemplateData(final Long companyId, final Long employeeId) {
		final List<EmployeeClaimTemplateDataDTO> employeeClaimTemplateDataDTOList = new ArrayList<>();

		getSession().doWork(new Work() {
			@Override
			public void execute(Connection connection) throws SQLException {
				try {
					CallableStatement cstmt = null;
					cstmt = connection.prepareCall("{call Get_Employee_All_Claim_Template_Data(?,?)}");	
					cstmt.setLong("@EmployeeId", employeeId);
					cstmt.setLong("@CompanyId", companyId);
					ResultSet rs = cstmt.executeQuery();
					if (rs != null) {
						while (rs.next()) {
							EmployeeClaimTemplateDataDTO employeeClaimTemplateDataDTO = new EmployeeClaimTemplateDataDTO();

							employeeClaimTemplateDataDTO.setEmployeeClaimTemplateId(FormatPreserveCryptoUtil.encrypt(rs.getLong("Employee_Claim_Template_ID")));
							employeeClaimTemplateDataDTO.setClaimTemplateId(rs.getLong("Claim_Template_ID"));
							employeeClaimTemplateDataDTO.setTemplateName(rs.getString("Template_Name"));
							employeeClaimTemplateDataDTO.setTotalClaimEntitlement(rs.getFloat("Total_Entitlement"));
							employeeClaimTemplateDataDTO.setEntitlementType(rs.getString("Entitlement_Type"));
							employeeClaimTemplateDataDTO.setUsedClaimEntitlement(rs.getFloat("Used_Entitlement"));
							employeeClaimTemplateDataDTO.setLeftClaimEntitlement(rs.getFloat("Remaining_Entitlement"));
							//employeeClaimTemplateDataDTO.setEmployeeId(rs.getLong("Employee_ID"));
							
							employeeClaimTemplateDataDTOList.add(employeeClaimTemplateDataDTO);
						}
					}
				} catch (SQLException e) {
					throw new PayAsiaSystemException(e.getMessage(), e);
				}

			}
		});

		return employeeClaimTemplateDataDTOList;
	}
}
