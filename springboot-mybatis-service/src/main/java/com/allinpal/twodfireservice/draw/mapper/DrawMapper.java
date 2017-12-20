package com.allinpal.twodfireservice.draw.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.allinpal.twodfireservice.draw.domain.Charge;
import com.allinpal.twodfireservice.draw.domain.Draw;

@Mapper
public interface DrawMapper {
	Map<String, Object> getReqData(@Param("recordNo") String recordNo, @Param("timeLimit") String timeLimit);
	
	void saveAppInfo(Draw draw);
	
	List<Charge> queryChargeList(Charge charge);
	
	void updateCharge(Charge charge);
	
	void saveCharge(Charge charge);
	
	Map<String, String> getAcctNo(@Param("acctNo") String acctNo);
	
	void updateUse(Draw draw);
	
	Map getProtocolData(@Param("recordNo") String recordNo, @Param("applTerm") String applTerm);
	
	Draw queryUseInfo(Draw draw);
	
	Map queryUserInfo(@Param("recordNo") String recordNo);
}
