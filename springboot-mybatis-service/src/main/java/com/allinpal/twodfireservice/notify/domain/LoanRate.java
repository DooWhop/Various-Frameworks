package com.allinpal.twodfireservice.notify.domain;

import java.io.Serializable;
import java.math.BigDecimal;

public class LoanRate implements Serializable {

	private static final long serialVersionUID = 1L;

	private String recordNo;

	private String prodCode;

	private String applTerm;

	private BigDecimal loanRate;

	private String status;

	private Long expiryTime;

	private String remark;

	private String createUid;

	private Long createTime;

	private String lastModifyUid;

	private Long lastModifyTime;

	public String getRecordNo() {
		return recordNo;
	}

	public void setRecordNo(String recordNo) {
		this.recordNo = recordNo;
	}

	public String getProdCode() {
		return prodCode;
	}

	public void setProdCode(String prodCode) {
		this.prodCode = prodCode;
	}

	public String getApplTerm() {
		return applTerm;
	}

	public void setApplTerm(String applTerm) {
		this.applTerm = applTerm;
	}

	public BigDecimal getLoanRate() {
		return loanRate;
	}

	public void setLoanRate(BigDecimal loanRate) {
		this.loanRate = loanRate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getExpiryTime() {
		return expiryTime;
	}

	public void setExpiryTime(Long expiryTime) {
		this.expiryTime = expiryTime;
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
}
