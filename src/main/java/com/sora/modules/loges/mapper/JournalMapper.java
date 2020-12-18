package com.sora.modules.loges.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sora.modules.loges.entity.Journal;

import java.util.List;

public interface JournalMapper extends BaseMapper<Journal> {

    List<Journal> findAll(Journal journal);

}
