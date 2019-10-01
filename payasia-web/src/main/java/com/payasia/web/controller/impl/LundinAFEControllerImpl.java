package com.payasia.web.controller.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

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
import com.payasia.common.dto.LundinAFEDTO;
import com.payasia.common.dto.LundinBlockDTO;
import com.payasia.common.dto.ResponseObjectDTO;
import com.payasia.common.form.LundinAFEResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.logic.LundinAFELogic;
import com.payasia.web.controller.LundinAFEController;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

@Controller
public class LundinAFEControllerImpl implements LundinAFEController {
	private static final Logger LOGGER = Logger
			.getLogger(LundinAFEControllerImpl.class);

	@Resource
	private LundinAFELogic lundinAFELogic;

	@Override
	@RequestMapping(value = "/admin/lundin/afe/getAfeData.html", method = RequestMethod.POST)
	public @ResponseBody String getAfeData(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "fromDate", required = false) String fromDate,
			@RequestParam(value = "toDate", required = false) String toDate,
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

		LundinAFEResponse LundinAFEResponse = lundinAFELogic.getAfeResponse(
				fromDate, toDate, companyId, pageDTO, sortDTO, searchCondition,
				searchText, transactionType);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(LundinAFEResponse,
				jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/admin/lundin/afe/addNewAfe.html", method = RequestMethod.POST)
	public @ResponseBody String addLundinAFE(
			@ModelAttribute(value = "addLundinAfeForm") LundinAFEDTO lundinAFEDTO,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		try {
			ResponseObjectDTO responseDto = lundinAFELogic.save(lundinAFEDTO,companyId);
			JsonConfig jsonConfig = new JsonConfig();
			JSONObject jsonObject = JSONObject.fromObject(responseDto,
					jsonConfig);
			return URLEncoder.encode(jsonObject.toString(), "UTF8");
		} catch (Exception e) {

			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/admin/lundin/afe/editAfe.html", method = RequestMethod.POST)
	@ResponseBody
	public String editLundinAFE(
			@ModelAttribute(value = "addLundinAfeForm") LundinAFEDTO lundinAFEDTO,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {

		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		
		/*ID DECRYPT*/
		lundinAFEDTO.setAfeId(FormatPreserveCryptoUtil.decrypt(lundinAFEDTO.getAfeId()));
		
		try {
			ResponseObjectDTO responseDto = lundinAFELogic.update(lundinAFEDTO,
					companyId);
			
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
	@RequestMapping(value = "/admin/lundin/afe/deleteAfe.html", method = RequestMethod.POST)
	@ResponseBody
	public String deleteLundinAFE(@ModelAttribute(value = "afeId") long afeId,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {

		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		
		/*ID DECRYPT*/
		afeId = FormatPreserveCryptoUtil.decrypt(afeId);
		
		try {
			ResponseObjectDTO responseDto = lundinAFELogic.delete(afeId,
					companyId);
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
	@RequestMapping(value = "/admin/lundin/afe/getActiveBlocksForCombo.html", method = RequestMethod.POST)
	@ResponseBody
	public String getActiveBlockList(HttpServletRequest request,
			HttpServletResponse response, HttpSession session) {

		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		try {
			List<LundinBlockDTO> lundinBlockDtos = lundinAFELogic
					.getActiveBlocks(new Timestamp(new Date().getTime()),
							companyId);
			JsonConfig jsonConfig = new JsonConfig();
			JSONArray jsonObject = JSONArray.fromObject(lundinBlockDtos,
					jsonConfig);

			return URLEncoder.encode(jsonObject.toString(), "UTF8");
		} catch (UnsupportedEncodingException e) {

			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/admin/lundin/afe/getRowAfeData.html", method = RequestMethod.POST)
	@ResponseBody
	public String getRowAfeData(
			@RequestParam(value = "afeId", required = true) Long afeId,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {

		/*ID DECRYPT*/
		afeId = FormatPreserveCryptoUtil.decrypt(afeId);
		
		LundinAFEDTO lundinAfeDto = lundinAFELogic.getAfeDataById(afeId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(lundinAfeDto, jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

}
