# SecondHandSystem 后端 README

本项目为二手交易系统的后端服务，基于 Spring Boot + MyBatis-Plus + MySQL，提供用户注册登录、商品发布与浏览、购物车、下单与订单流转、站内消息，以及后台审核与统计等能力。本文档面向本地开发与部署，包含快速开始、配置说明、接口概览与关键业务规则。

## 技术栈
- Spring Boot 3.2（Java 17）
- MyBatis-Plus 3.5.5（含 mybatis-spring 3.0.3）
- MySQL 8+ 驱动
- Jakarta Validation（参数校验）
- BCrypt 密码哈希（spring-security-crypto）
- Maven 构建

相关文件：
- 主启动类：SecondHandSystemApplication.java
 - 依赖与构建：pom.xml
 - 应用配置：src/main/resources/application.properties

## 目录结构
- src/main/java/com/lyx/secondhandsystem
-  - common：统一响应与全局异常（ApiResponse.java、GlobalExceptionHandler.java）
-  - config：登录/管理员拦截与静态资源（LoginInterceptor.java、AdminInterceptor.java、WebConfig.java）
  - controller：REST 控制器（认证、商品、分类、购物车、订单、消息、文件上传、后台）
  - dto：请求/响应 DTO
  - entity：领域模型（User/Product/Category/ShoppingCart/Order/Message）
  - mapper：MyBatis-Plus Mapper（含少量原生 SQL）
  - service：服务接口与实现（分页、筛选、并发保障等）

## 快速开始

### 环境准备
- JDK 17
- Maven 3.9+
- MySQL 8+，并创建数据库 `secondhand_database`

### 配置数据库
编辑 application.properties 或通过环境变量覆盖：
```
spring.datasource.url=jdbc:mysql://localhost:3306/secondhand_database?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD:123456}
mybatis-plus.configuration.log-impl=org.apache.ibatis.logging.stdout.StdOutImpl
```
在 PowerShell 下运行时可设置环境变量：
```
$env:DB_USERNAME='root'
$env:DB_PASSWORD='123456'
```

### 启动服务
- 方式一：开发模式启动
```
mvn spring-boot:run
```
- 方式二：打包并运行
```
mvn -q -DskipTests clean package
java -jar target/SecondHandSystem-0.0.1-SNAPSHOT.jar
```
默认监听 `http://localhost:8080`。

## 统一响应与错误处理
- 响应结构：`{"code":200,"message":"OK","data":...}`（见 ApiResponse.java）
- 验证异常：422 Unprocessable Entity（见 GlobalExceptionHandler.java）
- 业务参数非法：400 Bad Request（见 GlobalExceptionHandler.java）

## 认证与授权
- 会话机制：基于服务端 Session，登录成功后返回 `JSESSIONID`；受保护接口需要在请求头携带 Cookie。
- 登录拦截：检查 Session 中 `userId`（未登录返回 401），放行部分 GET 接口（商品列表/详情、分类树/路径）。见 LoginInterceptor.java 与 WebConfig.java。
- 管理员拦截：`/api/admin/**` 需要 `role=admin`，否则 403。见 AdminInterceptor.java。
- 密码安全：BCrypt 哈希比对（见 AuthController.java，登录逻辑见 AuthController.java）。

## 领域模型
- 用户：User.java（`password_hash` 通过 `@JsonIgnore` 不在接口输出）
- 商品：Product.java（`images` 使用 `JacksonTypeHandler`，类上启用 `@TableName(autoResultMap = true)`）
- 分类：Category.java
- 购物车：ShoppingCart.java
- 订单：Order.java
- 消息：Message.java

## 接口概览（主要）
完整请求/响应示例请参考文档：document/接口文档.md。

- 认证（AuthController.java）
  - POST `/api/auth/register`（校验重复、BCrypt 存储）
  - POST `/api/auth/login`（返回基础用户信息并写入 Session）
  - POST `/api/auth/logout`
- 商品（ProductController.java）
  - GET `/api/products`：分页浏览；`size` 范围 1–50；`category/status/keyword/sort=price_asc|price_desc|newest`；可选 `excludeActive`
  - GET `/api/products/{id}`：详情（访问量+1、附带卖家昵称与头像）
  - POST `/api/products`：发布（登录态）
  - PUT `/api/products/{id}`：编辑（登录态；待审核不可编辑）
  - PATCH `/api/products/{id}/status`：在售/下架（登录态；`status=1|4`）
  - DELETE `/api/products/{id}`：删除（登录态；仅待审核/审核不通过/已下架且无关联订单）
  - POST `/api/products/{id}/cancel`：取消发布（在售→下架；其他状态在无关联订单时允许删除）
  - GET `/api/products/me`：我发布的商品（分页，筛选与排序同商品列表）
- 分类（CategoryController.java）
  - GET `/api/categories/tree`、GET `/api/categories/{id}/path`
- 购物车（CartController.java）
  - POST `/api/cart`（添加）
  - GET `/api/cart`（分页）、GET `/api/cart/items`（聚合视图）
  - DELETE `/api/cart/{id}`（移除）
