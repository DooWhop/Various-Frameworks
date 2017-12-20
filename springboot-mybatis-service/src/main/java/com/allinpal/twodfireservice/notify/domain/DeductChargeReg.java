package com.allinpal.twodfireservice.notify.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class DeductChargeReg implements Serializable{

	private static final long serialVersionUID = 1L;
	
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

    private Integer deductCount;

    private String procState;

    private String reconFlag;

    private Long reconTime;

    private String remark;

    private String createUid;

    private Long createTime;

    private String lastModifyUid;

    private Long lastModifyTime;
    
    private String bankIdenName;

    private String bankIdenCode;

    private String acctNo;
    
    private String acctName;
    
    private String certType;

    private String certNo;
    
    private String hpNo;
    
    private String cardStatus;
    
    private String newProcState;
    
    private String prodCode;
    
    private Long oldLastModifyTime;

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

	public Integer getDeductCount() {
		return deductCount;
	}

	public void setDeductCount(Integer deductCount) {
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

	public Long getReconTime() {
		return reconTime;
	}

	public void setReconTime(Long reconTime) {
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

	public String getHpNo() {
		return hpNo;
	}

	public void setHpNo(String hpNo) {
		this.hpNo = hpNo;
	}

	public String getCardStatus() {
		return cardStatus;
	}

	public void setCardStatus(String cardStatus) {
		this.cardStatus = cardStatus;
	}

	public String getNewProcState() {
		return newProcState;
	}

	public void setNewProcState(String newProcState) {
		this.newProcState = newProcState;
	}

	public String getProdCode() {
		return prodCode;
	}

	public void setProdCode(String prodCode) {
		this.prodCode = prodCode;
	}

	public Long getOldLastModifyTime() {
		return oldLastModifyTime;
	}

	public void setOldLastModifyTime(Long oldLastModifyTime) {
		this.oldLastModifyTime = oldLastModifyTime;
	}
	
}
