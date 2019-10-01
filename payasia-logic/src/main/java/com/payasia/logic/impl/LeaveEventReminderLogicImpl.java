package com.payasia.logic.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.payasia.common.dto.AppCodeDTO;
import com.payasia.common.dto.LeaveEventReminderConditionDTO;
import com.payasia.common.dto.LeaveEventReminderDTO;
import com.payasia.common.dto.LeaveTypeDTO;
import com.payasia.common.dto.MailTemplateDTO;
import com.payasia.common.dto.ReminderEventDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.LeaveEventReminderForm;
import com.payasia.common.form.PageRequest;
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
import com.payasia.dao.bean.LeaveScheme;
import com.payasia.dao.bean.LeaveTypeMaster;
import com.payasia.dao.bean.ReminderEventConfig;
import com.payasia.dao.bean.ReminderEventMaster;
import com.payasia.logic.LeaveEventReminderLogic;

@Component
public class LeaveEventReminderLogicImpl implements LeaveEventReminderLogic {

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
	LeaveSchemeDAO leaveSchemeDAO;

	@Resource
	ReminderEventConfigDAO reminderEventConfigDAO;

	@Resource
	EmployeeDAO employeeDAO;

	@Override
	public List<ReminderEventDTO> getLeaveReminderEvents() {
		List<ReminderEventDTO> reminderEventDTOs = new ArrayList<>();
		List<ReminderEventMaster> reminderEventMasterVOs = reminderEventMasterDAO
				.getAllReminderEvents(PayAsiaConstants.LEAVE_Event_Reminder_MODULE_LEAVE);
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
	public List<AppCodeDTO> getRecepientTypes() {
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
	public List<LeaveTypeDTO> getLeaveTypes(Long leaveSchemeId, Long companyId) {
		List<LeaveTypeDTO> leaveTypeDTOs = new ArrayList<>();
		List<LeaveTypeMaster> leaveTypeMasterVOs;

		if (leaveSchemeId == null) {
			leaveTypeMasterVOs = leaveTypeMasterDAO.getAllLeaveTypes(companyId);

		} else {
			leaveTypeMasterVOs = leaveTypeMasterDAO
					.findByConditionAndVisibility(leaveSchemeId, companyId);
		}

		for (LeaveTypeMaster leaveTypeMaster : leaveTypeMasterVOs) {
			LeaveTypeDTO leaveTypeDTO = new LeaveTypeDTO();
			leaveTypeDTO.setLeaveTypeId(leaveTypeMaster.getLeaveTypeId());
			leaveTypeDTO.setLeaveTypeName(leaveTypeMaster.getLeaveTypeName());
			leaveTypeDTOs.add(leaveTypeDTO);
		}
		return leaveTypeDTOs;
	}

	@Override
	public List<MailTemplateDTO> getMailTemplates(Long companyId) {
		List<MailTemplateDTO> mailTemplateDTOs = new ArrayList<>();
		List<EmailTemplate> emailTemplates = emailTemplateDAO
				.findByConditionCompanyAndSubCategoryName(companyId,
						PayAsiaConstants.LEAVE_Event_Reminder_SUB_CATEGORY_NAME);
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
	public void saveLeaveEventReminder(
			LeaveEventReminderForm leaveEventReminderForm, Long companyId) {

		ReminderEventConfig reminderEventConfig = new ReminderEventConfig();
		Company company = companyDAO.findById(companyId);
		reminderEventConfig.setCompany(company);
		ReminderEventMaster reminderEventMaster = reminderEventMasterDAO
				.findById(leaveEventReminderForm.getLeaveEventId());
		reminderEventConfig.setReminderEventMaster(reminderEventMaster);
		if (leaveEventReminderForm.getLeaveSchemeId() != null) {
			LeaveScheme leaveScheme = leaveSchemeDAO
					.findSchemeByCompanyID(leaveEventReminderForm.getLeaveSchemeId(), companyId);
			if(leaveScheme == null)
			{
				throw new PayAsiaSystemException("Authentication Exception");
			}
			reminderEventConfig.setLeaveScheme(leaveScheme);
		}
		if (leaveEventReminderForm.getLeaveSchemeTypeId() != null) {
			LeaveTypeMaster leaveTypeMaster = leaveTypeMasterDAO
					.findById(leaveEventReminderForm.getLeaveSchemeTypeId());
			reminderEventConfig.setLeaveTypeMaster(leaveTypeMaster);
		}

		AppCodeMaster recepAppcode = appCodeMasterDAO
				.findById(leaveEventReminderForm.getRecipeintTypeID());
		reminderEventConfig.setRecipientType(recepAppcode);
		if (StringUtils.isNotBlank(leaveEventReminderForm.getRecepValue())) {

			reminderEventConfig.setRecipientValue(leaveEventReminderForm
					.getRecepValue());
		}
		reminderEventConfig
				.setAllowSendMailBeforeEventDay(leaveEventReminderForm
						.getAllowSendMailBeforeEventDay());
		if (leaveEventReminderForm.getAllowSendMailBeforeEventDay() == true) {

			if (leaveEventReminderForm.getSendMailBeforeDays() != null) {
				reminderEventConfig
						.setSendMailBeforeDays(leaveEventReminderForm
								.getSendMailBeforeDays());
			}
			if (leaveEventReminderForm.getSendMailBeforeRepeatDays() != null) {
				reminderEventConfig
						.setSendMailBeforeRepeatDays(leaveEventReminderForm
								.getSendMailBeforeRepeatDays());

			}
			if (leaveEventReminderForm.getSendMailBeforeMailTemplate() != null) {
				EmailTemplate emplateBeforeMailTemplate = emailTemplateDAO
						.findById(leaveEventReminderForm
								.getSendMailBeforeMailTemplate());
				reminderEventConfig
						.setSendMailBeforeMailTemplate(emplateBeforeMailTemplate);
			}
		}
		reminderEventConfig.setAllowSendMailOnEventDay(leaveEventReminderForm
				.getAllowSendMailOnEventDay());
		if (leaveEventReminderForm.getAllowSendMailOnEventDay() == true) {
			if (leaveEventReminderForm.getSendMailOnEventMailTemplate() != null) {
				EmailTemplate emplateOnMailTemplate = emailTemplateDAO
						.findById(leaveEventReminderForm
								.getSendMailOnEventMailTemplate());
				reminderEventConfig
						.setSendMailOnEventMailTemplate(emplateOnMailTemplate);
			}

		}
		reminderEventConfig
				.setAllowSendMailAfterEventDay(leaveEventReminderForm
						.getAllowSendMailAfterEventDay());
		if (leaveEventReminderForm.getAllowSendMailAfterEventDay() == true) {
			if (leaveEventReminderForm.getSendMailAfterDays() != null) {

				reminderEventConfig.setSendMailAfterDays(leaveEventReminderForm
						.getSendMailAfterDays());

			}
			if (leaveEventReminderForm.getSendMailAfterRepeatDays() != null) {
				reminderEventConfig
						.setSendMailAfterRepeatDays(leaveEventReminderForm
								.getSendMailAfterRepeatDays());
			}
			if (leaveEventReminderForm.getSendMailAfterTillDays() != null) {
				reminderEventConfig
						.setSendMailAfterTillDays(leaveEventReminderForm
								.getSendMailAfterTillDays());
			}
			if (leaveEventReminderForm.getSendMailAfterMailTemplate() != null) {
				EmailTemplate emplateAfterMailTemplate = emailTemplateDAO
						.findById(leaveEventReminderForm
								.getSendMailAfterMailTemplate());
				reminderEventConfig
						.setSendMailAfterMailTemplate(emplateAfterMailTemplate);
			}

		}

		reminderEventConfigDAO.save(reminderEventConfig);
	}

	@Override
	public LeaveEventReminderForm getEventReminders(Long companyId,
			PageRequest pageDTO, String searchType, Long searchValue) {

		LeaveEventReminderForm leaveEventReminderForm = new LeaveEventReminderForm();
		LeaveEventReminderConditionDTO leaveEventReminderConditionDTO = new LeaveEventReminderConditionDTO();
		leaveEventReminderConditionDTO.setCompanyId(companyId);

		if (StringUtils.isNotBlank(searchType)
				&& searchType
						.equalsIgnoreCase(PayAsiaConstants.LEAVE_Event_Reminder_TYPE_EVENT)) {
			leaveEventReminderConditionDTO.setSearchType(searchType);
			leaveEventReminderConditionDTO.setSearchTypeId(searchValue);

		} else if (StringUtils.isNotBlank(searchType)
				&& searchType
						.equalsIgnoreCase(PayAsiaConstants.LEAVE_Event_Reminder_TYPE_LEAVE_TYPE)) {
			leaveEventReminderConditionDTO.setSearchType(searchType);
			leaveEventReminderConditionDTO.setSearchTypeId(searchValue);

		} else if (StringUtils.isNotBlank(searchType)
				&& searchType
						.equalsIgnoreCase(PayAsiaConstants.LEAVE_Event_Reminder_TYPE_LEAVE_SCHEME)) {
			leaveEventReminderConditionDTO.setSearchType(searchType);
			leaveEventReminderConditionDTO.setSearchTypeId(searchValue);

		} else if (StringUtils.isNotBlank(searchType)
				&& searchType
						.equalsIgnoreCase(PayAsiaConstants.LEAVE_Event_Reminder_TYPE_LEAVE_RECEP)) {
			leaveEventReminderConditionDTO.setSearchType(searchType);
			leaveEventReminderConditionDTO.setSearchTypeId(searchValue);

		}

		List<ReminderEventConfig> reminderEventConfigs = reminderEventConfigDAO
				.findByCondition(leaveEventReminderConditionDTO, pageDTO);
		Integer recordSize = reminderEventConfigDAO
				.getCountForCondition(leaveEventReminderConditionDTO);
		List<LeaveEventReminderDTO> leaveEventReminderDTOs = new ArrayList<>();

		for (ReminderEventConfig reminderEventConfig : reminderEventConfigs) {
			LeaveEventReminderDTO leaveEventReminderDTO = new LeaveEventReminderDTO();
			/* ID ENCRYPT*/
			leaveEventReminderDTO.setReminderEventConfigId(FormatPreserveCryptoUtil.encrypt(reminderEventConfig
					.getReminderEventConfigId()));
			leaveEventReminderDTO.setRecepientName(reminderEventConfig
					.getRecipientType().getCodeDesc());
			leaveEventReminderDTO.setReminderEventName(reminderEventConfig
					.getReminderEventMaster().getEventDesc());
			if (reminderEventConfig.getLeaveScheme() != null) {
				leaveEventReminderDTO.setLeaveSchemeName(reminderEventConfig
						.getLeaveScheme().getSchemeName());
			}
			if (reminderEventConfig.getLeaveTypeMaster() != null) {
				leaveEventReminderDTO.setLeaveTypeName(reminderEventConfig
						.getLeaveTypeMaster().getLeaveTypeName());
			}

			leaveEventReminderDTOs.add(leaveEventReminderDTO);
		}

		leaveEventReminderForm
				.setLeaveEventReminderDTOs(leaveEventReminderDTOs);

		if (pageDTO != null) {

			int pageSize = pageDTO.getPageSize();
			int totalPages = recordSize / pageSize;

			if (recordSize % pageSize != 0) {
				totalPages = totalPages + 1;
			}
			if (recordSize == 0) {
				pageDTO.setPageNumber(0);
			}

			leaveEventReminderForm.setPage(pageDTO.getPageNumber());
			leaveEventReminderForm.setTotal(totalPages);
			leaveEventReminderForm.setRecords(recordSize);
		}
		return leaveEventReminderForm;
	}

	@Override
	public LeaveEventReminderForm getLeaveEventReminderData(Long companyId,
			Long eventReminderConfigId) {
		LeaveEventReminderForm leaveEventReminderForm = new LeaveEventReminderForm();
		ReminderEventConfig reminderEventConfig = reminderEventConfigDAO
				.findEventReminderConfigByCompanyId(eventReminderConfigId, companyId);

		if (reminderEventConfig.getLeaveScheme() != null) {
			leaveEventReminderForm.setLeaveSchemeId(reminderEventConfig
					.getLeaveScheme().getLeaveSchemeId());
		}
		if (reminderEventConfig.getLeaveTypeMaster() != null) {
			leaveEventReminderForm.setLeaveSchemeTypeId(reminderEventConfig
					.getLeaveTypeMaster().getLeaveTypeId());
		}
		leaveEventReminderForm.setLeaveEventId(reminderEventConfig
				.getReminderEventMaster().getReminderEventId());
		leaveEventReminderForm.setRecepientId(reminderEventConfig
				.getRecipientType().getAppCodeID());
		leaveEventReminderForm
				.setAllowSendMailAfterEventDay(reminderEventConfig
						.isAllowSendMailAfterEventDay());
		leaveEventReminderForm
				.setAllowSendMailBeforeEventDay(reminderEventConfig
						.isAllowSendMailBeforeEventDay());
		leaveEventReminderForm.setAllowSendMailOnEventDay(reminderEventConfig
				.isAllowSendMailOnEventDay());
		if (reminderEventConfig.getSendMailAfterDays() != null) {
			leaveEventReminderForm.setSendMailAfterDays(reminderEventConfig
					.getSendMailAfterDays());

		}
		if (reminderEventConfig.getSendMailAfterRepeatDays() != null) {
			leaveEventReminderForm
					.setSendMailAfterRepeatDays(reminderEventConfig
							.getSendMailAfterRepeatDays());
		}
		if (reminderEventConfig.getSendMailAfterTillDays() != null) {
			leaveEventReminderForm.setSendMailAfterTillDays(reminderEventConfig
					.getSendMailAfterTillDays());
		}

		if (reminderEventConfig.getSendMailAfterMailTemplate() != null) {
			leaveEventReminderForm
					.setSendMailAfterMailTemplate(reminderEventConfig
							.getSendMailAfterMailTemplate()
							.getEmailTemplateId());
		}
		if (reminderEventConfig.getSendMailBeforeDays() != null) {
			leaveEventReminderForm.setSendMailBeforeDays(reminderEventConfig
					.getSendMailBeforeDays());
		}
		if (reminderEventConfig.getSendMailBeforeMailTemplate() != null) {
			leaveEventReminderForm
					.setSendMailBeforeMailTemplate(reminderEventConfig
							.getSendMailBeforeMailTemplate()
							.getEmailTemplateId());
		}
		if (reminderEventConfig.getSendMailBeforeRepeatDays() != null) {
			leaveEventReminderForm
					.setSendMailBeforeRepeatDays(reminderEventConfig
							.getSendMailBeforeRepeatDays());
		}
		if (reminderEventConfig.getSendMailOnEventMailTemplate() != null) {

			leaveEventReminderForm
					.setSendMailOnEventMailTemplate(reminderEventConfig
							.getSendMailOnEventMailTemplate()
							.getEmailTemplateId());
		}
		if (reminderEventConfig
				.getRecipientType()
				.getCodeDesc()
				.equalsIgnoreCase(
						PayAsiaConstants.LEAVE_Event_Reminder_RECEPIENT_TYPE_EMPLOYEE_USER)) {
			if (StringUtils.isNotBlank(reminderEventConfig.getRecipientValue())) {
				Employee emp = employeeDAO.findByNumber(
						reminderEventConfig.getRecipientValue(), companyId);
				if (emp != null) {

					leaveEventReminderForm.setEmployeeName(emp.getFirstName());
					leaveEventReminderForm.setEmployeeNumber(emp
							.getEmployeeNumber());
				}
			}

		}
		if (reminderEventConfig
				.getRecipientType()
				.getCodeDesc()
				.equalsIgnoreCase(
						PayAsiaConstants.LEAVE_Event_Reminder_RECEPIENT_TYPE_EXTERNAL_ENTITY)) {
			if (StringUtils.isNotBlank(reminderEventConfig.getRecipientValue())) {
				leaveEventReminderForm.setRecepValue(reminderEventConfig
						.getRecipientValue());
			}
		}

		return leaveEventReminderForm;
	}

	@Override
	public void updateLeaveEventReminder(
			LeaveEventReminderForm leaveEventReminderForm,
			Long eventReminderConfigId, Long companyId) {
		ReminderEventConfig reminderEventConfig = reminderEventConfigDAO
				.findEventReminderConfigByCompanyId(eventReminderConfigId, companyId);
		if(reminderEventConfig == null)
		{
			throw new PayAsiaSystemException("Authentication Exception");
		}
		Company company = companyDAO.findById(companyId);
		reminderEventConfig.setCompany(company);
		ReminderEventMaster reminderEventMaster = reminderEventMasterDAO
				.findById(leaveEventReminderForm.getLeaveEventId());
		reminderEventConfig.setReminderEventMaster(reminderEventMaster);
		if (leaveEventReminderForm.getLeaveSchemeId() != null) {
			LeaveScheme leaveScheme = leaveSchemeDAO
					.findByID(leaveEventReminderForm.getLeaveSchemeId());
			reminderEventConfig.setLeaveScheme(leaveScheme);
		} else {
			reminderEventConfig.setLeaveScheme(null);
		}
		if (leaveEventReminderForm.getLeaveSchemeTypeId() != null) {
			LeaveTypeMaster leaveTypeMaster = leaveTypeMasterDAO
					.findById(leaveEventReminderForm.getLeaveSchemeTypeId());
			reminderEventConfig.setLeaveTypeMaster(leaveTypeMaster);
		} else {
			reminderEventConfig.setLeaveTypeMaster(null);
		}

		AppCodeMaster recepAppcode = appCodeMasterDAO
				.findById(leaveEventReminderForm.getRecipeintTypeID());
		reminderEventConfig.setRecipientType(recepAppcode);
		if (StringUtils.isNotBlank(leaveEventReminderForm.getRecepValue())) {

			reminderEventConfig.setRecipientValue(leaveEventReminderForm
					.getRecepValue());
		}
		reminderEventConfig
				.setAllowSendMailBeforeEventDay(leaveEventReminderForm
						.getAllowSendMailBeforeEventDay());
		if (leaveEventReminderForm.getAllowSendMailBeforeEventDay() == true) {

			if (leaveEventReminderForm.getSendMailBeforeDays() != null) {
				reminderEventConfig
						.setSendMailBeforeDays(leaveEventReminderForm
								.getSendMailBeforeDays());
			}
			if (leaveEventReminderForm.getSendMailBeforeRepeatDays() != null) {
				reminderEventConfig
						.setSendMailBeforeRepeatDays(leaveEventReminderForm
								.getSendMailBeforeRepeatDays());

			}
			if (leaveEventReminderForm.getSendMailBeforeMailTemplate() != null) {
				EmailTemplate emplateBeforeMailTemplate = emailTemplateDAO
						.findById(leaveEventReminderForm
								.getSendMailBeforeMailTemplate());
				reminderEventConfig
						.setSendMailBeforeMailTemplate(emplateBeforeMailTemplate);
			}
		}
		reminderEventConfig.setAllowSendMailOnEventDay(leaveEventReminderForm
				.getAllowSendMailOnEventDay());
		if (leaveEventReminderForm.getAllowSendMailOnEventDay() == true) {
			if (leaveEventReminderForm.getSendMailOnEventMailTemplate() != null) {
				EmailTemplate emplateOnMailTemplate = emailTemplateDAO
						.findById(leaveEventReminderForm
								.getSendMailOnEventMailTemplate());
				reminderEventConfig
						.setSendMailOnEventMailTemplate(emplateOnMailTemplate);
			}

		}
		reminderEventConfig
				.setAllowSendMailAfterEventDay(leaveEventReminderForm
						.getAllowSendMailAfterEventDay());
		if (leaveEventReminderForm.getAllowSendMailAfterEventDay() == true) {
			if (leaveEventReminderForm.getSendMailAfterDays() != null) {

				reminderEventConfig.setSendMailAfterDays(leaveEventReminderForm
						.getSendMailAfterDays());

			}
			if (leaveEventReminderForm.getSendMailAfterRepeatDays() != null) {
				reminderEventConfig
						.setSendMailAfterRepeatDays(leaveEventReminderForm
								.getSendMailAfterRepeatDays());
			}
			if (leaveEventReminderForm.getSendMailAfterTillDays() != null) {
				reminderEventConfig
						.setSendMailAfterTillDays(leaveEventReminderForm
								.getSendMailAfterTillDays());
			}
			if (leaveEventReminderForm.getSendMailAfterMailTemplate() != null) {
				EmailTemplate emplateAfterMailTemplate = emailTemplateDAO
						.findById(leaveEventReminderForm
								.getSendMailAfterMailTemplate());
				reminderEventConfig
						.setSendMailAfterMailTemplate(emplateAfterMailTemplate);
			}

		}

		reminderEventConfigDAO.update(reminderEventConfig);

	}

	@Override
	public void deleteLeaveEventReminder(Long eventReminderConfigId,
			Long companyId) {
		/*ReminderEventConfig reminderEventConfig = reminderEventConfigDAO
				.findById(eventReminderConfigId);*/
		
		ReminderEventConfig reminderEventConfig = reminderEventConfigDAO.findEventReminderConfigByCompanyId(eventReminderConfigId, companyId);
		reminderEventConfigDAO.delete(reminderEventConfig);

	}
}
