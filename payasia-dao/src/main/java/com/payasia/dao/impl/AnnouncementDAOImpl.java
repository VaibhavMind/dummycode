package com.payasia.dao.impl;

import java.sql.Date;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.payasia.common.form.AnouncementForm;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.dao.AnnouncementDAO;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.bean.Announcement;
import com.payasia.dao.bean.Announcement_;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.CompanyGroup;
import com.payasia.dao.bean.CompanyGroup_;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.HelpDesk;

@Repository
public class AnnouncementDAOImpl extends BaseDAO implements AnnouncementDAO {

	@Override
	protected Object getBaseEntity() {
		Announcement announcement = new Announcement();
		return announcement;
	}

	@Override
	public void save(Announcement announcement) {
		super.save(announcement);

	}
	
	@Override
	public List<Announcement> findByCompanyAndScope(Long companyId, String scope) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Announcement> criteriaQuery = cb.createQuery(Announcement.class);
		Root<Announcement> announcementRoot = criteriaQuery.from(Announcement.class);

		criteriaQuery.select(announcementRoot);

		Join<Announcement, Company> announcementCompJoin = announcementRoot.join(Announcement_.company);

		Predicate restriction = cb.conjunction();
		
		restriction = cb.and(restriction, cb.equal(announcementCompJoin.get(Company_.companyId), companyId));
		restriction = cb.and(restriction,
				cb.equal(cb.upper(announcementRoot.get(Announcement_.scope)), scope.toUpperCase()));
		criteriaQuery.where(restriction);

		criteriaQuery.orderBy(cb.desc(announcementRoot.get(Announcement_.announcementId)));
		
