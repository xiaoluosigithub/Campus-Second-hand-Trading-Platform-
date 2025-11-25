package com.lyx.secondhandsystem.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lyx.secondhandsystem.common.ApiResponse;
import com.lyx.secondhandsystem.dto.CartAddRequest;
import com.lyx.secondhandsystem.dto.CartItemResponse;
import com.lyx.secondhandsystem.entity.Product;
import com.lyx.secondhandsystem.entity.ShoppingCart;
import com.lyx.secondhandsystem.mapper.ProductMapper;
import com.lyx.secondhandsystem.mapper.ShoppingCartMapper;
import com.lyx.secondhandsystem.service.ShoppingCartService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

// 购物车控制器
@RestController
@RequestMapping("/api/cart")
public class CartController {
    private final ShoppingCartService cartService; // 购物车服务
    private final ShoppingCartMapper cartMapper; // 购物车Mapper
    private final ProductMapper productMapper; // 商品Mapper    

    // 构造函数
    public CartController(ShoppingCartService cartService, ShoppingCartMapper cartMapper, ProductMapper productMapper) {
        this.cartService = cartService; // 购物车服务
        this.cartMapper = cartMapper; // 购物车Mapper
        this.productMapper = productMapper; // 商品Mapper
    }

    // 添加商品到购物车
    @PostMapping
    public ApiResponse<Void> add(@RequestBody @Valid CartAddRequest req, HttpSession session) {
        // 检查用户是否登录
        Object uid = session.getAttribute("userId");
        if (uid == null) throw new IllegalArgumentException("未登录");
        // 检查商品是否存在
        Product p = productMapper.selectById(req.getProductId());
        if (p == null) throw new IllegalArgumentException("商品不存在");
        // 检查购物车是否已存在该商品
        LambdaQueryWrapper<ShoppingCart> qw = new LambdaQueryWrapper<>();
        qw.eq(ShoppingCart::getUserId, (Integer) uid).eq(ShoppingCart::getProductId, req.getProductId());
        ShoppingCart exists = cartMapper.selectOne(qw);
        if (exists == null) {
            // 购物车不存在该商品，创建新项
            ShoppingCart sc = new ShoppingCart();
            sc.setUserId((Integer) uid);
            sc.setProductId(req.getProductId());
            try {
                cartMapper.insert(sc);
            } catch (org.springframework.dao.DuplicateKeyException e) {
            }
        }
        return ApiResponse.ok(null); // 添加成功
    }

    // 获取用户购物车列表
    @GetMapping
    public ApiResponse<Page<ShoppingCart>> list(@RequestParam(defaultValue = "1") int page,
                                                @RequestParam(defaultValue = "10") int size,
                                                HttpSession session) 
    {
        // 检查用户是否登录
        Object uid = session.getAttribute("userId");
        if (uid == null) throw new IllegalArgumentException("未登录");
        // 检查分页参数是否合法
        if (page <= 0 || size <= 0 || size > 50) throw new IllegalArgumentException("分页参数不合法");
        // 分页查询购物车项
        Page<ShoppingCart> p = new Page<>(page, size);
        Page<ShoppingCart> result = cartService.listByUser(p, (Integer) uid);
        return ApiResponse.ok(result); // 返回购物车项列表
    }

    @GetMapping("/items")
    public ApiResponse<Page<CartItemResponse>> items(@RequestParam(defaultValue = "1") int page,
                                                     @RequestParam(defaultValue = "10") int size,
                                                     HttpSession session) {
        Object uid = session.getAttribute("userId");
        if (uid == null) throw new IllegalArgumentException("未登录");
        if (page <= 0 || size <= 0 || size > 50) throw new IllegalArgumentException("分页参数不合法");
        Page<ShoppingCart> p = new Page<>(page, size);
        Page<ShoppingCart> carts = cartService.listByUser(p, (Integer) uid);
        Page<CartItemResponse> out = new Page<>(carts.getCurrent(), carts.getSize(), carts.getTotal());
        java.util.List<CartItemResponse> records = new java.util.ArrayList<>();
        for (ShoppingCart item : carts.getRecords()) {
            Product pdt = productMapper.selectById(item.getProductId());
            CartItemResponse r = new CartItemResponse();
            r.setProductId(item.getProductId());
            if (pdt != null) {
                r.setTitle(pdt.getTitle());
                r.setPrice(pdt.getPrice());
                r.setStatus(pdt.getStatus());
                java.util.List<String> imgs = pdt.getImages();
                r.setImage(imgs != null && !imgs.isEmpty() ? imgs.get(0) : null);
            } else {
                r.setStatus(null);
            }
            r.setAddedAt(item.getCreatedAt());
            records.add(r);
        }
        out.setRecords(records);
        return ApiResponse.ok(out);
    }

    // 从购物车移除商品
    @DeleteMapping("/{productId}")
    public ApiResponse<Void> remove(@PathVariable Integer productId, HttpSession session) {
        // 检查用户是否登录
        Object uid = session.getAttribute("userId");
        if (uid == null) throw new IllegalArgumentException("未登录");
        // 检查购物车是否存在该商品
        LambdaQueryWrapper<ShoppingCart> qw = new LambdaQueryWrapper<>();
        qw.eq(ShoppingCart::getUserId, (Integer) uid).eq(ShoppingCart::getProductId, productId);
        cartMapper.delete(qw); // 删除购物车项
        return ApiResponse.ok(null); // 删除成功
    }

    // 删除用户购物车中的无效商品
    @PostMapping("/cleanup")
    public ApiResponse<Integer> cleanup(HttpSession session) {
        // 检查用户是否登录
        Object uid = session.getAttribute("userId");
        if (uid == null) throw new IllegalArgumentException("未登录");
        int removed = 0;// 记录删除的购物车项数量
        LambdaQueryWrapper<ShoppingCart> qw = new LambdaQueryWrapper<>();
        qw.eq(ShoppingCart::getUserId, (Integer) uid);
        // 遍历购物车项，删除无效商品
        for (ShoppingCart item : cartMapper.selectList(qw)) {
            Product p = productMapper.selectById(item.getProductId());
            // 检查商品是否存在或已下架
            if (p == null || !Integer.valueOf(1).equals(p.getStatus())) {
                // 商品不存在或已下架，删除购物车项
                cartMapper.delete(new LambdaQueryWrapper<ShoppingCart>()
                        .eq(ShoppingCart::getUserId, (Integer) uid)
                        .eq(ShoppingCart::getProductId, item.getProductId()));
                removed++;
            }
        }
        // 返回删除的购物车项数量
        return ApiResponse.ok(removed);
    }
}
