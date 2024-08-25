### 4.4.0.1
- fix bug

### 4.4.0
- 新增mabatis-plus-generator,引入mybatis-plus-join
- 引入lombok，简化代码
- jcodemodel升级至3.4.1
- *-sdkg和sdkg统一版本号
- 支持swagger3
- 示例引入knife4j-4.5.0

### 4.3.0-alpha
- log4j2 升级到2.23.1
- dom4j 升级到2.1.4
- 新增mabatis-plus-generator
### 4.2.5
- RequestParam的required应该跟随配置
### 4.2.4
- RequestParam的required应该跟随配置
### 4.2.3
### 4.2.2
- fix bug
  - 生成的bundle中植入swagger标记不完整
  
### 4.2.1
- fix bug
  - 升级snakeyaml
  
### 4.2
- fix bug
  - 引入jsr250-api
  - 生成的bundle中植入swagger标记
  - 删除eladmin-sdkg

### 4.1.1
- fix bug
  - controller支持parameter参数为数组

### 4.1 
- 代码生成器重构
- 新增接口生成器脚手架:
  - [https://github.com/fastjrun/fastjrun-archetype](https://github.com/fastjrun/fastjrun-archetype "https://github.com/fastjrun/fastjrun-archetype")
  - [https://gitee.com/fastjrun/fastjrun-archetype](https://gitee.com/fastjrun/fastjrun-archetype "https://gitee.com/fastjrun/fastjrun-archetype")

### 4.0.3
- 修复*-mock-server的pom文件
- 基于spring-boot2提供测试基类
- testng升级为7.4.0

### 4.0.2
#### bugfix
- 当requestBody为数组的时候，变量名可配置，如果配置为空，则直接使用变量类型的驼峰式类名称

#### bug //TODO
- 当requestBody为List的时候，变量名不配置将会报错

### 4.0
- 修改对应Generic接口的代码生成方法
- 删除基于mabatis基于注解代码的生成方法
- 删除elastic-job相关
- 删除dubbo相关
- 删除APP和API接口代码生产方法
- 升级log4j2为时新版本2.17.1
- 删除task
- 重构
- 新增示例接口生成器：eladmin-sdkg和接口生成实例
- jcodemodel升级至3.3.0

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
mvn versions:set -DnewVersion=4.2.4
mvn versions:update-child-modules
```

