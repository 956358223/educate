package com.sora.common.type.other;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SmsType {
    验证(0), 报名(1), 催缴(2);
    @EnumValue
    private Integer value;
}
