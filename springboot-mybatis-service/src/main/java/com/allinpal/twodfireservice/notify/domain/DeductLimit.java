package com.allinpal.twodfireservice.notify.domain;

import java.io.Serializable;
import java.math.BigDecimal;

public class DeductLimit implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String recordNo;

    private String deductChannel;

    private String busiType;

    private String bankCode;

    private String bankName;

    private BigDecimal onceDeductAmt;

    private Short dayDeductCount;

    private BigDecimal dayDeductAmt;

    private String status;

	public String getRecordNo() {
		return recordNo;
	}

	public void setRecordNo(String recordNo) {
		this.recordNo = recordNo;
	}

	public String getDeductChannel() {
		return deductChannel;
	}

	public void setDeductChannel(String deductChannel) {
		this.deductChannel = deductChannel;
	}

	public String getBusiType() {
		return busiType;
	}

	public void setBusiType(String busiType) {
		this.busiType = busiType;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public BigDecimal getOnceDeductAmt() {
		return onceDeductAmt;
	}

	public void setOnceDeductAmt(BigDecimal onceDeductAmt) {
		this.onceDeductAmt = onceDeductAmt;
	}

	public Short getDayDeductCount() {
		return dayDeductCount;
	}

	public void setDayDeductCount(Short dayDeductCount) {
		this.dayDeductCount = dayDeductCount;
	}

	public BigDecimal getDayDeductAmt() {
		return dayDeductAmt;
	}

	public void setDayDeductAmt(BigDecimal dayDeductAmt) {
		this.dayDeductAmt = dayDeductAmt;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
