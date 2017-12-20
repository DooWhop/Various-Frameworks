package com.allinpal.twodfireservice.idenverify.rcservice;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.allinpal.twodfireservice.idenverify.domain.BankCard;
import com.allinpal.twodfireservice.idenverify.domain.P001P017VerifyCode;
import com.allinpal.twodfireservice.idenverify.util.InterfaceServiceUtil;

@Service
public class P017CheckVerifyCodeService {
	
	@Value("P017")
	private String INTERFACE_CHECK_VERIFY_CODE;
	
	@Value("${tlb.route.url}")
	private String TLB_ROUTE_URL;
	
	@Autowired
	private RestTemplate restTemplate;
	
	public String checkVerifyCode(BankCard bankCard) throws Exception{

		P001P017VerifyCode verifyCode = new P001P017VerifyCode();
		verifyCode.setTimestamp(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
		verifyCode.setInterfacecode(INTERFACE_CHECK_VERIFY_CODE);
		verifyCode.setHpno(bankCard.getHpNo());
		verifyCode.setPhoneno(bankCard.getHpNo());
		verifyCode.setVerifycode(bankCard.getVerifyCode());
		verifyCode.setVerifycodeid(bankCard.getVerifyCodeId());
		verifyCode.setReqtype(bankCard.getReqType());		
		Map<String, String> params = InterfaceServiceUtil.getParamLess(verifyCode);
		MultiValueMap<String, String> C012Map = InterfaceServiceUtil.convertMap2MultiValueMap(params);
        String result = restTemplate.postForObject(TLB_ROUTE_URL, C012Map, String.class);
        String res = InterfaceServiceUtil.getResponse(result);
        return res;	
	}

}
