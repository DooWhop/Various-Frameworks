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
import com.allinpal.twodfireservice.idenverify.domain.P001P017VerifyCode;
import com.allinpal.twodfireservice.idenverify.service.IdenVerifyService;
import com.allinpal.twodfireservice.idenverify.util.InterfaceServiceUtil;

@Service
public class P001SendVerifyCodeService {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Value("P001")
	private String INTERFACE_SEND_VERIFY_CODE;
	
	@Value("${tlb.route.url}")
	private String TLB_ROUTE_URL;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private IdenVerifyService idenVerifyService;
	
	@Autowired
	private P020QueryOpenIdService p020QueryOpenIdService;
	
	public String sendVerifyCode(BankCard bankCard, String openId) throws Exception{		
		logger.info("P020 sendVerifyCode param bankCard is {}, openId is {}", bankCard, openId);
		P001P017VerifyCode verifyCode = new P001P017VerifyCode();
		verifyCode.setTimestamp(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
		verifyCode.setInterfacecode(INTERFACE_SEND_VERIFY_CODE);
		verifyCode.setUsercode(openId);
		verifyCode.setHpno(bankCard.getHpNo());
		verifyCode.setPhoneno(bankCard.getHpNo());
		verifyCode.setReqtype(bankCard.getReqType());		
		Map<String, String> params = InterfaceServiceUtil.getParamLess(verifyCode);
		MultiValueMap<String, String> C012Map = InterfaceServiceUtil.convertMap2MultiValueMap(params);
        String result = restTemplate.postForObject(TLB_ROUTE_URL, C012Map, String.class);
        String res = InterfaceServiceUtil.getResponse(result);
        return res;		
	}

}
