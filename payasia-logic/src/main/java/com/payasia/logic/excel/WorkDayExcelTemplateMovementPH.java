package com.payasia.logic.excel;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
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
import com.payasia.common.dto.BasicRateProgressionPHReportDTO;
import com.payasia.common.dto.CareerProgressionPHReportDTO;
import com.payasia.common.dto.EmeeDataChangesPHReportDTO;
import com.payasia.common.dto.NewHiresPHReportDTO;
import com.payasia.common.dto.PayItemsRecurringPHReportDTO;
import com.payasia.common.dto.ResignationsPHReportDTO;
import com.payasia.common.dto.WorkDayPHReportDTO;
import com.payasia.common.dto.WorkDayReportDTO;
import com.payasia.common.util.DateUtils;
import com.payasia.dao.bean.WorkdayPaygroupBatchData;

public class WorkDayExcelTemplateMovementPH extends WorkDayExcelTemplate {

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
			List<WorkDayReportDTO> workDayPHReportList = new ArrayList<WorkDayReportDTO>();

			WorkDayPHReportDTO workDayPHReportDTO = new WorkDayPHReportDTO();

			NewHiresPHReportDTO newHiresPHReport = null;
			EmeeDataChangesPHReportDTO emeeDataChangesPHReport = null;
			ResignationsPHReportDTO resignationsPHReport = null;
			CareerProgressionPHReportDTO careerProgressionPHReport = null;
			BasicRateProgressionPHReportDTO basicRateProgressionPHReport = null;
			PayItemsRecurringPHReportDTO payItemsRecurringPHReport = null;

			EmployeeType employeeType = (EmployeeType) employeeTypeElement.getValue();

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

