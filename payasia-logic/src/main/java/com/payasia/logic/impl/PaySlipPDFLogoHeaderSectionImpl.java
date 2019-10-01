package com.payasia.logic.impl;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import com.mind.payasia.xml.bean.Field;
import com.mind.payasia.xml.bean.FieldOrder;
import com.mind.payasia.xml.bean.Tab;
import com.mind.payasia.xml.bean.TabColumn;
import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.ColumnPropertyDTO;
import com.payasia.common.dto.PaySlipPDFTemplateDTO;
import com.payasia.common.dto.PayslipRowDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.util.PDFThreadLocal;
import com.payasia.common.util.PDFUtils;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.PayAsiaPDFConstants;
import com.payasia.common.util.XMLUtil;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.DynamicFormDAO;
import com.payasia.dao.DynamicFormRecordDAO;
import com.payasia.dao.EntityMasterDAO;
import com.payasia.dao.bean.DynamicForm;
import com.payasia.dao.bean.DynamicFormRecord;
import com.payasia.dao.bean.EntityMaster;
import com.payasia.dao.bean.Payslip;
import com.payasia.logic.MultilingualLogic;
import com.payasia.logic.PaySlipPDFLogoHeaderSection;
import com.payasia.logic.PayslipDataUtils;

/**
 * The Class PaySlipPDFLogoHeaderSectionImpl.
 */
/**
 * @author vivekjain
 * 
 */
