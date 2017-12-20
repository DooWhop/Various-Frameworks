package com.allinpal.twodfireweb.request;

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

    private BigDecimal investorOid;

    private Long expireTime;

    private String createUid;

    private Long createTime;

    private String lastModifyUid;

    private Long lastModifyTime;
    
    private String merchantNo;
    
    private String prodCode;

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

	public String getMerchantNo() {
		return merchantNo;
	}

	public void setMerchantNo(String merchantNo) {
		this.merchantNo = merchantNo;
	}

	public String getProdCode() {
		return prodCode;
	}

	public void setProdCode(String prodCode) {
		this.prodCode = prodCode;
	}
}
