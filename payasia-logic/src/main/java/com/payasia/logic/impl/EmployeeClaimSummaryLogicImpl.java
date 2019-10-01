/**
 * @author vivekjain
 *
 */
package com.payasia.logic.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import com.payasia.common.dto.AddClaimDTO;
import com.payasia.common.dto.EmployeeClaimSummaryDTO;
import com.payasia.common.form.EmployeeClaimSummaryResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.EmployeeClaimSummaryComparison;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.dao.ClaimTemplateDAO;
import com.payasia.dao.EmployeeClaimTemplateItemDAO;
import com.payasia.dao.bean.ClaimTemplateItemGeneral;
import com.payasia.dao.bean.EmployeeClaimTemplateItem;
import com.payasia.logic.EmployeeClaimSummaryLogic;

/**
 * The Class EmployeeClaimSummaryLogicImpl.
 */
@Component
public class EmployeeClaimSummaryLogicImpl implements EmployeeClaimSummaryLogic {

	@Autowired
	private MessageSource messageSource;
	@Resource
	ClaimTemplateDAO claimTemplateDAO;
	@Resource
	EmployeeClaimTemplateItemDAO employeeClaimTemplateItemDAO;

	@Override
	public EmployeeClaimSummaryResponse getEmpClaimSummaryDetails(AddClaimDTO claimDTO) {

		EmployeeClaimSummaryResponse employeeClaimSummaryResponse = new EmployeeClaimSummaryResponse();
		String details = "payasia.claim.details";
		String view = "payasia.view";
		List<EmployeeClaimSummaryDTO> employeeClaimSummaryDTOList = claimTemplateDAO.getEmployeeClaimBalanceSummaryProc(claimDTO);

		List<EmployeeClaimSummaryDTO> employeeClaimSummaryFinalDTOList = new ArrayList<EmployeeClaimSummaryDTO>();
		for (EmployeeClaimSummaryDTO employeeClaimSummaryDTO : employeeClaimSummaryDTOList) {

			StringBuilder entitlements = new StringBuilder();
			
			entitlements.append("<span style='float:left;margin-left:40%;margin-right:2%'>"
                    + new BigDecimal(employeeClaimSummaryDTO.getEntitlement()).setScale(2, BigDecimal.ROUND_HALF_UP) + "</span>");		
			
			entitlements
					.append("<span class='Textsmall' style='padding-bottom: 3px;'><a class='alink' style='text-decoration: none;' href='#' onClick = 'viewEmpEntitlementDetails("
							+ FormatPreserveCryptoUtil.encrypt(employeeClaimSummaryDTO.getEmployeeClaimTemplateId()) + ")'>["
							+ messageSource.getMessage(details, new Object[] {}, claimDTO.getLocale()) + "]</a></span>");
			employeeClaimSummaryDTO.setEntitlementDetails(String.valueOf(entitlements));

			StringBuilder adjustments = new StringBuilder();
			
			adjustments.append("<span style='float:left;margin-left:40%;margin-right:2%'>"
					+ employeeClaimSummaryDTO.getAdjustments() + "</span>");
			adjustments
					.append("<span class='Textsmall' style='padding-bottom: 3px;'><a class='alink' style='text-decoration: none;' href='#' onClick = 'viewEmpEntitlementAdjustments("
							+ FormatPreserveCryptoUtil.encrypt(employeeClaimSummaryDTO.getEmployeeClaimTemplateId()) + ")'>["
							+ messageSource.getMessage(view, new Object[] {}, claimDTO.getLocale()) + "]</a></span>");
			
			employeeClaimSummaryDTO.setAdjustments(String.valueOf(adjustments));
			employeeClaimSummaryFinalDTOList.add(employeeClaimSummaryDTO);
		}

		employeeClaimSummaryResponse.setRows(employeeClaimSummaryFinalDTOList);
		return employeeClaimSummaryResponse;
	}

