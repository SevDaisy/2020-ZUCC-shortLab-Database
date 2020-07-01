package cn.edu.zucc.personplan.itf;

import java.util.List;

import cn.edu.zucc.personplan.model.BeanPlan;
import cn.edu.zucc.personplan.util.BaseException;

public interface IPlanManager {
	/**
	 * 添加计划
	 * 要求新增的计划的排序号为当前用户现有最大排序号+1
	 * 注意：当前登陆用户可通过 BeanUser.currentLoginUser获取
	 * @param name  计划名称
	 * @throws BaseException
	 */
	public BeanPlan addPlan(String name) throws BaseException;
	/**
	 * 提取当前用户所有计划
	 * @return
	 * @throws BaseException
	 */
	public List<BeanPlan> loadAll()throws BaseException;
	/**
	 * 删除计划，如果计划下存在步骤，则不允许删除
	 * @param plan
	 * @throws BaseException
	 */
	public void deletePlan(BeanPlan plan)throws BaseException;

}
