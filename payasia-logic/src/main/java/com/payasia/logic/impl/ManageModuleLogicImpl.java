package com.payasia.logic.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.payasia.common.dto.CompanyConditionDTO;
import com.payasia.common.dto.ManageModuleDTO;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.CompanyModuleMappingDAO;
import com.payasia.dao.ModuleMasterDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.CompanyModuleMapping;
import com.payasia.logic.ManageModuleLogic;

@Transactional
@Component
public class ManageModuleLogicImpl implements ManageModuleLogic {
	@Resource
	CompanyDAO companyDAO;

	@Resource
	ModuleMasterDAO moduleMasterDAO;

	@Resource
	CompanyModuleMappingDAO companyModuleMappingDAO;

	@Override
	public String updateModuleManageOfcompany(
			final ManageModuleDTO manageModuleDTO) {

		Set<Long> modulesToUpdate = new HashSet<Long>() {
			{
				if (manageModuleDTO.isHasClaimModule())
					add(2L);
				if (manageModuleDTO.isHasHrisModule())
					add(3L);
				if (manageModuleDTO.isHasLeaveModule())
					add(1L);
				if (manageModuleDTO.isHasLundinTimesheetModule())
					add(5L);
				if (manageModuleDTO.isHasLionTimesheetModule())
					add(6L);
				if (manageModuleDTO.isHasMobile())
					add(4L);
				// if (manageModuleDTO.isHasOTTimesheetModule())
				// add(5L);
				if (manageModuleDTO.isHasCoherentTimesheetModule())
					add(7L);

			}
		};

		Company company = companyDAO.findById(manageModuleDTO.getCompanyId());
		Set<CompanyModuleMapping> set = company.getCompanyModuleMappings();

		HashSet<Long> modulesToRemove = new HashSet<>();

		for (CompanyModuleMapping companyModuleMapping : set) {
			Long moduleId = companyModuleMapping.getModuleMaster()
					.getModuleId();
			if (modulesToUpdate.contains(moduleId))
				modulesToUpdate.remove(moduleId);
			else
				modulesToRemove.add(moduleId);

		}

		for (Long moduleId : modulesToRemove) {
			companyModuleMappingDAO.delete(company.getCompanyId(), moduleId);
		}

		for (Long modulesToUpdateId : modulesToUpdate) {
			set.add(new CompanyModuleMapping(company, moduleMasterDAO
					.findByID(modulesToUpdateId)));
		}
		Iterator<CompanyModuleMapping> itr = set.iterator();
		while (itr.hasNext()) {
			if (modulesToRemove.contains((itr.next().getModuleMaster()
					.getModuleId())))
				itr.remove();
		}

		return "success";
	}

	@Override
	public List<ManageModuleDTO> findCompanyWithGroupAndModule(
			CompanyConditionDTO companyConditionDTO) {

		List<ManageModuleDTO> list = companyDAO.findCompanyWithGroupAndModule(
				new CompanyConditionDTO(), null, null);
		return list;
	}

}
