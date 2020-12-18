package com.sora.modules.loges.service;

import com.sora.common.http.RespBody;
import com.sora.common.spec.SpecBody;
import com.sora.modules.loges.entity.Journal;

public interface JournalService {

    RespBody search(SpecBody<Journal> body);

    void append(String value, Long userId, String username, String url, String clazz);

}
