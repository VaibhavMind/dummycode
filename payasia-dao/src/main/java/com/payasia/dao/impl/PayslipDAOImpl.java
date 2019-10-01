package com.payasia.dao.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.payasia.common.dto.DataImportKeyValueDTO;
import com.payasia.common.dto.ExcelExportQueryDTO;
import com.payasia.common.dto.PayslipConditionDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.ExcelExportFiltersForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.NullAwareBeanUtilsBean;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.SortConstants;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.PayslipDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.Employee_;
import com.payasia.dao.bean.MonthMaster;
import com.payasia.dao.bean.MonthMaster_;
import com.payasia.dao.bean.Payslip;
import com.payasia.dao.bean.PayslipFrequency;
import com.payasia.dao.bean.PayslipFrequency_;
import com.payasia.dao.bean.Payslip_;

/**
 * The Class PayslipDAOImpl.
 */
@Repository
public class PayslipDAOImpl extends BaseDAO implements PayslipDAO {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.BaseDAO#getBaseEntity()
	 */
	@Override
	protected Object getBaseEntity() {
		return new Payslip();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.PayslipDAO#save(com.payasia.dao.bean.Payslip)
	 */
	@Override
	public void save(Payslip payslip) {
		super.save(payslip);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.PayslipDAO#update(com.payasia.dao.bean.Payslip)
	 */
	@Override
	public void update(Payslip payslip) {
		super.update(payslip);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.PayslipDAO#delete(com.payasia.dao.bean.Payslip)
	 */
	@Override
	public void delete(Payslip payslip) {
		super.delete(payslip);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.PayslipDAO#findByEmployee(java.lang.Long)
	 */
	@Override
	public List<Payslip> findByEmployee(Long empId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Payslip> criteriaQuery = cb.createQuery(Payslip.class);
		Root<Payslip> payslipRoot = criteriaQuery.from(Payslip.class);

		Join<Payslip, Employee> empJoin = payslipRoot.join(Payslip_.employee);

		criteriaQuery.select(payslipRoot);
		criteriaQuery.where(cb.equal(empJoin.get(Employee_.employeeId), empId));

		TypedQuery<Payslip> payslipQuery = entityManagerFactory.createQuery(criteriaQuery);

		return payslipQuery.getResultList();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.PayslipDAO#findAll(long)
	 */
	@Override
	public List<Payslip> findAll(long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Payslip> criteriaQuery = cb.createQuery(Payslip.class);
		Root<Payslip> payslipRoot = criteriaQuery.from(Payslip.class);

		Join<Payslip, Company> compJoin = payslipRoot.join(Payslip_.company);

		criteriaQuery.select(payslipRoot);
		criteriaQuery.where(cb.equal(compJoin.get(Company_.companyId), companyId));

		TypedQuery<Payslip> payslipQuery = entityManagerFactory.createQuery(criteriaQuery);

		return payslipQuery.getResultList();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.PayslipDAO#findByCondition(com.payasia.common.dto.
	 * PayslipConditionDTO)
	 */
	@Override
	public List<Payslip> findByCondition(PayslipConditionDTO payslipConditionDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Payslip> criteriaQuery = cb.createQuery(Payslip.class);
		Root<Payslip> payslipRoot = criteriaQuery.from(Payslip.class);

		Join<Payslip, Employee> empJoin = payslipRoot.join(Payslip_.employee);
		Join<Payslip, MonthMaster> monthJoin = payslipRoot.join(Payslip_.monthMaster);
		Join<Payslip, PayslipFrequency> freqJoin = payslipRoot.join(Payslip_.payslipFrequency);

		criteriaQuery.select(payslipRoot);
		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(empJoin.get(Employee_.employeeId), payslipConditionDTO.getEmployeeId()));

		if (payslipConditionDTO.getMonthMasterId() != null) {
			restriction = cb.and(restriction,
					cb.equal(monthJoin.get(MonthMaster_.monthId), payslipConditionDTO.getMonthMasterId()));
		}

		if (payslipConditionDTO.getYear() > 0) {
			restriction = cb.and(restriction, cb.equal(payslipRoot.get(Payslip_.year), payslipConditionDTO.getYear()));
		}

		if (payslipConditionDTO.getPart() > 0) {
			restriction = cb.and(restriction, cb.equal(payslipRoot.get(Payslip_.part), payslipConditionDTO.getPart()));
		}

		if (payslipConditionDTO.getPayslipFrequencyId() != null) {
			restriction = cb.and(restriction, cb.equal(freqJoin.get(PayslipFrequency_.payslipFrequencyID),
					payslipConditionDTO.getPayslipFrequencyId()));
		}

		criteriaQuery.where(restriction);

		TypedQuery<Payslip> payslipQuery = entityManagerFactory.createQuery(criteriaQuery);

		return payslipQuery.getResultList();

	}

	@Override
	public Payslip getPayslip(PayslipConditionDTO payslipConditionDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Payslip> criteriaQuery = cb.createQuery(Payslip.class);
		Root<Payslip> payslipRoot = criteriaQuery.from(Payslip.class);

		Join<Payslip, Employee> empJoin = payslipRoot.join(Payslip_.employee);

		Join<Payslip, MonthMaster> monthJoin = payslipRoot.join(Payslip_.monthMaster);
		Join<Payslip, PayslipFrequency> freqJoin = payslipRoot.join(Payslip_.payslipFrequency);
		Join<Payslip, Company> companyJoin = payslipRoot.join(Payslip_.company);
		companyJoin.join(Company_.countryMaster);
		companyJoin.join(Company_.companyGroup);

		payslipRoot.fetch(Payslip_.employee);
		payslipRoot.fetch(Payslip_.monthMaster);
		payslipRoot.fetch(Payslip_.payslipFrequency);
		Fetch<Payslip, Company> companyFetchJoin = payslipRoot.fetch(Payslip_.company);
		companyFetchJoin.fetch(Company_.countryMaster);
		companyFetchJoin.fetch(Company_.companyGroup, JoinType.LEFT);

		criteriaQuery.select(payslipRoot);
		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(empJoin.get(Employee_.employeeId), payslipConditionDTO.getEmployeeId()));

		if (payslipConditionDTO.getMonthMasterId() != null) {
			restriction = cb.and(restriction,
					cb.equal(monthJoin.get(MonthMaster_.monthId), payslipConditionDTO.getMonthMasterId()));
		}

		if (payslipConditionDTO.getYear() > 0) {
			restriction = cb.and(restriction, cb.equal(payslipRoot.get(Payslip_.year), payslipConditionDTO.getYear()));
		}

		if (payslipConditionDTO.getPart() > 0) {
			restriction = cb.and(restriction, cb.equal(payslipRoot.get(Payslip_.part), payslipConditionDTO.getPart()));
		}

		if (payslipConditionDTO.getPayslipFrequencyId() != null) {
			restriction = cb.and(restriction, cb.equal(freqJoin.get(PayslipFrequency_.payslipFrequencyID),
					payslipConditionDTO.getPayslipFrequencyId()));
		}

		criteriaQuery.where(restriction);

		TypedQuery<Payslip> payslipQuery = entityManagerFactory.createQuery(criteriaQuery);
		
		List<Payslip> paySlipList = payslipQuery.getResultList();
		
		if(paySlipList!=null && !paySlipList.isEmpty()){
			return paySlipList.get(0);
		}
		else{
		   return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.PayslipDAO#getPayslip(com.payasia.common.dto.
	 * PayslipConditionDTO)
	 */
	@Override
	public List<Payslip> getPayslipEmployee(PayslipConditionDTO payslipConditionDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Payslip> criteriaQuery = cb.createQuery(Payslip.class);
		Root<Payslip> payslipRoot = criteriaQuery.from(Payslip.class);

		Join<Payslip, Employee> empJoin = payslipRoot.join(Payslip_.employee);

		Join<Payslip, MonthMaster> monthJoin = payslipRoot.join(Payslip_.monthMaster);
		Join<Payslip, PayslipFrequency> freqJoin = payslipRoot.join(Payslip_.payslipFrequency);
		Join<Payslip, Company> companyJoin = payslipRoot.join(Payslip_.company);
		companyJoin.join(Company_.countryMaster);
		companyJoin.join(Company_.companyGroup);

		payslipRoot.fetch(Payslip_.employee);
		payslipRoot.fetch(Payslip_.monthMaster);
		payslipRoot.fetch(Payslip_.payslipFrequency);
		Fetch<Payslip, Company> companyFetchJoin = payslipRoot.fetch(Payslip_.company);
		companyFetchJoin.fetch(Company_.countryMaster);
		companyFetchJoin.fetch(Company_.companyGroup, JoinType.LEFT);

		criteriaQuery.select(payslipRoot);
		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(empJoin.get(Employee_.employeeId), payslipConditionDTO.getEmployeeId()));

		if (payslipConditionDTO.getMonthMasterId() != null) {
			restriction = cb.and(restriction,
					cb.equal(monthJoin.get(MonthMaster_.monthId), payslipConditionDTO.getMonthMasterId()));
		}

		if (payslipConditionDTO.getYear() > 0) {
			restriction = cb.and(restriction, cb.equal(payslipRoot.get(Payslip_.year), payslipConditionDTO.getYear()));
		}

		/*
		 * if (payslipConditionDTO.getPart() > 0) { restriction =
		 * cb.and(restriction, cb.equal( payslipRoot.get(Payslip_.part),
		 * payslipConditionDTO.getPart())); }
		 */

		if (payslipConditionDTO.getPayslipFrequencyId() != null) {
			restriction = cb.and(restriction, cb.equal(freqJoin.get(PayslipFrequency_.payslipFrequencyID),
					payslipConditionDTO.getPayslipFrequencyId()));
		}

		criteriaQuery.where(restriction);

		TypedQuery<Payslip> payslipQuery = entityManagerFactory.createQuery(criteriaQuery);

		return (List<Payslip>) payslipQuery.getResultList();

	}

	@Override
	public List<Payslip> getReleasedPayslipDetails(PayslipConditionDTO payslipConditionDTO, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Payslip> criteriaQuery = cb.createQuery(Payslip.class);
		Root<Payslip> payslipRoot = criteriaQuery.from(Payslip.class);

		Join<Payslip, MonthMaster> monthJoin = payslipRoot.join(Payslip_.monthMaster);
		Join<Payslip, Company> companyJoin = payslipRoot.join(Payslip_.company);
		Join<Payslip, Employee> employeeJoin = payslipRoot.join(Payslip_.employee);
		companyJoin.join(Company_.countryMaster);
		companyJoin.join(Company_.companyGroup);

		payslipRoot.fetch(Payslip_.employee);
		payslipRoot.fetch(Payslip_.monthMaster);
		payslipRoot.fetch(Payslip_.payslipFrequency);
		Fetch<Payslip, Company> companyFetchJoin = payslipRoot.fetch(Payslip_.company);
		companyFetchJoin.fetch(Company_.countryMaster);
		companyFetchJoin.fetch(Company_.companyGroup, JoinType.LEFT);

		criteriaQuery.select(payslipRoot);
		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(companyJoin.get(Company_.companyId), companyId));

		if (payslipConditionDTO.getMonthMasterId() != null) {
			restriction = cb.and(restriction,
					cb.equal(monthJoin.get(MonthMaster_.monthId), payslipConditionDTO.getMonthMasterId()));
		}

		if (payslipConditionDTO.getYear() > 0) {
			restriction = cb.and(restriction, cb.equal(payslipRoot.get(Payslip_.year), payslipConditionDTO.getYear()));
		}

		if (payslipConditionDTO.getPart() > 0) {
			restriction = cb.and(restriction, cb.equal(payslipRoot.get(Payslip_.part), payslipConditionDTO.getPart()));
		}

		if (payslipConditionDTO.getPayslipFrequencyId() != null) {
			Join<Payslip, PayslipFrequency> freqJoin = payslipRoot.join(Payslip_.payslipFrequency);
			restriction = cb.and(restriction, cb.equal(freqJoin.get(PayslipFrequency_.payslipFrequencyID),
					payslipConditionDTO.getPayslipFrequencyId()));
		}
		if (payslipConditionDTO.getEmployeeNumberList() != null) {
			restriction = cb.and(restriction,
					employeeJoin.get(Employee_.employeeNumber).in(payslipConditionDTO.getEmployeeNumberList()));
		}
		restriction = cb.and(restriction, cb.equal(employeeJoin.get(Employee_.status), true));
		criteriaQuery.where(restriction);

		TypedQuery<Payslip> payslipQuery = entityManagerFactory.createQuery(criteriaQuery);
		return payslipQuery.getResultList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.PayslipDAO#saveReturn(com.payasia.dao.bean.Payslip)
	 */
	@Override
	public Payslip saveReturn(Payslip payslip) {
		Payslip persistObj = payslip;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (Payslip) getBaseEntity();
			beanUtil.copyProperties(persistObj, payslip);
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
	 * @see com.payasia.dao.PayslipDAO#deleteByCondition(com.payasia.common.dto.
	 * PayslipConditionDTO)
	 */
	@Override
	public void deleteByCondition(PayslipConditionDTO payslipConditionDTO) {

		String queryString = "DELETE FROM Payslip d WHERE d.company.companyId = :company AND d.employee.employeeId = :employee";
		queryString = queryString
				+ " AND d.part = :part AND d.year = :year AND d.monthMaster.monthId = :month AND d.payslipFrequency.payslipFrequencyID = :frequency";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("employee", payslipConditionDTO.getEmployeeId());
		q.setParameter("company", payslipConditionDTO.getCompanyId());
		q.setParameter("year", payslipConditionDTO.getYear());
		q.setParameter("part", payslipConditionDTO.getPart());
		q.setParameter("frequency", payslipConditionDTO.getPayslipFrequencyId());
		q.setParameter("month", payslipConditionDTO.getMonthMasterId());

		q.executeUpdate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.PayslipDAO#findByCondition(java.util.Map,
	 * java.util.List, java.util.List, java.lang.Long)
	 */
	@Override
	public List<Object[]> findByCondition(Map<String, DataImportKeyValueDTO> colMap, List<Long> formIds,
			List<ExcelExportFiltersForm> finalFilterList, Long companyId) {
		Map<String, String> paramValueMap = new HashMap<String, String>();

		ExcelExportQueryDTO selectDTO = createSelect(colMap);
		String from = createFrom(colMap, formIds, selectDTO);
		String where = createWhere(companyId, finalFilterList, paramValueMap);
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
	 * Creates the where.
	 * 
	 * @param companyId
	 *            the company id
	 * @param finalFilterList
	 *            the final filter list
	 * @param paramValueMap
	 * @return the string
	 */
	private String createWhere(Long companyId, List<ExcelExportFiltersForm> finalFilterList,
			Map<String, String> paramValueMap) {

		StringBuilder whereBuilder = new StringBuilder("");
		whereBuilder.append(" WHERE payslip.Company_ID =:companyId ");

		if (!finalFilterList.isEmpty()) {
			whereBuilder.append(" AND ");
		}

		int count = 1;

		for (ExcelExportFiltersForm excelExportFiltersForm : finalFilterList) {

			if (excelExportFiltersForm.getDataImportKeyValueDTO().isStatic()) {
				if ("EmployeeNumber"
						.equalsIgnoreCase(excelExportFiltersForm.getDataImportKeyValueDTO().getMethodName())) {
					whereBuilder.append(" ").append(excelExportFiltersForm.getOpenBracket()).append(" employee.")
							.append(excelExportFiltersForm.getDataImportKeyValueDTO().getActualColName()).append(" ");
				} else if ("FirstName"
						.equalsIgnoreCase(excelExportFiltersForm.getDataImportKeyValueDTO().getMethodName())) {
					whereBuilder.append(" ").append(excelExportFiltersForm.getOpenBracket()).append(" employee.")
							.append(excelExportFiltersForm.getDataImportKeyValueDTO().getActualColName()).append(" ");
				} else if ("LastName"
						.equalsIgnoreCase(excelExportFiltersForm.getDataImportKeyValueDTO().getMethodName())) {
					whereBuilder.append(" ").append(excelExportFiltersForm.getOpenBracket()).append(" employee.")
							.append(excelExportFiltersForm.getDataImportKeyValueDTO().getActualColName()).append(" ");
				} else if ("MiddleName"
						.equalsIgnoreCase(excelExportFiltersForm.getDataImportKeyValueDTO().getMethodName())) {
					whereBuilder.append(" ").append(excelExportFiltersForm.getOpenBracket()).append(" employee.")
							.append(excelExportFiltersForm.getDataImportKeyValueDTO().getActualColName()).append(" ");
				} else if ("HireDate"
						.equalsIgnoreCase(excelExportFiltersForm.getDataImportKeyValueDTO().getMethodName())) {
					whereBuilder.append(" ").append(excelExportFiltersForm.getOpenBracket()).append(" employee.")
							.append(excelExportFiltersForm.getDataImportKeyValueDTO().getActualColName()).append(" ");
				} else if ("OriginalHireDate"
						.equalsIgnoreCase(excelExportFiltersForm.getDataImportKeyValueDTO().getMethodName())) {
					whereBuilder.append(" ").append(excelExportFiltersForm.getOpenBracket()).append(" employee.")
							.append(excelExportFiltersForm.getDataImportKeyValueDTO().getActualColName()).append(" ");
				} else if ("ResignationDate"
						.equalsIgnoreCase(excelExportFiltersForm.getDataImportKeyValueDTO().getMethodName())) {
					whereBuilder.append(" ").append(excelExportFiltersForm.getOpenBracket()).append(" employee.")
							.append(excelExportFiltersForm.getDataImportKeyValueDTO().getActualColName()).append(" ");
				} else if ("ConfirmationDate"
						.equalsIgnoreCase(excelExportFiltersForm.getDataImportKeyValueDTO().getMethodName())) {
					whereBuilder.append(" ").append(excelExportFiltersForm.getOpenBracket()).append(" employee.")
							.append(excelExportFiltersForm.getDataImportKeyValueDTO().getActualColName()).append(" ");
				} else if ("MonthName"
						.equalsIgnoreCase(excelExportFiltersForm.getDataImportKeyValueDTO().getMethodName())) {

					whereBuilder.append(" ").append(excelExportFiltersForm.getOpenBracket()).append(" month.")
							.append(excelExportFiltersForm.getDataImportKeyValueDTO().getActualColName()).append(" ");

				} else if ("Frequency"
						.equalsIgnoreCase(excelExportFiltersForm.getDataImportKeyValueDTO().getMethodName())) {
					whereBuilder.append(" ").append(excelExportFiltersForm.getOpenBracket())
							.append(" payslipFrequency.")
							.append(excelExportFiltersForm.getDataImportKeyValueDTO().getActualColName()).append(" ");
				} else if ("CompanyName"
						.equalsIgnoreCase(excelExportFiltersForm.getDataImportKeyValueDTO().getMethodName())) {
					whereBuilder.append(" ").append(excelExportFiltersForm.getOpenBracket()).append(" company.")
							.append(excelExportFiltersForm.getDataImportKeyValueDTO().getActualColName()).append(" ");
				} else if ("CompanyCode"
						.equalsIgnoreCase(excelExportFiltersForm.getDataImportKeyValueDTO().getMethodName())) {
					whereBuilder.append(" ").append(excelExportFiltersForm.getOpenBracket()).append(" company.")
							.append(excelExportFiltersForm.getDataImportKeyValueDTO().getActualColName()).append(" ");
				} else if ("DateFormat"
						.equalsIgnoreCase(excelExportFiltersForm.getDataImportKeyValueDTO().getMethodName())) {
					whereBuilder.append(" ").append(excelExportFiltersForm.getOpenBracket()).append(" company.")
							.append(excelExportFiltersForm.getDataImportKeyValueDTO().getActualColName()).append(" ");
				} else if ("GroupCode"
						.equalsIgnoreCase(excelExportFiltersForm.getDataImportKeyValueDTO().getMethodName())) {
					whereBuilder.append(" ").append(excelExportFiltersForm.getOpenBracket()).append(" companygroup.")
							.append(excelExportFiltersForm.getDataImportKeyValueDTO().getActualColName()).append(" ");
				} else if ("CountryName"
						.equalsIgnoreCase(excelExportFiltersForm.getDataImportKeyValueDTO().getMethodName())) {
					whereBuilder.append(" ").append(excelExportFiltersForm.getOpenBracket()).append(" country.")
							.append(excelExportFiltersForm.getDataImportKeyValueDTO().getActualColName()).append(" ");
				} else if ("GroupName"
						.equalsIgnoreCase(excelExportFiltersForm.getDataImportKeyValueDTO().getMethodName())) {
					whereBuilder.append(" ").append(excelExportFiltersForm.getOpenBracket()).append(" companygroup.")
							.append(excelExportFiltersForm.getDataImportKeyValueDTO().getActualColName()).append(" ");
				} else if ("TimeZoneName"
						.equalsIgnoreCase(excelExportFiltersForm.getDataImportKeyValueDTO().getMethodName())) {
					whereBuilder.append(" ").append(excelExportFiltersForm.getOpenBracket()).append(" timezone.")
							.append(excelExportFiltersForm.getDataImportKeyValueDTO().getActualColName()).append(" ");
				} else {
					whereBuilder.append(" ").append(excelExportFiltersForm.getOpenBracket()).append(" payslip.")
							.append(excelExportFiltersForm.getDataImportKeyValueDTO().getActualColName()).append(" ");
				}

			} else {

				if (!excelExportFiltersForm.getDataImportKeyValueDTO().isChild()) {

					if ("numeric".equalsIgnoreCase(excelExportFiltersForm.getDataImportKeyValueDTO().getFieldType())) {
						whereBuilder.append(" ").append(excelExportFiltersForm.getOpenBracket())
								.append("CAST(dynamicFormRecord")
								.append(excelExportFiltersForm.getDataImportKeyValueDTO().getFormId()).append(".Col_")
								.append(excelExportFiltersForm.getDataImportKeyValueDTO().getMethodName())
								.append(" as int ) ");
					} else if ("date"
							.equalsIgnoreCase(excelExportFiltersForm.getDataImportKeyValueDTO().getFieldType())) {

						whereBuilder.append(" ").append(excelExportFiltersForm.getOpenBracket())
								.append("convert(datetime, dynamicFormRecord")
								.append(excelExportFiltersForm.getDataImportKeyValueDTO().getFormId()).append(".Col_")
								.append(excelExportFiltersForm.getDataImportKeyValueDTO().getMethodName()).append(") ");

					} else {
						whereBuilder.append(" ").append(excelExportFiltersForm.getOpenBracket())
								.append(" dynamicFormRecord")
								.append(excelExportFiltersForm.getDataImportKeyValueDTO().getFormId()).append(".Col_")
								.append(excelExportFiltersForm.getDataImportKeyValueDTO().getMethodName()).append(" ");
					}
				} else {

					if ("numeric".equalsIgnoreCase(excelExportFiltersForm.getDataImportKeyValueDTO().getFieldType())) {
						whereBuilder.append(" ").append(excelExportFiltersForm.getOpenBracket())
								.append("CAST(dynamicFormTableRecord").append(".Col_")
								.append(excelExportFiltersForm.getDataImportKeyValueDTO().getMethodName())
								.append(" as int ) ");
					} else if ("date"
							.equalsIgnoreCase(excelExportFiltersForm.getDataImportKeyValueDTO().getFieldType())) {

						whereBuilder.append(" ").append(excelExportFiltersForm.getOpenBracket())
								.append("convert(datetime, dynamicFormTableRecord").append(".Col_")
								.append(excelExportFiltersForm.getDataImportKeyValueDTO().getMethodName()).append(") ");

					} else {
						whereBuilder.append(" ").append(excelExportFiltersForm.getOpenBracket())
								.append(" dynamicFormTableRecord").append(".Col_")
								.append(excelExportFiltersForm.getDataImportKeyValueDTO().getMethodName()).append(" ");
					}
				}

			}

			if (count < finalFilterList.size()) {
				paramValueMap.put(new StringBuilder("param").append(count).toString(),
						excelExportFiltersForm.getValue());
				whereBuilder.append(excelExportFiltersForm.getEqualityOperator()).append(":param").append(count)
						.append(" ").append(excelExportFiltersForm.getCloseBracket()).append(" ")
						.append(excelExportFiltersForm.getLogicalOperator()).append(" ");

			} else {
				paramValueMap.put(new StringBuilder("param").append(count).toString(),
						excelExportFiltersForm.getValue());
				whereBuilder.append(excelExportFiltersForm.getEqualityOperator()).append(":param").append(count)
						.append(" ").append(excelExportFiltersForm.getCloseBracket()).append(" ");

			}

			count++;
		}

		return whereBuilder.toString() + " ORDER BY payslip.Payslip_ID DESC";
	}

	/**
	 * Creates the from.
	 * 
	 * @param colMap
	 *            the col map
	 * @param formIds
	 *            the form ids
	 * @param selectDTO
	 *            the select dto
	 * @return the string
	 */
	private String createFrom(Map<String, DataImportKeyValueDTO> colMap, List<Long> formIds,
			ExcelExportQueryDTO selectDTO) {
		String from = "FROM Payslip AS payslip LEFT OUTER JOIN Employee AS employee ON (payslip.Employee_ID = employee.Employee_ID)  ";
		from = from
				+ "LEFT OUTER JOIN Payslip_Frequency AS payslipFrequency ON (payslip.Payslip_Frequency_ID = payslipFrequency.Payslip_Frequency_ID) ";
		from = from + "LEFT OUTER JOIN Month_Master AS month ON (payslip.Month_ID = month.Month_ID) ";
		from = from + "LEFT OUTER JOIN Company AS company ON (payslip.Company_ID = company.Company_ID) ";
		from = from + "LEFT OUTER JOIN Country_Master AS country ON (company.Country_ID = country.Country_ID) ";
		from = from + "LEFT OUTER JOIN Company_Group AS companygroup ON (company.Group_ID = companygroup.Group_ID) ";
		from = from + "LEFT OUTER JOIN Time_Zone_Master AS timezone ON (company.Time_Zone_ID = timezone.Time_Zone_ID) ";
		StringBuilder fromBuilder = new StringBuilder(from);
		for (Long formId : formIds) {

			fromBuilder.append("LEFT OUTER JOIN Dynamic_Form_Record AS dynamicFormRecord").append(formId).append(" ");
			fromBuilder.append("ON (payslip.Payslip_ID = dynamicFormRecord").append(formId)
					.append(".Entity_Key) AND (dynamicFormRecord").append(formId).append(".Form_ID = ").append(formId)
					.append(" ) ");

		}

		if (selectDTO.getTablePosition() != null) {
			fromBuilder.append("LEFT OUTER JOIN Dynamic_Form_Table_Record AS dynamicFormTableRecord ")
					.append("ON (dynamicFormRecord");
			fromBuilder.append(selectDTO.getFormId()).append(".Col_").append(selectDTO.getTablePosition())
					.append(" = CAST(dynamicFormTableRecord.Dynamic_Form_Table_Record_ID AS varchar(255)))");

		}
		return fromBuilder.toString();

	}

	/**
	 * Creates the select.
	 * 
	 * @param colMap
	 *            the col map
	 * @return the excel export query dto
	 */
	private ExcelExportQueryDTO createSelect(Map<String, DataImportKeyValueDTO> colMap) {

		ExcelExportQueryDTO excelExportQueryDTO = new ExcelExportQueryDTO();

		StringBuilder selectQueryBuilder = new StringBuilder("SELECT ");
		String tablePosition = null;
		Set<String> xlKeySet = colMap.keySet();

		int count = 1;

		for (Iterator<String> itr = xlKeySet.iterator(); itr.hasNext();) {

			String key = (String) itr.next();
			DataImportKeyValueDTO valueDTO = (DataImportKeyValueDTO) colMap.get(key);
			String fieldName = valueDTO.getMethodName();
			key = key.replaceAll("\\.", "_");
			key = new StringBuilder("[").append(key).append("]").toString();

			if (count < xlKeySet.size()) {
				if (valueDTO.isStatic()) {
					if (valueDTO.getMethodName().equalsIgnoreCase("EmployeeNumber")) {
						selectQueryBuilder.append("employee.Employee_Number").append(" AS ").append(key).append(", ");
					} else if (valueDTO.getMethodName().equalsIgnoreCase("FirstName")) {
						selectQueryBuilder.append("employee.First_Name").append(" AS ").append(key).append(", ");
					} else if (valueDTO.getMethodName().equalsIgnoreCase("LastName")) {
						selectQueryBuilder.append("employee.Last_Name").append(" AS ").append(key).append(", ");
					} else if (valueDTO.getMethodName().equalsIgnoreCase("MiddleName")) {
						selectQueryBuilder.append("employee.Middle_Name").append(" AS ").append(key).append(", ");
					} else if (valueDTO.getMethodName().equalsIgnoreCase("HireDate")) {
						selectQueryBuilder.append("employee.Hire_Date").append(" AS ").append(key).append(", ");
					} else if (valueDTO.getMethodName().equalsIgnoreCase("OriginalHireDate")) {
						selectQueryBuilder.append("employee.Original_Hire_Date").append(" AS ").append(key)
								.append(", ");
					} else if (valueDTO.getMethodName().equalsIgnoreCase("ResignationDate")) {
						selectQueryBuilder.append("employee.Resignation_Date").append(" AS ").append(key).append(", ");
					} else if (valueDTO.getMethodName().equalsIgnoreCase("ConfirmationDate")) {
						selectQueryBuilder.append("employee.Confirmation_Date").append(" AS ").append(key).append(", ");
					} else if (valueDTO.getMethodName().equalsIgnoreCase("MonthName")) {
						selectQueryBuilder.append("month.Month_Name").append(" AS ").append(key).append(", ");
					} else if (valueDTO.getMethodName().equalsIgnoreCase("Frequency")) {
						selectQueryBuilder.append("payslipFrequency.Frequency").append(" AS ").append(key).append(", ");
					} else if (valueDTO.getMethodName().equalsIgnoreCase("CompanyName")) {
						selectQueryBuilder.append("company.Company_Name").append(" AS ").append(key).append(", ");
					} else if (valueDTO.getMethodName().equalsIgnoreCase("CompanyCode")) {
						selectQueryBuilder.append("company.Company_Code").append(" AS ").append(key).append(", ");
					} else if (valueDTO.getMethodName().equalsIgnoreCase("GroupCode")) {
						selectQueryBuilder.append("companygroup.Group_Code").append(" AS ").append(key).append(", ");
					} else if (valueDTO.getMethodName().equalsIgnoreCase("CountryName")) {
						selectQueryBuilder.append("country.Country_Name").append(" AS ").append(key).append(", ");
					} else if (valueDTO.getMethodName().equalsIgnoreCase("DateFormat")) {
						selectQueryBuilder.append("company.Date_Format").append(" AS ").append(key).append(", ");
					} else if (valueDTO.getMethodName().equalsIgnoreCase("TimeZoneName")) {
						selectQueryBuilder.append("timezone.Time_Zone_Name").append(" AS ").append(key).append(", ");
					} else if (valueDTO.getMethodName().equalsIgnoreCase("GroupName")) {
						selectQueryBuilder.append("companygroup.Group_Name").append(" AS ").append(key).append(", ");
					} else {
						String methodName = valueDTO.getActualColName();

						selectQueryBuilder.append("payslip.").append(methodName).append(" AS ").append(key)
								.append(", ");
					}
				} else {

					String methodName = new StringBuilder("Col_").append(fieldName).toString();

					if (!valueDTO.isChild()) {
						selectQueryBuilder.append(" dynamicFormRecord").append(valueDTO.getFormId()).append(".")
								.append(methodName).append(" AS ").append(key).append(", ");

					} else {

						selectQueryBuilder.append(" dynamicFormTableRecord.").append(methodName).append(" AS ")
								.append(key).append(", ");

						tablePosition = valueDTO.getTablePosition();
						excelExportQueryDTO.setTablePosition(tablePosition);
						excelExportQueryDTO.setFormId(valueDTO.getFormId());

					}

				}

			} else {
				if (valueDTO.isStatic()) {
					if (valueDTO.getMethodName().equalsIgnoreCase("EmployeeNumber")) {
						selectQueryBuilder.append("employee.Employee_Number").append(" AS ").append(key).append(" ");
					} else if (valueDTO.getMethodName().equalsIgnoreCase("FirstName")) {
						selectQueryBuilder.append("employee.First_Name").append(" AS ").append(key).append(" ");
					} else if (valueDTO.getMethodName().equalsIgnoreCase("LastName")) {
						selectQueryBuilder.append("employee.Last_Name").append(" AS ").append(key).append(" ");
					} else if (valueDTO.getMethodName().equalsIgnoreCase("MiddleName")) {
						selectQueryBuilder.append("employee.Middle_Name").append(" AS ").append(key).append(" ");
					} else if (valueDTO.getMethodName().equalsIgnoreCase("HireDate")) {
						selectQueryBuilder.append("employee.Hire_Date").append(" AS ").append(key).append(" ");
					} else if (valueDTO.getMethodName().equalsIgnoreCase("OriginalHireDate")) {
						selectQueryBuilder.append("employee.Original_Hire_Date").append(" AS ").append(key).append(" ");
					} else if (valueDTO.getMethodName().equalsIgnoreCase("ResignationDate")) {
						selectQueryBuilder.append("employee.Resignation_Date").append(" AS ").append(key).append(" ");
					} else if (valueDTO.getMethodName().equalsIgnoreCase("ConfirmationDate")) {
						selectQueryBuilder.append("employee.Confirmation_Date").append(" AS ").append(key).append(" ");
					} else if (valueDTO.getMethodName().equalsIgnoreCase("MonthName")) {
						selectQueryBuilder.append("month.Month_Name").append(" AS ").append(key).append(" ");
					} else if (valueDTO.getMethodName().equalsIgnoreCase("Frequency")) {
						selectQueryBuilder.append("payslipFrequency.Frequency").append(" AS ").append(key).append(" ");
					} else if (valueDTO.getMethodName().equalsIgnoreCase("CompanyName")) {
						selectQueryBuilder.append("company.Company_Name").append(" AS ").append(key).append(" ");
					} else if (valueDTO.getMethodName().equalsIgnoreCase("CompanyCode")) {
						selectQueryBuilder.append("company.Company_Code").append(" AS ").append(key).append(" ");
					} else if (valueDTO.getMethodName().equalsIgnoreCase("GroupCode")) {
						selectQueryBuilder.append("companygroup.Group_Code").append(" AS ").append(key).append(" ");
					} else if (valueDTO.getMethodName().equalsIgnoreCase("CountryName")) {
						selectQueryBuilder.append("country.Country_Name").append(" AS ").append(key).append(" ");
					} else if (valueDTO.getMethodName().equalsIgnoreCase("DateFormat")) {
						selectQueryBuilder.append("company.Date_Format").append(" AS ").append(key).append(" ");
					} else if (valueDTO.getMethodName().equalsIgnoreCase("TimeZoneName")) {
						selectQueryBuilder.append("timezone.Time_Zone_Name").append(" AS ").append(key).append(" ");
					} else if (valueDTO.getMethodName().equalsIgnoreCase("GroupName")) {
						selectQueryBuilder.append("companygroup.Group_Name").append(" AS ").append(key).append(" ");
					} else {
						String methodName = valueDTO.getActualColName();

						selectQueryBuilder.append("payslip.").append(methodName).append(" AS ").append(key).append(" ");
					}
				} else {

					String methodName = new StringBuilder("Col_").append(fieldName).toString();

					if (!valueDTO.isChild()) {
						selectQueryBuilder.append(" dynamicFormRecord").append(valueDTO.getFormId()).append(".")
								.append(methodName).append(" AS ").append(key).append(" ");

					} else {

						selectQueryBuilder.append(" dynamicFormTableRecord.").append(methodName).append(" AS ")
								.append(key).append(" ");

						tablePosition = valueDTO.getTablePosition();
						excelExportQueryDTO.setTablePosition(tablePosition);
						excelExportQueryDTO.setFormId(valueDTO.getFormId());

					}

				}

			}
			count++;
		}

		excelExportQueryDTO.setSelectQuery(selectQueryBuilder.toString());
		return excelExportQueryDTO;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.PayslipDAO#getYTMValueGT(long, long, int, long,
	 * long, long, int, java.util.Map)
	 */
	@Override
	public Double getYTMValueGT(long currentMonth, long finStartMonth, int year, long employeeId, long companyId,
			long payslipFrequencyId, int part, Map<Long, DataImportKeyValueDTO> formulaMap) {

		Set<Long> keySet = formulaMap.keySet();
		StringBuilder queryBuilder = new StringBuilder("");
		for (Iterator<Long> itr = keySet.iterator(); itr.hasNext();) {
			Long key = (Long) itr.next();
			DataImportKeyValueDTO valueDTO = (DataImportKeyValueDTO) formulaMap.get(key);

			queryBuilder.append("select sum(temp.ytm) ytm from");
			queryBuilder.append(" (select isnull(sum(convert(float,dfr.col_");
			queryBuilder.append(valueDTO.getMethodName());
			queryBuilder.append(")),0) as ytm from payslip payslip inner join dynamic_form_record dfr");
			queryBuilder.append(" on payslip.payslip_id = dfr.entity_key");
			queryBuilder.append(" where payslip.employee_id=:employeeId");
			queryBuilder.append(" and dfr.form_id=").append(valueDTO.getFormId());
			queryBuilder.append(" and payslip.month_id >=:finStartMonth ");
			queryBuilder.append(" and payslip.month_id <:currentMonth ");
			queryBuilder.append(" and payslip.year=:year");
			queryBuilder.append(" AND payslip.Company_ID =:companyId ");
			queryBuilder.append(" union all");
			queryBuilder.append(" select isnull(sum(convert(float,dfr.col_");
			queryBuilder.append(valueDTO.getMethodName());
			queryBuilder.append(")),0) as ytm from payslip payslip inner join dynamic_form_record dfr");
			queryBuilder.append(" on payslip.payslip_id = dfr.entity_key");
			queryBuilder.append(" where payslip.employee_id=:employeeId");
			queryBuilder.append(" and dfr.form_id=").append(valueDTO.getFormId());
			queryBuilder.append(" and payslip.month_id=:currentMonth");
			queryBuilder.append(" and payslip.year=:year and payslip.part <=:part");
			queryBuilder.append(" AND payslip.Company_ID =:companyId  ) as temp");

		}

		Query q = entityManagerFactory.createNativeQuery(queryBuilder.toString());
		q.setParameter("currentMonth", currentMonth);
		q.setParameter("finStartMonth", finStartMonth);
		q.setParameter("year", year);
		q.setParameter("employeeId", employeeId);
		q.setParameter("companyId", companyId);
		q.setParameter("part", part);
		return (Double) q.getSingleResult();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.PayslipDAO#getYTMValueLT(long, long, int, long,
	 * long, long, int, java.util.Map)
	 */
	@Override
	public Double getYTMValueLT(long currentMonth, long finStartMonth, int year, long employeeId, long companyId,
			long payslipFrequencyId, int part, Map<Long, DataImportKeyValueDTO> formulaMap) {

		Set<Long> keySet = formulaMap.keySet();
		StringBuilder queryBuilder = new StringBuilder("");
		for (Iterator<Long> itr = keySet.iterator(); itr.hasNext();) {
			Long key = (Long) itr.next();
			DataImportKeyValueDTO valueDTO = (DataImportKeyValueDTO) formulaMap.get(key);

			queryBuilder.append("select sum(temp.ytm) ytm from ");
			queryBuilder.append(" (select isnull(sum(convert(float,dfr.col_");
			queryBuilder.append(valueDTO.getMethodName());
			queryBuilder.append(")),0) as ytm from payslip payslip inner join dynamic_form_record dfr");
			queryBuilder.append(" on payslip.payslip_id = dfr.entity_key");
			queryBuilder.append(" where payslip.employee_id=:employeeId");
			queryBuilder.append(" and dfr.form_id=").append(valueDTO.getFormId());
			queryBuilder.append(" and payslip.month_id >=:finStartMonth ");
			queryBuilder.append(" and payslip.year=:YTMYear");
			queryBuilder.append(" AND payslip.Company_ID =:companyId ");
			queryBuilder.append(" union all");
			queryBuilder.append(" select sum(convert(float,dfr.col_");
			queryBuilder.append(valueDTO.getMethodName());
			queryBuilder.append(")) as ytm from payslip payslip inner join dynamic_form_record dfr");
			queryBuilder.append(" on payslip.payslip_id = dfr.entity_key");
			queryBuilder.append(" where payslip.employee_id=:employeeId");
			queryBuilder.append(" and dfr.form_id=").append(valueDTO.getFormId());
			queryBuilder.append(" and payslip.month_id <:currentMonth ");
			queryBuilder.append(" and payslip.year=:year");
			queryBuilder.append(" AND payslip.Company_ID =:companyId ");
			queryBuilder.append(" union all");
			queryBuilder.append(" select sum(convert(float,dfr.col_");
			queryBuilder.append(valueDTO.getMethodName());
			queryBuilder.append(")) as ytm from payslip payslip inner join dynamic_form_record dfr");
			queryBuilder.append(" on payslip.payslip_id = dfr.entity_key");
			queryBuilder.append(" where payslip.employee_id=:employeeId");
			queryBuilder.append(" and dfr.form_id=").append(valueDTO.getFormId());
			queryBuilder.append(" and payslip.month_id =:currentMonth ");
			queryBuilder.append(" and payslip.year=:year and payslip.part <=:part");
			queryBuilder.append(" AND payslip.Company_ID =:companyId ");
			queryBuilder.append(" ) as temp");

		}

		Query q = entityManagerFactory.createNativeQuery(queryBuilder.toString());
		q.setParameter("currentMonth", currentMonth);
		q.setParameter("finStartMonth", finStartMonth);
		q.setParameter("YTMYear", (year - 1));
		q.setParameter("year", year);
		q.setParameter("employeeId", employeeId);
		q.setParameter("companyId", companyId);
		q.setParameter("part", part);
		return (Double) q.getSingleResult();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.PayslipDAO#getYearList(java.lang.Long)
	 */
	@Override
	public List<Integer> getYearList(Long employeeId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Integer> criteriaQuery = cb.createQuery(Integer.class);
		Root<Payslip> payslipRoot = criteriaQuery.from(Payslip.class);
		criteriaQuery.select(payslipRoot.get(Payslip_.year)).distinct(true);

		Join<Payslip, Employee> employeeJoin = payslipRoot.join(Payslip_.employee);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(employeeJoin.get(Employee_.employeeId), employeeId));
		criteriaQuery.where(restriction);

		TypedQuery<Integer> yearTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		return yearTypedQuery.getResultList();
	}

	@Override
	public List<Object[]> getLatestPayslipEffectiveDate(Long companyId) {
		String queryString = "SELECT DISTINCT p.year, p.monthMaster.monthId, p.part FROM Payslip p WHERE p.company.companyId = :company ORDER BY p.year DESC , p.monthMaster.monthId DESC, p.part DESC";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("company", companyId);
		q.setMaxResults(1);
		return q.getResultList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.PayslipDAO#findByCondition(com.payasia.common.dto.
	 * PayslipConditionDTO)
	 */
	@Override
	public List<Payslip> getPayslips(PayslipConditionDTO payslipConditionDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Payslip> criteriaQuery = cb.createQuery(Payslip.class);
		Root<Payslip> payslipRoot = criteriaQuery.from(Payslip.class);

		Join<Payslip, Employee> empJoin = payslipRoot.join(Payslip_.employee);
		Join<Payslip, MonthMaster> monthJoin = payslipRoot.join(Payslip_.monthMaster);

		criteriaQuery.select(payslipRoot);
		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(empJoin.get(Employee_.employeeId), payslipConditionDTO.getEmployeeId()));

		criteriaQuery.where(restriction);
		List<Order> orderByList = new ArrayList<>();
		orderByList.add(cb.desc(payslipRoot.get(Payslip_.year)));
		orderByList.add(cb.desc(monthJoin.get(MonthMaster_.monthId)));
		orderByList.add(cb.asc(payslipRoot.get(Payslip_.part)));
		criteriaQuery.orderBy(orderByList);

		TypedQuery<Payslip> payslipQuery = entityManagerFactory.createQuery(criteriaQuery);
		payslipQuery.setMaxResults(PayAsiaConstants.NO_OF_PAYSLIPS);
		return payslipQuery.getResultList();

	}

	@Override
	public Payslip findByID(long payslipId) {
		return super.findById(Payslip.class, payslipId);
	}

	@Override
	public List<Integer> getParts(PayslipConditionDTO payslipConditionDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Integer> criteriaQuery = cb.createQuery(Integer.class);
		Root<Payslip> payslipRoot = criteriaQuery.from(Payslip.class);
		criteriaQuery.select(payslipRoot.get(Payslip_.part)).distinct(true);

		Join<Payslip, Employee> employeeJoin = payslipRoot.join(Payslip_.employee);
		Join<Payslip, MonthMaster> monthMasterJoin = payslipRoot.join(Payslip_.monthMaster);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(employeeJoin.get(Employee_.employeeId), payslipConditionDTO.getEmployeeId()));
		restriction = cb.and(restriction,
				cb.equal(monthMasterJoin.get(MonthMaster_.monthId), payslipConditionDTO.getMonthMasterId()));
		restriction = cb.and(restriction, cb.equal(payslipRoot.get(Payslip_.year), payslipConditionDTO.getYear()));

		criteriaQuery.where(restriction);
		criteriaQuery.orderBy(cb.asc(payslipRoot.get(Payslip_.part)));

		TypedQuery<Integer> partTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		return partTypedQuery.getResultList();
	}

	@Override
	public Payslip fetchPayslipByEmpId(Long payslip, Long employeeId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Payslip> criteriaQuery = cb.createQuery(Payslip.class);
		Root<Payslip> root = criteriaQuery.from(Payslip.class);
		criteriaQuery.select(root);
		Predicate restriction = cb.conjunction();
		Join<Payslip, Employee> empJoin = root.join(Payslip_.employee);
		restriction = cb.and(restriction, cb.equal(empJoin.get(Employee_.employeeId), employeeId));
		restriction = cb.and(restriction, cb.equal(root.get(Payslip_.payslipId), payslip));
		criteriaQuery.where(restriction);
		TypedQuery<Payslip> payslipQuery = entityManagerFactory.createQuery(criteriaQuery);
		List<Payslip> list = payslipQuery.getResultList();
		return list != null && !list.isEmpty() ? list.get(0) : null;
	}


	public List<Payslip> getPayslipEmployee(PayslipConditionDTO payslipConditionDTO, PageRequest pageDTO,
			SortCondition sortDTO, String workingCompanyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Payslip> criteriaQuery = cb.createQuery(Payslip.class);
		Root<Payslip> payslipRoot = criteriaQuery.from(Payslip.class);

		Join<Payslip, Employee> empJoin = payslipRoot.join(Payslip_.employee);

		Join<Payslip, MonthMaster> monthJoin = payslipRoot.join(Payslip_.monthMaster);
		Join<Payslip, PayslipFrequency> freqJoin = payslipRoot.join(Payslip_.payslipFrequency);
		Join<Payslip, Company> companyJoin = payslipRoot.join(Payslip_.company);
		companyJoin.join(Company_.countryMaster);
		companyJoin.join(Company_.companyGroup);

		payslipRoot.fetch(Payslip_.employee);
		payslipRoot.fetch(Payslip_.monthMaster);
		payslipRoot.fetch(Payslip_.payslipFrequency);
		Fetch<Payslip, Company> companyFetchJoin = payslipRoot.fetch(Payslip_.company);
		companyFetchJoin.fetch(Company_.countryMaster);
		companyFetchJoin.fetch(Company_.companyGroup, JoinType.LEFT);

		criteriaQuery.select(payslipRoot);
		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(empJoin.get(Employee_.employeeId), payslipConditionDTO.getEmployeeId()));

		if (payslipConditionDTO.getMonthMasterId() != null) {
			restriction = cb.and(restriction,
					cb.equal(monthJoin.get(MonthMaster_.monthId), payslipConditionDTO.getMonthMasterId()));
		}

		if (payslipConditionDTO.getYear() > 0) {
			restriction = cb.and(restriction, cb.equal(payslipRoot.get(Payslip_.year), payslipConditionDTO.getYear()));
		}

		if (payslipConditionDTO.getPayslipFrequencyId() != null) {
			restriction = cb.and(restriction, cb.equal(freqJoin.get(PayslipFrequency_.payslipFrequencyID),
					payslipConditionDTO.getPayslipFrequencyId()));
		}

		if (sortDTO != null) {

			Path<String> sortPath = getSortPathForEmployeePayslip(sortDTO, payslipRoot, empJoin);
			if (sortPath != null) {
				if (SortConstants.DB_ORDER_BY_ASC.equalsIgnoreCase(sortDTO.getOrderType())) {
					criteriaQuery.orderBy(cb.asc(sortPath));
				}
				if (SortConstants.DB_ORDER_BY_DESC.equalsIgnoreCase(sortDTO.getOrderType())) {
					criteriaQuery.orderBy(cb.desc(sortPath));
				}
			}

		}

		criteriaQuery.where(restriction);

		TypedQuery<Payslip> payslipQuery = entityManagerFactory.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			payslipQuery.setFirstResult(getStartPosition(pageDTO));
			payslipQuery.setMaxResults(pageDTO.getPageSize());
		}

		List<Payslip> payslipDetailList = payslipQuery.getResultList();

		return payslipDetailList;

	}

