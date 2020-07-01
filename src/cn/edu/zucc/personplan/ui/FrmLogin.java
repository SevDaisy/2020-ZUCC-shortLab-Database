package cn.edu.zucc.personplan.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import cn.edu.zucc.personplan.PersonPlanUtil;
import cn.edu.zucc.personplan.model.BeanUser;
import cn.edu.zucc.personplan.util.BaseException;

public class FrmLogin extends JDialog implements ActionListener {
  /**
   *
   */
  private static final long serialVersionUID = 4350830209634088267L;
  private JPanel toolBar = new JPanel();
  private JPanel workPane = new JPanel();
  private JButton btnLogin = new JButton("登陆");
  private JButton btnCancel = new JButton("退出");
  private JButton btnRegister = new JButton("注册");

  private JLabel labelUser = new JLabel("用户：");
  private JLabel labelPwd = new JLabel("密码：");
  private JTextField edtUserId = new JTextField(20);
  private JPasswordField edtPwd = new JPasswordField(20);

  public FrmLogin(Frame f, String s, boolean b) {
    super(f, s, b);
    toolBar.setLayout(new FlowLayout(FlowLayout.RIGHT));
    toolBar.add(this.btnRegister);
    toolBar.add(btnLogin);
    toolBar.add(btnCancel);
    this.getContentPane().add(toolBar, BorderLayout.SOUTH);
    workPane.add(labelUser);
    workPane.add(edtUserId);
    workPane.add(labelPwd);
    workPane.add(edtPwd);
    this.getContentPane().add(workPane, BorderLayout.CENTER);
    this.setSize(320, 140);
    // 屏幕居中显示
    double width = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
    double height = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
    this.setLocation((int) (width - this.getWidth()) / 2, (int) (height - this.getHeight()) / 2);

    this.validate();

    btnLogin.addActionListener(this);
    btnCancel.addActionListener(this);
    this.btnRegister.addActionListener(this);
    this.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        System.exit(0);
      }
    });
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == this.btnLogin) {
      String userid = this.edtUserId.getText();
      String pwd = new String(this.edtPwd.getPassword());
      try {
        BeanUser.currentLoginUser = PersonPlanUtil.userManager.login(userid, pwd);
      } catch (BaseException e1) {
        JOptionPane.showMessageDialog(null, e1.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        return;
      }
      this.setVisible(false);

    } else if (e.getSource() == this.btnCancel) {
      System.exit(0);
    } else if (e.getSource() == this.btnRegister) {
      FrmRegister dlg = new FrmRegister(this, "注册", true);
      dlg.setVisible(true);
    }
  }
}
