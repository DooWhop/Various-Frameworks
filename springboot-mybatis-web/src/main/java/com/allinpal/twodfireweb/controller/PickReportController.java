package com.allinpal.twodfireweb.controller;


import java.util.List;
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
import com.allinpal.twodfireweb.request.ProdAcct;

@Controller
public class PickReportController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Value("${userService.url}")
	private String userServiceUrl;
	
	@Autowired
	private RestTemplate restTemplate; 
	
	@RequestMapping(value="/accountHome",produces = "application/json; charset=utf-8")
	public String foo(Model model, HttpServletRequest request) {
		String merchantNo = request.getParameter("merchantNo");
		logger.info("merchantNo is :"+merchantNo);
		if(null==merchantNo||merchantNo.equals("")){
			logger.error("merchantNo is null");
			return "/common/error";
		}
		MultiValueMap<String, String>  map = new LinkedMultiValueMap<String, String>(); 		
    	map.add("merchantNo", merchantNo);
        JSONObject initJson = restTemplate.postForObject(userServiceUrl.concat("/queryUser"), map, JSONObject.class);
        logger.info("initJson is :"+initJson);
		if(initJson.get("code").toString().equals("000000")){
			List<Map<String,String>> list = (List) initJson.get("userList");
			if(null!=list&&list.size()>0){
				Map<String,String> prodAcct = list.get(0);
				model.addAttribute("merchantNo", merchantNo);
				model.addAttribute("legalName", prodAcct.get("legalName"));
				model.addAttribute("mobile", prodAcct.get("mobile"));
				model.addAttribute("mobileStr", prodAcct.get("mobile").substring(0, 3).concat("****").concat(prodAcct.get("mobile").substring(8)));
				model.addAttribute("bankIdenName", prodAcct.get("bankIdenName"));
				model.addAttribute("userId", prodAcct.get("userId"));
				if(prodAcct.get("acctNo")!=null&&!prodAcct.get("acctNo").toString().equals(""))
				{
					model.addAttribute("acctNo", prodAcct.get("acctNo").substring(prodAcct.get("acctNo").length()-4));
				}
				return "/home/accountHome";
			}else{
				return "/common/error";
			}
		}else{
			return "/common/error";
		}
	}
	
	@RequestMapping("/toMybankCard")
	public String toMybankCard(Model model, HttpServletRequest request) {
		String merchantNo = request.getParameter("merchantNo");
		String mobile = request.getParameter("mobile");
		String userId = request.getParameter("userId");
		String status = "1";
		model.addAttribute("merchantNo", merchantNo);
		model.addAttribute("mobile", mobile);
		model.addAttribute("userId", userId);
		model.addAttribute("status", status);
		logger.info("merchantNo is {}",merchantNo,"mobile is {}",mobile,"userId is {}",userId);
		return "/myCard/myCardList";
	}
}
