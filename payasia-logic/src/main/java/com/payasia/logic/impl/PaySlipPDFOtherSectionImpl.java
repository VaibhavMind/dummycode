package com.payasia.logic.impl;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.text.DecimalFormat;
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
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
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
import com.payasia.common.util.PDFUtils;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.PayAsiaPDFConstants;
import com.payasia.common.util.XMLUtil;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.DynamicFormDAO;
import com.payasia.dao.EntityMasterDAO;
import com.payasia.dao.bean.DynamicForm;
import com.payasia.dao.bean.DynamicFormRecord;
import com.payasia.dao.bean.EntityMaster;
import com.payasia.dao.bean.Payslip;
import com.payasia.logic.MultilingualLogic;
import com.payasia.logic.PaySlipPDFOtherSection;
import com.payasia.logic.PayslipDataUtils;

/**
 * The Class PaySlipPDFOtherSectionImpl.
 */
/**
 * @author vivekjain
 * 
 */
@Component
public class PaySlipPDFOtherSectionImpl implements PaySlipPDFOtherSection {

	/** The Constant logger. */
	private static final Logger LOGGER = Logger
			.getLogger(PaySlipPDFOtherSectionImpl.class);

	/** The entity master dao. */
	@Resource
	EntityMasterDAO entityMasterDAO;

	/** The company dao. */
	@Resource
	CompanyDAO companyDAO;

	/** The dynamic form dao. */
	@Resource
	DynamicFormDAO dynamicFormDAO;

	/** The payslip data utils. */
	@Resource
	PayslipDataUtils payslipDataUtils;

	/** The company id. */
	private Long companyId;

	private int leftColumnCount;
	private int leftColumnKeyCount;
	private int rightColumnKeyCount;
	private String leftColumnKey;
	private String leftColumnValue;
	HashMap<Integer, Integer> columnLeftColumnKeyMap = new HashMap<>();
	HashMap<Integer, Integer> columnRightColumnKeyMap = new HashMap<>();
	/** The payslip. */
	private Payslip payslip;

