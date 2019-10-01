package com.payasia.logic.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import com.mind.payasia.xml.bean.Column;
import com.mind.payasia.xml.bean.Field;
import com.mind.payasia.xml.bean.Tab;
import com.payasia.common.dto.WorkDayGenerateReportDTO;
import com.payasia.common.dto.WorkDayReportDTO;
import com.payasia.common.dto.WorkdayPaygroupBatchDTO;
import com.payasia.common.dto.WorkdayReportMasterDTO;
import com.payasia.common.dto.WorkdayTimeSheetDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.WorkdayGenerateReportForm;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.PayAsiaStringUtils;
import com.payasia.common.util.WorkdayEmployeeDataXMLUtil;
import com.payasia.common.util.WorkdayPayrollDataXMLUtil;
import com.payasia.common.util.XMLUtil;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.DynamicFormDAO;
import com.payasia.dao.DynamicFormRecordDAO;
import com.payasia.dao.DynamicFormTableRecordDAO;
import com.payasia.dao.WorkdayAppCodeMasterDAO;
import com.payasia.dao.WorkdayFieldMasterDAO;
import com.payasia.dao.WorkdayFtpFieldMappingDAO;
import com.payasia.dao.WorkdayPaygroupBatchDAO;
import com.payasia.dao.WorkdayPaygroupBatchDataDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.DynamicForm;
import com.payasia.dao.bean.DynamicFormRecord;
import com.payasia.dao.bean.DynamicFormTableRecord;
import com.payasia.dao.bean.WorkdayAppCodeMaster;
import com.payasia.dao.bean.WorkdayFieldMaster;
import com.payasia.dao.bean.WorkdayFtpFieldMapping;
import com.payasia.dao.bean.WorkdayPaygroupBatch;
import com.payasia.dao.bean.WorkdayPaygroupBatchData;
import com.payasia.logic.WorkdayReportLogic;
import com.payasia.logic.excel.WorkDayExcelTemplate;
import com.payasia.logic.excel.WorkDayExcelTemplateFactory;

@Component
public class WorkdayReportLogicImpl implements WorkdayReportLogic {

	private static final Logger LOGGER = Logger.getLogger(WorkdayReportLogicImpl.class);

	// private static final Logger LOGGER =
	// Logger.getLogger(WorkdayReportLogicImpl.class);

	@Resource
	WorkdayFieldMasterDAO workdayFieldMasterDAO;

	@Resource
	WorkdayPaygroupBatchDataDAO workdayPaygroupBatchDataDAO;

	@Resource
	WorkdayPaygroupBatchDAO workdayPaygroupBatchDAO;

	@Resource
	DynamicFormRecordDAO dynamicFormRecordDAO;

	@Resource
	WorkdayAppCodeMasterDAO workdayAppCodeMasterDAO;

	@Resource
	WorkdayFtpFieldMappingDAO workdayFTPFieldMappingDAO;

	@Resource
	DynamicFormTableRecordDAO dynamicFormTableRecordDAO;

	@Resource
	DynamicFormDAO dynamicFormDAO;

	@Resource
	CompanyDAO companyDAO;

