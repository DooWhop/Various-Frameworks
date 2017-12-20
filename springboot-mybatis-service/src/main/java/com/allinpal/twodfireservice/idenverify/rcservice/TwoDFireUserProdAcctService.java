package com.allinpal.twodfireservice.idenverify.rcservice;

import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.allinpal.twodfireservice.idenverify.domain.BankCard;
import com.allinpal.twodfireservice.idenverify.domain.ProdAcct;
import com.allinpal.twodfireservice.idenverify.mapper.IdenVerifyMapper;
import com.allinpal.twodfireservice.idenverify.util.InterfaceServiceUtil;

@Service
public class TwoDFireUserProdAcctService {
		
	@Value("${user.saveProdAcct.url}")
	private String SAVE_PRODACCT_URL;
	
	@Autowired
	private IdenVerifyMapper idenVerifyMapper;
	
	@Autowired
	private RestTemplate restTemplate;
	
	public String saveProdBank(BankCard bankCard, String merchantNo, String mobile) throws Exception{
				
		ProdAcct prodAcct = new ProdAcct();
		String busiCertNo = idenVerifyMapper.getBusiCerNoByMerchantNo(merchantNo);		
		prodAcct.setBusiCertNo(busiCertNo);		
		prodAcct.setAcctNo(bankCard.getAcctNo());
		prodAcct.setBankIdenCode(bankCard.getBankIdenCode());
		prodAcct.setBankIdenName(bankCard.getBankIdenName());
		prodAcct.setCertNo(bankCard.getCertNo());
		prodAcct.setLegalName(bankCard.getAcctName());
		prodAcct.setMerchantNo(merchantNo);
		prodAcct.setMobile(mobile);
		prodAcct.setProdCode("9002000003");//二维火产品code
		prodAcct.setUserCardId(bankCard.getRecordNo());
		prodAcct.setUserId(bankCard.getUserId());		
		Map<String, String> params = BeanUtils.describe(prodAcct);
		MultiValueMap<String, String> prodAcctMap = InterfaceServiceUtil.convertMap2MultiValueMap(params);
        String result = restTemplate.postForObject(SAVE_PRODACCT_URL, prodAcctMap, String.class);
        return result;	
	}

}
