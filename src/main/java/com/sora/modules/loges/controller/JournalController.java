package com.sora.modules.loges.controller;

import com.sora.common.auto.AssertParam;
import com.sora.common.http.RespBody;
import com.sora.common.spec.SpecBody;
import com.sora.modules.loges.entity.Journal;
import com.sora.modules.loges.service.JournalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/journal")
public class JournalController {

    @Autowired
    private JournalService journalService;

    @PostMapping("/search")
    @AssertParam(value = "start,size", target = SpecBody.class)
    public RespBody search(@RequestBody SpecBody<Journal> body) {
        return journalService.search(body);
    }

}
