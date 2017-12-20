package com.allinpal.twodfireservice.accessVerify.service;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONObject;
import com.allinpal.twodfireservice.AppConfig;
import com.allinpal.twodfireservice.accessVerify.domain.AccessInfo;
import com.allinpal.twodfireservice.accessVerify.domain.BasicInfo;
import com.allinpal.twodfireservice.accessVerify.domain.TLBUser;
import com.allinpal.twodfireservice.accessVerify.domain.TApplyNotaccess;
import com.allinpal.twodfireservice.accessVerify.domain.TApplyNotaccessErrorCode;
import com.allinpal.twodfireservice.accessVerify.mapper.AccessVerifyMapper;
import com.allinpal.twodfireservice.accessVerify.util.TransferInterfaceUtil;
import com.allinpal.twodfireservice.util.GenerateIdsUtil;

@Service("accessVerifyService")
//@Transactional(propagation = Propagation.REQUIRED,isolation = Isolation.DEFAULT,timeout=36000,readOnly=false,rollbackFor=RuntimeException.class)
public class AccessVerifyService {

	@Autowired
	public AccessVerifyMapper accessVerifyMapper;
	@Autowired
	public AppConfig appConfig;
	
	@Value("P020")
	private String INTERFACE_QUERY_OPENID;
	
	@Value("${p020.tlb.route.url}")
	private String TLB_ROUTE_URL;
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private RestTemplate restTemplate;

	public AccessInfo getAcctInfo(Map<String, Object> paramTemp){
		AccessInfo pai = new AccessInfo();
		pai.setMerchantNo(paramTemp.get("merchantNo").toString());
		pai.setProdCode(paramTemp.get("prodCode").toString());
		pai.setMobile(paramTemp.get("mobile").toString());
		pai.setStatus("1");//1-生效
		return accessVerifyMapper.getAcctInfo(pai);
	}
	
	public boolean isAgeCheckPass(String birthday) {
		int intDay = Integer.parseInt(birthday.substring(6, 14));
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String now = sdf.format(new Date());
		int intNow = Integer.parseInt(now);
		if(intDay + 550000 < intNow || intDay + 220000 > intNow){
			return  false;
		}
		return true;
	}
	
	public boolean isNetTimeCheckPass(String netTime) throws ParseException{
		netTime = netTime.replace("/", "");
		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		Date netDate = df.parse(netTime);
		//3个月
		Calendar last3month = Calendar.getInstance();
		last3month.add(Calendar.MONTH, -3);
		if(last3month.getTime().compareTo(netDate)>0){
			return true;
		}
		return false;
	}
	
	public boolean isComOpenDateCheckPass(String registDate) throws ParseException{
		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		Date regDate = df.parse(registDate);
		
		Calendar last2year = Calendar.getInstance();
		last2year.add(Calendar.YEAR, -1);
		
		if(last2year.getTime().compareTo(regDate)>0){
			return true;
		}
		return false;	
	}
	
	public void recordErrorInfor(AccessInfo ai,String errorCode){
		TApplyNotaccess tn = new TApplyNotaccess();
		tn.setRecordNo(GenerateIdsUtil.generateId(appConfig.getAppIp()));
		tn.setProdCode(ai.getProdCode());
		tn.setMobile(ai.getMobile());
		tn.setMerchantNo(ai.getMerchantNo());
		tn.setErrorMsg(TApplyNotaccessErrorCode.getRetValue(errorCode));
		tn.setErrorCode(errorCode);
		tn.setCreateTime(System.currentTimeMillis());
		tn.setCertNo(ai.getCertNo());
		accessVerifyMapper.recordErrorInfor(tn);
	}
	
	//资金方给力参数格式为(月份：金额|月份：金额)：2017-08:101559.0|2017-09:77757.0|2017-10:99941.0
	public Map<String,String> getPerMonthAmt(String amtList){
		Map<String,String> amtMap = new HashMap<String,String>();
		String isFlowSeriesCheckPass = "Y";
		String isAvgFlowCheckPass  = "Y";
		BigDecimal threeAll = new BigDecimal(0);
		amtList = amtList.replace(":", "|");
		String[] arrAmt = amtList.split("\\|");
		
		//要求连续三个月流水
		if(arrAmt.length != 6 ){
			isFlowSeriesCheckPass = "N";
			isAvgFlowCheckPass = "N";
			amtMap.put("isFlowSeriesCheckPass", isFlowSeriesCheckPass);
			amtMap.put("isAvgFlowCheckPass", isAvgFlowCheckPass);
			return amtMap;
		}
		BigDecimal zero = new BigDecimal(0);
		for(int i=0;i<arrAmt.length-1;i++){
			
			if(zero.compareTo(new BigDecimal(arrAmt[i+1]))>=0){
				isFlowSeriesCheckPass = "N";
				isAvgFlowCheckPass = "N";
				amtMap.put("isFlowSeriesCheckPass", isFlowSeriesCheckPass);
				amtMap.put("isAvgFlowCheckPass", isAvgFlowCheckPass);
				return amtMap;
			}
			threeAll = new BigDecimal(arrAmt[i+1]).add(threeAll);
			++i;
		}
		BigDecimal result = threeAll.divide(new BigDecimal(3), 2, BigDecimal.ROUND_HALF_UP);
		if(result.compareTo(new BigDecimal(10000))<0){
			isAvgFlowCheckPass = "N";
		}
		amtMap.put("isFlowSeriesCheckPass", isFlowSeriesCheckPass);
		amtMap.put("isAvgFlowCheckPass", isAvgFlowCheckPass);
		return amtMap;
	}
	
public TLBUser queryTlbUserInfo(String userId) throws Exception{
        String tlbUserOid = accessVerifyMapper.getTlbUserOid(userId);
        TLBUser tlbUser = new TLBUser();
        tlbUser.setTimestamp(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
        tlbUser.setInterfacecode(INTERFACE_QUERY_OPENID);
        tlbUser.setUserid(tlbUserOid);
		Map<String, String> params = TransferInterfaceUtil.getParamLess(tlbUser);
		logger.info("queryTlbUserInfo params is {}",params);
		MultiValueMap<String, String> P020Map = TransferInterfaceUtil.convertMap2MultiValueMap(params);
        String result = restTemplate.postForObject(TLB_ROUTE_URL, P020Map, String.class);
        String res = TransferInterfaceUtil.getResponse(result);                                   
        
        logger.info("queryTlbUserInfo result is {}",res);
        if(res == null || "".equals(res)){
			return null;
		}		
		Map<String, Object> ret = JSONObject.parseObject(res,Map.class);
		if(ret == null || !"true".equals(ret.get("isSuccess"))){
			return null;
		}
		Map<String, Object> retMap = (Map<String, Object>) ret.get("response");
		String interfaceRetCode = (String)retMap.get("code");	
		if(!"W-Y000000".equals(interfaceRetCode)){
			return null;
		}
        String openId = (String) retMap.get("openid");
        String idenno = (String) retMap.get("idenno");
        String ispassidenauth = (String) retMap.get("ispassidenauth");
		tlbUser.setOpenid(openId);
		tlbUser.setIdenno(idenno);
		tlbUser.setIspassidenauth(ispassidenauth);
		
        return tlbUser;                        	
	}
	
	
	public BasicInfo getBasicInfo(String merchantNo){
		
		return accessVerifyMapper.getBasicInfo(merchantNo);
	}	
}
