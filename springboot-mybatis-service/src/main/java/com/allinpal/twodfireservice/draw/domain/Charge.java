package com.allinpal.twodfireservice.draw.domain;

import java.math.BigDecimal;

public class Charge {
	private String recordNo;
	
	private String useRecordNo;
	
	private String acctId;
	
	private String curDate;
	
	private BigDecimal baseAmount;
	
	private BigDecimal serviceFee;
	
	private BigDecimal serviceRate;
	
	private BigDecimal managerFee;
	
	private BigDecimal managerRate;
	
	private BigDecimal tolamount;
	
	private String deductCount;
	
	private String procState;
	
	private String reconFlag;
	
	private String reconTime;
	
	private String remark;
	
	private String createUid;
	
	private String createTime;
	
	private String lastModifyUid;
	
	private String lastModifyTime;

	public String getRecordNo() {
		return recordNo;
	}

	public void setRecordNo(String recordNo) {
		this.recordNo = recordNo;
	}

	public String getUseRecordNo() {
		return useRecordNo;
	}

	public void setUseRecordNo(String useRecordNo) {
		this.useRecordNo = useRecordNo;
	}

	public String getAcctId() {
		return acctId;
	}

	public void setAcctId(String acctId) {
		this.acctId = acctId;
	}

	public String getCurDate() {
		return curDate;
	}

	public void setCurDate(String curDate) {
		this.curDate = curDate;
	}

	public BigDecimal getBaseAmount() {
		return baseAmount;
	}

	public void setBaseAmount(BigDecimal baseAmount) {
		this.baseAmount = baseAmount;
	}

	public BigDecimal getServiceFee() {
		return serviceFee;
	}

	public void setServiceFee(BigDecimal serviceFee) {
		this.serviceFee = serviceFee;
	}

	public BigDecimal getServiceRate() {
		return serviceRate;
	}

	public void setServiceRate(BigDecimal serviceRate) {
		this.serviceRate = serviceRate;
	}

	public BigDecimal getManagerFee() {
		return managerFee;
	}

	public void setManagerFee(BigDecimal managerFee) {
		this.managerFee = managerFee;
	}

	public BigDecimal getManagerRate() {
		return managerRate;
	}

	public void setManagerRate(BigDecimal managerRate) {
		this.managerRate = managerRate;
	}

	public BigDecimal getTolamount() {
		return tolamount;
	}

	public void setTolamount(BigDecimal tolamount) {
		this.tolamount = tolamount;
	}

	public String getDeductCount() {
		return deductCount;
	}

	public void setDeductCount(String deductCount) {
		this.deductCount = deductCount;
	}

	public String getProcState() {
		return procState;
	}

	public void setProcState(String procState) {
		this.procState = procState;
	}

	public String getReconFlag() {
		return reconFlag;
	}

	public void setReconFlag(String reconFlag) {
		this.reconFlag = reconFlag;
	}

	public String getReconTime() {
		return reconTime;
	}

	public void setReconTime(String reconTime) {
		this.reconTime = reconTime;
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

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getLastModifyUid() {
		return lastModifyUid;
	}

	public void setLastModifyUid(String lastModifyUid) {
		this.lastModifyUid = lastModifyUid;
	}

	public String getLastModifyTime() {
		return lastModifyTime;
	}

	public void setLastModifyTime(String lastModifyTime) {
		this.lastModifyTime = lastModifyTime;
	}
}
