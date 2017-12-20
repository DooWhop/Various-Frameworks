package com.allinpal.twodfireservice.idenverify.rcservice;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.allinpal.twodfireservice.idenverify.domain.C010BankAcct;
import com.allinpal.twodfireservice.idenverify.util.InterfaceServiceUtil;

@Service
public class C010QueryBankAcctService {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Value("C010")
	private String BANK_ACCT_INTER_FACE;
	
	@Value("${tlb.route.url}")
	private String TLB_ROUTE_URL;
	
	@Autowired
	private RestTemplate restTemplate;
	
	public String queryBankAcct(String openId) throws Exception {
		logger.info("C010 queryBankAcct param openId is {}", openId);	
		C010BankAcct bankAcct = new C010BankAcct();
		bankAcct.setTimestamp(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
		bankAcct.setInterfacecode(BANK_ACCT_INTER_FACE);	
		bankAcct.setUsercode(openId);
		Map<String, String> params = InterfaceServiceUtil.getParamLess(bankAcct);
		MultiValueMap<String, String> C010Map = InterfaceServiceUtil.convertMap2MultiValueMap(params);
		logger.info("C010 postForObject param is {}", C010Map);	
        String result = restTemplate.postForObject(TLB_ROUTE_URL, C010Map, String.class);
        String res = InterfaceServiceUtil.getResponse(result);
        return res;
		
	}
}
