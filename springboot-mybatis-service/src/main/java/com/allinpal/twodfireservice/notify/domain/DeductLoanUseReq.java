package com.allinpal.twodfireservice.notify.domain;

import java.io.Serializable;
import java.math.BigDecimal;

public class DeductLoanUseReq implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String recordNo;

    private String creditRecordNo;

    private String acctId;

    private String orgCode;

    private String prodCode;

    private String lmtSerno;

    private String billNo;

    private String useDate;

    private String retuAcno;

    private String loanCustName;

    private String loanCerttypeName;

    private String loanCertNo;

    private String loanCustAddr;

    private String loanPhone;

    private BigDecimal appAmt;

    private BigDecimal baseAmount;

    private String applTerm;

    private String appStartDate;

    private String appEndDate;

    private BigDecimal loanYrate;

    private BigDecimal loanBalance;

    private BigDecimal serviceFee;

    private BigDecimal managerFee;

    private String loanUse;

    private String loanPayway;

    private BigDecimal loanOverupRate;

    private String useSignFlag;

    private Long useSignTime;

    private String withholdProtocol;

    private Long withholdProtocolTime;

    private String borrowProtocol;

    private Long borrowProtocolTime;

    private Long accessTime;

    private String status;
    
    private String loanDate;

    private String failCode;

    private String describle;

    private String createUid;

    private Long createTime;

    private String lastModifyUid;

    private Long lastModifyTime;
    
    private String newStatus;
    
    private Long oldLastModifyTime;

	public String getRecordNo() {
		return recordNo;
	}

	public void setRecordNo(String recordNo) {
		this.recordNo = recordNo;
	}

	public String getCreditRecordNo() {
		return creditRecordNo;
	}

	public void setCreditRecordNo(String creditRecordNo) {
		this.creditRecordNo = creditRecordNo;
	}

	public String getAcctId() {
		return acctId;
	}

	public void setAcctId(String acctId) {
		this.acctId = acctId;
	}

	public String getOrgCode() {
		return orgCode;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}

	public String getProdCode() {
		return prodCode;
	}

	public void setProdCode(String prodCode) {
		this.prodCode = prodCode;
	}

	public String getLmtSerno() {
		return lmtSerno;
	}

	public void setLmtSerno(String lmtSerno) {
		this.lmtSerno = lmtSerno;
	}

	public String getBillNo() {
		return billNo;
	}

	public void setBillNo(String billNo) {
		this.billNo = billNo;
	}

	public String getUseDate() {
		return useDate;
	}

	public void setUseDate(String useDate) {
		this.useDate = useDate;
	}

	public String getRetuAcno() {
		return retuAcno;
	}

	public void setRetuAcno(String retuAcno) {
		this.retuAcno = retuAcno;
	}

	public String getLoanCustName() {
		return loanCustName;
	}

	public void setLoanCustName(String loanCustName) {
		this.loanCustName = loanCustName;
	}

	public String getLoanCerttypeName() {
		return loanCerttypeName;
	}

	public void setLoanCerttypeName(String loanCerttypeName) {
		this.loanCerttypeName = loanCerttypeName;
	}

	public String getLoanCertNo() {
		return loanCertNo;
	}

	public void setLoanCertNo(String loanCertNo) {
		this.loanCertNo = loanCertNo;
	}

	public String getLoanCustAddr() {
		return loanCustAddr;
	}

	public void setLoanCustAddr(String loanCustAddr) {
		this.loanCustAddr = loanCustAddr;
	}

	public String getLoanPhone() {
		return loanPhone;
	}

	public void setLoanPhone(String loanPhone) {
		this.loanPhone = loanPhone;
	}

	public BigDecimal getAppAmt() {
		return appAmt;
	}

	public void setAppAmt(BigDecimal appAmt) {
		this.appAmt = appAmt;
	}

	public BigDecimal getBaseAmount() {
		return baseAmount;
	}

	public void setBaseAmount(BigDecimal baseAmount) {
		this.baseAmount = baseAmount;
	}

	public String getApplTerm() {
		return applTerm;
	}

	public void setApplTerm(String applTerm) {
		this.applTerm = applTerm;
	}

	public String getAppStartDate() {
		return appStartDate;
	}

	public void setAppStartDate(String appStartDate) {
		this.appStartDate = appStartDate;
	}

	public String getAppEndDate() {
		return appEndDate;
	}

	public void setAppEndDate(String appEndDate) {
		this.appEndDate = appEndDate;
	}

	public BigDecimal getLoanYrate() {
		return loanYrate;
	}

	public void setLoanYrate(BigDecimal loanYrate) {
		this.loanYrate = loanYrate;
	}

	public BigDecimal getLoanBalance() {
		return loanBalance;
	}

	public void setLoanBalance(BigDecimal loanBalance) {
		this.loanBalance = loanBalance;
	}

	public BigDecimal getServiceFee() {
		return serviceFee;
	}

	public void setServiceFee(BigDecimal serviceFee) {
		this.serviceFee = serviceFee;
	}

	public BigDecimal getManagerFee() {
		return managerFee;
	}

	public void setManagerFee(BigDecimal managerFee) {
		this.managerFee = managerFee;
	}

	public String getLoanUse() {
		return loanUse;
	}

	public void setLoanUse(String loanUse) {
		this.loanUse = loanUse;
	}

	public String getLoanPayway() {
		return loanPayway;
	}

	public void setLoanPayway(String loanPayway) {
		this.loanPayway = loanPayway;
	}

	public BigDecimal getLoanOverupRate() {
		return loanOverupRate;
	}

	public void setLoanOverupRate(BigDecimal loanOverupRate) {
		this.loanOverupRate = loanOverupRate;
	}

	public String getUseSignFlag() {
		return useSignFlag;
	}

	public void setUseSignFlag(String useSignFlag) {
		this.useSignFlag = useSignFlag;
	}

	public Long getUseSignTime() {
		return useSignTime;
	}

	public void setUseSignTime(Long useSignTime) {
		this.useSignTime = useSignTime;
	}

	public String getWithholdProtocol() {
		return withholdProtocol;
	}

	public void setWithholdProtocol(String withholdProtocol) {
		this.withholdProtocol = withholdProtocol;
	}

	public Long getWithholdProtocolTime() {
		return withholdProtocolTime;
	}

	public void setWithholdProtocolTime(Long withholdProtocolTime) {
		this.withholdProtocolTime = withholdProtocolTime;
	}

	public String getBorrowProtocol() {
		return borrowProtocol;
	}

	public void setBorrowProtocol(String borrowProtocol) {
		this.borrowProtocol = borrowProtocol;
	}

	public Long getBorrowProtocolTime() {
		return borrowProtocolTime;
	}

	public void setBorrowProtocolTime(Long borrowProtocolTime) {
		this.borrowProtocolTime = borrowProtocolTime;
	}

	public Long getAccessTime() {
		return accessTime;
	}

	public void setAccessTime(Long accessTime) {
		this.accessTime = accessTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getLoanDate() {
		return loanDate;
	}

	public void setLoanDate(String loanDate) {
		this.loanDate = loanDate;
	}

	public String getFailCode() {
		return failCode;
	}

	public void setFailCode(String failCode) {
		this.failCode = failCode;
	}

	public String getDescrible() {
		return describle;
	}

	public void setDescrible(String describle) {
		this.describle = describle;
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

	public String getNewStatus() {
		return newStatus;
	}

	public void setNewStatus(String newStatus) {
		this.newStatus = newStatus;
	}

	public Long getOldLastModifyTime() {
		return oldLastModifyTime;
	}

	public void setOldLastModifyTime(Long oldLastModifyTime) {
		this.oldLastModifyTime = oldLastModifyTime;
	}
}
