package com.lyx.secondhandsystem.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lyx.secondhandsystem.common.ApiResponse;
import com.lyx.secondhandsystem.entity.Product;
import com.lyx.secondhandsystem.entity.User;
import com.lyx.secondhandsystem.mapper.UserMapper;
import com.lyx.secondhandsystem.mapper.ShoppingCartMapper;
import com.lyx.secondhandsystem.mapper.OrderMapper;
import com.lyx.secondhandsystem.dto.ProductDetailResponse;
import com.lyx.secondhandsystem.service.ProductService;
import com.lyx.secondhandsystem.dto.ProductCreateRequest;
import com.lyx.secondhandsystem.dto.ProductUpdateRequest;
import com.lyx.secondhandsystem.dto.ProductStatusRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

// 商品控制器，处理商品相关操作
@RestController
@RequestMapping("/api/products")
public class ProductController {
    // 注入商品服务
    private final ProductService productService;
    private final UserMapper userMapper;
    private final ShoppingCartMapper shoppingCartMapper;
    private final OrderMapper orderMapper;
    // 构造函数注入商品服务
    public ProductController(ProductService productService, UserMapper userMapper, ShoppingCartMapper shoppingCartMapper, OrderMapper orderMapper) {
        this.productService = productService;
        this.userMapper = userMapper;
        this.shoppingCartMapper = shoppingCartMapper;
        this.orderMapper = orderMapper;
    }

    // 分页获取商品列表
    @GetMapping
    public ApiResponse<Page<Product>> list(@RequestParam(defaultValue = "1") int page,
                                           @RequestParam(defaultValue = "10") int size,
                                           @RequestParam(required = false) Integer category,
                                           @RequestParam(required = false) Integer status,
                                           @RequestParam(required = false) String keyword,
                                           @RequestParam(required = false) String sort,
                                           @RequestParam(required = false) Boolean excludeActive) 
    {   
        // 校验分页参数
        if (page <= 0) throw new IllegalArgumentException("page必须大于0");
        // 校验每页商品数量参数
        if (size <= 0 || size > 50) throw new IllegalArgumentException("size范围为1-50");
        // 创建分页对象
        Page<Product> p = new Page<>(page, size);
        Page<Product> result = productService.list(p, null, category, status, keyword, sort, excludeActive);
        // 返回分页商品列表
        return ApiResponse.ok(result); 
    }

    // 获取商品详情
    @GetMapping("/{id}")
    public ApiResponse<ProductDetailResponse> detail(@PathVariable Integer id) {
        // 校验商品ID参数
        if (id <= 0) throw new IllegalArgumentException("id必须大于0");
        Product product = productService.getById(id); // 查询商品详情
        if (product == null) throw new IllegalArgumentException("商品不存在");
        // 增加商品访问量
        Integer vc = product.getViewCount() == null ? 0 : product.getViewCount();
        product.setViewCount(vc + 1);
        productService.update(product);
        // 查询卖家信息
        User seller = userMapper.selectById(product.getSellerId());
        // 填充商品详情响应对象
        ProductDetailResponse resp = new ProductDetailResponse();
        resp.setProduct(product);
        resp.setSellerId(product.getSellerId());
        if (seller != null) {
            // 填充卖家信息到响应对象
            resp.setSellerNickname(seller.getNickname());
            resp.setSellerAvatarUrl(seller.getAvatarUrl());
        }
        return ApiResponse.ok(resp); // 返回商品详情响应对象
    }

    // 创建商品
    @PostMapping
    public ApiResponse<Integer> create(@RequestBody @Valid ProductCreateRequest req, HttpSession session) {
        Object uid = session.getAttribute("userId"); // 从会话中获取用户ID
        if (uid == null) throw new IllegalArgumentException("未登录"); // 校验用户是否登录
        Product p = new Product(); // 创建新商品对象
        p.setSellerId((Integer) uid);
        p.setCategoryId(req.getCategoryId());
        p.setTitle(req.getTitle());
        p.setDescription(req.getDescription());
        p.setPrice(req.getPrice());
        p.setImages(req.getImages());
        p.setStatus(0);
        productService.create(p); // 调用商品服务创建商品
        return ApiResponse.ok(p.getProductId()); // 返回创建的商品ID
    }

