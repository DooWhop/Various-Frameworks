package com.allinpal.twodfireservice.idenverify.domain;

public class P001P017VerifyCode extends BaseModule{
	
	private String usercode;
    private String hpno;	//手机号码  服务定义不规范
    private String phoneno;	//手机号码
    private String verifycode;//验证码
    private String verifycodeid;//验证码标识
    private String reqtype; //验证码类型
    
	public String getUsercode() {
		return usercode;
	}
	public void setUsercode(String usercode) {
		this.usercode = usercode;
	}
	public String getHpno() {
		return hpno;
	}
	public void setHpno(String hpno) {
		this.hpno = hpno;
	}
	public String getPhoneno() {
		return phoneno;
	}
	public void setPhoneno(String phoneno) {
		this.phoneno = phoneno;
	}
	public String getVerifycode() {
		return verifycode;
	}
	public void setVerifycode(String verifycode) {
		this.verifycode = verifycode;
	}
	public String getVerifycodeid() {
		return verifycodeid;
	}
	public void setVerifycodeid(String verifycodeid) {
		this.verifycodeid = verifycodeid;
	}
	public String getReqtype() {
		return reqtype;
	}
	public void setReqtype(String reqtype) {
		this.reqtype = reqtype;
	}

}
