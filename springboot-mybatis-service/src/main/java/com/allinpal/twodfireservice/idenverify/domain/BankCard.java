package com.allinpal.twodfireservice.idenverify.domain;

public class BankCard {
	
	private String recordNo;
	
	private String userId;
	
	private String acctName;
	
	private String certType;
	
	private String certNo;
	
	private String bankIdenName;
	
	private String bankIdenCode;
	
	private String acctNo;
	
	private String hpNo;
	
	private String verifyMode;
	
	private String verifyCode;
	
	private String verifyCodeId;
	
	private String reqType;
	
	private String status;
	
	private String remark;
	
	private String createUid;
	
	private long createTime;
	
	private String lastModifyUid;
	
	private long lastModifyTime;

	public String getRecordNo() {
		return recordNo;
	}

	public void setRecordNo(String recordNo) {
		this.recordNo = recordNo;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getAcctName() {
		return acctName;
	}

	public void setAcctName(String acctName) {
		this.acctName = acctName;
	}

	public String getCertType() {
		return certType;
	}

	public void setCertType(String certType) {
		this.certType = certType;
	}

	public String getCertNo() {
		return certNo;
	}

	public void setCertNo(String certNo) {
		this.certNo = certNo;
	}

	public String getBankIdenName() {
		return bankIdenName;
	}

	public void setBankIdenName(String bankIdenName) {
		this.bankIdenName = bankIdenName;
	}

	public String getBankIdenCode() {
		return bankIdenCode;
	}

	public void setBankIdenCode(String bankIdenCode) {
		this.bankIdenCode = bankIdenCode;
	}

	public String getAcctNo() {
		return acctNo;
	}

	public void setAcctNo(String acctNo) {
		this.acctNo = acctNo;
	}

	public String getHpNo() {
		return hpNo;
	}

	public void setHpNo(String hpNo) {
		this.hpNo = hpNo;
	}

	public String getVerifyMode() {
		return verifyMode;
	}

	public void setVerifyMode(String verifyMode) {
		this.verifyMode = verifyMode;
	}

	public String getVerifyCode() {
		return verifyCode;
	}

	public void setVerifyCode(String verifyCode) {
		this.verifyCode = verifyCode;
	}

	public String getVerifyCodeId() {
		return verifyCodeId;
	}

	public void setVerifyCodeId(String verifyCodeId) {
		this.verifyCodeId = verifyCodeId;
	}

	public String getReqType() {
		return reqType;
	}

	public void setReqType(String reqType) {
		this.reqType = reqType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getCreateUid() {
		return createUid;
	}

	public void setCreateUid(String createUid) {
		this.createUid = createUid;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public String getLastModifyUid() {
		return lastModifyUid;
	}

	public void setLastModifyUid(String lastModifyUid) {
		this.lastModifyUid = lastModifyUid;
	}

	public long getLastModifyTime() {
		return lastModifyTime;
	}

	public void setLastModifyTime(long lastModifyTime) {
		this.lastModifyTime = lastModifyTime;
	}
	
	@Override
	public String toString() {
		return "BankCard [recordNo=" + recordNo + ", userID=" + userId
				+ ", acctName=" + acctName + ", certType=" + certType
				+ ", certNo=" + certNo + ", bankIdenName=" + bankIdenName
				+ ", bankIdenCode=" + bankIdenCode + ", acctNo=" + acctNo
				+ ", hpNo=" + hpNo + ", verifyMode=" + verifyMode
				+ ", verifyCode=" + verifyCode + ", verifyCodeId=" + verifyCodeId 
				+ ", reqType=" + reqType + ", status=" + status
				+ ", remark=" + remark + ", createUid=" + createUid
				+ ", createTime=" + createTime + ", lastModifyUid="
				+ lastModifyUid + ", lastModifyTime=" + lastModifyTime + "]";
	}

	

}
