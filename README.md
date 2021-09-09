## sdkg（快嘉代码生成Maven插件）
### 功能说明
- 根据自定义接口文档生成接口api代码，该代码可以直接用于实际项目中
- 根据自定义接口文档生成接口实现代码和service定义，该代码可以直接用于实际项目中，支持http和dubbo
- 根据自定义接口文档生成基于swagger-ui风格的代码和默认的serviceMock，利用该代码可以部署接口文档服务器和保证接口可用的联调环境接口系统
- 根据自定义接口文档生成基于testng框架的接口测试用例代码，该代码可直接用于研发过程的联调、自动化测试和冒烟测试
    - 响应报文结构验证
    - 支持自定义断言
        - 错误码断言
        - 返回指定值断言

### 实现原理
- 基于maven插件实现
- 使用dom4j解析接口文档，用jcodemodel输出代码
- 使用jsqlparser解析sql脚本，用jcodemodel输出代码

### to be continued
- 不支持返回对象为interface或者abstract class

## 版本说明：
### v3.1 升级说明
支持响应对象是Page
重构example接口，删除App、Api，新增Web


### v3.0 升级说明
默认支持mybatis3.4
修复一些bug
支持自定义接口扩展的写法

### v2.1.5 升级说明
修复base*Dao的insertAll方法(注意个数限制，应该是<=1000,未完整测试)
完善base*DaoTest的生成方法
修复JacksonUtil的readList方法bug

### v2.1.3 升级说明
修复client-tests install不成功的问题
修复codeg-test install没有skip的问题

### v2.1.2 升级说明
新增mybaits3.4.0+ 支持

### v2.1.1 升级说明
修改client相关代码
重构client测试用例和base*Dao测试用例
新增service测试用例
代码生成用foreach代替线程池


### v2.1 升级说明
#### 项目结构进一步解耦，将代码生成功能和插件分开,从codeg-helper中拆分出codeg-plugin模块
#### 抽离出codeg-test的单测模块
#### 优化代码生成逻辑
#### 调整web和provider模块的代码生成位置区分package
#### 新增接口协议，支持web方式
#### base模板新增BaseService、BaseDao的单元测试用例代码
#### codemodel切换成大神的jcodemodel ：https://github.com/phax/jcodemodel
#### 项目结构进一步解耦，抽离出项目自带协议和自定义协议代码生成器放在sdkg-example模块下
#### 日志组件切换成slf4j+log4j2

### v2.0.1 升级说明
#### bugfix
### v2.0 升级说明
#### 模块进一步优化，sdkg-common拆分成5个模块api、common、client、service和provider
#### 进一步优化接口定义规范：fastjrun-schema.xsd
#### 删除sdkg-demo模块
#### 代码分层，重构
#### 代码生成使用线程池
#### 测试用例增加自定义断言
- 错误码断言
- 响应值中对应标签为指定值，如 {"list[0].versionNo":"v1.4"}、{"nickName":"fastjrun"}等
- 组合条件（and）如{"sex":"1","nickName":"fastjrun"}
#### mybatisgenerator新增
#### 丰富业务层单元测试用例的写法
- 使用h2数据库代替持久层
- 单元测试用例代码模板化，参数和自定义断言配置化

