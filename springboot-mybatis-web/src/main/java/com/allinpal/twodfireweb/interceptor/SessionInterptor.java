package com.allinpal.twodfireweb.interceptor;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.alibaba.fastjson.JSON;
import com.allinpal.twodfireweb.util.RedisUtils;

@Component
public class SessionInterptor extends HandlerInterceptorAdapter {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private RedisUtils redisUtils;
	
	private String homeUrl = "/home";
	private String loginUrl = "/initUser";
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		logger.info("收到请求 v ");
		logger.info(request.getRequestURL() + JSON.toJSONString(request.getParameterMap()));
		
		try {
			// 获取cachekey, 没有值则往response加一个
			String cacheKey = getCacheKeyFromCookie(request, response);
			String requestOpenId = request.getParameter("openId");//用于校验是否复制链接跳转
			
			// 看redis有没有值
			Object tokenObj = redisUtils.get(cacheKey);
			String openId = tokenObj != null ? tokenObj.toString() : null;
			logger.info("preHandle-h5-getCacheKeyFromRedis:" + cacheKey + " openId:" + openId);
			// redis没有值，引导用户去授权页面,跳转首页
			if (StringUtils.isEmpty(openId)) {//新创建或者redis没值，直接跳转登录注册页，此处没必要判断是否新创建，同一个结果
				logger.info("preHandle-h5-redis中没有openId，返回false，forward:" + loginUrl);
				request.setAttribute("enteraction", request.getRequestURI());
				if(!StringUtils.isEmpty(request.getRequestURI()) && request.getRequestURI().indexOf(loginUrl) >= 0){
					return true;
				}else{
					request.getRequestDispatcher(loginUrl).forward(request,response);
					return false;
				}
			}else{
				//requestOpenId不为空的情况需要比较,校验是否复制链接跳转
				if(!StringUtils.isEmpty(requestOpenId) && !requestOpenId.equals(openId)){
					logger.info("preHandle-h5-redis中openId和requestOpenId不一致，返回false，forward:" + loginUrl);
					request.getRequestDispatcher(homeUrl).forward(request,response);
					return false;
				}
				
				// redis有openId值，刷新openId有效时间，返回通过
				redisUtils.set(cacheKey, openId, 60*60L);
				logger.info("preHandle-h5-redis中有openId，刷新openId有效时间, 返回true");
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private String getCacheKeyFromCookie(HttpServletRequest request, HttpServletResponse response) {
		String cacheKey = null;
		Cookie[] cookies = request.getCookies();
		if (null != cookies) {
			for (Cookie cookie : cookies) {
				if ("wxOpenId".equals(cookie.getName())) {
					cacheKey = cookie.getValue();
					logger.info("getCookieFromRequest cacheKey:" + cacheKey);
				}
			}
		}

		if (StringUtils.isEmpty(cacheKey)) {
			cacheKey = "CookieId" + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
			Cookie cookie = new Cookie("wxOpenId", cacheKey);
			//未来可能分多产品，留做后用
//			cookie.setDomain(CommonConstants.DOMAIN);
//			cookie.setPath("/");
			response.addCookie(cookie);
			
			//request取不到cookie的时候，此处一定要向request里面加个cacheKey,否则BaseController可能获取不到cacheKey
			HttpSession session = request.getSession();
			session.setAttribute("wxOpenId", cacheKey);
			
			// cacheKey为空，创建新cacheKey并response给客户端
			logger.info("preHandle-addCacheKeyToCookieAndSession cacheKey:" + cacheKey);
		}
		return cacheKey;
	}
	
}
