package com.payasia.dao;

import java.util.List;

import com.payasia.common.dto.EmployeeMobileConditionDTO;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.EmployeeMobileDetails;

public interface EmployeeMobileDetailsDAO {

	void update(EmployeeMobileDetails employeeMobileDetails);

	void save(EmployeeMobileDetails employeeMobileDetails);

	void delete(EmployeeMobileDetails employeeMobileDetails);

	EmployeeMobileDetails findByID(long employeeMobileDetailsId);

	EmployeeMobileDetails saveReturn(EmployeeMobileDetails employeeMobileDetails);

	List<EmployeeMobileDetails> findByCondition(
			EmployeeMobileConditionDTO conditionDTO, PageRequest pageDTO,
			SortCondition sortDTO, Long companyId);

	Integer getCountForCondition(EmployeeMobileConditionDTO conditionDTO,
			Long companyId);

	EmployeeMobileDetails findByEmployeeActivationCode(
			Long employeeActivationCodeId);

	void updateByDeviceId(String deviceID, Long employeeActivationCodeId);

	EmployeeMobileDetails findByMobileId(String mobileId);

}
