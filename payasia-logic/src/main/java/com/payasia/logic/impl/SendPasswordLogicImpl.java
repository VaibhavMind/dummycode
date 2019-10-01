package com.payasia.logic.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.payasia.common.dto.AccessControlConditionDTO;
import com.payasia.common.dto.EmployeeShortListDTO;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SendPasswordForm;
import com.payasia.common.form.SendPasswordResponse;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PasswordUtils;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.PayAsiaEmailTO;
import com.payasia.common.util.PayAsiaMailUtils;
import com.payasia.common.util.RandomNumberGenerator;
import com.payasia.dao.EmailPreferenceMasterDAO;
import com.payasia.dao.EmailTemplateDAO;
import com.payasia.dao.EmailTemplateSubCategoryMasterDAO;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.EmployeeLoginDetailDAO;
import com.payasia.dao.bean.EmailPreferenceMaster;
import com.payasia.dao.bean.EmailTemplate;
import com.payasia.dao.bean.EmailTemplateSubCategoryMaster;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeLoginDetail;
import com.payasia.logic.EmployeeDetailLogic;
import com.payasia.logic.GeneralLogic;
import com.payasia.logic.GeneralMailLogic;
import com.payasia.logic.SecurityLogic;
import com.payasia.logic.SendPasswordLogic;

/**
 * The Class SendPasswordLogicImpl.
 */
/**
 * @author vivekjain
 * 
 */
@Component
public class SendPasswordLogicImpl implements SendPasswordLogic {

	/** The Constant logger. */
	private static final Logger LOGGER = Logger
			.getLogger(SendPasswordLogicImpl.class);

	/** The payasia temp path. */
	@Value("#{payasiaptProperties['payasia.temp.path']}")
	private String PAYASIA_TEMP_PATH;

	/** The employee dao. */
	@Resource
	EmployeeDAO employeeDAO;

	/** The pay asia mail utils. */
	@Resource
	PayAsiaMailUtils payAsiaMailUtils;

	/** The email preference master dao. */
	@Resource
	EmailPreferenceMasterDAO emailPreferenceMasterDAO;

	/** The email template dao. */
	@Resource
	EmailTemplateDAO emailTemplateDAO;

	/** The email template sub category master dao. */
	@Resource
	EmailTemplateSubCategoryMasterDAO emailTemplateSubCategoryMasterDAO;

	@Resource
	EmployeeDetailLogic employeeDetailLogic;

	@Resource
	GeneralLogic generalLogic;

	@Resource
	SecurityLogic securityLogic;

