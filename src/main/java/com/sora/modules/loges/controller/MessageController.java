package com.sora.modules.loges.controller;

import com.sora.common.auto.AssertParam;
import com.sora.common.auto.LogType;
import com.sora.common.auto.Logback;
import com.sora.common.http.RespBody;
import com.sora.common.spec.SpecBody;
import com.sora.modules.loges.entity.Message;
import com.sora.modules.loges.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/message")
@Logback(module = "短信记录")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @PostMapping("/search")
    @Logback(value = "短信查询", type = LogType.SELECT)
    @AssertParam(value = "start,size", target = SpecBody.class)
    public RespBody search(@RequestBody SpecBody<Message> body) {
        return messageService.search(body);
    }

}