	@Override
	public List<WorkdayPaygroupBatchDTO> getBatchPeriod(Long companyId, Long batchYear, Long workDayReportVal,
			Long batchTypeVal) {
		List<WorkdayPaygroupBatchDTO> workdayPaygroupBatchDTOList = null;

		Date fromDate = DateUtils.stringToDate(batchYear + "-01-01", "yyyy-MM-dd");
		Date toDate = DateUtils.stringToDate(batchYear + "-12-31", "yyyy-MM-dd");

		Map<String, Object> dataRecord = new HashMap<String, Object>();
		dataRecord.put("fromDate", fromDate);
		dataRecord.put("toDate", toDate);

		WorkdayAppCodeMaster workdayReport = workdayAppCodeMasterDAO.findById(workDayReportVal);
		if (workdayReport != null) {
			switch (workdayReport.getCodeDesc()) {
			case PayAsiaConstants.WorkDay_App_Code_Name_New_Employee_Upload_AU:
				dataRecord.put("isEmpData", "E");
				break;
			case PayAsiaConstants.WorkDay_App_Code_Name_Time_Sheet_AU:
				dataRecord.put("isEmpData", "T");
				break;
			case PayAsiaConstants.WorkDay_App_Code_Name_Payroll_Preparation_AU:
				dataRecord.put("isEmpData", "ET");
				break;
			case PayAsiaConstants.WorkDay_App_Code_Name_Movement_Template_PH:
				dataRecord.put("isEmpData", "E");
				break;
			case PayAsiaConstants.WorkDay_App_Code_Name_Summrized_Hours_PH:
				dataRecord.put("isEmpData", "T");
				break;
			case PayAsiaConstants.WorkDay_App_Code_Name_New_Starter_SpreadSheet_UK:
				dataRecord.put("isEmpData", "E");
				break;
			case PayAsiaConstants.WorkDay_App_Code_Name_Payroll_Input_Template_UK:
				dataRecord.put("isEmpData", "T");
				break;
			default:
				dataRecord.put("isEmpData", "");
			}
		}
		dataRecord.put("batchTypeVal", batchTypeVal);

		List<WorkdayPaygroupBatch> workdayPaygroupBatchList = workdayPaygroupBatchDAO.findByCompanyId(companyId,
				dataRecord);
		if (workdayPaygroupBatchList != null && !workdayPaygroupBatchList.isEmpty()) {
			workdayPaygroupBatchDTOList = new ArrayList<WorkdayPaygroupBatchDTO>();
			for (WorkdayPaygroupBatch workdayPaygroupBatch : workdayPaygroupBatchList) {
				WorkdayPaygroupBatchDTO workdayPaygroupBatchDTO = new WorkdayPaygroupBatchDTO();
				String startDate = DateUtils.convertDate(
						String.valueOf(workdayPaygroupBatch.getPayPeriodStartDate()).substring(0, 10), "yyyy-MM-dd",
						"dd-MMM-yyyy");
				String endDate = DateUtils.convertDate(
						String.valueOf(workdayPaygroupBatch.getPayPeriodEndDate()).substring(0, 10), "yyyy-MM-dd",
						"dd-MMM-yyyy");
				workdayPaygroupBatchDTO.setWorkdayPaygroupBatchId(
						FormatPreserveCryptoUtil.encrypt(workdayPaygroupBatch.getWorkdayPaygroupBatchId()));
				workdayPaygroupBatchDTO.setWorkdayStartDate(startDate);
				workdayPaygroupBatchDTO.setWorkdayEndDate(endDate);
				workdayPaygroupBatchDTOList.add(workdayPaygroupBatchDTO);
			}
		} else {
			return new ArrayList<WorkdayPaygroupBatchDTO>();
		}
		return workdayPaygroupBatchDTOList;
	}

	@Override
	public List<WorkdayPaygroupBatchDTO> getBatchYear(Long companyId) {
		List<WorkdayPaygroupBatchDTO> workdayPaygroupBatchDTOList = null;
		List<WorkdayPaygroupBatch> workdayPaygroupBatchList = workdayPaygroupBatchDAO.findByCompanyId(companyId);
		if (workdayPaygroupBatchList != null && !workdayPaygroupBatchList.isEmpty()) {
			workdayPaygroupBatchDTOList = new ArrayList<WorkdayPaygroupBatchDTO>();
			Set<String> yearSet = new HashSet<String>();
			for (WorkdayPaygroupBatch workdayPaygroupBatch : workdayPaygroupBatchList) {
				yearSet.add(String.valueOf(workdayPaygroupBatch.getPayPeriodStartDate()).substring(0, 4));
			}
			Iterator<String> value = yearSet.iterator();
			while (value.hasNext()) {
				WorkdayPaygroupBatchDTO workdayPaygroupBatchDTO = new WorkdayPaygroupBatchDTO();
				workdayPaygroupBatchDTO.setYear(value.next());
				workdayPaygroupBatchDTOList.add(workdayPaygroupBatchDTO);
			}
		} else {
			return new ArrayList<WorkdayPaygroupBatchDTO>();
		}
		return workdayPaygroupBatchDTOList;
	}

