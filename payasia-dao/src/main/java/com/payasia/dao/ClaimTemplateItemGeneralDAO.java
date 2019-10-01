package com.payasia.dao;

import com.payasia.dao.bean.ClaimTemplateItemGeneral;

public interface ClaimTemplateItemGeneralDAO {

	void update(ClaimTemplateItemGeneral claimTemplateItemGeneral);

	void save(ClaimTemplateItemGeneral claimTemplateItemGeneral);

	void delete(ClaimTemplateItemGeneral claimTemplateItemGeneral);

	ClaimTemplateItemGeneral findByID(long claimTemplateItemGeneralId);

	ClaimTemplateItemGeneral saveReturn(
			ClaimTemplateItemGeneral claimTemplateItemGeneral);

	ClaimTemplateItemGeneral findByItemId(String itemId);

}
