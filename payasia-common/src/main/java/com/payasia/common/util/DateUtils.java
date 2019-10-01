package com.payasia.common.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.log4j.Logger;

import com.payasia.common.bean.util.UserContext;
import com.payasia.dao.bean.Company;

/**
 * The Class DateUtils.
 */
public class DateUtils {

	/**
	 * Time stamp to string.
	 * 
	 * @param timestamp
	 *            the timestamp
	 * @return the string
	 */
	
	private static final Logger LOGGER = Logger
			.getLogger(DateUtils.class);
	public static String timeStampToStringTimeZone(Timestamp timestamp) {

		Date date = new Date(timestamp.getTime());
		date = getDate(date);
		SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
		return formatter.format(date);
	}
	
	
	/**
	 * Time stamp to string.
	 * 
	 * @param timestamp
	 *            the timestamp
	 * @return the string
	 */
	public static String timeStampToString(Timestamp timestamp) {

		if(timestamp == null)
			return "";
		Date date = new Date(timestamp.getTime());
		SimpleDateFormat formatter = new SimpleDateFormat(UserContext.getWorkingCompanyDateFormat());
		return formatter.format(date);
			
	}
	
	public static Date timeStampToDate(Timestamp timestamp) {
		return new Date(timestamp.getTime());			
	}
	
	/**
	 * Time stamp to string.
	 * 
	 * @param timestamp
	 *            the timestamp
	 * @return the string
	 */
	public static String timeStampToStringWOTimezone(Timestamp timestamp,String dateFormat) {

		Date date = new Date(timestamp.getTime());
		SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
		return formatter.format(date);
	}
	
	
	/**
	 * String to timestamp.
	 * 
	 * @param dateString
	 *            the date string
	 * @return the timestamp
	 */
	public static Timestamp stringToTimestampWithTime(String dateString) {

		SimpleDateFormat formatter = new SimpleDateFormat(UserContext.getWorkingCompanyDateFormat());

		Date date;
		try {
			date = (Date) formatter.parse(dateString);
			Date todayDate = new Date();
			Calendar todayCal = Calendar.getInstance();
			todayCal.setTime(todayDate);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.set(Calendar.HOUR_OF_DAY, todayCal.get(Calendar.HOUR_OF_DAY));
			cal.set(Calendar.MINUTE, todayCal.get(Calendar.MINUTE));
			cal.set(Calendar.SECOND, todayCal.get(Calendar.SECOND));
			cal.set(Calendar.MILLISECOND, todayCal.get(Calendar.MILLISECOND));
			
			return  new Timestamp(cal.getTime().getTime());

		} catch (ParseException e) {
			LOGGER.error(e.getMessage(), e);

			return null;
		}
	}
	
	public static Timestamp stringToTimestampWOTime(String dateString) {

		SimpleDateFormat formatter = new SimpleDateFormat(UserContext.getWorkingCompanyDateFormat());

		Date date;
		try {
			date = (Date) formatter.parse(dateString);
			Date todayDate = new Date();
			Calendar todayCal = Calendar.getInstance();
			todayCal.setTime(todayDate);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
					
			return  new Timestamp(cal.getTime().getTime());

		} catch (ParseException e) {
			LOGGER.error(e.getMessage(), e);

			return null;
		}
	}
	

	public static Date getDate(Date date) {
		TimeZone timeZone = TimeZone.getTimeZone("GMT"+UserContext.getWorkingCompanyTimeZoneGMTOffset());
		Calendar calendarIns = Calendar.getInstance(timeZone);
		calendarIns.setTime(date);

		TimeZone z = calendarIns.getTimeZone();
		int offset = z.getRawOffset();
		if (z.inDaylightTime(new Date())) {
			offset = offset + z.getDSTSavings();
		}
		int offsetHrs = offset / 1000 / 60 / 60;
		//offsetHrs = offsetHrs - 05;
		int offsetMins = offset / 1000 / 60 % 60;
		//offsetMins = offsetMins - 30;

		calendarIns.add(Calendar.HOUR_OF_DAY, (offsetHrs));
		calendarIns.add(Calendar.MINUTE, (offsetMins));
		
		return calendarIns.getTime();
	}
	
