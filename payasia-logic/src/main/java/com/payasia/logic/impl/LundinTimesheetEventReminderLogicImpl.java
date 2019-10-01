package com.payasia.logic.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.payasia.common.dto.AppCodeDTO;
import com.payasia.common.dto.EmployeeConditionDTO;
import com.payasia.common.dto.EmployeeShortListDTO;
import com.payasia.common.dto.LeaveEventReminderConditionDTO;
import com.payasia.common.dto.LundinTimesheetEventReminderDTO;
import com.payasia.common.dto.MailTemplateDTO;
import com.payasia.common.dto.ReminderEventDTO;
import com.payasia.common.dto.ResponseObjectDTO;
import com.payasia.common.form.EmployeeListForm;
import com.payasia.common.form.LeaveReviewerResponseForm;
import com.payasia.common.form.LundinTimesheetEventReminderForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.dao.AppCodeMasterDAO;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.EmailTemplateDAO;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.LeaveSchemeDAO;
import com.payasia.dao.LeaveTypeMasterDAO;
import com.payasia.dao.ReminderEventConfigDAO;
import com.payasia.dao.ReminderEventMasterDAO;
import com.payasia.dao.bean.AppCodeMaster;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.EmailTemplate;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.ReminderEventConfig;
import com.payasia.dao.bean.ReminderEventMaster;
import com.payasia.logic.GeneralLogic;
import com.payasia.logic.LundinTimesheetEventReminderLogic;

