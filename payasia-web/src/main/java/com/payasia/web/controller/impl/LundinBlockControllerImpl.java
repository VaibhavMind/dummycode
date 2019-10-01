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
import com.payasia.common.dto.LundinBlockDTO;
import com.payasia.common.dto.LundinBlockWithAfeDTO;
import com.payasia.common.dto.ResponseObjectDTO;
import com.payasia.common.form.LundinBlockResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.logic.LundinBlockLogic;
import com.payasia.web.controller.LundinBlockController;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

@Controller
public class LundinBlockControllerImpl implements LundinBlockController {
	private static final Logger LOGGER = Logger
			.getLogger(LundinBlockControllerImpl.class);

	@Resource
	LundinBlockLogic lundinBlockLogic;

	@Override
	@RequestMapping(value = "/admin/lundin/block/getData.html", method = RequestMethod.POST)
	public @ResponseBody String getBlocks(
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

		LundinBlockResponse lundinBlockResponse = lundinBlockLogic
				.getBlockResponse(fromDate, toDate, companyId, pageDTO,
						sortDTO, searchCondition, searchText, transactionType);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(lundinBlockResponse,
				jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/admin/lundin/block/addNewBlock.html", method = RequestMethod.POST)
	public @ResponseBody String addLundinBlock(
			@ModelAttribute(value = "addLundinBlockForm") LundinBlockDTO lundinBlockDTO,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		try {
			lundinBlockDTO.setBlockId(-1);
			ResponseObjectDTO responseDto = lundinBlockLogic.save(
					lundinBlockDTO, companyId);
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
	@RequestMapping(value = "/admin/lundin/block/editBlock.html", method = RequestMethod.POST)
	@ResponseBody
	public String editLundinBlock(
			@ModelAttribute(value = "addLundinBlockForm") LundinBlockDTO lundinBlockDTO,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {

		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		/*ID DECRYPT*/
		lundinBlockDTO.setBlockId(FormatPreserveCryptoUtil.decrypt(lundinBlockDTO.getBlockId()));

		try {
			ResponseObjectDTO responseDto = lundinBlockLogic.update(
					lundinBlockDTO, companyId);
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
	@RequestMapping(value = "/admin/lundin/block/deleteBlock.html", method = RequestMethod.POST)
	@ResponseBody
	public String deleteLundinBlock(
			@ModelAttribute(value = "blockId") long blockId,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		/*ID DECRYPT*/
		blockId = FormatPreserveCryptoUtil.decrypt(blockId);
		
		ResponseObjectDTO responseDto = new ResponseObjectDTO();
		try {
			responseDto = lundinBlockLogic.delete(blockId, companyId);
			
			JsonConfig jsonConfig = new JsonConfig();
			JSONObject jsonObject = JSONObject.fromObject(responseDto,
					jsonConfig);
			return URLEncoder.encode(jsonObject.toString(), "UTF8");
		} catch (Exception e) {

			LOGGER.error(e.getMessage(), e);
			responseDto.setSuccess(false);
			JsonConfig jsonConfig = new JsonConfig();
			JSONObject jsonObject = JSONObject.fromObject(responseDto,
					jsonConfig);
			try {
				return URLEncoder.encode(jsonObject.toString(), "UTF8");
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/employee/lundin/block/getBlockAndAfes.html", method = RequestMethod.POST)
	@ResponseBody
	public String getBlocksAndAfes(HttpServletRequest request,
			HttpServletResponse response, HttpSession session) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		try {
			List<LundinBlockWithAfeDTO> lundinBlockDtos = lundinBlockLogic
					.getBlockAndAfe(new Timestamp(new Date().getTime()),
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
	@RequestMapping(value = "/admin/lundin/block/getRowBlockData.html", method = RequestMethod.POST)
	@ResponseBody
	public String getRowBlockData(
			@RequestParam(value = "blockId", required = true) Long blockId,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		
		/*ID DECRYPT*/
		blockId = FormatPreserveCryptoUtil.decrypt(blockId);
		
		LundinBlockDTO lundinBlockDto = lundinBlockLogic
				.getBlockDataById(blockId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(lundinBlockDto,
				jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

}
