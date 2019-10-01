/**
 * @author vivekjain
 *
 */
package com.payasia.logic.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.persistence.Tuple;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.payasia.common.dto.ClaimCustomFieldDTO;
import com.payasia.common.dto.DataImportKeyValueDTO;
import com.payasia.common.dto.EmployeeShortListDTO;
import com.payasia.common.form.ClaimPreferenceForm;
import com.payasia.common.form.ExcelExportFiltersForm;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.PDFUtils;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.PayAsiaPDFConstants;
import com.payasia.dao.ClaimApplicationDAO;
import com.payasia.dao.ClaimApplicationItemDAO;
import com.payasia.dao.ClaimPreferenceDAO;
import com.payasia.dao.DataDictionaryDAO;
import com.payasia.dao.EmployeeClaimReviewerDAO;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.bean.ClaimApplication;
import com.payasia.dao.bean.ClaimApplicationItem;
import com.payasia.dao.bean.ClaimApplicationItemAttachment;
import com.payasia.dao.bean.ClaimApplicationItemCustomField;
import com.payasia.dao.bean.ClaimApplicationItemLundinDetail;
import com.payasia.dao.bean.ClaimApplicationItem_;
import com.payasia.dao.bean.ClaimApplicationReviewer;
import com.payasia.dao.bean.ClaimApplicationWorkflow;
import com.payasia.dao.bean.ClaimCategoryMaster_;
import com.payasia.dao.bean.ClaimPreference;
import com.payasia.dao.bean.ClaimTemplateItemClaimType;
import com.payasia.dao.bean.DataDictionary;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeClaimReviewer;
import com.payasia.logic.BaseLogic;
import com.payasia.logic.ClaimFormPrintPDFLogic;
import com.payasia.logic.GeneralLogic;

@Component
public class ClaimFormPrintPDFLogicImpl extends BaseLogic implements ClaimFormPrintPDFLogic {
	/** The total. */

	@Resource
	ClaimApplicationDAO claimApplicationDAO;
	@Resource
	ClaimApplicationItemDAO claimApplicationItemDAO;
	@Resource
	EmployeeClaimReviewerDAO employeeClaimReviewerDAO;
	@Resource
	EmployeeDAO employeeDAO;
	@Resource
	DataDictionaryDAO dataDictionaryDAO;
	@Resource
	GeneralLogic generalLogic;
	@Resource
	ClaimPreferenceDAO claimPreferenceDAO;

	private static final Logger LOGGER = Logger.getLogger(ClaimFormPrintPDFLogicImpl.class);

	@Override
	public PdfPTable createClaimReportPdf(Document document, PdfWriter writer, int currentPageNumber, Long companyId,
			Long claimApplicationId, boolean hasLundinTimesheetModule) {
		PdfPTable mainSectionTable = new PdfPTable(new float[] { 1 });
		try {
			mainSectionTable.setWidthPercentage(95f);
			mainSectionTable.setHorizontalAlignment(Element.ALIGN_CENTER);

			PdfPCell mainCell = new PdfPCell();
			mainCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			mainCell.setPadding(0);
			mainCell.setPaddingTop(5f);
			mainCell.setBorder(0);
			PDFUtils.setDefaultCellLayout(mainCell);
			ClaimApplication claimApplicationVO = claimApplicationDAO.findByID(claimApplicationId);

			PdfPTable headerSectionTable = getHeaderSection(document, claimApplicationVO);
			headerSectionTable.setWidthPercentage(110f);

			PdfPTable pdfTableSummaryHeaderTable = getPdfTableHeader(document, "Summary");
			pdfTableSummaryHeaderTable.setWidthPercentage(110f);
			PdfPTable summarySectionTable = getSummarySection(document, companyId, claimApplicationVO);

			PdfPTable workflowSectionTable = null;
			if (!claimApplicationVO.getClaimStatusMaster().getClaimStatusName()
					.equalsIgnoreCase(PayAsiaConstants.CLAIM_LIST_STATUS_DRAFT)) {
				PdfPTable pdfTableWorkflowHeaderTable = getPdfTableHeader(document, "Workflow History");
				pdfTableWorkflowHeaderTable.setWidthPercentage(110f);
				workflowSectionTable = getWorkFlowHistorySection(document, companyId, claimApplicationVO);
			}
			if (claimApplicationVO.getClaimStatusMaster().getClaimStatusName()
					.equalsIgnoreCase(PayAsiaConstants.CLAIM_LIST_STATUS_DRAFT)) {
				PdfPTable pdfTableWorkflowHeaderTable = getPdfTableHeader(document, "Workflow History");
				pdfTableWorkflowHeaderTable.setWidthPercentage(110f);
				workflowSectionTable = getWorkFlowHistorySectionDraft(document, companyId, claimApplicationVO);
			}

			PdfPTable pdfTableBankDetailHeaderTable = getPdfTableHeader(document, "Bank Details");
			pdfTableBankDetailHeaderTable.setWidthPercentage(110f);

			ClaimPreferenceForm claimPreferenceForm = setEmpBankDetail(claimApplicationVO.getEmployee().getEmployeeId(),
					claimApplicationVO.getEmployee().getEmployeeNumber(), companyId,
					claimApplicationVO.getCompany().getDateFormat());
			PdfPTable bankDetailTable = getBankDetailsSection(document, claimPreferenceForm);

			PdfPTable pdfTableCategoryWiseHeaderTable = getPdfTableHeader(document, "Category Wise Total");
			pdfTableCategoryWiseHeaderTable.setWidthPercentage(110f);
			PdfPTable categoryWiseTotalSectionTable = getCategoryWiseTotalSection(document, claimApplicationVO);

			PdfPTable pdfTableItemsSectionHeaderTable = getPdfTableHeader(document, "Items");
			pdfTableItemsSectionHeaderTable.setWidthPercentage(110f);
			PdfPTable itemsSectiontable = getItemsSection(document, writer, claimApplicationVO,
					hasLundinTimesheetModule);

			mainCell.addElement(headerSectionTable);
			mainCell.addElement(summarySectionTable);
			// if
			// (!claimApplicationVO.getClaimStatusMaster().getClaimStatusName()
			// .equalsIgnoreCase(PayAsiaConstants.CLAIM_LIST_STATUS_DRAFT)) {
			mainCell.addElement(workflowSectionTable);
			// }

			ClaimPreference claimPreferenceVO = claimPreferenceDAO.findByCompanyId(companyId);
			if (claimPreferenceVO != null) {
				if (claimPreferenceVO.getBankAccountNameDictionary() != null
						&& claimPreferenceVO.getBankAccountNumDictionary() != null
						&& claimPreferenceVO.getBankNameDictionary() != null) {
					mainCell.addElement(bankDetailTable);
				}
			}

			mainCell.addElement(categoryWiseTotalSectionTable);
			mainCell.addElement(pdfTableItemsSectionHeaderTable);
			mainCell.addElement(itemsSectiontable);

			mainSectionTable.addCell(mainCell);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		return mainSectionTable;
	}

	@Override
	public List<Object[]> getEmpBankDetailFieldsValueList(Long companyId, String dateFormat, Long employeeId) {
		List<Long> formIds = new ArrayList<Long>();
		List<DataImportKeyValueDTO> tableElements = new ArrayList<DataImportKeyValueDTO>();
		Map<String, DataImportKeyValueDTO> tableRecordInfoFrom = new HashMap<String, DataImportKeyValueDTO>();
		Map<String, DataImportKeyValueDTO> colMap = new LinkedHashMap<String, DataImportKeyValueDTO>();

		List<ExcelExportFiltersForm> finalFilterList = new ArrayList<ExcelExportFiltersForm>();

		// Get Static Dictionary
		DataDictionary dataDictionaryEmp = dataDictionaryDAO.findByDictionaryNameGroup(null, 1l, "Employee Number",
				null, null);
		generalLogic.setStaticDictionary(colMap, dataDictionaryEmp.getLabel() + dataDictionaryEmp.getDataDictionaryId(),
				dataDictionaryEmp);

		ClaimPreference claimPreferenceVO = claimPreferenceDAO.findByCompanyId(companyId);
		if (claimPreferenceVO != null) {
			if (claimPreferenceVO.getBankAccountNameDictionary() == null
					&& claimPreferenceVO.getBankAccountNumDictionary() == null
					&& claimPreferenceVO.getBankNameDictionary() == null) {
				List<Object[]> objectList = new ArrayList<Object[]>();
				return objectList;
			}

			// Bank Account Name Field
			if (claimPreferenceVO.getBankAccountNameDictionary() != null) {
				generalLogic.getColMap(claimPreferenceVO.getBankAccountNameDictionary(), colMap);
				addDynamicTableKeyMap(claimPreferenceVO.getBankAccountNameDictionary(), tableRecordInfoFrom, colMap);
				formIds.add(claimPreferenceVO.getBankAccountNameDictionary().getFormID());
			}
			// Bank Account Num Field
			if (claimPreferenceVO.getBankAccountNumDictionary() != null) {
				generalLogic.getColMap(claimPreferenceVO.getBankAccountNumDictionary(), colMap);
				addDynamicTableKeyMap(claimPreferenceVO.getBankAccountNumDictionary(), tableRecordInfoFrom, colMap);
				if (!formIds.contains(claimPreferenceVO.getBankAccountNumDictionary().getFormID())) {
					formIds.add(claimPreferenceVO.getBankAccountNumDictionary().getFormID());
				}
			}
			// Bank Name Field
			if (claimPreferenceVO.getBankNameDictionary() != null) {
				generalLogic.getColMap(claimPreferenceVO.getBankNameDictionary(), colMap);
				addDynamicTableKeyMap(claimPreferenceVO.getBankNameDictionary(), tableRecordInfoFrom, colMap);
				if (!formIds.contains(claimPreferenceVO.getBankNameDictionary().getFormID())) {
					formIds.add(claimPreferenceVO.getBankNameDictionary().getFormID());
				}
			}
		}

		EmployeeShortListDTO employeeShortListDTO = generalLogic.getShortListEmployeeIds(employeeId, companyId);

		// Show by effective date table data if one custom table record is
		// required, here it is true i.e. show only by effective date table data
		boolean showByEffectiveDateTableData = true;

		List<Object[]> objectList = employeeDAO.findByCondition(colMap, formIds, companyId, finalFilterList, dateFormat,
				tableRecordInfoFrom, tableElements, employeeShortListDTO, showByEffectiveDateTableData);
		return objectList;
	}

	private ClaimPreferenceForm setEmpBankDetail(long employeeId, String employeeNumber, long companyId,
			String companyDateFormat) {
		List<Object[]> empDetailObjectList = getEmpBankDetailFieldsValueList(companyId, companyDateFormat, employeeId);
		ClaimPreferenceForm claimPreferenceForm = new ClaimPreferenceForm();
		for (Object[] empDetailObj : empDetailObjectList) {
			if (empDetailObj != null && empDetailObj[0] != null && empDetailObj[0].equals(employeeNumber)) {
				if (StringUtils.isNotBlank(String.valueOf(empDetailObj[1]))
						&& !String.valueOf(empDetailObj[1]).equalsIgnoreCase("null")) {
					claimPreferenceForm.setBankAccountName(String.valueOf(empDetailObj[1]));
				} else {
					claimPreferenceForm.setBankAccountName("");
				}

				if (StringUtils.isNotBlank(String.valueOf(empDetailObj[2]))
						&& !String.valueOf(empDetailObj[2]).equalsIgnoreCase("null")) {
					claimPreferenceForm.setBankAccountNum(String.valueOf(empDetailObj[2]));
				} else {
					claimPreferenceForm.setBankAccountNum("");
				}

				if (StringUtils.isNotBlank(String.valueOf(empDetailObj[3]))
						&& !String.valueOf(empDetailObj[3]).equalsIgnoreCase("null")) {
					claimPreferenceForm.setBankName(String.valueOf(empDetailObj[3]));
				} else {
					claimPreferenceForm.setBankName("");
				}
			}
		}
		return claimPreferenceForm;
	}

	private PdfPTable getBankDetailsSection(Document document, ClaimPreferenceForm claimPreferenceForm) {
		PdfPTable categoryWiseTotalSectionTable = new PdfPTable(new float[] { 1f, 3f });
		categoryWiseTotalSectionTable.setWidthPercentage(110f);
		categoryWiseTotalSectionTable.getTotalWidth();
		categoryWiseTotalSectionTable.setSpacingBefore(5f);

		PdfPCell categoryWiseTableHeaderCell = new PdfPCell(new Phrase("Bank Detail", getBaseFont10Bold()));
		categoryWiseTableHeaderCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		categoryWiseTableHeaderCell.setBackgroundColor(PayAsiaPDFConstants.TABLE_STANDARD_HEADER_GRAY_COLOR);
		categoryWiseTableHeaderCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);
		categoryWiseTableHeaderCell.setColspan(2);
		categoryWiseTotalSectionTable.addCell(categoryWiseTableHeaderCell);

		PdfPCell bankNameCell = new PdfPCell(new Phrase("Bank Name", getBaseFontNormal()));
		bankNameCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		bankNameCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);
		PDFUtils.setDefaultCellLayout(bankNameCell);
		bankNameCell.enableBorderSide(Rectangle.LEFT);

