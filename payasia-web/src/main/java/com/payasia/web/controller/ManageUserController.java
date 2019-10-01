package com.payasia.web.controller;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.payasia.common.form.UserRoleListForm;

 
/**
 * The Interface ManageUserController.
 *
 * @author vivekjain
 */
/**
 * The Interface ManageUserController.
 */
public interface ManageUserController {

	/**
	 * purpose : view All Roles.
	 * 
	 * @param columnName
	 *            the column name
	 * @param sortingType
	 *            the sorting type
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @return UserRoleForm contains All Existing roles
	 */
	String viewAllRole(String columnName, String sortingType,
			HttpServletRequest request, HttpServletResponse response);

	/**
	 * purpose : view All Privileges.
	 * 
	 * @param columnName
	 *            the column name
	 * @param sortingType
	 *            the sorting type
	 * @param roleId
	 *            the role id
	 * @return PrivilageResponseForm contains All Existing Privileges
	 */
	String viewPrivilage(String searchCondition, String searchText,
			String columnName, String sortingType, Long roleId);

	/**
	 * purpose : view All Privilege Users.
	 * 
	 * @param columnName
	 *            the column name
	 * @param sortingType
	 *            the sorting type
	 * @param roleId
	 *            the role id
	 * @param page
	 *            the page
	 * @param rows
	 *            the rows
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @return UserResponseForm contains All Users with assigned role and
	 *         privileges or without assigned role and privileges.
	 */
	String viewPrivilageUser(String searchCondition, String searchText,
			String columnName, String sortingType, Long roleId, int page,
			int rows, HttpServletRequest request, HttpServletResponse response);

	/**
	 * purpose : getCompany IsPayAsia.
	 * 
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @return Status.
	 */
	String getCompanyIsPayAsia(HttpServletRequest request,
			HttpServletResponse response);

	/**
	 * purpose : save Role.
	 * 
	 * @param userRoleListForm
	 *            the user role list form
	 * @param request
	 *            the request
	 * @param locale
	 *            the locale
	 * @return response.
	 */
	String saveRole(UserRoleListForm userRoleListForm,
			HttpServletRequest request, Locale locale);

	/**
	 * purpose : Copy the Role from existing role with defined privileges.
	 * 
	 * @param userRoleListForm
	 *            the user role list form
	 * @param request
	 *            the request
	 * @param locale
	 *            the locale
	 * @return response.
	 */
	String copyRole(UserRoleListForm userRoleListForm,
			HttpServletRequest request, Locale locale);

	/**
	 * purpose : Delete Role.
	 * 
	 * @param roleId
	 *            the role id
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @param locale
	 *            the locale
	 * @return response.
	 */
	String deleteRole(Long roleId, HttpServletRequest request,
			HttpServletResponse response, Locale locale);

	/**
	 * purpose : get AssignedCompanyList to user.
	 * 
	 * @param roleId
	 *            the role id
	 * @param employeeId
	 *            the employee id
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @param locale
	 *            the locale
	 * @return ManageUserAddCompanyResponseForm contains companies list.
	 */
	String getAssignedCompanyList(Long roleId, Long employeeId,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale);

	/**
	 * purpose : get Employee FilterList for Edit.
	 * 
	 * @param employeeId
	 *            the employee id
	 * @param roleId
	 *            the role id
	 * @param companyId
	 *            the company id
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @param locale
	 *            the locale
	 * @return EmployeeFilterListForm contains Employee FilterList .
	 */
	String getEditEmployeeFilterList(Long employeeId, Long roleId,
			Long companyId, HttpServletRequest request,
			HttpServletResponse response, Locale locale);

	/**
	 * purpose : Delete Filter for Employee.
	 * 
	 * @param filterId
	 *            the filter id
	 */
	void deleteFilter(Long filterId);

	/**
	 * purpose : save Employee FilterList.
	 * 
	 * @param metaData
	 *            the meta data
	 * @param employeeId
	 *            the employee id
	 * @param roleId
	 *            the role id
	 * @param companyId
	 *            the company id
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @param locale
	 *            the locale
	 * @return EmployeeFilterListForm contains Employee FilterList .
	 */
	String saveEmployeeFilterList(String metaData, Long employeeId,
			Long roleId, Long companyId, HttpServletRequest request,
			HttpServletResponse response, Locale locale);

	/**
	 * purpose : get Employee FilterList.
	 * 
	 * @param companyId
	 *            the company id
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @param locale
	 *            the locale
	 * @return ManageUserAddCompanyResponseForm contains companies list.
	 */
	String getEmployeeFilterList(Long companyId, HttpServletRequest request,
			HttpServletResponse response, Locale locale);

	String isPayAsiaUserAdmin(HttpServletRequest request,
			HttpServletResponse response);

	String getAdvanceFilterComboHashmap(Long companyId,
			HttpServletRequest request, HttpServletResponse response);

	/**
	 * purpose : save User RoleAndPrivileges.
	 * 
	 * @param roleId
	 *            the role id
	 * @param privilageId
	 *            the privilage id
	 * @param userIdAndCompanyName
	 *            the user id and company name
	 * @param notSelectedUserIds
	 *            the not selected user ids
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @param locale
	 *            the locale
	 * @return response.
	 */
	String saveUserRoleAndPrivilage(String roleId, String[] privilageId,
			String[] sectionIds, String userIdAndCompanyName,
			String notSelectedUserIds, HttpServletRequest request,
			HttpServletResponse response, Locale locale);

	String saveRolePrivileges(String roleId, String[] privilageIds,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale);

	String saveEmployeeRoleWithAssignCompany(String roleId, Long userId,
			String[] companyIds, HttpServletRequest request,
			HttpServletResponse response, Locale locale);

	String saveCompanySection(Long roleId, Long userId, Long companyId,
			String[] sectionIds, HttpServletRequest request,
			HttpServletResponse response, Locale locale);

	String getAssignCompanySection(Long roleId, Long userId, Long companyId);

	String isSeletedCompanyAssignToUser(Long roleId, Long userId,
			Long companyId, HttpServletRequest request,
			HttpServletResponse response, Locale locale);

	String saveRoleSections(String roleId, String[] sectionIds,
			boolean overrideSection, HttpServletRequest request,
			HttpServletResponse response, Locale locale);

	/**
	 * purpose : get CompanyList.
	 * 
	 * @param columnName
	 *            the column name
	 * @param sortingType
	 *            the sorting type
	 * @param isCompanyPayasia
	 *            the is company payasia
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @return ManageUserAddCompanyResponseForm contains companies list.
	 */
	String getCompanyList(String columnName, String sortingType,
			Boolean isCompanyPayasia, Long roleId, Long userId,
			HttpServletRequest request, HttpServletResponse response);

	String viewSectionName(String searchCondition, String searchText,
			String columnName, String sortingType, Long roleId,
			HttpServletRequest request);

	String viewSectionNameByCompany(String searchCondition, String searchText,
			String columnName, String sortingType, Long roleId, Long companyId,
			HttpServletRequest request);

	String saveEmployeeRoleWithDefaultCompany(String roleId, String[] userIds,
			String[] allUserIds, HttpServletRequest request,
			HttpServletResponse response, Locale locale);

}
