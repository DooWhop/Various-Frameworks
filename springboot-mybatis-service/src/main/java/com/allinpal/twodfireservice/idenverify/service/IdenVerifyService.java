package com.allinpal.twodfireservice.idenverify.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.allinpal.twodfireservice.AppConfig;
import com.allinpal.twodfireservice.idenverify.domain.BankCard;
import com.allinpal.twodfireservice.idenverify.domain.IdenFile;
import com.allinpal.twodfireservice.idenverify.mapper.IdenVerifyMapper;
import com.allinpal.twodfireservice.util.GenerateIdsUtil;
import com.thfund.commonService.file.hessian.FileService;

@Service
public class IdenVerifyService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	
	@Autowired
	private IdenVerifyMapper idenverifyMapper;
	
	@Autowired
	private AppConfig appConfig;
	
	@Autowired
	private FileService fileService;
	
	@Value("${fileDownload.url}")
	private String FILE_DOWNLOAD_URL;
	
	@Transactional(propagation = Propagation.REQUIRED)
	public boolean idenFileUpload(String file, IdenFile idenFile){
			
		byte[] fileBytes;
		try {
			 //1.上传photo返回fileId
			 //1.1 解码文件base64
			fileBytes = com.thfund.commons.security.Base64Utils.decode(file.replace("data:image/jpeg;base64,",""));		
			 //1.2上传到云存储
			InputStream is = new ByteArrayInputStream(fileBytes);
			Map<String, Object> paramMap = new HashMap<String, Object>();
			String fileName = idenFile.getUserId()+"_"+idenFile.getIdenFace()+".jpeg";
			paramMap.put("id", idenFile.getProdCode());
			paramMap.put("fileType", "4");
			paramMap.put("fileName", fileName);
			paramMap.put("serviceName", "idenVerifyService/ocrVerify");
			Map<String, Object> result = fileService.upload(paramMap, is);
			String fileId = result!=null?(String) result.get("fileId"):"";		
			//2.保存文件信息到 2dfire.T_USER_IDEN_FILE
			if(StringUtils.isEmpty(fileId)){
				logger.info("上传文件失败,fileId不能为空");
				return false;
			}
			idenFile.setFileId(fileId);
			idenFile.setFileName(fileName);
			int cnt = saveIdenFile(idenFile);
			if(cnt!=1){
				logger.info("保存用户文件表失败,fileId:{}", idenFile.getFileId());
				return false;
			}
		} catch (Exception e) {			
			logger.error("上传文件发生异常! Exception is :{}", e);
			return false;
		}
		return true;
	}
	
	
	@Transactional(propagation = Propagation.REQUIRED)
	public int saveIdenFile(IdenFile idenFile) {
			
		try {		
			long time = System.currentTimeMillis();
			idenFile.setCreateTime(time);
			idenFile.setCreateUid("system");
			idenFile.setLastModifyTime(time);
			idenFile.setLastModifyUid("system");
			idenFile.setRecordNo(GenerateIdsUtil.generateId(appConfig.getAppIp()));
			idenFile.setStatus("10");//成功
			idenFile.setFilePath(FILE_DOWNLOAD_URL.concat("?fileId="));
			//3.保存到二维火数据库用户认证表	
			int updCnt = idenverifyMapper.updateIdenFile(idenFile);
			logger.info("更新 ocr_state 为 20 updCnt:{}",updCnt);
			idenFile.setOcrState("10");
			int cnt = idenverifyMapper.saveIdenFile(idenFile);	
			logger.info("插入saveIdenFile cnt:{}",cnt);
			return cnt;
		} catch (Exception e) {			
			logger.error("保存用户认证文件信息发生异常! Exception is :{}", e);
			throw new RuntimeException("用户认证文件信息入库异常回滚");
		}		
	}
		
	/**根据手机号查询userId
	 * 
	 * @param phone
	 * @return
	 */
	public String getTlbUserOidByUserId(String userId){
		
		try {
			String tlbUserOid = idenverifyMapper.getTlbOidByUserId(userId);
			return tlbUserOid;
		} catch (Exception e) {
			logger.error("根据用户标识查询通联宝账户主键oid异常! Exception is :{}", e);
			return null;
		}
		
	}
	
	/**查询身份文件
	 * 
	 * @param idenFile
	 * @return
	 */
	public int getCountOfIdenFile(IdenFile idenFile){
		try {
			int cnt = idenverifyMapper.queryIdenFile(idenFile);
			return cnt;
		} catch (Exception e) {
			logger.error("查询身份文件发生异常! Exception is :{}", e);
			return -1;
		}			
	}
	
	
	/**查询是否重复绑卡
	 * 
	 * @param phone
	 * @return
	 */
	public int getCountOfRepeatCard(BankCard bankCard){
		
		try {
			int cnt = idenverifyMapper.queryBankCard(bankCard);
			return cnt;
		} catch (Exception e) {
			logger.error("查询重复绑卡发生异常! Exception is :{}", e);
			return -1;
		}		
	}
	
	/**查询支持的银行卡列表
	 * 
	 * @param prodCode
	 * @return
	 */
	public Map<String, Object> querySupportCard(String prodCode){
		
		try {
			List<Map<String, Object>> bankList = idenverifyMapper.getSupportBanks(prodCode);
			Map<String, Object> bankMap = new HashMap<String, Object>();
			if(bankList!=null && bankList.size()>0){
				for (Map<String, Object> map : bankList) {
					bankMap.put((String) map.get("bank_iden_code"), map.get("bank_iden_name"));
				}
			}		
			return bankMap;						
		} catch (Exception e) {
			logger.error("查询支持的银行卡列表发生异常! Exception is :{}", e);
			return null;
		}		
	}
	
	//方法的@Transactional会覆盖类上面声明的事务;rollbackFor可定义异常回滚类型,默认是RuntimeException或者Error
	/**绑卡信息入库
	 * 
	 * @param bankCard
	 * @return
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public int saveCard(BankCard bankCard) {
			
		try {		
			long time = System.currentTimeMillis();
			bankCard.setCertType("10100");//身份证-10100
			bankCard.setCreateTime(time);
			bankCard.setCreateUid("system");
			bankCard.setLastModifyTime(time);
			bankCard.setLastModifyUid("system");
			bankCard.setRecordNo(GenerateIdsUtil.generateId(appConfig.getAppIp()));
			bankCard.setStatus("1");			
			//3.保存到二维火数据库			
			int cnt = idenverifyMapper.saveBankCard(bankCard);				
			return cnt;
		} catch (Exception e) {			
			logger.error("保存绑卡信息发生异常! Exception is :{}", e);
			throw new RuntimeException("绑卡信息入库异常回滚");
		}		
	}	
	
}
