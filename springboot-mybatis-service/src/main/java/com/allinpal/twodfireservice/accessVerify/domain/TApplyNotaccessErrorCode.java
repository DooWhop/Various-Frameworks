package com.allinpal.twodfireservice.accessVerify.domain;

public enum TApplyNotaccessErrorCode {
	
	ERRORCODE_TH00001("TH00001","法人年龄不准入"),
	ERRORCODE_TH00002("TH00002","企业成立时间不准入"),
	ERRORCODE_TH00003("TH00003","入网时间不准入"),
	ERRORCODE_TH00004("TH00004","经营流水连续性不准入"),
	ERRORCODE_TH00005("TH00005","月均流水金额不准入"),
	ERRORCODE_TH00006("TH00006","征信返回结果为空"),
	ERRORCODE_TH00007("TH00007","征信验证超过3次"),
	ERRORCODE_TH00008("TH00008","征信返回企业名称为空"),
	ERRORCODE_TH00009("TH00009","征信接口故障"),
	ERRORCODE_TH00010("TH00010","征信返回企业法人变更未满6个月"),
	ERRORCODE_TH00011("TH00011","征信不通过"),
	ERRORCODE_TH00012("TH00012","企业注册日期为空"),
	ERRORCODE_TH00013("TH00013","用户源信息未录入");
	
    
	private String code;
	private String desc;
	
	
	public String getCode() {
		return code;
	}


	public void setCode(String code) {
		this.code = code;
	}


	public String getDesc() {
		return desc;
	}


	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	TApplyNotaccessErrorCode(String code,String desc) {
		this.code = code;
		this.desc = desc;
	}
	
	public static String getRetValue(String value) {
		if (value != null) {
			for (TApplyNotaccessErrorCode nameEnum : values()) {
				if (nameEnum.getCode().equals(value)) {
					return nameEnum.desc;
				}
			}
		}
		return null;
	}
}
