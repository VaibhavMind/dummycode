package com.payasia.logic.excel;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.mind.payasia.xml.bean.workday.empdata.AdditionalInformationType;
import com.mind.payasia.xml.bean.workday.empdata.AddressLineType;
import com.mind.payasia.xml.bean.workday.empdata.AllowancePlansType;
import com.mind.payasia.xml.bean.workday.empdata.EarningsDeductionsType;
import com.mind.payasia.xml.bean.workday.empdata.EmployeeType;
import com.mind.payasia.xml.bean.workday.empdata.IdentifierType;
import com.mind.payasia.xml.bean.workday.empdata.PaymentElectionType;
import com.mind.payasia.xml.bean.workday.empdata.PersonalType;
import com.mind.payasia.xml.bean.workday.empdata.PositionType;
import com.mind.payasia.xml.bean.workday.empdata.SalaryAndHourlyPlansType;
import com.mind.payasia.xml.bean.workday.empdata.StatusType;
import com.mind.payasia.xml.bean.workday.empdata.SummaryType;
import com.payasia.common.dto.NewStarterSpreadsheet;
import com.payasia.common.dto.RecurringAllowances;
import com.payasia.common.dto.WorkDayReportDTO;
import com.payasia.common.dto.WorkDayUKNewStarterReportDTO;
import com.payasia.dao.bean.WorkdayPaygroupBatchData;

public class WorkDayExcelTemplateNewStarterUK extends WorkDayExcelTemplate {

	@Override
	public XSSFWorkbook generateReport(List<WorkDayReportDTO> workDayReportDTOList) {

		XSSFWorkbook workbook = new XSSFWorkbook();

		createSheetForNewStarter(workbook, workDayReportDTOList);

		return workbook;
	}

