package cn.edu.zucc.personplan.model;

public class BeanStep {
	public static final String[] tblStepTitle={"序号","名称","计划开始时间","计划完成时间","实际开始时间","实际完成时间"};
	/**
	 * 请自行根据javabean的设计修改本函数代码，col表示界面表格中的列序号，0开始
	 */
	public String getCell(int col){
		if(col==0) return "1";
		else if(col==1) return "示例步骤";
		else if(col==2) return "2015-01-01";
		else if(col==3) return "2015-08-01";
		else if(col==4) return "";
		else if(col==5) return "";
		
		else return "";
	}
}
