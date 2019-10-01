package com.payasia.logic.impl;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Image;
import com.mind.payasia.xml.bean.Column;
import com.mind.payasia.xml.bean.Field;
import com.mind.payasia.xml.bean.Tab;
import com.payasia.common.dto.ColumnPropertyDTO;
import com.payasia.common.dto.DataImportKeyValueDTO;
import com.payasia.common.dto.FilePathGeneratorDTO;
import com.payasia.common.util.FileUtils;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.PayAsiaStringUtils;
import com.payasia.dao.CompanyLogoDAO;
import com.payasia.dao.DataDictionaryDAO;
import com.payasia.dao.DynamicFormDAO;
import com.payasia.dao.DynamicFormFieldRefValueDAO;
import com.payasia.dao.DynamicFormRecordDAO;
import com.payasia.dao.DynamicFormTableRecordDAO;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.EntityMasterDAO;
import com.payasia.dao.FinancialYearMasterDAO;
import com.payasia.dao.GeneralDAO;
import com.payasia.dao.PayslipDAO;
import com.payasia.dao.bean.CompanyLogo;
import com.payasia.dao.bean.DataDictionary;
import com.payasia.dao.bean.DynamicForm;
import com.payasia.dao.bean.DynamicFormFieldRefValue;
import com.payasia.dao.bean.DynamicFormRecord;
import com.payasia.dao.bean.DynamicFormTableRecord;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.FinancialYearMaster;
import com.payasia.dao.bean.Payslip;
import com.payasia.logic.AWSS3Logic;
import com.payasia.logic.DataExportUtils;
import com.payasia.logic.EmployeeDetailLogic;
import com.payasia.logic.PayslipDataUtils;

@Component
public class PayslipDataUtilsImpl implements PayslipDataUtils {
	private static final Logger LOGGER = Logger
			.getLogger(PayslipDataUtilsImpl.class);
	@Resource
	DataDictionaryDAO dataDictionaryDAO;

	@Resource
	PayslipDAO payslipDAO;

	@Resource
	GeneralDAO generalDAO;

	@Resource
	EntityMasterDAO entityMasterDAO;

	@Resource
	DynamicFormDAO dynamicFormDAO;

	@Resource
	DataExportUtils dataExportUtils;

	@Resource
	EmployeeDAO employeeDAO;

	@Resource
	DynamicFormRecordDAO dynamicFormRecordDAO;

	@Resource
	DynamicFormTableRecordDAO dynamicFormTableRecordDAO;

	@Resource
	CompanyLogoDAO companyLogoDAO;

	@Resource
	DynamicFormFieldRefValueDAO dynamicFormFieldRefValueDAO;

	@Resource
	FinancialYearMasterDAO financialYearMasterDAO;

	@Resource
	EmployeeDetailLogic employeeDetailLogic;

	@Resource
	FileUtils fileUtils;

	@Value("#{payasiaptProperties['payasia.home']}")
	private String downloadPath;

	/** The documents Root Directory Name. */
	@Value("#{payasiaptProperties['payasia.document.root.directory']}")
	private String rootDirectoryName;

	@Resource
	AWSS3Logic awss3LogicImpl;

	@Value("#{payasiaptProperties['payasia.application.deployment.location']}")
	private String appDeployLocation;

	@Override
	public Image getLogoImage(Payslip payslip) throws BadElementException,
			MalformedURLException, IOException {
		Image img;
		CompanyLogo companyLogoData = companyLogoDAO
				.findByConditionCompany(payslip.getCompany().getCompanyId());
		if (companyLogoData != null) {
			/*
			 * String filePath = "company/" +
			 * companyLogoData.getCompany().getCompanyId() + "/" +
			 * PayAsiaConstants.COMPANY_LOGO_DIRECTORY_NAME + "/" +
			 * companyLogoData.getCompanyLogoId();
			 */

			FilePathGeneratorDTO filePathGenerator = fileUtils
					.getFileCommonPath(downloadPath, rootDirectoryName,
							companyLogoData.getCompany().getCompanyId(),
							PayAsiaConstants.COMPANY_LOGO_DIRECTORY_NAME,
							String.valueOf(companyLogoData.getCompanyLogoId()),
							null, null, null,
							PayAsiaConstants.PAYASIA_DOC_READALL_EVENT, 0);
			String filePath = fileUtils.getGeneratedFilePath(filePathGenerator);

			File file = new File(filePath);

			byte[] byteFile;
			if (PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION
					.equalsIgnoreCase(appDeployLocation)) {
				byteFile = IOUtils.toByteArray(awss3LogicImpl
						.readS3ObjectAsStream(filePath));
			} else {
				byteFile = Files.readAllBytes(file.toPath());
			}

			img = Image.getInstance(byteFile);
			img.scaleToFit(102, 50);
			img.setRotationDegrees(0);
		} else {
			URL imageURL = Thread.currentThread().getContextClassLoader()
					.getResource("images/logo.png");

			img = Image.getInstance(imageURL);
			img.scaleToFit(102, 50);
			img.setRotationDegrees(0);
		}

		return img;
	}

