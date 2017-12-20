package com.allinpal.twodfireservice.draw.controller;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.allinpal.twodfireservice.AppConfig;
import com.allinpal.twodfireservice.creditService.domain.Credit;
import com.allinpal.twodfireservice.creditService.service.CreditService;
import com.allinpal.twodfireservice.draw.domain.Charge;
import com.allinpal.twodfireservice.draw.domain.Draw;
import com.allinpal.twodfireservice.draw.service.DrawService;
import com.allinpal.twodfireservice.util.GenerateIdsUtil;
import com.allinpay.base.holder.CommonParameterHolder;
import com.allinpay.base.util.RadmonUtil;
import com.allinpay.fmp.extintfc.sms.service.SMSSendService;
import com.allinpay.fmp.sms.domain.SmsParam;

@RestController
@RequestMapping("drawService")
public class DrawController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Value("${mas.send.url}")
	private String masSendUrl;
	
	@Value("${mas.sendV2.url}")
	private String masSendV2Url;
	
	@Value("${reportscanService.queryCredit.url}")
	private String creditUrl;
	
	@Value("${contractService.creditCotract.url}")
	private String creditCotractUrl;
	
	@Value("${drawService.validateRisk.url}")
	private String validateRisUrl;
	
	@Value("${userService.open.url}")
	private String openUrl;
	
	@Value("${service.six.rate}")
	private String sixServiceRate;
	
	@Value("${manage.six.rate}")
	private String sixManageRate;
	
	@Value("${manage.twl.rate}")
	private String twlManageRate;
	
	@Value("${service.twl.rate}")
	private String twlServiceRate;
	
	@Autowired
	private AppConfig appConfig;

	@Autowired
	private DrawService drawService;
	
	@Autowired
	private CreditService creditService;
	
	@Autowired
	private SMSSendService smsSendService;

	@Autowired
	private RestTemplate restTemplate;

	@RequestMapping("/apply")
	String apply(@RequestBody JSONObject param) throws Exception {
		// TODO 通过request参数获取recordNo获取授信信息
		logger.info("=======申请支用======={}", param);
		JSONObject resultJO = new JSONObject();
		String recordNo = param.getString("recordNo");
		String timeLimit = param.getString("timeLimit");
		String usercode = param.getString("usercode");
		String merchantNo = param.getString("merchantNo");
		String lmtserno = param.getString("lmtserno");
		String prodcode = param.getString("prodcode");
		if ("".equals(recordNo) || "".equals(timeLimit) || "".equals(usercode)
				|| "".equals(lmtserno) || "".equals(prodcode) ||
				"".equals(merchantNo)) {
			resultJO.put("error", "输入参数有误");
			resultJO.put("code", "01");
			 return resultJO.toJSONString();
		}
		// TODO 获得tlb_user_oid
		Map userInfo = drawService.queryUserInfo(usercode);
		if (userInfo == null) {
			resultJO.put("error", "获取tlb_user_oid失败");
			resultJO.put("code", "01");
			 return resultJO.toJSONString();
		}
		Map<String, String> P020Map =  new TreeMap<String,String>();
		P020Map.put("interfacecode", "P020");
		P020Map.put("userid", userInfo.get("tlbUserOid").toString());
		MultiValueMap<String, String> multiValueMap = this.getParamLess(P020Map);
		logger.info("P020 params {}", multiValueMap);
		String jsonStr = URLDecoder.decode(restTemplate.postForObject(openUrl, multiValueMap, String.class), "UTF-8");
		logger.info("P020's result is {}", jsonStr);
		
		if(jsonStr == null || "".equals(jsonStr)){
			resultJO.put("code", "000002");
			resultJO.put("message", "P020's return jsonStr is null or empty!");
			logger.info("P020's return jsonStr is null or empty!");
			return JSON.toJSONString(resultJO);
		}
		
		Map<String, Object> ret = (Map<String, Object>)JSONObject.parseObject(jsonStr,Map.class);
		if(ret == null || !"true".equals(ret.get("isSuccess"))){
			resultJO.put("code", "000002");
			resultJO.put("message", "P020 is failure !");
			logger.info("P020 is failure !");
			return JSON.toJSONString(resultJO);
		}
		Map<String, Object> retMap = (Map<String, Object>)ret.get("response");
		String interfaceretcode = (String)retMap.get("code");
		String openId = "";
		if ("W-Y000000".equals(interfaceretcode)) {
			openId = (String) retMap.get("openid");
		} else {
			resultJO.put("code", "000002");
			resultJO.put("message", "P020 is failure !");
			logger.info("P020 is failure !");
			return JSON.toJSONString(resultJO);
		}
		
		// TODO 判断是否通过货前
		Map<String, String> natrustParams = new HashMap<String, String>();
		natrustParams.put("merchantNo", merchantNo);
		natrustParams.put("lmtserno", lmtserno);
		natrustParams.put("prodcode", prodcode);
		natrustParams.put("usercode", openId);
		JSONObject tlbJO = this.getTlbInterface(natrustParams);
		if (tlbJO != null && !tlbJO.isEmpty()) {
			JSONObject res = tlbJO.getJSONObject("response");
			if (res != null && !res.isEmpty()
					&& "W-Y000000".equals(res.getString("code"))
					&& "0".equals(res.getString("checkresult"))) {
				// TODO 获取支用信息
				Map<String, Object> creditMap = this.formatApply(recordNo,
						timeLimit);
				logger.info("=======获取支用信息=======" + creditMap);
				// TODO 通过贷前检查
				// TODO 同步贷款信息
				creditMap.put("timeLimit", timeLimit);
				String fileId = "";
				Map protocolData = drawService.getProtocolData(recordNo, timeLimit);
				if (protocolData == null || protocolData.isEmpty()) {
					JSONObject natrustJO = this.getNatrustData(creditMap);
					String errCode = natrustJO.getString("natrustJO");
					if (!natrustJO.isEmpty() && !"02".equals(errCode)) {
						fileId = natrustJO.getString("fileId");
						
					} else {
						resultJO.put("errMsg", "同步信托信息管理失败");
						resultJO.put("code", "01");
						return resultJO.toJSONString();
					}
				} else {
					creditMap.put("fileId", protocolData.get("originalPdffileId"));
					creditMap.put("protocolNum", protocolData.get("protocolNo"));
					creditMap.put("byte", "");
					creditMap.put("batchNo", protocolData.get("batchNo"));
					creditMap.put("useRecordNo", protocolData.get("useRecordNo"));
					fileId = (String) protocolData.get("originalZipfileId");
				}
				JSONObject contractMap = new JSONObject();
				contractMap.put("creditRecordNo", recordNo);
				contractMap.put("protocolNo", creditMap.get("protocolNum"));
				contractMap.put("fileId", fileId);
				contractMap.put("batchNo", creditMap.get("batchNo"));
				contractMap.put("applTerm", timeLimit);
				contractMap.put("useRecordNo", creditMap.get("useRecordNo"));
				logger.info("=======contractMap=======>>>>>>>>{}",contractMap);
				JSONObject createContractStr = restTemplate.postForEntity(creditCotractUrl, contractMap, JSONObject.class).getBody();
				//logger.info("=======creditCotractUrl=======>>>>>>>>{}",createContractStr);
				String code = createContractStr.getString("code");
				if (createContractStr != null && !createContractStr.isEmpty() && !"".equals(code) &&
						"000000".equals(code)) {
					logger.info("code is 99999999  {}", createContractStr.getString("path"));
					creditMap.put("fileId", createContractStr.getString("fileId"));
					creditMap.put("byte", createContractStr.getString("byte"));
				} else {
					resultJO.put("errMsg", "同步信托信息管理失败");
					resultJO.put("code", "01");
					return resultJO.toJSONString();
				}
				creditMap.put("userCode", usercode);
				creditMap.put("recordNo", recordNo);
				resultJO.put("appData", creditMap);
			} else {
				// TODO 未通过返回异常
				resultJO.put("errMsg", "调用货前管理校验失败");
				resultJO.put("code", "01");
				return resultJO.toJSONString();
			}
		}
		JSONObject showData = new JSONObject();
		showData.put("apply_tnr", timeLimit);
//		Map<String, Object> creditMap = this.formatApply(recordNo,
//				timeLimit);
		resultJO.put("code", "0000");
		return resultJO.toJSONString();
	}
	
	@RequestMapping("/getBatchNo")
	String getBatchNo(@RequestBody JSONObject param) {
		JSONObject response = new JSONObject();
		String recordNo = param.getString("recordNo");
		String timeLimit = param.getString("timeLimit");
		Map protocolData = drawService.getProtocolData(recordNo, timeLimit);
		if (protocolData == null || protocolData.isEmpty()) {
			response.put("errMsg", "获取batch no失败");
			response.put("code", "9999");
		} else {
			response.put("code", "0000");
			response.put("batchNo", protocolData.get("batchNo"));
			response.put("useRecordNo", protocolData.get("useRecordNo"));
		}
		return response.toJSONString();
	}
	
	@RequestMapping("/ajaxSendVerifyCode")
	String ajaxSendVerifyCode(@RequestBody JSONObject param) {
		JSONObject response = new JSONObject();
		try {
			logger.info(">>>drawService/ajaxSendVerifyCode,生成手机验证码{}", param.toJSONString());
			String validateCode = RadmonUtil.radmonPassword(4);
			//发送短信
			String smsContent = "短信验证码："+validateCode;
			logger.info("smsContent :" + smsContent);
			SmsParam smsParam = new SmsParam();
			smsParam.setInstCode("allinpay");
			smsParam.setContent(smsContent);
			smsParam.setMobiles(param.getString("mobile"));
			
			CommonParameterHolder commonParam = new CommonParameterHolder();
			commonParam.setValue("ACTOR_ID", "SYSTEM");
			
			boolean blnSendRlt = smsSendService.sendSMS(smsParam, commonParam);
			logger.info("smsSendService>>>,{}", blnSendRlt);
			if(blnSendRlt){
				response.put("verifyCode", validateCode);
				response.put("code", "000000");
			}else{
				response.put("code", "000003");
			}
		} catch (Exception e) {
			response.put("code", "000003");
			logger.info(">>>drawService/ajaxSendVerifyCode exception===={}", e);
		}

		return response.toJSONString();
	}

	@RequestMapping("/calFee")
	String calFee(@RequestBody JSONObject param) {
		logger.info(">>>drawService/calFee,生成服务费与管理费{}", param.toJSONString());
		float amt = param.getLongValue("amt");
		
		JSONObject feeMap = new JSONObject();
		if (amt == 0) {
			feeMap.put("error", "输入参数有误");
			feeMap.put("code", "9999");
		}
		feeMap.put("sixServiceFee", new BigDecimal(param.getString("amt")).multiply(new BigDecimal(sixServiceRate)).setScale(2,BigDecimal.ROUND_HALF_UP));
		feeMap.put("sixManageFee", new BigDecimal(param.getString("amt")).multiply(new BigDecimal(sixManageRate)).setScale(2,BigDecimal.ROUND_HALF_UP));
		feeMap.put("twlServiceFee", new BigDecimal(param.getString("amt")).multiply(new BigDecimal(twlServiceRate)).setScale(2,BigDecimal.ROUND_HALF_UP));
		feeMap.put("twlManageFee", new BigDecimal(param.getString("amt")).multiply(new BigDecimal(twlManageRate)).setScale(2,BigDecimal.ROUND_HALF_UP));
		feeMap.put("code", "0000");
		return feeMap.toJSONString();
	}
	
	@RequestMapping("/queryUseInfo")
	String queryUseInfo(@RequestBody JSONObject param) {
		logger.info("draw/queryUseInfo params {}", param);
		Draw draw = JSONObject.toJavaObject(param, Draw.class);
		JSONObject response = new JSONObject();
		try {
			Draw useInfo = drawService.queryUseInfo(draw);
			logger.info("queryUseInfo result {}", useInfo);
			if (useInfo == null) {
				response.put("code", "0001");
			} else {
				response.put("code", "0000");
				response.put("data", useInfo);
			}
		} catch (Exception e) {
			response.put("code", "9999");
			response.put("errMsg", "查询支用信息失败");
			logger.info("queryUseInfo exception, {}", e);
		}
		return response.toJSONString();
	}

	@RequestMapping("/confirm")
	String applyCommit(@RequestBody JSONObject param) {
		logger.info("=======申请支用签约======={}", param);
		JSONObject response = new JSONObject();
		String recordNo = param.getString("recordNo");
		String withholdProtocol = param.getString("protocolNum");
		String timeLimit = param.getString("timeLimit");
		String usercode = param.getString("usercode");
		String useRecordNo = param.getString("useRecordNo");
		
		if (recordNo == null || "".equals(recordNo)
				|| timeLimit == null || "".equals(timeLimit)
				|| usercode == null || "".equals(usercode) ||
				useRecordNo == null || "".equals(useRecordNo)) {
			response.put("code", "9999");
			response.put("errMsg", "签约服务失败，参数不全。");
			return response.toJSONString();
		}
		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		map.add("recordNo", recordNo);
		String result = restTemplate.postForObject(creditUrl, map, String.class);
		JSONObject resultJO = JSON.parseObject(result);
		if (resultJO == null || resultJO.isEmpty()) {
			response.put("code", "9999");
			response.put("errMsg", "获取授信信息失败");
			return response.toJSONString();
		} else if ("".equals(resultJO.getString("code")) || !"000000".equals(resultJO.getString("code"))) {
			response.put("code", "9999");
			response.put("errMsg", "获取授信信息失败");
			return response.toJSONString();
		}
		String creditList = resultJO.getString("creditList");
		JSONArray creditList1 = JSON.parseArray(creditList.toString());
		JSONObject credit = creditList1.getJSONObject(0);
		if (credit == null || credit.isEmpty()) {
			response.put("code", "9999");
			response.put("errMsg", "获取授信信息失败");
			return response.toJSONString();
		}
		try{
			Draw draw = new Draw();
			draw.setRecordNo(useRecordNo);
			draw.setCreditRecordNo(recordNo);
			draw.setAcctId(credit.getString("acctId"));
			draw.setOrgCode(credit.getString("orgCode"));
			draw.setProdCode(credit.getString("prodCode"));
			draw.setLmtSerno(credit.getString("lmtSerno"));
			draw.setUseDate(this.getDate1("0", 1));
			String accRecordNo = drawService.getAcctNo(credit.getString("bankAcctNo"));
			draw.setRetuAcno(accRecordNo);
			draw.setLoanCustName(credit.getString("custName"));
			draw.setLoanCerttypeName("身份证");
			draw.setLoanCertNo(credit.getString("certNo"));
			draw.setLoanCustAddr(credit.getString("liveAddr"));
			draw.setLoanPhone(credit.getString("mobile"));
			draw.setStatus("0");
			draw.setUseSignFlag("00");
			
			draw.setAppStartDate(this.getDate1("0", 1));
			if ("6".equals(timeLimit)) {
				draw.setAppAmt(new BigDecimal(credit.getString("sixMonthAmt")));
				draw.setLoanBalance(new BigDecimal(credit.getString("sixMonthAmt")));
				draw.setServiceFee(new BigDecimal(credit.getString("sixMonthAmt")).multiply(new BigDecimal(sixServiceRate)).setScale(2,BigDecimal.ROUND_HALF_UP));
				draw.setManagerFee(new BigDecimal(credit.getString("sixMonthAmt")).multiply(new BigDecimal(sixManageRate)).setScale(2,BigDecimal.ROUND_HALF_UP));
				draw.setBaseAmount(new BigDecimal(credit.getString("sixMonthAmt")));
				draw.setApplTerm("6M");
				draw.setAppEndDate(this.getDate1("1", 6));
				draw.setLoanYrate(new BigDecimal(sixManageRate).setScale(2,BigDecimal.ROUND_HALF_UP));
			} else if ("12".equals(timeLimit)) {
				draw.setAppAmt(new BigDecimal(credit.getString("twelveMonthAmt")));
				draw.setLoanBalance(new BigDecimal(credit.getString("twelveMonthAmt")));
				draw.setServiceFee(new BigDecimal(credit.getString("twelveMonthAmt")).multiply(new BigDecimal(twlServiceRate)).setScale(2,BigDecimal.ROUND_HALF_UP));
				draw.setManagerFee(new BigDecimal(credit.getString("twelveMonthAmt")).multiply(new BigDecimal(twlManageRate)).setScale(2,BigDecimal.ROUND_HALF_UP));
				draw.setBaseAmount(new BigDecimal(credit.getString("twelveMonthAmt")));
				draw.setApplTerm("12M");
				draw.setAppEndDate(this.getDate1("1", 12));
				draw.setLoanYrate(new BigDecimal(twlManageRate).setScale(2,BigDecimal.ROUND_HALF_UP));
			}
			draw.setLoanPayway("2");
			draw.setUseSignTime(System.currentTimeMillis() + "");
			draw.setWithholdProtocol(withholdProtocol);
			draw.setWithholdProtocolTime(System.currentTimeMillis() + "");
			draw.setCreateUid(usercode);
			draw.setCreateTime(System.currentTimeMillis() + "");
			draw.setLastModifyUid(usercode);
			draw.setLastModifyTime(System.currentTimeMillis() + "");

			// String status = request.getParameter("status");
			// boolean ret = false;
			// //===========commService.checkVerifyCode 验证验证码 start===========
			// Map<String, String> paramMap = new HashMap<String, String>();
			// paramMap.put("verifyNo", verifyNo);
			// paramMap.put("type", "1");
			// paramMap.put("verifyCode", verifyCode);
			// MessageResponse retMessage = BaseService.sendMessage(paramMap,
			// "commService.checkVerifyCode");
			// // 处理成功或受理成功
			//
			// if (RetCode.RETCODE_000000.getCode().equals(retMessage.getRetCode())
			// || RetCode.RETCODE_000001.getCode().equals(retMessage.getRetCode()))
			// {
			// ret = true;
			// } else {
			// ret = false;
			// }

			// TODO 保存支用信息
			drawService.saveDrawData(draw);
			logger.info("=======draw/save=======" + draw.getRecordNo());
			Charge charge = new Charge();
			charge.setRecordNo(GenerateIdsUtil.generateId(appConfig.getAppIp()));
			charge.setUseRecordNo(draw.getRecordNo());
			charge.setAcctId(draw.getAcctId());
			charge.setCurDate(draw.getUseDate());
			charge.setBaseAmount(draw.getAppAmt());
			if ("6".equals(timeLimit)) {
				charge.setServiceRate(new BigDecimal(sixServiceRate));
				charge.setManagerRate(new BigDecimal(sixManageRate));
			} else {
				charge.setServiceRate(new BigDecimal(twlServiceRate));
				charge.setManagerRate(new BigDecimal(twlManageRate));
			}
			charge.setServiceFee(draw.getServiceFee());
			charge.setManagerFee(draw.getManagerFee());
			charge.setTolamount(draw.getServiceFee().add(draw.getManagerFee()));
			charge.setCreateUid(draw.getCreateUid());
			charge.setCreateTime(draw.getCreateTime()+"");
			charge.setLastModifyUid(draw.getLastModifyUid());
			charge.setLastModifyTime(draw.getLastModifyTime()+"");
			charge.setDeductCount("0");
			charge.setProcState("00");
			// TODO 保存手续费信息, 保存到信托
			drawService.saveCharge(charge);
			
			response.put("code", "000000");
			response.put("message", "success");
        }catch (Exception e) {
        	response.put("code", "000002");
        	response.put("message", "error");
			logger.info("drawService/confirm error : ",e);
		}
		
		return response.toJSONString();
	}
	
	@RequestMapping(value="/queryCharge", method = RequestMethod.POST)
	@ResponseBody
	String queryCharge(@RequestBody JSONObject param) {
		logger.info("service method {}", "drawService/queryCharge");
		JSONObject result = new JSONObject();
		
		try{
			Charge charge = JSON.parseObject(param.toString(), Charge.class);
			List<Charge> chargeList = drawService.queryChargeList(charge);
			logger.info("service query chargeList result {}",JSON.toJSONString(chargeList));
			
			result.put("chargeList", chargeList);
			result.put("code", "000000");
			result.put("message", "success");
        }catch (Exception e) {
        	result.put("code", "000002");
			result.put("message", "error");
			logger.info("drawService/queryCharge error : ",e);
		}
		return result.toJSONString();
	}
	
	@RequestMapping(value="/updateUse", method = RequestMethod.POST)
	@ResponseBody
	String updateUse(@RequestBody JSONObject param) {
		logger.info("service method {}", "drawService/updateUse");
		logger.info("drawService/updateUse, param====>{}", param);
		JSONObject result = new JSONObject();
		
		try{
			Draw charge = JSON.parseObject(param.toString(), Draw.class);
			drawService.updateUse(charge);
			logger.info("service update use success");
			
			result.put("code", "000000");
			result.put("message", "success");
        }catch (Exception e) {
        	result.put("code", "000002");
			result.put("message", "error");
			logger.info("drawService/updateCharge error : ",e);
		}
		return result.toJSONString();
	}
	
	@RequestMapping(value="/updateCharge", method = RequestMethod.POST)
	@ResponseBody
	String updateCharge(@RequestBody JSONObject param) {
		logger.info("service method {}", "drawService/updateCharge");
		logger.info("drawService/updateCharge, param====>{}", param);
		JSONObject result = new JSONObject();
		
		try{
			Charge charge = JSON.parseObject(param.toString(), Charge.class);
			drawService.updateCharge(charge);
			logger.info("service update charge success");
			
			result.put("code", "000000");
			result.put("message", "success");
        }catch (Exception e) {
        	result.put("code", "000002");
			result.put("message", "error");
			logger.info("drawService/updateCharge error : ",e);
		}
		return result.toJSONString();
	}

	private JSONObject getTlbInterface(Map<String, String> params) {
		JSONObject tlbResult = new JSONObject();
		Map<String, String> paramCheck = new TreeMap<String, String>();
		for (Map.Entry<String, String> entry : params.entrySet()) {
			paramCheck.put(entry.getKey(), entry.getValue());
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		paramCheck.put("terminalno", "sdfds");// 接入方
		paramCheck.put("accesscode", "weixin");
		paramCheck.put("usertype", "P");
		paramCheck.put("channel", "W");
		paramCheck.put("signType", "MD5");
		paramCheck.put("version", "V1.0");
		paramCheck.put("partner", "uap");
		paramCheck.put("inputCharset", "UTF-8");
		paramCheck.put("timestamp", sdf.format(new Date()));
		StringBuilder sb = new StringBuilder();
		Iterator<String> iterator = paramCheck.keySet().iterator();
		while (iterator.hasNext()) {
			sb.append(paramCheck.get(iterator.next()) + "|");
		}
		String source = sb.toString();
		if (!StringUtils.isEmpty(source)) {
			source = source + "1234567890";
		}

		String sourceSign = "";
		try {
			sourceSign = DigestUtils.md5Hex((source).getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			// TODO 添加异常code返回
			tlbResult.put("code", "9999");
			tlbResult.put("errMsg", "系统异常");
			e1.printStackTrace();
			return tlbResult;
		}

		paramCheck.put("sign", sourceSign);
		MultiValueMap<String, String> P001Map1 = new LinkedMultiValueMap<String, String>();
		for (Map.Entry<String, String> entry : paramCheck.entrySet()) {
			P001Map1.add(entry.getKey(), entry.getValue());
		}
		String result = restTemplate.postForObject(validateRisUrl, P001Map1,
				String.class);
		try {
			result = URLDecoder.decode(result, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		logger.info("调用贷前管理校验接口url:" + validateRisUrl);
		logger.info("调用贷前管理校验接口param:" + paramCheck);
		logger.info("调用贷前管理校验接口res:" + result);
		tlbResult = JSON.parseObject(result);
		return tlbResult;
	}
	
	private MultiValueMap<String, String> getParamLess(Map<String, String> paramsMap) throws Exception
    {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss"); 
		paramsMap.put("terminalno", "sdfds");//接入方
		paramsMap.put("accesscode", "weixin");
		paramsMap.put("usertype", "P");
		paramsMap.put("channel", "W");
		paramsMap.put("signType", "MD5");
		paramsMap.put("version", "V1.0");
		paramsMap.put("partner", "uap");
		paramsMap.put("inputCharset", "UTF-8");
		paramsMap.put("timestamp", sdf.format(new Date()));
        StringBuilder sb = new StringBuilder();
        Iterator<String> iterator = paramsMap.keySet().iterator();
        while (iterator.hasNext())
        {
            sb.append(paramsMap.get(iterator.next()) + "|");
        }
        String source = sb.toString();
        if (!StringUtils.isEmpty(source))
        {
            source = source + "1234567890";
        }
        
        String sourceSign = DigestUtils.md5Hex((source).getBytes("UTF-8"));
        
        paramsMap.put("sign", sourceSign);
         
        MultiValueMap<String, String> multiValueMap =  new LinkedMultiValueMap<String, String>();
        
        for(Map.Entry<String, String> entry : paramsMap.entrySet()){
        	multiValueMap.add(entry.getKey(), entry.getValue());
        }

        return multiValueMap;
    }

	private JSONObject getNatrustData(Map<String, Object> creditMap) {
		JSONObject jo = new JSONObject();
		// TODO 同步贷款信息
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		JSONObject uriVariables = new JSONObject();
		JSONObject base_info = new JSONObject();
		JSONObject personal_info = new JSONObject();
		JSONObject loan_info = new JSONObject();
//		JSONObject payment_info = new JSONObject();
//		JSONArray paymentList = new JSONArray();
		this.prepareBaseInfo(base_info, creditMap);
		this.preparePersonal(personal_info, creditMap);
		this.prepareLoanInfo(loan_info, creditMap);
		// this.preparePayment(payment_info, creditMap);
		// paymentList.add(payment_info);
		uriVariables.put("base_info", base_info.toJSONString());
		uriVariables.put("personal_info", personal_info.toJSONString());
		uriVariables.put("loan_info", loan_info.toJSONString());
		uriVariables.put("payment_info", "");
		uriVariables.put("orgCode", "21100005");
		uriVariables.put("prodCode", "8007000001");
		// sysTraceUID
		uriVariables.put("sysTraceUID", GenerateIdsUtil.sysTraceUID());
//		uriVariables.add("sysTraceUID", "15125433925350000011237");
		uriVariables.put("transCode", "03");

		params.add("topicName", "loan.orgGateway");
		params.add("messgeJsonBody", uriVariables.toJSONString());
		String result = restTemplate.postForObject(masSendV2Url, params,
				String.class);
		logger.info("调用信托贷前管理：", masSendV2Url + "/loan.orgGateway");
		logger.info("调用信托贷前管理参数：" + params);
		logger.info("调用信托贷前管理结果：" + result);
		JSONObject resultNa = JSON.parseObject(result);
		if (resultNa != null && !resultNa.isEmpty()) {
			if (!"W-Y000000".equals(resultNa.getString("resultCode"))) {
				jo.put("code", "9999");
				jo.put("errMsg", resultNa.getString("resultMessage"));
			} else {
				if ("0".equals(jo.getString("status"))) {
					jo.put("code", "9999");
					jo.put("errMsg", jo.getString("errInfo"));
				} else {
					jo = resultNa;
				}
			}
		} else {
			jo.put("code", "9999");
			jo.put("errMsg", "货前管理服务调用异常");
		}
		return jo;
	}

	private void prepareLoanInfo(JSONObject map, Map<String, Object> creditMap) {
		// TODO createTime
		long createTime = Long.parseLong(creditMap.get("createTime").toString());
		SimpleDateFormat sdf= new SimpleDateFormat("yyyyMMdd");  
        java.util.Date date = new Date(createTime / 1000);  
        String lmtEndData = sdf.format(date);
		String app_data = lmtEndData.substring(0, 4) + "-" + lmtEndData.substring(4, 6) + 
				"-" +lmtEndData.substring(6, 8);
		map.put("app_date", app_data); // 申请日期(授信)
		map.put("app_location", "北京"); // 贷款申请地点
		
		String apply_amt = creditMap.get("appl_amt").toString();
		map.put("apply_amt", new BigDecimal(Double.valueOf(apply_amt)).multiply(new BigDecimal(100)).setScale(0,BigDecimal.ROUND_HALF_UP).toString()); // 贷款金额(分)
		map.put("apply_tnr", creditMap.get("apply_tnr")); // 贷款期限_按月,以月为单位
		map.put("apr", creditMap.get("pay_rate")); // 贷款年利率
//		map.put("borrow_deposit_fee", "1000"); // 保证金
		map.put("daily_apr", creditMap.get("pay_rate")); // 贷款日利率
//		map.put("dis_acc_bank_cde", "102");
//		map.put("dis_acc_bank_city", "北京"); // 借款人开户行所在城市
		map.put("dis_acc_bank_name", creditMap.get("bank_name")); // 借款人开户行
		
//		map.put("dis_acc_bank_province", "北京"); // 借款人开户行所在省份
		map.put("dis_acc_kind", "11"); // 借款人账户性质,固定值11
		map.put("dis_acc_name", creditMap.get("cust_name")); // 借款人账户名
		map.put("dis_acc_no", creditMap.get("bank_acct_no")); // 借款人账户号
		String limitDate = this.getDate("1", Integer.parseInt(creditMap.get("apply_tnr").toString()));
		map.put("due_date", limitDate); // 贷款到期日期
//		map.put("duration_days", "20"); // 贷款天数
//		map.put("family_knowing", "是"); // 家人是否知晓，是或否
//		map.put("first_repayment", "100000"); // 首期还款金额
//		map.put("guarantee_amt", "1000"); // 担保金额
		map.put("guarantee_status", "0"); // 担保状态，固定值0
//		map.put("guarantor_id_no", "1000000000000000"); // 担保人证件号码
//		map.put("guarantor_id_type", "0"); // 担保人证件类型
//		map.put("guarantor_name", "李四"); // 担保人姓名
//		map.put("hrd_amt", "100"); // 平台贴息金额
//		map.put("hrd_pct", "0.12");
//		map.put("last_repayment", "100000"); // 最后一期还款
		map.put("lending_date", creditMap.get("applyDate")); // 首次放款日期
		map.put("loan_purpose", "经营性贷款"); // 贷款用途，固定值经营性贷款
		map.put("loan_type", "91"); // 贷款类型,固定值41
		map.put("monthly_apr", creditMap.get("pay_rate")); // 贷款月利率
//		map.put("monthly_repayment", "100000"); // 每月还款额
		map.put("mtd_cde", "2"); // 还款方式
		map.put("payment_frequenc", "03"); // 还款频率,固定值03
//		map.put("repayment_date", "贷款发放日的对日"); // 还款日
		String service_fee = creditMap.get("serviceFee").toString();
		BigDecimal cenData = new BigDecimal(Double.valueOf(service_fee)).multiply(new BigDecimal(100)).setScale(0,BigDecimal.ROUND_HALF_UP);
		map.put("service_fee", cenData.toString()); // 服务费
//		map.put("sysb_amt", "100"); // 合作商贴息金额
//		map.put("sysb_pct", "0.12");
//		map.put("sysb_tnr", "36"); //合作商贴息期限，单位月
//		map.put("total_int", "10000000"); // 应还利息总额
		map.put("lcontract_needed", "1"); // 是否需要贷款合同
	}

	private void prepareBaseInfo(JSONObject map, Map<String, Object> creditMap) {
		// 生成一个新的支用表recordNo
		String app_seq = GenerateIdsUtil.generateId(appConfig.getAppIp());
		creditMap.put("useRecordNo", app_seq);
		map.put("app_seq", app_seq);
		String batchNo = "A" + String.valueOf(System.currentTimeMillis());
		creditMap.put("batchNo", batchNo);
		map.put("batch_no", batchNo); //批次号
//		map.put("comments", "备注");
//		map.put("company_name", "TESTC");
		String protocolNum= GenerateIdsUtil.protocolId("4");
		map.put("contract_num", protocolNum); // 合同编号
		creditMap.put("protocolNum", protocolNum);
		map.put("coopr_code", creditMap.get("merchant_no").toString()); // 商户代码
		map.put("coopr_name", "**商户"); // 商户名称
//		map.put("project_name", "测试项目"); // 
//		map.put("project_num", "1"); 
		map.put("applicant_type", "1"); // 贷款主体 (默认为1-自然人)
	}

	private void preparePersonal(JSONObject map, Map<String, Object> creditMap) {
		String iden = creditMap.get("cert_no").toString();
		String dates = iden.substring(6, 10);
        SimpleDateFormat df = new SimpleDateFormat("yyyy");
        String year=df.format(new Date());
        int age =Integer.parseInt(year)-Integer.parseInt(dates);
		map.put("appt_age", age); // 年龄
		String birthday = iden.substring(6, 10) + "-" + iden.substring(10, 12) + "-" + iden.substring(12, 14);
		map.put("birthday", birthday); // 出生日期，格式为YYYY-MM-DD
//		map.put("child_count", "1"); // 有无子女
		map.put("cust_name", creditMap.get("cust_name")); // 申请人姓名
//		map.put("email", "zhangsan@126.com"); //电子邮箱
//		map.put("highest_dgredd", "3"); // 最高学位
//		map.put("highest_education", "20");  // 最高学历
//		map.put("home_phone", "010-00000000"); //住宅电话
		map.put("id_no", iden); // 证件号码
		map.put("id_type", "0"); // 证件类型，必须是0-身份证
		map.put("indiv_rsd_address", creditMap.get("live_addr")); // 居住地址
//		map.put("indiv_rsd_postalcode", "1000000"); // 居住地址邮政编码
		map.put("mailing_address", creditMap.get("live_city")); //通讯地址，包含省市县（区）等详细信息
//		map.put("mailing_postalcode", "1000000"); // 通讯地址邮政编码
		map.put("marital_status", creditMap.get("marriag")); // 婚姻状况 
		map.put("mobile", creditMap.get("mobile")); // 手机号码
		map.put("permanent_address", creditMap.get("live_city")); // 户籍地址
//		map.put("real_estate_info", "1"); // 居住状况
		String sGender = "未知";
        String sCardNum = iden.substring(16, 17);
        if (Integer.parseInt(sCardNum) % 2 != 0) {
            sGender = "1";//男
        } else {
            sGender = "2";//女
        }
		map.put("sex", sGender); // 性别
//		map.put("spouse_company", "**公司"); // 配偶工作单位
//		map.put("spouse_id_no", "6220000000000000"); // 配偶证件号码
//		map.put("spouse_id_type", "0"); // 配偶证件类型
//		map.put("spouse_name", "李四"); // 配偶姓名
//		map.put("spouse_phone", "13100000000"); // 配偶联系电话
//		map.put("used_name", "王五"); 
//		map.put("work_telephone", "010-00000000"); //单位电话
	}

	private void preparePayment(JSONObject map, Map<String, Object> creditMap) {
//		map.put("payee_bankb_province", "北京"); // 收款人分支行所在省份
		map.put("app_seq", creditMap.get("lmt_serno")); // 申请流水号
		map.put("payee_id_no", creditMap.get("cert_no")); // 收款人证件号码
		map.put("payment_model", "200"); // 放款模式,指示支付通道
		map.put("total_num", "1"); // 放款总步数
		map.put("payee_mobile", creditMap.get("mobile")); // 收款人手机号
		map.put("settlement_flag", "0001"); // 收款账户结算标示符
		map.put("payment_purpose", "101"); // 本步骤用途
		String payment_amt = Float.parseFloat(creditMap.get("appl_amt").toString())*100 + "";
		map.put("payment_amt", payment_amt); // 本步骤的放款额
		String total_amt = Float.parseFloat(creditMap.get("appl_amt").toString())*100 + "";
		map.put("total_amt", total_amt); // 放款总额，单位分
		map.put("payee_act_num", creditMap.get("bank_acct_no")); // 收款人账户号码
		map.put("batch_no", "20170208007");
		map.put("payee_bank_name", creditMap.get("bank_name")); // 收款人开户行
		map.put("payment_no", "1"); // 放款步骤序号
//		map.put("payee_card_thr", "");
//		map.put("payee_email", "aa@126.com");
		map.put("payee_act_name", creditMap.get("cust_name")); // 收款人账户名称
//		map.put("payee_bankb_name", "北京银行国贸分行");  // 收款人分支行名称
		map.put("payee_act_type", "11"); //收款人账户类型，固定值11
//		map.put("payee_bankb_city", "北京"); // 收款人分支行所在城市
		map.put("payee_card_exp", "");
		map.put("payee_bank_code", creditMap.get("bank_code")); // 收款人开户行银行编码,
		map.put("payee_id_type", "0"); // 收款人证件类型，固定值0
		map.put("comment", "10"); // 备注,主要用于测试
		map.put("payment_type", "13"); // 本步骤类型
	}

	private Map<String, Object> formatApply(String recordNo, String timeLimit) {
		Map<String, Object> map = new HashMap<String, Object>();
		map = drawService.getReq(recordNo, timeLimit);
		logger.info("drawService getReq====>{}", map);
		String applyDate = this.getDate("0", 0);
		String limitDate = this.getDate("1", Integer.parseInt(timeLimit));
		map.put("applyDate", applyDate);
		map.put("limitDate", applyDate + "至" + limitDate);
		// todo 调用服务获取服务费
		JSONObject feeParam = new JSONObject();
		feeParam.put("amt", map.get("appl_amt"));
		String feeStr = this.calFee(feeParam);
		JSONObject feeJO = JSON.parseObject(feeStr);
		if ("6".equals(timeLimit) && "0000".equals(feeJO.getString("code"))) {
			map.put("manageFee", feeJO.getString("sixManageFee"));
			map.put("serviceFee", feeJO.getString("sixServiceFee"));
		} else if ("12".equals(timeLimit) && "0000".equals(feeJO.getString("code"))) {
			map.put("manageFee", feeJO.getString("twlManageFee"));
			map.put("serviceFee", feeJO.getString("twlServiceFee"));
		}
		map.put("apply_tnr", timeLimit);
		String bankName = (String) map.get("bank_name");
		String bankAcctNo = (String) map.get("bank_acct_no");
		map.put("lmBank",
				bankName + "(" + bankAcctNo.substring(bankAcctNo.length() - 4)
						+ ")");
		String status = (String) map.get("status");
		if ("1".equals(status)) {
			map.put("statusDesc", "申请未完成");
		} else if ("2".equals(status)) {
			map.put("statusDesc", "申请失效");
		} else if ("3".equals(status)) {
			map.put("statusDesc", "审批中");
		} else if ("4".equals(status)) {
			map.put("statusDesc", "审批失败");
		} else if ("5".equals(status)) {
			map.put("statusDesc", "审批通过");
		} else if ("6".equals(status)) {
			map.put("statusDesc", "额度生效");
		} else if ("7".equals(status)) {
			map.put("statusDesc", "额度失效");
		} else if ("8".equals(status)) {
			map.put("statusDesc", "审批作废");
		} else if ("9".equals(status)) {
			map.put("statusDesc", "额度暂停");
		} else if ("10".equals(status)) {
			map.put("status", "通华不准入");
		} else if ("11".equals(status)) {
			map.put("statusDesc", "征信未通过");
		} else if ("12".equals(status)) {
			map.put("statusDesc", "资方银行不准入");
		}
		return map;
	}

	private String getDate(String type, int months) {
		Calendar c = Calendar.getInstance();// 获得一个日历的实例
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date currentDate = new java.util.Date();
		String str = sdf.format(currentDate);
		Date date = null;
		if ("1".equals(type)) {
			try {
				date = sdf.parse(str);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			c.setTime(date);
			c.add(Calendar.MONTH, months);
			str = sdf.format(c.getTime());

		}
		System.out.println(str);
		return str;
	}
	
	private String getDate1(String type, int months) {
		Calendar c = Calendar.getInstance();// 获得一个日历的实例
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Date currentDate = new java.util.Date();
		String str = sdf.format(currentDate);
		Date date = null;
		if ("1".equals(type)) {
			try {
				date = sdf.parse(str);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			c.setTime(date);
			c.add(Calendar.MONTH, months);
			str = sdf.format(c.getTime());

		}
		System.out.println(str);
		return str;
	}
}