package com.payasia.logic;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.payasia.common.dto.CompanyConditionDTO;
import com.payasia.common.dto.ManageModuleDTO;

@Transactional
public interface ManageModuleLogic {

	String updateModuleManageOfcompany(ManageModuleDTO manageModuleDTO);

	List<ManageModuleDTO> findCompanyWithGroupAndModule(
			CompanyConditionDTO companyConditionDTO);
}