	@Override
	public List<WorkDayReportDTO> getReportDataMappedObject(JAXBElement employeeTypeElement,
			WorkdayPaygroupBatchData workdayPaygroupBatch) {

		EmployeeType employeeType = (EmployeeType) employeeTypeElement.getValue();
		List<WorkDayReportDTO> workDayReportDTOList = new ArrayList<WorkDayReportDTO>();
		WorkDayUKNewStarterReportDTO workDayUKNewStarterReport = new WorkDayUKNewStarterReportDTO();
		SummaryType summaryType = employeeType.getSummary();

		PersonalType personalType = employeeType.getPersonal() == null ? null : employeeType.getPersonal().getValue();
		StatusType statusType = employeeType.getStatus() == null ? null : employeeType.getStatus().getValue();

		List<PositionType> positionTypeList = employeeType.getPosition();
		List<SalaryAndHourlyPlansType> salaryAndHourlyPlansTypeList = employeeType.getSalaryAndHourlyPlans();
		List<AllowancePlansType> allowancePlansTypeList = employeeType.getAllowancePlans();
		List<PaymentElectionType> paymentElectionTypeList = employeeType.getPaymentElection();
		List<IdentifierType> identifierTypeList = employeeType.getIdentifier();
		List<AdditionalInformationType> additionalInformationTypeList = employeeType.getAdditionalInformation();
		List<EarningsDeductionsType> earningsDeductionsTypeList = employeeType.getEarningsDeductions();

		NewStarterSpreadsheet newStarterSpreadsheet = null;
		RecurringAllowances recurringAllowances = null;
		if (true) {
			if (summaryType != null) {
				newStarterSpreadsheet = new NewStarterSpreadsheet();
				newStarterSpreadsheet
						.setEeeRef(summaryType.getEmployeeID() == null ? "" : summaryType.getEmployeeID().getValue());
				newStarterSpreadsheet.setOldRTIRef(
						summaryType.getEmployeeID() == null ? "" : summaryType.getEmployeeID().getValue());
			}
			if (personalType != null) {

				newStarterSpreadsheet.setGender(
						personalType.getGender() == null ? "" : personalType.getGender().getValue().getValue());
				newStarterSpreadsheet
						.setTitle(personalType.getTitle() == null ? "" : personalType.getTitle().getValue().getValue());
				newStarterSpreadsheet.setForename(
						personalType.getFirstName() == null ? "" : personalType.getFirstName().getValue().getValue());
				newStarterSpreadsheet.setSurname(
						personalType.getLastName() == null ? "" : personalType.getLastName().getValue().getValue());
				List<AddressLineType> addressLineTypeList = personalType.getFirstAddressLineData();
				if (addressLineTypeList != null && !addressLineTypeList.isEmpty()) {
					int i = 0;
					for (AddressLineType addressLineType : addressLineTypeList) {
						/*
						 * if (addressLineType.getLabel() != null &&
						 * addressLineType.getLabel().equalsIgnoreCase(
						 * "ADDRESS_LINE_1")) {
						 * newHiresPHReport.setAddress(addressLineType.getValue(
						 * )); } if (addressLineType.getLabel() != null &&
						 * addressLineType.getLabel().equalsIgnoreCase(
						 * "ADDRESS_LINE_2")) {
						 * newHiresPHReport.setAddress2(addressLineType.getValue
						 * ()); } if (addressLineType.getLabel() != null &&
						 * addressLineType.getLabel().equalsIgnoreCase(
						 * "ADDRESS_LINE_3")) {
						 * newHiresPHReport.setAddress3(addressLineType.getValue
						 * ()); }
						 */
						if (addressLineType.getValue() != null && i == 0) {
							newStarterSpreadsheet.setAddress1(addressLineType.getValue());
						}
						if (addressLineType.getValue() != null && i == 1) {
							newStarterSpreadsheet.setAddress2(addressLineType.getValue());
						}
						if (addressLineType.getValue() != null && i == 2) {
							newStarterSpreadsheet.setAddress3(addressLineType.getValue());
						}
						if (addressLineType.getValue() != null && i == 3) {
							newStarterSpreadsheet.setAddress4(addressLineType.getValue());
						}
						i++;
					}
				}

				newStarterSpreadsheet.setPostcode(personalType.getFirstPostalCode() == null ? ""
						: personalType.getFirstPostalCode().getValue().getValue());
				newStarterSpreadsheet.setCountry(personalType.getFirstCountry() == null ? ""
						: personalType.getFirstCountry().getValue().getValue());
				newStarterSpreadsheet.setMaritalStatus(personalType.getMaritalStatus() == null ? ""
						: personalType.getMaritalStatus().getValue().getValue());
				newStarterSpreadsheet.setdOB(
						personalType.getBirthDate() == null ? "" : personalType.getBirthDate().getValue().getValue());
				newStarterSpreadsheet.setEmailAddress(personalType.getFirstEmailAddress() == null ? ""
						: personalType.getFirstEmailAddress().getValue().getValue());// moved
																						// above

			}
			newStarterSpreadsheet.setEmpStatus("E");
			if (statusType != null) {
				newStarterSpreadsheet.setStartDate(
						statusType.getHireDate() == null ? "" : statusType.getHireDate().getValue().getValue());
				newStarterSpreadsheet.setLeaveDate(statusType.getTerminationDate() == null ? ""
						: statusType.getTerminationDate().getValue().getValue());
				// newStarterSpreadsheet.setLeaver(leaver);
				// newStarterSpreadsheet.setP45FilingReq(p45FilingReq);
				// newStarterSpreadsheet.setDirstartdate(dirstartdate);
				// newStarterSpreadsheet.setBranch(branch);
				// newStarterSpreadsheet.setCostCentre(costCentre);
			}
			
			if (positionTypeList != null) {
				for (PositionType positionType : positionTypeList) {
					newStarterSpreadsheet.setDepartment(positionType.getOrganizationOne() == null ? ""
							: positionType.getOrganizationOne().getValue().getValue());
					newStarterSpreadsheet.setWorkingHours(positionType.getScheduledWeeklyHours()==null?"":positionType.getScheduledWeeklyHours().getValue().getValue());
				}
			}

			// newStarterSpreadsheet.setRunGroup(runGroup);
			// newStarterSpreadsheet.setDefaultCostSplit(defaultCostSplit);

			if (identifierTypeList != null && !identifierTypeList.isEmpty()) {
				for (IdentifierType identifierType : identifierTypeList) {
					if (identifierType.getOperation() != null
							&& !identifierType.getOperation().getValue().value().equalsIgnoreCase("REMOVE")) {
						if (identifierType.getIdentifierType() != null && identifierType.getIdentifierType().getValue()
								.getValue().equalsIgnoreCase("National_Insurance_Number"))
							newStarterSpreadsheet.setnINumber(identifierType.getIdentifierValue() == null ? ""
									: identifierType.getIdentifierValue().getValue().getValue());
					}
				}
			}
			// newStarterSpreadsheet.setnILetter(nILetter);
			// newStarterSpreadsheet.setTaxCode(taxCode);
			// newStarterSpreadsheet.setwK1M1(wK1M1);
			// newStarterSpreadsheet.setChangeType(changeType);

			newStarterSpreadsheet.setFrequency("12");
			// newStarterSpreadsheet.setPayMethod(payMethod);
			if (paymentElectionTypeList != null && !paymentElectionTypeList.isEmpty()) {
				for (PaymentElectionType paymentElectionType : paymentElectionTypeList) {
					newStarterSpreadsheet.setBankAccNo(paymentElectionType.getAccountNumber() == null ? ""
							: paymentElectionType.getAccountNumber().getValue().getValue());
					newStarterSpreadsheet.setBankAccName(paymentElectionType.getBankAccountName() == null ? ""
							: paymentElectionType.getBankAccountName().getValue().getValue());
					// newStarterSpreadsheet.setSortCode(sortCode);
					newStarterSpreadsheet.setBankName(paymentElectionType.getBankName() == null ? ""
							: paymentElectionType.getBankName().getValue().getValue());
				}
			}
			// newStarterSpreadsheet.setBranchName(branchName);
			// newStarterSpreadsheet.setBuildingSocRef(buildingSocRef);
			// newStarterSpreadsheet.setAutopayRef(autopayRef);
			// newStarterSpreadsheet.setStudentLoanPlan(studentLoanPlan);
			// newStarterSpreadsheet.setStudentStartDate(studentStartDate);
			// newStarterSpreadsheet.setStudentLoanEndDate(studentLoanEndDate);
			// newStarterSpreadsheet.setStudentLoanYTD(studentLoanYTD);
			// email moved above
			// newStarterSpreadsheet.setPassportNumber(passportNumber);
			newStarterSpreadsheet.setStartingDeclaration("O");
			newStarterSpreadsheet.setIrregularEmployment("Y");
			// newStarterSpreadsheet.setOmitfromRTI(omitfromRTI);
			// newStarterSpreadsheet.setPaymenttononindividual(paymenttononindividual);
			// OldRTIRef moved above

			if (salaryAndHourlyPlansTypeList != null && !salaryAndHourlyPlansTypeList.isEmpty()) {
				for (SalaryAndHourlyPlansType salaryAndHourlyPlansType : salaryAndHourlyPlansTypeList) {
					newStarterSpreadsheet.setAnnualSalary(salaryAndHourlyPlansType.getAmount() == null ? ""
							: salaryAndHourlyPlansType.getAmount().getValue().getValue());
				}
			}
			// newStarterSpreadsheet.setDaysWorked(daysWorked);
			// newStarterSpreadsheet.setCarAllowance(carAllowance);
			// newStarterSpreadsheet.setEmployeeLoan(employeeLoan);
			// newStarterSpreadsheet.setaOE(aOE);
			// newStarterSpreadsheet.setNotes(notes);
			workDayUKNewStarterReport.setNewStarterSpreadsheet(newStarterSpreadsheet);
		}
		if(earningsDeductionsTypeList!=null && !earningsDeductionsTypeList.isEmpty()){			
			int count = 0;
				for (EarningsDeductionsType earningsDeductionsType : earningsDeductionsTypeList) {
					if (earningsDeductionsType.getOperation() != null
							&& !earningsDeductionsType.getOperation().getValue().value().equalsIgnoreCase("REMOVE")) {
						if (count == 0) {
							recurringAllowances = new RecurringAllowances();
							if (summaryType != null) {
								recurringAllowances
										.setEeeRef(summaryType.getEmployeeID() == null ? "" : summaryType.getEmployeeID().getValue());
								String name = summaryType.getName() == null ? "" : summaryType.getName().getValue();
								String nameArr[] = name.split(" ");
								if (nameArr != null && nameArr.length > 0) {
									recurringAllowances.setForename(nameArr[0]);
									if (nameArr.length == 2) {
										recurringAllowances.setSurname(nameArr[1]);
									}
								}
							}
							recurringAllowances.setPayCode(earningsDeductionsType.getCode()==null?"":earningsDeductionsType.getCode().getValue().getValue());
							//recurringAllowances.setPayDescription(earningsDeductionsType.getCode()==null?"":earningsDeductionsType.getCode().getValue().getValue());
							recurringAllowances.setRecurringAmount(earningsDeductionsType.getAmount()==null?"":earningsDeductionsType.getAmount().getValue().getValue());
							String startDate=earningsDeductionsType.getStartDate()==null?"":earningsDeductionsType.getStartDate().getValue().getValue();
							if(startDate!=null && startDate.length()>=10){
								recurringAllowances.setStartYear(startDate.substring(0, 3));
								recurringAllowances.setStartMonth(startDate.substring(5, 6));
							}
							//recurringAllowances.setEndYear(endYear);
							//recurringAllowances.setEndMonth(endMonth);
							//recurringAllowances.setRemarks(remarks);
						}
						else{
							WorkDayUKNewStarterReportDTO workDayUKNewStarterReportExt = new WorkDayUKNewStarterReportDTO();
							RecurringAllowances recurringAllowancesExt = new RecurringAllowances();	
							if (summaryType != null) {
								recurringAllowancesExt
										.setEeeRef(summaryType.getEmployeeID() == null ? "" : summaryType.getEmployeeID().getValue());
								String name = summaryType.getName() == null ? "" : summaryType.getName().getValue();
								String nameArr[] = name.split(" ");
								if (nameArr != null && nameArr.length > 0) {
									recurringAllowancesExt.setForename(nameArr[0]);
									if (nameArr.length == 2) {
										recurringAllowancesExt.setSurname(nameArr[1]);
									} 
								}
							}		
							recurringAllowancesExt.setPayCode(earningsDeductionsType.getCode()==null?"":earningsDeductionsType.getCode().getValue().getValue());
							//recurringAllowancesExt.setPayDescription(earningsDeductionsType.getCode()==null?"":earningsDeductionsType.getCode().getValue().getValue());
							recurringAllowancesExt.setRecurringAmount(earningsDeductionsType.getAmount()==null?"":earningsDeductionsType.getAmount().getValue().getValue());
							String startDate=earningsDeductionsType.getStartDate()==null?"":earningsDeductionsType.getStartDate().getValue().getValue();
							if(startDate!=null && startDate.length()>=10){
								recurringAllowancesExt.setStartYear(startDate.substring(0, 3));
								recurringAllowancesExt.setStartMonth(startDate.substring(5, 6));
							}
							//recurringAllowancesExt.setEndYear(endYear);
							//recurringAllowancesExt.setEndMonth(endMonth);
							//recurringAllowancesExt.setRemarks(remarks);
							
							workDayUKNewStarterReportExt.setRecurringAllowances(recurringAllowancesExt);
							workDayReportDTOList.add(workDayUKNewStarterReportExt);
						}
						count++;
					}else{
						WorkDayUKNewStarterReportDTO workDayUKNewStarterReportExt = new WorkDayUKNewStarterReportDTO();
						RecurringAllowances recurringAllowancesExt = new RecurringAllowances();	
						if (summaryType != null) {
							recurringAllowancesExt
									.setEeeRef(summaryType.getEmployeeID() == null ? "" : summaryType.getEmployeeID().getValue());
							String name = summaryType.getName() == null ? "" : summaryType.getName().getValue();
							String nameArr[] = name.split(" ");
							if (nameArr != null && nameArr.length > 0) {
								recurringAllowancesExt.setForename(nameArr[0]);
								if (nameArr.length == 2) {
									recurringAllowancesExt.setSurname(nameArr[1]);
								} 
							}
						}		
						recurringAllowancesExt.setPayCode(earningsDeductionsType.getCode()==null?"":earningsDeductionsType.getCode().getValue().getValue());
						//recurringAllowancesExt.setPayDescription(earningsDeductionsType.getCode()==null?"":earningsDeductionsType.getCode().getValue().getValue());
						recurringAllowancesExt.setRecurringAmount(earningsDeductionsType.getAmount()==null?"":earningsDeductionsType.getAmount().getValue().getValue());
						//recurringAllowancesExt.setStartYear(endDate.substring(0, 3));
						//recurringAllowancesExt.setStartMonth(endDate.substring(5, 6));
					
						String endDate=earningsDeductionsType.getFirstDayNoLongerApplies()==null?"":earningsDeductionsType.getFirstDayNoLongerApplies().getValue().getValue();
						if(endDate!=null && endDate.length()>=10){
							recurringAllowancesExt.setEndYear(endDate.substring(0, 3));
							recurringAllowancesExt.setEndMonth(endDate.substring(5, 6));							
						}
						//recurringAllowancesExt.setRemarks(remarks);
						
						workDayUKNewStarterReportExt.setRecurringAllowances(recurringAllowancesExt);
						workDayReportDTOList.add(workDayUKNewStarterReportExt);
					}
				}			
		}
		workDayUKNewStarterReport.setRecurringAllowances(recurringAllowances);
		workDayReportDTOList.add(workDayUKNewStarterReport);
		return workDayReportDTOList;
	}

