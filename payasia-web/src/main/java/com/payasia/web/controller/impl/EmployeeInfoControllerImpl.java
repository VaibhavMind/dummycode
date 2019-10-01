package com.payasia.web.controller.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.payasia.common.form.CustomEmpQualificationDetailsResponse;
import com.payasia.common.form.CustomEmpResidenceDetailsResponse;
import com.payasia.common.form.EmployeeQualificationDetailsForm;
import com.payasia.common.form.EmployeeResidenceDetailsForm;
import com.payasia.web.controller.EmployeeInfoController;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

@Controller
@RequestMapping(value = "/employee")
public class EmployeeInfoControllerImpl implements EmployeeInfoController {

	@RequestMapping(value = "/qualificationDetails")
	public @ResponseBody
	String getqualificationDetails() {
		CustomEmpQualificationDetailsResponse response = new CustomEmpQualificationDetailsResponse();

		List<EmployeeQualificationDetailsForm> list = new ArrayList<EmployeeQualificationDetailsForm>();

		EmployeeQualificationDetailsForm empQualificationDetail = new EmployeeQualificationDetailsForm();
		empQualificationDetail.setYear("2011");
		empQualificationDetail.setQualification("Btech");
		empQualificationDetail.setDuration("4 Years");
		empQualificationDetail.setInstitute("fsdjfhjkasdfhh");
		empQualificationDetail.setUniversity("dsfbsdjl");
		empQualificationDetail.setLevel("Graduate");
		empQualificationDetail.setQualificationArea("");
		empQualificationDetail.setGrade("A");
		empQualificationDetail.setRemarks("");
		list.add(empQualificationDetail);
		EmployeeQualificationDetailsForm empQualificationDetail1 = new EmployeeQualificationDetailsForm();
		empQualificationDetail1.setYear("2013");
		empQualificationDetail1.setQualification("Mtech");
		empQualificationDetail1.setDuration("2 Years");
		empQualificationDetail1.setInstitute("fsdjfhjkasdfhh");
		empQualificationDetail1.setUniversity("dsfbsdjl");
		empQualificationDetail1.setLevel("Post Graduate");
		empQualificationDetail1.setQualificationArea("");
		empQualificationDetail1.setGrade("A");
		empQualificationDetail1.setRemarks("");
		list.add(empQualificationDetail1);
		EmployeeQualificationDetailsForm empQualificationDetail2 = new EmployeeQualificationDetailsForm();
		empQualificationDetail2.setYear("2011");
		empQualificationDetail2.setQualification("Phd");
		empQualificationDetail2.setDuration("2 Years");
		empQualificationDetail2.setInstitute("fsdjfhjkasdfhh");
		empQualificationDetail2.setUniversity("dsfbsdjl");
		empQualificationDetail2.setLevel("Post Graduate");
		empQualificationDetail2.setQualificationArea("");
		empQualificationDetail2.setGrade("A");
		empQualificationDetail2.setRemarks("");
		list.add(empQualificationDetail2);

		int totalRecords;
		totalRecords = list.size();
		response.setRows(list);

		int recordSize = totalRecords;
		int pageSize = 5;
		int totalPages = recordSize / pageSize;
		if (recordSize % pageSize != 0) {
			totalPages = totalPages + 1;
		}
		if (recordSize == 0) {
			totalPages = 0;
		}

		response.setRecords(String.valueOf(recordSize));
		response.setPage("1");
		response.setTotal(String.valueOf(totalPages));

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(response, jsonConfig);
		return jsonObject.toString();
	}

	@RequestMapping(value = "/residenceInfomation")
	public @ResponseBody
	String getResidenceInfomation() {
		CustomEmpResidenceDetailsResponse response = new CustomEmpResidenceDetailsResponse();

		List<EmployeeResidenceDetailsForm> list = new ArrayList<EmployeeResidenceDetailsForm>();

		EmployeeResidenceDetailsForm empResidenceDetail = new EmployeeResidenceDetailsForm();
		empResidenceDetail.setEffectiveFrom("");
		empResidenceDetail.setFwlClass("");
		empResidenceDetail.setIdentityNumber("");
		empResidenceDetail.setIdentityType("");
		empResidenceDetail.setResidenceStatus("");
		list.add(empResidenceDetail);

		int totalRecords;
		totalRecords = list.size();
		response.setRows(list);

		int recordSize = totalRecords;
		int pageSize = 5;
		int totalPages = recordSize / pageSize;
		if (recordSize % pageSize != 0) {
			totalPages = totalPages + 1;
		}
		if (recordSize == 0) {
			totalPages = 0;
		}

		response.setRecords(String.valueOf(recordSize));
		response.setPage("1");
		response.setTotal(String.valueOf(totalPages));

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(response, jsonConfig);
		return jsonObject.toString();
	}

}