	@Override
	public void getEmployeeFieldValue(Payslip payslip, Field field,
			HashMap<Long, Tab> dataTabMap,
			HashMap<Long, DynamicForm> dynamicFormMap,
			HashMap<String, ColumnPropertyDTO> staticPropMap,
			List<DynamicFormRecord> dynamicFormRecordList)
			throws NoSuchMethodException, IllegalAccessException,
			InvocationTargetException {
		Map<Long, DataImportKeyValueDTO> colMap = new LinkedHashMap<Long, DataImportKeyValueDTO>();

		if (!StringUtils.equalsIgnoreCase(field.getType(), "table")
				&& !StringUtils.equalsIgnoreCase(field.getType(), "label")
				&& !StringUtils.equalsIgnoreCase(field.getType(),
						PayAsiaConstants.FIELD_TYPE_LOGO)) {
			DataDictionary dataDictionary = dataDictionaryDAO.findById(field
					.getDictionaryId());
			if (dataDictionary == null) {
				return;

			}

			// Set value entityId in Referenced variable as temporarily
			field.setReferenced(dataDictionary.getEntityMaster().getEntityId());

			if (dataDictionary.getFieldType().equalsIgnoreCase(
					PayAsiaConstants.STATIC_TYPE)) {
				setStaticDictionary(colMap, dataDictionary, staticPropMap);
			} else {
				setDynamicDictionary(colMap, dataDictionary, payslip,
						dataTabMap, dynamicFormMap);
			}

			Set<Long> xlKeySet = colMap.keySet();
			for (Iterator<Long> itr = xlKeySet.iterator(); itr.hasNext();) {
				Long key = itr.next();
				DataImportKeyValueDTO valueDTO = colMap.get(key);

				if (valueDTO.isStatic()) {
					field.setDefault(true);
					if (valueDTO.getMethodName().equalsIgnoreCase(
							"EmployeeNumber")) {

						String value = payslip.getEmployee()
								.getEmployeeNumber();
						field.setValue(value);
					} else if (valueDTO.getMethodName().equalsIgnoreCase(
							"FirstName")) {

						String value = payslip.getEmployee().getFirstName();
						field.setValue(value);
					} else if (valueDTO.getMethodName().equalsIgnoreCase(
							"MiddleName")) {

						String value = payslip.getEmployee().getMiddleName();
						field.setValue(value);
					} else if (valueDTO.getMethodName().equalsIgnoreCase(
							"LastName")) {

						String value = payslip.getEmployee().getLastName();
						field.setValue(value);
					} else if (valueDTO.getMethodName().equalsIgnoreCase(
							"HireDate")) {
						Timestamp value = payslip.getEmployee().getHireDate();
						if (value != null) {
							Date date = new Date(value.getTime());
							SimpleDateFormat dateFormat = new SimpleDateFormat(
									payslip.getCompany().getDateFormat());
							field.setValue(dateFormat.format(date));
						} else {
							field.setValue("");
						}

					} else if (valueDTO.getMethodName().equalsIgnoreCase(
							"OriginalHireDate")) {
						Timestamp value = payslip.getEmployee()
								.getOriginalHireDate();
						if (value != null) {
							Date date = new Date(value.getTime());
							SimpleDateFormat dateFormat = new SimpleDateFormat(
									payslip.getCompany().getDateFormat());
							field.setValue(dateFormat.format(date));
						} else {
							field.setValue("");
						}
					} else if (valueDTO.getMethodName().equalsIgnoreCase(
							"ResignationDate")) {
						Timestamp value = payslip.getEmployee()
								.getResignationDate();
						if (value != null) {
							Date date = new Date(value.getTime());
							SimpleDateFormat dateFormat = new SimpleDateFormat(
									payslip.getCompany().getDateFormat());
							field.setValue(dateFormat.format(date));
						} else {
							field.setValue("");
						}
					} else if (valueDTO.getMethodName().equalsIgnoreCase(
							"ConfirmationDate")) {
						Timestamp value = payslip.getEmployee()
								.getConfirmationDate();
						if (value != null) {
							Date date = new Date(value.getTime());
							SimpleDateFormat dateFormat = new SimpleDateFormat(
									payslip.getCompany().getDateFormat());
							field.setValue(dateFormat.format(date));
						} else {
							field.setValue("");
						}
					} else if (valueDTO.getMethodName().equalsIgnoreCase(
							"Email")) {

						String value = payslip.getEmployee().getEmail();
						field.setValue(value);
					} else if (valueDTO.getMethodName().equalsIgnoreCase(
							"Status")) {

						boolean value = payslip.getEmployee().isStatus();
						field.setValue(String.valueOf(value));
					} else if (valueDTO.getMethodName().equalsIgnoreCase(
							"EmploymentStatus")) {

						String value = payslip.getEmployee()
								.getEmploymentStatus();
						field.setValue(value);
					} else if (valueDTO.getMethodName().equalsIgnoreCase(
							"MonthName")) {
						String value = payslip.getMonthMaster().getMonthName();
						field.setValue(value);
					} else if (valueDTO.getMethodName().equalsIgnoreCase(
							"Frequency")) {
						String value = payslip.getPayslipFrequency()
								.getFrequency();
						field.setValue(value);

					} else if (valueDTO.getMethodName().equalsIgnoreCase(
							"CompanyName")) {
						String value = payslip.getCompany().getCompanyName();
						field.setValue(value);

					} else if (valueDTO.getMethodName().equalsIgnoreCase(
							"CompanyCode")) {
						String value = payslip.getCompany().getCompanyCode();
						field.setValue(value);

					} else if (valueDTO.getMethodName().equalsIgnoreCase(
							"GroupName")) {
						String value = payslip.getCompany().getCompanyGroup()
								.getGroupName();
						field.setValue(value);

					} else if (valueDTO.getMethodName().equalsIgnoreCase(
							"DateFormat")) {
						String value = payslip.getCompany().getDateFormat();
						field.setValue(value);

					} else if (valueDTO.getMethodName().equalsIgnoreCase(
							"Frequency")) {
						String value = payslip.getPayslipFrequency()
								.getFrequency();
						field.setValue(value);

					} else if (valueDTO.getMethodName().equalsIgnoreCase(
							"FinancialYear")) {
						String value = payslip.getCompany()
								.getFinancialYearMaster().getMonthMaster1()
								+ ""
								+ payslip.getCompany().getFinancialYearMaster()
										.getMonthMaster2();
						field.setValue(value);

					} else if (valueDTO.getMethodName().equalsIgnoreCase(
							"CountryName")) {
						String value = payslip.getCompany().getCountryMaster()
								.getCountryName();
						field.setValue(value);

					} else if (valueDTO.getMethodName().equalsIgnoreCase(
							"GroupCode")) {
						String value = payslip.getCompany().getCompanyGroup()
								.getGroupCode();
						field.setValue(value);

					} else {

						String methodName = "get" + valueDTO.getMethodName();
						Method getCompanyMethod;
						Class<?> payslipClass = payslip.getClass();

						getCompanyMethod = payslipClass.getMethod(methodName);

						if ("int".equalsIgnoreCase(valueDTO.getFieldType())) {
							Integer value = (Integer) getCompanyMethod
									.invoke(payslip);
							field.setValue(String.valueOf(value));
						} else {
							String value = (String) getCompanyMethod
									.invoke(payslip);
							field.setValue(value);
						}
					}
				} else {

					field.setDefault(false);

					boolean noRecordFound = true;
					if (dynamicFormRecordList != null) {
						if (valueDTO.isFormula() && !valueDTO.isChild()) {
							if (valueDTO.getFormula().substring(0, 1)
									.equalsIgnoreCase("Y")) {

								String ytmFormula = valueDTO.getFormula();
								Long ytmFieldId = Long.parseLong(ytmFormula
										.substring(ytmFormula.indexOf('{') + 1,
												ytmFormula.indexOf('}')));

								DataDictionary ytmDictionary = dataDictionaryDAO
										.findById(ytmFieldId);
								if (ytmDictionary == null) {
									continue;
								}

								FinancialYearMaster financialYearMaster = financialYearMasterDAO
										.findByCompany(payslip.getCompany()
												.getCompanyId());

								long startMonthId = financialYearMaster
										.getMonthMaster1().getMonthId();
								long currentMonth = payslip.getMonthMaster()
										.getMonthId();
								Map<Long, DataImportKeyValueDTO> formulaMap = new LinkedHashMap<Long, DataImportKeyValueDTO>();
								if (ytmDictionary.getFieldType()
										.equalsIgnoreCase(
												PayAsiaConstants.STATIC_TYPE)) {
									field.setValue("--NA--");
								} else {
									setDynamicDictionary(formulaMap,
											ytmDictionary, payslip, dataTabMap,
											dynamicFormMap);

									Double ytmVal;
									if (currentMonth >= startMonthId) {
										ytmVal = payslipDAO
												.getYTMValueGT(
														currentMonth,
														startMonthId,
														payslip.getYear(),
														payslip.getEmployee()
																.getEmployeeId(),
														payslip.getCompany()
																.getCompanyId(),
														payslip.getPayslipFrequency()
																.getPayslipFrequencyID(),
														payslip.getPart(),

														formulaMap);
									} else {
										ytmVal = payslipDAO
												.getYTMValueLT(
														currentMonth,
														startMonthId,
														payslip.getYear(),
														payslip.getEmployee()
																.getEmployeeId(),
														payslip.getCompany()
																.getCompanyId(),
														payslip.getPayslipFrequency()
																.getPayslipFrequencyID(),
														payslip.getPart(),
														formulaMap);
									}
									if (ytmVal != null) {
										noRecordFound = false;
										field.setValue(ytmVal.toString());
									}

								}

							} else {
								List<Long> dataDictionaries = new ArrayList<Long>();
								String formulaEx = valueDTO.getFormula();

								while (!"".equals(formulaEx)) {
									if (formulaEx.indexOf('{') != -1) {
										dataDictionaries
												.add(Long.parseLong(formulaEx.substring(
														formulaEx.indexOf('{') + 1,
														formulaEx.indexOf('}'))));
										formulaEx = formulaEx
												.substring(formulaEx
														.indexOf('}') + 1);
									} else {
										break;
									}

								}
								List<String> valueList = null;
								if (valueDTO.isEmployeeEntity()) {
									valueList = getEmployeeFormulaValueForDictionary(
											payslip, dataDictionary,
											dataDictionaries, dataTabMap,
											dynamicFormMap, staticPropMap,
											null, 0);
								} else {
									valueList = getValueForDictionary(payslip,
											dataDictionary, dataDictionaries,
											dataTabMap, dynamicFormMap,
											staticPropMap);
								}

								String expressionStr = valueDTO.getFormula();
								Pattern pattern = Pattern.compile("[{]\\d*[}]");
								Matcher matcher = pattern.matcher(valueDTO
										.getFormula());

								int count = 0;
								while (matcher.find()) {
									String valueReturned = valueList.get(count);
									if ("".equalsIgnoreCase(valueReturned)) {
										valueReturned = "0";
									}
									expressionStr = expressionStr.replaceAll(
											PayAsiaStringUtils
													.convertToRegEx(matcher
															.group()),
											valueReturned);
									count++;

								}

								ScriptEngineManager mgr = new ScriptEngineManager();
								ScriptEngine engine = mgr
										.getEngineByName("JavaScript");
								try {
									noRecordFound = false;
									field.setValue(engine.eval(expressionStr)
											.toString());

								} catch (Exception e) {
									LOGGER.error(e.getMessage(), e);
									field.setValue("--NA--");
								}
							}

						} else {
							for (DynamicFormRecord dynamicFormRecord : dynamicFormRecordList) {
								if (valueDTO.getFormId() == dynamicFormRecord
										.getForm_ID()) {
									noRecordFound = false;
									if (!valueDTO.isChild()) {
										if (valueDTO.isFormula()) {
										} else {
											String value = getColValueFile(
													valueDTO.getMethodName(),
													dynamicFormRecord);
											if (value != null) {

												SimpleDateFormat sdf = new SimpleDateFormat(
														PayAsiaConstants.SERVER_SIDE_DATE_FORMAT);
												SimpleDateFormat companySDF = new SimpleDateFormat(
														payslip.getCompany()
																.getDateFormat());
												try {
													Date date = sdf
															.parse(value);
													field.setValue(companySDF
															.format(date));

												} catch (ParseException e) {
													// LOGGER.error(
													// e.getMessage(), e);
													field.setValue(value);
												}

												if (valueDTO
														.getFieldType()
														.equalsIgnoreCase(
																PayAsiaConstants.CODEDESC_FIELD_TYPE)) {
													value = getCodeDescVal(
															field, value);
													field.setValue(value);
												}

												if (valueDTO
														.getFieldType()
														.equalsIgnoreCase(
																PayAsiaConstants.EMPLOYEE_lIST_REFERENCE)) {
													Employee employee = employeeDAO
															.findById(Long
																	.parseLong(value));

													field.setValue(employeeDetailLogic
															.getEmployeeName(employee));
												}

											} else {
												field.setValue("");
											}
										}

									} else {

										String value = getColValueFile(
												valueDTO.getTablePosition(),
												dynamicFormRecord);

										if (value != null
												&& !value.trim().equals("")) {
											List<DynamicFormTableRecord> dynamicFormTableRecords = dynamicFormTableRecordDAO
													.getTableRecords(
															Long.parseLong(value),
															PayAsiaConstants.SORT_ORDER_OLDEST_TO_NEWEST,
															"");
											List<Date> effdateList = new ArrayList<Date>();
											SimpleDateFormat formatter = new SimpleDateFormat(
													"yyyy/MM/dd");
											for (DynamicFormTableRecord dynamicFormTableRecord : dynamicFormTableRecords) {
												String effDateString = dynamicFormTableRecord
														.getCol1().substring(0,
																10);
												Date effDate;
												try {
													effDate = formatter
															.parse(effDateString);
													effdateList.add(effDate);
												} catch (ParseException e) {
													LOGGER.error(
															e.getMessage(), e);
												}

											}
											Comparator<Date> comparator = Collections
													.reverseOrder();
											Collections.sort(effdateList,
													comparator);
											Date currentDate = payslip
													.getCreatedDate();
											Date requiredEffdate = null;
											for (Date effectiveDate : effdateList) {
												if (!currentDate
														.before(effectiveDate)) {
													requiredEffdate = effectiveDate;
													break;
												}
											}
											for (DynamicFormTableRecord dynamicFormTableRecord : dynamicFormTableRecords) {
												String effDateString = dynamicFormTableRecord
														.getCol1().substring(0,
																10);
												Date effDate = null;
												try {
													effDate = formatter
															.parse(effDateString);
												} catch (ParseException e) {
													LOGGER.error(
															e.getMessage(), e);
												}
												if (requiredEffdate != null) {
													if (requiredEffdate
															.equals(effDate)) {
														if (valueDTO
																.isFormula()) {

															List<Long> dataDictionaries = new ArrayList<Long>();
															String formulaEx = valueDTO
																	.getFormula();

															while (!formulaEx
																	.equals("")) {
																dataDictionaries
																		.add(Long
																				.parseLong(formulaEx
																						.substring(
																								formulaEx
																										.indexOf('{') + 1,
																								formulaEx
																										.indexOf('}'))));

																formulaEx = formulaEx
																		.substring(formulaEx
																				.indexOf('}') + 1);
															}

															List<String> valueList = getEmployeeFormulaValueForDictionary(
																	payslip,
																	dataDictionary,
																	dataDictionaries,
																	dataTabMap,
																	dynamicFormMap,
																	staticPropMap,
																	dynamicFormTableRecord
																			.getId()
																			.getDynamicFormTableRecordId(),
																	dynamicFormTableRecord
																			.getId()
																			.getSequence());

															String expressionStr = valueDTO
																	.getFormula();
															Pattern pattern = Pattern
																	.compile("[{]\\d*[}]");
															Matcher matcher = pattern
																	.matcher(valueDTO
																			.getFormula());

															int count = 0;
															while (matcher
																	.find()) {
																String valueReturned = valueList
																		.get(count);
																if (valueReturned
																		.equalsIgnoreCase("")) {
																	valueReturned = "0";
																}
																expressionStr = expressionStr
																		.replaceAll(
																				PayAsiaStringUtils
																						.convertToRegEx(matcher
																								.group()),
																				valueReturned);
																count++;

															}

															ScriptEngineManager mgr = new ScriptEngineManager();
															ScriptEngine engine = mgr
																	.getEngineByName("JavaScript");
															try {
																noRecordFound = false;
																field.setValue(engine
																		.eval(expressionStr)
																		.toString());

															} catch (Exception e) {
																LOGGER.error(
																		e.getMessage(),
																		e);
																field.setValue("--NA--");
															}

														} else if (valueDTO
																.getFieldType()
																.equalsIgnoreCase(
																		PayAsiaConstants.EMPLOYEE_lIST_REFERENCE)) {
															String tableValue = getColValueOfDynamicTableRecord(
																	valueDTO.getMethodName(),
																	dynamicFormTableRecord);
															Employee employee = employeeDAO
																	.findById(Long
																			.parseLong(tableValue));

															field.setValue(employeeDetailLogic
																	.getEmployeeName(employee));
														} else {
															String tableValue = getColValueOfDynamicTableRecord(
																	valueDTO.getMethodName(),
																	dynamicFormTableRecord);

															if (valueDTO
																	.getFieldType()
																	.equalsIgnoreCase(
																			PayAsiaConstants.CODEDESC_FIELD_TYPE)) {
																tableValue = getCodeDescVal(
																		field,
																		tableValue);
															}

															field.setValue(tableValue);

														}

													}
												} else {
													field.setValue("");
												}

											}

										} else {
											field.setValue("");
										}

									}

								}

							}
						}

					} else {
						field.setValue("");
					}

					if (noRecordFound) {
						field.setValue("");
					}

				}

			}
		}

	}

