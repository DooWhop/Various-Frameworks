package com.allinpal.twodfireservice.contractService.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Expand;
import org.apache.tools.ant.taskdefs.Zip;
import org.apache.tools.ant.types.FileSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import sun.misc.BASE64Encoder;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.allinpal.twodfireservice.AppConfig;
import com.allinpal.twodfireservice.contractService.domain.TLoanProtocol;
import com.allinpal.twodfireservice.contractService.service.ContractService;
import com.allinpal.twodfireservice.util.GenerateIdsUtil;
import com.allinpay.base.util.ReportUtil;
import com.thfund.commonService.file.hessian.FileService;

@RestController
@RequestMapping("contract")
public class ContractController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private static final ExecutorService executor = Executors
			.newSingleThreadExecutor();

	@Autowired
	private ContractService contractService;

	@Autowired
	private FileService fileService;

	@Autowired
	private AppConfig appConfig;

	private static String init_status = "0";

	private static String signed_status = "1";

	@Autowired
	private RestTemplate restTemplate;

	@Value("${mas.send.url}")
	private String masSendUrl;

	@Value("${mas.sendV2.url}")
	private String masSendV2Url;

	@Value("${twodfire.service.contract.WTKK.version}")
	private String WTKKVersion;

	@Value("${twodfire.service.contract.KHFW.version}")
	private String KHFWVersion;

	@Value("${twodfire.service.contract.KKSQS.version}")
	private String KKSQSVersion;

	@Value("${twodfire.service.contract.ZXCX.version}")
	private String ZXCXVersion;

	@Value("${twodfire.service.contract.DKHT.version}")
	private String DKHTVersion;

	@Value("${twodfire.service.contract.YHZC.version}")
	private String YHZCVersion;

	@Value("${reportscanService.queryCredit.url}")
	private String creditUrl;

	@Value("${reportscanService.updateCredit.url}")
	private String creditUpdateUrl;

	@Value("${drawService.calFee.url}")
	private String drawServiceCalFeeUrl;

	@Value("${drawService.updateCharge.url}")
	private String drawServiceUpdateChargeUrl;

	@Value("${drawService.updateUse.url}")
	private String drawServiceUpdateUseUrl;

	@Value("${drawService.queryUse.url}")
	private String drawServiceQueryUseUrl;

	@Value("${twodfire.feededuct.org}")
	private String feededuct;

	@Value("${contractService.packContract.url}")
	private String packContractUrl;
	
	@Value("${service.six.rate}")
	private String sixServiceRate;
	
	@Value("${manage.six.rate}")
	private String sixManageRate;
	
	@Value("${manage.twl.rate}")
	private String twlManageRate;
	
	@Value("${service.twl.rate}")
	private String twlServiceRate;

	@RequestMapping(value = "/createContract", method = RequestMethod.POST)
	String createContract(@RequestBody Map<String, Object> map)
			throws IOException {
		// @RequestMapping("/createContract")
		// String createContract(Map<String,Object> map) throws IOException {
		/**
		 * 调接口创建合同
		 */
		// map.put("creditRecordNo", "15126111491900001");
		// map.put("protocolNo", "12306");
		// map.put("fileId", "3d812afffd6d24e38aa172fc8de25daa");
		// map.put("batchNo", "1234567980");
		// map.put("applTerm", "06M");
		// map.put("useRecordNo", "12306");
		JSONObject result = new JSONObject();
		logger.info("createContract request param is {}", map);

		if (null == map.get("protocolNo")
				|| map.get("protocolNo").toString().equals("")) {
			result.put("code", "000002");
			result.put("message", "protocolNo is null");
			return result.toJSONString();
		}
		if (null == map.get("fileId")
				|| map.get("fileId").toString().equals("")) {
			result.put("code", "000002");
			result.put("message", "fileId is null");
			return result.toJSONString();
		}
		if (null == map.get("creditRecordNo")
				|| map.get("creditRecordNo").toString().equals("")) {
			result.put("code", "000002");
			result.put("message", "creditRecordNo is null");
			return result.toJSONString();
		}

		logger.info("createContract request param:{}", map);
		String creditRecordNo = map.get("creditRecordNo").toString();

		TLoanProtocol paramter = new TLoanProtocol();
		paramter.setOriginalZipfileId(map.get("fileId").toString());
		paramter.setCreditRecordNo(creditRecordNo);
		List<TLoanProtocol> lists = contractService.getTLoanProtocols(paramter);
		String pdfFileId = null;
		if (lists == null || lists.size() == 0) {
			Map<String, Object> credit = contractService.getCreditById(map);
			if (credit == null) {
				result.put("code", "000002");
				result.put("message", "credit record is null");
				return result.toJSONString();
			}

			TLoanProtocol loanProtocol = new TLoanProtocol();
			loanProtocol.setRecordNo(GenerateIdsUtil.generateId(appConfig
					.getAppIp()));
			loanProtocol.setCreditRecordNo(creditRecordNo);
			loanProtocol.setAcctId(credit.get("acct_id").toString());
			loanProtocol.setProdCode(credit.get("prod_code").toString());
			loanProtocol.setStatus(init_status);
			loanProtocol.setVersion(getVersion("4"));
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			loanProtocol.setSignDate(sdf.format(new Date()));
			loanProtocol.setCreateUid(credit.get("cust_name").toString());
			loanProtocol.setCreateTime(System.currentTimeMillis());
			loanProtocol.setLastModifyUid(credit.get("cust_name").toString());
			loanProtocol.setLastModifyTime(System.currentTimeMillis());
			loanProtocol.setProtocolType("4");
			loanProtocol.setBatchNo(map.get("batchNo").toString());
			loanProtocol.setApplTerm(map.get("applTerm").toString());
			loanProtocol.setUseRecordNo(map.get("useRecordNo").toString());
			loanProtocol.setProtocolNo(map.get("protocolNo").toString());
			loanProtocol.setOriginalZipfileId(map.get("fileId").toString());
			int i = contractService.saveProtocol(loanProtocol);
			if (i != 1) {
				result.put("code", "000002");
				result.put("message", "insert错误，结果不为1");
				return result.toJSONString();
			}
			pdfFileId = downloadZipFromCloud(map.get("protocolNo").toString(),
					map.get("fileId").toString(), loanProtocol.getRecordNo());
		} else {
			pdfFileId = lists.get(0).getOriginalPdffileId();
		}

		if (pdfFileId == null) {
			result.put("code", "000002");
			result.put("message", "上传错误");
			logger.info("pdfFileId is {}", pdfFileId);
			return result.toJSONString();
		}

		// 下载文件
		String filePathPdf = appConfig.getDownloadFilePath()
				.concat(File.separator).concat("pdf");
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("fileId", pdfFileId);
		Map<String, Object> resultMap = fileService.download(param);
		byte[] bytes = (byte[]) resultMap.get("data");
		File parent = new File(filePathPdf);
		if (!parent.exists()) {
			parent.mkdirs();
		}
		File file = new File(filePathPdf + File.separator + pdfFileId + ".pdf");
		FileOutputStream fout = new FileOutputStream(file);
		fout.write(bytes);
		IOUtils.closeQuietly(fout);

		String base64String = getPDFBinary(file);
		// byte[] b = Base64.decodeBase64(base64String);
		delAllFile(filePathPdf, 0);

		result.put("code", "000000");
		result.put("message", "处理成功");
		result.put("fileId", pdfFileId);
		result.put("byte", base64String);
		logger.info("createContract response fileId:{}", result.get("fileId"));
		return result.toJSONString();
	}

	/*
	 * @RequestMapping(value = "/viewContract", method = RequestMethod.POST)
	 * String viewContract(@RequestBody String fileId) { //
	 * @RequestMapping("/viewContract") // String viewContract(String fileId) {
	 * JSONObject resultMap = new JSONObject(); String filePath =
	 * appConfig.getDownloadFilePath(); String filePathPdf =
	 * filePath.concat(File.separator).concat("pdf"); FileOutputStream fout =
	 * null; try{ logger.info("fileId is {}",fileId); //下载文件 Map<String, Object>
	 * param = new HashMap<String, Object>(); param.put("fileId", fileId);
	 * Map<String, Object> result = fileService.download(param); byte[] bytes =
	 * (byte[]) result.get("data"); File parent = new File(filePathPdf); if
	 * (!parent.exists()) { parent.mkdirs(); } File file = new
	 * File(filePathPdf+File.separator + fileId+".pdf"); fout = new
	 * FileOutputStream(file); fout.write(bytes);
	 * 
	 * resultMap.put("code", "000000"); resultMap.put("message", "处理成功");
	 * resultMap.put("path", filePathPdf+File.separator + fileId+".pdf"); return
	 * resultMap.toJSONString(); }catch (Exception e) { StringWriter w = new
	 * StringWriter(); e.printStackTrace(new PrintWriter(w));
	 * logger.error(w.toString());
	 * 
	 * resultMap.put("code", "000002"); resultMap.put("message", "转换失败"); return
	 * resultMap.toJSONString(); }finally{ IOUtils.closeQuietly(fout);
	 * delAllFile(filePath, 0);
	 * 
	 * }
	 * 
	 * 
	 * }
	 */

	// @RequestMapping("/signAgreement")
	// String signAgreement(Map map){
	@RequestMapping(value = "/signAgreement", method = RequestMethod.POST)
	String signAgreement(@RequestBody Map<String, Object> map) {
		/*
		 * map.put("creditRecordNo", "15126111491900001");
		 * map.put("useRecordNo", "useRecordNo"); List<Map<String,Object>> list
		 * = new ArrayList<Map<String,Object>>(); Map<String,Object> p = new
		 * HashMap<String, Object>(); p.put("protocolType", "0");
		 * p.put("protocolNo", "WTKK12306"); list.add(p); Map<String,Object> p1
		 * = new HashMap<String, Object>(); p1.put("protocolType", "1");
		 * p1.put("protocolNo", "KHFW12306"); list.add(p1); Map<String,Object>
		 * p2 = new HashMap<String, Object>(); p2.put("protocolType", "2");
		 * list.add(p2); Map<String,Object> p3 = new HashMap<String, Object>();
		 * p3.put("protocolType", "3"); list.add(p3); map.put("protocols",
		 * list);
		 */
		JSONObject result = new JSONObject();
		result.put("code", "000000");
		result.put("message", "处理成功");
		logger.info("signAgreement request param is {}", map);

		if (null == map.get("creditRecordNo")
				|| map.get("creditRecordNo").toString().equals("")) {
			result.put("code", "000002");
			result.put("message", "creditRecordNo is null");
			return result.toJSONString();
		}
		if (null == map.get("useRecordNo")
				|| map.get("useRecordNo").toString().equals("")) {
			result.put("code", "000002");
			result.put("message", "useRecordNo is null");
			return result.toJSONString();
		}
		if (null == map.get("protocols")
				|| map.get("protocols").toString().equals("")) {
			result.put("code", "000002");
			result.put("message", "protocols is null");
			return result.toJSONString();
		}

		logger.info("signAgreement request param:{}", map);
		String creditRecordNo = map.get("creditRecordNo").toString();
		Map<String, Object> credit = contractService.getCreditById(map);

		TLoanProtocol loanProtocol = new TLoanProtocol();

		loanProtocol.setCreditRecordNo(creditRecordNo);
		loanProtocol.setUseRecordNo(map.get("useRecordNo").toString());
		loanProtocol.setStatus(init_status);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		loanProtocol.setSignDate(sdf.format(new Date()));
		loanProtocol.setCreateTime(System.currentTimeMillis());
		loanProtocol.setLastModifyTime(System.currentTimeMillis());

		List<Map<String, Object>> protocols = (List<Map<String, Object>>) map
				.get("protocols");

		for (Map<String, Object> protocol : protocols) {
			if (protocol.get("protocolType") == null
					|| protocol.get("protocolType").toString().equals("")) {
				result.put("code", "000002");
				result.put("message", "protocolType is null");
				return result.toJSONString();
			}

			if (protocol.get("protocolType").toString().equals("5")) {// 用户注册协议
				loanProtocol.setProdCode("9002000003");
				loanProtocol.setCreateUid("SYSTEM");
				loanProtocol.setLastModifyUid("SYSTEM");
			} else {
				TLoanProtocol tLoanProtocol = new TLoanProtocol();
				tLoanProtocol.setCreditRecordNo(creditRecordNo);
				tLoanProtocol.setProtocolType(protocol.get("protocolType")
						.toString());
				List<TLoanProtocol> lists = contractService
						.getTLoanProtocols(tLoanProtocol);
				if (lists != null & lists.size() > 0) {
					continue;
				}
				loanProtocol.setAcctId(credit.get("acct_id").toString());
				loanProtocol.setProdCode(credit.get("prod_code").toString());
				loanProtocol.setCreateUid(credit.get("cust_name").toString());
				loanProtocol.setLastModifyUid(credit.get("cust_name")
						.toString());
			}

			loanProtocol.setRecordNo(GenerateIdsUtil.generateId(appConfig
					.getAppIp()));
			loanProtocol.setProtocolType(protocol.get("protocolType")
					.toString());
			loanProtocol.setVersion(getVersion(protocol.get("protocolType")
					.toString()));
			// 合同类型:0,火融e委托扣款授权书;1,火融e客户服务协议;2,扣款授权书;3,征信查询上报授权书;4,贷款合同
			if (protocol.containsKey("protocolNo")) {
				loanProtocol.setProtocolNo(protocol.get("protocolNo")
						.toString());
			} else {
				loanProtocol.setProtocolNo(GenerateIdsUtil.protocolId(protocol
						.get("protocolType").toString()));
			}
			int i = contractService.saveProtocol(loanProtocol);
			if (i != 1) {
				result.put("code", "000002");
				result.put("message", "insert错误，结果不为1");
				return result.toJSONString();
			}

			if (credit == null) {
				credit = new HashMap<String, Object>();
			}
			credit.put("proRecordNo", loanProtocol.getRecordNo());
			credit.put("protocolNo", loanProtocol.getProtocolNo());
			credit.put("protocolType", loanProtocol.getProtocolType());

			if (protocol.get("protocolType").toString().equals("5")) {
				credit.put("cust_name", "SYSTEM");
			}
			buildPdfAndUpLoad(credit, loanProtocol.getRecordNo());
		}
		return result.toJSONString();
	}

	@RequestMapping(value = "/signContract", method = RequestMethod.GET)
	String signContract(@RequestBody Map<String, Object> map) {
		String pdfFileId = null;
		// 签署协议
		// signAgreement(map);
		// map.put("protocolNo", "12306");
		// map.put("fileId", "5681b1cb69c808b91c8115d37d732a87");

		logger.info("signContract request param is {}", map);
		JSONObject result = new JSONObject();
		result.put("code", "000002");
		result.put("message", "上传错误");
		if (null == map.get("protocolNo")
				|| map.get("protocolNo").toString().equals("")) {
			result.put("code", "000002");
			result.put("message", "protocolNo is null");
			return result.toJSONString();
		}
		if (null == map.get("fileId")
				|| map.get("fileId").toString().equals("")) {
			result.put("code", "000002");
			result.put("message", "fileId is null");
			return result.toJSONString();
		}

		logger.info("signContract request param:{}", map);
		String protocolNo = map.get("protocolNo").toString();
		TLoanProtocol paramLoanProtocol = new TLoanProtocol();
		paramLoanProtocol.setProtocolNo(protocolNo);
		List<TLoanProtocol> loanProtocols = contractService
				.getTLoanProtocols(paramLoanProtocol);

		if (loanProtocols != null && loanProtocols.size() > 0) {
			TLoanProtocol loanProtocol = loanProtocols.get(0);

			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			TLoanProtocol tLoanProtocol = new TLoanProtocol();
			tLoanProtocol.setSignedPdffileId(map.get("fileId").toString());
			tLoanProtocol.setSignDate(sdf.format(new Date()));
			tLoanProtocol.setStatus(signed_status);
			tLoanProtocol.setLastModifyTime(System.currentTimeMillis());
			tLoanProtocol.setRecordNo(loanProtocol.getRecordNo());
			contractService.updateProtocol(tLoanProtocol);

			pdfFileId = downloadPdfFromCloud(map.get("protocolNo").toString(),
					map.get("fileId").toString(), loanProtocol.getRecordNo());

			if (pdfFileId != null && !pdfFileId.equals("")) {
				result.put("code", "000000");
				result.put("message", "处理成功");
				result.put("fileId", pdfFileId);
				return result.toJSONString();
			}

			return result.toJSONString();
		}
		return result.toJSONString();
	}

	@RequestMapping(value = "/packContract", method = RequestMethod.POST)
	String packContract(@RequestBody Map<String, Object> map) {
		// String packContract(Map<String,Object> map){
		// map.put("useRecordNo", "110120");
		// map.put("creditRecordNo", "15126111491900001");

		logger.info("packContract request param is {}", map);
		JSONObject result = new JSONObject();
		result.put("code", "000000");
		result.put("message", "处理成功");
		if (map.get("useRecordNo") == null
				|| map.get("useRecordNo").toString().equals("")) {
			result.put("code", "000002");
			result.put("message", "packContract useRecordNo is null ");
		}
		String useRecordNo = map.get("useRecordNo").toString();
		String buildFileRootPath = appConfig.getDownloadFilePath()
				.concat(File.separator)
				.concat(map.get("creditRecordNo").toString());
		String buildFilePath = buildFileRootPath.concat(File.separator).concat(
				useRecordNo);
		try {
			Map<String, Object> credit = contractService.getCreditById(map);
			logger.info("credit====>>>{}", credit);
			if (credit == null) {
				result.put("code", "000002");
				result.put("message", "credit record is null");
				return result.toJSONString();
			}
			File parent = new File(buildFilePath);
			if (!parent.exists()) {
				parent.mkdirs();
			}
			// 下载身份证正面
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("userId", credit.get("user_id"));
			paramMap.put("fileType", "1");
			paramMap.put("idenFace", "101");
			paramMap.put("status", "10");
			logger.info("paramMap====>>>{}", paramMap);
			Map<String, Object> idenMap = contractService
					.getUserIdenFile(paramMap);
			if (idenMap == null) {
				result.put("code", "000002");
				result.put("message", "UserIdenFile query is null");
				return result.toJSONString();
			}
			if (idenMap.get("fileId") == null
					&& idenMap.get("fileId").toString().equals("")) {
				result.put("code", "000002");
				result.put("message", "UserIdenFile 101 fileId is null");
				return result.toJSONString();
			}
			if (idenMap.get("fileName") == null
					&& idenMap.get("fileName").toString().equals("")) {
				result.put("code", "000002");
				result.put("message", "UserIdenFile 101 fileName is null");
				return result.toJSONString();
			}
			String fileName[] = idenMap.get("fileName").toString().split("\\.");
			downFileFromCloud(idenMap.get("fileId").toString(), buildFilePath,
					useRecordNo.concat("_").concat("01").concat("_")
							.concat("1").concat(".").concat(fileName[1]));

			// 下载身份证反面
			paramMap.put("idenFace", "102");
			idenMap = contractService.getUserIdenFile(paramMap);
			if (idenMap == null) {
				result.put("code", "000002");
				result.put("message", "UserIdenFile query is null");
				return result.toJSONString();
			}
			if (idenMap.get("fileId") == null
					&& idenMap.get("fileId").toString().equals("")) {
				result.put("code", "000002");
				result.put("message", "UserIdenFile 102 fileId is null");
				return result.toJSONString();
			}
			if (idenMap.get("fileName") == null
					&& idenMap.get("fileName").toString().equals("")) {
				result.put("code", "000002");
				result.put("message", "UserIdenFile 102 fileName is null");
				return result.toJSONString();
			}
			fileName = idenMap.get("fileName").toString().split("\\.");
			downFileFromCloud(idenMap.get("fileId").toString(), buildFilePath,
					useRecordNo.concat("_").concat("01").concat("_")
							.concat("2").concat(".").concat(fileName[1]));

			// 生成贷款申请表
			// TLoanProtocol tLoanProtocol = new TLoanProtocol();
			// tLoanProtocol.setProtocolType("6");//6:贷款申请表
			// tLoanProtocol.setProtocolNo(useRecordNo);
			build(credit, "6", useRecordNo, buildFilePath,
					useRecordNo.concat("_").concat("09").concat("_")
							.concat("1").concat(".pdf"));

			// 下载征信查询上报授权书
			TLoanProtocol paramProtocol = new TLoanProtocol();
			paramProtocol.setUseRecordNo(useRecordNo);
			paramProtocol.setStatus(signed_status);
			// 合同类型:0,火融e委托扣款授权书;1,火融e客户服务协议;2,扣款授权书;3,征信查询上报授权书;4,贷款合同
			paramProtocol.setProtocolType("3");
			List<TLoanProtocol> tLoanProtocols = contractService
					.getTLoanProtocols(paramProtocol);
			if (tLoanProtocols == null || tLoanProtocols.size() == 0) {
				result.put("code", "000002");
				result.put("message", "tLoanProtocols record is null");
				return result.toJSONString();
			}
			downFileFromCloud(tLoanProtocols.get(0).getSignedPdffileId(),
					buildFilePath,
					useRecordNo.concat("_").concat("09").concat("_")
							.concat("1").concat(".pdf"));

			// 下载扣款授权书
			paramProtocol.setProtocolType("2");
			tLoanProtocols = contractService.getTLoanProtocols(paramProtocol);
			if (tLoanProtocols == null || tLoanProtocols.size() == 0) {
				result.put("code", "000002");
				result.put("message", "tLoanProtocols record is null");
				return result.toJSONString();
			}
			downFileFromCloud(tLoanProtocols.get(0).getSignedPdffileId(),
					buildFilePath,
					useRecordNo.concat("_").concat("13").concat("_")
							.concat("4").concat(".pdf"));

			// 下载签章贷款合同
			paramProtocol.setProtocolType("4");
			tLoanProtocols = contractService.getTLoanProtocols(paramProtocol);
			if (tLoanProtocols == null || tLoanProtocols.size() == 0) {
				result.put("code", "000002");
				result.put("message", "tLoanProtocols record is null");
				return result.toJSONString();
			}
			downFileFromCloud(tLoanProtocols.get(0).getSignedPdffileId(),
					buildFilePath,
					useRecordNo.concat("_").concat("10").concat("_")
							.concat("6").concat(".pdf"));

			// 压缩文件
			boolean zipRlt = zip(buildFilePath,
					buildFilePath.concat(useRecordNo).concat(".zip"));
			if (!zipRlt) {
				// 压缩zip文件失败
				logger.error("packContract ZipFile is failure!");
				result.put("code", "000002");
				result.put("message", "packContract ZipFile is failure!");
				return result.toJSONString();
			}
			File zipFile = new File(buildFilePath.concat(useRecordNo).concat(
					".zip"));
			String zipFileId = getFileId(useRecordNo.concat(".zip"), zipFile);

			String base64String = getPDFBinary(zipFile);
			result.put("fileId", zipFileId);
			// result.put("byte", base64String);
			logger.info("packContract result fileId is {}", zipFileId);
			return result.toJSONString();
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("packContract pdf has exception :" + e);
			result.put("code", "000002");
			result.put("message", "packContract ZipFile is failure!");
			return result.toJSONString();
		} finally {
			delAllFile(buildFileRootPath, 0);
		}
	}

	/**
	 * 将PDF转换成base64编码 1.使用BufferedInputStream和FileInputStream从File指定的文件中读取内容；
	 * 2.然后建立写入到ByteArrayOutputStream底层输出流对象的缓冲输出流BufferedOutputStream
	 * 3.底层输出流转换成字节数组，然后由BASE64Encoder的对象对流进行编码
	 * */
	static String getPDFBinary(File file) {
		FileInputStream fin = null;
		BufferedInputStream bin = null;
		ByteArrayOutputStream baos = null;
		BufferedOutputStream bout = null;
		try {
			// 建立读取文件的文件输出流
			fin = new FileInputStream(file);
			// 在文件输出流上安装节点流（更大效率读取）
			bin = new BufferedInputStream(fin);
			// 创建一个新的 byte 数组输出流，它具有指定大小的缓冲区容量
			baos = new ByteArrayOutputStream();
			// 创建一个新的缓冲输出流，以将数据写入指定的底层输出流
			bout = new BufferedOutputStream(baos);
			byte[] buffer = new byte[1024];
			int len = bin.read(buffer);
			while (len != -1) {
				bout.write(buffer, 0, len);
				len = bin.read(buffer);
			}
			// 刷新此输出流并强制写出所有缓冲的输出字节，必须这行代码，否则有可能有问题
			bout.flush();
			byte[] bytes = baos.toByteArray();
			// sun公司的API
			BASE64Encoder encoder = new sun.misc.BASE64Encoder();
			return encoder.encodeBuffer(bytes).trim();
			// apache公司的API
			// return Base64.encodeBase64String(bytes);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fin.close();
				bin.close();
				// 关闭 ByteArrayOutputStream 无效。此类中的方法在关闭此流后仍可被调用，而不会产生任何
				// IOException
				// baos.close();
				bout.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	// 上传云存储
	public void buildPdfAndUpLoad(Map<String, Object> map, String protocolNo) {
		executor.execute(new pdfFileThread(map, protocolNo));
	}

	// 从云存储上下载
	public String downloadZipFromCloud(String protocolNo, String fileId,
			String recordNo) {
		// protocolNo="12306";
		// fileId = "a4a066e43a235650d5e439bf8b50f1dd";

		InputStream is = null;
		// String FILE_PATH = "contract/download";
		String filePath = appConfig.getDownloadFilePath()
				.concat(File.separator).concat(fileId);
		String filePathPdf = filePath.concat(File.separator).concat("pdf");
		FileOutputStream fout = null;
		try {
			String zipFileName = fileId + ".zip";

			boolean downloadRet = downloadZip(fileId, zipFileName, filePath);
			if (!downloadRet) {
				// 下载zip文件失败
				logger.error("downloadZip is failure!");
				return null;
			}

			boolean unZipRet = UnZipFile(zipFileName, filePath, filePathPdf);
			if (!unZipRet) {
				// 解压zip文件失败
				logger.error("UnZipFile is failure!");
				return null;
			}

			FileFilter DEFAULT_FILTER = new PDFFileFilter();
			File targetDir = new File(filePathPdf);
			File pdfFile = filterTargetFiles(targetDir, DEFAULT_FILTER);

			logger.info("upload start");
			String fileId1 = getFileId(protocolNo + ".pdf", pdfFile);

			TLoanProtocol tLoanProtocol = new TLoanProtocol();
			tLoanProtocol.setOriginalPdffileId(fileId1);
			tLoanProtocol.setLastModifyUid("SYSTEM");
			tLoanProtocol.setLastModifyTime(System.currentTimeMillis());
			tLoanProtocol.setRecordNo(recordNo);
			int i = contractService.updateProtocol(tLoanProtocol);
			if (i != 1) {
				logger.error("update错误，recordNo={}",
						tLoanProtocol.getRecordNo());
			}

			return fileId1;
		} catch (Exception e) {
			StringWriter w = new StringWriter();
			e.printStackTrace(new PrintWriter(w));
			logger.error(w.toString());
			return null;
		} finally {
			IOUtils.closeQuietly(is);
			IOUtils.closeQuietly(fout);
			delAllFile(filePath, 0);
		}
	}

	public void downFileFromCloud(String fileId, String path, String fileName) {
		FileOutputStream fout = null;
		try {
			// 下载文件
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("fileId", fileId);
			Map<String, Object> result = fileService.download(param);
			byte[] bytes = (byte[]) result.get("data");
			File parent = new File(path);
			if (!parent.exists()) {
				parent.mkdirs();
			}
			File file = new File(path + File.separator + fileName);
			fout = new FileOutputStream(file);
			fout.write(bytes);
		} catch (Exception e) {
			StringWriter w = new StringWriter();
			e.printStackTrace(new PrintWriter(w));
			logger.error(w.toString());
		} finally {
			IOUtils.closeQuietly(fout);
		}

	}

	// 从云存储上下载
	public String downloadPdfFromCloud(String protocolNo, String fileId,
			String recordNo) {

		InputStream is = null;
		String filePath = appConfig.getDownloadFilePath()
				.concat(File.separator).concat(fileId);
		String filePathPdf = filePath.concat(File.separator).concat("pdf");
		FileOutputStream fout = null;
		try {
			downFileFromCloud(fileId, filePathPdf, fileId + ".pdf");

			boolean zipRlt = zip(
					filePathPdf + File.separator + fileId + ".pdf", filePath
							.concat(File.separator).concat(fileId + ".zip"));
			if (!zipRlt) {
				// 压缩zip文件失败
				logger.error("ZipFile is failure!");
				return null;
			}
			File zipFile = new File(filePath.concat(File.separator).concat(
					fileId + ".zip"));
			String zipFileId = getFileId(protocolNo + ".pdf", zipFile);

			TLoanProtocol tLoanProtocol = new TLoanProtocol();
			tLoanProtocol.setSignedZipfileId(zipFileId);
			tLoanProtocol.setLastModifyTime(System.currentTimeMillis());
			tLoanProtocol.setRecordNo(recordNo);
			int i = contractService.updateProtocol(tLoanProtocol);
			if (i != 1) {
				logger.error("update错误，recordNo={}",
						tLoanProtocol.getRecordNo());
			}

			return zipFileId;
		} catch (Exception e) {
			StringWriter w = new StringWriter();
			e.printStackTrace(new PrintWriter(w));
			logger.error(w.toString());
			return null;
		} finally {
			IOUtils.closeQuietly(is);
			IOUtils.closeQuietly(fout);
			delAllFile(filePath, 0);

		}
	}

	/***
	 * 压缩GZip
	 * 
	 * @param data
	 * @return
	 */
	public static byte[] gZip(byte[] data) {
		byte[] b = null;
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			GZIPOutputStream gzip = new GZIPOutputStream(bos);
			gzip.write(data);
			gzip.finish();
			gzip.close();
			b = bos.toByteArray();
			bos.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return b;
	}

	public File filterTargetFiles(File targetDir, FileFilter filter) {

		/**
		 * listFiles : only list the files in the current directory, not include
		 * the files in the sub directories.
		 */
		File[] files = targetDir.listFiles(filter);
		File file1 = null;
		for (File file : files) {

			if (file.isDirectory()) {

				file1 = filterTargetFiles(file, filter);
			} else {

				return file;
			}
		}

		return file1;

	}

	private String getFileId(String fileName, File pdfFile) throws IOException {
		// 上传文件至fileCloud
		HashMap<String, Object> paramFileMap = new HashMap<String, Object>();
		// String fileName=protocolNo+".pdf";
		paramFileMap.put("id", "9002000003");
		paramFileMap.put("fileType", "2");// 协议/合同类型
		paramFileMap.put("fileName", fileName);
		paramFileMap.put("serviceName", "erWeiFire");

		InputStream is = new FileInputStream(pdfFile);
		Map<String, Object> result = fileService.upload(paramFileMap, is);
		String fileId = "";
		for (Entry<String, Object> entry : result.entrySet()) {
			if ("fileId".equalsIgnoreCase(entry.getKey())) {
				fileId = (String) entry.getValue();
			}
		}
		is.close();
		return fileId;
	}

	/**
	 * 下载zip文件
	 * 
	 * @param fileId
	 *            云存储的fileId
	 * @param zipFileName
	 *            zip文件名称
	 * @param filePath
	 *            下载zip文件的临时目录
	 * @return
	 */
	public boolean downloadZip(String fileId, String zipFileName,
			String filePath) {
		logger.info("downloadZip is start! fileId is " + fileId);
		boolean ret = false;
		OutputStream outputStream = null;
		try {
			/*
			 * File directory = new File("");//设定为当前文件夹
			 * System.out.println(directory.getCanonicalPath());//获取标准的路径
			 * System.out.println(directory.getAbsolutePath());//获取绝对路径
			 */// 创建文件夹路径
			File parent = new File(filePath);
			if (!parent.exists()) {
				parent.mkdirs();
			}
			// 先删除文件夹下的文件，防止前次未删除完毕
			delAllFile(filePath, 1);

			// 下载文件
			/*
			 * HessianProxyFactory factory = new HessianProxyFactory();
			 * HdfsService hdfsService=
			 * (HdfsService)factory.create(hdfsServiceUrl); byte[] bytes =
			 * hdfsService.downloadById(fileId);
			 */
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("fileId", fileId);
			Map<String, Object> result = fileService.download(param);
			byte[] bytes = (byte[]) result.get("data");

			String p = filePath + File.separator + zipFileName;
			outputStream = new FileOutputStream(p);
			outputStream.write(bytes);
			outputStream.flush();
			ret = true;
		} catch (Exception ex) {
			StringWriter w = new StringWriter();
			ex.printStackTrace(new PrintWriter(w));
			logger.error("downloadZip has an Exception: " + w.toString());
		} finally {
			IOUtils.closeQuietly(outputStream);
		}
		return ret;
	}

	/**
	 * 解压下载的zip文件
	 * 
	 * @param zipFileName
	 *            zip文件名称
	 * @param filePath
	 *            下载zip文件的临时目录
	 * @return
	 */
	public boolean UnZipFile(String zipFileName, String filePath,
			String filePathCSV) {
		if (!new File(filePath + File.separator + zipFileName).exists()) {
			logger.error("UnZipFile zip file " + zipFileName
					+ " does not exist.");
			return false;
		}

		Project proj = new Project();
		Expand expand = new Expand();
		expand.setProject(proj);
		expand.setTaskType("unzip");
		expand.setTaskName("unzip");
		expand.setEncoding("GBK");

		expand.setSrc(new File(filePath + File.separator + zipFileName));
		expand.setDest(new File(filePathCSV));
		expand.execute();
		return true;
	}

	/**
	 * 压缩文件和文件夹
	 * 
	 * @param srcPathname
	 *            需要被压缩的文件或文件夹路径
	 * @param zipFilepath
	 *            将要生成的zip文件路径
	 * @throws BuildException
	 * @throws RuntimeException
	 */
	public boolean zip(String srcPathname, String zipFilepath)
			throws BuildException, RuntimeException {
		File file = new File(srcPathname);
		if (!file.exists()) {
			throw new RuntimeException("source file or directory "
					+ srcPathname + " does not exist.");
		}

		Project proj = new Project();
		FileSet fileSet = new FileSet();
		fileSet.setProject(proj);
		// 判断是目录还是文件
		if (file.isDirectory()) {
			fileSet.setDir(file);
			// ant中include/exclude规则在此都可以使用
			// 比如:
			// fileSet.setExcludes("**/*.txt");
			// fileSet.setIncludes("**/*.xls");
		} else {
			fileSet.setFile(file);
		}

		Zip zip = new Zip();
		zip.setProject(proj);
		zip.setDestFile(new File(zipFilepath));
		zip.addFileset(fileSet);
		zip.setEncoding("GBK");
		zip.execute();

		System.out.println("compress successed.");
		return true;
	}

	/**
	 * 删除文件夹下的zip文件以及解压出来的文件
	 * 
	 * @param path
	 *            路径
	 * @param type
	 *            为0 时删除文件夹
	 * @return
	 */
	public static boolean delAllFile(String path, int type) {
		boolean flag = false;
		File file = new File(path);
		if (!file.exists()) {
			return flag;
		}
		if (!file.isDirectory()) {
			return flag;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			temp = new File(path + File.separator + tempList[i]);
			if (temp.isDirectory()) {
				delAllFile(path + File.separator + tempList[i], 0);
			} else {
				temp.delete();
			}
		}
		if (type == 0) {
			file.delete();// 删除文件夹
		}
		return flag;
	}

	public void startTobuildPdfAndUpLoad(Map<String, Object> map,
			String recordNo) {
		logger.info("****************************buildPdf start*************************************");
		String buildFilePath = appConfig.getDownloadFilePath()
				.concat(File.separator).concat(recordNo);
		try {

			File file = build(map, null, recordNo, buildFilePath, null);

			// 上传文件至fileCloud
			HashMap<String, Object> paramFileMap = new HashMap<String, Object>();
			// String fileName="shytjxx_0002_"+DateUtil.formatDateTime(new
			// Date(), "yyyyMMdd")+".pdf";
			paramFileMap.put("id", "9002000003");
			paramFileMap.put("fileType", "2");// 协议/合同类型
			paramFileMap.put("fileName", file.getName());
			paramFileMap.put("serviceName", "erWeiFire");

			String fileId = getFileId(
					map.get("protocolNo").toString() + ".pdf", file);
			logger.info("上传客户协议:recordNo is " + recordNo + "上传成功，fileId="
					+ fileId);

			TLoanProtocol tLoanProtocol = new TLoanProtocol();
			tLoanProtocol.setSignedPdffileId(fileId);
			tLoanProtocol.setStatus(signed_status);
			tLoanProtocol.setLastModifyUid(map.get("cust_name").toString());
			tLoanProtocol.setLastModifyTime(System.currentTimeMillis());
			tLoanProtocol.setRecordNo(recordNo);
			contractService.updateProtocol(tLoanProtocol);
		} catch (Exception e) {
			logger.info("****************************buildPdf failed*************************************");
			e.printStackTrace();
			logger.info("Create pdf has exception :" + e);
		} finally {
			delAllFile(buildFilePath, 0);
		}

	}

	private File build(Map<String, Object> map, String type, String recordNo,
			String buildFilePath, String fileName) {
		logger.info("****************************build start*************************************");
		FileOutputStream fout = null;

		try {
			TLoanProtocol loanProtocol = contractService
					.selectProtocolByRecordNo(recordNo);
			String protocolNo = null;
			// 组装list数据，全部数据，不需要分页
			List<Map<String, Object>> pdfList = new ArrayList<Map<String, Object>>();
			// 获取报表模板文件路径
			String filePath = null;
			String protocolType = null;
			if (type != null) {
				protocolType = type;
				protocolNo = recordNo;
			} else {
				protocolType = loanProtocol.getProtocolType();
				protocolNo = loanProtocol.getProtocolNo();
			}

			String rootPath = appConfig.getTemplatePath()
					.concat(File.separator);
			if (protocolType.equals("0")) {
				filePath = rootPath.concat("WTKKSQS(".concat(WTKKVersion)
						.concat(").jrxml"));
			} else if (protocolType.equals("1")) {
				filePath = rootPath.concat("KHFWXY(".concat(KHFWVersion)
						.concat(").jrxml"));
			} else if (protocolType.equals("2")) {
				filePath = rootPath.concat("KKSQS(".concat(KKSQSVersion)
						.concat(").jrxml"));
			} else if (protocolType.equals("3")) {
				filePath = rootPath.concat("ZXCXSQS(".concat(ZXCXVersion)
						.concat(").jrxml"));
			} else if (protocolType.equals("5")) {
				filePath = rootPath.concat("YHZCXY(".concat(ZXCXVersion)
						.concat(").jrxml"));
			} else if (protocolType.equals("6")) {
				filePath = rootPath.concat("DKSQB.jrxml");
			}
			Map<String, Object> paramMap = new HashMap<String, Object>();
			if (!protocolType.equals("5")) {
				paramMap.put("username", map.get("cust_name"));
				paramMap.put("certno", map.get("cert_no"));
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				String date = sdf.format(new Date());
				paramMap.put("date", date);
				paramMap.put("protocolno", protocolNo);
				paramMap.put("acctno", map.get("bank_acct_no"));
				paramMap.put("bankname", map.get("bank_name"));
				paramMap.put("city", map.get("live_city"));
				paramMap.put("year", date.substring(0, 4));
				paramMap.put("month", date.substring(4, 6));
				paramMap.put("day", date.substring(6, 8));

				// 性别
				int sex = Integer.parseInt(map.get("cert_no").toString()
						.substring(16, 17));
				paramMap.put("sex", sex % 2 == 0 ? "女" : "男");// 性别 2男性 3女性
				// 出生日期
				String birthday = map.get("cert_no").toString()
						.substring(6, 14);
				paramMap.put("birthday", birthday);
				paramMap.put("address", map.get("live_addr"));
				paramMap.put("mobile", map.get("mobile"));
				// 11:博士研究生毕业 14:硕士研究生毕业 20:大学本科 30:专科教育 40:中等职业教育 60:普通高级中学教育
				// 90:其他
				String edu = "";
				if (map.get("highest_edu").toString().equals("11")) {
					edu = "博士研究生毕业";
				} else if (map.get("highest_edu").toString().equals("14")) {
					edu = "硕士研究生毕业";
				} else if (map.get("highest_edu").toString().equals("20")) {
					edu = "大学本科";
				} else if (map.get("highest_edu").toString().equals("30")) {
					edu = "专科教育";
				} else if (map.get("highest_edu").toString().equals("40")) {
					edu = "中等职业教育";
				} else if (map.get("highest_edu").toString().equals("60")) {
					edu = "普通高级中学教育";
				} else if (map.get("highest_edu").toString().equals("90")) {
					edu = "其他";
				}
				paramMap.put("edu", edu);
				// 10 : 未婚 20 : 已婚 30 : 丧偶 40 : 离婚
				String marriage = "";
				if (map.get("marriag").toString().equals("10")) {
					marriage = "未婚";
				} else if (map.get("marriag").toString().equals("20")) {
					marriage = "已婚";
				} else if (map.get("marriag").toString().equals("30")) {
					marriage = "丧偶";
				} else if (map.get("marriag").toString().equals("40")) {
					marriage = "离婚";
				}
				paramMap.put("marriage", marriage);
				paramMap.put("applamt", map.get("appl_amt"));
				SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
				String dateStr = dateformat.format(new Date(Long.parseLong(map
						.get("create_time").toString())));
				paramMap.put("appldate", dateStr);
			}

			pdfList.add(paramMap);
			// 通过报表引擎生成二进制字节流
			logger.info("buildPdf filePath is {}",
					filePath + ",paramMap is {}", paramMap);
			byte[] bytes = ReportUtil.runToPdf(filePath, null, pdfList);
			if (fileName == null || fileName.equals("")) {
				fileName = protocolNo + ".pdf";
			}

			File parent = new File(buildFilePath);
			if (!parent.exists()) {
				parent.mkdirs();
			}
			String updatePath = buildFilePath + File.separator + fileName;
			logger.info("filePath is {}", updatePath);
			File file = new File(updatePath);
			fout = new FileOutputStream(file);
			fout.write(bytes);

			return file;
		} catch (Exception e) {
			logger.info("****************************buildPdf failed*************************************");
			e.printStackTrace();
			logger.info("Create pdf has exception :" + e);
			return null;
		} finally {
			if (fout != null) {
				try {
					fout.close();
				} catch (IOException e) {
					logger.error("buildPdfAndUpLoad fout.close exception:" + e);
				}
			}
		}
	}

	class pdfFileThread implements Runnable {

		private Map<String, Object> map;
		private String protocolNo;

		public pdfFileThread(Map<String, Object> map, String protocolNo) {
			this.map = map;
			this.protocolNo = protocolNo;
		}

		@Override
		public void run() {
			startTobuildPdfAndUpLoad(map, protocolNo);
		}

	}

	private static class PDFFileFilter implements FileFilter {

		@Override
		public boolean accept(File file) {

			if (file.isDirectory()) {

				return true;
			}
			String fileName = file.getName();

			return fileName.matches("(?i).+pdf$");
		}

	}

	@RequestMapping("/postApply")
	String postApply(@RequestBody JSONObject param) {
		logger.info("postApply param, {}", param);
		JSONObject response = new JSONObject();
		String recordNo = param.getString("recordNo");
		String usercode = param.getString("usercode");
		String limitTime = param.getString("timeLimit");
		String batchNo = param.getString("batchNo");
		String useRecordNo = param.getString("useRecordNo");
		if (recordNo == null || "".equals(recordNo) || usercode == null
				|| "".equals(usercode) || batchNo == null || "".equals(batchNo)
				|| useRecordNo == null || "".equals(useRecordNo)) {
			response.put("code", "9999");
			response.put("errMsg", "调用签约服务失败，参数不对。");
			return response.toJSONString();
		}
		logger.info("postApply param, {}", batchNo);
		// JSONObject uriVariables = new JSONObject();
		MultiValueMap<String, String> uriVariables = new LinkedMultiValueMap<String, String>();
		uriVariables.add("recordNo", recordNo);
		logger.info("=====uriVariables====>{}", uriVariables);
		String creditListStr = restTemplate.postForObject(creditUrl,
				uriVariables, String.class);
		JSONObject creditListJO = JSON.parseObject(creditListStr);

		if (creditListJO == null || creditListJO.isEmpty()) {
			response.put("code", "9999");
			response.put("errMsg", "获取授信信息失败");
			return response.toJSONString();
		} else if ("".equals(creditListJO.getString("code"))
				|| !"000000".equals(creditListJO.getString("code"))) {
			response.put("code", "9999");
			response.put("errMsg", "获取授信信息失败");
			return response.toJSONString();
		}
		String creditListJO1 = creditListJO.getString("creditList");
		JSONArray creditList = JSON.parseArray(creditListJO1.toString());
		JSONObject credit = creditList.getJSONObject(0);
		if (credit == null || credit.isEmpty()) {
			response.put("code", "9999");
			response.put("errMsg", "获取授信信息失败");
			return response.toJSONString();
		}
		credit.put("useRecordNo", useRecordNo);
		String useRes = this.applyUse(credit, limitTime, batchNo, useRecordNo);
		JSONObject useResJO = JSON.parseObject(useRes);
		String usecode = useResJO.getString("code");
		if ("9999".equals(usecode)) {
			response.put("code", "9999");
			response.put("errMsg", "调用放款服务失败");
			return response.toJSONString();
		} else {
			response.put("code", "0000");
			return response.toJSONString();
		}
	}

	/*
	 * String hessionClient() { logger.debug("service method {}", "debug");
	 * logger.info("service method {}", "info");
	 * logger.warn("service method {}", "warn");
	 * logger.error("service method {}", "error"); Map<String, Object> map =
	 * null; try { HessianProxyFactory factory = new HessianProxyFactory();
	 * HdfsService hdfsService= (HdfsService)factory.create(hdfsServiceUrl); }
	 * catch (Exception e) { e.printStackTrace(); } return map.toString(); }
	 */
	@RequestMapping("/sign")
	String sign(@RequestBody JSONObject param) {
		JSONObject response = new JSONObject();
		String recordNo = param.getString("recordNo");
		String protocolNo = param.getString("protocolNum");
		String usercode = param.getString("usercode");
		String limitTime = param.getString("timeLimit");
		String fileId = param.getString("fileId");
		String batchNo = param.getString("batchNo");
		String useRecordNo = param.getString("useRecordNo");
		String status = param.getString("status");
		if (recordNo == null || "".equals(recordNo) || protocolNo == null
				|| "".equals(protocolNo) || usercode == null
				|| "".equals(usercode) || fileId == null || "".equals(fileId)
				|| batchNo == null || "".equals(batchNo) || useRecordNo == null
				|| "".equals(useRecordNo)) {
			response.put("code", "9999");
			response.put("errMsg", "调用签约服务失败，参数不对。");
			return response.toJSONString();
		}

		// JSONObject uriVariables = new JSONObject();
		MultiValueMap<String, String> uriVariables = new LinkedMultiValueMap<String, String>();
		uriVariables.add("recordNo", recordNo);
		logger.info("=====uriVariables====>{}", uriVariables);
		String creditListStr = restTemplate.postForObject(creditUrl,
				uriVariables, String.class);
		JSONObject creditListJO = JSON.parseObject(creditListStr);

		if (creditListJO == null || creditListJO.isEmpty()) {
			response.put("code", "9999");
			response.put("errMsg", "获取授信信息失败");
			return response.toJSONString();
		} else if ("".equals(creditListJO.getString("code"))
				|| !"000000".equals(creditListJO.getString("code"))) {
			response.put("code", "9999");
			response.put("errMsg", "获取授信信息失败");
			return response.toJSONString();
		}
		String creditListJO1 = creditListJO.getString("creditList");
		JSONArray creditList = JSON.parseArray(creditListJO1.toString());
		JSONObject credit = creditList.getJSONObject(0);
		if (credit == null || credit.isEmpty()) {
			response.put("code", "9999");
			response.put("errMsg", "获取授信信息失败");
			return response.toJSONString();
		}

		logger.info("=======查询是否存在个人签章=======");
		// TODO 查询是否存在个人签章
		String sealData = "";
		String iden = credit.getString("certNo");
		String accountId = "";
		credit.put("useRecordNo", useRecordNo);

		if ("13".equals(status)) {
			logger.info("该授信流水签章已完成，但未同步合同给信托。");
			String sendResStr = this.protocolSend(recordNo, useRecordNo);
			JSONObject sendResJO = JSON.parseObject(sendResStr);
			if ("9999".equals(sendResJO.get("code"))) {
				response.put("code", "9999");
				return response.toJSONString();
			}
			
			String useRes = this.applyUse(credit, limitTime, batchNo, useRecordNo);
			JSONObject useResJO = JSON.parseObject(useRes);
			String usecode = useResJO.getString("code");
			if ("9999".equals(usecode)) {
				response.put("code", "9999");
				return response.toJSONString();
			}
			response.put("code", "0000");
			return response.toJSONString();
		} else if ("9".equals(status) || "11".equals(status)) {
			logger.info("该授信流水已申请签约，且已签章，但未申请放款。");
			String useRes = this.applyUse(credit, limitTime, batchNo,
					useRecordNo);
			JSONObject useResJO = JSON.parseObject(useRes);
			String usecode = useResJO.getString("code");
			if ("9999".equals(usecode)) {
				response.put("code", "9999");
				response.put("errMsg", "调用放款服务失败");
				return response.toJSONString();
			}
			response.put("code", "0000");
			return response.toJSONString();
		}

		boolean getSignFlag = false;
		Map<String, Object> personSign = contractService.queryPerSign(iden);
		if (personSign == null || personSign.isEmpty()) {
			JSONObject jo = this.getAccountId(credit);
			if (jo != null && !jo.isEmpty()) {
				if (jo.getString("RET_CODE") != ""
						&& jo.getString("RET_CODE") != null
						&& "0000".equals(jo.getString("RET_CODE"))) {
					JSONObject resultData = jo.getJSONObject("RESULT_DATA");
					if (resultData != null && !resultData.isEmpty()) {
						accountId = resultData.getString("AccountId");
						if ("".equals(accountId) || accountId == null) {
							// this.updateUseInfo(useRecordNo, "30", "2");
							response.put("code", "9999");
							response.put("errMsg", "百事通个人账户注册业务失败");
							return response.toJSONString();
						} else {
							JSONObject sealJO = this
									.signAcct(accountId, credit);
							if (sealJO != null && !sealJO.isEmpty()) {
								if (sealJO.getString("RET_CODE") != ""
										&& sealJO.getString("RET_CODE") != null
										&& "0000".equals(sealJO
												.getString("RET_CODE"))) {
									JSONObject resultData2 = sealJO
											.getJSONObject("RESULT_DATA");
									if (resultData2 != null
											&& !resultData2.isEmpty()) {
										sealData = resultData2
												.getString("sealData");
										getSignFlag = true;
										// TODO 把当前身份证相关的sealData保存到数据库
										contractService.insertPerSign(iden,
												accountId, sealData);
									}
								}
							}
						}
					}
				}
			} else {
				// this.updateUseInfo(useRecordNo, "30", "2");
				// this.updateCreditInfo(recordNo, usercode, protocolNo,
				// limitTime, "3", "8", "30");
				response.put("code", "9999");
				response.put("errMsg", "百事通个人账户注册业务失败");
				return response.toJSONString();
			}
		} else {
			getSignFlag = true;
			sealData = (String) personSign.get("sealData");
			accountId = (String) personSign.get("accountId");
		}
		if (!getSignFlag) {
			// this.updateUseInfo(useRecordNo, "30", "2");
			response.put("code", "9999");
			return response.toJSONString();
		}
		credit.put("fileId", fileId);
		JSONObject result = this.saveSign(accountId, sealData, credit);
		if (result != null && !result.isEmpty()) {
			JSONObject resultData = result.getJSONObject("RESULT_DATA");
			if (resultData != null && !resultData.isEmpty()) {
				String code = resultData.getString("code");
				boolean signFlag = false;
				if ("0".equals(code)) {
					// TODO 签章成功 description
					String fileId1 = resultData.getString("fileId");
					JSONObject signMap = new JSONObject();
					signMap.put("fileId", fileId1);
					signMap.put("protocolNo", protocolNo);
					logger.info("signContract param, {}", signMap);
					String signStr = this.signContract(signMap);
					logger.info("signContract response, {}", signStr);
					signFlag = true;
					this.updateUseInfo(useRecordNo, "20", "13");
					String sendResStr = this
							.protocolSend(recordNo, useRecordNo);
					JSONObject sendResJO = JSON.parseObject(sendResStr);
					if ("9999".equals(sendResJO.get("code"))) {
						response.put("code", "9999");
						signFlag = false;
					}
				} else {
					// TODO 签章不成功 description
					response.put("code", "9999");
					response.put("errMsg", resultData.getString("description"));
					logger.info("uriVariables====>{}", uriVariables);
					// this.updateCreditInfo(recordNo, usercode, protocolNo,
					// limitTime, "3", "8", "30");
					signFlag = false;
					// this.updateUseInfo(useRecordNo, "30", "2");
					return response.toJSONString();
				}
				if (signFlag) {
					String useRes = this.applyUse(credit, limitTime, batchNo,
							useRecordNo);
					JSONObject useResJO = JSON.parseObject(useRes);
					String usecode = useResJO.getString("code");
					if ("9999".equals(usecode)) {
						response.put("code", "9999");
						response.put("errMsg", "调用放款服务失败");
						return response.toJSONString();
					}
					this.updateCreditInfo(recordNo, usercode, protocolNo,
							limitTime, "4", "6", "20");
				}
			} else {
				// TODO 与百事通交换报文出错
				// this.updateUseInfo(useRecordNo, "30", "2");
				response.put("code", "9999");
				response.put("errMsg", "与百事通交换报文出错");
			}
		}
		return response.toJSONString();
	}

	private String protocolSend(String recordNo, String useRecordNo) {
		JSONObject response = new JSONObject();
		// 调用上传签约相关文件
		Map<String, String> packParam = new HashMap<String, String>();
		packParam.put("useRecordNo", useRecordNo);
		packParam.put("creditRecordNo", recordNo);
		logger.info("packContract param=====>{}", packParam);
		JSONObject packContractResult = restTemplate.postForObject(
				packContractUrl, packParam, JSONObject.class);
		logger.info("signAgree response=====>{}", packContractResult);
		String packCode = packContractResult.getString("code");
		if (packCode == null || "".equals(packCode) || !"000000".equals(packCode)) {
			logger.info("上传签约相关文件失败。");
			response.put("code", "9999");
		} else {
			logger.info("上传签约相关文件成功。");
			// 更新支用表
			JSONObject useMap = new JSONObject();
			useMap.put("recordNo", useRecordNo);
			useMap.put("packageId", packContractResult.get("fileId"));
			JSONObject useUpdateStr = restTemplate.postForEntity(
					drawServiceUpdateUseUrl, useMap, JSONObject.class)
					.getBody();
			JSONObject useResult = JSON.parseObject(useUpdateStr.toString());
			String useResultCode = useResult.getString("code");
			if (!"000000".equals(useResultCode)) {
				logger.info("=======更新支用表失败。=======");
			}
			MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
			JSONObject reqJson = new JSONObject();
			reqJson.put("appSeq", useRecordNo);
			reqJson.put("fileId", packContractResult.get("fileId"));
			reqJson.put("transCode", "04");
			reqJson.put("comment", "10");
			reqJson.put("orgCode", "21100005");
			reqJson.put("prodCode", "8007000001");
			params.add("topicName", "loan.orgGateway");
			params.add("messgeJsonBody", reqJson.toJSONString());

			JSONObject result = restTemplate.postForObject(masSendV2Url,
					params, JSONObject.class);
			logger.info("04盖章合同回传：", masSendV2Url + "/loan.orgGateway");
			logger.info("04盖章合同回传：" + params);
			logger.info("04盖章合同回传：" + result);
			String status = result.getString("status");
			if (status != null && "1".equals(status)) {
				this.updateUseInfo(useRecordNo, "20", "9");
				response.put("code", "0000");
			} else {
				response.put("code", "9999");
			}
		}

		return response.toJSONString();
	}

	private String applyUse(JSONObject credit, String limitTime,
			String batchNo, String useRecordNo) {
		// TODO 映射银行信息
		JSONObject response = new JSONObject();
		Map bankInfo = contractService.getBankCodeMapping(credit
				.getString("bankCode"));
		if (bankInfo == null) {
			response.put("code", "9999");
			response.put("errMsg", "签约银行映射数据未找到。");
			return response.toJSONString();
		}
		credit.put("xtBankCode", bankInfo.get("xtBankCode"));
		credit.put("xtProdCode", bankInfo.get("prodCode"));
		credit.put("xtPaymentModel", bankInfo.get("paymentModel"));
		credit.put("xtBankName", bankInfo.get("bankName"));
		// TODO 调用实时放款
		String postResult = this.postMoney(credit, limitTime, batchNo);
		JSONObject postRJO = JSON.parseObject(postResult);
		if (postRJO == null || postRJO.isEmpty()) {
			this.updateUseInfo(useRecordNo, "20", "11");
			response.put("code", "9999");
			response.put("errMsg", "调用放款服务失败");
			return response.toJSONString();
		}
		String postCode = postRJO.getString("retCode");
		if (postCode == null || "".equals(postCode) || !"0000".equals(postCode)) {
			this.updateUseInfo(useRecordNo, "20", "11");
			response.put("code", "9999");
			response.put("errMsg", "调用放款服务失败");
			return response.toJSONString();
		}
		String status = postRJO.getString("status");
		if (status == null || "".equals(status) || !"1".equals(status)) {
			this.updateUseInfo(useRecordNo, "20", "11");
			response.put("code", "9999");
			response.put("errMsg", "调用放款服务失败");
			return response.toJSONString();
		}
		this.updateUseInfo(useRecordNo, "20", "12");
		response.put("code", "0000");
		return response.toJSONString();
	}

	private void updateUseInfo(String recordNo, String useSignFlag,
			String status) {
		JSONObject useMap = new JSONObject();
		useMap.put("recordNo", recordNo);
		if (!"".equals(status)) {
			useMap.put("status", status);
		}
		if (!"".equals(useSignFlag)) {
			useMap.put("useSignFlag", useSignFlag);
		}

		JSONObject useUpdateStr = restTemplate.postForEntity(
				drawServiceUpdateUseUrl, useMap, JSONObject.class).getBody();
		JSONObject useResult = JSON.parseObject(useUpdateStr.toString());
		String useResultCode = useResult.getString("code");
		if (!"000000".equals(useResultCode)) {
			logger.info("=======更新支用表失败。=======");
		}
	}

	private void updateCreditInfo(String recordNo, String usercode,
			String protocolNo, String limitTime, String status2dfire,
			String status, String contSignFlag) {
		MultiValueMap<String, String> creditMap = new LinkedMultiValueMap<String, String>();
		creditMap.add("recordNo", recordNo);
		creditMap.add("lastModifyUid", usercode);
		creditMap.add("lastModifyTime", System.currentTimeMillis() + "");
		creditMap.add("contractNo", protocolNo);
		creditMap.add("lmtStartDate", this.getDate("0", 1));
		creditMap.add("lmtEndDate",
				this.getDate("1", Integer.parseInt(limitTime)));
		creditMap.add("status2dfire", status2dfire);
		creditMap.add("status", status);
		creditMap.add("contSignFlag", contSignFlag);
		creditMap.add("contSignTime", System.currentTimeMillis() + "");
		String creditUpdateStr = restTemplate.postForObject(creditUpdateUrl,
				creditMap, String.class);
		logger.info("creditUpdateStr response===>, {}", creditUpdateStr);
		logger.info("creditUpdateStr creditMap===>, {}", creditMap);
		JSONObject creditResult = JSON.parseObject(creditUpdateStr.toString());
		String creditResultCode = creditResult.getString("code");
		if (!"000000".equals(creditResultCode)) {
			logger.info("=======更新授信表失败。=======");
		}
	}

	private String postMoney(JSONObject credit, String limitTime, String batchNo) {
		// TODO 同步贷款信息
		JSONObject jo = new JSONObject();
		JSONObject reqJson = new JSONObject();
		// MultiValueMap params = new JSONObject();
		MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
		String newbatchNo = "A" + String.valueOf(System.currentTimeMillis());
		JSONObject uriVariables1 = new JSONObject();
		uriVariables1.put("batch_no", newbatchNo);
		uriVariables1.put("app_seq", credit.getString("useRecordNo"));
		if ("6".equals(limitTime)) {
			uriVariables1.put(
					"total_amt",
					new BigDecimal(Double.valueOf(credit
							.getString("sixMonthAmt")))
							.multiply(new BigDecimal(100))
							.setScale(0, BigDecimal.ROUND_HALF_UP).toString());
		} else if ("12".equals(limitTime)) {
			uriVariables1.put(
					"total_amt",
					new BigDecimal(Double.valueOf(credit
							.getString("twelveMonthAmt")))
							.multiply(new BigDecimal(100))
							.setScale(0, BigDecimal.ROUND_HALF_UP).toString());
		}
		uriVariables1.put("payment_model", credit.getString("xtPaymentModel"));
		if ("allinpal".equals(feededuct)) {
			uriVariables1.put("total_num", "1");
		} else {
			uriVariables1.put("total_num", "2");
		}
		uriVariables1.put("payment_no", "1");
		uriVariables1.put("payment_purpose", "101");
		uriVariables1.put("payment_type", "11");
		if ("6".equals(limitTime)) {
			uriVariables1.put(
					"payment_amt",
					new BigDecimal(Double.valueOf(credit
							.getString("sixMonthAmt")))
							.multiply(new BigDecimal(100))
							.setScale(0, BigDecimal.ROUND_HALF_UP).toString());
		} else if ("12".equals(limitTime)) {
			uriVariables1.put(
					"payment_amt",
					new BigDecimal(Double.valueOf(credit
							.getString("twelveMonthAmt")))
							.multiply(new BigDecimal(100))
							.setScale(0, BigDecimal.ROUND_HALF_UP).toString());
		}
		uriVariables1.put("payee_act_name", credit.getString("custName"));
		uriVariables1.put("payee_act_num", credit.getString("bankAcctNo"));
		uriVariables1.put("payee_act_type", "11");
		uriVariables1.put("payee_bank_name", credit.getString("xtBankName"));
		uriVariables1.put("payee_bank_code", credit.getString("xtBankCode"));
		uriVariables1.put("payee_mobile", credit.getString("mobile"));
		uriVariables1.put("payee_id_type", "0");
		uriVariables1.put("payee_id_no", credit.getString("certNo"));

		JSONObject uriVariables2 = new JSONObject();
		uriVariables2.put("batch_no", newbatchNo);
		uriVariables2.put("app_seq", credit.getString("useRecordNo"));
		if ("6".equals(limitTime)) {
			uriVariables2.put(
					"total_amt",
					new BigDecimal(Double.valueOf(credit
							.getString("sixMonthAmt")))
							.multiply(new BigDecimal(100))
							.setScale(0, BigDecimal.ROUND_HALF_UP).toString());
		} else if ("12".equals(limitTime)) {

			uriVariables2.put(
					"total_amt",
					new BigDecimal(Double.valueOf(credit
							.getString("twelveMonthAmt")))
							.multiply(new BigDecimal(100))
							.setScale(0, BigDecimal.ROUND_HALF_UP).toString());
		}
		uriVariables2.put("payment_model", credit.getString("xtPaymentModel"));
		uriVariables2.put("total_num", "2");
		uriVariables2.put("payment_no", "2");
		uriVariables2.put("payment_purpose", "102");
		uriVariables2.put("payment_type", "12");
		uriVariables2.put("settlement_flag", "0001");
		if ("6".equals(limitTime)) {
			uriVariables2.put(
					"payment_amt",
					new BigDecimal(Double.valueOf(credit
							.getString("sixMonthAmt")))
							.multiply(new BigDecimal(sixManageRate))
							.multiply(new BigDecimal(100))
							.setScale(0, BigDecimal.ROUND_HALF_UP).toString());
		} else if ("12".equals(limitTime)) {
			uriVariables2.put(
					"payment_amt",
					new BigDecimal(Double.valueOf(credit
							.getString("twelveMonthAmt")))
							.multiply(new BigDecimal(twlManageRate))
							.multiply(new BigDecimal(100))
							.setScale(0, BigDecimal.ROUND_HALF_UP).toString());
		}
		uriVariables2.put("payee_act_name", credit.getString("custName"));
		uriVariables2.put("payee_act_num", credit.getString("bankAcctNo"));
		uriVariables2.put("payee_act_type", "11");
		uriVariables2.put("payee_bank_name", credit.getString("xtBankName"));
		uriVariables2.put("payee_bank_code", credit.getString("xtBankCode"));
		uriVariables2.put("payee_mobile", credit.getString("mobile"));
		uriVariables2.put("payee_id_type", "0");
		uriVariables2.put("payee_id_no", credit.getString("certNo"));

		JSONArray jr = new JSONArray();
		jr.add(uriVariables1);
		if (!"allinpal".equals(feededuct)) {
			jr.add(uriVariables2);
		}
		reqJson.put("realtimePayInfo", jr);
		reqJson.put("app_seq", credit.getString("useRecordNo"));
		reqJson.put("batch_no", batchNo);
		reqJson.put("transCode", "20");
		reqJson.put("comment", "10");
		reqJson.put("orgCode", "21100005");
		reqJson.put("prodCode", "8007000001");
		params.add("topicName", "loan.orgGateway");
		params.add("messgeJsonBody", reqJson.toJSONString());

		String result = restTemplate.postForObject(masSendV2Url, params,
				String.class);
		logger.info("调用信托放款管理：", masSendV2Url + "/loan.orgGateway");
		logger.info("调用信托放款管理参数：" + params);
		logger.info("调用信托放款管理结果：" + result);

		return result;
	}

	private String getDate(String type, int months) {
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

	private JSONObject getAccountId(JSONObject credit) {
		MultiValueMap<String, String> uriVariables = new LinkedMultiValueMap<>();
		uriVariables.add("TRANS_CODE", "01");
		uriVariables.add("TRANS_SN", credit.getString("useRecordNo"));
		uriVariables.add("CO_ORG_CODE", "902");
		uriVariables.add("ID_NO", credit.getString("certNo"));
		uriVariables.add("REAL_NAME", credit.getString("custName"));
		uriVariables.add("PHONE", credit.getString("mobile"));
		uriVariables.add("TITLE", "私营企业主");
		String result = this.sendBaishitongUrl(uriVariables);
		// JSON转对象，此处转为map
		JSONObject map = com.alibaba.fastjson.JSONObject.parseObject(result);
		logger.info("result map is :" + map);
		return map;
	}

	private JSONObject signAcct(String accountId, JSONObject credit) {
		MultiValueMap<String, String> uriVariables = new LinkedMultiValueMap<>();
		uriVariables.add("TRANS_CODE", "02");
		uriVariables.add("TRANS_SN", credit.getString("useRecordNo"));
		uriVariables.add("CO_ORG_CODE", "902");
		uriVariables.add("ACCOUNT_ID", accountId);
		uriVariables.add("TEMPLATE_TYPE", "10");
		uriVariables.add("COLOR", "0");
		String result = this.sendBaishitongUrl(uriVariables);
		JSONObject map = com.alibaba.fastjson.JSONObject.parseObject(result);
		logger.info("result map is :" + map);
		return map;
	}

	private JSONObject saveSign(String accountId, String sealData,
			JSONObject credit) {
		logger.info("saveSign param===>, {}", credit);
		TLoanProtocol tLoanProtocol = new TLoanProtocol();
		tLoanProtocol.setCreditRecordNo(credit.get("recordNo").toString());
		MultiValueMap<String, String> uriVariables = new LinkedMultiValueMap<>();
		uriVariables.add("TRANS_CODE", "03");
		uriVariables.add("TRANS_SN", credit.getString("useRecordNo"));
		uriVariables.add("CO_ORG_CODE", "902");
		uriVariables.add("ACCOUNT_ID", accountId);
		uriVariables.add("SEAL_DATA", sealData);
		uriVariables.add("SIGN_ID", System.currentTimeMillis() + "");
		uriVariables.add("FILE_ID", credit.getString("fileId"));
		uriVariables.add("SIGN_TYPE", "3");
		uriVariables.add("POS_TYPE", "1");
		uriVariables.add("KEY", "%LqNGgwZ%");

		String result = this.sendBaishitongUrl(uriVariables);
		JSONObject map = com.alibaba.fastjson.JSONObject.parseObject(result);
		logger.info("result map is :" + map);
		return map;
	}

	private String sendBaishitongUrl(MultiValueMap<String, String> uriVariables) {
		uriVariables.add("topicName", "coAgency.baishitong");
		String result = restTemplate.postForObject(masSendUrl, uriVariables,
				String.class);
		logger.info("调用电子签章url，coAgency.baishitong");
		logger.info("调用电子签章url，coAgency.baishitong param, {}", uriVariables);
		logger.info("调用电子签章url，coAgency.baishitong response, {}", result);
		return result;
	}

	private String getVersion(String type) {
		if ("0".equals(type)) {
			return WTKKVersion;
		} else if ("1".equals(type)) {
			return KHFWVersion;
		} else if ("2".equals(type)) {
			return KKSQSVersion;
		} else if ("3".equals(type)) {
			return ZXCXVersion;
		} else if ("4".equals(type)) {
			return DKHTVersion;
		} else if ("5".equals(type)) {
			return YHZCVersion;
		} else {
			return null;
		}
	}
}
