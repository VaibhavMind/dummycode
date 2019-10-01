package com.payasia.logic.impl;

import java.io.StringReader;
import java.math.BigDecimal;
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

import com.mind.payasia.xml.bean.OTForm;
import com.mind.payasia.xml.bean.OTFormField;
import com.mind.payasia.xml.bean.OTFormItemField;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.EmpOTAddForm;
import com.payasia.common.form.OTItemDefinitionForm;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.OTFormXMLUtil;
import com.payasia.common.util.XMLUtil;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.DayTypeMasterDAO;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.EmployeeOTReviewerDAO;
import com.payasia.dao.OTApplicationDAO;
import com.payasia.dao.OTApplicationItemDAO;
import com.payasia.dao.OTApplicationItemDetailDAO;
import com.payasia.dao.OTItemMasterDAO;
import com.payasia.dao.OTStatusMasterDAO;
import com.payasia.dao.OTTemplateDAO;
import com.payasia.dao.OTTemplateItemDAO;
import com.payasia.dao.OTTemplateWorkflowDAO;
import com.payasia.dao.WorkFlowRuleMasterDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.DayTypeMaster;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeOTReviewer;
import com.payasia.dao.bean.OTApplication;
import com.payasia.dao.bean.OTApplicationItem;
import com.payasia.dao.bean.OTApplicationItemDetail;
import com.payasia.dao.bean.OTStatusMaster;
import com.payasia.dao.bean.OTTemplate;
import com.payasia.dao.bean.OTTemplateItem;
import com.payasia.logic.EmpOTAddLogic;

/**
 * The Class EmpOTAddLogicImpl.
 */
/**
 * @author vivekjain
 * 
 */
@Component
public class EmpOTAddLogicImpl implements EmpOTAddLogic {

	/** The Constant logger. */
	private static final Logger LOGGER = Logger
			.getLogger(EmpOTAddLogicImpl.class);

	/** The ot template workflow dao. */
	@Resource
	OTTemplateWorkflowDAO otTemplateWorkflowDAO;

	/** The ot template dao. */
	@Resource
	OTTemplateDAO otTemplateDAO;

	/** The ot template item dao. */
	@Resource
	OTTemplateItemDAO otTemplateItemDAO;

	/** The ot item master dao. */
	@Resource
	OTItemMasterDAO otItemMasterDAO;

	/** The company dao. */
	@Resource
	CompanyDAO companyDAO;

	/** The employee dao. */
	@Resource
	EmployeeDAO employeeDAO;

	/** The work flow rule master dao. */
	@Resource
	WorkFlowRuleMasterDAO workFlowRuleMasterDAO;

	/** The employee ot reviewer dao. */
	@Resource
	EmployeeOTReviewerDAO employeeOTReviewerDAO;

	/** The day type master dao. */
	@Resource
	DayTypeMasterDAO dayTypeMasterDAO;

	/** The ot application dao. */
	@Resource
	OTApplicationDAO otApplicationDAO;

	/** The ot application item dao. */
	@Resource
	OTApplicationItemDAO otApplicationItemDAO;

	/** The ot application item detail dao. */
	@Resource
	OTApplicationItemDetailDAO otApplicationItemDetailDAO;

