package com.payasia.dao;

import java.util.List;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.RoleMaster;

/**
 * The Interface RoleMasterDAO.
 */
/**
 * @author vivekjain
 * 
 */
public interface RoleMasterDAO {

	/**
	 * Gets the sort path for all role.
	 * 
	 * @param sortDTO
	 *            the sort dto
	 * @param roleMasterRoot
	 *            the role master root
	 * @return the sort path for all role
	 */
	Path<String> getSortPathForAllRole(SortCondition sortDTO,
			Root<RoleMaster> roleMasterRoot);

	/**
	 * Save RoleMaster Object .
	 * 
	 * @param roleMaster
	 *            the role master
	 */
	void save(RoleMaster roleMaster);

	/**
	 * Save and persist RoleMaster Object and returns a generated identity.
	 * 
	 * @param roleMaster
	 *            the role master
	 * @return the role master
	 */
	RoleMaster saveRole(RoleMaster roleMaster);

	/**
	 * Find RoleMaster Object by roleId.
	 * 
	 * @param roleId
	 *            the role id
	 * @return the role master
	 */
	RoleMaster findByID(long roleId);

	/**
	 * Update RoleMaster Object.
	 * 
	 * @param roleMaster
	 *            the role master
	 */
	void update(RoleMaster roleMaster);

	/**
	 * Delete RoleMaster Object.
	 * 
	 * @param roleMaster
	 *            the role master
	 */
	void delete(RoleMaster roleMaster);

	/**
	 * Gets the count for all roles.
	 * 
	 * @param companyId
	 *            the company id
	 * @return the count for all roles
	 */
	int getCountForAll(Long companyId);

	/**
	 * Find all RoleMaster Objects List.
	 * 
	 * @param sortDTO
	 *            the sort dto
	 * @param companyId
	 *            the company id
	 * @return the list
	 */
	List<RoleMaster> findAll(SortCondition sortDTO, Long companyId);

	/**
	 * Delete RoleMaster Object by condition companyId.
	 * 
	 * @param companyId
	 *            the company id
	 */
	void deleteByCondition(Long companyId);

	/**
	 * Find RoleMaster Object by role name and companyId.
	 * 
	 * @param roleName
	 *            the role name
	 * @param companyId
	 *            the company id
	 * @return the role master
	 */
	RoleMaster findByRoleName(String roleName, Long companyId);
	
	RoleMaster findByRoleId(long roleID, Long companyId);

}
