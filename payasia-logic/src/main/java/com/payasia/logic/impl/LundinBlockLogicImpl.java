package com.payasia.logic.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.LundinAFEWithoutBlockDTO;
import com.payasia.common.dto.LundinBlockDTO;
import com.payasia.common.dto.LundinBlockWithAfeDTO;
import com.payasia.common.dto.LundinConditionDTO;
import com.payasia.common.dto.ResponseObjectDTO;
import com.payasia.common.form.LundinBlockResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.LundinBlockDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.LundinAFE;
import com.payasia.dao.bean.LundinBlock;
import com.payasia.logic.LundinBlockLogic;

@Component
public class LundinBlockLogicImpl implements LundinBlockLogic {

	/** The Constant logger. */
	private static final Logger LOGGER = Logger
			.getLogger(LundinBlockLogicImpl.class);

	@Resource
	LundinBlockDAO lundinBlockDAO;

	@Resource
	CompanyDAO companyDAO;

	@Override
	public LundinBlockDTO findById(long id) {
		LundinBlockDTO lundinBlockDTO = new LundinBlockDTO();
		LundinBlock obj = lundinBlockDAO.findById(id);
		try {
			BeanUtils.copyProperties(lundinBlockDTO, obj);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}

		return lundinBlockDTO;
	}

	@Override
	public ResponseObjectDTO save(LundinBlockDTO lundinBlockDto, long companyId) {

		ResponseObjectDTO responseDto = new ResponseObjectDTO();
		LundinBlock lundinBlockobj = new LundinBlock();
		try {
			boolean duplicateFieldInDB = checkExistInDB(null,
					lundinBlockDto.getBlockCode(), companyId);

			if (duplicateFieldInDB) {
				responseDto.setKey(3);
			} else {
				Company company = companyDAO.findById(companyId);
				lundinBlockobj.setCompany(company);
				lundinBlockobj.setBlockCode(lundinBlockDto.getBlockCode());
				lundinBlockobj.setBlockName(lundinBlockDto.getBlockName());
				lundinBlockobj.setEffectiveAllocation(lundinBlockDto
						.isEffectiveAllocation());
				lundinBlockobj.setEffectiveDate(DateUtils
						.stringToTimestamp(lundinBlockDto.getEffectiveDate()));
				lundinBlockobj.setStatus(lundinBlockDto.isStatus());
				lundinBlockDAO.save(lundinBlockobj);
				responseDto.setKey(1);
			}
			return responseDto;
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			responseDto.setKey(2);
			return responseDto;
		}
	}

	@Override
	public ResponseObjectDTO update(LundinBlockDTO lundinBlockDto,
			long companyId) {
		ResponseObjectDTO responseDto = new ResponseObjectDTO();
		try {
			boolean duplicateFieldInDB = checkExistInDB(
					lundinBlockDto.getBlockId(), lundinBlockDto.getBlockCode(),
					companyId);

			if (duplicateFieldInDB) {
				responseDto.setKey(3);
			} else {
				LundinBlock lundinBlockobj = lundinBlockDAO
						.findById(lundinBlockDto.getBlockId());
				lundinBlockobj.setBlockCode(lundinBlockDto.getBlockCode());
				lundinBlockobj.setBlockName(lundinBlockDto.getBlockName());
				lundinBlockobj.setEffectiveAllocation(lundinBlockDto
						.isEffectiveAllocation());
				lundinBlockobj.setEffectiveDate(DateUtils
						.stringToTimestamp(lundinBlockDto.getEffectiveDate()));
				lundinBlockobj.setStatus(lundinBlockDto.isStatus());
				lundinBlockDAO.update(lundinBlockobj);
				responseDto.setKey(1);
			}
			return responseDto;
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			responseDto.setKey(2);
			return responseDto;
		}
	}

	private boolean checkExistInDB(Long blockId, String blockCode,
			long companyId) {
		LundinBlock lundinBlockVO = lundinBlockDAO.findByDescCompany(blockId,
				blockCode, companyId);
		if (lundinBlockVO == null) {
			return false;
		} else
			return true;
	}

	@Override
	public ResponseObjectDTO delete(long lundinBlockId, long companyId) {
		ResponseObjectDTO responseDto = new ResponseObjectDTO();
		try {
			LundinBlock lundinBlockVO = lundinBlockDAO.findById(lundinBlockId,companyId);
			if (lundinBlockVO != null) {
			lundinBlockDAO.delete(lundinBlockId);
			responseDto.setKey(1);
			}
			return responseDto;
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			responseDto.setKey(2);
			return responseDto;
		}
	}

