package com.payasia.dao;

import java.sql.Timestamp;
import java.util.List;

import com.payasia.dao.bean.LundinTimesheetDetail;

public interface LundinTimesheetDetailDAO {
	LundinTimesheetDetail findById(long id);

	void save(LundinTimesheetDetail timesheetDetail);

	List<LundinTimesheetDetail> findByTimesheetId(long id);

	void update(LundinTimesheetDetail timesheetDetail);

	LundinTimesheetDetail findIfExists(long tsheetId, Timestamp tdate,
			long afeId, long blockId);

	void delete(long id);

	List<LundinTimesheetDetail> findByBlockAndAfe(long blockId, long afeId,
			long timesheetId);
	
	public LundinTimesheetDetail findIfExists(long otTimesheetId,
			Timestamp otDate, long afeId, long blockId,Long companyId);
	
}
