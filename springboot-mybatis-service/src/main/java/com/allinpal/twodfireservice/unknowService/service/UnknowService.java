package com.allinpal.twodfireservice.unknowService.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.allinpal.twodfireservice.unknowService.domain.ChargeReg;
import com.allinpal.twodfireservice.unknowService.domain.Draw;
import com.allinpal.twodfireservice.unknowService.mapper.ChargeRegMapper;

@Service("unknowService")
public class UnknowService {

	@Autowired
	private ChargeRegMapper chargeRegMapper;
	
	public List<ChargeReg> getchargeRegList(ChargeReg chargeReg) {
		return chargeRegMapper.getchargeRegList(chargeReg);
	}

	public List<Draw> getDrawList(Draw draw) {
		return chargeRegMapper.getDrawList(draw);
	}

}
