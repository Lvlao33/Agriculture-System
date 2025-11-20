package com.farmporject.backend.finance.model;

public enum Status {
    CREATED, // 已创建，等待银行审分配负责人
    REVIEWING, // 银行审核中，提交证明资料银行端未进行操作
    APPROVED, // 审核通过，待签约
    REJECTED, // 审核不通过，用户未修改提交资料/直接不通过，建议更换产品或申请联合贷款
    SIGNED, // 已签约，未放款
    REPAYING, // 还款中
    CLEARED_NORMAL, // 正常结清
    CLEARED_EARLY // 提前结清
}
