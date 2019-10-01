/**
 * @author vivekjain
 *
 */
package com.payasia.logic.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

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
import com.payasia.common.dto.LeaveCustomFieldDTO;
import com.payasia.common.dto.LeaveDTO;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.PDFUtils;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.PayAsiaPDFConstants;
import com.payasia.dao.EmployeeLeaveSchemeTypeDAO;
import com.payasia.dao.LeaveApplicationDAO;
import com.payasia.dao.LeavePreferenceDAO;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.LeaveApplication;
import com.payasia.dao.bean.LeaveApplicationAttachment;
import com.payasia.dao.bean.LeaveApplicationCustomField;
import com.payasia.dao.bean.LeaveApplicationReviewer;
import com.payasia.dao.bean.LeaveApplicationWorkflow;
import com.payasia.dao.bean.LeavePreference;
import com.payasia.logic.BaseLogic;
import com.payasia.logic.LeaveApplicationPrintPDFLogic;
import com.payasia.logic.LeaveBalanceSummaryLogic;

@Component
public class LeaveApplicationPrintPDFLogicImpl extends BaseLogic implements LeaveApplicationPrintPDFLogic {
	/** The total. */

	@Resource
	LeaveApplicationDAO leaveApplicationDAO;
	@Resource
	EmployeeLeaveSchemeTypeDAO employeeLeaveSchemeTypeDAO;
	@Resource
	LeaveBalanceSummaryLogic leaveBalanceSummaryLogic;
	@Resource
	LeavePreferenceDAO leavePreferenceDAO;

	private static final Logger LOGGER = Logger.getLogger(LeaveApplicationPrintPDFLogicImpl.class);

