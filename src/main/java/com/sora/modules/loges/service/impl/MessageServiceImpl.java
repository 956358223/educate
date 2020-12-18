package com.sora.modules.loges.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;
import com.sora.common.http.RespBody;
import com.sora.common.spec.SpecBody;
import com.sora.common.type.other.SmsType;
import com.sora.modules.loges.entity.Message;
import com.sora.modules.loges.mapper.MessageMapper;
import com.sora.modules.loges.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Override
    public RespBody search(SpecBody<Message> body) {
        PageHelper.startPage(body.getStart(), body.getSize());
        return RespBody.ok(new PageInfo<>(messageMapper.findAll(body.getBody())));
    }

    @Override
    public void append(String data, String signName, String templateCode, String phone, String param, SmsType smsType) {
        Map<String, String> map = new Gson().fromJson(data, Map.class);
        String code = "";
        if (map.get("Code").contains("OK")) code = "OK";
        else code = "NO";
        Message message = Message.builder().phone(phone).bizId(map.get("BizId")).message(map.get("Message")).createTime(new Date()).smsType(smsType)
                .requestId(map.get("RequestId")).code(code).signName(signName).templateCode(templateCode).param(param).build();
        messageMapper.insert(message);
    }
}
