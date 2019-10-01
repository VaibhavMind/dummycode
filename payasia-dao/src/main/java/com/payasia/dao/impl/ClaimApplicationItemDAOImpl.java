package com.payasia.dao.impl;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.hibernate.jdbc.Work;
import org.springframework.stereotype.Repository;

import com.payasia.common.dto.AddClaimDTO;
import com.payasia.common.dto.ClaimItemBalanceDTO;
import com.payasia.common.dto.ValidationClaimItemDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.NullAwareBeanUtilsBean;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.SortConstants;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.ClaimApplicationItemDAO;
import com.payasia.dao.bean.ClaimApplication;
import com.payasia.dao.bean.ClaimApplicationItem;
import com.payasia.dao.bean.ClaimApplicationItem_;
import com.payasia.dao.bean.ClaimApplicationReviewer;
import com.payasia.dao.bean.ClaimApplicationReviewer_;
import com.payasia.dao.bean.ClaimApplicationWorkflow;
import com.payasia.dao.bean.ClaimApplicationWorkflow_;
import com.payasia.dao.bean.ClaimApplication_;
import com.payasia.dao.bean.ClaimCategoryMaster;
import com.payasia.dao.bean.ClaimCategoryMaster_;
import com.payasia.dao.bean.ClaimItemMaster;
import com.payasia.dao.bean.ClaimItemMaster_;
import com.payasia.dao.bean.ClaimStatusMaster;
import com.payasia.dao.bean.ClaimStatusMaster_;
import com.payasia.dao.bean.ClaimTemplate;
import com.payasia.dao.bean.ClaimTemplateItem;
import com.payasia.dao.bean.ClaimTemplateItemGeneral;
import com.payasia.dao.bean.ClaimTemplateItemGeneral_;
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
public class ClaimApplicationItemDAOImpl extends BaseDAO implements ClaimApplicationItemDAO {

	@Override
	protected Object getBaseEntity() {
		ClaimApplicationItem claimApplicationItem = new ClaimApplicationItem();
		return claimApplicationItem;
	}

	@Override
	public void update(ClaimApplicationItem claimApplicationItem) {
		if (claimApplicationItem.getTaxAmount() == null) {
			claimApplicationItem.setTaxAmount(new BigDecimal(0));
		}
		// claimApplicationItem
		// .setAmountBeforeTax(claimApplicationItem.getClaimAmount()
		// .subtract(claimApplicationItem.getTaxAmount()));
		super.update(claimApplicationItem);
	}

	@Override
	public void save(ClaimApplicationItem claimApplicationItem) {
		if (claimApplicationItem.getForexReceiptAmount() != null && claimApplicationItem.getExchangeRate() != null) {
			claimApplicationItem.setClaimAmount(
					claimApplicationItem.getExchangeRate().multiply(claimApplicationItem.getForexReceiptAmount()));
		}
		if (claimApplicationItem.getTaxAmount() == null) {
			claimApplicationItem.setTaxAmount(new BigDecimal(0));
		}
		// claimApplicationItem
		// .setAmountBeforeTax(claimApplicationItem.getClaimAmount()
		// .subtract(claimApplicationItem.getTaxAmount()));
		super.save(claimApplicationItem);

	}

	@Override
	public void delete(ClaimApplicationItem claimApplicationItem) {
		super.delete(claimApplicationItem);
	}

	@Override
	public ClaimApplicationItem findById(long claimApplicationItemId) {

		ClaimApplicationItem claimApplicationItem = super.findById(ClaimApplicationItem.class, claimApplicationItemId);
		return claimApplicationItem;
	}

