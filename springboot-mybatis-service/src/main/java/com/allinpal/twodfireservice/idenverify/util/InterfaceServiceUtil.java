package com.allinpal.twodfireservice.idenverify.util;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class InterfaceServiceUtil {
	
	private static String MD5_KEY = "1234567890";
	
	public static Map<String, String> getParamLess(Object bean) throws Exception
    {
        Map<String, String> paramsMap = convertBeanLess(bean);
        StringBuilder sb = new StringBuilder();
        Iterator<String> iterator = paramsMap.keySet().iterator();
        while (iterator.hasNext())
        {
            sb.append(paramsMap.get(iterator.next()) + "|");
        }
        String source = sb.toString();
        if (!StringUtils.isEmpty(source))
        {
            source = source + MD5_KEY;
        }       
        String sourceSign = DigestUtils.md5Hex((source).getBytes("UTF-8"));        
        paramsMap.put("sign", sourceSign);       
        return paramsMap;
    }
	
	
	@SuppressWarnings("rawtypes")
	public static Map<String, String> convertBeanLess(Object bean) throws Exception
    {
		Class type = bean.getClass();
        Map<String, String> returnMap = new TreeMap<String, String>();
        BeanInfo beanInfo = Introspector.getBeanInfo(type);
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (int i = 0; i < propertyDescriptors.length; i++)
        {
            PropertyDescriptor descriptor = propertyDescriptors[i];
            String propertyName = descriptor.getName();
            if (!propertyName.equals("class"))
            {
                Method readMethod = descriptor.getReadMethod();
                Object result = readMethod.invoke(bean, new Object[0]);
                if (result != null)
                {
                    returnMap.put(propertyName, (String)result);
                }
            }
        }
        return returnMap;
    }
	
		
	public static String getResponse(String res) throws UnsupportedEncodingException{
        res = URLDecoder.decode(res, "UTF-8");
        //JSONObject json = JSONObject.parseObject(res);
        return res;
    }
	
	public static MultiValueMap<String, String> convertMap2MultiValueMap(Map<String, String> map){
		
		MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
		for(Map.Entry<String, String> entry :map.entrySet()){
			multiValueMap.add(entry.getKey(), entry.getValue());
		}
		return multiValueMap;		
	}

}
