package com.payasia.dao.impl;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

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
import com.payasia.common.dto.EmployeeEntitlementDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.EmployeeClaimAdjustmentDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeClaimAdjustment;
import com.payasia.dao.bean.EmployeeClaimAdjustment_;
import com.payasia.dao.bean.EmployeeClaimTemplate;
import com.payasia.dao.bean.EmployeeClaimTemplate_;
import com.payasia.dao.bean.Employee_;

@Repository
public class EmployeeClaimAdjustmentDAOImpl extends BaseDAO implements EmployeeClaimAdjustmentDAO {

	@Override
	protected Object getBaseEntity() {
		EmployeeClaimAdjustment employeeClaimAdjustment = new EmployeeClaimAdjustment();
		return employeeClaimAdjustment;
	}

	@Override
	public void update(EmployeeClaimAdjustment employeeClaimAdjustment) {
		super.update(employeeClaimAdjustment);

	}

	@Override
	public void save(EmployeeClaimAdjustment employeeClaimAdjustment) {
		super.save(employeeClaimAdjustment);
	}

	@Override
	public void delete(EmployeeClaimAdjustment employeeClaimAdjustment) {
		super.delete(employeeClaimAdjustment);

	}

	@Override
	public EmployeeClaimAdjustment findByID(long employeeClaimAdjustmentId) {
		return super.findById(EmployeeClaimAdjustment.class, employeeClaimAdjustmentId);
	}