	@Override
	public PdfPTable createLeaveReportPdf(Document document, PdfWriter writer, int currentPageNumber, Long companyId, Long leaveApplicationId) {
		PdfPTable mainSectionTable = new PdfPTable(new float[] { 1 });
		mainSectionTable.setWidthPercentage(95f);
		mainSectionTable.setHorizontalAlignment(Element.ALIGN_CENTER);

		PdfPCell mainCell = new PdfPCell();
		mainCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		mainCell.setPadding(0);
		mainCell.setPaddingTop(5f);
		mainCell.setBorder(0);
		PDFUtils.setDefaultCellLayout(mainCell);

		LeaveApplication leaveApplicationVO = leaveApplicationDAO.findById(leaveApplicationId);

		PdfPTable headerSectionTable = getHeaderSection(document, leaveApplicationVO);
		headerSectionTable.setWidthPercentage(110f);

		PdfPTable pdfTableSummaryHeaderTable = getPdfTableHeader(document, "Summary");
		pdfTableSummaryHeaderTable.setWidthPercentage(110f);
		PdfPTable summarySectionTable = getSummarySection(document, companyId, leaveApplicationVO);

		PdfPTable pdfTableWorkflowHeaderTable = getPdfTableHeader(document, "Workflow History");
		pdfTableWorkflowHeaderTable.setWidthPercentage(110f);
		PdfPTable workflowSectionTable = getWorkFlowHistorySection(document, companyId, leaveApplicationVO);

		mainCell.addElement(headerSectionTable);
		mainCell.addElement(summarySectionTable);
		mainCell.addElement(workflowSectionTable);

		mainSectionTable.addCell(mainCell);
		return mainSectionTable;
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

	private PdfPTable getHeaderSection(Document document, LeaveApplication leaveApplicationVO) {
		PdfPTable headerSectionTable = new PdfPTable(new float[] { 1f });
		headerSectionTable.setWidthPercentage(110f);
		headerSectionTable.setSpacingAfter(5f);

		PdfPCell headerCell = new PdfPCell(new Phrase(leaveApplicationVO.getCompany().getCompanyName(), getBaseFont12Bold()));
		headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		headerCell.setBorder(0);

		headerSectionTable.addCell(headerCell);
		return headerSectionTable;
	}

	private PdfPTable getSummarySection(Document document, Long companyId, LeaveApplication leaveApplicationVO) {
		DecimalFormat decimalFmt = new DecimalFormat("#,##0.00");

		LeavePreference leavePreferenceVO = leavePreferenceDAO.findByCompanyId(companyId);

		PdfPTable summarySectionTable = new PdfPTable(new float[] { 1f });
		summarySectionTable.setWidthPercentage(110f);
		summarySectionTable.setSpacingBefore(5f);

		PdfPCell SummaryTableHeaderCell = new PdfPCell(new Phrase("Summary", getBaseFont10Bold()));
		SummaryTableHeaderCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		SummaryTableHeaderCell.setBackgroundColor(PayAsiaPDFConstants.TABLE_STANDARD_HEADER_GRAY_COLOR);
		SummaryTableHeaderCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);
		SummaryTableHeaderCell.setPaddingRight(50.0f);
		summarySectionTable.addCell(SummaryTableHeaderCell);

		String employeeName = leaveApplicationVO.getEmployee().getFirstName();
		if (StringUtils.isNotBlank(leaveApplicationVO.getEmployee().getLastName())) {
			employeeName += " " + leaveApplicationVO.getEmployee().getLastName();
		}

		PdfPCell pdfCell1 = createSummarySectionTableData("Employee Name:", employeeName, "Employee Number:",
				leaveApplicationVO.getEmployee().getEmployeeNumber());
		PDFUtils.setDefaultCellLayout(pdfCell1);
		pdfCell1.enableBorderSide(Rectangle.LEFT);
		pdfCell1.enableBorderSide(Rectangle.RIGHT);

		summarySectionTable.addCell(pdfCell1);

		PdfPCell pdfCell2 = createSummarySectionTableData("Leave Scheme:",
				leaveApplicationVO.getEmployeeLeaveSchemeType().getEmployeeLeaveScheme().getLeaveScheme().getSchemeName(), "Leave Type:",
				leaveApplicationVO.getEmployeeLeaveSchemeType().getLeaveSchemeType().getLeaveTypeMaster().getLeaveTypeName());
		PDFUtils.setDefaultCellLayout(pdfCell2);
		pdfCell2.enableBorderSide(Rectangle.LEFT);
		pdfCell2.enableBorderSide(Rectangle.RIGHT);
		summarySectionTable.addCell(pdfCell2);

		boolean isLeaveUnitDays = leaveBalanceSummaryLogic.isLeaveUnitDays(companyId);

		String fromSessionCaption = "";
		String toSessionCaption = "";
		String fromSession = "";
		String toSession = "";
		if (isLeaveUnitDays) {
			fromSessionCaption = "From Session:";
			toSessionCaption = "To Session:";
			fromSession = leaveApplicationVO.getLeaveSessionMaster1().getSession();
			toSession = leaveApplicationVO.getLeaveSessionMaster2().getSession();
		}
		PdfPCell pdfCell3 = createSummarySectionTableData("From Date:", DateUtils.timeStampToString(leaveApplicationVO.getStartDate()), fromSessionCaption,
				fromSession);
		PDFUtils.setDefaultCellLayout(pdfCell3);
		pdfCell3.enableBorderSide(Rectangle.LEFT);
		pdfCell3.enableBorderSide(Rectangle.RIGHT);
		summarySectionTable.addCell(pdfCell3);

		PdfPCell pdfCell4 = createSummarySectionTableData("To Date:", DateUtils.timeStampToString(leaveApplicationVO.getEndDate()), toSessionCaption,
				toSession);
		PDFUtils.setDefaultCellLayout(pdfCell4);
		pdfCell4.enableBorderSide(Rectangle.LEFT);
		pdfCell4.enableBorderSide(Rectangle.RIGHT);
		summarySectionTable.addCell(pdfCell4);

		LeaveDTO leaveBalDTO = employeeLeaveSchemeTypeDAO.getLeaveBalance(leaveApplicationVO.getEmployeeLeaveSchemeType().getEmployeeLeaveSchemeTypeId());

		String leaveUnitCaption = "";
		BigDecimal totalLeaveDays = null;
		if (isLeaveUnitDays) {
			LeaveDTO leaveDaysDTO = new LeaveDTO();
			leaveDaysDTO.setFromDate(DateUtils.timeStampToString(leaveApplicationVO.getStartDate()));
			leaveDaysDTO.setToDate(DateUtils.timeStampToString(leaveApplicationVO.getEndDate()));
			leaveDaysDTO.setSession1(leaveApplicationVO.getLeaveSessionMaster1().getLeaveSessionId());
			leaveDaysDTO.setSession2(leaveApplicationVO.getLeaveSessionMaster2().getLeaveSessionId());
			leaveDaysDTO.setEmployeeLeaveSchemeTypeId(leaveApplicationVO.getEmployeeLeaveSchemeType().getEmployeeLeaveSchemeTypeId());
			LeaveDTO leaveDTORes = employeeLeaveSchemeTypeDAO.getNoOfDays(leaveDaysDTO);
			totalLeaveDays = leaveDTORes.getDays();
			leaveUnitCaption = "Leave Days:";
		} else {
			// Hours between dates
			totalLeaveDays = new BigDecimal(leaveApplicationVO.getTotalDays()).setScale(2, BigDecimal.ROUND_HALF_UP);
			leaveUnitCaption = "Leave Hours:";
		}

		PdfPCell pdfCell5 = createSummarySectionTableData("Leave Balance:",
				leaveBalDTO.getLeaveBalance() == null ? "" : decimalFmt.format(leaveBalDTO.getLeaveBalance()).toString(), leaveUnitCaption,
				totalLeaveDays == null ? "" : decimalFmt.format(totalLeaveDays).toString());
		PDFUtils.setDefaultCellLayout(pdfCell5);
		pdfCell5.enableBorderSide(Rectangle.LEFT);
		pdfCell5.enableBorderSide(Rectangle.RIGHT);
		summarySectionTable.addCell(pdfCell5);

		List<LeaveCustomFieldDTO> customFields = new ArrayList<>();
		for (LeaveApplicationCustomField leaveApplicationCustomField : leaveApplicationVO.getLeaveApplicationCustomFields()) {
			LeaveCustomFieldDTO leaveCustomFieldDTO = new LeaveCustomFieldDTO();
			leaveCustomFieldDTO.setCustomFieldName(leaveApplicationCustomField.getLeaveSchemeTypeCustomField().getFieldName());
			leaveCustomFieldDTO.setValue(leaveApplicationCustomField.getValue());
			customFields.add(leaveCustomFieldDTO);
		}
		PdfPCell customFieldCell = createCustomFieldTableData(customFields);
		PDFUtils.setDefaultCellLayout(customFieldCell);
		customFieldCell.enableBorderSide(Rectangle.LEFT);
		customFieldCell.enableBorderSide(Rectangle.RIGHT);
		if (!customFields.isEmpty()) {
			summarySectionTable.addCell(customFieldCell);
		}

		String applyTo = "";
		for (LeaveApplicationReviewer leaveApplicationReviewer : leaveApplicationVO.getLeaveApplicationReviewers()) {
			if (leaveApplicationReviewer.getWorkFlowRuleMaster().getRuleValue().equals("1")) {

				applyTo = getEmployeeName(leaveApplicationReviewer.getEmployee());

			}
		}

		PdfPCell applyToCell = createRemarksCell("Apply To:", applyTo);
		PDFUtils.setDefaultCellLayout(applyToCell);
		applyToCell.enableBorderSide(Rectangle.LEFT);
		applyToCell.enableBorderSide(Rectangle.RIGHT);
		summarySectionTable.addCell(applyToCell);

		PdfPCell ccEmailCell = createRemarksCell("Email CC:", leaveApplicationVO.getEmailCC());
		PDFUtils.setDefaultCellLayout(ccEmailCell);
		ccEmailCell.enableBorderSide(Rectangle.LEFT);
		ccEmailCell.enableBorderSide(Rectangle.RIGHT);
		summarySectionTable.addCell(ccEmailCell);

		for (LeaveApplicationAttachment leaveApplicationAttachment : leaveApplicationVO.getLeaveApplicationAttachments()) {

			PdfPCell attachmentCell = createRemarksCell("Attachment:", leaveApplicationAttachment.getFileName());
			PDFUtils.setDefaultCellLayout(attachmentCell);
			attachmentCell.enableBorderSide(Rectangle.LEFT);
			attachmentCell.enableBorderSide(Rectangle.RIGHT);
			summarySectionTable.addCell(attachmentCell);

		}

		if (leavePreferenceVO.isPreApprovalRequired()) {

			PdfPCell preApprovedCell = null;

			if (leaveApplicationVO.getPreApprovalRequest()) {
				preApprovedCell = createRemarksCell("Is Pre-Approved Leave:", "Yes");
			} else {
				preApprovedCell = createRemarksCell("Is Pre-Approved Leave:", "No");
			}

			PDFUtils.setDefaultCellLayout(preApprovedCell);
			preApprovedCell.enableBorderSide(Rectangle.LEFT);
			preApprovedCell.enableBorderSide(Rectangle.RIGHT);
			summarySectionTable.addCell(preApprovedCell);

		}

		PdfPCell reasonCell = createRemarksCell("Reason:", leaveApplicationVO.getReason());
		PDFUtils.setDefaultCellLayout(reasonCell);
		reasonCell.enableBorderSide(Rectangle.LEFT);
		reasonCell.enableBorderSide(Rectangle.RIGHT);
		reasonCell.enableBorderSide(Rectangle.BOTTOM);
		summarySectionTable.addCell(reasonCell);
		return summarySectionTable;
	}

