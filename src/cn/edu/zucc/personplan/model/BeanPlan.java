package cn.edu.zucc.personplan.model;

public class BeanPlan {
	public static final String[] tableTitles={"序号","名称","步骤数","已完成数"};
	/**
	 * 请自行根据javabean的设计修改本函数代码，col表示界面表格中的列序号，0开始
	 */
	public String getCell(int col){
		if(col==0) return "1";
		else if(col==1) return "示例计划";
		else if(col==2) return "2";
		else if(col==3) return "1";
		else return "";
	}

}
