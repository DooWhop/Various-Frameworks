package com.allinpal.twodfireservice.creditService.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.allinpal.twodfireservice.creditService.domain.Credit;
import com.allinpal.twodfireservice.creditService.domain.ProdAcct;

@Mapper
public interface CreditMapper {

	ProdAcct getAcctByMerchantNo(ProdAcct prodAcct);

	int saveLoanCreditReq(Credit credit);

	List<Map<String, Object>> getUserCard(Map<String, String> request);

	List<Map<String, Object>> getLicenseNo(Map<String, String> request);

	List<Credit> getCreditList(Credit creditInfo);

	int updateLoanCreditReq(Credit creditInfo);

	List<Credit> getCreditDetail(Credit creditInfo);

	List<Map<String, Object>> getUserById(Map<String, String> request);

	List<Credit> getCreditListNotSign(Credit creditQuery);

}
