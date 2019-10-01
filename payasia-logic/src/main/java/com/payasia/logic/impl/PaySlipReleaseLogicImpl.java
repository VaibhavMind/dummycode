package com.payasia.logic.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.ui.velocity.VelocityEngineUtils;

import com.payasia.common.dto.EmployeeConditionDTO;
import com.payasia.common.dto.PayslipConditionDTO;
import com.payasia.common.dto.PayslipDTO;
import com.payasia.common.dto.PayslipReleaseConditionDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.PaySlipReleaseForm;
import com.payasia.common.form.PaySlipReleaseFormResponse;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.RandomNumberGenerator;
import com.payasia.dao.AnnouncementDAO;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.CompanyDocumentDAO;
import com.payasia.dao.CompanyPayslipReleaseDAO;
import com.payasia.dao.DocumentCategoryMasterDAO;
import com.payasia.dao.EmailDAO;
import com.payasia.dao.EmailPreferenceMasterDAO;
import com.payasia.dao.EmailTemplateDAO;
import com.payasia.dao.EmailTemplateSubCategoryMasterDAO;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.HRISPreferenceDAO;
import com.payasia.dao.MonthMasterDAO;
import com.payasia.dao.PayslipDAO;
import com.payasia.dao.bean.Announcement;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.CompanyDocument;
import com.payasia.dao.bean.CompanyPayslipRelease;
import com.payasia.dao.bean.DocumentCategoryMaster;
import com.payasia.dao.bean.Email;
import com.payasia.dao.bean.EmailPreferenceMaster;
import com.payasia.dao.bean.EmailTemplate;
import com.payasia.dao.bean.EmailTemplateSubCategoryMaster;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.HRISPreference;
import com.payasia.dao.bean.MonthMaster;
import com.payasia.dao.bean.Payslip;
import com.payasia.logic.DataImportLogic;
import com.payasia.logic.GeneralMailLogic;
import com.payasia.logic.PaySlipReleaseLogic;

@Component
public class PaySlipReleaseLogicImpl implements PaySlipReleaseLogic {

	private static final Logger LOGGER = Logger
			.getLogger(PaySlipReleaseLogicImpl.class);

	@Resource
	CompanyPayslipReleaseDAO companyPayslipReleaseDAO;
	@Resource
	CompanyDAO companyDAO;
	@Resource
	MonthMasterDAO monthMasterDAO;

	@Resource
	DocumentCategoryMasterDAO documentCategoryMasterDAO;
	@Resource
	CompanyDocumentDAO companyDocumentDAO;

	@Resource
	AnnouncementDAO announcementDAO;
	
	@Resource
	PayslipDAO payslipDAO;
	
	@Resource
	EmployeeDAO employeeDAO;
	
	@Resource
	DataImportLogic dataImportLogic;
	
	@Resource
	HRISPreferenceDAO hrisPreferenceDAO;
	
	@Resource
	EmailDAO emailDAO;
	
	@Resource
	EmailPreferenceMasterDAO emailPreferenceMasterDAO;

	@Autowired
	private VelocityEngine velocityEngine;
	
	@Resource
	GeneralMailLogic generalMailLogic;
	
	@Resource
	EmailTemplateSubCategoryMasterDAO emailTemplateSubCategoryMasterDAO;
	
	@Resource
	EmailTemplateDAO emailTemplateDAO;
	
	@Value("#{payasiaptProperties['payasia.temp.path']}")
	private String PAYASIA_TEMP_PATH;
	
	@Override
	public PaySlipReleaseFormResponse viewPayslipReleaseList(
			String searchCondition, String searchText, PageRequest pageDTO,
			SortCondition sortDTO, Long companyId) {
		PayslipReleaseConditionDTO conditionDTO = new PayslipReleaseConditionDTO();

		try {
			searchText = URLDecoder.decode(searchText, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		if (searchCondition
				.equals(PayAsiaConstants.PAYSLIP_RELEASE_PAYSLIP_NAME)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setName(searchText);
			}
		}
		if (searchCondition
				.equals(PayAsiaConstants.PAYSLIP_RELEASE_PAYSLIP_YEAR)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setYear(Integer.parseInt(searchText));
			}
		}
		if (searchCondition
				.equals(PayAsiaConstants.PAYSLIP_RELEASE_PAYSLIP_MONTH)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setMonthId(Long.parseLong(searchText));
			}
		}

