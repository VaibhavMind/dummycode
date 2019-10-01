package com.payasia.dao.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.hibernate.jdbc.Work;
import org.springframework.stereotype.Repository;

import com.payasia.common.dto.AddClaimDTO;
import com.payasia.common.dto.ClaimReviewerReportDataDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.util.DateUtils;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.EmployeeClaimTemplateItemDAO;
import com.payasia.dao.bean.ClaimCategoryMaster;
import com.payasia.dao.bean.ClaimCategoryMaster_;
import com.payasia.dao.bean.ClaimItemMaster;
import com.payasia.dao.bean.ClaimItemMaster_;
import com.payasia.dao.bean.ClaimTemplateItem;
import com.payasia.dao.bean.ClaimTemplateItem_;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeClaimTemplate;
import com.payasia.dao.bean.EmployeeClaimTemplateItem;
import com.payasia.dao.bean.EmployeeClaimTemplateItem_;
import com.payasia.dao.bean.EmployeeClaimTemplate_;
import com.payasia.dao.bean.Employee_;

@Repository
public class EmployeeClaimTemplateItemDAOImpl extends BaseDAO implements EmployeeClaimTemplateItemDAO {

	@Override
	protected Object getBaseEntity() {
		EmployeeClaimTemplateItem employeeClaimTemplateItem = new EmployeeClaimTemplateItem();
		return employeeClaimTemplateItem;
	}

	@Override
	public void update(EmployeeClaimTemplateItem employeeClaimTemplateItem) {
		super.update(employeeClaimTemplateItem);

	}

	@Override
	public void save(EmployeeClaimTemplateItem employeeClaimTemplateItem) {
		super.save(employeeClaimTemplateItem);
	}

	@Override
	public void delete(EmployeeClaimTemplateItem employeeClaimTemplateItem) {
		super.delete(employeeClaimTemplateItem);

	}

	@Override
	public EmployeeClaimTemplateItem findByID(long employeeClaimTemplateItemId) {
		return super.findById(EmployeeClaimTemplateItem.class, employeeClaimTemplateItemId);
	}

	@Override
	public List<ClaimReviewerReportDataDTO> findClaimReviewerReportData(final Long companyId, final String employeeIds,
			final Boolean isAllEmployees) {
		final List<ClaimReviewerReportDataDTO> claimReviewerDataDTOs = new ArrayList<>();

		getSession().doWork(new Work() {

			@Override
			public void execute(Connection connection) throws SQLException {
				try {
					CallableStatement cstmt = null;
					cstmt = connection.prepareCall("{call Claim_Reviewer (?,?,?)}");

					cstmt.setLong("@Company_ID", companyId);
					cstmt.setString("@Employee_ID_List", employeeIds);
					cstmt.setBoolean("@All_Employees", isAllEmployees);

					ResultSet rs = cstmt.executeQuery();
					if (rs != null) {
						while (rs.next()) {
							ClaimReviewerReportDataDTO claimReviewerDataDTO = new ClaimReviewerReportDataDTO();
							claimReviewerDataDTO.setEmployeeNumber(rs.getString("Employee_Number"));
							claimReviewerDataDTO.setClaimTemplateName(rs.getString("Claim_Template_Name"));
							claimReviewerDataDTO.setReviewer1EmployeeNo(rs.getString("Reviewer1_Employee_Number"));
							claimReviewerDataDTO.setReviewer1FirstName(rs.getString("Reviewer1_First_Name"));
							claimReviewerDataDTO.setReviewer1LastName(rs.getString("Reviewer1_Last_Name"));
							claimReviewerDataDTO.setReviewer1Email(rs.getString("Reviewer1_Email"));
							claimReviewerDataDTO.setReviewer2EmployeeNo(rs.getString("Reviewer2_Employee_Number"));
							claimReviewerDataDTO.setReviewer2FirstName(rs.getString("Reviewer2_First_Name"));
							claimReviewerDataDTO.setReviewer2LastName(rs.getString("Reviewer2_Last_Name"));
							claimReviewerDataDTO.setReviewer2Email(rs.getString("Reviewer2_Email"));
							claimReviewerDataDTO.setReviewer3EmployeeNo(rs.getString("Reviewer3_Employee_Number"));
							claimReviewerDataDTO.setReviewer3FirstName(rs.getString("Reviewer3_First_Name"));
							claimReviewerDataDTO.setReviewer3LastName(rs.getString("Reviewer3_Last_Name"));
							claimReviewerDataDTO.setReviewer3Email(rs.getString("Reviewer3_Email"));
							claimReviewerDataDTO.setFirstName(rs.getString("First_Name"));
							claimReviewerDataDTO.setLastName(rs.getString("Last_Name"));

							claimReviewerDataDTOs.add(claimReviewerDataDTO);
						}
					}
				} catch (SQLException e) {
					throw new PayAsiaSystemException(e.getMessage(), e);
				}

			}
		});

		return claimReviewerDataDTOs;
	}

