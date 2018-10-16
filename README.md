## sdkg（快嘉代码生成Maven插件）
### 功能说明
- 根据自定义接口文档生成接口sdk代码，该代码可以直接用于实际项目中
- 根据自定义接口文档生成接口实现代码和service定义，该代码可以直接用于实际项目中，支持http和dubbo
- 根据自定义接口文档生成基于swagger-ui风格的代码和默认的serviceMock，利用该代码可以部署接口文档服务器和保证接口可用的联调环境接口系统
- 根据自定义接口文档生成基于testng框架的接口测试用例代码，该代码可直接用于研发过程的联调、自动化测试和冒烟测试
    - 响应报文结构验证
    - 支持自定义断言
        - 错误码断言
        - 返回指定值断言

### 实现原理
- 基于maven插件实现
- 快嘉代码生成Maven插件使用dom4j解析接口文档，用codelmodel输出代码，sdk中使用jackson解析返回报文转换成相应的报文JavaBean
- sdk-demo为示例工程，其中demo-bundle和demo-bundle-mock中的代码均为自动生成，demo-api的主体代码为自动生成，配置文件和配置参数需要自行维护，另外可能会写一些额外的测试类。
- sdk-demo/demo-api示例工程使用testng框架的参数化方法和maven的profile实现测试用例代码和数据分离，并可支持多套环境
- sdk-demo/demo-api示例工程使用testng框架的参数化方法可以支持对同一个测试用例灌入多组测试数据
- sdk-demo/demo-provider-mock示例工程集成了sdk-demo/demo-bundle-mock模块，打包后，可以作为接口文档服务器和保证接口可用的联调环境接口系统部署使用


### 常见问题
- 测试参数中如果有中文(unicode)，注意修改dos窗口代码页为utf-8，参考命令chcp 65001；修改后将dos窗口字体修改为True Type字体"Lucida Console"
- 测试参数中如果有中文(unicode)，eclipse中直接执行testng测试用例会报错，参考报错信息如下：
` Software caused connection abort: socket write error`


### 基于sdkg命令生成代码参考示例
```
# 本地参考安装codeg-helper插件
mvn clean install -pl codeg-helper -am
# 本地编译打包demo-provider-mock，并设置生成代码作者为“张三”
mvn clean package -pl sdkg-demo/demo-provider-mock -am -Dbdmgc.skip=false -Dcodeg.author=张三
# 本地编译打包demo-provider-mock，并设置生成代码公司为“阿里居”
mvn clean package -pl sdkg-demo/demo-provider-mock -am -Dbdmgc.skip=false -Dcodeg.company=阿里居
```

### to be continued
#### codemodel重构
#### mybatisgenerator新增
#### 支持自定义协议api和provider的代码生成
#### 代码生成多线程化

## 版本说明：
### v2.0 升级说明
#### 模块进一步优化，sdkg-common拆分成5个模块api、common、client、service和web
#### 进一步优化接口定义规范：fastjrun-schema.xsd
#### 新增sdkg-demo模块，会作为示例工程演示如何基于快嘉代码生成插件实现项目演进
#### 代码分层，重构
#### 测试用例增加自定义断言
- 错误码断言
- 响应值中对应标签为指定值，如 {"list[0].versionNo":"v1.4"}、{"nickName":"fastjrun"}等
- 组合条件（and）如{"sex":"1","nickName":"fastjrun"}

