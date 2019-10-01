package com.payasia.logic.impl;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import com.mind.payasia.xml.bean.PayDataCollectionData;
import com.mind.payasia.xml.bean.PayDataCollectionField;
import com.payasia.common.dto.EmployeeShortListDTO;
import com.payasia.common.dto.PayDataCollectionConditionDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.PayDataCollectionForm;
import com.payasia.common.form.PayDataCollectionResponseForm;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.PayDataXMLUtil;
import com.payasia.common.util.XMLUtil;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.PayCodeDAO;
import com.payasia.dao.PayDataCollectionDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.PayDataCollection;
import com.payasia.dao.bean.Paycode;
import com.payasia.logic.EmployeeDetailLogic;
import com.payasia.logic.GeneralLogic;
import com.payasia.logic.PayDataCollectionLogic;

/**
 * The Class PayDataCollectionLogicImpl.
 */
/**
 * @author vivekjain
 * 
 */
@Component
public class PayDataCollectionLogicImpl implements PayDataCollectionLogic {

	/** The Constant logger. */
	private static final Logger LOGGER = Logger
			.getLogger(PayDataCollectionLogicImpl.class);

	/** The employee dao. */
	@Resource
	EmployeeDAO employeeDAO;

	/** The company dao. */
	@Resource
	CompanyDAO companyDAO;

	/** The pay data collection dao. */
	@Resource
	PayDataCollectionDAO payDataCollectionDAO;

	/** The pay code dao. */
	@Resource
	PayCodeDAO payCodeDAO;

	@Resource
	EmployeeDetailLogic employeeDetailLogic;

