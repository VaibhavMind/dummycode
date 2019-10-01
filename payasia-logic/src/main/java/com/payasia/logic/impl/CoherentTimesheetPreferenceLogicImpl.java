package com.payasia.logic.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.payasia.common.dto.EmployeeConditionDTO;
import com.payasia.common.dto.EmployeeShortListDTO;
import com.payasia.common.form.ClaimReviewerForm;
import com.payasia.common.form.CoherentOTEmployeeListForm;
import com.payasia.common.form.CoherentTimesheetPreferenceForm;
import com.payasia.common.form.EmployeeListForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.dao.CoherentOTEmployeeListDAO;
import com.payasia.dao.CoherentTimesheetPreferenceDAO;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.DataDictionaryDAO;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.LundinTimesheetPreferenceDAO;
import com.payasia.dao.TimesheetBatchDAO;
import com.payasia.dao.bean.CoherentOTEmployeeList;
import com.payasia.dao.bean.CoherentTimesheetPreference;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.DataDictionary;
import com.payasia.dao.bean.Employee;
import com.payasia.logic.CoherentTimesheetPreferenceLogic;
import com.payasia.logic.GeneralLogic;

@Component
public class CoherentTimesheetPreferenceLogicImpl implements
		CoherentTimesheetPreferenceLogic {
	@Resource
	LundinTimesheetPreferenceDAO lundinTimesheetPreferenceDAO;
	@Resource
	CompanyDAO companyDAO;
	@Resource
	TimesheetBatchDAO lundinTimesheetBatchDAO;
	@Resource
	DataDictionaryDAO dataDictionaryDAO;
	@Resource
	EmployeeDAO employeeDAO;
	@Resource
	GeneralLogic generalLogic;
	@Resource
	CoherentTimesheetPreferenceDAO coherentTimesheetPreferenceDAO;
	@Resource
	CoherentOTEmployeeListDAO coherentOTEmployeeListDAO;

	@Override
	public void saveCoherentTimesheetPreference(
			CoherentTimesheetPreferenceForm coherentTimesheetPreferenceForm,
			Long companyId) {

		CoherentTimesheetPreference coherentTimesheetPreferenceVO = coherentTimesheetPreferenceDAO
				.findByCompanyId(companyId);

		if (coherentTimesheetPreferenceVO == null) {
			CoherentTimesheetPreference coherentTimesheetPreference = new CoherentTimesheetPreference();
			coherentTimesheetPreference.setCompany(companyDAO
					.findById(companyId));
			// ingersollOTPreference.setOtPreferenceId(ingersollOTPreferenceForm
			// .getId());
			coherentTimesheetPreference.setCutoffDay(Integer
					.parseInt(coherentTimesheetPreferenceForm.getCutOffDay()));
			coherentTimesheetPreference
					.setUseSystemMailAsFromAddress(coherentTimesheetPreferenceForm
							.getUseSystemMailAsFromAddress());
			// Department Mapped by dataDictionary

			DataDictionary dataDictionary = dataDictionaryDAO
					.findById(coherentTimesheetPreferenceForm
							.getDataDictionaryId());
			coherentTimesheetPreference.setDepartment(dataDictionary);

			DataDictionary costCenter = dataDictionaryDAO
					.findById(coherentTimesheetPreferenceForm.getCostCenter());
			coherentTimesheetPreference.setCostCenter(costCenter);

			DataDictionary location = dataDictionaryDAO
					.findById(coherentTimesheetPreferenceForm.getLocation());

			coherentTimesheetPreference.setLocation(location);

			coherentTimesheetPreference.setWorkingHoursInADay(Double
					.valueOf(coherentTimesheetPreferenceForm
							.getWorkingHourPerday()));
			coherentTimesheetPreference
					.setIs_validation_72_Hours(coherentTimesheetPreferenceForm
							.getIsValidate72Hours());
			coherentTimesheetPreferenceDAO.save(coherentTimesheetPreference);
		} else {
			coherentTimesheetPreferenceVO.setCutoffDay(Integer
					.parseInt(coherentTimesheetPreferenceForm.getCutOffDay()));

			coherentTimesheetPreferenceVO
					.setUseSystemMailAsFromAddress(coherentTimesheetPreferenceForm
							.getUseSystemMailAsFromAddress());

			DataDictionary dataDictionary = dataDictionaryDAO
					.findById(coherentTimesheetPreferenceForm
							.getDataDictionaryId());
			coherentTimesheetPreferenceVO.setDepartment(dataDictionary);

			DataDictionary costCenter = dataDictionaryDAO
					.findById(coherentTimesheetPreferenceForm.getCostCenter());
			coherentTimesheetPreferenceVO.setCostCenter(costCenter);

			DataDictionary location = dataDictionaryDAO
					.findById(coherentTimesheetPreferenceForm.getLocation());

			coherentTimesheetPreferenceVO.setLocation(location);
			coherentTimesheetPreferenceVO.setWorkingHoursInADay(Double
					.valueOf(coherentTimesheetPreferenceForm
							.getWorkingHourPerday()));
			coherentTimesheetPreferenceVO
					.setIs_validation_72_Hours(coherentTimesheetPreferenceForm
							.getIsValidate72Hours());
			coherentTimesheetPreferenceDAO
					.update(coherentTimesheetPreferenceVO);
		}

	}

	@Override
	public CoherentTimesheetPreferenceForm getCoherentTimesheetPreference(
			Long companyId) {
		CoherentTimesheetPreference coherentTimesheetPreference = coherentTimesheetPreferenceDAO
				.findByCompanyId(companyId);
		CoherentTimesheetPreferenceForm coherentTimesheetPreferenceForm = new CoherentTimesheetPreferenceForm();
		if (coherentTimesheetPreference != null) {
			try {
				BeanUtils.copyProperties(coherentTimesheetPreferenceForm,
						coherentTimesheetPreference);

				coherentTimesheetPreferenceForm.setCutOffDay(String
						.valueOf(coherentTimesheetPreference.getCutoffDay()));

				if (coherentTimesheetPreference.getDepartment() != null) {
					coherentTimesheetPreferenceForm
							.setDataDictionaryId(coherentTimesheetPreference
									.getDepartment().getDataDictionaryId());
				} else {
					coherentTimesheetPreferenceForm.setDataDictionaryId(-1);
				}

				if (coherentTimesheetPreference.getCostCenter() != null) {
					coherentTimesheetPreferenceForm
							.setCostCenter(coherentTimesheetPreference
									.getCostCenter().getDataDictionaryId());
				} else {
					coherentTimesheetPreferenceForm.setCostCenter(-1);
					;
				}

				if (coherentTimesheetPreference.getLocation() != null) {
					coherentTimesheetPreferenceForm
							.setLocation(coherentTimesheetPreference
									.getLocation().getDataDictionaryId());
				} else {
					coherentTimesheetPreferenceForm.setLocation(-1);
					;
				}

				if (coherentTimesheetPreference.getWorkingHoursInADay() != null) {
					coherentTimesheetPreferenceForm.setWorkingHourPerday(String
							.valueOf(coherentTimesheetPreference
									.getWorkingHoursInADay()));
				}
				coherentTimesheetPreferenceForm
						.setIsValidate72Hours(coherentTimesheetPreference
								.getIs_validation_72_Hours());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return coherentTimesheetPreferenceForm;

	}

	@Override
	public ClaimReviewerForm searchEmployee(PageRequest pageDTO,
			SortCondition sortDTO, String empName, String empNumber,
			Long companyId, Long employeeId) {

		EmployeeConditionDTO conditionDTO = new EmployeeConditionDTO();

		if (StringUtils.isNotBlank(empName)) {
			conditionDTO.setEmployeeName("%" + empName.trim() + "%");
		}

		if (StringUtils.isNotBlank(empNumber)) {
			conditionDTO.setEmployeeNumber("%" + empNumber.trim() + "%");

		}

		EmployeeShortListDTO employeeShortListDTO = generalLogic
				.getShortListEmployeeIds(employeeId, companyId);
		conditionDTO.setEmployeeShortListDTO(employeeShortListDTO);
		conditionDTO.setEmployeeShortListDTO(employeeShortListDTO);

		int recordSize = employeeDAO.getCountForCondition(conditionDTO,
				companyId);

		List<Employee> finalList = employeeDAO.findByCondition(conditionDTO,
				pageDTO, sortDTO, companyId);

		List<EmployeeListForm> employeeListFormList = new ArrayList<EmployeeListForm>();

		for (Employee employee : finalList) {
			EmployeeListForm employeeForm = new EmployeeListForm();

			String employeeName = "";
			employeeName += employee.getFirstName() + " ";
			if (StringUtils.isNotBlank(employee.getLastName())) {
				employeeName += employee.getLastName();
			}
			employeeForm.setEmployeeName(employeeName);

			employeeForm.setEmployeeNumber(employee.getEmployeeNumber());
			/*ID DECRYPT*/
			employeeForm.setEmployeeID(FormatPreserveCryptoUtil.encrypt(employee.getEmployeeId()));
			employeeListFormList.add(employeeForm);
		}

		ClaimReviewerForm response = new ClaimReviewerForm();
		// if (pageDTO != null) {
		//
		// int pageSize = pageDTO.getPageSize();
		// int totalPages = recordSize / pageSize;
		//
		// if (recordSize % pageSize != 0) {
		// totalPages = totalPages + 1;
		// }
		// if (recordSize == 0) {
		// pageDTO.setPageNumber(0);
		// }
		//
		// response.setPage(pageDTO.getPageNumber());
		// response.setTotal(totalPages);
		// response.setRecords(recordSize);
		//
		// }

		response.setSearchEmployeeList(employeeListFormList);
		return response;
	}

	@Override
	public String saveCoherentOTEmployee(Long otEmployeeId, Long employeeId,
			Long companyId) {
		CoherentOTEmployeeList coherentOTEmployeeListVO = coherentOTEmployeeListDAO
				.findById(otEmployeeId);
		if (coherentOTEmployeeListVO != null) {
			return null;
		}
		Employee otEmployeeVO = employeeDAO.findByID(otEmployeeId);
		Company companyVO = companyDAO.findById(companyId);
		CoherentOTEmployeeList coherentOTEmployeeList = new CoherentOTEmployeeList();
		coherentOTEmployeeList.setEmployee(otEmployeeVO);
		coherentOTEmployeeList.setCompany(otEmployeeVO.getCompany());

		coherentOTEmployeeListDAO.save(coherentOTEmployeeList);
		return null;
	}

	@Override
	public CoherentOTEmployeeListForm searchCoherentSelectOTEmployees(
			Long companyId, String searchCondition, String searchText) {

		List<CoherentOTEmployeeList> finalList = coherentOTEmployeeListDAO
				.findByCondition(searchCondition, searchText, companyId);

		List<CoherentOTEmployeeListForm> employeeListFormList = new ArrayList<CoherentOTEmployeeListForm>();

		for (CoherentOTEmployeeList coherentOTEmployee : finalList) {
			CoherentOTEmployeeListForm coherentOTEmployeeListForm = new CoherentOTEmployeeListForm();

			String employeeName = "";
			employeeName += coherentOTEmployee.getEmployee().getFirstName()
					+ " ";
			if (StringUtils.isNotBlank(coherentOTEmployee.getEmployee()
					.getLastName())) {
				employeeName += coherentOTEmployee.getEmployee().getLastName();
			}
			coherentOTEmployeeListForm.setEmployeeName(employeeName);
			coherentOTEmployeeListForm.setEmployeeId(""
					+ coherentOTEmployee.getEmployee().getEmployeeId());

			coherentOTEmployeeListForm.setEmployeeNumber(coherentOTEmployee
					.getEmployee().getEmployeeNumber());
			coherentOTEmployeeListForm.setAddOn(DateUtils
					.timeStampToString(coherentOTEmployee.getCreatedDate()));
			employeeListFormList.add(coherentOTEmployeeListForm);
		}

		CoherentOTEmployeeListForm response = new CoherentOTEmployeeListForm();

		response.setSearchEmployeeList(employeeListFormList);
		return response;

	}

	@Override
	public void deleteCoherentOTEmployee(String employeeId) {
		coherentOTEmployeeListDAO.deleteCoherentOTEmployee(Long
				.parseLong(employeeId));
	}

}