	@Override
	public EmployeeClaimSummaryResponse getEmpClaimSummaryWSDetails(AddClaimDTO claimDTO) {
		EmployeeClaimSummaryResponse employeeClaimSummaryResponse = new EmployeeClaimSummaryResponse();

		List<EmployeeClaimSummaryDTO> employeeClaimSummaryDTOList = claimTemplateDAO
				.getEmployeeClaimBalanceSummaryProc(claimDTO);

		employeeClaimSummaryResponse.setRows(employeeClaimSummaryDTOList);
		return employeeClaimSummaryResponse;
	}

	@Override
	public EmployeeClaimSummaryResponse getEmpClaimTemplateItemEntDetails(AddClaimDTO addClaimDTO,
			PageRequest pageDTO, SortCondition sortDTO) {
		
		List<EmployeeClaimTemplateItem> empClaimTemplateItemList = employeeClaimTemplateItemDAO.findByEmployeeClaimTemplateId(addClaimDTO);
		EmployeeClaimSummaryResponse employeeClaimSummaryResponse = new EmployeeClaimSummaryResponse();
		List<EmployeeClaimSummaryDTO> employeeClaimSummaryDTOList = new ArrayList<EmployeeClaimSummaryDTO>();
		
		for(EmployeeClaimTemplateItem employeeClaimTemplateItem : empClaimTemplateItemList) {
			EmployeeClaimSummaryDTO employeeClaimSummaryDTO = new EmployeeClaimSummaryDTO();
			List<ClaimTemplateItemGeneral> claimTemplateItemGenerals = new ArrayList<>(employeeClaimTemplateItem.getClaimTemplateItem().getClaimTemplateItemGenerals());
			if (!claimTemplateItemGenerals.isEmpty()) {
				ClaimTemplateItemGeneral claimTemplateItemGeneralVO = claimTemplateItemGenerals.get(0);
				if (claimTemplateItemGeneralVO.getEntitlementPerYear().compareTo(BigDecimal.ZERO) != 0) { // Should be != 0)
					employeeClaimSummaryDTO.setEntitlement(String.valueOf(
							claimTemplateItemGeneralVO.getEntitlementPerYear().setScale(2, BigDecimal.ROUND_HALF_UP)));

					employeeClaimSummaryDTO.setClaimItemName(
							employeeClaimTemplateItem.getClaimTemplateItem().getClaimItemMaster().getClaimItemName());
					employeeClaimSummaryDTOList.add(employeeClaimSummaryDTO);
				}
			}
		}
		employeeClaimSummaryResponse.setRows(employeeClaimSummaryDTOList);
		return employeeClaimSummaryResponse;
	}

	@Override
	public BigDecimal getEmpAllClaimItemEntitlement(AddClaimDTO claimDTO) {
		BigDecimal claimItemEntitlement = new BigDecimal(0.0);
		List<EmployeeClaimTemplateItem> empClaimTemplateItemList = employeeClaimTemplateItemDAO
				.findByEmployeeClaimTemplateId(claimDTO);
		for (EmployeeClaimTemplateItem employeeClaimTemplateItem : empClaimTemplateItemList) {
			EmployeeClaimSummaryDTO employeeClaimSummaryDTO = new EmployeeClaimSummaryDTO();
			List<ClaimTemplateItemGeneral> claimTemplateItemGenerals = new ArrayList<>(
					employeeClaimTemplateItem.getClaimTemplateItem().getClaimTemplateItemGenerals());
			if (!claimTemplateItemGenerals.isEmpty()) {
				ClaimTemplateItemGeneral claimTemplateItemGeneralVO = claimTemplateItemGenerals.get(0);
				if (claimTemplateItemGeneralVO!=null && claimTemplateItemGeneralVO.getEntitlementPerYear()!=null && claimTemplateItemGeneralVO.getEntitlementPerYear().compareTo(BigDecimal.ZERO) != 0) {
					employeeClaimSummaryDTO
							.setEntitlement(String.valueOf(claimTemplateItemGeneralVO.getEntitlementPerYear()));
					claimItemEntitlement = claimItemEntitlement.add(claimTemplateItemGeneralVO.getEntitlementPerYear());
				}
			}
		}
		if (claimItemEntitlement.compareTo(BigDecimal.ZERO) != 0) {
			BigDecimal claimItemEntitlements = claimDTO.getEntitlement().subtract(claimItemEntitlement);
			claimItemEntitlements = claimItemEntitlements.setScale(2, BigDecimal.ROUND_HALF_UP);
			return claimItemEntitlements;
		} else {
			claimItemEntitlement = claimDTO.getEntitlement();
		}
		return claimItemEntitlement;
	}
	
