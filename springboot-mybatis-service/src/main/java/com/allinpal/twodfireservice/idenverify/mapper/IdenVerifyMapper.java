package com.allinpal.twodfireservice.idenverify.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.allinpal.twodfireservice.idenverify.domain.BankCard;
import com.allinpal.twodfireservice.idenverify.domain.IdenFile;


@Mapper
public interface IdenVerifyMapper {
	
	int saveBankCard(BankCard bankCard);
	
	int saveIdenFile(IdenFile idenFile);
	
	int updateIdenFile(IdenFile idenFile);
	
	String getUserIdByPhone(String phone);
	
	List<Map<String, Object>> getSupportBanks(String prodCode);
	
	String getBusiCerNoByMerchantNo(String merchantNo);
	
	int queryBankCard(BankCard bankCard);
	
	int queryIdenFile(IdenFile idenFile); 
    
	String getTlbOidByUserId(String userId);
}
