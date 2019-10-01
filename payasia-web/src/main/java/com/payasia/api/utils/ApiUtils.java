package com.payasia.api.utils;

import org.springframework.beans.factory.annotation.Value;

/**
 * @author manojkumar2
 * @Param : This class used for the API related constant.
*/
public final class ApiUtils {
   
	@Value("#{payasiaptProperties['api.version']}")
    public static final String API_VERSION="v1";
    
	public final static String API_ROOT_PATH="/payasia/api/"+API_VERSION+"/";
	public final static String API_ROOT_PATH_FOR_COMMON=API_ROOT_PATH+"common/";
	public final static String API_ROOT_PATH_FOR_EMPLOYEE=API_ROOT_PATH+"employee/";
	public final static String API_ROOT_PATH_FOR_ADMIN=API_ROOT_PATH+"admin/";
	
	public final static String API_ROOT_PATH_FOR_COMMON_HRIS=API_ROOT_PATH_FOR_COMMON+"hris/";
	public final static String API_ROOT_PATH_FOR_EMPLOYEE_HRIS=API_ROOT_PATH_FOR_EMPLOYEE+"hris/";
	
	
	public final static String CLAIM="claim";
	public final static String LEAVE="leave";
	
	public final static String PENDING_CLAIM="/pendingclaim";
	public final static String MY_CLAIM="/myclaim";
	public final static String EMPLOYEE_CLAIM_SUMMARY="/employeeclaimsummary";
	public final static String API_ROOT_PATH_FOR_EMPLOYEE_CLAIM=API_ROOT_PATH_FOR_EMPLOYEE+CLAIM;
	public final static String API_ROOT_PATH_FOR_COMMON_CLAIM=API_ROOT_PATH_FOR_COMMON+CLAIM;
	
	public final static String API_ROOT_PATH_FOR_EMPLOYEE_LEAVE=API_ROOT_PATH_FOR_EMPLOYEE+LEAVE;
}
