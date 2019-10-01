package com.payasia.logic;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.payasia.common.form.DynamicFormSectionForm;
import com.payasia.common.form.DynamicFormSectionFormResponse;
import com.payasia.common.form.EmployeeFilterListForm;
import com.payasia.common.form.ManageUserAddCompanyForm;
import com.payasia.common.form.ManageUserAddCompanyResponseForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.PrivilageResponseForm;
import com.payasia.common.form.SortCondition;
import com.payasia.common.form.UserResponseForm;
import com.payasia.common.form.UserRoleForm;
import com.payasia.common.form.UserRoleListForm;

/**
 * @author vivekjain
 * 
 */
/**
 * The Interface ManageUserLogic.
 */
@Transactional
public interface ManageUserLogic {

	/**
	 * purpose : view All Roles.
	 * 
	 * @param searchText
	 * @param searchCondition
	 * 
	 * @param Long
	 *            the companyId
	 * @param SortCondition
	 *            the SortCondition
	 * @return UserRoleForm contains All Existing roles
	 */
	/**
	 * @param companyId
	 * @param sortDTO
	 * @return
	 */
	UserRoleForm viewAllRole(Long companyId, SortCondition sortDTO);

	/**
	 * purpose : view All Privileges.
	 * 
	 * @param Long
	 *            the roleId
	 * @param SortCondition
	 *            the SortCondition
	 * @return PrivilageResponseForm contains All Existing Privileges
	 */
	PrivilageResponseForm viewPrivilage(Long roleId, String searchCondition,
			String searchText, SortCondition sortDTO);

	/**
	 * purpose : view All Privilege Users.
	 * 
	 * @param companyId
	 *            the company id
	 * @param roleId
	 *            the role id
	 * @param sortDTO
	 *            the sort dto
	 * @param pageDTO
	 *            the page dto
	 * @return UserResponseForm contains All Users with assigned role and
	 *         privileges or not.
	 */
	UserResponseForm viewPrivilageUser(Long companyId, Long employeeId,
			Long roleId, SortCondition sortDTO, PageRequest pageDTO,
			String searchCondition, String searchText);

	/**
	 * purpose : Copy the Role from existing role with defined privileges.
	 * 
	 * @param UserRoleListForm
	 *            the UserRoleListForm
	 * @param Long
	 *            the companyId
	 * @return response.
	 */
	String copyRole(UserRoleListForm userRoleListForm, Long companyId);

	/**
	 * purpose : Delete Role.
	 * 
	 * @param Long
	 *            the roleId
	 * @param Long
	 *            the companyId
	 * @return response.
	 */
	String deleteRole(Long companyId, Long roleId);

	/**
	 * purpose : getCompany IsPayAsia.
	 * 
	 * @param Long
	 *            the companyId
	 * @return Status.
	 */
	String getCompanyIsPayAsia(Long companyId);

	/**
	 * purpose : save User RoleAndPrivilages.
	 * 
	 * @param companyId
	 *            the company id
	 * @param roleId
	 *            the role id
	 * @param privilegeId
	 *            the privilege id
	 * @param sectionIds
	 * @param userIdAndCompanyName
	 *            the user id and company name
	 * @param notSelectedUserIds
	 *            the not selected user ids
	 * @return response.
	 */
	String saveUserRoleAndPrivilage(Long companyId, String roleId,
			String[] privilegeId, String[] sectionIds,
			String userIdAndCompanyName, String notSelectedUserIds);

	/**
	 * purpose : get CompanyList.
	 * 
	 * @param userId
	 * @param roleId
	 * 
	 * @param SortCondition
	 *            the sortCondition
	 * @param Long
	 *            the companyId
	 * @param Long
	 *            employeeId
	 * @param Boolean
	 *            isCompanyPayasia
	 * @return ManageUserAddCompanyResponseForm contains companies list.
	 */
	ManageUserAddCompanyResponseForm getCompanyList(SortCondition sortDTO,
			Boolean isCompanyPayasia, Long companyId, String companyName,
			Long sessionEmployeeId, Long roleId, Long userId);

	/**
	 * purpose : save Role.
	 * 
	 * @param UserRoleListForm
	 *            the userRoleListForm
	 * @param Long
	 *            the companyId
	 * @return response.
	 */
	String saveRole(UserRoleListForm userRoleListForm, Long companyId);

	/**
	 * purpose : get AssignedCompanyList to user.
	 * 
	 * @param Long
	 *            the roleId
	 * @param Long
	 *            the companyId
	 * @param Long
	 *            employeeId
	 * @return ManageUserAddCompanyResponseForm contains companies list.
	 */
	List<ManageUserAddCompanyForm> getAssignedCompanyList(Long companyId,
			Long roleId, Long employeeId);

	/**
	 * purpose : get Employee FilterList.
	 * 
	 * @param Long
	 *            the companyId
	 * @return ManageUserAddCompanyResponseForm contains companies list.
	 */
	List<EmployeeFilterListForm> getEmployeeFilterList(Long companyId);

	/**
	 * purpose : get Employee FilterList for Edit.
	 * 
	 * @param Long
	 *            the roleId
	 * @param Long
	 *            the companyId
	 * @param Long
	 *            employeeId
	 * @return EmployeeFilterListForm contains Employee FilterList .
	 */
	List<EmployeeFilterListForm> getEditEmployeeFilterList(Long employeeId,
			Long roleId, Long companyId);

	/**
	 * purpose : Delete Filter for Employee.
	 * 
	 * @param Long
	 *            the filterId
	 */
	void deleteFilter(Long filterId);

	/**
	 * purpose : save Employee FilterList.
	 * 
	 * @param Long
	 *            the roleId
	 * @param String
	 *            the metaData
	 * @param Long
	 *            the companyId
	 * @param Long
	 *            employeeId
	 * @return EmployeeFilterListForm contains Employee FilterList .
	 */
	String saveEmployeeFilterList(String metaData, Long employeeId,
			Long roleId, Long companyId);

	String isPayAsiaUserAdmin(Long employeeId, Long companyId);

	DynamicFormSectionFormResponse viewSectionName(Long roleId, Long companyId,
			SortCondition sortDTO, String searchCondition, String searchText);

	String saveRolePrivileges(Long companyId, String roleId,
			String[] privilegeIdsArr);

	String saveRoleSections(Long companyId, String roleId,
			String[] sectionIdsArr, boolean overrideSection);

	String saveEmployeeRoleWithAssignCompany(Long sessionCompanyId,
			String roleId, Long userId, String[] companyIdsArr);

	String saveCompanySection(Long roleId, Long companyId, Long userId,
			String[] sectionIdsArr);

	List<DynamicFormSectionForm> getAssignCompanySection(Long roleId,
			Long employeeId, Long companyId);

	String isSeletedCompanyAssignToUser(Long roleId, Long employeeId,
			Long companyId);

	String saveEmployeeRoleWithDefaultCompany(Long companyId, String roleId,
			String[] userIdsArr, String[] allUserIds);

}
