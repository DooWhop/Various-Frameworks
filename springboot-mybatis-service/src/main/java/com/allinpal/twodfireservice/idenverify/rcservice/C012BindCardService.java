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
import com.allinpal.twodfireservice.idenverify.domain.C003C012BindCard;
import com.allinpal.twodfireservice.idenverify.util.InterfaceServiceUtil;

@Service
public class C012BindCardService {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Value("C012")
	private String C012_BIND_CARD_FACE;
	
	@Value("${tlb.route.url}")
	private String TLB_ROUTE_URL;
	
	@Autowired
	private RestTemplate restTemplate;
	
	public String bindCard(BankCard bankCard, String openId) throws Exception {
		logger.info("C012 bindCard param bankCard is {}, openId is {}", bankCard, openId);		
		C003C012BindCard bindCard = new C003C012BindCard();
		bindCard.setTimestamp(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
		bindCard.setInterfacecode(C012_BIND_CARD_FACE);	
		bindCard.setUsercode(openId);
		bindCard.setAcctno(bankCard.getAcctNo());
		bindCard.setAcctname(bankCard.getAcctName());
		bindCard.setBankidenno(bankCard.getBankIdenCode());
		bindCard.setIdenno(bankCard.getCertNo());
		bindCard.setDistrictcode("10100");//默认地区码
		bindCard.setVerifymode(bankCard.getVerifyMode());
		bindCard.setBankhpno(bankCard.getHpNo());
		Map<String, String> params = InterfaceServiceUtil.getParamLess(bindCard);
		MultiValueMap<String, String> C012Map = InterfaceServiceUtil.convertMap2MultiValueMap(params);
		logger.info("C012 postForObject param is {}", C012Map);	
        String result = restTemplate.postForObject(TLB_ROUTE_URL, C012Map, String.class);
        String res = InterfaceServiceUtil.getResponse(result);
        return res;
		
	}
}
