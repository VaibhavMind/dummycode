/**
 * @author ragulapraveen
 *
 */
package com.payasia.dao;

import java.util.List;
import java.util.Map;

import com.payasia.common.dto.DataImportKeyValueDTO;
import com.payasia.common.dto.PayslipConditionDTO;
import com.payasia.common.form.ExcelExportFiltersForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.Payslip;

/**
 * The Interface PayslipDAO.
 */
public interface PayslipDAO {

	/**
	 * Save Payslip Object .
	 * 
	 * @param payslip
	 *            the payslip
	 */
	void save(Payslip payslip);

	/**
	 * Update.
	 * 
	 * @param payslip
	 *            the payslip
	 */
	void update(Payslip payslip);

	/**
	 * Delete Payslip Object.
	 * 
	 * @param payslip
	 *            the payslip
	 */
	void delete(Payslip payslip);

	/**
	 * Find Payslip Object by employeeId .
	 * 
	 * @param empId
	 *            the emp id
	 * @return the list
	 */
	List<Payslip> findByEmployee(Long empId);

	/**
	 * Save return Payslip Object.
	 * 
	 * @param payslip
	 *            the payslip
	 * @return the payslip
	 */
	Payslip saveReturn(Payslip payslip);

	/**
	 * Find Payslip Object by payslipConditionDTO.
	 * 
	 * @param payslipConditionDTO
	 *            the payslip condition dto
	 * @return the list
	 */
	List<Payslip> findByCondition(PayslipConditionDTO payslipConditionDTO);

	/**
	 * Delete Payslip Object by payslipConditionDTO by condition.
	 * 
	 * @param payslipConditionDTO
	 *            the payslip condition dto
	 */
	void deleteByCondition(PayslipConditionDTO payslipConditionDTO);

	/**
	 * Find Payslip Object list .
	 * 
	 * @param companyId
	 *            the company id
	 * @return the list
	 */
	List<Payslip> findAll(long companyId);

	/**
	 * Gets the payslip.
	 * 
	 * @param payslipConditionDTO
	 *            the payslip condition dto
	 * @return the payslip
	 */
	Payslip getPayslip(PayslipConditionDTO payslipConditionDTO);

	/**
	 * Find Payslip Object by colMap,formIds,finalFilterLIst and companyID.
	 * 
	 * @param colMap
	 *            the col map
	 * @param formIds
	 *            the form ids
	 * @param finalFilterList
	 *            the final filter list
	 * @param companyId
	 *            the company id
	 * @return the list
	 */
	List<Object[]> findByCondition(Map<String, DataImportKeyValueDTO> colMap,
			List<Long> formIds, List<ExcelExportFiltersForm> finalFilterList,
			Long companyId);

	/**
	 * Gets the yTM value gt.
	 * 
	 * @param currentMonth
	 *            the current month
	 * @param finStartMonth
	 *            the fin start month
	 * @param year
	 *            the year
	 * @param employeeId
	 *            the employee id
	 * @param companyId
	 *            the company id
	 * @param payslipFrequencyId
	 *            the payslip frequency id
	 * @param part
	 *            the part
	 * @param formulaMap
	 *            the formula map
	 * @return the yTM value gt
	 */
	Double getYTMValueGT(long currentMonth, long finStartMonth, int year,
			long employeeId, long companyId, long payslipFrequencyId, int part,
			Map<Long, DataImportKeyValueDTO> formulaMap);

	/**
	 * Gets the yTM value lt.
	 * 
	 * @param currentMonth
	 *            the current month
	 * @param finStartMonth
	 *            the fin start month
	 * @param year
	 *            the year
	 * @param employeeId
	 *            the employee id
	 * @param companyId
	 *            the company id
	 * @param payslipFrequencyId
	 *            the payslip frequency id
	 * @param part
	 *            the part
	 * @param formulaMap
	 *            the formula map
	 * @return the yTM value lt
	 */
	Double getYTMValueLT(long currentMonth, long finStartMonth, int year,
			long employeeId, long companyId, long payslipFrequencyId, int part,
			Map<Long, DataImportKeyValueDTO> formulaMap);

	/**
	 * Gets the year list.
	 * 
	 * @param employeeId
	 *            the employee id
	 * @return the year list
	 */
	List<Integer> getYearList(Long employeeId);

	List<Object[]> getLatestPayslipEffectiveDate(Long companyId);

	List<Payslip> getPayslips(PayslipConditionDTO payslipConditionDTO);

	Payslip findByID(long payslipId);

	List<Integer> getParts(PayslipConditionDTO payslipConditionDTO);

	/**
	 * Get Released Payslip Details for Month, Year, Part
	 * 
	 * @param employeeId
	 *            the employee id
	 * @return the year list
	 */

	List<Payslip> getReleasedPayslipDetails(
			PayslipConditionDTO payslipConditionDTO, Long companyId);

	Payslip fetchPayslipByEmpId(Long payslip, Long employeeId);

	List<Payslip> getPayslipEmployee(PayslipConditionDTO payslipConditionDTO);

	List<Payslip> getPayslipEmployee(PayslipConditionDTO payslipConditionDTO, PageRequest pageDTO,
			SortCondition sortDTO, String workingCompanyId);

	List<Payslip> getPositionPayslipEmployee(PayslipConditionDTO payslipConditionDTO, PageRequest pageDTO,
			SortCondition sortDTO, String workingCompanyId, int startPos);


}
