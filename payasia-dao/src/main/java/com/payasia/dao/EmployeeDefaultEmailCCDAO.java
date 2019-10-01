package com.payasia.dao;

import com.payasia.dao.bean.EmployeeDefaultEmailCC;

public interface EmployeeDefaultEmailCCDAO {

	void delete(EmployeeDefaultEmailCC employeeDefaultEmailCC);

	void save(EmployeeDefaultEmailCC employeeDefaultEmailCC);

	void update(EmployeeDefaultEmailCC employeeDefaultEmailCC);

	EmployeeDefaultEmailCC findByCondition(Long companyId, Long employeeId,
			Long moduleId);

}
