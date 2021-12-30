### 3.2.4
- 修改mock代码种swagger标签API的tags值，可以为数组（逗号间隔）
### 3.2.3
- 如果po自包含，生成的mock中对于自包含节点设置为空

### 3.2.2
- 方法请求参数可以为数组或者List
- 简化接口定义文件，可以支持空路径


### 3.2.1
- MockHelper.geDate中增加的日期随机值偏大，调整为10以内
- 单元测试测试数据文件修改为yaml格式
- 跨接口定义文件引用对象
- 支持po自包含


### 3.2 
- 支持新生成的po也能作为其他po的parent
- MockHelper.geDate去掉参数
- 支持请求和响应参数也有描述

### tip's
#### 修改版本号
```
mvn versions:set -DnewVersion=3.2.4-SNAPSHOT
mvn versions:update-child-modules
```

