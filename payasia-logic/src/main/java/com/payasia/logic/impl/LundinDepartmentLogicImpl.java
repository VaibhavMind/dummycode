package com.payasia.logic.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.LundinConditionDTO;
import com.payasia.common.dto.LundinDepartmentDTO;
import com.payasia.common.dto.ResponseObjectDTO;
import com.payasia.common.form.LundinDepartmentResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.dao.AppCodeMasterDAO;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.DynamicFormFieldRefValueDAO;
import com.payasia.dao.LundinAFEDAO;
import com.payasia.dao.LundinBlockDAO;
import com.payasia.dao.LundinDepartmentDAO;
import com.payasia.dao.LundinTimesheetPreferenceDAO;
import com.payasia.dao.bean.AppCodeMaster;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.DataDictionary;
import com.payasia.dao.bean.DynamicFormFieldRefValue;
import com.payasia.dao.bean.LundinAFE;
import com.payasia.dao.bean.LundinBlock;
import com.payasia.dao.bean.LundinDepartment;
import com.payasia.dao.bean.LundinTimesheetPreference;
import com.payasia.logic.LundinDepartmentLogic;

@Component
public class LundinDepartmentLogicImpl implements LundinDepartmentLogic {
	/** The Constant logger. */
	private static final Logger LOGGER = Logger
			.getLogger(LundinDepartmentLogicImpl.class);
	@Resource
	LundinDepartmentDAO lundinDepartmentDAO;

	@Resource
	DynamicFormFieldRefValueDAO dynamicFormFieldRefValueDAO;
	@Resource
	CompanyDAO companyDAO;

	@Resource
	AppCodeMasterDAO appCodeMasterDAO;

	@Resource
	LundinTimesheetPreferenceDAO LundinTimesheetPreferenceDAO;

	@Resource
	LundinBlockDAO lundinBlockDAO;
	@Resource
	LundinAFEDAO lundinAFEDAO;

