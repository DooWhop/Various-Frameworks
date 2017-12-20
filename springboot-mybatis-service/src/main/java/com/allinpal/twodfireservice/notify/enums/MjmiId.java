package com.allinpal.twodfireservice.notify.enums;

public enum MjmiId {
	RATE_6M("6M","A00000"),
	RATE_12M("12M","A00001");
	
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

	MjmiId(String code,String desc) {
		this.code = code;
		this.desc = desc;
	}
	
	public static String getRetValue(String value) {
		if (value != null) {
			for (MjmiId nameEnum : values()) {
				if (nameEnum.getCode().equals(value)) {
					return nameEnum.desc;
				}
			}
		}
		return null;
	}
}
