package com.payasia.dao;

import java.util.List;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.PasswordSecurityQuestionMaster;

/**
 * The Interface PasswordSecurityQuestionMasterDAO.
 */
/**
 * @author vivekjain
 * 
 */
public interface PasswordSecurityQuestionMasterDAO {

	/**
	 * Save PasswordSecurityQuestionMaster Object.
	 * 
	 * @param passwordSecurityQuestionMaster
	 *            the password security question master
	 */
	void save(PasswordSecurityQuestionMaster passwordSecurityQuestionMaster);

	/**
	 * Update PasswordSecurityQuestionMaster Object.
	 * 
	 * @param passwordSecurityQuestionMaster
	 *            the password security question master
	 */
	void update(PasswordSecurityQuestionMaster passwordSecurityQuestionMaster);

	/**
	 * Find PasswordSecurityQuestionMaster Objects List by condition companyId.
	 * 
	 * @param companyId
	 *            the company id
	 * @param pageDTO
	 *            the page dto
	 * @param sortDTO
	 *            the sort dto
	 * @return the list
	 */
	List<PasswordSecurityQuestionMaster> findByConditionCompany(Long companyId,
			PageRequest pageDTO, SortCondition sortDTO);

	/**
	 * Gets the sort path for password security question.
	 * 
	 * @param sortDTO
	 *            the sort dto
	 * @param passTypeRoot
	 *            the pass type root
	 * @param passTypeRootJoin
	 *            the pass type root join
	 * @return the sort path for password security question
	 */
	Path<String> getSortPathForPasswordSecurityQuestion(SortCondition sortDTO,
			Root<PasswordSecurityQuestionMaster> passTypeRoot,
			Join<PasswordSecurityQuestionMaster, Company> passTypeRootJoin);

	/**
	 * Gets PasswordSecurityQuestionMaster Object for edit by companyId and
	 * pwdSecurityQuestionId.
	 * 
	 * @param companyId
	 *            the company id
	 * @param pwdSecurityQuestionId
	 *            the pwd security question id
	 * @return the question for edit
	 */
	PasswordSecurityQuestionMaster getQuestionForEdit(Long companyId,
			Long pwdSecurityQuestionId);

	/**
	 * Delete PasswordSecurityQuestionMaster Object.
	 * 
	 * @param passwordSecurityQuestionMaster
	 *            the password security question master
	 */
	void delete(PasswordSecurityQuestionMaster passwordSecurityQuestionMaster);

	/**
	 * Find PasswordSecurityQuestionMaster Object by pwdSecurityQuestionId.
	 * 
	 * @param pwdSecurityQuestionId
	 *            the pwd security question id
	 * @return the password security question master
	 */
	PasswordSecurityQuestionMaster findById(Long pwdSecurityQuestionId);

	/**
	 * Gets the count for condition companyId.
	 * 
	 * @param companyId
	 *            the company id
	 * @return the count for condition company
	 */
	int getCountForConditionCompany(Long companyId);

}