	@Override
	public LundinDepartmentDTO findById(long id) {
		LundinDepartmentDTO lundinDepartmentDTO = new LundinDepartmentDTO();
		LundinDepartment obj = lundinDepartmentDAO.findById(id);

		try {
			DynamicFormFieldRefValue refValue = obj
					.getDynamicFormFieldRefValue();

			if (refValue != null) {
				BeanUtils.copyProperties(lundinDepartmentDTO, obj);
				lundinDepartmentDTO.setDepartmentCode(refValue.getCode());
				lundinDepartmentDTO
						.setDepartmentName(refValue.getDescription());
				lundinDepartmentDTO.setDynamicFieldId(refValue
						.getFieldRefValueId());
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}

		return lundinDepartmentDTO;
	}

	@Override
	public ResponseObjectDTO save(LundinDepartmentDTO departmentDto,
			long companyId) {
		DynamicFormFieldRefValue refValue = dynamicFormFieldRefValueDAO
				.findById(departmentDto.getDynamicFieldId());
		ResponseObjectDTO responseDto = new ResponseObjectDTO();
		if (refValue != null) {

			Company company = companyDAO.findById(companyId);
			LundinDepartment lundinDepartmentObj = new LundinDepartment();

			try {
				boolean duplicateFieldInDB = checkExistInDB(null,
						departmentDto.getDepartmentCode(), companyId);

				if (duplicateFieldInDB) {
					responseDto.setKey(3);
				} else {

					AppCodeMaster appCodeMasterObj = appCodeMasterDAO
							.findById(departmentDto.getSelectedValue());
					appCodeMasterObj.setAppCodeID(departmentDto
							.getSelectedValue());

					lundinDepartmentObj.setDepartmentType(appCodeMasterObj);
					lundinDepartmentObj.setCompany(company);
					lundinDepartmentObj.setDynamicFormFieldRefValue(refValue);

					lundinDepartmentObj
							.setEffectiveDate(DateUtils
									.stringToTimestamp(departmentDto
											.getEffectiveDate()));
					lundinDepartmentObj.setStatus(departmentDto.isStatus());
					lundinDepartmentObj.setOrder(departmentDto.getOrder());
					lundinDepartmentDAO.save(lundinDepartmentObj);
					responseDto.setKey(1);
				}
				return responseDto;
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
				responseDto.setKey(2);
				return responseDto;
			}
		} else {
			responseDto.setSuccess(false);
			responseDto.setKey(2);
			return responseDto;
		}
	}

	private boolean checkExistInDB(Long deptId, String departmentCode,
			long companyId) {
		LundinDepartment lundinDepartmentVO = lundinDepartmentDAO
				.findByDescCompany(deptId, departmentCode, companyId);
		if (lundinDepartmentVO == null) {
			return false;
		} else
			return true;
	}

	@Override
	public ResponseObjectDTO update(LundinDepartmentDTO departmentDto,
			long companyId) {
		DynamicFormFieldRefValue refValue = dynamicFormFieldRefValueDAO
				.findById(departmentDto.getDynamicFieldId());
		ResponseObjectDTO responseDto = new ResponseObjectDTO();
		if (refValue != null) {
			try {

				LundinDepartment lundinDepartmentObj = lundinDepartmentDAO
						.findByDescCompany(departmentDto.getDepartmentId(),departmentDto.getDepartmentCode(),companyId);
				if(lundinDepartmentObj!=null) {
				AppCodeMaster appCodeMasterObj = appCodeMasterDAO
						.findById(departmentDto.getSelectedValue());

				lundinDepartmentObj.setDynamicFormFieldRefValue(refValue);
				lundinDepartmentObj.setDepartmentType(appCodeMasterObj);
				lundinDepartmentObj.setEffectiveDate(DateUtils
						.stringToTimestamp(departmentDto.getEffectiveDate()));
				lundinDepartmentObj.setStatus(departmentDto.isStatus());
				lundinDepartmentObj.setOrder(departmentDto.getOrder());

				LundinBlock defaultBlock = lundinBlockDAO
						.findById(departmentDto.getDefaultBlockId());
				lundinDepartmentObj.setDefaultBlock(defaultBlock);
				LundinAFE defaultAFE = lundinAFEDAO.findById(departmentDto
						.getDefaultAFEId());
				lundinDepartmentObj.setDefaultAFE(defaultAFE);

				lundinDepartmentDAO.update(lundinDepartmentObj);
				responseDto.setKey(1);
				}

				return responseDto;
			}
				catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
				responseDto.setKey(2);
				return responseDto;
			}
				
		} else {
			responseDto.setSuccess(false);
			responseDto.setKey(2);
			return responseDto;
		}
	}

	@Override
	public ResponseObjectDTO delete(long departmentId, long compId) {
		ResponseObjectDTO responseDto = new ResponseObjectDTO();
		try {
			lundinDepartmentDAO.delete(departmentId);
			responseDto.setKey(1);
			return responseDto;
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			responseDto.setKey(2);
			return responseDto;
		}
	}