	private void createSheetForNewStarter(XSSFWorkbook workbook, List<WorkDayReportDTO> workDayReportDTOList) {

		XSSFSheet sheet1 = workbook.createSheet("New Starter Spreadsheet ");
		//sheet1.setTabColor(3);

		createSheetForNewStarterSpreadsheet(workbook, sheet1);

		XSSFSheet sheet2 = workbook.createSheet("Recurring Allowances");
		//sheet2.setTabColor(3);

		createSheetForRecurringAllowances(workbook, sheet2);
		int rowCount1 = 2, rowCount2 = 2;

		if (workDayReportDTOList != null && !workDayReportDTOList.isEmpty()) {

			for (WorkDayReportDTO workDayReportDTO : workDayReportDTOList) {

				WorkDayUKNewStarterReportDTO workDayUKNewStarterReportDTO = (WorkDayUKNewStarterReportDTO) workDayReportDTO;

				if (workDayUKNewStarterReportDTO.getNewStarterSpreadsheet() != null) {
					XSSFRow rowsheet1 = sheet1.createRow((short) rowCount1++);
					rowsheet1.setHeightInPoints((short) 20);
					setNewStarterSpreadsheet(workbook, rowsheet1, workDayUKNewStarterReportDTO);
				}
				if (workDayUKNewStarterReportDTO.getRecurringAllowances() != null) {
					XSSFRow rowsheet2 = sheet2.createRow((short) rowCount2++);
					rowsheet2.setHeightInPoints((short) 20);
					setRecurringAllowances(workbook, rowsheet2, workDayUKNewStarterReportDTO);
				}
			}
		}
	}

