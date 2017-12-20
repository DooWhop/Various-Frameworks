package com.allinpal.twodfireservice.user.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.allinpal.twodfireservice.user.domain.ProdAcct;

@Mapper
public interface ProdAcctMapper {
	List<ProdAcct> queryProdAcct(ProdAcct prodAcct);
	
	int saveProdAcct(ProdAcct prodAcct);
}
