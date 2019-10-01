/**
 * @author abhisheksachdeva
 *
 */
package com.payasia.logic.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.payasia.common.dto.EmployeeConditionDTO;
import com.payasia.common.dto.EmployeeShortListDTO;
import com.payasia.common.form.AssignLeaveSchemeResponse;
import com.payasia.common.form.ChangeEmployeeNameListForm;
import com.payasia.common.form.ChangeEmployeeNumberFromResponse;
import com.payasia.common.form.EmployeeListForm;
import com.payasia.common.form.EmployeeNumberSrForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.dao.EmpNoSeriesMasterDAO;
import com.payasia.dao.EmpNumberHistoryDAO;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.EmployeeLoginDetailDAO;
import com.payasia.dao.bean.EmpNoSeriesMaster;
import com.payasia.dao.bean.EmpNumberHistory;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeLoginDetail;
import com.payasia.logic.ChangeEmployeeNumberLogic;
import com.payasia.logic.GeneralLogic;

/**
 * The Class ChangeEmployeeNumberLogicImpl.
 */
@Component
public class ChangeEmployeeNumberLogicImpl implements ChangeEmployeeNumberLogic {
	/** The Constant logger. */
	private static final Logger LOGGER = Logger
			.getLogger(ChangeEmployeeNumberLogicImpl.class);

	/** The emp number history dao. */
	@Resource
	EmpNumberHistoryDAO empNumberHistoryDAO;

	/** The employee dao. */
	@Resource
	EmployeeDAO employeeDAO;