	/** The pay slip pdf template dto. */
	private PaySlipPDFTemplateDTO paySlipPDFTemplateDTO;
	@Resource
	MultilingualLogic multilingualLogic;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.PaySlipPDFOtherSection#setPayslip(com.payasia.dao.bean
	 * .Payslip)
	 */
	@Override
	public void setPayslip(Payslip payslip) {
		this.payslip = payslip;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.PaySlipPDFOtherSection#getPaySlipPDFTemplateDTO()
	 */
	@Override
	public PaySlipPDFTemplateDTO getPaySlipPDFTemplateDTO() {
		return paySlipPDFTemplateDTO;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.PaySlipPDFOtherSection#setPaySlipPDFTemplateDTO(com
	 * .payasia.common.dto.PaySlipPDFTemplateDTO)
	 */
	@Override
	public void setPaySlipPDFTemplateDTO(
			PaySlipPDFTemplateDTO paySlipPDFTemplateDTO) {
		this.paySlipPDFTemplateDTO = paySlipPDFTemplateDTO;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.PaySlipPDFOtherSection#setCompanyId(java.lang.Long)
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
	 * Prepare the Payslip Other section .
	 * 
	 * @param document
	 *            the document
	 * @param writer
	 *            the writer
	 * @param paySlipPDFTemplateDTO
	 *            the pay slip pdf template dto
	 * @param payslip
	 *            the payslip
	 * @throws MalformedURLException
	 *             the malformed url exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws DocumentException
	 *             the Document Exception
	 */
	@Override
	public void preparePaySlipPDFOtherSection(Document document,
			PdfWriter writer, PaySlipPDFTemplateDTO paySlipPDFTemplateDTO,
			Payslip payslip, List<DynamicFormRecord> dynamicFormRecordList)
			throws MalformedURLException, IOException, DocumentException {

		float tableWidth = document.right() - document.left() - 3
				* PayAsiaPDFConstants.X_PADDING;

		PdfPTable otherSectionTable = getAllOtherSection(document, payslip,
				dynamicFormRecordList);
		otherSectionTable.setTotalWidth(tableWidth);

		ColumnText column = new ColumnText(writer.getDirectContent());
		column.addElement(otherSectionTable);

		int count = 0;
		float height = 0;
		float footerHeight = 0;
		float rptFooterHeigth = paySlipPDFTemplateDTO.getFooterSection()
				.getHeight();
		int status = ColumnText.START_COLUMN;

		while (ColumnText.hasMoreText(status)) {

			height = paySlipPDFTemplateDTO.getLogoHeaderSection().getHeight()
					+ (2 * PayAsiaPDFConstants.Y_PADDING);
			footerHeight = rptFooterHeigth;

			column.setSimpleColumn(document.left(0f), document.bottom()
					+ footerHeight + (2 * PayAsiaPDFConstants.Y_PADDING),
					document.right(0f), document.top() - height, 0f,
					Element.ALIGN_CENTER);

			column.setFilledWidth(tableWidth);
			status = column.go();

			if (ColumnText.hasMoreText(status)) {
				count++;
				document.newPage();
			}
		}

		paySlipPDFTemplateDTO.getOtherSection().setHeight(
				otherSectionTable.getTotalHeight());

	}

	/**
	 * Gets the all other section.
	 * 
	 * @param document
	 *            the document
	 * @param payslip
	 *            the payslip
	 * @return the all other section Pdf Table
	 * @throws BadElementException
	 *             the bad element exception
	 * @throws MalformedURLException
	 *             the malformed url exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public PdfPTable getAllOtherSection(Document document, Payslip payslip,
			List<DynamicFormRecord> dynamicFormRecordList)
			throws BadElementException, MalformedURLException, IOException {
		PdfPTable allOtherSectionTable = new PdfPTable(new float[] { 1 });
		float tableWidth = document.right() - document.left() - 3
				* PayAsiaPDFConstants.X_PADDING;
		allOtherSectionTable.setTotalWidth(tableWidth);
		allOtherSectionTable.setSpacingBefore(2f);
		allOtherSectionTable.setHorizontalAlignment(Element.ALIGN_CENTER);

		PdfPCell totalIncomeSectionCell = getOtherSection(
				document,
				getXML(companyId, PayAsiaConstants.TOTAL_INCOME_SECTION,
						payslip), payslip, dynamicFormRecordList,
				PayAsiaConstants.TOTAL_INCOME_SECTION);
		totalIncomeSectionCell.setPadding(0);

		PdfPCell statutorySectionCell = getOtherSection(document,
				getXML(companyId, PayAsiaConstants.STATUTORY_SECTION, payslip),
				payslip, dynamicFormRecordList,
				PayAsiaConstants.STATUTORY_SECTION);
		statutorySectionCell.setPadding(0);

		PdfPCell summarySectionCell = getOtherSection(document,
				getXML(companyId, PayAsiaConstants.SUMMARY_SECTION, payslip),
				payslip, dynamicFormRecordList,
				PayAsiaConstants.SUMMARY_SECTION);
		summarySectionCell.setPadding(0);

		allOtherSectionTable.addCell(totalIncomeSectionCell);
		allOtherSectionTable.addCell(statutorySectionCell);
		allOtherSectionTable.addCell(summarySectionCell);

		return allOtherSectionTable;

	}

	/**
	 * Prepare the Other section PdfCell.
	 * 
	 * @param document
	 *            the document
	 * @param otherSectionXML
	 *            the other section xml
	 * @param payslip
	 *            the payslip
	 * @return the other section
	 * @throws BadElementException
	 *             the BadElement Exception
	 * @throws MalformedURLException
	 *             the malformed url exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Override
	public PdfPCell getOtherSection(Document document, String otherSectionXML,
			Payslip payslip, List<DynamicFormRecord> dynamicFormRecordList,
			String sectionName) throws BadElementException,
			MalformedURLException, IOException {
		Unmarshaller unmarshaller = null;
		try {
			unmarshaller = XMLUtil.getDocumentUnmarshaller();
		} catch (JAXBException ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new PayAsiaSystemException(ex.getMessage(), ex);
		} catch (SAXException ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new PayAsiaSystemException(ex.getMessage(), ex);
		}

		final StringReader xmlReader = new StringReader(otherSectionXML);
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
		} catch (JAXBException ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new PayAsiaSystemException(ex.getMessage(), ex);
		}

		HashMap<Long, Tab> dataTabMap = new HashMap<>();
		HashMap<Long, DynamicForm> dynamicFormMap = new HashMap<>();
		HashMap<String, ColumnPropertyDTO> staticPropMap = new HashMap<>();

		List<Field> leftColumnFieldList = new ArrayList<>();
		List<Field> rightColumnFieldList = new ArrayList<>();

		FieldOrder fieldOrder = tab.getFieldOrder();
		List<TabColumn> tabColumns = fieldOrder.getTabColumn();

		int columnNumber = 1;
		boolean status = false;

		for (TabColumn tabColumn : tabColumns) {

			List<String> fieldReferences = tabColumn.getFieldReference();
			for (String fieldReference : fieldReferences) {
				status = true;
				if (status) {
					if (otherSectionXML != "footerSection") {
					}
				}
				List<Field> fields = tab.getField();

				for (Field field : fields) {
					if (fieldReference.equalsIgnoreCase(field.getName())) {
						try {
							if (payslip != null) {
								payslipDataUtils.getEmployeeFieldValue(payslip,
										field, dataTabMap, dynamicFormMap,
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

						if (columnNumber == 1) {
							if (payslip != null) {
								String fieldValue = field.getValue();

								if (field.getType().equalsIgnoreCase(
										PayAsiaConstants.FIELD_TYPE_LABEL)) {
									leftColumnFieldList.add(field);
								} else {
									if (!isFieldValueBlankOrZero(fieldValue)) {
										leftColumnFieldList.add(field);
									}
								}

							} else {
								leftColumnFieldList.add(field);
							}

						}
						if (columnNumber == 2) {
							if (payslip != null) {
								String fieldValue = field.getValue();
								if (field.getType().equalsIgnoreCase(
										PayAsiaConstants.FIELD_TYPE_LABEL)) {
									rightColumnFieldList.add(field);
								} else {
									if (!isFieldValueBlankOrZero(fieldValue)) {
										rightColumnFieldList.add(field);
									}
								}

							} else {
								rightColumnFieldList.add(field);
							}

						}

					}

				}

			}

			columnNumber++;

		}

		int sizeMap1 = leftColumnFieldList.size();
		int sizeMap2 = rightColumnFieldList.size();
		int maxRowSize = 0;
		if (sizeMap1 > sizeMap2) {
			maxRowSize = sizeMap1;

			Field fieldTemp = null;

			if (sizeMap2 != 0) {

				int difference = sizeMap1 - sizeMap2 - 1;
				fieldTemp = rightColumnFieldList.get(sizeMap2 - 1);

				if (!sectionName
						.equalsIgnoreCase(PayAsiaConstants.TOTAL_INCOME_SECTION)) {
					for (int count = 0; count <= difference; count++) {
						rightColumnFieldList.add(null);
					}
				} else {
					rightColumnFieldList.set(sizeMap2 - 1, null);
					for (int count = 0; count < difference; count++) {
						rightColumnFieldList.add(null);
					}
					rightColumnFieldList.add(fieldTemp);
				}

			} else {

				int difference = sizeMap1 - sizeMap2;
				for (int count = 0; count < difference; count++) {
					rightColumnFieldList.add(null);
				}
			}

		}

		if (sizeMap1 < sizeMap2) {
			maxRowSize = sizeMap2;

			Field fieldTemp = null;
			if (sizeMap1 != 0) {

				int difference = sizeMap2 - sizeMap1 - 1;
				fieldTemp = leftColumnFieldList.get(sizeMap1 - 1);
				if (!sectionName
						.equalsIgnoreCase(PayAsiaConstants.TOTAL_INCOME_SECTION)) {
					for (int count = 0; count <= difference; count++) {
						leftColumnFieldList.add(null);
					}
				} else {
					leftColumnFieldList.set(sizeMap1 - 1, null);
					for (int count = 0; count < difference; count++) {
						leftColumnFieldList.add(null);
					}
					leftColumnFieldList.add(fieldTemp);
				}

			} else {

				int difference = sizeMap2 - sizeMap1;
				for (int count = 0; count < difference; count++) {
					leftColumnFieldList.add(null);
				}
			}

		}

		if (maxRowSize == 0) {
			maxRowSize = sizeMap1;
		}

		List<PayslipRowDTO> payslipRowDTOList = new ArrayList<>();
		for (int count = 0; count < maxRowSize; count++) {
			PayslipRowDTO payslipRowDTO = new PayslipRowDTO();
			payslipRowDTO.setLeftColSize(sizeMap1);
			payslipRowDTO.setRightColSize(sizeMap2);
			Field leftFieldTemp = leftColumnFieldList.get(count);
			Field rightFieldTemp = rightColumnFieldList.get(count);

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

		float tableWidth = document.right() - document.left() - 3
				* PayAsiaPDFConstants.X_PADDING;

		PdfPCell mainCell = new PdfPCell();
		mainCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		mainCell.setPadding(0);
		mainCell.setPaddingTop(20f);
		mainCell.setBorder(0);
		PDFUtils.setDefaultCellLayout(mainCell);

		PdfPTable nestedMainSectionTable = getPayslipOtherSectionRowsTable(
				payslipRowDTOList, payslip);
		nestedMainSectionTable.setWidthPercentage(125f);
		nestedMainSectionTable.setSpacingAfter(20f);

		mainCell.addElement(nestedMainSectionTable);
		return mainCell;
	}

	/**
	 * Gets the right column.
	 * 
	 * @param field
	 *            the field
	 * @param payslip
	 *            the payslip
	 * @return the right column PdfCell
	 */
	public PdfPCell getRightColumn(Field field, Payslip payslip) {
		DecimalFormat decimalFmt = new DecimalFormat("#,##0.00");
		rightColumnKeyCount++;

		PdfPCell rightColumnCell = new PdfPCell();
		rightColumnCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		rightColumnCell.setPadding(0);

		PdfPTable nestedRightColumnTable = new PdfPTable(new float[] { 1.5f,
				0.5f });
		nestedRightColumnTable.setWidthPercentage(99f);
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
			nestedLeftCell.setPaddingLeft(2);
			nestedLeftCell.enableBorderSide(Rectangle.BOTTOM);
			nestedLeftCell.enableBorderSide(Rectangle.TOP);
			nestedLeftCell.enableBorderSide(Rectangle.RIGHT);
			nestedLeftCell.enableBorderSide(Rectangle.LEFT);
			nestedLeftCell.setFixedHeight(17f);
			if (payslip != null) {
				String fieldValue = field.getValue();
				if (!isFieldValueBlankOrZero(fieldValue)) {
					if (field.isDefault()) {
						nestedRightCell = new PdfPCell(new Phrase(
								field.getValue(), baseFont));
					} else {
						// Get value entityId from Referenced variable as
						// temporarily as it is set in payslipDataUtils
						if (field.getReferenced().equals(
								PayAsiaConstants.PAYSLIP_FORM_ENTITY_ID)) {
							try {
								nestedRightCell = new PdfPCell(
										new Phrase(
												decimalFmt.format(Double
														.parseDouble(field
																.getValue())),
												baseFont));
							} catch (NumberFormatException e) {
								nestedRightCell = new PdfPCell(new Phrase(
										field.getValue(), baseFont));
							}
						} else {
							nestedRightCell = new PdfPCell(new Phrase(
									field.getValue(), baseFont));
						}

					}
					nestedRightCell.setPadding(2);
					nestedRightCell.setBorder(0);
					nestedRightCell.enableBorderSide(Rectangle.BOTTOM);
					nestedRightCell.enableBorderSide(Rectangle.TOP);
					nestedRightCell.enableBorderSide(Rectangle.RIGHT);
					nestedRightCell.setFixedHeight(17f);
					setPayslipColDataAlignment(field, nestedLeftCell,
							nestedRightCell);

					if (field.isHideLabel() != null && field.isHideLabel()) {
						nestedRightCell.setColspan(2);
						nestedRightCell.enableBorderSide(Rectangle.LEFT);
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
				nestedRightCell.enableBorderSide(Rectangle.BOTTOM);
				nestedRightCell.enableBorderSide(Rectangle.TOP);
				nestedRightCell.enableBorderSide(Rectangle.RIGHT);
				nestedRightCell.setFixedHeight(17f);
				setPayslipColDataAlignment(field, nestedLeftCell,
						nestedRightCell);
			}

			if (payslip != null) {
			} else {
				if (field.isHideLabel() != null && field.isHideLabel()) {
					nestedRightCell.setColspan(2);
					nestedRightCell.enableBorderSide(Rectangle.LEFT);
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
			nestedLeftCell.setPaddingLeft(2);
			nestedLeftCell.enableBorderSide(Rectangle.BOTTOM);
			nestedLeftCell.enableBorderSide(Rectangle.TOP);
			nestedLeftCell.enableBorderSide(Rectangle.RIGHT);
			nestedLeftCell.enableBorderSide(Rectangle.LEFT);
			nestedLeftCell.setFixedHeight(17f);
			if (payslip != null) {
				String fieldValue = field.getValue();
				if (!isFieldValueBlankOrZero(fieldValue)) {
					if (field.isDefault()) {
						nestedRightCell = new PdfPCell(new Phrase(
								field.getValue(), baseFont));
					} else {
						try {
							nestedRightCell = new PdfPCell(new Phrase(
									decimalFmt.format(Double.parseDouble(field
											.getValue())), baseFont));
						} catch (NumberFormatException e) {
							nestedRightCell = new PdfPCell(new Phrase(
									field.getValue(), baseFont));
						}
					}
					nestedRightCell.setPadding(2);
					nestedRightCell.setBorder(0);
					nestedRightCell.enableBorderSide(Rectangle.BOTTOM);
					nestedRightCell.enableBorderSide(Rectangle.TOP);
					nestedRightCell.enableBorderSide(Rectangle.RIGHT);
					nestedRightCell.setFixedHeight(17f);
					if (field.isHideLabel() != null && field.isHideLabel()) {
						nestedRightCell.setColspan(2);
						nestedRightCell.enableBorderSide(Rectangle.LEFT);
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
				nestedRightCell.enableBorderSide(Rectangle.BOTTOM);
				nestedRightCell.enableBorderSide(Rectangle.TOP);
				nestedRightCell.enableBorderSide(Rectangle.RIGHT);
				nestedRightCell.setFixedHeight(17f);
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
						nestedRightCell.enableBorderSide(Rectangle.LEFT);
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
			nestedLeftCell.setPaddingLeft(2);
			nestedLeftCell.enableBorderSide(Rectangle.BOTTOM);
			nestedLeftCell.enableBorderSide(Rectangle.TOP);
			nestedLeftCell.enableBorderSide(Rectangle.RIGHT);
			nestedLeftCell.enableBorderSide(Rectangle.LEFT);
			nestedLeftCell.setFixedHeight(17f);
			if (payslip != null) {
				String fieldValue = field.getValue();
				if (!isFieldValueBlankOrZero(fieldValue)) {
					if (field.isDefault()) {
						nestedRightCell = new PdfPCell(new Phrase(
								field.getValue(), baseFont));
					} else {
						try {
							nestedRightCell = new PdfPCell(new Phrase(
									decimalFmt.format(Double.parseDouble(field
											.getValue())), baseFont));
						} catch (NumberFormatException e) {
							nestedRightCell = new PdfPCell(new Phrase(
									field.getValue(), baseFont));
						}
					}
					nestedRightCell.setPadding(2);
					nestedRightCell.setBorder(0);
					nestedRightCell.enableBorderSide(Rectangle.BOTTOM);
					nestedRightCell.enableBorderSide(Rectangle.TOP);
					nestedRightCell.enableBorderSide(Rectangle.RIGHT);
					nestedRightCell.setFixedHeight(17f);
					if (field.isHideLabel() != null && field.isHideLabel()) {
						nestedRightCell.setColspan(2);
						nestedRightCell.enableBorderSide(Rectangle.LEFT);
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
				nestedRightCell.enableBorderSide(Rectangle.BOTTOM);
				nestedRightCell.enableBorderSide(Rectangle.TOP);
				nestedRightCell.enableBorderSide(Rectangle.RIGHT);
				nestedRightCell.setFixedHeight(17f);
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
					nestedRightCell.enableBorderSide(Rectangle.LEFT);
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
			nestedLeftCell.setPaddingLeft(2);
			nestedLeftCell.enableBorderSide(Rectangle.BOTTOM);
			nestedLeftCell.enableBorderSide(Rectangle.TOP);
			nestedLeftCell.enableBorderSide(Rectangle.RIGHT);
			nestedLeftCell.enableBorderSide(Rectangle.LEFT);
			nestedLeftCell.setFixedHeight(17f);

			if (payslip != null) {
				String fieldValue = field.getValue();
				if (!isFieldValueBlankOrZero(fieldValue)) {
					if (field.isDefault()) {
						nestedRightCell = new PdfPCell(new Phrase(
								field.getValue(), baseFont));
					} else {
						try {
							nestedRightCell = new PdfPCell(new Phrase(
									decimalFmt.format(Double.parseDouble(field
											.getValue())), baseFont));
						} catch (NumberFormatException e) {
							nestedRightCell = new PdfPCell(new Phrase(
									field.getValue(), baseFont));
						}
					}
					nestedRightCell.setPadding(2);
					nestedRightCell.setBorder(0);
					nestedRightCell.enableBorderSide(Rectangle.BOTTOM);
					nestedRightCell.enableBorderSide(Rectangle.TOP);
					nestedRightCell.enableBorderSide(Rectangle.RIGHT);
					nestedRightCell.setFixedHeight(17f);
					if (field.isHideLabel() != null && field.isHideLabel()) {
						nestedRightCell.setColspan(2);
						nestedRightCell.enableBorderSide(Rectangle.LEFT);
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
				nestedRightCell.enableBorderSide(Rectangle.BOTTOM);
				nestedRightCell.enableBorderSide(Rectangle.TOP);
				nestedRightCell.enableBorderSide(Rectangle.RIGHT);
				nestedRightCell.setFixedHeight(17f);
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
					nestedRightCell.enableBorderSide(Rectangle.LEFT);
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
			String tempLabelVal = "";

			if (StringUtils.isNotBlank(base64Str)) {
				if (field.isBold() == null && field.isUnderline() == null) {
					labelCell = new PdfPCell(new Phrase(base64Str,
							getBaseFontBold()));
				} else {
					labelCell = new PdfPCell(new Phrase(base64Str, baseFont));
				}
				tempLabelVal = base64Str;
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

				tempLabelVal = new String(Base64.decodeBase64(field.getLabel()
						.getBytes()));
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
			labelCell.setPaddingLeft(2);
			labelCell.setBorder(0);
			labelCell.enableBorderSide(Rectangle.BOTTOM);
			labelCell.enableBorderSide(Rectangle.TOP);
			labelCell.enableBorderSide(Rectangle.RIGHT);
			labelCell.enableBorderSide(Rectangle.LEFT);
			labelCell.setFixedHeight(17f);
			if (StringUtils.isNotBlank(tempLabelVal)) {
				if (field.isHideLabel() != null && field.isHideLabel()) {
					PdfPCell hideLabelCell = new PdfPCell(new Phrase("",
							baseFont));
					nestedRightColumnTable.addCell(hideLabelCell);
				} else {
					nestedRightColumnTable.addCell(labelCell);
				}
			} else {
				nestedRightColumnTable.addCell(labelCell);
			}

		}

		rightColumnCell.addElement(nestedRightColumnTable);
		return rightColumnCell;
	}

	/**
	 * Gets the left column.
	 * 
	 * @param field
	 *            the field
	 * @param payslip
	 *            the payslip
	 * @return the left column PdfCell
	 */
	private PdfPCell getLeftColumn(Field field, Payslip payslip) {
		DecimalFormat decimalFmt = new DecimalFormat("#,##0.00");
		leftColumnKeyCount++;
		PdfPCell leftColumnCell = new PdfPCell();
		leftColumnCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		leftColumnCell.setPadding(0);

		PdfPTable nestedLeftColumnTable = new PdfPTable(new float[] { 1.5f,
				0.5f });
		nestedLeftColumnTable.getTotalHeight();
		nestedLeftColumnTable.setWidthPercentage(99f);

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
			nestedLeftCell.setBorder(1);
			nestedLeftCell.enableBorderSide(Rectangle.BOTTOM);
			nestedLeftCell.enableBorderSide(Rectangle.TOP);
			nestedLeftCell.enableBorderSide(Rectangle.LEFT);
			nestedLeftCell.enableBorderSide(Rectangle.RIGHT);
			nestedLeftCell.setFixedHeight(17f);
			if (payslip != null) {
				String fieldValue = field.getValue();
				if (!isFieldValueBlankOrZero(fieldValue)) {
					if (field.isDefault()) {
						nestedRightCell = new PdfPCell(new Phrase(
								field.getValue(), baseFont));
					} else {
						// Get value entityId from Referenced variable as
						// temporarily as it is set in payslipDataUtils
						if (field.getReferenced().equals(
								PayAsiaConstants.PAYSLIP_FORM_ENTITY_ID)) {
							try {
								nestedRightCell = new PdfPCell(
										new Phrase(
												decimalFmt.format(Double
														.parseDouble(field
																.getValue())),
												baseFont));
							} catch (NumberFormatException e) {
								nestedRightCell = new PdfPCell(new Phrase(
										field.getValue(), baseFont));
							}
						} else {
							nestedRightCell = new PdfPCell(new Phrase(
									field.getValue(), baseFont));
						}
					}
					nestedRightCell.setPadding(2);
					nestedRightCell.setPaddingRight(5);
					nestedRightCell.setPadding(2);
					nestedRightCell.setBorder(1);
					setPayslipColDataAlignment(field, nestedLeftCell,
							nestedRightCell);
					nestedRightCell.enableBorderSide(Rectangle.BOTTOM);
					nestedRightCell.enableBorderSide(Rectangle.TOP);
					nestedRightCell.enableBorderSide(Rectangle.RIGHT);
					nestedRightCell.setFixedHeight(17f);
					if (field.isHideLabel() != null && field.isHideLabel()) {
						nestedRightCell.setColspan(2);
						nestedRightCell.enableBorderSide(Rectangle.LEFT);
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
				nestedRightCell.setPaddingRight(5);
				nestedRightCell.setPadding(2);
				nestedRightCell.setBorder(1);
				nestedRightCell.enableBorderSide(Rectangle.BOTTOM);
				nestedRightCell.enableBorderSide(Rectangle.TOP);
				nestedRightCell.enableBorderSide(Rectangle.RIGHT);
				nestedRightCell.setFixedHeight(17f);
				setPayslipColDataAlignment(field, nestedLeftCell,
						nestedRightCell);
			}

			if (payslip != null) {
			} else {
				if (field.isHideLabel() != null && field.isHideLabel()) {
					nestedRightCell.setColspan(2);
					nestedRightCell.enableBorderSide(Rectangle.LEFT);
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
			nestedLeftCell.setBorder(1);
			nestedLeftCell.enableBorderSide(Rectangle.BOTTOM);
			nestedLeftCell.enableBorderSide(Rectangle.TOP);
			nestedLeftCell.enableBorderSide(Rectangle.LEFT);
			nestedLeftCell.enableBorderSide(Rectangle.RIGHT);
			nestedLeftCell.setFixedHeight(17f);
			if (payslip != null) {
				String fieldValue = field.getValue();
				if (!isFieldValueBlankOrZero(fieldValue)) {
					if (field.isDefault()) {
						nestedRightCell = new PdfPCell(new Phrase(
								field.getValue(), baseFont));
					} else {
						try {
							nestedRightCell = new PdfPCell(new Phrase(
									decimalFmt.format(Double.parseDouble(field
											.getValue())), baseFont));
						} catch (NumberFormatException e) {
							nestedRightCell = new PdfPCell(new Phrase(
									field.getValue(), baseFont));
						}
					}
					nestedRightCell.setPadding(2);
					nestedRightCell.setPaddingRight(5);
					nestedRightCell.setPadding(2);
					nestedRightCell.setBorder(1);
					nestedRightCell.enableBorderSide(Rectangle.BOTTOM);
					nestedRightCell.enableBorderSide(Rectangle.TOP);
					nestedRightCell.enableBorderSide(Rectangle.RIGHT);
					nestedRightCell.setFixedHeight(17f);
					if (field.isHideLabel() != null && field.isHideLabel()) {
						nestedRightCell.setColspan(2);
						nestedRightCell.enableBorderSide(Rectangle.LEFT);
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
				nestedRightCell.setPaddingRight(5);
				nestedRightCell.setPadding(2);
				nestedRightCell.setBorder(1);
				nestedRightCell.enableBorderSide(Rectangle.BOTTOM);
				nestedRightCell.enableBorderSide(Rectangle.TOP);
				nestedRightCell.enableBorderSide(Rectangle.RIGHT);
				nestedRightCell.setFixedHeight(17f);
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
						nestedRightCell.enableBorderSide(Rectangle.LEFT);
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
			nestedLeftCell.setBorder(1);
			nestedLeftCell.enableBorderSide(Rectangle.BOTTOM);
			nestedLeftCell.enableBorderSide(Rectangle.TOP);
			nestedLeftCell.enableBorderSide(Rectangle.LEFT);
			nestedLeftCell.enableBorderSide(Rectangle.RIGHT);
			nestedLeftCell.setFixedHeight(17f);
			if (payslip != null) {
				String fieldValue = field.getValue();
				if (!isFieldValueBlankOrZero(fieldValue)) {
					if (field.isDefault()) {
						nestedRightCell = new PdfPCell(new Phrase(
								field.getValue(), baseFont));
					} else {
						try {
							nestedRightCell = new PdfPCell(new Phrase(
									decimalFmt.format(Double.parseDouble(field
											.getValue())), baseFont));
						} catch (NumberFormatException e) {
							nestedRightCell = new PdfPCell(new Phrase(
									field.getValue(), baseFont));
						}
					}
					nestedRightCell.setPadding(2);
					nestedRightCell.setPaddingRight(5);
					nestedRightCell.setPadding(2);
					nestedRightCell.setBorder(1);
					nestedRightCell.enableBorderSide(Rectangle.BOTTOM);
					nestedRightCell.enableBorderSide(Rectangle.TOP);
					nestedRightCell.enableBorderSide(Rectangle.RIGHT);
					nestedRightCell.setFixedHeight(17f);
					if (field.isHideLabel() != null && field.isHideLabel()) {
						nestedRightCell.setColspan(2);
						nestedRightCell.enableBorderSide(Rectangle.LEFT);
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
				nestedRightCell.setPaddingRight(5);
				nestedRightCell.setPadding(2);
				nestedRightCell.setBorder(1);
				nestedRightCell.enableBorderSide(Rectangle.BOTTOM);
				nestedRightCell.enableBorderSide(Rectangle.TOP);
				nestedRightCell.enableBorderSide(Rectangle.RIGHT);
				nestedRightCell.setFixedHeight(17f);
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
					nestedRightCell.enableBorderSide(Rectangle.LEFT);
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
			nestedLeftCell.setBorder(1);
			nestedLeftCell.enableBorderSide(Rectangle.BOTTOM);
			nestedLeftCell.enableBorderSide(Rectangle.TOP);
			nestedLeftCell.enableBorderSide(Rectangle.LEFT);
			nestedLeftCell.enableBorderSide(Rectangle.RIGHT);
			nestedLeftCell.setFixedHeight(17f);

			if (payslip != null) {
				String fieldValue = field.getValue();
				if (!isFieldValueBlankOrZero(fieldValue)) {
					if (field.isDefault()) {
						nestedRightCell = new PdfPCell(new Phrase(
								field.getValue(), baseFont));
					} else {
						try {
							nestedRightCell = new PdfPCell(new Phrase(
									decimalFmt.format(Double.parseDouble(field
											.getValue())), baseFont));
						} catch (NumberFormatException e) {
							nestedRightCell = new PdfPCell(new Phrase(
									field.getValue(), baseFont));
						}
					}
					nestedRightCell.setPadding(2);
					nestedRightCell.setPaddingRight(5);
					nestedRightCell.setPadding(2);
					nestedRightCell.setBorder(1);
					nestedRightCell.enableBorderSide(Rectangle.BOTTOM);
					nestedRightCell.enableBorderSide(Rectangle.TOP);
					nestedRightCell.enableBorderSide(Rectangle.RIGHT);
					nestedRightCell.setFixedHeight(17f);
					if (field.isHideLabel() != null && field.isHideLabel()) {
						nestedRightCell.setColspan(2);
						nestedRightCell.enableBorderSide(Rectangle.LEFT);
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
				nestedRightCell.setPaddingRight(5);
				nestedRightCell.setPadding(2);
				nestedRightCell.setBorder(1);
				nestedRightCell.enableBorderSide(Rectangle.BOTTOM);
				nestedRightCell.enableBorderSide(Rectangle.TOP);
				nestedRightCell.enableBorderSide(Rectangle.RIGHT);
				nestedRightCell.setFixedHeight(17f);
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
					nestedRightCell.enableBorderSide(Rectangle.LEFT);
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
			String tempLabelVal = "";

			if (StringUtils.isNotBlank(base64Str)) {
				if (field.isBold() == null && field.isUnderline() == null) {
					labelCell = new PdfPCell(new Phrase(base64Str,
							getBaseFontBold()));
				} else {
					labelCell = new PdfPCell(new Phrase(base64Str, baseFont));
				}
				tempLabelVal = base64Str;
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

				tempLabelVal = new String(Base64.decodeBase64(field.getLabel()
						.getBytes()));
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
			labelCell.setBorder(1);
			labelCell.enableBorderSide(Rectangle.BOTTOM);
			labelCell.enableBorderSide(Rectangle.TOP);
			labelCell.enableBorderSide(Rectangle.LEFT);
			labelCell.enableBorderSide(Rectangle.RIGHT);
			labelCell.setFixedHeight(17f);

			if (StringUtils.isNotBlank(tempLabelVal)) {
				if (field.isHideLabel() != null && field.isHideLabel()) {
					PdfPCell hideLabelCell = new PdfPCell(new Phrase("",
							getBaseFontNormal()));
					nestedLeftColumnTable.addCell(hideLabelCell);
				} else {
					nestedLeftColumnTable.addCell(labelCell);
				}

			} else {
				nestedLeftColumnTable.addCell(labelCell);
			}

		}

		leftColumnCell.addElement(nestedLeftColumnTable);
		return leftColumnCell;
	}

	/**
	 * Checks if is field value blank or zero.
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
		Font unicodeFont = new Font(bf, 9, Font.NORMAL, BaseColor.BLACK);
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
		Font unicodeFont = new Font(bf, 9, Font.BOLD, BaseColor.BLACK);
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
		Font unicodeFont = new Font(bf, 9, Font.BOLD | Font.UNDERLINE,
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
		Font unicodeFont = new Font(bf, 9, Font.UNDERLINE, BaseColor.BLACK);
		return unicodeFont;

	}

	private PdfPTable getPayslipOtherSectionRowsTable(
			List<PayslipRowDTO> payslipRowDTOList, Payslip payslip) {

		PdfPTable otherSectionRowsTable = new PdfPTable(new float[] { 1, 1 });
		otherSectionRowsTable.getTotalHeight();

		otherSectionRowsTable.setSpacingAfter(20f);

		for (PayslipRowDTO payslipRowDTO : payslipRowDTOList) {

			if (payslipRowDTO.getColumnField1() != null) {
				if (payslipRowDTO.getColumnField1().isColspan() != null) {
					if (!payslipRowDTO.getColumnField1().isColspan()) {
						PdfPCell leftCell = getLeftColumn(
								payslipRowDTO.getColumnField1(), payslip);
						leftCell.setBorder(0);
						PdfPCell rightCell = null;
						if (payslipRowDTO.getColumnField2() != null) {
							rightCell = getRightColumn(
									payslipRowDTO.getColumnField2(), payslip);
						} else {
							rightCell = getEmptyCell();
						}
						rightCell.setBorder(0);
						if (payslipRowDTO.getLeftColSize() != 0) {
							otherSectionRowsTable.addCell(leftCell);
						} else {
							otherSectionRowsTable
									.addCell(getEmptyCellWOBorder());
						}

						if (payslipRowDTO.getRightColSize() != 0) {
							otherSectionRowsTable.addCell(rightCell);
						} else {
							otherSectionRowsTable
									.addCell(getEmptyCellWOBorder());
						}

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
					PdfPCell leftCell = getLeftColumn(
							payslipRowDTO.getColumnField1(), payslip);
					leftCell.setBorder(0);
					PdfPCell rightCell = null;
					if (payslipRowDTO.getColumnField2() != null) {
						rightCell = getRightColumn(
								payslipRowDTO.getColumnField2(), payslip);
					} else {
						rightCell = getEmptyCell();
					}
					rightCell.setBorder(0);
					if (payslipRowDTO.getLeftColSize() != 0) {
						otherSectionRowsTable.addCell(leftCell);
					} else {
						otherSectionRowsTable.addCell(getEmptyCellWOBorder());
					}

					if (payslipRowDTO.getRightColSize() != 0) {
						otherSectionRowsTable.addCell(rightCell);
					} else {
						otherSectionRowsTable.addCell(getEmptyCellWOBorder());
					}

				}

			} else {
				PdfPCell leftCell = getEmptyCell();
				leftCell.setPadding(0);
				PdfPCell rightCell = null;
				if (payslipRowDTO.getColumnField2() != null) {
					rightCell = getRightColumn(payslipRowDTO.getColumnField2(),
							payslip);
					rightCell.setBorder(0);
				} else {
					rightCell = getEmptyCell();
				}
				leftCell.setBorder(0);
				if (payslipRowDTO.getLeftColSize() != 0) {
					otherSectionRowsTable.addCell(leftCell);
				} else {
					otherSectionRowsTable.addCell(getEmptyCellWOBorder());
				}

				if (payslipRowDTO.getRightColSize() != 0) {
					otherSectionRowsTable.addCell(rightCell);
				} else {
					otherSectionRowsTable.addCell(getEmptyCellWOBorder());
				}
			}

		}

		return otherSectionRowsTable;
	}

	private PdfPCell getEmptyCell() {
		PdfPCell leftColumnCell = new PdfPCell();
		leftColumnCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		leftColumnCell.setPadding(0);

		PdfPTable nestedLeftColumnTable = new PdfPTable(new float[] { 1.5f,
				0.5f });
		nestedLeftColumnTable.setWidthPercentage(99f);
		nestedLeftColumnTable.getTotalHeight();

		PdfPCell nestedLeftCell = new PdfPCell(new Phrase(" ",
				getBaseFontBold()));
		nestedLeftCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		nestedLeftCell.setPadding(2);
		nestedLeftCell.setBorder(0);
		nestedLeftCell.setFixedHeight(17);
		nestedLeftCell.enableBorderSide(Rectangle.BOTTOM);
		nestedLeftCell.enableBorderSide(Rectangle.TOP);
		nestedLeftCell.enableBorderSide(Rectangle.LEFT);
		nestedLeftCell.enableBorderSide(Rectangle.RIGHT);

		PdfPCell nestedRightCell = new PdfPCell(new Phrase(" ",
				getBaseFontBold()));
		nestedRightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

		nestedRightCell.setPadding(2);
		nestedRightCell.setBorder(0);
		nestedRightCell.setFixedHeight(17);
		nestedRightCell.enableBorderSide(Rectangle.BOTTOM);
		nestedRightCell.enableBorderSide(Rectangle.TOP);
		nestedRightCell.enableBorderSide(Rectangle.RIGHT);

		nestedLeftColumnTable.addCell(nestedLeftCell);
		nestedLeftColumnTable.addCell(nestedRightCell);
		leftColumnCell.addElement(nestedLeftColumnTable);
		return leftColumnCell;
	}

	private PdfPCell getEmptyCellWOBorder() {
		PdfPCell leftColumnCell = new PdfPCell();
		leftColumnCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		leftColumnCell.setPadding(0);
		leftColumnCell.setBorder(0);

		PdfPTable nestedLeftColumnTable = new PdfPTable(new float[] { 1.5f,
				0.5f });
		nestedLeftColumnTable.setWidthPercentage(99f);
		nestedLeftColumnTable.getTotalHeight();

		PdfPCell nestedLeftCell = new PdfPCell(new Phrase(" ",
				getBaseFontBold()));
		nestedLeftCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		nestedLeftCell.setPadding(2);
		nestedLeftCell.setBorder(0);
		nestedLeftCell.setFixedHeight(17);

		PdfPCell nestedRightCell = new PdfPCell(new Phrase(" ",
				getBaseFontBold()));
		nestedRightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

		nestedRightCell.setPadding(2);
		nestedRightCell.setBorder(0);
		nestedRightCell.setFixedHeight(17);

		nestedLeftColumnTable.addCell(nestedLeftCell);
		nestedLeftColumnTable.addCell(nestedRightCell);
		leftColumnCell.addElement(nestedLeftColumnTable);
		return leftColumnCell;
	}

	private PdfPCell getColSpanTwoCell(Field field, Payslip payslip) {
		DecimalFormat decimalFmt = new DecimalFormat("#,##0.00");
		PdfPCell columnCell = new PdfPCell();
		columnCell.setColspan(2);
		columnCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		columnCell.setPadding(0);

		PdfPTable nestedColumnTable = null;

		if (field.getType().equalsIgnoreCase(PayAsiaConstants.FIELD_TYPE_TEXT)) {
			nestedColumnTable = new PdfPTable(new float[] { 1, 1, 1, 1 });
			nestedColumnTable.setWidthPercentage(99.5f);
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
			nestedLeftCell.setBorder(1);
			nestedLeftCell.enableBorderSide(Rectangle.BOTTOM);
			nestedLeftCell.enableBorderSide(Rectangle.TOP);
			nestedLeftCell.enableBorderSide(Rectangle.LEFT);
			nestedLeftCell.enableBorderSide(Rectangle.RIGHT);
			nestedLeftCell.setFixedHeight(17f);
			if (payslip != null) {
				String fieldValue = field.getValue();
				if (!isFieldValueBlankOrZero(fieldValue)) {
					if (field.isDefault()) {
						nestedRightCell = new PdfPCell(new Phrase(
								field.getValue(), baseFont));
					} else {
						try {
							nestedRightCell = new PdfPCell(new Phrase(
									decimalFmt.format(Double.parseDouble(field
											.getValue())), baseFont));
						} catch (NumberFormatException e) {
							nestedRightCell = new PdfPCell(new Phrase(
									field.getValue(), baseFont));
						}
					}
					nestedRightCell.setPadding(2);
					nestedRightCell.setPaddingRight(5);
					nestedRightCell.setPadding(2);
					nestedRightCell.setBorder(1);
					setPayslipColDataAlignment(field, nestedLeftCell,
							nestedRightCell);
					nestedRightCell.enableBorderSide(Rectangle.BOTTOM);
					nestedRightCell.enableBorderSide(Rectangle.TOP);
					nestedRightCell.enableBorderSide(Rectangle.RIGHT);
					nestedRightCell.setFixedHeight(17f);
					nestedRightCell.setColspan(3);
					if (field.isHideLabel() != null && field.isHideLabel()) {
						nestedRightCell.setColspan(4);
						nestedRightCell.enableBorderSide(Rectangle.LEFT);
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
				nestedRightCell.setPaddingRight(5);
				nestedRightCell.setPadding(2);
				nestedRightCell.setBorder(1);
				nestedRightCell.setColspan(3);
				nestedRightCell.enableBorderSide(Rectangle.BOTTOM);
				nestedRightCell.enableBorderSide(Rectangle.TOP);
				nestedRightCell.enableBorderSide(Rectangle.RIGHT);
				nestedRightCell.setFixedHeight(17f);
				setPayslipColDataAlignment(field, nestedLeftCell,
						nestedRightCell);
			}

			if (payslip != null) {
			} else {
				if (field.isHideLabel() != null && field.isHideLabel()) {
					nestedRightCell.setColspan(4);
					nestedRightCell.enableBorderSide(Rectangle.LEFT);
					nestedColumnTable.addCell(nestedRightCell);
				} else {
					nestedColumnTable.addCell(nestedLeftCell);
					nestedColumnTable.addCell(nestedRightCell);
				}
			}

		}
		if (field.getType().equalsIgnoreCase(PayAsiaConstants.FIELD_TYPE_LABEL)) {
			nestedColumnTable = new PdfPTable(new float[] { 1 });
			nestedColumnTable.setWidthPercentage(99.5f);
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

			if ("right".equalsIgnoreCase(field.getAlign())) {
				nestedLeftCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			} else if ("center".equalsIgnoreCase(field.getAlign())) {
				nestedLeftCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			} else {
				nestedLeftCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			}
			nestedLeftCell.setPadding(2);
			nestedLeftCell.setBorder(0);
			nestedLeftCell.enableBorderSide(Rectangle.BOTTOM);
			nestedLeftCell.enableBorderSide(Rectangle.TOP);
			nestedLeftCell.enableBorderSide(Rectangle.LEFT);
			nestedLeftCell.enableBorderSide(Rectangle.RIGHT);
			nestedLeftCell.setFixedHeight(17);

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

}
