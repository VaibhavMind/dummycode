package com.payasia.logic.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.payasia.common.dto.AccessControlConditionDTO;
import com.payasia.common.dto.EmployeeShortListDTO;
import com.payasia.common.dto.HolidayCalendarConditionDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.CompanyHolidayCalendarForm;
import com.payasia.common.form.EmployeeListForm;
import com.payasia.common.form.EmployeeListFormPage;
import com.payasia.common.form.HolidayListForm;
import com.payasia.common.form.HolidayListResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.CompanyHolidayCalendarDAO;
import com.payasia.dao.CompanyHolidayCalendarDetailDAO;
import com.payasia.dao.CountryMasterDAO;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.EmployeeHolidayCalendarDAO;
import com.payasia.dao.HolidayConfigMasterDAO;
import com.payasia.dao.StateMasterDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.CompanyHolidayCalendar;
import com.payasia.dao.bean.CompanyHolidayCalendarDetail;
import com.payasia.dao.bean.CountryMaster;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeHolidayCalendar;
import com.payasia.dao.bean.HolidayConfigMaster;
import com.payasia.dao.bean.StateMaster;
import com.payasia.logic.GeneralLogic;
import com.payasia.logic.HolidayCalendarLogic;

@Component
public class HolidayCalendarLogicImpl implements HolidayCalendarLogic {
	/** The Constant logger. */
	private static final Logger LOGGER = Logger
			.getLogger(HolidayCalendarLogicImpl.class);
	@Resource
	StateMasterDAO stateMasterDAO;
	@Resource
	CountryMasterDAO countryMasterDAO;
	@Resource
	GeneralLogic generalLogic;
	@Resource
	EmployeeDAO employeeDAO;
	@Resource
	CompanyDAO companyDAO;
	@Resource
	CompanyHolidayCalendarDAO companyHolidayCalendarDAO;
	@Resource
	CompanyHolidayCalendarDetailDAO companyHolidayCalendarDetailDAO;
	@Resource
	HolidayConfigMasterDAO holidayConfigMasterDAO;

	@Resource
	EmployeeHolidayCalendarDAO employeeHolidayCalendarDAO;

