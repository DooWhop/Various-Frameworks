package com.allinpal.twodfireservice.unknowService.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.allinpal.twodfireservice.notify.domain.Notify;
import com.allinpal.twodfireservice.unknowService.domain.ChargeReg;
import com.allinpal.twodfireservice.unknowService.domain.Draw;
import com.allinpal.twodfireservice.unknowService.service.UnknowService;

@RestController
@RequestMapping("unknowService")
public class UnknowServiceController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private UnknowService unknowService;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Value("${mas.sendV2.url}")
	private String masSendV2Url;
	
	@Value("${notifyService.url}")
	private String notifyServiceUrl;
	
	@RequestMapping(value="/loanQuery")
	String loanQuery() {
		logger.info("service method {}", "unknowService/loanQuery");
		Map<String,String> response = new HashMap<String,String>();
		try{
			Draw draw = new Draw();
			draw.setStatus("12");//12-放款中
			List<Draw> drawList = unknowService.getDrawList(draw);
			for(int i=0;i<drawList.size();i++){
				//05放款结果查询(单条)
				JSONObject reqJson = new JSONObject();
				reqJson.put("orgCode", "21100005");
				reqJson.put("prodCode", "8007000001");
				reqJson.put("transCode", "05");
				reqJson.put("app_seq", drawList.get(i).getRecordNo());

				MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>();
				paramMap.add("topicName", "loan.orgGateway");
				paramMap.add("messgeJsonBody", reqJson.toJSONString()); //主题参数拼成的json字符串
				logger.info("service orgGateway.natrust paramMap {}",paramMap);
				String retStr = restTemplate.postForObject(masSendV2Url, paramMap,String.class);
				logger.info("service orgGateway.natrust result {}",retStr);
		        JSONObject retJson = JSONObject.parseObject(retStr);
		        
		        if("W-Y000000".equals(retJson.getString("resultCode"))){
					if("0000".equals(retJson.getString("retCode")) && "1".equals(retJson.getString("status"))){
						List<Notify> list = JSONObject.parseArray(retJson.getString("resultInfo").toString(),Notify.class);
						Iterator<Notify> iterator = list.iterator();
						while(iterator.hasNext()){
							Notify notify = iterator.next();
							//status 00：未做 01：成功 02：处理中 03：失败  04：等候
							if("01".equals(notify.getStatus()) || "03".equals(notify.getStatus())){
								//do nothing
							}else{
								iterator.remove(); 
							}
						}
						if(list.size() > 0){
							String notifyRet = restTemplate.postForEntity(notifyServiceUrl.concat("/loanResult"), list, String.class).getBody();
					        logger.info("unknowService/loanQuery param {}",drawList.get(i).getRecordNo());
							logger.info("unknowService/loanQuery result {}", notifyRet);
						}
					}
		        }
			}
		
		}catch(Exception e){
			response.put("code", "000002");
			response.put("message", "error");
			logger.error("unknowService/loanQuery error : ",e);
		}
		logger.info("service response {}", JSON.toJSONString(response));
		return  JSON.toJSONString(response);
	}

	private Notify getMap(JSONObject retJson) {
		Notify notify = new Notify();
		notify.setApp_seq(retJson.getString("app_seq"));
		notify.setResponse_message(retJson.getString("response_message"));
		notify.setPayment_time(retJson.getString("payment_time"));
		notify.setPayment_no(retJson.getString("payment_no"));
		notify.setOverall_status(retJson.getString("overall_status"));
		notify.setPayment_amount(retJson.getString("payment_amount"));
		notify.setStatus(retJson.getString("trans_status"));
		return notify;
	}
}
