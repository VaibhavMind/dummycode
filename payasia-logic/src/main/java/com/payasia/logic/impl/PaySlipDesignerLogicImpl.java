package com.payasia.logic.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfAction;
import com.itextpdf.text.pdf.PdfDestination;
import com.itextpdf.text.pdf.PdfPageEvent;
import com.itextpdf.text.pdf.PdfWriter;
import com.mind.payasia.xml.bean.Field;
import com.mind.payasia.xml.bean.Tab;
import com.payasia.common.dto.PaySlipPDFTemplateDTO;
import com.payasia.common.exception.PDFMultiplePageException;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.PaySlipDesignerForm;
import com.payasia.common.form.PaySlipDynamicForm;
import com.payasia.common.form.PayslipDesignerResponse;
import com.payasia.common.util.PDFThreadLocal;
import com.payasia.common.util.PDFUtils;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.PayAsiaPDFConstants;
import com.payasia.common.util.XMLUtil;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.DataDictionaryDAO;
import com.payasia.dao.DynamicFormDAO;
import com.payasia.dao.EntityMasterDAO;
import com.payasia.dao.MonthMasterDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.DataDictionary;
import com.payasia.dao.bean.DynamicForm;
import com.payasia.dao.bean.DynamicFormPK;
import com.payasia.dao.bean.EntityMaster;
import com.payasia.dao.bean.MonthMaster;
import com.payasia.logic.PaySlipDesignerLogic;
import com.payasia.logic.PaySlipPDFLogoHeaderSection;
import com.payasia.logic.PaySlipPDFOtherSection;

/**
 * The Class PaySlipDesignerLogic.
 */
/**
 * @author vivekjain
 * 
 */
@Component
public class PaySlipDesignerLogicImpl implements PaySlipDesignerLogic {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger
			.getLogger(PaySlipDesignerLogicImpl.class);

	/** The payasia temp path. */
	@Value("#{payasiaptProperties['payasia.temp.path']}")
	private String PAYASIA_TEMP_PATH;

	/** The data dictionary dao. */
	@Resource
	DataDictionaryDAO dataDictionaryDAO;

	/** The entity master dao. */
	@Resource
	EntityMasterDAO entityMasterDAO;

	/** The company dao. */
	@Resource
	CompanyDAO companyDAO;

	/** The dynamic form dao. */
	@Resource
	DynamicFormDAO dynamicFormDAO;

	/** The pay slip pdf logo section. */
	@Resource
	PaySlipPDFLogoHeaderSection paySlipPDFLogoSection;

	/** The pay slip pdf other section. */
	@Resource
	PaySlipPDFOtherSection paySlipPDFOtherSection;

