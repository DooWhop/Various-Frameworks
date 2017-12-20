package com.allinpal.twodfireweb.controller;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.allinpal.twodfireweb.request.CreditReq;
import com.allinpal.twodfireweb.util.GenerateIdsUtil;

@Controller
public class MyCreditController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private RestTemplate restTemplate;
	
	@Value("${creditService.url}")
	private String creditServiceUrl;

	@Value("${orgCode}")
	private String orgCode;

	@Value("${sdk.mode}")
	private String mode;
	
	private static String ORGCODE_2DFIRE = "21100005";
	private static String PRODCODE_2DFIRE = "9002000003";
	private static String RETCODE_000000="000000";
	//0全部 1申请未完成 2申请失效 3审批中 4审批失败 5审批通过 6额度生效 66额度到期 7额度失效 8审批作废 9额度暂停
	private static String EFFECTIVE_STATUS="3,5,6,9";
	
    @RequestMapping(value ="/queryCreditDetail")  
    public String queryCreditDetail(CreditReq credit,Model model){ 
    	try{
    	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	    logger.info("queryCreditDetail credit "+JSON.toJSONString(credit));

	    	model.addAttribute("merchantNo", credit.getMerchantNo());
	    	model.addAttribute("prodCode", credit.getProdCode());
	    	model.addAttribute("mobile", credit.getMobile());
	    	
			MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
			if(null != credit.getRecordNo() && !"".equals(credit.getRecordNo())){
		    	map.add("recordNo", credit.getRecordNo());
			}
	    	map.add("merchantNo", credit.getMerchantNo());
			map.add("prodCode", credit.getProdCode());
			map.add("describle", "queryCreditDetail");
	        String result = restTemplate.postForObject(creditServiceUrl.concat("/query"), map, String.class);
			logger.info("queryCreditDetail result {}", result);
	        JSONObject initJson = JSONObject.parseObject(result);
			if(RETCODE_000000.equals(initJson.getString("code"))){
	    		List<CreditReq> creditList = JSON.parseArray(initJson.getString("creditList"), CreditReq.class); 
	    		if(null != creditList && creditList.size() > 0){
	    			if(!EFFECTIVE_STATUS.contains(creditList.get(0).getStatus())){
		    	    	model.addAttribute("effective", "0"); 
	    			}else{
		    			if("1".equals(creditList.get(0).getStatus2dfire()) || "2".equals(creditList.get(0).getStatus2dfire())){
		    				Map<String,String> temp = new HashMap<String,String>();
		    				temp.put("orgCode", orgCode);
		    				temp.put("mode", mode);
		    				temp.put("loginId", creditList.get(0).getCertNo());
		    				temp.put("userName", creditList.get(0).getCustName());
		    				temp.put("idenno", creditList.get(0).getCertNo());
		    				model.addAttribute("message", JSONObject.toJSON(temp));
		    		    	model.addAttribute("merchantNo", creditList.get(0).getMerchantNo());
		    				return"pickReport";  
		    			}
	    			}
	    			Date createTime=new Date(creditList.get(0).getCreateTime());
	    			model.addAttribute("createTime", sdf.format(createTime));
	    			Date currDate = new Date();
        	    	Calendar calendar = Calendar.getInstance();
        	    	calendar.setTime(currDate);
        	    	calendar.add(Calendar.MONTH, 6);
        	    	String sixEndDate = sdf.format(calendar.getTime());
        	    	calendar.add(Calendar.MONTH, 6);
        	    	String twelveEndDate = sdf.format(calendar.getTime());
        	    	logger.info("sixEndDate "+sixEndDate+"twelveEndDate "+twelveEndDate);
	    			model.addAttribute("currDate", sdf.format(currDate));
	    			model.addAttribute("sixEndDate", sixEndDate);
	    			model.addAttribute("twelveEndDate", twelveEndDate);
	    			model.addAttribute("creditReq", creditList.get(0));
	        		model.addAttribute("creditReqStr", JSON.toJSONString(creditList.get(0)));
	        		
	        		//协议参数
	    			SimpleDateFormat df = new SimpleDateFormat( "yyyyMMdd");
	    			model.addAttribute("signDate", df.format(currDate));//协议签订日期
	    			model.addAttribute("protocolNoWTKK", GenerateIdsUtil.protocolId("0"));//委托扣款协议编号
	    			model.addAttribute("protocolNoKHFW", GenerateIdsUtil.protocolId("1"));//客户服务协议编号

	    		}else{
	    	    	model.addAttribute("totalCount", "0"); 
	    		}
			}
	    }catch(Exception e){
    		logger.info("queryCreditDetail exception : ",e);
	    	model.addAttribute("code", "000002");
	    	model.addAttribute("message", "很抱歉，系统繁忙，请稍后再试");
    		return"myCredit/fail";  
    	}
		return"myCredit/creditDetail";  
    }

    @RequestMapping(value ="/showProtocol", method = RequestMethod.POST)  
    public String showProtocol(Model model,HttpServletRequest request){ 
    	try{
        	String creditReqStr = request.getParameter("creditReqStr");
        	logger.info("creditReqStr is : "+creditReqStr);
        	CreditReq tempInfo = JSON.parseObject(creditReqStr, CreditReq.class);
	    	model.addAttribute("custName", tempInfo.getCustName());
	    	model.addAttribute("certNo", tempInfo.getCertNo());
	    	model.addAttribute("signDate", request.getParameter("signDate"));
	    	
    		String protocolType = request.getParameter("protocolType");
    		if("1".equals(protocolType)){
    	    	model.addAttribute("protocolNoKHFW", request.getParameter("protocolNoKHFW"));
    	 		return"myCredit/2dfireCustomerServiceProtocol";  
    		}else if("0".equals(protocolType)){
    	    	model.addAttribute("bankAcctNo", tempInfo.getBankAcctNo());
    	    	model.addAttribute("protocolNoWTKK", request.getParameter("protocolNoWTKK"));
    	 		return"myCredit/2dfireWTKKProtocol";  
    		}
    		
    	 }catch(Exception e){
     		logger.info("showProtocol exception : ",e);
 	    	model.addAttribute("code", "000002");
 	    	model.addAttribute("message", "很抱歉，系统繁忙，请稍后再试");
     	}
 		return"myCredit/fail";  
     }


    @RequestMapping(value ="/queryCreditList")  
    public String queryCreditList(CreditReq credit,Model model){ 
    	try{
    	    logger.info("queryCreditList credit param "+JSON.toJSONString(credit));
	    	model.addAttribute("merchantNo", credit.getMerchantNo());
	    	model.addAttribute("prodCode", credit.getProdCode());
	    	model.addAttribute("mobile", credit.getMobile());	    	

    	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
	    	map.add("merchantNo", credit.getMerchantNo());
			map.add("prodCode", credit.getProdCode());
	        String result = restTemplate.postForObject(creditServiceUrl.concat("/query"), map, String.class);
			logger.info("queryCreditList result {}", result);
	        JSONObject initJson = JSONObject.parseObject(result);
			if(RETCODE_000000.equals(initJson.getString("code"))){
	    		List<CreditReq> creditList = JSON.parseArray(initJson.getString("creditList"), CreditReq.class); 
	    		if(null != creditList && creditList.size() > 0){
	    			for(int i=0;i<creditList.size();i++){
		    			Date createTime=new Date(creditList.get(i).getCreateTime());
		    			creditList.get(i).setDescrible((sdf.format(createTime)));
	    			}
	    			
	    			model.addAttribute("creditList", creditList);
	    			model.addAttribute("creditListStr", JSONObject.toJSON(creditList));
	    			
	    		}else{
	    	    	model.addAttribute("totalCount", "0"); 
	    		}
			}
    		
    	}catch(Exception e){
    		logger.info("queryCreditList exception : ",e);
	    	model.addAttribute("code", "000002");
	    	model.addAttribute("message", "很抱歉，系统繁忙，请稍后再试");
    		return"myCredit/fail";  
    	}
		return"myCredit/creditList";  
    }
	
}
