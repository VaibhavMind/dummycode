/*
 * author by vivek jain
 */
package com.payasia.logic.impl;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.annotation.Resource;
import javax.persistence.Tuple;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.comparators.ComparableComparator;
import org.apache.commons.collections.comparators.ReverseComparator;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import com.mind.payasia.xml.bean.Column;
import com.mind.payasia.xml.bean.EmployeeFilter;
import com.mind.payasia.xml.bean.EmployeeFilterTemplate;
import com.mind.payasia.xml.bean.Field;
import com.mind.payasia.xml.bean.Tab;
import com.payasia.common.dto.ColumnPropertyDTO;
import com.payasia.common.dto.EmployeeShortListDTO;
import com.payasia.common.dto.ManageRolesConditionDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.DynamicFormSectionForm;
import com.payasia.common.form.DynamicFormSectionFormResponse;
import com.payasia.common.form.EmployeeFilterListForm;
import com.payasia.common.form.ManageUserAddCompanyForm;
import com.payasia.common.form.ManageUserAddCompanyResponseForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.PrevilegeForm;
import com.payasia.common.form.PrivilageResponseForm;
import com.payasia.common.form.SortCondition;
import com.payasia.common.form.UserResponseForm;
import com.payasia.common.form.UserRoleForm;
import com.payasia.common.form.UserRoleListForm;
import com.payasia.common.form.UsersForm;
import com.payasia.common.util.EmployeeFilterXMLUtil;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.ShortlistOperatorEnum;
import com.payasia.common.util.XMLUtil;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.CompanyEmployeeShortListDAO;
import com.payasia.dao.DataDictionaryDAO;
import com.payasia.dao.DynamicFormDAO;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.EmployeeRoleMappingDAO;
import com.payasia.dao.EmployeeRoleSectionMappingDAO;
import com.payasia.dao.EntityMasterDAO;
import com.payasia.dao.GeneralDAO;
import com.payasia.dao.PrivilegeMasterDAO;
import com.payasia.dao.RoleMasterDAO;
import com.payasia.dao.RoleSectionMappingDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.CompanyEmployeeShortList;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.DataDictionary;
import com.payasia.dao.bean.DynamicForm;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeRoleMapping;
import com.payasia.dao.bean.EmployeeRoleMappingPK;
import com.payasia.dao.bean.EmployeeRoleSectionMapping;
import com.payasia.dao.bean.EntityMaster;
import com.payasia.dao.bean.PrivilegeMaster;
import com.payasia.dao.bean.RoleMaster;
import com.payasia.dao.bean.RoleSectionMapping;
import com.payasia.logic.BaseLogic;
import com.payasia.logic.GeneralFilterLogic;
import com.payasia.logic.GeneralLogic;
import com.payasia.logic.ManageUserLogic;

/**
 * The Class ManageUserLogicImpl.
 */
@Component
public class ManageUserLogicImpl extends BaseLogic implements ManageUserLogic {

	private static final Logger LOGGER = Logger
			.getLogger(ManageUserLogicImpl.class);
	@Resource
	RoleMasterDAO roleMasterDAO;

	@Resource
	PrivilegeMasterDAO privilegeMasterDAO;

	@Resource
	EmployeeDAO employeeDAO;

	@Resource
	GeneralLogic generalLogic;
	@Resource
	EmployeeRoleMappingDAO employeeRoleMappingDAO;

	@Resource
	CompanyDAO companyDAO;

	@Resource
	EntityMasterDAO entityMasterDAO;

	@Resource
	GeneralDAO generalDAO;

	@Resource
	DataDictionaryDAO dataDictionaryDAO;

	@Resource
	CompanyEmployeeShortListDAO companyEmployeeShortListDAO;

	@Resource
	DynamicFormDAO dynamicFormDAO;

	@Resource
	GeneralFilterLogic generalFilterLogic;

	@Resource
	RoleSectionMappingDAO roleSectionMappingDAO;

	@Resource
	EmployeeRoleSectionMappingDAO employeeRoleSectionMappingDAO;

	/**
	 * view All Roles.
	 * 
	 * @param companyId
	 *            the company id
	 * @param sortDTO
	 *            the sort dto
	 * @return UserRoleForm contains All Existing roles
	 */
	@Override
	public UserRoleForm viewAllRole(Long companyId, SortCondition sortDTO) {
		boolean isLoggedInEmpAdmin = false;
		List<UserRoleListForm> userRoleListForm = new ArrayList<UserRoleListForm>();

		ArrayList<String> roleList = new ArrayList<>();
		SecurityContext securityContext = SecurityContextHolder.getContext();
		Authentication authentication = securityContext.getAuthentication();
		for (GrantedAuthority grantedAuthority : authentication
				.getAuthorities()) {
			if (grantedAuthority.getAuthority().equals("ROLE_SUPER ADMIN")) {
				roleList.add("ROLE_ADMIN");
			}
		}
		if (roleList.contains("ROLE_ADMIN")) {
			isLoggedInEmpAdmin = true;
		}

		List<RoleMaster> roleMasterList = roleMasterDAO.findAll(sortDTO,
				companyId);
		for (RoleMaster roleMaster : roleMasterList) {
			if (!isLoggedInEmpAdmin) {
				if (!roleMaster.getRoleName().equalsIgnoreCase("SUPER ADMIN")) {
					UserRoleListForm UserRoleList = new UserRoleListForm();
					/*ID ENCRYPT*/
					UserRoleList.setRoleId(FormatPreserveCryptoUtil.encrypt(roleMaster.getRoleId()));
					UserRoleList.setRoleName(roleMaster.getRoleName());
					UserRoleList.setRoleDesc(roleMaster.getRoleDesc());
					UserRoleList.setDeletable(roleMaster.getDeletable());
					userRoleListForm.add(UserRoleList);
				}
			} else {
				UserRoleListForm UserRoleList = new UserRoleListForm();
				/*ID ENCRYPT*/
				UserRoleList.setRoleId(FormatPreserveCryptoUtil.encrypt(roleMaster.getRoleId()));
				UserRoleList.setRoleName(roleMaster.getRoleName());
				UserRoleList.setRoleDesc(roleMaster.getRoleDesc());
				UserRoleList.setDeletable(roleMaster.getDeletable());
				userRoleListForm.add(UserRoleList);
			}

		}

		UserRoleForm response = new UserRoleForm();

		response.setUserRoleListForm(userRoleListForm);

		return response;

	}