			if (workdayPaygroupBatch.isNewEmployee()) {
				newHiresPHReport = new NewHiresPHReportDTO();
				if (summaryType != null) {
					newHiresPHReport.setEmployeeID(
							summaryType.getEmployeeID() == null ? "" : summaryType.getEmployeeID().getValue());
				}
				if (personalType != null) {
					newHiresPHReport.setFirstName(personalType.getFirstName() == null ? ""
							: personalType.getFirstName().getValue().getValue());
					newHiresPHReport.setLastName(
							personalType.getLastName() == null ? "" : personalType.getLastName().getValue().getValue());
					newHiresPHReport.setMiddleName(personalType.getMiddleName() == null ? ""
							: personalType.getMiddleName().getValue().getValue());
				}
				if (additionalInformationTypeList != null && !additionalInformationTypeList.isEmpty()) {
					for (AdditionalInformationType additionalInformationType : additionalInformationTypeList) {
						newHiresPHReport.setMiddleInitial(additionalInformationType.getMiddleNameInitial() == null ? ""
								: additionalInformationType.getMiddleNameInitial().getValue().getValue());
					}
				}
				if (personalType != null) {
					newHiresPHReport.setGender(
							personalType.getGender() == null ? "" : personalType.getGender().getValue().getValue());
					newHiresPHReport.setSalutation(
							personalType.getTitle() == null ? "" : personalType.getTitle().getValue().getValue());
					newHiresPHReport.setMaritalStatus(personalType.getMaritalStatus() == null ? ""
							: personalType.getMaritalStatus().getValue().getValue());
					newHiresPHReport.setBirthDate(personalType.getBirthDate() == null ? ""
							: personalType.getBirthDate().getValue().getValue());
				}
				if (personalType != null) {
					List<AddressLineType> addressLineTypeList = personalType.getFirstAddressLineData();
					if (addressLineTypeList != null && !addressLineTypeList.isEmpty()) {
						int i = 0;
						for (AddressLineType addressLineType : addressLineTypeList) {
							/*
							 * if (addressLineType.getLabel() != null &&
							 * addressLineType.getLabel().equalsIgnoreCase(
							 * "ADDRESS_LINE_1")) {
							 * newHiresPHReport.setAddress(addressLineType.
							 * getValue()); } if (addressLineType.getLabel() !=
							 * null &&
							 * addressLineType.getLabel().equalsIgnoreCase(
							 * "ADDRESS_LINE_2")) {
							 * newHiresPHReport.setAddress2(addressLineType.
							 * getValue()); } if (addressLineType.getLabel() !=
							 * null &&
							 * addressLineType.getLabel().equalsIgnoreCase(
							 * "ADDRESS_LINE_3")) {
							 * newHiresPHReport.setAddress3(addressLineType.
							 * getValue()); }
							 */
							if (i == 0) {
								newHiresPHReport.setAddress(addressLineType.getValue());
							}
							if (i == 1) {
								newHiresPHReport.setAddress2(addressLineType.getValue());
							}
							if (i == 2) {
								newHiresPHReport.setAddress3(addressLineType.getValue());
							}
							i++;
						}
					}
				}
				if (personalType != null) {
					newHiresPHReport.setCity(personalType.getFirstMunicipality() == null ? ""
							: personalType.getFirstMunicipality().getValue().getValue());
					newHiresPHReport.setZipCode(personalType.getFirstPostalCode() == null ? ""
							: personalType.getFirstPostalCode().getValue().getValue());
					newHiresPHReport.setCountry(personalType.getFirstCountry() == null ? ""
							: personalType.getFirstCountry().getValue().getValue());
					newHiresPHReport.setPhone(personalType.getPhoneNumber() == null ? ""
							: personalType.getPhoneNumber().getValue().getValue());
					// newHiresPHReport.setMobilePhone(String.valueOf(personalType));
					newHiresPHReport.setEmail(personalType.getFirstEmailAddress() == null ? ""
							: personalType.getFirstEmailAddress().getValue().getValue());
				}
				if (positionTypeList != null && !positionTypeList.isEmpty()) {
					for (PositionType positionType : positionTypeList) {
						if (positionType.getOperation() != null
								&& !positionType.getOperation().getValue().value().equalsIgnoreCase("REMOVE")) {
							if (personalType != null) {
								newHiresPHReport.setJobTitle(positionType.getBusinessTitle() == null ? ""
										: positionType.getBusinessTitle().getValue().getValue());
							}
							if (statusType != null) {
								newHiresPHReport.setStartDate(statusType.getHireDate() == null ? ""
										: statusType.getHireDate().getValue().getValue());
							}
							if (personalType != null) {
								newHiresPHReport.setDepartment(positionType.getOrganizationOne() == null ? ""
										: positionType.getOrganizationOne().getValue().getValue());
							}
						}
					}
				}

				// newHiresPHReport.setCostCenter("");
				// newHiresPHReport.setEmpTaxID("");//HRO
				// newHiresPHReport.setDependantName1("");
				// newHiresPHReport.setDateofBirth("");
				// newHiresPHReport.setDependantName2("");
				// newHiresPHReport.setDependantName3("");
				// newHiresPHReport.setDependantName4("");

				if (identifierTypeList != null && !identifierTypeList.isEmpty()) {
					for (IdentifierType identifierType : identifierTypeList) {
						if (identifierType.getOperation() != null
								&& !identifierType.getOperation().getValue().value().equalsIgnoreCase("REMOVE")) {
							if (identifierType != null && identifierType.getIdentifierType() != null && identifierType
									.getIdentifierType().getValue().getValue().equals("Tax_Identification_Number")) {
								newHiresPHReport
										.settINNumber(identifierType.getIdentifierValue().getValue().getValue());
							}
							if (identifierType != null && identifierType.getIdentifierType() != null
									&& identifierType.getIdentifierType().getValue().getValue()
											.equals("PhilHealth_Identification_Number")) {
								newHiresPHReport
										.setpHICNumber(identifierType.getIdentifierValue().getValue().getValue());
							}
							if (identifierType != null && identifierType.getIdentifierType() != null && identifierType
									.getIdentifierType().getValue().getValue().equals("Pag_IBIG_MID_Number")) {
								newHiresPHReport
										.sethDMFNumber(identifierType.getIdentifierValue().getValue().getValue());
							}
							if (identifierType != null && identifierType.getIdentifierType() != null
									&& identifierType.getIdentifierType().getValue().getValue()
											.equals("Social_Security_System_Number")) {
								newHiresPHReport
										.setsSSNumber(identifierType.getIdentifierValue().getValue().getValue());
							}
						}
					}
				}
				if (salaryAndHourlyPlansTypeList != null && !salaryAndHourlyPlansTypeList.isEmpty()) {
					for (SalaryAndHourlyPlansType salaryAndHourlyPlansType : salaryAndHourlyPlansTypeList) {
						if (salaryAndHourlyPlansType.getOperation() != null && !salaryAndHourlyPlansType.getOperation()
								.getValue().getValue().equalsIgnoreCase("REMOVE")) {
							newHiresPHReport.setMonthlyBasicWage(salaryAndHourlyPlansType.getAmount() == null ? ""
									: String.valueOf(salaryAndHourlyPlansType.getAmount().getValue().getValue()));
						}
					}
				}
				if (paymentElectionTypeList != null && !paymentElectionTypeList.isEmpty()) {
					for (PaymentElectionType paymentElectionType : paymentElectionTypeList) {
						if (paymentElectionType.getOperation() != null
								&& !paymentElectionType.getOperation().getValue().value().equalsIgnoreCase("REMOVE")) {
							newHiresPHReport.setBank(paymentElectionType.getBankIDNumber() == null ? ""
									: paymentElectionType.getBankIDNumber().getValue().getValue());
							// newHiresPHReport.setBankBranch(paymentElectionType.getBankIDNumber()!=null?"":);
							newHiresPHReport.setAccountNumber(paymentElectionType.getAccountNumber() == null ? ""
									: paymentElectionType.getAccountNumber().getValue().getValue());
							newHiresPHReport.setAccountName(paymentElectionType.getBankAccountName() == null ? ""
									: paymentElectionType.getBankAccountName().getValue().getValue());
						}
					}
				}
				workDayPHReportDTO.setNewHiresPHReport(newHiresPHReport);
			} else if (!workdayPaygroupBatch.isNewEmployee()) {

				if (personalType != null
						|| (additionalInformationTypeList != null && !additionalInformationTypeList.isEmpty())
						|| (paymentElectionTypeList != null && !paymentElectionTypeList.isEmpty())) {
					emeeDataChangesPHReport = new EmeeDataChangesPHReportDTO();
					if (summaryType != null) {
						emeeDataChangesPHReport.setEmployeeID(
								summaryType.getEmployeeID() == null ? "" : summaryType.getEmployeeID().getValue());
						emeeDataChangesPHReport
								.setEmployeeName(summaryType.getName() == null ? "" : summaryType.getName().getValue());
					}
					/*
					 * if (personalType != null) { String firstName =
					 * personalType.getFirstName() == null ? "" :
					 * personalType.getFirstName().getValue().getValue(); String
					 * middleName = personalType.getFirstName() == null ? "" :
					 * personalType.getFirstName().getValue().getValue(); String
					 * lastName = personalType.getFirstName() == null ? "" :
					 * personalType.getFirstName().getValue().getValue();
					 * emeeDataChangesPHReport.setEmployeeName(firstName + " " +
					 * middleName + " " + lastName); }
					 */
					/*
					 * if (additionalInformationTypeList != null &&
					 * !additionalInformationTypeList.isEmpty()) { for
					 * (AdditionalInformationType additionalInformationType :
					 * additionalInformationTypeList) {
					 * 
					 * emeeDataChangesPHReport
					 * .setMiddleInitial(additionalInformationType.
					 * getMiddleNameInitial() == null ? "" :
					 * additionalInformationType.getMiddleNameInitial().getValue
					 * ().getValue());
					 * 
					 * } }
					 */
					if (personalType != null) {
						emeeDataChangesPHReport.setMaritalStatus(personalType.getMaritalStatus() == null ? ""
								: personalType.getMaritalStatus().getValue().getValue());
					}
					if (paymentElectionTypeList != null && !paymentElectionTypeList.isEmpty()) {
						for (PaymentElectionType paymentElectionType : paymentElectionTypeList) {
							if (paymentElectionType.getOperation() != null && !paymentElectionType.getOperation()
									.getValue().value().equalsIgnoreCase("REMOVE")) {
								emeeDataChangesPHReport.setBankCode(paymentElectionType.getBankIDNumber() == null ? ""
										: paymentElectionType.getBankIDNumber().getValue().getValue());
								// newHiresPHReport.setBankBranch(paymentElectionType.getBankIDNumber()!=null?"":);
								emeeDataChangesPHReport
										.setBankAccountNumber(paymentElectionType.getAccountNumber() == null ? ""
												: paymentElectionType.getAccountNumber().getValue().getValue());
								emeeDataChangesPHReport.setAccountName(paymentElectionType.getBankAccountName() == null
										? "" : paymentElectionType.getBankAccountName().getValue().getValue());
							}
						}
					}
					if (personalType != null) {
						List<AddressLineType> addressLineTypeList = personalType.getFirstAddressLineData();
						if (addressLineTypeList != null && !addressLineTypeList.isEmpty()) {
							int i = 0;
							for (AddressLineType addressLineType : addressLineTypeList) {
								/*
								 * if (addressLineType.getLabel() != null &&
								 * addressLineType.getLabel().equalsIgnoreCase(
								 * "ADDRESS_LINE_1")) {
								 * emeeDataChangesPHReport.setAddress1(
								 * addressLineType.getValue()); } if
								 * (addressLineType.getLabel() != null &&
								 * addressLineType.getLabel().equalsIgnoreCase(
								 * "ADDRESS_LINE_2")) {
								 * emeeDataChangesPHReport.setAddress2(
								 * addressLineType.getValue()); } if
								 * (addressLineType.getLabel() != null &&
								 * addressLineType.getLabel().equalsIgnoreCase(
								 * "ADDRESS_LINE_3")) {
								 * emeeDataChangesPHReport.setAddress3(
								 * addressLineType.getValue()); }
								 */
								if (i == 0) {
									emeeDataChangesPHReport.setAddress1(addressLineType.getValue());
								}
								if (i == 1) {
									emeeDataChangesPHReport.setAddress2(addressLineType.getValue());
								}
								if (i == 2) {
									emeeDataChangesPHReport.setAddress3(addressLineType.getValue());
								}
								i++;
							}
						}
					}
					if (personalType != null) {
						emeeDataChangesPHReport.setPostalCode(personalType.getFirstPostalCode() == null ? ""
								: personalType.getFirstPostalCode().getValue().getValue());
						emeeDataChangesPHReport.setCountry(personalType.getFirstCountry() == null ? ""
								: personalType.getFirstCountry().getValue().getValue());
						emeeDataChangesPHReport.setPhone(personalType.getPhoneNumber() == null ? ""
								: personalType.getPhoneNumber().getValue().getValue());
						// emeeDataChangesPHReport.setMobilePhone(String.valueOf(personalType));
						emeeDataChangesPHReport.setEmailAddress(personalType.getFirstEmailAddress() == null ? ""
								: personalType.getFirstEmailAddress().getValue().getValue());
					}
					workDayPHReportDTO.setEmeeDataChangesPHReport(emeeDataChangesPHReport);
				}
			}
			if (!workdayPaygroupBatch.isNewEmployee()) {
				// RESIGNATION
				if (statusType != null) {
					if (statusType.getStaffingEvent() != null
							&& statusType.getStaffingEvent().getValue().getValue().equals("TRM")) {
						resignationsPHReport = new ResignationsPHReportDTO();
						if (summaryType != null) {
							resignationsPHReport.setEmployeeID(
									summaryType.getEmployeeID() == null ? "" : summaryType.getEmployeeID().getValue());
							resignationsPHReport.setEmployeeName(
									summaryType.getName() == null ? "" : summaryType.getName().getValue());
						}
						/*
						 * if (personalType != null) { String firstName =
						 * personalType.getFirstName() == null ? "" :
						 * personalType.getFirstName().getValue().getValue();
						 * String middleName = personalType.getFirstName() ==
						 * null ? "" :
						 * personalType.getFirstName().getValue().getValue();
						 * String lastName = personalType.getFirstName() == null
						 * ? "" :
						 * personalType.getFirstName().getValue().getValue();
						 * resignationsPHReport.setEmployeeName(firstName + " "
						 * + middleName + " " + lastName); }
						 */
						resignationsPHReport.setCessationReason(statusType.getPrimaryTerminationReason() == null ? ""
								: statusType.getPrimaryTerminationReason().getValue().getValue());
						;
						resignationsPHReport.setCessationDate(statusType.getTerminationDate() == null ? ""
								: statusType.getTerminationDate().getValue().getValue());
						;
						workDayPHReportDTO.setResignationsPHReport(resignationsPHReport);
					}

				}

				// CareerProgression
				if (positionTypeList != null && !positionTypeList.isEmpty()) {
					int count = 0;
					for (PositionType positionType : positionTypeList) {
						if (positionType.getOperation() != null
								&& !positionType.getOperation().getValue().value().equalsIgnoreCase("REMOVE")) {

							if (count == 0) {
								careerProgressionPHReport = new CareerProgressionPHReportDTO();
								if (summaryType != null) {
									careerProgressionPHReport.setEmployeeID(summaryType.getEmployeeID() == null ? ""
											: summaryType.getEmployeeID().getValue());
									careerProgressionPHReport.setEmployeeName(
											summaryType.getName() == null ? "" : summaryType.getName().getValue());
								}
								// careerProgressionPHReport.setCareerProgressionReason("");
								careerProgressionPHReport
										.setCareerProgressionDate(positionType.getEffectiveDate() == null ? ""
												: positionType.getEffectiveDate().getValue().getValue());
								careerProgressionPHReport.setDepartment(positionType.getOrganizationOne() == null ? ""
										: positionType.getOrganizationOne().getValue().getValue());
								// careerProgressionPHReport.setCostCenter(positionType.getEffectiveDate()==null?"":positionType.getEffectiveDate().getValue().getValue());
								careerProgressionPHReport.setJobTitle(positionType.getBusinessTitle() == null ? ""
										: positionType.getBusinessTitle().getValue().getValue());
								// careerProgressionPHReport.setRemarks(positionType.getEffectiveDate()==null?"":positionType.getEffectiveDate().getValue().getValue());
								workDayPHReportDTO.setCareerProgressionPHReport(careerProgressionPHReport);
							} else {
								WorkDayPHReportDTO workDayPHReportDTOExt = new WorkDayPHReportDTO();
								CareerProgressionPHReportDTO careerProgressionPHReportExt = new CareerProgressionPHReportDTO();

								if (summaryType != null) {
									careerProgressionPHReportExt.setEmployeeID(summaryType.getEmployeeID() == null ? ""
											: summaryType.getEmployeeID().getValue());
									careerProgressionPHReportExt.setEmployeeName(
											summaryType.getName() == null ? "" : summaryType.getName().getValue());
								}
								// careerProgressionPHReportExt.setCareerProgressionReason("");
								careerProgressionPHReportExt
										.setCareerProgressionDate(positionType.getEffectiveDate() == null ? ""
												: positionType.getEffectiveDate().getValue().getValue());
								careerProgressionPHReportExt.setDepartment(positionType.getOrganizationOne() == null
										? "" : positionType.getOrganizationOne().getValue().getValue());
								// careerProgressionPHReportExt.setCostCenter(positionType.getEffectiveDate()==null?"":positionType.getEffectiveDate().getValue().getValue());
								careerProgressionPHReportExt.setJobTitle(positionType.getBusinessTitle() == null ? ""
										: positionType.getBusinessTitle().getValue().getValue());
								// careerProgressionPHReportExt.setRemarks(positionType.getEffectiveDate()==null?"":positionType.getEffectiveDate().getValue().getValue());
								workDayPHReportDTOExt.setCareerProgressionPHReport(careerProgressionPHReportExt);
								workDayPHReportList.add(workDayPHReportDTOExt);
							}
							count++;
						}
					}
				}

				// BasicRateProgression
				if (salaryAndHourlyPlansTypeList != null && !salaryAndHourlyPlansTypeList.isEmpty()) {
					int count = 0;
					for (SalaryAndHourlyPlansType salaryAndHourlyPlansType : salaryAndHourlyPlansTypeList) {
						if (salaryAndHourlyPlansType.getOperation() != null && !salaryAndHourlyPlansType.getOperation()
								.getValue().getValue().equalsIgnoreCase("REMOVE")) {
							if (count == 0) {
								basicRateProgressionPHReport = new BasicRateProgressionPHReportDTO();
								if (summaryType != null) {
									basicRateProgressionPHReport.setEmployeeID(summaryType.getEmployeeID() == null ? ""
											: summaryType.getEmployeeID().getValue());
									basicRateProgressionPHReport.setEmployeeName(
											summaryType.getName() == null ? "" : summaryType.getName().getValue());
								}
								// basicRateProgressionPHReport.setBasicRateReason("");//HRO
								basicRateProgressionPHReport
										.setProgressionDate(salaryAndHourlyPlansType.getStartDate() == null ? ""
												: salaryAndHourlyPlansType.getStartDate().getValue().getValue());
								basicRateProgressionPHReport.setEffectiveDate(DateUtils.convertDate(String
										.valueOf(workdayPaygroupBatch.getWorkdayPaygroupBatch().getPayPeriodStartDate())
										.substring(0, 10), "yyyy-MM-dd", "dd-MMM-yyyy"));
								basicRateProgressionPHReport.setNewSalary(salaryAndHourlyPlansType.getAmount() == null
										? "" : salaryAndHourlyPlansType.getAmount().getValue().getValue());
								basicRateProgressionPHReport.setCurrency(salaryAndHourlyPlansType.getCurrency() == null
										? "" : salaryAndHourlyPlansType.getCurrency().getValue().getValue());
								basicRateProgressionPHReport
										.setFrequency(salaryAndHourlyPlansType.getFrequency() == null ? ""
												: salaryAndHourlyPlansType.getFrequency().getValue().getValue());
								// basicRateProgressionPHReport.setRemarks("");
								workDayPHReportDTO.setBasicRateProgressionPHReport(basicRateProgressionPHReport);
							} else {
								WorkDayPHReportDTO workDayPHReportDTOExt = new WorkDayPHReportDTO();
								BasicRateProgressionPHReportDTO basicRateProgressionPHReportExt = new BasicRateProgressionPHReportDTO();
								if (summaryType != null) {
									basicRateProgressionPHReportExt.setEmployeeID(summaryType.getEmployeeID() == null
											? "" : summaryType.getEmployeeID().getValue());
									basicRateProgressionPHReportExt.setEmployeeName(
											summaryType.getName() == null ? "" : summaryType.getName().getValue());
								}
								// basicRateProgressionPHReportExt.setBasicRateReason("");//HRO
								// basicRateProgressionPHReportExt.setProgressionDate("");
								basicRateProgressionPHReportExt.setEffectiveDate(DateUtils.convertDate(String
										.valueOf(workdayPaygroupBatch.getWorkdayPaygroupBatch().getPayPeriodStartDate())
										.substring(0, 10), "yyyy-MM-dd", "dd-MMM-yyyy"));
								basicRateProgressionPHReportExt
										.setNewSalary(salaryAndHourlyPlansType.getAmount() == null ? ""
												: salaryAndHourlyPlansType.getAmount().getValue().getValue());
								basicRateProgressionPHReportExt
										.setCurrency(salaryAndHourlyPlansType.getCurrency() == null ? ""
												: salaryAndHourlyPlansType.getCurrency().getValue().getValue());
								basicRateProgressionPHReportExt
										.setFrequency(salaryAndHourlyPlansType.getFrequency() == null ? ""
												: salaryAndHourlyPlansType.getFrequency().getValue().getValue());
								// basicRateProgressionPHReportExt.setRemarks("");
								workDayPHReportDTOExt.setBasicRateProgressionPHReport(basicRateProgressionPHReportExt);
								workDayPHReportList.add(workDayPHReportDTOExt);
							}
							count++;
						}
					}
				}
			}
			// Pay Items - (Recurring)
			if (earningsDeductionsTypeList != null && !earningsDeductionsTypeList.isEmpty()) {
				int count = 0;
				for (EarningsDeductionsType earningsDeductionsType : earningsDeductionsTypeList) {
					if (earningsDeductionsType.getOperation() != null
							&& !earningsDeductionsType.getOperation().getValue().value().equalsIgnoreCase("REMOVE")) {

						if (count == 0) {
							payItemsRecurringPHReport = new PayItemsRecurringPHReportDTO();

							if (summaryType != null) {
								payItemsRecurringPHReport.setEmployeeID(summaryType.getEmployeeID() == null ? ""
										: summaryType.getEmployeeID().getValue());
								payItemsRecurringPHReport.setEmployeeName(
										summaryType.getName() == null ? "" : summaryType.getName().getValue());
							}
							payItemsRecurringPHReport.setPayCode(earningsDeductionsType.getCode() == null ? ""
									: earningsDeductionsType.getCode().getValue().getValue());
							// payItemsRecurringPHReport.setPayDescription("");
							payItemsRecurringPHReport.setRecurringAmount(earningsDeductionsType.getAmount() == null ? ""
									: earningsDeductionsType.getAmount().getValue().getValue());
							String startDate = earningsDeductionsType.getStartDate() == null ? ""
									: earningsDeductionsType.getStartDate().getValue().getValue();
							if (startDate.length() >= 10) {
								payItemsRecurringPHReport.setStartYear(startDate.substring(0, 4));
								payItemsRecurringPHReport.setStartMonth(startDate.substring(6, 7));
							}
							payItemsRecurringPHReport.setEndYear("2099");
							payItemsRecurringPHReport.setEndMonth("12");
							payItemsRecurringPHReport.setEndCounter("0");
							payItemsRecurringPHReport.setSuspendRecurring("0");
							// payItemsRecurringPHReport.setRemarks("");
							workDayPHReportDTO.setPayItemsRecurringPHReportDTO(payItemsRecurringPHReport);
						} else {
							WorkDayPHReportDTO workDayPHReportDTOExt = new WorkDayPHReportDTO();
							PayItemsRecurringPHReportDTO payItemsRecurringPHReportExt = new PayItemsRecurringPHReportDTO();
							if (summaryType != null) {
								payItemsRecurringPHReportExt.setEmployeeID(summaryType.getEmployeeID() == null ? ""
										: summaryType.getEmployeeID().getValue());
								payItemsRecurringPHReportExt.setEmployeeName(
										summaryType.getName() == null ? "" : summaryType.getName().getValue());
							}
							payItemsRecurringPHReportExt.setPayCode(earningsDeductionsType.getCode() == null ? ""
									: earningsDeductionsType.getCode().getValue().getValue());
							// payItemsRecurringPHReportExt.setPayDescription("");
							payItemsRecurringPHReportExt.setRecurringAmount(earningsDeductionsType.getAmount() == null
									? "" : earningsDeductionsType.getAmount().getValue().getValue());
							String startDate = earningsDeductionsType.getStartDate() == null ? ""
									: earningsDeductionsType.getStartDate().getValue().getValue();
							if (startDate.length() >= 10) {
								payItemsRecurringPHReportExt.setStartYear(startDate.substring(0, 4));
								payItemsRecurringPHReportExt.setStartMonth(startDate.substring(6, 7));
							}
							payItemsRecurringPHReportExt.setEndYear("2099");
							payItemsRecurringPHReportExt.setEndMonth("12");
							payItemsRecurringPHReportExt.setEndCounter("0");
							payItemsRecurringPHReportExt.setSuspendRecurring("0");
							// payItemsRecurringPHReportExt.setRemarks("");
							workDayPHReportDTOExt.setPayItemsRecurringPHReportDTO(payItemsRecurringPHReportExt);
							workDayPHReportList.add(workDayPHReportDTOExt);
						}
						count++;
					} else {
						WorkDayPHReportDTO workDayPHReportDTOExt = new WorkDayPHReportDTO();
						PayItemsRecurringPHReportDTO payItemsRecurringPHReportExt = new PayItemsRecurringPHReportDTO();

						if (summaryType != null) {
							payItemsRecurringPHReportExt.setEmployeeID(
									summaryType.getEmployeeID() == null ? "" : summaryType.getEmployeeID().getValue());
							payItemsRecurringPHReportExt.setEmployeeName(
									summaryType.getName() == null ? "" : summaryType.getName().getValue());
						}
						payItemsRecurringPHReportExt.setPayCode(earningsDeductionsType.getCode() == null ? ""
								: earningsDeductionsType.getCode().getValue().getValue());
						// payItemsRecurringPHReport.setPayDescription("");
						payItemsRecurringPHReportExt.setRecurringAmount(earningsDeductionsType.getAmount() == null ? ""
								: earningsDeductionsType.getAmount().getValue().getValue());
						String startDate = earningsDeductionsType.getStartDate() == null ? ""
								: earningsDeductionsType.getStartDate().getValue().getValue();
						if (startDate.length() >= 10) {
							payItemsRecurringPHReportExt.setStartYear(startDate.substring(0, 4));
							payItemsRecurringPHReportExt.setStartMonth(startDate.substring(6, 7));
						}
						// payItemsRecurringPHReportExt.setEndYear("2099");
						// payItemsRecurringPHReportExt.setEndMonth("12");
						// payItemsRecurringPHReportExt.setEndCounter("0");
						payItemsRecurringPHReportExt.setSuspendRecurring("1");
						// payItemsRecurringPHReportExt.setRemarks("");

						workDayPHReportDTOExt.setPayItemsRecurringPHReportDTO(payItemsRecurringPHReportExt);
						workDayPHReportList.add(workDayPHReportDTOExt);

					}
				}
			}

