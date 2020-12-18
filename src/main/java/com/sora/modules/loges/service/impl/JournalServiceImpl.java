package com.sora.modules.loges.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sora.common.http.RespBody;
import com.sora.common.spec.SpecBody;
import com.sora.modules.loges.entity.Journal;
import com.sora.modules.loges.mapper.JournalMapper;
import com.sora.modules.loges.service.JournalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Service
public class JournalServiceImpl implements JournalService {

    @Autowired
    private JournalMapper journalMapper;

    @Autowired
    private HttpServletRequest servletRequest;

    @Override
    public RespBody search(SpecBody<Journal> body) {
        PageHelper.startPage(body.getStart(), body.getSize());
        return RespBody.ok(new PageInfo<>(journalMapper.findAll(body.getBody())));
    }

    @Override
    public void append(String value, Long userId, String username, String url, String clazz) {
        Journal journal = Journal.builder().module("系统管理").type("其他").value(value)
                .param("[]").createTime(new Date()).userId(userId).operator(username).url(url)
                .target(clazz).normal(true).content("normal")
                .ip(servletRequest.getRemoteAddr()).build();
        journalMapper.insert(journal);
    }
}