	@Override
	public List<WorkdayReportMasterDTO> getWorkDayReportMaster(Long companyId) {

		Company company = companyDAO.findById(companyId);

		List<WorkdayAppCodeMaster> workdayAppCodeMasterList = workdayAppCodeMasterDAO
				.findByCountryId(company.getCountryMaster().getCountryId());
		List<WorkdayReportMasterDTO> workdayReportMasterDTOList = null;

		if (workdayAppCodeMasterList != null && !workdayAppCodeMasterList.isEmpty()) {
			workdayReportMasterDTOList = new ArrayList<WorkdayReportMasterDTO>();
			for (WorkdayAppCodeMaster workdayAppCodeMaster : workdayAppCodeMasterList) {
				WorkdayReportMasterDTO workdayReportMasterDTO = new WorkdayReportMasterDTO();
				workdayReportMasterDTO.setWorkdayReportId(
						FormatPreserveCryptoUtil.encrypt(workdayAppCodeMaster.getWorkdayAppCodeId()));
				workdayReportMasterDTO.setReportName(workdayAppCodeMaster.getCodeValue());
				workdayReportMasterDTO.setReportType(workdayAppCodeMaster.getCategory());
				workdayReportMasterDTOList.add(workdayReportMasterDTO);
			}
		} else {
			workdayReportMasterDTOList = new ArrayList<WorkdayReportMasterDTO>();
		}
		return workdayReportMasterDTOList;
	}

