package com.payasia.api.hris.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.payasia.api.hris.EmployeeProfileImageApi;
import com.payasia.api.utils.ApiUtils;
import com.payasia.common.bean.util.UserContext;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.bean.Employee;
import com.payasia.logic.EmployeeDetailLogic;

/**
 * @author gauravkumar
 * @param :
 *            This class used for Employee Profile Image APIs
 * 
 */
@RestController
@RequestMapping(value = ApiUtils.API_ROOT_PATH_FOR_EMPLOYEE_HRIS)
public class EmployeeProfileImageApiImpl implements EmployeeProfileImageApi {

	@Resource
	EmployeeDetailLogic employeeDetailLogic;

	/** The employee image width. */
	@Value("#{payasiaptProperties['payasia.employee.image.width']}")
	private Integer employeeImageWidth;

	/** The employee image height. */
	@Value("#{payasiaptProperties['payasia.employee.image.height']}")
	private Integer employeeImageHeight;

	@Resource
	private EmployeeDAO employeeDAO;

	@Override
	@PostMapping(value = "profile-image")
	public ResponseEntity<?> employeeViewProfileImage() throws IOException {
		final Long employeeId = Long.valueOf(UserContext.getUserId());
		Map<String, Object> imageMap = new HashMap<String, Object>();
		byte[] byteFile = null;
		String empname;
		
		Employee emp = employeeDAO.findById(employeeId);
		
		
		empname = emp.getFirstName() + " "+(emp.getMiddleName()!=null ?emp.getMiddleName():"") + " " +(emp.getLastName()!=null ?emp.getLastName():"");
		imageMap.put("employeeFullname", empname);
		imageMap.put("employeeNum", emp.getEmployeeNumber());
		imageMap.put("firstName", emp.getFirstName());
		imageMap.put("middleName",  emp.getMiddleName());
		imageMap.put("lastName", emp.getLastName());
		
		byteFile = employeeDetailLogic.getEmployeeImage(employeeId, null, employeeImageWidth, employeeImageHeight);
		if (byteFile!= null) {
			imageMap.put("employeeImageData", byteFile);
		} 
		
		return new ResponseEntity<>(imageMap, HttpStatus.OK);
	}

}
