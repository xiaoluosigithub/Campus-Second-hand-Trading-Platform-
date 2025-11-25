package com.lyx.secondhandsystem.controller.admin;

import com.lyx.secondhandsystem.common.ApiResponse;
import com.lyx.secondhandsystem.dto.AdminReviewRequest;
import com.lyx.secondhandsystem.entity.Product;
import com.lyx.secondhandsystem.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

// 管理员审核商品控制器
@RestController
@RequestMapping("/api/admin/products")
public class AdminProductController {
    private final ProductService productService; // 商品服务

    // 构造函数注入商品服务
    public AdminProductController(ProductService productService) {
        this.productService = productService;
    }

    // 审核商品
    @PatchMapping("/{id}/review")
    public ApiResponse<Void> review(@PathVariable Integer id, @RequestBody @Valid AdminReviewRequest req) {
        // 检查商品是否存在
        Product p = productService.getById(id);
        if (p == null) throw new IllegalArgumentException("商品不存在");
        // 检查商品状态是否为待审核 
        if (!Integer.valueOf(0).equals(p.getStatus())) throw new IllegalArgumentException("仅待审核商品可审核");
        // 检查审核状态是否为通过
        if (Boolean.TRUE.equals(req.getApproved())) {
            p.setStatus(Integer.valueOf(1)); // 设置商品状态为已审核
            p.setRejectionReason(null);
        } else {
            if (req.getReason() == null || req.getReason().trim().isEmpty()) {
                throw new IllegalArgumentException("审核不通过必须填写驳回理由");
            }
            p.setStatus(Integer.valueOf(2)); // 设置商品状态为审核拒绝
            p.setRejectionReason(req.getReason());
        }
        // 更新商品状态
        productService.update(p);
        return ApiResponse.ok(null);
    }
}