		PdfPCell bankNameValCell = new PdfPCell(new Phrase(claimPreferenceForm.getBankName(), getBaseFontNormal()));
		bankNameValCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		bankNameValCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);
		PDFUtils.setDefaultCellLayout(bankNameValCell);
		bankNameValCell.enableBorderSide(Rectangle.RIGHT);

		categoryWiseTotalSectionTable.addCell(bankNameCell);
		categoryWiseTotalSectionTable.addCell(bankNameValCell);

		// Bank Account Name
		PdfPCell bankAccountNameCell = new PdfPCell(new Phrase("Bank Account Number", getBaseFontNormal()));
		bankAccountNameCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		bankAccountNameCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);
		PDFUtils.setDefaultCellLayout(bankAccountNameCell);
		bankAccountNameCell.enableBorderSide(Rectangle.LEFT);

		PdfPCell bankAccountNameValCell = new PdfPCell(
				new Phrase(claimPreferenceForm.getBankAccountNum(), getBaseFontNormal()));
		bankAccountNameValCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		bankAccountNameValCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);
		PDFUtils.setDefaultCellLayout(bankAccountNameValCell);
		bankAccountNameValCell.enableBorderSide(Rectangle.RIGHT);

		categoryWiseTotalSectionTable.addCell(bankAccountNameCell);
		categoryWiseTotalSectionTable.addCell(bankAccountNameValCell);

		// Bank Account Number
		PdfPCell bankAccountNumCell = new PdfPCell(new Phrase("Bank Account Name", getBaseFontNormal()));
		bankAccountNumCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		bankAccountNumCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);
		PDFUtils.setDefaultCellLayout(bankAccountNumCell);
		bankAccountNumCell.enableBorderSide(Rectangle.LEFT);
		bankAccountNumCell.enableBorderSide(Rectangle.BOTTOM);

		PdfPCell bankAccountNumValCell = new PdfPCell(
				new Phrase(claimPreferenceForm.getBankAccountName(), getBaseFontNormal()));
		bankAccountNumValCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		bankAccountNumValCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);
		PDFUtils.setDefaultCellLayout(bankAccountNumValCell);
		bankAccountNumValCell.enableBorderSide(Rectangle.RIGHT);
		bankAccountNumValCell.enableBorderSide(Rectangle.BOTTOM);

		categoryWiseTotalSectionTable.addCell(bankAccountNumCell);
		categoryWiseTotalSectionTable.addCell(bankAccountNumValCell);

		return categoryWiseTotalSectionTable;
	}

	private void addDynamicTableKeyMap(DataDictionary dataDictionary,
			Map<String, DataImportKeyValueDTO> tableRecordInfoFrom, Map<String, DataImportKeyValueDTO> colMap) {
		if (colMap.get(dataDictionary.getLabel() + dataDictionary.getDataDictionaryId()) != null && colMap
				.get(dataDictionary.getLabel() + dataDictionary.getDataDictionaryId()).getTablePosition() != null) {
			String tableKey;
			tableKey = colMap.get(dataDictionary.getLabel() + dataDictionary.getDataDictionaryId()).getFormId()
					+ colMap.get(dataDictionary.getLabel() + dataDictionary.getDataDictionaryId()).getTablePosition();
			tableRecordInfoFrom.put(tableKey,
					colMap.get(dataDictionary.getLabel() + dataDictionary.getDataDictionaryId()));
		}
	}

	private PdfPTable getPdfTableHeader(Document document, String tableHeaderName) {
		PdfPTable pdfTableHeaderTable = new PdfPTable(new float[] { 1f });
		pdfTableHeaderTable.setWidthPercentage(110f);
		pdfTableHeaderTable.getTotalWidth();
		pdfTableHeaderTable.setSpacingBefore(5f);

		PdfPCell headerCell = new PdfPCell(new Phrase(tableHeaderName, getBaseFont10Bold()));
		headerCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		headerCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);
		headerCell.setPadding(2);
		headerCell.setBackgroundColor(PayAsiaPDFConstants.TABLE_STANDARD_HEADER_GRAY_COLOR);

		pdfTableHeaderTable.addCell(headerCell);
		return pdfTableHeaderTable;
	}

	private PdfPTable getHeaderSection(Document document, ClaimApplication claimApplicationVO) {
		PdfPTable headerSectionTable = new PdfPTable(new float[] { 1f });
		headerSectionTable.setWidthPercentage(110f);
		headerSectionTable.setSpacingAfter(5f);

		PdfPCell headerCell = new PdfPCell(
				new Phrase(claimApplicationVO.getCompany().getCompanyName(), getBaseFont12Bold()));
		headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		headerCell.setBorder(0);

		headerSectionTable.addCell(headerCell);
		return headerSectionTable;
	}

	private PdfPTable getSummarySection(Document document, Long companyId, ClaimApplication claimApplicationVO) {
		PdfPTable summarySectionTable = new PdfPTable(new float[] { 1f });
		summarySectionTable.setWidthPercentage(110f);
		summarySectionTable.getTotalWidth();
		summarySectionTable.setSpacingBefore(5f);

		PdfPCell SummaryTableHeaderCell = new PdfPCell(new Phrase("Summary", getBaseFont10Bold()));
		SummaryTableHeaderCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		SummaryTableHeaderCell.setBackgroundColor(PayAsiaPDFConstants.TABLE_STANDARD_HEADER_GRAY_COLOR);
		SummaryTableHeaderCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);

		PdfPCell pdfCell1 = createSummarySectionTableData("Claim Number:",
				String.valueOf(claimApplicationVO.getClaimNumber()), "", "");
		PDFUtils.setDefaultCellLayout(pdfCell1);
		pdfCell1.enableBorderSide(Rectangle.LEFT);
		pdfCell1.enableBorderSide(Rectangle.RIGHT);

		String employeeName = claimApplicationVO.getEmployee().getFirstName();
		if (StringUtils.isNotBlank(claimApplicationVO.getEmployee().getLastName())) {
			employeeName += " " + claimApplicationVO.getEmployee().getLastName();
		}

		PdfPCell pdfCell2 = createSummarySectionTableData("Employee Name:", employeeName, "Employee No:",
				claimApplicationVO.getEmployee().getEmployeeNumber());
		PDFUtils.setDefaultCellLayout(pdfCell2);
		pdfCell2.enableBorderSide(Rectangle.LEFT);
		pdfCell2.enableBorderSide(Rectangle.RIGHT);

		PdfPCell pdfCell3 = createSummarySectionTableData("Claim Form:",
				claimApplicationVO.getEmployeeClaimTemplate().getClaimTemplate().getTemplateName(), "Created Date:",
				DateUtils.timeStampToString(claimApplicationVO.getCreatedDate()));
		PDFUtils.setDefaultCellLayout(pdfCell3);
		pdfCell3.enableBorderSide(Rectangle.LEFT);
		pdfCell3.enableBorderSide(Rectangle.RIGHT);

		DecimalFormat df = new DecimalFormat("##.00");
		BigDecimal totalApplicableAmount = BigDecimal.ZERO;
		totalApplicableAmount = getTotalAmountApplicable(claimApplicationVO, totalApplicableAmount);

		PdfPCell pdfCell4 = createSummarySectionTableData("No of items claimed:",
				String.valueOf(claimApplicationVO.getTotalItems()), "Total Amount:", df.format(totalApplicableAmount));
		PDFUtils.setDefaultCellLayout(pdfCell4);
		pdfCell4.enableBorderSide(Rectangle.LEFT);
		pdfCell4.enableBorderSide(Rectangle.RIGHT);
		pdfCell4.enableBorderSide(Rectangle.BOTTOM);
		summarySectionTable.addCell(SummaryTableHeaderCell);
		summarySectionTable.addCell(pdfCell1);
		summarySectionTable.addCell(pdfCell2);
		summarySectionTable.addCell(pdfCell3);
		summarySectionTable.addCell(pdfCell4);
		return summarySectionTable;
	}

	private BigDecimal getTotalAmountApplicable(ClaimApplication claimApplication, BigDecimal totalApplicableAmount) {
		Set<ClaimApplicationItem> itemsList = claimApplication.getClaimApplicationItems();
		for (ClaimApplicationItem applicationItem : itemsList) {
			if (applicationItem.getEmployeeClaimTemplateItem().getClaimTemplateItem().getClaimTemplateItemClaimTypes()
					.isEmpty()) {
				continue;
			}
			ClaimTemplateItemClaimType claimTemplateItemClaimType = applicationItem.getEmployeeClaimTemplateItem()
					.getClaimTemplateItem().getClaimTemplateItemClaimTypes().iterator().next();
			if (claimTemplateItemClaimType.getClaimType().getCodeDesc()
					.equals(PayAsiaConstants.APP_CODE_CLAIM_TYPE_AMOUNT_BASED)) {
				if (claimTemplateItemClaimType.getReceiptAmtPercentApplicable() != null && claimTemplateItemClaimType
						.getReceiptAmtPercentApplicable() <= PayAsiaConstants.APP_CODE_CLAIM_TYPE_AMOUNT_BASED_AMOUNT_APPLICABLE_PERCENT) {

					Integer amtApplicablePercent = claimTemplateItemClaimType.getReceiptAmtPercentApplicable();
					if (amtApplicablePercent == null || amtApplicablePercent == 0) {
						amtApplicablePercent = PayAsiaConstants.APP_CODE_CLAIM_TYPE_AMOUNT_BASED_AMOUNT_APPLICABLE_PERCENT;
					}
					BigDecimal totalAmt = (applicationItem.getClaimAmount()
							.multiply(new BigDecimal(amtApplicablePercent))).divide(new BigDecimal(100));
					totalApplicableAmount = totalApplicableAmount.add(totalAmt);
				} else {
					totalApplicableAmount = totalApplicableAmount.add(applicationItem.getClaimAmount());
				}
			} else {
				totalApplicableAmount = totalApplicableAmount.add(applicationItem.getClaimAmount());
			}
		}
		return totalApplicableAmount;
	}

	private PdfPCell createSummarySectionTableData(String leftColKey, String leftColVal, String rightColKey,
			String rightColVal) {
		PdfPCell pdfPCell = new PdfPCell();

		PdfPTable summarySectionTable = new PdfPTable(new float[] { 1.5f, 1.5f, 1.5f, 1.5f });
		summarySectionTable.setWidthPercentage(100f);

		PdfPCell nestedLeftKeyCell = new PdfPCell(new Phrase(leftColKey, getBaseFontNormal()));
		nestedLeftKeyCell.setBorder(0);

		PdfPCell nestedLeftValueCell = new PdfPCell(new Phrase(leftColVal, getBaseFontNormal()));
		nestedLeftValueCell.setBorder(0);

		PdfPCell nestedRightKeyCell = new PdfPCell(new Phrase(rightColKey, getBaseFontNormal()));
		nestedRightKeyCell.setBorder(0);

		PdfPCell nestedRightValueCell = new PdfPCell(new Phrase(rightColVal, getBaseFontNormal()));
		nestedRightValueCell.setBorder(0);
		summarySectionTable.addCell(nestedLeftKeyCell);
		summarySectionTable.addCell(nestedLeftValueCell);
		summarySectionTable.addCell(nestedRightKeyCell);
		summarySectionTable.addCell(nestedRightValueCell);

		pdfPCell.addElement(summarySectionTable);
		return pdfPCell;
	}

	private String getEmployeeName(Employee employee) {
		String employeeName = employee.getFirstName();
		if (employee.getMiddleName() != null) {
			employeeName = employeeName + " " + employee.getMiddleName();
		}
		if (employee.getLastName() != null) {
			employeeName = employeeName + " " + employee.getLastName();
		}
		employeeName = employeeName + " (" + employee.getEmployeeNumber() + ")";
		return employeeName;
	}

	/**
	 * Comparator Class for Ordering ClaimApplicationReviewer List
	 */
	private class ClaimTypeComp implements Comparator<ClaimApplicationReviewer> {
		public int compare(ClaimApplicationReviewer templateField, ClaimApplicationReviewer compWithTemplateField) {
			if (templateField.getClaimApplicationReviewerId() > compWithTemplateField.getClaimApplicationReviewerId()) {
				return 1;
			} else if (templateField.getClaimApplicationReviewerId() < compWithTemplateField
					.getClaimApplicationReviewerId()) {
				return -1;
			}
			return 0;

		}

	}

	private PdfPTable getWorkFlowHistorySection(Document document, Long companyId,
			ClaimApplication claimApplicationVO) {
		PdfPTable workflowySectionTable = new PdfPTable(new float[] { 1f });
		workflowySectionTable.setWidthPercentage(110f);
		workflowySectionTable.getTotalWidth();
		workflowySectionTable.setSpacingBefore(5f);

		PdfPCell workflowyTableHeaderCell = new PdfPCell(new Phrase("WorkFlow History", getBaseFont10Bold()));
		workflowyTableHeaderCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		workflowyTableHeaderCell.setBackgroundColor(PayAsiaPDFConstants.TABLE_STANDARD_HEADER_GRAY_COLOR);
		workflowyTableHeaderCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);
		PdfPCell pdfCell1 = createWorkFlowSectionTableData("S.No", "WorkFlow Role", "Name", "Remarks", "Status",
				"Date");
		PDFUtils.setDefaultCellLayout(pdfCell1);
		pdfCell1.enableBorderSide(Rectangle.LEFT);
		pdfCell1.enableBorderSide(Rectangle.RIGHT);
		pdfCell1.setBackgroundColor(PayAsiaPDFConstants.TABLE_STANDARD_HEADER_GRAY_COLOR);

		String userStatus = "";
		if (claimApplicationVO.getClaimStatusMaster().getClaimStatusName()
				.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_WITHDRAWN)) {
			userStatus = claimApplicationVO.getClaimStatusMaster().getClaimStatusName();
		} else {
			userStatus = "Submitted";
		}

		Timestamp claimAppSubmittedDate = null;
		for (ClaimApplicationWorkflow claimApplicationWorkflow : claimApplicationVO.getClaimApplicationWorkflows()) {

			if (claimApplicationWorkflow.getEmployee().getEmployeeId() == claimApplicationVO.getEmployee()
					.getEmployeeId()) {
				claimAppSubmittedDate = claimApplicationWorkflow.getCreatedDate();
			}
		}

		String claimUserRemarks = "";
		if (StringUtils.isNotBlank(claimApplicationVO.getRemarks())) {
			try {
				claimUserRemarks = URLDecoder.decode(claimApplicationVO.getRemarks(), "UTF-8");
			} catch (UnsupportedEncodingException | IllegalArgumentException e) {
				claimUserRemarks = claimApplicationVO.getRemarks();
				LOGGER.error(e.getMessage(), e);
			}
		}

		PdfPCell userCell = createWorkFlowSectionTableData("1", "User",
				getEmployeeName(claimApplicationVO.getEmployee()), claimUserRemarks, userStatus,
				DateUtils.timeStampToString(claimAppSubmittedDate));
		PDFUtils.setDefaultCellLayout(userCell);
		userCell.enableBorderSide(Rectangle.LEFT);
		userCell.enableBorderSide(Rectangle.RIGHT);

		workflowySectionTable.addCell(workflowyTableHeaderCell);
		workflowySectionTable.addCell(pdfCell1);

		HashMap<Long, ClaimApplicationWorkflow> workFlow = new HashMap<>();
		for (ClaimApplicationWorkflow claimApplicationWorkflow : claimApplicationVO.getClaimApplicationWorkflows()) {

			workFlow.put(claimApplicationWorkflow.getEmployee().getEmployeeId(), claimApplicationWorkflow);

		}

		List<ClaimApplicationReviewer> claimApplicationReviewers = new ArrayList<>(
				claimApplicationVO.getClaimApplicationReviewers());
		Collections.sort(claimApplicationReviewers, new ClaimTypeComp());

		if (claimApplicationReviewers.size() == 0) {
			userCell.enableBorderSide(Rectangle.BOTTOM);
		}
		workflowySectionTable.addCell(userCell);

		Integer snoCount = 2;
		Integer revCount = 1;

		String claimRev1Status = "";
		String claimRev2Status = "";
		String claimRev3Status = "";
		int tempRevCount = 1;

		for (ClaimApplicationReviewer claimApplicationReviewer : claimApplicationReviewers) {
			ClaimApplicationWorkflow claimApplicationWorkflow = workFlow
					.get(claimApplicationReviewer.getEmployee().getEmployeeId());
			String status = "";
			if (claimApplicationWorkflow != null) {
				status = claimApplicationWorkflow.getClaimStatusMaster().getClaimStatusName();
			}
			if (tempRevCount == 1) {
				claimRev1Status = status;
			}
			if (tempRevCount == 2) {
				claimRev2Status = status;
			}
			if (tempRevCount == 3) {
				claimRev3Status = status;
			}

			tempRevCount++;
		}

		int claimAppRevListSize = claimApplicationReviewers.size();
		for (ClaimApplicationReviewer claimApplicationReviewer : claimApplicationReviewers) {
			ClaimApplicationWorkflow claimApplicationWorkflow = workFlow
					.get(claimApplicationReviewer.getEmployee().getEmployeeId());
			String remarks = "";
			String status = "";
			String claimDate = "";
			if (claimApplicationWorkflow != null) {
				String claimRevRemarks = "";

				if (StringUtils.isNotBlank(claimApplicationWorkflow.getRemarks())) {
					try {
						claimRevRemarks = URLDecoder.decode(claimApplicationWorkflow.getRemarks(), "UTF-8");
					} catch (UnsupportedEncodingException e) {
						claimRevRemarks = claimApplicationWorkflow.getRemarks();
						LOGGER.error(e.getMessage(), e);
					}
				}

				remarks = claimRevRemarks;
				status = claimApplicationWorkflow.getClaimStatusMaster().getClaimStatusName();
				claimDate = DateUtils.timeStampToString(claimApplicationWorkflow.getCreatedDate());
			}
			if (!claimApplicationVO.getClaimStatusMaster().getClaimStatusName()
					.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_WITHDRAWN)) {
				if (revCount == 1) {
					if (StringUtils.isBlank(claimRev1Status)) {
						status = "Pending";
					}
				}
				if (revCount == 2) {
					if (StringUtils.isBlank(claimRev2Status)) {
						if (StringUtils.isBlank(claimRev1Status)) {
							status = "Pending";
						} else {
							if (claimRev1Status.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_COMPLETED)) {
								status = "";
							}
						}

					}
				}

				if (revCount == 3) {
					if (StringUtils.isBlank(claimRev3Status)) {
						if (StringUtils.isBlank(claimRev1Status) && StringUtils.isBlank(claimRev2Status)) {
							status = "Pending";
						} else {
							if (claimRev2Status.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_COMPLETED)) {
								status = "";
							}
						}

					}
				}

			}

			PdfPCell claimRevCell = createWorkFlowSectionTableData(snoCount.toString(), "ClaimReviewer" + revCount,
					getEmployeeName(claimApplicationReviewer.getEmployee()), remarks, status, claimDate);
			PDFUtils.setDefaultCellLayout(claimRevCell);
			claimRevCell.enableBorderSide(Rectangle.LEFT);
			claimRevCell.enableBorderSide(Rectangle.RIGHT);
			if (claimAppRevListSize == revCount) {
				claimRevCell.enableBorderSide(Rectangle.BOTTOM);
			}

			workflowySectionTable.addCell(claimRevCell);

			snoCount++;
			revCount++;
		}

		return workflowySectionTable;

	}

	private PdfPTable getWorkFlowHistorySectionDraft(Document document, Long companyId,
			ClaimApplication claimApplicationVO) {
		PdfPTable workflowySectionTable = new PdfPTable(new float[] { 1f });
		workflowySectionTable.setWidthPercentage(110f);
		workflowySectionTable.getTotalWidth();
		workflowySectionTable.setSpacingBefore(5f);

		PdfPCell workflowyTableHeaderCell = new PdfPCell(new Phrase("WorkFlow History", getBaseFont10Bold()));
		workflowyTableHeaderCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		workflowyTableHeaderCell.setBackgroundColor(PayAsiaPDFConstants.TABLE_STANDARD_HEADER_GRAY_COLOR);
		workflowyTableHeaderCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);
		PdfPCell pdfCell1 = createWorkFlowSectionTableData("S.No", "WorkFlow Role", "Name", "Remarks", "Status",
				"Date");
		PDFUtils.setDefaultCellLayout(pdfCell1);
		pdfCell1.enableBorderSide(Rectangle.LEFT);
		pdfCell1.enableBorderSide(Rectangle.RIGHT);
		pdfCell1.setBackgroundColor(PayAsiaPDFConstants.TABLE_STANDARD_HEADER_GRAY_COLOR);

		String userStatus = "";
		if (claimApplicationVO.getClaimStatusMaster().getClaimStatusName()
				.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_DRAFT)) {
			userStatus = claimApplicationVO.getClaimStatusMaster().getClaimStatusName();
		}

		String claimUserRemarks = "";
		if (StringUtils.isNotBlank(claimApplicationVO.getRemarks())) {
			try {
				claimUserRemarks = URLDecoder.decode(claimApplicationVO.getRemarks(), "UTF-8");
			} catch (UnsupportedEncodingException | IllegalArgumentException e) {
				claimUserRemarks = claimApplicationVO.getRemarks();
				LOGGER.error(e.getMessage(), e);
			}
		}

		PdfPCell userCell = createWorkFlowSectionTableData("1", "User",
				getEmployeeName(claimApplicationVO.getEmployee()), claimUserRemarks, userStatus,
				DateUtils.timeStampToString(claimApplicationVO.getCreatedDate()));
		PDFUtils.setDefaultCellLayout(userCell);
		userCell.enableBorderSide(Rectangle.LEFT);
		userCell.enableBorderSide(Rectangle.RIGHT);

		workflowySectionTable.addCell(workflowyTableHeaderCell);
		workflowySectionTable.addCell(pdfCell1);

		List<EmployeeClaimReviewer> applicationReviewers = new ArrayList<>(
				employeeClaimReviewerDAO.findByCondition(claimApplicationVO.getEmployee().getEmployeeId(),
						claimApplicationVO.getEmployeeClaimTemplate().getEmployeeClaimTemplateId(),
						claimApplicationVO.getCompany().getCompanyId()));
		Collections.sort(applicationReviewers, new EmployeeReviewerComp());

		if (applicationReviewers.size() == 0) {
			userCell.enableBorderSide(Rectangle.BOTTOM);
		}
		workflowySectionTable.addCell(userCell);

		Integer snoCount = 2;
		Integer revCount = 1;

		int claimAppRevListSize = applicationReviewers.size();
		for (EmployeeClaimReviewer claimApplicationReviewer : applicationReviewers) {
			String remarks = "";
			String status = "";
			String claimDate = "";

			PdfPCell claimRevCell = createWorkFlowSectionTableData(snoCount.toString(), "ClaimReviewer" + revCount,
					getEmployeeName(claimApplicationReviewer.getEmployee2()), remarks, status, claimDate);
			PDFUtils.setDefaultCellLayout(claimRevCell);
			claimRevCell.enableBorderSide(Rectangle.LEFT);
			claimRevCell.enableBorderSide(Rectangle.RIGHT);
			if (claimAppRevListSize == revCount) {
				claimRevCell.enableBorderSide(Rectangle.BOTTOM);
			}

			workflowySectionTable.addCell(claimRevCell);

			snoCount++;
			revCount++;
		}
		return workflowySectionTable;
	}

	private class EmployeeReviewerComp implements Comparator<EmployeeClaimReviewer> {
		public int compare(EmployeeClaimReviewer templateField, EmployeeClaimReviewer compWithTemplateField) {
			if (templateField.getEmployeeClaimReviewerId() > compWithTemplateField.getEmployeeClaimReviewerId()) {
				return 1;
			} else if (templateField.getEmployeeClaimReviewerId() < compWithTemplateField
					.getEmployeeClaimReviewerId()) {
				return -1;
			}
			return 0;
		}
	}

	private PdfPCell createWorkFlowSectionTableData(String leftColKey, String leftColVal, String middleColKey,
			String middleColVal, String rightColKey, String rightColVal) {
		PdfPCell pdfPCell = new PdfPCell();
		pdfPCell.setBorder(0);

		PdfPTable amountBasedTable = new PdfPTable(new float[] { 0.3f, 0.8f, 1.5f, 2f, 0.7f, 0.7f });
		amountBasedTable.setWidthPercentage(100f);

		PdfPCell nestedLeftKeyCell = new PdfPCell(new Phrase(leftColKey, getBaseFontNormal()));
		nestedLeftKeyCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);
		nestedLeftKeyCell.setBorder(0);

		PdfPCell nestedLeftValueCell = new PdfPCell(new Phrase(leftColVal, getBaseFontNormal()));
		nestedLeftValueCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);
		nestedLeftValueCell.setBorder(0);
		PdfPCell nestedMiddleKeyCell = new PdfPCell(new Phrase(middleColKey, getBaseFontNormal()));
		nestedMiddleKeyCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);
		nestedMiddleKeyCell.setBorder(0);
		PdfPCell nestedMiddleValueCell = new PdfPCell(new Phrase(middleColVal, getBaseFontNormal()));
		nestedMiddleValueCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);
		nestedMiddleValueCell.setBorder(0);
		PdfPCell nestedRightKeyCell = new PdfPCell(new Phrase(rightColKey, getBaseFontNormal()));
		nestedRightKeyCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);
		nestedRightKeyCell.setBorder(0);

		PdfPCell nestedRightValueCell = new PdfPCell(new Phrase(rightColVal, getBaseFontNormal()));
		nestedRightValueCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);
		nestedRightValueCell.setBorder(0);

		amountBasedTable.addCell(nestedLeftKeyCell);
		amountBasedTable.addCell(nestedLeftValueCell);
		amountBasedTable.addCell(nestedMiddleKeyCell);
		amountBasedTable.addCell(nestedMiddleValueCell);
		amountBasedTable.addCell(nestedRightKeyCell);
		amountBasedTable.addCell(nestedRightValueCell);

		pdfPCell.addElement(amountBasedTable);
		return pdfPCell;
	}

	private PdfPTable getCategoryWiseTotalSection(Document document, ClaimApplication claimApplicationVO) {
		PdfPTable categoryWiseTotalSectionTable = new PdfPTable(new float[] { 1f, 3f });
		categoryWiseTotalSectionTable.setWidthPercentage(110f);
		categoryWiseTotalSectionTable.getTotalWidth();
		categoryWiseTotalSectionTable.setSpacingBefore(5f);

		PdfPCell categoryWiseTableHeaderCell = new PdfPCell(new Phrase("Category Wise Total", getBaseFont10Bold()));
		categoryWiseTableHeaderCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		categoryWiseTableHeaderCell.setBackgroundColor(PayAsiaPDFConstants.TABLE_STANDARD_HEADER_GRAY_COLOR);
		categoryWiseTableHeaderCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);
		categoryWiseTableHeaderCell.setColspan(2);
		categoryWiseTotalSectionTable.addCell(categoryWiseTableHeaderCell);

		List<Tuple> tuplelist = claimApplicationDAO
				.getCategoryWiseTotalCount(claimApplicationVO.getClaimApplicationId());
		int tuplelistSize = tuplelist.size();
		int countT = 1;
		for (Tuple tuple : tuplelist) {
			PdfPCell pdfLeftCell = new PdfPCell(
					new Phrase((String) tuple.get(getAlias(ClaimCategoryMaster_.claimCategoryName), String.class),
							getBaseFontNormal()));
			pdfLeftCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			pdfLeftCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);
			PDFUtils.setDefaultCellLayout(pdfLeftCell);
			pdfLeftCell.enableBorderSide(Rectangle.LEFT);
			if (tuplelistSize == countT) {
				pdfLeftCell.enableBorderSide(Rectangle.BOTTOM);
			}
			DecimalFormat thousandSeperator = new DecimalFormat("###,###.00");
			String totalAmount = thousandSeperator
					.format(tuple.get(getAlias(ClaimApplicationItem_.claimAmount), BigDecimal.class).doubleValue());

			PdfPCell pdfRightCell = new PdfPCell(new Phrase(totalAmount + " "
					+ (claimApplicationVO.getEmployeeClaimTemplate().getClaimTemplate().getDefaultCurrency() == null
							? ""
							: claimApplicationVO.getEmployeeClaimTemplate().getClaimTemplate().getDefaultCurrency()
									.getCurrencyCode()),
					getBaseFontNormal()));
			pdfRightCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			pdfRightCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);
			PDFUtils.setDefaultCellLayout(pdfRightCell);
			pdfRightCell.enableBorderSide(Rectangle.RIGHT);
			if (tuplelistSize == countT) {
				pdfRightCell.enableBorderSide(Rectangle.BOTTOM);
			}

			categoryWiseTotalSectionTable.addCell(pdfLeftCell);
			categoryWiseTotalSectionTable.addCell(pdfRightCell);
			countT++;
		}

		return categoryWiseTotalSectionTable;
	}

	/**
	 * Comparator Class for Ordering LeaveSchemeType List
	 */
	private class ClaimAppTempItemCusFieComp implements Comparator<ClaimApplicationItemCustomField> {
		public int compare(ClaimApplicationItemCustomField templateField,
				ClaimApplicationItemCustomField compWithTemplateField) {
			if (templateField.getClaimTemplateItemCustomField().getCustomFieldId() > compWithTemplateField
					.getClaimTemplateItemCustomField().getCustomFieldId()) {
				return 1;
			} else if (templateField.getClaimTemplateItemCustomField().getCustomFieldId() < compWithTemplateField
					.getClaimTemplateItemCustomField().getCustomFieldId()) {
				return -1;
			}
			return 0;

		}

	}

	private PdfPTable getItemsSection(Document document, PdfWriter writer, ClaimApplication claimApplicationVO,
			boolean hasLundinTimesheetModule) {
		DecimalFormat thousandSeperator = new DecimalFormat("###,###.00");
		PdfPTable itemSectionTable = new PdfPTable(new float[] { 1f });
		itemSectionTable.setWidthPercentage(110f);
		itemSectionTable.getTotalHeight();

		PdfPCell pdfPCell = new PdfPCell();
		PDFUtils.setDefaultCellLayout(pdfPCell);
		pdfPCell.enableBorderSide(Rectangle.TOP);
		pdfPCell.enableBorderSide(Rectangle.LEFT);
		pdfPCell.enableBorderSide(Rectangle.RIGHT);
		pdfPCell.enableBorderSide(Rectangle.BOTTOM);
		pdfPCell.setPadding(0);

		PdfPTable itemTable;

		ClaimPreference claimPreference = claimPreferenceDAO
				.findByCompanyId(claimApplicationVO.getCompany().getCompanyId());

		List<SortCondition> sortConditions = new ArrayList<SortCondition>();

		if (StringUtils.isNotBlank(claimPreference.getClaimItemDateSortOrder())) {
			SortCondition sortCondition = new SortCondition();
			sortCondition.setColumnName(PayAsiaConstants.CLAIMITEMDATESORTORDER);
			if (claimPreference.getClaimItemDateSortOrder().equalsIgnoreCase(PayAsiaConstants.ASC)) {
				sortCondition.setOrderType(PayAsiaConstants.ASC);
			} else {
				sortCondition.setOrderType(PayAsiaConstants.DESC);
			}
			sortConditions.add(sortCondition);
		}

		if (StringUtils.isNotBlank(claimPreference.getClaimItemNameSortOrder())) {
			SortCondition sortCondition = new SortCondition();
			sortCondition.setColumnName(PayAsiaConstants.ITEMNAMESORTORDER);
			if (claimPreference.getClaimItemNameSortOrder().equalsIgnoreCase(PayAsiaConstants.ASC)) {
				sortCondition.setOrderType(PayAsiaConstants.ASC);
			} else {
				sortCondition.setOrderType(PayAsiaConstants.DESC);
			}
			sortConditions.add(sortCondition);
		}

		List<ClaimApplicationItem> claimApplicationItems = claimApplicationItemDAO
				.findByCondition(claimApplicationVO.getClaimApplicationId(), sortConditions, PayAsiaConstants.ASC);

		if ((StringUtils.isNotBlank(claimPreference.getClaimItemNameSortOrder()))
				&& (StringUtils.isNotBlank(claimPreference.getClaimItemDateSortOrder()))) {
			Collections.sort(claimApplicationItems, new ClaimItemCompAndClaimDateComp(
					claimPreference.getClaimItemDateSortOrder(), claimPreference.getClaimItemNameSortOrder()));
		}

		for (ClaimApplicationItem claimApplicationItem : claimApplicationItems) {

			if (!claimApplicationItem.isActive()) {
				continue;
			}

			itemTable = new PdfPTable(new float[] { 1f });
			itemTable.setWidthPercentage(100f);

			PdfPCell itemTableHeaderCell = new PdfPCell(new Phrase(claimApplicationItem.getEmployeeClaimTemplateItem()
					.getClaimTemplateItem().getClaimItemMaster().getClaimItemName(), getBaseFont10Bold()));
			itemTableHeaderCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			itemTableHeaderCell.setBackgroundColor(PayAsiaPDFConstants.TABLE_STANDARD_HEADER_GRAY_COLOR);
			PDFUtils.setDefaultCellLayout(itemTableHeaderCell);
			itemTableHeaderCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);
			itemTable.addCell(itemTableHeaderCell);

			PdfPCell pdfCell1 = createItemsSectionTableData("Category:",
					claimApplicationItem.getEmployeeClaimTemplateItem().getClaimTemplateItem().getClaimItemMaster()
							.getClaimCategoryMaster().getClaimCategoryName(),
					"Claim Date:", DateUtils.timeStampToString(claimApplicationItem.getClaimDate()), "Receipt No:",
					claimApplicationItem.getReceiptNumber());
			PDFUtils.setDefaultCellLayout(pdfCell1);

			itemTable.addCell(pdfCell1);

			if (claimApplicationItem.getEmployeeClaimTemplateItem().getClaimTemplateItem()
					.getClaimTemplateItemClaimTypes().size() > 0) {
				ClaimTemplateItemClaimType claimTemplateItemClaimType = claimApplicationItem
						.getEmployeeClaimTemplateItem().getClaimTemplateItem().getClaimTemplateItemClaimTypes()
						.iterator().next();
				if (claimTemplateItemClaimType.getClaimType().getCodeValue()
						.equals(PayAsiaConstants.APP_CODE_CLAIM_TYPE_QUANTITY_BASED)) {
					PdfPCell pdfCell3 = createItemsSectionTableData("Quantity:",
							claimApplicationItem.getQuantity() == null ? ""
									: String.valueOf(claimApplicationItem.getQuantity()),
							"Unit Price:",
							claimApplicationItem.getUnitPrice() == null ? ""
									: claimApplicationItem.getUnitPrice().toString(),
							"Actual Unit Price:", claimTemplateItemClaimType.getDefaultUnitPrice() == null ? ""
									: claimTemplateItemClaimType.getDefaultUnitPrice().toString());
					PDFUtils.setDefaultCellLayout(pdfCell3);
					itemTable.addCell(pdfCell3);
				}
				BigDecimal applicableAmt = claimApplicationItem.getApplicableClaimAmount();
				String applicableAmount = "";
				if (applicableAmt != null) {
					applicableAmount = thousandSeperator.format(applicableAmt.doubleValue());
				}

				if (claimTemplateItemClaimType.getClaimType().getCodeValue()
						.equals(PayAsiaConstants.APP_CODE_CLAIM_TYPE_AMOUNT_BASED)) {
					PdfPCell pdfCell3 = createItemsSectionTableData("Amount Applicable:", applicableAmount, "", "", "",
							"");
					PDFUtils.setDefaultCellLayout(pdfCell3);
					itemTable.addCell(pdfCell3);
				}
				if (claimTemplateItemClaimType.getClaimType().getCodeValue()
						.equals(PayAsiaConstants.APP_CODE_CLAIM_TYPE_FOREX_BASED)) {
					PdfPCell currencyNameCell = createRemarksCell("Currency Name",
							claimApplicationItem.getCurrencyMaster() == null ? ""
									: claimApplicationItem.getCurrencyMaster().getCurrencyName());
					PDFUtils.setDefaultCellLayout(currencyNameCell);
					itemTable.addCell(currencyNameCell);

					PdfPCell pdfCell3 = createItemsSectionTableData("Receipt Amount",
							claimApplicationItem.getForexReceiptAmount() == null ? ""
									: thousandSeperator
											.format(claimApplicationItem.getForexReceiptAmount().doubleValue()),
							"Forex Rate:", claimApplicationItem.getExchangeRate() == null ? ""
									: claimApplicationItem.getExchangeRate().toString(),
							"", "");
					PDFUtils.setDefaultCellLayout(pdfCell3);
					itemTable.addCell(pdfCell3);
				}
			}

			String ClaimantNameStr = "";
			String claimantName = "";
			if (claimApplicationItem.getEmployeeClaimTemplateItem().getClaimTemplateItem()
					.getClaimTemplateItemGenerals().size() > 0) {
				if (claimApplicationItem.getEmployeeClaimTemplateItem().getClaimTemplateItem()
						.getClaimTemplateItemGenerals().iterator().next().getOpenToDependents()) {
					if (StringUtils.isNotBlank(claimApplicationItem.getClaimantName())) {
						ClaimantNameStr = "Claimant Name";
						claimantName = claimApplicationItem.getClaimantName();
					}
				}
			}

			PdfPCell pdfCell2 = createItemsSectionTableData("Amount:",
					claimApplicationItem.getClaimAmount() == null ? ""
							: thousandSeperator.format(claimApplicationItem.getClaimAmount().doubleValue()) + " "
									+ (claimApplicationVO.getEmployeeClaimTemplate().getClaimTemplate()
											.getDefaultCurrency() == null
													? ""
													: claimApplicationVO.getEmployeeClaimTemplate().getClaimTemplate()
															.getDefaultCurrency().getCurrencyCode()),
					"Tax amount:",
					claimApplicationItem.getTaxAmount() == null ? ""
							: thousandSeperator.format(claimApplicationItem.getTaxAmount().doubleValue()),
					ClaimantNameStr, claimantName);
			PDFUtils.setDefaultCellLayout(pdfCell2);
			itemTable.addCell(pdfCell2);

			// Get Lundin Claims Details
			Set<ClaimApplicationItemLundinDetail> applicationItemLundinDetails = claimApplicationItem
					.getClaimApplicationItemLundinDetails();
			if (!applicationItemLundinDetails.isEmpty() && hasLundinTimesheetModule) {
				PdfPCell pdfLundinDetailsCell = createItemsSectionTableData("Block:",
						claimApplicationItem.getClaimApplicationItemLundinDetails().iterator().next().getLundinBlock()
								.getBlockName(),
						"AFE:", claimApplicationItem.getClaimApplicationItemLundinDetails().iterator().next()
								.getLundinAFE().getAfeName(),
						"", "");
				PDFUtils.setDefaultCellLayout(pdfLundinDetailsCell);
				itemTable.addCell(pdfLundinDetailsCell);
			}

			List<ClaimCustomFieldDTO> customFields = new ArrayList<>();

			List<ClaimApplicationItemCustomField> claimApplicationItemCustomFieldList = new ArrayList<ClaimApplicationItemCustomField>(
					claimApplicationItem.getClaimApplicationItemCustomFields());
			Collections.sort(claimApplicationItemCustomFieldList, new ClaimAppTempItemCusFieComp());
			for (ClaimApplicationItemCustomField claimApplicationItemCustomField : claimApplicationItemCustomFieldList) {
				ClaimCustomFieldDTO claimCustomFieldDTO = new ClaimCustomFieldDTO();
				claimCustomFieldDTO.setCustomFieldName(
						claimApplicationItemCustomField.getClaimTemplateItemCustomField().getFieldName());
				if (claimApplicationItemCustomField.getClaimTemplateItemCustomField().getFieldType().getCodeDesc()
						.equalsIgnoreCase("date")) {

					claimCustomFieldDTO.setValue(DateUtils.convertDateFormat(claimApplicationItemCustomField.getValue(),
							claimApplicationVO.getCompany().getDateFormat()));
				} else {
					claimCustomFieldDTO.setValue(claimApplicationItemCustomField.getValue());
				}

				customFields.add(claimCustomFieldDTO);
			}
			PdfPCell pdfCell4 = createItemsSectionCustomFieldTableData(customFields);
			PDFUtils.setDefaultCellLayout(pdfCell4);
			if (!customFields.isEmpty()) {
				itemTable.addCell(pdfCell4);
			}

			PdfPCell remarksCell = createRemarksCell("Remarks:",
					claimApplicationItem.getRemarks() == null ? "" : claimApplicationItem.getRemarks());
			PDFUtils.setDefaultCellLayout(remarksCell);
			itemTable.addCell(remarksCell);

			for (ClaimApplicationItemAttachment claimApplicationItemAttachment : claimApplicationItem
					.getClaimApplicationItemAttachments()) {

				PdfPCell attachmentCell = createRemarksCell("Attachment:",
						claimApplicationItemAttachment.getFileName());
				PDFUtils.setDefaultCellLayout(attachmentCell);

				itemTable.addCell(attachmentCell);
			}
			pdfPCell.addElement(itemTable);
		}

		itemSectionTable.addCell(pdfPCell);

		return itemSectionTable;

	}

	private PdfPCell createItemsSectionTableData(String leftColKey, String leftColVal, String middleColKey,
			String middleColVal, String rightColKey, String rightColVal) {
		PdfPCell pdfPCell = new PdfPCell();
		pdfPCell.setBorder(0);

		PdfPTable amountBasedTable = new PdfPTable(new float[] { 1f, 1f, 1f, 1f, 1f, 1f });
		amountBasedTable.setWidthPercentage(100f);

		PdfPCell nestedLeftKeyCell = new PdfPCell(new Phrase(leftColKey, getBaseFontNormal()));
		nestedLeftKeyCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);
		nestedLeftKeyCell.setBorder(0);

		PdfPCell nestedLeftValueCell = new PdfPCell(new Phrase(leftColVal, getBaseFontNormal()));
		nestedLeftValueCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);
		nestedLeftValueCell.setBorder(0);

		PdfPCell nestedMiddleKeyCell = new PdfPCell(new Phrase(middleColKey, getBaseFontNormal()));
		nestedMiddleKeyCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);
		nestedMiddleKeyCell.setBorder(0);

		PdfPCell nestedMiddleValueCell = new PdfPCell(new Phrase(middleColVal, getBaseFontNormal()));
		nestedMiddleValueCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);
		nestedMiddleValueCell.setBorder(0);

		PdfPCell nestedRightKeyCell = new PdfPCell(new Phrase(rightColKey, getBaseFontNormal()));
		nestedRightKeyCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);
		nestedRightKeyCell.setBorder(0);
		PdfPCell nestedRightValueCell = new PdfPCell(new Phrase(rightColVal, getBaseFontNormal()));
		nestedRightValueCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);
		nestedRightValueCell.setBorder(0);

		amountBasedTable.addCell(nestedLeftKeyCell);
		amountBasedTable.addCell(nestedLeftValueCell);
		amountBasedTable.addCell(nestedMiddleKeyCell);
		amountBasedTable.addCell(nestedMiddleValueCell);
		amountBasedTable.addCell(nestedRightKeyCell);
		amountBasedTable.addCell(nestedRightValueCell);

		pdfPCell.addElement(amountBasedTable);
		return pdfPCell;
	}

	private PdfPCell createItemsSectionCustomFieldTableData(List<ClaimCustomFieldDTO> customFields) {
		String key = "";
		String val = "";

		PdfPCell pdfPCell = new PdfPCell();
		pdfPCell.setBorder(0);

		PdfPTable amountBasedTable = new PdfPTable(new float[] { 1f, 1f });
		amountBasedTable.setWidthPercentage(100f);

		PdfPCell nestedLeftKeyCell = null;
		PdfPCell nestedLeftValueCell = null;
		for (ClaimCustomFieldDTO customFieldDTO : customFields) {
			key = customFieldDTO.getCustomFieldName();
			val = customFieldDTO.getValue();

			nestedLeftKeyCell = new PdfPCell(new Phrase(key, getBaseFontNormal()));

			nestedLeftKeyCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);
			nestedLeftKeyCell.setBorder(0);

			nestedLeftValueCell = new PdfPCell(new Phrase(val, getBaseFontNormal()));
			nestedLeftValueCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);
			nestedLeftValueCell.setBorder(0);

			amountBasedTable.addCell(nestedLeftKeyCell);
			amountBasedTable.addCell(nestedLeftValueCell);
		}

		pdfPCell.addElement(amountBasedTable);
		return pdfPCell;
	}

	private PdfPCell createRemarksCell(String remarksColKey, String remarksColVal) {
		PdfPCell pdfPCell = new PdfPCell();
		pdfPCell.setBorder(0);

		PdfPTable remarksTable = new PdfPTable(new float[] { 0.3f, 1.7f });
		remarksTable.setWidthPercentage(100f);

		PdfPCell pdfRemarksCell = new PdfPCell(new Phrase(remarksColKey, getBaseFontNormal()));
		pdfRemarksCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		pdfRemarksCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);
		pdfRemarksCell.setBorder(0);

		PdfPCell pdfRemarksValCell = new PdfPCell(new Phrase(remarksColVal, getBaseFontNormal()));
		pdfRemarksValCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		pdfRemarksValCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);
		pdfRemarksValCell.setBorder(0);

		remarksTable.addCell(pdfRemarksCell);
		remarksTable.addCell(pdfRemarksValCell);

		pdfPCell.addElement(remarksTable);

		return pdfPCell;

	}

	private Font getBaseFontNormal() {
		BaseFont bf = null;
		try {
			try {
				bf = BaseFont.createFont(PayAsiaPDFConstants.PDF_MULTILINGUAL_FONT_PATH, BaseFont.IDENTITY_H,
						BaseFont.EMBEDDED);
			} catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
			}
		} catch (DocumentException e1) {
			LOGGER.error(e1.getMessage(), e1);
		}
		Font unicodeFont = new Font(bf, 8, Font.NORMAL, BaseColor.BLACK);
		return unicodeFont;

	}

	private Font getBaseFont10Bold() {
		BaseFont bf = null;
		try {
			try {
				bf = BaseFont.createFont(PayAsiaPDFConstants.PDF_MULTILINGUAL_FONT_PATH, BaseFont.IDENTITY_H,
						BaseFont.EMBEDDED);
			} catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
			}
		} catch (DocumentException e1) {
			LOGGER.error(e1.getMessage(), e1);
		}
		Font unicodeFont = new Font(bf, 8, Font.BOLD, BaseColor.BLACK);
		return unicodeFont;

	}

	private Font getBaseFont12Bold() {
		BaseFont bf = null;
		try {
			try {
				bf = BaseFont.createFont(PayAsiaPDFConstants.PDF_MULTILINGUAL_FONT_PATH, BaseFont.IDENTITY_H,
						BaseFont.EMBEDDED);
			} catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
			}
		} catch (DocumentException e1) {
			LOGGER.error(e1.getMessage(), e1);
		}
		Font unicodeFont = new Font(bf, 11, Font.BOLD, BaseColor.BLACK);
		return unicodeFont;

	}

	private class ClaimItemCompAndClaimDateComp implements Comparator<ClaimApplicationItem> {
		String sortItem, sortByDate;

		public ClaimItemCompAndClaimDateComp(final String sortByDate, final String sortByItem) {
			this.sortByDate = sortByDate;
			this.sortItem = sortByItem;
		}

		@Override
		public int compare(ClaimApplicationItem templateField, ClaimApplicationItem compWithTemplateField) {

			int comparableVariable = 0;
			Date date = null, date1 = null;
			date1 = (Date) DateUtils.stringToTimestampWOTime(DateUtils.timeStampToString(templateField.getClaimDate()));

			date = (Date) DateUtils
					.stringToTimestampWOTime(DateUtils.timeStampToString(compWithTemplateField.getClaimDate()));

			String name = templateField.getEmployeeClaimTemplateItem().getClaimTemplateItem().getClaimItemMaster()
					.getClaimItemName();

			String name1 = compWithTemplateField.getEmployeeClaimTemplateItem().getClaimTemplateItem()
					.getClaimItemMaster().getClaimItemName();

			if (sortByDate.equalsIgnoreCase(PayAsiaConstants.ASC) && (sortItem.equalsIgnoreCase(PayAsiaConstants.ASC)))

			{
				if (date.before(date1)) {
					comparableVariable = 1;
				} else if (date.after(date1)) {
					comparableVariable = -1;
				} else {
					comparableVariable = name.compareTo(name1);
				}

			}

			else if ((sortByDate.equalsIgnoreCase(PayAsiaConstants.DESC)
					&& (sortItem.equalsIgnoreCase(PayAsiaConstants.DESC)))) {

				if (date.before(date1)) {
					comparableVariable = -1;
				} else if (date.after(date1)) {
					comparableVariable = 1;
				} else {
					comparableVariable = name1.compareTo(name);
				}

			}

			else if ((sortByDate.equalsIgnoreCase(PayAsiaConstants.ASC)
					&& (sortItem.equalsIgnoreCase(PayAsiaConstants.DESC)))) {
				if (date.before(date1)) {
					comparableVariable = 1;
				} else if (date.after(date1)) {
					comparableVariable = -1;
				} else {
					comparableVariable = name1.compareTo(name);
				}
			}

			else if ((sortByDate.equalsIgnoreCase(PayAsiaConstants.DESC)
					&& (sortItem.equalsIgnoreCase(PayAsiaConstants.ASC)))) {
				if (date.before(date1)) {
					comparableVariable = -1;
				} else if (date.after(date1)) {
					comparableVariable = 1;
				} else {
					comparableVariable = name.compareTo(name1);
				}
			}
			return comparableVariable;
		}

	}
}
