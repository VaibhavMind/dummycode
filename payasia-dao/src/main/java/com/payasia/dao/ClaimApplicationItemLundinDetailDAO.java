package com.payasia.dao;

import com.payasia.dao.bean.ClaimApplicationItemLundinDetail;

public interface ClaimApplicationItemLundinDetailDAO {
	ClaimApplicationItemLundinDetail findById(long id);

	void save(ClaimApplicationItemLundinDetail claimApplicationItemLundinDetail);

	void update(
			ClaimApplicationItemLundinDetail claimApplicationItemLundinDetail);

	void delete(long id);

	void deleteByCondition(long ClaimApplicationItemLundinDetailId);

}