	public static Date getDateForCalendar(Date date) {
		
		Calendar calendarIns = Calendar.getInstance();
		calendarIns.setTime(date);

		TimeZone z = calendarIns.getTimeZone();
		int offset = z.getRawOffset();
		if (z.inDaylightTime(new Date())) {
			offset = offset + z.getDSTSavings();
		}
		int offsetHrs = offset / 1000 / 60 / 60;
		int offsetMins = offset / 1000 / 60 % 60;

		calendarIns.add(Calendar.HOUR_OF_DAY, (offsetHrs));
		calendarIns.add(Calendar.MINUTE, (offsetMins));
		
		return calendarIns.getTime();
	}
	
	public static Date getDate2(Date date,String timeZoneOffset) {
		TimeZone timeZone = TimeZone.getTimeZone("GMT"+timeZoneOffset);
		Calendar calendarIns = Calendar.getInstance(timeZone);
		calendarIns.setTime(date);

		TimeZone z = calendarIns.getTimeZone();
		int offset = z.getRawOffset();
		if (z.inDaylightTime(new Date())) {
			offset = offset + z.getDSTSavings();
		}
		int offsetHrs = offset / 1000 / 60 / 60;
		offsetHrs = offsetHrs - 00;
		int offsetMins = offset / 1000 / 60 % 60;
		offsetMins = offsetMins - 00;

		calendarIns.add(Calendar.HOUR_OF_DAY, (offsetHrs));
		calendarIns.add(Calendar.MINUTE, (offsetMins));
		
		return calendarIns.getTime();
	}
	
	public static Date getDateM(Date date,String timeZoneOffset) {
		TimeZone timeZone = TimeZone.getTimeZone("GMT"+timeZoneOffset);
		Calendar calendarIns = Calendar.getInstance(timeZone);
		calendarIns.setTime(date);

		TimeZone z = calendarIns.getTimeZone();
		int offset = z.getRawOffset();
		if (z.inDaylightTime(new Date())) {
			offset = offset + z.getDSTSavings();
		}
		int offsetHrs = offset / 1000 / 60 / 60;
		//offsetHrs = offsetHrs - 05;
		int offsetMins = offset / 1000 / 60 % 60;
		//offsetMins = offsetMins - 30;

		calendarIns.add(Calendar.HOUR_OF_DAY, (offsetHrs));
		calendarIns.add(Calendar.MINUTE, (offsetMins));
		
		return calendarIns.getTime();
	}
	
	public static Date getDateAccToTimeZone(Date date,String cmpTimeZoneOffset) {
		TimeZone timeZone = TimeZone.getTimeZone("GMT"+cmpTimeZoneOffset);
		Calendar calendarIns = Calendar.getInstance(timeZone);
		calendarIns.setTime(date);

		TimeZone z = calendarIns.getTimeZone();
		int offset = z.getRawOffset();
		if (z.inDaylightTime(new Date())) {
			offset = offset + z.getDSTSavings();
		}
		int offsetHrs = offset / 1000 / 60 / 60;
		//offsetHrs = offsetHrs - 05;
		int offsetMins = offset / 1000 / 60 % 60;
		//offsetMins = offsetMins - 30;

		calendarIns.add(Calendar.HOUR_OF_DAY, (offsetHrs));
		calendarIns.add(Calendar.MINUTE, (offsetMins));
		
		return calendarIns.getTime();
	}



	public static String timeStampToStringWithTime(Timestamp timestamp) {

		Date date = new Date(timestamp.getTime());
		date = getDate(date);
		String workingCompanyDateFormat = UserContext.getWorkingCompanyDateFormat() + " HH:mm:ss";
		SimpleDateFormat formatter = new SimpleDateFormat(workingCompanyDateFormat);
		return formatter.format(date);
	}
	
