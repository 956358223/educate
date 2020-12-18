package com.sora.common.type.basic;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Subject {
    理科(0), 文科(1), 艺术(2);
    @EnumValue
    private Integer value;
}
