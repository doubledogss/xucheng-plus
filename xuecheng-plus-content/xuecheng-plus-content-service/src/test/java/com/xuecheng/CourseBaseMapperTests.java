package com.xuecheng;

import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.model.po.CourseBase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author lianke
 * @version 1.0
 * @description TODO
 * @date 2023/4/11 20:27
 */
@SpringBootTest
public class CourseBaseMapperTests {
    @Autowired
    CourseBaseMapper courseBaseMapper;

    @Test
    public void testCourseBaseMapper() {
        CourseBase courseBase = courseBaseMapper.selectById(18);
        Assertions.assertNotNull(courseBase);
    }
}