	public static String timeStampToStringWithTime(Timestamp timestamp,String dateFormat) {

		Date date = new Date(timestamp.getTime());
		date = getDate(date);
		 dateFormat += " HH:mm:ss";
		SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
		return formatter.format(date);
	}
	public static String timeStampToStringWithTime2(Timestamp timestamp,Company company) {

		Date date = new Date(timestamp.getTime());
		date = getDate2(date,company.getTimeZoneMaster().getGmtOffset());
		String dateFormat=company.getDateFormat() + " HH:mm:ss";
		SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
		return formatter.format(date);
	}
	
	public static String timeStampToStringWithTimeM(Timestamp timestamp,String timeZoneOffset) {

		Date date = new Date(timestamp.getTime());
		date = getDateM(date,timeZoneOffset);
		String workingCompanyDateFormat = UserContext.getWorkingCompanyDateFormat() + " HH:mm:ss";
		SimpleDateFormat formatter = new SimpleDateFormat(workingCompanyDateFormat);
		return formatter.format(date);
	}

	public static String timeStampToString(Timestamp timestamp,
			String dateFormat) {

		Date date = new Date(timestamp.getTime());
		date = getDate(date);
		SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
		return formatter.format(date);
	}
	
	public static String timeStampToStringForCalendar(Timestamp timestamp,
			String dateFormat) {

		Date date = new Date(timestamp.getTime());
		date = getDateForCalendar(date);
		SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
		return formatter.format(date);
	}

	/**
	 * String to timestamp.
	 * 
	 * @param dateString
	 *            the date string
	 * @return the timestamp
	 */
	public static Timestamp stringToTimestamp(String dateString) {

		SimpleDateFormat formatter = new SimpleDateFormat(UserContext.getWorkingCompanyDateFormat());

		Date date;
		try {
			date = (Date) formatter.parse(dateString);
			
			TimeZone timeZone = TimeZone.getTimeZone(PayAsiaConstants.PAYASIA_BASE_TIMEZONE_WITH_GMT);
			Calendar calendarIns = Calendar.getInstance(timeZone);
			calendarIns.setTime(date);
			return  new Timestamp(calendarIns.getTimeInMillis());

		} catch (ParseException e) {
			LOGGER.error(e.getMessage(), e);

			return null;
		}
	}

	public static Timestamp stringToTimestamp(String dateString,
			String dateFormat) {

		SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

		Date date;
		try {
			date = (Date) formatter.parse(dateString);
			TimeZone timeZone = TimeZone.getTimeZone(PayAsiaConstants.PAYASIA_BASE_TIMEZONE_WITH_GMT);
			Calendar calendarIns = Calendar.getInstance(timeZone);
			calendarIns.setTime(date);
			return new Timestamp(calendarIns.getTimeInMillis());

		} catch (ParseException e) {
			LOGGER.error(e.getMessage(), e);

			return null;
		}
	}

	public static Date stringToDate(String dateString) {

		SimpleDateFormat formatter = new SimpleDateFormat(UserContext.getWorkingCompanyDateFormat());

		Date date;
		try {
			date = (Date) formatter.parse(dateString);
			return date;

		} catch (ParseException e) {
			LOGGER.error(e.getMessage(), e);

			return null;
		}
	}

	public static Date stringToDate(String dateString, String dateFormat) {

		SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

		Date date;
		try {
			date = (Date) formatter.parse(dateString);
			return date;

		} catch (ParseException e) {
			LOGGER.error(e.getMessage(), e);

			return null;
		}
	}

