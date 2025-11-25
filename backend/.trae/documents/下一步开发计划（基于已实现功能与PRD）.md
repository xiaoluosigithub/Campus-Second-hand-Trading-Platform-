## 当前进度
- 已就绪：注册/登录/登出（Session）、统一响应与异常、登录拦截器、实体/Mapper/Service CRUD。
- 商品：列表与详情（分页/筛选/排序）、发布、编辑（价格/描述）、状态（在售/下架）、“我的商品”。
- 交易：购物车（增/查/删/清理失效）、单品下单（事务锁定唯一售出）、取消订单（事务回滚为在售）。
- 文档：接口文档与已实现功能已同步更新，构建与最小联调通过。

## M4：订单流转与后台管理
### 订单流转
- 接口：
  - `PATCH /api/orders/{id}/ship`（卖家发货：填写 `trackingNumber` → `status=1`）
  - `PATCH /api/orders/{id}/confirm`（买家确认收货 → `status=2`）
  - `GET /api/orders/me/bought`（我买到的，分页）
  - `GET /api/orders/me/sold`（我卖出的，分页）
- 规则与校验：
  - IDOR 防护：发货需 `order.sellerId == currentUserId`；确认需 `order.buyerId == currentUserId`。
  - 状态边界：仅 `status=0` 可发货；仅 `status=1` 可确认收货；其余返回 400。
  - 参数校验：`trackingNumber` 非空；分页 `size≤50`。
- 文档与测试：
  - 更新 `document/接口文档.md` 增加以上契约与示例。
  - 测试用例：发货/确认越权、状态边界错误、我的订单分页。

### 后台管理（管理员）
- 商品审核：`PATCH /api/admin/products/{id}/review`（通过/不通过+驳回理由；状态：0 待审 → 1 在售 / 2 不通过）
- 分类管理：`GET/POST/DELETE /api/admin/categories`（名称唯一、基础CRUD）
- 管理校验：
  - 拦截器或角色判断：要求 `role=admin` 才能访问 `/api/admin/**`。
- 文档与测试：
  - 文档补充审核与分类接口；
  - 测试：审核状态切换、分类唯一名校验、管理员权限控制。

## M5：消息模块与图片占位
### 消息（留言板）
- 接口：
  - `GET /api/messages?contactId={id}`（与某人消息记录，分页）
  - `POST /api/messages`（发送消息：`receiverId`, `content`）
  - `GET /api/messages/contacts`（最近联系人列表，基于 messages 聚合）
- 规则与校验：
  - IDOR 防护：仅会话参与双方可访问对话记录；发送时 `receiverId != currentUserId`。
  - 分页与排序：按 `created_at` 降序；`size≤50`。
- 文档与测试：
  - 文档补充消息接口契约、错误示例；
  - 测试：会话分页、未授权访问拦截、发送参数校验。

### 图片占位（可选）
- `GET /api/mock/image` 返回占位图 URL，前端发布演示可直接使用。

## 非功能与一致性
- 错误码与提示统一（401/403/409/422/400）；分页默认与上限。
- 日志合规：不输出敏感信息；关键流程记录 info。
- 状态语义与DB一致：商品状态 0/1/2/3/4；订单状态 0/1/2/3。

## 交付与文档
- 每阶段完成后：更新 `document/接口文档.md` 与 `document/已实现功能.md`，附 curl 示例与边界说明；提交 Postman/Apifox 用例集合。

## 起步任务（优先实现）
- 实现订单流转接口（发货/确认/我的订单）与管理员审核；接入管理员权限判断；更新文档与测试用例。