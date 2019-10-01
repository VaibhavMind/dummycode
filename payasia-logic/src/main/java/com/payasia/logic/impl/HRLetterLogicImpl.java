package com.payasia.logic.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.output.FileWriterWithEncoding;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.ui.velocity.VelocityEngineUtils;
import org.xml.sax.SAXException;

import com.mind.payasia.xml.bean.Column;
import com.mind.payasia.xml.bean.EmployeeFilter;
import com.mind.payasia.xml.bean.EmployeeFilterTemplate;
import com.mind.payasia.xml.bean.Field;
import com.mind.payasia.xml.bean.Tab;
import com.payasia.common.dto.CodeDescDTO;
import com.payasia.common.dto.ColumnPropertyDTO;
import com.payasia.common.dto.DataImportKeyValueDTO;
import com.payasia.common.dto.DynamicTableDTO;
import com.payasia.common.dto.EmployeeShortListDTO;
import com.payasia.common.dto.FilePathGeneratorDTO;
import com.payasia.common.dto.HRLetterConditionDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.EmployeeFilterListForm;
import com.payasia.common.form.GeneralFilterDTO;
import com.payasia.common.form.HRLetterForm;
import com.payasia.common.form.HRLetterResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.EmployeeFilterXMLUtil;
import com.payasia.common.util.FileUtils;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.PayAsiaEmailTO;
import com.payasia.common.util.PayAsiaMailUtils;
import com.payasia.common.util.PayAsiaStringUtils;
import com.payasia.common.util.RandomNumberGenerator;
import com.payasia.common.util.ShortlistOperatorEnum;
import com.payasia.common.util.XMLUtil;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.CompanyDocumentDAO;
import com.payasia.dao.CompanyDocumentDetailDAO;
import com.payasia.dao.CompanyDocumentShortListDAO;
import com.payasia.dao.DataDictionaryDAO;
import com.payasia.dao.DocumentCategoryMasterDAO;
import com.payasia.dao.DynamicFormDAO;
import com.payasia.dao.DynamicFormFieldRefValueDAO;
import com.payasia.dao.DynamicFormRecordDAO;
import com.payasia.dao.DynamicFormTableRecordDAO;
import com.payasia.dao.EmailAttachmentDAO;
import com.payasia.dao.EmailDAO;
import com.payasia.dao.EmailPreferenceMasterDAO;
import com.payasia.dao.EmailTemplateDAO;
import com.payasia.dao.EmailTemplateSubCategoryMasterDAO;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.EntityMasterDAO;
import com.payasia.dao.HRISPreferenceDAO;
import com.payasia.dao.HRLetterDAO;
import com.payasia.dao.HRLetterShortlistDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.CompanyDocument;
import com.payasia.dao.bean.CompanyDocumentDetail;
import com.payasia.dao.bean.DataDictionary;
import com.payasia.dao.bean.DocumentCategoryMaster;
import com.payasia.dao.bean.DynamicForm;
import com.payasia.dao.bean.DynamicFormFieldRefValue;
import com.payasia.dao.bean.DynamicFormRecord;
import com.payasia.dao.bean.DynamicFormTableRecord;
import com.payasia.dao.bean.Email;
import com.payasia.dao.bean.EmailAttachment;
import com.payasia.dao.bean.EmailPreferenceMaster;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EntityMaster;
import com.payasia.dao.bean.HRISPreference;
import com.payasia.dao.bean.HRLetter;
import com.payasia.dao.bean.HRLetterShortlist;
import com.payasia.logic.AWSS3Logic;
import com.payasia.logic.EmployeeDetailLogic;
import com.payasia.logic.FiltersInfoUtilsLogic;
import com.payasia.logic.GeneralFilterLogic;
import com.payasia.logic.GeneralLogic;
import com.payasia.logic.GeneralMailLogic;
import com.payasia.logic.HRLetterLogic;

/**
 * The Class HRLetterLogicImpl.
 */
/**
 * @author vivekjain
 * 
 */
@Component
public class HRLetterLogicImpl implements HRLetterLogic {

	/** The Constant logger. */
	private static final Logger LOGGER = Logger
			.getLogger(HRLetterLogicImpl.class);

	/** The payasia temp path. */
	@Value("#{payasiaptProperties['payasia.temp.path']}")
	private String PAYASIA_TEMP_PATH;

	/** The hr letter dao. */
	@Resource
	HRLetterDAO hrLetterDAO;
	@Autowired
	VelocityEngine velocityEngine;
	/** The compnay dao. */
	@Resource
	CompanyDAO compnayDAO;
	@Resource
	GeneralFilterLogic generalFilterLogic;
	/** The pay asia mail utils. */
	@Resource
	PayAsiaMailUtils payAsiaMailUtils;

	/** The employee dao. */
	@Resource
	EmployeeDAO employeeDAO;

	/** The email preference master dao. */
	@Resource
	EmailPreferenceMasterDAO emailPreferenceMasterDAO;

	/** The email template sub category master dao. */
	@Resource
	EmailTemplateSubCategoryMasterDAO emailTemplateSubCategoryMasterDAO;

	/** The email template dao. */
	@Resource
	EmailTemplateDAO emailTemplateDAO;
	@Resource
	CompanyDocumentShortListDAO companyDocumentShortListDAO;
	@Resource
	HRLetterShortlistDAO hrLetterShortlistDAO;
	@Resource
	DataDictionaryDAO dataDictionaryDAO;

	/** The python exe path. */
	@Value("#{payasiaptProperties['payasia.python.exe.path']}")
	private String PYTHON_EXE_PATH;

	/** The document convertor path. */
	@Value("#{payasiaptProperties['payasia.document.converter.path']}")
	private String DOCUMENT_CONVERTOR_PATH;
	@Resource
	DocumentCategoryMasterDAO documentCategoryMasterDAO;
	@Resource
	CompanyDocumentDAO companyDocumentDAO;
	@Resource
	CompanyDocumentDetailDAO companyDocumentDetailDAO;
	@Resource
	AWSS3Logic awss3LogicImpl;

	/** The download path. */
	@Value("#{payasiaptProperties['payasia.home']}")
	private String downloadPath;

	@Value("#{payasiaptProperties['payasia.application.deployment.location']}")
	private String appDeployLocation;

	/** The documents Root Directory Name. */
	@Value("#{payasiaptProperties['payasia.document.root.directory']}")
	private String rootDirectoryName;

	@Resource
	HRISPreferenceDAO hrisPreferenceDAO;
	@Resource
	FiltersInfoUtilsLogic filtersInfoUtilsLogic;
	@Resource
	DynamicFormDAO dynamicFormDAO;

	@Resource
	EntityMasterDAO entityMasterDAO;
	@Resource
	EmailDAO emailDAO;

	@Resource
	EmailAttachmentDAO emailAttachementDAO;
	@Resource
	DynamicFormRecordDAO dynamicFormRecordDAO;
	@Resource
	DynamicFormFieldRefValueDAO dynamicFormFieldRefValueDAO;
	@Resource
	EmployeeDetailLogic employeeDetailLogic;
	@Resource
	DynamicFormTableRecordDAO dynamicFormTableRecordDAO;

	@Resource
	GeneralLogic generalLogic;
	@Resource
	GeneralMailLogic generalMailLogic;

