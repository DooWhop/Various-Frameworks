package com.allinpal.twodfireservice.user.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.allinpal.twodfireservice.user.domain.ProdAcct;
import com.allinpal.twodfireservice.user.mapper.ProdAcctMapper;

/**
 * @author admin
 * 贷款账户service
 */
@Service("prodAcctService")
public class ProdAcctService {
	@Autowired
	public ProdAcctMapper prodAcctMapper;
	
	public List<ProdAcct> queryUserInfo(ProdAcct prodAcct) {
		return prodAcctMapper.queryProdAcct(prodAcct);
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public int saveProdAcct(ProdAcct prodAcct) {
		int cnt = 0;
		try {
		    cnt = prodAcctMapper.saveProdAcct(prodAcct);
		    return cnt;
		} catch (Exception e) {
			throw new RuntimeException("saveProdAcct插入数据(cnt: "+cnt+")将回滚!");
		}
		
	}
}
