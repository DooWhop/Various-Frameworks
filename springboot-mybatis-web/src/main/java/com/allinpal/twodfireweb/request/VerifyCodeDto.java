package com.allinpal.twodfireweb.request;

public class VerifyCodeDto {
	
	private String userId;
	
	private String hpNo;
	
	private String reqType;
	
	private String verifyCode;
	
	private String verifyCodeId;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getHpNo() {
		return hpNo;
	}

	public void setHpNo(String hpNo) {
		this.hpNo = hpNo;
	}

	public String getReqType() {
		return reqType;
	}

	public void setReqType(String reqType) {
		this.reqType = reqType;
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

}