	@Override
	public WorkDayGenerateReportDTO generateWorkDayReport(WorkdayGenerateReportForm workdayGenerateReportForm) {

		WorkDayGenerateReportDTO workDayGenerateReportDTO = new WorkDayGenerateReportDTO();

		WorkDayExcelTemplate excelLogic = null;

		List<WorkDayReportDTO> workDayReportDTOList = null;

		WorkdayPaygroupBatch workdayPaygroupBatch = workdayPaygroupBatchDAO.findByWorkdayPaygroupBatchId(
				workdayGenerateReportForm.getBatchPeriod(), workdayGenerateReportForm.getCompanyId());

		WorkdayAppCodeMaster workdayAppCodeMaster = workdayAppCodeMasterDAO
				.findById(workdayGenerateReportForm.getWorkDayReport());

		WorkdayAppCodeMaster workdayAppCodeBatchType = workdayAppCodeMasterDAO
				.findById(Long.parseLong(workdayGenerateReportForm.getBatchType()));

		if (workdayAppCodeBatchType != null) {
			workDayGenerateReportDTO.setPayGroup(workdayAppCodeBatchType.getCodeDesc());
		}
		workDayGenerateReportDTO.setFileDatePeriod(
				DateUtils.convertDate(String.valueOf(workdayPaygroupBatch.getPayPeriodStartDate()).substring(0, 10),
						"yyyy-MM-dd", "dd-MMM-yyyy")
						+ " to "
						+ DateUtils.convertDate(
								String.valueOf(workdayPaygroupBatch.getPayPeriodEndDate()).substring(0, 10),
								"yyyy-MM-dd", "dd-MMM-yyyy"));
		workDayGenerateReportDTO.setEntiryName(workdayPaygroupBatch.getCompany().getCompanyCode());
		if (workdayAppCodeMaster != null) {
			switch (workdayAppCodeMaster.getCodeDesc()) {
			case PayAsiaConstants.WorkDay_App_Code_Name_New_Employee_Upload_AU:
				workDayGenerateReportDTO.setFileName("New_Employee_Upload");
				excelLogic = WorkDayExcelTemplateFactory.instantiate("WorkDayExcelTemplateNewUploadAU");
				workDayReportDTOList = getWorkDayReportDTONewUploadAU(excelLogic, workdayGenerateReportForm);
				break;
			case PayAsiaConstants.WorkDay_App_Code_Name_Time_Sheet_AU:
				workDayGenerateReportDTO.setFileName("Time_Sheet");
				excelLogic = WorkDayExcelTemplateFactory.instantiate("WorkDayExcelTemplateTimeSheetAU");
				workDayReportDTOList = getWorkDayReportDTOTimeSheetAU(excelLogic, workdayGenerateReportForm);
				break;
			case PayAsiaConstants.WorkDay_App_Code_Name_Payroll_Preparation_AU:
				workDayGenerateReportDTO.setFileName("Payroll_Preparation");
				excelLogic = WorkDayExcelTemplateFactory.instantiate("WorkDayExcelTemplatePayrollPreparationAU");
				workDayReportDTOList = getWorkDayReportDTOPayrollPreparationAU(excelLogic, workdayGenerateReportForm);
				break;
			case PayAsiaConstants.WorkDay_App_Code_Name_Movement_Template_PH:
				workDayGenerateReportDTO.setFileName("Movement_Template");
				excelLogic = WorkDayExcelTemplateFactory.instantiate("WorkDayExcelTemplateMovementPH");
				workDayReportDTOList = getWorkDayReportDTOMovementPH(excelLogic, workdayGenerateReportForm);
				break;
			case PayAsiaConstants.WorkDay_App_Code_Name_Summrized_Hours_PH:
				workDayGenerateReportDTO.setFileName("Summrized_Hours");
				excelLogic = WorkDayExcelTemplateFactory.instantiate("WorkDayExcelTemplateSummrizedHoursPH");
				workDayReportDTOList = getWorkDayReportDTOSummrizedHoursPH(excelLogic, workdayGenerateReportForm);
				break;
			case PayAsiaConstants.WorkDay_App_Code_Name_New_Starter_SpreadSheet_UK:
				workDayGenerateReportDTO.setFileName("New_Starter_SpreadSheet");
				excelLogic = WorkDayExcelTemplateFactory.instantiate("WorkDayExcelTemplateNewStarterUK");
				workDayReportDTOList = getWorkDayReportDTONewStarterUK(excelLogic, workdayGenerateReportForm);
				break;
			case PayAsiaConstants.WorkDay_App_Code_Name_Payroll_Input_Template_UK:
				workDayGenerateReportDTO.setFileName("Payroll_Input_Template");
				excelLogic = WorkDayExcelTemplateFactory.instantiate("WorkDayExcelTemplateSummrizedHoursPH");
				workDayReportDTOList = getWorkDayReportDTOSummrizedHoursPH(excelLogic, workdayGenerateReportForm);
				break;
			}
		}

		XSSFWorkbook workbook = excelLogic.generateReport(workDayReportDTOList);

		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			workbook.write(out);
			workDayGenerateReportDTO.setWorkBookByteArr(out.toByteArray());
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return workDayGenerateReportDTO;
	}

	private List<WorkDayReportDTO> getWorkDayReportDTONewUploadAU(WorkDayExcelTemplate excelLogic,
			WorkdayGenerateReportForm workdayGererateReportDTO) {

		List<WorkDayReportDTO> workDayReportDTOList = null;
		workDayReportDTOList = new ArrayList<WorkDayReportDTO>();
		List<WorkdayPaygroupBatchData> workDayPaygroupBatchDataList = workdayPaygroupBatchDataDAO
				.findEmployeeWorkdayPaygroupBatchData(workdayGererateReportDTO.getCompanyId(),
						workdayGererateReportDTO.getBatchPeriod());

		JAXBElement employeeType = null;
		if (workDayPaygroupBatchDataList != null && !workDayPaygroupBatchDataList.isEmpty()) {
			for (WorkdayPaygroupBatchData workdayPaygroupBatch : workDayPaygroupBatchDataList) {
				if (workdayPaygroupBatch.isNewEmployee()) {
					employeeType = WorkdayEmployeeDataXMLUtil
							.convertXmlStringToEmployee(workdayPaygroupBatch.getEmployeeXML().replaceAll("pi:", ""));
					List<WorkDayReportDTO> workDayReportDTO = excelLogic.getReportDataMappedObject(employeeType,
							workdayPaygroupBatch);
					if (workDayReportDTO != null && !workDayReportDTO.isEmpty()) {
						for (WorkDayReportDTO WorkDayReport : workDayReportDTO) {
							workDayReportDTOList.add(WorkDayReport);
						}
					}
				}
			}
		}
		return workDayReportDTOList;
	}

