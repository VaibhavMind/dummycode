/**
x * @author ragulapraveen
 *
 */
package com.payasia.dao;

import java.util.List;
import java.util.Map;

import javax.persistence.Tuple;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import com.payasia.common.dto.CompanyConditionDTO;
import com.payasia.common.dto.CompanyCopyDTO;
import com.payasia.common.dto.DataImportKeyValueDTO;
import com.payasia.common.dto.ManageModuleDTO;
import com.payasia.common.dto.PayAsiaCompanyStatisticReportDTO;
import com.payasia.common.dto.RolePrivilegeReportDTO;
import com.payasia.common.form.ExcelExportFiltersForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.CompanyGroup;

/**
 * The Interface CompanyDAO.
 */

public interface CompanyDAO {

	/**
	 * Find all company object list.
	 * 
	 * @param conditionDTO
	 *            the condition dto
	 * @return the list
	 */
	List<Company> findAll(CompanyConditionDTO conditionDTO);

	/**
	 * Find Company object by id.
	 * 
	 * @param companyId
	 *            the company id
	 * @return the company
	 */
	Company findById(long companyId);

	/**
	 * Find Company object by id.
	 * 
	 * @param companyId
	 *            the company id
	 * @return the company
	 */
	Company findByIdDetached(long companyId);

	/**
	 * Save Company object.
	 * 
	 * @param company
	 *            the company
	 * @return the company
	 */
	Company save(Company company);

	/**
	 * Update Company object.
	 * 
	 * @param company
	 *            the company
	 */
	void update(Company company);

	/**
	 * Find Company object by condition.
	 * 
	 * @param conditionDTO
	 *            the condition dto
	 * @return the company
	 */
	Company findByCondition(CompanyConditionDTO conditionDTO);

	/**
	 * Delete Company object.
	 * 
	 * @param company
	 *            the company
	 */
	void delete(Company company);

	/**
	 * Save return Company object.
	 * 
	 * @param company
	 *            the company
	 * @return the company
	 */
	Company saveReturn(Company company);

	/**
	 * New tran save return Company object.
	 * 
	 * @param company
	 *            the company
	 * @return the company
	 */
	Company newTranSaveReturn(Company company);

	/**
	 * New tran save Company object.
	 * 
	 * @param company
	 *            the company
	 */
	void newTranSave(Company company);

	/**
	 * New tran update Company object.
	 * 
	 * @param company
	 *            the company
	 */
	void newTranUpdate(Company company);

	/**
	 * New tran delete Company object.
	 * 
	 * @param company
	 *            the company
	 */
	void newTranDelete(Company company);

	/**
	 * Find Company object by condition .
	 * 
	 * @param colMap
	 *            the col map
	 * @param formIds
	 *            the form ids
	 * @param finalFilterList
	 *            the final filter list
	 * @param employeeId
	 *            the employee id
	 * @param tableRecordInfo
	 *            the table record info
	 * @return the list
	 */
	List<Object[]> findByCondition(Map<String, DataImportKeyValueDTO> colMap,
			List<Long> formIds, List<ExcelExportFiltersForm> finalFilterList,
			Long employeeId, Map<String, DataImportKeyValueDTO> tableRecordInfo);

	/**
	 * Copy company Company object.
	 * 
	 * @param companyCopyDTO
	 *            the company copy dto
	 * @return the boolean
	 */
	CompanyCopyDTO copyCompany(CompanyCopyDTO companyCopyDTO);

	/**
	 * Find Company object list by condition and employee id.
	 * 
	 * @param conditionDTO
	 *            the condition dto
	 * @param empId
	 *            the emp id
	 * @param pageDTO
	 *            the page dto
	 * @param sortDTO
	 *            the sort dto
	 * @return the list
	 */
	List<Company> findByConditionAndEmployeeId(
			CompanyConditionDTO conditionDTO, Long empId, PageRequest pageDTO,
			SortCondition sortDTO);

	/**
	 * Gets the count for condition and employee id.
	 * 
	 * @param conditionDTO
	 *            the condition dto
	 * @param empId
	 *            the emp id
	 * @return the count for condition and employee id
	 */
	int getCountForConditionAndEmployeeId(CompanyConditionDTO conditionDTO,
			Long empId);

	/**
	 * Find by company code.
	 * 
	 * @param companyCode
	 *            the company code
	 * @param companyId
	 *            the company id
	 * @return the company
	 */
	Company findByCompanyCode(String companyCode, Long companyId);

	/**
	 * Find all.
	 * 
	 * @param sortDTO
	 *            the sort dto
	 * @return the list
	 */
	List<Company> findAll(SortCondition sortDTO);

	/**
	 * Gets the sort path for all company.
	 * 
	 * @param sortDTO
	 *            the sort dto
	 * @param companyRoot
	 *            the company root
	 * @param companyGroupJoin
	 *            the company group join
	 * @return the sort path for all company
	 */
	Path<String> getSortPathForAllCompany(SortCondition sortDTO,
			Root<Company> companyRoot,
			Join<Company, CompanyGroup> companyGroupJoin);

	/**
	 * Gets the sort path for assign company.
	 * 
	 * @param sortDTO
	 *            the sort dto
	 * @param companyRoot
	 *            the company root
	 * @param companyGroupJoin
	 *            the company group join
	 * @return the sort path for assign company
	 */
	Path<String> getSortPathForAssignCompany(SortCondition sortDTO,
			Root<Company> companyRoot,
			Join<Company, CompanyGroup> companyGroupJoin);

