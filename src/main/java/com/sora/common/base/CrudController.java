package com.sora.common.base;

import com.sora.common.auto.AssertParam;
import com.sora.common.auto.LogType;
import com.sora.common.auto.Logback;
import com.sora.common.http.RespBody;
import com.sora.common.spec.SpecBody;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;

/**
 * 通用控制
 *
 * @param <T>
 * @param <ID>
 */
public abstract class CrudController<T, ID extends Serializable> {

    public abstract CrudService getCrudService();

    @PostMapping("/append")
    @Logback(value = "新增数据", type = LogType.INSERT)
    protected RespBody append(@RequestBody T t) {
        return getCrudService().append(t);
    }

    @DeleteMapping("/delete")
    @Logback(value = "删除数据", type = LogType.DELETE)
    protected RespBody delete(@RequestBody List<ID> ids) {
        return getCrudService().delete(ids);
    }

    @PutMapping("/modify")
    @Logback(value = "修改数据", type = LogType.UPDATE)
    protected RespBody modify(@RequestBody T t) {
        return getCrudService().modify(t);
    }

    @GetMapping("/details/{id}")
    @Logback(value = "数据详情", type = LogType.SELECT)
    protected RespBody details(@PathVariable ID id) {
        return getCrudService().details(id);
    }

    @PostMapping("/search")
    @Logback(value = "搜索数据", type = LogType.SELECT)
    @AssertParam(value = "start,size", target = SpecBody.class)
    protected RespBody search(@RequestBody SpecBody<T> entity) {
        return getCrudService().search(entity);
    }

}
