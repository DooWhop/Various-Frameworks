package com.allinpal.twodfireservice.idenverify.rcservice;

import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.allinpal.twodfireservice.idenverify.domain.MutexCheck;
import com.allinpal.twodfireservice.idenverify.util.InterfaceServiceUtil;

@Service
public class TwoDFireMutexCheckService {
		
	@Value("${access.mutexCheck.url}")
	private String ACCESS_MUTEXCHECK_URL;
	
	@Autowired
	private RestTemplate restTemplate;
	
	public String mutexCheck(String openId) throws Exception{
				
		MutexCheck mutexCheck = new MutexCheck();
		mutexCheck.setOpenId(openId);
		Map<String, String> params = BeanUtils.describe(mutexCheck);
		MultiValueMap<String, String> mutexMap = InterfaceServiceUtil.convertMap2MultiValueMap(params);
        String result = restTemplate.postForObject(ACCESS_MUTEXCHECK_URL, mutexMap, String.class);
        return result;	
	}

}