	private List<WorkDayReportDTO> getWorkDayReportDTOTimeSheetAU(WorkDayExcelTemplate excelLogic,
			WorkdayGenerateReportForm workdayGererateReportDTO) {
		List<WorkDayReportDTO> workDayReportDTOList = null;
		workDayReportDTOList = new ArrayList<WorkDayReportDTO>();
		List<WorkdayPaygroupBatchData> workDayPaygroupBatchDataList = workdayPaygroupBatchDataDAO
				.findEmployeeWorkdayPaygroupBatchData(workdayGererateReportDTO.getCompanyId(),
						workdayGererateReportDTO.getBatchPeriod());
		JAXBElement employeeType = null;
		if (workDayPaygroupBatchDataList != null && !workDayPaygroupBatchDataList.isEmpty()) {
			for (WorkdayPaygroupBatchData workdayPaygroupBatch : workDayPaygroupBatchDataList) {
				employeeType = WorkdayPayrollDataXMLUtil
						.convertXmlStringToEmployee(workdayPaygroupBatch.getEmployeeXML().replaceAll("pi:", ""));

				List<WorkDayReportDTO> workDayReportDTO = excelLogic.getReportDataMappedObject(employeeType,
						workdayPaygroupBatch);
				if (workDayReportDTO != null && !workDayReportDTO.isEmpty()) {
					for (WorkDayReportDTO workDayReport : workDayReportDTO) {
						getHROFieldDataTimeSheet(workdayPaygroupBatch, workDayReport);
						workDayReportDTOList.add(workDayReport);
					}
				}
			}
		}
		return workDayReportDTOList;
	}

	private List<WorkDayReportDTO> getWorkDayReportDTOPayrollPreparationAU(WorkDayExcelTemplate excelLogic,
			WorkdayGenerateReportForm workdayGenerateReportForm) {
		List<WorkDayReportDTO> workDayReportDTOList = null;
		workDayReportDTOList = new ArrayList<WorkDayReportDTO>();
		List<WorkdayPaygroupBatchData> workDayPaygroupBatchDataList = workdayPaygroupBatchDataDAO
				.findEmployeeWorkdayPaygroupBatchData(workdayGenerateReportForm.getCompanyId(),
						workdayGenerateReportForm.getBatchPeriod());

		JAXBElement employeeType = null;
		if (workDayPaygroupBatchDataList != null && !workDayPaygroupBatchDataList.isEmpty()) {
			for (WorkdayPaygroupBatchData workdayPaygroupBatch : workDayPaygroupBatchDataList) {
				if (!workdayPaygroupBatch.isNewEmployee()) {
					employeeType = WorkdayEmployeeDataXMLUtil
							.convertXmlStringToEmployee(workdayPaygroupBatch.getEmployeeXML().replaceAll("pi:", ""));
					List<WorkDayReportDTO> workDayReportDTO = excelLogic.getReportDataMappedObject(employeeType,
							workdayPaygroupBatch);
					if (workDayReportDTO != null && !workDayReportDTO.isEmpty()) {
						for (WorkDayReportDTO workDayReport : workDayReportDTO) {
							workDayReportDTOList.add(workDayReport);
						}
					}
					WorkdayPaygroupBatchData workdayPaygroupBatchPrtx = workdayPaygroupBatchDataDAO
							.findEmployeePayrollBatchData(workdayGenerateReportForm.getCompanyId(),
									workdayPaygroupBatch);
					if (workdayPaygroupBatchPrtx != null) {
						employeeType = WorkdayPayrollDataXMLUtil.convertXmlStringToEmployee(
								workdayPaygroupBatchPrtx.getEmployeeXML().replaceAll("pi:", ""));
						List<WorkDayReportDTO> workDayReportDTOPayRoll = excelLogic
								.getReportDataMappedObject(employeeType, workdayPaygroupBatch);
						if (workDayReportDTOPayRoll != null && !workDayReportDTOPayRoll.isEmpty()) {
							for (WorkDayReportDTO workDayReport : workDayReportDTOPayRoll) {
								workDayReportDTOList.add(workDayReport);
							}
						}
					}
				}
			}
		}
		return workDayReportDTOList;
	}

