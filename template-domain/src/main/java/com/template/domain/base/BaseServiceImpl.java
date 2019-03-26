package com.template.domain.base;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.esotericsoftware.reflectasm.MethodAccess;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.sun.org.apache.xpath.internal.operations.Bool;
import com.template.dal.annotation.CheckIfExist;
import com.template.dal.annotation.UnCheckSuperClass;
import com.template.dal.base.BaseMapper;
import com.template.dal.base.BaseModel;
import com.template.dal.core.exception.TemplateCommonExtCodeEnum;
import com.template.dal.core.exception.TemplateException;
import com.template.dal.util.DataUtil;
import com.template.dal.util.StringUtils;
import com.template.dal.util.collection.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.crypto.Data;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.template.dal.util.StringUtils.camelToUnderline;

/**
 * @author zhuangwj
 * @since 19-3-25
 */
public class BaseServiceImpl<M extends BaseMapper<T>, T extends BaseModel> implements IBaseService<T> {
    protected final Log logger = LogFactory.getLog(getClass());

    @Autowired
    protected M baseMapper;

    @Override
    @Transactional(rollbackFor = Throwable.class, timeout = 60)
    public boolean addAll(List<T> records) throws TemplateException {
        if (CollectionUtils.isEmpty(records)) {
            return true;
        }
        records.forEach(this::completeRecord);
        baseMapper.insertAll(records);
        return false;
    }

    private void completeRecord(T record) throws TemplateException {
        Integer userId = getCurrUser();
        if (record.getCreateBy() == null) {
            record.setCreateBy(userId);
        }
        if (record.getUpdateBy() == null) {
            record.setUpdateBy(userId);
        }
        Date currentDateTime = new Date();
        if (record.getCreateTime() == null) {
            record.setCreateTime(currentDateTime);
        }
        if (record.getUpdateTime() == null) {
            record.setUpdateTime(currentDateTime);
        }
        if (record.getDelFlg() == null) {
            record.setDelFlg(1);
        }
    }

    @Override
    public void logicDel(Integer id) throws TemplateException {
        try {
            BaseModel record = this.queryById(id);
            Integer userId = getCurrUser();
            record.setUpdateBy(userId);
            record.setUpdateTime(new Date());
            record.setDelFlg(0);
            baseMapper.updateById((T) record);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new TemplateException(TemplateCommonExtCodeEnum.DB_DELETE_ERROR);
        }

    }

    @Override
    public void logicDel(T record) throws TemplateException {
        try {
            Integer userId = getCurrUser();
            record.setUpdateBy(userId);
            record.setUpdateTime(new Date());
            record.setDelFlg(0);
            baseMapper.updateById(record);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new TemplateException(TemplateCommonExtCodeEnum.DB_DELETE_ERROR);
        }
    }

