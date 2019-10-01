package com.payasia.common.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

public class LeaveTranReportDTO extends LeaveReportDataDTO implements Serializable {
	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String postedDate;
	private String approvedDate;
	private String fromDate;
	private String toDate;
 
	private String session1;
	private String session2;
	private HashMap<String, String> custFieldMap;
	String custField1 ;
	String custField2 ;
	String custField3 ;
	String custField4 ;
	String custField5 ;
	String custField6 ;
	String custField7 ;
	String custField8 ;
	String custField9 ;
	String custField10 ;
	private String leaveDate;
	private BigDecimal leaveDuration;
	private String leaveApplyType;
	private Long leaveApplicationId;
	private String preApproval;
	private String leaveExtension;
	private List<LeaveReportCustomDataDTO> reportCustomDataDTOs;
	
	private String customFieldHeaderName1 ; 
	private String customFieldValueName1 ;
	private String customFieldHeaderName2 ; 
	private String customFieldValueName2 ;
	private String customFieldHeaderName3 ; 
	private String customFieldValueName3 ;
	private String customFieldHeaderName4 ; 
	private String customFieldValueName4 ;
	private String customFieldHeaderName5 ; 
	private String customFieldValueName5 ;
	private String customFieldHeaderName6 ; 
	private String customFieldValueName6 ;
	private String leaveTransactionType;
	
	
	
