package com.payasia.common.util;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class ValidationUtils {

	public static int validateNumber(String key, String value, int precision,
			int scale) {
		int isValid = 0;
		int length = 0;
		
		
        if (value.contains(".")) {
        	try {

        		Double rawVal = Double.valueOf(value);
        		BigDecimal bd = new BigDecimal(rawVal);
                BigDecimal rounded = bd.setScale(scale,BigDecimal.ROUND_HALF_UP);
                 
                
                value = rounded.toString();
                
    			length = value.length() - 1;
    			if (length - value.lastIndexOf(".") > scale) {
    				isValid = PayAsiaConstants.SCALE_ERROR;
    			} else {
    				isValid = 0;
    			}

    		} catch (NumberFormatException e) {
    			isValid = PayAsiaConstants.NOT_A_NUMBER;
    		}
        	

		} else {
			try {

				length = value.length();

			} catch (NumberFormatException e) {
				isValid = PayAsiaConstants.NOT_A_NUMBER;
			}
			
		}

		if (length > precision) {
			isValid = PayAsiaConstants.PRECISION_ERROR;
		}

		try {

			Double.parseDouble(value);

		} catch (NumberFormatException e) {
			isValid = PayAsiaConstants.NOT_A_NUMBER;
		}

		return isValid;
	}
	
	

	public static int validateString(String key, String value, int minLength,
			int maxLength) {
		int isValid = 0;
		if (minLength != 0 && maxLength != 0) {
			if (value.length() < minLength) {
				isValid = PayAsiaConstants.LESS_THEN_MINLENGTH;
			}
			if (value.length() > maxLength) {
				isValid = PayAsiaConstants.MORE_THEN_MAXLENGTH;
			}
		}
		return isValid;
	}

	public static int validateCheck(String key, String value) {
		int isValid = 0;
		if (!"FALSE".equalsIgnoreCase(value) && !"TRUE".equalsIgnoreCase(value)) {
			isValid = PayAsiaConstants.CHECK_ERROR;
		}
		return isValid;
	}

	public static int validateCombo(String key, String value,
			List<String> options) {
		int isValid = 0;
		if(StringUtils.isNotBlank(value)){
			value=value.trim();
		}
		if (!options.contains(value.toUpperCase())) {
			isValid = PayAsiaConstants.COMBO_ERROR;
		}

		return isValid;

	}

	public static int validateNAN(String value) {
		int isValid = 0;
		try {

			Double.parseDouble(value);

		} catch (NumberFormatException e) {
			isValid = PayAsiaConstants.NOT_A_NUMBER;
		}
		return isValid;

	}

	public static int validateDate(String value, String dateFormat) {
		int isValid = 0;
		SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

		try {
			Date date = (Date) formatter.parse(value);
			Date startDate = (Date) formatter.parse(PayAsiaConstants.START_DATE);
			if(date.compareTo(startDate) < 0){
				isValid = PayAsiaConstants.DATE_NOT_VALID;;
			}
		
		} catch (ParseException e1) {
			isValid = PayAsiaConstants.DATE_NOT_VALID;
		}
		return isValid;

	}
	
	

}