	private PdfPCell createSummarySectionTableData(String leftColKey, String leftColVal, String rightColKey, String rightColVal) {
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
	 * Comparator Class for Ordering LeaveApplicationReviewer List
	 */
	private class LeaveTypeComp implements Comparator<LeaveApplicationReviewer> {
		public int compare(LeaveApplicationReviewer templateField, LeaveApplicationReviewer compWithTemplateField) {
			if (templateField.getLeaveApplicationReviewerId() > compWithTemplateField.getLeaveApplicationReviewerId()) {
				return 1;
			} else if (templateField.getLeaveApplicationReviewerId() < compWithTemplateField.getLeaveApplicationReviewerId()) {
				return -1;
			}
			return 0;

		}

	}

	private PdfPTable getWorkFlowHistorySection(Document document, Long companyId, LeaveApplication leaveApplicationVO) {
		PdfPTable workflowySectionTable = new PdfPTable(new float[] { 1f });
		workflowySectionTable.setWidthPercentage(110f);
		workflowySectionTable.getTotalWidth();
		workflowySectionTable.setSpacingBefore(5f);

		PdfPCell workflowyTableHeaderCell = new PdfPCell(new Phrase("WorkFlow History", getBaseFont10Bold()));
		workflowyTableHeaderCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		workflowyTableHeaderCell.setBackgroundColor(PayAsiaPDFConstants.TABLE_STANDARD_HEADER_GRAY_COLOR);
		workflowyTableHeaderCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);
		PdfPCell pdfCell1 = createWorkFlowSectionTableData("S.No", "WorkFlow Role", "Name", "Remarks", "Status", "Date");
		PDFUtils.setDefaultCellLayout(pdfCell1);
		pdfCell1.enableBorderSide(Rectangle.LEFT);
		pdfCell1.enableBorderSide(Rectangle.RIGHT);
		pdfCell1.setBackgroundColor(PayAsiaPDFConstants.TABLE_STANDARD_HEADER_GRAY_COLOR);
		String userStatus = "";
		if (leaveApplicationVO.getLeaveStatusMaster().getLeaveStatusName().equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_WITHDRAWN)) {
			userStatus = leaveApplicationVO.getLeaveStatusMaster().getLeaveStatusName();
		} else {
			userStatus = "Submitted";
		}

