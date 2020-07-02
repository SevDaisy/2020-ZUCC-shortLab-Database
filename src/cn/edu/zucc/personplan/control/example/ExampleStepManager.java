package cn.edu.zucc.personplan.control.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
import cn.edu.zucc.personplan.util.DBUtil_Pool;
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
      conn = DBUtil_Pool.getConnection();
      conn.setAutoCommit(false);
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
      // !!! 惊现第二次数据操作，需要事务控制！
      sql = "UPDATE tbl_plan SET step_count=? WHERE plan_id=?";
      pst = conn.prepareStatement(sql);
      pst.setInt(1, step_order);
      pst.setInt(2, plan.getPlan_id());
      if (pst.executeUpdate() == 1) {
        System.out.println("成功更新计划: " + plan.getPlan_name() + " 的步骤计数");
      } else {
        throw new RuntimeException("数据库 更新 plan 异常");
      }
      conn.commit();
      rs.close();
      pst.close();
    } catch (SQLException e) {
      System.out.println("出现SQL异常，开始回滚");
      try {
        conn.rollback();
        System.out.println("回滚成功");
      } catch (SQLException e1) {
        System.out.println("回滚失败！！！");
        throw new DbException(e1);
      }
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
      conn = DBUtil_Pool.getConnection();
      String sql = "SELECT step_order, step_name, plan_begin_time, plan_end_time,real_begin_time, real_end_time, step_id "
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
        s.setStep_id(rs.getInt(7));
        s.setUser_id(BeanUser.currentLoginUser.getUserid());
        s.setPlan_id(plan.getPlan_id());
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

    // 对 plan 的 step_count 的调整，
    // 我认为，step_count 应当与其步骤的最大的step_order保持一致
    // 而不是与 其对应的 step 的数量保持一致


    // 对 plan 的 finished_step_count 的调整
    // 我认为，如果被删除的步骤已经是finished，那么finished_step_count应该-1

    Connection conn = null;
    try {
      conn = DBUtil_Pool.getConnection();
      conn.setAutoCommit(false);
      String sql = "DELETE from tbl_step WHERE step_id=?";
      java.sql.PreparedStatement pst = conn.prepareStatement(sql);
      pst.setInt(1, step.getStep_id());
      if (pst.executeUpdate() == 1) {
        System.out.println("成功删除 step _id: " + step.getStep_id());
      } else {
        throw new RuntimeException("数据库 删除 step by step_id 异常");
      }
      // 对 plan 的 step_count 的调整 —— 参数预备
      int step_order_max;
      sql = "SELECT step_order FROM tbl_step WHERE plan_id=? ORDER BY step_order DESC LIMIT 0,1";
      pst = conn.prepareStatement(sql);
      pst.setInt(1, step.getPlan_id());
      ResultSet rs = pst.executeQuery();
      if (rs.next()) {
        step_order_max = rs.getInt(1);
      } else {
        step_order_max = 0;
      }
      // 对 plan 的 finished_step_count 的调整 —— 参数预备
      int finished_step_count = 0;
      int start_step_count = 0;
      sql = "SELECT start_step_count,finished_step_count FROM tbl_plan WHERE plan_id=?";
      pst = conn.prepareStatement(sql);
      pst.setInt(1, step.getPlan_id());
      if (rs.next()) {
        start_step_count = rs.getInt(1);
        finished_step_count = rs.getInt(2);
        if (step.getReal_end_time() != null) {
          finished_step_count -= 1;
        }
        if (step.getReal_begin_time() != null) {
          start_step_count -= 1;
        }
      }
      rs.close();

      sql = "UPDATE tbl_plan SET step_count=?,start_step_count=?,finished_step_count=? WHERE plan_id=?";
      pst = conn.prepareStatement(sql);
      pst.setInt(1, step_order_max);
      pst.setInt(2, start_step_count);
      pst.setInt(3, finished_step_count);
      pst.setInt(4, step.getPlan_id());
      if (pst.executeUpdate() == 1) {
        System.out.println("成功更新计划 _id: " + step.getPlan_id() + " 的步骤计数");
      } else {
        throw new RuntimeException("数据库 更新 plan 异常");
      }
      conn.commit();
      pst.close();
    } catch (SQLException e) {
      System.out.println("出现SQL异常，开始回滚");
      try {
        conn.rollback();
        System.out.println("回滚成功");
      } catch (SQLException e1) {
        System.out.println("回滚失败！！！");
        throw new DbException(e1);
      }
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
    // 关于更新计划表中的 已开始步骤的数量 是每次都重新数步骤，还是只需要当前值+1？
    // 我选择当前值+1 因为这样和之前的 plan.step_count::step.step_order.MAX 的绑定相匹配
    // 同样需要事务控制
    long timeNow = System.currentTimeMillis();
    Connection conn = null;
    try {
      conn = DBUtil_Pool.getConnection();
      conn.setAutoCommit(false);
      String sql = "UPDATE tbl_step SET real_begin_time=? WHERE step_id=?";
      java.sql.PreparedStatement pst = conn.prepareStatement(sql);
      pst.setTimestamp(1, new java.sql.Timestamp(timeNow));
      pst.setInt(2, step.getStep_id());
      if (pst.executeUpdate() == 1) {
        System.out.println("成功设置 步骤 _id: " + step.getStep_id() + " 的实际开始时间");
      } else {
        throw new RuntimeException("数据库 更新 step 异常");
      }
      int start_step_count;
      sql = "SELECT start_step_count FROM tbl_plan WHERE plan_id=?";
      pst = conn.prepareStatement(sql);
      pst.setInt(1, step.getPlan_id());
      ResultSet rs = pst.executeQuery();
      rs.next();
      start_step_count = rs.getInt(1) + 1;
      sql = "UPDATE tbl_plan SET start_step_count=? WHERE plan_id=?";
      pst = conn.prepareStatement(sql);
      pst.setInt(1, start_step_count);
      pst.setInt(2, step.getPlan_id());
      if (pst.executeUpdate() == 1) {
        System.out.println("成功更新当前计划 _id: " + step.getPlan_id() + " 的 已开始步骤数量");
      } else {
        throw new RuntimeException("数据库 更新 plan 异常");
      }
      conn.commit();
      pst.close();
    } catch (SQLException e) {
      System.out.println("出现SQL异常，开始回滚");
      try {
        conn.rollback();
        System.out.println("回滚成功");
      } catch (SQLException e1) {
        System.out.println("回滚失败！！！");
        throw new DbException(e1);
      }
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
  public void finishStep(BeanStep step) throws BaseException {
    // 和 上一个 startStep 几乎一模一样
    // 关于更新计划表中的 已结束步骤的数量 是每次都重新数步骤，还是只需要当前值+1？
    // 我选择当前值+1 因为这样和之前的 plan.step_count::step.step_order.MAX 的绑定相匹配
    // 同样需要事务控制
    long timeNow = System.currentTimeMillis();
    Connection conn = null;
    try {
      conn = DBUtil_Pool.getConnection();
      conn.setAutoCommit(false);
      String sql = "UPDATE tbl_step SET real_end_time=? WHERE step_id=?";
      java.sql.PreparedStatement pst = conn.prepareStatement(sql);
      pst.setTimestamp(1, new java.sql.Timestamp(timeNow));
      pst.setInt(2, step.getStep_id());
      if (pst.executeUpdate() == 1) {
        System.out.println("成功设置 步骤 _id: " + step.getStep_id() + " 的实际结束时间");
      } else {
        throw new RuntimeException("数据库 更新 step 异常");
      }
      int finished_step_count;
      sql = "SELECT finished_step_count FROM tbl_plan WHERE plan_id=?";
      pst = conn.prepareStatement(sql);
      pst.setInt(1, step.getPlan_id());
      ResultSet rs = pst.executeQuery();
      rs.next();
      finished_step_count = rs.getInt(1) + 1;
      sql = "UPDATE tbl_plan SET finished_step_count=? WHERE plan_id=?";
      pst = conn.prepareStatement(sql);
      pst.setInt(1, finished_step_count);
      pst.setInt(2, step.getPlan_id());
      if (pst.executeUpdate() == 1) {
        System.out.println("成功更新当前计划 _id: " + step.getPlan_id() + " 的 已结束步骤数量");
      } else {
        throw new RuntimeException("数据库 更新 plan 异常");
      }
      conn.commit();
      pst.close();
    } catch (SQLException e) {
      System.out.println("出现SQL异常，开始回滚");
      try {
        conn.rollback();
        System.out.println("回滚成功");
      } catch (SQLException e1) {
        System.out.println("回滚失败！！！");
        throw new DbException(e1);
      }
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
  public void moveUp(BeanStep step) throws BaseException {
    /*
     * 关键在于避免出现 plan_id 和 step_order 同时相等的 step 元组
     */
    // 先 得知自己的 step_order
    // 显然，不可小于 2
    int old_order = step.getStep_order();
    if (old_order < 2) {
      throw new BusinessException("当前步骤是第一步，不能更提前了");
    }
    // 目标 序号是 step_order - 1
    int target_order = old_order - 1;
    // 不管目标序号上有没有步骤，直接将其 update 至 序号=0
    // sql: UPDATE tbl_step SET step_order=0
    // WHERE plan_id=step.getPlan_id() AND step_order=$target_order

    // 设置当前步骤的序号为目标序号
    // sql: UPDATE tbl_step SET step_order=$target_order
    // WHERE plan_id=step.getPlan_id() AND step_order=$old_order

    // 重新将0号步骤设置回 old_order
    // sql: UPDATE tbl_step SET step_order=$old_order
    // WHERE plan_id=step.getPlan_id() AND step_order=0

    // 这样，需要 3次格式相似的Update
    // 感觉有点蠢，害，先能用再说
    Connection conn = null;
    try {
      conn = DBUtil_Pool.getConnection();
      conn.setAutoCommit(false);
      String sql;
      java.sql.PreparedStatement pst;

      sql = "UPDATE tbl_step SET step_order=? WHERE plan_id=? AND step_order=?";
      pst = conn.prepareStatement(sql);
      pst.setInt(1, 0);
      pst.setInt(2, step.getPlan_id());
      pst.setInt(3, target_order);
      pst.executeUpdate();

      sql = "UPDATE tbl_step SET step_order=? WHERE plan_id=? AND step_order=?";
      pst = conn.prepareStatement(sql);
      pst.setInt(1, target_order);
      pst.setInt(2, step.getPlan_id());
      pst.setInt(3, old_order);
      pst.executeUpdate();

      sql = "UPDATE tbl_step SET step_order=? WHERE plan_id=? AND step_order=?";
      pst = conn.prepareStatement(sql);
      pst.setInt(1, old_order);
      pst.setInt(2, step.getPlan_id());
      pst.setInt(3, 0);
      pst.executeUpdate();

      conn.commit();
      pst.close();
    } catch (SQLException e) {
      System.out.println("出现SQL异常，开始回滚");
      try {
        conn.rollback();
        System.out.println("回滚成功");
      } catch (SQLException e1) {
        System.out.println("回滚失败！！！");
        throw new DbException(e1);
      }
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
  public void moveDown(BeanStep step) throws BaseException {
    // 几乎和 moveUp 一模一样
    /*
     * 特殊的是，如果我允许步骤一直往后推，不做边界限制，那么，举个例子
     */
    // 在我的代码中，如果有三个步骤，第三个步骤被移至序号4
    // 然后再创建一个步骤，他会自动成为序号五
    // 同时，计划的步骤总计数更新为5个
    // 但是其实只有四个步骤，即使每个步骤都结束了，也将仍有 finished_step_count < step_count
    // 以至于无法删除这个计划
    // 这是我所不希望看到的。
    /*
     * 因此，我将对后推的步骤也进行边界控制，需要增加一次sql查询(select)
     */
    // sql: SELECT step_count From tbl_plan WHERE plan_id=$(step.getPlan_id)
    int old_order = step.getStep_order();
    // 目标 序号是 step_order + 1
    int target_order = old_order + 1;
    Connection conn = null;
    try {
      conn = DBUtil_Pool.getConnection();
      conn.setAutoCommit(false);
      String sql;
      java.sql.PreparedStatement pst;

      int order_Max;
      sql = "SELECT step_count From tbl_plan WHERE plan_id=?";
      pst = conn.prepareStatement(sql);
      pst.setInt(1, step.getPlan_id());
      ResultSet rs = pst.executeQuery();
      if (rs.next()) {
        order_Max = rs.getInt(1);
      } else {
        throw new RuntimeException("数据库 查询 plan.step_order By plan_id 异常");
      }
      rs.close();
      if (target_order > order_Max) {
        throw new BusinessException("当前步骤是最后一步，不能再后移了");
      }

      sql = "UPDATE tbl_step SET step_order=? WHERE plan_id=? AND step_order=?";
      pst = conn.prepareStatement(sql);
      pst.setInt(1, 0);
      pst.setInt(2, step.getPlan_id());
      pst.setInt(3, target_order);
      pst.executeUpdate();

      sql = "UPDATE tbl_step SET step_order=? WHERE plan_id=? AND step_order=?";
      pst = conn.prepareStatement(sql);
      pst.setInt(1, target_order);
      pst.setInt(2, step.getPlan_id());
      pst.setInt(3, old_order);
      pst.executeUpdate();

      sql = "UPDATE tbl_step SET step_order=? WHERE plan_id=? AND step_order=?";
      pst = conn.prepareStatement(sql);
      pst.setInt(1, old_order);
      pst.setInt(2, step.getPlan_id());
      pst.setInt(3, 0);
      pst.executeUpdate();

      conn.commit();
      pst.close();
    } catch (SQLException e) {
      System.out.println("出现SQL异常，开始回滚");
      try {
        conn.rollback();
        System.out.println("回滚成功");
      } catch (SQLException e1) {
        System.out.println("回滚失败！！！");
        throw new DbException(e1);
      }
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
