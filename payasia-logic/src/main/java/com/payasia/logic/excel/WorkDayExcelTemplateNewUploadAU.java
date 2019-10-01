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
import com.mind.payasia.xml.bean.workday.empdata.EarningsDeductionsType;
import com.mind.payasia.xml.bean.workday.empdata.EmployeeType;
import com.mind.payasia.xml.bean.workday.empdata.IdentifierType;
import com.mind.payasia.xml.bean.workday.empdata.PaymentElectionType;
import com.mind.payasia.xml.bean.workday.empdata.PersonalType;
import com.mind.payasia.xml.bean.workday.empdata.PositionType;
import com.mind.payasia.xml.bean.workday.empdata.SalaryAndHourlyPlansType;
import com.mind.payasia.xml.bean.workday.empdata.StatusType;
import com.mind.payasia.xml.bean.workday.empdata.SummaryType;
import com.payasia.common.dto.WorkDayAUReportDTO;
import com.payasia.common.dto.WorkDayReportDTO;
import com.payasia.dao.bean.WorkdayPaygroupBatchData;

public class WorkDayExcelTemplateNewUploadAU extends WorkDayExcelTemplate {

	@Override
	public XSSFWorkbook generateReport(List<WorkDayReportDTO> workDayReportDTOList) {

		XSSFWorkbook workbook = new XSSFWorkbook();
		createSheetForEmployee(workbook, workDayReportDTOList);

		return workbook;
	}