	/** The ot status master dao. */
	@Resource
	OTStatusMasterDAO otStatusMasterDAO;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.EmpOTAddLogic#getOTTemplateList(java.lang.Long)
	 */
	@Override
	public List<EmpOTAddForm> getOTTemplateList(Long companyId) {
		List<EmpOTAddForm> empOTAddFormList = new ArrayList<EmpOTAddForm>();
		List<OTTemplate> otTemplateList = otTemplateDAO
				.getOTTemplateList(companyId);
		for (OTTemplate otTemplateVO : otTemplateList) {
			EmpOTAddForm empOTAddForm = new EmpOTAddForm();
			empOTAddForm.setOtTemplateId(otTemplateVO.getOtTemplateId());
			empOTAddForm.setOtTemplateName(otTemplateVO.getTemplateName());
			empOTAddFormList.add(empOTAddForm);
		}
		return empOTAddFormList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.EmpOTAddLogic#getOTReviewerList(java.lang.Long,
	 * java.lang.Long, java.lang.Long)
	 */
	@Override
	public EmpOTAddForm getOTReviewerList(Long otTemplateId, Long companyId,
			Long employeeId) {
		EmpOTAddForm empOTAddForm = new EmpOTAddForm();

		OTTemplate otTemplate = otTemplateDAO.findById(otTemplateId);
		List<EmployeeOTReviewer> empOTReviewers = new ArrayList<EmployeeOTReviewer>(
				otTemplate.getEmployeeOtReviewers());
		List<OTItemDefinitionForm> otItemDefinitionFormList = new ArrayList<OTItemDefinitionForm>();
		List<OTTemplateItem> otTemplateItem = new ArrayList<OTTemplateItem>(
				otTemplate.getOtTemplateItems());
		for (OTTemplateItem otTemplateItemVO : otTemplateItem) {
			OTItemDefinitionForm otItemDefinitionForm = new OTItemDefinitionForm();
			otItemDefinitionForm.setOtTemplateItemId(otTemplateItemVO
					.getOTTemplateItemId());
			otItemDefinitionForm.setName(otTemplateItemVO.getOtItemMaster()
					.getOtItemName());
			otItemDefinitionFormList.add(otItemDefinitionForm);
		}

		int totalNoOfReviewers = 0;
		for (EmployeeOTReviewer employeeOTReviewer : empOTReviewers) {
			if (employeeId == employeeOTReviewer.getEmployee1().getEmployeeId()) {
				totalNoOfReviewers++;
				if (employeeOTReviewer.getWorkFlowRuleMaster().getRuleValue()
						.equals("1")) {
					empOTAddForm.setOtReviewer1(employeeOTReviewer
							.getEmployee2().getFirstName()
							+ " "
							+ employeeOTReviewer.getEmployee2().getLastName());
					empOTAddForm.setOtReviewer1Id(employeeOTReviewer
							.getEmployee2().getEmployeeId());

				} else if (employeeOTReviewer.getWorkFlowRuleMaster()
						.getRuleValue().equals("2")) {

					empOTAddForm.setOtReviewer2(employeeOTReviewer
							.getEmployee2().getFirstName()
							+ " "
							+ employeeOTReviewer.getEmployee2().getLastName());
					empOTAddForm.setOtReviewer2Id(employeeOTReviewer
							.getEmployee2().getEmployeeId());

				} else if (employeeOTReviewer.getWorkFlowRuleMaster()
						.getRuleValue().equals("3")) {

					empOTAddForm.setOtReviewer3(employeeOTReviewer
							.getEmployee2().getFirstName()
							+ " "
							+ employeeOTReviewer.getEmployee2().getLastName());
					empOTAddForm.setOtReviewer3Id(employeeOTReviewer
							.getEmployee2().getEmployeeId());
				}
			}

		}

		empOTAddForm.setTotalNoOfReviewers(totalNoOfReviewers);
		empOTAddForm.setOtTypeFormList(otItemDefinitionFormList);
		return empOTAddForm;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.EmpOTAddLogic#getOTDayTypeList()
	 */
	@Override
	public List<EmpOTAddForm> getOTDayTypeList() {
		List<EmpOTAddForm> empOTAddFormList = new ArrayList<EmpOTAddForm>();
		List<DayTypeMaster> dayTypeMasterList = dayTypeMasterDAO.findAll();
		for (DayTypeMaster dayTypeMasterVO : dayTypeMasterList) {
			EmpOTAddForm empOTAddForm = new EmpOTAddForm();
			empOTAddForm.setDayTypeId(dayTypeMasterVO.getDayTypeId());
			empOTAddForm.setDayType(dayTypeMasterVO.getDayType());
			empOTAddFormList.add(empOTAddForm);
		}
		return empOTAddFormList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.EmpOTAddLogic#addOTApplication(java.lang.Long,
	 * java.lang.Long, java.lang.Long, java.lang.String, java.lang.String)
	 */
	@Override
	public void addOTApplication(Long otTemplateId, Long companyId,
			Long employeeId, String metaData, String generalRemarks) {
		Unmarshaller unmarshaller = null;
		try {
			unmarshaller = OTFormXMLUtil.getDocumentUnmarshaller();
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

		OTForm otForm = null;
		try {
			otForm = (OTForm) unmarshaller.unmarshal(xmlSource);
		} catch (JAXBException ex) {

			LOGGER.error(ex.getMessage(), ex);
		}

		Company company = companyDAO.findById(companyId);
		Employee employee = employeeDAO.findById(employeeId);
		Long otStatusMasterId = getOTStatusMasterId("Draft");
		OTStatusMaster otStatusMaster = otStatusMasterDAO
				.findByID(otStatusMasterId);
		OTTemplate otTemplate = otTemplateDAO.findById(otTemplateId);

		OTApplication otApplication = new OTApplication();
		otApplication.setCompany(company);
		otApplication.setEmployee(employee);
		otApplication.setCreatedDate(DateUtils.getCurrentTimestamp());
		otApplication.setOtStatusMaster(otStatusMaster);
		if (StringUtils.isNotBlank(generalRemarks)) {
			otApplication.setRemarks(generalRemarks);
		}
		otApplication.setOtTemplate(otTemplate);
		OTApplication persistOTApplicationObj = otApplicationDAO
				.saveOTTemplate(otApplication);

		List<OTFormField> listOfFields = otForm.getOTFormField();
		for (OTFormField field : listOfFields) {
			OTApplicationItem otApplicationItem = new OTApplicationItem();
			OTApplicationItem persistOTAppItemObj = null;
			if (field.getDate() != "" && field.getDate() != null) {
				otApplicationItem.setOtDate(DateUtils.stringToTimestamp(field
						.getDate()));
			}
			if (field.getRemarks() != "" && field.getRemarks() != null) {
				otApplicationItem.setRemarks(field.getRemarks());

			}
			if (field.getDayType() != "" && field.getDayType() != null) {
				DayTypeMaster dayTypeMaster = dayTypeMasterDAO.findById(Long
						.parseLong(field.getDayType()));
				otApplicationItem.setDayTypeMaster(dayTypeMaster);
				otApplicationItem.setOtApplication(persistOTApplicationObj);
				persistOTAppItemObj = otApplicationItemDAO
						.saveOTApplicationItem(otApplicationItem);
			}

			List<OTFormItemField> listOfOTFormItemField = field
					.getOTFormItemField();
			for (OTFormItemField otFormItemField : listOfOTFormItemField) {
				OTApplicationItemDetail otApplicationItemDetail = new OTApplicationItemDetail();

				if (otFormItemField.getOtItemName() != ""
						&& otFormItemField.getOtItemName() != null) {
					OTTemplateItem otTemplateItem = otTemplateItemDAO
							.findById(Long.parseLong(otFormItemField
									.getOtItemName()));
					otApplicationItemDetail.setOtTemplateItem(otTemplateItem);
				}
				if (otFormItemField.getOtItemValue() != ""
						&& otFormItemField.getOtItemValue() != null) {
					otApplicationItemDetail.setOtTemplateItemValue(BigDecimal
							.valueOf(Long.parseLong(otFormItemField
									.getOtItemValue())));
					otApplicationItemDetail
							.setOtApplicationItem(persistOTAppItemObj);
					otApplicationItemDetailDAO.save(otApplicationItemDetail);
				}

			}

		}
	}

	/**
	 * Gets the oT status master id.
	 * 
	 * @param otStatus
	 *            the ot status
	 * @return the oT status master id
	 */
	public Long getOTStatusMasterId(String otStatus) {
		OTStatusMaster otStatusMaster = otStatusMasterDAO
				.findByOTStatus(otStatus);
		return otStatusMaster.getOtStatusId();

	}
}
