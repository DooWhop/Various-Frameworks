package com.allinpal.twodfireservice.notify.domain;

import java.io.Serializable;
import java.math.BigDecimal;

public class TouKongInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private String recordNo;

	private String orgChl;

	private String userId;

	private String acctId;

	private String orgCode;

	private String prodCode;

	private String merchantNo;

	private String licenseNo;

	private String lmtSerno;

	private String mobile;

	private String custName;

	private String certType;

	private String certNo;

	private String bankName;

	private String bankCode;

	private String bankAcctNo;

	private BigDecimal applAmt;

	private BigDecimal sixMonthAmt;

	private BigDecimal twelveMonthAmt;

	private String lmtStartDate;

	private String lmtEndDate;

	private String liveCity;

	private String liveAddr;

	private String livest;

	private String mchtCity;

	private String mchtAddr;

	private String marriag;

	private String spouseNm;

	private String spouseIdtp;

	private String spouseIdno;

	private String empPhone;

	private String status;

	private String status2dfire;

	private String contractNo;

	private String grantNo;

	private Long grantTime;

	private BigDecimal payRate;

	private String failCode;

	private String describle;

	private String createUid;

	private Long createTime;

	private String lastModifyUid;

	private Long lastModifyTime;

	private String workDate;

	private String clientNo;// 客户编号

	private BigDecimal appAmt;// 授信金额

	private Long openDate;// 开户日期

	private String applTerm;// 申请支用期限

	private String useRecordNo;// 支用主键

	private String billNo;// 投控生成的借据号
	
	private String loanDate;//渠道放款日期

	public String getRecordNo() {
		return recordNo;
	}

	public void setRecordNo(String recordNo) {
		this.recordNo = recordNo;
	}

	public String getOrgChl() {
		return orgChl;
	}

	public void setOrgChl(String orgChl) {
		this.orgChl = orgChl;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
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

	public String getMerchantNo() {
		return merchantNo;
	}

	public void setMerchantNo(String merchantNo) {
		this.merchantNo = merchantNo;
	}

	public String getLicenseNo() {
		return licenseNo;
	}

	public void setLicenseNo(String licenseNo) {
		this.licenseNo = licenseNo;
	}

	public String getLmtSerno() {
		return lmtSerno;
	}

	public void setLmtSerno(String lmtSerno) {
		this.lmtSerno = lmtSerno;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
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

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getBankAcctNo() {
		return bankAcctNo;
	}

	public void setBankAcctNo(String bankAcctNo) {
		this.bankAcctNo = bankAcctNo;
	}

	public BigDecimal getApplAmt() {
		return applAmt;
	}

	public void setApplAmt(BigDecimal applAmt) {
		this.applAmt = applAmt;
	}

	public BigDecimal getSixMonthAmt() {
		return sixMonthAmt;
	}

	public void setSixMonthAmt(BigDecimal sixMonthAmt) {
		this.sixMonthAmt = sixMonthAmt;
	}

	public BigDecimal getTwelveMonthAmt() {
		return twelveMonthAmt;
	}

	public void setTwelveMonthAmt(BigDecimal twelveMonthAmt) {
		this.twelveMonthAmt = twelveMonthAmt;
	}

	public String getLmtStartDate() {
		return lmtStartDate;
	}

	public void setLmtStartDate(String lmtStartDate) {
		this.lmtStartDate = lmtStartDate;
	}

	public String getLmtEndDate() {
		return lmtEndDate;
	}

	public void setLmtEndDate(String lmtEndDate) {
		this.lmtEndDate = lmtEndDate;
	}

	public String getLiveCity() {
		return liveCity;
	}

	public void setLiveCity(String liveCity) {
		this.liveCity = liveCity;
	}

	public String getLiveAddr() {
		return liveAddr;
	}

	public void setLiveAddr(String liveAddr) {
		this.liveAddr = liveAddr;
	}

	public String getLivest() {
		return livest;
	}

	public void setLivest(String livest) {
		this.livest = livest;
	}

	public String getMchtCity() {
		return mchtCity;
	}

	public void setMchtCity(String mchtCity) {
		this.mchtCity = mchtCity;
	}

	public String getMchtAddr() {
		return mchtAddr;
	}

	public void setMchtAddr(String mchtAddr) {
		this.mchtAddr = mchtAddr;
	}

	public String getMarriag() {
		return marriag;
	}

	public void setMarriag(String marriag) {
		this.marriag = marriag;
	}

	public String getSpouseNm() {
		return spouseNm;
	}

	public void setSpouseNm(String spouseNm) {
		this.spouseNm = spouseNm;
	}

	public String getSpouseIdtp() {
		return spouseIdtp;
	}

	public void setSpouseIdtp(String spouseIdtp) {
		this.spouseIdtp = spouseIdtp;
	}

	public String getSpouseIdno() {
		return spouseIdno;
	}

	public void setSpouseIdno(String spouseIdno) {
		this.spouseIdno = spouseIdno;
	}

	public String getEmpPhone() {
		return empPhone;
	}

	public void setEmpPhone(String empPhone) {
		this.empPhone = empPhone;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatus2dfire() {
		return status2dfire;
	}

	public void setStatus2dfire(String status2dfire) {
		this.status2dfire = status2dfire;
	}

	public String getContractNo() {
		return contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

	public String getGrantNo() {
		return grantNo;
	}

	public void setGrantNo(String grantNo) {
		this.grantNo = grantNo;
	}

	public Long getGrantTime() {
		return grantTime;
	}

	public void setGrantTime(Long grantTime) {
		this.grantTime = grantTime;
	}

	public BigDecimal getPayRate() {
		return payRate;
	}

	public void setPayRate(BigDecimal payRate) {
		this.payRate = payRate;
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

	public String getWorkDate() {
		return workDate;
	}

	public void setWorkDate(String workDate) {
		this.workDate = workDate;
	}

	public String getClientNo() {
		return clientNo;
	}

	public void setClientNo(String clientNo) {
		this.clientNo = clientNo;
	}

	public BigDecimal getAppAmt() {
		return appAmt;
	}

	public void setAppAmt(BigDecimal appAmt) {
		this.appAmt = appAmt;
	}

	public Long getOpenDate() {
		return openDate;
	}

	public void setOpenDate(Long openDate) {
		this.openDate = openDate;
	}

	public String getApplTerm() {
		return applTerm;
	}

	public void setApplTerm(String applTerm) {
		this.applTerm = applTerm;
	}

	public String getUseRecordNo() {
		return useRecordNo;
	}

	public void setUseRecordNo(String useRecordNo) {
		this.useRecordNo = useRecordNo;
	}

	public String getBillNo() {
		return billNo;
	}

	public void setBillNo(String billNo) {
		this.billNo = billNo;
	}

	public String getLoanDate() {
		return loanDate;
	}

	public void setLoanDate(String loanDate) {
		this.loanDate = loanDate;
	}
}
