package com.payasia.logic.impl;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.persistence.Tuple;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.ComparableComparator;
import org.apache.commons.collections.comparators.ReverseComparator;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.AddLeaveConditionDTO;
import com.payasia.common.dto.AppCodeDTO;
import com.payasia.common.dto.ComboValueDTO;
import com.payasia.common.dto.DataImportLogDTO;
import com.payasia.common.dto.EmployeeLeaveSchemeTypeDTO;
import com.payasia.common.dto.EmployeeLeaveSchemeTypeHistoryDTO;
import com.payasia.common.dto.EmployeeShortListDTO;
import com.payasia.common.dto.FilePathGeneratorDTO;
import com.payasia.common.dto.LeaveApplicationAttachmentDTO;
import com.payasia.common.dto.LeaveApplicationReviewerDTO;
import com.payasia.common.dto.LeaveApplicationWorkflowDTO;
import com.payasia.common.dto.LeaveBalanceSummaryConditionDTO;
import com.payasia.common.dto.LeaveCalendarDTO;
import com.payasia.common.dto.LeaveConditionDTO;
import com.payasia.common.dto.LeaveDTO;
import com.payasia.common.dto.LeaveSessionDTO;
import com.payasia.common.dto.PostLeavetransactionDTO;
import com.payasia.common.dto.TeamLeaveDTO;
import com.payasia.common.exception.PayAsiaRollBackDataException;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.AddLeaveForm;
import com.payasia.common.form.AddLeaveFormResponse;
import com.payasia.common.form.EmployeeFilterListForm;
import com.payasia.common.form.LeaveBalanceSummaryForm;
import com.payasia.common.form.LeaveBalanceSummaryResponse;
import com.payasia.common.form.LeavePreferenceForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.EmployeeLeaveDashboardComparison;
import com.payasia.common.util.FileUtils;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.ValidationUtils;
import com.payasia.dao.AppCodeMasterDAO;
import com.payasia.dao.ClaimPreferenceDAO;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.CompanyHolidayCalendarDetailDAO;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.EmployeeDefaultEmailCCDAO;
import com.payasia.dao.EmployeeHolidayCalendarDAO;
import com.payasia.dao.EmployeeLeaveReviewerDAO;
import com.payasia.dao.EmployeeLeaveSchemeDAO;
import com.payasia.dao.EmployeeLeaveSchemeTypeDAO;
import com.payasia.dao.EmployeeLeaveSchemeTypeHistoryDAO;
import com.payasia.dao.EmployeeRoleMappingDAO;
import com.payasia.dao.KeyPayIntLeaveApplicationDAO;
import com.payasia.dao.LeaveApplicationAttachmentDAO;
import com.payasia.dao.LeaveApplicationCustomFieldDAO;
import com.payasia.dao.LeaveApplicationDAO;
import com.payasia.dao.LeaveApplicationReviewerDAO;
import com.payasia.dao.LeaveApplicationWorkflowDAO;
import com.payasia.dao.LeavePreferenceDAO;
import com.payasia.dao.LeaveSchemeTypeAvailingLeaveDAO;
import com.payasia.dao.LeaveSchemeTypeDAO;
import com.payasia.dao.LeaveSessionMasterDAO;
import com.payasia.dao.LeaveStatusMasterDAO;
import com.payasia.dao.LeaveTypeMasterDAO;
import com.payasia.dao.ModuleMasterDAO;
import com.payasia.dao.RoleMasterDAO;
import com.payasia.dao.WorkFlowRuleMasterDAO;
import com.payasia.dao.WorkflowDelegateDAO;
import com.payasia.dao.bean.AppCodeMaster;
import com.payasia.dao.bean.ClaimPreference;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.CompanyEmployeeShortList;
import com.payasia.dao.bean.CompanyHolidayCalendarDetail;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeDefaultEmailCC;
import com.payasia.dao.bean.EmployeeHolidayCalendar;
import com.payasia.dao.bean.EmployeeLeaveReviewer;
import com.payasia.dao.bean.EmployeeLeaveScheme;
import com.payasia.dao.bean.EmployeeLeaveSchemeType;
import com.payasia.dao.bean.EmployeeLeaveSchemeTypeHistory;
import com.payasia.dao.bean.Employee_;
import com.payasia.dao.bean.KeyPayIntLeaveApplication;
import com.payasia.dao.bean.LeaveApplication;
import com.payasia.dao.bean.LeaveApplicationAttachment;
import com.payasia.dao.bean.LeaveApplicationCustomField;
import com.payasia.dao.bean.LeaveApplicationReviewer;
import com.payasia.dao.bean.LeaveApplicationWorkflow;
import com.payasia.dao.bean.LeavePreference;
import com.payasia.dao.bean.LeaveSchemeTypeAvailingLeave;
import com.payasia.dao.bean.LeaveSchemeTypeWorkflow;
import com.payasia.dao.bean.LeaveSessionMaster;
import com.payasia.dao.bean.LeaveStatusMaster;
import com.payasia.dao.bean.LeaveTypeMaster_;
import com.payasia.dao.bean.ModuleMaster;
import com.payasia.dao.bean.WorkFlowRuleMaster;
import com.payasia.dao.bean.WorkflowDelegate;
import com.payasia.logic.AWSS3Logic;
import com.payasia.logic.BaseLogic;
import com.payasia.logic.EmployeeDetailLogic;
import com.payasia.logic.GeneralLogic;
import com.payasia.logic.GeneralMailLogic;
import com.payasia.logic.LeaveBalanceSummaryLogic;

@Component
public class LeaveBalanceSummaryLogicImpl extends BaseLogic implements LeaveBalanceSummaryLogic {

	/** The Constant logger. */
	private static final Logger LOGGER = Logger.getLogger(LeaveBalanceSummaryLogicImpl.class);

	@Resource
	EmployeeLeaveSchemeTypeDAO employeeLeaveSchemeTypeDAO;

	@Resource
	EmployeeLeaveSchemeDAO employeeLeaveSchemeDAO;

	@Resource
	EmployeeDAO employeeDAO;
	@Resource
	LeaveTypeMasterDAO leaveTypeMasterDAO;
	@Resource
	LeaveSchemeTypeDAO leaveSchemeTypeDAO;

	@Resource
	EmployeeLeaveSchemeTypeHistoryDAO employeeLeaveSchemeTypeHistoryDAO;

	@Resource
	AppCodeMasterDAO appCodeMasterDAO;
	@Resource
	RoleMasterDAO roleMasterDAO;
	@Resource
	LeaveSessionMasterDAO leaveSessionMasterDAO;
	@Resource
	GeneralLogic generalLogic;
	@Resource
	EmployeeRoleMappingDAO employeeRoleMappingDAO;

	@Resource
	CompanyDAO companyDAO;
	@Resource
	LeaveApplicationDAO leaveApplicationDAO;
	@Resource
	LeaveStatusMasterDAO leaveStatusMasterDAO;
	@Resource
	LeaveApplicationAttachmentDAO leaveApplicationAttachmentDAO;

	@Resource
	LeaveApplicationWorkflowDAO leaveApplicationWorkflowDAO;
	@Resource
	LeaveApplicationCustomFieldDAO leaveApplicationCustomFieldDAO;

	@Resource
	LeaveApplicationReviewerDAO leaveApplicationReviewerDAO;

	@Resource
	GeneralMailLogic generalMailLogic;
	@Resource
	LeavePreferenceDAO leavePreferenceDAO;
	@Resource
	EmployeeLeaveReviewerDAO employeeLeaveReviewerDAO;

	@Resource
	EmployeeHolidayCalendarDAO employeeHolidayCalendarDAO;

	@Resource
	WorkFlowRuleMasterDAO workFlowRuleMasterDAO;

	@Resource
	WorkflowDelegateDAO workflowDelegateDAO;

	@Resource
	CompanyHolidayCalendarDetailDAO companyHolidayCalendarDetailDAO;

	@Resource
	ModuleMasterDAO moduleMasterDAO;

	@Resource
	ClaimPreferenceDAO claimPreferenceDAO;

	@Resource
	EmployeeDefaultEmailCCDAO employeeDefaultEmailCCDAO;

	@Resource
	MessageSource messageSource;
	@Resource
	LeaveSchemeTypeAvailingLeaveDAO leaveSchemeTypeAvailingLeaveDAO;
	@Resource
	KeyPayIntLeaveApplicationDAO keyPayIntLeaveApplicationDAO;
	@Value("#{payasiaptProperties['payasia.home']}")
	private String downloadPath;

	/** The documents Root Directory Name. */
	@Value("#{payasiaptProperties['payasia.document.root.directory']}")
	private String rootDirectoryName;

	@Value("#{payasiaptProperties['payasia.tax.document.document.name.separator']}")
	private String fileNameSeperator;

	@Resource
	FileUtils fileUtils;

	@Resource
	AWSS3Logic awss3LogicImpl;

	@Value("#{payasiaptProperties['payasia.application.deployment.location']}")
	private String appDeployLocation;

	@Value("#{payasiaptProperties['payasia.employee.image.width']}")
	private Integer employeeImageWidth;

	/** The employee image height. */
	@Value("#{payasiaptProperties['payasia.employee.image.height']}")
	private Integer employeeImageHeight;

	@Resource
	EmployeeDetailLogic employeeDetailLogic;

	public static class StudentComparator implements Comparator<Integer> {
		@Override
		public int compare(Integer s, Integer t) {
			int f = s.compareTo(t);
			return (f != 0) ? f : s.compareTo(t);
		}
	}

	@Override
	public List<EmployeeLeaveSchemeTypeDTO> getDistinctYears(Long companyId) {

		List<Integer> yearList = new ArrayList<>(employeeLeaveSchemeTypeHistoryDAO.getYearList(companyId));

		Calendar now = Calendar.getInstance();
		Integer currentYear = now.get(Calendar.YEAR);
		yearList.add(currentYear);
		yearList.add(currentYear + 1);
		Collections.sort(yearList);
		Set<Integer> yearSet = new LinkedHashSet<>(yearList);

		LeavePreference leavePreference = leavePreferenceDAO.findByCompanyId(companyId);
		
		int startMonth = 0;
		int endMonth = 0;
		if (leavePreference != null) {
			startMonth = Integer.parseInt(String.valueOf(leavePreference.getStartMonth().getMonthId())) - 1;
			endMonth = Integer.parseInt(String.valueOf(leavePreference.getEndMonth().getMonthId()));

		}

		Date date = now.getTime();

		List<EmployeeLeaveSchemeTypeDTO> typeDTOList = new ArrayList<>();
		SimpleDateFormat sdf = new SimpleDateFormat("MMM yyyy");
		for (Integer year : yearSet) {
			boolean isCurrentDateInLeaveCal = false;
			EmployeeLeaveSchemeTypeDTO typeDTO = new EmployeeLeaveSchemeTypeDTO();
			Calendar startCalendar;
			if (startMonth == 0) {
				startCalendar = new GregorianCalendar(year, startMonth, 1, 0, 0, 0);
			} else {
				startCalendar = new GregorianCalendar(year - 1, startMonth, 1, 0, 0, 0);
			}

			String startDate = sdf.format(startCalendar.getTime());
			Calendar endCalendar = new GregorianCalendar(year, endMonth, 1, 0, 0, 0);
			if (endMonth == 0) {
				endCalendar.add(Calendar.YEAR, 1);
			} else {
				endCalendar.add(Calendar.DAY_OF_MONTH, -1);
			}
			String endDate = sdf.format(endCalendar.getTime());
			if (date.after(startCalendar.getTime()) && date.before(endCalendar.getTime())) {
				isCurrentDateInLeaveCal = true;
			}

			typeDTO.setYearKey(startDate + " - " + endDate);
			typeDTO.setYearValue(year);
			typeDTO.setCurrentDateInLeaveCal(isCurrentDateInLeaveCal);
			typeDTOList.add(typeDTO);
		}

		return typeDTOList;
	}

	@Override
	public LeaveBalanceSummaryResponse getEmployeeLeaveSchemeType(int year, String employeeNumber, PageRequest pageDTO,
			SortCondition sortDTO, Long companyId, Long employeeId) {

		LeaveBalanceSummaryResponse response = new LeaveBalanceSummaryResponse();
		List<EmployeeLeaveSchemeTypeDTO> employeeLeaveSchemeTypeDTOList = null;

		Employee employeeVO = employeeDAO.findByNumber(employeeNumber, companyId);

		EmployeeLeaveSchemeTypeDTO employeeLeaveSchemeTypeDTO = new EmployeeLeaveSchemeTypeDTO();
		createLeaveCalendarDate(year, companyId, employeeLeaveSchemeTypeDTO);

		if (employeeVO != null) {
			List<BigInteger> companyShortListEmployeeIds = new ArrayList<BigInteger>();
			EmployeeShortListDTO employeeShortListDTO = generalLogic.getShortListEmployeeIds(employeeId, companyId);
			companyShortListEmployeeIds = employeeShortListDTO.getShortListEmployeeIds();

			if (companyShortListEmployeeIds != null && companyShortListEmployeeIds.size() > 0
					&& companyShortListEmployeeIds
							.contains(new BigInteger(String.valueOf(employeeVO.getEmployeeId())))) {
				employeeLeaveSchemeTypeDTOList = employeeLeaveSchemeTypeDAO.getEmployeeLeaveBalSummaryProc(
						employeeVO.getEmployeeId(), year, employeeLeaveSchemeTypeDTO.getStartDateTime(),
						employeeLeaveSchemeTypeDTO.getEndDateTime());
			}
			if (companyShortListEmployeeIds.size() == 0) {
				employeeLeaveSchemeTypeDTOList = employeeLeaveSchemeTypeDAO.getEmployeeLeaveBalSummaryProc(
						employeeVO.getEmployeeId(), year, employeeLeaveSchemeTypeDTO.getStartDateTime(),
						employeeLeaveSchemeTypeDTO.getEndDateTime());
			}
		}

		if (StringUtils.isNotBlank(sortDTO.getColumnName())) {
			BeanComparator beanComparator = null;

			if ("asc".equalsIgnoreCase(sortDTO.getOrderType())) {
				beanComparator = new BeanComparator(sortDTO.getColumnName());
			} else {
				beanComparator = new BeanComparator(sortDTO.getColumnName(),
						new ReverseComparator(new ComparableComparator()));
			}

			Collections.sort(employeeLeaveSchemeTypeDTOList, beanComparator);

		}

		response.setRows(employeeLeaveSchemeTypeDTOList);
		return response;

	}

	@SuppressWarnings("deprecation")
	@Override
	public void createLeaveCalendarDate(int year, Long companyId,
			EmployeeLeaveSchemeTypeDTO employeeLeaveSchemeTypeDTO) {
		LeavePreference leavePreference = leavePreferenceDAO.findByCompanyId(companyId);
		int startMonth = 0;
		int endMonth = 0;
		if (leavePreference != null) {
			startMonth = Integer.parseInt(String.valueOf(leavePreference.getStartMonth().getMonthId())) - 1;
			endMonth = Integer.parseInt(String.valueOf(leavePreference.getEndMonth().getMonthId()));
		}
		SimpleDateFormat sdf = new SimpleDateFormat(PayAsiaConstants.DEFAULT_DATE_FORMAT);
		Calendar startCalendar;
		if (startMonth == 0) {
			startCalendar = new GregorianCalendar(year, startMonth, 1, 0, 0, 0);
		} else {
			startCalendar = new GregorianCalendar(year - 1, startMonth, 1, 0, 0, 0);
		}
		
		
		
		String startDate = sdf.format(startCalendar.getTime());
		String str[] = startDate.split(" ");
		int startYear = Integer.valueOf(str[2]);
		
		employeeLeaveSchemeTypeDTO
				.setStartDateTime(DateUtils.stringToTimestamp(startDate, PayAsiaConstants.DEFAULT_DATE_FORMAT));
	Date date=	DateUtils.stringToDate(startDate);
	
	employeeLeaveSchemeTypeDTO.setStartYear(startYear);
		
		Calendar endCalendar = new GregorianCalendar(year, endMonth, 1, 0, 0, 0);
		if (endMonth == 0) {
			endCalendar.add(Calendar.YEAR, 1);
		} else {
			endCalendar.add(Calendar.DAY_OF_MONTH, -1);
		}
		String endDate = sdf.format(endCalendar.getTime());
		employeeLeaveSchemeTypeDTO
				.setEndDateTime(DateUtils.stringToTimestamp(endDate, PayAsiaConstants.DEFAULT_DATE_FORMAT));

	}

	@Override
	public LeaveBalanceSummaryResponse getTeamEmployeeLeaveSchemeType(int year, String employeeNumber,
			PageRequest pageDTO, SortCondition sortDTO, Long companyId, Long employeeId, String searchStringEmpId) {

		LeaveBalanceSummaryResponse response = new LeaveBalanceSummaryResponse();
		List<EmployeeLeaveSchemeTypeDTO> employeeLeaveSchemeTypeDTOList = null;

		if (StringUtils.isBlank(searchStringEmpId)) {
			return response;
		}
		Employee employeeVO = employeeDAO.findByID(Long.parseLong(searchStringEmpId));
		ArrayList<String> roleList = new ArrayList<>();

		SecurityContext securityContext = SecurityContextHolder.getContext();
		Authentication authentication = securityContext.getAuthentication();
		for (GrantedAuthority grantedAuthority : authentication.getAuthorities()) {
			if ("ROLE_LEAVE_MANAGER".equals(grantedAuthority.getAuthority())) {
				roleList.add("ROLE_LEAVE_MANAGER");
			}
		}
		List<EmployeeLeaveReviewer> employeeNumberList = null;
		EmployeeLeaveSchemeTypeDTO employeeLeaveSchemeTypeDTO = new EmployeeLeaveSchemeTypeDTO();
		createLeaveCalendarDate(year, companyId, employeeLeaveSchemeTypeDTO);

		if (roleList.contains("ROLE_LEAVE_MANAGER")) {
			EmployeeShortListDTO employeeShortListDTO = generalLogic.getShortListEmployeeIds(employeeId, companyId);
			employeeShortListDTO.setEmployeeShortList(false);
			employeeNumberList = employeeLeaveReviewerDAO.getEmployeeIdsForLeaveReviewer(employeeNumber, companyId,
					employeeId, employeeShortListDTO);

			if (employeeVO != null) {
				if (employeeNumberList != null) {
					employeeLeaveSchemeTypeDTOList = employeeLeaveSchemeTypeDAO.getEmployeeLeaveBalSummaryProc(
							employeeVO.getEmployeeId(), year, employeeLeaveSchemeTypeDTO.getStartDateTime(),
							employeeLeaveSchemeTypeDTO.getEndDateTime());
				}

			}
		} else {
			if (employeeVO != null) {
				employeeLeaveSchemeTypeDTOList = employeeLeaveSchemeTypeDAO.getEmployeeLeaveBalSummaryProc(
						employeeVO.getEmployeeId(), year, employeeLeaveSchemeTypeDTO.getStartDateTime(),
						employeeLeaveSchemeTypeDTO.getEndDateTime());

			}
		}

		if (StringUtils.isNotBlank(sortDTO.getColumnName())) {
			BeanComparator beanComparator = null;

			if ("asc".equalsIgnoreCase(sortDTO.getOrderType())) {
				beanComparator = new BeanComparator(sortDTO.getColumnName());
			} else {
				beanComparator = new BeanComparator(sortDTO.getColumnName(),
						new ReverseComparator(new ComparableComparator()));
			}

			Collections.sort(employeeLeaveSchemeTypeDTOList, beanComparator);

		}

		response.setRows(employeeLeaveSchemeTypeDTOList);
		return response;

	}

	@Override
	public LeaveBalanceSummaryResponse getTeamEmpLeaveSchemeTypeHistoryList(Long leaveSchemeTypeId,
			String postLeaveTypeFilter, int year, String employeeNumber, Long companyId, Long loggedInUser,
			String searchStringEmpId, PageRequest pageDTO, SortCondition sortDTO) {
		Company companyVO = companyDAO.findById(companyId);
		Long employeeId = null;
		LeaveBalanceSummaryResponse response = new LeaveBalanceSummaryResponse();

		if (StringUtils.isNotBlank(employeeNumber)) {
			if (leaveSchemeTypeId != 0) {
				Employee employeeVO = employeeDAO.findByID(Long.parseLong(searchStringEmpId));
				employeeId = employeeVO.getEmployeeId();
			}

		} else {
			employeeId = loggedInUser;
		}
		Integer leaveCancelValidationDays = null;
		LeavePreference leavePreference = leavePreferenceDAO.findByCompanyId(companyId);
		if (leavePreference != null) {
			leaveCancelValidationDays = leavePreference.getCancelLeaveValidationDays();
		}

		List<EmployeeLeaveSchemeTypeHistoryDTO> employeeLeaveSchemeTypeHistoryDTOs = new ArrayList<>();
		EmployeeLeaveSchemeTypeDTO employeeLeaveSchemeTypeDTO = new EmployeeLeaveSchemeTypeDTO();
		createLeaveCalendarDate(year, companyId, employeeLeaveSchemeTypeDTO);
		List<EmployeeLeaveSchemeTypeHistoryDTO> employeeLeaveSchemeTypeHistoryDTOList = employeeLeaveSchemeTypeHistoryDAO
				.getEmployeeLeaveTransDetailProc(employeeId, leaveSchemeTypeId, year, companyVO.getDateFormat(),
						loggedInUser, employeeLeaveSchemeTypeDTO.getStartDateTime(),
						employeeLeaveSchemeTypeDTO.getEndDateTime());
		if (StringUtils.isNotBlank(sortDTO.getColumnName())) {
			BeanComparator beanComparator = null;

			String sortColumnName = sortDTO.getColumnName();
			if ("startdate".equalsIgnoreCase(sortColumnName)) {
				sortColumnName = "startDateT";
			} else if ("enddate".equalsIgnoreCase(sortColumnName)) {
				sortColumnName = "endDateT";
			} else if ("days".equalsIgnoreCase(sortColumnName)) {
				sortColumnName = "days";
			} else if ("type".equalsIgnoreCase(sortColumnName)) {
				sortColumnName = "typeName";
			} else if ("createdDate".equalsIgnoreCase(sortColumnName)) {
				sortColumnName = "createdDate";
			}

			if ("asc".equalsIgnoreCase(sortDTO.getOrderType())) {
				beanComparator = new BeanComparator(sortColumnName);
			} else {
				beanComparator = new BeanComparator(sortColumnName, new ReverseComparator(new ComparableComparator()));
			}

			Collections.sort(employeeLeaveSchemeTypeHistoryDTOList, beanComparator);

		}

		for (EmployeeLeaveSchemeTypeHistoryDTO historyDTO : employeeLeaveSchemeTypeHistoryDTOList) {

			historyDTO.setStartDate(
					DateUtils.timeStampToStringWOTimezone(historyDTO.getStartDateT(), companyVO.getDateFormat()));
			historyDTO.setEndDate(
					DateUtils.timeStampToStringWOTimezone(historyDTO.getEndDateT(), companyVO.getDateFormat()));

			if (leaveCancelValidationDays != null && leaveCancelValidationDays != 0) {

				Calendar calEnd = new GregorianCalendar();
				Date endDate = DateUtils.stringToDate(historyDTO.getStartDate(), companyVO.getDateFormat());
				calEnd.setTime(endDate);
				calEnd.add(Calendar.DATE, leaveCancelValidationDays);
				endDate = calEnd.getTime();

				Date currDate = DateUtils.stringToDate(
						DateUtils.timeStampToString(DateUtils.getCurrentTimestamp(), companyVO.getDateFormat()),
						companyVO.getDateFormat());
				if (currDate.before(endDate) || currDate.equals(endDate)) {
					historyDTO.setIsValidDateForLeaveCancel(true);
				} else {
					historyDTO.setIsValidDateForLeaveCancel(false);
				}
			}
			/* ID ENCRYPT */
			historyDTO.setLeaveApplicationId(FormatPreserveCryptoUtil.encrypt(historyDTO.getLeaveApplicationId()));
			historyDTO.setAction(true);
			if (leaveCancelValidationDays == null) {
				historyDTO.setIsValidDateForLeaveCancel(false);
			}

			if (leaveCancelValidationDays != null && leaveCancelValidationDays == 0) {
				historyDTO.setIsValidDateForLeaveCancel(true);
			}
			if (postLeaveTypeFilter.toUpperCase().equals(historyDTO.getType().toUpperCase())) {
				employeeLeaveSchemeTypeHistoryDTOs.add(historyDTO);
			}
			if (postLeaveTypeFilter.equals("0")) {
				employeeLeaveSchemeTypeHistoryDTOs.add(historyDTO);
			}
			if (historyDTO.getType().equalsIgnoreCase(PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_APPROVED)) {
				historyDTO.setType("payasia.approved");
			}
			if (historyDTO.getType().equalsIgnoreCase(PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_APPROVED_CANCEL)) {
				historyDTO.setType("payasia.approved.cancel");
			}
			if (historyDTO.getType().equalsIgnoreCase(PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_BALANCE)) {
				historyDTO.setType("payasia.balance");
			}
			if (historyDTO.getType().equalsIgnoreCase(PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_ENCHASED)) {
				historyDTO.setType("payasia.encashed");
			}
			if (historyDTO.getType().equalsIgnoreCase(PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_CARRIED_FORWARD)) {
				historyDTO.setType("payasia.carried.forward");
			}
			if (historyDTO.getType().equalsIgnoreCase(PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_SUBMITTED)) {
				historyDTO.setType("payasia.submitted");
			}
			if (historyDTO.getType().equalsIgnoreCase(PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_WITHDRAWN)) {
				historyDTO.setType("payasia.withdrawn");
			}
			if (historyDTO.getType().equalsIgnoreCase(PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_REJECTED)) {
				historyDTO.setType("payasia.rejected");
			}
			if (historyDTO.getType().equalsIgnoreCase(PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_SUBMITTED_CANCEL)) {
				historyDTO.setType("payasia.submitted.cancel");
			}
			if (historyDTO.getType().equalsIgnoreCase(PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_APPROVED_CANCEL)) {
				historyDTO.setType("payasia.approved.cancel");
			}
			if (historyDTO.getType().equalsIgnoreCase(PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_CREDITED)) {
				historyDTO.setType("payasia.credited");
			}
			if (historyDTO.getType().equalsIgnoreCase(PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_FORFEITED)) {
				historyDTO.setType("payasia.forfeited");
			}
		}

		if (pageDTO != null && !employeeLeaveSchemeTypeHistoryDTOList.isEmpty()) {
			response.setEmpLeaveSchemeTypeHistoryList(employeeLeaveSchemeTypeHistoryDTOs);
		}
		return response;
	}

