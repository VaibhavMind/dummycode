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
import com.payasia.common.dto.LundinAFEDTO;
import com.payasia.common.dto.LundinBlockDTO;
import com.payasia.common.dto.LundinConditionDTO;
import com.payasia.common.dto.ResponseObjectDTO;
import com.payasia.common.form.LundinAFEResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.dao.LundinAFEDAO;
import com.payasia.dao.LundinBlockDAO;
import com.payasia.dao.bean.LundinAFE;
import com.payasia.dao.bean.LundinBlock;
import com.payasia.logic.LundinAFELogic;

@Component
public class LundinAFELogicImpl implements LundinAFELogic {
	/** The Constant logger. */
	private static final Logger LOGGER = Logger
			.getLogger(LundinAFELogicImpl.class);

	@Resource
	LundinAFEDAO lundinAFEDAO;

	@Resource
	LundinBlockDAO lundinBlockDao;

	@Override
	public LundinAFEDTO findById(long id) {
		LundinAFEDTO lundinAFEDTO = new LundinAFEDTO();
		LundinAFE obj = lundinAFEDAO.findById(id);
		try {
			BeanUtils.copyProperties(lundinAFEDTO, obj);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}

		return lundinAFEDTO;
	}

	@Override
	public ResponseObjectDTO save(LundinAFEDTO lundinAefDto, long companyId) {

		ResponseObjectDTO responseDto = new ResponseObjectDTO();

		try {

			boolean duplicateFieldInDB = checkExistInDB(null,
					lundinAefDto.getAfeCode(), companyId);

			if (duplicateFieldInDB) {
				responseDto.setKey(3);
			} else {
				LundinAFE lundinAfeObj = new LundinAFE();
				lundinAfeObj.setAfeCode(lundinAefDto.getAfeCode());
				lundinAfeObj.setAfeName(lundinAefDto.getAfeName());
				lundinAfeObj.setEffectiveDate(DateUtils
						.stringToTimestamp(lundinAefDto.getEffectiveDate()));
				lundinAfeObj.setStatus(lundinAefDto.isStatus());
				List<LundinBlock> lundinBlockList = new ArrayList<LundinBlock>();
				for (int count = 0; count < lundinAefDto.getLundinAfeBlock().length; count++) {
					LundinBlock lundinBlockObj = lundinBlockDao
							.findById(lundinAefDto.getLundinAfeBlock()[count]);
					lundinBlockList.add(lundinBlockObj);
				}
				lundinAfeObj.setLundinBlocks(lundinBlockList);
				lundinAFEDAO.save(lundinAfeObj);
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
	public ResponseObjectDTO update(LundinAFEDTO lundinAefDto, long companyId) {

		ResponseObjectDTO responseDto = new ResponseObjectDTO();
		try {
			boolean duplicateFieldInDB = checkExistInDB(lundinAefDto.getAfeId(), lundinAefDto.getAfeCode(),
					companyId);

			if (duplicateFieldInDB) {
				responseDto.setKey(3);
			} else {
				LundinAFE lundinAfeObj = lundinAFEDAO.findById(lundinAefDto
						.getAfeId());
				lundinAfeObj.setAfeCode(lundinAefDto.getAfeCode());
				lundinAfeObj.setAfeName(lundinAefDto.getAfeName());
				lundinAfeObj.setEffectiveDate(DateUtils
						.stringToTimestamp(lundinAefDto.getEffectiveDate()));
				lundinAfeObj.setStatus(lundinAefDto.isStatus());
				List<LundinBlock> lundinBlockList = new ArrayList<LundinBlock>();
				for (int count = 0; count < lundinAefDto.getLundinAfeBlock().length; count++) {
					LundinBlock lundinBlockObj = lundinBlockDao
							.findById(lundinAefDto.getLundinAfeBlock()[count]);
					lundinBlockList.add(lundinBlockObj);
				}
				lundinAfeObj.setLundinBlocks(lundinBlockList);
				lundinAFEDAO.update(lundinAfeObj);
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
	public ResponseObjectDTO delete(long id, long companyId) {
		ResponseObjectDTO responseDto = new ResponseObjectDTO();
		try {
			
			LundinAFE lundinAfeObj = lundinAFEDAO.findById(id,companyId);

			if(lundinAfeObj != null) {
			  lundinAFEDAO.delete(id);
			  responseDto.setKey(1);
			}
			return responseDto;
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			responseDto.setKey(2);
			return responseDto;
		}
	}

	private boolean checkExistInDB(Long afeId, String afeCode, long companyId) {
		LundinAFE lundinAfeObj = lundinAFEDAO.findByAfeCode(afeId, afeCode,
				companyId);
		if (lundinAfeObj == null) {
			return false;
		} else
			return true;
	}

	@Override
	public LundinAFEResponse getAfeResponse(String fromDate, String toDate,
			long companyId, PageRequest pageDTO, SortCondition sortDTO,
			String searchCondition, String searchText, String transactionType) {

		LundinConditionDTO conditionDTO = new LundinConditionDTO();

		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_CREATED_DATE)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setCreatedDate(searchText.trim());
			}

		}
		if (searchCondition.equals(PayAsiaConstants.Lundin_AFE_Code)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setAfeCode(searchText.trim());
			}
		}
		if (searchCondition.equals(PayAsiaConstants.Lundin_AFE_Name)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setAfeName(searchText.trim());
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

		conditionDTO.setCompanyId(companyId);

		if (StringUtils.isNotBlank(fromDate)) {
			conditionDTO.setFromDate(DateUtils.stringToTimestamp(fromDate));
		}

		if (StringUtils.isNotBlank(toDate)) {
			conditionDTO.setToDate(DateUtils.stringToTimestamp(toDate));
		}

		int recordSize = (lundinAFEDAO.getCountForCondition(conditionDTO))
				.intValue();

		List<LundinAFE> lundinAFEs = lundinAFEDAO.findByCondition(conditionDTO,
				pageDTO, sortDTO);

		List<LundinAFEDTO> lundinAFEDTOs = new ArrayList<LundinAFEDTO>();

		for (LundinAFE lundinAfeObj : lundinAFEs) {
			LundinAFEDTO lundinAFEDto = new LundinAFEDTO();

			try {
				/*ID ENCRYPT*/
				lundinAFEDto.setAfeId(FormatPreserveCryptoUtil.encrypt(lundinAfeObj.getAfeId()));
				
				lundinAFEDto.setAfeCode(lundinAfeObj.getAfeCode());
				lundinAFEDto.setAfeName(lundinAfeObj.getAfeName());
				lundinAFEDto.setEffectiveDate(DateUtils
						.timeStampToString(lundinAfeObj.getEffectiveDate()));
				lundinAFEDto.setStatus(lundinAfeObj.isStatus());
				List<LundinBlockDTO> lundinBlockDtos = new ArrayList<LundinBlockDTO>();
				List<LundinBlock> lundinBlocks = lundinAfeObj.getLundinBlocks();

				for (LundinBlock blockObj : lundinBlocks) {
					try {
						LundinBlockDTO lundinBlockDto = new LundinBlockDTO();
						BeanUtils.copyProperties(lundinBlockDto, blockObj);
						lundinBlockDtos.add(lundinBlockDto);
					} catch (Exception e) {
						LOGGER.error(e.getMessage(), e);
					}
				}
				lundinAFEDto.setLundinBlocks(lundinBlockDtos);
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			}
			lundinAFEDTOs.add(lundinAFEDto);
		}

		LundinAFEResponse response = new LundinAFEResponse();

		response.setLundinAFEDTO(lundinAFEDTOs);

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
	public List<LundinBlockDTO> getActiveBlocks(Timestamp effectiveDate,
			long companyId) {
		List<LundinBlock> lundinBlocks = lundinAFEDAO.getActiveBlocksList(
				effectiveDate, companyId);

		List<LundinBlockDTO> lundinBlockDtos = new ArrayList<LundinBlockDTO>();
		for (LundinBlock lundinBlock : lundinBlocks) {
			LundinBlockDTO dto = new LundinBlockDTO();
			dto.setBlockCode(lundinBlock.getBlockCode());
			dto.setBlockId(lundinBlock.getBlockId());
			dto.setBlockName(lundinBlock.getBlockName());
			dto.setEffectiveAllocation(lundinBlock.isEffectiveAllocation());
			dto.setEffectiveDate(lundinBlock.getEffectiveDate().toString());
			dto.setStatus(lundinBlock.isStatus());
			lundinBlockDtos.add(dto);
		}

		return lundinBlockDtos;
	}

	@Override
	public LundinAFEDTO getAfeDataById(Long afeId) {

		LundinAFEDTO lundinAFEDto = new LundinAFEDTO();
		try {
			Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
			LundinAFE lundinAfeObj = lundinAFEDAO.findById(afeId,companyId);
			if(lundinAfeObj!=null) {
				
			lundinAFEDto.setAfeId(FormatPreserveCryptoUtil.encrypt(lundinAfeObj.getAfeId()));
			lundinAFEDto.setAfeCode(lundinAfeObj.getAfeCode());
			lundinAFEDto.setAfeName(lundinAfeObj.getAfeName());
			lundinAFEDto.setEffectiveDate(DateUtils
					.timeStampToString(lundinAfeObj.getEffectiveDate()));
			lundinAFEDto.setStatus(lundinAfeObj.isStatus());
			List<LundinBlockDTO> lundinBlockDtos = new ArrayList<LundinBlockDTO>();
			List<LundinBlock> lundinBlocks = lundinAfeObj.getLundinBlocks();

			for (LundinBlock blockObj : lundinBlocks) {
				try {
					LundinBlockDTO lundinBlockDto = new LundinBlockDTO();
					BeanUtils.copyProperties(lundinBlockDto, blockObj);
					lundinBlockDtos.add(lundinBlockDto);
				} catch (Exception e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
			lundinAFEDto.setLundinBlocks(lundinBlockDtos);
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		return lundinAFEDto;
	}

}
