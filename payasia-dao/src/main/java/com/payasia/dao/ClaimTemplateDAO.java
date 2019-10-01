package com.payasia.dao;

import java.util.List;

import javax.persistence.Tuple;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import com.payasia.common.dto.AddClaimDTO;
import com.payasia.common.dto.ClaimTemplateConditionDTO;
import com.payasia.common.dto.EmployeeClaimSummaryDTO;
import com.payasia.common.dto.RollBackProcDTO;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.ClaimCategoryMaster;
import com.payasia.dao.bean.ClaimTemplate;

public interface ClaimTemplateDAO {

	List<ClaimTemplate> getAllClaimTemplateByConditionCompany(Long companyId, ClaimTemplateConditionDTO conditionDTO,
			PageRequest pageDTO, SortCondition sortDTO);

	int getCountForAllClaimTemplate(Long companyId, ClaimTemplateConditionDTO conditionDTO);

	ClaimTemplate findById(Long claimTemplateId);

	void update(ClaimTemplate claimTemplate);

	void save(ClaimTemplate claimTemplate);

	void delete(ClaimTemplate claimTemplate);

	Path<String> getSortPathForClaimTemplate(SortCondition sortDTO, Root<ClaimTemplate> claimTempRoot);

	ClaimTemplate findByClaimTemplateAndCompany(Long claimTemplateId, String claimTemplateName, Long companyId);

	List<ClaimTemplate> getClaimTemplateList(Long companyId);

	List<ClaimTemplate> getAssignedClaimTemplates(Long companyId, ClaimTemplateConditionDTO conditionDTO,
			PageRequest pageDTO, SortCondition sortDTO);

	List<ClaimTemplate> getAllClaimTemplateCompany(Long companyId);

	ClaimTemplate saveReturn(ClaimTemplate claimTemplate);

	List<ClaimTemplate> getAllClaimTemplateWithTemplateCapping(Long companyId);

	List<ClaimTemplate> getAllClaimTemplateWithClaimItemCapping(Long companyId);

	RollBackProcDTO adjustClaimResignedEmp(long employeeId, long userId);

	List<Tuple> getClaimTemplateNameTupleList(Long companyId);

	List<ClaimCategoryMaster> getAllClaimCategoryCompany(Long companyId);

	ClaimTemplate findByName(String templateName, Long companyId);

	List<ClaimTemplate> getClaimCategoryCompany(Long companyId, Long claimTemplateId);

	List<EmployeeClaimSummaryDTO> getEmployeeClaimBalanceSummaryProc(AddClaimDTO claimDTO);

	ClaimTemplate findByClaimTemplateID(Long claimTemplateId, Long companyId);

}