	private void createSheetForNewStarterSpreadsheet(XSSFWorkbook workbook, XSSFSheet sheet) {

		XSSFFont font3Row = workbook.createFont();
		font3Row.setFontName("Calibri");
		font3Row.setColor(HSSFColor.WHITE.index);
		font3Row.setItalic(false);
		font3Row.setBold(true);
		font3Row.setFontHeightInPoints((short) 11);

		XSSFCellStyle stylefont3Row = workbook.createCellStyle();
		stylefont3Row.setFont(font3Row);
		stylefont3Row.setFillForegroundColor(new XSSFColor(new java.awt.Color(39, 72, 128)));
		stylefont3Row.setFillPattern(CellStyle.SOLID_FOREGROUND);
		stylefont3Row.setAlignment(HorizontalAlignment.CENTER);

		XSSFRow row0 = sheet.createRow((short) 0);
		row0.setHeightInPoints((short) 14);

		XSSFCell cell3A = (XSSFCell) row0.createCell((short) 0);
		cell3A.setCellStyle(stylefont3Row);
		cell3A.setCellValue("E'ee Ref");

		sheet.setColumnWidth(0, 4200);

		XSSFCell cell3B = (XSSFCell) row0.createCell((short) 1);
		cell3B.setCellStyle(stylefont3Row);
		cell3B.setCellValue("Gender");

		sheet.setColumnWidth(1, 4200);

		XSSFCell cell3C = (XSSFCell) row0.createCell((short) 2);
		cell3C.setCellStyle(stylefont3Row);
		cell3C.setCellValue("Title");

		sheet.setColumnWidth(2, 4200);

		XSSFCell cell3D = (XSSFCell) row0.createCell((short) 3);
		cell3D.setCellStyle(stylefont3Row);
		cell3D.setCellValue("Forename");

		sheet.setColumnWidth(3, 4200);

		XSSFCell cell3E = (XSSFCell) row0.createCell((short) 4);
		cell3E.setCellStyle(stylefont3Row);
		cell3E.setCellValue("Surname");

		sheet.setColumnWidth(4, 4200);

		XSSFCell cell3F = (XSSFCell) row0.createCell((short) 5);
		cell3F.setCellStyle(stylefont3Row);
		cell3F.setCellValue("Address 1");

		sheet.setColumnWidth(5, 4200);

		XSSFCell cell3G = (XSSFCell) row0.createCell((short) 6);
		cell3G.setCellStyle(stylefont3Row);
		cell3G.setCellValue("Address 2");

		sheet.setColumnWidth(6, 4200);

		XSSFCell cell3H = (XSSFCell) row0.createCell((short) 7);
		cell3H.setCellStyle(stylefont3Row);
		cell3H.setCellValue("Address 3");

		sheet.setColumnWidth(7, 4200);

		XSSFCell cell3I = (XSSFCell) row0.createCell((short) 8);
		cell3I.setCellStyle(stylefont3Row);
		cell3I.setCellValue("Address 4");

		sheet.setColumnWidth(8, 4200);

		XSSFCell cell3J = (XSSFCell) row0.createCell((short) 9);
		cell3J.setCellStyle(stylefont3Row);
		cell3J.setCellValue("Postcode");

		sheet.setColumnWidth(9, 4200);

		XSSFCell cell3K = (XSSFCell) row0.createCell((short) 10);
		cell3K.setCellStyle(stylefont3Row);
		cell3K.setCellValue("Country");

		sheet.setColumnWidth(10, 4200);

		XSSFCell cell3L = (XSSFCell) row0.createCell((short) 11);
		cell3L.setCellStyle(stylefont3Row);
		cell3L.setCellValue("Marital Status");

		sheet.setColumnWidth(11, 4200);

		XSSFCell cell3M = (XSSFCell) row0.createCell((short) 12);
		cell3M.setCellStyle(stylefont3Row);
		cell3M.setCellValue("DOB");

		sheet.setColumnWidth(12, 4200);

		XSSFCell cell3N = (XSSFCell) row0.createCell((short) 13);
		cell3N.setCellStyle(stylefont3Row);
		cell3N.setCellValue("Emp Status");

		sheet.setColumnWidth(13, 4200);

		XSSFCell cell3O = (XSSFCell) row0.createCell((short) 14);
		cell3O.setCellStyle(stylefont3Row);
		cell3O.setCellValue("Start Date");

		sheet.setColumnWidth(14, 4200);

		XSSFCell cell3P = (XSSFCell) row0.createCell((short) 15);
		cell3P.setCellStyle(stylefont3Row);
		cell3P.setCellValue("Leave Date");

		sheet.setColumnWidth(15, 4200);

		XSSFCell cell3Q = (XSSFCell) row0.createCell((short) 16);
		cell3Q.setCellStyle(stylefont3Row);
		cell3Q.setCellValue("Leaver");

		sheet.setColumnWidth(16, 4200);

		XSSFCell cell3R = (XSSFCell) row0.createCell((short) 17);
		cell3R.setCellStyle(stylefont3Row);
		cell3R.setCellValue("P45 Filing Req");

		sheet.setColumnWidth(17, 4200);

		XSSFCell cell3S = (XSSFCell) row0.createCell((short) 18);
		cell3S.setCellStyle(stylefont3Row);
		cell3S.setCellValue("Dir start date");

		sheet.setColumnWidth(18, 4200);

		XSSFCell cell3T = (XSSFCell) row0.createCell((short) 19);
		cell3T.setCellStyle(stylefont3Row);
		cell3T.setCellValue("Branch");

		sheet.setColumnWidth(19, 4200);

		XSSFCell cell3U = (XSSFCell) row0.createCell((short) 20);
		cell3U.setCellStyle(stylefont3Row);
		cell3U.setCellValue("Cost Centre");

		sheet.setColumnWidth(20, 4200);

		XSSFCell cell3V = (XSSFCell) row0.createCell((short) 21);
		cell3V.setCellStyle(stylefont3Row);
		cell3V.setCellValue("Department");

		sheet.setColumnWidth(21, 4200);

		XSSFCell cell3W = (XSSFCell) row0.createCell((short) 22);
		cell3W.setCellStyle(stylefont3Row);
		cell3W.setCellValue("Run Group");

		sheet.setColumnWidth(22, 4200);

		XSSFCell cell3X = (XSSFCell) row0.createCell((short) 23);
		cell3X.setCellStyle(stylefont3Row);
		cell3X.setCellValue("Default Cost Split");

		sheet.setColumnWidth(23, 4200);

		XSSFCell cell3Y = (XSSFCell) row0.createCell((short) 24);
		cell3Y.setCellStyle(stylefont3Row);
		cell3Y.setCellValue("NI Number");

		sheet.setColumnWidth(24, 4200);

		XSSFCell cell0Z = (XSSFCell) row0.createCell((short) 25);
		cell0Z.setCellValue("NI Letter");
		cell0Z.setCellStyle(stylefont3Row);

		sheet.setColumnWidth(25, 4500);

		XSSFCell cell0AA = (XSSFCell) row0.createCell((short) 26);
		cell0AA.setCellValue("Tax Code");
		cell0AA.setCellStyle(stylefont3Row);

		sheet.setColumnWidth(26, 4500);

		XSSFCell cell0AB = (XSSFCell) row0.createCell((short) 27);
		cell0AB.setCellValue("WK1/M1");
		cell0AB.setCellStyle(stylefont3Row);

		sheet.setColumnWidth(27, 4500);

		XSSFCell cell0AC = (XSSFCell) row0.createCell((short) 28);
		cell0AC.setCellValue("Change Type");
		cell0AC.setCellStyle(stylefont3Row);

		sheet.setColumnWidth(28, 4500);

		XSSFCell cell0AD = (XSSFCell) row0.createCell((short) 29);
		cell0AD.setCellValue("Frequency");
		cell0AD.setCellStyle(stylefont3Row);

		sheet.setColumnWidth(29, 4500);

		XSSFCell cell0AE = (XSSFCell) row0.createCell((short) 30);
		cell0AE.setCellValue("Pay Method");
		cell0AE.setCellStyle(stylefont3Row);

		sheet.setColumnWidth(30, 4500);

		XSSFCell cell0AF = (XSSFCell) row0.createCell((short) 31);
		cell0AF.setCellValue("Bank Acc No");
		cell0AF.setCellStyle(stylefont3Row);

		sheet.setColumnWidth(31, 4500);

		XSSFCell cell0AG = (XSSFCell) row0.createCell((short) 32);
		cell0AG.setCellValue("Bank Acc Name");
		cell0AG.setCellStyle(stylefont3Row);

		sheet.setColumnWidth(32, 4500);

		XSSFCell cell0AH = (XSSFCell) row0.createCell((short) 33);
		cell0AH.setCellValue("Sort Code");
		cell0AH.setCellStyle(stylefont3Row);

		sheet.setColumnWidth(33, 4500);

		XSSFCell cell0AI = (XSSFCell) row0.createCell((short) 34);
		cell0AI.setCellValue("Bank Name");
		cell0AI.setCellStyle(stylefont3Row);

		sheet.setColumnWidth(34, 4500);

		XSSFCell cell0AJ = (XSSFCell) row0.createCell((short) 35);
		cell0AJ.setCellValue("Branch Name");
		cell0AJ.setCellStyle(stylefont3Row);

		sheet.setColumnWidth(35, 4500);

		XSSFCell cell0AK = (XSSFCell) row0.createCell((short) 36);
		cell0AK.setCellValue("Building Soc Ref");
		cell0AK.setCellStyle(stylefont3Row);

		sheet.setColumnWidth(36, 4500);

		XSSFCell cell0AL = (XSSFCell) row0.createCell((short) 37);
		cell0AL.setCellValue("Autopay Ref");
		cell0AL.setCellStyle(stylefont3Row);

		sheet.setColumnWidth(37, 4500);

		XSSFCell cell0AM = (XSSFCell) row0.createCell((short) 38);
		cell0AM.setCellValue("Student Loan Plan");
		cell0AM.setCellStyle(stylefont3Row);

		sheet.setColumnWidth(38, 4500);

		XSSFCell cell0AN = (XSSFCell) row0.createCell((short) 39);
		cell0AN.setCellValue("Student Start Date");
		cell0AN.setCellStyle(stylefont3Row);

		sheet.setColumnWidth(39, 4500);

		XSSFCell cell0AO = (XSSFCell) row0.createCell((short) 40);
		cell0AO.setCellValue("Student Loan End Date");
		cell0AO.setCellStyle(stylefont3Row);

		sheet.setColumnWidth(40, 4500);

		XSSFCell cell0AP = (XSSFCell) row0.createCell((short) 41);
		cell0AP.setCellValue("Student Loan YTD");
		cell0AP.setCellStyle(stylefont3Row);

		sheet.setColumnWidth(41, 4500);

		XSSFCell cell0AQ = (XSSFCell) row0.createCell((short) 42);
		cell0AQ.setCellValue("Email Address");
		cell0AQ.setCellStyle(stylefont3Row);

		sheet.setColumnWidth(42, 4500);

		XSSFCell cell0AR = (XSSFCell) row0.createCell((short) 43);
		cell0AR.setCellValue("Passport Number");
		cell0AR.setCellStyle(stylefont3Row);

		sheet.setColumnWidth(43, 4500);

		XSSFCell cell0AS = (XSSFCell) row0.createCell((short) 44);
		cell0AS.setCellValue("Starting Declaration");
		cell0AS.setCellStyle(stylefont3Row);

		sheet.setColumnWidth(44, 4500);

		XSSFCell cell0AT = (XSSFCell) row0.createCell((short) 45);
		cell0AT.setCellValue("Irregular Employment");
		cell0AT.setCellStyle(stylefont3Row);

		sheet.setColumnWidth(45, 4500);

		XSSFCell cell0AU = (XSSFCell) row0.createCell((short) 46);
		cell0AU.setCellValue("Omit from RTI");
		cell0AU.setCellStyle(stylefont3Row);

		sheet.setColumnWidth(46, 4500);

		XSSFCell cell0AV = (XSSFCell) row0.createCell((short) 47);
		cell0AV.setCellValue("Payment to non individual");
		cell0AV.setCellStyle(stylefont3Row);

		sheet.setColumnWidth(47, 4500);

		XSSFCell cell0AW = (XSSFCell) row0.createCell((short) 48);
		cell0AW.setCellValue("Old RTI Ref (Same as E'ee Ref)");
		cell0AW.setCellStyle(stylefont3Row);

		sheet.setColumnWidth(48, 4500);

		XSSFCell cell0AX = (XSSFCell) row0.createCell((short) 49);
		cell0AX.setCellValue("Annual Salary");
		cell0AX.setCellStyle(stylefont3Row);

		sheet.setColumnWidth(49, 4500);

		XSSFCell cell0AY = (XSSFCell) row0.createCell((short) 50);
		cell0AY.setCellValue("Working Hours");
		cell0AY.setCellStyle(stylefont3Row);

		sheet.setColumnWidth(50, 4500);

		XSSFCell cell0AZ = (XSSFCell) row0.createCell((short) 51);
		cell0AZ.setCellValue("Days Worked");
		cell0AZ.setCellStyle(stylefont3Row);

		sheet.setColumnWidth(51, 4500);

		XSSFCell cell0BA = (XSSFCell) row0.createCell((short) 52);
		cell0BA.setCellValue("Car Allowance (P.A)");
		cell0BA.setCellStyle(stylefont3Row);

		sheet.setColumnWidth(52, 4500);

		XSSFCell cell0BB = (XSSFCell) row0.createCell((short) 53);
		cell0BB.setCellValue("Employee Loan");
		cell0BB.setCellStyle(stylefont3Row);

		sheet.setColumnWidth(53, 4500);

		XSSFCell cell0BC = (XSSFCell) row0.createCell((short) 54);
		cell0BC.setCellValue("AOE");
		cell0BC.setCellStyle(stylefont3Row);

		sheet.setColumnWidth(54, 4500);

		XSSFCell cell0BD = (XSSFCell) row0.createCell((short) 55);
		cell0BD.setCellValue("Notes");
		cell0BD.setCellStyle(stylefont3Row);

		sheet.setColumnWidth(55, 4500);

	}

