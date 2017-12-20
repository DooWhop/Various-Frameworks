package com.allinpal.twodfireservice.user.domain;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author admin
 * 用户信息
 */
public class User implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String userId;

    private String mobile;

    private String status;
    
    private BigDecimal tlbUserOid;

    private String merchantNo;

    private BigDecimal investorOid;

    private Long expireTime;

    private String createUid;

    private Long createTime;

    private String lastModifyUid;

    private Long lastModifyTime;
    
    private String type;
    
    private String verifycodeid;
    
    private String isExist;
    
    private String verifycode;
    
    private String prodCode;
    
    private String cardStatus;
    
    private String ocrState;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public BigDecimal getTlbUserOid() {
		return tlbUserOid;
	}

	public void setTlbUserOid(BigDecimal tlbUserOid) {
		this.tlbUserOid = tlbUserOid;
	}

	public String getMerchantNo() {
		return merchantNo;
	}

	public void setMerchantNo(String merchantNo) {
		this.merchantNo = merchantNo;
	}

	public BigDecimal getInvestorOid() {
		return investorOid;
	}

	public void setInvestorOid(BigDecimal investorOid) {
		this.investorOid = investorOid;
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getVerifycodeid() {
		return verifycodeid;
	}

	public void setVerifycodeid(String verifycodeid) {
		this.verifycodeid = verifycodeid;
	}

	public String getIsExist() {
		return isExist;
	}

	public void setIsExist(String isExist) {
		this.isExist = isExist;
	}

	public String getVerifycode() {
		return verifycode;
	}

	public void setVerifycode(String verifycode) {
		this.verifycode = verifycode;
	}

	public String getProdCode() {
		return prodCode;
	}

	public void setProdCode(String prodCode) {
		this.prodCode = prodCode;
	}

	public String getCardStatus() {
		return cardStatus;
	}

	public void setCardStatus(String cardStatus) {
		this.cardStatus = cardStatus;
	}

	public String getOcrState() {
		return ocrState;
	}

	public void setOcrState(String ocrState) {
		this.ocrState = ocrState;
	}
}