	private String getCodeDescVal(Field field, String tableValue) {
		if (StringUtils.isNotBlank(tableValue)) {
			DynamicFormFieldRefValue codeDescField = dynamicFormFieldRefValueDAO
					.findById(Long.parseLong(tableValue));

			tableValue = codeDescField.getDescription() + " ["
					+ codeDescField.getCode() + "]";
		} else {
			field.setValue("");
		}
		return tableValue;
	}

	private List<String> getValueForDictionary(Payslip payslip,
			DataDictionary dataDictionary, List<Long> dataDictionaries,
			HashMap<Long, Tab> dataTabMap,
			HashMap<Long, DynamicForm> dynamicFormMap,
			HashMap<String, ColumnPropertyDTO> staticPropMap)
			throws NoSuchMethodException, IllegalAccessException,
			InvocationTargetException {
		Map<Long, DataImportKeyValueDTO> formulaMap = new LinkedHashMap<Long, DataImportKeyValueDTO>();
		for (Long dataDicId : dataDictionaries) {
			DataDictionary dictionary = dataDictionaryDAO.findById(dataDicId);

			if (dictionary.getFieldType().equalsIgnoreCase(
					PayAsiaConstants.STATIC_TYPE)) {
				setStaticDictionary(formulaMap, dictionary, staticPropMap);
			} else {
				setDynamicDictionary(formulaMap, dictionary, payslip,
						dataTabMap, dynamicFormMap);
			}
		}

		List<String> valueList = new ArrayList<String>();
		for (Long dicID : dataDictionaries) {
			DataImportKeyValueDTO formulaValueDTO = formulaMap.get(dicID);
			if (formulaValueDTO.isStatic()) {
				if (formulaValueDTO.getMethodName().equalsIgnoreCase(
						"EmployeeNumber")) {

					String value = payslip.getEmployee().getEmployeeNumber();

					valueList.add(value);
				} else if (formulaValueDTO.getMethodName().equalsIgnoreCase(
						"FirstName")) {

					String value = payslip.getEmployee().getFirstName();
					valueList.add(value);
				} else if (formulaValueDTO.getMethodName().equalsIgnoreCase(
						"LastName")) {

					String value = payslip.getEmployee().getLastName();
					valueList.add(value);
				} else if (formulaValueDTO.getMethodName().equalsIgnoreCase(
						"HireDate")) {

					Timestamp value = payslip.getEmployee().getHireDate();
					if (value != null) {
						valueList.add(value.toString());
					} else {
						valueList.add("");
					}
				} else if (formulaValueDTO.getMethodName().equalsIgnoreCase(
						"OriginalHireDate")) {

					Timestamp value = payslip.getEmployee()
							.getOriginalHireDate();
					if (value != null) {
						valueList.add(value.toString());
					} else {
						valueList.add("");
					}

				} else if (formulaValueDTO.getMethodName().equalsIgnoreCase(
						"ResignationDate")) {

					Timestamp value = payslip.getEmployee()
							.getResignationDate();
					if (value != null) {
						valueList.add(value.toString());
					} else {
						valueList.add("");
					}
				} else if (formulaValueDTO.getMethodName().equalsIgnoreCase(
						"ConfirmationDate")) {

					Timestamp value = payslip.getEmployee()
							.getConfirmationDate();
					if (value != null) {
						valueList.add(value.toString());
					} else {
						valueList.add("");
					}
				} else if (formulaValueDTO.getMethodName().equalsIgnoreCase(
						"Email")) {

					String value = payslip.getEmployee().getEmail();
					valueList.add(value);
				} else if (formulaValueDTO.getMethodName().equalsIgnoreCase(
						"Status")) {

					boolean value = payslip.getEmployee().isStatus();
					valueList.add(String.valueOf(value));
				} else if (formulaValueDTO.getMethodName().equalsIgnoreCase(
						"MonthName")) {
					String value = payslip.getMonthMaster().getMonthName();
					valueList.add(value);
				} else if (formulaValueDTO.getMethodName().equalsIgnoreCase(
						"Frequency")) {
					String value = payslip.getPayslipFrequency().getFrequency();
					valueList.add(value);

				} else if (formulaValueDTO.getMethodName().equalsIgnoreCase(
						"CompanyName")) {
					String value = payslip.getCompany().getCompanyName();
					valueList.add(value);

				} else if (formulaValueDTO.getMethodName().equalsIgnoreCase(
						"CompanyCode")) {
					String value = payslip.getCompany().getCompanyCode();
					valueList.add(value);

				} else if (formulaValueDTO.getMethodName().equalsIgnoreCase(
						"GroupName")) {
					String value = payslip.getCompany().getCompanyGroup()
							.getGroupName();
					valueList.add(value);

				} else if (formulaValueDTO.getMethodName().equalsIgnoreCase(
						"DateFormat")) {
					String value = payslip.getCompany().getDateFormat();
					valueList.add(value);

				} else if (formulaValueDTO.getMethodName().equalsIgnoreCase(
						"Frequency")) {
					String value = payslip.getPayslipFrequency().getFrequency();
					valueList.add(value);

				} else if (formulaValueDTO.getMethodName().equalsIgnoreCase(
						"FinancialYear")) {
					String value = payslip.getCompany()
							.getFinancialYearMaster().getMonthMaster1()
							+ ""
							+ payslip.getCompany().getFinancialYearMaster()
									.getMonthMaster2();
					valueList.add(value);

				} else if (formulaValueDTO.getMethodName().equalsIgnoreCase(
						"CountryName")) {
					String value = payslip.getCompany().getCountryMaster()
							.getCountryName();
					valueList.add(value);

				}

				else {

					String methodName = "get" + formulaValueDTO.getMethodName();
					Method getCompanyMethod;
					Class<?> payslipClass = payslip.getClass();

					getCompanyMethod = payslipClass.getMethod(methodName);

					if ("int".equalsIgnoreCase(formulaValueDTO.getFieldType())) {
						Integer value = (Integer) getCompanyMethod
								.invoke(payslip);
						valueList.add(String.valueOf(value));
					} else {
						String value = (String) getCompanyMethod
								.invoke(payslip);
						valueList.add(value);
					}
				}
			} else {
				List<DynamicFormRecord> formulaDynFormRecordList = dynamicFormRecordDAO
						.findByEntityKey(payslip.getPayslipId(), dataDictionary
								.getEntityMaster().getEntityId(), payslip
								.getCompany().getCompanyId());
				boolean isRecordFound = false;
				if (formulaDynFormRecordList != null) {
					for (DynamicFormRecord formulaDynFormRecord : formulaDynFormRecordList) {
						if (formulaValueDTO.getFormId() == formulaDynFormRecord
								.getForm_ID()) {
							isRecordFound = true;
							String value = getColValueFile(
									formulaValueDTO.getMethodName(),
									formulaDynFormRecord);
							if (value != null) {
								valueList.add(value);
							} else {
								valueList.add("");
							}
						}
					}
				} else {
					valueList.add("");
				}

				if (!isRecordFound) {
					valueList.add("");
				}
			}
		}
		return valueList;
	}