	private void setNewStarterSpreadsheet(XSSFWorkbook workbook, XSSFRow row0,
			WorkDayUKNewStarterReportDTO workDayUKNewStarterReportDTO) {

		XSSFFont font = workbook.createFont();
		font.setFontName("Calibri");
		font.setItalic(false);
		font.setColor(HSSFColor.BLACK.index);
		font.setBold(true);
		font.setFontHeightInPoints((short) 14);

		NewStarterSpreadsheet newStarterSpreadsheet = workDayUKNewStarterReportDTO.getNewStarterSpreadsheet();

		XSSFCellStyle style = workbook.createCellStyle();
		style.setFont(font);

		XSSFCell cell0A = (XSSFCell) row0.createCell((short) 0);
		cell0A.setCellValue(newStarterSpreadsheet.getEeeRef());
		cell0A.setCellStyle(style);

		XSSFCell cell0B = (XSSFCell) row0.createCell((short) 1);
		cell0B.setCellValue(newStarterSpreadsheet.getGender());
		cell0B.setCellStyle(style);

		XSSFCell cell0C = (XSSFCell) row0.createCell((short) 2);
		cell0C.setCellValue(newStarterSpreadsheet.getTitle());
		cell0C.setCellStyle(style);

		XSSFCell cell0D = (XSSFCell) row0.createCell((short) 3);
		cell0D.setCellValue(newStarterSpreadsheet.getForename());
		cell0D.setCellStyle(style);

		XSSFCell cell0E = (XSSFCell) row0.createCell((short) 4);
		cell0E.setCellValue(newStarterSpreadsheet.getSurname());
		cell0E.setCellStyle(style);

		XSSFCell cell0F = (XSSFCell) row0.createCell((short) 5);
		cell0F.setCellValue(newStarterSpreadsheet.getAddress1());
		cell0F.setCellStyle(style);

		XSSFCell cell0G = (XSSFCell) row0.createCell((short) 6);
		cell0G.setCellValue(newStarterSpreadsheet.getAddress2());
		cell0G.setCellStyle(style);

		XSSFCell cell0H = (XSSFCell) row0.createCell((short) 7);
		cell0H.setCellValue(newStarterSpreadsheet.getAddress3());
		cell0H.setCellStyle(style);

		XSSFCell cell0I = (XSSFCell) row0.createCell((short) 8);
		cell0I.setCellValue(newStarterSpreadsheet.getAddress4());
		cell0I.setCellStyle(style);

		XSSFCell cell0J = (XSSFCell) row0.createCell((short) 9);
		cell0J.setCellValue(newStarterSpreadsheet.getPostcode());
		cell0J.setCellStyle(style);

		XSSFCell cell0K = (XSSFCell) row0.createCell((short) 10);
		cell0K.setCellValue(newStarterSpreadsheet.getCountry());
		cell0K.setCellStyle(style);

		XSSFCell cell0L = (XSSFCell) row0.createCell((short) 11);
		cell0L.setCellValue(newStarterSpreadsheet.getMaritalStatus());
		cell0L.setCellStyle(style);

		XSSFCell cell0M = (XSSFCell) row0.createCell((short) 12);
		cell0M.setCellValue(newStarterSpreadsheet.getdOB());
		cell0M.setCellStyle(style);

		XSSFCell cell0N = (XSSFCell) row0.createCell((short) 13);
		cell0N.setCellValue(newStarterSpreadsheet.getEmpStatus());
		cell0N.setCellStyle(style);

		XSSFCell cell0O = (XSSFCell) row0.createCell((short) 14);
		cell0O.setCellValue(newStarterSpreadsheet.getStartDate());
		cell0O.setCellStyle(style);

		XSSFCell cell0P = (XSSFCell) row0.createCell((short) 15);
		cell0P.setCellValue(newStarterSpreadsheet.getLeaveDate());
		cell0P.setCellStyle(style);

		XSSFCell cell0Q = (XSSFCell) row0.createCell((short) 16);
		cell0Q.setCellValue(newStarterSpreadsheet.getLeaver());
		cell0Q.setCellStyle(style);

		XSSFCell cell0R = (XSSFCell) row0.createCell((short) 17);
		cell0R.setCellValue(newStarterSpreadsheet.getP45FilingReq());
		cell0R.setCellStyle(style);

		XSSFCell cell0S = (XSSFCell) row0.createCell((short) 18);
		cell0S.setCellValue(newStarterSpreadsheet.getDirstartdate());
		cell0S.setCellStyle(style);

		XSSFCell cell0T = (XSSFCell) row0.createCell((short) 19);
		cell0T.setCellValue(newStarterSpreadsheet.getBranch());
		cell0T.setCellStyle(style);

		XSSFCell cell0U = (XSSFCell) row0.createCell((short) 20);
		cell0U.setCellValue(newStarterSpreadsheet.getCostCentre());
		cell0U.setCellStyle(style);

		XSSFCell cell0V = (XSSFCell) row0.createCell((short) 21);
		cell0V.setCellValue(newStarterSpreadsheet.getDepartment());
		cell0V.setCellStyle(style);

		XSSFCell cell0W = (XSSFCell) row0.createCell((short) 22);
		cell0W.setCellValue(newStarterSpreadsheet.getRunGroup());
		cell0W.setCellStyle(style);

		XSSFCell cell0X = (XSSFCell) row0.createCell((short) 23);
		cell0X.setCellValue(newStarterSpreadsheet.getDefaultCostSplit());
		cell0X.setCellStyle(style);

		XSSFCell cell0Y = (XSSFCell) row0.createCell((short) 24);
		cell0Y.setCellValue(newStarterSpreadsheet.getnINumber());
		cell0Y.setCellStyle(style);

		XSSFCell cell0Z = (XSSFCell) row0.createCell((short) 25);
		cell0Z.setCellValue(newStarterSpreadsheet.getnILetter());
		cell0Z.setCellStyle(style);

		XSSFCell cell0AA = (XSSFCell) row0.createCell((short) 26);
		cell0AA.setCellValue(newStarterSpreadsheet.getTaxCode());
		cell0AA.setCellStyle(style);

		XSSFCell cell0AB = (XSSFCell) row0.createCell((short) 27);
		cell0AB.setCellValue(newStarterSpreadsheet.getwK1M1());
		cell0AB.setCellStyle(style);

		XSSFCell cell0AC = (XSSFCell) row0.createCell((short) 28);
		cell0AC.setCellValue(newStarterSpreadsheet.getChangeType());
		cell0AC.setCellStyle(style);

		XSSFCell cell0AD = (XSSFCell) row0.createCell((short) 29);
		cell0AD.setCellValue(newStarterSpreadsheet.getFrequency());
		cell0AD.setCellStyle(style);

		XSSFCell cell0AE = (XSSFCell) row0.createCell((short) 30);
		cell0AE.setCellValue(newStarterSpreadsheet.getPayMethod());
		cell0AE.setCellStyle(style);

		XSSFCell cell0AF = (XSSFCell) row0.createCell((short) 31);
		cell0AF.setCellValue(newStarterSpreadsheet.getBankAccNo());
		cell0AF.setCellStyle(style);

		XSSFCell cell0AG = (XSSFCell) row0.createCell((short) 32);
		cell0AG.setCellValue(newStarterSpreadsheet.getBankAccName());
		cell0AG.setCellStyle(style);

		XSSFCell cell0AH = (XSSFCell) row0.createCell((short) 33);
		cell0AH.setCellValue(newStarterSpreadsheet.getSortCode());
		cell0AH.setCellStyle(style);

		XSSFCell cell0AI = (XSSFCell) row0.createCell((short) 34);
		cell0AI.setCellValue(newStarterSpreadsheet.getBankName());
		cell0AI.setCellStyle(style);

		XSSFCell cell0AJ = (XSSFCell) row0.createCell((short) 35);
		cell0AJ.setCellValue(newStarterSpreadsheet.getBranchName());
		cell0AJ.setCellStyle(style);

		XSSFCell cell0AK = (XSSFCell) row0.createCell((short) 36);
		cell0AK.setCellValue(newStarterSpreadsheet.getBuildingSocRef());
		cell0AK.setCellStyle(style);

		XSSFCell cell0AL = (XSSFCell) row0.createCell((short) 37);
		cell0AL.setCellValue(newStarterSpreadsheet.getAutopayRef());
		cell0AL.setCellStyle(style);

		XSSFCell cell0AM = (XSSFCell) row0.createCell((short) 38);
		cell0AM.setCellValue(newStarterSpreadsheet.getStudentLoanPlan());
		cell0AM.setCellStyle(style);

		XSSFCell cell0AN = (XSSFCell) row0.createCell((short) 39);
		cell0AN.setCellValue(newStarterSpreadsheet.getStudentStartDate());
		cell0AN.setCellStyle(style);

		XSSFCell cell0AO = (XSSFCell) row0.createCell((short) 40);
		cell0AO.setCellValue(newStarterSpreadsheet.getStudentLoanEndDate());
		cell0AO.setCellStyle(style);

		XSSFCell cell0AP = (XSSFCell) row0.createCell((short) 41);
		cell0AP.setCellValue(newStarterSpreadsheet.getStudentLoanYTD());
		cell0AP.setCellStyle(style);

		XSSFCell cell0AQ = (XSSFCell) row0.createCell((short) 42);
		cell0AQ.setCellValue(newStarterSpreadsheet.getEmailAddress());
		cell0AQ.setCellStyle(style);

		XSSFCell cell0AR = (XSSFCell) row0.createCell((short) 43);
		cell0AR.setCellValue(newStarterSpreadsheet.getPassportNumber());
		cell0AR.setCellStyle(style);

		XSSFCell cell0AS = (XSSFCell) row0.createCell((short) 44);
		cell0AS.setCellValue(newStarterSpreadsheet.getStartingDeclaration());
		cell0AS.setCellStyle(style);

		XSSFCell cell0AT = (XSSFCell) row0.createCell((short) 45);
		cell0AT.setCellValue(newStarterSpreadsheet.getIrregularEmployment());
		cell0AT.setCellStyle(style);

		XSSFCell cell0AU = (XSSFCell) row0.createCell((short) 46);
		cell0AU.setCellValue(newStarterSpreadsheet.getOmitfromRTI());
		cell0AU.setCellStyle(style);

		XSSFCell cell0AV = (XSSFCell) row0.createCell((short) 47);
		cell0AV.setCellValue(newStarterSpreadsheet.getPaymenttononindividual());
		cell0AV.setCellStyle(style);

		XSSFCell cell0AW = (XSSFCell) row0.createCell((short) 48);
		cell0AW.setCellValue(newStarterSpreadsheet.getOldRTIRef());
		cell0AW.setCellStyle(style);

		XSSFCell cell0AX = (XSSFCell) row0.createCell((short) 49);
		cell0AX.setCellValue(newStarterSpreadsheet.getAnnualSalary());
		cell0AX.setCellStyle(style);

		XSSFCell cell0AY = (XSSFCell) row0.createCell((short) 50);
		cell0AY.setCellValue(newStarterSpreadsheet.getWorkingHours());
		cell0AY.setCellStyle(style);

		XSSFCell cell0AZ = (XSSFCell) row0.createCell((short) 51);
		cell0AZ.setCellValue(newStarterSpreadsheet.getDaysWorked());
		cell0AZ.setCellStyle(style);

		XSSFCell cell0BA = (XSSFCell) row0.createCell((short) 52);
		cell0BA.setCellValue(newStarterSpreadsheet.getCarAllowance());
		cell0BA.setCellStyle(style);

		XSSFCell cell0BB = (XSSFCell) row0.createCell((short) 53);
		cell0BB.setCellValue(newStarterSpreadsheet.getEmployeeLoan());
		cell0BB.setCellStyle(style);

		XSSFCell cell0BC = (XSSFCell) row0.createCell((short) 54);
		cell0BC.setCellValue(newStarterSpreadsheet.getaOE());
		cell0BC.setCellStyle(style);

		XSSFCell cell0BD = (XSSFCell) row0.createCell((short) 55);
		cell0BD.setCellValue(newStarterSpreadsheet.getNotes());
		cell0BD.setCellStyle(style);
	}

