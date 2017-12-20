package com.allinpal.twodfireservice.contractService.mapper;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import com.allinpal.twodfireservice.contractService.domain.TLoanProtocol;


public interface TLoanProtocolMapper {
	int countByExample(TLoanProtocol tLoanProtocol);

    int deleteByExample(TLoanProtocol tLoanProtocol);

    int insert(TLoanProtocol record);
    
    int updateProtocol(TLoanProtocol record);

    TLoanProtocol selectProtocolByRecordNo(String recordNo);

    List<TLoanProtocol> getTLoanProtocols(TLoanProtocol tLoanProtocol);
    
    Map<String,Object> getCreditById(Map<String, Object> map);
    
    public Map queryPerSign(@Param("idenNo") String idenNo);
	
	public void insertPerSign(@Param("accountId") String accountId, @Param("idenNo") String idenNo, @Param("sealData") String sealData);
	
	Map getBankCodeMapping(@Param("bankCode") String bankCode);
	
	Map<String,Object> getUserIdenFile(Map<String, Object> map);
}
