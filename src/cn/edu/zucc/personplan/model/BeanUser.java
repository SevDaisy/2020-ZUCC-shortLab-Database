package cn.edu.zucc.personplan.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BeanUser {
  public static BeanUser currentLoginUser = null;
  public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  private String userid;
  private String pwd;
  // private boolean On_Off;
  private Date reg_time;

  public BeanUser(String userid, String pwd, long time_Now) {
    this.userid = userid;
    this.pwd = pwd;
    this.reg_time = new Date(time_Now);
  }

  public BeanUser(String userid, String pwd, String dateStr) {
    this.userid = userid;
    this.pwd = pwd;
    try {
      this.reg_time = sdf.parse(dateStr);
    } catch (ParseException e) {
      throw new RuntimeException("登陆账号的创建时间无法解析");
    }
  }

  public static BeanUser getCurrentLoginUser() {
    return currentLoginUser;
  }

  public static void setCurrentLoginUser(BeanUser currentLoginUser) {
    BeanUser.currentLoginUser = currentLoginUser;
  }

  public String getUserid() {
    return userid;
  }

  public void setUserid(String userid) {
    this.userid = userid;
  }

  public String getPwd() {
    return pwd;
  }

  public void setPwd(String pwd) {
    this.pwd = pwd;
  }

  public Date getReg_time() {
    return reg_time;
  }

  public void setReg_time(Date reg_time) {
    this.reg_time = reg_time;
  }

}
