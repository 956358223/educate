package com.sora.modules.major.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.sora.common.http.RespBody;
import com.sora.common.http.RespState;
import com.sora.modules.major.entity.College;
import com.sora.modules.major.mapper.CollegeMapper;
import com.sora.modules.major.service.CollegeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CollegeServiceImpl implements CollegeService {

    @Autowired
    private CollegeMapper collegeMapper;

    @Override
    public RespBody append(College college) {
        if (college.getPid() == null || college.getPid().equals(0)) {
            college.setPid(0L);
            college.setLevel(String.valueOf(0));
        } else {
            College entity = collegeMapper.selectById(college.getPid());
            if (entity != null) college.setLevel(entity.getLevel() + "," + college.getPid());
        }
        college.setCreateTime(new Date());
        return collegeMapper.insert(college) > 0 ? RespBody.ok() : RespBody.body(RespState.FAILURE);
    }

    @Override
    public RespBody modify(College college) {
        college.setModifyTime(new Date());
        if (collegeMapper.selectById(college.getId()) == null) return RespBody.body(RespState.SELECT_FAILURE);
        return collegeMapper.updateById(college) > 0 ? RespBody.ok() : RespBody.body(RespState.INSERT_FAILURE);
    }

    @Override
    public RespBody delete(Long id) {
        if (collegeMapper.selectList(Wrappers.<College>lambdaQuery().eq(College::getPid, id)).size() > 0)
            return RespBody.body(RespState.RELATE_EXISTS);
        return collegeMapper.deleteById(id) > 0 ? RespBody.ok() : RespBody.no();
    }

    @Override
    public RespBody list() {
        return RespBody.ok(collegeMapper.selectList(null));
    }

    @Override
    public RespBody search() {
        List<College> colleges = collegeMapper.selectList(null);
        List<College> collect = colleges.stream().filter(x -> x.getPid().equals(0L)).map(x -> convert(x, colleges)).collect(Collectors.toList());
        return RespBody.ok(collect);
    }

    @Override
    public RespBody details(Long pid) {
        College college = collegeMapper.selectById(pid);
        college.setChildren(null);
        return RespBody.ok(college);
    }

    @Override
    public RespBody cascade(College college) {
        College entity = collegeMapper.selectOne(Wrappers.<College>lambdaQuery().eq(College::getId, college.getId()).eq(College::getName, college.getName()));
        return entity != null ? RespBody.ok(entity.getChildren()) : RespBody.ok(Collections.emptyList());
    }

    @Override
    public RespBody college() {
        return RespBody.ok(collegeMapper.selectList(Wrappers.<College>lambdaQuery().eq(College::getType, 0)));
    }

    private College convert(College college, List<College> colleges) {
        College node = new College();
        BeanUtils.copyProperties(college, node);
        List<College> children = colleges.stream().filter(x -> x.getPid().equals(college.getId())).map(x -> convert(x, colleges)).collect(Collectors.toList());
        node.setChildren(children);
        return node;
    }

}