	public static Timestamp getCurrentTimestamp() {
		java.util.Date date = new java.util.Date();

		 
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);

		 
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		 
		date = cal.getTime();
		return  new Timestamp(date.getTime());

	}

	public static Timestamp getCurrentTimestampWithTime() {
		java.util.Date date = new java.util.Date();

		 
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);

		date = cal.getTime();
		return new Timestamp(date.getTime());

	}
	
	
	public static Timestamp convertDateToTimeStamp(Date date) {
		

		 
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);

		date = cal.getTime();
		return  new Timestamp(date.getTime());

	}
	
public static Timestamp convertDateToTimeStampWithTime(Date date) {
		

		 
		Calendar cal = Calendar.getInstance();
		Calendar currentTime = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, currentTime.get(Calendar.HOUR_OF_DAY));
		cal.set(Calendar.MINUTE, currentTime.get(Calendar.MINUTE));
		cal.set(Calendar.SECOND, currentTime.get(Calendar.SECOND));
		cal.set(Calendar.MILLISECOND, currentTime.get(Calendar.MILLISECOND));

		date = cal.getTime();
		return  new Timestamp(date.getTime());

	}

	public static String appendTodayTime(String dateVal) {

		Date date = DateUtils.stringToDate(dateVal,
				PayAsiaConstants.SERVER_SIDE_DATE_FORMAT);
		Date todayDate = new Date();
		Calendar todayCal = Calendar.getInstance();
		todayCal.setTime(todayDate);
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, todayCal.get(Calendar.HOUR_OF_DAY));
		cal.set(Calendar.MINUTE, todayCal.get(Calendar.MINUTE));
		cal.set(Calendar.SECOND, todayCal.get(Calendar.SECOND));
		cal.set(Calendar.MILLISECOND, todayCal.get(Calendar.MILLISECOND));

		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy/MM/dd HH:mm:ss");
		return dateFormat.format(cal.getTime());

	}

	public static String convertDateFormat(String strDate, String destFormat) {
		
		if(strDate==null || strDate.equals("")){
			return "";
		}else{
		SimpleDateFormat sdfSource =null;
		if(strDate.length()>10){
			sdfSource= new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		}else{
			sdfSource = new SimpleDateFormat("yyyy/MM/dd");
		}
		SimpleDateFormat sdfDestination = new SimpleDateFormat(destFormat);
		Date date = null;
		try {
			date = sdfSource.parse(strDate);
		} catch (ParseException e) {
			 
			return "";
		}
		strDate = sdfDestination.format(date);

		return strDate;
		}
	}

	public static String convertDateFormatyyyyMMdd(String strDate,
			String sourceFormat) {
		SimpleDateFormat sdfSource = new SimpleDateFormat(sourceFormat);
		SimpleDateFormat sdfDestination = new SimpleDateFormat(
				PayAsiaConstants.SERVER_SIDE_DATE_FORMAT);
		Date date = null;
		try {
			date = sdfSource.parse(strDate);
		} catch (ParseException e) {
			 
			return "";
		}
		strDate = sdfDestination.format(date);

		return strDate;
	}

	public static String convertDateToSpecificFormat(String strDate,
			String destFormat) {
		SimpleDateFormat sdfSource = new SimpleDateFormat(
				PayAsiaConstants.SERVER_SIDE_DATE_FORMAT);
		SimpleDateFormat sdfDestination = new SimpleDateFormat(destFormat);
		Date date = null;
		try {
			date = sdfSource.parse(strDate);
		} catch (ParseException e) {
			 
			return "";
		}

		return sdfDestination.format(date);
	}

	public static String convertDate(String strDate, String sourceFormat,
			String destinationFormat) {
		SimpleDateFormat sdfSource = new SimpleDateFormat(sourceFormat);
		SimpleDateFormat sdfDestination = new SimpleDateFormat(
				destinationFormat);
		Date date = null;
		try {
			date = sdfSource.parse(strDate);
		} catch (ParseException e) {
			 
			return "";
		}

		return  sdfDestination.format(date);
	}
	public static String convertCurrentDateTimeWithMilliSecToString(String dateFormat) {
		Calendar currentDate = Calendar.getInstance();
	    SimpleDateFormat formatter=  new SimpleDateFormat(dateFormat);
	    String currentDateWithTime = formatter.format(currentDate.getTimeInMillis());
		return currentDateWithTime;
		
	}
	
	public static int getCurrentYear() {
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.YEAR);
	}
	
	public static Date convertTimeZoneToIST(String offSet){
		
		TimeZone timeZone = TimeZone.getTimeZone("GMT"+offSet+"");
		Calendar c = Calendar.getInstance(timeZone);
		System.out.println("current: " + c.getTime());

		TimeZone z = c.getTimeZone();
		int offset = z.getRawOffset();
		if (z.inDaylightTime(new Date())) {
			offset = offset + z.getDSTSavings();
		}
		int offsetHrs = offset / 1000 / 60 / 60;
		//offsetHrs = offsetHrs - 05;
		int offsetMins = offset / 1000 / 60 % 60;
		//offsetMins = offsetMins - 30;
		System.out.println("offset: " + offsetHrs);
		System.out.println("offset: " + offsetMins);

		c.add(Calendar.HOUR_OF_DAY, (offsetHrs));
		c.add(Calendar.MINUTE, (offsetMins));
		
		return c.getTime();

	}
	
	public static Date convertTimeZoneToLocal(String offSet){
		
		LOGGER.info("Converting to local time...");
		TimeZone timeZone = TimeZone.getTimeZone("GMT"+offSet+"");
		Calendar c = Calendar.getInstance(timeZone);
		System.out.println("current: " + c.getTime());
		LOGGER.info("Current Time: "+c.getTime());

		TimeZone z = c.getTimeZone();
		int offset = z.getRawOffset();
		if (z.inDaylightTime(new Date())) {
			offset = offset + z.getDSTSavings();
		}
		int offsetHrs = offset / 1000 / 60 / 60;
		//offsetHrs = offsetHrs - 05;
		int offsetMins = offset / 1000 / 60 % 60;
		//offsetMins = offsetMins - 30;*/
		System.out.println("offset: " + offsetHrs);
		System.out.println("offset: " + offsetMins);
		LOGGER.info("offset hours: " + offsetHrs);
		LOGGER.info("offset minutes: " + offsetHrs);

		c.add(Calendar.HOUR_OF_DAY, (offsetHrs));
		c.add(Calendar.MINUTE, (offsetMins));
		
		return c.getTime();

	}
	
	public static Boolean checkDates(String fromDate, String toDate) {

		Timestamp fromTimeStamp = DateUtils.stringToTimestamp(fromDate);
		Timestamp toTimeStamp = DateUtils.stringToTimestamp(toDate);
		Integer dateCompVal = fromTimeStamp.compareTo(toTimeStamp);
		if (dateCompVal >= 1) {
			return true;
		}
		return false;

	}
	
	public static String getCurrentDateWithFormat(String dateFormat) {
		java.util.Date date = new java.util.Date();

		 
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);

		 
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		 
		date = cal.getTime();
		 SimpleDateFormat formatter=  new SimpleDateFormat(dateFormat);
		return formatter.format(date);

	}
	
	/**
	 * formats the date to String as per company format from UserContext
	 * 
	 * @param date util.Date object to convert to String
	 * @return date in String format, null if supplied date is null.
	 */
	
	public static String dateToString(Date date){
		if(date==null)return null;
		SimpleDateFormat formatter = new SimpleDateFormat(UserContext.getWorkingCompanyDateFormat());
			return formatter.format(date);	
	}
	
	public static Date addDays(Date date, int days)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); 
        return cal.getTime();
    }
	
	public static java.sql.Date workdayDateStringToDate(String dateString, String dateFormat) {
		
		SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
		try {
			return new java.sql.Date(((Date) formatter.parse(dateString)).getTime());
		} catch (ParseException e) {
			LOGGER.error(e.getMessage(), e);
			return null;
		}
	}
	
	public static Timestamp convertStringToTimestamp(String date,String dateformat) throws ParseException {
		String offSet=UserContext.getWorkingCompanyTimeZoneGMTOffset();
		DateFormat sdf = new SimpleDateFormat(dateformat);
		Date date1 = sdf.parse(date);
		TimeZone timeZone = TimeZone.getTimeZone("GMT"+offSet+"");
		Calendar c = Calendar.getInstance(timeZone);
		c.setTime(date1);
		TimeZone z = c.getTimeZone();
		int offset = z.getRawOffset();
		if (z.inDaylightTime(new Date())) {
			offset = offset + z.getDSTSavings();
		}
		int offsetHrs = offset / 1000 / 60 / 60;
		int offsetMins = offset / 1000 / 60 % 60;
		LOGGER.debug("offset: " + offsetHrs);
		LOGGER.debug("offset: " + offsetMins);
		c.add(Calendar.HOUR_OF_DAY, -(offsetHrs));
		c.add(Calendar.MINUTE, -(offsetMins));
		String formattedDate = sdf.format(c.getTime());
	    return new Timestamp(sdf.parse(formattedDate).getTime());
	}
	
	public static java.sql.Date getCurrentDate(String dateFormat) {
		java.util.Date date = new java.util.Date();
		 
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);

		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		date = cal.getTime();
		java.sql.Date formattedDate = null;
		SimpleDateFormat formatter=  new SimpleDateFormat(dateFormat);
		String formattedDateStr =  formatter.format(date);
		formattedDate = workdayDateStringToDate(formattedDateStr, dateFormat);
		return formattedDate;
	}
	
	public static Timestamp stringToTimestampDate(String date,String dateformat,String offSet) throws ParseException {
		
		DateFormat sdf = new SimpleDateFormat(dateformat+" HH:mm");
		Date date1 = sdf.parse(date);
		TimeZone timeZone = TimeZone.getTimeZone("GMT"+offSet+"");
		Calendar c = Calendar.getInstance(timeZone);
		c.setTime(date1);
		TimeZone z = c.getTimeZone();
		int offset = z.getRawOffset();
		if (z.inDaylightTime(new Date())) {
			offset = offset + z.getDSTSavings();
		}
		int offsetHrs = offset / 1000 / 60 / 60;
		int offsetMins = offset / 1000 / 60 % 60;
		LOGGER.debug("offset: " + offsetHrs);
		LOGGER.debug("offset: " + offsetMins);
		c.add(Calendar.HOUR_OF_DAY, -(offsetHrs));
		c.add(Calendar.MINUTE, -(offsetMins));
		String formattedDate = sdf.format(c.getTime());
	    Timestamp ts=new Timestamp(sdf.parse(formattedDate).getTime());
	    return ts;
	}
	
	public static String dateToStringTime(Timestamp ts) throws ParseException {
		Date date = new Date();
		date.setTime(ts.getTime());
		String formattedDate = new SimpleDateFormat("HH:mm").format(date);
		return formattedDate;

	}
	
	public static Date gmttoLocalDate(Date date,String offSet) throws ParseException {
		
		TimeZone timeZone = TimeZone.getTimeZone("GMT"+offSet+"");
		Calendar c = Calendar.getInstance(timeZone);
		c.setTime(date);
		TimeZone z = c.getTimeZone();
		int offset = z.getRawOffset();
		if (z.inDaylightTime(new Date())) {
			offset = offset + z.getDSTSavings();
		}
		int offsetHrs = offset / 1000 / 60 / 60;
		int offsetMins = offset / 1000 / 60 % 60;
		c.add(Calendar.HOUR_OF_DAY, (offsetHrs));
		c.add(Calendar.MINUTE, (offsetMins));
	    return c.getTime();
	}

}
