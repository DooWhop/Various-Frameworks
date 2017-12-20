package com.allinpal.twodfireservice.accessVerify.domain;

import java.io.Serializable;

public class BasicInfo implements Serializable{
	private static final long serialVersionUID = 1L;
	//商户编号
	private String merchantNo;
	//商户名称
    private String corpName;  
    //营业执照号
    private String busiCertNo;
    //入网时间
    private String netTime;
    //近3个月流水信息
    private String seriInfo;
	public String getMerchantNo() {
		return merchantNo;
	}
	public void setMerchantNo(String merchantNo) {
		this.merchantNo = merchantNo;
	}
	public String getCorpName() {
		return corpName;
	}
	public void setCorpName(String corpName) {
		this.corpName = corpName;
	}
	public String getBusiCertNo() {
		return busiCertNo;
	}
	public void setBusiCertNo(String busiCertNo) {
		this.busiCertNo = busiCertNo;
	}
	public String getNetTime() {
		return netTime;
	}
	public void setNetTime(String netTime) {
		this.netTime = netTime;
	}
	public String getSeriInfo() {
		return seriInfo;
	}
	public void setSeriInfo(String seriInfo) {
		this.seriInfo = seriInfo;
	}

}
