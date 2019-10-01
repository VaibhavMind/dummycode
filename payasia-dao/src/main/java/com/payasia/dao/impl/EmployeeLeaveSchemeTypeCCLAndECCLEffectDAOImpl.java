package com.payasia.dao.impl;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.payasia.dao.BaseDAO;
import com.payasia.dao.EmployeeLeaveSchemeTypeCCLAndECCLEffectDAO;

@Repository
public class EmployeeLeaveSchemeTypeCCLAndECCLEffectDAOImpl extends BaseDAO
		implements EmployeeLeaveSchemeTypeCCLAndECCLEffectDAO {
	
	/*@Override
	public void saveAndReturnEffectiveECCLAndCCL(EmployeeLeaveSchemeTypeCCLAndECCLEffect ecclEffect) {	
		try {
			this.entityManagerFactory.persist(ecclEffect);
		} catch (Exception e) {
			throw new PayAsiaSystemException("errors.dao", e);
		} 
	}

	@Override
	public void updateEffectiveECCLAndCCL(EmployeeLeaveSchemeTypeCCLAndECCLEffect ecclEffectObj) {
		super.update(ecclEffectObj);
	}*/
	
	@Override
	public void deleteByCondition(Long employeeLeaveSchemeTypeId,Long companyId) {

		String queryString = "DELETE FROM EmployeeLeaveSchemeTypeCCLAndECCLEffect e WHERE e.employeeLeaveSchemeType.employeeLeaveSchemeTypeId = :employeeLeaveSchemeTypeId and e.companyId =:companyId";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("employeeLeaveSchemeTypeId", employeeLeaveSchemeTypeId);
		q.setParameter("companyId", companyId);

		q.executeUpdate();

	}

	@Override
	protected Object getBaseEntity() {
		/*EmployeeLeaveSchemeTypeCCLAndECCLEffect employeeLeaveSchemeTypeCCLAndECCLEffect = new EmployeeLeaveSchemeTypeCCLAndECCLEffect();
		return employeeLeaveSchemeTypeCCLAndECCLEffect;*/
		return null;
	}
}