	@Override
	public List<WorkDayReportDTO> getReportDataMappedObject(JAXBElement employeeTypeElement,
			WorkdayPaygroupBatchData workdayPaygroupBatch) {
		try {
			List<WorkDayReportDTO> workDayAUReportDTOList = new ArrayList<WorkDayReportDTO>();
			EmployeeType employeeType = (EmployeeType) employeeTypeElement.getValue();
			if (employeeType != null) {

				SummaryType summaryType = employeeType.getSummary();

				PersonalType personalType = employeeType.getPersonal() == null ? null
						: employeeType.getPersonal().getValue();

				StatusType statusType = employeeType.getStatus() == null ? null : employeeType.getStatus().getValue();

				List<PositionType> positionTypeList = employeeType.getPosition();
				List<SalaryAndHourlyPlansType> salaryAndHourlyPlansTypeList = employeeType.getSalaryAndHourlyPlans();
				List<IdentifierType> identifierTypeList = employeeType.getIdentifier();
				List<PaymentElectionType> paymentElectionTypeList = employeeType.getPaymentElection();
				List<AdditionalInformationType> additionalInformationTypeList = employeeType.getAdditionalInformation();
				List<EarningsDeductionsType> earningsDeductionsTypeList = employeeType.getEarningsDeductions();

				WorkDayAUReportDTO workDayAUReportDTO = new WorkDayAUReportDTO();
				if (summaryType != null) {
					workDayAUReportDTO.setEmployeeId(
							summaryType.getEmployeeID() == null ? "" : summaryType.getEmployeeID().getValue());
				}

				if (identifierTypeList != null && !identifierTypeList.isEmpty()) {
					for (IdentifierType identifierType : identifierTypeList) {
						if (identifierType.getOperation() != null
								&& !identifierType.getOperation().getValue().value().equalsIgnoreCase("REMOVE")) {
							workDayAUReportDTO.setTaxFileNumber(identifierType.getIdentifierValue() == null ? ""
									: identifierType.getIdentifierValue().getValue().getValue());
						}
					}
				}

				if (personalType != null) {
					workDayAUReportDTO.setTitle(
							personalType.getTitle() == null ? "" : personalType.getTitle().getValue().getValue());
					// workDayAUReportDTO.setPreferredName("");
					workDayAUReportDTO.setFirstName(personalType.getFirstName() == null ? ""
							: personalType.getFirstName().getValue().getValue());
					workDayAUReportDTO.setMiddleName(personalType.getMiddleName() == null ? ""
							: personalType.getMiddleName().getValue().getValue());
					workDayAUReportDTO.setSurname(
							personalType.getLastName() == null ? "" : personalType.getLastName().getValue().getValue());
					workDayAUReportDTO.setDateOfBirth(personalType.getBirthDate() == null ? ""
							: personalType.getBirthDate().getValue().getValue());
					workDayAUReportDTO.setGender(
							personalType.getGender() == null ? "" : personalType.getGender().getValue().getValue());
				}
				if (summaryType != null) {
					workDayAUReportDTO.setExternalId(
							summaryType.getEmployeeID() == null ? "" : summaryType.getEmployeeID().getValue());
				}
				if (personalType != null) {
					List<AddressLineType> addressLineTypeList = personalType.getFirstAddressLineData();
					if (addressLineTypeList != null && !addressLineTypeList.isEmpty()) {
						for (AddressLineType addressLineType : addressLineTypeList) {
							/*
							 * if (addressLineType.getLabel() != null &&
							 * addressLineType.getLabel().equalsIgnoreCase(
							 * "ADDRESS_LINE_1")) {
							 * 
							 * }
							 */

							workDayAUReportDTO.setResidentialStreetAddress(addressLineType.getValue());
						}
					}
					// workDayAUReportDTO.setResidentialAddressLine2("");
					workDayAUReportDTO.setResidentialSuburb(personalType.getFirstMunicipality() == null ? ""
							: personalType.getFirstMunicipality().getValue().getValue());
					workDayAUReportDTO.setResidentialState(personalType.getFirstRegion() == null ? ""
							: personalType.getFirstRegion().getValue().getValue());
					workDayAUReportDTO.setResidentialPostCode(personalType.getFirstPostalCode() == null ? ""
							: personalType.getFirstPostalCode().getValue().getValue());
					// workDayAUReportDTO.setPostalStreetAddress("");
					// workDayAUReportDTO.setPostalAddressLine2("");
					// workDayAUReportDTO.setPostalSuburb("");
					// workDayAUReportDTO.setPostalState("");
					// workDayAUReportDTO.setPostalPostCode("");
					workDayAUReportDTO.setEmailAddress(personalType.getFirstEmailAddress() == null ? ""
							: personalType.getFirstEmailAddress().getValue().getValue());
					// workDayAUReportDTO.setHomePhone("");
					// workDayAUReportDTO.setWorkPhone("");
					workDayAUReportDTO.setMobilePhone(personalType.getPhoneNumber() == null ? ""
							: personalType.getPhoneNumber().getValue().getValue());
				}

				if (statusType != null) {
					workDayAUReportDTO.setStartDate(
							statusType.getHireDate() == null ? "" : statusType.getHireDate().getValue().getValue());
					workDayAUReportDTO.setEndDate(statusType.getTerminationDate() == null ? ""
							: statusType.getTerminationDate().getValue().getValue());
					workDayAUReportDTO.setDateTaxFileDeclarationSigned(
							statusType.getHireDate() == null ? "" : statusType.getHireDate().getValue().getValue());
				}

				// workDayAUReportDTO.setAnniversaryDate("");
				// workDayAUReportDTO.setTags("");
				// workDayAUReportDTO.setEmployingEntityABN("");
				if (positionTypeList != null && !positionTypeList.isEmpty()) {
					for (PositionType positionType : positionTypeList) {
						if (positionType.getOperation() != null
								&& !positionType.getOperation().getValue().value().equalsIgnoreCase("REMOVE")) {
							if (additionalInformationTypeList != null && !additionalInformationTypeList.isEmpty()) {
								for (AdditionalInformationType additionalInformationType : additionalInformationTypeList) {
									workDayAUReportDTO.setEmploymentType(
											additionalInformationType.getEmploymentType() == null ? ""
													: additionalInformationType.getEmploymentType().getValue()
															.getValue());
								}
							}
							workDayAUReportDTO.setJobTitle(positionType.getBusinessTitle() == null ? ""
									: positionType.getBusinessTitle().getValue().getValue());

						}
					}
				}

				if (additionalInformationTypeList != null && !additionalInformationTypeList.isEmpty()) {
					for (AdditionalInformationType additionalInformationType : additionalInformationTypeList) {
						workDayAUReportDTO.setPrimaryLocation(additionalInformationType.getPrimaryLocation() == null
								? "" : additionalInformationType.getPrimaryLocation().getValue().getValue());
						// workDayAUReportDTO.setPreviousSurname("");
						workDayAUReportDTO
								.setAustralianResident(additionalInformationType.getTFNAustralianResident() == null ? ""
										: additionalInformationType.getTFNAustralianResident().getValue().getValue());
						workDayAUReportDTO.setClaimTaxFreeThreshold(
								additionalInformationType.getTFNClaimTaxFreeThreshold() == null ? ""
										: additionalInformationType.getTFNClaimTaxFreeThreshold().getValue()
												.getValue());
						workDayAUReportDTO
								.setSeniorsTaxOffset(additionalInformationType.getTFNSeniorsTaxOffset() == null ? ""
										: additionalInformationType.getTFNSeniorsTaxOffset().getValue().getValue());
						workDayAUReportDTO.setOtherTaxOffset(additionalInformationType.getTFNOtherTaxOffset() == null
								? "" : additionalInformationType.getTFNOtherTaxOffset().getValue().getValue());
						workDayAUReportDTO.setHelpDebt(additionalInformationType.getTFNHelpDebt() == null ? ""
								: additionalInformationType.getTFNHelpDebt().getValue().getValue());
						workDayAUReportDTO.setAFSDebt(additionalInformationType.getTFNAFSDebt() == null ? ""
								: additionalInformationType.getTFNAFSDebt().getValue().getValue());
						workDayAUReportDTO.setAutomaticallyPayEmployee(
								additionalInformationType.getAutomaticallyPayEmployee() == null ? ""
										: additionalInformationType.getAutomaticallyPayEmployee().getValue()
												.getValue());
						workDayAUReportDTO.setIsEnabledForTimesheets(
								additionalInformationType.getIsEnabledForTimesheets() == null ? ""
										: additionalInformationType.getIsEnabledForTimesheets().getValue().getValue());
						workDayAUReportDTO.setPaySchedule(additionalInformationType.getPaySchedule() == null ? ""
								: additionalInformationType.getPaySchedule().getValue().getValue());
						workDayAUReportDTO
								.setPrimaryPayCategory(additionalInformationType.getPrimaryPayCategory() == null ? ""
										: additionalInformationType.getPrimaryPayCategory().getValue().getValue());
					}
				}

				// workDayAUReportDTO.setIsExemptFromFloodLevy("");
				// workDayAUReportDTO.setHasApprovedWorkingHolidayVisa("");
				// workDayAUReportDTO.setHasWithholdingVariation("");
				// workDayAUReportDTO.setTaxVariation("");
				// workDayAUReportDTO.setMedicareLevyExemption("");
				// workDayAUReportDTO.setDateTaxFileDeclarationSigned(""); --
				// set above
				// workDayAUReportDTO.setDateTaxFileDeclarationReported("");
				// workDayAUReportDTO.setJobTitle(""); -- set above

				// workDayAUReportDTO.setPaySchedule("");--set above
				// workDayAUReportDTO.setPrimaryPayCategory("");--set above

				// workDayAUReportDTO.setPrimaryLocation(""); --set above
				// workDayAUReportDTO.setPaySlipNotificationType("");

				if (salaryAndHourlyPlansTypeList != null && !salaryAndHourlyPlansTypeList.isEmpty()) {
					for (SalaryAndHourlyPlansType salaryAndHourlyPlansType : salaryAndHourlyPlansTypeList) {
						if (salaryAndHourlyPlansType.getOperation() != null && !salaryAndHourlyPlansType.getOperation()
								.getValue().getValue().equalsIgnoreCase("REMOVE")) {
							workDayAUReportDTO.setRateUnit(salaryAndHourlyPlansType.getFrequency() == null ? ""
									: salaryAndHourlyPlansType.getFrequency().getValue().getValue());
							workDayAUReportDTO.setRate(salaryAndHourlyPlansType.getAmount() == null ? ""
									: String.valueOf(salaryAndHourlyPlansType.getAmount().getValue().getValue()));
						}
					}
				}

				// workDayAUReportDTO.setOverrideTemplateRate("");

				if (positionTypeList != null && !positionTypeList.isEmpty()) {
					for (PositionType positionType : positionTypeList) {
						if (positionType.getOperation() != null
								&& !positionType.getOperation().getValue().value().equalsIgnoreCase("REMOVE")) {
							workDayAUReportDTO.setHoursPerWeek(positionType.getScheduledWeeklyHours() == null ? ""
									: positionType.getScheduledWeeklyHours().getValue().getValue());
							// moved above
							// workDayAUReportDTO.setLeaveTemplate("");
							// workDayAUReportDTO.setPayRateTemplate("");
							// workDayAUReportDTO.setPayConditionRuleSet("");
							// workDayAUReportDTO.setEmploymentAgreement("");
							// moved above
						}
					}
				}

				// workDayAUReportDTO.setIsExemptFromPayrollTax("");
				// workDayAUReportDTO.setLocations("");
				// workDayAUReportDTO.setWorkTypes("");
				// workDayAUReportDTO.setEmergencyContact1_Name("");
				// workDayAUReportDTO.setEmergencyContact1_Relationship("");
				// workDayAUReportDTO.setEmergencyContact1_Address("");
				// workDayAUReportDTO.setEmergencyContact1_ContactNumber("");
				// workDayAUReportDTO.setEmergencyContact1_AlternateContactNumber("");
				// workDayAUReportDTO.setEmergencyContact2_Name("");
				// workDayAUReportDTO.setEmergencyContact2_Relationship("");
				// workDayAUReportDTO.setEmergencyContact2_Address("");
				// workDayAUReportDTO.setEmergencyContact2_ContactNumber("");
				// workDayAUReportDTO.setEmergencyContact2_AlternateContactNumber("");

				if (paymentElectionTypeList != null && !paymentElectionTypeList.isEmpty()) {
					for (PaymentElectionType paymentElectionType : paymentElectionTypeList) {
						if (paymentElectionType.getOperation() != null
								&& !paymentElectionType.getOperation().getValue().value().equalsIgnoreCase("REMOVE")) {
							workDayAUReportDTO.setBankAccount1_BSB(paymentElectionType.getBankIDNumber() == null ? ""
									: paymentElectionType.getBankIDNumber().getValue().getValue());
							workDayAUReportDTO
									.setBankAccount1_AccountNumber(paymentElectionType.getAccountNumber() == null ? ""
											: paymentElectionType.getAccountNumber().getValue().getValue());
							workDayAUReportDTO
									.setBankAccount1_AccountName(paymentElectionType.getBankAccountName() == null ? ""
											: paymentElectionType.getBankAccountName().getValue().getValue());
						}
					}
				}

				// workDayAUReportDTO.setBankAccount1_AllocatedPercentage("");
				// workDayAUReportDTO.setBankAccount1_FixedAmount("");
				// workDayAUReportDTO.setBankAccount2_BSB("");
				// workDayAUReportDTO.setBankAccount2_AccountNumber("");
				// workDayAUReportDTO.setBankAccount2_AccountName("");
				// workDayAUReportDTO.setBankAccount2_AllocatedPercentage("");
				// workDayAUReportDTO.setBankAccount2_FixedAmount("");
				// workDayAUReportDTO.setBankAccount3_BSB("");
				// workDayAUReportDTO.setBankAccount3_AccountNumber("");
				// workDayAUReportDTO.setBankAccount3_AccountName("");
				// workDayAUReportDTO.setBankAccount3_AllocatedPercentage("");
				// workDayAUReportDTO.setBankAccount3_FixedAmount("");

				if (additionalInformationTypeList != null && !additionalInformationTypeList.isEmpty()) {
					for (AdditionalInformationType additionalInformationType : additionalInformationTypeList) {
						workDayAUReportDTO
								.setSuperFund1_ProductCode(additionalInformationType.getSuperProductCode() == null ? ""
										: additionalInformationType.getSuperProductCode().getValue().getValue());
						workDayAUReportDTO.setSuperFund1_FundName(additionalInformationType.getSuperFundName() == null
								? "" : additionalInformationType.getSuperFundName().getValue().getValue());
						workDayAUReportDTO
								.setSuperFund1_MemberNumber(additionalInformationType.getSuperMemberNumber() == null
										? "" : additionalInformationType.getSuperMemberNumber().getValue().getValue());
					}
				}

				// workDayAUReportDTO.setSuperFund1_AllocatedPercentage("");
				// workDayAUReportDTO.setSuperFund1_FixedAmount("");
				// workDayAUReportDTO.setSuperFund2_ProductCode("");
				// workDayAUReportDTO.setSuperFund2_FundName("");
				// workDayAUReportDTO.setSuperFund2_MemberNumber("");
				// workDayAUReportDTO.setSuperFund2_AllocatedPercentage("");
				// workDayAUReportDTO.setSuperFund2_FixedAmount("");
				// workDayAUReportDTO.setSuperFund3_ProductCode("");
				// workDayAUReportDTO.setSuperFund3_FundName("");
				// workDayAUReportDTO.setSuperFund3_MemberNumber("");
				// workDayAUReportDTO.setSuperFund3_AllocatedPercentage("");
				// workDayAUReportDTO.setSuperFund3_FixedAmount("");
				// workDayAUReportDTO.setSuperThresholdAmount("");
				// workDayAUReportDTO.setMaximumQuarterlySuperContributionsBase("");
				// workDayAUReportDTO.setRosteringNotificationChoices("");

				workDayAUReportDTOList.add(workDayAUReportDTO);
			}

			return workDayAUReportDTOList;
		} catch (Exception e) {

		}
		return null;
	}