	private List<WorkDayReportDTO> getWorkDayReportDTOMovementPH(WorkDayExcelTemplate excelLogic,
			WorkdayGenerateReportForm workdayGererateReportDTO) {

		List<WorkDayReportDTO> workDayReportDTOList = null;

		workDayReportDTOList = new ArrayList<WorkDayReportDTO>();

		List<WorkdayPaygroupBatchData> workDayPaygroupBatchDataList = workdayPaygroupBatchDataDAO
				.findEmployeeWorkdayPaygroupBatchData(workdayGererateReportDTO.getCompanyId(),
						workdayGererateReportDTO.getBatchPeriod());

		JAXBElement employeeType = null;
		if (workDayPaygroupBatchDataList != null && !workDayPaygroupBatchDataList.isEmpty()) {
			for (WorkdayPaygroupBatchData workdayPaygroupBatch : workDayPaygroupBatchDataList) {

				employeeType = WorkdayEmployeeDataXMLUtil
						.convertXmlStringToEmployee(workdayPaygroupBatch.getEmployeeXML().replaceAll("pi:", ""));
				List<WorkDayReportDTO> workDayReportDTO = excelLogic.getReportDataMappedObject(employeeType,
						workdayPaygroupBatch);
				if (workDayReportDTO != null && !workDayReportDTO.isEmpty()) {
					for (WorkDayReportDTO WorkDayReport : workDayReportDTO) {
						workDayReportDTOList.add(WorkDayReport);
					}
				}
			}
		}
		return workDayReportDTOList;
	}

	private List<WorkDayReportDTO> getWorkDayReportDTOSummrizedHoursPH(WorkDayExcelTemplate excelLogic,
			WorkdayGenerateReportForm workdayGererateReportDTO) {
		List<WorkDayReportDTO> workDayReportDTOList = null;

		workDayReportDTOList = new ArrayList<WorkDayReportDTO>();

		List<WorkdayPaygroupBatchData> workDayPaygroupBatchDataList = workdayPaygroupBatchDataDAO
				.findEmployeeWorkdayPaygroupBatchData(workdayGererateReportDTO.getCompanyId(),
						workdayGererateReportDTO.getBatchPeriod());

		JAXBElement employeeType = null;
		if (workDayPaygroupBatchDataList != null && !workDayPaygroupBatchDataList.isEmpty()) {
			for (WorkdayPaygroupBatchData workdayPaygroupBatch : workDayPaygroupBatchDataList) {

				employeeType = WorkdayPayrollDataXMLUtil
						.convertXmlStringToEmployee(workdayPaygroupBatch.getEmployeeXML().replaceAll("pi:", ""));
				List<WorkDayReportDTO> workDayReportDTO = excelLogic.getReportDataMappedObject(employeeType,
						workdayPaygroupBatch);
				if (workDayReportDTO != null && !workDayReportDTO.isEmpty()) {
					for (WorkDayReportDTO WorkDayReport : workDayReportDTO) {
						workDayReportDTOList.add(WorkDayReport);
					}
				}
			}
		}
		return workDayReportDTOList;
	}

