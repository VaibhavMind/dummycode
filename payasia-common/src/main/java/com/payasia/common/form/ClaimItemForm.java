package com.payasia.common.form;

import java.io.Serializable;

import com.payasia.common.dto.ClaimItemDTO;

public class ClaimItemForm implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9069910301892477707L;
	private long itemId;
	private long categoryId;
	private String accountCode;
	private String description;
	private String name;
	private String code;
	private String visibility;
	private Long claimCategoryId;
	private String claimCategory;
	private String claimTemplateCount;

	private ClaimItemDTO claimItemDTO;

	public Long getClaimCategoryId() {
		return claimCategoryId;
	}
	
	public void setClaimCategoryId(Long claimCategoryId) {
		this.claimCategoryId = claimCategoryId;
	}

	public String getClaimTemplateCount() {
		return claimTemplateCount;
	}

	public void setClaimTemplateCount(String claimTemplateCount) {
		this.claimTemplateCount = claimTemplateCount;
	}

	public long getItemId() {
		return itemId;
	}

	public void setItemId(long itemId) {
		this.itemId = itemId;
	}

	public String getAccountCode() {
		return accountCode;
	}

	public void setAccountCode(String accountCode) {
		this.accountCode = accountCode;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getVisibility() {
		return visibility;
	}

	public void setVisibility(String visibility) {
		this.visibility = visibility;
	}

	public String getClaimCategory() {
		return claimCategory;
	}

	public void setClaimCategory(String claimCategory) {
		this.claimCategory = claimCategory;
	}

	public long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(long categoryId) {
		this.categoryId = categoryId;
	}

	public ClaimItemDTO getClaimItemDTO() {
		return claimItemDTO;
	}

	public void setClaimItemDTO(ClaimItemDTO claimItemDTO) {
		this.claimItemDTO = claimItemDTO;
	}





}