	@Override
	public List<CompanyHolidayCalendarForm> getCountryList() {
		List<CompanyHolidayCalendarForm> holidayListMasterFormList = new ArrayList<CompanyHolidayCalendarForm>();
		List<CountryMaster> countryList = countryMasterDAO.findAll();
		for (CountryMaster countryMaster : countryList) {
			CompanyHolidayCalendarForm holidayListMasterForm = new CompanyHolidayCalendarForm();
			holidayListMasterForm.setCountryId(countryMaster.getCountryId());
			try {
				holidayListMasterForm.setCountryName(URLEncoder.encode(
						countryMaster.getCountryName(), "UTF-8"));
			} catch (UnsupportedEncodingException ex) {
				LOGGER.error(ex.getMessage(), ex);
			}
			holidayListMasterFormList.add(holidayListMasterForm);
		}
		return holidayListMasterFormList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.HolidayListMasterLogic#getStateList(java.lang.Long)
	 */
	@Override
	public List<CompanyHolidayCalendarForm> getStateList(Long countryId) {
		List<CompanyHolidayCalendarForm> holidayListMasterFormList = new ArrayList<CompanyHolidayCalendarForm>();
		List<StateMaster> stateList = stateMasterDAO.findByCountry(countryId);
		for (StateMaster stateMaster : stateList) {
			CompanyHolidayCalendarForm holidayListMasterForm = new CompanyHolidayCalendarForm();
			holidayListMasterForm.setStateId(stateMaster.getStateId());
			try {
				holidayListMasterForm.setStateName(URLEncoder.encode(
						stateMaster.getStateName(), "UTF-8"));
			} catch (UnsupportedEncodingException ex) {
				LOGGER.error(ex.getMessage(), ex);
			}
			holidayListMasterFormList.add(holidayListMasterForm);
		}
		return holidayListMasterFormList;
	}

	@Override
	public HolidayListResponse searchHolidayCalendar(PageRequest pageDTO,
			SortCondition sortDTO, String searchCondition, String searchText,
			int year, Long companyId) {
		HolidayCalendarConditionDTO conditionDTO = new HolidayCalendarConditionDTO();

		if (searchCondition.equals(PayAsiaConstants.HOLIDAY_CALENDAR_NAME)) {
			if (StringUtils.isNotBlank(searchText)) {

				try {
					conditionDTO.setCalName("%"
							+ URLDecoder.decode(searchText, "UTF-8") + "%");
				} catch (UnsupportedEncodingException ex) {
					LOGGER.error(ex.getMessage(), ex);
				}
			}

		}
		if (searchCondition.equals(PayAsiaConstants.HOLIDAY_CALENDAR_DESC)) {
			if (StringUtils.isNotBlank(searchText)) {

				try {
					conditionDTO.setCalDesc("%"
							+ URLDecoder.decode(searchText, "UTF-8") + "%");
				} catch (UnsupportedEncodingException ex) {
					LOGGER.error(ex.getMessage(), ex);
				}
			}

		}

		List<CompanyHolidayCalendarForm> companyHolidayCalendarFormList = new ArrayList<CompanyHolidayCalendarForm>();
		List<CompanyHolidayCalendar> companyHolidayCalendarVOList = companyHolidayCalendarDAO
				.findByCondition(conditionDTO, companyId, pageDTO, sortDTO);
		for (CompanyHolidayCalendar companyHolidayCalendarVO : companyHolidayCalendarVOList) {
			CompanyHolidayCalendarForm companyHolidayCalendarForm = new CompanyHolidayCalendarForm();
			companyHolidayCalendarForm.setCalName(companyHolidayCalendarVO
					.getCalendarName());
			companyHolidayCalendarForm.setCalDesc(companyHolidayCalendarVO
					.getCalendarDesc());
			// ID ENCRYPT
			companyHolidayCalendarForm.setHolidayCalId(FormatPreserveCryptoUtil.encrypt(companyHolidayCalendarVO
					.getCompanyHolidayCalendarId()));
			Integer noOfHolidays = companyHolidayCalendarDetailDAO
					.getHolidayCountByYear(companyHolidayCalendarVO
							.getCompanyHolidayCalendarId(), year);
			StringBuilder empCountBuilder = new StringBuilder();

			empCountBuilder.append("<span class='Text'><h2>"
					+ String.valueOf(noOfHolidays) + "</h2></span>");
			empCountBuilder
					.append("<span class='Textsmall' style='padding-top: 5px;'>&nbsp;Holidays</span>");
			empCountBuilder.append("<br><br>");
		
			// ID ENCRYPT-2
			empCountBuilder
					.append("<span class='Textsmall' style='padding-bottom: 3px;'><a class='alink' style='text-decoration: none;' href='#' onClick = 'editHolidayListFunc("
							+ FormatPreserveCryptoUtil.encrypt(companyHolidayCalendarVO
									.getCompanyHolidayCalendarId())
							+ ")'>[Edit]</a></span>");
			companyHolidayCalendarForm.setNoOfHolidays(String
					.valueOf(empCountBuilder));

			companyHolidayCalendarFormList.add(companyHolidayCalendarForm);
		}
		HolidayListResponse response = new HolidayListResponse();
		int recordSize = companyHolidayCalendarDAO.getCountForCondition(
				conditionDTO, companyId);
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
		response.setCompanyHolidayCalendarForm(companyHolidayCalendarFormList);

		return response;
	}

	@Override
	public String addHolidayCal(
			CompanyHolidayCalendarForm companyHolidayCalendarForm,
			Long companyId) {
		Company company = companyDAO.findById(companyId);
		CompanyHolidayCalendar companyHolidayCalendar = new CompanyHolidayCalendar();
		companyHolidayCalendar.setCalendarName(companyHolidayCalendarForm
				.getCalName());
		companyHolidayCalendar.setCalendarDesc(companyHolidayCalendarForm
				.getCalDesc());
		companyHolidayCalendar.setCompany(company);
		CompanyHolidayCalendar holidayCalendar = companyHolidayCalendarDAO
				.checkDuplicateCalendar(companyId,
						companyHolidayCalendarForm.getCalName(), null);
		if (holidayCalendar != null) {
			return "duplicate";
		} else {
			companyHolidayCalendarDAO.save(companyHolidayCalendar);
			return "success";
		}

	}

	@Override
	public CompanyHolidayCalendarForm getHolidayCalData(Long holidayCalId,
			Long companyId) {
		CompanyHolidayCalendar companyHolidayCalendar = companyHolidayCalendarDAO
				.findByID(holidayCalId,companyId);
		CompanyHolidayCalendarForm companyHolidayCalendarForm = new CompanyHolidayCalendarForm();
		/*
		 * ID ENCRYPT
		 * */
		
		companyHolidayCalendarForm.setHolidayCalId(FormatPreserveCryptoUtil.encrypt(companyHolidayCalendar
				.getCompanyHolidayCalendarId()));
		companyHolidayCalendarForm.setCalName(companyHolidayCalendar
				.getCalendarName());
		companyHolidayCalendarForm.setCalDesc(companyHolidayCalendar
				.getCalendarDesc());
		return companyHolidayCalendarForm;
	}

	@Override
	public String editHolidayCal(
			CompanyHolidayCalendarForm companyHolidayCalendarForm,
			Long companyId) {
		CompanyHolidayCalendar companyHolidayCalendar = companyHolidayCalendarDAO
				.findByID(companyHolidayCalendarForm.getHolidayCalId(),companyId);

		CompanyHolidayCalendar holidayCalendar = companyHolidayCalendarDAO
				.checkDuplicateCalendar(companyId,
						companyHolidayCalendarForm.getCalName(),
						companyHolidayCalendarForm.getHolidayCalId());
		if (holidayCalendar != null) {
			return "duplicate";
		} else {
			companyHolidayCalendar.setCalendarName(companyHolidayCalendarForm
					.getCalName());
			companyHolidayCalendar.setCalendarDesc(companyHolidayCalendarForm
					.getCalDesc());
			companyHolidayCalendarDAO.update(companyHolidayCalendar);
			return "success";
		}

	}

	@Override
	public void deleteHolidayCal(Long holidayCalId, Long companyId) {
		CompanyHolidayCalendar companyHolidayCalendar = companyHolidayCalendarDAO
				.findByID(holidayCalId,companyId);
		companyHolidayCalendarDAO.delete(companyHolidayCalendar);
	}

	@Override
	public HolidayListResponse getCalendarHolidayList(PageRequest pageDTO,
			SortCondition sortDTO, Long holidayCalId, Long companyId, int year) {

		List<CompanyHolidayCalendarForm> companyHolidayCalendarFormList = new ArrayList<CompanyHolidayCalendarForm>();
		List<CompanyHolidayCalendarDetail> companyHolidayCalendarVOList = companyHolidayCalendarDetailDAO
				.findByCondition(holidayCalId, companyId, year, pageDTO,
						sortDTO);
		for (CompanyHolidayCalendarDetail companyHolidayCalendarDetailVO : companyHolidayCalendarVOList) {
			CompanyHolidayCalendarForm companyHolidayCalendarForm = new CompanyHolidayCalendarForm();

			companyHolidayCalendarForm
					.setCountryName(companyHolidayCalendarDetailVO
							.getCountryMaster().getCountryName());
			companyHolidayCalendarForm
					.setStateName(companyHolidayCalendarDetailVO
							.getStateMaster().getStateName());
			companyHolidayCalendarForm.setHolidayDate(DateUtils
					.timeStampToString(companyHolidayCalendarDetailVO
							.getHolidayDate()));
			companyHolidayCalendarForm
					.setHolidayDesc(companyHolidayCalendarDetailVO
							.getHolidayDesc());
			/*
			 * ID ENCRYPT
			 * */
			companyHolidayCalendarForm
					.setCompanyHolidayCalDetailId(FormatPreserveCryptoUtil.encrypt(companyHolidayCalendarDetailVO
							.getCompanyHolidayCalendarDetailId()));
			companyHolidayCalendarFormList.add(companyHolidayCalendarForm);
		}
		HolidayListResponse response = new HolidayListResponse();
		int recordSize = companyHolidayCalendarDetailDAO.getCountForCondition(
				holidayCalId, companyId, year);
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
		response.setCompanyHolidayCalendarForm(companyHolidayCalendarFormList);

		return response;
	}

	@Override
	public CompanyHolidayCalendarForm getCompanyHolidayCalData(
			Long companyHolidayCalId, Long companyId) {
		CompanyHolidayCalendarDetail companyHolidayCalendarDetail = companyHolidayCalendarDetailDAO
				.findByID(companyHolidayCalId,companyId);
		CompanyHolidayCalendarForm companyHolidayCalendarForm = new CompanyHolidayCalendarForm();
		companyHolidayCalendarForm.setCountryId(companyHolidayCalendarDetail
				.getCountryMaster().getCountryId());
		companyHolidayCalendarForm.setCountryName(companyHolidayCalendarDetail
				.getCountryMaster().getCountryName());
		companyHolidayCalendarForm.setStateId(companyHolidayCalendarDetail
				.getStateMaster().getStateId());
		companyHolidayCalendarForm.setStateName(companyHolidayCalendarDetail
				.getStateMaster().getStateName());
		companyHolidayCalendarForm.setHolidayDate(DateUtils
				.timeStampToString(companyHolidayCalendarDetail
						.getHolidayDate()));
		companyHolidayCalendarForm.setHolidayDesc(companyHolidayCalendarDetail
				.getHolidayDesc());
		/*
		 * ID ENCRYPT
		 * */
		companyHolidayCalendarForm
				.setCompanyHolidayCalDetailId(FormatPreserveCryptoUtil.encrypt(companyHolidayCalendarDetail
						.getCompanyHolidayCalendarDetailId()));
		companyHolidayCalendarForm.setHolidayCalId(companyHolidayCalendarDetail
				.getCompanyHolidayCalendar().getCompanyHolidayCalendarId());
		return companyHolidayCalendarForm;
	}

	@Override
	public void deleteCompanyHolidayCal(Long companyHolidayCalDetailId,
			Long companyId) {
		CompanyHolidayCalendarDetail companyHolidayCalendarDetail = companyHolidayCalendarDetailDAO
				.findByID(companyHolidayCalDetailId,companyId);
		companyHolidayCalendarDetailDAO.delete(companyHolidayCalendarDetail);
	}

	@Override
	public String editCompanyHolidayCal(
			CompanyHolidayCalendarForm companyHolidayCalendarForm,
			Long companyHolidayCalId, Long companyId) {
		CompanyHolidayCalendarDetail companyHolidayCalendarDetail = companyHolidayCalendarDetailDAO
				.findByID(companyHolidayCalendarForm
						.getCompanyHolidayCalDetailId(),companyId);
		CompanyHolidayCalendarDetail checkCompanyHolidayCalendarDetail = companyHolidayCalendarDetailDAO
				.findByDateAndCalId(companyHolidayCalendarForm
						.getCompanyHolidayCalDetailId(), companyHolidayCalId,
						DateUtils.stringToTimestamp(companyHolidayCalendarForm
								.getHolidayDate()));
		if (checkCompanyHolidayCalendarDetail != null) {
			return "duplicate";
		}
		companyHolidayCalendarDetail
				.setHolidayDate(DateUtils
						.stringToTimestamp(companyHolidayCalendarForm
								.getHolidayDate()));
		companyHolidayCalendarDetail.setHolidayDesc(companyHolidayCalendarForm
				.getHolidayDesc());
		CountryMaster countryMaster = countryMasterDAO
				.findById(companyHolidayCalendarForm.getCountryId());
		companyHolidayCalendarDetail.setCountryMaster(countryMaster);

		StateMaster stateMaster = stateMasterDAO
				.findById(companyHolidayCalendarForm.getStateId());
		companyHolidayCalendarDetail.setStateMaster(stateMaster);

		companyHolidayCalendarDetailDAO.update(companyHolidayCalendarDetail);
		return "success";

	}

	@Override
	public String addCompanyHolidayData(
			CompanyHolidayCalendarForm companyHolidayCalendarForm,
			Long companyId, Long companyHolidayCalId) {
		CompanyHolidayCalendar companyHolidayCalendar = companyHolidayCalendarDAO
				.findByID(companyHolidayCalId,companyId);
		CompanyHolidayCalendarDetail companyHolidayCalendarDetail = new CompanyHolidayCalendarDetail();
		companyHolidayCalendarDetail
				.setHolidayDate(DateUtils
						.stringToTimestamp(companyHolidayCalendarForm
								.getHolidayDate()));
		companyHolidayCalendarDetail.setHolidayDesc(companyHolidayCalendarForm
				.getHolidayDesc());
		CountryMaster countryMaster = countryMasterDAO
				.findById(companyHolidayCalendarForm.getCountryId());
		companyHolidayCalendarDetail.setCountryMaster(countryMaster);

		StateMaster stateMaster = stateMasterDAO
				.findById(companyHolidayCalendarForm.getStateId());
		companyHolidayCalendarDetail.setStateMaster(stateMaster);
		companyHolidayCalendarDetail
				.setCompanyHolidayCalendar(companyHolidayCalendar);
		CompanyHolidayCalendarDetail checkCompanyHolidayCalendarDetail = companyHolidayCalendarDetailDAO
				.findByDateAndCalId(null, companyHolidayCalId, DateUtils
						.stringToTimestamp(companyHolidayCalendarForm
								.getHolidayDate()));
		if (checkCompanyHolidayCalendarDetail != null) {
			return "duplicate";
		} else {
			companyHolidayCalendarDetailDAO.save(companyHolidayCalendarDetail);
			return "success";
		}

	}

	@Override
	public EmployeeListFormPage fetchEmpsForCalendarTemplate(
			String searchCondition, String searchText, PageRequest pageDTO,
			SortCondition sortDTO, Long companyId, Long employeeId) {

		AccessControlConditionDTO conditionDTO = new AccessControlConditionDTO();

		int recordSize = 0;

		if (searchCondition.equals(PayAsiaConstants.EMPLOYEE_NUMBER)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setEmployeeNumber("%" + searchText.trim() + "%");
			}

		}
		if (searchCondition.equals(PayAsiaConstants.FIRST_NAME)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setFirstName("%" + searchText.trim() + "%");
			}

		}
		if (searchCondition.equals(PayAsiaConstants.LAST_NAME)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setLastName("%" + searchText.trim() + "%");
			}

		}

		List<Employee> empList;
		 
		 
		 
		EmployeeShortListDTO employeeShortListDTO = generalLogic
				.getShortListEmployeeIds(employeeId, companyId);
		conditionDTO.setEmployeeShortListDTO(employeeShortListDTO);

		empList = employeeDAO.findEmployeesCalendarTemplate(conditionDTO,
				pageDTO, sortDTO, companyId);
		List<EmployeeListForm> employeeList = new ArrayList<EmployeeListForm>();
		for (Employee employee : empList) {
			EmployeeListForm empForm = new EmployeeListForm();
			empForm.setEmployeeID((int) employee.getEmployeeId());
			empForm.setEmployeeNumber(employee.getEmployeeNumber());

			empForm.setFirstName(employee.getFirstName());

			if (employee.getLastName() != null) {
				empForm.setLastName(employee.getLastName());

			}
			if (employee.getEmail() != null) {
				empForm.setEmail(employee.getEmail());
			}

			if (employee.isStatus() == true) {
				empForm.setStatusMsg(PayAsiaConstants.EMPLOYEE_ENABLED);

			} else {
				empForm.setStatusMsg(PayAsiaConstants.EMPLOYEE_DISABLED);
			}
			employeeList.add(empForm);
		}

		EmployeeListFormPage employeeListFormPage = new EmployeeListFormPage();

		employeeListFormPage.setEmployeeListFrom(employeeList);

		return employeeListFormPage;
	}

	@Override
	public HolidayListResponse getHolidayMasterListForImport(
			PageRequest pageDTO, SortCondition sortDTO, String countryId,
			String stateId, String year, Long companyId) {

		List<CompanyHolidayCalendarForm> companyHolidayCalendarFormList = new ArrayList<CompanyHolidayCalendarForm>();
		List<HolidayConfigMaster> holidayConfigMasterVOList = holidayConfigMasterDAO
				.findByCondition(pageDTO, sortDTO, countryId, stateId, year);
		for (HolidayConfigMaster holidayConfigMasterVO : holidayConfigMasterVOList) {
			CompanyHolidayCalendarForm companyHolidayCalendarForm = new CompanyHolidayCalendarForm();

			companyHolidayCalendarForm.setCountryName(holidayConfigMasterVO
					.getCountryMaster().getCountryName());
			companyHolidayCalendarForm.setStateName(holidayConfigMasterVO
					.getStateMaster().getStateName());
			companyHolidayCalendarForm.setHolidayDate(DateUtils
					.timeStampToString(holidayConfigMasterVO.getHolidayDate()));
			companyHolidayCalendarForm.setHolidayDesc(holidayConfigMasterVO
					.getHolidayDesc());
			companyHolidayCalendarForm
					.setHolidayConfigMasterId(holidayConfigMasterVO
							.getHolidayConfigMasterId());
			companyHolidayCalendarFormList.add(companyHolidayCalendarForm);
		}
		HolidayListResponse response = new HolidayListResponse();
		int recordSize = holidayConfigMasterDAO.getHolidayListCount(
				String.valueOf(countryId), stateId, year);
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
		response.setCompanyHolidayCalendarForm(companyHolidayCalendarFormList);

		return response;
	}

	@Override
	public String importCompanyHolidayData(
			CompanyHolidayCalendarForm companyHolidayCalendarForm,
			Long companyId, String[] selectedIds, Long companyHolidayCalId) {
		 
		CompanyHolidayCalendar companyHolidayCalendar = companyHolidayCalendarDAO
				.findByID(companyHolidayCalId,companyId);
		for (int count = 0; count < selectedIds.length; count++) {
			if (StringUtils.isNotBlank(selectedIds[count])) {
				HolidayConfigMaster holidayConfigMasterVO = holidayConfigMasterDAO
						.findByID(Long.parseLong(selectedIds[count]));
				CompanyHolidayCalendarDetail calendarDetail = new CompanyHolidayCalendarDetail();
				calendarDetail
						.setCompanyHolidayCalendar(companyHolidayCalendar);
				calendarDetail.setCountryMaster(holidayConfigMasterVO
						.getCountryMaster());
				calendarDetail.setStateMaster(holidayConfigMasterVO
						.getStateMaster());
				calendarDetail.setHolidayDate(holidayConfigMasterVO
						.getHolidayDate());
				calendarDetail.setHolidayDesc(holidayConfigMasterVO
						.getHolidayDesc());

				CompanyHolidayCalendarDetail holidayCalendarDetail = companyHolidayCalendarDetailDAO
						.findByDateAndCalId(null, companyHolidayCalendar
								.getCompanyHolidayCalendarId(),
								holidayConfigMasterVO.getHolidayDate());
				if (holidayCalendarDetail != null) {
					calendarDetail
							.setCompanyHolidayCalendarDetailId(holidayCalendarDetail
									.getCompanyHolidayCalendarDetailId());
					companyHolidayCalendarDetailDAO.update(calendarDetail);
				} else {
					companyHolidayCalendarDetailDAO.save(calendarDetail);
				}
			}

		}
		return "success";
	}

	@Override
	public void unAssignCompanyHolidayCal(Long employeeHolidayCalId,
			Long companyId) {
		EmployeeHolidayCalendar employeeHolidayCalendar = employeeHolidayCalendarDAO
				.findByID(employeeHolidayCalId,companyId);
		employeeHolidayCalendarDAO.delete(employeeHolidayCalendar);
	}

	@Override
	public HolidayListResponse getHolidayCalendars(Long companyId) {
		HolidayListResponse holidayListResponse = new HolidayListResponse();

		List<CompanyHolidayCalendar> companyHolidayCalendars = companyHolidayCalendarDAO
				.findByCondition(new HolidayCalendarConditionDTO(), companyId,
						null, null);
		List<HolidayListForm> holidayCalendars = new ArrayList<>();
		for (CompanyHolidayCalendar companyHolidayCalendar : companyHolidayCalendars) {
			HolidayListForm holidayCalendar = new HolidayListForm();
			holidayCalendar.setCmpHolidayCalendarId(companyHolidayCalendar
					.getCompanyHolidayCalendarId());
			holidayCalendar.setCmpHolidayCalendarName(companyHolidayCalendar
					.getCalendarName());
			holidayCalendars.add(holidayCalendar);
		}
		holidayListResponse.setHolidayListForm(holidayCalendars);

		return holidayListResponse;
	}

	@Override
	public String assignHolCalToEmployees(Long companyId, String employeeIds,
			Long holidayCalId) {
		 
		int count = 0;
		int empCount;
		try {
			CompanyHolidayCalendar cmpHolCal = companyHolidayCalendarDAO
					.findByID(holidayCalId,companyId);
			String[] empIds = employeeIds.split(",");
			empCount = empIds.length;
			for (String employeeId : empIds) {
				EmployeeHolidayCalendar employeeHolCal = employeeHolidayCalendarDAO
						.isAssignedEmpExist(Long.parseLong(employeeId));
				if (employeeHolCal != null) {
					count++;
					continue;
				}
				EmployeeHolidayCalendar employeeHolidayCalendar = new EmployeeHolidayCalendar();
				Employee employee = employeeDAO.findById(Long
						.parseLong(employeeId));
				employeeHolidayCalendar.setEmployee(employee);
				employeeHolidayCalendar.setCompanyHolidayCalendar(cmpHolCal);

				employeeHolidayCalendarDAO.save(employeeHolidayCalendar);

			}

		} catch (Exception exception) {
			LOGGER.error(exception.getMessage(), exception);
			throw new PayAsiaSystemException(exception.getMessage(), exception);
		}
		if (count == empCount) {
			return "duplicate";
		} else {
			return "success";
		}

	}

	@Override
	public HolidayListResponse getEmployeeHolidayCalendars(
			String searchCondition, String searchText, PageRequest pageDTO,
			SortCondition sortDTO, Long companyId, Long employeeId) {
		HolidayCalendarConditionDTO conditionDTO = new HolidayCalendarConditionDTO();

		if (searchCondition.equals(PayAsiaConstants.HOLIDAY_CALENDAR_NAME)) {
			if (StringUtils.isNotBlank(searchText)) {

				try {
					conditionDTO.setCalName("%"
							+ URLDecoder.decode(searchText, "UTF-8") + "%");
				} catch (UnsupportedEncodingException ex) {
					LOGGER.error(ex.getMessage(), ex);
				}
			}

		}
		if (searchCondition
				.equals(PayAsiaConstants.LEAVE_REVIEWER_EMPLOYEE_NAME)) {
			if (StringUtils.isNotBlank(searchText)) {

				try {
					conditionDTO.setEmployeeName("%"
							+ URLDecoder.decode(searchText, "UTF-8") + "%");
				} catch (UnsupportedEncodingException ex) {
					LOGGER.error(ex.getMessage(), ex);
				}
			}

		}
		if (searchCondition
				.equals(PayAsiaConstants.PAYDATA_COLLECTION_EMPLOYEE_NUM)) {
			if (StringUtils.isNotBlank(searchText)) {

				try {
					conditionDTO.setEmployeeNumber("%"
							+ URLDecoder.decode(searchText, "UTF-8") + "%");
				} catch (UnsupportedEncodingException ex) {
					LOGGER.error(ex.getMessage(), ex);
				}
			}

		}

		conditionDTO.setCompanyId(companyId);

		List<EmployeeHolidayCalendar> employeeHolidayCalendars = employeeHolidayCalendarDAO
				.getEmployeeHolidayCalendars(conditionDTO, pageDTO, sortDTO);
		List<HolidayListForm> empHolidayCalendars = new ArrayList<>();
		HolidayListResponse response = new HolidayListResponse();
		for (EmployeeHolidayCalendar employeeHolidayCalendar : employeeHolidayCalendars) {
			HolidayListForm holidayListForm = new HolidayListForm();
			holidayListForm
					.setEmployeeName(getEmployeeName(employeeHolidayCalendar
							.getEmployee()));
			/* ID ENCRYPT
			 * */
			holidayListForm
					.setEmployeeHolidayCalendarId(FormatPreserveCryptoUtil.encrypt(employeeHolidayCalendar
							.getEmployeeHolidayCalendarId()));
			holidayListForm.setCalendarName(employeeHolidayCalendar
					.getCompanyHolidayCalendar().getCalendarName());

			empHolidayCalendars.add(holidayListForm);

		}

		response.setHolidayListForm(empHolidayCalendars);
		int recordSize = employeeHolidayCalendarDAO.getCountForCondition(
				conditionDTO, companyId);
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

		return response;
	}

	private String getEmployeeName(Employee employee) {
		String employeeName = employee.getFirstName();
		if (employee.getMiddleName() != null) {
			employeeName = employeeName + " " + employee.getMiddleName();
		}
		if (employee.getLastName() != null) {
			employeeName = employeeName + " " + employee.getLastName();
		}
		employeeName = employeeName + " (" + employee.getEmployeeNumber() + ")";
		return employeeName;
	}
}