	private List<WorkDayReportDTO> getWorkDayReportDTONewStarterUK(WorkDayExcelTemplate excelLogic,
			WorkdayGenerateReportForm workdayGererateReportDTO) {
		List<WorkDayReportDTO> workDayReportDTOList = null;

		workDayReportDTOList = new ArrayList<WorkDayReportDTO>();

		List<WorkdayPaygroupBatchData> workDayPaygroupBatchDataList = workdayPaygroupBatchDataDAO
				.findEmployeeWorkdayPaygroupBatchData(workdayGererateReportDTO.getCompanyId(),
						workdayGererateReportDTO.getBatchPeriod());

		JAXBElement employeeType = null;
		if (workDayPaygroupBatchDataList != null && !workDayPaygroupBatchDataList.isEmpty()) {
			for (WorkdayPaygroupBatchData workdayPaygroupBatch : workDayPaygroupBatchDataList) {
				if (workdayPaygroupBatch.isNewEmployee()) {
					employeeType = WorkdayEmployeeDataXMLUtil
							.convertXmlStringToEmployee(workdayPaygroupBatch.getEmployeeXML().replaceAll("pi:", ""));
					List<WorkDayReportDTO> workDayReportDTO = excelLogic.getReportDataMappedObject(employeeType,
							workdayPaygroupBatch);
					if (workDayReportDTO != null && !workDayReportDTO.isEmpty()) {
						for (WorkDayReportDTO workDayReport : workDayReportDTO) {
							workDayReportDTOList.add(workDayReport);
						}
					}
				}
			}
		}
		return workDayReportDTOList;
	}

	private void getHROFieldDataTimeSheet(WorkdayPaygroupBatchData workdayPaygroupBatch,
			WorkDayReportDTO workDayReport) {

		WorkdayTimeSheetDTO workdayTimeSheetDTO = (WorkdayTimeSheetDTO) workDayReport;

		Long empId = workdayPaygroupBatch.getEmployee().getEmployeeId();
		Date startDate = workdayPaygroupBatch.getWorkdayPaygroupBatch().getPayPeriodStartDate();
		Long dataDicId = null;
		Long formId = null;
		Long companyId = workdayPaygroupBatch.getCompany().getCompanyId();

		workdayTimeSheetDTO.setExternalID("");

		WorkdayFieldMaster workdayFieldMaster = workdayFieldMasterDAO
				.findBySectionAndFieldName("Salary_and_Hourly_Plans", "Amount");

		if (workdayFieldMaster != null) {
			WorkdayFtpFieldMapping workdayFtpFieldMapping = workdayFTPFieldMappingDAO.findByCompanyIdAndWorkdayFieldId(
					workdayPaygroupBatch.getCompany().getCompanyId(), workdayFieldMaster.getWorkdayFieldId());
			if (workdayFtpFieldMapping != null) {
				dataDicId = workdayFtpFieldMapping.getHroField().getDataDictionaryId();
				formId = workdayFtpFieldMapping.getHroField().getFormID();
				workdayTimeSheetDTO
						.setRate(getEffectiveDateDataFromHRO(empId, startDate, dataDicId, formId, companyId));
			}
		}
	}

