package com.lyx.secondhandsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lyx.secondhandsystem.entity.Order;
import com.lyx.secondhandsystem.entity.Product;
import com.lyx.secondhandsystem.entity.ShoppingCart;
import com.lyx.secondhandsystem.mapper.OrderMapper;
import com.lyx.secondhandsystem.mapper.ProductMapper;
import com.lyx.secondhandsystem.mapper.ShoppingCartMapper;
import com.lyx.secondhandsystem.service.OrderService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

// 订单服务实现类
@Service
public class OrderServiceImpl implements OrderService {
    private final OrderMapper mapper; // 订单映射器
    private final ProductMapper productMapper;
    private final ShoppingCartMapper shoppingCartMapper;

    public OrderServiceImpl(OrderMapper mapper, ProductMapper productMapper, ShoppingCartMapper shoppingCartMapper) {
        this.mapper = mapper;
        this.productMapper = productMapper;
        this.shoppingCartMapper = shoppingCartMapper;
    }

    // 创建订单
    @Override
    public Order create(Order order) {
        mapper.insert(order);
        return order;
    }

    // 根据ID获取订单   
    @Override
    public Order getById(Integer id) {
        return mapper.selectById(id);
    }

    // 分页获取买家订单
    @Override
    public Page<Order> listByBuyer(Page<Order> page, Integer buyerId) {
        LambdaQueryWrapper<Order> qw = new LambdaQueryWrapper<>();
        qw.eq(Order::getBuyerId, buyerId);
        return mapper.selectPage(page, qw);
    }

    // 分页获取卖家订单
    @Override
    public Page<Order> listBySeller(Page<Order> page, Integer sellerId) {
        LambdaQueryWrapper<Order> qw = new LambdaQueryWrapper<>();
        qw.eq(Order::getSellerId, sellerId);
        return mapper.selectPage(page, qw);
    }

    // 更新订单
    @Override
    public boolean update(Order order) {
        return mapper.updateById(order) > 0;
    }

    // 根据ID删除订单
    @Override
    public boolean deleteById(Integer id) {
        return mapper.deleteById(id) > 0;
    }

    // 下单
    @Override
    @Transactional
    public Order placeSingleOrder(Integer buyerId, Integer productId, String shippingName, String shippingPhone, String shippingAddress, String paymentMethod) {
        Product product = productMapper.selectForUpdate(productId);
        if (product == null) throw new IllegalArgumentException("商品不存在");
        if (!Integer.valueOf(1).equals(product.getStatus())) throw new IllegalArgumentException("商品已失效或非在售");
        if (product.getSellerId().equals(buyerId)) throw new IllegalArgumentException("禁止购买自己发布的商品");
        if (!("alipay".equalsIgnoreCase(paymentMethod) || "wechat".equalsIgnoreCase(paymentMethod))) {
            throw new IllegalArgumentException("支付方式仅支持支付宝或微信");
        }
        // 检查商品是否已被抢先下单（状态为0或1）
        int active = mapper.countActiveOrdersByProduct(productId);
        if (active > 0) throw new IllegalArgumentException("商品已被抢先下单");

        // 创建订单
        Order order = new Order();
        order.setOrderNumber(String.valueOf(System.currentTimeMillis()) + "-" + buyerId);
        order.setBuyerId(buyerId);
        order.setSellerId(product.getSellerId());
        order.setProductId(productId);
        order.setProductTitleSnapshot(product.getTitle());
        order.setProductImageSnapshot(product.getImages() != null && !product.getImages().isEmpty() ? product.getImages().get(0) : null);
        order.setTransactionPrice(product.getPrice());
        order.setShippingName(shippingName);
        order.setShippingPhone(shippingPhone);
        order.setShippingAddress(shippingAddress);
        order.setPaymentMethod(paymentMethod);
        order.setStatus(0);
        mapper.insert(order); // 插入订单

        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ShoppingCart> cw = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        cw.eq(ShoppingCart::getProductId, productId); // 查询购物车中该商品
        shoppingCartMapper.delete(cw); // 删除购物车中该商品

        // 返回订单
        return order;
    }

    // 取消订单
    @Override
    @Transactional
    public boolean cancelOrder(Integer buyerId, Integer orderId) {
        // 检查订单是否存在、是否为买家、是否为待发货状态
        Order order = mapper.selectById(orderId);
        if (order == null) throw new IllegalArgumentException("订单不存在");
        if (!order.getBuyerId().equals(buyerId)) throw new IllegalArgumentException("无权操作他人订单");
        if (!Integer.valueOf(0).equals(order.getStatus())) throw new IllegalArgumentException("仅待发货订单可取消");

        // 更新订单状态为已取消（3）
        order.setStatus(3);
        mapper.updateById(order);
        // 返回取消成功
        return true;
    }

    // 发货
    @Override
    @Transactional
    public boolean shipOrder(Integer sellerId, Integer orderId, String trackingNumber) {
        // 检查订单是否存在、是否为卖家、是否为待发货状态
        Order order = mapper.selectById(orderId);
        if (order == null) throw new IllegalArgumentException("订单不存在");
        if (!order.getSellerId().equals(sellerId)) throw new IllegalArgumentException("无权操作他人订单");
        if (!Integer.valueOf(0).equals(order.getStatus())) throw new IllegalArgumentException("仅待发货订单可发货");
        // 更新订单状态为已发货（1），并设置物流单号
        order.setTrackingNumber(trackingNumber);
        order.setStatus(1);
        // 更新订单
        return mapper.updateById(order) > 0;
    }

    // 确认收货
    @Override
    @Transactional
    public boolean confirmOrder(Integer buyerId, Integer orderId) {
        // 检查订单是否存在、是否为买家、是否为待收货状态
        Order order = mapper.selectById(orderId);
        if (order == null) throw new IllegalArgumentException("订单不存在");
        if (!order.getBuyerId().equals(buyerId)) throw new IllegalArgumentException("无权操作他人订单");
        if (!Integer.valueOf(1).equals(order.getStatus())) throw new IllegalArgumentException("仅待收货订单可确认");
        // 更新订单状态为已收货（2）
        order.setStatus(2);
        boolean ok = mapper.updateById(order) > 0;
        if (ok) {
            com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<Product> uw = new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<>();
            uw.eq(Product::getProductId, order.getProductId()).set(Product::getStatus, 3);
            productMapper.update(null, uw);
        }
        return ok;
    }

    @Override
    public boolean precheckProduct(Integer productId) {
        Product p = productMapper.selectById(productId);
        if (p == null) throw new IllegalArgumentException("商品不存在");
        return Integer.valueOf(1).equals(p.getStatus()) && mapper.countActiveOrdersByProduct(productId) == 0;
    }
}
