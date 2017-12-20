package com.allinpal.twodfireservice.notify.domain;

import java.io.Serializable;
import java.math.BigDecimal;

public class TouKongRepayPlan implements Serializable {

	private static final long serialVersionUID = 1L;

	private String recordNo;

	private String prodCode;

	private String useRecordNo;

	private String contractNo;

	private String startDate;

	private Integer tpnum;

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
		this.recordNo = recordNo;
	}

	public String getProdCode() {
		return prodCode;
	}

	public void setProdCode(String prodCode) {
		this.prodCode = prodCode;
	}

	public String getUseRecordNo() {
		return useRecordNo;
	}

	public void setUseRecordNo(String useRecordNo) {
		this.useRecordNo = useRecordNo;
	}

	public String getContractNo() {
		return contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public Integer getTpnum() {
		return tpnum;
	}

	public void setTpnum(Integer tpnum) {
		this.tpnum = tpnum;
	}

	public String getPayDate() {
		return payDate;
	}

	public void setPayDate(String payDate) {
		this.payDate = payDate;
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