    @Override
    public void delete(Integer id) {
        try {
            baseMapper.deleteById(id);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public void delete(T record) {
        try {
            BaseModel m = (BaseModel) baseMapper.selectOne(record);
            baseMapper.deleteById(m.getId());
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class, timeout = 60)
    public void delete(List<T> models) {
        try {
            for (int i = 0; i < models.size(); i++) {
                if (!DataUtil.isEmpty(models)) {
                    T model = models.get(i);
                    BaseModel m = (BaseModel) baseMapper.selectOne(model);
                    baseMapper.deleteById(m.getId());
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public T update(T record) throws TemplateException {
        try {
            Integer userId = getCurrUser();
            record.setUpdateBy(userId);
            record.setUpdateTime(new Date());
            baseMapper.updateById(record);
        } catch (DuplicateKeyException e) {
            logger.error(e.getMessage());
            throw new TemplateException(TemplateCommonExtCodeEnum.DUPLICATE_KEY_ERROR);
        } catch(Exception e) {
            logger.error(e.getMessage(), e);
            throw new TemplateException(TemplateCommonExtCodeEnum.SYS_TOO_BUSY);
        }
        return record;
    }

    @Override
    public boolean add(T record) throws TemplateException {
        try {
            Integer userId = getCurrUser();
            record.setCreateBy(userId);
            record.setUpdateBy(userId);
            record.setCreateTime(new Date());
            record.setUpdateTime(new Date());
            record.setDelFlg(1);
            baseMapper.insert(record);
        } catch (DuplicateKeyException e) {
            logger.error(e.getMessage());
            throw new TemplateException(TemplateCommonExtCodeEnum.DUPLICATE_KEY_ERROR);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new TemplateException(TemplateCommonExtCodeEnum.SYS_TOO_BUSY);
        }
        return true;
    }

    @Override
    public T queryById(Integer id) {
        try {
            T record = (T) baseMapper.selectById(id);
            return record;
        } catch(Exception e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public List<T> queryByIds(List<Integer> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.EMPTY_LIST;
        }
        return baseMapper.selectBatchIds(ids);
    }

    @Override
    public <O> List<T> queryByNIds(List<O> colIds, String normalColName, T param) {
        if (CollectionUtils.isEmpty(colIds)) {
            return Collections.EMPTY_LIST;
        }
        return baseMapper.selectByNIds(colIds, param, normalColName);
    }

    @Override
    public <O> List<T> queryByNIds(List<O> colIds, String normalColName) {
        Type genType = getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        Class<T> tClass = (Class<T>) params[1];

        try {
            return queryByNIds(colIds, normalColName, tClass.newInstance());
        } catch (Exception e) {
            logger.error(e);
            throw new RuntimeException("获取列表数据失败");
        }
    }

    @Override
    public List<T> queryList(Map<String, Object> params) {
        return baseMapper.selectRecords(params);
    }

    @Override
    public List<T> queryListByFieldsOrKeyword(T param) {
        return baseMapper.selectRecords(param);
    }

    @Override
    public Page<T> queryPage(Map<String, Object> param) {
        Page<T> page = getPage(param);
        List<T> records = baseMapper.selectRecords(page, param);
        page.setRecords(records);
        return page;
    }

    @Override
    public Page<T> queryPageByFieldsOrKeyword(Integer pageNo, Integer pageSize, T param) {
        if (DataUtil.isEmpty(pageNo)) {
            pageNo = 1;
        }
        if (DataUtil.isEmpty(pageSize)) {
            pageSize = 10;
        }
        BaseModel model = (BaseModel) param;
        String orderBy = DataUtil.isNotEmpty(model.getOrderBy()) ? camelToUnderline(model.getOrderBy()) : "id";
        Boolean asc = DataUtil.isNotEmpty(model.getAsc()) ? model.getAsc() : false;
        Page page = getPage(pageNo, pageSize, orderBy, asc);
        List<T> records = baseMapper.selectRecords(page, param);
        List<T> rows = baseMapper.selectRecords(param);
        page.setRecords(records);
        page.setTotal(rows.size());
        return page;
    }

    /**
     *分页查询
     */
    @Override
    public <K> Page<K> getPage(Map<String, Object> params) {
        Integer current = 1;
        Integer size = 10;
        String orderBy = "id";
        Boolean asc = false;
        if (DataUtil.isNotEmpty(params.get("pageNo"))) {
            current = Integer.valueOf(params.get("pageNo").toString());
        }
        if (DataUtil.isNotEmpty(params.get("pageSize"))) {
            size = Integer.valueOf(params.get("pageSize").toString());
        }
        if (DataUtil.isNotEmpty(params.get("orderBy"))) {
            orderBy = (String) params.get("orderBy");
            orderBy = camelToUnderline(orderBy);
        }
        if (DataUtil.isNotEmpty(params.get("asc"))) {
            asc = (Boolean) params.get("asc");
        }
        if (size == -1) {
            return new Page<K>();
        }
        Page<K> page = new Page<K>(current, size, orderBy);
        page.setAsc(asc);
        return page;
    }

    @Override
    public <K> Page<K> getPage(Integer pageNo, Integer pageSize, String orderBy, Boolean asc) {
        if (DataUtil.isEmpty(pageNo)) {
            pageNo = 1;
        }
        if (DataUtil.isEmpty(pageSize)) {
            pageSize = 10;
        }
        Page<K> page = null;
        if (DataUtil.isEmpty(orderBy)) {
            orderBy = "id";
        } else {
            orderBy = camelToUnderline(orderBy);
        }
        page = new Page<K>(pageNo, pageSize, orderBy);
        page.setAsc(asc);
        return page;
    }

    @Override
    public T getByOne(T param) {
        T record = (T) baseMapper.selectOne(param);
        return record;
    }

    @Override
    public boolean checkIfExist(T record, boolean excludeId) throws TemplateException {
        Map<String, Object> map = getNotNullColumnAndValueToMap(record);
        if (DataUtil.isEmpty(map)) {
            return false;
        }
        Wrapper<T> wrapper = new EntityWrapper<T>().eq("del_flg", 1);
        for (Map.Entry entry : map.entrySet()) {
            wrapper.eq(entry.getKey().toString(), entry.getValue());
        }
        if (excludeId) {
            wrapper.ne("id", record.getId());
        }
        return baseMapper.selectCount(wrapper) > 0;
    }

    private Map<String, Object> getNotNullColumnAndValueToMap(T record) {
        if (record == null) {
            return Collections.emptyMap();
        }
        Map<String, Object> map = new HashMap<>(6);
        for (Class<?> clazz = record.getClass(); clazz != Object.class && clazz != null; clazz = clazz.getSuperclass()) {
            if (clazz.getName().matches(".*(Ext|Param|DTO|Request|Response)$")) {
                continue;
            }
            Field[] fields = cacheField.getUnchecked(clazz);
            if (fields == null) {
                continue;
            }
            List<Field> fieldList = Arrays.stream(fields)
                    .filter(field -> field.getAnnotation(CheckIfExist.class) != null && !field.getName().matches("^(id|delFlg)$"))
                    .collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(fieldList)) {
                for (Field field : fieldList) {
                    MethodAccess methodAccess = cacheMethod.getUnchecked(clazz);
                    Object value = methodAccess.invoke(record, "get" + StringUtils.captureFirstChar(field.getName()));
                    if (value != null && !"".equals(value.toString())) {
                        map.put(camelToUnderline(field.getName()), value);
                    }
                }
            }
            if (clazz.getAnnotation(UnCheckSuperClass.class) != null) {
                break;
            }
        }
        return map;
    }

    private final LoadingCache<Class<?>, Field[]> cacheField = CacheBuilder.newBuilder().maximumSize(100)
            .expireAfterAccess(365, TimeUnit.DAYS).build(new CacheLoader<Class<?>, Field[]>() {
                @Override
                public Field[] load(Class<?> clazz) throws Exception {
                    return clazz.getDeclaredFields();
                }
            });
    private final LoadingCache<Class<?>, MethodAccess> cacheMethod = CacheBuilder.newBuilder().maximumSize(100)
            .expireAfterAccess(365, TimeUnit.DAYS).build(new CacheLoader<Class<?>, MethodAccess>() {
                @Override
                public MethodAccess load(Class<?> clazz) throws Exception {
                    return MethodAccess.get(clazz);
                }
            });

    /**
     * 获取当前用户Id
     */
    protected Integer getCurrUser() {
        return 1;
    }

    /**
     * 当前用户总表的id
     *
     * @return
     */
//    protected Integer getCurrUserCenterId() {
//        return CustomizeWebUtil.getCurUserCenterId();
//    }
}