	@Override
	public LeaveBalanceSummaryResponse getEmpLeaveSchemeTypeHistoryList(Long leaveSchemeTypeId,
			String postLeaveTypeFilter, int year, String employeeNumber, Long companyId, Long loggedInUser,
			PageRequest pageDTO, SortCondition sortDTO, boolean isAdmin) {
		LeaveBalanceSummaryResponse response = new LeaveBalanceSummaryResponse();
		Company companyVO = companyDAO.findById(companyId);
		if (leaveSchemeTypeId == 0) {
			return response;
		}
		Employee employeeVO = null;
		if (StringUtils.isNotBlank(employeeNumber)) {
			if (!isAdmin) {
				employeeVO = employeeDAO.findByNumberEmp(employeeNumber, companyId, loggedInUser);
			} else {
				employeeVO = employeeDAO.findByNumber(employeeNumber, companyId);
			}
		}
		Integer leaveCancelValidationDays = null;
		LeavePreference leavePreference = leavePreferenceDAO.findByCompanyId(companyId);
		if (leavePreference != null) {
			leaveCancelValidationDays = leavePreference.getCancelLeaveValidationDays();
		}

		List<EmployeeLeaveSchemeTypeHistoryDTO> employeeLeaveSchemeTypeHistoryDTOs = new ArrayList<>();
		EmployeeLeaveSchemeTypeDTO employeeLeaveSchemeTypeDTO = new EmployeeLeaveSchemeTypeDTO();
		createLeaveCalendarDate(year, companyId, employeeLeaveSchemeTypeDTO);
		List<EmployeeLeaveSchemeTypeHistoryDTO> employeeLeaveSchemeTypeHistoryDTOList = employeeLeaveSchemeTypeHistoryDAO
				.getEmployeeLeaveTransDetailProc(employeeVO.getEmployeeId(), leaveSchemeTypeId, year,
						companyVO.getDateFormat(), loggedInUser, employeeLeaveSchemeTypeDTO.getStartDateTime(),
						employeeLeaveSchemeTypeDTO.getEndDateTime());
		if (StringUtils.isNotBlank(sortDTO.getColumnName())) {
			BeanComparator beanComparator = null;

			String sortColumnName = sortDTO.getColumnName();
			if ("startdate".equalsIgnoreCase(sortColumnName)) {
				sortColumnName = "startDateT";
			} else if ("enddate".equalsIgnoreCase(sortColumnName))
				sortColumnName = "endDateT";

			if ("asc".equalsIgnoreCase(sortDTO.getOrderType())) {
				beanComparator = new BeanComparator(sortColumnName);
			} else {
				beanComparator = new BeanComparator(sortColumnName, new ReverseComparator(new ComparableComparator()));
			}

			Collections.sort(employeeLeaveSchemeTypeHistoryDTOList, beanComparator);

		}
		for (EmployeeLeaveSchemeTypeHistoryDTO historyDTO : employeeLeaveSchemeTypeHistoryDTOList) {
			historyDTO.setStartDate(
					DateUtils.timeStampToStringWOTimezone(historyDTO.getStartDateT(), companyVO.getDateFormat()));
			historyDTO.setEndDate(
					DateUtils.timeStampToStringWOTimezone(historyDTO.getEndDateT(), companyVO.getDateFormat()));
			historyDTO.setAction(true);
			if (historyDTO.getLeaveApplicationId() != 0) {
				LeaveSchemeTypeAvailingLeave leaveSchemeTypeAvailingLeave = leaveSchemeTypeAvailingLeaveDAO
						.findByLeaveAppliationId(historyDTO.getLeaveApplicationId());
				if (leaveSchemeTypeAvailingLeave != null && leaveSchemeTypeAvailingLeave.isLeaveExtension()) {

					historyDTO.setEmployeeLeaveSchemeTypeId(
							leaveApplicationDAO.findById(historyDTO.getLeaveApplicationId())
									.getEmployeeLeaveSchemeType().getEmployeeLeaveSchemeTypeId());
					historyDTO.setLeaveExtension(true);
				} else {
					historyDTO.setLeaveExtension(false);
				}
			} else {
				historyDTO.setLeaveExtension(false);
			}

			if (leaveCancelValidationDays != null && leaveCancelValidationDays != 0) {

				Calendar calEnd = new GregorianCalendar();
				Date endDate = DateUtils.stringToDate(historyDTO.getStartDate(), companyVO.getDateFormat());
				calEnd.setTime(endDate);
				calEnd.add(Calendar.DATE, leaveCancelValidationDays);
				endDate = calEnd.getTime();

				Date currDate = DateUtils.stringToDate(
						DateUtils.timeStampToString(DateUtils.getCurrentTimestamp(), companyVO.getDateFormat()),
						companyVO.getDateFormat());
				if (currDate.before(endDate) || currDate.equals(endDate)) {
					historyDTO.setIsValidDateForLeaveCancel(true);
				} else {
					historyDTO.setIsValidDateForLeaveCancel(false);
				}
			}

			/* ID ENCRYPT */

			historyDTO.setLeaveApplicationId(FormatPreserveCryptoUtil.encrypt(historyDTO.getLeaveApplicationId()));
			if (leaveCancelValidationDays == null) {
				historyDTO.setIsValidDateForLeaveCancel(false);
			}

			if (leaveCancelValidationDays != null && leaveCancelValidationDays == 0) {
				historyDTO.setIsValidDateForLeaveCancel(true);
			}
			if (postLeaveTypeFilter.toUpperCase().equals(historyDTO.getType().toUpperCase())) {
				employeeLeaveSchemeTypeHistoryDTOs.add(historyDTO);
			}
			if (postLeaveTypeFilter.equals("0")) {
				employeeLeaveSchemeTypeHistoryDTOs.add(historyDTO);
			}

			if (historyDTO.getType().equalsIgnoreCase(PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_APPROVED)) {
				historyDTO.setType("payasia.approved");
			}
			if (historyDTO.getType().equalsIgnoreCase(PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_APPROVED_CANCEL)) {
				historyDTO.setType("payasia.approved.cancel");
			}
			if (historyDTO.getType().equalsIgnoreCase(PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_BALANCE)) {
				historyDTO.setType("payasia.balance");
			}
			if (historyDTO.getType().equalsIgnoreCase(PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_ENCHASED)) {
				historyDTO.setType("payasia.encashed");
			}
			if (historyDTO.getType().equalsIgnoreCase(PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_CARRIED_FORWARD)) {
				historyDTO.setType("payasia.carried.forward");
			}
			if (historyDTO.getType().equalsIgnoreCase(PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_SUBMITTED)) {
				historyDTO.setType("payasia.submitted");
			}
			if (historyDTO.getType().equalsIgnoreCase(PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_WITHDRAWN)) {
				historyDTO.setType("payasia.withdrawn");
			}
			if (historyDTO.getType().equalsIgnoreCase(PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_REJECTED)) {
				historyDTO.setType("payasia.rejected");
			}
			if (historyDTO.getType().equalsIgnoreCase(PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_SUBMITTED_CANCEL)) {
				historyDTO.setType("payasia.submitted.cancel");
			}
			if (historyDTO.getType().equalsIgnoreCase(PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_APPROVED_CANCEL)) {
				historyDTO.setType("payasia.approved.cancel");
			}
			if (historyDTO.getType().equalsIgnoreCase(PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_CREDITED)) {
				historyDTO.setType("payasia.credited");
			}
			if (historyDTO.getType().equalsIgnoreCase(PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_FORFEITED)) {
				historyDTO.setType("payasia.forfeited");
			}

		}

		response.setEmpLeaveSchemeTypeHistoryList(employeeLeaveSchemeTypeHistoryDTOs);
		return response;

	}

	@Override
	public LeaveBalanceSummaryForm getEmployeeSchemeDetail(String empNumber, Long companyId) {
		LeaveBalanceSummaryForm leaveBalanceSummaryForm = new LeaveBalanceSummaryForm();
		Employee employee = employeeDAO.findByNumber(empNumber, companyId);

		if (employee == null) {
			leaveBalanceSummaryForm.setResponseString(PayAsiaConstants.LEAVE_BALANCE_EMPLOYEE_ERROR);
			return leaveBalanceSummaryForm;
		}

		EmployeeLeaveScheme employeeLeaveScheme = employeeLeaveSchemeDAO.findByActive(employee.getEmployeeId(), true);

		if (employeeLeaveScheme == null) {
			leaveBalanceSummaryForm.setResponseString(PayAsiaConstants.LEAVE_BALANCE_SCHEME_ERROR);
			return leaveBalanceSummaryForm;
		}

		leaveBalanceSummaryForm.setEmployeeLeaveSchemeId(employeeLeaveScheme.getEmployeeLeaveSchemeId());
		leaveBalanceSummaryForm.setLeaveScheme(employeeLeaveScheme.getLeaveScheme().getSchemeName());
		leaveBalanceSummaryForm.setEmployeeName(getEmployeeName(employee));

		return leaveBalanceSummaryForm;
	}

	private String getEmployeeName(Employee employee) {
		String employeeName = employee.getFirstName();
		if (employee.getMiddleName() != null) {
			employeeName = employeeName + " " + employee.getMiddleName();
		}
		if (employee.getLastName() != null) {
			employeeName = employeeName + " " + employee.getLastName();
		}
		employeeName = employeeName + " (" + employee.getEmployeeNumber() + ")";
		return employeeName;
	}

	@Override
	public EmployeeLeaveSchemeTypeDTO getDataForSchemeType(Long empLeaveSchemeTypeId) {
		EmployeeLeaveSchemeType employeeLeaveSchemeType = employeeLeaveSchemeTypeDAO.findById(empLeaveSchemeTypeId);
		EmployeeLeaveSchemeTypeDTO employeeLeaveSchemeTypeDTO = new EmployeeLeaveSchemeTypeDTO();
		employeeLeaveSchemeTypeDTO.setBalance(employeeLeaveSchemeType.getBalance());
		return employeeLeaveSchemeTypeDTO;
	}

	public LeaveBalanceSummaryResponse savePostTransactionLeaveType(
			EmployeeLeaveSchemeTypeHistoryDTO employeeLeaveSchemeTypeHistoryDTO, Long companyId, Long employeeId,
			boolean isAdmin) {
		LeaveBalanceSummaryResponse leaveBalanceSummaryResponse = new LeaveBalanceSummaryResponse();
		LeaveConditionDTO leaveConditionDTO = new LeaveConditionDTO();
		boolean isLeaveUnitDays = isLeaveUnitDays(companyId);
		if (!isLeaveUnitDays) {
			employeeLeaveSchemeTypeHistoryDTO.setFromSessionId(1l);
			employeeLeaveSchemeTypeHistoryDTO.setToSessionId(2l);
			leaveConditionDTO.setTotalHoursBetweenDates(employeeLeaveSchemeTypeHistoryDTO.getDays().floatValue());
			leaveConditionDTO.setLeaveUnitHours(true);
		}

		EmployeeLeaveSchemeType employeeLeaveSchemeType = null;
		if (!isAdmin) {
			employeeLeaveSchemeType = employeeLeaveSchemeTypeDAO.findByleaveSchemeTypeIdAndCompanyIdAndEmpId(
					employeeLeaveSchemeTypeHistoryDTO.getEmployeeLeaveSchemeTypeId(), companyId, employeeId);
			if (employeeLeaveSchemeType == null) {
				throw new PayAsiaSystemException("Authentication Exception");
			}
		} else {
			employeeLeaveSchemeType = employeeLeaveSchemeTypeDAO.findSchemeTypeByCompanyId(
					employeeLeaveSchemeTypeHistoryDTO.getEmployeeLeaveSchemeTypeId(), companyId);
			if (employeeLeaveSchemeType == null) {
				throw new PayAsiaSystemException("Authentication Exception");
			}
		}
		AppCodeMaster transactionType = appCodeMasterDAO
				.findById(employeeLeaveSchemeTypeHistoryDTO.getTransactionType());

		LeaveApplication persistLeaveApplication = null;
		if (transactionType.getCodeDesc().equals(PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_APPROVED)) {

			leaveConditionDTO
					.setEmployeeId(employeeLeaveSchemeType.getEmployeeLeaveScheme().getEmployee().getEmployeeId());
			leaveConditionDTO.setEmployeeLeaveSchemeTypeId(employeeLeaveSchemeType.getEmployeeLeaveSchemeTypeId());
			leaveConditionDTO.setStartDate(employeeLeaveSchemeTypeHistoryDTO.getStartDate());
			leaveConditionDTO.setEndDate(employeeLeaveSchemeTypeHistoryDTO.getEndDate());
			leaveConditionDTO.setStartSession(employeeLeaveSchemeTypeHistoryDTO.getFromSessionId());
			leaveConditionDTO.setEndSession(employeeLeaveSchemeTypeHistoryDTO.getToSessionId());
			leaveConditionDTO.setAttachementStatus(false);
			leaveConditionDTO.setPost(true);

			LeaveDTO leaveDTOValidate = leaveApplicationDAO.validateLeaveApplication(leaveConditionDTO);
			leaveBalanceSummaryResponse.setLeaveDTO(leaveDTOValidate);
			if (leaveDTOValidate.getErrorCode() == 1) {
				return leaveBalanceSummaryResponse;

			}

			// List<EmployeeLeaveReviewer> employeeLeaveReviewer =
			// employeeLeaveReviewerDAO
			// .findByCondition(employeeLeaveSchemeType
			// .getEmployeeLeaveScheme().getEmployee()
			// .getEmployeeId(), employeeLeaveSchemeType
			// .getEmployeeLeaveScheme()
			// .getEmployeeLeaveSchemeId(),
			// employeeLeaveSchemeType
			// .getEmployeeLeaveSchemeTypeId(), companyId);
			// if (employeeLeaveReviewer == null) {
			// leaveDTOValidate
			// .setErrorKey("payasia.leave.reviewer.not.defined");
			// leaveDTOValidate.setErrorValue("0");
			// leaveDTOValidate.setErrorCode(1);
			// leaveBalanceSummaryResponse.setLeaveDTO(leaveDTOValidate);
			// return leaveBalanceSummaryResponse;
			// }

			if (isLeaveUnitDays) {
				LeaveDTO leaveDTO = new LeaveDTO();
				leaveDTO.setFromDate(employeeLeaveSchemeTypeHistoryDTO.getStartDate());
				leaveDTO.setToDate(employeeLeaveSchemeTypeHistoryDTO.getEndDate());
				leaveDTO.setSession1(employeeLeaveSchemeTypeHistoryDTO.getFromSessionId());
				leaveDTO.setSession2(employeeLeaveSchemeTypeHistoryDTO.getToSessionId());
				leaveDTO.setEmployeeLeaveSchemeTypeId(employeeLeaveSchemeType.getEmployeeLeaveSchemeTypeId());
				AddLeaveForm noOfDays = getNoOfDays(null, null, leaveDTO);
				employeeLeaveSchemeTypeHistoryDTO.setDays(noOfDays.getNoOfDays());
			}

			persistLeaveApplication = saveLeaveApplication(employeeLeaveSchemeTypeHistoryDTO, companyId, employeeId,
					employeeLeaveSchemeType);
			// Add Leave Application to keyPay Sync table name
			// 'KeyPay_Int_Leave_Application'
			if (!isLeaveUnitDays) {
				addLeaveAppToKeyPayInt(persistLeaveApplication);
			}

			// Insert Forfeit leave entry in EmployeeLeaveSchemeTypeHistory
			// table, only if current leave taken by Consider Leave Balance from
			// Other leave
			forfeitFromOtherLeaveType(persistLeaveApplication,
					employeeLeaveSchemeType.getLeaveSchemeType().getLeaveSchemeTypeId());
		}

		EmployeeLeaveSchemeTypeHistory employeeLeaveSchemeTypeHistory = new EmployeeLeaveSchemeTypeHistory();
		if (employeeLeaveSchemeTypeHistoryDTO.getFromSessionId() != 0) {
			LeaveSessionMaster leaveStartSessionMaster = leaveSessionMasterDAO
					.findById(employeeLeaveSchemeTypeHistoryDTO.getFromSessionId());
			employeeLeaveSchemeTypeHistory.setStartSessionMaster(leaveStartSessionMaster);
		}
		if (employeeLeaveSchemeTypeHistoryDTO.getToSessionId() != 0) {
			LeaveSessionMaster leaveToSessionMaster = leaveSessionMasterDAO
					.findById(employeeLeaveSchemeTypeHistoryDTO.getToSessionId());
			employeeLeaveSchemeTypeHistory.setEndSessionMaster(leaveToSessionMaster);
		}
		employeeLeaveSchemeTypeHistory.setLeaveApplication(persistLeaveApplication);
		if (persistLeaveApplication != null) {
			employeeLeaveSchemeTypeHistory.setLeaveStatusMaster(persistLeaveApplication.getLeaveStatusMaster());
		}
		employeeLeaveSchemeTypeHistory.setAppCodeMaster(transactionType);
		employeeLeaveSchemeTypeHistory.setDays(employeeLeaveSchemeTypeHistoryDTO.getDays());
		employeeLeaveSchemeTypeHistory.setEmployeeLeaveSchemeType(employeeLeaveSchemeType);
		employeeLeaveSchemeTypeHistory
				.setEndDate(DateUtils.stringToTimestamp(employeeLeaveSchemeTypeHistoryDTO.getEndDate()));
		employeeLeaveSchemeTypeHistory
				.setStartDate(DateUtils.stringToTimestamp(employeeLeaveSchemeTypeHistoryDTO.getStartDate()));
		employeeLeaveSchemeTypeHistory.setReason(employeeLeaveSchemeTypeHistoryDTO.getReason());
		employeeLeaveSchemeTypeHistory.setForfeitAtEndDate(employeeLeaveSchemeTypeHistoryDTO.getForfeitAtEndDate());
		employeeLeaveSchemeTypeHistoryDAO.save(employeeLeaveSchemeTypeHistory);

		return leaveBalanceSummaryResponse;
	}

	@Override
	public void addLeaveAppToKeyPayInt(LeaveApplication leaveApplication) {
		KeyPayIntLeaveApplication keyPayIntLeaveApplication = new KeyPayIntLeaveApplication();
		keyPayIntLeaveApplication.setLeaveApplication(leaveApplication);
		keyPayIntLeaveApplication.setCompany(leaveApplication.getCompany());
		keyPayIntLeaveApplication.setEmployeeNumber(leaveApplication.getEmployee().getEmployeeNumber());
		keyPayIntLeaveApplication.setStartDate(leaveApplication.getStartDate());
		keyPayIntLeaveApplication.setEndDate(leaveApplication.getEndDate());
		keyPayIntLeaveApplication.setHours(new BigDecimal(leaveApplication.getTotalDays()));
		keyPayIntLeaveApplication.setLeaveTypeName(leaveApplication.getEmployeeLeaveSchemeType().getLeaveSchemeType()
				.getLeaveTypeMaster().getLeaveTypeName());
		keyPayIntLeaveApplication.setRemarks(leaveApplication.getReason());
		if (leaveApplication.getLeaveCancelApplication() == null) {
			keyPayIntLeaveApplication.setLeaveStatus(PayAsiaConstants.LEAVE_STATUS_APPROVED);
		} else {
			keyPayIntLeaveApplication.setLeaveStatus(PayAsiaConstants.LEAVE_STATUS_CANCELLED);
			keyPayIntLeaveApplication
					.setCancelLeaveApplicationId(leaveApplication.getLeaveCancelApplication().getLeaveApplicationId());
		}
		keyPayIntLeaveApplication.setSyncStatus(PayAsiaConstants.PAYASIA_KEYPAY_INT_LEAVE_SYNC_STATUS_UNPROCESS);
		keyPayIntLeaveApplicationDAO.save(keyPayIntLeaveApplication);
	}

	@Override
	public boolean isLeaveUnitDays(Long companyId) {
		boolean isLeaveUnitDays = true;
		LeavePreference leavePreferenceVO = leavePreferenceDAO.findByCompanyId(companyId);
		if (leavePreferenceVO != null && leavePreferenceVO.getLeaveUnit() != null && leavePreferenceVO.getLeaveUnit()
				.getCodeDesc().equalsIgnoreCase(PayAsiaConstants.PAYASIA_LEAVE_UNIT_IN_HOURS)) {
			isLeaveUnitDays = false;

		}
		return isLeaveUnitDays;
	}

	@Override
	public void forfeitFromOtherLeaveType(LeaveApplication leaveApplication, Long leaveSchemeTypeId) {
		LeaveSchemeTypeAvailingLeave leaveSchemeTypeAvailingLeave = leaveSchemeTypeAvailingLeaveDAO
				.findByLeaveSchemeType(leaveSchemeTypeId);
		if (leaveSchemeTypeAvailingLeave != null
				&& leaveSchemeTypeAvailingLeave.getConsiderLeaveBalanceFrom() != null) {
			EmployeeLeaveSchemeType empLeaveSchemeTypeConsiderFrom = employeeLeaveSchemeTypeDAO
					.findByLeaveSchIdAndEmpId(leaveApplication.getEmployee().getEmployeeId(),
							leaveSchemeTypeAvailingLeave.getConsiderLeaveBalanceFrom().getLeaveSchemeTypeId());
			if (empLeaveSchemeTypeConsiderFrom != null) {
				EmployeeLeaveSchemeTypeHistory empLeaveSchemeTypeHistory = new EmployeeLeaveSchemeTypeHistory();
				empLeaveSchemeTypeHistory.setEmployeeLeaveSchemeType(empLeaveSchemeTypeConsiderFrom);
				AppCodeMaster appcodeForfeit = appCodeMasterDAO.findByCategoryAndDesc(
						PayAsiaConstants.APP_CODE_LEAVE_TRANSACTION_TYPE,
						PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_FORFEITED);
				empLeaveSchemeTypeHistory.setAppCodeMaster(appcodeForfeit);
				empLeaveSchemeTypeHistory.setReason(leaveApplication.getReason());
				empLeaveSchemeTypeHistory.setLeaveApplication(leaveApplication);
				empLeaveSchemeTypeHistory.setDays(BigDecimal.valueOf(leaveApplication.getTotalDays()));
				empLeaveSchemeTypeHistory.setStartDate(leaveApplication.getStartDate());
				empLeaveSchemeTypeHistory.setEndDate(leaveApplication.getEndDate());
				empLeaveSchemeTypeHistory.setStartSessionMaster(leaveApplication.getLeaveSessionMaster1());
				empLeaveSchemeTypeHistory.setEndSessionMaster(leaveApplication.getLeaveSessionMaster2());

				employeeLeaveSchemeTypeHistoryDAO.save(empLeaveSchemeTypeHistory);
			}
		}
	}

	private LeaveApplication saveLeaveApplicationForImport(PostLeavetransactionDTO postLeavetransactionDTO,
			Company company, Employee employee, EmployeeLeaveSchemeType employeeLeaveSchemeType,
			Map<String, LeaveStatusMaster> leaveStatusMasterMap, Map<String, LeaveSessionMaster> leaveSessionMasterMap,
			WorkFlowRuleMaster reviewer1WorkFlowRuleMaster, Employee loggedInEmployee, boolean isLeaveUnitDays) {

		employeeLeaveReviewerDAO.findByCondition(employee.getEmployeeId(),
				employeeLeaveSchemeType.getEmployeeLeaveScheme().getEmployeeLeaveSchemeId(),
				employeeLeaveSchemeType.getEmployeeLeaveSchemeTypeId(), company.getCompanyId());

		LeaveApplication leaveApplication = new LeaveApplication();
		leaveApplication.setCompany(company);

		leaveApplication.setEmployee(employeeLeaveSchemeType.getEmployeeLeaveScheme().getEmployee());
		leaveApplication.setEndDate(DateUtils.stringToTimestamp(postLeavetransactionDTO.getEndDate(),
				PayAsiaConstants.SERVER_SIDE_DATE_FORMAT));
		leaveApplication.setStartDate(DateUtils.stringToTimestamp(postLeavetransactionDTO.getStartDate(),
				PayAsiaConstants.SERVER_SIDE_DATE_FORMAT));
		leaveApplication.setReason(postLeavetransactionDTO.getReason());
		leaveApplication.setApplyTo("");
		LeaveStatusMaster leaveStatusCompleted = leaveStatusMasterMap.get(PayAsiaConstants.LEAVE_STATUS_COMPLETED);
		leaveApplication.setLeaveStatusMaster(leaveStatusCompleted);
		leaveApplication.setCreatedDate(DateUtils.getCurrentTimestampWithTime());

		if (isLeaveUnitDays) {
			leaveApplication.setTotalDays(Float.parseFloat(postLeavetransactionDTO.getDays().toString()));
		} else {
			leaveApplication.setTotalDays(Float.parseFloat(postLeavetransactionDTO.getHours().toString()));
		}

		if (StringUtils.isNotBlank(postLeavetransactionDTO.getStartSession())) {
			LeaveSessionMaster leaveStartSessionMaster = leaveSessionMasterMap
					.get(postLeavetransactionDTO.getStartSession());
			leaveApplication.setLeaveSessionMaster1(leaveStartSessionMaster);

		}
		if (StringUtils.isNotBlank(postLeavetransactionDTO.getEndSession())) {
			LeaveSessionMaster leaveToSessionMaster = leaveSessionMasterMap
					.get(postLeavetransactionDTO.getEndSession());
			leaveApplication.setLeaveSessionMaster2(leaveToSessionMaster);
		}
		leaveApplication.setEmployeeLeaveSchemeType(employeeLeaveSchemeType);
		LeaveApplication persistLeaveApplication = leaveApplicationDAO.saveReturn(leaveApplication);

		LeaveApplicationReviewer leaveApplicationReviewer = new LeaveApplicationReviewer();

		leaveApplicationReviewer.setWorkFlowRuleMaster(reviewer1WorkFlowRuleMaster);
		leaveApplicationReviewer.setLeaveApplication(persistLeaveApplication);
		leaveApplicationReviewer.setPending(false);
		leaveApplicationReviewer.setEmployee(loggedInEmployee);
		leaveApplicationReviewerDAO.save(leaveApplicationReviewer);

		LeaveApplicationWorkflow leaveApplicationWorkflow = new LeaveApplicationWorkflow();
		leaveApplicationWorkflow.setCreatedDate(DateUtils.getCurrentTimestampWithTime());
		leaveApplicationWorkflow.setEmployee(loggedInEmployee);
		leaveApplicationWorkflow.setEndDate(persistLeaveApplication.getEndDate());
		leaveApplicationWorkflow.setEndSessionMaster(persistLeaveApplication.getLeaveSessionMaster2());
		leaveApplicationWorkflow.setStartSessionMaster(persistLeaveApplication.getLeaveSessionMaster1());
		leaveApplicationWorkflow.setForwardTo("");
		leaveApplicationWorkflow.setLeaveApplication(persistLeaveApplication);
		leaveApplicationWorkflow.setRemarks(persistLeaveApplication.getReason());
		leaveApplicationWorkflow.setLeaveStatusMaster(persistLeaveApplication.getLeaveStatusMaster());
		leaveApplicationWorkflow.setStartDate(persistLeaveApplication.getStartDate());
		leaveApplicationWorkflow.setTotalDays(persistLeaveApplication.getTotalDays());
		leaveApplicationWorkflowDAO.save(leaveApplicationWorkflow);

		return persistLeaveApplication;

	}

	private Employee getDelegatedEmployee(Long leaveAppEmpId, Long employeeId) {
		Employee emp = employeeDAO.findById(employeeId);
		WorkflowDelegate workflowDelegate = null;

		AppCodeMaster appCodeMasterLeave = appCodeMasterDAO.findByCategoryAndDesc(PayAsiaConstants.WORKFLOW_CATEGORY,
				PayAsiaConstants.WORKFLOW_CATEGORY_LEAVE);

		WorkflowDelegate workflowDelegateLeave = workflowDelegateDAO.findEmployeeByCurrentDate(employeeId,
				appCodeMasterLeave.getAppCodeID());

		if (workflowDelegateLeave != null) {
			workflowDelegate = workflowDelegateLeave;
		} else {

			AppCodeMaster appCodeMasterALL = appCodeMasterDAO.findByCategoryAndDesc(PayAsiaConstants.WORKFLOW_CATEGORY,
					PayAsiaConstants.WORKFLOW_CATEGORY_ALL);

			WorkflowDelegate workflowDelegateALL = workflowDelegateDAO.findEmployeeByCurrentDate(employeeId,
					appCodeMasterALL.getAppCodeID());

			workflowDelegate = workflowDelegateALL;
		}

		if (workflowDelegate != null) {
			if (leaveAppEmpId.equals(workflowDelegate.getEmployee2().getEmployeeId())) {
				return emp;
			}
			return workflowDelegate.getEmployee2();
		}
		return emp;
	}