	@Override
	public List<EmployeeClaimTemplateItem> findByEmployeeClaimTemplateId(AddClaimDTO addClaimDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeClaimTemplateItem> criteriaQuery = cb
				.createQuery(EmployeeClaimTemplateItem.class);
		Root<EmployeeClaimTemplateItem> employeeClaimTemplateRoot = criteriaQuery
				.from(EmployeeClaimTemplateItem.class);
		criteriaQuery.select(employeeClaimTemplateRoot);
		Join<EmployeeClaimTemplateItem, EmployeeClaimTemplate> employeeClaimTemplateJoin = employeeClaimTemplateRoot
				.join(EmployeeClaimTemplateItem_.employeeClaimTemplate);

		Join<EmployeeClaimTemplateItem, ClaimTemplateItem> claimTemplateItemJoin = employeeClaimTemplateRoot
				.join(EmployeeClaimTemplateItem_.claimTemplateItem);
		Join<ClaimTemplateItem, ClaimItemMaster> claimItemMasterJoin = claimTemplateItemJoin
				.join(ClaimTemplateItem_.claimItemMaster);
		Join<ClaimItemMaster, ClaimCategoryMaster> claimCategoryMasterJoin = claimItemMasterJoin
				.join(ClaimItemMaster_.claimCategoryMaster);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(employeeClaimTemplateJoin
				.get(EmployeeClaimTemplate_.employeeClaimTemplateId),
				addClaimDTO.getEmployeeClaimTemplateId()));
		restriction = cb.and(restriction, cb.equal(employeeClaimTemplateRoot
				.get(EmployeeClaimTemplateItem_.active), true));
		if(!addClaimDTO.getAdmin()){
		Join<EmployeeClaimTemplate, Employee> employeeJoin = employeeClaimTemplateJoin
				.join(EmployeeClaimTemplate_.employee);
		restriction = cb.and(restriction,
				cb.equal(employeeJoin.get(Employee_.employeeId), addClaimDTO.getEmployeeId()));
		}
		
		restriction = cb.and(restriction, cb.equal(
				claimTemplateItemJoin.get(ClaimTemplateItem_.companyId),
				addClaimDTO.getCompanyId()));
		criteriaQuery.where(restriction);

		criteriaQuery.orderBy(cb.asc(claimCategoryMasterJoin
				.get(ClaimCategoryMaster_.claimCategoryName)), cb
				.asc(claimItemMasterJoin.get(ClaimItemMaster_.sortOrder)));