	@Resource
	GeneralLogic generalLogic;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.PayDataCollectionLogic#getEmployeeList(java.lang.Long,
	 * java.lang.String, java.lang.String, com.payasia.common.form.PageRequest,
	 * com.payasia.common.form.SortCondition)
	 */
	@Override
	public PayDataCollectionResponseForm getEmployeeList(Long companyId,
			String searchCondition, String searchText, PageRequest pageDTO,
			SortCondition sortDTO, Long employeeId)

	{
		PayDataCollectionConditionDTO conditionDTO = new PayDataCollectionConditionDTO();
		Company company = companyDAO.findById(companyId);

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
		if (searchCondition
				.equals(PayAsiaConstants.PAYDATA_COLLECTION_EMP_NAME)) {
			if (StringUtils.isNotBlank(searchText)) {
				try {
					conditionDTO.setEmployeeName("%"
							+ URLDecoder.decode(searchText, "UTF-8") + "%");
				} catch (UnsupportedEncodingException ex) {
					LOGGER.error(ex.getMessage(), ex);
				}
			}

		}
		if (searchCondition.equals(PayAsiaConstants.PAYDATA_COLLECTION_PAYCODE)) {
			if (StringUtils.isNotBlank(searchText)) {
				try {
					conditionDTO.setPayCode("%"
							+ URLDecoder.decode(searchText, "UTF-8") + "%");
				} catch (UnsupportedEncodingException ex) {
					LOGGER.error(ex.getMessage(), ex);
				}
			}

		}

		EmployeeShortListDTO employeeShortListDTO = generalLogic
				.getShortListEmployeeIds(employeeId, companyId);

		conditionDTO.setEmployeeShortListDTO(employeeShortListDTO);

		List<PayDataCollectionForm> payDataCollectionFormList = new ArrayList<PayDataCollectionForm>();
		List<PayDataCollection> searchList = payDataCollectionDAO
				.findByCondition(conditionDTO, companyId, pageDTO, sortDTO);
		if ( !searchList.isEmpty()) {
			for (PayDataCollection payDataCollection : searchList) {
				PayDataCollectionForm payDataCollectionForm = new PayDataCollectionForm();
		
				/*ID ENCRYPT*/
				payDataCollectionForm.setPayDataCollectionId(FormatPreserveCryptoUtil.encrypt(payDataCollection
						.getPayDataCollectionId()));

				payDataCollectionForm.setEmployeeId(payDataCollection
						.getEmployee().getEmployeeId());
				payDataCollectionForm.setEmployeeNumber(payDataCollection
						.getEmployee().getEmployeeNumber());
				String empName = "";
				empName += payDataCollection.getEmployee().getFirstName() + " ";
				if (StringUtils.isNotBlank(payDataCollection.getEmployee()
						.getLastName())) {
					empName += payDataCollection.getEmployee().getLastName();
				}
				payDataCollectionForm.setEmployeeName(empName);
				payDataCollectionForm.setPayCodeId(payDataCollection
						.getPaycode().getPaycodeID());
				payDataCollectionForm.setPayCode(payDataCollection.getPaycode()
						.getPaycode());
				payDataCollectionForm.setAmount(payDataCollection.getAmount());

				Date fromDate = new Date(payDataCollection.getStartDate()
						.getTime());
				SimpleDateFormat fromFormatter = new SimpleDateFormat(
						company.getDateFormat());
				payDataCollectionForm.setFromDate(fromFormatter
						.format(fromDate));

				Date toDate = new Date(payDataCollection.getEndDate().getTime());
				SimpleDateFormat toFormatter = new SimpleDateFormat(
						company.getDateFormat());
				payDataCollectionForm.setToDate(toFormatter.format(toDate));

				payDataCollectionFormList.add(payDataCollectionForm);
			}
		}

		int recordSize = payDataCollectionDAO.getCountForCondition(
				conditionDTO, companyId);
		PayDataCollectionResponseForm response = new PayDataCollectionResponseForm();
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
		response.setPayDataCollectionForm(payDataCollectionFormList);
		response.setRecords(recordSize);
		return response;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.PayDataCollectionLogic#addPayDataCollection(java.lang
	 * .Long, java.lang.String)
	 */
	@Override
	public String addPayDataCollection(Long companyId, String metaData) {
		boolean status = false;
		Unmarshaller unmarshaller = null;
		try {
			unmarshaller = PayDataXMLUtil.getDocumentUnmarshaller();
		} catch (JAXBException ex) {

			LOGGER.error(ex.getMessage(), ex);
		} catch (SAXException ex) {

			LOGGER.error(ex.getMessage(), ex);
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

		PayDataCollectionData payDataCollectionData = null;
		try {
			payDataCollectionData = (PayDataCollectionData) unmarshaller
					.unmarshal(xmlSource);
		} catch (JAXBException ex) {

			LOGGER.error(ex.getMessage(), ex);
		}
		Company company = companyDAO.findById(companyId);
		List<PayDataCollectionField> listOfFields = payDataCollectionData
				.getPayDataCollectionField();
		for (PayDataCollectionField field : listOfFields) {
			status = false;
			PayDataCollection PayDataCollection = new PayDataCollection();

			if (field.getPayCodeId() != "" && field.getPayCodeId() != null) {
				Paycode paycode = payCodeDAO.findByID(Long.parseLong(field
						.getPayCodeId()));
				PayDataCollection.setPaycode(paycode);
			}
			if (field.getEmployeeId() != "" && field.getEmployeeId() != null) {
				Employee employee = employeeDAO.findById(Long.parseLong(field
						.getEmployeeId()));
				PayDataCollection.setEmployee(employee);
			}
			if (field.getAmount() != null) {
				PayDataCollection.setAmount(BigDecimal.valueOf(field
						.getAmount()));
			}
			if (field.getFromDate() != "" && field.getFromDate() != null) {
				PayDataCollection.setStartDate(DateUtils.stringToTimestamp(
						field.getFromDate(), company.getDateFormat()));
			}
			if (field.getToDate() != "" && field.getToDate() != null) {
				PayDataCollection.setEndDate(DateUtils.stringToTimestamp(
						field.getToDate(), company.getDateFormat()));

				PayDataCollection.setCompany(company);
				status = true;
				payDataCollectionDAO.save(PayDataCollection);
			}
		}

		if (status) {
			return "pay.data.collection.saved.successfully";
		}
		return "pay.data.collection.are.not.saved";

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.PayDataCollectionLogic#getEmployeeId(java.lang.Long,
	 * java.lang.String)
	 */
	@Override
	public List<PayDataCollectionForm> getEmployeeId(Long companyId,
			String searchString, Long employeeId) {
		List<PayDataCollectionForm> payDataCollectionFormList = new ArrayList<PayDataCollectionForm>();

		EmployeeShortListDTO employeeShortListDTO = generalLogic
				.getShortListEmployeeIds(employeeId, companyId);

		List<Employee> employeeNumberList = employeeDAO.getEmployeeIds(
				searchString.trim(), companyId, employeeShortListDTO);

		for (Employee employee : employeeNumberList) {
			PayDataCollectionForm payDataCollectionForm = new PayDataCollectionForm();
			payDataCollectionForm.setEmployeeNumber(employee
					.getEmployeeNumber());
			String empName = "";
			empName += employee.getFirstName() + " ";
			if (StringUtils.isNotBlank(employee.getLastName())) {
				empName += employee.getLastName();
			}
			payDataCollectionForm.setEmployeeName(empName);
			payDataCollectionForm.setEmployeeId(employee.getEmployeeId());
			payDataCollectionFormList.add(payDataCollectionForm);

		}
		return payDataCollectionFormList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.PayDataCollectionLogic#getPayCode(java.lang.Long,
	 * java.lang.String)
	 */
	@Override
	public List<PayDataCollectionForm> getPayCode(Long companyId,
			String searchString) {
		List<PayDataCollectionForm> payDataCollectionFormList = new ArrayList<PayDataCollectionForm>();
		List<Paycode> PaycodeList = payCodeDAO.getPayCode(searchString,
				companyId);
		for (Paycode paycodedata : PaycodeList) {
			PayDataCollectionForm payDataCollectionForm = new PayDataCollectionForm();
			payDataCollectionForm.setPayCode(paycodedata.getPaycode());
			payDataCollectionForm.setPayCodeId(paycodedata.getPaycodeID());
			payDataCollectionFormList.add(payDataCollectionForm);
		}
		return payDataCollectionFormList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.PayDataCollectionLogic#getAllPayCode(java.lang.Long)
	 */
	@Override
	public List<PayDataCollectionForm> getAllPayCode(Long companyId) {
		PageRequest pageDTO = null;
		SortCondition sortDTO = null;
		List<PayDataCollectionForm> payDataCollectionFormList = new ArrayList<PayDataCollectionForm>();
		List<Paycode> PaycodeList = payCodeDAO.getAllPayCodeByConditionCompany(
				companyId, pageDTO, sortDTO);
		for (Paycode paycodedata : PaycodeList) {
			PayDataCollectionForm payDataCollectionForm = new PayDataCollectionForm();
			payDataCollectionForm.setPayCode(paycodedata.getPaycode());
			payDataCollectionForm.setPayCodeId(paycodedata.getPaycodeID());
			payDataCollectionFormList.add(payDataCollectionForm);
		}
		return payDataCollectionFormList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.PayDataCollectionLogic#getPayDataCollectionForEdit(
	 * java.lang.Long, java.lang.Long)
	 */
	@Override
	public PayDataCollectionForm getPayDataCollectionForEdit(Long companyId,
			Long payDataCollectionId) {
		Company company = companyDAO.findById(companyId);
		PayDataCollectionForm payDataCollectionForm = new PayDataCollectionForm();
		PayDataCollection payDataCollection = payDataCollectionDAO
				.getPayDataCollectionForEdit(companyId, payDataCollectionId);

		/*ID ENCRYPT*/
		payDataCollectionForm.setPayDataCollectionId(FormatPreserveCryptoUtil.encrypt(payDataCollection
				.getPayDataCollectionId()));
		
		payDataCollectionForm.setEmployeeId(payDataCollection.getEmployee()
				.getEmployeeId());
		payDataCollectionForm.setEmployeeNumber(payDataCollection.getEmployee()
				.getEmployeeNumber());
		String empName = "";
		empName += payDataCollection.getEmployee().getFirstName() + " ";
		if (StringUtils.isNotBlank(payDataCollection.getEmployee()
				.getLastName())) {
			empName += payDataCollection.getEmployee().getLastName();
		}

		payDataCollectionForm.setEmployeeName(empName);
		payDataCollectionForm.setPayCodeId(payDataCollection.getPaycode()
				.getPaycodeID());
		payDataCollectionForm.setPayCode(payDataCollection.getPaycode()
				.getPaycode());
		payDataCollectionForm.setAmount(payDataCollection.getAmount());

		Date fromDate = new Date(payDataCollection.getStartDate().getTime());
		SimpleDateFormat fromFormatter = new SimpleDateFormat(
				company.getDateFormat());
		payDataCollectionForm.setFromDate(fromFormatter.format(fromDate));

		Date toDate = new Date(payDataCollection.getEndDate().getTime());
		SimpleDateFormat toFormatter = new SimpleDateFormat(
				company.getDateFormat());
		payDataCollectionForm.setToDate(toFormatter.format(toDate));

		return payDataCollectionForm;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.PayDataCollectionLogic#deletePayDataCollection(java
	 * .lang.Long)
	 */
	@Override
	public void deletePayDataCollection(Long payDataCollectionId) {
		PayDataCollection payDataCollection = payDataCollectionDAO
				.findByID(payDataCollectionId);

		payDataCollectionDAO.delete(payDataCollection);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.PayDataCollectionLogic#updatePayDataCollection(com.
	 * payasia.common.form.PayDataCollectionForm, java.lang.Long)
	 */
	@Override
	public void updatePayDataCollection(
			PayDataCollectionForm payDataCollectionForm, Long companyId) {
		PayDataCollection payDataCollection = new PayDataCollection();

		payDataCollection.setPayDataCollectionId(payDataCollectionForm
				.getPayDataCollectionId());

		Employee employee = employeeDAO.findById(payDataCollectionForm
				.getEmployeeId());
		payDataCollection.setEmployee(employee);

		Company company = companyDAO.findById(companyId);
		payDataCollection.setCompany(company);

		Paycode paycode = payCodeDAO.findByID(payDataCollectionForm
				.getPayCodeId());
		payDataCollection.setPaycode(paycode);

		payDataCollection.setAmount(payDataCollectionForm.getAmount());
		payDataCollection.setStartDate(DateUtils.stringToTimestamp(
				payDataCollectionForm.getFromDate(), company.getDateFormat()));
		payDataCollection.setEndDate(DateUtils.stringToTimestamp(
				payDataCollectionForm.getToDate(), company.getDateFormat()));
		payDataCollectionDAO.update(payDataCollection);

	}

}
