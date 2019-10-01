package com.payasia.dao;

import java.sql.Timestamp;
import java.util.List;

import com.payasia.dao.bean.KeyPayIntPayRun;

public interface KeyPayIntPayRunDAO {

	void save(KeyPayIntPayRun KeyPayIntPayRun);

	void delete(KeyPayIntPayRun KeyPayIntPayRun);

	KeyPayIntPayRun saveReturn(KeyPayIntPayRun keyPayIntPayRun);

	KeyPayIntPayRun findById(Long KeyPayIntPayRunId);

	void update(KeyPayIntPayRun KeyPayIntPayRun);

	List<KeyPayIntPayRun> findAllPayRun(Long companyId);

	List<KeyPayIntPayRun> findByPayRunParameterDate(Long companyId,
			Timestamp startCalDate, Timestamp endCalDate);

	KeyPayIntPayRun findByPayRunId(Long companyId, Long payRunId);

}
