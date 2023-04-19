package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuecheng.base.execption.XueChengPlusException;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.mapper.CourseMarketMapper;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.EditCourseDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.mapper.CourseBaseMapper;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuecheng.content.model.po.CourseCategory;
import com.xuecheng.content.model.po.CourseMarket;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import com.xuecheng.content.service.CourseBaseInfoService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 课程基本信息 服务实现类
 * </p>
 *
 * @author itcast
 */
@Slf4j
@Service
public class CourseBaseInfoServiceImpl implements CourseBaseInfoService{

    @Autowired
    private CourseBaseMapper courseBaseMapper;
    @Autowired
    private CourseMarketMapper courseMarketMapper;
    @Autowired
    CourseCategoryMapper courseCategoryMapper;
    @Autowired
    CourseMarketServiceImpl courseMarketService;

    @Override
    public PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto queryCourseParams) {

        //构建查询条件对象
        LambdaQueryWrapper<CourseBase> queryWrapper = new LambdaQueryWrapper<>();
        //课程名称
        queryWrapper.like(StringUtils.isNotEmpty(queryCourseParams.getCourseName()),CourseBase::getName,queryCourseParams.getCourseName());
        //审核状态
        queryWrapper.eq(StringUtils.isNotEmpty(queryCourseParams.getAuditStatus()),CourseBase::getAuditStatus,queryCourseParams.getAuditStatus());
        //发布状态
        queryWrapper.eq(StringUtils.isNotEmpty(queryCourseParams.getPublishStatus()),CourseBase::getStatus,queryCourseParams.getPublishStatus());
        //分页对象
        Page<CourseBase> courseBasePage = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        Page<CourseBase> coursePageResult = courseBaseMapper.selectPage(courseBasePage, queryWrapper);
        List<CourseBase> item = coursePageResult.getRecords();
        long total = courseBasePage.getTotal();
        PageResult<CourseBase> pageResult = new PageResult<>(item, total, pageParams.getPageNo(), pageParams.getPageSize());
        return pageResult;
    }

    /***
     * @description 添加课程基本信息
     * @param companyId  教学机构id
     * @param dto 课程基本信息
     * @return com.xuecheng.content.model.dto.CourseBaseInfoDto
     * @author lianke
     * @date 2023/4/17 17:24
    */
    @Transactional
    @Override
    public CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto dto) {

        String charge=dto.getCharge();

        if ("201001".equals(charge)) {
            BigDecimal price = dto.getPrice();
            if (ObjectUtils.isEmpty(price)||price.floatValue()<=0) {
                throw new XueChengPlusException("收费课程价格不能为空且必须大于0");
            }
        }

        //向课程基本信息表course_base写入数据
        CourseBase courseBase = new CourseBase();
        BeanUtils.copyProperties(dto,courseBase);
        courseBase.setCompanyId(companyId);
        courseBase.setCreateDate(LocalDateTime.now());
        courseBase.setAuditStatus("202002"); //审核状态默认未提交
        courseBase.setStatus("203001"); //发布状态默认为未发布
        int insert = courseBaseMapper.insert(courseBase);
        Long courseId = courseBase.getId();

        //向课程基本营销表course_market写入数据
        CourseMarket courseMarket = new CourseMarket();
        BeanUtils.copyProperties(dto,courseMarket);
        courseMarket.setId(courseId);
        int insert1 = saveCourseMarket(courseMarket);

        if(insert<1 || insert1<1){
            log.error("创建课程过程中出错:{}",dto);
            throw new XueChengPlusException("创建课程过程中出错");
        }
        return getCourseBaseInfo(courseId);
    }


    //抽取对营销的保存
    private int saveCourseMarket(CourseMarket courseMarket){
        String charge = courseMarket.getCharge();
        if(StringUtils.isBlank(charge)){
            XueChengPlusException.cast("收费规则没有选择");
        }
        if(charge.equals("201001")){
            if(courseMarket.getPrice()==null || courseMarket.getPrice().floatValue()<=0){
                XueChengPlusException.cast("课程为收费价格不能为空且必须大于0");
            }
        }
        //保存
        boolean b = courseMarketService.saveOrUpdate(courseMarket);
        return b?1:0;
    }

    /**
     * @description 根据课程id查询课程信息，包括基本信息和营销信息
     * @param courseId
     * @return com.xuecheng.content.model.dto.CourseBaseInfoDto
     * @author Mr.M
     * @date 2022/10/8 16:10
     */
    public CourseBaseInfoDto getCourseBaseInfo(Long courseId){
        //课程基本信息
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        //课程营销信息
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        //组成要返回的数据
        CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();
        BeanUtils.copyProperties(courseBase,courseBaseInfoDto);
        if(courseMarket!=null){
            BeanUtils.copyProperties(courseMarket,courseBaseInfoDto);
        }

        //向分类的名称查询出来
        CourseCategory courseCategory = courseCategoryMapper.selectById(courseBase.getMt());//一级分类
        courseBaseInfoDto.setMtName(courseCategory.getName());
        CourseCategory courseCategory2 = courseCategoryMapper.selectById(courseBase.getSt());//二级分类
        courseBaseInfoDto.setStName(courseCategory2.getName());

        return courseBaseInfoDto;
    }


    @Transactional
    @Override
    public CourseBaseInfoDto updateCourseInfo(Long companyId,EditCourseDto dto) {
        //机构校验
        Long courseId = dto.getId();
        CourseBase courseBaseUpdate = courseBaseMapper.selectById(courseId);
        if (!companyId.equals(courseBaseUpdate.getCompanyId())) {
            throw new XueChengPlusException("只允许修改本机构的课程!");
        }

        //更新课程基本信息
        BeanUtils.copyProperties(dto,courseBaseUpdate);
        courseBaseUpdate.setChangeDate(LocalDateTime.now());
        int update = courseBaseMapper.updateById(courseBaseUpdate);

        CourseBaseInfoDto courseBaseInfo = getCourseBaseInfo(courseId);

        //更新课程营销信息
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);

        BeanUtils.copyProperties(dto,courseMarket);
        courseMarket.setId(companyId);
        int i = saveCourseMarket(courseMarket);
        if (update<=0||i<=0){
            XueChengPlusException.cast("修改课程失败");
        }

        return getCourseBaseInfo(courseId);
    }




}