	/*
	 * 	NEW METHOD TO IMPLEMENT PAGINATION AND SORTING IN CLAIM BALANCE SUMMARY
	 */
	@Override
	public EmployeeClaimSummaryResponse getEmpClaimSummary(AddClaimDTO claimDTO, PageRequest pageDTO, SortCondition sortDTO) {

		EmployeeClaimSummaryResponse employeeClaimSummaryResponse = new EmployeeClaimSummaryResponse();
		List<EmployeeClaimSummaryDTO> employeeClaimSummaryDTOList = claimTemplateDAO.getEmployeeClaimBalanceSummaryProc(claimDTO);
	 	List<EmployeeClaimSummaryDTO> employeeClaimSummaryFinalDTOList = new ArrayList<EmployeeClaimSummaryDTO>();
		
		for(EmployeeClaimSummaryDTO employeeClaimSummaryDTO : employeeClaimSummaryDTOList) {		
			employeeClaimSummaryDTO.setEmployeeClaimTemplateId(FormatPreserveCryptoUtil.encrypt(employeeClaimSummaryDTO.getEmployeeClaimTemplateId()));;	
			StringBuilder entitlements = new StringBuilder();
        	entitlements.append(new BigDecimal(employeeClaimSummaryDTO.getEntitlement()).setScale(2, BigDecimal.ROUND_HALF_UP));
			employeeClaimSummaryDTO.setEntitlementDetails(String.valueOf(entitlements));
			StringBuilder adjustments = new StringBuilder();
			employeeClaimSummaryDTO.setEntitlement(String.valueOf(entitlements));
			adjustments.append(employeeClaimSummaryDTO.getAdjustments());
			employeeClaimSummaryDTO.setAdjustments(String.valueOf(adjustments));	
			employeeClaimSummaryDTO.setAction(true);
			employeeClaimSummaryFinalDTOList.add(employeeClaimSummaryDTO);
		}
		
		if (sortDTO != null && !employeeClaimSummaryFinalDTOList.isEmpty()) {
			Collections.sort(employeeClaimSummaryFinalDTOList, new EmployeeClaimSummaryComparison(sortDTO.getOrderType(), sortDTO.getColumnName()));
		}
		
		 List<EmployeeClaimSummaryDTO> finalEmpClaimSummaryList = new ArrayList<>();
		 if (pageDTO != null) {
			int pageSize = pageDTO.getPageSize();
			int pageNo = pageDTO.getPageNumber();
			int startPos = (pageSize * (pageNo - 1));
			int endPos = 0;
			int recordSizeFinal = employeeClaimSummaryFinalDTOList.size();
			int totalPages = recordSizeFinal / pageSize;

			if (recordSizeFinal % pageSize != 0) {
				totalPages = totalPages + 1;
			}
			if (recordSizeFinal == 0) {
				pageDTO.setPageNumber(0);
			}

			if ((startPos + pageSize) <= recordSizeFinal){
				endPos = startPos + pageSize;
			} else if (startPos <= recordSizeFinal) {
				endPos = recordSizeFinal;
			}
			finalEmpClaimSummaryList = employeeClaimSummaryFinalDTOList.subList(startPos, endPos);
			
			employeeClaimSummaryResponse.setRows(finalEmpClaimSummaryList);
			employeeClaimSummaryResponse.setTotal(totalPages);
			employeeClaimSummaryResponse.setRecords(recordSizeFinal);
			employeeClaimSummaryResponse.setPage(pageDTO.getPageNumber());
		}
		
		return employeeClaimSummaryResponse;
	}

