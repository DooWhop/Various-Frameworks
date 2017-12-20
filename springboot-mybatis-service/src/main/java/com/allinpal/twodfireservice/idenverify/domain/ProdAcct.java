package com.allinpal.twodfireservice.idenverify.domain;

import java.io.Serializable;

public class ProdAcct implements Serializable {

	private static final long serialVersionUID = 1L;

	private String acctId;

	private String userId;

	private String userCardId;

	private String prodCode;

	private String legalName;

	private String mobile;

	private String certNo;

	private String merchantNo;

	private String busiCertNo;

	private String status;

	private Long expireTime;

	private String createUid;

	private Long createTime;

	private String lastModifyUid;

	private Long lastModifyTime;
	
	private String bankIdenName;

    private String bankIdenCode;

    private String acctNo;

	public String getAcctId() {
		return acctId;
	}

	public void setAcctId(String acctId) {
		this.acctId = acctId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserCardId() {
		return userCardId;
	}

	public void setUserCardId(String userCardId) {
		this.userCardId = userCardId;
	}

	public String getProdCode() {
		return prodCode;
	}

	public void setProdCode(String prodCode) {
		this.prodCode = prodCode;
	}

	public String getLegalName() {
		return legalName;
	}

	public void setLegalName(String legalName) {
		this.legalName = legalName;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getCertNo() {
		return certNo;
	}

	public void setCertNo(String certNo) {
		this.certNo = certNo;
	}

	public String getMerchantNo() {
		return merchantNo;
	}

	public void setMerchantNo(String merchantNo) {
		this.merchantNo = merchantNo;
	}

	public String getBusiCertNo() {
		return busiCertNo;
	}

	public void setBusiCertNo(String busiCertNo) {
		this.busiCertNo = busiCertNo;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(Long expireTime) {
		this.expireTime = expireTime;
	}

	public String getCreateUid() {
		return createUid;
	}

	public void setCreateUid(String createUid) {
		this.createUid = createUid;
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	public String getLastModifyUid() {
		return lastModifyUid;
	}

	public void setLastModifyUid(String lastModifyUid) {
		this.lastModifyUid = lastModifyUid;
	}

	public Long getLastModifyTime() {
		return lastModifyTime;
	}

	public void setLastModifyTime(Long lastModifyTime) {
		this.lastModifyTime = lastModifyTime;
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
}
