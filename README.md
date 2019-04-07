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
- 快嘉代码生成Maven插件使用dom4j解析接口文档，用codelmodel输出代码

### to be continued
#### codemodel重构
#### 支持自定义协议api和provider的代码生成

## 版本说明：
### v2.1 升级说明
#### 项目结构进一步解耦，将代码生成功能和插件分开,从codeg-helper中拆分出codeg-plugin模块
#### 抽离出codeg-test的单测模块
#### 优化代码生成逻辑
#### 调整web和provider模块的代码生成位置区分package
#### 新增web模块进一步优化
#### 新增接口协议，支持web方式
#### base模板新增BaseService、BaseController

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

