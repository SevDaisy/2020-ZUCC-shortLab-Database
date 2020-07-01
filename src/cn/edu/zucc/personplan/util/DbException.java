package cn.edu.zucc.personplan.util;

public class DbException extends BaseException {
  /**
   *
   */
  private static final long serialVersionUID = -6411211100530539028L;

  public DbException(java.lang.Throwable ex) {
    super("数据库操作错误：" + ex.getMessage());
  }
}