	private Path<String> getSortPathForEmployeePayslip(SortCondition sortDTO, Root<Payslip> payslipRoot,
			Join<Payslip, Employee> empJoin) {

		List<String> employeePayslipList = new ArrayList<String>();
		employeePayslipList.add(SortConstants.PAYSLIP_RELEASE_MONTH);
		employeePayslipList.add(SortConstants.PAYSLIP_RELEASE_PART);
		employeePayslipList.add(SortConstants.PAYSLIP_RELEASE_YEAR);
		employeePayslipList.add(SortConstants.PAYSLIP_RELEASE_NAME);
		employeePayslipList.add("part");
		employeePayslipList.add("monthName");
		employeePayslipList.add("monthId");

		Path<String> sortPath = null;

		if (employeePayslipList.contains(sortDTO.getColumnName())) {
			//sortPath = payslipRoot.get(colMap.get(Payslip_.part + sortDTO.getColumnName() ));
			
			switch (sortDTO.getColumnName()) {

			case "part":
				sortPath = payslipRoot.get("part");
				break;

			case "monthId":
				sortPath = payslipRoot.get("monthMaster").get("monthId");
				break;

			case "monthName":
				sortPath = payslipRoot.get("monthMaster").get("monthName");
				break;
				
			default :
				sortPath = payslipRoot.get(colMap.get(Payslip_.part + sortDTO.getColumnName() ));
				
			}

		}

		return sortPath;
	}

	
	public List<Payslip> getPositionPayslipEmployee(PayslipConditionDTO payslipConditionDTO, PageRequest pageDTO,
			SortCondition sortDTO, String workingCompanyId, int startPos) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Payslip> criteriaQuery = cb.createQuery(Payslip.class);
		Root<Payslip> payslipRoot = criteriaQuery.from(Payslip.class);

