package com.payasia.web.controller.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.LundinDepartmentDTO;
import com.payasia.common.dto.ResponseObjectDTO;
import com.payasia.common.form.LundinDepartmentResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.logic.LundinDepartmentLogic;
import com.payasia.web.controller.LundinDepartmentController;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

@Controller
public class LundinDepartmentControllerImpl implements
		LundinDepartmentController {

	private static final Logger LOGGER = Logger
			.getLogger(LundinDepartmentControllerImpl.class);

	@Resource
	LundinDepartmentLogic lundinDepartmentLogic;

	@Override
	@RequestMapping(value = "/admin/lundin/department/getData.html", method = RequestMethod.POST)
	public @ResponseBody String getDepartments(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "fromDate", required = true) String fromDate,
			@RequestParam(value = "toDate", required = true) String toDate,
			@RequestParam(value = "searchCondition", required = false) String searchCondition,
			@RequestParam(value = "searchText", required = false) String searchText,
			@RequestParam(value = "transactionType", required = false) String transactionType,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		LundinDepartmentResponse lundinDepartmentResponse = lundinDepartmentLogic
				.getDepartmentResponse(fromDate, toDate, companyId, pageDTO,
						sortDTO, searchCondition, searchText, transactionType);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(lundinDepartmentResponse,
				jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/admin/lundin/department/editDepartment.html", method = RequestMethod.POST)
	@ResponseBody
	public String editLundinDepartment(
			@ModelAttribute(value = "addLundinDeptForm") LundinDepartmentDTO lundinDepartmentDTO,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {

		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		
		/*ID DECRYPT*/
		lundinDepartmentDTO.setDepartmentId(FormatPreserveCryptoUtil.decrypt(lundinDepartmentDTO.getDepartmentId()));
		
		try {
			ResponseObjectDTO responseDto = lundinDepartmentLogic.update(
					lundinDepartmentDTO, companyId);
			JsonConfig jsonConfig = new JsonConfig();
			JSONObject jsonObject = JSONObject.fromObject(responseDto,
					jsonConfig);
			return URLEncoder.encode(jsonObject.toString(), "UTF8");
		} catch (UnsupportedEncodingException e) {

			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/admin/lundin/department/getRowDeptData.html", method = RequestMethod.POST)
	@ResponseBody
	public String getRowDepartmentData(
			@RequestParam(value = "departmentId", required = true) Long departmentId,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		
		/*ID DECRYPT*/
		departmentId =  FormatPreserveCryptoUtil.decrypt(departmentId);
		
		LundinDepartmentDTO lundinDepartmentDto = lundinDepartmentLogic
				.getBlockDataById(departmentId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(lundinDepartmentDto,
				jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

}
