/**
 * @author vivekjain
 *
 */
package com.payasia.dao;

import java.util.List;

import com.payasia.common.form.AnouncementForm;
import com.payasia.dao.bean.Announcement;
import com.payasia.dao.bean.HelpDesk;


/**
 * The Interface AnnouncementDAO.
 */

public interface AnnouncementDAO {

	void save(Announcement announcement);

	List<Announcement> findByCompanyGroupAndScope(Long groupId, String scope);

	List<Announcement> findByCompanyAndScope(Long companyId, String scope);
	
	Announcement findById(Long announcementId);

	void update(Announcement announcement);

	void delete(Announcement announcement);

	void updatePayslipEndate(Long companyId);

	void deleteByCondition(String announcementNDesc, String paySlipEntityName, Long companyId);

	List<HelpDesk> findByTopicAndRole(String screenName, String role, String moduleName);

	List<Announcement> findAnnouncementList(Long groupId, Long companyId, AnouncementForm announcementForm);

	List<Announcement> findByCompanyAndScope(Long companyId, String scope, AnouncementForm announcementForm);

	List<Announcement> findByCompanyGroupAndScope(Long groupId, String scope, AnouncementForm announcementForm);

}
