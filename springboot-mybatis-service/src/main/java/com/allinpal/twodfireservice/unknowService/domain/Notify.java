package com.allinpal.twodfireservice.unknowService.domain;

import java.io.Serializable;

public class Notify implements Serializable{

	private static final long serialVersionUID = 9136067363555935693L;
	
	private String app_seq;//申请流水号(授信编号)
	
	private String overall_status;//放款总状态
	
	private String payment_time;//放款时间
	
	private String payment_no;//放款步骤序号
	
	private String payment_amount;//本步骤金额
	
	private String status;//本步骤放款结果
	
	private String response_message;//结果消息

	public String getApp_seq() {
		return app_seq;
	}

	public void setApp_seq(String app_seq) {
		this.app_seq = app_seq;
	}

	public String getOverall_status() {
		return overall_status;
	}

	public void setOverall_status(String overall_status) {
		this.overall_status = overall_status;
	}

	public String getPayment_time() {
		return payment_time;
	}

	public void setPayment_time(String payment_time) {
		this.payment_time = payment_time;
	}

	public String getPayment_no() {
		return payment_no;
	}

	public void setPayment_no(String payment_no) {
		this.payment_no = payment_no;
	}

	public String getPayment_amount() {
		return payment_amount;
	}

	public void setPayment_amount(String payment_amount) {
		this.payment_amount = payment_amount;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getResponse_message() {
		return response_message;
	}

	public void setResponse_message(String response_message) {
		this.response_message = response_message;
	}
}
