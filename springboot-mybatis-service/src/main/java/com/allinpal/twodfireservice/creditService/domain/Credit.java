package com.allinpal.twodfireservice.creditService.domain;

import java.math.BigDecimal;

public class Credit {
    private String recordNo;
    private String orgChl;
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
    private String userId;
    private String dayLimit;
    private BigDecimal sixManageFee;
    private BigDecimal twelveManageFee;
    private String managerFee;
    private String statusUse;
    private String useTerm; 
    private String useAmt;
    private String registerDate;
    private String creditPoint;
    private String merName;
    private String contSignFlag;
    private Long contSignTime;
    private String useRecordNo;
    private BigDecimal investorOid;
    private String effectiveStatus;
    private String highestEdu;

    public String getRecordNo() {
        return recordNo;
    }

    public void setRecordNo(String recordNo) {
        this.recordNo = recordNo == null ? null : recordNo.trim();
    }

    public String getOrgChl() {
        return orgChl;
    }

    public void setOrgChl(String orgChl) {
        this.orgChl = orgChl == null ? null : orgChl.trim();
    }

    public String getAcctId() {
        return acctId;
    }

    public void setAcctId(String acctId) {
        this.acctId = acctId == null ? null : acctId.trim();
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode == null ? null : orgCode.trim();
    }

    public String getProdCode() {
        return prodCode;
    }

    public void setProdCode(String prodCode) {
        this.prodCode = prodCode == null ? null : prodCode.trim();
    }

    public String getMerchantNo() {
        return merchantNo;
    }

    public void setMerchantNo(String merchantNo) {
        this.merchantNo = merchantNo == null ? null : merchantNo.trim();
    }

    public String getLicenseNo() {
        return licenseNo;
    }

    public void setLicenseNo(String licenseNo) {
        this.licenseNo = licenseNo == null ? null : licenseNo.trim();
    }

    public String getLmtSerno() {
        return lmtSerno;
    }

    public void setLmtSerno(String lmtSerno) {
        this.lmtSerno = lmtSerno == null ? null : lmtSerno.trim();
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile == null ? null : mobile.trim();
    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName == null ? null : custName.trim();
    }

    public String getCertType() {
        return certType;
    }

    public void setCertType(String certType) {
        this.certType = certType == null ? null : certType.trim();
    }

    public String getCertNo() {
        return certNo;
    }

    public void setCertNo(String certNo) {
        this.certNo = certNo == null ? null : certNo.trim();
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName == null ? null : bankName.trim();
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode == null ? null : bankCode.trim();
    }

    public String getBankAcctNo() {
        return bankAcctNo;
    }

    public void setBankAcctNo(String bankAcctNo) {
        this.bankAcctNo = bankAcctNo == null ? null : bankAcctNo.trim();
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
        this.lmtStartDate = lmtStartDate == null ? null : lmtStartDate.trim();
    }

    public String getLmtEndDate() {
        return lmtEndDate;
    }

    public void setLmtEndDate(String lmtEndDate) {
        this.lmtEndDate = lmtEndDate == null ? null : lmtEndDate.trim();
    }

    public String getLiveCity() {
        return liveCity;
    }

    public void setLiveCity(String liveCity) {
        this.liveCity = liveCity == null ? null : liveCity.trim();
    }

    public String getLiveAddr() {
        return liveAddr;
    }

    public void setLiveAddr(String liveAddr) {
        this.liveAddr = liveAddr == null ? null : liveAddr.trim();
    }

    public String getLivest() {
        return livest;
    }

    public void setLivest(String livest) {
        this.livest = livest == null ? null : livest.trim();
    }

    public String getMchtCity() {
        return mchtCity;
    }

    public void setMchtCity(String mchtCity) {
        this.mchtCity = mchtCity == null ? null : mchtCity.trim();
    }

    public String getMchtAddr() {
        return mchtAddr;
    }

    public void setMchtAddr(String mchtAddr) {
        this.mchtAddr = mchtAddr == null ? null : mchtAddr.trim();
    }

    public String getMarriag() {
        return marriag;
    }

    public void setMarriag(String marriag) {
        this.marriag = marriag == null ? null : marriag.trim();
    }

    public String getSpouseNm() {
        return spouseNm;
    }