	@Override
	public List<EmployeeClaimAdjustment> findByEmployeeClaimTemplateId(AddClaimDTO claimDTO) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeClaimAdjustment> criteriaQuery = cb.createQuery(EmployeeClaimAdjustment.class);
		Root<EmployeeClaimAdjustment> employeeClaimAdjustmentRoot = criteriaQuery.from(EmployeeClaimAdjustment.class);
		criteriaQuery.select(employeeClaimAdjustmentRoot);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(employeeClaimAdjustmentRoot.get(EmployeeClaimAdjustment_.employeeClaimTemplate)
						.get("employeeClaimTemplateId"), claimDTO.getEmployeeClaimTemplateId()));

		Join<EmployeeClaimAdjustment, Company> companyJoin = employeeClaimAdjustmentRoot
				.join(EmployeeClaimAdjustment_.company);

		if (!claimDTO.getAdmin()) {
			Join<EmployeeClaimAdjustment, EmployeeClaimTemplate> employeeClaimTemplateJoin = employeeClaimAdjustmentRoot
					.join(EmployeeClaimAdjustment_.employeeClaimTemplate);
			Join<EmployeeClaimTemplate, Employee> employeeJoin = employeeClaimTemplateJoin
					.join(EmployeeClaimTemplate_.employee);
			restriction = cb.and(restriction,
					cb.equal(employeeJoin.get(Employee_.employeeId), claimDTO.getEmployeeId()));
		}

		restriction = cb.and(restriction, cb.equal(companyJoin.get(Company_.companyId), claimDTO.getCompanyId()));

		criteriaQuery.where(restriction);

		TypedQuery<EmployeeClaimAdjustment> empTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		return empTypedQuery.getResultList();

	}

	@Override
	public List<EmployeeClaimAdjustment> findByEffectiveDate(Long companyId, Timestamp startDate, Timestamp endDate) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeClaimAdjustment> criteriaQuery = cb.createQuery(EmployeeClaimAdjustment.class);
		Root<EmployeeClaimAdjustment> employeeClaimAdjustmentRoot = criteriaQuery.from(EmployeeClaimAdjustment.class);
		criteriaQuery.select(employeeClaimAdjustmentRoot);

		Join<EmployeeClaimAdjustment, EmployeeClaimTemplate> empClaimTemplateJoin = employeeClaimAdjustmentRoot
				.join(EmployeeClaimAdjustment_.employeeClaimTemplate);
		Join<EmployeeClaimTemplate, Employee> empJoin = empClaimTemplateJoin.join(EmployeeClaimTemplate_.employee);
		Join<Employee, Company> empCompanyJoin = empJoin.join(Employee_.company);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(empCompanyJoin.get(Company_.companyId), companyId));

		if (startDate != null) {
			restriction = cb.and(restriction, cb.greaterThanOrEqualTo(
					(employeeClaimAdjustmentRoot.get(EmployeeClaimAdjustment_.effectiveDate)), startDate));
		}
		if (endDate != null) {
			restriction = cb.and(restriction, cb.lessThanOrEqualTo(
					(employeeClaimAdjustmentRoot.get(EmployeeClaimAdjustment_.effectiveDate)), endDate));
		}

		criteriaQuery.where(restriction);

		TypedQuery<EmployeeClaimAdjustment> empTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		return empTypedQuery.getResultList();

	}

	@Override
	public Integer getCountForCondition(long employeeClaimTemplateId, Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Integer> criteriaQuery = cb.createQuery(Integer.class);
		Root<EmployeeClaimAdjustment> employeeClaimAdjustmentRoot = criteriaQuery.from(EmployeeClaimAdjustment.class);
		criteriaQuery.select(cb.count(employeeClaimAdjustmentRoot).as(Integer.class));

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction,
				cb.equal(employeeClaimAdjustmentRoot.get(EmployeeClaimAdjustment_.employeeClaimTemplate)
						.get("employeeClaimTemplateId").as(Long.class), employeeClaimTemplateId));

		criteriaQuery.where(restriction);

		TypedQuery<Integer> empTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		return empTypedQuery.getSingleResult();

	}

	@Override
	public List<EmployeeEntitlementDTO> callEmployeeClaimEntitlementSummary(final Long companyId,
			final String employeeNumber, final Long claimTemplateId, final int year) {
		final List<EmployeeEntitlementDTO> employeeEntitlementDTOs = new ArrayList<>();

		getSession().doWork(new Work() {

			@Override
			public void execute(Connection connection) throws SQLException {
				try {
					CallableStatement cstmt = null;
					cstmt = connection.prepareCall("{call Employee_Claim_Entitlement_Summary (?,?,?,?)}");

					cstmt.setLong("@Company_ID", companyId);
					if (StringUtils.isBlank(employeeNumber)) {
						cstmt.setNull("@Employee_Number", java.sql.Types.BIGINT);
					} else {
						cstmt.setString("@Employee_Number", employeeNumber);
					}

					if (claimTemplateId == -1) {
						cstmt.setNull("@Claim_Template_ID", java.sql.Types.BIGINT);
					} else {
						cstmt.setLong("@Claim_Template_ID", claimTemplateId);
					}

					cstmt.setInt("@Year", year);
					ResultSet rs = cstmt.executeQuery();
					if (rs != null) {
						int count = 1;
						while (rs.next()) {
							EmployeeEntitlementDTO employeeEntitlementDTO = new EmployeeEntitlementDTO();
							employeeEntitlementDTO.setEmployeeNumber(rs.getString("Employee_Number"));
							employeeEntitlementDTO.setAdjustments(String.valueOf(rs.getInt("Claim_Adjustment_Count")));
							
							employeeEntitlementDTO.setBalance(new BigDecimal(rs.getString("Entitlement_Balance")).setScale(2,BigDecimal.ROUND_HALF_UP));
	                        employeeEntitlementDTO.setEntitlement(new BigDecimal(rs.getString("Entitlement")).setScale(2,BigDecimal.ROUND_HALF_UP));
	                            
							employeeEntitlementDTO.setClaimTemplate(rs.getString("Claim_Template_Name"));
							employeeEntitlementDTO.setEmployeeClaimTemplateId(rs.getLong("Employee_Claim_Template_ID"));
							String lastName = StringUtils.isBlank(rs.getString("Last_Name")) ? ""
									: rs.getString("Last_Name");
							employeeEntitlementDTO.setFirstName(rs.getString("First_Name") + " " + lastName);
							employeeEntitlementDTOs.add(employeeEntitlementDTO);
						}
					}

				} catch (SQLException e) {
					throw new PayAsiaSystemException(e.getMessage(), e);
				}

			}
		});

		return employeeEntitlementDTOs;
	}

}
