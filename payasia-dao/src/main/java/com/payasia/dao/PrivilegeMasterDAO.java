package com.payasia.dao;

import java.util.List;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import com.payasia.common.dto.ManageRolesConditionDTO;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.PrivilegeMaster;

/**
 * The Interface PrivilegeMasterDAO.
 */
/**
 * @author vivekjain
 * 
 */
public interface PrivilegeMasterDAO {

	/**
	 * Find all PrivilegeMaster Objects List.
	 * 
	 * @param sortDTO
	 *            the sort dto
	 * @param conditionDTO
	 * @return the list
	 */
	List<PrivilegeMaster> findAll(SortCondition sortDTO,
			ManageRolesConditionDTO conditionDTO);

	/**
	 * Gets the sort path for all privilege.
	 * 
	 * @param sortDTO
	 *            the sort dto
	 * @param privilegeMasterRoot
	 *            the privilege master root
	 * @return the sort path for all privilege
	 */
	Path<String> getSortPathForAllPrivilege(SortCondition sortDTO,
			Root<PrivilegeMaster> privilegeMasterRoot);

	/**
	 * Find PrivilegeMaster Object List by roleId.
	 * 
	 * @param roleId
	 *            the role id
	 * @param conditionDTO
	 * @return the list
	 */
	List<PrivilegeMaster> findByRole(Long roleId,
			ManageRolesConditionDTO conditionDTO);

	/**
	 * Find PrivilegeMaster Object by privilegeId.
	 * 
	 * @param privilegeId
	 *            the privilege id
	 * @return the privilege master
	 */
	PrivilegeMaster findByID(Long privilegeId);

	/**
	 * Gets PrivilegeMaster Objects List by roleId.
	 * 
	 * @param roleId
	 *            the role id
	 * @return the privileges by role
	 */
	List<PrivilegeMaster> getPrivilegesByRole(Long roleId);

	/**
	 * Gets the count for roles by roleId.
	 * 
	 * @param roleId
	 *            the role id
	 * @return the count for role
	 */
	int getCountForRole(Long roleId, ManageRolesConditionDTO conditionDTO);

	/**
	 * Gets the count for all.
	 * 
	 * @return the count for all
	 */
	int getCountForAll(ManageRolesConditionDTO conditionDTO);

	Boolean getTimesheetPrivilegesByRole(Long roleId);

	List<PrivilegeMaster> getPrivilegesByRole(Long roleId, Long moduleId, String privilegeRole);

	
	

}
