# 前端项目说明（SecondHandSystem）

## 项目简介
- 二手交易系统的前端子项目，基于 Vue 3 + Vue CLI 搭建
- 内置 Element Plus 组件库与中文本地化，使用 Pinia 管理用户状态，Vue Router 实现路由与权限控制
- 与后端通过统一的 REST 接口交互，默认通过开发代理转发至 `http://localhost:8080`

## 技术栈
- 框架：Vue 3（组合式 API 可选，当前代码以选项式为主）
- 路由：Vue Router 4（历史模式）
- 状态：Pinia
- UI：Element Plus（中文 locale 已启用）
- HTTP：Axios（包含请求/响应拦截、统一错误处理）
- 构建工具：@vue/cli-service
- 代码质量：ESLint（plugin:vue/vue3-essential + eslint:recommended）、Prettier

相关文件：
- 包管理与脚本：package.json
- 开发代理与端口配置：vue.config.js
- 入口与插件注册：src/main.js
- HTTP 客户端封装：src/api/http.js
- 路由与守卫：src/router/index.js
- 路径别名配置（@ 指向 src）：jsconfig.json
- 后端接口文档（供联调参考）：document/接口文档.md

## 环境要求
- Node.js：建议使用当前 LTS（推荐 18+）
- 包管理器：npm（项目脚本基于 npm）

## 快速开始
- 安装依赖

```bash
npm install
```

- 启动开发服务器（默认端口 5173，自动代理 /api 与 /uploads 到后端）

```bash
npm run dev
# 或
npm run serve
```

- 构建生产包（输出至 /dist 目录）

```bash
npm run build
```

- 代码检查与格式化

```bash
npm run lint
npm run format
```

## 目录结构
- src/
  - api/：接口封装（以资源维度拆分，如 products、orders、users 等）
  - components/：通用组件与管理端组件
  - router/：路由定义与路由守卫
  - stores/：Pinia 状态（含用户登录态）
  - views/：页面视图（首页、登录注册、商品详情/发布、订单确认、个人中心、后台面板等）
  - App.vue：应用根组件
  - main.js：应用入口

建议通过 `@` 别名引用 src 下模块，例如：

```js
import X from '@/api/products'
```

## 路由与权限
- 路由文件：src/router/index.js
- 受保护页面通过 `meta.requiresAuth` 标记，需登录后访问
- 管理员页面通过 `meta.requiresAdmin` 标记，需具备 `role=admin`
- 在 `beforeEach` 守卫中，前置调用 `getMe()` 初始化用户登录态；未登录访问受保护页将重定向至登录页并附带 `redirect` 参数

## 接口与网络
- 统一基地址：`/api`（见 src/api/http.js）
- 开发环境代理：在 vue.config.js 中将 `/api` 与 `/uploads` 代理到 `http://localhost:8080`
- 认证机制：与后端基于 Session；`withCredentials: true` 以携带 Cookie
- 响应规范：后端返回 `{code, message, data}`；拦截器对 `code !== 200` 均抛错并携带错误信息
- 如生产环境不使用同域反向代理，可在部署环境层（Nginx 等）处理路径转发，或自行调整 `http.js` 的 `baseURL`

示例（商品接口，更多见后端接口文档）：

```js
import * as ProductsApi from '@/api/products'

// 列表
ProductsApi.list({ page: 1, size: 10, keyword: 'iphone' })

// 详情
ProductsApi.detail(1001)
```

## 构建与部署
- 运行 `npm run build` 生成生产资源至 `/dist`
- 部署静态资源至任意静态服务器（Nginx/Apache 等）
- 生产环境推荐在网关或 Web 服务器层将 `/api` 与 `/uploads` 代理到后端服务

## 代码风格
- ESLint 规则见 package.json `eslintConfig` 段
- Prettier 配置见 .prettierrc.json
- 组件命名规则中关闭了 `vue/multi-word-component-names`（适配项目内部分单词组件名）

## 常见问题
- 启动后无法登录或访问受保护页面：确认后端运行在 `http://localhost:8080` 且允许 Session；前端已开启 `withCredentials`
- 接口 401：由路由守卫统一处理重定向到登录页；登录成功后将回跳原目标页
- 跨域问题：开发态由代理解决；生产态需在反向代理或网关层统一域名与路径
