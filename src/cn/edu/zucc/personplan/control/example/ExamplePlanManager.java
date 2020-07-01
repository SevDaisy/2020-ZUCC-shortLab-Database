package cn.edu.zucc.personplan.control.example;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import cn.edu.zucc.personplan.itf.IPlanManager;
import cn.edu.zucc.personplan.model.BeanPlan;
import cn.edu.zucc.personplan.model.BeanUser;
import cn.edu.zucc.personplan.util.BaseException;
import cn.edu.zucc.personplan.util.BusinessException;
import cn.edu.zucc.personplan.util.DBUtil_Pool;
import cn.edu.zucc.personplan.util.DbException;

public class ExamplePlanManager implements IPlanManager {

  @Override
  public BeanPlan addPlan(String name) throws BaseException {
    /*
     * 参数合法性检测
     */
    if (name == null || "".equals(name) || name.length() > 255) {
      throw new BusinessException("计划的名字 必须是 1~255 个字符");
    }
    BeanPlan result = null;
    /*
     * 数据库操作
     */
    // plan_id 在数据库中是自动自增的，干脆就不插入了，不管～
    // user_id 用当前登陆的用户 BeanUser.getCurrentLoginUser().getUserid()
    String user_id = BeanUser.currentLoginUser.getUserid();
    // plan_order 要求新增的计划的排序号为当前用户现有最大排序号+1 先sql查得最大排序号 再+1
    int plan_order;
    // plan_name 即 name
    // creat_time 即 pst.setTimestamp($index, new java.sql.Timestamp(time_now));
    long time_now = System.currentTimeMillis();
    // step，全部置为0
    // insert 一条Plan数据
    Connection conn = null;
    try {
      conn = DBUtil_Pool.getConnection();
      // plan_order 要求新增的计划的排序号为当前用户现有最大排序号+1 先sql查得最大排序号 再+1
      String sql = "SELECT plan_order FROM tbl_plan WHERE user_id=? ORDER BY plan_order DESC LIMIT 0,1";
      java.sql.PreparedStatement pst = conn.prepareStatement(sql);
      pst.setString(1, user_id);
      java.sql.ResultSet rs = pst.executeQuery();
      if (rs.next()) {
        plan_order = rs.getInt(1) + 1;
      } else {
        plan_order = 1;
      }
      rs.close();
      sql = "INSERT INTO tbl_plan"
          + "(user_id,plan_order,plan_name,create_time,start_step_count,step_count,finished_step_count)"
          + " VALUES(?,?,?,?,0,0,0)";
      pst = conn.prepareStatement(sql);
      pst.setString(1, user_id);
      pst.setInt(2, plan_order);
      pst.setString(3, name);
      pst.setTimestamp(4, new java.sql.Timestamp(time_now));
      if (pst.executeUpdate() == 1) {
        System.out.println("成功添加一个计划 step是（0，0，0）");
        result = new BeanPlan();
        result.setPlan_name(name);
        result.setPlan_order(plan_order);
        result.setStep_count(0);
        result.setFinished_step_count(0);
        // result.setUser_id(user_id);
      } else {
        throw new RuntimeException("数据库插入一条新计划失败");
      }
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
    return result;
  }

  @Override
  public List<BeanPlan> loadAll() throws BaseException {
    List<BeanPlan> result = new ArrayList<BeanPlan>();
    Connection conn = null;
    try {
      conn = DBUtil_Pool.getConnection();
      String sql = "SELECT plan_name,plan_order,step_count,finished_step_count,plan_id from tbl_plan where user_id=?";
      java.sql.PreparedStatement pst = conn.prepareStatement(sql);
      pst.setString(1, BeanUser.currentLoginUser.getUserid());
      java.sql.ResultSet rs = pst.executeQuery();
      while (rs.next()) {
        BeanPlan p = new BeanPlan();
        p.setPlan_name(rs.getString(1));
        p.setPlan_order(rs.getInt(2));
        p.setStep_count(rs.getInt(3));
        p.setFinished_step_count(rs.getInt(4));
        p.setPlan_id(rs.getInt(5));
        // p.setUser_id(BeanUser.currentLoginUser.getUserid());
        result.add(p);
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
    return result;
  }

  @Override
  public void deletePlan(BeanPlan plan) throws BaseException {
    /*
     * 选择是从UI界面选择，无需考虑 plan 不在数据库中的情形（信任 UI界面被及时地更新）
     */
    // 有未完成的步骤的不应该被删除（有一说一，真狠嗷，不做完不允许取消，哈哈哈哈哈）
    // step部分的代码我还没写，所以这边的step相关数据，还是先从 数据库 查询
    // 直接数据库 delete
    Connection conn = null;
    try {
      conn = DBUtil_Pool.getConnection();
      String sql = "SELECT finished_step_count,step_count FROM tbl_plan WHERE user_id=? AND plan_order=?";
      java.sql.PreparedStatement pst = conn.prepareStatement(sql);
      pst.setString(1, BeanUser.currentLoginUser.getUserid());
      pst.setInt(2, plan.getPlan_order());
      ResultSet rs = pst.executeQuery();
      rs.next();// 不考虑 plan 不在数据库中的情况
      if (rs.getInt(1) < rs.getInt(2)) {
        throw new BusinessException("尚有未完成的步骤，不可删除");
      }
      sql = "DELETE FROM tbl_plan WHERE user_id=? AND plan_order=?";
      pst = conn.prepareStatement(sql);
      pst.setString(1, BeanUser.currentLoginUser.getUserid());
      pst.setInt(2, plan.getPlan_order());
      pst.executeUpdate();
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
}