		PdfPCell userCell = createWorkFlowSectionTableData("1", "User", getEmployeeName(leaveApplicationVO.getEmployee()), leaveApplicationVO.getReason(),
				userStatus, DateUtils.timeStampToString(leaveApplicationVO.getCreatedDate()));
		PDFUtils.setDefaultCellLayout(userCell);
		userCell.enableBorderSide(Rectangle.LEFT);
		userCell.enableBorderSide(Rectangle.RIGHT);
		workflowySectionTable.addCell(workflowyTableHeaderCell);
		workflowySectionTable.addCell(pdfCell1);

		HashMap<Long, LeaveApplicationWorkflow> workFlow = new HashMap<>();
		for (LeaveApplicationWorkflow leaveApplicationWorkflow : leaveApplicationVO.getLeaveApplicationWorkflows()) {

			workFlow.put(leaveApplicationWorkflow.getEmployee().getEmployeeId(), leaveApplicationWorkflow);

		}

		List<LeaveApplicationReviewer> leaveApplicationReviewers = new ArrayList<>(leaveApplicationVO.getLeaveApplicationReviewers());
		Collections.sort(leaveApplicationReviewers, new LeaveTypeComp());

		if (leaveApplicationReviewers.size() == 0) {
			userCell.enableBorderSide(Rectangle.BOTTOM);
		}
		workflowySectionTable.addCell(userCell);

		Integer snoCount = 2;
		Integer revCount = 1;

		String leaveRev1Status = "";
		String leaveRev2Status = "";
		String leaveRev3Status = "";
		int tempRevCount = 1;