	private String getEffectiveDateDataFromHRO(Long empId, Date startDate, Long dataDicId, Long formId,
			Long companyId) {

		int maxVersion = dynamicFormDAO.getMaxVersionByFormId(companyId, 1, formId);
		DynamicForm dynamicForm = dynamicFormDAO.findByMaxVersionByFormId(companyId, 1, maxVersion, formId);

		Unmarshaller unmarshaller = null;

		try {
			unmarshaller = XMLUtil.getDocumentUnmarshaller();
		} catch (JAXBException jaxbException) {
			LOGGER.error(jaxbException.getMessage(), jaxbException);
			throw new PayAsiaSystemException(jaxbException.getMessage(), jaxbException);
		} catch (SAXException saxException) {
			LOGGER.error(saxException.getMessage(), saxException);
			throw new PayAsiaSystemException(saxException.getMessage(), saxException);
		}
		Tab tab = null;
		final StringReader xmlReader = new StringReader(dynamicForm.getMetaData());
		final StreamSource xmlSource = new StreamSource(xmlReader);

		try {
			tab = (Tab) unmarshaller.unmarshal(xmlSource);
		} catch (JAXBException jaxbException) {
			LOGGER.error(jaxbException.getMessage(), jaxbException);
			throw new PayAsiaSystemException(jaxbException.getMessage(), jaxbException);
		}
		DynamicFormRecord dynamicFormRecord = null;

		if (dynamicForm != null) {
			dynamicFormRecord = dynamicFormRecordDAO.getEmpRecords(empId, dynamicForm.getId().getVersion(),
					dynamicForm.getId().getFormId(), dynamicForm.getEntityMaster().getEntityId(), companyId);
		}
		
		if (dynamicFormRecord != null) {
			List<Field> listOfFields = tab.getField();
			String colValue = null;
			String rateColNumber = null;
			String effDateColNumber = null;
			for (Field field : listOfFields) {
				if (StringUtils.equalsIgnoreCase(field.getType(), PayAsiaConstants.TABLE_FIELD_TYPE)) {
					List<Column> columnList = field.getColumn();
					boolean correctData = false;
					for (Column column : columnList) {
						if (column.getDictionaryId().equals(dataDicId)) {
							correctData = true;
							rateColNumber = PayAsiaStringUtils.getColNumber(column.getName());
							break;
						}
					}
					if (correctData) {
						for (Column column : columnList) {
							if (column.getLabel().equals("RWZmZWN0aXZlIERhdGU=")) {
								effDateColNumber = PayAsiaStringUtils.getColNumber(column.getName());
								break;
							}
						}
						colValue = getColValueFile(PayAsiaStringUtils.getColNumber(field.getName()), dynamicFormRecord);
					}
				}
			}
			if (colValue != null && !colValue.isEmpty()) {
				List<DynamicFormTableRecord> dynamicFormTableRecords = dynamicFormTableRecordDAO
						.getTableRecords(Long.parseLong(colValue), PayAsiaConstants.SORT_ORDER_OLDEST_TO_NEWEST, "");
				List<Date> dateList = new ArrayList<Date>();

				dateList.add(startDate);
				Map<Date, String> dataMap = new HashMap<Date, String>();
				boolean equalsDate = false;
				for (DynamicFormTableRecord dynamicFormTableRecord : dynamicFormTableRecords) {

					Class<?> dynamicFormTableRecordClass = dynamicFormTableRecord.getClass();

					String effDateCol = "getCol" + effDateColNumber;
					String rateCol = "getCol" + rateColNumber;

					Method dynamicFormRecordMethod;
					try {
						dynamicFormRecordMethod = dynamicFormTableRecordClass.getMethod(effDateCol);
						String effDate = (String) dynamicFormRecordMethod.invoke(dynamicFormTableRecord);
						Date parseDate = DateUtils.stringToDate(effDate.substring(0, 10), "yyyy/MM/dd");
						if (dateList.contains(parseDate)) {
							equalsDate = true;
						} else {
							dateList.add(parseDate);
						}
						dynamicFormRecordMethod = dynamicFormTableRecordClass.getMethod(rateCol);
						String rate = (String) dynamicFormRecordMethod.invoke(dynamicFormTableRecord);
						dataMap.put(parseDate, rate);
					} catch (SecurityException | NoSuchMethodException | IllegalArgumentException
							| IllegalAccessException | InvocationTargetException e) {
						LOGGER.error(e.getMessage(), e);
						throw new PayAsiaSystemException(e.getMessage(), e);
					}
				}

				Collections.sort(dateList);
				int count = 0;
				Date lastDate = null;
				if (equalsDate) {
					return dataMap.get(startDate);
				} else {
					for (Date date : dateList) {
						if (date.equals(startDate) && count != dateList.size()) {
							break;
						} else if (date.equals(startDate) && count == dateList.size()) {

						} else {
							lastDate = date;
						}
						count++;
					}
				}
				if (lastDate != null) {
					return dataMap.get(lastDate);
				}
			}
		}
		return "0";
	}

	private String getColValueFile(String colNumber, DynamicFormRecord existingFormRecord) {

		String tableRecordId = null;
		Class<?> dynamicFormRecordClass = existingFormRecord.getClass();
		String colMehtodName = "getCol" + colNumber;
		Method dynamicFormRecordMethod;
		try {
			dynamicFormRecordMethod = dynamicFormRecordClass.getMethod(colMehtodName);
			tableRecordId = (String) dynamicFormRecordMethod.invoke(existingFormRecord);
		} catch (SecurityException | NoSuchMethodException | IllegalArgumentException | IllegalAccessException
				| InvocationTargetException e) {
			LOGGER.error(e.getMessage(), e);
			throw new PayAsiaSystemException(e.getMessage(), e);
		}

		return tableRecordId;

	}
}
