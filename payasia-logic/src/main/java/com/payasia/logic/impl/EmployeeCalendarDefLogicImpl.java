package com.payasia.logic.impl;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.payasia.common.dto.AccessControlConditionDTO;
import com.payasia.common.dto.CalendarPatternDTO;
import com.payasia.common.dto.EmployeeCalendarConditionDTO;
import com.payasia.common.dto.EmployeeCalendarDTO;
import com.payasia.common.dto.EmployeeShortListDTO;
import com.payasia.common.dto.LeaveCalendarEventDTO;
import com.payasia.common.form.CalendarDefForm;
import com.payasia.common.form.EmployeeCalendarDefForm;
import com.payasia.common.form.EmployeeListForm;
import com.payasia.common.form.EmployeeListFormPage;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.dao.CalendarCodeMasterDAO;
import com.payasia.dao.CompanyCalendarTemplateDAO;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.EmployeeCalendarConfigDAO;
import com.payasia.dao.EmployeeCalendarDAO;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.MonthMasterDAO;
import com.payasia.dao.bean.CalendarCodeMaster;
import com.payasia.dao.bean.CalendarPatternDetail;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.CompanyCalendarTemplate;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeCalendar;
import com.payasia.dao.bean.EmployeeCalendarConfig;
import com.payasia.logic.EmployeeCalendarDefLogic;
import com.payasia.logic.GeneralLogic;

@Component
public class EmployeeCalendarDefLogicImpl implements EmployeeCalendarDefLogic {

	@Resource
	EmployeeDAO employeeDAO;

	@Resource
	CompanyDAO companyDAO;
	@Resource
	EmployeeCalendarConfigDAO employeeCalendarConfigDAO;
	@Resource
	MonthMasterDAO monthMasterDAO;
	@Resource
	CompanyCalendarTemplateDAO companyCalendarTemplateDAO;
	@Resource
	CalendarCodeMasterDAO calendarCodeMasterDAO;
	@Resource
	EmployeeCalendarDAO employeeCalendarDAO;
	@Resource
	GeneralLogic generalLogic;

	/** The Constant logger. */
	private static final Logger LOGGER = Logger
			.getLogger(EmployeeCalendarDefLogicImpl.class);

	@Override
	public List<EmployeeCalendarDefForm> getCalTempYear(Long companyId) {
		List<EmployeeCalendarDefForm> employeeCalendarDefFormList = new ArrayList<>();
		List<Integer> yearList = companyCalendarTemplateDAO
				.getYearList(companyId);
		for (Integer year : yearList) {
			EmployeeCalendarDefForm employeeCalendarDefForm = new EmployeeCalendarDefForm();
			employeeCalendarDefForm.setYear(year);
			employeeCalendarDefFormList.add(employeeCalendarDefForm);
		}
		return employeeCalendarDefFormList;
	}

	@Override
	public List<CalendarDefForm> getEmployeeCalendarTemplates(Integer year,
			Long companyId) {
		List<CompanyCalendarTemplate> calendarTemplates = companyCalendarTemplateDAO
				.findByYearCompany(year, companyId);

		List<CalendarDefForm> listCalForm = new ArrayList<CalendarDefForm>();

		for (CompanyCalendarTemplate calTempVO : calendarTemplates) {
			CalendarDefForm calDefForm = new CalendarDefForm();

			calDefForm.setCalDefId(calTempVO.getCompanyCalendarTemplateId());
			calDefForm.setCalDefName(calTempVO.getTemplateName());
			listCalForm.add(calDefForm);
		}

		return listCalForm;
	}

