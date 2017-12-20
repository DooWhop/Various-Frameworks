package com.allinpal.twodfireservice.reportscanService.controller;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.allinpal.twodfireservice.creditService.domain.Credit;

@RestController
@RequestMapping("reportscanService")
public class ReportscanController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private RestTemplate restTemplate;
	@Value("${reportscanService.queryCredit.url}")
	private String reportscanServiceQueryCreditUrl;
	
	@Value("${reportscanService.updateCredit.url}")
	private String reportscanServiceUpdateCreditUrl;
	
	@Value("${mas.send.url}")
	private String masSendUrl;
	
	@RequestMapping(value="/scan", method=RequestMethod.GET)
	void service() {		
		logger.info("1.----查询授信申请信息表 t_loan_credit_req.status_2dfire--1-审批中；2:待额度审批-状态的记录--");
		
		MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<String, String>();
		paramMap.add("dayLimit", "5");
		paramMap.add("prodCode","9002000003");
		logger.info("search status param is:{}",paramMap);
		
		String result = restTemplate.postForObject(reportscanServiceQueryCreditUrl, paramMap, String.class);
		
        logger.info("search status_2dfire result is:"+result);
        
        JSONObject initJson = JSONObject.parseObject(result);
		
        if(null!= result && "000000".equals((String) initJson.get("code"))){
        	List<Credit> creditList = JSON.parseArray(initJson.getString("creditList"), Credit.class); 

        	if (CollectionUtils.isEmpty(creditList)) {
        		return;
        	}
        	for(Credit credit: creditList){
        		String certNo = (String)credit.getCertNo();
        		String recordNo =  (String)credit.getRecordNo();
        		String entity_id =  (String)credit.getMerchantNo(); 
        		String livest =  (String)credit.getLivest();
        		String register_date = (String)credit.getRegisterDate();
        		String credit_amt = credit.getApplAmt().toString();
        		
        		logger.info("2.--人行征信查询----com-thfund-mgr-queryPBCredit-----------");
        		MultiValueMap<String, String> queryPBMap = new LinkedMultiValueMap<>();
        		queryPBMap.add("topicName", "mgr.queryPBCredit");
        		queryPBMap.add("operatorId", certNo);
        		logger.info("queryPBMap param is:{}",queryPBMap);
        		String pbResult = restTemplate.postForObject(masSendUrl, queryPBMap, String.class);
        		Map<String,Object> pbResultMap =JSONObject.parseObject(pbResult);
        		logger.info ("service queryEmp result {}"+pbResult);
        		
        		String report = "";
        		String report_risk = "";
        		if(null !=pbResultMap && "W-Y000000".equals((String)pbResultMap.get("resultCode"))){
        			report = (String)pbResultMap.get("report");
        			report_risk = (String)pbResultMap.get("reportRisk");
        			
        			logger.info("3.--大数据额度查询----------------------------------------");
        			Date dateNow = new Date();
        			Calendar calendar = Calendar.getInstance();
        	        calendar.setTime(dateNow); 
        	        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
        	        dateNow = calendar.getTime();
        	        
        			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMM");
        			String work_date = sdf2.format(dateNow);
        			String resi_status = convertStatus(livest); 
        			MultiValueMap<String, String> queryBigDataMap = new LinkedMultiValueMap<>();
        			queryBigDataMap.add("topicName", "dataProcess.twoDFireCreditCheck");
        			queryBigDataMap.add("work_date", work_date);//申请月份
        			queryBigDataMap.add("entity_id", entity_id);//二维火商户ID
        			queryBigDataMap.add("resi_status", resi_status);//居住状态
        			queryBigDataMap.add("credit_amt", credit_amt);//申请额度
        			queryBigDataMap.add("report", report);//人行报告内容
        			queryBigDataMap.add("report_risk", report_risk);//算话人行风险报告内容
        			queryBigDataMap.add("register_date", register_date);//企业成立日期
        			
        			logger.info("twoDFireCreditCheck param is:{}",queryBigDataMap);
        			
            		String bigDataResult = restTemplate.postForObject(masSendUrl, queryBigDataMap, String.class);
            		Map<String,Object> bigDataResultMap =JSONObject.parseObject(bigDataResult);
            		logger.info ("service queryBigDataMap result {}"+bigDataResultMap);
            		
            		String pre_credit_amt_6m = "0";//预授信额度6个月
            		String pre_credit_amt_12m = "0";//预授信额度12个月
            		String f_score = "0";//通华信用分
            		if(null !=bigDataResultMap && "W-Y000000".equals((String)bigDataResultMap.get("resultCode"))){
            			logger.info("4.更新授信申请表 t_loan_credit_req.status_2dfire and 二维火信息同步 com-thfund-loan-twoFireCreditInfoSynch");
            			
            			String success = (String)bigDataResultMap.get("success");//是否成功0:失败 1:成功
            			String status2dfire = "";
            			String status = "";
            			BigDecimal amt_6 = new BigDecimal(0);
            			BigDecimal amt_12 = new BigDecimal(0);
            			if("1".equals(success)){
            				if(null != bigDataResultMap.get("esti_trust_amt_6m")){
                				pre_credit_amt_6m = (String)bigDataResultMap.get("esti_trust_amt_6m");
                				amt_6 = new BigDecimal(pre_credit_amt_6m);
            				}
                			if(null != bigDataResultMap.get("esti_trust_amt_12m")){
                				pre_credit_amt_12m = (String)bigDataResultMap.get("esti_trust_amt_12m");
                				amt_12 = new BigDecimal(pre_credit_amt_12m);
                			}
                			if(null != bigDataResultMap.get("f_score")){
                				f_score = (String)bigDataResultMap.get("f_score");
                			}
                			if(amt_6.compareTo(new BigDecimal(100))>=0 || amt_12.compareTo(new BigDecimal(100))>=0){
                				status2dfire = "3";
                    			status = "5";//5:审批通过
                			}else{
                				//返回的金额必须有一个大于100的，否则审批失败
                				status2dfire = "2";
                    			status = "4";//审批失败
                			}
            			}else{
            				status2dfire = "2";
                			status = "4";//审批失败
            			}	
            			
            			MultiValueMap<String, String> paraToUpdate = new LinkedMultiValueMap<>();
            			paraToUpdate.add("recordNo", recordNo);
            			paraToUpdate.add("status", status);
            			paraToUpdate.add("status2dfire", status2dfire);
            			paraToUpdate.add("sixMonthAmt", pre_credit_amt_6m);
            			paraToUpdate.add("twelveMonthAmt", pre_credit_amt_12m);
            			paraToUpdate.add("creditPoint", f_score);
            			
        				String updateJson = restTemplate.postForObject(reportscanServiceUpdateCreditUrl, paraToUpdate, String.class);
        				logger.info("update status_2dfire result is:"+updateJson.toString());
            		}
            		
        		}
        	    continue;
        	}
        }
	}	
	
	public String convertStatus(String livest){
		//大数据授信额度查询：0:无 1:自有 2:借住 3:租住
		//二维火授信申请表： 100-自有住房   201-普通租房   202-借宿   999-其他'
		if("100".equals(livest)){
			return "1";
		}else if("201".equals(livest)){
			return "3";
		}else if("202".equals(livest)){
			return "2";
		}else if("999".equals(livest)){
			return "0";
		}else{
			return "0";
		}
	}
}