	private LeaveApplication saveLeaveApplication(EmployeeLeaveSchemeTypeHistoryDTO employeeLeaveSchemeTypeHistoryDTO,
			Long companyId, Long employeeId, EmployeeLeaveSchemeType employeeLeaveSchemeType) {
		LeaveApplication leaveApplication = new LeaveApplication();
		Company company = companyDAO.findById(companyId);
		leaveApplication.setCompany(company);
		Employee loggedInEmployee = employeeDAO.findById(employeeId);
		leaveApplication.setEmployee(employeeLeaveSchemeType.getEmployeeLeaveScheme().getEmployee());
		leaveApplication.setEndDate(DateUtils.stringToTimestamp(employeeLeaveSchemeTypeHistoryDTO.getEndDate()));
		leaveApplication.setStartDate(DateUtils.stringToTimestamp(employeeLeaveSchemeTypeHistoryDTO.getStartDate()));
		leaveApplication.setReason(employeeLeaveSchemeTypeHistoryDTO.getReason());
		leaveApplication.setApplyTo(loggedInEmployee.getEmail());
		LeaveStatusMaster leaveStatusCompleted = leaveStatusMasterDAO
				.findByCondition(PayAsiaConstants.LEAVE_STATUS_COMPLETED);
		leaveApplication.setLeaveStatusMaster(leaveStatusCompleted);
		leaveApplication.setCreatedDate(DateUtils.getCurrentTimestampWithTime());
		leaveApplication.setTotalDays(Float.parseFloat(employeeLeaveSchemeTypeHistoryDTO.getDays().toString()));
		if (employeeLeaveSchemeTypeHistoryDTO.getFromSessionId() != 0) {
			LeaveSessionMaster leaveStartSessionMaster = leaveSessionMasterDAO
					.findById(employeeLeaveSchemeTypeHistoryDTO.getFromSessionId());
			leaveApplication.setLeaveSessionMaster1(leaveStartSessionMaster);

		}
		if (employeeLeaveSchemeTypeHistoryDTO.getToSessionId() != 0) {
			LeaveSessionMaster leaveToSessionMaster = leaveSessionMasterDAO
					.findById(employeeLeaveSchemeTypeHistoryDTO.getToSessionId());
			leaveApplication.setLeaveSessionMaster2(leaveToSessionMaster);
		}
		leaveApplication.setEmployeeLeaveSchemeType(employeeLeaveSchemeType);
		LeaveApplication persistLeaveApplication = leaveApplicationDAO.saveReturn(leaveApplication);

		LeaveApplicationReviewer leaveApplicationReviewer = new LeaveApplicationReviewer();
		WorkFlowRuleMaster workflowRuleMaster = workFlowRuleMasterDAO
				.findByRuleNameValue(PayAsiaConstants.WORK_FLOW_RULE_NAME_LEAVE_REVIEWER, "1");
		leaveApplicationReviewer.setWorkFlowRuleMaster(workflowRuleMaster);
		leaveApplicationReviewer.setLeaveApplication(persistLeaveApplication);
		leaveApplicationReviewer.setPending(false);
		leaveApplicationReviewer.setEmployee(loggedInEmployee);
		leaveApplicationReviewerDAO.save(leaveApplicationReviewer);

		LeaveApplicationWorkflow leaveApplicationWorkflow = new LeaveApplicationWorkflow();
		leaveApplicationWorkflow.setCreatedDate(DateUtils.getCurrentTimestampWithTime());
		leaveApplicationWorkflow.setEmployee(loggedInEmployee);
		leaveApplicationWorkflow.setEndDate(persistLeaveApplication.getEndDate());
		leaveApplicationWorkflow.setEndSessionMaster(persistLeaveApplication.getLeaveSessionMaster2());
		leaveApplicationWorkflow.setStartSessionMaster(persistLeaveApplication.getLeaveSessionMaster1());
		leaveApplicationWorkflow.setForwardTo(loggedInEmployee.getEmail());
		leaveApplicationWorkflow.setLeaveApplication(persistLeaveApplication);
		leaveApplicationWorkflow.setRemarks(persistLeaveApplication.getReason());
		leaveApplicationWorkflow.setLeaveStatusMaster(persistLeaveApplication.getLeaveStatusMaster());
		leaveApplicationWorkflow.setStartDate(persistLeaveApplication.getStartDate());
		leaveApplicationWorkflow.setTotalDays(persistLeaveApplication.getTotalDays());
		leaveApplicationWorkflowDAO.save(leaveApplicationWorkflow);

		return persistLeaveApplication;

	}

	@Override
	public List<AppCodeDTO> getLeaveTransactionType(Long companyId) {
		boolean status = true;
		LeavePreference leavePreferenceVO = leavePreferenceDAO.findByCompanyId(companyId);
		if (leavePreferenceVO != null) {
			if (leavePreferenceVO.getShowEncashed() != null) {
				if (!leavePreferenceVO.getShowEncashed()) {
					status = false;
				}
			}

		}
		List<AppCodeMaster> appCodeList = appCodeMasterDAO
				.findByCondition(PayAsiaConstants.APP_CODE_LEAVE_TRANSACTION_TYPE);
		List<AppCodeDTO> appCodeDTOs = new ArrayList<>();
		for (AppCodeMaster appCodeMaster : appCodeList) {
			AppCodeDTO appCodeDTO = new AppCodeDTO();

			if (!status) {
				if (!"Encashed".equalsIgnoreCase(appCodeMaster.getCodeDesc())) {
					appCodeDTO.setAppCodeID(appCodeMaster.getAppCodeID());
					appCodeDTO.setCodeDesc(appCodeMaster.getCodeValue());
					appCodeDTOs.add(appCodeDTO);
				}
			} else {
				appCodeDTO.setAppCodeID(appCodeMaster.getAppCodeID());
				appCodeDTO.setCodeDesc(appCodeMaster.getCodeValue());
				appCodeDTOs.add(appCodeDTO);
			}

		}

		return appCodeDTOs;
	}

	@Override
	public List<String> getLeaveTransactionHistoryType(Long companyId) {
		boolean status = true;
		LeavePreference leavePreferenceVO = leavePreferenceDAO.findByCompanyId(companyId);
		if (leavePreferenceVO != null) {
			if (leavePreferenceVO.getShowEncashed() != null) {
				if (!leavePreferenceVO.getShowEncashed()) {
					status = false;
				}
			}

		}
		List<String> typeList = new ArrayList<>();
		typeList.add(PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_SUBMITTED);
		typeList.add(PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_WITHDRAWN);
		typeList.add(PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_REJECTED);
		typeList.add(PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_APPROVED);
		typeList.add(PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_SUBMITTED_CANCEL);
		typeList.add(PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_APPROVED_CANCEL);
		typeList.add(PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_CREDITED);
		typeList.add(PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_CARRIED_FORWARD);
		if (status) {
			typeList.add(PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_ENCHASED);
		}

		typeList.add(PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_FORFEITED);
		typeList.add(PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_BALANCE);

		return typeList;
	}

	@Override
	public List<LeaveBalanceSummaryForm> getEmployeeId(Long companyId, String searchString, Long employeeId) {
		List<LeaveBalanceSummaryForm> leaveBalanceSummaryFormList = new ArrayList<LeaveBalanceSummaryForm>();

		EmployeeShortListDTO employeeShortListDTO = generalLogic.getShortListEmployeeIds(employeeId, companyId);
		List<Employee> employeeNumberList = employeeDAO.getEmployeeIds(searchString.trim(), companyId,
				employeeShortListDTO);
		if (employeeNumberList == null) {
			throw new PayAsiaSystemException("Authentication Exception");
		}

		for (Employee employee : employeeNumberList) {
			LeaveBalanceSummaryForm leaveBalanceSummaryForm = new LeaveBalanceSummaryForm();
			leaveBalanceSummaryForm.setEmployeeNumber(employee.getEmployeeNumber());
			String empName = "";
			empName += employee.getFirstName() + " ";
			if (StringUtils.isNotBlank(employee.getLastName())) {
				empName += employee.getLastName();
			}
			leaveBalanceSummaryForm.setEmployeeName(empName);
			leaveBalanceSummaryForm.setEmployeeId(employee.getEmployeeId());
			leaveBalanceSummaryFormList.add(leaveBalanceSummaryForm);

		}
		return leaveBalanceSummaryFormList;
	}

