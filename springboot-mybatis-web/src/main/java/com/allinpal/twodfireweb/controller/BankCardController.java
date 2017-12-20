package com.allinpal.twodfireweb.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.allinpal.twodfireweb.request.User;

@Controller
public class BankCardController {	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Value("${prodAcctService.query.url}")
	private String QUERY_CARD_URL;
	
	@RequestMapping("/getCardTest")
	public String getCardTest(Map<String, Object> model){
		model.put("merchantNo", "821000000000000");
		model.put("userId", "1001");
		return "myCard/myCardList";
	}
	
	@RequestMapping("/goBindCard")
	public String goBindCard(User user, Map<String, Object> model){
		//跳转绑卡页面
		logger.info("goBindCard param merchantNo:{},userId:{},mobile:{}",
				 user.getMerchantNo(), user.getUserId(),user.getMobile());
		model.put("userId", user.getUserId());
		model.put("merchantNo", user.getMerchantNo());
		model.put("mobile", user.getMobile());
		return "idenVerify/bindCard";
	}
	
	
	@RequestMapping(value="/ajaxGetCardList", produces="application/json; charset=utf-8")
	@ResponseBody
	public String ajaxGetCardList(String merchantNo, String status) {
		logger.info("ajaxGetCardList param merchantNo:{},status:{}",merchantNo,status);
		MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<String, String>();
		paramMap.add("merchantNo", merchantNo);
		paramMap.add("status", status);//当前贷款卡
        String result = restTemplate.postForObject(QUERY_CARD_URL, paramMap, String.class);
        logger.info("ajaxGetCardList param result:{}",result);
        return result;
	}

}
