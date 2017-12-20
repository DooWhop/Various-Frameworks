package com.allinpal.twodfireservice.accessVerify.domain;

//*****************************************************************************
//
//File Name       :  BaseModule.java
//Date Created    :  2015��7��7��
//Description     :  TODO To fill in a brief description of the purpose of
//                  this class.
//
//Allinpay Pte Ltd.  Copyright (c) 2015.  All Rights Reserved.
//
//*****************************************************************************

import java.lang.reflect.Method;

/**
* TODO To describe the functionality of this method
* 
* @author hwpc
*/
public class InterfaceModule
{
  private String terminalno = "sdfds";
  private String accesscode = "weixin";
  private String interfacecode;
  private String usertype = "P";
  private String channel = "W";
  private String signType = "MD5";
  private String timestamp;
  private String version = "V1.0";
  private String partner = "uap";
  private String inputCharset = "UTF-8";


  /**
   * @return Returns the terminalno.
   */
  public String getTerminalno()
  {
      return terminalno;
  }


  /**
   * @param terminalno The terminalno to set.
   */
  public void setTerminalno(String terminalno)
  {
      this.terminalno = terminalno;
  }


  /**
   * @return Returns the accesscode.
   */
  public String getAccesscode()
  {
      return accesscode;
  }


  /**
   * @param accesscode The accesscode to set.
   */
  public void setAccesscode(String accesscode)
  {
      this.accesscode = accesscode;
  }


  /**
   * @return Returns the interfacecode.
   */
  public String getInterfacecode()
  {
      return interfacecode;
  }


  /**
   * @param interfacecode The interfacecode to set.
   */
  public void setInterfacecode(String interfacecode)
  {
      this.interfacecode = interfacecode;
  }


  public String getUsertype()
  {
      return usertype;
  }


  public void setUsertype(String usertype)
  {
      this.usertype = usertype;
  }


  public String getChannel()
  {
      return channel;
  }


  public void setChannel(String channel)
  {
      this.channel = channel;
  }


  public String getSignType()
  {
      return signType;
  }


  public void setSignType(String signType)
  {
      this.signType = signType;
  }


  /**
   * @return Returns the timestamp.
   */
  public String getTimestamp()
  {
      return timestamp;
  }


  /**
   * @param timestamp The timestamp to set.
   */
  public void setTimestamp(String timestamp)
  {
      this.timestamp = timestamp;
  }


  /**
   * @return Returns the version.
   */
  public String getVersion()
  {
      return version;
  }


  /**
   * @param version The version to set.
   */
  public void setVersion(String version)
  {
      this.version = version;
  }


  public String getPartner()
  {
      return partner;
  }


  public void setPartner(String partner)
  {
      this.partner = partner;
  }


  /**
   * @return Returns the inputCharset.
   */
  public String getInputCharset()
  {
      return inputCharset;
  }


  /**
   * @param inputCharset The inputCharset to set.
   */
  public void setInputCharset(String inputCharset)
  {
      this.inputCharset = inputCharset;
  }
  
  public void trimAllString() throws Exception
  {
      Method[] methods = this.getClass().getMethods();
      if (methods != null && methods.length > 0)
      {
          for (int i = 0; i < methods.length; i++)
          {
              Method method = methods[i];
              if (method.getName().startsWith("get"))
              {
                  Object resultObj = method.invoke(this, new Object[] {});
                  if (resultObj != null && resultObj instanceof String)
                  {
                      String resultStr = (String) resultObj;
                      String setMethodName = "set"
                              + method.getName().substring(3,
                                      method.getName().length());
                      
                      try
                      {
                          Method setMethod = this.getClass()
                                  .getMethod(setMethodName,
                                          new Class[] { String.class });
                          setMethod.invoke(this, new Object[] { resultStr
                                  .trim() });
                      }
                      catch(NoSuchMethodException e)
                      {
                          // there is not setXXX method
                      }
                  }
              }
          }
      }
  }   

}
