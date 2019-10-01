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
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.payasia.common.dto.DataImportKeyValueDTO;
import com.payasia.common.dto.ExcelExportQueryDTO;
import com.payasia.common.dto.PayCodeDataConditionDTO;
import com.payasia.common.dto.PayDataCollectionConditionDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.ExcelExportFiltersForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.NullAwareBeanUtilsBean;
import com.payasia.common.util.SortConstants;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.PayDataCollectionDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.Employee_;
import com.payasia.dao.bean.PayDataCollection;
import com.payasia.dao.bean.PayDataCollection_;
import com.payasia.dao.bean.Paycode;
import com.payasia.dao.bean.Paycode_;

/**
 * The Class PayDataCollectionDAOImpl.
 */
@Repository
public class PayDataCollectionDAOImpl extends BaseDAO implements
		PayDataCollectionDAO {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.BaseDAO#getBaseEntity()
	 */
	@Override
	protected Object getBaseEntity() {
		PayDataCollection payDataCollection = new PayDataCollection();

		return payDataCollection;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.PayDataCollectionDAO#findAll(long)
	 */
	@Override
	public List<PayDataCollection> findAll(long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<PayDataCollection> criteriaQuery = cb
				.createQuery(PayDataCollection.class);
		Root<PayDataCollection> payDataRoot = criteriaQuery
				.from(PayDataCollection.class);
		Join<PayDataCollection, Company> PayDataCollectionCompanyJoin = payDataRoot
				.join(PayDataCollection_.company);

		criteriaQuery.select(payDataRoot);

		criteriaQuery
				.where(cb.equal(
						PayDataCollectionCompanyJoin.get(Company_.companyId),
						companyId));

		TypedQuery<PayDataCollection> payDataCollectionQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return payDataCollectionQuery.getResultList();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.PayDataCollectionDAO#findByCondition(com.payasia.common
	 * .dto.PayDataCollectionConditionDTO, java.lang.Long,
	 * com.payasia.common.form.PageRequest,
	 * com.payasia.common.form.SortCondition)
	 */
	@Override
	public List<PayDataCollection> findByCondition(
			PayDataCollectionConditionDTO payDataCollectionConditionDTO,
			Long companyId, PageRequest pageDTO, SortCondition sortDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<PayDataCollection> criteriaQuery = cb
				.createQuery(PayDataCollection.class);
		Root<PayDataCollection> payDataRoot = criteriaQuery
				.from(PayDataCollection.class);

		criteriaQuery.select(payDataRoot);

		Join<PayDataCollection, Employee> PayDataCollectionEmpJoin = payDataRoot
				.join(PayDataCollection_.employee);
		Join<PayDataCollection, Paycode> PayDataCollectionPayCodeJoin = payDataRoot
				.join(PayDataCollection_.paycode);
		Join<PayDataCollection, Company> PayDataCollectionCompanyJoin = payDataRoot
				.join(PayDataCollection_.company);

		Path<Long> employeeID = PayDataCollectionEmpJoin
				.get(Employee_.employeeId);

		Predicate restriction = cb.conjunction();

		if (StringUtils.isNotBlank(payDataCollectionConditionDTO
				.getEmployeeNumber())) {

			restriction = cb.and(restriction, cb.like(cb
					.upper(PayDataCollectionEmpJoin
							.get(Employee_.employeeNumber)),
					payDataCollectionConditionDTO.getEmployeeNumber()
							.toUpperCase()));
		}

		if (StringUtils.isNotBlank(payDataCollectionConditionDTO
				.getEmployeeName())) {

			restriction = cb.and(restriction, cb.or(
					cb.like(cb.upper(PayDataCollectionEmpJoin
							.get(Employee_.firstName)),
							payDataCollectionConditionDTO.getEmployeeName()
									.toUpperCase()), cb.like(cb
							.upper(PayDataCollectionEmpJoin
									.get(Employee_.lastName)),
							payDataCollectionConditionDTO.getEmployeeName()
									.toUpperCase())));
		}
		if (payDataCollectionConditionDTO.getPayCode() != null) {
			restriction = cb.and(restriction, cb.like(
					PayDataCollectionPayCodeJoin.get(Paycode_.paycode),
					payDataCollectionConditionDTO.getPayCode()));

		}

		if (payDataCollectionConditionDTO.getEmployeeShortListDTO()
				.getEmployeeShortList()
				&& payDataCollectionConditionDTO.getEmployeeShortListDTO()
						.getShortListEmployeeIds().size() == 0) {

			restriction = cb.and(restriction, employeeID.in(-1));

		} else if (payDataCollectionConditionDTO.getEmployeeShortListDTO()
				.getEmployeeShortList()
				&& payDataCollectionConditionDTO.getEmployeeShortListDTO()
						.getShortListEmployeeIds().size() > 0) {
			restriction = cb.and(restriction, employeeID
					.in(payDataCollectionConditionDTO.getEmployeeShortListDTO()
							.getShortListEmployeeIds()));

		}

		restriction = cb.and(restriction,
				cb.equal(PayDataCollectionCompanyJoin.get(Company_.companyId),
						companyId));
		criteriaQuery.where(restriction);

		if (sortDTO != null) {

			Path<String> sortPath = getSortPathForPayData(sortDTO, payDataRoot,
					PayDataCollectionEmpJoin, PayDataCollectionPayCodeJoin);
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

		TypedQuery<PayDataCollection> payDataCollectionQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			payDataCollectionQuery.setFirstResult(getStartPosition(pageDTO));
			payDataCollectionQuery.setMaxResults(pageDTO.getPageSize());
		}

		List<PayDataCollection> payDataCollectionList = payDataCollectionQuery
				.getResultList();

		return payDataCollectionList;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.PayDataCollectionDAO#getSortPathForPayData(com.payasia
	 * .common.form.SortCondition, javax.persistence.criteria.Root,
	 * javax.persistence.criteria.Join, javax.persistence.criteria.Join)
	 */
	@Override
	public Path<String> getSortPathForPayData(SortCondition sortDTO,
			Root<PayDataCollection> payDataRoot,
			Join<PayDataCollection, Employee> PayDataCollectionEmpJoin,
			Join<PayDataCollection, Paycode> PayDataCollectionPayCodeJoin) {

		List<String> payDataIdList = new ArrayList<String>();
		payDataIdList.add(SortConstants.PAY_CODE_ID);

		List<String> employeeIdList = new ArrayList<String>();
		employeeIdList.add(SortConstants.PAY_CODE_EMPLOYEE_ID);

		List<String> payDataIsColList = new ArrayList<String>();
		payDataIsColList.add(SortConstants.PAYDATA_COLLECTION_AMOUNT);
		payDataIsColList.add(SortConstants.PAYDATA_COLLECTION_FROM_DATE);
		payDataIsColList.add(SortConstants.PAYDATA_COLLECTION_TO_DATE);

		List<String> empDataIsColList = new ArrayList<String>();
		empDataIsColList.add(SortConstants.PAYDATA_COLLECTION_EMPLOYEE_NAME);
		empDataIsColList.add(SortConstants.PAYDATA_COLLECTION_EMPLOYEE_NUMBER);

		List<String> payCodeIsColList = new ArrayList<String>();
		payCodeIsColList.add(SortConstants.PAYDATA_COLLECTION_PAYCODE);

		Path<String> sortPath = null;

		if (payDataIsColList.contains(sortDTO.getColumnName())) {
			sortPath = payDataRoot.get(colMap.get(PayDataCollection.class
					+ sortDTO.getColumnName()));
		}
		if (empDataIsColList.contains(sortDTO.getColumnName())) {
			sortPath = PayDataCollectionEmpJoin.get(colMap.get(Employee.class
					+ sortDTO.getColumnName()));
		}
		if (payCodeIsColList.contains(sortDTO.getColumnName())) {
			sortPath = PayDataCollectionPayCodeJoin.get(colMap
					.get(Paycode.class + sortDTO.getColumnName()));
		}
		return sortPath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.PayDataCollectionDAO#update(com.payasia.dao.bean.
	 * PayDataCollection)
	 */
	@Override
	public void update(PayDataCollection payDataCollection) {

		super.update(payDataCollection);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.PayDataCollectionDAO#save(com.payasia.dao.bean.
	 * PayDataCollection)
	 */
	@Override
	public void save(PayDataCollection payDataCollection) {
		super.save(payDataCollection);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.PayDataCollectionDAO#newTranSave(com.payasia.dao.bean
	 * .PayDataCollection)
	 */
	@Override
	public void newTranSave(PayDataCollection payDataCollection) {
		super.save(payDataCollection);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.PayDataCollectionDAO#delete(com.payasia.dao.bean.
	 * PayDataCollection)
	 */
	@Override
	public void delete(PayDataCollection payDataCollection) {
		super.delete(payDataCollection);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.PayDataCollectionDAO#findByID(long)
	 */
	@Override
	public PayDataCollection findByID(long payDataCollectionId) {

		return super.findById(PayDataCollection.class, payDataCollectionId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.PayDataCollectionDAO#getCountForCondition(com.payasia
	 * .common.dto.PayDataCollectionConditionDTO, java.lang.Long)
	 */
	@Override
	public int getCountForCondition(
			PayDataCollectionConditionDTO payDataCollectionConditionDTO,
			Long companyId) {
		return findByCondition(payDataCollectionConditionDTO, companyId, null,
				null).size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.PayDataCollectionDAO#getCountForAll(com.payasia.common
	 * .dto.PayCodeDataConditionDTO)
	 */
	@Override
	public int getCountForAll(PayCodeDataConditionDTO payCodeDataConditionDTO) {

		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.PayDataCollectionDAO#getPayDataCollectionForEdit(java
	 * .lang.Long, java.lang.Long)
	 */
	@Override
	public PayDataCollection getPayDataCollectionForEdit(Long companyId,
			Long payDataCollectionId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<PayDataCollection> criteriaQuery = cb
				.createQuery(PayDataCollection.class);
		Root<PayDataCollection> payDataRoot = criteriaQuery
				.from(PayDataCollection.class);

		criteriaQuery.select(payDataRoot);

		Join<PayDataCollection, Company> payDataCollectionCompanyJoin = payDataRoot
				.join(PayDataCollection_.company);
		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction,
				cb.equal(payDataCollectionCompanyJoin.get(Company_.companyId),
						companyId));
		restriction = cb.and(restriction, cb.equal(
				payDataRoot.get(PayDataCollection_.payDataCollectionId),
				payDataCollectionId));

		criteriaQuery.where(restriction);

		TypedQuery<PayDataCollection> payDataCollectionQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<PayDataCollection> payDataCollectionList = payDataCollectionQuery
				.getResultList();
		if (payDataCollectionList != null &&  !payDataCollectionList.isEmpty()) {
			return payDataCollectionList.get(0);
		}
		return null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.PayDataCollectionDAO#findByPayCode(java.lang.Long,
	 * java.lang.Long)
	 */
	@Override
	public List<PayDataCollection> findByPayCode(Long companyId, Long payCodeId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<PayDataCollection> criteriaQuery = cb
				.createQuery(PayDataCollection.class);
		Root<PayDataCollection> payDataRoot = criteriaQuery
				.from(PayDataCollection.class);

		criteriaQuery.select(payDataRoot);

		Join<PayDataCollection, Company> PayDataCollectionCompanyJoin = payDataRoot
				.join(PayDataCollection_.company);
		Join<PayDataCollection, Paycode> PayDataCollectionPaycodeJoin = payDataRoot
				.join(PayDataCollection_.paycode);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(PayDataCollectionPaycodeJoin.get(Paycode_.paycodeID),
						payCodeId));
		restriction = cb.and(restriction,
				cb.equal(PayDataCollectionCompanyJoin.get(Company_.companyId),
						companyId));

		criteriaQuery.where(restriction);

		TypedQuery<PayDataCollection> payDataCollectionQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<PayDataCollection> payDataCollectionList = payDataCollectionQuery
				.getResultList();

		return payDataCollectionList;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.PayDataCollectionDAO#deleteByCondition(java.lang.Long,
	 * java.lang.Long)
	 */
	@Override
	public void deleteByCondition(Long enployeeId, Long companyId) {

		String queryString = "DELETE FROM PayDataCollection d WHERE d.company.companyId = :company AND d.employee.employeeId = :employee";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("employee", enployeeId);
		q.setParameter("company", companyId);

		q.executeUpdate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.PayDataCollectionDAO#newTranDeleteByCondition(java.lang
	 * .Long, java.lang.Long)
	 */
	@Override
	public void deleteByEmpCondition(Long enployeeId, Long companyId) {

		String queryString = "DELETE FROM PayDataCollection d WHERE d.company.companyId = :company AND d.employee.employeeId = :employee";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("employee", enployeeId);
		q.setParameter("company", companyId);

		q.executeUpdate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.PayDataCollectionDAO#saveReturn(com.payasia.dao.bean.
	 * PayDataCollection)
	 */
	@Override
	public PayDataCollection saveReturn(PayDataCollection payDataCollection) {
		PayDataCollection persistObj = payDataCollection;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (PayDataCollection) getBaseEntity();
			beanUtil.copyProperties(persistObj, payDataCollection);
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
	 * com.payasia.dao.PayDataCollectionDAO#newTranSaveReturn(com.payasia.dao
	 * .bean.PayDataCollection)
	 */
	@Override
	public PayDataCollection newTranSaveReturn(
			PayDataCollection payDataCollection) {
		PayDataCollection persistObj = payDataCollection;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (PayDataCollection) getBaseEntity();
			beanUtil.copyProperties(persistObj, payDataCollection);
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
	 * @see com.payasia.dao.PayDataCollectionDAO#findByCondition(java.util.Map,
	 * java.util.List, java.lang.Long, java.lang.String)
	 */
	@Override
	public List<Object[]> findByCondition(
			Map<String, DataImportKeyValueDTO> colMap,
			List<ExcelExportFiltersForm> finalFilterList, Long companyId,
			String dateFormat) {
		Map<String, String> paramValueMap = new HashMap<String, String>();

		ExcelExportQueryDTO selectDTO = createSelect(colMap);
		String from = createFrom(colMap, selectDTO);
		String where = createWhere(companyId, finalFilterList, dateFormat,
				paramValueMap);
		String queryString = selectDTO.getSelectQuery() + from + where;

		Query q = entityManagerFactory.createNativeQuery(queryString);
		q.setParameter("companyId", companyId);
		for (Map.Entry<String, String> entry : paramValueMap.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}

		List<Object[]> tupleList = q.getResultList();

		return tupleList;

	}

	/**
	 * Creates the where.
	 * 
	 * @param companyId
	 *            the company id
	 * @param finalFilterList
	 *            the final filter list
	 * @param dateFormat
	 *            the date format
	 * @return the string
	 */
	private String createWhere(Long companyId,
			List<ExcelExportFiltersForm> finalFilterList, String dateFormat,
			Map<String, String> paramValueMap) {

		String where = " WHERE payDataCollection.Company_ID =:companyId ";

		if ( !finalFilterList.isEmpty()) {
			where = where + " AND ";
		}

		int count = 1;
		for (ExcelExportFiltersForm excelExportFiltersForm : finalFilterList) {

			if (excelExportFiltersForm.getDataImportKeyValueDTO()
					.getMethodName().equalsIgnoreCase("EmployeeNumber")) {

				where = where
						+ " "
						+ excelExportFiltersForm.getOpenBracket()
						+ " employee."
						+ excelExportFiltersForm.getDataImportKeyValueDTO()
								.getActualColName() + " ";

			} else if (excelExportFiltersForm.getDataImportKeyValueDTO()
					.getMethodName().equalsIgnoreCase("Paycode")) {
				where = where
						+ " "
						+ excelExportFiltersForm.getOpenBracket()
						+ " paycode."
						+ excelExportFiltersForm.getDataImportKeyValueDTO()
								.getActualColName() + " ";
			} else {

				where = where
						+ " "
						+ excelExportFiltersForm.getOpenBracket()
						+ " payDataCollection."
						+ excelExportFiltersForm.getDataImportKeyValueDTO()
								.getActualColName() + " ";
			}

			if (count < finalFilterList.size()) {
				paramValueMap.put("param" + count,
						excelExportFiltersForm.getValue());
				where = where + excelExportFiltersForm.getEqualityOperator()
						+ ":param" + count + " "
						+ excelExportFiltersForm.getCloseBracket() + " "
						+ excelExportFiltersForm.getLogicalOperator() + " ";

			} else {
				paramValueMap.put("param" + count,
						excelExportFiltersForm.getValue());
				where = where + excelExportFiltersForm.getEqualityOperator()
						+ ":param" + count + " "
						+ excelExportFiltersForm.getCloseBracket() + " ";

			}
			count++;
		}

		where = where
				+ " ORDER BY payDataCollection.Pay_Data_Collection_ID DESC";

		return where;
	}

	/**
	 * Creates the from.
	 * 
	 * @param colMap
	 *            the col map
	 * @param selectDTO
	 *            the select dto
	 * @return the string
	 */
	private String createFrom(Map<String, DataImportKeyValueDTO> colMap,
			ExcelExportQueryDTO selectDTO) {

		String from = "FROM Pay_Data_Collection AS payDataCollection LEFT OUTER JOIN Employee AS employee ON (payDataCollection.Employee_ID = employee.Employee_ID) ";
		from = from
				+ "LEFT OUTER JOIN Paycode AS paycode ON (payDataCollection.Paycode_ID = paycode.Paycode_ID) ";

		return from;
	}

	/**
	 * Creates the select.
	 * 
	 * @param colMap
	 *            the col map
	 * @return the excel export query dto
	 */
	private ExcelExportQueryDTO createSelect(
			Map<String, DataImportKeyValueDTO> colMap) {
		ExcelExportQueryDTO excelExportQueryDTO = new ExcelExportQueryDTO();

		String selectQuery = "SELECT ";
		Set<String> xlKeySet = colMap.keySet();

		int count = 1;
		for (Iterator<String> itr = xlKeySet.iterator(); itr.hasNext();) {

			String key = (String) itr.next();
			DataImportKeyValueDTO valueDTO = (DataImportKeyValueDTO) colMap
					.get(key);
			key = key.replaceAll("\\.", "_");
			key = "[" + key + "]";

			if (count < xlKeySet.size()) {
				if ("EmployeeNumber".equalsIgnoreCase(valueDTO.getMethodName())) {
					selectQuery = selectQuery + "employee.Employee_Number"
							+ " AS " + key + ", ";
				} else if ("Paycode".equalsIgnoreCase(valueDTO.getMethodName())) {
					selectQuery = selectQuery + "paycode.Paycode" + " AS "
							+ key + ", ";
				} else {
					String methodName = valueDTO.getActualColName();

					selectQuery = selectQuery + "payDataCollection."
							+ methodName + " AS " + key + ", ";
				}
			} else {

				if ("EmployeeNumber".equalsIgnoreCase(valueDTO.getMethodName())) {
					selectQuery = selectQuery + "employee.Employee_Number"
							+ " AS " + key + " ";
				} else if ("Paycode".equalsIgnoreCase(valueDTO.getMethodName())) {
					selectQuery = selectQuery + "paycode.Paycode" + " AS "
							+ key + " ";
				} else {
					String methodName = valueDTO.getActualColName();

					selectQuery = selectQuery + "payDataCollection."
							+ methodName + " AS " + key + " ";
				}

			}
			count++;
		}
		excelExportQueryDTO.setSelectQuery(selectQuery);
		return excelExportQueryDTO;
	}

}