- 订单（OrderController.java）
  - POST `/api/orders`（下单单件，参数校验与并发保障）
  - POST `/api/orders/{id}/cancel`（买家取消，限待发货）
  - PATCH `/api/orders/{id}/ship`（卖家发货）
  - PATCH `/api/orders/{id}/confirm`（买家确认收货）
  - GET `/api/orders/me/bought`、GET `/api/orders/me/sold`（我的订单）
  - GET `/api/orders/precheck?productId=...`（前置校验商品在售且无进行中订单）
- 支付（PayController.java）
  - POST `/api/pay/mock?orderId=...&method=alipay|wechat`
- 站内消息（MessageController.java）
  - GET `/api/messages?contactId=...`（分页对话，附带双方昵称与头像）
  - POST `/api/messages`（发送消息；禁止发送给自己）
  - GET `/api/messages/contacts`（联系人列表，按最后消息排序）
  - GET `/api/messages/unread/count`、POST `/api/messages/{id}/read`
- 文件上传（FilesUploadController.java）
  - POST `/api/files/upload`（multipart/form-data，支持 png/jpg/jpeg/webp，≤5MB；返回 `/uploads/<file>`）
- 后台（admin/*，需管理员角色）
-  - 商品审核（AdminProductController.java）
  - 用户/订单/统计（AdminUserController.java、AdminOrderController.java、AdminStatsController.java）

## 关键业务规则
- 商品状态流转
  - 0 待审核（创建/编辑后进入）
  - 1 在售（管理员审核通过）
  - 2 审核拒绝（管理员驳回，需填写理由）
  - 3 已售（买家确认收货后，系统将商品状态置为 3）
  - 4 已下架（卖家主动下架或取消发布）
-  - 规则入口：商品编辑不可在“待审核”状态进行（见 ProductController.java:update），管理员审核接口（见 AdminProductController.java），订单确认后置商品为已售（见 OrderServiceImpl.java）。
- 订单状态流转
-  - 0 待发货（创建订单）→ 1 已发货 → 2 已完成；买家取消（3 已取消）仅限“待发货”（见 OrderServiceImpl.java）。
- 并发下单保障
-  - 下单时使用 `SELECT ... FOR UPDATE` 锁商品（见 ProductMapper.java），并统计该商品进行中的订单数量（状态 0/1）（见 OrderMapper.java），若存在则拒绝下单（见 OrderServiceImpl.java:placeSingleOrder）。
- 删除/取消发布约束
-  - 删除：仅 0/2/4 状态且商品无关联订单；同时清理购物车中的该商品（见 ProductController.java:delete）。
-  - 取消发布：在售（1）→ 下架（4）；其它可在无关联订单时删除（见 ProductController.java:cancel）。
- 支付校验
-  - 模拟支付接口校验买家身份、订单状态与支付方式一致性（`method=alipay|wechat`）（见 PayController.java）。

## 静态资源与文件上传
- 静态映射：`/uploads/**` → 项目根目录下的 `uploads` 文件夹（见 WebConfig.java）。
- 上传接口返回相对路径，可直接用于前端展示。

## 分页与筛选约定
- 通用分页：`page>=1`，`size` 范围 1–50。
- 商品排序：`sort=newest|price_asc|price_desc`，默认 `newest`。
- 关键字搜索：对 `title` 模糊匹配。
- `excludeActive=true`：商品列表中排除存在进行中订单的在售商品（仅在非“我的商品”场景生效）。

## 典型调用示例（本地）
以下示例使用 PowerShell + curl（携带 Cookie 实现会话）：
```
# 注册
curl -s -X POST http://localhost:8080/api/auth/register -H "Content-Type: application/json" -d "{\"username\":\"alice\",\"password\":\"123456\",\"confirmPassword\":\"123456\",\"nickname\":\"Alice\"}"

# 登录（保存 Cookie 到文件）
curl -i -X POST http://localhost:8080/api/auth/login -H "Content-Type: application/json" -d "{\"username\":\"alice\",\"password\":\"123456\"}" | Select-String -Pattern "Set-Cookie"
# 或者使用 curl 的 --cookie-jar 保存到本地

# 浏览商品列表
curl -s "http://localhost:8080/api/products?page=1&size=10&sort=newest"

# 携带 JSESSIONID 访问受保护接口（示例）
curl -s -X POST http://localhost:8080/api/products -H "Content-Type: application/json" -H "Cookie: JSESSIONID=<复制登录返回的值>" -d "{\"title\":\"旧书\",\"price\":9.9,\"categoryId\":1,\"images\":[\"/uploads/xxx.jpg\"]}"
```

## 开发与调试建议
- 启用 SQL 日志：已默认开启 `mybatis-plus.configuration.log-impl=org.apache.ibatis.logging.stdout.StdOutImpl`。
- 校验提示：所有 DTO 使用 `jakarta.validation` 注解；422 返回字段级错误。
- 编译构建：`mvn -q -DskipTests compile` 可快速校验依赖与源码。

## 常见问题排查
- 数据库连接失败：检查 MySQL 是否启动、账号密码是否正确、`serverTimezone=UTC` 与 `allowPublicKeyRetrieval=true`。
- 端口占用：Spring Boot 默认 8080；如冲突可在 `application.properties` 增加 `server.port=8081`。
- JDK 版本不匹配：确保本地为 JDK 17（POM 使用 `maven-compiler-plugin` 指定 `release=17`）。

## 更多文档
- 接口细节与示例：document/接口文档.md