    public void setSpouseNm(String spouseNm) {
        this.spouseNm = spouseNm == null ? null : spouseNm.trim();
    }

    public String getSpouseIdtp() {
        return spouseIdtp;
    }

    public void setSpouseIdtp(String spouseIdtp) {
        this.spouseIdtp = spouseIdtp == null ? null : spouseIdtp.trim();
    }

    public String getSpouseIdno() {
        return spouseIdno;
    }

    public void setSpouseIdno(String spouseIdno) {
        this.spouseIdno = spouseIdno == null ? null : spouseIdno.trim();
    }

    public String getEmpPhone() {
        return empPhone;
    }

    public void setEmpPhone(String empPhone) {
        this.empPhone = empPhone == null ? null : empPhone.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public String getStatus2dfire() {
        return status2dfire;
    }

    public void setStatus2dfire(String status2dfire) {
        this.status2dfire = status2dfire == null ? null : status2dfire.trim();
    }

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo == null ? null : contractNo.trim();
    }

    public String getGrantNo() {
        return grantNo;
    }

    public void setGrantNo(String grantNo) {
        this.grantNo = grantNo == null ? null : grantNo.trim();
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
        this.failCode = failCode == null ? null : failCode.trim();
    }

    public String getDescrible() {
        return describle;
    }

    public void setDescrible(String describle) {
        this.describle = describle == null ? null : describle.trim();
    }

    public String getCreateUid() {
        return createUid;
    }

    public void setCreateUid(String createUid) {
        this.createUid = createUid == null ? null : createUid.trim();
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
        this.lastModifyUid = lastModifyUid == null ? null : lastModifyUid.trim();
    }

    public Long getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(Long lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getDayLimit() {
		return dayLimit;
	}

	public void setDayLimit(String dayLimit) {
		this.dayLimit = dayLimit;
	}

	public BigDecimal getSixManageFee() {
		return sixManageFee;
	}

	public void setSixManageFee(BigDecimal sixManageFee) {
		this.sixManageFee = sixManageFee;
	}

	public BigDecimal getTwelveManageFee() {
		return twelveManageFee;
	}

	public void setTwelveManageFee(BigDecimal twelveManageFee) {
		this.twelveManageFee = twelveManageFee;
	}

	public String getManagerFee() {
		return managerFee;
	}

	public void setManagerFee(String managerFee) {
		this.managerFee = managerFee;
	}

	public String getStatusUse() {
		return statusUse;
	}

	public void setStatusUse(String statusUse) {
		this.statusUse = statusUse;
	}

	public String getUseTerm() {
		return useTerm;
	}

	public void setUseTerm(String useTerm) {
		this.useTerm = useTerm;
	}

	public String getUseAmt() {
		return useAmt;
	}

	public void setUseAmt(String useAmt) {
		this.useAmt = useAmt;
	}

	public String getRegisterDate() {
		return registerDate;
	}

	public void setRegisterDate(String registerDate) {
		this.registerDate = registerDate;
	}

	public String getCreditPoint() {
		return creditPoint;
	}

	public void setCreditPoint(String creditPoint) {
		this.creditPoint = creditPoint;
	}

	public String getMerName() {
		return merName;
	}

	public void setMerName(String merName) {
		this.merName = merName;
	}

	public String getContSignFlag() {
		return contSignFlag;
	}

	public void setContSignFlag(String contSignFlag) {
		this.contSignFlag = contSignFlag;
	}

	public Long getContSignTime() {
		return contSignTime;
	}

	public void setContSignTime(Long contSignTime) {
		this.contSignTime = contSignTime;
	}

	public String getUseRecordNo() {
		return useRecordNo;
	}

	public void setUseRecordNo(String useRecordNo) {
		this.useRecordNo = useRecordNo;
	}

	public BigDecimal getInvestorOid() {
		return investorOid;
	}

	public void setInvestorOid(BigDecimal investorOid) {
		this.investorOid = investorOid;
	}

	public String getEffectiveStatus() {
		return effectiveStatus;
	}

	public void setEffectiveStatus(String effectiveStatus) {
		this.effectiveStatus = effectiveStatus;
	}

	public String getHighestEdu() {
		return highestEdu;
	}

	public void setHighestEdu(String highestEdu) {
		this.highestEdu = highestEdu;
	}

}