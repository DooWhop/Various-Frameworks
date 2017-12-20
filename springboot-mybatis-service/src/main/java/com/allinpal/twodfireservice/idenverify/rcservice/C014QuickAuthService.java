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

import com.allinpal.twodfireservice.idenverify.domain.BankCard;
import com.allinpal.twodfireservice.idenverify.domain.C014QuickAuth;
import com.allinpal.twodfireservice.idenverify.util.InterfaceServiceUtil;

@Service
public class C014QuickAuthService {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Value("C014")
	private String QUICK_AUTH_SERVICE_FACE;
	
	@Value("${tlb.route.url}")
	private String TLB_ROUTE_URL;
	
	@Autowired
	private RestTemplate restTemplate;
	
	public String quickAuth(BankCard bankCard, String openId) throws Exception {
		logger.info("C014 quickAuth param bankCard is {}", bankCard);	
		logger.info("C014 quickAuth param openId is {}", openId);	
		C014QuickAuth quickAuth= new C014QuickAuth();
		quickAuth.setTimestamp(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
		quickAuth.setInterfacecode(QUICK_AUTH_SERVICE_FACE);	
		quickAuth.setUsercode(openId);
		quickAuth.setAcctno(bankCard.getAcctNo());
		quickAuth.setAcctname(bankCard.getAcctName());
		quickAuth.setBankidenno(bankCard.getBankIdenCode());
		quickAuth.setIdenno(bankCard.getCertNo());
		quickAuth.setVerifymode(bankCard.getVerifyMode());
		quickAuth.setBankhpno(bankCard.getHpNo());
		Map<String, String> params = InterfaceServiceUtil.getParamLess(quickAuth);
		MultiValueMap<String, String> C014Map = InterfaceServiceUtil.convertMap2MultiValueMap(params);
		logger.info("C014 postForObject param is {}", C014Map);	
        String result = restTemplate.postForObject(TLB_ROUTE_URL, C014Map, String.class);
        String res = InterfaceServiceUtil.getResponse(result);
        return res;
		
	}
}
