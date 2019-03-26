package com.template.dal.base;

import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.session.RowBounds;

import java.util.List;
import java.util.Map;

/**
 * @author zhuangwj
 * @since 19-3-26
 */
public interface BaseMapper<T> extends com.baomidou.mybatisplus.mapper.BaseMapper {

    List<Integer> selectIdPage(@Param("cm") Map<String, Object> params);

    List<Integer> selectIdPage(RowBounds rowBounds, @Param("cm") Map<String, Object> params);

    List<T> selectRecords(Pagination rowBounds, T param);

    List<T> selectRecords(T param);

    List<T> selectRecords(Map<String, Object> param);

    List<T> selectRecords(Pagination rowBounds, Map<String, Object> param);

    @SelectProvider(type = SQLTemplate.class, method = "selectIds")
    <O> List<T> selectByNIds(@Param("list") List<O> list, @Param("object") T object, @Param("nColName") String nColName);

    @InsertProvider(type = SQLTemplate.class, method = "insertAll")
    Integer insertAll(@Param("list") List<T> list);
}
