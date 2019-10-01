package com.payasia.dao;

import java.util.List;

import javax.persistence.Tuple;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import com.payasia.common.dto.EmployeeConditionDTO;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.CompanyGroup;
import com.payasia.dao.bean.EmployeeRoleMapping;

/**
 * The Interface EmployeeRoleMappingDAO.
 */
/**
 * @author vivekjain
 * 
 */
public interface EmployeeRoleMappingDAO {

	/**
	 * Find EmployeeRoleMapping Object by condition roleId, companyId and
	 * employeeId.
	 * 
	 * @param roleId
	 *            the role id
	 * @param companyId
	 *            the company id
	 * @param employeeId
	 *            the employee id
	 * @return the employee role mapping
	 */
	EmployeeRoleMapping findByCondition(Long roleId, Long companyId,
			Long employeeId);

	/**
	 * Find EmployeeRoleMapping Objects List by condition roleId and employeeId.
	 * 
	 * @param roleId
	 *            the role id
	 * @param employeeId
	 *            the employee id
	 * @return the list
	 */
	List<EmployeeRoleMapping> findByConditionRoleAndEmpID(Long roleId,
			Long employeeId);

	/**
	 * Save EmployeeRoleMapping Object.
	 * 
	 * @param employeeRoleMapping
	 *            the employee role mapping
	 */
	void save(EmployeeRoleMapping employeeRoleMapping);

	/**
	 * Update EmployeeRoleMapping Object.
	 * 
	 * @param employeeRoleMapping
	 *            the employee role mapping
	 */
	void update(EmployeeRoleMapping employeeRoleMapping);

	/**
	 * Delete EmployeeRoleMapping Object.
	 * 
	 * @param employeeRoleMapping
	 *            the employee role mapping
	 */
	void delete(EmployeeRoleMapping employeeRoleMapping);

	/**
	 * Delete EmployeeRoleMapping Object by condition roleId and employeeId.
	 * 
	 * @param employeeId
	 *            the employee id
	 * @param roleId
	 *            the role id
	 */
	void deleteByConditionRoleAndEmpId(long employeeId, long roleId);

	/**
	 * Delete EmployeeRoleMapping Object by condition companyId and employeeId.
	 * 
	 * @param employeeId
	 *            the employee id
	 * @param companyId
	 *            the company id
	 */
	void deleteByConditionCompanyAndEmpId(long employeeId, long companyId);

	/**
	 * Find EmployeeRoleMapping Objects List by condition CompanyId.
	 * 
	 * @param CompanyId
	 *            the company id
	 * @return the list
	 */
	List<EmployeeRoleMapping> findByCondition(Long CompanyId);

	/**
	 * Delete EmployeeRoleMapping Object by condition employeeId.
	 * 
	 * @param employeeId
	 *            the employee id
	 */
	void deleteByCondition(long employeeId,Long companyId);

	/**
	 * Find EmployeeRoleMapping Objects List by condition employeeId and
	 * companyId.
	 * 
	 * @param companyId
	 *            the company id
	 * @param employeeId
	 *            the employee id
	 * @return the list
	 */
	List<EmployeeRoleMapping> findByConditionEmpIdAndCompanyId(Long companyId,
			Long employeeId);

	/**
	 * save EmployeeRoleMapping Object By New Transaction.
	 * 
	 * @param employeeRoleMapping
	 *            the employee role mapping
	 */
	void newTranSave(EmployeeRoleMapping employeeRoleMapping);

	/**
	 * Gets EmployeeRoleMapping Objects List By employeeId.
	 * 
	 * @param employeeId
	 *            the employee id
	 * @param pageDTO
	 *            the page dto
	 * @param sortDTO
	 *            the sort dto
	 * @return the switch company list
	 */
	List<EmployeeRoleMapping> getSwitchCompanyList(Long employeeId,
			PageRequest pageDTO, SortCondition sortDTO);

	/**
	 * Gets the sort path for company and company group.
	 * 
	 * @param sortDTO
	 *            the sort dto
	 * @param companyRoot
	 *            the company root
	 * @param groupCompanyJoin
	 *            the group company join
	 * @return the sort path for company and company group
	 */
	Path<String> getSortPathForCompanyAndCompanyGroup(SortCondition sortDTO,
			Root<Company> companyRoot,
			Join<Company, CompanyGroup> groupCompanyJoin);

	/**
	 * Gets the count for get switch company list By employeeId.
	 * 
	 * @param employeeId
	 *            the employee id
	 * @return the count for get switch company list
	 */
	int getCountForGetSwitchCompanyList(Long employeeId);

	/**
	 * Find EmployeeRoleMapping Objects List by condition role id.
	 * 
	 * @param roleId
	 *            the role id
	 * @return the list
	 */
	List<EmployeeRoleMapping> findByConditionRoleID(Long roleId);

	/**
	 * Delete EmployeeRoleMapping Object by condition employeeId ,roleId and
	 * companyId .
	 * 
	 * @param employeeId
	 *            the employee id
	 * @param roleId
	 *            the role id
	 * @param companyId
	 *            the company id
	 */
	void deleteByCondition(Long employeeId, Long roleId, Long companyId);

	/**
	 * Delete EmployeeRoleMapping Object by condition companyId and roleId.
	 * 
	 * @param companyId
	 *            the company id
	 * @param roleId
	 *            the role id
	 */
	void deleteByCondition(Long companyId, Long roleId);

	List<EmployeeRoleMapping> findByConditionRoleAndEmpID(Long roleId,
			Long employeeId, boolean isPayasiaUserRoleOtherThanAdmin,
			Long payasiaCompanyGroupId);

	List<EmployeeRoleMapping> findByConditionRoleIdAndCompanyId(Long companyId,
			Long roleId);

	/**
	 * Find Employee which is Active and has company role
	 * 
	 * @param companyId
	 *            the company id
	 * @return the tuple list
	 */
	List<Tuple> findByConditionCompany(Long companyId);

	List<Tuple> findPayAsiaUsersByCond(EmployeeConditionDTO conditionDTO,
			Long payAsiaCompanyId, Long sessionCompanyId);

	List<EmployeeRoleMapping> findByConditionEmpIdAndCompanyId(Long companyId, Long employeeId, Long roleId);

}
