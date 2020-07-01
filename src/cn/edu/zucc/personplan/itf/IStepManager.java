package cn.edu.zucc.personplan.itf;

import java.util.List;

import cn.edu.zucc.personplan.model.BeanPlan;
import cn.edu.zucc.personplan.model.BeanStep;
import cn.edu.zucc.personplan.util.BaseException;

public interface IStepManager {
	public List<BeanStep> loadSteps(BeanPlan plan)throws BaseException;
	
	/**
	 * 添加步骤
	 * 新填的步骤序号为当前计划最大步骤序号+1
	 * 注意：需完成字符串和时间类型的转换，添加后需调整计划表中相应的数量值
	 * @param plan
	 * @param name
	 * @param planstartdate
	 * @param planfinishdate
	 * @throws BaseException
	 */
	public void add(BeanPlan plan, String name, String planstartdate, String planfinishdate)throws BaseException;

	
	
	/**
	 * 删除步骤，
	 * 注意删除后需调整计划表中对应的步骤数量
	 * @param step
	 * @throws BaseException
	 */
	public void deleteStep(BeanStep step)throws BaseException;

	/**
	 * 设置当前步骤的实际开始时间，及对应的计划表中已开始步骤数量
	 * 
	 * @param step
	 * @throws BaseException
	 */
	public void startStep(BeanStep step)throws BaseException;
	/**
	 * 设置当前步骤的实际完成时间，及对应的计划表中已完成步骤数量
	 * @param step
	 * @throws BaseException
	 */
	public void finishStep(BeanStep step)throws BaseException;
	/**
	 * 调整当前步骤的顺序号
	 * 注意：数据库表中，plan_id,step_order上建立了唯一索引，调整当前步骤的序号值和上一步骤的序号值时不能出现序号值一样的情况
	 * @param step
	 * @throws BaseException
	 */
	public void moveUp(BeanStep step)throws BaseException;
	public void moveDown(BeanStep step)throws BaseException;
	

}