		Join<Payslip, Employee> empJoin = payslipRoot.join(Payslip_.employee);

		Join<Payslip, MonthMaster> monthJoin = payslipRoot.join(Payslip_.monthMaster);
		Join<Payslip, PayslipFrequency> freqJoin = payslipRoot.join(Payslip_.payslipFrequency);
		Join<Payslip, Company> companyJoin = payslipRoot.join(Payslip_.company);
		companyJoin.join(Company_.countryMaster);
		companyJoin.join(Company_.companyGroup);

		payslipRoot.fetch(Payslip_.employee);
		payslipRoot.fetch(Payslip_.monthMaster);
		payslipRoot.fetch(Payslip_.payslipFrequency);
		Fetch<Payslip, Company> companyFetchJoin = payslipRoot.fetch(Payslip_.company);
		companyFetchJoin.fetch(Company_.countryMaster);
		companyFetchJoin.fetch(Company_.companyGroup, JoinType.LEFT);

		criteriaQuery.select(payslipRoot);
		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(empJoin.get(Employee_.employeeId), payslipConditionDTO.getEmployeeId()));

		if (payslipConditionDTO.getMonthMasterId() != null) {
			restriction = cb.and(restriction,
					cb.equal(monthJoin.get(MonthMaster_.monthId), payslipConditionDTO.getMonthMasterId()));
		}

		if (payslipConditionDTO.getYear() > 0) {
			restriction = cb.and(restriction, cb.equal(payslipRoot.get(Payslip_.year), payslipConditionDTO.getYear()));
		}

		if (payslipConditionDTO.getPayslipFrequencyId() != null) {
			restriction = cb.and(restriction, cb.equal(freqJoin.get(PayslipFrequency_.payslipFrequencyID),
					payslipConditionDTO.getPayslipFrequencyId()));
		}

		if (sortDTO != null) {

			Path<String> sortPath = getSortPathForEmployeePayslip(sortDTO, payslipRoot, empJoin);
			if (sortPath != null) {
				if (SortConstants.DB_ORDER_BY_ASC.equalsIgnoreCase(sortDTO.getOrderType())) {
					criteriaQuery.orderBy(cb.asc(sortPath));
				}
				if (SortConstants.DB_ORDER_BY_DESC.equalsIgnoreCase(sortDTO.getOrderType())) {
					criteriaQuery.orderBy(cb.desc(sortPath));
				}
			}

		}

		criteriaQuery.where(restriction);

		TypedQuery<Payslip> payslipQuery = entityManagerFactory.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			payslipQuery.setFirstResult(startPos);
			payslipQuery.setMaxResults(pageDTO.getPageSize());
		}

		List<Payslip> payslipDetailList = payslipQuery.getResultList();

		return payslipDetailList;

	}


}
