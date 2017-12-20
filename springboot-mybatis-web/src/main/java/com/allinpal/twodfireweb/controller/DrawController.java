package com.allinpal.twodfireweb.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.allinpal.twodfireweb.request.CreditReq;
import com.allinpal.twodfireweb.util.GenerateIdsUtil;

@Controller
public class DrawController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private RestTemplate restTemplate;

	@Value("${draw.url}")
	private String drawUrl;

	@Value("${creditService.query.url}")
	private String creditUrl;

	@Value("${contract.url}")
	private String contractUrl;

	@RequestMapping("/drawApply")
	public String drawApply(CreditReq credit, Model model,
			HttpServletRequest request) {
		logger.info("drawApply=====>{}", credit);
		String merchantNo = credit.getMerchantNo();
		try {
			String recordNo = credit.getRecordNo();
			String lmtserno = credit.getLmtSerno();
			String timeLimit = credit.getUseTerm();
			String prodcode = credit.getProdCode();
			String usercode = credit.getUserId();
			if ("".equals(recordNo) || "".equals(timeLimit)) {
				logger.info("=====>recordNo or timeLimit is null");
				model.addAttribute("merchantNo",
						credit.getMerchantNo());
				return "draw/error";
			}
			JSONObject param = new JSONObject();
			param.put("recordNo", recordNo);
			param.put("merchantNo", merchantNo);
			param.put("lmtserno", lmtserno);
			param.put("usercode", usercode);
			if ("06M".equals(timeLimit)) {
				param.put("timeLimit", "6");
			} else {
				param.put("timeLimit", "12");
			}
			param.put("prodcode", prodcode);
			// TODO 区分当前recordNo是否已申请支用
			String drawApplyQueryUrl = drawUrl + "/queryUseInfo";
			JSONObject queryParam = new JSONObject();
			queryParam.put("creditRecordNo", credit.getRecordNo());
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
			} else if ("9999".equals(queryRes.get("code"))) {
				model.addAttribute("merchantNo", merchantNo);
				return "draw/error";
			}
			String drawApplyUrl = drawUrl + "/apply";
			
			logger.info("drawApplyUrl=====>" + drawApplyUrl);
			logger.info("drawApply param=====>" + param.toString());
			JSONObject restResult = restTemplate.postForObject(drawApplyUrl,
					param, JSONObject.class);
			logger.info("restResult=====>" + restResult);
			logger.debug("request from {} result is {}", drawApplyUrl,
					restResult);
			String code = restResult.getString("code");
			if (code != null && !"".equals(code)) {
				if ("0000".equals(code)) {
					JSONObject appData = restResult.getJSONObject("appData");
					model.addAttribute("applyData", appData);
					
					String byte1 = appData.getString("byte");
					if (byte1 != null && !"".equals(byte1)) {
						String current = this.getClass().getResource("/").getPath() + "static/pdfjs/";
						logger.info("current=====>" + current);
						File file = new File(current);
						if (!file.exists() && !file.isDirectory()) {
							logger.info("所在目录不存在，创建目录，{}", current);
							file .mkdir();
						}
						String fileID = current + appData.getString("fileId") + ".pdf";
						File pdfFile = new File(fileID);
						if (!pdfFile.exists()) {
							byte[] bytes = Base64.decodeBase64(appData.getString("byte"));
							FileOutputStream fileOutputStream = new FileOutputStream(new File(fileID));
							fileOutputStream.write(bytes);
							fileOutputStream.close();
						}
						String imgId = current + appData.getString("fileId") + ".jpg";
						File imgFile = new File(imgId);
						if (!imgFile.exists()) {
							this.pdf2multiImage(fileID, imgId);
						}
					}
					// 生成合同
					Map<String, Object> protocolParams = new HashMap<String, Object>();
					protocolParams.put("creditRecordNo", credit.getRecordNo());
					List<Map<String, Object>> protocolList = new ArrayList<Map<String, Object>>();
					Map<String, Object> KHXYmap = new HashMap<String, Object>();
					KHXYmap.put("protocolType", "1");
					KHXYmap.put("protocolNo", request.getParameter("protocolNoKHFW"));
					Map<String, Object> KKmap = new HashMap<String, Object>();
					KKmap.put("protocolType", "0");
					KKmap.put("protocolNo", request.getParameter("protocolNoWTKK"));
					protocolList.add(KHXYmap);
					protocolList.add(KKmap);
					protocolParams.put("protocols", protocolList);
					protocolParams.put("useRecordNo", appData.getString("useRecordNo"));
					String signAgreeUrl = contractUrl + "/signAgreement";
					logger.info("signAgree signAgreeUrl=====>{}", signAgreeUrl);
					logger.info("signAgree param=====>{}", protocolParams);
					String signAgreeResult = restTemplate.postForObject(signAgreeUrl,
							protocolParams, String.class);
					logger.info("signAgree response=====>{}", signAgreeResult);
					
					return "draw/drawApply";
				} else {
					model.addAttribute("message",
							restResult.getString("errMsg"));
					model.addAttribute("merchantNo", merchantNo);
					return "draw/error";
				}
			} else {
				return "draw/error";
			}
		} catch (Exception e) {
			logger.info("drawApply exception : ", e);
			model.addAttribute("code", "000002");
			model.addAttribute("message", "很抱歉，系统繁忙，请稍后再试");
			model.addAttribute("merchantNo", merchantNo);
			return "draw/error";
		}
	}
	
	
	@RequestMapping("/showKKHTProtocal")
	public String showKKHTProtocal(Model model, HttpServletRequest request) {
		String merchantNo = request.getParameter("merchantNo");
		try {
			String fileId = request.getParameter("fileId");
			String current = "pdfjs/";
			String imgId = current + fileId + ".jpg";
			model.addAttribute("imgId", imgId);
			return "draw/KKHT";
		} catch (Exception e) {
			logger.info("showKKSQSProtocal exception : ", e);
			model.addAttribute("code", "000002");
			model.addAttribute("message", "很抱歉，系统繁忙，请稍后再试");
			model.addAttribute("merchantNo", merchantNo);
			return "draw/error";
		}
	}

	@RequestMapping("/showKKSQSProtocal")
	public String showKKSQSProtocal(Model model, HttpServletRequest request) {
		String merchantNo = request.getParameter("merchantNo");
		try {
			String recordNo = request.getParameter("recordNo");
			if (recordNo == null || "".equals(recordNo)) {
				model.addAttribute("merchantNo", merchantNo);
				return "draw/error";
			}
			// 协议参数
			SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月dd日");
			model.addAttribute("signDate", df.format(new Date()));// 协议签订日期

			MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
			map.add("recordNo", recordNo);
			String result = restTemplate.postForObject(creditUrl, map,
					String.class);
			JSONObject resultJO = JSON.parseObject(result);
			if (resultJO == null || resultJO.isEmpty()) {
			} else if ("".equals(resultJO.getString("code"))
					|| !"000000".equals(resultJO.getString("code"))) {
			}
			String creditList = resultJO.getString("creditList");
			JSONArray creditList1 = JSON.parseArray(creditList.toString());
			JSONObject credit = creditList1.getJSONObject(0);
			model.addAttribute("credit", credit);
			logger.info("showKKSQSProtocal model====>{} ", model);
			return "draw/drawKKSQS";
		} catch (Exception e) {
			logger.info("showKKSQSProtocal exception : ", e);
			model.addAttribute("code", "000002");
			model.addAttribute("message", "很抱歉，系统繁忙，请稍后再试");
			model.addAttribute("merchantNo", merchantNo);
			return "draw/error";
		}
	}

	@RequestMapping("/showZXCXSBSQSProtocal")
	public String showZXCXSBSQSProtocal(Model model, HttpServletRequest request) {
		String merchantNo = request.getParameter("merchantNo");
		try {
			String custName = request.getParameter("custName");
			if (custName == null || "".equals(custName)) {
				model.addAttribute("merchantNo", merchantNo);
				return "draw/error";
			}
			// 协议参数
			SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月dd日");
			model.addAttribute("signDate", df.format(new Date()));// 协议签订日期
			model.addAttribute("custName", custName);
			logger.info("showKKSQSProtocal model====>{} ", model);
			return "draw/drawZXCXSBSQS";
		} catch (Exception e) {
			logger.info("showKKSQSProtocal exception : ", e);
			model.addAttribute("code", "000002");
			model.addAttribute("message", "很抱歉，系统繁忙，请稍后再试");
			model.addAttribute("merchantNo", merchantNo);
			return "draw/error";
		}
	}

	@RequestMapping("/applySendVerifyCode")
	@ResponseBody
	public String ajaxSendVerifyCode(Model model, HttpServletRequest request) {
		JSONObject param = new JSONObject();
		String mobile = request.getParameter("mobile");
		param.put("mobile", mobile);
		String drawApplyUrl = drawUrl + "/ajaxSendVerifyCode";
		String result = restTemplate.postForObject(drawApplyUrl, param,
				String.class);
		logger.info("ajaxSendVerifyCode response,{}", result);
		// 协议参数
		return result;

	}
	
	public static void yPic(List<BufferedImage> piclist, String outPath) {// 纵向处理图片  
        if (piclist == null || piclist.size() <= 0) {  
            System.out.println("图片数组为空!");  
            return;  
        }  
        try {  
            int height = 0, // 总高度  
            width = 0, // 总宽度  
            _height = 0, // 临时的高度 , 或保存偏移高度  
            __height = 0, // 临时的高度，主要保存每个高度  
            picNum = piclist.size();// 图片的数量  
            int[] heightArray = new int[picNum]; // 保存每个文件的高度  
            BufferedImage buffer = null; // 保存图片流  
            List<int[]> imgRGB = new ArrayList<int[]>(); // 保存所有的图片的RGB  
            int[] _imgRGB; // 保存一张图片中的RGB数据  
            for (int i = 0; i < picNum; i++) {  
                buffer = piclist.get(i);  
                heightArray[i] = _height = buffer.getHeight();// 图片高度  
                if (i == 0) {  
                    width = buffer.getWidth();// 图片宽度  
                }  
                height += _height; // 获取总高度  
                _imgRGB = new int[width * _height];// 从图片中读取RGB  
                _imgRGB = buffer.getRGB(0, 0, width, _height, _imgRGB, 0, width);  
                imgRGB.add(_imgRGB);  
            }  
            _height = 0; // 设置偏移高度为0  
            // 生成新图片  
            BufferedImage imageResult = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);  
            for (int i = 0; i < picNum; i++) {  
                __height = heightArray[i];  
                if (i != 0) _height += __height; // 计算偏移高度  
                imageResult.setRGB(0, _height, width, __height, imgRGB.get(i), 0, width); // 写入流中  
            }  
            File outFile = new File(outPath);  
            ImageIO.write(imageResult, "jpg", outFile);// 写图片  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
	private static void pdf2multiImage(String pdfFile, String outpath) {  
        try {  
            InputStream is = new FileInputStream(pdfFile);  
            PDDocument pdf = PDDocument.load(is);  
            int actSize  = pdf.getNumberOfPages();  
            List<BufferedImage> piclist = new ArrayList<BufferedImage>();  
            for (int i = 0; i < actSize; i++) {
            BufferedImage  image = new PDFRenderer(pdf).renderImageWithDPI(i,130,ImageType.RGB);  
                piclist.add(image); 
            }  
            yPic(piclist, outpath);  
            is.close();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    } 
}
