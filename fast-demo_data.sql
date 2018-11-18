insert into t_user(`id`,`loginPwd`,`nickName`,`sex`,`mobileNo`,`loginErrCount`,`lastLoginTime`,`createTime`,
`loginName`,
`lastModifyTime`,`email`,`lastRecordLoginErrTime`,`status`) values(1,'123456','张三',1,12810002000,0,null,'2018-11-18 ' ||
 '08:29:43.382','fastjrun','2018-11-18 08:29:43.382','12810002000@128.com',null,1);
 insert into t_user(`id`,`loginPwd`,`nickName`,`sex`,`mobileNo`,`loginErrCount`,`lastLoginTime`,`createTime`,
`loginName`,
`lastModifyTime`,`email`,`lastRecordLoginErrTime`,`status`) values(2,'123456','王五',1,12810002002,0,null,'2018-11-18' ||
 ' ' ||
 '08:29:43.382','fastjrun2','2018-11-18 08:29:43.382','12810002002@128.com',null,1);

 insert into t_user_login(`id`,`userId`,`deviceId`,`loginCredential`,`createTime`,`logOutTime`, `inValidateTime`,`status`) values(1,1,'unittest1','uuid1','20181118082943382',null,null,1);

 insert into t_user_login(`id`,`userId`,`deviceId`,`loginCredential`,`createTime`,`logOutTime`,`inValidateTime`,`status`) values(2,2,'unittest2','uuid2','20181118082943382',null,null,1);
