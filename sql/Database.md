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