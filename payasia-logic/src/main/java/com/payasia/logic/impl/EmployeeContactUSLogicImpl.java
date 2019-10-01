package com.payasia.logic.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.payasia.common.form.EmployeeContactUSForm;
import com.payasia.common.util.PayAsiaEmailTO;
import com.payasia.common.util.PayAsiaMailUtils;
import com.payasia.dao.EmailPreferenceMasterDAO;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.bean.EmailPreferenceMaster;
import com.payasia.dao.bean.Employee;
import com.payasia.logic.EmployeeContactUSLogic;

/**
 * The Class EmployeeContactUSLogicImpl.
 */
/**
 * @author vivekjain
 * 
 */
@Component
public class EmployeeContactUSLogicImpl implements EmployeeContactUSLogic {
	private static final Logger LOGGER = Logger
			.getLogger(EmployeeContactUSLogicImpl.class);

	/** The employee dao. */
	@Resource
	EmployeeDAO employeeDAO;

	/** The email preference master dao. */
	@Resource
	EmailPreferenceMasterDAO emailPreferenceMasterDAO;

	/** The pay asia mail utils. */
	@Resource
	PayAsiaMailUtils payAsiaMailUtils;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.EmployeeContactUSLogic#getContactEmail(java.lang.Long)
	 */
	@Override
	public String getContactEmail(Long companyId) {
		EmailPreferenceMaster emailPreferenceMaster = emailPreferenceMasterDAO
				.findByConditionCompany(companyId);
		if (emailPreferenceMaster.getContactEmail() != null) {
			String contactEmailId = emailPreferenceMaster.getContactEmail();
			return contactEmailId;
		} else {
			return null;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.EmployeeContactUSLogic#sendMail(com.payasia.common.
	 * form.EmployeeContactUSForm, java.lang.Long)
	 */
	@Override
	public String sendMail(EmployeeContactUSForm employeeContactUSForm,
			Long employeeId) {

		Employee employee = employeeDAO.findById(employeeId);

		if (employee == null) {
			return "employee.profile.is.not.defined";
		} else {
			if (!StringUtils.isNotBlank(employee.getEmail())) {
				return "email.id.not.found.please.update.your.email.id";
			}
		}

		EmailPreferenceMaster emailPreferenceMasterVO = emailPreferenceMasterDAO
				.findByConditionCompany(employee.getCompany().getCompanyId());
		if (emailPreferenceMasterVO == null) {
			return "email.configuration.is.not.defined";
		}
		if (StringUtils.isBlank(emailPreferenceMasterVO.getContactEmail())) {
			return "email.configuration.contact.email.is.not.defined";
		}

		PayAsiaEmailTO payAsiaEmailTO = new PayAsiaEmailTO();
		if (StringUtils.isNotBlank(emailPreferenceMasterVO.getContactEmail())) {
			payAsiaEmailTO.addMailTo(emailPreferenceMasterVO.getContactEmail());
		}
		if (StringUtils.isNotBlank(employeeContactUSForm.getMailCc())) {
			payAsiaEmailTO.addMainCc(employeeContactUSForm.getMailCc());
		}
		if (StringUtils.isNotBlank(employeeContactUSForm.getMailSubject())) {
			String subject = "";
			try {
				subject = URLDecoder.decode(
						employeeContactUSForm.getMailSubject(), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				LOGGER.error(e.getMessage(), e);
			}
			payAsiaEmailTO.setMailSubject(subject);
		}
		if (StringUtils.isNotBlank(employeeContactUSForm.getMailMessage())) {
			String message = "";
			try {
				message = URLDecoder.decode(
						employeeContactUSForm.getMailMessage(), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				LOGGER.error(e.getMessage(), e);
			}
			payAsiaEmailTO.setMailText(message);
		}
		if (StringUtils.isNotBlank(employee.getEmail())) {
			payAsiaEmailTO.setMailFrom(employee.getEmail());
		}

		payAsiaMailUtils.sendEmail(false, payAsiaEmailTO);
		return "payasia.success";

	}
}
