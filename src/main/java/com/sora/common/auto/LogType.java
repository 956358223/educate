package com.sora.common.auto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LogType {

    INSERT("新增"), DELETE("删除"), UPDATE("修改"), SELECT("查询"), OTHERS("其他"), SIGNIN("登录"), LOGOUT("退出");

    private String name;

    public static String n = "";

}
