package com.allinpal.twodfireservice.user.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.allinpal.twodfireservice.AppConfig;
import com.allinpal.twodfireservice.user.domain.ProdAcct;
import com.allinpal.twodfireservice.user.service.ProdAcctService;
import com.allinpal.twodfireservice.user.service.UserService;
import com.allinpal.twodfireservice.util.GenerateIdsUtil;

/**
 * @author admin
 * 贷款账户
 */
@RestController
@RequestMapping("prodAcctService")
public class ProdAcctController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private ProdAcctService prodAcctService;
	
	@Autowired
	public AppConfig appConfig;
	
	@RequestMapping(value="/query", method=RequestMethod.POST)
	@ResponseBody
	String query(ProdAcct prodAcct) {
		
		JSONObject result = new JSONObject(); 
		try {
			logger.info("ProdAcctController method query param is {}", prodAcct);
			prodAcct.setStatus("1");//生效
			List<ProdAcct> prodAcctList = prodAcctService.queryUserInfo(prodAcct);		
			if(prodAcctList != null && prodAcctList.size() > 0){
				result.put("code", "000000");
				result.put("prodAcctList", prodAcctList);
			}else{
				result.put("code", "000002");
				result.put("message", "query failed!");
			}
		} catch (Exception e) {
			logger.error("ProdAcctController method query has an exception {},",e);
			result.put("code", "000001");
			result.put("message", "system error!");
		}				
		return result.toJSONString();
	}
	
	
	@RequestMapping(value="/saveProdAcct", method=RequestMethod.POST)
	@ResponseBody
	String saveProdAcct(ProdAcct prodAcct) {
		
		JSONObject result = new JSONObject(); 
		try {
			logger.info("ProdAcctController method saveProdAcct param is {}", prodAcct);				
			String acctId = GenerateIdsUtil.generateId(appConfig.getAppIp());
			logger.info("ProdAcctController method register, prodAcct's acctId is {}",acctId);
			prodAcct.setAcctId(acctId);
			prodAcct.setStatus("1");//生效
			prodAcct.setCreateUid(prodAcct.getUserId());
			prodAcct.setCreateTime(System.currentTimeMillis());
			prodAcct.setLastModifyUid(prodAcct.getUserId());
			prodAcct.setLastModifyTime(System.currentTimeMillis());		
			int cnt = prodAcctService.saveProdAcct(prodAcct);
			
			if(cnt==1){
				result.put("code", "000000");
				result.put("cnt", cnt);
			}else{
				result.put("code", "000003");
				result.put("message", "save failed!");
			}
			logger.info("ProdAcctController method saveProdAcct result is {}",result);
			
		} catch (Exception e) {
			logger.error("ProdAcctController method saveProdAcct has an exception {},",e);
			result.put("code", "000001");
			result.put("message", "system error!");
		}
		return result.toJSONString();
	}
}