		for (LeaveApplicationReviewer leaveApplicationReviewer : leaveApplicationReviewers) {
			LeaveApplicationWorkflow leaveApplicationWorkflow = workFlow.get(leaveApplicationReviewer.getEmployee().getEmployeeId());
			String status = "";
			if (leaveApplicationWorkflow != null) {
				status = leaveApplicationWorkflow.getLeaveStatusMaster().getLeaveStatusName();
			}
			if (tempRevCount == 1) {
				leaveRev1Status = status;
			}
			if (tempRevCount == 2) {
				leaveRev2Status = status;
			}
			if (tempRevCount == 3) {
				leaveRev3Status = status;
			}

			tempRevCount++;
		}

		int leaveAppRevListSize = leaveApplicationReviewers.size();
		for (LeaveApplicationReviewer leaveApplicationReviewer : leaveApplicationReviewers) {
			LeaveApplicationWorkflow leaveApplicationWorkflow = workFlow.get(leaveApplicationReviewer.getEmployee().getEmployeeId());
			String remarks = "";
			String status = "";
			String claimDate = "";
			if (leaveApplicationWorkflow != null) {
				remarks = leaveApplicationWorkflow.getRemarks();
				status = leaveApplicationWorkflow.getLeaveStatusMaster().getLeaveStatusName();
				claimDate = DateUtils.timeStampToString(leaveApplicationWorkflow.getCreatedDate());
			}

			if (!leaveApplicationVO.getLeaveStatusMaster().getLeaveStatusName().equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_WITHDRAWN)) {
				if (revCount == 1) {
					if (StringUtils.isBlank(leaveRev1Status)) {
						status = "Pending";
					}
				}
				if (revCount == 2) {
					if (StringUtils.isBlank(leaveRev2Status)) {
						if (StringUtils.isBlank(leaveRev1Status)) {
							status = "Pending";
						} else {
							if (leaveRev1Status.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_COMPLETED)) {
								status = "";
							}
						}

					}
				}

