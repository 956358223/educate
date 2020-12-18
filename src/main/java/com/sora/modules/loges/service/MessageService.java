package com.sora.modules.loges.service;

import com.sora.common.http.RespBody;
import com.sora.common.spec.SpecBody;
import com.sora.common.type.other.SmsType;
import com.sora.modules.loges.entity.Message;

public interface MessageService {

    RespBody search(SpecBody<Message> body);

    void append(String data, String signName, String templateCode, String phone, String param, SmsType smsType);

}
