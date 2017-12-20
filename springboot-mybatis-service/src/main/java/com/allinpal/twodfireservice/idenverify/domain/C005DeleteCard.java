package com.allinpal.twodfireservice.idenverify.domain;

public class C005DeleteCard extends BaseModule {
	
	private String usercode;
	
	private String acctno;
	
	private String opntype;

	public String getUsercode() {
		return usercode;
	}

	public void setUsercode(String usercode) {
		this.usercode = usercode;
	}

	public String getAcctno() {
		return acctno;
	}

	public void setAcctno(String acctno) {
		this.acctno = acctno;
	}

	public String getOpntype() {
		return opntype;
	}

	public void setOpntype(String opntype) {
		this.opntype = opntype;
	}

}
