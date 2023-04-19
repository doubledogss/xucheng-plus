package com.xuecheng.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.Teachplan;

import java.util.List;

/**
 * <p>
 * 课程计划 Mapper 接口
 * </p>
 *
 * @author itcast
 */
public interface TeachplanMapper extends BaseMapper<Teachplan> {

    /***
     * @description 查询某课程的课程计划，组成树型结构
     * @param courseId 
     * @return java.util.List<com.xuecheng.content.model.dto.TeachplanDto>
     * @author lianke
     * @date 2023/4/18 14:34
    */
    List<TeachplanDto> selectTreeNodes(long courseId);
}
