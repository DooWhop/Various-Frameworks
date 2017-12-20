package com.allinpal.twodfireweb.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.allinpal.twodfireweb.request.CreditReq;
import com.allinpal.twodfireweb.request.RepayPlan;

@Controller
public class RepayPlanController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private RestTemplate restTemplate;
	
	@Value("${repayService.url}")
	private String repayServiceUrl;

	private static String RETCODE_000000="000000";

    @RequestMapping(value ="/queryRepayPlan", method = RequestMethod.POST)  
    public String apply(CreditReq credit,Model model){ 
    	try{ 
			model.addAttribute("merchantNo", credit.getMerchantNo());
	    	model.addAttribute("prodCode", credit.getProdCode());
	    	model.addAttribute("mobile", credit.getMobile());
	    	
			MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
			map.add("useRecordNo", credit.getUseRecordNo());
			map.add("prodCode", credit.getProdCode());
	        String result = restTemplate.postForObject(repayServiceUrl.concat("/queryRepayPlan"), map, String.class);
			logger.info("queryRepayPlan result {}", result);
	        JSONObject initJson = JSONObject.parseObject(result);
			if(RETCODE_000000.equals(initJson.getString("code"))){
	    		List<RepayPlan> repayList = JSON.parseArray(initJson.getString("repayList"), RepayPlan.class); 
	    		if(null != repayList && repayList.size() > 0){

	    			model.addAttribute("repayList", repayList);
	    			model.addAttribute("useRecordNo", initJson.getString("useRecordNo"));
	    		}
			}
    		
    	}catch(Exception e){
    		logger.info("queryRepayPlan exception : ",e);
	    	model.addAttribute("code", "000002");
	    	model.addAttribute("message", "很抱歉，系统繁忙，请稍后再试");
    		return"myCredit/fail";  
    	}
		return"repay/repayPlan";   
    }
	
}
