package com.template.domain.base;

import com.baomidou.mybatisplus.plugins.Page;
import com.template.dal.base.BaseModel;
import com.template.dal.core.exception.TemplateException;

import java.util.List;
import java.util.Map;

/**
 * @author zhuangwj
 * @since 2019/3/5
 */
public interface IBaseService<T extends BaseModel> {
    /**
     * 批量添加元素
     * @param records
     * @return
     * @throws TemplateException
     */
    boolean addAll(List<T> records) throws TemplateException;

    /**
     * 逻辑删除
     * @param id
     * @throws TemplateException
     */
    void logicDel(Integer id) throws TemplateException;

    /**
     * 逻辑删除
     * @param model
     * @throws TemplateException
     */
    void logicDel(T model) throws TemplateException;

    /**
     * 真实删除
     * @param id
     */
    void delete(Integer id);

    /**
     * 真实删除
     * @param model
     */
    void delete(T model);

    /**
     * 真实删除
     * @param models
     */
    void delete(List<T> models);

    /**
     * 更新数据
     * @param record
     * @return
     * @throws TemplateException
     */
    T update(T record) throws TemplateException;

    /**
     * 增加数据
     * @param record
     * @return
     * @throws TemplateException
     */
    boolean add(T record) throws TemplateException;

    /**
     * 单条数据
     * @param id
     * @return
     */
    T queryById(Integer id);

    /**
     * 根据Id列表获取数据
     * @param ids
     * @return
     */
    List<T> queryByIds(List<Integer> ids);

    /**
     * 根据相应列列表获取数据
     * @param colIds
     * @param normalColName
     * @param param 筛选条件
     * @return
     */
    <O> List<T> queryByNIds(List<O> colIds, String normalColName, T param);

    /**
     * 根据相应列列表获取数据
     * @param colIds 常用数据类型int,long,string
     * @param normalColName 普通列名
     * @return
     */
    <O> List<T> queryByNIds(List<O> colIds, String normalColName);

    /**
     * 列表筛选
     * @param params
     * @return
     */
    List<T> queryList(Map<String, Object> params);

    /**
     * 搜索
     * @param param
     * @return
     */
    List<T> queryListByFieldsOrKeyword(T param);

    /**
     * 翻页筛选
     * @param param
     * @return
     */
    Page<T> queryPage(Map<String, Object> param);

    /**
     * 翻页搜索
     * 根据实体对象字段或者关键字查询带分页列表
     * keyword为关键字, fieds为需要用关键字查询的数据库字段名
     *
     * */
    Page<T> queryPageByFieldsOrKeyword(Integer pageNo, Integer pageSize, T param);

    /**
     * 分页查询结构
     * @param params
     * @param <K>
     * @return
     */
    <K> Page<K> getPage(Map<String, Object> params);

    <K> Page<K> getPage(Integer pageNo, Integer pageSize, String orderBy, Boolean asc);

    /**
     * 根据部分信息查询
     * @param param
     * @return
     */
    T getByOne(T param);

    /**
     * 查重
     * @param record
     * @param excludeId
     * @return
     * @throws TemplateException
     */
    boolean checkIfExist(T record, boolean excludeId) throws TemplateException;

}
