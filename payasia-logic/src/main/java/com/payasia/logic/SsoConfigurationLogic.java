package com.payasia.logic;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.payasia.common.dto.SsoConfigurationDTO;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeLoginDetail;

@Transactional
public interface SsoConfigurationLogic {

	public SsoConfigurationDTO getSsoConfigurationForComapny(Long companyId);

	public void saveSsoConfiguration(SsoConfigurationDTO ssoConfigurationDTO);

	SsoConfigurationDTO getSsoConfigByCompCodeWithGroup(String companyCode);

	List<Employee> getUserByEmail(String email, String companyCode);

	EmployeeLoginDetail getUserLoginByEmployeeID(long employeeID);
}
