package com.payasia.logic;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.payasia.common.form.CompanyForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.form.SwitchCompanyForm;
import com.payasia.common.form.SwitchCompanyResponse;

/**
 * @author vivekjain
 * 
 */
/**
 * The Interface SwitchCompanyLogic.
 */
@Transactional
public interface SwitchCompanyLogic {

	/**
	 * purpose : Switch Company.
	 * 
	 * @param Long
	 *            the companyId
	 * @return CompanyCode
	 */
	void SwitchCompany(Long companyId);

	/**
	 * purpose : get Number Of Open Tabs count.
	 * 
	 */
	String getNumberOfOpenTabs();

	/**
	 * purpose : get Switch Company List.
	 * 
	 * @param searchText
	 * @param searchCondition
	 * 
	 * @param PageRequest
	 *            the pageDTO
	 * @param SortCondition
	 *            the sortDTO
	 * @param Long
	 *            the employeeId
	 * @return SwitchCompanyResponse contains Switch CompanyList
	 */
	SwitchCompanyResponse getSwitchCompanyList(PageRequest pageDTO,
			SortCondition sortDTO, Long employeeId, String searchCondition,
			String searchText, Boolean includeInactiveCompany);

	/**
	 * purpose : get Switch Company List.
	 * 
	 * @param Long
	 *            the employeeId
	 * @return SwitchCompanyForm contains SwitchCompany List
	 */
	List<SwitchCompanyForm> getSwitchCompanyList(Long employeeId);

	/**
	 * purpose : get Company Name.
	 * 
	 * @param Long
	 *            the companyId
	 */
	CompanyForm getCompany(Long companyId);

	SwitchCompanyResponse getExportCompanies(PageRequest pageDTO,
			SortCondition sortDTO, Long employeeId, Long companyId);

}