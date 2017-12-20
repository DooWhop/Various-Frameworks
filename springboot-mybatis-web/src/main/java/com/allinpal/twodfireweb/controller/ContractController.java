package com.allinpal.twodfireweb.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

@Controller
public class ContractController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private RestTemplate restTemplate;

	@Value("${contract.url}")
	private String contractUrl;

	@Value("${draw.url}")
	private String drawUrl;

	@RequestMapping(value = "/contractSign", method = RequestMethod.POST)
	public String contractSign(Model model, HttpServletRequest request) {
		logger.info("contractSign=====>");
		String merchantNo = request.getParameter("merchantNo");
		try {

			String appAmt = request.getParameter("appAmt");
			String status = request.getParameter("status");
			String protocolNum = request.getParameter("protocolNum");
			String timeLimit = request.getParameter("applyTnr");
			String usercode = request.getParameter("userCode");
			String recordNo = request.getParameter("recordNo");
			String fileId = request.getParameter("fileId");
			String prodCode = request.getParameter("prodCode");
			String batchNo = request.getParameter("batchNo");
			String useRecordNo = request.getParameter("useRecordNo");

			JSONObject params = new JSONObject();
			if (appAmt == null || "".equals(appAmt) || status == null
					|| "".equals(status) || protocolNum == null
					|| "".equals(protocolNum) || timeLimit == null
					|| "".equals(timeLimit) || usercode == null
					|| "".equals(usercode) || recordNo == null
					|| "".equals(recordNo) || fileId == null
					|| "".equals(fileId) || prodCode == null
					|| "".equals(prodCode) || merchantNo == null
					|| "".equals(merchantNo) || batchNo == null
					|| "".equals(batchNo) || useRecordNo == null
					|| "".equals(useRecordNo)) {
				// logger.error("contractSign 参数不正确，{}");
				// return "common/error";
			}
			params.put("appAmt", appAmt);
			params.put("status", status);
			params.put("protocolNum", protocolNum);
			params.put("timeLimit", timeLimit);
			params.put("usercode", usercode);
			params.put("recordNo", recordNo);
			params.put("fileId", fileId);
			params.put("prodCode", prodCode);
			params.put("merchantNo", merchantNo);
			params.put("batchNo", batchNo);
			params.put("useRecordNo", useRecordNo);
			logger.info("contractSign 参数{}", params);
			// TODO 新增判断是否已支用，已支用则返回提示页面
			String drawApplyQueryUrl = drawUrl + "/queryUseInfo";
			JSONObject queryParam = new JSONObject();
			queryParam.put("creditRecordNo", recordNo);
			queryParam.put("useSignFlag", "20");
			JSONObject queryRes = restTemplate.postForObject(drawApplyQueryUrl,
					queryParam, JSONObject.class);
			logger.info("判断是否已成功签约支用queryUseInfo param, {}", queryParam);
			logger.info("判断是否已成功签约支用queryUseInfo res, {}", queryRes);
			if ("0000".equals(queryRes.get("code"))) {
				JSONObject useInfoJO = queryRes.getJSONObject("data");
				logger.info("useInfoJO========>>>{}", useInfoJO);
				if ("12".equals(useInfoJO.get("status"))) {
					model.addAttribute("message", "已完成支用签约。");
					model.addAttribute("merchantNo", merchantNo);
					return "draw/error";
				}
				params.put("status", useInfoJO.get("status"));
			} else if ("9999".equals(queryRes.get("code"))) {
				model.addAttribute("merchantNo", merchantNo);
				return "draw/error";
			}
			
			Map<String, Object> protocolParams = new HashMap<String, Object>();
			protocolParams.put("creditRecordNo", recordNo);
			List<Map<String, Object>> protocolList = new ArrayList<Map<String, Object>>();
			Map<String, Object> KHXYmap = new HashMap<String, Object>();
			KHXYmap.put("protocolType", "2");
			Map<String, Object> KKmap = new HashMap<String, Object>();
			KKmap.put("protocolType", "3");
			protocolList.add(KHXYmap);
			protocolList.add(KKmap);
			protocolParams.put("protocols", protocolList);
			protocolParams.put("useRecordNo", useRecordNo);
			String signAgreeUrl = contractUrl + "/signAgreement";
			logger.info("signAgree signAgreeUrl=====>{}", signAgreeUrl);
			logger.info("signAgree param=====>{}", protocolParams);
			String signAgreeResult = restTemplate.postForObject(signAgreeUrl,
					protocolParams, String.class);
			logger.info("signAgree response=====>{}", signAgreeResult);

			JSONObject queryParam1 = new JSONObject();
			queryParam1.put("recordNo", useRecordNo);
			JSONObject queryRes1 = restTemplate.postForObject(drawApplyQueryUrl,
					queryParam1, JSONObject.class);
			logger.info("判断进行过签约支用queryUseInfo param, {}", queryParam1);
			logger.info("判断进行过签约支用queryUseInfo res, {}", queryRes1);
			if ("0001".equals(queryRes1.get("code"))) {
				String drawApplyUrl = drawUrl + "/confirm";
				logger.info("contractSign=====>" + drawApplyUrl);
				String drawResult = restTemplate.postForObject(drawApplyUrl, params,
						String.class);
				logger.debug("request from {} result is {}", drawApplyUrl,
						drawResult);
				JSONObject drawJo = JSON.parseObject(drawResult);
				String code = drawJo.getString("code");
				if (code != null && !"".equals(code) && !"000000".equals(code)) {
					model.addAttribute("message", drawJo.getString("errMsg"));
					model.addAttribute("merchantNo", merchantNo);
					return "draw/error";
				}
			} else if ("9999".equals(queryRes.get("code"))) {
				model.addAttribute("merchantNo", merchantNo);
				return "draw/error";
			}
			
			String contractSignUrl = contractUrl + "/sign";
			logger.info("contractSign=====>" + contractSignUrl);
			String restResult = restTemplate.postForObject(contractSignUrl, params,
					String.class);
			// String restResult = restTemplate.getForObject(contractSignUrl,
			// String.class);
			logger.debug("request from {} result is {}", contractSignUrl,
					restResult);
			JSONObject restJO = JSON.parseObject(restResult);
			String code1 = restJO.getString("code");
			if (code1 != null && !"".equals(code1) && !"0000".equals(code1)) {
				// 签约失败
				model.addAttribute("merchantNo", merchantNo);
				return "draw/signError";
			}

			model.addAttribute("prodCode", prodCode);
			model.addAttribute("merchantNo", merchantNo);
			logger.info("response model, {}", model);

			// TODO 删除文件
			String current = this.getClass().getResource("/").getPath()
					+ "static/pdfjs/";
			logger.info("current=====>" + current);

			String fileID = current + fileId + ".pdf";
			File file = new File(fileID);
			if (file.delete()) {
				System.out.println(file.getName() + "is deleted");
				logger.info("File is deleted, {}.", file.getName());
			} else {
				logger.info("Delete failed.");
			}
			String imgID = current + fileId + ".jpg";
			File imgFile = new File(imgID);
			if (imgFile.delete()) {
				logger.info("File is deleted, {}.", imgFile.getName());
			} else {
				logger.info("IMG Delete failed.");
			}
			return "draw/drawSucc";
		} catch (Exception e) {
			logger.info("contractSign exception : ", e);
			model.addAttribute("code", "000002");
			model.addAttribute("message", "很抱歉，系统繁忙，请稍后再试");
			model.addAttribute("merchantNo", merchantNo);
			return "draw/error";
		}

	}
}
