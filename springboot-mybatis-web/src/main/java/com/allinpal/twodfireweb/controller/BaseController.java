package com.allinpal.twodfireweb.controller;  
  
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.allinpal.twodfireweb.RedisConfig;
import com.allinpal.twodfireweb.util.RedisUtils;  

public class BaseController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private RedisUtils redisUtils;
	
    public String getOpenId(HttpServletRequest request){  
    	String cacheKey = getCacheKeyFromRequest(request);
    	if (!StringUtils.isEmpty(cacheKey))
    	{
	    	return (String)redisUtils.get(cacheKey);
    	}else{
	    	return null;
    	}
    }
    
    public void setOpenId(String openId, HttpServletRequest request){  
    	String cacheKey = getCacheKeyFromRequest(request);
    	if (!StringUtils.isEmpty(cacheKey))
    	{
	    	redisUtils.set(cacheKey, openId, RedisConfig.expireTime);
    	}else{
    		logger.info("get cacheKey from request empty");
    	}
    }
    
    protected String getCacheKeyFromRequest(HttpServletRequest request)
    {
    	String cacheKey = null;
        Cookie[] cookies = request.getCookies();
        if(null != cookies){            
            for (Cookie cookie : cookies)
            {
                if ("wxOpenId".equals(cookie.getName()))
                {
                    cacheKey = cookie.getValue();
                }
            }
        }
        if(StringUtils.isEmpty(cacheKey)){
        	HttpSession session = request.getSession();
        	if(session != null){
        		Object keyObject = session.getAttribute("wxOpenId");
        		if(keyObject != null){
        			cacheKey = keyObject.toString();
        		}
        	}
        }
        return cacheKey;
    }
    
    
    protected void setCacheKeyToResponse(HttpServletResponse response){
    	String cacheKey = "CookieId" + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
		Cookie cookie = new Cookie("wxOpenId", cacheKey);
		response.addCookie(cookie);
    }
}