package com.allinpal.twodfireservice.notify.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.allinpal.twodfireservice.notify.domain.Notify;
import com.allinpal.twodfireservice.notify.service.DeductService;
import com.allinpal.twodfireservice.notify.service.TouKongService;

/**
 * @author admin
 * 放款结果通知
 */
@RestController
@RequestMapping("notifyService")
public class NotifyController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private final String OVERALL_STATUS_SUCCESS = "01";
	
	private final String OVERALL_STATUS_FAILURE = "03";
	
	private final String OVERALL_STATUS_UNKNOW = "02";//02：处理中
	
	@Autowired
	private DeductService deductService;
	
	@Autowired
	private TouKongService toukongService;
	
	@Value("${twodfire.feededuct.org}")
	private String feededuct;
	
	@RequestMapping(value="/loanResult", method=RequestMethod.POST)
	@ResponseBody
	String loanResult(@RequestBody List<Notify> list) {
		logger.info("NotifyController method loanResult param is {}", JSON.toJSONString(list));
		
		JSONObject result = new JSONObject(); 
		//Notify notify = (Notify)JSONObject.parseObject(jsonStr, Notify.class);
		if(list == null || list.size() == 0){
			result.put("code", "000001");
			result.put("message", "JSON info is null or empty!");
			logger.info("JSON info is null or empty!");
			return result.toJSONString();
		}
		
		boolean ret = false;
		for(Notify notify : list){
			String app_seq = notify.getApp_seq();
			if(app_seq == null || "".equals(app_seq)){
				result.put("code", "000001");
				result.put("message", "app_seq info is null or empty!");
				logger.info("app_seq info is null or empty!");
				return result.toJSONString();
			}
			
			String overall_status = notify.getOverall_status();
			if(overall_status == null || "".equals(overall_status)){
				result.put("code", "000001");
				result.put("message", "overall_status info is null or empty!");
				logger.info("overall_status info is null or empty!");
				return result.toJSONString();
			}
			
			if(!OVERALL_STATUS_SUCCESS.equals(overall_status) && 
					!OVERALL_STATUS_FAILURE.equals(overall_status) &&
					!OVERALL_STATUS_UNKNOW.equals(overall_status)){
				result.put("code", "000001");
				result.put("message", "overall_status is not 01,02,03, overall_status is " +overall_status);
				logger.info("overall_status is not 01 or 03, overall_status is {}!",overall_status);
				return result.toJSONString();
			}
			//判断扣款方式
			if("allinpal".equals(feededuct)){
				deductService.deductCharge(notify);
				toukongService.doToukong(notify);
			}else if("3rdparty".equals(feededuct)){
				//信托
				ret = deductService.updateLoanResult(notify);
				if(!ret){
					break;
				}
				if("2".equals(notify.getPayment_no())){
					//调用机构网关： 用户信息上送，贷款信息上送，生成还款计划
					toukongService.doToukong(notify);
				}
			}
		}
		
		result.put("code", "000000");
		result.put("message", "success");
		return result.toJSONString();
	}
	
	
//	@RequestMapping(value="/dealUnkownDeductState", method=RequestMethod.POST)
//	@ResponseBody
//	void dealUnkownDeductState() {
//		logger.info("NotifyController method dealUnkownDeductState is start!");
//		
//		deductService.dealUnkownDeductState();
//	}
	
	@RequestMapping(value="/dealChargeDeduct", method=RequestMethod.POST)
	@ResponseBody
	void dealChargeDeduct() {
		
		deductService.dealUnkownAndFailedDeductState();
	}
	
}