		List<PaySlipReleaseForm> paySlipReleaseFormList = new ArrayList<PaySlipReleaseForm>();
		List<CompanyPayslipRelease> companyPayRelVOList = companyPayslipReleaseDAO
				.findByCondition(conditionDTO, companyId, pageDTO, sortDTO);
		for (CompanyPayslipRelease companyPayRelVO : companyPayRelVOList) {
			PaySlipReleaseForm payRelForm = new PaySlipReleaseForm();
			payRelForm.setName(companyPayRelVO.getName());
			payRelForm.setPayslipYear(companyPayRelVO.getYear());
			payRelForm.setMonthName(companyPayRelVO.getMonthMaster()
					.getMonthName());
			payRelForm.setPayslipPart(companyPayRelVO.getPart());
			payRelForm.setRelease(companyPayRelVO.isReleased());
			/*ID ENCRYPT*/
			payRelForm.setCompanyPayslipReleaseId(FormatPreserveCryptoUtil.encrypt(companyPayRelVO
					.getCompanyPayslipReleaseId()));
			paySlipReleaseFormList.add(payRelForm);
		}
		PaySlipReleaseFormResponse response = new PaySlipReleaseFormResponse();
		int recordSize = companyPayslipReleaseDAO.getCountByCondition(
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
		response.setResponse(paySlipReleaseFormList);

		return response;
	}