@Component
public class LundinTimesheetEventReminderLogicImpl implements
		LundinTimesheetEventReminderLogic {

	private static final Logger LOGGER = Logger
			.getLogger(LundinTimesheetEventReminderLogicImpl.class);

	@Resource
	ReminderEventMasterDAO reminderEventMasterDAO;

	@Resource
	AppCodeMasterDAO appCodeMasterDAO;

	@Resource
	LeaveTypeMasterDAO leaveTypeMasterDAO;

	@Resource
	EmailTemplateDAO emailTemplateDAO;

	@Resource
	CompanyDAO companyDAO;

	@Resource
	GeneralLogic generalLogic;

	@Resource
	LeaveSchemeDAO leaveSchemeDAO;

	@Resource
	ReminderEventConfigDAO reminderEventConfigDAO;

	@Resource
	EmployeeDAO employeeDAO;

	@Override
	public List<ReminderEventDTO> getTimesheetReminderEvents() {
		List<ReminderEventDTO> reminderEventDTOs = new ArrayList<>();
		List<ReminderEventMaster> reminderEventMasterVOs = reminderEventMasterDAO
				.getAllReminderEvents(PayAsiaConstants.LUNDIN_Event_Reminder_MODULE_TIMESHEET);
		for (ReminderEventMaster reminderEventMaster : reminderEventMasterVOs) {
			ReminderEventDTO reminderEventDTO = new ReminderEventDTO();
			reminderEventDTO.setReminderEventId(reminderEventMaster
					.getReminderEventId());
			reminderEventDTO.setEventName(reminderEventMaster.getEvent());
			reminderEventDTOs.add(reminderEventDTO);
		}
		return reminderEventDTOs;
	}

	@Override
	public List<AppCodeDTO> getTimesheetRecepientTypes() {
		List<AppCodeMaster> appCodeList = appCodeMasterDAO
				.findByCondition(PayAsiaConstants.APP_CODE_REMINDER_MAIL_RECIEPT_TYPE);
		List<AppCodeDTO> appCodeDTOs = new ArrayList<>();
		for (AppCodeMaster appCodeMaster : appCodeList) {
			AppCodeDTO appCodeDTO = new AppCodeDTO();
			appCodeDTO.setAppCodeID(appCodeMaster.getAppCodeID());
			appCodeDTO.setCodeDesc(appCodeMaster.getCodeDesc());
			appCodeDTOs.add(appCodeDTO);
		}

		return appCodeDTOs;
	}

	@Override
	public List<MailTemplateDTO> getMailTemplates(Long companyId) {
		List<MailTemplateDTO> mailTemplateDTOs = new ArrayList<>();
		List<EmailTemplate> emailTemplates = emailTemplateDAO
				.findByConditionCompanyAndSubCategoryName(
						companyId,
						PayAsiaConstants.TIMESHEET_Event_Reminder_SUB_CATEGORY_NAME);
		for (EmailTemplate emailTemplate : emailTemplates) {
			MailTemplateDTO mailTemplateDTO = new MailTemplateDTO();
			mailTemplateDTO.setMailTemplateId(emailTemplate
					.getEmailTemplateId());
			mailTemplateDTO.setMailTemplateName(emailTemplate.getName());
			mailTemplateDTOs.add(mailTemplateDTO);
		}

		return mailTemplateDTOs;
	}

	@Override
	public ResponseObjectDTO saveTimesheetEventReminder(
			LundinTimesheetEventReminderForm timesheetEventReminderForm,
			Long companyId) {

		ResponseObjectDTO responseDto = new ResponseObjectDTO();

		try {

			ReminderEventConfig reminderEventConfig = new ReminderEventConfig();
			Company company = companyDAO.findById(companyId);
			reminderEventConfig.setCompany(company);
			ReminderEventMaster reminderEventMaster = reminderEventMasterDAO
					.findById(timesheetEventReminderForm.getTimesheetEventId());
			reminderEventConfig.setReminderEventMaster(reminderEventMaster);

			AppCodeMaster recepAppcode = appCodeMasterDAO
					.findById(timesheetEventReminderForm.getRecipeintTypeID());
			reminderEventConfig.setRecipientType(recepAppcode);
			if (StringUtils.isNotBlank(timesheetEventReminderForm
					.getRecepValue())) {

				reminderEventConfig
						.setRecipientValue(timesheetEventReminderForm
								.getRecepValue());
			}
			reminderEventConfig
					.setAllowSendMailBeforeEventDay(timesheetEventReminderForm
							.getAllowSendMailBeforeEventDay());
			if (timesheetEventReminderForm.getAllowSendMailBeforeEventDay() == true) {

				if (timesheetEventReminderForm.getSendMailBeforeDays() != null) {
					reminderEventConfig
							.setSendMailBeforeDays(timesheetEventReminderForm
									.getSendMailBeforeDays());
				}
				if (timesheetEventReminderForm.getSendMailBeforeRepeatDays() != null) {
					reminderEventConfig
							.setSendMailBeforeRepeatDays(timesheetEventReminderForm
									.getSendMailBeforeRepeatDays());

				}
				if (timesheetEventReminderForm.getSendMailBeforeMailTemplate() != null) {
					EmailTemplate emplateBeforeMailTemplate = emailTemplateDAO
							.findById(timesheetEventReminderForm
									.getSendMailBeforeMailTemplate());
					reminderEventConfig
							.setSendMailBeforeMailTemplate(emplateBeforeMailTemplate);
				}
			}
			reminderEventConfig
					.setAllowSendMailOnEventDay(timesheetEventReminderForm
							.getAllowSendMailOnEventDay());
			if (timesheetEventReminderForm.getAllowSendMailOnEventDay() == true) {
				if (timesheetEventReminderForm.getSendMailOnEventMailTemplate() != null) {
					EmailTemplate emplateOnMailTemplate = emailTemplateDAO
							.findById(timesheetEventReminderForm
									.getSendMailOnEventMailTemplate());
					reminderEventConfig
							.setSendMailOnEventMailTemplate(emplateOnMailTemplate);
				}

			}
			reminderEventConfig
					.setAllowSendMailAfterEventDay(timesheetEventReminderForm
							.getAllowSendMailAfterEventDay());
			if (timesheetEventReminderForm.getAllowSendMailAfterEventDay() == true) {
				if (timesheetEventReminderForm.getSendMailAfterDays() != null) {

					reminderEventConfig
							.setSendMailAfterDays(timesheetEventReminderForm
									.getSendMailAfterDays());

				}
				if (timesheetEventReminderForm.getSendMailAfterRepeatDays() != null) {
					reminderEventConfig
							.setSendMailAfterRepeatDays(timesheetEventReminderForm
									.getSendMailAfterRepeatDays());
				}
				if (timesheetEventReminderForm.getSendMailAfterTillDays() != null) {
					reminderEventConfig
							.setSendMailAfterTillDays(timesheetEventReminderForm
									.getSendMailAfterTillDays());
				}
				if (timesheetEventReminderForm.getSendMailAfterMailTemplate() != null) {
					EmailTemplate emplateAfterMailTemplate = emailTemplateDAO
							.findById(timesheetEventReminderForm
									.getSendMailAfterMailTemplate());
					reminderEventConfig
							.setSendMailAfterMailTemplate(emplateAfterMailTemplate);
				}
			}
			reminderEventConfigDAO.save(reminderEventConfig);
			responseDto.setKey(1);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			responseDto.setKey(2);
			return responseDto;
		}
		return responseDto;
	}

	@Override
	public ResponseObjectDTO updateTimesheetEventReminder(
			LundinTimesheetEventReminderForm timesheetEventReminderForm,
			Long eventReminderConfigId, Long companyId) {

		ResponseObjectDTO responseDto = new ResponseObjectDTO();
		try {

			ReminderEventConfig reminderEventConfig = reminderEventConfigDAO
					.findById(eventReminderConfigId);
			Company company = companyDAO.findById(companyId);
			reminderEventConfig.setCompany(company);
			ReminderEventMaster reminderEventMaster = reminderEventMasterDAO
					.findById(timesheetEventReminderForm.getTimesheetEventId());
			reminderEventConfig.setReminderEventMaster(reminderEventMaster);

			AppCodeMaster recepAppcode = appCodeMasterDAO
					.findById(timesheetEventReminderForm.getRecipeintTypeID());
			reminderEventConfig.setRecipientType(recepAppcode);
			if (StringUtils.isNotBlank(timesheetEventReminderForm
					.getRecepValue())) {

				reminderEventConfig
						.setRecipientValue(timesheetEventReminderForm
								.getRecepValue());
			}
			reminderEventConfig
					.setAllowSendMailBeforeEventDay(timesheetEventReminderForm
							.getAllowSendMailBeforeEventDay());
			if (timesheetEventReminderForm.getAllowSendMailBeforeEventDay() == true) {

				if (timesheetEventReminderForm.getSendMailBeforeDays() != null) {
					reminderEventConfig
							.setSendMailBeforeDays(timesheetEventReminderForm
									.getSendMailBeforeDays());
				}
				if (timesheetEventReminderForm.getSendMailBeforeRepeatDays() != null) {
					reminderEventConfig
							.setSendMailBeforeRepeatDays(timesheetEventReminderForm
									.getSendMailBeforeRepeatDays());

				}
				if (timesheetEventReminderForm.getSendMailBeforeMailTemplate() != null) {
					EmailTemplate emplateBeforeMailTemplate = emailTemplateDAO
							.findById(timesheetEventReminderForm
									.getSendMailBeforeMailTemplate());
					reminderEventConfig
							.setSendMailBeforeMailTemplate(emplateBeforeMailTemplate);
				}
			}
			reminderEventConfig
					.setAllowSendMailOnEventDay(timesheetEventReminderForm
							.getAllowSendMailOnEventDay());
			if (timesheetEventReminderForm.getAllowSendMailOnEventDay() == true) {
				if (timesheetEventReminderForm.getSendMailOnEventMailTemplate() != null) {
					EmailTemplate emplateOnMailTemplate = emailTemplateDAO
							.findById(timesheetEventReminderForm
									.getSendMailOnEventMailTemplate());
					reminderEventConfig
							.setSendMailOnEventMailTemplate(emplateOnMailTemplate);
				}

			}
			reminderEventConfig
					.setAllowSendMailAfterEventDay(timesheetEventReminderForm
							.getAllowSendMailAfterEventDay());
			if (timesheetEventReminderForm.getAllowSendMailAfterEventDay() == true) {
				if (timesheetEventReminderForm.getSendMailAfterDays() != null) {

					reminderEventConfig
							.setSendMailAfterDays(timesheetEventReminderForm
									.getSendMailAfterDays());

				}
				if (timesheetEventReminderForm.getSendMailAfterRepeatDays() != null) {
					reminderEventConfig
							.setSendMailAfterRepeatDays(timesheetEventReminderForm
									.getSendMailAfterRepeatDays());
				}
				if (timesheetEventReminderForm.getSendMailAfterTillDays() != null) {
					reminderEventConfig
							.setSendMailAfterTillDays(timesheetEventReminderForm
									.getSendMailAfterTillDays());
				}
				if (timesheetEventReminderForm.getSendMailAfterMailTemplate() != null) {
					EmailTemplate emplateAfterMailTemplate = emailTemplateDAO
							.findById(timesheetEventReminderForm
									.getSendMailAfterMailTemplate());
					reminderEventConfig
							.setSendMailAfterMailTemplate(emplateAfterMailTemplate);
				}
			}
			reminderEventConfigDAO.update(reminderEventConfig);
			responseDto.setKey(1);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			responseDto.setKey(2);
			return responseDto;
		}
		return responseDto;
	}

	@Override
	public ResponseObjectDTO deleteTimesheetEventReminder(
			Long eventReminderConfigId, Long companyId) {

		ResponseObjectDTO responseDto = new ResponseObjectDTO();
		try {
			ReminderEventConfig reminderEventConfig = reminderEventConfigDAO
					.findEventReminderConfigByCompanyId(eventReminderConfigId,companyId);
			
			if (reminderEventConfig!=null) {
				
			reminderEventConfigDAO.delete(reminderEventConfig);
			responseDto.setKey(1);
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			responseDto.setKey(2);
			return responseDto;
		}
		return responseDto;
	}

	@Override
	public LundinTimesheetEventReminderForm getTimesheetEventReminders(
			Long companyId, PageRequest pageDTO, String searchType,
			Long searchValue) {

		LundinTimesheetEventReminderForm timesheetEventReminderForm = new LundinTimesheetEventReminderForm();
		LeaveEventReminderConditionDTO eventReminderConditionDTO = new LeaveEventReminderConditionDTO();
		eventReminderConditionDTO.setCompanyId(companyId);

		if (StringUtils.isNotBlank(searchType)
				&& searchType
						.equalsIgnoreCase(PayAsiaConstants.TIMESHEET_Event_Reminder_TYPE_EVENT)) {
			eventReminderConditionDTO.setSearchType(searchType);
			eventReminderConditionDTO.setSearchTypeId(searchValue);

		} else if (StringUtils.isNotBlank(searchType)
				&& searchType
						.equalsIgnoreCase(PayAsiaConstants.TIMESHEET_Event_Reminder_TYPE_RECEP)) {
			eventReminderConditionDTO.setSearchType(searchType);
			eventReminderConditionDTO.setSearchTypeId(searchValue);

		}

		List<ReminderEventConfig> reminderEventConfigs = reminderEventConfigDAO
				.findByCondition(eventReminderConditionDTO, pageDTO);
		Integer recordSize = reminderEventConfigDAO
				.getCountForCondition(eventReminderConditionDTO);
		List<LundinTimesheetEventReminderDTO> timesheetEventReminderDTOs = new ArrayList<>();

		for (ReminderEventConfig reminderEventConfig : reminderEventConfigs) {
			LundinTimesheetEventReminderDTO timesheetEventReminderDTO = new LundinTimesheetEventReminderDTO();
			
			/*ID ENCRYPT*/
			timesheetEventReminderDTO
					.setReminderEventConfigId(FormatPreserveCryptoUtil.encrypt(reminderEventConfig
							.getReminderEventConfigId()));
			timesheetEventReminderDTO.setRecepientName(reminderEventConfig
					.getRecipientType().getCodeDesc());
			timesheetEventReminderDTO.setReminderEventName(reminderEventConfig
					.getReminderEventMaster().getEventDesc());

			timesheetEventReminderDTOs.add(timesheetEventReminderDTO);
		}

		timesheetEventReminderForm
				.setLundinTimesheetEventReminderDTOs(timesheetEventReminderDTOs);

		if (pageDTO != null) {

			int pageSize = pageDTO.getPageSize();
			int totalPages = recordSize / pageSize;

			if (recordSize % pageSize != 0) {
				totalPages = totalPages + 1;
			}
			if (recordSize == 0) {
				pageDTO.setPageNumber(0);
			}
			timesheetEventReminderForm.setPage(pageDTO.getPageNumber());
			timesheetEventReminderForm.setTotal(totalPages);
			timesheetEventReminderForm.setRecords(recordSize);
		}
		return timesheetEventReminderForm;
	}

	@Override
	public LundinTimesheetEventReminderForm getTimesheetEventReminderData(
			Long companyId, Long eventReminderConfigId) {

		LundinTimesheetEventReminderForm timesheetEventReminderForm = new LundinTimesheetEventReminderForm();
		ReminderEventConfig reminderEventConfig = reminderEventConfigDAO
				.findEventReminderConfigByCompanyId(eventReminderConfigId,companyId);
		if (reminderEventConfig!=null) {
		timesheetEventReminderForm.setTimesheetEventId(reminderEventConfig
				.getReminderEventMaster().getReminderEventId());
		timesheetEventReminderForm.setRecepientId(reminderEventConfig
				.getRecipientType().getAppCodeID());
		timesheetEventReminderForm
				.setAllowSendMailAfterEventDay(reminderEventConfig
						.isAllowSendMailAfterEventDay());
		timesheetEventReminderForm
				.setAllowSendMailBeforeEventDay(reminderEventConfig
						.isAllowSendMailBeforeEventDay());
		timesheetEventReminderForm
				.setAllowSendMailOnEventDay(reminderEventConfig
						.isAllowSendMailOnEventDay());
		if (reminderEventConfig.getSendMailAfterDays() != null) {
			timesheetEventReminderForm.setSendMailAfterDays(reminderEventConfig
					.getSendMailAfterDays());

		}
		if (reminderEventConfig.getSendMailAfterRepeatDays() != null) {
			timesheetEventReminderForm
					.setSendMailAfterRepeatDays(reminderEventConfig
							.getSendMailAfterRepeatDays());
		}
		if (reminderEventConfig.getSendMailAfterTillDays() != null) {
			timesheetEventReminderForm
					.setSendMailAfterTillDays(reminderEventConfig
							.getSendMailAfterTillDays());
		}

		if (reminderEventConfig.getSendMailAfterMailTemplate() != null) {
			timesheetEventReminderForm
					.setSendMailAfterMailTemplate(reminderEventConfig
							.getSendMailAfterMailTemplate()
							.getEmailTemplateId());
		}
		if (reminderEventConfig.getSendMailBeforeDays() != null) {
			timesheetEventReminderForm
					.setSendMailBeforeDays(reminderEventConfig
							.getSendMailBeforeDays());
		}
		if (reminderEventConfig.getSendMailBeforeMailTemplate() != null) {
			timesheetEventReminderForm
					.setSendMailBeforeMailTemplate(reminderEventConfig
							.getSendMailBeforeMailTemplate()
							.getEmailTemplateId());
		}
		if (reminderEventConfig.getSendMailBeforeRepeatDays() != null) {
			timesheetEventReminderForm
					.setSendMailBeforeRepeatDays(reminderEventConfig
							.getSendMailBeforeRepeatDays());
		}
		if (reminderEventConfig.getSendMailOnEventMailTemplate() != null) {

			timesheetEventReminderForm
					.setSendMailOnEventMailTemplate(reminderEventConfig
							.getSendMailOnEventMailTemplate()
							.getEmailTemplateId());
		}
		if (reminderEventConfig
				.getRecipientType()
				.getCodeDesc()
				.equalsIgnoreCase(
						PayAsiaConstants.TIMESHEET_Event_Reminder_RECEPIENT_TYPE_EMPLOYEE_USER)) {
			if (StringUtils.isNotBlank(reminderEventConfig.getRecipientValue())) {
				Employee emp = employeeDAO.findByNumber(
						reminderEventConfig.getRecipientValue(), companyId);
				if (emp != null) {

					timesheetEventReminderForm.setEmployeeName(emp
							.getFirstName());
					timesheetEventReminderForm.setEmployeeNumber(emp
							.getEmployeeNumber());
				}
			}
		}
		if (reminderEventConfig
				.getRecipientType()
				.getCodeDesc()
				.equalsIgnoreCase(
						PayAsiaConstants.TIMESHEET_Event_Reminder_RECEPIENT_TYPE_EXTERNAL_ENTITY)) {
			if (StringUtils.isNotBlank(reminderEventConfig.getRecipientValue())) {
				timesheetEventReminderForm.setRecepValue(reminderEventConfig
						.getRecipientValue());
			}
		}
		}

		return timesheetEventReminderForm;
	}

	@Override
	public LeaveReviewerResponseForm searchEmployee(PageRequest pageDTO,
			SortCondition sortDTO, long employeeId, String empName,
			String empNumber, long companyId) {

		EmployeeConditionDTO conditionDTO = new EmployeeConditionDTO();
		Company companyVO = companyDAO.findById(companyId);
		if (StringUtils.isNotBlank(empName)) {
			conditionDTO.setEmployeeName("%" + empName.trim() + "%");
		}

		if (StringUtils.isNotBlank(empNumber)) {
			conditionDTO.setEmployeeNumber("%" + empNumber.trim() + "%");

		}
		EmployeeShortListDTO employeeShortListDTO = generalLogic
				.getShortListEmployeeIds(employeeId, companyId);
		conditionDTO.setEmployeeShortListDTO(employeeShortListDTO);
		conditionDTO.setGroupId(companyVO.getCompanyGroup().getGroupId());
		int recordSize = employeeDAO.findEmployeesOfGroupCompaniesCount(
				conditionDTO, pageDTO, sortDTO, companyId);

		List<Employee> finalList = employeeDAO.findEmployeesOfGroupCompanies(
				conditionDTO, pageDTO, sortDTO, companyId);

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
			employeeForm.setEmail(employee.getEmail());
			employeeForm.setCompanyName(employee.getCompany().getCompanyName());
			employeeForm.setEmployeeID(FormatPreserveCryptoUtil.encrypt(employee.getEmployeeId()));
			employeeListFormList.add(employeeForm);
		}

		LeaveReviewerResponseForm response = new LeaveReviewerResponseForm();
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
