## 系统架构

系统技术架构为**前后端分离**：

- 后端采用 **Maven** + **SpringBoot + MyBatisPlus** 构建服务端逻辑与数据库交互。
- 前端采用 **Vue 3 + ElementUI + Axios** 实现页面展示和接口调用。
- 数据库采用 **MySQL 8** 进行数据存储。

## 前端

前端基于二手交易系统的业务需求，已完成用户端与管理端的核心功能闭环，支持商品发布与管理、购物车、订单全流程、消息交流以及后台审核与统计。

架构围绕模块化视图与统一 API 封装，配合路由守卫实现登录与权限控制，整体交互采用 Element Plus 保持一致的视觉与操作体验。

功能完成情况

- 用户生命周期：注册、登录、登出、获取当前用户；个人资料编辑（头像上传、昵称、签名）
- 商品管理：首页筛选与排序、详情展示、发布表单（分类级联、图片上传、校验）、我的商品（编辑、上下架、删除）
- 购物车：加入、列表、移除、清理失效、单品结算
- 订单流程：预校验、创建、取消、发货（物流单号）、确认收货；我买到的/我卖出的订单列表
- 消息中心：联系人、消息列表、发送消息、未读计数、标记已读
- 管理后台：商品审核（通过/驳回）、用户管理（搜索、角色调整、删除）、分类管理（增删）、订单列表与详情、订单统计（日/区间）

## 后端

后端已打通核心业务链路，支持本地可运行与基础稳定性。采用会话认证与统一响应，覆盖用户、商品、购物车、订单、消息和管理后台等模块。

已实现功能

- 用户认证与会话：注册、登录、退出，会话保持与权限控制（普通用户/管理员）
- 商品功能：商品浏览与详情查看，发布、编辑、上下架，我的商品列表，取消与删除
- 购物车功能：添加商品到购物车，查看购物车，删除单项。
- 订单流转：下单与下单前校验，取消订单、发货、确认收货，我的购买/我的出售订单查询
- 消息系统：私信收发，联系人列表，未读消息统计与消息已读处理
- 用户资料：修改个人资料（昵称、头像等）
- 管理后台：商品审核，分类维护，用户管理（查询、角色调整、删除），订单查询与数据统计

## 数据库

### 1. 用户表（Users Table）  

```sql
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,       -- 主键，用户唯一ID
    
    username VARCHAR(50) NOT NULL UNIQUE,         -- 账号：用于登录，唯一
    password_hash VARCHAR(255) NOT NULL,          -- 密码哈希，而不是明文密码
    
    nickname VARCHAR(50),                         -- 昵称，默认可设置为账号
    avatar_url VARCHAR(255),                      -- 头像图片URL
    signature VARCHAR(200),                       -- 个性签名
    
    role ENUM('user', 'admin') DEFAULT 'user',    -- 角色：普通用户 or 管理员
                                                  -- 用于控制用户登录进入不同页面
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, 
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP 
        ON UPDATE CURRENT_TIMESTAMP               -- 更新时间（自动维护）
);
```

### 2. 商品表（Products Table）  

```sql
CREATE TABLE products (
    product_id INT AUTO_INCREMENT PRIMARY KEY,     -- 商品ID
    
    seller_id INT NOT NULL,                        -- 卖家用户ID（关联 users 表）
    category_id INT NOT NULL,                      -- 商品所属分类ID（关联 categories）
    
    title VARCHAR(150) NOT NULL,                   -- 商品标题
    description TEXT,                              -- 商品描述
    price DECIMAL(10, 2) NOT NULL,                 -- 商品价格
    
    images JSON,                                   -- 商品图片列表（JSON数组）
                                                    -- 例：["img1.jpg", "img2.jpg"]
    
    status TINYINT NOT NULL DEFAULT 0,             -- 商品状态：
                                                    -- 0 待审核  
                                                    -- 1 出售中  
                                                    -- 2 审核不通过  
                                                    -- 3 已售出（完成订单后自动变为此状态）  
                                                    -- 4 已下架（用户主动下架）
    
    rejection_reason VARCHAR(255),                 -- 审核不通过原因（管理员填写）
    view_count INT DEFAULT 0,                      -- 浏览量
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP 
        ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (seller_id) REFERENCES users(user_id) ON DELETE CASCADE,
                                                   -- 删除用户 → 删除其商品
    
    FOREIGN KEY (category_id) REFERENCES categories(category_id)
);
```

### 3. 分类表 (Categories) 

```sql
CREATE TABLE categories (
    category_id INT AUTO_INCREMENT PRIMARY KEY,   -- 分类ID
    
    name VARCHAR(50) NOT NULL UNIQUE,             -- 分类名称（如：数码产品 / 服饰）
                                                  -- UNIQUE 保证不会出现重复分类
    
    sort_order INT DEFAULT 0,                     -- 前端展示排序用（越大越靠前）
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 4. 购物车表（Shopping Cart Table）  

```sql
CREATE TABLE shopping_cart (
    cart_id INT AUTO_INCREMENT PRIMARY KEY,
    
    user_id INT NOT NULL,                         -- 购物车属于哪个用户
    product_id INT NOT NULL,                      -- 加入购物车的商品
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_cart_item (user_id, product_id),
                                                   -- 避免同一用户重复添加商品
    
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
                                                   -- 删除用户 → 自动清理购物车
    
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE
                                                   -- 商品被完全删除（极少见）→ 清理购物车
);
```

### 5. 订单表（Orders Table）  

```sql
CREATE TABLE orders (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    
    order_number VARCHAR(32) NOT NULL UNIQUE,      -- 订单号（可用UUID/时间戳生成）
    
    buyer_id INT NOT NULL,                         -- 买家ID
    seller_id INT NOT NULL,                        -- 卖家ID（冗余字段，方便查询“我卖出的”）
    product_id INT NOT NULL,                       -- 商品ID
    
    product_title_snapshot VARCHAR(150),           -- 商品标题快照
    product_image_snapshot VARCHAR(255),           -- 商品封面快照（存单张即可）
    transaction_price DECIMAL(10, 2) NOT NULL,     -- 成交价格
    
    -- 收货信息（创建订单时填写）
    shipping_name VARCHAR(100) NOT NULL,
    shipping_phone VARCHAR(20) NOT NULL,
    shipping_address TEXT NOT NULL,
    
    tracking_number VARCHAR(100),                  -- 卖家发货填写物流单号
    
    payment_method ENUM('alipay', 'wechat') NOT NULL, 
                                                   -- 支付方式（模拟）
    
    status TINYINT NOT NULL DEFAULT 0,
    -- 订单状态：
    -- 0 待发货（买家已付款，卖家需填写单号）
    -- 1 待收货（卖家填了单号）
    -- 2 已完成（买家确认收货）
    -- 3 已取消（超时/未支付/手动取消）
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP 
        ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (buyer_id) REFERENCES users(user_id),
    FOREIGN KEY (seller_id) REFERENCES users(user_id),
    FOREIGN KEY (product_id) REFERENCES products(product_id)
);
```

### 6. 消息表（Messages Table）  

```sql
CREATE TABLE messages (
    message_id INT AUTO_INCREMENT PRIMARY KEY,
    
    sender_id INT NOT NULL,                        -- 发送者ID
    receiver_id INT NOT NULL,                      -- 接收者ID
    
    content TEXT NOT NULL,                         -- 消息内容
    is_read BOOLEAN DEFAULT FALSE,                 -- 是否已读（用于红点提示）
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (sender_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (receiver_id) REFERENCES users(user_id) ON DELETE CASCADE
);
```