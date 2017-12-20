package com.allinpal.twodfireservice.draw.service;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.allinpal.twodfireservice.draw.domain.Charge;
import com.allinpal.twodfireservice.draw.domain.Draw;
import com.allinpal.twodfireservice.draw.mapper.DrawMapper;

@Service("drawService")
public class DrawService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	public DrawMapper drawMapper;
	
	public Map<String, Object> getReq(String recordNo, String timeLimit) {
		logger.info("======>DrawService");
		Map<String, Object> map = drawMapper.getReqData(recordNo, timeLimit);
		return map;
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void saveDrawData(Draw draw) {
		logger.info("========保存支用信息======");
		drawMapper.saveAppInfo(draw);
	}
	
	public List<Charge> queryChargeList(Charge charge) {
		List<Charge> list = drawMapper.queryChargeList(charge);
		return list;
	}
	
	public void updateCharge(Charge charge) {
		drawMapper.updateCharge(charge);
	}
	
	public void updateUse(Draw draw) {
		drawMapper.updateUse(draw);
	}
	
	public void saveCharge(Charge charge) {
		drawMapper.saveCharge(charge);
	}
	
	public String getAcctNo(String acctNo) {
		Map<String, String> map = drawMapper.getAcctNo(acctNo);
		return map.get("record_no");
	}
	
	public Map getProtocolData(String recordNo, String timeLimit) {
		return drawMapper.getProtocolData(recordNo, timeLimit);
	}
	
	public Draw queryUseInfo(Draw draw) {
		return drawMapper.queryUseInfo(draw);
	}
	
	public Map queryUserInfo(String recordNo) {
		return drawMapper.queryUserInfo(recordNo);
	}
}
