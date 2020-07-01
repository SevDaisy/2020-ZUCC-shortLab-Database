package cn.edu.zucc.personplan;

import cn.edu.zucc.personplan.comtrol.example.ExamplePlanManager;
import cn.edu.zucc.personplan.comtrol.example.ExampleStepManager;
import cn.edu.zucc.personplan.comtrol.example.ExampleUserManager;
import cn.edu.zucc.personplan.itf.IPlanManager;
import cn.edu.zucc.personplan.itf.IStepManager;
import cn.edu.zucc.personplan.itf.IUserManager;

public class PersonPlanUtil {
  public static IPlanManager planManager = new ExamplePlanManager();// ��Ҫ����������Ƶ�ʵ����
  public static IStepManager stepManager = new ExampleStepManager();// ��Ҫ����������Ƶ�ʵ����
  public static IUserManager userManager = new ExampleUserManager();// ��Ҫ����������Ƶ�ʵ����

}
