## 原因
- 当前代码库未包含用户实体与其 Mapper/Service；此前以为已有基线，实际不存在。为与数据库 `users` 表保持一致，需要补齐。

## 待新增文件
- 实体：`com.lyx.secondhandsystem.entity.User`
  - `@TableName("users")`
  - 字段映射：
    - `@TableId("user_id", IdType.AUTO) Integer userId`
    - `String username`
    - `@TableField("password_hash") String passwordHash`
    - `String nickname`
    - `@TableField("avatar_url") String avatarUrl`
    - `String signature`
    - `String role`（先用 String 承载 ENUM 值 `user|admin`）
    - `@TableField("created_at") LocalDateTime createdAt`
    - `@TableField("updated_at") LocalDateTime updatedAt`
- Mapper：`com.lyx.secondhandsystem.mapper.UserMapper extends BaseMapper<User>`
- Service：
  - 接口 `UserService`：`create/getById/list(update/deleteById)`（与其它实体保持一致风格）
  - 实现 `UserServiceImpl`：基础 CRUD 与按 `username` 的查询方法（后续接口层用于注册/登录）

## 约定与风格
- 与现有实体一致：使用 `LocalDateTime`、`@TableField` 明确下划线列、Lombok `@Data`。
- 不编写 Controller/接口逻辑，仅落地实体、Mapper、Service 层。
- 可选：在实体与字段上补充简要注释，便于代码审阅。

## 验证
- 运行 `mvn -q -DskipTests compile` 确认编译通过。
- 简单调用 Service 进行基础 CRUD 自检（不暴露接口）。

确认后我将按此计划补齐 User 实体、Mapper 与 Service，并完成一次构建验证。