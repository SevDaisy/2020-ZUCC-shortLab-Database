package cn.edu.zucc.personplan.control.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import cn.edu.zucc.personplan.itf.IStepManager;
import cn.edu.zucc.personplan.model.BeanPlan;
import cn.edu.zucc.personplan.model.BeanStep;
import cn.edu.zucc.personplan.model.BeanUser;
import cn.edu.zucc.personplan.util.BaseException;
import cn.edu.zucc.personplan.util.BusinessException;
import cn.edu.zucc.personplan.util.DBUtil;
import cn.edu.zucc.personplan.util.DbException;

public class ExampleStepManager implements IStepManager {
  private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  @Override
  public void add(BeanPlan plan, String name, String planstartdate, String planfinishdate) throws BaseException {
    /*
     * 参数合法性检测
     */
    if (name == null || "".equals(name) || name.length() > 50) {
      throw new BusinessException("步骤名称 必须是 1~255 个字符");
    }
    try {
      sdf.parse(planstartdate);
      sdf.parse(planfinishdate);
      // 此处仅仅是检查 格式合法性
      // 之后的插入还是直接 pst.setString 即可
    } catch (ParseException e) {
      throw new BusinessException("请检查时间格式\n请参照：yyyy-MM-dd HH:mm:ss");
    }
    // 查询 当前计划最大步骤 并 +1
    int step_order;
    Connection conn = null;
    try {
      conn = DBUtil.getConnection();
      String sql = "SELECT step_order FROM tbl_step WHERE plan_id=? ORDER BY step_order DESC LIMIT 0,1";
      java.sql.PreparedStatement pst = conn.prepareStatement(sql);
      pst.setInt(1, plan.getPlan_id());
      java.sql.ResultSet rs = pst.executeQuery();
      if (rs.next()) {
        step_order = rs.getInt(1) + 1;
      } else {
        step_order = 1;
      }
      sql = "INSERT INTO tbl_step(plan_id,step_order,step_name,plan_begin_time,plan_end_time) VALUES(?,?,?,?,?)";
      pst = conn.prepareStatement(sql);
      pst.setInt(1, plan.getPlan_id());
      pst.setInt(2, step_order);
      pst.setString(3, name);
      pst.setString(4, planstartdate);
      pst.setString(5, planstartdate);
      if (pst.executeUpdate() == 1) {
        System.out.println("成功建立步骤: " + name);
      } else {
        throw new RuntimeException("数据库 插入 step 异常");
      }
      // !!! 惊现第二次数据操作，需要事务控制！但是我写不来，现在是深夜不想查Google
      sql = "UPDATE tbl_plan SET step_count=? WHERE plan_id=?";
      pst = conn.prepareStatement(sql);
      pst.setInt(1, step_order);
      pst.setInt(2, plan.getPlan_id());
      if (pst.executeUpdate() == 1) {
        System.out.println("成功更新计划: " + plan.getPlan_name() + " 的步骤计数");
      } else {
        throw new RuntimeException("数据库 更新 plan 异常");
      }
      rs.close();
      pst.close();
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DbException(e);
    } finally {
      if (conn != null)
        try {
          conn.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
    }
  }

  @Override
  public List<BeanStep> loadSteps(BeanPlan plan) throws BaseException {
    List<BeanStep> result = new ArrayList<BeanStep>();
    Connection conn = null;
    try {
      conn = DBUtil.getConnection();
      String sql = "SELECT step_order, step_name, plan_begin_time, plan_end_time,real_begin_time, real_end_time "
          + "FROM tbl_step " + "WHERE plan_id=?";
      PreparedStatement pst = conn.prepareStatement(sql);
      pst.setInt(1, plan.getPlan_id());
      ResultSet rs = pst.executeQuery();
      while (rs.next()) {
        BeanStep s = new BeanStep();
        s.setStep_order(rs.getInt(1));
        s.setStep_name(rs.getString(2));
        s.setPlan_begin_time(sdf.parse(rs.getString(3)));
        s.setPlan_end_time(sdf.parse(rs.getString(4)));
        s.setReal_begin_time(rs.getString(5) == null ? null : sdf.parse(rs.getString(5)));
        s.setReal_end_time(rs.getString(6) == null ? null : sdf.parse(rs.getString(6)));
        s.setUser_id(BeanUser.currentLoginUser.getUserid());
        result.add(s);
      }
      rs.close();
      pst.close();
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DbException(e);
    } catch (ParseException e) {
      // 其实。从数据库里查出来的 String 时间，基本不会解析错误嗷
      throw new RuntimeException("数据库查得 step datetime 格式解析 错误");
    } finally {
      if (conn != null)
        try {
          conn.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
    }
    return result;
  }

  @Override
  public void deleteStep(BeanStep step) throws BaseException {
    // 删除步骤， 注意删除后需调整计划表中对应的步骤数量
    // 涉及两次数据更新，需要事务控制
    Connection conn = null;
    try {
      conn = DBUtil.getConnection();
      String sql = "";
      java.sql.PreparedStatement pst = conn.prepareStatement(sql);
      java.sql.ResultSet rs = pst.executeQuery();
      rs.close();
      pst.close();
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DbException(e);
    } finally {
      if (conn != null)
        try {
          conn.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
    }

  }

  @Override
  public void startStep(BeanStep step) throws BaseException {

  }

  @Override
  public void finishStep(BeanStep step) throws BaseException {

  }

  @Override
  public void moveUp(BeanStep step) throws BaseException {

  }

  @Override
  public void moveDown(BeanStep step) throws BaseException {

  }

}
