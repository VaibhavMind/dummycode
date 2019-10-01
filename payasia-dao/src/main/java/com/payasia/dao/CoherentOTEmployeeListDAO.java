package com.payasia.dao;

import java.util.List;

import com.payasia.dao.bean.CoherentOTEmployeeList;

public interface CoherentOTEmployeeListDAO {

	List<CoherentOTEmployeeList> findAll();

	void save(CoherentOTEmployeeList coherentOTEmployeeList);

	CoherentOTEmployeeList findById(Long employeeId);

	void delete(CoherentOTEmployeeList coherentOTEmployeeList);

	List<CoherentOTEmployeeList> findByCondition(String searchCondition,
			String searchText, Long companyId);

	void deleteCoherentOTEmployee(long parseLong);

}
