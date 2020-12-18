package com.sora.modules.loges.entity;

import com.sora.common.type.other.SmsType;
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
public class Message implements Serializable {

    private Long id;

    private String signName;

    private String templateCode;

    private String phone;

    private String requestId;

    private String bizId;

    private String code;

    private String message;

    private SmsType smsType;

    private String param;

    private Date createTime;

}
