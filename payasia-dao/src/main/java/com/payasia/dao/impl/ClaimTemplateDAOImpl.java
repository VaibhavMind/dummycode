package com.payasia.dao.impl;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import org.apache.commons.lang.StringUtils;
import org.hibernate.jdbc.Work;
import org.springframework.stereotype.Repository;

import com.payasia.common.dto.AddClaimDTO;
import com.payasia.common.dto.ClaimTemplateConditionDTO;
import com.payasia.common.dto.EmployeeClaimSummaryDTO;
import com.payasia.common.dto.RollBackProcDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.NullAwareBeanUtilsBean;
import com.payasia.common.util.SortConstants;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.ClaimTemplateDAO;
import com.payasia.dao.bean.ClaimCategoryMaster;
import com.payasia.dao.bean.ClaimCategoryMaster_;
import com.payasia.dao.bean.ClaimItemMaster;
import com.payasia.dao.bean.ClaimItemMaster_;
import com.payasia.dao.bean.ClaimTemplate;
import com.payasia.dao.bean.ClaimTemplateItem;
import com.payasia.dao.bean.ClaimTemplateItemGeneral;
import com.payasia.dao.bean.ClaimTemplateItemGeneral_;
import com.payasia.dao.bean.ClaimTemplateItem_;
import com.payasia.dao.bean.ClaimTemplate_;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Company_;

@Repository
public class ClaimTemplateDAOImpl extends BaseDAO implements ClaimTemplateDAO {

	@Override
	protected Object getBaseEntity() {
		ClaimTemplate claimTemplate = new ClaimTemplate();
		return claimTemplate;
	}

	@Override
	public List<ClaimTemplate> getAllClaimTemplateByConditionCompany(Long companyId,
			ClaimTemplateConditionDTO conditionDTO, PageRequest pageDTO, SortCondition sortDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ClaimTemplate> criteriaQuery = cb.createQuery(ClaimTemplate.class);
		Root<ClaimTemplate> claimTempRoot = criteriaQuery.from(ClaimTemplate.class);
		criteriaQuery.select(claimTempRoot);

		Join<ClaimTemplate, Company> claimTempRootCompanyJoin = claimTempRoot.join(ClaimTemplate_.company);

		Predicate restriction = cb.conjunction();
		if (StringUtils.isNotBlank(conditionDTO.getTemplateName())) {

			restriction = cb.and(restriction, cb.like(cb.upper(claimTempRoot.get(ClaimTemplate_.templateName)),
					conditionDTO.getTemplateName().toUpperCase()));
		}

		if (StringUtils.isNotBlank(conditionDTO.getStatus())) {
			if ("visible".equals(conditionDTO.getStatus())) {
				restriction = cb.and(restriction, cb.equal((claimTempRoot.get(ClaimTemplate_.visibility)), true));
			}
			if ("hidden".equals(conditionDTO.getStatus())) {
				restriction = cb.and(restriction, cb.equal((claimTempRoot.get(ClaimTemplate_.visibility)), false));
			}
		}

		restriction = cb.and(restriction, cb.equal(claimTempRootCompanyJoin.get(Company_.companyId), companyId));

		criteriaQuery.where(restriction);

		if (sortDTO != null) {

			Path<String> sortPath = getSortPathForClaimTemplate(sortDTO, claimTempRoot);
			if (sortPath != null) {
				if (SortConstants.DB_ORDER_BY_ASC.equalsIgnoreCase(sortDTO.getOrderType())) {
					criteriaQuery.orderBy(cb.asc(sortPath));
				}
				if (SortConstants.DB_ORDER_BY_DESC.equalsIgnoreCase(sortDTO.getOrderType())) {
					criteriaQuery.orderBy(cb.desc(sortPath));
				}
			}

		}

		TypedQuery<ClaimTemplate> claimTempTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			claimTempTypedQuery.setFirstResult(getStartPosition(pageDTO));
			claimTempTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		List<ClaimTemplate> claimTemplateList = claimTempTypedQuery.getResultList();

		return claimTemplateList;
	}

	@Override
	public Path<String> getSortPathForClaimTemplate(SortCondition sortDTO, Root<ClaimTemplate> claimTempRoot) {

		List<String> claimTempIsColList = new ArrayList<String>();
		claimTempIsColList.add(SortConstants.CLAIM_TEMPLATE_NAME);
		claimTempIsColList.add(SortConstants.CLAIM_TEMPLATE_VISIBILITY);
		claimTempIsColList.add(SortConstants.CLAIM_TEMPLATE_NUM_OF_ITEMS);

		Path<String> sortPath = null;

		if (claimTempIsColList.contains(sortDTO.getColumnName())) {
			sortPath = claimTempRoot.get(colMap.get(ClaimTemplate.class + sortDTO.getColumnName()));
		}
		return sortPath;
	}

