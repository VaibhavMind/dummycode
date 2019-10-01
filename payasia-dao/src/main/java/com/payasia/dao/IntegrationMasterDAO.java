package com.payasia.dao;

import com.payasia.dao.bean.IntegrationMaster;

public interface IntegrationMasterDAO {

	IntegrationMaster findByCondition(long companyId);

}
