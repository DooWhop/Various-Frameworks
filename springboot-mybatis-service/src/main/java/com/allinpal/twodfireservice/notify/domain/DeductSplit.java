package com.allinpal.twodfireservice.notify.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class DeductSplit implements Serializable{

	private static final long serialVersionUID = 1L;

	private String recordNo;

    private String origRecordNo;

    private String prodCode;

    private String deductDate;

    private String curDate;

    private String busiType;
    
    private String serialNo;

    private BigDecimal transAmount;

    private BigDecimal splitAmount;

    private Integer splitCount;

    private Integer splitNum;

    private Long transDate;

    private String transType;

    private String bankCode;

    private String acctName;

    private String acctNo;

    private String acctCat;

    private String idNo;

    private String rtnCode;

    private String rtnMsg;

    private String procState;

    private String handRecordNo;

    private String tppReconFlag;

    private String remark;

    private String createUid;

    private Long createTime;

    private String lastModifyUid;

    private Long lastModifyTime;
    
    private Long oldLastModifyTime;
    
    private String newProcState;
    
    private String sortField;
    
    private String notProcState;
    
    private List<String> procStates;

	public String getRecordNo() {
		return recordNo;
	}

	public void setRecordNo(String recordNo) {
		this.recordNo = recordNo;
	}

	public String getOrigRecordNo() {
		return origRecordNo;
	}

	public void setOrigRecordNo(String origRecordNo) {
		this.origRecordNo = origRecordNo;
	}

	public String getProdCode() {
		return prodCode;
	}

	public void setProdCode(String prodCode) {
		this.prodCode = prodCode;
	}

	public String getDeductDate() {
		return deductDate;
	}

	public void setDeductDate(String deductDate) {
		this.deductDate = deductDate;
	}

	public String getCurDate() {
		return curDate;
	}

	public void setCurDate(String curDate) {
		this.curDate = curDate;
	}

	public String getBusiType() {
		return busiType;
	}

	public void setBusiType(String busiType) {
		this.busiType = busiType;
	}

	public String getSerialNo() {
		return serialNo;
	}

	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}

	public BigDecimal getTransAmount() {
		return transAmount;
	}

	public void setTransAmount(BigDecimal transAmount) {
		this.transAmount = transAmount;
	}

	public BigDecimal getSplitAmount() {
		return splitAmount;
	}

	public void setSplitAmount(BigDecimal splitAmount) {
		this.splitAmount = splitAmount;
	}

	public Integer getSplitCount() {
		return splitCount;
	}

	public void setSplitCount(Integer splitCount) {
		this.splitCount = splitCount;
	}

	public Integer getSplitNum() {
		return splitNum;
	}

	public void setSplitNum(Integer splitNum) {
		this.splitNum = splitNum;
	}

	public Long getTransDate() {
		return transDate;
	}

	public void setTransDate(Long transDate) {
		this.transDate = transDate;
	}

	public String getTransType() {
		return transType;
	}

	public void setTransType(String transType) {
		this.transType = transType;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getAcctName() {
		return acctName;
	}

	public void setAcctName(String acctName) {
		this.acctName = acctName;
	}

	public String getAcctNo() {
		return acctNo;
	}

	public void setAcctNo(String acctNo) {
		this.acctNo = acctNo;
	}

	public String getAcctCat() {
		return acctCat;
	}

	public void setAcctCat(String acctCat) {
		this.acctCat = acctCat;
	}

	public String getIdNo() {
		return idNo;
	}

	public void setIdNo(String idNo) {
		this.idNo = idNo;
	}

	public String getRtnCode() {
		return rtnCode;
	}

	public void setRtnCode(String rtnCode) {
		this.rtnCode = rtnCode;
	}

	public String getRtnMsg() {
		return rtnMsg;
	}

	public void setRtnMsg(String rtnMsg) {
		this.rtnMsg = rtnMsg;
	}

	public String getProcState() {
		return procState;
	}

	public void setProcState(String procState) {
		this.procState = procState;
	}

	public String getHandRecordNo() {
		return handRecordNo;
	}

	public void setHandRecordNo(String handRecordNo) {
		this.handRecordNo = handRecordNo;
	}

	public String getTppReconFlag() {
		return tppReconFlag;
	}

	public void setTppReconFlag(String tppReconFlag) {
		this.tppReconFlag = tppReconFlag;
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

	public Long getOldLastModifyTime() {
		return oldLastModifyTime;
	}

	public void setOldLastModifyTime(Long oldLastModifyTime) {
		this.oldLastModifyTime = oldLastModifyTime;
	}

	public String getNewProcState() {
		return newProcState;
	}

	public void setNewProcState(String newProcState) {
		this.newProcState = newProcState;
	}

	public String getSortField() {
		return sortField;
	}

	public void setSortField(String sortField) {
		this.sortField = sortField;
	}

	public String getNotProcState() {
		return notProcState;
	}

	public void setNotProcState(String notProcState) {
		this.notProcState = notProcState;
	}

	public List<String> getProcStates() {
		return procStates;
	}

	public void setProcStates(List<String> procStates) {
		this.procStates = procStates;
	}
}
