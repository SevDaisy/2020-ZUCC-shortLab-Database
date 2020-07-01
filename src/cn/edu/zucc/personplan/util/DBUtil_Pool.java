package cn.edu.zucc.personplan.util;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;

import com.mchange.v2.c3p0.ComboPooledDataSource;

// public class DBUtil_Pool {
// }
public class DBUtil_Pool {
  private static DBUtil_Pool dbUtil2;
  static {
    dbUtil2 = new DBUtil_Pool();
  }
  private static ComboPooledDataSource dataSource;

  public DBUtil_Pool() {
    try {
      dataSource = new ComboPooledDataSource();
      dataSource.setUser("root");
      dataSource.setPassword("1");
      dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/ShortLab?useUnicode=true&characterEncoding=utf8&useSSL=false");
      dataSource.setDriverClass("com.mysql.jdbc.Driver");
      dataSource.setInitialPoolSize(2);
      dataSource.setMinPoolSize(1);
      dataSource.setMaxPoolSize(10);
      dataSource.setMaxStatements(50);
      dataSource.setMaxIdleTime(60);
    } catch (PropertyVetoException e) {
      throw new RuntimeException(e);
    }
  }

  public final static DBUtil_Pool getInstance() {
    return dbUtil2;
  }

  public final static Connection getConnection() {
    try {
      return dataSource.getConnection();
    } catch (SQLException e) {
      throw new RuntimeException("无法从数据源获取连接 ", e);
    }
  }
}