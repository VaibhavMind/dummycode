package com.payasia.dao.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.hibernate.jdbc.Work;
import org.springframework.stereotype.Repository;

import com.payasia.common.dto.CalendarTemplateConditionDTO;
import com.payasia.common.dto.LeaveCalendarEventDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.SortConstants;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.CompanyCalendarTemplateDAO;
import com.payasia.dao.bean.CalendarPatternMaster;
import com.payasia.dao.bean.CalendarPatternMaster_;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.CompanyCalendarTemplate;
import com.payasia.dao.bean.CompanyCalendarTemplate_;
import com.payasia.dao.bean.Company_;

@Repository
public class CompanyCalendarTemplateDAOImpl extends BaseDAO implements
		CompanyCalendarTemplateDAO {

	@Override
	protected Object getBaseEntity() {
		CompanyCalendarTemplate companyCalendarTemplate = new CompanyCalendarTemplate();
		return companyCalendarTemplate;
	}

	@Override
	public void update(CompanyCalendarTemplate companyCalendarTemplate) {
		super.update(companyCalendarTemplate);
	}

	@Override
	public void delete(CompanyCalendarTemplate companyCalendarTemplate) {
		super.delete(companyCalendarTemplate);
	}

	@Override
	public void save(CompanyCalendarTemplate companyCalendarTemplate) {
		super.save(companyCalendarTemplate);
	}

	@Override
	public CompanyCalendarTemplate findByID(Long companyCalendarTemplateId) {
		return super.findById(CompanyCalendarTemplate.class,
				companyCalendarTemplateId);
	}

	@Override
	public List<CompanyCalendarTemplate> findByCompanyId(
			CalendarTemplateConditionDTO conditionDTO, PageRequest pageDTO,
			SortCondition sortDTO, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CompanyCalendarTemplate> criteriaQuery = cb
				.createQuery(CompanyCalendarTemplate.class);

		Root<CompanyCalendarTemplate> calTempRoot = criteriaQuery
				.from(CompanyCalendarTemplate.class);

		criteriaQuery.select(calTempRoot);

		Join<CompanyCalendarTemplate, Company> companyJoin = calTempRoot
				.join(CompanyCalendarTemplate_.company);
		Join<CompanyCalendarTemplate, CalendarPatternMaster> patternMasterJoin = calTempRoot
				.join(CompanyCalendarTemplate_.calendarPatternMaster);
		Predicate restriction = cb.conjunction();

		if (StringUtils.isNotBlank(conditionDTO.getTemplateName())) {
			restriction = cb.and(restriction, cb.like(cb.upper((calTempRoot
					.get(CompanyCalendarTemplate_.templateName))), conditionDTO
					.getTemplateName().toUpperCase()));
		}
		if (StringUtils.isNotBlank(conditionDTO.getTemplateDesc())) {
			restriction = cb.and(restriction, cb.like(cb.upper((calTempRoot
					.get(CompanyCalendarTemplate_.templateDesc))), conditionDTO
					.getTemplateDesc().toUpperCase()));
		}
		if (StringUtils.isNotBlank(conditionDTO.getYear())) {
			restriction = cb.and(restriction, cb.equal(
					(calTempRoot.get(CompanyCalendarTemplate_.Start_Year)),
					conditionDTO.getYear()));
		}
		if (StringUtils.isNotBlank(conditionDTO.getPatternName())) {
			restriction = cb.and(restriction, cb.like(cb
					.upper(patternMasterJoin
							.get(CalendarPatternMaster_.patternName)),
					conditionDTO.getPatternName().toUpperCase()));
		}

		restriction = cb.and(restriction,
				cb.equal((companyJoin.get(Company_.companyId)), companyId));

		criteriaQuery.where(restriction);

		if (sortDTO != null) {

			Path<String> sortPath = getSortPathForCalTemplate(sortDTO,
					calTempRoot, patternMasterJoin);

			if (sortPath != null) {
				if (SortConstants.DB_ORDER_BY_ASC.equalsIgnoreCase(sortDTO
						.getOrderType())) {
					criteriaQuery.orderBy(cb.asc(sortPath));
				}
				if (SortConstants.DB_ORDER_BY_DESC.equalsIgnoreCase(sortDTO
						.getOrderType())) {
					criteriaQuery.orderBy(cb.desc(sortPath));
				}
			}
		}

		TypedQuery<CompanyCalendarTemplate> calTempTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			calTempTypedQuery.setFirstResult(getStartPosition(pageDTO));
			calTempTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		List<CompanyCalendarTemplate> calTempList = calTempTypedQuery
				.getResultList();

		return calTempList;
	}

	@Override
	public Integer getCountforfindByCompanyId(
			CalendarTemplateConditionDTO conditionDTO, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Integer> criteriaQuery = cb.createQuery(Integer.class);

		Root<CompanyCalendarTemplate> calTempRoot = criteriaQuery
				.from(CompanyCalendarTemplate.class);

		criteriaQuery.select(cb.count(calTempRoot).as(Integer.class));

		Join<CompanyCalendarTemplate, Company> companyJoin = calTempRoot
				.join(CompanyCalendarTemplate_.company);
		Join<CompanyCalendarTemplate, CalendarPatternMaster> patternMasterJoin = calTempRoot
				.join(CompanyCalendarTemplate_.calendarPatternMaster);
		Predicate restriction = cb.conjunction();

		if (StringUtils.isNotBlank(conditionDTO.getTemplateName())) {
			restriction = cb.and(restriction, cb.like(cb.upper((calTempRoot
					.get(CompanyCalendarTemplate_.templateName))), conditionDTO
					.getTemplateName().toUpperCase()));
		}
		if (StringUtils.isNotBlank(conditionDTO.getTemplateDesc())) {
			restriction = cb.and(restriction, cb.like(cb.upper((calTempRoot
					.get(CompanyCalendarTemplate_.templateDesc))), conditionDTO
					.getTemplateDesc().toUpperCase()));
		}
		if (StringUtils.isNotBlank(conditionDTO.getYear())) {
			restriction = cb.and(restriction, cb.equal(
					(calTempRoot.get(CompanyCalendarTemplate_.Start_Year)),
					conditionDTO.getYear()));
		}
		if (StringUtils.isNotBlank(conditionDTO.getPatternName())) {
			restriction = cb.and(restriction, cb.like(cb
					.upper(patternMasterJoin
							.get(CalendarPatternMaster_.patternName)),
					conditionDTO.getPatternName().toUpperCase()));
		}

		restriction = cb.and(restriction,
				cb.equal((companyJoin.get(Company_.companyId)), companyId));

		criteriaQuery.where(restriction);

		TypedQuery<Integer> calTempTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return calTempTypedQuery.getSingleResult();
	}

	public Path<String> getSortPathForCalTemplate(
			SortCondition sortDTO,
			Root<CompanyCalendarTemplate> calTempRoot,
			Join<CompanyCalendarTemplate, CalendarPatternMaster> patternMasterJoin) {

		List<String> calTemplateColList = new ArrayList<String>();
		calTemplateColList.add(SortConstants.CALENDAR_TEMPLATE_NAME);
		calTemplateColList.add(SortConstants.CALENDAR_TEMPLATE_DESCRIPTION);
		calTemplateColList.add(SortConstants.CALENDAR_YEAR);

		List<String> calPatternColList = new ArrayList<String>();
		calPatternColList.add(SortConstants.CALENDAR_PATTERN_NAME);

		Path<String> sortPath = null;

		if (calTemplateColList.contains(sortDTO.getColumnName())) {
			sortPath = calTempRoot.get(colMap.get(CompanyCalendarTemplate.class
					+ sortDTO.getColumnName()));
		}
		if (calPatternColList.contains(sortDTO.getColumnName())) {
			sortPath = patternMasterJoin
					.get(colMap.get(CalendarPatternMaster.class
							+ sortDTO.getColumnName()));
		}
		return sortPath;

	}

	@Override
	public List<LeaveCalendarEventDTO> getCompanyCalendarTemplate(
			final Long companyId, final Long companyCalendarTemplateId,
			final int year, final Boolean isCompanyCalendar,
			final String dateFormat, final Long employeeId) {
		final List<LeaveCalendarEventDTO> leaveCalendarEventDTOList = new ArrayList<>();

		getSession().doWork(new Work() {

			@Override
			public void execute(Connection connection) throws SQLException {
				try {
					CallableStatement cstmt = null;
					cstmt = connection
							.prepareCall("{call Get_Yearly_Leave_Calendar (?,?,?,?,?)}");

					cstmt.setLong("@Company_ID", companyId);
					cstmt.setLong("@Company_Calendar_Template_ID",
							companyCalendarTemplateId);
					cstmt.setInt("@Year", year);
					cstmt.setBoolean("@Is_Company_Calendar", isCompanyCalendar);
					cstmt.setLong("@Employee_ID", employeeId == null ? -1
							: employeeId);

					ResultSet rs = cstmt.executeQuery();
					if (rs != null) {
						while (rs.next()) {
							LeaveCalendarEventDTO calendarEventDTO = new LeaveCalendarEventDTO();
							calendarEventDTO.setCalendarDate(DateUtils.timeStampToStringForCalendar(
									rs.getTimestamp("Calendar_Date"),
									PayAsiaConstants.DEFAULT_DATE_FORMAT));
							calendarEventDTO.setPatternCode(rs
									.getString("Pattern_Code"));
							calendarEventDTO.setPatternValue(rs
									.getString("Pattern_Value"));
							calendarEventDTO.setIsChanged(rs
									.getBoolean("Is_Changed"));
							leaveCalendarEventDTOList.add(calendarEventDTO);
						}
					}
				} catch (SQLException e) {
					throw new PayAsiaSystemException(e.getMessage(), e);
				}

			}
		});

		return leaveCalendarEventDTOList;
	}

	@Override
	public List<Integer> getYearList(Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Integer> criteriaQuery = cb.createQuery(Integer.class);
		Root<CompanyCalendarTemplate> calRoot = criteriaQuery
				.from(CompanyCalendarTemplate.class);
		criteriaQuery.select(calRoot.get(CompanyCalendarTemplate_.Start_Year))
				.distinct(true);

		Join<CompanyCalendarTemplate, Company> companyJoin = calRoot
				.join(CompanyCalendarTemplate_.company);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(companyJoin.get(Company_.companyId), companyId));
		criteriaQuery.where(restriction);
		criteriaQuery.orderBy(cb.asc(calRoot
				.get(CompanyCalendarTemplate_.Start_Year)));

		TypedQuery<Integer> yearTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<Integer> yearList = yearTypedQuery.getResultList();
		return yearList;
	}

	@Override
	public List<CompanyCalendarTemplate> findByYearCompany(Integer year,
			Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CompanyCalendarTemplate> criteriaQuery = cb
				.createQuery(CompanyCalendarTemplate.class);
		Root<CompanyCalendarTemplate> calRoot = criteriaQuery
				.from(CompanyCalendarTemplate.class);
		criteriaQuery.select(calRoot);

		Join<CompanyCalendarTemplate, Company> companyJoin = calRoot
				.join(CompanyCalendarTemplate_.company);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(companyJoin.get(Company_.companyId), companyId));
		restriction = cb.and(restriction, cb.equal(
				calRoot.get(CompanyCalendarTemplate_.Start_Year), year));
		criteriaQuery.where(restriction);

		TypedQuery<CompanyCalendarTemplate> calTempTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<CompanyCalendarTemplate> calTempList = calTempTypedQuery
				.getResultList();
		return calTempList;
	}

	@Override
	public CompanyCalendarTemplate findByTemplateNameAndId(Long companyId,
			String calendarTemplateName, Long companyCalTemplateId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CompanyCalendarTemplate> criteriaQuery = cb
				.createQuery(CompanyCalendarTemplate.class);
		Root<CompanyCalendarTemplate> calTempRoot = criteriaQuery
				.from(CompanyCalendarTemplate.class);
		criteriaQuery.select(calTempRoot);

		Join<CompanyCalendarTemplate, Company> companyJoin = calTempRoot
				.join(CompanyCalendarTemplate_.company);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(companyJoin.get(Company_.companyId), companyId));
		restriction = cb.and(restriction, cb.equal(cb.upper(calTempRoot
				.get(CompanyCalendarTemplate_.templateName)),
				calendarTemplateName.toUpperCase()));
		if (companyCalTemplateId != null) {
			restriction = cb.and(restriction, cb.notEqual(calTempRoot
					.get(CompanyCalendarTemplate_.companyCalendarTemplateId),
					companyCalTemplateId));
		}

		criteriaQuery.where(restriction);

		TypedQuery<CompanyCalendarTemplate> calCodeTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		List<CompanyCalendarTemplate> calCodeList = calCodeTypedQuery
				.getResultList();
		if (calCodeList != null && !calCodeList.isEmpty()) {
			return calCodeList.get(0);
		}
		return null;
	}
}