	@Override
	public String assignCalendarTemplates(
			EmployeeCalendarDefForm employeeCalendarDefForm, Long companyId,
			String[] selectedIds) {
		Boolean status = true;
		Company companyVO = companyDAO.findById(companyId);
		CompanyCalendarTemplate companyCalendarTemplate = companyCalendarTemplateDAO
				.findByID(employeeCalendarDefForm.getCalendarTemplateId());
		for (int count = 0; count < selectedIds.length; count++) {
			if (StringUtils.isNotBlank(selectedIds[count])) {
				EmployeeCalendarConfig empCalendarTemplateVO = employeeCalendarConfigDAO
						.findByEmpIdAndEndDate(Long
								.parseLong(selectedIds[count]));
				if (empCalendarTemplateVO != null) {
					if (empCalendarTemplateVO.getEndDate() == null) {

						status = false;
					}
				}
				EmployeeCalendarConfig empCalendarByFromDateVO = employeeCalendarConfigDAO
						.checkEmpCalTempByDate(null,
								Long.parseLong(selectedIds[count]),
								employeeCalendarDefForm.getStartDate(),
								companyVO.getDateFormat());
				if (empCalendarByFromDateVO != null) {
					status = false;
				}
				if (StringUtils
						.isNotBlank(employeeCalendarDefForm.getEndDate())) {
					EmployeeCalendarConfig empCalendarByToDateVO = employeeCalendarConfigDAO
							.checkEmpCalTempByDate(null,
									Long.parseLong(selectedIds[count]),
									employeeCalendarDefForm.getEndDate(),
									companyVO.getDateFormat());
					if (empCalendarByToDateVO != null) {
						status = false;
					}
				}

				if (status) {

					EmployeeCalendarConfig empCalendarTemplate = new EmployeeCalendarConfig();
					empCalendarTemplate
							.setCompanyCalendarTemplate(companyCalendarTemplate);
					empCalendarTemplate.setStartDate(DateUtils
							.stringToTimestamp(
									employeeCalendarDefForm.getStartDate(),
									companyVO.getDateFormat()));
					if (StringUtils.isNotBlank(employeeCalendarDefForm
							.getEndDate())) {
						empCalendarTemplate.setEndDate(DateUtils
								.stringToTimestamp(
										employeeCalendarDefForm.getEndDate(),
										companyVO.getDateFormat()));
					}
					Employee employee = employeeDAO.findById(Long
							.parseLong(selectedIds[count]));
					empCalendarTemplate.setEmployee(employee);
					employeeCalendarConfigDAO.save(empCalendarTemplate);

				}
			}

		}
		return "success";

	}