	private List<String> getEmployeeFormulaValueForDictionary(Payslip payslip,
			DataDictionary dataDictionary, List<Long> dataDictionaries,
			HashMap<Long, Tab> dataTabMap,
			HashMap<Long, DynamicForm> dynamicFormMap,
			HashMap<String, ColumnPropertyDTO> staticPropMap, Long tableId,
			Integer seqNo) throws NoSuchMethodException,
			IllegalAccessException, InvocationTargetException {
		Map<Long, DataImportKeyValueDTO> formulaMap = new LinkedHashMap<Long, DataImportKeyValueDTO>();
		for (Long dataDicId : dataDictionaries) {
			DataDictionary dictionary = dataDictionaryDAO.findById(dataDicId);

			if (dictionary.getFieldType().equalsIgnoreCase(
					PayAsiaConstants.STATIC_TYPE)) {
			} else {
				setDynamicDictionary(formulaMap, dictionary, payslip,
						dataTabMap, dynamicFormMap);
			}
		}

		Set<Long> formulaValueSet = formulaMap.keySet();
		List<String> valueList = new ArrayList<String>();
		for (Iterator<Long> iterator = formulaValueSet.iterator(); iterator
				.hasNext();) {
			Long dicID = iterator.next();
			DataImportKeyValueDTO formulaValueDTO = formulaMap.get(dicID);
			if (formulaValueDTO.isStatic()) {
			} else {
				if (formulaValueDTO.isChild()) {
					DynamicFormTableRecord formulaDynFormTableRecord = dynamicFormTableRecordDAO
							.findByIdAndSeq(tableId, seqNo);
					boolean isRecordFound = false;
					if (formulaDynFormTableRecord != null) {
						isRecordFound = true;
						String value = getColValueOfDynamicTableRecord(
								formulaValueDTO.getMethodName(),
								formulaDynFormTableRecord);
						if (value != null) {
							valueList.add(value);
						} else {
							valueList.add("");
						}

					} else {
						valueList.add("");
					}

					if (!isRecordFound) {
						valueList.add("");
					}
				} else {
					List<DynamicFormRecord> formulaDynFormRecordList = dynamicFormRecordDAO
							.findByEntityKey(payslip.getEmployee()
									.getEmployeeId(), dataDictionary
									.getEntityMaster().getEntityId(), payslip
									.getCompany().getCompanyId());
					boolean isRecordFound = false;
					if (!formulaDynFormRecordList.isEmpty()
							&& formulaDynFormRecordList != null) {
						for (DynamicFormRecord formulaDynFormRecord : formulaDynFormRecordList) {
							if (formulaValueDTO.getFormId() == formulaDynFormRecord
									.getForm_ID()) {
								isRecordFound = true;
								String value = getColValueFile(
										formulaValueDTO.getMethodName(),
										formulaDynFormRecord);
								if (value != null) {
									valueList.add(value);
								} else {
									valueList.add("");
								}
							}
						}
					} else {
						valueList.add("");
					}

					if (!isRecordFound) {
						valueList.add("");
					}
				}

			}
		}
		return valueList;
	}

