package com.payasia.dao;

import com.payasia.dao.bean.ClaimStatusMaster;

public interface ClaimStatusMasterDAO {

	ClaimStatusMaster findByCondition(String claimStatusName);

}