	private void createSheetForRecurringAllowances(XSSFWorkbook workbook, XSSFSheet sheet) {

		XSSFFont font3Row = workbook.createFont();
		font3Row.setFontName("Calibri");
		font3Row.setColor(HSSFColor.WHITE.index);
		font3Row.setItalic(false);
		font3Row.setBold(true);
		font3Row.setFontHeightInPoints((short) 11);

		XSSFCellStyle stylefont3Row = workbook.createCellStyle();
		stylefont3Row.setFont(font3Row);
		stylefont3Row.setFillForegroundColor(new XSSFColor(new java.awt.Color(39, 72, 128)));
		stylefont3Row.setFillPattern(CellStyle.SOLID_FOREGROUND);
		stylefont3Row.setAlignment(HorizontalAlignment.CENTER);

		XSSFRow row0 = sheet.createRow((short) 0);
		row0.setHeightInPoints((short) 14);

		XSSFCell cell3A = (XSSFCell) row0.createCell((short) 0);
		cell3A.setCellStyle(stylefont3Row);
		cell3A.setCellValue(" E'ee Ref");

		sheet.setColumnWidth(0, 4200);

		XSSFCell cell3B = (XSSFCell) row0.createCell((short) 1);
		cell3B.setCellStyle(stylefont3Row);
		cell3B.setCellValue("Forename");

		sheet.setColumnWidth(1, 4200);

		XSSFCell cell3C = (XSSFCell) row0.createCell((short) 2);
		cell3C.setCellStyle(stylefont3Row);
		cell3C.setCellValue("Surname");

		sheet.setColumnWidth(2, 4200);

		XSSFCell cell3D = (XSSFCell) row0.createCell((short) 3);
		cell3D.setCellStyle(stylefont3Row);
		cell3D.setCellValue("Pay Code");

		sheet.setColumnWidth(3, 4200);

		XSSFCell cell3E = (XSSFCell) row0.createCell((short) 4);
		cell3E.setCellStyle(stylefont3Row);
		cell3E.setCellValue("Pay Description");

		sheet.setColumnWidth(4, 4200);

		XSSFCell cell3F = (XSSFCell) row0.createCell((short) 5);
		cell3F.setCellStyle(stylefont3Row);
		cell3F.setCellValue("Recurring Amount");

		sheet.setColumnWidth(5, 4200);

		XSSFCell cell3G = (XSSFCell) row0.createCell((short) 6);
		cell3G.setCellStyle(stylefont3Row);
		cell3G.setCellValue("Start Year");

		sheet.setColumnWidth(6, 4200);

		XSSFCell cell3H = (XSSFCell) row0.createCell((short) 7);
		cell3H.setCellStyle(stylefont3Row);
		cell3H.setCellValue("Start Month");

		sheet.setColumnWidth(7, 4200);

		XSSFCell cell3I = (XSSFCell) row0.createCell((short) 8);
		cell3I.setCellStyle(stylefont3Row);
		cell3I.setCellValue("End Year");

		sheet.setColumnWidth(8, 4200);

		XSSFCell cell3J = (XSSFCell) row0.createCell((short) 9);
		cell3J.setCellStyle(stylefont3Row);
		cell3J.setCellValue("End Month");

		sheet.setColumnWidth(9, 4200);

		XSSFCell cell3K = (XSSFCell) row0.createCell((short) 10);
		cell3K.setCellStyle(stylefont3Row);
		cell3K.setCellValue("Remarks");

		sheet.setColumnWidth(10, 4200);
	}