	@Override
	public ClaimApplicationItem saveReturn(ClaimApplicationItem claimApplicationItem) {
		if (claimApplicationItem.getForexReceiptAmount() != null && claimApplicationItem.getExchangeRate() != null) {
			//claimApplicationItem.setClaimAmount(
				//	claimApplicationItem.getExchangeRate().multiply(claimApplicationItem.getForexReceiptAmount()));
		}
		if (claimApplicationItem.getTaxAmount() == null) {
			claimApplicationItem.setTaxAmount(new BigDecimal(0));
		}
		// claimApplicationItem
		// .setAmountBeforeTax(claimApplicationItem.getClaimAmount()
		// .subtract(claimApplicationItem.getTaxAmount()));

		ClaimApplicationItem persistObj = claimApplicationItem;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (ClaimApplicationItem) getBaseEntity();
			beanUtil.copyProperties(persistObj, claimApplicationItem);
		} catch (IllegalAccessException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		} catch (InvocationTargetException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		}
		this.entityManagerFactory.persist(persistObj);
		return persistObj;
	}

	@Override
	public List<ClaimApplicationItem> findByCondition(Long claimApplicationId, PageRequest pageDTO,
			SortCondition sortDTO) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ClaimApplicationItem> criteriaQuery = cb.createQuery(ClaimApplicationItem.class);
		Root<ClaimApplicationItem> claimApplicationItemRoot = criteriaQuery.from(ClaimApplicationItem.class);
		criteriaQuery.select(claimApplicationItemRoot);

		Predicate restriction = cb.conjunction();

		Join<ClaimApplicationItem, ClaimApplication> claimAppJoin = claimApplicationItemRoot
				.join(ClaimApplicationItem_.claimApplication);
		
		restriction = cb.and(restriction,
				cb.equal(claimAppJoin.get(ClaimApplication_.claimApplicationId), claimApplicationId));

		criteriaQuery.where(restriction);
		
		if (sortDTO != null) {

			Path<String> sortPath = getSortPathForClaimTemplateItem(sortDTO, claimApplicationItemRoot, claimAppJoin);
			if (sortPath != null) {
				if (SortConstants.DB_ORDER_BY_ASC.equalsIgnoreCase(sortDTO.getOrderType())) {
					criteriaQuery.orderBy(cb.asc(sortPath));
				}
				if (SortConstants.DB_ORDER_BY_DESC.equalsIgnoreCase(sortDTO.getOrderType())) {
					criteriaQuery.orderBy(cb.desc(sortPath));
				}
			}
		}
		
		TypedQuery<ClaimApplicationItem> claimAppTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			claimAppTypedQuery.setFirstResult(getStartPosition(pageDTO));
			claimAppTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		return claimAppTypedQuery.getResultList();
	}

	@Override
	public Integer getCountForCondition(Long claimApplicationId, PageRequest pageDTO, SortCondition sortDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Integer> criteriaQuery = cb.createQuery(Integer.class);
		Root<ClaimApplicationItem> claimApplicationItemRoot = criteriaQuery.from(ClaimApplicationItem.class);
		criteriaQuery.select(cb.count(claimApplicationItemRoot).as(Integer.class));

		Predicate restriction = cb.conjunction();

		Join<ClaimApplicationItem, ClaimApplication> claimAppJoin = claimApplicationItemRoot
				.join(ClaimApplicationItem_.claimApplication);

		restriction = cb.and(restriction,
				cb.equal(claimAppJoin.get(ClaimApplication_.claimApplicationId), claimApplicationId));

		criteriaQuery.where(restriction);

		TypedQuery<Integer> claimAppTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		return claimAppTypedQuery.getSingleResult();
	}

	@Override
	public ValidationClaimItemDTO validateClaimItem(final ClaimApplicationItem claimApplicationItem,
			final Boolean isAdmin, final Boolean isManager) {
		final ValidationClaimItemDTO validationClaimItemDTO = new ValidationClaimItemDTO();

		getSession().doWork(new Work() {

			@Override
			public void execute(Connection connection) throws SQLException {
				try {
					CallableStatement cstmt = null;
					cstmt = connection
							.prepareCall("{call Validate_Claim_Application_Item (?,?,?,?,?,?,?,?,?,?,?,?,?)}");

					cstmt.setLong("@Employee_ID",
							claimApplicationItem.getClaimApplication().getEmployee().getEmployeeId());
					cstmt.setLong("@Employee_Claim_Application_Id",
							claimApplicationItem.getClaimApplication().getClaimApplicationId());
					cstmt.setLong("@Claim_Application_Item_Id", claimApplicationItem.getClaimApplicationItemId());
					cstmt.setLong("@Employee_Claim_Template_Item_ID",
							claimApplicationItem.getEmployeeClaimTemplateItem().getEmployeeClaimTemplateItemId());
					cstmt.setString("@Receipt_Number", claimApplicationItem.getReceiptNumber());
					cstmt.setTimestamp("@Receipt_Date", claimApplicationItem.getClaimDate());
					if (claimApplicationItem.getApplicableClaimAmount() == null
							|| claimApplicationItem.getApplicableClaimAmount().compareTo(BigDecimal.ZERO) == 0) {
						cstmt.setFloat("@Claim_Amount",
								Float.parseFloat(String.valueOf(claimApplicationItem.getClaimAmount())));
					} else {
						cstmt.setFloat("@Claim_Amount",
								Float.parseFloat(String.valueOf(claimApplicationItem.getApplicableClaimAmount())));
					}
					cstmt.setString("@Remarks", claimApplicationItem.getRemarks());
					cstmt.setBoolean("@Is_Admin", isAdmin);
					cstmt.setBoolean("@Is_Manager", isManager);

					cstmt.registerOutParameter("@Error_Code", java.sql.Types.INTEGER);
					cstmt.registerOutParameter("@Error_Key", java.sql.Types.VARCHAR);
					cstmt.registerOutParameter("@Error_Value", java.sql.Types.VARCHAR);

					cstmt.execute();

					validationClaimItemDTO.setErrorCode(cstmt.getInt("@Error_Code"));
					validationClaimItemDTO.setErrorKey(cstmt.getString("@Error_Key"));
					validationClaimItemDTO.setErrorValue(cstmt.getString("@Error_Value"));

				} catch (SQLException e) {
					throw new PayAsiaSystemException(e.getMessage(), e);
				}

			}
		});
		return validationClaimItemDTO;
	}

	@Override
	public ClaimItemBalanceDTO getClaimItemBal(final Long employeeClaimTemplateItemId, final Long companyId,
			final Long claimApplicationId, final Long claimApplicationItemId, final boolean isPreviousYearClaim) {

		final ClaimItemBalanceDTO claimItemBalanceDTO = new ClaimItemBalanceDTO();

		getSession().doWork(new Work() {

			@Override
			public void execute(Connection connection) throws SQLException {
				try {
					CallableStatement cstmt = null;
					/*cstmt = connection.prepareCall("{call Get_Employee_Claim_Balance (?,?,?,?,?,?,?,?,?,?)}");
					cstmt.setBoolean("@IsPreviousYearClaim", isPreviousYearClaim);*/

					cstmt = connection.prepareCall("{call Get_Employee_Claim_Balance (?,?,?,?,?,?,?,?,?)}");
					
					cstmt.setLong("@Employee_Claim_Template_Item_ID", employeeClaimTemplateItemId);
					cstmt.setLong("@Employee_Claim_Application_Id", claimApplicationId);
					cstmt.setLong("@Claim_Application_Item_Id", claimApplicationItemId);
					
					cstmt.registerOutParameter("@Has_Claim_Limit", java.sql.Types.BOOLEAN);
					cstmt.registerOutParameter("@Claim_Balance", java.sql.Types.FLOAT);
					cstmt.registerOutParameter("@Entitlement_Type", java.sql.Types.VARCHAR);
					cstmt.registerOutParameter("@Error_Code", java.sql.Types.INTEGER);
					cstmt.registerOutParameter("@Error_Key", java.sql.Types.VARCHAR);
					cstmt.registerOutParameter("@Error_Value", java.sql.Types.VARCHAR);

					cstmt.execute();

					claimItemBalanceDTO.setEntitlementType(cstmt.getString("@Entitlement_Type"));
					claimItemBalanceDTO.setErrorCode(cstmt.getInt("@Error_Code"));
					claimItemBalanceDTO.setErrorKey(cstmt.getString("@Error_Key"));
					claimItemBalanceDTO.setErrorValue(cstmt.getString("@Error_Value"));

					claimItemBalanceDTO.setClaimItemBalance(cstmt.getFloat("@Claim_Balance"));
					claimItemBalanceDTO.setHasClaimLimit(cstmt.getBoolean("@Has_Claim_Limit"));

				} catch (SQLException e) {
					throw new PayAsiaSystemException(e.getMessage(), e);
				}

			}
		});
		return claimItemBalanceDTO;

	}

	@Override
	public boolean openToDependentsExists(long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ClaimTemplateItemGeneral> criteriaQuery = cb.createQuery(ClaimTemplateItemGeneral.class);
		Root<ClaimTemplateItemGeneral> claimTIGeneral = criteriaQuery.from(ClaimTemplateItemGeneral.class);
		criteriaQuery.select(claimTIGeneral);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(claimTIGeneral.get(ClaimTemplateItemGeneral_.companyId), companyId));
		restriction = cb.and(restriction, cb.equal(claimTIGeneral.get(ClaimTemplateItemGeneral_.openToDependents), 1));

		criteriaQuery.where(restriction);
		TypedQuery<ClaimTemplateItemGeneral> claimTemplateItemGeneral = entityManagerFactory.createQuery(criteriaQuery);
		List<ClaimTemplateItemGeneral> elems = claimTemplateItemGeneral.getResultList();
		if (!elems.isEmpty())
			return true;
		else
			return false;

	}

	@Override
	public void deleteByCondition(Long claimApplicationId) {

		String queryString = "DELETE FROM ClaimApplicationItem CAI WHERE CAI.claimApplication.claimApplicationId = :claimApplicationId";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("claimApplicationId", claimApplicationId);
		q.executeUpdate();
	}

	@Override
	public List<ClaimApplicationItem> findByCondition(Long companyId, String startDate, String endDate,
			List<Long> claimTemplateIdList, Long claimCategoryId, List<Long> claimItemIdList,
			List<String> claimStatusList, String groupByName, List<BigInteger> employeeIds) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ClaimApplicationItem> criteriaQuery = cb.createQuery(ClaimApplicationItem.class);
		Root<ClaimApplicationItem> claimAppItemRoot = criteriaQuery.from(ClaimApplicationItem.class);
		criteriaQuery.select(claimAppItemRoot);

		Join<ClaimApplicationItem, ClaimApplication> claimAppJoin = claimAppItemRoot
				.join(ClaimApplicationItem_.claimApplication);
		Join<ClaimApplication, Employee> claimAppEmpJoin = claimAppJoin.join(ClaimApplication_.employee);
		Join<ClaimApplication, Company> companyJoin = claimAppJoin.join(ClaimApplication_.company);

		Join<ClaimApplicationItem, EmployeeClaimTemplateItem> empClaimTempItemJoin = claimAppItemRoot
				.join(ClaimApplicationItem_.employeeClaimTemplateItem);

		Join<EmployeeClaimTemplateItem, ClaimTemplateItem> claimTempItemJoin = empClaimTempItemJoin
				.join(EmployeeClaimTemplateItem_.claimTemplateItem);
		Join<ClaimTemplateItem, ClaimItemMaster> claimItemJoin = claimTempItemJoin
				.join(ClaimTemplateItem_.claimItemMaster);

		Join<ClaimApplication, ClaimStatusMaster> claimStatusJoin = claimAppJoin
				.join(ClaimApplication_.claimStatusMaster);

		Join<ClaimApplication, EmployeeClaimTemplate> empClaimTempJoin = claimAppJoin
				.join(ClaimApplication_.employeeClaimTemplate);

		Join<EmployeeClaimTemplate, ClaimTemplate> claimTempJoin = empClaimTempJoin
				.join(EmployeeClaimTemplate_.claimTemplate);

		Join<ClaimApplication, ClaimApplicationWorkflow> claimAppWorkflowJoin = claimAppJoin
				.join(ClaimApplication_.claimApplicationWorkflows, JoinType.LEFT);
		Join<ClaimApplicationWorkflow, ClaimStatusMaster> claimAppWorkflowStatusJoin = claimAppWorkflowJoin
				.join(ClaimApplicationWorkflow_.claimStatusMaster);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, claimStatusJoin.get(ClaimStatusMaster_.claimStatusName).in(claimStatusList));

		if (StringUtils.isNotBlank(startDate)) {
			restriction = cb.and(restriction,
					cb.greaterThanOrEqualTo(
							(claimAppWorkflowJoin.get(ClaimApplicationWorkflow_.createdDate).as(Date.class)),
							DateUtils.stringToTimestamp(startDate)));
		}
		if (StringUtils.isNotBlank(endDate)) {
			restriction = cb.and(restriction,
					cb.lessThanOrEqualTo(
							(claimAppWorkflowJoin.get(ClaimApplicationWorkflow_.createdDate).as(Date.class)),
							DateUtils.stringToTimestamp(endDate)));
		}

		if (claimTemplateIdList.size() > 0) {
			restriction = cb.and(restriction,
					claimTempJoin.get(ClaimTemplate_.claimTemplateId).in(claimTemplateIdList));
		}

		if (claimItemIdList.size() > 0) {
			restriction = cb.and(restriction, claimItemJoin.get(ClaimItemMaster_.claimItemId).in(claimItemIdList));
		}
		restriction = cb.and(restriction, cb.equal(companyJoin.get(Company_.companyId), companyId));
		// restriction = cb.and(restriction,
		// cb.equal(claimAppEmpJoin.get(Employee_.status), true));

		if (!employeeIds.isEmpty()) {
			restriction = cb.and(restriction, claimAppEmpJoin.get(Employee_.employeeId).in(employeeIds));
		}
		restriction = cb.and(restriction,
				claimAppWorkflowStatusJoin.get(ClaimStatusMaster_.claimStatusName).in(claimStatusList));

		criteriaQuery.where(restriction);

		if (StringUtils.isNotBlank(groupByName)) {

			if (groupByName.equals(PayAsiaConstants.CLAIM_REPORTS_GROUP_BY_CLAIM_ITEM)) {
				criteriaQuery.orderBy(cb.asc(claimItemJoin.get(ClaimItemMaster_.claimItemName)));
			}
			if (groupByName.equals(PayAsiaConstants.CLAIM_REPORTS_GROUP_BY_CLAIM_TEMPLATE)) {
				criteriaQuery.orderBy(cb.asc(claimTempJoin.get(ClaimTemplate_.templateName)));
			}
			if (groupByName.equals(PayAsiaConstants.CLAIM_REPORTS_GROUP_BY_MONTH)) {
				criteriaQuery.orderBy(cb.asc(
						cb.function("month", Integer.class, claimAppItemRoot.get(ClaimApplicationItem_.claimDate))));

			}
		}

		TypedQuery<ClaimApplicationItem> claimAppItemTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		return claimAppItemTypedQuery.getResultList();
	}

	@Override
	public List<ClaimApplicationItem> findByCategoryWiseCondition(Long companyId, String startDate, String endDate,
			List<Long> claimTemplateIdList, List<Long> claimCategoryId, List<Long> claimItemIdList,
			List<String> claimStatusList, String groupByName, List<BigInteger> employeeIds) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ClaimApplicationItem> criteriaQuery = cb.createQuery(ClaimApplicationItem.class);
		Root<ClaimApplicationItem> claimAppItemRoot = criteriaQuery.from(ClaimApplicationItem.class);
		criteriaQuery.select(claimAppItemRoot);

		Join<ClaimApplicationItem, ClaimApplication> claimAppJoin = claimAppItemRoot
				.join(ClaimApplicationItem_.claimApplication);
		Join<ClaimApplication, Employee> claimAppEmpJoin = claimAppJoin.join(ClaimApplication_.employee);
		Join<ClaimApplication, Company> companyJoin = claimAppJoin.join(ClaimApplication_.company);

		Join<ClaimApplicationItem, EmployeeClaimTemplateItem> empClaimTempItemJoin = claimAppItemRoot
				.join(ClaimApplicationItem_.employeeClaimTemplateItem);

		Join<EmployeeClaimTemplateItem, ClaimTemplateItem> claimTempItemJoin = empClaimTempItemJoin
				.join(EmployeeClaimTemplateItem_.claimTemplateItem);
		Join<ClaimTemplateItem, ClaimItemMaster> claimItemJoin = claimTempItemJoin
				.join(ClaimTemplateItem_.claimItemMaster);

		Join<ClaimItemMaster, ClaimCategoryMaster> claimItemCategoryJoin = claimItemJoin
				.join(ClaimItemMaster_.claimCategoryMaster);

		Join<ClaimApplication, ClaimStatusMaster> claimStatusJoin = claimAppJoin
				.join(ClaimApplication_.claimStatusMaster);

		Join<ClaimApplication, EmployeeClaimTemplate> empClaimTempJoin = claimAppJoin
				.join(ClaimApplication_.employeeClaimTemplate);

		Join<EmployeeClaimTemplate, ClaimTemplate> claimTempJoin = empClaimTempJoin
				.join(EmployeeClaimTemplate_.claimTemplate);

		Join<ClaimApplication, ClaimApplicationWorkflow> claimAppWorkflowJoin = claimAppJoin
				.join(ClaimApplication_.claimApplicationWorkflows, JoinType.LEFT);
		Join<ClaimApplicationWorkflow, ClaimStatusMaster> claimAppWorkflowStatusJoin = claimAppWorkflowJoin
				.join(ClaimApplicationWorkflow_.claimStatusMaster);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, claimStatusJoin.get(ClaimStatusMaster_.claimStatusName).in(claimStatusList));

		if (StringUtils.isNotBlank(startDate)) {
			restriction = cb.and(restriction,
					cb.greaterThanOrEqualTo(
							(claimAppWorkflowJoin.get(ClaimApplicationWorkflow_.createdDate).as(Date.class)),
							DateUtils.stringToTimestamp(startDate)));
		}
		if (StringUtils.isNotBlank(endDate)) {
			restriction = cb.and(restriction,
					cb.lessThanOrEqualTo(
							(claimAppWorkflowJoin.get(ClaimApplicationWorkflow_.createdDate).as(Date.class)),
							DateUtils.stringToTimestamp(endDate)));
		}

		if (claimTemplateIdList.size() > 0) {
			restriction = cb.and(restriction,
					claimTempJoin.get(ClaimTemplate_.claimTemplateId).in(claimTemplateIdList));
		}

		if (claimItemIdList.size() > 0) {
			restriction = cb.and(restriction, claimItemJoin.get(ClaimItemMaster_.claimItemId).in(claimItemIdList));
		}

		if (claimCategoryId.size() > 0) {
			restriction = cb.and(restriction,
					claimItemCategoryJoin.get(ClaimCategoryMaster_.claimCategoryId).in(claimCategoryId));
		}

		restriction = cb.and(restriction, cb.equal(companyJoin.get(Company_.companyId), companyId));
		// restriction = cb.and(restriction,
		// cb.equal(claimAppEmpJoin.get(Employee_.status), true));

		if (!employeeIds.isEmpty()) {
			restriction = cb.and(restriction, claimAppEmpJoin.get(Employee_.employeeId).in(employeeIds));
		}
		restriction = cb.and(restriction,
				claimAppWorkflowStatusJoin.get(ClaimStatusMaster_.claimStatusName).in(claimStatusList));

		criteriaQuery.where(restriction);

		if (StringUtils.isNotBlank(groupByName)) {

			if (groupByName.equals(PayAsiaConstants.CLAIM_REPORTS_GROUP_BY_CLAIM_ITEM)) {
				criteriaQuery.orderBy(cb.asc(claimItemJoin.get(ClaimItemMaster_.claimItemName)));
			}
			if (groupByName.equals(PayAsiaConstants.CLAIM_REPORTS_GROUP_BY_CLAIM_TEMPLATE)) {
				criteriaQuery.orderBy(cb.asc(claimTempJoin.get(ClaimTemplate_.templateName)));
			}
			if (groupByName.equals(PayAsiaConstants.CLAIM_REPORTS_GROUP_BY_MONTH)) {
				criteriaQuery.orderBy(cb.asc(
						cb.function("month", Integer.class, claimAppItemRoot.get(ClaimApplicationItem_.claimDate))));

			}
		}

		TypedQuery<ClaimApplicationItem> claimAppItemTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		return claimAppItemTypedQuery.getResultList();
	}

	@Override
	public List<ClaimApplicationItem> findApplicationByBatch(Long companyId, Long claimBatchId,
			Timestamp claimBatchStartDate, Timestamp claimBatchEndDate, List<Long> claimTemplateIdList,
			Long claimCategoryId, List<Long> claimItemIdList, String groupByName, String claimStatusCompleted,
			List<BigInteger> employeeIds) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ClaimApplicationItem> criteriaQuery = cb.createQuery(ClaimApplicationItem.class);
		Root<ClaimApplicationItem> claimAppItemRoot = criteriaQuery.from(ClaimApplicationItem.class);
		criteriaQuery.select(claimAppItemRoot);

		Join<ClaimApplicationItem, ClaimApplication> claimAppJoin = claimAppItemRoot
				.join(ClaimApplicationItem_.claimApplication);
		Join<ClaimApplication, Employee> claimAppEmpJoin = claimAppJoin.join(ClaimApplication_.employee);
		Join<ClaimApplication, Company> companyJoin = claimAppJoin.join(ClaimApplication_.company);

		Join<ClaimApplicationItem, EmployeeClaimTemplateItem> empClaimTempItemJoin = claimAppItemRoot
				.join(ClaimApplicationItem_.employeeClaimTemplateItem);

		Join<EmployeeClaimTemplateItem, ClaimTemplateItem> claimTempItemJoin = empClaimTempItemJoin
				.join(EmployeeClaimTemplateItem_.claimTemplateItem);
		Join<ClaimTemplateItem, ClaimItemMaster> claimItemJoin = claimTempItemJoin
				.join(ClaimTemplateItem_.claimItemMaster);

		Join<ClaimApplication, ClaimStatusMaster> claimStatusJoin = claimAppJoin
				.join(ClaimApplication_.claimStatusMaster);

		Join<ClaimApplication, EmployeeClaimTemplate> empClaimTempJoin = claimAppJoin
				.join(ClaimApplication_.employeeClaimTemplate);

		Join<EmployeeClaimTemplate, ClaimTemplate> claimTempJoin = empClaimTempJoin
				.join(EmployeeClaimTemplate_.claimTemplate);

		Join<ClaimApplication, ClaimApplicationWorkflow> claimAppWorkflowJoin = claimAppJoin
				.join(ClaimApplication_.claimApplicationWorkflows, JoinType.LEFT);
		Join<ClaimApplicationWorkflow, ClaimStatusMaster> claimAppWorkflowStatusJoin = claimAppWorkflowJoin
				.join(ClaimApplicationWorkflow_.claimStatusMaster);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.greaterThanOrEqualTo(
				(claimAppWorkflowJoin.get(ClaimApplicationWorkflow_.createdDate).as(Date.class)), claimBatchStartDate));
		restriction = cb.and(restriction, cb.lessThanOrEqualTo(
				(claimAppWorkflowJoin.get(ClaimApplicationWorkflow_.createdDate).as(Date.class)), claimBatchEndDate));

		if (!claimTemplateIdList.isEmpty()) {
			restriction = cb.and(restriction,
					claimTempJoin.get(ClaimTemplate_.claimTemplateId).in(claimTemplateIdList));
		}

		if (!claimItemIdList.isEmpty()) {
			restriction = cb.and(restriction, claimItemJoin.get(ClaimItemMaster_.claimItemId).in(claimItemIdList));
		}
		restriction = cb.and(restriction,
				cb.equal(claimStatusJoin.get(ClaimStatusMaster_.claimStatusName), claimStatusCompleted));
		restriction = cb.and(restriction,
				cb.equal(claimAppWorkflowStatusJoin.get(ClaimStatusMaster_.claimStatusName), claimStatusCompleted));
		restriction = cb.and(restriction, cb.equal(companyJoin.get(Company_.companyId), companyId));
		// restriction = cb.and(restriction,
		// cb.equal(claimAppEmpJoin.get(Employee_.status), true));
		if (!employeeIds.isEmpty()) {
			restriction = cb.and(restriction, claimAppEmpJoin.get(Employee_.employeeId).in(employeeIds));
		}

		criteriaQuery.where(restriction);

		if (StringUtils.isNotBlank(groupByName)) {

			if (groupByName.equals(PayAsiaConstants.CLAIM_REPORTS_GROUP_BY_CLAIM_ITEM)) {
				criteriaQuery.orderBy(cb.asc(claimItemJoin.get(ClaimItemMaster_.claimItemName)));
			}
			if (groupByName.equals(PayAsiaConstants.CLAIM_REPORTS_GROUP_BY_CLAIM_TEMPLATE)) {
				criteriaQuery.orderBy(cb.asc(claimTempJoin.get(ClaimTemplate_.templateName)));
			}
			if (groupByName.equals(PayAsiaConstants.CLAIM_REPORTS_GROUP_BY_MONTH)) {
				criteriaQuery.orderBy(cb.asc(
						cb.function("month", Integer.class, claimAppItemRoot.get(ClaimApplicationItem_.claimDate))));

			}
		}

		TypedQuery<ClaimApplicationItem> claimAppItemTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		return claimAppItemTypedQuery.getResultList();
	}

	@Override
	public List<ClaimApplicationItem> findByCondition(Long claimApplicationId, List<SortCondition> sortConditions,
			String sortOrder) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ClaimApplicationItem> criteriaQuery = cb.createQuery(ClaimApplicationItem.class);
		Root<ClaimApplicationItem> claimApplicationItemRoot = criteriaQuery.from(ClaimApplicationItem.class);
		criteriaQuery.select(claimApplicationItemRoot);

		Predicate restriction = cb.conjunction();

		Join<ClaimApplicationItem, ClaimApplication> claimAppJoin = claimApplicationItemRoot
				.join(ClaimApplicationItem_.claimApplication);

		Join<ClaimApplicationItem, EmployeeClaimTemplateItem> empClaimTemplateItemJoin = claimApplicationItemRoot
				.join(ClaimApplicationItem_.employeeClaimTemplateItem);
		Join<EmployeeClaimTemplateItem, ClaimTemplateItem> claimTemplateItemJoin = empClaimTemplateItemJoin
				.join(EmployeeClaimTemplateItem_.claimTemplateItem);
		Join<ClaimTemplateItem, ClaimItemMaster> claimItemJoin = claimTemplateItemJoin
				.join(ClaimTemplateItem_.claimItemMaster);
		Join<ClaimItemMaster, ClaimCategoryMaster> claimCategoryJoin = claimItemJoin
				.join(ClaimItemMaster_.claimCategoryMaster);

		restriction = cb.and(restriction,
				cb.equal(claimAppJoin.get(ClaimApplication_.claimApplicationId), claimApplicationId));

		criteriaQuery.where(restriction);
		for (SortCondition sortBy : sortConditions) {
			if (sortBy.getColumnName().equalsIgnoreCase(PayAsiaConstants.CLAIMITEMDATESORTORDER)) {
				if (sortBy.getOrderType().equalsIgnoreCase(PayAsiaConstants.ASC)) {
					criteriaQuery.orderBy(cb.asc(claimApplicationItemRoot.get(ClaimApplicationItem_.claimDate)));
				} else {
					criteriaQuery.orderBy(cb.desc(claimApplicationItemRoot.get(ClaimApplicationItem_.claimDate)));
				}
			}
			if (sortBy.getColumnName().equalsIgnoreCase(PayAsiaConstants.ITEMNAMESORTORDER)) {
				if (sortBy.getOrderType().equalsIgnoreCase(PayAsiaConstants.ASC)) {
					criteriaQuery.orderBy(cb.asc(claimItemJoin.get(ClaimItemMaster_.claimItemName)));
				} else {
					criteriaQuery.orderBy(cb.desc(claimItemJoin.get(ClaimItemMaster_.claimItemName)));
				}
			}
		}
		if (sortConditions == null || (sortConditions != null && sortConditions.size() == 0)) {
			if (sortOrder.equalsIgnoreCase(PayAsiaConstants.ASC)) {
				criteriaQuery.orderBy(cb.asc(claimCategoryJoin.get(ClaimCategoryMaster_.claimCategoryName)),
						cb.asc(claimApplicationItemRoot.get(ClaimApplicationItem_.claimDate)));
			}  else if (sortOrder.equalsIgnoreCase(PayAsiaConstants.DESC)) {
				criteriaQuery.orderBy(cb.desc(claimCategoryJoin.get(ClaimCategoryMaster_.claimCategoryName)),
						cb.desc(claimApplicationItemRoot.get(ClaimApplicationItem_.claimDate)));
			}else if (sortOrder.equalsIgnoreCase("updatedDate")) {
				criteriaQuery.orderBy(cb.desc(claimApplicationItemRoot.get(ClaimApplicationItem_.updatedDate)));
			}
		}
		TypedQuery<ClaimApplicationItem> claimAppTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		return claimAppTypedQuery.getResultList();
	}

	/**
	 * Auto populated data
	 * 
	 * @param claimApplicationId
	 * @param sortConditions
	 * @param sortOrder
	 * @param categoryID
	 * @return
	 */
	public List<ClaimApplicationItem> findByConditions(Long claimApplicationId, List<SortCondition> sortConditions,
			String sortOrder, Long categoryID) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ClaimApplicationItem> criteriaQuery = cb.createQuery(ClaimApplicationItem.class);
		Root<ClaimApplicationItem> claimApplicationItemRoot = criteriaQuery.from(ClaimApplicationItem.class);
		criteriaQuery.select(claimApplicationItemRoot);

		Predicate restriction = cb.conjunction();

		Join<ClaimApplicationItem, ClaimApplication> claimAppJoin = claimApplicationItemRoot
				.join(ClaimApplicationItem_.claimApplication);

		Join<ClaimApplicationItem, EmployeeClaimTemplateItem> empClaimTemplateItemJoin = claimApplicationItemRoot
				.join(ClaimApplicationItem_.employeeClaimTemplateItem);
		Join<EmployeeClaimTemplateItem, ClaimTemplateItem> claimTemplateItemJoin = empClaimTemplateItemJoin
				.join(EmployeeClaimTemplateItem_.claimTemplateItem);
		Join<ClaimTemplateItem, ClaimItemMaster> claimItemJoin = claimTemplateItemJoin
				.join(ClaimTemplateItem_.claimItemMaster);
		Join<ClaimItemMaster, ClaimCategoryMaster> claimCategoryJoin = claimItemJoin
				.join(ClaimItemMaster_.claimCategoryMaster);

		restriction = cb.and(restriction,
				cb.equal(claimAppJoin.get(ClaimApplication_.claimApplicationId), claimApplicationId));
		if (categoryID != null && categoryID != 0) {
			restriction = cb.and(restriction,
					cb.equal(claimCategoryJoin.get(ClaimCategoryMaster_.claimCategoryId), categoryID));
		}
		criteriaQuery.where(restriction);
		for (SortCondition sortBy : sortConditions) {

			if (sortBy.getColumnName().equalsIgnoreCase(PayAsiaConstants.CLAIMITEMDATESORTORDER)) {
				if (sortBy.getOrderType().equalsIgnoreCase(PayAsiaConstants.ASC)) {
					criteriaQuery.orderBy(cb.asc(claimApplicationItemRoot.get(ClaimApplicationItem_.claimDate)));
				} else {
					criteriaQuery.orderBy(cb.desc(claimApplicationItemRoot.get(ClaimApplicationItem_.claimDate)));
				}
			}
			if (sortBy.getColumnName().equalsIgnoreCase(PayAsiaConstants.ITEMNAMESORTORDER)) {
				if (sortBy.getOrderType().equalsIgnoreCase(PayAsiaConstants.ASC)) {
					criteriaQuery.orderBy(cb.asc(claimItemJoin.get(ClaimItemMaster_.claimItemName)));
				} else {
					criteriaQuery.orderBy(cb.desc(claimItemJoin.get(ClaimItemMaster_.claimItemName)));
				}
			}
		}
		if (sortConditions == null || sortConditions.size() == 0) {
			if (sortOrder.equalsIgnoreCase(PayAsiaConstants.ASC)) {
				criteriaQuery.orderBy(cb.asc(claimCategoryJoin.get(ClaimCategoryMaster_.claimCategoryName)),
						cb.asc(claimApplicationItemRoot.get(ClaimApplicationItem_.claimDate)));
			} else if (sortOrder.equalsIgnoreCase("updatedDate")) {
				criteriaQuery.orderBy(cb.desc(claimApplicationItemRoot.get(ClaimApplicationItem_.updatedDate)));
			} else {
				criteriaQuery.orderBy(cb.asc(claimCategoryJoin.get(ClaimCategoryMaster_.claimCategoryName)),
						cb.desc(claimApplicationItemRoot.get(ClaimApplicationItem_.claimDate)));
			}
		}
		TypedQuery<ClaimApplicationItem> claimAppTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		return claimAppTypedQuery.getResultList();
	}
	
	@Override
	public List<ClaimApplicationItem> findByCondition(Long claimApplicationId, String sortOrder) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ClaimApplicationItem> criteriaQuery = cb.createQuery(ClaimApplicationItem.class);
		Root<ClaimApplicationItem> claimApplicationItemRoot = criteriaQuery.from(ClaimApplicationItem.class);
		criteriaQuery.select(claimApplicationItemRoot);

		Predicate restriction = cb.conjunction();

		Join<ClaimApplicationItem, ClaimApplication> claimAppJoin = claimApplicationItemRoot
				.join(ClaimApplicationItem_.claimApplication);

		Join<ClaimApplicationItem, EmployeeClaimTemplateItem> empClaimTemplateItemJoin = claimApplicationItemRoot
				.join(ClaimApplicationItem_.employeeClaimTemplateItem);
		Join<EmployeeClaimTemplateItem, ClaimTemplateItem> claimTemplateItemJoin = empClaimTemplateItemJoin
				.join(EmployeeClaimTemplateItem_.claimTemplateItem);
		Join<ClaimTemplateItem, ClaimItemMaster> claimItemJoin = claimTemplateItemJoin
				.join(ClaimTemplateItem_.claimItemMaster);
		Join<ClaimItemMaster, ClaimCategoryMaster> claimCategoryJoin = claimItemJoin
				.join(ClaimItemMaster_.claimCategoryMaster);

		restriction = cb.and(restriction,
				cb.equal(claimAppJoin.get(ClaimApplication_.claimApplicationId), claimApplicationId));

		criteriaQuery.where(restriction);

		if (sortOrder.equalsIgnoreCase(PayAsiaConstants.ASC)) {
			criteriaQuery.orderBy(cb.asc(claimCategoryJoin.get(ClaimCategoryMaster_.claimCategoryName)),
					cb.asc(claimApplicationItemRoot.get(ClaimApplicationItem_.claimDate)));
		} else {
			criteriaQuery.orderBy(cb.asc(claimCategoryJoin.get(ClaimCategoryMaster_.claimCategoryName)),
					cb.desc(claimApplicationItemRoot.get(ClaimApplicationItem_.claimDate)));
		}

		TypedQuery<ClaimApplicationItem> claimAppTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		return claimAppTypedQuery.getResultList();
	}

	@Override
	public ClaimApplicationItem findByClaimApplicationItemId(AddClaimDTO addClaimDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ClaimApplicationItem> criteriaQuery = cb
				.createQuery(ClaimApplicationItem.class);
		Root<ClaimApplicationItem> claimApplicationItemRoot = criteriaQuery
				.from(ClaimApplicationItem.class);
		criteriaQuery.select(claimApplicationItemRoot);
		
		Join<ClaimApplicationItem, ClaimApplication> claimAppJoin = claimApplicationItemRoot
				.join(ClaimApplicationItem_.claimApplication);
		
		Join<ClaimApplication,Employee> empClaimJoin = claimAppJoin.join(ClaimApplication_.employee);
		Predicate restriction = cb.conjunction();
		if(!addClaimDTO.getAdmin())
		restriction = cb.and(restriction,
				cb.equal(empClaimJoin.get(Employee_.employeeId), addClaimDTO.getEmployeeId()));
		
		restriction = cb.and(restriction, cb.equal(claimAppJoin.get(ClaimApplication_.company), addClaimDTO.getCompanyId()));
		restriction = cb.and(restriction, cb.equal(claimApplicationItemRoot.get(ClaimApplicationItem_.claimApplicationItemId), addClaimDTO.getClaimApplicationItemId()));
		criteriaQuery.where(restriction); 
		TypedQuery<ClaimApplicationItem> empTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		List<ClaimApplicationItem> list = empTypedQuery.getResultList();
		return list!=null && !list.isEmpty()  ? list.get(0) :null;
			}
	@Override
	public ClaimApplicationItem findByClaimApplicationItemIdReview(AddClaimDTO addClaimDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ClaimApplicationItem> criteriaQuery = cb
				.createQuery(ClaimApplicationItem.class);
		Root<ClaimApplicationItem> claimApplicationItemRoot = criteriaQuery
				.from(ClaimApplicationItem.class);
		criteriaQuery.select(claimApplicationItemRoot);
		
		Join<ClaimApplicationItem, ClaimApplication> claimAppJoin = claimApplicationItemRoot
				.join(ClaimApplicationItem_.claimApplication);
		
		Join<ClaimApplication,ClaimApplicationReviewer> empClaimJoin = claimAppJoin.join(ClaimApplication_.claimApplicationReviewers);
		Predicate restriction = cb.conjunction();
		Join<ClaimApplicationReviewer,Employee> empJoin = empClaimJoin.join(ClaimApplicationReviewer_.employee);
		restriction = cb.and(restriction,
				cb.equal(empClaimJoin.get(ClaimApplicationReviewer_.pending), true));
		restriction = cb.and(restriction, cb.equal(empJoin.get(Employee_.employeeId), addClaimDTO.getEmployeeId()));
		
		restriction = cb.and(restriction, cb.equal(claimApplicationItemRoot.get(ClaimApplicationItem_.claimApplicationItemId), addClaimDTO.getClaimApplicationItemId()));
		criteriaQuery.where(restriction);
		TypedQuery<ClaimApplicationItem> empTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		List<ClaimApplicationItem> list = empTypedQuery.getResultList();
		return list!=null && !list.isEmpty()  ? list.get(0) :null;

	}
	
	
	@Override
	public List<ClaimApplicationItem> findByClaimReviewerCondition(Long companyId, String startDate, String endDate,
			List<Long> claimTemplateIdList, Long claimCategoryId, List<Long> claimItemIdList,
			List<String> claimStatusList, String groupByName, Long employeeIds, Boolean isIncludeResignedEmployees, Boolean isIncludeSubordinateEmployees,
			Boolean isCheckedFromCreatedDate) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ClaimApplicationItem> criteriaQuery = cb.createQuery(ClaimApplicationItem.class);
		Root<ClaimApplicationItem> claimAppItemRoot = criteriaQuery.from(ClaimApplicationItem.class);
		criteriaQuery.select(claimAppItemRoot).distinct(true);

		Join<ClaimApplicationItem, ClaimApplication> claimAppJoin = claimAppItemRoot
				.join(ClaimApplicationItem_.claimApplication);
		Join<ClaimApplication, Employee> claimAppEmpJoin = claimAppJoin.join(ClaimApplication_.employee);
		Join<ClaimApplication, Company> companyJoin = claimAppJoin.join(ClaimApplication_.company);

		Join<ClaimApplicationItem, EmployeeClaimTemplateItem> empClaimTempItemJoin = claimAppItemRoot
				.join(ClaimApplicationItem_.employeeClaimTemplateItem);

		Join<EmployeeClaimTemplateItem, ClaimTemplateItem> claimTempItemJoin = empClaimTempItemJoin
				.join(EmployeeClaimTemplateItem_.claimTemplateItem);
		Join<ClaimTemplateItem, ClaimItemMaster> claimItemJoin = claimTempItemJoin
				.join(ClaimTemplateItem_.claimItemMaster);

		Join<ClaimApplication, ClaimStatusMaster> claimStatusJoin = claimAppJoin
				.join(ClaimApplication_.claimStatusMaster);

		Join<ClaimApplication, EmployeeClaimTemplate> empClaimTempJoin = claimAppJoin
				.join(ClaimApplication_.employeeClaimTemplate);

		Join<EmployeeClaimTemplate, ClaimTemplate> claimTempJoin = empClaimTempJoin
				.join(EmployeeClaimTemplate_.claimTemplate);

		Join<ClaimApplication, ClaimApplicationWorkflow> claimAppWorkflowJoin = claimAppJoin
				.join(ClaimApplication_.claimApplicationWorkflows, JoinType.LEFT);
		
		Join<ClaimApplication, ClaimApplicationReviewer> claimAppRevWofJoin = claimAppJoin
				.join(ClaimApplication_.claimApplicationReviewers, JoinType.LEFT);
		
		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, claimStatusJoin.get(ClaimStatusMaster_.claimStatusName).in(claimStatusList));

		if(isCheckedFromCreatedDate){
			
			if (StringUtils.isNotBlank(startDate)) {
				restriction = cb.and(restriction,
						cb.greaterThanOrEqualTo(
								(claimAppWorkflowJoin.get(ClaimApplicationWorkflow_.createdDate).as(Date.class)),
								DateUtils.stringToTimestamp(startDate)));
				restriction = cb.and(restriction,
						cb.greaterThanOrEqualTo(
								(claimAppJoin.get(ClaimApplication_.createdDate).as(Date.class)),
								DateUtils.stringToTimestamp(startDate)));
			}
			if (StringUtils.isNotBlank(endDate)) {
				restriction = cb.and(restriction,
						cb.lessThanOrEqualTo(
								(claimAppWorkflowJoin.get(ClaimApplicationWorkflow_.createdDate).as(Date.class)),
								DateUtils.stringToTimestamp(endDate)));
				restriction = cb.and(restriction,
						cb.lessThanOrEqualTo(
								(claimAppJoin.get(ClaimApplication_.createdDate).as(Date.class)),
								DateUtils.stringToTimestamp(endDate)));
			}
			
		}else{
			
			if (StringUtils.isNotBlank(startDate)) {
				restriction = cb.and(restriction,
						cb.greaterThanOrEqualTo(
								(claimAppWorkflowJoin.get(ClaimApplicationWorkflow_.createdDate).as(Date.class)),
								DateUtils.stringToTimestamp(startDate)));
				
			}
			if (StringUtils.isNotBlank(endDate)) {
				restriction = cb.and(restriction,
						cb.lessThanOrEqualTo(
								(claimAppWorkflowJoin.get(ClaimApplicationWorkflow_.createdDate).as(Date.class)),
								DateUtils.stringToTimestamp(endDate)));
			}
		}

		if (claimTemplateIdList.size() > 0) {
			restriction = cb.and(restriction,
					claimTempJoin.get(ClaimTemplate_.claimTemplateId).in(claimTemplateIdList));
		}
		if (claimItemIdList.size() > 0) {
			restriction = cb.and(restriction, claimItemJoin.get(ClaimItemMaster_.claimItemId).in(claimItemIdList));
		}
		if(!isIncludeSubordinateEmployees){
			restriction = cb.and(restriction, cb.equal(companyJoin.get(Company_.companyId), companyId));
		}
		if(!isIncludeResignedEmployees){
			restriction = cb.and(restriction,cb.equal(claimAppEmpJoin.get(Employee_.status),true));
		}
		restriction = cb.and(restriction, cb.or(
											cb.and(
												cb.equal(claimAppRevWofJoin.get(ClaimApplicationReviewer_.pending), true),
												cb.equal(claimAppRevWofJoin.get(ClaimApplicationReviewer_.employee), employeeIds)
												),
											
											cb.and(
												cb.equal(claimAppWorkflowJoin.get(ClaimApplicationWorkflow_.employee), employeeIds),
												cb.notEqual(claimAppWorkflowJoin.get(ClaimApplicationWorkflow_.employee), claimAppJoin.get(ClaimApplication_.employee))
												)
											));

		criteriaQuery.where(restriction);
		if (StringUtils.isNotBlank(groupByName)) {
			if (groupByName.equals(PayAsiaConstants.CLAIM_REPORTS_GROUP_BY_CLAIM_ITEM)) {
				criteriaQuery.orderBy(cb.asc(claimItemJoin.get(ClaimItemMaster_.claimItemName)));
			}
			if (groupByName.equals(PayAsiaConstants.CLAIM_REPORTS_GROUP_BY_CLAIM_TEMPLATE)) {
				criteriaQuery.orderBy(cb.asc(claimTempJoin.get(ClaimTemplate_.templateName)));
			}
			if (groupByName.equals(PayAsiaConstants.CLAIM_REPORTS_GROUP_BY_MONTH)) {
				criteriaQuery.orderBy(cb.asc(
						cb.function("month", Integer.class, claimAppItemRoot.get(ClaimApplicationItem_.claimDate))));
			}
		}
		TypedQuery<ClaimApplicationItem> claimAppItemTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		return claimAppItemTypedQuery.getResultList();
	}
	
	private Path<String> getSortPathForClaimTemplateItem(SortCondition sortDTO, Root<ClaimApplicationItem> claimApplicationItemRoot, 
			Join<ClaimApplicationItem, ClaimApplication> claimAppJoin) {
		
		List<String> claimAppItemColList = new ArrayList<>();
		List<String> claimAppColList = new ArrayList<>();

		claimAppItemColList.add("receiptNumber");
		claimAppItemColList.add("claimAmount");
		claimAppItemColList.add("claimDate");
		// claimAppColList.add("claimItemName");
		// claimAppColList.add("claimItemStatus");

		Path<String> sortPath = null;

		if (claimAppItemColList.contains(sortDTO.getColumnName())) {
			sortPath = claimApplicationItemRoot.get(colMap.get(ClaimApplicationItem.class + sortDTO.getColumnName()));
		}
		if (claimAppColList.contains(sortDTO.getColumnName())) {
			sortPath = claimAppJoin.get(colMap.get(ClaimApplication.class + sortDTO.getColumnName()));
		}
		return sortPath;
	}
	
}