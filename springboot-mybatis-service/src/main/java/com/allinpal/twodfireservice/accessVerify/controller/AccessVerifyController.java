package com.allinpal.twodfireservice.accessVerify.controller;

import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.allinpal.twodfireservice.accessVerify.domain.AccessInfo;
import com.allinpal.twodfireservice.accessVerify.domain.BasicInfo;
import com.allinpal.twodfireservice.accessVerify.domain.TLBUser;
import com.allinpal.twodfireservice.accessVerify.service.AccessVerifyService;
import com.allinpal.twodfireservice.idenverify.rcservice.P020QueryOpenIdService;
import com.caucho.hessian.client.HessianProxyFactory;
import com.tonghuafund.loan.credit.factory.CreditFactory;

@RestController
@RequestMapping("accessVerifyService")
public class AccessVerifyController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private AccessVerifyService accessVerifyService;
	
	@Value("${credit.factory.url}")
	private String creditFactoryUrl;
	
	@Value("${wxMutexCheckService.open.url}")
	private String openUrl;
	
	@Autowired
	private P020QueryOpenIdService p020QueryOpenIdService;
	
	@Autowired
	private RestTemplate restTemplate;
	
//	@RequestMapping("/mutexCheckService")
//	public String mutexCheckService(HttpServletRequest request) throws Exception {
	@RequestMapping(value="/mutexCheckService", method=RequestMethod.POST)
	@ResponseBody
	String mutexCheckService(String openId,String userId) throws Exception {
//		String openId = request.getParameter("openId");
//		String userId = request.getParameter("userId");
		logger.info("accessVerifyService.mutexCheckService openId is {}", openId);
		logger.info("accessVerifyService.mutexCheckService userId is {}", userId);
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("code", "000000");
		resultMap.put("message", "校验通过");
		
		String OPEN_ID = null;
		if(openId!=null && !openId.equals("")){
			OPEN_ID = openId;
		}else if(userId != null && !userId.equals("")){
			//通过userId查询tlbuseroid
			TLBUser user = accessVerifyService.queryTlbUserInfo(userId);
			OPEN_ID = user.getOpenid();
		}
		
//		String openId = (String) parm.get("openId");
//		String idNo =  (String) parm.get("idNo");
//		String merchantNo =  (String) parm.get("merchantNo");
		if(OPEN_ID==null){
			resultMap.put("code", "000001");
			resultMap.put("message", "openId is null");
			return JSON.toJSONString(resultMap);
		}
		Map<String, String> L131Map = new TreeMap<String,String>();
		L131Map.put("interfacecode", "L131");
		L131Map.put("usercode", OPEN_ID);//OPEN_ID
//		if(merchantNo!=null){
//			L131Map.put("merno", merchantNo);
//		}
//		if(idNo!=null){
//			L131Map.put("idno", openId);
//		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss"); 
		L131Map.put("terminalno", "SYB");//接入方
		L131Map.put("accesscode", "weixin");
		L131Map.put("usertype", "P");
		L131Map.put("channel", "P");
		L131Map.put("signType", "MD5");
		L131Map.put("version", "V1.0");
		L131Map.put("partner", "tlbapp");
		L131Map.put("inputCharset", "UTF-8");
		L131Map.put("timestamp", sdf.format(new Date()));

		 StringBuilder sb = new StringBuilder();
	        Iterator<String> iterator = L131Map.keySet().iterator();
	        while (iterator.hasNext())
	        {
	            sb.append(L131Map.get(iterator.next()) + "|");
	        }
	        String source = sb.toString();
	        if (!StringUtils.isEmpty(source))
	        {
	            source = source + "1234567890";
	        }
	        
	        String sourceSign = DigestUtils.md5Hex((source).getBytes("UTF-8"));
	        
	        L131Map.put("sign", sourceSign);
	    
	    MultiValueMap<String, String> L131Map1 = new LinkedMultiValueMap<String, String>();
	    for(Map.Entry<String,String> entry :L131Map.entrySet()) {
	    	L131Map1.add(entry.getKey(), entry.getValue());
	    }
	        
		String jsonStr = URLDecoder.decode(restTemplate.postForObject(openUrl, L131Map1, String.class), "UTF-8");
		logger.info("L131's result is {}", jsonStr);
		
		if(jsonStr == null || "".equals(jsonStr)){
			resultMap.put("code", "000002");
			resultMap.put("message", "L131's return jsonStr is null or empty!");
			logger.info("L131's return jsonStr is null or empty!");
			return JSON.toJSONString(resultMap);
		}
		
		Map<String, Object> ret = (Map<String, Object>)JSONObject.parseObject(jsonStr,Map.class);
		if(ret == null || !"true".equals(ret.get("isSuccess"))){
			resultMap.put("code", "000002");
			resultMap.put("message", "L131 is failure !");
			logger.info("L131 is failure !ret is {}",ret);
			return JSON.toJSONString(resultMap);
		}
		Map<String, Object> retMap = (Map<String, Object>)ret.get("response");
		String interfaceretcode = (String)retMap.get("code");
		
		if(!"W-Y000000".equals(interfaceretcode)){
			resultMap.put("code", "000002");
			resultMap.put("message", "L131 is faliure! interfaceretcode is "+interfaceretcode);
			logger.info("L131 is faliure! interfaceretcode is {}, message is {}", interfaceretcode,  retMap.get("message"));
			return JSON.toJSONString(resultMap);
		}else{
			if(retMap.get("hasactive")!=null&&retMap.get("hasactive").toString().equals("Y")){
				resultMap.put("code", "000002");
				resultMap.put("message", "L131 is faliure! interfaceretcode is "+interfaceretcode);
				logger.info("L131 is faliure! interfaceretcode is {}, message is {}", interfaceretcode,  retMap.get("message"));
				return JSON.toJSONString(resultMap);
			}
		}
		return JSON.toJSONString(resultMap);
	}
	
	@RequestMapping(value="/merchantVerify", method=RequestMethod.POST)
	String service(@RequestBody JSONObject parm) {
		logger.info("accessVerifyService.merchantVerify param is:", parm.toString());
		String merchantNo = (String) parm.get("merchantNo");
		String mobile =  (String) parm.get("mobile");
		String prodCode =  (String) parm.get("prodCode");
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		//isAccessCheckPass-准入校验是否通过；Y-是 N-否
		resultMap.put("isAccessCheckPass", "N");
		String rsultFlag = JSON.toJSONString(resultMap);
		AccessInfo ai = new AccessInfo();
		BasicInfo bi = new BasicInfo();
		try {
			Map<String, Object> paramTemp = new HashMap<String, Object>();
			paramTemp.put("merchantNo", merchantNo);
			paramTemp.put("mobile", mobile);
			paramTemp.put("prodCode", prodCode);
			
			ai = accessVerifyService.getAcctInfo(paramTemp);
			if(null == ai){
				logger.info("can't find data in t_prod_acct");
				return rsultFlag;
			}
			
			logger.info("1.------------check age（22-55,含22、55周岁）------------------");
			String idNo = ai.getCertNo();
			boolean isAgeCheckPass = accessVerifyService.isAgeCheckPass(idNo);
			if(!isAgeCheckPass){
				recordErrorInfor(ai,"TH00001");
				resultMap.put("errorCode", "TH00001");
				return JSON.toJSONString(resultMap);
			}
			
			bi = accessVerifyService.getBasicInfo(merchantNo);
			logger.info("cal_2dfire_month_metadata data is {}",bi);
			if(null == bi){
				logger.info("can't find data in cal_2dfire_month_metadata");
				recordErrorInfor(ai,"TH00013");
				resultMap.put("errorCode", "TH00013");
				return JSON.toJSONString(resultMap);
			}
			logger.info("cal_2dfire_month_metadata.receive_amt_3m_csctv is {}",bi.getSeriInfo());

			logger.info("3.------------二维火入网时间：3个月及以上------------------");
			boolean isNetTimeCheckPass = accessVerifyService.isNetTimeCheckPass(bi.getNetTime());
			if(!isNetTimeCheckPass){
				recordErrorInfor(ai,"TH00003");
				resultMap.put("errorCode", "TH00003");
				return JSON.toJSONString(resultMap);
			}
			
			Map<String,String> mapAmt = accessVerifyService.getPerMonthAmt(bi.getSeriInfo());
			logger.info("4.------------授信前3个月连续有经营流水--------------------");
			boolean isFlowSeriesCheckPass = mapAmt.get("isFlowSeriesCheckPass")=="Y"?true:false;
			if(!isFlowSeriesCheckPass){
				recordErrorInfor(ai,"TH00004");
				resultMap.put("errorCode", "TH00004");
				return JSON.toJSONString(resultMap);
			}
			logger.info("5.------------流水金额要求：月均不得低于10000元（授信前3个月）--");
			boolean isAvgFlowCheckPass = mapAmt.get("isAvgFlowCheckPass")=="Y"?true:false;
			if(!isAvgFlowCheckPass){
				recordErrorInfor(ai,"TH00005");
				resultMap.put("errorCode", "TH00005");
				return JSON.toJSONString(resultMap);		
			}
			logger.info("6.------------工商校验--------------------------------");
			HessianProxyFactory factory = new HessianProxyFactory(); 
			CreditFactory creditFactory;
			String legalName = ai.getLegalName();//法人名称
			String legalIdNo = ai.getCertNo();//法人身份证号码
			String registerNo = bi.getBusiCertNo();//营业执照号
			String corpName = bi.getCorpName();//商户名称
			
			creditFactory = (CreditFactory)factory.create(creditFactoryUrl);
			Map<String, Object> zxMap = creditFactory.validate(legalName, legalIdNo, registerNo, corpName, merchantNo,prodCode,null);
			if(zxMap == null){
				recordErrorInfor(ai,"TH00006");
				resultMap.put("errorCode", "TH00006");
				return JSON.toJSONString(resultMap);	
			}
			if(zxMap.get("SUCCESS") !=null){
				String result = zxMap.get("SUCCESS").toString();
				if("1".equals(result)){
					Map<String, Object> data = (Map<String, Object>)zxMap.get("data");
					//征信返回成功，判断企业成立日期,12个月以上
					String registDate = data.get("registDate") == null ? "" : data.get("registDate") .toString();
					registDate = registDate.replace("-", "");
					if(registDate != null && !"".equals(registDate)){
						boolean isOpenDateCheckPass = accessVerifyService.isComOpenDateCheckPass(registDate);
						if(!isOpenDateCheckPass){
							recordErrorInfor(ai,"TH00002");
							resultMap.put("errorCode", "TH00002");
							return JSON.toJSONString(resultMap);
						}
						//企业成立日期，方便插入到授信申请表中
						resultMap.put("registerDate", registDate);
					}else{
						recordErrorInfor(ai,"TH00012");
						resultMap.put("errorCode", "TH00012");
						return JSON.toJSONString(resultMap);
					}	
					resultMap.put("isAccessCheckPass", "Y");
					return JSON.toJSONString(resultMap);
				}else if("2".equals(result)){
					recordErrorInfor(ai,"TH00007");
					resultMap.put("errorCode", "TH00007");
				}else if("3c".equals(result.toLowerCase())){
					recordErrorInfor(ai,"TH00008");
					resultMap.put("errorCode", "TH00008");
				}else if("A".equalsIgnoreCase(result) || result.contains("3") || "4".equals(result)){
					recordErrorInfor(ai,"TH00009");
					resultMap.put("errorCode", "TH00009");
				}else if("10".equals(result)){
					recordErrorInfor(ai,"TH00010");
					resultMap.put("errorCode", "TH00010");
				}else{
					recordErrorInfor(ai,"TH00011");
					resultMap.put("errorCode", "TH00011");
				}
				return  JSON.toJSONString(resultMap);
			}else{
				recordErrorInfor(ai,"TH00011");
				resultMap.put("errorCode", "TH00011");
				return JSON.toJSONString(resultMap);
			}
		}catch (Exception e) {
			logger.error("AccessVerifyService merchantVerify Exception is:",e);
			recordErrorInfor(ai,"TH00005");
			resultMap.put("errorCode", "TH00005");
			return JSON.toJSONString(resultMap);
		} 	
	}
	
	//重启线程，错误信息入库
	public void recordErrorInfor(final AccessInfo ai,final String errorCode){
		new Thread() {
			public void run() {
				accessVerifyService.recordErrorInfor(ai,errorCode);        
			}
		}.start();
	}
}
