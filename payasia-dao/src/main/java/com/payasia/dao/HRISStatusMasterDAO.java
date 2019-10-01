/**
 * @author vivekjain
 * 
 */
package com.payasia.dao;

import com.payasia.dao.bean.HRISStatusMaster;

public interface HRISStatusMasterDAO {

	HRISStatusMaster findById(Long hrisStatusMasterId);

	void save(HRISStatusMaster hrisStatusMaster);

	void delete(HRISStatusMaster hrisStatusMaster);

	void update(HRISStatusMaster hrisStatusMaster);

	HRISStatusMaster findByName(String hrisStatusSubmitted);

}
