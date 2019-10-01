package com.payasia.logic;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.springframework.transaction.annotation.Transactional;

import com.payasia.common.dto.AppCodeDTO;
import com.payasia.common.form.ClaimTemplateForm;
import com.payasia.common.form.ClaimTemplateItemForm;
import com.payasia.common.form.ClaimTemplateResponse;
import com.payasia.common.form.EmployeeFilterListForm;
import com.payasia.common.form.LeaveGranterFormResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.ClaimTemplateItem;
import com.payasia.dao.bean.ClaimTemplateItemShortlist;
import com.payasia.dao.bean.EmployeeClaimTemplateItem;

@Transactional
public interface ClaimTemplateLogic {

	ClaimTemplateResponse accessControl(Long companyId, String searchCondition,
			String searchText, PageRequest pageDTO, SortCondition sortDTO,
			Locale locale);

	String addClaimTemplate(Long companyId, ClaimTemplateForm claimTemplateForm);

	void deleteClaimTemplate(Long companyId, Long claimTemplateId);

	ClaimTemplateForm getClaimTemplate(Long companyId, Long claimTemplateId);

	String configureClaimTemplate(ClaimTemplateForm claimTemplateForm,
			Long companyId);

	Set<com.payasia.common.form.ClaimTemplateForm> getClaimTypeList(
			Long companyId, Long claimTemplateId, Long itemCategoryId);

	void addClaimType(String[] claimTypeId, Long claimTemplateId, Long companyId);

	ClaimTemplateResponse viewClaimType(Long claimTemplateId, Long companyId,
			PageRequest pageDTO, SortCondition sortDTO, Locale locale);

	ClaimTemplateForm getClaimTemplateItemWorkflow(Long companyId,
			Long claimTemplateItemId);

	String updateClaimTemplateItemWorkflow(ClaimTemplateForm claimTemplateForm,
			Long companyId);

	List<EmployeeFilterListForm> getClaimTemplateItemShortlist(
			Long claimTemplateItemId, Long companyId);

	List<AppCodeDTO> getClaimTypeItemList(Locale locale);

	String saveClaimTemplateItemConf(Long companyId,
			ClaimTemplateItemForm claimTemplateItemForm);

	ClaimTemplateForm getClaimTemplateIdConfData(Long companyId,
			Long claimTemplateItemId);

	String updateClaimTemplateItemConf(Long companyId,
			ClaimTemplateItemForm claimTemplateItemForm);

	ClaimTemplateForm getClaimItemCategories(Long companyId);

	List<ClaimTemplateForm> getAllowedNoOfTimesAppCodeList();

	List<AppCodeDTO> getCustomFieldTypes();

	String saveEmployeeFilterList(String metaData, Long claimTemplateItemId,
			Long companyId);

	void deleteFilter(Long filterId, Long companyId);

	List<ClaimTemplateForm> getProrationBasedOnAppCodeList();

	void setEmployeeClaimTemplateItem(
			List<ClaimTemplateItemShortlist> claimTemplateItemShortlists,
			Long companyId,
			List<Long> claimTemplateEmpIds,
			ClaimTemplateItem claimTemplateItem,
			Date currentDate,
			List<Long> formIds,
			HashMap<String, EmployeeClaimTemplateItem> employeeClaimTemplateItemMap);

	ClaimTemplateForm getClaimTemplateAppCodeList(Locale locale);

	List<ClaimTemplateForm> getAppcodeListForProration(Locale locale);

	LeaveGranterFormResponse getResignedEmployees(PageRequest pageDTO,
			SortCondition sortDTO, Long companyId, String fromDate,
			String toDate);

	Boolean adjustClaimResignedEmp(Long companyId, String employeeIds,
			Long userId);
	
	List<ClaimTemplateForm> getClaimTemplateList(Long companyId, Long claimTemplateId);

	void deleteClaimType(Long companyId, Long claimTemplateItemId);

	ClaimTemplateForm getClaimTypeForEdit(Long claimTemplateTypeId, Long companyId);

	void editClaimType(Long claimTemplateTypeId, ClaimTemplateForm claimTemplateForm, Long companyId);

}
