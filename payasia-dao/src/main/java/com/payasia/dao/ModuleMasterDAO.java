package com.payasia.dao;

import com.payasia.dao.bean.ModuleMaster;

public interface ModuleMasterDAO {

	void update(ModuleMaster moduleMaster);

	void delete(ModuleMaster moduleMaster);

	void save(ModuleMaster moduleMaster);

	ModuleMaster findByID(Long moduleMasterId);

	ModuleMaster findByName(String payasiaLeaveModuleName);


}
