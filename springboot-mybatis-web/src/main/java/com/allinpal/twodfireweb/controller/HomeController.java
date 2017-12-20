package com.allinpal.twodfireweb.controller;  
  
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.allinpal.twodfireweb.request.CreditReq;
import com.allinpal.twodfireweb.util.ConstantsUtil;  

  
@Controller  
public class HomeController extends BaseController{
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Value("${creditService.query.url}")
	private String creSerUrl;
	
//	@RequestMapping("/error")  
//    public String error(Map<String,Object> model, HttpServletRequest request){ 
//		return "common/error";
//	}
	
    @RequestMapping("/home")  
    public String twodfireHome(Model model, HttpServletRequest request, HttpServletResponse response){
    	String cacheKey = getCacheKeyFromRequest(request);
    	if(StringUtils.isEmpty(cacheKey)){
    		setCacheKeyToResponse(response);
    	}
    	
    	String merchantNo = request.getParameter("merchantNo");
    	if(StringUtils.isEmpty(merchantNo)){
    		model.addAttribute("message", "商户编码为空!");
    		return "common/error";
    	}else{
    		model.addAttribute("merchantNo", merchantNo);
    	}
    	
    	String openId = getOpenId(request);
    	if(StringUtils.isEmpty(openId)){
    		return"home/2dfireHome";
    	}else{
			MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
	    	map.add("merchantNo", merchantNo);
			map.add("prodCode", ConstantsUtil.PRODCODE_2DFIRE);
	        String result = restTemplate.postForObject(creSerUrl, map, String.class);
	        JSONObject jObj = JSONObject.parseObject(result);
    		logger.info("request from {} result is {}", creSerUrl, jObj.toString());
    		if(ConstantsUtil.CODE_SUCC.equals(jObj.getString("code"))){
    			List<CreditReq> creditList = JSON.parseArray(jObj.getString("creditList"), CreditReq.class); 
    			boolean hasValidCredit = false;
    			//0全部 1申请未完成 2申请失效 3审批中 4审批失败 5审批通过 6额度生效 66额度到期 7额度失效 8审批作废 9额度暂停
        		List<String> validlist = Arrays.asList("3","5","6","9");
        		for(int i=0; i<creditList.size(); i++){
        			if(validlist.contains(creditList.get(i).getStatus())){
    			    	hasValidCredit = true;
    			    	break;
    			    }
        		}
    	    	if(hasValidCredit){
    	    		return"forward:/accountHome";
    	    	}else{
    	    		return"home/2dfireHome";
    	    	}
    		}else{
    			return "common/error";
    		}
    	}
    }
    
    
}