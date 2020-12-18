package com.sora.common.type.trade;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PayWays {
    现金(0), 支付宝(1), 微信(2);
    @EnumValue
    private Integer value;
}
