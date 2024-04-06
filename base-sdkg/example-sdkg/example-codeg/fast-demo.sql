/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2017/4/8 22:05:52                            */
/*==============================================================*/


drop table if exists t_user;

/*==============================================================*/
/* Table: t_user                                                */
/*==============================================================*/
create table t_user
(
   id                   int not null auto_increment comment '主键',
   loginName            varchar(50) default NULL,
   loginPwd             char(32),
   mobileNo             varchar(20) comment '手机号',
   nickName             varchar(20) comment '昵称',
   sex                  smallint default 1 comment '1：男；2：女：3；未知',
   email                char(30) comment '邮件',
   createTime           datetime,
   lastModifyTime       datetime,
   lastLoginTime        char(17),
   loginErrCount        smallint default 0,
   lastRecordLoginErrTime char(17),
   status               char(1) default '1' comment '1：正常；2：密码锁定；3：人工锁定',
   primary key (id)
);

drop table if exists t_user_login;

/*==============================================================*/
/* Table: t_user_login                                          */
/*==============================================================*/
create table t_user_login
(
   id                   int not null auto_increment comment '主键',
   userId               int not null,
   deviceId             char(32),
   loginCredential      char(32),
   createTime           char(17),
   logOutTime           char(17) comment '凭证实际注销时间',
   inValidateTime       char(17) comment '按照系统设置，凭证应该失效的时间',
   status               char(1) default '1' comment '1：正常；2：无效',
   primary key (id)
);