	private void createSheetForEmployee(XSSFWorkbook workbook, List<WorkDayReportDTO> workDayReportDTOList) {

		XSSFSheet sheet = workbook.createSheet("Export");

		createSheetForNewUpload(workbook, sheet);

		if (workDayReportDTOList != null && !workDayReportDTOList.isEmpty()) {

			for (int rowNo = 0; rowNo < workDayReportDTOList.size(); rowNo++) {
				XSSFRow row = sheet.createRow((short) rowNo + 1);
				row.setHeightInPoints((short) 25);
				WorkDayAUReportDTO workDayAUReportDTO = (WorkDayAUReportDTO) workDayReportDTOList.get(rowNo);
				setEmployeeDataToSheet(workbook, row, workDayAUReportDTO);
			}
		}
	}

	private void createSheetForNewUpload(XSSFWorkbook workbook, XSSFSheet sheet) {

		XSSFFont font = workbook.createFont();
		font.setFontName("Calibri");
		font.setItalic(false);
		font.setColor(HSSFColor.BLACK.index);
		font.setBold(true);
		font.setFontHeightInPoints((short) 12);

		XSSFCellStyle style = workbook.createCellStyle();
		style.setFont(font);
		style.setFillForegroundColor(new XSSFColor(new java.awt.Color(76, 183, 76)));
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		style.setAlignment(HorizontalAlignment.CENTER);

		XSSFRow row0 = sheet.createRow((short) 0);
		row0.setHeightInPoints((short) 25);

		XSSFCell cell0A = (XSSFCell) row0.createCell((short) 0);
		cell0A.setCellValue("EmployeeId");
		cell0A.setCellStyle(style);

		sheet.setColumnWidth(0, 4500);

		XSSFCell cell0B = (XSSFCell) row0.createCell((short) 1);
		cell0B.setCellValue("TaxFileNumber");
		cell0B.setCellStyle(style);

		sheet.setColumnWidth(1, 4500);

		XSSFCell cell0C = (XSSFCell) row0.createCell((short) 2);
		cell0C.setCellValue("Title");
		cell0C.setCellStyle(style);

		sheet.setColumnWidth(2, 4500);

		XSSFCell cell0D = (XSSFCell) row0.createCell((short) 3);
		cell0D.setCellValue("PreferredName");
		cell0D.setCellStyle(style);

		sheet.setColumnWidth(3, 4500);

		XSSFCell cell0E = (XSSFCell) row0.createCell((short) 4);
		cell0E.setCellValue("FirstName");
		cell0E.setCellStyle(style);

		sheet.setColumnWidth(4, 4500);

		XSSFCell cell0F = (XSSFCell) row0.createCell((short) 5);
		cell0F.setCellValue("MiddleName");
		cell0F.setCellStyle(style);

		sheet.setColumnWidth(5, 4500);

		XSSFCell cell0G = (XSSFCell) row0.createCell((short) 6);
		cell0G.setCellValue("Surname");
		cell0G.setCellStyle(style);

		sheet.setColumnWidth(6, 4500);

		XSSFCell cell0H = (XSSFCell) row0.createCell((short) 7);
		cell0H.setCellValue("DateOfBirth");
		cell0H.setCellStyle(style);

		sheet.setColumnWidth(7, 4500);

		XSSFCell cell0I = (XSSFCell) row0.createCell((short) 8);
		cell0I.setCellValue("Gender");
		cell0I.setCellStyle(style);

		sheet.setColumnWidth(8, 4500);

		XSSFCell cell0J = (XSSFCell) row0.createCell((short) 9);
		cell0J.setCellValue("ExternalId");
		cell0J.setCellStyle(style);

		sheet.setColumnWidth(9, 4500);

		XSSFCell cell0K = (XSSFCell) row0.createCell((short) 10);
		cell0K.setCellValue("ResidentialStreetAddress");
		cell0K.setCellStyle(style);

		sheet.setColumnWidth(10, 4500);

		XSSFCell cell0L = (XSSFCell) row0.createCell((short) 11);
		cell0L.setCellValue("ResidentialAddressLine2");
		cell0L.setCellStyle(style);

		sheet.setColumnWidth(11, 4500);

		XSSFCell cell0M = (XSSFCell) row0.createCell((short) 12);
		cell0M.setCellValue("ResidentialSuburb");
		cell0M.setCellStyle(style);

		sheet.setColumnWidth(12, 4500);

		XSSFCell cell0N = (XSSFCell) row0.createCell((short) 13);
		cell0N.setCellValue("ResidentialState");
		cell0N.setCellStyle(style);

		sheet.setColumnWidth(13, 4500);

		XSSFCell cell0O = (XSSFCell) row0.createCell((short) 14);
		cell0O.setCellValue("ResidentialPostCode");
		cell0O.setCellStyle(style);

		sheet.setColumnWidth(14, 4500);

		XSSFCell cell0P = (XSSFCell) row0.createCell((short) 15);
		cell0P.setCellValue("PostalStreetAddress");
		cell0P.setCellStyle(style);

		sheet.setColumnWidth(15, 4500);

		XSSFCell cell0Q = (XSSFCell) row0.createCell((short) 16);
		cell0Q.setCellValue("PostalAddressLine2");
		cell0Q.setCellStyle(style);

		sheet.setColumnWidth(16, 4500);

		XSSFCell cell0R = (XSSFCell) row0.createCell((short) 17);
		cell0R.setCellValue("PostalSuburb");
		cell0R.setCellStyle(style);

		sheet.setColumnWidth(17, 4500);

		XSSFCell cell0S = (XSSFCell) row0.createCell((short) 18);
		cell0S.setCellValue("PostalState");
		cell0S.setCellStyle(style);

		sheet.setColumnWidth(18, 4500);

		XSSFCell cell0T = (XSSFCell) row0.createCell((short) 19);
		cell0T.setCellValue("PostalPostCode");
		cell0T.setCellStyle(style);

		sheet.setColumnWidth(19, 4500);

		XSSFCell cell0U = (XSSFCell) row0.createCell((short) 20);
		cell0U.setCellValue("EmailAddress");
		cell0U.setCellStyle(style);

		sheet.setColumnWidth(20, 4500);

		XSSFCell cell0V = (XSSFCell) row0.createCell((short) 21);
		cell0V.setCellValue("HomePhone");
		cell0V.setCellStyle(style);

		sheet.setColumnWidth(21, 4500);

		XSSFCell cell0W = (XSSFCell) row0.createCell((short) 22);
		cell0W.setCellValue("WorkPhone");
		cell0W.setCellStyle(style);

		sheet.setColumnWidth(22, 4500);

		XSSFCell cell0X = (XSSFCell) row0.createCell((short) 23);
		cell0X.setCellValue("MobilePhone");
		cell0X.setCellStyle(style);

		sheet.setColumnWidth(23, 4500);

		XSSFCell cell0Y = (XSSFCell) row0.createCell((short) 24);
		cell0Y.setCellValue("StartDate");
		cell0Y.setCellStyle(style);

		sheet.setColumnWidth(24, 4500);

		XSSFCell cell0Z = (XSSFCell) row0.createCell((short) 25);
		cell0Z.setCellValue("EndDate");
		cell0Z.setCellStyle(style);

		sheet.setColumnWidth(25, 4500);

		XSSFCell cell0AA = (XSSFCell) row0.createCell((short) 26);
		cell0AA.setCellValue("AnniversaryDate");
		cell0AA.setCellStyle(style);

		sheet.setColumnWidth(26, 4500);

		XSSFCell cell0AB = (XSSFCell) row0.createCell((short) 27);
		cell0AB.setCellValue("Tags");
		cell0AB.setCellStyle(style);

		sheet.setColumnWidth(27, 4500);

		XSSFCell cell0AC = (XSSFCell) row0.createCell((short) 28);
		cell0AC.setCellValue("EmployingEntityABN");
		cell0AC.setCellStyle(style);

		sheet.setColumnWidth(28, 4500);

		XSSFCell cell0AD = (XSSFCell) row0.createCell((short) 29);
		cell0AD.setCellValue("EmploymentType");
		cell0AD.setCellStyle(style);

		sheet.setColumnWidth(29, 4500);

		XSSFCell cell0AE = (XSSFCell) row0.createCell((short) 30);
		cell0AE.setCellValue("PreviousSurname");
		cell0AE.setCellStyle(style);

		sheet.setColumnWidth(30, 4500);

		XSSFCell cell0AF = (XSSFCell) row0.createCell((short) 31);
		cell0AF.setCellValue("AustralianResident");
		cell0AF.setCellStyle(style);

		sheet.setColumnWidth(31, 4500);

		XSSFCell cell0AG = (XSSFCell) row0.createCell((short) 32);
		cell0AG.setCellValue("ClaimTaxFreeThreshold");
		cell0AG.setCellStyle(style);

		sheet.setColumnWidth(32, 4500);

		XSSFCell cell0AH = (XSSFCell) row0.createCell((short) 33);
		cell0AH.setCellValue("SeniorsTaxOffset");
		cell0AH.setCellStyle(style);

		sheet.setColumnWidth(33, 4500);

		XSSFCell cell0AI = (XSSFCell) row0.createCell((short) 34);
		cell0AI.setCellValue("OtherTaxOffset");
		cell0AI.setCellStyle(style);

		sheet.setColumnWidth(34, 4500);

		XSSFCell cell0AJ = (XSSFCell) row0.createCell((short) 35);
		cell0AJ.setCellValue("HelpDebt");
		cell0AJ.setCellStyle(style);

		sheet.setColumnWidth(35, 4500);

		XSSFCell cell0AK = (XSSFCell) row0.createCell((short) 36);
		cell0AK.setCellValue("AFSDebt");
		cell0AK.setCellStyle(style);

		sheet.setColumnWidth(36, 4500);

		XSSFCell cell0AL = (XSSFCell) row0.createCell((short) 37);
		cell0AL.setCellValue("IsExemptFromFloodLevy");
		cell0AL.setCellStyle(style);

		sheet.setColumnWidth(37, 4500);

		XSSFCell cell0AM = (XSSFCell) row0.createCell((short) 38);
		cell0AM.setCellValue("HasApprovedWorkingHolidayVisa");
		cell0AM.setCellStyle(style);

		sheet.setColumnWidth(38, 4500);

		XSSFCell cell0AN = (XSSFCell) row0.createCell((short) 39);
		cell0AN.setCellValue("HasWithholdingVariation");
		cell0AN.setCellStyle(style);

		sheet.setColumnWidth(39, 4500);

		XSSFCell cell0AO = (XSSFCell) row0.createCell((short) 40);
		cell0AO.setCellValue("TaxVariation");
		cell0AO.setCellStyle(style);

		sheet.setColumnWidth(40, 4500);

		XSSFCell cell0AP = (XSSFCell) row0.createCell((short) 41);
		cell0AP.setCellValue("MedicareLevyExemption");
		cell0AP.setCellStyle(style);

		sheet.setColumnWidth(41, 4500);

		XSSFCell cell0AQ = (XSSFCell) row0.createCell((short) 42);
		cell0AQ.setCellValue("DateTaxFileDeclarationSigned");
		cell0AQ.setCellStyle(style);

		sheet.setColumnWidth(42, 4500);

		XSSFCell cell0AR = (XSSFCell) row0.createCell((short) 43);
		cell0AR.setCellValue("DateTaxFileDeclarationReported");
		cell0AR.setCellStyle(style);

		sheet.setColumnWidth(43, 4500);

		XSSFCell cell0AS = (XSSFCell) row0.createCell((short) 44);
		cell0AS.setCellValue("JobTitle");
		cell0AS.setCellStyle(style);

		sheet.setColumnWidth(44, 4500);

		XSSFCell cell0AT = (XSSFCell) row0.createCell((short) 45);
		cell0AT.setCellValue("PaySchedule");
		cell0AT.setCellStyle(style);

		sheet.setColumnWidth(45, 4500);

		XSSFCell cell0AU = (XSSFCell) row0.createCell((short) 46);
		cell0AU.setCellValue("PrimaryPayCategory");
		cell0AU.setCellStyle(style);

		sheet.setColumnWidth(46, 4500);

		XSSFCell cell0AV = (XSSFCell) row0.createCell((short) 47);
		cell0AV.setCellValue("PrimaryLocation");
		cell0AV.setCellStyle(style);

		sheet.setColumnWidth(47, 4500);

		XSSFCell cell0AW = (XSSFCell) row0.createCell((short) 48);
		cell0AW.setCellValue("PaySlipNotificationType");
		cell0AW.setCellStyle(style);

		sheet.setColumnWidth(48, 4500);

		XSSFCell cell0AX = (XSSFCell) row0.createCell((short) 49);
		cell0AX.setCellValue("Rate");
		cell0AX.setCellStyle(style);

		sheet.setColumnWidth(49, 4500);

		XSSFCell cell0AY = (XSSFCell) row0.createCell((short) 50);
		cell0AY.setCellValue("RateUnit");
		cell0AY.setCellStyle(style);

		sheet.setColumnWidth(50, 4500);

		XSSFCell cell0AZ = (XSSFCell) row0.createCell((short) 51);
		cell0AZ.setCellValue("OverrideTemplateRate");
		cell0AZ.setCellStyle(style);

		sheet.setColumnWidth(51, 4500);

		XSSFCell cell0BA = (XSSFCell) row0.createCell((short) 52);
		cell0BA.setCellValue("HoursPerWeek");
		cell0BA.setCellStyle(style);

		sheet.setColumnWidth(52, 4500);

		XSSFCell cell0BB = (XSSFCell) row0.createCell((short) 53);
		cell0BB.setCellValue("AutomaticallyPayEmployee");
		cell0BB.setCellStyle(style);

		sheet.setColumnWidth(53, 4500);

		XSSFCell cell0BC = (XSSFCell) row0.createCell((short) 54);
		cell0BC.setCellValue("LeaveTemplate");
		cell0BC.setCellStyle(style);

		sheet.setColumnWidth(54, 4500);

		XSSFCell cell0BD = (XSSFCell) row0.createCell((short) 55);
		cell0BD.setCellValue("PayRateTemplate");
		cell0BD.setCellStyle(style);

		sheet.setColumnWidth(55, 4500);

		XSSFCell cell0BE = (XSSFCell) row0.createCell((short) 56);
		cell0BE.setCellValue("PayConditionRuleSet");
		cell0BE.setCellStyle(style);

		sheet.setColumnWidth(56, 4500);

		XSSFCell cell0BF = (XSSFCell) row0.createCell((short) 57);
		cell0BF.setCellValue("EmploymentAgreement");
		cell0BF.setCellStyle(style);

		sheet.setColumnWidth(57, 4500);

		XSSFCell cell0BG = (XSSFCell) row0.createCell((short) 58);
		cell0BG.setCellValue("IsEnabledForTimesheets");
		cell0BG.setCellStyle(style);

		sheet.setColumnWidth(58, 4500);

		XSSFCell cell0BH = (XSSFCell) row0.createCell((short) 59);
		cell0BH.setCellValue("IsExemptFromPayrollTax");
		cell0BH.setCellStyle(style);

		sheet.setColumnWidth(59, 4500);

		XSSFCell cell0BI = (XSSFCell) row0.createCell((short) 60);
		cell0BI.setCellValue("Locations");
		cell0BI.setCellStyle(style);

		sheet.setColumnWidth(60, 4500);

		XSSFCell cell0BJ = (XSSFCell) row0.createCell((short) 61);
		cell0BJ.setCellValue("WorkTypes");
		cell0BJ.setCellStyle(style);

		sheet.setColumnWidth(61, 4500);

		XSSFCell cell0BK = (XSSFCell) row0.createCell((short) 62);
		cell0BK.setCellValue("EmergencyContact1_Name");
		cell0BK.setCellStyle(style);

		sheet.setColumnWidth(62, 4500);

		XSSFCell cell0BL = (XSSFCell) row0.createCell((short) 63);
		cell0BL.setCellValue("EmergencyContact1_Relationship");
		cell0BL.setCellStyle(style);

		sheet.setColumnWidth(63, 4500);

		XSSFCell cell0BM = (XSSFCell) row0.createCell((short) 64);
		cell0BM.setCellValue("EmergencyContact1_Address");
		cell0BM.setCellStyle(style);

		sheet.setColumnWidth(64, 4500);

		XSSFCell cell0BN = (XSSFCell) row0.createCell((short) 65);
		cell0BN.setCellValue("EmergencyContact1_ContactNumber");
		cell0BN.setCellStyle(style);

		sheet.setColumnWidth(65, 4500);

		XSSFCell cell0BO = (XSSFCell) row0.createCell((short) 66);
		cell0BO.setCellValue("EmergencyContact1_AlternateContactNumber");
		cell0BO.setCellStyle(style);

		sheet.setColumnWidth(66, 4500);

		XSSFCell cell0BP = (XSSFCell) row0.createCell((short) 67);
		cell0BP.setCellValue("EmergencyContact2_Name");
		cell0BP.setCellStyle(style);

		sheet.setColumnWidth(67, 4500);

		XSSFCell cell0BQ = (XSSFCell) row0.createCell((short) 68);
		cell0BQ.setCellValue("EmergencyContact2_Relationship");
		cell0BQ.setCellStyle(style);

		sheet.setColumnWidth(68, 4500);

		XSSFCell cell0BR = (XSSFCell) row0.createCell((short) 69);
		cell0BR.setCellValue("EmergencyContact2_Address");
		cell0BR.setCellStyle(style);

		sheet.setColumnWidth(69, 4500);

		XSSFCell cell0BS = (XSSFCell) row0.createCell((short) 70);
		cell0BS.setCellValue("EmergencyContact2_ContactNumber");
		cell0BS.setCellStyle(style);

		sheet.setColumnWidth(70, 4500);

		XSSFCell cell0BT = (XSSFCell) row0.createCell((short) 71);
		cell0BT.setCellValue("EmergencyContact2_AlternateContactNumber");
		cell0BT.setCellStyle(style);

		sheet.setColumnWidth(71, 4500);

		XSSFCell cell0BU = (XSSFCell) row0.createCell((short) 72);
		cell0BU.setCellValue("BankAccount1_BSB");
		cell0BU.setCellStyle(style);

		sheet.setColumnWidth(72, 4500);

		XSSFCell cell0BV = (XSSFCell) row0.createCell((short) 73);
		cell0BV.setCellValue("BankAccount1_AccountNumber");
		cell0BV.setCellStyle(style);

		sheet.setColumnWidth(73, 4500);

		XSSFCell cell0BW = (XSSFCell) row0.createCell((short) 74);
		cell0BW.setCellValue("BankAccount1_AccountName");
		cell0BW.setCellStyle(style);

		sheet.setColumnWidth(74, 4500);

		XSSFCell cell0BX = (XSSFCell) row0.createCell((short) 75);
		cell0BX.setCellValue("BankAccount1_AllocatedPercentage");
		cell0BX.setCellStyle(style);

		sheet.setColumnWidth(75, 4500);

		XSSFCell cell0BY = (XSSFCell) row0.createCell((short) 76);
		cell0BY.setCellValue("BankAccount1_FixedAmount");
		cell0BY.setCellStyle(style);

		sheet.setColumnWidth(76, 4500);

		XSSFCell cell0BZ = (XSSFCell) row0.createCell((short) 77);
		cell0BZ.setCellValue("BankAccount2_BSB");
		cell0BZ.setCellStyle(style);

		sheet.setColumnWidth(77, 4500);

		XSSFCell cell0CA = (XSSFCell) row0.createCell((short) 78);
		cell0CA.setCellValue("BankAccount2_AccountNumber");
		cell0CA.setCellStyle(style);

		sheet.setColumnWidth(78, 4500);

		XSSFCell cell0CB = (XSSFCell) row0.createCell((short) 79);
		cell0CB.setCellValue("BankAccount2_AccountName");
		cell0CB.setCellStyle(style);

		sheet.setColumnWidth(79, 4500);

		XSSFCell cell0CC = (XSSFCell) row0.createCell((short) 80);
		cell0CC.setCellValue("BankAccount2_AllocatedPercentage");
		cell0CC.setCellStyle(style);

		sheet.setColumnWidth(80, 4500);

		XSSFCell cell0CD = (XSSFCell) row0.createCell((short) 81);
		cell0CD.setCellValue("BankAccount2_FixedAmount");
		cell0CD.setCellStyle(style);

		sheet.setColumnWidth(81, 4500);

		XSSFCell cell0CE = (XSSFCell) row0.createCell((short) 82);
		cell0CE.setCellValue("BankAccount3_BSB");
		cell0CE.setCellStyle(style);

		sheet.setColumnWidth(82, 4500);

		XSSFCell cell0CF = (XSSFCell) row0.createCell((short) 83);
		cell0CF.setCellValue("BankAccount3_AccountNumber");
		cell0CF.setCellStyle(style);

		sheet.setColumnWidth(83, 4500);

		XSSFCell cell0CG = (XSSFCell) row0.createCell((short) 84);
		cell0CG.setCellValue("BankAccount3_AccountName");
		cell0CG.setCellStyle(style);

		sheet.setColumnWidth(84, 4500);

		XSSFCell cell0CH = (XSSFCell) row0.createCell((short) 85);
		cell0CH.setCellValue("BankAccount3_AllocatedPercentage");
		cell0CH.setCellStyle(style);

		sheet.setColumnWidth(85, 4500);

		XSSFCell cell0CI = (XSSFCell) row0.createCell((short) 86);
		cell0CI.setCellValue("BankAccount3_FixedAmount");
		cell0CI.setCellStyle(style);

		sheet.setColumnWidth(86, 4500);

		XSSFCell cell0CJ = (XSSFCell) row0.createCell((short) 87);
		cell0CJ.setCellValue("SuperFund1_ProductCode");
		cell0CJ.setCellStyle(style);

		sheet.setColumnWidth(87, 4500);

		XSSFCell cell0CK = (XSSFCell) row0.createCell((short) 88);
		cell0CK.setCellValue("SuperFund1_FundName");
		cell0CK.setCellStyle(style);

		sheet.setColumnWidth(88, 4500);

		XSSFCell cell0CL = (XSSFCell) row0.createCell((short) 89);
		cell0CL.setCellValue("SuperFund1_MemberNumber");
		cell0CL.setCellStyle(style);

		sheet.setColumnWidth(89, 4500);

		XSSFCell cell0CM = (XSSFCell) row0.createCell((short) 90);
		cell0CM.setCellValue("SuperFund1_AllocatedPercentage");
		cell0CM.setCellStyle(style);

		sheet.setColumnWidth(90, 4500);

		XSSFCell cell0CN = (XSSFCell) row0.createCell((short) 91);
		cell0CN.setCellValue("SuperFund1_FixedAmount");
		cell0CN.setCellStyle(style);

		sheet.setColumnWidth(91, 4500);

		XSSFCell cell0CO = (XSSFCell) row0.createCell((short) 92);
		cell0CO.setCellValue("SuperFund2_ProductCode");
		cell0CO.setCellStyle(style);

		sheet.setColumnWidth(92, 4500);

		XSSFCell cell0CP = (XSSFCell) row0.createCell((short) 93);
		cell0CP.setCellValue("SuperFund2_FundName");
		cell0CP.setCellStyle(style);

		sheet.setColumnWidth(93, 4500);

		XSSFCell cell0CQ = (XSSFCell) row0.createCell((short) 94);
		cell0CQ.setCellValue("SuperFund2_MemberNumber");
		cell0CQ.setCellStyle(style);

		sheet.setColumnWidth(94, 4500);

		XSSFCell cell0CR = (XSSFCell) row0.createCell((short) 95);
		cell0CR.setCellValue("SuperFund2_AllocatedPercentage");
		cell0CR.setCellStyle(style);

		sheet.setColumnWidth(95, 4500);

		XSSFCell cell0CS = (XSSFCell) row0.createCell((short) 96);
		cell0CS.setCellValue("SuperFund2_FixedAmount");
		cell0CS.setCellStyle(style);

		sheet.setColumnWidth(96, 4500);

		XSSFCell cell0CT = (XSSFCell) row0.createCell((short) 97);
		cell0CT.setCellValue("SuperFund3_ProductCode");
		cell0CT.setCellStyle(style);

		sheet.setColumnWidth(97, 4500);

		XSSFCell cell0CU = (XSSFCell) row0.createCell((short) 98);
		cell0CU.setCellValue("SuperFund3_FundName");
		cell0CU.setCellStyle(style);

		sheet.setColumnWidth(98, 4500);

		XSSFCell cell0CV = (XSSFCell) row0.createCell((short) 99);
		cell0CV.setCellValue("SuperFund3_MemberNumber");
		cell0CV.setCellStyle(style);

		sheet.setColumnWidth(99, 4500);

		XSSFCell cell0CW = (XSSFCell) row0.createCell((short) 100);
		cell0CW.setCellValue("SuperFund3_AllocatedPercentage");
		cell0CW.setCellStyle(style);

		sheet.setColumnWidth(100, 4500);

		XSSFCell cell0CX = (XSSFCell) row0.createCell((short) 101);
		cell0CX.setCellValue("SuperFund3_FixedAmount");
		cell0CX.setCellStyle(style);

		sheet.setColumnWidth(101, 4500);

		XSSFCell cell0CY = (XSSFCell) row0.createCell((short) 102);
		cell0CY.setCellValue("SuperThresholdAmount");
		cell0CY.setCellStyle(style);

		sheet.setColumnWidth(102, 4500);

		XSSFCell cell0CZ = (XSSFCell) row0.createCell((short) 103);
		cell0CZ.setCellValue("MaximumQuarterlySuperContributionsBase");
		cell0CZ.setCellStyle(style);

		sheet.setColumnWidth(103, 4500);

		XSSFCell cell0DA = (XSSFCell) row0.createCell((short) 104);
		cell0DA.setCellValue("RosteringNotificationChoices");
		cell0DA.setCellStyle(style);

		sheet.setColumnWidth(104, 4500);
	}