	@Resource
	EmployeeLoginDetailDAO employeeLoginDetailDAO;
	@Resource
	GeneralMailLogic generalMailLogic;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.SendPasswordLogic#getEmployeeList(java.lang.String,
	 * java.lang.String, com.payasia.common.form.PageRequest,
	 * com.payasia.common.form.SortCondition, java.lang.Long)
	 */
	@Override
	public SendPasswordResponse getEmployeeList(String searchCondition,
			String searchText, PageRequest pageDTO, SortCondition sortDTO,
			Long companyId, Long employeeId)

	{
		try {
			searchText = URLDecoder.decode(searchText, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
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
		if (searchCondition.equals(PayAsiaConstants.EMAIL)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setEmail("%" + searchText.trim() + "%");
			}

		}

		EmployeeShortListDTO employeeShortListDTO = generalLogic
				.getShortListEmployeeIds(employeeId, companyId);

		conditionDTO.setEmployeeShortListDTO(employeeShortListDTO);

		List<Employee> empList;
		if ("all".equals(searchCondition)) {

			empList = employeeDAO.findAll(companyId, pageDTO, sortDTO);
		} else {
			recordSize = employeeDAO.getEmpCountForCondition(conditionDTO,
					companyId);
			empList = employeeDAO.findEmpForSendPassword(conditionDTO, pageDTO,
					sortDTO, companyId);
		}
		List<SendPasswordForm> employeeList = new ArrayList<SendPasswordForm>();
		for (Employee employee : empList) {
			SendPasswordForm empForm = new SendPasswordForm();
			
			/*ID ENCRYPT*/
			empForm.setEmployeeId(FormatPreserveCryptoUtil.encrypt(employee.getEmployeeId()));
			
			empForm.setEmployeeNumber(employee.getEmployeeNumber());
			empForm.setFirstName(employee.getFirstName());
			if (StringUtils.isNotBlank(employee.getLastName())) {
				empForm.setLastName(employee.getLastName());
			}

			empForm.setEmail(employee.getEmail());
			employeeList.add(empForm);
		}

		SendPasswordResponse response = new SendPasswordResponse();

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
		response.setRows(employeeList);

		return response;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.SendPasswordLogic#sendPwdEmail(java.lang.Long,
	 * java.lang.String[])
	 */
	@Override
	public String sendPwdEmail(Long companyId, String[] employeeId) {
		File emailTemplate = null;
		FileOutputStream fos = null;
		File emailSubjectTemplate = null;
		FileOutputStream fosSubject = null;

		List<EmailTemplateSubCategoryMaster> EmailTemplateSubCategoryMasterList = emailTemplateSubCategoryMasterDAO
				.findAll();

		Long subCategoryId = getSubCategoryId(
				PayAsiaConstants.PAYASIA_SUB_CATEGORY_PASSWORD,
				EmailTemplateSubCategoryMasterList);

		EmailTemplate emailTemplateVO = emailTemplateDAO
				.findByConditionSubCategoryAndCompId(
						PayAsiaConstants.PAYASIA_SEND_PASSWORD_NAME,
						subCategoryId, companyId);
		if (emailTemplateVO == null) {
			return "send.password.template.is.not.defined";
		}

		EmailPreferenceMaster emailPreferenceMasterVO = emailPreferenceMasterDAO
				.findByConditionCompany(companyId);
		if (emailPreferenceMasterVO == null) {
			return "email.configuration.is.not.defined";
		}
		if (StringUtils.isBlank(emailPreferenceMasterVO.getContactEmail())) {
			return "email.configuration.contact.email.is.not.defined";
		}

		String returnEmpIds = "";

		for (int count = 0; count < employeeId.length; count++) {
			PayAsiaEmailTO payAsiaEmailTO = new PayAsiaEmailTO();
			Employee employeeVO = employeeDAO.findById(Long
					.parseLong(employeeId[count]));
			String encrptedPassword = "";
			String salt = securityLogic.generateSalt();
			if (StringUtils.isNotBlank(employeeVO.getEmail())) {
				String mailBody = emailTemplateVO.getBody();
				String mailSubject = emailTemplateVO.getSubject();
				Map<String, Object> modelMap = new HashMap<String, Object>();

				modelMap.put("firstName", employeeVO.getFirstName());
				modelMap.put("First_Name", employeeVO.getFirstName());
				modelMap.put("employeeId", employeeVO.getEmployeeNumber());
				modelMap.put("Employee_Number", employeeVO.getEmployeeNumber());

				modelMap.put("userName", employeeVO.getEmployeeLoginDetail()
						.getLoginName());
				modelMap.put("Username", employeeVO.getEmployeeLoginDetail()
						.getLoginName());
				modelMap.put("companyName", employeeVO.getCompany()
						.getCompanyName());
				modelMap.put("Company_Name", employeeVO.getCompany()
						.getCompanyName());
				if (StringUtils.isNotBlank(employeeVO.getEmployeeLoginDetail()
						.getPassword())) {

					String resetPassword = PasswordUtils.getRandomPassword();
					encrptedPassword = securityLogic.encrypt(resetPassword,
							salt);
					modelMap.put("password", resetPassword);
					modelMap.put("Password", resetPassword);
				}
				if (!StringUtils.isNotBlank(employeeVO.getEmployeeLoginDetail()
						.getPassword())) {
					modelMap.put("password", "");
					modelMap.put("Password", "");
				}
				if (StringUtils.isNotBlank(employeeVO.getMiddleName())) {
					modelMap.put("middleName", employeeVO.getMiddleName());
					modelMap.put("Middle_Name", employeeVO.getMiddleName());
				}
				if (!StringUtils.isNotBlank(employeeVO.getMiddleName())) {
					modelMap.put("middleName", "");
					modelMap.put("Middle_Name", "");
				}
				if (StringUtils.isNotBlank(employeeVO.getLastName())) {
					modelMap.put("lastName", employeeVO.getLastName());
					modelMap.put("Last_Name", employeeVO.getLastName());
				}
				if (!StringUtils.isNotBlank(employeeVO.getLastName())) {
					modelMap.put("lastName", "");
					modelMap.put("Last_Name", "");
				}
				if (StringUtils.isNotBlank(employeeVO.getEmail())) {
					modelMap.put("email", employeeVO.getEmail());
					modelMap.put("Email", employeeVO.getEmail());
				}
				if (!StringUtils.isNotBlank(employeeVO.getEmail())) {
					modelMap.put("email", "");
					modelMap.put("Email", "");
				}
				modelMap.put(PayAsiaConstants.HRIS_EMAIL_EMPLOYEE_NAME,
						generalMailLogic.getEmployeeName(employeeVO));
				if (emailPreferenceMasterVO.getCompanyUrl() != null) {
					if (StringUtils.isNotBlank(emailPreferenceMasterVO
							.getCompanyUrl())) {
						String link = "<a href='"
								+ emailPreferenceMasterVO.getCompanyUrl()
								+ "'>"
								+ emailPreferenceMasterVO.getCompanyUrl()
								+ "</a>";
						modelMap.put(PayAsiaConstants.URL, link);
					} else {
						modelMap.put(PayAsiaConstants.URL, "");
					}
				} else {
					modelMap.put(PayAsiaConstants.URL, "");
				}

				try {
					byte[] mailBodyBytes = mailBody.getBytes("UTF-8");
					byte[] mailSubjectBytes = mailSubject.getBytes("UTF-8");
					String uniqueId = RandomNumberGenerator
							.getNDigitRandomNumber(8);
					emailTemplate = new File(PAYASIA_TEMP_PATH + "//"
							+ "sendPasswordTemplate" + employeeId[count] + "_"
							+ uniqueId + ".vm");
					emailTemplate.deleteOnExit();
					emailSubjectTemplate = new File(PAYASIA_TEMP_PATH + "//"
							+ "sendPasswordSubjectTemplate" + employeeId[count]
							+ "_" + uniqueId + ".vm");
					emailSubjectTemplate.deleteOnExit();

					try {
						fos = new FileOutputStream(emailTemplate);
						fos.write(mailBodyBytes);
						fos.flush();
						fos.close();
						fosSubject = new FileOutputStream(emailSubjectTemplate);
						fosSubject.write(mailSubjectBytes);
						fosSubject.flush();
						fosSubject.close();
					} catch (FileNotFoundException ex) {
						LOGGER.error(ex.getMessage(), ex);
					} catch (IOException ex) {
						LOGGER.error(ex.getMessage(), ex);
					}

					if (StringUtils.isNotBlank(employeeVO.getEmail())) {
						payAsiaEmailTO.addMailTo(employeeVO.getEmail());
					}
					if (StringUtils.isNotBlank(emailTemplateVO.getSubject())) {
						payAsiaEmailTO.setMailSubject(emailTemplateVO
								.getSubject());
					}
					if (StringUtils.isNotBlank(emailTemplateVO.getBody())) {
						payAsiaEmailTO.setMailText(emailTemplateVO.getBody());
					}
					if (StringUtils.isNotBlank(emailPreferenceMasterVO
							.getContactEmail())) {
						payAsiaEmailTO.setMailFrom(emailPreferenceMasterVO
								.getContactEmail());
					}

					EmployeeLoginDetail employeeLoginDetail = employeeVO
							.getEmployeeLoginDetail();
					employeeLoginDetail.setPasswordSent(true);
					employeeLoginDetail.setPassword(encrptedPassword);
					employeeLoginDetail.setSalt(salt);
					employeeLoginDetail.setPasswordReset(true);
					employeeLoginDetailDAO.update(employeeLoginDetail);

					payAsiaMailUtils.sendEmail(
							modelMap,
							emailTemplate.getPath().substring(
									emailTemplate.getParent().length() + 1),
							emailSubjectTemplate.getPath()
									.substring(
											emailSubjectTemplate.getParent()
													.length() + 1), true,
							payAsiaEmailTO);

				} catch (Exception ex) {
					LOGGER.error(ex.getMessage(), ex);
				} finally {
					if (emailTemplate != null) {
						emailTemplate.delete();

					}
				}
			} else {
				returnEmpIds += employeeVO.getEmployeeNumber();
				if ((count + 1) != employeeId.length) {
					returnEmpIds += " ,";
				}
			}
		}
		if (StringUtils.isNotBlank(returnEmpIds)) {

			return "password.are.not.sent.to.these.employees.excluding.employe.numbers.are/"
					+ returnEmpIds;

		} else {
			return "password.is.sent.to.selected.employees";
		}
	}

	/**
	 * Gets the sub category id.
	 * 
	 * @param subCategoryName
	 *            the sub category name
	 * @param EmailTemplateSubCategoryMasterList
	 *            the email template sub category master list
	 * @return the sub category id
	 */
	public Long getSubCategoryId(
			String subCategoryName,
			List<EmailTemplateSubCategoryMaster> EmailTemplateSubCategoryMasterList) {
		for (EmailTemplateSubCategoryMaster emailTemplateSubCategoryMaster : EmailTemplateSubCategoryMasterList) {
			if ((subCategoryName.toUpperCase())
					.equals(emailTemplateSubCategoryMaster.getSubCategoryName()
							.toUpperCase())) {
				return emailTemplateSubCategoryMaster
						.getEmailTemplateSubCategoryId();
			}
		}
		return null;
	}

}
