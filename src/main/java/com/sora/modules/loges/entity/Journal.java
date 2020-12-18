package com.sora.modules.loges.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Journal implements Serializable {

    private Long id;

    private Long userId;

    private String module;

    private String ip;

    private String url;

    private String param;

    private String type;

    private String value;

    private String target;

    private Boolean normal;

    private String content;

    private String operator;

    private Date createTime;

}
