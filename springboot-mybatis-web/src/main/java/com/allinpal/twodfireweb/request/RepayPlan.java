package com.allinpal.twodfireweb.request;

import java.math.BigDecimal;

public class RepayPlan {
    private String recordNo;

    private String prodCode;

    private String useRecordNo;

    private String contractNo;

    private String tpnum;

    private String payDate;

    private BigDecimal payRate;

    private BigDecimal payTotamt;

    private BigDecimal payPrinamt;

    private BigDecimal payInteamt;

    private BigDecimal startBalamt;

    private BigDecimal endBalamt;

    private BigDecimal chargeAmt;

    private String remark;

    private String createUid;

    private Long createTime;

    private String lastModifyUid;

    private Long lastModifyTime;

    public String getRecordNo() {
        return recordNo;
    }

    public void setRecordNo(String recordNo) {
        this.recordNo = recordNo == null ? null : recordNo.trim();
    }

    public String getProdCode() {
        return prodCode;
    }

    public void setProdCode(String prodCode) {
        this.prodCode = prodCode == null ? null : prodCode.trim();
    }

    public String getUseRecordNo() {
        return useRecordNo;
    }

    public void setUseRecordNo(String useRecordNo) {
        this.useRecordNo = useRecordNo == null ? null : useRecordNo.trim();
    }

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo == null ? null : contractNo.trim();
    }

    public String getTpnum() {
        return tpnum;
    }

    public void setTpnum(String tpnum) {
        this.tpnum = tpnum == null ? null : tpnum.trim();
    }

    public String getPayDate() {
        return payDate;
    }

    public void setPayDate(String payDate) {
        this.payDate = payDate == null ? null : payDate.trim();
    }

    public BigDecimal getPayRate() {
        return payRate;
    }

    public void setPayRate(BigDecimal payRate) {
        this.payRate = payRate;
    }

    public BigDecimal getPayTotamt() {
        return payTotamt;
    }

    public void setPayTotamt(BigDecimal payTotamt) {
        this.payTotamt = payTotamt;
    }

    public BigDecimal getPayPrinamt() {
        return payPrinamt;
    }

    public void setPayPrinamt(BigDecimal payPrinamt) {
        this.payPrinamt = payPrinamt;
    }

    public BigDecimal getPayInteamt() {
        return payInteamt;
    }

    public void setPayInteamt(BigDecimal payInteamt) {
        this.payInteamt = payInteamt;
    }

    public BigDecimal getStartBalamt() {
        return startBalamt;
    }

    public void setStartBalamt(BigDecimal startBalamt) {
        this.startBalamt = startBalamt;
    }

    public BigDecimal getEndBalamt() {
        return endBalamt;
    }

    public void setEndBalamt(BigDecimal endBalamt) {
        this.endBalamt = endBalamt;
    }

    public BigDecimal getChargeAmt() {
        return chargeAmt;
    }

    public void setChargeAmt(BigDecimal chargeAmt) {
        this.chargeAmt = chargeAmt;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
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
}