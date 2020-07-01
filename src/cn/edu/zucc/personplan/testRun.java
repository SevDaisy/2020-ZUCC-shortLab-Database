package cn.edu.zucc.personplan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import cn.edu.zucc.personplan.util.DBUtil;

public class testRun {
  public void showTables() {
    Connection conn = null;
    try {
      conn = DBUtil.getConnection();
      String sql = "SELECT create_time from tbl_plan";
      PreparedStatement pst = conn.prepareStatement(sql);
      ResultSet rs = pst.executeQuery();
      rs.next();
      System.out.println(rs.getString(1));
    } catch (SQLException throwables) {
      throwables.printStackTrace();
    } finally {
      if (conn != null) {
        try {
          conn.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }
  }

  public static void main(String[] args) throws SQLException {
    // testRun k = new testRun();
    // k.showTables();
    int order = 100;
    System.out.println(order + "");
    System.out.println(order);
  }
}