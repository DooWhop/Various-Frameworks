package com.allinpal.twodfireservice.contractService.service;


import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.allinpal.twodfireservice.contractService.domain.TLoanProtocol;
import com.allinpal.twodfireservice.contractService.mapper.TLoanProtocolMapper;


@Service("contractService")
public class ContractService {
	@Autowired
	public TLoanProtocolMapper contractMapper;
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public TLoanProtocol selectProtocolByRecordNo(String recordNo) {
		return contractMapper.selectProtocolByRecordNo(recordNo);
	}
	
	public List<TLoanProtocol> getTLoanProtocols(TLoanProtocol tLoanProtocol) {
		return contractMapper.getTLoanProtocols(tLoanProtocol);
	}
	
	public Map<String,Object> getCreditById(Map<String, Object> map) {
		return contractMapper.getCreditById(map);
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public int saveProtocol(TLoanProtocol tLoanProtocol) {
		int cnt = contractMapper.insert(tLoanProtocol);	
		return cnt;
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public int updateProtocol(TLoanProtocol tLoanProtocol) {
		int cnt = contractMapper.updateProtocol(tLoanProtocol);	
		return cnt;
	}
	
	public void saveContract() {
		logger.info("======保存合同信息到数据库=====");
	}
	
	public Map<String, Object> queryPerSign(String iden) {
		logger.info("======查询当前用户是否已申请电子签章=======" + iden);
		Map<String, Object> map = contractMapper.queryPerSign(iden);
		return map;
	}
	
	public void insertPerSign(String iden, String accountId, String sealData) {
		logger.info("=======保存个人签章｛｝====", iden);
		contractMapper.insertPerSign(accountId, iden, sealData);
	}
	
	public Map getBankCodeMapping(String bankCode) {
		return contractMapper.getBankCodeMapping(bankCode);
	}
	
	public Map<String,Object> getUserIdenFile(Map<String, Object> map) {
		return contractMapper.getUserIdenFile(map);
	}
}
