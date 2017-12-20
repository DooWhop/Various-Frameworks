package com.allinpal.twodfireweb.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONObject;
import com.allinpal.twodfireweb.request.User;

@Controller
public class IdenVerifyController {	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private final String PROD_CODE_2FIRE = "9002000003";
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Value("${userService.url}")
	private String userServiceUrl;
	
	@SuppressWarnings("unchecked")
	@RequestMapping("/idenVerifyRedirect")
	public String idenVerifyRedirect(User user, Model model, HttpServletRequest request) {
	
		logger.info("idenVerifyRedirect is start! userId is {}, merchantNo is {}, mobile is {} ",user.getUserId(),user.getMerchantNo(),user.getMobile());
		MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<String, String>();
		String userId = user.getUserId();
		String mobile = user.getMobile();
		String merchantNo = user.getMerchantNo();
		paramMap.add("userId", userId);
		paramMap.add("merchantNo", merchantNo);
		paramMap.add("prodCode", PROD_CODE_2FIRE);
		String jsonStr = restTemplate.postForObject(userServiceUrl.concat("/redirect"), paramMap, String.class);
		logger.info("initRedirect' result is {}", jsonStr);
		
		Map<String,Object> result = (Map<String,Object>)JSONObject.parseObject(jsonStr,Map.class);
		String code = (String)result.get("code");
		if(!"000000".equals(code)){
			model.addAttribute("message", "系统异常，请稍后再试！");
			model.addAttribute("merchantNo",merchantNo);
			model.addAttribute("mobile", mobile);
			model.addAttribute("prodCode", PROD_CODE_2FIRE);
			return"common/error";
		}
		String pageCode = (String)result.get("pageCode");
		
		if("100000".equals(pageCode)){
			//跳转绑卡页面
			model.addAttribute("userId", userId);
			model.addAttribute("merchantNo", merchantNo);
			model.addAttribute("mobile", mobile);
			return "idenVerify/bindCard";
		}else if("400000".equals(pageCode)){
			//跳转身份证上传页面
			model.addAttribute("userId", userId);
			model.addAttribute("merchantNo", merchantNo);
			model.addAttribute("mobile", mobile);
			return "idenVerify/uploadIdentity";
		}else{
			model.addAttribute("message", "系统异常，请稍后再试！");
			model.addAttribute("merchantNo",merchantNo);
			model.addAttribute("mobile", mobile);
			model.addAttribute("prodCode", PROD_CODE_2FIRE);
			return"home/2dfireHome";
		}
	}
	

}