	@Override
	public List<LeaveBalanceSummaryForm> getHolidaycalendar(Long companyId, Long employeeId, int year) {
		List<LeaveBalanceSummaryForm> leaveBalanceSummaryFormList = new ArrayList<LeaveBalanceSummaryForm>();
		LeavePreference leavePreference = leavePreferenceDAO.findByCompanyId(companyId);
		int startMonth = 0;
		int endMonth = 0;
		if (leavePreference != null) {
			startMonth = Integer.parseInt(String.valueOf(leavePreference.getStartMonth().getMonthId()));
			endMonth = Integer.parseInt(String.valueOf(leavePreference.getEndMonth().getMonthId()));

		}
		if (startMonth != 0 && endMonth != 0 && startMonth != 1 && endMonth != 12) {
			year = year - 1;
		}
		EmployeeHolidayCalendar employeeHolidayCalendar = employeeHolidayCalendarDAO.findByEmpId(employeeId, companyId,
				year);
		if (employeeHolidayCalendar != null) {
			List<CompanyHolidayCalendarDetail> CompanyHolidayCalendarDetailList = companyHolidayCalendarDetailDAO
					.findByCondition(employeeHolidayCalendar.getCompanyHolidayCalendar().getCompanyHolidayCalendarId(),
							companyId, year, null, null);

			for (CompanyHolidayCalendarDetail companyHolidayCalVO : CompanyHolidayCalendarDetailList) {
				LeaveBalanceSummaryForm leaveBalanceSummaryForm = new LeaveBalanceSummaryForm();
				Calendar calendar = new GregorianCalendar();
				Date trialTime = DateUtils
						.stringToDate(DateUtils.timeStampToString(companyHolidayCalVO.getHolidayDate()));
				calendar.setTime(trialTime);
				leaveBalanceSummaryForm.setHolidayDesc(companyHolidayCalVO.getHolidayDesc());
				leaveBalanceSummaryForm.setMonth(calendar.get(Calendar.MONTH) + 1);
				String dayOfMonth = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
				boolean daystatus = dayOfMonth.matches("[0-9]");
				if (daystatus) {
					dayOfMonth = "0" + calendar.get(Calendar.DAY_OF_MONTH);
					leaveBalanceSummaryForm.setDayOfMonthStr(dayOfMonth);
				} else {
					leaveBalanceSummaryForm.setDayOfMonthStr(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
				}
				leaveBalanceSummaryForm.setDayOfMonth(calendar.get(Calendar.DAY_OF_MONTH));
				leaveBalanceSummaryForm.setDayOfWeek(getDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK)));
				leaveBalanceSummaryFormList.add(leaveBalanceSummaryForm);
			}
		}
		Collections.sort(leaveBalanceSummaryFormList, new holidCalComparator());
		return leaveBalanceSummaryFormList;
	}

	private class holidCalComparator implements Comparator<LeaveBalanceSummaryForm> {
		public int compare(LeaveBalanceSummaryForm one, LeaveBalanceSummaryForm another) {
			return (one.getMonth() - another.getMonth() != 0) ? one.getMonth() - another.getMonth()
					: one.getDayOfMonth() - another.getDayOfMonth();

		}

	}

	private String getDayOfWeek(int day) {
		String dayOfWeek = "";
		if (day == 1) {
			dayOfWeek = "Sunday";
		}
		if (day == 2) {
			dayOfWeek = "Monday";
		}
		if (day == 3) {
			dayOfWeek = "Tuesday";
		}
		if (day == 4) {
			dayOfWeek = "Wednesday";
		}
		if (day == 5) {
			dayOfWeek = "Thursday";
		}
		if (day == 6) {
			dayOfWeek = "Friday";
		}
		if (day == 7) {
			dayOfWeek = "Saturday";
		}
		return dayOfWeek;
	}

	@Override
	public LeaveBalanceSummaryResponse getDashBoardEmpOnLeaveList(String fromDate, String toDate, PageRequest pageDTO,
			SortCondition sortDTO, Long companyId, Long employeeId) {
		Locale locale = UserContext.getLocale();
		Company companyVO = companyDAO.findById(companyId);
		List<EmployeeLeaveSchemeTypeDTO> leaveSchemeFormList = new ArrayList<EmployeeLeaveSchemeTypeDTO>();
		LeaveBalanceSummaryConditionDTO conditionDTO = new LeaveBalanceSummaryConditionDTO();
		if (StringUtils.isNotBlank(fromDate)) {
			conditionDTO.setFromDate(DateUtils.stringToTimestamp(fromDate, companyVO.getDateFormat()));

		}
		if (StringUtils.isNotBlank(toDate)) {
			conditionDTO.setToDate(DateUtils.stringToTimestamp(toDate, companyVO.getDateFormat()));

		}

		List<String> leaveStatusList = new ArrayList<>();

		LeavePreference leavePreference = leavePreferenceDAO.findByCompanyId(companyId);

		if (leavePreference != null) {

			if (leavePreference.getLeaveTransactionToShow().getCodeDesc()
					.equals(PayAsiaConstants.LEAVE_PREFERENCE_SHOW_LEAVE_TRANASACTION_BOTH)) {

				leaveStatusList.add(PayAsiaConstants.CLAIM_APPLICATION_SUBMITTED);
				leaveStatusList.add(PayAsiaConstants.LEAVE_STATUS_APPROVED);
				leaveStatusList.add(PayAsiaConstants.LEAVE_STATUS_COMPLETED);

			} else if (leavePreference.getLeaveTransactionToShow().getCodeDesc()
					.equals(PayAsiaConstants.LEAVE_PREFERENCE_SHOW_LEAVE_TRANASACTION_APPROVED)) {
				leaveStatusList.add(PayAsiaConstants.LEAVE_STATUS_COMPLETED);

			} else if (leavePreference.getLeaveTransactionToShow().getCodeDesc()
					.equals(PayAsiaConstants.LEAVE_PREFERENCE_SHOW_LEAVE_TRANASACTION_PENDING)) {
				leaveStatusList.add(PayAsiaConstants.CLAIM_APPLICATION_SUBMITTED);
				leaveStatusList.add(PayAsiaConstants.LEAVE_STATUS_APPROVED);

			}

		} else {
			leaveStatusList.add(PayAsiaConstants.LEAVE_STATUS_COMPLETED);

		}
		List<LeaveApplication> searchList = null;
		int recordSize = 0;

		EmployeeShortListDTO employeeShortListDTO = generalLogic.getShortListEmployeeIds(employeeId, companyId);
		conditionDTO.setEmployeeShortListDTO(employeeShortListDTO);

		searchList = leaveApplicationDAO.findByLeaveStatus(conditionDTO, companyId, leaveStatusList, pageDTO, sortDTO);
		recordSize = (leaveApplicationDAO.getCountForCondLeaveStatus(conditionDTO, companyId, leaveStatusList))
				.intValue();

		if (searchList != null) {
			for (LeaveApplication leaveApplickationVO : searchList) {
				EmployeeLeaveSchemeTypeDTO leaveSchemeTypeForm = new EmployeeLeaveSchemeTypeDTO();

				leaveSchemeTypeForm.setEmployeeNumber(leaveApplickationVO.getEmployee().getEmployeeNumber());
				String empName = "";
				empName += leaveApplickationVO.getEmployee().getFirstName() + " ";
				if (StringUtils.isNotBlank(leaveApplickationVO.getEmployee().getLastName())) {
					empName += leaveApplickationVO.getEmployee().getLastName();
				}
				leaveSchemeTypeForm.setEmployeeName(empName);

				leaveSchemeTypeForm.setStartDate(
						DateUtils.timeStampToString(leaveApplickationVO.getStartDate(), companyVO.getDateFormat()));
				if (leaveApplickationVO.getEndDate() != null) {
					leaveSchemeTypeForm.setEndDate(
							DateUtils.timeStampToString(leaveApplickationVO.getEndDate(), companyVO.getDateFormat()));
				}

				leaveSchemeTypeForm.setLeaveType(leaveApplickationVO.getEmployeeLeaveSchemeType().getLeaveSchemeType()
						.getLeaveTypeMaster().getLeaveTypeName());
				leaveSchemeTypeForm.setNoOfDays(String.valueOf(leaveApplickationVO.getTotalDays()));
				leaveSchemeTypeForm.setLeaveApplicationId(leaveApplickationVO.getLeaveApplicationId());

				if (StringUtils.isNotBlank(leaveApplickationVO.getLeaveSessionMaster1().getSessionLabelKey())) {
					String labelMsg = messageSource.getMessage(
							leaveApplickationVO.getLeaveSessionMaster1().getSessionLabelKey(), new Object[] {}, locale);
					leaveSchemeTypeForm.setStartSession(labelMsg);
				} else {
					leaveSchemeTypeForm.setStartSession(leaveApplickationVO.getLeaveSessionMaster1().getSession());
				}

				if (StringUtils.isNotBlank(leaveApplickationVO.getLeaveSessionMaster2().getSessionLabelKey())) {
					String labelMsg = messageSource.getMessage(
							leaveApplickationVO.getLeaveSessionMaster2().getSessionLabelKey(), new Object[] {}, locale);
					leaveSchemeTypeForm.setEndSession(labelMsg);
				} else {
					leaveSchemeTypeForm.setEndSession(leaveApplickationVO.getLeaveSessionMaster2().getSession());
				}

				leaveSchemeFormList.add(leaveSchemeTypeForm);
			}
		}

		LeaveBalanceSummaryResponse response = new LeaveBalanceSummaryResponse();
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

		}
		response.setRows(leaveSchemeFormList);
		response.setRecords(recordSize);
		return response;
	}

	@Override
	public LeaveBalanceSummaryResponse getDashBoardByManagerEmpOnLeaveList(String fromDate, String toDate,
			PageRequest pageDTO, SortCondition sortDTO, Long companyId, Long employeeId,List<String> roleLst,MessageSource messageSource,int year,int fromYear, int toYear) {

		Company companyVO = companyDAO.findById(companyId);
		List<EmployeeLeaveSchemeTypeDTO> leaveSchemeFormList = new ArrayList<EmployeeLeaveSchemeTypeDTO>();
		LeaveBalanceSummaryConditionDTO conditionDTO = new LeaveBalanceSummaryConditionDTO();
	

		List<String> leaveStatusList = new ArrayList<>();

		LeavePreference leavePreference = leavePreferenceDAO.findByCompanyId(companyId);
		EmployeeLeaveSchemeTypeDTO employeeLeaveSchemeTypeDTO = new EmployeeLeaveSchemeTypeDTO();
		createLeaveCalendarDate(year, companyId, employeeLeaveSchemeTypeDTO);
		int startYear = employeeLeaveSchemeTypeDTO.getStartYear();
		Boolean dashboardSameAsAdmin = false;
		
		if (leavePreference != null) {
			if (leavePreference.getDashboardSameAsAdmin() != null) {
				dashboardSameAsAdmin = leavePreference.getDashboardSameAsAdmin();
			}
			if (leavePreference.getLeaveTransactionToShow().getCodeDesc()
					.equals(PayAsiaConstants.LEAVE_PREFERENCE_SHOW_LEAVE_TRANASACTION_BOTH)) {

				leaveStatusList.add(PayAsiaConstants.CLAIM_APPLICATION_SUBMITTED);
				leaveStatusList.add(PayAsiaConstants.LEAVE_STATUS_APPROVED);
				leaveStatusList.add(PayAsiaConstants.LEAVE_STATUS_COMPLETED);

			} else if (leavePreference.getLeaveTransactionToShow().getCodeDesc()
					.equals(PayAsiaConstants.LEAVE_PREFERENCE_SHOW_LEAVE_TRANASACTION_APPROVED)) {
				leaveStatusList.add(PayAsiaConstants.LEAVE_STATUS_COMPLETED);

			} else if (leavePreference.getLeaveTransactionToShow().getCodeDesc()
					.equals(PayAsiaConstants.LEAVE_PREFERENCE_SHOW_LEAVE_TRANASACTION_PENDING)) {
				leaveStatusList.add(PayAsiaConstants.CLAIM_APPLICATION_SUBMITTED);
				leaveStatusList.add(PayAsiaConstants.LEAVE_STATUS_APPROVED);

			}

		} else {
			leaveStatusList.add(PayAsiaConstants.LEAVE_STATUS_COMPLETED);

		}

		int recordSize = 0;

		EmployeeShortListDTO employeeShortListDTO = new EmployeeShortListDTO();
	
		employeeShortListDTO.setEmployeeShortList(false);
		/*if (StringUtils.isNotBlank(fromDate) || StringUtils.isNotBlank(toDate) ) {*/
			
			  
			if (StringUtils.isNotBlank(fromDate)) {
				
				conditionDTO.setFromDate(DateUtils.stringToTimestamp(fromDate, companyVO.getDateFormat()));

			}
			if (StringUtils.isNotBlank(toDate)) {
				conditionDTO.setToDate(DateUtils.stringToTimestamp(toDate, companyVO.getDateFormat()));

			}
		//}
		//else{
		conditionDTO.setEmployeeShortListDTO(employeeShortListDTO);
		//conditionDTO.setFromDate(employeeLeaveSchemeTypeDTO.getStartDateTime());
		//conditionDTO.setToDate(employeeLeaveSchemeTypeDTO.getEndDateTime());
		//}
		
		ArrayList<String> roleList = new ArrayList<>();

		for (String role : roleLst) {
			if ("ROLE_LEAVE_MANAGER".equals(role)) {
				roleList.add("ROLE_LEAVE_MANAGER");
			}
		}
		List<LeaveApplication> searchList = null;
		if(dashboardSameAsAdmin) {
			
			if ((startYear == fromYear || fromYear == 0)
					&& ((toYear == year || toYear == 0) || (toYear == startYear))) {

				if (fromYear == 0) {
					conditionDTO.setFromDate(employeeLeaveSchemeTypeDTO.getStartDateTime());

				}
				if (toYear == 0) {
					conditionDTO.setToDate(employeeLeaveSchemeTypeDTO.getEndDateTime());
				}
				searchList = leaveApplicationDAO.findByLeaveStatus(conditionDTO, companyId, leaveStatusList, null, null);
				recordSize = (leaveApplicationDAO.getCountForCondLeaveStatus(conditionDTO, companyId, leaveStatusList)).intValue();
			}
			
			 if((fromYear == 0 && toYear == 0) ){
				 
				 searchList = leaveApplicationDAO.findByLeaveStatus(conditionDTO, companyId, leaveStatusList, null, null);
				 recordSize = (leaveApplicationDAO.getCountForCondLeaveStatus(conditionDTO, companyId, leaveStatusList)).intValue();
			 }
		
	    }
		
	  else{
			if ((startYear == fromYear || fromYear == 0)
					&& ((toYear == year || toYear == 0) || (toYear == startYear))) {

				if (fromYear == 0) {
					conditionDTO.setFromDate(employeeLeaveSchemeTypeDTO.getStartDateTime());

				}
				if (toYear == 0) {
					conditionDTO.setToDate(employeeLeaveSchemeTypeDTO.getEndDateTime());
				}
				searchList = leaveApplicationDAO.findByLeaveStatusAndReviewer(conditionDTO, companyId, leaveStatusList,
						employeeId, employeeId, null, null);
				recordSize = (leaveApplicationDAO.getCountForCondLeaveStatusAndReviewer(conditionDTO, companyId,
						employeeId, employeeId, leaveStatusList)).intValue();
			}
		   if((fromYear == 0 && toYear == 0) ){
				searchList = leaveApplicationDAO.findByLeaveStatusAndReviewer(conditionDTO, companyId, leaveStatusList, employeeId, employeeId, null,null);
				recordSize = (leaveApplicationDAO.getCountForCondLeaveStatusAndReviewer(conditionDTO, companyId, employeeId, employeeId, leaveStatusList)).intValue();
				  }
		

		}
		 

		if (searchList != null) {
			for (LeaveApplication leaveApplickationVO : searchList) {
				EmployeeLeaveSchemeTypeDTO leaveSchemeTypeForm = new EmployeeLeaveSchemeTypeDTO();

				leaveSchemeTypeForm.setEmployeeNumber(leaveApplickationVO.getEmployee().getEmployeeNumber());
				String empName = "";
				empName += leaveApplickationVO.getEmployee().getFirstName() + " ";
				if (StringUtils.isNotBlank(leaveApplickationVO.getEmployee().getLastName())) {
					empName += leaveApplickationVO.getEmployee().getLastName();
				}
				leaveSchemeTypeForm.setEmployeeName(empName);

				leaveSchemeTypeForm.setStartDate(DateUtils.timeStampToString(leaveApplickationVO.getStartDate(), companyVO.getDateFormat()));
				if (leaveApplickationVO.getEndDate() != null) {
					leaveSchemeTypeForm.setEndDate(DateUtils.timeStampToString(leaveApplickationVO.getEndDate(), companyVO.getDateFormat()));
				}

				leaveSchemeTypeForm
						.setLeaveType(leaveApplickationVO.getEmployeeLeaveSchemeType().getLeaveSchemeType().getLeaveTypeMaster().getLeaveTypeName());
				leaveSchemeTypeForm.setNoOfDays(String.valueOf(leaveApplickationVO.getTotalDays()));
				leaveSchemeTypeForm.setLeaveApplicationId(leaveApplickationVO.getLeaveApplicationId());
				if (StringUtils.isNotBlank(leaveApplickationVO.getLeaveSessionMaster1().getSessionLabelKey())) {
					String labelMsg = messageSource.getMessage(leaveApplickationVO.getLeaveSessionMaster1().getSessionLabelKey(), new Object[] {}, UserContext.getLocale());
					leaveSchemeTypeForm.setStartSession(labelMsg);
				} else {
					leaveSchemeTypeForm.setStartSession(leaveApplickationVO.getLeaveSessionMaster1().getSession());
				}

				if (StringUtils.isNotBlank(leaveApplickationVO.getLeaveSessionMaster2().getSessionLabelKey())) {
					String labelMsg = messageSource.getMessage(leaveApplickationVO.getLeaveSessionMaster2().getSessionLabelKey(), new Object[] {}, UserContext.getLocale());
					leaveSchemeTypeForm.setEndSession(labelMsg);
				} else {
					leaveSchemeTypeForm.setEndSession(leaveApplickationVO.getLeaveSessionMaster2().getSession());
				}

				leaveSchemeFormList.add(leaveSchemeTypeForm);
			}
		}


		LeaveBalanceSummaryResponse response = new LeaveBalanceSummaryResponse();

		sortData(response, sortDTO, pageDTO, leaveSchemeFormList, recordSize);

		return response;
	}

	// method added for sorting and pagination
	private void sortData(LeaveBalanceSummaryResponse response, SortCondition sortDTO, PageRequest pageDTO,
			List<EmployeeLeaveSchemeTypeDTO> leaveSchemeFormList, int recordSize) {

		if (sortDTO != null && !StringUtils.isEmpty(sortDTO.getColumnName()) && !leaveSchemeFormList.isEmpty()) {
			Collections.sort(leaveSchemeFormList,
					new EmployeeLeaveDashboardComparison(sortDTO.getOrderType(), sortDTO.getColumnName()));
		}

		List<EmployeeLeaveSchemeTypeDTO> finalLeaveSchemeFormList = new ArrayList<EmployeeLeaveSchemeTypeDTO>();

		if (pageDTO != null && !leaveSchemeFormList.isEmpty()) {
			int recordSizeFinal = leaveSchemeFormList.size();
			int pageSize = pageDTO.getPageSize();
			int pageNo = pageDTO.getPageNumber();
			int startPos = (pageSize * (pageNo - 1));
			int endPos = 0;

			int totalPages = recordSize / pageSize;

			if (recordSize % pageSize != 0) {
				totalPages = totalPages + 1;
			}
			if (recordSize == 0) {
				pageDTO.setPageNumber(0);
			}

			if ((startPos + pageSize) <= recordSizeFinal){
				endPos = startPos + pageSize;
			} else if (startPos <= recordSizeFinal) {
				endPos = recordSizeFinal;
			}

			finalLeaveSchemeFormList = leaveSchemeFormList.subList(startPos, endPos);
			response.setPage(pageDTO.getPageNumber());
			response.setTotal(totalPages);
		}

		response.setRows(finalLeaveSchemeFormList);
		response.setRecords(recordSize);
	}

	@Override
	public LeaveBalanceSummaryForm myLeaveSchemeDetail(Long employeeId, Long companyId) {
		LeaveBalanceSummaryForm leaveBalanceSummaryForm = new LeaveBalanceSummaryForm();
		Employee employee = employeeDAO.findById(employeeId);

		if (employee == null) {
			leaveBalanceSummaryForm.setResponseString(PayAsiaConstants.LEAVE_BALANCE_EMPLOYEE_ERROR);
			return leaveBalanceSummaryForm;
		}

		EmployeeLeaveScheme employeeLeaveScheme = employeeLeaveSchemeDAO.findByActive(employee.getEmployeeId(), true);

		if (employeeLeaveScheme == null) {
			leaveBalanceSummaryForm.setResponseString(PayAsiaConstants.LEAVE_BALANCE_SCHEME_ERROR);
			return leaveBalanceSummaryForm;
		}

		leaveBalanceSummaryForm.setEmployeeLeaveSchemeId(employeeLeaveScheme.getEmployeeLeaveSchemeId());
		leaveBalanceSummaryForm.setLeaveScheme(employeeLeaveScheme.getLeaveScheme().getSchemeName());
		leaveBalanceSummaryForm.setEmployeeName(getEmployeeName(employee));

		return leaveBalanceSummaryForm;
	}

	@Override
	public LeaveBalanceSummaryForm getLeaveCalMonthList(String year, String month, Long companyId, Long employeeId) {
		LeaveBalanceSummaryConditionDTO conditionDTO = new LeaveBalanceSummaryConditionDTO();
		Company companyVO = companyDAO.findById(companyId);

		List<String> leaveStatusList = new ArrayList<>();

		LeavePreference leavePreference = leavePreferenceDAO.findByCompanyId(companyId);

		if (leavePreference != null) {

			if (leavePreference.getLeaveTransactionToShow().getCodeDesc()
					.equals(PayAsiaConstants.LEAVE_PREFERENCE_SHOW_LEAVE_TRANASACTION_BOTH)) {

				leaveStatusList.add(PayAsiaConstants.CLAIM_APPLICATION_SUBMITTED);
				leaveStatusList.add(PayAsiaConstants.LEAVE_STATUS_APPROVED);
				leaveStatusList.add(PayAsiaConstants.LEAVE_STATUS_COMPLETED);

			} else if (leavePreference.getLeaveTransactionToShow().getCodeDesc()
					.equals(PayAsiaConstants.LEAVE_PREFERENCE_SHOW_LEAVE_TRANASACTION_APPROVED)) {
				leaveStatusList.add(PayAsiaConstants.LEAVE_STATUS_COMPLETED);

			} else if (leavePreference.getLeaveTransactionToShow().getCodeDesc()
					.equals(PayAsiaConstants.LEAVE_PREFERENCE_SHOW_LEAVE_TRANASACTION_PENDING)) {
				leaveStatusList.add(PayAsiaConstants.CLAIM_APPLICATION_SUBMITTED);
				leaveStatusList.add(PayAsiaConstants.LEAVE_STATUS_APPROVED);

			}

		} else {
			leaveStatusList.add(PayAsiaConstants.LEAVE_STATUS_COMPLETED);

		}
		EmployeeShortListDTO employeeShortListDTO = generalLogic.getShortListEmployeeIds(employeeId, companyId);
		conditionDTO.setEmployeeShortListDTO(employeeShortListDTO);

		HashMap<String, ArrayList<Long>> dateValueMap = new HashMap<>();
		LeaveBalanceSummaryForm leaveBalanceSummaryForm = new LeaveBalanceSummaryForm();

		List<LeaveApplication> leaveList = null;

		leaveList = leaveApplicationDAO.findByYearAndMonth(conditionDTO, companyId, null, null, leaveStatusList, year,
				month);

		for (LeaveApplication leaveApplication : leaveList) {
			if (leaveApplication.getLeaveCancelApplication() != null) {
				continue;
			}
			SimpleDateFormat sdfDate = new SimpleDateFormat(companyVO.getDateFormat());
			Calendar calStart = new GregorianCalendar();
			Date startDate = DateUtils.stringToDate(
					DateUtils.timeStampToString(leaveApplication.getStartDate(), companyVO.getDateFormat()),
					companyVO.getDateFormat());
			calStart.setTime(startDate);

			Calendar calEnd = new GregorianCalendar();
			Date endDate = DateUtils.stringToDate(
					DateUtils.timeStampToString(leaveApplication.getEndDate(), companyVO.getDateFormat()),
					companyVO.getDateFormat());
			calEnd.setTime(endDate);

			while (startDate.before(endDate) || startDate.equals(endDate)) {
				if (dateValueMap.containsKey(sdfDate.format(calStart.getTime()))) {
					ArrayList<Long> leaveAppList = dateValueMap.get(sdfDate.format(calStart.getTime()));
					leaveAppList.add(leaveApplication.getLeaveApplicationId());

				} else {
					ArrayList<Long> leaveAppIdList = new ArrayList<>();
					leaveAppIdList.add(leaveApplication.getLeaveApplicationId());
					dateValueMap.put(sdfDate.format(calStart.getTime()), leaveAppIdList);
				}

				calStart.add(Calendar.DATE, 1);
				startDate = calStart.getTime();
			}
		}

		leaveBalanceSummaryForm.setDateValueMap(dateValueMap);
		return leaveBalanceSummaryForm;

	}

	@Override
	public LeaveBalanceSummaryForm getLeaveCalMonthListByManager(String year, String month, Long companyId,
			Long employeeId, List<String> rolesList) {
		LeaveBalanceSummaryConditionDTO conditionDTO = new LeaveBalanceSummaryConditionDTO();
		Company companyVO = companyDAO.findById(companyId);

		List<String> leaveStatusList = new ArrayList<>();

		LeavePreference leavePreference = leavePreferenceDAO.findByCompanyId(companyId);
		boolean isEnableLeaveCalBasedOnCustField = false;
		if (leavePreference != null) {

			if (leavePreference.getLeaveTransactionToShow().getCodeDesc()
					.equals(PayAsiaConstants.LEAVE_PREFERENCE_SHOW_LEAVE_TRANASACTION_BOTH)) {

				leaveStatusList.add(PayAsiaConstants.CLAIM_APPLICATION_SUBMITTED);
				leaveStatusList.add(PayAsiaConstants.LEAVE_STATUS_APPROVED);
				leaveStatusList.add(PayAsiaConstants.LEAVE_STATUS_COMPLETED);

			} else if (leavePreference.getLeaveTransactionToShow().getCodeDesc()
					.equals(PayAsiaConstants.LEAVE_PREFERENCE_SHOW_LEAVE_TRANASACTION_APPROVED)) {
				leaveStatusList.add(PayAsiaConstants.LEAVE_STATUS_COMPLETED);

			} else if (leavePreference.getLeaveTransactionToShow().getCodeDesc()
					.equals(PayAsiaConstants.LEAVE_PREFERENCE_SHOW_LEAVE_TRANASACTION_PENDING)) {
				leaveStatusList.add(PayAsiaConstants.CLAIM_APPLICATION_SUBMITTED);
				leaveStatusList.add(PayAsiaConstants.LEAVE_STATUS_APPROVED);

			}

			if (leavePreference.isShowLeaveCalendarBasedOnCustomField()) {
				if (leavePreference.getLeaveCalendarBasedOnCustomField() != null) {
					isEnableLeaveCalBasedOnCustField = true;
					String fieldValue = generalLogic.getEmpoyeeDynamicFormFieldValue(
							leavePreference.getLeaveCalendarBasedOnCustomField(), employeeId, companyId);
					CompanyEmployeeShortList companyEmployeeShortList = new CompanyEmployeeShortList();
					companyEmployeeShortList.setDataDictionary(leavePreference.getLeaveCalendarBasedOnCustomField());
					companyEmployeeShortList.setValue(fieldValue);
					companyEmployeeShortList.setEqualityOperator(PayAsiaConstants.EMPLOYEE_FILTER_EQUAL_OPERATOR);
					EmployeeShortListDTO employeeShortListDTO = generalLogic
							.getShortListEmployeeIdsByCondition(companyEmployeeShortList, companyId);
					List<BigInteger> shortListEmployeeIds = employeeShortListDTO.getShortListEmployeeIds();
					shortListEmployeeIds.add(BigInteger.valueOf(employeeId));
					employeeShortListDTO.setShortListEmployeeIds(shortListEmployeeIds);
					conditionDTO.setEmployeeShortListDTO(employeeShortListDTO);

				}
			}

		} else {
			leaveStatusList.add(PayAsiaConstants.LEAVE_STATUS_COMPLETED);

		}

		HashMap<String, ArrayList<LeaveCalendarDTO>> dateValueMap = new HashMap<>();
		LeaveBalanceSummaryForm leaveBalanceSummaryForm = new LeaveBalanceSummaryForm();

		List<LeaveApplication> leaveList = new ArrayList<LeaveApplication>();
		ArrayList<String> roleList = new ArrayList<>();

		// Show Leave calendar based on custom field
		// this code remove by manoj
		/*
		 * SecurityContext securityContext = SecurityContextHolder.getContext();
		 * Authentication authentication = securityContext.getAuthentication();
		 */
		for (String role : rolesList) {
			if ("ROLE_LEAVE_MANAGER".equals(role)) {
				roleList.add("ROLE_LEAVE_MANAGER");
			}
		}

		if (isEnableLeaveCalBasedOnCustField) {
			if (conditionDTO.getEmployeeShortListDTO() == null) {
				EmployeeShortListDTO employeeShortListDTO = new EmployeeShortListDTO();
				List<BigInteger> shortListEmployeeIds = new ArrayList<BigInteger>();
				shortListEmployeeIds.add(BigInteger.valueOf(employeeId));
				employeeShortListDTO.setShortListEmployeeIds(shortListEmployeeIds);
				employeeShortListDTO.setEmployeeShortList(true);
				conditionDTO.setEmployeeShortListDTO(employeeShortListDTO);
			}
			if (roleList.contains("ROLE_LEAVE_MANAGER")) {
				List<LeaveApplication> selfLeaveList = leaveApplicationDAO.findByYearAndMonth(conditionDTO, companyId,
						null, null, leaveStatusList, year, month);
				EmployeeShortListDTO employeeShortListDTO = new EmployeeShortListDTO();
				employeeShortListDTO.setEmployeeShortList(false);
				conditionDTO.setEmployeeShortListDTO(employeeShortListDTO);
				List<LeaveApplication> empsLeaveList = leaveApplicationDAO.findByYearAndMonthForManager(conditionDTO,
						companyId, employeeId, leaveStatusList, year, month);

				leaveList.addAll(empsLeaveList);
				leaveList.addAll(selfLeaveList);
			} else {
				List<LeaveApplication> empLeaveList = leaveApplicationDAO.findByYearAndMonth(conditionDTO, companyId,
						null, null, leaveStatusList, year, month);
				leaveList.addAll(empLeaveList);
			}
		} else {
			EmployeeShortListDTO employeeShortListDTO = new EmployeeShortListDTO();
			employeeShortListDTO.setEmployeeShortList(false);
			conditionDTO.setEmployeeShortListDTO(employeeShortListDTO);

			if (roleList.contains("ROLE_LEAVE_MANAGER")) {

				List<LeaveApplication> selfLeaveList = leaveApplicationDAO.findByYearAndMonth(conditionDTO, companyId,
						employeeId, null, leaveStatusList, year, month);
				List<LeaveApplication> empsLeaveList = leaveApplicationDAO.findByYearAndMonthForManager(conditionDTO,
						companyId, employeeId, leaveStatusList, year, month);

				leaveList.addAll(empsLeaveList);
				leaveList.addAll(selfLeaveList);
			} else {
				List<LeaveApplication> empLeaveList = leaveApplicationDAO.findByYearAndMonth(conditionDTO, companyId,
						employeeId, null, leaveStatusList, year, month);
				leaveList.addAll(empLeaveList);
			}
		}

		if (leaveList != null) {
			Set<LeaveApplication> leaveApplications = new HashSet<>(leaveList);
			for (LeaveApplication leaveApplication : leaveApplications) {
				if (leaveApplication.getLeaveCancelApplication() != null) {
					continue;
				}
				SimpleDateFormat sdfDate = new SimpleDateFormat(companyVO.getDateFormat());
				Calendar calStart = new GregorianCalendar();
				Date startDate = DateUtils.stringToDate(DateUtils.timeStampToStringWOTimezone(
						leaveApplication.getStartDate(), companyVO.getDateFormat()), companyVO.getDateFormat());
				calStart.setTime(startDate);

				Calendar calEnd = new GregorianCalendar();
				Date endDate = DateUtils.stringToDate(
						DateUtils.timeStampToStringWOTimezone(leaveApplication.getEndDate(), companyVO.getDateFormat()),
						companyVO.getDateFormat());
				calEnd.setTime(endDate);

				while (startDate.before(endDate) || startDate.equals(endDate)) {
					if (dateValueMap.containsKey(sdfDate.format(calStart.getTime()))) {
						ArrayList<LeaveCalendarDTO> leaveAppList = dateValueMap.get(sdfDate.format(calStart.getTime()));
						LeaveCalendarDTO leaveCalendarDTO = new LeaveCalendarDTO();
						leaveCalendarDTO.setLeaveApplicationId(leaveApplication.getLeaveApplicationId());
						leaveCalendarDTO.setLeaveStatus(leaveApplication.getLeaveStatusMaster().getLeaveStatusName());
						// leaveCalendarDTO.setSubmitDate(new
						// Date().toString());
						LeaveApplicationWorkflow leaveApplicationWorkFlow = getDateByStatusAndLeaveAppId(
								leaveApplication.getLeaveStatusMaster().getLeaveStatusID(),
								leaveApplication.getLeaveApplicationId());
						if (leaveApplicationWorkFlow != null) {
							leaveCalendarDTO.setStatusDate(DateUtils.timeStampToStringWOTimezone(
									leaveApplicationWorkFlow.getCreatedDate(), companyVO.getDateFormat()));
						}

						leaveCalendarDTO.setLeaveAppByEmployee(leaveApplication.getEmployee().getFirstName() + "["
								+ leaveApplication.getEmployee().getEmployeeNumber() + "]");
						leaveCalendarDTO.setEmployeeLeaveId(leaveApplication.getEmployee().getEmployeeId());
						leaveAppList.add(leaveCalendarDTO);

					} else {
						ArrayList<LeaveCalendarDTO> leaveAppIdList = new ArrayList<>();
						LeaveCalendarDTO leaveCalendarDTO = new LeaveCalendarDTO();
						leaveCalendarDTO.setLeaveApplicationId(leaveApplication.getLeaveApplicationId());
						leaveCalendarDTO.setLeaveStatus(leaveApplication.getLeaveStatusMaster().getLeaveStatusName());
						LeaveApplicationWorkflow leaveApplicationWorkFlow = getDateByStatusAndLeaveAppId(
								leaveApplication.getLeaveStatusMaster().getLeaveStatusID(),
								leaveApplication.getLeaveApplicationId());
						if (leaveApplicationWorkFlow != null) {
							leaveCalendarDTO.setStatusDate(DateUtils.timeStampToStringWOTimezone(
									leaveApplicationWorkFlow.getCreatedDate(), companyVO.getDateFormat()));
						}
						leaveCalendarDTO.setLeaveAppByEmployee(leaveApplication.getEmployee().getFirstName() + "["
								+ leaveApplication.getEmployee().getEmployeeNumber() + "]");
						leaveCalendarDTO.setEmployeeLeaveId(leaveApplication.getEmployee().getEmployeeId());
						leaveAppIdList.add(leaveCalendarDTO);
						dateValueMap.put(sdfDate.format(calStart.getTime()), leaveAppIdList);
					}

					calStart.add(Calendar.DATE, 1);
					startDate = calStart.getTime();
				}
			}
		}
		leaveBalanceSummaryForm.setDateValueMapLeave(dateValueMap);
		return leaveBalanceSummaryForm;

	}

	private LeaveApplicationWorkflow getDateByStatusAndLeaveAppId(long statusId, Long leaveApplicationId) {
		LeaveApplicationWorkflow leaveApplicationWorkFlow = leavePreferenceDAO
				.findWorkFlowByStatusAndLeaveAppId(statusId, leaveApplicationId);
		return leaveApplicationWorkFlow;
	}

	@Override
	public LeaveBalanceSummaryResponse getEmpOnLeaveByDate(String[] leaveAppIds, Long companyId, Long employeeId,
			PageRequest pageDTO, SortCondition sortDTO, boolean isAdmin) {
		ArrayList<Long> leaveAppIdList = new ArrayList<>();
		for (int count = 0; count < leaveAppIds.length; count++) {
			if (StringUtils.isNotBlank(leaveAppIds[count])) {
				leaveAppIdList.add(Long.parseLong(leaveAppIds[count]));
			}
		}
		LinkedList<EmployeeLeaveSchemeTypeDTO> linkedList = new LinkedList<EmployeeLeaveSchemeTypeDTO>();
		List<LeaveApplication> leaveApplicationList = null;
		if (!isAdmin) {
			leaveApplicationList = leaveApplicationDAO.findByLeaveAppIdsEmp(companyId, employeeId, leaveAppIdList,
					pageDTO, sortDTO);
		} else {
			leaveApplicationList = leaveApplicationDAO.findByLeaveAppIds(companyId, leaveAppIdList, pageDTO, sortDTO);
		}
		// List<LeaveApplication> leaveApplicationList =
		// leaveApplicationDAO.findByLeaveAppIds(companyId, leaveAppIdList,
		// pageDTO, sortDTO);
		for (LeaveApplication leaveApplication : leaveApplicationList) {
			EmployeeLeaveSchemeTypeDTO leaveAppDTO = new EmployeeLeaveSchemeTypeDTO();
			leaveAppDTO.setStartDate(DateUtils.timeStampToString(leaveApplication.getStartDate()));
			leaveAppDTO.setEndDate(DateUtils.timeStampToString(leaveApplication.getEndDate()));
			leaveAppDTO.setEmployeeId(leaveApplication.getEmployee().getEmployeeId());
			String empName = "";
			empName += leaveApplication.getEmployee().getFirstName() + " ";
			if (StringUtils.isNotBlank(leaveApplication.getEmployee().getLastName())) {
				empName += leaveApplication.getEmployee().getLastName();
			}
			leaveAppDTO.setLeaveApplicationId(leaveApplication.getLeaveApplicationId());
			leaveAppDTO.setEmployeeName(empName);
			leaveAppDTO.setEmployeeNumber(leaveApplication.getEmployee().getEmployeeNumber());
			leaveAppDTO.setLeaveType(leaveApplication.getEmployeeLeaveSchemeType().getLeaveSchemeType()
					.getLeaveTypeMaster().getLeaveTypeName());
			
			leaveAppDTO.setStartSession(leaveApplication.getLeaveSessionMaster1().getSession());
			leaveAppDTO.setEndSession(leaveApplication.getLeaveSessionMaster2().getSession());
			
			if(leaveApplication.getLeaveStatusMaster().getLeaveStatusName().equals(PayAsiaConstants.LEAVE_STATUS_APPROVED))
			{
				leaveAppDTO.setStatus(PayAsiaConstants.LEAVE_STATUS_SUBMITTED);
			}
			else
			{
				leaveAppDTO.setStatus(leaveApplication.getLeaveStatusMaster().getLeaveStatusName());	
			}
			if (employeeId.equals(leaveAppDTO.getEmployeeId())) {

				linkedList.addFirst(leaveAppDTO);
			} else {
				linkedList.add(leaveAppDTO);
			}
			/*
			 * if
			 * (StringUtils.isNotBlank(leaveApplication.getLeaveSessionMaster1()
			 * .getSessionLabelKey())) { String labelMsg =
			 * messageSource.getMessage(leaveApplication.getLeaveSessionMaster1(
			 * ).getSessionLabelKey(), new Object[] {}, locale);
			 * leaveAppDTO.setStartSession(labelMsg); } else {
			 * leaveAppDTO.setStartSession(leaveApplication.
			 * getLeaveSessionMaster1().getSession()); }
			 * 
			 * if
			 * (StringUtils.isNotBlank(leaveApplication.getLeaveSessionMaster2()
			 * .getSessionLabelKey())) { String labelMsg =
			 * messageSource.getMessage(leaveApplication.getLeaveSessionMaster2(
			 * ).getSessionLabelKey(), new Object[] {}, locale);
			 * leaveAppDTO.setEndSession(labelMsg); } else {
			 * leaveAppDTO.setEndSession(leaveApplication.getLeaveSessionMaster2
			 * ().getSession()); }
			 */

		}
		int recordSize = (leaveApplicationDAO.getCountLeaveAppIds(companyId, leaveAppIdList)).intValue();
		LeaveBalanceSummaryResponse response = new LeaveBalanceSummaryResponse();
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

		}
		response.setRows(linkedList);
		response.setRecords(recordSize);
		return response;
	}

	@Override
	public LeaveBalanceSummaryResponse getPostLeaveSchemeData(String empNumber, String year, Long companyId) {
		boolean status = true;
		Company companyVO = companyDAO.findById(companyId);
		List<LeaveBalanceSummaryForm> leaveBalFormList = new ArrayList<LeaveBalanceSummaryForm>();
		LeaveBalanceSummaryResponse response = new LeaveBalanceSummaryResponse();
		Date calDate = DateUtils.stringToDate(DateUtils.timeStampToString(DateUtils.getCurrentTimestampWithTime()));
		Calendar cal = Calendar.getInstance();
		cal.setTime(calDate);

		response.setLeavePreferencePreApproval(leavePreferenceDAO.findByCompanyId(companyId).isPreApprovalRequired());

		if (year != null && !"null".equals(year)) {
			EmployeeLeaveScheme empLeaveSchemeVO = employeeLeaveSchemeDAO.checkEmpLeaveSchemeByDateAndEmpNum(empNumber,
					Integer.parseInt(year),
					DateUtils.timeStampToString(DateUtils.getCurrentTimestampWithTime(), companyVO.getDateFormat()),
					companyVO.getDateFormat(), companyId);
			if (empLeaveSchemeVO != null) {
				Date toDate = null;
				if (empLeaveSchemeVO.getEndDate() != null) {
					toDate = DateUtils.stringToDate(
							DateUtils.timeStampToString(empLeaveSchemeVO.getEndDate(), companyVO.getDateFormat()),
							companyVO.getDateFormat());
				}

				status = checkValidLeaveScheme(DateUtils.stringToDate(
						DateUtils.timeStampToString(empLeaveSchemeVO.getStartDate(), companyVO.getDateFormat()),
						companyVO.getDateFormat()), toDate, companyVO.getDateFormat());
				if (status) {
					List<EmployeeLeaveSchemeType> empLeaveSchemeTypeList = employeeLeaveSchemeTypeDAO
							.findByEmpLeaveSchemeId(empLeaveSchemeVO.getEmployeeLeaveSchemeId(), null, null);

					for (EmployeeLeaveSchemeType leaveSchemeType : empLeaveSchemeTypeList) {
						if (!leaveSchemeType.getLeaveSchemeType().getLeaveTypeMaster().isBackEndApplicationMode()) {
							continue;
						}
						if (!leaveSchemeType.getLeaveSchemeType().getLeaveTypeMaster().getVisibility()) {
							continue;
						}
						if (!leaveSchemeType.getLeaveSchemeType().getVisibility()) {
							continue;
						}
						LeaveBalanceSummaryForm leaveBalForm = new LeaveBalanceSummaryForm();
						leaveBalForm.setEmployeeLeaveSchemeTypeId(leaveSchemeType.getEmployeeLeaveSchemeTypeId());
						try {
							leaveBalForm.setLeaveType(URLEncoder.encode(
									leaveSchemeType.getLeaveSchemeType().getLeaveTypeMaster().getLeaveTypeName(),
									"UTF-8"));
						} catch (UnsupportedEncodingException ex) {
							LOGGER.error(ex.getMessage(), ex);
						}
						leaveBalForm.setLeaveTypeId(leaveSchemeType.getEmployeeLeaveSchemeTypeId());
						leaveBalForm.setLeaveSchemeId(
								leaveSchemeType.getEmployeeLeaveScheme().getLeaveScheme().getLeaveSchemeId());
						try {
							leaveBalForm.setLeaveScheme(URLEncoder.encode(
									leaveSchemeType.getEmployeeLeaveScheme().getLeaveScheme().getSchemeName(),
									"UTF-8"));
						} catch (UnsupportedEncodingException ex) {
							LOGGER.error(ex.getMessage(), ex);
						}
						leaveBalForm.setEmployeeName(getEmployeeName(empLeaveSchemeVO.getEmployee()));
						leaveBalForm.setEmployeeLeaveSchemeId(empLeaveSchemeVO.getEmployeeLeaveSchemeId());
						leaveBalFormList.add(leaveBalForm);
					}
					response.setLeaveBalanceSummaryFormList(leaveBalFormList);
					return response;
				} else {
					response.setLeaveBalanceSummaryFormList(leaveBalFormList);
					return response;
				}

			} else {
				response.setLeaveBalanceSummaryFormList(leaveBalFormList);
				response.setResponseString(PayAsiaConstants.LEAVE_BALANCE_SCHEME_ERROR);
				return response;
			}
		} else {
			response.setLeaveBalanceSummaryFormList(leaveBalFormList);
			response.setResponseString(PayAsiaConstants.LEAVE_BALANCE_SCHEME_ERROR);
			return response;
		}
	}

	public boolean checkValidLeaveScheme(Date fromDate, Date toDate, String compDateFormat) {
		boolean status = true;
		Date currentDate = DateUtils.stringToDate(
				DateUtils.timeStampToString(DateUtils.getCurrentTimestampWithTime(), compDateFormat), compDateFormat);

		if (currentDate.before(fromDate)) {
			status = false;
		}
		if (toDate != null) {
			if (currentDate.after(toDate)) {
				status = false;
			}
		}

		if (status) {
			return true;
		} else
			return false;

	}

	@Override
	public List<LeaveBalanceSummaryForm> getLeaveScheme(String empNumber, String year, Long companyId) {
		List<LeaveBalanceSummaryForm> leaveSchemeList = new ArrayList<>();
		List<EmployeeLeaveScheme> employeeLeaveSchemeList = employeeLeaveSchemeDAO.findByCompanyAndEmployee(empNumber,
				Integer.parseInt(year), companyId);
		if (!employeeLeaveSchemeList.isEmpty()) {
			for (EmployeeLeaveScheme employeeLeaveScheme : employeeLeaveSchemeList) {
				LeaveBalanceSummaryForm leaveBalanceSummaryForm = new LeaveBalanceSummaryForm();
				try {
					leaveBalanceSummaryForm.setLeaveScheme(
							URLEncoder.encode(employeeLeaveScheme.getLeaveScheme().getSchemeName(), "UTF-8"));
				} catch (UnsupportedEncodingException ex) {
					LOGGER.error(ex.getMessage(), ex);
				}
				leaveBalanceSummaryForm.setEmployeeLeaveSchemeId(employeeLeaveScheme.getEmployeeLeaveSchemeId());
				leaveSchemeList.add(leaveBalanceSummaryForm);
			}
		}

		return leaveSchemeList;
	}

	@Override
	public List<LeaveBalanceSummaryForm> getMyLeaveScheme(Long employeeId, String year, Long companyId) {
		List<LeaveBalanceSummaryForm> leaveSchemeList = new ArrayList<>();
		List<EmployeeLeaveScheme> employeeLeaveSchemeList = employeeLeaveSchemeDAO
				.findByCompanyAndEmployeeId(employeeId, Integer.parseInt(year), companyId);
		if (!employeeLeaveSchemeList.isEmpty()) {
			for (EmployeeLeaveScheme employeeLeaveScheme : employeeLeaveSchemeList) {
				LeaveBalanceSummaryForm leaveBalanceSummaryForm = new LeaveBalanceSummaryForm();
				try {
					leaveBalanceSummaryForm.setLeaveScheme(
							URLEncoder.encode(employeeLeaveScheme.getLeaveScheme().getSchemeName(), "UTF-8"));
				} catch (UnsupportedEncodingException ex) {
					LOGGER.error(ex.getMessage(), ex);
				}
				leaveBalanceSummaryForm.setEmployeeLeaveSchemeId(employeeLeaveScheme.getEmployeeLeaveSchemeId());
				leaveSchemeList.add(leaveBalanceSummaryForm);
			}
		}

		return leaveSchemeList;
	}

	@Override
	public AddLeaveFormResponse getCompletedLeaves(Long empId, PageRequest pageDTO, SortCondition sortDTO,
			String pageContextPath, Long companyId) {
    	Integer leaveCancelValidationDays = null;
		int recordSize=0;
		LeavePreference leavePreference = leavePreferenceDAO.findByCompanyId(companyId);
		if (leavePreference != null) {
			leaveCancelValidationDays = leavePreference.getCancelLeaveValidationDays();
		}
		Company companyVO = companyDAO.findById(companyId);
		AddLeaveConditionDTO conditionDTO = new AddLeaveConditionDTO();
		conditionDTO.setEmployeeId(empId);
		LeaveStatusMaster leaveStatusMaster = leaveStatusMasterDAO.findByCondition(PayAsiaConstants.LEAVE_STATUS_COMPLETED);
		conditionDTO.setLeaveStatusId(leaveStatusMaster.getLeaveStatusID());

		List<LeaveApplication> completedLeaves = leaveApplicationDAO.findByConditionCompletedForCancellation(conditionDTO, pageDTO, sortDTO);
		
		List<Tuple> cancellationsIds = leaveApplicationDAO.findLeaveCancellationIds(companyId);
		List<Long> cancelledApplicationIds = new ArrayList<>();
		for (Tuple leaveTuple : cancellationsIds) {
			cancelledApplicationIds.add((Long) leaveTuple.get("Cancellation_Id"));
		}
		List<AddLeaveForm> addLeaveFormList = new ArrayList<AddLeaveForm>();
		Date currentDate = DateUtils.stringToDate(DateUtils.timeStampToString(DateUtils.getCurrentTimestamp(), companyVO.getDateFormat()),
				companyVO.getDateFormat());
		for (LeaveApplication leaveApplication : completedLeaves) {

			if (leaveApplication.getLeaveCancelApplication() != null) {
				continue;
			}

			Date startDate = new Date(leaveApplication.getStartDate().getTime());
			if (leaveCancelValidationDays != null && leaveCancelValidationDays != 0) {
				Date validstartDate = org.apache.commons.lang.time.DateUtils.addDays(startDate, leaveCancelValidationDays);
				if (!validstartDate.after(currentDate)) {
					if (!currentDate.equals(validstartDate)) {
						continue;
					}

				}

			}
			if (leaveCancelValidationDays == null) {
				continue;
			}

			if (cancelledApplicationIds.contains(leaveApplication.getLeaveApplicationId())) {
				Long leaveAppId = leaveApplication.getLeaveApplicationId();
				List<LeaveApplication> cancelledLeaveApplications = leaveApplicationDAO.findCancelledLeaveApplications(leaveAppId, companyId);				
				Boolean isLeaveAppShown = false;
				for (LeaveApplication cancelledLeave : cancelledLeaveApplications) {

					if (cancelledLeave.getLeaveStatusMaster().getLeaveStatusDesc().equals(PayAsiaConstants.LEAVE_STATUS_COMPLETED)
							|| cancelledLeave.getLeaveStatusMaster().getLeaveStatusDesc().equals(PayAsiaConstants.LEAVE_STATUS_APPROVED)
							|| cancelledLeave.getLeaveStatusMaster().getLeaveStatusDesc().equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_SUBMITTED)) {
						isLeaveAppShown = true;
					} else if (cancelledLeave.getLeaveStatusMaster().getLeaveStatusDesc().equals(PayAsiaConstants.LEAVE_STATUS_REJECTED)
							|| cancelledLeave.getLeaveStatusMaster().getLeaveStatusDesc().equals(PayAsiaConstants.LEAVE_STATUS_WITHDRAWN)) {

						isLeaveAppShown = false;
					}

				}

				if (isLeaveAppShown) {
					continue;
				}

			}

			AddLeaveForm addLeaveForm = new AddLeaveForm();

			Set<LeaveApplicationReviewer> leaveApplicationReviewers = leaveApplication.getLeaveApplicationReviewers();
			List<LeaveApplicationReviewerDTO> leaveApplicationReviewerDTOs = new ArrayList<>();
			for (LeaveApplicationReviewer leaveApplicationReviewer : leaveApplicationReviewers) {
				LeaveApplicationReviewerDTO leaveApplicationReviewerDTO = new LeaveApplicationReviewerDTO();
				if (leaveApplicationReviewer.getWorkFlowRuleMaster().getRuleValue().equals("1")) {
					leaveApplicationReviewerDTO.setLeaveApplicationReviewer1(getEmployeeName(leaveApplicationReviewer.getEmployee()));
					leaveApplicationReviewerDTO.setLeaveApplicationReviewer1Id(leaveApplicationReviewer.getEmployee().getEmployeeId());

				} else if (leaveApplicationReviewer.getWorkFlowRuleMaster().getRuleValue().equals("2")) {
					leaveApplicationReviewerDTO.setLeaveApplicationReviewer1(getEmployeeName(leaveApplicationReviewer.getEmployee()));
					leaveApplicationReviewerDTO.setLeaveApplicationReviewer1Id(leaveApplicationReviewer.getEmployee().getEmployeeId());

				}
				if (leaveApplicationReviewer.getWorkFlowRuleMaster().getRuleValue().equals("3")) {
					leaveApplicationReviewerDTO.setLeaveApplicationReviewer1(getEmployeeName(leaveApplicationReviewer.getEmployee()));
					leaveApplicationReviewerDTO.setLeaveApplicationReviewer1Id(leaveApplicationReviewer.getEmployee().getEmployeeId());

				}
				leaveApplicationReviewerDTOs.add(leaveApplicationReviewerDTO);

			}
			addLeaveForm.setLeaveApplicationReviewers(leaveApplicationReviewerDTOs);
			addLeaveForm.setLeaveScheme(leaveApplication.getEmployeeLeaveSchemeType().getLeaveSchemeType().getLeaveScheme().getSchemeName());

			StringBuilder leaveType = new StringBuilder();
			leaveType.append(leaveApplication.getEmployeeLeaveSchemeType().getLeaveSchemeType().getLeaveTypeMaster().getLeaveTypeName());
			leaveType.append("<br>");

			addLeaveForm.setLeaveType(String.valueOf(leaveType));
			addLeaveForm.setAction(true);
			addLeaveForm.setCreateDate(DateUtils
					.timeStampToStringWOTimezone(leaveApplication.getCreatedDate(),PayAsiaConstants.DEFAULT_DATE_FORMAT_TIMESTAMP));
			addLeaveForm.setFromDate(DateUtils.timeStampToStringWOTimezone(leaveApplication.getStartDate(),UserContext.getWorkingCompanyDateFormat()));
			addLeaveForm.setToDate(DateUtils.timeStampToStringWOTimezone(leaveApplication.getEndDate(),UserContext.getWorkingCompanyDateFormat()));
			addLeaveForm.setLeaveApplicationId(FormatPreserveCryptoUtil.encrypt(leaveApplication.getLeaveApplicationId()));
			List<LeaveApplicationReviewer> applicationReviewers = new ArrayList<>(leaveApplication.getLeaveApplicationReviewers());
			Collections.sort(applicationReviewers, new LeaveReviewerComp());
			for (LeaveApplicationReviewer leaveApplicationReviewer : applicationReviewers) {
				if (leaveApplicationReviewer.getWorkFlowRuleMaster().getRuleValue().equals("1")) {

					LeaveApplicationWorkflow applicationWorkflow = leaveApplicationWorkflowDAO.findByCondition(leaveApplication.getLeaveApplicationId(),
							leaveApplicationReviewer.getEmployee().getEmployeeId());
					if (applicationWorkflow == null) {
						continue;
					}
					StringBuilder leaveReviewer1 = new StringBuilder(
							getStatusImage(PayAsiaConstants.LEAVE_STATUS_APPROVED, pageContextPath, leaveApplicationReviewer.getEmployee()));

					leaveReviewer1
							.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>" + applicationWorkflow.getCreatedDate() + "</span>");

					addLeaveForm.setLeaveReviewer1(String.valueOf(leaveReviewer1));

				}
				if (leaveApplicationReviewer.getWorkFlowRuleMaster().getRuleValue().equals("2")) {

					LeaveApplicationWorkflow applicationWorkflow = leaveApplicationWorkflowDAO.findByCondition(leaveApplication.getLeaveApplicationId(),
							leaveApplicationReviewer.getEmployee().getEmployeeId());
					if (applicationWorkflow == null) {
						continue;
					}

					StringBuilder leaveReviewer2 = new StringBuilder(
							getStatusImage(PayAsiaConstants.LEAVE_STATUS_APPROVED, pageContextPath, leaveApplicationReviewer.getEmployee()));

					if (applicationWorkflow.getCreatedDate() != null) {
						leaveReviewer2
								.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>" + applicationWorkflow.getCreatedDate() + "</span>");
					}

					addLeaveForm.setLeaveReviewer2(String.valueOf(leaveReviewer2));

				}
				if (leaveApplicationReviewer.getWorkFlowRuleMaster().getRuleValue().equals("3")) {

					LeaveApplicationWorkflow applicationWorkflow = leaveApplicationWorkflowDAO.findByCondition(leaveApplication.getLeaveApplicationId(),
							leaveApplicationReviewer.getEmployee().getEmployeeId());
					if (applicationWorkflow == null) {
						continue;
					}

					StringBuilder leaveReviewer3 = new StringBuilder(
							getStatusImage(PayAsiaConstants.LEAVE_STATUS_APPROVED, pageContextPath, leaveApplicationReviewer.getEmployee()));

					leaveReviewer3
							.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>" + applicationWorkflow.getCreatedDate() + "</span>");

					addLeaveForm.setLeaveReviewer3(String.valueOf(leaveReviewer3));

				}

			}

			addLeaveFormList.add(addLeaveForm);
		}
		recordSize=addLeaveFormList.size();
		AddLeaveFormResponse response = new AddLeaveFormResponse();
		List<AddLeaveForm> newList =  new ArrayList<>();
		if(pageDTO != null){
			newList =	addLeaveFormList.subList(getStartPosition(pageDTO), (getStartPosition(pageDTO)+ pageDTO.getPageSize())<recordSize? getStartPosition(pageDTO)+ pageDTO.getPageSize(): recordSize);
			response.setRows(newList);
		} else {
			response.setRows(addLeaveFormList);
		}	
			
		
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

		//response.setAddLeaveFormList(addLeaveFormList);

		return response;
	}

	protected int getStartPosition(PageRequest pageRequestDTO) {
		int firstRow = ((pageRequestDTO.getPageNumber() - 1) * pageRequestDTO.getPageSize());

		return firstRow;
	}

	/**
	 * Comparator Class for Ordering LeaveApplicationWorkflow List
	 */
	private class LeaveReviewerComp implements Comparator<LeaveApplicationReviewer> {
		public int compare(LeaveApplicationReviewer templateField, LeaveApplicationReviewer compWithTemplateField) {
			if (templateField.getLeaveApplicationReviewerId() > compWithTemplateField.getLeaveApplicationReviewerId()) {
				return 1;
			} else if (templateField.getLeaveApplicationReviewerId() < compWithTemplateField
					.getLeaveApplicationReviewerId()) {
				return -1;
			}
			return 0;

		}

	}

	/**
	 * Comparator Class for Ordering Employee LEave Reviewer List
	 */
	private class EmployeeLeaveReviewerComp implements Comparator<EmployeeLeaveReviewer> {
		public int compare(EmployeeLeaveReviewer templateField, EmployeeLeaveReviewer compWithTemplateField) {
			if (templateField.getEmployeeLeaveReviewerID() > compWithTemplateField.getEmployeeLeaveReviewerID()) {
				return 1;
			} else if (templateField.getEmployeeLeaveReviewerID() < compWithTemplateField
					.getEmployeeLeaveReviewerID()) {
				return -1;
			}
			return 0;

		}

	}

	private String getStatusImage(String status, String contextPath, Employee employee) {
		String imagePath = contextPath + "/resources/images/icon/16/";
		if (status.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_PENDING)) {
			imagePath = imagePath + "pending.png";
		} else if (status.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_APPROVED)) {
			imagePath = imagePath + "approved.png";
		} else if ("NA".equalsIgnoreCase(status)) {
			imagePath = imagePath + "pending-next-level.png";
		} else if (status.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_REJECTED)) {
			imagePath = imagePath + "rejected.png";
		}
		String employeeName = getEmployeeName(employee);

		return "<img src='" + imagePath + "'  />  " + employeeName;

	}

	@Override
	public void canCelLeave(LeaveBalanceSummaryForm leaveBalanceSummaryForm, Long employeeId, Long companyId,
			LeaveSessionDTO sessionDTO) {

		LeaveApplication leaveApplicationVO = leaveApplicationDAO
				.findById(leaveBalanceSummaryForm.getLeaveApplicationId());

		if (leaveBalanceSummaryForm.getLeaveApplicationId() != null && !leaveBalanceSummaryForm.getLeaveApplicationId()
				.equals(leaveApplicationVO.getLeaveApplicationId())) {
			throw new PayAsiaSystemException("Authentication Exception");
		}

		Employee reviewer = null;
		String applyTo = "";
		Company company = companyDAO.findById(companyId);
		Employee employee = employeeDAO.findById(employeeId);
		EmployeeLeaveSchemeType employeeLeaveSchemeType = leaveApplicationVO.getEmployeeLeaveSchemeType();
		List<EmployeeLeaveReviewer> employeeLeaveReviewers = employeeLeaveReviewerDAO.findByCondition(
				leaveApplicationVO.getEmployee().getEmployeeId(),
				employeeLeaveSchemeType.getEmployeeLeaveScheme().getEmployeeLeaveSchemeId(),
				employeeLeaveSchemeType.getEmployeeLeaveSchemeTypeId(),
				leaveApplicationVO.getEmployee().getCompany().getCompanyId());
		Collections.sort(employeeLeaveReviewers, new EmployeeLeaveReviewerComp());

		for (EmployeeLeaveReviewer employeeLeaveReviewer : employeeLeaveReviewers) {

			if (employeeLeaveReviewer.getWorkFlowRuleMaster().getRuleValue().equals("1")) {

				Employee reviewEmployee = getDelegatedEmployee(leaveApplicationVO.getEmployee().getEmployeeId(),
						employeeLeaveReviewer.getEmployee2().getEmployeeId());
				applyTo = reviewEmployee.getEmail();

			}

		}

		LeaveApplication leaveApplication = new LeaveApplication();
		leaveApplication.setLeaveCancelApplication(leaveApplicationVO);
		leaveApplication.setEmployee(employee);
		leaveApplication.setCompany(company);
		leaveApplication.setEmployeeLeaveSchemeType(employeeLeaveSchemeType);
		leaveApplication.setStartDate(leaveApplicationVO.getStartDate());
		leaveApplication.setEndDate(leaveApplicationVO.getEndDate());
		leaveApplication.setReason(leaveBalanceSummaryForm.getReason());
		leaveApplication.setApplyTo(applyTo);
		leaveApplication.setEmailCC(leaveBalanceSummaryForm.getLeaveCC());
		leaveApplication.setLeaveSessionMaster1(leaveApplicationVO.getLeaveSessionMaster1());
		leaveApplication.setLeaveSessionMaster2(leaveApplicationVO.getLeaveSessionMaster2());
		leaveApplication.setTotalDays(leaveApplicationVO.getTotalDays());
		LeaveStatusMaster leaveStatusMaster = null;
		leaveStatusMaster = leaveStatusMasterDAO.findByCondition(PayAsiaConstants.LEAVE_STATUS_SUBMITTED);
		leaveApplication.setLeaveStatusMaster(leaveStatusMaster);
		Date date = new Date();
		leaveApplication.setCreatedDate(new Timestamp(date.getTime()));
		leaveApplication.setUpdatedDate(new Timestamp(date.getTime()));

		LeaveApplication persistLeaveApplication = leaveApplicationDAO.saveReturn(leaveApplication);

		Set<LeaveApplicationCustomField> leaveApplicationCustomFieldsVOs = leaveApplicationVO
				.getLeaveApplicationCustomFields();
		for (LeaveApplicationCustomField leaveApplicationCustomFieldVO : leaveApplicationCustomFieldsVOs) {

			LeaveApplicationCustomField leaveApplicationCustomField = new LeaveApplicationCustomField();
			leaveApplicationCustomField.setLeaveApplication(persistLeaveApplication);
			leaveApplicationCustomField.setValue(leaveApplicationCustomFieldVO.getValue());
			leaveApplicationCustomField
					.setLeaveSchemeTypeCustomField(leaveApplicationCustomFieldVO.getLeaveSchemeTypeCustomField());

			leaveApplicationCustomFieldDAO.save(leaveApplicationCustomField);
		}

		Set<LeaveApplicationAttachment> leaveApplicationAttachmentVos = leaveApplicationVO
				.getLeaveApplicationAttachments();

		for (LeaveApplicationAttachment leaveApplicationAttachmentVO : leaveApplicationAttachmentVos) {

			LeaveApplicationAttachment leaveApplicationAttachment = new LeaveApplicationAttachment();
			leaveApplicationAttachment.setLeaveApplication(persistLeaveApplication);
			leaveApplicationAttachment.setFileName(leaveApplicationAttachmentVO.getFileName());
			leaveApplicationAttachment.setFileType(leaveApplicationAttachmentVO.getFileType());
			leaveApplicationAttachment.setUploadedDate(new Timestamp(date.getTime()));
			LeaveApplicationAttachment saveReturn = leaveApplicationAttachmentDAO
					.saveReturn(leaveApplicationAttachment);

			// Get Leave Attachment to save new object for cancel leave
			String fileExt = leaveApplicationAttachmentVO.getFileType();
			/*
			 * String filePath = "/company/" +
			 * leaveApplicationAttachmentVO.getLeaveApplication()
			 * .getCompany().getCompanyId() + "/" +
			 * PayAsiaConstants.LEAVE_ATTACHMENT_DIRECTORY_NAME + "/" +
			 * leaveApplicationAttachmentVO .getLeaveApplicationAttachmentId() +
			 * "." + fileExt;
			 */

			FilePathGeneratorDTO filePathGenerator = fileUtils.getFileCommonPath(downloadPath, rootDirectoryName,
					leaveApplicationAttachmentVO.getLeaveApplication().getCompany().getCompanyId(),
					PayAsiaConstants.LEAVE_ATTACHMENT_DIRECTORY_NAME,
					String.valueOf(leaveApplicationAttachmentVO.getLeaveApplicationAttachmentId()), null, null, fileExt,
					PayAsiaConstants.PAYASIA_DOC_READALL_EVENT, 0);
			String filePath = fileUtils.getGeneratedFilePath(filePathGenerator);

			File file = new File(filePath);
			try {
				// save Leave attachment to file directory
				/*
				 * String filePathForSaveAttach = downloadPath + "/company/" +
				 * leaveApplication.getCompany().getCompanyId() + "/" +
				 * PayAsiaConstants.LEAVE_ATTACHMENT_DIRECTORY_NAME + "/";
				 */

				FilePathGeneratorDTO filePathGeneratorSave = fileUtils.getFileCommonPath(downloadPath,
						rootDirectoryName, leaveApplication.getCompany().getCompanyId(),
						PayAsiaConstants.LEAVE_ATTACHMENT_DIRECTORY_NAME, null, null, null, null,
						PayAsiaConstants.PAYASIA_DOC_UPLOAD_EVENT, 0);
				String filePathForSaveAttach = fileUtils.getGeneratedFilePath(filePathGeneratorSave);

				if (PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION.equalsIgnoreCase(appDeployLocation)) {
					byte[] byteFile = IOUtils.toByteArray(awss3LogicImpl.readS3ObjectAsStream(filePath));
					String fileName = leaveApplicationAttachmentVO.getFileName();
					fileName = saveReturn.getLeaveApplicationAttachmentId() + "."
							+ fileName.substring(fileName.lastIndexOf('.') + 1);
					awss3LogicImpl.uploadByteArrayFile(byteFile, filePathForSaveAttach + fileNameSeperator + fileName);
				} else {
					FileUtils.uploadFile(Files.readAllBytes(file.toPath()), leaveApplicationAttachmentVO.getFileName(),
							filePathForSaveAttach, fileNameSeperator, saveReturn.getLeaveApplicationAttachmentId());
				}
			} catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
			}

		}

		String allowApprove = "";
		for (LeaveSchemeTypeWorkflow leaveSchemeTypeWorkflow : employeeLeaveSchemeType.getLeaveSchemeType()
				.getLeaveSchemeTypeWorkflows()) {
			if (leaveSchemeTypeWorkflow.getWorkFlowRuleMaster().getRuleName()
					.equalsIgnoreCase(PayAsiaConstants.LEAVE_SCHEME_DEF_ALLOW_APPROVE)) {
				allowApprove = leaveSchemeTypeWorkflow.getWorkFlowRuleMaster().getRuleValue();
			}
		}
		boolean leaveWorkflowNotRequired = false;
		LeavePreference leavePreferenceVO = leavePreferenceDAO.findByCompanyId(companyId);
		if (leavePreferenceVO != null) {
			leaveWorkflowNotRequired = leavePreferenceVO.isLeaveWorkflowNotRequired();
		}

		LeaveApplicationWorkflow leaveApplicationWorkflow = new LeaveApplicationWorkflow();

		if (leaveApplicationVO.getLeaveApplicationWorkflows().size() > 0) {

			LeaveApplicationWorkflow leaveApplicationWorkflowVO = leaveApplicationVO.getLeaveApplicationWorkflows()
					.iterator().next();

			leaveApplicationWorkflow.setLeaveApplication(persistLeaveApplication);
			leaveApplicationWorkflow.setLeaveStatusMaster(leaveStatusMaster);
			leaveApplicationWorkflow.setRemarks(leaveApplicationWorkflowVO.getRemarks());
			leaveApplicationWorkflow.setForwardTo(applyTo);
			leaveApplicationWorkflow.setEmailCC(leaveApplicationWorkflowVO.getEmailCC());
			leaveApplicationWorkflow.setEmployee(employee);
			leaveApplicationWorkflow.setCreatedDate(new Timestamp(date.getTime()));
			leaveApplicationWorkflow.setStartDate(leaveApplicationWorkflowVO.getStartDate());
			leaveApplicationWorkflow.setEndDate(leaveApplicationWorkflowVO.getEndDate());

			leaveApplicationWorkflow.setTotalDays(leaveApplicationWorkflowVO.getTotalDays());
			leaveApplicationWorkflow.setStartSessionMaster(leaveApplicationWorkflowVO.getStartSessionMaster());
			leaveApplicationWorkflow.setEndSessionMaster(leaveApplicationWorkflowVO.getEndSessionMaster());
			leaveApplicationWorkflowDAO.save(leaveApplicationWorkflow);

			for (EmployeeLeaveReviewer employeeLeaveReviewer : employeeLeaveReviewers) {

				LeaveApplicationReviewer leaveApplicationReviewer = new LeaveApplicationReviewer();
				leaveApplicationReviewer.setLeaveApplication(persistLeaveApplication);
				leaveApplicationReviewer.setWorkFlowRuleMaster(employeeLeaveReviewer.getWorkFlowRuleMaster());
				if (employeeLeaveReviewer.getWorkFlowRuleMaster().getRuleValue().equals("1")) {
					reviewer = getDelegatedEmployee(leaveApplicationVO.getEmployee().getEmployeeId(),
							employeeLeaveReviewer.getEmployee2().getEmployeeId());
					leaveApplicationReviewer.setPending(true);

				}
				if (employeeLeaveReviewer.getWorkFlowRuleMaster().getRuleValue().equals("2")) {
					if (allowApprove.length() == 3 && allowApprove.substring(1, 2).equals("1")
							&& leaveWorkflowNotRequired) {
						leaveApplicationReviewer.setPending(true);
					} else {
						leaveApplicationReviewer.setPending(false);
					}
				}
				if (employeeLeaveReviewer.getWorkFlowRuleMaster().getRuleValue().equals("3")) {
					if (allowApprove.length() == 3 && allowApprove.substring(2, 3).equals("1")
							&& leaveWorkflowNotRequired) {
						leaveApplicationReviewer.setPending(true);
					} else {
						leaveApplicationReviewer.setPending(false);
					}
				}
				leaveApplicationReviewer
						.setEmployee(getDelegatedEmployee(leaveApplicationVO.getEmployee().getEmployeeId(),
								employeeLeaveReviewer.getEmployee2().getEmployeeId()));
				leaveApplicationReviewerDAO.save(leaveApplicationReviewer);

			}
		}

		LeaveDTO leaveDTO = new LeaveDTO();
		leaveDTO.setFromDate(DateUtils.timeStampToString(leaveApplication.getStartDate()));
		leaveDTO.setToDate(DateUtils.timeStampToString(leaveApplication.getEndDate()));
		leaveDTO.setSession1(leaveApplication.getLeaveSessionMaster1().getLeaveSessionId());
		leaveDTO.setSession2(leaveApplication.getLeaveSessionMaster2().getLeaveSessionId());
		leaveDTO.setEmployeeLeaveSchemeTypeId(
				leaveApplication.getEmployeeLeaveSchemeType().getEmployeeLeaveSchemeTypeId());

		AddLeaveForm noOfDays = generalMailLogic.getNoOfDays(null, null, leaveDTO);
		BigDecimal totalLeaveDays = null;
		boolean isLeaveUnitDays = isLeaveUnitDays(companyId);
		if (isLeaveUnitDays) {
			totalLeaveDays = noOfDays.getNoOfDays();
		} else {
			totalLeaveDays = new BigDecimal(leaveApplicationVO.getTotalDays());
		}

		AddLeaveForm leaveBalance = generalMailLogic.getLeaveBalance(null, null,
				leaveApplication.getEmployeeLeaveSchemeType().getEmployeeLeaveSchemeTypeId());

		generalMailLogic.sendEMailForLeave(companyId, persistLeaveApplication,
				PayAsiaConstants.PAYASIA_SUB_CATEGORY_LEAVE_CANCEL_APPLY, totalLeaveDays,
				leaveBalance.getLeaveBalance(), employee, reviewer, sessionDTO, isLeaveUnitDays);

	}

	@Override
	public List<ComboValueDTO> getLeaveSessionList() {
		List<LeaveSessionMaster> leaveSessionMasters = leaveSessionMasterDAO.findAll();
		List<ComboValueDTO> sessionList = new ArrayList<>();
		for (LeaveSessionMaster leaveSessionMaster : leaveSessionMasters) {
			ComboValueDTO comboValueDTO = new ComboValueDTO();
			comboValueDTO.setLabel(leaveSessionMaster.getSession());
			comboValueDTO.setValue(leaveSessionMaster.getLeaveSessionId());
			comboValueDTO.setLabelKey(leaveSessionMaster.getSessionLabelKey());
			sessionList.add(comboValueDTO);
		}
		return sessionList;
	}

	@Override
	public AddLeaveForm getLeaveBalance(Long employeeLeaveSchemeTypeId) {
		AddLeaveForm addLeaveForm = new AddLeaveForm();
		if (employeeLeaveSchemeTypeId != null) {
			LeaveDTO leaveDTO = employeeLeaveSchemeTypeDAO.getLeaveBalance(employeeLeaveSchemeTypeId);
			addLeaveForm.setLeaveBalance(leaveDTO.getLeaveBalance());
		}

		return addLeaveForm;
	}

	@Override
	public AddLeaveForm getNoOfDays(Long companyId, Long employeeId, LeaveDTO leaveDTO) {
		AddLeaveForm addLeaveForm = new AddLeaveForm();
		LeaveDTO leaveDTORes = employeeLeaveSchemeTypeDAO.getNoOfDays(leaveDTO);
		addLeaveForm.setNoOfDays(leaveDTORes.getDays());
		addLeaveForm.setLeaveDTO(leaveDTORes);
		return addLeaveForm;
	}

	@Override
	public LeaveBalanceSummaryResponse getMyLeaveSchemeType(int year, PageRequest pageDTO, SortCondition sortDTO,
			Long companyId, Long employeeId) {
		// Company companyVO = companyDAO.findById(companyId);
		LeaveBalanceSummaryResponse response = new LeaveBalanceSummaryResponse();

		LeavePreferenceForm leavePreferenceForm = isEncashedVisible(companyId);

		EmployeeLeaveSchemeTypeDTO employeeLeaveSchemeTypeDTO = new EmployeeLeaveSchemeTypeDTO();
		if (year == 0) {
			List<EmployeeLeaveSchemeTypeDTO> employeeYearList = getDistinctYears(companyId);
			for (EmployeeLeaveSchemeTypeDTO yearList : employeeYearList) {
				if (yearList.isCurrentDateInLeaveCal()) {
					year = yearList.getYearValue();
				}

			}
		}
		createLeaveCalendarDate(year, companyId, employeeLeaveSchemeTypeDTO);
		List<EmployeeLeaveSchemeTypeDTO> employeeLeaveSchemeTypeDTOList = employeeLeaveSchemeTypeDAO
				.getEmployeeLeaveBalSummaryProcNew(employeeId, year, employeeLeaveSchemeTypeDTO.getStartDateTime(),
						employeeLeaveSchemeTypeDTO.getEndDateTime(), leavePreferenceForm);

		if (StringUtils.isNotBlank(sortDTO.getColumnName())) {
			BeanComparator beanComparator = null;

			if ("asc".equalsIgnoreCase(sortDTO.getOrderType())) {
				beanComparator = new BeanComparator(sortDTO.getColumnName());
			} else {
				beanComparator = new BeanComparator(sortDTO.getColumnName(),
						new ReverseComparator(new ComparableComparator()));
			}

			Collections.sort(employeeLeaveSchemeTypeDTOList, beanComparator);

		}

		response.setRows(employeeLeaveSchemeTypeDTOList);

		return response;
	}

	@Override
	public AddLeaveFormResponse getLeaveReviewers(Long leaveApplicationId) {
		AddLeaveFormResponse addLeaveFormResponse = new AddLeaveFormResponse();
		LeaveApplication leaveApplication = leaveApplicationDAO.findById(leaveApplicationId);
		AddLeaveForm addLeaveForm = new AddLeaveForm();

		List<LeaveApplicationReviewer> applicationReviewers = new ArrayList<>(
				leaveApplication.getLeaveApplicationReviewers());
		Collections.sort(applicationReviewers, new LeaveReviewerComp());
		EmployeeLeaveSchemeType employeeLeaveSchemeType = leaveApplication.getEmployeeLeaveSchemeType();

		List<EmployeeLeaveReviewer> employeeLeaveReviewers = new ArrayList<>(
				employeeLeaveSchemeType.getEmployeeLeaveReviewers());
		Collections.sort(employeeLeaveReviewers, new EmployeeLeaveReviewerComp());
		Integer ruleValue = 0;
		List<LeaveApplicationReviewerDTO> leaveApplicationReviewerDToList = new ArrayList<LeaveApplicationReviewerDTO>();
		LeaveApplicationReviewerDTO leaveApplicationReviewerDTO = new LeaveApplicationReviewerDTO();

		for (EmployeeLeaveReviewer employeeLeaveReviewer : employeeLeaveReviewers) {
			ruleValue++;

			if (employeeLeaveReviewer.getWorkFlowRuleMaster().getRuleValue().equals("1")) {
				leaveApplicationReviewerDTO
						.setLeaveApplicationReviewer1(getEmployeeName(employeeLeaveReviewer.getEmployee2()));
				leaveApplicationReviewerDTO
						.setLeaveApplicationReviewer1Id(employeeLeaveReviewer.getEmployee2().getEmployeeId());
				leaveApplicationReviewerDTO.setRuleValue(employeeLeaveReviewer.getWorkFlowRuleMaster().getRuleValue());
				byte[] byteImage = null;
				try {
					byteImage = employeeDetailLogic.getEmployeeImage(
							employeeLeaveReviewer.getEmployee2().getEmployeeId(), null, employeeImageWidth,
							employeeImageHeight);
				} catch (IOException e) {
					e.printStackTrace();
				}
				leaveApplicationReviewerDTO.setLeaveApplicationReviewer1Image(byteImage);

			} else if (employeeLeaveReviewer.getWorkFlowRuleMaster().getRuleValue().equals("2")) {
				leaveApplicationReviewerDTO
						.setLeaveApplicationReviewer2(getEmployeeName(employeeLeaveReviewer.getEmployee2()));
				leaveApplicationReviewerDTO
						.setLeaveApplicationReviewer2Id(employeeLeaveReviewer.getEmployee2().getEmployeeId());
				leaveApplicationReviewerDTO.setRuleValue(employeeLeaveReviewer.getWorkFlowRuleMaster().getRuleValue());
				byte[] byteImage = null;
				try {
					byteImage = employeeDetailLogic.getEmployeeImage(
							employeeLeaveReviewer.getEmployee2().getEmployeeId(), null, employeeImageWidth,
							employeeImageHeight);
				} catch (IOException e) {
					e.printStackTrace();
				}
				leaveApplicationReviewerDTO.setLeaveApplicationReviewer2Image(byteImage);

			}
			if (employeeLeaveReviewer.getWorkFlowRuleMaster().getRuleValue().equals("3")) {
				leaveApplicationReviewerDTO
						.setLeaveApplicationReviewer3(getEmployeeName(employeeLeaveReviewer.getEmployee2()));
				leaveApplicationReviewerDTO
						.setLeaveApplicationReviewer3Id(employeeLeaveReviewer.getEmployee2().getEmployeeId());
				leaveApplicationReviewerDTO.setRuleValue(employeeLeaveReviewer.getWorkFlowRuleMaster().getRuleValue());
				byte[] byteImage = null;
				try {
					byteImage = employeeDetailLogic.getEmployeeImage(
							employeeLeaveReviewer.getEmployee2().getEmployeeId(), null, employeeImageWidth,
							employeeImageHeight);
				} catch (IOException e) {
					e.printStackTrace();
				}
				leaveApplicationReviewerDTO.setLeaveApplicationReviewer3Image(byteImage);

			}

		}

		// Pre approval Leave cancelation
		LeavePreference leavePreference = leavePreferenceDAO
				.findByCompanyId(leaveApplication.getCompany().getCompanyId());
		if (leavePreference != null) {
			addLeaveForm.setPreApprovalReq(leavePreference.isPreApprovalRequired());
			addLeaveForm.setPreApprovalReqRemark(leavePreference.getPreApprovalReqRemark());
		}
		// Pre approval Leave cancelation ends

		if (leaveApplicationReviewerDTO.getLeaveApplicationReviewer1() != null) {
			leaveApplicationReviewerDToList.add(leaveApplicationReviewerDTO);
			addLeaveForm.setLeaveApplicationReviewerDToList(leaveApplicationReviewerDToList);

		}
		addLeaveForm.setRuleValue(ruleValue);
		addLeaveFormResponse.setAddLeaveForm(addLeaveForm);
		return addLeaveFormResponse;
	}

	@Override
	public List<LeaveBalanceSummaryForm> getEmployeeIdForManager(Long companyId, String searchString, Long employeeId) {
		List<LeaveBalanceSummaryForm> leaveBalanceSummaryFormList = new ArrayList<LeaveBalanceSummaryForm>();

		EmployeeShortListDTO employeeShortListDTO = generalLogic.getShortListEmployeeIds(employeeId, companyId);
		employeeShortListDTO.setEmployeeShortList(false);
		List<Tuple> employeeTupleList = employeeLeaveReviewerDAO.getEmployeeIdsTupleForLeaveReviewer(searchString,
				companyId, employeeId, employeeShortListDTO);

		for (Tuple employeeLeaveRev : employeeTupleList) {
			LeaveBalanceSummaryForm leaveBalanceSummaryForm = new LeaveBalanceSummaryForm();
			leaveBalanceSummaryForm
					.setEmployeeNumber(employeeLeaveRev.get(getAlias(Employee_.employeeNumber), String.class));
			String empName = "";
			empName += employeeLeaveRev.get(getAlias(Employee_.firstName), String.class) + " ";
			if (StringUtils.isNotBlank(employeeLeaveRev.get(getAlias(Employee_.lastName), String.class))) {
				empName += employeeLeaveRev.get(getAlias(Employee_.lastName), String.class);
			}
			leaveBalanceSummaryForm.setEmployeeName(empName);
			leaveBalanceSummaryForm.setEmployeeId(employeeLeaveRev.get(getAlias(Employee_.employeeId), Long.class));
			leaveBalanceSummaryFormList.add(leaveBalanceSummaryForm);

		}
		return leaveBalanceSummaryFormList;
	}

	@Override
	public String getEmployeeName(Long loggedInEmployeeId, String employeeNumber, Long companyId) {
		Employee employeeVO = employeeDAO.findByNumber(employeeNumber, companyId);
		EmployeeShortListDTO employeeShortListDTO = generalLogic.getShortListEmployeeIds(loggedInEmployeeId, companyId);
		if (employeeVO != null) {
			if ((employeeShortListDTO.getEmployeeShortList()) && (employeeShortListDTO.getShortListEmployeeIds()
					.contains(BigInteger.valueOf(employeeVO.getEmployeeId())))) {
				String employeeName = employeeVO.getFirstName();
				if (StringUtils.isNotBlank(employeeVO.getLastName())) {
					employeeName += " " + employeeVO.getLastName();
				}
				employeeName += "[" + employeeVO.getEmployeeNumber() + "]";
				return employeeName;
			} else {
				String employeeName = employeeVO.getFirstName();
				if (StringUtils.isNotBlank(employeeVO.getLastName())) {
					employeeName += " " + employeeVO.getLastName();
				}
				employeeName += "[" + employeeVO.getEmployeeNumber() + "]";
				return employeeName;
			}
		} else {
			return "";
		}
	}

	@Override
	public String getEmployeeNameForManager(Long loggedInEmployeeId, String employeeNumber, Long companyId) {
		EmployeeShortListDTO employeeShortListDTO = generalLogic.getShortListEmployeeIds(loggedInEmployeeId, companyId);
		employeeShortListDTO.setEmployeeShortList(false);
		Tuple employeeVO = employeeLeaveReviewerDAO.getEmployeeIdTupleForManager(employeeNumber, companyId,
				loggedInEmployeeId, employeeShortListDTO);
		if (employeeVO != null) {
			String employeeName = employeeVO.get(getAlias(Employee_.employeeId), Long.class) + "$"
					+ employeeVO.get(getAlias(Employee_.firstName), String.class);
			if (StringUtils.isNotBlank(employeeVO.get(getAlias(Employee_.lastName), String.class))) {
				employeeName += " " + employeeVO.get(getAlias(Employee_.lastName), String.class);
			}
			employeeName += "[" + employeeVO.get(getAlias(Employee_.employeeNumber), String.class) + "]";
			return employeeName;
		} else {
			return "";
		}
	}

	@Override
	public String deleteLeaveTransaction(Long companyId, Long leaveTranId) {
		// EmployeeLeaveSchemeTypeHistory employeeLeaveSchemeTypeHistory =
		// employeeLeaveSchemeTypeHistoryDAO.findById(leaveTranId);

		EmployeeLeaveSchemeTypeHistory employeeLeaveSchemeTypeHistory = employeeLeaveSchemeTypeHistoryDAO
				.findLeaveTransByCompanyId(leaveTranId, companyId);
		if (employeeLeaveSchemeTypeHistory == null) {
			throw new PayAsiaSystemException("Authentication Exception");
		}

		if (employeeLeaveSchemeTypeHistory.getAppCodeMaster().getCodeDesc()
				.equals(PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_APPROVED)) {
			LeaveApplication leaveApplication = employeeLeaveSchemeTypeHistory.getLeaveApplication();

			// delete Forfeit leave entry in EmployeeLeaveSchemeTypeHistory
			// table, only if current leave taken by Consider Leave Balance
			// from
			// Other leave
			LeaveSchemeTypeAvailingLeave leaveSchemeTypeAvailingLeave = leaveSchemeTypeAvailingLeaveDAO
					.findByLeaveSchemeType(employeeLeaveSchemeTypeHistory.getEmployeeLeaveSchemeType()
							.getLeaveSchemeType().getLeaveSchemeTypeId());
			if (leaveSchemeTypeAvailingLeave != null) {
				if (leaveSchemeTypeAvailingLeave.getConsiderLeaveBalanceFrom() != null) {
					EmployeeLeaveSchemeType empLeaveSchemeTypeConsiderFrom = employeeLeaveSchemeTypeDAO
							.findByLeaveSchIdAndEmpId(
									employeeLeaveSchemeTypeHistory.getLeaveApplication().getEmployee().getEmployeeId(),
									leaveSchemeTypeAvailingLeave.getConsiderLeaveBalanceFrom().getLeaveSchemeTypeId());
					if (empLeaveSchemeTypeConsiderFrom != null) {
						AppCodeMaster appcodeForfeit = appCodeMasterDAO.findByCategoryAndDesc(
								PayAsiaConstants.APP_CODE_LEAVE_TRANSACTION_TYPE,
								PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_FORFEITED);

						EmployeeLeaveSchemeTypeHistory empLeaveSchemeTypeHistoryVO = employeeLeaveSchemeTypeHistoryDAO
								.findByCondition(empLeaveSchemeTypeConsiderFrom.getEmployeeLeaveSchemeTypeId(),
										appcodeForfeit.getAppCodeID(),
										employeeLeaveSchemeTypeHistory.getLeaveApplication().getLeaveApplicationId());
						if (empLeaveSchemeTypeHistoryVO != null) {
							employeeLeaveSchemeTypeHistoryDAO.delete(empLeaveSchemeTypeHistoryVO);
						}
					}

				}
			}

			leaveApplicationReviewerDAO.deleteByCondition(leaveApplication.getLeaveApplicationId());
			leaveApplicationWorkflowDAO.deleteByCondition(leaveApplication.getLeaveApplicationId());
			leaveApplicationDAO.delete(leaveApplication);
		}

		employeeLeaveSchemeTypeHistoryDAO.delete(employeeLeaveSchemeTypeHistory);

		return null;

	}

	@Override
	public LeaveBalanceSummaryResponse getLeavetransactionData(Long leaveTranId, Long companyId) {
		LeaveBalanceSummaryResponse leaveBalanceSummaryResponse = new LeaveBalanceSummaryResponse();
		EmployeeLeaveSchemeTypeHistory employeeLeaveSchemeTypeHistory = employeeLeaveSchemeTypeHistoryDAO
				.findById(leaveTranId);
		if (employeeLeaveSchemeTypeHistory == null) {
			throw new PayAsiaSystemException("Authentication Exception");
		}
		PostLeavetransactionDTO postLeavetransactionDTO = new PostLeavetransactionDTO();
		postLeavetransactionDTO.setEmployeeLeaveSchemeTypeId(
				employeeLeaveSchemeTypeHistory.getEmployeeLeaveSchemeType().getEmployeeLeaveSchemeTypeId());
		postLeavetransactionDTO.setTransactionTypeId(employeeLeaveSchemeTypeHistory.getAppCodeMaster().getAppCodeID());
		postLeavetransactionDTO
				.setStartDate(DateUtils.timeStampToString(employeeLeaveSchemeTypeHistory.getStartDate()));
		postLeavetransactionDTO.setEndDate(DateUtils.timeStampToString(employeeLeaveSchemeTypeHistory.getEndDate()));
		if (employeeLeaveSchemeTypeHistory.getStartSessionMaster() != null) {
			postLeavetransactionDTO.setStartSessionMasterId(
					employeeLeaveSchemeTypeHistory.getStartSessionMaster().getLeaveSessionId());
		}
		if (employeeLeaveSchemeTypeHistory.getEndSessionMaster() != null) {
			postLeavetransactionDTO
					.setEndSessionMasterId(employeeLeaveSchemeTypeHistory.getEndSessionMaster().getLeaveSessionId());
		}

		postLeavetransactionDTO.setDays(employeeLeaveSchemeTypeHistory.getDays());
		postLeavetransactionDTO.setReason(employeeLeaveSchemeTypeHistory.getReason());
		postLeavetransactionDTO.setForfeitAtEndDate(employeeLeaveSchemeTypeHistory.getForfeitAtEndDate());
		leaveBalanceSummaryResponse.setPostLeavetransactionDTO(postLeavetransactionDTO);
		postLeavetransactionDTO.setEmployeeLeaveSchemeTypeHistoryId(
				employeeLeaveSchemeTypeHistory.getEmployeeLeaveSchemeTypeHistoryId());

		return leaveBalanceSummaryResponse;
	}

	@Override
	public LeaveBalanceSummaryResponse updatePostTransactionLeaveType(
			EmployeeLeaveSchemeTypeHistoryDTO employeeLeaveSchemeTypeHistoryDTO, Long companyId, Long employeeId,
			Long historyId) {
		LeaveBalanceSummaryResponse leaveBalanceSummaryResponse = new LeaveBalanceSummaryResponse();

		boolean isLeaveUnitDays = isLeaveUnitDays(companyId);
		if (!isLeaveUnitDays) {
			employeeLeaveSchemeTypeHistoryDTO.setFromSessionId(1l);
			employeeLeaveSchemeTypeHistoryDTO.setToSessionId(2l);
		}
		EmployeeLeaveSchemeTypeHistory employeeLeaveSchemeTypeHistoryVO = employeeLeaveSchemeTypeHistoryDAO
				.findById(historyId);
		if (employeeLeaveSchemeTypeHistoryVO == null) {
			throw new PayAsiaSystemException("Authentication Exception");
		}
		EmployeeLeaveSchemeType employeeLeaveSchemeType = employeeLeaveSchemeTypeHistoryVO.getEmployeeLeaveSchemeType();
		if (employeeLeaveSchemeTypeHistoryVO.getAppCodeMaster().getCodeDesc()
				.equals(PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_APPROVED)) {
			LeaveConditionDTO leaveConditionDTO = new LeaveConditionDTO();
			if (!isLeaveUnitDays) {
				leaveConditionDTO.setTotalHoursBetweenDates(employeeLeaveSchemeTypeHistoryDTO.getDays().floatValue());
				leaveConditionDTO.setLeaveUnitHours(true);
			}
			leaveConditionDTO
					.setEmployeeId(employeeLeaveSchemeType.getEmployeeLeaveScheme().getEmployee().getEmployeeId());
			leaveConditionDTO.setEmployeeLeaveSchemeTypeId(employeeLeaveSchemeType.getEmployeeLeaveSchemeTypeId());
			leaveConditionDTO.setStartDate(employeeLeaveSchemeTypeHistoryDTO.getStartDate());
			leaveConditionDTO.setEndDate(employeeLeaveSchemeTypeHistoryDTO.getEndDate());
			leaveConditionDTO.setStartSession(employeeLeaveSchemeTypeHistoryDTO.getFromSessionId());
			leaveConditionDTO.setEndSession(employeeLeaveSchemeTypeHistoryDTO.getToSessionId());
			leaveConditionDTO.setAttachementStatus(true);
			leaveConditionDTO.setPost(true);
			leaveConditionDTO.setEmployeeLeaveApplicationId(
					employeeLeaveSchemeTypeHistoryVO.getLeaveApplication().getLeaveApplicationId());

			LeaveDTO leaveDTOValidate = leaveApplicationDAO.validateLeaveApplication(leaveConditionDTO);
			leaveBalanceSummaryResponse.setLeaveDTO(leaveDTOValidate);
			if (leaveDTOValidate.getErrorCode() == 1) {
				return leaveBalanceSummaryResponse;

			}
		}

		if (employeeLeaveSchemeTypeHistoryDTO.getFromSessionId() != 0) {
			LeaveSessionMaster leaveStartSessionMaster = leaveSessionMasterDAO
					.findById(employeeLeaveSchemeTypeHistoryDTO.getFromSessionId());
			employeeLeaveSchemeTypeHistoryVO.setStartSessionMaster(leaveStartSessionMaster);
		}
		if (employeeLeaveSchemeTypeHistoryDTO.getToSessionId() != 0) {
			LeaveSessionMaster leaveToSessionMaster = leaveSessionMasterDAO
					.findById(employeeLeaveSchemeTypeHistoryDTO.getToSessionId());
			employeeLeaveSchemeTypeHistoryVO.setEndSessionMaster(leaveToSessionMaster);
		}

		employeeLeaveSchemeTypeHistoryVO.setDays(employeeLeaveSchemeTypeHistoryDTO.getDays());
		employeeLeaveSchemeTypeHistoryVO
				.setEndDate(DateUtils.stringToTimestamp(employeeLeaveSchemeTypeHistoryDTO.getEndDate()));
		employeeLeaveSchemeTypeHistoryVO
				.setStartDate(DateUtils.stringToTimestamp(employeeLeaveSchemeTypeHistoryDTO.getStartDate()));
		employeeLeaveSchemeTypeHistoryVO.setReason(employeeLeaveSchemeTypeHistoryDTO.getReason());
		employeeLeaveSchemeTypeHistoryVO.setForfeitAtEndDate(employeeLeaveSchemeTypeHistoryDTO.getForfeitAtEndDate());
		employeeLeaveSchemeTypeHistoryVO.setForfeitAtEndDate(employeeLeaveSchemeTypeHistoryDTO.getForfeitAtEndDate());
		employeeLeaveSchemeTypeHistoryDAO.update(employeeLeaveSchemeTypeHistoryVO);

		if (employeeLeaveSchemeTypeHistoryVO.getAppCodeMaster().getCodeDesc()
				.equals(PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_APPROVED)) {
			LeaveApplication leaveApplication = employeeLeaveSchemeTypeHistoryVO.getLeaveApplication();
			if (leaveApplication != null) {
				leaveApplication
						.setEndDate(DateUtils.stringToTimestamp(employeeLeaveSchemeTypeHistoryDTO.getEndDate()));
				leaveApplication
						.setStartDate(DateUtils.stringToTimestamp(employeeLeaveSchemeTypeHistoryDTO.getStartDate()));
				leaveApplication.setReason(employeeLeaveSchemeTypeHistoryDTO.getReason());

				if (isLeaveUnitDays) {
					LeaveDTO leaveDTO = new LeaveDTO();
					leaveDTO.setFromDate(employeeLeaveSchemeTypeHistoryDTO.getStartDate());
					leaveDTO.setToDate(employeeLeaveSchemeTypeHistoryDTO.getEndDate());
					leaveDTO.setSession1(employeeLeaveSchemeTypeHistoryDTO.getFromSessionId());
					leaveDTO.setSession2(employeeLeaveSchemeTypeHistoryDTO.getToSessionId());
					leaveDTO.setEmployeeLeaveSchemeTypeId(employeeLeaveSchemeType.getEmployeeLeaveSchemeTypeId());
					AddLeaveForm noOfDays = getNoOfDays(null, null, leaveDTO);
					employeeLeaveSchemeTypeHistoryDTO.setDays(noOfDays.getNoOfDays());
				}

				leaveApplication.setTotalDays(Float.parseFloat(employeeLeaveSchemeTypeHistoryDTO.getDays().toString()));

				if (employeeLeaveSchemeTypeHistoryDTO.getFromSessionId() != 0) {
					LeaveSessionMaster leaveStartSessionMaster = leaveSessionMasterDAO
							.findById(employeeLeaveSchemeTypeHistoryDTO.getFromSessionId());
					leaveApplication.setLeaveSessionMaster1(leaveStartSessionMaster);
				}
				if (employeeLeaveSchemeTypeHistoryDTO.getToSessionId() != 0) {
					LeaveSessionMaster leaveToSessionMaster = leaveSessionMasterDAO
							.findById(employeeLeaveSchemeTypeHistoryDTO.getToSessionId());
					leaveApplication.setLeaveSessionMaster2(leaveToSessionMaster);
				}
				leaveApplicationDAO.update(leaveApplication);

				// update Forfeit leave entry in EmployeeLeaveSchemeTypeHistory
				// table, only if current leave taken by Consider Leave Balance
				// from
				// Other leave
				if (leaveApplication.getLeaveCancelApplication() == null) {
					LeaveSchemeTypeAvailingLeave leaveSchemeTypeAvailingLeave = leaveSchemeTypeAvailingLeaveDAO
							.findByLeaveSchemeType(leaveApplication.getEmployeeLeaveSchemeType().getLeaveSchemeType()
									.getLeaveSchemeTypeId());
					if (leaveSchemeTypeAvailingLeave != null
							&& leaveSchemeTypeAvailingLeave.getConsiderLeaveBalanceFrom() != null) {
						EmployeeLeaveSchemeType empLeaveSchemeTypeConsiderFrom = employeeLeaveSchemeTypeDAO
								.findByLeaveSchIdAndEmpId(leaveApplication.getEmployee().getEmployeeId(),
										leaveSchemeTypeAvailingLeave.getConsiderLeaveBalanceFrom()
												.getLeaveSchemeTypeId());
						if (empLeaveSchemeTypeConsiderFrom != null) {
							AppCodeMaster appcodeForfeit = appCodeMasterDAO.findByCategoryAndDesc(
									PayAsiaConstants.APP_CODE_LEAVE_TRANSACTION_TYPE,
									PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_FORFEITED);

							EmployeeLeaveSchemeTypeHistory empLeaveSchemeTypeHistoryVO = employeeLeaveSchemeTypeHistoryDAO
									.findByCondition(empLeaveSchemeTypeConsiderFrom.getEmployeeLeaveSchemeTypeId(),
											appcodeForfeit.getAppCodeID(), leaveApplication.getLeaveApplicationId());
							empLeaveSchemeTypeHistoryVO.setReason(leaveApplication.getReason());
							empLeaveSchemeTypeHistoryVO.setDays(BigDecimal.valueOf(leaveApplication.getTotalDays()));
							empLeaveSchemeTypeHistoryVO.setStartDate(leaveApplication.getStartDate());
							empLeaveSchemeTypeHistoryVO.setEndDate(leaveApplication.getEndDate());
							empLeaveSchemeTypeHistoryVO
									.setStartSessionMaster(leaveApplication.getLeaveSessionMaster1());
							empLeaveSchemeTypeHistoryVO.setEndSessionMaster(leaveApplication.getLeaveSessionMaster2());
							employeeLeaveSchemeTypeHistoryDAO.update(empLeaveSchemeTypeHistoryVO);
						}
					}

				}

			}

		}

		return leaveBalanceSummaryResponse;
	}

	@Override
	public void cancelLeaveTransaction(Long companyId, Long leaveTranId, Long employeeId) {
		// EmployeeLeaveSchemeTypeHistory employeeLeaveSchemeTypeHistory =
		// employeeLeaveSchemeTypeHistoryDAO.findById(leaveTranId);
		EmployeeLeaveSchemeTypeHistory employeeLeaveSchemeTypeHistory = employeeLeaveSchemeTypeHistoryDAO
				.findLeaveTransByCompanyId(leaveTranId, companyId);
		if (employeeLeaveSchemeTypeHistory == null) {
			throw new PayAsiaSystemException("Authentication Exception");
		}
		Employee loggedInEmployee = employeeDAO.findById(employeeId);
		if (employeeLeaveSchemeTypeHistory.getAppCodeMaster().getCodeDesc()
				.equals(PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_APPROVED)) {
			LeaveApplication leaveApplicationToBeCancelled = employeeLeaveSchemeTypeHistory.getLeaveApplication();

			LeaveApplication leaveApplicationCancel = new LeaveApplication();
			leaveApplicationCancel.setCompany(leaveApplicationToBeCancelled.getCompany());
			leaveApplicationCancel.setEmployee(leaveApplicationToBeCancelled.getEmployee());
			leaveApplicationCancel.setEndDate(leaveApplicationToBeCancelled.getEndDate());
			leaveApplicationCancel.setStartDate(leaveApplicationToBeCancelled.getStartDate());
			leaveApplicationCancel.setReason(leaveApplicationToBeCancelled.getReason());
			leaveApplicationCancel.setLeaveCancelApplication(leaveApplicationToBeCancelled);
			leaveApplicationCancel.setApplyTo(leaveApplicationToBeCancelled.getApplyTo());
			leaveApplicationCancel.setLeaveStatusMaster(leaveApplicationToBeCancelled.getLeaveStatusMaster());
			leaveApplicationCancel.setCreatedDate(DateUtils.getCurrentTimestampWithTime());
			leaveApplicationCancel.setTotalDays(leaveApplicationToBeCancelled.getTotalDays());
			leaveApplicationCancel.setLeaveSessionMaster1(leaveApplicationToBeCancelled.getLeaveSessionMaster1());

			leaveApplicationCancel.setLeaveSessionMaster2(leaveApplicationToBeCancelled.getLeaveSessionMaster2());
			leaveApplicationCancel
					.setEmployeeLeaveSchemeType(leaveApplicationToBeCancelled.getEmployeeLeaveSchemeType());
			LeaveApplication persistLeaveApplicationCancel = leaveApplicationDAO.saveReturn(leaveApplicationCancel);

			LeaveApplicationReviewer leaveApplicationReviewerCancel = new LeaveApplicationReviewer();
			WorkFlowRuleMaster workflowRuleMaster = workFlowRuleMasterDAO
					.findByRuleNameValue(PayAsiaConstants.WORK_FLOW_RULE_NAME_LEAVE_REVIEWER, "1");
			leaveApplicationReviewerCancel.setWorkFlowRuleMaster(workflowRuleMaster);
			leaveApplicationReviewerCancel.setLeaveApplication(persistLeaveApplicationCancel);
			leaveApplicationReviewerCancel.setPending(false);
			leaveApplicationReviewerCancel.setEmployee(loggedInEmployee);
			leaveApplicationReviewerDAO.save(leaveApplicationReviewerCancel);

			LeaveApplicationWorkflow leaveApplicationWorkflowCancel = new LeaveApplicationWorkflow();
			leaveApplicationWorkflowCancel.setCreatedDate(DateUtils.getCurrentTimestampWithTime());
			leaveApplicationWorkflowCancel.setEmployee(loggedInEmployee);
			leaveApplicationWorkflowCancel.setEndDate(leaveApplicationCancel.getEndDate());
			leaveApplicationWorkflowCancel.setLeaveApplication(persistLeaveApplicationCancel);
			leaveApplicationWorkflowCancel.setEndSessionMaster(leaveApplicationCancel.getLeaveSessionMaster2());
			leaveApplicationWorkflowCancel.setStartSessionMaster(leaveApplicationCancel.getLeaveSessionMaster1());
			leaveApplicationWorkflowCancel.setForwardTo(loggedInEmployee.getEmail());
			leaveApplicationWorkflowCancel.setRemarks(leaveApplicationCancel.getReason());
			leaveApplicationWorkflowCancel.setLeaveStatusMaster(leaveApplicationCancel.getLeaveStatusMaster());
			leaveApplicationWorkflowCancel.setStartDate(leaveApplicationCancel.getStartDate());
			leaveApplicationWorkflowCancel.setTotalDays(leaveApplicationCancel.getTotalDays());
			leaveApplicationWorkflowDAO.save(leaveApplicationWorkflowCancel);

			LeaveStatusMaster leaveStatusCancelled = leaveStatusMasterDAO
					.findByCondition(PayAsiaConstants.LEAVE_STATUS_CANCELLED);
			leaveApplicationToBeCancelled.setLeaveStatusMaster(leaveStatusCancelled);
			leaveApplicationDAO.update(leaveApplicationToBeCancelled);

			// Delete Forfeit leave entry in EmployeeLeaveSchemeTypeHistory
			// table, only if current leave taken by Consider Leave Balance from
			// Other leave
			deleteForfeitFromOtherLeaveType(leaveApplicationCancel,
					leaveApplicationCancel.getEmployeeLeaveSchemeType().getLeaveSchemeType().getLeaveSchemeTypeId());

			EmployeeLeaveSchemeTypeHistory employeeLeaveSchemeTypeHistoryCancel = new EmployeeLeaveSchemeTypeHistory();
			employeeLeaveSchemeTypeHistoryCancel
					.setStartSessionMaster(employeeLeaveSchemeTypeHistory.getStartSessionMaster());
			employeeLeaveSchemeTypeHistoryCancel
					.setEndSessionMaster(employeeLeaveSchemeTypeHistory.getEndSessionMaster());
			employeeLeaveSchemeTypeHistoryCancel.setLeaveApplication(persistLeaveApplicationCancel);
			employeeLeaveSchemeTypeHistoryCancel.setLeaveStatusMaster(leaveStatusCancelled);
			employeeLeaveSchemeTypeHistoryCancel.setAppCodeMaster(employeeLeaveSchemeTypeHistory.getAppCodeMaster());
			employeeLeaveSchemeTypeHistoryCancel.setDays(employeeLeaveSchemeTypeHistory.getDays());
			employeeLeaveSchemeTypeHistoryCancel
					.setEmployeeLeaveSchemeType(employeeLeaveSchemeTypeHistory.getEmployeeLeaveSchemeType());
			employeeLeaveSchemeTypeHistoryCancel.setEndDate(employeeLeaveSchemeTypeHistory.getEndDate());
			employeeLeaveSchemeTypeHistoryCancel.setStartDate(employeeLeaveSchemeTypeHistory.getStartDate());
			employeeLeaveSchemeTypeHistoryCancel.setReason(employeeLeaveSchemeTypeHistory.getReason());
			employeeLeaveSchemeTypeHistoryCancel
					.setForfeitAtEndDate(employeeLeaveSchemeTypeHistory.getForfeitAtEndDate());
			employeeLeaveSchemeTypeHistoryDAO.save(employeeLeaveSchemeTypeHistoryCancel);

		}

	}

	@Override
	public void deleteForfeitFromOtherLeaveType(LeaveApplication leaveApplication, Long leaveSchemeTypeId) {
		LeaveSchemeTypeAvailingLeave leaveSchemeTypeAvailingLeave = leaveSchemeTypeAvailingLeaveDAO
				.findByLeaveSchemeType(leaveSchemeTypeId);
		if (leaveSchemeTypeAvailingLeave != null) {
			if (leaveSchemeTypeAvailingLeave.getConsiderLeaveBalanceFrom() != null) {
				EmployeeLeaveSchemeType empLeaveSchemeTypeConsiderFrom = employeeLeaveSchemeTypeDAO
						.findByLeaveSchIdAndEmpId(leaveApplication.getEmployee().getEmployeeId(),
								leaveSchemeTypeAvailingLeave.getConsiderLeaveBalanceFrom().getLeaveSchemeTypeId());
				if (empLeaveSchemeTypeConsiderFrom != null) {
					AppCodeMaster appcodeForfeit = appCodeMasterDAO.findByCategoryAndDesc(
							PayAsiaConstants.APP_CODE_LEAVE_TRANSACTION_TYPE,
							PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_FORFEITED);

					employeeLeaveSchemeTypeHistoryDAO.deleteByCondition(
							empLeaveSchemeTypeConsiderFrom.getEmployeeLeaveSchemeTypeId(),
							appcodeForfeit.getAppCodeID(),
							leaveApplication.getLeaveCancelApplication().getLeaveApplicationId());
				}

			}
		}
	}

	@Override
	public LeaveBalanceSummaryForm importPostLeaveTran(LeaveBalanceSummaryForm leaveBalanceSummaryForm, Long companyId,
			Long employeeId) {
		Employee loggedInEmployee = employeeDAO.findById(employeeId);

		Company company = companyDAO.findById(companyId);
		LeaveBalanceSummaryForm leaveBalSummExcelFieldForm = leaveBalanceSummaryForm;
		boolean isLeaveUnitDays = isLeaveUnitDays(companyId);

		LeaveBalanceSummaryForm LeaveBalanceSummaryFrm = new LeaveBalanceSummaryForm();
		List<DataImportLogDTO> dataImportLogDTOs = new ArrayList<>();
		HashMap<String, PostLeavetransactionDTO> postLeaveTranMap = new HashMap<>();
		setPostLeaveTransactionDTO(dataImportLogDTOs, leaveBalSummExcelFieldForm, postLeaveTranMap, companyId,
				isLeaveUnitDays);
		validateImpotedData(dataImportLogDTOs, postLeaveTranMap, companyId, isLeaveUnitDays);

		if (!dataImportLogDTOs.isEmpty()) {
			LeaveBalanceSummaryFrm.setDataValid(false);
			LeaveBalanceSummaryFrm.setDataImportLogDTOs(dataImportLogDTOs);
			return LeaveBalanceSummaryFrm;
		}

		Set<String> keySet = postLeaveTranMap.keySet();

		Map<String, AppCodeMaster> transactionTypeMap = new HashMap<>();
		List<AppCodeMaster> appCodeMasterList = appCodeMasterDAO
				.findByCondition(PayAsiaConstants.APP_CODE_LEAVE_TRANSACTION_TYPE);
		for (AppCodeMaster appCodeMaster : appCodeMasterList) {
			transactionTypeMap.put(appCodeMaster.getCodeDesc(), appCodeMaster);
		}

		Map<String, LeaveSessionMaster> leaveSessionMasterMap = new HashMap<>();
		List<LeaveSessionMaster> leaveSessionMasterList = leaveSessionMasterDAO.findAll();
		for (LeaveSessionMaster leaveSessionMaster : leaveSessionMasterList) {
			leaveSessionMasterMap.put(leaveSessionMaster.getSession(), leaveSessionMaster);
		}

		Map<String, LeaveStatusMaster> leaveStatusMasterMap = new HashMap<>();
		List<LeaveStatusMaster> leaveStatusMasterList = leaveStatusMasterDAO.findAll();
		for (LeaveStatusMaster leaveStatusMaster : leaveStatusMasterList) {
			leaveStatusMasterMap.put(leaveStatusMaster.getLeaveStatusName(), leaveStatusMaster);
		}

		WorkFlowRuleMaster reviewer1WorkFlowRuleMaster = workFlowRuleMasterDAO
				.findByRuleNameValue(PayAsiaConstants.WORK_FLOW_RULE_NAME_LEAVE_REVIEWER, "1");

		for (Iterator<String> iterator = keySet.iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			PostLeavetransactionDTO postLeavetransDTO = postLeaveTranMap.get(key);
			EmployeeLeaveSchemeType employeeLeaveSchemeType = employeeLeaveSchemeTypeDAO.findByCondition(
					postLeavetransDTO.getEmployeeNumber(), postLeavetransDTO.getLeaveTypeName(), companyId,
					postLeavetransDTO.getStartDate(), PayAsiaConstants.SERVER_SIDE_DATE_FORMAT);
			AppCodeMaster transactionType = transactionTypeMap.get(postLeavetransDTO.getTransactionTypeName());

			LeaveConditionDTO leaveConditionDTO = new LeaveConditionDTO();
			LeaveDTO leaveDTO = new LeaveDTO();

			LeaveApplication persistLeaveApplication = null;
			if (transactionType.getCodeDesc()
					.equalsIgnoreCase(PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_APPROVED)) {
				if (!isLeaveUnitDays) {
					leaveConditionDTO.setTotalHoursBetweenDates(postLeavetransDTO.getHours().floatValue());
					leaveConditionDTO.setLeaveUnitHours(true);
				}

				leaveConditionDTO
						.setEmployeeId(employeeLeaveSchemeType.getEmployeeLeaveScheme().getEmployee().getEmployeeId());
				leaveConditionDTO.setEmployeeLeaveSchemeTypeId(employeeLeaveSchemeType.getEmployeeLeaveSchemeTypeId());
				leaveDTO.setEmployeeLeaveSchemeTypeId(employeeLeaveSchemeType.getEmployeeLeaveSchemeTypeId());
				leaveConditionDTO.setStartDate(postLeavetransDTO.getStartDate());
				leaveDTO.setFromDate(postLeavetransDTO.getStartDate());
				leaveConditionDTO.setEndDate(postLeavetransDTO.getEndDate());
				leaveDTO.setToDate(postLeavetransDTO.getEndDate());

				LeaveSessionMaster leaveStartSession = leaveSessionMasterMap.get(postLeavetransDTO.getStartSession());
				leaveConditionDTO.setStartSession(leaveStartSession.getLeaveSessionId());
				leaveDTO.setSession1(leaveStartSession.getLeaveSessionId());

				LeaveSessionMaster leaveEndSession = leaveSessionMasterMap.get(postLeavetransDTO.getEndSession());
				leaveConditionDTO.setEndSession(leaveEndSession.getLeaveSessionId());
				leaveDTO.setSession2(leaveEndSession.getLeaveSessionId());

				leaveConditionDTO.setAttachementStatus(false);
				leaveConditionDTO.setPost(true);
				leaveConditionDTO.setDateFormat(PayAsiaConstants.SERVER_SIDE_DATE_FORMAT);

				LeaveDTO leaveDTOValidate = leaveApplicationDAO.validateLeaveApplication(leaveConditionDTO);
				if (leaveDTOValidate.getErrorCode() == 1) {
					DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();

					dataImportLogDTO.setErrorKey(leaveDTOValidate.getErrorKey());
					dataImportLogDTO.setErrorValue(leaveDTOValidate.getErrorValue());
					dataImportLogDTO.setRowNumber(Long.parseLong(key) + 1);
					dataImportLogDTOs.add(dataImportLogDTO);

					LeaveBalanceSummaryFrm.setDataValid(false);
					LeaveBalanceSummaryFrm.setDataImportLogDTOs(dataImportLogDTOs);
					throw new PayAsiaRollBackDataException(dataImportLogDTOs);

				}

				if (isLeaveUnitDays) {
					LeaveDTO leaveDTODaysValidate = employeeLeaveSchemeTypeDAO
							.getNoOfDaysForPostLeaveTranImport(leaveDTO, PayAsiaConstants.SERVER_SIDE_DATE_FORMAT);
					if (postLeavetransDTO.getDays().compareTo(leaveDTODaysValidate.getDays()) != 0) {
						DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
						dataImportLogDTO.setColName(PayAsiaConstants.PAYASIA_LEAVE_DAYS);
						dataImportLogDTO.setRemarks("payasia.leave.leave.days.invalid");
						dataImportLogDTO.setErrorValue(postLeavetransDTO.getDays().toString());
						dataImportLogDTO.setRowNumber(Long.parseLong(key) + 1);
						dataImportLogDTOs.add(dataImportLogDTO);

						LeaveBalanceSummaryFrm.setDataValid(false);
						LeaveBalanceSummaryFrm.setDataImportLogDTOs(dataImportLogDTOs);
						throw new PayAsiaRollBackDataException(dataImportLogDTOs);
					}
				}

				// List<EmployeeLeaveReviewer> employeeLeaveReviewer =
				// employeeLeaveReviewerDAO
				// .findByCondition(employeeLeaveSchemeType
				// .getEmployeeLeaveScheme().getEmployee()
				// .getEmployeeId(), employeeLeaveSchemeType
				// .getEmployeeLeaveScheme()
				// .getEmployeeLeaveSchemeId(),
				// employeeLeaveSchemeType
				// .getEmployeeLeaveSchemeTypeId(),
				// company.getCompanyId());
				//
				// if (employeeLeaveReviewer == null
				// || employeeLeaveReviewer.size() == 0) {
				// DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
				// dataImportLogDTO
				// .setColName(PayAsiaConstants.PAYASIA_LEAVE_REVIEWER);
				// dataImportLogDTO
				// .setRemarks("payasia.leave.reviewer.not.defined");
				// dataImportLogDTO.setErrorValue(postLeavetransDTO.getDays()
				// .toString());
				// dataImportLogDTO.setRowNumber(Long.parseLong(key) + 1);
				// dataImportLogDTOs.add(dataImportLogDTO);
				//
				// LeaveBalanceSummaryFrm.setDataValid(false);
				// LeaveBalanceSummaryFrm
				// .setDataImportLogDTOs(dataImportLogDTOs);
				// throw new PayAsiaRollBackDataException(dataImportLogDTOs);
				// }

				persistLeaveApplication = saveLeaveApplicationForImport(postLeavetransDTO, company,
						employeeLeaveSchemeType.getEmployeeLeaveScheme().getEmployee(), employeeLeaveSchemeType,
						leaveStatusMasterMap, leaveSessionMasterMap, reviewer1WorkFlowRuleMaster, loggedInEmployee,
						isLeaveUnitDays);
				// Insert Forfeit leave entry in EmployeeLeaveSchemeTypeHistory
				// table, only if current leave taken by Consider Leave Balance
				// from
				// Other leave
				forfeitFromOtherLeaveType(persistLeaveApplication,
						employeeLeaveSchemeType.getLeaveSchemeType().getLeaveSchemeTypeId());

				// Add Leave Application to keyPay Sync table name
				// 'KeyPay_Int_Leave_Application'
				if (!isLeaveUnitDays && persistLeaveApplication != null) {
					addLeaveAppToKeyPayInt(persistLeaveApplication);
				}

			}

			EmployeeLeaveSchemeTypeHistory employeeLeaveSchemeTypeHistory = new EmployeeLeaveSchemeTypeHistory();

			if (transactionType.getCodeDesc()
					.equalsIgnoreCase(PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_APPROVED)) {
				if (StringUtils.isNotBlank(postLeavetransDTO.getStartSession())) {
					LeaveSessionMaster leaveStartSessionMaster = leaveSessionMasterMap
							.get(postLeavetransDTO.getStartSession());

					employeeLeaveSchemeTypeHistory.setStartSessionMaster(leaveStartSessionMaster);
				}
				if (StringUtils.isNotBlank(postLeavetransDTO.getEndSession())) {
					LeaveSessionMaster leaveToSessionMaster = leaveSessionMasterMap
							.get(postLeavetransDTO.getEndSession());
					employeeLeaveSchemeTypeHistory.setEndSessionMaster(leaveToSessionMaster);
				}
			}

			employeeLeaveSchemeTypeHistory.setLeaveApplication(persistLeaveApplication);
			if (persistLeaveApplication != null) {
				employeeLeaveSchemeTypeHistory.setLeaveStatusMaster(persistLeaveApplication.getLeaveStatusMaster());
			}
			employeeLeaveSchemeTypeHistory.setAppCodeMaster(transactionType);
			if (isLeaveUnitDays) {
				employeeLeaveSchemeTypeHistory.setDays(postLeavetransDTO.getDays());
			} else {
				employeeLeaveSchemeTypeHistory.setDays(postLeavetransDTO.getHours());
			}

			employeeLeaveSchemeTypeHistory.setEmployeeLeaveSchemeType(employeeLeaveSchemeType);
			employeeLeaveSchemeTypeHistory.setEndDate(DateUtils.stringToTimestamp(postLeavetransDTO.getEndDate(),
					PayAsiaConstants.SERVER_SIDE_DATE_FORMAT));
			employeeLeaveSchemeTypeHistory.setStartDate(DateUtils.stringToTimestamp(postLeavetransDTO.getStartDate(),
					PayAsiaConstants.SERVER_SIDE_DATE_FORMAT));
			employeeLeaveSchemeTypeHistory.setReason(postLeavetransDTO.getReason());
			if (StringUtils.isBlank(postLeavetransDTO.getForfeitAtEndDateStr())) {
				employeeLeaveSchemeTypeHistory.setForfeitAtEndDate(false);
			} else {
				if (postLeavetransDTO.getForfeitAtEndDateStr().equalsIgnoreCase("yes")) {
					employeeLeaveSchemeTypeHistory.setForfeitAtEndDate(true);
				} else {
					employeeLeaveSchemeTypeHistory.setForfeitAtEndDate(false);
				}
			}

			employeeLeaveSchemeTypeHistoryDAO.save(employeeLeaveSchemeTypeHistory);
		}

		LeaveBalanceSummaryFrm.setDataValid(true);

		return LeaveBalanceSummaryFrm;

	}

	/**
	 * Purpose: To Validate impoted employee leave scheme.
	 * 
	 * @param empLeaveSchemeList
	 *            the employee LeaveScheme List
	 * @param companyId
	 *            the company Id
	 * @param dataImportLogDTOs
	 *            the data import log dtos
	 */
	private void validateImpotedData(List<DataImportLogDTO> dataImportLogDTOs,
			HashMap<String, PostLeavetransactionDTO> postLeaveTranMap, Long companyId, boolean isLeaveUnitDays) {
		List<String> transactionTypeList = new ArrayList<>();
		transactionTypeList.add(PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_APPROVED.toUpperCase());
		transactionTypeList.add(PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_CARRIED_FORWARD.toUpperCase());
		transactionTypeList.add(PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_CREDITED.toUpperCase());
		transactionTypeList.add(PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_FORFEITED.toUpperCase());

		List<String> leaveTypeNameList = new ArrayList<>();
		List<Tuple> leaveTypeListVO = leaveTypeMasterDAO.getLeaveTypeNameTupleList(companyId);
		for (Tuple leaveTypeTuple : leaveTypeListVO) {
			String leaveType = (String) leaveTypeTuple.get(getAlias(LeaveTypeMaster_.leaveTypeName), String.class);
			leaveTypeNameList.add(leaveType.toUpperCase());
		}

		List<String> employeeNameList = new ArrayList<>();
		List<Tuple> employeeNameListVO = employeeDAO.getEmployeeNameTupleList(companyId);
		for (Tuple empTuple : employeeNameListVO) {
			String employeeName = (String) empTuple.get(getAlias(Employee_.employeeNumber), String.class);
			employeeNameList.add(employeeName.toUpperCase());
		}

		Set<String> keySet = postLeaveTranMap.keySet();
		for (Iterator<String> iterator = keySet.iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			PostLeavetransactionDTO postLeavetransDTO = postLeaveTranMap.get(key);

			String rowNumber = key;
			if (key != "rowNumber") {

				if (StringUtils.isBlank(postLeavetransDTO.getEmployeeNumber())) {
					setPostLeaveTranImportLogs(dataImportLogDTOs, "Employee No", "payasia.empty",
							Long.parseLong(rowNumber));
				}
				if (StringUtils.isNotBlank(postLeavetransDTO.getEmployeeNumber())) {
					if (!employeeNameList.contains(postLeavetransDTO.getEmployeeNumber().toUpperCase())) {
						setPostLeaveTranImportLogs(dataImportLogDTOs, "Employee No", "payasia.invalid.employee.number",
								Long.parseLong(rowNumber));
					}
				}

				if (StringUtils.isBlank(postLeavetransDTO.getLeaveTypeName())) {
					setPostLeaveTranImportLogs(dataImportLogDTOs, PayAsiaConstants.PAYASIA_LEAVE_TYPE_NAME,
							"payasia.empty", Long.parseLong(rowNumber));
				}
				if (StringUtils.isNotBlank(postLeavetransDTO.getLeaveTypeName())) {
					if (!leaveTypeNameList.contains(postLeavetransDTO.getLeaveTypeName().toUpperCase())) {
						setPostLeaveTranImportLogs(dataImportLogDTOs, PayAsiaConstants.PAYASIA_LEAVE_TYPE_NAME,
								"payasia.invalid.leave.type", Long.parseLong(rowNumber));
					} else {
						EmployeeLeaveSchemeType employeeLeaveSchemeType = employeeLeaveSchemeTypeDAO.findByCondition(
								postLeavetransDTO.getEmployeeNumber(), postLeavetransDTO.getLeaveTypeName(), companyId,
								postLeavetransDTO.getStartDate(), PayAsiaConstants.SERVER_SIDE_DATE_FORMAT);
						if (employeeLeaveSchemeType == null) {
							setPostLeaveTranImportLogs(dataImportLogDTOs, PayAsiaConstants.PAYASIA_LEAVE_TYPE_NAME,
									"payasia.invalid.leave.type.scheme", Long.parseLong(rowNumber));
						}
					}
				}
				if (StringUtils.isBlank(postLeavetransDTO.getTransactionTypeName())) {

					setPostLeaveTranImportLogs(dataImportLogDTOs, PayAsiaConstants.PAYASIA_LEAVE_TRANSACTION_TYPE,
							"payasia.empty", Long.parseLong(rowNumber));
				}
				if (StringUtils.isNotBlank(postLeavetransDTO.getTransactionTypeName())) {
					if (!transactionTypeList.contains(postLeavetransDTO.getTransactionTypeName().toUpperCase())) {
						DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
						dataImportLogDTO.setColName("");
						dataImportLogDTO.setRemarks("payasia.leave.transaction.type.invalid");
						dataImportLogDTO.setErrorValue(postLeavetransDTO.getTransactionTypeName());
						dataImportLogDTO.setRowNumber(Long.parseLong(rowNumber));
						dataImportLogDTOs.add(dataImportLogDTO);

					}
					if (postLeavetransDTO.getTransactionTypeName().equalsIgnoreCase(
							PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_APPROVED) && isLeaveUnitDays) {
						if (StringUtils.isBlank(postLeavetransDTO.getStartSession())) {
							setPostLeaveTranImportLogs(dataImportLogDTOs, PayAsiaConstants.PAYASIA_START_SESSION,
									"payasia.empty", Long.parseLong(rowNumber));
						}
						if (StringUtils.isBlank(postLeavetransDTO.getEndSession())) {
							setPostLeaveTranImportLogs(dataImportLogDTOs, PayAsiaConstants.PAYASIA_END_SESSION,
									"payasia.empty", Long.parseLong(rowNumber));
						}
					}

				}
				if (StringUtils.isBlank(postLeavetransDTO.getStartDate())) {
					setPostLeaveTranImportLogs(dataImportLogDTOs, PayAsiaConstants.PAYASIA_FROM_DATE, "payasia.empty",
							Long.parseLong(rowNumber));
				}
				if (StringUtils.isNotBlank(postLeavetransDTO.getStartDate())) {
					int isStartDateValid = ValidationUtils.validateDate(postLeavetransDTO.getStartDate(),
							PayAsiaConstants.SERVER_SIDE_DATE_FORMAT);
					if (isStartDateValid == 1) {
						setPostLeaveTranImportLogs(dataImportLogDTOs, PayAsiaConstants.PAYASIA_FROM_DATE,
								"payasia.data.import.invalid.date", Long.parseLong(rowNumber));
					}

				}
				if (StringUtils.isBlank(postLeavetransDTO.getEndDate())) {
					setPostLeaveTranImportLogs(dataImportLogDTOs, PayAsiaConstants.PAYASIA_TO_DATE, "payasia.empty",
							Long.parseLong(rowNumber));
				}
				if (StringUtils.isNotBlank(postLeavetransDTO.getEndDate())) {
					int isStartDateValid = ValidationUtils.validateDate(postLeavetransDTO.getEndDate(),
							PayAsiaConstants.SERVER_SIDE_DATE_FORMAT);
					if (isStartDateValid == 1) {
						setPostLeaveTranImportLogs(dataImportLogDTOs, PayAsiaConstants.PAYASIA_TO_DATE,
								"payasia.data.import.invalid.date", Long.parseLong(rowNumber));
					}

				}

				if (postLeavetransDTO.getDays() == null && isLeaveUnitDays) {
					setPostLeaveTranImportLogs(dataImportLogDTOs, PayAsiaConstants.PAYASIA_LEAVE_DAYS, "payasia.empty",
							Long.parseLong(rowNumber));
				}
				if (postLeavetransDTO.getHours() == null && !isLeaveUnitDays) {
					setPostLeaveTranImportLogs(dataImportLogDTOs, PayAsiaConstants.PAYASIA_LEAVE_HOURS, "payasia.empty",
							Long.parseLong(rowNumber));
				}

			}

		}
	}

	public void setPostLeaveTransactionDTO(List<DataImportLogDTO> dataImportLogDTOs,
			LeaveBalanceSummaryForm leaveBalSummExcelFieldForm,
			HashMap<String, PostLeavetransactionDTO> postLeaveTranMap, Long companyId, boolean isLeaveUnitDays) {
		for (HashMap<String, String> map : leaveBalSummExcelFieldForm.getImportedData()) {
			PostLeavetransactionDTO postLeavetransactionDTO = new PostLeavetransactionDTO();
			Set<String> keySet = map.keySet();
			String rowNumber = map.get("rowNumber");
			for (Iterator<String> iterator = keySet.iterator(); iterator.hasNext();) {
				String key = (String) iterator.next();
				String value = (String) map.get(key);
				if (key != "rowNumber") {

					if (key.equals("Employee No") || key.equals("Employee Number")) {
						postLeavetransactionDTO.setEmployeeNumber(value);
					}
					if (key.equals(PayAsiaConstants.PAYASIA_LEAVE_TYPE_NAME)) {
						postLeavetransactionDTO.setLeaveTypeName(value);
					}
					if (key.equals(PayAsiaConstants.PAYASIA_LEAVE_TRANSACTION_TYPE)) {
						postLeavetransactionDTO.setTransactionTypeName(value);
					}
					if (key.equals(PayAsiaConstants.PAYASIA_FROM_DATE)) {
						postLeavetransactionDTO.setStartDate(value);
					}
					if (key.equals(PayAsiaConstants.PAYASIA_TO_DATE)) {
						postLeavetransactionDTO.setEndDate(value);
					}
					if (isLeaveUnitDays) {
						if (key.equals(PayAsiaConstants.PAYASIA_START_SESSION)) {
							postLeavetransactionDTO.setStartSession(value);
						}
						if (key.equals(PayAsiaConstants.PAYASIA_END_SESSION)) {
							postLeavetransactionDTO.setEndSession(value);
						}
					} else {
						postLeavetransactionDTO.setStartSession(PayAsiaConstants.LEAVE_SESSION_1);
						postLeavetransactionDTO.setEndSession(PayAsiaConstants.LEAVE_SESSION_2);
					}

					if (key.equals("Forfeit At End Date")) {
						postLeavetransactionDTO.setForfeitAtEndDateStr(value);
					}
					if (key.equals(PayAsiaConstants.PAYASIA_LEAVE_DAYS) && isLeaveUnitDays) {
						if (StringUtils.isNotBlank(value)) {
							postLeavetransactionDTO.setDays(new BigDecimal(value));
						}
					}
					if (key.equals(PayAsiaConstants.PAYASIA_LEAVE_HOURS) && !isLeaveUnitDays) {
						if (StringUtils.isNotBlank(value)) {
							postLeavetransactionDTO.setHours(new BigDecimal(value));
						}
					}
					if ("Reason".equals(key)) {
						postLeavetransactionDTO.setReason(value);
					}
					postLeaveTranMap.put(rowNumber, postLeavetransactionDTO);
				}

			}

		}
	}

	public void setPostLeaveTranImportLogs(List<DataImportLogDTO> dataImportLogDTOs, String key, String remarks,
			Long rowNumber) {
		DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
		dataImportLogDTO.setColName(key);
		dataImportLogDTO.setRemarks(remarks);
		dataImportLogDTO.setRowNumber(rowNumber + 1);
		dataImportLogDTOs.add(dataImportLogDTO);
	}

	@Override
	public LeavePreferenceForm isEncashedVisible(Long companyId) {
		LeavePreferenceForm leavePreferenceForm = new LeavePreferenceForm();
		LeavePreference leavePreferenceVO = leavePreferenceDAO.findByCompanyId(companyId);
		if (leavePreferenceVO == null) {
			leavePreferenceForm.setShowFullEntitlement(true);
			leavePreferenceForm.setShowEncashed(true);
			leavePreferenceForm.setDefaultEmailCC(false);
			leavePreferenceForm.setLeaveUnit(PayAsiaConstants.PAYASIA_LEAVE_UNIT_IN_DAYS);
			return leavePreferenceForm;
		}

		if (leavePreferenceVO.getShowEncashed() == null) {
			leavePreferenceForm.setShowEncashed(true);
		} else {
			leavePreferenceForm.setShowEncashed(leavePreferenceVO.getShowEncashed());
		}

		if (leavePreferenceVO.getShowFullEntitlement() == null) {
			leavePreferenceForm.setShowFullEntitlement(true);
		} else {
			leavePreferenceForm.setShowFullEntitlement(leavePreferenceVO.getShowFullEntitlement());
		}

		if (leavePreferenceVO.getLeaveUnit() != null) {
			leavePreferenceForm.setLeaveUnit(leavePreferenceVO.getLeaveUnit().getCodeDesc());
		} else {
			leavePreferenceForm.setLeaveUnit(PayAsiaConstants.PAYASIA_LEAVE_UNIT_IN_DAYS);
		}

		leavePreferenceForm.setDefaultEmailCC(leavePreferenceVO.isDefaultEmailCC());
		if (leavePreferenceVO.getCompany().getCountryMaster() != null) {
			leavePreferenceForm
					.setCompanyCountryName(leavePreferenceVO.getCompany().getCountryMaster().getCountryName());
		}

		return leavePreferenceForm;
	}

	@Override
	public LeaveBalanceSummaryResponse getLeaveTransactionHistory(Long leaveApplicationId, String transactionType,
			Long companyId) {
		LeaveBalanceSummaryResponse response = new LeaveBalanceSummaryResponse();
		LeaveApplication leaveApplicationVO = leaveApplicationDAO.findById(leaveApplicationId);
		Set<LeaveApplicationWorkflow> leaveApplicationWorkflows = leaveApplicationVO.getLeaveApplicationWorkflows();
		LeaveApplication leaveApplicationVOforFirstRequest = null;
		LeavePreference leavePreference = leavePreferenceDAO.findByCompanyId(companyId);

		response.setLeavePreferencePreApproval(leavePreference.isPreApprovalRequired());
		response.setLeaveExtensionPreference(leavePreference.isLeaveExtensionRequired());

		if (leavePreference.isPreApprovalRequired()) {
			response.setPreApprovalReq(leaveApplicationVO.getPreApprovalRequest());
			if (leaveApplicationVO.getLeaveCancelApplication() != null) {
				leaveApplicationVOforFirstRequest = leaveApplicationDAO
						.findById(leaveApplicationVO.getLeaveCancelApplication().getLeaveApplicationId());
				response.setPreApprovalReqFirst(leaveApplicationVO.getPreApprovalRequest());
				response.setPreApprovalReq(leaveApplicationVOforFirstRequest.getPreApprovalRequest());
			} else {
				response.setPreApprovalReqFirst(false);
				response.setPreApprovalReq(leaveApplicationVO.getPreApprovalRequest());
			}
		}

		if (leavePreference.isLeaveExtensionRequired()) {
			response.setLeaveExtension(leaveApplicationVO.getLeaveExtension());
		}

		EmployeeLeaveSchemeTypeHistoryDTO historyDTO = new EmployeeLeaveSchemeTypeHistoryDTO();
		historyDTO.setReason(leaveApplicationVO.getReason());
		historyDTO.setUpdatedDate(DateUtils.timeStampToString(leaveApplicationVO.getUpdatedDate()));
		if (StringUtils.isNotBlank(leaveApplicationVO.getCreatedBy())
				&& !leaveApplicationVO.getCreatedBy().equalsIgnoreCase("0")) {
			Employee submittedByEmp = employeeDAO.findById(Long.valueOf(leaveApplicationVO.getCreatedBy()));
			if (submittedByEmp != null) {
				historyDTO.setSubmittedBy(getEmployeeName(submittedByEmp));
			}
		} else {
		}

		List<LeaveApplicationAttachmentDTO> leaveApplicationAttachmentDTOs = new ArrayList<LeaveApplicationAttachmentDTO>();

		for (LeaveApplicationAttachment leaveApplicationAttachment : leaveApplicationVO
				.getLeaveApplicationAttachments()) {
			LeaveApplicationAttachmentDTO applicationAttachmentDTO = new LeaveApplicationAttachmentDTO();
			applicationAttachmentDTO.setFileName(leaveApplicationAttachment.getFileName());
			applicationAttachmentDTO.setLeaveApplicationAttachmentId(
					FormatPreserveCryptoUtil.encrypt(leaveApplicationAttachment.getLeaveApplicationAttachmentId()));
			leaveApplicationAttachmentDTOs.add(applicationAttachmentDTO);
		}
		response.setAttachmentList(leaveApplicationAttachmentDTOs);

		List<LeaveApplicationWorkflowDTO> applicationWorkflowDTOs = new ArrayList<>();
		for (LeaveApplicationWorkflow applicationWorkflow : leaveApplicationWorkflows) {
			if (applicationWorkflow.getLeaveStatusMaster().getLeaveStatusName()
					.equals(PayAsiaConstants.LEAVE_STATUS_SUBMITTED)) {
				historyDTO.setCreatedDate(DateUtils.timeStampToString(applicationWorkflow.getCreatedDate()));
				continue;
			}
			LeaveApplicationWorkflowDTO applicationWorkflowDTO = new LeaveApplicationWorkflowDTO();
			applicationWorkflowDTO.setUserRemarks(applicationWorkflow.getRemarks());

			if (applicationWorkflow.getLeaveStatusMaster().getLeaveStatusName()
					.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_APPROVED)) {
				applicationWorkflowDTO.setStatus("payasia.approved");
			} else if (applicationWorkflow.getLeaveStatusMaster().getLeaveStatusName()
					.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_SUBMITTED)) {
				applicationWorkflowDTO.setStatus("payasia.submitted");
			} else if (applicationWorkflow.getLeaveStatusMaster().getLeaveStatusName()
					.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_WITHDRAWN)) {
				applicationWorkflowDTO.setStatus("payasia.withdrawn");
			} else if (applicationWorkflow.getLeaveStatusMaster().getLeaveStatusName()
					.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_REJECTED)) {
				applicationWorkflowDTO.setStatus("payasia.rejected");
			} else if (applicationWorkflow.getLeaveStatusMaster().getLeaveStatusName()
					.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_CANCELLED)) {
				applicationWorkflowDTO.setStatus("payasia.cancelled");
			} else if (applicationWorkflow.getLeaveStatusMaster().getLeaveStatusName()
					.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_COMPLETED)) {
				applicationWorkflowDTO.setStatus("payasia.completed");
			} else if (applicationWorkflow.getLeaveStatusMaster().getLeaveStatusName()
					.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_DRAFT)) {
				applicationWorkflowDTO.setStatus("payasia.draft");
			} else if (applicationWorkflow.getLeaveStatusMaster().getLeaveStatusName()
					.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_PENDING)) {
				applicationWorkflowDTO.setStatus("payasia.pending");
			}

			applicationWorkflowDTO.setEmployeeInfo(getEmployeeName(applicationWorkflow.getEmployee()));
			applicationWorkflowDTO.setCreatedDate(DateUtils.timeStampToString(applicationWorkflow.getUpdatedDate()));
			applicationWorkflowDTOs.add(applicationWorkflowDTO);
		}

		historyDTO.setWorkflowList(applicationWorkflowDTOs);
		historyDTO.setFromSessionId(leaveApplicationVO.getLeaveSessionMaster1().getLeaveSessionId());
		historyDTO.setFromSessionLabelKey(leaveApplicationVO.getLeaveSessionMaster1().getSessionLabelKey());
		historyDTO.setToSessionId(leaveApplicationVO.getLeaveSessionMaster2().getLeaveSessionId());
		historyDTO.setToSessionLabelKey(leaveApplicationVO.getLeaveSessionMaster2().getSessionLabelKey());
		historyDTO.setFromSession(leaveApplicationVO.getLeaveSessionMaster1().getSession());
		historyDTO.setToSession(leaveApplicationVO.getLeaveSessionMaster2().getSession());
		response.setEmployeeLeaveSchemeTypeHistoryDTO(historyDTO);
		return response;
	}

	@Override
	public LeaveApplicationAttachmentDTO viewAttachment(long attachmentId) {

		LeaveApplicationAttachment attachment = leaveApplicationAttachmentDAO.findById(attachmentId);
		LeaveApplicationAttachmentDTO applicationAttachmentDTO = new LeaveApplicationAttachmentDTO();

		String fileExt = attachment.getFileType();
		/*
		 * String filePath = "/company/" +
		 * attachment.getLeaveApplication().getCompany().getCompanyId() + "/" +
		 * PayAsiaConstants.LEAVE_ATTACHMENT_DIRECTORY_NAME + "/" +
		 * attachment.getLeaveApplicationAttachmentId() + "." + fileExt;
		 */

		FilePathGeneratorDTO filePathGenerator = fileUtils.getFileCommonPath(downloadPath, rootDirectoryName,
				attachment.getLeaveApplication().getCompany().getCompanyId(),
				PayAsiaConstants.LEAVE_ATTACHMENT_DIRECTORY_NAME,
				String.valueOf(attachment.getLeaveApplicationAttachmentId()), null, null, fileExt,
				PayAsiaConstants.PAYASIA_DOC_READALL_EVENT, 0);
		String filePath = fileUtils.getGeneratedFilePath(filePathGenerator);
		File file = new File(filePath);
		try {
			byte[] bytefile;
			if (PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION.equalsIgnoreCase(appDeployLocation)) {
				bytefile = IOUtils.toByteArray(awss3LogicImpl.readS3ObjectAsStream(filePath));
			} else {
				bytefile = Files.readAllBytes(file.toPath());
			}
			applicationAttachmentDTO.setAttachmentBytes(bytefile);
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}

		applicationAttachmentDTO.setFileName(attachment.getFileName());
		applicationAttachmentDTO.setFileType(attachment.getFileType());

		return applicationAttachmentDTO;
	}

	@Override
	public String getDefaultEmailCCByEmp(Long companyId, Long employeeId, String moduleName, boolean moduleEnabled) {
		String emailCC = "";
		ModuleMaster moduleMaster = null;
		if (moduleName.equalsIgnoreCase(PayAsiaConstants.COMPANY_MODULE_CLAIM)) {
			ClaimPreference claimPreferenceVO = claimPreferenceDAO.findByCompanyId(companyId);
			if (claimPreferenceVO == null) {
				return emailCC;
			}
			if (claimPreferenceVO != null && !claimPreferenceVO.isDefaultEmailCC()) {
				return emailCC;
			}
			moduleMaster = moduleMasterDAO.findByName(PayAsiaConstants.COMPANY_MODULE_CLAIM);
		}
		if (moduleName.equalsIgnoreCase(PayAsiaConstants.COMPANY_MODULE_LEAVE)) {
			LeavePreference leavePreferenceVO = leavePreferenceDAO.findByCompanyId(companyId);
			if (leavePreferenceVO == null) {
				return emailCC;
			}
			if (leavePreferenceVO != null && !leavePreferenceVO.isDefaultEmailCC()) {
				return emailCC;
			}
			moduleMaster = moduleMasterDAO.findByName(PayAsiaConstants.PAYASIA_LEAVE_MODULE_NAME);
		}
		if (moduleMaster != null) {
			EmployeeDefaultEmailCC employeeDefaultEmailCCVO = employeeDefaultEmailCCDAO.findByCondition(companyId,
					employeeId, moduleMaster.getModuleId());
			if (employeeDefaultEmailCCVO != null) {
				emailCC = employeeDefaultEmailCCVO.getEmailCC();
			}
		}
		return emailCC;
	}

	@Override
	public List<EmployeeFilterListForm> getDefaultEmailCCListByEmployee(Long companyId, Long employeeId,
			String moduleName, boolean moduleEnabled) {
		List<EmployeeFilterListForm> employeeFilterList = new ArrayList<EmployeeFilterListForm>();

		ModuleMaster moduleMaster = null;
		if (moduleName.equalsIgnoreCase(PayAsiaConstants.COMPANY_MODULE_CLAIM)) {
			ClaimPreference claimPreferenceVO = claimPreferenceDAO.findByCompanyId(companyId);
			if (claimPreferenceVO == null) {
				return employeeFilterList;
			}
			if (claimPreferenceVO != null && !claimPreferenceVO.isDefaultEmailCC()) {
				return employeeFilterList;
			}
			moduleMaster = moduleMasterDAO.findByName(PayAsiaConstants.COMPANY_MODULE_CLAIM);
		}
		if (moduleName.equalsIgnoreCase(PayAsiaConstants.COMPANY_MODULE_LEAVE)) {
			LeavePreference leavePreferenceVO = leavePreferenceDAO.findByCompanyId(companyId);
			if (leavePreferenceVO == null) {
				return employeeFilterList;
			}
			if (leavePreferenceVO != null && !leavePreferenceVO.isDefaultEmailCC()) {
				return employeeFilterList;
			}
			moduleMaster = moduleMasterDAO.findByName(PayAsiaConstants.PAYASIA_LEAVE_MODULE_NAME);
		}
		if (moduleMaster != null) {
			EmployeeDefaultEmailCC employeeDefaultEmailCCVO = employeeDefaultEmailCCDAO.findByCondition(companyId,
					employeeId, moduleMaster.getModuleId());
			if (employeeDefaultEmailCCVO != null) {
				String[] emailCCArray = employeeDefaultEmailCCVO.getEmailCC().split(";");
				for (int count = 0; count < emailCCArray.length; count++) {
					EmployeeFilterListForm employeeFilterListForm = new EmployeeFilterListForm();
					employeeFilterListForm.setValue(emailCCArray[count]);
					employeeFilterList.add(employeeFilterListForm);
				}
			}

		}
		return employeeFilterList;
	}

	@Override
	public void saveDefaultEmailCCByEmployee(Long companyId, Long employeeId, String ccEmailIds, String moduleName,
			boolean moduleEnabled) {
		Company company = companyDAO.findById(companyId);
		boolean isDefaultEmailCC = true;
		ModuleMaster moduleMaster = null;
		if (moduleName.equalsIgnoreCase(PayAsiaConstants.COMPANY_MODULE_CLAIM)) {
			ClaimPreference claimPreferenceVO = claimPreferenceDAO.findByCompanyId(companyId);
			if (claimPreferenceVO == null) {
				isDefaultEmailCC = false;
			}
			if (claimPreferenceVO != null && !claimPreferenceVO.isDefaultEmailCC()) {
				isDefaultEmailCC = false;
			}
			moduleMaster = moduleMasterDAO.findByName(PayAsiaConstants.COMPANY_MODULE_CLAIM);
		}
		if (moduleName.equalsIgnoreCase(PayAsiaConstants.COMPANY_MODULE_LEAVE)) {
			LeavePreference leavePreferenceVO = leavePreferenceDAO.findByCompanyId(companyId);
			if (leavePreferenceVO == null) {
				isDefaultEmailCC = false;
			}
			if (leavePreferenceVO != null && !leavePreferenceVO.isDefaultEmailCC()) {
				isDefaultEmailCC = false;
			}
			moduleMaster = moduleMasterDAO.findByName(PayAsiaConstants.PAYASIA_LEAVE_MODULE_NAME);
		}
		if (moduleMaster != null && isDefaultEmailCC) {
			EmployeeDefaultEmailCC employeeDefaultEmailCCVO = employeeDefaultEmailCCDAO.findByCondition(companyId,
					employeeId, moduleMaster.getModuleId());
			String emailCC = "";
			String[] emailCCArray = ccEmailIds.split(";");
			for (int count = 0; count < emailCCArray.length; count++) {
				if (StringUtils.isNotBlank(emailCCArray[count])) {
					emailCC += emailCCArray[count] + ";";
				}
			}

			if (employeeDefaultEmailCCVO == null) {
				if (StringUtils.isNotBlank(emailCC)) {
					employeeDefaultEmailCCVO = new EmployeeDefaultEmailCC();
					employeeDefaultEmailCCVO.setCompany(company);
					employeeDefaultEmailCCVO.setModuleMaster(moduleMaster);
					Employee employee = employeeDAO.findById(employeeId);
					employeeDefaultEmailCCVO.setEmployee(employee);
					employeeDefaultEmailCCVO.setEmailCC(emailCC);
					employeeDefaultEmailCCDAO.save(employeeDefaultEmailCCVO);
				}
			} else {
				if (StringUtils.isNotBlank(emailCC)) {
					employeeDefaultEmailCCVO.setEmailCC(emailCC);
					employeeDefaultEmailCCDAO.update(employeeDefaultEmailCCVO);
				} else {
					employeeDefaultEmailCCDAO.delete(employeeDefaultEmailCCVO);
				}
			}

		}
	}

	@Override
	public AddLeaveForm getLeaveBalance(Long employeeLeaveSchemeTypeId, Long empId, Long companyId) {
		AddLeaveForm addLeaveForm = new AddLeaveForm();
		if (employeeLeaveSchemeTypeDAO.findByleaveSchemeTypeIdAndCompanyIdAndEmpId(employeeLeaveSchemeTypeId, companyId,
				empId) == null) {
			addLeaveForm.setStatus("Failure");
			return addLeaveForm;
		}
		if (employeeLeaveSchemeTypeId != null) {
			LeaveDTO leaveDTO = employeeLeaveSchemeTypeDAO.getLeaveBalance(employeeLeaveSchemeTypeId);
			addLeaveForm.setLeaveBalance(leaveDTO.getLeaveBalance());
		}

		return addLeaveForm;
	}

	@Override
	public Long getEmployeeIdByCode(String employeeNumber, Long companyID) {
		return employeeDAO.findByNumber(employeeNumber, companyID).getEmployeeId();

	}

	@Override
	public LeaveApplication findByLeaveApplicationIdAndEmpId(Long leaveApplicationId, Long loginEmployeeID,
			Long companyID) {
		LeaveApplication leaveApplication = leaveApplicationDAO.findByLeaveApplicationIdAndEmpId(leaveApplicationId,
				loginEmployeeID, companyID);
		return leaveApplication;
	}

	@Override
	public EmployeeShortListDTO getEmployeeNameForManagerDup(Long loggedInEmployeeId, String employeeNumber,
			Long companyId) {
		EmployeeShortListDTO employeeShortListDTO = generalLogic.getShortListEmployeeIds(loggedInEmployeeId, companyId);
		if (employeeShortListDTO == null) {
			return null;
		}
		employeeShortListDTO.setEmployeeShortList(false);
		Tuple employeeVO = employeeLeaveReviewerDAO.getEmployeeIdTupleForManager(employeeNumber, companyId,
				loggedInEmployeeId, employeeShortListDTO);
		if (employeeVO != null) {

			employeeShortListDTO.setSearchEmployeeId(employeeVO.get(getAlias(Employee_.employeeId), Long.class));
			employeeShortListDTO
					.setSearchEmployeeNumber(employeeVO.get(getAlias(Employee_.employeeNumber), String.class));
			return employeeShortListDTO;
		} else {
			return null;
		}
	}
	
	/*
	 	FUNCTION FOR TEAM LEAVE MEMBERS INFO. API
	 */
	
	@Override
	public List<TeamLeaveDTO> getTeamMemberInfo(Long companyId, Long employeeId) {
		List<TeamLeaveDTO> teamLeaveDTOList = new ArrayList<>();
		
		EmployeeShortListDTO employeeShortListDTO = generalLogic.getShortListEmployeeIds(employeeId, companyId);
		employeeShortListDTO.setEmployeeShortList(false);
		
		List<Tuple> employeeTupleList = employeeLeaveReviewerDAO.getEmployeeIdsTupleForLeaveReviewer(null,
				null, employeeId, employeeShortListDTO);
		
		List<Long> leaveReviewerList = employeeLeaveReviewerDAO.getLeaveReviewerList(companyId);

		AddLeaveConditionDTO conditionDTO = new AddLeaveConditionDTO();
		LeaveStatusMaster leaveStatusMaster = leaveStatusMasterDAO.findByCondition(PayAsiaConstants.LEAVE_STATUS_SUBMITTED);
		List<String> leaveStatusNames = new ArrayList<>();
		leaveStatusNames.add(PayAsiaConstants.LEAVE_STATUS_SUBMITTED);
		leaveStatusNames.add(PayAsiaConstants.LEAVE_STATUS_APPROVED);
		conditionDTO.setLeaveStatusNames(leaveStatusNames);
		conditionDTO.setLeaveStatusId(leaveStatusMaster.getLeaveStatusID());//2l
		
		for (Tuple employeeLeaveRev : employeeTupleList) {
			TeamLeaveDTO teamLeaveObj = new TeamLeaveDTO();
			
			Long empId = employeeLeaveRev.get(getAlias(Employee_.employeeId), Long.class);
			conditionDTO.setEmployeeId(empId);
			
			String empName = "";
			empName += employeeLeaveRev.get(getAlias(Employee_.firstName), String.class) + " ";
			if (StringUtils.isNotBlank(employeeLeaveRev.get(getAlias(Employee_.lastName), String.class))) {
				empName += employeeLeaveRev.get(getAlias(Employee_.lastName), String.class);
			}
			teamLeaveObj.setEmployeeName(empName);

			byte[] empImg = null;
			try {
				empImg = employeeDetailLogic.getEmployeeImage(empId, null, employeeImageWidth, employeeImageHeight);
			} catch (IOException e) {
			}
			if (empImg!= null) {
				teamLeaveObj.setEmployeeImage(empImg);
			} 
			
			teamLeaveObj.setEmployeeNumber(employeeLeaveRev.get(getAlias(Employee_.employeeNumber), String.class));
			
			boolean onLeaveStatus = leaveApplicationDAO.findIsOnLeave(conditionDTO);
			teamLeaveObj.setOnLeave(onLeaveStatus);

			if (leaveReviewerList != null && leaveReviewerList.contains(empId)) {
				teamLeaveObj.setLeaveReviewer(true);
				
				List<LeaveApplicationReviewer> leavePendingLeavesList = leaveApplicationReviewerDAO.findByCondition(empId, null, null);
				if(leavePendingLeavesList!=null) {
					teamLeaveObj.setPendingLeavesForApproval(leavePendingLeavesList.size());
				}
			}
			else {
				List<LeaveApplication> submittedLeaves = leaveApplicationDAO.findByConditionSubmitted(conditionDTO, null, null);
				if (submittedLeaves != null) {
					teamLeaveObj.setSubmittedLeaves(submittedLeaves.size());
				}
			}
			
			if(!(empId.equals(Long.valueOf(UserContext.getUserId())))) {
				teamLeaveDTOList.add(teamLeaveObj);
			}
		}
		return teamLeaveDTOList;
	}
}
