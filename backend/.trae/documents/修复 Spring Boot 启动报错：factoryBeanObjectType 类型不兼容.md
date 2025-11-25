## 问题根因
- 报错：`Invalid value type for attribute 'factoryBeanObjectType': java.lang.String`，发生在加载 `categoryMapper` Bean 时。
- Spring 6.1/Spring Boot 3.2 对 `FactoryBean` 类型解析做了校验升级，要求 `factoryBeanObjectType` 必须是 `Class/ResolvableType`，而旧版 MyBatis Spring 用了 `String`，导致不兼容。
- 该问题是 Spring Boot 3.2 与旧版 MyBatis Spring 2.x 的已知不兼容，官方在 MyBatis Spring 3.0.3 进行了修复。[参考: mybatis/spring #855/#865/#896](https://github.com/mybatis/spring/issues/855)

## 当前代码情况
- `CategoryMapper.java` 为标准 MyBatis-Plus Mapper (`@Mapper` + `BaseMapper<Category>`)，自身无异常。
- `pom.xml` 使用 `mybatis-plus-boot-starter:3.5.5`，该版本可能引入旧版 `mybatis-spring` 2.x，从而触发不兼容。

## 修复方案（二选一，推荐其一即可）
- 方案A（最小变更，强制覆盖传递依赖）：
  - 在 `pom.xml` 里显式添加/覆盖依赖：
    - `org.mybatis:mybatis-spring:3.0.3`（或更高稳定版）
    - 可选：`org.mybatis:mybatis:3.5.14`（或更高稳定版）
  - 保持 `mybatis-plus-boot-starter` 版本不变，确保运行时使用修复后的 `mybatis-spring`。
- 方案B（升级 MyBatis-Plus）：
  - 将 `mybatis-plus-boot-starter` 升级到支持 Spring Boot 3.2 的版本（如 `3.5.6+`）。
  - 让其传递依赖自动带入兼容的 `mybatis-spring` 3.0.3+。

## 同步优化（可选但建议）
- 让 `spring-boot-maven-plugin` 版本与父 POM 对齐：直接移除插件版本声明或改为 `3.2.12`，减少版本漂移风险。

## 验证步骤
- 清理并重新运行：`mvn -U clean spring-boot:run`（`-U` 确保更新到最新依赖）。
- 应用应能正常启动；数据库连接失败等将是后续独立问题，不会再出现 `factoryBeanObjectType` 报错。

## 我将执行的具体改动
- 按【方案A】修改 `pom.xml`，新增依赖覆盖 `mybatis-spring` 到 `3.0.3`，必要时一并升级 `mybatis` 到 `3.5.14`。
- 可选：对齐 `spring-boot-maven-plugin` 版本（或删版本号以继承父版）。
- 运行并确认服务成功启动。

请确认采用方案A（最小变更）或方案B（整体升级）。我更推荐方案A，改动小、可快速验证兼容性。