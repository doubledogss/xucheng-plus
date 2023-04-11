package com.xuecheng.content.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author lianke
 * @version 1.0
 * @description 课程查询参数Dto
 * @date 2023/4/11 11:33
 */
@Data
@ToString
public class QueryCourseParamsDto {
    @ApiModelProperty("审核状态")
    private String auditStatus;
    @ApiModelProperty("课程名称")
    private String courseName;
    @ApiModelProperty("发布状态")
    private String publishStatus;
}
