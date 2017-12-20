package com.allinpal.twodfireservice.reportscanService.exception;

public class SystemException extends RuntimeException
{
  private static final long serialVersionUID = 5809164630995639478L;
  private String[] args;
  protected String errorCode;

  public SystemException()
  {
  }

  public SystemException(String message, Throwable cause, String[] args)
  {
    super(message, cause);
    this.args = args;
  }

  public SystemException(String message, String[] args)
  {
    super(message);
    this.args = args;
  }

  public SystemException(String errorCode, String message)
  {
    super(message);
    this.errorCode = errorCode;
  }

  public SystemException(String errorCode, String message, Throwable cause)
  {
    super(message, cause);
    this.errorCode = errorCode;
  }

  public SystemException(Throwable cause)
  {
    super(cause);
  }

  public String[] getArgs()
  {
    return this.args;
  }
}