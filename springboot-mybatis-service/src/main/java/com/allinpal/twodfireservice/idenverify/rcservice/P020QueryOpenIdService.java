package com.allinpal.twodfireservice.idenverify.rcservice;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONObject;
import com.allinpal.twodfireservice.idenverify.domain.P020TLBUser;
import com.allinpal.twodfireservice.idenverify.util.InterfaceServiceUtil;

@Service
public class P020QueryOpenIdService {
	
	@Value("P020")
	private String INTERFACE_QUERY_OPENID;
	
	@Value("${p020.tlb.route.url}")
	private String TLB_ROUTE_URL;
	
	@Autowired
	private RestTemplate restTemplate;
	
	public P020TLBUser queryTlbUserInfo(String userId) throws Exception{
        
        P020TLBUser tlbUser = new P020TLBUser();
        tlbUser.setTimestamp(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
        tlbUser.setInterfacecode(INTERFACE_QUERY_OPENID);
        tlbUser.setUserid(userId);
		Map<String, String> params = InterfaceServiceUtil.getParamLess(tlbUser);
		MultiValueMap<String, String> P020Map = InterfaceServiceUtil.convertMap2MultiValueMap(params);
        String result = restTemplate.postForObject(TLB_ROUTE_URL, P020Map, String.class);
        String res = InterfaceServiceUtil.getResponse(result);                                   
        
        if(res == null || "".equals(res)){
			return null;
		}		
		Map<String, Object> ret = JSONObject.parseObject(res,Map.class);
		if(ret == null || !"true".equals(ret.get("isSuccess"))){
			return null;
		}
		Map<String, Object> retMap = (Map<String, Object>) ret.get("response");
		String interfaceRetCode = (String)retMap.get("code");	
		if(!"W-Y000000".equals(interfaceRetCode)){
			return null;
		}
        String openId = (String) retMap.get("openid");
        String idenno = (String) retMap.get("idenno");
        String ispassidenauth = (String) retMap.get("ispassidenauth");
		tlbUser.setOpenid(openId);
		tlbUser.setIdenno(idenno);
		tlbUser.setIspassidenauth(ispassidenauth);
		
        return tlbUser;                        	
	}
}
