package com.payasia.dao.impl;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.payasia.dao.BaseDAO;
import com.payasia.dao.ClaimApplicationItemLundinDetailDAO;
import com.payasia.dao.bean.ClaimApplicationItemLundinDetail;

@Repository
public class ClaimApplicationItemLundinDetailDAOImpl extends BaseDAO implements
		ClaimApplicationItemLundinDetailDAO {

	@Override
	protected Object getBaseEntity() {
		ClaimApplicationItemLundinDetail obj = new ClaimApplicationItemLundinDetail();
		return obj;
	}

	@Override
	public ClaimApplicationItemLundinDetail findById(long id) {
		return super.findById(ClaimApplicationItemLundinDetail.class, id);
	}

	@Override
	public void save(
			ClaimApplicationItemLundinDetail claimApplicationItemLundinDetail) {
		super.save(claimApplicationItemLundinDetail);
	}

	@Override
	public void update(
			ClaimApplicationItemLundinDetail claimApplicationItemLundinDetail) {
		super.update(claimApplicationItemLundinDetail);

	}

	@Override
	public void delete(long id) {
		super.delete(super.findById(ClaimApplicationItemLundinDetail.class, id));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.EmployeeRoleMappingDAO#deleteByCondition(long)
	 */
	@Override
	public void deleteByCondition(long claimApplicationItemId) {

		String queryString = "DELETE FROM ClaimApplicationItemLundinDetail caild WHERE caild.claimApplicationItem.claimApplicationItemId = :claimApplicationItemId";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("claimApplicationItemId", claimApplicationItemId);
		q.executeUpdate();

	}

}