		TypedQuery<EmployeeClaimTemplateItem> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		List<EmployeeClaimTemplateItem> resultList = typedQuery.getResultList();
		return resultList!=null && !resultList.isEmpty()  ? resultList :null;

	}

	@Override
	public EmployeeClaimTemplateItem findItemByTemplateIdandItemName(long employeeClaimTemplateId, String claimItemName,
			String claimCategory) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeClaimTemplateItem> criteriaQuery = cb.createQuery(EmployeeClaimTemplateItem.class);
		Root<EmployeeClaimTemplateItem> employeeClaimTemplateRoot = criteriaQuery.from(EmployeeClaimTemplateItem.class);
		criteriaQuery.select(employeeClaimTemplateRoot);
		Join<EmployeeClaimTemplateItem, EmployeeClaimTemplate> employeeClaimTemplateJoin = employeeClaimTemplateRoot
				.join(EmployeeClaimTemplateItem_.employeeClaimTemplate);
		Join<EmployeeClaimTemplateItem, ClaimTemplateItem> claimTemplateItemJoin = employeeClaimTemplateRoot
				.join(EmployeeClaimTemplateItem_.claimTemplateItem);
		Join<ClaimTemplateItem, ClaimItemMaster> claimItemJoin = claimTemplateItemJoin
				.join(ClaimTemplateItem_.claimItemMaster);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(employeeClaimTemplateJoin.get(EmployeeClaimTemplate_.employeeClaimTemplateId),
						employeeClaimTemplateId));

		restriction = cb.and(restriction, cb.equal(claimItemJoin.get(ClaimItemMaster_.claimItemName), claimItemName));

		if (StringUtils.isNotBlank(claimCategory)) {
			Join<ClaimItemMaster, ClaimCategoryMaster> claimCategoryJoin = claimItemJoin
					.join(ClaimItemMaster_.claimCategoryMaster);
			restriction = cb.and(restriction,
					cb.equal(claimCategoryJoin.get(ClaimCategoryMaster_.claimCategoryName), claimCategory));
		}

		restriction = cb.and(restriction,
				cb.equal(employeeClaimTemplateRoot.get(EmployeeClaimTemplateItem_.active), true));

		criteriaQuery.where(restriction);

		TypedQuery<EmployeeClaimTemplateItem> typedQuery = entityManagerFactory.createQuery(criteriaQuery);
		if (typedQuery.getResultList().size() > 0) {
			EmployeeClaimTemplateItem employeeClaimTemplateItem = typedQuery.getResultList().get(0);
			return employeeClaimTemplateItem;
		}
		return null;

	}

	@Override
	public EmployeeClaimTemplateItem findByEmployeeIdAndClaimTemplateItemId(Long employeeId, Long claimTemplateItemId,Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeClaimTemplateItem> criteriaQuery = cb.createQuery(EmployeeClaimTemplateItem.class);
		Root<EmployeeClaimTemplateItem> employeeClaimTemplateItemRoot = criteriaQuery
				.from(EmployeeClaimTemplateItem.class);
		criteriaQuery.select(employeeClaimTemplateItemRoot);
		Join<EmployeeClaimTemplateItem, EmployeeClaimTemplate> employeeClaimTemplateJoin = employeeClaimTemplateItemRoot
				.join(EmployeeClaimTemplateItem_.employeeClaimTemplate);
		Join<EmployeeClaimTemplate, Employee> employeeJoin = employeeClaimTemplateJoin
				.join(EmployeeClaimTemplate_.employee);
		Join<EmployeeClaimTemplateItem, ClaimTemplateItem> claimJoin = employeeClaimTemplateItemRoot
				.join(EmployeeClaimTemplateItem_.claimTemplateItem);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(employeeJoin.get(Employee_.employeeId), employeeId));
		restriction = cb.and(restriction,
				cb.equal(claimJoin.get(ClaimTemplateItem_.claimTemplateItemId), claimTemplateItemId));
		restriction =cb.and(restriction,cb.equal(employeeClaimTemplateItemRoot.get(EmployeeClaimTemplateItem_.companyId), companyId));

		criteriaQuery.where(restriction);

		TypedQuery<EmployeeClaimTemplateItem> typedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<EmployeeClaimTemplateItem> employeeClaimTemplateItemList = typedQuery.getResultList();
		if (employeeClaimTemplateItemList != null && !employeeClaimTemplateItemList.isEmpty()) {
			return employeeClaimTemplateItemList.get(0);
		}
		return null;

	}

	@Override
	public List<EmployeeClaimTemplateItem> findByCompanyCondition(Long companyId, Date currentDate,
			long claimTemplateId) {

		String queryString = "select ECTI from EmployeeClaimTemplateItem ECTI inner join ECTI.employeeClaimTemplate ECT inner join ECT.claimTemplate CT where CT.company.companyId = :companyId AND CT.claimTemplateId = :claimTemplateId  AND   cast(:currentDate as date) between ECT.startDate and isnull(ECT.endDate,'2999-12-31') ";
		Query query = entityManagerFactory.createQuery(queryString);
		query.setParameter("companyId", companyId);
		query.setParameter("claimTemplateId", claimTemplateId);
		query.setParameter("currentDate", DateUtils.convertDateToTimeStamp(currentDate));
		List<EmployeeClaimTemplateItem> employeeClaimTemplateItems = query.getResultList();
		return employeeClaimTemplateItems;

	}

	@Override
	public EmployeeClaimTemplateItem findByEmployeeClaimTemplateItemID(AddClaimDTO addClaimDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeClaimTemplateItem> criteriaQuery = cb.createQuery(EmployeeClaimTemplateItem.class);
		Root<EmployeeClaimTemplateItem> employeeClaimTemplateItemRoot = criteriaQuery
				.from(EmployeeClaimTemplateItem.class);
		criteriaQuery.select(employeeClaimTemplateItemRoot);
		Join<EmployeeClaimTemplateItem, EmployeeClaimTemplate> employeeClaimTemplateJoin = employeeClaimTemplateItemRoot
				.join(EmployeeClaimTemplateItem_.employeeClaimTemplate);
		Join<EmployeeClaimTemplateItem, ClaimTemplateItem> claimJoin = employeeClaimTemplateItemRoot
				.join(EmployeeClaimTemplateItem_.claimTemplateItem);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(employeeClaimTemplateItemRoot.get(EmployeeClaimTemplateItem_.employeeClaimTemplateItemId),
						addClaimDTO.getEmployeeClaimTemplateItemId()));

		if (!addClaimDTO.getAdmin()) {
			
			Join<EmployeeClaimTemplate, Employee> employeeJoin = employeeClaimTemplateJoin
					.join(EmployeeClaimTemplate_.employee);
		
			restriction = cb.and(restriction,
					cb.equal(employeeJoin.get(Employee_.employeeId), addClaimDTO.getEmployeeId()));
		}
		restriction = cb.and(restriction,
				cb.equal(claimJoin.get(ClaimTemplateItem_.companyId), addClaimDTO.getCompanyId()));

		criteriaQuery.where(restriction);

		TypedQuery<EmployeeClaimTemplateItem> typedQuery = entityManagerFactory.createQuery(criteriaQuery);
		List<EmployeeClaimTemplateItem> list = typedQuery.getResultList();
		return list != null && !list.isEmpty() ? list.get(0) : null;
	}
	
	@Override
	public Integer findReceiptAmountPercentageForAmountBase(Long claimTemplateItemId) {
		StringBuilder sqlQuery = new StringBuilder("");
		sqlQuery.append(" SELECT NEW java.lang.Integer(ctict.receiptAmtPercentApplicable) FROM  ClaimTemplateItemClaimType ctict ");
		sqlQuery.append(" WHERE ctict.claimTemplateItem.claimTemplateItemId=:claimTemplateItemId");
		TypedQuery<Integer> query = entityManagerFactory.createQuery(
				sqlQuery.toString(), Integer.class);
		query.setParameter("claimTemplateItemId", claimTemplateItemId);
		return query.getResultList()!=null && !query.getResultList().isEmpty()? query.getResultList().get(0):null;
			
	}

}
