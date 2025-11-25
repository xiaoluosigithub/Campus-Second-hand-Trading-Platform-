package com.lyx.secondhandsystem.dto;

import jakarta.validation.constraints.NotNull;

// 管理员审核请求DTO
public class AdminReviewRequest {
    @NotNull
    private Boolean approved; // 是否审核通过
    private String reason; // 审核拒绝原因  

    public Boolean getApproved() { return approved; } // 获取是否审核通过
    public void setApproved(Boolean approved) { this.approved = approved; } // 设置是否审核通过
    public String getReason() { return reason; } // 获取审核拒绝原因
    public void setReason(String reason) { this.reason = reason; } // 设置审核拒绝原因
}
