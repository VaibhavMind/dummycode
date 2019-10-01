package com.payasia.logic;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.payasia.common.form.EmpOTAddForm;

/**
 * @author vivekjain
 * 
 */
/**
 * The Interface EmpOTAddLogic.
 */
@Transactional
public interface EmpOTAddLogic {
	/**
	 * purpose : Get OT Template List.
	 * 
	 * @param Long
	 *            the companyId
	 * @return EmpOTAddForm contains list
	 */
	List<EmpOTAddForm> getOTTemplateList(Long companyId);

	/**
	 * purpose : Get OT Reviewer List.
	 * 
	 * @param Long
	 *            the otTemplateId
	 * @param Long
	 *            the companyId
	 * @param Long
	 *            the employeeId
	 * @return EmpOTAddForm contains list of reviewer
	 */
	EmpOTAddForm getOTReviewerList(Long otTemplateId, Long companyId,
			Long employeeId);

	/**
	 * purpose : Get OT Day Type List.
	 * 
	 * @return EmpOTAddForm List
	 */
	List<EmpOTAddForm> getOTDayTypeList();

	/**
	 * purpose : add OT Application.
	 * 
	 * @param String
	 *            the metaData
	 * @param String
	 *            the generalRemarks
	 * @param Long
	 *            the otTemplateId
	 * @param Long
	 *            the companyId
	 * @param Long
	 *            the employeeId
	 */
	void addOTApplication(Long otTemplateId, Long companyId, Long employeeId,
			String metaData, String generalRemarks);

}
