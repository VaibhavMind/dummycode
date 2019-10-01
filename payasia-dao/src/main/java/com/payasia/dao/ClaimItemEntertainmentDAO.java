package com.payasia.dao;

import com.payasia.dao.bean.ClaimItemEntertainment;
import com.payasia.dao.bean.Employee;

public interface ClaimItemEntertainmentDAO {
	void update(ClaimItemEntertainment claimItemEntertainment);

	void save(ClaimItemEntertainment claimItemEntertainment);

	ClaimItemEntertainment findByCondition(Long employee, Long claimItemId,
			Long companyId);

	ClaimItemEntertainment findByCondition(
			String litemEntitlementClaimTemplateTypeId, Employee employee,
			Long companyId);

	void delete(long employeeId, Long valueOf);
}
