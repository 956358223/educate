package com.sora.common.base;

import com.sora.common.http.RespBody;
import com.sora.common.spec.SpecBody;

import java.util.List;

/**
 * 通用服务层
 *
 * @param <T>
 * @param <ID>
 */
public interface CrudService<T, ID> {

    RespBody append(T t);

    RespBody modify(T t);

    RespBody delete(List<ID> ids);

    RespBody details(ID id);

    RespBody search(SpecBody<T> body);

}
