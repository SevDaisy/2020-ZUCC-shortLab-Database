package cn.edu.zucc.personplan.control.example;

import java.sql.Connection;
import java.sql.SQLException;

import cn.edu.zucc.personplan.itf.IUserManager;
import cn.edu.zucc.personplan.model.BeanUser;
import cn.edu.zucc.personplan.util.BaseException;
import cn.edu.zucc.personplan.util.BusinessException;
import cn.edu.zucc.personplan.util.DBUtil_Pool;
import cn.edu.zucc.personplan.util.DbException;

public class ExampleUserManager implements IUserManager {

  @Override
  public BeanUser reg(String userid, String pwd, String pwd2) throws BaseException {
    /*
     * 参数合法性检测
     */
    if (userid == null || "".equals(userid) || userid.length() > 50) {
      throw new BusinessException("登陆账号必须是 1~50 个字符");
    }
    if (pwd == null || "".equals(pwd) || pwd.length() > 255) {
      throw new BusinessException("密码 必须是 1~255 个字符");
    }
    if (!pwd2.equals(pwd)) {
      throw new BusinessException("两次密码输入必须一致");
    }
    // 预备 return 对象
    BeanUser result = null;
    /*
     * 数据库操作
     */
    // 先查询，有没有重复的用户名
    // 再insert插入数据至数据库
    Connection conn = null;
    try {
      conn = DBUtil_Pool.getConnection();
      String sql = "select user_id from tbl_user where user_id=?";
      java.sql.PreparedStatement pst = conn.prepareStatement(sql);
      pst.setString(1, userid);
      java.sql.ResultSet rs = pst.executeQuery();
      if (rs.next())
        throw new BusinessException("存在重名的登陆账号（user_id不可重复）");
      sql = "insert into tbl_user(user_id,user_pwd,register_time) values(?,?,?)";
      pst = conn.prepareStatement(sql);
      pst.setString(1, userid);
      pst.setString(2, pwd);
      long time_Now = System.currentTimeMillis();
      pst.setTimestamp(3, new java.sql.Timestamp(time_Now));
      if (pst.executeUpdate() == 1) {
        System.out.println("注册用户成功");
        result = new BeanUser(userid, pwd, time_Now);
      } else {
        throw new RuntimeException("数据库插入新用户失败");
      }
      pst.close();
      rs.close();
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
  public BeanUser login(String userid, String pwd) throws BaseException {
    /*
     * 参数合法性检测
     */
    if (userid == null || "".equals(userid) || userid.length() > 50) {
      throw new BusinessException("登陆账号必须是 1~50 个字符");
    }
    if (pwd == null || "".equals(pwd) || pwd.length() > 50) {
      throw new BusinessException("密码 必须是 1~255 个字符");
    }
    // 预备 return 对象
    BeanUser result = null;
    /*
     * 数据库操作
     */
    // 用 user_id 从数据库查询到密码
    // 比较密码，密码相同即可登陆成功
    // 设置 BeanUser 的 currentLoginUser 为这个新建的登陆对象（result)
    // return 一个当前的登陆对象
    Connection conn = null;
    try {
      conn = DBUtil_Pool.getConnection();
      String sql = "SELECT user_pwd,register_time from tbl_user where user_id=?";
      java.sql.PreparedStatement pst = conn.prepareStatement(sql);
      pst.setString(1, userid);
      java.sql.ResultSet rs = pst.executeQuery();
      if (rs.next()) {
        if (rs.getString(1).equals(pwd)) {
          result = new BeanUser(userid, pwd, rs.getString(2));
          BeanUser.currentLoginUser = result;
        } else {
          throw new BusinessException("账号密码不匹配！");
        }
      } else {
        throw new BusinessException("查无此账号");
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
  public void changePwd(BeanUser user, String oldPwd, String newPwd, String newPwd2) throws BaseException {
    /*
     * 参数合法性检测
     */
    if (user.equals(null)) {
      throw new BusinessException("期望更改的目标账户不可为空");
    }
    if (user.getPwd().equals(oldPwd)) {
      throw new BusinessException("原密码验证错误，请检查 “原先的密码”");
    }
    if (newPwd == null || "".equals(newPwd) || newPwd.length() > 255) {
      throw new BusinessException("密码 必须是 1~255 个字符");
    }
    if (!newPwd2.equals(newPwd)) {
      throw new BusinessException("两次密码输入必须一致");
    }
    /*
     * 数据库操作
     */
    // 无需考虑数据库中不存在这个账户的情况（传入参数user保证了这一点）
    // 简单的Update处理即可
    Connection conn = null;
    try {
      conn = DBUtil_Pool.getConnection();
      String sql = "UPDATE tbl_user SET user_pwd=? where user_id=?";
      java.sql.PreparedStatement pst = conn.prepareStatement(sql);
      pst.setString(1, newPwd);
      pst.setString(2, user.getUserid());
      if (pst.executeUpdate() == 1) {
        System.out.println("更改密码成功");
      } else {
        throw new RuntimeException("数据库更新密码失败");
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
  }
}