	@Override
	public EmployeeClaimSummaryResponse getEmpClaimTemplateItemEnt(AddClaimDTO addClaimDTO,PageRequest pageDTO, SortCondition sortDTO) {
		
		List<EmployeeClaimTemplateItem> empClaimTemplateItemList = employeeClaimTemplateItemDAO.findByEmployeeClaimTemplateId(addClaimDTO);
		EmployeeClaimSummaryResponse employeeClaimSummaryResponse = new EmployeeClaimSummaryResponse();
		List<EmployeeClaimSummaryDTO> employeeClaimSummaryDTOList = new ArrayList<EmployeeClaimSummaryDTO>();
		
		for(EmployeeClaimTemplateItem employeeClaimTemplateItem : empClaimTemplateItemList) {
			EmployeeClaimSummaryDTO employeeClaimSummaryDTO = new EmployeeClaimSummaryDTO();
			List<ClaimTemplateItemGeneral> claimTemplateItemGenerals = new ArrayList<>(employeeClaimTemplateItem.getClaimTemplateItem().getClaimTemplateItemGenerals());
			if (!claimTemplateItemGenerals.isEmpty()) {
				ClaimTemplateItemGeneral claimTemplateItemGeneralVO = claimTemplateItemGenerals.get(0);
				if (claimTemplateItemGeneralVO!=null && claimTemplateItemGeneralVO.getEntitlementPerYear()!=null && claimTemplateItemGeneralVO.getEntitlementPerYear().compareTo(BigDecimal.ZERO) != 0) {
					employeeClaimSummaryDTO.setEntitlement(String.valueOf(claimTemplateItemGeneralVO.getEntitlementPerYear().setScale(2, BigDecimal.ROUND_HALF_UP)));
					employeeClaimSummaryDTO.setClaimItemName(employeeClaimTemplateItem.getClaimTemplateItem().getClaimItemMaster().getClaimItemName());
					employeeClaimSummaryDTOList.add(employeeClaimSummaryDTO);
				}
			}
		}
		if (sortDTO != null && !employeeClaimSummaryDTOList.isEmpty()) {
			Collections.sort(employeeClaimSummaryDTOList, new EmployeeClaimSummaryComparison(sortDTO.getOrderType(), sortDTO.getColumnName()));
		}
		
		List<EmployeeClaimSummaryDTO> finalEmpClaimSummaryList = new ArrayList<>();
		
		/*	PAGINATION	*/
		if (pageDTO != null && !employeeClaimSummaryDTOList.isEmpty()) {
			int recordSizeFinal = employeeClaimSummaryDTOList.size();
			int pageSize = pageDTO.getPageSize();
			int pageNo = pageDTO.getPageNumber();
			int startPos = (pageSize * (pageNo - 1));
			int endPos = 0;

			int totalPages = recordSizeFinal / pageSize;

			if (recordSizeFinal % pageSize != 0) {
				totalPages = totalPages + 1;
			}
			if (recordSizeFinal == 0) {
				pageDTO.setPageNumber(0);
			}

			if ((startPos + pageSize) <= recordSizeFinal){
				endPos = startPos + pageSize;
			} else if (startPos < recordSizeFinal) {
				endPos = recordSizeFinal;
			}

			finalEmpClaimSummaryList = employeeClaimSummaryDTOList.subList(startPos, endPos);
			employeeClaimSummaryResponse.setTotal(totalPages);
		}
		employeeClaimSummaryResponse.setPage(pageDTO.getPageNumber());
		employeeClaimSummaryResponse.setRows(finalEmpClaimSummaryList);
		employeeClaimSummaryResponse.setRecords(employeeClaimSummaryDTOList.size());
		
		return employeeClaimSummaryResponse;
	}
	
}
