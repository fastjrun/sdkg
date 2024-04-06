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


drop table if exists t_user1;

/*==============================================================*/
/* Table: t_user                                                */
/*==============================================================*/
create table t_user1
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


drop table if exists t_user2;

/*==============================================================*/
/* Table: t_user                                                */
/*==============================================================*/
create table t_user2
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


drop table if exists t_user3;

/*==============================================================*/
/* Table: t_user                                                */
/*==============================================================*/
create table t_user3
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


drop table if exists t_user4;

/*==============================================================*/
/* Table: t_user                                                */
/*==============================================================*/
create table t_user4
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


drop table if exists t_user5;

/*==============================================================*/
/* Table: t_user                                                */
/*==============================================================*/
create table t_user5
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


drop table if exists t_user6;

/*==============================================================*/
/* Table: t_user                                                */
/*==============================================================*/
create table t_user6
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


drop table if exists t_user7;

/*==============================================================*/
/* Table: t_user                                                */
/*==============================================================*/
create table t_user7
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


drop table if exists t_user8;

/*==============================================================*/
/* Table: t_user                                                */
/*==============================================================*/
create table t_user8
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


drop table if exists t_user80;

/*==============================================================*/
/* Table: t_user                                                */
/*==============================================================*/
create table t_user80
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


drop table if exists t_user81;

/*==============================================================*/
/* Table: t_user                                                */
/*==============================================================*/
create table t_user81
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


drop table if exists t_user82;

/*==============================================================*/
/* Table: t_user                                                */
/*==============================================================*/
create table t_user82
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


drop table if exists t_user83;

/*==============================================================*/
/* Table: t_user                                                */
/*==============================================================*/
create table t_user83
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


drop table if exists t_user84;

/*==============================================================*/
/* Table: t_user                                                */
/*==============================================================*/
create table t_user84
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


drop table if exists t_user85;

/*==============================================================*/
/* Table: t_user                                                */
/*==============================================================*/
create table t_user85
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


drop table if exists t_user86;

/*==============================================================*/
/* Table: t_user                                                */
/*==============================================================*/
create table t_user86
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


drop table if exists t_user87;

/*==============================================================*/
/* Table: t_user                                                */
/*==============================================================*/
create table t_user87
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


drop table if exists t_user88;

/*==============================================================*/
/* Table: t_user                                                */
/*==============================================================*/
create table t_user88
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


drop table if exists t_user89;

/*==============================================================*/
/* Table: t_user                                                */
/*==============================================================*/
create table t_user89
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


drop table if exists t_user810;

/*==============================================================*/
/* Table: t_user                                                */
/*==============================================================*/
create table t_user810
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


drop table if exists t_user811;

/*==============================================================*/
/* Table: t_user                                                */
/*==============================================================*/
create table t_user811
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


drop table if exists t_user812;

/*==============================================================*/
/* Table: t_user                                                */
/*==============================================================*/
create table t_user812
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


drop table if exists t_user813;

/*==============================================================*/
/* Table: t_user                                                */
/*==============================================================*/
create table t_user813
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