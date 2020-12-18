package com.sora.common.type.trade;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PayState {
    未缴清(0), 已缴清(1);
    @EnumValue
    private Integer value;
}
