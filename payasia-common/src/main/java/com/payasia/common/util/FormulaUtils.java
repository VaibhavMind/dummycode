/**
 * @author vivekjain
 *
 */
package com.payasia.common.util;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


/**
 * The Class DateUtils.
 */

public class FormulaUtils {
	private static final Logger LOGGER = Logger
			.getLogger(FormulaUtils.class);
	/**
	 * Calculate Numeric Type Formula Field Value
	 * 
	 * @param String
	 *            the formula string
	 * @param List<String>
	 *            the value List            
	 * @return the calculated formula
	 */
	public static String getNumericTypeFormulaCalculatedValue(String formula,
			List<String> valueList) {
		String calculatedValue;
		Pattern pattern = Pattern.compile("[{]\\d*[}]");
		Matcher matcher = pattern.matcher(formula);

		int count = 0;
		while (matcher.find()) {
			String valueReturned = valueList.get(count);
			if (valueReturned.equalsIgnoreCase("")) {
				valueReturned = "0";
			} 
			formula = formula.replaceAll(
					PayAsiaStringUtils.convertToRegEx(matcher.group()),
					valueReturned);
			count++;

		}
		DecimalFormat decimalFmt = new DecimalFormat("#,##0.00");
		ScriptEngineManager mgr = new ScriptEngineManager();
		ScriptEngine engine = mgr.getEngineByName("JavaScript");
		try {
			calculatedValue = engine.eval(formula).toString();
			if (StringUtils.isNotBlank(calculatedValue)) {
				calculatedValue = decimalFmt.format(Double
						.parseDouble(calculatedValue));
			}

		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			calculatedValue = "";
		}
		return calculatedValue;
	}

	/**
	 * Purpose : Concatenation/DeConcatenation
	 * Calculate String Type Formula Field Value
	 * 
	 * @param String
	 *            the formula string
	 * @param List<String>
	 *            the value List            
	 * @return the calculated formula
	 */
	public static String getStringTypeFormulaCalulatedValue(String formula,
			List<String> valueList) {
		String calculatedValue = "";
		if (StringUtils.isNotBlank(formula)) {
			formula = formula.toUpperCase();

			Pattern pattern = Pattern.compile("[{]\\d*[}]");
			Matcher matcher = pattern.matcher(formula);
			int count = 0;
			while (matcher.find()) {
				String valueReturned = valueList.get(count);
				if (valueReturned.equalsIgnoreCase("")) {
					valueReturned = "";
				}
				formula = formula.replaceAll(
						PayAsiaStringUtils.convertToRegEx(matcher.group()),
						valueReturned);
				count++;

			}

			String[] splitFormulaExp = formula.split("\\+");
			for (int countt = 0; countt < splitFormulaExp.length; countt++) {
				String expression = splitFormulaExp[countt];
				if (expression.contains("LEFT")) {
					String substringExp = expression.substring(
							expression.indexOf('(') + 1,
							expression.indexOf(')'));
					String fieldVal = substringExp.substring(0,
							substringExp.indexOf(',')).trim();
					int length = Integer.parseInt(substringExp.substring(
							substringExp.indexOf(',') + 1,
							substringExp.length()).trim());
					calculatedValue += StringUtils.left(fieldVal, length);
				}
				if (expression.contains("RIGHT")) {
					String substringExp = expression.substring(
							expression.indexOf('(') + 1,
							expression.indexOf(')'));
					String fieldVal = substringExp.substring(0,
							substringExp.indexOf(',')).trim();
					int length = Integer.parseInt(substringExp.substring(
							substringExp.indexOf(',') + 1,
							substringExp.length()).trim());
					calculatedValue += StringUtils.right(fieldVal, length);
				}
				if (expression.contains("MID")) {
					String substringExp = expression.substring(
							expression.indexOf('(') + 1,
							expression.indexOf(')'));
					String[] splitSubstringExp = substringExp.split(",");

					String fieldVal = splitSubstringExp[0].trim();
					int pos = Integer.parseInt(splitSubstringExp[1].trim());
					int length = Integer.parseInt(splitSubstringExp[2].trim());
					calculatedValue += StringUtils.mid(fieldVal, pos-1, length);
				}
			}

			return calculatedValue;
		}
		return calculatedValue;
	}

	/**
	 * Purpose : Add Date Field with year/month/day
	 * Calculate Date Type Formula Field Value
	 * 
	 * @param String
	 *            the formula string
	 * @param List<String>
	 *            the value List    
	 * @param String
	 *            company Date Format                        
	 * @return the calculated formula
	 */
	public static String getDateTypeFormulaCalulatedValue(String formula,
			List<String> valueList, String companyDateFormat) {
		Date modifiedDate = null;
		if (StringUtils.isNotBlank(formula)) {
			formula = formula.toUpperCase();

			Pattern pattern = Pattern.compile("[{]\\d*[}]");
			Matcher matcher = pattern.matcher(formula);
			int count = 0;
			while (matcher.find()) {
				String valueReturned = valueList.get(count);
				if (valueReturned.equalsIgnoreCase("")) {
					valueReturned = "";
				}
				formula = formula.replaceAll(
						PayAsiaStringUtils.convertToRegEx(matcher.group()),
						valueReturned);
				count++;

			}
			if (formula.contains("ADD")) {
				String fex = formula.substring(formula.indexOf('(') + 1,
						formula.indexOf(')'));
				String[] splitFex = fex.split(",");

				String fieldDateVal = splitFex[0].trim();
				if(StringUtils.isNotBlank(fieldDateVal)){
					String calendarField = splitFex[1].trim();
					int amount = Integer.parseInt(splitFex[2].trim());
					if ("DAY".equalsIgnoreCase(calendarField)) {
						modifiedDate = org.apache.commons.lang.time.DateUtils
								.addDays(DateUtils.stringToDate(fieldDateVal,
										PayAsiaConstants.SERVER_SIDE_DATE_FORMAT),
										amount);
					} else if ("MONTH".equalsIgnoreCase(calendarField)) {
						modifiedDate = org.apache.commons.lang.time.DateUtils
								.addMonths(DateUtils.stringToDate(fieldDateVal,
										PayAsiaConstants.SERVER_SIDE_DATE_FORMAT),
										amount);
					} else if ("YEAR".equalsIgnoreCase(calendarField)) {
						modifiedDate = org.apache.commons.lang.time.DateUtils
								.addYears(DateUtils.stringToDate(fieldDateVal,
										PayAsiaConstants.SERVER_SIDE_DATE_FORMAT),
										amount);
					}
				}
			}
			String calculatedDate;
			if (modifiedDate != null) {
				DateFormat df = new SimpleDateFormat(
						PayAsiaConstants.SERVER_SIDE_DATE_FORMAT);
				calculatedDate = df.format(modifiedDate);
				calculatedDate = DateUtils.convertDateToSpecificFormat(
						calculatedDate, companyDateFormat);
			} else {
				calculatedDate = "";
			}
			return calculatedDate;
		}
		return "";
	}
}
