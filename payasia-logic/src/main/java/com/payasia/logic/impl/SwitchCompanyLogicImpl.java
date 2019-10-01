package com.payasia.logic.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.persistence.Tuple;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.payasia.common.dto.CompanyConditionDTO;
import com.payasia.common.form.CompanyForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.form.SwitchCompanyForm;
import com.payasia.common.form.SwitchCompanyResponse;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.dao.AppConfigMasterDAO;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.EmployeeRoleMappingDAO;
import com.payasia.dao.SwitchCompanyDAO;
import com.payasia.dao.bean.AppConfigMaster;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.CompanyGroup_;
import com.payasia.dao.bean.CompanyModuleMapping;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.EmployeeRoleMapping;
import com.payasia.logic.BaseLogic;
import com.payasia.logic.SwitchCompanyLogic;

/**
 * The Class SwitchCompanyLogicImpl.
 */
/**
 * @author vivekjain
 * 
 */
@Component
public class SwitchCompanyLogicImpl extends BaseLogic implements
		SwitchCompanyLogic {

	/** The switch company dao. */
	@Resource
	SwitchCompanyDAO switchCompanyDAO;

	/** The company dao. */
	@Resource
	CompanyDAO companyDAO;

	/** The employee role mapping dao. */
	@Resource
	EmployeeRoleMappingDAO employeeRoleMappingDAO;

	/** The employee dao. */
	@Resource
	EmployeeDAO employeeDAO;

	/** The app config master dao. */
	@Resource
	AppConfigMasterDAO appConfigMasterDAO;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.SwitchCompanyLogic#SwitchCompany(java.lang.Long)
	 */
	@Override
	public void SwitchCompany(Long companyId) {
		switchCompanyDAO.SwitchCompany(companyId);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.SwitchCompanyLogic#getSwitchCompanyList(com.payasia
	 * .common.form.PageRequest, com.payasia.common.form.SortCondition,
	 * java.lang.Long)
	 */
	@Override
	public SwitchCompanyResponse getSwitchCompanyList(PageRequest pageDTO,
			SortCondition sortDTO, Long employeeId, String searchCondition,
			String searchText, Boolean includeInactiveCompany) {
		if (sortDTO.getColumnName().equals("")) {
			sortDTO.setColumnName(PayAsiaConstants.GROUP_NAME);
			sortDTO.setOrderType(PayAsiaConstants.ASC);
		}
		CompanyConditionDTO conditionDTO = new CompanyConditionDTO();
		if (StringUtils.isNotBlank(searchCondition)) {
			if (searchCondition.equals(PayAsiaConstants.COMPANY_NAME)) {
				if (StringUtils.isNotBlank(searchText)) {
					conditionDTO.setCompanyName(searchText.trim());
				}

			}
			if (searchCondition.equals(PayAsiaConstants.COMPANY_CODE)) {
				if (StringUtils.isNotBlank(searchText)) {
					conditionDTO.setCompanyCode(searchText.trim());
				}

			}
			if (searchCondition.equals(PayAsiaConstants.GROUP_NAME)) {
				if (StringUtils.isNotBlank(searchText)) {
					conditionDTO.setGroupName(searchText.trim());
				}

			}
			if (searchCondition.equals(PayAsiaConstants.GROUP_CODE)) {
				if (StringUtils.isNotBlank(searchText)) {
					conditionDTO.setGroupCode(searchText.trim());
				}

			}
		}

		conditionDTO.setIncludeActiveStatus(true);
		if (includeInactiveCompany != null) {
			conditionDTO.setIncludeInactiveCompany(includeInactiveCompany);
		}

		List<SwitchCompanyForm> switchCompanyFormList = new ArrayList<SwitchCompanyForm>();

		List<Tuple> companyListVO = companyDAO.findAssignCompanyToUser(sortDTO,
				pageDTO, employeeId, conditionDTO);
		for (Tuple companyTuple : companyListVO) {
			SwitchCompanyForm switchCompanyForm = new SwitchCompanyForm();
			switchCompanyForm.setCompanyId((Long) companyTuple.get(
					getAlias(Company_.companyId), Long.class));
			switchCompanyForm.setCompanyName((String) companyTuple.get(
					getAlias(Company_.companyName), String.class));
			switchCompanyForm.setCompanyCode((String) companyTuple.get(
					getAlias(Company_.companyCode), String.class));
			switchCompanyForm.setGroupId((Long) companyTuple.get(
					getAlias(CompanyGroup_.groupId), Long.class));
			switchCompanyForm.setGroupName((String) companyTuple.get(
					getAlias(CompanyGroup_.groupName), String.class));
			switchCompanyForm.setGroupCode((String) companyTuple.get(
					getAlias(CompanyGroup_.groupCode), String.class));
			switchCompanyFormList.add(switchCompanyForm);

		}

		SwitchCompanyResponse response = new SwitchCompanyResponse();
		int recordSize = companyDAO.getCountForAssignCompany(employeeId,
				conditionDTO);

		if (pageDTO != null) {

			int pageSize = pageDTO.getPageSize();
			int totalPages = recordSize / pageSize;

			if (recordSize % pageSize != 0) {
				totalPages = totalPages + 1;
			}
			if (recordSize == 0) {
				pageDTO.setPageNumber(0);
			}

			response.setPage(pageDTO.getPageNumber());
			response.setTotal(totalPages);

			response.setRecords(recordSize);
		}
		response.setSwitchCompanyFormList(switchCompanyFormList);

		return response;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.SwitchCompanyLogic#getSwitchCompanyList(com.payasia
	 * .common.form.PageRequest, com.payasia.common.form.SortCondition,
	 * java.lang.Long)
	 */
	@Override
	public SwitchCompanyResponse getExportCompanies(PageRequest pageDTO,
			SortCondition sortDTO, Long employeeId, Long companyId) {
		if (sortDTO.getColumnName().equals("")) {
			sortDTO.setColumnName(PayAsiaConstants.GROUP_NAME);
			sortDTO.setOrderType(PayAsiaConstants.ASC);
		}
		List<SwitchCompanyForm> switchCompanyFormList = new ArrayList<SwitchCompanyForm>();

		Company cmp = companyDAO.findById(companyId);

		List<Tuple> companyListVO = companyDAO.findAssignGroupCompanyToUser(
				sortDTO, pageDTO, employeeId, cmp.getCompanyGroup()
						.getGroupId());
		for (Tuple companyTuple : companyListVO) {
			SwitchCompanyForm switchCompanyForm = new SwitchCompanyForm();
			switchCompanyForm.setCompanyId((Long) companyTuple.get(
					getAlias(Company_.companyId), Long.class));
			switchCompanyForm.setCompanyName((String) companyTuple.get(
					getAlias(Company_.companyName), String.class));
			switchCompanyForm.setCompanyCode((String) companyTuple.get(
					getAlias(Company_.companyCode), String.class));
			switchCompanyForm.setGroupId((Long) companyTuple.get(
					getAlias(CompanyGroup_.groupId), Long.class));
			switchCompanyForm.setGroupName((String) companyTuple.get(
					getAlias(CompanyGroup_.groupName), String.class));
			switchCompanyForm.setGroupCode((String) companyTuple.get(
					getAlias(CompanyGroup_.groupCode), String.class));
			switchCompanyFormList.add(switchCompanyForm);

		}

		SwitchCompanyResponse response = new SwitchCompanyResponse();

		int recordSize = (companyDAO.getCountForSwitchGroupCompany(employeeId,
				cmp.getCompanyGroup().getGroupId())).intValue();

		if (pageDTO != null) {

			int pageSize = pageDTO.getPageSize();
			int totalPages = recordSize / pageSize;

			if (recordSize % pageSize != 0) {
				totalPages = totalPages + 1;
			}
			if (recordSize == 0) {
				pageDTO.setPageNumber(0);
			}

			response.setPage(pageDTO.getPageNumber());
			response.setTotal(totalPages);

			response.setRecords(recordSize);
		}
		response.setSwitchCompanyFormList(switchCompanyFormList);

		return response;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.SwitchCompanyLogic#getSwitchCompanyList(java.lang.Long)
	 */
	@Override
	public List<SwitchCompanyForm> getSwitchCompanyList(Long employeeId) {
		List<SwitchCompanyForm> switchCompanyFormList = new ArrayList<SwitchCompanyForm>();

		List<EmployeeRoleMapping> employeeRoleMappingVOList = employeeRoleMappingDAO
				.getSwitchCompanyList(employeeId, null, null);
		Set<Company> companySet = new LinkedHashSet<Company>();
		for (EmployeeRoleMapping employeeRoleMappingVO : employeeRoleMappingVOList) {
			companySet.add(employeeRoleMappingVO.getCompany());
		}
		for (Company company : companySet) {
			SwitchCompanyForm switchCompanyForm = new SwitchCompanyForm();
			switchCompanyForm.setCompanyId(company.getCompanyId());
			switchCompanyForm.setCompanyName(company.getCompanyName());
			switchCompanyForm.setCompanyCode(company.getCompanyCode());
			switchCompanyForm
					.setGroupId(company.getCompanyGroup().getGroupId());
			switchCompanyForm.setGroupName(company.getCompanyGroup()
					.getGroupName());
			switchCompanyForm.setGroupCode(company.getCompanyGroup()
					.getGroupCode());
			switchCompanyFormList.add(switchCompanyForm);
		}

		BeanComparator switchCompanyFormComp = new BeanComparator("companyName");
		Collections.sort(switchCompanyFormList, switchCompanyFormComp);
		return switchCompanyFormList;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.SwitchCompanyLogic#getNumberOfOpenTabs()
	 */
	@Override
	public String getNumberOfOpenTabs() {
		AppConfigMaster appConfigMaster = appConfigMasterDAO
				.findByName(PayAsiaConstants.NUMBER_OF_OPEN_TABS);
		if (appConfigMaster != null) {
			String NoOfOpenTabs = appConfigMaster.getParamValue();
			return NoOfOpenTabs;
		}
		return null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.SwitchCompanyLogic#getCompany(java.lang.Long)
	 */
	@Override
	public CompanyForm getCompany(Long companyId) {
		Company companyVO = companyDAO.findById(companyId);

		if (companyVO != null) {
			CompanyForm companyForm = new CompanyForm();
			companyForm.setCompanyCode(companyVO.getCompanyCode());
			companyForm.setCompanyName(companyVO.getCompanyName());
			companyForm.setDateFormat(companyVO.getDateFormat());
			companyForm
					.setGroupCode(companyVO.getCompanyGroup().getGroupCode());
			companyForm.setGmtOffset(companyVO.getTimeZoneMaster()
					.getGmtOffset());
			Set<CompanyModuleMapping> companyModuleList = companyVO
					.getCompanyModuleMappings();
			for (CompanyModuleMapping companyModuleMapping : companyModuleList) {

				if (companyModuleMapping.getModuleMaster().getModuleName()
						.equals(PayAsiaConstants.COMPANY_MODULE_CLAIM)) {
					companyForm.setHasClaimModule(true);
				} else if (companyModuleMapping.getModuleMaster()
						.getModuleName()
						.equals(PayAsiaConstants.COMPANY_MODULE_LEAVE)) {
					companyForm.setHasLeaveModule(true);
				} else if (companyModuleMapping.getModuleMaster()
						.getModuleName()
						.equals(PayAsiaConstants.COMPANY_MODULE_HRIS)) {
					companyForm.setHasHrisModule(true);
				} else if (companyModuleMapping
						.getModuleMaster()
						.getModuleName()
						.equals(PayAsiaConstants.COMPANY_MODULE_LUNDIN_TIMESHEET)) {
					companyForm.setHasLundinTimesheetModule(true);
				} else if (companyModuleMapping.getModuleMaster()
						.getModuleName()
						.equals(PayAsiaConstants.COMPANY_MODULE_LION_TIMESHEET)) {
					companyForm.setHasLionTimesheetModule(true);
				} else if (companyModuleMapping
						.getModuleMaster()
						.getModuleName()
						.equals(PayAsiaConstants.COMPANY_MODULE_COHERENT_TIMESHEET)) {
					companyForm.setHasCoherentTimesheetModule(true);
				} else if (companyModuleMapping
						.getModuleMaster()
						.getModuleName()
						.equals(PayAsiaConstants.COMPANY_MODULE_MOBILE)) {
					companyForm.setHasMobileModule(true);
				}
			}

			return companyForm;
		}
		return null;

	}

}