package com.xuecheng.content.service;

import com.xuecheng.base.execption.ValidationGroups;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.EditCourseDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;


/**
 * <p>
 * 课程基本信息 服务类
 * </p>
 *
 * @author lianke
 * @since 2023-04-11
 */
public interface CourseBaseInfoService {
    /***
     * @description 课程分页查询
     * @param pageParams 分页参数
     * @param queryCourseParams 查询条件
     * @return com.xuecheng.base.model.PageResult<com.xuecheng.content.model.po.CourseBase>
     * @author lianke
     * @date 2023/4/13 15:22
     */
    PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto queryCourseParams);

    //新增课程基础信息
    CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto addCourseDto);

    //根据课程id查询课程基础信息
    CourseBaseInfoDto getCourseBaseInfo(Long courseId);

    /***
     * @description 修改课程信息
     * @param companyId 机构id
     * @param editCourseDto 课程信息
     * @return com.xuecheng.content.model.dto.CourseBaseInfoDto
     * @author lianke
     * @date 2023/4/18 11:33
    */
    CourseBaseInfoDto updateCourseInfo(Long companyId,EditCourseDto editCourseDto);


}