	@Resource
	FileUtils fileUtils;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.HRLetterLogic#getHRLetterList(java.lang.Long,
	 * com.payasia.common.form.PageRequest,
	 * com.payasia.common.form.SortCondition)
	 */
	@Override
	public HRLetterResponse getHRLetterList(Long companyId,
			PageRequest pageDTO, SortCondition sortDTO) {
		List<HRLetterForm> hrLetterFormList = new ArrayList<HRLetterForm>();

		List<HRLetter> hrLetterList = hrLetterDAO.findByCompany(companyId,
				pageDTO, sortDTO);

		for (HRLetter hrLetter : hrLetterList) {
			HRLetterForm hrLetterForm = new HRLetterForm();

			hrLetterForm.setCompanyId(hrLetter.getCompany().getCompanyId());
			hrLetterForm.setLetterDescription(hrLetter.getLetterDesc());
			
			hrLetterForm.setLetterId(FormatPreserveCryptoUtil.encrypt(hrLetter.getHrLetterId()));
			
			hrLetterForm.setAction(true);
			
			String letterName = new String(hrLetter.getLetterName());
			hrLetterForm.setLetterName(letterName);
			hrLetterForm.setBody(hrLetter.getBody());
			hrLetterForm.setSubject(hrLetter.getSubject());
			hrLetterFormList.add(hrLetterForm);
		}
		HRLetterResponse response = new HRLetterResponse();
		int recordSize = hrLetterDAO.getCountHRLetterByCompany(companyId);
		if (pageDTO != null) {

			int pageSize = pageDTO.getPageSize();
			
			//int pageSize =1;
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
		response.setRows(hrLetterFormList);

		return response;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.HRLetterLogic#searchHRLetter(java.lang.Long,
	 * java.lang.String, java.lang.String, com.payasia.common.form.PageRequest,
	 * com.payasia.common.form.SortCondition)
	 */
	@Override
	public HRLetterResponse searchHRLetter(Long companyId, String searchCondition, String searchText, PageRequest pageDTO, SortCondition sortDTO) {
		HRLetterConditionDTO conditionDTO = new HRLetterConditionDTO();

		if (searchCondition.equals(PayAsiaConstants.HR_LETTER_LETTER_NAME) && StringUtils.isNotBlank(searchText)) {
			try {
				conditionDTO.setLetterName("%" + URLDecoder.decode(searchText, "UTF-8") + "%");
			} catch (UnsupportedEncodingException ex) {
				LOGGER.error(ex.getMessage(), ex);
			}
		}
		if (searchCondition.equals(PayAsiaConstants.HR_LETTER_LETTER_DESC) && StringUtils.isNotBlank(searchText)) {
			try {
				conditionDTO.setLetterDescription("%" + URLDecoder.decode(searchText, "UTF-8") + "%");
			} catch (UnsupportedEncodingException ex) {
				LOGGER.error(ex.getMessage(), ex);
			}
		}
		if (searchCondition.equals(PayAsiaConstants.HR_LETTER_LETTER_SUBJECT) && StringUtils.isNotBlank(searchText)) {
			try {
				conditionDTO.setLetterSubject("%" + URLDecoder.decode(searchText, "UTF-8") + "%");
			} catch (UnsupportedEncodingException ex) {
				LOGGER.error(ex.getMessage(), ex);
			}
		}

		List<HRLetterForm> hrLetterFormList = new ArrayList<>();
		List<HRLetter> hrLetterVOList = hrLetterDAO.findByCondition(conditionDTO, companyId, false, pageDTO, sortDTO);
		for (HRLetter hrLetterVO : hrLetterVOList) {
			HRLetterForm hrLetterForm = new HRLetterForm();
			hrLetterForm.setLetterName(hrLetterVO.getLetterName());
			hrLetterForm.setLetterDescription(hrLetterVO.getLetterDesc());
			hrLetterForm.setAction(true);
			hrLetterForm.setSubject(hrLetterVO.getSubject());
			hrLetterForm.setBody(hrLetterVO.getBody());
			
			/*ID ENCRYPT*/
			hrLetterForm.setLetterId(FormatPreserveCryptoUtil.encrypt(hrLetterVO.getHrLetterId()));
			
			if (hrLetterVO.getActive()) {
				hrLetterForm.setIsActive(PayAsiaConstants.PAYASIA_REQUIRED_YES);
			}
			else {
				hrLetterForm.setIsActive(PayAsiaConstants.PAYASIA_REQUIRED_NO);
			}

			List<HRLetterShortlist> hrLetterShortVOlist = hrLetterShortlistDAO.findByCondition(hrLetterVO.getHrLetterId());
			StringBuilder hrLetterShortList = new StringBuilder();

			if (!hrLetterShortVOlist.isEmpty()) {

				hrLetterShortList
						.append("<a class='alink' style='text-decoration: none;' href='#' onClick = 'hrLetterShortListFunc("
								+ hrLetterVO.getHrLetterId()
								+ ")'><span class='ctextgreen'>Set</span></a>");

				hrLetterForm.setShortlist(String.valueOf(hrLetterShortList));
			} else {

				hrLetterShortList
						.append("<a class='alink' style='text-decoration: none;' href='#' onClick = 'hrLetterShortListFunc("
								+ hrLetterVO.getHrLetterId()
								+ ")'><span class='ctextgray'>Not Set</span></a>");
				hrLetterForm.setShortlist(String.valueOf(hrLetterShortList));
			}

			hrLetterFormList.add(hrLetterForm);
		}
		HRLetterResponse response = new HRLetterResponse();
		int recordSize = hrLetterDAO.getCountHRLetterByCondition(conditionDTO, companyId, false);
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
		response.setRows(hrLetterFormList);

		return response;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.HRLetterLogic#saveHRLetter(com.payasia.common.form.
	 * HRLetterForm, java.lang.Long)
	 */
	@Override
	public String saveHRLetter(HRLetterForm hrLetterForm, Long companyId) {
		boolean status = true;
		HRLetter hrLetter = new HRLetter();

		try {
			hrLetter.setLetterName(URLDecoder.decode(
					hrLetterForm.getLetterName(), "UTF-8"));
			hrLetter.setLetterDesc(URLDecoder.decode(
					hrLetterForm.getLetterDescription(), "UTF-8"));
			hrLetter.setBody(URLDecoder.decode(hrLetterForm.getBody(), "UTF-8"));
			hrLetter.setSubject(URLDecoder.decode(hrLetterForm.getSubject(),
					"UTF-8"));
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}

		Company company = compnayDAO.findById(companyId);
		hrLetter.setCompany(company);

		hrLetter.setActive(hrLetterForm.isActive());

		status = checkHRLetter(null, hrLetterForm.getLetterName(), companyId);

		if (status) {
			hrLetterDAO.save(hrLetter);
			return PayAsiaConstants.NOTAVAILABLE;
		} else {
			return PayAsiaConstants.AVAILABLE;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.HRLetterLogic#deleteHRLetter(long)
	 */
	@Override
	public String deleteHRLetter(long letterId,Long companyId) {
		List<HRLetterShortlist> hrLetterShortVOList = hrLetterShortlistDAO
				.findByHRLetterShortlistCondition(letterId,companyId);
		if (!hrLetterShortVOList.isEmpty()) {
			hrLetterShortlistDAO.deleteByCondition(letterId);
		}
		HRLetter hrLetter = hrLetterDAO.findByHRLetterId(letterId,companyId);
		if(hrLetter == null) {
			return PayAsiaConstants.FALSE;
		}
		hrLetterDAO.delete(hrLetter);
		return PayAsiaConstants.PAYASIA_SUCCESS;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.HRLetterLogic#updateHRLetter(com.payasia.common.form
	 * .HRLetterForm, java.lang.Long)
	 */
	@Override
	public String updateHRLetter(HRLetterForm hrLetterForm, Long companyId) {
		boolean status = true;

		HRLetter hrLetter = new HRLetter();

		try {
			hrLetter.setLetterName(URLDecoder.decode(
					hrLetterForm.getLetterName(), "UTF-8"));
			hrLetter.setLetterDesc(URLDecoder.decode(
					hrLetterForm.getLetterDescription(), "UTF-8"));
			hrLetter.setBody(URLDecoder.decode(hrLetterForm.getBody(), "UTF-8"));
			hrLetter.setSubject(URLDecoder.decode(hrLetterForm.getSubject(),
					"UTF-8"));
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}

		hrLetter.setHrLetterId(hrLetterForm.getLetterId());

		Company company = compnayDAO.findById(companyId);
		hrLetter.setCompany(company);
		hrLetter.setActive(hrLetterForm.isActive());

		status = checkHRLetter(hrLetterForm.getLetterId(),
				hrLetterForm.getLetterName(), companyId);

		if (status) {
			hrLetterDAO.update(hrLetter);
			return PayAsiaConstants.NOTAVAILABLE;
		} else {
			return PayAsiaConstants.AVAILABLE;
		}

	}

	/**
	 * Check hr letter.
	 * 
	 * @param letterId
	 *            the letter id
	 * @param letterName
	 *            the letter name
	 * @param companyId
	 *            the company id
	 * @return true, if successful
	 */
	public boolean checkHRLetter(Long letterId, String letterName,
			Long companyId) {
		HRLetter hrLetterVO = hrLetterDAO.findByLetterAndCompany(letterId,
				letterName, companyId);
		if (hrLetterVO == null) {
			return true;
		}
		return false;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.HRLetterLogic#getHRLetter(long)
	 */
	@Override
	public HRLetterForm getHRLetter(long letterId) {
		HRLetterForm hrLetterForm = new HRLetterForm();
		HRLetter hrLetter = hrLetterDAO.findById(letterId);

		hrLetterForm.setActive(hrLetter.getActive());
		hrLetterForm.setCompanyId(hrLetter.getCompany().getCompanyId());
		hrLetterForm.setLetterDescription(hrLetter.getLetterDesc());
		hrLetterForm.setLetterId(hrLetter.getHrLetterId());
		String letterName = new String(hrLetter.getLetterName());
		hrLetterForm.setLetterName(letterName);
		hrLetterForm.setBody(hrLetter.getBody());
		hrLetterForm.setSubject(hrLetter.getSubject());

		return hrLetterForm;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.HRLetterLogic#sendHRLetter(long, long,
	 * java.lang.String)
	 */
	@Override
	public String sendHRLetter(Long companyId, long hrLetterId,
			HRLetterForm hrLetterForm, Long loggedInEmployeeId) {

		File emailTemplate = null;
		FileOutputStream fos = null;
		File emailSubjectTemplate = null;
		FileOutputStream fosSubject = null;

		PayAsiaEmailTO payAsiaEmailTO = new PayAsiaEmailTO();

		HRLetter hrletterVO = hrLetterDAO.findById(hrLetterId);

		if (hrletterVO == null) {
			return "send.hr.letter.configuration.is.not.defined";
		}

		EmailPreferenceMaster emailPreferenceMasterVO = emailPreferenceMasterDAO
				.findByConditionCompany(companyId);
		if (emailPreferenceMasterVO == null) {
			return "email.configuration.is.not.defined";
		}
		if (!StringUtils.isNotBlank(emailPreferenceMasterVO.getContactEmail())) {
			return "email.configuration.contact.email.is.not.defined";
		}

		String mailBody = hrletterVO.getBody();
		String mailSubject = hrletterVO.getSubject();
		Map<String, Object> modelMap = new HashMap<String, Object>();

		try {
			byte[] mailBodyBytes = mailBody.getBytes("UTF-8");
			byte[] mailSubjectBytes = mailSubject.getBytes("UTF-8");
			String uniqueId = RandomNumberGenerator.getNDigitRandomNumber(8);
			emailTemplate = new File(PAYASIA_TEMP_PATH + "//"
					+ "sendHRLetterTemplate" + uniqueId + ".vm");
			emailTemplate.deleteOnExit();
			emailSubjectTemplate = new File(PAYASIA_TEMP_PATH + "//"
					+ "sendHRLetterSubjectTemplate" + "_" + uniqueId + ".vm");
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
			} finally {
				if (fos != null) {
					fos.close();
				}
			}

			if (StringUtils.isNotBlank(hrletterVO.getSubject())) {
				payAsiaEmailTO.setMailSubject(hrletterVO.getSubject());
			}
			if (StringUtils.isNotBlank(hrletterVO.getBody())) {
				payAsiaEmailTO.setMailText(hrletterVO.getBody());
			}
			if (StringUtils.isNotBlank(emailPreferenceMasterVO
					.getContactEmail())) {
				payAsiaEmailTO.setMailFrom(emailPreferenceMasterVO
						.getContactEmail());
			}

			payAsiaMailUtils.sendEmail(
					modelMap,
					emailTemplate.getPath().substring(
							emailTemplate.getParent().length() + 1),
					emailSubjectTemplate.getPath().substring(
							emailSubjectTemplate.getParent().length() + 1),
					true, payAsiaEmailTO);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			if (emailTemplate != null) {
				emailTemplate.delete();

			}

		}
		return "hr.letter.template.is.sent.to.your.email.id";

	}

	@Override
	public List<EmployeeFilterListForm> getEditEmployeeFilterList(Long hrLetterId, Long companyId) {

		List<EmployeeFilterListForm> employeeFilterList = new ArrayList<EmployeeFilterListForm>();

		List<HRLetterShortlist> hrLetterShortVOList = hrLetterShortlistDAO.findByHRLetterShortlistCondition(hrLetterId,
				companyId);
		if (!hrLetterShortVOList.isEmpty()) {
			for (HRLetterShortlist hrLetterShort : hrLetterShortVOList) {
				EmployeeFilterListForm employeeFilterListForm = new EmployeeFilterListForm();
				employeeFilterListForm.setCloseBracket(hrLetterShort.getCloseBracket());
				employeeFilterListForm.setDataDictionaryId(hrLetterShort.getDataDictionary().getDataDictionaryId());
				employeeFilterListForm.setEqualityOperator(hrLetterShort.getEqualityOperator());
				employeeFilterListForm.setFilterId(hrLetterShort.getShort_List_ID());
				employeeFilterListForm.setLogicalOperator(hrLetterShort.getLogicalOperator());
				employeeFilterListForm.setOpenBracket(hrLetterShort.getOpenBracket());
				employeeFilterListForm.setValue(hrLetterShort.getValue());
				employeeFilterListForm.setDataType(generalFilterLogic.getFieldDataType(companyId, hrLetterShort.getDataDictionary()));
				employeeFilterList.add(employeeFilterListForm);
			}
		}

		Collections.sort(employeeFilterList, new EmployeeFilterListFormComp());
		return employeeFilterList;

	}

	private class EmployeeFilterListFormComp implements
			Comparator<EmployeeFilterListForm> {
		public int compare(EmployeeFilterListForm templateField,
				EmployeeFilterListForm compWithTemplateField) {
			if (templateField.getFilterId() > compWithTemplateField
					.getFilterId()) {
				return 1;
			} else if (templateField.getFilterId() < compWithTemplateField
					.getFilterId()) {
				return -1;
			}
			return 0;

		}

	}

	@Override
	public String saveEmployeeFilterList(String metaData, Long hrLetterId) {

		Boolean saveStatus = false;
		Unmarshaller unmarshaller = null;
		try {
			unmarshaller = EmployeeFilterXMLUtil.getDocumentUnmarshaller();
		} catch (JAXBException jAXBException) {

			LOGGER.error(jAXBException.getMessage(), jAXBException);
			throw new PayAsiaSystemException(jAXBException.getMessage(),
					jAXBException);
		} catch (SAXException sAXException) {

			LOGGER.error(sAXException.getMessage(), sAXException);
			throw new PayAsiaSystemException(sAXException.getMessage(),
					sAXException);
		}

		final StringReader xmlReader = new StringReader(metaData);
		Source xmlSource = null;
		try {
			xmlSource = XMLUtil.getSAXSource(xmlReader);
		} catch (SAXException | ParserConfigurationException e1) {
			LOGGER.error(e1.getMessage(), e1);
			throw new PayAsiaSystemException(e1.getMessage(),
					e1);
		}

		EmployeeFilterTemplate empFilterTemplate = null;
		try {
			empFilterTemplate = (EmployeeFilterTemplate) unmarshaller
					.unmarshal(xmlSource);
		} catch (JAXBException jAXBException) {

			LOGGER.error(jAXBException.getMessage(), jAXBException);
			throw new PayAsiaSystemException(jAXBException.getMessage(),
					jAXBException);
		}
		hrLetterShortlistDAO.deleteByCondition(hrLetterId);

		List<EmployeeFilter> listOfFields = empFilterTemplate
				.getEmployeeFilter();
		for (EmployeeFilter field : listOfFields) {
			HRLetterShortlist hrLetterShortlist = new HRLetterShortlist();

			HRLetter hrLetter = hrLetterDAO.findById(hrLetterId);
			hrLetterShortlist.sethRLetter(hrLetter);

			if (StringUtils.isNotBlank(field.getCloseBracket())) {
				hrLetterShortlist.setCloseBracket(field.getCloseBracket());
			}
			if (StringUtils.isNotBlank(field.getOpenBracket())) {
				hrLetterShortlist.setOpenBracket(field.getOpenBracket());

			}
			if (field.getDictionaryId() != 0) {
				DataDictionary dataDictionary = dataDictionaryDAO
						.findById(field.getDictionaryId());
				hrLetterShortlist.setDataDictionary(dataDictionary);
			}

			if (StringUtils.isNotBlank(field.getEqualityOperator())) {
				try {
					String equalityOperator = URLDecoder.decode(
							field.getEqualityOperator(), "UTF-8");
					// Check Valid ShortList Operator
					ShortlistOperatorEnum shortlistOperatorEnum = ShortlistOperatorEnum
							.getFromOperator(equalityOperator);
					if (shortlistOperatorEnum == null) {
						throw new PayAsiaSystemException(
								PayAsiaConstants.PAYASIA_SHORTLIST_OPERATOR_NOT_VALID);
					}
					hrLetterShortlist.setEqualityOperator(equalityOperator);
				} catch (UnsupportedEncodingException unsupportedEncodingException) {
					LOGGER.error(unsupportedEncodingException.getMessage(),
							unsupportedEncodingException);
					throw new PayAsiaSystemException(
							unsupportedEncodingException.getMessage(),
							unsupportedEncodingException);
				}

			}
			if (StringUtils.isNotBlank(field.getLogicalOperator())) {
				hrLetterShortlist
						.setLogicalOperator(field.getLogicalOperator());
			}

			if (StringUtils.isNotBlank(field.getValue())) {
				String fieldValue = "";
				try {
					fieldValue = URLDecoder.decode(field.getValue(), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					LOGGER.error(e.getMessage(), e);
				}
				hrLetterShortlist.setValue(fieldValue);
				saveStatus = true;
			}
			hrLetterShortlistDAO.save(hrLetterShortlist);

		}
		if (saveStatus) {
			return "payasia.company.document.center.shortlist.saved.successfully";
		} else {
			return "0";
		}

	}

	@Override
	public Set<String> custPlaceHolderList(Long companyId, long hrLetterId,
			Long loggedInEmployeeId) {
		Set<String> custFieldList = new HashSet<>();
		HRLetter hrLetter = hrLetterDAO.findByHRLetterId(hrLetterId,companyId);
		String bodyStr = hrLetter.getBody();
		Pattern pattern = Pattern.compile("\\#\\{(.+?)\\}");
		Matcher matcher = pattern.matcher(bodyStr);
		while (matcher.find()) {
			String tempStr = matcher.group();
			tempStr = tempStr.substring(2, tempStr.lastIndexOf('}'));
			custFieldList.add(tempStr);
		}
		return custFieldList;

	}

	public Set<String> getDynamicCustomFieldList(Long companyId, long hrLetterId) {
		Set<String> custFieldList = new HashSet<>();
		HRLetter hrLetter = hrLetterDAO.findById(hrLetterId);
		String bodyStr = hrLetter.getBody();
		Pattern pattern = Pattern.compile("(\\$\\{(.+?)\\})|(\\#\\{(.+?)\\})");
		Matcher matcher = pattern.matcher(bodyStr);
		while (matcher.find()) {
			String tempStr = matcher.group();
			tempStr = tempStr.substring(2, tempStr.lastIndexOf('}'));
			custFieldList.add(tempStr);
		}
		return custFieldList;

	}

	public String convertCustomFieldsInBase64(Long companyId, long hrLetterId) {
		HRLetter hrLetter = hrLetterDAO.findById(hrLetterId);
		String templateBody = hrLetter.getBody();

		List<String> staticFieldList = getStaticFieldsList();

		Pattern placeholderPattern = Pattern.compile("\\$\\{(.+?)\\}");
		Matcher placeholderMatcher = placeholderPattern.matcher(templateBody);
		while (placeholderMatcher.find()) {
			String placeholder = placeholderMatcher.group();
			placeholder = placeholder
					.substring(2, placeholder.lastIndexOf('}'));
			String modifiedPlaceholder = placeholder.replaceAll(
					"\\{[^a-zA-Z0-9]+\\}", "");
			modifiedPlaceholder = "\\$\\{" + modifiedPlaceholder + "\\}";
			if (!staticFieldList.contains(placeholder)) {
				templateBody = templateBody.replaceAll("\\$\\{" + placeholder
						+ "\\}", modifiedPlaceholder);
			}

		}
		return templateBody;

	}

	@Override
	public String previewHRLetterBodyText(Long companyId, long hrLetterId,
			long employeeId, Long loggedInEmpId, String[] userInputKeyArr,
			String[] userInputValArr, boolean isAdmin) {
		HRLetter hrletterVO = hrLetterDAO.findByHRLetterId(hrLetterId,companyId);
		if (hrletterVO == null) {
			return "send.hr.letter.configuration.is.not.defined";
		}
		Company companyVO = compnayDAO.findById(companyId);
		File emailTemplate = null;
		FileOutputStream fos = null;
		PayAsiaEmailTO payAsiaEmailTO = new PayAsiaEmailTO();

		Set<String> custPlaceHolderList = getDynamicCustomFieldList(companyId,
				hrLetterId);
		String mailBody = convertCustomFieldsInBase64(companyId, hrLetterId);

		mailBody = mailBody.replace("#{", "${");
		List<Long> formIdsList = new ArrayList<Long>();
		if (isAdmin) {
			formIdsList = generalLogic.getAdminAuthorizedSectionIdList(
					loggedInEmpId, companyId,
					PayAsiaConstants.EMPLOYEE_ENTITY_ID);

			EmployeeShortListDTO employeeShortListDTO = generalLogic
					.getShortListEmployeeIds(loggedInEmpId, companyId);
			List<BigInteger> companyShortListEmployeeIds = employeeShortListDTO
					.getShortListEmployeeIds();
			if (!companyShortListEmployeeIds.isEmpty()
					&& !companyShortListEmployeeIds.contains(new BigInteger(
							String.valueOf(employeeId)))) {
				return "";
			}
		} else {
			formIdsList = generalLogic.getEmployeeAuthorizedSectionIdList(
					loggedInEmpId, companyId,
					PayAsiaConstants.EMPLOYEE_ENTITY_ID);
		}

		Employee employeeVO = employeeDAO.findById(employeeId);

		Map<String, Object> modelMap = new HashMap<String, Object>();

//		for (int count = 0; count < userInputKeyArr.length; count++) {
//			if (StringUtils.isNotBlank(userInputKeyArr[count])) {
//				try {
//					modelMap.put(
//							URLDecoder.decode(userInputKeyArr[count], "UTF-8"),
//							URLDecoder.decode(userInputValArr[count], "UTF-8"));
//				} catch (UnsupportedEncodingException e) {
//					LOGGER.error(e.getMessage(), e);
//				}
//			}
//		}

		HashMap<Long, DynamicForm> dynamicFormMap = new HashMap<>();
		HashMap<String, ColumnPropertyDTO> staticPropMap = new HashMap<>();
		HashMap<Long, Tab> dataTabMap = new HashMap<>();

		List<String> staticFieldsList = getStaticFieldsList();

		for (String customField : custPlaceHolderList) {
			if (!staticFieldsList.contains(customField)) {
				getCustomFieldPlaceHolderValue(customField, companyId,
						PayAsiaConstants.EMPLOYEE_ENTITY_ID,
						employeeVO.getEmployeeId(), companyVO.getDateFormat(),
						modelMap, dynamicFormMap, staticPropMap, dataTabMap,
						formIdsList);
			}
		}

		addStaticFieldsInMap(companyVO, employeeVO, modelMap);

		String convertedMessage = "";
		try {
			byte[] mailBodyBytes = mailBody.getBytes("UTF-8");
			String uniqueId = RandomNumberGenerator.getNDigitRandomNumber(8);
			emailTemplate = new File(PAYASIA_TEMP_PATH + "//"
					+ "sendHRLetterTemplate" + uniqueId + ".vm");
			emailTemplate.deleteOnExit();

			try {
				fos = new FileOutputStream(emailTemplate);
				fos.write(mailBodyBytes);
				fos.flush();
				fos.close();
			} catch (FileNotFoundException ex) {
				LOGGER.error(ex.getMessage(), ex);
			} catch (IOException ex) {
				LOGGER.error(ex.getMessage(), ex);
			} finally {
				if (fos != null) {
					fos.close();
				}
			}

			if (StringUtils.isNotBlank(hrletterVO.getBody())) {
				payAsiaEmailTO.setMailText(mailBody);
			}

			convertedMessage = payAsiaMailUtils
					.getHRLetterEmailConvertedBodyText(
							modelMap,
							emailTemplate.getPath().substring(
									emailTemplate.getParent().length() + 1),
							payAsiaEmailTO);

		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			if (emailTemplate != null) {
				emailTemplate.delete();
			}
		}
		return convertedMessage;
	}

	private void addStaticFieldsInMap(Company companyVO, Employee employeeVO,
			Map<String, Object> modelMap) {
		modelMap.put("firstName", employeeVO.getFirstName());
		modelMap.put("First_Name", employeeVO.getFirstName());
		modelMap.put("employeeId", employeeVO.getEmployeeNumber());
		modelMap.put("Employee_Number", employeeVO.getEmployeeNumber());
		modelMap.put("userName", employeeVO.getEmployeeLoginDetail()
				.getLoginName());
		modelMap.put("Username", employeeVO.getEmployeeLoginDetail()
				.getLoginName());
		modelMap.put("companyName", employeeVO.getCompany().getCompanyName());
		modelMap.put("Company_Name", employeeVO.getCompany().getCompanyName());
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

		if (employeeVO.getHireDate() != null) {
			modelMap.put("Hire_Date", DateUtils.timeStampToString(
					employeeVO.getHireDate(), companyVO.getDateFormat()));
		}
		if (employeeVO.getHireDate() == null) {
			modelMap.put("Hire_Date", "");
		}
		if (employeeVO.getConfirmationDate() != null) {
			modelMap.put(
					"Confirmation_Date",
					DateUtils.timeStampToString(
							employeeVO.getConfirmationDate(),
							companyVO.getDateFormat()));
		}
		if (employeeVO.getConfirmationDate() == null) {
			modelMap.put("Confirmation_Date", "");
		}
		if (employeeVO.getResignationDate() != null) {
			modelMap.put("Termination_Date", DateUtils.timeStampToString(
					employeeVO.getResignationDate(), companyVO.getDateFormat()));
		}
		if (employeeVO.getResignationDate() == null) {
			modelMap.put("Termination_Date", "");
		}
		if (employeeVO.getOriginalHireDate() != null) {
			modelMap.put(
					"Original_Hire_Date",
					DateUtils.timeStampToString(
							employeeVO.getOriginalHireDate(),
							companyVO.getDateFormat()));
		}
		if (employeeVO.getOriginalHireDate() == null) {
			modelMap.put("Original_Hire_Date", "");
		}

		if (StringUtils.isNotBlank(employeeVO.getEmploymentStatus())) {
			modelMap.put("Employment_Status", employeeVO.getEmploymentStatus());
		}
		if (StringUtils.isBlank(employeeVO.getEmploymentStatus())) {
			modelMap.put("Employment_Status", "");
		}

		modelMap.put("Date_Of_Issue",
				DateUtils.getCurrentDateWithFormat(companyVO.getDateFormat()));
		modelMap.put("date_of_issue",
				DateUtils.getCurrentDateWithFormat(companyVO.getDateFormat()));
		modelMap.put("Date_of_Issue",
				DateUtils.getCurrentDateWithFormat(companyVO.getDateFormat()));
		modelMap.put(PayAsiaConstants.HRIS_EMAIL_EMPLOYEE_NAME,
				generalMailLogic.getEmployeeName(employeeVO));
	}

	private List<String> getStaticFieldsList() {
		List<String> staticFieldsList = new ArrayList<>();
		staticFieldsList.add("firstName");
		staticFieldsList.add("First_Name");
		staticFieldsList.add("employeeId");
		staticFieldsList.add("Employee_Number");
		staticFieldsList.add("userName");
		staticFieldsList.add("Username");
		staticFieldsList.add("companyName");
		staticFieldsList.add("Company_Name");
		staticFieldsList.add("middleName");
		staticFieldsList.add("Middle_Name");
		staticFieldsList.add("lastName");
		staticFieldsList.add("Last_Name");
		staticFieldsList.add("email");
		staticFieldsList.add("Email");
		staticFieldsList.add("Hire_Date");
		staticFieldsList.add("Confirmation_Date");
		staticFieldsList.add("Termination_Date");
		staticFieldsList.add("Original_Hire_Date");
		staticFieldsList.add("Employment_Status");
		staticFieldsList.add("Date_Of_Issue");
		staticFieldsList.add("date_of_issue");
		staticFieldsList.add("Date_of_Issue");
		return staticFieldsList;
	}

	@Override
	public String sendHrLetterWithPDF(Long companyId, long hrLetterId,
			long employeeId, Long loggedInEmployeeId, String hrLetterBodyText,
			String ccEmails, boolean saveInDocCenterCheck) {
		UUID uuid = UUID.randomUUID();
		hrLetterBodyText = new String(Base64.decodeBase64(hrLetterBodyText
				.getBytes()));
//		try {
//			hrLetterBodyText = URLDecoder.decode(hrLetterBodyText, "UTF8");
//		} catch (UnsupportedEncodingException e2) {
//			LOGGER.error(e2.getMessage(), e2);
//		}

		String hrLetterHTMLBody = "<html><head><meta charset='UTF-8' /></head><body>";
		hrLetterHTMLBody += hrLetterBodyText;
		hrLetterHTMLBody += "</body></html>";
		File tempPdfFile = null;
		File tempFolder = new File(PAYASIA_TEMP_PATH + "/hrLetter/" + uuid);
		tempFolder.mkdirs();

		String destFileNameHTML = PAYASIA_TEMP_PATH + "/HRLetter" + uuid
				+ ".html";
		File htmlFile = new File(destFileNameHTML);
		FileWriterWithEncoding fw;
		try {
			fw = new FileWriterWithEncoding(htmlFile.getAbsoluteFile(), "UTF-8");
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(hrLetterHTMLBody);
			bw.close();
		} catch (IOException e1) {
			LOGGER.error(e1);
		}

		String destFileNamePDF = PAYASIA_TEMP_PATH + "/HRLetter" + uuid
				+ ".pdf";

		try {
			ArrayList<String> cmdList = new ArrayList<String>();
			String file1 = destFileNameHTML;
			String file2 = destFileNamePDF;
			// cmdList.add(PYTHON_EXE_PATH);
			cmdList.add(DOCUMENT_CONVERTOR_PATH);
			cmdList.add(file1);
			cmdList.add(file2);

			ProcessBuilder processBuilder = new ProcessBuilder(cmdList);
			LOGGER.info("processBuilder.command()   "
					+ processBuilder.command());
			processBuilder.redirectErrorStream(true);
			Process process = processBuilder.start();
			process.waitFor();
		} catch (IOException e) {
			LOGGER.error(e);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}

		HRLetter hrletterVO = hrLetterDAO.findByHRLetterId(hrLetterId,companyId);
		if (hrletterVO == null) {
			return "send.hr.letter.configuration.is.not.defined";
		}

		EmailPreferenceMaster emailPreferenceMasterVO = emailPreferenceMasterDAO
				.findByConditionCompany(companyId);
		if (emailPreferenceMasterVO == null) {
			return "email.configuration.is.not.defined";
		}
		if (StringUtils.isBlank(emailPreferenceMasterVO.getContactEmail())) {
			return "email.configuration.contact.email.is.not.defined";
		}

		File emailTemplate = null;
		FileOutputStream fos = null;

		Employee employeeVO;
		if (employeeId != 0) {
			employeeVO = employeeDAO.findById(employeeId,companyId);
		} else {
			employeeVO = employeeDAO.findById(loggedInEmployeeId,companyId);
		}

		Map<String, Object> modelMap = new HashMap<String, Object>();
		try {

			String uniqueId = RandomNumberGenerator.getNDigitRandomNumber(8);
			emailTemplate = new File(PAYASIA_TEMP_PATH + "//"
					+ "sendHRLetterTemplate" + uniqueId + ".vm");
			emailTemplate.deleteOnExit();

			try {
				fos = new FileOutputStream(emailTemplate);

				fos.flush();
				fos.close();
			} catch (FileNotFoundException ex) {
				LOGGER.error(ex.getMessage(), ex);
			} catch (IOException ex) {
				LOGGER.error(ex.getMessage(), ex);
			} finally {
				if (fos != null) {
					fos.close();
				}
			}

			Email email = new Email();

			if (StringUtils.isNotBlank(employeeVO.getEmail())) {
				email.setEmailTo(employeeVO.getEmail());
			}
			if (StringUtils.isNotBlank(hrletterVO.getSubject())) {
				email.setSubject(hrletterVO.getSubject());
			}

			String[] ccEmailIds = ccEmails.split(";");
			for (String ccEmailId : ccEmailIds) {
				if (StringUtils.isNotBlank(ccEmailId)) {
					email.setEmailCC(ccEmailId);
				}
			}

			if (StringUtils.isNotBlank(emailPreferenceMasterVO
					.getContactEmail())) {
				email.setEmailFrom(emailPreferenceMasterVO.getContactEmail());
			}

			email.setCreatedDate(DateUtils.getCurrentTimestampWithTime());

			StringBuilder bodyText = new StringBuilder("");
			bodyText.append(VelocityEngineUtils.mergeTemplateIntoString(
					velocityEngine,
					emailTemplate.getPath().substring(
							emailTemplate.getParent().length() + 1), modelMap));

			email.setBody("");

			Company company = new Company();
			company.setCompanyId(companyId);
			email.setCompany(company);
			Email persistEmail = emailDAO.saveReturn(email);

			Path path = Paths.get(destFileNamePDF);
			byte[] dataFile = Files.readAllBytes(path);

			EmailAttachment emailAttachment = new EmailAttachment();
			emailAttachment.setAttachment(dataFile);
			emailAttachment.setEmail(persistEmail);
			emailAttachment.setFileName(hrletterVO.getLetterName() + ".pdf");
			emailAttachementDAO.saveReturn(emailAttachment);

			if (saveInDocCenterCheck) {
				String fileName = employeeVO.getEmployeeNumber() + "_"
						+ hrletterVO.getLetterName() + ".pdf";
				saveEmployeeDocument(employeeVO.getEmployeeNumber(), companyId,
						fileName, destFileNamePDF);
			}
		} catch (Exception e) {
			tempPdfFile = new File(destFileNamePDF);
			if (tempPdfFile != null) {
				tempPdfFile.delete();
			}
			tempFolder.delete();
			htmlFile.delete();
			LOGGER.error(e.getMessage(), e);
			throw new PayAsiaSystemException(e.getMessage(), e);
		} finally {
			tempPdfFile = new File(destFileNamePDF);
			if (tempPdfFile != null) {
				tempPdfFile.delete();
			}
			tempFolder.delete();
			htmlFile.delete();
			if (emailTemplate != null) {
				emailTemplate.delete();
			}
		}
		return "payasia.hr.letters.hrletter.send.successfully";
	}

	private void saveEmployeeDocument(String employeeNumber, Long companyId,
			String fileName, String pdfFilePath) {

		FilePathGeneratorDTO filePathGenerator = fileUtils.getFileCommonPath(
				downloadPath, rootDirectoryName, companyId,
				PayAsiaConstants.COMPANY_DOCUMENT_EMPLOYEE_LETTER, null, null,
				employeeNumber, null,
				PayAsiaConstants.PAYASIA_DOC_UPLOAD_EVENT, 0);
		String destFilePath = fileUtils.getGeneratedFilePath(filePathGenerator);

		/*
		 * String destFilePath = downloadPath + "/company/" + companyId + "/" +
		 * PayAsiaConstants.COMPANY_DOCUMENT_EMPLOYEE_LETTER + "/" +
		 * employeeNumber + "/";
		 */
		if (PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION
				.equalsIgnoreCase(appDeployLocation)) {
			String ext = fileName.substring(fileName.lastIndexOf('.') + 1);
			if (!("").equals(fileName)) {
				fileName = fileName.substring(0, fileName.indexOf('.')) + "_"
						+ UUID.randomUUID() + "." + ext;
				Path path = Paths.get(pdfFilePath);
				try {
					awss3LogicImpl.uploadByteArrayFile(
							Files.readAllBytes(path), destFilePath + fileName);
				} catch (IOException ex) {
					LOGGER.error(ex.getMessage(), ex);
				}
			}
		} else {
			fileName = FileUtils.uploadEmployeeDocFile(pdfFilePath, fileName,
					destFilePath, "_");
		}
		CompanyDocument companyDocument = new CompanyDocument();
		Company company = new Company();
		company.setCompanyId(companyId);
		companyDocument.setCompany(company);

		DocumentCategoryMaster documentCategoryMasterVO = documentCategoryMasterDAO
				.findByCondition(PayAsiaConstants.DOCUMENT_CATEGORY_EMPLOYEE_LETTER);
		companyDocument.setDocumentCategoryMaster(documentCategoryMasterVO);

		companyDocument
				.setUploadedDate(DateUtils.getCurrentTimestampWithTime());

		companyDocument.setDescription(fileName);
		companyDocument
				.setSource(PayAsiaConstants.COMPANY_DOCUMENT_UPLOAD_SOURCE_DOCUMENT);
		CompanyDocument saveReturnObj = companyDocumentDAO
				.save(companyDocument);

		CompanyDocumentDetail companyDocumentDetail = new CompanyDocumentDetail();
		companyDocumentDetail.setCompanyDocument(saveReturnObj);
		companyDocumentDetail.setCompanyId(companyId);
		companyDocumentDetail.setFileType(PayAsiaConstants.FILE_TYPE_PDF);
		companyDocumentDetail.setFileName(fileName);
		companyDocumentDetailDAO.save(companyDocumentDetail);
	}

	@Override
	public HRLetterResponse searchEmployeeHRLetter(Long companyId,
			String searchCondition, String searchText, PageRequest pageDTO,
			SortCondition sortDTO, Long loggedInEmployeeId) {

		HRLetterConditionDTO conditionDTO = new HRLetterConditionDTO();

		if (searchCondition.equals(PayAsiaConstants.HR_LETTER_LETTER_NAME)) {
			if (StringUtils.isNotBlank(searchText)) {

				try {
					conditionDTO.setLetterName("%"
							+ URLDecoder.decode(searchText, "UTF-8") + "%");
				} catch (UnsupportedEncodingException ex) {
					LOGGER.error(ex.getMessage(), ex);
				}
			}

		}
		if (searchCondition.equals(PayAsiaConstants.HR_LETTER_LETTER_DESC)) {
			if (StringUtils.isNotBlank(searchText)) {

				try {
					conditionDTO.setLetterDescription("%"
							+ URLDecoder.decode(searchText, "UTF-8") + "%");
				} catch (UnsupportedEncodingException ex) {
					LOGGER.error(ex.getMessage(), ex);
				}
			}

		}
		if (searchCondition.equals(PayAsiaConstants.HR_LETTER_LETTER_SUBJECT)) {
			if (StringUtils.isNotBlank(searchText)) {

				try {
					conditionDTO.setLetterSubject("%"
							+ URLDecoder.decode(searchText, "UTF-8") + "%");
				} catch (UnsupportedEncodingException ex) {
					LOGGER.error(ex.getMessage(), ex);
				}
			}

		}

		List<HRLetterForm> hrLetterFormList = new ArrayList<HRLetterForm>();
		List<HRLetter> hrLetterVOList = hrLetterDAO.findByCondition(
				conditionDTO, companyId, true, pageDTO, sortDTO);

		List<BigInteger> validHRLetterShortlistIds = getShortListHRLettersIds(
				loggedInEmployeeId, conditionDTO, companyId);

		for (HRLetter hrLetterVO : hrLetterVOList) {

			Integer hrLetterId = Integer.valueOf(String.valueOf(hrLetterVO
					.getHrLetterId()));
			if (!validHRLetterShortlistIds.contains(hrLetterId)) {
				continue;
			}

			HRLetterForm hrLetterForm = new HRLetterForm();
			hrLetterForm.setLetterName(hrLetterVO.getLetterName());
			hrLetterForm.setLetterDescription(hrLetterVO.getLetterDesc());
			hrLetterForm.setSubject(hrLetterVO.getSubject());
			hrLetterForm.setBody(hrLetterVO.getBody());
			hrLetterForm.setAction(true);
			/* ID ENCRYPT*/
			hrLetterForm.setLetterId(FormatPreserveCryptoUtil.encrypt(hrLetterVO.getHrLetterId()));

			hrLetterFormList.add(hrLetterForm);
		}
		HRLetterResponse response = new HRLetterResponse();
		int recordSize = hrLetterDAO.getCountHRLetterByCondition(conditionDTO,
				companyId, true);
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
		response.setRows(hrLetterFormList);

		return response;

	}

	@Override
	public String isSaveHrLetterInDocumentCenter(Long companyId) {

		HRISPreference hrisPreferenceVO = hrisPreferenceDAO
				.findByCompanyId(companyId);
		if (hrisPreferenceVO == null) {
			return PayAsiaConstants.TRUE;
		} else if (hrisPreferenceVO.isSaveHrLetterInDocumentCenter()) {
			return PayAsiaConstants.TRUE;
		} else if (!hrisPreferenceVO.isSaveHrLetterInDocumentCenter()) {
			return PayAsiaConstants.FALSE;
		}
		return PayAsiaConstants.TRUE;

	}

	private int getCountFromParamValueMap(Map<String, String> paramValueMap) {
		int count = 0;

		for (Map.Entry<String, String> entry : paramValueMap.entrySet()) {
			if (entry.getKey().startsWith("sysdateParam")) {
				count++;
			}
			if (entry.getKey().startsWith("param")) {
				count++;
			}
		}

		return count;
	}

	private List<BigInteger> getShortListHRLettersIds(Long employeeId,
			HRLetterConditionDTO hrLetterConditionDTO, Long companyId) {
		List<BigInteger> hrLetterIds = new ArrayList<BigInteger>();
		List<HRLetter> hrLetterVOList = hrLetterDAO.findByCondition(
				hrLetterConditionDTO, companyId, true, null, null);
		Map<String, String> paramValueMap = new LinkedHashMap<String, String>();
		String queryString = "";
		int count = 1;
		for (HRLetter hrLetterVO : hrLetterVOList) {
			List<HRLetterShortlist> hrLetterShortlist = hrLetterShortlistDAO
					.findByCondition(hrLetterVO.getHrLetterId());
			Set<DynamicTableDTO> tableNames = new HashSet<DynamicTableDTO>();
			List<CodeDescDTO> codeDescDTOs = new ArrayList<CodeDescDTO>();
			List<GeneralFilterDTO> finalFilterList = new ArrayList<GeneralFilterDTO>();
			setFilterInfo(hrLetterShortlist, finalFilterList, tableNames,
					codeDescDTOs);

			EntityMaster entityMaster = entityMasterDAO
					.findByEntityName(PayAsiaConstants.EMPLOYEE_ENTITY_NAME);

			List<Long> formIds = dynamicFormDAO.getDistinctFormId(companyId,
					entityMaster.getEntityId());

			int paramValueCount;
			if (!paramValueMap.isEmpty()) {
				paramValueCount = getCountFromParamValueMap(paramValueMap) + 1;
			} else {
				paramValueCount = 1;
			}

			if (hrLetterVOList.size() > 1) {
				queryString += createQuery(employeeId, formIds,
						hrLetterVO.getHrLetterId(), finalFilterList,
						tableNames, codeDescDTOs, companyId, paramValueMap,
						paramValueCount);

				if (count != hrLetterVOList.size()) {
					queryString += " UNION ";
				}

			} else {
				queryString = createQuery(employeeId, formIds,
						hrLetterVO.getHrLetterId(), finalFilterList,
						tableNames, codeDescDTOs, companyId, paramValueMap,
						paramValueCount);
			}
			count++;
		}

		if (!hrLetterVOList.isEmpty()) {
			hrLetterIds.addAll(employeeDAO.checkForEmployeeDocuments(
					queryString, paramValueMap, employeeId, null));
		}

		return hrLetterIds;
	}

	private String createQuery(Long employeeId, List<Long> formIds,
			Long hrLetterId, List<GeneralFilterDTO> finalFilterList,
			Set<DynamicTableDTO> tableNames, List<CodeDescDTO> codeDescDTOs,
			Long companyId, Map<String, String> paramValueMap,
			int paramValueCount) {
		String queryString = "";
		String select = "SELECT " + hrLetterId + " AS [HRLetter ID] ";
		StringBuilder fromBuilder = new StringBuilder(
				" FROM Employee AS employee ");
		for (Long formId : formIds) {

			fromBuilder
					.append("LEFT OUTER JOIN Dynamic_Form_Record AS dynamicFormRecord")
					.append(formId).append(" ")
					.append("ON (employee.Employee_ID = dynamicFormRecord")
					.append(formId)
					.append(".Entity_Key) AND (dynamicFormRecord")
					.append(formId).append(".Form_ID = ").append(formId)
					.append(" ) ");

		}

		for (DynamicTableDTO dynamicTableDTO : tableNames) {

			String dynamicFormTableRecord = new StringBuilder(
					"dynamicFormTableRecordT")
					.append(dynamicTableDTO.getFormId())
					.append(dynamicTableDTO.getTableName()).toString();

			fromBuilder.append("LEFT OUTER JOIN Dynamic_Form_Table_Record AS ")
					.append(dynamicFormTableRecord).append(" ")
					.append("ON (dynamicFormRecord")
					.append(dynamicTableDTO.getFormId()).append(".Col_")
					.append(dynamicTableDTO.getTableName()).append(" = CAST(")
					.append(dynamicFormTableRecord)
					.append(".Dynamic_Form_Table_Record_ID AS varchar(255))) ");
		}

		List<String> codeAliasList = new ArrayList<>();

		for (CodeDescDTO codeDescDTO : codeDescDTOs) {

			if (codeDescDTO.isChildVal()) {
				for (DynamicTableDTO dynamicTableDTO : tableNames) {

					String dynamicFormTableRecord = new StringBuilder(
							"dynamicFormTableRecordT")
							.append(dynamicTableDTO.getFormId())
							.append(dynamicTableDTO.getTableName()).toString();

					String dynamicFormFieldRefValueT = new StringBuilder(
							"dynamicFormFieldRefValueT")
							.append(codeDescDTO.getFormId())
							.append(codeDescDTO.getMethodName()).toString();
					if (!codeAliasList.contains(dynamicFormFieldRefValueT)) {

						fromBuilder
								.append("LEFT OUTER JOIN Dynamic_Form_Field_Ref_Value AS ")
								.append(dynamicFormFieldRefValueT)
								.append(" ")
								.append("ON (")
								.append(dynamicFormTableRecord)
								.append(".")
								.append(codeDescDTO.getMethodName())
								.append(" = CAST(")
								.append(dynamicFormFieldRefValueT)
								.append(".Field_Ref_Value_ID AS varchar(255))) ");
						codeAliasList.add(dynamicFormFieldRefValueT);
					}

				}

			} else {

				String dynamicFormFieldRefValueT = new StringBuilder(
						"dynamicFormFieldRefValue")
						.append(codeDescDTO.getFormId())
						.append(codeDescDTO.getMethodName()).toString();

				if (!codeAliasList.contains(dynamicFormFieldRefValueT)) {
					fromBuilder
							.append("LEFT OUTER JOIN Dynamic_Form_Field_Ref_Value AS ")
							.append(dynamicFormFieldRefValueT).append(" ")
							.append("ON (dynamicFormRecord")
							.append(codeDescDTO.getFormId()).append(".")
							.append(codeDescDTO.getMethodName())
							.append(" = CAST(")
							.append(dynamicFormFieldRefValueT)
							.append(".Field_Ref_Value_ID AS varchar(255))) ");
					codeAliasList.add(dynamicFormFieldRefValueT);
				}

			}

		}

		String where = generalFilterLogic.createWhereWithCount(employeeId,
				finalFilterList, companyId, paramValueMap, paramValueCount);
		queryString = select + fromBuilder.toString() + where;
		return queryString;
	}

	private void setFilterInfo(List<HRLetterShortlist> hrLetterShortlist,
			List<GeneralFilterDTO> finalFilterList,
			Set<DynamicTableDTO> tableNames, List<CodeDescDTO> codeDescDTOs) {
		for (HRLetterShortlist hrLetterShortlistVO : hrLetterShortlist) {
			DataImportKeyValueDTO dataImportKeyValueDTO = new DataImportKeyValueDTO();

			if (hrLetterShortlistVO.getDataDictionary().getFieldType()
					.equalsIgnoreCase(PayAsiaConstants.STATIC_TYPE)) {

				filtersInfoUtilsLogic.getStaticDictionaryInfo(
						hrLetterShortlistVO.getDataDictionary(),
						dataImportKeyValueDTO);

			} else {

				filtersInfoUtilsLogic.getDynamicDictionaryInfo(
						hrLetterShortlistVO.getDataDictionary(),
						dataImportKeyValueDTO, tableNames, codeDescDTOs);

			}
			GeneralFilterDTO generalFilterDTO = new GeneralFilterDTO();

			if (hrLetterShortlistVO.getCloseBracket() != null) {
				generalFilterDTO.setCloseBracket(hrLetterShortlistVO
						.getCloseBracket());
			} else {
				generalFilterDTO.setCloseBracket("");
			}

			if (hrLetterShortlistVO.getOpenBracket() != null) {
				generalFilterDTO.setOpenBracket(hrLetterShortlistVO
						.getOpenBracket());
			} else {
				generalFilterDTO.setOpenBracket("");
			}

			generalFilterDTO.setDataDictionaryName(hrLetterShortlistVO
					.getDataDictionary().getDataDictName());
			generalFilterDTO.setDataImportKeyValueDTO(dataImportKeyValueDTO);
			generalFilterDTO.setDictionaryId(hrLetterShortlistVO
					.getDataDictionary().getDataDictionaryId());
			generalFilterDTO.setEqualityOperator(hrLetterShortlistVO
					.getEqualityOperator());
			generalFilterDTO
					.setFilterId(hrLetterShortlistVO.getShort_List_ID());
			generalFilterDTO.setLogicalOperator(hrLetterShortlistVO
					.getLogicalOperator());
			generalFilterDTO.setValue(hrLetterShortlistVO.getValue());

			finalFilterList.add(generalFilterDTO);
		}

	}

	private Map<String, Object> getCustomFieldPlaceHolderValue(
			String dataDictName, Long companyId, Long entityId,
			Long employeeId, String companyDateFormat,
			Map<String, Object> modelMap,
			HashMap<Long, DynamicForm> dynamicFormMap,
			HashMap<String, ColumnPropertyDTO> staticPropMap,
			HashMap<Long, Tab> dataTabMap, List<Long> formIdsList) {
		String modifiedDataDictName = dataDictName.replace("_", " ");
		dataDictName = dataDictName.replaceAll("\\{[^a-zA-Z0-9]+\\}", "");

		DataDictionary dataDictionary = dataDictionaryDAO.findByDictionaryName(
				companyId, entityId, modifiedDataDictName);
		Employee employeeVO = employeeDAO.findById(employeeId);
		if (dataDictionary != null) {
			if (!formIdsList.isEmpty()
					&& !formIdsList.contains(dataDictionary.getFormID())) {
				modelMap.put(dataDictName, "");
				return modelMap;
			}
			DynamicForm dynamicForm = null;
			if (dynamicFormMap.containsKey(dataDictionary.getFormID())) {
				dynamicForm = dynamicFormMap.get(dataDictionary.getFormID());
			} else {
				dynamicForm = dynamicFormDAO.findMaxVersionByFormId(companyId,
						dataDictionary.getEntityMaster().getEntityId(),
						dataDictionary.getFormID());
				dynamicFormMap
						.put(dynamicForm.getId().getFormId(), dynamicForm);
			}

			if (dynamicForm != null) {
				Unmarshaller unmarshallerRef = null;
				try {
					unmarshallerRef = XMLUtil.getDocumentUnmarshaller();
				} catch (JAXBException jAXBException) {
					LOGGER.error(jAXBException.getMessage(), jAXBException);
					throw new PayAsiaSystemException(
							jAXBException.getMessage(), jAXBException);
				} catch (SAXException sAXException) {

					LOGGER.error(sAXException.getMessage(), sAXException);
					throw new PayAsiaSystemException(sAXException.getMessage(),
							sAXException);
				}
				final StringReader xmlRefReader = new StringReader(
						dynamicForm.getMetaData());
				Source xmlSource = null;
				try {
					xmlSource = XMLUtil.getSAXSource(xmlRefReader);
				} catch (SAXException | ParserConfigurationException e1) {
					LOGGER.error(e1.getMessage(), e1);
					throw new PayAsiaSystemException(e1.getMessage(),
							e1);
				}

				Tab tabRef = null;
				try {
					tabRef = (Tab) unmarshallerRef.unmarshal(xmlSource);
				} catch (JAXBException jAXBException) {
					LOGGER.error(jAXBException.getMessage(), jAXBException);
					throw new PayAsiaSystemException(
							jAXBException.getMessage(), jAXBException);
				}
				DynamicFormRecord dynamicFormRecord = dynamicFormRecordDAO
						.getEmpRecords(employeeId, dynamicForm.getId()
								.getVersion(), dynamicForm.getId().getFormId(),
								dynamicForm.getEntityMaster().getEntityId(),
								companyId);
				List<Field> listOfRefFields = tabRef.getField();
				for (Field fieldR : listOfRefFields) {
					String fieldRName = fieldR.getName();
					String fieldRtype = fieldR.getType();
					if (dynamicFormRecord != null) {

						if (fieldRtype
								.equalsIgnoreCase(PayAsiaConstants.TABLE_FIELD_TYPE)) {
							String colValue = getColValueFile(
									PayAsiaStringUtils.getColNumber(fieldR
											.getName()), dynamicFormRecord);
							if (StringUtils.isNotBlank(colValue)) {
								List<DynamicFormTableRecord> dynamicFormTableRecords = dynamicFormTableRecordDAO
										.getTableRecords(
												Long.parseLong(colValue),
												PayAsiaConstants.SORT_ORDER_OLDEST_TO_NEWEST,
												"");
								List<Date> effdateList = new ArrayList<Date>();
								SimpleDateFormat formatter = new SimpleDateFormat(
										"yyyy/MM/dd");
								for (DynamicFormTableRecord dynamicFormTableRecord : dynamicFormTableRecords) {
									String effDateString = dynamicFormTableRecord
											.getCol1().substring(0, 10);
									Date effDate;
									try {
										effDate = formatter
												.parse(effDateString);
										effdateList.add(effDate);
									} catch (ParseException e) {
									}

								}
								Comparator<Date> comparator = Collections
										.reverseOrder();
								Collections.sort(effdateList, comparator);
								Date currentDate = DateUtils
										.getCurrentTimestamp();
								Date requiredEffdate = null;
								for (Date effectiveDate : effdateList) {
									if (!currentDate.before(effectiveDate)) {
										requiredEffdate = effectiveDate;
										break;
									}
								}
								List<Column> columnList = fieldR.getColumn();
								for (Column column : columnList) {
									if (column.getDictionaryId().equals(
											dataDictionary
													.getDataDictionaryId())) {
										for (DynamicFormTableRecord dynamicFormTableRecord : dynamicFormTableRecords) {
											String effDateString = dynamicFormTableRecord
													.getCol1().substring(0, 10);
											Date effDate = null;
											try {
												effDate = formatter
														.parse(effDateString);
											} catch (ParseException e) {
											}
											if (requiredEffdate != null) {
												if (requiredEffdate
														.equals(effDate)) {
													if (column
															.getType()
															.equalsIgnoreCase(
																	PayAsiaConstants.FIELD_TYPE_CALCULATORY_FIELD)) {
														String tableFieldval;
														try {
															tableFieldval = employeeDetailLogic
																	.getFormulaFieldCalculatedValue(
																			dynamicFormMap,
																			dataTabMap,
																			staticPropMap,
																			column.getFormula(),
																			column.getFormulaType(),
																			dataDictionary,
																			employeeId,
																			companyId,
																			dynamicFormTableRecord
																					.getId()
																					.getDynamicFormTableRecordId(),
																			dynamicFormTableRecord
																					.getId()
																					.getSequence());
														} catch (
																NoSuchMethodException
																| IllegalAccessException
																| InvocationTargetException e) {
															LOGGER.error(
																	e.getMessage(),
																	e);
															throw new PayAsiaSystemException(
																	e.getMessage(),
																	e);
														}
														if (StringUtils
																.isNotBlank(tableFieldval)
																&& !tableFieldval
																		.equalsIgnoreCase("null")) {
															modelMap.put(
																	dataDictName,
																	tableFieldval);
														}
													} else if (column
															.getType()
															.equalsIgnoreCase(
																	PayAsiaConstants.EMPLOYEE_lIST_REFERENCE)) {
														String tableValue = getColValueOfDynamicTableRecord(
																PayAsiaStringUtils
																		.getColNumber(column
																				.getName()),
																dynamicFormTableRecord);
														Employee employee = employeeDAO
																.findById(Long
																		.parseLong(tableValue));

														String tableFieldval = employeeDetailLogic
																.getEmployeeName(employee);

														if (StringUtils
																.isNotBlank(tableFieldval)
																&& !tableFieldval
																		.equalsIgnoreCase("null")) {
															modelMap.put(
																	dataDictName,
																	tableFieldval);
														}

													} else if (column
															.getType()
															.equalsIgnoreCase(
																	PayAsiaConstants.FIELD_TYPE_REFERENCE_FIELD)) {
														String tableValue = getTableReferenceFieldValue(
																modifiedDataDictName,
																companyId,
																column,
																companyDateFormat,
																employeeVO,
																modelMap,
																dynamicFormMap,
																dynamicFormTableRecord
																		.getId()
																		.getDynamicFormTableRecordId(),
																dynamicFormTableRecord
																		.getId()
																		.getSequence());
														if (StringUtils
																.isNotBlank(tableValue)
																&& !tableValue
																		.equalsIgnoreCase("null")) {
															modelMap.put(
																	dataDictName,
																	tableValue);
														}

													} else {
														String tableValue = getColValueOfDynamicTableRecord(
																PayAsiaStringUtils
																		.getColNumber(column
																				.getName()),
																dynamicFormTableRecord);

														if (column
																.getType()
																.equalsIgnoreCase(
																		PayAsiaConstants.CODEDESC_FIELD_TYPE)) {
															DynamicFormFieldRefValue codeDescField = dynamicFormFieldRefValueDAO
																	.findById(Long
																			.parseLong(tableValue));
															if (codeDescField != null) {
																tableValue = codeDescField
																		.getDescription()
																		+ " ["
																		+ codeDescField
																				.getCode()
																		+ "]";
															} else {
																tableValue = "";
															}

														}

														if (StringUtils
																.isNotBlank(tableValue)
																&& !tableValue
																		.equalsIgnoreCase("null")) {
															modelMap.put(
																	dataDictName,
																	tableValue);
														}

													}

												}
											}

										}
									}

								}

							}

						} else {

							if (fieldR.getDictionaryId().equals(
									dataDictionary.getDataDictionaryId())) {

								Class<?> dynamicFormRecordClass = dynamicFormRecord
										.getClass();

								String getMethodName = "getCol"
										+ fieldRName
												.substring(fieldRName
														.lastIndexOf("_") + 1,
														fieldRName.length());
								String fieldRValue;
								try {
									Method dynamicFormRecordMethod = dynamicFormRecordClass
											.getMethod(getMethodName);
									try {
										fieldRValue = (String) dynamicFormRecordMethod
												.invoke(dynamicFormRecord);
									} catch (IllegalAccessException e) {
										LOGGER.error(e.getMessage(), e);
										throw new PayAsiaSystemException(
												e.getMessage(), e);
									} catch (IllegalArgumentException e) {
										LOGGER.error(e.getMessage(), e);
										throw new PayAsiaSystemException(
												e.getMessage(), e);
									} catch (InvocationTargetException e) {
										LOGGER.error(e.getMessage(), e);
										throw new PayAsiaSystemException(
												e.getMessage(), e);
									}
								} catch (NoSuchMethodException e) {
									LOGGER.error(e.getMessage(), e);
									throw new PayAsiaSystemException(
											e.getMessage(), e);
								} catch (SecurityException e) {
									LOGGER.error(e.getMessage(), e);
									throw new PayAsiaSystemException(
											e.getMessage(), e);
								}

								if (fieldRtype
										.equalsIgnoreCase(PayAsiaConstants.FIELD_TYPE_DATE)
										&& !fieldRValue.equalsIgnoreCase("")) {
									fieldRValue = DateUtils
											.convertDateToSpecificFormat(
													fieldRValue,
													companyDateFormat);
								}
								if (fieldRtype
										.equalsIgnoreCase(PayAsiaConstants.CODEDESC_FIELD_TYPE)
										&& StringUtils.isNotBlank(fieldRValue)) {
									DynamicFormFieldRefValue dynamicFormFieldRefValue = dynamicFormFieldRefValueDAO
											.findById(Long
													.parseLong(fieldRValue));
									if (dynamicFormFieldRefValue != null) {
										try {
											String codeDescription = dynamicFormFieldRefValue
													.getDescription()
													+ "["
													+ dynamicFormFieldRefValue
															.getCode() + "]";
											fieldRValue = codeDescription;
										} catch (Exception exception) {
											LOGGER.error(
													exception.getMessage(),
													exception);
											fieldRValue = "";
										}
									} else {
										fieldRValue = "";
									}

								}
								if (fieldRtype
										.equalsIgnoreCase(PayAsiaConstants.FIELD_TYPE_REFERENCE_FIELD)) {
									fieldRValue = getReferenceFieldValue(
											modifiedDataDictName, companyId,
											fieldR, companyDateFormat,
											employeeVO, modelMap,
											dynamicFormMap);
								}
								if (fieldRtype
										.equalsIgnoreCase(PayAsiaConstants.EMPLOYEE_lIST_REFERENCE)) {
									Employee employee = employeeDAO
											.findById(Long
													.parseLong(fieldRValue));

									fieldRValue = employeeDetailLogic
											.getEmployeeName(employee);
								}
								if (fieldRtype
										.equalsIgnoreCase(PayAsiaConstants.FIELD_TYPE_CALCULATORY_FIELD)) {
									try {
										fieldRValue = employeeDetailLogic
												.getFormulaFieldCalculatedValue(
														dynamicFormMap,
														dataTabMap,
														staticPropMap,
														fieldR.getFormula(),
														fieldR.getFormulaType(),
														dataDictionary,
														employeeId, companyId,
														null, 0);
									} catch (NoSuchMethodException
											| IllegalAccessException
											| InvocationTargetException e) {
										LOGGER.error(e.getMessage(), e);
										throw new PayAsiaSystemException(
												e.getMessage(), e);
									}
								}

								if (StringUtils.isNotBlank(fieldRValue)
										&& !fieldRValue
												.equalsIgnoreCase("null")) {
									modelMap.put(dataDictName, fieldRValue);
								}

							}
						}
					}
				}

			}

		}

		Object fieldValue = modelMap.get(dataDictName);
		if (fieldValue == null) {
			modelMap.put(dataDictName, "");
		}
		return modelMap;

	}

	private String getReferenceFieldValue(String dataDictName, Long companyId,
			Field field, String companyDateFormat, Employee employeeVO,
			Map<String, Object> modelMap,
			HashMap<Long, DynamicForm> dynamicFormMap) {
		Long fieldReferencedId = field.getReferenced();

		DataDictionary refDataDictionary = dataDictionaryDAO
				.findById(fieldReferencedId);
		String fieldRValue = null;
		if (refDataDictionary.getFieldType().equalsIgnoreCase(
				PayAsiaConstants.STATIC_TYPE)) {
			Class<?> employeeClass = employeeVO.getClass();

			String getMethodName = "get"
					+ refDataDictionary.getColumnName().replace("_", "");
			Method employeeMethod = null;
			try {
				employeeMethod = employeeClass.getMethod(getMethodName);
			} catch (NoSuchMethodException e) {
				LOGGER.error(e.getMessage(), e);
				throw new PayAsiaSystemException(e.getMessage(), e);
			} catch (SecurityException e) {
				LOGGER.error(e.getMessage(), e);
				throw new PayAsiaSystemException(e.getMessage(), e);
			}

			try {
				Object obj = employeeMethod.invoke(employeeVO);
				if (obj instanceof Timestamp) {
					fieldRValue = String.valueOf(DateUtils.timeStampToString(
							(Timestamp) employeeMethod.invoke(employeeVO),
							companyDateFormat));
				} else {
					fieldRValue = String.valueOf(employeeMethod
							.invoke(employeeVO));
				}

			} catch (IllegalAccessException e) {
				LOGGER.error(e.getMessage(), e);
				throw new PayAsiaSystemException(e.getMessage(), e);
			} catch (IllegalArgumentException e) {
				LOGGER.error(e.getMessage(), e);
				throw new PayAsiaSystemException(e.getMessage(), e);
			} catch (InvocationTargetException e) {
				LOGGER.error(e.getMessage(), e);
				throw new PayAsiaSystemException(e.getMessage(), e);
			}

		} else {
			DynamicForm dynamicForm = null;
			if (dynamicFormMap.containsKey(refDataDictionary.getFormID())) {
				dynamicForm = dynamicFormMap.get(refDataDictionary.getFormID());
			} else {
				dynamicForm = dynamicFormDAO.findMaxVersionByFormId(companyId,
						refDataDictionary.getEntityMaster().getEntityId(),
						refDataDictionary.getFormID());
				dynamicFormMap
						.put(dynamicForm.getId().getFormId(), dynamicForm);
			}

			if (dynamicForm != null) {
				Unmarshaller unmarshallerRef = null;
				try {
					unmarshallerRef = XMLUtil.getDocumentUnmarshaller();
				} catch (JAXBException jAXBException) {
					LOGGER.error(jAXBException.getMessage(), jAXBException);
					throw new PayAsiaSystemException(
							jAXBException.getMessage(), jAXBException);
				} catch (SAXException sAXException) {

					LOGGER.error(sAXException.getMessage(), sAXException);
					throw new PayAsiaSystemException(sAXException.getMessage(),
							sAXException);
				}
				final StringReader xmlRefReader = new StringReader(
						dynamicForm.getMetaData());
				Source xmlRefSource = null;
				try {
					xmlRefSource = XMLUtil.getSAXSource(xmlRefReader);
				} catch (SAXException | ParserConfigurationException e1) {
					LOGGER.error(e1.getMessage(), e1);
					throw new PayAsiaSystemException(e1.getMessage(),
							e1);
				}
				Tab tabRef = null;
				try {
					tabRef = (Tab) unmarshallerRef.unmarshal(xmlRefSource);
				} catch (JAXBException jAXBException) {
					LOGGER.error(jAXBException.getMessage(), jAXBException);
					throw new PayAsiaSystemException(
							jAXBException.getMessage(), jAXBException);
				}

				DynamicFormRecord dynamicFormRecord = dynamicFormRecordDAO
						.getEmpRecords(employeeVO.getEmployeeId(), dynamicForm
								.getId().getVersion(), dynamicForm.getId()
								.getFormId(), dynamicForm.getEntityMaster()
								.getEntityId(), companyId);
				if (dynamicFormRecord != null) {
					Class<?> dynamicFormRecordClass = dynamicFormRecord
							.getClass();

					List<Field> listOfRefFields = tabRef.getField();
					for (Field fieldR : listOfRefFields) {
						if (fieldR.getDictionaryId().equals(
								refDataDictionary.getDataDictionaryId())) {
							String fieldRName = fieldR.getName();
							String fieldRtype = fieldR.getType();

							String getMethodName = "getCol"
									+ fieldRName.substring(
											fieldRName.lastIndexOf("_") + 1,
											fieldRName.length());

							try {
								Method dynamicFormRecordMethod = dynamicFormRecordClass
										.getMethod(getMethodName);
								try {
									fieldRValue = (String) dynamicFormRecordMethod
											.invoke(dynamicFormRecord);
								} catch (IllegalAccessException e) {
									LOGGER.error(e.getMessage(), e);
									throw new PayAsiaSystemException(
											e.getMessage(), e);
								} catch (IllegalArgumentException e) {
									LOGGER.error(e.getMessage(), e);
									throw new PayAsiaSystemException(
											e.getMessage(), e);
								} catch (InvocationTargetException e) {
									LOGGER.error(e.getMessage(), e);
									throw new PayAsiaSystemException(
											e.getMessage(), e);
								}
							} catch (NoSuchMethodException e) {
								LOGGER.error(e.getMessage(), e);
								throw new PayAsiaSystemException(
										e.getMessage(), e);
							} catch (SecurityException e) {
								LOGGER.error(e.getMessage(), e);
								throw new PayAsiaSystemException(
										e.getMessage(), e);
							}

							if (fieldRtype
									.equalsIgnoreCase(PayAsiaConstants.FIELD_TYPE_DATE)
									&& !fieldRValue.equalsIgnoreCase("")) {
								fieldRValue = DateUtils
										.convertDateToSpecificFormat(
												fieldRValue, companyDateFormat);
							}
							if (fieldRtype
									.equalsIgnoreCase(PayAsiaConstants.CODEDESC_FIELD_TYPE)
									&& StringUtils.isNotBlank(fieldRValue)) {
								DynamicFormFieldRefValue dynamicFormFieldRefValue = dynamicFormFieldRefValueDAO
										.findById(Long.parseLong(fieldRValue));

								try {
									String codeDescription = dynamicFormFieldRefValue
											.getDescription()
											+ "["
											+ dynamicFormFieldRefValue
													.getCode() + "]";
									fieldRValue = codeDescription;
								} catch (Exception exception) {
									LOGGER.error(exception.getMessage(),
											exception);
									fieldRValue = "";
								}

							}

						}
					}
				}

			}

		}
		return fieldRValue;

	}

	private String getTableReferenceFieldValue(String dataDictName,
			Long companyId, Column column, String companyDateFormat,
			Employee employeeVO, Map<String, Object> modelMap,
			HashMap<Long, DynamicForm> dynamicFormMap, Long tableId, int seqNo) {

		Long fieldReferencedId = column.getReferenced();
		String fieldRValue = null;
		DataDictionary refTableDataDictionary = dataDictionaryDAO
				.findById(fieldReferencedId);
		if (refTableDataDictionary.getFieldType().equalsIgnoreCase(
				PayAsiaConstants.STATIC_TYPE)) {
			Class<?> employeeClass = employeeVO.getClass();

			String getMethodName = "get"
					+ refTableDataDictionary.getColumnName().replace("_", "");
			Method employeeMethod = null;
			try {
				employeeMethod = employeeClass.getMethod(getMethodName);
			} catch (NoSuchMethodException e) {
				LOGGER.error(e.getMessage(), e);
				throw new PayAsiaSystemException(e.getMessage(), e);
			} catch (SecurityException e) {
				LOGGER.error(e.getMessage(), e);
				throw new PayAsiaSystemException(e.getMessage(), e);
			}
			try {
				Object obj = employeeMethod.invoke(employeeVO);
				if (obj instanceof Timestamp) {
					fieldRValue = String.valueOf(DateUtils.timeStampToString(
							(Timestamp) employeeMethod.invoke(employeeVO),
							companyDateFormat));
				} else {
					fieldRValue = String.valueOf(employeeMethod
							.invoke(employeeVO));
				}

			} catch (IllegalAccessException e) {
				LOGGER.error(e.getMessage(), e);
				throw new PayAsiaSystemException(e.getMessage(), e);
			} catch (IllegalArgumentException e) {
				LOGGER.error(e.getMessage(), e);
				throw new PayAsiaSystemException(e.getMessage(), e);
			} catch (InvocationTargetException e) {
				LOGGER.error(e.getMessage(), e);
				throw new PayAsiaSystemException(e.getMessage(), e);
			}

		} else {
			DynamicForm dynamicForm = null;
			if (dynamicFormMap.containsKey(refTableDataDictionary.getFormID())) {
				dynamicForm = dynamicFormMap.get(refTableDataDictionary
						.getFormID());
			} else {
				dynamicForm = dynamicFormDAO.findMaxVersionByFormId(companyId,
						refTableDataDictionary.getEntityMaster().getEntityId(),
						refTableDataDictionary.getFormID());
				dynamicFormMap
						.put(dynamicForm.getId().getFormId(), dynamicForm);
			}

			if (dynamicForm != null) {
				Unmarshaller unmarshallerRef = null;
				try {
					unmarshallerRef = XMLUtil.getDocumentUnmarshaller();
				} catch (JAXBException jAXBException) {
					LOGGER.error(jAXBException.getMessage(), jAXBException);
					throw new PayAsiaSystemException(
							jAXBException.getMessage(), jAXBException);
				} catch (SAXException sAXException) {

					LOGGER.error(sAXException.getMessage(), sAXException);
					throw new PayAsiaSystemException(sAXException.getMessage(),
							sAXException);
				}
				final StringReader xmlRefReader = new StringReader(
						dynamicForm.getMetaData());
				Source xmlRefSource = null;
				try {
					xmlRefSource = XMLUtil.getSAXSource(xmlRefReader);
				} catch (SAXException | ParserConfigurationException e1) {
					LOGGER.error(e1.getMessage(), e1);
					throw new PayAsiaSystemException(e1.getMessage(),
							e1);
				}

				Tab tabRef = null;
				try {
					tabRef = (Tab) unmarshallerRef.unmarshal(xmlRefSource);
				} catch (JAXBException jAXBException) {
					LOGGER.error(jAXBException.getMessage(), jAXBException);
					throw new PayAsiaSystemException(
							jAXBException.getMessage(), jAXBException);
				}
				DynamicFormRecord dynamicFormRecord = dynamicFormRecordDAO
						.getEmpRecords(employeeVO.getEmployeeId(), dynamicForm
								.getId().getVersion(), dynamicForm.getId()
								.getFormId(), dynamicForm.getEntityMaster()
								.getEntityId(), companyId);

				List<Field> listOfRefFields = tabRef.getField();
				for (Field fieldR : listOfRefFields) {
					if (!fieldR.getType().equalsIgnoreCase(
							PayAsiaConstants.TABLE_FIELD_TYPE)) {
						if (dynamicFormRecord != null) {
							Class<?> dynamicFormRecordClass = dynamicFormRecord
									.getClass();
							if (fieldR.getDictionaryId().equals(
									refTableDataDictionary
											.getDataDictionaryId())) {
								String fieldRName = fieldR.getName();
								String fieldRtype = fieldR.getType();

								String getMethodName = "getCol"
										+ fieldRName
												.substring(fieldRName
														.lastIndexOf("_") + 1,
														fieldRName.length());

								try {
									Method dynamicFormRecordMethod = dynamicFormRecordClass
											.getMethod(getMethodName);
									try {
										fieldRValue = (String) dynamicFormRecordMethod
												.invoke(dynamicFormRecord);
									} catch (IllegalAccessException e) {
										LOGGER.error(e.getMessage(), e);
										throw new PayAsiaSystemException(
												e.getMessage(), e);
									} catch (IllegalArgumentException e) {
										LOGGER.error(e.getMessage(), e);
										throw new PayAsiaSystemException(
												e.getMessage(), e);
									} catch (InvocationTargetException e) {
										LOGGER.error(e.getMessage(), e);
										throw new PayAsiaSystemException(
												e.getMessage(), e);
									}
								} catch (NoSuchMethodException e) {
									LOGGER.error(e.getMessage(), e);
									throw new PayAsiaSystemException(
											e.getMessage(), e);
								} catch (SecurityException e) {
									LOGGER.error(e.getMessage(), e);
									throw new PayAsiaSystemException(
											e.getMessage(), e);
								}

								if (fieldRtype
										.equalsIgnoreCase(PayAsiaConstants.FIELD_TYPE_DATE)
										&& !fieldRValue.equalsIgnoreCase("")) {
									fieldRValue = DateUtils
											.convertDateToSpecificFormat(
													fieldRValue,
													companyDateFormat);
								}
								if (fieldRtype
										.equalsIgnoreCase(PayAsiaConstants.CODEDESC_FIELD_TYPE)
										&& StringUtils.isNotBlank(fieldRValue)) {
									DynamicFormFieldRefValue dynamicFormFieldRefValue = dynamicFormFieldRefValueDAO
											.findById(Long
													.parseLong(fieldRValue));

									try {
										String codeDescription = dynamicFormFieldRefValue
												.getDescription()
												+ "["
												+ dynamicFormFieldRefValue
														.getCode() + "]";
										fieldRValue = codeDescription;
									} catch (Exception exception) {
										LOGGER.error(exception.getMessage(),
												exception);
										fieldRValue = "";
									}

								}
							}
						}

					} else {

						DynamicFormTableRecord refDynFormTableRecord = dynamicFormTableRecordDAO
								.findByIdAndSeq(tableId, seqNo);
						Class<?> dynamicTableClass = refDynFormTableRecord
								.getClass();
						if (column.getDictionaryId().equals(
								refTableDataDictionary.getDataDictionaryId())) {
							String colRName = column.getName();

							String colNameCount = colRName.substring(
									colRName.lastIndexOf("_") + 1,
									colRName.length());

							String dynamicFormTableRecordMethodName = PayAsiaConstants.GET_COL
									+ colNameCount;

							Method dynamicFormTableRecordMethod = null;
							try {
								dynamicFormTableRecordMethod = dynamicTableClass
										.getMethod(dynamicFormTableRecordMethodName);
							} catch (NoSuchMethodException e) {
								LOGGER.error(e.getMessage(), e);
								throw new PayAsiaSystemException(
										e.getMessage(), e);
							} catch (SecurityException e) {
								LOGGER.error(e.getMessage(), e);
								throw new PayAsiaSystemException(
										e.getMessage(), e);
							}

							try {
								fieldRValue = (String) dynamicFormTableRecordMethod
										.invoke(refDynFormTableRecord);
							} catch (IllegalAccessException e) {
								LOGGER.error(e.getMessage(), e);
								throw new PayAsiaSystemException(
										e.getMessage(), e);
							} catch (IllegalArgumentException e) {
								LOGGER.error(e.getMessage(), e);
								throw new PayAsiaSystemException(
										e.getMessage(), e);
							} catch (InvocationTargetException e) {
								LOGGER.error(e.getMessage(), e);
								throw new PayAsiaSystemException(
										e.getMessage(), e);
							}

							if (column.getType().equalsIgnoreCase(
									PayAsiaConstants.FIELD_TYPE_DATE)
									&& StringUtils.isNotBlank(fieldRValue)) {

								fieldRValue = DateUtils
										.convertDateToSpecificFormat(
												fieldRValue, companyDateFormat);

							} else if (column.getType().equalsIgnoreCase(
									PayAsiaConstants.CODEDESC_FIELD_TYPE)
									&& StringUtils.isNotBlank(fieldRValue)) {

								DynamicFormFieldRefValue dynamicFormFieldRefValue = dynamicFormFieldRefValueDAO
										.findById(Long.parseLong(fieldRValue));
								try {
									String codeDescription = dynamicFormFieldRefValue
											.getDescription()
											+ "["
											+ dynamicFormFieldRefValue
													.getCode() + "]";
									fieldRValue = codeDescription;
								} catch (Exception exception) {
									LOGGER.error(exception.getMessage(),
											exception);
									fieldRValue = "";
								}

							}

						}

					}

				}

			}

		}
		return fieldRValue;

	}

	private String getColValueFile(String colNumber,
			DynamicFormRecord existingFormRecord) {

		String tableRecordId = null;
		Class<?> dynamicFormRecordClass = existingFormRecord.getClass();
		String colMehtodName = "getCol" + colNumber;
		Method dynamicFormRecordMethod;
		try {

			dynamicFormRecordMethod = dynamicFormRecordClass
					.getMethod(colMehtodName);
			tableRecordId = (String) dynamicFormRecordMethod
					.invoke(existingFormRecord);
		} catch (SecurityException | NoSuchMethodException
				| IllegalArgumentException | IllegalAccessException
				| InvocationTargetException e) {
			LOGGER.error(e.getMessage(), e);
			throw new PayAsiaSystemException(e.getMessage(), e);
		}

		return tableRecordId;

	}

	private String getColValueOfDynamicTableRecord(String colNumber,
			DynamicFormTableRecord existingFormRecord) {

		String tableRecordId = null;
		Class<?> dynamicFormTableRecordClass = existingFormRecord.getClass();
		String colMehtodName = "getCol" + colNumber;
		Method dynamicFormTableRecordMethod;
		try {

			dynamicFormTableRecordMethod = dynamicFormTableRecordClass
					.getMethod(colMehtodName);
			tableRecordId = (String) dynamicFormTableRecordMethod
					.invoke(existingFormRecord);
		} catch (SecurityException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (NoSuchMethodException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (IllegalArgumentException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (InvocationTargetException e) {
			LOGGER.error(e.getMessage(), e);
		}

		return tableRecordId;

	}

	@Override
	public HRLetterForm getHRLetter(long letterId, Long companyId) {
		HRLetterForm hrLetterForm = new HRLetterForm();
		HRLetter hrLetter = hrLetterDAO.findByHRLetterId(letterId,companyId);
		if(hrLetter!=null){
			hrLetterForm.setActive(hrLetter.getActive());
			hrLetterForm.setCompanyId(hrLetter.getCompany().getCompanyId());
			hrLetterForm.setLetterDescription(hrLetter.getLetterDesc());
			/*ID ENCRYPT*/
			hrLetterForm.setLetterId(FormatPreserveCryptoUtil.encrypt(hrLetter.getHrLetterId()));
			hrLetterForm.setLetterName(hrLetter.getLetterName());
			hrLetterForm.setBody(hrLetter.getBody());
			hrLetterForm.setSubject(hrLetter.getSubject());
		}
		return hrLetterForm;
		
	}

	@Override
	public void deleteFilter(Long filterId, Long companyId) {
		HRLetterShortlist hrLetterShortlist = hrLetterShortlistDAO
				.findByID(filterId,companyId);
		hrLetterShortlistDAO.delete(hrLetterShortlist);
		
	}
	
	@Override
	public Long getEmployeeIdByCode(String employeeNumber,Long companyID){
		return employeeDAO.findByNumber(employeeNumber, companyID).getEmployeeId();
		
	}
	
}