	private void setRecurringAllowances(XSSFWorkbook workbook, XSSFRow row0,
			WorkDayUKNewStarterReportDTO workDayUKNewStarterReportDTO) {
		XSSFFont font = workbook.createFont();
		font.setFontName("Calibri");
		font.setItalic(false);
		font.setColor(HSSFColor.BLACK.index);
		font.setBold(true);
		font.setFontHeightInPoints((short) 14);

		RecurringAllowances recurringAllowances = workDayUKNewStarterReportDTO.getRecurringAllowances();

		XSSFCellStyle style = workbook.createCellStyle();
		style.setFont(font);

		XSSFCell cell0A = (XSSFCell) row0.createCell((short) 0);
		cell0A.setCellValue(recurringAllowances.getEeeRef());
		cell0A.setCellStyle(style);

		XSSFCell cell0B = (XSSFCell) row0.createCell((short) 1);
		cell0B.setCellValue(recurringAllowances.getForename());
		cell0B.setCellStyle(style);

		XSSFCell cell0C = (XSSFCell) row0.createCell((short) 2);
		cell0C.setCellValue(recurringAllowances.getSurname());
		cell0C.setCellStyle(style);

		XSSFCell cell0D = (XSSFCell) row0.createCell((short) 3);
		cell0D.setCellValue(recurringAllowances.getPayCode());
		cell0D.setCellStyle(style);

		XSSFCell cell0E = (XSSFCell) row0.createCell((short) 4);
		cell0E.setCellValue(recurringAllowances.getPayDescription());
		cell0E.setCellStyle(style);

		XSSFCell cell0F = (XSSFCell) row0.createCell((short) 5);
		cell0F.setCellValue(recurringAllowances.getRecurringAmount());
		cell0F.setCellStyle(style);

		XSSFCell cell0G = (XSSFCell) row0.createCell((short) 6);
		cell0G.setCellValue(recurringAllowances.getStartYear());
		cell0G.setCellStyle(style);

		XSSFCell cell0H = (XSSFCell) row0.createCell((short) 7);
		cell0H.setCellValue(recurringAllowances.getStartMonth());
		cell0H.setCellStyle(style);

		XSSFCell cell0I = (XSSFCell) row0.createCell((short) 8);
		cell0I.setCellValue(recurringAllowances.getEndYear());
		cell0I.setCellStyle(style);

		XSSFCell cell0J = (XSSFCell) row0.createCell((short) 9);
		cell0J.setCellValue(recurringAllowances.getEndMonth());
		cell0J.setCellStyle(style);

		XSSFCell cell0K = (XSSFCell) row0.createCell((short) 10);
		cell0K.setCellValue(recurringAllowances.getRemarks());
		cell0K.setCellStyle(style);

	}
}
