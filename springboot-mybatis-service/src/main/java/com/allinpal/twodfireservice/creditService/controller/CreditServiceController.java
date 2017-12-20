package com.allinpal.twodfireservice.creditService.controller;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import org.springframework.http.HttpMethod;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.allinpal.twodfireservice.AppConfig;
import com.allinpal.twodfireservice.creditService.domain.Credit;
import com.allinpal.twodfireservice.creditService.domain.ProdAcct;
import com.allinpal.twodfireservice.creditService.service.CreditService;
import com.allinpal.twodfireservice.creditService.util.GenerateIdsUtil;
import com.thfund.commons.utils.JsonUtils;

@RestController
@RequestMapping("creditService")
public class CreditServiceController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private AppConfig appConfig;
	
	@Autowired
	private CreditService creditService;

	@Autowired
	private RestTemplate restTemplate;
	
	@Value("${accessVerifyService.merchantVerify.url}")
	private String accessVerifyServiceMerchantVerifyUrl;
	
	@Value("${mas.send.url}")
	private String masSendUrl;
	
	@Value("${drawService.calFee.url}")
	private String drawServiceCalFeeUrl;
	
	@Value("${access.mutexCheck.url}")
	private String accessMutexCheckUrl;
	
	private static String RETCODE_SUCCESS="000000";
	
	@RequestMapping(value="/preapply", method = RequestMethod.POST)
	String preapply(Credit creditInfo) {
		logger.info("service method {}", "creditService/preapply");
		logger.info("service param {}", JSON.toJSONString(creditInfo));
		Map<String,String> request = new HashMap<String,String>();
		Map<String,String> response = new HashMap<String,String>();
		response.put("merchantNo", creditInfo.getMerchantNo());
		response.put("mobile", creditInfo.getMobile());
		response.put("prodCode", creditInfo.getProdCode());
		try{
			//校验参数
			if(!checkParam(creditInfo,"ruleAccess")){
				response.put("code", "100000");
				response.put("message", "参数错误");
				return  JSON.toJSONString(response);
			}
			//1、查询帐户表
			ProdAcct prodAcct = new ProdAcct();
			prodAcct.setMerchantNo(creditInfo.getMerchantNo());
			prodAcct.setProdCode(creditInfo.getProdCode());
			prodAcct.setMobile(creditInfo.getMobile());
			prodAcct = creditService.getAcctByMerchantNo(prodAcct);
			logger.info("creditService/preapply getAcctByMerchantNo"+JSON.toJSONString(prodAcct));
			if(null != prodAcct){
				response.put("acctId", prodAcct.getAcctId());
				response.put("custName", prodAcct.getLegalName());
				response.put("certNo", prodAcct.getCertNo());
				response.put("userId", prodAcct.getUserId());
			}else{
				logger.info("can't find data in t_prod_acct");
				response.put("code", "200000");
				response.put("message", "您暂时不能申请此贷款");
				return JSONObject.toJSONString(response);
			}
			
			//2、互斥校验
			MultiValueMap<String, Object> paramMap = new LinkedMultiValueMap<String, Object>();
			paramMap.add("userId", prodAcct.getUserId());
	        String result = restTemplate.postForObject(accessMutexCheckUrl, paramMap, String.class);
			logger.info("creditService/preapply accessMutexCheckUrl result is {}", result);	
			Map checkMap = JSONObject.parseObject(result, Map.class);
			String checkCode = (String) (checkMap.get("code")==null?"":checkMap.get("code"));
			if(!RETCODE_SUCCESS.equals(checkCode)){
				response.put("code", "300000");
				response.put("message", "您暂时不能申请此贷款");
				return JSONObject.toJSONString(response);
			}
			
			//3、准入校验			
	    	JSONObject map = new JSONObject(); 
			map.put("merchantNo", creditInfo.getMerchantNo());
			map.put("mobile", creditInfo.getMobile());
			map.put("prodCode", creditInfo.getProdCode());
	        JSONObject accessVerifyJson = restTemplate.postForEntity(accessVerifyServiceMerchantVerifyUrl, map, JSONObject.class).getBody();
			logger.info("preapply param {}", accessVerifyJson.toString());
			if("N".equals(accessVerifyJson.getString("isAccessCheckPass"))){
				response.put("code", "400000");
				response.put("message", "您暂时不能申请此贷款");
				return  JSON.toJSONString(response);
			}else if("Y".equals(accessVerifyJson.getString("isAccessCheckPass"))){
				response.put("registerDate", accessVerifyJson.getString("registerDate"));
			}

			//4、查询营业执照号
			request.put("entityId", creditInfo.getMerchantNo());
			List<Map<String, Object>> mchtlist = creditService.getLicenseNo(request);
			if(null != mchtlist && mchtlist.size() > 0){
				response.put("licenseNo", String.valueOf(mchtlist.get(0).get("license_code")));
				response.put("merName", String.valueOf(mchtlist.get(0).get("shop_name")));
			}
			
			//5、查询贷款银行卡
			request.put("recordNo", prodAcct.getUserCardId());
			List<Map<String, Object>> cardlist = creditService.getUserCard(request);
			if(null != cardlist && cardlist.size() > 0){
				response.put("bankName", cardlist.get(0).get("bank_iden_name").toString());
				response.put("bankCode", cardlist.get(0).get("bank_iden_code").toString());
				response.put("bankAcctNo", cardlist.get(0).get("acct_no").toString());
			}

			//6、查询用户表
			request.put("userId", prodAcct.getUserId());
			List<Map<String, Object>> userlist = creditService.getUserById(request);
			if(null != userlist && userlist.size() > 0){
				response.put("investorOid", userlist.get(0).get("investor_oid").toString());
			}
			
			response.put("code", "000000");
			response.put("message", "success");
		}catch (Exception e) {
			response.put("code", "000002");
			response.put("message", "系统繁忙，请稍后再试！");
			logger.error("creditService/preapply error : ",e);
		}
		logger.info("service response {}", JSON.toJSONString(response));
		return  JSON.toJSONString(response);
	}

	@RequestMapping(value="/checkEmpPhone", method = RequestMethod.POST)
	String checkEmpPhone(String phone) {
		logger.info("service method {}", "creditService/checkEmpPhone");
		logger.info("service param {}", phone);
		Map<String,String> response = new HashMap<String,String>();
		try{
			if(null == phone || "".equals(phone)){
				response.put("code", "100000");
				response.put("message", "参数错误");
				return  JSON.toJSONString(response);
			}
			//验证客户经理手机号
			MultiValueMap<String, String> empMap = new LinkedMultiValueMap<>();
			empMap.add("topicName", "operation.queryEmp");
			empMap.add("phone", phone);
			empMap.add("status", "1");
			logger.info("service queryEmp empMap {}",empMap);
			JSONObject empJson = restTemplate.postForEntity(masSendUrl, empMap, JSONObject.class).getBody();
			logger.info("service queryEmp result {}",empJson);
			if(!"1".equals(empJson.getString("totalCount"))){
				response.put("code", "W-AL10501");
				response.put("message", "客户经理手机号无效");
				return  JSON.toJSONString(response);
			}
			Map map = JsonUtils.toObject(empJson.toJSONString(), Map.class);
			List<Map> recordList = (List) map.get("recordList");
			if(!"Y".equals(recordList.get(0).get("isCompleteSign"))){
				response.put("code", "W-AL10502");
				response.put("message", "客户经理手机号无效");
				return  JSON.toJSONString(response);
			}
			
			response.put("code", "000000");
			response.put("message", "success");			
		}catch (Exception e) {
			response.put("code", "000002");
			response.put("message", "系统繁忙，请稍后再试！");
			logger.error("creditService/checkEmpPhone error : ",e);
		}
		return  JSON.toJSONString(response);
	}
	
	
	@RequestMapping(value="/apply", method = RequestMethod.POST)
	String submitApply(Credit creditInfo) {
		logger.info("service method {}", "creditService/apply");
		logger.info("service param {}", JSON.toJSONString(creditInfo));
        String result = null;
		Map<String,String> response = new HashMap<String,String>();
		response.put("merchantNo", creditInfo.getMerchantNo());
		response.put("mobile", creditInfo.getMobile());
		response.put("prodCode", creditInfo.getProdCode());
		response.put("code", "000002");
		response.put("message", "申请失败");
		try{
			//查询是否有审批中的授信
			Credit creditQuery = new Credit();
			creditQuery.setMerchantNo(creditInfo.getMerchantNo());
			creditQuery.setProdCode(creditInfo.getProdCode());
			creditQuery.setMobile(creditInfo.getMobile());
			creditQuery.setEffectiveStatus("I");
			List<Credit> creditList = creditService.getCreditList(creditQuery);
			if(null != creditList && creditList.size() > 0){
				response.put("code", "000003");
				response.put("message", "您已经有申请中的授信，请勿重新提交");
				return  JSON.toJSONString(response);
			}
			
        	creditInfo.setStatus("3");//3-审批中
        	creditInfo.setStatus2dfire("1");//1:待提取征信报告
			creditInfo.setCreateUid(creditInfo.getAcctId());
			creditInfo.setCreateTime(System.currentTimeMillis());
			creditInfo.setLastModifyUid(creditInfo.getAcctId());
			creditInfo.setLastModifyTime(System.currentTimeMillis());
			
			//1、调用loan.twoFireCreditInfoSynch
			MultiValueMap<String, Object> createReqMap = new LinkedMultiValueMap<>();
			createReqMap = getCreditReq(creditInfo);
			createReqMap.add("topicName", "loan.twoFireCreditInfoSynch");
			createReqMap.add("operType", "1");
			logger.info("service apply loan.twoFireCreditInfoSynch params {}",createReqMap);
			JSONObject createReqJson = restTemplate.postForEntity(masSendUrl, createReqMap, JSONObject.class).getBody();
			logger.info("service apply loan.twoFireCreditInfoSynch result {}",createReqJson);
			if(!"W-Y000000".equals(createReqJson.getString("resultCode"))){
        		logger.info("service creditService/apply loan.twoFireCreditInfoSynch {} ", "创建二代授信失败");
				return  JSON.toJSONString(response);
			}
			
			//2、保存授信信息2dfire.t_loan_credit_req
			creditInfo.setRecordNo(GenerateIdsUtil.generateId(appConfig.getAppIp()));
			creditInfo.setLmtSerno(createReqJson.getString("creditrecordno"));
			int i = creditService.saveLoanCreditReq(creditInfo);
			logger.info("service apply saveLoanCreditReq result {}",i);
			if(i == 1){
				response.put("creditInfo", JSON.toJSONString(creditInfo));
				response.put("code", "000000");
				response.put("message", "success");
			}
			
		}catch (Exception e) {
			logger.error("creditService/apply error : ",e);
		}
		return  JSON.toJSONString(response);
	}
	
	
	@RequestMapping(value="/query", method = RequestMethod.POST)
	String query(Credit creditInfo) {
		logger.info("service method {}", "creditService/query");
		logger.info("service param {}", JSON.toJSONString(creditInfo));
        String result = null;
		Map<String,Object> response = new HashMap<String,Object>();
		try{
			if(null == creditInfo.getRecordNo() || "".equals(creditInfo.getRecordNo())){
				//校验参数
				if(!checkParam(creditInfo,"query")){
					response.put("code", "100000");
					response.put("message", "参数错误");
					return  JSON.toJSONString(response);
				}
			}
			
        	List<Credit> creditList = new ArrayList<>();
        	if("queryCreditDetail".equals(creditInfo.getDescrible())){
        		creditList = creditService.getCreditDetail(creditInfo);
        	}else{
        		creditList = creditService.getCreditList(creditInfo);
        	}
			logger.info("service query getCreditList result {}",JSON.toJSONString(creditList));
			if(null != creditList && creditList.size() > 0){
				if("3".equals(creditList.get(0).getStatus2dfire())){
					try{
						//计算管理费 6个月一次性收取2%  12个月一次性收取3%
						JSONObject map = new JSONObject(); 
						map.put("amt", creditList.get(0).getSixMonthAmt());
				        JSONObject calFeeJson = restTemplate.postForEntity(drawServiceCalFeeUrl, map, JSONObject.class).getBody();
						logger.info("drawServiceCalFee param {}", calFeeJson.toString());
						if("0000".equals(calFeeJson.getString("code"))){
							creditList.get(0).setSixManageFee(new BigDecimal(calFeeJson.getString("sixManageFee")));
						}
						map.put("amt", creditList.get(0).getTwelveMonthAmt());
				        calFeeJson = restTemplate.postForEntity(drawServiceCalFeeUrl, map, JSONObject.class).getBody();
						logger.info("drawServiceCalFee param {}", calFeeJson.toString());
						if("0000".equals(calFeeJson.getString("code"))){
							creditList.get(0).setTwelveManageFee(new BigDecimal(calFeeJson.getString("twlManageFee")));
						}
					}catch(Exception e){
						logger.error("creditService/query drawServiceCalFee error : ",e);
					}
				}
				
			}
			response.put("totalCount", creditList.size());
			response.put("creditList", JSON.toJSONString(creditList));
			response.put("code", "000000");
			response.put("message", "success");
        }catch (Exception e) {
			response.put("code", "000002");
			response.put("message", "error");
			logger.error("creditService/query error : ",e);
		}
		return  JSON.toJSONString(response);
	}
	
	@RequestMapping(value="/approve", method = RequestMethod.POST)
	String approve(Credit creditInfo) {
		logger.info("service method {}", "creditService/approve");
		logger.info("service param {}", JSON.toJSONString(creditInfo));
        String result = null;
		Map<String,Object> response = new HashMap<String,Object>();
		response.put("code", "000002");
		response.put("message", "error");
		try{
        	//查询原记录
        	Credit creditQuery = new Credit();
        	creditQuery.setRecordNo(creditInfo.getRecordNo());
        	creditQuery.setLmtSerno(creditInfo.getLmtSerno());
        	creditQuery.setMerchantNo(creditInfo.getMerchantNo());
        	creditQuery.setProdCode(creditInfo.getProdCode());
			List<Credit> creditList = creditService.getCreditList(creditQuery);
        	if(null == creditList || creditList.size() != 1){
        		logger.info("service creditService/approve getCreditList {} ", "未查询到要更新的记录");
				return  JSON.toJSONString(response);
        	}
        	Credit creditOld = creditList.get(0);
			//1、调用loan.twoFireCreditInfoSynch
			MultiValueMap<String, Object> createReqMap = new LinkedMultiValueMap<>();
			createReqMap = getCreditUpdate(creditInfo,creditOld);
			createReqMap.add("topicName", "loan.twoFireCreditInfoSynch");
			createReqMap.add("operType", "2");
			logger.info("service approve loan.twoFireCreditInfoSynch params {}",createReqMap);
			JSONObject createReqJson = restTemplate.postForEntity(masSendUrl, createReqMap, JSONObject.class).getBody();
			logger.info("service approve loan.twoFireCreditInfoSynch result {}",createReqJson);
			if(!"W-Y000000".equals(createReqJson.getString("resultCode"))){
        		logger.info("service creditService/approve loan.twoFireCreditInfoSynch {} ", "更新二代授信失败");
				return  JSON.toJSONString(response);
			}
			
			//2、变更授信信息2dfire.t_loan_credit_req
			creditInfo.setRecordNo(creditOld.getRecordNo());
			int i = creditService.updateLoanCreditReq(creditInfo);
			logger.info("service approve saveLoanCreditReq result {}",i);
			if(i == 1){
				response.put("recordNo", creditInfo.getRecordNo());
				response.put("code", "000000");
				response.put("message", "success");
			}
        }catch (Exception e) {
			response.put("code", "000002");
			response.put("message", "系统繁忙，请稍后再试！");
			logger.error("creditService/approve error : ",e);
		}
		return  JSON.toJSONString(response);
	}

	@RequestMapping(value="/nullify")
	String nullify(){
		logger.info("service method {}", "creditService/nullify");
        String result = null;
        int count = 0;
		Map<String,Object> response = new HashMap<String,Object>();
		response.put("code", "000002");
		response.put("message", "error");
		try{
			//授信失效：将7天内未签约的授信置为失效   
			//查询7天内未签约的授信
        	Credit creditQuery = new Credit();
        	creditQuery.setContSignFlag("20");
        	creditQuery.setProdCode("9002000003");
			List<Credit> notSignList = creditService.getCreditListNotSign(creditQuery);
			for(int i=0;i<notSignList.size();i++){
				//1、调用loan.twoFireCreditInfoSynch
				MultiValueMap<String, Object> createReqMap = new LinkedMultiValueMap<>();
				createReqMap.add("merserno",notSignList.get(i).getLmtSerno());//openId
				createReqMap.add("orgCode", notSignList.get(i).getOrgCode());//机构代码
				createReqMap.add("appstate", "8");//status 8-贷款作废
				createReqMap.add("topicName", "loan.twoFireCreditInfoSynch");
				createReqMap.add("operType", "2");
				logger.info("service approve loan.twoFireCreditInfoSynch params {}",createReqMap);
				JSONObject createReqJson = restTemplate.postForEntity(masSendUrl, createReqMap, JSONObject.class).getBody();
				logger.info("service approve loan.twoFireCreditInfoSynch result {}",createReqJson);
				if(!"W-Y000000".equals(createReqJson.getString("resultCode"))){
					count++;
	        		logger.info("service creditService/nullify loan.twoFireCreditInfoSynch {} recordNo {}", "更新二代授信失败",notSignList.get(i).getLmtSerno());
				}
				
				//2、变更授信信息2dfire.t_loan_credit_req
				Credit creditNew = new Credit();
				creditNew.setRecordNo(notSignList.get(i).getRecordNo());
				creditNew.setStatus("8");	//status 8-贷款作废
				creditNew.setStatus2dfire("5");	//status2dfire 5-已失效
				int ret = creditService.updateLoanCreditReq(creditNew);
				logger.info("service approve saveLoanCreditReq result {}",ret);
				if(ret != 1){
					count++;
	        		logger.info("service creditService/nullify 2dfire.t_loan_credit_req {} recordNo {}", "更新二维火授信失败",notSignList.get(i).getRecordNo());
				}
			}
			if(count == 0){
				response.put("code", "000000");
				response.put("message", "all success");
			}else{
				response.put("code", "000001");
				response.put("message", "part success");
			}
        }catch (Exception e) {
			logger.error("creditService/nullify error : ",e);
		}
		return  JSON.toJSONString(response);
	}
	
	private boolean checkParam(Credit creditInfo, String type) {
		boolean ret = true;
		if(null == creditInfo.getDayLimit() || "".equals(creditInfo.getDayLimit())){
			if(null == creditInfo.getMerchantNo() || "".equals(creditInfo.getMerchantNo())){
				logger.info("merchantNo is null");
				ret = false;
			}
		}
		if(null == creditInfo.getProdCode() || "".equals(creditInfo.getProdCode())){
			logger.info("prodCode is null");
			ret = false;
		}
		if("ruleAccess".equals(type)){
			if(null == creditInfo.getMobile() || "".equals(creditInfo.getMobile())){
				logger.info("mobile is null");
				ret = false;
			}
		}
		return ret;
	}
	
	private MultiValueMap<String, Object> getCreditReq(Credit credit) {
		MultiValueMap<String, Object> createReqMap = new LinkedMultiValueMap<>();
		createReqMap.add("openid",credit.getUserId());//openId
		createReqMap.add("orgCode", credit.getOrgCode());//机构代码
		createReqMap.add("orgChl", credit.getOrgChl());//渠道代码
		createReqMap.add("memberId", credit.getInvestorOid());//会员ID
		createReqMap.add("prodCode", credit.getProdCode());//产品代码
		createReqMap.add("empPhone", credit.getEmpPhone());//客户经理手机号
		createReqMap.add("cunm", credit.getCustName());//客户名称
		createReqMap.add("idtp", credit.getCertType());//证件类型：身份证
		createReqMap.add("idno", credit.getCertNo());//证件号码		
		createReqMap.add("merno", credit.getMerchantNo());//商户ID		
		createReqMap.add("bankname", credit.getBankName());//银行名称		
		createReqMap.add("retubankid", credit.getBankCode());//放/还款开户银行行号		
		createReqMap.add("pickupacno", credit.getBankAcctNo());//放/还款银行账号
		createReqMap.add("mobl", credit.getMobile());//手机号码
		createReqMap.add("appamt", String.valueOf(credit.getApplAmt()));//申请金额
		createReqMap.add("livest", credit.getLivest());//居住状态
		createReqMap.add("livecity", credit.getLiveCity());//居住地城市
		createReqMap.add("liveaddr", credit.getLiveAddr());//居住地		
		createReqMap.add("opercity", credit.getMchtCity());//经营城市	
		createReqMap.add("operaddr", credit.getMchtAddr());//经营地址
		createReqMap.add("marriag", credit.getMarriag());//婚姻状况	
		createReqMap.add("spousenm", credit.getSpouseNm());//配偶姓名【婚姻状况】为 已婚 时，必须输入
		createReqMap.add("spouseidtp", credit.getSpouseIdtp());//配偶证件类型【婚姻状况】为 已婚 时，必须输入
		createReqMap.add("spouseidno", credit.getSpouseIdno());//配偶证件号码【婚姻状况】为 已婚 时，必须输入
		createReqMap.add("regno", credit.getLicenseNo());//营业执照编号
		createReqMap.add("grantno", credit.getGrantNo());//征信查询授权书编号
		createReqMap.add("granttime", credit.getGrantTime().toString());//征信查询授权时间	
		createReqMap.add("creditYrate", credit.getPayRate().toString());//授信利率	
		createReqMap.add("status", credit.getStatus());//状态	
		createReqMap.add("connaddr", credit.getMchtAddr());//联系地址
		createReqMap.add("mername", credit.getMerName());//商户名称
		createReqMap.add("loanuse", "2");//贷款用途
		return createReqMap;
	}
	
	private MultiValueMap<String, Object> getCreditUpdate(Credit credit, Credit creditOld) {
		MultiValueMap<String, Object> createReqMap = new LinkedMultiValueMap<>();
		createReqMap.add("merserno",creditOld.getLmtSerno());//openId
		createReqMap.add("orgCode", creditOld.getOrgCode());//机构代码
		createReqMap.add("estimatedAmount", credit.getSixMonthAmt());//6个月额度
		createReqMap.add("totallmt", credit.getTwelveMonthAmt());//12个月额度
		createReqMap.add("creditpoint", credit.getCreditPoint());//通华信用分
		createReqMap.add("appstate", credit.getStatus());//状态	
		createReqMap.add("lmtcontractno", credit.getContractNo());//合同编号	
		createReqMap.add("lmtstartdate", credit.getLmtStartDate());//额度起始日
		createReqMap.add("lmtenddate", credit.getLmtEndDate());//额度到期日
		createReqMap.add("appterm", credit.getUseTerm());//申请授信期限
		createReqMap.add("effectiveAmount", credit.getUseAmt());//最终签约后额度
		createReqMap.add("contsignFlag", credit.getContSignFlag());//签约状态
		createReqMap.add("contsigntime", credit.getContSignTime());//签约时间
		return createReqMap;
	}
	
}
