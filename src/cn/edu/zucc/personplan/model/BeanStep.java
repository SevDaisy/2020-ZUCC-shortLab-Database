package cn.edu.zucc.personplan.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class BeanStep {
  public static final String[] tblStepTitle = { "序号", "名称", "计划开始时间", "计划完成时间", "实际开始时间", "实际完成时间" };
  private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  private int step_order;
  private int step_id;
  private int plan_id;
  private String user_id;
  private String step_name;
  private Date plan_begin_time;
  private Date plan_end_time;
  private Date real_begin_time = null;
  private Date real_end_time = null;

  /**
   * 请自行根据javabean的设计修改本函数代码，col表示界面表格中的列序号，0开始
   */
  public String getCell(int col) {
    if (col == 0)
      return this.step_order + "";
    else if (col == 1)
      return this.step_name;
    else if (col == 2)
      return sdf.format(this.plan_begin_time);
    else if (col == 3)
      return sdf.format(this.plan_end_time);
    else if (col == 4)
      return this.real_begin_time == null ? "" : sdf.format(this.real_begin_time);
    else if (col == 5)
      return this.real_end_time == null ? "" : sdf.format(this.real_end_time);
    else
      return "";
  }

  public int getStep_order() {
    return step_order;
  }

  public void setStep_order(int step_order) {
    this.step_order = step_order;
  }

  public String getUser_id() {
    return user_id;
  }

  public void setUser_id(String user_id) {
    this.user_id = user_id;
  }

  public String getStep_name() {
    return step_name;
  }

  public void setStep_name(String step_name) {
    this.step_name = step_name;
  }

  public Date getPlan_begin_time() {
    return plan_begin_time;
  }

  public void setPlan_begin_time(Date plan_begin_time) {
    this.plan_begin_time = plan_begin_time;
  }

  public Date getPlan_end_time() {
    return plan_end_time;
  }

  public void setPlan_end_time(Date plan_end_time) {
    this.plan_end_time = plan_end_time;
  }

  public Date getReal_begin_time() {
    return real_begin_time;
  }

  public void setReal_begin_time(Date real_begin_time) {
    this.real_begin_time = real_begin_time;
  }

  public Date getReal_end_time() {
    return real_end_time;
  }

  public void setReal_end_time(Date real_end_time) {
    this.real_end_time = real_end_time;
  }

  public int getStep_id() {
    return step_id;
  }

  public void setStep_id(int step_id) {
    this.step_id = step_id;
  }

  public int getPlan_id() {
    return plan_id;
  }

  public void setPlan_id(int plan_id) {
    this.plan_id = plan_id;
  }
}
