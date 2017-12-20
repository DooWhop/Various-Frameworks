package com.allinpal.twodfireservice.draw.controller;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Test {
	public static void main(String[] args) {
		String amt = "300000.00";
		BigDecimal b1 = new BigDecimal(amt).multiply(new BigDecimal(100));
		System.out.print("b1:" + b1);
	}
}