    // 更新商品
    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Integer id, @RequestBody @Valid ProductUpdateRequest req, HttpSession session) {
        // 校验商品ID参数
        if (id == null || id <= 0) throw new IllegalArgumentException("id必须大于0");
        // 校验用户是否登录
        Object uid = session.getAttribute("userId");
        // 校验用户是否登录
        if (uid == null) throw new IllegalArgumentException("未登录");
        // 校验商品是否存在
        Product db = productService.getById(id);
        if (db == null) throw new IllegalArgumentException("商品不存在");
        // 校验用户是否有权限更新商品
        if (!db.getSellerId().equals((Integer) uid)) throw new IllegalArgumentException("无权操作他人商品");
        if (Integer.valueOf(0).equals(db.getStatus())) throw new IllegalArgumentException("待审核商品不可编辑");
        db.setPrice(req.getPrice());
        db.setDescription(req.getDescription());
        db.setStatus(0);
        db.setRejectionReason(null);
        productService.update(db); // 调用商品服务更新商品
        return ApiResponse.ok(null); // 返回空响应体    
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Integer id, HttpSession session) {
        if (id == null || id <= 0) throw new IllegalArgumentException("id必须大于0");
        Object uid = session.getAttribute("userId");
        if (uid == null) throw new IllegalArgumentException("未登录");
        Product db = productService.getById(id);
        if (db == null) throw new IllegalArgumentException("商品不存在");
        if (!db.getSellerId().equals((Integer) uid)) throw new IllegalArgumentException("无权操作他人商品");
        Integer st = db.getStatus();
        if (!(Integer.valueOf(0).equals(st) || Integer.valueOf(2).equals(st) || Integer.valueOf(4).equals(st))) throw new IllegalArgumentException("仅待审核/审核未通过/已下架可删除");
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.lyx.secondhandsystem.entity.Order> qwo = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        qwo.eq(com.lyx.secondhandsystem.entity.Order::getProductId, id);
        Long oc = orderMapper.selectCount(qwo);
        if (oc != null && oc > 0) throw new IllegalArgumentException("商品存在关联订单，无法删除");
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.lyx.secondhandsystem.entity.ShoppingCart> cw = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        cw.eq(com.lyx.secondhandsystem.entity.ShoppingCart::getProductId, id);
        shoppingCartMapper.delete(cw);
        productService.deleteById(id);
        return ApiResponse.ok(null);
    }

    @PostMapping("/{id}/cancel")
    public ApiResponse<Void> cancel(@PathVariable Integer id, HttpSession session) {
        if (id == null || id <= 0) throw new IllegalArgumentException("id必须大于0");
        Object uid = session.getAttribute("userId");
        if (uid == null) throw new IllegalArgumentException("未登录");
        Product db = productService.getById(id);
        if (db == null) throw new IllegalArgumentException("商品不存在");
        if (!db.getSellerId().equals((Integer) uid)) throw new IllegalArgumentException("无权操作他人商品");
        Integer st = db.getStatus();
        if (Integer.valueOf(1).equals(st)) {
            db.setStatus(4);
            productService.update(db);
            return ApiResponse.ok(null);
        }
        if (Integer.valueOf(0).equals(st) || Integer.valueOf(2).equals(st) || Integer.valueOf(4).equals(st)) {
            com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.lyx.secondhandsystem.entity.Order> qwo = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
            qwo.eq(com.lyx.secondhandsystem.entity.Order::getProductId, id);
            Long oc = orderMapper.selectCount(qwo);
            if (oc != null && oc > 0) throw new IllegalArgumentException("商品存在关联订单，无法取消");
            com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.lyx.secondhandsystem.entity.ShoppingCart> cw = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
            cw.eq(com.lyx.secondhandsystem.entity.ShoppingCart::getProductId, id);
            shoppingCartMapper.delete(cw);
            productService.deleteById(id);
            return ApiResponse.ok(null);
        }
        throw new IllegalArgumentException("不支持的商品状态");
    }

    // 更新商品状态（在售/下架）
    @PatchMapping("/{id}/status")
    public ApiResponse<Void> updateStatus(@PathVariable Integer id, @RequestBody @Valid ProductStatusRequest req, HttpSession session) {
        // 校验商品ID参数
        if (id == null || id <= 0) throw new IllegalArgumentException("id必须大于0");
        // 校验用户是否登录
        Object uid = session.getAttribute("userId");
        // 校验用户是否登录
        if (uid == null) throw new IllegalArgumentException("未登录");
        // 校验商品是否存在
        Product db = productService.getById(id);
        if (db == null) throw new IllegalArgumentException("商品不存在");
        // 校验用户是否有权限更新商品状态   
        if (!db.getSellerId().equals((Integer) uid)) throw new IllegalArgumentException("无权操作他人商品");
        // 校验更新状态参数是否合法
        Integer st = req.getStatus();
        // 校验更新状态参数是否合法
        if (st == null || !(st == 1 || st == 4)) throw new IllegalArgumentException("状态仅支持在售(1)/下架(4)");
        db.setStatus(st); // 更新商品状态
        productService.update(db); // 调用商品服务更新商品
        return ApiResponse.ok(null); // 返回空响应体    
    }

    // 获取当前用户发布的商品列表（分页）
    @GetMapping("/me")
    public ApiResponse<Page<Product>> myProducts(@RequestParam(defaultValue = "1") int page,
                                                 @RequestParam(defaultValue = "10") int size,
                                                 @RequestParam(required = false) Integer status,
                                                 @RequestParam(required = false) String keyword,
                                                 @RequestParam(required = false) String sort,
                                                 HttpSession session) {
        if (size <= 0 || size > 50) throw new IllegalArgumentException("size范围为1-50");
        Object uid = session.getAttribute("userId"); // 从会话中获取用户ID
        if (uid == null) throw new IllegalArgumentException("未登录"); // 校验用户是否登录
        Page<Product> p = new Page<>(page, size); // 创建分页对象
        Page<Product> result = productService.list(p, (Integer) uid, null, status, keyword, sort, false);
        return ApiResponse.ok(result); // 返回分页商品列表  
    }
}