	@Override
	public EmployeeCalendarDefForm getEmpCalendarTemplateList(
			String searchCondition, String searchText, PageRequest pageDTO,
			SortCondition sortDTO, Long companyId, Long languageId)
			throws UnsupportedEncodingException {

		int recordSize;
		EmployeeCalendarConditionDTO conditionDTO = new EmployeeCalendarConditionDTO();

		if (searchCondition.equals(PayAsiaConstants.EMPLOYEE_CALENDAR_NUMBER)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setEmployeeNumber("%" + searchText.trim() + "%");
			}

		}
		if (searchCondition
				.equals(PayAsiaConstants.EMPLOYEE_CALENDAR_FIRST_NAME)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setFirstName("%" + searchText.trim() + "%");
			}

		}
		if (searchCondition
				.equals(PayAsiaConstants.EMPLOYEE_CALENDAR_LAST_NAME)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setLastName("%" + searchText.trim() + "%");
			}

		}

		if (searchCondition
				.equals(PayAsiaConstants.EMPLOYEE_CALENDAR_TEMPLATE_NAME)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setTemplateName("%" + searchText.trim() + "%");
			}

		}

		if (searchCondition.equals(PayAsiaConstants.EMPLOYEE_CALENDAR_YEAR)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setYear(Integer.parseInt(searchText));
			}

		}

		List<EmployeeCalendarConfig> empCalendarTemplateList;
		recordSize = (employeeCalendarConfigDAO.getCountForCondition(
				conditionDTO, companyId));
		empCalendarTemplateList = employeeCalendarConfigDAO.findByCondition(
				conditionDTO, pageDTO, sortDTO, companyId);
		List<EmployeeCalendarDTO> employeeCalendarDTOList = new ArrayList<EmployeeCalendarDTO>();
		for (EmployeeCalendarConfig employeeCalendar : empCalendarTemplateList) {
			EmployeeCalendarDTO employeeCalendarDTO = new EmployeeCalendarDTO();
			employeeCalendarDTO.setEmployeeNumber(employeeCalendar
					.getEmployee().getEmployeeNumber());
			employeeCalendarDTO
					.setEmployeeName(getEmployeeName(employeeCalendar
							.getEmployee()));
			employeeCalendarDTO.setCalTemplateName(employeeCalendar
					.getCompanyCalendarTemplate().getTemplateName());
			/* ID ENCRYPT*/
			employeeCalendarDTO.setEmployeeCalendarId(FormatPreserveCryptoUtil.encrypt(employeeCalendar
					.getEmployeeCalendarConfigId()));
			employeeCalendarDTO.setStartDate(DateUtils
					.timeStampToString(employeeCalendar.getStartDate()));
			if (employeeCalendar.getEndDate() != null) {
				employeeCalendarDTO.setEndDate(DateUtils
						.timeStampToString(employeeCalendar.getEndDate()));
			}

			EmployeeCalendar employeeCalendarVO = employeeCalendarDAO
					.findByEmployeeCalConfigId(employeeCalendar
							.getEmployeeCalendarConfigId());
			StringBuilder calendarConfigure = new StringBuilder();
			if (employeeCalendarVO != null) {
				/* ID ENCRYPT*/
				calendarConfigure
						.append("<a class='alink' style='text-decoration: none;' href='#' onClick = 'empConfCalTemp("
								+ FormatPreserveCryptoUtil.encrypt(employeeCalendar
										.getEmployeeCalendarConfigId())
								+ ")'><span class='ctextgreen'>change</span></a>");

				employeeCalendarDTO.setConfigureCalendar(String
						.valueOf(calendarConfigure));
			} else {
				/* ID ENCRYPT*/
				calendarConfigure
						.append("<a class='alink' style='text-decoration: none;' href='#' onClick = 'empConfCalTemp("
								+ FormatPreserveCryptoUtil.encrypt(employeeCalendar
										.getEmployeeCalendarConfigId())
								+ ")'><span class='ctextgray'>No Change</span></a>");

				employeeCalendarDTO.setConfigureCalendar(String
						.valueOf(calendarConfigure));
			}

			employeeCalendarDTOList.add(employeeCalendarDTO);
		}

		EmployeeCalendarDefForm employeeCalendarDefForm = new EmployeeCalendarDefForm();

		if (pageDTO != null) {

			int pageSize = pageDTO.getPageSize();
			int totalPages = recordSize / pageSize;

			if (recordSize % pageSize != 0) {
				totalPages = totalPages + 1;
			}
			if (recordSize == 0) {
				pageDTO.setPageNumber(0);
			}

			employeeCalendarDefForm.setPage(pageDTO.getPageNumber());
			employeeCalendarDefForm.setTotal(totalPages);

			employeeCalendarDefForm.setRecords(recordSize);
		}
		employeeCalendarDefForm
				.setEmployeeCalendarDTOList(employeeCalendarDTOList);

		return employeeCalendarDefForm;
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

	private void setCalEventDTO(CalendarDefForm calDefForm,
			List<LeaveCalendarEventDTO> leaveCalendarEventDTOList,
			String dateFormat) {
		List<LeaveCalendarEventDTO> eventsJanuary = new ArrayList<>();
		List<LeaveCalendarEventDTO> eventsFebruary = new ArrayList<>();
		List<LeaveCalendarEventDTO> eventsMarch = new ArrayList<>();
		List<LeaveCalendarEventDTO> eventsApril = new ArrayList<>();
		List<LeaveCalendarEventDTO> eventsMay = new ArrayList<>();
		List<LeaveCalendarEventDTO> eventsJune = new ArrayList<>();
		List<LeaveCalendarEventDTO> eventsJuly = new ArrayList<>();
		List<LeaveCalendarEventDTO> eventsAugust = new ArrayList<>();
		List<LeaveCalendarEventDTO> eventsSeptember = new ArrayList<>();
		List<LeaveCalendarEventDTO> eventsOctober = new ArrayList<>();
		List<LeaveCalendarEventDTO> eventsNovember = new ArrayList<>();
		List<LeaveCalendarEventDTO> eventsDecember = new ArrayList<>();
		for (LeaveCalendarEventDTO eventDTO : leaveCalendarEventDTOList) {
			Date calDate = DateUtils.stringToDate(eventDTO.getCalendarDate(),
					PayAsiaConstants.DEFAULT_DATE_FORMAT);
			Calendar cal = Calendar.getInstance();
			cal.setTime(calDate);
			int month = cal.get(Calendar.MONTH);
			if (month == 0) {
				eventsJanuary.add(eventDTO);
			}
			if (month == 1) {
				eventsFebruary.add(eventDTO);
			}
			if (month == 2) {
				eventsMarch.add(eventDTO);
			}
			if (month == 3) {
				eventsApril.add(eventDTO);
			}
			if (month == 4) {
				eventsMay.add(eventDTO);
			}
			if (month == 5) {
				eventsJune.add(eventDTO);
			}
			if (month == 6) {
				eventsJuly.add(eventDTO);
			}
			if (month == 7) {
				eventsAugust.add(eventDTO);
			}
			if (month == 8) {
				eventsSeptember.add(eventDTO);
			}
			if (month == 9) {
				eventsOctober.add(eventDTO);
			}
			if (month == 10) {
				eventsNovember.add(eventDTO);
			}
			if (month == 11) {
				eventsDecember.add(eventDTO);
			}

		}
		calDefForm.setEventsJanuary(eventsJanuary);
		calDefForm.setEventsFebruary(eventsFebruary);
		calDefForm.setEventsMarch(eventsMarch);
		calDefForm.setEventsApril(eventsApril);
		calDefForm.setEventsMay(eventsMay);
		calDefForm.setEventsJune(eventsJune);
		calDefForm.setEventsJuly(eventsJuly);
		calDefForm.setEventsAugust(eventsAugust);
		calDefForm.setEventsSeptember(eventsSeptember);
		calDefForm.setEventsOctober(eventsOctober);
		calDefForm.setEventsNovember(eventsNovember);
		calDefForm.setEventsDecember(eventsDecember);

	}

	@Override
	public CalendarDefForm getEmpCalTempData(Long employeeCalendarId, int year,
			Long companyId) {
		CalendarDefForm calDefForm = new CalendarDefForm();
		List<CalendarPatternDTO> calendarPatternDTOList = new ArrayList<>();

		EmployeeCalendarConfig employeeCalendarConfig = employeeCalendarConfigDAO
				.findByID(employeeCalendarId);
		CompanyCalendarTemplate companyCalendarTemplate = companyCalendarTemplateDAO
				.findByID(employeeCalendarConfig.getCompanyCalendarTemplate()
						.getCompanyCalendarTemplateId());

		Company companyVO = companyDAO.findById(companyId);
		List<LeaveCalendarEventDTO> leaveCalendarEventDTOList = companyCalendarTemplateDAO
				.getCompanyCalendarTemplate(companyId, employeeCalendarConfig
						.getCompanyCalendarTemplate()
						.getCompanyCalendarTemplateId(), year, false, companyVO
						.getDateFormat(), employeeCalendarConfig.getEmployee()
						.getEmployeeId());
		Set<CalendarPatternDetail> calendarPatternDetailSet = companyCalendarTemplate
				.getCalendarPatternMaster().getCalendarPatternDetails();
		Set<String> calendarCodeSet = new HashSet<>();
		for (CalendarPatternDetail calendarPatternDetail : calendarPatternDetailSet) {
			calendarCodeSet.add(calendarPatternDetail.getCalendarCodeMaster()
					.getCode());

		}
		for (String calendarCode : calendarCodeSet) {
			CalendarPatternDTO calendarPatternDTO = new CalendarPatternDTO();
			calendarPatternDTO.setCalendarCode(calendarCode);
			calendarPatternDTOList.add(calendarPatternDTO);
		}
		calDefForm.setCalendarPatternDTOList(calendarPatternDTOList);
		setCalEventDTO(calDefForm, leaveCalendarEventDTOList,
				companyVO.getDateFormat());

		return calDefForm;
	}

	@Override
	public void deleteEmpCalTemplate(Long empCalTempId) {
		EmployeeCalendarConfig employeCalendar = employeeCalendarConfigDAO
				.findByID(empCalTempId);
		employeeCalendarConfigDAO.delete(employeCalendar);

	}

	@Override
	public List<EmployeeCalendarDefForm> getCalTemConfigYearList(
			Long empCalConfigId, Long companyId) {
		/* ID DECRYPT */
		empCalConfigId= FormatPreserveCryptoUtil.decrypt(empCalConfigId);
		
		Company companyVO = companyDAO.findById(companyId);
		List<EmployeeCalendarDefForm> employeeCalendarDefFormList = new ArrayList<>();
		EmployeeCalendarConfig employeeCalendarConfig = employeeCalendarConfigDAO
				.findByID(empCalConfigId);

		Date calDate = DateUtils.stringToDate(
				DateUtils.timeStampToString(
						employeeCalendarConfig.getStartDate(),
						companyVO.getDateFormat()), companyVO.getDateFormat());
		Calendar cal = Calendar.getInstance();
		cal.setTime(calDate);
		int startYear = cal.get(Calendar.YEAR);

		if (employeeCalendarConfig.getEndDate() == null) {
			for (int count = 0; count < 5; count++) {
				EmployeeCalendarDefForm calendarDefForm = new EmployeeCalendarDefForm();
				calendarDefForm.setYear(startYear + count);
				employeeCalendarDefFormList.add(calendarDefForm);
			}
			return employeeCalendarDefFormList;
		} else {
			Date calEndDate = DateUtils.stringToDate(
					DateUtils.timeStampToString(
							employeeCalendarConfig.getEndDate(),
							companyVO.getDateFormat()),
					companyVO.getDateFormat());
			Calendar calEnd = Calendar.getInstance();
			calEnd.setTime(calEndDate);
			int endYear = calEnd.get(Calendar.YEAR);
			for (int count = 0; count <= (endYear - startYear); count++) {
				EmployeeCalendarDefForm calendarDefForm = new EmployeeCalendarDefForm();
				calendarDefForm.setYear(startYear + count);
				employeeCalendarDefFormList.add(calendarDefForm);
			}
			return employeeCalendarDefFormList;
		}
	}

	@Override
	public void saveCalEventByDate(Long calTempId, String calCode,
			String eventDate, Long companyId) {
		Company companyVO = companyDAO.findById(companyId);
		//calTempId= FormatPreserveCryptoUtil.decrypt(calTempId);
		EmployeeCalendarConfig empCalConfig = employeeCalendarConfigDAO
				.findByID(calTempId);

		CalendarCodeMaster calendarCodeMaster = calendarCodeMasterDAO
				.findByCodeName(companyId, calCode);

		EmployeeCalendar employeeCalendarVO = employeeCalendarDAO.findByDate(
				eventDate, companyVO.getDateFormat());

		EmployeeCalendar employeeyCalendar = new EmployeeCalendar();
		employeeyCalendar.setCalendarDate(DateUtils.stringToTimestamp(
				eventDate, PayAsiaConstants.DEFAULT_DATE_FORMAT));
		employeeyCalendar.setEmployeeCalendarConfig(empCalConfig);

		employeeyCalendar.setCalendarCodeMaster(calendarCodeMaster);
		if (employeeCalendarVO != null) {
			employeeCalendarDAO.update(employeeyCalendar);
		} else {
			employeeCalendarDAO.save(employeeyCalendar);
		}

	}

	@Override
	public EmployeeCalendarDefForm getDataForEmpCalConfig(Long empCalConfigId) {
		EmployeeCalendarDefForm calendarDefForm = new EmployeeCalendarDefForm();

		EmployeeCalendarConfig empCalendarVO = employeeCalendarConfigDAO
				.findByID(empCalConfigId);
		if (empCalendarVO != null) {
			calendarDefForm.setCalendarTemplateName(empCalendarVO
					.getCompanyCalendarTemplate().getTemplateName());
			calendarDefForm.setEmployeeName(getEmployeeName(empCalendarVO
					.getEmployee()));
			calendarDefForm.setEmployeeNumber(empCalendarVO.getEmployee()
					.getEmployeeNumber());
			calendarDefForm.setStartDate(DateUtils
					.timeStampToString(empCalendarVO.getStartDate()));
			if (empCalendarVO.getEndDate() != null) {
				calendarDefForm.setEndDate(DateUtils
						.timeStampToString(empCalendarVO.getEndDate()));
			}

			calendarDefForm.setCalendarTemplateId(empCalendarVO
					.getEmployeeCalendarConfigId());
		}
		return calendarDefForm;
	}

	@Override
	public String editEmpCalConfig(
			EmployeeCalendarDefForm employeeCalendarDefForm, Long companyId) {
		Boolean status = true;
		Company companyVO = companyDAO.findById(companyId);

		EmployeeCalendarConfig empCalendarConfig = employeeCalendarConfigDAO
				.findByID(employeeCalendarDefForm.getCalendarTemplateId());

		EmployeeCalendarConfig empCalendarByFromDateVO = employeeCalendarConfigDAO
				.checkEmpCalTempByDate(
						employeeCalendarDefForm.getCalendarTemplateId(),
						empCalendarConfig.getEmployee().getEmployeeId(),
						employeeCalendarDefForm.getStartDate(),
						companyVO.getDateFormat());
		if (empCalendarByFromDateVO != null) {
			status = false;
		}
		EmployeeCalendarConfig empCalendarByToDateVO = employeeCalendarConfigDAO
				.checkEmpCalTempByDate(
						employeeCalendarDefForm.getCalendarTemplateId(),
						empCalendarConfig.getEmployee().getEmployeeId(),
						employeeCalendarDefForm.getEndDate(),
						companyVO.getDateFormat());
		if (empCalendarByToDateVO != null) {
			status = false;
		}
		if (status) {
			empCalendarConfig.setStartDate(DateUtils.stringToTimestamp(
					employeeCalendarDefForm.getStartDate(),
					companyVO.getDateFormat()));
			if (StringUtils.isNotBlank(employeeCalendarDefForm.getEndDate())) {
				empCalendarConfig.setEndDate(DateUtils.stringToTimestamp(
						employeeCalendarDefForm.getEndDate(),
						companyVO.getDateFormat()));
			} else {
				empCalendarConfig.setEndDate(null);
			}

			employeeCalendarConfigDAO.update(empCalendarConfig);
			return "success";
		} else {
			return "overlap";
		}
	}

	@Override
	public EmployeeListFormPage fetchEmpsForCalendarTemplate(
			String searchCondition, String searchText, PageRequest pageDTO,
			SortCondition sortDTO, Long companyId, Long employeeId,
			Long calendartemplateId, String startDate) {

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
		Company companyVO = companyDAO.findById(companyId);
		empList = employeeDAO.findUnAssignEmployeesCalendarTemplate(
				conditionDTO, pageDTO, sortDTO, companyId, calendartemplateId,
				startDate, companyVO.getDateFormat());
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
}