@Component
public class PaySlipPDFLogoHeaderSectionImpl extends PdfPageEventHelper
		implements PaySlipPDFLogoHeaderSection {

	/** The Constant logger. */
	private static final Logger LOGGER = Logger
			.getLogger(PaySlipPDFLogoHeaderSectionImpl.class);

	/** The entity master dao. */
	@Resource
	EntityMasterDAO entityMasterDAO;

	/** The company dao. */
	@Resource
	CompanyDAO companyDAO;

	/** The dynamic form dao. */
	@Resource
	DynamicFormDAO dynamicFormDAO;

	/** The dynamic form Record dao. */
	@Resource
	DynamicFormRecordDAO dynamicFormRecordDAO;

	/** The payslip data utils. */
	@Resource
	PayslipDataUtils payslipDataUtils;

	@Resource
	MultilingualLogic multilingualLogic;

	/** The total. */
	private PdfTemplate total;

	/** The company id. */
	private Long companyId;

	/** The payslip. */
	private Payslip payslip;

	/** The pay slip pdf template dto. */
	private PaySlipPDFTemplateDTO paySlipPDFTemplateDTO;

	/**
	 * Sets the payslip.
	 * 
	 * @param payslip
	 *            the new payslip
	 */
	@Override
	public void setPayslip(Payslip payslip) {
		this.payslip = payslip;
	}

	/**
	 * Get the PaySlipPDFTemplateDTO.
	 * 
	 * @return the pay slip pdf template dto
	 */
	@Override
	public PaySlipPDFTemplateDTO getPaySlipPDFTemplateDTO() {
		return paySlipPDFTemplateDTO;
	}

	/**
	 * set the PaySlipPDFTemplateDTO.
	 * 
	 * @param paySlipPDFTemplateDTO
	 *            the new pay slip pdf template dto
	 */
	@Override
	public void setPaySlipPDFTemplateDTO(
			PaySlipPDFTemplateDTO paySlipPDFTemplateDTO) {
		this.paySlipPDFTemplateDTO = paySlipPDFTemplateDTO;
	}

	/**
	 * set the Company Id.
	 * 
	 * @param companyId
	 *            the new company id
	 */
	@Override
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	/**
	 * Gets the XML.
	 * 
	 * @param companyId
	 *            the company id
	 * @param sectionName
	 *            the section name
	 * @return the Max Dynamic form Meta Data
	 */
	@Override
	public String getXML(Long companyId, String sectionName, Payslip payslip) {
		EntityMaster entityMaster = entityMasterDAO
				.findById(PayAsiaConstants.PAYSLIP_ENTITY_ID);

		if (payslip == null) {
			int maxVersion = dynamicFormDAO.getMaxVersion(companyId,
					entityMaster.getEntityId(), sectionName);
			DynamicForm maxDynamicForm = dynamicFormDAO.findByMaxVersion(
					companyId, entityMaster.getEntityId(), maxVersion,
					sectionName);

			return maxDynamicForm.getMetaData();
		}

		List<Object[]> tuples = dynamicFormDAO.getEffectiveDateByCondition(
				companyId, entityMaster.getEntityId(), sectionName,
				payslip.getYear(), payslip.getMonthMaster().getMonthId(),
				payslip.getPart());

		DynamicForm dynamicForm = null;
		for (Object[] tuple : tuples) {
			BigInteger monthId = (BigInteger) tuple[1];
			dynamicForm = dynamicFormDAO
					.getDynamicFormBasedOnEffectiveDate(companyId,
							entityMaster.getEntityId(), sectionName,
							(Integer) tuple[0], monthId.longValue(),
							(Integer) tuple[2]);
		}

		String xmlString = multilingualLogic
				.convertLabelsToSpecificLanguageWithoutEntity(
						dynamicForm.getMetaData(), UserContext.getLanguageId(),
						companyId, dynamicForm.getId().getFormId());

		return xmlString;
	}

	/**
	 * Runs On Open Document and set the total number of documents.
	 * 
	 * @param writer
	 *            the writer
	 * @param document
	 *            the document
	 */
	@Override
	public void onOpenDocument(PdfWriter writer, Document document) {
		total = writer.getDirectContent().createTemplate(30, 16);

	}

	/**
	 * Runs On Start Page of Pdf template.
	 * 
	 * @param writer
	 *            the writer
	 * @param document
	 *            the document
	 */
	@Override
	public void onStartPage(PdfWriter writer, Document document) {

		int currentPageNumber = writer.getPageNumber();

		PdfPTable headerTable = null;
		PdfPTable footerTable = null;
		try {

			float tableWidth = document.right() - document.left() - 3
					* PayAsiaPDFConstants.X_PADDING;

			List<DynamicFormRecord> dynamicFormRecordList = new ArrayList<DynamicFormRecord>();

			if (payslip != null) {
				EntityMaster payslipEntityMaster = entityMasterDAO
						.findById(PayAsiaConstants.PAYSLIP_FORM_ENTITY_ID);

				List<DynamicFormRecord> dynamicFormPayslipRecordList = dynamicFormRecordDAO
						.findByEntityKey(payslip.getPayslipId(),
								payslipEntityMaster.getEntityId(), payslip
										.getCompany().getCompanyId());
				if (dynamicFormPayslipRecordList != null) {
					dynamicFormRecordList.addAll(dynamicFormPayslipRecordList);
				}

				EntityMaster companyEntityMaster = entityMasterDAO
						.findByEntityName(PayAsiaConstants.COMPANY_ENTITY_NAME);

				List<DynamicFormRecord> dynamicFormCompanyRecordList = dynamicFormRecordDAO
						.findByEntityKey(payslip.getCompany().getCompanyId(),
								companyEntityMaster.getEntityId(), payslip
										.getCompany().getCompanyId());
				if (dynamicFormCompanyRecordList != null) {
					dynamicFormRecordList.addAll(dynamicFormCompanyRecordList);
				}

				EntityMaster employeeEntityMaster = entityMasterDAO
						.findByEntityName(PayAsiaConstants.EMPLOYEE_ENTITY_NAME);
				List<DynamicFormRecord> dynamicFormEmployeeRecordList = dynamicFormRecordDAO
						.findByEntityKey(payslip.getEmployee().getEmployeeId(),
								employeeEntityMaster.getEntityId(), payslip
										.getCompany().getCompanyId());
				if (dynamicFormEmployeeRecordList != null) {
					dynamicFormRecordList.addAll(dynamicFormEmployeeRecordList);
				}

			}
			try {
				headerTable = getLogoSection(writer, document, payslip,
						currentPageNumber, dynamicFormRecordList);
			} catch (JAXBException e) {
				LOGGER.error(e.getMessage(), e);
				throw new PayAsiaSystemException(e.getMessage(), e);
			} catch (SAXException e) {
				LOGGER.error(e.getMessage(), e);
				throw new PayAsiaSystemException(e.getMessage(), e);
			}
			headerTable.setTotalWidth(tableWidth);
			headerTable.writeSelectedRows(0, -1,
					document.left(PayAsiaPDFConstants.X_PADDING),
					document.top(), writer.getDirectContent());
			paySlipPDFTemplateDTO.getLogoHeaderSection().setHeight(
					headerTable.calculateHeights());

			try {
				footerTable = getFooterSection(document, payslip,
						currentPageNumber, dynamicFormRecordList);
			} catch (JAXBException e) {
				LOGGER.error(e.getMessage(), e);
				throw new PayAsiaSystemException(e.getMessage(), e);
			} catch (SAXException e) {
				LOGGER.error(e.getMessage(), e);
				throw new PayAsiaSystemException(e.getMessage(), e);
			}
			footerTable.setTotalWidth(tableWidth);
			footerTable.writeSelectedRows(0, -1,
					document.left(PayAsiaPDFConstants.X_PADDING),
					document.bottom()
							+ paySlipPDFTemplateDTO.getFooterSection()
									.getHeight(), writer.getDirectContent());
			paySlipPDFTemplateDTO.getFooterSection().setHeight(
					footerTable.calculateHeights());

		} catch (BadElementException e) {
			LOGGER.error(e.getMessage(), e);
			throw new PayAsiaSystemException(e.getMessage(), e);
		} catch (MalformedURLException e) {
			LOGGER.error(e.getMessage(), e);
			throw new PayAsiaSystemException(e.getMessage(), e);
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
			throw new PayAsiaSystemException(e.getMessage(), e);
		}

	}

	/**
	 * Gets the logo image.
	 * 
	 * @return the logo image
	 * @throws BadElementException
	 *             the bad element exception
	 * @throws MalformedURLException
	 *             the malformed url exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private static Image getLogoImage() throws BadElementException,
			MalformedURLException, IOException {

		URL imageURL = Thread.currentThread().getContextClassLoader()
				.getResource("images/logo.png");

		Image img = Image.getInstance(imageURL);
		img.scaleToFit(102, 50);
		img.setRotationDegrees(0);
		return img;
	}

	/**
	 * Gets the Logo Section Pdf Table.
	 * 
	 * @param writer
	 *            the writer
	 * @param document
	 *            the document
	 * @param payslip
	 *            the payslip
	 * @param currentPageNumber
	 *            the current page number
	 * @return PdfPTable with Logo and Header Section
	 * @throws BadElementException
	 *             the bad element exception
	 * @throws MalformedURLException
	 *             the malformed url exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws JAXBException
	 *             the jAXB exception
	 * @throws SAXException
	 *             the sAX exception
	 */
	@Override
	public PdfPTable getLogoSection(PdfWriter writer, Document document,
			Payslip payslip, int currentPageNumber,
			List<DynamicFormRecord> dynamicFormRecordList)
			throws BadElementException, MalformedURLException, IOException,
			JAXBException, SAXException {
		Unmarshaller logoUnmarshaller = null;
		logoUnmarshaller = XMLUtil.getDocumentUnmarshaller();
		String logoXML = getXML(companyId, PayAsiaConstants.LOGO_SECTION,
				payslip);
		final StringReader xmlLogoReader = new StringReader(logoXML);
		Source xmlLogoSource = null;
		try {
			xmlLogoSource = XMLUtil.getSAXSource(xmlLogoReader);
		} catch (SAXException | ParserConfigurationException e1) {
			LOGGER.error(e1.getMessage(), e1);
			throw new PayAsiaSystemException(e1.getMessage(),
					e1);
		}

		Tab logoTab = null;
		try {
			logoTab = (Tab) logoUnmarshaller.unmarshal(xmlLogoSource);
		} catch (JAXBException e) {
			LOGGER.error(e.getMessage(), e);
			throw new PayAsiaSystemException(e.getMessage(), e);
		}

		FieldOrder logoFieldOrder = logoTab.getFieldOrder();
		List<TabColumn> logoTabColumns = logoFieldOrder.getTabColumn();

		int logoColumnNumber = 1;

		HashMap<Long, Tab> dataTabMap = new HashMap<>();
		HashMap<Long, DynamicForm> dynamicFormMap = new HashMap<>();
		HashMap<String, ColumnPropertyDTO> staticPropMap = new HashMap<>();

		List<Field> logoLeftColumnList = new ArrayList<>();
		List<Field> logoRightColumnList = new ArrayList<>();

		for (TabColumn logoTabColumn : logoTabColumns) {
			List<String> fieldReferences = logoTabColumn.getFieldReference();
			for (String fieldReference : fieldReferences) {

				List<Field> logoFields = logoTab.getField();

				for (Field logoField : logoFields) {

					if (fieldReference.equalsIgnoreCase(logoField.getName())) {

						try {
							if (payslip != null) {
								payslipDataUtils.getEmployeeFieldValue(payslip,
										logoField, dataTabMap, dynamicFormMap,
										staticPropMap, dynamicFormRecordList);
							}

						} catch (NoSuchMethodException e) {
							LOGGER.error(e.getMessage(), e);
							throw new PayAsiaSystemException(e.getMessage(), e);
						} catch (IllegalAccessException e) {
							LOGGER.error(e.getMessage(), e);
							throw new PayAsiaSystemException(e.getMessage(), e);
						} catch (InvocationTargetException e) {
							LOGGER.error(e.getMessage(), e);
							throw new PayAsiaSystemException(e.getMessage(), e);
						}

						if (logoColumnNumber == 1) {
							if (payslip != null) {
								String fieldValue = logoField.getValue();

								if (logoField.getType().equalsIgnoreCase(
										PayAsiaConstants.FIELD_TYPE_LABEL)
										|| logoField
												.getType()
												.equalsIgnoreCase(
														PayAsiaConstants.FIELD_TYPE_LOGO)) {
									logoLeftColumnList.add(logoField);
								} else {
									if (!isFieldValueBlankOrZero(fieldValue)) {
										logoLeftColumnList.add(logoField);
									}
								}

							} else {
								logoLeftColumnList.add(logoField);
							}

						}
						if (logoColumnNumber == 2) {
							if (payslip != null) {
								String fieldValue = logoField.getValue();
								if (logoField.getType().equalsIgnoreCase(
										PayAsiaConstants.FIELD_TYPE_LABEL)
										|| logoField
												.getType()
												.equalsIgnoreCase(
														PayAsiaConstants.FIELD_TYPE_LOGO)) {
									logoRightColumnList.add(logoField);
								} else {
									if (!isFieldValueBlankOrZero(fieldValue)) {
										logoRightColumnList.add(logoField);
									}
								}

							} else {
								logoRightColumnList.add(logoField);
							}

						}

					}

				}

			}

			logoColumnNumber++;

		}

		int sizeMap1 = logoLeftColumnList.size();
		int sizeMap2 = logoRightColumnList.size();
		int maxRowSize = 0;
		if (sizeMap1 > sizeMap2) {
			maxRowSize = sizeMap1;
			int difference = sizeMap1 - sizeMap2;
			for (int count = 0; count < difference; count++) {
				logoRightColumnList.add(null);
			}
		}

		if (sizeMap1 < sizeMap2) {
			maxRowSize = sizeMap2;
			int difference = sizeMap2 - sizeMap1;
			for (int count = 0; count < difference; count++) {
				logoLeftColumnList.add(null);
			}
		}

		if (maxRowSize == 0) {
			maxRowSize = sizeMap1;
		}

		List<PayslipRowDTO> payslipRowDTOList = new ArrayList<>();
		if (maxRowSize != 0) {
			for (int count = 0; count < maxRowSize; count++) {
				PayslipRowDTO payslipRowDTO = new PayslipRowDTO();

				Field leftFieldTemp = logoLeftColumnList.get(count);
				Field rightFieldTemp = logoRightColumnList.get(count);

				if (leftFieldTemp != null) {
					if (leftFieldTemp.isColspan() != null) {
						if (leftFieldTemp.isColspan()) {
							payslipRowDTO.setColumnField1(leftFieldTemp);
							payslipRowDTO.setColumnField2(null);
						} else {
							payslipRowDTO.setColumnField1(leftFieldTemp);
							payslipRowDTO.setColumnField2(rightFieldTemp);
						}
					} else {
						payslipRowDTO.setColumnField1(leftFieldTemp);
						payslipRowDTO.setColumnField2(rightFieldTemp);
					}

				} else {
					payslipRowDTO.setColumnField1(leftFieldTemp);
					payslipRowDTO.setColumnField2(rightFieldTemp);
				}
				payslipRowDTOList.add(payslipRowDTO);
			}
		}

		PdfPTable headerLogoSectionTable = new PdfPTable(new float[] { 1 });
		float tableWidth = document.right() - document.left() - 3
				* PayAsiaPDFConstants.X_PADDING;
		headerLogoSectionTable.setTotalWidth(tableWidth);

		PdfPCell logoCell = new PdfPCell();
		logoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		logoCell.setPadding(0);

		logoCell.setBorder(0);
		PDFUtils.setDefaultCellLayout(logoCell);

		PdfPTable nestedLogoSectionTable = getLogoSectionRowsTable(
				payslipRowDTOList, payslip);
		nestedLogoSectionTable.setWidthPercentage(100f);
		logoCell.addElement(nestedLogoSectionTable);

		headerLogoSectionTable.addCell(logoCell);

		Unmarshaller unmarshallerHeader = null;
		try {
			unmarshallerHeader = XMLUtil.getDocumentUnmarshaller();
		} catch (JAXBException e) {
			LOGGER.error(e.getMessage(), e);
			throw new PayAsiaSystemException(e.getMessage(), e);
		} catch (SAXException e) {
			LOGGER.error(e.getMessage(), e);
			throw new PayAsiaSystemException(e.getMessage(), e);
		}
		String headerXML = getXML(companyId, PayAsiaConstants.HEADER_SECTION,
				payslip);
		final StringReader xmlHeaderReader = new StringReader(headerXML);
		Source xmlHeaderSource = null;
		try {
			xmlHeaderSource = XMLUtil.getSAXSource(xmlHeaderReader);
		} catch (SAXException | ParserConfigurationException e1) {
			LOGGER.error(e1.getMessage(), e1);
			throw new PayAsiaSystemException(e1.getMessage(),
					e1);
		}

		Tab headerTab = null;
		try {
			headerTab = (Tab) unmarshallerHeader.unmarshal(xmlHeaderSource);
		} catch (JAXBException e) {
			LOGGER.error(e.getMessage(), e);
			throw new PayAsiaSystemException(e.getMessage(), e);
		}

		FieldOrder headerFieldOrder = headerTab.getFieldOrder();
		List<TabColumn> headerTabColumns = headerFieldOrder.getTabColumn();

		int headerColumnNumber = 1;
		boolean headerStatus = false;

		List<Field> headerLeftColumnList = new ArrayList<>();
		List<Field> headerRightColumnList = new ArrayList<>();

		for (TabColumn headerTabColumn : headerTabColumns) {

			List<String> fieldReferences = headerTabColumn.getFieldReference();
			for (String fieldReference : fieldReferences) {
				headerStatus = true;
				List<Field> headerFields = headerTab.getField();

				for (Field headerField : headerFields) {
					if (fieldReference.equalsIgnoreCase(headerField.getName())) {

						try {
							if (payslip != null) {
								payslipDataUtils.getEmployeeFieldValue(payslip,
										headerField, dataTabMap,
										dynamicFormMap, staticPropMap,
										dynamicFormRecordList);
							}
						} catch (NoSuchMethodException e) {
							LOGGER.error(e.getMessage(), e);
							throw new PayAsiaSystemException(e.getMessage(), e);
						} catch (IllegalAccessException e) {
							LOGGER.error(e.getMessage(), e);
							throw new PayAsiaSystemException(e.getMessage(), e);
						} catch (InvocationTargetException e) {
							LOGGER.error(e.getMessage(), e);
							throw new PayAsiaSystemException(e.getMessage(), e);
						}

						if (headerColumnNumber == 1) {
							if (payslip != null) {
								String fieldValue = headerField.getValue();

								if (headerField.getType().equalsIgnoreCase(
										PayAsiaConstants.FIELD_TYPE_LABEL)) {
									headerLeftColumnList.add(headerField);
								} else {
									if (!isFieldValueBlankOrZero(fieldValue)) {
										headerLeftColumnList.add(headerField);
									}
								}

							} else {
								headerLeftColumnList.add(headerField);
							}

						}
						if (headerColumnNumber == 2) {
							if (payslip != null) {
								String fieldValue = headerField.getValue();
								if (headerField.getType().equalsIgnoreCase(
										PayAsiaConstants.FIELD_TYPE_LABEL)) {
									headerRightColumnList.add(headerField);
								} else {
									if (!isFieldValueBlankOrZero(fieldValue)) {
										headerRightColumnList.add(headerField);
									}
								}

							} else {
								headerRightColumnList.add(headerField);
							}

						}

					}

				}

			}
			headerColumnNumber++;

		}

		int sizeHeaderMap1 = headerLeftColumnList.size();
		int sizeHeaderMap2 = headerRightColumnList.size();
		int maxHeaderRowSize = 0;
		if (sizeHeaderMap1 > sizeHeaderMap2) {
			maxHeaderRowSize = sizeHeaderMap1;
			int difference = sizeHeaderMap1 - sizeHeaderMap2;
			for (int count = 0; count < difference; count++) {
				headerRightColumnList.add(null);
			}
		}

		if (sizeHeaderMap1 < sizeHeaderMap2) {
			maxHeaderRowSize = sizeHeaderMap2;
			int difference = sizeHeaderMap2 - sizeHeaderMap1;
			for (int count = 0; count < difference; count++) {
				headerLeftColumnList.add(null);
			}
		}

		if (maxHeaderRowSize == 0) {
			maxHeaderRowSize = sizeHeaderMap1;
		}

		List<PayslipRowDTO> payslipHeaderRowDTOList = new ArrayList<>();
		if (maxHeaderRowSize != 0) {
			for (int count = 0; count < maxHeaderRowSize; count++) {
				PayslipRowDTO payslipHeaderRowDTO = new PayslipRowDTO();

				Field leftHeaderFieldTemp = headerLeftColumnList.get(count);
				Field rightHeaderFieldTemp = headerRightColumnList.get(count);

				if (leftHeaderFieldTemp != null) {
					if (leftHeaderFieldTemp.isColspan() != null) {
						if (leftHeaderFieldTemp.isColspan()) {
							payslipHeaderRowDTO
									.setColumnField1(leftHeaderFieldTemp);
							payslipHeaderRowDTO.setColumnField2(null);
						} else {
							payslipHeaderRowDTO
									.setColumnField1(leftHeaderFieldTemp);
							payslipHeaderRowDTO
									.setColumnField2(rightHeaderFieldTemp);
						}
					} else {
						payslipHeaderRowDTO
								.setColumnField1(leftHeaderFieldTemp);
						payslipHeaderRowDTO
								.setColumnField2(rightHeaderFieldTemp);
					}

				} else {
					payslipHeaderRowDTO.setColumnField1(leftHeaderFieldTemp);
					payslipHeaderRowDTO.setColumnField2(rightHeaderFieldTemp);
				}
				payslipHeaderRowDTOList.add(payslipHeaderRowDTO);
			}
		}

		PdfPCell mainCell = new PdfPCell();
		mainCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		mainCell.setPadding(0);

		mainCell.setBorder(0);
		PDFUtils.setDefaultCellLayout(mainCell);

		headerStatus = true;
		if (headerStatus) {
			if (headerXML != "footerSection") {
				mainCell.enableBorderSide(Rectangle.BOTTOM);
			}
		}

		PdfPTable nestedMainSectionTable = getHeaderSectionRowsTable(
				payslipHeaderRowDTOList, payslip);
		nestedMainSectionTable.setWidthPercentage(100f);
		nestedMainSectionTable.setSpacingAfter(20f);
		mainCell.addElement(nestedMainSectionTable);

		headerLogoSectionTable.addCell(mainCell);
		return headerLogoSectionTable;

	}

	private PdfPTable getLogoSectionRowsTable(
			List<PayslipRowDTO> payslipRowDTOList, Payslip payslip)
			throws BadElementException, MalformedURLException, IOException {

		PdfPTable otherSectionRowsTable = new PdfPTable(new float[] { 1, 1 });
		otherSectionRowsTable.getTotalHeight();

		otherSectionRowsTable.setSpacingAfter(20f);

		for (PayslipRowDTO payslipRowDTO : payslipRowDTOList) {

			if (payslipRowDTO.getColumnField1() != null) {
				if (payslipRowDTO.getColumnField1().isColspan() != null) {
					if (!payslipRowDTO.getColumnField1().isColspan()) {
						PdfPCell leftCell = getLogoLeftSection(
								payslipRowDTO.getColumnField1(), payslip);
						leftCell.setBorder(0);
						PdfPCell rightCell = null;
						if (payslipRowDTO.getColumnField2() != null) {
							rightCell = getLogoMiddleSection(
									payslipRowDTO.getColumnField2(), payslip);
						} else {
							rightCell = getEmptyCell();
						}
						rightCell.setBorder(0);
						otherSectionRowsTable.addCell(leftCell);
						otherSectionRowsTable.addCell(rightCell);
					} else {
						PdfPCell colSpanCell = getColSpanTwoCell(
								payslipRowDTO.getColumnField1(), payslip);
						colSpanCell.setColspan(2);
						colSpanCell.setHorizontalAlignment(Element.ALIGN_LEFT);
						colSpanCell.setPadding(0);
						colSpanCell.setBorder(0);
						otherSectionRowsTable.addCell(colSpanCell);
					}
				} else {
					PdfPCell leftCell = getLogoLeftSection(
							payslipRowDTO.getColumnField1(), payslip);
					leftCell.setBorder(0);
					PdfPCell rightCell = null;
					if (payslipRowDTO.getColumnField2() != null) {
						rightCell = getLogoMiddleSection(
								payslipRowDTO.getColumnField2(), payslip);
					} else {
						rightCell = getEmptyCell();
					}
					rightCell.setBorder(0);
					otherSectionRowsTable.addCell(leftCell);
					otherSectionRowsTable.addCell(rightCell);
				}

			} else {
				PdfPCell leftCell = getEmptyCell();
				leftCell.setPadding(0);
				PdfPCell rightCell = null;
				if (payslipRowDTO.getColumnField2() != null) {
					rightCell = getLogoMiddleSection(
							payslipRowDTO.getColumnField2(), payslip);
					rightCell.setBorder(0);
				} else {
					rightCell = getEmptyCell();
				}
				leftCell.setBorder(0);
				otherSectionRowsTable.addCell(leftCell);
				otherSectionRowsTable.addCell(rightCell);
			}

		}

		return otherSectionRowsTable;
	}

	private PdfPTable getHeaderSectionRowsTable(
			List<PayslipRowDTO> payslipRowDTOList, Payslip payslip)
			throws BadElementException, MalformedURLException, IOException {

		PdfPTable otherSectionRowsTable = new PdfPTable(new float[] { 1, 1 });
		otherSectionRowsTable.getTotalHeight();

		otherSectionRowsTable.setSpacingAfter(20f);

		for (PayslipRowDTO payslipRowDTO : payslipRowDTOList) {

			if (payslipRowDTO.getColumnField1() != null) {
				if (payslipRowDTO.getColumnField1().isColspan() != null) {
					if (!payslipRowDTO.getColumnField1().isColspan()) {
						PdfPCell leftCell = getHeaderLeftColumn(
								payslipRowDTO.getColumnField1(), payslip);
						leftCell.setBorder(0);
						PdfPCell rightCell = null;
						if (payslipRowDTO.getColumnField2() != null) {
							rightCell = getHeaderRightColumn(
									payslipRowDTO.getColumnField2(), payslip);
						} else {
							rightCell = getEmptyCell();
						}
						rightCell.setBorder(0);
						otherSectionRowsTable.addCell(leftCell);
						otherSectionRowsTable.addCell(rightCell);
					} else {
						PdfPCell colSpanCell = getColSpanTwoCell(
								payslipRowDTO.getColumnField1(), payslip);
						colSpanCell.setColspan(2);
						colSpanCell.setHorizontalAlignment(Element.ALIGN_LEFT);
						colSpanCell.setPadding(0);
						colSpanCell.setBorder(0);
						otherSectionRowsTable.addCell(colSpanCell);
					}
				} else {
					PdfPCell leftCell = getHeaderLeftColumn(
							payslipRowDTO.getColumnField1(), payslip);
					leftCell.setBorder(0);
					PdfPCell rightCell = null;
					if (payslipRowDTO.getColumnField2() != null) {
						rightCell = getHeaderRightColumn(
								payslipRowDTO.getColumnField2(), payslip);
					} else {
						rightCell = getEmptyCell();
					}
					rightCell.setBorder(0);
					otherSectionRowsTable.addCell(leftCell);
					otherSectionRowsTable.addCell(rightCell);
				}

			} else {
				PdfPCell leftCell = getEmptyCell();
				leftCell.setPadding(0);
				PdfPCell rightCell = null;
				if (payslipRowDTO.getColumnField2() != null) {
					rightCell = getHeaderRightColumn(
							payslipRowDTO.getColumnField2(), payslip);
					rightCell.setBorder(0);
				} else {
					rightCell = getEmptyCell();
				}
				leftCell.setBorder(0);
				otherSectionRowsTable.addCell(leftCell);
				otherSectionRowsTable.addCell(rightCell);
			}

		}

		return otherSectionRowsTable;
	}

	private PdfPCell getEmptyCell() {
		PdfPCell logoLeftCell = new PdfPCell();
		logoLeftCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		logoLeftCell.setPadding(0);
		logoLeftCell.setBorder(0);

		PdfPTable nestedlogoLeftTable = new PdfPTable(new float[] { 1, 1 });
		nestedlogoLeftTable.setWidthPercentage(100f);
		nestedlogoLeftTable.getTotalHeight();

		PdfPCell leftCell = new PdfPCell(new Phrase(" ", getBaseFontBold()));
		leftCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		leftCell.setPadding(2);
		leftCell.setBorder(0);
		leftCell.setFixedHeight(17);
		PdfPCell rightCell = new PdfPCell(new Phrase(" ", getBaseFontNormal()));
		rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		rightCell.setPadding(2);
		rightCell.setBorder(0);
		rightCell.setFixedHeight(17);
		nestedlogoLeftTable.addCell(leftCell);
		nestedlogoLeftTable.addCell(rightCell);

		logoLeftCell.addElement(nestedlogoLeftTable);
		return logoLeftCell;
	}

	private PdfPCell getColSpanTwoCell(Field field, Payslip payslip) {

		PdfPCell columnCell = new PdfPCell();
		columnCell.setColspan(2);
		columnCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		columnCell.setPadding(0);
		columnCell.setBorder(0);

		PdfPTable nestedColumnTable = null;

		if (field.getType().equalsIgnoreCase(PayAsiaConstants.FIELD_TYPE_TEXT)) {
			nestedColumnTable = new PdfPTable(new float[] { 1, 1, 1, 1 });
			nestedColumnTable.setWidthPercentage(100f);
			nestedColumnTable.getTotalHeight();

			String base64Str = new String(Base64.decodeBase64(field
					.getDictionaryName().getBytes()));

			try {
				base64Str = new String(URLDecoder.decode(base64Str, "UTF8"));
			} catch (UnsupportedEncodingException uee) {
				LOGGER.error(uee.getMessage(), uee);
				throw new PayAsiaSystemException(uee.getMessage(), uee);
			} catch (IllegalArgumentException ill) {
				LOGGER.error(ill.getMessage(), ill);
			}
			Font baseFont = setBaseFontForField(field);

			PdfPCell nestedLeftCell = null;
			PdfPCell nestedRightCell = null;

			nestedLeftCell = new PdfPCell(new Phrase(base64Str, baseFont));
			nestedLeftCell.setPadding(2);
			nestedLeftCell.setBorder(0);
			if (payslip != null) {
				String fieldValue = field.getValue();
				if (!isFieldValueBlankOrZero(fieldValue)) {
					if (field.isDefault()) {
						nestedRightCell = new PdfPCell(new Phrase(
								field.getValue(), baseFont));
					} else {
						try {
							nestedRightCell = new PdfPCell(new Phrase(
									field.getValue(), baseFont));
						} catch (NumberFormatException e) {
							nestedRightCell = new PdfPCell(new Phrase(
									field.getValue(), baseFont));
						}
					}
					nestedRightCell.setPadding(2);
					nestedRightCell.setBorder(0);
					setPayslipColDataAlignment(field, nestedLeftCell,
							nestedRightCell);
					nestedRightCell.setColspan(3);

					if (field.isHideLabel() != null && field.isHideLabel()) {
						nestedRightCell.setColspan(4);
						nestedColumnTable.addCell(nestedRightCell);
					} else {
						nestedColumnTable.addCell(nestedLeftCell);
						nestedColumnTable.addCell(nestedRightCell);
					}
				}
			} else {
				nestedRightCell = new PdfPCell(new Phrase(
						PayAsiaConstants.PAYSLIP_FIELD_VALUE, baseFont));
				nestedRightCell.setPadding(2);
				nestedRightCell.setBorder(0);
				nestedRightCell.setColspan(3);
				setPayslipColDataAlignment(field, nestedLeftCell,
						nestedRightCell);
			}

			if (payslip != null) {

			} else {
				if (field.isHideLabel() != null && field.isHideLabel()) {
					nestedRightCell.setColspan(4);
					nestedColumnTable.addCell(nestedRightCell);
				} else {
					nestedColumnTable.addCell(nestedLeftCell);
					nestedColumnTable.addCell(nestedRightCell);
				}
			}

		}
		if (field.getType().equalsIgnoreCase(PayAsiaConstants.FIELD_TYPE_LABEL)) {
			nestedColumnTable = new PdfPTable(new float[] { 1 });
			nestedColumnTable.setWidthPercentage(100f);
			nestedColumnTable.getTotalHeight();
			PdfPCell nestedLeftCell;
			String base64Str = new String(Base64.decodeBase64(field
					.getDictionaryName().getBytes()));

			try {
				base64Str = new String(URLDecoder.decode(base64Str, "UTF8"));
			} catch (UnsupportedEncodingException uee) {
				LOGGER.error(uee.getMessage(), uee);
				throw new PayAsiaSystemException(uee.getMessage(), uee);
			} catch (IllegalArgumentException ill) {
				LOGGER.error(ill.getMessage(), ill);
			}

			Font baseFont = setBaseFontForField(field);
			if (StringUtils.isNotBlank(base64Str)) {
				nestedLeftCell = new PdfPCell(new Phrase(base64Str, baseFont));
			} else {
				nestedLeftCell = new PdfPCell(new Phrase(new String(
						Base64.decodeBase64(field.getLabel().getBytes())),
						baseFont));
			}

			if ("left".equalsIgnoreCase(field.getAlign())) {
				nestedLeftCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			} else if ("center".equalsIgnoreCase(field.getAlign())) {
				nestedLeftCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			} else {
				nestedLeftCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			}
			nestedLeftCell.setFixedHeight(17);
			nestedLeftCell.setPadding(2);
			nestedLeftCell.setBorder(0);
			nestedColumnTable.addCell(nestedLeftCell);
		}

		columnCell.addElement(nestedColumnTable);
		return columnCell;
	}

	private void setPayslipColDataAlignment(Field field, PdfPCell leftCell,
			PdfPCell rightCell) {
		if (field.getAlign() != null
				&& "right".equalsIgnoreCase(field.getAlign())) {
			leftCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		} else if (field.getAlign() != null
				&& "center".equalsIgnoreCase(field.getAlign())) {
			leftCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			rightCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		} else if (field.getAlign() != null
				&& "left".equalsIgnoreCase(field.getAlign())) {
			leftCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			rightCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		} else {
			leftCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		}
	}

	private Font setBaseFontForField(Field field) {
		Font baseFont = null;
		if (field.isBold() != null && field.isUnderline() != null
				&& field.isBold() && field.isUnderline()) {
			baseFont = getBaseFontBoldUnderLine();
		} else if ((field.isBold() != null && field.isBold())
				&& (field.isUnderline() != null && !field.isUnderline())) {
			baseFont = getBaseFontBold();
		} else if ((field.isBold() != null && !field.isBold())
				&& (field.isUnderline() != null && field.isUnderline())) {
			baseFont = getBaseFontUnderLine();
		} else {
			baseFont = getBaseFontNormal();
		}
		return baseFont;
	}

	/**
	 * Gets the logo left section.
	 * 
	 * @param field
	 *            the field
	 * @param payslip
	 *            the payslip
	 * @return the logo left section
	 * @throws BadElementException
	 *             the bad element exception
	 * @throws MalformedURLException
	 *             the malformed url exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private PdfPCell getLogoLeftSection(Field field, Payslip payslip)
			throws BadElementException, MalformedURLException, IOException {

		PdfPCell logoLeftCell = new PdfPCell();
		logoLeftCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		logoLeftCell.setPadding(0);
		logoLeftCell.setBorder(0);

		PdfPTable nestedlogoLeftTable = new PdfPTable(new float[] { 1, 1 });
		nestedlogoLeftTable.setWidthPercentage(100f);
		nestedlogoLeftTable.getTotalHeight();

		if (field.getType().equalsIgnoreCase(PayAsiaConstants.FIELD_TYPE_LOGO)) {
			Image img;
			if (payslip != null) {
				img = payslipDataUtils.getLogoImage(payslip);
			} else {
				img = getLogoImage();
			}
			PdfPCell logoCell = new PdfPCell(img);
			logoCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			logoCell.setColspan(2);
			logoCell.setBorder(0);
			logoCell.setPadding(2);
			nestedlogoLeftTable.addCell(logoCell);
		} else if (field.getType().equalsIgnoreCase(
				PayAsiaConstants.FIELD_TYPE_CALCULATORY_FIELD)) {
			String base64Str = new String(Base64.decodeBase64(field
					.getDictionaryName().getBytes()));

			try {
				base64Str = new String(URLDecoder.decode(base64Str, "UTF8"));
			} catch (UnsupportedEncodingException uee) {
				LOGGER.error(uee.getMessage(), uee);
				throw new PayAsiaSystemException(uee.getMessage(), uee);
			} catch (IllegalArgumentException ill) {
				LOGGER.error(ill.getMessage(), ill);
			}

			Font baseFont = setBaseFontForField(field);

			PdfPCell leftCell = null;
			PdfPCell rightCell = null;
			if (field.isBold() == null && field.isUnderline() == null) {
				leftCell = new PdfPCell(
						new Phrase(base64Str, getBaseFontBold()));
			} else {
				leftCell = new PdfPCell(new Phrase(base64Str, baseFont));
			}
			leftCell.setPadding(2);
			leftCell.setBorder(0);
			if (payslip != null) {
				String fieldValue = field.getValue();
				if (!isFieldValueBlankOrZero(fieldValue)) {
					if (field.isDefault()) {
						rightCell = new PdfPCell(new Phrase(field.getValue(),
								baseFont));
					} else {
						try {
							rightCell = new PdfPCell(new Phrase(
									field.getValue(), baseFont));
						} catch (NumberFormatException e) {
							rightCell = new PdfPCell(new Phrase(
									field.getValue(), baseFont));
						}
					}
					rightCell.setPadding(2);
					rightCell.setBorder(0);
					setPayslipColDataAlignment(field, leftCell, rightCell);
					if (field.isHideLabel() != null && field.isHideLabel()) {
						rightCell.setColspan(2);
						nestedlogoLeftTable.addCell(rightCell);
					} else {
						nestedlogoLeftTable.addCell(leftCell);
						nestedlogoLeftTable.addCell(rightCell);
					}
				}
			} else {
				rightCell = new PdfPCell(new Phrase(
						PayAsiaConstants.PAYSLIP_FIELD_VALUE, baseFont));
				rightCell.setPadding(2);
				rightCell.setBorder(0);
				setPayslipColDataAlignment(field, leftCell, rightCell);
			}

			if (payslip != null) {

			} else {
				if (field.isHideLabel() != null && field.isHideLabel()) {
					rightCell.setColspan(2);
					nestedlogoLeftTable.addCell(rightCell);
				} else {
					nestedlogoLeftTable.addCell(leftCell);
					nestedlogoLeftTable.addCell(rightCell);
				}
			}

		} else if (field.getType().equalsIgnoreCase(
				PayAsiaConstants.FIELD_TYPE_TEXT)) {
			String base64Str = new String(Base64.decodeBase64(field
					.getDictionaryName().getBytes()));

			try {
				base64Str = new String(URLDecoder.decode(base64Str, "UTF8"));
			} catch (UnsupportedEncodingException uee) {
				LOGGER.error(uee.getMessage(), uee);
				throw new PayAsiaSystemException(uee.getMessage(), uee);
			} catch (IllegalArgumentException ill) {
				LOGGER.error(ill.getMessage(), ill);
			}

			Font baseFont = setBaseFontForField(field);

			PdfPCell leftCell = null;
			PdfPCell rightCell = null;
			if (field.isBold() == null && field.isUnderline() == null) {
				leftCell = new PdfPCell(
						new Phrase(base64Str, getBaseFontBold()));
			} else {
				leftCell = new PdfPCell(new Phrase(base64Str, baseFont));
			}
			leftCell.setPadding(2);
			leftCell.setBorder(0);
			if (payslip != null) {
				String fieldValue = field.getValue();
				if (!isFieldValueBlankOrZero(fieldValue)) {
					if (field.isDefault()) {
						rightCell = new PdfPCell(new Phrase(field.getValue(),
								baseFont));
					} else {
						try {
							rightCell = new PdfPCell(new Phrase(
									field.getValue(), baseFont));
						} catch (NumberFormatException e) {
							rightCell = new PdfPCell(new Phrase(
									field.getValue(), baseFont));
						}
					}
					rightCell.setPadding(2);
					rightCell.setBorder(0);
					setPayslipColDataAlignment(field, leftCell, rightCell);
					if (field.isHideLabel() != null && field.isHideLabel()) {
						rightCell.setColspan(2);
						nestedlogoLeftTable.addCell(rightCell);
					} else {
						nestedlogoLeftTable.addCell(leftCell);
						nestedlogoLeftTable.addCell(rightCell);
					}
				}
			} else {
				rightCell = new PdfPCell(new Phrase(
						PayAsiaConstants.PAYSLIP_FIELD_VALUE, baseFont));
				rightCell.setPadding(2);
				rightCell.setBorder(0);
				setPayslipColDataAlignment(field, leftCell, rightCell);
			}

			if (payslip != null) {

			} else {
				if (field.isHideLabel() != null && field.isHideLabel()) {
					rightCell.setColspan(2);
					nestedlogoLeftTable.addCell(rightCell);
				} else {
					nestedlogoLeftTable.addCell(leftCell);
					nestedlogoLeftTable.addCell(rightCell);
				}
			}

		} else if (field.getType().equalsIgnoreCase(
				PayAsiaConstants.FIELD_TYPE_NUMERIC)) {
			String base64Str = new String(Base64.decodeBase64(field
					.getDictionaryName().getBytes()));

			try {
				base64Str = new String(URLDecoder.decode(base64Str, "UTF8"));
			} catch (UnsupportedEncodingException uee) {
				LOGGER.error(uee.getMessage(), uee);
				throw new PayAsiaSystemException(uee.getMessage(), uee);
			} catch (IllegalArgumentException ill) {
				LOGGER.error(ill.getMessage(), ill);
			}

			Font baseFont = setBaseFontForField(field);

			PdfPCell leftCell = null;
			PdfPCell rightCell = null;

			if (field.isBold() == null && field.isUnderline() == null) {
				leftCell = new PdfPCell(
						new Phrase(base64Str, getBaseFontBold()));
			} else {
				leftCell = new PdfPCell(new Phrase(base64Str, baseFont));
			}
			leftCell.setPadding(2);
			leftCell.setBorder(0);
			if (payslip != null) {
				String fieldValue = field.getValue();
				if (!isFieldValueBlankOrZero(fieldValue)) {
					if (field.isDefault()) {
						rightCell = new PdfPCell(new Phrase(field.getValue(),
								baseFont));
					} else {
						try {
							rightCell = new PdfPCell(new Phrase(
									field.getValue(), baseFont));
						} catch (NumberFormatException e) {
							rightCell = new PdfPCell(new Phrase(
									field.getValue(), baseFont));
						}
					}
					rightCell.setPadding(2);
					rightCell.setBorder(0);
					setPayslipColDataAlignment(field, leftCell, rightCell);
					if (field.isHideLabel() != null && field.isHideLabel()) {
						rightCell.setColspan(2);
						nestedlogoLeftTable.addCell(rightCell);
					} else {
						nestedlogoLeftTable.addCell(leftCell);
						nestedlogoLeftTable.addCell(rightCell);
					}
				}
			} else {
				rightCell = new PdfPCell(new Phrase(
						PayAsiaConstants.PAYSLIP_FIELD_VALUE, baseFont));
				rightCell.setPadding(2);
				rightCell.setBorder(0);
				setPayslipColDataAlignment(field, leftCell, rightCell);
			}

			if (payslip != null) {

			} else {
				if (field.isHideLabel() != null && field.isHideLabel()) {
					rightCell.setColspan(2);
					nestedlogoLeftTable.addCell(rightCell);
				} else {
					nestedlogoLeftTable.addCell(leftCell);
					nestedlogoLeftTable.addCell(rightCell);
				}
			}

		} else if (field.getType().equalsIgnoreCase(
				PayAsiaConstants.FIELD_TYPE_DATE)) {
			String base64Str = new String(Base64.decodeBase64(field
					.getDictionaryName().getBytes()));

			try {
				base64Str = new String(URLDecoder.decode(base64Str, "UTF8"));
			} catch (UnsupportedEncodingException uee) {
				LOGGER.error(uee.getMessage(), uee);
				throw new PayAsiaSystemException(uee.getMessage(), uee);
			} catch (IllegalArgumentException ill) {
				LOGGER.error(ill.getMessage(), ill);
			}

			Font baseFont = setBaseFontForField(field);

			PdfPCell leftCell = null;
			PdfPCell rightCell = null;

			if (field.isBold() == null && field.isUnderline() == null) {
				leftCell = new PdfPCell(
						new Phrase(base64Str, getBaseFontBold()));
			} else {
				leftCell = new PdfPCell(new Phrase(base64Str, baseFont));
			}
			leftCell.setPadding(2);
			leftCell.setBorder(0);
			if (payslip != null) {
				String fieldValue = field.getValue();
				if (!isFieldValueBlankOrZero(fieldValue)) {
					if (field.isDefault()) {
						rightCell = new PdfPCell(new Phrase(field.getValue(),
								baseFont));
					} else {
						try {
							rightCell = new PdfPCell(new Phrase(
									field.getValue(), baseFont));
						} catch (NumberFormatException e) {
							rightCell = new PdfPCell(new Phrase(
									field.getValue(), baseFont));
						}
					}
					rightCell.setPadding(2);
					rightCell.setBorder(0);
					setPayslipColDataAlignment(field, leftCell, rightCell);
					if (field.isHideLabel() != null && field.isHideLabel()) {
						rightCell.setColspan(2);
						nestedlogoLeftTable.addCell(rightCell);
					} else {
						nestedlogoLeftTable.addCell(leftCell);
						nestedlogoLeftTable.addCell(rightCell);
					}
				}
			} else {
				rightCell = new PdfPCell(new Phrase(
						PayAsiaConstants.PAYSLIP_FIELD_VALUE, baseFont));
				rightCell.setPadding(2);
				rightCell.setBorder(0);
				setPayslipColDataAlignment(field, leftCell, rightCell);
			}

			if (field.isHideLabel() != null && field.isHideLabel()) {
				rightCell.setColspan(2);
				nestedlogoLeftTable.addCell(rightCell);
			} else {
				nestedlogoLeftTable.addCell(leftCell);
				nestedlogoLeftTable.addCell(rightCell);
			}

		} else if (field.getType().equalsIgnoreCase(
				PayAsiaConstants.FIELD_TYPE_LABEL)) {
			String base64Str = new String(Base64.decodeBase64(field
					.getDictionaryName().getBytes()));

			try {
				base64Str = new String(URLDecoder.decode(base64Str, "UTF8"));
			} catch (UnsupportedEncodingException uee) {
				LOGGER.error(uee.getMessage(), uee);
				throw new PayAsiaSystemException(uee.getMessage(), uee);
			} catch (IllegalArgumentException ill) {
				LOGGER.error(ill.getMessage(), ill);
			}

			Font baseFont = setBaseFontForField(field);

			PdfPCell labelCell;

			if (StringUtils.isNotBlank(base64Str)) {
				if (field.isBold() == null && field.isUnderline() == null) {
					labelCell = new PdfPCell(new Phrase(base64Str,
							getBaseFontBold()));
				} else {
					labelCell = new PdfPCell(new Phrase(base64Str, baseFont));
				}

			} else {
				if (field.isBold() == null && field.isUnderline() == null) {
					labelCell = new PdfPCell(new Phrase(new String(
							Base64.decodeBase64(field.getLabel().getBytes())),
							getBaseFontBold()));
				} else {
					labelCell = new PdfPCell(new Phrase(new String(
							Base64.decodeBase64(field.getLabel().getBytes())),
							baseFont));
				}

			}

			if (field.getAlign() != null
					&& "right".equalsIgnoreCase(field.getAlign())) {
				labelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			} else if (field.getAlign() != null
					&& "center".equalsIgnoreCase(field.getAlign())) {
				labelCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			} else {
				labelCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			}

			labelCell.setColspan(2);
			labelCell.setPadding(2);
			labelCell.setBorder(0);

			if (field.isHideLabel() != null && field.isHideLabel()) {
				PdfPCell hideLabelCell = new PdfPCell(new Phrase("",
						getBaseFontNormal()));
				nestedlogoLeftTable.addCell(hideLabelCell);
			} else {
				nestedlogoLeftTable.addCell(labelCell);
			}

		}

		logoLeftCell.addElement(nestedlogoLeftTable);
		return logoLeftCell;

	}

	/**
	 * Gets the logo middle section.
	 * 
	 * @param field
	 *            the field
	 * @param payslip
	 *            the payslip
	 * @return the logo middle section
	 * @throws BadElementException
	 *             the bad element exception
	 * @throws MalformedURLException
	 *             the malformed url exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private PdfPCell getLogoMiddleSection(Field field, Payslip payslip)
			throws BadElementException, MalformedURLException, IOException {
		PdfPCell logoMiddleCell = new PdfPCell();
		logoMiddleCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		logoMiddleCell.setPadding(0);

		PdfPTable nestedlogoMiddleTable = new PdfPTable(new float[] { 1, 1 });
		nestedlogoMiddleTable.setWidthPercentage(100f);
		nestedlogoMiddleTable.getTotalHeight();

		if (field.getType().equalsIgnoreCase(PayAsiaConstants.FIELD_TYPE_LOGO)) {
			Image img;
			if (payslip != null) {
				img = payslipDataUtils.getLogoImage(payslip);
			} else {
				img = getLogoImage();
			}
			PdfPCell logoCell = new PdfPCell(img);
			logoCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			logoCell.setPaddingLeft(5);
			logoCell.setColspan(2);
			logoCell.setBorder(0);
			logoCell.setPadding(2);
			nestedlogoMiddleTable.addCell(logoCell);
		} else if (field.getType().equalsIgnoreCase(
				PayAsiaConstants.FIELD_TYPE_TEXT)) {
			String base64Str = new String(Base64.decodeBase64(field
					.getDictionaryName().getBytes()));

			try {
				base64Str = new String(URLDecoder.decode(base64Str, "UTF8"));
			} catch (UnsupportedEncodingException uee) {
				LOGGER.error(uee.getMessage(), uee);
				throw new PayAsiaSystemException(uee.getMessage(), uee);
			} catch (IllegalArgumentException ill) {
				LOGGER.error(ill.getMessage(), ill);
			}

			Font baseFont = setBaseFontForField(field);

			PdfPCell leftCell = null;
			PdfPCell rightCell = null;

			if (field.isBold() == null && field.isUnderline() == null) {
				leftCell = new PdfPCell(
						new Phrase(base64Str, getBaseFontBold()));
			} else {
				leftCell = new PdfPCell(new Phrase(base64Str, baseFont));
			}
			leftCell.setPadding(2);
			leftCell.setBorder(0);
			leftCell.setPaddingLeft(5);
			if (payslip != null) {
				String fieldValue = field.getValue();
				if (!isFieldValueBlankOrZero(fieldValue)) {
					if (field.isDefault()) {
						rightCell = new PdfPCell(new Phrase(field.getValue(),
								baseFont));
					} else {
						try {
							rightCell = new PdfPCell(new Phrase(
									field.getValue(), baseFont));
						} catch (NumberFormatException e) {
							rightCell = new PdfPCell(new Phrase(
									field.getValue(), baseFont));
						}
					}
					rightCell.setPadding(2);
					rightCell.setBorder(0);
					setPayslipColDataAlignment(field, leftCell, rightCell);
					if (field.isHideLabel() != null && field.isHideLabel()) {
						rightCell.setColspan(2);
						nestedlogoMiddleTable.addCell(rightCell);
					} else {
						nestedlogoMiddleTable.addCell(leftCell);
						nestedlogoMiddleTable.addCell(rightCell);
					}
				}
			} else {
				rightCell = new PdfPCell(new Phrase(
						PayAsiaConstants.PAYSLIP_FIELD_VALUE, baseFont));
				rightCell.setPadding(2);
				rightCell.setBorder(0);
				setPayslipColDataAlignment(field, leftCell, rightCell);
			}

			if (payslip != null) {

			} else {
				if (field.isHideLabel() != null && field.isHideLabel()) {
					rightCell.setColspan(2);
					nestedlogoMiddleTable.addCell(rightCell);
				} else {
					nestedlogoMiddleTable.addCell(leftCell);
					nestedlogoMiddleTable.addCell(rightCell);
				}
			}

		} else if (field.getType().equalsIgnoreCase(
				PayAsiaConstants.FIELD_TYPE_NUMERIC)) {
			String base64Str = new String(Base64.decodeBase64(field
					.getDictionaryName().getBytes()));

			try {
				base64Str = new String(URLDecoder.decode(base64Str, "UTF8"));
			} catch (UnsupportedEncodingException uee) {
				LOGGER.error(uee.getMessage(), uee);
				throw new PayAsiaSystemException(uee.getMessage(), uee);
			} catch (IllegalArgumentException ill) {
				LOGGER.error(ill.getMessage(), ill);
			}

			Font baseFont = setBaseFontForField(field);

			PdfPCell leftCell = null;
			PdfPCell rightCell = null;

			if (field.isBold() == null && field.isUnderline() == null) {
				leftCell = new PdfPCell(
						new Phrase(base64Str, getBaseFontBold()));
			} else {
				leftCell = new PdfPCell(new Phrase(base64Str, baseFont));
			}
			leftCell.setPadding(2);
			leftCell.setBorder(0);
			leftCell.setPaddingLeft(5);
			if (payslip != null) {
				String fieldValue = field.getValue();
				if (!isFieldValueBlankOrZero(fieldValue)) {
					if (field.isDefault()) {
						rightCell = new PdfPCell(new Phrase(field.getValue(),
								baseFont));
					} else {
						try {
							rightCell = new PdfPCell(new Phrase(
									field.getValue(), baseFont));
						} catch (NumberFormatException e) {
							rightCell = new PdfPCell(new Phrase(
									field.getValue(), baseFont));
						}
					}
					rightCell.setPadding(2);
					rightCell.setBorder(0);
					if (field.isHideLabel() != null && field.isHideLabel()) {
						rightCell.setColspan(2);
						nestedlogoMiddleTable.addCell(rightCell);
					} else {
						nestedlogoMiddleTable.addCell(leftCell);
						nestedlogoMiddleTable.addCell(rightCell);
					}
				}
			} else {
				rightCell = new PdfPCell(new Phrase(
						PayAsiaConstants.PAYSLIP_FIELD_VALUE, baseFont));
				rightCell.setPadding(2);
				rightCell.setBorder(0);
			}
			if (rightCell != null) {
				if (field.getAlign() != null
						&& "right".equalsIgnoreCase(field.getAlign())) {
					leftCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				} else if (field.getAlign() != null
						&& "center".equalsIgnoreCase(field.getAlign())) {
					leftCell.setHorizontalAlignment(Element.ALIGN_CENTER);
					rightCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				} else {
					leftCell.setHorizontalAlignment(Element.ALIGN_LEFT);
					rightCell.setHorizontalAlignment(Element.ALIGN_LEFT);
				}

				if (payslip != null) {

				} else {
					if (field.isHideLabel() != null && field.isHideLabel()) {
						rightCell.setColspan(2);
						nestedlogoMiddleTable.addCell(rightCell);
					} else {
						nestedlogoMiddleTable.addCell(leftCell);
						nestedlogoMiddleTable.addCell(rightCell);
					}
				}
			}

		} else if (field.getType().equalsIgnoreCase(
				PayAsiaConstants.FIELD_TYPE_DATE)) {
			String base64Str = new String(Base64.decodeBase64(field
					.getDictionaryName().getBytes()));

			try {
				base64Str = new String(URLDecoder.decode(base64Str, "UTF8"));
			} catch (UnsupportedEncodingException uee) {
				LOGGER.error(uee.getMessage(), uee);
				throw new PayAsiaSystemException(uee.getMessage(), uee);
			} catch (IllegalArgumentException ill) {
				LOGGER.error(ill.getMessage(), ill);
			}
			Font baseFont = setBaseFontForField(field);

			PdfPCell leftCell = null;
			PdfPCell rightCell = null;

			if (field.isBold() == null && field.isUnderline() == null) {
				leftCell = new PdfPCell(
						new Phrase(base64Str, getBaseFontBold()));
			} else {
				leftCell = new PdfPCell(new Phrase(base64Str, baseFont));
			}
			leftCell.setPadding(2);
			leftCell.setBorder(0);
			leftCell.setPaddingLeft(5);
			if (payslip != null) {
				String fieldValue = field.getValue();
				if (!isFieldValueBlankOrZero(fieldValue)) {
					if (field.isDefault()) {
						rightCell = new PdfPCell(new Phrase(field.getValue(),
								baseFont));
					} else {
						try {
							rightCell = new PdfPCell(new Phrase(
									field.getValue(), baseFont));
						} catch (NumberFormatException e) {
							rightCell = new PdfPCell(new Phrase(
									field.getValue(), baseFont));
						}
					}
					rightCell.setPadding(2);
					rightCell.setBorder(0);
					if (field.isHideLabel() != null && field.isHideLabel()) {
						rightCell.setColspan(2);
						nestedlogoMiddleTable.addCell(rightCell);
					} else {
						nestedlogoMiddleTable.addCell(leftCell);
						nestedlogoMiddleTable.addCell(rightCell);
					}
				}
			} else {
				rightCell = new PdfPCell(new Phrase(
						PayAsiaConstants.PAYSLIP_FIELD_VALUE, baseFont));
				rightCell.setPadding(2);
				rightCell.setBorder(0);
			}
			if (rightCell != null) {
				if (field.getAlign() != null
						&& "right".equalsIgnoreCase(field.getAlign())) {
					leftCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				} else if (field.getAlign() != null
						&& "center".equalsIgnoreCase(field.getAlign())) {
					leftCell.setHorizontalAlignment(Element.ALIGN_CENTER);
					rightCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				} else {
					leftCell.setHorizontalAlignment(Element.ALIGN_LEFT);
					rightCell.setHorizontalAlignment(Element.ALIGN_LEFT);
				}
			}

		} else if (field.getType().equalsIgnoreCase(
				PayAsiaConstants.FIELD_TYPE_CALCULATORY_FIELD)) {
			String base64Str = new String(Base64.decodeBase64(field
					.getDictionaryName().getBytes()));

			try {
				base64Str = new String(URLDecoder.decode(base64Str, "UTF8"));
			} catch (UnsupportedEncodingException uee) {
				LOGGER.error(uee.getMessage(), uee);
				throw new PayAsiaSystemException(uee.getMessage(), uee);
			} catch (IllegalArgumentException ill) {
				LOGGER.error(ill.getMessage(), ill);
			}
			Font baseFont = setBaseFontForField(field);

			PdfPCell leftCell = null;
			PdfPCell rightCell = null;

			if (field.isBold() == null && field.isUnderline() == null) {
				leftCell = new PdfPCell(
						new Phrase(base64Str, getBaseFontBold()));
			} else {
				leftCell = new PdfPCell(new Phrase(base64Str, baseFont));
			}
			leftCell.setPadding(2);
			leftCell.setBorder(0);
			leftCell.setPaddingLeft(5);
			if (payslip != null) {
				String fieldValue = field.getValue();
				if (!isFieldValueBlankOrZero(fieldValue)) {
					if (field.isDefault()) {
						rightCell = new PdfPCell(new Phrase(field.getValue(),
								baseFont));
					} else {
						try {
							rightCell = new PdfPCell(new Phrase(
									field.getValue(), baseFont));
						} catch (NumberFormatException e) {
							rightCell = new PdfPCell(new Phrase(
									field.getValue(), baseFont));
						}
					}
					rightCell.setPadding(2);
					rightCell.setBorder(0);
					if (field.isHideLabel() != null && field.isHideLabel()) {
						rightCell.setColspan(2);
						nestedlogoMiddleTable.addCell(rightCell);
					} else {
						nestedlogoMiddleTable.addCell(leftCell);
						nestedlogoMiddleTable.addCell(rightCell);
					}
				}
			} else {
				rightCell = new PdfPCell(new Phrase(
						PayAsiaConstants.PAYSLIP_FIELD_VALUE, baseFont));
				rightCell.setPadding(2);
				rightCell.setBorder(0);
			}
			if (rightCell != null) {
				if (field.getAlign() != null
						&& "right".equalsIgnoreCase(field.getAlign())) {
					leftCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				} else if (field.getAlign() != null
						&& "center".equalsIgnoreCase(field.getAlign())) {
					leftCell.setHorizontalAlignment(Element.ALIGN_CENTER);
					rightCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				} else {
					leftCell.setHorizontalAlignment(Element.ALIGN_LEFT);
					rightCell.setHorizontalAlignment(Element.ALIGN_LEFT);
				}
			}

		} else if (field.getType().equalsIgnoreCase(
				PayAsiaConstants.FIELD_TYPE_LABEL)) {
			String base64Str = new String(Base64.decodeBase64(field
					.getDictionaryName().getBytes()));

			try {
				base64Str = new String(URLDecoder.decode(base64Str, "UTF8"));
			} catch (UnsupportedEncodingException uee) {
				LOGGER.error(uee.getMessage(), uee);
				throw new PayAsiaSystemException(uee.getMessage(), uee);
			} catch (IllegalArgumentException ill) {
				LOGGER.error(ill.getMessage(), ill);
			}

			Font baseFont = setBaseFontForField(field);

			PdfPCell labelCell;

			if (StringUtils.isNotBlank(base64Str)) {
				if (field.isBold() == null && field.isUnderline() == null) {
					labelCell = new PdfPCell(new Phrase(base64Str,
							getBaseFontBold()));
				} else {
					labelCell = new PdfPCell(new Phrase(base64Str, baseFont));
				}

			} else {
				if (field.isBold() == null && field.isUnderline() == null) {
					labelCell = new PdfPCell(new Phrase(new String(
							Base64.decodeBase64(field.getLabel().getBytes())),
							getBaseFontBold()));
				} else {
					labelCell = new PdfPCell(new Phrase(new String(
							Base64.decodeBase64(field.getLabel().getBytes())),
							baseFont));
				}

			}

			if (field.getAlign() != null
					&& "right".equalsIgnoreCase(field.getAlign())) {
				labelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			} else if (field.getAlign() != null
					&& "center".equalsIgnoreCase(field.getAlign())) {
				labelCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			} else {
				labelCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			}
			labelCell.setColspan(2);
			labelCell.setPaddingLeft(5);
			labelCell.setPadding(2);
			labelCell.setBorder(0);
			if (field.isHideLabel() != null && field.isHideLabel()) {
				PdfPCell hideLabelCell = new PdfPCell(new Phrase("",
						getBaseFontNormal()));
				nestedlogoMiddleTable.addCell(hideLabelCell);
			} else {
				nestedlogoMiddleTable.addCell(labelCell);
			}
		}

		logoMiddleCell.addElement(nestedlogoMiddleTable);
		return logoMiddleCell;
	}

	/**
	 * Gets the header left column.
	 * 
	 * @param field
	 *            the field
	 * @param payslip
	 *            the payslip
	 * @return the header left column
	 * @throws BadElementException
	 *             the bad element exception
	 * @throws MalformedURLException
	 *             the malformed url exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private PdfPCell getHeaderLeftColumn(Field field, Payslip payslip)
			throws BadElementException, MalformedURLException, IOException {
		PdfPCell leftColumnCell = new PdfPCell();
		leftColumnCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		leftColumnCell.setPadding(0);

		PdfPTable nestedLeftColumnTable = new PdfPTable(new float[] { 1, 1 });
		nestedLeftColumnTable.setWidthPercentage(100f);
		nestedLeftColumnTable.getTotalHeight();

		if (field.getType().equalsIgnoreCase(PayAsiaConstants.FIELD_TYPE_TEXT)) {
			String base64Str = new String(Base64.decodeBase64(field
					.getDictionaryName().getBytes()));

			try {
				base64Str = new String(URLDecoder.decode(base64Str, "UTF8"));
			} catch (UnsupportedEncodingException uee) {
				LOGGER.error(uee.getMessage(), uee);
				throw new PayAsiaSystemException(uee.getMessage(), uee);
			} catch (IllegalArgumentException ill) {
				LOGGER.error(ill.getMessage(), ill);
			}
			Font baseFont = setBaseFontForField(field);

			PdfPCell nestedLeftCell = null;
			PdfPCell nestedRightCell = null;

			if (field.isBold() == null && field.isUnderline() == null) {
				nestedLeftCell = new PdfPCell(new Phrase(base64Str,
						getBaseFontBold()));
			} else {
				nestedLeftCell = new PdfPCell(new Phrase(base64Str, baseFont));
			}
			nestedLeftCell.setPadding(2);
			nestedLeftCell.setBorder(0);
			if (payslip != null) {
				String fieldValue = field.getValue();
				if (!isFieldValueBlankOrZero(fieldValue)) {
					if (field.isDefault()) {
						nestedRightCell = new PdfPCell(new Phrase(
								field.getValue(), baseFont));
					} else {
						try {
							nestedRightCell = new PdfPCell(new Phrase(
									field.getValue(), baseFont));
						} catch (NumberFormatException e) {
							nestedRightCell = new PdfPCell(new Phrase(
									field.getValue(), baseFont));
						}
					}
					nestedRightCell.setPadding(2);
					nestedRightCell.setBorder(0);
					setPayslipColDataAlignment(field, nestedLeftCell,
							nestedRightCell);
					if (field.isHideLabel() != null && field.isHideLabel()) {
						nestedRightCell.setColspan(2);
						nestedLeftColumnTable.addCell(nestedRightCell);
					} else {
						nestedLeftColumnTable.addCell(nestedLeftCell);
						nestedLeftColumnTable.addCell(nestedRightCell);
					}
				}
			} else {
				nestedRightCell = new PdfPCell(new Phrase(
						PayAsiaConstants.PAYSLIP_FIELD_VALUE, baseFont));
				nestedRightCell.setPadding(2);
				nestedRightCell.setBorder(0);
				setPayslipColDataAlignment(field, nestedLeftCell,
						nestedRightCell);
			}

			if (payslip != null) {

			} else {
				if (nestedRightCell != null) {
					if (field.isHideLabel() != null && field.isHideLabel()) {
						nestedRightCell.setColspan(2);
						nestedLeftColumnTable.addCell(nestedRightCell);
					} else {
						nestedLeftColumnTable.addCell(nestedLeftCell);
						nestedLeftColumnTable.addCell(nestedRightCell);
					}
				}
			}

		} else if (field.getType().equalsIgnoreCase(
				PayAsiaConstants.FIELD_TYPE_NUMERIC)) {
			String base64Str = new String(Base64.decodeBase64(field
					.getDictionaryName().getBytes()));

			try {
				base64Str = new String(URLDecoder.decode(base64Str, "UTF8"));
			} catch (UnsupportedEncodingException uee) {
				LOGGER.error(uee.getMessage(), uee);
				throw new PayAsiaSystemException(uee.getMessage(), uee);
			} catch (IllegalArgumentException ill) {
				LOGGER.error(ill.getMessage(), ill);
			}
			Font baseFont = setBaseFontForField(field);

			PdfPCell nestedLeftCell = null;
			PdfPCell nestedRightCell = null;

			if (field.isBold() == null && field.isUnderline() == null) {
				nestedLeftCell = new PdfPCell(new Phrase(base64Str,
						getBaseFontBold()));
			} else {
				nestedLeftCell = new PdfPCell(new Phrase(base64Str, baseFont));
			}
			nestedLeftCell.setPadding(2);
			nestedLeftCell.setBorder(0);
			if (payslip != null) {
				String fieldValue = field.getValue();
				if (!isFieldValueBlankOrZero(fieldValue)) {
					if (field.isDefault()) {
						nestedRightCell = new PdfPCell(new Phrase(
								field.getValue(), baseFont));
					} else {
						try {
							nestedRightCell = new PdfPCell(new Phrase(
									field.getValue(), baseFont));
						} catch (NumberFormatException e) {
							nestedRightCell = new PdfPCell(new Phrase(
									field.getValue(), baseFont));
						}
					}
					if (nestedRightCell != null) {
						nestedRightCell.setPadding(2);
						nestedRightCell.setBorder(0);
						if (field.isHideLabel() != null && field.isHideLabel()) {
							nestedRightCell.setColspan(2);
							nestedLeftColumnTable.addCell(nestedRightCell);
						} else {
							nestedLeftColumnTable.addCell(nestedLeftCell);
							nestedLeftColumnTable.addCell(nestedRightCell);
						}
					}
				}
			} else {
				nestedRightCell = new PdfPCell(new Phrase(
						PayAsiaConstants.PAYSLIP_FIELD_VALUE, baseFont));
				nestedRightCell.setPadding(2);
				nestedRightCell.setBorder(0);
			}
			if (nestedRightCell != null) {
				if (field.getAlign() != null
						&& "right".equalsIgnoreCase(field.getAlign())) {
					nestedLeftCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					nestedRightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				} else if (field.getAlign() != null
						&& "center".equalsIgnoreCase(field.getAlign())) {
					nestedLeftCell.setHorizontalAlignment(Element.ALIGN_CENTER);
					nestedRightCell
							.setHorizontalAlignment(Element.ALIGN_CENTER);
				} else {
					nestedLeftCell.setHorizontalAlignment(Element.ALIGN_LEFT);
					nestedRightCell.setHorizontalAlignment(Element.ALIGN_LEFT);

				}
			}
			if (payslip != null) {

			} else {
				if (nestedRightCell != null) {
					if (field.isHideLabel() != null && field.isHideLabel()) {
						nestedRightCell.setColspan(2);
						nestedLeftColumnTable.addCell(nestedRightCell);
					} else {
						nestedLeftColumnTable.addCell(nestedLeftCell);
						nestedLeftColumnTable.addCell(nestedRightCell);
					}
				}
			}

		} else if (field.getType().equalsIgnoreCase(
				PayAsiaConstants.FIELD_TYPE_DATE)) {
			String base64Str = new String(Base64.decodeBase64(field
					.getDictionaryName().getBytes()));

			try {
				base64Str = new String(URLDecoder.decode(base64Str, "UTF8"));
			} catch (UnsupportedEncodingException uee) {
				LOGGER.error(uee.getMessage(), uee);
				throw new PayAsiaSystemException(uee.getMessage(), uee);
			} catch (IllegalArgumentException ill) {
				LOGGER.error(ill.getMessage(), ill);
			}
			Font baseFont = setBaseFontForField(field);

			PdfPCell nestedLeftCell = null;
			PdfPCell nestedRightCell = null;

			if (field.isBold() == null && field.isUnderline() == null) {
				nestedLeftCell = new PdfPCell(new Phrase(base64Str,
						getBaseFontBold()));
			} else {
				nestedLeftCell = new PdfPCell(new Phrase(base64Str, baseFont));
			}
			nestedLeftCell.setPadding(2);
			nestedLeftCell.setBorder(0);
			if (payslip != null) {
				String fieldValue = field.getValue();
				if (!isFieldValueBlankOrZero(fieldValue)) {
					if (field.isDefault()) {
						nestedRightCell = new PdfPCell(new Phrase(
								field.getValue(), baseFont));
					} else {
						try {
							nestedRightCell = new PdfPCell(new Phrase(
									field.getValue(), baseFont));
						} catch (NumberFormatException e) {
							nestedRightCell = new PdfPCell(new Phrase(
									field.getValue(), baseFont));
						}
					}
					if (nestedRightCell != null) {
						nestedRightCell.setPadding(2);
						nestedRightCell.setBorder(0);
						if (field.isHideLabel() != null && field.isHideLabel()) {
							nestedRightCell.setColspan(2);
							nestedLeftColumnTable.addCell(nestedRightCell);
						} else {
							nestedLeftColumnTable.addCell(nestedLeftCell);
							nestedLeftColumnTable.addCell(nestedRightCell);
						}
					}
				}
			} else {
				nestedRightCell = new PdfPCell(new Phrase(
						PayAsiaConstants.PAYSLIP_FIELD_VALUE, baseFont));
				nestedRightCell.setPadding(2);
				nestedRightCell.setBorder(0);
			}
			if (nestedRightCell != null) {
				if (field.getAlign() != null
						&& "right".equalsIgnoreCase(field.getAlign())) {
					nestedLeftCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					nestedRightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				} else if (field.getAlign() != null
						&& "center".equalsIgnoreCase(field.getAlign())) {
					nestedLeftCell.setHorizontalAlignment(Element.ALIGN_CENTER);
					nestedRightCell
							.setHorizontalAlignment(Element.ALIGN_CENTER);
				} else {
					nestedLeftCell.setHorizontalAlignment(Element.ALIGN_LEFT);
					nestedRightCell.setHorizontalAlignment(Element.ALIGN_LEFT);
				}

				if (field.isHideLabel() != null && field.isHideLabel()) {
					nestedRightCell.setColspan(2);
					nestedLeftColumnTable.addCell(nestedRightCell);
				} else {
					nestedLeftColumnTable.addCell(nestedLeftCell);
					nestedLeftColumnTable.addCell(nestedRightCell);
				}
			}

		} else if (field.getType().equalsIgnoreCase(
				PayAsiaConstants.FIELD_TYPE_CALCULATORY_FIELD)) {
			String base64Str = new String(Base64.decodeBase64(field
					.getDictionaryName().getBytes()));

			try {
				base64Str = new String(URLDecoder.decode(base64Str, "UTF8"));
			} catch (UnsupportedEncodingException uee) {
				LOGGER.error(uee.getMessage(), uee);
				throw new PayAsiaSystemException(uee.getMessage(), uee);
			} catch (IllegalArgumentException ill) {
				LOGGER.error(ill.getMessage(), ill);
			}

			Font baseFont = setBaseFontForField(field);
			PdfPCell nestedLeftCell = null;
			PdfPCell nestedRightCell = null;

			if (field.isBold() == null && field.isUnderline() == null) {
				nestedLeftCell = new PdfPCell(new Phrase(base64Str,
						getBaseFontBold()));
			} else {
				nestedLeftCell = new PdfPCell(new Phrase(base64Str, baseFont));
			}
			nestedLeftCell.setPadding(2);
			nestedLeftCell.setBorder(0);
			if (payslip != null) {
				String fieldValue = field.getValue();
				if (!isFieldValueBlankOrZero(fieldValue)) {
					if (field.isDefault()) {
						nestedRightCell = new PdfPCell(new Phrase(
								field.getValue(), baseFont));
					} else {
						try {
							nestedRightCell = new PdfPCell(new Phrase(
									field.getValue(), baseFont));
						} catch (NumberFormatException e) {
							nestedRightCell = new PdfPCell(new Phrase(
									field.getValue(), baseFont));
						}
					}
					if (nestedRightCell != null) {
						nestedRightCell.setPadding(2);
						nestedRightCell.setBorder(0);
						if (field.isHideLabel() != null && field.isHideLabel()) {
							nestedRightCell.setColspan(2);
							nestedLeftColumnTable.addCell(nestedRightCell);
						} else {
							nestedLeftColumnTable.addCell(nestedLeftCell);
							nestedLeftColumnTable.addCell(nestedRightCell);
						}
					}
				}
			} else {
				nestedRightCell = new PdfPCell(new Phrase(
						PayAsiaConstants.PAYSLIP_FIELD_VALUE, baseFont));
				nestedRightCell.setPadding(2);
				nestedRightCell.setBorder(0);
			}
			if (nestedRightCell != null) {
				if (field.getAlign() != null
						&& "right".equalsIgnoreCase(field.getAlign())) {
					nestedLeftCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					nestedRightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				} else if (field.getAlign() != null
						&& "center".equalsIgnoreCase(field.getAlign())) {
					nestedLeftCell.setHorizontalAlignment(Element.ALIGN_CENTER);
					nestedRightCell
							.setHorizontalAlignment(Element.ALIGN_CENTER);
				} else {
					nestedLeftCell.setHorizontalAlignment(Element.ALIGN_LEFT);
					nestedRightCell.setHorizontalAlignment(Element.ALIGN_LEFT);

				}
				if (field.isHideLabel() != null && field.isHideLabel()) {
					nestedRightCell.setColspan(2);
					nestedLeftColumnTable.addCell(nestedRightCell);
				} else {
					nestedLeftColumnTable.addCell(nestedLeftCell);
					nestedLeftColumnTable.addCell(nestedRightCell);
				}
			}

		} else if (field.getType().equalsIgnoreCase(
				PayAsiaConstants.FIELD_TYPE_LABEL)) {
			String base64Str = new String(Base64.decodeBase64(field
					.getDictionaryName().getBytes()));

			try {
				base64Str = new String(URLDecoder.decode(base64Str, "UTF8"));
			} catch (UnsupportedEncodingException uee) {
				LOGGER.error(uee.getMessage(), uee);
				throw new PayAsiaSystemException(uee.getMessage(), uee);
			} catch (IllegalArgumentException ill) {
				LOGGER.error(ill.getMessage(), ill);
			}
			Font baseFont = setBaseFontForField(field);

			PdfPCell labelCell;

			if (StringUtils.isNotBlank(base64Str)) {
				if (field.isBold() == null && field.isUnderline() == null) {
					labelCell = new PdfPCell(new Phrase(base64Str,
							getBaseFontBold()));
				} else {
					labelCell = new PdfPCell(new Phrase(base64Str, baseFont));
				}
			} else {
				if (field.isBold() == null && field.isUnderline() == null) {
					labelCell = new PdfPCell(new Phrase(new String(
							Base64.decodeBase64(field.getLabel().getBytes())),
							getBaseFontBold()));
				} else {
					labelCell = new PdfPCell(new Phrase(new String(
							Base64.decodeBase64(field.getLabel().getBytes())),
							baseFont));
				}

			}

			if (field.getAlign() != null
					&& "right".equalsIgnoreCase(field.getAlign())) {
				labelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			} else if (field.getAlign() != null
					&& "center".equalsIgnoreCase(field.getAlign())) {
				labelCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			} else {
				labelCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			}
			labelCell.setColspan(2);
			labelCell.setPadding(2);
			labelCell.setBorder(0);
			if (field.isHideLabel() != null && field.isHideLabel()) {
				PdfPCell hideLabelCell = new PdfPCell(new Phrase("",
						getBaseFontNormal()));
				nestedLeftColumnTable.addCell(hideLabelCell);
			} else {
				nestedLeftColumnTable.addCell(labelCell);
			}
		}

		leftColumnCell.addElement(nestedLeftColumnTable);
		return leftColumnCell;

	}

	/**
	 * Gets the header right column.
	 * 
	 * @param field
	 *            the field
	 * @param payslip
	 *            the payslip
	 * @return the header right column
	 * @throws BadElementException
	 *             the bad element exception
	 * @throws MalformedURLException
	 *             the malformed url exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private PdfPCell getHeaderRightColumn(Field field, Payslip payslip)
			throws BadElementException, MalformedURLException, IOException {

		PdfPCell rightColumnCell = new PdfPCell();
		rightColumnCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		rightColumnCell.setPadding(0);

		PdfPTable nestedRightColumnTable = new PdfPTable(new float[] { 1, 1 });
		nestedRightColumnTable.setWidthPercentage(100f);
		nestedRightColumnTable.getTotalHeight();

		if (field.getType().equalsIgnoreCase(PayAsiaConstants.FIELD_TYPE_TEXT)) {
			String base64Str = new String(Base64.decodeBase64(field
					.getDictionaryName().getBytes()));

			try {
				base64Str = new String(URLDecoder.decode(base64Str, "UTF8"));
			} catch (UnsupportedEncodingException uee) {
				LOGGER.error(uee.getMessage(), uee);
				throw new PayAsiaSystemException(uee.getMessage(), uee);
			} catch (IllegalArgumentException ill) {
				LOGGER.error(ill.getMessage(), ill);
			}
			Font baseFont = setBaseFontForField(field);

			PdfPCell nestedLeftCell = null;
			PdfPCell nestedRightCell = null;

			if (field.isBold() == null && field.isUnderline() == null) {
				nestedLeftCell = new PdfPCell(new Phrase(base64Str,
						getBaseFontBold()));
			} else {
				nestedLeftCell = new PdfPCell(new Phrase(base64Str, baseFont));
			}
			nestedLeftCell.setPadding(2);
			nestedLeftCell.setPaddingLeft(5);
			nestedLeftCell.setBorder(0);
			if (payslip != null) {
				String fieldValue = field.getValue();
				if (!isFieldValueBlankOrZero(fieldValue)) {
					if (field.isDefault()) {
						nestedRightCell = new PdfPCell(new Phrase(
								field.getValue(), baseFont));
					} else {
						try {
							nestedRightCell = new PdfPCell(new Phrase(
									field.getValue(), baseFont));
						} catch (NumberFormatException e) {
							nestedRightCell = new PdfPCell(new Phrase(
									field.getValue(), baseFont));
						}
					}
					nestedRightCell.setPadding(2);
					nestedRightCell.setBorder(0);
					setPayslipColDataAlignment(field, nestedLeftCell,
							nestedRightCell);
					if (field.isHideLabel() != null && field.isHideLabel()) {
						nestedRightCell.setColspan(2);
						nestedRightColumnTable.addCell(nestedRightCell);
					} else {
						nestedRightColumnTable.addCell(nestedLeftCell);
						nestedRightColumnTable.addCell(nestedRightCell);
					}
				}
			} else {
				nestedRightCell = new PdfPCell(new Phrase(
						PayAsiaConstants.PAYSLIP_FIELD_VALUE, baseFont));
				nestedRightCell.setPadding(2);
				nestedRightCell.setBorder(0);
				setPayslipColDataAlignment(field, nestedLeftCell,
						nestedRightCell);
			}

			if (payslip != null) {

			} else {
				if (field.isHideLabel() != null && field.isHideLabel()) {
					nestedRightCell.setColspan(2);
					nestedRightColumnTable.addCell(nestedRightCell);
				} else {
					nestedRightColumnTable.addCell(nestedLeftCell);
					nestedRightColumnTable.addCell(nestedRightCell);
				}
			}

		} else if (field.getType().equalsIgnoreCase(
				PayAsiaConstants.FIELD_TYPE_NUMERIC)) {
			String base64Str = new String(Base64.decodeBase64(field
					.getDictionaryName().getBytes()));

			try {
				base64Str = new String(URLDecoder.decode(base64Str, "UTF8"));
			} catch (UnsupportedEncodingException uee) {
				LOGGER.error(uee.getMessage(), uee);
				throw new PayAsiaSystemException(uee.getMessage(), uee);
			} catch (IllegalArgumentException ill) {
				LOGGER.error(ill.getMessage(), ill);
			}
			Font baseFont = setBaseFontForField(field);

			PdfPCell nestedLeftCell = null;
			PdfPCell nestedRightCell = null;

			if (field.isBold() == null && field.isUnderline() == null) {
				nestedLeftCell = new PdfPCell(new Phrase(base64Str,
						getBaseFontBold()));
			} else {
				nestedLeftCell = new PdfPCell(new Phrase(base64Str, baseFont));
			}
			nestedLeftCell.setPadding(2);
			nestedLeftCell.setBorder(0);
			nestedLeftCell.setPaddingLeft(5);

			if (payslip != null) {
				String fieldValue = field.getValue();
				if (!isFieldValueBlankOrZero(fieldValue)) {
					if (field.isDefault()) {
						nestedRightCell = new PdfPCell(new Phrase(
								field.getValue(), baseFont));
					} else {
						try {
							nestedRightCell = new PdfPCell(new Phrase(
									field.getValue(), baseFont));
						} catch (NumberFormatException e) {
							nestedRightCell = new PdfPCell(new Phrase(
									field.getValue(), baseFont));
						}
					}
					nestedRightCell.setPadding(2);
					nestedRightCell.setBorder(0);
					if (field.isHideLabel() != null && field.isHideLabel()) {
						nestedRightCell.setColspan(2);
						nestedRightColumnTable.addCell(nestedRightCell);
					} else {
						nestedRightColumnTable.addCell(nestedLeftCell);
						nestedRightColumnTable.addCell(nestedRightCell);
					}
				}
			} else {
				nestedRightCell = new PdfPCell(new Phrase(
						PayAsiaConstants.PAYSLIP_FIELD_VALUE, baseFont));
				nestedRightCell.setPadding(2);
				nestedRightCell.setBorder(0);
			}
			if (nestedRightCell != null) {
				if (field.getAlign() != null
						&& "right".equalsIgnoreCase(field.getAlign())) {
					nestedLeftCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					nestedRightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

				} else if (field.getAlign() != null
						&& "center".equalsIgnoreCase(field.getAlign())) {
					nestedLeftCell.setHorizontalAlignment(Element.ALIGN_CENTER);
					nestedRightCell
							.setHorizontalAlignment(Element.ALIGN_CENTER);
				} else {
					nestedLeftCell.setHorizontalAlignment(Element.ALIGN_LEFT);
					nestedRightCell.setHorizontalAlignment(Element.ALIGN_LEFT);
				}
				if (payslip != null) {
				} else {
					if (field.isHideLabel() != null && field.isHideLabel()) {
						nestedRightCell.setColspan(2);
						nestedRightColumnTable.addCell(nestedRightCell);
					} else {
						nestedRightColumnTable.addCell(nestedLeftCell);
						nestedRightColumnTable.addCell(nestedRightCell);
					}
				}
			}

		} else if (field.getType().equalsIgnoreCase(
				PayAsiaConstants.FIELD_TYPE_DATE)) {
			String base64Str = new String(Base64.decodeBase64(field
					.getDictionaryName().getBytes()));

			try {
				base64Str = new String(URLDecoder.decode(base64Str, "UTF8"));
			} catch (UnsupportedEncodingException uee) {
				LOGGER.error(uee.getMessage(), uee);
				throw new PayAsiaSystemException(uee.getMessage(), uee);
			} catch (IllegalArgumentException ill) {
				LOGGER.error(ill.getMessage(), ill);
			}
			Font baseFont = setBaseFontForField(field);

			PdfPCell nestedLeftCell = null;
			PdfPCell nestedRightCell = null;

			if (field.isBold() == null && field.isUnderline() == null) {
				nestedLeftCell = new PdfPCell(new Phrase(base64Str,
						getBaseFontBold()));
			} else {
				nestedLeftCell = new PdfPCell(new Phrase(base64Str, baseFont));
			}

			nestedLeftCell.setPadding(2);
			nestedLeftCell.setBorder(0);
			nestedLeftCell.setPaddingLeft(5);
			if (payslip != null) {
				String fieldValue = field.getValue();
				if (!isFieldValueBlankOrZero(fieldValue)) {
					if (field.isDefault()) {
						nestedRightCell = new PdfPCell(new Phrase(
								field.getValue(), baseFont));
					} else {
						try {
							nestedRightCell = new PdfPCell(new Phrase(
									field.getValue(), baseFont));
						} catch (NumberFormatException e) {
							nestedRightCell = new PdfPCell(new Phrase(
									field.getValue(), baseFont));
						}
					}
					nestedRightCell.setPadding(2);
					nestedRightCell.setBorder(0);
					if (field.isHideLabel() != null && field.isHideLabel()) {
						nestedRightCell.setColspan(2);
						nestedRightColumnTable.addCell(nestedRightCell);
					} else {
						nestedRightColumnTable.addCell(nestedLeftCell);
						nestedRightColumnTable.addCell(nestedRightCell);
					}
				}
			} else {
				nestedRightCell = new PdfPCell(new Phrase(
						PayAsiaConstants.PAYSLIP_FIELD_VALUE, baseFont));
				nestedRightCell.setPadding(2);
				nestedRightCell.setBorder(0);
			}
			if (nestedRightCell != null) {
				if (field.getAlign() != null
						&& "right".equalsIgnoreCase(field.getAlign())) {
					nestedLeftCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					nestedRightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				} else if (field.getAlign() != null
						&& "center".equalsIgnoreCase(field.getAlign())) {
					nestedLeftCell.setHorizontalAlignment(Element.ALIGN_CENTER);
					nestedRightCell
							.setHorizontalAlignment(Element.ALIGN_CENTER);
				} else {
					nestedLeftCell.setHorizontalAlignment(Element.ALIGN_LEFT);
					nestedRightCell.setHorizontalAlignment(Element.ALIGN_LEFT);

				}

				if (field.isHideLabel() != null && field.isHideLabel()) {
					nestedRightCell.setColspan(2);
					nestedRightColumnTable.addCell(nestedRightCell);
				} else {
					nestedRightColumnTable.addCell(nestedLeftCell);
					nestedRightColumnTable.addCell(nestedRightCell);
				}
			}
		} else if (field.getType().equalsIgnoreCase(
				PayAsiaConstants.FIELD_TYPE_CALCULATORY_FIELD)) {
			String base64Str = new String(Base64.decodeBase64(field
					.getDictionaryName().getBytes()));

			try {
				base64Str = new String(URLDecoder.decode(base64Str, "UTF8"));
			} catch (UnsupportedEncodingException uee) {
				LOGGER.error(uee.getMessage(), uee);
				throw new PayAsiaSystemException(uee.getMessage(), uee);
			} catch (IllegalArgumentException ill) {
				LOGGER.error(ill.getMessage(), ill);
			}
			Font baseFont = setBaseFontForField(field);

			PdfPCell nestedLeftCell = null;
			PdfPCell nestedRightCell = null;

			if (field.isBold() == null && field.isUnderline() == null) {
				nestedLeftCell = new PdfPCell(new Phrase(base64Str,
						getBaseFontBold()));
			} else {
				nestedLeftCell = new PdfPCell(new Phrase(base64Str, baseFont));
			}
			nestedLeftCell.setPadding(2);
			nestedLeftCell.setBorder(0);
			nestedLeftCell.setPaddingLeft(5);
			if (payslip != null) {
				String fieldValue = field.getValue();
				if (!isFieldValueBlankOrZero(fieldValue)) {
					if (field.isDefault()) {
						nestedRightCell = new PdfPCell(new Phrase(
								field.getValue(), baseFont));
					} else {
						try {
							nestedRightCell = new PdfPCell(new Phrase(
									field.getValue(), baseFont));
						} catch (NumberFormatException e) {
							nestedRightCell = new PdfPCell(new Phrase(
									field.getValue(), baseFont));
						}
					}
					nestedRightCell.setPadding(2);
					nestedRightCell.setBorder(0);
					if (field.isHideLabel() != null && field.isHideLabel()) {
						nestedRightCell.setColspan(2);
						nestedRightColumnTable.addCell(nestedRightCell);
					} else {
						nestedRightColumnTable.addCell(nestedLeftCell);
						nestedRightColumnTable.addCell(nestedRightCell);
					}
				}
			} else {
				nestedRightCell = new PdfPCell(new Phrase(
						PayAsiaConstants.PAYSLIP_FIELD_VALUE, baseFont));
				nestedRightCell.setPadding(2);
				nestedRightCell.setBorder(0);
			}
			if (nestedRightCell != null) {
				if (field.getAlign() != null
						&& "right".equalsIgnoreCase(field.getAlign())) {
					nestedLeftCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					nestedRightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				} else if (field.getAlign() != null
						&& "center".equalsIgnoreCase(field.getAlign())) {
					nestedLeftCell.setHorizontalAlignment(Element.ALIGN_CENTER);
					nestedRightCell
							.setHorizontalAlignment(Element.ALIGN_CENTER);
				} else {
					nestedLeftCell.setHorizontalAlignment(Element.ALIGN_LEFT);
					nestedRightCell.setHorizontalAlignment(Element.ALIGN_LEFT);

				}
			}

		} else if (field.getType().equalsIgnoreCase(
				PayAsiaConstants.FIELD_TYPE_LABEL)) {
			String base64Str = new String(Base64.decodeBase64(field
					.getDictionaryName().getBytes()));

			try {
				base64Str = new String(URLDecoder.decode(base64Str, "UTF8"));
			} catch (UnsupportedEncodingException uee) {
				LOGGER.error(uee.getMessage(), uee);
				throw new PayAsiaSystemException(uee.getMessage(), uee);
			} catch (IllegalArgumentException ill) {
				LOGGER.error(ill.getMessage(), ill);
			}
			Font baseFont = setBaseFontForField(field);

			PdfPCell labelCell;

			if (StringUtils.isNotBlank(base64Str)) {
				if (field.isBold() == null && field.isUnderline() == null) {
					labelCell = new PdfPCell(new Phrase(base64Str,
							getBaseFontBold()));
				} else {
					labelCell = new PdfPCell(new Phrase(base64Str, baseFont));
				}
			} else {
				if (field.isBold() == null && field.isUnderline() == null) {
					labelCell = new PdfPCell(new Phrase(new String(
							Base64.decodeBase64(field.getLabel().getBytes())),
							getBaseFontBold()));
				} else {
					labelCell = new PdfPCell(new Phrase(new String(
							Base64.decodeBase64(field.getLabel().getBytes())),
							baseFont));
				}

			}

			if (field.getAlign() != null
					&& "right".equalsIgnoreCase(field.getAlign())) {
				labelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			} else if (field.getAlign() != null
					&& "center".equalsIgnoreCase(field.getAlign())) {
				labelCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			} else {
				labelCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			}
			labelCell.setColspan(2);
			labelCell.setPaddingLeft(5);
			labelCell.setPadding(2);
			labelCell.setBorder(0);
			if (field.isHideLabel() != null && field.isHideLabel()) {
				PdfPCell hideLabelCell = new PdfPCell(new Phrase("",
						getBaseFontNormal()));
				nestedRightColumnTable.addCell(hideLabelCell);
			} else {
				nestedRightColumnTable.addCell(labelCell);
			}
		}

		rightColumnCell.addElement(nestedRightColumnTable);
		return rightColumnCell;

	}

	/**
	 * Gets the page header height.
	 * 
	 * @param writer
	 *            the writer
	 * @param document
	 *            the document
	 * @param pageNumber
	 *            the page number
	 * @return the page header height
	 * @throws BadElementException
	 *             the bad element exception
	 * @throws MalformedURLException
	 *             the malformed url exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws JAXBException
	 *             the jAXB exception
	 * @throws SAXException
	 *             the sAX exception
	 */
	@Override
	public float getPageHeaderHeight(PdfWriter writer, Document document,
			int pageNumber, List<DynamicFormRecord> dynamicFormRecordList)
			throws BadElementException, MalformedURLException, IOException,
			JAXBException, SAXException {
		if (pageNumber <= 0) {
			pageNumber = 1;
		}

		boolean enablePageNumbers = PDFThreadLocal.pageNumbers.get();
		PDFThreadLocal.pageNumbers.set(false);

		PdfPTable headerTable = getLogoSection(writer, document, payslip,
				pageNumber, dynamicFormRecordList);
		float tableHeaderHeight = headerTable.calculateHeights();

		PDFThreadLocal.pageNumbers.set(enablePageNumbers);

		return tableHeaderHeight;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.itextpdf.text.pdf.PdfPageEventHelper#onCloseDocument(com.itextpdf
	 * .text.pdf.PdfWriter, com.itextpdf.text.Document)
	 */
	@Override
	public void onCloseDocument(PdfWriter writer, Document document) {

		String numberOfPages = String.valueOf(writer.getPageNumber() - 1);
		ColumnText.showTextAligned(total, Element.ALIGN_LEFT, new Phrase(
				numberOfPages, getBaseFontBold()), 0, 0, 0);
	}

	/**
	 * Gets the Footer Section PdfTable.
	 * 
	 * @param document
	 *            the document
	 * @param payslip
	 *            the payslip
	 * @param currentPageNumber
	 *            the current page number
	 * @return the Footer Section Pdf table
	 * @throws BadElementException
	 *             the bad element exception
	 * @throws MalformedURLException
	 *             the malformed url exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws JAXBException
	 *             the jAXB exception
	 * @throws SAXException
	 *             the sAX exception
	 */
	@Override
	public PdfPTable getFooterSection(Document document, Payslip payslip,
			int currentPageNumber, List<DynamicFormRecord> dynamicFormRecordList)
			throws BadElementException, MalformedURLException, IOException,
			JAXBException, SAXException {
		Unmarshaller unmarshaller = null;
		unmarshaller = XMLUtil.getDocumentUnmarshaller();

		String footerXML = getXML(companyId, PayAsiaConstants.FOOTER_SECTION,
				payslip);
		final StringReader xmlReader = new StringReader(footerXML);
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
		} catch (JAXBException e) {
			LOGGER.error(e.getMessage(), e);
			throw new PayAsiaSystemException(e.getMessage(), e);
		}

		FieldOrder fieldOrder = tab.getFieldOrder();
		List<TabColumn> tabColumns = fieldOrder.getTabColumn();

		int columnNumber = 1;
		boolean status = false;

		List<Field> footerLeftColumnList = new ArrayList<>();
		List<Field> footerRightColumnList = new ArrayList<>();

		for (TabColumn tabColumn : tabColumns) {

			List<String> fieldReferences = tabColumn.getFieldReference();
			for (String fieldReference : fieldReferences) {
				status = true;
				List<Field> fields = tab.getField();

				for (Field field : fields) {
					if (fieldReference.equalsIgnoreCase(field.getName())) {

						if (columnNumber == 1) {
							if (payslip != null) {
								String fieldValue = field.getValue();

								if (field.getType().equalsIgnoreCase(
										PayAsiaConstants.FIELD_TYPE_LABEL)) {
									footerLeftColumnList.add(field);
								} else {
									if (!isFieldValueBlankOrZero(fieldValue)) {
										footerLeftColumnList.add(field);
									}
								}

							} else {
								footerLeftColumnList.add(field);
							}

						}
						if (columnNumber == 2) {
							if (payslip != null) {
								String fieldValue = field.getValue();
								if (field.getType().equalsIgnoreCase(
										PayAsiaConstants.FIELD_TYPE_LABEL)) {
									footerRightColumnList.add(field);
								} else {
									if (!isFieldValueBlankOrZero(fieldValue)) {
										footerRightColumnList.add(field);
									}
								}

							} else {
								footerRightColumnList.add(field);
							}

						}

					}

				}

			}
			columnNumber++;
		}

		int sizeMap1 = footerLeftColumnList.size();
		int sizeMap2 = footerRightColumnList.size();
		int maxRowSize = 0;
		if (sizeMap1 > sizeMap2) {
			maxRowSize = sizeMap1;
			int difference = sizeMap1 - sizeMap2;
			for (int count = 0; count < difference; count++) {
				footerRightColumnList.add(null);
			}
		}

		if (sizeMap1 < sizeMap2) {
			maxRowSize = sizeMap2;
			int difference = sizeMap2 - sizeMap1;
			for (int count = 0; count < difference; count++) {
				footerLeftColumnList.add(null);
			}
		}

		if (maxRowSize == 0) {
			maxRowSize = sizeMap1;
		}

		List<PayslipRowDTO> payslipRowDTOList = new ArrayList<>();
		if (maxRowSize != 0) {
			for (int count = 0; count < maxRowSize; count++) {
				PayslipRowDTO payslipRowDTO = new PayslipRowDTO();

				Field leftFieldTemp = footerLeftColumnList.get(count);
				Field rightFieldTemp = footerRightColumnList.get(count);

				if (leftFieldTemp != null) {
					if (leftFieldTemp.isColspan() != null) {
						if (leftFieldTemp.isColspan()) {
							payslipRowDTO.setColumnField1(leftFieldTemp);
							payslipRowDTO.setColumnField2(null);
						} else {
							payslipRowDTO.setColumnField1(leftFieldTemp);
							payslipRowDTO.setColumnField2(rightFieldTemp);
						}
					} else {
						payslipRowDTO.setColumnField1(leftFieldTemp);
						payslipRowDTO.setColumnField2(rightFieldTemp);
					}

				} else {
					payslipRowDTO.setColumnField1(leftFieldTemp);
					payslipRowDTO.setColumnField2(rightFieldTemp);
				}
				payslipRowDTOList.add(payslipRowDTO);
			}
		}

		PdfPTable mainSectionTable = new PdfPTable(new float[] { 1 });
		float tableWidth = document.right() - document.left() - 3
				* PayAsiaPDFConstants.X_PADDING;
		mainSectionTable.setTotalWidth(tableWidth);
		mainSectionTable.setSpacingBefore(20f);
		mainSectionTable.setHorizontalAlignment(Element.ALIGN_CENTER);

		PdfPCell mainCell = new PdfPCell();
		mainCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		mainCell.setPadding(0);
		mainCell.setPaddingTop(5f);
		mainCell.setBorder(0);
		PDFUtils.setDefaultCellLayout(mainCell);

		status = true;
		if (status) {
			if (footerXML != "footerSection") {
				mainCell.enableBorderSide(Rectangle.TOP);
			}
		}

		PdfPTable nestedMainSectionTable = getFooterSectionRowsTable(
				payslipRowDTOList, payslip);
		nestedMainSectionTable.setWidthPercentage(100f);

		if (PDFThreadLocal.pageNumbers.get()) {
			PdfPCell footerPageNumCell = new PdfPCell(new Phrase(""));
			footerPageNumCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			footerPageNumCell.setColspan(2);
			footerPageNumCell.setPadding(0f);
			footerPageNumCell.setBorder(0);
			footerPageNumCell.setBorderWidthLeft(0);
			footerPageNumCell.setBorderWidthRight(0);

			PdfPTable nestedFooterPageNumTable = new PdfPTable(
					new float[] { 1 });
			nestedFooterPageNumTable.setWidthPercentage(100f);
			nestedFooterPageNumTable.getTotalHeight();

			PdfPCell nestedFooterPageNumCell = new PdfPCell(
					getPageNumberingPhrase(currentPageNumber));
			nestedFooterPageNumCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			nestedFooterPageNumCell.setBorder(0);
			nestedFooterPageNumCell.setPadding(0f);
			nestedFooterPageNumTable.addCell(nestedFooterPageNumCell);
			footerPageNumCell.addElement(nestedFooterPageNumTable);

			nestedMainSectionTable.addCell(footerPageNumCell);
		}

		mainCell.addElement(nestedMainSectionTable);
		mainSectionTable.addCell(mainCell);
		return mainSectionTable;

	}

	private PdfPTable getFooterSectionRowsTable(
			List<PayslipRowDTO> payslipRowDTOList, Payslip payslip)
			throws BadElementException, MalformedURLException, IOException {

		PdfPTable otherSectionRowsTable = new PdfPTable(new float[] { 1, 1 });
		otherSectionRowsTable.getTotalHeight();

		otherSectionRowsTable.setSpacingAfter(20f);

		for (PayslipRowDTO payslipRowDTO : payslipRowDTOList) {

			if (payslipRowDTO.getColumnField1() != null) {
				if (payslipRowDTO.getColumnField1().isColspan() != null) {
					if (!payslipRowDTO.getColumnField1().isColspan()) {
						PdfPCell leftCell = getLeftFooterColumn(payslip,
								payslipRowDTO.getColumnField1());
						leftCell.setBorder(0);
						PdfPCell rightCell = null;
						if (payslipRowDTO.getColumnField2() != null) {
							rightCell = getFooterRightColumn(payslip,
									payslipRowDTO.getColumnField2());
						} else {
							rightCell = getEmptyCell();
						}
						rightCell.setBorder(0);
						otherSectionRowsTable.addCell(leftCell);
						otherSectionRowsTable.addCell(rightCell);
					} else {
						PdfPCell colSpanCell = getColSpanTwoCell(
								payslipRowDTO.getColumnField1(), payslip);
						colSpanCell.setColspan(2);
						colSpanCell.setHorizontalAlignment(Element.ALIGN_LEFT);
						colSpanCell.setPadding(0);
						colSpanCell.setBorder(0);
						otherSectionRowsTable.addCell(colSpanCell);
					}
				} else {
					PdfPCell leftCell = getLeftFooterColumn(payslip,
							payslipRowDTO.getColumnField1());
					leftCell.setBorder(0);
					PdfPCell rightCell = null;
					if (payslipRowDTO.getColumnField2() != null) {
						rightCell = getFooterRightColumn(payslip,
								payslipRowDTO.getColumnField2());
					} else {
						rightCell = getEmptyCell();
					}
					rightCell.setBorder(0);
					otherSectionRowsTable.addCell(leftCell);
					otherSectionRowsTable.addCell(rightCell);
				}

			} else {
				PdfPCell leftCell = getEmptyCell();
				leftCell.setPadding(0);
				PdfPCell rightCell = null;
				if (payslipRowDTO.getColumnField2() != null) {
					rightCell = getFooterRightColumn(payslip,
							payslipRowDTO.getColumnField2());
					rightCell.setBorder(0);
				} else {
					rightCell = getEmptyCell();
				}
				leftCell.setBorder(0);
				otherSectionRowsTable.addCell(leftCell);
				otherSectionRowsTable.addCell(rightCell);
			}

		}

		return otherSectionRowsTable;
	}

	/**
	 * Gets the left footer column.
	 * 
	 * @param payslip
	 *            the payslip
	 * @param field
	 *            the field
	 * @return the left footer column
	 * @throws BadElementException
	 *             the bad element exception
	 * @throws MalformedURLException
	 *             the malformed url exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private PdfPCell getLeftFooterColumn(Payslip payslip, Field field)
			throws BadElementException, MalformedURLException, IOException {
		//

		PdfPCell leftColumnCell = new PdfPCell();
		leftColumnCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		leftColumnCell.setPadding(0);

		PdfPTable nestedLeftColumnTable = new PdfPTable(new float[] { 1, 1 });
		nestedLeftColumnTable.setWidthPercentage(100f);
		nestedLeftColumnTable.getTotalHeight();

		if (field.getType().equalsIgnoreCase(PayAsiaConstants.FIELD_TYPE_TEXT)) {
			String base64Str = new String(Base64.decodeBase64(field
					.getDictionaryName().getBytes()));

			try {
				base64Str = new String(URLDecoder.decode(base64Str, "UTF8"));
			} catch (UnsupportedEncodingException uee) {
				LOGGER.error(uee.getMessage(), uee);
				throw new PayAsiaSystemException(uee.getMessage(), uee);
			} catch (IllegalArgumentException ill) {
				LOGGER.error(ill.getMessage(), ill);
			}
			Font baseFont = setBaseFontForField(field);

			PdfPCell nestedLeftCell = null;
			PdfPCell nestedRightCell = null;

			if (field.isBold() == null && field.isUnderline() == null) {
				nestedLeftCell = new PdfPCell(new Phrase(base64Str,
						getBaseFontBold()));
			} else {
				nestedLeftCell = new PdfPCell(new Phrase(base64Str, baseFont));
			}
			nestedLeftCell.setPadding(2);
			nestedLeftCell.setBorder(0);
			if (payslip != null) {
				String fieldValue = field.getValue();
				if (!isFieldValueBlankOrZero(fieldValue)) {
					if (field.isDefault()) {
						nestedRightCell = new PdfPCell(new Phrase(
								field.getValue(), baseFont));
					} else {
						try {
							nestedRightCell = new PdfPCell(new Phrase(
									field.getValue(), baseFont));
						} catch (NumberFormatException e) {
							nestedRightCell = new PdfPCell(new Phrase(
									field.getValue(), baseFont));
						}
					}
					nestedRightCell.setPadding(2);
					nestedRightCell.setBorder(0);
					setPayslipColDataAlignment(field, nestedLeftCell,
							nestedRightCell);
					if (field.isHideLabel() != null && field.isHideLabel()) {
						nestedRightCell.setColspan(2);
						nestedLeftColumnTable.addCell(nestedRightCell);
					} else {
						nestedLeftColumnTable.addCell(nestedLeftCell);
						nestedLeftColumnTable.addCell(nestedRightCell);
					}
				}
			} else {
				nestedRightCell = new PdfPCell(new Phrase(
						PayAsiaConstants.PAYSLIP_FIELD_VALUE, baseFont));
				nestedRightCell.setPadding(2);
				setPayslipColDataAlignment(field, nestedLeftCell,
						nestedRightCell);
				nestedRightCell.setBorder(0);
			}

			if (payslip != null) {
			} else {
				if (field.isHideLabel() != null && field.isHideLabel()) {
					nestedRightCell.setColspan(2);
					nestedLeftColumnTable.addCell(nestedRightCell);
				} else {
					nestedLeftColumnTable.addCell(nestedLeftCell);
					nestedLeftColumnTable.addCell(nestedRightCell);
				}
			}

		} else if (field.getType().equalsIgnoreCase(
				PayAsiaConstants.FIELD_TYPE_NUMERIC)) {
			String base64Str = new String(Base64.decodeBase64(field
					.getDictionaryName().getBytes()));

			try {
				base64Str = new String(URLDecoder.decode(base64Str, "UTF8"));
			} catch (UnsupportedEncodingException uee) {
				LOGGER.error(uee.getMessage(), uee);
				throw new PayAsiaSystemException(uee.getMessage(), uee);
			} catch (IllegalArgumentException ill) {
				LOGGER.error(ill.getMessage(), ill);
			}
			Font baseFont = setBaseFontForField(field);

			PdfPCell nestedLeftCell = null;
			PdfPCell nestedRightCell = null;

			if (field.isBold() == null && field.isUnderline() == null) {
				nestedLeftCell = new PdfPCell(new Phrase(base64Str,
						getBaseFontBold()));
			} else {
				nestedLeftCell = new PdfPCell(new Phrase(base64Str, baseFont));
			}
			nestedLeftCell.setPadding(2);
			nestedLeftCell.setBorder(0);

			if (payslip != null) {
				String fieldValue = field.getValue();
				if (!isFieldValueBlankOrZero(fieldValue)) {
					if (field.isDefault()) {
						nestedRightCell = new PdfPCell(new Phrase(
								field.getValue(), baseFont));
					} else {
						try {
							nestedRightCell = new PdfPCell(new Phrase(
									field.getValue(), baseFont));
						} catch (NumberFormatException e) {
							nestedRightCell = new PdfPCell(new Phrase(
									field.getValue(), baseFont));
						}
					}
					nestedRightCell.setPadding(2);
					nestedRightCell.setBorder(0);
					if (field.isHideLabel() != null && field.isHideLabel()) {
						nestedRightCell.setColspan(2);
						nestedLeftColumnTable.addCell(nestedRightCell);
					} else {
						nestedLeftColumnTable.addCell(nestedLeftCell);
						nestedLeftColumnTable.addCell(nestedRightCell);
					}
				}
			} else {
				nestedRightCell = new PdfPCell(new Phrase(
						PayAsiaConstants.PAYSLIP_FIELD_VALUE, baseFont));
				nestedRightCell.setPadding(2);
				nestedRightCell.setBorder(0);
			}
			if (nestedRightCell != null) {
				if (field.getAlign() != null
						&& "right".equalsIgnoreCase(field.getAlign())) {
					nestedLeftCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					nestedRightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				} else if (field.getAlign() != null
						&& "center".equalsIgnoreCase(field.getAlign())) {
					nestedLeftCell.setHorizontalAlignment(Element.ALIGN_CENTER);
					nestedRightCell
							.setHorizontalAlignment(Element.ALIGN_CENTER);
				} else {
					nestedLeftCell.setHorizontalAlignment(Element.ALIGN_LEFT);
					nestedRightCell.setHorizontalAlignment(Element.ALIGN_LEFT);
				}
				if (payslip != null) {
				} else {
					if (field.isHideLabel() != null && field.isHideLabel()) {
						nestedRightCell.setColspan(2);
						nestedLeftColumnTable.addCell(nestedRightCell);
					} else {
						nestedLeftColumnTable.addCell(nestedLeftCell);
						nestedLeftColumnTable.addCell(nestedRightCell);
					}
				}
			}

		} else if (field.getType().equalsIgnoreCase(
				PayAsiaConstants.FIELD_TYPE_DATE)) {
			String base64Str = new String(Base64.decodeBase64(field
					.getDictionaryName().getBytes()));

			try {
				base64Str = new String(URLDecoder.decode(base64Str, "UTF8"));
			} catch (UnsupportedEncodingException uee) {
				LOGGER.error(uee.getMessage(), uee);
				throw new PayAsiaSystemException(uee.getMessage(), uee);
			} catch (IllegalArgumentException ill) {
				LOGGER.error(ill.getMessage(), ill);
			}
			Font baseFont = setBaseFontForField(field);

			PdfPCell nestedLeftCell = null;
			PdfPCell nestedRightCell = null;

			if (field.isBold() == null && field.isUnderline() == null) {
				nestedLeftCell = new PdfPCell(new Phrase(base64Str,
						getBaseFontBold()));
			} else {
				nestedLeftCell = new PdfPCell(new Phrase(base64Str, baseFont));
			}
			nestedLeftCell.setPadding(2);
			nestedLeftCell.setBorder(0);
			if (payslip != null) {
				String fieldValue = field.getValue();
				if (!isFieldValueBlankOrZero(fieldValue)) {
					if (field.isDefault()) {
						nestedRightCell = new PdfPCell(new Phrase(
								field.getValue(), baseFont));
					} else {
						try {
							nestedRightCell = new PdfPCell(new Phrase(
									field.getValue(), baseFont));
						} catch (NumberFormatException e) {
							nestedRightCell = new PdfPCell(new Phrase(
									field.getValue(), baseFont));
						}
					}
					nestedRightCell.setPadding(2);
					nestedRightCell.setBorder(0);
					if (field.isHideLabel() != null && field.isHideLabel()) {
						nestedRightCell.setColspan(2);
						nestedLeftColumnTable.addCell(nestedRightCell);
					} else {
						nestedLeftColumnTable.addCell(nestedLeftCell);
						nestedLeftColumnTable.addCell(nestedRightCell);
					}
				}
			} else {
				nestedRightCell = new PdfPCell(new Phrase(
						PayAsiaConstants.PAYSLIP_FIELD_VALUE, baseFont));
				nestedRightCell.setPadding(2);
				nestedRightCell.setBorder(0);
			}
			if (nestedRightCell != null) {
				if (field.getAlign() != null
						&& "right".equalsIgnoreCase(field.getAlign())) {
					nestedLeftCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					nestedRightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				} else if (field.getAlign() != null
						&& "center".equalsIgnoreCase(field.getAlign())) {
					nestedLeftCell.setHorizontalAlignment(Element.ALIGN_CENTER);
					nestedRightCell
							.setHorizontalAlignment(Element.ALIGN_CENTER);
				} else {
					nestedLeftCell.setHorizontalAlignment(Element.ALIGN_LEFT);
					nestedRightCell.setHorizontalAlignment(Element.ALIGN_LEFT);
				}
				if (payslip != null) {
				} else {
					if (field.isHideLabel() != null && field.isHideLabel()) {
						nestedRightCell.setColspan(2);
						nestedLeftColumnTable.addCell(nestedRightCell);
					} else {
						nestedLeftColumnTable.addCell(nestedLeftCell);
						nestedLeftColumnTable.addCell(nestedRightCell);
					}
				}
			}

		} else if (field.getType().equalsIgnoreCase(
				PayAsiaConstants.FIELD_TYPE_NUMERIC)) {
			String base64Str = new String(Base64.decodeBase64(field
					.getDictionaryName().getBytes()));

			try {
				base64Str = new String(URLDecoder.decode(base64Str, "UTF8"));
			} catch (UnsupportedEncodingException uee) {
				LOGGER.error(uee.getMessage(), uee);
				throw new PayAsiaSystemException(uee.getMessage(), uee);
			} catch (IllegalArgumentException ill) {
				LOGGER.error(ill.getMessage(), ill);
			}
			Font baseFont = setBaseFontForField(field);

			PdfPCell nestedLeftCell = null;
			PdfPCell nestedRightCell = null;

			if (field.isBold() == null && field.isUnderline() == null) {
				nestedLeftCell = new PdfPCell(new Phrase(base64Str,
						getBaseFontBold()));
			} else {
				nestedLeftCell = new PdfPCell(new Phrase(base64Str, baseFont));
			}
			nestedLeftCell.setPadding(2);
			nestedLeftCell.setBorder(0);

			if (payslip != null) {
				String fieldValue = field.getValue();
				if (!isFieldValueBlankOrZero(fieldValue)) {
					if (field.isDefault()) {
						nestedRightCell = new PdfPCell(new Phrase(
								field.getValue(), baseFont));
					} else {
						try {
							nestedRightCell = new PdfPCell(new Phrase(
									field.getValue(), baseFont));
						} catch (NumberFormatException e) {
							nestedRightCell = new PdfPCell(new Phrase(
									field.getValue(), baseFont));
						}
					}
					nestedRightCell.setPadding(2);
					nestedRightCell.setBorder(0);
					if (field.isHideLabel() != null && field.isHideLabel()) {
						nestedRightCell.setColspan(2);
						nestedLeftColumnTable.addCell(nestedRightCell);
					} else {
						nestedLeftColumnTable.addCell(nestedLeftCell);
						nestedLeftColumnTable.addCell(nestedRightCell);
					}
				}
			} else {
				nestedRightCell = new PdfPCell(new Phrase(
						PayAsiaConstants.PAYSLIP_FIELD_VALUE, baseFont));
				nestedRightCell.setPadding(2);
				nestedRightCell.setBorder(0);
			}
			if (nestedRightCell != null) {
				if (field.getAlign() != null
						&& "right".equalsIgnoreCase(field.getAlign())) {
					nestedLeftCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					nestedRightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				} else if (field.getAlign() != null
						&& "center".equalsIgnoreCase(field.getAlign())) {
					nestedLeftCell.setHorizontalAlignment(Element.ALIGN_CENTER);
					nestedRightCell
							.setHorizontalAlignment(Element.ALIGN_CENTER);
				} else {
					nestedLeftCell.setHorizontalAlignment(Element.ALIGN_LEFT);
					nestedRightCell.setHorizontalAlignment(Element.ALIGN_LEFT);
				}
				if (field.isHideLabel() != null && field.isHideLabel()) {
					nestedRightCell.setColspan(2);
					nestedLeftColumnTable.addCell(nestedRightCell);
				} else {
					nestedLeftColumnTable.addCell(nestedLeftCell);
					nestedLeftColumnTable.addCell(nestedRightCell);
				}
			}
		} else if (field.getType().equalsIgnoreCase(
				PayAsiaConstants.FIELD_TYPE_CALCULATORY_FIELD)) {
			String base64Str = new String(Base64.decodeBase64(field
					.getDictionaryName().getBytes()));

			try {
				base64Str = new String(URLDecoder.decode(base64Str, "UTF8"));
			} catch (UnsupportedEncodingException uee) {
				LOGGER.error(uee.getMessage(), uee);
				throw new PayAsiaSystemException(uee.getMessage(), uee);
			} catch (IllegalArgumentException ill) {
				LOGGER.error(ill.getMessage(), ill);
			}
			Font baseFont = setBaseFontForField(field);

			PdfPCell nestedLeftCell = null;
			PdfPCell nestedRightCell = null;

			if (field.isBold() == null && field.isUnderline() == null) {
				nestedLeftCell = new PdfPCell(new Phrase(base64Str,
						getBaseFontBold()));
			} else {
				nestedLeftCell = new PdfPCell(new Phrase(base64Str, baseFont));
			}
			nestedLeftCell.setPadding(2);
			nestedLeftCell.setBorder(0);
			if (payslip != null) {
				String fieldValue = field.getValue();
				if (!isFieldValueBlankOrZero(fieldValue)) {
					if (field.isDefault()) {
						nestedRightCell = new PdfPCell(new Phrase(
								field.getValue(), baseFont));
					} else {
						try {
							nestedRightCell = new PdfPCell(new Phrase(
									field.getValue(), baseFont));
						} catch (NumberFormatException e) {
							nestedRightCell = new PdfPCell(new Phrase(
									field.getValue(), baseFont));
						}
					}
					nestedRightCell.setPadding(2);
					nestedRightCell.setBorder(0);
					if (field.isHideLabel() != null && field.isHideLabel()) {
						nestedRightCell.setColspan(2);
						nestedLeftColumnTable.addCell(nestedRightCell);
					} else {
						nestedLeftColumnTable.addCell(nestedLeftCell);
						nestedLeftColumnTable.addCell(nestedRightCell);
					}
				}
			} else {
				nestedRightCell = new PdfPCell(new Phrase(
						PayAsiaConstants.PAYSLIP_FIELD_VALUE, baseFont));
				nestedRightCell.setPadding(2);
				nestedRightCell.setBorder(0);
			}
			if (nestedRightCell != null) {
				if (field.getAlign() != null
						&& "right".equalsIgnoreCase(field.getAlign())) {
					nestedLeftCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					nestedRightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				} else if (field.getAlign() != null
						&& "center".equalsIgnoreCase(field.getAlign())) {
					nestedLeftCell.setHorizontalAlignment(Element.ALIGN_CENTER);
					nestedRightCell
							.setHorizontalAlignment(Element.ALIGN_CENTER);
				} else {
					nestedLeftCell.setHorizontalAlignment(Element.ALIGN_LEFT);
					nestedRightCell.setHorizontalAlignment(Element.ALIGN_LEFT);

				}
				if (field.isHideLabel() != null && field.isHideLabel()) {
					nestedRightCell.setColspan(2);
					nestedLeftColumnTable.addCell(nestedRightCell);
				} else {
					nestedLeftColumnTable.addCell(nestedLeftCell);
					nestedLeftColumnTable.addCell(nestedRightCell);
				}
			}

		} else if (field.getType().equalsIgnoreCase(
				PayAsiaConstants.FIELD_TYPE_LABEL)) {
			String base64Str = new String(Base64.decodeBase64(field
					.getDictionaryName().getBytes()));

			try {
				base64Str = new String(URLDecoder.decode(base64Str, "UTF8"));
			} catch (UnsupportedEncodingException uee) {
				LOGGER.error(uee.getMessage(), uee);
				throw new PayAsiaSystemException(uee.getMessage(), uee);
			} catch (IllegalArgumentException ill) {
				LOGGER.error(ill.getMessage(), ill);
			}
			Font baseFont = setBaseFontForField(field);

			PdfPCell labelCell;

			if (StringUtils.isNotBlank(base64Str)) {
				if (field.isBold() == null && field.isUnderline() == null) {
					labelCell = new PdfPCell(new Phrase(base64Str,
							getBaseFontBold()));
				} else {
					labelCell = new PdfPCell(new Phrase(base64Str, baseFont));
				}
			} else {
				if (field.isBold() == null && field.isUnderline() == null) {
					labelCell = new PdfPCell(new Phrase(new String(
							Base64.decodeBase64(field.getLabel().getBytes())),
							getBaseFontBold()));
				} else {
					labelCell = new PdfPCell(new Phrase(new String(
							Base64.decodeBase64(field.getLabel().getBytes())),
							baseFont));
				}

			}

			if (field.getAlign() != null
					&& "right".equalsIgnoreCase(field.getAlign())) {
				labelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			} else if (field.getAlign() != null
					&& "center".equalsIgnoreCase(field.getAlign())) {
				labelCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			} else {
				labelCell.setHorizontalAlignment(Element.ALIGN_LEFT);

			}
			labelCell.setColspan(2);
			labelCell.setPadding(2);
			labelCell.setBorder(0);
			if (field.isHideLabel() != null && field.isHideLabel()) {
				PdfPCell hideLabelCell = new PdfPCell(new Phrase("",
						getBaseFontNormal()));
				nestedLeftColumnTable.addCell(hideLabelCell);
			} else {
				nestedLeftColumnTable.addCell(labelCell);
			}
		}

		leftColumnCell.addElement(nestedLeftColumnTable);
		return leftColumnCell;

	}

	/**
	 * Gets the footer right column.
	 * 
	 * @param payslip
	 *            the payslip
	 * @param field
	 *            the field
	 * @return the footer right column
	 * @throws BadElementException
	 *             the bad element exception
	 * @throws MalformedURLException
	 *             the malformed url exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private PdfPCell getFooterRightColumn(Payslip payslip, Field field)
			throws BadElementException, MalformedURLException, IOException {

		PdfPCell rightColumnCell = new PdfPCell();
		rightColumnCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		rightColumnCell.setPadding(0);

		PdfPTable nestedRightColumnTable = new PdfPTable(new float[] { 1, 1 });
		nestedRightColumnTable.setWidthPercentage(100f);
		nestedRightColumnTable.getTotalHeight();

		if (field.getType().equalsIgnoreCase(PayAsiaConstants.FIELD_TYPE_TEXT)) {
			String base64Str = new String(Base64.decodeBase64(field
					.getDictionaryName().getBytes()));

			try {
				base64Str = new String(URLDecoder.decode(base64Str, "UTF8"));
			} catch (UnsupportedEncodingException uee) {
				LOGGER.error(uee.getMessage(), uee);
				throw new PayAsiaSystemException(uee.getMessage(), uee);
			} catch (IllegalArgumentException ill) {
				LOGGER.error(ill.getMessage(), ill);
			}
			Font baseFont = setBaseFontForField(field);

			PdfPCell nestedLeftCell = null;
			PdfPCell nestedRightCell = null;

			if (field.isBold() == null && field.isUnderline() == null) {
				nestedLeftCell = new PdfPCell(new Phrase(base64Str,
						getBaseFontBold()));
			} else {
				nestedLeftCell = new PdfPCell(new Phrase(base64Str, baseFont));
			}
			nestedLeftCell.setPadding(2);
			nestedLeftCell.setBorder(0);
			nestedLeftCell.setPaddingLeft(5);
			if (payslip != null) {
				String fieldValue = field.getValue();
				if (!isFieldValueBlankOrZero(fieldValue)) {
					if (field.isDefault()) {
						nestedRightCell = new PdfPCell(new Phrase(
								field.getValue(), baseFont));
					} else {
						try {
							nestedRightCell = new PdfPCell(new Phrase(
									field.getValue(), baseFont));
						} catch (NumberFormatException e) {
							nestedRightCell = new PdfPCell(new Phrase(
									field.getValue(), baseFont));
						}
					}
					nestedRightCell.setPadding(2);
					nestedRightCell.setBorder(0);
					setPayslipColDataAlignment(field, nestedLeftCell,
							nestedRightCell);
					if (field.isHideLabel() != null && field.isHideLabel()) {
						nestedRightCell.setColspan(2);
						nestedRightColumnTable.addCell(nestedRightCell);
					} else {
						nestedRightColumnTable.addCell(nestedLeftCell);
						nestedRightColumnTable.addCell(nestedRightCell);
					}
				}
			} else {
				nestedRightCell = new PdfPCell(new Phrase(
						PayAsiaConstants.PAYSLIP_FIELD_VALUE, baseFont));
				nestedRightCell.setPadding(2);
				setPayslipColDataAlignment(field, nestedLeftCell,
						nestedRightCell);
				nestedRightCell.setBorder(0);
			}

			if (payslip != null) {
			} else {
				if (field.isHideLabel() != null && field.isHideLabel()) {
					nestedRightCell.setColspan(2);
					nestedRightColumnTable.addCell(nestedRightCell);
				} else {
					nestedRightColumnTable.addCell(nestedLeftCell);
					nestedRightColumnTable.addCell(nestedRightCell);
				}
			}

		} else if (field.getType().equalsIgnoreCase(
				PayAsiaConstants.FIELD_TYPE_NUMERIC)) {
			String base64Str = new String(Base64.decodeBase64(field
					.getDictionaryName().getBytes()));

			try {
				base64Str = new String(URLDecoder.decode(base64Str, "UTF8"));
			} catch (UnsupportedEncodingException uee) {
				LOGGER.error(uee.getMessage(), uee);
				throw new PayAsiaSystemException(uee.getMessage(), uee);
			} catch (IllegalArgumentException ill) {
				LOGGER.error(ill.getMessage(), ill);
			}
			Font baseFont = setBaseFontForField(field);

			PdfPCell nestedLeftCell = null;
			PdfPCell nestedRightCell = null;

			if (field.isBold() == null && field.isUnderline() == null) {
				nestedLeftCell = new PdfPCell(new Phrase(base64Str,
						getBaseFontBold()));
			} else {
				nestedLeftCell = new PdfPCell(new Phrase(base64Str, baseFont));
			}
			nestedLeftCell.setPadding(2);
			nestedLeftCell.setBorder(0);
			nestedLeftCell.setPaddingLeft(5);

			if (payslip != null) {
				String fieldValue = field.getValue();
				if (!isFieldValueBlankOrZero(fieldValue)) {
					if (field.isDefault()) {
						nestedRightCell = new PdfPCell(new Phrase(
								field.getValue(), baseFont));
					} else {
						try {
							nestedRightCell = new PdfPCell(new Phrase(
									field.getValue(), baseFont));
						} catch (NumberFormatException e) {
							nestedRightCell = new PdfPCell(new Phrase(
									field.getValue(), baseFont));
						}
					}
					nestedRightCell.setPadding(2);
					nestedRightCell.setBorder(0);
					if (field.isHideLabel() != null && field.isHideLabel()) {
						nestedRightCell.setColspan(2);
						nestedRightColumnTable.addCell(nestedRightCell);
					} else {
						nestedRightColumnTable.addCell(nestedLeftCell);
						nestedRightColumnTable.addCell(nestedRightCell);
					}
				}
			} else {
				nestedRightCell = new PdfPCell(new Phrase(
						PayAsiaConstants.PAYSLIP_FIELD_VALUE, baseFont));
				nestedRightCell.setPadding(2);
				nestedRightCell.setBorder(0);
			}
			if (nestedRightCell != null) {
				if (field.getAlign() != null
						&& "right".equalsIgnoreCase(field.getAlign())) {
					nestedLeftCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					nestedRightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

				} else if (field.getAlign() != null
						&& "center".equalsIgnoreCase(field.getAlign())) {
					nestedLeftCell.setHorizontalAlignment(Element.ALIGN_CENTER);
					nestedRightCell
							.setHorizontalAlignment(Element.ALIGN_CENTER);
				} else {
					nestedLeftCell.setHorizontalAlignment(Element.ALIGN_LEFT);
					nestedRightCell.setHorizontalAlignment(Element.ALIGN_LEFT);
				}
				if (payslip != null) {
				} else {
					if (field.isHideLabel() != null && field.isHideLabel()) {
						nestedRightCell.setColspan(2);
						nestedRightColumnTable.addCell(nestedRightCell);
					} else {
						nestedRightColumnTable.addCell(nestedLeftCell);
						nestedRightColumnTable.addCell(nestedRightCell);
					}
				}
			}
		} else if (field.getType().equalsIgnoreCase(
				PayAsiaConstants.FIELD_TYPE_DATE)) {
			String base64Str = new String(Base64.decodeBase64(field
					.getDictionaryName().getBytes()));

			try {
				base64Str = new String(URLDecoder.decode(base64Str, "UTF8"));
			} catch (UnsupportedEncodingException uee) {
				LOGGER.error(uee.getMessage(), uee);
				throw new PayAsiaSystemException(uee.getMessage(), uee);
			} catch (IllegalArgumentException ill) {
				LOGGER.error(ill.getMessage(), ill);
			}
			Font baseFont = setBaseFontForField(field);

			PdfPCell nestedLeftCell = null;
			PdfPCell nestedRightCell = null;

			if (field.isBold() == null && field.isUnderline() == null) {
				nestedLeftCell = new PdfPCell(new Phrase(base64Str,
						getBaseFontBold()));
			} else {
				nestedLeftCell = new PdfPCell(new Phrase(base64Str, baseFont));
			}
			nestedLeftCell.setPadding(2);
			nestedLeftCell.setBorder(0);
			nestedLeftCell.setPaddingLeft(5);
			if (payslip != null) {
				String fieldValue = field.getValue();
				if (!isFieldValueBlankOrZero(fieldValue)) {
					if (field.isDefault()) {
						nestedRightCell = new PdfPCell(new Phrase(
								field.getValue(), baseFont));
					} else {
						try {
							nestedRightCell = new PdfPCell(new Phrase(
									field.getValue(), baseFont));
						} catch (NumberFormatException e) {
							nestedRightCell = new PdfPCell(new Phrase(
									field.getValue(), baseFont));
						}
					}
					nestedRightCell.setPadding(2);
					nestedRightCell.setBorder(0);
					if (field.isHideLabel() != null && field.isHideLabel()) {
						nestedRightCell.setColspan(2);
						nestedRightColumnTable.addCell(nestedRightCell);
					} else {
						nestedRightColumnTable.addCell(nestedLeftCell);
						nestedRightColumnTable.addCell(nestedRightCell);
					}
				}
			} else {
				nestedRightCell = new PdfPCell(new Phrase(
						PayAsiaConstants.PAYSLIP_FIELD_VALUE, baseFont));
				nestedRightCell.setPadding(2);
				nestedRightCell.setBorder(0);
			}
			if (nestedRightCell != null) {
				if (field.getAlign() != null
						&& "right".equalsIgnoreCase(field.getAlign())) {
					nestedLeftCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					nestedRightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				} else if (field.getAlign() != null
						&& "center".equalsIgnoreCase(field.getAlign())) {
					nestedLeftCell.setHorizontalAlignment(Element.ALIGN_CENTER);
					nestedRightCell
							.setHorizontalAlignment(Element.ALIGN_CENTER);
				} else {
					nestedLeftCell.setHorizontalAlignment(Element.ALIGN_LEFT);
					nestedRightCell.setHorizontalAlignment(Element.ALIGN_LEFT);

				}
				if (field.isHideLabel() != null && field.isHideLabel()) {
					nestedRightCell.setColspan(2);
					nestedRightColumnTable.addCell(nestedRightCell);
				} else {
					nestedRightColumnTable.addCell(nestedLeftCell);
					nestedRightColumnTable.addCell(nestedRightCell);
				}
			}
		} else if (field.getType().equalsIgnoreCase(
				PayAsiaConstants.FIELD_TYPE_CALCULATORY_FIELD)) {
			String base64Str = new String(Base64.decodeBase64(field
					.getDictionaryName().getBytes()));

			try {
				base64Str = new String(URLDecoder.decode(base64Str, "UTF8"));
			} catch (UnsupportedEncodingException uee) {
				LOGGER.error(uee.getMessage(), uee);
				throw new PayAsiaSystemException(uee.getMessage(), uee);
			} catch (IllegalArgumentException ill) {
				LOGGER.error(ill.getMessage(), ill);
			}
			Font baseFont = setBaseFontForField(field);

			PdfPCell nestedLeftCell = null;
			PdfPCell nestedRightCell = null;

			if (field.isBold() == null && field.isUnderline() == null) {
				nestedLeftCell = new PdfPCell(new Phrase(base64Str,
						getBaseFontBold()));
			} else {
				nestedLeftCell = new PdfPCell(new Phrase(base64Str, baseFont));
			}
			nestedLeftCell.setPadding(2);
			nestedLeftCell.setBorder(0);
			nestedLeftCell.setPaddingLeft(5);
			if (payslip != null) {
				String fieldValue = field.getValue();
				if (!isFieldValueBlankOrZero(fieldValue)) {
					if (field.isDefault()) {
						nestedRightCell = new PdfPCell(new Phrase(
								field.getValue(), baseFont));
					} else {
						try {
							nestedRightCell = new PdfPCell(new Phrase(
									field.getValue(), baseFont));
						} catch (NumberFormatException e) {
							nestedRightCell = new PdfPCell(new Phrase(
									field.getValue(), baseFont));
						}
					}
					nestedRightCell.setPadding(2);
					nestedRightCell.setBorder(0);
					if (field.isHideLabel() != null && field.isHideLabel()) {
						nestedRightCell.setColspan(2);
						nestedRightColumnTable.addCell(nestedRightCell);
					} else {
						nestedRightColumnTable.addCell(nestedLeftCell);
						nestedRightColumnTable.addCell(nestedRightCell);
					}
				}
			} else {
				nestedRightCell = new PdfPCell(new Phrase(
						PayAsiaConstants.PAYSLIP_FIELD_VALUE, baseFont));
				nestedRightCell.setPadding(2);
				nestedRightCell.setBorder(0);
			}
			if (nestedRightCell != null) {
				if (field.getAlign() != null
						&& "right".equalsIgnoreCase(field.getAlign())) {
					nestedLeftCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					nestedRightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				} else if (field.getAlign() != null
						&& "center".equalsIgnoreCase(field.getAlign())) {
					nestedLeftCell.setHorizontalAlignment(Element.ALIGN_CENTER);
					nestedRightCell
							.setHorizontalAlignment(Element.ALIGN_CENTER);
				} else {
					nestedLeftCell.setHorizontalAlignment(Element.ALIGN_LEFT);
					nestedRightCell.setHorizontalAlignment(Element.ALIGN_LEFT);
				}
				if (field.isHideLabel() != null && field.isHideLabel()) {
					nestedRightCell.setColspan(2);
					nestedRightColumnTable.addCell(nestedRightCell);
				} else {
					nestedRightColumnTable.addCell(nestedLeftCell);
					nestedRightColumnTable.addCell(nestedRightCell);
				}
			}

		} else if (field.getType().equalsIgnoreCase(
				PayAsiaConstants.FIELD_TYPE_LABEL)) {
			String base64Str = new String(Base64.decodeBase64(field
					.getDictionaryName().getBytes()));

			try {
				base64Str = new String(URLDecoder.decode(base64Str, "UTF8"));
			} catch (UnsupportedEncodingException uee) {
				LOGGER.error(uee.getMessage(), uee);
				throw new PayAsiaSystemException(uee.getMessage(), uee);
			} catch (IllegalArgumentException ill) {
				LOGGER.error(ill.getMessage(), ill);
			}
			Font baseFont = setBaseFontForField(field);

			PdfPCell labelCell;

			if (StringUtils.isNotBlank(base64Str)) {
				if (field.isBold() == null && field.isUnderline() == null) {
					labelCell = new PdfPCell(new Phrase(base64Str,
							getBaseFontBold()));
				} else {
					labelCell = new PdfPCell(new Phrase(base64Str, baseFont));
				}
			} else {
				if (field.isBold() == null && field.isUnderline() == null) {
					labelCell = new PdfPCell(new Phrase(new String(
							Base64.decodeBase64(field.getLabel().getBytes())),
							getBaseFontBold()));
				} else {
					labelCell = new PdfPCell(new Phrase(new String(
							Base64.decodeBase64(field.getLabel().getBytes())),
							baseFont));
				}

			}

			if (field.getAlign() != null
					&& "right".equalsIgnoreCase(field.getAlign())) {
				labelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			} else if (field.getAlign() != null
					&& "center".equalsIgnoreCase(field.getAlign())) {
				labelCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			} else {
				labelCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			}
			labelCell.setColspan(2);
			labelCell.setPaddingLeft(5);
			labelCell.setPadding(2);
			labelCell.setBorder(0);
			if (field.isHideLabel() != null && field.isHideLabel()) {
				PdfPCell hideLabelCell = new PdfPCell(new Phrase("",
						getBaseFontNormal()));
				nestedRightColumnTable.addCell(hideLabelCell);
			} else {
				nestedRightColumnTable.addCell(labelCell);
			}
		}

		rightColumnCell.addElement(nestedRightColumnTable);
		return rightColumnCell;

	}

	/**
	 * Gets the page numbering phrase.
	 * 
	 * @param currentPageNumber
	 *            the currentPageNumber
	 * 
	 * @return the page number
	 */
	private Phrase getPageNumberingPhrase(int currentPageNumber) {
		Phrase pageNumber = new Phrase(String.format("%s", currentPageNumber),
				getBaseFontNormal());

		return pageNumber;
	}

	/**
	 * Gets the page Footer height.
	 * 
	 * @param writer
	 *            the writer
	 * @param document
	 *            the document
	 * @return the page Footer height
	 * @throws BadElementException
	 *             the bad element exception
	 * @throws MalformedURLException
	 *             the malformed url exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws JAXBException
	 *             the jAXB exception
	 * @throws SAXException
	 *             the sAX exception
	 */
	@Override
	public float getFooterHeight(PdfWriter writer, Document document,
			List<DynamicFormRecord> dynamicFormRecordList)
			throws BadElementException, MalformedURLException, IOException,
			JAXBException, SAXException {
		int currentPageNumber = writer.getCurrentPageNumber();

		boolean enablePageNumbers = PDFThreadLocal.pageNumbers.get();
		PDFThreadLocal.pageNumbers.set(false);

		PdfPTable footerSectionTable = getFooterSection(document, payslip,
				currentPageNumber, dynamicFormRecordList);
		float tableWidth = document.right() - document.left() - 3
				* PayAsiaPDFConstants.X_PADDING;
		footerSectionTable.setTotalWidth(tableWidth);
		float tableFooterHeight = footerSectionTable.calculateHeights();

		PDFThreadLocal.pageNumbers.set(enablePageNumbers);

		return tableFooterHeight;

	}

	/**
	 * Check if FieldValue Blank Or Zero.
	 * 
	 * @param fieldValue
	 *            the field value
	 * @return true, if is field value blank or zero
	 */
	private boolean isFieldValueBlankOrZero(String fieldValue) {
		if (StringUtils.isBlank(fieldValue)) {
			return true;
		}
		try {
			if (Double.parseDouble(fieldValue) == 0) {
				return true;
			}
		} catch (NumberFormatException nfe) {
			return false;
		}
		return false;
	}

	private Font getBaseFontNormal() {
		BaseFont bf = null;
		try {
			try {
				bf = BaseFont.createFont(
						PayAsiaPDFConstants.PDF_MULTILINGUAL_FONT_PATH,
						BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
			} catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
			}
		} catch (DocumentException e1) {
			LOGGER.error(e1.getMessage(), e1);
		}
		Font unicodeFont = new Font(bf, 10, Font.NORMAL, BaseColor.BLACK);
		return unicodeFont;

	}

	private Font getBaseFontBold() {
		BaseFont bf = null;
		try {
			try {
				bf = BaseFont.createFont(
						PayAsiaPDFConstants.PDF_MULTILINGUAL_FONT_PATH,
						BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
			} catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
			}
		} catch (DocumentException e1) {
			LOGGER.error(e1.getMessage(), e1);
		}
		Font unicodeFont = new Font(bf, 10, Font.BOLD, BaseColor.BLACK);
		return unicodeFont;

	}

	private Font getBaseFontBoldUnderLine() {
		BaseFont bf = null;
		try {
			try {
				bf = BaseFont.createFont(
						PayAsiaPDFConstants.PDF_MULTILINGUAL_FONT_PATH,
						BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
			} catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
			}
		} catch (DocumentException e1) {
			LOGGER.error(e1.getMessage(), e1);
		}
		Font unicodeFont = new Font(bf, 10, Font.BOLD | Font.UNDERLINE,
				BaseColor.BLACK);
		return unicodeFont;

	}

	private Font getBaseFontUnderLine() {
		BaseFont bf = null;
		try {
			try {
				bf = BaseFont.createFont(
						PayAsiaPDFConstants.PDF_MULTILINGUAL_FONT_PATH,
						BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
			} catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
			}
		} catch (DocumentException e1) {
			LOGGER.error(e1.getMessage(), e1);
		}
		Font unicodeFont = new Font(bf, 10, Font.UNDERLINE, BaseColor.BLACK);
		return unicodeFont;

	}

}
