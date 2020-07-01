package cn.edu.zucc.personplan.model;

public class BeanPlan {
  public static final String[] tableTitles = { "序号", "名称", "步骤数", "已完成数" };
  private int plan_order;
  private String plan_name;
  private int step_count;
  private int finished_step_count;
  // private String user_id;
  // private int start_step_count;

  /**
   * 请自行根据javabean的设计修改本函数代码，col表示界面表格中的列序号，0开始
   */
  public String getCell(int col) {
    if (col == 0)
      return (this.plan_order + "");
    else if (col == 1)
      return this.plan_name;
    else if (col == 2)
      return this.step_count + "";
    else if (col == 3)
      return this.finished_step_count + "";
    else
      return "";
  }

  public int getPlan_order() {
    return plan_order;
  }

  public void setPlan_order(int plan_order) {
    this.plan_order = plan_order;
  }

  public String getPlan_name() {
    return plan_name;
  }

  public void setPlan_name(String plan_name) {
    this.plan_name = plan_name;
  }

  public int getStep_count() {
    return step_count;
  }

  public void setStep_count(int step_count) {
    this.step_count = step_count;
  }

  public int getFinished_step_count() {
    return finished_step_count;
  }

  public void setFinished_step_count(int finished_step_count) {
    this.finished_step_count = finished_step_count;
  }

  // public String getUser_id() {
  // return user_id;
  // }

  // public void setUser_id(String user_id) {
  // this.user_id = user_id;
  // }

}
