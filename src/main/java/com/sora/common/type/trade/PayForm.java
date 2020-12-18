package com.sora.common.type.trade;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PayForm {
    定金(0), 余款(1), 全款(2);
    @EnumValue
    private Integer value;
}