	@Override
	public int getCountForAllClaimTemplate(Long companyId, ClaimTemplateConditionDTO conditionDTO) {
		return getAllClaimTemplateByConditionCompany(companyId, conditionDTO, null, null).size();
	}

	@Override
	public ClaimTemplate findById(Long claimTemplateId) {
		ClaimTemplate claimTemplate = super.findById(ClaimTemplate.class, claimTemplateId);
		return claimTemplate;
	}

	@Override
	public void update(ClaimTemplate claimTemplate) {
		super.update(claimTemplate);
	}

	@Override
	public void save(ClaimTemplate claimTemplate) {
		super.save(claimTemplate);

	}

	@Override
	public void delete(ClaimTemplate claimTemplate) {
		super.delete(claimTemplate);
	}
	
	/*
	 * 
	 * This Method is used to check duplicate Template Name is not there
	 * 
	 * */

	@Override
	public ClaimTemplate findByClaimTemplateAndCompany(Long claimTemplateId, String claimTemplateName, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ClaimTemplate> criteriaQuery = cb.createQuery(ClaimTemplate.class);
		Root<ClaimTemplate> claimTemplateRoot = criteriaQuery.from(ClaimTemplate.class);

		criteriaQuery.select(claimTemplateRoot);

		Join<ClaimTemplate, Company> claimTemplateRootJoin = claimTemplateRoot.join(ClaimTemplate_.company);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(claimTemplateRootJoin.get(Company_.companyId), companyId));

		if (claimTemplateId != null) {
			restriction = cb.and(restriction,
					cb.notEqual(claimTemplateRoot.get(ClaimTemplate_.claimTemplateId), claimTemplateId));
		}

		if (claimTemplateName != null && !claimTemplateName.isEmpty()) {
			restriction = cb.and(restriction, cb.equal(cb.upper(claimTemplateRoot.get(ClaimTemplate_.templateName)),
					claimTemplateName.toUpperCase()));
		}

		criteriaQuery.where(restriction);

