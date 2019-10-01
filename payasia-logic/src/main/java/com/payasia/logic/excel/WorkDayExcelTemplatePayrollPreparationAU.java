package com.payasia.logic.excel;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
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
import com.mind.payasia.xml.bean.workday.empdata.IdentifierType;
import com.mind.payasia.xml.bean.workday.empdata.PayDataType;
import com.mind.payasia.xml.bean.workday.empdata.PaymentElectionType;
import com.mind.payasia.xml.bean.workday.empdata.PersonalType;
import com.mind.payasia.xml.bean.workday.empdata.PositionType;
import com.mind.payasia.xml.bean.workday.empdata.SalaryAndHourlyPlansType;
import com.mind.payasia.xml.bean.workday.empdata.SummaryType;
import com.mind.payasia.xml.bean.workday.paydata.TimeOffType;
import com.payasia.common.dto.AllowancePlansAUReportDTO;
import com.payasia.common.dto.IdentifierAdditionalInfoAUReportDTO;
import com.payasia.common.dto.OneOffPaymentsAUReportDTO;
import com.payasia.common.dto.PaymentElectionsAUReportDTO;
import com.payasia.common.dto.PersonalAUReportDTO;
import com.payasia.common.dto.PositionAUReportDTO;
import com.payasia.common.dto.SalaryHourlyPlansAUReportDTO;
import com.payasia.common.dto.TimeOffAUReportDTO;
import com.payasia.common.dto.WorkDayAUPayRollReportDTO;
import com.payasia.common.dto.WorkDayReportDTO;
import com.payasia.dao.bean.WorkdayPaygroupBatchData;

public class WorkDayExcelTemplatePayrollPreparationAU extends WorkDayExcelTemplate {

	@Override
	public XSSFWorkbook generateReport(List<WorkDayReportDTO> workDayReportDTOList) {

		XSSFWorkbook workbook = new XSSFWorkbook();

		createSheetForPayrollPreparation(workbook, workDayReportDTOList);

		return workbook;
	}

	@Override
	public List<WorkDayReportDTO> getReportDataMappedObject(JAXBElement employeeTypeElement,
			WorkdayPaygroupBatchData workdayPaygroupBatch) {

		List<WorkDayReportDTO> workDayReportDTOList = new ArrayList<WorkDayReportDTO>();

		WorkDayAUPayRollReportDTO workDayAUPayRollReport = new WorkDayAUPayRollReportDTO();

		if (employeeTypeElement.getValue() != null
				&& (employeeTypeElement.getValue() instanceof com.mind.payasia.xml.bean.workday.paydata.EmployeeType)) {
			com.mind.payasia.xml.bean.workday.paydata.EmployeeType employeeType = (com.mind.payasia.xml.bean.workday.paydata.EmployeeType) employeeTypeElement
					.getValue();
			TimeOffAUReportDTO timeOffAUReportDTO = null;
			com.mind.payasia.xml.bean.workday.paydata.SummaryType summaryType = employeeType.getSummary();
			List<TimeOffType> timeOffTypeList = employeeType.getTimeOff();

			if (timeOffTypeList != null && !timeOffTypeList.isEmpty()) {
				int count = 0;
				for (TimeOffType timeOffType : timeOffTypeList) {
					timeOffAUReportDTO = new TimeOffAUReportDTO();
					if (count == 0) {
						if (summaryType != null) {
							timeOffAUReportDTO.setEmployeeID(
									summaryType.getEmployeeID() == null ? "" : summaryType.getEmployeeID().getValue());
							String name = summaryType.getName() == null ? "" : summaryType.getName().getValue();
							String nameArr[] = name.split(" ");
							if (nameArr != null && nameArr.length > 0) {
								timeOffAUReportDTO.setFirstName(nameArr[0]);
								if (nameArr.length == 2) {
									timeOffAUReportDTO.setLastName(nameArr[1]);
								} else if (nameArr.length == 3) {
									timeOffAUReportDTO.setLastName(nameArr[1] + " " + nameArr[2]);
								}
							}
						}
						timeOffAUReportDTO.setTimeOffCode(
								timeOffType.getCode() == null ? "" : timeOffType.getCode().getValue().getValue());
						timeOffAUReportDTO.setQuantity(timeOffType.getQuantity() == null ? ""
								: timeOffType.getQuantity().getValue().getValue());
						timeOffAUReportDTO.setUnitofTime(timeOffType.getUnitOfTime() == null ? ""
								: timeOffType.getUnitOfTime().getValue().getValue());
					} else {
						TimeOffAUReportDTO timeOffAUReportDTOExt = new TimeOffAUReportDTO();
						WorkDayAUPayRollReportDTO workDayAUPayRollReportExt = new WorkDayAUPayRollReportDTO();

						if (summaryType != null) {
							timeOffAUReportDTOExt.setEmployeeID(
									summaryType.getEmployeeID() == null ? "" : summaryType.getEmployeeID().getValue());
							String name = summaryType.getName() == null ? "" : summaryType.getName().getValue();
							String nameArr[] = name.split(" ");
							if (nameArr != null && nameArr.length > 0) {
								timeOffAUReportDTOExt.setFirstName(nameArr[0]);
								if (nameArr.length == 2) {
									timeOffAUReportDTOExt.setLastName(nameArr[1]);
								} else if (nameArr.length == 3) {
									timeOffAUReportDTOExt.setLastName(nameArr[1] + " " + nameArr[2]);
								}
							}
						}
						timeOffAUReportDTOExt.setTimeOffCode(
								timeOffType.getCode() == null ? "" : timeOffType.getCode().getValue().getValue());
						timeOffAUReportDTOExt.setQuantity(timeOffType.getQuantity() == null ? ""
								: timeOffType.getQuantity().getValue().getValue());
						timeOffAUReportDTOExt.setUnitofTime(timeOffType.getUnitOfTime() == null ? ""
								: timeOffType.getUnitOfTime().getValue().getValue());
						workDayAUPayRollReportExt.setTimeOffAUReportDTO(timeOffAUReportDTOExt);
					}
				}
				workDayAUPayRollReport.setTimeOffAUReportDTO(timeOffAUReportDTO);
			}
		} else if (employeeTypeElement.getValue() != null
				&& (employeeTypeElement.getValue() instanceof com.mind.payasia.xml.bean.workday.empdata.EmployeeType)) {
			com.mind.payasia.xml.bean.workday.empdata.EmployeeType employeeType = (com.mind.payasia.xml.bean.workday.empdata.EmployeeType) employeeTypeElement
					.getValue();

			SummaryType summaryType = employeeType.getSummary();

			PersonalType personalType = employeeType.getPersonal() == null ? null
					: employeeType.getPersonal().getValue();

			List<PositionType> positionTypeList = employeeType.getPosition();
			List<SalaryAndHourlyPlansType> salaryAndHourlyPlansTypeList = employeeType.getSalaryAndHourlyPlans();
			List<AllowancePlansType> allowancePlansTypeList = employeeType.getAllowancePlans();
			List<PaymentElectionType> paymentElectionTypeList = employeeType.getPaymentElection();
			List<IdentifierType> identifierTypeList = employeeType.getIdentifier();
			List<AdditionalInformationType> additionalInformationTypeList = employeeType.getAdditionalInformation();
			List<PayDataType> payDataTypeList = employeeType.getPayData();

			PersonalAUReportDTO personalAUReport = null;
			PositionAUReportDTO positionAUReport = null;
			SalaryHourlyPlansAUReportDTO salaryHourlyPlansAUReport = null;
			AllowancePlansAUReportDTO allowancePlansAUReport = null;
			PaymentElectionsAUReportDTO paymentElectionsAUReport = null;
			IdentifierAdditionalInfoAUReportDTO identifierAdditionalInfoAUReport = null;
			OneOffPaymentsAUReportDTO oneOffPaymentsAUReportDTO = null;

			if (personalType != null) {
				personalAUReport = new PersonalAUReportDTO();
				if (summaryType != null) {
					personalAUReport.setEmployeeID(
							summaryType.getEmployeeID() == null ? "" : summaryType.getEmployeeID().getValue());
				}
				personalAUReport
						.setTitle(personalType.getTitle() == null ? "" : personalType.getTitle().getValue().getValue());
				personalAUReport
						.setTitle(personalType.getTitle() == null ? "" : personalType.getTitle().getValue().getValue());
				// personalAUReport.setPreferredName(preferredName);
				personalAUReport.setFirstName(
						personalType.getFirstName() == null ? "" : personalType.getFirstName().getValue().getValue());
				personalAUReport.setMiddleName(
						personalType.getMiddleName() == null ? "" : personalType.getMiddleName().getValue().getValue());
				personalAUReport.setSurname(
						personalType.getLastName() == null ? "" : personalType.getLastName().getValue().getValue());
				personalAUReport.setDateOfBirth(
						personalType.getBirthDate() == null ? "" : personalType.getBirthDate().getValue().getValue());
				personalAUReport.setGender(
						personalType.getGender() == null ? "" : personalType.getGender().getValue().getValue());
				// personalAUReport.setExternalId(externalId);
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
							personalAUReport.setResidentialStreetAddress(addressLineType.getValue());
							personalAUReport.setPostalStreetAddress(addressLineType.getValue());
						}
						if (addressLineType.getValue() != null && i == 1) {
							personalAUReport.setResidentialAddressLine2(addressLineType.getValue());
							personalAUReport.setPostalAddressLine2(addressLineType.getValue());
						}
						if (addressLineType.getValue() != null && i == 2) {

						}
						i++;
					}
				}
				personalAUReport.setResidentialSuburb(personalType.getFirstMunicipality() == null ? ""
						: personalType.getFirstMunicipality().getValue().getValue());
				personalAUReport.setResidentialState(personalType.getFirstRegion() == null ? ""
						: personalType.getFirstRegion().getValue().getValue());
				personalAUReport.setResidentialPostCode(personalType.getFirstPostalCode() == null ? ""
						: personalType.getFirstPostalCode().getValue().getValue());
				personalAUReport.setPostalSuburb(personalType.getFirstMunicipality() == null ? ""
						: personalType.getFirstMunicipality().getValue().getValue());
				personalAUReport.setPostalState(personalType.getFirstRegion() == null ? ""
						: personalType.getFirstRegion().getValue().getValue());
				personalAUReport.setPostalPostCode(personalType.getFirstPostalCode() == null ? ""
						: personalType.getFirstPostalCode().getValue().getValue());
				personalAUReport.setEmailAddress(personalType.getFirstEmailAddress() == null ? ""
						: personalType.getFirstEmailAddress().getValue().getValue());
				personalAUReport.setHomePhone(personalType.getPhoneNumber() == null ? ""
						: personalType.getPhoneNumber().getValue().getValue());
				// personalAUReport.setWorkPhone(personalType.getBirthDate()==
				// null ? "" :
				// personalType.getBirthDate().getValue().getValue());
				personalAUReport.setMobilePhone("0");
				// personalAUReport.setStartDate(personalType.get== null ? "" :
				// personalType.getBirthDate().getValue().getValue());
				// personalAUReport.setEndDate(personalType.getBirthDate()==
				// null ? "" :
				// personalType.getBirthDate().getValue().getValue());

