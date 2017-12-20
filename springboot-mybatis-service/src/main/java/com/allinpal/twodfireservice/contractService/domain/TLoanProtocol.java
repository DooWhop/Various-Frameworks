package com.allinpal.twodfireservice.contractService.domain;

public class TLoanProtocol {
	private String recordNo;

    private String creditRecordNo;

    private String acctId;

    private String prodCode;

    private String protocolType;

    private String protocolNo;

    private String status;

    private String version;

    private String signDate;

    private String originalZipfileId;

    private String originalPdffileId;

    private String signedPdffileId;

    private String signedZipfileId;

    private String createUid;

    private Long createTime;

    private String lastModifyUid;

    private Long lastModifyTime;
    
    private String batchNo;
    
    private String applTerm;
    
    private String useRecordNo;

    public String getRecordNo() {
        return recordNo;
    }

    public void setRecordNo(String recordNo) {
        this.recordNo = recordNo == null ? null : recordNo.trim();
    }

    public String getCreditRecordNo() {
        return creditRecordNo;
    }

    public void setCreditRecordNo(String creditRecordNo) {
        this.creditRecordNo = creditRecordNo == null ? null : creditRecordNo.trim();
    }

    public String getAcctId() {
        return acctId;
    }

    public void setAcctId(String acctId) {
        this.acctId = acctId == null ? null : acctId.trim();
    }

    public String getProdCode() {
        return prodCode;
    }

    public void setProdCode(String prodCode) {
        this.prodCode = prodCode == null ? null : prodCode.trim();
    }

    public String getProtocolType() {
        return protocolType;
    }

    public void setProtocolType(String protocolType) {
        this.protocolType = protocolType == null ? null : protocolType.trim();
    }

    public String getProtocolNo() {
        return protocolNo;
    }

    public void setProtocolNo(String protocolNo) {
        this.protocolNo = protocolNo == null ? null : protocolNo.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version == null ? null : version.trim();
    }

    public String getSignDate() {
        return signDate;
    }

    public void setSignDate(String signDate) {
        this.signDate = signDate == null ? null : signDate.trim();
    }

    public String getOriginalZipfileId() {
        return originalZipfileId;
    }

    public void setOriginalZipfileId(String originalZipfileId) {
        this.originalZipfileId = originalZipfileId == null ? null : originalZipfileId.trim();
    }

    public String getOriginalPdffileId() {
        return originalPdffileId;
    }

    public void setOriginalPdffileId(String originalPdffileId) {
        this.originalPdffileId = originalPdffileId == null ? null : originalPdffileId.trim();
    }

    public String getSignedPdffileId() {
        return signedPdffileId;
    }

    public void setSignedPdffileId(String signedPdffileId) {
        this.signedPdffileId = signedPdffileId == null ? null : signedPdffileId.trim();
    }

    public String getSignedZipfileId() {
        return signedZipfileId;
    }

    public void setSignedZipfileId(String signedZipfileId) {
        this.signedZipfileId = signedZipfileId == null ? null : signedZipfileId.trim();
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

	public String getBatchNo() {
		return batchNo;
	}

	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
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
}
