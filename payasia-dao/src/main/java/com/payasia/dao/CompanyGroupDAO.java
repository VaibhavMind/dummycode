package com.payasia.dao;

import java.util.List;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.CompanyGroup;

/**
 * The Interface CompanyGroupDAO.
 */
/**
 * @author vivekjain
 * 
 */
public interface CompanyGroupDAO {

	/**
	 * Find all CompanyGroup Objects List.
	 * 
	 * @return the list
	 */
	List<CompanyGroup> findAll();

	/**
	 * Find CompanyGroup Object by groupId.
	 * 
	 * @param groupId
	 *            the group id
	 * @return the company group
	 */
	CompanyGroup findById(long groupId);

	/**
	 * Find CompanyGroup Object by groupCode.
	 * 
	 * @param groupCode
	 *            the group code
	 * @return the company group
	 */
	CompanyGroup findByCode(String groupCode);

	/**
	 * Gets CompanyGroup Object List.
	 * 
	 * @param pageDTO
	 *            the page dto
	 * @param sortDTO
	 *            the sort dto
	 * @return the company groups
	 */
	List<CompanyGroup> getCompanyGroups(PageRequest pageDTO,
			SortCondition sortDTO);

	/**
	 * Gets the sort path for company category.
	 * 
	 * @param sortDTO
	 *            the sort dto
	 * @param companyGroupRoot
	 *            the company group root
	 * @return the sort path for company category
	 */
	Path<String> getSortPathForCompanyCategory(SortCondition sortDTO,
			Root<CompanyGroup> companyGroupRoot);

	/**
	 * Save CompanyGroup Object.
	 * 
	 * @param companyGroup
	 *            the company group
	 */
	void save(CompanyGroup companyGroup);

	/**
	 * Update CompanyGroup Object.
	 * 
	 * @param companyGroup
	 *            the company group
	 */
	void update(CompanyGroup companyGroup);

	/**
	 * Delete CompanyGroup Object.
	 * 
	 * @param companyGroup
	 *            the company group
	 */
	void delete(CompanyGroup companyGroup);

	/**
	 * Find CompanyGroup Object by group code and group id.
	 * 
	 * @param groupCode
	 *            the group code
	 * @param groupId
	 *            the group id
	 * @return the company group
	 */
	CompanyGroup findByGroupCodeGroupId(String groupCode, Long groupId);

	List<CompanyGroup> findAllYEP();

	CompanyGroup findByCompanyId(Long companyId);

	CompanyGroup findByGroupName(String groupName);

	Company getEmployeeByCompany(Long employeeId, Long companyId);
}
