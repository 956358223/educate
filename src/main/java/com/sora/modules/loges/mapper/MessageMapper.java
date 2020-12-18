package com.sora.modules.loges.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sora.modules.loges.entity.Message;

import java.util.List;

public interface MessageMapper extends BaseMapper<Message> {

    List<Message> findAll(Message message);

}