			/*
			 * workDayPHReportDTO.setBasicRateProgressionPHReport(
			 * basicRateProgressionPHReport);
			 * workDayPHReportDTO.setPayItemsAdhocPHReportDTO(
			 * payItemsAdhocPHReportDTO);
			 * 
			 * workDayPHReportDTO.setResignationsPHReport(
			 * resignationsPHReport);
			 */

			workDayPHReportList.add(workDayPHReportDTO);

			return workDayPHReportList;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private void createSheetForEmployee(XSSFWorkbook workbook, List<WorkDayReportDTO> workDayReportDTOList) {

		XSSFSheet sheet1 = workbook.createSheet("1. New Hires PH");
		//sheet1.setTabColor(3);

		createSheetForNewHires(workbook, sheet1);

		XSSFSheet sheet2 = workbook.createSheet("2. Emee Data Changes PH");
		///sheet2.setTabColor(3);

		createSheetForEmeeDataChanges(workbook, sheet2);

		XSSFSheet sheet3 = workbook.createSheet("3. Resignations PH");
		///sheet3.setTabColor(3);

		createSheetForResignations(workbook, sheet3);

		XSSFSheet sheet4 = workbook.createSheet("4. Career Progression PH");
		//sheet4.setTabColor(3);

		createSheetForCareerProgression(workbook, sheet4);

		XSSFSheet sheet5 = workbook.createSheet("5. Basic Rate Progression PH");
		//sheet5.setTabColor(3);

		createSheetForBasicRateProgression(workbook, sheet5);

		XSSFSheet sheet6 = workbook.createSheet("6. Pay Items - Recurring");
		//sheet6.setTabColor(IndexedColors.ORCHID.getIndex());

		createSheetForPayItemsRecurring(workbook, sheet6);

		int rowCount1 = 5, rowCount2 = 5, rowCount3 = 5, rowCount4 = 5, rowCount5 = 5, rowCount6 = 5;

		if (workDayReportDTOList != null && !workDayReportDTOList.isEmpty()) {

			for (WorkDayReportDTO workDayReportDTO : workDayReportDTOList) {

				WorkDayPHReportDTO workDayPHReportDTO = (WorkDayPHReportDTO) workDayReportDTO;

				if (workDayPHReportDTO.getNewHiresPHReport() != null) {
					XSSFRow rowsheet1 = sheet1.createRow((short) rowCount1++);
					rowsheet1.setHeightInPoints((short) 20);
					setNewHires(workbook, rowsheet1, workDayPHReportDTO);
				}

				if (workDayPHReportDTO.getEmeeDataChangesPHReport() != null) {
					XSSFRow rowsheet2 = sheet2.createRow((short) rowCount2++);
					rowsheet2.setHeightInPoints((short) 20);
					setEmeeDataChanges(workbook, rowsheet2, workDayPHReportDTO);
				}

				if (workDayPHReportDTO.getResignationsPHReport() != null) {
					XSSFRow rowsheet3 = sheet3.createRow((short) rowCount3++);
					rowsheet3.setHeightInPoints((short) 20);
					setResignations(workbook, rowsheet3, workDayPHReportDTO);
				}

				if (workDayPHReportDTO.getCareerProgressionPHReport() != null) {
					XSSFRow rowsheet4 = sheet4.createRow((short) rowCount4++);
					rowsheet4.setHeightInPoints((short) 20);
					setCareerProgression(workbook, rowsheet4, workDayPHReportDTO);
				}

				if (workDayPHReportDTO.getBasicRateProgressionPHReport() != null) {
					XSSFRow rowsheet5 = sheet5.createRow((short) rowCount5++);
					rowsheet5.setHeightInPoints((short) 20);
					setBasicRateProgression(workbook, rowsheet5, workDayPHReportDTO);
				}

				if (workDayPHReportDTO.getPayItemsRecurringPHReportDTO() != null) {
					XSSFRow rowsheet6 = sheet6.createRow((short) rowCount6++);
					rowsheet6.setHeightInPoints((short) 20);
					setPayItemsRecurring(workbook, rowsheet6, workDayPHReportDTO);
				}
			}
		}

	}

	private void createSheetForNewHires(XSSFWorkbook workbook, XSSFSheet sheet) {

		XSSFFont fontHeader = workbook.createFont();
		fontHeader.setFontName("Calibri");
		fontHeader.setItalic(true);
		fontHeader.setColor(HSSFColor.WHITE.index);
		fontHeader.setBold(true);
		fontHeader.setFontHeightInPoints((short) 15);

		XSSFCellStyle style = workbook.createCellStyle();
		style.setFont(fontHeader);
		style.setFillForegroundColor(new XSSFColor(new java.awt.Color(255, 0, 0)));
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);

		XSSFRow row0 = sheet.createRow((short) 0);
		row0.setHeightInPoints((short) 15);

		XSSFRow row1 = sheet.createRow((short) 1);
		row1.setHeightInPoints((short) 28);

		XSSFCell cell1A = (XSSFCell) row1.createCell((short) 0);
		cell1A.setCellStyle(style);
		cell1A.setCellValue("            NEW HIRES");
		sheet.setColumnWidth(0, 4200);

		XSSFCell cell1B = (XSSFCell) row1.createCell((short) 1);
		cell1B.setCellStyle(style);
		sheet.setColumnWidth(1, 4200);

		sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 1));
		sheet.setColumnWidth(2, 7300);

		XSSFFont font = workbook.createFont();
		font.setFontName("Calibri");
		font.setItalic(false);
		font.setColor(HSSFColor.BLACK.index);
		font.setBold(false);
		font.setFontHeightInPoints((short) 10);

		XSSFCellStyle stylefont = workbook.createCellStyle();
		stylefont.setFont(font);
		stylefont.setWrapText(true);
		fontHeader.setColor(HSSFColor.WHITE.index);

		XSSFCell cell1C = (XSSFCell) row1.createCell((short) 2);
		cell1C.setCellValue("List of all Newly hired employees after the last payroll cut off date");
		cell1C.setCellStyle(stylefont);

		XSSFRow row2 = sheet.createRow((short) 2);
		row2.setHeightInPoints((short) 12);

		XSSFFont font2Row = workbook.createFont();
		font2Row.setFontName("Calibri");
		font2Row.setColor(HSSFColor.RED.index);
		font2Row.setItalic(true);
		font2Row.setBold(true);
		font2Row.setFontHeightInPoints((short) 10);

		XSSFCellStyle stylefont2Row = workbook.createCellStyle();
		stylefont2Row.setFont(font2Row);

		XSSFCell cell2A = (XSSFCell) row2.createCell((short) 0);
		cell2A.setCellStyle(stylefont2Row);
		cell2A.setCellValue("* Mandatory Fields");

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

		XSSFRow row3 = sheet.createRow((short) 3);
		row3.setHeightInPoints((short) 14);

		XSSFCell cell3A = (XSSFCell) row3.createCell((short) 0);
		cell3A.setCellStyle(stylefont3Row);
		cell3A.setCellValue(" Employee ID *");

		XSSFCell cell3B = (XSSFCell) row3.createCell((short) 1);
		cell3B.setCellStyle(stylefont3Row);
		cell3B.setCellValue("First Name*");

		XSSFCell cell3C = (XSSFCell) row3.createCell((short) 2);
		cell3C.setCellStyle(stylefont3Row);
		cell3C.setCellValue("Last Name*");

		XSSFCell cell3D = (XSSFCell) row3.createCell((short) 3);
		cell3D.setCellStyle(stylefont3Row);
		cell3D.setCellValue("Middle Name");

		sheet.setColumnWidth(3, 4200);

		XSSFCell cell3E = (XSSFCell) row3.createCell((short) 4);
		cell3E.setCellStyle(stylefont3Row);
		cell3E.setCellValue("Middle Initial");

		sheet.setColumnWidth(4, 4200);

		XSSFCell cell3F = (XSSFCell) row3.createCell((short) 5);
		cell3F.setCellStyle(stylefont3Row);
		cell3F.setCellValue("Gender*");

		sheet.setColumnWidth(5, 4200);

		XSSFCell cell3G = (XSSFCell) row3.createCell((short) 6);
		cell3G.setCellStyle(stylefont3Row);
		cell3G.setCellValue("Salutation");

		sheet.setColumnWidth(6, 4200);

		XSSFCell cell3H = (XSSFCell) row3.createCell((short) 7);
		cell3H.setCellStyle(stylefont3Row);
		cell3H.setCellValue("Marital Status*");

		sheet.setColumnWidth(7, 4200);

		XSSFCell cell3I = (XSSFCell) row3.createCell((short) 8);
		cell3I.setCellStyle(stylefont3Row);
		cell3I.setCellValue("Birth Date*");

		sheet.setColumnWidth(8, 4200);

		XSSFCell cell3J = (XSSFCell) row3.createCell((short) 9);
		cell3J.setCellStyle(stylefont3Row);
		cell3J.setCellValue("Address*");

		sheet.setColumnWidth(9, 13000);

		XSSFCell cell3K = (XSSFCell) row3.createCell((short) 10);
		cell3K.setCellStyle(stylefont3Row);
		cell3K.setCellValue(" Address 2*");

		sheet.setColumnWidth(10, 8000);

		XSSFCell cell3L = (XSSFCell) row3.createCell((short) 11);
		cell3L.setCellStyle(stylefont3Row);
		cell3L.setCellValue("Address 3");

		sheet.setColumnWidth(11, 4200);

		XSSFCell cell3M = (XSSFCell) row3.createCell((short) 12);
		cell3M.setCellStyle(stylefont3Row);
		cell3M.setCellValue("City*");

		sheet.setColumnWidth(12, 4200);

		XSSFCell cell3N = (XSSFCell) row3.createCell((short) 13);
		cell3N.setCellStyle(stylefont3Row);
		cell3N.setCellValue("Zip Code*");

		sheet.setColumnWidth(13, 4200);

		XSSFCell cell3O = (XSSFCell) row3.createCell((short) 14);
		cell3O.setCellStyle(stylefont3Row);
		cell3O.setCellValue("Country*");

		sheet.setColumnWidth(14, 4200);

		XSSFCell cell3P = (XSSFCell) row3.createCell((short) 15);
		cell3P.setCellStyle(stylefont3Row);
		cell3P.setCellValue("Phone");

		sheet.setColumnWidth(15, 4200);

		XSSFCell cell3Q = (XSSFCell) row3.createCell((short) 16);
		cell3Q.setCellStyle(stylefont3Row);
		cell3Q.setCellValue("Mobile Phone");

		sheet.setColumnWidth(16, 4200);

		XSSFCell cell3R = (XSSFCell) row3.createCell((short) 17);
		cell3R.setCellStyle(stylefont3Row);
		cell3R.setCellValue("Email*");

		sheet.setColumnWidth(17, 4200);

		XSSFCell cell3S = (XSSFCell) row3.createCell((short) 18);
		cell3S.setCellStyle(stylefont3Row);
		cell3S.setCellValue("Job Title*");

		sheet.setColumnWidth(18, 4200);

		XSSFCell cell3T = (XSSFCell) row3.createCell((short) 19);
		cell3T.setCellStyle(stylefont3Row);
		cell3T.setCellValue("Start Date*");

		sheet.setColumnWidth(19, 4200);

		XSSFCell cell3U = (XSSFCell) row3.createCell((short) 20);
		cell3U.setCellStyle(stylefont3Row);
		cell3U.setCellValue("Department");

		sheet.setColumnWidth(20, 7400);

		XSSFCell cell3V = (XSSFCell) row3.createCell((short) 21);
		cell3V.setCellStyle(stylefont3Row);
		cell3V.setCellValue("Cost Center");

		sheet.setColumnWidth(21, 7400);

		XSSFCell cell3W = (XSSFCell) row3.createCell((short) 22);
		cell3W.setCellStyle(stylefont3Row);
		cell3W.setCellValue("Emp Tax ID*");

		sheet.setColumnWidth(22, 4200);

		XSSFCell cell3X = (XSSFCell) row3.createCell((short) 23);
		cell3X.setCellStyle(stylefont3Row);
		cell3X.setCellValue("Dependant Name 1");

		sheet.setColumnWidth(23, 4500);

		XSSFCell cell3Y = (XSSFCell) row3.createCell((short) 24);
		cell3Y.setCellStyle(stylefont3Row);
		cell3Y.setCellValue("Date of Birth");

		sheet.setColumnWidth(24, 4200);

		XSSFCell cell3Z = (XSSFCell) row3.createCell((short) 25);
		cell3Z.setCellStyle(stylefont3Row);
		cell3Z.setCellValue("Dependant Name 2");

		sheet.setColumnWidth(25, 4500);

		XSSFCell cell3AA = (XSSFCell) row3.createCell((short) 26);
		cell3AA.setCellStyle(stylefont3Row);
		cell3AA.setCellValue("");

		sheet.setColumnWidth(26, 4200);

		XSSFCell cell3AB = (XSSFCell) row3.createCell((short) 27);
		cell3AB.setCellStyle(stylefont3Row);
		cell3AB.setCellValue("Dependant Name 3");

		sheet.setColumnWidth(27, 4500);

		XSSFCell cell3AC = (XSSFCell) row3.createCell((short) 28);
		cell3AC.setCellStyle(stylefont3Row);
		cell3AC.setCellValue("");

		sheet.setColumnWidth(28, 4200);

		XSSFCell cell3AD = (XSSFCell) row3.createCell((short) 29);
		cell3AD.setCellStyle(stylefont3Row);
		cell3AD.setCellValue("Dependant Name 4");

		sheet.setColumnWidth(29, 4500);

		XSSFCell cell3AE = (XSSFCell) row3.createCell((short) 30);
		cell3AE.setCellStyle(stylefont3Row);
		cell3AE.setCellValue("");

		sheet.setColumnWidth(30, 4200);

		XSSFCell cell3AF = (XSSFCell) row3.createCell((short) 31);
		cell3AF.setCellStyle(stylefont3Row);
		cell3AF.setCellValue("TIN Number*");

		sheet.setColumnWidth(31, 4200);

		XSSFCell cell3AG = (XSSFCell) row3.createCell((short) 32);
		cell3AG.setCellStyle(stylefont3Row);
		cell3AG.setCellValue("PHIC Number*");

		sheet.setColumnWidth(32, 4200);

		XSSFCell cell3AH = (XSSFCell) row3.createCell((short) 33);
		cell3AH.setCellStyle(stylefont3Row);
		cell3AH.setCellValue("HDMF Number*");

		sheet.setColumnWidth(33, 4200);

		XSSFCell cell3AI = (XSSFCell) row3.createCell((short) 34);
		cell3AI.setCellStyle(stylefont3Row);
		cell3AI.setCellValue("SSS Number*");

		sheet.setColumnWidth(34, 4200);

		XSSFCell cell3AJ = (XSSFCell) row3.createCell((short) 35);
		cell3AJ.setCellStyle(stylefont3Row);
		cell3AJ.setCellValue("Monthly Basic Wage*");

		sheet.setColumnWidth(35, 5200);

		XSSFCell cell3AK = (XSSFCell) row3.createCell((short) 36);
		cell3AK.setCellStyle(stylefont3Row);
		cell3AK.setCellValue("Bank*");

		sheet.setColumnWidth(36, 4200);

		XSSFCell cell3AL = (XSSFCell) row3.createCell((short) 37);
		cell3AL.setCellStyle(stylefont3Row);
		cell3AL.setCellValue("Bank Branch");

		sheet.setColumnWidth(37, 4200);

		XSSFCell cell3AM = (XSSFCell) row3.createCell((short) 38);
		cell3AM.setCellStyle(stylefont3Row);
		cell3AM.setCellValue("Account Number*");

		sheet.setColumnWidth(38, 4200);

		XSSFCell cell3AN = (XSSFCell) row3.createCell((short) 39);
		cell3AN.setCellStyle(stylefont3Row);
		cell3AN.setCellValue("Account Name*");

		sheet.setColumnWidth(39, 4200);

		XSSFFont font4Row = workbook.createFont();
		font4Row.setFontName("Calibri");
		font4Row.setColor(HSSFColor.BLACK.index);
		font4Row.setItalic(true);
		font4Row.setBold(false);
		font4Row.setFontHeightInPoints((short) 10);

		XSSFCellStyle stylefont4Row = workbook.createCellStyle();
		stylefont4Row.setFont(font4Row);
		stylefont4Row.setFillForegroundColor(new XSSFColor(new java.awt.Color(178, 223, 249)));
		stylefont4Row.setFillPattern(CellStyle.SOLID_FOREGROUND);
		stylefont4Row.setAlignment(HorizontalAlignment.CENTER);
		stylefont4Row.setVerticalAlignment(VerticalAlignment.CENTER);
		stylefont4Row.setWrapText(true);

		XSSFRow row4 = sheet.createRow((short) 4);
		row4.setHeightInPoints((short) 30);

		XSSFCell cell4A = (XSSFCell) row4.createCell((short) 0);
		cell4A.setCellStyle(stylefont4Row);
		cell4A.setCellValue("Maximum of 20 characters");

		XSSFCell cell4B = (XSSFCell) row4.createCell((short) 1);
		cell4B.setCellStyle(stylefont4Row);
		cell4B.setCellValue("");

		XSSFCell cell4C = (XSSFCell) row4.createCell((short) 2);
		cell4C.setCellStyle(stylefont4Row);
		cell4C.setCellValue("");

		XSSFCell cell4D = (XSSFCell) row4.createCell((short) 3);
		cell4D.setCellStyle(stylefont4Row);
		cell4D.setCellValue("");

		XSSFCell cell4E = (XSSFCell) row4.createCell((short) 4);
		cell4E.setCellStyle(stylefont4Row);
		cell4E.setCellValue("");

		XSSFCell cell4F = (XSSFCell) row4.createCell((short) 5);
		cell4F.setCellStyle(stylefont4Row);
		cell4F.setCellValue("");

		XSSFCell cell4G = (XSSFCell) row4.createCell((short) 6);
		cell4G.setCellStyle(stylefont4Row);
		cell4G.setCellValue("");

		XSSFCell cell4H = (XSSFCell) row4.createCell((short) 7);
		cell4H.setCellStyle(stylefont4Row);
		cell4H.setCellValue("");

		XSSFCell cell4I = (XSSFCell) row4.createCell((short) 8);
		cell4I.setCellStyle(stylefont4Row);
		cell4I.setCellValue("Format: mm/dd/yyyy");

		XSSFCell cell4J = (XSSFCell) row4.createCell((short) 9);
		cell4J.setCellStyle(stylefont4Row);
		cell4J.setCellValue("");

		XSSFCell cell4K = (XSSFCell) row4.createCell((short) 10);
		cell4K.setCellStyle(stylefont4Row);
		cell4K.setCellValue("");

		XSSFCell cell4L = (XSSFCell) row4.createCell((short) 11);
		cell4L.setCellStyle(stylefont4Row);
		cell4L.setCellValue("");

		XSSFCell cell4M = (XSSFCell) row4.createCell((short) 12);
		cell4M.setCellStyle(stylefont4Row);
		cell4M.setCellValue("");

		XSSFCell cell4N = (XSSFCell) row4.createCell((short) 13);
		cell4N.setCellStyle(stylefont4Row);
		cell4N.setCellValue("");

		XSSFCell cell4O = (XSSFCell) row4.createCell((short) 14);
		cell4O.setCellStyle(stylefont4Row);
		cell4O.setCellValue("");

		XSSFCell cell4P = (XSSFCell) row4.createCell((short) 15);
		cell4P.setCellStyle(stylefont4Row);
		cell4P.setCellValue("");

		XSSFCell cell4Q = (XSSFCell) row4.createCell((short) 16);
		cell4Q.setCellStyle(stylefont4Row);
		cell4Q.setCellValue("");

		XSSFCell cell4R = (XSSFCell) row4.createCell((short) 17);
		cell4R.setCellStyle(stylefont4Row);
		cell4R.setCellValue("");

		XSSFCell cell4S = (XSSFCell) row4.createCell((short) 18);
		cell4S.setCellStyle(stylefont4Row);
		cell4S.setCellValue("");

		XSSFCell cell4T = (XSSFCell) row4.createCell((short) 19);
		cell4T.setCellStyle(stylefont4Row);
		cell4T.setCellValue("Format: dd/mm/yyyy");

		XSSFCell cell4U = (XSSFCell) row4.createCell((short) 20);
		cell4U.setCellStyle(stylefont4Row);
		cell4U.setCellValue("(Select New Changed Value)       (Leave Blank If No Changes)");

		XSSFCell cell4V = (XSSFCell) row4.createCell((short) 21);
		cell4V.setCellStyle(stylefont4Row);
		cell4V.setCellValue("(Select New Changed Value)       (Leave Blank If No Changes)");

		XSSFCell cell4W = (XSSFCell) row4.createCell((short) 22);
		cell4W.setCellStyle(stylefont4Row);
		cell4W.setCellValue("Tax Status");

		XSSFCell cell4X = (XSSFCell) row4.createCell((short) 23);
		cell4X.setCellStyle(stylefont4Row);
		cell4X.setCellValue("Indicate value");

		XSSFCell cell4Y = (XSSFCell) row4.createCell((short) 24);
		cell4Y.setCellStyle(stylefont4Row);
		cell4Y.setCellValue("Format: dd/mm/yyyy");

		XSSFCell cell4Z = (XSSFCell) row4.createCell((short) 25);
		cell4Z.setCellStyle(stylefont4Row);
		cell4Z.setCellValue("Indicate value");

		XSSFCell cell4AA = (XSSFCell) row4.createCell((short) 26);
		cell4AA.setCellStyle(stylefont4Row);
		cell4AA.setCellValue("Format: dd/mm/yyyy");

		XSSFCell cell4AB = (XSSFCell) row4.createCell((short) 27);
		cell4AB.setCellStyle(stylefont4Row);
		cell4AB.setCellValue("Indicate value");

		XSSFCell cell4AC = (XSSFCell) row4.createCell((short) 28);
		cell4AC.setCellStyle(stylefont4Row);
		cell4AC.setCellValue("Format: dd/mm/yyyy");

		XSSFCell cell4AD = (XSSFCell) row4.createCell((short) 29);
		cell4AD.setCellStyle(stylefont4Row);
		cell4AD.setCellValue("Indicate value");

		XSSFCell cell4AE = (XSSFCell) row4.createCell((short) 30);
		cell4AE.setCellStyle(stylefont4Row);
		cell4AE.setCellValue("Format: dd/mm/yyyy");

		XSSFCell cell4AF = (XSSFCell) row4.createCell((short) 31);
		cell4AF.setCellStyle(stylefont4Row);
		cell4AF.setCellValue("Format:XXX-XXX-XXX");

		XSSFCell cell4AG = (XSSFCell) row4.createCell((short) 32);
		cell4AG.setCellStyle(stylefont4Row);
		cell4AG.setCellValue("Format:XX-XXXXXXXXX-X");

		XSSFCell cell4AH = (XSSFCell) row4.createCell((short) 33);
		cell4AH.setCellStyle(stylefont4Row);
		cell4AH.setCellValue("Format:XXXX-XXXX-XXXX");

		XSSFCell cell4AI = (XSSFCell) row4.createCell((short) 34);
		cell4AI.setCellStyle(stylefont4Row);
		cell4AI.setCellValue("Format:XX-XXXXXXX-X");

		XSSFCell cell4AJ = (XSSFCell) row4.createCell((short) 35);
		cell4AJ.setCellStyle(stylefont4Row);
		cell4AJ.setCellValue("");

		XSSFCell cell4AK = (XSSFCell) row4.createCell((short) 36);
		cell4AK.setCellStyle(stylefont4Row);
		cell4AK.setCellValue("");

		XSSFCell cell4AL = (XSSFCell) row4.createCell((short) 37);
		cell4AL.setCellStyle(stylefont4Row);
		cell4AL.setCellValue("");

		XSSFCell cell4AM = (XSSFCell) row4.createCell((short) 38);
		cell4AM.setCellStyle(stylefont4Row);
		cell4AM.setCellValue("");

		XSSFCell cell4AN = (XSSFCell) row4.createCell((short) 39);
		cell4AN.setCellStyle(stylefont4Row);
		cell4AN.setCellValue("");

	}

	private void setNewHires(XSSFWorkbook workbook, XSSFRow row4, WorkDayPHReportDTO workDayPHReportDTO) {

		XSSFFont font4Row = workbook.createFont();
		font4Row.setFontName("Calibri");
		font4Row.setColor(HSSFColor.BLACK.index);
		font4Row.setBold(false);
		font4Row.setFontHeightInPoints((short) 10);

		NewHiresPHReportDTO newHiresPHReportDTO = workDayPHReportDTO.getNewHiresPHReport();

		XSSFCellStyle stylefont4Row = workbook.createCellStyle();
		stylefont4Row.setFont(font4Row);
		stylefont4Row.setAlignment(HorizontalAlignment.CENTER);
		stylefont4Row.setVerticalAlignment(VerticalAlignment.CENTER);
		stylefont4Row.setWrapText(true);

		XSSFCell cell4A = (XSSFCell) row4.createCell((short) 0);
		cell4A.setCellStyle(stylefont4Row);
		cell4A.setCellValue(newHiresPHReportDTO.getEmployeeID());

		XSSFCell cell4B = (XSSFCell) row4.createCell((short) 1);
		cell4B.setCellStyle(stylefont4Row);
		cell4B.setCellValue(newHiresPHReportDTO.getFirstName());

		XSSFCell cell4C = (XSSFCell) row4.createCell((short) 2);
		cell4C.setCellStyle(stylefont4Row);
		cell4C.setCellValue(newHiresPHReportDTO.getLastName());

		XSSFCell cell4D = (XSSFCell) row4.createCell((short) 3);
		cell4D.setCellStyle(stylefont4Row);
		cell4D.setCellValue(newHiresPHReportDTO.getMiddleName());

		XSSFCell cell4E = (XSSFCell) row4.createCell((short) 4);
		cell4E.setCellStyle(stylefont4Row);
		cell4E.setCellValue(newHiresPHReportDTO.getMiddleInitial());

		XSSFCell cell4F = (XSSFCell) row4.createCell((short) 5);
		cell4F.setCellStyle(stylefont4Row);
		cell4F.setCellValue(newHiresPHReportDTO.getGender());

		XSSFCell cell4G = (XSSFCell) row4.createCell((short) 6);
		cell4G.setCellStyle(stylefont4Row);
		cell4G.setCellValue(newHiresPHReportDTO.getSalutation());

		XSSFCell cell4H = (XSSFCell) row4.createCell((short) 7);
		cell4H.setCellStyle(stylefont4Row);
		cell4H.setCellValue(newHiresPHReportDTO.getMaritalStatus());

		XSSFCell cell4I = (XSSFCell) row4.createCell((short) 8);
		cell4I.setCellStyle(stylefont4Row);
		cell4I.setCellValue(newHiresPHReportDTO.getBirthDate());

		XSSFCell cell4J = (XSSFCell) row4.createCell((short) 9);
		cell4J.setCellStyle(stylefont4Row);
		cell4J.setCellValue(newHiresPHReportDTO.getAddress());

		XSSFCell cell4K = (XSSFCell) row4.createCell((short) 10);
		cell4K.setCellStyle(stylefont4Row);
		cell4K.setCellValue(newHiresPHReportDTO.getAddress2());

		XSSFCell cell4L = (XSSFCell) row4.createCell((short) 11);
		cell4L.setCellStyle(stylefont4Row);
		cell4L.setCellValue(newHiresPHReportDTO.getAddress3());

		XSSFCell cell4M = (XSSFCell) row4.createCell((short) 12);
		cell4M.setCellStyle(stylefont4Row);
		cell4M.setCellValue(newHiresPHReportDTO.getCity());

		XSSFCell cell4N = (XSSFCell) row4.createCell((short) 13);
		cell4N.setCellStyle(stylefont4Row);
		cell4N.setCellValue(newHiresPHReportDTO.getZipCode());

		XSSFCell cell4O = (XSSFCell) row4.createCell((short) 14);
		cell4O.setCellStyle(stylefont4Row);
		cell4O.setCellValue(newHiresPHReportDTO.getCountry());

		XSSFCell cell4P = (XSSFCell) row4.createCell((short) 15);
		cell4P.setCellStyle(stylefont4Row);
		cell4P.setCellValue(newHiresPHReportDTO.getPhone());

		XSSFCell cell4Q = (XSSFCell) row4.createCell((short) 16);
		cell4Q.setCellStyle(stylefont4Row);
		cell4Q.setCellValue(newHiresPHReportDTO.getMobilePhone());

		XSSFCell cell4R = (XSSFCell) row4.createCell((short) 17);
		cell4R.setCellStyle(stylefont4Row);
		cell4R.setCellValue(newHiresPHReportDTO.getEmail());

		XSSFCell cell4S = (XSSFCell) row4.createCell((short) 18);
		cell4S.setCellStyle(stylefont4Row);
		cell4S.setCellValue(newHiresPHReportDTO.getJobTitle());

		XSSFCell cell4T = (XSSFCell) row4.createCell((short) 19);
		cell4T.setCellStyle(stylefont4Row);
		cell4T.setCellValue(newHiresPHReportDTO.getStartDate());

		XSSFCell cell4U = (XSSFCell) row4.createCell((short) 20);
		cell4U.setCellStyle(stylefont4Row);
		cell4U.setCellValue(newHiresPHReportDTO.getDepartment());

		XSSFCell cell4V = (XSSFCell) row4.createCell((short) 21);
		cell4V.setCellStyle(stylefont4Row);
		cell4V.setCellValue(newHiresPHReportDTO.getCostCenter());

		XSSFCell cell4W = (XSSFCell) row4.createCell((short) 22);
		cell4W.setCellStyle(stylefont4Row);
		cell4W.setCellValue(newHiresPHReportDTO.getEmpTaxID());

		XSSFCell cell4X = (XSSFCell) row4.createCell((short) 23);
		cell4X.setCellStyle(stylefont4Row);
		cell4X.setCellValue(newHiresPHReportDTO.getDependantName1());

		XSSFCell cell4Y = (XSSFCell) row4.createCell((short) 24);
		cell4Y.setCellStyle(stylefont4Row);
		cell4Y.setCellValue(newHiresPHReportDTO.getDateofBirth());

		XSSFCell cell4Z = (XSSFCell) row4.createCell((short) 25);
		cell4Z.setCellStyle(stylefont4Row);
		cell4Z.setCellValue(newHiresPHReportDTO.getDependantName2());

		XSSFCell cell4AA = (XSSFCell) row4.createCell((short) 26);
		cell4AA.setCellStyle(stylefont4Row);
		cell4AA.setCellValue("");

		XSSFCell cell4AB = (XSSFCell) row4.createCell((short) 27);
		cell4AB.setCellStyle(stylefont4Row);
		cell4AB.setCellValue(newHiresPHReportDTO.getDependantName3());

		XSSFCell cell4AC = (XSSFCell) row4.createCell((short) 28);
		cell4AC.setCellStyle(stylefont4Row);
		cell4AC.setCellValue("");

		XSSFCell cell4AD = (XSSFCell) row4.createCell((short) 29);
		cell4AD.setCellStyle(stylefont4Row);
		cell4AD.setCellValue(newHiresPHReportDTO.getDependantName4());

		XSSFCell cell4AE = (XSSFCell) row4.createCell((short) 30);
		cell4AE.setCellStyle(stylefont4Row);
		cell4AE.setCellValue("");

		XSSFCell cell4AF = (XSSFCell) row4.createCell((short) 31);
		cell4AF.setCellStyle(stylefont4Row);
		cell4AF.setCellValue(newHiresPHReportDTO.gettINNumber());

		XSSFCell cell4AG = (XSSFCell) row4.createCell((short) 32);
		cell4AG.setCellStyle(stylefont4Row);
		cell4AG.setCellValue(newHiresPHReportDTO.getpHICNumber());

		XSSFCell cell4AH = (XSSFCell) row4.createCell((short) 33);
		cell4AH.setCellStyle(stylefont4Row);
		cell4AH.setCellValue(newHiresPHReportDTO.gethDMFNumber());

		XSSFCell cell4AI = (XSSFCell) row4.createCell((short) 34);
		cell4AI.setCellStyle(stylefont4Row);
		cell4AI.setCellValue(newHiresPHReportDTO.getsSSNumber());

		XSSFCell cell4AJ = (XSSFCell) row4.createCell((short) 35);
		cell4AJ.setCellStyle(stylefont4Row);
		cell4AJ.setCellValue(newHiresPHReportDTO.getMonthlyBasicWage());

		XSSFCell cell4AK = (XSSFCell) row4.createCell((short) 36);
		cell4AK.setCellStyle(stylefont4Row);
		cell4AK.setCellValue(newHiresPHReportDTO.getBank());

		XSSFCell cell4AL = (XSSFCell) row4.createCell((short) 37);
		cell4AL.setCellStyle(stylefont4Row);
		cell4AL.setCellValue(newHiresPHReportDTO.getBankBranch());

		XSSFCell cell4AM = (XSSFCell) row4.createCell((short) 38);
		cell4AM.setCellStyle(stylefont4Row);
		cell4AM.setCellValue(newHiresPHReportDTO.getAccountNumber());

		XSSFCell cell4AN = (XSSFCell) row4.createCell((short) 39);
		cell4AN.setCellStyle(stylefont4Row);
		cell4AN.setCellValue(newHiresPHReportDTO.getAccountName());

	}

	private void createSheetForEmeeDataChanges(XSSFWorkbook workbook, XSSFSheet sheet) {

		XSSFFont fontHeader = workbook.createFont();
		fontHeader.setFontName("Calibri");
		fontHeader.setItalic(true);
		fontHeader.setColor(HSSFColor.WHITE.index);
		fontHeader.setBold(true);
		fontHeader.setFontHeightInPoints((short) 15);

		XSSFCellStyle style = workbook.createCellStyle();
		style.setFont(fontHeader);
		style.setFillForegroundColor(new XSSFColor(new java.awt.Color(255, 0, 0)));
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);

		XSSFRow row0 = sheet.createRow((short) 0);
		row0.setHeightInPoints((short) 15);

		XSSFRow row1 = sheet.createRow((short) 1);
		row1.setHeightInPoints((short) 28);

		XSSFCell cell1A = (XSSFCell) row1.createCell((short) 0);
		cell1A.setCellStyle(style);
		cell1A.setCellValue("              EMPLOYEE DATA CHANGES");
		sheet.setColumnWidth(0, 7000);

		XSSFCell cell1B = (XSSFCell) row1.createCell((short) 1);
		cell1B.setCellStyle(style);
		sheet.setColumnWidth(1, 7000);

		sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 1));
		sheet.setColumnWidth(2, 11000);

		/*
		 * sheet.createFreezePane(0, 1, 0, 1); // sheet.createFreezePane(0, 0);
		 * sheet.setFitToPage(true);
		 */

		XSSFFont font = workbook.createFont();
		font.setFontName("Calibri");
		font.setItalic(false);
		font.setColor(HSSFColor.BLACK.index);
		font.setBold(false);
		font.setFontHeightInPoints((short) 10);

		XSSFCellStyle stylefont = workbook.createCellStyle();
		stylefont.setFont(font);
		stylefont.setWrapText(true);
		fontHeader.setColor(HSSFColor.WHITE.index);

		XSSFCell cell1C = (XSSFCell) row1.createCell((short) 2);
		cell1C.setCellValue("List of all employee data changes(Personal) after the last payroll cut off date");
		cell1C.setCellStyle(stylefont);

		XSSFRow row2 = sheet.createRow((short) 2);
		row2.setHeightInPoints((short) 12);

		XSSFFont font2Row = workbook.createFont();
		font2Row.setFontName("Calibri");
		font2Row.setColor(HSSFColor.RED.index);
		font2Row.setItalic(true);
		font2Row.setBold(true);
		font2Row.setFontHeightInPoints((short) 10);

		XSSFCellStyle stylefont2Row = workbook.createCellStyle();
		stylefont2Row.setFont(font2Row);

		XSSFCell cell2A = (XSSFCell) row2.createCell((short) 0);
		cell2A.setCellStyle(stylefont2Row);
		cell2A.setCellValue("* Mandatory Fields");

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

		XSSFRow row3 = sheet.createRow((short) 3);
		row3.setHeightInPoints((short) 14);

		XSSFCell cell3A = (XSSFCell) row3.createCell((short) 0);
		cell3A.setCellStyle(stylefont3Row);
		cell3A.setCellValue(" Employee ID *");

		XSSFCell cell3B = (XSSFCell) row3.createCell((short) 1);
		cell3B.setCellStyle(stylefont3Row);
		cell3B.setCellValue("Employee Name *");

		XSSFCell cell3C = (XSSFCell) row3.createCell((short) 2);
		cell3C.setCellStyle(stylefont3Row);
		cell3C.setCellValue("Marital Status");

		XSSFCell cell3D = (XSSFCell) row3.createCell((short) 3);
		cell3D.setCellStyle(stylefont3Row);
		cell3D.setCellValue("Bank Code *");

		sheet.setColumnWidth(3, 4200);

		XSSFCell cell3E = (XSSFCell) row3.createCell((short) 4);
		cell3E.setCellStyle(stylefont3Row);
		cell3E.setCellValue("Bank Branch Code");

		sheet.setColumnWidth(4, 4200);

		XSSFCell cell3F = (XSSFCell) row3.createCell((short) 5);
		cell3F.setCellStyle(stylefont3Row);
		cell3F.setCellValue("Bank Account Number *");

		sheet.setColumnWidth(5, 4200);

		XSSFCell cell3G = (XSSFCell) row3.createCell((short) 6);
		cell3G.setCellStyle(stylefont3Row);
		cell3G.setCellValue("Account Name*");

		sheet.setColumnWidth(6, 4200);

		XSSFCell cell3H = (XSSFCell) row3.createCell((short) 7);
		cell3H.setCellStyle(stylefont3Row);
		cell3H.setCellValue("Address 1 *");

		sheet.setColumnWidth(7, 4200);

		XSSFCell cell3I = (XSSFCell) row3.createCell((short) 8);
		cell3I.setCellStyle(stylefont3Row);
		cell3I.setCellValue("Address 2 *");

		sheet.setColumnWidth(8, 4200);

		XSSFCell cell3J = (XSSFCell) row3.createCell((short) 9);
		cell3J.setCellStyle(stylefont3Row);
		cell3J.setCellValue("Address 3");

		sheet.setColumnWidth(9, 13000);

		XSSFCell cell3K = (XSSFCell) row3.createCell((short) 10);
		cell3K.setCellStyle(stylefont3Row);
		cell3K.setCellValue("Country *");

		sheet.setColumnWidth(10, 8000);

		XSSFCell cell3L = (XSSFCell) row3.createCell((short) 11);
		cell3L.setCellStyle(stylefont3Row);
		cell3L.setCellValue("Postal Code *");

		sheet.setColumnWidth(11, 4200);

		XSSFCell cell3M = (XSSFCell) row3.createCell((short) 12);
		cell3M.setCellStyle(stylefont3Row);
		cell3M.setCellValue("Phone");

		sheet.setColumnWidth(12, 4200);

		XSSFCell cell3N = (XSSFCell) row3.createCell((short) 13);
		cell3N.setCellStyle(stylefont3Row);
		cell3N.setCellValue("Mobile Phone");

		sheet.setColumnWidth(13, 4200);

		XSSFCell cell3O = (XSSFCell) row3.createCell((short) 14);
		cell3O.setCellStyle(stylefont3Row);
		cell3O.setCellValue("Email Address");

		sheet.setColumnWidth(14, 4200);

		XSSFCell cell3P = (XSSFCell) row3.createCell((short) 15);
		cell3P.setCellStyle(stylefont3Row);
		cell3P.setCellValue("Remarks");

		sheet.setColumnWidth(15, 18000);

		XSSFFont font4Row = workbook.createFont();
		font4Row.setFontName("Calibri");
		font4Row.setColor(HSSFColor.BLACK.index);
		font4Row.setItalic(true);
		font4Row.setBold(false);
		font4Row.setFontHeightInPoints((short) 10);

		XSSFCellStyle stylefont4Row = workbook.createCellStyle();
		stylefont4Row.setFont(font4Row);
		stylefont4Row.setFillForegroundColor(new XSSFColor(new java.awt.Color(178, 223, 249)));
		stylefont4Row.setFillPattern(CellStyle.SOLID_FOREGROUND);
		stylefont4Row.setAlignment(HorizontalAlignment.CENTER);
		stylefont4Row.setVerticalAlignment(VerticalAlignment.CENTER);
		stylefont4Row.setWrapText(true);

		XSSFRow row4 = sheet.createRow((short) 4);
		row4.setHeightInPoints((short) 38);

		XSSFCell cell4A = (XSSFCell) row4.createCell((short) 0);
		cell4A.setCellStyle(stylefont4Row);
		cell4A.setCellValue("(Select Existing Employee)");

		XSSFCell cell4B = (XSSFCell) row4.createCell((short) 1);
		cell4B.setCellStyle(stylefont4Row);
		cell4B.setCellValue("");

		XSSFCell cell4C = (XSSFCell) row4.createCell((short) 2);
		cell4C.setCellStyle(stylefont4Row);
		cell4C.setCellValue("(Select Value)");

		XSSFCell cell4D = (XSSFCell) row4.createCell((short) 3);
		cell4D.setCellStyle(stylefont4Row);
		cell4D.setCellValue("(Select Value)");

		XSSFCell cell4E = (XSSFCell) row4.createCell((short) 4);
		cell4E.setCellStyle(stylefont4Row);
		cell4E.setCellValue("(3 Digit)");

		XSSFCell cell4F = (XSSFCell) row4.createCell((short) 5);
		cell4F.setCellStyle(stylefont4Row);
		cell4F.setCellValue("(Integer)");

		XSSFCell cell4G = (XSSFCell) row4.createCell((short) 6);
		cell4G.setCellStyle(stylefont4Row);
		cell4G.setCellValue("");

		XSSFCell cell4H = (XSSFCell) row4.createCell((short) 7);
		cell4H.setCellStyle(stylefont4Row);
		cell4H.setCellValue("(Alphanumeric)");

		XSSFCell cell4I = (XSSFCell) row4.createCell((short) 8);
		cell4I.setCellStyle(stylefont4Row);
		cell4I.setCellValue("(Alphanumeric)");

		XSSFCell cell4J = (XSSFCell) row4.createCell((short) 9);
		cell4J.setCellStyle(stylefont4Row);
		cell4J.setCellValue("(Alphanumeric)");

		XSSFCell cell4K = (XSSFCell) row4.createCell((short) 10);
		cell4K.setCellStyle(stylefont4Row);
		cell4K.setCellValue("(Select Value)");

		XSSFCell cell4L = (XSSFCell) row4.createCell((short) 11);
		cell4L.setCellStyle(stylefont4Row);
		cell4L.setCellValue("(6 digit)");

		XSSFCell cell4M = (XSSFCell) row4.createCell((short) 12);
		cell4M.setCellStyle(stylefont4Row);
		cell4M.setCellValue("(Integer)");

		XSSFCell cell4N = (XSSFCell) row4.createCell((short) 13);
		cell4N.setCellStyle(stylefont4Row);
		cell4N.setCellValue("(Integer)");

		XSSFCell cell4O = (XSSFCell) row4.createCell((short) 14);
		cell4O.setCellStyle(stylefont4Row);
		cell4O.setCellValue("(email)");

		XSSFCell cell4P = (XSSFCell) row4.createCell((short) 15);
		cell4P.setCellStyle(stylefont4Row);
		cell4P.setCellValue("(Optional) \n"
				+ "For general remarks only.  All movements and payment instructions are to be keyed into the appropriate worksheets accordingly.");

	}

	private void setEmeeDataChanges(XSSFWorkbook workbook, XSSFRow row4, WorkDayPHReportDTO workDayPHReportDTO) {

		EmeeDataChangesPHReportDTO emeeDataChangesPHReport = workDayPHReportDTO.getEmeeDataChangesPHReport();

		XSSFFont font4Row = workbook.createFont();
		font4Row.setFontName("Calibri");
		font4Row.setColor(HSSFColor.BLACK.index);
		font4Row.setBold(false);
		font4Row.setFontHeightInPoints((short) 10);

		XSSFCellStyle stylefont4Row = workbook.createCellStyle();
		stylefont4Row.setFont(font4Row);
		stylefont4Row.setAlignment(HorizontalAlignment.CENTER);
		stylefont4Row.setVerticalAlignment(VerticalAlignment.CENTER);
		stylefont4Row.setWrapText(true);

		XSSFCell cell4A = (XSSFCell) row4.createCell((short) 0);
		cell4A.setCellStyle(stylefont4Row);
		cell4A.setCellValue(emeeDataChangesPHReport.getEmployeeID());

		XSSFCell cell4B = (XSSFCell) row4.createCell((short) 1);
		cell4B.setCellStyle(stylefont4Row);
		cell4B.setCellValue(emeeDataChangesPHReport.getEmployeeName());

		XSSFCell cell4C = (XSSFCell) row4.createCell((short) 2);
		cell4C.setCellStyle(stylefont4Row);
		cell4C.setCellValue(emeeDataChangesPHReport.getMaritalStatus());

		XSSFCell cell4D = (XSSFCell) row4.createCell((short) 3);
		cell4D.setCellStyle(stylefont4Row);
		cell4D.setCellValue(emeeDataChangesPHReport.getBankCode());

		XSSFCell cell4E = (XSSFCell) row4.createCell((short) 4);
		cell4E.setCellStyle(stylefont4Row);
		cell4E.setCellValue(emeeDataChangesPHReport.getBankBranchCode());

		XSSFCell cell4F = (XSSFCell) row4.createCell((short) 5);
		cell4F.setCellStyle(stylefont4Row);
		cell4F.setCellValue(emeeDataChangesPHReport.getBankAccountNumber());

		XSSFCell cell4G = (XSSFCell) row4.createCell((short) 6);
		cell4G.setCellStyle(stylefont4Row);
		cell4G.setCellValue(emeeDataChangesPHReport.getAccountName());

		XSSFCell cell4H = (XSSFCell) row4.createCell((short) 7);
		cell4H.setCellStyle(stylefont4Row);
		cell4H.setCellValue(emeeDataChangesPHReport.getAddress1());

		XSSFCell cell4I = (XSSFCell) row4.createCell((short) 8);
		cell4I.setCellStyle(stylefont4Row);
		cell4I.setCellValue(emeeDataChangesPHReport.getAddress2());

		XSSFCell cell4J = (XSSFCell) row4.createCell((short) 9);
		cell4J.setCellStyle(stylefont4Row);
		cell4J.setCellValue(emeeDataChangesPHReport.getAddress3());

		XSSFCell cell4K = (XSSFCell) row4.createCell((short) 10);
		cell4K.setCellStyle(stylefont4Row);
		cell4K.setCellValue(emeeDataChangesPHReport.getCountry());

		XSSFCell cell4L = (XSSFCell) row4.createCell((short) 11);
		cell4L.setCellStyle(stylefont4Row);
		cell4L.setCellValue(emeeDataChangesPHReport.getPostalCode());

		XSSFCell cell4M = (XSSFCell) row4.createCell((short) 12);
		cell4M.setCellStyle(stylefont4Row);
		cell4M.setCellValue(emeeDataChangesPHReport.getPhone());

		XSSFCell cell4N = (XSSFCell) row4.createCell((short) 13);
		cell4N.setCellStyle(stylefont4Row);
		cell4N.setCellValue(emeeDataChangesPHReport.getMobilePhone());

		XSSFCell cell4O = (XSSFCell) row4.createCell((short) 14);
		cell4O.setCellStyle(stylefont4Row);
		cell4O.setCellValue(emeeDataChangesPHReport.getEmailAddress());

		XSSFCell cell4P = (XSSFCell) row4.createCell((short) 15);
		cell4P.setCellStyle(stylefont4Row);
		cell4P.setCellValue(emeeDataChangesPHReport.getRemarks());
	}

	private void createSheetForResignations(XSSFWorkbook workbook, XSSFSheet sheet) {

		XSSFFont fontHeader = workbook.createFont();
		fontHeader.setFontName("Calibri");
		fontHeader.setItalic(true);
		fontHeader.setColor(HSSFColor.WHITE.index);
		fontHeader.setBold(true);
		fontHeader.setFontHeightInPoints((short) 15);

		XSSFCellStyle style = workbook.createCellStyle();
		style.setFont(fontHeader);
		style.setFillForegroundColor(new XSSFColor(new java.awt.Color(255, 0, 0)));
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);

		XSSFRow row0 = sheet.createRow((short) 0);
		row0.setHeightInPoints((short) 15);

		XSSFRow row1 = sheet.createRow((short) 1);
		row1.setHeightInPoints((short) 28);

		XSSFCell cell1A = (XSSFCell) row1.createCell((short) 0);
		cell1A.setCellStyle(style);
		cell1A.setCellValue("              RESIGNATIONS");
		sheet.setColumnWidth(0, 7000);

		XSSFCell cell1B = (XSSFCell) row1.createCell((short) 1);
		cell1B.setCellStyle(style);
		sheet.setColumnWidth(1, 7000);

		sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 1));
		sheet.setColumnWidth(2, 11000);

		/*
		 * sheet.createFreezePane(0, 1, 0, 1); // sheet.createFreezePane(0, 0);
		 * sheet.setFitToPage(true);
		 */

		XSSFFont font = workbook.createFont();
		font.setFontName("Calibri");
		font.setItalic(false);
		font.setColor(HSSFColor.BLACK.index);
		font.setBold(false);
		font.setFontHeightInPoints((short) 10);

		XSSFCellStyle stylefont = workbook.createCellStyle();
		stylefont.setFont(font);
		stylefont.setWrapText(true);
		fontHeader.setColor(HSSFColor.WHITE.index);

		XSSFCell cell1C = (XSSFCell) row1.createCell((short) 2);
		cell1C.setCellValue("List of all Resigned employees after the last payroll cut off date");
		cell1C.setCellStyle(stylefont);

		XSSFRow row2 = sheet.createRow((short) 2);
		row2.setHeightInPoints((short) 12);

		XSSFFont font2Row = workbook.createFont();
		font2Row.setFontName("Calibri");
		font2Row.setColor(HSSFColor.RED.index);
		font2Row.setItalic(true);
		font2Row.setBold(true);
		font2Row.setFontHeightInPoints((short) 10);

		XSSFCellStyle stylefont2Row = workbook.createCellStyle();
		stylefont2Row.setFont(font2Row);

		XSSFCell cell2A = (XSSFCell) row2.createCell((short) 0);
		cell2A.setCellStyle(stylefont2Row);
		cell2A.setCellValue("* Mandatory Fields");

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

		XSSFRow row3 = sheet.createRow((short) 3);
		row3.setHeightInPoints((short) 14);

		XSSFCell cell3A = (XSSFCell) row3.createCell((short) 0);
		cell3A.setCellStyle(stylefont3Row);
		cell3A.setCellValue(" Employee ID *");

		XSSFCell cell3B = (XSSFCell) row3.createCell((short) 1);
		cell3B.setCellStyle(stylefont3Row);
		cell3B.setCellValue("Employee Name *");

		XSSFCell cell3C = (XSSFCell) row3.createCell((short) 2);
		cell3C.setCellStyle(stylefont3Row);
		cell3C.setCellValue("Cessation Reason");

		XSSFCell cell3D = (XSSFCell) row3.createCell((short) 3);
		cell3D.setCellStyle(stylefont3Row);
		cell3D.setCellValue("Cessation Date *");

		sheet.setColumnWidth(3, 4200);

		XSSFCell cell3E = (XSSFCell) row3.createCell((short) 4);
		cell3E.setCellStyle(stylefont3Row);
		cell3E.setCellValue("Remarks");

		sheet.setColumnWidth(4, 18000);

		XSSFFont font4Row = workbook.createFont();
		font4Row.setFontName("Calibri");
		font4Row.setColor(HSSFColor.BLACK.index);
		font4Row.setItalic(true);
		font4Row.setBold(false);
		font4Row.setFontHeightInPoints((short) 10);

		XSSFCellStyle stylefont4Row = workbook.createCellStyle();
		stylefont4Row.setFont(font4Row);
		stylefont4Row.setFillForegroundColor(new XSSFColor(new java.awt.Color(178, 223, 249)));
		stylefont4Row.setFillPattern(CellStyle.SOLID_FOREGROUND);
		stylefont4Row.setAlignment(HorizontalAlignment.CENTER);
		stylefont4Row.setVerticalAlignment(VerticalAlignment.CENTER);
		stylefont4Row.setWrapText(true);

		XSSFRow row4 = sheet.createRow((short) 4);
		row4.setHeightInPoints((short) 38);

		XSSFCell cell4A = (XSSFCell) row4.createCell((short) 0);
		cell4A.setCellStyle(stylefont4Row);
		cell4A.setCellValue("(Select Existing Employee)");

		XSSFCell cell4B = (XSSFCell) row4.createCell((short) 1);
		cell4B.setCellStyle(stylefont4Row);
		cell4B.setCellValue("");

		XSSFCell cell4C = (XSSFCell) row4.createCell((short) 2);
		cell4C.setCellStyle(stylefont4Row);
		cell4C.setCellValue("(Select Value)");

		XSSFCell cell4D = (XSSFCell) row4.createCell((short) 3);
		cell4D.setCellStyle(stylefont4Row);
		cell4D.setCellValue("(mm/dd/yyyy)");

		XSSFCell cell4P = (XSSFCell) row4.createCell((short) 4);
		cell4P.setCellStyle(stylefont4Row);
		cell4P.setCellValue("(Optional) \n"
				+ "For general remarks only.  All movements and payment instructions are to be keyed into the appropriate worksheets accordingly.");

	}

	private void setResignations(XSSFWorkbook workbook, XSSFRow row4, WorkDayPHReportDTO workDayPHReportDTO) {

		ResignationsPHReportDTO resignationsPHReportDTO = workDayPHReportDTO.getResignationsPHReport();

		XSSFFont font4Row = workbook.createFont();
		font4Row.setFontName("Calibri");
		font4Row.setColor(HSSFColor.BLACK.index);
		font4Row.setBold(false);
		font4Row.setFontHeightInPoints((short) 10);

		XSSFCellStyle stylefont4Row = workbook.createCellStyle();
		stylefont4Row.setFont(font4Row);
		stylefont4Row.setAlignment(HorizontalAlignment.CENTER);
		stylefont4Row.setVerticalAlignment(VerticalAlignment.CENTER);
		stylefont4Row.setWrapText(true);

		XSSFCell cell4A = (XSSFCell) row4.createCell((short) 0);
		cell4A.setCellStyle(stylefont4Row);
		cell4A.setCellValue(resignationsPHReportDTO.getEmployeeID());

		XSSFCell cell4B = (XSSFCell) row4.createCell((short) 1);
		cell4B.setCellStyle(stylefont4Row);
		cell4B.setCellValue(resignationsPHReportDTO.getEmployeeName());

		XSSFCell cell4C = (XSSFCell) row4.createCell((short) 2);
		cell4C.setCellStyle(stylefont4Row);
		cell4C.setCellValue(resignationsPHReportDTO.getCessationReason());

		XSSFCell cell4D = (XSSFCell) row4.createCell((short) 3);
		cell4D.setCellStyle(stylefont4Row);
		cell4D.setCellValue(resignationsPHReportDTO.getCessationDate());

		XSSFCell cell4P = (XSSFCell) row4.createCell((short) 4);
		cell4P.setCellStyle(stylefont4Row);
		cell4P.setCellValue(resignationsPHReportDTO.getRemarks());

	}

	private void createSheetForCareerProgression(XSSFWorkbook workbook, XSSFSheet sheet) {

		XSSFFont fontHeader = workbook.createFont();
		fontHeader.setFontName("Calibri");
		fontHeader.setItalic(true);
		fontHeader.setColor(HSSFColor.WHITE.index);
		fontHeader.setBold(true);
		fontHeader.setFontHeightInPoints((short) 15);

		XSSFCellStyle style = workbook.createCellStyle();
		style.setFont(fontHeader);
		style.setFillForegroundColor(new XSSFColor(new java.awt.Color(255, 0, 0)));
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);

		XSSFRow row0 = sheet.createRow((short) 0);
		row0.setHeightInPoints((short) 15);

		XSSFRow row1 = sheet.createRow((short) 1);
		row1.setHeightInPoints((short) 28);

		XSSFCell cell1A = (XSSFCell) row1.createCell((short) 0);
		cell1A.setCellStyle(style);
		cell1A.setCellValue("              CAREER PROGRESSION");
		sheet.setColumnWidth(0, 7000);

		XSSFCell cell1B = (XSSFCell) row1.createCell((short) 1);
		cell1B.setCellStyle(style);
		sheet.setColumnWidth(1, 7000);

		sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 1));
		sheet.setColumnWidth(2, 11000);

		/*
		 * sheet.createFreezePane(0, 1, 0, 1); // sheet.createFreezePane(0, 0);
		 * sheet.setFitToPage(true);
		 */

		XSSFFont font = workbook.createFont();
		font.setFontName("Calibri");
		font.setItalic(false);
		font.setColor(HSSFColor.BLACK.index);
		font.setBold(false);
		font.setFontHeightInPoints((short) 10);

		XSSFCellStyle stylefont = workbook.createCellStyle();
		stylefont.setFont(font);
		stylefont.setWrapText(true);
		fontHeader.setColor(HSSFColor.WHITE.index);

		XSSFCell cell1C = (XSSFCell) row1.createCell((short) 2);
		cell1C.setCellValue(
				"List of all employee data changes(Employment related) after the last payroll cut off date");
		cell1C.setCellStyle(stylefont);

		XSSFRow row2 = sheet.createRow((short) 2);
		row2.setHeightInPoints((short) 12);

		XSSFFont font2Row = workbook.createFont();
		font2Row.setFontName("Calibri");
		font2Row.setColor(HSSFColor.RED.index);
		font2Row.setItalic(true);
		font2Row.setBold(true);
		font2Row.setFontHeightInPoints((short) 10);

		XSSFCellStyle stylefont2Row = workbook.createCellStyle();
		stylefont2Row.setFont(font2Row);

		XSSFCell cell2A = (XSSFCell) row2.createCell((short) 0);
		cell2A.setCellStyle(stylefont2Row);
		cell2A.setCellValue("* Mandatory Fields");

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

		XSSFRow row3 = sheet.createRow((short) 3);
		row3.setHeightInPoints((short) 14);

		XSSFCell cell3A = (XSSFCell) row3.createCell((short) 0);
		cell3A.setCellStyle(stylefont3Row);
		cell3A.setCellValue(" Employee ID *");

		XSSFCell cell3B = (XSSFCell) row3.createCell((short) 1);
		cell3B.setCellStyle(stylefont3Row);
		cell3B.setCellValue("Employee Name *");

		XSSFCell cell3C = (XSSFCell) row3.createCell((short) 2);
		cell3C.setCellStyle(stylefont3Row);
		cell3C.setCellValue("Career Progression Reason *");

		XSSFCell cell3D = (XSSFCell) row3.createCell((short) 3);
		cell3D.setCellStyle(stylefont3Row);
		cell3D.setCellValue("Career Progression Date *");

		sheet.setColumnWidth(3, 4200);

		XSSFCell cell3E = (XSSFCell) row3.createCell((short) 4);
		cell3E.setCellStyle(stylefont3Row);
		cell3E.setCellValue("Department ");

		sheet.setColumnWidth(4, 4200);

		XSSFCell cell3F = (XSSFCell) row3.createCell((short) 5);
		cell3F.setCellStyle(stylefont3Row);
		cell3F.setCellValue("Job Title");

		sheet.setColumnWidth(5, 4200);

		XSSFCell cell3G = (XSSFCell) row3.createCell((short) 6);
		cell3G.setCellStyle(stylefont3Row);
		cell3G.setCellValue("Cost Center");

		sheet.setColumnWidth(6, 4200);

		XSSFCell cell3H = (XSSFCell) row3.createCell((short) 7);
		cell3H.setCellStyle(stylefont3Row);
		cell3H.setCellValue("Remarks");

		sheet.setColumnWidth(7, 18000);

		XSSFFont font4Row = workbook.createFont();
		font4Row.setFontName("Calibri");
		font4Row.setColor(HSSFColor.BLACK.index);
		font4Row.setItalic(true);
		font4Row.setBold(false);
		font4Row.setFontHeightInPoints((short) 10);

		XSSFCellStyle stylefont4Row = workbook.createCellStyle();
		stylefont4Row.setFont(font4Row);
		stylefont4Row.setFillForegroundColor(new XSSFColor(new java.awt.Color(178, 223, 249)));
		stylefont4Row.setFillPattern(CellStyle.SOLID_FOREGROUND);
		stylefont4Row.setAlignment(HorizontalAlignment.CENTER);
		stylefont4Row.setVerticalAlignment(VerticalAlignment.CENTER);
		stylefont4Row.setWrapText(true);

		XSSFRow row4 = sheet.createRow((short) 4);
		row4.setHeightInPoints((short) 38);

		XSSFCell cell4A = (XSSFCell) row4.createCell((short) 0);
		cell4A.setCellStyle(stylefont4Row);
		cell4A.setCellValue("(Select Existing Employee)");

		XSSFCell cell4B = (XSSFCell) row4.createCell((short) 1);
		cell4B.setCellStyle(stylefont4Row);
		cell4B.setCellValue("");

		XSSFCell cell4C = (XSSFCell) row4.createCell((short) 2);
		cell4C.setCellStyle(stylefont4Row);
		cell4C.setCellValue("(Select Value)");

		XSSFCell cell4D = (XSSFCell) row4.createCell((short) 3);
		cell4D.setCellStyle(stylefont4Row);
		cell4D.setCellValue("(mm/dd/yyyy)");

		XSSFCell cell4E = (XSSFCell) row4.createCell((short) 4);
		cell4E.setCellStyle(stylefont4Row);
		cell4E.setCellValue("(Select New Changed Value) \n (Leave Blank If No Changes)");

		XSSFCell cell4F = (XSSFCell) row4.createCell((short) 5);
		cell4F.setCellStyle(stylefont4Row);
		cell4F.setCellValue("(Select New Changed Value) \n (Leave Blank If No Changes)");

		XSSFCell cell4G = (XSSFCell) row4.createCell((short) 6);
		cell4G.setCellStyle(stylefont4Row);
		cell4G.setCellValue("(Select New Changed Value) \n (Leave Blank If No Changes)");

		XSSFCell cell4P = (XSSFCell) row4.createCell((short) 7);
		cell4P.setCellStyle(stylefont4Row);
		cell4P.setCellValue("(Optional) \n"
				+ "For general remarks only.  All movements and payment instructions are to be keyed into the appropriate worksheets accordingly.");
	}

	private void setCareerProgression(XSSFWorkbook workbook, XSSFRow row4, WorkDayPHReportDTO workDayPHReportDTO) {

		CareerProgressionPHReportDTO careerProgressionPHReport = workDayPHReportDTO.getCareerProgressionPHReport();

		XSSFFont font4Row = workbook.createFont();
		font4Row.setFontName("Calibri");
		font4Row.setColor(HSSFColor.BLACK.index);
		font4Row.setBold(false);
		font4Row.setFontHeightInPoints((short) 10);

		XSSFCellStyle stylefont4Row = workbook.createCellStyle();
		stylefont4Row.setFont(font4Row);
		stylefont4Row.setAlignment(HorizontalAlignment.CENTER);
		stylefont4Row.setVerticalAlignment(VerticalAlignment.CENTER);
		stylefont4Row.setWrapText(true);

		XSSFCell cell4A = (XSSFCell) row4.createCell((short) 0);
		cell4A.setCellStyle(stylefont4Row);
		cell4A.setCellValue(careerProgressionPHReport.getEmployeeID());

		XSSFCell cell4B = (XSSFCell) row4.createCell((short) 1);
		cell4B.setCellStyle(stylefont4Row);
		cell4B.setCellValue(careerProgressionPHReport.getEmployeeName());

		XSSFCell cell4C = (XSSFCell) row4.createCell((short) 2);
		cell4C.setCellStyle(stylefont4Row);
		cell4C.setCellValue(careerProgressionPHReport.getCareerProgressionReason());

		XSSFCell cell4D = (XSSFCell) row4.createCell((short) 3);
		cell4D.setCellStyle(stylefont4Row);
		cell4D.setCellValue(careerProgressionPHReport.getCareerProgressionDate());

		XSSFCell cell4E = (XSSFCell) row4.createCell((short) 4);
		cell4E.setCellStyle(stylefont4Row);
		cell4E.setCellValue(careerProgressionPHReport.getDepartment());

		XSSFCell cell4F = (XSSFCell) row4.createCell((short) 5);
		cell4F.setCellStyle(stylefont4Row);
		cell4F.setCellValue(careerProgressionPHReport.getJobTitle());

		XSSFCell cell4G = (XSSFCell) row4.createCell((short) 6);
		cell4G.setCellStyle(stylefont4Row);
		cell4G.setCellValue(careerProgressionPHReport.getCostCenter());

		XSSFCell cell4P = (XSSFCell) row4.createCell((short) 7);
		cell4P.setCellStyle(stylefont4Row);
		cell4P.setCellValue(careerProgressionPHReport.getRemarks());

	}

	private void createSheetForBasicRateProgression(XSSFWorkbook workbook, XSSFSheet sheet) {

		XSSFFont fontHeader = workbook.createFont();
		fontHeader.setFontName("Calibri");
		fontHeader.setItalic(true);
		fontHeader.setColor(HSSFColor.WHITE.index);
		fontHeader.setBold(true);
		fontHeader.setFontHeightInPoints((short) 15);

		XSSFCellStyle style = workbook.createCellStyle();
		style.setFont(fontHeader);
		style.setFillForegroundColor(new XSSFColor(new java.awt.Color(255, 0, 0)));
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);

		XSSFRow row0 = sheet.createRow((short) 0);
		row0.setHeightInPoints((short) 15);

		XSSFRow row1 = sheet.createRow((short) 1);
		row1.setHeightInPoints((short) 28);

		XSSFCell cell1A = (XSSFCell) row1.createCell((short) 0);
		cell1A.setCellStyle(style);
		cell1A.setCellValue("          BASIC RATE PROGRESSION");
		sheet.setColumnWidth(0, 7000);

		XSSFCell cell1B = (XSSFCell) row1.createCell((short) 1);
		cell1B.setCellStyle(style);
		sheet.setColumnWidth(1, 7000);

		sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 1));
		sheet.setColumnWidth(2, 11000);

		/*
		 * sheet.createFreezePane(0, 1, 0, 1); // sheet.createFreezePane(0, 0);
		 * sheet.setFitToPage(true);
		 */

		XSSFFont font = workbook.createFont();
		font.setFontName("Calibri");
		font.setItalic(false);
		font.setColor(HSSFColor.BLACK.index);
		font.setBold(false);
		font.setFontHeightInPoints((short) 10);

		XSSFCellStyle stylefont = workbook.createCellStyle();
		stylefont.setFont(font);
		stylefont.setWrapText(true);
		fontHeader.setColor(HSSFColor.WHITE.index);

		XSSFCell cell1C = (XSSFCell) row1.createCell((short) 2);
		cell1C.setCellValue("List of all basic rate changes after the last payroll cut off date");
		cell1C.setCellStyle(stylefont);

		XSSFRow row2 = sheet.createRow((short) 2);
		row2.setHeightInPoints((short) 12);

		XSSFFont font2Row = workbook.createFont();
		font2Row.setFontName("Calibri");
		font2Row.setColor(HSSFColor.RED.index);
		font2Row.setItalic(true);
		font2Row.setBold(true);
		font2Row.setFontHeightInPoints((short) 10);

		XSSFCellStyle stylefont2Row = workbook.createCellStyle();
		stylefont2Row.setFont(font2Row);

		XSSFCell cell2A = (XSSFCell) row2.createCell((short) 0);
		cell2A.setCellStyle(stylefont2Row);
		cell2A.setCellValue("* Mandatory Fields");

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

		XSSFRow row3 = sheet.createRow((short) 3);
		row3.setHeightInPoints((short) 14);

		XSSFCell cell3A = (XSSFCell) row3.createCell((short) 0);
		cell3A.setCellStyle(stylefont3Row);
		cell3A.setCellValue(" Employee ID *");

		XSSFCell cell3B = (XSSFCell) row3.createCell((short) 1);
		cell3B.setCellStyle(stylefont3Row);
		cell3B.setCellValue("Employee Name *");

		XSSFCell cell3C = (XSSFCell) row3.createCell((short) 2);
		cell3C.setCellStyle(stylefont3Row);
		cell3C.setCellValue("Basic Rate Reason *");

		XSSFCell cell3D = (XSSFCell) row3.createCell((short) 3);
		cell3D.setCellStyle(stylefont3Row);
		cell3D.setCellValue("Progression Date *");

		sheet.setColumnWidth(3, 4200);

		XSSFCell cell3E = (XSSFCell) row3.createCell((short) 4);
		cell3E.setCellStyle(stylefont3Row);
		cell3E.setCellValue("Effective Date *");

		sheet.setColumnWidth(4, 4200);

		XSSFCell cell3F = (XSSFCell) row3.createCell((short) 5);
		cell3F.setCellStyle(stylefont3Row);
		cell3F.setCellValue("New Salary * ");

		sheet.setColumnWidth(5, 4200);

		XSSFCell cell3G = (XSSFCell) row3.createCell((short) 6);
		cell3G.setCellStyle(stylefont3Row);
		cell3G.setCellValue("Currency");

		sheet.setColumnWidth(6, 4200);

		XSSFCell cell3H = (XSSFCell) row3.createCell((short) 7);
		cell3H.setCellStyle(stylefont3Row);
		cell3H.setCellValue("Frequency");

		sheet.setColumnWidth(7, 4200);

		XSSFCell cell3I = (XSSFCell) row3.createCell((short) 8);
		cell3I.setCellStyle(stylefont3Row);
		cell3I.setCellValue("Remarks");

		sheet.setColumnWidth(8, 18000);

		XSSFFont font4Row = workbook.createFont();
		font4Row.setFontName("Calibri");
		font4Row.setColor(HSSFColor.BLACK.index);
		font4Row.setItalic(true);
		font4Row.setBold(false);
		font4Row.setFontHeightInPoints((short) 10);

		XSSFCellStyle stylefont4Row = workbook.createCellStyle();
		stylefont4Row.setFont(font4Row);
		stylefont4Row.setFillForegroundColor(new XSSFColor(new java.awt.Color(178, 223, 249)));
		stylefont4Row.setFillPattern(CellStyle.SOLID_FOREGROUND);
		stylefont4Row.setAlignment(HorizontalAlignment.CENTER);
		stylefont4Row.setVerticalAlignment(VerticalAlignment.CENTER);
		stylefont4Row.setWrapText(true);

		XSSFRow row4 = sheet.createRow((short) 4);
		row4.setHeightInPoints((short) 38);

		XSSFCell cell4A = (XSSFCell) row4.createCell((short) 0);
		cell4A.setCellStyle(stylefont4Row);
		cell4A.setCellValue("(Select Existing Employee)");

		XSSFCell cell4B = (XSSFCell) row4.createCell((short) 1);
		cell4B.setCellStyle(stylefont4Row);
		cell4B.setCellValue("");

		XSSFCell cell4C = (XSSFCell) row4.createCell((short) 2);
		cell4C.setCellStyle(stylefont4Row);
		cell4C.setCellValue("(Select Value) \n (Salary Change Reason)");

		XSSFCell cell4D = (XSSFCell) row4.createCell((short) 3);
		cell4D.setCellStyle(stylefont4Row);
		cell4D.setCellValue("(mm/dd/yyyy)");

		XSSFCell cell4E = (XSSFCell) row4.createCell((short) 4);
		cell4E.setCellStyle(stylefont4Row);
		cell4E.setCellValue("(mm/dd/yyyy)");

		XSSFCell cell4F = (XSSFCell) row4.createCell((short) 5);
		cell4F.setCellStyle(stylefont4Row);
		cell4F.setCellValue("(Key In New Salary) \n (Numeric: 2 dec. plc.)");

		XSSFCell cell4G = (XSSFCell) row4.createCell((short) 6);
		cell4G.setCellStyle(stylefont4Row);
		cell4G.setCellValue("");

		XSSFCell cell4H = (XSSFCell) row4.createCell((short) 7);
		cell4H.setCellStyle(stylefont4Row);
		cell4H.setCellValue("");

		XSSFCell cell4I = (XSSFCell) row4.createCell((short) 5);
		cell4I.setCellStyle(stylefont4Row);
		cell4I.setCellValue("(Optional) \n"
				+ "For general remarks only.  All movements and payment instructions are to be keyed into the appropriate worksheets accordingly.");
	}

	private void setBasicRateProgression(XSSFWorkbook workbook, XSSFRow row4, WorkDayPHReportDTO workDayPHReportDTO) {

		BasicRateProgressionPHReportDTO basicRateProgressionPHReport = workDayPHReportDTO
				.getBasicRateProgressionPHReport();

		XSSFFont font4Row = workbook.createFont();
		font4Row.setFontName("Calibri");
		font4Row.setColor(HSSFColor.BLACK.index);
		font4Row.setBold(false);
		font4Row.setFontHeightInPoints((short) 10);

		XSSFCellStyle stylefont4Row = workbook.createCellStyle();
		stylefont4Row.setFont(font4Row);
		stylefont4Row.setAlignment(HorizontalAlignment.CENTER);
		stylefont4Row.setVerticalAlignment(VerticalAlignment.CENTER);
		stylefont4Row.setWrapText(true);

		XSSFCell cell4A = (XSSFCell) row4.createCell((short) 0);
		cell4A.setCellStyle(stylefont4Row);
		cell4A.setCellValue(basicRateProgressionPHReport.getEmployeeID());

		XSSFCell cell4B = (XSSFCell) row4.createCell((short) 1);
		cell4B.setCellStyle(stylefont4Row);
		cell4B.setCellValue(basicRateProgressionPHReport.getEmployeeName());

		XSSFCell cell4C = (XSSFCell) row4.createCell((short) 2);
		cell4C.setCellStyle(stylefont4Row);
		cell4C.setCellValue(basicRateProgressionPHReport.getBasicRateReason());

		XSSFCell cell4D = (XSSFCell) row4.createCell((short) 3);
		cell4D.setCellStyle(stylefont4Row);
		cell4D.setCellValue(basicRateProgressionPHReport.getProgressionDate());

		XSSFCell cell4E = (XSSFCell) row4.createCell((short) 4);
		cell4E.setCellStyle(stylefont4Row);
		cell4E.setCellValue(basicRateProgressionPHReport.getEffectiveDate());

		XSSFCell cell4F = (XSSFCell) row4.createCell((short) 5);
		cell4F.setCellStyle(stylefont4Row);
		cell4F.setCellValue(basicRateProgressionPHReport.getNewSalary());

		XSSFCell cell4G = (XSSFCell) row4.createCell((short) 6);
		cell4G.setCellStyle(stylefont4Row);
		cell4G.setCellValue(basicRateProgressionPHReport.getCurrency());

		XSSFCell cell4H = (XSSFCell) row4.createCell((short) 7);
		cell4H.setCellStyle(stylefont4Row);
		cell4H.setCellValue(basicRateProgressionPHReport.getFrequency());

		XSSFCell cell4I = (XSSFCell) row4.createCell((short) 8);
		cell4I.setCellStyle(stylefont4Row);
		cell4I.setCellValue(basicRateProgressionPHReport.getRemarks());

	}

	private void createSheetForPayItemsRecurring(XSSFWorkbook workbook, XSSFSheet sheet) {

		XSSFFont fontHeader = workbook.createFont();
		fontHeader.setFontName("Calibri");
		fontHeader.setItalic(true);
		fontHeader.setColor(HSSFColor.WHITE.index);
		fontHeader.setBold(true);
		fontHeader.setFontHeightInPoints((short) 15);

		XSSFCellStyle style = workbook.createCellStyle();
		style.setFont(fontHeader);
		style.setFillForegroundColor(new XSSFColor(new java.awt.Color(255, 0, 0)));
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);

		XSSFRow row0 = sheet.createRow((short) 0);
		row0.setHeightInPoints((short) 15);

		XSSFRow row1 = sheet.createRow((short) 1);
		row1.setHeightInPoints((short) 28);

		XSSFCell cell1A = (XSSFCell) row1.createCell((short) 0);
		cell1A.setCellStyle(style);
		cell1A.setCellValue("       PAY ITEMS - Recurring (Amounts)");
		sheet.setColumnWidth(0, 7000);

		XSSFCell cell1B = (XSSFCell) row1.createCell((short) 1);
		cell1B.setCellStyle(style);
		sheet.setColumnWidth(1, 7000);

		sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 1));
		sheet.setColumnWidth(2, 11000);

		/*
		 * sheet.createFreezePane(0, 1, 0, 1); // sheet.createFreezePane(0, 0);
		 * sheet.setFitToPage(true);
		 */

		XSSFFont font = workbook.createFont();
		font.setFontName("Calibri");
		font.setItalic(false);
		font.setColor(HSSFColor.BLACK.index);
		font.setBold(false);
		font.setFontHeightInPoints((short) 10);

		XSSFCellStyle stylefont = workbook.createCellStyle();
		stylefont.setFont(font);
		stylefont.setWrapText(true);
		fontHeader.setColor(HSSFColor.WHITE.index);

		XSSFCell cell1C = (XSSFCell) row1.createCell((short) 2);
		cell1C.setCellValue(
				"List of all recurring allowances for the new hires and for changes in recurring allowances after the last payroll cut off date");
		cell1C.setCellStyle(stylefont);

		XSSFRow row2 = sheet.createRow((short) 2);
		row2.setHeightInPoints((short) 12);

		XSSFFont font2Row = workbook.createFont();
		font2Row.setFontName("Calibri");
		font2Row.setColor(HSSFColor.RED.index);
		font2Row.setItalic(true);
		font2Row.setBold(true);
		font2Row.setFontHeightInPoints((short) 10);

		XSSFCellStyle stylefont2Row = workbook.createCellStyle();
		stylefont2Row.setFont(font2Row);

		XSSFCell cell2A = (XSSFCell) row2.createCell((short) 0);
		cell2A.setCellStyle(stylefont2Row);
		cell2A.setCellValue("* Mandatory Fields");

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

		XSSFRow row3 = sheet.createRow((short) 3);
		row3.setHeightInPoints((short) 14);

		XSSFCell cell3A = (XSSFCell) row3.createCell((short) 0);
		cell3A.setCellStyle(stylefont3Row);
		cell3A.setCellValue(" Employee ID *");

		XSSFCell cell3B = (XSSFCell) row3.createCell((short) 1);
		cell3B.setCellStyle(stylefont3Row);
		cell3B.setCellValue("Employee Name *");

		XSSFCell cell3C = (XSSFCell) row3.createCell((short) 2);
		cell3C.setCellStyle(stylefont3Row);
		cell3C.setCellValue("Pay Code *");

		XSSFCell cell3D = (XSSFCell) row3.createCell((short) 3);
		cell3D.setCellStyle(stylefont3Row);
		cell3D.setCellValue("Pay Description");

		sheet.setColumnWidth(3, 4200);

		XSSFCell cell3E = (XSSFCell) row3.createCell((short) 4);
		cell3E.setCellStyle(stylefont3Row);
		cell3E.setCellValue("Recurring Amount *");

		sheet.setColumnWidth(4, 4200);

		XSSFCell cell3F = (XSSFCell) row3.createCell((short) 5);
		cell3F.setCellStyle(stylefont3Row);
		cell3F.setCellValue("Start Year *");

		sheet.setColumnWidth(5, 4200);

		XSSFCell cell3G = (XSSFCell) row3.createCell((short) 6);
		cell3G.setCellStyle(stylefont3Row);
		cell3G.setCellValue("Start Month *");

		sheet.setColumnWidth(6, 4200);

		XSSFCell cell3H = (XSSFCell) row3.createCell((short) 7);
		cell3H.setCellStyle(stylefont3Row);
		cell3H.setCellValue("End Year");

		sheet.setColumnWidth(7, 4200);

		XSSFCell cell3I = (XSSFCell) row3.createCell((short) 8);
		cell3I.setCellStyle(stylefont3Row);
		cell3I.setCellValue("End Month");

		sheet.setColumnWidth(8, 4200);

		XSSFCell cell3J = (XSSFCell) row3.createCell((short) 9);
		cell3J.setCellStyle(stylefont3Row);
		cell3J.setCellValue("End Counter");

		sheet.setColumnWidth(9, 4200);

		XSSFCell cell3K = (XSSFCell) row3.createCell((short) 10);
		cell3K.setCellStyle(stylefont3Row);
		cell3K.setCellValue("Suspend Recurring");

		sheet.setColumnWidth(10, 4200);

		XSSFCell cell3L = (XSSFCell) row3.createCell((short) 11);
		cell3L.setCellStyle(stylefont3Row);
		cell3L.setCellValue("Remarks");

		sheet.setColumnWidth(11, 18000);

		XSSFFont font4Row = workbook.createFont();
		font4Row.setFontName("Calibri");
		font4Row.setColor(HSSFColor.BLACK.index);
		font4Row.setItalic(true);
		font4Row.setBold(false);
		font4Row.setFontHeightInPoints((short) 10);

		XSSFCellStyle stylefont4Row = workbook.createCellStyle();
		stylefont4Row.setFont(font4Row);
		stylefont4Row.setFillForegroundColor(new XSSFColor(new java.awt.Color(178, 223, 249)));
		stylefont4Row.setFillPattern(CellStyle.SOLID_FOREGROUND);
		stylefont4Row.setAlignment(HorizontalAlignment.CENTER);
		stylefont4Row.setVerticalAlignment(VerticalAlignment.CENTER);
		stylefont4Row.setWrapText(true);

		XSSFRow row4 = sheet.createRow((short) 4);
		row4.setHeightInPoints((short) 38);

		XSSFCell cell4A = (XSSFCell) row4.createCell((short) 0);
		cell4A.setCellStyle(stylefont4Row);
		cell4A.setCellValue("(Select Existing Employee)");

		XSSFCell cell4B = (XSSFCell) row4.createCell((short) 1);
		cell4B.setCellStyle(stylefont4Row);
		cell4B.setCellValue("");

		XSSFCell cell4C = (XSSFCell) row4.createCell((short) 2);
		cell4C.setCellStyle(stylefont4Row);
		cell4C.setCellValue("(Select Value)");

		XSSFCell cell4D = (XSSFCell) row4.createCell((short) 3);
		cell4D.setCellStyle(stylefont2Row);
		cell4D.setCellValue("Auto-Display \n (According to Pay Code Selected)");

		XSSFCell cell4E = (XSSFCell) row4.createCell((short) 4);
		cell4E.setCellStyle(stylefont4Row);
		cell4E.setCellValue("(Key In New Salary) \n (Numeric: 2 dec. plc.)");

		XSSFCell cell4F = (XSSFCell) row4.createCell((short) 5);
		cell4F.setCellStyle(stylefont4Row);
		cell4F.setCellValue("(Select Year)");

		XSSFCell cell4G = (XSSFCell) row4.createCell((short) 6);
		cell4G.setCellStyle(stylefont4Row);
		cell4G.setCellValue("(Select Month)");

		XSSFCell cell4H = (XSSFCell) row4.createCell((short) 7);
		cell4H.setCellStyle(stylefont4Row);
		cell4H.setCellValue("(Select Year) \n (Leave Blank If No Specific End Year )");

		XSSFCell cell4I = (XSSFCell) row4.createCell((short) 8);
		cell4I.setCellStyle(stylefont4Row);
		cell4I.setCellValue("(Select Month) \n (Leave Blank If No Specific End Month)");

		XSSFCell cell4J = (XSSFCell) row4.createCell((short) 9);
		cell4J.setCellStyle(stylefont4Row);
		cell4J.setCellValue("");

		XSSFCell cell4K = (XSSFCell) row4.createCell((short) 10);
		cell4K.setCellStyle(stylefont4Row);
		cell4K.setCellValue("");

		XSSFCell cell4L = (XSSFCell) row4.createCell((short) 11);
		cell4L.setCellStyle(stylefont4Row);
		cell4L.setCellValue("(Optional) \n"
				+ "For general remarks only.  All movements and payment instructions are to be keyed into the appropriate worksheets accordingly.");

	}

	private void setPayItemsRecurring(XSSFWorkbook workbook, XSSFRow row4, WorkDayPHReportDTO workDayPHReportDTO) {

		PayItemsRecurringPHReportDTO payItemsRecurringPHReport = workDayPHReportDTO.getPayItemsRecurringPHReportDTO();

		XSSFFont font4Row = workbook.createFont();
		font4Row.setFontName("Calibri");
		font4Row.setColor(HSSFColor.BLACK.index);
		font4Row.setBold(false);
		font4Row.setFontHeightInPoints((short) 10);

		XSSFCellStyle stylefont4Row = workbook.createCellStyle();
		stylefont4Row.setFont(font4Row);
		stylefont4Row.setAlignment(HorizontalAlignment.CENTER);
		stylefont4Row.setVerticalAlignment(VerticalAlignment.CENTER);
		stylefont4Row.setWrapText(true);

		XSSFCell cell4A = (XSSFCell) row4.createCell((short) 0);
		cell4A.setCellStyle(stylefont4Row);
		cell4A.setCellValue(payItemsRecurringPHReport.getEmployeeID());

		XSSFCell cell4B = (XSSFCell) row4.createCell((short) 1);
		cell4B.setCellStyle(stylefont4Row);
		cell4B.setCellValue(payItemsRecurringPHReport.getEmployeeName());

		XSSFCell cell4C = (XSSFCell) row4.createCell((short) 2);
		cell4C.setCellStyle(stylefont4Row);
		cell4C.setCellValue(payItemsRecurringPHReport.getPayCode());

		XSSFCell cell4D = (XSSFCell) row4.createCell((short) 3);
		cell4D.setCellStyle(stylefont4Row);
		cell4D.setCellValue(payItemsRecurringPHReport.getPayDescription());

		XSSFCell cell4E = (XSSFCell) row4.createCell((short) 4);
		cell4E.setCellStyle(stylefont4Row);
		cell4E.setCellValue(payItemsRecurringPHReport.getRecurringAmount());

		XSSFCell cell4F = (XSSFCell) row4.createCell((short) 5);
		cell4F.setCellStyle(stylefont4Row);
		cell4F.setCellValue(payItemsRecurringPHReport.getStartYear());

		XSSFCell cell4G = (XSSFCell) row4.createCell((short) 6);
		cell4G.setCellStyle(stylefont4Row);
		cell4G.setCellValue(payItemsRecurringPHReport.getStartMonth());

		XSSFCell cell4H = (XSSFCell) row4.createCell((short) 7);
		cell4H.setCellStyle(stylefont4Row);
		cell4H.setCellValue(payItemsRecurringPHReport.getEndYear());

		XSSFCell cell4I = (XSSFCell) row4.createCell((short) 8);
		cell4I.setCellStyle(stylefont4Row);
		cell4I.setCellValue(payItemsRecurringPHReport.getEndMonth());

		XSSFCell cell4J = (XSSFCell) row4.createCell((short) 9);
		cell4J.setCellStyle(stylefont4Row);
		cell4J.setCellValue(payItemsRecurringPHReport.getEndCounter());

		XSSFCell cell4K = (XSSFCell) row4.createCell((short) 10);
		cell4K.setCellStyle(stylefont4Row);
		cell4K.setCellValue(payItemsRecurringPHReport.getSuspendRecurring());

		XSSFCell cell4L = (XSSFCell) row4.createCell((short) 11);
		cell4L.setCellStyle(stylefont4Row);
		cell4L.setCellValue(payItemsRecurringPHReport.getRemarks());

	}

}
