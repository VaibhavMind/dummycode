package com.payasia.dao;

import java.util.List;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import com.payasia.common.dto.HRLetterConditionDTO;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.HRLetter;

/**
 * The Interface HRLetter.
 */
/**
 * @author vivekjain
 * 
 */
public interface HRLetterDAO {

	/**
	 * Save HRLetter Object.
	 * 
	 * @param hrLetter
	 *            the hr letter
	 * @return the hR letter
	 */
	HRLetter save(HRLetter hrLetter);

	/**
	 * Find HRLetter Objects List by companyId.
	 * 
	 * @param companyId
	 *            the company id
	 * @param pageDTO
	 *            the page dto
	 * @param sortDTO
	 *            the sort dto
	 * @return the list
	 */
	List<HRLetter> findByCompany(long companyId, PageRequest pageDTO,
			SortCondition sortDTO);

	/**
	 * Gets the sort path for hr letter.
	 * 
	 * @param sortDTO
	 *            the sort dto
	 * @param hrLetterRoot
	 *            the hr letter root
	 * @param hrLetterRootJoin
	 *            the hr letter root join
	 * @return the sort path for hr letter
	 */
	Path<String> getSortPathForHRLetter(SortCondition sortDTO,
			Root<HRLetter> hrLetterRoot,
			Join<HRLetter, Company> hrLetterRootJoin);

	/**
	 * Delete HRLetter Object.
	 * 
	 * @param hrLetter
	 *            the hr letter
	 */
	void delete(HRLetter hrLetter);

	/**
	 * Find HRLetter Object by letterId.
	 * 
	 * @param letterId
	 *            the letter id
	 * @return the hR letter
	 */
	HRLetter findById(long letterId);

	/**
	 * Update HRLetter Object.
	 * 
	 * @param hrLetter
	 *            the hr letter
	 */
	void update(HRLetter hrLetter);

	/**
	 * Gets the count hr letter by companyId.
	 * 
	 * @param conditionDTO
	 *            the condition dto
	 * @param companyId
	 *            the company id
	 * @return the count hr letter by company
	 */
	int getCountHRLetterByCompany(Long companyId);

	/**
	 * Find HRLetter Object by letterId ,letterName and companyId.
	 * 
	 * @param letterId
	 *            the letter id
	 * @param letterName
	 *            the letter name
	 * @param companyId
	 *            the company id
	 * @return the hR letter
	 */
	HRLetter findByLetterAndCompany(Long letterId, String letterName,
			long companyId);

	/**
	 * Find HRLetter Objects List by condition companyId and
	 * HRLetterConditionDTO.
	 * 
	 * @param conditionDTO
	 *            the condition dto
	 * @param companyId
	 *            the company id
	 * @param pageDTO
	 *            the page dto
	 * @param sortDTO
	 *            the sort dto
	 * @return the list
	 */
	List<HRLetter> findByCondition(HRLetterConditionDTO conditionDTO,
			Long companyId, boolean isActive, PageRequest pageDTO,
			SortCondition sortDTO);

	int getCountHRLetterByCondition(HRLetterConditionDTO conditionDTO,
			Long companyId, boolean isActive);
	
	HRLetter findByHRLetterId(long letterId,Long companyId);

}