	@Override
	public LundinBlockResponse getBlockResponse(String fromDate, String toDate,
			Long compId, PageRequest pageDTO, SortCondition sortDTO,
			String searchCondition, String searchText, String transactionType) {

		LundinConditionDTO conditionDTO = new LundinConditionDTO();

		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_CREATED_DATE)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setCreatedDate(searchText.trim());
			}

		}
		if (searchCondition.equals(PayAsiaConstants.Lundin_Block_Code)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setBlockCode(searchText.trim());
			}
		}
		if (searchCondition.equals(PayAsiaConstants.Lundin_Block_Name)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setBlockName(searchText.trim());
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

		int recordSize = (lundinBlockDAO.getCountForCondition(conditionDTO))
				.intValue();

		List<LundinBlock> lundinBlocks = lundinBlockDAO.findByCondition(
				conditionDTO, pageDTO, sortDTO);

		List<LundinBlockDTO> lundinBlockDTOs = new ArrayList<LundinBlockDTO>();

		for (LundinBlock lundinBlockObj : lundinBlocks) {
			LundinBlockDTO lundinBlockDto = new LundinBlockDTO();

			try {
				/*ID ENCRYPT*/
				lundinBlockDto.setBlockId(FormatPreserveCryptoUtil.encrypt(lundinBlockObj.getBlockId()));
				
				lundinBlockDto.setBlockCode(lundinBlockObj.getBlockCode());
				lundinBlockDto.setBlockName(lundinBlockObj.getBlockName());
				lundinBlockDto.setEffectiveAllocation(lundinBlockObj
						.isEffectiveAllocation());
				lundinBlockDto.setEffectiveDate(DateUtils
						.timeStampToString(lundinBlockObj.getEffectiveDate()));
				lundinBlockDto.setStatus(lundinBlockObj.isStatus());
			} catch (Exception e) {

				LOGGER.error(e.getMessage(), e);
			}
			lundinBlockDTOs.add(lundinBlockDto);

		}

		LundinBlockResponse response = new LundinBlockResponse();

		response.setLundinBlockDTO(lundinBlockDTOs);

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
	public List<LundinBlockWithAfeDTO> getBlockAndAfe(Timestamp effectiveDate,
			long companyId) {
		List<LundinBlock> lundinBlocks = lundinBlockDAO.getBlocks(
				effectiveDate, companyId);

		List<LundinBlockWithAfeDTO> lundinBlockDtos = new ArrayList<LundinBlockWithAfeDTO>();
		for (LundinBlock lundinBlock : lundinBlocks) {
			LundinBlockWithAfeDTO dto = new LundinBlockWithAfeDTO();
			dto.setBlockCode(lundinBlock.getBlockCode());
			
			dto.setBlockId(lundinBlock.getBlockId());
			dto.setBlockName(lundinBlock.getBlockName());
			dto.setEffectiveAllocation(lundinBlock.isEffectiveAllocation());
			dto.setEffectiveDate(lundinBlock.getEffectiveDate().toString());
			dto.setStatus(lundinBlock.isStatus());
			List<LundinAFEWithoutBlockDTO> afeDtos = new ArrayList<LundinAFEWithoutBlockDTO>();
			List<LundinAFE> afes = lundinBlock.getLundinAfes();
			for (LundinAFE afe : afes) {
				try {
					if (afe.isStatus()
							&& afe.getEffectiveDate().before(effectiveDate)) {
						LundinAFEWithoutBlockDTO afeDto = new LundinAFEWithoutBlockDTO();
						BeanUtils.copyProperties(afeDto, afe);
						afeDtos.add(afeDto);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			dto.setLundinAfes(afeDtos);
			lundinBlockDtos.add(dto);
		}

		return lundinBlockDtos;
	}

	@Override
	public LundinBlockDTO getBlockDataById(Long blockId) {

		LundinBlockDTO lundinBlockDto = new LundinBlockDTO();
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		try {
			LundinBlock lundinBlock = lundinBlockDAO.findById(blockId,companyId);

			lundinBlockDto.setBlockId(FormatPreserveCryptoUtil.encrypt(lundinBlock.getBlockId()));
			lundinBlockDto.setBlockCode(lundinBlock.getBlockCode());
			lundinBlockDto.setBlockName(lundinBlock.getBlockName());
			lundinBlockDto.setEffectiveAllocation(lundinBlock
					.isEffectiveAllocation());
			lundinBlockDto.setEffectiveDate(DateUtils
					.timeStampToString(lundinBlock.getEffectiveDate()));
			lundinBlockDto.setStatus(lundinBlock.isStatus());
		} catch (Exception e) {

			LOGGER.error(e.getMessage(), e);
		}
		return lundinBlockDto;
	}

}
