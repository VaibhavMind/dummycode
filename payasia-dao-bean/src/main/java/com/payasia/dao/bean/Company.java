package com.payasia.dao.bean;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * The persistent class for the Company database table.
 * 
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "Company")
public class Company extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Company_ID")
	private long companyId;

	@Column(name = "Company_Code")
	private String companyCode;

	@Column(name = "Short_Company_Code")
	private String shortCompanyCode;

	@Column(name = "Company_Name")
	private String companyName;

	@Column(name = "Date_Format")
	private String dateFormat = "dd MMM yyyy";

	@Column(name = "Part")
	private Integer part;

	@Column(name = "Audit_Enable")
	private Boolean auditEnable = false;

	@Column(name = "Payslip_Import_Type")
	private String payslipImportType = "E";

	@Column(name = "Active")
	private boolean active;

	@Column(name = "Is_Demo_Company")
	private boolean isDemoCompany;
	
	@Column(name = "Is_Two_Factor_Auth")
	private Boolean isTwoFactorAuth;

	@OneToMany(mappedBy = "company")
	private Set<ClaimApplication> claimApplications;

	@OneToMany(mappedBy = "company")
	private Set<ClaimBatchMaster> claimBatchMasters;

	@OneToMany(mappedBy = "company")
	private Set<ClaimCategoryMaster> claimCategoryMasters;

	@OneToMany(mappedBy = "company")
	private Set<ClaimItemMaster> claimItemMasters;

	@OneToMany(mappedBy = "company")
	private Set<ClaimTemplate> claimTemplates;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "Group_ID")
	private CompanyGroup companyGroup;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "Country_ID")
	private CountryMaster countryMaster;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "Base_Currency")
	private CurrencyMaster currencyMaster;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "Fin_Year_ID")
	private FinancialYearMaster financialYearMaster;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "Payslip_Frequency_ID")
	private PayslipFrequency payslipFrequency;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "Time_Zone_ID")
	private TimeZoneMaster timeZoneMaster;

	@OneToMany(mappedBy = "company")
	private Set<CompanyDocument> companyDocuments;

	@OneToMany(mappedBy = "company")
	private Set<CompanyExchangeRate> companyExchangeRates;

	@OneToMany(mappedBy = "company")
	private Set<CompanyExternalLink> companyExternalLinks;

	@OneToMany(mappedBy = "company")
	private Set<CompanyLogo> companyLogos;

	@OneToMany(mappedBy = "company")
	private Set<DataDictionary> dataDictionaries;

	@OneToMany(mappedBy = "company")
	private Set<DataImportHistory> dataImportHistories;

	@OneToMany(mappedBy = "company")
	private Set<DynamicForm> dynamicForms;

	@OneToMany(mappedBy = "company")
	private Set<EmailPreferenceMaster> emailPreferenceMasters;

	@OneToMany(mappedBy = "company")
	private Set<EmailTemplate> emailTemplates;

	@OneToMany(mappedBy = "company")
	private Set<EmpDataExportTemplate> empDataExportTemplates;

	@OneToMany(mappedBy = "company")
	private Set<EmpDataImportTemplate> empDataImportTemplates;

	@OneToMany(mappedBy = "company")
	private Set<EmpNoSeriesMaster> empNoSeriesMasters;

	@OneToMany(mappedBy = "company")
	private Set<Employee> employees;

	@OneToMany(mappedBy = "company")
	private Set<EmployeePreferenceMaster> employeePreferenceMasters;

	@OneToMany(mappedBy = "company")
	private Set<EmployeeRoleMapping> employeeRoleMappings;

	@OneToMany(mappedBy = "company")
	private Set<EmployeeTypeMaster> employeeTypeMasters;

	@OneToMany(mappedBy = "company")
	private Set<EntityListView> entityListViews;

	@OneToMany(mappedBy = "company")
	private Set<HRLetter> hrLetters;

	@OneToMany(mappedBy = "company")
	private Set<CompanyHolidayCalendar> companyHolidayCalendars;

	@OneToMany(mappedBy = "company")
	private Set<LeaveApplication> leaveApplications;

	@OneToMany(mappedBy = "company")
	private Set<LeaveBatchMaster> leaveBatchMasters;

	@OneToMany(mappedBy = "company")
	private Set<LeaveScheme> leaveSchemes;

	@OneToMany(mappedBy = "company")
	private Set<LeaveTypeMaster> leaveTypeMasters;

	@OneToMany(mappedBy = "company")
	private Set<OTApplication> otApplications;

	@OneToMany(mappedBy = "company")
	private Set<OTBatchMaster> otBatchMasters;

	@OneToMany(mappedBy = "company")
	private Set<OTItemMaster> otItemMasters;

	@OneToMany(mappedBy = "company")
	private Set<OTTemplate> otTemplates;

	@OneToMany(mappedBy = "company")
	private Set<PasswordPolicyConfigMaster> passwordPolicyConfigMasters;

	@OneToMany(mappedBy = "company")
	private Set<PasswordSecurityQuestionMaster> passwordSecurityQuestionMasters;

	@OneToMany(mappedBy = "company")
	private Set<PayDataCollection> payDataCollections;

	@OneToMany(mappedBy = "company")
	private Set<Paycode> paycodes;

	@OneToMany(mappedBy = "company")
	private Set<Payslip> payslips;

	@OneToMany(mappedBy = "company")
	private Set<ReportMaster> reportMasters;

	@OneToMany(mappedBy = "company")
	private Set<RoleMaster> roleMasters;

	@OneToMany(mappedBy = "company")
	private Set<WorkflowDelegate> workflowDelegates;

	@OneToMany(mappedBy = "company")
	private Set<PayslipUploadHistory> payslipUploadHistories;

	@OneToMany(mappedBy = "company")
	private Set<Announcement> announcements;

	@OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
	private Set<CompanyModuleMapping> companyModuleMappings;

	@OneToMany(mappedBy = "company")
	private Set<CalendarCodeMaster> calendarCodeMasters;

	@OneToMany(mappedBy = "company")
	private Set<CalendarPatternMaster> calendarPatternMasters;

	@OneToMany(mappedBy = "company")
	private Set<CompanyCalendarTemplate> companyCalendarTemplate;

	@OneToMany(mappedBy = "company")
	private Set<ReminderEventConfig> reminderEventConfigs;

	@OneToMany(mappedBy = "company")
	private Set<LeaveGrantBatch> leaveGrantBatchs;

	@OneToMany(mappedBy = "company")
	private Set<Email> emails;

	@OneToMany(mappedBy = "company")
	private Set<LeavePreference> leavePreferences;

	@OneToMany(mappedBy = "company")
	private Set<SchedulerStatus> schedulerStatuses;

	@OneToMany(mappedBy = "company")
	private Set<LeaveYearEndBatch> leaveYearEndBatchs;

	@OneToMany(mappedBy = "company")
	private Set<ClaimPreference> claimPreferences;

	@OneToMany(mappedBy = "company")
	private Set<CompanyPayslipRelease> companyPayslipReleases;

	@OneToMany(mappedBy = "company")
	private Set<EmployeeRoleSectionMapping> employeeRoleSectionMappings;

	@OneToMany(mappedBy = "company")
	private Set<YearEndProcessSchedule> yearEndProcessSchedules;

	public Company() {
	}

	public long getCompanyId() {
		return this.companyId;
	}

	public void setCompanyId(long companyId) {
		this.companyId = companyId;
	}

	public String getCompanyCode() {
		return this.companyCode;
	}

	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}

	public String getCompanyName() {
		return this.companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getDateFormat() {
		return this.dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	public Integer getPart() {
		return this.part;
	}

	public void setPart(Integer part) {
		this.part = part;
	}

	public Set<ClaimApplication> getClaimApplications() {
		return this.claimApplications;
	}

	public void setClaimApplications(Set<ClaimApplication> claimApplications) {
		this.claimApplications = claimApplications;
	}

	public Set<ClaimBatchMaster> getClaimBatchMasters() {
		return this.claimBatchMasters;
	}

	public void setClaimBatchMasters(Set<ClaimBatchMaster> claimBatchMasters) {
		this.claimBatchMasters = claimBatchMasters;
	}

	public Set<ClaimCategoryMaster> getClaimCategoryMasters() {
		return this.claimCategoryMasters;
	}

	public void setClaimCategoryMasters(
			Set<ClaimCategoryMaster> claimCategoryMasters) {
		this.claimCategoryMasters = claimCategoryMasters;
	}

	public Set<ClaimItemMaster> getClaimItemMasters() {
		return this.claimItemMasters;
	}

	public void setClaimItemMasters(Set<ClaimItemMaster> claimItemMasters) {
		this.claimItemMasters = claimItemMasters;
	}

	public Set<ClaimTemplate> getClaimTemplates() {
		return this.claimTemplates;
	}

	public void setClaimTemplates(Set<ClaimTemplate> claimTemplates) {
		this.claimTemplates = claimTemplates;
	}

	public CompanyGroup getCompanyGroup() {
		return this.companyGroup;
	}

	public void setCompanyGroup(CompanyGroup companyGroup) {
		this.companyGroup = companyGroup;
	}

	public CountryMaster getCountryMaster() {
		return this.countryMaster;
	}

	public void setCountryMaster(CountryMaster countryMaster) {
		this.countryMaster = countryMaster;
	}

	public CurrencyMaster getCurrencyMaster() {
		return this.currencyMaster;
	}

	public void setCurrencyMaster(CurrencyMaster currencyMaster) {
		this.currencyMaster = currencyMaster;
	}

	public FinancialYearMaster getFinancialYearMaster() {
		return this.financialYearMaster;
	}

	public void setFinancialYearMaster(FinancialYearMaster financialYearMaster) {
		this.financialYearMaster = financialYearMaster;
	}

	public PayslipFrequency getPayslipFrequency() {
		return this.payslipFrequency;
	}

	public void setPayslipFrequency(PayslipFrequency payslipFrequency) {
		this.payslipFrequency = payslipFrequency;
	}

	public TimeZoneMaster getTimeZoneMaster() {
		return this.timeZoneMaster;
	}

	public void setTimeZoneMaster(TimeZoneMaster timeZoneMaster) {
		this.timeZoneMaster = timeZoneMaster;
	}

	public Set<CompanyDocument> getCompanyDocuments() {
		return this.companyDocuments;
	}

	public void setCompanyDocuments(Set<CompanyDocument> companyDocuments) {
		this.companyDocuments = companyDocuments;
	}

	public Set<CompanyExchangeRate> getCompanyExchangeRates() {
		return this.companyExchangeRates;
	}

	public void setCompanyExchangeRates(
			Set<CompanyExchangeRate> companyExchangeRates) {
		this.companyExchangeRates = companyExchangeRates;
	}

	public Set<CompanyExternalLink> getCompanyExternalLinks() {
		return this.companyExternalLinks;
	}

	public void setCompanyExternalLinks(
			Set<CompanyExternalLink> companyExternalLinks) {
		this.companyExternalLinks = companyExternalLinks;
	}

	public Set<CompanyLogo> getCompanyLogos() {
		return this.companyLogos;
	}

	public void setCompanyLogos(Set<CompanyLogo> companyLogos) {
		this.companyLogos = companyLogos;
	}

	public Set<DataDictionary> getDataDictionaries() {
		return this.dataDictionaries;
	}

	public void setDataDictionaries(Set<DataDictionary> dataDictionaries) {
		this.dataDictionaries = dataDictionaries;
	}

	public Set<DataImportHistory> getDataImportHistories() {
		return this.dataImportHistories;
	}

	public void setDataImportHistories(
			Set<DataImportHistory> dataImportHistories) {
		this.dataImportHistories = dataImportHistories;
	}

	public Set<DynamicForm> getDynamicForms() {
		return this.dynamicForms;
	}

	public void setDynamicForms(Set<DynamicForm> dynamicForms) {
		this.dynamicForms = dynamicForms;
	}

	public Set<EmailPreferenceMaster> getEmailPreferenceMasters() {
		return this.emailPreferenceMasters;
	}

	public void setEmailPreferenceMasters(
			Set<EmailPreferenceMaster> emailPreferenceMasters) {
		this.emailPreferenceMasters = emailPreferenceMasters;
	}

	public Set<EmailTemplate> getEmailTemplates() {
		return this.emailTemplates;
	}

	public void setEmailTemplates(Set<EmailTemplate> emailTemplates) {
		this.emailTemplates = emailTemplates;
	}

	public Set<EmpDataExportTemplate> getEmpDataExportTemplates() {
		return this.empDataExportTemplates;
	}

	public void setEmpDataExportTemplates(
			Set<EmpDataExportTemplate> empDataExportTemplates) {
		this.empDataExportTemplates = empDataExportTemplates;
	}

	public Set<EmpDataImportTemplate> getEmpDataImportTemplates() {
		return this.empDataImportTemplates;
	}

	public void setEmpDataImportTemplates(
			Set<EmpDataImportTemplate> empDataImportTemplates) {
		this.empDataImportTemplates = empDataImportTemplates;
	}

	public Set<EmpNoSeriesMaster> getEmpNoSeriesMasters() {
		return this.empNoSeriesMasters;
	}

	public void setEmpNoSeriesMasters(Set<EmpNoSeriesMaster> empNoSeriesMasters) {
		this.empNoSeriesMasters = empNoSeriesMasters;
	}

	public Set<Employee> getEmployees() {
		return this.employees;
	}

	public void setEmployees(Set<Employee> employees) {
		this.employees = employees;
	}

	public Set<EmployeePreferenceMaster> getEmployeePreferenceMasters() {
		return this.employeePreferenceMasters;
	}

	public void setEmployeePreferenceMasters(
			Set<EmployeePreferenceMaster> employeePreferenceMasters) {
		this.employeePreferenceMasters = employeePreferenceMasters;
	}

	public Set<EmployeeRoleMapping> getEmployeeRoleMappings() {
		return this.employeeRoleMappings;
	}

	public void setEmployeeRoleMappings(
			Set<EmployeeRoleMapping> employeeRoleMappings) {
		this.employeeRoleMappings = employeeRoleMappings;
	}

	public Set<EmployeeTypeMaster> getEmployeeTypeMasters() {
		return this.employeeTypeMasters;
	}

	public void setEmployeeTypeMasters(
			Set<EmployeeTypeMaster> employeeTypeMasters) {
		this.employeeTypeMasters = employeeTypeMasters;
	}

	public Set<EntityListView> getEntityListViews() {
		return this.entityListViews;
	}

	public void setEntityListViews(Set<EntityListView> entityListViews) {
		this.entityListViews = entityListViews;
	}

	public Set<HRLetter> getHrLetters() {
		return this.hrLetters;
	}

	public void setHrLetters(Set<HRLetter> hrLetters) {
		this.hrLetters = hrLetters;
	}

	public Set<CompanyHolidayCalendar> getCompanyHolidayCalendars() {
		return companyHolidayCalendars;
	}

	public void setCompanyHolidayCalendars(
			Set<CompanyHolidayCalendar> companyHolidayCalendars) {
		this.companyHolidayCalendars = companyHolidayCalendars;
	}

	public Set<LeaveApplication> getLeaveApplications() {
		return this.leaveApplications;
	}

	public void setLeaveApplications(Set<LeaveApplication> leaveApplications) {
		this.leaveApplications = leaveApplications;
	}

	public Set<LeaveBatchMaster> getLeaveBatchMasters() {
		return this.leaveBatchMasters;
	}

	public void setLeaveBatchMasters(Set<LeaveBatchMaster> leaveBatchMasters) {
		this.leaveBatchMasters = leaveBatchMasters;
	}

	public Set<LeaveScheme> getLeaveSchemes() {
		return this.leaveSchemes;
	}

	public void setLeaveSchemes(Set<LeaveScheme> leaveSchemes) {
		this.leaveSchemes = leaveSchemes;
	}

	public Set<LeaveTypeMaster> getLeaveTypeMasters() {
		return this.leaveTypeMasters;
	}

	public void setLeaveTypeMasters(Set<LeaveTypeMaster> leaveTypeMasters) {
		this.leaveTypeMasters = leaveTypeMasters;
	}

	public Set<OTApplication> getOtApplications() {
		return this.otApplications;
	}

	public void setOtApplications(Set<OTApplication> otApplications) {
		this.otApplications = otApplications;
	}

	public Set<OTBatchMaster> getOtBatchMasters() {
		return this.otBatchMasters;
	}

	public void setOtBatchMasters(Set<OTBatchMaster> otBatchMasters) {
		this.otBatchMasters = otBatchMasters;
	}

	public Set<OTItemMaster> getOtItemMasters() {
		return this.otItemMasters;
	}

	public void setOtItemMasters(Set<OTItemMaster> otItemMasters) {
		this.otItemMasters = otItemMasters;
	}

	public Set<OTTemplate> getOtTemplates() {
		return this.otTemplates;
	}

	public void setOtTemplates(Set<OTTemplate> otTemplates) {
		this.otTemplates = otTemplates;
	}

	public Set<PasswordPolicyConfigMaster> getPasswordPolicyConfigMasters() {
		return this.passwordPolicyConfigMasters;
	}

	public void setPasswordPolicyConfigMasters(
			Set<PasswordPolicyConfigMaster> passwordPolicyConfigMasters) {
		this.passwordPolicyConfigMasters = passwordPolicyConfigMasters;
	}

	public Set<PasswordSecurityQuestionMaster> getPasswordSecurityQuestionMasters() {
		return this.passwordSecurityQuestionMasters;
	}

	public void setPasswordSecurityQuestionMasters(
			Set<PasswordSecurityQuestionMaster> passwordSecurityQuestionMasters) {
		this.passwordSecurityQuestionMasters = passwordSecurityQuestionMasters;
	}

	public Set<PayDataCollection> getPayDataCollections() {
		return this.payDataCollections;
	}

	public void setPayDataCollections(Set<PayDataCollection> payDataCollections) {
		this.payDataCollections = payDataCollections;
	}

	public Set<Paycode> getPaycodes() {
		return this.paycodes;
	}

	public void setPaycodes(Set<Paycode> paycodes) {
		this.paycodes = paycodes;
	}

	public Set<Payslip> getPayslips() {
		return this.payslips;
	}

	public void setPayslips(Set<Payslip> payslips) {
		this.payslips = payslips;
	}

	public Set<ReportMaster> getReportMasters() {
		return this.reportMasters;
	}

	public void setReportMasters(Set<ReportMaster> reportMasters) {
		this.reportMasters = reportMasters;
	}

	public Set<RoleMaster> getRoleMasters() {
		return this.roleMasters;
	}

	public void setRoleMasters(Set<RoleMaster> roleMasters) {
		this.roleMasters = roleMasters;
	}

	public Set<WorkflowDelegate> getWorkflowDelegates() {
		return this.workflowDelegates;
	}

	public void setWorkflowDelegates(Set<WorkflowDelegate> workflowDelegates) {
		this.workflowDelegates = workflowDelegates;
	}

	public Set<PayslipUploadHistory> getPayslipUploadHistories() {
		return this.payslipUploadHistories;
	}

	public void setPayslipUploadHistories(
			Set<PayslipUploadHistory> payslipUploadHistories) {
		this.payslipUploadHistories = payslipUploadHistories;
	}

	public Boolean getAuditEnable() {
		return auditEnable;
	}

	public void setAuditEnable(Boolean auditEnable) {
		this.auditEnable = auditEnable;
	}

	public Set<Announcement> getAnnouncements() {
		return announcements;
	}

	public void setAnnouncements(Set<Announcement> announcements) {
		this.announcements = announcements;
	}

	public Set<CompanyModuleMapping> getCompanyModuleMappings() {
		return companyModuleMappings;
	}

	public void setCompanyModuleMappings(
			Set<CompanyModuleMapping> companyModuleMappings) {
		this.companyModuleMappings = companyModuleMappings;
	}

	public Set<CalendarCodeMaster> getCalendarCodeMasters() {
		return calendarCodeMasters;
	}

	public void setCalendarCodeMasters(
			Set<CalendarCodeMaster> calendarCodeMasters) {
		this.calendarCodeMasters = calendarCodeMasters;
	}

	public Set<CalendarPatternMaster> getCalendarPatternMasters() {
		return calendarPatternMasters;
	}

	public void setCalendarPatternMasters(
			Set<CalendarPatternMaster> calendarPatternMasters) {
		this.calendarPatternMasters = calendarPatternMasters;
	}

	public Set<CompanyCalendarTemplate> getCompanyCalendarTemplate() {
		return companyCalendarTemplate;
	}

	public void setCompanyCalendarTemplate(
			Set<CompanyCalendarTemplate> companyCalendarTemplate) {
		this.companyCalendarTemplate = companyCalendarTemplate;
	}

	public Set<ReminderEventConfig> getReminderEventConfigs() {
		return reminderEventConfigs;
	}

	public void setReminderEventConfigs(
			Set<ReminderEventConfig> reminderEventConfigs) {
		this.reminderEventConfigs = reminderEventConfigs;
	}

	public Set<LeaveGrantBatch> getLeaveGrantBatchs() {
		return leaveGrantBatchs;
	}

	public void setLeaveGrantBatchs(Set<LeaveGrantBatch> leaveGrantBatchs) {
		this.leaveGrantBatchs = leaveGrantBatchs;
	}

	public Set<Email> getEmails() {
		return emails;
	}

	public void setEmails(Set<Email> emails) {
		this.emails = emails;
	}

	public Set<LeavePreference> getLeavePreferences() {
		return leavePreferences;
	}

	public void setLeavePreferences(Set<LeavePreference> leavePreferences) {
		this.leavePreferences = leavePreferences;
	}

	public Set<SchedulerStatus> getSchedulerStatuses() {
		return schedulerStatuses;
	}

	public void setSchedulerStatuses(Set<SchedulerStatus> schedulerStatuses) {
		this.schedulerStatuses = schedulerStatuses;
	}

	public Set<LeaveYearEndBatch> getLeaveYearEndBatchs() {
		return leaveYearEndBatchs;
	}

	public void setLeaveYearEndBatchs(Set<LeaveYearEndBatch> leaveYearEndBatchs) {
		this.leaveYearEndBatchs = leaveYearEndBatchs;
	}

	public Set<ClaimPreference> getClaimPreferences() {
		return claimPreferences;
	}

	public void setClaimPreferences(Set<ClaimPreference> claimPreferences) {
		this.claimPreferences = claimPreferences;
	}

	public Set<CompanyPayslipRelease> getCompanyPayslipReleases() {
		return companyPayslipReleases;
	}

	public void setCompanyPayslipReleases(
			Set<CompanyPayslipRelease> companyPayslipReleases) {
		this.companyPayslipReleases = companyPayslipReleases;
	}

	public String getPayslipImportType() {
		return payslipImportType;
	}

	public void setPayslipImportType(String payslipImportType) {
		this.payslipImportType = payslipImportType;
	}

	public Set<EmployeeRoleSectionMapping> getEmployeeRoleSectionMappings() {
		return employeeRoleSectionMappings;
	}

	public void setEmployeeRoleSectionMappings(
			Set<EmployeeRoleSectionMapping> employeeRoleSectionMappings) {
		this.employeeRoleSectionMappings = employeeRoleSectionMappings;
	}

	public Set<YearEndProcessSchedule> getYearEndProcessSchedules() {
		return yearEndProcessSchedules;
	}

	public void setYearEndProcessSchedules(
			Set<YearEndProcessSchedule> yearEndProcessSchedules) {
		this.yearEndProcessSchedules = yearEndProcessSchedules;
	}

	public String getShortCompanyCode() {
		return shortCompanyCode;
	}

	public void setShortCompanyCode(String shortCompanyCode) {
		this.shortCompanyCode = shortCompanyCode;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isDemoCompany() {
		return isDemoCompany;
	}

	public void setDemoCompany(boolean isDemoCompany) {
		this.isDemoCompany = isDemoCompany;
	}

	public Boolean getIsTwoFactorAuth() {
		return isTwoFactorAuth;
	}

	public void setIsTwoFactorAuth(Boolean isTwoFactorAuth) {
		this.isTwoFactorAuth = isTwoFactorAuth;
	}

}