	@Resource
	MonthMasterDAO monthMasterDAO;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.PaySlipDesignerLogic#getLabelList(java.lang.Long)
	 */
	@Override
	public PayslipDesignerResponse getLabelList(Long companyId) {

		EntityMaster payslipDesignerFormEntityMaster = entityMasterDAO
				.findByEntityName(PayAsiaConstants.PAY_SLIP_FORM_ENTITY_NAME);

		EntityMaster payslipDesignerCompanyFormEntityMaster = entityMasterDAO
				.findByEntityName(PayAsiaConstants.COMPANY_ENTITY_NAME);

		EntityMaster payslipDesignerEmployeeFormEntityMaster = entityMasterDAO
				.findByEntityName(PayAsiaConstants.EMPLOYEE_ENTITY_NAME);

		List<DataDictionary> paySlipDesignerDataDictionary = dataDictionaryDAO
				.findByConditionEntityAndCompanyId(companyId,
						payslipDesignerFormEntityMaster.getEntityId(), null);

		List<DataDictionary> paySlipDesignerDataDictionaryCompanyDynamicList = dataDictionaryDAO
				.findByConditionEntityAndCompanyId(companyId,
						payslipDesignerCompanyFormEntityMaster.getEntityId(),
						null);

		List<DataDictionary> paySlipDesignerDataDictionaryEmployeeDynamicList = dataDictionaryDAO
				.findByConditionEntityAndCompanyIdAndFormula(companyId,
						payslipDesignerEmployeeFormEntityMaster.getEntityId());

		List<PaySlipDesignerForm> labelList = new ArrayList<PaySlipDesignerForm>();
		for (DataDictionary dataDictionary : paySlipDesignerDataDictionary) {
			PaySlipDesignerForm paySlipDesignerForm = new PaySlipDesignerForm();
			if (dataDictionary.getFieldType().equalsIgnoreCase(
					PayAsiaConstants.STATIC_TYPE)) {
				paySlipDesignerForm.setLabelName(dataDictionary
						.getDataDictName());
			} else {
				paySlipDesignerForm.setLabelName(dataDictionary.getLabel());
			}

			paySlipDesignerForm.setDictionaryId(dataDictionary
					.getDataDictionaryId());
			paySlipDesignerForm.setFieldType(dataDictionary.getFieldType());
			if (dataDictionary
					.getEntityMaster()
					.getEntityName()
					.equalsIgnoreCase(
							PayAsiaConstants.PAY_SLIP_FORM_ENTITY_NAME)) {
				paySlipDesignerForm
						.setEntityName(PayAsiaConstants.PAY_SLIP_ENTITY_NAME);
			} else {
				paySlipDesignerForm.setEntityName(dataDictionary
						.getEntityMaster().getEntityName());
			}

			labelList.add(paySlipDesignerForm);
		}

		for (DataDictionary dataDictionary : paySlipDesignerDataDictionaryCompanyDynamicList) {
			PaySlipDesignerForm paySlipDesignerForm = new PaySlipDesignerForm();
			if (dataDictionary.getFieldType().equalsIgnoreCase(
					PayAsiaConstants.DYNAMIC_TYPE)) {
				paySlipDesignerForm.setLabelName(dataDictionary.getLabel());
				paySlipDesignerForm.setDictionaryId(dataDictionary
						.getDataDictionaryId());
				paySlipDesignerForm.setFieldType(dataDictionary.getFieldType());
				if (dataDictionary
						.getEntityMaster()
						.getEntityName()
						.equalsIgnoreCase(
								PayAsiaConstants.PAY_SLIP_FORM_ENTITY_NAME)) {
					paySlipDesignerForm
							.setEntityName(PayAsiaConstants.PAY_SLIP_ENTITY_NAME);
				} else {
					paySlipDesignerForm.setEntityName(dataDictionary
							.getEntityMaster().getEntityName());
				}
				labelList.add(paySlipDesignerForm);

			}

		}

		for (DataDictionary dataDictionary : paySlipDesignerDataDictionaryEmployeeDynamicList) {
			PaySlipDesignerForm paySlipDesignerForm = new PaySlipDesignerForm();
			if (dataDictionary.getFieldType().equalsIgnoreCase(
					PayAsiaConstants.DYNAMIC_TYPE)) {
				paySlipDesignerForm.setLabelName(dataDictionary.getLabel());
				paySlipDesignerForm.setDictionaryId(dataDictionary
						.getDataDictionaryId());
				paySlipDesignerForm.setFieldType(dataDictionary.getFieldType());
				if (dataDictionary
						.getEntityMaster()
						.getEntityName()
						.equalsIgnoreCase(
								PayAsiaConstants.PAY_SLIP_FORM_ENTITY_NAME)) {
					paySlipDesignerForm
							.setEntityName(PayAsiaConstants.PAY_SLIP_ENTITY_NAME);
				} else {
					paySlipDesignerForm.setEntityName(dataDictionary
							.getEntityMaster().getEntityName());
				}
				labelList.add(paySlipDesignerForm);

			}

		}

		PayslipDesignerResponse payslipDesignerResponse = new PayslipDesignerResponse();
		payslipDesignerResponse.setLabelList(labelList);
		return payslipDesignerResponse;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.PaySlipDesignerLogic#saveXML(java.lang.Long,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public void saveXML(Long companyId, String metaData, String sectionName,
			int year, long month, int part) {
		Company company = companyDAO.findById(companyId);
		EntityMaster entityMaster = entityMasterDAO
				.findByEntityName(PayAsiaConstants.PAY_SLIP_ENTITY_NAME);

		long maxmFormId;
		if (dynamicFormDAO.getCountByCondition(companyId,
				entityMaster.getEntityId(), sectionName) > 0) {
			synchronized (this) {
				int maxVersion = dynamicFormDAO.getMaxVersion(companyId,
						entityMaster.getEntityId(), sectionName);

				DynamicForm maxDynamicForm = dynamicFormDAO.findByMaxVersion(
						companyId, entityMaster.getEntityId(), maxVersion,
						sectionName);
				maxmFormId = maxDynamicForm.getId().getFormId();
			}
		} else {

			synchronized (this) {
				maxmFormId = dynamicFormDAO.getMaxFormId(null, null) + 1;
			}
		}

		List<Long> existingDictionaryIds = new ArrayList<Long>();
		List<Long> tabDictionaryIds = new ArrayList<Long>();

		if (dataDictionaryDAO.getCountByCondition(companyId,
				entityMaster.getEntityId(), maxmFormId) > 0) {

			List<DataDictionary> existingDictionaries = dataDictionaryDAO
					.findByConditionFormId(companyId,
							entityMaster.getEntityId(), maxmFormId);

			for (DataDictionary dictionary : existingDictionaries) {
				existingDictionaryIds.add(dictionary.getDataDictionaryId());
			}

		} else {
			LOGGER.info("No data dictionary exist.");
		}

		Tab tab = getTabObject(metaData);

		List<Field> listOfFields = tab.getField();
		for (Field field : listOfFields) {

			if (StringUtils.equalsIgnoreCase(field.getType(),
					PayAsiaConstants.LABEL_FIELD_TYPE)) {
				tabDictionaryIds.add(field.getDictionaryId());
			}
		}
		for (Long dictionaryId : existingDictionaryIds) {
			if (!(tabDictionaryIds.contains(dictionaryId))) {
				DataDictionary dataDictionary = dataDictionaryDAO
						.findById(dictionaryId);

				try {
					dataDictionaryDAO.delete(dataDictionary);
				} catch (Exception e) {
					LOGGER.error(e.getMessage(), e);
					throw new PayAsiaSystemException(
							PayAsiaConstants.PAYASIA_DELETE);
				}

			}
		}

		for (Field field : listOfFields) {
			if (StringUtils.equalsIgnoreCase(field.getType(),
					PayAsiaConstants.LABEL_FIELD_TYPE)) {
				if (field.getDictionaryId() > 0) {
					DataDictionary dataDictionary = dataDictionaryDAO
							.findById(field.getDictionaryId());
					if (!dataDictionary.getDataDictName().equals(
							new String(Base64.decodeBase64(field
									.getDictionaryName().getBytes())))) {
						dataDictionary.setDataDictName(new String(Base64
								.decodeBase64(field.getDictionaryName()
										.getBytes())));
						dataDictionary.setLabel(new String(Base64
								.decodeBase64(field.getLabel().getBytes())));
						dataDictionary
								.setDataType(PayAsiaConstants.PAYASIA_DATA_DICTIONARY_DATA_TYPE_LABEL);
						dataDictionary.setImportable(false);
						dataDictionaryDAO.update(dataDictionary);
					}
					if (dataDictionary.getLabel() == null) {
						dataDictionary
								.setDataType(PayAsiaConstants.PAYASIA_DATA_DICTIONARY_DATA_TYPE_LABEL);
						dataDictionary.setImportable(false);
						dataDictionary.setLabel(new String(Base64
								.decodeBase64(field.getLabel().getBytes())));
						dataDictionaryDAO.update(dataDictionary);
					}
				} else {
					DataDictionary dataDictionary = new DataDictionary();
					dataDictionary.setCompany(company);
					dataDictionary.setEntityMaster(entityMaster);
					dataDictionary
							.setDataDictName(new String(Base64
									.decodeBase64(field.getDictionaryName()
											.getBytes())));
					dataDictionary.setLabel(new String(Base64
							.decodeBase64(field.getLabel().getBytes())));

					dataDictionary.setFormID(maxmFormId);
					dataDictionary.setFieldType(PayAsiaConstants.DYNAMIC_TYPE);
					dataDictionary
							.setDataType(PayAsiaConstants.PAYASIA_DATA_DICTIONARY_DATA_TYPE_LABEL);
					dataDictionary.setImportable(false);
					dataDictionaryDAO.save(dataDictionary);
				}
			}
		}

		DynamicFormPK dynamicFormPk = new DynamicFormPK();
		DynamicForm dynamicForm = new DynamicForm();
		dynamicForm.setMetaData(metaData);
		dynamicForm.setCompany(company);
		dynamicForm.setEntityMaster(entityMaster);
		dynamicForm.setTabName(sectionName);
		dynamicForm.setEffectiveYear(year);
		MonthMaster monthMaster = monthMasterDAO.findById(month);
		dynamicForm.setMonthMaster(monthMaster);

		if (company.getPayslipFrequency().getFrequency()
				.equalsIgnoreCase(PayAsiaConstants.PAYSLIP_FREQUENCY_MONTHLY)) {
			dynamicForm.setEffectivePart(1);
		} else {
			dynamicForm.setEffectivePart(part);
		}

		if (dynamicFormDAO.getCountByCondition(companyId,
				entityMaster.getEntityId(), sectionName) > 0) {
			synchronized (this) {
				int maxVersion = dynamicFormDAO.getMaxVersion(companyId,
						entityMaster.getEntityId(), sectionName);

				DynamicForm maxDynamicForm = dynamicFormDAO.findByMaxVersion(
						companyId, entityMaster.getEntityId(), maxVersion,
						sectionName);
				dynamicFormPk
						.setVersion(maxDynamicForm.getId().getVersion() + 1);
				dynamicFormPk.setFormId((maxDynamicForm.getId().getFormId()));
				dynamicFormPk.setCompany_ID(company.getCompanyId());
				dynamicFormPk.setEntity_ID(entityMaster.getEntityId());
				dynamicForm.setId(dynamicFormPk);
				dynamicFormDAO.saveAndReturn(dynamicForm);
			}

		} else {

			dynamicFormPk.setVersion(1);
			synchronized (this) {
				long maxFormId = dynamicFormDAO.getMaxFormId(null, null);
				dynamicFormPk.setFormId(maxFormId + 1);
				dynamicFormPk.setCompany_ID(company.getCompanyId());
				dynamicFormPk.setEntity_ID(entityMaster.getEntityId());
				dynamicForm.setId(dynamicFormPk);
				dynamicFormDAO.saveAndReturn(dynamicForm);
			}

		}

	}