				workDayAUPayRollReport.setPersonalAUReport(personalAUReport);
			}

			// Position 0 or 1
			if (positionTypeList != null && !positionTypeList.isEmpty()) {
				positionAUReport = new PositionAUReportDTO();
				for (PositionType positionType : positionTypeList) {
					if (summaryType != null) {
						positionAUReport.setEmployeeID(
								summaryType.getEmployeeID() == null ? "" : summaryType.getEmployeeID().getValue());
						String name = summaryType.getName() == null ? "" : summaryType.getName().getValue();
						String nameArr[] = name.split(" ");
						if (nameArr != null && nameArr.length > 0) {
							positionAUReport.setFirstName(nameArr[0]);
							if (nameArr.length == 2) {
								positionAUReport.setLastName(nameArr[1]);
							} else if (nameArr.length == 3) {
								positionAUReport.setLastName(nameArr[1] + " " + nameArr[2]);
							}
						}
					}

					positionAUReport.setEffectiveDate(positionType.getEffectiveDate() == null ? ""
							: positionType.getEffectiveDate().getValue().getValue());
					positionAUReport.setJobTitle(positionType.getBusinessTitle() == null ? ""
							: positionType.getBusinessTitle().getValue().getValue());
					if (additionalInformationTypeList != null && !additionalInformationTypeList.isEmpty()) {
						for (AdditionalInformationType additionalInformationType : additionalInformationTypeList) {
							positionAUReport.setEmploymentType(additionalInformationType.getEmploymentType() == null
									? "" : additionalInformationType.getEmploymentType().getValue().getValue());
							positionAUReport.setPrimaryPayCategory(
									additionalInformationType.getPrimaryPayCategory() == null ? ""
											: additionalInformationType.getPrimaryPayCategory().getValue().getValue());
							positionAUReport.setPaySchedule(additionalInformationType.getPaySchedule() == null ? ""
									: additionalInformationType.getPaySchedule().getValue().getValue());
						}
					}

					positionAUReport.setHoursPerWeek(positionType.getScheduledWeeklyHours() == null ? ""
							: positionType.getScheduledWeeklyHours().getValue().getValue());

				}
				workDayAUPayRollReport.setPositionAUReport(positionAUReport);
			}

			// Salary_and_Hourly_Plans 0 or 1
			if (salaryAndHourlyPlansTypeList != null && !salaryAndHourlyPlansTypeList.isEmpty()) {
				salaryHourlyPlansAUReport = new SalaryHourlyPlansAUReportDTO();
				for (SalaryAndHourlyPlansType salaryAndHourlyPlansType : salaryAndHourlyPlansTypeList) {
					if (summaryType != null) {
						salaryHourlyPlansAUReport.setEmployeeID(
								summaryType.getEmployeeID() == null ? "" : summaryType.getEmployeeID().getValue());
						String name = summaryType.getName() == null ? "" : summaryType.getName().getValue();
						String nameArr[] = name.split(" ");
						if (nameArr != null && nameArr.length > 0) {
							salaryHourlyPlansAUReport.setFirstName(nameArr[0]);
							if (nameArr.length == 2) {
								salaryHourlyPlansAUReport.setLastName(nameArr[1]);
							} else if (nameArr.length == 3) {
								salaryHourlyPlansAUReport.setLastName(nameArr[1] + " " + nameArr[2]);
							}
						}
					}
					salaryHourlyPlansAUReport.setEffectiveDate(
							"" + workdayPaygroupBatch.getWorkdayPaygroupBatch().getPayPeriodStartDate());
					salaryHourlyPlansAUReport.setProgressionDate(salaryAndHourlyPlansType.getStartDate() == null ? ""
							: salaryAndHourlyPlansType.getStartDate().getValue().getValue());
					salaryHourlyPlansAUReport.setCompensationPlan(salaryAndHourlyPlansType.getCompensationPlan() == null
							? "" : salaryAndHourlyPlansType.getCompensationPlan().getValue().getValue());
					salaryHourlyPlansAUReport.setRate(salaryAndHourlyPlansType.getAmount() == null ? ""
							: salaryAndHourlyPlansType.getAmount().getValue().getValue());
					salaryHourlyPlansAUReport.setCurrency(salaryAndHourlyPlansType.getCurrency() == null ? ""
							: salaryAndHourlyPlansType.getCurrency().getValue().getValue());
					salaryHourlyPlansAUReport.setRateUnit(salaryAndHourlyPlansType.getFrequency() == null ? ""
							: salaryAndHourlyPlansType.getFrequency().getValue().getValue());
				}
				workDayAUPayRollReport.setSalaryHourlyPlansAUReport(salaryHourlyPlansAUReport);
			}

			// Allowance Plans 0 or many
			if (allowancePlansTypeList != null && !allowancePlansTypeList.isEmpty()) {
				allowancePlansAUReport = new AllowancePlansAUReportDTO();
				int count = 0;
				for (AllowancePlansType allowancePlansType : allowancePlansTypeList) {
					if (allowancePlansType.getOperation() != null
							&& !allowancePlansType.getOperation().getValue().value().equalsIgnoreCase("REMOVE")) {
						if (count == 0) {
							if (summaryType != null) {
								allowancePlansAUReport.setEmployeeID(summaryType.getEmployeeID() == null ? ""
										: summaryType.getEmployeeID().getValue());
								String name = summaryType.getName() == null ? "" : summaryType.getName().getValue();
								String nameArr[] = name.split(" ");
								if (nameArr != null && nameArr.length > 0) {
									allowancePlansAUReport.setFirstName(nameArr[0]);
									if (nameArr.length == 2) {
										allowancePlansAUReport.setLastName(nameArr[1]);
									} else if (nameArr.length == 3) {
										allowancePlansAUReport.setLastName(nameArr[1] + " " + nameArr[2]);
									}
								}
							}
							allowancePlansAUReport.setEffectiveDate(
									"" + workdayPaygroupBatch.getWorkdayPaygroupBatch().getPayPeriodStartDate());
							allowancePlansAUReport.setCompensationPlan(allowancePlansType.getCompensationPlan() == null
									? "" : allowancePlansType.getCompensationPlan().getValue().getValue());
							allowancePlansAUReport.setAmount(allowancePlansType.getAmount() == null ? ""
									: allowancePlansType.getAmount().getValue().getValue());
							allowancePlansAUReport.setPercentage(allowancePlansType.getPercentage() == null ? ""
									: allowancePlansType.getPercentage().getValue().getValue());
							allowancePlansAUReport.setCurrency(allowancePlansType.getCurrency() == null ? ""
									: allowancePlansType.getCurrency().getValue().getValue());
							allowancePlansAUReport.setFrequency(allowancePlansType.getFrequency() == null ? ""
									: allowancePlansType.getFrequency().getValue().getValue());
							count++;
						} else {
							WorkDayAUPayRollReportDTO workDayAUPayRollReportExt = new WorkDayAUPayRollReportDTO();
							AllowancePlansAUReportDTO allowancePlansAUReportExt = new AllowancePlansAUReportDTO();
							if (summaryType != null) {
								allowancePlansAUReportExt.setEmployeeID(summaryType.getEmployeeID() == null ? ""
										: summaryType.getEmployeeID().getValue());
								String name = summaryType.getName() == null ? "" : summaryType.getName().getValue();
								String nameArr[] = name.split(" ");
								if (nameArr != null && nameArr.length > 0) {
									allowancePlansAUReportExt.setFirstName(nameArr[0]);
									if (nameArr.length == 2) {
										allowancePlansAUReportExt.setLastName(nameArr[1]);
									} else if (nameArr.length == 3) {
										allowancePlansAUReportExt.setLastName(nameArr[1] + " " + nameArr[2]);
									}
								}
							}
							allowancePlansAUReportExt.setEffectiveDate(
									"" + workdayPaygroupBatch.getWorkdayPaygroupBatch().getPayPeriodStartDate());
							allowancePlansAUReportExt
									.setCompensationPlan(allowancePlansType.getCompensationPlan() == null ? ""
											: allowancePlansType.getCompensationPlan().getValue().getValue());
							allowancePlansAUReportExt.setAmount(allowancePlansType.getAmount() == null ? ""
									: allowancePlansType.getAmount().getValue().getValue());
							allowancePlansAUReportExt.setPercentage(allowancePlansType.getPercentage() == null ? ""
									: allowancePlansType.getPercentage().getValue().getValue());
							allowancePlansAUReportExt.setCurrency(allowancePlansType.getCurrency() == null ? ""
									: allowancePlansType.getCurrency().getValue().getValue());
							allowancePlansAUReportExt.setFrequency(allowancePlansType.getFrequency() == null ? ""
									: allowancePlansType.getFrequency().getValue().getValue());
							count++;

							workDayAUPayRollReportExt.setAllowancePlansAUReport(allowancePlansAUReportExt);
							workDayReportDTOList.add(workDayAUPayRollReportExt);
						}
					}
				}
				workDayAUPayRollReport.setAllowancePlansAUReport(allowancePlansAUReport);
			}

			// Payment Election 0 or many
			if (paymentElectionTypeList != null && !paymentElectionTypeList.isEmpty()) {
				paymentElectionsAUReport = new PaymentElectionsAUReportDTO();
				int count = 0;

				for (PaymentElectionType paymentElectionType : paymentElectionTypeList) {
					if (paymentElectionType.getOperation() != null
							&& !paymentElectionType.getOperation().getValue().value().equalsIgnoreCase("REMOVE")) {

						if (count == 0) {
							if (summaryType != null) {
								paymentElectionsAUReport.setEmployeeID(summaryType.getEmployeeID() == null ? ""
										: summaryType.getEmployeeID().getValue());
								String name = summaryType.getName() == null ? "" : summaryType.getName().getValue();
								String nameArr[] = name.split(" ");
								if (nameArr != null && nameArr.length > 0) {
									paymentElectionsAUReport.setFirstName(nameArr[0]);
									if (nameArr.length == 2) {
										paymentElectionsAUReport.setLastName(nameArr[1]);
									} else if (nameArr.length == 3) {
										paymentElectionsAUReport.setLastName(nameArr[1] + " " + nameArr[2]);
									}
								}
							}
							paymentElectionsAUReport.setEffectivedate(
									workdayPaygroupBatch.getWorkdayPaygroupBatch().getPayPeriodStartDate().toString());
							paymentElectionsAUReport.setDisplayOrder(paymentElectionType.getOrder() == null ? ""
									: paymentElectionType.getOrder().getValue().getValue());
							paymentElectionsAUReport.setBankAccount1BSB(paymentElectionType.getBankIDNumber() == null
									? "" : paymentElectionType.getBankIDNumber().getValue().getValue());
							paymentElectionsAUReport
									.setBankAccount1AccountNumber(paymentElectionType.getAccountNumber() == null ? ""
											: paymentElectionType.getAccountNumber().getValue().getValue());
							paymentElectionsAUReport
									.setBankAccount1AccountName(paymentElectionType.getBankAccountName() == null ? ""
											: paymentElectionType.getBankAccountName().getValue().getValue());
							paymentElectionsAUReport.setBankAccount1AllocatedPercentage(
									paymentElectionType.getDistributionPercentage() == null ? ""
											: paymentElectionType.getDistributionPercentage().getValue().getValue());
							paymentElectionsAUReport
									.setBankAccount1FixedAmount(paymentElectionType.getDistributionAmount() == null ? ""
											: paymentElectionType.getDistributionAmount().getValue().getValue());

							paymentElectionsAUReport.setBankAccount1DistributionBalance(
									paymentElectionType.getDistributionBalance() == null ? ""
											: paymentElectionType.getDistributionBalance().getValue().getValue());
							count++;
						} else {
							WorkDayAUPayRollReportDTO workDayAUPayRollReportExt = new WorkDayAUPayRollReportDTO();
							PaymentElectionsAUReportDTO paymentElectionsAUReportExt = new PaymentElectionsAUReportDTO();
							if (summaryType != null) {
								paymentElectionsAUReportExt.setEmployeeID(summaryType.getEmployeeID() == null ? ""
										: summaryType.getEmployeeID().getValue());
								String name = summaryType.getName() == null ? "" : summaryType.getName().getValue();
								String nameArr[] = name.split(" ");
								if (nameArr != null && nameArr.length > 0) {
									paymentElectionsAUReportExt.setFirstName(nameArr[0]);
									if (nameArr.length == 2) {
										paymentElectionsAUReportExt.setLastName(nameArr[1]);
									} else if (nameArr.length == 3) {
										paymentElectionsAUReportExt.setLastName(nameArr[1] + " " + nameArr[2]);
									}
								}
							}
							paymentElectionsAUReportExt.setEffectivedate(
									workdayPaygroupBatch.getWorkdayPaygroupBatch().getPayPeriodStartDate().toString());
							paymentElectionsAUReportExt.setDisplayOrder(paymentElectionType.getOrder() == null ? ""
									: paymentElectionType.getOrder().getValue().getValue());
							paymentElectionsAUReportExt.setBankAccount1BSB(paymentElectionType.getBankIDNumber() == null
									? "" : paymentElectionType.getBankIDNumber().getValue().getValue());
							paymentElectionsAUReportExt
									.setBankAccount1AccountNumber(paymentElectionType.getAccountNumber() == null ? ""
											: paymentElectionType.getAccountNumber().getValue().getValue());
							paymentElectionsAUReportExt
									.setBankAccount1AccountName(paymentElectionType.getBankAccountName() == null ? ""
											: paymentElectionType.getBankAccountName().getValue().getValue());
							paymentElectionsAUReportExt.setBankAccount1AllocatedPercentage(
									paymentElectionType.getDistributionPercentage() == null ? ""
											: paymentElectionType.getDistributionPercentage().getValue().getValue());
							paymentElectionsAUReportExt
									.setBankAccount1FixedAmount(paymentElectionType.getDistributionAmount() == null ? ""
											: paymentElectionType.getDistributionAmount().getValue().getValue());

							paymentElectionsAUReportExt.setBankAccount1DistributionBalance(
									paymentElectionType.getDistributionBalance() == null ? ""
											: paymentElectionType.getDistributionBalance().getValue().getValue());
							count++;
							workDayAUPayRollReportExt.setPaymentElectionsAUReport(paymentElectionsAUReportExt);
							workDayReportDTOList.add(workDayAUPayRollReportExt);
						}
					}
					workDayAUPayRollReport.setPaymentElectionsAUReport(paymentElectionsAUReport);
				}
			}

			// Identifier 0 or 1
			if (identifierTypeList != null && !identifierTypeList.isEmpty()) {
				identifierAdditionalInfoAUReport = new IdentifierAdditionalInfoAUReportDTO();

				for (IdentifierType identifierType : identifierTypeList) {
					if (identifierType.getOperation() != null
							&& !identifierType.getOperation().getValue().value().equalsIgnoreCase("REMOVE")) {
						if (summaryType != null) {
							identifierAdditionalInfoAUReport.setEmployeeID(
									summaryType.getEmployeeID() == null ? "" : summaryType.getEmployeeID().getValue());
							String name = summaryType.getName() == null ? "" : summaryType.getName().getValue();
							String nameArr[] = name.split(" ");
							if (nameArr != null && nameArr.length > 0) {
								identifierAdditionalInfoAUReport.setFirstName(nameArr[0]);
								if (nameArr.length == 2) {
									identifierAdditionalInfoAUReport.setLastName(nameArr[1]);
								} else if (nameArr.length == 3) {
									identifierAdditionalInfoAUReport.setLastName(nameArr[1] + " " + nameArr[2]);
								}
							}
							identifierAdditionalInfoAUReport
									.setTaxFileNumber(identifierType.getIdentifierValue() == null ? ""
											: identifierType.getIdentifierValue().getValue().getValue());
						}
					}
				}

				if (additionalInformationTypeList != null && !additionalInformationTypeList.isEmpty()) {
					for (AdditionalInformationType additionalInformationType : additionalInformationTypeList) {
						identifierAdditionalInfoAUReport
								.setAustralianResident(additionalInformationType.getTFNAustralianResident() == null ? ""
										: additionalInformationType.getTFNAustralianResident().getValue().getValue());
						identifierAdditionalInfoAUReport.setClaimTaxFreeThreshold(
								additionalInformationType.getTFNClaimTaxFreeThreshold() == null ? ""
										: additionalInformationType.getTFNClaimTaxFreeThreshold().getValue()
												.getValue());
						identifierAdditionalInfoAUReport.setClaimsSeniorsTaxOffset(
								additionalInformationType.getTFNSeniorsTaxOffset() == null ? ""
										: additionalInformationType.getTFNSeniorsTaxOffset().getValue().getValue());
						identifierAdditionalInfoAUReport
								.setClaimsOtherTaxOffset(additionalInformationType.getTFNOtherTaxOffset() == null ? ""
										: additionalInformationType.getTFNOtherTaxOffset().getValue().getValue());
						identifierAdditionalInfoAUReport
								.setAccumulatedHELPdebt(additionalInformationType.getTFNHelpDebt() == null ? ""
										: additionalInformationType.getTFNHelpDebt().getValue().getValue());
						identifierAdditionalInfoAUReport
								.setAccumulatedAFSDebt(additionalInformationType.getTFNAFSDebt() == null ? ""
										: additionalInformationType.getTFNAFSDebt().getValue().getValue());
						// identifierAdditionalInfoAUReport.setExemptFromFloodLevy("");
						// identifierAdditionalInfoAUReport.setHasApprovedWorkingHolidayVisa("");
						// identifierAdditionalInfoAUReport.setHasWithholdingVariation("");
						// identifierAdditionalInfoAUReport.setTaxVariation("");
						// identifierAdditionalInfoAUReport.setMedicareLevyExemption("");
						// identifierAdditionalInfoAUReport.setDateTaxFileDeclarationSigned("");
						// identifierAdditionalInfoAUReport.setDateTaxFileDeclarationReported(additionalInformationType.getTFNAustralianResident()==null?"":additionalInformationType.getTFNAustralianResident().getValue().getValue());
						identifierAdditionalInfoAUReport
								.setPrimaryLocation(additionalInformationType.getPrimaryLocation() == null ? ""
										: additionalInformationType.getPrimaryLocation().getValue().getValue());
						identifierAdditionalInfoAUReport.setIsEnabledForTimesheets(
								additionalInformationType.getIsEnabledForTimesheets() == null ? ""
										: additionalInformationType.getIsEnabledForTimesheets().getValue().getValue());
						identifierAdditionalInfoAUReport
								.setEmployingEntityABN(additionalInformationType.getEmployingEntityABN() == null ? ""
										: additionalInformationType.getEmployingEntityABN().getValue().getValue());
						identifierAdditionalInfoAUReport.setAutomaticallyPayEmployee(
								additionalInformationType.getAutomaticallyPayEmployee() == null ? ""
										: additionalInformationType.getAutomaticallyPayEmployee().getValue()
												.getValue());
					}
				}
				workDayAUPayRollReport.setIdentifierAdditionalInfoAUReport(identifierAdditionalInfoAUReport);
			}
			// One-Off Payments 0 or 1
			if (payDataTypeList != null && !payDataTypeList.isEmpty()) {
				oneOffPaymentsAUReportDTO = new OneOffPaymentsAUReportDTO();
				int count = 0;

				for (PayDataType payDataType : payDataTypeList) {
					if (count == 0) {
						if (summaryType != null) {
							oneOffPaymentsAUReportDTO.setEmployeeID(
									summaryType.getEmployeeID() == null ? "" : summaryType.getEmployeeID().getValue());
							String name = summaryType.getName() == null ? "" : summaryType.getName().getValue();
							String nameArr[] = name.split(" ");
							if (nameArr != null && nameArr.length > 0) {
								oneOffPaymentsAUReportDTO.setFirstName(nameArr[0]);
								if (nameArr.length == 2) {
									oneOffPaymentsAUReportDTO.setLastName(nameArr[1]);
								} else if (nameArr.length == 3) {
									oneOffPaymentsAUReportDTO.setLastName(nameArr[1] + " " + nameArr[2]);
								}
							}
						}
						oneOffPaymentsAUReportDTO.setCode(
								payDataType.getCode() == null ? "" : payDataType.getCode().getValue().getValue());
						oneOffPaymentsAUReportDTO.setAmount(
								payDataType.getAmount() == null ? "" : payDataType.getAmount().getValue().getValue());
						oneOffPaymentsAUReportDTO.setCurrency(payDataType.getCurrency() == null ? ""
								: payDataType.getCurrency().getValue().getValue());

					} else {
						WorkDayAUPayRollReportDTO workDayAUPayRollReportExt = new WorkDayAUPayRollReportDTO();
						OneOffPaymentsAUReportDTO oneOffPaymentsAUReportDTOExt = new OneOffPaymentsAUReportDTO();
						if (summaryType != null) {
							oneOffPaymentsAUReportDTOExt.setEmployeeID(
									summaryType.getEmployeeID() == null ? "" : summaryType.getEmployeeID().getValue());
							String name = summaryType.getName() == null ? "" : summaryType.getName().getValue();
							String nameArr[] = name.split(" ");
							if (nameArr != null && nameArr.length > 0) {
								oneOffPaymentsAUReportDTOExt.setFirstName(nameArr[0]);
								if (nameArr.length == 2) {
									oneOffPaymentsAUReportDTOExt.setLastName(nameArr[1]);
								} else if (nameArr.length == 3) {
									oneOffPaymentsAUReportDTOExt.setLastName(nameArr[1] + " " + nameArr[2]);
								}
							}
						}
						oneOffPaymentsAUReportDTOExt.setCode(
								payDataType.getCode() == null ? "" : payDataType.getCode().getValue().getValue());
						oneOffPaymentsAUReportDTOExt.setAmount(
								payDataType.getAmount() == null ? "" : payDataType.getAmount().getValue().getValue());
						oneOffPaymentsAUReportDTOExt.setCurrency(payDataType.getCurrency() == null ? ""
								: payDataType.getCurrency().getValue().getValue());

						workDayAUPayRollReportExt.setOneOffPaymentsAUReportDTO(oneOffPaymentsAUReportDTOExt);
						workDayReportDTOList.add(workDayAUPayRollReportExt);
					}
					count++;
				}
				workDayAUPayRollReport.setOneOffPaymentsAUReportDTO(oneOffPaymentsAUReportDTO);
			}
		}
		workDayReportDTOList.add(workDayAUPayRollReport);
		return workDayReportDTOList;
	}

	private void createSheetForPayrollPreparation(XSSFWorkbook workbook, List<WorkDayReportDTO> workDayReportDTOList) {

		XSSFSheet sheet1 = workbook.createSheet("Personal");
		///sheet1.setTabColor(3);

		createSheetForPersonal(workbook, sheet1);

		XSSFSheet sheet2 = workbook.createSheet("Position");
		///sheet2.setTabColor(3);

		createSheetForPosition(workbook, sheet2);

		XSSFSheet sheet3 = workbook.createSheet("Salary & Hourly Plans");
		///sheet3.setTabColor(3);

		createSheetForSalaryHourlyPlans(workbook, sheet3);

		XSSFSheet sheet4 = workbook.createSheet("Allowance Plans");
		///sheet4.setTabColor(3);

		createSheetForAllowancePlans(workbook, sheet4);

		XSSFSheet sheet5 = workbook.createSheet("Payment Elections");

		//sheet5.setTabColor(3);

		createSheetForPaymentElections(workbook, sheet5);

		XSSFSheet sheet6 = workbook.createSheet("Identifier & Additional Info");
		//sheet6.setTabColor(3);

		createSheetForIdentifierAdditionalInfo(workbook, sheet6);

		XSSFSheet sheet7 = workbook.createSheet("Time-Off");
		///sheet7.setTabColor(3);

		createSheetForTimeOff(workbook, sheet7);

		XSSFSheet sheet8 = workbook.createSheet("One-Off Payments");
		///sheet8.setTabColor(3);

		createSheetForOneOffPayments(workbook, sheet8);

		int rowCount1 = 1, rowCount2 = 1, rowCount3 = 1, rowCount4 = 1, rowCount5 = 1, rowCount6 = 1, rowCount7 = 1,
				rowCount8 = 1;

		if (workDayReportDTOList != null && !workDayReportDTOList.isEmpty()) {

			for (WorkDayReportDTO workDayReportDTO : workDayReportDTOList) {

				WorkDayAUPayRollReportDTO workDayAUPayRollReport = (WorkDayAUPayRollReportDTO) workDayReportDTO;

				if (workDayAUPayRollReport.getPersonalAUReport() != null) {
					XSSFRow rowsheet1 = sheet1.createRow((short) rowCount1++);
					rowsheet1.setHeightInPoints((short) 20);
					setPersonal(workbook, rowsheet1, workDayAUPayRollReport);
				}
				if (workDayAUPayRollReport.getPositionAUReport() != null) {
					XSSFRow rowsheet2 = sheet2.createRow((short) rowCount2++);
					rowsheet2.setHeightInPoints((short) 20);
					setPosition(workbook, rowsheet2, workDayAUPayRollReport);
				}
				if (workDayAUPayRollReport.getSalaryHourlyPlansAUReport() != null) {
					XSSFRow rowsheet3 = sheet3.createRow((short) rowCount3++);
					rowsheet3.setHeightInPoints((short) 20);
					setSalaryHourlyPlans(workbook, rowsheet3, workDayAUPayRollReport);
				}
				if (workDayAUPayRollReport.getAllowancePlansAUReport() != null) {
					XSSFRow rowsheet4 = sheet4.createRow((short) rowCount4++);
					rowsheet4.setHeightInPoints((short) 20);
					setAllowancePlans(workbook, rowsheet4, workDayAUPayRollReport);
				}
				if (workDayAUPayRollReport.getPaymentElectionsAUReport() != null) {
					XSSFRow rowsheet5 = sheet5.createRow((short) rowCount5++);
					rowsheet5.setHeightInPoints((short) 20);
					setPaymentElections(workbook, rowsheet5, workDayAUPayRollReport);
				}
				if (workDayAUPayRollReport.getIdentifierAdditionalInfoAUReport() != null) {
					XSSFRow rowsheet6 = sheet6.createRow((short) rowCount6++);
					rowsheet6.setHeightInPoints((short) 20);
					setIdentifierAdditionalInfo(workbook, rowsheet6, workDayAUPayRollReport);
				}
				if (workDayAUPayRollReport.getTimeOffAUReportDTO() != null) {
					XSSFRow rowsheet7 = sheet7.createRow((short) rowCount7++);
					rowsheet7.setHeightInPoints((short) 20);
					setTimeOff(workbook, rowsheet7, workDayAUPayRollReport);
				}
				if (workDayAUPayRollReport.getOneOffPaymentsAUReportDTO() != null) {
					XSSFRow rowsheet8 = sheet8.createRow((short) rowCount8++);
					rowsheet8.setHeightInPoints((short) 20);
					setOneOffPayments(workbook, rowsheet8, workDayAUPayRollReport);
				}
			}
		}
	}

	private void createSheetForPersonal(XSSFWorkbook workbook, XSSFSheet sheet) {

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
		cell3A.setCellValue(" Employee ID *");

		sheet.setColumnWidth(0, 4200);

		XSSFCell cell3B = (XSSFCell) row0.createCell((short) 1);
		cell3B.setCellStyle(stylefont3Row);
		cell3B.setCellValue("Title");

		sheet.setColumnWidth(1, 4200);

		XSSFCell cell3C = (XSSFCell) row0.createCell((short) 2);
		cell3C.setCellStyle(stylefont3Row);
		cell3C.setCellValue("Preferred Name");

		sheet.setColumnWidth(2, 4200);

		XSSFCell cell3D = (XSSFCell) row0.createCell((short) 3);
		cell3D.setCellStyle(stylefont3Row);
		cell3D.setCellValue("First Name");

		sheet.setColumnWidth(3, 4200);

		XSSFCell cell3E = (XSSFCell) row0.createCell((short) 4);
		cell3E.setCellStyle(stylefont3Row);
		cell3E.setCellValue("Middle Name");

		sheet.setColumnWidth(4, 4200);

		XSSFCell cell3F = (XSSFCell) row0.createCell((short) 5);
		cell3F.setCellStyle(stylefont3Row);
		cell3F.setCellValue("Surname");

		sheet.setColumnWidth(5, 4200);

		XSSFCell cell3G = (XSSFCell) row0.createCell((short) 6);
		cell3G.setCellStyle(stylefont3Row);
		cell3G.setCellValue("Date Of Birth");

		sheet.setColumnWidth(6, 4200);

		XSSFCell cell3H = (XSSFCell) row0.createCell((short) 7);
		cell3H.setCellStyle(stylefont3Row);
		cell3H.setCellValue("Gender");

		sheet.setColumnWidth(7, 4200);

		XSSFCell cell3I = (XSSFCell) row0.createCell((short) 8);
		cell3I.setCellStyle(stylefont3Row);
		cell3I.setCellValue("External Id");

		sheet.setColumnWidth(8, 4200);

		XSSFCell cell3J = (XSSFCell) row0.createCell((short) 9);
		cell3J.setCellStyle(stylefont3Row);
		cell3J.setCellValue("Residential Street Address");

		sheet.setColumnWidth(9, 13000);

		XSSFCell cell3K = (XSSFCell) row0.createCell((short) 10);
		cell3K.setCellStyle(stylefont3Row);
		cell3K.setCellValue("Residential Address Line 2");

		sheet.setColumnWidth(10, 8000);

		XSSFCell cell3L = (XSSFCell) row0.createCell((short) 11);
		cell3L.setCellStyle(stylefont3Row);
		cell3L.setCellValue("Residential Suburb");

		sheet.setColumnWidth(11, 4200);

		XSSFCell cell3M = (XSSFCell) row0.createCell((short) 12);
		cell3M.setCellStyle(stylefont3Row);
		cell3M.setCellValue("Residential State");

		sheet.setColumnWidth(12, 4200);

		XSSFCell cell3N = (XSSFCell) row0.createCell((short) 13);
		cell3N.setCellStyle(stylefont3Row);
		cell3N.setCellValue("Residential Post Code");

		sheet.setColumnWidth(13, 4200);

		XSSFCell cell3O = (XSSFCell) row0.createCell((short) 14);
		cell3O.setCellStyle(stylefont3Row);
		cell3O.setCellValue("Postal Street Address");

		sheet.setColumnWidth(14, 4200);

		XSSFCell cell3P = (XSSFCell) row0.createCell((short) 15);
		cell3P.setCellStyle(stylefont3Row);
		cell3P.setCellValue("Postal Address Line 2");

		sheet.setColumnWidth(15, 4200);

		XSSFCell cell3Q = (XSSFCell) row0.createCell((short) 16);
		cell3Q.setCellStyle(stylefont3Row);
		cell3Q.setCellValue("Postal Suburb");

		sheet.setColumnWidth(16, 4200);

		XSSFCell cell3R = (XSSFCell) row0.createCell((short) 17);
		cell3R.setCellStyle(stylefont3Row);
		cell3R.setCellValue("Postal State");

		sheet.setColumnWidth(17, 4200);

		XSSFCell cell3S = (XSSFCell) row0.createCell((short) 18);
		cell3S.setCellStyle(stylefont3Row);
		cell3S.setCellValue("Postal PostCode");

		sheet.setColumnWidth(18, 4200);

		XSSFCell cell3T = (XSSFCell) row0.createCell((short) 19);
		cell3T.setCellStyle(stylefont3Row);
		cell3T.setCellValue("Email Address");

		sheet.setColumnWidth(19, 4200);

		XSSFCell cell3U = (XSSFCell) row0.createCell((short) 20);
		cell3U.setCellStyle(stylefont3Row);
		cell3U.setCellValue("Home Phone");

		sheet.setColumnWidth(20, 7400);

		XSSFCell cell3V = (XSSFCell) row0.createCell((short) 21);
		cell3V.setCellStyle(stylefont3Row);
		cell3V.setCellValue("Work Phone");

		sheet.setColumnWidth(21, 7400);

		XSSFCell cell3W = (XSSFCell) row0.createCell((short) 22);
		cell3W.setCellStyle(stylefont3Row);
		cell3W.setCellValue("Mobile Phone");

		sheet.setColumnWidth(22, 4200);

		XSSFCell cell3X = (XSSFCell) row0.createCell((short) 23);
		cell3X.setCellStyle(stylefont3Row);
		cell3X.setCellValue("Start Date");

		sheet.setColumnWidth(23, 4500);

		XSSFCell cell3Y = (XSSFCell) row0.createCell((short) 24);
		cell3Y.setCellStyle(stylefont3Row);
		cell3Y.setCellValue("End Date");

		sheet.setColumnWidth(24, 4200);
	}

	private void setPersonal(XSSFWorkbook workbook, XSSFRow row4, WorkDayAUPayRollReportDTO workDayAUPayRollReport) {

		XSSFFont font4Row = workbook.createFont();
		font4Row.setFontName("Calibri");
		font4Row.setColor(HSSFColor.BLACK.index);
		font4Row.setBold(false);
		font4Row.setFontHeightInPoints((short) 10);

		PersonalAUReportDTO personalAUReportDTO = workDayAUPayRollReport.getPersonalAUReport();

		XSSFCellStyle stylefont4Row = workbook.createCellStyle();
		stylefont4Row.setFont(font4Row);
		stylefont4Row.setAlignment(HorizontalAlignment.CENTER);
		stylefont4Row.setVerticalAlignment(VerticalAlignment.CENTER);
		stylefont4Row.setWrapText(true);

		XSSFCell cell4A = (XSSFCell) row4.createCell((short) 0);
		cell4A.setCellStyle(stylefont4Row);
		cell4A.setCellValue(personalAUReportDTO.getEmployeeID());

		XSSFCell cell4B = (XSSFCell) row4.createCell((short) 1);
		cell4B.setCellStyle(stylefont4Row);
		cell4B.setCellValue(personalAUReportDTO.getTitle());

		XSSFCell cell4C = (XSSFCell) row4.createCell((short) 2);
		cell4C.setCellStyle(stylefont4Row);
		cell4C.setCellValue(personalAUReportDTO.getPreferredName());

		XSSFCell cell4D = (XSSFCell) row4.createCell((short) 3);
		cell4D.setCellStyle(stylefont4Row);
		cell4D.setCellValue(personalAUReportDTO.getFirstName());

		XSSFCell cell4E = (XSSFCell) row4.createCell((short) 4);
		cell4E.setCellStyle(stylefont4Row);
		cell4E.setCellValue(personalAUReportDTO.getMiddleName());

		XSSFCell cell4F = (XSSFCell) row4.createCell((short) 5);
		cell4F.setCellStyle(stylefont4Row);
		cell4F.setCellValue(personalAUReportDTO.getSurname());

		XSSFCell cell4G = (XSSFCell) row4.createCell((short) 6);
		cell4G.setCellStyle(stylefont4Row);
		cell4G.setCellValue(personalAUReportDTO.getDateOfBirth());

		XSSFCell cell4H = (XSSFCell) row4.createCell((short) 7);
		cell4H.setCellStyle(stylefont4Row);
		cell4H.setCellValue(personalAUReportDTO.getGender());

		XSSFCell cell4I = (XSSFCell) row4.createCell((short) 8);
		cell4I.setCellStyle(stylefont4Row);
		cell4I.setCellValue(personalAUReportDTO.getExternalId());

		XSSFCell cell4J = (XSSFCell) row4.createCell((short) 9);
		cell4J.setCellStyle(stylefont4Row);
		cell4J.setCellValue(personalAUReportDTO.getResidentialStreetAddress());

		XSSFCell cell4K = (XSSFCell) row4.createCell((short) 10);
		cell4K.setCellStyle(stylefont4Row);
		cell4K.setCellValue(personalAUReportDTO.getResidentialAddressLine2());

		XSSFCell cell4L = (XSSFCell) row4.createCell((short) 11);
		cell4L.setCellStyle(stylefont4Row);
		cell4L.setCellValue(personalAUReportDTO.getResidentialSuburb());

		XSSFCell cell4M = (XSSFCell) row4.createCell((short) 12);
		cell4M.setCellStyle(stylefont4Row);
		cell4M.setCellValue(personalAUReportDTO.getResidentialState());

		XSSFCell cell4N = (XSSFCell) row4.createCell((short) 13);
		cell4N.setCellStyle(stylefont4Row);
		cell4N.setCellValue(personalAUReportDTO.getResidentialPostCode());

		XSSFCell cell4O = (XSSFCell) row4.createCell((short) 14);
		cell4O.setCellStyle(stylefont4Row);
		cell4O.setCellValue(personalAUReportDTO.getPostalStreetAddress());

		XSSFCell cell4P = (XSSFCell) row4.createCell((short) 15);
		cell4P.setCellStyle(stylefont4Row);
		cell4P.setCellValue(personalAUReportDTO.getPostalAddressLine2());

		XSSFCell cell4Q = (XSSFCell) row4.createCell((short) 16);
		cell4Q.setCellStyle(stylefont4Row);
		cell4Q.setCellValue(personalAUReportDTO.getPostalSuburb());

		XSSFCell cell4R = (XSSFCell) row4.createCell((short) 17);
		cell4R.setCellStyle(stylefont4Row);
		cell4R.setCellValue(personalAUReportDTO.getPostalState());

		XSSFCell cell4S = (XSSFCell) row4.createCell((short) 18);
		cell4S.setCellStyle(stylefont4Row);
		cell4S.setCellValue(personalAUReportDTO.getPostalPostCode());

		XSSFCell cell4T = (XSSFCell) row4.createCell((short) 19);
		cell4T.setCellStyle(stylefont4Row);
		cell4T.setCellValue(personalAUReportDTO.getEmailAddress());

		XSSFCell cell4U = (XSSFCell) row4.createCell((short) 20);
		cell4U.setCellStyle(stylefont4Row);
		cell4U.setCellValue(personalAUReportDTO.getHomePhone());

		XSSFCell cell4V = (XSSFCell) row4.createCell((short) 21);
		cell4V.setCellStyle(stylefont4Row);
		cell4V.setCellValue(personalAUReportDTO.getWorkPhone());

		XSSFCell cell4W = (XSSFCell) row4.createCell((short) 22);
		cell4W.setCellStyle(stylefont4Row);
		cell4W.setCellValue(personalAUReportDTO.getMobilePhone());

		XSSFCell cell4X = (XSSFCell) row4.createCell((short) 23);
		cell4X.setCellStyle(stylefont4Row);
		cell4X.setCellValue(personalAUReportDTO.getStartDate());

		XSSFCell cell4Y = (XSSFCell) row4.createCell((short) 24);
		cell4Y.setCellStyle(stylefont4Row);
		cell4Y.setCellValue(personalAUReportDTO.getEndDate());

	}

	private void createSheetForPosition(XSSFWorkbook workbook, XSSFSheet sheet) {

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
		cell3A.setCellValue("Employee ID");

		sheet.setColumnWidth(0, 4200);

		XSSFCell cell3B = (XSSFCell) row0.createCell((short) 1);
		cell3B.setCellStyle(stylefont3Row);
		cell3B.setCellValue("Last Name");

		sheet.setColumnWidth(1, 4200);

		XSSFCell cell3C = (XSSFCell) row0.createCell((short) 2);
		cell3C.setCellStyle(stylefont3Row);
		cell3C.setCellValue("First Name");

		sheet.setColumnWidth(2, 4200);

		XSSFCell cell3D = (XSSFCell) row0.createCell((short) 3);
		cell3D.setCellStyle(stylefont3Row);
		cell3D.setCellValue("Effective Date");

		sheet.setColumnWidth(3, 4200);

		XSSFCell cell3E = (XSSFCell) row0.createCell((short) 4);
		cell3E.setCellStyle(stylefont3Row);
		cell3E.setCellValue("Job Title");

		sheet.setColumnWidth(4, 4200);

		XSSFCell cell3F = (XSSFCell) row0.createCell((short) 5);
		cell3F.setCellStyle(stylefont3Row);
		cell3F.setCellValue("Employment Type");

		sheet.setColumnWidth(5, 4200);

		XSSFCell cell3G = (XSSFCell) row0.createCell((short) 6);
		cell3G.setCellStyle(stylefont3Row);
		cell3G.setCellValue("Primary Pay Category");

		sheet.setColumnWidth(6, 4200);

		XSSFCell cell3H = (XSSFCell) row0.createCell((short) 7);
		cell3H.setCellStyle(stylefont3Row);
		cell3H.setCellValue("Pay Schedule");

		sheet.setColumnWidth(7, 4200);

		XSSFCell cell3I = (XSSFCell) row0.createCell((short) 8);
		cell3I.setCellStyle(stylefont3Row);
		cell3I.setCellValue("Hours Per Week");

		sheet.setColumnWidth(8, 4200);
	}

	private void setPosition(XSSFWorkbook workbook, XSSFRow row4, WorkDayAUPayRollReportDTO workDayAUPayRollReportDTO) {

		PositionAUReportDTO positionAUReportDTO = workDayAUPayRollReportDTO.getPositionAUReport();

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
		cell4A.setCellValue(positionAUReportDTO.getEmployeeID());

		XSSFCell cell4B = (XSSFCell) row4.createCell((short) 1);
		cell4B.setCellStyle(stylefont4Row);
		cell4B.setCellValue(positionAUReportDTO.getLastName());

		XSSFCell cell4C = (XSSFCell) row4.createCell((short) 2);
		cell4C.setCellStyle(stylefont4Row);
		cell4C.setCellValue(positionAUReportDTO.getFirstName());

		XSSFCell cell4D = (XSSFCell) row4.createCell((short) 3);
		cell4D.setCellStyle(stylefont4Row);
		cell4D.setCellValue(positionAUReportDTO.getEffectiveDate());

		XSSFCell cell4E = (XSSFCell) row4.createCell((short) 4);
		cell4E.setCellStyle(stylefont4Row);
		cell4E.setCellValue(positionAUReportDTO.getJobTitle());

		XSSFCell cell4F = (XSSFCell) row4.createCell((short) 5);
		cell4F.setCellStyle(stylefont4Row);
		cell4F.setCellValue(positionAUReportDTO.getEmploymentType());

		XSSFCell cell4G = (XSSFCell) row4.createCell((short) 6);
		cell4G.setCellStyle(stylefont4Row);
		cell4G.setCellValue(positionAUReportDTO.getPrimaryPayCategory());

		XSSFCell cell4H = (XSSFCell) row4.createCell((short) 7);
		cell4H.setCellStyle(stylefont4Row);
		cell4H.setCellValue(positionAUReportDTO.getPaySchedule());

		XSSFCell cell4I = (XSSFCell) row4.createCell((short) 8);
		cell4I.setCellStyle(stylefont4Row);
		cell4I.setCellValue(positionAUReportDTO.getHoursPerWeek());
	}

	private void createSheetForSalaryHourlyPlans(XSSFWorkbook workbook, XSSFSheet sheet) {

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
		cell3A.setCellValue("Employee ID");

		sheet.setColumnWidth(0, 4200);

		XSSFCell cell3B = (XSSFCell) row0.createCell((short) 1);
		cell3B.setCellStyle(stylefont3Row);
		cell3B.setCellValue("Last Name");

		sheet.setColumnWidth(1, 4200);

		XSSFCell cell3C = (XSSFCell) row0.createCell((short) 2);
		cell3C.setCellStyle(stylefont3Row);
		cell3C.setCellValue("First Name");

		sheet.setColumnWidth(2, 4200);

		XSSFCell cell3D = (XSSFCell) row0.createCell((short) 3);
		cell3D.setCellStyle(stylefont3Row);
		cell3D.setCellValue("Effective Date");

		sheet.setColumnWidth(3, 4200);

		XSSFCell cell3E = (XSSFCell) row0.createCell((short) 4);
		cell3E.setCellStyle(stylefont3Row);
		cell3E.setCellValue("Progression Date");

		sheet.setColumnWidth(4, 4200);

		XSSFCell cell3F = (XSSFCell) row0.createCell((short) 5);
		cell3F.setCellStyle(stylefont3Row);
		cell3F.setCellValue("Compensation Plan");

		sheet.setColumnWidth(5, 4200);

		XSSFCell cell3G = (XSSFCell) row0.createCell((short) 6);
		cell3G.setCellStyle(stylefont3Row);
		cell3G.setCellValue("Rate");

		sheet.setColumnWidth(6, 4200);

		XSSFCell cell3H = (XSSFCell) row0.createCell((short) 7);
		cell3H.setCellStyle(stylefont3Row);
		cell3H.setCellValue("Currency");

		sheet.setColumnWidth(7, 4200);

		XSSFCell cell3I = (XSSFCell) row0.createCell((short) 8);
		cell3I.setCellStyle(stylefont3Row);
		cell3I.setCellValue("RateUnit");

		sheet.setColumnWidth(8, 4200);
	}

	private void setSalaryHourlyPlans(XSSFWorkbook workbook, XSSFRow row4,
			WorkDayAUPayRollReportDTO workDayAUPayRollReportDTO) {

		SalaryHourlyPlansAUReportDTO salaryHourlyPlansAUReportDTO = workDayAUPayRollReportDTO
				.getSalaryHourlyPlansAUReport();

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
		cell4A.setCellValue(salaryHourlyPlansAUReportDTO.getEmployeeID());

		XSSFCell cell4B = (XSSFCell) row4.createCell((short) 1);
		cell4B.setCellStyle(stylefont4Row);
		cell4B.setCellValue(salaryHourlyPlansAUReportDTO.getLastName());

		XSSFCell cell4C = (XSSFCell) row4.createCell((short) 2);
		cell4C.setCellStyle(stylefont4Row);
		cell4C.setCellValue(salaryHourlyPlansAUReportDTO.getFirstName());

		XSSFCell cell4D = (XSSFCell) row4.createCell((short) 3);
		cell4D.setCellStyle(stylefont4Row);
		cell4D.setCellValue(salaryHourlyPlansAUReportDTO.getEffectiveDate());

		XSSFCell cell4P = (XSSFCell) row4.createCell((short) 4);
		cell4P.setCellStyle(stylefont4Row);
		cell4P.setCellValue(salaryHourlyPlansAUReportDTO.getProgressionDate());

		XSSFCell cell4F = (XSSFCell) row4.createCell((short) 5);
		cell4F.setCellStyle(stylefont4Row);
		cell4F.setCellValue(salaryHourlyPlansAUReportDTO.getCompensationPlan());

		XSSFCell cell4G = (XSSFCell) row4.createCell((short) 6);
		cell4G.setCellStyle(stylefont4Row);
		cell4G.setCellValue(salaryHourlyPlansAUReportDTO.getRate());

		XSSFCell cell4H = (XSSFCell) row4.createCell((short) 7);
		cell4H.setCellStyle(stylefont4Row);
		cell4H.setCellValue(salaryHourlyPlansAUReportDTO.getCurrency());

		XSSFCell cell4I = (XSSFCell) row4.createCell((short) 8);
		cell4I.setCellStyle(stylefont4Row);
		cell4I.setCellValue(salaryHourlyPlansAUReportDTO.getRateUnit());
	}

	private void createSheetForAllowancePlans(XSSFWorkbook workbook, XSSFSheet sheet) {

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
		cell3A.setCellValue("Employee ID");

		sheet.setColumnWidth(0, 4200);

		XSSFCell cell3B = (XSSFCell) row0.createCell((short) 1);
		cell3B.setCellStyle(stylefont3Row);
		cell3B.setCellValue("Last Name");

		sheet.setColumnWidth(1, 4200);

		XSSFCell cell3C = (XSSFCell) row0.createCell((short) 2);
		cell3C.setCellStyle(stylefont3Row);
		cell3C.setCellValue("First Name");

		sheet.setColumnWidth(2, 4200);

		XSSFCell cell3D = (XSSFCell) row0.createCell((short) 3);
		cell3D.setCellStyle(stylefont3Row);
		cell3D.setCellValue("Effective Date");

		sheet.setColumnWidth(3, 4200);

		XSSFCell cell3E = (XSSFCell) row0.createCell((short) 4);
		cell3E.setCellStyle(stylefont3Row);
		cell3E.setCellValue("Compensation Plan");

		sheet.setColumnWidth(4, 4200);

		XSSFCell cell3F = (XSSFCell) row0.createCell((short) 5);
		cell3F.setCellStyle(stylefont3Row);
		cell3F.setCellValue("Amount");

		sheet.setColumnWidth(5, 4200);

		XSSFCell cell3G = (XSSFCell) row0.createCell((short) 6);
		cell3G.setCellStyle(stylefont3Row);
		cell3G.setCellValue("Percentage");

		sheet.setColumnWidth(6, 4200);

		XSSFCell cell3H = (XSSFCell) row0.createCell((short) 7);
		cell3H.setCellStyle(stylefont3Row);
		cell3H.setCellValue("Currency");

		sheet.setColumnWidth(7, 4200);

		XSSFCell cell3I = (XSSFCell) row0.createCell((short) 8);
		cell3I.setCellStyle(stylefont3Row);
		cell3I.setCellValue("Frequency");

		sheet.setColumnWidth(8, 4200);
	}

	private void setAllowancePlans(XSSFWorkbook workbook, XSSFRow row4,
			WorkDayAUPayRollReportDTO workDayAUPayRollReportDTO) {

		AllowancePlansAUReportDTO allowancePlansAUReportDTO = workDayAUPayRollReportDTO.getAllowancePlansAUReport();

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
		cell4A.setCellValue(allowancePlansAUReportDTO.getEmployeeID());

		XSSFCell cell4B = (XSSFCell) row4.createCell((short) 1);
		cell4B.setCellStyle(stylefont4Row);
		cell4B.setCellValue(allowancePlansAUReportDTO.getLastName());

		XSSFCell cell4C = (XSSFCell) row4.createCell((short) 2);
		cell4C.setCellStyle(stylefont4Row);
		cell4C.setCellValue(allowancePlansAUReportDTO.getFirstName());

		XSSFCell cell4D = (XSSFCell) row4.createCell((short) 3);
		cell4D.setCellStyle(stylefont4Row);
		cell4D.setCellValue(allowancePlansAUReportDTO.getEffectiveDate());

		XSSFCell cell4E = (XSSFCell) row4.createCell((short) 4);
		cell4E.setCellStyle(stylefont4Row);
		cell4E.setCellValue(allowancePlansAUReportDTO.getCompensationPlan());

		XSSFCell cell4F = (XSSFCell) row4.createCell((short) 5);
		cell4F.setCellStyle(stylefont4Row);
		cell4F.setCellValue(allowancePlansAUReportDTO.getAmount());

		XSSFCell cell4G = (XSSFCell) row4.createCell((short) 6);
		cell4G.setCellStyle(stylefont4Row);
		cell4G.setCellValue(allowancePlansAUReportDTO.getPercentage());

		XSSFCell cell4H = (XSSFCell) row4.createCell((short) 7);
		cell4H.setCellStyle(stylefont4Row);
		cell4H.setCellValue(allowancePlansAUReportDTO.getCurrency());

		XSSFCell cell4I = (XSSFCell) row4.createCell((short) 8);
		cell4I.setCellStyle(stylefont4Row);
		cell4I.setCellValue(allowancePlansAUReportDTO.getFrequency());

	}

	private void createSheetForPaymentElections(XSSFWorkbook workbook, XSSFSheet sheet) {

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
		cell3A.setCellValue("Employee ID");

		sheet.setColumnWidth(0, 4200);

		XSSFCell cell3B = (XSSFCell) row0.createCell((short) 1);
		cell3B.setCellStyle(stylefont3Row);
		cell3B.setCellValue("Last Name");

		sheet.setColumnWidth(1, 4200);

		XSSFCell cell3C = (XSSFCell) row0.createCell((short) 2);
		cell3C.setCellStyle(stylefont3Row);
		cell3C.setCellValue("First Name");

		sheet.setColumnWidth(2, 4200);

		XSSFCell cell3D = (XSSFCell) row0.createCell((short) 3);
		cell3D.setCellStyle(stylefont3Row);
		cell3D.setCellValue("Effective Date");

		sheet.setColumnWidth(3, 4200);

		XSSFCell cell3E = (XSSFCell) row0.createCell((short) 4);
		cell3E.setCellStyle(stylefont3Row);
		cell3E.setCellValue("Display Order");

		sheet.setColumnWidth(4, 4200);

		XSSFCell cell3F = (XSSFCell) row0.createCell((short) 5);
		cell3F.setCellStyle(stylefont3Row);
		cell3F.setCellValue("BankAccount1_BSB");

		sheet.setColumnWidth(5, 4200);

		XSSFCell cell3G = (XSSFCell) row0.createCell((short) 6);
		cell3G.setCellStyle(stylefont3Row);
		cell3G.setCellValue("BankAccount1_AccountNumber");

		sheet.setColumnWidth(6, 4200);

		XSSFCell cell3H = (XSSFCell) row0.createCell((short) 7);
		cell3H.setCellStyle(stylefont3Row);
		cell3H.setCellValue("BankAccount1_AccountName");

		sheet.setColumnWidth(7, 4200);

		XSSFCell cell3I = (XSSFCell) row0.createCell((short) 8);
		cell3I.setCellStyle(stylefont3Row);
		cell3I.setCellValue("BankAccount1_AllocatedPercentage");

		sheet.setColumnWidth(8, 4200);

		XSSFCell cell3J = (XSSFCell) row0.createCell((short) 9);
		cell3J.setCellStyle(stylefont3Row);
		cell3J.setCellValue("BankAccount1_FixedAmount");

		sheet.setColumnWidth(9, 4200);

		XSSFCell cell3K = (XSSFCell) row0.createCell((short) 10);
		cell3K.setCellStyle(stylefont3Row);
		cell3K.setCellValue("BankAccount1_DistributionBalance");

		sheet.setColumnWidth(10, 4200);
	}

	private void setPaymentElections(XSSFWorkbook workbook, XSSFRow row4,
			WorkDayAUPayRollReportDTO workDayAUPayRollReport) {

		PaymentElectionsAUReportDTO paymentElectionsAUReportDTO = workDayAUPayRollReport.getPaymentElectionsAUReport();

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
		cell4A.setCellValue(paymentElectionsAUReportDTO.getEmployeeID());

		XSSFCell cell4B = (XSSFCell) row4.createCell((short) 1);
		cell4B.setCellStyle(stylefont4Row);
		cell4B.setCellValue(paymentElectionsAUReportDTO.getLastName());

		XSSFCell cell4C = (XSSFCell) row4.createCell((short) 2);
		cell4C.setCellStyle(stylefont4Row);
		cell4C.setCellValue(paymentElectionsAUReportDTO.getFirstName());

		XSSFCell cell4D = (XSSFCell) row4.createCell((short) 3);
		cell4D.setCellStyle(stylefont4Row);
		cell4D.setCellValue(paymentElectionsAUReportDTO.getEffectivedate());

		XSSFCell cell4E = (XSSFCell) row4.createCell((short) 4);
		cell4E.setCellStyle(stylefont4Row);
		cell4E.setCellValue(paymentElectionsAUReportDTO.getDisplayOrder());

		XSSFCell cell4F = (XSSFCell) row4.createCell((short) 5);
		cell4F.setCellStyle(stylefont4Row);
		cell4F.setCellValue(paymentElectionsAUReportDTO.getBankAccount1BSB());

		XSSFCell cell4G = (XSSFCell) row4.createCell((short) 6);
		cell4G.setCellStyle(stylefont4Row);
		cell4G.setCellValue(paymentElectionsAUReportDTO.getBankAccount1AccountNumber());

		XSSFCell cell4H = (XSSFCell) row4.createCell((short) 7);
		cell4H.setCellStyle(stylefont4Row);
		cell4H.setCellValue(paymentElectionsAUReportDTO.getBankAccount1AccountName());

		XSSFCell cell4I = (XSSFCell) row4.createCell((short) 8);
		cell4I.setCellStyle(stylefont4Row);
		cell4I.setCellValue(paymentElectionsAUReportDTO.getBankAccount1AllocatedPercentage());

		XSSFCell cell4J = (XSSFCell) row4.createCell((short) 9);
		cell4J.setCellStyle(stylefont4Row);
		cell4J.setCellValue(paymentElectionsAUReportDTO.getBankAccount1FixedAmount());

		XSSFCell cell4K = (XSSFCell) row4.createCell((short) 10);
		cell4K.setCellStyle(stylefont4Row);
		cell4K.setCellValue(paymentElectionsAUReportDTO.getBankAccount1DistributionBalance());
	}

	private void createSheetForIdentifierAdditionalInfo(XSSFWorkbook workbook, XSSFSheet sheet) {

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
		cell3A.setCellValue("Employee ID");

		sheet.setColumnWidth(0, 4200);

		XSSFCell cell3B = (XSSFCell) row0.createCell((short) 1);
		cell3B.setCellStyle(stylefont3Row);
		cell3B.setCellValue("Last Name");

		sheet.setColumnWidth(1, 4200);

		XSSFCell cell3C = (XSSFCell) row0.createCell((short) 2);
		cell3C.setCellStyle(stylefont3Row);
		cell3C.setCellValue("First Name");

		sheet.setColumnWidth(2, 4200);

		XSSFCell cell3D = (XSSFCell) row0.createCell((short) 3);
		cell3D.setCellStyle(stylefont3Row);
		cell3D.setCellValue("Tax File Number");

		sheet.setColumnWidth(3, 4200);

		XSSFCell cell3E = (XSSFCell) row0.createCell((short) 4);
		cell3E.setCellStyle(stylefont3Row);
		cell3E.setCellValue("Australian Resident");

		sheet.setColumnWidth(4, 4200);

		XSSFCell cell3F = (XSSFCell) row0.createCell((short) 5);
		cell3F.setCellStyle(stylefont3Row);
		cell3F.setCellValue("Claim Tax Free Threshold");

		sheet.setColumnWidth(5, 4200);

		XSSFCell cell3G = (XSSFCell) row0.createCell((short) 6);
		cell3G.setCellStyle(stylefont3Row);
		cell3G.setCellValue("Claims Seniors Tax Offset");

		sheet.setColumnWidth(6, 4200);

		XSSFCell cell3H = (XSSFCell) row0.createCell((short) 7);
		cell3H.setCellStyle(stylefont3Row);
		cell3H.setCellValue("Claims Other Tax Offset");

		sheet.setColumnWidth(7, 4200);

		XSSFCell cell3I = (XSSFCell) row0.createCell((short) 8);
		cell3I.setCellStyle(stylefont3Row);
		cell3I.setCellValue("Accumulated HELP debt");

		sheet.setColumnWidth(8, 4200);

		XSSFCell cell3J = (XSSFCell) row0.createCell((short) 9);
		cell3J.setCellStyle(stylefont3Row);
		cell3J.setCellValue("Accumulated AFS Debt");

		sheet.setColumnWidth(9, 4200);

		XSSFCell cell3K = (XSSFCell) row0.createCell((short) 10);
		cell3K.setCellStyle(stylefont3Row);
		cell3K.setCellValue("Exempt From Flood Levy");

		sheet.setColumnWidth(10, 4200);

		XSSFCell cell3L = (XSSFCell) row0.createCell((short) 11);
		cell3L.setCellStyle(stylefont3Row);
		cell3L.setCellValue("Has Approved Working Holiday Visa");

		sheet.setColumnWidth(11, 4200);

		XSSFCell cell3M = (XSSFCell) row0.createCell((short) 12);
		cell3M.setCellStyle(stylefont3Row);
		cell3M.setCellValue("Has Withholding Variation");

		sheet.setColumnWidth(12, 4200);

		XSSFCell cell3N = (XSSFCell) row0.createCell((short) 13);
		cell3N.setCellStyle(stylefont3Row);
		cell3N.setCellValue("Tax Variation");

		sheet.setColumnWidth(13, 4200);

		XSSFCell cell3O = (XSSFCell) row0.createCell((short) 14);
		cell3O.setCellStyle(stylefont3Row);
		cell3O.setCellValue("Medicare Levy Exemption");

		sheet.setColumnWidth(14, 4200);

		XSSFCell cell3P = (XSSFCell) row0.createCell((short) 15);
		cell3P.setCellStyle(stylefont3Row);
		cell3P.setCellValue("Date Tax File Declaration Signed");

		sheet.setColumnWidth(15, 4200);

		XSSFCell cell3Q = (XSSFCell) row0.createCell((short) 16);
		cell3Q.setCellStyle(stylefont3Row);
		cell3Q.setCellValue("Date Tax File Declaration Reported");

		sheet.setColumnWidth(16, 4200);

		XSSFCell cell3R = (XSSFCell) row0.createCell((short) 17);
		cell3R.setCellStyle(stylefont3Row);
		cell3R.setCellValue("Primary Location");

		sheet.setColumnWidth(17, 4200);

		XSSFCell cell3S = (XSSFCell) row0.createCell((short) 18);
		cell3S.setCellStyle(stylefont3Row);
		cell3S.setCellValue("IsEnabledForTimesheets");

		sheet.setColumnWidth(18, 4200);

		XSSFCell cell3T = (XSSFCell) row0.createCell((short) 19);
		cell3T.setCellStyle(stylefont3Row);
		cell3T.setCellValue("EmployingEntityABN");

		sheet.setColumnWidth(19, 4200);

		XSSFCell cell3U = (XSSFCell) row0.createCell((short) 9);
		cell3U.setCellStyle(stylefont3Row);
		cell3U.setCellValue("Automatically Pay Employee");

		sheet.setColumnWidth(20, 4200);

	}

	private void setIdentifierAdditionalInfo(XSSFWorkbook workbook, XSSFRow row4,
			WorkDayAUPayRollReportDTO workDayAUPayRollReport) {

		IdentifierAdditionalInfoAUReportDTO identifierAdditionalInfoAUReport = workDayAUPayRollReport
				.getIdentifierAdditionalInfoAUReport();

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
		cell4A.setCellValue(identifierAdditionalInfoAUReport.getEmployeeID());

		XSSFCell cell4B = (XSSFCell) row4.createCell((short) 1);
		cell4B.setCellStyle(stylefont4Row);
		cell4B.setCellValue(identifierAdditionalInfoAUReport.getLastName());

		XSSFCell cell4C = (XSSFCell) row4.createCell((short) 2);
		cell4C.setCellStyle(stylefont4Row);
		cell4C.setCellValue(identifierAdditionalInfoAUReport.getFirstName());

		XSSFCell cell4D = (XSSFCell) row4.createCell((short) 3);
		cell4D.setCellStyle(stylefont4Row);
		cell4D.setCellValue(identifierAdditionalInfoAUReport.getTaxFileNumber());

		XSSFCell cell4E = (XSSFCell) row4.createCell((short) 4);
		cell4E.setCellStyle(stylefont4Row);
		cell4E.setCellValue(identifierAdditionalInfoAUReport.getAustralianResident());

		XSSFCell cell4F = (XSSFCell) row4.createCell((short) 5);
		cell4F.setCellStyle(stylefont4Row);
		cell4F.setCellValue(identifierAdditionalInfoAUReport.getClaimTaxFreeThreshold());

		XSSFCell cell4G = (XSSFCell) row4.createCell((short) 6);
		cell4G.setCellStyle(stylefont4Row);
		cell4G.setCellValue(identifierAdditionalInfoAUReport.getClaimsSeniorsTaxOffset());

		XSSFCell cell4H = (XSSFCell) row4.createCell((short) 7);
		cell4H.setCellStyle(stylefont4Row);
		cell4H.setCellValue(identifierAdditionalInfoAUReport.getClaimsOtherTaxOffset());

		XSSFCell cell4I = (XSSFCell) row4.createCell((short) 8);
		cell4I.setCellStyle(stylefont4Row);
		cell4I.setCellValue(identifierAdditionalInfoAUReport.getAccumulatedHELPdebt());

		XSSFCell cell4J = (XSSFCell) row4.createCell((short) 9);
		cell4J.setCellStyle(stylefont4Row);
		cell4J.setCellValue(identifierAdditionalInfoAUReport.getAccumulatedAFSDebt());

		XSSFCell cell4K = (XSSFCell) row4.createCell((short) 10);
		cell4K.setCellStyle(stylefont4Row);
		cell4K.setCellValue(identifierAdditionalInfoAUReport.getExemptFromFloodLevy());

		XSSFCell cell4L = (XSSFCell) row4.createCell((short) 11);
		cell4L.setCellStyle(stylefont4Row);
		cell4L.setCellValue(identifierAdditionalInfoAUReport.getHasApprovedWorkingHolidayVisa());

		XSSFCell cell4M = (XSSFCell) row4.createCell((short) 12);
		cell4M.setCellStyle(stylefont4Row);
		cell4M.setCellValue(identifierAdditionalInfoAUReport.getHasWithholdingVariation());

		XSSFCell cell4N = (XSSFCell) row4.createCell((short) 13);
		cell4N.setCellStyle(stylefont4Row);
		cell4N.setCellValue(identifierAdditionalInfoAUReport.getTaxVariation());

		XSSFCell cell4O = (XSSFCell) row4.createCell((short) 14);
		cell4O.setCellStyle(stylefont4Row);
		cell4O.setCellValue(identifierAdditionalInfoAUReport.getMedicareLevyExemption());

		XSSFCell cell4P = (XSSFCell) row4.createCell((short) 15);
		cell4P.setCellStyle(stylefont4Row);
		cell4P.setCellValue(identifierAdditionalInfoAUReport.getDateTaxFileDeclarationSigned());

		XSSFCell cell4Q = (XSSFCell) row4.createCell((short) 16);
		cell4Q.setCellStyle(stylefont4Row);
		cell4Q.setCellValue(identifierAdditionalInfoAUReport.getDateTaxFileDeclarationReported());

		XSSFCell cell4R = (XSSFCell) row4.createCell((short) 17);
		cell4R.setCellStyle(stylefont4Row);
		cell4R.setCellValue(identifierAdditionalInfoAUReport.getPrimaryLocation());

		XSSFCell cell4S = (XSSFCell) row4.createCell((short) 18);
		cell4S.setCellStyle(stylefont4Row);
		cell4S.setCellValue(identifierAdditionalInfoAUReport.getIsEnabledForTimesheets());

		XSSFCell cell4T = (XSSFCell) row4.createCell((short) 19);
		cell4T.setCellStyle(stylefont4Row);
		cell4T.setCellValue(identifierAdditionalInfoAUReport.getEmployingEntityABN());

		XSSFCell cell4U = (XSSFCell) row4.createCell((short) 20);
		cell4U.setCellStyle(stylefont4Row);
		cell4U.setCellValue(identifierAdditionalInfoAUReport.getAutomaticallyPayEmployee());
	}

	private void createSheetForTimeOff(XSSFWorkbook workbook, XSSFSheet sheet) {

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
		cell3A.setCellValue("Employee ID");

		sheet.setColumnWidth(0, 4200);

		XSSFCell cell3B = (XSSFCell) row0.createCell((short) 1);
		cell3B.setCellStyle(stylefont3Row);
		cell3B.setCellValue("Last Name");

		sheet.setColumnWidth(1, 4200);

		XSSFCell cell3C = (XSSFCell) row0.createCell((short) 2);
		cell3C.setCellStyle(stylefont3Row);
		cell3C.setCellValue("First Name");

		sheet.setColumnWidth(2, 4200);

		XSSFCell cell3D = (XSSFCell) row0.createCell((short) 3);
		cell3D.setCellStyle(stylefont3Row);
		cell3D.setCellValue("Code");

		sheet.setColumnWidth(3, 4200);

		XSSFCell cell3E = (XSSFCell) row0.createCell((short) 4);
		cell3E.setCellStyle(stylefont3Row);
		cell3E.setCellValue("Quantity");

		sheet.setColumnWidth(4, 4200);

		XSSFCell cell3F = (XSSFCell) row0.createCell((short) 5);
		cell3F.setCellStyle(stylefont3Row);
		cell3F.setCellValue("Unit Of Time");

		sheet.setColumnWidth(5, 4200);

	}

	private void setTimeOff(XSSFWorkbook workbook, XSSFRow row4, WorkDayAUPayRollReportDTO workDayAUPayRollReport) {

		TimeOffAUReportDTO timeOffAUReport = workDayAUPayRollReport.getTimeOffAUReportDTO();

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
		cell4A.setCellValue(timeOffAUReport.getEmployeeID());

		XSSFCell cell4B = (XSSFCell) row4.createCell((short) 1);
		cell4B.setCellStyle(stylefont4Row);
		cell4B.setCellValue(timeOffAUReport.getLastName());

		XSSFCell cell4C = (XSSFCell) row4.createCell((short) 2);
		cell4C.setCellStyle(stylefont4Row);
		cell4C.setCellValue(timeOffAUReport.getFirstName());

		XSSFCell cell4D = (XSSFCell) row4.createCell((short) 3);
		cell4D.setCellStyle(stylefont4Row);
		cell4D.setCellValue(timeOffAUReport.getTimeOffCode());

		XSSFCell cell4E = (XSSFCell) row4.createCell((short) 4);
		cell4E.setCellStyle(stylefont4Row);
		cell4E.setCellValue(timeOffAUReport.getQuantity());

		XSSFCell cell4F = (XSSFCell) row4.createCell((short) 5);
		cell4F.setCellStyle(stylefont4Row);
		cell4F.setCellValue(timeOffAUReport.getUnitofTime());
	}

	private void createSheetForOneOffPayments(XSSFWorkbook workbook, XSSFSheet sheet) {
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
		cell3A.setCellValue("Employee ID");

		sheet.setColumnWidth(0, 4200);

		XSSFCell cell3B = (XSSFCell) row0.createCell((short) 1);
		cell3B.setCellStyle(stylefont3Row);
		cell3B.setCellValue("Last Name");

		sheet.setColumnWidth(1, 4200);

		XSSFCell cell3C = (XSSFCell) row0.createCell((short) 2);
		cell3C.setCellStyle(stylefont3Row);
		cell3C.setCellValue("First Name");

		sheet.setColumnWidth(2, 4200);

		XSSFCell cell3D = (XSSFCell) row0.createCell((short) 3);
		cell3D.setCellStyle(stylefont3Row);
		cell3D.setCellValue("Code");

		sheet.setColumnWidth(3, 4200);

		XSSFCell cell3E = (XSSFCell) row0.createCell((short) 4);
		cell3E.setCellStyle(stylefont3Row);
		cell3E.setCellValue("Amount");

		sheet.setColumnWidth(4, 4200);

		XSSFCell cell3F = (XSSFCell) row0.createCell((short) 5);
		cell3F.setCellStyle(stylefont3Row);
		cell3F.setCellValue("Currency");

		sheet.setColumnWidth(5, 4200);

	}

	private void setOneOffPayments(XSSFWorkbook workbook, XSSFRow row4,
			WorkDayAUPayRollReportDTO workDayAUPayRollReport) {

		OneOffPaymentsAUReportDTO oneOffPaymentsAUReportDTO = workDayAUPayRollReport.getOneOffPaymentsAUReportDTO();

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
		cell4A.setCellValue(oneOffPaymentsAUReportDTO.getEmployeeID());

		XSSFCell cell4B = (XSSFCell) row4.createCell((short) 1);
		cell4B.setCellStyle(stylefont4Row);
		cell4B.setCellValue(oneOffPaymentsAUReportDTO.getLastName());

		XSSFCell cell4C = (XSSFCell) row4.createCell((short) 2);
		cell4C.setCellStyle(stylefont4Row);
		cell4C.setCellValue(oneOffPaymentsAUReportDTO.getFirstName());

		XSSFCell cell4D = (XSSFCell) row4.createCell((short) 3);
		cell4D.setCellStyle(stylefont4Row);
		cell4D.setCellValue(oneOffPaymentsAUReportDTO.getCode());

		XSSFCell cell4E = (XSSFCell) row4.createCell((short) 4);
		cell4E.setCellStyle(stylefont4Row);
		cell4E.setCellValue(oneOffPaymentsAUReportDTO.getAmount());

		XSSFCell cell4F = (XSSFCell) row4.createCell((short) 5);
		cell4F.setCellStyle(stylefont4Row);
		cell4F.setCellValue(oneOffPaymentsAUReportDTO.getCurrency());
	}

}