		TypedQuery<Announcement> announcementTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<Announcement> allAnnouncementList = announcementTypedQuery.getResultList();
		return allAnnouncementList;
	}

	public Predicate filterFunction(CriteriaBuilder cb, Root<Announcement> announcementRoot, Predicate restriction, AnouncementForm announcementForm) {
		if (announcementForm != null) {
			if (StringUtils.isNotBlank(announcementForm.getTitle())) {
				restriction = cb.and(restriction, cb.like(cb.upper(announcementRoot.get(Announcement_.title)),
						'%' + announcementForm.getTitle().toUpperCase() + '%'));
			}
			if (StringUtils.isNotBlank(announcementForm.getDescription())) {
				restriction = cb.and(restriction, cb.like(cb.upper(announcementRoot.get(Announcement_.description)),
						'%' + announcementForm.getDescription().toUpperCase() + '%'));
			}
			if (StringUtils.isNotBlank(announcementForm.getPostDateTime())) {
				restriction = cb.and(restriction, cb.equal(announcementRoot.get(Announcement_.postDateTime).as(Date.class),
						DateUtils.stringToDate(announcementForm.getPostDateTime())));
			}
			if (StringUtils.isNotBlank(announcementForm.getRemoveDateTime())) {
				restriction = cb.and(restriction, cb.equal(announcementRoot.get(Announcement_.removeDateTime).as(Date.class),
						DateUtils.stringToDate(announcementForm.getRemoveDateTime().toUpperCase())));
			}
		}
		return restriction;
	}
	
	@Override
	public List<Announcement> findByCompanyGroupAndScope(Long groupId, String scope) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Announcement> criteriaQuery = cb.createQuery(Announcement.class);
		Root<Announcement> announcementRoot = criteriaQuery.from(Announcement.class);

		criteriaQuery.select(announcementRoot);

		Join<Announcement, CompanyGroup> announcementGroupJoin = announcementRoot.join(Announcement_.companyGroup);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(announcementGroupJoin.get(CompanyGroup_.groupId), groupId));
		restriction = cb.and(restriction,
				cb.equal(cb.upper(announcementRoot.get(Announcement_.scope)), scope.toUpperCase()));

		criteriaQuery.where(restriction);

		criteriaQuery.orderBy(cb.desc(announcementRoot.get(Announcement_.announcementId)));
		
		TypedQuery<Announcement> announcementTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<Announcement> allAnnouncementList = announcementTypedQuery.getResultList();
		return allAnnouncementList;
	}

	@Override
	public Announcement findById(Long announcementId) {
		return super.findById(Announcement.class, announcementId);
	}

	@Override
	public void update(Announcement announcement) {
		super.update(announcement);

	}

	@Override
	public void delete(Announcement announcement) {
		super.delete(announcement);

	}

	@Override
	public void updatePayslipEndate(Long companyId) {

		String queryString = "UPDATE Announcement announcement set  announcement.removeDateTime = :removeDateTime where  announcement.company.companyId = :companyId and announcement.removeDateTime is null and announcement.title = :title";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("removeDateTime", DateUtils.getCurrentTimestamp());
		q.setParameter("companyId", companyId);
		q.setParameter("title", PayAsiaConstants.PAY_SLIP_ENTITY_NAME);
		q.executeUpdate();

	}

	@Override
	public void deleteByCondition(String announcementNDesc, String title, Long companyId) {
		String queryString = "DELETE FROM Announcement announcement WHERE announcement.company.companyId = :company AND announcement.description = :description AND announcement.title =:title";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("title", title);
		q.setParameter("company", companyId);
		q.setParameter("description", announcementNDesc);

		q.executeUpdate();

	}

	@Override
	public List<HelpDesk> findByTopicAndRole(String keyName, String role, String moduleName) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<HelpDesk> criteriaQuery = cb.createQuery(HelpDesk.class);
		Root<HelpDesk> helpDeskRoot = criteriaQuery.from(HelpDesk.class);
		
		criteriaQuery.select(helpDeskRoot);
		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(helpDeskRoot.get("keyName"), keyName));
		restriction = cb.and(restriction, cb.equal(helpDeskRoot.get("role"), role));
		restriction = cb.and(restriction, cb.equal(helpDeskRoot.get("module"), moduleName));
		restriction = cb.and(restriction, cb.equal(helpDeskRoot.get("active"), 1));
		
		criteriaQuery.where(restriction);
		
		TypedQuery<HelpDesk> helpDeskTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		List<HelpDesk> requiredHelpList = helpDeskTypedQuery.getResultList();
		
		return requiredHelpList;
	}
	
	/*
	 	COMBINE GROUP AND COMPANY ANNOUNCEMENTS
	 */

	@Override
	public List<Announcement> findAnnouncementList(Long groupId, Long companyId, AnouncementForm announcementForm) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Announcement> criteriaQuery = cb.createQuery(Announcement.class);
		Root<Announcement> announcementRoot = criteriaQuery.from(Announcement.class);

		criteriaQuery.select(announcementRoot);

		Join<Announcement, CompanyGroup> announcementGroupJoin = announcementRoot.join(Announcement_.companyGroup);
		Join<Announcement, Company> announcementCompJoin = announcementRoot.join(Announcement_.company);
		
		Predicate restriction = cb.conjunction();

		restriction = filterFunction(cb, announcementRoot, restriction, announcementForm);
		
		if(groupId!=null) {
			restriction = cb.and(restriction, cb.equal(announcementGroupJoin.get(CompanyGroup_.groupId), groupId));
		}
		if(companyId!=null) {
			restriction = cb.and(restriction, cb.equal(announcementCompJoin.get(Company_.companyId), companyId));
		}
		
		criteriaQuery.where(restriction);
		criteriaQuery.orderBy(cb.desc(announcementRoot.get(Announcement_.announcementId)));
		
		TypedQuery<Announcement> announcementTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		List<Announcement> allAnnouncementList = announcementTypedQuery.getResultList();
		return allAnnouncementList;
	}
	
	// OLD
	
	@Override
	public List<Announcement> findByCompanyAndScope(Long companyId, String scope, AnouncementForm announcementForm) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Announcement> criteriaQuery = cb.createQuery(Announcement.class);
		Root<Announcement> announcementRoot = criteriaQuery.from(Announcement.class);
		criteriaQuery.select(announcementRoot);
		Join<Announcement, Company> announcementCompJoin = announcementRoot.join(Announcement_.company);
		Predicate restriction = cb.conjunction();
		
		restriction = filterFunction(cb, announcementRoot, restriction, announcementForm);
		
		restriction = cb.and(restriction, cb.equal(announcementCompJoin.get(Company_.companyId), companyId));
		restriction = cb.and(restriction,
				cb.equal(cb.upper(announcementRoot.get(Announcement_.scope)), scope.toUpperCase()));
		criteriaQuery.where(restriction);
		criteriaQuery.orderBy(cb.desc(announcementRoot.get(Announcement_.announcementId)));
		
		TypedQuery<Announcement> announcementTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		List<Announcement> allAnnouncementList = announcementTypedQuery.getResultList();
		return allAnnouncementList;
	}
	
	@Override
	public List<Announcement> findByCompanyGroupAndScope(Long groupId, String scope, AnouncementForm announcementForm) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Announcement> criteriaQuery = cb.createQuery(Announcement.class);
		Root<Announcement> announcementRoot = criteriaQuery.from(Announcement.class);
		criteriaQuery.select(announcementRoot);
		Join<Announcement, CompanyGroup> announcementGroupJoin = announcementRoot.join(Announcement_.companyGroup);
		Predicate restriction = cb.conjunction();
		restriction = filterFunction(cb, announcementRoot, restriction, announcementForm);
		
		restriction = cb.and(restriction, cb.equal(announcementGroupJoin.get(CompanyGroup_.groupId), groupId));
		restriction = cb.and(restriction,
				cb.equal(cb.upper(announcementRoot.get(Announcement_.scope)), scope.toUpperCase()));
		criteriaQuery.where(restriction);
		criteriaQuery.orderBy(cb.desc(announcementRoot.get(Announcement_.announcementId)));
		
		TypedQuery<Announcement> announcementTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		List<Announcement> allAnnouncementList = announcementTypedQuery.getResultList();
		return allAnnouncementList;
	}
	
}
