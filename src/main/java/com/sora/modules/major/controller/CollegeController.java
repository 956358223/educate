package com.sora.modules.major.controller;

import com.sora.common.auto.AssertParam;
import com.sora.common.auto.LogType;
import com.sora.common.auto.Logback;
import com.sora.common.http.RespBody;
import com.sora.modules.major.entity.College;
import com.sora.modules.major.service.CollegeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/college")
@Logback(module = "院系专业")
public class CollegeController {

    @Autowired
    private CollegeService collegeService;

    @PostMapping("/append")
    @Logback(value = "新增数据", type = LogType.INSERT)
    public RespBody append(@RequestBody College college) {
        return collegeService.append(college);
    }

    @DeleteMapping("/delete/{id}")
    @Logback(value = "删除院系", type = LogType.DELETE)
    public RespBody delete(@PathVariable Long id) {
        return collegeService.delete(id);
    }

    @PutMapping("/modify")
    @Logback(value = "修改院系", type = LogType.UPDATE)
    public RespBody modify(@RequestBody College college) {
        return collegeService.modify(college);
    }

    @GetMapping("/search")
    @Logback(value = "所有院系", type = LogType.SELECT)
    public RespBody trees() {
        return collegeService.search();
    }

    @GetMapping("/list")
    @Logback(value = "院系列表", type = LogType.SELECT)
    public RespBody list() {
        return collegeService.list();
    }

    @GetMapping("/details/{id}")
    @Logback(value = "详情查询", type = LogType.SELECT)
    public RespBody details(@PathVariable Long id) {
        return collegeService.details(id);
    }

    @PostMapping("/cascade")
    @AssertParam(value = "id,name", target = College.class)
    @Logback(value = "名称查询", type = LogType.SELECT)
    public RespBody cascade(@RequestBody College college) {
        return collegeService.cascade(college);
    }

    @GetMapping("/college")
    @Logback(value = "名称查询", type = LogType.SELECT)
    public RespBody colleges() {
        return collegeService.college();
    }

}