	private String getColValueFile(String colNumber,
			DynamicFormRecord existingFormRecord) {

		String tableRecordId = null;
		Class<?> dynamicFormRecordClass = existingFormRecord.getClass();
		String colMehtodName = "getCol" + colNumber;
		Method dynamicFormRecordMethod;
		try {

			dynamicFormRecordMethod = dynamicFormRecordClass
					.getMethod(colMehtodName);
			tableRecordId = (String) dynamicFormRecordMethod
					.invoke(existingFormRecord);
		} catch (SecurityException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (NoSuchMethodException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (IllegalArgumentException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (InvocationTargetException e) {
			LOGGER.error(e.getMessage(), e);
		}

		return tableRecordId;

	}

	private String getColValueOfDynamicTableRecord(String colNumber,
			DynamicFormTableRecord existingFormRecord) {

		String tableRecordId = null;
		Class<?> dynamicFormTableRecordClass = existingFormRecord.getClass();
		String colMehtodName = "getCol" + colNumber;
		Method dynamicFormTableRecordMethod;
		try {

			dynamicFormTableRecordMethod = dynamicFormTableRecordClass
					.getMethod(colMehtodName);
			tableRecordId = (String) dynamicFormTableRecordMethod
					.invoke(existingFormRecord);
		} catch (SecurityException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (NoSuchMethodException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (IllegalArgumentException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (InvocationTargetException e) {
			LOGGER.error(e.getMessage(), e);
		}

		return tableRecordId;

	}

	private void setStaticDictionary(Map<Long, DataImportKeyValueDTO> colMap,
			DataDictionary dataDictionary,
			HashMap<String, ColumnPropertyDTO> staticPropMap) {
		DataImportKeyValueDTO dataImportKeyValueDTO = new DataImportKeyValueDTO();
		ColumnPropertyDTO colProp = staticPropMap.get(dataDictionary
				.getTableName() + dataDictionary.getColumnName());
		if (colProp == null) {
			colProp = generalDAO.getColumnProperties(
					dataDictionary.getTableName(),
					dataDictionary.getColumnName());
			staticPropMap.put(
					dataDictionary.getTableName()
							+ dataDictionary.getColumnName(), colProp);
		}

		dataImportKeyValueDTO.setFieldType(colProp.getColumnType());
		dataImportKeyValueDTO.setMethodName(PayAsiaStringUtils
				.getCamelCase(dataDictionary.getColumnName()));
		dataImportKeyValueDTO.setStatic(true);
		colMap.put(dataDictionary.getDataDictionaryId(), dataImportKeyValueDTO);

	}

	private void setDynamicDictionary(Map<Long, DataImportKeyValueDTO> colMap,
			DataDictionary dataDictionary, Payslip payslip,
			HashMap<Long, Tab> dataTabMap,
			HashMap<Long, DynamicForm> dynamicFormMap) {
		DynamicForm dynamicForm = null;

		Tab tab = dataTabMap.get(dataDictionary.getFormID());
		if (tab == null) {
			if (dataDictionary
					.getEntityMaster()
					.getEntityName()
					.equalsIgnoreCase(
							PayAsiaConstants.PAY_SLIP_FORM_ENTITY_NAME)) {
				List<Object[]> tuples = dynamicFormDAO
						.getEffectiveDateByCondition(dataDictionary
								.getCompany().getCompanyId(), dataDictionary
								.getEntityMaster().getEntityId(),
								dataDictionary.getFormID(), payslip.getYear(),
								payslip.getMonthMaster().getMonthId(), payslip
										.getPart());
				for (Object[] tuple : tuples) {
					BigInteger monthId = (BigInteger) tuple[1];
					dynamicForm = dynamicFormDAO
							.getDynamicFormBasedOnEffectiveDate(dataDictionary
									.getCompany().getCompanyId(),
									dataDictionary.getEntityMaster()
											.getEntityId(), dataDictionary
											.getFormID(), (Integer) tuple[0],
									monthId.longValue(), (Integer) tuple[2]);
				}
			} else {
				int maxVersion = dynamicFormDAO.getMaxVersionByFormId(
						dataDictionary.getCompany().getCompanyId(),
						dataDictionary.getEntityMaster().getEntityId(),
						dataDictionary.getFormID());

				dynamicForm = dynamicFormDAO.findByMaxVersionByFormId(
						dataDictionary.getCompany().getCompanyId(),
						dataDictionary.getEntityMaster().getEntityId(),
						maxVersion, dataDictionary.getFormID());

			}

			tab = dataExportUtils.getTabObject(dynamicForm);
			dataTabMap.put(dataDictionary.getFormID(), tab);
			dynamicFormMap.put(dataDictionary.getFormID(), dynamicForm);
		} else {
			dynamicForm = dynamicFormMap.get(dataDictionary.getFormID());
		}

		List<Field> listOfFields = tab.getField();
		DataImportKeyValueDTO dataImportKeyValueDTO = new DataImportKeyValueDTO();
		for (Field field : listOfFields) {
			if (!StringUtils.equalsIgnoreCase(field.getType(), "table")
					&& !StringUtils.equalsIgnoreCase(field.getType(), "label")
					&& !StringUtils.equalsIgnoreCase(field.getType(),
							PayAsiaConstants.FIELD_TYPE_CALCULATORY_FIELD)) {
				if (new String(Base64.decodeBase64(field.getDictionaryName()
						.getBytes())).equals(dataDictionary.getDataDictName())
						|| field.getDictionaryId().longValue() == dataDictionary
								.getDataDictionaryId()) {
					dataImportKeyValueDTO.setStatic(false);
					dataImportKeyValueDTO.setChild(false);
					dataImportKeyValueDTO.setFieldType(field.getType());
					dataImportKeyValueDTO.setMethodName(PayAsiaStringUtils
							.getColNumber(field.getName()));
					dataImportKeyValueDTO.setFormId(dynamicForm.getId()
							.getFormId());
					dataImportKeyValueDTO.setFormula(false);

				}
			} else if (StringUtils.equalsIgnoreCase(field.getType(), "table")) {

				List<Column> listOfColumns = field.getColumn();
				for (Column column : listOfColumns) {
					if (column.getType().equalsIgnoreCase(
							PayAsiaConstants.CALCULATORY_FIELD_FIELD_TYPE)) {
						if (new String(Base64.decodeBase64(column
								.getDictionaryName().getBytes()))
								.equals(dataDictionary.getDataDictName())
								|| column.getDictionaryId().longValue() == dataDictionary
										.getDataDictionaryId()) {
							dataImportKeyValueDTO.setStatic(false);
							dataImportKeyValueDTO.setChild(true);
							dataImportKeyValueDTO
									.setFieldType(column.getType());
							dataImportKeyValueDTO
									.setMethodName(PayAsiaStringUtils
											.getColNumber(column.getName()));
							dataImportKeyValueDTO.setFormId(dynamicForm.getId()
									.getFormId());
							dataImportKeyValueDTO
									.setTablePosition(PayAsiaStringUtils
											.getColNumber(field.getName()));
							dataImportKeyValueDTO.setFormula(true);
							dataImportKeyValueDTO.setFormula(column
									.getFormula());
							if (dataDictionary
									.getEntityMaster()
									.getEntityName()
									.equalsIgnoreCase(
											PayAsiaConstants.EMPLOYEE_ENTITY_NAME)) {
								dataImportKeyValueDTO.setEmployeeEntity(true);
							} else {
								dataImportKeyValueDTO.setEmployeeEntity(false);
							}
						}
					} else if (new String(Base64.decodeBase64(column
							.getDictionaryName().getBytes()))
							.equals(dataDictionary.getDataDictName())
							|| column.getDictionaryId().longValue() == dataDictionary
									.getDataDictionaryId()) {
						dataImportKeyValueDTO.setStatic(false);
						dataImportKeyValueDTO.setChild(true);
						dataImportKeyValueDTO.setFieldType(column.getType());
						dataImportKeyValueDTO.setMethodName(PayAsiaStringUtils
								.getColNumber(column.getName()));
						dataImportKeyValueDTO.setFormId(dynamicForm.getId()
								.getFormId());
						dataImportKeyValueDTO
								.setTablePosition(PayAsiaStringUtils
										.getColNumber(field.getName()));
					}
				}
			} else if (StringUtils.equalsIgnoreCase(field.getType(),
					PayAsiaConstants.FIELD_TYPE_CALCULATORY_FIELD)) {
				if (new String(Base64.decodeBase64(field.getDictionaryName()
						.getBytes())).equals(dataDictionary.getDataDictName())
						|| field.getDictionaryId().longValue() == dataDictionary
								.getDataDictionaryId()) {
					dataImportKeyValueDTO.setStatic(false);
					dataImportKeyValueDTO.setChild(false);
					dataImportKeyValueDTO.setFieldType(field.getType());
					dataImportKeyValueDTO.setMethodName(PayAsiaStringUtils
							.getColNumber(field.getName()));
					dataImportKeyValueDTO.setFormula(true);
					dataImportKeyValueDTO.setFormId(dynamicForm.getId()
							.getFormId());
					dataImportKeyValueDTO.setFormula(field.getFormula());
					if (dataDictionary
							.getEntityMaster()
							.getEntityName()
							.equalsIgnoreCase(
									PayAsiaConstants.EMPLOYEE_ENTITY_NAME)) {
						dataImportKeyValueDTO.setEmployeeEntity(true);
					} else {
						dataImportKeyValueDTO.setEmployeeEntity(false);
					}

				}
			}

			colMap.put(dataDictionary.getDataDictionaryId(),
					dataImportKeyValueDTO);
		}

	}
}