		TypedQuery<ClaimTemplate> claimTemplateTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<ClaimTemplate> claimTemplateList = claimTemplateTypedQuery.getResultList();
		if (claimTemplateList != null && !claimTemplateList.isEmpty()) {
			return claimTemplateList.get(0);
		}
		return null;

	}

	@Override
	public List<ClaimTemplate> getClaimTemplateList(Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ClaimTemplate> criteriaQuery = cb.createQuery(ClaimTemplate.class);
		Root<ClaimTemplate> claimTempRoot = criteriaQuery.from(ClaimTemplate.class);
		criteriaQuery.select(claimTempRoot);

		Join<ClaimTemplate, Company> claimTempRootCompanyJoin = claimTempRoot.join(ClaimTemplate_.company);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(claimTempRootCompanyJoin.get(Company_.companyId), companyId));

		criteriaQuery.where(restriction);

		TypedQuery<ClaimTemplate> claimTempTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<ClaimTemplate> claimTemplateList = claimTempTypedQuery.getResultList();

		return claimTemplateList;
	}

	@Override
	public List<ClaimTemplate> getAssignedClaimTemplates(Long companyId, ClaimTemplateConditionDTO conditionDTO,
			PageRequest pageDTO, SortCondition sortDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ClaimTemplate> criteriaQuery = cb.createQuery(ClaimTemplate.class);
		Root<ClaimTemplate> claimTempRoot = criteriaQuery.from(ClaimTemplate.class);
		criteriaQuery.select(claimTempRoot);

		Join<ClaimTemplate, Company> claimTempRootCompanyJoin = claimTempRoot.join(ClaimTemplate_.company);

		Join<ClaimTemplate, ClaimTemplateItem> claimTemplateItemJoin = claimTempRoot
				.join(ClaimTemplate_.claimTemplateItems);

		Join<ClaimTemplateItem, ClaimItemMaster> claimItemJoin = claimTemplateItemJoin
				.join(ClaimTemplateItem_.claimItemMaster);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal((claimTempRoot.get(ClaimTemplate_.visibility)), true));

		restriction = cb.and(restriction, cb.equal(claimTempRootCompanyJoin.get(Company_.companyId), companyId));

		restriction = cb.and(restriction,
				cb.equal(claimItemJoin.get(ClaimItemMaster_.claimItemId), conditionDTO.getClaimItemId()));

		criteriaQuery.where(restriction);

		if (sortDTO != null) {

			Path<String> sortPath = getSortPathForClaimTemplate(sortDTO, claimTempRoot);
			if (sortPath != null) {
				if (SortConstants.DB_ORDER_BY_ASC.equalsIgnoreCase(sortDTO.getOrderType())) {
					criteriaQuery.orderBy(cb.asc(sortPath));
				}
				if (SortConstants.DB_ORDER_BY_DESC.equalsIgnoreCase(sortDTO.getOrderType())) {
					criteriaQuery.orderBy(cb.desc(sortPath));
				}
			}

		}

		TypedQuery<ClaimTemplate> claimTempTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			claimTempTypedQuery.setFirstResult(getStartPosition(pageDTO));
			claimTempTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		List<ClaimTemplate> claimTemplateList = claimTempTypedQuery.getResultList();

		return claimTemplateList;
	}

	@Override
	public List<ClaimTemplate> getAllClaimTemplateCompany(Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ClaimTemplate> criteriaQuery = cb.createQuery(ClaimTemplate.class);
		Root<ClaimTemplate> claimTempRoot = criteriaQuery.from(ClaimTemplate.class);
		criteriaQuery.select(claimTempRoot);

		Join<ClaimTemplate, Company> claimTempRootCompanyJoin = claimTempRoot.join(ClaimTemplate_.company);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal((claimTempRoot.get(ClaimTemplate_.visibility)), true));

		restriction = cb.and(restriction, cb.equal(claimTempRootCompanyJoin.get(Company_.companyId), companyId));

		criteriaQuery.where(restriction);

		TypedQuery<ClaimTemplate> claimTempTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<ClaimTemplate> claimTemplateList = claimTempTypedQuery.getResultList();

		return claimTemplateList;
	}

	@Override
	public List<ClaimCategoryMaster> getAllClaimCategoryCompany(Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ClaimCategoryMaster> criteriaQuery = cb.createQuery(ClaimCategoryMaster.class);
		Root<ClaimCategoryMaster> claimCategRoot = criteriaQuery.from(ClaimCategoryMaster.class);
		criteriaQuery.select(claimCategRoot);

		Join<ClaimCategoryMaster, Company> claimCategRootCompanyJoin = claimCategRoot
				.join(ClaimCategoryMaster_.company);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(claimCategRootCompanyJoin.get(Company_.companyId), companyId));

		criteriaQuery.where(restriction);

		TypedQuery<ClaimCategoryMaster> claimTempTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<ClaimCategoryMaster> claimTemplateList = claimTempTypedQuery.getResultList();

		return claimTemplateList;
	}

	public List<ClaimTemplate> getClaimCategoryCompany(Long companyId, Long claimTemplateId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ClaimTemplate> criteriaQuery = cb.createQuery(ClaimTemplate.class);
		Root<ClaimTemplate> claimCategRoot = criteriaQuery.from(ClaimTemplate.class);
		criteriaQuery.select(claimCategRoot);
		Join<ClaimTemplate, Company> claimTemplateCompanyJoin = claimCategRoot.join(ClaimTemplate_.company);
		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(claimTemplateCompanyJoin.get(Company_.companyId), companyId));
		if (claimTemplateId != null) {
			restriction = cb.and(restriction,
					cb.notEqual(claimCategRoot.get(ClaimTemplate_.claimTemplateId), claimTemplateId));
		}
		criteriaQuery.where(restriction);
		TypedQuery<ClaimTemplate> claimTempTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<ClaimTemplate> claimTemplateList = claimTempTypedQuery.getResultList();

		return claimTemplateList;
	}

	@Override
	public List<ClaimTemplate> getAllClaimTemplateWithTemplateCapping(Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ClaimTemplate> criteriaQuery = cb.createQuery(ClaimTemplate.class);
		Root<ClaimTemplate> claimTempRoot = criteriaQuery.from(ClaimTemplate.class);
		criteriaQuery.select(claimTempRoot);

		Join<ClaimTemplate, Company> claimTempRootCompanyJoin = claimTempRoot.join(ClaimTemplate_.company);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal((claimTempRoot.get(ClaimTemplate_.visibility)), true));

		restriction = cb.and(restriction,
				cb.or(cb.notEqual(claimTempRoot.get(ClaimTemplate_.entitlementPerClaim), 0),
						cb.notEqual(claimTempRoot.get(ClaimTemplate_.entitlementPerYear), 0),
						cb.notEqual(claimTempRoot.get(ClaimTemplate_.entitlementPerMonth), 0)));

		restriction = cb.and(restriction, cb.equal(claimTempRootCompanyJoin.get(Company_.companyId), companyId));

		criteriaQuery.where(restriction);

		TypedQuery<ClaimTemplate> claimTempTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<ClaimTemplate> claimTemplateList = claimTempTypedQuery.getResultList();

		return claimTemplateList;
	}

	@Override
	public List<ClaimTemplate> getAllClaimTemplateWithClaimItemCapping(Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ClaimTemplate> criteriaQuery = cb.createQuery(ClaimTemplate.class);
		Root<ClaimTemplate> claimTempRoot = criteriaQuery.from(ClaimTemplate.class);
		criteriaQuery.select(claimTempRoot).distinct(true);

		Join<ClaimTemplate, Company> claimTempRootCompanyJoin = claimTempRoot.join(ClaimTemplate_.company);
		Join<ClaimTemplate, ClaimTemplateItem> claimTempItemJoin = claimTempRoot
				.join(ClaimTemplate_.claimTemplateItems);
		Join<ClaimTemplateItem, ClaimTemplateItemGeneral> claimTempItemGeneralJoin = claimTempItemJoin
				.join(ClaimTemplateItem_.claimTemplateItemGenerals);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal((claimTempRoot.get(ClaimTemplate_.visibility)), true));

		restriction = cb.and(restriction,
				cb.or(cb.notEqual(claimTempItemGeneralJoin.get(ClaimTemplateItemGeneral_.entitlementPerDay), 0),
						cb.notEqual(claimTempItemGeneralJoin.get(ClaimTemplateItemGeneral_.entitlementPerYear), 0),
						cb.notEqual(claimTempItemGeneralJoin.get(ClaimTemplateItemGeneral_.entitlementPerMonth), 0)));

		restriction = cb.and(restriction, cb.equal(claimTempRootCompanyJoin.get(Company_.companyId), companyId));

		criteriaQuery.where(restriction);

		TypedQuery<ClaimTemplate> claimTempTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<ClaimTemplate> claimTemplateList = claimTempTypedQuery.getResultList();

		return claimTemplateList;
	}

	@Override
	public ClaimTemplate saveReturn(ClaimTemplate claimTemplate) {

		ClaimTemplate persistObj = claimTemplate;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (ClaimTemplate) getBaseEntity();
			beanUtil.copyProperties(persistObj, claimTemplate);
		} catch (IllegalAccessException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		} catch (InvocationTargetException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		}
		this.entityManagerFactory.persist(persistObj);
		return persistObj;
	}

	@Override
	public RollBackProcDTO adjustClaimResignedEmp(final long employeeId, final long userId) {

		final RollBackProcDTO rollBackProcDTO = new RollBackProcDTO();

		getSession().doWork(new Work() {

			@Override
			public void execute(Connection connection) throws SQLException {
				try {
					CallableStatement cstmt = null;
					cstmt = connection.prepareCall("{call Rollback_Resigned_Employee_Claim (?,?,?,?,?,?)}");
					cstmt.setLong("@Employee_ID", employeeId);
					cstmt.setBoolean("@Is_Resignation", true);
					cstmt.setLong("@User_ID", userId);
					cstmt.setTimestamp("@Current_Date", DateUtils.getCurrentTimestamp());
					cstmt.registerOutParameter("@Status", java.sql.Types.BOOLEAN);
					cstmt.registerOutParameter("@Err_Msg", java.sql.Types.VARCHAR);
					cstmt.execute();
					rollBackProcDTO.setStatus(cstmt.getBoolean("@Status"));
					rollBackProcDTO.setErrorMsg(cstmt.getString("@Err_Msg"));

				} catch (SQLException e) {
					throw new PayAsiaSystemException(e.getMessage(), e);
				}

			}
		});

		return rollBackProcDTO;
	}

	@Override
	public List<EmployeeClaimSummaryDTO> getEmployeeClaimBalanceSummaryProc(final AddClaimDTO claimDTO) {
		final List<EmployeeClaimSummaryDTO> employeeClaimSummaryDTOList = new ArrayList<>();

		getSession().doWork(new Work() {
			@Override
			public void execute(Connection connection) throws SQLException {
				try {
					CallableStatement cstmt = null;
					cstmt = connection.prepareCall("{call Employee_Claim_Entitlement_Summary(?,?,?,?,?)}");

					cstmt.setLong("@Company_ID", claimDTO.getCompanyId());
					cstmt.setNull("@Employee_Number", java.sql.Types.VARCHAR);
					cstmt.setLong("@Employee_ID", claimDTO.getEmployeeId());
					cstmt.setInt("@Year", claimDTO.getYear());
					cstmt.setNull("@Claim_Template_ID", java.sql.Types.BIGINT);
					ResultSet rs = cstmt.executeQuery();
					if (rs != null) {
						while (rs.next()) {
							EmployeeClaimSummaryDTO employeeClaimSummaryDTO = new EmployeeClaimSummaryDTO();

							employeeClaimSummaryDTO.setAdjustments(String.valueOf(rs.getInt("Claim_Adjustment_Count")));
							employeeClaimSummaryDTO
									.setAdjustmentsCount(String.valueOf(rs.getInt("Claim_Adjustment_Count")));
							employeeClaimSummaryDTO
									.setEmployeeClaimTemplateId(rs.getLong("Employee_Claim_Template_ID"));
							employeeClaimSummaryDTO.setClaimTemplateName(rs.getString("Claim_Template_Name"));
							employeeClaimSummaryDTO.setEntitlement(rs.getString("Entitlement"));
							
							employeeClaimSummaryDTO.setClaimedAmount(rs.getBigDecimal("Claimed").setScale(2, BigDecimal.ROUND_HALF_UP));
							employeeClaimSummaryDTO.setBalance(rs.getBigDecimal("Entitlement_Balance").setScale(2, BigDecimal.ROUND_HALF_UP));
							
							employeeClaimSummaryDTOList.add(employeeClaimSummaryDTO);
						}
					}
				} catch (SQLException e) {
					throw new PayAsiaSystemException(e.getMessage(), e);
				}

			}
		});

		return employeeClaimSummaryDTOList;
	}

	@Override
	public List<Tuple> getClaimTemplateNameTupleList(Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Tuple> criteriaQuery = cb.createQuery(Tuple.class);
		Root<ClaimTemplate> claimTemplateRoot = criteriaQuery.from(ClaimTemplate.class);

		Join<ClaimTemplate, Company> compJoin = claimTemplateRoot.join(ClaimTemplate_.company);

		List<Selection<?>> selectionItems = new ArrayList<Selection<?>>();

		selectionItems
				.add(claimTemplateRoot.get(ClaimTemplate_.templateName).alias(getAlias(ClaimTemplate_.templateName)));
		criteriaQuery.multiselect(selectionItems).distinct(true);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(compJoin.get(Company_.companyId), companyId));

		criteriaQuery.where(restriction);

		TypedQuery<Tuple> companyTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<Tuple> companyGroupList = companyTypedQuery.getResultList();
		return companyGroupList;
	}

	@Override
	public ClaimTemplate findByName(String templateName, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ClaimTemplate> criteriaQuery = cb.createQuery(ClaimTemplate.class);
		Root<ClaimTemplate> claimTemplateRoot = criteriaQuery.from(ClaimTemplate.class);

		criteriaQuery.select(claimTemplateRoot);

		Join<ClaimTemplate, Company> claimTemplateRootJoin = claimTemplateRoot.join(ClaimTemplate_.company);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(claimTemplateRootJoin.get(Company_.companyId), companyId));

		restriction = cb.and(restriction,
				cb.equal(cb.upper(claimTemplateRoot.get(ClaimTemplate_.templateName)), templateName.toUpperCase()));

		criteriaQuery.where(restriction);

		TypedQuery<ClaimTemplate> claimTemplateTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<ClaimTemplate> claimTemplateList = claimTemplateTypedQuery.getResultList();
		if (claimTemplateList != null && !claimTemplateList.isEmpty()) {
			return claimTemplateList.get(0);
		}
		return null;

	}

	@Override
	public ClaimTemplate findByClaimTemplateID(Long claimTemplateId, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ClaimTemplate> criteriaQuery = cb.createQuery(ClaimTemplate.class);
		Root<ClaimTemplate> claimTemplateRoot = criteriaQuery.from(ClaimTemplate.class);

		criteriaQuery.select(claimTemplateRoot);

		Join<ClaimTemplate, Company> claimTemplateRootJoin = claimTemplateRoot.join(ClaimTemplate_.company);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(claimTemplateRootJoin.get(Company_.companyId), companyId));

		restriction = cb.and(restriction,
				cb.equal(claimTemplateRoot.get(ClaimTemplate_.claimTemplateId), claimTemplateId));

		criteriaQuery.where(restriction);

		TypedQuery<ClaimTemplate> claimTemplateTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<ClaimTemplate> claimTemplateList = claimTemplateTypedQuery.getResultList();
		if (claimTemplateList != null && !claimTemplateList.isEmpty()) {
			return claimTemplateList.get(0);
		}
		return null;
	}

}