	/**
	 * Find Company object list by group id.
	 * 
	 * @param sortDTO
	 *            the sort dto
	 * @param companyGroupId
	 *            the company group id
	 * @return the list
	 */
	List<Company> findByGroupId(SortCondition sortDTO, Long companyGroupId);

	/**
	 * Find Company object by company name.
	 * 
	 * @param companyName
	 *            the company name
	 * @return the company
	 */
	Company findByCompanyName(String companyName);

	/**
	 * Gets the count of companies.
	 * 
	 * @param conditionDTO
	 *            the condition dto
	 * @param empId
	 *            the emp id
	 * @return the count of companies
	 */
	Long getCountOfCompanies(CompanyConditionDTO conditionDTO, Long empId);

	/**
	 * Gets the count for switch company.
	 * 
	 * @param employeeId
	 *            the employee id
	 * @return the count for switch company
	 */
	Long getCountForSwitchCompany(Long employeeId);

	/**
	 * Find by condition company id.
	 * 
	 * @param colMap
	 *            the col map
	 * @param formIds
	 *            the form ids
	 * @param finalFilterList
	 *            the final filter list
	 * @param companyId
	 *            the company id
	 * @param tableRecordInfo
	 *            the table record info
	 * @return the list
	 */
	List<Object[]> findByConditionCompanyId(
			Map<String, DataImportKeyValueDTO> colMap, List<Long> formIds,
			List<ExcelExportFiltersForm> finalFilterList, Long companyId,
			Map<String, DataImportKeyValueDTO> tableRecordInfo);

	/**
	 * Find by condition group company id.
	 * 
	 * @param colMap
	 *            the col map
	 * @param formIds
	 *            the form ids
	 * @param finalFilterList
	 *            the final filter list
	 * @param companyId
	 *            the company id
	 * @param tableRecordInfo
	 *            the table record info
	 * @return the list
	 */
	List<Object[]> findByConditionGroupCompanyId(
			Map<String, DataImportKeyValueDTO> colMap, List<Long> formIds,
			List<ExcelExportFiltersForm> finalFilterList, Long companyId,
			Map<String, DataImportKeyValueDTO> tableRecordInfo);

	/**
	 * Find assign group company to user.
	 * 
	 * @param sortDTO
	 *            the sort dto
	 * @param pageDTO
	 *            the page dto
	 * @param employeeId
	 *            the employee id
	 * @param groupID
	 *            the group id
	 * @return the list
	 */
	List<Tuple> findAssignGroupCompanyToUser(SortCondition sortDTO,
			PageRequest pageDTO, Long employeeId, Long groupID);

	/**
	 * Gets the count for switch group company.
	 * 
	 * @param employeeId
	 *            the employee id
	 * @param groupId
	 *            the group id
	 * @return the count for switch group company
	 */
	Long getCountForSwitchGroupCompany(Long employeeId, Long groupId);

	/**
	 * Find all cmps.
	 * 
	 * @return the list
	 */
	List<Company> findAllCmps();

	/**
	 * Gets the distinct time zone ids.
	 * 
	 * @return the distinct time zone ids
	 */
	List<Long> getDistinctTimeZoneIds();

	/**
	 * Find company by time zone.
	 * 
	 * @param timeZoneId
	 *            the time zone id
	 * @return the list
	 */
	List<Company> findCompanyByTimeZone(Long timeZoneId);

	List<Long> getDistinctAssignedGroupCompanies(Long employeeId, Long groupId);

	List<Company> getCompanyListOtherThanPaysaiaGroup(SortCondition sortDTO,
			Long payasiaGroupId);

	/**
	 * Find assign company to user.
	 * 
	 * @param sortDTO
	 *            the sort dto
	 * @param pageDTO
	 *            the page dto
	 * @param employeeId
	 *            the employee id
	 * @return the tuple list
	 */
	List<Tuple> findAssignCompanyToUser(SortCondition sortDTO,
			PageRequest pageDTO, Long employeeId,
			CompanyConditionDTO conditionDTO);

	/**
	 * Gets the count for assign company.
	 * 
	 * @param empId
	 *            the emp id
	 * @return the count for assign company
	 */
	int getCountForAssignCompany(Long empId, CompanyConditionDTO conditionDTO);

	List<Object[]> createQueryForCompanyCustomField(
			Map<String, DataImportKeyValueDTO> colMap, Long companyId,
			List<Long> formIds, String dateFormat,
			Map<String, DataImportKeyValueDTO> tableRecordInfo,
			boolean showOnlyCompanyDynFieldCode);

	List<PayAsiaCompanyStatisticReportDTO> getPayAsiaCompanyStatisticReport(
			String asOnDate, String dateFormat, String companyIdList,
			boolean isIncludeInactiveCompany);

	/**
	 * @param companyId
	 */
	void deleteCompanyProc(Long companyId);

	List<Tuple> findAssignedCompanyToUserByCond(SortCondition sortDTO,
			PageRequest pageDTO, Long employeeId,
			CompanyConditionDTO conditionDTO);

	List<ManageModuleDTO> findCompanyWithGroupAndModule(
			CompanyConditionDTO conditionDTO, PageRequest pageDTO,
			SortCondition sortDTO);

	String getCompanyDefaultLanguage(Long companyCode);

	List<RolePrivilegeReportDTO> getEmployeeRolePrivilegeReport(
			String companyIdList, Long groupId);

	Company findCompanyByEmpId(long empId);

}