	@Override
	public String savePaySlipRelease(PaySlipReleaseForm paySlipReleaseForm,
			Long companyId) {
		CompanyPayslipRelease companyPayslipRelease = new CompanyPayslipRelease();

		CompanyPayslipRelease duplicateEntry = companyPayslipReleaseDAO
				.findByCondition(null, paySlipReleaseForm.getPayslipMonthId(),
						paySlipReleaseForm.getPayslipYear(),
						paySlipReleaseForm.getPayslipPart(), companyId);
		if (duplicateEntry != null) {
			return "duplicate";
		}

		Company company = companyDAO.findById(companyId);
		companyPayslipRelease.setCompany(company);

		MonthMaster monthMaster = monthMasterDAO.findById(paySlipReleaseForm
				.getPayslipMonthId());
		companyPayslipRelease.setMonthMaster(monthMaster);
		try {
			companyPayslipRelease.setName(URLDecoder.decode(
					paySlipReleaseForm.getName(), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		String str = paySlipReleaseForm.getScheduleDate() + " " + paySlipReleaseForm.getScheduleTime();
		companyPayslipRelease.setPart(paySlipReleaseForm.getPayslipPart());
		companyPayslipRelease.setReleased(paySlipReleaseForm.isRelease());
		companyPayslipRelease.setYear(paySlipReleaseForm.getPayslipYear());
		companyPayslipRelease.setEmailTo(paySlipReleaseForm.getEmailTo());
		companyPayslipRelease.setSendEmail(paySlipReleaseForm.isSendMail());
		try {
			 if(paySlipReleaseForm.getScheduleDate()!=null && !paySlipReleaseForm.getScheduleDate().isEmpty()){
			companyPayslipRelease.setReleaseDateTime(DateUtils.stringToTimestampDate(str,company.getDateFormat(),company.getTimeZoneMaster().getGmtOffset()));
			 }else{
				 companyPayslipRelease.setReleaseDateTime(null);
			 }
			
		} catch (Exception e) {

			LOGGER.error(e.getMessage(), e);
		}
		companyPayslipReleaseDAO.save(companyPayslipRelease);

		if (paySlipReleaseForm.isRelease()) {
			boolean isFuturePaySlipReleased = companyPayslipReleaseDAO
					.isFuturePaySlipReleased(
							paySlipReleaseForm.getPayslipMonthId(),
							paySlipReleaseForm.getPayslipYear(), companyId,
							paySlipReleaseForm.getPayslipPart());
			if (!isFuturePaySlipReleased) {

				announcementDAO.updatePayslipEndate(companyId);

				Announcement announcement = new Announcement();

				announcement.setCompany(company);
				announcement.setCompanyGroup(company.getCompanyGroup());
				announcement.setTitle(PayAsiaConstants.PAY_SLIP_ENTITY_NAME);
				announcement
						.setDescription(PayAsiaConstants.PAYASIA_PAYSLIP_ANNOUNCEMENT_START_TEXT
								+ monthMaster.getMonthName()
								+ " "
								+ paySlipReleaseForm.getPayslipYear()
								+ PayAsiaConstants.PAYASIA_PAYSLIP_ANNOUNCEMENT_END_TEXT);
				announcement.setScope("C");
				announcement.setPostDateTime(DateUtils.getCurrentTimestamp());
				announcementDAO.save(announcement);
			}

		}
		if (paySlipReleaseForm.isSendMail() && paySlipReleaseForm.isRelease()) {
			saveAndSendPayslipReleaseEmail(companyId, paySlipReleaseForm);
		}
		return "success";
	}

	@Override
	public String deletePaySlipRelease(Long companyPayslipReleaseId,Long companyId ) {
		CompanyPayslipRelease companyPayslipReleaseVO = companyPayslipReleaseDAO.findByCompanyPayslipReleaseId(companyPayslipReleaseId,companyId);
		if(companyPayslipReleaseVO!=null){
			int payslipMonth = (int) companyPayslipReleaseVO.getMonthMaster().getMonthId();
			int payslipYear = companyPayslipReleaseVO.getYear();
			Date currentDate = new Date();
			Calendar cal = Calendar.getInstance();
			cal.setTime(currentDate);
			int currYear = cal.get(Calendar.YEAR);
			int currMonth = cal.get(Calendar.MONTH) + 1;
			if(payslipYear >= currYear) {
				if (payslipMonth > currMonth) {
					companyPayslipReleaseDAO.delete(companyPayslipReleaseVO);
					return "SUCCESS";
				}
			 }
		}
		return "FAILED";

	}

	@Override
	public PaySlipReleaseForm getPaySlipReleaseDetails(
			long companyPayslipReleaseId,long companyId) {
		PaySlipReleaseForm paySlipReleaseForm = new PaySlipReleaseForm();
		CompanyPayslipRelease companyPayslipReleaseVO = companyPayslipReleaseDAO.findByCompanyPayslipReleaseId(companyPayslipReleaseId,companyId);
		Company company=companyDAO.findById(companyId);	
		if (companyPayslipReleaseVO != null) {
			paySlipReleaseForm.setName(companyPayslipReleaseVO.getName());
			paySlipReleaseForm.setPayslipYear(companyPayslipReleaseVO.getYear());
			paySlipReleaseForm.setPayslipMonthId(companyPayslipReleaseVO.getMonthMaster().getMonthId());
			paySlipReleaseForm.setPayslipPart(companyPayslipReleaseVO.getPart());
			paySlipReleaseForm.setRelease(companyPayslipReleaseVO.isReleased());
			paySlipReleaseForm.setSendMail(companyPayslipReleaseVO.getSendEmail());
			try {
				if (companyPayslipReleaseVO.getReleaseDateTime() != null) {
		             Date date=new Date(companyPayslipReleaseVO.getReleaseDateTime().getTime());  
		             Date d=DateUtils.gmttoLocalDate(date,company.getTimeZoneMaster().getGmtOffset());
		             paySlipReleaseForm.setScheduleDate(DateUtils.dateToString(d));
		             Timestamp ts=DateUtils.convertDateToTimeStamp(d);
					 paySlipReleaseForm.setScheduleTime(DateUtils.dateToStringTime(ts));
					 paySlipReleaseForm.setEmailTo(companyPayslipReleaseVO.getEmailTo());
				}
				else{
					paySlipReleaseForm.setEmailTo(null);	
				}
				
			} catch (Exception e) {

				LOGGER.error(e.getMessage(), e);
			}
		}
		return paySlipReleaseForm;
	}

	@Override
	public String editPaySlipRelease(PaySlipReleaseForm paySlipReleaseForm,
			Long companyId, long companyPayslipReleaseId) {
		
		CompanyPayslipRelease duplicateEntry = companyPayslipReleaseDAO
				.findByCondition(companyPayslipReleaseId,
						paySlipReleaseForm.getPayslipMonthId(),
						paySlipReleaseForm.getPayslipYear(),
						paySlipReleaseForm.getPayslipPart(), companyId);
		if (duplicateEntry != null) {
			return "duplicate";
		}
		
		CompanyPayslipRelease companyPayslipReleaseVO = companyPayslipReleaseDAO
				.findByCompanyPayslipReleaseId(companyPayslipReleaseId,companyId);
	
		Company company = companyDAO.findById(companyId);
		companyPayslipReleaseVO.setCompany(company);

		MonthMaster monthMaster = monthMasterDAO.findById(paySlipReleaseForm
				.getPayslipMonthId());
		companyPayslipReleaseVO.setMonthMaster(monthMaster);

		try {
			companyPayslipReleaseVO.setName(URLDecoder.decode(
					paySlipReleaseForm.getName(), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		String str = paySlipReleaseForm.getScheduleDate() + " " + paySlipReleaseForm.getScheduleTime();
		companyPayslipReleaseVO.setPart(paySlipReleaseForm.getPayslipPart());
		companyPayslipReleaseVO.setReleased(paySlipReleaseForm.isRelease());
		companyPayslipReleaseVO.setYear(paySlipReleaseForm.getPayslipYear());
		companyPayslipReleaseVO.setEmailTo(paySlipReleaseForm.getEmailTo());
		companyPayslipReleaseVO.setSendEmail(paySlipReleaseForm.isSendMail());
		try {
            if(paySlipReleaseForm.getScheduleDate()!=null && !paySlipReleaseForm.getScheduleDate().isEmpty()){
               companyPayslipReleaseVO.setReleaseDateTime(DateUtils.stringToTimestampDate(str,company.getDateFormat(),company.getTimeZoneMaster().getGmtOffset()));
          	 
            }
            else{
          		companyPayslipReleaseVO.setReleaseDateTime(null);
            }

		} catch (Exception e) {

			LOGGER.error(e.getMessage(), e);
		}
		companyPayslipReleaseDAO.update(companyPayslipReleaseVO);
		String announcementNDesc = PayAsiaConstants.PAYASIA_PAYSLIP_ANNOUNCEMENT_START_TEXT
				+ monthMaster.getMonthName()
				+ " "
				+ paySlipReleaseForm.getPayslipYear()
				+ PayAsiaConstants.PAYASIA_PAYSLIP_ANNOUNCEMENT_END_TEXT;
		if (paySlipReleaseForm.isRelease()) {
			boolean isFuturePaySlipReleased = companyPayslipReleaseDAO
					.isFuturePaySlipReleased(
							paySlipReleaseForm.getPayslipMonthId(),
							paySlipReleaseForm.getPayslipYear(), companyId,
							paySlipReleaseForm.getPayslipPart());
			if (!isFuturePaySlipReleased) {

				announcementDAO.updatePayslipEndate(companyId);

				Announcement announcement = new Announcement();

				announcement.setCompany(company);
				announcement.setCompanyGroup(company.getCompanyGroup());
				announcement.setTitle(PayAsiaConstants.PAY_SLIP_ENTITY_NAME);
				announcement.setDescription(announcementNDesc);
				announcement.setScope("C");
				announcement.setPostDateTime(DateUtils.getCurrentTimestamp());
				announcementDAO.save(announcement);
			}

		} else {

			announcementDAO.deleteByCondition(announcementNDesc,
					PayAsiaConstants.PAY_SLIP_ENTITY_NAME, companyId);

		}

		DocumentCategoryMaster categoryMaster = documentCategoryMasterDAO
				.findByCondition(PayAsiaConstants.DOCUMENT_CATEGORY_PAYSLIP);
		companyDocumentDAO.updatePayslipReleaseStatus(companyId,
				monthMaster.getMonthId(), paySlipReleaseForm.getPayslipYear(),
				paySlipReleaseForm.getPayslipPart(),
				categoryMaster.getDocumentCategoryId(),
				paySlipReleaseForm.isRelease());

		if (paySlipReleaseForm.isSendMail() && paySlipReleaseForm.isRelease()) {
			saveAndSendPayslipReleaseEmail(companyId, paySlipReleaseForm);
		}
		return "success";
	}

	private void saveAndSendPayslipReleaseEmail(Long companyId,
			PaySlipReleaseForm paySlipReleaseForm) {
		List<PayslipDTO> payslipDTOList = new ArrayList<PayslipDTO>();
		HRISPreference hrisPreferenceVO = hrisPreferenceDAO
				.findByCompanyId(companyId);
		if (hrisPreferenceVO != null
				&& hrisPreferenceVO.isSendPayslipReleaseMail()) {
			DocumentCategoryMaster categoryMaster = documentCategoryMasterDAO
					.findByCondition(PayAsiaConstants.DOCUMENT_CATEGORY_PAYSLIP);
			List<Payslip> payslipVOList = null;
			List<CompanyDocument> companyDocumentListVO = companyDocumentDAO
					.findByConditionSourceTextAndDesc(
							companyId,
							categoryMaster.getDocumentCategoryId(),
							paySlipReleaseForm.getPayslipYear(),
							paySlipReleaseForm.getPayslipPart(),
							paySlipReleaseForm.getPayslipMonthId(),
							PayAsiaConstants.COMPANY_DOCUMENT_UPLOAD_SOURCE_TEXT);
			if (companyDocumentListVO.isEmpty()) {
				companyDocumentListVO = companyDocumentDAO
						.findByConditionSourceTextAndDesc(
								companyId,
								categoryMaster.getDocumentCategoryId(),
								paySlipReleaseForm.getPayslipYear(),
								paySlipReleaseForm.getPayslipPart(),
								paySlipReleaseForm.getPayslipMonthId(),
								PayAsiaConstants.COMPANY_DOCUMENT_UPLOAD_SOURCE_PDF);
				if (companyDocumentListVO.isEmpty()) {
					PayslipConditionDTO payslipConditionDTO = new PayslipConditionDTO();
					payslipConditionDTO.setCompanyId(companyId);
					payslipConditionDTO.setMonthMasterId(paySlipReleaseForm
							.getPayslipMonthId());
					payslipConditionDTO.setYear(paySlipReleaseForm
							.getPayslipYear());
					payslipConditionDTO.setPart(paySlipReleaseForm
							.getPayslipPart());
					payslipVOList = payslipDAO.getReleasedPayslipDetails(
							payslipConditionDTO, companyId);
				}

			}
			if (!companyDocumentListVO.isEmpty()) {
				MonthMaster monthMasterVO = monthMasterDAO
						.findById(paySlipReleaseForm.getPayslipMonthId());
				Map<String, Employee> employeeMap = new HashMap<String, Employee>();
				EmployeeConditionDTO conditionDTO = new EmployeeConditionDTO();
				List<BigInteger> employeeShortListEmployeeIds = new ArrayList<>();
				conditionDTO
						.setShortListEmployeeIds(employeeShortListEmployeeIds);
				conditionDTO.setEmployeeShortList(false);

				List<Employee> employeeVOList = employeeDAO
						.getEmployeeListByAdvanceFilter(conditionDTO, null,
								null, companyId);
				for (Employee employeeVO : employeeVOList) {
					employeeMap.put(employeeVO.getEmployeeNumber(), employeeVO);
				}
				for (CompanyDocument companyDocument : companyDocumentListVO) {
					PayslipDTO payslipDTO = new PayslipDTO();
					payslipDTO.setYear(companyDocument.getYear());
					payslipDTO.setMonthName(monthMasterVO.getMonthAbbr());
					payslipDTO.setPart(companyDocument.getPart());
					payslipDTO.setEmployeeNumber(companyDocument
							.getDescription().substring(
									0,
									companyDocument.getDescription().indexOf(
											'_')));
					Employee employeeVO = employeeMap.get(companyDocument
							.getDescription().substring(
									0,
									companyDocument.getDescription().indexOf(
											'_')));
					if (employeeVO != null) {
						payslipDTO.setEmployeeId(employeeVO.getEmployeeId());
						payslipDTO.setEmployeeNumber(employeeVO
								.getEmployeeNumber());
						payslipDTO.setEmail(employeeVO.getEmail());
						payslipDTO.setFirstName(employeeVO.getFirstName());
						payslipDTO.setLastName(employeeVO.getLastName());
						payslipDTO.setMiddleName(employeeVO.getMiddleName());
						payslipDTO.setLoginName(employeeVO
								.getEmployeeLoginDetail().getLoginName());
						payslipDTO.setCompanyName(employeeVO.getCompany()
								.getCompanyName());
						payslipDTOList.add(payslipDTO);
					}
				}
			}
			if (payslipVOList != null) {
				for (Payslip payslipVO : payslipVOList) {
					PayslipDTO payslipDTO = new PayslipDTO();
					payslipDTO.setYear(payslipVO.getYear());
					payslipDTO.setMonthName(payslipVO.getMonthMaster()
							.getMonthAbbr());
					payslipDTO.setPart(payslipVO.getPart());
					payslipDTO.setEmployeeId(payslipVO.getEmployee()
							.getEmployeeId());
					payslipDTO.setEmployeeNumber(payslipVO.getEmployee()
							.getEmployeeNumber());
					payslipDTO.setEmail(payslipVO.getEmployee().getEmail());
					payslipDTO.setFirstName(payslipVO.getEmployee()
							.getFirstName());
					payslipDTO.setLastName(payslipVO.getEmployee()
							.getLastName());
					payslipDTO.setMiddleName(payslipVO.getEmployee()
							.getMiddleName());
					payslipDTO.setLoginName(payslipVO.getEmployee()
							.getEmployeeLoginDetail().getLoginName());
					payslipDTO.setCompanyName(payslipVO.getEmployee()
							.getCompany().getCompanyName());
					payslipDTOList.add(payslipDTO);
				}
			}
			dataImportLogic.savePayslipReleaseEmail(companyId, payslipDTOList);
		}
	}

	@Override
	public Integer getPaySlipPart(Long companyId) {
		Company company = companyDAO.findById(companyId);

		return company.getPart();
	}

	@Override
	public String getPayslipSendMailDetails(Long companyId) {
		String status = PayAsiaConstants.FALSE;
		HRISPreference hrisPreferenceVO = hrisPreferenceDAO
				.findByCompanyId(companyId);
		if (hrisPreferenceVO == null) {
			status = PayAsiaConstants.FALSE;
		} else if (hrisPreferenceVO.isSendPayslipReleaseMail()) {
			status = PayAsiaConstants.TRUE;
		}
		return status;
	}
	
	@Override
	public void sendPayslipReleaseEmailTO(Long companyId, CompanyPayslipRelease companyPayslipRelease) {
		
		String systemEmailFrom = isSystemMailReq(companyId);
		String emailTo = companyPayslipRelease.getEmailTo();
		List<String> emailToList = null;
		if (StringUtils.isNotBlank(emailTo)) {
			emailTo.trim();
			emailToList = Arrays.asList(emailTo.split(";"));
		}
		if (emailToList != null) {
			for (String emailTO : emailToList) {
				Company companyVO = companyDAO.findById(companyId);
				List<EmailTemplateSubCategoryMaster> EmailTemplateSubCategoryMasterList = emailTemplateSubCategoryMasterDAO
						.findAll();
				Long subCategoryId = generalMailLogic.getSubCategoryId(
						PayAsiaConstants.PAYASIA_SUB_CATEGORY_SCHEDULE_PAYSLIP_RELEASE,
						EmailTemplateSubCategoryMasterList);

				EmailTemplate emailTemplateVO = emailTemplateDAO.findByConditionSubCategoryAndCompId(
						PayAsiaConstants.PAYASIA_SUB_CATEGORY_SCHEDULE_PAYSLIP_RELEASE, subCategoryId, companyId);
				if(emailTemplateVO!=null){

				EmailPreferenceMaster emailPreferenceMasterVO = emailPreferenceMasterDAO
						.findByConditionCompany(companyId);
				

				if (StringUtils.isNotBlank(emailTO)) {
					String mailBody = emailTemplateVO.getBody();
					Map<String, Object> modelMap = new HashMap<String, Object>();

					modelMap.put("Company_Name", companyPayslipRelease.getCompany().getCompanyName());

					modelMap.put("Month", companyPayslipRelease.getMonthMaster().getMonthName());
					modelMap.put("Year", companyPayslipRelease.getYear());
					modelMap.put("Part", companyPayslipRelease.getPart());

					if (emailPreferenceMasterVO.getCompanyUrl() != null) {
						String link = "<a href='" + emailPreferenceMasterVO.getCompanyUrl() + "'>"
								+ emailPreferenceMasterVO.getCompanyUrl() + "</a>";
						modelMap.put(PayAsiaConstants.LEAVE_COMPANY_URL, link);

					} else {
						modelMap.put(PayAsiaConstants.LEAVE_COMPANY_URL, "");
					}
				
					File emailTemplate = null;
					FileOutputStream fos = null;
					File emailSubjectTemplate = null;
					FileOutputStream fosSubject = null;
					
					try {
						byte[] mailBodyBytes = mailBody.getBytes("UTF-8");
						byte[] mailSubjectBytes = emailTemplateVO.getSubject().getBytes("UTF-8");
						String uniqueId = RandomNumberGenerator.getNDigitRandomNumber(8);
						emailTemplate = new File(PAYASIA_TEMP_PATH + "//" + "sendReleasePayslipMailTemplate"
								+ companyPayslipRelease.getCompanyPayslipReleaseId() + "_" + uniqueId + ".vm");
						emailSubjectTemplate = new File(PAYASIA_TEMP_PATH + "//"
								+ "sendReleasePayslipMailTemplateSubject"
								+ companyPayslipRelease.getCompanyPayslipReleaseId() + "_" + uniqueId + ".vm");
						emailSubjectTemplate.deleteOnExit();
						emailTemplate.deleteOnExit();

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

						String templateBodyString = emailTemplate.getPath()
								.substring(emailTemplate.getParent().length() + 1);
						String templateSubjectString = emailSubjectTemplate.getPath()
								.substring(emailSubjectTemplate.getParent().length() + 1);

						StringBuilder subjectText = new StringBuilder("");
						subjectText.append(VelocityEngineUtils.mergeTemplateIntoString(velocityEngine,
								templateSubjectString, modelMap));

						StringBuilder bodyText = new StringBuilder("");
						bodyText.append(VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, templateBodyString,
								modelMap));

						Email email = new Email();
						email.setBody(bodyText.toString());
						email.setSubject(subjectText.toString());
						email.setCompany(companyVO);
						email.setEmailFrom(systemEmailFrom);
						email.setCreatedDate(DateUtils.getCurrentTimestampWithTime());
						email.setEmailTo(emailTO);
						emailDAO.saveReturn(email);

					} catch (Exception ex) {
						LOGGER.error(ex.getMessage(), ex);
					} finally {
						if (emailTemplate != null) {
							emailTemplate.delete();

						}
					}
				}
			}else{
				throw new PayAsiaSystemException("Email template is not defined");
			}
		}
	}
}

	private String isSystemMailReq(Long companyId) {
		HRISPreference hrisPreferenceVO = hrisPreferenceDAO
				.findByCompanyId(companyId);
		String fromAddress = "";
		if (hrisPreferenceVO!=null && hrisPreferenceVO.isUseSystemMailAsFromAddress()) {

			EmailPreferenceMaster emailPreferenceMaster = emailPreferenceMasterDAO
					.findByConditionCompany(companyId);
			fromAddress = emailPreferenceMaster
					.getSystemEmailForSendingEmails();
		}
		return fromAddress;
	}
	
	@Override
	public String getPaySlipEmailTo(Long companyId) {
		HRISPreference hrisPreference = hrisPreferenceDAO.findByCompanyId(companyId);
		return hrisPreference.getPaySlipDefaultEmailTo();
	}

}