	/** The emp no series master dao. */
	@Resource
	EmpNoSeriesMasterDAO empNoSeriesMasterDAO;
	@Resource
	EmployeeLoginDetailDAO employeeLoginDetailDAO;
	@Resource
	GeneralLogic generalLogic;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mind.payasia.common.logic.ChangeEmployeeNumberLogic#
	 * getChangeEmployeeNameList()
	 */
	@Override
	public ChangeEmployeeNumberFromResponse getChangeEmployeeNameList(
			PageRequest pageDTO, SortCondition sortDTO, Long companyId) {

		int recordSize = empNumberHistoryDAO.getCountForAll(companyId);

		List<EmpNumberHistory> empNumberHistoryList = empNumberHistoryDAO
				.findAll(pageDTO, sortDTO, companyId);

		List<ChangeEmployeeNameListForm> changeEmployeeNameList = new ArrayList<ChangeEmployeeNameListForm>();

		for (EmpNumberHistory empNumberHistory : empNumberHistoryList) {
			ChangeEmployeeNameListForm changeEmployeeNameListForm = new ChangeEmployeeNameListForm();
			changeEmployeeNameListForm.setEmpNoHistoryID(empNumberHistory
					.getEmpNoHistoryId());
			changeEmployeeNameListForm.setOldEmployeeNumber(empNumberHistory
					.getPrevEmpNo());
			changeEmployeeNameListForm.setChangeTo(empNumberHistory
					.getChangedEmpNo());
			String empName = "";
			empName += empNumberHistory.getEmployee2().getFirstName() + " ";
			if (StringUtils.isNotBlank(empNumberHistory.getEmployee2()
					.getLastName())) {
				empName += empNumberHistory.getEmployee2().getLastName();
			}
			changeEmployeeNameListForm.setChangedBy(empName);

			changeEmployeeNameListForm.setChangeReason(empNumberHistory
					.getReason());
			changeEmployeeNameListForm.setChangeOn(DateUtils
					.timeStampToString(empNumberHistory.getChangedDate()));
			changeEmployeeNameList.add(changeEmployeeNameListForm);
		}

		ChangeEmployeeNumberFromResponse response = new ChangeEmployeeNumberFromResponse();

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

		response.setChangeEmployeeNameListForm(changeEmployeeNameList);

		return response;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mind.payasia.common.logic.ChangeEmployeeNumberLogic#getSearchResultList
	 * (java.lang.String)
	 */
	@Override
	public ChangeEmployeeNumberFromResponse getSearchResultList(Long companyId,
			String keyword, String searchBy, PageRequest pageDTO,
			SortCondition sortDTO) {

		EmployeeConditionDTO conditionDTO = new EmployeeConditionDTO();

		try {
			keyword = "%" + URLDecoder.decode(keyword, "UTF-8") + "%";
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		int recordSize;
		List<Employee> employeeList;
		if (searchBy.toUpperCase().equals(PayAsiaConstants.EMPLOYEE_NAME)) {
			conditionDTO.setEmployeeName(keyword);
			recordSize = employeeDAO.getCountForConditionCompany(companyId,
					conditionDTO);
			employeeList = employeeDAO.findByConditionCompany(companyId,
					conditionDTO, pageDTO, sortDTO);
		} else {
			conditionDTO.setEmployeeNumber(keyword);
			recordSize = employeeDAO.getCountForConditionCompany(companyId,
					conditionDTO);
			employeeList = employeeDAO.findByConditionCompany(companyId,
					conditionDTO, pageDTO, sortDTO);
		}

		List<EmployeeListForm> employeeListForm = new ArrayList<EmployeeListForm>();

		for (Employee employee : employeeList) {
			EmployeeListForm employeeForm = new EmployeeListForm();
			String empName = "";
			empName += employee.getFirstName() + " ";
			if (StringUtils.isNotBlank(employee.getLastName())) {
				empName += employee.getLastName();
			}
			employeeForm.setEmployeeName(empName);
			employeeForm.setEmployeeNumber(employee.getEmployeeNumber());
			employeeForm
					.setIsActive(PayAsiaConstants.CHANGE_EMP_NUM_ACTIVE_YES);

			employeeForm.setEmployeeID(employee.getEmployeeId());
			employeeListForm.add(employeeForm);
		}

		ChangeEmployeeNumberFromResponse response = new ChangeEmployeeNumberFromResponse();

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

		}

		response.setSearchEmployeeList(employeeListForm);
		response.setRecords(recordSize);
		return response;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mind.payasia.common.logic.ChangeEmployeeNumberLogic#changeEmployeeNumber
	 * (com.mind.payasia.common.form.ChangeEmployeeNameListForm)
	 */
	@Override
	public String changeEmployeeNumber(
			ChangeEmployeeNameListForm changeEmployeeNameListForm,
			Long companyId, Long employeeId, Long empNumSeriesId) {
		Employee oldEmployeeVO = null;
		try {
			oldEmployeeVO = employeeDAO.findByNumber(
					URLDecoder.decode(
							changeEmployeeNameListForm.getOldEmployeeNumber(),
							"UTF-8"), companyId);
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}

		if (oldEmployeeVO == null) {
			return "invalid";
		}

		String oldEmployeeNumber = oldEmployeeVO.getEmployeeNumber();
		Employee changedByEmployeeVO = employeeDAO.findById(employeeId);

		EmpNoSeriesMaster EmpNoSeriesMasterVO = null;
		if (empNumSeriesId != null) {
			EmpNoSeriesMasterVO = empNoSeriesMasterDAO.findById(empNumSeriesId);
		}

		Employee employeeNew = oldEmployeeVO;
		String newEmployeeNumber = "";
		String changeEmployeeNumber = "";
		try {
			changeEmployeeNumber = URLDecoder.decode(
					changeEmployeeNameListForm.getChangeEmployeeNumber(),
					"UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		if (EmpNoSeriesMasterVO != null) {
			newEmployeeNumber = EmpNoSeriesMasterVO.getPrefix()
					+ changeEmployeeNumber + EmpNoSeriesMasterVO.getSuffix();

		} else {
			newEmployeeNumber = changeEmployeeNumber;
		}

		if (StringUtils.isNotBlank(newEmployeeNumber)) {
			Employee checkExistingEmployeeVO = employeeDAO.findByNumber(
					newEmployeeNumber, companyId);
			if (checkExistingEmployeeVO == null) {

				employeeNew.setEmployeeNumber(newEmployeeNumber);
				employeeDAO.update(employeeNew);

				EmployeeLoginDetail employeeLoginDetail = employeeLoginDetailDAO
						.findByEmployeeId(oldEmployeeVO.getEmployeeId());
				employeeLoginDetail.setLoginName(newEmployeeNumber.trim());
				employeeLoginDetailDAO.update(employeeLoginDetail);

				EmpNumberHistory empNumberHistory = new EmpNumberHistory();
				empNumberHistory.setChangedEmpNo(newEmployeeNumber);

				empNumberHistory.setEmployee1(employeeNew);
				empNumberHistory.setEmployee2(changedByEmployeeVO);
				empNumberHistory.setPrevEmpNo(oldEmployeeNumber);
				try {
					if (StringUtils.isNotBlank(changeEmployeeNameListForm
							.getChangeReason())) {
						empNumberHistory.setReason(URLDecoder.decode(
								changeEmployeeNameListForm.getChangeReason(),
								"UTF-8"));
					} else if (StringUtils.isBlank(changeEmployeeNameListForm
							.getChangeReason())) {
						return "reasonBlank";
					}
				} catch (UnsupportedEncodingException e) {
					LOGGER.error(e.getMessage(), e);
				}

				empNumberHistory.setChangedDate(DateUtils
						.getCurrentTimestampWithTime());
				empNumberHistoryDAO.save(empNumberHistory);
			} else {
				return PayAsiaConstants.EXISTS;
			}

		}

		return PayAsiaConstants.PAYASIA_SUCCESS;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.ChangeEmployeeNumberLogic#getEmployeeNumSeries(java
	 * .lang.Long)
	 */
	@Override
	public List<EmployeeNumberSrForm> getEmployeeNumSeries(Long companyId) {

		List<EmpNoSeriesMaster> EmpNoSeriesMasterVOList = empNoSeriesMasterDAO
				.findActiveSeriesByCompany(companyId);
		List<EmployeeNumberSrForm> EmployeeNumberSrFormList = new ArrayList<EmployeeNumberSrForm>();
		for (EmpNoSeriesMaster EmpNoSeriesMasterVO : EmpNoSeriesMasterVOList) {
			EmployeeNumberSrForm employeeNumberSrForm = new EmployeeNumberSrForm();
			employeeNumberSrForm.setEmpNoSeriesId(EmpNoSeriesMasterVO
					.getEmpNoSeriesId());
			employeeNumberSrForm.setDescription(EmpNoSeriesMasterVO
					.getSeriesDesc());
			employeeNumberSrForm.setPrefix(EmpNoSeriesMasterVO.getPrefix());
			employeeNumberSrForm.setSuffix(EmpNoSeriesMasterVO.getSuffix());
			employeeNumberSrForm.setPrefixSuffix(EmpNoSeriesMasterVO
					.getEmpNoSeriesId()
					+ "/"
					+ EmpNoSeriesMasterVO.getPrefix()
					+ "/" + (EmpNoSeriesMasterVO.getSuffix()));
			EmployeeNumberSrFormList.add(employeeNumberSrForm);
		}
		return EmployeeNumberSrFormList;

	}

	@Override
	public AssignLeaveSchemeResponse searchEmployee(PageRequest pageDTO,
			SortCondition sortDTO, String empName, String empNumber,
			Long companyId, Long employeeId) {

		EmployeeConditionDTO conditionDTO = new EmployeeConditionDTO();

		EmployeeShortListDTO employeeShortListDTO = generalLogic
				.getShortListEmployeeIds(employeeId, companyId);
		conditionDTO.setEmployeeShortListDTO(employeeShortListDTO);

		if (StringUtils.isNotBlank(empName)) {
			try {
				empName = URLDecoder.decode(empName, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				LOGGER.error(e.getMessage(), e);
			}
			conditionDTO.setEmployeeName("%" + empName.trim() + "%");
		}

		if (StringUtils.isNotBlank(empNumber)) {
			try {
				empNumber = URLDecoder.decode(empNumber, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				LOGGER.error(e.getMessage(), e);
			}
			conditionDTO.setEmployeeNumber("%" + empNumber.trim() + "%");

		}
		int recordSize = employeeDAO.getCountForCondition(conditionDTO,
				companyId);

		List<Employee> employeeVOList = employeeDAO.findEmployeesByCondition(
				conditionDTO, pageDTO, sortDTO, companyId);

		List<EmployeeListForm> employeeListFormList = new ArrayList<EmployeeListForm>();

		for (Employee employee : employeeVOList) {
			EmployeeListForm employeeForm = new EmployeeListForm();

			String employeeName = "";
			employeeName += employee.getFirstName() + " ";
			if (StringUtils.isNotBlank(employee.getLastName())) {
				employeeName += employee.getLastName();
			}
			employeeForm.setEmployeeName(employeeName);

			employeeForm.setEmployeeName(employeeName);

			employeeForm.setEmployeeNumber(employee.getEmployeeNumber());

			employeeForm.setEmployeeID(employee.getEmployeeId());
			employeeListFormList.add(employeeForm);
		}

		AssignLeaveSchemeResponse response = new AssignLeaveSchemeResponse();
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

		response.setSearchEmployeeList(employeeListFormList);
		return response;
	}
}