	/**
	 * view All Privileges.
	 * 
	 * @param roleId
	 *            the role id
	 * @param sortDTO
	 *            the sort dto
	 * @return PrivilageResponseForm contains All Existing Privileges
	 */
	@Override
	public PrivilageResponseForm viewPrivilage(Long roleId,
			String searchCondition, String searchText, SortCondition sortDTO) {

		ManageRolesConditionDTO conditionDTO = new ManageRolesConditionDTO();
		try {
			if (StringUtils.isNotBlank(searchText)) {
				searchText = URLDecoder.decode(searchText, "UTF-8");
			}
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}

		if (PayAsiaConstants.MANAGE_ROLES_PRIVILEGE.equals(searchCondition)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setPrivilege(searchText.trim());
			}
		} else if (PayAsiaConstants.MANAGE_ROLES_MODULE.equals(searchCondition)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setModule(searchText.trim());
			}
		} else if (PayAsiaConstants.MANAGE_ROLES_ROLE.equals(searchCondition)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setRole(searchText.trim());
			}
		}

		List<PrevilegeForm> previlageFormListAll = new ArrayList<PrevilegeForm>();
		List<PrevilegeForm> previlageFormListByRole = new ArrayList<PrevilegeForm>();
		List<PrivilegeMaster> privilegeByRole = privilegeMasterDAO.findByRole(
				roleId, conditionDTO);
		for (PrivilegeMaster privilegeMasterByRole : privilegeByRole) {
			PrevilegeForm previlageFormByRole = new PrevilegeForm();
			previlageFormByRole.setPrivilegeId(privilegeMasterByRole
					.getPrivilegeId());
			previlageFormByRole.setPrivilegeName(privilegeMasterByRole
					.getPrivilegeName());
			previlageFormByRole.setPrivilegeDesc(privilegeMasterByRole
					.getPrivilegeDesc());
			previlageFormByRole.setModuleName(privilegeMasterByRole
					.getModuleMaster().getModuleName());
			previlageFormByRole.setPrivilegeRole(privilegeMasterByRole
					.getPrivilegeRole());
			previlageFormListByRole.add(previlageFormByRole);
		}

		List<PrivilegeMaster> privilegeAll = privilegeMasterDAO.findAll(
				sortDTO, conditionDTO);
		for (PrivilegeMaster privilegeMasterAll : privilegeAll) {
			PrevilegeForm PrevilageFormAll = new PrevilegeForm();
			PrevilageFormAll
					.setPrivilegeId(privilegeMasterAll.getPrivilegeId());
			PrevilageFormAll.setPrivilegeName(privilegeMasterAll
					.getPrivilegeDesc());
			PrevilageFormAll.setPrivilegeDesc(privilegeMasterAll
					.getPrivilegeDesc());
			PrevilageFormAll.setModuleName(privilegeMasterAll.getModuleMaster()
					.getModuleName());
			PrevilageFormAll.setPrivilegeRole(privilegeMasterAll
					.getPrivilegeRole());
			for (PrivilegeMaster privilegeMasterByRole : privilegeByRole) {
				if ((privilegeMasterAll.getPrivilegeId()) == (privilegeMasterByRole
						.getPrivilegeId())) {
					PrevilageFormAll.setRoleAssigned(true);
				}
			}

			previlageFormListAll.add(PrevilageFormAll);
		}
		PrivilageResponseForm response = new PrivilageResponseForm();
		response.setPrevilageListForm(previlageFormListAll);

		return response;
	}

	/**
	 * view All Privilege Users.
	 * 
	 * @param companyId
	 *            the company id
	 * @param roleId
	 *            the role id
	 * @param sortDTO
	 *            the sort dto
	 * @return UserResponseForm contains All Users with assigned role and
	 *         privileges or not.
	 */
	@Override
	public UserResponseForm viewPrivilageUser(Long companyId, Long employeeId,
			Long roleId, SortCondition sortDTO, PageRequest pageDTO,
			String searchCondition, String searchText) {
		ManageRolesConditionDTO conditionDTO = new ManageRolesConditionDTO();
		try {
			if (StringUtils.isNotBlank(searchText)) {
				searchText = URLDecoder.decode(searchText, "UTF-8");
			}
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		if (PayAsiaConstants.MANAGE_ROLES_EMPLOYEENUMBER
				.equals(searchCondition)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setEmployeeNumber(searchText.trim());
			}
		} else if (PayAsiaConstants.MANAGE_ROLES_FIRSTNAME
				.equals(searchCondition)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setFirstName(searchText.trim());
			}
		} else if (PayAsiaConstants.MANAGE_ROLES_LASTNAME
				.equals(searchCondition)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setLastName(searchText.trim());
			}
		} else if (PayAsiaConstants.MANAGE_ROLES_USERNAME
				.equals(searchCondition)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setEmployeeNumber(searchText.trim());
			}
		}

		List<UsersForm> usersFormListAll = new ArrayList<UsersForm>();
		Company company = companyDAO.findById(companyId);
		EmployeeShortListDTO employeeShortListDTO = generalLogic
				.getShortListEmployeeIds(employeeId, companyId);

		ArrayList<String> roleList = new ArrayList<>();
		SecurityContext securityContext = SecurityContextHolder.getContext();
		Authentication authentication = securityContext.getAuthentication();
		for (GrantedAuthority grantedAuthority : authentication
				.getAuthorities()) {
			if (grantedAuthority.getAuthority().equals("ROLE_SUPER ADMIN")) {
				roleList.add("ROLE_ADMIN");
			}
		}

		if (company.getCompanyName().toUpperCase()
				.equals(PayAsiaConstants.PAYASIA_COMPANY_NAME)) {
			List<Employee> allEmpList = null;
			allEmpList = employeeDAO.findByShortlist(companyId, null,
					employeeShortListDTO, pageDTO, sortDTO, conditionDTO);
			for (Employee employee : allEmpList) {
				StringBuilder companyNameForPayAsiaBuilder = new StringBuilder(
						"");
				StringBuilder companyIDForPayAsiaBuilder = new StringBuilder("");
				UsersForm usersFormAll = new UsersForm();
				usersFormAll.setEmployeeId(FormatPreserveCryptoUtil.encrypt(employee.getEmployeeId()));
				usersFormAll.setFirstName(employee.getFirstName());
				if (StringUtils.isNotBlank(employee.getLastName())) {
					usersFormAll.setLastName(employee.getLastName());
				}

				usersFormAll.setLoginName(employee.getEmployeeLoginDetail()
						.getLoginName());
				usersFormAll.setEmployeeNumber(employee.getEmployeeNumber());

				Set<EmployeeRoleMapping> employeeRoleMappings = employee
						.getEmployeeRoleMappings();
				List<EmployeeRoleMapping> employeeRoleMappingList = new ArrayList<>();
				for (EmployeeRoleMapping employeeRoleMapping : employeeRoleMappings) {
					if (employeeRoleMapping.getRoleMaster().getRoleId() == roleId) {
						employeeRoleMappingList.add(employeeRoleMapping);
					}
				}
				int countList = 1;
				for (EmployeeRoleMapping employeeRoleMapping : employeeRoleMappingList) {
					int length = employeeRoleMappingList.size();
					usersFormAll.setRoleId(employeeRoleMapping.getId()
							.getRoleId());
					usersFormAll.setRoleName(employeeRoleMapping
							.getRoleMaster().getRoleName());
					if (countList != length) {
						companyIDForPayAsiaBuilder.append(
								String.valueOf(employeeRoleMapping.getId()
										.getCompanyId())).append(',');
						companyNameForPayAsiaBuilder.append(
								employeeRoleMapping.getCompany()
										.getCompanyName()).append(',');
						countList++;
					} else {
						companyIDForPayAsiaBuilder.append(String
								.valueOf(employeeRoleMapping.getId()
										.getCompanyId()));
						companyNameForPayAsiaBuilder.append(employeeRoleMapping
								.getCompany().getCompanyName());
						countList++;
					}

				}
				if ((StringUtils.isNotBlank(companyIDForPayAsiaBuilder
						.toString()))
						&& (StringUtils.isNotBlank(companyNameForPayAsiaBuilder
								.toString()))) {
					usersFormAll.setCompanyId(companyIDForPayAsiaBuilder
							.toString());
					usersFormAll.setCompanyName(companyNameForPayAsiaBuilder
							.toString());
				}

				String assignPayasiaCompanyCount = new StringBuilder(
						"<span class='Text'><h2 style ='width: 36px;'>")
						.append(String.valueOf(employeeRoleMappingList.size()))
						.append("</h2></span>")
						.append("<span class='Textsmall' style='padding-top: 5px;'>&nbsp;Company</span>")
						.append("<span class='Textsmall' style='padding-bottom: 5px;padding-top: 5px;padding-left: 5px;'><a class='alink' style='text-decoration: none;' href='#' onClick = 'addCompany(")
						.append(FormatPreserveCryptoUtil.encrypt(employee.getEmployeeId()))
						.append(")'>[Assign]</a></span>").toString();

				usersFormAll.setAssignCompanyCount(assignPayasiaCompanyCount);

				if (employeeRoleMappingList.size() != 0) {
					usersFormAll.setRoleAssigned(true);
				}

				usersFormListAll.add(usersFormAll);

			}
		}

		else {

			List<Employee> allEmpList = null;

			allEmpList = employeeDAO.findByShortlist(companyId, null,
					employeeShortListDTO, pageDTO, sortDTO, conditionDTO);

			for (Employee employee : allEmpList) {
				String companyNameForOtherThanPayAsia = "";
				String companyIDForOtherThanPayAsia = "";

				UsersForm usersFormAll = new UsersForm();
				usersFormAll.setEmployeeId(FormatPreserveCryptoUtil.encrypt(employee.getEmployeeId()));
				usersFormAll.setFirstName(employee.getFirstName());

				if (StringUtils.isNotBlank(employee.getLastName())) {
					usersFormAll.setLastName(employee.getLastName());
				}
				usersFormAll.setLoginName(employee.getEmployeeLoginDetail()
						.getLoginName());
				usersFormAll.setEmployeeNumber(employee.getEmployeeNumber());

				Set<EmployeeRoleMapping> employeeRoleMappings = employee
						.getEmployeeRoleMappings();
				List<EmployeeRoleMapping> employeeRoleMappingList = new ArrayList<>();
				for (EmployeeRoleMapping employeeRoleMapping : employeeRoleMappings) {
					if (employeeRoleMapping.getRoleMaster().getRoleId() == roleId) {
						employeeRoleMappingList.add(employeeRoleMapping);
					}
				}

				int countList = 1;
				for (EmployeeRoleMapping employeeRoleMapping : employeeRoleMappingList) {
					int length = employeeRoleMappingList.size();
					usersFormAll.setRoleId(employeeRoleMapping.getId()
							.getRoleId());
					usersFormAll.setRoleName(employeeRoleMapping
							.getRoleMaster().getRoleName());
					if (countList != length) {
						companyIDForOtherThanPayAsia += String
								.valueOf(employeeRoleMapping.getId()
										.getCompanyId()) + ',';
						companyNameForOtherThanPayAsia += employeeRoleMapping
								.getCompany().getCompanyName() + ',';
						countList++;
					} else {
						companyIDForOtherThanPayAsia += String
								.valueOf(employeeRoleMapping.getId()
										.getCompanyId());
						companyNameForOtherThanPayAsia += employeeRoleMapping
								.getCompany().getCompanyName();
						countList++;
					}

				}
				if ((StringUtils.isNotBlank(companyIDForOtherThanPayAsia))
						&& (StringUtils
								.isNotBlank(companyNameForOtherThanPayAsia))) {
					usersFormAll.setCompanyId(companyIDForOtherThanPayAsia);
					usersFormAll.setCompanyName(companyNameForOtherThanPayAsia);
				}

				String assignOtherCompanyCount = "<span class='Text'><h2 style ='width: 36px;'>"
						+ String.valueOf(employeeRoleMappingList.size())
						+ "</h2></span>";
				assignOtherCompanyCount += "<span class='Textsmall' style='padding-top: 5px;'>&nbsp;Company</span>";
				assignOtherCompanyCount += "<span class='Textsmall' style='padding-bottom: 5px;padding-top: 5px;padding-left: 5px;'><a class='alink' style='text-decoration: none;' href='#' onClick = 'addCompany("
						+ FormatPreserveCryptoUtil.encrypt(employee.getEmployeeId()) + ")'>[Assign]</a></span>";

				usersFormAll.setAssignCompanyCount(assignOtherCompanyCount);

				if (employeeRoleMappingList.size() != 0) {
					usersFormAll.setRoleAssigned(true);
				}

				usersFormListAll.add(usersFormAll);

			}
		}
		int recordSize;
		if (roleList.contains("ROLE_ADMIN")) {
			recordSize = employeeDAO.getCountForPrivilegeUser(companyId, null,
					employeeShortListDTO);
		} else {
			recordSize = employeeDAO.getCountForPrivilegeUser(companyId,
					employeeId, employeeShortListDTO);
		}

		UserResponseForm response = new UserResponseForm();
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
		response.setRecords(recordSize);

		response.setUsersListForm(usersFormListAll);

		return response;
	}

	/**
	 * save User RoleAndPrivilages.
	 * 
	 * @param companyId
	 *            the company id
	 * @param roleId
	 *            the role id
	 * @param privilegeId
	 *            the privilege id
	 * @param userIdAndCompanyName
	 *            the user id and company name
	 * @return response.
	 */
	@Override
	public String saveUserRoleAndPrivilage(Long companyId, String roleId,
			String[] privilegeId, String[] sectionIds,
			String userIdAndCompanyName, String notSelectedUserIds) {
		boolean isPayasiaUserRoleOtherThanAdmin = false;
		Long payasiaCompanyGroupId = PayAsiaConstants.PAYASIA_COMPANY_GROUP_ID;

		ArrayList<String> roleList = new ArrayList<>();
		SecurityContext securityContext = SecurityContextHolder.getContext();
		Authentication authentication = securityContext.getAuthentication();
		for (GrantedAuthority grantedAuthority : authentication
				.getAuthorities()) {
			if (grantedAuthority.getAuthority().equals("ROLE_SUPER ADMIN")) {
				roleList.add("ROLE_ADMIN");
			}
		}
		if (!roleList.contains("ROLE_ADMIN")) {
			isPayasiaUserRoleOtherThanAdmin = true;
		}

		Set<PrivilegeMaster> privilegeMasterSet = new HashSet<PrivilegeMaster>();
		RoleMaster roleMaster = roleMasterDAO.findByRoleId(Long.parseLong(roleId),companyId);

		for (int count = 0; count < privilegeId.length; count++) {
			PrivilegeMaster privilegeMaster = privilegeMasterDAO.findByID(Long
					.parseLong(privilegeId[count]));
			privilegeMasterSet.add(privilegeMaster);
		}
		roleMaster.setPrivilegeMasters(privilegeMasterSet);
		roleMasterDAO.update(roleMaster);

		roleSectionMappingDAO.deleteByCondition(Long.parseLong(roleId),companyId);
		for (int count = 0; count < sectionIds.length; count++) {
			RoleSectionMapping roleSectionMapping = new RoleSectionMapping();
			String formId = sectionIds[count].substring(0,
					sectionIds[count].length() - 1);
			roleSectionMapping.setFormId(Long.parseLong(formId));
			roleSectionMapping.setRoleMaster(roleMaster);
			roleSectionMappingDAO.save(roleSectionMapping);
		}

		List<String> empInsertList = new ArrayList<String>();
		List<String> empIdsList = new ArrayList<String>();
		List<String> empUnAssignCompanyList = new ArrayList<String>();
		List<String> empDbTempList = new ArrayList<String>();
		List<String> empUnSelectedList = new ArrayList<String>();
		List<String> empRoleInsertList = new ArrayList<String>();
		List<String> empHasNoCompanyList = new ArrayList<String>();
		List<EmployeeRoleMapping> empRoleMappingVOlist = employeeRoleMappingDAO
				.findByConditionRoleIdAndCompanyId(Long.parseLong(roleId),companyId);

		if (empRoleMappingVOlist != null) {
			for (EmployeeRoleMapping empVO : empRoleMappingVOlist) {
				empDbTempList.add(String.valueOf(empVO.getEmployee()
						.getEmployeeId())
						+ "-"
						+ String.valueOf(empVO.getCompany().getCompanyId()));
			}
		}

		if (StringUtils.isNotBlank(notSelectedUserIds)) {

			String[] userIdAndCompanyNameVal = notSelectedUserIds.split(";");
			int countId = 1;
			String userID = null;

			for (int count = 0; count < userIdAndCompanyNameVal.length; count++) {
				countId = 1;

				StringTokenizer st = new StringTokenizer(
						userIdAndCompanyNameVal[count], ",");
				while (st.hasMoreTokens()) {
					String compID = null;
					if (countId == 1) {
						userID = st.nextToken();
						countId++;
					} else {
						compID = st.nextToken();
						if (!"false".equals(compID)) {
							if (compID.equals("#")) {
								empHasNoCompanyList.add(userID);
							} else {
								empUnSelectedList.add(userID + "-" + compID);
							}

						}
						countId++;
					}
				}

			}

			if (!empUnSelectedList.isEmpty()) {
				for (String empForDelete : empUnSelectedList) {
					String[] empAndCompId = empForDelete.split("-");
					companyEmployeeShortListDAO.deleteByCondition(
							Long.parseLong(empAndCompId[0]),
							Long.parseLong(roleId),
							Long.parseLong(empAndCompId[1]));

					employeeRoleMappingDAO.deleteByCondition(
							Long.parseLong(empAndCompId[0]),
							Long.parseLong(roleId),
							Long.parseLong(empAndCompId[1]));
				}
			}

		}
		if (!empHasNoCompanyList.isEmpty()) {
			for (String empForDelete : empHasNoCompanyList) {
				companyEmployeeShortListDAO.deleteByEmployeeAndRole(
						Long.parseLong(empForDelete), Long.parseLong(roleId));

				employeeRoleMappingDAO.deleteByConditionRoleAndEmpId(
						Long.parseLong(empForDelete), Long.parseLong(roleId));
			}
		}

		if (StringUtils.isNotBlank(userIdAndCompanyName)) {

			String[] userIdAndCompanyNameVal = userIdAndCompanyName.split(";");
			int countId = 1;
			String userID = null;

			for (int count = 0; count < userIdAndCompanyNameVal.length; count++) {
				countId = 1;

				StringTokenizer st = new StringTokenizer(
						userIdAndCompanyNameVal[count], ",");
				while (st.hasMoreTokens()) {
					String compID = null;
					if (countId == 1) {
						userID = st.nextToken();
						empRoleInsertList.add(userID + "-" + companyId);
						empIdsList.add(userID);
						countId++;
					} else {
						compID = st.nextToken();
						empInsertList.add(userID + "-" + compID);
						countId++;
					}
				}
				if (userID != null) {

				}

			}
			for (String empId : empIdsList) {
				List<EmployeeRoleMapping> empBasedOnRolelist = employeeRoleMappingDAO
						.findByConditionRoleAndEmpID(Long.parseLong(roleId),
								Long.parseLong(empId),
								isPayasiaUserRoleOtherThanAdmin,
								payasiaCompanyGroupId);
				if (empRoleMappingVOlist != null) {
					for (EmployeeRoleMapping empVO : empBasedOnRolelist) {
						empUnAssignCompanyList.add(String.valueOf(empVO
								.getEmployee().getEmployeeId())
								+ "-"
								+ String.valueOf(empVO.getCompany()
										.getCompanyId()));
					}
				}
			}

			empUnAssignCompanyList.removeAll(empInsertList);

			if (!empUnAssignCompanyList.isEmpty()) {
				for (String empForDelete : empUnAssignCompanyList) {
					String[] empAndCompId = empForDelete.split("-");
					companyEmployeeShortListDAO.deleteByCondition(
							Long.parseLong(empAndCompId[0]),
							Long.parseLong(roleId),
							Long.parseLong(empAndCompId[1]));

					employeeRoleMappingDAO.deleteByCondition(
							Long.parseLong(empAndCompId[0]),
							Long.parseLong(roleId),
							Long.parseLong(empAndCompId[1]));
				}
			}

		}

		Set<EmployeeRoleMapping> employeeRoleMappingSet = new HashSet<EmployeeRoleMapping>();
		EmployeeRoleMapping employeeRoleMapping;
		EmployeeRoleMappingPK employeeRoleMappingPK;

		if (StringUtils.isNotBlank(userIdAndCompanyName)) {

			if (roleMaster
					.getRoleName()
					.toUpperCase()
					.equals(PayAsiaConstants.EMPLOYEE_DEFAULT_ROLE
							.toUpperCase())) {
				empRoleInsertList.removeAll(empDbTempList);
				if (!empRoleInsertList.isEmpty()) {
					for (String insertEmpId : empRoleInsertList) {
						String[] empAndCompId = insertEmpId.split("-");

						employeeRoleMapping = new EmployeeRoleMapping();
						employeeRoleMappingPK = new EmployeeRoleMappingPK();
						employeeRoleMappingPK.setCompanyId(companyId);
						employeeRoleMappingPK.setEmployeeId(Long
								.parseLong(empAndCompId[0]));
						employeeRoleMappingPK.setRoleId(Long.parseLong(roleId));
						employeeRoleMapping.setId(employeeRoleMappingPK);
						employeeRoleMappingSet.add(employeeRoleMapping);
						employeeRoleMappingDAO.save(employeeRoleMapping);
					}
				}

			} else {
				empInsertList.removeAll(empDbTempList);
				if (!empInsertList.isEmpty()) {
					String[] userIdAndCompanyNameVal = userIdAndCompanyName
							.split(";");
					int countId = 1;
					String userID;
					for (String insertEmpId : empInsertList) {
						String[] empAndCompId = insertEmpId.split("-");

						for (int count = 0; count < userIdAndCompanyNameVal.length; count++) {
							countId = 1;
							userID = null;
							StringTokenizer st = new StringTokenizer(
									userIdAndCompanyNameVal[count], ",");
							while (st.hasMoreTokens()) {
								String compID = null;
								if (countId == 1) {
									userID = st.nextToken();
									countId++;
								} else {
									compID = st.nextToken();
									countId++;
								}
								if ((compID != null) && (userID != null)
										&& (userID.equals(empAndCompId[0]))
										&& (compID.equals(empAndCompId[1]))
										&& (!compID.equals("#"))) {
									employeeRoleMapping = new EmployeeRoleMapping();
									employeeRoleMappingPK = new EmployeeRoleMappingPK();
									employeeRoleMappingPK.setCompanyId(Long
											.parseLong(empAndCompId[1]));
									employeeRoleMappingPK.setEmployeeId(Long
											.parseLong(empAndCompId[0]));
									employeeRoleMappingPK.setRoleId(Long
											.parseLong(roleId));
									employeeRoleMapping
											.setId(employeeRoleMappingPK);
									employeeRoleMappingSet
											.add(employeeRoleMapping);
									employeeRoleMappingDAO
											.save(employeeRoleMapping);
								}
							}
						}
					}
				}
			}

			return "configuration.saved.successfully";
		}
		return "configuration.saved.successfully";
	}

	/**
	 * save Role.
	 * 
	 * @param userRoleListForm
	 *            the user role list form
	 * @param companyId
	 *            the company id
	 * @return response.
	 */
	@Override
	public String saveRole(UserRoleListForm userRoleListForm, Long companyId) {
		boolean status = true;

		Company company = companyDAO.findById(companyId);

		RoleMaster roleMaster = new RoleMaster();
		roleMaster.setRoleName(userRoleListForm.getRoleName());
		roleMaster.setRoleDesc(userRoleListForm.getRoleDesc());
		roleMaster.setCompany(company);
		roleMaster.setDeletable(true);
		List<RoleMaster> roleMasterVOList = roleMasterDAO.findAll(null,
				companyId);
		if (roleMasterVOList != null) {
			for (RoleMaster RoleMasterVO : roleMasterVOList) {
				if (userRoleListForm.getRoleName().toUpperCase()
						.equals(RoleMasterVO.getRoleName().toUpperCase())) {
					status = false;
				}
			}
		}
		if (status) {
			roleMasterDAO.save(roleMaster);
			return "new.role.saved.successfully";
		} else {
			return "duplicate.role.Name.please.add.other.role.name";
		}

	}

	/**
	 * Delete Role.
	 * 
	 * @param companyId
	 *            the company id
	 * @param roleId
	 *            the role id
	 * @return response.
	 */
	@Override
	public String deleteRole(Long companyId, Long roleId) {
		RoleMaster roleMaster = roleMasterDAO.findByRoleId(roleId,companyId);
		List<Employee> usersRoleMap = employeeDAO.findByRole(roleId, companyId);
		ManageRolesConditionDTO conditionDTO = new ManageRolesConditionDTO();
		List<PrivilegeMaster> privilegeRoleMap = privilegeMasterDAO.findByRole(
				roleId, conditionDTO);
		if ((usersRoleMap.size() == 0) && (privilegeRoleMap.size() == 0)) {
			roleMasterDAO.delete(roleMaster);
			return "deleted.successfully";
		} else {
			return "delete.the.privileges.and.users.assigned.to.role";
		}
	}

	/**
	 * Copy the Role from existing role with defined privileges.
	 * 
	 * @param userRoleListForm
	 *            the user role list form
	 * @param companyId
	 *            the company id
	 * @return response.
	 */
	@Override
	public String copyRole(UserRoleListForm userRoleListForm, Long companyId) {
		boolean status = true;

		Company company = companyDAO.findById(companyId);

		RoleMaster roleMaster = new RoleMaster();
		roleMaster.setRoleName(userRoleListForm.getRoleName());
		roleMaster.setRoleDesc(userRoleListForm.getRoleDesc());
		roleMaster.setCompany(company);
		roleMaster.setDeletable(true);

		List<RoleMaster> roleMasterVOList = roleMasterDAO.findAll(null,
				companyId);

		if (roleMasterVOList != null) {
			for (RoleMaster RoleMasterVO : roleMasterVOList) {
				if (userRoleListForm.getRoleName().toUpperCase()
						.equals(RoleMasterVO.getRoleName().toUpperCase())) {
					status = false;
				}
			}
		}
		if (status) {
			RoleMaster persistObj = roleMasterDAO.saveRole(roleMaster);

			Set<PrivilegeMaster> privilegeMasterSet = new HashSet<PrivilegeMaster>();
			ManageRolesConditionDTO conditionDTO = new ManageRolesConditionDTO();
			List<PrivilegeMaster> privilegeRoleMapList = privilegeMasterDAO
					.findByRole(userRoleListForm.getCopyRoleId(), conditionDTO);
			for (PrivilegeMaster privilegeRoleMap : privilegeRoleMapList) {
				PrivilegeMaster privilegeMaster = privilegeMasterDAO
						.findByID(privilegeRoleMap.getPrivilegeId());
				privilegeMasterSet.add(privilegeMaster);
			}
			persistObj.setPrivilegeMasters(privilegeMasterSet);
			roleMasterDAO.update(persistObj);

			Set<EmployeeRoleMapping> employeeRoleMappingSet = new HashSet<EmployeeRoleMapping>();
			EmployeeRoleMapping employeeRoleMapping;
			EmployeeRoleMappingPK employeeRoleMappingPK;
			List<Employee> usersRoleMapList = employeeDAO.findByRole(
					userRoleListForm.getCopyRoleId(), companyId);
			for (Employee employeeRoleMap : usersRoleMapList) {
				employeeRoleMapping = new EmployeeRoleMapping();
				employeeRoleMappingPK = new EmployeeRoleMappingPK();
				employeeRoleMappingPK.setCompanyId(companyId);
				employeeRoleMappingPK.setEmployeeId(employeeRoleMap
						.getEmployeeId());
				employeeRoleMappingPK.setRoleId(persistObj.getRoleId());
				employeeRoleMapping.setId(employeeRoleMappingPK);
				employeeRoleMappingSet.add(employeeRoleMapping);
				employeeRoleMappingDAO.save(employeeRoleMapping);
			}
			return "role.are.successfully.copied";
		} else {
			return "duplicate.role.name.please.copy.as.other.role.name";
		}

	}

	/**
	 * getCompany IsPayAsia.
	 * 
	 * @param companyId
	 *            the company id
	 * @return Status.
	 */
	@Override
	public String getCompanyIsPayAsia(Long companyId) {

		Company company = companyDAO.findById(companyId);

		if (company.getCompanyName().toUpperCase()
				.equals(PayAsiaConstants.PAYASIA_COMPANY_NAME)) {
			return PayAsiaConstants.TRUE;
		} else {

			return PayAsiaConstants.FALSE;
		}

	}

	/**
	 * get CompanyList.
	 * 
	 * @param sortDTO
	 *            the sort dto
	 * @param isCompanyPayasia
	 *            the is company payasia
	 * @param companyId
	 *            the company id
	 * @param companyName
	 *            the company name
	 * @param employeeId
	 *            the employee id
	 * @return ManageUserAddCompanyResponseForm contains companies list.
	 */
	@Override
	public ManageUserAddCompanyResponseForm getCompanyList(
			SortCondition sortDTO, Boolean isCompanyPayasia, Long companyId,
			String companyName, Long sessionEmployeeId, Long roleId, Long userId) {
		List<ManageUserAddCompanyForm> manageUserAddCompanyFormList = new ArrayList<ManageUserAddCompanyForm>();

		if (companyName.toUpperCase().equals(
				PayAsiaConstants.PAYASIA_COMPANY_NAME.toUpperCase())) {

			ArrayList<String> roleList = new ArrayList<>();
			SecurityContext securityContext = SecurityContextHolder
					.getContext();
			Authentication authentication = securityContext.getAuthentication();
			for (GrantedAuthority grantedAuthority : authentication
					.getAuthorities()) {
				if (grantedAuthority.getAuthority().equals("ROLE_SUPER ADMIN")) {
					roleList.add("ROLE_ADMIN");
				}
			}
			List<Company> companyList;
			if (roleList.contains("ROLE_ADMIN")) {
				companyList = companyDAO.findAll(sortDTO);
			} else {
				Company companyVO = companyDAO.findById(companyId);
				companyList = companyDAO.getCompanyListOtherThanPaysaiaGroup(
						sortDTO, companyVO.getCompanyGroup().getGroupId());
			}

			Map<Long, Integer> companyEmpShortlistCountMap = getCompanyEmpShortListCount(
					roleId, userId);

			for (Company company : companyList) {
				ManageUserAddCompanyForm manageUserAddCompanyForm = new ManageUserAddCompanyForm();
				manageUserAddCompanyForm.setCompanyId(company.getCompanyId());
				manageUserAddCompanyForm.setCompanyName(company
						.getCompanyName());
				manageUserAddCompanyForm.setCompanyCode(company
						.getCompanyCode());
				manageUserAddCompanyForm.setGroupCode(company.getCompanyGroup()
						.getGroupCode());
				manageUserAddCompanyForm.setGroupId(company.getCompanyGroup()
						.getGroupId());
				manageUserAddCompanyForm.setGroupName(company.getCompanyGroup()
						.getGroupName());

				StringBuilder payasiaShortListYesNo = new StringBuilder();
				if (companyEmpShortlistCountMap.get(company.getCompanyId()) != null
						&& companyEmpShortlistCountMap.get(company
								.getCompanyId()) > 0) {

					payasiaShortListYesNo
							.append("<a class='alink' style='text-decoration: none;' href='#' onClick = 'manageUsersShortListFunc("
									+ FormatPreserveCryptoUtil.encrypt(userId)
									+ ','
									+ company.getCompanyId()
									+ ")'><span class='ctextgreen'>Yes</span></a>");

				} else {

					payasiaShortListYesNo
							.append("<a class='alink' style='text-decoration: none;' href='#' onClick = 'manageUsersShortListFunc("
									+ FormatPreserveCryptoUtil.encrypt(userId)
									+ ','
									+ company.getCompanyId()
									+ ")'><span class='ctextgray'>No</span></a>");

				}
				manageUserAddCompanyForm.setShortList(String
						.valueOf(payasiaShortListYesNo));

				manageUserAddCompanyFormList.add(manageUserAddCompanyForm);
			}
		}

		else {
			Company companyVO = companyDAO.findByCompanyName(companyName
					.toUpperCase());
			Long companyGroupId = companyVO.getCompanyGroup().getGroupId();

			Map<Long, Integer> companyEmpShortlistCountMap = getCompanyEmpShortListCount(
					roleId, userId);

			List<Company> companyList = companyDAO.findByGroupId(sortDTO,
					companyGroupId);
			for (Company company : companyList) {
				ManageUserAddCompanyForm manageUserAddCompanyForm = new ManageUserAddCompanyForm();
				manageUserAddCompanyForm.setCompanyId(company.getCompanyId());
				manageUserAddCompanyForm.setCompanyName(company
						.getCompanyName());
				manageUserAddCompanyForm.setCompanyCode(company
						.getCompanyCode());
				manageUserAddCompanyForm.setGroupCode(company.getCompanyGroup()
						.getGroupCode());
				manageUserAddCompanyForm.setGroupId(company.getCompanyGroup()
						.getGroupId());
				manageUserAddCompanyForm.setGroupName(company.getCompanyGroup()
						.getGroupName());

				StringBuilder payasiaShortListYesNo = new StringBuilder();
				if (companyEmpShortlistCountMap.get(company.getCompanyId()) != null
						&& companyEmpShortlistCountMap.get(company
								.getCompanyId()) > 0) {

					payasiaShortListYesNo
							.append("<a class='alink' style='text-decoration: none;' href='#' onClick = 'manageUsersShortListFunc("
									+ FormatPreserveCryptoUtil.encrypt(userId)
									+ ','
									+ company.getCompanyId()
									+ ")'><span class='ctextgreen'>Set</span></a>");

				} else {

					payasiaShortListYesNo
							.append("<a class='alink' style='text-decoration: none;' href='#' onClick = 'manageUsersShortListFunc("
									+ FormatPreserveCryptoUtil.encrypt(userId)
									+ ','
									+ company.getCompanyId()
									+ ")'><span class='ctextgray'>Not Set</span></a>");

				}
				manageUserAddCompanyForm.setShortList(String
						.valueOf(payasiaShortListYesNo));
				manageUserAddCompanyFormList.add(manageUserAddCompanyForm);
			}
		}

		ManageUserAddCompanyResponseForm response = new ManageUserAddCompanyResponseForm();
		response.setManageUserAddCompanyForm(manageUserAddCompanyFormList);

		return response;
	}

	private Map<Long, Integer> getCompanyEmpShortListCount(Long roleId,
			Long userId) {
		Map<Long, Integer> companyEmpShortlistCountMap = new HashMap<Long, Integer>();
		List<Tuple> companyEmpShortlistCountList = companyEmployeeShortListDAO
				.getCompanyEmpShortlistCount(userId, roleId);
		if (!companyEmpShortlistCountList.isEmpty()) {
			for (Tuple companyTuple : companyEmpShortlistCountList) {
				companyEmpShortlistCountMap.put((Long) companyTuple.get(
						getAlias(Company_.companyId), Long.class), companyTuple
						.get("shortlistCount", Integer.class));
			}
		}
		return companyEmpShortlistCountMap;
	}

	/**
	 * get AssignedCompanyList to user.
	 * 
	 * @param companyId
	 *            the company id
	 * @param roleId
	 *            the role id
	 * @param employeeId
	 *            the employee id
	 * @return ManageUserAddCompanyResponseForm contains companies list.
	 */
	@Override
	public List<ManageUserAddCompanyForm> getAssignedCompanyList(
			Long companyId, Long roleId, Long employeeId) {
		List<ManageUserAddCompanyForm> manageUserAddCompanyFormList = new ArrayList<ManageUserAddCompanyForm>();

		List<EmployeeRoleMapping> employeeRoleMappingVOList = employeeRoleMappingDAO
				.findByConditionRoleAndEmpID(roleId, employeeId);
		if (employeeRoleMappingVOList != null) {
			for (EmployeeRoleMapping employeeRoleMappingVO : employeeRoleMappingVOList) {
				ManageUserAddCompanyForm companyForm = new ManageUserAddCompanyForm();
				companyForm.setCompanyId(employeeRoleMappingVO.getCompany()
						.getCompanyId());
				companyForm.setCompanyName(employeeRoleMappingVO.getCompany()
						.getCompanyName());
				manageUserAddCompanyFormList.add(companyForm);
			}

		}
		return manageUserAddCompanyFormList;
	}

	/**
	 * get Employee FilterList.
	 * 
	 * @param companyId
	 *            the company id
	 * @return ManageUserAddCompanyResponseForm contains companies list.
	 */
	@Override
	public List<EmployeeFilterListForm> getEmployeeFilterList(Long companyId) {
		List<EmployeeFilterListForm> employeeFilterList = new ArrayList<EmployeeFilterListForm>();

		List<EntityMaster> entityMasterList = entityMasterDAO.findAll();
		Long entityId = getEntityId(PayAsiaConstants.EMPLOYEE_ENTITY_NAME,
				entityMasterList);

		List<DataDictionary> dataDictionaryList = dataDictionaryDAO
				.findByConditionEntity(entityId, PayAsiaConstants.STATIC_TYPE);
		if (dataDictionaryList != null) {
			for (DataDictionary dataDictionary : dataDictionaryList) {
				EmployeeFilterListForm employeeFilterListForm = new EmployeeFilterListForm();
				employeeFilterListForm.setDataDictionaryId(dataDictionary
						.getDataDictionaryId());
				employeeFilterListForm.setFieldName(dataDictionary.getLabel());
				try {
					ColumnPropertyDTO columnPropertyDTO = generalDAO
							.getColumnProperties(dataDictionary.getTableName(),
									dataDictionary.getColumnName());
					employeeFilterListForm.setDataType(columnPropertyDTO
							.getColumnType());
				} catch (Exception exception) {
					LOGGER.error(exception.getMessage(), exception);
				}
				employeeFilterList.add(employeeFilterListForm);
			}
		}
		Map<Long, DynamicForm> formIdMap = new HashMap<Long, DynamicForm>();
		List<DataDictionary> dataDictionaryCompanyList = dataDictionaryDAO
				.findByConditionEntityAndCompanyId(companyId, entityId,
						PayAsiaConstants.DYNAMIC_TYPE);
		if (dataDictionaryCompanyList != null) {
			for (DataDictionary dataDictionary : dataDictionaryCompanyList) {
				EmployeeFilterListForm employeeFilterListForm = new EmployeeFilterListForm();
				employeeFilterListForm.setDataDictionaryId(dataDictionary
						.getDataDictionaryId());
				employeeFilterListForm.setFieldName(dataDictionary.getLabel());

				DynamicForm dynamicForm = null;
				if (formIdMap.containsKey(dataDictionary.getFormID())) {
					dynamicForm = formIdMap.get(dataDictionary.getFormID());
				} else {
					dynamicForm = dynamicFormDAO.findMaxVersionByFormId(
							companyId, entityId, dataDictionary.getFormID());
					formIdMap.put(dynamicForm.getId().getFormId(), dynamicForm);
				}

				Unmarshaller unmarshaller = null;
				try {
					unmarshaller = XMLUtil.getDocumentUnmarshaller();
				} catch (JAXBException ex) {

					LOGGER.error(ex.getMessage(), ex);
				} catch (SAXException ex) {

					LOGGER.error(ex.getMessage(), ex);
				}

				final StringReader xmlReader = new StringReader(
						dynamicForm.getMetaData());
				Source xmlSource = null;
				try {
					xmlSource = XMLUtil.getSAXSource(xmlReader);
				} catch (SAXException | ParserConfigurationException e1) {
					LOGGER.error(e1.getMessage(), e1);
					throw new PayAsiaSystemException(e1.getMessage(),
							e1);
				}

				Tab tab = null;
				try {
					tab = (Tab) unmarshaller.unmarshal(xmlSource);
				} catch (JAXBException ex) {

					LOGGER.error(ex.getMessage(), ex);
				}
				List<Field> listOfFields = tab.getField();

				for (Field field : listOfFields) {
					if (!StringUtils.equalsIgnoreCase(field.getType(),
							PayAsiaConstants.TABLE_FIELD_TYPE)
							&& !StringUtils.equalsIgnoreCase(field.getType(),
									PayAsiaConstants.LABEL_FIELD_TYPE)
							&& !StringUtils
									.equalsIgnoreCase(
											field.getType(),
											PayAsiaConstants.FIELD_TYPE_CALCULATORY_FIELD)) {
						if (new String(Base64.decodeBase64(field
								.getDictionaryName().getBytes()))
								.equals(dataDictionary.getDataDictName())) {
							employeeFilterListForm.setDataType(field.getType());

						}

					} else if (field.getType().equals(
							PayAsiaConstants.TABLE_FIELD_TYPE)) {
						List<Column> listOfColumns = field.getColumn();
						for (Column column : listOfColumns) {
							if (new String(Base64.decodeBase64(column
									.getDictionaryName().getBytes()))
									.equals(dataDictionary.getDataDictName())) {
								employeeFilterListForm.setDataType(column
										.getType());
							}
						}
					}
				}

				employeeFilterList.add(employeeFilterListForm);

			}
		}
		return employeeFilterList;

	}

	/**
	 * Gets the entity id.
	 * 
	 * @param entityName
	 *            the entity name
	 * @param entityMasterList
	 *            the entity master list
	 * @return the entity id
	 */
	private Long getEntityId(String entityName,
			List<EntityMaster> entityMasterList) {
		for (EntityMaster entityMaster : entityMasterList) {
			if (entityName.equalsIgnoreCase(entityMaster.getEntityName())) {
				return entityMaster.getEntityId();
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.ManageUserLogic#getEditEmployeeFilterList(java.lang
	 * .Long, java.lang.Long, java.lang.Long)
	 */
	@Override
	public List<EmployeeFilterListForm> getEditEmployeeFilterList(
			Long employeeId, Long roleId, Long companyId) {
		List<EmployeeFilterListForm> employeeFilterList = new ArrayList<EmployeeFilterListForm>();
		List<CompanyEmployeeShortList> companyEmployeeShortVOList = companyEmployeeShortListDAO
				.findByCondition(employeeId, roleId, companyId);
		if (companyEmployeeShortVOList != null) {
			for (CompanyEmployeeShortList employeeShortList : companyEmployeeShortVOList) {
				EmployeeFilterListForm employeeFilterListForm = new EmployeeFilterListForm();
				employeeFilterListForm.setCloseBracket(employeeShortList
						.getCloseBracket());
				employeeFilterListForm.setDataDictionaryId(employeeShortList
						.getDataDictionary().getDataDictionaryId());
				employeeFilterListForm.setEqualityOperator(employeeShortList
						.getEqualityOperator());
				employeeFilterListForm.setFilterId(employeeShortList
						.getShortListId());
				employeeFilterListForm.setLogicalOperator(employeeShortList
						.getLogicalOperator());
				employeeFilterListForm.setOpenBracket(employeeShortList
						.getOpenBracket());
				employeeFilterListForm.setDataType(generalFilterLogic
						.getFieldDataType(companyId,
								employeeShortList.getDataDictionary()));
				employeeFilterListForm.setValue(employeeShortList.getValue());
				employeeFilterList.add(employeeFilterListForm);
			}
		}

		return employeeFilterList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.ManageUserLogic#deleteFilter(java.lang.Long)
	 */
	@Override
	public void deleteFilter(Long filterId) {
		CompanyEmployeeShortList companyEmployeeShortListVO = companyEmployeeShortListDAO
				.findById(filterId);
		companyEmployeeShortListDAO.delete(companyEmployeeShortListVO);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.ManageUserLogic#saveEmployeeFilterList(java.lang.String
	 * , java.lang.Long, java.lang.Long, java.lang.Long)
	 */
	@Override
	public String saveEmployeeFilterList(String metaData, Long employeeId,
			Long roleId, Long companyId) {
		Boolean saveStatus = false;
		Unmarshaller unmarshaller = null;
		try {
			unmarshaller = EmployeeFilterXMLUtil.getDocumentUnmarshaller();
		} catch (JAXBException ex) {

			LOGGER.error(ex.getMessage(), ex);
		} catch (SAXException ex) {

			LOGGER.error(ex.getMessage(), ex);
		}

		final StringReader xmlReader = new StringReader(metaData);
		Source xmlSource = null;
		try {
			xmlSource = XMLUtil.getSAXSource(xmlReader);
		} catch (SAXException | ParserConfigurationException e1) {
			LOGGER.error(e1.getMessage(), e1);
			throw new PayAsiaSystemException(e1.getMessage(),
					e1);
		}

		EmployeeFilterTemplate empFilterTemplate = null;
		try {
			empFilterTemplate = (EmployeeFilterTemplate) unmarshaller
					.unmarshal(xmlSource);
		} catch (JAXBException ex) {

			LOGGER.error(ex.getMessage(), ex);
		}

		companyEmployeeShortListDAO.deleteByCondition(employeeId, roleId,
				companyId);

		List<EmployeeFilter> listOfFields = empFilterTemplate
				.getEmployeeFilter();
		for (EmployeeFilter field : listOfFields) {
			CompanyEmployeeShortList companyEmployeeShortList = new CompanyEmployeeShortList();

			EmployeeRoleMappingPK empRoleMappingPk = new EmployeeRoleMappingPK();
			EmployeeRoleMapping empRoleMapping = new EmployeeRoleMapping();
			empRoleMappingPk.setCompanyId(companyId);
			empRoleMappingPk.setEmployeeId(employeeId);
			empRoleMappingPk.setRoleId(roleId);
			empRoleMapping.setId(empRoleMappingPk);
			companyEmployeeShortList.setEmployeeRoleMapping(empRoleMapping);

			if (field.getCloseBracket() != ""
					&& field.getCloseBracket() != null) {
				companyEmployeeShortList.setCloseBracket(field
						.getCloseBracket());
			}
			if (field.getOpenBracket() != "" && field.getOpenBracket() != null) {
				companyEmployeeShortList.setOpenBracket(field.getOpenBracket());

			}
			if (field.getDictionaryId() != 0) {
				DataDictionary dataDictionary = dataDictionaryDAO
						.findById(field.getDictionaryId());
				companyEmployeeShortList.setDataDictionary(dataDictionary);
			}

			if (field.getEqualityOperator() != ""
					&& field.getEqualityOperator() != null) {
				try {
					String equalityOperator = URLDecoder.decode(
							field.getEqualityOperator(), "UTF-8");

					// Check Valid ShortList Operator
					ShortlistOperatorEnum shortlistOperatorEnum = ShortlistOperatorEnum
							.getFromOperator(equalityOperator);
					if (shortlistOperatorEnum == null) {
						throw new PayAsiaSystemException(
								PayAsiaConstants.PAYASIA_SHORTLIST_OPERATOR_NOT_VALID);
					}

					companyEmployeeShortList
							.setEqualityOperator(equalityOperator);
				} catch (UnsupportedEncodingException ex) {
					LOGGER.error(ex.getMessage(), ex);
				}

			}
			if (field.getLogicalOperator() != ""
					&& field.getLogicalOperator() != null) {
				companyEmployeeShortList.setLogicalOperator(field
						.getLogicalOperator());
			}
			if (field.getValue() != "" && field.getValue() != null) {
				String fieldValue = "";
				try {
					fieldValue = URLDecoder.decode(field.getValue(), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					LOGGER.error(e.getMessage(), e);
				}
				companyEmployeeShortList.setValue(fieldValue);
				saveStatus = true;
			}
			companyEmployeeShortListDAO.save(companyEmployeeShortList);
		}
		if (saveStatus) {
			return "payasia.manage.roles.shortlist.saved.successfully";
		} else {
			return "0";
		}

	}

	@Override
	public String isPayAsiaUserAdmin(Long employeeId, Long companyId) {
		Company companyVO = companyDAO.findById(companyId);
		ArrayList<String> roleList = new ArrayList<>();

		if (companyVO.getCompanyGroup().getGroupName().toUpperCase()
				.equals(PayAsiaConstants.PAYASIA_GROUP_NAME)) {
			SecurityContext securityContext = SecurityContextHolder
					.getContext();
			Authentication authentication = securityContext.getAuthentication();
			for (GrantedAuthority grantedAuthority : authentication
					.getAuthorities()) {
				if ("ROLE_ADMIN".equals(grantedAuthority.getAuthority())
						|| grantedAuthority.getAuthority().equals(
								"ROLE_SUPER ADMIN")) {
					roleList.add("ROLE_ADMIN");
				}
			}
			if (roleList.contains("ROLE_ADMIN")) {
				return PayAsiaConstants.TRUE;
			} else {
				return PayAsiaConstants.FALSE;
			}

		} else {
			return PayAsiaConstants.TRUE;
		}

	}

	@Override
	public DynamicFormSectionFormResponse viewSectionName(Long roleId,
			Long companyId, SortCondition sortDTO, String searchCondition,
			String searchText) {
		List<DynamicFormSectionForm> dynamicFormSectionFormList = new ArrayList<>();

		ManageRolesConditionDTO conditionDTO = new ManageRolesConditionDTO();
		try {
			if (StringUtils.isNotBlank(searchText)) {
				searchText = URLDecoder.decode(searchText, "UTF-8");
			}
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		if (PayAsiaConstants.MANAGE_ROLES_SECTION.equals(searchCondition)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setSection(searchText.trim());
			}
		}

		EntityMaster entityMaster = entityMasterDAO
				.findByEntityName(PayAsiaConstants.EMPLOYEE_ENTITY_NAME);

		List<RoleSectionMapping> roleSectionMappingByRoleList = roleSectionMappingDAO
				.findByRoleId(roleId);

		List<Object[]> tupleList = dynamicFormDAO
				.getTabNameAndFormIdByMaxVersion(companyId,
						entityMaster.getEntityId(), conditionDTO);
		for (Object[] tuple : tupleList) {
			String tabName = (String) tuple[1];

			if (!tabName
					.equalsIgnoreCase(PayAsiaConstants.EMPLOYEE_BASIC_TAB_NAME)) {
				DynamicFormSectionForm dynamicFormSectionForm = new DynamicFormSectionForm();

				dynamicFormSectionForm.setFormIdStr(String.valueOf(tuple[0])
						+ "t");
				dynamicFormSectionForm.setFormId(Long.parseLong(String
						.valueOf(tuple[0])));
				dynamicFormSectionForm.setSectionName((String) tuple[1]);

				for (RoleSectionMapping roleSectionMappingByRole : roleSectionMappingByRoleList) {
					Long formId = Long.parseLong(String.valueOf(tuple[0]));
					if (formId.equals((roleSectionMappingByRole.getFormId()))) {
						dynamicFormSectionForm.setRoleAssigned(true);
					}
				}
				dynamicFormSectionFormList.add(dynamicFormSectionForm);
			}
		}

		if (StringUtils.isBlank(sortDTO.getColumnName())) {
			sortDTO.setColumnName("sectionName");

		}
		if (StringUtils.isNotBlank(sortDTO.getColumnName())) {
			BeanComparator beanComparator = null;

			if ("asc".equalsIgnoreCase(sortDTO.getOrderType())) {
				beanComparator = new BeanComparator(sortDTO.getColumnName());
			} else {
				beanComparator = new BeanComparator(sortDTO.getColumnName(),
						new ReverseComparator(new ComparableComparator()));
			}

			Collections.sort(dynamicFormSectionFormList, beanComparator);

		}

		DynamicFormSectionFormResponse response = new DynamicFormSectionFormResponse();
		response.setDynamicFormSectionFormList(dynamicFormSectionFormList);
		return response;
	}

	@Override
	public String saveRolePrivileges(Long companyId, String roleId,
			String[] privilegeIdsArr) {
		Set<PrivilegeMaster> privilegeMasterSet = new HashSet<PrivilegeMaster>();
		RoleMaster roleMaster = roleMasterDAO.findByRoleId(Long.parseLong(roleId),companyId);

		for (int count = 0; count < privilegeIdsArr.length; count++) {
			PrivilegeMaster privilegeMaster = privilegeMasterDAO.findByID(Long
					.parseLong(privilegeIdsArr[count]));
			privilegeMasterSet.add(privilegeMaster);
		}
		roleMaster.setPrivilegeMasters(privilegeMasterSet);
		roleMasterDAO.update(roleMaster);

		return "configuration.saved.successfully";

	}

	@Override
	public String saveRoleSections(Long companyId, String roleId,
			String[] sectionIdsArr, boolean overrideSection) {

		RoleMaster roleMaster = roleMasterDAO.findByRoleId(Long.parseLong(roleId),companyId);

		roleSectionMappingDAO.deleteByCondition(Long.parseLong(roleId),companyId);
		for (int count = 0; count < sectionIdsArr.length; count++) {
			RoleSectionMapping roleSectionMapping = new RoleSectionMapping();
			String formId = sectionIdsArr[count].substring(0,
					sectionIdsArr[count].length() - 1);
			roleSectionMapping.setFormId(Long.parseLong(formId));
			roleSectionMapping.setRoleMaster(roleMaster);
			roleSectionMappingDAO.save(roleSectionMapping);
		}

		if (overrideSection) {
			List<EmployeeRoleMapping> employeeRoleMappingList = employeeRoleMappingDAO
					.findByConditionRoleIdAndCompanyId(companyId,
							Long.parseLong(roleId));
			for (EmployeeRoleMapping employeeRoleMapping : employeeRoleMappingList) {
				employeeRoleSectionMappingDAO.deleteByCondition(
						employeeRoleMapping.getEmployee().getEmployeeId(),
						employeeRoleMapping.getRoleMaster().getRoleId(),
						employeeRoleMapping.getCompany().getCompanyId());
			}
			for (EmployeeRoleMapping employeeRoleMapping : employeeRoleMappingList) {
				for (int count = 0; count < sectionIdsArr.length; count++) {
					EmployeeRoleSectionMapping empRoleSectionMapping = new EmployeeRoleSectionMapping();
					String formId = sectionIdsArr[count].substring(0,
							sectionIdsArr[count].length() - 1);
					empRoleSectionMapping.setFormId(Long.parseLong(formId));
					empRoleSectionMapping.setRoleMaster(roleMaster);
					empRoleSectionMapping.setEmployee(employeeRoleMapping
							.getEmployee());
					empRoleSectionMapping.setCompany(employeeRoleMapping
							.getCompany());
					employeeRoleSectionMappingDAO.save(empRoleSectionMapping);
				}
			}
		}

		return "configuration.saved.successfully";

	}

	@Override
	public String saveEmployeeRoleWithDefaultCompany(Long companyId,
			String roleId, String[] userIdsArr, String[] allUserIds) {

		List<String> empDBDeleteList = new ArrayList<String>();
		List<String> empDBInsertList = new ArrayList<String>();
		List<String> empInsertList = new ArrayList<String>();
		List<EmployeeRoleMapping> empRoleMappingVOlist = employeeRoleMappingDAO
				.findByConditionRoleIdAndCompanyId(companyId,
						Long.parseLong(roleId));

		for (int count = 0; count < userIdsArr.length; count++) {
			empInsertList.add(userIdsArr[count]);
		}

		List<String> allUserIdsList = new ArrayList<String>();
		for (int count = 0; count < allUserIds.length; count++) {
			allUserIdsList.add(allUserIds[count]);
		}
		allUserIdsList.removeAll(empInsertList);

		if (!empRoleMappingVOlist.isEmpty()) {
			for (EmployeeRoleMapping employeeRoleMapping : empRoleMappingVOlist) {
				empDBInsertList.add(String.valueOf(employeeRoleMapping
						.getEmployee().getEmployeeId()));
				empDBDeleteList.add(String.valueOf(employeeRoleMapping
						.getEmployee().getEmployeeId()));
			}
		}

		if (!allUserIdsList.isEmpty()) {
			for (String empVO : allUserIdsList) {
				employeeRoleMappingDAO.deleteByConditionRoleAndEmpId(
						Long.parseLong(empVO), Long.parseLong(roleId));
				employeeRoleSectionMappingDAO.deleteByConditionRoleAndEmpId(
						Long.parseLong(empVO), Long.parseLong(roleId));
				companyEmployeeShortListDAO.deleteByEmployeeAndRole(
						Long.parseLong(empVO), Long.parseLong(roleId));

			}

		}

		empInsertList.removeAll(empDBInsertList);

		Set<EmployeeRoleMapping> employeeRoleMappingSet = new HashSet<EmployeeRoleMapping>();
		EmployeeRoleMapping employeeRoleMapping;
		EmployeeRoleMappingPK employeeRoleMappingPK;

		if (!empInsertList.isEmpty()) {
			for (String insertEmpId : empInsertList) {
				employeeRoleMapping = new EmployeeRoleMapping();
				employeeRoleMappingPK = new EmployeeRoleMappingPK();
				employeeRoleMappingPK.setCompanyId(companyId);
				employeeRoleMappingPK
						.setEmployeeId(Long.parseLong(insertEmpId));
				employeeRoleMappingPK.setRoleId(Long.parseLong(roleId));
				employeeRoleMapping.setId(employeeRoleMappingPK);
				employeeRoleMappingSet.add(employeeRoleMapping);
				employeeRoleMappingDAO.save(employeeRoleMapping);
			}
		}

		return "configuration.saved.successfully";

	}

	private List<DynamicFormSectionForm> getSectionIdListByCompany(
			Long companyId) {

		List<DynamicFormSectionForm> dynamicFormSectionFormList = new ArrayList<>();
		EntityMaster entityMaster = entityMasterDAO
				.findByEntityName(PayAsiaConstants.EMPLOYEE_ENTITY_NAME);

		ManageRolesConditionDTO conditionDTO = new ManageRolesConditionDTO();
		List<Object[]> tupleList = dynamicFormDAO
				.getTabNameAndFormIdByMaxVersion(companyId,
						entityMaster.getEntityId(), conditionDTO);
		for (Object[] tuple : tupleList) {
			String tabName = (String) tuple[1];
			if (!tabName
					.equalsIgnoreCase(PayAsiaConstants.EMPLOYEE_BASIC_TAB_NAME)) {
				DynamicFormSectionForm dynamicFormSectionForm = new DynamicFormSectionForm();
				dynamicFormSectionForm.setFormId(Long.parseLong(String
						.valueOf(tuple[0])));
				dynamicFormSectionFormList.add(dynamicFormSectionForm);
			}
		}

		return dynamicFormSectionFormList;

	}

	@Override
	public String saveEmployeeRoleWithAssignCompany(Long sessionCompanyId,
			String roleId, Long userId, String[] companyIdsArr) {
		boolean isPayasiaUserRoleOtherThanAdmin = false;
		Long payasiaCompanyGroupId = PayAsiaConstants.PAYASIA_COMPANY_GROUP_ID;

		ArrayList<String> roleList = new ArrayList<>();
		SecurityContext securityContext = SecurityContextHolder.getContext();
		Authentication authentication = securityContext.getAuthentication();
		for (GrantedAuthority grantedAuthority : authentication
				.getAuthorities()) {
			if (grantedAuthority.getAuthority().equals("ROLE_SUPER ADMIN")) {
				roleList.add("ROLE_ADMIN");
			}
		}
		if (!roleList.contains("ROLE_ADMIN")) {
			isPayasiaUserRoleOtherThanAdmin = true;
		}

		List<String> empDBDeleteList = new ArrayList<String>();
		List<String> empDBInsertList = new ArrayList<String>();
		List<String> empInsertList = new ArrayList<String>();
		List<EmployeeRoleMapping> empRoleMappingVOlist = employeeRoleMappingDAO
				.findByConditionRoleAndEmpID(Long.parseLong(roleId), userId,
						isPayasiaUserRoleOtherThanAdmin, payasiaCompanyGroupId);

		if (empRoleMappingVOlist != null) {
			for (EmployeeRoleMapping empVO : empRoleMappingVOlist) {
				empDBInsertList.add(String.valueOf(empVO.getEmployee()
						.getEmployeeId())
						+ "-"
						+ String.valueOf(empVO.getCompany().getCompanyId()));
				empDBDeleteList.add(String.valueOf(empVO.getEmployee()
						.getEmployeeId())
						+ "-"
						+ String.valueOf(empVO.getCompany().getCompanyId()));
			}
		}

		for (int count = 0; count < companyIdsArr.length; count++) {
			empInsertList.add(String.valueOf(userId) + "-"
					+ companyIdsArr[count]);
		}

		empDBDeleteList.removeAll(empInsertList);
		if (!empDBDeleteList.isEmpty()) {
			for (String empVO : empDBDeleteList) {
				String[] empAndCompId = empVO.split("-");
				employeeRoleMappingDAO
						.deleteByCondition(userId, Long.parseLong(roleId),
								Long.parseLong(empAndCompId[1]));
				employeeRoleSectionMappingDAO
						.deleteByCondition(userId, Long.parseLong(roleId),
								Long.parseLong(empAndCompId[1]));
			}

		}

		empInsertList.removeAll(empDBInsertList);

		Set<EmployeeRoleMapping> employeeRoleMappingSet = new HashSet<EmployeeRoleMapping>();
		EmployeeRoleMapping employeeRoleMapping;
		EmployeeRoleMappingPK employeeRoleMappingPK;

		Employee employee = employeeDAO.findById(userId);
		RoleMaster roleMaster = roleMasterDAO.findByID(Long.parseLong(roleId));

		if (!empInsertList.isEmpty()) {
			for (String insertEmpId : empInsertList) {
				String[] empAndCompId = insertEmpId.split("-");
				employeeRoleMapping = new EmployeeRoleMapping();
				employeeRoleMappingPK = new EmployeeRoleMappingPK();
				employeeRoleMappingPK.setCompanyId(Long
						.parseLong(empAndCompId[1]));
				employeeRoleMappingPK.setEmployeeId(Long
						.parseLong(empAndCompId[0]));
				employeeRoleMappingPK.setRoleId(Long.parseLong(roleId));
				employeeRoleMapping.setId(employeeRoleMappingPK);
				employeeRoleMappingSet.add(employeeRoleMapping);
				employeeRoleMappingDAO.save(employeeRoleMapping);

				Long userCompanyId = Long.parseLong(empAndCompId[1]);

				if (sessionCompanyId.equals(userCompanyId)) {
					List<EmployeeRoleSectionMapping> empRoleSectionMappingVOlist = employeeRoleSectionMappingDAO
							.findByCondition(Long.parseLong(roleId), userId,
									sessionCompanyId);
					if (!empRoleSectionMappingVOlist.isEmpty()) {
						for (EmployeeRoleSectionMapping roleSectionMapping : empRoleSectionMappingVOlist) {
							EmployeeRoleSectionMapping employeeRoleSectionMapping = new EmployeeRoleSectionMapping();
							Company company = companyDAO
									.findById(userCompanyId);
							employeeRoleSectionMapping.setCompany(company);
							employeeRoleSectionMapping
									.setRoleMaster(roleMaster);
							employeeRoleSectionMapping.setEmployee(employee);
							employeeRoleSectionMapping
									.setFormId(roleSectionMapping.getFormId());
							employeeRoleSectionMappingDAO
									.save(employeeRoleSectionMapping);
						}

					} else {
						List<DynamicFormSectionForm> dynamicFormSectionFormList = getSectionIdListByCompany(sessionCompanyId);
						for (DynamicFormSectionForm sectionForm : dynamicFormSectionFormList) {
							EmployeeRoleSectionMapping employeeRoleSectionMapping = new EmployeeRoleSectionMapping();
							Company company = companyDAO
									.findById(userCompanyId);
							employeeRoleSectionMapping.setCompany(company);
							employeeRoleSectionMapping
									.setRoleMaster(roleMaster);
							employeeRoleSectionMapping.setEmployee(employee);
							employeeRoleSectionMapping.setFormId(sectionForm
									.getFormId());
							employeeRoleSectionMappingDAO
									.save(employeeRoleSectionMapping);
						}
					}

				} else {
					List<DynamicFormSectionForm> dynamicFormSectionFormList = getSectionIdListByCompany(userCompanyId);
					for (DynamicFormSectionForm sectionForm : dynamicFormSectionFormList) {
						EmployeeRoleSectionMapping employeeRoleSectionMapping = new EmployeeRoleSectionMapping();
						Company company = companyDAO.findById(userCompanyId);
						employeeRoleSectionMapping.setCompany(company);
						employeeRoleSectionMapping.setRoleMaster(roleMaster);
						employeeRoleSectionMapping.setEmployee(employee);
						employeeRoleSectionMapping.setFormId(sectionForm
								.getFormId());
						employeeRoleSectionMappingDAO
								.save(employeeRoleSectionMapping);
					}
				}

			}
		}

		return "configuration.saved.successfully";

	}

	@Override
	public String saveCompanySection(Long roleId, Long companyId, Long userId,
			String[] sectionIdsArr) {

		List<String> empDBDeleteList = new ArrayList<String>();
		List<String> empDBInsertList = new ArrayList<String>();
		List<String> empInsertList = new ArrayList<String>();
		List<EmployeeRoleSectionMapping> empRoleSectionMappingVOlist = employeeRoleSectionMappingDAO
				.findByCondition(roleId, userId, companyId);

		if (empRoleSectionMappingVOlist != null) {
			for (EmployeeRoleSectionMapping empVO : empRoleSectionMappingVOlist) {
				empDBInsertList.add(String.valueOf(empVO.getEmployee()
						.getEmployeeId())
						+ "-"
						+ String.valueOf(empVO.getFormId()));
				empDBDeleteList.add(String.valueOf(empVO.getEmployee()
						.getEmployeeId())
						+ "-"
						+ String.valueOf(empVO.getFormId()));
			}
		}

		for (int count = 0; count < sectionIdsArr.length; count++) {
			empInsertList.add(String.valueOf(userId) + "-"
					+ sectionIdsArr[count]);
		}

		empDBDeleteList.removeAll(empInsertList);
		if (!empDBDeleteList.isEmpty()) {
			for (String empVO : empDBDeleteList) {
				String[] empAndSectionId = empVO.split("-");
				employeeRoleSectionMappingDAO.deleteByCondition(userId, roleId,
						companyId, Long.parseLong(empAndSectionId[1]));
			}

		}

		empInsertList.removeAll(empDBInsertList);

		RoleMaster roleMaster = roleMasterDAO.findByID(roleId);
		Employee employee = employeeDAO.findById(userId);
		Company company = companyDAO.findById(companyId);

		if (!empInsertList.isEmpty()) {
			for (String insertEmpId : empInsertList) {
				String[] empAndSectionId = insertEmpId.split("-");
				EmployeeRoleSectionMapping employeeRoleSectionMapping = new EmployeeRoleSectionMapping();
				employeeRoleSectionMapping.setEmployee(employee);
				employeeRoleSectionMapping.setCompany(company);
				employeeRoleSectionMapping.setRoleMaster(roleMaster);
				employeeRoleSectionMapping.setFormId(Long
						.parseLong(empAndSectionId[1]));
				employeeRoleSectionMappingDAO.save(employeeRoleSectionMapping);
			}
		}

		return "configuration.saved.successfully";
	}

	@Override
	public List<DynamicFormSectionForm> getAssignCompanySection(Long roleId,
			Long employeeId, Long companyId) {
		List<DynamicFormSectionForm> dynamicFormSectionFormList = new ArrayList<>();
		List<EmployeeRoleSectionMapping> empRoleSectionMappingVOlist = employeeRoleSectionMappingDAO
				.findByCondition(roleId, employeeId, companyId);
		for (EmployeeRoleSectionMapping empRoleSectionMapping : empRoleSectionMappingVOlist) {
			DynamicFormSectionForm dynamicFormSectionForm = new DynamicFormSectionForm();
			dynamicFormSectionForm.setFormId(empRoleSectionMapping.getFormId());
			dynamicFormSectionFormList.add(dynamicFormSectionForm);
		}

		return dynamicFormSectionFormList;
	}

	@Override
	public String isSeletedCompanyAssignToUser(Long roleId, Long employeeId,
			Long companyId) {
		EmployeeRoleMapping empRoleMappingVO = employeeRoleMappingDAO
				.findByCondition(roleId, companyId, employeeId);
		if (empRoleMappingVO != null) {
			return PayAsiaConstants.TRUE;
		} else {
			return PayAsiaConstants.FALSE;
		}
	}
}
