package com.payasia.web.controller.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.payasia.common.dto.CompanyConditionDTO;
import com.payasia.common.dto.CompanyModuleDTO;
import com.payasia.common.dto.ManageModuleDTO;
import com.payasia.common.form.ManageModuleFormResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.logic.ManageModuleLogic;
import com.payasia.web.controller.ManageModuleController;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

@Controller
@RequestMapping(value = "/admin/manageModule")
public class ManageModuleControllerImpl implements ManageModuleController {

	@Resource
	ManageModuleLogic manageModuleLogic;

	@RequestMapping(value = "/viewManagModList", method = RequestMethod.POST)
	public @ResponseBody @Override String viewManageModuleList(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "searchCondition", required = false) String searchCondition,
			@RequestParam(value = "searchText", required = false) String searchText,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session

	) {

		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		List<ManageModuleDTO> list = manageModuleLogic
				.findCompanyWithGroupAndModule(new CompanyConditionDTO());

		Map<Long, CompanyModuleDTO> map = new HashMap<>();

		for (ManageModuleDTO manageModuleDTO : list) {
			if (manageModuleDTO.getModuleId() == null) {
				manageModuleDTO.setModuleId(0l);
			}
			CompanyModuleDTO dto = new CompanyModuleDTO();

			if (manageModuleDTO.getModuleId() == 1) {
				dto.setHasLeaveModule(true);
			}
			if (manageModuleDTO.getModuleId() == 2) {
				dto.setHasClaimModule(true);
			}

			if (manageModuleDTO.getModuleId() == 3) {
				dto.setHasHrisModule(true);
			}
			if (manageModuleDTO.getModuleId() == 4) {
				dto.setHasMobile(true);
			}
			// if (manageModuleDTO.getModuleId() == 5) {
			// dto.setHasOTTimesheetModule(true);
			// }
			if (manageModuleDTO.getModuleId() == 5) {
				dto.setHasLundinTimesheetModule(true);
			}
			if (manageModuleDTO.getModuleId() == 6) {
				dto.setHasLionTimesheetModule(true);
			}

			if (manageModuleDTO.getModuleId() == 7) {
				dto.setHasCoherentTimesheetModule(true);
			}

			if (!map.isEmpty()
					&& map.containsKey(manageModuleDTO.getCompanyId())) {
				CompanyModuleDTO obj = map.get(manageModuleDTO.getCompanyId());
				if (!obj.isHasLeaveModule()) {
					obj.setHasLeaveModule(dto.isHasLeaveModule());
				}
				if (!obj.isHasClaimModule()) {
					obj.setHasClaimModule(dto.isHasClaimModule());
				}
				if (!obj.isHasHrisModule()) {
					obj.setHasHrisModule(dto.isHasHrisModule());
				}
				// if (!obj.isHasOTTimesheetModule()) {
				// obj.setHasOTTimesheetModule(dto.isHasOTTimesheetModule());
				// }
				if (!obj.isHasLundinTimesheetModule()) {
					obj.setHasLundinTimesheetModule(dto
							.isHasLundinTimesheetModule());
				}
				if (!obj.isHasLionTimesheetModule()) {
					obj.setHasLionTimesheetModule(dto
							.isHasLionTimesheetModule());
				}
				if (!obj.isHasMobile()) {
					obj.setHasMobile(dto.isHasMobile());
				}
				if (!obj.isHasCoherentTimesheetModule()) {
					obj.setHasCoherentTimesheetModule(dto
							.isHasCoherentTimesheetModule());
				}

			} else {
				map.put(manageModuleDTO.getCompanyId(), dto);
			}

		}

		List<ManageModuleDTO> manageModuleDTOs = new ArrayList<>();

		for (ManageModuleDTO manageModuleDto : list) {
			// if(manageModuleDTO2.getCompanyId())

			ManageModuleDTO manModuleDTO = new ManageModuleDTO();

			CompanyModuleDTO companyModDto = map.get(manageModuleDto
					.getCompanyId());
			manModuleDTO.setCompanyCode(manageModuleDto.getCompanyCode());
			manModuleDTO.setCompanyId(manageModuleDto.getCompanyId());
			manModuleDTO.setCompanyName(manageModuleDto.getCompanyName());
			manModuleDTO.setModuleId(manageModuleDto.getModuleId());
			manModuleDTO.setModuleName(manageModuleDto.getModuleName());
			manModuleDTO.setGroupName(manageModuleDto.getGroupName());
			manModuleDTO.setGroupId(manageModuleDto.getGroupId());
			manModuleDTO.setHasLeaveModule(companyModDto.isHasLeaveModule());
			manModuleDTO.setHasClaimModule(companyModDto.isHasClaimModule());
			manModuleDTO.setHasHrisModule(companyModDto.isHasHrisModule());
			// manModuleDTO.setHasOTTimesheetModule(companyModDto
			// .isHasOTTimesheetModule());
			manModuleDTO.setHasLundinTimesheetModule(companyModDto
					.isHasLundinTimesheetModule());
			manModuleDTO.setHasLionTimesheetModule(companyModDto
					.isHasLionTimesheetModule());
			manModuleDTO.setHasMobile(companyModDto.isHasMobile());
			manModuleDTO.setHasCoherentTimesheetModule(companyModDto
					.isHasCoherentTimesheetModule());
			if (!manageModuleDTOs.contains(manModuleDTO)) {
				manageModuleDTOs.add(manModuleDTO);
			}
		}

		List<ManageModuleDTO> finalManageModeuleList = new ArrayList<>();

		for (ManageModuleDTO manModuleDTO : manageModuleDTOs) {
			if (searchCondition.equals("grpName")) {
				if (StringUtils.isNotBlank(searchText)) {
					if (manModuleDTO.getGroupName().trim().toLowerCase()
							.contains(searchText.trim().toLowerCase())) {
						finalManageModeuleList.add(manModuleDTO);
					}

				} else {
					finalManageModeuleList.add(manModuleDTO);
				}
			} else if (searchCondition.equals("comName")) {
				if (StringUtils.isNotBlank(searchText)) {
					if (manModuleDTO.getCompanyName().toLowerCase().trim()
							.contains(searchText.trim().toLowerCase())) {
						finalManageModeuleList.add(manModuleDTO);
					}
				} else {
					finalManageModeuleList.add(manModuleDTO);
				}
			} else if (searchCondition.equals("comCode")) {
				if (StringUtils.isNotBlank(searchText)) {
					if (manModuleDTO.getCompanyCode().toLowerCase().trim()
							.contains(searchText.trim().toLowerCase())) {
						finalManageModeuleList.add(manModuleDTO);
					}
				} else {
					finalManageModeuleList.add(manModuleDTO);
				}
			} else {
				finalManageModeuleList.add(manModuleDTO);
			}

		}

		Collections.sort(finalManageModeuleList,
				new Comparator<ManageModuleDTO>() {

					@Override
					public int compare(ManageModuleDTO o1, ManageModuleDTO o2) {
						// TODO Auto-generated method stub
						return o1.getGroupName().compareToIgnoreCase(
								o2.getGroupName());
					}
				});

		ManageModuleFormResponse responses = new ManageModuleFormResponse();
		responses.setResponse(finalManageModeuleList);
		responses.setPage(1);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(responses, jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/editManageModule.html", method = RequestMethod.POST)
	@ResponseBody
	public String editManageModule(
			@ModelAttribute(value = "manageModuleDTO") ManageModuleDTO manageModuleDTO,
			HttpServletRequest request, HttpServletResponse response) {

		String status = manageModuleLogic
				.updateModuleManageOfcompany(manageModuleDTO);

		return status;
	}

}
