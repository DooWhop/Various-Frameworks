package com.allinpal.twodfireservice.creditService.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.allinpal.twodfireservice.creditService.domain.Credit;
import com.allinpal.twodfireservice.creditService.domain.ProdAcct;
import com.allinpal.twodfireservice.creditService.mapper.CreditMapper;

@Service("creditService")
public class CreditService {
	
	@Autowired
	private CreditMapper creditMapper;

	public ProdAcct getAcctByMerchantNo(ProdAcct prodAcct) {
		return creditMapper.getAcctByMerchantNo(prodAcct);
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public int saveLoanCreditReq(Credit credit) {
		int cnt = creditMapper.saveLoanCreditReq(credit);
		if(cnt != 1){
			throw new RuntimeException("插入数据(cnt: "+cnt+")将回滚!");
		}
		return cnt;
	}

	public List<Map<String, Object>> getUserCard(Map<String, String> request) {
		return creditMapper.getUserCard(request);
	}

	public List<Map<String, Object>> getLicenseNo(Map<String, String> request) {
		return creditMapper.getLicenseNo(request);
	}

	public List<Credit> getCreditList(Credit creditInfo) {
		return creditMapper.getCreditList(creditInfo);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public int updateLoanCreditReq(Credit creditInfo) {
		int cnt = creditMapper.updateLoanCreditReq(creditInfo);
		if(cnt != 1){
			throw new RuntimeException("更新数据(cnt: "+cnt+")将回滚!");
		}
		return cnt;
	}

	public List<Credit> getCreditDetail(Credit creditInfo) {
		return creditMapper.getCreditDetail(creditInfo);
	}

	public List<Map<String, Object>> getUserById(Map<String, String> request) {
		return creditMapper.getUserById(request);
	}

	public List<Credit> getCreditListNotSign(Credit creditQuery) {
		return creditMapper.getCreditListNotSign(creditQuery);
	}

}
