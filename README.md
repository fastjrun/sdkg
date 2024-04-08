## sdkg（快嘉代码生成Maven插件）
### 功能说明
- 根据标准sql文件生成mybatis-plus-join代码（只支持mysql）
- 根据自定义接口文档生成接口api代码
- 根据自定义接口文档生成接口实现代码和service定义
- 根据自定义接口文档生成基于swagger-ui风格的代码和默认的serviceMock，利用该代码可以部署接口文档服务器和保证接口可用的联调环境接口系统
- 支持不同类型接口代码生成器定制

### 实现原理
- 基于maven插件实现
- 使用dom4j解析接口文档，使用jsqlparser解析sql文件，使用jcodemodel输出代码


