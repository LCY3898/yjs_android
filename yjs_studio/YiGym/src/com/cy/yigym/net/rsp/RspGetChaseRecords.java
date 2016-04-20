package com.cy.yigym.net.rsp;

import com.cy.wbs.RspBase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ejianshen on 15/7/29.
 */
public class RspGetChaseRecords extends RspBase implements Serializable {

  public Data data=new Data();

  public static class Data{
  // public String msg="";
   public List<CrsSList> crs_sList=new ArrayList<CrsSList>();
   public List<CrsUList> crs_uList=new ArrayList<CrsUList>();
  }
  public static class CrsUList implements Serializable{

   public String _id="";
   public String anotherName="";
   public int apart_distance=0;
   public String profile_fid="";
  }
 public static class CrsSList implements Serializable{

  public String _id="";
  public String anotherName="";
  public int apart_distance=0;
  public String profile_fid="";
 }

}
/**
 *  data: {
 crs_sList: [ ],
 crs_uList: [
 {
 _id: "o14376201563265879154",
 anotherName: "18065855693",
 apart_distance: "8888",
 profile_fid: null
 },
 {
 _id: "o14376220138509809970",
 anotherName: "18065855693",
 apart_distance: "8888",
 profile_fid: null
 }
 ]
 },
*/