	private Tab getTabObject(String metaData) {
		Unmarshaller unmarshaller = null;
		try {
			unmarshaller = XMLUtil.getDocumentUnmarshaller();
		} catch (JAXBException jaxbException) {
			LOGGER.error(jaxbException.getMessage(), jaxbException);
		} catch (SAXException saxException) {
			LOGGER.error(saxException.getMessage(), saxException);
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

		Tab tab = null;
		try {
			tab = (Tab) unmarshaller.unmarshal(xmlSource);
		} catch (JAXBException jaxbException) {
			LOGGER.error(jaxbException.getMessage(), jaxbException);
		}
		return tab;
	}

	/**
	 * purpose: Gets the XML.
	 * 
	 * @param companyId
	 *            the company id
	 * @param sectionName
	 *            the section name
	 * @return the Max Dynamic form Meta Data
	 */
	@Override
	public String getXML(Long companyId, String sectionName) {

		EntityMaster entityMaster = entityMasterDAO
				.findByEntityName(PayAsiaConstants.PAY_SLIP_ENTITY_NAME);

		DynamicForm maxDynamicForm = dynamicFormDAO.findMaxVersionBySection(
				companyId, entityMaster.getEntityId(), sectionName);

		if (maxDynamicForm == null) {
			return "";
		}

		Unmarshaller unmarshaller = null;
		try {
			unmarshaller = XMLUtil.getDocumentUnmarshaller();
		} catch (JAXBException jaxbException) {
			LOGGER.error(jaxbException.getMessage(), jaxbException);
		} catch (SAXException saxException) {
			LOGGER.error(saxException.getMessage(), saxException);
		}

		final StringReader xmlReader = new StringReader(
				maxDynamicForm.getMetaData());
		Source xmlSource = null;
		try {
			xmlSource = XMLUtil.getSAXSource(xmlReader);
		} catch (SAXException | ParserConfigurationException e1) {
			LOGGER.error(e1.getMessage(), e1);
			throw new PayAsiaSystemException(e1.getMessage(),
					e1);
		}
		XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		XMLStreamWriter streamWriter = null;
		try {
			streamWriter = outputFactory
					.createXMLStreamWriter(byteArrayOutputStream);
		} catch (XMLStreamException xmlStreamException) {
			LOGGER.error(xmlStreamException.getMessage(), xmlStreamException);
		}
		Tab tab = null;
		try {
			tab = (Tab) unmarshaller.unmarshal(xmlSource);
		} catch (JAXBException jaxbException) {
			LOGGER.error(jaxbException.getMessage(), jaxbException);
		}

		List<Field> listOfFields = tab.getField();

		for (Field field : listOfFields) {
			if (StringUtils.equalsIgnoreCase(field.getType(),
					PayAsiaConstants.LABEL_FIELD_TYPE)) {

				DataDictionary dataDictionary;

				if (Base64.isArrayByteBase64(field.getDictionaryName()
						.getBytes())) {
					dataDictionary = dataDictionaryDAO.findByDictionaryName(
							companyId,
							entityMaster.getEntityId(),
							new String(Base64.decodeBase64(field
									.getDictionaryName().getBytes())),
							maxDynamicForm.getId().getFormId());
					if (dataDictionary != null) {
						field.setDictionaryId(dataDictionary
								.getDataDictionaryId());
					} else {
						field.setDictionaryId(0l);
					}

				} else {
					dataDictionary = dataDictionaryDAO.findByDictionaryName(
							companyId, entityMaster.getEntityId(), field
									.getDictionaryName(), maxDynamicForm
									.getId().getFormId());
					if (dataDictionary != null) {
						field.setDictionaryId(dataDictionary
								.getDataDictionaryId());
					} else {
						field.setDictionaryId(0l);
					}

				}

			}
			if (field.isColspan() == null) {
				field.setColspan(false);
			}
			if (field.isBold() == null) {
				field.setBold(false);
			}
			if (field.isHideLabel() == null) {
				field.setHideLabel(false);
			}
			if (field.isUnderline() == null) {
				field.setUnderline(false);
			}
			if (StringUtils.isBlank(field.getAlign())) {
				field.setAlign("left");
			}
		}
		Marshaller marshaller = null;

		try {
			marshaller = XMLUtil.getDocumentMarshaller();
			marshaller.marshal(tab, streamWriter);
		} catch (JAXBException jaxbException) {
			LOGGER.error(jaxbException.getMessage(), jaxbException);
		} catch (SAXException saxException) {
			LOGGER.error(saxException.getMessage(), saxException);
		}
		String modifiedMetaData = byteArrayOutputStream.toString();
		return modifiedMetaData;

	}

	/**
	 * Generate Payslip Pdf Template.
	 * 
	 * @param companyId
	 *            the company id
	 * @return pdf in Byte Format
	 */
	@Override
	public byte[] generatePdf(Long companyId, Long employeeId) {
		try {

			PDFThreadLocal.pageNumbers.set(true);
			return generatePaySlipPdf(companyId, employeeId);
		} catch (PDFMultiplePageException mpe) {

			LOGGER.error(mpe.getMessage(), mpe);
			PDFThreadLocal.pageNumbers.set(true);
			return generatePaySlipPdf(companyId, employeeId);
		}
	}

	/**
	 * purpose: Generate Payslip Pdf Template.
	 * 
	 * @param companyId
	 *            the company id
	 * @return pdf in Byte Format
	 */
	private byte[] generatePaySlipPdf(Long companyId, Long employeeId) {
		File tempFile = PDFUtils.getTemporaryFile(employeeId,
				PAYASIA_TEMP_PATH, "Payslip");
		Document document = null;
		OutputStream pdfOut = null;
		InputStream pdfIn = null;
		try {
			document = new Document(PayAsiaPDFConstants.PAGE_SIZE,
					PayAsiaPDFConstants.PAGE_LEFT_MARGIN,
					PayAsiaPDFConstants.PAGE_TOP_MARGIN,
					PayAsiaPDFConstants.PAGE_RIGHT_MARGIN,
					PayAsiaPDFConstants.PAGE_BOTTOM_MARGIN);

			pdfOut = new FileOutputStream(tempFile);

			PdfWriter writer = PdfWriter.getInstance(document, pdfOut);

			PaySlipPDFTemplateDTO pdfTemplateDTO = new PaySlipPDFTemplateDTO();

			paySlipPDFLogoSection.setCompanyId(companyId);
			paySlipPDFLogoSection.setPaySlipPDFTemplateDTO(pdfTemplateDTO);
			paySlipPDFLogoSection.setPayslip(null);

			paySlipPDFLogoSection
					.getPageHeaderHeight(writer, document, 1, null);

			pdfTemplateDTO.getLogoHeaderSection().setHeight(
					paySlipPDFLogoSection.getPageHeaderHeight(writer, document,
							1, null));

			pdfTemplateDTO.getFooterSection().setHeight(
					paySlipPDFLogoSection.getFooterHeight(writer, document,
							null));

			writer.setPageEvent((PdfPageEvent) paySlipPDFLogoSection);

			document.open();

			paySlipPDFOtherSection.setCompanyId(companyId);
			paySlipPDFOtherSection.setPaySlipPDFTemplateDTO(pdfTemplateDTO);
			paySlipPDFOtherSection.preparePaySlipPDFOtherSection(document,
					writer, pdfTemplateDTO, null, null);

			PdfAction action = PdfAction.gotoLocalPage(1, new PdfDestination(
					PdfDestination.FIT, 0, 10000, 1), writer);
			writer.setOpenAction(action);
			writer.setViewerPreferences(PdfWriter.FitWindow
					| PdfWriter.CenterWindow);

			document.close();

			pdfIn = new FileInputStream(tempFile);

			return IOUtils.toByteArray(pdfIn);
		} catch (DocumentException ex) {

			LOGGER.error(ex.getMessage(), ex);
		} catch (IOException ex) {

			LOGGER.error(ex.getMessage(), ex);
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
		} finally {
			if (document != null) {
				try {
					document.close();
				} catch (Exception e) {
					LOGGER.error(e.getMessage(), e);
				}
			}

			IOUtils.closeQuietly(pdfOut);
			IOUtils.closeQuietly(pdfIn);

			try {
				if (tempFile.exists()) {
					tempFile.delete();
				}
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			}

		}
		return null;
	}

	@Override
	public PaySlipDynamicForm getEffectiveFrom(Long companyId) {
		PaySlipDynamicForm paySlipDynamicForm = new PaySlipDynamicForm();
		EntityMaster entityMaster = entityMasterDAO
				.findByEntityName(PayAsiaConstants.PAY_SLIP_ENTITY_NAME);
		Integer logoSectionFormMax = null;
		synchronized (this) {
			logoSectionFormMax = dynamicFormDAO.getMaxVersion(companyId,
					entityMaster.getEntityId(), PayAsiaConstants.LOGO_SECTION);
		}

		if (logoSectionFormMax == null) {
			paySlipDynamicForm.setEffectiveMonth(0);
			paySlipDynamicForm.setEffectiveYear(0);
			paySlipDynamicForm.setEffectivePart(0);
		} else {
			DynamicForm dynamicForm = dynamicFormDAO.findByMaxVersion(
					companyId, entityMaster.getEntityId(), logoSectionFormMax,
					PayAsiaConstants.LOGO_SECTION);
			paySlipDynamicForm.setEffectiveMonth(dynamicForm.getMonthMaster()
					.getMonthId());
			paySlipDynamicForm.setEffectiveYear(dynamicForm.getEffectiveYear());
			paySlipDynamicForm.setEffectivePart(dynamicForm.getEffectivePart());
		}

		return paySlipDynamicForm;

	}

}
