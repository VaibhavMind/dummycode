package com.payasia.dao;

import com.payasia.dao.bean.OTStatusMaster;

public interface OTStatusMasterDAO {

	OTStatusMaster findByID(long otStatusMasterId);

	OTStatusMaster findByOTStatus(String otStatus);

}
