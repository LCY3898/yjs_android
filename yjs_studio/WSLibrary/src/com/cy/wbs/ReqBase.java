package com.cy.wbs;

public class ReqBase {
  public EchoContent echo=new EchoContent();
  public String sess; //session字段
  public String verbose = "1"; //详细内容，用于调试
  public String io = "i";//input，表示发送请求
  //为null时 gson序列化时默认不传入字段
  public String pid;//用户ID
  
  public static class EchoContent {
	  public String timestamp; //获取请求时间
  }
}