				if (revCount == 3) {
					if (StringUtils.isBlank(leaveRev3Status)) {
						if (StringUtils.isBlank(leaveRev1Status) && StringUtils.isBlank(leaveRev2Status)) {
							status = "Pending";
						} else {
							if (leaveRev2Status.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_COMPLETED)) {
								status = "";
							}
						}

					}
				}

			}

			PdfPCell leaveRevCell = createWorkFlowSectionTableData(snoCount.toString(), "Reviewer " + revCount,
					getEmployeeName(leaveApplicationReviewer.getEmployee()), remarks, status, claimDate);
			PDFUtils.setDefaultCellLayout(leaveRevCell);
			leaveRevCell.enableBorderSide(Rectangle.LEFT);
			leaveRevCell.enableBorderSide(Rectangle.RIGHT);
			if (leaveAppRevListSize == revCount) {
				leaveRevCell.enableBorderSide(Rectangle.BOTTOM);
			}
			workflowySectionTable.addCell(leaveRevCell);

			snoCount++;
			revCount++;
		}

		return workflowySectionTable;

	}

	private PdfPCell createWorkFlowSectionTableData(String leftColKey, String leftColVal, String middleColKey, String middleColVal, String rightColKey,
			String rightColVal) {
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

	private PdfPCell createCustomFieldTableData(List<LeaveCustomFieldDTO> customFields) {
		String key1 = "";
		String val1 = "";
		String key2 = "";
		String val2 = "";
		String key3 = "";
		String val3 = "";
		String key4 = "";
		String val4 = "";
		String key5 = "";
		String val5 = "";
		String key6 = "";
		String val6 = "";

		PdfPCell pdfPCell = new PdfPCell();
		pdfPCell.setBorder(0);

		PdfPTable pdfTable = new PdfPTable(new float[] { 1f, 1f, 1f, 1f });
		pdfTable.setWidthPercentage(100f);

		int countF = 1;
		for (LeaveCustomFieldDTO customFieldDTO : customFields) {
			if (countF == 1) {
				key1 = customFieldDTO.getCustomFieldName();
				val1 = customFieldDTO.getValue();
			}
			if (countF == 2) {
				key2 = customFieldDTO.getCustomFieldName();
				val2 = customFieldDTO.getValue();
			}
			if (countF == 3) {
				key3 = customFieldDTO.getCustomFieldName();
				val3 = customFieldDTO.getValue();
			}
			if (countF == 4) {
				key4 = customFieldDTO.getCustomFieldName();
				val4 = customFieldDTO.getValue();
			}
			if (countF == 5) {
				key5 = customFieldDTO.getCustomFieldName();
				val5 = customFieldDTO.getValue();
			}
			if (countF == 6) {
				key6 = customFieldDTO.getCustomFieldName();
				val6 = customFieldDTO.getValue();
			}

			countF++;
		}

		PdfPCell nestedLeftKeyCell = new PdfPCell(new Phrase(key1, getBaseFontNormal()));
		nestedLeftKeyCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);
		nestedLeftKeyCell.setBorder(0);

		PdfPCell nestedLeftValueCell = new PdfPCell(new Phrase(val1, getBaseFontNormal()));
		nestedLeftValueCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);
		nestedLeftValueCell.setBorder(0);

		PdfPCell nestedMiddleKeyCell = new PdfPCell(new Phrase(key2, getBaseFontNormal()));
		nestedMiddleKeyCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);
		nestedMiddleKeyCell.setBorder(0);

		PdfPCell nestedMiddleValueCell = new PdfPCell(new Phrase(val2, getBaseFontNormal()));
		nestedMiddleValueCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);
		nestedMiddleValueCell.setBorder(0);

		PdfPCell nestedRightKeyCell = new PdfPCell(new Phrase(key3, getBaseFontNormal()));
		nestedRightKeyCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);
		nestedRightKeyCell.setBorder(0);

		PdfPCell nestedRightValueCell = new PdfPCell(new Phrase(val3, getBaseFontNormal()));
		nestedRightValueCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);
		nestedRightValueCell.setBorder(0);

		PdfPCell nestedLeft1KeyCell = new PdfPCell(new Phrase(key4, getBaseFontNormal()));
		nestedLeft1KeyCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);
		nestedLeft1KeyCell.setBorder(0);

		PdfPCell nestedLeft1ValueCell = new PdfPCell(new Phrase(val4, getBaseFontNormal()));
		nestedLeft1ValueCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);
		nestedLeft1ValueCell.setBorder(0);

		PdfPCell nestedMiddle1KeyCell = new PdfPCell(new Phrase(key5, getBaseFontNormal()));
		nestedMiddle1KeyCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);
		nestedMiddle1KeyCell.setBorder(0);

		PdfPCell nestedMiddle1ValueCell = new PdfPCell(new Phrase(val5, getBaseFontNormal()));
		nestedMiddle1ValueCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);
		nestedMiddle1ValueCell.setBorder(0);

		PdfPCell nestedRight1KeyCell = new PdfPCell(new Phrase(key6, getBaseFontNormal()));
		nestedRight1KeyCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);
		nestedRight1KeyCell.setBorder(0);

		PdfPCell nestedRight1ValueCell = new PdfPCell(new Phrase(val6, getBaseFontNormal()));
		nestedRight1ValueCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);
		nestedRight1ValueCell.setBorder(0);

		pdfTable.addCell(nestedLeftKeyCell);
		pdfTable.addCell(nestedLeftValueCell);
		pdfTable.addCell(nestedMiddleKeyCell);
		pdfTable.addCell(nestedMiddleValueCell);
		if (StringUtils.isNotBlank(key3)) {
			pdfTable.addCell(nestedRightKeyCell);
			pdfTable.addCell(nestedRightValueCell);
			pdfTable.addCell(nestedLeft1KeyCell);
			pdfTable.addCell(nestedLeft1ValueCell);
		}
		if (StringUtils.isNotBlank(key5)) {

			pdfTable.addCell(nestedMiddle1KeyCell);
			pdfTable.addCell(nestedMiddle1ValueCell);
			pdfTable.addCell(nestedRight1KeyCell);
			pdfTable.addCell(nestedRight1ValueCell);
		}

		pdfPCell.addElement(pdfTable);
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
				bf = BaseFont.createFont(PayAsiaPDFConstants.PDF_MULTILINGUAL_FONT_PATH, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
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
				bf = BaseFont.createFont(PayAsiaPDFConstants.PDF_MULTILINGUAL_FONT_PATH, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
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
				bf = BaseFont.createFont(PayAsiaPDFConstants.PDF_MULTILINGUAL_FONT_PATH, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
			} catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
			}
		} catch (DocumentException e1) {
			LOGGER.error(e1.getMessage(), e1);
		}
		Font unicodeFont = new Font(bf, 11, Font.BOLD, BaseColor.BLACK);
		return unicodeFont;

	}
}
