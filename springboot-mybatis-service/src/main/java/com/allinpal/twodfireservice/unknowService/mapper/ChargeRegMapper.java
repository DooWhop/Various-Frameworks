package com.allinpal.twodfireservice.unknowService.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.allinpal.twodfireservice.unknowService.domain.ChargeReg;
import com.allinpal.twodfireservice.unknowService.domain.Draw;

@Mapper
public interface ChargeRegMapper {

	List<ChargeReg> getchargeRegList(ChargeReg chargeReg);

	List<Draw> getDrawList(Draw draw);

}
