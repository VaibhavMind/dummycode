package com.payasia.dao;

import com.payasia.common.dto.AddClaimDTO;
import com.payasia.dao.bean.ClaimApplicationItemAttachment;

public interface ClaimApplicationItemAttachmentDAO {

	ClaimApplicationItemAttachment saveReturn(ClaimApplicationItemAttachment claimApplicationItemAttachment);

	void save(ClaimApplicationItemAttachment claimApplicationItemAttachment);

	void update(ClaimApplicationItemAttachment claimApplicationItemAttachment);

	ClaimApplicationItemAttachment findByID(long claimApplicationItemAttachmentId);

	void delete(ClaimApplicationItemAttachment claimApplicationItemAttachment);

	ClaimApplicationItemAttachment findByClaimApplicationItemAttachmentID(AddClaimDTO addClaimDTO);

}