	@Override
	public LundinDepartmentResponse getDepartmentResponse(String fromDate,
			String toDate, Long compId, PageRequest pageDTO,
			SortCondition sortDTO, String searchCondition, String searchText,
			String transactionType) {

		LundinConditionDTO conditionDTO = new LundinConditionDTO();

		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_CREATED_DATE)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setCreatedDate(searchText.trim());
			}

		}
		if (searchCondition.equals(PayAsiaConstants.Lundin_Department_Code)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setDepartmentCode(searchText.trim());
			}
		}
		if (searchCondition.equals(PayAsiaConstants.Lundin_Department_Name)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setDepartmentName(searchText.trim());
			}
		}
		if (StringUtils.isNotBlank(transactionType)) {
			if (transactionType.equalsIgnoreCase("inActive")) {
				conditionDTO.setTransactionType(transactionType.trim());
				conditionDTO.setStatus(false);
			} else if (transactionType.equalsIgnoreCase("active")) {
				conditionDTO.setTransactionType(transactionType.trim());
				conditionDTO.setStatus(true);
			}
		}

		conditionDTO.setCompanyId(compId);

		if (StringUtils.isNotBlank(fromDate)) {
			conditionDTO.setFromDate(DateUtils.stringToTimestamp(fromDate));
		}

		if (StringUtils.isNotBlank(toDate)) {
			conditionDTO.setToDate(DateUtils.stringToTimestamp(toDate));
		}

		LundinTimesheetPreference lundinTimesheetPreference = LundinTimesheetPreferenceDAO
				.findByCompanyId(compId);
		List<LundinDepartmentDTO> lundinDeptDTOs = new ArrayList<LundinDepartmentDTO>();
		if (lundinTimesheetPreference != null) {
			DataDictionary dd = lundinTimesheetPreference.getDataDictionary();
			if (dd != null) {
				List<DynamicFormFieldRefValue> dynamicFormFieldRefValues = dynamicFormFieldRefValueDAO
						.findByDataDictionayId(dd.getDataDictionaryId(),
								conditionDTO);
				/*
				 * remove the entries which are not present in dynamicForm table
				 * and were already present in lundin table
				 */
				List<LundinDepartment> lundinDepartments = lundinDepartmentDAO
						.getAllEntries(compId);
				for (LundinDepartment lundinDepartment : lundinDepartments) {
					DynamicFormFieldRefValue dfv = lundinDepartment
							.getDynamicFormFieldRefValue();
					if (dfv == null) {
						lundinDepartmentDAO.delete(lundinDepartment
								.getDepartmentId());
					}
				}

				/*
				 * now insert values form dynamicFormField if they are not
				 * present in lundin department table
				 */
				for (DynamicFormFieldRefValue dynamicFormFieldRef : dynamicFormFieldRefValues) {
					LundinDepartment lundinDepartment = lundinDepartmentDAO
							.findByDynamicFieldId(dynamicFormFieldRef
									.getFieldRefValueId());
					LundinDepartmentDTO lundinDeptDto = new LundinDepartmentDTO();
					long lundinDepId = -1;
					if (lundinDepartment == null) {

						lundinDepartment = new LundinDepartment();
						lundinDepartment
								.setCompany(companyDAO.findById(compId));
						lundinDepartment.setDepartmentType(null);
						lundinDepartment
								.setDynamicFormFieldRefValue(dynamicFormFieldRef);
						lundinDepartment.setEffectiveDate(new Timestamp(
								new Date().getTime()));
						lundinDepartment.setOrder(0);
						lundinDepartment.setStatus(false);
						lundinDepId = lundinDepartmentDAO
								.saveAndReturn(lundinDepartment);
						// create new lundin department
					} else {
						lundinDepId = lundinDepartment.getDepartmentId();
					}

					try {
						/*ID ENCRYPT*/
						lundinDeptDto.setDepartmentId(FormatPreserveCryptoUtil.encrypt(lundinDepId));
						lundinDeptDto.setDepartmentCode(dynamicFormFieldRef
								.getCode());
						lundinDeptDto.setDepartmentName(dynamicFormFieldRef
								.getDescription());
						lundinDeptDto.setOrder(lundinDepartment.getOrder());
						if (lundinDepartment.getDepartmentType() != null) {
							lundinDeptDto.setSelectedValue(lundinDepartment
									.getDepartmentType().getAppCodeID());
							lundinDeptDto.setDepartmentType(lundinDepartment
									.getDepartmentType().getCodeDesc());
							lundinDeptDto.setDepartmentTypeId(lundinDepartment
									.getDepartmentType().getAppCodeID());
						}

						lundinDeptDto.setEffectiveDate(DateUtils
								.timeStampToString(lundinDepartment
										.getEffectiveDate()));
						lundinDeptDto.setStatus(lundinDepartment.isStatus());
						lundinDeptDTOs.add(lundinDeptDto);

					} catch (Exception e) {

						LOGGER.error(e.getMessage(), e);
					}
				}
			}
		}

		// List<LundinDepartment> lundinDepartments = lundinDepartmentDAO
		// .findByCondition(conditionDTO, pageDTO, sortDTO);

		LundinDepartmentResponse response = new LundinDepartmentResponse();
		int recordSize = lundinDeptDTOs.size();
		response.setLundinDepartmentDTO(lundinDeptDTOs);

		if (pageDTO != null) {

			int pageSize = pageDTO.getPageSize();
			int totalPages = recordSize / pageSize;

			if (recordSize % pageSize != 0) {
				totalPages = totalPages + 1;
			}
			if (recordSize == 0) {
				pageDTO.setPageNumber(0);
			}

			response.setPage(pageDTO.getPageNumber());
			response.setTotal(totalPages);
			response.setRecords(recordSize);
		}
		return response;
	}

	@Override
	public LundinDepartmentDTO getDepartmentType() {

		LundinDepartmentDTO toReturn = new LundinDepartmentDTO();
		try {
			List<AppCodeMaster> appCodeMasters = appCodeMasterDAO
					.findByCondition(PayAsiaConstants.Lundin_Department_Type);

			Map<Long, String> map = new HashMap<Long, String>();
			map.put(-1L, "Select");

			for (AppCodeMaster appCodeMaster : appCodeMasters) {
				map.put(appCodeMaster.getAppCodeID(),
						appCodeMaster.getCodeDesc());

			}
			toReturn.setAppCodeMaster(map);
			toReturn.setSelectedValue(-1L);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		return toReturn;
	}

	@Override
	public LundinDepartmentDTO getBlockDataById(Long departmetnId) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
        LundinDepartmentDTO lundinDeptDto = new LundinDepartmentDTO();

		try {
			LundinDepartment lundinDeptobj = lundinDepartmentDAO
					.findByDepartmentCompanyId(departmetnId,companyId);
			if(lundinDeptobj!=null)
			{
			DynamicFormFieldRefValue refValue = lundinDeptobj
					.getDynamicFormFieldRefValue();

			if (refValue != null) {
				lundinDeptDto.setDepartmentId(FormatPreserveCryptoUtil.encrypt(lundinDeptobj.getDepartmentId()));
				lundinDeptDto.setDepartmentCode(refValue.getCode());
				lundinDeptDto.setDepartmentName(refValue.getDescription());
				lundinDeptDto.setOrder(lundinDeptobj.getOrder());
				if (lundinDeptobj.getDepartmentType() != null) {
					lundinDeptDto.setSelectedValue(lundinDeptobj
							.getDepartmentType().getAppCodeID());
					lundinDeptDto.setDepartmentType(lundinDeptobj
							.getDepartmentType().getCodeDesc());
					lundinDeptDto.setDepartmentTypeId(lundinDeptobj
							.getDepartmentType().getAppCodeID());
				}
				if (lundinDeptobj.getDefaultBlock() != null) {
					lundinDeptDto.setDefaultBlockId(lundinDeptobj
							.getDefaultBlock().getBlockId());
				}
				if (lundinDeptobj.getDefaultAFE() != null) {
					lundinDeptDto.setDefaultAFEId(lundinDeptobj.getDefaultAFE()
							.getAfeId());
				}

				lundinDeptDto.setDynamicFieldId(lundinDeptobj
						.getDynamicFormFieldRefValue().getFieldRefValueId());
				lundinDeptDto.setEffectiveDate(DateUtils
						.timeStampToString(lundinDeptobj.getEffectiveDate()));
				lundinDeptDto.setStatus(lundinDeptobj.isStatus());
			}
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		return lundinDeptDto;
	}
}
