package com.allinpal.twodfireweb.request;

public class BankCardDto {
	
	private String userId;
		
	private String bankIdenName;
	
	private String bankIdenCode;
	
	private String hpNo;
	
	private String acctNo;
	
	private String certNo;
	
	private String acctName;
	
	private String verifyMode;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
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

	public String getHpNo() {
		return hpNo;
	}

	public void setHpNo(String hpNo) {
		this.hpNo = hpNo;
	}

	public String getAcctNo() {
		return acctNo;
	}

	public void setAcctNo(String acctNo) {
		this.acctNo = acctNo;
	}

	public String getCertNo() {
		return certNo;
	}

	public void setCertNo(String certNo) {
		this.certNo = certNo;
	}

	public String getAcctName() {
		return acctName;
	}

	public void setAcctName(String acctName) {
		this.acctName = acctName;
	}

	public String getVerifyMode() {
		return verifyMode;
	}

	public void setVerifyMode(String verifyMode) {
		this.verifyMode = verifyMode;
	}

}