	public String getPreApproval() {
		return preApproval;
	}
	public void setPreApproval(String preApproval) {
		this.preApproval = preApproval;
	}
	public String getLeaveExtension() {
		return leaveExtension;
	}
	public void setLeaveExtension(String leaveExtension) {
		this.leaveExtension = leaveExtension;
	}
	public String getLeaveDate() {
		return leaveDate;
	}
	public void setLeaveDate(String leaveDate) {
		this.leaveDate = leaveDate;
	}
	public BigDecimal getLeaveDuration() {
		return leaveDuration;
	}
	public void setLeaveDuration(BigDecimal leaveDuration) {
		this.leaveDuration = leaveDuration;
	}
	public String getLeaveApplyType() {
		return leaveApplyType;
	}
	public void setLeaveApplyType(String leaveApplyType) {
		this.leaveApplyType = leaveApplyType;
	}
	public String getCustField1() {
		return custField1;
	}
	public void setCustField1(String custField1) {
		this.custField1 = custField1;
	}
	public String getCustField2() {
		return custField2;
	}
	public void setCustField2(String custField2) {
		this.custField2 = custField2;
	}
	public String getCustField3() {
		return custField3;
	}
	public void setCustField3(String custField3) {
		this.custField3 = custField3;
	}
	public String getCustField4() {
		return custField4;
	}
	public void setCustField4(String custField4) {
		this.custField4 = custField4;
	}
	public String getCustField5() {
		return custField5;
	}
	public void setCustField5(String custField5) {
		this.custField5 = custField5;
	}
	public String getCustField6() {
		return custField6;
	}
	public void setCustField6(String custField6) {
		this.custField6 = custField6;
	}
	public String getCustField7() {
		return custField7;
	}
	public void setCustField7(String custField7) {
		this.custField7 = custField7;
	}
	public String getCustField8() {
		return custField8;
	}
	public void setCustField8(String custField8) {
		this.custField8 = custField8;
	}
	public String getCustField9() {
		return custField9;
	}
	public void setCustField9(String custField9) {
		this.custField9 = custField9;
	}
	public String getCustField10() {
		return custField10;
	}
	public void setCustField10(String custField10) {
		this.custField10 = custField10;
	}
	public String getPostedDate() {
		return postedDate;
	}
	public void setPostedDate(String postedDate) {
		this.postedDate = postedDate;
	}
	public String getFromDate() {
		return fromDate;
	}
	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}
	public String getToDate() {
		return toDate;
	}
	public void setToDate(String toDate) {
		this.toDate = toDate;
	}
 
	public String getApprovedDate() {
		return approvedDate;
	}
	public void setApprovedDate(String approvedDate) {
		this.approvedDate = approvedDate;
	}
	public HashMap<String, String> getCustFieldMap() {
		return custFieldMap;
	}
	public void setCustFieldMap(HashMap<String, String> custFieldMap) {
		this.custFieldMap = custFieldMap;
	}
	public Long getLeaveApplicationId() {
		return leaveApplicationId;
	}
	public void setLeaveApplicationId(Long leaveApplicationId) {
		this.leaveApplicationId = leaveApplicationId;
	}
	public List<LeaveReportCustomDataDTO> getReportCustomDataDTOs() {
		return reportCustomDataDTOs;
	}
	public void setReportCustomDataDTOs(
			List<LeaveReportCustomDataDTO> reportCustomDataDTOs) {
		this.reportCustomDataDTOs = reportCustomDataDTOs;
	}
	public String getCustomFieldHeaderName1() {
		return customFieldHeaderName1;
	}
	public void setCustomFieldHeaderName1(String customFieldHeaderName1) {
		this.customFieldHeaderName1 = customFieldHeaderName1;
	}
	public String getCustomFieldValueName1() {
		return customFieldValueName1;
	}
	public void setCustomFieldValueName1(String customFieldValueName1) {
		this.customFieldValueName1 = customFieldValueName1;
	}
	public String getCustomFieldHeaderName2() {
		return customFieldHeaderName2;
	}
	public void setCustomFieldHeaderName2(String customFieldHeaderName2) {
		this.customFieldHeaderName2 = customFieldHeaderName2;
	}
	public String getCustomFieldValueName2() {
		return customFieldValueName2;
	}
	public void setCustomFieldValueName2(String customFieldValueName2) {
		this.customFieldValueName2 = customFieldValueName2;
	}
	public String getCustomFieldHeaderName3() {
		return customFieldHeaderName3;
	}
	public void setCustomFieldHeaderName3(String customFieldHeaderName3) {
		this.customFieldHeaderName3 = customFieldHeaderName3;
	}
	public String getCustomFieldValueName3() {
		return customFieldValueName3;
	}
	public void setCustomFieldValueName3(String customFieldValueName3) {
		this.customFieldValueName3 = customFieldValueName3;
	}
	public String getCustomFieldHeaderName4() {
		return customFieldHeaderName4;
	}
	public void setCustomFieldHeaderName4(String customFieldHeaderName4) {
		this.customFieldHeaderName4 = customFieldHeaderName4;
	}
	public String getCustomFieldValueName4() {
		return customFieldValueName4;
	}
	public void setCustomFieldValueName4(String customFieldValueName4) {
		this.customFieldValueName4 = customFieldValueName4;
	}
	public String getCustomFieldHeaderName5() {
		return customFieldHeaderName5;
	}
	public void setCustomFieldHeaderName5(String customFieldHeaderName5) {
		this.customFieldHeaderName5 = customFieldHeaderName5;
	}
	public String getCustomFieldValueName5() {
		return customFieldValueName5;
	}
	public void setCustomFieldValueName5(String customFieldValueName5) {
		this.customFieldValueName5 = customFieldValueName5;
	}
	public String getCustomFieldHeaderName6() {
		return customFieldHeaderName6;
	}
	public void setCustomFieldHeaderName6(String customFieldHeaderName6) {
		this.customFieldHeaderName6 = customFieldHeaderName6;
	}
	public String getCustomFieldValueName6() {
		return customFieldValueName6;
	}
	public void setCustomFieldValueName6(String customFieldValueName6) {
		this.customFieldValueName6 = customFieldValueName6;
	}
	public String getSession1() {
		return session1;
	}
	public void setSession1(String session1) {
		this.session1 = session1;
	}
	public String getSession2() {
		return session2;
	}
	public void setSession2(String session2) {
		this.session2 = session2;
	}
	
    public String getLeaveTransactionType() {
		return leaveTransactionType;
	}
	public void setLeaveTransactionType(String leaveTransactionType) {
		this.leaveTransactionType = leaveTransactionType;
	}
	
}
