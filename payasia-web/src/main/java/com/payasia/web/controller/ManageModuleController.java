package com.payasia.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.payasia.common.dto.ManageModuleDTO;

public interface ManageModuleController {

	String viewManageModuleList(String columnName, String sortingType,
			String searchCondition, String searchText, int page, int rows,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session);

	String editManageModule(ManageModuleDTO manageModuleDTO,
			HttpServletRequest request, HttpServletResponse response);

}