	private void setEmployeeDataToSheet(XSSFWorkbook workbook, XSSFRow row0, WorkDayAUReportDTO workDayAUReportDTO) {

		XSSFFont font = workbook.createFont();
		font.setFontName("Calibri");
		font.setItalic(false);
		font.setColor(HSSFColor.BLACK.index);
		font.setBold(true);
		font.setFontHeightInPoints((short) 14);

		XSSFCellStyle style = workbook.createCellStyle();
		style.setFont(font);

		XSSFCell cell0A = (XSSFCell) row0.createCell((short) 0);
		cell0A.setCellValue(workDayAUReportDTO.getEmployeeId());
		cell0A.setCellStyle(style);

		XSSFCell cell0B = (XSSFCell) row0.createCell((short) 1);
		cell0B.setCellValue(workDayAUReportDTO.getTaxFileNumber());
		cell0B.setCellStyle(style);

		XSSFCell cell0C = (XSSFCell) row0.createCell((short) 2);
		cell0C.setCellValue(workDayAUReportDTO.getTitle());
		cell0C.setCellStyle(style);

		XSSFCell cell0D = (XSSFCell) row0.createCell((short) 3);
		cell0D.setCellValue(workDayAUReportDTO.getPreferredName());
		cell0D.setCellStyle(style);

		XSSFCell cell0E = (XSSFCell) row0.createCell((short) 4);
		cell0E.setCellValue(workDayAUReportDTO.getFirstName());
		cell0E.setCellStyle(style);

		XSSFCell cell0F = (XSSFCell) row0.createCell((short) 5);
		cell0F.setCellValue(workDayAUReportDTO.getMiddleName());
		cell0F.setCellStyle(style);

		XSSFCell cell0G = (XSSFCell) row0.createCell((short) 6);
		cell0G.setCellValue(workDayAUReportDTO.getSurname());
		cell0G.setCellStyle(style);

		XSSFCell cell0H = (XSSFCell) row0.createCell((short) 7);
		cell0H.setCellValue(workDayAUReportDTO.getDateOfBirth());
		cell0H.setCellStyle(style);

		XSSFCell cell0I = (XSSFCell) row0.createCell((short) 8);
		cell0I.setCellValue(workDayAUReportDTO.getGender());
		cell0I.setCellStyle(style);

		XSSFCell cell0J = (XSSFCell) row0.createCell((short) 9);
		cell0J.setCellValue(workDayAUReportDTO.getExternalId());
		cell0J.setCellStyle(style);

		XSSFCell cell0K = (XSSFCell) row0.createCell((short) 10);
		cell0K.setCellValue(workDayAUReportDTO.getResidentialStreetAddress());
		cell0K.setCellStyle(style);

		XSSFCell cell0L = (XSSFCell) row0.createCell((short) 11);
		cell0L.setCellValue(workDayAUReportDTO.getResidentialAddressLine2());
		cell0L.setCellStyle(style);

		XSSFCell cell0M = (XSSFCell) row0.createCell((short) 12);
		cell0M.setCellValue(workDayAUReportDTO.getResidentialSuburb());
		cell0M.setCellStyle(style);

		XSSFCell cell0N = (XSSFCell) row0.createCell((short) 13);
		cell0N.setCellValue(workDayAUReportDTO.getResidentialState());
		cell0N.setCellStyle(style);

		XSSFCell cell0O = (XSSFCell) row0.createCell((short) 14);
		cell0O.setCellValue(workDayAUReportDTO.getResidentialPostCode());
		cell0O.setCellStyle(style);

		XSSFCell cell0P = (XSSFCell) row0.createCell((short) 15);
		cell0P.setCellValue(workDayAUReportDTO.getPostalStreetAddress());
		cell0P.setCellStyle(style);

		XSSFCell cell0Q = (XSSFCell) row0.createCell((short) 16);
		cell0Q.setCellValue(workDayAUReportDTO.getPostalAddressLine2());
		cell0Q.setCellStyle(style);

		XSSFCell cell0R = (XSSFCell) row0.createCell((short) 17);
		cell0R.setCellValue(workDayAUReportDTO.getPostalSuburb());
		cell0R.setCellStyle(style);

		XSSFCell cell0S = (XSSFCell) row0.createCell((short) 18);
		cell0S.setCellValue(workDayAUReportDTO.getPostalState());
		cell0S.setCellStyle(style);

		XSSFCell cell0T = (XSSFCell) row0.createCell((short) 19);
		cell0T.setCellValue(workDayAUReportDTO.getPostalPostCode());
		cell0T.setCellStyle(style);

		XSSFCell cell0U = (XSSFCell) row0.createCell((short) 20);
		cell0U.setCellValue(workDayAUReportDTO.getEmailAddress());
		cell0U.setCellStyle(style);

		XSSFCell cell0V = (XSSFCell) row0.createCell((short) 21);
		cell0V.setCellValue(workDayAUReportDTO.getHomePhone());
		cell0V.setCellStyle(style);

		XSSFCell cell0W = (XSSFCell) row0.createCell((short) 22);
		cell0W.setCellValue(workDayAUReportDTO.getWorkPhone());
		cell0W.setCellStyle(style);

		XSSFCell cell0X = (XSSFCell) row0.createCell((short) 23);
		cell0X.setCellValue(workDayAUReportDTO.getMobilePhone());
		cell0X.setCellStyle(style);

		XSSFCell cell0Y = (XSSFCell) row0.createCell((short) 24);
		cell0Y.setCellValue(workDayAUReportDTO.getStartDate());
		cell0Y.setCellStyle(style);

		XSSFCell cell0Z = (XSSFCell) row0.createCell((short) 25);
		cell0Z.setCellValue(workDayAUReportDTO.getEndDate());
		cell0Z.setCellStyle(style);

		XSSFCell cell0AA = (XSSFCell) row0.createCell((short) 26);
		cell0AA.setCellValue(workDayAUReportDTO.getAnniversaryDate());
		cell0AA.setCellStyle(style);

		XSSFCell cell0AB = (XSSFCell) row0.createCell((short) 27);
		cell0AB.setCellValue(workDayAUReportDTO.getTags());
		cell0AB.setCellStyle(style);

		XSSFCell cell0AC = (XSSFCell) row0.createCell((short) 28);
		cell0AC.setCellValue(workDayAUReportDTO.getEmployingEntityABN());
		cell0AC.setCellStyle(style);

		XSSFCell cell0AD = (XSSFCell) row0.createCell((short) 29);
		cell0AD.setCellValue(workDayAUReportDTO.getEmploymentType());
		cell0AD.setCellStyle(style);

		XSSFCell cell0AE = (XSSFCell) row0.createCell((short) 30);
		cell0AE.setCellValue(workDayAUReportDTO.getPreviousSurname());
		cell0AE.setCellStyle(style);

		XSSFCell cell0AF = (XSSFCell) row0.createCell((short) 31);
		cell0AF.setCellValue(workDayAUReportDTO.getAustralianResident());
		cell0AF.setCellStyle(style);

		XSSFCell cell0AG = (XSSFCell) row0.createCell((short) 32);
		cell0AG.setCellValue(workDayAUReportDTO.getClaimTaxFreeThreshold());
		cell0AG.setCellStyle(style);

		XSSFCell cell0AH = (XSSFCell) row0.createCell((short) 33);
		cell0AH.setCellValue(workDayAUReportDTO.getSeniorsTaxOffset());
		cell0AH.setCellStyle(style);

		XSSFCell cell0AI = (XSSFCell) row0.createCell((short) 34);
		cell0AI.setCellValue(workDayAUReportDTO.getOtherTaxOffset());
		cell0AI.setCellStyle(style);

		XSSFCell cell0AJ = (XSSFCell) row0.createCell((short) 35);
		cell0AJ.setCellValue(workDayAUReportDTO.getHelpDebt());
		cell0AJ.setCellStyle(style);

		XSSFCell cell0AK = (XSSFCell) row0.createCell((short) 36);
		cell0AK.setCellValue(workDayAUReportDTO.getAFSDebt());
		cell0AK.setCellStyle(style);

		XSSFCell cell0AL = (XSSFCell) row0.createCell((short) 37);
		cell0AL.setCellValue(workDayAUReportDTO.getIsExemptFromFloodLevy());
		cell0AL.setCellStyle(style);

		XSSFCell cell0AM = (XSSFCell) row0.createCell((short) 38);
		cell0AM.setCellValue(workDayAUReportDTO.getHasApprovedWorkingHolidayVisa());
		cell0AM.setCellStyle(style);

		XSSFCell cell0AN = (XSSFCell) row0.createCell((short) 39);
		cell0AN.setCellValue(workDayAUReportDTO.getHasWithholdingVariation());
		cell0AN.setCellStyle(style);

		XSSFCell cell0AO = (XSSFCell) row0.createCell((short) 40);
		cell0AO.setCellValue(workDayAUReportDTO.getTaxVariation());
		cell0AO.setCellStyle(style);

		XSSFCell cell0AP = (XSSFCell) row0.createCell((short) 41);
		cell0AP.setCellValue(workDayAUReportDTO.getMedicareLevyExemption());
		cell0AP.setCellStyle(style);

		XSSFCell cell0AQ = (XSSFCell) row0.createCell((short) 42);
		cell0AQ.setCellValue(workDayAUReportDTO.getDateTaxFileDeclarationSigned());
		cell0AQ.setCellStyle(style);

		XSSFCell cell0AR = (XSSFCell) row0.createCell((short) 43);
		cell0AR.setCellValue(workDayAUReportDTO.getDateTaxFileDeclarationReported());
		cell0AR.setCellStyle(style);

		XSSFCell cell0AS = (XSSFCell) row0.createCell((short) 44);
		cell0AS.setCellValue(workDayAUReportDTO.getJobTitle());
		cell0AS.setCellStyle(style);

		XSSFCell cell0AT = (XSSFCell) row0.createCell((short) 45);
		cell0AT.setCellValue(workDayAUReportDTO.getPaySchedule());
		cell0AT.setCellStyle(style);

		XSSFCell cell0AU = (XSSFCell) row0.createCell((short) 46);
		cell0AU.setCellValue(workDayAUReportDTO.getPrimaryPayCategory());
		cell0AU.setCellStyle(style);

		XSSFCell cell0AV = (XSSFCell) row0.createCell((short) 47);
		cell0AV.setCellValue(workDayAUReportDTO.getPrimaryLocation());
		cell0AV.setCellStyle(style);

		XSSFCell cell0AW = (XSSFCell) row0.createCell((short) 48);
		cell0AW.setCellValue(workDayAUReportDTO.getPaySlipNotificationType());
		cell0AW.setCellStyle(style);

		XSSFCell cell0AX = (XSSFCell) row0.createCell((short) 49);
		cell0AX.setCellValue(workDayAUReportDTO.getRate());
		cell0AX.setCellStyle(style);

		XSSFCell cell0AY = (XSSFCell) row0.createCell((short) 50);
		cell0AY.setCellValue(workDayAUReportDTO.getRateUnit());
		cell0AY.setCellStyle(style);

		XSSFCell cell0AZ = (XSSFCell) row0.createCell((short) 51);
		cell0AZ.setCellValue(workDayAUReportDTO.getOverrideTemplateRate());
		cell0AZ.setCellStyle(style);

		XSSFCell cell0BA = (XSSFCell) row0.createCell((short) 52);
		cell0BA.setCellValue(workDayAUReportDTO.getHoursPerWeek());
		cell0BA.setCellStyle(style);

		XSSFCell cell0BB = (XSSFCell) row0.createCell((short) 53);
		cell0BB.setCellValue(workDayAUReportDTO.getAutomaticallyPayEmployee());
		cell0BB.setCellStyle(style);

		XSSFCell cell0BC = (XSSFCell) row0.createCell((short) 54);
		cell0BC.setCellValue(workDayAUReportDTO.getLeaveTemplate());
		cell0BC.setCellStyle(style);

		XSSFCell cell0BD = (XSSFCell) row0.createCell((short) 55);
		cell0BD.setCellValue(workDayAUReportDTO.getPayRateTemplate());
		cell0BD.setCellStyle(style);

		XSSFCell cell0BE = (XSSFCell) row0.createCell((short) 56);
		cell0BE.setCellValue(workDayAUReportDTO.getPayConditionRuleSet());
		cell0BE.setCellStyle(style);

		XSSFCell cell0BF = (XSSFCell) row0.createCell((short) 57);
		cell0BF.setCellValue(workDayAUReportDTO.getEmploymentAgreement());
		cell0BF.setCellStyle(style);

		XSSFCell cell0BG = (XSSFCell) row0.createCell((short) 58);
		cell0BG.setCellValue(workDayAUReportDTO.getIsEnabledForTimesheets());
		cell0BG.setCellStyle(style);

		XSSFCell cell0BH = (XSSFCell) row0.createCell((short) 59);
		cell0BH.setCellValue(workDayAUReportDTO.getIsExemptFromPayrollTax());
		cell0BH.setCellStyle(style);

		XSSFCell cell0BI = (XSSFCell) row0.createCell((short) 60);
		cell0BI.setCellValue(workDayAUReportDTO.getLocations());
		cell0BI.setCellStyle(style);

		XSSFCell cell0BJ = (XSSFCell) row0.createCell((short) 61);
		cell0BJ.setCellValue(workDayAUReportDTO.getWorkTypes());
		cell0BJ.setCellStyle(style);

		XSSFCell cell0BK = (XSSFCell) row0.createCell((short) 62);
		cell0BK.setCellValue(workDayAUReportDTO.getEmergencyContact1_Name());
		cell0BK.setCellStyle(style);

		XSSFCell cell0BL = (XSSFCell) row0.createCell((short) 63);
		cell0BL.setCellValue(workDayAUReportDTO.getEmergencyContact1_Relationship());
		cell0BL.setCellStyle(style);

		XSSFCell cell0BM = (XSSFCell) row0.createCell((short) 64);
		cell0BM.setCellValue(workDayAUReportDTO.getEmergencyContact1_Address());
		cell0BM.setCellStyle(style);

		XSSFCell cell0BN = (XSSFCell) row0.createCell((short) 65);
		cell0BN.setCellValue(workDayAUReportDTO.getEmergencyContact1_ContactNumber());
		cell0BN.setCellStyle(style);

		XSSFCell cell0BO = (XSSFCell) row0.createCell((short) 66);
		cell0BO.setCellValue(workDayAUReportDTO.getEmergencyContact1_AlternateContactNumber());
		cell0BO.setCellStyle(style);

		XSSFCell cell0BP = (XSSFCell) row0.createCell((short) 67);
		cell0BP.setCellValue(workDayAUReportDTO.getEmergencyContact2_Name());
		cell0BP.setCellStyle(style);

		XSSFCell cell0BQ = (XSSFCell) row0.createCell((short) 68);
		cell0BQ.setCellValue(workDayAUReportDTO.getEmergencyContact2_Relationship());
		cell0BQ.setCellStyle(style);

		XSSFCell cell0BR = (XSSFCell) row0.createCell((short) 69);
		cell0BR.setCellValue(workDayAUReportDTO.getEmergencyContact2_Address());
		cell0BR.setCellStyle(style);

		XSSFCell cell0BS = (XSSFCell) row0.createCell((short) 70);
		cell0BS.setCellValue(workDayAUReportDTO.getEmergencyContact2_ContactNumber());
		cell0BS.setCellStyle(style);

		XSSFCell cell0BT = (XSSFCell) row0.createCell((short) 71);
		cell0BT.setCellValue(workDayAUReportDTO.getEmergencyContact2_AlternateContactNumber());
		cell0BT.setCellStyle(style);

		XSSFCell cell0BU = (XSSFCell) row0.createCell((short) 72);
		cell0BU.setCellValue(workDayAUReportDTO.getBankAccount1_BSB());
		cell0BU.setCellStyle(style);

		XSSFCell cell0BV = (XSSFCell) row0.createCell((short) 73);
		cell0BV.setCellValue(workDayAUReportDTO.getBankAccount1_AccountNumber());
		cell0BV.setCellStyle(style);

		XSSFCell cell0BW = (XSSFCell) row0.createCell((short) 74);
		cell0BW.setCellValue(workDayAUReportDTO.getBankAccount1_AccountName());
		cell0BW.setCellStyle(style);

		XSSFCell cell0BX = (XSSFCell) row0.createCell((short) 75);
		cell0BX.setCellValue(workDayAUReportDTO.getBankAccount1_AllocatedPercentage());
		cell0BX.setCellStyle(style);

		XSSFCell cell0BY = (XSSFCell) row0.createCell((short) 76);
		cell0BY.setCellValue(workDayAUReportDTO.getBankAccount1_FixedAmount());
		cell0BY.setCellStyle(style);

		XSSFCell cell0BZ = (XSSFCell) row0.createCell((short) 77);
		cell0BZ.setCellValue(workDayAUReportDTO.getBankAccount2_BSB());
		cell0BZ.setCellStyle(style);

		XSSFCell cell0CA = (XSSFCell) row0.createCell((short) 78);
		cell0CA.setCellValue(workDayAUReportDTO.getBankAccount2_AccountNumber());
		cell0CA.setCellStyle(style);

		XSSFCell cell0CB = (XSSFCell) row0.createCell((short) 79);
		cell0CB.setCellValue(workDayAUReportDTO.getBankAccount2_AccountName());
		cell0CB.setCellStyle(style);

		XSSFCell cell0CC = (XSSFCell) row0.createCell((short) 80);
		cell0CC.setCellValue(workDayAUReportDTO.getBankAccount2_AllocatedPercentage());
		cell0CC.setCellStyle(style);

		XSSFCell cell0CD = (XSSFCell) row0.createCell((short) 81);
		cell0CD.setCellValue(workDayAUReportDTO.getBankAccount2_FixedAmount());
		cell0CD.setCellStyle(style);

		XSSFCell cell0CE = (XSSFCell) row0.createCell((short) 82);
		cell0CE.setCellValue(workDayAUReportDTO.getBankAccount3_BSB());
		cell0CE.setCellStyle(style);

		XSSFCell cell0CF = (XSSFCell) row0.createCell((short) 83);
		cell0CF.setCellValue(workDayAUReportDTO.getBankAccount3_AccountNumber());
		cell0CF.setCellStyle(style);

		XSSFCell cell0CG = (XSSFCell) row0.createCell((short) 84);
		cell0CG.setCellValue(workDayAUReportDTO.getBankAccount3_AccountName());
		cell0CG.setCellStyle(style);

		XSSFCell cell0CH = (XSSFCell) row0.createCell((short) 85);
		cell0CH.setCellValue(workDayAUReportDTO.getBankAccount3_AllocatedPercentage());
		cell0CH.setCellStyle(style);

		XSSFCell cell0CI = (XSSFCell) row0.createCell((short) 86);
		cell0CI.setCellValue(workDayAUReportDTO.getBankAccount3_FixedAmount());
		cell0CI.setCellStyle(style);

		XSSFCell cell0CJ = (XSSFCell) row0.createCell((short) 87);
		cell0CJ.setCellValue(workDayAUReportDTO.getSuperFund1_ProductCode());
		cell0CJ.setCellStyle(style);

		XSSFCell cell0CK = (XSSFCell) row0.createCell((short) 88);
		cell0CK.setCellValue(workDayAUReportDTO.getSuperFund1_FundName());
		cell0CK.setCellStyle(style);

		XSSFCell cell0CL = (XSSFCell) row0.createCell((short) 89);
		cell0CL.setCellValue(workDayAUReportDTO.getSuperFund1_MemberNumber());
		cell0CL.setCellStyle(style);

		XSSFCell cell0CM = (XSSFCell) row0.createCell((short) 90);
		cell0CM.setCellValue(workDayAUReportDTO.getSuperFund1_AllocatedPercentage());
		cell0CM.setCellStyle(style);

		XSSFCell cell0CN = (XSSFCell) row0.createCell((short) 91);
		cell0CN.setCellValue(workDayAUReportDTO.getSuperFund1_FixedAmount());
		cell0CN.setCellStyle(style);

		XSSFCell cell0CO = (XSSFCell) row0.createCell((short) 92);
		cell0CO.setCellValue(workDayAUReportDTO.getSuperFund2_ProductCode());
		cell0CO.setCellStyle(style);

		XSSFCell cell0CP = (XSSFCell) row0.createCell((short) 93);
		cell0CP.setCellValue(workDayAUReportDTO.getSuperFund2_FundName());
		cell0CP.setCellStyle(style);

		XSSFCell cell0CQ = (XSSFCell) row0.createCell((short) 94);
		cell0CQ.setCellValue(workDayAUReportDTO.getSuperFund2_MemberNumber());
		cell0CQ.setCellStyle(style);

		XSSFCell cell0CR = (XSSFCell) row0.createCell((short) 95);
		cell0CR.setCellValue(workDayAUReportDTO.getSuperFund2_AllocatedPercentage());
		cell0CR.setCellStyle(style);

		XSSFCell cell0CS = (XSSFCell) row0.createCell((short) 96);
		cell0CS.setCellValue(workDayAUReportDTO.getSuperFund2_FixedAmount());
		cell0CS.setCellStyle(style);

		XSSFCell cell0CT = (XSSFCell) row0.createCell((short) 97);
		cell0CT.setCellValue(workDayAUReportDTO.getSuperFund3_ProductCode());
		cell0CT.setCellStyle(style);

		XSSFCell cell0CU = (XSSFCell) row0.createCell((short) 98);
		cell0CU.setCellValue(workDayAUReportDTO.getSuperFund3_FundName());
		cell0CU.setCellStyle(style);

		XSSFCell cell0CV = (XSSFCell) row0.createCell((short) 99);
		cell0CV.setCellValue(workDayAUReportDTO.getSuperFund3_MemberNumber());
		cell0CV.setCellStyle(style);

		XSSFCell cell0CW = (XSSFCell) row0.createCell((short) 100);
		cell0CW.setCellValue(workDayAUReportDTO.getSuperFund3_AllocatedPercentage());
		cell0CW.setCellStyle(style);

		XSSFCell cell0CX = (XSSFCell) row0.createCell((short) 101);
		cell0CX.setCellValue(workDayAUReportDTO.getSuperFund3_FixedAmount());
		cell0CX.setCellStyle(style);

		XSSFCell cell0CY = (XSSFCell) row0.createCell((short) 102);
		cell0CY.setCellValue(workDayAUReportDTO.getSuperThresholdAmount());
		cell0CY.setCellStyle(style);

		XSSFCell cell0CZ = (XSSFCell) row0.createCell((short) 103);
		cell0CZ.setCellValue(workDayAUReportDTO.getMaximumQuarterlySuperContributionsBase());
		cell0CZ.setCellStyle(style);

		XSSFCell cell0DA = (XSSFCell) row0.createCell((short) 104);
		cell0DA.setCellValue(workDayAUReportDTO.getRosteringNotificationChoices());
		cell0DA.setCellStyle(style);
	}

}
