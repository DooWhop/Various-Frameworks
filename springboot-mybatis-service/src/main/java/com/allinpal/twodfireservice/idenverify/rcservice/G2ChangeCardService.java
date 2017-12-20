package com.allinpal.twodfireservice.idenverify.rcservice;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.allinpal.twodfireservice.idenverify.domain.BankCard;

@Service
public class G2ChangeCardService {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
		
	@Value("${mas.send.url}")
	private String masSendUrl;
	
	@Autowired
	private RestTemplate restTemplate;
	
	public String changeCard(Map<String, Object> oldCard, BankCard newCard) throws Exception {
		
		logger.info("---------二代服务loan.changeCard-----------");
		MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>();
		paramMap.add("topicName", "loan.changeCard");
		paramMap.add("idNo", newCard.getCertNo());
		paramMap.add("custName", newCard.getAcctName());
		paramMap.add("oldBankName", (String) oldCard.get("bankname"));
		paramMap.add("oldBankCode", (String) oldCard.get("bankcode"));
		paramMap.add("oldAcctNo", (String) oldCard.get("acctno"));
		paramMap.add("newBankName", newCard.getBankIdenName());
		paramMap.add("newBankCode", newCard.getBankIdenCode());
		paramMap.add("newAcctNo", newCard.getAcctNo());
		logger.info("postForObject loan.changeCard param is:{}", paramMap);
		String res = restTemplate.postForObject(masSendUrl, paramMap, String.class);		
		return res;
		
	}	
		
}
