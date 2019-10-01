package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

public class DiscussionBoardFormResponse extends PageResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4131221104992120612L;

	
	private List<DiscussionBoardForm> discussionBoardFormList;
	private DiscussionBoardForm discussionBoardForm;
	private List<EmployeeListForm> searchEmployeeList;

	public List<DiscussionBoardForm> getDiscussionBoardFormList() {
		return discussionBoardFormList;
	}

	public void setDiscussionBoardFormList(
			List<DiscussionBoardForm> discussionBoardFormList) {
		this.discussionBoardFormList = discussionBoardFormList;
	}

	public DiscussionBoardForm getDiscussionBoardForm() {
		return discussionBoardForm;
	}

	public void setDiscussionBoardForm(DiscussionBoardForm discussionBoardForm) {
		this.discussionBoardForm = discussionBoardForm;
	}

	public List<EmployeeListForm> getSearchEmployeeList() {
		return searchEmployeeList;
	}

	public void setSearchEmployeeList(List<EmployeeListForm> searchEmployeeList) {
		this.searchEmployeeList = searchEmployeeList;
	}
}
