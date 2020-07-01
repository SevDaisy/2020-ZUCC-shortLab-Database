CREATE TABLE `tbl_user` (
  `user_id` varchar(50) NOT NULL,
  `user_pwd` varchar(255) NOT NULL,
  `register_time` datetime NOT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



CREATE TABLE `tbl_plan` (
  `plan_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` varchar(50) NOT NULL COMMENT '计划所属用户id',
  `plan_order` int(11) NOT NULL COMMENT '计划序号，要求同一个用户下序号不重复',
  `plan_name` varchar(255) NOT NULL COMMENT '计划名称',
  `create_time` datetime NOT NULL,
  `step_count` int(11) NOT NULL COMMENT '步骤总数据',
  `start_step_count` int(11) NOT NULL COMMENT '已经开始的步骤数',
  `finished_step_count` int(11) NOT NULL COMMENT '已经完成的步骤数',
  PRIMARY KEY (`plan_id`),
  UNIQUE KEY `user_id` (`user_id`,`plan_order`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `tbl_step` (
  `step_id` int(11) NOT NULL AUTO_INCREMENT,
  `plan_id` int(11) NOT NULL COMMENT '所属计划编号',
  `step_order` int(11) NOT NULL COMMENT '步骤序号，要求同一计划下步骤序号不重复',
  `step_name` varchar(255) NOT NULL,
  `plan_begin_time` datetime NOT NULL COMMENT '计划开始时间',
  `plan_end_time` datetime NOT NULL COMMENT '计划结束时间',
  `real_begin_time` datetime DEFAULT NULL COMMENT '实际开始时间',
  `real_end_time` datetime DEFAULT NULL COMMENT '实际结束时间',
  PRIMARY KEY (`step_id`),
  KEY `plan_id` (`plan_id`,`step_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



