package com.template.dal.base;

import com.baomidou.mybatisplus.annotations.TableField;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.template.dal.util.ClassUtils;
import com.template.dal.util.StringUtils;
import com.template.dal.util.collection.CollectionUtils;
import com.template.dal.util.collection.MapUtils;
import org.apache.ibatis.jdbc.SQL;
import org.apache.log4j.Logger;
import org.springframework.util.Assert;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * 语句模板类
 */
public class SQLTemplate<T extends BaseModel> {

    private static final Logger logger = Logger.getLogger(SQLTemplate.class);

    private static final String PRIMARY_KEY = "id";


    private BiMap<String, String> getCodeToDbColMap(T obj) {
        BiMap<String, String> codeToDbColMap = HashBiMap.create();
        Field[] fields = ClassUtils.getAllFields(obj.getClass());
        // 循环添加属性
        for (Field field : fields) {
            TableField tableField = field.getAnnotation(TableField.class);
            if (tableField == null) {
                codeToDbColMap.put(field.getName(), changeAttrToDatabase(field.getName()));
            } else if (tableField.exist()) {
                codeToDbColMap.put(field.getName(), tableField.value());
            }
        }
        return codeToDbColMap;
    }


    private Function<Map.Entry<String, String>, String> entryFunc = new Function<Map.Entry<String, String>, String>() {
        @Override
        public String apply(Map.Entry<String, String> s) {
            return String.format("`%s` as `%s`", s.getValue(), s.getKey());
        }
    };

    private String gainAllAttributesMapperToDatabase(T obj) {
        BiMap<String, String> colMap = getCodeToDbColMap(obj);

        List<String> colDesc = CollectionUtils.selectList(colMap.entrySet(), entryFunc);

        return StringUtils.join(colDesc, ",");
    }

    private String changeAttrToDatabase(String attr) {
        StringBuffer newAttr = new StringBuffer();
        boolean isFirstChar = true;
        for (char c : attr.toCharArray()) {
            newAttr.append(Character.isUpperCase(c) ?
                    (isFirstChar ? Character.toLowerCase(c) : "_" + Character.toLowerCase(c)) : c);
            isFirstChar = false;
        }
        return newAttr.toString();
    }

    private String tableName(T model) {
        return changeAttrToDatabase(model.getClass().getSimpleName());
    }

    /**
     * in条件查询多条nId语句生成
     *
     * @return
     */
    public String selectIds(Map<String, Object> map) {
        List<Object> ids = MapUtils.getValue(map, "list");
        if (ids == null) {
            ids = MapUtils.getValue(map, "0");
            map.put("list", ids);
        }
        T filterObject = MapUtils.getValue(map, "object");
        if (filterObject == null) {
            filterObject = MapUtils.getValue(map, "1");
        }

        String nColName = MapUtils.getValue(map, "nColName");

        nColName = nColName == null ? PRIMARY_KEY : changeAttrToDatabase(nColName);

        Assert.notNull(filterObject);
        SQL sql = new SQL();
        sql.SELECT(gainAllAttributesMapperToDatabase(filterObject));
        sql.FROM(tableName(filterObject));

        if (!CollectionUtils.isEmpty(ids)) {
            String inStr = getListIndexStr(ids);
            sql.WHERE(nColName + String.format(" in(%s)", inStr));

            BiMap<String, String> colMap = getCodeToDbColMap(filterObject);
            for (String attrName : colMap.keySet()) {
                Object value = ClassUtils.getValue(filterObject, attrName);
                if (value != null) {
                    sql.WHERE(colMap.get(attrName) + "=" + value);
                }
            }
        } else {
            sql.WHERE("0 = 1");
        }


        logger.debug("selectIds :" + sql);
        return sql.toString();
    }

    private String getListIndexStr(List<Object> ids) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < ids.size(); i++) {
            if (i > 0) {
                builder.append(",");
            }
            builder.append(String.format("#{list[%s]}", i));
        }
        return builder.toString();
    }


    final static String listValuePattern = "#{%s.%s}";



    /**
     * 批量插入语句，没有用batch
     */
    public String insertAll(Map<String, Object> params) {
        List<T> list = (List<T>) params.get("list");
        if (CollectionUtils.isEmpty(list)) {
            throw new IllegalArgumentException("list can't be empty");
        }
        T entity = list.get(0);

        BiMap<String, String> attrMap = getCodeToDbColMap(entity);

        List<String> valuesSql = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            final String listIndexStr = String.format("list[%s]", i);
            List<String> attributes = CollectionUtils.selectList(attrMap.keySet(), new Function<String, String>() {
                @Override
                public String apply(String from) {
                    return String.format(listValuePattern, listIndexStr, from);
                }
            });
            valuesSql.add(String.format("(%s)", StringUtils.join(attributes, ",")));
        }

        String sql = String.format("insert into %s(%s) values%s", tableName(entity), StringUtils.join(attrMap.values(), ","), StringUtils.join(valuesSql, ","));
        return sql;
    }
}

