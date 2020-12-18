package com.sora.modules.major.service;

import com.sora.common.http.RespBody;
import com.sora.modules.major.entity.College;

public interface CollegeService {

    RespBody append(College college);

    RespBody modify(College college);

    RespBody delete(Long id);

    RespBody list();

    RespBody search();

    RespBody details(Long pid);

    RespBody cascade(College college);

    RespBody college();

}
