package com.allinpal.twodfireservice.repayService.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.allinpal.twodfireservice.repayService.domain.RepayPlan;
import com.allinpal.twodfireservice.repayService.service.RepayPlanService;


@RestController
@RequestMapping("repayService")
public class RepayServiceController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private RepayPlanService repayPlanService;
	
	@RequestMapping(value="/queryRepayPlan", method = RequestMethod.POST)
	String query(RepayPlan repayInfo) {
		logger.info("service method {}", "repayService/queryRepayPlan");
		logger.info("service param {}", JSON.toJSONString(repayInfo));
		Map<String,Object> response = new HashMap<String,Object>();
		try{
        	List<RepayPlan> repayList = repayPlanService.getRepayList(repayInfo);

			response.put("useRecordNo", repayInfo.getUseRecordNo());
			response.put("repayList", JSON.toJSONString(repayList));
			response.put("code", "000000");
			response.put("message", "success");
	    }catch (Exception e) {
			response.put("code", "000002");
			response.put("message", "error");
			logger.error("repayService/queryRepayPlan error : ",e);
		}
		return  JSON.toJSONString(response);
	}

}
